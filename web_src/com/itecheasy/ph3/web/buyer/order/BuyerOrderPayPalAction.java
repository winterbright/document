package com.itecheasy.ph3.web.buyer.order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Enumeration;

import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.common.Utilities;
import com.itecheasy.ph3.common.log.LogData;
import com.itecheasy.ph3.common.log.LogUtils;
import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.customer.CustomerService;
import com.itecheasy.ph3.order.Order;
import com.itecheasy.ph3.order.OrderComponentService;
import com.itecheasy.ph3.order.OrderPaymentInfo;
import com.itecheasy.ph3.order.OrderPrice;
import com.itecheasy.ph3.order.OrderService;
import com.itecheasy.ph3.order.PayPalPayLog;
import com.itecheasy.ph3.order.TempOrder;
import com.itecheasy.ph3.paypal.PayPalExpressCheckOutAPI;
import com.itecheasy.ph3.system.Country;
import com.itecheasy.ph3.web.utils.ConfigHelper;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.sslplugin.annotation.Secured;
import com.paypal.sdk.core.nvp.NVPDecoder;

public class BuyerOrderPayPalAction extends BuyerPlaceOrderBaseAction {
	private static final long serialVersionUID = 9881666L;
	private OrderComponentService orderComponentService;
	
	
	public void setOrderComponentService(OrderComponentService orderComponentService) {
		this.orderComponentService = orderComponentService;
	}

	/**
	 *访问PayPal支付页面
	 */
	@Secured
	public String accessPayPal() 
	{
		SessionOrder sessionOrder = getSessionOrder();
		String jsessionid = request.getSession().getId();

		request.setAttribute("amount", sessionOrder.getPaymentInfo().getOnLinePay());
		request.setAttribute("currencycode", sessionOrder.getPaymentInfo().getCurrency().getCode());
		request.setAttribute("orderno", sessionOrder.getOrderNo());
		request.setAttribute("temporderid", sessionOrder.getTempOrderId());
		request.setAttribute("email", this.getLoginedUserBuyer().getEmail());
		request.setAttribute("jsessionid", jsessionid);
		request.setAttribute("receiveremail", ConfigHelper.PAYPAL_RECEIVER_EMAIL);
		request.setAttribute("paypalurl", ConfigHelper.PAYPAL_WEB_URL);
		request.setAttribute("paypalreturnsite", ConfigHelper.PAYPAL_RETURN_SITE);
		
		payPalService.addInfoOperateLog("Redirect PayPal Site,Order number is :" + sessionOrder.getOrderNo() + ",temp order id is:" + sessionOrder.getTempOrderId());	
		
		return SUCCESS;
	}
	
	/**
	 *PayPal支付成功后，返回到下单成功页面
	 */
	@Secured
	public String PayPalReturn() 
	{
		SessionOrder sessionOrder = getSessionOrder();		
		
		return SUCCESS;
	}
	
	/**
	 *获得PayPal返回信息
	 */
	public void getPayPalReturnInfo() 
	{				
		String exceptionInfo = "";		

		//校验PayPal返回信息的合法性
		if( checkIsValidOfPayPalReturnInfo())
		{		
			Integer tempOrderId = paramInt("temp_order_id");	
			String orderNO = param("item_number");
			
			//增加支付日志
			try 
			{
				addPayPalPayLog(orderNO);
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				exceptionInfo = "Saving the payment information of the PayPal appear abnormal.The abnormal  information is\r\n" + e.getMessage();
				
                payPalService.addErrorOperateLog(exceptionInfo);	
				
				addExceptionLog("PayPal_Exception",orderNO,orderNO,tempOrderId,exceptionInfo);	
				
				errorLog(e);
			}
			
			try 
			{				
				payPalService.addInfoOperateLog("It begins to place order");	
				//下订单
				placeOrder(orderNO,tempOrderId);
				
				//清空Session
				OrderSessionUtils.clearSessionOrder(request);
				OrderSessionUtils.clearSessionOrderPage(request);
				OrderSessionUtils.clearSessionOrderByCreditCard(request);
				OrderSessionUtils.clearCreditCardUrl(request);
				//移除购物车
				SessionUtils.removeMinShoppingCartInfo(request);
				//移除现金券
				SessionUtils.removeCashCouponFromShoppingCart(request);
				
				payPalService.addInfoOperateLog("Success to place order.The order number is " + orderNO);	
			}  
			catch (BussinessException e) 
			{
				String error = e.getErrorMessage();
				exceptionInfo = "PayPal Pay order for success, but order failed ";
				if( OrderService.ERROR_PRODUCT_UNDER_STOCK == error ){
					//库存不足
					exceptionInfo = "The PayPal is paid successfully,but inventory is in shortage when booking them.The order number is " + orderNO;
				}
				else if ( OrderService.ERROR_CASH_PAY_FAIL == error ){
					//Cash支付失败
					exceptionInfo = "The PayPal is paid successfully,but cash payment is failure .The order number is：" + orderNO;
				}
				else 
				{
					//获取临时订单的支付信息
					OrderPaymentInfo orderPaymentInfo = orderService.getPaymentInfoOfTempOrder(tempOrderId);
					
					BigDecimal cashPay = orderPaymentInfo.getCashPay();
					BigDecimal onLinePay = orderPaymentInfo.getOnLinePayUs();
					if( cashPay == null) cashPay = BigDecimal.ZERO;
					if( onLinePay == null) onLinePay = BigDecimal.ZERO;
					
					exceptionInfo = "The PayPal is paid successfully,but failure to release order.The possible cause is that shall pay for the account is not accordance with the prepaid amount of payment.The order number is " + orderNO + ",cash payment amount is" + cashPay.toString() + ",payPal pay amount is" + onLinePay.toString();		
				}				
				
				payPalService.addErrorOperateLog(exceptionInfo);	
				
				addExceptionLog("PayPal_Exception",orderNO,orderNO,tempOrderId,exceptionInfo);	
			}
			catch (Exception e) 
			{
				exceptionInfo = "The PayPal is paid successfully,but it appear to abnormal phenomena when releasing order.The order number is " + orderNO + ",The anomaly information is " + e.getMessage();
				payPalService.addErrorOperateLog(exceptionInfo);	

				addExceptionLog("PayPal_Exception",orderNO,orderNO,tempOrderId, exceptionInfo);
				
				errorLog(e);
			}	
		}
	}
	
	
	private String getPayPalApplyUrl()
	{
		Enumeration en = request.getParameterNames();			
		
		String url = "";
		while(en.hasMoreElements())
		{
			String paramName = (String)en.nextElement();
			if( paramName.equalsIgnoreCase("temp_order_id"))
			{
				continue;
			}
			String paramValue = request.getParameter(paramName);
			url = url + "&" + paramName + "=" + URLEncoder.encode(paramValue);
		 }
				
		return url;
	}
	
	private String getPayPalCheckResult(String checkUrl) throws MalformedURLException,IOException
	{
		URL u = new URL(ConfigHelper.PAYPAL_WEB_URL);			                
		URLConnection uc = u.openConnection();
		uc.setDoOutput(true);
		uc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");	
		
		PrintWriter pw = new PrintWriter(uc.getOutputStream());
		pw.println(checkUrl);
		pw.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		String res = in.readLine();
		in.close();
		
		return res;
//		return "VERIFIED";
	}
	
	/**
	 *校验PayPal返回的信息是否合法
	 */
	private boolean checkIsValidOfPayPalReturnInfo()
	{
		String exceptionInfo = "";
		String orderNoOfPayPalReturn = param("item_number");
		Integer tempOrderId = paramInt("temp_order_id");
		String payPalReturnBaseInfo = "The returned order number of the PayPal is " + orderNoOfPayPalReturn + ". The returned temporary order number ID of the PayPal is " + tempOrderId;
		String payPalReturnInfo = payPalReturnBaseInfo;
		
		try 
		{
			String checkUrl = "cmd=_notify-validate" + getPayPalApplyUrl();			
			String checkResult = getPayPalCheckResult(checkUrl);
			
			payPalReturnInfo = payPalReturnBaseInfo + ",PayPal return info :\r\n " + checkUrl;				
			
			payPalService.addInfoOperateLog("It begins to check payment information of the PayPal." + payPalReturnBaseInfo + ",valid text == " + checkResult);
			//1、判断是否为PayPal发起的合法请求
		/*	if(checkResult.equals("INVALID")) 
			{//PayPal非法请求
				payPalService.addErrorOperateLog("Detect the illegal request of the PayPal." + payPalReturnInfo);
				return false;
			}			
			else */
			if(checkResult.equals("VERIFIED") || checkResult.equals("INVALID")) //此处暂时允许非法请求下单，因为客户地址出现特殊字符时，这里总判断不正确
			{//PayPal合法请求
				BigDecimal payAmountOfPayPalReturn = paramBigDecimal("mc_gross"); // 支付金额	
				String paymentStatus = param("payment_status"); // 支付状态
				String paymentCurrency = param("mc_currency");// 货币方式
				String receiverEmail = param("receiver_email");// 收款账号	
				
				//2、校验PayPal返回的收款账号是否正确
				if( !receiverEmail.equals(ConfigHelper.PAYPAL_VERIFY_EMAIL))
				{
					payPalService.addErrorOperateLog("We find that the account number is mistaken when it return to the information of checking out the PayPal. The account number is " + receiverEmail + "." + payPalReturnInfo);	
					return false;
				}
				
				//3、校验PayPal返回的支付状态是否为已完成
				if(!"Completed".equalsIgnoreCase(paymentStatus))
				{
					payPalService.addErrorOperateLog("We find that the pay is not complete when it return to the information of checking out the PayPal.The state of payment is " + paymentStatus + "." + payPalReturnInfo);	
					return false;
				}
	
			    //3、校验临时订单是否存在支付信息(从数据库中读取临时订单及支付信息)
				TempOrder tempOrder = orderService.getTempOrder(tempOrderId);
				OrderPaymentInfo orderPaymentInfo = orderService.getPaymentInfoOfTempOrder(tempOrderId);
				if( tempOrder == null || orderPaymentInfo == null || orderPaymentInfo.getOnlinePayInfo() == null)
				{
					exceptionInfo = "The payment information of the order can not be founded when it return to the information of checking out the PayPal. " + payPalReturnInfo;
                    payPalService.addErrorOperateLog(exceptionInfo);	
                    
                    addExceptionLog("PayPal_Exception",orderNoOfPayPalReturn,"",null,exceptionInfo);	
					return false;
				}		
								
			   //4、校验PayPal返回的订单号是否与临时订单对应(对比数据库中记录的订单号是否与PayPal返回的相同)		
			   Order order = orderService.getOrderByTempOrderId(tempOrderId);
			   String orderNO = order != null ? order.getOrderNo() : null;
			   if( orderNoOfPayPalReturn == null || !orderNoOfPayPalReturn.equalsIgnoreCase(order.getOrderNo()))
			   {
				    exceptionInfo = "We find that the returned order number of the PayPal is not the same as the recorded order number of the database. The order number of the database is " + orderNO + "." + payPalReturnInfo;
				    payPalService.addErrorOperateLog(exceptionInfo);	
				    
				    addExceptionLog("PayPal_Exception",orderNoOfPayPalReturn,orderNO,tempOrderId,exceptionInfo);		
				    return false;
			   }				

			   //5、校验订单的支付方式是否为PayPal
			   if( orderPaymentInfo.getOnLinePayType() == null || OrderService.PAY_TYPE_PAY_PAL != orderPaymentInfo.getOnLinePayType())
			   {
				   exceptionInfo = "We find that the payment terms of temporary order is not the PayPal when it return to the information of checking out the PayPal." + payPalReturnInfo;
				   payPalService.addErrorOperateLog(exceptionInfo);	
				   
				   addExceptionLog("PayPal_Exception",orderNoOfPayPalReturn,orderNO,tempOrderId,exceptionInfo);		
				   return false;
			   }
			   
			   //6、检查PayPal返回的支付金额和币种是否与数据库中相同		  
			   if( payAmountOfPayPalReturn == null || orderPaymentInfo.getOnlinePayInfo().getCurrency() == null || payAmountOfPayPalReturn.compareTo(orderPaymentInfo.getOnLinePay()) != 0 || !paymentCurrency.equals(orderPaymentInfo.getOnlinePayInfo().getCurrency().getCode()))
			   {		
				   exceptionInfo = "We find that the returned paid amount or the pay currency of the PayPal are not accordance with the temporary order when it return to the information of checking out the PayPal." + payPalReturnInfo;
				   payPalService.addErrorOperateLog(exceptionInfo);	
				   
				   addExceptionLog("PayPal_Exception",orderNoOfPayPalReturn,orderNO,tempOrderId,exceptionInfo);	
				   return false;
			   }
			   
			   payPalService.addInfoOperateLog("Checking the payment information of the PayPal is correct." + payPalReturnBaseInfo);	
			   return true;
			}
			else 
			{   // error
				payPalService.addErrorOperateLog("Checking the payment information of the PayPal is failure." + payPalReturnInfo);	
			}
        } 
	    catch (Exception e) 
		{
			// TODO: handle exception
	    	exceptionInfo = "Checking the payment information of the PayPal appear abnormal." + payPalReturnInfo + "\r\n The abnormal information is\r\n " + e.getMessage();
	    	
	    	payPalService.addErrorOperateLog(exceptionInfo);	
	    	addExceptionLog("PayPal_Exception",orderNoOfPayPalReturn,orderNoOfPayPalReturn,tempOrderId,exceptionInfo);

	    	errorLog(e);
		}	
			
	   return false;		
	}

	private void addExceptionLog(String commandName,String orderNoOfPayPalReturn,String orderNo,Integer tempOrderId,String remark)
	{
	   LogData logdata = new LogData();
	   logdata.setCommand(commandName);
	   logdata.addOperateData("OrderNoOfPayPalReturn", orderNoOfPayPalReturn);
	   logdata.addOperateData("OrderNo", orderNo);
	   logdata.addOperateData("TempOrderId", tempOrderId);
	   logdata.setRemark(remark);
		
	   LogUtils.addLog(logdata);				   
	}
	
	/**
	 *增加PayPal支付日志
	 */
	private void addPayPalPayLog(String orderNO)
	{
		payPalService.addInfoOperateLog("It begins to save payment information of the PayPal.");	
		
		PayPalPayLog payPalPayLog = new PayPalPayLog();
		payPalPayLog.setOrderNo(orderNO);
		
		payPalPayLog.setMcGross(param("mc_gross"));	
		payPalPayLog.setMcFee(param("mc_fee"));
		payPalPayLog.setMcCurrency(param("mc_currency"));
		payPalPayLog.setPayerId(param("payer_id"));
		payPalPayLog.setTax(paramBigDecimal("tax"));		
		
		Date paymentDate = paramDate("payment_date");
		payPalPayLog.setPaymentDate(paymentDate == null ? new Date() : paymentDate);
		payPalPayLog.setPaymentFee(param("payment_fee"));
		payPalPayLog.setPaymentGross(param("payment_gross"));
		payPalPayLog.setPaymentType(param("payment_type"));
		payPalPayLog.setPaymentStatus(param("payment_status"));
		payPalPayLog.setPayerStatus(param("payer_status"));
		payPalPayLog.setPayerEmail(param("payer_email"));
		
		payPalPayLog.setAddressName(param("address_name"));
		payPalPayLog.setAddressState(param("address_status"));
		payPalPayLog.setAddressStreet(param("address_street"));
		payPalPayLog.setAddressCountry(param("address_country"));
		payPalPayLog.setAddressCity(param("address_city"));				
		payPalPayLog.setAddressZip(param("address_zip"));
		payPalPayLog.setAddressCountryCode(param("address_country_code"));	
		
		payPalPayLog.setFirstName(param("first_name"));
		payPalPayLog.setLastName(param("last_name"));				
		payPalPayLog.setTxnId(param("txn_id"));				
		payPalPayLog.setNotifyVersion(param("notify_version"));
		payPalPayLog.setCustom(param("custom"));				
		payPalPayLog.setBusiness(param("business"));				
		payPalPayLog.setQuantity(param("quantity"));
		payPalPayLog.setVerifySign(param("verify_sign"));
		payPalPayLog.setShipping(param("shipping"));
		payPalPayLog.setCharset(param("charset"));				
		
		payPalPayLog.setReceiverId(param("receiver_id"));
		payPalPayLog.setReceiverEmail(param("receiver_email"));
		payPalPayLog.setResidenceCountry(param("residence_country"));
		
		payPalPayLog.setTxnType(param("txn_type"));
		payPalPayLog.setItemName(param("item_name"));
		payPalPayLog.setItemNumber(param("item_number"));	
		
		payPalService.addPayPalPayLog(payPalPayLog);
		
		payPalService.addInfoOperateLog("Complete to save save payment information of the PayPal.");	
	}
	
	public String getExpressPayPalReturnInfo()
	{
		String token = param("token");
		String payerID = param("PayerID");
		String exceptionInfo = "";	
		NVPDecoder paymentInfo = null;
		try 
		{				
			payPalService.addInfoOperateLog("ExpressCheckOut:Received PayPal information.Token:" + token + ",PayerID:" + payerID);	
			
			//1、从PayPal获取支付信息
			payPalService.addInfoOperateLog("ExpressCheckOut:Start from PayPal for payment information. Token:" + token);
		    PayPalExpressCheckOutAPI payPalExpressCheckOutAPI = new PayPalExpressCheckOutAPI();			
			paymentInfo = payPalExpressCheckOutAPI.GetExpressCheckoutDetails(token);			
			orderNO = paymentInfo.get("INVNUM");
			
			//2、保存PayPal支付信息
			payPalService.addInfoOperateLog("ExpressCheckOut:Began to save PayPal payment information.");			
			orderComponentService.savePayPalInfoByExpressCheckOut(paymentInfo);
			
			//下订单
			orderNO = orderService.placeOrderByExpressCheckOut(token,payerID,paymentInfo);
			payPalService.addInfoOperateLog("ExpressCheckOut:Success to place order.The order number is " + orderNO);	
			
			if( orderNO != null)
			{
				Order order = orderService.getOrderByOrderNo(orderNO);
				if( order != null && order.getCustomerId() != null)
				{
					Customer customer = customerService.getCustomer(order.getCustomerId());
					if( customer != null)
					{
						email = customer.getEmail();
					}
				}
			}

			//移除购物车
			SessionUtils.removeMinShoppingCartInfo(request);
			//移除现金券
			SessionUtils.removeCashCouponFromShoppingCart(request);
			
			return SUCCESS;
		}  
		catch (BussinessException e) 
		{
			String error = e.getErrorMessage();
			exceptionInfo = "ExpressCheckOut:PayPal Pay order for success, but order failed .Token is " + token;
			if ( OrderService.ERROR_ORDER_NOT_HAVE_FREIGHT == error )
			{	//PayPal返回的地址没有运费
				exceptionInfo = "ExpressCheckOut:not have freight .Token is " + token;
				errorInfo = "001";
				
				//没有运费是的处理
				notHaveFreightDeal(paymentInfo);
			}
			else if ( OrderService.ERROR_ORDER_POBOX_NOT_HAVE_EXPEDITED_SHIPPING == error )
			{	//PayPal返回的地址是P.O BOX地址，且选择的快递方式
				exceptionInfo = "ExpressCheckOut:p.o box not have expedited shipping .Token is " + token;
				errorInfo = "002";
			}
			else if( OrderService.ERROR_PRODUCT_UNDER_STOCK == error )
			{   //库存不足
				exceptionInfo = "ExpressCheckOut:The PayPal is paid successfully,but inventory is in shortage when booking them.Token is " + token;
			}			 
			else if ( OrderService.ERROR_CASH_PAY_FAIL == error )
			{
				//Cash支付失败
				exceptionInfo = "ExpressCheckOut:The PayPal is paid successfully,but cash payment is failure .Token is " + token;
			}
			else if ( OrderService.ERROR_EXPRESS_PAYPAL_INFO_EXCEPTION == error )
			{	//请求非法
				exceptionInfo = "ExpressCheckOut:The PayPal is paid successfully,but Request information illegal or cannot find the order.Token is " + token;
			}						
			else if ( OrderService.ERROR_EXPRESS_PAYPAL_FAIL == error )
			{
				exceptionInfo = "ExpressCheckOut:Paypal pay for failure .Token is " + token;
			}
			
			payPalService.addErrorOperateLog(exceptionInfo);	
			
			if(Utilities.isEmpty(errorInfo))
			{
				errorInfo = "003";
				addOrderErrorLog(paymentInfo);
			}
			return ERROR;
		}
		catch (Exception e) 
		{
			exceptionInfo = "ExpressCheckOut:Place Order exception.Token is " + token + ". Exception info:" + e.getMessage();
			payPalService.addErrorOperateLog(exceptionInfo);	
	
			errorLog(e);
			addOrderErrorLog(paymentInfo);
			
			errorInfo = "003";
			return ERROR;
		}	
		finally
		{
			if( paymentInfo != null )
			{
				setAreaInfo(paymentInfo);
			}
		}
	}
	
	private void setAreaInfo(NVPDecoder paymentInfo)
	{
		Country country = null;
		
		if( paymentInfo.get("SHIPTOCOUNTRYCODE") != null)
		{
			country = dictionaryService.getCountryOfISOCode(paymentInfo.get("SHIPTOCOUNTRYCODE"));	
		}
		
		if( country == null) return;
		
		String city = paymentInfo.get("SHIPTOCITY") == null ? "" : paymentInfo.get("SHIPTOCITY");
		String zip = paymentInfo.get("SHIPTOZIP") == null ? "" : paymentInfo.get("SHIPTOZIP");
		
		//改变区域信息设置
		setAreaInfo(country,city,zip);
	}
	
	private void notHaveFreightDeal(NVPDecoder paymentInfo)
	{
		//获得国家信息
		Country country = null;
		if( paymentInfo != null && paymentInfo.get("SHIPTOCOUNTRYCODE") != null)
		{
			country = dictionaryService.getCountryOfISOCode(paymentInfo.get("SHIPTOCOUNTRYCODE"));	
		}
		String countryName = country == null ? "" : country.getName();
		
		//获得订单的订单重量
		Order order = orderService.getOrderByOrderNo(orderNO);
		BigDecimal orderWeight = BigDecimal.ZERO;
		if( order != null && order.getOrderPrice() != null)
		{
			orderWeight = order.getOrderPrice().getOrderWeight();
		}
		
		//获得PayPal邮箱及账号信息
		String paypalEmail = paymentInfo.get("EMAIL");
		String customerEmail = paypalEmail;
		String customerType = "PayPal";
		Customer customer = customerService.getCustomerAccountByPayPal(paypalEmail);
		if( customer != null && CustomerService.CUSTOMER_TYPE_PH3 == customer.getType().intValue())
		{
			customerType = "PH3";
			customerEmail = customer.getEmail();
		}
	
		//没有运费，则记录日志并发邮件给客服
		warnNotHaveFreight(customerEmail , countryName,orderWeight, "PayPal Express Checkout", customerType);
	}
	
	private void addOrderErrorLog(NVPDecoder paymentInfo)
	{
		//获得国家信息
		Country country = null;
		if( paymentInfo != null && paymentInfo.get("SHIPTOCOUNTRYCODE") != null)
		{
			country = dictionaryService.getCountryOfISOCode(paymentInfo.get("SHIPTOCOUNTRYCODE"));	
		}
		String countryName = country == null ? "" : country.getName();
		
		//获得订单的订单重量
		Order order = orderService.getOrderByOrderNo(orderNO);
		BigDecimal orderWeight = BigDecimal.ZERO;
		if( order != null && order.getOrderPrice() != null)
		{
			orderWeight = order.getOrderPrice().getOrderWeight();
		}
		
		//获得PayPal邮箱及账号信息
		String paypalEmail = paymentInfo.get("EMAIL");
		String customerEmail = paypalEmail;
		String customerType = "PayPal";
		Customer customer = customerService.getCustomerAccountByPayPal(paypalEmail);
		if( customer != null && CustomerService.CUSTOMER_TYPE_PH3 == customer.getType().intValue())
		{
			customerType = "PH3";
			customerEmail = customer.getEmail();
		}
	
		//没有运费，则记录日志并发邮件给客服
		addOrderErrorLog("Express Checkout下单失败",customerEmail , countryName,orderWeight, "PayPal Express Checkout", customerType);
	}
	
	private String orderNO;
	public String getOrderNo()
	{
		return orderNO;
	}
	
	private String email;
	public String getEmail()
	{
		return email;
	}
	
	private String errorInfo;
	public String getErrorInfo()
	{
		return errorInfo;
	}
	
}

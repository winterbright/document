package com.itecheasy.ph3.web.buyer.order;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.itecheasy.ph3.order.CreditCardPayInfo;
import com.itecheasy.ph3.order.OnlinePayInfo;
import com.itecheasy.ph3.order.OrderPaymentInfo;
import com.itecheasy.ph3.order.OrderService;
import com.itecheasy.ph3.web.buyer.BuyerPageController.PlaceOrderPage;
import com.itecheasy.ph3.web.utils.StrUtils;
import com.itecheasy.sslplugin.annotation.Secured;


@Secured
public class BuyerOrderGlobalCollectAction extends BuyerPlaceOrderBaseAction {
	private static final long serialVersionUID = 9881666L;
	private static Logger ORDER_PAY = Logger.getLogger("ORDERPAY");
	
	/**
	 * 信用卡信息录入页面
	 * @return
	 */
	public String doCreditCard()
	{
		SessionOrder sessionOrder = getSessionOrder();
		String checkResult = checkSessionOrder(PlaceOrderPage.PAGE_CREDIT_CARD);
		if( checkResult != null)
		{
			return checkResult;
		}
		
		//获取信用卡支付网址
		String payUrl = getPaymentUrl(sessionOrder);
		//显示该页面是否是因为支付失败产生的
		boolean isPayError = paramBool("payError");
		
		beginPage(PlaceOrderPage.PAGE_CREDIT_CARD);

		request.setAttribute("sessionOrder", sessionOrder);
		request.setAttribute("payUrl", payUrl);
		request.setAttribute("currentPage", PlaceOrderPage.PAGE_CREDIT_CARD);
		request.setAttribute("isPayError", isPayError);		

		return SUCCESS;
	}
	
	
	
	/**
	 * 获取信用卡支付网址
	 * @param sessionOrder 当前Session订单信息
	 * @return 信用卡支付网址
	 */	
	private String getPaymentUrl(SessionOrder sessionOrder)
	{
		if( sessionOrder == null ) return "";
		
		OrderPaymentInfo paymentInfo = sessionOrder.getPaymentInfo();
		if( paymentInfo == null ) return "";
		
		CreditCardPayInfo creditCardPayInfo = paymentInfo.getCreditCardPayInfo();		
		if( creditCardPayInfo == null) return "";	
		
	    /*String url = OrderSessionUtils.getCreditCardUrl(request.getSession());
		
		//判断是否已经存在支付地址
		if(url != null && url.length() > 0  )		
		{   //如果已经存在支付地址，则判断是否更改了支付信息，没有则仍返回原来的地址			
			if( !checkIsModifyCreditCardInfo(sessionOrder)) return url;
		}	*/
		
		//获取地址前，需生成新的订单号,因为第三方的接口每次请求的订单号必须不同
		if( !createOrderNo() ) 	return "";
		
		BigDecimal creditCardPayAmount = paymentInfo.getOnLinePay();
		Integer currentCurrencyId = paymentInfo.getCurrency().getId();
		
		creditCardPayInfo= orderPayService.getPaymentUrl(creditCardPayInfo, creditCardPayAmount,currentCurrencyId, getLoginedUserBuyer().getEmail(),sessionOrder.getBillingAddress(),sessionOrder.getShippingAddress());
		String url = creditCardPayInfo.getPayUrl();
		/*sessionOrder.getPaymentInfo().setCreditCardPayInfo(creditCardPayInfo);
		
		OrderSessionUtils.setCreditCardUrl(request, url);		
		//保存信用卡支付时的订单信息，以便下次比较
		OrderSessionUtils.setSessionOrderByCreditCard(request, copyOrder(sessionOrder));*/
		String strCustomerId = getLoginedUserBuyer() != null && getLoginedUserBuyer().getId() != null ? getLoginedUserBuyer().getId().toString() : "";
		ORDER_PAY.info("request pay url,url is " + url  + ",customerid is " + strCustomerId);
		
		return url;
	}	
	
	/**
	 * 获取Global Collect支付站点返回的信息
	 * @return
	 */
	public String getGlobalCollectReturnInfo()
	{		
		String orderNo = "";
		String ref = param("REF");
		String mac = param("RETURNMAC");
		
		if (ref != null && ref.length() >= 20)
		{
			Date now = new Date();
			DateFormat df = new SimpleDateFormat("yy");
			String strYear = df.format(now);
			
			orderNo = "S" + strYear + ref.substring(10, 20);
		}

		request.setAttribute("orderNo",orderNo );
		request.setAttribute("ref",ref );
		request.setAttribute("mac",mac );
		return SUCCESS;
	}

	/**
	 * 从Global Collect支付站点校验用户信用卡信息
	 * @throws IOException
	 */
	public void checkCreditCardPayInfo() throws IOException
	{	
        String fm = "[{\"result\":%1$s}]";
		
		int result = 0;
		
		try {						
			String orderNo = param("OrderNo");
			String ref = param("REF");
			String macAddressInfo = param("MAC");
			String refInfo = "request info:ref=" + ref + "&mac=" + macAddressInfo;
			
			String strCustomerId = getLoginedUserBuyer() != null && getLoginedUserBuyer().getId() != null ? getLoginedUserBuyer().getId().toString() : "";
			ORDER_PAY.info("begin check pay info,customerid is " + strCustomerId + ",order no is " + orderNo);
			
			/*if( orderNo != null && !orderNo.isEmpty())
			{*/
				SessionOrder sessionOrder = getSessionOrder();
				if( sessionOrder != null && sessionOrder.getPaymentInfo() != null)
				{			
					String strTempOrderId = sessionOrder.getTempOrderId() == null ? "" : sessionOrder.getTempOrderId().toString();
					OrderPaymentInfo paymentInfo = sessionOrder.getPaymentInfo();
					CreditCardPayInfo creditCardPayInfo = paymentInfo.getCreditCardPayInfo();
						
					refInfo += "&orderno=" + sessionOrder.getOrderNo() + "&customerId=" + strCustomerId + "&tempOrderId=" + strTempOrderId;
					
					//检查当前订单是否为信用卡支付类型
					if( OrderService.PAY_TYPE_CREDIT_CARD == paymentInfo.getOnLinePayType() && creditCardPayInfo != null )
					{
						//检查订单号是否相同
						if( StrUtils.equalsIgnoreCase(orderNo, sessionOrder.getOrderNo()) )
						{
							//检查支付是否完成
							if( orderPayService.checkPaymentInfo(creditCardPayInfo))
							{
								String effortId =  creditCardPayInfo.getEffortId();
								if( !effortId.isEmpty())
								{
									sessionOrder.getPaymentInfo().setPaymentAttachInfo(creditCardPayInfo);
									sessionOrder.setConfirmPayInfo(true);
									setSessionOrder(sessionOrder);
									result = 1;
									endPage(PlaceOrderPage.PAGE_CREDIT_CARD);
									
									ORDER_PAY.info("Check payment information success,customerid is " + strCustomerId + ",order no is " + orderNo);
								}
								else 
								{
									ORDER_PAY.error("Failed to pay.effort id is null." + refInfo);
								}
							}			
							else 
							{
								ORDER_PAY.error("Failed to pay.Pay state is " + creditCardPayInfo.getStatusId() + "." + refInfo);
							}
						}
						else 
						{
							ORDER_PAY.error("order no is different.GC return order no is " + orderNo + ",session order no is " +sessionOrder.getOrderNo());
						}
					}
				}
				else 
				{
					ORDER_PAY.error("session or payment info is null." + refInfo);
				}
//			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			errorLog(e);
		}		
		
		returnJson(String.format(fm, result));
	}
	
	/**
	 * 判断信用卡支付时的相关信息是否已更改
	 * @param sessionOrder 当前订单信息
	 * @return
	 */
	private boolean checkIsModifyCreditCardInfo(SessionOrder sessionOrder)
	{
		//信息不全则直接返回
		if( sessionOrder == null || sessionOrder.getPaymentInfo() == null ) return true;
		OnlinePayInfo onlinePayInfo = sessionOrder.getPaymentInfo().getOnlinePayInfo();		
		if( onlinePayInfo == null || onlinePayInfo.getPaymentAttachInfo() == null || !(onlinePayInfo.getPaymentAttachInfo() instanceof CreditCardPayInfo)) return true;
		CreditCardPayInfo creditCardPayInfo = (CreditCardPayInfo)onlinePayInfo.getPaymentAttachInfo() ;
		if( creditCardPayInfo.getCreditCardType() == null) return true;
		
		//取得信用卡备份的订单信息
		SessionOrder bakOrder = OrderSessionUtils.getSessionOrderByCreditCard(request.getSession());
		
		if( bakOrder == null ||  bakOrder.getPaymentInfo() == null ) return true;		
		OnlinePayInfo bakOnlinePayInfo = bakOrder.getPaymentInfo().getOnlinePayInfo();
		if( bakOnlinePayInfo == null || bakOnlinePayInfo.getPaymentAttachInfo() == null || !(onlinePayInfo.getPaymentAttachInfo() instanceof CreditCardPayInfo)) return true;
		CreditCardPayInfo bakCreditCardPayInfo = (CreditCardPayInfo)bakOnlinePayInfo.getPaymentAttachInfo() ;
		if( bakCreditCardPayInfo.getCreditCardType() == null) return true;
		
		//先判断货运地址是否更改
		if( isModifyAddress(bakOrder.getShippingAddress(), sessionOrder.getShippingAddress())) return true;
		
		//先判断账单地址是否更改
		if( isModifyAddress(bakOrder.getBillingAddress(), sessionOrder.getBillingAddress())) return true;
		
		//判断支付金额是否更改		
		if( bakOnlinePayInfo.getAmountUs().compareTo(onlinePayInfo.getAmountUs()) != 0) return true;
		
		//判断币种是否更改
		if( bakOnlinePayInfo.getCurrency().getId().compareTo(onlinePayInfo.getCurrency().getId()) != 0) return true;
		
		//判断支付方式是否更改
		if( bakOnlinePayInfo.getOnLinePayType().compareTo(onlinePayInfo.getOnLinePayType()) != 0) return true;
		
		//判断信用卡类型是否更改
		if( bakCreditCardPayInfo.getCreditCardType().getId().compareTo(creditCardPayInfo.getCreditCardType().getId()) != 0) return true;
			
		return false;
	}
}

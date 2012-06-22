package com.itecheasy.ph3.web.buyer.order;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.itecheasy.common.PageList;
import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.SearchOrder;
import com.itecheasy.ph3.customer.Address;
import com.itecheasy.ph3.order.CashCoupon;
import com.itecheasy.ph3.order.CashCouponService;
import com.itecheasy.ph3.order.CreditCardType;
import com.itecheasy.ph3.order.CustomerCashCoupon;
import com.itecheasy.ph3.order.OrderPaymentInfo;
import com.itecheasy.ph3.order.OrderService;
import com.itecheasy.ph3.order.CashCouponService.CustomerCashCouponCriteria;
import com.itecheasy.ph3.order.CashCouponService.CustomerCashCouponSearchOrder;
import com.itecheasy.ph3.order.CashCouponService.CustomerCouponStatus;
import com.itecheasy.ph3.system.CookieConfigService;
import com.itecheasy.ph3.system.Country;
import com.itecheasy.ph3.system.Currency;
import com.itecheasy.ph3.web.buyer.BuyerPageController.PlaceOrderPage;
import com.itecheasy.ph3.web.utils.CurrencyUtils;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.vo.CookieArea;
import com.itecheasy.ph3.web.vo.PaymentCurrencyVO;
import com.itecheasy.sslplugin.annotation.Secured;

@Secured
public class BuyerOrderPaymentMethodAction extends BuyerPlaceOrderBaseAction {
	private static final long serialVersionUID = 9881666L;
	private CashCouponService cashCouponService;
	
	public void setCashCouponService(CashCouponService cashCouponService) 
	{
		this.cashCouponService = cashCouponService;
	}
	
	/**
	 *获取支付方式
	 */
	public String doPaymentMethod() 
	{		
		String commandName = param("commandName");
		if ( commandName == null || commandName.equals(""))
		{//初始化，第一次进入页面
			return initPaymentMethod();
		}
		else
		{//在本页面刷新
			return postPaymentMethod();
		}
	}	
	
	private String initPaymentMethod()
	{
		//获取是哪个页面调用此方法(doPaymentMethod())，用来区分是Payment页面调用还是Payment页面之前调用
		PlaceOrderPage beginPage = getBeginPage();
	
		SessionOrder sessionOrder = getSessionOrder();
		String checkResult = checkSessionOrder(PlaceOrderPage.PAGE_PAYMENT_METHOD);
		if( checkResult != null)
		{
			return checkResult;
		}
		
		Integer customerId = getLoginedUserBuyer().getId();
		
		//获取当前会有所有可用的现金券
		List<CustomerCashCoupon> customerCashCouponList=this.getCustomerCashCouponListByCustomerId(customerId);
		//获得当前正在使用的订单
		CashCoupon cashCouopon=this.getCurrentCashCouponCode(customerCashCouponList, sessionOrder.getProductPriceAfterDiscount(), customerId, beginPage);
				
		//获取订单应支付金额
		BigDecimal dueAmount = sessionOrder.getDueAmount();
		//获取用户Cash账户余额
		BigDecimal balance = cashAccountService.getBalance(customerId);			
		//获取Cash账户能支付的金额
		BigDecimal cashCanPayAmount = getCashCanAmount(dueAmount,balance);
		//信用卡与Cash同时支付时，信用卡需支付的金额
		BigDecimal partOnlinePayAmount =dueAmount.subtract(cashCanPayAmount);
		
		//获取所有的币种
		List<Currency> currecyList = systemService.getEnabledCurrencies();
		
		List<PaymentCurrencyVO> paymentCurrencyList = toPaymentCurrencyVO(currecyList,partOnlinePayAmount,dueAmount);
		
		//获取所有国家
		List<Country> countries = dictionaryService.getAllCountries();
		List<Country> commonCountries = dictionaryService.getAllCommonCountries();
		
	    //获取所有的信用卡类型
		List<CreditCardType> creditCardTypeList = orderPayService.getCreditCardTypes();
		
		//获取当前选择的币种
		PaymentCurrencyVO currentCurrency = getCurrentCurrency(sessionOrder,partOnlinePayAmount,dueAmount);
		//currentCurrency.setCurrency(SessionUtils.getAreaInfo(getSession()).getCurrency());
		
		
		//获取当前选择的支付方式
		Integer currentCreditCardTypeId = null;
		Integer onLinePayTpye = null;		
	
		OrderPaymentInfo paymentInfo = sessionOrder.getPaymentInfo();		
		if( paymentInfo != null){
			
			onLinePayTpye = paymentInfo.getOnLinePayType();
			if( paymentInfo.getCreditCardPayInfo() != null && paymentInfo.getCreditCardPayInfo().getCreditCardType() != null)
			{
				currentCreditCardTypeId = paymentInfo.getCreditCardPayInfo().getCreditCardType().getId();
			}
		}		
		
		if( onLinePayTpye == null || 0 == onLinePayTpye || OrderService.PAY_TYPE_PAY_PAL_EXPRESS == onLinePayTpye)
		{//如果客户第一次下单，则默认为信用卡的Visa支付类型
			onLinePayTpye = OrderService.PAY_TYPE_CREDIT_CARD;
			
			currentCreditCardTypeId = 1;
		}
		
		if( currentCreditCardTypeId == null)
		{
			currentCreditCardTypeId = 1;
		}
				
		//获取是否需要Cash支付
		boolean isCashPay = isCashPay(sessionOrder,balance);
		//显示该页面是否是因为支付失败产生的
		boolean isPayError = paramBool("payError");
		
		//下单步骤的Payment页面，用户第一次下单，尚未创建账单地址，Region项默认值
		Address billingAddress = sessionOrder.getBillingAddress();
		if(billingAddress == null){
			billingAddress = new Address();
			Country country = new Country();
			CookieArea cookieArea =SessionUtils.getAreaInfo(request.getSession());
			if(cookieArea != null && cookieArea.getCountry() != null){
				country.setId(cookieArea.getCountry().getId());
				billingAddress.setCountry(country);
				billingAddress.setCity(cookieArea.getCity());
				billingAddress.setZip(cookieArea.getZip());
			}
		}
		
		
				
		beginPage(PlaceOrderPage.PAGE_PAYMENT_METHOD);

		request.setAttribute("countries", countries);
		request.setAttribute("commonCountries", commonCountries);
		request.setAttribute("sessionOrder", sessionOrder);
		request.setAttribute("balance", balance);
		request.setAttribute("currecyList", paymentCurrencyList);
		request.setAttribute("creditCardTypeList",creditCardTypeList);		
		request.setAttribute("dueAmount",dueAmount);
		request.setAttribute("isCashPay", isCashPay);
		request.setAttribute("partOnlinePayAmount",partOnlinePayAmount);
		request.setAttribute("currentCurrency", currentCurrency);
		request.setAttribute("currentCreditCardTypeId", currentCreditCardTypeId);
		request.setAttribute("onLinePayTpye", onLinePayTpye);
		request.setAttribute("currentPage", PlaceOrderPage.PAGE_PAYMENT_METHOD);
		request.setAttribute("isPayError", isPayError);		
		request.setAttribute("customerCashCouponList", customerCashCouponList);
		request.setAttribute("defaultCustomerCashCouponCode", cashCouopon==null?null:cashCouopon.getCode());
		request.setAttribute("billingAddress", billingAddress);
		
		return SUCCESS;
	}
	
	private String postPaymentMethod()
	{
		//获取是哪个页面调用此方法(doPaymentMethod())，用来区分是Payment页面调用还是Payment页面之前调用
		PlaceOrderPage beginPage = getBeginPage();
		
		SessionOrder sessionOrder = getSessionOrder();
		
		//zw		
		String checkResult = checkSessionOrder(PlaceOrderPage.PAGE_PAYMENT_METHOD);
		if( checkResult != null)
		{
			return checkResult;
		}
		
		Integer customerId = getLoginedUserBuyer().getId();
		
		//获取当前会有所有可用的现金券
		List<CustomerCashCoupon> customerCashCouponList=this.getCustomerCashCouponListByCustomerId(customerId);
		//获得当前正在使用的订单
		CashCoupon cashCouopon=this.getCurrentCashCouponCode(customerCashCouponList, sessionOrder.getProductPriceAfterDiscount(), customerId, beginPage);
		
		//获取订单应支付金额
		BigDecimal dueAmount = sessionOrder.getDueAmount();
		//获取用户Cash账户余额
		BigDecimal balance = cashAccountService.getBalance(customerId);			
		//获取Cash账户能支付的金额
		BigDecimal cashCanPayAmount = getCashCanAmount(dueAmount,balance);
		//信用卡与Cash同时支付时，信用卡需支付的金额
		BigDecimal partOnlinePayAmount =dueAmount.subtract(cashCanPayAmount);		
		//获取所有的币种
		List<Currency> currecyList = systemService.getEnabledCurrencies();		
		List<PaymentCurrencyVO> paymentCurrencyList = toPaymentCurrencyVO(currecyList,partOnlinePayAmount,dueAmount);	
		
		//获取所有国家
		List<Country> countries = dictionaryService.getAllCountries();
		List<Country> commonCountries = dictionaryService.getAllCommonCountries();
		
	    //获取所有的信用卡类型
		List<CreditCardType> creditCardTypeList = orderPayService.getCreditCardTypes();
		
		Integer onLinePayTpye=paramInt("OnLinePayType",0);
		Integer currentCreditCardTypeId=paramInt("creditCardType",0);
				
		//获取是否需要Cash支付
		boolean isCashPay = request.getParameterValues("cashAccount") != null;
		boolean isSameAddress=request.getParameterValues("cbSameAddress") != null;
		Currency currency=systemService.getCurrency(paramInt("currency",null));
		PaymentCurrencyVO currentCurrency = null;
		if( currency != null )
		{	//转换当前选择的币种金额
			currentCurrency = toPaymentCurrencyVO(currency,partOnlinePayAmount,dueAmount); 
		}

		Address billingAddress =new Address();
		billingAddress.setFirstName(request.getParameter("BillingAddress_FirstName"));
		billingAddress.setLastName(request.getParameter("BillingAddress_LastName"));
		billingAddress.setPhone(request.getParameter("BillingAddress_PhoneNumber"));
		billingAddress.setStreet1(request.getParameter("BillingAddress_Street1"));
		billingAddress.setStreet2(request.getParameter("BillingAddress_Street2"));
		billingAddress.setCity(request.getParameter("BillingAddress_City"));
		billingAddress.setState(request.getParameter("BillingAddress_State"));
		Country country = dictionaryService.getCountry(paramInt("BillingAddress_Country",0));
		billingAddress.setCountry(country);
		billingAddress.setZip(request.getParameter("BillingAddress_Zip"));
		
		request.setAttribute("countries", countries);
		request.setAttribute("commonCountries", commonCountries);
		request.setAttribute("sessionOrder", sessionOrder);
		request.setAttribute("balance", balance);
		request.setAttribute("currecyList", paymentCurrencyList);
		request.setAttribute("creditCardTypeList",creditCardTypeList);		
		request.setAttribute("dueAmount",dueAmount);
		request.setAttribute("isCashPay", isCashPay);
		request.setAttribute("partOnlinePayAmount",partOnlinePayAmount);
		request.setAttribute("currentCurrency", currentCurrency);
		request.setAttribute("currentCreditCardTypeId", currentCreditCardTypeId);
		request.setAttribute("onLinePayTpye", onLinePayTpye);
		request.setAttribute("currentPage", PlaceOrderPage.PAGE_PAYMENT_METHOD);
		request.setAttribute("isPayError", false);		
		request.setAttribute("isSameAddress", isSameAddress);	
		request.setAttribute("customerCashCouponList", customerCashCouponList);
		request.setAttribute("defaultCustomerCashCouponCode", cashCouopon==null?null:cashCouopon.getCode());
		request.setAttribute("billingAddress", billingAddress);
		return SUCCESS;
	}
	
	/**
	 * 获取此用户关联的现金券的status为"可用"的List
	 * @author zhengw
	 */
	private List<CustomerCashCoupon> getCustomerCashCouponListByCustomerId(Integer customerId){
		Map<CustomerCashCouponCriteria, Object> searchCustomerCashCouponCriteria =new HashMap<CustomerCashCouponCriteria,Object>();
		searchCustomerCashCouponCriteria.put(CustomerCashCouponCriteria.CUSTOMER_ID, customerId);
		searchCustomerCashCouponCriteria.put(CustomerCashCouponCriteria.STATUS, CustomerCouponStatus.ENABLED.getValue());
		
		List<SearchOrder<CustomerCashCouponSearchOrder>> customerCashCouponSearchOrder=new ArrayList<SearchOrder<CustomerCashCouponSearchOrder>>();
		customerCashCouponSearchOrder.add(new SearchOrder<CustomerCashCouponSearchOrder>(CustomerCashCouponSearchOrder.GRANT_DATE));
		PageList<CustomerCashCoupon> customerCashCouponList = cashCouponService.searchCustomerCashCoupon(1, 10000, searchCustomerCashCouponCriteria,customerCashCouponSearchOrder);
		if(customerCashCouponList.getData()!=null && !customerCashCouponList.getData().isEmpty()){
			return customerCashCouponList.getData();
		}
		return null;
	}
	
	
	/**
	 * 如果客户存在有用的现金券，且未选择现金券，则默认选用第一个可用使用的现金券
	 * @author zhengw
	 */
	private CashCoupon applyDefaultCustomerCashCouponCode(List<CustomerCashCoupon> customerCashCouponList,BigDecimal productPrice,Integer customerId){
		if(customerCashCouponList==null || customerCashCouponList.size()==0){
			return null;
		}
		for(CustomerCashCoupon customerCashCoupon : customerCashCouponList){
			try{
				cashCouponService.checkIsAbleUse(productPrice,customerCashCoupon.getCashCoupon().getCode(),customerId);	
				setCashCouponForSessionOrder(customerCashCoupon.getCashCoupon());
				setCashCouponForShoppingCart(customerCashCoupon.getCashCoupon());
				return customerCashCoupon.getCashCoupon();
			}
			catch(BussinessException ex){}				
		}
		return null;
	}
	
	/**
	 * 获得当前下单中使用的现金券
	 * @param customerCashCouponList
	 * @param productPrice
	 * @param customerId
	 * @param beginPage
	 * @return
	 */
	private CashCoupon getCurrentCashCouponCode(List<CustomerCashCoupon> customerCashCouponList,BigDecimal productPrice,Integer customerId,PlaceOrderPage beginPage){
		//获得Session中的现金券
		SessionOrder sessionOrder = getSessionOrder();
		CashCoupon currentCashCoupon=sessionOrder.getPaymentInfo().getCashCouponInfo();		

		//如果客户没有选择现金券，且存在有用的现金券，则默认使用第一个可用的现金券
		if( currentCashCoupon == null && customerCashCouponList!=null)
		{
			if(PlaceOrderPage.PAGE_PAYMENT_METHOD != beginPage)
			{//如果客户没有确认过支付方式，则默认选择第一个可用的现金券
				//if( sessionOrder.getPaymentInfo() == null)
				//{
					if(!isSelectPaymentInfo(sessionOrder))
					{
						//根据需求如果客户的订单金额满足Coupon列表的Min.Order Amount，则默认选择第一个
						currentCashCoupon = this.applyDefaultCustomerCashCouponCode(customerCashCouponList, productPrice,customerId);
					}
					
				//}
			}			
		}		  
		return currentCashCoupon;
	}
	
	/**
	 * 获取默认的可用的现金券
	 * @param customerCashCouponList
	 * @param productPrice
	 * @param customerId
	 * @param beginPageNumber
	 * @return
	 */
	private String getDefaultCustomerCashCouponCode(List<CustomerCashCoupon> customerCashCouponList,BigDecimal productPrice,Integer customerId,Integer beginPageNumber){
		if(customerCashCouponList==null || customerCashCouponList.size()==0){
			return null;
		}
		boolean flag=false;
		//默认选中的现金券
		CustomerCashCoupon defaultCustomerCashCoupon=null;
		Integer length=customerCashCouponList.size();
		
		for(int index=0;index<length;index++){
			try{
				defaultCustomerCashCoupon=customerCashCouponList.get(index);
				flag=cashCouponService.checkIsAbleUse(productPrice,defaultCustomerCashCoupon.getCashCoupon().getCode(),customerId);	
			}catch(BussinessException ex){
				defaultCustomerCashCoupon=null;
			}	
			if(flag){
				break;
			}
		}
		//获得当前正在使用的订单
		SessionOrder order = OrderSessionUtils.getSessionOrder(request.getSession());
		CashCoupon cashCoupon=order.getPaymentInfo().getCashCouponInfo();
		
		//如果在Payment页面之前没有使用现金券，现金券列表有默认的选择项，则使用此默认的现金券
		if(cashCoupon==null){
			if(defaultCustomerCashCoupon!=null){
				//不是本页面(Payment页面)取消，即在Payment页面之前使用了现金券，然后取消，则进入Payment页面有默认值。
				//beginPage(PlaceOrderPage.PAGE_PAYMENT_METHOD);设置开始的页面
				if(beginPageNumber.intValue()!=4){
					setCashCouponForSessionOrder(defaultCustomerCashCoupon.getCashCoupon());
					setCashCouponForShoppingCart(defaultCustomerCashCoupon.getCashCoupon());
					return defaultCustomerCashCoupon.getCashCoupon().getCode();
				}
			}
		//在Payment页面之前使用了现金券
		}else{
			return cashCoupon.getCode();
		}
		return null;
	}
	
	/**
	 * 把CashCoupon存放到SessionOrder中
	 * @param cashCoupon
	 * @return
	 * @author zhengw
	 */
	private SessionOrder setCashCouponForSessionOrder(CashCoupon cashCoupon){
		SessionOrder order = OrderSessionUtils.getSessionOrder(request.getSession());
		if( order != null && order.getPaymentInfo() != null){
			order.getPaymentInfo().setCashCouponInfo(cashCoupon);	
			setSessionOrder(order);
		}			
		return order;
	}
	
	
	
	/**
	 * 保存支付方式
	 * @throws IOException
	 */	
	public void savePayInfo() throws IOException
	{
		if( !isValidSessionByJson()) return;
		
		String fm = "[{\"result\":%1$s}]";		
		int result = 0;
		
		try {
			//保存账单地址
			Address billingAddress = saveBillingAddress();
			if( billingAddress != null )
			{
				//保存支付方式
				if( setPayInfo())
				{
					setBillingAddress(billingAddress);	
					
					if( isCeditCardPay())
					{   //如果需要信用卡支付，则跳转到信用卡支付页面
						result = 1;//信用卡支付情况
					}
					else 
					{  //Cash全额支付或PayPal支付时，跳转到订单确认页面
						result = 2;				
					}
					endPage(PlaceOrderPage.PAGE_PAYMENT_METHOD);
				}
			}
			
		} catch (Exception e) 
		{
			// TODO: handle exception
			errorLog(e);
		}

		returnJson(String.format(fm, result));
	}
	
	/**
	 * 获取当前选择的币种
	 * @param sessionOrder 当前Session订单信息
	 * @param partCreditCardPayAmount 与Cash共同支付时信用卡应支付的金额
	 * @param orderAmount 订单总金额
	 * @return 当前选择的币种
	 */
	private PaymentCurrencyVO getCurrentCurrency(SessionOrder sessionOrder,BigDecimal partCreditCardPayAmount,BigDecimal orderAmount)
	{		
		PaymentCurrencyVO currentCurrency = null;
		//如果当前已选择了币种，则返回当前币种		
		if( sessionOrder.isConfirmPayInfo())
		{
			if( sessionOrder.getPaymentInfo().getCurrency() != null)
			{
			    currentCurrency = toPaymentCurrencyVO(sessionOrder.getPaymentInfo().getCurrency(),partCreditCardPayAmount,orderAmount);
		     }			
		}
		else 
		{
			//从区域信息中读取选择的币种
			CookieArea cookieArea = SessionUtils.getAreaInfo(getSession());
			Currency currency = cookieArea != null ? cookieArea.getCurrency() : null;
			currentCurrency = toPaymentCurrencyVO(currency, partCreditCardPayAmount, orderAmount);
		}
		
		
		//如果当前没有选择币种，则根据货运地址国家获取默认币种
		if( currentCurrency == null  )
		{
			Integer currentCountryID = null;
			if(sessionOrder.getShippingAddress() != null && sessionOrder.getShippingAddress().getCountry() != null)
			{
				currentCountryID = sessionOrder.getShippingAddress().getCountry().getId();
			}
			
			//根据所在国家取默认币种
			Currency defCurrency = systemService.getDefaultCurrencyByCountry(currentCountryID);
			currentCurrency = toPaymentCurrencyVO(defCurrency,partCreditCardPayAmount,orderAmount);
		}	
		
		return currentCurrency;
	}
	
	/**
	 * 获取Cash能支付的金额
	 * @param orderAmount 订单总金额
	 * @param balance 账户余额
	 * @return Cash能支付的金额
	 */
	private BigDecimal getCashCanAmount(BigDecimal orderAmount,BigDecimal balance)
	{
		if( balance == null || BigDecimal.ZERO.compareTo(balance) >= 0)
		{
			return BigDecimal.ZERO;
		}
		
		return orderAmount.compareTo(balance) > 0 ? balance : orderAmount;
	}
	
	/**
	 * 是否需要Cash支付
	 * @param sessionOrder 当前Session订单信息
	 * @param balance 账户余额
	 * @return 是否需要Cash支付
	 */
	private boolean isCashPay(SessionOrder sessionOrder,BigDecimal balance)
	{
		if( balance != null && balance.compareTo(BigDecimal.ZERO) > 0)
		{	
			if( !isSelectPaymentInfo(sessionOrder))
			{//如Cash有余额，且没有确定支付方式时，默认采用现金账户支付		
				return true;
			}
			else
			{//如果已确定过支付方式，则以确定的为准
				return sessionOrder.getPaymentInfo().getCashPay().compareTo(BigDecimal.ZERO) > 0;
			}
		}
		return false;
	}
	
	
	private boolean isSelectPaymentInfo(SessionOrder sessionOrder){
		if( sessionOrder == null) return false;
		return sessionOrder.isConfirmPayInfo();
	}
	
	/**
	 * 是否需要信用卡支付
	 * @return 是否需要信用卡支付
	 */
	private boolean isCeditCardPay()
	{
		SessionOrder sessionOrder = getSessionOrder();
		return( sessionOrder.getPaymentInfo() != null && sessionOrder.getPaymentInfo().getOnLinePayType() != null && OrderService.PAY_TYPE_CREDIT_CARD == sessionOrder.getPaymentInfo().getOnLinePayType() );
	}
	
	/**
	 * 保存账单地址
	 * @param customerId 用户ID
	 * @return
	 */
	private Address saveBillingAddress()
	{
		String firstName = param("BillingAddress_FirstName");
		String lastName  = param("BillingAddress_LastName");
		String phone  = param("BillingAddress_PhoneNumber");
		String street1  = param("BillingAddress_Street1");
		String street2  = param("BillingAddress_Street2");
		String city  = param("BillingAddress_City");
		String state  = param("BillingAddress_State");
		Integer countryId  = paramInt("BillingAddress_Country", 0);
		String zip  = param("BillingAddress_Zip");
		
		//校验输入信息
		if (!validataAddress(firstName,lastName,phone,street1,city,state,zip)) return null;
		
	    //获取国家信息
		Country country = dictionaryService.getCountry(countryId);			
		if (country == null) return null;//国家无效
		
		//构造地址对象
		Address billingAddress = buildAddress(firstName, lastName, phone, "", street1, street2, city, state, country, zip);
		
		if( checkIsModifyAddress(billingAddress) )
		{
			Integer customerId = this.getLoginedUserBuyer().getId();
			//保存账单地址
			customerService.setBilliingAddress(customerId, billingAddress);
		}
		
		return billingAddress;
	}
	
	private boolean checkIsModifyAddress(Address billingAddress)
	{
		Integer customerId = getLoginedUserBuyer().getId();
		
		Address currentBillingAddress = customerService.getBillingAddress(customerId);
		
		return isModifyAddress(billingAddress,currentBillingAddress);
	}
	

	/**
	 * 保存支付信息
	 * @param isCashPay 是否Cash支付
	 * @param currentCreditCardTypeId 当前信用卡类型
	 * @param currentCurrencyId 当前币种
	 */
	private boolean setPayInfo()
	{			
		Boolean isCashPay =  param("IsCashPay",null) == null ? null : paramBool("IsCashPay");
		
		Currency onLinePayCurrency = null;
		CreditCardType creditCardType = null;
		Integer currentCreditCardTypeId = null;
		Integer currentCurrencyId = null;		
		Integer onLinePayType = null;
		
		//如果支付信息不正确则返回
		if( isCashPay == null)
		{
			return false;
		}

		Integer customerId = this.getLoginedUserBuyer().getId();
		
		SessionOrder sessionOrder = getSessionOrder();
		
		//获取订单应支付金额
		BigDecimal dueAmount = sessionOrder.getDueAmount();
		
		//计算Cash应支付金额
		BigDecimal cashPayAmount = BigDecimal.ZERO;		
		if( isCashPay )
		{   
			BigDecimal balance = cashAccountService.getBalance(customerId);	
			cashPayAmount =  dueAmount.compareTo(balance) > 0 ? balance : dueAmount;
		}

		//计算应在线支付金额
		BigDecimal onLinePayAmount = BigDecimal.ZERO;		
		BigDecimal onLinePayAmountUS = BigDecimal.ZERO;		
		onLinePayAmountUS = dueAmount.subtract(cashPayAmount);
		
		//如果需要在线支付，则读取在线支付信息
		if( onLinePayAmountUS.compareTo(BigDecimal.ZERO) > 0)
		{
			currentCreditCardTypeId = paramInt("CurrentCreditCardTypeId");
			currentCurrencyId = paramInt("CurrentCurrencyId");		
			onLinePayType =  paramInt("OnLinePayType");
			
			if( !checkOnLinePayInfo(onLinePayType,currentCurrencyId,currentCreditCardTypeId)) return false;
			
			onLinePayCurrency = systemService.getCurrency(currentCurrencyId);
			if( onLinePayCurrency == null) return false;
			
			//保存正常下单的币种信息同步到网站设置的币种
			CookieArea cookieArea = SessionUtils.getAreaInfo(getSession());
			if(cookieArea == null){
				cookieArea = new CookieArea();
				cookieArea.setUuid(UUID.randomUUID().toString());
			}
			cookieArea.setCurrency(onLinePayCurrency);
			SessionUtils.setAreaInfo(cookieArea, getSession());
			//保存正常下单的币种信息同步到网站设置的币种到数据库
			this.cookieConfigService.setCookie(cookieArea.getUuid(), cookieArea.getCurrency().getId().toString(), CookieConfigService.CookieType.CURRENCY);
			
			onLinePayAmount = CurrencyUtils.USDToOrderCurrency(onLinePayCurrency, onLinePayAmountUS);
			
			 //选择信用卡支付时，获得卡类型
			if( OrderService.PAY_TYPE_CREDIT_CARD == onLinePayType && currentCreditCardTypeId != null)
			{
				creditCardType = orderPayService.getCreditCardType(currentCreditCardTypeId);
				if( creditCardType == null) return false;
			}
			
			
		}
		else
		{
			onLinePayAmount = null;
			onLinePayAmountUS = null;
		}

		setPaymentInfo(cashPayAmount, onLinePayAmount, onLinePayAmountUS,onLinePayCurrency, onLinePayType, creditCardType);
		
		sessionOrder.setConfirmPayInfo(true);
		setSessionOrder(sessionOrder);
		return true;
	}
	
	private boolean checkOnLinePayInfo(Integer onLinePayType,Integer currentCurrencyId,Integer currentCreditCardTypeId)
	{
		//如果需要在线支付
		if( onLinePayType != null)
		{
			//在线支付必须是信用卡或PayPal支付
			 if( OrderService.PAY_TYPE_CREDIT_CARD != onLinePayType && OrderService.PAY_TYPE_PAY_PAL != onLinePayType )
			 {
				 return false;
			 }
			 //选择信用卡支付时，必须选择卡类型
			if( OrderService.PAY_TYPE_CREDIT_CARD == onLinePayType && currentCreditCardTypeId == null)
			{
				return false;	
			}	
			//在线支付必须选择币种
			if( currentCurrencyId == null)
			{
				return false;
			}			
		}
		
		return true; 
	}
	
	private List<PaymentCurrencyVO> toPaymentCurrencyVO(List<Currency> currecyList,BigDecimal partCreditCardPayAmount,BigDecimal orderAmount)
	{
		List<PaymentCurrencyVO>  voList = new ArrayList<PaymentCurrencyVO>();
		PaymentCurrencyVO vo = null;
		for (Currency currecy :currecyList) {
			vo = toPaymentCurrencyVO(currecy,partCreditCardPayAmount,orderAmount);
			voList.add(vo);
		}
		return voList;
	}
	
	private PaymentCurrencyVO toPaymentCurrencyVO(Currency currecy,BigDecimal partCreditCardPayAmount,BigDecimal orderAmount)
	{
		if( currecy == null) return null;
		PaymentCurrencyVO vo = new PaymentCurrencyVO();

		vo.setCurrency(currecy);
		vo.setPartPayAmount(CurrencyUtils.USDToOrderCurrency(currecy, partCreditCardPayAmount));
		vo.setPartPayAmountUS(partCreditCardPayAmount);
			
		vo.setAllPayAmount(CurrencyUtils.USDToOrderCurrency(currecy, orderAmount));
		vo.setAllPayAmountUS(orderAmount);
	
		return vo;
	}
}

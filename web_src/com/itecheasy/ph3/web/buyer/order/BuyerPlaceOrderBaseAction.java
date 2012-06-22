package com.itecheasy.ph3.web.buyer.order;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.struts2.json.annotations.JSON;

import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.common.BeanUtils;
import com.itecheasy.ph3.common.Utilities;
import com.itecheasy.ph3.customer.Address;
import com.itecheasy.ph3.customer.CashAccountService;
import com.itecheasy.ph3.email.EmailService;
import com.itecheasy.ph3.order.CreditCardPayInfo;
import com.itecheasy.ph3.order.CreditCardType;
import com.itecheasy.ph3.order.OnlinePayInfo;
import com.itecheasy.ph3.order.OrderBillingAddress;
import com.itecheasy.ph3.order.OrderPayService;
import com.itecheasy.ph3.order.OrderPaymentInfo;
import com.itecheasy.ph3.order.OrderService;
import com.itecheasy.ph3.order.OrderShippingAddress;
import com.itecheasy.ph3.order.PayPalService;
import com.itecheasy.ph3.shopping.ShoppingCartTotal;
import com.itecheasy.ph3.system.Country;
import com.itecheasy.ph3.system.Currency;
import com.itecheasy.ph3.system.DeliveryFreightRegion;
import com.itecheasy.ph3.system.DictionaryService;
import com.itecheasy.ph3.system.SystemService;
import com.itecheasy.ph3.system.ShippingService.DeliveryTypeEnum;
import com.itecheasy.ph3.web.BuyerBaseAction;
import com.itecheasy.ph3.web.buyer.BuyerPageController;
import com.itecheasy.ph3.web.buyer.BuyerPageController.PlaceOrderPage;
import com.itecheasy.ph3.web.utils.ConfigHelper;
import com.itecheasy.ph3.web.utils.CurrencyUtils;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.utils.StrUtils;
import com.itecheasy.ph3.web.utils.UrlHelper;
import com.itecheasy.ph3.web.vo.CookieArea;
import com.itecheasy.ph3.web.vo.DeliveryVO;
import com.itecheasy.ph3.web.vo.MinShoppingCartTotalInfo;

public class BuyerPlaceOrderBaseAction extends BuyerBaseAction {
	private static final long serialVersionUID = 9881666L;
	protected transient final Logger log = Logger.getLogger("PH3");
	private static final String jsonReturnFormte = "[{\"result\":%1$s}]";
	protected static final String usecashcoupon = "usecashcoupon";
	protected static final String cancelcashcoupon = "cancelcashcoupon";
	/**
	 * P.O BOX地址的正则表达式
	 */
	private static final String PO_BOX_PATTERN = "^.*(hc|p).*box.*$";
	
	protected DictionaryService dictionaryService;
	protected OrderService orderService;
	protected OrderPayService orderPayService;
	private EmailService emailService;
	//protected ShippingService shippingService;
	protected CashAccountService cashAccountService;
	protected SystemService systemService ;
	protected PayPalService payPalService ;
	
	public void setCashAccountService(CashAccountService cashAccountService) {
		this.cashAccountService = cashAccountService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}
	 
	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}
//	public void setShippingService(ShippingService shippingService) {
//		this.shippingService = shippingService;
//	}
	
	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}	
	
	public void setOrderPayService(OrderPayService orderPayService) {
		this.orderPayService = orderPayService;
	}
	
	public void setPayPalService(PayPalService payPalService) {
		this.payPalService = payPalService;
	}
	
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}
	
	private SessionOrder sessionOrder;
	
	/**
	 * 检查Session里的信息是否满足当前页面的要求，不满足要求，则返回需要跳转到的页面
	 * @param currentPageNum 当前页
	 * @return 需要跳转到的页面,如满足要求，则返回NULL
	 */
	protected String checkSessionOrder(PlaceOrderPage currentPageNum)
	{
		SessionOrder sessionOrder = getSessionOrder();
		if( sessionOrder == null)
		{
			return BuyerPageController.GO_TO_SET_SHIPPING_CART;
		}
		
		String lastPage = checkBeginPageAndEndPage(currentPageNum);
		if( lastPage != null)
		{
			return lastPage;
		}
		
		switch (currentPageNum) 
		{
			case PAGE_SHIPPING_METHOD:
			    if( sessionOrder.getShippingAddress() == null)
			    	return BuyerPageController.GO_TO_SET_SHIPPING_ADDRESS;
			    else
                    break;
			    
			case PAGE_PAYMENT_METHOD:
				 if( sessionOrder.getShippingAddress() == null)
				    	return BuyerPageController.GO_TO_SET_SHIPPING_ADDRESS;
				 else if( !isSelectShippingMethod(sessionOrder))
				    	return BuyerPageController.GO_TO_SET_SHIPPING_METHOD;
				 else
	                    break;
				 
			case PAGE_CREDIT_CARD:
				 if( sessionOrder.getShippingAddress() == null)
				    	return BuyerPageController.GO_TO_SET_SHIPPING_ADDRESS;
				 else if( !isSelectShippingMethod(sessionOrder))
				    	return BuyerPageController.GO_TO_SET_SHIPPING_METHOD;
				 else if( !isCreditCardPay(sessionOrder) )
				    	return BuyerPageController.GO_TO_SET_PAY_METHOD;
				 else
	                    break;
				 
			case PAGE_CONFIRM_ORDER:
				 if( sessionOrder.getShippingAddress() == null)
				    	return BuyerPageController.GO_TO_SET_SHIPPING_ADDRESS;
				 else if( !isSelectShippingMethod(sessionOrder))
				    	return BuyerPageController.GO_TO_SET_SHIPPING_METHOD;
				 else if( !isSelectPaymentMethode(sessionOrder))
				    	return BuyerPageController.GO_TO_SET_PAY_METHOD;
				 else
	                    break;

			default:
				break;
		}
		
		OrderSessionUtils.beginPlaceOrderPage(request, currentPageNum);
		return null;
	}
	
	private boolean isSelectPaymentMethode(SessionOrder sessionOrder)
	{
		if( sessionOrder.getPaymentInfo() == null ) return false;
		
		return sessionOrder.getTotalPayAmount().compareTo(sessionOrder.getOrderAmount()) == 0;
	}
	
	/**
	 * 清除当前页面之后步骤设置的信息
	 * @param pageNum 当前页
	 */
/*	protected void clearCurrentInfoOfSessionOrder(PlaceOrderPage pageNum)
	{
		SessionOrder sessionOrder = getSessionOrder();
		switch (pageNum) {
		case PAGE_SHIPPING_ADDRESS:
			sessionOrder.setShippingAddress(null);
			clearCurrentShippingMethod(sessionOrder);
			clearCurrentPaymentMethod(sessionOrder);
			break;
		case PAGE_SHIPPING_METHOD:
			clearCurrentShippingMethod(sessionOrder);
			clearCurrentPaymentMethod(sessionOrder);
			break;
		case PAGE_PAYMENT_METHOD:
			clearCurrentPaymentMethod(sessionOrder);
			break;

		default:
			break;
		}

		setSessionOrder(sessionOrder);
	}*/
	
	
	@JSON(serialize = false)
	protected  SessionOrder getSessionOrder()
	{
		sessionOrder = OrderSessionUtils.getSessionOrder(request.getSession());
		return sessionOrder;
	}
	
	protected void setSessionOrder(SessionOrder sessionOrder) {
		this.sessionOrder =sessionOrder;
		OrderSessionUtils.setSessionOrder(request, sessionOrder);
	}
	
	/**
	 * 设置订单的商品总信息
	 * 
	 * @param cartTotal 商品总信息实体
	 * 
	 */
	protected void setProductTotalInfo(ShoppingCartTotal cartTotal)
	{
		if( cartTotal == null)
		{
			throw new RuntimeException("No shopping cart information");
		}
		
		//设置货物总金额
		sessionOrder.setProductPriceBeforeDiscount(cartTotal.getPriceBeforeDiscount());
		//设置订单应有的订单折扣
		sessionOrder.setDiscount(cartTotal.getDiscount());
		//设置订单商品总物理重量
		sessionOrder.setTotalWeight(cartTotal.getTotalWeight());
		//设置订单商品总运输体积
		sessionOrder.setTotalVolume(cartTotal.getTotalVolume());
		//设置订单商品项数
		sessionOrder.setTotalProductQty(cartTotal.getTotalProductQty());
		//计算订单重量
		BigDecimal orderWeight = this.shippingService.computeOrderWeight(cartTotal.getTotalWeight(), cartTotal.getTotalVolume());
		sessionOrder.setOrderWeight(orderWeight);
	}
	
	/**
	 * 设置订单的货运地址
	 * @param shippingAddress 货运地址
	 */
	protected void setShippingAddress(Address shippingAddress)
	{
		if(shippingAddress == null)
		{
			return;
		}
		
		sessionOrder.setShippingAddress(shippingAddress);
		
		//将订单的货运地址保存到数据库
		orderService.setTempOrderShippingAddress(sessionOrder.getTempOrderId(), shippingAddress);
	
		//改变区域信息设置
		setAreaInfo(shippingAddress.getCountry(),shippingAddress.getCity(),shippingAddress.getZip());	
	}
	
	/**
	 * 根据货运地址自动计算运费，尽量会采用原先已选择货运类型，如果该货运类型不存在，则选择默认的货运类型
	 * 当货运地址变化时，请调用此方法
	 */
	protected void computeFregithByShippingAddress()
	{
		Address shippingAddress = sessionOrder.getShippingAddress();
		if( shippingAddress == null) return;
		
		DeliveryFreightRegion currentDeliveryFreightRegion = null;
		if( sessionOrder.getDelivery() != null && sessionOrder.getDelivery().getDeliveryType() != null )
		{
			currentDeliveryFreightRegion = shippingService.getDeliveryFreightRegion(getDeliveryTypeEnum(sessionOrder.getDelivery().getDeliveryType().getId()), shippingAddress.getCountry().getId(),sessionOrder.getOrderWeight(), SessionUtils.getDeliveryRemoteInfos(getSession()));
		}
		
		if( currentDeliveryFreightRegion == null)
		{
			currentDeliveryFreightRegion = shippingService.getDefaultDeliveryFreightRegion(shippingAddress.getCountry().getId(),sessionOrder.getOrderWeight(),SessionUtils.getDeliveryRemoteInfos(getSession()));
		}
		
		if( currentDeliveryFreightRegion != null)
		{
			sessionOrder.setDelivery(toDeliveryVO(currentDeliveryFreightRegion));	  
		}
	}
	
	/**
	 * 设置订单的账单地址
	 * @param billingAddress 账单地址
	 */
	protected void setBillingAddress(Address billingAddress)
	{
		sessionOrder.setBillingAddress(billingAddress);
		orderService.setTempOrderBillingAddress(sessionOrder.getTempOrderId(), billingAddress);
	}
	
	protected boolean isModifyAddress(Address newAddress,Address currentAddress)
	{
		if( newAddress == null || currentAddress == null) return true;
		
		if( !StrUtils.equalsIgnoreCase(newAddress.getCity(),currentAddress.getCity())) return true;
		if( !StrUtils.equalsIgnoreCase(newAddress.getFirstName(),currentAddress.getFirstName())) return true;
		if( !StrUtils.equalsIgnoreCase(newAddress.getLastName(),currentAddress.getLastName())) return true;
		if( !StrUtils.equalsIgnoreCase(newAddress.getPhone(),currentAddress.getPhone())) return true;
		if( !StrUtils.equalsIgnoreCase(newAddress.getState(),currentAddress.getState())) return true;
		if( !StrUtils.equalsIgnoreCase(newAddress.getStreet1(),currentAddress.getStreet1())) return true;
		if( !StrUtils.equalsIgnoreCase(newAddress.getStreet2(),currentAddress.getStreet2())) return true;
		if( !StrUtils.equalsIgnoreCase(newAddress.getZip(),currentAddress.getZip())) return true;
		if( (newAddress.getCountry() != null  && currentAddress.getCountry() == null) || (newAddress.getCountry() == null  && currentAddress.getCountry() != null) ) return true;
		if( (newAddress.getCountry().getId().compareTo(currentAddress.getCountry().getId()) != 0) ) return true;
				
		return false;
	}

	/**
	 * 设置订单的货运方式(设置货运方式前，必须先设置订单的货运地址，及订单的商品总重量和总体积)
	 * 
	 * @param deliveryId 货运方式ID
	 * 
	 */
	protected void setShippingMethod(Integer deliveryTypeId,boolean isRealInvoice,String shippingComment)
	{
		Address shippingAddress = sessionOrder.getShippingAddress();		
		if( shippingAddress == null || shippingAddress.getCountry() == null )
		{
			throw new RuntimeException("No designated freight address");
		}

		if( deliveryTypeId == null )
		{
			throw new RuntimeException("No designated freight type");
		}

		//计算运费
		DeliveryTypeEnum deliveryType = getDeliveryTypeEnum(deliveryTypeId);		
		//注意，下单过程计算运费时不要使用Session里的偏远信息，因为Session里的偏远信息会根据区域设置变动，而下单的运费只跟货运地址有关
		DeliveryFreightRegion deliveryFreightRegion = shippingService.getDeliveryFreightRegion(deliveryType, shippingAddress.getCountry(),sessionOrder.getOrderWeight(),shippingAddress.getCity(),shippingAddress.getZip());
		if( deliveryFreightRegion == null)
		{	
			throw new RuntimeException("Invalid freight");
		}
		
		setShippingMethod(deliveryFreightRegion,isRealInvoice,shippingComment);
	}	
	
	/**
	 * 设置订单的货运方式(设置货运方式前，必须先设置订单的货运地址，及订单的商品总重量和总体积)
	 * 
	 * @param deliveryId 货运方式ID
	 * 
	 */
	protected void setShippingMethod(DeliveryFreightRegion deliveryFreightRegion,boolean isRealInvoice,String shippingComment)
	{
		if( deliveryFreightRegion == null )
		{
			throw new RuntimeException("No designated freight type");
		}

		//设置货运方式
	    sessionOrder.setDelivery(toDeliveryVO(deliveryFreightRegion));	    
	    sessionOrder.setIsRealInvoice(isRealInvoice);
		sessionOrder.setShippingComment(shippingComment);
		setSessionOrder(sessionOrder);
		
		//保存货运方式
		orderService.setTempOrderDeliveryInfo(sessionOrder.getTempOrderId(), sessionOrder.getDelivery().getBaseInfo().getId(),sessionOrder.getOrderWeight(),sessionOrder.getFreight(),sessionOrder.getRemoteFreight(),isRealInvoice, shippingComment);
		
		//设置订单的货运类型时，同时修改购物车中的货运类型相同
		MinShoppingCartTotalInfo miniCartTotal = getMinShoppingCartTotalInfo();
		CookieArea cookieArea = SessionUtils.getAreaInfo(getSession());
		if( miniCartTotal == null || cookieArea == null) return ;		
		DeliveryFreightRegion currentyDeliveryFreightRegion = this.shippingService.getDeliveryFreightRegion(getDeliveryTypeEnum(deliveryFreightRegion.getDeliveryTypeId()), cookieArea.getCountry().getId(), miniCartTotal.getOrderWeigth(), SessionUtils.getDeliveryRemoteInfos(getSession()));
		if( currentyDeliveryFreightRegion != null )
		{
			miniCartTotal.setCurrentyDeliveryFreightRegion(currentyDeliveryFreightRegion);
			SessionUtils.setMinShoppingCartInfo(getRequest(), miniCartTotal);
		}		
	}	
	
	/**
	 * 保存订单的货运方式到数据库
	 * @param deliveryFreightRegion
	 * @param isRealInvoice
	 * @param shippingComment
	 */
	protected void saveShippingMethod(Integer tempOrderId,BigDecimal orderWeight,DeliveryFreightRegion deliveryFreightRegion,boolean isRealInvoice,String shippingComment)
	{
		if( deliveryFreightRegion == null )
		{
			throw new RuntimeException("No designated freight type");
		}
		
		//保存货运方式
		orderService.setTempOrderDeliveryInfo(tempOrderId, deliveryFreightRegion.getDeliveryId(),orderWeight,deliveryFreightRegion.getTotalFreight(),deliveryFreightRegion.getRemoteFreight(),isRealInvoice, shippingComment);
	}

	/**
	 * 设置订单的支付信息
	 * 
	 * @param cashPay Cash支付金额
	 * @param onLinePay 在线支付金额(支付币种对应金额)
	 * @param onLinePayUS 在线支付金额(美元)
	 * @param payCurrency 在线支付币种
	 * @param onLinePayType 在线支付类型
	 * @param creditCardType 信用卡类型
	 */
	protected void setPaymentInfo(BigDecimal cashPay,BigDecimal onLinePay,BigDecimal onLinePayUS,Currency payCurrency,Integer onLinePayType,CreditCardType creditCardType)
	{
		OrderPaymentInfo paymentInfo =  sessionOrder.getPaymentInfo();
		if( paymentInfo == null )
		{
			paymentInfo = new OrderPaymentInfo();
		}

		paymentInfo.setCashPay(cashPay);
		
		paymentInfo.setOnlinePayInfo(new OnlinePayInfo());
		//获取币种应支付金额,按币种汇率转换
		paymentInfo.setOnLinePay(CurrencyUtils.USDToOrderCurrency(payCurrency, onLinePayUS));		
		paymentInfo.setOnLinePayUs(onLinePayUS);
		paymentInfo.setCurrency(payCurrency);
		paymentInfo.setOnLinePayType(onLinePayType);
		
		//设置信用卡类型
		if( creditCardType != null)
		{
			paymentInfo.setPaymentAttachInfo(new CreditCardPayInfo());
			paymentInfo.getCreditCardPayInfo().setCreditCardType(creditCardType);
		}
		else
		{
			paymentInfo.setPaymentAttachInfo(null);
		}
		
		sessionOrder.setPaymentInfo(paymentInfo);
		
		try 
		{
			orderService.setTempOrderPriceInfo(sessionOrder.getTempOrderId(),paymentInfo,sessionOrder.getCashCouponRedemptionAmount());
		} 
		catch (BussinessException e) 
		{
			// TODO: handle exception
		}	
		
		//设置区域信息
		setAreaInfo(payCurrency);
	}
	
	/**
	 * 判断是否为P.O.Box地址
	 * @param address
	 * @return
	 */
	protected boolean checkPOBoxAddress(String address)
	{
		return customerService.checkPOBoxAddress(address);
	}
	
	protected String setOrderNo()
	{
		return setOrderNo(sessionOrder);
	}
	
	protected String setOrderNo(SessionOrder sessionOrder)
	{
        String orderNo = sessionOrder.getOrderNo();
		
		//如果没有订单号，则生成一个订单号
		if( orderNo == null || orderNo.isEmpty())
		{	//生成订单号
			createOrderNo();	
			orderNo = sessionOrder.getOrderNo();
		}	
		
		return orderNo;
	}
	
	protected boolean createOrderNo()
	{
		if( sessionOrder == null) return false;
		
		String orderNo = sessionOrder.getOrderNo();

		orderNo = orderService.createOrderNo(sessionOrder.getTempOrderId());
		if( orderNo == null) return false;
		
		sessionOrder.setOrderNo(orderNo);
		
		if( sessionOrder.getPaymentInfo() != null && sessionOrder.getPaymentInfo().getCreditCardPayInfo() != null)
		{
			String payOrderNo = orderNo.substring(3);	
			sessionOrder.getPaymentInfo().getCreditCardPayInfo().setPayOrderNo(payOrderNo);
			sessionOrder.getPaymentInfo().getCreditCardPayInfo().setOrderNo(orderNo);
			
			return true;
		}
		else {
			return false;
		}			
	}
	
	protected void placeOrder(String orderNo,Integer tempOrderId) throws BussinessException
	{
		//正式下单
		orderService.placeOrder(tempOrderId);
	}
	
	protected void placeOrder(SessionOrder sessionOrder) throws BussinessException
	{
		String orderNo = setOrderNo(sessionOrder);
		
		//正式下单
		CreditCardPayInfo creditCardPayInfo = null;
		if( sessionOrder.getPaymentInfo().getOnlinePayInfo() != null && sessionOrder.getPaymentInfo().getOnlinePayInfo().getPaymentAttachInfo() instanceof CreditCardPayInfo)
		{
			creditCardPayInfo = (CreditCardPayInfo)sessionOrder.getPaymentInfo().getOnlinePayInfo().getPaymentAttachInfo();
		}
		
		orderService.placeOrder(sessionOrder.getCustomerId(), sessionOrder.getShoppingCartId(),creditCardPayInfo);
	}
	
	/**
	 * Ajax调用时，检查当前会话是否有效
	 * 如果用户未登录，则要求返回登录页面；如果Session中没有订单信息，则要求返回到购物车页面
	 * @return
	 * @throws IOException
	 */
	protected boolean isValidSessionByJson()  throws IOException
	{
		if( getLoginedUserBuyer() == null)
		{
			returnJson(String.format(jsonReturnFormte, "\"" + LOGIN + "\""));
			return false;
		}
		
		if( getSessionOrder() == null)
		{
			returnJson(String.format(jsonReturnFormte, "\"" + BuyerPageController.GO_TO_SET_SHIPPING_CART + "\""));
			return false;
		}
		return true;
	}
	
	protected DeliveryVO toDeliveryVO(DeliveryFreightRegion currentyDeliveryFreightRegion)
	{
		if( currentyDeliveryFreightRegion == null) return null;
		
		DeliveryVO vo = new DeliveryVO();
		vo.setDeliveryType(shippingService.getDeliveryTypeByDeliveryId(currentyDeliveryFreightRegion.getDeliveryId()));
		vo.setBaseInfo(shippingService.getDelivery(currentyDeliveryFreightRegion.getDeliveryId()));
		vo.setBaseFreight(currentyDeliveryFreightRegion.getBaseFreight());
		vo.setRemoteFreight(currentyDeliveryFreightRegion.getRemoteFreight());
		
		return vo;
	}
	
	/**
	 * 验证地址输入信息
	 * @param firstName
	 * @param lastName
	 * @param phone
	 * @param street1
	 * @param city
	 * @param state
	 * @param zip
	 * @return
	 */
	protected boolean validataAddress(String firstName, String lastName, String phone, String street1, String city, String state, String zip)
	{
		if (firstName == null || firstName.isEmpty()) {
			return false;
		}
		if (lastName == null || lastName.isEmpty()) {
			return false;
		}
		if (phone == null || phone.isEmpty()) {
			return false;
		}
		if (street1 == null || street1.isEmpty()) {
			return false;
		}
		if (city == null || city.isEmpty()) {
			return false;
		}
		if (state == null || state.isEmpty()) {
			return false;
		}
		if (zip == null || zip.isEmpty()) {
			return false;
		}
		return true;
	}
	
	/**
	 * 构造地址对象
	 * @param firstName
	 * @param lastName
	 * @param phone
	 * @param fax
	 * @param street1
	 * @param street2
	 * @param city
	 * @param state
	 * @param country
	 * @param zip
	 * @return
	 */
	protected Address buildAddress(String firstName, String lastName, String phone, String fax, String street1, String street2,String city, String state, Country country,String zip)
	{
		Address address = new Address();
		address.setFirstName(firstName);
		address.setLastName(lastName);
		address.setPhone(phone);
//		address.setFax(fax);
		address.setStreet1(street1);
		address.setStreet2(street2);
		address.setCity(city);
		address.setState(state);
		address.setCountry(country);
		address.setZip(zip);
		
		return address;
	}
	
	/**
	 * 当货运地址没有运费是发出警报：记日志并发邮件给客服
	 */
	protected void warnNotHaveFreight(String customerEmail,String countryName,BigDecimal orderWeight,String checkOutType,String customerType)
	{
		StringBuilder content = new StringBuilder();
		content.append("时间:" + (new Date()).toLocaleString());
		content.append("\r\nEmail:" + customerEmail + "(" + customerType + ")");
		content.append("\r\n货运国家:" + countryName);
		content.append("\r\n订单重量:" + orderWeight.toString() + "g");
		content.append("\r\n支付方式:" + checkOutType);
		
		addOrderErrorLog("无运费订单信息 ",customerEmail,countryName,orderWeight,checkOutType,customerType);
		
		//发邮件
		String subject = "无运费订单信息 " + UrlHelper.getIpAddr(request);
		emailService.sendEmailToCustomerService(subject, content.toString());
	}
	
	/**
	 * 记录PayPal下单错误日志
	 */
	protected void addOrderErrorLog(String errorType,String customerEmail,String countryName,BigDecimal orderWeight,String checkOutType,String customerType)
	{
		StringBuilder content = new StringBuilder();
		if( !Utilities.isEmpty(errorType))
		{
			content.append(errorType + "\r\n");
		}
		
		content.append("时间:" + (new Date()).toLocaleString());
		content.append("\r\nEmail:" + customerEmail + "(" + customerType + ")");
		content.append("\r\n货运国家:" + countryName);
		content.append("\r\n订单重量:" + orderWeight.toString() + "g");
		content.append("\r\n支付方式:" + checkOutType);
		
		//记录日志
		log.error(content.toString());
	}
	
	
	protected SessionOrder copyOrder(SessionOrder order)
	{
		if( order == null) return null;
		SessionOrder newOrder = new SessionOrder();
	
		newOrder.setShippingAddress(BeanUtils.copyProperties(order.getShippingAddress(),Address.class));
		newOrder.setBillingAddress(BeanUtils.copyProperties(order.getBillingAddress(),Address.class));
		
		if(order.getPaymentInfo() != null)
		{			
			newOrder.setPaymentInfo(BeanUtils.copyProperties(order.getPaymentInfo(),OrderPaymentInfo.class,new String[] {"OnlinePayInfo","CreditCardPayInfo","OnLinePayType","OnLinePay","OnLinePayUs","Currency"}));
			if( order.getPaymentInfo().getOnlinePayInfo() != null)
			{				
				newOrder.getPaymentInfo().setOnlinePayInfo(BeanUtils.copyProperties(order.getPaymentInfo().getOnlinePayInfo(),OnlinePayInfo.class,new String[] {"CreditCardPayInfo"}));
				
				if( order.getPaymentInfo().getOnlinePayInfo().getPaymentAttachInfo() != null && order.getPaymentInfo().getOnlinePayInfo().getPaymentAttachInfo() instanceof CreditCardPayInfo)
				{
					CreditCardPayInfo creditCardPayInfo = (CreditCardPayInfo)order.getPaymentInfo().getOnlinePayInfo().getPaymentAttachInfo();
					newOrder.getPaymentInfo().getOnlinePayInfo().setPaymentAttachInfo(BeanUtils.copyProperties(creditCardPayInfo,CreditCardPayInfo.class));
					if( creditCardPayInfo.getCreditCardType() != null)
					{
						((CreditCardPayInfo)newOrder.getPaymentInfo().getOnlinePayInfo().getPaymentAttachInfo()).setCreditCardType(BeanUtils.copyProperties(creditCardPayInfo.getCreditCardType(),CreditCardType.class));
					}
				}
			}
		}
		return newOrder;
	}
	
	private OrderShippingAddress toOrderShippingAddress(Address address)
	{
		if( address == null) return null;
		return BeanUtils.copyProperties(address,OrderShippingAddress.class);
	}
	private OrderBillingAddress toOrderBillingAddress(Address address)
	{
		if( address == null) return null;
		return BeanUtils.copyProperties(address,OrderBillingAddress.class);
	}
	
	/**
	 * 检查是否是PO Box地址禁用的货运方式
	 * @param deliveryId
	 * @return
	 */
	protected boolean isDisabledDeliveryOfPOBOX(Integer deliveryId) {
		return ConfigHelper.DELIVERY_DHL_ID == deliveryId || ConfigHelper.DELIVERY_UPS_ID == deliveryId || ConfigHelper.DELIVERY_FEDEX_ID  == deliveryId;
	}
	
	/**
	 * 清除当前选择的货运方式
	 * @param sessionOrder
	 */
	private void clearCurrentShippingMethod(SessionOrder sessionOrder)
	{
		if( sessionOrder == null) return;
		sessionOrder.setDelivery(null);
		sessionOrder.setIsRealInvoice(null);
	}
	
	/**
	 * 清除当前选择的支付方式
	 * @param sessionOrder
	 */
	private void clearCurrentPaymentMethod(SessionOrder sessionOrder)
	{
		if( sessionOrder == null) return;
		
		sessionOrder.setPaymentInfo(null);
		sessionOrder.setBillingAddress(null);
	}
	
	/**
	 * 当前订单是否选择了货运方式
	 * @param sessionOrder
	 * @return
	 */
	private boolean isSelectShippingMethod(SessionOrder sessionOrder)
	{
		return sessionOrder != null && sessionOrder.getDelivery() != null && sessionOrder.getIsRealInvoice() != null;
	}
	
	/**
	 * 当前订单是否设置了信用卡支付
	 * @param sessionOrder
	 * @return
	 */
	private boolean isCreditCardPay(SessionOrder sessionOrder)
	{
		return sessionOrder != null && sessionOrder.getBillingAddress() != null && sessionOrder.getPaymentInfo() != null && sessionOrder.getPaymentInfo().getCreditCardPayInfo() != null && sessionOrder.getPaymentInfo().getOnLinePayUs().compareTo(BigDecimal.ZERO) > 0;
	}
}

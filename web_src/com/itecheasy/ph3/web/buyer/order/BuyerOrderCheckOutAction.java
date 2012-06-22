package com.itecheasy.ph3.web.buyer.order;

import java.math.BigDecimal;

import javax.xml.ws.Holder;

import org.apache.poi.hssf.record.chart.BeginRecord;

import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.common.DeployProperties;
import com.itecheasy.ph3.customer.Address;
import com.itecheasy.ph3.customer.CustomerHabit;
import com.itecheasy.ph3.order.CashCoupon;
import com.itecheasy.ph3.order.CreditCardPayInfo;
import com.itecheasy.ph3.order.CreditCardType;
import com.itecheasy.ph3.order.ExpressPayPalInfo;
import com.itecheasy.ph3.order.OnlinePayInfo;
import com.itecheasy.ph3.order.OrderPaymentInfo;
import com.itecheasy.ph3.order.OrderService;
import com.itecheasy.ph3.shopping.ShoppingCartTotal;
import com.itecheasy.ph3.system.Currency;
import com.itecheasy.ph3.system.DeliveryFreightRegion;
import com.itecheasy.ph3.web.buyer.BuyerPageController;
import com.itecheasy.ph3.web.buyer.BuyerPageController.PlaceOrderPage;
import com.itecheasy.ph3.web.utils.CurrencyUtils;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.vo.CookieArea;
import com.itecheasy.ph3.web.vo.MinShoppingCartTotalInfo;
import com.itecheasy.sslplugin.annotation.Secured;

@Secured
public class BuyerOrderCheckOutAction extends BuyerPlaceOrderBaseAction {
	private static final long serialVersionUID = 9881666L;
	private static final String paypalExpressUrl = DeployProperties.getInstance().getExpressPayPalWebURL();

	public String expressCheckOut() throws Exception 
	{
		// 获取客户ID
		Integer customerId = getLoginedUserBuyer() == null ? null : getLoginedUserBuyer().getId();

		// 获取购物车编号
		Integer shoppingCartId = getShoppingCartId();
		if( shoppingCartId == null)
		{
			return BuyerPageController.GO_TO_SET_SHIPPING_CART;
		}
		
		MinShoppingCartTotalInfo minShoppingCartTotalInfo = getMinShoppingCartTotalInfo();		
		CookieArea cookieArea = SessionUtils.getAreaInfo(request.getSession());

		// 下预订单
		Integer tempOrderId = null;
		Holder<String> payPalACK = new Holder<String>();
		Holder<String> outOrderNo = new Holder<String>();
		try 
		{
			Currency payCurrency = cookieArea.getCurrency();//支付时使用的币种
			
			BigDecimal orderAmountUS = minShoppingCartTotalInfo.getDueAmount();//订单金额（美元）
			BigDecimal orderAmount = CurrencyUtils.USDToOrderCurrency(payCurrency,orderAmountUS);//订单金额（支付币种对应的金额）
			int onLinePayType = OrderService.ONLINE_PAY_TYPE_PAYPAL_EXPRESS;//支付类型：PayPal快速支付			
			
			OrderPaymentInfo orderPaymentInfo = new OrderPaymentInfo();
			OnlinePayInfo onlinePayInfo = new OnlinePayInfo();
			onlinePayInfo.setAmount(orderAmount);
			onlinePayInfo.setAmountUs(orderAmountUS);
			onlinePayInfo.setCurrency(payCurrency);
			onlinePayInfo.setOnLinePayType(onLinePayType);
			orderPaymentInfo.setOnlinePayInfo(onlinePayInfo);
			orderPaymentInfo.setCashCouponInfo(minShoppingCartTotalInfo.getCashCouponInfo());			
			orderPaymentInfo.setOnLinePayType(onLinePayType);
			orderPaymentInfo.setOnLinePayUs(orderAmountUS);
			orderPaymentInfo.setOnLinePay(orderAmount);
			orderPaymentInfo.setCurrency(payCurrency);
			//PayPal快速支付时，生成临时订单
			tempOrderId = orderService.bookOrderByExpressCheckOut(customerId, shoppingCartId,orderPaymentInfo,minShoppingCartTotalInfo.getCashCouponRedemptionAmount(),outOrderNo,payPalACK);
			
			if( tempOrderId != null)
			{
				InitOrderByExpressCheckOut(customerId,shoppingCartId,tempOrderId);				
				
				/*ExpressPayPalInfo expressPayPalInfo = new ExpressPayPalInfo();
				expressPayPalInfo.setOrderNo(outOrderNo.value);
				expressPayPalInfo.setToken(payPalACK.value);
				expressPayPalInfo.setTempOrderId(tempOrderId);
				orderPaymentInfo.getOnlinePayInfo().setPaymentAttachInfo(expressPayPalInfo);
				getSessionOrder().setPaymentInfo(orderPaymentInfo);*/
			}
		} 
		catch (BussinessException e) 
		{
			String errMsg = e.getErrorMessage();			

			if (OrderService.ERROR_PRODUCT_UNDER_STOCK == errMsg) 
			{ // 库存不足时返回购物车页面			
				return BuyerPageController.GO_TO_SET_SHIPPING_CART;
			} 
			else if (OrderService.ERROR_SHOP_CART_EMPTY == errMsg) 
			{ // 如果购物车没有任何信息，仍返回购物车页面
				return BuyerPageController.GO_TO_SET_SHIPPING_CART;
			} 
			else 
			{
				log.error(e.getMessage());
				e.printStackTrace();
				throw new Exception(e);
			}
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
			throw ex;
		}
		
		strPayPalACK = payPalACK.value;
		
		payPalService.addInfoOperateLog("ExpressCheckOut:Began to jump to PayPal pay site.Token:" + strPayPalACK + ",ShoppingCartId:" + shoppingCartId.toString());	
		return SUCCESS;
	}
	
	private String strPayPalACK;
	public String getPayPalACK()
	{
		return strPayPalACK;
	}
	
	public String getPaypalExpressUrl()
	{
		return paypalExpressUrl;
	}
	
	public String checkOut() throws Exception {
		// 获取客户ID
		Integer customerId = getLoginedUserBuyer().getId();
		if (customerId == null)
			return LOGIN;

		// 获取购物车编号
		Integer shoppingCartId = getShoppingCartId();
		if( shoppingCartId == null)
		{
			return BuyerPageController.GO_TO_SET_SHIPPING_CART;
		}

		// 下预订单
		Integer tempOrderId = null;
		try 
		{
			tempOrderId = bookOrder(customerId, shoppingCartId);
		} 
		catch (BussinessException e) 
		{//下订单失败，抛业务异常
			String errMsg = e.getErrorMessage();

			if (OrderService.ERROR_PRODUCT_UNDER_STOCK == errMsg) 
			{ // 库存不足时返回购物车页面				
				return BuyerPageController.GO_TO_SET_SHIPPING_CART;
			} 
			else if (OrderService.ERROR_SHOP_CART_EMPTY == errMsg) 
			{ // 如果购物车没有任何信息，仍返回购物车页面
				return BuyerPageController.GO_TO_SET_SHIPPING_CART;
			} 
			else 
			{
				log.error(e.getMessage());
				e.printStackTrace();
				throw new Exception(e);
			}
		} 
		catch (Exception ex) 
		{//下单失败，抛运行时异常
			ex.printStackTrace();
			throw ex;
		}

		// 如果没有预订ID，则抛异常
		if (tempOrderId == null || tempOrderId <= 0) 
		{
			throw new RuntimeException("Reservation failure");
		}
		
		//获取区域设置的信息，要在InitOrder()方法之前调用,因为InitOrder()方法会根据货运地址重新设置区域信息
		Address areaSetShippingAddress = this.getAreaSetShippingAddress();
		//获取迷你购物车的总运费，要在InitOrder()方法之前调用,因为InitOrder()方法会修改迷你购物的运费
		MinShoppingCartTotalInfo minShoppingCartTotalInfo = getMinShoppingCartTotalInfo();	
		BigDecimal shoppingCartTotalFreight = minShoppingCartTotalInfo.getTotalFreight();
		
		// 初始化订单信息
		InitOrder(customerId, shoppingCartId, tempOrderId);
		
		//设置下单时当前完成操作的页面
		endPage(PlaceOrderPage.PAGE_SHIPPING_CART);

		// 跳到指定页面
		//1、没有默认的货运地址，则跳转到设置货运地址页面
		if (getSessionOrder().getShippingAddress() == null) 
		{
			OrderSessionUtils.beginPlaceOrderPage(request,PlaceOrderPage.PAGE_SHIPPING_CART);
			OrderSessionUtils.endPlaceOrderPage(request,PlaceOrderPage.PAGE_SHIPPING_CART);
			return BuyerPageController.GO_TO_SET_SHIPPING_ADDRESS;
		}
		
		//2、检查货运地址是否为P.O BOX地址，是P.O BOX地址，则跳转到设置货运方式页面
		boolean isPOBOXAddress = checkPOBoxAddress(getSessionOrder().getShippingAddress().getStreet1()) || checkPOBoxAddress(getSessionOrder().getShippingAddress().getStreet2());
		//3、检查货运地址中的国家和区域设置的国家是否一致，不一致则跳转到货运方式页面
		boolean isChangeCountry = getSessionOrder().getShippingAddress().getCountry().getId().intValue() != areaSetShippingAddress.getCountry().getId().intValue();
		//4、检查是否有运费,无运费则跳转到设置货运方式页面
		boolean hasFreight = getSessionOrder().getDelivery() != null && getSessionOrder().getFreight() != null;
		//5、检查订单中的运费是否与购物车中的运费是否相同，不一致则跳转到货运方式页面
		
		boolean isChangeFreight = !hasFreight || getSessionOrder().getFreight().compareTo(shoppingCartTotalFreight) != 0;
		if( isPOBOXAddress || isChangeCountry || !hasFreight || isChangeFreight )
		{
			OrderSessionUtils.beginPlaceOrderPage(request,PlaceOrderPage.PAGE_SHIPPING_ADDRESS);
			OrderSessionUtils.endPlaceOrderPage(request,PlaceOrderPage.PAGE_SHIPPING_ADDRESS);
			return BuyerPageController.GO_TO_SET_SHIPPING_METHOD;
		}
		
		//6、如果上面条件均满足，则跳转到设置付款方式页面
		OrderSessionUtils.beginPlaceOrderPage(request,PlaceOrderPage.PAGE_SHIPPING_ADDRESS);
		OrderSessionUtils.endPlaceOrderPage(request,PlaceOrderPage.PAGE_SHIPPING_ADDRESS);
		return BuyerPageController.GO_TO_SET_PAY_METHOD;
	}
	
	
	/**
	 * 获取cookieArea区域信息设置的地址
	 */
	private Address getAreaSetShippingAddress()
	{
		CookieArea cookieArea = SessionUtils.getAreaInfo(getSession());
		if(cookieArea == null)
		{
			return null;
		}
		else
		{
			Address areaSetShippingAddress = new Address();
			areaSetShippingAddress.setCountry(cookieArea.getCountry());
			areaSetShippingAddress.setCity(cookieArea.getCity());
			areaSetShippingAddress.setZip(cookieArea.getZip());
			return areaSetShippingAddress;
		}
	}

	/**
	 * 预订当前购物车中的商品
	 * 
	 * @param customerId
	 *            预订的客户iD
	 * @param shoppingCartId
	 *            购物车编号
	 * 
	 * @return 预订成功后，返回预订ID
	 */
	private Integer bookOrder(Integer customerId, Integer shoppingCartId) throws BussinessException 
	{	
		return orderService.bookOrder(customerId, shoppingCartId);
	}

	/**
	 * 初始化订单信息
	 * 
	 * @param customerId
	 *            预订的客户iD
	 * @param shoppingCartId
	 *            购物车编号
	 * @param tempOrderId
	 *            临时订单ID
	 */
	private void InitOrder(Integer customerId, Integer shoppingCartId,Integer tempOrderId) 
	{
		// 初始化一个新的订单实体
		SessionOrder sessionOrder = new SessionOrder();
		sessionOrder.setTempOrderId(tempOrderId);
		sessionOrder.setCustomerId(customerId);
		sessionOrder.setShoppingCartId(shoppingCartId);
		setSessionOrder(sessionOrder);

		// 加载订单商品信息
		loadOrderProductInfo(shoppingCartId);

		// 加载用户默认的货运地址
		loadDefaultShippingAddress(customerId);
		// 加载用户默认的账单地址
		loadDefaultBillingAddress(customerId);

		// 获取客户的下下单习性
		CustomerHabit customerHabit = customerService.getCustomerHabit(customerId);

		// 加载用户默认的货运类型和默认的发票类型
		loadDefaultShippingMethod(customerHabit);
		
		// 加载用户默认的付款信息
		loadDefaultPaymentInfo(customerHabit);
	}
	
	/**
	 * ExpressCheckOut时,初始化订单信息
	 * 
	 * @param customerId
	 *            预订的客户iD
	 * @param shoppingCartId
	 *            购物车编号
	 * @param tempOrderId
	 *            临时订单ID
	 */
	private void InitOrderByExpressCheckOut(Integer customerId, Integer shoppingCartId,Integer tempOrderId)
	{	//设置货运信息：读取购物车中的运费信息
		MinShoppingCartTotalInfo minShoppingCartTotalInfo = getMinShoppingCartTotalInfo();	
		DeliveryFreightRegion deliveryFreightRegion= minShoppingCartTotalInfo.getCurrentyDeliveryFreightRegion();			
		boolean isRealInvoice = paramBool("CustomsType");// 获得发票类型			
		String shippingComment = param("HidShippingComment").trim();// 获得发货提醒
		
		saveShippingMethod(tempOrderId, minShoppingCartTotalInfo.getOrderWeigth(), deliveryFreightRegion, isRealInvoice, shippingComment);
	}

	/**
	 * 加载订单商品信息
	 * 
	 * @param sessionOrder
	 *            订单实体
	 * @param shoppingCartId
	 *            购物车编号
	 */
	private void loadOrderProductInfo(Integer shoppingCartId) 
	{	// 获取购物车信息
		ShoppingCartTotal cartTotal = shoppingService.getShoppingCartTotalInfo(shoppingCartId);

		setProductTotalInfo(cartTotal);
	}

	/**
	 * 加载用户默认的货运地址
	 * 
	 * @param customerId
	 *            客户Id
	 */
	private void loadDefaultShippingAddress(Integer customerId) 
	{
		// 获取客户的默认货运地址
		Address shippingAddress = customerService.getDefaultShippingAddress(customerId);

		setShippingAddress(shippingAddress);
	}

	/**
	 * 加载用户默认的账单地址
	 * 
	 * @param customerId
	 *            客户Id
	 */
	private void loadDefaultBillingAddress(Integer customerId) 
	{
		// 获取客户的账单地址
		Address billingAddress = customerService.getBillingAddress(customerId);
		setBillingAddress(billingAddress);
	}

	/**
	 * 加载用户默认的货运类型(与购物车中选择的货运类型保持相同，如没有对应的货运类型再选择其他货运类型，如都没有，则不用设置货运类型)和用户默认的发票类型(用户最后一次订单的发票类型)
	 * 
	 * @param customerHabit
	 *            用户下单习性
	 */
	private void loadDefaultShippingMethod(CustomerHabit customerHabit) 
	{
		Address shippingAddress = getSessionOrder().getShippingAddress();
		if (shippingAddress == null || shippingAddress.getCountry() == null) return;
		
		//客户上一个订单的发票类型
		boolean isRealInvoice = customerHabit != null ? customerHabit.getInvoiceType() : true;
		
		//1、获得购物车中选择的货运类型
		MinShoppingCartTotalInfo minShoppingCartTotalInfo = getMinShoppingCartTotalInfo();
		Integer shoppingCartDeliveryTypeId = minShoppingCartTotalInfo.getCurrentyDeliveryFreightRegion() == null ? null : minShoppingCartTotalInfo.getCurrentyDeliveryFreightRegion().getDeliveryTypeId();
		
		//2、根据订单的货运地址和购物车中的货运类型获得运费		
		DeliveryFreightRegion deliveryFreightRegion = null;
		if( shoppingCartDeliveryTypeId == null )
		{
//			deliveryFreightRegion = shippingService.getDefaultDeliveryFreightRegion(shippingAddress.getCountry().getId(), getSessionOrder().getOrderWeight(), SessionUtils.getDeliveryRemoteInfos(getSession()));
			deliveryFreightRegion = shippingService.getDefaultDeliveryFreightRegion(shippingAddress.getCountry(), getSessionOrder().getOrderWeight(), shippingAddress.getCity(),shippingAddress.getZip());
		}
		else 
		{
			deliveryFreightRegion = shippingService.getDeliveryFreightRegion(getDeliveryTypeEnum(shoppingCartDeliveryTypeId), shippingAddress.getCountry(), getSessionOrder().getOrderWeight(), shippingAddress.getCity(),shippingAddress.getZip());
		}
		
		//3、设置默认的货运信息
		if( deliveryFreightRegion != null)
		{
			setShippingMethod(deliveryFreightRegion, isRealInvoice, null);
		}
		else 
		{
			getSessionOrder().setIsRealInvoice(isRealInvoice);
		}
	}
	
	/**
	 * 加载用户默认的支付信息
	 * 
	 * @param customerHabit 客户下单习性信息:客户上一个订单预订的各种信息，如在线支付方式、信用卡类型等
	 */
	private void loadDefaultPaymentInfo(CustomerHabit customerHabit) 
	{		
		SessionOrder sessionOrder = getSessionOrder();
		OrderPaymentInfo paymentInfo = new OrderPaymentInfo();
		paymentInfo.setOnlinePayInfo(new OnlinePayInfo());
		
		//1、加载默认币种：跟区域设置的币种一致
		Currency defaultCurrency = SessionUtils.getAreaInfo(getSession()).getCurrency();
		paymentInfo.setCurrency(defaultCurrency);
		
		//2、加载订单使用的陷进去:读取购物车中的现金券信息
		MinShoppingCartTotalInfo minShoppingCartTotalInfo = getMinShoppingCartTotalInfo();	
		CashCoupon cashCoupon = minShoppingCartTotalInfo == null ? null : minShoppingCartTotalInfo.getCashCouponInfo();
		paymentInfo.setCashCouponInfo(cashCoupon);
		
		if (customerHabit != null) //客户习性存储的是客户上一个订单预订的各种信息，如在线支付方式、信用卡类型等
		{
			//3、加载默认的在线支付方式:上一个订单的在线支付方式
			paymentInfo.setOnLinePayType(customerHabit.getOnlinePayType());
			
			//4、加载默认的信用卡类型：上一个订单的信用卡类型
			CreditCardType creditCardType = orderPayService.getCreditCardType(customerHabit.getCreditCardType());
			if( creditCardType != null )
			{
				CreditCardPayInfo creditCardPayInfo = new CreditCardPayInfo();
				creditCardPayInfo.setCreditCardType(creditCardType);
				paymentInfo.setPaymentAttachInfo(creditCardPayInfo);
			}			
		}

		sessionOrder.setPaymentInfo(paymentInfo);
	}
}

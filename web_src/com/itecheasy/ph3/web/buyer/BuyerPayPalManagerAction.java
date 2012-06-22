package com.itecheasy.ph3.web.buyer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.itecheasy.common.Page;
import com.itecheasy.common.PageList;
import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.SearchOrder;
import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.customer.CustomerService;
import com.itecheasy.ph3.order.CashCoupon;
import com.itecheasy.ph3.order.CashCouponService;
import com.itecheasy.ph3.order.CreditCardPayInfo;
import com.itecheasy.ph3.order.Order;
import com.itecheasy.ph3.order.OrderForCMSAndDMSService;
import com.itecheasy.ph3.order.OrderItem;
import com.itecheasy.ph3.order.OrderPayService;
import com.itecheasy.ph3.order.OrderProgressBarItem;
import com.itecheasy.ph3.order.OrderSendInfo;
import com.itecheasy.ph3.order.OrderService;
import com.itecheasy.ph3.order.OrderService.OrderSearchCriteria;
import com.itecheasy.ph3.order.OrderService.OrderSearchOrder;
import com.itecheasy.ph3.system.CookieConfigService;
import com.itecheasy.ph3.system.Currency;
import com.itecheasy.ph3.system.DeliveryType;
import com.itecheasy.ph3.system.DictionaryService;
import com.itecheasy.ph3.system.ShippingService;
import com.itecheasy.ph3.system.SystemService;
import com.itecheasy.ph3.web.BuyerBaseAction;
import com.itecheasy.ph3.web.exception.AppException;
import com.itecheasy.ph3.web.vo.OrderVO;
import com.itecheasy.sslplugin.annotation.Secured;
/**
 * 关于PayPal账号的的操作
 * @author shuaibf
 */
@Secured
public class BuyerPayPalManagerAction extends BuyerBaseAction {
	private OrderService orderService;
	private OrderPayService orderPayService;
	private OrderForCMSAndDMSService orderForCMSAndDMSService;
	private SystemService systemService;
	private CashCouponService cashCouponService;
	
	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public void setOrderForCMSAndDMSService(
			OrderForCMSAndDMSService orderForCMSAndDMSService) {
		this.orderForCMSAndDMSService = orderForCMSAndDMSService;
	}

	public void setOrderPayService(OrderPayService orderPayService) {
		this.orderPayService = orderPayService;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}
	
	public void setCashCouponService(CashCouponService cashCouponService) {
		this.cashCouponService = cashCouponService;
	}

	/**
	 * 去paypal订单详细页面
	 * @return
	 */
	public String searchPayPalOrder() throws AppException {
		String orderNo = param("orderId").toUpperCase();				//订单编号
		String payPalEmail = param("payPalEmail");		//PayPal账号Email
		if(StringUtils.isEmpty(orderNo)||StringUtils.isEmpty(payPalEmail)){//检查数据的有效性
			return "fail";								
		}		
//		Order order = orderService.getOrderByOrderNo(orderNo);
		Order order =orderService.getOrderByPayPalCustomer(payPalEmail, orderNo);//得到对应的订单
		if (order == null) {							//不存在就返回
			setMessageInfo("ERROR_PAYPAL_EMAIL_OR_ORDERID_NOT_EXISTS");
			return "fail";
		}
		//组合条件得到最近的10条订单		
		Map<OrderSearchCriteria, Object> searchCriteria = new HashMap<OrderSearchCriteria, Object>();//添加PayPal账号Email条件
		List<SearchOrder<OrderSearchOrder>> searchOrder = new ArrayList<SearchOrder<OrderSearchOrder>>();
		searchOrder.add( new SearchOrder<OrderSearchOrder>(OrderSearchOrder.ORDER_DATE, false));//按下单日期倒序
		PageList<Order> orderList =orderService.searchPaypalOrders(1, 10,payPalEmail, searchCriteria, searchOrder);//得到最近下的10次单
		List<String> orderNoList = new ArrayList<String>();		
		for(Order o :orderList.getData()){		
			orderNoList.add(o.getOrderNo());
		}
		Integer orderStatusId = order.getOrderStatus().getId();//得到当前订单的状态Id
		Page page = null;									
		List<OrderItem> orderItemList = null;					//订单商品详细集合
		int completedStatus = OrderService.ORDER_STATUS_COMPLETED; 
		int shippedStatus = OrderService.ORDER_STATUS_SHIPPED;	//订单的各种状态
		int pendingStatus = OrderService.ORDER_STATUS_PENDING_CONFIRMATION;
	
		boolean trackOrderPackageShow = false;
		boolean showShippingComment = false;
		Integer pageSize = 100;
		OrderSendInfo orderSendInfo = orderForCMSAndDMSService.getSendInfo(order.getId());
		OrderVO orderVO = new OrderVO();
		orderVO.setOrder(order);
		orderVO.setOrderSendInfo(orderSendInfo);
		DeliveryType deliveryType=shippingService.getDeliveryTypeByDeliveryId(order.getDeliveryId());
		orderVO.setDelivery(shippingService.getDelivery(order.getDeliveryId()));
		CreditCardPayInfo creditCardPayInfo = null;
		Currency payCurrency = null;
		if( order.getOrderPrice().getOnlinePayType() != null)
		{
			if (OrderService.ONLINE_PAY_TYPE_NONE != order.getOrderPrice().getOnlinePayType()) 
			{
				if (OrderService.ONLINE_PAY_TYPE_CREDIT_CART == order.getOrderPrice().getOnlinePayType()) 
				{
					creditCardPayInfo = orderPayService.getOrderPayInfo(orderNo);
				}
				payCurrency = systemService.getCurrency(order.getOrderPrice().getOnlinePayCurrencyId());
			}
		}
		PageList<OrderItem> orderItemPageList = orderService.getOrderDetails(currentPage, pageSize, order.getId(),OrderService.OrderDetailType.VALID);
		String shippingComment = orderService.getOrderShippingComment(order.getId());
		if (shippingComment != null && shippingComment.length() > 0) {
			showShippingComment = true;
		}
		if (orderItemPageList != null) {
			orderItemList = orderItemPageList.getData();
			orderVO.setOrderItemList(orderItemList);
			page = orderItemPageList.getPage();
		}
		if (orderStatusId == pendingStatus || orderStatusId == OrderService.ORDER_STATUS_AWAITING_LOCK_STOCK) {
			orderVO.setShowCancel(true);
		}
		if (orderStatusId == shippedStatus) {
			orderVO.setShowComplete(true);
		}
		if (orderStatusId == shippedStatus || orderStatusId == completedStatus) {
			trackOrderPackageShow = true;
		}		
		CashCoupon cashCoupon = null;
		if( order.getOrderPrice().getCashCouponPay().compareTo(BigDecimal.ZERO) > 0 && order.getId() != null )
		{
			cashCoupon = cashCouponService.getCashCouponByOrderId(order.getId());
		}		
		List<OrderProgressBarItem> progressBarItemList = null;
		progressBarItemList = orderService.getOrderProgressBar(orderNo); 
		request.setAttribute("completedStatus", completedStatus);//已完成的状态(Integer型)
		request.setAttribute("orderVO", orderVO);				 //页面要显示的订单VO对象
		request.setAttribute("orderNo", orderNo);				 //要显示的订单编号
		request.setAttribute("creditCardPayInfo", creditCardPayInfo);//支付信息
		request.setAttribute("payCurrency", payCurrency);			//支付货币
		request.setAttribute("orderStatusId", orderStatusId);	 //订单状态
		request.setAttribute("progressBarItemList", progressBarItemList);//订单完成的进度条
		request.setAttribute("trackOrderPackageShow", trackOrderPackageShow);
		request.setAttribute("itemPageList", page);
		request.setAttribute("shippingComment", shippingComment);
		request.setAttribute("showShippingComment", showShippingComment);	
		request.setAttribute("cashCoupon", cashCoupon);			//使用的现金券
		request.setAttribute("payPalEmail",payPalEmail);		//PayPalEmail
		request.setAttribute("orderNoList",orderNoList);		//订单编号集合
		request.setAttribute("deliveryType", deliveryType);
		return SUCCESS;
	}
	/**
	 * 取消PayPal订单
	 * @throws IOException
	 */
	public void cancelOrder() throws IOException {
		String orderNo = param("orderId", "");
		String payPalEmail = param("payPalEmail");
		int result = 0;
		String fm = "[{\"result\":%1$s}]";
		if(payPalEmail==null || orderNo == "")
			result = -1;
		else{
			Customer customer =customerService.getCustomerAccountByPayPal(payPalEmail);
			if (customer!=null&&orderNo!="") {
				try {
					orderService.cancelOrderByCustomer(customer.getId(),orderService.getOrderByOrderNo(orderNo).getId());
					result = 1;
				} catch (Exception e) {
					errorLog(e);
				}
			}
		}
		returnJson(String.format(fm, result));
	}

	/**
	 * 完成PayPal订单
	 * @throws IOException
	 */
	public void completeOrder() throws IOException {
		String orderNo = param("orderId", "");
		String payPalEmail = param("payPalEmail");
		String fm = "[{\"result\":%1$s}]";
		int result = 0;
		if(payPalEmail==null || orderNo == "")
			result = -1;
		else{			
			Customer customer =customerService.getCustomerAccountByPayPal(payPalEmail);
			if (customer!=null&&orderNo!="") {
				try {
					orderService.completeOrderByCustomer(customer.getId(),orderService.getOrderByOrderNo(orderNo).getId());
					result = 1;
				} catch (Exception e) {
					errorLog(e);
				}
			}
		}
		returnJson(String.format(fm, result));
	}
	/**
	 * 确认p3账号与PayPal关联
	 */
	public String affirmRelate(){
		String uuid = param("id");
		try {
			customerService.activePaypalCustomer(uuid);
		} catch (BussinessException e) {
			setMessageInfo("fail");
		}
		return SUCCESS;
	}
	/**
	 * 去跟踪paypal订单页面
	 * @return
	 */
	public String doTrackPayPalOrder(){
		Customer user = getLoginedUserBuyer();
		if(user!=null)
			request.setAttribute("user",user);
		return SUCCESS;
	}
}

package com.itecheasy.ph3.web.buyer.order;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.itecheasy.common.Page;
import com.itecheasy.common.PageList;
import com.itecheasy.ph3.SearchOrder;
import com.itecheasy.ph3.customer.Customer;
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
import com.itecheasy.ph3.order.OrderStatus;
import com.itecheasy.ph3.order.OrderService.OrderSearchCriteria;
import com.itecheasy.ph3.order.OrderService.OrderSearchOrder;
import com.itecheasy.ph3.system.Currency;
import com.itecheasy.ph3.system.DeliveryType;
import com.itecheasy.ph3.system.ShippingService;
import com.itecheasy.ph3.system.SystemService;
import com.itecheasy.ph3.web.BuyerBaseAction;
import com.itecheasy.ph3.web.exception.AppException;
import com.itecheasy.ph3.web.utils.WebUtils;
import com.itecheasy.ph3.web.vo.OrderVO;
import com.itecheasy.sslplugin.annotation.Secured;

@Secured
public class BuyerOrderManagerAction extends BuyerBaseAction {
	private static final long serialVersionUID = 9881666666L;
	private static final int ORDERITEM_SIZE = 6;

	private static final String NAV_AllOrders = "AllOrders";
	private static final String NAV_ActiveOrders = "ActiveOrders";
	private static final String NAV_CanceledOrders = "CanceledOrders";
	private static final String NAV_CompletedOrders = "CompletedOrders";

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
	 * 订单详细
	 * 
	 * @return
	 * @throws AppException
	 */
	public String doOrderDetails() throws AppException {
		Integer orderId = paramInt("orderId", null);
		String orderNo = null;
		Integer customerId = this.getLoginedUserBuyer().getId();

		Order order = null;
		if (orderId != null) {
			if (orderId > 0) {
				order = orderService.getOrderByCustomer(customerId, orderId);
			}
		} else {
			orderNo = param("orderNo");
			if (orderNo != null || !orderNo.isEmpty()) {
				order = orderService.getOrderByCustomer(customerId, orderNo
						.trim());
			}
		}
		if (order == null) {
			throw new AppException("Cannot find the order(Customer Id:"
					+ customerId + ",Order ID:" + orderId + ").");
		}
		Integer orderStatusId = order.getOrderStatus().getId();

		Page page = null;
		List<OrderItem> orderItemList = null;
		int completedStatus = OrderService.ORDER_STATUS_COMPLETED;
		int shippedStatus = OrderService.ORDER_STATUS_SHIPPED;
		int pendingStatus = OrderService.ORDER_STATUS_PENDING_CONFIRMATION;
		List<OrderProgressBarItem> progressBarItemList = null;
		boolean trackOrderPackageShow = false;
		boolean showShippingComment = false;
		Integer pageSize = 100;

		OrderSendInfo orderSendInfo = orderForCMSAndDMSService
				.getSendInfo(orderId);
		OrderVO orderVO = new OrderVO();
		orderVO.setOrder(order);
		orderVO.setOrderSendInfo(orderSendInfo);
		DeliveryType deliveryType = shippingService.getDeliveryTypeByDeliveryId(order.getDeliveryId());
		orderVO.setDelivery(shippingService.getDelivery(order.getDeliveryId()));
		orderNo = order.getOrderNo();

		CreditCardPayInfo creditCardPayInfo = null;
		Currency payCurrency = null;

		if( order.getOrderPrice().getOnlinePayType() != null)
		{
			if (OrderService.ONLINE_PAY_TYPE_NONE != order.getOrderPrice().getOnlinePayType()) 
			{
				if (OrderService.ONLINE_PAY_TYPE_CREDIT_CART == order.getOrderPrice().getOnlinePayType()) 
				{
					creditCardPayInfo = orderPayService.getOrderPayInfo(order.getOrderNo());
				}
				payCurrency = systemService.getCurrency(order.getOrderPrice().getOnlinePayCurrencyId());
			}
		}

		PageList<OrderItem> orderItemPageList = orderService.getOrderDetails(currentPage, pageSize, orderId,OrderService.OrderDetailType.VALID);
		String shippingComment = orderService.getOrderShippingComment(orderId);
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
		if( order.getOrderPrice().getCashCouponPay().compareTo(BigDecimal.ZERO) > 0 && orderId != null )
		{
			cashCoupon = cashCouponService.getCashCouponByOrderId(orderId);
		}
		
		progressBarItemList = orderService.getOrderProgressBar(orderNo);

		request.setAttribute("completedStatus", completedStatus);
		request.setAttribute("orderVO", orderVO);
		request.setAttribute("orderNo", orderNo);
		request.setAttribute("creditCardPayInfo", creditCardPayInfo);
		request.setAttribute("payCurrency", payCurrency);
		request.setAttribute("orderStatusId", orderStatusId);
		request.setAttribute("progressBarItemList", progressBarItemList);
		request.setAttribute("trackOrderPackageShow", trackOrderPackageShow);
		request.setAttribute("itemPageList", page);
		request.setAttribute("shippingComment", shippingComment);
		request.setAttribute("showShippingComment", showShippingComment);
		request.setAttribute("cashCoupon", cashCoupon);
		request.setAttribute("deliveryType", deliveryType);
		if (OrderService.ORDER_STATUS_CANCELED == orderStatusId) {
			setNavigation(NAV_CanceledOrders);
		} else if (OrderService.ORDER_STATUS_COMPLETED == orderStatusId) {
			setNavigation(NAV_CompletedOrders);
		} else {
			setNavigation(NAV_ActiveOrders);
		}

		return SUCCESS;
	}

	/**
	 * 查询订单
	 * 
	 * @throws IOException
	 */
	public String doOrderList() {
		Integer active = paramInt("active", 0);
		Integer tp = paramInt("tp", 0);
		String orderNo = param("orderId");
		Integer status = paramInt("status", -1);
		Date beginDate = paramDate("beginDate");
		Date endDate = paramDate("endDate");

		Page page = null;
		List<Order> orderList = null;
		OrderStatus orderStatus = null;
		List<OrderVO> OrderVOList = null;
		int canceled = OrderService.ORDER_STATUS_CANCELED;
		int completed = OrderService.ORDER_STATUS_COMPLETED;
		int shipped = OrderService.ORDER_STATUS_SHIPPED;
		int preparing = OrderService.ORDER_STATUS_PREPARING;
		int pending = OrderService.ORDER_STATUS_PENDING_CONFIRMATION;
		int awating_shipment = OrderService.ORDER_STATUS_AWAITING_SHIPMENT;
		int packing = OrderService.ORDER_STATUS_PACKAGING;
		Integer customerId = this.getLoginedUserBuyer().getId();
		Map<OrderSearchCriteria, Object> searchCriteria = new HashMap<OrderSearchCriteria, Object>();
		List<SearchOrder<OrderSearchOrder>> searchOrder = new LinkedList<SearchOrder<OrderSearchOrder>>();
		List<OrderStatus> orderStatusList = new ArrayList<OrderStatus>();

		List<Integer> searchStatusIds = new LinkedList<Integer>();

		if (active == 2 || active == 3) {
			if (active == 2) {
				status = canceled;
			} else if (active == 3) {
				status = completed;
			} else {

			}
			orderStatus = orderService.getOrderStatus(status);
			if (orderStatus != null) {
				orderStatusList.add(orderStatus);
				searchStatusIds.add(orderStatus.getId());
			}
		} else if (active == 1) {
			// active orders
			orderStatusList = orderService.getOrderStatuses();
			if (orderStatusList != null && !orderStatusList.isEmpty()) {
				for (int i = orderStatusList.size() - 1; i > -1; i--) {
					OrderStatus item = orderStatusList.get(i);
					if (item.getId().equals(completed)
							|| item.getId().equals(canceled)) {
						orderStatusList.remove(i);
					}
				}
				if (status >= 0) {
					orderStatus = orderService.getOrderStatus(status);
					if (orderStatus != null) {
						searchStatusIds.add(orderStatus.getId());
					}
				} else {
					for (OrderStatus item : orderStatusList) {
						searchStatusIds.add(item.getId());
					}
				}
			}
		} else {
			orderStatusList = orderService.getOrderStatuses();

			if (orderStatusList != null && !orderStatusList.isEmpty()) {
				if (status >= 0) {
					orderStatus = orderService.getOrderStatus(status);
					if (orderStatus != null) {
						searchStatusIds.add(orderStatus.getId());
					}
				} else {
					for (OrderStatus item : orderStatusList) {
						searchStatusIds.add(item.getId());
					}
				}
			}
		}

		if (beginDate != null && endDate != null) {
			if (beginDate.after(endDate)) {
				// 交换开始时间跟结束时间
				Date tempDate = beginDate;
				beginDate = endDate;
				endDate = tempDate;
			}
		}
		searchCriteria.put(OrderSearchCriteria.CUSTOMER_ID, customerId);
		if (orderNo != null) {
			orderNo = orderNo.trim();
			if (!orderNo.isEmpty()) {
				searchCriteria.put(OrderSearchCriteria.ORDER_NO, orderNo);
			} else {
				searchCriteria.put(OrderSearchCriteria.ORDER_STATUS,
						searchStatusIds);
				if (beginDate != null) {
					searchCriteria.put(OrderSearchCriteria.ORDER_DATE_BEGIN,
							beginDate);
				}
				if (endDate != null) {
					searchCriteria.put(OrderSearchCriteria.ORDER_DATE_END,
							WebUtils.getLongDateTime(endDate));
				}
			}
		}

		searchOrder.add(new SearchOrder<OrderSearchOrder>(
				OrderSearchOrder.ORDER_DATE, false));
		PageList<Order> list = orderService.searchOrders(currentPage,
				PAGE_SIZE, searchCriteria, searchOrder);
		int showInfo = 0;
		Integer statusInteger;
		if (list != null) {
			orderList = list.getData();
			page = list.getPage();
			if (orderList != null) {
				OrderVOList = new ArrayList<OrderVO>();
				OrderVO orderVO;
				PageList<OrderItem> orderItemPageList;
				for (Order order : orderList) {
					orderVO = new OrderVO();
					orderVO.setOrder(order);
					statusInteger = order.getOrderStatus().getId();
					orderItemPageList = orderService.getOrderDetails(1,ORDERITEM_SIZE, order.getId(),OrderService.OrderDetailType.VALID);
					if (orderItemPageList != null) {
						orderVO.setOrderItemList(orderItemPageList.getData());
						if (orderItemPageList.getPage().getTotalRowCount() > ORDERITEM_SIZE) {
							orderVO.setShowMore(true);
						}
						if (statusInteger.equals(pending) || statusInteger==8) {
							orderVO.setShowCancel(true);
						} else if (statusInteger.equals(shipped)) {
							orderVO.setShowComplete(true);
						}
					}
					OrderVOList.add(orderVO);
				}
				if (OrderVOList.size() <= 0) {
					if (tp == 0) {
						showInfo = 1;
					}
					if (tp == 1) {
						showInfo = 2;
					}
				}

			}
		}

		request.setAttribute("beginDate", beginDate);
		request.setAttribute("endDate", endDate);
		request.setAttribute("showInfo", showInfo);
		request.setAttribute("orderPageList", page);
		request.setAttribute("orderList", OrderVOList);
		request.setAttribute("orderStatusList", orderStatusList);

		request.setAttribute("canceled", canceled);
		request.setAttribute("preparing", preparing);
		request.setAttribute("completed", completed);
		request.setAttribute("awating_shipment", awating_shipment);
		request.setAttribute("packing", packing);
		request.setAttribute("shipped", shipped);
		request.setAttribute("pending", pending);

		if (1 == active) {
			setNavigation(NAV_ActiveOrders);
		} else if (2 == active) {
			setNavigation(NAV_CanceledOrders);
		} else if (3 == active) {
			setNavigation(NAV_CompletedOrders);
		} else {
			setNavigation(NAV_AllOrders);
		}
		return SUCCESS;
	}

	/**
	 * 取消订单
	 * 
	 * @throws IOException
	 */
	public void cancelOrder() throws IOException {
		Integer orderId = paramInt("orderId", 0);
		Customer customer = this.getLoginedUserBuyer();
		String fm = "[{\"result\":%1$s}]";
		int result = 0;
		if (customer == null) {
			result = -1;
		} else {
			if (orderId > 0) {
				try {
					orderService.cancelOrderByCustomer(customer.getId(),
							orderId);
					result = 1;
				} catch (Exception e) {
					errorLog(e);
				}
			}
		}
		returnJson(String.format(fm, result));
	}

	/**
	 * 完成订单
	 * 
	 * @throws IOException
	 */
	public void completeOrder() throws IOException {
		Integer orderId = paramInt("orderId", 0);
		Customer customer = this.getLoginedUserBuyer();
		String fm = "[{\"result\":%1$s}]";
		int result = 0;
		if (customer == null) {
			result = -1;
		} else {
			if (orderId > 0) {
				try {
					orderService.completeOrderByCustomer(customer.getId(),
							orderId);
					result = 1;
				} catch (Exception e) {
					errorLog(e);
				}
			}
		}
		returnJson(String.format(fm, result));
	}

	private void setNavigation(String navKey) {
		request.setAttribute("NavHover", navKey);
	}
}

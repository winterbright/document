package com.itecheasy.ph3.web.vo;

import java.util.List;

import com.itecheasy.ph3.order.Order;
import com.itecheasy.ph3.order.OrderItem;
import com.itecheasy.ph3.order.OrderSendInfo;
import com.itecheasy.ph3.system.Delivery;

public class OrderVO {
	private Order order;
	private Delivery delivery;
	private OrderSendInfo orderSendInfo;
	private List<OrderItem> orderItemList;
	private String deliveryName;
	private boolean showMore;
	private boolean showCancel;
	private boolean showComplete;

	
	public List<OrderItem> getOrderItemList() {
		return orderItemList;
	}

	public void setOrderItemList(List<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public String getDeliveryName() {
		return deliveryName;
	}

	public void setDeliveryName(String deliveryName) {
		this.deliveryName = deliveryName;
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public OrderSendInfo getOrderSendInfo() {
		return orderSendInfo;
	}

	public void setOrderSendInfo(OrderSendInfo orderSendInfo) {
		this.orderSendInfo = orderSendInfo;
	}

	public boolean getShowMore() {
		return showMore;
	}

	public void setShowMore(boolean showMore) {
		this.showMore = showMore;
	}
	public boolean getShowCancel() {
		return showCancel;
	}

	public void setShowCancel(boolean showCancel) {
		this.showCancel = showCancel;
	}

	public boolean getShowComplete() {
		return showComplete;
	}

	public void setShowComplete(boolean showComplete) {
		this.showComplete = showComplete;
	}

	
}

package com.itecheasy.ph3.web.admin.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.category.CategoryService;
import com.itecheasy.ph3.category.ShowCategory;
import com.itecheasy.ph3.order.OrderForCMSAndDMSService;
import com.itecheasy.ph3.order.Order;
import com.itecheasy.ph3.order.OrderShippingAddress;
import com.itecheasy.ph3.order.OrderCustomsInvoice;
import com.itecheasy.ph3.order.OrderCustomsInvoiceItem;
import com.itecheasy.ph3.order.OrderItem;
import com.itecheasy.ph3.order.OrderService;
import com.itecheasy.ph3.web.AdminBaseAction;
import com.itecheasy.ph3.web.exception.AppException;
import com.itecheasy.ph3.web.utils.SessionUtils;

public class OrderCustomsInvoiceAction extends AdminBaseAction {

	private static final long serialVersionUID = 1378379404323168971L;
	private OrderForCMSAndDMSService orderForCMSAndDMSService;
	private OrderService orderService;
	private CategoryService categoryService;
	private Integer orderId;
	private OrderCustomsInvoice  orderCustomsInvoice;
	private Order order;
	/**
	 * 一百
	 */
	private static final BigDecimal HUNDERED =  new BigDecimal("100");
	/**
	 * 发票
	 * @return
	 */
	public String doInvoice() throws AppException{
		order = orderService.getOrder(orderId);
		
		if(order == null ) throw new AppException("订单不存在");
		
		Integer status = order.getOrderStatus().getId();

		// 检查订单状态，等待确认、等待出库、出库中、打包中、等待发货时
		if (!(OrderService.ORDER_STATUS_PENDING_CONFIRMATION == status
				|| OrderService.ORDER_STATUS_AWAITING_PREPARING == status
				|| OrderService.ORDER_STATUS_PREPARING == status
				|| OrderService.ORDER_STATUS_PACKAGING == status || OrderService.ORDER_STATUS_AWAITING_SHIPMENT == status)) {
			
			throw new AppException("订单状态已改变，操作失败！");
		}
		if(!order.getIsDoneInvoice()){ //未做发票
			setDefultInvoice();
		}else{
			orderCustomsInvoice = orderForCMSAndDMSService.getOrderCustomsInvoice(orderId);
			sort(orderCustomsInvoice.getItems());//排序
		}
		return SUCCESS;
	}
	
	
	/**
	 * 制作发票
	 * @return
	 */
	public String makeInvoice(){
		seInvoiceData(orderCustomsInvoice.getProductCost(),false);
		try {
			orderForCMSAndDMSService.makeCustomsInvoice(SessionUtils.getLoginedAdminUser(request).getId(), orderCustomsInvoice);
		} catch (BussinessException e) {
			this.setMessageInfo(e.getErrorMessage());
		}
		return SUCCESS;
	}
	

	/**
	 * 自动制作发票
	 * @return
	 */
	public String autoMakeInvoice(){
		BigDecimal customsPrice = new BigDecimal(param("customsPrice","0")); //报关金额
		order = orderService.getOrder(orderCustomsInvoice.getOrderId());
		if(!order.getIsDoneInvoice()){ //未做发票
			//按ProductCost 设置比例
			seInvoiceData(orderCustomsInvoice.getProductCost(),false);
		}
		seInvoiceData(customsPrice,true);
		try {
			this.orderForCMSAndDMSService.makeCustomsInvoice(SessionUtils.getLoginedAdminUser().getId(), orderCustomsInvoice);
		} catch (BussinessException e) {
			this.setMessageInfo(e.getErrorMessage());
			return INPUT;
		}
		return SUCCESS;
	}
	
	
	/*
	 * 设置默认发票
	 */
	private void setDefultInvoice(){
		
		orderCustomsInvoice = new OrderCustomsInvoice();
		List<OrderItem> orderItems =orderService.getOrderDetails(orderId,OrderService.OrderDetailType.VALID);
		//发票信息
		OrderShippingAddress orderAddress = order.getShippingAddress();
		orderCustomsInvoice.setOrderId(orderId);
		orderCustomsInvoice.setReceiver(orderAddress.getFullName());//收件人
		String phoneFax  = orderAddress.getPhone()==null ? orderAddress.getFax() :  orderAddress.getFax()==null ? 
					orderAddress.getPhone():orderAddress.getPhone()+"/" + orderAddress.getFax() ;
		orderCustomsInvoice.setPhoneFax(phoneFax); //电话传真
		//地址
		String address = orderAddress.getStreet2() == null ? orderAddress.getStreet1() : orderAddress.getStreet1()+", " + orderAddress.getStreet2();
		address += ","+orderAddress.getCity()+", "+orderAddress.getState()+", ZIP:"+orderAddress.getZip();    
		orderCustomsInvoice.setAddress(address);
		orderCustomsInvoice.setCity(orderAddress.getCity());
		orderCustomsInvoice.setCountry(orderAddress.getCountry().getName());
		orderCustomsInvoice.setCreateDate(new Date());
		
		List<OrderCustomsInvoiceItem> items = new ArrayList<OrderCustomsInvoiceItem>();
		OrderCustomsInvoiceItem invoiceItem;
		//按类别分组
		Map<Integer, List<OrderItem>> mapOrderItems = groupOrderItem(orderItems);
		BigDecimal productCost = new BigDecimal(0);
		BigDecimal orderDiscount = order.getOrderPrice().getOrderDiscount();
		for (Map.Entry<Integer, List<OrderItem>> entry : mapOrderItems.entrySet()) {
			invoiceItem = new OrderCustomsInvoiceItem();
			String name = categoryService.getCategoryCustomsInvoiceName(entry.getKey());
			if (name == null) {
				ShowCategory showCategory = categoryService.getShowCategory(entry.getKey());
				name = showCategory != null ? showCategory.getName() : null;
			}
			invoiceItem.setInvoiceItemName(name);
			invoiceItem.setQuantity(entry.getValue().size());
			BigDecimal singlePrice = new BigDecimal(0);
			for(OrderItem orderItem : entry.getValue()){
				singlePrice = singlePrice.add(orderItem.getTotalSalePrice());//购买价格
			}
			if(orderDiscount.compareTo(BigDecimal.ZERO)!=0){
				singlePrice = singlePrice.multiply((HUNDERED.subtract(orderDiscount)).divide(HUNDERED,6,BigDecimal.ROUND_UP));
			}
			singlePrice = singlePrice.divide(new BigDecimal(invoiceItem.getQuantity()),6,BigDecimal.ROUND_UP);
			invoiceItem.setSinglePrice(singlePrice); //购买价格
			items.add(invoiceItem);
			productCost = productCost.add(invoiceItem.getSinglePrice().multiply(new BigDecimal(invoiceItem.getQuantity())));
		}
		sort(items);//排序
		orderCustomsInvoice.setItems(items);
		orderCustomsInvoice.setProductCost(productCost);
		orderCustomsInvoice.setShippingCost(order.getOrderPrice().getShippingCost());
		orderCustomsInvoice.setGrandTotal(orderCustomsInvoice.getProductCost().add(orderCustomsInvoice.getShippingCost()));
	}
	
	/*
	 * 排序
	 */
	protected void sort(List<OrderCustomsInvoiceItem> items){
		Collections.sort(items,new Comparator<OrderCustomsInvoiceItem>(){
			@Override
			public int compare(OrderCustomsInvoiceItem o1, OrderCustomsInvoiceItem o2) {
				if(o1==null || o2== null) return 0;
				if(!o1.equals(o2)) return 0;
				return o1.getInvoiceItemName().compareTo(o2.getInvoiceItemName());
			}
		});
	}
	
	/*
	 * 按类别Id分组
	 */
	protected Map<Integer, List<OrderItem>> groupOrderItem(List<OrderItem> orderItems){
		Map<Integer, List<OrderItem>> map= new HashMap<Integer, List<OrderItem>>();
		if(orderItems == null) return map;
		for (final OrderItem orderItem : orderItems) {
			if(map.containsKey(orderItem.getSecondCategoryId())){
				List<OrderItem> orderItemList = map.get(orderItem.getSecondCategoryId());
				orderItemList.add(orderItem);
				map.put(orderItem.getSecondCategoryId(), orderItemList);
			}else{
				map.put(orderItem.getSecondCategoryId(),  new ArrayList<OrderItem>(){
					private static final long serialVersionUID = 1221548872329L;
					{
						this.add(orderItem);
					}
				});
			}
			
		}
		return map;
	}
	
	/*
	 * 每个值占总金额比例
	 */
	protected  BigDecimal autoRatio(BigDecimal value,BigDecimal total){
		 return value.divide(total,8,BigDecimal.ROUND_HALF_UP).multiply(HUNDERED).setScale(6,BigDecimal.ROUND_HALF_UP);
	}
	/*
	 * 设置发票数据
	 */
	protected void seInvoiceData(BigDecimal total,boolean isSinglePrice){
		BigDecimal currentRatio = BigDecimal.ZERO; //当前的比例
		int i = 1;
		BigDecimal productCost = BigDecimal.ZERO;  
		for (OrderCustomsInvoiceItem item : orderCustomsInvoice.getItems()) {
			//最后一个的比例 = 100 - 当前比例
			if (i==orderCustomsInvoice.getItems().size()) {
				item.setPriceRatio(HUNDERED.subtract(currentRatio));
			}
			if(item.getPriceRatio()==null){
				item.setPriceRatio(autoRatio(item.getSinglePrice().multiply(BigDecimal.valueOf(item.getQuantity())),total));
			}
			currentRatio = currentRatio.add(item.getPriceRatio());
			if(isSinglePrice){
				item.setSinglePrice(total.multiply(item.getPriceRatio()).divide(HUNDERED,6,BigDecimal.ROUND_UP).divide(new BigDecimal(item.getQuantity()),6,BigDecimal.ROUND_UP));
			}
			productCost = productCost.add(item.getSinglePrice().multiply(new BigDecimal(item.getQuantity())));
			i++;
		}
		orderCustomsInvoice.setProductCost(productCost);
		orderCustomsInvoice.setGrandTotal(orderCustomsInvoice.getProductCost().add(orderCustomsInvoice.getShippingCost()));
	}
	
	
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	

	public void setOrderForCMSAndDMSService(OrderForCMSAndDMSService orderForCMSAndDMSService) {
		this.orderForCMSAndDMSService = orderForCMSAndDMSService;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	public OrderCustomsInvoice getOrderCustomsInvoice() {
		return orderCustomsInvoice;
	}
	public void setOrderCustomsInvoice(OrderCustomsInvoice orderCustomsInvoice) {
		this.orderCustomsInvoice = orderCustomsInvoice;
	}
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	
}

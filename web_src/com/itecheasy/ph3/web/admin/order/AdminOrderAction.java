package com.itecheasy.ph3.web.admin.order;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.itecheasy.common.PageList;
import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.adminuser.User;
import com.itecheasy.ph3.common.Utilities;
import com.itecheasy.ph3.customer.CashAccountService;
import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.customer.CustomerService;
import com.itecheasy.ph3.order.CashCoupon;
import com.itecheasy.ph3.order.CashCouponService;
import com.itecheasy.ph3.order.CreditCardPayInfo;
import com.itecheasy.ph3.order.Order;
import com.itecheasy.ph3.order.OrderCustomsInvoice;
import com.itecheasy.ph3.order.OrderForCMSAndDMSService;
import com.itecheasy.ph3.order.OrderItem;
import com.itecheasy.ph3.order.OrderPayService;
import com.itecheasy.ph3.order.OrderSendInfo;
import com.itecheasy.ph3.order.OrderService;
import com.itecheasy.ph3.order.OrderStatus;
import com.itecheasy.ph3.order.OrderService.OrderSearchCriteria;
import com.itecheasy.ph3.product.ProductImage;
import com.itecheasy.ph3.product.ProductService;
import com.itecheasy.ph3.system.Currency;
import com.itecheasy.ph3.system.Delivery;
import com.itecheasy.ph3.system.DictionaryService;
import com.itecheasy.ph3.system.Note;
import com.itecheasy.ph3.system.NoteService;
import com.itecheasy.ph3.system.ShippingService;
import com.itecheasy.ph3.system.SystemService;
import com.itecheasy.ph3.web.AdminBaseAction;
import com.itecheasy.ph3.web.exception.AppException;
import com.itecheasy.ph3.web.tag.ProductPictureFuncitonUtils;
import com.itecheasy.ph3.web.utils.ConfigHelper;
import com.itecheasy.ph3.web.utils.ExportHelper;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.utils.StrUtils;
import com.itecheasy.ph3.web.utils.WebUtils;
import com.itecheasy.ph3.web.vo.OrderVO;

public class AdminOrderAction extends AdminBaseAction {
	private static final long serialVersionUID = 820101218L;

	private OrderService orderService;
	private ShippingService shippingService;
	private CustomerService customerService;
	private CashAccountService cashAccountService;
	private OrderForCMSAndDMSService orderForCMSAndDMSService;
	private DictionaryService dictionaryService;
	private OrderPayService orderPayService;
	private SystemService systemService;
	private CashCouponService cashCouponService;
	private NoteService noteService;
	private ProductService productService;

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public void setCashCouponService(CashCouponService cashCouponService) {
		this.cashCouponService = cashCouponService;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public void setShippingService(ShippingService shippingService) {
		this.shippingService = shippingService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public void setCashAccountService(CashAccountService cashAccountService) {
		this.cashAccountService = cashAccountService;
	}

	public void setOrderForCMSAndDMSService(
			OrderForCMSAndDMSService orderForCMSAndDMSService) {
		this.orderForCMSAndDMSService = orderForCMSAndDMSService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setOrderPayService(OrderPayService orderPayService) {
		this.orderPayService = orderPayService;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	public void setNoteService(NoteService noteService) {
		this.noteService = noteService;
	}

	public String doOrderList() {
		String commandName = param("commandName");
		if (commandName != null
				&& commandName.compareToIgnoreCase("MergeOrders") == 0) {
			doMergeOrders();
		}

		String orderNo = param("orderNo");
		String receiverName = param("receiver");
		String productCode = param("pCode");
		String productName = param("pName");
		int hasProblem = paramInt("problem", -1);
		int isDoneInvoice = paramInt("doneInv", -1);
		int allowSend = paramInt("send", -1);
		int orderStatusId = paramInt("status", -1);

		Map<OrderSearchCriteria, Object> searchCriteria = new HashMap<OrderSearchCriteria, Object>();
		if (!isNullOrEmpty(orderNo)) {
			searchCriteria.put(OrderService.OrderSearchCriteria.ORDER_NO,
					orderNo.trim());
		} else {
			if (!isNullOrEmpty(receiverName)) {
				searchCriteria.put(
						OrderService.OrderSearchCriteria.RECEIVER_NAME,
						receiverName.trim());
			}
			if (!isNullOrEmpty(productCode)) {
				searchCriteria.put(
						OrderService.OrderSearchCriteria.PRODUCT_CODE,
						productCode.trim());
			}
			if (!isNullOrEmpty(productName)) {
				searchCriteria.put(
						OrderService.OrderSearchCriteria.PRODUCT_NAME,
						productName.trim());
			}
			if (hasProblem > -1) {
				searchCriteria.put(
						OrderService.OrderSearchCriteria.HAS_PROBLEM,
						hasProblem == 1);
			}
			if (isDoneInvoice > -1) {
				searchCriteria.put(
						OrderService.OrderSearchCriteria.IS_DONE_INVOICE,
						isDoneInvoice == 1);
			}
			if (allowSend > -1) {
				searchCriteria.put(OrderService.OrderSearchCriteria.ALLOW_SEND,
						allowSend == 1);
			}
			if (orderStatusId > -1) {
				List<Integer> statusList = new LinkedList<Integer>();
				statusList.add(orderStatusId);
				searchCriteria.put(
						OrderService.OrderSearchCriteria.ORDER_STATUS,
						statusList);
			}
			if (orderStatusId == -1) {
				List<Integer> statusList = new LinkedList<Integer>();
				statusList.add(8);
				statusList.add(10);
				statusList.add(20);
				statusList.add(30);
				statusList.add(40);
				statusList.add(50);
				statusList.add(60);
				statusList.add(100);
				searchCriteria.put(
						OrderService.OrderSearchCriteria.ORDER_STATUS,
						statusList);
			}
		}

		Integer pageIndex = getCurrentPage();
		if (pageIndex == null || pageIndex < 1)
			pageIndex = 1;

		PageList<Order> orderPageList = orderService.searchOrders(pageIndex,
				20, searchCriteria, null);
		List<OrderVO> orderList = new LinkedList<OrderVO>();
		OrderVO vo;
		for (Order order : orderPageList.getData()) {
			vo = new OrderVO();
			vo.setOrder(order);
			vo.setDeliveryName(getDeliveryName(order.getDeliveryId()));
			orderList.add(vo);
		}

		request.setAttribute("page", orderPageList.getPage());
		request.setAttribute("orderList", orderList);
		request
				.setAttribute("orderStatusList", orderService
						.getOrderStatuses());
		return SUCCESS;
	}

	public String doOrderShipmentList() {
		String orderNo = param("orderNo");
		String receiverName = param("receiver");
		Date sendDateStart = paramDate("sDate");
		Date sendDateEnd = paramDate("eDate");
		int allowSend = paramInt("send", -1);
		int orderStatusId = paramInt("status", -1);
		int countryId = paramInt("country", 0);
		boolean isSearchByNo = !isNullOrEmpty(orderNo);
		Map<OrderSearchCriteria, Object> searchCriteria = new HashMap<OrderSearchCriteria, Object>();
		if (isSearchByNo) {
			searchCriteria.put(OrderService.OrderSearchCriteria.ORDER_NO,
					orderNo.trim());
		} else {
			if (sendDateStart != null) {
				searchCriteria
						.put(
								OrderService.OrderSearchCriteria.SEND_SHIPPING_DEPT_DATE_BEGIN,
								sendDateStart);
			}
			if (sendDateEnd != null) {
				searchCriteria
						.put(
								OrderService.OrderSearchCriteria.SEND_SHIPPING_DEPT_DATE_END,
								WebUtils.getLongDateTime(sendDateEnd));
			}
			if (!isNullOrEmpty(receiverName)) {
				searchCriteria.put(
						OrderService.OrderSearchCriteria.RECEIVER_NAME,
						receiverName.trim());
			}
			if (countryId > 0) {
				searchCriteria.put(
						OrderService.OrderSearchCriteria.RECEIVER_COUNTRY_ID,
						countryId);
			}
			if (allowSend > -1) {
				searchCriteria.put(OrderService.OrderSearchCriteria.ALLOW_SEND,
						allowSend == 1);
			}

		}

		List<Integer> statusList = new LinkedList<Integer>();
		if (!isSearchByNo
				&& orderStatusId > -1
				&& containOrderStatus(orderStatusId,
						OrderService.ORDER_STATUS_PACKAGING,
						OrderService.ORDER_STATUS_AWAITING_SHIPMENT)) {
			statusList.add(orderStatusId);
		} else {
			statusList.add(OrderService.ORDER_STATUS_PACKAGING);
			statusList.add(OrderService.ORDER_STATUS_AWAITING_SHIPMENT);
		}
		searchCriteria.put(OrderService.OrderSearchCriteria.ORDER_STATUS,
				statusList);

		PageList<Order> orderPageList = orderService.searchOrders(
				getCurrentPage(), 20, searchCriteria, null);
		List<OrderVO> orderList = new LinkedList<OrderVO>();
		OrderVO vo;
		for (Order order : orderPageList.getData()) {
			vo = new OrderVO();
			vo.setOrder(order);
			vo.setDeliveryName(getDeliveryName(order.getDeliveryId()));
			orderList.add(vo);
		}
		List<OrderStatus> orderStatusList = new LinkedList<OrderStatus>();
		orderStatusList.add(orderService
				.getOrderStatus(OrderService.ORDER_STATUS_PACKAGING));
		orderStatusList.add(orderService
				.getOrderStatus(OrderService.ORDER_STATUS_AWAITING_SHIPMENT));

		request.setAttribute("page", orderPageList.getPage());
		request.setAttribute("orderList", orderList);
		request.setAttribute("orderStatusList", orderStatusList);
		request
				.setAttribute("countryList", dictionaryService
						.getAllCountries());
		return SUCCESS;
	}

	/**
	 * 订单发送
	 * 
	 * @throws AppException
	 */
	public String doOrderSender() throws AppException {
		Integer orderId = paramInt("orderId", null);

		Order order = orderId > 0 ? orderService.getOrder(orderId) : null;
		if (order == null) {
			throw new AppException("找不到订单。");
		}
		OrderVO orderVO = new OrderVO();
		orderVO.setOrder(order);
		orderVO.setDeliveryName(shippingService.getDelivery(
				order.getDeliveryId()).getName());
		List<OrderItem> orderItemList = orderService.getOrderDetails(orderId,
				OrderService.OrderDetailType.VALID);
		orderVO.setOrderItemList(orderItemList);
		String sendRemind = orderService.getOrderSendRemind(orderId);

		request.setAttribute("orderVO", orderVO);
		request.setAttribute("sendRemind", sendRemind);
		request.setAttribute("analysisBarCodeURL", ConfigHelper.ANALYSIS_BARCODE_URL);
		return SUCCESS;
	}

	/**
	 * 合并订单
	 * 
	 * 
	 */
	private Integer doMergeOrders() {
		String mergeOrderIds = param("mergeOrderIds");
		if (Utilities.isEmpty(mergeOrderIds)) {
			messageInfo = "请选择两个或多个订单进行合并操作!";
			return null;
		}

		String[] orderIds = mergeOrderIds.split(",");
		if (orderIds.length < 2) {
			messageInfo = "请选择两个或多个订单进行合并操作!";
			return null;
		}

		List<Integer> orderIdList = new ArrayList<Integer>();
		for (String id : orderIds) {
			orderIdList.add(Integer.valueOf(id));
		}

		Integer userId = getLoginUserAdmin().getId();
		Integer newOrderId = null;
		try {
			newOrderId = orderService.mergeOrders(orderIdList, userId);
			if (newOrderId != null) {
				messageInfo = SUCCESS;
			}
		} catch (BussinessException e) {
			String error = e.getErrorMessage();
			if (OrderService.ERROR_MERGE_ORDER_DATA_INVALID == error) {
				messageInfo = "合并的订单不存在或已删除!";
			} else if (OrderService.ERROR_MERGE_ORDER_DATA_ILLEGAL == error) {
				messageInfo = "只有同一客户的，相同订单状态的，且订单状态为等待确认、等待出库、出库中、打包中的订单才允许进行合并操作!";
			} else if (OrderService.ERROR_MERGE_ORDER_HAS_QUESTION == error) {
				messageInfo = "”有问题“订单不允许合并!";
			} else {
				messageInfo = error;
			}
		}

		return newOrderId;
	}
	/**
	 * 获取非订单商品信息
	 */
	public void unOrderProductInfo() {
		Integer productId = paramInt("productId", null);
		com.itecheasy.ph3.product.Product product = productService.getProduct(productId);
		if(product != null){
			List<ProductImage> imageUrlList = productService
					.getProductImages(productId);
			String imageUrl = null;
			if (imageUrlList != null && !imageUrlList.isEmpty()) {
				imageUrl = imageUrlList.get(0).getImageUrl();
			}
			String photoUrl = ProductPictureFuncitonUtils.getPhotoUrl(imageUrl,
					155, 155);
			try {
				returnJson("{\"photoUrl\":\"" + photoUrl
						+ "\"}");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				returnJson("{\"photoUrl\":\"error\"}");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * 订单明细。
	 */
	public String doOrderDetail() throws AppException {
		return doOrderDetail(false);
	}

	/**
	 * 订单明细。没有任何操作
	 */
	public String doOrderDetailView() throws AppException {
		return doOrderDetail(true);
	}

	private String doOrderDetail(boolean isViewOnly) throws AppException {
		int orderId = paramInt("id", 0);

		Order order = orderId > 0 ? orderService.getOrder(orderId) : null;
		if (order == null) {
			throw new AppException("找不到订单。");
		}
		PageList<OrderItem> orderItemPageList = orderService.getOrderDetails(
				currentPage, 100, orderId, OrderService.OrderDetailType.VALID);

		Customer customer = customerService.getCustomer(order.getCustomerId());
		BigDecimal balance = cashAccountService.getBalance(order.getCustomerId());
		Delivery delivery = shippingService.getDelivery(order.getDeliveryId());
		String sendRemind = orderService.getOrderSendRemind(orderId);
		String shippingComment = orderService.getOrderShippingComment(orderId);
		// 获取客户备注和订单备注
		User user = SessionUtils.getLoginedAdminUser(request);

		Note orderNote = noteService.getNote(null, NoteService.NoteType.ORDER,String.valueOf(orderId));
		Note customerNote = noteService.getNote(null,NoteService.NoteType.CUSTERMER, String.valueOf(order.getCustomerId()));

		CreditCardPayInfo creditCardPayInfo = null;
		Currency payCurrency = null;

		if (order.getOrderPrice() != null
				&& order.getOrderPrice().getOnlinePayType() != null) {
			if (OrderService.ONLINE_PAY_TYPE_NONE != order.getOrderPrice()
					.getOnlinePayType()) {
				if (OrderService.ONLINE_PAY_TYPE_CREDIT_CART == order
						.getOrderPrice().getOnlinePayType()) {
					creditCardPayInfo = orderPayService.getOrderPayInfo(order
							.getOrderNo());
				}
				payCurrency = systemService.getCurrency(order.getOrderPrice()
						.getOnlinePayCurrencyId());
			}
		}

		OrderSendInfo orderSendInfo = orderForCMSAndDMSService.getSendInfo(orderId);
		Integer orderStatusId = order.getOrderStatus().getId();
		boolean showUpdateSendRemindButton = false;
		if (!isViewOnly) {
			boolean showSubscribeInsufficientButton = (order.getHasProblem()&& containOrderStatus(orderStatusId,
					OrderService.ORDER_STATUS_PENDING_CONFIRMATION));
			
			boolean showCancelOrderButton = containOrderStatus(orderStatusId,
					OrderService.ORDER_STATUS_AWAITING_LOCK_STOCK,
					OrderService.ORDER_STATUS_PENDING_CONFIRMATION,
					OrderService.ORDER_STATUS_AWAITING_PREPARING,
					OrderService.ORDER_STATUS_PREPARING,
					OrderService.ORDER_STATUS_PACKAGING);
			
			boolean showConfirmOrderButton = !showSubscribeInsufficientButton && containOrderStatus(orderStatusId,
					OrderService.ORDER_STATUS_PENDING_CONFIRMATION);
			
			boolean showStockoutOrderButton = (order.getHasProblem() && containOrderStatus(
					orderStatusId, OrderService.ORDER_STATUS_PACKAGING));
		
			boolean showAllowSendButton = !showSubscribeInsufficientButton && containOrderStatus(orderStatusId,
					OrderService.ORDER_STATUS_PENDING_CONFIRMATION,
					OrderService.ORDER_STATUS_AWAITING_PREPARING,
					OrderService.ORDER_STATUS_PREPARING,
					OrderService.ORDER_STATUS_PACKAGING);
			
			boolean showCompletedButton = containOrderStatus(orderStatusId,
					OrderService.ORDER_STATUS_SHIPPED);
			
			boolean showMakeInvoiceButton = !showSubscribeInsufficientButton && containOrderStatus(orderStatusId,
					OrderService.ORDER_STATUS_PENDING_CONFIRMATION,
					OrderService.ORDER_STATUS_AWAITING_PREPARING,
					OrderService.ORDER_STATUS_PREPARING,
					OrderService.ORDER_STATUS_PACKAGING,
					OrderService.ORDER_STATUS_AWAITING_SHIPMENT);
			
			boolean showExportInvoiceButton = !showSubscribeInsufficientButton && !containOrderStatus(
					orderStatusId, OrderService.ORDER_STATUS_AWAITING_LOCK_STOCK,OrderService.ORDER_STATUS_CANCELED);

			// 添加一个按钮（更新订单备注、更新客户备注）
			boolean showUpdateOrderNoteButton = showCancelOrderButton;

			showUpdateSendRemindButton = showCancelOrderButton;

			CashCoupon cashCoupon = null;
			if (order.getOrderPrice().getCashCouponPay().compareTo(
					BigDecimal.ZERO) > 0) {
				cashCoupon = cashCouponService.getCashCouponByOrderId(orderId);
			}
			String inputPath = null;
			if (order.getIsDoneInvoice()) {
				inputPath = orderForCMSAndDMSService
						.getOrderInvoiceAddress(orderId);
			}

			request
					.setAttribute("showCancelOrderButton",
							showCancelOrderButton);
			request.setAttribute("showConfirmOrderButton",
					showConfirmOrderButton);
			request.setAttribute("showStockoutOrderButton",
					showStockoutOrderButton);
			request.setAttribute("showSubscribeInsufficientButton", showSubscribeInsufficientButton);
			request.setAttribute("showAllowSendButton", showAllowSendButton);
			request.setAttribute("showCompletedButton", showCompletedButton);
			request
					.setAttribute("showMakeInvoiceButton",
							showMakeInvoiceButton);
			request.setAttribute("showExportInvoiceButton",
					showExportInvoiceButton);
			request.setAttribute("showUpdateOrderNoteButton",
					showUpdateOrderNoteButton);
			request.setAttribute("cashCoupon", cashCoupon);
			request.setAttribute("inputPath", inputPath);
		}

		boolean showTrackInfo = containOrderStatus(orderStatusId,
				OrderService.ORDER_STATUS_SHIPPED,
				OrderService.ORDER_STATUS_COMPLETED);

		request.setAttribute("order", order);
		request.setAttribute("orderItemList", orderItemPageList.getData());
		request.setAttribute("orderItemPage", orderItemPageList.getPage());
		request.setAttribute("customer", customer);
		request.setAttribute("balance", balance);
		request.setAttribute("delivery", delivery);
		request.setAttribute("shippingComment", shippingComment);
		request.setAttribute("sendRemind", sendRemind);
		request.setAttribute("orderNote", orderNote);
		request.setAttribute("customerNote", customerNote);
		request
				.setAttribute("showSendRemindButton",
						showUpdateSendRemindButton);
		request.setAttribute("showTrackInfo", showTrackInfo);
		request.setAttribute("orderSendInfo", orderSendInfo);
		request.setAttribute("creditCardPayInfo", creditCardPayInfo);
		request.setAttribute("payCurrency", payCurrency);
		request.setAttribute("viewOnly", isViewOnly);
		return SUCCESS;
	}

	/**
	 * 订单操作.AJAX请求
	 * 
	 * @return
	 */
	public void operateOrder() {
		int orderId = paramInt("orderId", 0);
		int operate = paramInt("operate", -1);
		String fm = "[{\"result\":%1$s}]";
		int result = 0;
		if (orderId > 0) {
			Integer userId = getLoginUserAdmin().getId();
			if (operate == 0) {// 取消订单
				try {
					orderService.cancelOrderByCustomerService(userId, orderId);
					result = 1;
				} catch (BussinessException e) {
					e.printStackTrace();
				} catch (Exception e) {
					result = -1;
					e.printStackTrace();
				}
			} else if (operate == 1) {// 确认订单
				try {
					orderService.confirmOrderByCustomerService(userId, orderId);
					result = 1;
				} catch (BussinessException e) {
					e.printStackTrace();
				} catch (Exception e) {
					result = -1;
					e.printStackTrace();
				}
			} else if (operate == 2) {// 缺货处理
				try {
					orderService.disposeStockout(userId, orderId);
					result = 1;
				} catch (BussinessException e) {
					String errMsg = e.getErrorMessage();
					if (errMsg.equals("ERROR_HAS_NO_EFFECTIVE_ITEM")) {
						// 客户订单中没有到货商品
						result = 2;
					} else if (errMsg.equals("ERROR_OPERATE_FAIL")) {

					}
					e.printStackTrace();
				} catch (Exception e) {
					result = -1;
					e.printStackTrace();
				}
			} else if (operate == 3) {// 完成订单
				try {
					orderService
							.completeOrderByCustomerService(userId, orderId);
					result = 1;
				} catch (BussinessException e) {
					e.printStackTrace();
				} catch (Exception e) {
					result = -1;
					e.printStackTrace();
				}
			} else if (operate == 4) {// 允许发运
				try {
					orderService.setOrderStatusOfAllowSend(orderId);
					result = 1;
				} catch (BussinessException e) {
					e.printStackTrace();
				} catch (Exception e) {
					result = -1;
					e.printStackTrace();
				}
			} else if (operate == 5) {// 不允许发运
				try {
					orderService.setOrderStatusOfCannotSend(orderId);
					result = 1;
				} catch (BussinessException e) {
					e.printStackTrace();
				} catch (Exception e) {
					result = -1;
					e.printStackTrace();
				}
			} else if(operate == 6){	//预定不足处理
				try {
					orderService.disposePartBook(userId, orderId);
					result = 1;
				} catch (BussinessException e) {
					e.printStackTrace();
				} catch (Exception e) {
					result = -1;
					e.printStackTrace();
				}
			}
		}
		try {
			returnJson(String.format(fm, result));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新货运备注.AJAX请求
	 */
	public void updateSendRemind() {
		int orderId = paramInt("orderId", 0);
		String remind = param("remind");
		String fm = "[{\"result\":%1$s}]";
		int result = 0;
		if (orderId > 0) {
			try {
				if (remind != null) {
					remind = remind.trim();
				}
				orderService.updateOrderSendRemind(orderId, remind);
				result = 1;
			} catch (BussinessException e) {
			} catch (Exception e) {
				result = -1;
				e.printStackTrace();
			}
		}
		try {
			returnJson(String.format(fm, result));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 更新订单备注或客户备注
	 */
	public void updateOrderOrCustomerNote(){
		Integer id=paramInt("id",null);
		String content=param("content");
		String key=param("key");
		Integer type=paramInt("type",0);
		
		Note note=new Note();
		note.setId(id);
		note.setType(type);
		
		String fm = "[{\"result\":%1$s,\"noteId\":\"%2$s\"}]";
		int result=0;
		int noteId=0;
		if(!StringUtils.isEmpty(key)){
			try{
				note.setKey(key);
				if(content!=null && StringUtils.isNotEmpty(content)){
					content=content.trim();
					note.setContent(content);
				}
				noteId=noteService.writeNote(note);
				result=1;
			}catch(Exception e){
				result=-1;
				e.printStackTrace();
			}
		}
		try {
			returnJson(String.format(fm, result,noteId));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	/**
	 * 发往货运部
	 */
	public void sendToShipDept() {
		String orderIds = param("orderIds");
		BigDecimal orderWeight = new BigDecimal(param("orderWeight"));
		String remark = param("remark");
		String fm = "[{\"result\":%1$s}]";
		int result = 0;
		if (orderIds != null && !orderIds.isEmpty()) {
			String[] idArr = orderIds.split(",");
			Integer userId = getLoginUserAdmin().getId();
			int completedQty = 0;
			for (String sId : idArr) {
				int orderId = StrUtils.tryParseInt(sId, 0);
				if (orderId > 0) {
					try {
						orderForCMSAndDMSService.sendToShippingDepartment(
								userId, orderId, orderWeight, remark);
						completedQty++;
					} catch (BussinessException e) {
						e.printStackTrace();
					} catch (Exception e) {
						result = -1;
						e.printStackTrace();
					}
				}
			}
			if (completedQty > 0) {
				if (completedQty < idArr.length) {
					// 部分提交成功
					result = 2;
				} else {
					result = 1;
				}
			}
		}
		try {
			returnJson(String.format(fm, result));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exportOrder() {
		int orderId = paramInt("orderId", 0);
		if (orderId == 0) {
			return;
		}
		Order order = orderService.getOrder(orderId);
		// 订单已取消
		if (OrderService.ORDER_STATUS_CANCELED == order.getOrderStatus()
				.getId()) {
			String alert = "<script>alert(\"Order has been canceled,The operation failed！\"); window.close(); </script>";
			try {
				// response.setCharacterEncoding("utf-8");
				response.getWriter().print(alert);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		List<OrderItem> orderItemList = orderService.getOrderDetails(order
				.getId(), OrderService.OrderDetailType.VALID);
		Delivery delivery = shippingService.getDelivery(order.getDeliveryId());
		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("items", orderItemList);
		beans.put("order", order);
		beans.put("delivery", delivery);
		// 格式日期
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		beans.put("dateFormat", dateFormat);

		ExportHelper.exportExcel(response,
				"mailtemplate/templates/order_invoice.xls", order.getOrderNo()
						+ ".xls", beans);
	}

	public void exportOrderCustomsInvoice() {
		String setDate = param("date");
		String fileName = param("fileName");

		if (StringUtils.isEmpty(setDate) || StringUtils.isEmpty(fileName)) {
			ExportHelper.alterInfo(response, "输入的地址无效！");
			return;
		}

		String invoiceAddress = setDate + "/" + fileName + ".xls";
		OrderCustomsInvoice customsInvoice = orderForCMSAndDMSService
				.getOrderCustomsInvoice(invoiceAddress);
		if (customsInvoice == null) {
			ExportHelper.alterInfo(response, "无法找到该文件，请确认该文件是否存在！");
			return;
		}

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("orderCustomsInvoice", customsInvoice);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		beans.put("dateFormat", dateFormat);
		
		Order order = orderService.getOrder(customsInvoice.getOrderId());
		beans.put("orderno", order != null ? order.getOrderNo() : "");
		// 导出Excel
		ExportHelper.exportExcel(response,
				"mailtemplate/templates/order_customs_invoice.xls", fileName
						+ ".xls", beans);
	}

	private String getDeliveryName(int deliveryId) {
		Delivery delivery = shippingService.getDelivery(deliveryId);
		if (delivery == null) {
			return null;
		} else {
			return delivery.getName();
		}
	}

	private static boolean isNullOrEmpty(String s) {
		if (s == null || s.trim().length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 订单状态集合中是否包含某个状态
	 */
	private static boolean containOrderStatus(Integer orderStatusId,
			Integer... statuses) {
		for (Integer id : statuses) {
			if (id.equals(orderStatusId)) {
				return true;
			}
		}
		return false;
	}

}

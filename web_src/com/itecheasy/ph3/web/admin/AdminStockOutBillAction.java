package com.itecheasy.ph3.web.admin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.itecheasy.common.PageList;
import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.SearchOrder;
import com.itecheasy.ph3.order.Order;
import com.itecheasy.ph3.order.OrderForCMSAndDMSService;
import com.itecheasy.ph3.order.OrderService;
import com.itecheasy.ph3.order.StockOutBill;
import com.itecheasy.ph3.order.StockOutBillItem;
import com.itecheasy.ph3.order.OrderForCMSAndDMSService.StockOutBillSearchCriteria;
import com.itecheasy.ph3.order.OrderForCMSAndDMSService.StockOutBillSearchOrder;
import com.itecheasy.ph3.web.AdminBaseAction;
import com.itecheasy.ph3.web.exception.AppException;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.utils.UrlHelper;
import com.itecheasy.ph3.web.vo.StockOutBillVO;

/**
 * 出库单管理
 */
public class AdminStockOutBillAction extends AdminBaseAction {

	private static final long serialVersionUID = 889954231354L;

	private OrderForCMSAndDMSService orderForCMSAndDMSService;

	private Integer id;
	private String stockOutNo;
	private String orderNo;
	private Integer completeDate;
	private Integer status;
	private Integer warehouseId;
	private String productCode;
	private String refererUrl;
	private StockOutBill stockOutBill;
	private OrderService orderService;

	private List<StockOutBillItem> stockOutBillDetails;

	/**
	 * 出库单列表
	 * 
	 * @return
	 */
	public String doStockOutBillList() {
		List<SearchOrder<StockOutBillSearchOrder>> searchOrder = new ArrayList<SearchOrder<StockOutBillSearchOrder>>();
		SearchOrder<StockOutBillSearchOrder> so = new SearchOrder<StockOutBillSearchOrder>(StockOutBillSearchOrder.ORDER_DATE, false);
		searchOrder.add(so);
		Map<StockOutBillSearchCriteria, Object> searchCriteria = new HashMap<StockOutBillSearchCriteria, Object>();
		if (StringUtils.isNotEmpty(stockOutNo)) 
		{
			searchCriteria.put(StockOutBillSearchCriteria.STOCK_OUT_BILL_NO,stockOutNo);
		} 
		else 
		{
			if (StringUtils.isNotEmpty(orderNo)) 
			{
				searchCriteria.put(StockOutBillSearchCriteria.ORDER_NO, orderNo);
			}
			if (StringUtils.isNotEmpty(productCode)) {
				searchCriteria.put(StockOutBillSearchCriteria.PRODUCT_CODE,productCode);
			}
			if (status != null && status > 0) {
				searchCriteria.put(StockOutBillSearchCriteria.STOCK_OUT_STATUS,status);
			}
			if (warehouseId != null &&warehouseId > 0) {
				searchCriteria.put(StockOutBillSearchCriteria.WAREHOUSE_NUMBER,warehouseId);
			}
			if (completeDate != null && completeDate > 0) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.DAY_OF_MONTH, -completeDate);
				searchCriteria.put(StockOutBillSearchCriteria.BEGIN_COMPLETE_STOCK_OUT_BILL_DATE,calendar.getTime());
				calendar.setTime(new Date());
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				searchCriteria.put(StockOutBillSearchCriteria.END_COMPLETE_STOCK_OUT_BILL_DATE,calendar.getTime());
			}
		}
		if (currentPage == null)
			currentPage = 1;
		
		PageList<StockOutBill>  stockOutBillList= orderForCMSAndDMSService.searchStockOutBills(currentPage,PAGE_SIZE, searchCriteria, searchOrder);
		List<StockOutBillVO> stockOutBillVOList = new ArrayList<StockOutBillVO>();
		StockOutBillVO stockOutBillVO = null;
		Order order = null;
		for (StockOutBill stockOutBill : stockOutBillList.getData()) {
			stockOutBillVO = new StockOutBillVO();
			stockOutBillVO.setStockOutBill(stockOutBill);
			
			order =  orderService.getOrder(stockOutBill.getOrderId());
			if( order != null)
			{
				stockOutBillVO.setOrderNo(order.getOrderNo());
			}
			stockOutBillVOList.add(stockOutBillVO);
		}
		
		request.setAttribute("stockOutBillList", stockOutBillVOList);
		request.setAttribute("page", stockOutBillList.getPage());

		return SUCCESS;
	}

	/**
	 * 出库单详细
	 * 
	 * @return
	 */
	public String doStockOutBillDetail() {
		stockOutBill = orderForCMSAndDMSService.getStockOutBill(id);
		stockOutBillDetails = orderForCMSAndDMSService
				.getStockOutBillDetails(id);
		if (StringUtils.isEmpty(refererUrl))
			refererUrl = UrlHelper.getRefererUrl(request);
		
		Order order = orderService.getOrder(stockOutBill.getOrderId());
		if(order != null)
		{
			setOrderNo(order.getOrderNo());
		}
		
		if (OrderForCMSAndDMSService.STOCK_OUT_BILL_STATUS_NOT_OUTBOUND != stockOutBill.getStatus()) 
		{
			return SUCCESS;
		}
		
		if (OrderService.ORDER_STATUS_AWAITING_PREPARING != order.getOrderStatus().getId()) {
			return SUCCESS;
		}
		try 
		{
			orderForCMSAndDMSService.prepareStockOut(SessionUtils.getLoginedAdminUser().getId(), id);
		} catch (BussinessException e) 
		{
			setMessageInfo(e.getErrorMessage());
			return "list";
		}
		
		
		return SUCCESS;
	}

	/**
	 * 保存出库单
	 * 
	 * @return
	 */
	public String saveOutStock() {
		Map<Integer, Integer> stockOutQtys = new HashMap<Integer, Integer>();
		if (stockOutBillDetails != null && stockOutBillDetails.size() > 0) {
			for (StockOutBillItem item : stockOutBillDetails) {
				if (item.getActualCheckOutQty() != null && item.getId() != null) {
					stockOutQtys.put(item.getId(), item.getActualCheckOutQty());
				}
			}
		}
		String remark = param("remark");
		Integer operatorId = SessionUtils.getLoginedAdminUser().getId();
		try {
			orderForCMSAndDMSService.updateStockOutInfo(operatorId, id,
					stockOutQtys, remark);
		} catch (BussinessException e) {
			setMessageInfo(e.getErrorMessage()); // 出库单已完成
			return SUCCESS;
		}
		setMessageInfo("SUCCESS_SAVE");
		return SUCCESS;
	}

	/**
	 * 完成出库单
	 * 
	 * @return
	 * @throws AppException
	 */
	public String completeOutStock() throws AppException {
		try {
			// 保存出库单
			saveOutStock();
			orderForCMSAndDMSService.completeStockOut(SessionUtils.getLoginedAdminUser().getId(), id);
		} catch (BussinessException e) {
			this.setMessageInfo(e.getErrorMessage());
			return SUCCESS;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new AppException(ex.getMessage());
		}
		setMessageInfo("SUCCESS_COMPLETE");
		return SUCCESS;
	}

	public String printStockOutBill() {
		stockOutBill = orderForCMSAndDMSService.getStockOutBill(id);
		stockOutBillDetails = orderForCMSAndDMSService.getStockOutBillDetails(id);
		
		Order order = orderService.getOrder(stockOutBill.getOrderId());
		if(order != null)
		{
			setOrderNo(order.getOrderNo());
		}
		
		request.setAttribute("printDate", new Date());
		return SUCCESS;
	}

	public void setOrderForCMSAndDMSService(
			OrderForCMSAndDMSService orderForCMSAndDMSService) {
		this.orderForCMSAndDMSService = orderForCMSAndDMSService;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStockOutNo() {
		return stockOutNo;
	}

	public void setStockOutNo(String stockOutNo) {
		this.stockOutNo = stockOutNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	
	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Integer getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(Integer completeDate) {
		this.completeDate = completeDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public StockOutBill getStockOutBill() {
		return stockOutBill;
	}

	public void setStockOutBill(StockOutBill stockOutBill) {
		this.stockOutBill = stockOutBill;
	}

	public List<StockOutBillItem> getStockOutBillDetails() {
		return stockOutBillDetails;
	}

	public void setStockOutBillDetails(
			List<StockOutBillItem> stockOutBillDetails) {
		this.stockOutBillDetails = stockOutBillDetails;
	}

	public String getRefererUrl() {
		return refererUrl;
	}

	public void setRefererUrl(String refererUrl) {
		this.refererUrl = refererUrl;
	}

}

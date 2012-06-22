package com.itecheasy.ph3.web.admin;

import java.io.IOException;
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
import com.itecheasy.ph3.order.StockInBill;
import com.itecheasy.ph3.order.StockInBillItem;
import com.itecheasy.ph3.order.StockOutBill;
import com.itecheasy.ph3.order.OrderForCMSAndDMSService.StockInBillSearchCriteria;
import com.itecheasy.ph3.order.OrderForCMSAndDMSService.StockInBillSearchOrder;
import com.itecheasy.ph3.order.OrderForCMSAndDMSService.StockOutBillSearchCriteria;
import com.itecheasy.ph3.web.AdminBaseAction;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.vo.StockInBillVO;
import com.itecheasy.ph3.web.vo.StockOutBillVO;

public class AdminStockInBillAction extends AdminBaseAction {
	private static final long serialVersionUID = 9901012221888L;
	private OrderForCMSAndDMSService orderForCMSAndDMSService;
	private OrderService orderService;

	public void setOrderForCMSAndDMSService(OrderForCMSAndDMSService orderForCMSAndDMSService) {
		this.orderForCMSAndDMSService = orderForCMSAndDMSService;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	private Integer id;
	private String stockInNo;
	private String orderNo;
	private String productCode;
	private Integer completeDate;
	private Integer status;
	private String remark;
	private Integer warehouseId;
	private StockInBill stockInBill;
	private List<StockInBillItem> stockInBillItems;

	public String doStockInBillList() {
		Map<StockInBillSearchCriteria, Object> searchCriteria = new HashMap<StockInBillSearchCriteria, Object>();
		List<SearchOrder<StockInBillSearchOrder>> searchOrder = new ArrayList<SearchOrder<StockInBillSearchOrder>>();
		searchOrder.add(new SearchOrder<StockInBillSearchOrder>(StockInBillSearchOrder.ORDER_DATE, false));
		
		if (StringUtils.isNotEmpty(stockInNo)) 
		{
			searchCriteria.put(StockInBillSearchCriteria.STOCK_IN_BILL_NO,stockInNo);
		} 
		else 
		{
			if (StringUtils.isNotEmpty(orderNo)) 
			{
				searchCriteria.put(StockInBillSearchCriteria.ORDER_NO, orderNo);
			}
			if (StringUtils.isNotEmpty(productCode)) 
			{
				searchCriteria.put(StockInBillSearchCriteria.PRODUCT_CODE,productCode);
			}
			if (status != null && status > 0) 
			{
				searchCriteria.put(StockInBillSearchCriteria.STOCK_IN_STATUS,status);
			}
			if (warehouseId != null &&warehouseId > 0) {
				searchCriteria.put(StockInBillSearchCriteria.WAREHOUSE_NUMBER,warehouseId);
			}
			if (completeDate != null && completeDate > 0) 
			{
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.add(Calendar.DAY_OF_MONTH, -completeDate);
				searchCriteria.put(StockInBillSearchCriteria.BEGIN_COMPLETE_STOCK_IN_BILL_DATE,calendar.getTime());
				calendar.setTime(new Date());
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				searchCriteria.put(StockInBillSearchCriteria.END_COMPLETE_STOCK_IN_BILL_DATE,calendar.getTime());
			}
		}
		PageList<StockInBill>  stockInBillList = orderForCMSAndDMSService.searchStockInBills(currentPage, PAGE_SIZE, searchCriteria, searchOrder);
		
		List<StockInBillVO> stockInBillVOList = new ArrayList<StockInBillVO>();
		StockInBillVO stockInBillVO = null;
		Order order = null;
		StockOutBill stockOutBill = null;
		for (StockInBill stockInBill : stockInBillList.getData()) 
		{
			stockInBillVO = new StockInBillVO();
			stockInBillVO.setStockInBill(stockInBill);
			
			stockOutBill = orderForCMSAndDMSService.getStockOutBill(stockInBill.getStockOutId());
			if( stockOutBill != null)
			{
				order =  orderService.getOrder(stockOutBill.getOrderId());
				if( order != null)
				{
					stockInBillVO.setOrderNo(order.getOrderNo());
				}
				stockInBillVOList.add(stockInBillVO);
			}
		}
		
		request.setAttribute("stockInBillList", stockInBillVOList);
		request.setAttribute("page", stockInBillList.getPage());

		return SUCCESS;
	}

	public String doStockInBillDetails() {
		stockInBill = orderForCMSAndDMSService.getStockInBill(id);
		stockInBillItems = orderForCMSAndDMSService.getStockInBillDetails(id);
		
		StockOutBill stockOutBill = orderForCMSAndDMSService.getStockOutBill(stockInBill.getStockOutId());
		if( stockOutBill != null)
		{
			Order order =  orderService.getOrder(stockOutBill.getOrderId());
			if( order != null)
			{
				setOrderNo(order.getOrderNo());
			}
		}
		
		return SUCCESS;
	}

	public String saveStockInBill() {
		Integer customerId = SessionUtils.getLoginedAdminUser().getId();
		try {
			orderForCMSAndDMSService.updateStockInRemark(customerId, id, remark);
		} catch (BussinessException  e) {
			setMessageInfo(e.getErrorMessage());
			return SUCCESS; 
		}
		setMessageInfo("Success_Info");
		return SUCCESS;
	}

	public void completeStockIn() {
		String fm = "[{\"result\":%1$s}]";
		Integer customerId = SessionUtils.getLoginedAdminUser().getId();
		try {
			orderForCMSAndDMSService.updateStockInRemark(customerId, id, remark);
			orderForCMSAndDMSService.completeStockIn(customerId, id);
		} catch (BussinessException  e) {
			try {
				returnJson(String.format(fm, "\"BussinessException\""));
			} catch (IOException ex) {}
			
			return;
		}
		try {
			returnJson(String.format(fm, "\"SUCCESS\""));
		} catch (IOException ex) {}
	}
	
	public String printStockInBill(){
		stockInBill = orderForCMSAndDMSService.getStockInBill(id);
		stockInBillItems = orderForCMSAndDMSService.getStockInBillDetails(id);
		
		StockOutBill stockOutBill = orderForCMSAndDMSService.getStockOutBill(stockInBill.getStockOutId());
		if( stockOutBill != null)
		{
			Order order =  orderService.getOrder(stockOutBill.getOrderId());
			if( order != null)
			{
				setOrderNo(order.getOrderNo());
			}
		}
		
		request.setAttribute("printDate", new Date());
		return SUCCESS;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStockInNo() {
		return stockInNo;
	}

	public void setStockInNo(String stockInNo) {
		this.stockInNo = stockInNo;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
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

	public StockInBill getStockInBill() {
		return stockInBill;
	}

	public void setStockInBill(StockInBill stockInBill) {
		this.stockInBill = stockInBill;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public List<StockInBillItem> getStockInBillItems() {
		return stockInBillItems;
	}

	public void setStockInBillItems(List<StockInBillItem> stockInBillItems) {
		this.stockInBillItems = stockInBillItems;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
		
	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}
}

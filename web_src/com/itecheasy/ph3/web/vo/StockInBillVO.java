package com.itecheasy.ph3.web.vo;

import com.itecheasy.ph3.order.StockInBill;

public class StockInBillVO {
	private String orderNo;
	private StockInBill stockInBill;
	
	public StockInBill getStockInBill() {
		return stockInBill;
	}
	public void setStockInBill(StockInBill stockInBill) {
		this.stockInBill = stockInBill;
	}
	
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
}

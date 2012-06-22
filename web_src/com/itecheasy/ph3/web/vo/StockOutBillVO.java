package com.itecheasy.ph3.web.vo;

import com.itecheasy.ph3.order.StockOutBill;

public class StockOutBillVO {
	private String orderNo;
	private StockOutBill stockOutBill;
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public StockOutBill getStockOutBill() {
		return stockOutBill;
	}
	public void setStockOutBill(StockOutBill stockOutBill) {
		this.stockOutBill = stockOutBill;
	}
}

package com.itecheasy.ph3.web.vo;

import java.math.BigDecimal;

import com.itecheasy.ph3.system.Currency;

public class PaymentCurrencyVO {
	private Currency currency;
	private BigDecimal partPayAmount;
	private BigDecimal partPayAmountUS;
	private BigDecimal allPayAmount;
	private BigDecimal allPayAmountUS;
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	public BigDecimal getPartPayAmount() {
		return partPayAmount;
	}
	public void setPartPayAmount(BigDecimal partPayAmount) {
		this.partPayAmount = partPayAmount;
	}
	public BigDecimal getPartPayAmountUS() {
		return partPayAmountUS;
	}
	public void setPartPayAmountUS(BigDecimal partPayAmountUS) {
		this.partPayAmountUS = partPayAmountUS;
	}
	public BigDecimal getAllPayAmount() {
		return allPayAmount;
	}
	public void setAllPayAmount(BigDecimal allPayAmount) {
		this.allPayAmount = allPayAmount;
	}
	public BigDecimal getAllPayAmountUS() {
		return allPayAmountUS;
	}
	public void setAllPayAmountUS(BigDecimal allPayAmountUS) {
		this.allPayAmountUS = allPayAmountUS;
	}

}

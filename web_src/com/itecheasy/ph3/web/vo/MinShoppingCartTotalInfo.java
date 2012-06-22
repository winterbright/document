package com.itecheasy.ph3.web.vo;

import java.math.BigDecimal;

import com.itecheasy.ph3.order.CashCoupon;
import com.itecheasy.ph3.shopping.ShoppingCartTotal;
import com.itecheasy.ph3.system.DeliveryFreightRegion;
import com.itecheasy.ph3.system.DeliveryFreightRegions;

public class MinShoppingCartTotalInfo {

	private int productQty;
	//货物折后总金额
	private BigDecimal productPriceAfterDiscount;
	
	//订单重量
	private BigDecimal orderWeight;
	
	/**
	 * 使用的现金券信息
	 */
	private CashCoupon cashCouponInfo;
	
	//存储当前选择的货运方式
	private DeliveryFreightRegion currentyDeliveryFreightRegion;

	

	public int getProductQty() {
		return productQty;
	}

	public MinShoppingCartTotalInfo() {

	}
   
	public MinShoppingCartTotalInfo(int productQty, BigDecimal productPriceAfterDiscount, BigDecimal orderWeight,DeliveryFreightRegion currentyDeliveryFreightRegion) {
		this.productQty = productQty;
		this.productPriceAfterDiscount = productPriceAfterDiscount;
		this.currentyDeliveryFreightRegion = currentyDeliveryFreightRegion;
		this.orderWeight = orderWeight;
	}

	public void setProductQty(int productQty) {
		this.productQty = productQty;
	}
	
	
	
	
	/**
	 * 使用的现金券信息
	 */
	public CashCoupon getCashCouponInfo() 
	{
		return cashCouponInfo;
	}
	
	public BigDecimal getProductPriceAfterDiscount() {
		return productPriceAfterDiscount;
	}

	public void setProductPriceAfterDiscount(BigDecimal productPriceAfterDiscount) {
		this.productPriceAfterDiscount = productPriceAfterDiscount;
	}

	public DeliveryFreightRegion getCurrentyDeliveryFreightRegion() {
		return currentyDeliveryFreightRegion;
	}

	public void setCurrentyDeliveryFreightRegion(
			DeliveryFreightRegion currentyDeliveryFreightRegion) {
		this.currentyDeliveryFreightRegion = currentyDeliveryFreightRegion;
	}

	public void setCashCouponInfo(CashCoupon cashCouponInfo) 
	{
		this.cashCouponInfo = cashCouponInfo;
	}		
	
	/**
	 * 获得现金券可抵扣的金额（现金券只能抵扣货物金额）
	 * @return
	 */
	public BigDecimal getCashCouponRedemptionAmount()
	{
		CashCoupon cashCouponInfo = this.getCashCouponInfo();
		
		if( cashCouponInfo == null ) return BigDecimal.ZERO;
		
		return cashCouponInfo.getAmount().compareTo(this.getProductPriceAfterDiscount()) > 0 ? this.getProductPriceAfterDiscount() : cashCouponInfo.getAmount();
	}

	/**
	 * 货物总运费 = 基本运费 + 偏远运费
	 * @return
	 */
	public BigDecimal getTotalFreight() 
	{
		return( getCurrentyDeliveryFreightRegion() == null || getCurrentyDeliveryFreightRegion().getTotalFreight() == null) ? BigDecimal.ZERO : getCurrentyDeliveryFreightRegion().getTotalFreight();
	}
	
	/**
	 * 货物基本运费
	 * @return
	 */
	public BigDecimal getBaseFreight() 
	{
		return( getCurrentyDeliveryFreightRegion() == null || getCurrentyDeliveryFreightRegion().getBaseFreight() == null) ? BigDecimal.ZERO : getCurrentyDeliveryFreightRegion().getBaseFreight();
	}
	
	/**
	 * 偏远运费
	 * @return
	 */
	public BigDecimal getRemoteFreight() 
	{
		return( getCurrentyDeliveryFreightRegion() == null || getCurrentyDeliveryFreightRegion().getRemoteFreight() == null) ? BigDecimal.ZERO : getCurrentyDeliveryFreightRegion().getRemoteFreight();
	}
	
	/**
	 * 订单重量
	 * @return
	 */
	public BigDecimal getOrderWeigth() 
	{
		return orderWeight;
	}
	
	public void setOrderWeight(BigDecimal orderWeight) {
		this.orderWeight = orderWeight;
	}

	/**
	 * 订单总金额 = 货物折后金额 + 运费
	 */
	public BigDecimal getOrderAmount() 
	{
		return getProductPriceAfterDiscount().add(getTotalFreight());
	}
	
	/**
	 * 订单应付金额 = 货物折后金额 + 运费 - 现金券抵扣金额
	 */
	public BigDecimal getDueAmount() 
	{		
		return getOrderAmount().subtract(getCashCouponRedemptionAmount());
	}
	
}

package com.itecheasy.ph3.web.buyer.order;

import java.math.BigDecimal;

import com.itecheasy.ph3.customer.Address;
import com.itecheasy.ph3.order.CashCoupon;
import com.itecheasy.ph3.order.OrderPaymentInfo;
import com.itecheasy.ph3.web.vo.DeliveryVO;

public class SessionOrder {
	
	private static final BigDecimal HUNDRED = new BigDecimal("100");
	
	/**
	 * 正式订单号
	 */
	private String orderNo;
	
	/**
	 * 客户ID
	 */
	private Integer customerId;
	
	/**
	 * 购物车ID
	 */
	private Integer shoppingCartId;
	
	/**
	 * 临时订单ID
	 */
	private Integer tempOrderId;
	/**
	 * 货运地址
	 */
	private Address shippingAddress;

	/**
	 * 账单地址
	 */
	private Address billingAddress;

	/**
	 * 订单折扣
	 */
	private BigDecimal discount;
	/**
	 * 折前价格
	 */
	private BigDecimal productPriceBeforeDiscount;
	
	/**
	 * 商品总物理重量
	 */
	private BigDecimal totalWeight;
	
	/**
	 * 商品总体积
	 */
	private BigDecimal totalVolume;
	
	/**
	 * 订单重量
	 */
	private BigDecimal orderWeight;
	
	/**
	 * 货运方式
	 */
	private DeliveryVO delivery;
	
	/**
	 * 是否需要真实的海关发票
	 */
	private Boolean isRealInvoice;
	
	/**
	 * 支付相关信息
	 */
	private OrderPaymentInfo paymentInfo;
	
	/**
	 * 总数量
	 */
	private int totalProductQty;
	

	/**
	 * 发货提醒
	 */
	private String shippingComment;

	/**
	 * 是否已确定付款信息
	 */
	private boolean isConfirmPayInfo = false;

	public Address getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public Address getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	/**
	 * 货物折前总金额
	 * @return
	 */
	public BigDecimal getProductPriceBeforeDiscount() 
	{
		return productPriceBeforeDiscount;
	}

	public void setProductPriceBeforeDiscount(BigDecimal productPriceBeforeDiscount) {
		this.productPriceBeforeDiscount = productPriceBeforeDiscount;
	}

	/**
	 * 货物折后总金额
	 * @return
	 */
	public BigDecimal getProductPriceAfterDiscount() 
	{
		BigDecimal disCount = HUNDRED.subtract(getDiscount());
		if( disCount.compareTo(BigDecimal.ZERO) <= 0)
		{
			disCount = HUNDRED;
		}
		return productPriceBeforeDiscount.multiply(disCount).divide(HUNDRED,2,BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * 订单总运费 = 基本运费 + 偏远运费
	 * @return
	 */
	public BigDecimal getFreight() 
	{
		return( getDelivery() == null) ? BigDecimal.ZERO : getDelivery().getFreight();
	}
	
	/**
	 * 获得现金券可抵扣的金额（现金券只能抵扣货物金额）
	 * @return
	 */
	public BigDecimal getCashCouponRedemptionAmount()
	{
		if( this.getPaymentInfo() == null ) return BigDecimal.ZERO;
		
		CashCoupon cashCouponInfo = this.getPaymentInfo().getCashCouponInfo();
		if( cashCouponInfo == null || cashCouponInfo.getAmount() == null) return BigDecimal.ZERO;
		
		return cashCouponInfo.getAmount().compareTo(this.getProductPriceAfterDiscount()) > 0 ? this.getProductPriceAfterDiscount() : cashCouponInfo.getAmount();
	}
	
	/**
	 * 订单总金额 = 货物折后金额 + 运费
	 */
	public BigDecimal getOrderAmount() 
	{
		return getProductPriceAfterDiscount().add(getFreight());
	}
	
	/**
	 * 订单应付金额 = 货物折后金额 + 运费 - 现金券抵扣金额
	 */
	public BigDecimal getDueAmount() 
	{		
		return getOrderAmount().subtract(getCashCouponRedemptionAmount());
	}	
	

	/**
	 * 总共支付金额 =  Cash支付金额 + 现金券抵扣金额 + 在线支付金额 
	 */
	public BigDecimal getTotalPayAmount() 
	{
		OrderPaymentInfo orderPaymentInfo =  this.getPaymentInfo();
		if( orderPaymentInfo == null ) return BigDecimal.ZERO;
		
		return orderPaymentInfo.getOnLinePayUs().add(orderPaymentInfo.getCashPay()).add(getCashCouponRedemptionAmount());
	}
	
	/**
	 * 订单总节省金额 = 折前货物金额 - 折后货物金额
	 */
	public BigDecimal getProductPriceSaveAmount() 
	{
		return getProductPriceBeforeDiscount().subtract(getProductPriceAfterDiscount());
	}

	/**
	 * 订单基本运费
	 * @return
	 */
	public BigDecimal getBaseFreight() {
		if( getDelivery() == null) return BigDecimal.ZERO;
		return getDelivery().getBaseFreight();
	}

	/**
	 * 订单偏远运费
	 * @return
	 */
	public BigDecimal getRemoteFreight() {
		if( getDelivery() == null) return BigDecimal.ZERO;
		return getDelivery().getRemoteFreight();
	}

	/**
	 * 订单物理重量
	 * @return
	 */
	public BigDecimal getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(BigDecimal totalWeight) {
		this.totalWeight = totalWeight;
	}

	/**
	 * 订单总体积
	 * @return
	 */
	public BigDecimal getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(BigDecimal totalVolume) {
		this.totalVolume = totalVolume;
	}
	
	/**
	 * 订单总重量 = max(订单物理重量,订单体积重量=(sum(各商品体积 * 体积转换系数 * 购买批量)))
	 * @return
	 */
	public BigDecimal getOrderWeight() {
		return orderWeight;
	}

	public void setOrderWeight(BigDecimal orderWeight) {
		this.orderWeight = orderWeight;
	}

	/**
	 * 购买的商品总项数(即几种商品）
	 * @return
	 */
	public int getTotalProductQty() {
		return totalProductQty;
	}

	public void setTotalProductQty(int totalProductQty) {
		this.totalProductQty = totalProductQty;
	}

	/**
	 * 当前对应的临时订单ID
	 * @return
	 */
	public Integer getTempOrderId() {
		return tempOrderId;
	}

	public void setTempOrderId(Integer tempOrderId) {
		this.tempOrderId = tempOrderId;
	}

	/**
	 * 是否采用真实海关发票
	 * @return
	 */
	public Boolean getIsRealInvoice() {
		if( isRealInvoice == null)  return true;
		return isRealInvoice;
	}

	public void setIsRealInvoice(Boolean isRealInvoice) {		
		this.isRealInvoice = isRealInvoice;
	}

	/**
	 * 货运方式
	 * @return
	 */
	public DeliveryVO getDelivery() {
		return delivery;
	}

	public void setDelivery(DeliveryVO delivery) {
		this.delivery = delivery;
	}

	/**
	 * 货运提醒
	 * @return
	 */
	public String getShippingComment() {
		return shippingComment;
	}

	public void setShippingComment(String shippingComment) {
		this.shippingComment = shippingComment;
	}

	public boolean isConfirmPayInfo() {
		return isConfirmPayInfo;
	}

	public void setConfirmPayInfo(boolean isConfirmPayInfo) {
		this.isConfirmPayInfo = isConfirmPayInfo;
	}

	/**
	 * 订单号
	 * @return
	 */
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	/**
	 * 订单的所有付款信息
	 * @return
	 */
	public OrderPaymentInfo getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(OrderPaymentInfo paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	/**
	 * 客户ID
	 * @return
	 */
	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	/**
	 * 购物车ID
	 * @return
	 */
	public Integer getShoppingCartId() {
		return shoppingCartId;
	}

	public void setShoppingCartId(Integer shoppingCartId) {
		this.shoppingCartId = shoppingCartId;
	}
}

package com.itecheasy.ph3.web.vo;

import java.math.BigDecimal;

import com.itecheasy.ph3.system.Delivery;
import com.itecheasy.ph3.system.DeliveryType;

public class DeliveryVO {
	private Delivery baseInfo;
	private BigDecimal baseFreight;
	private BigDecimal remoteFreight;
	private DeliveryType deliveryType;
	private boolean isEnable = true;	

	public BigDecimal getFreight() {
		return getBaseFreight().add(getRemoteFreight());
	}

	public BigDecimal getRemoteFreight() {
		return ( remoteFreight == null) ? BigDecimal.ZERO : remoteFreight;
	}
	public void setRemoteFreight(BigDecimal remoteFreight) {
		this.remoteFreight = remoteFreight;
	}
	public Delivery getBaseInfo() {
		return baseInfo;
	}
	public void setBaseInfo(Delivery baseInfo) {
		this.baseInfo = baseInfo;
	}
	public boolean isEnable() {
		return isEnable;
	}
	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}
	public BigDecimal getBaseFreight() {
		return ( baseFreight == null) ? BigDecimal.ZERO : baseFreight;
	}
	public void setBaseFreight(BigDecimal baseFreight) {
		this.baseFreight = baseFreight;
	}

	public DeliveryType getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(DeliveryType deliveryType) {
		this.deliveryType = deliveryType;
	}

	
	
}

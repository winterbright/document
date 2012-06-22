package com.itecheasy.ph3.web.vo;

import com.itecheasy.ph3.system.DeliveryFreightRegion;
import com.itecheasy.ph3.system.DeliveryType;

public class DeliveryFreightRegionVO {
	private DeliveryFreightRegion deliveryFreightRegion;
	private DeliveryType deliveryType;
	private boolean isEnable = true;
	
	public DeliveryFreightRegion getDeliveryFreightRegion() {
		return deliveryFreightRegion;
	}
	public void setDeliveryFreightRegion(DeliveryFreightRegion deliveryFreightRegion) {
		this.deliveryFreightRegion = deliveryFreightRegion;
	}
	public boolean isEnable() {
		return isEnable;
	}
	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}
	public DeliveryType getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(DeliveryType deliveryType) {
		this.deliveryType = deliveryType;
	}
	
	

}

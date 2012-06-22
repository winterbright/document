package com.itecheasy.ph3.web.vo;

import java.math.BigDecimal;
import java.util.List;

import com.itecheasy.ph3.web.ListItem;

public class ProductSearchUrlParams {
	private String keyword;
	private Integer showCategoryId;
	private BigDecimal price1;
	private BigDecimal price2;
	private List<Integer> propertyValueIds;
	private List<Integer> propertyValueGroupIds;
	private List<ListItem<Integer, String>> urlPropertyIds;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer getShowCategoryId() {
		return showCategoryId;
	}

	public void setShowCategoryId(Integer showCategoryId) {
		this.showCategoryId = showCategoryId;
	}

	public List<Integer> getPropertyValueIds() {
		return propertyValueIds;
	}

	public void setPropertyValueIds(List<Integer> propertyValueIds) {
		this.propertyValueIds = propertyValueIds;
	}

	public List<Integer> getPropertyValueGroupIds() {
		return propertyValueGroupIds;
	}

	public void setPropertyValueGroupIds(List<Integer> propertyValueGroupIds) {
		this.propertyValueGroupIds = propertyValueGroupIds;
	}

	public int getPropertyCount() {
		int qty = 0;
		if (propertyValueIds != null) {
			qty += propertyValueIds.size();
		}
		if (propertyValueGroupIds != null) {
			qty += propertyValueGroupIds.size();
		}
		return qty;
	}

	public List<ListItem<Integer, String>> getUrlPropertyIds() {
		return urlPropertyIds;
	}

	public void setUrlPropertyIds(List<ListItem<Integer, String>> urlPropertyIds) {
		this.urlPropertyIds = urlPropertyIds;
	}

	public BigDecimal getPrice1() {
		return price1;
	}

	public void setPrice1(BigDecimal price1) {
		this.price1 = price1;
	}

	public BigDecimal getPrice2() {
		return price2;
	}

	public void setPrice2(BigDecimal price2) {
		this.price2 = price2;
	}

}

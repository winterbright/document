package com.itecheasy.ph3.web.vo;

import java.util.List;

import com.itecheasy.ph3.category.ShowCategory;

public class ShowCategoryTree {
	private ShowCategory category;
	private List<ShowCategoryTree> subCategories;
	private int productQty;

	public ShowCategory getCategory() {
		return category;
	}

	public void setCategory(ShowCategory category) {
		this.category = category;
	}

	public List<ShowCategoryTree> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<ShowCategoryTree> subCategories) {
		this.subCategories = subCategories;
	}

	public int getProductQty() {
		return productQty;
	}

	public void setProductQty(int productQty) {
		this.productQty = productQty;
	}

}

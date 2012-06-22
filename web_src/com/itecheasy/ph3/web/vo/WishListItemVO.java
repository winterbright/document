package com.itecheasy.ph3.web.vo;

import com.itecheasy.ph3.product.Product;
import com.itecheasy.ph3.product.ProductSalePrice;
import com.itecheasy.ph3.shopping.WishListItem;

public class WishListItemVO {
	private WishListItem item;
	private Product product;
	private ProductSalePrice ProductSalePrice;
	private String showCategoryName;

	public WishListItem getItem() {
		return item;
	}

	public void setItem(WishListItem item) {
		this.item = item;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public ProductSalePrice getProductSalePrice() {
		return ProductSalePrice;
	}

	public void setProductSalePrice(ProductSalePrice productSalePrice) {
		ProductSalePrice = productSalePrice;
	}

	public String getShowCategoryName() {
		return showCategoryName;
	}

	public void setShowCategoryName(String showCategoryName) {
		this.showCategoryName = showCategoryName;
	}

}

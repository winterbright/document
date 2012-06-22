package com.itecheasy.ph3.web.vo;

import java.math.BigDecimal;

import com.itecheasy.ph3.product.LunceneProduct;

public class LunceneProductVO {
	private LunceneProduct product;
	private String showCategoryName;
	/** 已添加到购物车的商品数量*/
	private int productQtyInShoppingCart;
	private boolean isAddToWishList;
	private boolean isPromotion;
	private BigDecimal discount;

	public boolean getIsPromotion() {
		return isPromotion;
	}

	public void setIsPromotion(boolean isPromotion) {
		this.isPromotion = isPromotion;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public LunceneProductVO() {

	}

	public LunceneProductVO(LunceneProduct product, String showCategoryName) {
		this.product = product;
		this.showCategoryName = showCategoryName;
	}

	public LunceneProduct getProduct() {
		return product;
	}

	public void setProduct(LunceneProduct product) {
		this.product = product;
	}

	public String getShowCategoryName() {
		return showCategoryName;
	}

	public void setShowCategoryName(String showCategoryName) {
		this.showCategoryName = showCategoryName;
	}

	public int getProductQtyInShoppingCart() {
		return productQtyInShoppingCart;
	}

	public void setProductQtyInShoppingCart(int productQtyInShoppingCart) {
		this.productQtyInShoppingCart = productQtyInShoppingCart;
	}

	public boolean getIsAddToWishList() {
		return isAddToWishList;
	}

	public void setIsAddToWishList(boolean isAddToWishList) {
		this.isAddToWishList = isAddToWishList;
	}

}

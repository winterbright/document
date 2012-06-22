package com.itecheasy.ph3.web.vo;

import java.math.BigDecimal;

import com.itecheasy.ph3.product.Product;
import com.itecheasy.ph3.product.ProductSalePrice;
import com.itecheasy.ph3.shopping.ShoppingCartItem;

public class ShoppingCartItemVO {
	private ShoppingCartItem item;
	private Product product;
	private ProductSalePrice productSalePrice;
	private String productCategoryName;
	private int itemStatus;

	public ShoppingCartItem getItem() {
		return item;
	}

	public void setItem(ShoppingCartItem item) {
		this.item = item;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public ProductSalePrice getProductSalePrice() {
		return productSalePrice;
	}

	public void setProductSalePrice(ProductSalePrice productSalePrice) {
		this.productSalePrice = productSalePrice;
	}

	public BigDecimal getItemSubTotalPrice() {
		return productSalePrice.getSalePriceAfterDiscount().multiply(
				new BigDecimal(item.getProductQty()));
	}

	public String getProductCategoryName() {
		return productCategoryName;
	}

	public void setProductCategoryName(String productCategoryName) {
		this.productCategoryName = productCategoryName;
	}

	public int getItemStatus() {
		return itemStatus;
	}

	public void setItemStatus(int itemStatus) {
		this.itemStatus = itemStatus;
	}

}

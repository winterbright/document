package com.itecheasy.ph3.web.vo;

import java.util.List;

import com.itecheasy.ph3.category.ShowCategory;
import com.itecheasy.ph3.product.LunceneProduct;
import com.itecheasy.ph3.product.Product;
import com.itecheasy.ph3.product.ProductImage;
import com.itecheasy.ph3.product.ProductSalePrice;
import com.itecheasy.ph3.product.ProductSpec;
import com.itecheasy.ph3.property.PropertyValue;

public class ProductVO {
	private Product product;
	private String description;
	private ProductSalePrice firstPrice;
	private ShowCategory showCategory;
	private String showCategoryName;
	private List<ProductImage> productImages;
	private List<ProductSpec> productSpecs;
	private List<PropertyValue> propertyValues;
	private Integer promoteAreaId;
	
	private Boolean isPromote;
	
	private Boolean isRecommend;


	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ProductSalePrice getFirstPrice() {
		return firstPrice;
	}

	public void setFirstPrice(ProductSalePrice firstPrice) {
		this.firstPrice = firstPrice;
	}

	public ShowCategory getShowCategory() {
		return showCategory;
	}

	public void setShowCategory(ShowCategory showCategory) {
		this.showCategory = showCategory;
	}

	public String getShowCategoryName() {
		return showCategoryName;
	}

	public void setShowCategoryName(String showCategoryName) {
		this.showCategoryName = showCategoryName;
	}

	public List<ProductImage> getProductImages() {
		return productImages;
	}

	public void setProductImages(List<ProductImage> productImages) {
		this.productImages = productImages;
	}

	public List<ProductSpec> getProductSpecs() {
		return productSpecs;
	}

	public void setProductSpecs(List<ProductSpec> productSpecs) {
		this.productSpecs = productSpecs;
	}

	public List<PropertyValue> getPropertyValues() {
		return propertyValues;
	}

	public void setPropertyValues(List<PropertyValue> propertyValues) {
		this.propertyValues = propertyValues;
	}

	public Integer getPromoteAreaId() {
		return promoteAreaId;
	}

	public void setPromoteAreaId(Integer promoteAreaId) {
		this.promoteAreaId = promoteAreaId;
	}

	public Boolean getIsPromote() {
		return isPromote;
	}

	public void setIsPromote(Boolean isPromote) {
		this.isPromote = isPromote;
	}

	public Boolean getIsRecommend() {
		return isRecommend;
	}

	public void setIsRecommend(Boolean isRecommend) {
		this.isRecommend = isRecommend;
	}


	
	

}

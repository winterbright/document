package com.itecheasy.ph3.web.vo;

public class ProductUrl {
	private String baseUrl;
	private String baseUrlOfFirstPage;
	private String pagerFormatUrl;
	private String sorterFormatUrl;
	private String showModeFormatUrl;
	private String categoryFormatUrl;
	private String propertyFormatUrl;

	public String getPagerFormatUrl() {
		return pagerFormatUrl;
	}

	public void setPagerFormatUrl(String pagerFormatUrl) {
		this.pagerFormatUrl = pagerFormatUrl;
	}

	public String getSorterFormatUrl() {
		return sorterFormatUrl;
	}

	public void setSorterFormatUrl(String sorterFormatUrl) {
		this.sorterFormatUrl = sorterFormatUrl;
	}

	public String getShowModeFormatUrl() {
		return showModeFormatUrl;
	}

	public void setShowModeFormatUrl(String showModeFormatUrl) {
		this.showModeFormatUrl = showModeFormatUrl;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getBaseUrlOfFirstPage() {
		return baseUrlOfFirstPage;
	}

	public void setBaseUrlOfFirstPage(String baseUrlOfFirstPage) {
		this.baseUrlOfFirstPage = baseUrlOfFirstPage;
	}

	public String getCategoryFormatUrl() {
		return categoryFormatUrl;
	}

	public void setCategoryFormatUrl(String categoryFormatUrl) {
		this.categoryFormatUrl = categoryFormatUrl;
	}

	public String getPropertyFormatUrl() {
		return propertyFormatUrl;
	}

	public void setPropertyFormatUrl(String propertyFormatUrl) {
		this.propertyFormatUrl = propertyFormatUrl;
	}

}

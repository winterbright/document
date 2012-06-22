package com.itecheasy.ph3.web;

public class Link {
	private String text;
	private String href;
	private boolean isEscapeHtml = true;

	public Link() {
	}

	public Link(String text, String href) {
		this.text = text;
		this.href = href;
	}

	public Link(String text, String href, boolean isEscapeHtml) {
		this.text = text;
		this.href = href;
		this.isEscapeHtml = isEscapeHtml;
	}


	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}


	public boolean isEscapeHtml() {
		return isEscapeHtml;
	}

	public void setEscapeHtml(boolean isEscapeHtml) {
		this.isEscapeHtml = isEscapeHtml;
	}
}

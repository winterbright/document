package com.zjm.util.model;

/**
 * @alias
 * @author zjm
 *
 * 2012-7-9
 */
public class ButtonModelFtl {

	private String text;
	private String iconCls;
	private String applyTo;
	private String handler;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getIconCls() {
		return iconCls;
	}
	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}
	public String getHandler() {
		return handler;
	}
	public void setHandler(String handler) {
		this.handler = handler;
	}
	
}

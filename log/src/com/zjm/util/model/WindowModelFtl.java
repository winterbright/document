package com.zjm.util.model;

import java.util.List;

/**
 * @alias
 * @author zjm
 *
 * 2012-7-9
 */
public class WindowModelFtl {

	private String name;
	private String title;
	private String width;
	private String height;
	private String bodyStyle;
	private PanelModelFtl items; //
	private List<ButtonModelFtl> buttons;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public PanelModelFtl getItems() {
		return items;
	}
	public void setItems(PanelModelFtl items) {
		this.items = items;
	}
	public List<ButtonModelFtl> getButtons() {
		return buttons;
	}
	public void setButtons(List<ButtonModelFtl> buttons) {
		this.buttons = buttons;
	}
	
}

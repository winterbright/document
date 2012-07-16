package com.zjm.util.model;

import java.util.List;

/**
 * @alias
 * @author zjm
 *
 * 2012-7-9
 */
public class ToolBarModelFtl {

	private String name;
	private String text;
	private List<ButtonModelFtl> buttons;
	public List<ButtonModelFtl> getButtons() {
		return buttons;
	}
	public void setButtons(List<ButtonModelFtl> buttons) {
		this.buttons = buttons;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
}

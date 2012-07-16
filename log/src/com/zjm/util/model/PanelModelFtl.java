package com.zjm.util.model;

import java.util.List;

/**
 * @alias 面板
 * @author zjm
 *
 * 2012-7-9
 */
public class PanelModelFtl {

	private String id;
	private String name;
	private String region; //north,south,center,east,west
	private String title;
	private String iconCls;
	private Object contentEI; //TODO
	private String width;
	private String height;
	private String buttonAlign;
	private ToolBarModelFtl tbar;
	private List<ButtonModelFtl> buttons;
	private List<FunctionModelFtl> funs;
	private String renderTo;
	private String html;//----预留
	private String autoScroll;//----预留
	
	public List<FunctionModelFtl> getFuns() {
		return funs;
	}
	public void setFuns(List<FunctionModelFtl> funs) {
		this.funs = funs;
	}
	public String getRenderTo() {
		return renderTo;
	}
	public void setRenderTo(String renderTo) {
		this.renderTo = renderTo;
	}
	public ToolBarModelFtl getTbar() {
		return tbar;
	}
	public void setTbar(ToolBarModelFtl tbar) {
		this.tbar = tbar;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIconCls() {
		return iconCls;
	}
	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
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
	public String getButtonAlign() {
		return buttonAlign;
	}
	public void setButtonAlign(String buttonAlign) {
		this.buttonAlign = buttonAlign;
	}
	public List<ButtonModelFtl> getButtons() {
		return buttons;
	}
	public void setButtons(List<ButtonModelFtl> buttons) {
		this.buttons = buttons;
	}
	
}

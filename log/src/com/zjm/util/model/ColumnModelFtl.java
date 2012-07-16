package com.zjm.util.model;

/**
 * @alias
 * @author zjm
 *
 * 2012-7-9
 */
public class ColumnModelFtl {
	private String id;
	private String header;
	private String dataIndex;
	private String hidden;
	private String width;
	private String sortable;
	private String align;
	private String renderer; //TODO
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getAlign() {
		return align;
	}
	public void setAlign(String align) {
		this.align = align;
	}
	public String getRenderer() {
		return renderer;
	}
	public void setRenderer(String renderer) {
		this.renderer = renderer;
	}
	public String getSortable() {
		return sortable;
	}
	public void setSortable(String sortable) {
		this.sortable = sortable;
	}
	public String getHidden() {
		return hidden;
	}
	public void setHidden(String hidden) {
		this.hidden = hidden;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getDataIndex() {
		return dataIndex;
	}
	public void setDataIndex(String dataIndex) {
		this.dataIndex = dataIndex;
	}
}

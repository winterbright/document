package com.zjm.util.model;

import java.util.List;

/**
 * @alias
 * @author zjm
 *
 * 2012-7-13
 */
public class CheckBoxModelFtl extends FieldModelFtl{

	private String columns = "[100, 100]";
	private List<String> items;
	public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public List<String> getItems() {
		return items;
	}
	public void setItems(List<String> items) {
		this.items = items;
	}
}

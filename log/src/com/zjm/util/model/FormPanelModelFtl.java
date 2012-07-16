package com.zjm.util.model;

import java.util.List;

/**
 * @alias
 * @author zjm
 *
 * 2012-7-9
 */
public class FormPanelModelFtl extends PanelModelFtl{

	private String labelWidth; //标签宽度
	private String defaultType; // 表单元素默认类型:textfield
	private String labelAlign; // 标签对齐方式:right
	private String bodyStyle = "padding:5 5 5 5"; // 表单元素和表单面板的边距:'padding:5 5 5 5'
	private List<FormLayoutModelFtl> columns;
	public List<FormLayoutModelFtl> getColumns() {
		return columns;
	}
	public void setColumns(List<FormLayoutModelFtl> columns) {
		this.columns = columns;
	}
}

package com.zjm.util.model.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zjm.util.FmCreFile;
import com.zjm.util.model.ButtonModelFtl;
import com.zjm.util.model.CheckBoxModelFtl;
import com.zjm.util.model.ColumnModelFtl;
import com.zjm.util.model.FieldModelFtl;
import com.zjm.util.model.FormItemModelFtl;
import com.zjm.util.model.FormLayoutModelFtl;
import com.zjm.util.model.FormPanelModelFtl;
import com.zjm.util.model.GridModelFtl;
import com.zjm.util.model.StoreModelFtl;
import com.zjm.util.model.ToolBarModelFtl;

/**
 * @alias
 * @author zjm
 *
 * 2012-7-11
 */
@SuppressWarnings("unchecked")
public class CreQuery {

	public static void main(String[] args) {
		FmCreFile fcf = new FmCreFile();
		String template = "testMacro.ftl";
		Map map = get();
		fcf.cre(map, template, "E:/project/零散/ext-3.4.0/ext-3.4.0/test/edit-grid.js");
	}
	
	private static Map get(){
		Map map = new HashMap();
		FormPanelModelFtl form = getForm();
		map.put("form", form);
		GridModelFtl grid = getGrid();
		map.put("grid", grid);
		return map;
	}
	
	private static GridModelFtl getGrid(){
		GridModelFtl grid = new GridModelFtl();
		List<ColumnModelFtl> list = new ArrayList<ColumnModelFtl>();
		list.add(getCol("项目ID", "xmid"));
		list.add(getCol("大类", "xmdl"));
		list.add(getCol("项目名称", "xmmc"));
		list.add(getCol("项目热键", "xmrj"));
		list.add(getCol("报销比例", "bxbl"));
		list.add(getCol("规格", "gg"));
		list.add(getCol("单位", "dw"));
		list.add(getCol("启用状态", "qyzt"));
		list.add(getCol("剂型", "jx"));
		list.add(getCol("产地", "cd"));
		list.add(getCol("更改时间", "ggsj"));
		grid.setColumns(list);
		StoreModelFtl store = getStore(list);
		grid.setStore(store);
		grid.setTbar(getToolBar());
		grid.setTitle("医院收费项目");
		grid.setHeight("500");
		grid.setName("grid");
		return grid;
	}
	
	private static ToolBarModelFtl getToolBar(){
		ToolBarModelFtl bar = new ToolBarModelFtl();
		List<ButtonModelFtl> list = new ArrayList<ButtonModelFtl>();
		bar.setButtons(list);
		list.add(getButton("新增"));
		list.add(getButton("修改"));
		list.add(getButton("删除"));
		list.add(getButton("刷新"));
		return bar;
	}
	
	private static StoreModelFtl getStore(List<ColumnModelFtl> list){
		StoreModelFtl store = new StoreModelFtl();
		List<String> keys = new ArrayList<String>();
		for(ColumnModelFtl col : list){
			keys.add(col.getDataIndex());
		}
		store.setKeys(keys);
		store.setUrl("integrateDemo.ered?reqCode=querySfxmDatas");
		return store;
	}
	
	private static ColumnModelFtl getCol(String header, String dataIndex){
		ColumnModelFtl col = new ColumnModelFtl();
		col.setHeader(header);
		col.setDataIndex(dataIndex);
		return col;
	}
	
	private static FormPanelModelFtl getForm(){
		FormPanelModelFtl form = new FormPanelModelFtl();
		form.setName("qForm");
		form.setRegion("north");
		form.setTitle("查询条件");
		form.setButtonAlign("center");
		form.setHeight("420");
		List<FormLayoutModelFtl> list = new ArrayList<FormLayoutModelFtl>();
		list.add(getFormLay1());
//		list.add(getFormLay2());
		list.add(getFormLay3());
		form.setColumns(list);
		List<ButtonModelFtl> bus = new ArrayList<ButtonModelFtl>();
		bus.add(getButton("查询"));
		bus.add(getButton("打印"));
		bus.add(getButton("重置"));
		form.setButtons(bus);
		return form;
	}
	
	private static FormLayoutModelFtl getFormLay1(){
		FormLayoutModelFtl lay = new FormLayoutModelFtl();
		List<FormItemModelFtl> items = new ArrayList<FormItemModelFtl>();
		lay.setItems(items);
		items.add(getItem("项目ID", "xmid",null));
		items.add(getItem("项目名称", "xmmc",null));
		items.add(getItem("项目大类", "xmdl",null));
		return lay;
	}
	
	private static FormLayoutModelFtl getFormLay2(){
		FormLayoutModelFtl lay = new FormLayoutModelFtl();
		List<FormItemModelFtl> items = new ArrayList<FormItemModelFtl>();
		lay.setItems(items);
		items.add(getItem("剂型", "jx",null));
		items.add(getItem("产地", "cd",".66"));
		return lay;
	}
	
	private static FormLayoutModelFtl getFormLay3(){
		FormLayoutModelFtl lay = new FormLayoutModelFtl();
		List<FormItemModelFtl> items = new ArrayList<FormItemModelFtl>();
		lay.setItems(items);
		items.add(getCheckbox());
		FormItemModelFtl item = new FormItemModelFtl();
		item.setColWidth(".66");
		item.setLabelWidth("1");
		items.add(item);
		return lay;
	}
	
	private static FormItemModelFtl getCheckbox(){
		FormItemModelFtl item = new FormItemModelFtl();
		item.setColWidth(".33");
		CheckBoxModelFtl field = new CheckBoxModelFtl();
		field.setFieldLabel("字段");
		field.setName("column");
		field.setId("column");
		field.setXtype("checkboxgroup");
		List<String> items = new ArrayList<String>();
		items.add("Foo");
		items.add("Bar");
		items.add("Bar2");
		items.add("Bar3222222222222222222");
		items.add("Ba222222222222r4");
		items.add("Ba222222222222r5");
		items.add("Ba222222222222r6");
		items.add("Ba222222222222r7");
		items.add("Bar8");
		items.add("Ba222222222222r9");
		items.add("Bar11");
		items.add("Bar12");
		items.add("Bar13");
		items.add("Bar14");
		items.add("Bar15");
		items.add("Bar16");
		items.add("Bar21");
		field.setItems(items);
		item.setField(field);
		item.setLabelWidth("60");
		return item;
	}
	
	private static FormItemModelFtl getItem(String label,String name,String width){
		FormItemModelFtl item = new FormItemModelFtl();
		if(width != null){
			item.setColWidth(width);
		}else{
			item.setColWidth(".33");
		}
		item.setLabelWidth("60");
		item.setField(getField(label, name));
		return item;
	}
	
	private static FieldModelFtl getField(String label,String name){
		FieldModelFtl field = new FieldModelFtl();
		field.setFieldLabel(label);
		field.setName(name);
		field.setId(name);
		
		return field;
	}
	
	private static ButtonModelFtl getButton(String text){
		ButtonModelFtl button = new ButtonModelFtl();
		button.setText(text);
		return button;
	}
	
}

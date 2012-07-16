package com.zjm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonModel;

import com.zjm.util.model.ButtonModelFtl;
import com.zjm.util.model.ColumnModelFtl;
import com.zjm.util.model.FieldModelFtl;
import com.zjm.util.model.FormItemModelFtl;
import com.zjm.util.model.FormLayoutModelFtl;
import com.zjm.util.model.FormPanelModelFtl;
import com.zjm.util.model.FunctionModelFtl;
import com.zjm.util.model.GridModelFtl;
import com.zjm.util.model.ToolBarModelFtl;
//import com.zjm.util.model.NumberFieldModelFtl;
import com.zjm.util.model.StoreModelFtl;
import com.zjm.util.model.WindowModelFtl;


/**
 * @alias
 * @author zjm
 *
 * 2012-4-3
 */
@SuppressWarnings("unchecked")
public class Main {

	public static void main(String[] args) {
		FmCreFile fcf = new FmCreFile();
		String template = "testMacro.ftl";
		Map map = get();
//		fcf.show(map, template);
//		fcf.cre(map, template, "E:/fre/good.js");extjs-4.1.0\mytest
//		fcf.cre(map, template, "E:/fre/extjs-4.1.0/mytest/good.js");
		fcf.cre(map, template, "E:/project/零散/ext-3.4.0/ext-3.4.0/test/edit-grid.js");
		
	}

	public static Map get(){
		Map map = new HashMap();
		GridModelFtl gmf = new GridModelFtl();
		List<ColumnModelFtl> cols = getcols();	
		gmf.setColumns(cols);
		
		StoreModelFtl smf = new StoreModelFtl();
		smf.setUrl("plants.xml");
		List<String> keys = getList(cols);
		smf.setKeys(keys);
		
		gmf.setStore(smf);
		gmf.setTitle("Edit Plants");
		gmf.setRenderTo("editor-grid");
//		gmf.setWidth("600");
		gmf.setHeight("400");
		gmf.setTbar(creToolBar());
		gmf.setFuns(getFuns());
		map.put("grid", gmf);
		return map;
	}
	
	private static List<ColumnModelFtl> getcols(){
		List<ColumnModelFtl> cols = new ArrayList<ColumnModelFtl>();
		ColumnModelFtl cmf = new ColumnModelFtl();
		cmf.setHeader("Common Name");
		cmf.setDataIndex("common");
		cmf.setWidth("220");
		cols.add(cmf);
		
		ColumnModelFtl cmf1 = new ColumnModelFtl();
		cmf1.setHeader("Light");
		cmf1.setDataIndex("light");
		cmf1.setWidth("130");
		cols.add(cmf1);
		
		ColumnModelFtl cmf2 = new ColumnModelFtl();
		cmf2.setHeader("Price");
		cmf2.setDataIndex("price");
		cmf2.setWidth("70");
		cols.add(cmf2);
		
		ColumnModelFtl cmf3 = new ColumnModelFtl();
		cmf3.setHeader("Available");
		cmf3.setDataIndex("availDate");
		cmf3.setWidth("95");
		cols.add(cmf3);

		return cols;
	}
	
	private static List<String> getList(List<ColumnModelFtl> cols){
		List<String> keys = new ArrayList<String>();
		for(ColumnModelFtl col : cols){
			keys.add(col.getDataIndex());
		}
		return keys;
	}
	
	private static ToolBarModelFtl creToolBar(){
		ToolBarModelFtl toolBar = new ToolBarModelFtl();
		List<ButtonModelFtl> list = new ArrayList<ButtonModelFtl>();
		ButtonModelFtl button1 = new ButtonModelFtl();
		button1.setText("查询");
//		button1.setIconCls("page_findIcon");
		button1.setHandler("queryBalanceInfo");
		list.add(button1);
		ButtonModelFtl button2 = new ButtonModelFtl();
		button2.setText("刷新");
//		button2.setHandler("");
		list.add(button2);
		toolBar.setButtons(list);
		return toolBar;
	}
	
	private static List<FunctionModelFtl> getFuns(){
		List<FunctionModelFtl> funs = new ArrayList<FunctionModelFtl>();
		FunctionModelFtl fun1 = new FunctionModelFtl();
		fun1.setName("queryBalanceInfo");
		fun1.setInfo("alert('1');");
		funs.add(fun1);
		return funs;
	}
	
}

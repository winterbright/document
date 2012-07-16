package com.zjm.util.model.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zjm.util.FmCreFile;
import com.zjm.util.model.ButtonModelFtl;
import com.zjm.util.model.ColumnModelFtl;
import com.zjm.util.model.FormPanelModelFtl;
import com.zjm.util.model.GridModelFtl;
import com.zjm.util.model.StoreModelFtl;
import com.zjm.util.model.ToolBarModelFtl;

/**
 * @alias
 * @author zjm
 *
 * 2012-7-13
 */
@SuppressWarnings("unchecked")
public class TheFirst {


	public static void main(String[] args) {
		FmCreFile fcf = new FmCreFile();
		String template = "testMacro.ftl";
		Map map = get();
		fcf.cre(map, template, "E:/project/零散/ext-3.4.0/ext-3.4.0/test/edit-grid.js");
	}
	
	private static Map get(){
		Map map = new HashMap();
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
	
	private static ColumnModelFtl getCol(String header, String dataIndex){
		ColumnModelFtl col = new ColumnModelFtl();
		col.setHeader(header);
		col.setDataIndex(dataIndex);
		return col;
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
	
	private static ButtonModelFtl getButton(String text){
		ButtonModelFtl button = new ButtonModelFtl();
		button.setText(text);
		return button;
	}
	
}

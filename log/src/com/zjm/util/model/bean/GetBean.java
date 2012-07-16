package com.zjm.util.model.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zjm.util.model.ButtonModelFtl;
import com.zjm.util.model.ColumnModelFtl;
import com.zjm.util.model.FieldModelFtl;
import com.zjm.util.model.FormItemModelFtl;
import com.zjm.util.model.FormLayoutModelFtl;
import com.zjm.util.model.FormPanelModelFtl;
import com.zjm.util.model.StoreModelFtl;
import com.zjm.util.model.ToolBarModelFtl;
import com.zjm.util.model.WindowModelFtl;

/**
 * @alias
 * @author zjm
 *
 * 2012-7-11
 */
@SuppressWarnings("unchecked")
public class GetBean {

//	private String id;
//	private String name;
//	private String region; //north,south,center,east,west
//	private String title;
//	private String iconCls;
//	private String width;
//	private String height;
//	private String buttonAlign;
//	private ToolBarModelFtl tbar;
//	private List<ButtonModelFtl> buttons;
//	private String labelWidth; //标签宽度
//	private String defaultType; // 表单元素默认类型:textfield
//	private String labelAlign; // 标签对齐方式:right
//	private String bodyStyle; // 表单元素和表单面板的边距:'padding:5 5 5 5'
//	private List<FormLayoutModelFtl> columns;
	
	public void baseForm(){
		FormPanelModelFtl baseFormPlan = new FormPanelModelFtl();
		baseFormPlan.setName("baseFormPlan");
		baseFormPlan.setHeight("240");
		
//		FieldModelFtl nameField = new FieldModelFtl();
//		nameField.setAllowBlank("false");
//		nameField.setFieldLabel("查询");
//		FieldModelFtl
//		
	}
	
	public void queryForm(){
		FormPanelModelFtl queryForm = new FormPanelModelFtl();
		queryForm.setName("qForm");
		queryForm.setRegion("north");
		queryForm.setButtonAlign("center");
	}
	
	
	

	
	
	
	
	
	
	
	public FormPanelModelFtl getForm(){
		FormPanelModelFtl fpmf = new FormPanelModelFtl();
		fpmf.setName("qForm");
		fpmf.setRegion("north");
		fpmf.setTitle("查询条件");
		fpmf.setHeight("120");
		fpmf.setButtonAlign("center");
		List<FormLayoutModelFtl> columns = new ArrayList<FormLayoutModelFtl>();
		FormLayoutModelFtl flmf1 = new FormLayoutModelFtl();
		List<FormItemModelFtl> items = new ArrayList<FormItemModelFtl>();
		FormItemModelFtl fimf1 = new FormItemModelFtl();
		fimf1.setColWidth(".33");
//		FieldModelFtl fmf1 = new NumberFieldModelFtl();
//		fmf1.setFieldLabel("项目ID");
//		fmf1.setId("xmid");
//		fmf1.setName("xmid");
//		fimf1.setField(fmf1);
		items.add(fimf1);
		FormItemModelFtl fimf2 = new FormItemModelFtl();
		fimf2.setColWidth(".33");
		FieldModelFtl fmf2 = new FieldModelFtl();
		fmf2.setFieldLabel("项目名称");
		fmf2.setId("xmmc");
		fmf2.setName("xmmc");
		fmf2.setAllowBlank("true");
		fmf2.setMaxLength("50");
		fimf2.setField(fmf2);
		items.add(fimf2);
		FormItemModelFtl fimf3 = new FormItemModelFtl();
		fimf3.setColWidth(".33");
		FieldModelFtl fmf3 = new FieldModelFtl();
		fmf3.setFieldLabel("项目大类");
		fmf3.setId("xmdl");
		fmf3.setName("xmdl");
		fmf3.setAllowBlank("true");
		fmf3.setMaxLength("50");
		fimf3.setField(fmf3);
		items.add(fimf3);
		flmf1.setItems(items);
		columns.add(flmf1);
		
		fpmf.setColumns(columns);
		return fpmf;
	}
	
	public static Map getMacro(){
		Map map = new HashMap();
		map.put("field", "book");
		return map;
	}
	public static Map getMap4Field(){
		Map map = new HashMap();
//		NumberFieldModelFtl nfmf = new NumberFieldModelFtl();
//		nfmf.setName("test");
//		nfmf.setId("test");
//		nfmf.setFieldLabel("test");
//		map.put("field", nfmf);
		return map;
	}
	
	public static Map getMapForForm(){
		Map map = new HashMap();
		FormPanelModelFtl fpmf = new FormPanelModelFtl();
		fpmf.setName("firstForm");
		fpmf.setTitle("good");
		FormLayoutModelFtl column1 = new FormLayoutModelFtl();
		FormItemModelFtl fimf1 = new FormItemModelFtl();
		fimf1.setColWidth(".33");
//		fimf1.setDisabled("false");
//		fimf1.setId("11");
//		fimf1.setName("11");
//		fimf1.setLabel("ID");
//		fimf1.setLabelWidth("65");
//		fimf1.setXtype("textfield");
//		NumberFieldModelFtl field = new NumberFieldModelFtl();
//		field.setFieldLabel("test");
//		field.setName("test");
//		field.setId("test");
//		field.setMaxValue("100");
//		fimf1.setField(field);
		
		
		FormItemModelFtl fimf2 = new FormItemModelFtl();
		fimf2.setColWidth(".33");
//		fimf2.setDisabled("false");
//		fimf2.setId("22");
//		fimf2.setName("22");
//		fimf2.setLabel("项目热键");
//		fimf2.setLabelWidth("65");
//		fimf2.setXtype("textfield");
//		NumberFieldModelFtl nfmf2 = new NumberFieldModelFtl();
//		nfmf2.setId("22");
//		nfmf2.setName("22");
//		nfmf2.setFieldLabel("项目热键");
//		fimf2.setField(nfmf2);
		
		FormItemModelFtl fimf3 = new FormItemModelFtl();
		fimf3.setColWidth(".33");
//		fimf3.setDisabled("false");
//		fimf3.setId("33");
//		fimf3.setName("33");
//		fimf3.setLabel("报销比例");
//		fimf3.setLabelWidth("65");
//		fimf3.setXtype("datefield");
//		NumberFieldModelFtl nfmf3 = new NumberFieldModelFtl();
//		nfmf3.setId("33");
//		nfmf3.setName("33");
//		nfmf3.setFieldLabel("报销比例");
//		fimf3.setField(nfmf3);
		
		List<FormItemModelFtl> items1 = new ArrayList<FormItemModelFtl>();
		List<FormLayoutModelFtl> columns = new ArrayList<FormLayoutModelFtl>();
		items1.add(fimf1);
		items1.add(fimf2);
		items1.add(fimf3);
		column1.setItems(items1);
		
		FormLayoutModelFtl column2 = new FormLayoutModelFtl();
		FormItemModelFtl fimf4 = new FormItemModelFtl();
		fimf4.setColWidth(".66");
//		fimf4.setDisabled("false");
//		fimf4.setId("44");
//		fimf4.setName("44");
//		fimf4.setLabel("项目ID");
//		fimf4.setLabelWidth("65");
//		fimf4.setXtype("numberfield");
		FormItemModelFtl fimf5 = new FormItemModelFtl();
		fimf5.setColWidth(".33");
//		fimf5.setDisabled("false");
//		fimf5.setId("55");
//		fimf5.setName("55");
//		fimf5.setLabel("项目热键");
//		fimf5.setLabelWidth("65");
//		fimf5.setXtype("textfield");
		FormItemModelFtl fimf6 = new FormItemModelFtl();
		fimf6.setColWidth(".33");
//		fimf6.setDisabled("false");
//		fimf6.setId("66");
//		fimf6.setName("66");
//		fimf6.setLabel("报销比例");
//		fimf6.setLabelWidth("65");
//		fimf6.setXtype("datefield");
		List<FormItemModelFtl> items2 = new ArrayList<FormItemModelFtl>();

		items2.add(fimf4);
		items2.add(fimf5);
//		items2.add(fimf6);
		column2.setItems(items2);
		
		columns.add(column1);
//		columns.add(column2);
		fpmf.setColumns(columns);
		map.put("form", fpmf);
		return map;
	}
	
	public static Map getMapForStore(){
		Map map = new HashMap();
		StoreModelFtl smf = new StoreModelFtl();
		smf.setName("store");
		smf.setUrl("integrateDemo.ered?reqCode=querySfxmDatas");
		List<String> keys = new ArrayList<String>();
		keys.add("abc");
		keys.add("abc");
		keys.add("abc");
		keys.add("abc");
		keys.add("abc");
		keys.add("abc");
		smf.setKeys(keys);
		map.put("store", smf);
		return map;
	}
	
	public static Map getMapForWindow(){
		Map map = new HashMap();
		WindowModelFtl wmf = new WindowModelFtl();
		wmf.setName("firstWindow");
		wmf.setTitle("这是标题");
//		wmf.setItems("myForm");
		ButtonModelFtl saveButton = new ButtonModelFtl();
		saveButton.setText("保存1");
		saveButton.setIconCls("acceptIcon");
		saveButton.setHandler("submitTheForm();");
		ButtonModelFtl resetButton = new ButtonModelFtl();
		resetButton.setText("重置");
		resetButton.setIconCls("tbar_synchronizeIcon");
		resetButton.setHandler("myForm.getForm().reset();");
		ButtonModelFtl closeButton = new ButtonModelFtl();
		closeButton.setText("关闭");
		closeButton.setIconCls("deleteIcon");
		closeButton.setHandler("firstWindow.hide();");
		List<ButtonModelFtl> list = new ArrayList<ButtonModelFtl>();
		list.add(saveButton);
		list.add(resetButton);
		list.add(closeButton);
		wmf.setButtons(list);
		map.put("window", wmf);
		return map;
	}
	
	public static Map getMap1(){
		Map map = new HashMap();
		Map m1 = new HashMap();
		List list = new ArrayList();
		Map m = new HashMap();
		m.put("good", "good");
		m.put("hello", "hello");
		list.add(m);
		list.add(m);
		m1.put("list", list);
		m1.put("name", "map->list");
		map.put("m1", m1);
		map.put("name", "map->name");
		return map;
	}
	
	public static Map getMap(){
		
		ColumnModelFtl cmf = new ColumnModelFtl();
		cmf.setDataIndex("title");
		cmf.setHeader("title");
		
		Map map = new HashMap();
		Map column = new HashMap();
		List cols1 = new ArrayList();
		Map col = new HashMap();
		col.put("header", "title");
		col.put("dataIndex", "title");
//		col.put("hidden", "true");
//		col.put("sortable", "true");
		cols1.add(cmf);
		cols1.add(cmf);
		column.put("name", "cm");
		column.put("cols", cols1);
		map.put("column", column);
		return map;
	}
	
}

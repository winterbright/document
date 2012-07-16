${title};

	var qForm = new Ext.form.FormPanel({
				region : 'north',
				title : '<span class="commoncss">${title}<span>',
				collapsible : true,
				border : true,
				labelWidth : 50, // 标签宽度
				// frame : true, //是否渲染表单面板背景色
				labelAlign : 'right', // 标签对齐方式
				bodyStyle : 'padding:3 5 0', // 表单元素和表单面板的边距
				buttonAlign : 'center',
				height : 120,
				items : [{
					layout : 'column',
					border : false,
					items : [{
								columnWidth : .33,
								layout : 'form',
								labelWidth : 60, // 标签宽度
								defaultType : 'textfield',
								border : false,
								items : [{
											fieldLabel : '项目ID',
											name : 'xmid',
											id : 'id_txt_xmid',
											xtype : 'numberfield', // 设置为数字输入框类型
											anchor : '100%'
										}]
							}, {
								columnWidth : .33,
								layout : 'form',
								labelWidth : 60, // 标签宽度
								defaultType : 'textfield',
								border : false,
								items : [{
											fieldLabel : '项目名称', // 标签
											id : 'xmmc',
											name : 'xmmc', // name:后台根据此name属性取值
											allowBlank : true, // 是否允许为空
											maxLength : 50, // 可输入的最大文本长度,不区分中英文字符
											anchor : '100%' // 宽度百分比
										}]
							}, {
								columnWidth : .33,
								layout : 'form',
								labelWidth : 60, // 标签宽度
								defaultType : 'textfield',
								border : false,
								items : [new Ext.form.ComboBox({
											hiddenName : 'sfdlbm',
											fieldLabel : '项目大类',
											emptyText : '请选择',
											triggerAction : 'all',
											store : new Ext.data.SimpleStore({
														fields : ['name',
																'code'],
														data : [['西药', '01'],
																['中成药', '02']]
													}),
											displayField : 'name',
											valueField : 'code',
											mode : 'local',
											forceSelection : false, // 选中内容必须为下拉列表的子项
											editable : false,
											typeAhead : true,
											// value:'0002',
											resizable : true,
											anchor : '100%'
										})]
							}]
				}, {
					layout : 'column',
					border : false,
					items : [{
						columnWidth : .33,
						layout : 'form',
						labelWidth : 60, // 标签宽度
						defaultType : 'textfield',
						border : false,
						items : [new Ext.form.ComboBox({
									hiddenName : 'jx',
									fieldLabel : '剂型',
									triggerAction : 'all',
									emptyText : '请选择',
									store : new Ext.data.SimpleStore({
												fields : ['name', 'code'],
												data : [['注射剂', '注射剂'],
														['乳膏', '乳膏'],
														['片剂', '片剂']]
											}),
									displayField : 'name',
									valueField : 'code',
									mode : 'local',
									forceSelection : false, // 选中内容必须为下拉列表的子项
									editable : false,
									typeAhead : true,
									resizable : true,
									anchor : '100%'
								})]
					}, {
						columnWidth : .67,
						layout : 'form',
						labelWidth : 60, // 标签宽度
						defaultType : 'textfield',
						border : false,
						items : [{
									fieldLabel : '产地',
									name : 'cd',
									maxLength : 50,
									xtype : 'textfield',
									anchor : '99%'
								}]
					}]
				}],
				buttons : [{
							text : '查询',
							iconCls : 'previewIcon',
							handler : function() {
								Ext.getCmp('tbi_edit').disable();
								Ext.getCmp('tbi_del').disable();
								querySfxmDatas();
							}
						}, {
							text : '打印',
							id : 'id_btn_print',
							iconCls : 'printerIcon',
							handler : function() {
								printCatalog1();
							}
						}, {
							text : '重置',
							iconCls : 'tbar_synchronizeIcon',
							handler : function() {
								qForm.getForm().reset();
							}
						}]
			});
			
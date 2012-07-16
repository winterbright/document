${title};

	var qForm = new Ext.form.FormPanel({
				region : 'north',
				title : '<span class="commoncss">${title}<span>',
				collapsible : true,
				border : true,
				labelWidth : 50, // ��ǩ���
				// frame : true, //�Ƿ���Ⱦ����屳��ɫ
				labelAlign : 'right', // ��ǩ���뷽ʽ
				bodyStyle : 'padding:3 5 0', // ��Ԫ�غͱ����ı߾�
				buttonAlign : 'center',
				height : 120,
				items : [{
					layout : 'column',
					border : false,
					items : [{
								columnWidth : .33,
								layout : 'form',
								labelWidth : 60, // ��ǩ���
								defaultType : 'textfield',
								border : false,
								items : [{
											fieldLabel : '��ĿID',
											name : 'xmid',
											id : 'id_txt_xmid',
											xtype : 'numberfield', // ����Ϊ�������������
											anchor : '100%'
										}]
							}, {
								columnWidth : .33,
								layout : 'form',
								labelWidth : 60, // ��ǩ���
								defaultType : 'textfield',
								border : false,
								items : [{
											fieldLabel : '��Ŀ����', // ��ǩ
											id : 'xmmc',
											name : 'xmmc', // name:��̨���ݴ�name����ȡֵ
											allowBlank : true, // �Ƿ�����Ϊ��
											maxLength : 50, // �����������ı�����,��������Ӣ���ַ�
											anchor : '100%' // ��Ȱٷֱ�
										}]
							}, {
								columnWidth : .33,
								layout : 'form',
								labelWidth : 60, // ��ǩ���
								defaultType : 'textfield',
								border : false,
								items : [new Ext.form.ComboBox({
											hiddenName : 'sfdlbm',
											fieldLabel : '��Ŀ����',
											emptyText : '��ѡ��',
											triggerAction : 'all',
											store : new Ext.data.SimpleStore({
														fields : ['name',
																'code'],
														data : [['��ҩ', '01'],
																['�г�ҩ', '02']]
													}),
											displayField : 'name',
											valueField : 'code',
											mode : 'local',
											forceSelection : false, // ѡ�����ݱ���Ϊ�����б������
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
						labelWidth : 60, // ��ǩ���
						defaultType : 'textfield',
						border : false,
						items : [new Ext.form.ComboBox({
									hiddenName : 'jx',
									fieldLabel : '����',
									triggerAction : 'all',
									emptyText : '��ѡ��',
									store : new Ext.data.SimpleStore({
												fields : ['name', 'code'],
												data : [['ע���', 'ע���'],
														['���', '���'],
														['Ƭ��', 'Ƭ��']]
											}),
									displayField : 'name',
									valueField : 'code',
									mode : 'local',
									forceSelection : false, // ѡ�����ݱ���Ϊ�����б������
									editable : false,
									typeAhead : true,
									resizable : true,
									anchor : '100%'
								})]
					}, {
						columnWidth : .67,
						layout : 'form',
						labelWidth : 60, // ��ǩ���
						defaultType : 'textfield',
						border : false,
						items : [{
									fieldLabel : '����',
									name : 'cd',
									maxLength : 50,
									xtype : 'textfield',
									anchor : '99%'
								}]
					}]
				}],
				buttons : [{
							text : '��ѯ',
							iconCls : 'previewIcon',
							handler : function() {
								Ext.getCmp('tbi_edit').disable();
								Ext.getCmp('tbi_del').disable();
								querySfxmDatas();
							}
						}, {
							text : '��ӡ',
							id : 'id_btn_print',
							iconCls : 'printerIcon',
							handler : function() {
								printCatalog1();
							}
						}, {
							text : '����',
							iconCls : 'tbar_synchronizeIcon',
							handler : function() {
								qForm.getForm().reset();
							}
						}]
			});
			
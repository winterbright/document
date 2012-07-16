	var myForm = new Ext.form.FormPanel({
				collapsible : false,
				border : true,
				labelWidth : 60, // ��ǩ���
				// frame : true, //�Ƿ���Ⱦ����屳��ɫ
				labelAlign : 'right', // ��ǩ���뷽ʽ
				bodyStyle : 'padding:5 5 0', // ��Ԫ�غͱ����ı߾�
				buttonAlign : 'center',
				height : 250,
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
											disabled : true,
											fieldClass : 'x-custom-field-disabled',
											xtype : 'textfield', // ����Ϊ�������������
											anchor : '100%'
										}, {
											fieldLabel : '��Ŀ�ȼ�',
											name : 'xmrj',
											maxLength : 20,
											anchor : '100%'
										}, {
											fieldLabel : '��������',
											name : 'zfbl',
											maxValue : 1,
											decimalPrecision : 2,// С������
											allowBlank : false,
											xtype : 'numberfield',
											labelStyle : 'color:blue;',
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
											name : 'xmmc', // name:��̨���ݴ�name����ȡֵ
											maxLength : 20, // �����������ı�����,��������Ӣ���ַ�
											allowBlank : false,
											labelStyle : 'color:blue;',
											anchor : '100%'// ��Ȱٷֱ�
										}, {
											fieldLabel : '���',
											name : 'gg',
											xtype : 'textfield', // ����Ϊ�������������
											maxLength : 25,
											anchor : '100%'
										}, new Ext.form.ComboBox({
													hiddenName : 'jx',
													fieldLabel : '����',
													triggerAction : 'all',
													emptyText : '��ѡ��',
													store : new Ext.data.SimpleStore(
															{
																fields : [
																		'name',
																		'code'],
																data : [
																		['ע���',
																				'ע���'],
																		['���',
																				'���'],
																		['Ƭ��',
																				'Ƭ��']]
															}),
													displayField : 'name',
													valueField : 'code',
													mode : 'local',
													forceSelection : false, // ѡ�����ݱ���Ϊ�����б������
													editable : false,
													typeAhead : true,
													resizable : true,
													allowBlank : false,
													anchor : '100%'
												})]
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
											allowBlank : false,
											labelStyle : 'color:blue;',
											anchor : '100%'
										}), new Ext.form.ComboBox({
											hiddenName : 'dw',
											fieldLabel : '��λ',
											emptyText : '��ѡ��',
											triggerAction : 'all',
											store : new Ext.data.SimpleStore({
														fields : ['name',
																'code'],
														data : [['ƿ', 'ƿ'],
																['��', '��']]
													}),
											displayField : 'name',
											valueField : 'code',
											mode : 'local',
											forceSelection : false, // ѡ�����ݱ���Ϊ�����б������
											editable : false,
											typeAhead : true,
											// value:'0002',
											resizable : true,
											allowBlank : false,
											labelStyle : 'color:blue;',
											anchor : '100%'
										}), new Ext.form.ComboBox({
											hiddenName : 'qybz',
											fieldLabel : '���ñ�־',
											triggerAction : 'all',
											store : new Ext.data.SimpleStore({
														fields : ['name',
																'code'],
														data : [['����', '1'],
																['ͣ��', '0']]
													}),
											displayField : 'name',
											valueField : 'code',
											mode : 'local',
											forceSelection : false, // ѡ�����ݱ���Ϊ�����б������
											editable : false,
											typeAhead : true,
											value : '1',
											resizable : true,
											allowBlank : false,
											labelStyle : 'color:blue;',
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
								items : [{
											xtype : 'datefield',
											fieldLabel : '��Чʱ��', // ��ǩ
											name : 'ggsj', // name:��̨���ݴ�name����ȡֵ
											format : 'Y-m-d', // ���ڸ�ʽ��
											value : new Date(),
											anchor : '100%' // ��Ȱٷֱ�
										}]
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
											anchor : '99%'
										}]
							}]
				}, {
					fieldLabel : '��ע',
					name : 'remark',
					xtype : 'textarea',
					maxLength : 100,
					emptyText : '��ע��Ϣ.(˵��:���ֶν�������ʾ�����в���,���ݲ����г־û�)',
					anchor : '99%'
				}]
			});

	var firstWindow = new Ext.Window({
				title : '<span class="commoncss">¼��ҽԺ�շ���Ŀ<span>', // ���ڱ���
				layout : 'fit', // ���ô��ڲ���ģʽ
				width : 600, // ���ڿ��
				height : 260, // ���ڸ߶�
				closable : false, // �Ƿ�ɹر�
				collapsible : true, // �Ƿ������
				maximizable : true, // �����Ƿ�������
				border : false, // �߿�������
				constrain : true, // ���ô����Ƿ�������������
				animateTarget : Ext.getBody(),
				pageY : 20, // ҳ�涨λY����
				pageX : document.body.clientWidth / 2 - 600 / 2, // ҳ�涨λX����
				items : [myForm], // Ƕ��ı����
				buttons : [{
							text : '����1',
							iconCls : 'acceptIcon',
							handler : function() {
								submitTheForm();
							}
						}, {
							text : '����2(batch������)',
							iconCls : 'acceptIcon',
							handler : function() {
								submitTheFormBasedBatch();
							}
						}, {
							text : '����',
							iconCls : 'tbar_synchronizeIcon',
							handler : function() {
								myForm.getForm().reset();
							}
						}, {
							text : '�ر�',
							iconCls : 'deleteIcon',
							handler : function() {
								firstWindow.hide();
							}
						}]
			});
			
	/**
	 * ���ύ(���Դ�Ajax�ύ)
	 */
	function submitTheForm() {
		if (!myForm.getForm().isValid())
			return;
		myForm.form.submit({
					url : 'integrateDemo.ered?reqCode=updateSfxm',
					waitTitle : '��ʾ',
					method : 'POST',
					waitMsg : '���ڴ�������,���Ժ�...',
					success : function(form, action) { // �ص�������2������
						Ext.MessageBox.alert('��ʾ', action.result.msg);
					},
					failure : function(form, action) {
						Ext.Msg
								.alert('��ʾ', '���ݲ�ѯʧ��,��������:'
												+ action.failureType);
					}
				});
	}
	
	/**
	 * ���ύ(���Դ�Ajax�ύ)
	 */
	function submitTheForm() {
		if (!myForm.getForm().isValid())
			return;
		myForm.form.submit({
					url : 'integrateDemo.ered?reqCode=saveSfxmDomain',
					waitTitle : '��ʾ',
					method : 'POST',
					waitMsg : '���ڴ�������,���Ժ�...',
					success : function(form, action) { // �ص�������2������
						// Ext.MessageBox.alert('��ʾ',
						// action.result.msg);
						var items = myForm.find('name', 'xmid');
						items[0].setValue(action.result.xmid);
						Ext.Msg.confirm('��ȷ��', '�����ɹ�,��Ҫ���������շ���Ŀ��?', function(
										btn, text) {
									if (btn == 'yes') {
										myForm.getForm().reset();
									} else {
										firstWindow.hide();
										store.reload();
									}
								});
					},
					failure : function(form, action) {
						Ext.MessageBox.alert('��ʾ', '���ݱ���ʧ��');
					}
				});
	}
	
	/**
	 * ���ύ(��Batch��ʽ����ִ��SQL���)
	 */
	function submitTheFormBasedBatch() {
		if (!myForm.getForm().isValid())
			return;
		if (runMode == '0') {
			Ext.Msg.alert('��ʾ', 'ϵͳ��������ʾģʽ������,���Ĳ�����ȡ��!��ģʽ��ֻ�ܽ��в�ѯ����!');
			return;
		}
		myForm.form.submit({
					url : 'integrateDemo.ered?reqCode=batchSql',
					waitTitle : '��ʾ',
					method : 'POST',
					waitMsg : '���ڴ�������,���Ժ�...',
					success : function(form, action) {
						Ext.MessageBox.alert('��ʾ', action.result.msg);
					},
					failure : function(form, action) {
						Ext.Msg
								.alert('��ʾ', '���ݲ�ѯʧ��,��������:'
												+ action.failureType);
					}
				});
	}
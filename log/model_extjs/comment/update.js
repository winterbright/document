	var updateForm = new Ext.form.FormPanel({
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
											readOnly : true,
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

	var updateWindow = new Ext.Window({
				title : '<span class="commoncss">�޸�ҽԺ�շ���Ŀ<span>', // ���ڱ���
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
				items : [updateForm], // Ƕ��ı����
				buttons : [{
							text : '����',
							iconCls : 'acceptIcon',
							handler : function() {
								updateTheForm();
							}
						}, {
							text : '�ر�',
							iconCls : 'deleteIcon',
							handler : function() {
								updateWindow.hide();
							}
						}]
			});
			
	/**
	 * ���ύ(���Դ�Ajax�ύ)
	 */
	function updateTheForm() {
		if (!updateForm.getForm().isValid())
			return;
		updateForm.form.submit({
					url : 'integrateDemo.ered?reqCode=updateSfxm',
					waitTitle : '��ʾ',
					method : 'POST',
					waitMsg : '���ڴ�������,���Ժ�...',
					success : function(form, action) { // �ص�������2������
						Ext.MessageBox.alert('��ʾ', action.result.msg);
						updateWindow.hide();
						store.reload();
					},
					failure : function(form, action) {
						Ext.Msg
								.alert('��ʾ', '���ݱ���ʧ��,��������:'
												+ action.failureType);
					}
				});
	}
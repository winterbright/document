/**
 * �ۺ�ʵ��������ά��(�ĺ�һ)
 * 
 * @author XiongChun
 * @since 2010-11-20
 */
Ext.onReady(function() {


	// �����Զ���ǰҳ�к�
	var rownum = new Ext.grid.RowNumberer({
				header : 'NO',
				width : 28
			});

	// ������ģ��
	var cm = new Ext.grid.ColumnModel([rownum, {
		header : '��ĿID', // �б���
		dataIndex : 'xmid', // ��������:��Storeģ�Ͷ�Ӧ
		sortable : true
			// �Ƿ������
		}, {
		header : '����',
		dataIndex : 'sfdlbm',
		hidden : true, // ������
		sortable : true,
		width : 50
			// �п�
		}, {
		header : '��Ŀ����',
		dataIndex : 'xmmc',
		sortable : true
	}, {
		header : '��Ŀ�ȼ�',
		dataIndex : 'xmrj'
	}, {
		header : '��������',
		dataIndex : 'zfbl'
	}, {
		header : '���',
		dataIndex : 'gg'
	}, {
		header : '��λ',
		dataIndex : 'dw',
		width : 60
	}, {
		header : '����״̬',
		dataIndex : 'qybz',
		// ��ʾrender���÷�(����ת��,��render��<eRedG4:ext.codeRender/>��ǩ����)
		renderer : QYBZRender,
		width : 60
	}, {
		header : '����',
		dataIndex : 'jx',
		width : 60
	}, {
		header : '����',
		dataIndex : 'cd',
		width : 200
	}, {
		header : 'ҽԺ����',
		dataIndex : 'yybm'
	}, {
		header : '����ʱ��',
		dataIndex : 'ggsj'
	}]);

	/**
	 * ���ݴ洢
	 */
	var store = new Ext.data.Store({
				// ��ȡ���ݵķ�ʽ
				proxy : new Ext.data.HttpProxy({
							url : 'integrateDemo.ered?reqCode=querySfxmDatas'
						}),
				reader : new Ext.data.JsonReader({
							totalProperty : 'TOTALCOUNT', // ��¼����
							root : 'ROOT' // Json�е��б����ݸ��ڵ�
						}, [{
									name : 'xmid' // Json�е�����Keyֵ
								}, {
									name : 'sfdlbm'
								}, {
									name : 'xmmc'
								}, {
									name : 'xmrj'
								}, {
									name : 'gg'
								}, {
									name : 'dw'
								}, {
									name : 'qybz'
								}, {
									name : 'jx'
								}, {
									name : 'cd'
								}, {
									name : 'yybm'
								}, {
									name : 'ggsj'
								}, {
									name : 'zfbl'
								}])
			});

	/**
	 * ��ҳ����ʱ��Ĳ�������
	 */
	// ��ҳ����ʱ���ϲ�ѯ����
	store.on('beforeload', function() {
				this.baseParams = qForm.getForm().getValues();
			});
	// ÿҳ��ʾ��������ѡ���
	var pagesize_combo = new Ext.form.ComboBox({
				name : 'pagesize',
				triggerAction : 'all',
				mode : 'local',
				store : new Ext.data.ArrayStore({
							fields : ['value', 'text'],
							data : [[10, '10��/ҳ'], [20, '20��/ҳ'],
									[50, '50��/ҳ'], [100, '100��/ҳ'],
									[250, '250��/ҳ'], [500, '500��/ҳ']]
						}),
				valueField : 'value',
				displayField : 'text',
				value : '20',
				editable : false,
				width : 85
			});
	var number = parseInt(pagesize_combo.getValue());
	// �ı�ÿҳ��ʾ����reload����
	pagesize_combo.on("select", function(comboBox) {
				bbar.pageSize = parseInt(comboBox.getValue());
				number = parseInt(comboBox.getValue());
				store.reload({
							params : {
								start : 0,
								limit : bbar.pageSize
							}
						});
			});

	// ��ҳ������
	var bbar = new Ext.PagingToolbar({
				pageSize : number,
				store : store,
				displayInfo : true,
				displayMsg : '��ʾ{0}����{1}��,��{2}��',
				plugins : new Ext.ux.ProgressBarPager(), // ��ҳ������
				emptyMsg : "û�з��������ļ�¼",
				items : ['-', '&nbsp;&nbsp;', pagesize_combo]
			});

	// ��񹤾���
	var tbar = new Ext.Toolbar({
				items : [{
							text : '����',
							iconCls : 'addIcon',
							id : 'id_tbi_add',
							handler : function() {
								addCatalogItem();
							}
						}, {
							text : '�޸�',
							id : 'tbi_edit',
							iconCls : 'edit1Icon',
							disabled : true,
							handler : function() {
								updateCatalogItem();
							}
						}, {
							text : 'ɾ��',
							id : 'tbi_del',
							iconCls : 'deleteIcon',
							disabled : true,
							handler : function() {
								deleteCatalogItem();
							}
						}, '->', {
							text : 'ˢ��',
							iconCls : 'arrow_refreshIcon',
							handler : function() {
								store.reload();
							}
						}]
			});

	// ���ʵ��
	var grid = new Ext.grid.GridPanel({
				// ���������,Ĭ��Ϊ���壬�Ҳ�ϲ�����壬����������ʽ�����ʽΪ��������
				title : '<span class="commoncss">ҽԺ�շ���Ŀ</span>',
				height : 500,
				id : 'id_grid_sfxm',
				autoScroll : true,
				frame : true,
				region : 'center', // ��VIEWPORT����ģ�Ͷ�Ӧ���䵱center���򲼾�
				store : store, // ���ݴ洢
				stripeRows : true, // ������
				cm : cm, // ��ģ��
				tbar : tbar, // ��񹤾���
				bbar : bbar,// ��ҳ������
				viewConfig : {
	// ����������������, �����Զ���չ�Զ�ѹ��, �����������Ƚ��ٵ����
				// forceFit : true
				},
				loadMask : {
					msg : '���ڼ��ر������,���Ե�...'
				}
			});

	// ������ѡ���¼�
	grid.on('rowclick', function(pGrid, rowIndex, event) {
				Ext.getCmp('tbi_edit').enable();
				Ext.getCmp('tbi_del').enable();
			});

	grid.on('rowdblclick', function(grid, rowIndex, event) {
				updateCatalogItem();
			});





	// ����
	// �����form��Ϊcenter����Ļ�,��Height���Խ�ʧЧ��
	var viewport = new Ext.Viewport({
				layout : 'border',
				items : [qForm, grid]
			});

	/**
	 * ��ѯ��Ŀ�б�
	 */
	function querySfxmDatas() {
		var params = qForm.getForm().getValues();
		params.start = 0;
		params.limit = bbar.pageSize;
		store.load({
					params : params
				});
	}





	/**
	 * ������Ŀ
	 */
	function addCatalogItem() {
		firstWindow.show(); // ��ʾ����
	}



	/**
	 * �޸���Ŀ
	 */
	function updateCatalogItem() {
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.Msg.alert('��ʾ:', '����ѡ����Ŀ');
			return;
		}
		updateForm.getForm().loadRecord(record);
		updateWindow.show(); // ��ʾ����
	}



	/**
	 * ɾ����Ŀ
	 */
	function deleteCatalogItem() {
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.Msg.alert('��ʾ:', '����ѡ����Ŀ');
			return;
		}
		Ext.MessageBox.confirm('��ȷ��', 'ȷ��ɾ����?', function(btn, text) {
					if (btn == 'yes') {
						if (runMode == '0') {
							Ext.Msg.alert('��ʾ',
									'ϵͳ��������ʾģʽ������,���Ĳ�����ȡ��!��ģʽ��ֻ�ܽ��в�ѯ����!');
							return;
						}
						showWaitMsg();
						Ext.Ajax.request({
									url : 'integrateDemo.ered?reqCode=deleteSfxm',
									success : function(response) { // �ص�������1������
										var resultArray = Ext.util.JSON
												.decode(response.responseText);
										Ext.Msg.alert('��ʾ', resultArray.msg);
										store.reload();
									},
									failure : function(response) {
										Ext.MessageBox.alert('��ʾ', '��ɾ��ʧ��');
									},
									params : {
										xmid : record.data.xmid
									}
								});
					}
				})
	}

	/**
	 * ��ӡһ
	 */
	function printCatalog1() {
		showWaitMsg('����׼����������,���Ե�...');
		Ext.Ajax.request({
					url : 'integrateDemo.ered?reqCode=buildReportDataObject',
					success : function(response) {
						hideWaitMsg();
						doPrint('hisCatalogReport4App');
					},
					failure : function(response) {
						hideWaitMsg();
						Ext.Msg.alert('��ʾ', "׼���������ݶ���������,����!");
					}
				});
	}

});
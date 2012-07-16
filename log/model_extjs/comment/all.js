/**
 * 综合实例：数据维护(四合一)
 * 
 * @author XiongChun
 * @since 2010-11-20
 */
Ext.onReady(function() {


	// 定义自动当前页行号
	var rownum = new Ext.grid.RowNumberer({
				header : 'NO',
				width : 28
			});

	// 定义列模型
	var cm = new Ext.grid.ColumnModel([rownum, {
		header : '项目ID', // 列标题
		dataIndex : 'xmid', // 数据索引:和Store模型对应
		sortable : true
			// 是否可排序
		}, {
		header : '大类',
		dataIndex : 'sfdlbm',
		hidden : true, // 隐藏列
		sortable : true,
		width : 50
			// 列宽
		}, {
		header : '项目名称',
		dataIndex : 'xmmc',
		sortable : true
	}, {
		header : '项目热键',
		dataIndex : 'xmrj'
	}, {
		header : '报销比例',
		dataIndex : 'zfbl'
	}, {
		header : '规格',
		dataIndex : 'gg'
	}, {
		header : '单位',
		dataIndex : 'dw',
		width : 60
	}, {
		header : '启用状态',
		dataIndex : 'qybz',
		// 演示render的用法(代码转换,该render由<eRedG4:ext.codeRender/>标签生成)
		renderer : QYBZRender,
		width : 60
	}, {
		header : '剂型',
		dataIndex : 'jx',
		width : 60
	}, {
		header : '产地',
		dataIndex : 'cd',
		width : 200
	}, {
		header : '医院编码',
		dataIndex : 'yybm'
	}, {
		header : '更改时间',
		dataIndex : 'ggsj'
	}]);

	/**
	 * 数据存储
	 */
	var store = new Ext.data.Store({
				// 获取数据的方式
				proxy : new Ext.data.HttpProxy({
							url : 'integrateDemo.ered?reqCode=querySfxmDatas'
						}),
				reader : new Ext.data.JsonReader({
							totalProperty : 'TOTALCOUNT', // 记录总数
							root : 'ROOT' // Json中的列表数据根节点
						}, [{
									name : 'xmid' // Json中的属性Key值
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
	 * 翻页排序时候的参数传递
	 */
	// 翻页排序时带上查询条件
	store.on('beforeload', function() {
				this.baseParams = qForm.getForm().getValues();
			});
	// 每页显示条数下拉选择框
	var pagesize_combo = new Ext.form.ComboBox({
				name : 'pagesize',
				triggerAction : 'all',
				mode : 'local',
				store : new Ext.data.ArrayStore({
							fields : ['value', 'text'],
							data : [[10, '10条/页'], [20, '20条/页'],
									[50, '50条/页'], [100, '100条/页'],
									[250, '250条/页'], [500, '500条/页']]
						}),
				valueField : 'value',
				displayField : 'text',
				value : '20',
				editable : false,
				width : 85
			});
	var number = parseInt(pagesize_combo.getValue());
	// 改变每页显示条数reload数据
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

	// 分页工具栏
	var bbar = new Ext.PagingToolbar({
				pageSize : number,
				store : store,
				displayInfo : true,
				displayMsg : '显示{0}条到{1}条,共{2}条',
				plugins : new Ext.ux.ProgressBarPager(), // 分页进度条
				emptyMsg : "没有符合条件的记录",
				items : ['-', '&nbsp;&nbsp;', pagesize_combo]
			});

	// 表格工具栏
	var tbar = new Ext.Toolbar({
				items : [{
							text : '新增',
							iconCls : 'addIcon',
							id : 'id_tbi_add',
							handler : function() {
								addCatalogItem();
							}
						}, {
							text : '修改',
							id : 'tbi_edit',
							iconCls : 'edit1Icon',
							disabled : true,
							handler : function() {
								updateCatalogItem();
							}
						}, {
							text : '删除',
							id : 'tbi_del',
							iconCls : 'deleteIcon',
							disabled : true,
							handler : function() {
								deleteCatalogItem();
							}
						}, '->', {
							text : '刷新',
							iconCls : 'arrow_refreshIcon',
							handler : function() {
								store.reload();
							}
						}]
			});

	// 表格实例
	var grid = new Ext.grid.GridPanel({
				// 表格面板标题,默认为粗体，我不喜欢粗体，这里设置样式将其格式为正常字体
				title : '<span class="commoncss">医院收费项目</span>',
				height : 500,
				id : 'id_grid_sfxm',
				autoScroll : true,
				frame : true,
				region : 'center', // 和VIEWPORT布局模型对应，充当center区域布局
				store : store, // 数据存储
				stripeRows : true, // 斑马线
				cm : cm, // 列模型
				tbar : tbar, // 表格工具栏
				bbar : bbar,// 分页工具栏
				viewConfig : {
	// 不产横向生滚动条, 各列自动扩展自动压缩, 适用于列数比较少的情况
				// forceFit : true
				},
				loadMask : {
					msg : '正在加载表格数据,请稍等...'
				}
			});

	// 监听行选中事件
	grid.on('rowclick', function(pGrid, rowIndex, event) {
				Ext.getCmp('tbi_edit').enable();
				Ext.getCmp('tbi_del').enable();
			});

	grid.on('rowdblclick', function(grid, rowIndex, event) {
				updateCatalogItem();
			});





	// 布局
	// 如果把form作为center区域的话,其Height属性将失效。
	var viewport = new Ext.Viewport({
				layout : 'border',
				items : [qForm, grid]
			});

	/**
	 * 查询项目列表
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
	 * 新增项目
	 */
	function addCatalogItem() {
		firstWindow.show(); // 显示窗口
	}



	/**
	 * 修改项目
	 */
	function updateCatalogItem() {
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.Msg.alert('提示:', '请先选中项目');
			return;
		}
		updateForm.getForm().loadRecord(record);
		updateWindow.show(); // 显示窗口
	}



	/**
	 * 删除项目
	 */
	function deleteCatalogItem() {
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.Msg.alert('提示:', '请先选中项目');
			return;
		}
		Ext.MessageBox.confirm('请确认', '确认删除吗?', function(btn, text) {
					if (btn == 'yes') {
						if (runMode == '0') {
							Ext.Msg.alert('提示',
									'系统正处于演示模式下运行,您的操作被取消!该模式下只能进行查询操作!');
							return;
						}
						showWaitMsg();
						Ext.Ajax.request({
									url : 'integrateDemo.ered?reqCode=deleteSfxm',
									success : function(response) { // 回调函数有1个参数
										var resultArray = Ext.util.JSON
												.decode(response.responseText);
										Ext.Msg.alert('提示', resultArray.msg);
										store.reload();
									},
									failure : function(response) {
										Ext.MessageBox.alert('提示', '数删除失败');
									},
									params : {
										xmid : record.data.xmid
									}
								});
					}
				})
	}

	/**
	 * 打印一
	 */
	function printCatalog1() {
		showWaitMsg('正在准备报表数据,请稍等...');
		Ext.Ajax.request({
					url : 'integrateDemo.ered?reqCode=buildReportDataObject',
					success : function(response) {
						hideWaitMsg();
						doPrint('hisCatalogReport4App');
					},
					failure : function(response) {
						hideWaitMsg();
						Ext.Msg.alert('提示', "准备报表数据对象发生错误,请检查!");
					}
				});
	}

});
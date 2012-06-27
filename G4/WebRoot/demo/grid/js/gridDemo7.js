/**
 * 表格综合示例
 * 
 * @author XiongChun
 * @since 2010-10-20
 */
Ext.onReady(function() {

	var group1 = [{}, {
				header : '分组1-1a',
				colspan : 4,
				align : 'center'
			}, {}, {
				header : '分组1-3',
				colspan : 5,
				align : 'center'
			}];

	var group2 = [{}, {
				header : '分组2-1',
				colspan : 2,
				align : 'center'
			}, {
				header : '分组2-2',
				colspan : 2,
				align : 'center'
			}, {
				header : '单位',
				rowmerge:true,
				domid:'group_dw',
				align : 'center'
			}, {
				header : '分组2-3',
				colspan : 2,
				align : 'center'
			}, {
				header : '分组2-4',
				colspan : 3,
				align : 'center'
			}];
    /*
	var group = new Ext.ux.grid.ColumnHeaderGroup({
				rows : [group1, group2]
			});
   */

	var group = new Ext.ux.plugins.GroupHeaderGrid({
				rows : [group1, group2]
			});

	// 定义自动当前页行号
	var rownum = new Ext.grid.RowNumberer({
				header : 'NO',
				width : 28
			});
	// 复选框
	var sm = new Ext.grid.CheckboxSelectionModel();

	// 定义列模型
	var cm = new Ext.grid.ColumnModel([rownum, {
		header : '项目ID', // 列标题
		dataIndex : 'xmid', // 数据索引:和Store模型对应
		sortable : true
			// 是否可排序
		}, {
		header : '项目名称',
		dataIndex : 'xmmc',
		sortable : true,
		width : 200
	}, {
		header : '项目热键',
		dataIndex : 'xmrj'
	}, {
		header : '规格',
		dataIndex : 'gg'
	}, {
		dataIndex : 'dw',
		align:'center',
		fixed:true,
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
							url : 'gridDemo.ered?reqCode=querySfxmDatas'
						}),
				// 数据读取器
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
								}])
			});

	/**
	 * 翻页排序时候的参数传递
	 */
	// 翻页排序时带上查询条件
	store.on('beforeload', function() {
				this.baseParams = {
					xmmc : Ext.getCmp('xmmc').getValue()
				};
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
							xtype : 'textfield',
							id : 'xmmc',
							name : 'xmmc',
							emptyText : '请输入项目名称',
							width : 150,
							enableKeyEvents : true,
							// 响应回车键
							listeners : {
								specialkey : function(field, e) {
									if (e.getKey() == Ext.EventObject.ENTER) {
										queryCatalogItem();
									}
								}
							}
						}, {
							text : '查询',
							iconCls : 'page_findIcon',
							handler : function() {
								queryCatalogItem();
							}
						}, {
							text : '刷新',
							iconCls : 'page_refreshIcon',
							handler : function() {
								store.reload();
							}
						},'->' ,{
							text : '重设列标题',
							iconCls : 'acceptIcon',
							handler : function() {
								cm.setColumnHeader('2','开天辟地');
							}
						}, {
							text : '重设分组列标题',
							iconCls : 'acceptIcon',
							handler : function() {
								Ext.getDom('group_dw').innerHTML = '开天辟地';
							}
						}]
			});
			

	// 表格实例
	var grid = new Ext.grid.GridPanel({
				// 表格面板标题,默认为粗体，我不喜欢粗体，这里设置样式将其格式为正常字体
				title : '<span class="commoncss">表格综合演示七(表头分组支持)</span>',
				height : 500,
				frame : true,
				autoScroll : true,
				region : 'center', // 和VIEWPORT布局模型对应，充当center区域布局
				store : store, // 数据存储
				stripeRows : true, // 斑马线
				cm : cm, // 列模型
				tbar : tbar, // 表格工具栏
				bbar : bbar,// 分页工具栏
				plugins : group,
				viewConfig : {
	// 不产横向生滚动条, 各列自动扩展自动压缩, 适用于列数比较少的情况
				// forceFit : true
				},
				loadMask : {
					msg : '正在加载表格数据,请稍等...'
				}
			});

	// 页面初始自动查询数据
	// store.load({params : {start : 0,limit : bbar.pageSize}});

	// 布局模型
	var viewport = new Ext.Viewport({
				layout : 'border',
				items : [grid]
			});

	// 查询表格数据
	function queryCatalogItem() {
		store.load({
					params : {
						start : 0,
						limit : bbar.pageSize,
						xmmc : Ext.getCmp('xmmc').getValue()
					}
				});
	}

});
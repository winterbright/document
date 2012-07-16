<#import "store.ftl" as st>
<#import "function.ftl" as fun>
<#macro gridModel grid>

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
		//plugins : new Ext.ux.ProgressBarPager(), // 分页进度条
		emptyMsg : "没有符合条件的记录",
		items : ['-', '&nbsp;&nbsp;', pagesize_combo]
	});

	// 定义自动当前页行号
	var rownum = new Ext.grid.RowNumberer({
		header : 'NO',
		width : 28
	});
	// 定义列模型
	var cm = new Ext.grid.ColumnModel({
		defaults: {
            sortable: true // columns are not sortable by default           
        },
		columns: [rownum, <#list grid.columns as col>
		{
			header : '${col.header}', 
			<#if (col.id??)>id : ${col.id},
			</#if><#if (col.sortable??)>sortable : ${col.sortable},
			</#if><#if (col.align??)>align : ${col.align},
			</#if><#if (col.hidden??)>hidden : ${col.hidden},
			</#if><#if (col.width??)>width : ${col.width},
			</#if>dataIndex : '${col.dataIndex}'
		}<#if col_has_next>,</#if></#list>
	]});
	
	<#if grid.tbar??>
	// 表格工具栏
	var tbar = new Ext.Toolbar({
		items : [<#list grid.tbar.buttons as button>{
			text : '${button.text}',
			<#if button.iconCls??>iconCls : '${button.iconCls}',
			</#if>handler : function() {
				<#if button.handler??>${button.handler}();</#if>
			}
		}<#if button_has_next>,'-',</#if></#list>]
	});</#if>
	
	<@st.storeModel grid.store></@st.storeModel>
	
	// 表格实例
	var grid = new Ext.grid.GridPanel({
		<#if grid.title??>title : '<span class="commoncss">${grid.title}</span>',
		</#if><#if grid.width??>width : 600,
		</#if><#if grid.height??>height : 500,
		</#if>frame : true,
		autoScroll : true,
		<#if grid.renderTo??>renderTo : '${grid.renderTo}',
		</#if>region : 'center', // 和VIEWPORT布局模型对应，充当center区域布局
		store : store, // 数据存储
		stripeRows : true, // 斑马线
		cm : cm, // 列模型
		<#if grid.tbar??>tbar : tbar, // 表格工具栏
		</#if>bbar : bbar,// 分页工具栏
		viewConfig : {
		// forceFit : true
		},
		loadMask : {
			msg : '正在加载表格数据,请稍等...'
		}
	});
	
	<#if grid.funs??><#list grid.funs as fun1>
	<@fun.funModel fun1></@fun.funModel>
	</#list></#if>
</#macro>
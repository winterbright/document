<#macro windowModel win>
	var ${win.name} = new Ext.Window({
		title : '<span class="commoncss">${win.title}<span>',  // 窗口标题
		layout : 'fit', // 设置窗口布局模式
		width : 600, // 窗口宽度
		height : 260, // 窗口高度
		closable : true, // 是否可关闭
		collapsible : true, // 是否可收缩
		maximizable : true, // 设置是否可以最大化
		border : false, // 边框线设置
		constrain : true, // 设置窗口是否可以溢出父容器
		animateTarget : Ext.getBody(),
		pageY : 20, // 页面定位Y坐标
		pageX : document.body.clientWidth / 2 - 600 / 2, // 页面定位X坐标
		items : [${win.items}], // 嵌入的表单面板
		buttons : [<#list win.buttons as button>{
			text : '${button.text}',
			iconCls : '${button.iconCls}',
			handler : function() {
				${button.handler}
			}
		}<#if button_has_next>, </#if></#list>]
	});
</#macro>

<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/common/include/taglib.jsp"%>
<eRedG4:html title="FCF_2d柱状图" fcfEnabled="true">
<eRedG4:body>
	<eRedG4:flashReport type="2DC" dataVar="xmlString" id="my2DcChart" align="center"
		visible="false" />
	<eRedG4:div key="my2DcChart_panel_div" />
</eRedG4:body>
<script language="JavaScript">
Ext.onReady(function() {
	 var panel = new Ext.Panel({
        contentEl:'my2DcChart_div',
        applyTo:'my2DcChart_panel_div'
        });
     
		var window = new Ext.Window({
			layout : 'fit',
			width : 570,
			height : 390,
			resizable : false,
			draggable : true,
			closeAction : 'hide',
			title : '<span class="commoncss">Google软件2010年月度销售业绩图表</span>',
			collapsible : true,
			titleCollapse : false,
			//下拉层的动画效果必须关闭,否则将出现Flash图标下拉动画过场异常的现象
			animCollapse : false,
			maximizable : true,
			border : false,
			closable : false,
			animateTarget : Ext.getBody(),
			constrain : true,
			items : [panel]
		});

     window.show();
});
</script>
</eRedG4:html>
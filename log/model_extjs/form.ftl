<#import "field.ftl" as fm>
<#macro formModel form>
	var ${form.name} = new Ext.form.FormPanel({
		<#if form.region??>region : '${form.region}',
		</#if>title : '<span class="commoncss">${form.title}<span>',
		collapsible : false,
		border : true,
		labelWidth : 50, // 标签宽度
		// frame : true, //是否渲染表单面板背景色
		labelAlign : 'right', // 标签对齐方式
		bodyStyle : 'padding:5 5 0', // 表单元素和表单面板的边距
		buttonAlign : 'center',
		<#if form.height??>height : ${form.height},
		</#if>items : [<#list form.columns as column>{
			layout : 'column',
			border : false,
			items : [<#list column.items as item>{
				columnWidth : ${item.colWidth},
				layout : 'form',
				labelWidth : ${item.labelWidth}, 
				defaultType : 'textfield',
				border : false,
				items : [{
					<#if item.field??><@fm.fieldModel item.field '					'></@fm.fieldModel></#if>
				}]
			}<#if item_has_next>, </#if></#list>]
		}<#if column_has_next>, </#if></#list>],
		buttons : [<#list form.buttons as button>{
			text : '${button.text}',
			handler : function() {
				<#if button.handler??>${button.handler}();</#if>
			}
		}<#if button_has_next>,</#if></#list>]
	});
</#macro>



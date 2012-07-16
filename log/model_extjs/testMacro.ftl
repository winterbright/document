<#import "grid3.ftl" as gri>
<#import "form.ftl" as fo>
Ext.onReady(function(){

	var items = [];

	<#if form??>
	<@fo.formModel form></@fo.formModel>
	items.push(${form.name});
	</#if>
	
	<@gri.gridModel grid></@gri.gridModel>
	items.push(${grid.name});
	
	var viewport = new Ext.Viewport({
		layout : 'border',
		items : items
	});
	
});

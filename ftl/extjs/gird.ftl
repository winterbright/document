// create the Grid
    var grid = Ext.create('Ext.grid.Panel', {
        store: store,
        stateful: true,
        collapsible: true,
        multiSelect: true,
        stateId: 'stateGrid',
        columns: [
			<#list columns as column>
            {
                text: '${column.text}',
                flex: 1,
                sortable: false,
				<#if (column.renderer??)>
				renderer : '${column.renderer}',
				</#if>
				<#if (column.editor??)>
				editor: {
					<#if column.editor.xtype??>
					xtype: '${column.editor.xtype}'
					</#if>
					<#if column.editor.allowBlank??>
					,allowBlank: ${column.editor.allowBlank}
					</#if>
				},
				</#if>
				dataIndex: '${column.dataIndex}'
            }<#if column_has_next>,</#if>
			</#list>
        ],
		dockedItems: [
			<#list dockedItems as item>
			{
				xtype: 'toolbar',
				<#if item.dock??>
				dock: '${item.dock}',
				</#if>
				<#if item.ui??>
				ui: '${item.ui}',
				</#if>
				<#if item.layout??>
				layout: {
					pack: '${item.layout}'
				},
				</#if>
				items: [
				<#list item.items as toolItem>
				{
					<#if toolItem.itemId??>
					itemId: '${toolItem.itemId}',
					</#if>
					<#if toolItem.minWidth??>
					minWidth: ${toolItem.minWidth},
					</#if>
					<#if toolItem.tooltip??>
					tooltip: '${toolItem.tooltip}',
					</#if>
					<#if toolItem.iconCls??>
					iconCls: '${toolItem.iconCls}',
					</#if>
					<#if toolItem.handler??>
					handler: ${toolItem.handler},
					</#if>
					<#if toolItem.disabled??>
					disabled: ${toolItem.disabled},
					</#if>
					text: '${toolItem.text}'
				}<#if toolItem_has_next>,</#if>
				</#list>
				]
			}<#if item_has_next>,'-',</#if>
			</#list>
		],
		<#if plugins??>
		plugins: [${plugins}],
		</#if>
        height: 350,
        width: 600,
        title: '${title}',
        renderTo: '${renderTo}',
        viewConfig: {
            stripeRows: true,
            enableTextSelection: true
        }
    });
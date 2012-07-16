<#macro storeModel store>
	/**
	 * 数据存储
	 */
	var store = new Ext.data.Store({
		// 获取数据的方式
		proxy : new Ext.data.HttpProxy({
			url : '${store.url}'
		}),
		reader : new Ext.data.JsonReader({
			totalProperty : 'TOTALCOUNT', // 记录总数
			root : 'ROOT' // Json中的列表数据根节点
		}, [<#list store.keys as key>{
			name : '${key}'
		}<#if key_has_next>, </#if></#list>])
	});
</#macro>
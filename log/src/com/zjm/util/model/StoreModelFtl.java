package com.zjm.util.model;

import java.util.List;

/**
 * @alias
 * @author zjm
 *
 * 2012-7-9
 */
public class StoreModelFtl {
	
	private String id;
	private String name;
	private String url;
	private List<String> keys;
	
	private String pageSize;
	private String buffered;
	private String purgePageCount;
	private String autoLoad;
	
	//Ext.data.ArrayStore
	private String model;	
	private String data;	

	private String proxy;	//type:'ajax'; url:''; reader:{type:'xml';record:'Item';idProperty:'ASIN';totalProperty:'total'}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

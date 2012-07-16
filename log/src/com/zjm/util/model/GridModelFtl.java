package com.zjm.util.model;

import java.util.List;

/**
 * @alias
 * @author zjm
 *
 * 2012-7-9
 */
public class GridModelFtl extends PanelModelFtl{

	private StoreModelFtl store;
	private List<ColumnModelFtl> columns;
	
	public List<ColumnModelFtl> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnModelFtl> columns) {
		this.columns = columns;
	}

	public StoreModelFtl getStore() {
		return store;
	}

	public void setStore(StoreModelFtl store) {
		this.store = store;
	}
	
}

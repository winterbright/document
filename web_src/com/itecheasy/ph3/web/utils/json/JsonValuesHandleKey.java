package com.itecheasy.ph3.web.utils.json;

/**
 * json 根据key解释
 * */
public abstract class JsonValuesHandleKey implements JsonValuesHandle{

	private String valKey;

	public JsonValuesHandleKey(String valKey){
		this.valKey=valKey;
	}
	
	public String getValKey() {
		return valKey;
	}
		
}

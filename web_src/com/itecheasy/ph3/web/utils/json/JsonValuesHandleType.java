package com.itecheasy.ph3.web.utils.json;

/**
 * json 根据类型解释
 * */
public abstract class JsonValuesHandleType implements JsonValuesHandle{

	private Class keyTypeClazz;
	
	public JsonValuesHandleType(Class keyTypeClazz){
		this.keyTypeClazz=keyTypeClazz;
	}
	
	public Class getKeyTypeClazz() {
		return keyTypeClazz;
	}
}

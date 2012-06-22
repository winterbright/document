package com.itecheasy.ph3.web.utils.json;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import net.sf.json.JsonConfig;

/**
 * json 根据key解释日期对象为 yyyy-MM-dd HH：mm：ss
 * */
public class JsonDateHHmmssKey extends JsonValuesHandleKey{
 
	public JsonDateHHmmssKey(String valKey) {
		super(valKey);
	}
	
	public Object handleVal(String key, Object value, JsonConfig jc) {
		if(getValKey().equals(key)){
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return df.format(value);
		}
		return null;
	}

}

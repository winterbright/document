package com.itecheasy.ph3.web.utils.json;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import net.sf.json.JsonConfig;

/**
 * json 根据key解释日期对象为 yyyy-MM-dd
 */
public class JsonDateHandleKey extends JsonValuesHandleKey {

	public JsonDateHandleKey(String valKey) {
		super(valKey);
	}

	public Object handleVal(String key, Object value, JsonConfig jc) {
		if (getValKey().equals(key)) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			return df.format(value);
		}
		return null;
	}

}

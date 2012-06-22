package com.itecheasy.ph3.web.utils.json;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JsonConfig;

import com.itecheasy.common.util.DateUtils;

/**
 * json 根据类型解释日期对象为 yyyy-MM-dd
 * */
public class JsonDateHandleType extends JsonValuesHandleType{

	public JsonDateHandleType() {
		super(Date.class);
	}

	public Object handleVal(String key, Object value, JsonConfig jc) {
		 if (value instanceof Date) {
			 DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			return df.format(value);
		}
		return null;
	}

}

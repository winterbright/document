package com.itecheasy.ph3.web.utils.json;

import net.sf.json.JsonConfig;
/**
 * json 解释的接口
 * */
public interface JsonValuesHandle {
	public Object handleVal(String key, Object value ,JsonConfig jc);
}

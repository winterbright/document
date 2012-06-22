package com.itecheasy.ph3.web.utils.json;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

public class JsonUtil {

	/**
	 * @param Object obj    需要转换json的对象  
	 * @param Class clazz   转换json对象的类型 ，null
	 * @param String[] excludes 不需要转换的属性 ，null
	 * @param defaultJsonHandler 需要对json转换属性的转换处理对象， null
	 * */
	public static String obj2String(Object obj ,Class clazz , String[] excludes ,final DefaultJsonHandler defaultJsonHandler ){
		JsonConfig jc = new JsonConfig();
		if(excludes!=null && excludes.length>0){
			jc.setExcludes(excludes);
		}
		if(defaultJsonHandler!=null){
			  JsonValueProcessor jvp = new JsonValueProcessor() {
					public Object processArrayValue(Object value, JsonConfig jc) {
						return null;
					}
					
					public Object processObjectValue(String key, Object value, JsonConfig jc) {
						return defaultJsonHandler.handleVal(key, value, jc);
					}
				};
				
				for(String key: defaultJsonHandler.getKeyList()){
					 jc.registerJsonValueProcessor(key, jvp);
				}
				
				if(clazz!=null){
					for(Map<String, Class> map:defaultJsonHandler.getTypeList()){
						jc.registerJsonValueProcessor(clazz, map.get("keyType"), jvp);
					}
				}
		}
		
		return JSONObject.fromObject(obj, jc).toString();
	}
	
	/**
	 * @param List list    需要转换json的数组对象
	 * @param Class clazz   转换json对象的类型 ，null
	 * @param String[] excludes 不需要转换的属性 ，null
	 * @param defaultJsonHandler 需要对json转换属性的转换处理对象， null
	 * */
	public static String obj2String(List list ,Class clazz , String[] excludes ,final DefaultJsonHandler defaultJsonHandler ){
		JsonConfig jc = new JsonConfig();
		if(excludes!=null && excludes.length>0){
			jc.setExcludes(excludes);
		}
		if(defaultJsonHandler!=null){
			  JsonValueProcessor jvp = new JsonValueProcessor() {
					public Object processArrayValue(Object value, JsonConfig jc) {
						return null;
					}
					
					public Object processObjectValue(String key, Object value, JsonConfig jc) {
						return defaultJsonHandler.handleVal(key, value, jc);
					}
				};
				
				for(String key: defaultJsonHandler.getKeyList()){
					 jc.registerJsonValueProcessor(key, jvp);
				}
				
				if(clazz!=null){
					for(Map<String, Class> map:defaultJsonHandler.getTypeList()){
						jc.registerJsonValueProcessor(clazz, map.get("keyType"), jvp);
					}
				}
		}
		return JSONArray.fromObject(list, jc).toString();
	}
	
	public static Object string2obj(String jsonString ,Class clazz){
		if(jsonString==null ||"".equals(jsonString)) return null;
		JSONObject json=JSONObject.fromObject(jsonString);
		Object obj= JSONObject.toBean(json ,Map.class);
		return obj;
	}
}

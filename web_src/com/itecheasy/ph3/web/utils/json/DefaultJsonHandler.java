package com.itecheasy.ph3.web.utils.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import net.sf.json.JsonConfig;

public class DefaultJsonHandler implements JsonValuesHandle{

	private List<String> keyList=new ArrayList<String>();
	private List<Map<String, Class>> typeList=new ArrayList<Map<String,Class>>();
	private List<JsonValuesHandle> jvhList=new ArrayList<JsonValuesHandle>();
	

	public void addJsonHandlerKey(JsonValuesHandleKey jvhk){
		keyList.add(jvhk.getValKey());
		jvhList.add(jvhk);
	}
	
	public void addJsonHandlerType(JsonValuesHandleType jvht){
		Map<String, Class> typeMap=new HashMap<String, Class>();
		typeMap.put("keyType", jvht.getKeyTypeClazz());
		typeList.add(typeMap);
		jvhList.add(jvht);
	}
	
	public List<String> getKeyList() {
		return keyList;
	}

	public List<Map<String, Class>> getTypeList() {
		return typeList;
	}


	public Object handleVal(String key, Object value, JsonConfig jc) {
		for(JsonValuesHandle jvh:jvhList){
			 Object retStr= jvh.handleVal(key, value, jc);
			 if(retStr!=null) return retStr;
		}
		return value;
	}

	
}

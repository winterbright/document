package com.zjm.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @alias
 * @author zjm
 *
 * 2012-3-29
 */
public class Z8Util4PO {


	/**
	 * @param o
	 * @param b2p
	 * @return
	 * @throws ClassNotFoundException
	 * @throws Exception 
	 * @throws InstantiationException 
	 */
	private static Map<String, Object> creBody(Class o,boolean b2p,List<Object> list,Map<String, String> importMap)
			throws ClassNotFoundException, InstantiationException, Exception {
		Map<String, Object> beanMap = new HashMap<String, Object>();
		String boName = o.getName();
		if(boName.equals("java.util.List")){
//			beanMap.put("list", flag);
		}
		
		String bobeanName = boName.substring(boName.lastIndexOf(".")+1,boName.length());
		String pobeanName = bobeanName+"PO";
		String poName = boName.substring(0,boName.lastIndexOf("."));
		poName = poName.substring(0,poName.lastIndexOf("."));
		poName = poName + ".po." + pobeanName;
		
		importMap.put(boName, boName);
		importMap.put(poName, poName);
		
		String methodName = "";
		if(b2p){
			methodName = "converBO2PO";
		}else{
			String temp = "";
			temp = boName;
			boName = poName;
			poName = temp;
			temp = bobeanName;
			bobeanName = pobeanName;
			pobeanName = temp;
			methodName = "converPO2BO";
		}
		
		//创建主体
		Class bo = Class.forName(boName);
		Class po = Class.forName(poName);
		Field[] pofield = po.getDeclaredFields();
		Field[] bofield = bo.getDeclaredFields();
		Map<String, Field> map = new HashMap<String, Field>();
		for(Field field : bofield){
			if(!Modifier.isFinal(field.getModifiers()))
			map.put(field.getName(), field);
		}
		List<Map<String, Object>> body = new ArrayList<Map<String, Object>>();
		Map<Class, Field> checkMap = new HashMap<Class, Field>();
		for(Field field : pofield){
			if(map.containsKey(field.getName())){
				Map<String, Object> m = new HashMap<String, Object>();
				if(!field.getType().equals(map.get(field.getName()).getType())&&!checkMap.containsKey(field.getType())){
					if(b2p){
						creBody(map.get(field.getName()).getType(),b2p,list,importMap);
					}else{
						creBody(field.getType(),b2p,list,importMap);
					}
					checkMap.put(field.getType(), field);
					m.put("caveMathod", true);
				}else if(field.getType().getName().equals("java.util.List")){
					String fieldType = "";
					if(b2p){
						fieldType = map.get(field.getName()).getGenericType().toString();
					}else{
						fieldType = field.getGenericType().toString();
					}
					Class c = getClass(fieldType);
					Map<String, Object> listMap = creBody(c,b2p,list,importMap);
					listMap.put("list", true);
					m.put("caveMathod", true);
				}else if(checkMap.containsKey(field.getType())){
					m.put("caveMathod", true);
				}else{
					m.put("caveMathod", false);
				}
				m.put("method", field.getName());
				body.add(m);
			}
		}
		//封装内容
		beanMap.put("param", bobeanName);
		beanMap.put("returnObject", pobeanName);
		beanMap.put("regin", "private");
		beanMap.put("methodName", methodName);
		beanMap.put("body", body);
		list.add(beanMap);
		return beanMap;
	}

	/**
	 * @param fieldType
	 * @return
	 * @throws ClassNotFoundException
	 */
	private static Class getClass(String fieldType) throws ClassNotFoundException {
		if(fieldType.contains("<")){
			fieldType = fieldType.substring(fieldType.indexOf("<")+1,fieldType.indexOf(">"));
		}
		Class c = Class.forName(fieldType);
		return c;
	}
	
	public static Map<String, Object> creBean(Map<Class, Integer> map) throws Exception{
		Map<String, Object> impMap = new HashMap<String, Object>();
		Map<String, String> impSubMap = new HashMap<String, String>();
		List<Object> list = new ArrayList<Object>();
		Set<Class> set = map.keySet();
		for(Iterator<Class> it = set.iterator(); it.hasNext();){
			Class c = it.next();
			Integer type = map.get(c);
			if(type == 0){
				creBody(c,true,list,impSubMap);
				creBody(c,false,list,impSubMap);
			}else if(type == 1){
				creBody(c,true,list,impSubMap);
			}else if(type == 2){
				creBody(c,false,list,impSubMap);
			}
		}
		impMap.put("import", impSubMap);
		impMap.put("bean", list);
		return impMap;
	}

	public static List<String> creQuery(Class c) throws Exception{
		List<String> list = new ArrayList<String>();
		Field[] fields = c.getDeclaredFields();
		for (Field field : fields) {
			list.add(field.getName());
		}
		return list;
	}
	
//	/**
//	 * @param boName
//	 * @param bobeanName
//	 * @param pobeanName
//	 * @param poName
//	 * @return
//	 * @throws ClassNotFoundException
//	 */
//	private static List<String> creBody(String boName, String bobeanName, String pobeanName, String poName,List<Object> list)
//			throws ClassNotFoundException {
//
//		return body;
//	}
//	
//	/**
//	 * @return
//	 * @throws Exception 
//	 */
//	public static Map<String, Object> getMethodBO2PO(Class o) throws Exception {
//		Map<String, Object> map = Z8Util4PO.creBody(o,true);
//		return map;
//	}
	
//	public static List<Object> creB2PBody(List<Class> list) throws Exception{
//		List<Object> body = new ArrayList<Object>();
//		for(Class c : list){
//			body.add(getMethodBO2PO(c));
//		}
//		return body;
//	}
//	
//	public static List<Object> creB2PBody(Class[] list) throws Exception{
//		List<Object> body = new ArrayList<Object>();
//		for(Class c : list){
//			body.add(getMethodBO2PO(c));
//		}
//		return body;
//	}
	
//	public static List<String> creP2BBody(List<Class> list) throws Exception{
//		List<String> body = new ArrayList<String>();
//		for(Class c : list){
//			body.add(getMethodPO2BO(c));
//		}
//		return body;
//	}
//	
//	public static List<String> creP2BBody(Class[] list) throws Exception{
//		List<String> body = new ArrayList<String>();
//		for(Class c : list){
//			body.add(getMethodPO2BO(c));
//		}
//		return body;
//	}
	
//	/**
//	 * @return
//	 * @throws Exception 
//	 */
//	public static String getMethodPO2BO(Class o) throws Exception {
//		Map<String, Object> map = Z8Util4PO.creBody(o,false);
//		OutputStream os = new ByteArrayOutputStream();
//		Z8Util.cre(map, "method.ftl", os);
//		String sa = os.toString();
//		os.close();
//		return sa;
//	}
	
	
//	/**
//	 * @param <T>
//	 * @param <T>
//	 * @return
//	 * @throws Exception 
//	 */
//	public static String getMethodListBO2PO(Class o) throws Exception {
//		Map<String, Object> map = Z8Util4PO.creBody(o,true);
//		map.put("body", getMethodBO2PO(o));
//		OutputStream os = new ByteArrayOutputStream();
//		Z8Util.cre(map, "methodList.ftl", os);
//		String sa = os.toString();
//		os.close();
//		return sa;
//	}
	
}

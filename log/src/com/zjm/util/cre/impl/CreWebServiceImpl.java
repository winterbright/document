package com.zjm.util.cre.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.signature.SignatureVisitor;

import com.zjm.util.Z8Util;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;


/**
 * @alias
 * @author zjm
 *
 * 2012-3-31
 */
public class CreWebServiceImpl {

	public void pp(int ac){
		System.out.println("ac");
	}
	
	public static void main(String[] args) throws Exception {
		CreWebServiceImpl.cre(CreWebServiceImpl.class);
//		CreWebServiceImpl.cre(Test.class);
	}
	
	public static void cre(Class c) throws Exception{
//		if(!c.isInterface())return;
		ImplBeanTemp bean = getBean(c);
		Configuration cfg = new Configuration();
		try {
			cfg.setDirectoryForTemplateLoading(new File("E:/fre"));
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			Template tem = cfg.getTemplate("webServiceImpl.ftl");
			Writer out = new OutputStreamWriter(new FileOutputStream("src/com/zjm/util/cre/impl/CopyDelImpl.java"));
			tem.process(bean, out);
			out.flush();
			System.out.println("create file success!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param c
	 * @throws Exception
	 */
	private static ImplBeanTemp getBean(Class c) throws Exception {
		ImplBeanTemp bean = new ImplBeanTemp();
		bean.setBeanName(c.getSimpleName()+"Impl");
		bean.setBeanPackage(c.getPackage().getName());
		bean.setBeanModifier("public");
		bean.setBeanType("class");
		bean.setInterfaceName(c.getSimpleName());
		FieldTemp field = new FieldTemp();
		field.setFieldName(c.getSimpleName().replace("WebService", "Service"));
//		field.setFieldDisName(Z8Util.lowFirstName(field.getFieldName()));
		field.setFieldModifier("private");
		List<FieldTemp> fields = new ArrayList<FieldTemp>();
		fields.add(field);
		bean.setBeanFields(fields);
		List<MethodTemp> methods = new ArrayList<MethodTemp>();
		getServiceMethods(c,methods);
		bean.setBeanMethods(methods);
		return bean;
	}
	
	private static void getServiceMethods(Class c, List<MethodTemp> methods) throws Exception{
		ClassPool cp = ClassPool.getDefault();
		CtClass cc = cp.get(c.getName());
		CtMethod[] cms = cc.getDeclaredMethods();
		for (CtMethod cm : cms) {
			MethodTemp mt = new MethodTemp();
			MethodInfo mi = cm.getMethodInfo();
			StringBuffer sb = new StringBuffer();
			CodeAttribute ca = mi.getCodeAttribute();
			LocalVariableAttribute attr = (LocalVariableAttribute) ca.getAttribute(LocalVariableAttribute.tag); 
			String[] paramNames = new String[cm.getParameterTypes().length];
			int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;  
			for (int i = 0; i < paramNames.length; i++)  
				paramNames[i] = mi.getName()+" "+attr.variableName(i + pos);      
			//paramNames即参数名
			for (int i = 0; i < paramNames.length; i++) {
				if(i>0)sb.append(", ");
				sb.append(paramNames[i]);
			}
			mt.setMethodModifier("public");
			mt.setMethodName(cm.getName());
			mt.setMethodParams(sb.toString());
			mt.setMethodBody("// TODO Auto-generated method stub");
			mt.setMethodReturnClass(cm.getReturnType().getName());
			methods.add(mt);
		}
		
			
			
	}
	
}

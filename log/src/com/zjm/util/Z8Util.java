package com.zjm.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * @alias
 * @author zjm
 *
 * 2012-3-29
 */
public class Z8Util {


	
	
	/**
	 * 首字母转换为小写
	 * @param s
	 * @return
	 */
	public static String lowFirstName(String fuck,String s){
		return s.substring(0,1).toLowerCase()+s.substring(1,s.length());
	}
	
	public static void main(String[] args) throws Exception, NoSuchFieldException {
		ClassPool cp = ClassPool.getDefault();
		CtClass cc = cp.get("com.zjm.util.Z8Util");
		CtMethod cm = cc.getDeclaredMethod("lowFirstName");
		MethodInfo mi = cm.getMethodInfo();
		CodeAttribute ca = mi.getCodeAttribute();
		LocalVariableAttribute attr = (LocalVariableAttribute) ca.getAttribute(LocalVariableAttribute.tag); 
		String[] paramNames = new String[cm.getParameterTypes().length];
		int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;  
		for (int i = 0; i < paramNames.length; i++)  
		    paramNames[i] = attr.variableName(i + pos);      
		//paramNames即参数名
		for (int i = 0; i < paramNames.length; i++) {
		    System.out.println(paramNames[i]);
		}
	}
	
	public void pri(String abc){
		System.out.println(abc);
	}
	
    private static void printAccessFlags(ClassFile cf) {
        String s = "";
        int flags = cf.getAccessFlags();
        if ((flags & 0x0001)!=0) s += "public ";
        if ((flags & 0x0010)!=0) s += "final ";
        if ((flags & 0x0020)!=0) s += "super ";
        if ((flags & 0x0200)!=0) s += "interface ";
        if ((flags & 0x0400)!=0) s += "abstract ";
        out("access_flags", s);
    }
	
    private static void printConstPool(ClassFile cf) {
        out("constant_pool_count", cf.getConstPool().getSize());
        for (int i = 1; i < cf.getConstPool().getSize(); i++) {
            String tag = null;
            String value = null;
            switch (cf.getConstPool().getTag(i)) {
                case ConstPool.CONST_Class:
                    tag = "class";
                    value = cf.getConstPool().getClassInfo(i);
                    break;
                case ConstPool.CONST_Double:
                    tag = "double";
                    break;
                case ConstPool.CONST_Fieldref:
                    tag = "field reference";
                    value = "[name:" + cf.getConstPool().getFieldrefName(i) + ",type:" + cf.getConstPool().getFieldrefType(i) + ",class:" + cf.getConstPool().getFieldrefClassName(i) + "]";
                    break;
                case ConstPool.CONST_Float:
                    tag = "float";
                    break;
                case ConstPool.CONST_Integer:
                    tag = "integer";
                    value = Integer.toString(cf.getConstPool().getIntegerInfo(i));
                    break;
                case ConstPool.CONST_InterfaceMethodref:
                    tag = "interface method reference";
                    break;
                case ConstPool.CONST_Long:
                    tag = "long";
                    break;
                case ConstPool.CONST_Methodref:
                    tag = "method reference";
                    value = "[name:" + cf.getConstPool().getMethodrefName(i) + ",type:" + cf.getConstPool().getMethodrefType(i) + ",class:" + cf.getConstPool().getMethodrefClassName(i) + "]";
                    break;
                case ConstPool.CONST_NameAndType:
                    tag = "name and type";
                    value = cf.getConstPool().getNameAndTypeName(i) + ":" + cf.getConstPool().getNameAndTypeDescriptor(i);
                    break;
                case ConstPool.CONST_String:
                    tag = "string";
                    value = cf.getConstPool().getStringInfo(i);
                    break;
                case ConstPool.CONST_Utf8:
                    tag = "utf8";
                    value = cf.getConstPool().getUtf8Info(i);
                    break;
                default:
                    tag = Integer.toString(cf.getConstPool().getTag(i));
            }
            out("\tconst" + i + "(" + tag + ")", value);
        }
    }
	private static void out(String s ){
		System.out.println(s);
	}
	private static void out(int s ){
		System.out.println(s);
	}
	private static void out(String s, String b ){
		System.out.println(s+"  "+b);
	}
	private static void out(String s, int b ){
		System.out.println(s+"  "+b);
	}
	
	/**
	 * 转换为文件路径格式
	 * @param packageStyle
	 * @return
	 */
	public static String packageToPathStyle(String packageStyle){
		return packageStyle.replace(".", "/");
	}
	
	
	/**
	 * 遍历map
	 * @param map
	 */
	public static void visitMap(Map map){
		Set<String> set = map.keySet();
		for(Iterator<String> it = set.iterator();it.hasNext();){
			String s = it.next();
			System.out.println(s+"--"+map.get(s));
		}
	}
	
}

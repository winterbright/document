package com.zjm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * @alias freemarker创建文件，提供多种方法，默认输出为System.out，默认路径为E:/fre
 * @author zjm
 *
 * 2012-7-4
 */
public class FmCreFile {

	private String defaultDirForTem = "E:/fre";
	
	public FmCreFile(){
		Properties pro = new Properties();
		File file = new File("src/freemarker.properties");
		if(file.exists()){
			try {
				FileInputStream fis = new FileInputStream(file);
				pro.load(fis);
				fis.close();
				String dir = (String) pro.get("DirectoryForTemplate");
				if(dir != null && !"".equals(dir)){
					defaultDirForTem = dir;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 创建文件
	 * @param map
	 * @param template
	 */
	@SuppressWarnings("unchecked")
	public void show(Map map,String template){
		cre(map, template, System.out);
	}
	
	/**
	 * 创建文件
	 * @param map
	 * @param template
	 * @param filePath
	 */
	@SuppressWarnings("unchecked")
	public void cre(Map map,String template, String filePath){
		File javaFile = new File(filePath);
		try {
			cre(map, null, template, new FileOutputStream(javaFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建文件
	 * @param map
	 * @param template
	 * @param filePath
	 */
	@SuppressWarnings("unchecked")
	public void cre(Map map,String template, OutputStream os){
		cre(map, null, template, os);
	}
	
	/**
	 * 创建文件
	 * @param map
	 * @param template
	 * @param filePath
	 */
	@SuppressWarnings("unchecked")
	public void cre(Map map,String directoryForTemplate,String template, String filePath){
		File javaFile = new File(filePath);
		try {
			cre(map, directoryForTemplate, template, new FileOutputStream(javaFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建文件
	 * @param map
	 * @param directoryForTemplate
	 * @param template
	 * @param filePath
	 */
	@SuppressWarnings("unchecked")
	public void cre(Map context,String directoryForTemplate,String template, OutputStream os) {
		Configuration cfg = new Configuration();
		try {
			if(directoryForTemplate!=null&&!"".equals(directoryForTemplate)){
				cfg.setDirectoryForTemplateLoading(new File(directoryForTemplate));
			}else{
				cfg.setDirectoryForTemplateLoading(new File(defaultDirForTem));
			}
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			Template tem = cfg.getTemplate(template);
			Writer out = new OutputStreamWriter(os);
			tem.process(context, out);
			out.flush();
			System.out.println("create file success!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

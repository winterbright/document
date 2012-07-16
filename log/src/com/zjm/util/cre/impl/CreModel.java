
package com.zjm.util.cre.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * @alias
 * @author zjm
 *
 * 2012-4-11
 */
public class CreModel {

	
	
	public static void main(String[] args) {
		new CreModel().getInterface();
	}
	
	public void getInterface(){
		try {
			String path = "E:/workspace/dm_api/src/com/itecheasy/dm/buyer/BuyerService.java";
			File file = new File(path);
			List<String> list = new ArrayList<String>();
			Map<String, Object> map = new HashMap<String, Object>();
			int i =0;
			InputStream is = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String temp;
			while((temp = br.readLine())!=null){
				list.add(temp);
			}
			for(String s : list){
				if(list.get(i).startsWith("package")){
					map.put("package", list.get(i));
					i++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cre(){
		Configuration cfg = new Configuration();
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("company", "com.itecheasy");
			map.put("project", "dm");
			map.put("pakage", "purchase");
			map.put("model", "PurchaseProductInformation1");
			cfg.setDirectoryForTemplateLoading(new File("E:/fre/model"));
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			Template potem = cfg.getTemplate("po.ftl");
			Template daotem = cfg.getTemplate("dao.ftl");
			Template daoImpltem = cfg.getTemplate("daoImpl.ftl");
			Template serviceImpltem = cfg.getTemplate("serviceImpl.ftl");
			String workspace = "E:/workspace/"+map.get("project")+"_impl/";
			String packInfo = workspace+"src/"+map.get("company").replace(".", "/")+"/"+map.get("project");
			String pack = packInfo+"/"+map.get("pakage")+"/"+map.get("model");
			String pofile = packInfo+"/po/"+map.get("model")+"PO.java";
			String daofile = pack+"DAO.java";
			String daoImplfile = pack+"DAOImpl.java";
			String serviceImplfile = pack+"ServiceImpl.java";
			Writer poout = new OutputStreamWriter(new FileOutputStream(pofile));
			Writer daoout = new OutputStreamWriter(new FileOutputStream(daofile));
			Writer daoImplout = new OutputStreamWriter(new FileOutputStream(daoImplfile));
			Writer serviceImplout = new OutputStreamWriter(new FileOutputStream(serviceImplfile));
			potem.process(map, poout);
			daotem.process(map, daoout);
			daoImpltem.process(map, daoImplout);
			serviceImpltem.process(map, serviceImplout);
			poout.flush();
			daoout.flush();
			daoImplout.flush();
			serviceImplout.flush();
			System.out.println("create file success!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

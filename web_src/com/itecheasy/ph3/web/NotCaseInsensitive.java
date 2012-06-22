package com.itecheasy.ph3.web;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class NotCaseInsensitive implements Filter{
	private static List<String> actions = getAction("struts.xml");
	private String suffix ;
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		if(request.getRequestURI().lastIndexOf(".") == -1){
			arg2.doFilter(arg0, arg1);
			return;
		}
		String end = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("."));
		if(!end.equalsIgnoreCase(suffix)){
			arg2.doFilter(arg0, arg1);
			return;
		}
		String url = request.getRequestURI().substring(request.getContextPath().length(),request.getRequestURI().lastIndexOf("."));
//		System.out.println(url);
		
		if(actions.contains(url)){
			if(!end.equals(suffix)){
				request.getRequestDispatcher(url+suffix).forward(request, response);
				return;
			}
			arg2.doFilter(arg0, arg1);
			return;
		}
		for(String action : actions){
			if(action.equalsIgnoreCase(url)){
				request.getRequestDispatcher(action+suffix).forward(request, response);
				return;
			}
		}
		
		arg2.doFilter(arg0, arg1);
		
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		suffix = arg0.getInitParameter("suffix");
	}
	
	public static List<String>  getAction(String fileName){
		List<String> actions = new ArrayList<String>();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
		dbf.setValidating(false);
		DocumentBuilder db;
		FileInputStream inputStream = null;
		try {
			db = dbf.newDocumentBuilder();
			db.setEntityResolver(new EntityResolver() {
				@Override
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes())); 
				}
			});
			String filePath = NotCaseInsensitive.class.getClassLoader().getResource(fileName).getFile();
			inputStream = new FileInputStream(filePath );
			Document document = db.parse(inputStream);
			Element root = document.getDocumentElement();
			
			NodeList includes = root.getElementsByTagName("include");
			for(int i = 0 ; i < includes.getLength() ; ++i){ 
				Element include = (Element)includes.item(i);
				actions.addAll(getAction(include.getAttribute("file")));
			}
			
			actions.addAll(getPackage(root));
			
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return actions;
	}

	private static List<String> getPackage(Element root) {
		NodeList packages = root.getElementsByTagName("package");
		List<String> actions = new ArrayList<String>();
		for(int i = 0 ; i < packages.getLength(); ++i){
			Element pk = (Element)packages.item(i);
			actions.addAll(getActionName( pk));
			
		}
		return actions;
	}

	private static List<String> getActionName( Element pk) {
		List<String> actions = new ArrayList<String>();
		String namespace = pk.getAttribute("namespace");
		NodeList as = pk.getElementsByTagName("action");
		for(int j = 0 ; j < as.getLength(); ++j){
			Element action = (Element)as.item(j);
			String actionUrl = "";
			if(namespace != null){
				actionUrl = namespace;
			}
			actionUrl += "/" + action.getAttribute("name");
			actions.add(actionUrl);
		}
		return actions;
	}
	public static void main(String[] args) {
		getAction("struts.xml");
		
		System.out.println(actions.contains("/admin/doLogin"));
	}
	
}




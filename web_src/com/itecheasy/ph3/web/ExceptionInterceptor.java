package com.itecheasy.ph3.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.itecheasy.ph3.web.utils.UrlHelper;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class ExceptionInterceptor extends AbstractInterceptor{

	private static final long serialVersionUID = 1L;
	
	protected transient final Logger log = Logger.getLogger("PH3");

	@Override
	public String intercept(ActionInvocation actionInvoction) throws Exception {
		String ip = null;
		String url = null;
	       try{
	    	   ActionContext ctx = actionInvoction.getInvocationContext(); 
	    	   HttpServletRequest request = (HttpServletRequest)ctx.get(ServletActionContext.HTTP_REQUEST);
	    	   ip = request.getRemoteHost();
	    	   url = UrlHelper.getRawUrl(request);
	           return  actionInvoction.invoke();
	       }catch(Exception e){ 
	    	   log.error(" \r\n ip : " + ip + " url = " + url  + "  Exception:", e);
	    	   throw e;
	       }

	}

}

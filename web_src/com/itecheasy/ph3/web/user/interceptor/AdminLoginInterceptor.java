package com.itecheasy.ph3.web.user.interceptor;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.itecheasy.ph3.adminuser.FunctionService;
import com.itecheasy.ph3.adminuser.User;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AdminLoginInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = -43287872187546221L;

	private static final String BEAN_NAME = "ph3AdminFunctionService";
	
	/**
	 * 匿名权限URL
	 */
	protected static final String[] DEF_STRINGS = new String[] {
			"/admin/doIndex.do", "/admin/doRight.do","/admin/flushMemCache.do" };

	@Override
	public String intercept(ActionInvocation actionInvoction) throws Exception {
		ActionContext ctx = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) ctx.get(StrutsStatics.HTTP_REQUEST);
		ServletContext sctx = (ServletContext) ctx.get(StrutsStatics.SERVLET_CONTEXT);
		User admin = (User) ctx.getSession().get(SessionUtils.LOGIN_ADMIN);
		if (admin == null) {
			return Action.LOGIN;
		}
		for (String url : DEF_STRINGS) {
			if(url.equals(getUrl(request))){
				return actionInvoction.invoke();
			}
		}
		if (!getFunctionService(sctx).hasFunctionRightByUrl(admin.getId(),getUrl(request))) { // 无权限
			return Action.NONE;
		}
		return actionInvoction.invoke();
	}

	/**
	 * 获取FunctionService
	 * */
	private FunctionService getFunctionService(ServletContext sctx) {
		WebApplicationContext wac = WebApplicationContextUtils
				.getRequiredWebApplicationContext(sctx);
		return (FunctionService) wac.getBean(BEAN_NAME);
	}

	/**
	 * 获取请求URL
	 * */
	private String getUrl(HttpServletRequest req) {
		String url = req.getRequestURI();
		String context = req.getContextPath();
		if (url.indexOf("?") != -1) {
			return url.substring(context.length(), url.indexOf("?"));
		} else {
			return url.substring(context.length());
		}
	}

}

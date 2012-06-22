package com.itecheasy.ph3.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.json.annotations.JSON;

import com.itecheasy.ph3.adminuser.Function;
import com.itecheasy.ph3.adminuser.FunctionGroup;
import com.itecheasy.ph3.adminuser.FunctionService;
import com.itecheasy.ph3.adminuser.User;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.utils.UrlHelper;

public class AdminBaseAction extends BaseAction {
	private static final long serialVersionUID = 20101218L;

	protected FunctionService ph3AdminFunctionService;

	private List<FunctionGroup> navFunctionGroups;
	private Function selectedFunction;
	private boolean isGetFunction = true;

	public void setPh3AdminFunctionService(
			FunctionService ph3AdminFunctionService) {
		this.ph3AdminFunctionService = ph3AdminFunctionService;
		// System.out.println("ph3AdminFunctionService");
	}

	@JSON(serialize = false)
	public User getLoginUserAdmin() {
		return SessionUtils.getLoginedAdminUser(request);
	}

	@JSON(serialize = false)
	public void setLoginUserAdmin(User user) {
		SessionUtils.setLoginedAdminUser(request, user);
	}

	public List<FunctionGroup> getNavFunctionGroups() {
		if (this.navFunctionGroups == null) {
			this.navFunctionGroups = this.ph3AdminFunctionService
					.getUserFunctionNavigation(this.getLoginUserAdmin().getId());
		}
		return navFunctionGroups;
	}

	public Function getSelectedFunction() {
		if (this.isGetFunction) {
			this.isGetFunction = false;
			this.selectedFunction = ph3AdminFunctionService
					.getFunctionIdByUrl(getUri(request));
		}
		return selectedFunction;
	}

	// 获取请求URL
	private static String getUri(HttpServletRequest req) {
		String url = UrlHelper.getRequestURI(req);
		String context = req.getContextPath();
		return url.substring(context.length());
	}

}

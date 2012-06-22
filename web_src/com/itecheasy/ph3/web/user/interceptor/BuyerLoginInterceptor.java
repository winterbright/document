package com.itecheasy.ph3.web.user.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.xwork.StringUtils;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.views.util.UrlHelper;

import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.web.BaseAction;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class BuyerLoginInterceptor extends AbstractInterceptor {
	private static final long serialVersionUID = -53287872187546221L;
	private static final String CONTINUE_URL = "continueURL";

	@Override
	public String intercept(ActionInvocation actionInvoction) throws Exception {
		ActionContext ctx = ActionContext.getContext();
		Customer userBuyer = (Customer) ctx.getSession().get(
				SessionUtils.CUSTOMER_INFO);
		if (userBuyer != null) // 已登录
			return actionInvoction.invoke();

		HttpServletRequest request = (HttpServletRequest) ctx
				.get(StrutsStatics.HTTP_REQUEST);
		HttpServletResponse response = (HttpServletResponse) ctx
				.get(StrutsStatics.HTTP_RESPONSE);
		String url = request.getParameter(CONTINUE_URL);

		if ("POST".equals(request.getMethod()) && StringUtils.isEmpty(url)) { // post
																				// 提交没设置
																				// CONTINUE_URL
			return Action.LOGIN;
		}
		if (actionInvoction.getAction() instanceof BaseAction) {
			//如果是点击logout退出，不记录下一个URL
			Object logout_url = SessionUtils.getSession().getAttribute("logout_url") ;
		/*	if ( logout_url!= null && ((Boolean)logout_url) ) {
				SessionUtils.getSession().removeAttribute("logout_url");
				return Action.LOGIN;
			}*/
			url = StringUtils.isEmpty(url) ? UrlHelper.buildUrl(null, request,
					response, request.getParameterMap(), request.getScheme(),
					true, true, true, true) : url;
			 url = UrlHelper.translateAndEncode(url);
			((BaseAction) actionInvoction.getAction()).setContinueURL(url);
		}
		return Action.LOGIN;
	}

}

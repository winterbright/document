package com.itecheasy.ph3.web.user.interceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsStatics;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.itecheasy.ph3.common.Utilities;
import com.itecheasy.ph3.system.CookieConfig;
import com.itecheasy.ph3.system.CookieConfigService;
import com.itecheasy.ph3.system.Country;
import com.itecheasy.ph3.system.Currency;
import com.itecheasy.ph3.system.DeliveryRemoteInfo;
import com.itecheasy.ph3.system.DictionaryService;
import com.itecheasy.ph3.system.ShippingService;
import com.itecheasy.ph3.system.SystemService;
import com.itecheasy.ph3.system.CookieConfigService.CookieType;
import com.itecheasy.ph3.web.BuyerBaseAction;
import com.itecheasy.ph3.web.utils.CookieHelper;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.utils.UrlHelper;
import com.itecheasy.ph3.web.vo.CookieArea;
import com.itecheasy.ph3.web.vo.MinShoppingCartTotalInfo;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class DefaultAreaInterceptor extends AbstractInterceptor {
	private static final long serialVersionUID = -4543047183627115226L;
	private static final String SYSTEM_SERVICE = "systemService";
	private static final String DICTIONARY_SERVICE = "dictionaryService";
	private static final String COOKIE_CONFIG_SERVICE = "cookieConfigService";
	private static final String SHIPPING_SERVICE = "shippingService";
	private static final String FIRST_IN_SITE = "first_in_site";

	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		doWork(arg0);
		return arg0.invoke();
	}

	/**
	 * 设置默认的国家币种信息。
	 * 
	 */
	private void doWork(ActionInvocation arg0) {
		ActionContext ctx = arg0.getInvocationContext();
		// request
		HttpServletRequest request = (HttpServletRequest) ctx.get(StrutsStatics.HTTP_REQUEST);
		// response
		HttpServletResponse response = (HttpServletResponse) ctx.get(StrutsStatics.HTTP_RESPONSE);
		// context
		ServletContext servletCtx = (ServletContext) ctx.get(StrutsStatics.SERVLET_CONTEXT);
		// cookieConfigService
		CookieConfigService cookieConfigService = (CookieConfigService) WebApplicationContextUtils.getRequiredWebApplicationContext(servletCtx).getBean(COOKIE_CONFIG_SERVICE);
		DictionaryService dictionaryService = (DictionaryService) WebApplicationContextUtils.getRequiredWebApplicationContext(servletCtx).getBean(DICTIONARY_SERVICE);
		SystemService systemService = (SystemService) WebApplicationContextUtils.getRequiredWebApplicationContext(servletCtx).getBean(SYSTEM_SERVICE);
		ShippingService shippingService = (ShippingService) WebApplicationContextUtils.getRequiredWebApplicationContext(servletCtx).getBean(SHIPPING_SERVICE);
		
		CookieArea cookieArea = SessionUtils.getAreaInfo(request.getSession());
		if (cookieArea != null)   return;  // 如session有区域设置，则什么都不操作
		
		Country country = null;
		Currency currency = null;
		String zip = "";
		String city = "";
		
		CookieConfig cookieConfig = null;
		Cookie cookie = CookieHelper.getCookie(request, CookieHelper.CUSTOMER_CLIENT_COOKIE_UUID);
		
		if (cookie != null && cookie.getValue() != null && !"".equals(cookie.getValue().trim())) 
		{
			String uuid = cookie.getValue();
			if (uuid != null) 
			{// 客户端有uuid
				cookieConfig = cookieConfigService.readCookie(uuid);
			}
		}
		
		if (cookieConfig != null && cookieConfig.getItems() != null) 
		{// 客户端有区域设置
			Map<CookieType, String> items = cookieConfig.getItems();
			String countryId = items.get(CookieType.COUNTRY);
			String currencyId = items.get(CookieType.CURRENCY);
			zip = items.get(CookieType.ZIP);
			city = items.get(CookieType.CITY);
			if (countryId != null) 
			{ // 根据设置的国家id获得国家。
				country = dictionaryService.getCountry(Integer.parseInt(countryId));
			}
			if (currencyId != null) 
			{ // 根据设置的币种ID查币种。
				currency = systemService.getCurrency(Integer.parseInt(currencyId));
			}
		} 
		else 
		{ // 客户端没有区域设置,则根据IP获得国家和币种
			String ipAddress = UrlHelper.getIpAddr(request);//获得IP			
			String countryCode = systemService.getcountryCodeByIp(ipAddress); // 根据ip查询国家CODE
			if ( !Utilities.isEmpty(countryCode) ) 
			{// 根据CODE查找的国家编号去查国家。
				country = dictionaryService.getCountryOfISOCode(countryCode);
			}
			
			request.setAttribute(FIRST_IN_SITE, 1);// 设置第一次进入本站点标识
		}
		
		if (country == null) 
		{// 如果没有国家，则获得系统中的默认国家
			country = dictionaryService.getDefaultCountry();
		}
		if (currency == null) 
		{// 如果没有币种，则根据国家获得默认币种
			currency = systemService.getDefaultCurrencyByCountry(country.getId());
		}

		//设置区域信息
		BuyerBaseAction.setAreaInfo(request, shippingService, cookieConfigService, currency, country, city, zip);
		
		cookieArea = SessionUtils.getAreaInfo(request.getSession());
		CookieHelper.addCookie(response, request, CookieHelper.CUSTOMER_CLIENT_COOKIE_UUID, cookieArea.getUuid(), CookieHelper.MAX_AGE);
	}

}

package com.itecheasy.ph3.web.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

public class UrlHelper {
	public static String getContextPath() {
		return getContextPath(ServletActionContext.getRequest());
	}

	/**
	 * 获取访问者IP
	 * 
	 * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
	 * 
	 * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，
	 * 如果还不存在则调用Request .getRemoteAddr()。
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		} else {
			return ip;
		}
		if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		} else {
			// 多次反向代理后会有多个IP值，第一个为真实IP。
			int index = ip.indexOf(',');
			if (index != -1) {
				ip = ip.substring(0, index);
			}
		}
		return ip;
	}

	public static String getContextPath(HttpServletRequest request) {
		return getHostUrl(request) + request.getContextPath() + "/";
	}

	public static String getRequestUrl() {
		HttpServletRequest request = ServletActionContext.getRequest();
		return getRequestUrl(request);
	}

	public static String getRequestUrl(HttpServletRequest request) {
		StringBuilder url = new StringBuilder();
		url.append(request.getScheme() + "://");
		url.append(request.getHeader("host"));
		// url.append(request.getRequestURI());
		url.append(getRequestURI(request));
		if (request.getQueryString() != null) {
			url.append("?" + request.getQueryString());
		}
		return url.toString();
	}

	public static String getRawUrl() {
		HttpServletRequest request = ServletActionContext.getRequest();
		return getRawUrl(request);
	}

	/**
	 * 浏览器地址的基本URL，不带参数
	 */
	public static String getBaseUrl(HttpServletRequest request) {
		StringBuilder url = new StringBuilder();
		url.append(request.getScheme() + "://");
		url.append(request.getHeader("host"));
		String rawURI = (String) request
				.getAttribute("javax.servlet.forward.request_uri");
		if (rawURI == null) {
			rawURI = request.getRequestURI();
		}
		url.append(rawURI);
		return url.toString();
	}

	/**
	 * 浏览器地址的URL
	 */
	public static String getRawUrl(HttpServletRequest request) {
		StringBuilder url = new StringBuilder();
		url.append(request.getScheme() + "://");
		url.append(request.getHeader("host"));
		String rawURI = (String) request
				.getAttribute("javax.servlet.forward.request_uri");
		String queryString;
		if (rawURI != null) {
			queryString = (String) request
					.getAttribute("javax.servlet.forward.query_string");
		} else {
			rawURI = request.getRequestURI();
			queryString = request.getQueryString();
		}
		url.append(rawURI);
		if (queryString != null) {
			url.append("?" + queryString);
		}
		return url.toString();
	}

	public static String getRefererUrl() {
		HttpServletRequest request = ServletActionContext.getRequest();
		return request.getHeader("Referer");
	}

	public static String getRefererUrl(HttpServletRequest request) {
		return request.getHeader("Referer");
	}

	public static String getHostUrl() {
		return getHostUrl(ServletActionContext.getRequest());
	}

	public static String getHostUrl(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getHeader("host");
	}

	public static String getRequestURI(HttpServletRequest request) {
		String requestURI = (String) request.getAttribute("struts.request_uri");
		if (requestURI != null) {
			return requestURI;
		}
		/*
		 * requestURI = (String)
		 * request.getAttribute("javax.servlet.forward.request_uri"); if
		 * (requestURI != null) { return requestURI; }
		 */
		return request.getRequestURI();
	}

	public static String getQueryString(String url) {
		int index = url.trim().indexOf("?");
		if (index == -1) {
			return null;
		}
		return url.substring(index + 1).trim();
	}

	public static String getQueryString(Map<String, String> parameterMap) {
		if (parameterMap == null || parameterMap.isEmpty()) {
			return null;
		}
		StringBuilder query = new StringBuilder();
		for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
			query.append("&" + entry.getKey() + "=" + entry.getValue());
		}
		if (query.length() > 0) {
			return query.replace(0, 1, "").toString();
		} else {
			return null;
		}
	}

	public static Map<String, String> getParameterMap(String queryString) {
		if (queryString == null || queryString.isEmpty()) {
			return null;
		}
		Map<String, String> parameterMap = new HashMap<String, String>();
		String[] parmsArr = queryString.split("&");
		for (String parmString : parmsArr) {
			if (parmString.trim().length() == 0) {
				continue;
			}
			String[] keyValue = parmString.split("=");
			parameterMap.put(keyValue[0].trim(),
					(keyValue.length > 1 ? keyValue[1] : null));
		}
		return parameterMap;
	}

	/**
	 * 设置URL参数.参数名称区分大小写
	 * 
	 * @param url
	 * @param key
	 *            参数名称
	 * @param value
	 *            参数值
	 * @return
	 */
	public static String setUrlParameter(String url, String key, String value) {
		int index = url.indexOf("?");
		if (index < 0) {
			return url + "?" + key + "=" + value;
		} else if (index == (url.length() - 1)) {
			return url + key + "=" + value;
		}
		String path = url.substring(0, index + 1);
		String queryString = url.substring(index + 1) + "&";
		Pattern pattern = Pattern.compile(key + "=.*?&");
		Matcher matcher = pattern.matcher(queryString);
		if (matcher.find()) {
			queryString = matcher.replaceAll(key + "=" + value + "&");
			if (queryString.endsWith("&")) {
				queryString = queryString
						.substring(0, queryString.length() - 1);
			}
			return path + queryString;
		} else {
			return url + "&" + key + "=" + value;
		}
	}

	/**
	 * 删除URL参数.参数名称区分大小写
	 * 
	 * @param url
	 * @param key
	 * @return
	 */
	public static String removeUrlParameter(String url, String key) {
		int index = url.indexOf("?");
		if (index < 0) {
			return url;
		} else if (index == (url.length() - 1)) {
			return url.substring(0, url.length() - 1);
		}
		String path = url.substring(0, index);
		String queryString = url.substring(index + 1) + "&";
		Pattern pattern = Pattern.compile(key + "=.*?&");
		Matcher matcher = pattern.matcher(queryString);
		if (matcher.find()) {
			queryString = matcher.replaceAll("");
			if (queryString.endsWith("&")) {
				queryString = queryString
						.substring(0, queryString.length() - 1);
			}
			if (queryString.length() > 0) {
				return path + "?" + queryString;
			} else {
				return path;
			}
		}
		return url;
	}

	/**
	 * 合并URL参数串
	 */
	public static String mergeQueryString(String url, String query) {
		if (query == null || query.length() == 0) {
			return url;
		}
		return url + (url.indexOf("?") > -1 ? "&" : "?") + query;
	}

	/**
	 * 合并URL参数串
	 * 
	 * @param url
	 * @param query
	 * @return
	 */
	public static String mergeQueryString(String url, String... query) {
		if (query == null || query.length == 0) {
			return url;
		}
		StringBuilder quertyBuilder = new StringBuilder();
		for (String q : query) {
			if (q != null && q.length() > 0) {
				quertyBuilder.append("&").append(q);
			}
		}
		if (quertyBuilder.length() > 0) {
			if (url.indexOf("?") < 0) {
				quertyBuilder.replace(0, 1, "?");
			}
			return url + quertyBuilder.toString();
		} else {
			return url;
		}
	}

	/*
	 * private static void printRequest(HttpServletRequest request) {
	 * java.util.Enumeration names = request.getAttributeNames();
	 * System.out.println("=======HeaderNames Start====="); while
	 * (names.hasMoreElements()) { System.out.println(names.nextElement()); }
	 * System.out.println("=======HeaderNames End=====");
	 * System.out.println("struts.view_uri:" +
	 * request.getAttribute("struts.view_uri"));
	 * System.out.println("javax.servlet.forward.request_uri:" +
	 * request.getAttribute("javax.servlet.forward.request_uri"));
	 * System.out.println("javax.servlet.forward.context_path:" +
	 * request.getAttribute("javax.servlet.forward.context_path"));
	 * System.out.println("javax.servlet.forward.servlet_path:" +
	 * request.getAttribute("javax.servlet.forward.servlet_path"));
	 * System.out.println("javax.servlet.forward.query_string:" +
	 * request.getAttribute("javax.servlet.forward.query_string"));
	 * System.out.println("struts.actionMapping:" +
	 * request.getAttribute("struts.actionMapping")); }
	 */

	/*
	 * public static void main(String[] args) { String url =
	 * "aa.html?aa=2#this"; System.out.println(removeUrlParameter(url, "aa")); }
	 */

}

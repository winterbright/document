package com.itecheasy.ph3.web.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itecheasy.ph3.category.ShowCategory;
import com.itecheasy.ph3.web.Link;
import com.itecheasy.ph3.web.tag.UrlFunction;

/**
 * 网站帮助类
 * 
 */
public class WebUtils {
	private static final String SHOPPING_CART_ID = "shoppingCartId";

	/**
	 * 获取日期的最大值
	 */
	public static Date getLongDateTime(Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, 23);
		calendar.add(Calendar.MINUTE, 59);
		calendar.add(Calendar.SECOND, 59);
		calendar.add(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	/**
	 * 获取用户最近浏览的展示类别导航
	 * 
	 * @return
	 */
	public static List<List<Link>> getRecentViewedCategoriesNavLinks(
			HttpServletRequest request) {
		List<List<Link>> viewedCategoryNavLinks = null;
		List<ShowCategory> categories = SessionUtils
				.getRecentViewedCategories(request);
		if (categories == null || categories.isEmpty()) {
			return null;
		}
		viewedCategoryNavLinks = new LinkedList<List<Link>>();
		for (ShowCategory category : categories) {
			viewedCategoryNavLinks.add(getCategoryNavLinks(category, true));
		}
		return viewedCategoryNavLinks;
	}

	/**
	 * 获取展示类别导航
	 * 
	 * @param category
	 *            商品类别
	 * @param isShowCurrentCategoryNavUrl
	 *            商品类别是否显示链接
	 */
	public static List<Link> getCategoryNavLinks(ShowCategory category,
			boolean isShowCurrentCategoryNavUrl) {
		List<Link> navLinks = new LinkedList<Link>();
		ShowCategory parent = category.getParent();
		if (parent != null) {
			if (parent.getParent() != null) {
				navLinks.add(new Link(parent.getParent().getName(),
						getCategoryUrl(parent.getParent())));
			}
			navLinks.add(new Link(parent.getName(), getCategoryUrl(parent)));
		}
		navLinks.add(new Link(category.getName(),
				(isShowCurrentCategoryNavUrl ? UrlFunction.getCategoryProducts(
						category.getId(), category.getName(), 1, category
								.getShowMode()) : null)));
		return navLinks;
	}

	private static String getCategoryUrl(ShowCategory category) {
		return UrlFunction.getShowCategoryList(category.getId(), category
				.getName());
	}

	/**
	 * 获取购物车ID
	 * 
	 * @return
	 */
	public static Integer getShoppingCartId(HttpServletRequest request) {
		int id = StrUtils.tryParseInt(CookieHelper.getCookieValue(request,
				SHOPPING_CART_ID), 0);
		if (id > 0) {
			return id;
		} else {
			return null;
		}
	}

	/**
	 * 设置购物车ID
	 * 
	 * @param id
	 *            购物车ID
	 */
	public static void setShoppingCartId(HttpServletResponse response, HttpServletRequest request,int id) {
		CookieHelper.addCookie(response,request, SHOPPING_CART_ID, String.valueOf(id),Integer.MAX_VALUE);
	}

	/**
	 * 从Cookie获取用户信息
	 * 
	 * @param request
	 * @return
	 */
	public static Cookie getCustomerCookie(HttpServletRequest request) {
		return CookieHelper.getCookie(request, SessionUtils.CUSTOMER_INFO);
	}

	/**
	 * 设置用户信息
	 */
	public static void setCustomerCookie(HttpServletRequest request,HttpServletResponse response, String value) 
	{
		CookieHelper.addCookie(response, request,SessionUtils.CUSTOMER_INFO, value,CookieHelper.MAX_AGE);
		/*
		 * Cookie cookie = getCustomerCookie(request); if (cookie == null) {
		 * cookie = new Cookie(SessionUtils.CUSTOMER_INFO, ""); }
		 * cookie.setValue(value); cookie.setPath("/"); cookie.setMaxAge(365 *
		 * 24 * 3600); response.addCookie(cookie);
		 */
	}

	public static void removeCustomerCookie(HttpServletRequest request,HttpServletResponse response) {
		CookieHelper.removeCookie(request,response, SessionUtils.CUSTOMER_INFO);
	}
	public static void removeShoppingCartCookie(HttpServletRequest request,HttpServletResponse response){
		CookieHelper.removeCookie(request,response, SHOPPING_CART_ID);
	}
	
	/**
	 * 求两个日期相差多少秒
	 */
	 public static long getDifferenceSeconds(Date startDate,Date endDate){
	 if( startDate.compareTo(endDate) > 0)
	 {
		 return 0;
	 }
	  Calendar calendar=Calendar.getInstance();
	  calendar.setTime(startDate);
	  long timethis=calendar.getTimeInMillis();
	  calendar.setTime(endDate);
	  long timeend=calendar.getTimeInMillis();
	  
	  return (timeend-timethis)/1000;
	 }
	

}

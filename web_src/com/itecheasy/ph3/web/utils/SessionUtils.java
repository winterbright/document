package com.itecheasy.ph3.web.utils;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.itecheasy.ph3.adminuser.User;
import com.itecheasy.ph3.category.ShowCategory;
import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.order.CashCoupon;
import com.itecheasy.ph3.system.DeliveryRemoteInfo;
import com.itecheasy.ph3.web.vo.CookieArea;
import com.itecheasy.ph3.web.vo.MinShoppingCartTotalInfo;

/**
 * Session帮助类
 * 
 */
public class SessionUtils {
	private static final String LAST_SHOPPING_URL = "LAST_SHOPPING_URL";
	private static final String RECENT_VIEWED_CATEGORIES = "RECENT_VIEWED_CATEGORIES";
	/** 前台用户信息 */
	public static final String CUSTOMER_INFO = "customer_info";
	/** 验证码信息 */
	public static final String VALIDATE_CODE = "VALIDATE_CODE";
	/** 后台用户信息 */
	public static final String LOGIN_ADMIN = "login_admin";

	public static final String MIN_SHOPPING_CART_INFO = "MIN_SHOPPING_CART_INFO";
	/** 购物车中使用的现金券 */
	public static final String SHPPING_CART_CASH_COUPON = "SHPPING_CART_CASH_COUPON";
	/**
	 * 区域设置，存放有关于国家、币种、邮编、城市信息
	 */
	public static final String AREA_SET_SESSION = "AREA_SET_SESSION";
	/**
	 * 保存根据country、city、zip查询的此地区的所有货运方式的偏远地区费用的信息
	 */
	public static final String DELIVERY_REMOTE_INFOS = "DELIVERY_REMOTE_INFOS";

	public static HttpSession getSession() {
		return ServletActionContext.getRequest().getSession();
	}

	/**
	 * 获取前台登录用户
	 */
	public static Customer getLoginedCustomer() {
		return (Customer) getSession().getAttribute(CUSTOMER_INFO);
	}

	/**
	 * 获取前台登录用户
	 */
	public static Customer getLoginedCustomer(HttpServletRequest request) {
		return (Customer) request.getSession().getAttribute(CUSTOMER_INFO);
	}

	/**
	 * 设置前台登录用户
	 */
	public static void setLoginedCustomer(HttpServletRequest request, Customer customer) {
		request.getSession().setAttribute(CUSTOMER_INFO, customer);
	}

	/**
	 * 删除前台登录用户
	 */
	public static void removeLoginedCustomer(HttpServletRequest request) {
		request.getSession().removeAttribute(CUSTOMER_INFO);
	}

	/**
	 * 获取后台登录用户
	 */
	public static User getLoginedAdminUser() {
		return (User) getSession().getAttribute(LOGIN_ADMIN);
	}

	/**
	 * 获取后台登录用户
	 */
	public static User getLoginedAdminUser(HttpServletRequest request) {
		return (User) request.getSession().getAttribute(LOGIN_ADMIN);
	}

	/**
	 * 设置后台登录用户
	 */
	public static void setLoginedAdminUser(HttpServletRequest request, User user) {
		request.getSession().setAttribute(LOGIN_ADMIN, user);
	}

	/**
	 * 删除后台登录用户
	 */
	public static void removeLoginedAdminUser(HttpServletRequest request) {
		request.getSession().removeAttribute(LOGIN_ADMIN);
	}

	/**
	 * 获取验证码
	 */
	public static String getVerifyCode(HttpServletRequest request) {
		return (String) request.getSession().getAttribute(VALIDATE_CODE);
	}

	/**
	 * 设置验证码
	 */
	public static void setVerifyCode(HttpServletRequest request, String verifyCode) {
		request.getSession().setAttribute(VALIDATE_CODE, verifyCode);
	}

	/**
	 * 用户的最后购物URL
	 */
	public static String getLastShoppingPageUrl(HttpServletRequest request) {
		String url = (String) request.getSession().getAttribute(LAST_SHOPPING_URL);
		if (url != null) {
			return url;
		}
		// 返回首页
		return UrlHelper.getContextPath(request);
	}

	/**
	 * 设置用户的最后购物URL
	 */
	public static void setLastShoppingPageUrl(HttpServletRequest request, String url) {
		request.getSession().setAttribute(LAST_SHOPPING_URL, url);
	}

	/**
	 * 设置购物车信息
	 */
	public static void setMinShoppingCartInfo(HttpServletRequest request, MinShoppingCartTotalInfo cartTotalInfo) {
		// 同时保留已选择的现金券
		setMinShoppingCartInfo(request.getSession(), cartTotalInfo);
	}
	
	/**
	 * 设置购物车信息
	 */
	public static void setMinShoppingCartInfo(HttpSession session, MinShoppingCartTotalInfo cartTotalInfo) {
		// 同时保留已选择的现金券
		if (cartTotalInfo != null && cartTotalInfo.getCashCouponInfo() == null && getCashCouponFromShoppingCart(session) != null) {
			cartTotalInfo.setCashCouponInfo(getCashCouponFromShoppingCart(session));
		}

		session.setAttribute(MIN_SHOPPING_CART_INFO, cartTotalInfo);
	}

	/**
	 * 获取购物车信息
	 * 
	 * @param request
	 * @return
	 */
	public static MinShoppingCartTotalInfo getMinShoppingCartInfo(HttpServletRequest request) {
		return getMinShoppingCartInfo(request.getSession());
	}
	
	/**
	 * 获取购物车信息
	 * 
	 * @param request
	 * @return
	 */
	public static MinShoppingCartTotalInfo getMinShoppingCartInfo(HttpSession session) {
		return (MinShoppingCartTotalInfo) session.getAttribute(MIN_SHOPPING_CART_INFO);
	}

	/**
	 * 删除购物车信息
	 * 
	 * @param request
	 * @return
	 */
	public static void removeMinShoppingCartInfo(HttpServletRequest request) {
		request.getSession().removeAttribute(MIN_SHOPPING_CART_INFO);
	}

	/**
	 * 获取购物车中使用的现金券
	 */
	public static CashCoupon getCashCouponFromShoppingCart(HttpServletRequest request) {
		return (CashCoupon) request.getSession().getAttribute(SHPPING_CART_CASH_COUPON);
	}
	
	/**
	 * 获取购物车中使用的现金券
	 */
	public static CashCoupon getCashCouponFromShoppingCart(HttpSession session) {
		return (CashCoupon) session.getAttribute(SHPPING_CART_CASH_COUPON);
	}

	/**
	 * 设置购物车中使用的现金券
	 */
	public static void setCashCouponToShoppingCart(HttpServletRequest request, CashCoupon cashCoupon) {
		request.getSession().setAttribute(SHPPING_CART_CASH_COUPON, cashCoupon);

		// 同时更新购物车中的现金券
		MinShoppingCartTotalInfo minShoppingCartTotalInfo = getMinShoppingCartInfo(request);
		if (minShoppingCartTotalInfo != null) {
			minShoppingCartTotalInfo.setCashCouponInfo(cashCoupon);
			setMinShoppingCartInfo(request, minShoppingCartTotalInfo);
		}
	}

	/**
	 * 删除购物车中使用的现金券
	 */
	public static void removeCashCouponFromShoppingCart(HttpServletRequest request) {
		request.getSession().removeAttribute(SHPPING_CART_CASH_COUPON);
	}

	/**
	 * 获取用户最近浏览的类别
	 */
	@SuppressWarnings("unchecked")
	public static List<ShowCategory> getRecentViewedCategories(HttpServletRequest request) {
		return (List<ShowCategory>) request.getSession().getAttribute(RECENT_VIEWED_CATEGORIES);
	}

	/**
	 * 把用户最近浏览的类别添加到最近浏览类别列表，最多4个
	 */
	public static void setRecentViewedCategories(HttpServletRequest request, ShowCategory viewedCategory) {
		List<ShowCategory> categories = getRecentViewedCategories(request);
		if (categories == null) {
			categories = new LinkedList<ShowCategory>();
			categories.add(viewedCategory);
		} else {
			int index = getCategoryIndex(categories, viewedCategory);
			if (index == 0) {
				return;
			}
			if (index > -1) {
				categories.remove(index);
			} else if (categories.size() > 3) {
				categories.remove(categories.size() - 1);
			}
			categories.add(0, viewedCategory);
		}
		request.getSession().setAttribute(RECENT_VIEWED_CATEGORIES, categories);
	}

	private static int getCategoryIndex(List<ShowCategory> categories, ShowCategory category) {
		ShowCategory category2;
		for (int i = 0; i < categories.size(); i++) {
			category2 = categories.get(i);

			if (category2 != null && category.getId().compareTo(category2.getId()) == 0) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 设置区域信息
	 * 
	 * @param areaConfig
	 * @param request
	 */
	public static void setAreaInfo(CookieArea cookieArea, HttpSession session) {
		session.setAttribute(AREA_SET_SESSION, cookieArea);
	}

	/**
	 * 读取区域信息
	 * 
	 * @param request
	 * @return
	 */
	public static CookieArea getAreaInfo(HttpSession session) {
		Object obj = session.getAttribute(AREA_SET_SESSION);
		if (obj != null) {
			return (CookieArea) obj;
		} else {
			return null;
		}
	}
	
	public static void setDeliveryRemoteInfos(List<DeliveryRemoteInfo> deliveryRemoteInfos, HttpSession session){
		session.setAttribute(DELIVERY_REMOTE_INFOS, deliveryRemoteInfos);
	}
	
	public static List<DeliveryRemoteInfo> getDeliveryRemoteInfos(HttpSession session){
		Object obj = session.getAttribute(DELIVERY_REMOTE_INFOS);
		if(obj != null){
			return (List<DeliveryRemoteInfo>) obj;
		}
		return null;
	}

}

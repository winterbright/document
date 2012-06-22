package com.itecheasy.ph3.web.buyer.order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.itecheasy.ph3.web.buyer.BuyerPageController.PlaceOrderPage;

public class OrderSessionUtils {
	public static final String ORDER_INFO = "_order_info";
	public static final String ORDER_CREDIT_CARD_INFO = "_order_credit_card_info";
	public static final String ORDER_CREDIT_CARD_URL = "_order_credit_card_url";
	public static final String PLACE_ORDER_BEGIN_PAGE = "_place_order_begin_page";
	public static final String PLACE_ORDER_END_PAGE = "_place_order_end_page";
	public static final String EXPRESS_PAYPAL_TOKEN = "_express_paypal_token";

	/**
	 * 获取session中订单信息
	 * 
	 * @param request
	 * @return
	 */
	public static SessionOrder getSessionOrder(HttpSession session) {
		Object object = session.getAttribute(ORDER_INFO);
		if (object == null)
			return null;
		return (SessionOrder) object;
	}

	/**
	 * 设置session中订单信息
	 * 
	 * @param request
	 * @return
	 */
	public  static void setSessionOrder(HttpServletRequest request,
			SessionOrder sessionOrder) {
		request.getSession().setAttribute(ORDER_INFO, sessionOrder);
	}

	/**
	 * 清除session中订单信息
	 * 
	 * @param request
	 */
	public static void clearSessionOrder(HttpServletRequest request) {
		request.getSession().removeAttribute(ORDER_INFO);
	}
	
	/**
	 * 获取关于信用卡的订单信息
	 * 
	 * @param request
	 * @return
	 */
	public static SessionOrder getSessionOrderByCreditCard(HttpSession session) {
		Object object = session.getAttribute(ORDER_CREDIT_CARD_INFO);
		if (object == null)
			return null;
		return (SessionOrder) object;
	}

	/**
	 * 设置信用卡的订单信息
	 * 
	 * @param request
	 * @return
	 */
	public  static void setSessionOrderByCreditCard(HttpServletRequest request,
			SessionOrder sessionOrder) {
		request.getSession().setAttribute(ORDER_CREDIT_CARD_INFO, sessionOrder);
	}

	/**
	 * 清除信用卡的订单信息
	 * 
	 * @param request
	 */
	public static void clearSessionOrderByCreditCard(HttpServletRequest request) {
		request.getSession().removeAttribute(ORDER_CREDIT_CARD_INFO);
	}
	
	/**
	 * 获取关于信用卡的支付地址
	 * 
	 * @param request
	 * @return
	 */
	public static String getCreditCardUrl(HttpSession session) {
		Object object = session.getAttribute(ORDER_CREDIT_CARD_URL);
		if (object == null)
			return null;
		return (String) object;
	}

	/**
	 * 设置信用卡的支付地址
	 * 
	 * @param request
	 * @return
	 */
	public  static void setCreditCardUrl(HttpServletRequest request,
			String url) {
		request.getSession().setAttribute(ORDER_CREDIT_CARD_URL, url);
	}

	/**
	 * 清除信用卡的支付地址
	 * 
	 * @param request
	 */
	public static void clearCreditCardUrl(HttpServletRequest request) {
		request.getSession().removeAttribute(ORDER_CREDIT_CARD_URL);
	}
	
	
	/**
	 * 清除session中订单操作标示
	 * 
	 * @param request
	 */
	public static void clearSessionOrderPage(HttpServletRequest request) {
		request.getSession().removeAttribute(PLACE_ORDER_BEGIN_PAGE);
		request.getSession().removeAttribute(PLACE_ORDER_END_PAGE);
	}
	
	/**
	 * 下单相关页面开始显示时调用
	 * @param request
	 * @param pageNum 下单相关页面
	 */
	public static void beginPlaceOrderPage(HttpServletRequest request,PlaceOrderPage pageNum)
	{
		setPlaceOrderBeginPage(request,pageNum);
		setPlaceOrderEndPage(request, null);
	}
	
	/**
	 * 下单相关页面操作完成时调用
	 * @param request
	 * @param pageNum 下单相关页面
	 */
	public static void endPlaceOrderPage(HttpServletRequest request,PlaceOrderPage pageNum)
	{
		setPlaceOrderEndPage(request, pageNum);
	}
	
	/**
	 * 获取当前下单过程显示的页面
	 * 
	 * @param request
	 * @return
	 */
	public static PlaceOrderPage getPlaceOrderBeginPage(HttpServletRequest request) {
		Object object = request.getSession().getAttribute(PLACE_ORDER_BEGIN_PAGE);
		if (object == null)
			return null;
		return (PlaceOrderPage) object;
	}

	/**
	 * 设置当前下单过程显示的页面
	 * 
	 * @param request
	 * @return
	 */
	public  static void setPlaceOrderBeginPage(HttpServletRequest request,
			PlaceOrderPage pageNum) {
		request.getSession().setAttribute(PLACE_ORDER_BEGIN_PAGE, pageNum);
	}
	
	/**
	 * 获取当前下单过程完成操作的页面
	 * 
	 * @param request
	 * @return
	 */
	public static PlaceOrderPage getPlaceOrderEndPage(HttpServletRequest request) {
		Object object = request.getSession().getAttribute(PLACE_ORDER_END_PAGE);
		if (object == null)
			return null;
		return (PlaceOrderPage) object;
	}

	/**
	 * 设置当前下单过程完成操作的页面
	 * 
	 * @param request
	 * @return
	 */
	public  static void setPlaceOrderEndPage(HttpServletRequest request,
			PlaceOrderPage pageNum) {
		request.getSession().setAttribute(PLACE_ORDER_END_PAGE, pageNum);
	}
}

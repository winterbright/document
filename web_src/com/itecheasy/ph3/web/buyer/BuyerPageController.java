package com.itecheasy.ph3.web.buyer;

import javax.servlet.http.HttpServletRequest;

import com.itecheasy.ph3.web.buyer.order.OrderSessionUtils;

public class BuyerPageController {
	/**
	 * 跳转到购物车页面
	 */
	public static final String GO_TO_SET_SHIPPING_CART = "GO_TO_SET_SHIPPING_CART";	
	/**
	 * 跳转到设置订单地址页面
	 */
	public static final String GO_TO_SET_SHIPPING_ADDRESS = "GO_TO_SET_SHIPPING_ADDRESS";	
	/**
	 * 跳转到设置货运方式页面
	 */
	public static final String GO_TO_SET_SHIPPING_METHOD = "GO_TO_SET_SHIPPING_METHOD";	
	/**
	 * 跳转到支付方式页面
	 */
	public static final String GO_TO_SET_PAY_METHOD = "GO_TO_SET_PAY_METHOD";
	/**
	 * 跳转到信用卡支付页面
	 */
	public static final String GO_TO_SET_CREDIT_CARD = "GO_TO_SET_CREDIT_CARD";
	
	public static enum PlaceOrderPage
	{
		/**
		 * 非下单页面
		 */
		PAGE_OTHER_PAGE(0),
		/**
		 * 购物车页面
		 */
		PAGE_SHIPPING_CART(1),
		/**
		 * 设置订单地址页面
		 */
		PAGE_SHIPPING_ADDRESS(2),
		/**
		 * 设置货运方式页面
		 */
		PAGE_SHIPPING_METHOD(3),
		/**
		 * 支付方式页面
		 */
		PAGE_PAYMENT_METHOD(4),
		/**
		 * 信用卡页面
		 */
		PAGE_CREDIT_CARD(5),
		/**
		 * 确认订单页面
		 */
		PAGE_CONFIRM_ORDER(6);
		
		private int pageNo;

		private PlaceOrderPage(int pageNo) {
			this.pageNo = pageNo;
		}
		
		public int getValue()
		{
			return pageNo;
		}
	}
	
	/**
	 * 检查下单过程中，上一步显示的页面是否与完成操作的页面相同,如果完成操作的页面是上一步显示的页面后面的步骤，则不允许往下操作
	 * 主要是防止通过直接输入URL链接跳转到后面的页面
	 * @return 
	 */
	public static String checkBeginPageAndEndPage(HttpServletRequest request,PlaceOrderPage currentPageNum)
	{
		PlaceOrderPage beginPage = OrderSessionUtils.getPlaceOrderBeginPage(request);		
		if( beginPage == null ) return GO_TO_SET_SHIPPING_CART;
		
		PlaceOrderPage endPage = OrderSessionUtils.getPlaceOrderEndPage(request);		
		
		//下单的页面只允许往前面的步骤跳，但不能直接往后面的步骤跳
		//如下单共5步，如果当前已进行到第3步（但未完成第3步），这时可以将页面跳到3步之前的所有页面，但不能越过完成第3步的操作，而直接跳到第4或第5步
		if( beginPage.getValue() < currentPageNum.getValue() && endPage == null)
		{
			switch (beginPage) {
			case PAGE_OTHER_PAGE:				
				return GO_TO_SET_SHIPPING_CART;
				
			case PAGE_SHIPPING_CART:				
				return GO_TO_SET_SHIPPING_CART;
				
			case PAGE_SHIPPING_ADDRESS:
				return GO_TO_SET_SHIPPING_ADDRESS;
				
			case PAGE_SHIPPING_METHOD:
				return GO_TO_SET_SHIPPING_METHOD;
				
			case PAGE_PAYMENT_METHOD:
				return GO_TO_SET_PAY_METHOD;
				
			case PAGE_CREDIT_CARD:
				return GO_TO_SET_CREDIT_CARD;
				
			default:
				break;
			}
		}
		return null;
	}
	
	public static void beginPage(HttpServletRequest request,PlaceOrderPage pageNum) 
	{
		OrderSessionUtils.beginPlaceOrderPage(request, pageNum);
	}
	
	public static void endPage(HttpServletRequest request,PlaceOrderPage pageNum) 
	{
		OrderSessionUtils.endPlaceOrderPage(request, pageNum);
	}
	
	public static PlaceOrderPage getBeginPage(HttpServletRequest request) {
		return OrderSessionUtils.getPlaceOrderBeginPage(request);
	}
	
	public static PlaceOrderPage getEndPage(HttpServletRequest request) {
		return OrderSessionUtils.getPlaceOrderEndPage(request);
	}
	
}

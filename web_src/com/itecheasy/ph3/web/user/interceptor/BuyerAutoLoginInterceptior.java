package com.itecheasy.ph3.web.user.interceptor;

import java.math.BigDecimal;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.common.DeployProperties;
import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.customer.CustomerService;
import com.itecheasy.ph3.order.CashCoupon;
import com.itecheasy.ph3.order.CashCouponService;
import com.itecheasy.ph3.shopping.ShoppingCartTotal;
import com.itecheasy.ph3.shopping.ShoppingService;
import com.itecheasy.ph3.system.ShippingService;
import com.itecheasy.ph3.system.SystemService;
import com.itecheasy.ph3.web.utils.DesUtils;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.utils.UrlHelper;
import com.itecheasy.ph3.web.utils.WebUtils;
import com.itecheasy.ph3.web.vo.CookieArea;
import com.itecheasy.ph3.web.vo.MinShoppingCartTotalInfo;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class BuyerAutoLoginInterceptior extends AbstractInterceptor {

	private static final long serialVersionUID = -33287872187546221L;
	
	private static final String CUSTOMER_SERVICE = "customerService";
	
	private static final String SHOPPING_SERVICE = "shoppingService";
	
	private static final String SHIPPING_SERVICE = "shippingService";
	
	private static final String SYSTEM_SERVICE = "systemService";
	
	private static final String CAHS_COUPON_SERVICE = "cashCouponService";
	
	private static final String checkOutURL = "/customer/myaccount/checkout.do";

	private SystemService systemService;
	
	@Override
	public String intercept(ActionInvocation actionInvoction) throws Exception {

		ActionContext ctx = ActionContext.getContext();
		
		//request
		HttpServletRequest request = (HttpServletRequest) ctx.get(StrutsStatics.HTTP_REQUEST);
		//response
		HttpServletResponse response = (HttpServletResponse)ctx.get(StrutsStatics.HTTP_RESPONSE);
		//context
		ServletContext servletCtx = (ServletContext) ctx.get(StrutsStatics.SERVLET_CONTEXT);
		
		systemService = (SystemService) WebApplicationContextUtils.getRequiredWebApplicationContext(servletCtx).getBean(SYSTEM_SERVICE);
		
		//是否启用过滤IP功能
		if(DeployProperties.getInstance().isCheckIp()){ 
			//检查IP
			boolean isCheckIp = systemService.checkIp(UrlHelper.getIpAddr(request));
			if(!isCheckIp){
				return "ERROR_404";
			}
		}
		
		// 获取买家登录信息
		Customer userBuyer = (Customer) ctx.getSession().get(SessionUtils.CUSTOMER_INFO);
		if (userBuyer == null) {
			Customer c = getCustomer(request, getCustomerService(servletCtx));
			if (c != null){
				actionInvoction.getInvocationContext().getSession().put(SessionUtils.CUSTOMER_INFO, c);
				
				dealShoppingCart(request,response,servletCtx,c);
			}
		}

		return actionInvoction.invoke();
	}

	/*
	 * 获取 CustomerService对象
	 */
	private CustomerService getCustomerService(ServletContext servletCtx) {
		WebApplicationContext webctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletCtx);
		return (CustomerService) webctx.getBean(CUSTOMER_SERVICE);
	}
	
	/*
	 * 获取 ShoppingService对象
	 */
	private ShoppingService getShoppingService(ServletContext servletCtx) {
		WebApplicationContext webctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletCtx);
		return (ShoppingService) webctx.getBean(SHOPPING_SERVICE);
	}
	
	/*
	 * 获取ShippingService对象
	 */
	private ShippingService getShippingService(ServletContext servletCtx) {
		WebApplicationContext webctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletCtx);
		return (ShippingService) webctx.getBean(SHIPPING_SERVICE);
	}
	
	/*
	 * 获取 CashCouponService对象
	 */
	private CashCouponService getCashCouponService(ServletContext servletCtx) {
		WebApplicationContext webctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletCtx);
		return (CashCouponService) webctx.getBean(CAHS_COUPON_SERVICE);
	}

	/*
	 * 获取Customer对象
	 */
	private Customer getCustomer(HttpServletRequest request,CustomerService customerService) {
		Cookie cookie = WebUtils.getCustomerCookie(request);
		if (cookie == null) {
			return null;
		}
		String value = cookie.getValue();
		if (value == null || value.length() == 0) {
			return null;
		}
		JSONObject o = JSONObject.fromObject(value);
		String password = o.get("password").toString();
		String email = o.get("email").toString();
		try {
			DesUtils des = new DesUtils();
			customerService.login(des.decrypt(email), des.decrypt(password),request.getRemoteAddr());
			return customerService.getCustomerByEmail(des.decrypt(email));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void dealShoppingCart(HttpServletRequest request,HttpServletResponse response,ServletContext servletCtx,Customer customer)
	{
		ShoppingService shoppingService = getShoppingService(servletCtx);
		if( customer == null || shoppingService == null) return;
		
        String continueURL = UrlHelper.getBaseUrl(request);
		
		Integer shoppingCartId = WebUtils.getShoppingCartId(request);
		String httpCheckOutURL = UrlHelper.getContextPath()+ checkOutURL;
		WebUtils.removeShoppingCartCookie(request,response);
		if( httpCheckOutURL.equalsIgnoreCase(continueURL))		
        {  //如果是直接点checkOut之后登录的，则将客户原购物车中的商品替换成临时购物车中的商品
        	shoppingCartId = shoppingService.changeCustomerShoppingCartProducts(shoppingCartId, customer.getId());
        	//重新保存客户的购物车ID到Cookie
        	WebUtils.setShoppingCartId(response, request,shoppingCartId);
        }
        else
        { 	//如果不是直接checkOut之后引起的登录则，则将临时购物车中的商品与客户原购物车中的商品进行合并
        	shoppingCartId = shoppingService.mergeShoppingCart(shoppingCartId, customer.getId());
        	
        	//重新保存客户的购物车ID到Cookie
        	WebUtils.setShoppingCartId(response,request, shoppingCartId);
        }
	}
	
	private void changeCashCoupon(HttpServletRequest request,HttpServletResponse response,ServletContext servletCtx,Customer customer)
	{
		if( customer == null) return ;
		
		CashCouponService cashCouponService = getCashCouponService(servletCtx);
		if( customer == null || cashCouponService == null) return;
		
		//如果发现使用了现金券，则判断是否需要将无用户现金券转换成由用户现金券来使用
		CashCoupon cashCoupon = SessionUtils.getCashCouponFromShoppingCart(request);
		if( cashCoupon != null)
		{
			MinShoppingCartTotalInfo minShoppingCartTotalInfo = getMinShoppingCartTotalInfo(request,response,servletCtx);
			if( minShoppingCartTotalInfo != null)
			{			
				//获取当前购物车中的商品折后总金额
				BigDecimal productPrice = minShoppingCartTotalInfo.getProductPriceAfterDiscount();
				
				//检查现金券是否能使用				
				try
				{
					cashCouponService.checkIsAbleUse(productPrice,cashCoupon.getCode(),customer.getId());		
					
					//重新获取现金券信息
					cashCoupon = cashCouponService.getCashCouponByCodeAndCustomer(cashCoupon.getCode(),customer.getId());
				}
				catch(BussinessException ex)
				{
					cashCoupon = null;
				}		
			}
			//在购物车中使用现金券			
			SessionUtils.setCashCouponToShoppingCart(request, cashCoupon);
//			setCashCouponForShoppingCart(cashCoupon);
		}
	}
	
	/**
	 * 购物车商品信息
	 */
	public MinShoppingCartTotalInfo getMinShoppingCartTotalInfo(HttpServletRequest request,HttpServletResponse response,ServletContext servletCtx) {
		ShoppingService shoppingService = getShoppingService(servletCtx);
		if( shoppingService == null) return null;
		
		Integer id = WebUtils.getShoppingCartId(request);
		if (id == null) 
		{
			SessionUtils.setMinShoppingCartInfo(request, null);
			return null;
		}
		MinShoppingCartTotalInfo info = SessionUtils.getMinShoppingCartInfo(request);
		if (info == null) 
		{
			int productQty = shoppingService.getShoppingCartTotalProductQty(id);
			BigDecimal totalPrice = productQty <1 ? BigDecimal.ZERO : shoppingService.getShoppingCartTotalPriceAfterDiscount(id);
			
			
			//zw
			ShoppingCartTotal shoppingCartTotal = shoppingService.getShoppingCartTotalInfo(id);
			ShippingService shippingService = getShippingService(servletCtx);
			CookieArea cookieArea = SessionUtils.getAreaInfo(ServletActionContext.getRequest().getSession());
			//zw
			BigDecimal orderWeight = shippingService.computeOrderWeight(shoppingCartTotal.getTotalWeight(), shoppingCartTotal.getTotalVolume());
			info = new MinShoppingCartTotalInfo(productQty, totalPrice,orderWeight, shippingService.getDefaultDeliveryFreightRegion(cookieArea.getCountry(), orderWeight, cookieArea.getCity(), cookieArea.getZip()));
			SessionUtils.setMinShoppingCartInfo(request, info);
		}
		return info;
	}

}

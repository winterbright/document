package com.itecheasy.ph3.web.buyer;

import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.ws.Holder;

import org.apache.commons.lang.StringUtils;

import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.order.CashCoupon;
import com.itecheasy.ph3.order.CashCouponService;
import com.itecheasy.ph3.web.BuyerBaseAction;
import com.itecheasy.ph3.web.buyer.order.OrderSessionUtils;
import com.itecheasy.ph3.web.utils.DesUtils;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.utils.UrlHelper;
import com.itecheasy.ph3.web.utils.ValidateUtils;
import com.itecheasy.ph3.web.utils.WebUtils;
import com.itecheasy.ph3.web.vo.MinShoppingCartTotalInfo;
import com.itecheasy.sslplugin.annotation.Secured;

public class BuyerCustomerAction extends BuyerBaseAction {
	private static final long serialVersionUID = 192225455L;
	private static final String checkOutURL = "/customer/myaccount/checkout.do";

	private Customer customer;
	private String ckIsCookie;
	private String isNewsletter; // 是否订阅邮件
	private String re_password;
	private String password;
	private String couponCode;
	
    private CashCouponService cashCouponService;
	
	public void setCashCouponService(CashCouponService cashCouponService) 
	{
		this.cashCouponService = cashCouponService;
	}
	
	@Secured
	public String doRegister() {
		return SUCCESS;
	}

	public String doRegisterSuccess() {
		Customer customer = getLoginedUserBuyer();
		if (customer != null) {
			request.setAttribute("userEmail", customer.getEmail());
			return SUCCESS;
		}
		return "fial";
	}

	/**
	 * 注册
	 * 
	 * @return
	 */
	@Secured
	public String register() {
		if (!registerValidator()) {
			return "fial";
		}
		Holder<String> couponCode = new Holder<String>();
		try {
			Integer customerId = customerService.register(customer, password,StringUtils.isNotEmpty(getIsNewsletter()),couponCode);
			customer.setId(customerId);
		} catch (BussinessException e) {
			this.messageInfo = "ERROR_CUSTOMER_EMAIL_REGISTERED";
			return "fial";
		}
		setLoginedUserBuyer(customerService.getCustomerByEmail(customer.getEmail()));
		this.couponCode = couponCode.value;
		//注册成功后处理购物车相关操作
		dealShoppingCart();
		
		//注册前如果用户已使用现金券，则需要根据情况将无用户现金券转换成用户现金券
		changeCashCoupon();		
		
		return SUCCESS;
	}
	
	/**
	 * ajax注册
	 */
	public void ajaxRegister(){
		this.customer = new Customer();
		customer.setEmail(param("email"));
		this.isNewsletter = param("isNewsletter");
		this.password = param("password1");
		this.re_password = param("password2");
		customer.setFirstName(param("firstName"));
		customer.setLastName(param("lastName"));
		
		String regReturn = register();
		if(regReturn == "fial"){
			try {
				returnJson("[{\"result\":\"false\"}]");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				errorLog("change to json fail",e);
				e.printStackTrace();
			}
		}else{
			try {
				returnJson("[{\"result\":\"true\"}]");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Secured
	public String doLogin() {
		return SUCCESS;
	}
	public String getCouponCode(){
		return couponCode;
	}
	public void setCouponCode(String couponCode){
		 this.couponCode=couponCode;
	}
	/**
	 * 登录
	 * 
	 * @return
	 */
	@Secured
	public String login() {
		if (StringUtils.isEmpty(customer.getEmail())
				|| StringUtils.isEmpty(password)) {
			this.setMessageInfo("ERROR_CUSTOMER_EMAIL_NOT_EXISTS");
			return "fial";
		}
		try {
			customer = customerService.login(customer.getEmail().trim(), password,request.getRemoteAddr());
		} catch (BussinessException e) {
			this.setMessageInfo("ERROR_CUSTOMER_EMAIL_NOT_EXISTS");
			return "fial";
		}
		
		//缓存会员信息
		setLoginedUserBuyer(customer);
		if (StringUtils.isNotEmpty(ckIsCookie)) {
			ckIsCookice(customer.getEmail(), password);
		}else{
			WebUtils.removeCustomerCookie(request,response);
		}
		
		//登录成功后处理购物车相关操作
		dealShoppingCart();
		
		//登录前如果用户已使用现金券，则需要根据情况将无用户现金券转换成用户现金券
		changeCashCoupon();		
		
		String continueURL = getContinueURL();	
		if (StringUtils.isNotEmpty(continueURL)) {
			int index = continueURL.lastIndexOf(",");
			if (index > -1) {
				continueURL = continueURL.substring(index + 1).trim();
			}
			continueURL = org.apache.struts2.views.util.UrlHelper.translateAndDecode(continueURL);
			setContinueURL(continueURL);
			return CONTINUE_URL;
		}		
		return SUCCESS;
	}
	
	private void changeCashCoupon()
	{
		if( customer == null) return ;
		
		//如果发现使用了现金券，则判断是否需要将无用户现金券转换成由用户现金券来使用
		CashCoupon cashCoupon = SessionUtils.getCashCouponFromShoppingCart(request);
		if( cashCoupon != null)
		{
			MinShoppingCartTotalInfo minShoppingCartTotalInfo = getMinShoppingCartTotalInfo();
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
			setCashCouponForShoppingCart(cashCoupon);
		}
	}
	
	/**
	 *ajax判断用户是否登录 
	 */
	public void isLogin(){
		Customer userBuyer = (Customer)SessionUtils.getSession().getAttribute(SessionUtils.CUSTOMER_INFO);
		if (userBuyer != null){
			try {
				returnJson("[{\"result\":\"true\"}]");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				returnJson("[{\"result\":\"false\"}]");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * ajax用户登录
	 */
	public void ajaxLogin(){
		String email = param("email");
		String password = param("password");
		String ckIsCookie = param("ckIsCookie");
		if(this.customer == null){
			this.customer = new Customer();
		}
		if(email != null){
			this.customer.setEmail(email);
		}
		if(password != null){
			this.password = param("password");
		}
		if(ckIsCookie != null){
			this.ckIsCookie = ckIsCookie;
		}
		String loginReturn = login();
		if(loginReturn == "fial"){
			try {
				returnJson("[{\"result\":\"false\"}]");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				errorLog("change to json fail",e);
				e.printStackTrace();
			}
		}
		if(loginReturn == "continueURL" || loginReturn == "success" ){
			try {
				returnJson("[{\"result\":\"true\"}]");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 退出登录
	 * 
	 * @return
	 */
	@Secured
	public String logout() {
		WebUtils.removeCustomerCookie(request,response);
		WebUtils.removeShoppingCartCookie(request,response);
		SessionUtils.removeCashCouponFromShoppingCart(request);
		SessionUtils.removeMinShoppingCartInfo(request);
		OrderSessionUtils.clearSessionOrder(request);
		OrderSessionUtils.clearSessionOrderPage(request);

		if (getLoginedUserBuyer() == null ){
			return SUCCESS;
		}
		customerService.logout(getLoginedUserBuyer().getId());
		SessionUtils.removeLoginedCustomer(request);
		return getRefererUrl() ? CONTINUE_URL : SUCCESS;
	}

	private boolean getRefererUrl(){
		String refererUrl =  UrlHelper.getRefererUrl();
		if(StringUtils.isNotEmpty(refererUrl)){
			//通过登录推出
			SessionUtils.getSession().setAttribute("logout_url", true);
			setContinueURL(refererUrl);
			return true;
		}
		return false;
	}
	
	@Secured
	public String doFindPassword() {
		return SUCCESS;
	}

	/**
	 * 找回密码
	 * 
	 * @return
	 */
	@Secured
	public String findPassword() {
		String objVerifyCode = SessionUtils.getVerifyCode(request);
		if (objVerifyCode == null
				|| !(objVerifyCode).toUpperCase().equals(
						param("verifyCode").toUpperCase())) {
			this.setMessageInfo("ERROR_VERIFYCODE");
			return SUCCESS;
		}
		try {
			customerService.findPassword(this.customer.getEmail());
		} catch (BussinessException e) {
			this.setMessageInfo("ERROR_CUSTOMER_EMAIL_NOT_EXISTS");
			return SUCCESS;
		}
		this.setMessageInfo("SEND_SUCCESS");
		return SUCCESS;
	}

	private void ckIsCookice(String email, String password) {
		try {
			DesUtils des = new DesUtils();
			String desPassword = des.encrypt(password);
			String desEmail = des.encrypt(email);
			StringBuffer value = new StringBuffer();
			value.append("{\"email\":");
			value.append("\"" + desEmail + "\"");
			value.append(",\"password\":");
			value.append("\"" + desPassword + "\"");
			value.append("}");
			WebUtils.setCustomerCookie(request, response, value.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void dealShoppingCart()
	{
       //String continueURL = getContinueURL();
		
		Integer shoppingCartId = WebUtils.getShoppingCartId(request);
		//String httpCheckOutURL = request.getScheme() + "://" + request.getHeader("host") +request.getContextPath()+ checkOutURL;
		WebUtils.removeShoppingCartCookie(request,response);
		String loginRaw = param("loginRaw");
		//if( httpCheckOutURL.equalsIgnoreCase(continueURL))	
		if(loginRaw != null && loginRaw != "")
        {  //如果是直接点checkOut之后登录的，则将客户原购物车中的商品替换成临时购物车中的商品
			
			if( shoppingCartId != null)
			{
				shoppingCartId = shoppingService.changeCustomerShoppingCartProducts(shoppingCartId, customer.getId());
				
				//重新保存客户的购物车ID到Cookie
	        	WebUtils.setShoppingCartId(response,request, shoppingCartId);
			}
			else
			{
				shoppingCartId = shoppingService.getCustomerShoppingCartId(customer.getId());
				WebUtils.setShoppingCartId(response,request, shoppingCartId);
			}
        }
        else
        { 	//如果不是直接checkOut之后引起的登录则，则将临时购物车中的商品与客户原购物车中的商品进行合并
        	if( shoppingCartId != null)
			{
        		shoppingCartId = shoppingService.mergeShoppingCart(shoppingCartId, customer.getId());
        		
        		//重新保存客户的购物车ID到Cookie
            	WebUtils.setShoppingCartId(response,request, shoppingCartId);
			}
        	else
        	{
        		shoppingCartId = shoppingService.getCustomerShoppingCartId(customer.getId());
				WebUtils.setShoppingCartId(response,request, shoppingCartId);
        	}
        }
		
		refreshMiniShoppingCart(shoppingCartId);
	}
	private Boolean registerValidator() {
		if (customer == null)
			return false;
		else if (StringUtils.isEmpty(customer.getEmail()))
			return false;
		else if (!ValidateUtils.isEmail(customer.getEmail()))
			return false;
		else if (StringUtils.isEmpty(password))
			return false;
		else if (StringUtils.isEmpty(customer.getFirstName()))
			return false;
		else if (StringUtils.isEmpty(customer.getLastName()))
			return false;
		else
			return true;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCkIsCookie() {
		return ckIsCookie;
	}

	public void setCkIsCookie(String ckIsCookie) {
		this.ckIsCookie = ckIsCookie;
	}

	public String getRe_password() {
		return re_password;
	}

	public void setRe_password(String rePassword) {
		re_password = rePassword;
	}

	public String getIsNewsletter() {
		return isNewsletter;
	}

	public void setIsNewsletter(String isNewsletter) {
		this.isNewsletter = isNewsletter;
	}

}

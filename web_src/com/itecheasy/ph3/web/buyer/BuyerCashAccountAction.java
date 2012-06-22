package com.itecheasy.ph3.web.buyer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.itecheasy.common.Page;
import com.itecheasy.common.PageList;
import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.SearchOrder;
import com.itecheasy.ph3.customer.CashAccountService;
import com.itecheasy.ph3.order.CashCoupon;
import com.itecheasy.ph3.order.CashCouponService;
import com.itecheasy.ph3.order.CustomerCashCoupon;
import com.itecheasy.ph3.order.CashCouponService.CustomerCashCouponCriteria;
import com.itecheasy.ph3.order.CashCouponService.CustomerCashCouponSearchOrder;
import com.itecheasy.ph3.order.CashCouponService.CustomerCouponStatus;
import com.itecheasy.ph3.web.BuyerBaseAction;
import com.itecheasy.sslplugin.annotation.Secured;

@Secured
public class BuyerCashAccountAction extends BuyerBaseAction {
	private static final long serialVersionUID = 460101218L;
	private static String NAV_CashAccount = "CashAccount";
	private static String NAV_CashCoupon = "Coupon";
	private CashAccountService cashAccountService;
	private CashCouponService cashCouponService;
	public void setCashCouponService(CashCouponService cashCouponService) {
		this.cashCouponService = cashCouponService;
	}

	public void setCashAccountService(CashAccountService cashAccountService) {
		this.cashAccountService = cashAccountService;
	}

	/***************************************************************************
	 * 交易明细
	 * 
	 * @return
	 */
	public String searchCashAccountLogs() {
		pageSize = 20;
		if (currentPage == null)
			currentPage = 1;
		this.pageList = cashAccountService.searchCashAccountLogs(currentPage,
				pageSize, this.getLoginedUserBuyer().getId(), null, null);
		request.setAttribute("balance", cashAccountService.getBalance(this
				.getLoginedUserBuyer().getId()));
		setNavigation(NAV_CashAccount);
		return SUCCESS;
	}

	private void setNavigation(String navKey) {
		request.setAttribute("NavHover", navKey);
	}
	
	 /**
	  * 到买家账户现金券关联页面
	  */
	public String doCouponRelated(){
		//设置左侧导航选中
		pageSize = 20;
		setNavigation(NAV_CashCoupon);
		Integer userId = getLoginedUserBuyer().getId();
		//组合条件得到登陆用户已关联的现金券
		Map<CustomerCashCouponCriteria, Object> couponCriteria = new HashMap<CustomerCashCouponCriteria, Object>();
		couponCriteria.put(CustomerCashCouponCriteria.CUSTOMER_ID,userId);
		List<SearchOrder<CustomerCashCouponSearchOrder>> couponSearchOrder = new ArrayList<SearchOrder<CustomerCashCouponSearchOrder>>();
		couponSearchOrder.add(new SearchOrder<CustomerCashCouponSearchOrder>(CustomerCashCouponSearchOrder.STATUS,true));
		couponSearchOrder.add(new SearchOrder<CustomerCashCouponSearchOrder>(CustomerCashCouponSearchOrder.GRANT_DATE,false));
		this.pageList= cashCouponService.searchCustomerCashCoupon(currentPage, pageSize, couponCriteria,couponSearchOrder);
		return SUCCESS;
	}	
	 /**
	  * 买家账户现金券关联
	  */
	public String couponRelated(){
		String code = param("couponCode");
		Integer userId = getLoginedUserBuyer().getId();	
		if(!StringUtils.isEmpty(code)){
			try {					//现金券与用户进行关联，失败抛业务异常
				cashCouponService.grantCashCoupon(code, userId);
				request.setAttribute("message","SUCCESS_INFO");
			} catch (BussinessException e1) {
				request.setAttribute("message","RELATED_ERROR");
			}
		}
		return SUCCESS;
	}	
}

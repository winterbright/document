package com.itecheasy.ph3.web.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itecheasy.common.PageList;
import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.SearchOrder;
import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.customer.CustomerService;
import com.itecheasy.ph3.order.CashCoupon;
import com.itecheasy.ph3.order.CashCouponService;
import com.itecheasy.ph3.order.CustomerCashCoupon;
import com.itecheasy.ph3.order.CashCouponService.CashCouponCriteria;
import com.itecheasy.ph3.order.CashCouponService.CustomerCashCouponCriteria;
import com.itecheasy.ph3.order.CashCouponService.CustomerCashCouponSearchOrder;
import com.itecheasy.ph3.web.AdminBaseAction;
import com.itecheasy.ph3.web.utils.WebUtils;

public class AdminCashCouponAction extends AdminBaseAction {
	private static final long serialVersionUID = 1L;	
	private static final int defaultDeadLineDayCount = 30;
	private CashCouponService cashCouponService;
	private CustomerService customerService;
	
	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public void setCashCouponService(CashCouponService cashCouponService) {
		this.cashCouponService = cashCouponService;
	}
	
	/***
	 * 现金券管理列表
	 * 
	 * @return
	 */
	public String cashCouponList() {
		queryCashCouponList();
		return SUCCESS;
	}	
	
	private void queryCashCouponList()
	{
		int pageIndex = paramInt("currentPage",1);
		Map<CashCouponCriteria, Object> query = getQueryObject();

		PageList<CashCoupon> cashCouponPageList = cashCouponService.searchCommonCashConpon(pageIndex, 20, query);
	    List<CashCoupon> cashCouponList = new ArrayList<CashCoupon>();
	    
	    if (cashCouponPageList.getData() != null && !cashCouponPageList.getData().isEmpty()) 
	    {
	    	cashCouponList = (List<CashCoupon>)cashCouponPageList.getData();
	    }
	    
		request.setAttribute("cashCouponList", cashCouponList);
		request.setAttribute("query_useableQty", paramInt("query_useableQty"));
		request.setAttribute("beginDate", paramDate("beginDate"));
		request.setAttribute("endDate", paramDate("endDate"));
		request.setAttribute("query_amount", paramInt("query_amount"));
		
		request.setAttribute("serviceDate",new Date());
		request.setAttribute("defaultDeadLine",getDefaultDeadLine());
		request.setAttribute("page",cashCouponPageList.getPage());
		request.setAttribute("currentPage",pageIndex);
	}
	
	private Date getDefaultDeadLine()
	{
		Date now = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(now);
		calendar.add(Calendar.DATE, defaultDeadLineDayCount);
		
		return calendar.getTime();
	}
	
	private Map<CashCouponCriteria, Object> getQueryObject()
	{
		Integer useableQty = paramInt("query_useableQty");
		Date beginDate = paramDate("beginDate");
		Date endDate = paramDate("endDate");
		Integer amount = paramInt("query_amount");
		
		Map<CashCouponCriteria, Object> query = new HashMap<CashCouponCriteria, Object>();
		if(useableQty != null && useableQty != 0 ){		
			switch(useableQty)
			{
				case 1:
					query.put(CashCouponCriteria.USEABLE_QTY_MIN, 1);
					query.put(CashCouponCriteria.USEABLE_QTY_MAX, 1);
					break;
				case 2:
					query.put(CashCouponCriteria.USEABLE_QTY_MIN, 2);
					query.put(CashCouponCriteria.USEABLE_QTY_MAX, 20);
					break;
				case 3:
					query.put(CashCouponCriteria.USEABLE_QTY_MIN, 21);
					break;
			}			
		}		
		if(beginDate != null ){			 
			beginDate.setHours(0);
			beginDate.setMinutes(0);
			beginDate.setSeconds(0);
			query.put(CashCouponCriteria.EXPIRE_DATE_START, beginDate);
		}
		if(endDate != null ){			 
			endDate = WebUtils.getLongDateTime(endDate);
		
			query.put(CashCouponCriteria.EXPIRE_DATE_END, endDate);
		}
		if( amount != null && amount != 0)
		{
			switch(amount)
			{
				case 1:
					query.put(CashCouponCriteria.AMOUNT_MAX, new BigDecimal("9.9999"));
					break;
				case 2:
					query.put(CashCouponCriteria.AMOUNT_MIN, new BigDecimal(10));
					query.put(CashCouponCriteria.AMOUNT_MAX, new BigDecimal(30));
					break;
				case 3:
					query.put(CashCouponCriteria.AMOUNT_MIN, new BigDecimal("30.0001"));
					break;
			}
		}
		
		return query;
	}
	
	/***
	 * 添加现金券
	 * 
	 * @return
	 */
	public String addCashCoupon() {
	    boolean isError = false;
		Integer useableQty = paramInt("useable_qty");
		if( useableQty == null || useableQty <0 || useableQty>999999 )
		{
			request.setAttribute("useableQtyError", "可用次数只能输入6位正整数");
			isError = true;
		}
		Date validDate = paramDate("valid_date");
		if( validDate == null )
		{
			request.setAttribute("validDateError", "请将信息输入完整");
			isError = true;
		}
		Integer amount = paramInt("coupon_amount");
		if( amount == null || amount <0 || amount >999 )
		{
			request.setAttribute("amountError", "现金券面值只能输入3位正整数");
			isError = true;
		}
		Integer minOrderAmount = paramInt("min_order_amount");
		if( minOrderAmount == null || minOrderAmount <0 || minOrderAmount>9999 )
		{
			request.setAttribute("minOrderAmountError", "最小订单限额只能输入4位正整数");
			isError = true;
		}
		
		if(isError)
		{
			request.setAttribute("isAddError", true);
			request.setAttribute("useable_qty", useableQty);
			request.setAttribute("valid_date", validDate);
			request.setAttribute("coupon_amount", amount);
			request.setAttribute("minOrderAmount", minOrderAmount);
			queryCashCouponList();
			return ERROR;
		}
		else
		{
			validDate.setHours(23);
			validDate.setMinutes(59);
			validDate.setSeconds(59);
			
			cashCouponService.awardCashCoupon(new BigDecimal(amount), new BigDecimal(minOrderAmount), useableQty, validDate, this.getLoginUserAdmin().getId());
			queryCashCouponList();
			return SUCCESS;
		}
	}	
	
	/***
	 * 删除现金券
	 * 
	 * @return
	 */
	public String deleteCashCoupon() {
		Integer cashCouponId = paramInt("id");
		if( cashCouponId != null)
		{
			try
			{
				cashCouponService.deleteCashCoupon(cashCouponId);
			}
			catch(BussinessException ex)
			{
				if( ex.getErrorMessage() == CashCouponService.ERROR_CASH_COUPON_IS_USED)
				{
					this.messageInfo = "只用未被使用过的现金券才可以被删除!";
				}
			}			
		}
		
		queryCashCouponList();
		return SUCCESS;
	}
	
	/***
	 * 屏蔽/激活 现金券
	 * 
	 * @return
	 */
	public String updateCashCouponStatus() {
		Integer cashCouponId = paramInt("id");
		Integer stauts = paramInt("stauts");
		if( cashCouponId != null)
		{
			try
			{
				if( stauts == 0)
				{
					cashCouponService.disableCashCoupon(cashCouponId);
				}
				else if ( stauts == 1)
				{
					cashCouponService.activateCashCoupon(cashCouponId);
				}				
			}
			catch(BussinessException ex)
			{
			}			
		}
		
		queryCashCouponList();
		return SUCCESS;
	}
	
	/**
	 * 获取已关联用户查询条件
	 */
	private Map<CustomerCashCouponCriteria, Object> getCustomerCashCouponQueryObject(){
		Date beginDate = paramDate("beginDate");
		Date endDate = paramDate("endDate");
		Integer customerType = paramInt("customerType",1);
		String email = param("customerEmail");
		Integer status = paramInt("statusChoose");
		Map<CustomerCashCouponCriteria, Object> query = new HashMap<CustomerCashCouponCriteria, Object>();
		if(beginDate != null ){			 
			beginDate.setHours(0);
			beginDate.setMinutes(0);
			beginDate.setSeconds(0);
			query.put(CustomerCashCouponCriteria.EXPIRE_DATE_START, beginDate);
		}
		if(endDate != null ){			 
			endDate = WebUtils.getLongDateTime(endDate);
			query.put(CustomerCashCouponCriteria.EXPIRE_DATE_END, endDate);
		}
		if(email != null && email !=""){
			Customer customer = null;
			if(customerType==2){
				customer = customerService.getPaypalCustomer(email);
			}else {
				customer = customerService.getCustomerByEmail(email);
			}
			if(customer != null){
				query.put(CustomerCashCouponCriteria.CUSTOMER_ID, customer.getId());
			}
			else
			{
				query.put(CustomerCashCouponCriteria.CUSTOMER_ID, -1);
			}
		}
		if(status != null&&status !=0){
			query.put(CustomerCashCouponCriteria.STATUS, status);
		}
		
		return query;
	}
	
	/**
	 * 已关联客户现金券列表
	 */
	private void queryCashCouponUserList(){
		int pageIndex = paramInt("currentPage",1);
		Map<CustomerCashCouponCriteria, Object> customerCouponQuery = getCustomerCashCouponQueryObject();
		List<SearchOrder<CustomerCashCouponSearchOrder>> searchOrders = new ArrayList<SearchOrder<CustomerCashCouponSearchOrder>>();
		SearchOrder<CustomerCashCouponSearchOrder> searchOrder = new SearchOrder<CustomerCashCouponSearchOrder>(CustomerCashCouponSearchOrder.GRANT_DATE,false);
		searchOrders.add(searchOrder);
		PageList<CustomerCashCoupon> customerCashCouponPageList = cashCouponService.searchCustomerCashCoupon(pageIndex,20,customerCouponQuery, searchOrders);
		List<CustomerCashCoupon> customerCashCouponList = new ArrayList<CustomerCashCoupon>();
		
		 if (customerCashCouponPageList.getData() != null && !customerCashCouponPageList.getData().isEmpty()) 
		    {
			 	customerCashCouponList = (List<CustomerCashCoupon>)customerCashCouponPageList.getData();
		    }
	 	request.setAttribute("customerCashCouponList", customerCashCouponList);
		request.setAttribute("page",customerCashCouponPageList.getPage());
		request.setAttribute("defaultDeadLine",getDefaultDeadLine());
		request.setAttribute("beginDate", paramDate("beginDate"));
		request.setAttribute("endDate", paramDate("endDate"));
		request.setAttribute("customerType", paramInt("customerType",1));
		request.setAttribute("customerEmail",param("customerEmail") );
		request.setAttribute("statusChoose", paramInt("statusChoose"));
		request.setAttribute("currentPage",pageIndex);
	}
	
	public String cashCouponUserList(){
		queryCashCouponUserList();
		return SUCCESS;
	}
	
	/**
	 * 添加客户现金券
	 */
	public String addCustomerCashCoupon(){
		boolean isError = false;
		String email = param("email");
		Pattern pattern = Pattern.compile("^\\w+([-.]\\w+)*@\\w+([-]\\w+)*\\.(\\w+([-]\\w+)*\\.)*[a-z]{2,3}$");
		Matcher matcher = pattern.matcher(email);
		Customer customer = customerService.getCustomerByEmail(email);
		if(email == null){
			request.setAttribute("validEmailError", "请输入Email");
			isError = true;
		}else if(!matcher.matches()){
			request.setAttribute("validEmailError", "请输入有效的Email");
			isError = true;
		}else if(customer == null){
			request.setAttribute("validEmailError", "无法找到客户信息!");
			isError = true;
		}
		Date validDate = paramDate("valid_date");
		if( validDate == null )
		{
			request.setAttribute("validDateError", "请将信息输入完整");
			isError = true;
		}
		Integer amount = paramInt("coupon_amount");
		if( amount == null || amount <0 || amount >999 )
		{
			request.setAttribute("amountError", "现金券面值只能输入3位正整数");
			isError = true;
		}
		Integer minOrderAmount = paramInt("min_order_amount");
		if( minOrderAmount == null || minOrderAmount <0 || minOrderAmount>9999 )
		{
			request.setAttribute("minOrderAmountError", "最小订单限额只能输入4位正整数");
			isError = true;
		}
		
		if(isError)
		{
			request.setAttribute("isAddError", true);
			request.setAttribute("email", email);
			request.setAttribute("valid_date", validDate);
			request.setAttribute("coupon_amount", amount);
			request.setAttribute("minOrderAmount", minOrderAmount);
			queryCashCouponUserList();
			return ERROR;
		}else{
			validDate.setHours(23);
			validDate.setMinutes(59);
			validDate.setSeconds(59);			
			cashCouponService.awardCustomerCashCoupon(new BigDecimal(amount), new BigDecimal(minOrderAmount), validDate, this.getLoginUserAdmin().getId(), customer.getId());
			queryCashCouponUserList();
			return SUCCESS;
		}
	}
}

package com.itecheasy.ph3.web.admin;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.xwork.StringUtils;

import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.customer.CashAccountLog;
import com.itecheasy.ph3.customer.CashAccountService;
import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.customer.CustomerService;
import com.itecheasy.ph3.customer.CustomerService.CustomerSearchCriteria;
import com.itecheasy.ph3.web.AdminBaseAction;

public class AdminCashAccountAction extends AdminBaseAction {
	private static final long serialVersionUID = 1L;
	private CashAccountLog cashAccountLog;
	
	private CashAccountService cashAccountService;
	private CustomerService customerService; 
	
	private String id;
	private String email;
	private Integer customerType;
	
	/***
	 * 交易明细
	 * 
	 * @return
	 */
	public String searchCashAccount() {
		if(this.currentPage ==null)
			this.currentPage = 1;
		
		Map<CustomerSearchCriteria,Object> searchCriteria  = new HashMap<CustomerSearchCriteria,Object>();
		if(StringUtils.isNotEmpty(this.email)){
			searchCriteria.put(CustomerSearchCriteria.EMAIL, this.email);
			if(paramInt("customerType")!=null &&  paramInt("id") == null){
				searchCriteria.put(CustomerSearchCriteria.CUSTOMER_TYPE, paramInt("customerType"));
				request.setAttribute("customerType", paramInt("customerType"));
			}		
		}		
		if(this.paramInt("id")!=null){
			searchCriteria.put(CustomerSearchCriteria.ID, this.paramInt("id"));
		}
		if(searchCriteria.entrySet().size()==0){
			return SUCCESS;
		}
		this.pageList =  customerService.searchCustomers(1, PAGE_SIZE, searchCriteria, null);
		if(this.pageList.getData().size()== 0 ){
			return SUCCESS;
		}
		Customer customer = (Customer)pageList.getData().get(0);
		this.pageList = cashAccountService.searchCashAccountLogs(currentPage, PAGE_SIZE, customer.getId(), null, null);
		request.setAttribute("balance", cashAccountService.getBalance(customer.getId())) ;
		request.setAttribute("customerType",customer.getType());
		request.setAttribute("customer", customer);
		//request.setAttribute("cashAccountLogs", this.pageList.getData());
		return SUCCESS;
	}
	

	/***
	 * 充值
	 * 
	 * @return
	 */
	public String deposit() {
		Integer customerId = this.paramInt("customerId");
		BigDecimal tradeMoney = new BigDecimal(this.param("tradeMoney"));
		String remark = this.param("remark");		
		cashAccountService.deposit(customerId, tradeMoney, CashAccountService.OperatorType.ADMIN_USER, this.getLoginUserAdmin().getId(), remark);
		customerType = paramInt("customerType");
		return SUCCESS;
	}
	
	/***
	 * 提现
	 * 
	 * @return
	 */
	public String withdraw() {
		Integer customerId = this.paramInt("customerId");
		BigDecimal tradeMoney = new BigDecimal(this.param("tradeMoney"));
		String remark = this.param("remark");
		try {
			cashAccountService.withdraw(customerId, tradeMoney, CashAccountService.OperatorType.ADMIN_USER, this.getLoginUserAdmin().getId(), remark);
		} catch (BussinessException e) {
			this.setMessageInfo("ERROR_CASH_ACCOUNT_BALANCE_INSUFFICIENT"); //余额不足
		}
		customerType = paramInt("customerType");
		return SUCCESS;
	}
	
	/***
	 * 冲账
	 * 
	 * @return
	 */
	public String offsetMoney() {
		Integer customerId = this.paramInt("customerId");
		BigDecimal tradeMoney = new BigDecimal(this.param("tradeMoney"));
		String remark = this.param("remark");
		Integer type = this.paramInt("type");
		if(type == 1)
			cashAccountService.offsetMoney(customerId, tradeMoney, CashAccountService.OffseType.INCOME, CashAccountService.OperatorType.ADMIN_USER, this.getLoginUserAdmin().getId(), remark);
		else if(type == 2)
			cashAccountService.offsetMoney(customerId, tradeMoney, CashAccountService.OffseType.PAY, CashAccountService.OperatorType.ADMIN_USER, this.getLoginUserAdmin().getId(), remark);
		else
			this.setMessageInfo("ERROR_TYPE");
		
		customerType = paramInt("customerType");
		return SUCCESS;
	}

	
	
	
	public CashAccountLog getCashAccountLog() {
		return cashAccountLog;
	}


	public void setCashAccountLog(CashAccountLog cashAccountLog) {
		this.cashAccountLog = cashAccountLog;
	}


	public void setCashAccountService(CashAccountService cashAccountService) {
		this.cashAccountService = cashAccountService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}
	
	
	public Integer getCustomerType() {
		return customerType;
	}


	public void setCustomerType(Integer customerType) {
		this.customerType = customerType;
	}
}

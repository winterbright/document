package com.itecheasy.ph3.web.buyer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.Session;

import org.apache.commons.lang.StringUtils;

import com.itecheasy.common.PageList;
import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.SearchOrder;
import com.itecheasy.ph3.customer.Address;
import com.itecheasy.ph3.customer.AgeRange;
import com.itecheasy.ph3.customer.CashAccountService;
import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.order.Order;
import com.itecheasy.ph3.order.OrderItem;
import com.itecheasy.ph3.order.OrderService;
import com.itecheasy.ph3.order.OrderStatus;
import com.itecheasy.ph3.order.OrderService.OrderSearchCriteria;
import com.itecheasy.ph3.order.OrderService.OrderSearchOrder;
import com.itecheasy.ph3.system.Country;
import com.itecheasy.ph3.system.DictionaryService;
import com.itecheasy.ph3.web.BuyerBaseAction;
import com.itecheasy.ph3.web.exception.AppException;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.vo.CookieArea;
import com.itecheasy.ph3.web.vo.OrderVO;
import com.itecheasy.sslplugin.annotation.Secured;

@Secured
public class BuyerMyAccountAction extends BuyerBaseAction {
	private static final long serialVersionUID = 122225455L;
	private static final int ORDERITEM_SIZE = 6;
	private static final int PAGE_SIZE = 4;

	private static final String NAV_AccountSetting = "AccountSetting";
	private static final String NAV_ChangePassword = "ChangePassword";
	private static final String NAV_AddressBook = "AddressBook";
	private static final String NAV_BillingAddress = "BillingAddress";
	private static final String NAV_RelatePayPalAccount = "RelatePayPalAccount";

	private CashAccountService cashAccountService;
	private DictionaryService dictionaryService;
	private OrderService orderService;

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public void setCashAccountService(CashAccountService cashAccountService) {
		this.cashAccountService = cashAccountService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public String doMyaccount() {
		Integer customerId = this.getLoginedUserBuyer().getId();

		int shipped = OrderService.ORDER_STATUS_SHIPPED;
		int packing = OrderService.ORDER_STATUS_PACKAGING;
		int preparing = OrderService.ORDER_STATUS_PREPARING;
		int pending = OrderService.ORDER_STATUS_PENDING_CONFIRMATION;
		int awating_shipment = OrderService.ORDER_STATUS_AWAITING_SHIPMENT;

		Map<OrderSearchCriteria, Object> searchCriteria = new HashMap<OrderSearchCriteria, Object>();
		List<SearchOrder<OrderSearchOrder>> searchOrder = new LinkedList<SearchOrder<OrderSearchOrder>>();
		List<OrderStatus> orderStatusList = new ArrayList<OrderStatus>();
		List<Integer> searchStatusIds = new LinkedList<Integer>();
		orderStatusList = orderService.getOrderStatuses();
		if (orderStatusList != null) {
			for (OrderStatus item : orderStatusList) {
				searchStatusIds.add(item.getId());
			}
		}
		searchCriteria.put(OrderSearchCriteria.ORDER_STATUS, searchStatusIds);
		searchCriteria.put(OrderSearchCriteria.CUSTOMER_ID, customerId);
		searchOrder.add(new SearchOrder<OrderSearchOrder>(
				OrderSearchOrder.ORDER_DATE, false));

		PageList<Order> list = orderService.searchOrders(currentPage,
				PAGE_SIZE, searchCriteria, searchOrder);
		List<Order> orderList = null;
		List<OrderItem> orderItemList = null;
		PageList<OrderItem> orderItemPageList = null;
		OrderVO orderVO = null;
		List<OrderVO> OrderVOList = null;
		int orderCount = 0;
		Integer statusInteger;
		BigDecimal totalConsumption = orderService
				.getOrderConsumptionTotal(customerId);
		if (list != null) {
			orderList = list.getData();
			orderCount = list.getPage().getTotalRowCount();
			if (orderList != null) {
				OrderVOList = new ArrayList<OrderVO>();
				for (Order order : orderList) {
					orderVO = new OrderVO();
					orderVO.setOrder(order);
					statusInteger = order.getOrderStatus().getId();
					orderItemPageList = orderService.getOrderDetails(currentPage, ORDERITEM_SIZE, order.getId(),OrderService.OrderDetailType.VALID);
					if (orderItemPageList != null) {
						orderItemList = orderItemPageList.getData();
						if (orderItemPageList.getPage().getTotalRowCount() > ORDERITEM_SIZE) {
							orderVO.setShowMore(true);
						} else {
							orderVO.setShowMore(false);
						}
						if (statusInteger == pending || statusInteger==8) {
							orderVO.setShowCancel(true);
						}
						if (statusInteger == shipped) {
							orderVO.setShowComplete(true);
						}
						orderVO.setOrderItemList(orderItemList);
					}

					OrderVOList.add(orderVO);
				}
			}
		}

		request.setAttribute("orderCount", orderCount);
		request.setAttribute("orderList", OrderVOList);
		request.setAttribute("totalConsumption", totalConsumption);
		request.setAttribute("preparing", preparing);
		request.setAttribute("awating_shipment", awating_shipment);
		request.setAttribute("balance", cashAccountService
				.getBalance(customerId));
		request.setAttribute("packing", packing);
		request.setAttribute("shipped", shipped);
		request.setAttribute("pending", pending);
		return SUCCESS;
	}

	public String doAccountSetting() {
		Integer customerId = this.getLoginedUserBuyer().getId();
		doCustomer(customerId);
		setCountry();
		List<AgeRange> ageRangeList = customerService.getAgeRages();

		request.setAttribute("ageRangeList", ageRangeList);
		setNavigation(NAV_AccountSetting);
		return SUCCESS;
	}

	public String saveAccountSetting() {
		String firstName = param("firstName");
		String lastName = param("lastName");
		String phone = param("phone");
		Integer gender = paramInt("gender", 0);
		Integer countryId = paramInt("country", 0);
		Integer ageGroupId = paramInt("ageGroup", 0);
		Integer customerId = this.getLoginedUserBuyer().getId();
		if (!checkSetting(firstName, lastName)) {
			this.setMessageInfo("INPUT_INFO");
			return "fail";
		}
		Customer customer = customerService.getCustomer(customerId);
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		customer.setPhone(phone);
		customer.setCountryId(countryId);
		customer.setAgeGroupId(ageGroupId);
		customer.setGender(gender);
		customerService.updateCustomerInfo(customer);

		SessionUtils.setLoginedCustomer(request, customer);

		this.setMessageInfo("SUCCESS_INFO");
		setNavigation(NAV_AccountSetting);
		return SUCCESS;
	}

	public String doPassword() {
		Integer customerId = this.getLoginedUserBuyer().getId();
		doCustomer(customerId);
		setNavigation(NAV_ChangePassword);
		return SUCCESS;
	}

	/**
	 * 修改密码
	 * 
	 * @return
	 */
	public String changePassword() {
		String oldPsd = param("oldPsd");
		String newPsd = param("reNewPsd");
		Integer customerId = this.getLoginedUserBuyer().getId();

		doCustomer(customerId);
		if (checkValidate(customerId, oldPsd, newPsd)) {
			try {
				customerService.updatePassword(customerId, oldPsd, newPsd);
			} catch (Exception e) {
				this.setMessageInfo("ERROR_CUSTOMER_PASSWORD_ERROR");
				return "fail";
			}
			this.setMessageInfo("SUCCESS_INFO");
		}
		setNavigation(NAV_ChangePassword);
		return SUCCESS;
	}

	/**
	 * 获取货运地址列表
	 */
	public String doShippingAddresses() {
		Integer customerId = this.getLoginedUserBuyer().getId();
		List<Address> addressList = customerService
				.getShippingAddresses(customerId);
		// 获取是否有默认地址
		Address defaultShippingAddress = customerService
				.getDefaultShippingAddress(customerId);
		Integer showDefaultId = 0;
		if (defaultShippingAddress != null) {
			showDefaultId = defaultShippingAddress.getId();
		}
		request.setAttribute("addressList", addressList);
		request.setAttribute("showDefaultId", showDefaultId);
		setNavigation(NAV_AddressBook);
		return SUCCESS;
	}

	/**
	 * 获取货运地址
	 */
	public String doShippingAddress() {
 		Integer addressId = paramInt("id", 0);
		Address address = null;
		Integer customerId = this.getLoginedUserBuyer().getId();
		boolean checkDefault = false;// 是否勾选默认地址
		if(addressId==0){
			CookieArea cookieArea = SessionUtils.getAreaInfo(request.getSession());
			request.setAttribute("defaultCountryId", cookieArea.getCountry().getId());	
		}else if(addressId>0){
			// 获取地址详细信息 edit
			address = customerService.getShippingAddress(customerId, addressId);
			// 获取是否有默认地址
			checkDefault = isDefaultShippingAddress(customerId, addressId);
		}
		setCountry();
		request.setAttribute("address", address);
		request.setAttribute("checkDefault", checkDefault);
		setNavigation(NAV_AddressBook);
		return SUCCESS;
	}

	/**
	 * 删除货运地址
	 */
	public String deleteShippingAddress() {
		Integer addressId = paramInt("id", 0);
		Integer customerId = this.getLoginedUserBuyer().getId();
		boolean checkDefault = false;
		// 获取是否有默认地址
		checkDefault = isDefaultShippingAddress(customerId, addressId);
		if (addressId > 0) {
			if (!checkDefault) {
				customerService.deleteShippingAddress(customerId, addressId);
			}
		}
		setNavigation(NAV_AddressBook);
		return SUCCESS;
	}
	

	/**
	 * 保存货运地址
	 */
	public String saveShippingAddress() throws AppException {
		Integer addressId = paramInt("addressId", 0);
		String firstName = param("firstName");
		String lastName = param("lastName");
		String phone = param("phone");
		String fax = param("fax");
		String street1 = param("street1");
		String street2 = param("street2");
		String city = param("city");
		String state = param("state");
		Integer countryId = paramInt("country", 0);
		String zip = param("zip");
		boolean ckdefaultAddress = (param("ckdefaultAddress", null) != null);
		if (!validata(firstName, lastName, phone, street1, city, state, zip)) {
			throw new RuntimeException("Please enter your valid infomation.");
		}
		Country country = dictionaryService.getCountry(countryId);
		Integer customerId = this.getLoginedUserBuyer().getId();
		Address address = null;
		if (country == null) {
			throw new RuntimeException("Cannot find the country(ID:"
					+ countryId + ").");
		}
		if (addressId > 0) {
			// 编辑地址
			address = customerService.getShippingAddress(customerId, addressId);
		}
		if (address != null) {
			setAddress(address, firstName, lastName, phone, fax, street1,
					street2, city, state, country, zip);
			customerService.updateShippingAddress(customerId, address);
		} else {
			// 添加地址
			address = new Address();
			setAddress(address, firstName, lastName, phone, fax, street1,
					street2, city, state, country, zip);
			addressId = customerService.addShippingAddress(customerId, address);
			address.setId(addressId);
		}

		if (ckdefaultAddress) {
			// 设置默认地址
			customerService.setDefaultShippingAddress(customerId, addressId);
		} else {
			// 取消默认地址
			customerService.cancelDefaultShippingAddress(customerId, addressId);
		}

		setCountry();
		request.setAttribute("address", address);
		request.setAttribute("checkDefault", ckdefaultAddress);
		this.setMessageInfo("SUCCESS_INFO");
		setNavigation(NAV_AddressBook);
		return SUCCESS;
	}

	/**
	 * 获取账单地址
	 */
	public String doBillingAddress() {
		Integer customerId = this.getLoginedUserBuyer().getId();
		Address address = customerService.getBillingAddress(customerId);
		setCountry();
		CookieArea cookieArea = SessionUtils.getAreaInfo(request.getSession());
		request.setAttribute("defaultCountryId", cookieArea.getCountry().getId());
		request.setAttribute("address", address);
		setNavigation(NAV_BillingAddress);
		return SUCCESS;
	}

	/**
	 * 保存账单地址
	 */
	public String saveBillingAddress() throws AppException {
		Integer addressId = paramInt("addressId", 0);
		String firstName = param("firstName");
		String lastName = param("lastName");
		String phone = param("phone");
		String street1 = param("street1");
		String street2 = param("street2");
		String city = param("city");
//		String state = param("state");
		String state = request.getParameter("state");
		Integer countryId = paramInt("country", 0);
		String zip = param("zip");

		if (!validata(firstName, lastName, phone, street1, city, state, zip)) {
			throw new RuntimeException("Please enter your valid infomation.");
		}
		Address address = null;
		Integer returnID = 0;
		Integer customerId = this.getLoginedUserBuyer().getId();
		Country country = dictionaryService.getCountry(countryId);
		if (country == null) {
			throw new RuntimeException("Cannot find the country(ID:"
					+ countryId + ").");
		}
		if (addressId > 0) {
			// 编辑地址
			address = customerService.getBillingAddress(customerId);
		}
		if (address != null) {
			setAddress(address, firstName, lastName, phone, null, street1,
					street2, city, state, country, zip);
		} else {
			// 添加地址
			address = new Address();
			setAddress(address, firstName, lastName, phone, null, street1,
					street2, city, state, country, zip);
		}

		returnID = customerService.setBilliingAddress(customerId, address);

		if (returnID > 0) {
			this.setMessageInfo("SUCCESS_INFO");
		}
		setCountry();
		request.setAttribute("address", address);
		setNavigation(NAV_BillingAddress);
		return SUCCESS;
	}
	/**
	 * 去关联PalPal页面
	 * @return
	 */
	public String doRelatePayPalAccount(){
		//设置导航
		setNavigation(NAV_RelatePayPalAccount);
		String relateStatus = "notRelate";//关联状态
		//调用业务方法得到当前用户关联的PayPalEmail
		String payPalEmail = customerService.getPaypalEmailByCustomterId(getLoginedUserBuyer().getId());
		if(payPalEmail!=null){//判断是否已关联PayPal账号
			relateStatus = "relate";
			request.setAttribute("email",payPalEmail);
		}
		request.setAttribute("relateStatus",relateStatus);
		return SUCCESS;
	}
	/**
	 * 关联PayPal账号
	 * @return
	 */
	public String relatePayPalAccount(){
		setNavigation(NAV_RelatePayPalAccount);		//设置导航
		String relateStatus = "sent";		//关联的状态
		String email = param("email");
		String isReSend = param("isReSend"); //是否点了重复提交按钮
		Integer userId = getLoginedUserBuyer().getId();	
		//调用业务方法得到当前用户关联的PayPalEmail
		String payPalEmail = customerService.getPaypalEmailByCustomterId(userId);
		if(payPalEmail!=null){//判断是否关联PayPal账号
			relateStatus = "relate";
			email = payPalEmail;
		}else{
			if(isReSend==null||isReSend==""){//是点击了关联按钮，就要进行验证码检查
				String objVerifyCode = SessionUtils.getVerifyCode(request);
				if (objVerifyCode == null || !(objVerifyCode).toUpperCase().equals(
						 param("verifyCode").toUpperCase())) {
					this.setMessageInfo("ERROR_VERIFYCODE");
					relateStatus = "VERIFYCODE_ERROR";
					request.setAttribute("payPalEmail", email);
				}else if(email!=null&&email!=""){			
					try {//调用业务方法发送邮件
						customerService.relatePaypalCustomter(email, userId);
					} catch (BussinessException e) {
						relateStatus = "RELATE_FAIL";
						setMessageInfo(e.getErrorMessage());
					}
				}
			}else if(email!=null&&email!=""){			
				try {//调用业务方法发送邮件
					customerService.relatePaypalCustomter(email, userId);
				} catch (BussinessException e) {
					setMessageInfo(e.getErrorMessage());
				}
			}
		}		
		request.setAttribute("relateStatus",relateStatus);
		request.setAttribute("email", email);
		return SUCCESS;
	}
	private void setNavigation(String navKey) {
		request.setAttribute("NavHover", navKey);
	}

	private boolean checkSetting(String firstName, String lastName) {
		if (StringUtils.isEmpty(firstName)) {
			return false;
		} else if (StringUtils.isEmpty(lastName)) {
			return false;
		}
		return true;
	}

	private void doCustomer(Integer customerId) {
		Customer customer = customerService.getCustomer(customerId);
		if(customer.getCountryId()==null){
			CookieArea cookieArea =SessionUtils.getAreaInfo(request.getSession());			
			customer.setCountryId(cookieArea.getCountry().getId());
		}
		request.setAttribute("customer", customer);
	}

	private boolean checkValidate(Integer customerId, String oldPsd,
			String newPsd) {
		String fomat = "^[A-Za-z0-9]{4,100}$";
		if (customerId <= 0) {
			return false;
		} else if (StringUtils.isEmpty(oldPsd)) {
			return false;
		} else if (!checkFomat(fomat, oldPsd)) {
			return false;
		} else if (StringUtils.isEmpty(newPsd)) {
			return false;
		} else if (!checkFomat(fomat, newPsd)) {
			return false;
		}
		return true;
	}

	private boolean isDefaultShippingAddress(Integer customerId,
			Integer addressId) {
		// 获取是否有默认地址
		return customerService.isDefaultShippingAddress(customerId, addressId);
	}

	private void setAddress(Address address, String firstName, String lastName,
			String phone, String fax, String street1, String street2,
			String city, String state, Country country, String zip) {
		address.setFirstName(firstName.trim());
		address.setLastName(lastName.trim());
		address.setPhone(phone.trim());
		if (fax != null && !fax.isEmpty()) {
			address.setFax(fax.trim());
		} else {
			address.setFax(null);
		}
		address.setStreet1(street1.trim());
		if (street2 != null && !street2.isEmpty()) {
			address.setStreet2(street2.trim());
		} else {
			address.setStreet2(null);
		}
		address.setCity(city.trim());
		address.setState(state.trim());
		address.setCountry(country);
		address.setZip(zip.trim());
	}

	/**
	 * 验证输入信息
	 * 
	 * @param firstName
	 * @param lastName
	 * @param phone
	 * @param street1
	 * @param city
	 * @param state
	 * @param zip
	 * @return
	 */
	private boolean validata(String firstName, String lastName, String phone,
			String street1, String city, String state, String zip) {
		if (firstName == null || firstName.trim().isEmpty()) {
			return false;
		}
		if (lastName == null || lastName.trim().isEmpty()) {
			return false;
		}
		if (phone == null || phone.trim().isEmpty()) {
			return false;
		}
		if (street1 == null || street1.trim().isEmpty()) {
			return false;
		}
		if (city == null || city.trim().isEmpty()) {
			return false;
		}
		if (state == null || state.trim().isEmpty()) {
			return false;
		}
		if (zip == null || zip.trim().isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * 所有国家
	 */
	private void setCountry() {
		// 获取所有国家
		List<Country> countries = dictionaryService.getAllCountries();
		List<Country> commonCountries = dictionaryService.getAllCommonCountries();
		if (countries != null) {
			request.setAttribute("countries", countries);
		}
		if(commonCountries != null){
			request.setAttribute("commonCountries", commonCountries);
		}
	}

	private static boolean checkFomat(String fomat, String password) {
		Pattern p = Pattern.compile(fomat);
		Matcher m = p.matcher(password);
		if (!m.find()) {
			return false;
		}
		return true;
	}

}

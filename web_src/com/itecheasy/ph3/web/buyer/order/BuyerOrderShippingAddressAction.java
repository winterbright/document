package com.itecheasy.ph3.web.buyer.order;

import java.io.IOException;
import java.util.List;

import com.itecheasy.ph3.customer.Address;
import com.itecheasy.ph3.system.Country;
import com.itecheasy.ph3.web.buyer.BuyerPageController.PlaceOrderPage;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.vo.CookieArea;
import com.itecheasy.sslplugin.annotation.Secured;

@Secured
public class BuyerOrderShippingAddressAction extends BuyerPlaceOrderBaseAction {
	private static final long serialVersionUID = 9881666L;

	/**
	 * 获取货运地址
	 */
	public String doAddresses() 
	{
		String commandName = param("commandName");
		if ( commandName == null || commandName.equals(""))
		{//初始化，第一次进入页面
			return initAddresses();
		}
		else
		{//在本页面刷新
			return postAddresses();
		}
	}
	
	private  String initAddresses()
	{
		Integer customerId = this.getLoginedUserBuyer().getId();
		
		SessionOrder sessionOrder = getSessionOrder();		
		String checkResult = checkSessionOrder(PlaceOrderPage.PAGE_SHIPPING_ADDRESS);
		if( checkResult != null)
		{
			return checkResult;
		}
		
		//获取用户所有的货运地址
		List<Address> shippingAddresses = customerService.getShippingAddresses(customerId);
		// 获取是否有默认地址
		Address defaultShippingAddress = customerService.getDefaultShippingAddress(customerId);
		Integer showDefaultId = 0;
		if (defaultShippingAddress != null) {
			showDefaultId = defaultShippingAddress.getId();
		}
		//当设置区域信息成功时，修改下单流程中货运地址的默认值
		if(shippingAddresses == null || shippingAddresses.size() <=0)
		{
			if(defaultShippingAddress == null)
			{
				defaultShippingAddress = new Address();
				Country country = new Country();
				CookieArea cookieArea =SessionUtils.getAreaInfo(request.getSession());
				if(cookieArea != null && cookieArea.getCountry() != null)
				{
					country.setId(cookieArea.getCountry().getId());
					defaultShippingAddress.setCountry(country);
					defaultShippingAddress.setCity(cookieArea.getCity());
					defaultShippingAddress.setZip(cookieArea.getZip());
				}
			}
		}
	
		// 获取所有国家
		List<Country> countries = dictionaryService.getAllCountries();
		List<Country> commonCountries = dictionaryService.getAllCommonCountries();
		
		beginPage(PlaceOrderPage.PAGE_SHIPPING_ADDRESS);

		request.setAttribute("countries", countries);
		request.setAttribute("commonCountries", commonCountries);
		request.setAttribute("defaultShippingAddress", defaultShippingAddress);
		request.setAttribute("shippingAddresses", shippingAddresses);
		request.setAttribute("showDefaultId", showDefaultId);
		request.setAttribute("sessionOrder", sessionOrder);
		request.setAttribute("currentPage", PlaceOrderPage.PAGE_SHIPPING_ADDRESS);
		return SUCCESS;		
	}
	
	
	private String postAddresses()
	{
		Integer customerId = this.getLoginedUserBuyer().getId();
		
		SessionOrder sessionOrder = getSessionOrder();		
		String checkResult = checkSessionOrder(PlaceOrderPage.PAGE_SHIPPING_ADDRESS);
		if( checkResult != null)
		{
			return checkResult;
		}
		
		//获取用户所有的货运地址
		List<Address> shippingAddresses = customerService.getShippingAddresses(customerId);
		// 获取是否有默认地址
		Address defaultShippingAddress = customerService.getDefaultShippingAddress(customerId);
		Integer showDefaultId = 0;
		if (defaultShippingAddress != null) 
		{
			showDefaultId = defaultShippingAddress.getId();
		}
		else
		{
			defaultShippingAddress = new Address();
		}
	
		// 获取所有国家
		List<Country> countries = dictionaryService.getAllCountries();
		List<Country> commonCountries = dictionaryService.getAllCommonCountries();
		
		beginPage(PlaceOrderPage.PAGE_SHIPPING_ADDRESS);

		//zw
		showDefaultId = paramInt("rdoShippingAddress",0);		
		
		defaultShippingAddress.setFirstName(param("s_FirstName"));
		defaultShippingAddress.setLastName(param("s_LastName"));
		defaultShippingAddress.setPhone(param("s_PhoneNumber"));
		defaultShippingAddress.setFax(param("s_Fax"));
		defaultShippingAddress.setStreet1(param("s_Street1"));
		defaultShippingAddress.setStreet2(param("s_Street2"));
		defaultShippingAddress.setCity(param("s_City"));
		defaultShippingAddress.setState(param("s_State"));
		Country country = dictionaryService.getCountry(paramInt("s_Country",0));
		defaultShippingAddress.setCountry(country);
		defaultShippingAddress.setZip(param("s_Zip"));
		
		request.setAttribute("countries", countries);
		request.setAttribute("commonCountries", commonCountries);
		request.setAttribute("defaultShippingAddress", defaultShippingAddress);
		request.setAttribute("shippingAddresses", shippingAddresses);
		request.setAttribute("showDefaultId", showDefaultId);
		request.setAttribute("sessionOrder", sessionOrder);
		request.setAttribute("currentPage", PlaceOrderPage.PAGE_SHIPPING_ADDRESS);
		return SUCCESS;
	}
	
	
	/**
	 * 保存货运地址
	 * @throws IOException
	 */
	public void saveAddress() throws IOException 
	{
		if( !isValidSessionByJson()) return;
		
		String fm = "[{\"result\":%1$s}]";
		int result = 0;
		
		try 
		{
			Integer customerId = this.getLoginedUserBuyer().getId();
			
			Integer shippingId=paramInt("s_AddressId",0);
			
			// 获取是否有默认地址
			Address defaultShippingAddress = customerService.getDefaultShippingAddress(customerId);
			
			Address shippingAddress = null;
			if( shippingId != null && shippingId > 0)
			{   
				if( defaultShippingAddress == null || defaultShippingAddress.getId().compareTo(shippingId) != 0 )
				{
					//如果有货运地址ID，则设置此货运地址为默认货运地址
					customerService.setDefaultShippingAddress(customerId, shippingId);
					//获取当前设置的货运地址信息
					shippingAddress = customerService.getShippingAddress(customerId, shippingId);
				}
				else 
				{
					shippingAddress = defaultShippingAddress;
				}
			}
			else 
			{
			    //如果没有货运地址ID，则新增一个货运地址
				shippingAddress = addDefaultShippingAddress(customerId);			
			}
		
			if( shippingAddress != null)
			{
				result = 1;			
				setShippingAddress(shippingAddress);
				computeFregithByShippingAddress();
				
				endPage(PlaceOrderPage.PAGE_SHIPPING_ADDRESS);
			}
		} catch (Exception e) {
			// TODO: handle exception
			errorLog(e);
		}
		
		
		returnJson(String.format(fm, result));
	}
	
	/**
	 * 删除货运地址
	 */
	public void deleteShippingAddress() throws IOException{
		if( !isValidSessionByJson()) return;
		
		String fm = "[{\"result\":%1$s}]";
		int result = 0;
		
		try {
			Integer addressId = paramInt("addressId", 0);
			Integer customerId = this.getLoginedUserBuyer().getId();
			if (addressId > 0 ) 
			{
				// 删除地址
				customerService.deleteShippingAddress(customerId, addressId);
				result=1;
			}
		} catch (Exception e) {
			// TODO: handle exception
			errorLog(e);
		}
		
		
		returnJson(String.format(fm, result));
	}
	
	private Address addDefaultShippingAddress(Integer customerId)
	{
		String firstName = param("s_FirstName");
		String lastName = param("s_LastName");
		String phone = param("s_PhoneNumber");
		String fax = param("s_FaxNumber");
		String street1 = param("s_Street1");
		String street2 = param("s_Street2");
		String city = param("s_City");
		String state = param("s_State");
		Integer countryId = paramInt("s_Country", 0);
		String zip = param("s_Zip");
		
		//校验输入信息
		if (!validataAddress(firstName,lastName,phone,street1,city,state,zip)) return null;
		
	    //获取国家信息
		Country country = dictionaryService.getCountry(countryId);			
		if (country == null) return null;//国家无效
		
		//构造地址对象
		Address shippingAddress = buildAddress(firstName, lastName, phone, fax, street1, street2, city, state, country, zip);
		
		city = city.replaceFirst("\\s+", " ");
		//保存货运地址信息
		Integer shippingAddressId = customerService.addShippingAddress(customerId, shippingAddress);
		if( shippingAddressId == null) return null;
		
		//设置为默认货运地址
		customerService.setDefaultShippingAddress(customerId, shippingAddressId);
		
		return shippingAddress;
	}
}

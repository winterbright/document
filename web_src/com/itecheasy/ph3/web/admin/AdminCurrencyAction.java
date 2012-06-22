package com.itecheasy.ph3.web.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.itecheasy.common.PageList;
import com.itecheasy.ph3.system.Currency;
import com.itecheasy.ph3.system.SystemService;
import com.itecheasy.ph3.web.AdminBaseAction;
import com.itecheasy.ph3.web.utils.StrUtils;

public class AdminCurrencyAction extends AdminBaseAction {
	private static final long serialVersionUID = 99010121888L;
	private SystemService systemService;
	private static final int PAGE_SIZE = 20;
	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	public String doAllCurrencies()
	{
		String password=param("password");
		PageList<Currency> currencies=null;
		List<Currency> list=null;
		boolean matchPassword=false;
		if (password!=null && password!="") {
			matchPassword=systemService.checkCurrencyPassword(password);
			if(matchPassword) {
				currencies=systemService.getAllCurrenciesByPageList(currentPage,PAGE_SIZE);
				if (currencies!=null) {
					list=currencies.getData();
					this.pageList=currencies;
				}
			}else {
				this.setMessageInfo("ERROR_PASSWORD_ERROR");
			}
		}else {
			this.setMessageInfo("ERROR_PASSWORD_NULL");
		}
		
		request.setAttribute("password", password);
		request.setAttribute("currencies", list);
		return SUCCESS;
	}
	
	public String viewCurrencys()
	{
		List<Currency> currencies= systemService.getAllCurrencies();
		
		request.setAttribute("currencies", currencies);
		return SUCCESS;
	}
	
	public void saveAllCurrencies()
	{
		String password=param("psd");
		String rates=param("rates");
		String rateIds=param("rateIds");
		String fm = "[{\"result\":%1$s}]";
		int result = 0;
		Integer rateId = null;
		BigDecimal rate=null;
		Integer userId = getLoginUserAdmin().getId();
		if ( rateIds != null && !rateIds.isEmpty()) {
			String[] rateIdsList = rateIds.split("\\|");
			
			if (rateIdsList.length == 1) {
				rateId = StrUtils.tryParseInt(rateIdsList[0], 0);
				if (rateId > 0) {
					if (rates!= null && !rates.isEmpty()) {
						rate=StrUtils.tryParseBigDecimal(rates, null);
						try {
							systemService.updateCurrencyRate(password,rateId,rate,userId);
							result=1;
						} catch (Exception e) {
						}
					}
				}
			} else {
				if (rates!= null && !rates.isEmpty()) {
					String[] ratesList = rates.split("\\|");
					for (int i = 0; i < rateIdsList.length; i++) {
						rateId = StrUtils.tryParseInt(rateIdsList[i], 0);
						rate=StrUtils.tryParseBigDecimal(ratesList[i], null);
						if (rateId > 0 && rate!=null) 
						{try {
							systemService.updateCurrencyRate(password,rateId,rate,userId);
							result=1;
						} catch (Exception e) {
						}
						}
					}
				}
			}
		}
		try {
			returnJson(String.format(fm, result));
		} catch (IOException e) {
		}
	}
}

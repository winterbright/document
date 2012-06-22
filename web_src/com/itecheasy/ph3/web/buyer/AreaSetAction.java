package com.itecheasy.ph3.web.buyer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.itecheasy.common.PageList;
import com.itecheasy.ph3.order.OnlinePayInfo;
import com.itecheasy.ph3.order.OrderPaymentInfo;
import com.itecheasy.ph3.system.CookieConfig;
import com.itecheasy.ph3.system.Country;
import com.itecheasy.ph3.system.Currency;
import com.itecheasy.ph3.system.DeliveryFreightRegion;
import com.itecheasy.ph3.system.DeliveryFreightRegions;
import com.itecheasy.ph3.system.DeliveryRemoteInfo;
import com.itecheasy.ph3.system.DictionaryService;
import com.itecheasy.ph3.system.SystemService;
import com.itecheasy.ph3.system.CookieConfigService.CookieType;
import com.itecheasy.ph3.web.BuyerBaseAction;
import com.itecheasy.ph3.web.buyer.order.OrderSessionUtils;
import com.itecheasy.ph3.web.buyer.order.SessionOrder;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.utils.StrUtils;
import com.itecheasy.ph3.web.utils.UrlHelper;
import com.itecheasy.ph3.web.vo.CookieArea;
import com.itecheasy.ph3.web.vo.DeliveryVO;
import com.itecheasy.ph3.web.vo.MinShoppingCartTotalInfo;

public class AreaSetAction extends BuyerBaseAction {
	private static final long serialVersionUID = 6414214900568883751L;
	private DictionaryService dictionaryService;
	private SystemService systemService;

	/**
	 * 进入区域设置。
	 */
	public String doAreaSet() {
		String tabId = param("tabId",null);
		List<Currency> currencys = systemService.getAllCurrencies();
		List<Currency> curs = new ArrayList<Currency>();
		if (currencys != null) {
			for (int i = 0; i < currencys.size(); i++) {
				if (!currencys.get(i).getCode().equals("CNY"))
					curs.add(currencys.get(i));
			}
		}
		List<Country> countries = dictionaryService.getAllCountries();
		List<Country> commonCountries = dictionaryService.getAllCommonCountries();
		CookieArea cookieArea = SessionUtils.getAreaInfo(request.getSession());
		//查询是否有运费信息。
		PageList<DeliveryFreightRegions> pageList =shippingService.searchFreightRegions(1, 5, cookieArea.getCountry(),null,SessionUtils.getDeliveryRemoteInfos(getSession()));
		if(pageList != null && pageList.getData() != null && pageList.getData().size() >0){
			request.setAttribute("errorTip", 0);
		}else{//提示国家没有运费信息。
			request.setAttribute("errorTip", 1);
		}
		request.setAttribute("areaSetCurrentCountryId", cookieArea.getCountry().getId());
		request.setAttribute("areaSetCurrencys", curs);
		request.setAttribute("areaSetCountries", countries);
		request.setAttribute("areaSetCommonCountries", commonCountries);
		request.setAttribute("tabId", tabId);
		return "success";
	}

	/**
	 * 保存区域设置信息。
	 */
	public void areaSet() {
		Integer countryId = paramInt("countryId", 0);
		Integer currencyId = paramInt("currencyId", 0);
		String zip = StrUtils.replaceBlank(param("zip", ""));
		String city = StrUtils.replaceBlank(param("city",""));

		// 根据存的国家id国家。
		Country country = dictionaryService.getCountry(countryId);
		// 根据存的币种ID查币种。
		Currency currency = systemService.getCurrency(currencyId);

		//设置区域信息
		setAreaInfo(currency,country,city,zip);

		try 
		{
			returnJson("{\"result\":1}");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	
	/**
	 * 区域设置联运币种，选择。
	 */
	public void doSlectDefaultCurrency() {
		String fm = "[{\"result\":%1$s,\"currencyId\":%2$s}]";
		int HasReightFlag = 0;
		Integer countryId = paramInt("countryId", 0);
		Currency currency = systemService.getDefaultCurrencyByCountry(countryId);
		Country country = dictionaryService.getCountry(countryId);
		PageList<DeliveryFreightRegions> pageList = shippingService.searchFreightRegions(1, 3, country, null, SessionUtils.getDeliveryRemoteInfos(getSession()));
		if (pageList != null && pageList.getData() != null && pageList.getData().size() > 0) {
			HasReightFlag = 1;
		}
		try {
			if (currency != null) {
				returnJson(String.format(fm, HasReightFlag, currency.getId()));
			} else {
				returnJson(String.format(fm, HasReightFlag, 0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}
	public String doPayPalCheckOut(){
		return SUCCESS;
	}
}

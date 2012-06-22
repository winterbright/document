package com.itecheasy.ph3.web.buyer;

import java.util.List;

import com.itecheasy.common.PageList;
import com.itecheasy.ph3.system.Country;
import com.itecheasy.ph3.system.DeliveryFreightRegions;
import com.itecheasy.ph3.system.DictionaryService;
import com.itecheasy.ph3.web.BuyerBaseAction;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.vo.CookieArea;

public class DeliveryAction extends BuyerBaseAction{

	private static final long serialVersionUID = -461492939389418745L;

	private DictionaryService dictionaryService;

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}
	
	public String doDeliveryList(){
		Integer regionId = paramInt("regionId",0);
		Integer isSelectRequest = paramInt("isSelectRequest",0);
		Country country = null;
		if(isSelectRequest==0 || regionId == null || regionId==0){
			CookieArea cookieArea = SessionUtils.getAreaInfo(request.getSession());			
			country = cookieArea.getCountry();
		}else{
			country = dictionaryService.getCountry(regionId);
		}
		PageList<DeliveryFreightRegions> pageList = shippingService.searchFreightRegions(1, 500, country, null, SessionUtils.getDeliveryRemoteInfos(getSession()));
		List<Country> countries = dictionaryService.getAllCountries();
		List<Country> commonCountries = dictionaryService.getAllCommonCountries();
		request.setAttribute("DeliveryFreightRegions", pageList.getData());
		request.setAttribute("countries", countries);
		request.setAttribute("commonCountries", commonCountries);
		request.setAttribute("areaSetCurrentCountryId", country.getId());
		return SUCCESS;
	}
}

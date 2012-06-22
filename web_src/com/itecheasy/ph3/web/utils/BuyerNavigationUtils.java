package com.itecheasy.ph3.web.utils;


public class BuyerNavigationUtils {
	
	public static Boolean isSelected(String url,String urls){
		if(urls == null || urls.length() ==0)
			return false;
		if(url==null || url.length()==0)
			return false;
		for(String currentUrl : urls.split("\\|")){
			if(url.indexOf(currentUrl)>0)
			return true;
		}
		return false;
	}	
	
}

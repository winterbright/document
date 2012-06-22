package com.itecheasy.ph3.web.admin;

import org.apache.commons.lang.StringUtils;

import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.system.SystemService;
import com.itecheasy.ph3.web.AdminBaseAction;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.utils.UrlHelper;

public class AdminIpAction  extends AdminBaseAction{
	
	private static final long serialVersionUID = 7903242974978475249L;
	private SystemService  systemService;
	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}

	public String doList(){
		if(currentPage==null)
			currentPage =1;
		request.setAttribute("localIP", UrlHelper.getIpAddr(request)) ;
		pageList = systemService.searchIpAddress(currentPage, PAGE_SIZE);
		return SUCCESS;
	}
	
	public String addIpAddress(){
		try {
			String ip = param("ip");
			if(StringUtils.isEmpty(ip)){
				ip = UrlHelper.getIpAddr(request);
			}
			systemService.addIpAddress(ip, SessionUtils.getLoginedAdminUser().getId());
		} catch (BussinessException e) {
			setMessageInfo(e.getErrorMessage());
		}
		return SUCCESS;
	}
	
	
	public String deleteIpAddress(){
		Integer delId =  paramInt("id",0);
		if(delId!=0){
			systemService.deleteIpAddress(delId);
		}else{
			for (Integer id : paramInts("ids")) {
				systemService.deleteIpAddress(id);
			}
		}
		return SUCCESS;
	}
	
	public String deleteThreeDaysBefore(){
		systemService.del3DaysBeforeOfIpAddress();
		return SUCCESS;
	}
	
	
	
}

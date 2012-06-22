package com.itecheasy.ph3.web.admin;

import java.io.IOException;

import com.itecheasy.ph3.common.MemCachedUtils;
import com.itecheasy.ph3.web.AdminBaseAction;

public class AdminIndexAction extends AdminBaseAction {

	private static final long serialVersionUID = 1L;


	public String doIndex(){
		return SUCCESS;
	}
	
	
	public String doRight() {
		return SUCCESS;
	}
	
	/**
	 * 刷新MemCache缓存
	 */
	public void flushMemCache() throws IOException{
		if(MemCachedUtils.getInstance().flushAll()){
			returnHtml(AJAX_RESPONSE_STATES_SUCCESS);
		}else{
			returnHtml(AJAX_RESPONSE_STATES_ERROR);
		}
	}
}

package com.itecheasy.ph3.web.buyer;

import java.util.List;

import com.itecheasy.common.PageList;

import com.itecheasy.ph3.web.BuyerBaseAction;

public class BuyerExtendAction extends BuyerBaseAction {
	private static final long serialVersionUID = 660101218L;

	/**
	 * 前台通用静态页面
	 */
	public String doStaticPage() {
		return param("PageSign");
	}
}

package com.itecheasy.ph3.web.utils;

import com.itecheasy.ph3.web.WebConfig;

public class ConfigHelper {

	public static final int DELIVERY_MAIL_POST_ID = Integer.parseInt(WebConfig.getInstance().get("delivery.mail_post_id"));
	public static final int DELIVERY_DHL_ID = Integer.parseInt(WebConfig.getInstance().get("delivery.dhl_id"));
	public static final int DELIVERY_UPS_ID = Integer.parseInt(WebConfig.getInstance().get("delivery.ups_id"));
	public static final int DELIVERY_FEDEX_ID = Integer.parseInt(WebConfig.getInstance().get("delivery.fedex_id"));
	public static final int DELIVERY_EMS_ID = Integer.parseInt(WebConfig.getInstance().get("delivery.ems_id"));
	public static final int DELIVERY_SURFACE_ID = Integer.parseInt(WebConfig.getInstance().get("delivery.surface_id"));
	public static final int PROPERTY_SHAPE_ID = Integer.parseInt(WebConfig.getInstance().get("property.shape.id"));
	public static final int PROPERTY_USAGE_ID = Integer.parseInt(WebConfig.getInstance().get("property.usage.id"));
	public static final int PROPERTY_COLOR_ID = Integer.parseInt(WebConfig.getInstance().get("property.color.id"));
	public static final int CURRENCY_RMB_ID = Integer.parseInt(WebConfig.getInstance().get("currency.rmb.id"));
	
	public static final int NEW_PRODUCTS_BEGIN_JOIN_DATE = Integer.parseInt(WebConfig.getInstance().get("new_products_begin_join_date"));// 前30天内的商品为新产品
	
	public static final String NAVIGATION_NAME_NEW_ARRIVAL = WebConfig.getInstance().get("navigation.newArrival.name");
	public static final String NAVIGATION_NAME_MIX = WebConfig.getInstance().get("navigation.mix.name");
	
	public static final String PAYPAL_WEB_URL = WebConfig.getInstance().get("paypal.web.url");
	public static final String PAYPAL_RECEIVER_EMAIL = WebConfig.getInstance().get("paypal.receiver.email");
	public static final String PAYPAL_VERIFY_EMAIL = WebConfig.getInstance().get("paypal.verify.email");
	public static final String PAYPAL_RETURN_SITE = WebConfig.getInstance().get("paypal.return.site").endsWith("/") ? WebConfig.getInstance().get("paypal.return.site") : WebConfig.getInstance().get("paypal.return.site") + "/";
	public static final String ANALYSIS_BARCODE_URL = WebConfig.getInstance().get("analysis_bar_code_url");
	
	public static Integer getConfigInt(String key){
		String value = WebConfig.getInstance().get(key);
		if ( value == null) return null;
		
		return Integer.parseInt(value);
	}
	
	public static String getConfigString(String key){
		return WebConfig.getInstance().get(key);
	}
	
}

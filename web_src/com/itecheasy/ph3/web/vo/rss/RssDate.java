package com.itecheasy.ph3.web.vo.rss;

import java.util.Date;

public class RssDate {
	//private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
	//private static final DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z",Locale.ENGLISH);

	@SuppressWarnings("deprecation")
	public static String getDateFormate(Date date) {
		String dateStr = null;
		if (date != null) {
			dateStr = date.toGMTString();
		}
		return dateStr;
	}
	
	public static void main(String[] ars){
		System.out.println(getDateFormate(new Date()));
	}
}

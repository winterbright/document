package com.itecheasy.ph3.web.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证帮助类
 * 
 */
public class ValidateUtils {

	public static boolean isEmail(String email) {

		Pattern p = Pattern
				.compile("^([a-zA-Z0-9_\\.\\-\\+])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$");
		Matcher m = p.matcher(email);
		if (m.matches()) {
			return true;
		}
		return false;
	}
}

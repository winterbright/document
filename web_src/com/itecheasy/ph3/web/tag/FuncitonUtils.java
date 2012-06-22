package com.itecheasy.ph3.web.tag;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.xwork.StringUtils;

import com.itecheasy.ph3.web.utils.ConfigHelper;
import com.itecheasy.ph3.web.utils.StrUtils;

public class FuncitonUtils {
	private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat(
			"MMM dd, yyyy", Locale.US);

	private static final SimpleDateFormat LONG_DATE_FORMAT = new SimpleDateFormat(
			"MMM dd, yyyy 'at' hh:mm a", Locale.US);

	private static final DecimalFormat PRICE_FORMAT = new DecimalFormat(
			"#,##0.00");
	private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat(
			"#,##0");
	private static final DecimalFormat WEIGHT_KG_FORMAT = new DecimalFormat(
			"##0.000");
	private static final DecimalFormat WEIGHT_G_FORMAT = new DecimalFormat(
			"##0");
	private static final DecimalFormat DISCOUNT_FORMAT = new DecimalFormat(
			"##0.##");

	/**
	 * 短日期格式
	 */
	public static String getShortDateString(Date date) {
		if (date == null)
			return StringUtils.EMPTY;
		return SHORT_DATE_FORMAT.format(date);
	}

	/**
	 * 长日期显示格式
	 */
	public static String getLongDateString(Date date) {
		if (date == null)
			return "";
		return LONG_DATE_FORMAT.format(date);
	}

	public static String getNow() {
		return getLongDateString(new Date());
	}


	
	/**
	 * 字符串限长
	 */
	public static String getLimitString(String str, int length) {
		if (StringUtils.isEmpty(str)) {
			return StringUtils.EMPTY;
		}
		
		return str.length() <= length ? str
				: (str.substring(0, length) + "...");
	}
	
	/**
	 * 字符串以字节的长度限长
	 */
	public static String getLimitStringByByte(String str, int limitLength) {
		if (StringUtils.isEmpty(str)) {
			return StringUtils.EMPTY;
		}
		
		int allLength = str.length();
		if( allLength <= limitLength)
		{
			return str;
		}
		
		String Tstr = "";
		int bytesCount = 0;//计算字节大小
		for(int i=0; i<str.length();i++)
		{
			Tstr = str.substring(i,i+1);
			
			bytesCount += Tstr.getBytes().length;
			
			if( bytesCount > limitLength)
			{
				return str.substring(0,i)  + "...";
			}
		}
		
		return str;
	}

	/**
	 * 去除HMTL
	 */
	public static String getLimitStringWithoutHtml(String str, int length) {
		if (StringUtils.isEmpty(str)) {
			return StringUtils.EMPTY;
		}
		str = StrUtils.filterHtml(str, " ").trim();
		str = StrUtils.replaceBlank(str);
		if (str.length() <= length) {
			return str;
		}
		String unStr = StringEscapeUtils.unescapeHtml(str);
		if (unStr.length() > length) {
			unStr = unStr.substring(0, length);
			return StringEscapeUtils.escapeHtml(unStr) + "...";
		} else {
			return str;
		}
	}

	/**
	 * 字符串限长,HTML转义
	 */
	public static String getLimitEscapeHtmlString(String str, int length) {
		if (StringUtils.isEmpty(str)) {
			return StringUtils.EMPTY;
		}
		return str.length() <= length ? StringEscapeUtils.escapeHtml(str)
				: StringEscapeUtils.escapeHtml(str.substring(0, length))
						+ "...";
	}

	/**
	 * 字符串限长,HTML转义,没有省略号
	 */
	public static String getLimitEscapeHtmlString2(String str, int length) {
		if (StringUtils.isEmpty(str)) {
			return StringUtils.EMPTY;
		}
		return str.length() <= length ? StringEscapeUtils.escapeHtml(str)
				: StringEscapeUtils.escapeHtml(str.substring(0, length));
	}

	/**
	 * 获取某个符前面部分,HTML转义
	 * 
	 * @param str
	 * @param ch
	 *            字符
	 */
	public static String getFrontOfChar(String str, String ch) {
		if (StringUtils.isEmpty(str)) {
			return StringUtils.EMPTY;
		}
		return StringEscapeUtils.escapeHtml(StrUtils.getFrontStringOfChar(str,
				ch, 1));
	}

	/**
	 * 获取某个符前面部分,HTML转义
	 * 
	 * @param str
	 * @param ch
	 *            字符
	 * @param index
	 *            第几个字符开始截取
	 */
	public static String getFrontStringOfChar(String str, String ch, int index) {
		if (StringUtils.isEmpty(str)) {
			return StringUtils.EMPTY;
		}
		return StringEscapeUtils.escapeHtml(StrUtils.getFrontStringOfChar(str,
				ch, index));
	}

	/**
	 * 获取货币显示格式
	 */
	public static String getPriceString(BigDecimal money) {
		if (money == null) {
			return "0.00";
		}
		return PRICE_FORMAT.format(money);
	}

	/**
	 * 获取数字显示格式
	 */
	public static String getNumberString(int qty) {
		return NUMBER_FORMAT.format(qty);
	}
	
	/**
	 * 获取数字显示格式
	 */
	public static String getIntString(int qty) {
		return String.valueOf(qty);
	}

	/**
	 * 获取重量显示格式
	 * 
	 * @param weight
	 *            重量，单位g
	 * @param convertToKg
	 *            是否转换成Kg
	 */
	public static String getWeightString(BigDecimal weight, boolean convertToKg) {
		if (convertToKg) {
			BigDecimal kg = weight.divide(new BigDecimal(1000));
			return WEIGHT_KG_FORMAT.format(kg.setScale(3, BigDecimal.ROUND_UP));
		} else {
			return WEIGHT_G_FORMAT.format(weight.setScale(0,
					BigDecimal.ROUND_UP));
		}
	}

	/**
	 * 增加天数
	 * 
	 * @param date
	 *            要设置的日期
	 * @param addCount
	 *            需增加的天数，如设为负数，则得到更前的日期
	 * @return 新的日期
	 */
	public static Date addDays(Date date, int addCount) {
		return addDate(date, Calendar.DATE, addCount);
	}

	/**
	 * 增加月份
	 * 
	 * @param date
	 *            要设置的日期
	 * @param addCount
	 *            需增加的月数，如设为负数，则得到更前的日期
	 * @return 新的日期
	 */
	public static Date addMonths(Date date, int addCount) {
		return addDate(date, Calendar.MONTH, addCount);
	}

	/**
	 * 增加年份
	 * 
	 * @param date
	 *            要设置的日期
	 * @param addCount
	 *            需增加的月数，如设为负数，则得到更前的日期
	 * @return 新的日期
	 */
	public static Date addYears(Date date, int addCount) {
		return addDate(date, Calendar.YEAR, addCount);
	}

	private static Date addDate(Date date, int field, int addCount) {
		if (date == null)
			return null;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		calendar.add(field, addCount);
		return calendar.getTime();
	}

	/**
	 * 获取配置文件
	 * 
	 * @param key
	 * @return
	 */
	public static Integer getConfigInt(String key) {
		return ConfigHelper.getConfigInt(key);
	}

	/**
	 * 获取配置文件
	 * 
	 * @param key
	 * @return
	 */
	public static String getConfigString(String key) {
		return ConfigHelper.getConfigString(key);
	}

	/**
	 * 折扣显示格式
	 * 
	 * @param discount
	 * @return
	 */
	public static String getDiscountString(BigDecimal discount) {
		return DISCOUNT_FORMAT.format(discount);
	}

	public static String getTextareaHtml(String text) {
		if (text == null || text.length() == 0) {
			return StringUtils.EMPTY;
		}
		return StringEscapeUtils.escapeHtml(text).replace("\n", "<br/>");
	}
}

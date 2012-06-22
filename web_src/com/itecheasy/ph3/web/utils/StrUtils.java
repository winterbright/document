package com.itecheasy.ph3.web.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class StrUtils {
	/**
	 * 是否有中文字符
	 * 
	 * @param s
	 * @return
	 */
	public static boolean hasCn(String s) {
		if (s == null) {
			return false;
		}
		return countCn(s) > s.length();
	}

	/**
	 * 计算GBK编码的字符串的字节数
	 * 
	 * @param s
	 * @return
	 */
	public static int countCn(String s) {
		if (s == null) {
			return 0;
		}
		int count = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.codePointAt(i) < 256) {
				count++;
			} else {
				count += 2;
			}
		}
		return count;
	}

	/**
	 * 替换字符串
	 * 
	 * @param sb
	 * @param what
	 * @param with
	 * @return
	 */
	public static StringBuilder replace(StringBuilder sb, String what,
			String with) {
		int pos = sb.indexOf(what);
		while (pos > -1) {
			sb.replace(pos, pos + what.length(), with);
			pos = sb.indexOf(what);
		}
		return sb;
	}

	/**
	 * 替换字符串
	 * 
	 * @param s
	 * @param what
	 * @param with
	 * @return
	 */
	public static String replace(String s, String what, String with) {
		return replace(new StringBuilder(s), what, with).toString();
	}

	/**
	 * 全角-->半角
	 * 
	 * @param qjStr
	 * @return
	 */
	public static String Q2B(String qjStr) {
		String outStr = "";
		String Tstr = "";
		byte[] b = null;
		for (int i = 0; i < qjStr.length(); i++) {
			try {
				Tstr = qjStr.substring(i, i + 1);
				b = Tstr.getBytes("unicode");
			} catch (java.io.UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (b[3] == -1) {
				b[2] = (byte) (b[2] + 32);
				b[3] = 0;
				try {
					outStr = outStr + new String(b, "unicode");
				} catch (java.io.UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else
				outStr = outStr + Tstr;
		}
		return outStr;
	}

	public static final char[] N62_CHARS = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
			'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
			'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
			'X', 'Y', 'Z' };
	public static final char[] N36_CHARS = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
			'x', 'y', 'z' };

	private static StringBuilder longToNBuf(long l, char[] chars) {
		int upgrade = chars.length;
		StringBuilder result = new StringBuilder();
		int last;
		while (l > 0) {
			last = (int) (l % upgrade);
			result.append(chars[last]);
			l /= upgrade;
		}
		return result;
	}

	public static String longToN36(long l, int length) {
		StringBuilder sb = longToNBuf(l, N36_CHARS);
		for (int i = sb.length(); i < length; i++) {
			sb.append('0');
		}
		return sb.reverse().toString();
	}

	/**
	 * N62转换成整数
	 * 
	 * @param n62
	 * @return
	 */
	public static long n62ToLong(String n62) {
		return nToLong(n62, N62_CHARS);
	}

	public static long n36ToLong(String n36) {
		return nToLong(n36, N36_CHARS);
	}

	private static long nToLong(String s, char[] chars) {
		char[] nc = s.toCharArray();
		long result = 0;
		long pow = 1;
		for (int i = nc.length - 1; i >= 0; i--, pow *= chars.length) {
			int n = findNIndex(nc[i], chars);
			result += n * pow;
		}
		return result;
	}

	private static int findNIndex(char c, char[] chars) {
		for (int i = 0; i < chars.length; i++) {
			if (c == chars[i]) {
				return i;
			}
		}
		throw new RuntimeException("N62(N36)非法字符：" + c);
	}

	/**
	 * 获取URL
	 * 
	 * @param parameters
	 *            Map of parameters to be dynamically included (if any)
	 * @param page
	 *            Module-relative page for which a URL should be created (if
	 *            specified)
	 * @param encodeSeparator
	 *            This is only checked if redirect is set to false (never
	 *            encoded for a redirect). If true, query string parameter
	 *            separators are encoded as &gt;amp;, else &amp; is used.
	 * @return url
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	public static String calculateURL(Map<String, Object> parameters,
			String page, boolean encodeSeparator) {
		String separator = StringUtils.EMPTY;
		String charEncoding = "UTF-8";
		if (encodeSeparator) {
			separator = "&amp;";
		} else {
			separator = "&";
		}
		StringBuffer url = new StringBuffer();
		if (StringUtils.isNotBlank(page)) {
			url.append(page);
		}
		String temp = url.toString();
		boolean question = temp.indexOf('?') >= 0;
		Iterator keys = parameters.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			Object value = parameters.get(key);
			if (value == null) {
				if (!question) {
					url.append('?');
					question = true;
				} else {
					url.append(separator);
				}
				url.append(encodeURL(key, charEncoding));
				url.append('=');
			} else if (value instanceof String) {
				if (!question) {
					url.append('?');
					question = true;
				} else {
					url.append(separator);
				}
				url.append(encodeURL(key, charEncoding));
				url.append('=');
				url.append(encodeURL((String) value, charEncoding));
			} else if (value instanceof String[]) {
				String values[] = (String[]) value;
				for (int i = 0; i < values.length; i++) {
					if (!question) {
						url.append('?');
						question = true;
					} else {
						url.append(separator);
					}
					url.append(encodeURL(key, charEncoding));
					url.append('=');
					url.append(encodeURL(values[i], charEncoding));
				}
			} else if (value instanceof Integer[]) {
				Integer values[] = (Integer[]) value;
				for (int i = 0; i < values.length; i++) {
					if (!question) {
						url.append('?');
						question = true;
					} else {
						url.append(separator);
					}
					url.append(key);
					url.append('=');
					if (values[i] != null) {
						url.append(values[i]);
					}
				}
			} else if (value instanceof int[]) {
				int values[] = (int[]) value;
				for (int i = 0; i < values.length; i++) {
					if (!question) {
						url.append('?');
						question = true;
					} else {
						url.append(separator);
					}
					url.append(key);
					url.append('=');
					if (values[i] != 0) {
						url.append(values[i]);
					}
				}
			} else {
				if (!question) {
					url.append('?');
					question = true;
				} else {
					url.append(separator);
				}
				url.append(encodeURL(key, charEncoding));
				url.append('=');
				url.append(encodeURL(value.toString(), charEncoding));
			}
		}
		return url.toString();
	}

	public static String encodeURL(String url, String enc) {
		if (StringUtils.isBlank(enc)) {
			enc = "UTF-8";
		}
		try {
			return URLEncoder.encode(url, enc);
		} catch (UnsupportedEncodingException e) {
			return url;
		}
	}

	public static String decodeURL(String url, String enc) {
		if (StringUtils.isBlank(enc)) {
			enc = "UTF-8";
		}
		try {
			return URLDecoder.decode(url, enc);
		} catch (UnsupportedEncodingException e) {
			return url;
		}
	}

	public static String replaceUrl(String url) {
		String reg = "([/]{1,2})|([\\-&*#%^<>{}()?',.:\\s\\u002A\\u005C\"]+)";
		Pattern pattern = Pattern.compile(reg);
		return pattern.matcher(url).replaceAll("-");
	}

	/**
	 * 中文标点转换英文标点
	 */
	public static String toC2E(String input) {
		String[] chs = new String[] { "、", "。", "，", "；", "：", "？", "！", "……",
				"“", "—", "～", "（", "）", "《", "》", "&hellip;&hellip;" };
		String[] ens = new String[] { ",", ".", ",", ";", ":", "?", "!", "…",
				"\\\"", "-", "~", "(", ")", "<", ">", "&hellip;" };
		for (int i = 0; i < chs.length; i++) {
			input = input.replace(chs[i], ens[i]);
		}
		return input;
	}

	/**
	 * 把多个空格替换成一个空格
	 */
	public static String replaceBlank(String input) {
		String reg = "\\s{2,}";
		Pattern pattern = Pattern.compile(reg);
		return pattern.matcher(input).replaceAll(" ");
	}

	/**
	 * 过滤所有以<开头,以>结尾的标签
	 */
	public static String filterHtml(String input, String replacement) {
		String reg = "<([^>]*)>";
		Pattern pattern = Pattern.compile(reg);
		return pattern.matcher(input).replaceAll(replacement);
	}

	/**
	 * 过滤所有HTML注释
	 */
	public static String filterHtmlNote(String input, String replacement) {
		String reg = "<!--.*-->";
		Pattern pattern = Pattern.compile(reg);
		return pattern.matcher(input.replace("\r", "").replace("\n", ""))
				.replaceAll(replacement);
	}

	/**
	 * 过滤所有HTML注释
	 */
	public static String filterHtmlNote(String input) {
		return filterHtmlNote(input, "");
	}

	/**
	 * 过滤脚本
	 */
	public static String filterHtmlScript(String input) {
		return filterHtmlScript(input, "");
	}

	/**
	 * 过滤脚本
	 */
	public static String filterHtmlScript(String input, String replacement) {
		String reg = "<script.*/script>";
		Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		return pattern.matcher(input).replaceAll(replacement);
	}

	/**
	 * 获取指定第几个字符之前的字符串
	 * 
	 * @param str
	 *            字符串
	 * @param ch
	 *            字符
	 * @param i
	 *            第几个
	 */
	public static String getFrontStringOfChar(String str, String ch, int i) {
		if (StringUtils.isEmpty(str)) {
			return StringUtils.EMPTY;
		}
		int index = getIndexOfChar(str, ch, i);
		if (index > -1) {
			return str.substring(0, index);
		} else {
			return str;
		}
	}

	public static int getIndexOfChar(String str, String ch, int i) {
		if (i < 1) {
			return -1;
		}
		int index = str.indexOf(ch);
		if (index < 0) {
			return -1;
		}
		int j = 1;
		while (j < i) {
			if (index < 0) {
				break;
			}
			index = str.indexOf(ch, index + 1);
			j++;
		}
		return index;
	}

	/**
	 * 尝试把字符串转为整数
	 * 
	 * @param value
	 * @param defaultValue
	 *            转换失败返回的值
	 * @return
	 */
	public static int tryParseInt(String value, int defaultValue) {
		if (value == null)
			return defaultValue;
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 尝试把字符串转为 BigDecimal
	 * 
	 * @param value
	 * @param defaultValue
	 *            转换失败返回的值
	 * @return
	 */
	public static BigDecimal tryParseBigDecimal(String value,
			BigDecimal defaultValue) {
		if (value == null)
			return defaultValue;
		try {
			return new BigDecimal(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 尝试把字符串转为 Date
	 * 
	 * @param value
	 * @param dateFormat
	 *            日期格式
	 * @param defaultValue
	 *            转换失败返回的值
	 * @return
	 */
	public static Date tryParseDate(String value, String dateFormat,
			Date defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		DateFormat df = new SimpleDateFormat(dateFormat);
		try {
			return df.parse(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static String format(String format, String... args) {
		if (args == null || args.length == 0) {
			return format;
		}
		for (int i = 0; i < args.length; i++) {
			format = format.replaceAll("\\{" + i + "\\}", args[i]);
		}
		return format;
	}
	
	public static boolean equals(String str1,String str2)
	{
		if( StringUtils.isEmpty(str1) && StringUtils.isEmpty(str2)) return true;
		
		return StringUtils.equals(str1,  str2);
	}
	
	public static boolean equalsIgnoreCase(String str1,String str2)
	{
		if( StringUtils.isEmpty(str1) && StringUtils.isEmpty(str2)) return true;
		
		return StringUtils.equalsIgnoreCase( str1,  str2);
	}
	
	public static String htmlConvert(String text){ 
        if (text==null) 
        return ""; 
        StringBuffer results = null; 
        char[] orig = null; 
        int beg = 0,len=text.length(); 
        for (int i=0;i<len;i++)
        { 
              char c = text.charAt(i); 
              switch(c){ 
                    case 0: 
                    case '&': 
                    case '<': 
                    case '>': 
                          if (results == null){ 
                                orig = text.toCharArray(); 
                                results = new StringBuffer(len+10); 
                          } 
                          if (i>beg) results.append(orig,beg,i-beg); 
                          beg = i + 1; 
                          switch (c){ 
                                default : continue; 
                                case '&': results.append("&amp;"); break; 
                                case '<': results.append("&lt;"); break; 
                                case '>': results.append("&gt;"); break;
                          } 
                    break; 
              } //switch 
        }// for i 
        if (results == null) 
              return text; 
        results.append(orig,beg,len-beg); 
        return results.toString(); 
      }// HTMLEncode

	/**
	 * 获得字符串对象的字符串，如果对象为null，则返回空字符串,如果对象不为null，则是本身
	 * @param text
	 * @return 字符串，如果对象为null，则返回空字符串
	 */
	public static String convertEmptyStringOfNull(String text){
		if( text == null) return "";
		return text;
	}
	
	
	
	
	public static void main(String[] ags) {

	}
	
}

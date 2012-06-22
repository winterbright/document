package com.itecheasy.ph3.web.components.struts2;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.json.annotations.JSON;

import com.itecheasy.ph3.web.utils.UrlHelper;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 提供一些参数处理的方法
 * 
 * @author wxj
 */
public class ParamActionSupport extends ActionSupport implements
		ServletRequestAware, ServletResponseAware {

	
	
	private static final long serialVersionUID = 7635888833999847585L;

	private final Logger logger = Logger.getLogger("PH3");
	public static final String DEFAULT_DATE_FORMAT = "MMM dd,yyyy";
	public static final DateFormat[] ACCEPT_DATE_FORMATS = {
			new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.US),
			new SimpleDateFormat("yyyy-MM-dd"),
			new SimpleDateFormat("yyyy/MM/dd") };

	/**
	 * HttpServletRequest object
	 */
	protected HttpServletRequest request = null;
	/**
	 * HttpServletResponse object
	 */
	protected HttpServletResponse response = null;

	
	protected void errorLog(Throwable e){
		String ip = request.getRemoteHost();
		String url = UrlHelper.getRawUrl(request);
		logger.error(" \r\n ip : " + ip + " url = " + url  + "  Exception:", e);
	}
	protected void errorLog(String s ,Throwable e){
		String ip = request.getRemoteHost();
		String url = UrlHelper.getRawUrl(request);
		logger.error(" \r\n ip : " + ip + " url = " + url  + " \r\n "+ s + "  Exception:", e);
	}
	/**
	 * 根据参数名称得到改参数
	 * 
	 * @param paramName
	 *            参数名称，不允许NULL
	 * @return 如果是NULL返回""，否则返回值
	 */
	protected String param(String paramName) {
		return param(paramName, StringUtils.EMPTY);
	}

	/**
	 * 根据参数的名称得到改参数，如果得到的是NULL,那么用defaultValue替换.
	 * 
	 * @param paramName
	 *            参数名称，不允许NULL
	 * @param defaultValue
	 *            默认值，可以是NULL
	 * @return "" 或者得到的值
	 */
	protected String param(String paramName, String defaultValue) {
		String paramValue = ConvertUtils.convert(getRequest().getParameter(
				paramName));
		if (StringUtils.isBlank(paramValue)) {
			return defaultValue;
		} else {
			return paramValue.trim();
		}
	}

	/**
	 * 根据参数名称得到改参数
	 * 
	 * @param paramName
	 *            参数名称，不允许NULL
	 * @return 如果是NULL返回""，否则返回转换后的SQL
	 */
	protected String paramSql(String paramName) {
		return param(paramName, StringUtils.EMPTY);
	}

	/**
	 * 根据参数的名称得到改参数，如果得到的是NULL,那么用defaultValue替换.
	 * 
	 * @param paramName
	 *            参数名称，不允许NULL
	 * @param defaultValue
	 *            默认值，可以是NULL
	 * @return "" 或者得到并且转换成sql参数的值
	 */
	protected String paramSql(String paramName, String defaultValue) {
		if (param(paramName, null) == null) {
			return defaultValue;
		} else {
			return StringEscapeUtils.escapeSql(param(paramName,
					StringUtils.EMPTY));
		}
	}

	protected Long paramLong(String paramName) {
		return paramLong(paramName, null);
	}

	protected Long paramLong(String paramName, Long defaultValue) {
		String temp = getRequest().getParameter(paramName);
		try {
			return new Long(temp);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	protected Integer paramInt(String paramName) {
		return paramInt(paramName, null);
	}

	protected Integer paramInt(String paramName, Integer defaultValue) {
		String temp = getRequest().getParameter(paramName);
		try {
			return new Integer(temp);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	protected boolean paramBool(String paramName) {
		String temp = getRequest().getParameter(paramName);
		if (StringUtils.isBlank(temp))
			return false;
		return Boolean.parseBoolean(temp);
	}

	protected boolean paramBool(String paramName, boolean defaultValue) {
		String temp = getRequest().getParameter(paramName);
		if (StringUtils.isBlank(temp))
			return defaultValue;
		return Boolean.parseBoolean(temp);
	}

	protected Integer[] paramInts(String paramName) {
		String[] values = getRequest().getParameterValues(paramName);
		if (ArrayUtils.isEmpty(values)) {
			return new Integer[] {};
		}
		List<Integer> valueList = new ArrayList<Integer>();
		for (String vlaue : values) {
			if (StringUtils.isNotBlank(vlaue.trim()))
				valueList.add(new Integer(vlaue));
		}
		return valueList.toArray(new Integer[valueList.size()]);
	}

	protected Integer[] paramInts(String paramName, String regex) {
		String[] idsStr = param(paramName).split(regex);
		List<Integer> list = new ArrayList<Integer>();
		Integer id = null;
		for (String idStr : idsStr) {
			try {
				id = new Integer(idStr);
				list.add(id);
			} catch (Exception e) {
			}
		}
		return list.toArray(new Integer[list.size()]);
	}

	protected Long[] paramLongs(String paramName) {
		String[] values = getRequest().getParameterValues(paramName);
		if (ArrayUtils.isEmpty(values)) {
			return new Long[] {};
		}
		List<Integer> valueList = new ArrayList<Integer>();
		for (String vlaue : values) {
			if (StringUtils.isNotBlank(vlaue.trim()))
				valueList.add(new Integer(vlaue));
		}
		return valueList.toArray(new Long[valueList.size()]);
	}
	
	protected BigDecimal paramBigDecimal(String paramName) {
		String temp = getRequest().getParameter(paramName);
		if (StringUtils.isBlank(temp))
			return BigDecimal.ZERO;
		return new BigDecimal(temp);
	}

	protected Date paramDate(String paramName, Date defaultValue) {
		for (DateFormat format : ACCEPT_DATE_FORMATS) {
			try {
				return format.parse(param(paramName));
			} catch (ParseException e) {
				continue;
			} catch (RuntimeException e) {
				continue;
			}
		}
		return defaultValue;
	}

	protected Date paramDate(String paramName) {
		return paramDate(paramName, null);
	}

	/**
	 * 得到Session，如果没有就新建立
	 * 
	 * @return session
	 */
	public HttpSession getSession() {
		return getRequest().getSession();
	}

	/**
	 * 保存数据到当前请求，如果改键已经存在，会被新的数据覆盖
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public void saveRequest(String key, Object value) {
		getRequest().setAttribute(key, value);
	}

	/**
	 * 根据键得到值
	 * 
	 * @param key
	 *            键
	 * @return
	 */
	public Object getSessionAttribute(String key) {
		return getSession().getAttribute(key);
	}

	/**
	 * 保存数据到当前SESSION，如果改键已经存在，会被新的数据覆盖
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public void saveSessionAttribute(String key, Object value) {
		getSession().setAttribute(key, value);
	}

	/**
	 * SESSION 无效
	 */
	public void invalidateSession() {
		getSession().invalidate();
	}

	public void setServletRequest(HttpServletRequest arg0) {
		request = arg0;
	}

	public void setServletResponse(HttpServletResponse arg0) {
		response = arg0;
		response.setCharacterEncoding("utf-8");
	}

	@JSON(serialize = false)
	public HttpServletRequest getRequest() {
		return request;
	}
}

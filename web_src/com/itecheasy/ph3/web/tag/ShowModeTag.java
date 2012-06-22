package com.itecheasy.ph3.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class ShowModeTag extends TagSupport {
	private static final long serialVersionUID = 724101218L;

	/**
	 * 排序格式化URL
	 */
	private String urlFormat;
	/**
	 * 排序格式化URL参数
	 */
	private String urlFormatPattern = "{showMode}";

	private Integer currentShowMode;

	@Override
	public int doStartTag() throws JspException {
		try {
			if (currentShowMode == null || currentShowMode < 1) {
				currentShowMode = 1;
			}
			pageContext.getOut().print(getHtml());
		} catch (IOException e) {
			throw new JspException();
		}
		return SKIP_BODY;
	}

	private String getHtml() {
		StringBuilder html = new StringBuilder();
		html.append("<p class=\"ViewType\"><span class=\"ViewGrid\">");
		html.append("<a" + getSelectedStatus(2) + " href=\""
				+ getShowModeUrl(2) + "\">Grid</a></span>");
		html.append("<span class=\"ViewList\">");
		html.append("<a" + getSelectedStatus(1) + " href=\""
				+ getShowModeUrl(1) + "\">List</a></span></p>");
		return html.toString();
	}

	private String getShowModeUrl(int index) {
		return urlFormat.replace(urlFormatPattern, String.valueOf(index));
	}

	private String getSelectedStatus(int sortIndex) {
		if (sortIndex == currentShowMode) {
			return " class=\"ChooseView\"";
		}
		return "";
	}

	public String getUrlFormat() {
		return urlFormat;
	}

	public void setUrlFormat(String urlFormat) {
		this.urlFormat = urlFormat;
	}

	public String getUrlFormatPattern() {
		return urlFormatPattern;
	}

	public void setUrlFormatPattern(String urlFormatPattern) {
		this.urlFormatPattern = urlFormatPattern;
	}

	public Integer getCurrentShowMode() {
		return currentShowMode;
	}

	public void setCurrentShowMode(Integer currentShowMode) {
		this.currentShowMode = currentShowMode;
	}

}

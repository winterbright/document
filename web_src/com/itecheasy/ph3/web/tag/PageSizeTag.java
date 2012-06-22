package com.itecheasy.ph3.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;

public class PageSizeTag extends TagSupport {
	private static final long serialVersionUID = 724101218L;

	private Integer currentPageSize;
	private String urlFormat;
	private String urlFormatPattern = "{pageSize}";
	private String pageSizeItems;
	private String[] pageSizeArray;
	
	@Override
	public int doStartTag() throws JspException {
		try {
			if( StringUtils.isEmpty(pageSizeItems))
			{
				pageSizeItems = "40,80,120";
			}
			pageSizeArray = pageSizeItems.split(",");
			if( currentPageSize == null || currentPageSize <= 0)
			{
				currentPageSize = Integer.parseInt(pageSizeArray[0]);
			}

			pageContext.getOut().print(getHtml());
		} catch (IOException e) {
			throw new JspException();
		}
		return SKIP_BODY;
	}

	private String getHtml() {
		StringBuilder html = new StringBuilder();
		html.append("<ul class=\"PerPage\">");
		html.append("<li>Per Page:</li>");
		int tempPageSize = 0;
		for (String size : pageSizeArray) {
			html.append("<li ");
			tempPageSize = Integer.parseInt(size);
			if( currentPageSize == tempPageSize)
			{
				html.append("class=\"PageHover\"");
			}
			
			html.append(">");
			html.append("<a href=\"" + getPageSizeUrl(tempPageSize) + "\">" + size + "</a></li>");
		}
		html.append("</ul>");
		return html.toString();
	}
	
	private String getPageSizeUrl(int pageSize) {
		return urlFormat.replace(urlFormatPattern, String.valueOf(pageSize));
	}

	public String getUrlFormat() {
		return urlFormat;
	}

	public void setUrlFormat(String urlFormat) {
		this.urlFormat = urlFormat;
	}

	public String getPageSizeItems() {	
		return pageSizeItems;
	}

	public void setPageSizeItems(String pageSizeItems) {
		this.pageSizeItems = pageSizeItems;
	}

	public Integer getCurrentPageSize() {
		return currentPageSize;
	}

	public void setCurrentPageSize(Integer currentPageSize) {
		this.currentPageSize = currentPageSize;
	}

	public String getUrlFormatPattern() {
		return urlFormatPattern;
	}

	public void setUrlFormatPattern(String urlFormatPattern) {
		this.urlFormatPattern = urlFormatPattern;
	}
}

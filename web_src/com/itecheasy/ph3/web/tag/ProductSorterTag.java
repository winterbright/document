package com.itecheasy.ph3.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 商品排序
 */
public class ProductSorterTag extends TagSupport {
	private static final long serialVersionUID = 624101218L;
	/**
	 * 标签ID
	 */
	private String tagId;

	private String cssClass;
	/**
	 * 排序格式化URL
	 */
	private String urlFormat;
	/**
	 * 排序格式化URL参数
	 */
	private String urlFormatPattern = "{sortIndex}";

	/**
	 * 当前排序
	 */
	private Integer currentSortIndex;

	@Override
	public int doStartTag() throws JspException {
		try {
			pageContext.getOut().print(getHtml());
		} catch (IOException e) {
			throw new JspException();
		}
		return SKIP_BODY;
	}

	private String getHtml() {
		StringBuilder html = new StringBuilder();
		String idString = tagId == null ? "" : " id=" + tagId;
		String cssString = cssClass == null ? "" : " class=" + cssClass;
		html.append("<select" + idString + cssString
				+ " onchange=\"window.location.href=this.value\">\n");
		html.append("<option value=\"" + getSortUrl(1) + "\""
				+ getSelectedStatus(1) + ">Best Match</option>\n");
		html.append("<option value=\"" + getSortUrl(2) + "\""
				+ getSelectedStatus(2) + ">Price Low To High</option>\n");
		html.append("<option value=\"" + getSortUrl(3) + "\""
				+ getSelectedStatus(3) + ">Price High To Low</option>\n");
		html.append("<option value=\"" + getSortUrl(4) + "\""
				+ getSelectedStatus(4) + ">Newest</option>\n");
		html.append("</select>\n");
		return html.toString();
	}

	private String getSortUrl(int sortIndex) {
		return urlFormat.replace(urlFormatPattern, String.valueOf(sortIndex));
	}

	private String getSelectedStatus(int sortIndex) {
		if (sortIndex == currentSortIndex) {
			return " selected=\"selected\"";
		}
		return "";
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
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

	public Integer getCurrentSortIndex() {
		return currentSortIndex;
	}

	public void setCurrentSortIndex(Integer currentSortIndex) {
		this.currentSortIndex = currentSortIndex;
	}

}

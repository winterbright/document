package com.itecheasy.ph3.web.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts2.views.util.UrlHelper;

import com.itecheasy.common.Page;

public class PagerTag extends TagSupport {
	private static final long serialVersionUID = 20181218L;

	private static final String PAGET_BUTTON_FORMAT = "<li><a%3$s href=\"%2$s\">%1$s</a></li>\n";;
	private static final String DISABLED_BUTTON_FORMAT = "<li%2$s>%1$s</li>\n";
	/**
	 * 标签ID
	 */
	private String tagId;

	private Page page;

	/**
	 * 页码按钮数
	 */
	private int pageButtonCount = 7;
	private int currentPageIndex = 1;
	/**
	 * 请求URL
	 */
	private String url;
	/**
	 * URL的页码参数名
	 */
	private String urlPageParameter = "currentPage";
	/**
	 * 是否使用分页格式化URL
	 */
	private Boolean usePagerFormatUrl = false;
	/**
	 * 分页格式化URL.页码参数{pageIndex}
	 */
	private String pagerFormatUrl;
	private String pagetFormatPattern = "{pageIndex}";

	private String nextButtonText = "Next &gt;";
	private String previousButtonText = "&lt; Previous";

	private String cssClass = "ListPage";
	private String currentPageCssClass = "PageHover";

	/**
	 * 是否总是显示,如果否，只有一页时不显示分页
	 */
	private Boolean isAlwayShow = false;

	@Override
	public int doStartTag() throws JspException {
		try {
			if (this.usePagerFormatUrl) {
				this.pagerFormatUrl = this.url;
			} else {
				this.pagerFormatUrl = createPagerFormatUrl();
			}
			// System.out.println(this.pagerFormatUrl);
			pageContext.getOut().print(getHtml());
		} catch (IOException e) {
			throw new JspException();
		}
		return SKIP_BODY;
	}

	private String getPageUrl(int pageIndex) {
		return this.pagerFormatUrl.replace(this.pagetFormatPattern, String
				.valueOf(pageIndex));
	}

	/**
	 * 生成分页格式化URL
	 */
	private String createPagerFormatUrl() {
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext
				.getResponse();
		Map<String, Object> newMap = new HashMap<String, Object>();
		newMap.putAll(request.getParameterMap());
		newMap.remove(urlPageParameter);
		newMap.put(this.urlPageParameter, "{pageIndex}");
		this.pagetFormatPattern = "%7BpageIndex%7D";
		if (this.url == null || this.url.trim().length() == 0) {
			return UrlHelper.buildUrl(null, request, response, newMap, request
					.getScheme(), true, true, true, true);
		} else {
			return UrlHelper.buildUrl(this.url, request, response, newMap,
					null, true, false, false, true);
		}
	}

	private String getHtml() {
		if (this.page == null) {
			return "";
		}
		int totalPageCount = this.page.getPageCount();
		// 如果只有一页，则不显示
		if (totalPageCount < 2 && !isAlwayShow) {
			return "";
		}
		if (currentPageIndex < 1) {
			currentPageIndex = 1;
		} else if (currentPageIndex > totalPageCount) {
			currentPageIndex = totalPageCount;
		}
		if (pageButtonCount < 1) {
			pageButtonCount = 1;
		}
		int startIndex;
		int endIndex;
		if (totalPageCount <= pageButtonCount) {
			startIndex = 1;
			endIndex = totalPageCount;
		} else {
			int middleButtonIndex = (pageButtonCount + 1) / 2;
			if (currentPageIndex > totalPageCount - middleButtonIndex) {
				endIndex = totalPageCount;
				startIndex = endIndex - pageButtonCount + 1;
			} else if (currentPageIndex < middleButtonIndex + 1) {
				startIndex = 1;
				endIndex = pageButtonCount;
			} else {
				endIndex = currentPageIndex + middleButtonIndex - 1;
				startIndex = endIndex - pageButtonCount + 1;
				if (startIndex < 1) {
					startIndex = 1;
				}
			}
		}

		String idString = "";
		String cssString = "";
		String currentPageCssString = "";
		// String prevPageCssString = " class=\"prevPage\"";
		// String nextPageCssString = " class=\"nextPage\"";
		if (this.tagId != null && this.tagId.length() > 0) {
			idString = " id=\"" + this.tagId + "\"";
		}
		if (this.cssClass != null && this.cssClass.length() > 0) {
			cssString = " class=\"" + this.cssClass + "\"";
		}
		if (this.currentPageCssClass != null
				&& this.currentPageCssClass.length() > 0) {
			currentPageCssString = " class=\"" + this.currentPageCssClass
					+ "\"";
		}

		StringBuffer htmlBuffer = new StringBuffer();
		htmlBuffer.append("<ul" + idString + cssString + ">\n");
		if (this.currentPageIndex > 1) {
			htmlBuffer.append(getPageButton(
					getPageUrl(this.currentPageIndex - 1), previousButtonText,
					""));
		}

		for (int i = startIndex; i <= endIndex; i++) {
			if (i == this.currentPageIndex) {
				htmlBuffer.append(getDisablePageButton(String.valueOf(i),
						currentPageCssString));
			} else {
				htmlBuffer.append(getPageButton(getPageUrl(i), String
						.valueOf(i), ""));
			}
		}

		if (this.currentPageIndex < totalPageCount) {
			htmlBuffer.append(getPageButton(
					getPageUrl(this.currentPageIndex + 1), nextButtonText, ""));
		}
		htmlBuffer.append("</ul>\n");
		return htmlBuffer.toString();
	}

	private static String getPageButton(String url, String text,
			String cssString) {
		return String.format(PAGET_BUTTON_FORMAT, text, url, cssString);
	}

	private static String getDisablePageButton(String text, String cssString) {
		return String.format(DISABLED_BUTTON_FORMAT, text, cssString);
	}

	public int getPageButtonCount() {
		return pageButtonCount;
	}

	public void setPageButtonCount(int pageButtonCount) {
		this.pageButtonCount = pageButtonCount;
	}

	public int getCurrentPageIndex() {
		return currentPageIndex;
	}

	public void setCurrentPageIndex(int currentPageIndex) {
		this.currentPageIndex = currentPageIndex;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public String getCurrentPageCssClass() {
		return currentPageCssClass;
	}

	public void setCurrentPageCssClass(String currentPageCssClass) {
		this.currentPageCssClass = currentPageCssClass;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getNextButtonText() {
		return nextButtonText;
	}

	public void setNextButtonText(String nextButtonText) {
		this.nextButtonText = nextButtonText;
	}

	public String getPreviousButtonText() {
		return previousButtonText;
	}

	public void setPreviousButtonText(String previousButtonText) {
		this.previousButtonText = previousButtonText;
	}

	public Boolean getIsAlwayShow() {
		return isAlwayShow;
	}

	public void setIsAlwayShow(Boolean isAlwayShow) {
		this.isAlwayShow = isAlwayShow;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public String getUrlPageParameter() {
		return urlPageParameter;
	}

	public void setUrlPageParameter(String urlPageParameter) {
		this.urlPageParameter = urlPageParameter;
	}

	public Boolean getUsePagerFormatUrl() {
		return usePagerFormatUrl;
	}

	public void setUsePagerFormatUrl(Boolean usePagerFormatUrl) {
		this.usePagerFormatUrl = usePagerFormatUrl;
	}

}

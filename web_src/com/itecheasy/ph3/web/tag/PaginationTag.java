package com.itecheasy.ph3.web.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.xwork.StringUtils;
import org.apache.struts2.views.util.UrlHelper;

import com.itecheasy.common.Page;

public class PaginationTag extends TagSupport {
	private static final long serialVersionUID = 620181218L;

	/** 分页对象* */
	private Page page;

	/** URL* */
	private String url;

	/** 显示大小* */
	private int showSize;

	/** 开始和结束的...* */
	private int startAndEndSkip;

	/** 语言 * */
	private String language;

	/** 需要删除的参数,多个已逗号（,）隔开* */
	private String removeParams;

	/** 是否使用参数 */
	private Boolean isUseParameter;

	/** 是否格式化URL */
	private Boolean isUsePagerFormatUrl;

	private static final String ZH_LANGUAGE = "zh";
	private static final String PAGE_SIZE = "pageSize";
	private static final String CURRENT_PAGE = "currentPage";
	private static final String PAGE_FORMAT_PATTERN = "{pageIndex}";

	public PaginationTag() {
		this.isUseParameter = true;
		this.isUsePagerFormatUrl = false;
	}

	public int doStartTag() throws JspException {
		if (getPage() == null) {
			return SKIP_BODY;
		}
		try {
			if (ZH_LANGUAGE.equalsIgnoreCase(getLanguage())) {
				pageContext.getOut().print(
						getZhCNPageHTML(getPage().getTotalRowCount(), getPage()
								.getCurrentPage(), getPage().getPageCount(),
								getShowSize()));
			} else {
				pageContext.getOut().print(
						getPageHTML(getPage().getTotalRowCount(), getPage()
								.getCurrentPage(), getPage().getPageCount(),
								getShowSize()));
			}

		} catch (IOException e) {
			throw new JspException();
		}
		return SKIP_BODY;
	}

	public String getZhCNPageHTML(int totalRowCount, int currentPage,
			int pageCount, int showSize) {
		if (pageCount <= 1)
			return "";

		StringBuffer sb = new StringBuffer();
		int startPos = (int) ((currentPage - Math.floor(showSize / 2)) > 1 ? (currentPage - Math
				.floor(showSize / 2))
				: 1);
		int endPos = (int) (startPos + showSize - 1) > pageCount ? pageCount
				: (startPos + showSize - 1);

		if (currentPage == pageCount) { // 最后一页显示 showSize-1页
			startPos = (currentPage - showSize) > 1 ? ((currentPage - showSize) + 1)
					: 1;
		}
		sb.append("<div class=\"listpage\">");
		sb.append("<span>每页显示" + page.getPageSize() + "条,共" + totalRowCount + "条</span>");
		sb.append("<ul>");
		sb.append("<li>第").append(currentPage).append("页/共").append(pageCount)
				.append("页</li>");
		sb.append(getZhCNFirstButton(1));
		if (currentPage > 1)
			sb.append(getZhCNPreviousButton(currentPage));

		if (startPos > (startAndEndSkip + 1)) {
			for (int i = 1; i <= startAndEndSkip; i++) {
				sb.append(getZhCNPageButton(i));
			}
			if (startAndEndSkip != 0) {
				sb.append("...");
			}
			for (int j = startPos; j <= endPos; j++) {
				if (j == currentPage) {
					sb.append(getZhCNCurrentButton(j));
				} else {
					sb.append(getZhCNPageButton(j));
				}
			}
		} else {
			for (int i = 1; i <= endPos; i++) {
				if (i == currentPage) {
					sb.append(getZhCNCurrentButton(i));
				} else {
					sb.append(getZhCNPageButton(i));
				}
			}
		}
		if (endPos + startAndEndSkip < pageCount) {
			if (startAndEndSkip != 0) {
				sb.append("...");
			}
			for (int i = pageCount - startAndEndSkip + 1; i <= pageCount; i++) {
				sb.append(getZhCNPageButton(i));
			}
		} else {
			for (int i = endPos + 1; i <= pageCount; i++) {
				sb.append(getZhCNPageButton(i));
			}
		}
		// Next 尾页...
		if (currentPage != pageCount)
			sb.append(getZhCNNextButton(currentPage));

		sb.append(getZhCNLastButton(pageCount));
		sb.append("</ul>");
		sb.append("</div>");
		return sb.toString();
	}

	/**
	 * 获取URL
	 */
	@SuppressWarnings("unchecked")
	private String getUrl(int currentPage) {
		String url = getUrl();
		if (isUsePagerFormatUrl) {
			url = url.replace(PAGE_FORMAT_PATTERN, String.valueOf(currentPage));
		}
		if (isUseParameter) {
			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			HttpServletResponse response = (HttpServletResponse) pageContext
					.getResponse();
			Map<String, Object> newMap = removeParams(
					request.getParameterMap(), currentPage);
			return UrlHelper.buildUrl(url, request, response, newMap, null,
					true, false, false, true);
		} else{
			return url;
		}
	}

	/**
	 * 移除不需要的参数
	 */
	private Map<String, Object> removeParams(Map<String, Object> params,
			int currentPage) {
		Map<String, Object> newMap = new HashMap<String, Object>();
		newMap.putAll(params);
		newMap.remove(PAGE_SIZE);
		newMap.remove(CURRENT_PAGE);
		newMap.put(PAGE_SIZE, getPage().getPageSize());
		newMap.put(CURRENT_PAGE, currentPage);
		if (getRemoveParams() != null && getRemoveParams().length() > 0)
			for (String param : getRemoveParams().split(","))
				newMap.remove(param);
		return newMap;
	}

	/**
	 * @param 总数
	 * @param 当前页
	 * @param 每页数
	 * @return HTML代码
	 */
	private String getPageHTML(int totalRowCount, int currentPage,
			int pageCount, int showSize) {
		if (pageCount <= 1)
			return "";

		StringBuffer sb = new StringBuffer();
		int startPos = (int) ((currentPage - Math.floor(showSize / 2)) > 1 ? (currentPage - Math
				.floor(showSize / 2))
				: 1);
		int endPos = (int) (startPos + showSize - 1) > pageCount ? pageCount
				: (startPos + showSize - 1);

		if (currentPage == pageCount) { // 最后一页显示 showSize-1页
			startPos = (currentPage - showSize) > 1 ? ((currentPage - showSize) + 1)
					: 1;
		}
		sb.append("<ul class=\"ListPage\">");
		// Previous ...
		if (currentPage > 1)
			sb.append(getPreviousButton(currentPage));

		if (startPos > (startAndEndSkip + 1)) {
			for (int i = 1; i <= startAndEndSkip; i++) {
				sb.append(getPageButton(i));
			}
			if (startAndEndSkip != 0) {
				sb.append("...");
			}
			for (int j = startPos; j <= endPos; j++) {
				if (j == currentPage) {
					sb.append(getCurrentButton(j));
				} else {
					sb.append(getPageButton(j));
				}
			}
		} else {
			for (int i = 1; i <= endPos; i++) {
				if (i == currentPage) {
					sb.append(getCurrentButton(i));
				} else {
					sb.append(getPageButton(i));
				}
			}
		}
		if (endPos + startAndEndSkip < pageCount) {
			if (startAndEndSkip != 0) {
				sb.append("...");
			}
			for (int i = pageCount - startAndEndSkip + 1; i <= pageCount; i++) {
				sb.append(getPageButton(i));
			}
		} else {
			for (int i = endPos + 1; i <= pageCount; i++) {
				sb.append(getPageButton(i));
			}
		}
		// Next 尾页...
		if (currentPage != pageCount)
			sb.append(getNextButton(currentPage));

		sb.append("</ul>");
		return sb.toString();
	}

	private String getPageButton(int currentPage) {
		return "<li><a href=\"" + getUrl(currentPage) + "\" >" + currentPage
				+ "</a></li>";
	}

	private String getCurrentButton(int currentPage) {
		return "<li class=\"PageHover\">" + currentPage + "</li>";
	}

	private String getPreviousButton(int currentPage) {
		return new StringBuffer("<li><a rel=\"nofollow\" href=\"").append(
				getUrl(currentPage - 1)).append("\" >&lt; Previous</a></li>")
				.toString();
	}

	private String getNextButton(int currentPage) {
		return new StringBuffer("<li><a rel=\"nofollow\" href=\"").append(
				getUrl(currentPage + 1)).append("\">Next &gt;</a></li>")
				.toString();
	}

	private String getZhCNPreviousButton(int currentPage) {
		return new StringBuffer("<li><a rel=\"nofollow\" href=\"").append(
				getUrl(currentPage - 1)).append("\" > 上一页 </a></li>")
				.toString();
	}

	private String getZhCNNextButton(int currentPage) {
		return new StringBuffer("<li><a rel=\"nofollow\" href=\"").append(
				getUrl(currentPage + 1)).append("\" > 下一页 </a></li>")
				.toString();
	}

	private String getZhCNPageButton(int currentPage) {
		return "<li><a href=\"" + getUrl(currentPage) + "\" >[" + currentPage
				+ "]</a></li>";
	}

	private String getZhCNCurrentButton(int currentPage) {
		return "<li class=\"PageHover\">[" + currentPage + "]</li>";
	}

	private String getZhCNLastButton(int currentPage) {
		return new StringBuffer("<li><a rel=\"nofollow\" href=\"").append(
				getUrl(currentPage)).append("\" >最后一页</a></li>").toString();
	}

	private String getZhCNFirstButton(int currentPage) {
		return new StringBuffer("<li><a rel=\"nofollow\" href=\"").append(
				getUrl(currentPage)).append("\" >第一页</a></li>").toString();
	}

	public Page getPage() {
		if (page == null)
			new Page(0, 1, 20);
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getShowSize() {
		return showSize;
	}

	public void setShowSize(int showSize) {
		this.showSize = showSize;
	}

	public int getStartAndEndSkip() {
		return startAndEndSkip;
	}

	public void setStartAndEndSkip(int startAndEndSkip) {
		this.startAndEndSkip = startAndEndSkip;
	}

	public String getLanguage() {
		if (StringUtils.isEmpty(language))
			language = PAGE_FORMAT_PATTERN;
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getRemoveParams() {
		return removeParams;
	}

	public void setRemoveParams(String removeParams) {
		this.removeParams = removeParams;
	}

	public Boolean getIsUseParameter() {
		return isUseParameter;
	}

	public void setIsUseParameter(Boolean isUseParameter) {
		this.isUseParameter = isUseParameter;
	}

	public Boolean getIsUsePagerFormatUrl() {
		return isUsePagerFormatUrl;
	}

	public void setIsUsePagerFormatUrl(Boolean isUsePagerFormatUrl) {
		this.isUsePagerFormatUrl = isUsePagerFormatUrl;
	}

	// String action, HttpServletRequest request, HttpServletResponse response,
	// Map params,
	// String scheme, boolean includeContext, boolean encodeResult, boolean
	// forceAddSchemeHostAndPort, boolean escapeAmp
}

package com.itecheasy.ph3.web.tag;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringEscapeUtils;

import com.itecheasy.ph3.web.Link;
import com.itecheasy.ph3.web.utils.UrlHelper;

public class NavigationTag extends TagSupport {
	private static final long serialVersionUID = 24101218L;

	private List<Link> links;
	private String cssClass = "NavPath FontBlue";
	/**
	 * 是否包含首页链接
	 */
	private Boolean containHomeLink = true;

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
		html.append("<div").append((cssClass == null ? "" : " class=\"" + cssClass + "\"")).append(">\n");
		if (containHomeLink) {
			html.append("<a href=\"").append(UrlHelper.getContextPath((HttpServletRequest) pageContext.getRequest())).append("\">Home</a>\n");
		}
		if (links != null && !links.isEmpty()) {
			StringBuilder linkBuilder = new StringBuilder();
			String text;
			for (Link link : links) {
				text = link.isEscapeHtml() ? StringEscapeUtils.escapeHtml(link.getText()) : link.getText();
				linkBuilder.append(" &gt; ");
				if (link.getHref() == null) {
					linkBuilder.append("<h2>").append(text).append("</h2>\n");
				} else {
					linkBuilder.append("<a href=\"").append(link.getHref()).append("\">").append(text).append("</a>\n");
				}
			}
			if (!containHomeLink) {
				linkBuilder.replace(0, 6, "");// remove " &gt; "
			}
			html.append(linkBuilder);
		}
		html.append("</div>\n");
		return html.toString();
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public Boolean getContainHomeLink() {
		return containHomeLink;
	}

	public void setContainHomeLink(Boolean containHomeLink) {
		this.containHomeLink = containHomeLink;
	}

}

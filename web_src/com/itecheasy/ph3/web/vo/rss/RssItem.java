package com.itecheasy.ph3.web.vo.rss;

import java.util.Date;

public class RssItem {
	private static final String START_ELEMENT = "<item>\n";
	private static final String END_ELEMENT = "</item>\n";
	private String author;// <author> 可选的。规定项目作者的电子邮件地址。
	private String category; // <category> 可选的。定义项目所属的一个或多个类别。
	private String comments;// <comments> 可选的。允许项目连接到有关此项目的注释（文件）。
	private String description;// <description> 必需的。描述此项目。
	private Enclosure enclosure;// <enclosure> 可选的。允许将一个媒体文件导入一个项中。
	private String guid;// <guid> 可选的。为项目定义一个唯一的标识符。
	private String link;// <link> 必需的。定义指向此项目的超链接。
	private Date pubDate;// <pubDate> 可选的。定义此项目的最后发布日期。
	private String source;// <source> 可选的。为此项目指定一个第三方来源。
	private String title;// <title> 必需的。定义此项目的标题。

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Enclosure getEnclosure() {
		return enclosure;
	}

	public void setEnclosure(Enclosure enclosure) {
		this.enclosure = enclosure;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String toRssXml() {
		StringBuffer sb = new StringBuffer();
		sb.append(START_ELEMENT);
		if (link != null) {
			sb.append("<link>" + link + "</link>\n");
		} else {
			return "";
		}
		if (title != null) {
			sb.append("<title>" + title + "</title>\n");
		} else {
			return "";
		}
		if (author != null) {
			sb.append("<author>" + author + " </author>\n");
		}
		if (category != null) {
			sb.append("<category>" + category + "</category>\n");
		}
		if (comments != null) {
			sb.append("<comments>" + comments + "</comments>\n");
		}
		if (description != null) {
			sb.append("<description><![CDATA[" + description + "]]></description>\n");
		}
		if (enclosure != null) {
			sb.append(enclosure.toRssXml());
		}
		if (guid != null) {
			sb.append("<guid>" + guid + "</guid>\n");
		}
		if (pubDate != null) {
			sb.append("<pubDate>" + RssDate.getDateFormate(pubDate) + "</pubDate>\n");
		}
		if (source != null) {
			sb.append("<source>" + source + "</source>\n");
		}
		sb.append(END_ELEMENT);
		if (sb.toString().equals(START_ELEMENT + END_ELEMENT)) {
			return "";
		}
		return sb.toString();
	}

	public RssItem() {
	}

	public RssItem(String category, String description, String guid, String link, Date pubDate, String title) {
		this.category = category;
		this.description = description;
		this.guid = guid;
		this.link = link;
		this.pubDate = pubDate;
		this.title = title;
	}

}

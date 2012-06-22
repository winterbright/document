package com.itecheasy.ph3.web.vo.rss;

import java.util.Date;
import java.util.List;

public class Rss {
	private String title;
	private String link;
	private String description;
	private String copyRight;
	private Image image;
	private Date pubDate;
	private List<RssItem> items;

	public Rss() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCopyRight() {
		return copyRight;
	}

	public void setCopyRight(String copyRight) {
		this.copyRight = copyRight;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	public List<RssItem> getItems() {
		return items;
	}

	public void setItems(List<RssItem> items) {
		this.items = items;
	}

	public Rss(String title, String link, String description, String copyRight, Image image, Date pubDate, List<RssItem> items) {
		super();
		this.title = title;
		this.link = link;
		this.description = description;
		this.copyRight = copyRight;
		this.image = image;
		this.pubDate = pubDate;
		this.items = items;
	}

	public String toRssXml() {
		return toRssXml("UTF-8", "2.0");
	}

	public String toRssXml(String charSet, String version) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"" + charSet + "\" ?>\n");
		sb.append("<rss version=\"" + version + "\">\n");
		sb.append("<channel>\n");
		if (title != null) {
			sb.append("<title>" + title + "</title>\n");
		}
		if (link != null) {
			sb.append("<link>" + link + "</link>\n");
		}
		if (description != null) {
			sb.append("<description>" + description + "</description>\n");
		}
		if (copyRight != null) {
			sb.append("<copyright>" + copyRight + "</copyright>\n");
		}
		if (image != null) {
			sb.append(image.toRssXml());
		}
		if (pubDate != null) {
			sb.append("<pubDate>" + RssDate.getDateFormate(pubDate) + "</pubDate>\n");
		}
		if (items != null) {// 得到所有item项xmlString
			for (int i = 0; i < items.size(); i++) {
				sb.append(items.get(i).toRssXml());
			}
		}
		sb.append("</channel>\n");
		sb.append("</rss>");
		return sb.toString();
	}

	public static void main(String[] args) {
		String ss = new Rss().toRssXml();
		System.out.println(ss);
	}

}

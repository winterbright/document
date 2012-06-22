package com.itecheasy.ph3.web.vo.productfeed;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProductFeed {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmm:ssZ");
	private String title;
	private String link;
	private Date updated;
	private String id;
	private Author author;
	private List<FeedEntity> feedItems;

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

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<FeedEntity> getFeedItems() {
		return feedItems;
	}

	public void setFeedItems(List<FeedEntity> feedItems) {
		this.feedItems = feedItems;
	}

	public ProductFeed() {
		super();
	}

	public ProductFeed(String title, String link, Date updated, String id, Author author, List<FeedEntity> feedItems) {
		super();
		this.title = title;
		this.link = link;
		this.updated = updated;
		this.id = id;
		this.author = author;
		this.feedItems = feedItems;
	}

	public StringBuffer toProductFeedXml(FileWriter fw) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		sb.append("<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:g=\"http://base.google.com/ns/1.0\">\r\n");
		if (title != null) {
			sb.append("<title>" + title + "</title>\r\n");
		}
		if (link != null) {
			sb.append("<link" + link + "/>\r\n");
		}
		if (updated != null) {
			String time = sdf.format(updated);
			time = time.substring(0,time.length()-2)+":"+time.substring(time.length()-2);
			sb.append("<updated>" + time + "</updated>\r\n");
		}
		if (author != null) {
			sb.append("<author>");
				sb.append(author.toFeedXml());
			sb.append("</author>\r\n");
			
		}
		if (id != null) {
			sb.append("<id>" + id + "</id>\r\n");
		}
		fw.write(sb.toString());
		fw.flush();
		if (feedItems != null) {
			for (int i = 0; i < feedItems.size(); i++) {
				try {
					fw.write(feedItems.get(i).toFeedEntityXml());
					fw.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		fw.write("</feed>\r\n");
		sb.append("</feed>\r\n");
		fw.flush();
		return sb;
	}
}

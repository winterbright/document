package com.itecheasy.ph3.web.vo.rss;

public class Enclosure {
	private Long length;
	private String type;
	private String url;

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Enclosure(Long length, String type, String url) {
		super();
		this.length = length;
		this.type = type;
		this.url = url;
	}

	public String toRssXml() {
		StringBuffer sb = new StringBuffer();
		sb.append("<enclosure ");
		if (url != null) {
			sb.append("url=\"" + url + "\" ");
		}
		if (length != null) {
			sb.append("length=\" + length + \" ");
		}
		if (type != null) {
			sb.append("type=\" + type + \" ");
		}
		sb.append(" />");
		if (sb.toString().equals("")) {
			return "";
		}
		return sb.toString();
	}

}

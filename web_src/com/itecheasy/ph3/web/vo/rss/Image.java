package com.itecheasy.ph3.web.vo.rss;

public class Image {
	private static final String START_ELEMENT = "<image>";
	private static final String END_ELEMENT = "</image>";
	private String title; // 必需,定义当图片不能显示时所显示的替代文本。
	private String description; // 可选。规定图片链接的 HTML 标题属性中的文本。
	private String link; // 必需,定义提供该频道的网站的超连接。
	private String url; // 必需,定义图像的 URL。
	private Integer height; // 可选,定义图像的高度。默认是 31。最大值是 400。
	private Integer width;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Image(String title, String link, String url) {
		super();
		this.title = title;
		this.link = link;
		this.url = url;
	}

	public Image() {
		super();
	}

	public Image(String title, String description, String link, String url) {
		super();
		this.title = title;
		this.description = description;
		this.link = link;
		this.url = url;
	}

	public Image(String title, String description, String link, String url, Integer height, Integer width) {
		super();
		this.title = title;
		this.description = description;
		this.link = link;
		this.url = url;
		this.height = height;
		this.width = width;
	}

	public String toRssXml() {
		StringBuffer sb = new StringBuffer();
		sb.append("<image>");
		if (title != null) {
			sb.append(" <title>" + title + "</title>");
		}
		if (url != null) {
			sb.append("<url>" + url + "</url>");
		}
		if (link != null) {
			sb.append("<link>" + link + "</link>");
		}
		if (description != null) {
			sb.append("<description>" + description + "</description>");
		}
		if (width != null) {
			sb.append("<width>" + width + "</width>");
		}
		if (height != null) {
			sb.append("<height>" + height + "</height>");
		}
		sb.append("</image>");
		if (sb.toString().equalsIgnoreCase(START_ELEMENT + END_ELEMENT)) {
			return "";
		}
		return sb.toString();
	}
}

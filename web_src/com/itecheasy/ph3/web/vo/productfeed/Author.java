package com.itecheasy.ph3.web.vo.productfeed;

public class Author {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Author(String name) {
		this.name = name;
	}

	public String toFeedXml() {
		String str = "";
		if (name != null && !"".equals(name)) {
			str = "<name>" + name + "</name>";
		}
		return str;
	}
}

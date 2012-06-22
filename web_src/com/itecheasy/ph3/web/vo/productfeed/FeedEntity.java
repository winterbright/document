package com.itecheasy.ph3.web.vo.productfeed;

public class FeedEntity {
	private static final String START_ELEMENT = "<entry>\r\n";
	private static final String END_ELEMENT = "</entry>\r\n";
	private String gid;
	private String title;
	private String link;
	private String gPrice;
	private String description;
	private String gCondition;
	private String gBrand;
	private String gMpn;
	private String gImageLink;
	private String gProductType;
	private String gQuantity;
	private String gAvailability;
	private String gOnlineOnly;
	private String gShippingWeight;
	private String gColor;

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
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

	public String getgPrice() {
		return gPrice;
	}

	public void setgPrice(String gPrice) {
		this.gPrice = gPrice;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getgCondition() {
		return gCondition;
	}

	public void setgCondition(String gCondition) {
		this.gCondition = gCondition;
	}

	public String getgBrand() {
		return gBrand;
	}

	public void setgBrand(String gBrand) {
		this.gBrand = gBrand;
	}

	public String getgMpn() {
		return gMpn;
	}

	public void setgMpn(String gMpn) {
		this.gMpn = gMpn;
	}

	public String getgImageLink() {
		return gImageLink;
	}

	public void setgImageLink(String gImageLink) {
		this.gImageLink = gImageLink;
	}

	public String getgProductType() {
		return gProductType;
	}

	public void setgProductType(String gProductType) {
		this.gProductType = gProductType;
	}

	public String getgQuantity() {
		return gQuantity;
	}

	public void setgQuantity(String gQuantity) {
		this.gQuantity = gQuantity;
	}

	public String getgAvailability() {
		return gAvailability;
	}

	public void setgAvailability(String gAvailability) {
		this.gAvailability = gAvailability;
	}

	public String getgOnlineOnly() {
		return gOnlineOnly;
	}

	public void setgOnlineOnly(String gOnlineOnly) {
		this.gOnlineOnly = gOnlineOnly;
	}

	public String getgShippingWeight() {
		return gShippingWeight;
	}

	public void setgShippingWeight(String gShippingWeight) {
		this.gShippingWeight = gShippingWeight;
	}

	public String getgColor() {
		return gColor;
	}

	public void setgColor(String gColor) {
		this.gColor = gColor;
	}

	public FeedEntity() {
	}

	public FeedEntity(
			String gid, 
			String title, 
			String link, 
			String gPrice,
			String description, 
			String gCondition, 
			String gBrand, 
			String gMpn, 
			String gImageLink, 
			String gProductType,
			String gQuantity, 
			String gAvailability, 
			String gOnlineOnly, 
			String gShippingWeight, 
			String gColor) {
		this.gid = gid;
		this.title = title;
		this.link = link;
		this.gPrice = gPrice;
		this.description = description;
		this.gCondition = gCondition;
		this.gBrand = gBrand;
		this.gMpn = gMpn;
		this.gImageLink = gImageLink;
		this.gProductType = gProductType;
		this.gQuantity = gQuantity;
		this.gAvailability = gAvailability;
		this.gOnlineOnly = gOnlineOnly;
		this.gShippingWeight = gShippingWeight;
		this.gColor = gColor;
	}

	public String toFeedEntityXml() {
		StringBuffer sb = new StringBuffer();
		sb.append(START_ELEMENT);
		if (gid != null) {
			sb.append("<g:id>" + gid + "</g:id>\r\n");
		}
		if (title != null) {
			sb.append("<title>" + title + "</title>\r\n");
		}
		if (link != null) {
			sb.append("<link>" + link + "</link>\r\n");
		}
		if (gPrice != null) {
			sb.append("<g:price>" + gPrice + " USD</g:price>\r\n");
		}
		if (description != null) {
			sb.append("<description>" + description + "</description>\r\n");
		}
		if (gCondition != null) {
			sb.append("<g:condition>" + gCondition + "</g:condition>\r\n");
		}
		if (gBrand != null) {
			sb.append("<g:brand>" + gBrand + "</g:brand>\r\n");
		}
		if (gMpn != null) {
			sb.append("<g:mpn>" + gMpn + "</g:mpn>\r\n");
		}
		if (gImageLink != null) {
			sb.append("<g:image_link>" + gImageLink + "</g:image_link>\r\n");
		}
		if (gProductType != null) {
			sb.append("<g:google_product_category>Apparel &amp; Accessories &gt; Jewelry</g:google_product_category>\r\n");
			sb.append("<g:product_type>" + gProductType + "</g:product_type>\r\n");
		}
		if (gQuantity != null) {
			sb.append("<g:quantity>" + gQuantity + "</g:quantity>\r\n");
		}
		if (gAvailability != null) {
			sb.append("<g:availability>" + gAvailability + "</g:availability>\r\n");
		}
		if (gOnlineOnly != null) {
			sb.append("<g:online_only>" + gOnlineOnly + "</g:online_only>\r\n");
		}
		if (gColor != null) {
			sb.append("<g:shipping_weight>" + gColor + "</g:shipping_weight\r\n");
		}
		if (gShippingWeight != null) {
			sb.append("<g:shipping_weight>" + gShippingWeight + " g</g:shipping_weight>\r\n");
		}
		sb.append(END_ELEMENT);
		if (sb.toString().equals(START_ELEMENT + END_ELEMENT)) {
			return "";
		}
		return sb.toString();
	}
}

package com.itecheasy.ph3.web.buyer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.itecheasy.common.PageList;
import com.itecheasy.ph3.SearchOrder;
import com.itecheasy.ph3.category.CategoryService;
import com.itecheasy.ph3.category.ShowCategory;
import com.itecheasy.ph3.product.LunceneProduct;
import com.itecheasy.ph3.product.ProductImage;
import com.itecheasy.ph3.product.ProductService;
import com.itecheasy.ph3.product.ProductService.ProductLunceneSearchCriteria;
import com.itecheasy.ph3.product.ProductService.ProductLunceneSearchOrder;
import com.itecheasy.ph3.web.BaseAction;
import com.itecheasy.ph3.web.WebConfig;
import com.itecheasy.ph3.web.tag.FuncitonUtils;
import com.itecheasy.ph3.web.tag.ProductPictureFuncitonUtils;
import com.itecheasy.ph3.web.tag.UrlFunction;
import com.itecheasy.ph3.web.utils.ConfigHelper;
import com.itecheasy.ph3.web.utils.ExportHelper;
import com.itecheasy.ph3.web.vo.rss.Image;
import com.itecheasy.ph3.web.vo.rss.Rss;
import com.itecheasy.ph3.web.vo.rss.RssItem;

public class RssAction extends BaseAction {
	private static final long serialVersionUID = 515398583026485517L;
	private static final String PAGE_HEAD_RECOMMENDED = "recommended";
	private static final String PAGE_HEAD_NEW_ARRIVAL = "new_arrival";
	private static final String PAGE_HEAD_MIX = "mix";
	private static final String PAGE_HEAD_CATEGORY = "category";
	private ProductService productService;
	private CategoryService categoryService;
	private final static int FEET_SIZE = 50;
	private Date finalPubDate;
	private Integer categoryId;

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * 新产品
	 */
	public void recently() {
		Rss rss = productToRss(PAGE_HEAD_NEW_ARRIVAL, FEET_SIZE);
		ExportHelper.exportXml(response, rss.toRssXml());

	}

	/**
	 * 推荐商品
	 */
	public void featured() {
		Rss rss = productToRss(PAGE_HEAD_RECOMMENDED, FEET_SIZE);
		ExportHelper.exportXml(response, rss.toRssXml());
	}

	/**
	 * 混装商品
	 */
	public void mixed() {
		Rss rss = productToRss(PAGE_HEAD_MIX, FEET_SIZE);
		ExportHelper.exportXml(response, rss.toRssXml());
	}

	/**
	 * 类别
	 */
	public void category() {
		String categoryIdStr = request.getParameter("categoryId");
		if (categoryIdStr != null && !"".equals(categoryIdStr)) {
			categoryId = Integer.valueOf(categoryIdStr);
		}
		Rss rss = productToRss(PAGE_HEAD_CATEGORY, FEET_SIZE);
		ExportHelper.exportXml(response, rss.toRssXml());
	}

	/**
	 * 
	 * @param specialArea
	 *            指定区域
	 * @param pageSize
	 *            分页大小
	 * @return RSS对象
	 */
	private Rss productToRss(String specialArea, int pageSize) {
		Rss rss = new Rss();
		WebConfig webconfig = WebConfig.getInstance();
		rss.setCopyRight(webconfig.get("Rss.copyRight"));
		Map<ProductLunceneSearchCriteria, Object> searchCriteria = new HashMap<ProductLunceneSearchCriteria, Object>(); // 设置查询参数
		List<SearchOrder<ProductLunceneSearchOrder>> searchOrder = new ArrayList<SearchOrder<ProductLunceneSearchOrder>>(); // 设置排序方式
		if (PAGE_HEAD_RECOMMENDED.equals(specialArea)) {// 推荐商品专区
			rss.setTitle(webconfig.get("Rss.Featured.title"));
			rss.setLink(webconfig.get("Rss.Featured.link"));
			rss.setDescription(webconfig.get("Rss.Featured.description"));
			Image image = new Image(webconfig.get("Rss.Featured.image.title"), webconfig.get("Rss.Featured.image.link"), webconfig.get("Rss.Logo.url"));
			rss.setImage(image);
			searchCriteria.put(ProductLunceneSearchCriteria.IS_RECOMMEND, true);
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.RECOMMEND_DATE, false));
		} else if (PAGE_HEAD_NEW_ARRIVAL.equals(specialArea)) {// 新产品专区
			rss.setTitle(webconfig.get("Rss.Recently.title"));
			rss.setLink(webconfig.get("Rss.Recently.link"));
			rss.setDescription(webconfig.get("Rss.Recently.description"));
			Image image = new Image(webconfig.get("Rss.Recently.image.title"), webconfig.get("Rss.Recently.image.link"), webconfig.get("Rss.Logo.url"));
			rss.setImage(image);
			Date date = FuncitonUtils.addDays(new Date(), -ConfigHelper.NEW_PRODUCTS_BEGIN_JOIN_DATE);
			searchCriteria.put(ProductLunceneSearchCriteria.BEGIN_JOIN_DATE, date);
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.JOIN_DATE, false));
		} else if (PAGE_HEAD_MIX.equals(specialArea)) {// 混装商品专区
			rss.setTitle(webconfig.get("Rss.Mix.title"));
			rss.setLink(webconfig.get("Rss.Mix.link"));
			rss.setDescription(webconfig.get("Rss.Mix.description"));
			Image image = new Image(webconfig.get("Rss.Mix.image.title"), webconfig.get("Rss.Mix.image.link"), webconfig.get("Rss.Logo.url"));
			rss.setImage(image);
			searchCriteria.put(ProductLunceneSearchCriteria.IS_MIX, true);
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.JOIN_DATE, false));
		} else if (categoryId != null) { // 商品类别
			ShowCategory category = categoryService.getShowCategory(categoryId);
			searchCriteria.put(ProductLunceneSearchCriteria.SHOW_CATEGORY_ID, categoryId);
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.JOIN_DATE, false));
			// 当前类别页面url
			String pageUrl = "";
			if (category != null) {
				rss.setTitle("PandaHallStock - New " + StringEscapeUtils.escapeHtml(category.getName()) + " Items");
				rss.setLink(UrlFunction.getCategoryProducts(category.getId(), category.getName()));
				rss.setDescription("Newest wholesale " + StringEscapeUtils.escapeHtml(category.getName()) + " for jewelry making around the world");
				pageUrl = UrlFunction.getCategoryProducts(category.getId(), category.getName(), 1, category.getShowMode());
			}
			Image image = new Image(webconfig.get("Rss.Category.image.title"), pageUrl, webconfig.get("Rss.Logo.url"));
			rss.setImage(image);

		}
		searchCriteria.put(ProductLunceneSearchCriteria.IS_HAS_STOCK, true);
		searchCriteria.put(ProductLunceneSearchCriteria.IS_DISPLAY, true);
		// 查询索引
		PageList<LunceneProduct> lProductPageList = productService.searchProductsFromLuncene(currentPage, pageSize, searchCriteria, searchOrder);
		List<LunceneProduct> listlp = lProductPageList != null ? lProductPageList.getData() : null;
		List<RssItem> rssItems = lunceneProductToRssObj(specialArea,listlp);
		rss.setItems(rssItems);
		rss.setPubDate(finalPubDate);
		return rss;
	}

	/**
	 * List<LunceneProduct> 转换到List<RSSItem>对象
	 * 
	 * @param lProductPageList
	 *            LunceneProduct 对象列表
	 * @return List<RSSItem>对象
	 */
	private List<RssItem> lunceneProductToRssObj(String specialArea,List<LunceneProduct> listlp) {
		List<RssItem> items = new ArrayList<RssItem>();
		// 图片大小
		int imageWidth = 360;
		int imageHeight = 360;
		if (listlp != null) {
			for (int i = 0; i < listlp.size(); i++) {
				RssItem rssItem = new RssItem();
				LunceneProduct lp = listlp.get(i);
				// 得到显示类别
				ShowCategory category = categoryService.getShowCategory(lp.getShowCategoryId());
				rssItem.setTitle(StringEscapeUtils.escapeHtml(lp.getName() + "(" + lp.getCode() + ")"));
				List<ProductImage> images = productService.getProductImages(lp.getId());
				String photoName = images != null && images.size() > 0 ? images.get(0).getImageUrl() : null;
				StringBuffer sbDescription = new StringBuffer();
				String imageSrc = ProductPictureFuncitonUtils.getPhotoUrl(photoName, imageWidth, imageHeight);
				sbDescription.append("<img src=\"" + imageSrc + "\" alt=\"" + StringEscapeUtils.escapeHtml(lp.getName()) + "\"/><br/>");
				sbDescription.append("Unit:" + lp.getNewUnitQuantity() + " " + lp.getUnit() + "<br/>");
				sbDescription.append("Weight:" + FuncitonUtils.getWeightString(lp.getShippingWeight(),false) + "g<br/>");
				String price = lp.getProductSalePrices() == null && lp.getProductSalePrices().size() > 0 ? "" : FuncitonUtils.getPriceString(lp.getProductSalePrices().get(0).getSalePriceAfterDiscount());
				sbDescription.append("Price/Unit: US $" + StringEscapeUtils.escapeHtml(price) + "<br/>");
				sbDescription.append(StringEscapeUtils.escapeHtml(lp.getDescription()) + "<br/>");
				sbDescription.append("Priced per " + lp.getNewUnitQuantity() + " " + StringEscapeUtils.escapeHtml(lp.getUnit()) + "<br/>");
				if (PAGE_HEAD_MIX.equals(specialArea)){
					sbDescription.append("Please Note: Due to stock variety, color or shape of mixed products may vary from photo sample shown on our website.<br/>");
				}
				rssItem.setDescription(sbDescription.toString());
				String cateGoryStr = "";
				cateGoryStr = category.getName();
				for (; category.getParent() != null;) {
					cateGoryStr = category.getParent().getName() + " > " + cateGoryStr;
					category = category.getParent();
				}
				rssItem.setCategory(StringEscapeUtils.escapeHtml(cateGoryStr));
				rssItem.setLink(StringEscapeUtils.escapeHtml(UrlFunction.getProductDetail(lp.getId(), lp.getName(), category.getName())));
				rssItem.setGuid(StringEscapeUtils.escapeHtml(lp.getCode()));
				Date joinTime = lp.getJoinPh3DateTime();
				// 得到最近发布的产品时间
				if (finalPubDate == null) {
					finalPubDate = joinTime;
				}
				if (joinTime != null && finalPubDate != null) {
					if (joinTime.compareTo(finalPubDate) > 0) {
						finalPubDate = joinTime;
					}
				}
				rssItem.setPubDate(joinTime);
				items.add(rssItem);
			}
		}
		return items;
	}
}

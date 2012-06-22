package com.itecheasy.ph3.web.buyer;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.itecheasy.common.Page;
import com.itecheasy.common.PageList;
import com.itecheasy.ph3.SearchOrder;
import com.itecheasy.ph3.category.CategoryService;
import com.itecheasy.ph3.category.ShowCategory;
import com.itecheasy.ph3.common.DeployProperties;
import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.product.LunceneProduct;
import com.itecheasy.ph3.product.Product;
import com.itecheasy.ph3.product.ProductImage;
import com.itecheasy.ph3.product.ProductPromotionService;
import com.itecheasy.ph3.product.ProductSalePrice;
import com.itecheasy.ph3.product.ProductService;
import com.itecheasy.ph3.product.ProductService.ProductLunceneSearchCriteria;
import com.itecheasy.ph3.product.ProductService.ProductLunceneSearchOrder;
import com.itecheasy.ph3.product.ProductService.ProductSearchStatistics;
import com.itecheasy.ph3.property.Property;
import com.itecheasy.ph3.property.PropertyService;
import com.itecheasy.ph3.property.PropertyValue;
import com.itecheasy.ph3.property.PropertyValueGroup;
import com.itecheasy.ph3.shopping.ShoppingCartProduct;
import com.itecheasy.ph3.web.BuyerBaseAction;
import com.itecheasy.ph3.web.Link;
import com.itecheasy.ph3.web.ListItem;
import com.itecheasy.ph3.web.WebConfig;
import com.itecheasy.ph3.web.exception.AppException;
import com.itecheasy.ph3.web.tag.FuncitonUtils;
import com.itecheasy.ph3.web.tag.ProductPictureFuncitonUtils;
import com.itecheasy.ph3.web.tag.UrlFunction;
import com.itecheasy.ph3.web.utils.ConfigHelper;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.utils.StrUtils;
import com.itecheasy.ph3.web.utils.UrlHelper;
import com.itecheasy.ph3.web.utils.WebUtils;
import com.itecheasy.ph3.web.vo.LunceneProductVO;
import com.itecheasy.ph3.web.vo.ProductSearchUrlParams;
import com.itecheasy.ph3.web.vo.ProductUrl;
import com.itecheasy.ph3.web.vo.PropertyVO;
import com.itecheasy.ph3.web.vo.PropertyValueGroupVO;
import com.itecheasy.ph3.web.vo.PropertyValueVO;
import com.itecheasy.ph3.web.vo.ShowCategoryTree;

public class BuyerProductAction extends BuyerBaseAction {
	private static final Logger PH3_LOG = Logger.getLogger("PH3");
	private static final long serialVersionUID = 1666L;
	private static final String PARAM_PROPERTY_VALUE = "p";
	private static final String PARAM_PROPERTY_VALUE_GROUP = "_p";
	private static final String PAGE_HEAD_HOME = "home";
	private static final String PAGE_HEAD_RECOMMENDED = "recommended";
	private static final String PAGE_HEAD_NEW_ARRIVAL = "new_arrival";
	private static final String PAGE_HEAD_MIX = "mix";
	private static final String PAGE_HEAD_WEEKLY_SPECIAL = "weekly_special";
	private static final String PAGE_HEAD_PROMOTION = "Promotion"; // 促销区
	private static final String PARAM_CURRENT_PAGE = "currentPage";
	private static final String PARAM_PAGE_SIZE_STRING = "pageSize";
	private static final String PARAM_SHOW_MODE_STRING = "showMode";
	private static final String PARAM_CATEGORY_ID_STRING = "categoryId";
	private static final String PARAM_SORT_INDEX_STRING = "sortIndex";
	private static final String NAVIGATION_PROMOTION_NAME = "Special Offer";
	private static final int PAGE_SIZE = 28;
	private static final int PAGE_SIZE_RECOMMENDED = 30;
	private static final int PAGE_SIZE_NEW_ARRIVAL_1 = 40;
	private static final int PAGE_SIZE_NEW_ARRIVAL_2 = 80;
	private static final int PAGE_SIZE_NEW_ARRIVAL_3 = 120;

	private static final String DESCR_PER_FORMAT = "<br/>Priced per %1$s %2$s";
	/** 混装商品增加的一段描述 */
	private static final String MIX_PRODUCT_DESC = WebConfig.getInstance().get("mix_product_description");
	private static final int COLOR_PORPERTY_ID = Integer.parseInt(WebConfig.getInstance().get("property.color.id"));
	private boolean queryProductFlag = true;
	private Integer sortIndex;
	private Integer showMode;
	private ProductService productService;
	private PropertyService propertyService;
	private ProductPromotionService productPromotionService;

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	public void setProductPromotionService(ProductPromotionService productPromotionService) {
		this.productPromotionService = productPromotionService;
	}

	public Integer getColorPorpertyId() {
		return COLOR_PORPERTY_ID;
	}

	public Integer getSortIndex() {
		if (sortIndex == null || sortIndex < 1) {
			sortIndex = 1;
		}
		return sortIndex;
	}

	public void setSortIndex(Integer sortIndex) {
		this.sortIndex = sortIndex;
	}

	public Integer getShowMode() {
		if (showMode == null || showMode < 1) {
			showMode = 1;
		}
		return showMode;
	}

	public void setShowMode(Integer showMode) {
		this.showMode = showMode;
	}

	/**
	 * 商品列表
	 */
	public String doProducts() throws AppException {
		ProductSearchUrlParams urlParams = getSearchUrlParams(false);
		Integer categoryId = urlParams.getShowCategoryId();
		ShowCategory category = null;

		if (categoryId != null && categoryId > 0) {
			category = categoryService.getShowCategory(categoryId);
		}
		if (category == null) {
			throw new AppException("Cannot find the category.");
		} else if (!category.getIsVisible()) {
			throw new AppException("The category is not visible.");
		}
		String rawUrl = UrlHelper.getRawUrl(request);

		String createUrl = UrlFunction.getCategoryProducts(categoryId, getAlias(category), Integer.parseInt(request.getParameter("currentPage")), Integer.parseInt(request.getParameter("showMode")));
		String queryString = UrlHelper.getQueryString(rawUrl);

		if (queryString != null) {
			createUrl += "?" + queryString;
		}
		// 重定向URL
		if (!createUrl.equalsIgnoreCase(rawUrl)) {
			redirect301(createUrl);
			return SUCCESS;
		}
		SessionUtils.setRecentViewedCategories(request, category);
		SessionUtils.setLastShoppingPageUrl(request, UrlHelper.getRawUrl(request));

		int showModeIndex = getShowMode();
		int sortIndex = getSortIndex();

		int productQty = 0;
		List<LunceneProductVO> products = null;
		List<PropertyVO> propertyStatistics = null;
		Page page = null;

		int propertyValueCount = urlParams.getPropertyCount() + 1;
		ProductUrl productUrl = getProductUrl(category, currentPage, showModeIndex, sortIndex);
		String propertiesQuery = getPropertiesQuery(urlParams.getUrlPropertyIds());
		String addPropertyValueUrl = getAddPropertyUrl(UrlHelper.mergeQueryString(productUrl.getBaseUrlOfFirstPage(), propertiesQuery), propertyValueCount);
		List<Link> navLinks = WebUtils.getCategoryNavLinks(category, propertyValueCount > 1);
		appendPropertiesNavLinks(navLinks, urlParams, productUrl, null);
		if (propertiesQuery != null) {
			productUrl.setPagerFormatUrl(UrlHelper.mergeQueryString(productUrl.getPagerFormatUrl(), propertiesQuery));
			productUrl.setShowModeFormatUrl(UrlHelper.mergeQueryString(productUrl.getShowModeFormatUrl(), propertiesQuery));
			productUrl.setSorterFormatUrl(UrlHelper.mergeQueryString(productUrl.getSorterFormatUrl(), propertiesQuery));
		}

		Map<ProductLunceneSearchCriteria, Object> searchCriteria = getSearchCriteria(urlParams);
		// 根据类别查看时不能查看无库存的商品
		searchCriteria.put(ProductLunceneSearchCriteria.IS_HAS_STOCK, true);

		List<SearchOrder<ProductLunceneSearchOrder>> searchOrder = getSearchOrders(sortIndex);
		PageList<LunceneProduct> lProductPageList = productService.searchProductsFromLuncene(currentPage, PAGE_SIZE, searchCriteria, searchOrder);
		if (lProductPageList != null) {
			page = lProductPageList.getPage();
			productQty = page.getTotalRowCount();
			if (productQty > 0) {
				products = toLunceneProductVo(lProductPageList.getData(), category, getShoppingCartProductMap(), getWishListProducts());
				propertyStatistics = getPropertyStatistics(category, searchCriteria, urlParams);
			}
		}

		addProductToWishList();

		// Rss显示与隐藏
		boolean showRss = false;
		// 带有参数为为进入第一个页面，不显示rss 图标
		if (urlParams != null) {
			showRss = urlParams.getKeyword() == null && urlParams.getPrice1() == null && urlParams.getPrice1() == null && urlParams.getPrice2() == null && urlParams.getPropertyValueGroupIds() == null
					&& urlParams.getShowCategoryId() != null && urlParams.getUrlPropertyIds() == null;
		}
		if (categoryId == null || category.getIsVisible() == false || currentPage != 1 || category.getShowMode() != showModeIndex) {
			showRss = false;
		}
		// 排序方式不同将不显示rss图标
		if (sortIndex == 2 || sortIndex == 3 || sortIndex == 4) {
			showRss = false;
		}
		if (showRss) {
			String urlPre = request.getScheme() + "://" + request.getHeader("host") + request.getContextPath();
			String categoryRssUrl = urlPre + "/Rss/sale-beads/" + category.getId() + "/" + StrUtils.replaceUrl(category.getName()) + ".xml";
			request.setAttribute("showRssHead", categoryRssUrl);
			request.setAttribute("showRssHeadTitle", "Bead Categories");
		}
		request.setAttribute("totalProductQty", productQty);
		request.setAttribute("products", products);
		request.setAttribute("recommendProducts", getRecommendProducts(categoryId));
		request.setAttribute("propertyStatistics", propertyStatistics);
		request.setAttribute("page", page);
		request.setAttribute("navLinks", navLinks);
		request.setAttribute("showModeFormatUrl", productUrl.getShowModeFormatUrl());
		request.setAttribute("pagerFormatUrl", productUrl.getPagerFormatUrl());
		request.setAttribute("sorterFormatUrl", productUrl.getSorterFormatUrl());
		request.setAttribute("addPropertyValueUrl", addPropertyValueUrl);
		request.setAttribute("propertyValueCount", propertyValueCount);
		request.setAttribute("homeType", PAGE_HEAD_HOME);
		request.setAttribute("seoCanonical", productUrl.getShowModeFormatUrl().replace(UrlFunction.SHOW_MODE_FORMAT, "1"));

		this.setPageSize(PAGE_SIZE);
		setCurrentCategory(category);
		List<String> propertyValues = new ArrayList<String>();
		if (urlParams.getPropertyValueIds() != null) {
			for (Integer p : urlParams.getPropertyValueIds()) {
				propertyValues.add(propertyService.getPropertyValue(p).getName());
			}
		}
		seo = seoService.getCategoryProductListSeo(category.getId(), getAlias(category), category.getParent() != null ? getAlias(category.getParent()) : null, propertyValues, currentPage, pageSize);
		return SUCCESS;
	}

	/**
	 * 搜索商品
	 * 
	 * @return
	 */
	public String searchProducts() {
		ProductSearchUrlParams urlParams = getSearchUrlParams(true);
		String keyword = urlParams.getKeyword();
		Integer categoryId = urlParams.getShowCategoryId();

		if (keyword == null) {
			request.setAttribute("sKeyword", keyword);
			return SUCCESS;
		}
		ShowCategory category = null;
		if (categoryId != null && categoryId > 0) {
			category = categoryService.getShowCategory(categoryId);
		}

		int showModeIndex = getShowMode();
		int sortIndex = getSortIndex();
		ProductUrl productUrl = getSearchProductUrl(keyword, categoryId, currentPage, showModeIndex, sortIndex);

		int productQty = 0;
		List<LunceneProductVO> products = null;
		List<PropertyVO> propertyStatistics = null;
		List<ShowCategoryTree> categoryStatistics = null;
		Page page = null;
		int propertyValueCount = urlParams.getPropertyCount() + 1;

		String propertiesQuery = getPropertiesQuery(urlParams.getUrlPropertyIds());
		String priceQuery = getPriceQuery(urlParams);
		String priceActionUrl = UrlHelper.mergeQueryString(productUrl.getBaseUrlOfFirstPage(), propertiesQuery);
		String addPropertyValueUrl = getAddPropertyUrl(UrlHelper.mergeQueryString(productUrl.getBaseUrlOfFirstPage(), propertiesQuery, priceQuery), propertyValueCount);

		if (priceActionUrl.indexOf("?") > -1) {
			priceActionUrl += "&";
		} else {
			priceActionUrl += "?";
		}
		String query = null;
		if (propertiesQuery != null) {
			query = propertiesQuery;
		}
		if (priceQuery != null) {
			if (query == null) {
				query = priceQuery;
			} else {
				query += "&" + priceQuery;
			}
		}
		if (query != null) {
			productUrl.setPagerFormatUrl(UrlHelper.mergeQueryString(productUrl.getPagerFormatUrl(), query));
			productUrl.setShowModeFormatUrl(UrlHelper.mergeQueryString(productUrl.getShowModeFormatUrl(), query));
			productUrl.setSorterFormatUrl(UrlHelper.mergeQueryString(productUrl.getSorterFormatUrl(), query));
		}
		List<Link> navLinks = new LinkedList<Link>();
		navLinks.add(new Link("Search result for \"" + keyword + "\"", UrlFunction.getProductSearch(keyword, null, 1, showModeIndex, sortIndex)));
		if (category != null) {
			int categoryLevel = categoryService.getShowCategoryLevel(categoryId);
			if (2 == categoryLevel) {
				navLinks.add(new Link(category.getName(), UrlFunction.getProductSearch(keyword, categoryId, currentPage, showModeIndex, sortIndex)));
			} else if (3 == categoryLevel) {
				navLinks.add(new Link(category.getParent().getName(), UrlFunction.getProductSearch(keyword, category.getParent().getId(), currentPage, showModeIndex, sortIndex)));
				navLinks.add(new Link(category.getName(), UrlFunction.getProductSearch(keyword, categoryId, currentPage, showModeIndex, sortIndex)));
			}
		}
		appendPropertiesNavLinks(navLinks, urlParams, productUrl, priceQuery);
		appendPriceNavLinks(navLinks, urlParams, productUrl, propertiesQuery);

		Map<ProductLunceneSearchCriteria, Object> searchCriteria = getSearchCriteria(urlParams);

		List<SearchOrder<ProductLunceneSearchOrder>> searchOrder = getSearchOrdersOfSearch(sortIndex);
		PageList<LunceneProduct> lProductPageList = productService.searchProductsFromLuncene(currentPage, PAGE_SIZE, searchCriteria, searchOrder, SessionUtils.getLoginedCustomer(request), request
				.getHeader("Referer"));
		if (lProductPageList != null) {
			page = lProductPageList.getPage();
			productQty = page.getTotalRowCount();
			if (productQty > 0) {
				products = toLunceneProductVo(lProductPageList.getData(), category, getShoppingCartProductMap(), getWishListProducts());
				propertyStatistics = getPropertyStatistics(category, searchCriteria, urlParams);
				final int colorId = Integer.parseInt(WebConfig.getInstance().get("property.color.id"));
				if (propertyStatistics != null && propertyStatistics.size() > 1) {
					for (int i = 1; i < propertyStatistics.size(); i++) {
						if (propertyStatistics.get(i).getProperty().getId() == colorId) {
							PropertyVO p = propertyStatistics.get(i);
							propertyStatistics.set(i, propertyStatistics.get(0));
							propertyStatistics.set(0, p);
							break;
						}
					}
				}

				categoryStatistics = getCategoryStatistics(keyword, category);
			}
		}

		addProductToWishList();

		request.setAttribute("sKeyword", keyword);
		request.setAttribute("searchPrice1", urlParams.getPrice1());
		request.setAttribute("searchPrice2", urlParams.getPrice2());
		request.setAttribute("totalProductQty", productQty);
		request.setAttribute("products", products);
		request.setAttribute("propertyStatistics", propertyStatistics);
		request.setAttribute("categoryStatistics", categoryStatistics);
		request.setAttribute("page", page);
		request.setAttribute("navLinks", navLinks);
		request.setAttribute("pagerFormatUrl", productUrl.getPagerFormatUrl());
		request.setAttribute("showModeFormatUrl", productUrl.getShowModeFormatUrl());
		request.setAttribute("sorterFormatUrl", productUrl.getSorterFormatUrl());
		request.setAttribute("priceActionUrl", priceActionUrl);
		request.setAttribute("addPropertyValueUrl", addPropertyValueUrl);
		request.setAttribute("propertyValueCount", propertyValueCount);
		request.setAttribute("homeType", PAGE_HEAD_HOME);
		request.setAttribute("categoryFormatUrl", productUrl.getCategoryFormatUrl());
		this.setPageSize(PAGE_SIZE);
		setCurrentCategory(category);
		return SUCCESS;
	}

	/**
	 * 商品详细
	 */
	public String doProductDetail() throws AppException {
		Integer productId = paramInt("id", 0);
		Product product = productService.getProduct(productId);
		if (product == null) {
			String ip = request.getRemoteHost();
			String url = UrlHelper.getRawUrl(request);
			PH3_LOG.error(" \r\n ip : " + ip + " url = " + url + "  Exception: Cannot find the product.");
			return "appException";
			// throw new AppException("Cannot find the product.");
		} else if (!product.getIsDisplay()) {
			String ip = request.getRemoteHost();
			String url = UrlHelper.getRawUrl(request);
			PH3_LOG.error(" \r\n ip : " + ip + " url = " + url + "  Exception: Cannot find the product.");
			return "appException";
			// throw new AppException("The product is out of stock.");
		}
		/*
		 * else if (product.getBatchStock() <= 0 ) { throw new
		 * AppException("Batch stock is Less than zero."); }
		 */
		ShowCategory category = productService.getShowCategoryOfProduct(productId);
		if (category == null) {
			throw new AppException("Cannot find the category of the product(product ID:" + productId.toString() + ").");
		} else if (!categoryIsVisible(category)) {
			throw new AppException("The category of product is not visible.");
		}

		String rawUrl = UrlHelper.getRawUrl(request);
		String createUrl = UrlFunction.getProductDetail(productId, product.getName(), getAlias(category));
		String queryString = request.getQueryString();
		queryString = queryString.substring(queryString.indexOf("&") + 1);
		if (queryString != null && !queryString.equals("") && queryString.contains("=")) {
			createUrl += "?" + queryString;
		}
		// 重定向URL
		if (!createUrl.equalsIgnoreCase(rawUrl)) {
			redirect301(createUrl);
			return SUCCESS;
		}

		ProductSalePrice productPrice = productService.getProductFirstSalePrice(productId);
		boolean isPromotion = productPromotionService.isPromotionProduct(productId);
		BigDecimal discount = productPromotionService.getPromoteDiscount(productId);
		StringBuilder description = new StringBuilder(productService.getProductDescription(productId));
		description.append(String.format(DESCR_PER_FORMAT, product.getUnitQuantity(), StringEscapeUtils.escapeHtml(product.getUnit())));
		if (productService.isMixProduct(productId)) {
			description.append(MIX_PRODUCT_DESC);
		}
		StringBuilder imagesString = new StringBuilder();
		StringBuilder largerImagesString = new StringBuilder();
		List<ProductImage> images = productService.getProductImages(productId);
		if (images != null && !images.isEmpty()) {
			// 最多只取四个图片
			if (images.size() > 4) {
				for (int i = images.size() - 1; i > 3; i--) {
					images.remove(i);
				}
			}
			for (ProductImage img : images) {
				imagesString.append("|").append(ProductPictureFuncitonUtils.getPhotoUrl(img.getImageUrl(), 360, 360));
				largerImagesString.append("|").append(ProductPictureFuncitonUtils.getPhotoUrl(img.getImageUrl(), 500, 500));
			}
			if (imagesString.length() > 0) {
				imagesString.replace(0, 1, "");
			}
			if (largerImagesString.length() > 0) {
				largerImagesString.replace(0, 1, "");
			}

		}

		List<Link> navLinks = WebUtils.getCategoryNavLinks(category, true);
		navLinks.add(new Link(product.getCode(), null));

		List<LunceneProductVO> similarProducts = null;
		List<LunceneProduct> similarProductList = productService.getRandomSimilarProducts(productId, 20);
		if (similarProductList != null && !similarProductList.isEmpty()) {
			similarProducts = new LinkedList<LunceneProductVO>();
			LunceneProductVO vo;
			for (LunceneProduct p : similarProductList) {
				vo = new LunceneProductVO();
				vo.setProduct(p);
				vo.setShowCategoryName(productService.getShowCategoryNameOfProduct(p.getId()));
				similarProducts.add(vo);
			}
		}

		StringBuilder productProperyValues = new StringBuilder();
		List<PropertyValue> propertyValueList = productService.getRelatedPropertyValueOfProduct(productId);
		if (propertyValueList != null && !propertyValueList.isEmpty()) {
			for (PropertyValue pv : propertyValueList) {
				productProperyValues.append(", ").append(pv.getName());
			}
			if (productProperyValues.length() > 0) {
				productProperyValues = productProperyValues.replace(0, 2, "");
			}
		}

		addProductToWishList();

		request.setAttribute("productInfo", product);
		request.setAttribute("productDescription", description.toString());
		request.setAttribute("productPrice", productPrice);
		request.setAttribute("isPromotion", isPromotion);
		request.setAttribute("discount", discount);
		request.setAttribute("productImages", images);
		request.setAttribute("productImagesString", imagesString);
		request.setAttribute("productLargerImagesString", largerImagesString);
		request.setAttribute("navLinks", navLinks);
		request.setAttribute("similarProducts", similarProducts);
		request.setAttribute("productProperyValues", productProperyValues.toString());
		request.setAttribute("productQtyInShoppingCart", shoppingService.getProductQtyInShoppingCart(WebUtils.getShoppingCartId(request), productId));
		request.setAttribute("isAddToWishList", isAddProductToWishList(productId));
		setCurrentCategory(category);
		List<String> productAttributeValues = new ArrayList<String>();
		for (PropertyValue value : propertyValueList) {
			productAttributeValues.add(value.getName());
		}
		seo = seoService.getProductDetailSeo(category.getId(), getAlias(category), category.getParent() != null ? getAlias(category.getParent()) : null, product.getName(), product.getCode(),
				productAttributeValues);
		return SUCCESS;
	}

	/**
	 * AJAX 商品详细
	 */
	public String doProductDetailByAjax() {
		Integer productId = paramInt("id", 0);
		Product product = null;
		ProductSalePrice productPrice = null;
		StringBuilder description = new StringBuilder();
		String productDetailUrl = null;
		boolean isPromotion = false;
		BigDecimal discount = new BigDecimal(0);
		if (productId > 0) {
			product = productService.getProduct(productId);
		}
		if (product != null) {
			productPrice = productService.getProductFirstSalePrice(productId);
			isPromotion = productPromotionService.isPromotionProduct(productId);
			discount = productPromotionService.getPromoteDiscount(productId);
			description.append(productService.getProductDescription(productId));
			description.append(String.format(DESCR_PER_FORMAT, product.getUnitQuantity(), StringEscapeUtils.escapeHtml(product.getUnit())));
			if (productService.isMixProduct(productId)) {
				description.append(MIX_PRODUCT_DESC);
			}
			String categoryName = productService.getShowCategoryNameOfProduct(productId);
			productDetailUrl = UrlFunction.getProductDetail(productId, product.getName(), categoryName == null ? "category" : categoryName);
		}
		request.setAttribute("product", product);
		request.setAttribute("productPrice", productPrice);
		request.setAttribute("isPromotion", isPromotion);
		request.setAttribute("discount", discount);
		request.setAttribute("productDescription", description.toString());
		request.setAttribute("productDetailUrl", productDetailUrl);
		return SUCCESS;
	}

	/**
	 * 促销商品列表
	 * 
	 * @return
	 */
	public String doWeeklySpecialProducts() {
		// 此处只是临时代码
		request.setAttribute("BatchStock_1", productService.getProductBatchStock(41083));
		request.setAttribute("BatchStock_2", productService.getProductBatchStock(453379));
		request.setAttribute("BatchStock_3", productService.getProductBatchStock(156130));
		request.setAttribute("BatchStock_4", productService.getProductBatchStock(169874));
		request.setAttribute("BatchStock_5", productService.getProductBatchStock(453702));
		request.setAttribute("BatchStock_6", productService.getProductBatchStock(405321));
		request.setAttribute("BatchStock_7", productService.getProductBatchStock(458887));
		request.setAttribute("BatchStock_8", productService.getProductBatchStock(3042));
		request.setAttribute("BatchStock_9", productService.getProductBatchStock(454750));
		request.setAttribute("BatchStock_10", productService.getProductBatchStock(36210));
		request.setAttribute("BatchStock_11", productService.getProductBatchStock(378612));
		request.setAttribute("BatchStock_12", productService.getProductBatchStock(440592));

		// List<PromotionProduct> promotionProductList =
		// productPromotionService.getCurrentPromoteProducts();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date endDate = sdf.parse(WebConfig.getInstance().get("promotion.end.date"));
			long seconds = WebUtils.getDifferenceSeconds(new Date(), endDate);
			request.setAttribute("PromotionEndTime", seconds);
		} catch (Exception ex) {

		}

		request.setAttribute("homeType", PAGE_HEAD_WEEKLY_SPECIAL);

		// request.setAttribute("PromotionProductList", promotionProductList);

		return SUCCESS;
	}

	/**
	 * 推荐商品列表
	 * 
	 * @return
	 */
	public String doRecommendedProducts() {
		// 搜索商品
		searchProductBySpecialArea(PAGE_HEAD_RECOMMENDED, PAGE_SIZE_RECOMMENDED, false, false, false, false, true, true);
		String featuredItemsRss = null;
		if (currentPage == 1) {
			featuredItemsRss = request.getScheme() + "://" + request.getHeader("host") + request.getContextPath() + "/Rss/Featured-items.xml";
			request.setAttribute("showRssHead", featuredItemsRss);
			request.setAttribute("showRssHeadTitle", "Featured Items");
		}

		return SUCCESS;
	}

	/**
	 * 新商品列表
	 * 
	 * @return
	 */
	public String doNewProducts() {
		// 获得当前需要显示的页大小
		int pageSize = getPageSizeOfNewProducts();
		// 搜索商品
		searchProductBySpecialArea(PAGE_HEAD_NEW_ARRIVAL, pageSize, true, true, true, false, false, false);

		return SUCCESS;
	}

	/**
	 * 混装商品列表
	 * 
	 * @return
	 */
	public String doMixedProducts() {
		// 搜索商品
		searchProductBySpecialArea(PAGE_HEAD_MIX, PAGE_SIZE, true, true, true, true, true, true);

		return SUCCESS;
	}

	/**
	 * 促销商品进入
	 * 
	 * @return
	 */
	public String doPromotion() {
		int pageSize = getPageSizeOfNewProducts();
		String baseUrl = UrlHelper.getRequestUrl();
		//pc是指是否有属性值进行统计，在此把类别做为一个属性值的方式方。存在pc做为转跳到静态页的条件。
		Integer currentCategoryId = paramInt(PARAM_CATEGORY_ID_STRING);
		if(baseUrl.contains("pc=") || currentCategoryId !=null){
			queryProductFlag = true;
		}else{
			queryProductFlag = false;
		}
		searchProductBySpecialArea(PAGE_HEAD_PROMOTION, pageSize, true, true, true, false, true, true);
		if (!queryProductFlag) {
			return "staticHtml";
		}
		return SUCCESS;
	}

	private boolean isAddProductToWishList(Integer productId) {
		Customer user = this.getLoginedUserBuyer();
		if (user == null) {
			return false;
		}
		return shoppingService.isAddProductToWishList(user.getId(), productId);
	}

	private List<Integer> getWishListProducts() {
		// 如果显示模式不是列表，则返回NULL
		if (getShowMode() != 1) {
			return null;
		}
		Customer user = this.getLoginedUserBuyer();
		if (user == null) {
			return null;
		}
		return shoppingService.getProductIdsOfWishList(user.getId());
	}

	private Map<Integer, Integer> getShoppingCartProductMap() {
		Integer cartId = WebUtils.getShoppingCartId(request);
		if (cartId == null) {
			return null;
		}
		List<ShoppingCartProduct> items = shoppingService.getProdcutsOfShoppingCart(cartId);
		if (items == null || items.isEmpty()) {
			return null;
		}
		Map<Integer, Integer> shoppingCartProductMap = new HashMap<Integer, Integer>();
		for (ShoppingCartProduct item : items) {
			shoppingCartProductMap.put(item.getItem().getProductId(), item.getItem().getProductQty());
		}
		items = null;
		return shoppingCartProductMap;
	}

	/**
	 * 搜索专区商品
	 * 
	 * @param specialArea
	 *            专区名称
	 * @param pageSize
	 *            页大小
	 * @param hasUrlParams
	 *            URL是否包含参数
	 * @param isSetUrlFormate
	 *            是否需要设置各种Tag的Url的格式
	 */
	private void searchProductBySpecialArea(String specialArea, int pageSize, boolean hasUrlParams, boolean isSetUrlFormate, boolean isPropertyStatistics, boolean isCategoryStatistics,
			boolean showShoppingCartStatus, boolean showWishListStatus) {
		// 获得Url传过来的过滤条件
		ProductSearchUrlParams urlParams = null;
		ShowCategory currentCategory = null;
		Integer categoryId = null;
		String baseUrl = UrlHelper.getBaseUrl(request);
		String wholeUrl = baseUrl;
		int propertyValueCount = 0;

		if (hasUrlParams) {
			urlParams = getSearchUrlParams(false);

			if (urlParams != null) {
				propertyValueCount = urlParams.getPropertyCount() + 1;

				categoryId = urlParams.getShowCategoryId();
				if (categoryId != null && categoryId > 0) {
					currentCategory = categoryService.getShowCategory(categoryId);
				}

				// 设置Url链接地址
				wholeUrl = setUrlParams(urlParams, baseUrl, specialArea, propertyValueCount);

				// 设置专区导航
				setNavigation(urlParams, baseUrl, specialArea);
			}
		}

		// 设置查询条件
		Map<ProductLunceneSearchCriteria, Object> searchCriteria = getSearchCriteriaBySpecialArea(urlParams, specialArea);

		if (queryProductFlag) { // 默认都查询，除了进入促销区首页时
			// 搜索商品
			searchProductBySpecialArea(specialArea, pageSize, searchCriteria, showShoppingCartStatus, showWishListStatus);
		}

		if (isSetUrlFormate) {// 设置各种Tag的Url模式：排序、PageSize和显示模式
			setFormatUrl(specialArea, wholeUrl, currentCategory);
		}

		if (isCategoryStatistics) {  //当促销区没有选中当前类别时也进行统计
			// 首先获得该专区的基本查询条件，即排除通过筛选的条件，因为始终要显示全类别树
			Map<ProductLunceneSearchCriteria, Object> baseSearchCriteria = getBaseSearchCriteriaBySpecialArea(specialArea);
			doCategoryStatisticsBySpecialArea(baseSearchCriteria);
		}
		if (isPropertyStatistics) {// 进行属性统计
			doPropertyStatisticsBySpecialArea(currentCategory, searchCriteria, urlParams, specialArea);
		}
		
		Integer currentCategoryId = paramInt(PARAM_CATEGORY_ID_STRING); //当关类别ID
		if((currentCategoryId == null && PAGE_HEAD_PROMOTION.equals(specialArea))){
			// 首先获得该专区的基本查询条件，即排除通过筛选的条件，因为始终要显示全类别树
			//Map<ProductLunceneSearchCriteria, Object> baseSearchCriteria = getBaseSearchCriteriaBySpecialArea(specialArea);
			doCategoryStatisticsBySpecialAreaPromotion(searchCriteria);
		}

		request.setAttribute("pageSize", pageSize);
		request.setAttribute("homeType", specialArea);
		request.setAttribute("propertyValueCount", propertyValueCount);
		request.setAttribute("currentCategoryId", categoryId);
		request.setAttribute("currentCategory", currentCategory);
		request.setAttribute("baseUrl", baseUrl);
	}

	/**
	 * 搜索专区商品
	 * 
	 * @param specialArea
	 *            专区名称
	 * @param pageSize
	 *            页大小
	 * @param searchCriteria
	 *            搜索条件
	 */
	private void searchProductBySpecialArea(String specialArea, int pageSize, Map<ProductLunceneSearchCriteria, Object> searchCriteria, boolean showShoppingCartStatus, boolean showWishListStatus) {
		// 设置排序方式
		List<SearchOrder<ProductLunceneSearchOrder>> searchOrder = getSearchOrderaBySpecialArea(specialArea);

		// 查询索引
		PageList<LunceneProduct> lProductPageList = productService.searchProductsFromLuncene(currentPage, pageSize, searchCriteria, searchOrder);

		int productQty = 0;
		List<LunceneProductVO> products = null;
		Page page = null;

		if (lProductPageList != null) {
			page = lProductPageList.getPage();
			productQty = page.getTotalRowCount();
			if (productQty > 0) {
				products = toLunceneProductVo(lProductPageList.getData(), null, showShoppingCartStatus ? getShoppingCartProductMap() : null, showWishListStatus ? getWishListProducts() : null);
			}
		}

		request.setAttribute("page", page);
		request.setAttribute("totalProductQty", productQty);
		request.setAttribute("products", products);
	}

	/**
	 * 获得专区当前所有的搜索条件
	 * 
	 * @param urlParams
	 *            URL参数对象
	 * @param specialArea
	 *            专区名称
	 * @return 搜索条件
	 */
	private Map<ProductLunceneSearchCriteria, Object> getSearchCriteriaBySpecialArea(ProductSearchUrlParams urlParams, String specialArea) {
		Map<ProductLunceneSearchCriteria, Object> searchCriteria = getSearchCriteria(urlParams);
		if (searchCriteria == null)
			searchCriteria = new HashMap<ProductLunceneSearchCriteria, Object>();

		if (PAGE_HEAD_RECOMMENDED.equals(specialArea)) {// 推荐商品专区
			searchCriteria.put(ProductLunceneSearchCriteria.IS_RECOMMEND, true);
			searchCriteria.put(ProductLunceneSearchCriteria.IS_HAS_STOCK, true);
		} else if (PAGE_HEAD_NEW_ARRIVAL.equals(specialArea)) {// 新产品专区
			Date date = FuncitonUtils.addDays(new Date(), -ConfigHelper.NEW_PRODUCTS_BEGIN_JOIN_DATE);
			searchCriteria.put(ProductLunceneSearchCriteria.BEGIN_JOIN_DATE, date);
			searchCriteria.put(ProductLunceneSearchCriteria.IS_HAS_STOCK, true);
		} else if (PAGE_HEAD_MIX.equals(specialArea)) {// 混装商品专区
			searchCriteria = getSearchCriteria(urlParams);
			searchCriteria.put(ProductLunceneSearchCriteria.IS_MIX, true);
			searchCriteria.put(ProductLunceneSearchCriteria.IS_HAS_STOCK, true);
		} else if (PAGE_HEAD_PROMOTION.equals(specialArea)) {
			searchCriteria = getSearchCriteria(urlParams);
			searchCriteria.put(ProductLunceneSearchCriteria.BEGIN_DISCOUNT, new BigDecimal(0.01));
		} else {
			searchCriteria.put(ProductLunceneSearchCriteria.IS_HAS_STOCK, true);
		}
		searchCriteria.put(ProductLunceneSearchCriteria.IS_DISPLAY, true);
		return searchCriteria;
	}

	/**
	 * 获得专区的基本搜索条件,即不包含类别、属性或其它过滤条件
	 * 
	 * @param urlParams
	 *            URL参数对象
	 * @param specialArea
	 *            专区名称
	 * @return 搜索条件
	 */
	private Map<ProductLunceneSearchCriteria, Object> getBaseSearchCriteriaBySpecialArea(String specialArea) {
		Map<ProductLunceneSearchCriteria, Object> searchCriteria = new HashMap<ProductLunceneSearchCriteria, Object>();

		if (PAGE_HEAD_RECOMMENDED.equals(specialArea)) {// 推荐商品专区
			searchCriteria.put(ProductLunceneSearchCriteria.IS_RECOMMEND, true);
			searchCriteria.put(ProductLunceneSearchCriteria.IS_HAS_STOCK, true);
		} else if (PAGE_HEAD_NEW_ARRIVAL.equals(specialArea)) {// 新产品专区
			Date date = FuncitonUtils.addDays(new Date(), -ConfigHelper.NEW_PRODUCTS_BEGIN_JOIN_DATE);
			searchCriteria.put(ProductLunceneSearchCriteria.BEGIN_JOIN_DATE, date);
			searchCriteria.put(ProductLunceneSearchCriteria.IS_HAS_STOCK, true);
		} else if (PAGE_HEAD_MIX.equals(specialArea)) {// 混装商品专区
			searchCriteria.put(ProductLunceneSearchCriteria.IS_MIX, true);
			searchCriteria.put(ProductLunceneSearchCriteria.IS_HAS_STOCK, true);
		} else if (PAGE_HEAD_PROMOTION.equals(specialArea)) {
			searchCriteria.put(ProductLunceneSearchCriteria.BEGIN_DISCOUNT, new BigDecimal(0.01));
		} else {
			searchCriteria.put(ProductLunceneSearchCriteria.IS_HAS_STOCK, true);
		}
		searchCriteria.put(ProductLunceneSearchCriteria.IS_DISPLAY, true);
		return searchCriteria;
	}

	/**
	 * 获得专区的排序方式
	 * 
	 * @param specialArea
	 *            专区名称
	 * @return
	 */
	private List<SearchOrder<ProductLunceneSearchOrder>> getSearchOrderaBySpecialArea(String specialArea) {
		List<SearchOrder<ProductLunceneSearchOrder>> searchOrder = new ArrayList<SearchOrder<ProductLunceneSearchOrder>>();
		if (PAGE_HEAD_RECOMMENDED.equals(specialArea)) {// 推荐商品专区
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.RECOMMEND_DATE, false));
		} else if (PAGE_HEAD_NEW_ARRIVAL.equals(specialArea)) {// 新产品专区
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.JOIN_DATE, false));
		} else if (PAGE_HEAD_MIX.equals(specialArea)) {// 混装商品专区
			searchOrder = getSearchOrders(getSortIndex());
		} else if (PAGE_HEAD_PROMOTION.equals(specialArea)) {
			searchOrder = getSearchOrders(getSortIndex());
		}

		return searchOrder;
	}

	/**
	 * 统计展示类别信息，含三级的全树
	 * 
	 * @param searchCategoryCriteria
	 *            查询条件
	 * 
	 */
	private void doCategoryStatisticsBySpecialArea(Map<ProductLunceneSearchCriteria, Object> searchCategoryCriteria) {
		List<ShowCategoryTree> showCategoryStatistics = null;
		if (searchCategoryCriteria != null && !searchCategoryCriteria.isEmpty()) {
			// 根据条件获得统计结果
			Map<String, Integer> categoryStatisticsMap = productService.getProductsStatisticsFromLuncene(searchCategoryCriteria, ProductSearchStatistics.ALL_SHOW_CATEGORY);

			if (categoryStatisticsMap != null && !categoryStatisticsMap.isEmpty()) {
				// 获得全部的展示类别
				List<ShowCategoryTree> allCateogryList = getCategoryTreeList();

				showCategoryStatistics = getCategoryStatisticsBySpecialArea(allCateogryList, categoryStatisticsMap);
			}
		}

		
		request.setAttribute("showCategoryStatistics", showCategoryStatistics);
	}

	/**
	 * 专为促销提供,类别统计
	 * @param searchCategoryCriteria
	 */
	private void doCategoryStatisticsBySpecialAreaPromotion(Map<ProductLunceneSearchCriteria, Object> searchCategoryCriteria) {
		List<ShowCategoryTree> showCategoryStatistics = null;
		if (searchCategoryCriteria != null && !searchCategoryCriteria.isEmpty()) {
			// 根据条件获得统计结果
			Map<String, Integer> categoryStatisticsMap = productService.getProductsStatisticsFromLuncene(searchCategoryCriteria, ProductSearchStatistics.ALL_SHOW_CATEGORY);
			if (categoryStatisticsMap != null && !categoryStatisticsMap.isEmpty()) {
				// 获得全部的展示类别
				List<ShowCategoryTree> allCateogryList = getCategoryTreeList();
				List<ShowCategoryTree> allCateogryListLeveTwo = new ArrayList<ShowCategoryTree>();
				for(int i =0 ; allCateogryList != null && i < allCateogryList.size(); i++){
					ShowCategoryTree leverOne = allCateogryList.get(i);
					if(leverOne != null && leverOne.getSubCategories() != null && leverOne.getSubCategories().size() >0 ){
						allCateogryListLeveTwo.addAll(leverOne.getSubCategories());
					}
				}
				showCategoryStatistics = getCategoryStatisticsBySpecialArea(allCateogryListLeveTwo, categoryStatisticsMap);
				if(showCategoryStatistics != null){
					sortCategoryTree(showCategoryStatistics); //排序操作	
				}
			}
		}
		request.setAttribute("showCategoryStatistics", showCategoryStatistics);
	}
	
	/**
	 * 获得展示类别统计信息，递归统计
	 * 
	 * @param showCategoryTree
	 *            初始类别树
	 * @param categoryStatisticsMap
	 *            统计结果
	 * 
	 * @return 有商品的展示类别树
	 */
	private List<ShowCategoryTree> getCategoryStatisticsBySpecialArea(List<ShowCategoryTree> showCategoryTree, Map<String, Integer> categoryStatisticsMap) {
		if (categoryStatisticsMap == null || categoryStatisticsMap.isEmpty()) {
			return null;
		}

		if (showCategoryTree == null || showCategoryTree.isEmpty()) {
			return null;
		}

		List<ShowCategoryTree> treeList = new LinkedList<ShowCategoryTree>();
		// 根据统计信息得到有商品的展示类别
		String categoryId;
		int productCount = 0;
		ShowCategoryTree showCategory = null;
		for (ShowCategoryTree category : showCategoryTree) {
			categoryId = category.getCategory().getId().toString();
			if (categoryStatisticsMap.containsKey(categoryId)) {
				productCount = categoryStatisticsMap.get(categoryId);
				if (productCount > 0) {
					showCategory = new ShowCategoryTree();
					showCategory.setCategory(category.getCategory());
					showCategory.setProductQty(productCount);
					showCategory.setSubCategories(getCategoryStatisticsBySpecialArea(category.getSubCategories(), categoryStatisticsMap));
					treeList.add(showCategory);
				}
			}
		}

		return treeList;
	}

	/**
	 * 统计属性信息
	 * 
	 * @param category
	 *            统计的显示类别
	 * @param searchCriteria
	 *            查询条件
	 * @param urlParams
	 *            URL参数对象
	 * @param specialArea
	 *            专区名称
	 * 
	 */
	private void doPropertyStatisticsBySpecialArea(ShowCategory category, Map<ProductLunceneSearchCriteria, Object> searchCriteria, ProductSearchUrlParams urlParams, String specialArea) {
		List<PropertyVO> propertyStatistics = null;
		if (PAGE_HEAD_NEW_ARRIVAL.equals(specialArea)) { // 新产品专区
			List<Property> propertyList = new ArrayList<Property>();
			propertyList.add(propertyService.getProperty(ConfigHelper.PROPERTY_SHAPE_ID));
			propertyList.add(propertyService.getProperty(ConfigHelper.PROPERTY_USAGE_ID));
			propertyList.add(propertyService.getProperty(ConfigHelper.PROPERTY_COLOR_ID));
			propertyStatistics = bindPropertyStatistics(searchCriteria, propertyList, urlParams);
		} else if (PAGE_HEAD_MIX.equals(specialArea)) {// 混装区
			if (category != null && category.getCategoryType().equals(CategoryService.CATEGORY_TYPE_PRODUCT)) {
				propertyStatistics = getPropertyStatistics(category, searchCriteria, urlParams);
			}
		} else if (PAGE_HEAD_PROMOTION.equals(specialArea)) {
			List<Property> propertyList = new ArrayList<Property>();
			propertyList.add(propertyService.getProperty(Integer.parseInt(DeployProperties.getInstance().getProperty("property.Discount.id"))));
			propertyList.add(propertyService.getProperty(ConfigHelper.PROPERTY_USAGE_ID));
			propertyList.add(propertyService.getProperty(ConfigHelper.PROPERTY_SHAPE_ID));
			propertyList.add(propertyService.getProperty(ConfigHelper.PROPERTY_COLOR_ID));
			propertyStatistics = bindPropertyStatistics(searchCriteria, propertyList, urlParams);
		}
		request.setAttribute("propertyStatistics", propertyStatistics);
	}

	/**
	 * 设置Url链接
	 * 
	 * @param urlParams
	 *            获得URL传递的参数
	 * @param specialArea
	 *            专区名称
	 * 
	 */
	private String setUrlParams(ProductSearchUrlParams urlParams, String baseUrl, String specialArea, int propertyValueCount) {
		StringBuilder urlString = new StringBuilder(baseUrl);
		if (urlParams != null) {
			String propertiesQuery = getPropertiesQuery(urlParams.getUrlPropertyIds());
			urlString.append(getAddPropertyUrl(UrlHelper.mergeQueryString("", propertiesQuery), propertyValueCount));
		}

		String url = addSpecialAreaParams(urlString.toString());

		request.setAttribute("addPropertyValueUrl", url);

		return url;
	}

	/**
	 * 设置专区的导航信息
	 * 
	 */
	private void setNavigation(ProductSearchUrlParams urlParams, String baseUrl, String specialArea) {
		List<Link> navLinks = new ArrayList<Link>();
		// 添加类别导航
		Integer currentCategoryId = paramInt(PARAM_CATEGORY_ID_STRING);
		if (currentCategoryId != null && specialArea.equals(PAGE_HEAD_PROMOTION)) { // 设置属性统计中根据类型统计的面包屑。
			ShowCategory category = categoryService.getShowCategory(currentCategoryId);
			if (category != null && urlParams != null && urlParams.getPropertyCount() > 0) {
				//在导航上加入pc=1的目的是把类别的id 变向的转为属性值id做转跳到静态页的参数。但是查询时还是类别id 
				navLinks.add(new Link(category.getName(), String.format("%1$s?%2$s", baseUrl,PARAM_CATEGORY_ID_STRING + "=" + category.getId())));  
			}else if(category != null){
				navLinks.add(new Link(category.getName(),null));
			}
		}else if (currentCategoryId != null) { //除促销区以外的通用代码
			ShowCategory category = categoryService.getShowCategory(currentCategoryId);
			if (category != null) {
				int pCount = paramInt("pc", 0);

				navLinks = new LinkedList<Link>();
				ShowCategory parent = category.getParent();
				if (parent != null) {
					if (parent.getParent() != null) {
						navLinks.add(new Link(parent.getParent().getName(), String.format("%1$s?%2$s", baseUrl, PARAM_CATEGORY_ID_STRING + "=" + parent.getParent().getId())));
					}
					navLinks.add(new Link(parent.getName(), String.format("%1$s?%2$s", baseUrl, PARAM_CATEGORY_ID_STRING + "=" + parent.getId())));
				}
				navLinks.add(new Link(category.getName(), (pCount > 0 ? String.format("%1$s?%2$s", baseUrl, PARAM_CATEGORY_ID_STRING + "=" + category.getId()) : null)));
			}
		}

		// 添加属性导航
		if (urlParams != null && urlParams.getPropertyCount() > 0) {
			String propertyBaseUrl = addSpecialAreaParams(baseUrl);

			appendPropertiesNavLinks(navLinks, urlParams, propertyBaseUrl, null);
		}

		// 添加专区名称
		String linkName = getNavigationName(specialArea);
		if (navLinks.size() > 0) {
			navLinks.add(0, new Link(linkName, baseUrl));
		} else {
			navLinks.add(new Link(linkName, null));
		}

		// 设置RSS 与url 比较并非比较参数
		String url = UrlHelper.getRequestUrl(request);
		String urlPre = request.getScheme() + "://" + request.getHeader("host") + request.getContextPath();
		if (url.equalsIgnoreCase(urlPre + "/customer/doMixedProducts.do")) {// 混合商品
			request.setAttribute("showRssHead", urlPre + "/Rss/Mixed-Products.xml");
			request.setAttribute("showRssHeadTitle", "Mixed Products");
		} else if (url.equalsIgnoreCase(urlPre + "/customer/doNewProducts.do")) { // 新商品
			request.setAttribute("showRssHead", urlPre + "/Rss/Recently-Listed-Items.xml");
			request.setAttribute("showRssHeadTitle", "Recently Listed Items");
		} else if (url.equalsIgnoreCase(urlPre + "/customer/doRecommendedProducts.do")) { // 推荐商品
			request.setAttribute("showRssHead", urlPre + "/Rss/Featured-items.xml");
		}
		request.setAttribute("navLinks", navLinks);
	}

	/**
	 * 加上专区特有的参数信息
	 * 
	 */
	private String addSpecialAreaParams(String url) {
		// 将导航上加上PageSize的参数
		Integer pageSize = paramInt(PARAM_PAGE_SIZE_STRING);
		if (pageSize != null) {
			url = UrlFunction.addParam(url, PARAM_PAGE_SIZE_STRING, pageSize.toString());
		}

		// 设置当前类别参数
		Integer currentCategoryId = paramInt(PARAM_CATEGORY_ID_STRING);
		if (currentCategoryId != null) {
			url = UrlFunction.addParam(url, PARAM_CATEGORY_ID_STRING, currentCategoryId.toString());
		}

		// 设置显示模式参数
		Integer showMode = paramInt(PARAM_SHOW_MODE_STRING);
		if (showMode != null) {
			url = UrlFunction.addParam(url, PARAM_SHOW_MODE_STRING, showMode.toString());
		}

		// 设置显示模式参数
		Integer sortIndex = paramInt(PARAM_SORT_INDEX_STRING);
		if (sortIndex != null) {
			url = UrlFunction.addParam(url, PARAM_SORT_INDEX_STRING, sortIndex.toString());
		}

		return url;
	}

	/**
	 * 获取专区导航名称
	 * 
	 * @param specialArea
	 *            专区名称
	 * @return
	 */
	private String getNavigationName(String specialArea) {
		if (PAGE_HEAD_NEW_ARRIVAL.equals(specialArea)) {// 新产品专区
			return ConfigHelper.NAVIGATION_NAME_NEW_ARRIVAL;
		} else if (PAGE_HEAD_MIX.equals(specialArea)) {// 混装商品专区
			return ConfigHelper.NAVIGATION_NAME_MIX;
		} else if (PAGE_HEAD_PROMOTION.equals(specialArea)) {
			return NAVIGATION_PROMOTION_NAME;
		}

		return "";
	}

	/**
	 * 搜索专区的排序和显示模式、PageSize设置
	 * 
	 * @param specialArea
	 *            专区名称
	 * @param baseUrl
	 *            专区的基本URL
	 * @param category
	 *            当前的类别
	 */
	private void setFormatUrl(String specialArea, String baseUrl, ShowCategory category) {
		// 改变模式和排序时不改变页号
		Integer currenctPage = paramInt(PARAM_CURRENT_PAGE);
		if (currenctPage != null) {
			baseUrl = UrlFunction.addParam(baseUrl, PARAM_CURRENT_PAGE, currenctPage.toString());
		}

		Integer showMode = paramInt(PARAM_SHOW_MODE_STRING);
		if (showMode == null && PAGE_HEAD_MIX.equals(specialArea)) {// 混装商品专区默认为Grid模式
			showMode = 2;
		}

		Integer sortIndex = paramInt(PARAM_SORT_INDEX_STRING);
		String sorterUrl = UrlHelper.setUrlParameter(baseUrl, PARAM_SORT_INDEX_STRING, "{" + PARAM_SORT_INDEX_STRING + "}");
		String showModeUrl = UrlHelper.setUrlParameter(baseUrl, PARAM_SHOW_MODE_STRING, "{" + PARAM_SHOW_MODE_STRING + "}");
		String pageSizeUrl = UrlHelper.setUrlParameter(baseUrl, PARAM_PAGE_SIZE_STRING, "{" + PARAM_PAGE_SIZE_STRING + "}");

		request.setAttribute("showModeFormatUrl", showModeUrl);
		request.setAttribute("sorterFormatUrl", sorterUrl);
		request.setAttribute("pageSizeFormatUrl", pageSizeUrl);
		request.setAttribute("showMode", showMode);
		request.setAttribute("sortIndex", sortIndex);
	}

	/**
	 * 获得新产品专区的页大小
	 * 
	 * @return 页大小
	 */
	private int getPageSizeOfNewProducts() {
		Integer pageSize = paramInt(PARAM_PAGE_SIZE_STRING);
		if (pageSize == null)
			return PAGE_SIZE_NEW_ARRIVAL_1;

		switch (pageSize) {
		case PAGE_SIZE_NEW_ARRIVAL_1:
		case PAGE_SIZE_NEW_ARRIVAL_2:
		case PAGE_SIZE_NEW_ARRIVAL_3:
			return pageSize;
		default:
			return PAGE_SIZE_NEW_ARRIVAL_1;
		}
	}

	/**
	 * 如果URL有wish参数，则把商品加入收藏夹
	 */
	private void addProductToWishList() {
		int productId = paramInt("wish", 0);
		if (productId < 1) {
			return;
		}
		Customer user = this.getLoginedUserBuyer();
		if (user == null) {
			return;
		}
		shoppingService.addProductToWishList(user.getId(), productId);
	}

	private static boolean categoryIsVisible(ShowCategory category) {
		if (true != category.getIsVisible()) {
			return false;
		}
		ShowCategory parent = category.getParent();
		if (parent != null) {
			if (true != parent.getIsVisible()) {
				return false;
			}
			if (parent.getParent() != null) {
				if (true != parent.getParent().getIsVisible()) {
					return false;
				}
			}
		}
		return true;
	}

	private List<LunceneProductVO> getRecommendProducts(int categoryId) {
		List<LunceneProductVO> recommendProducts = null;
		List<LunceneProduct> rProducts = productService.getRecommendProductsByShowCategoryFromLuncene(categoryId, 4);
		if (rProducts != null && !rProducts.isEmpty()) {
			recommendProducts = new LinkedList<LunceneProductVO>();
			LunceneProductVO vo;
			for (LunceneProduct rProduct : rProducts) {
				vo = new LunceneProductVO();
				vo.setProduct(rProduct);
				/*
				 * vo.setFirstPrice(productService.getProductFirstSalePrice(rProduct
				 * .getId()));
				 */
				recommendProducts.add(vo);
			}
		}
		return recommendProducts;
	}

	private ProductSearchUrlParams getSearchUrlParams(boolean isSearch) {
		ProductSearchUrlParams criteriaInfo = new ProductSearchUrlParams();
		criteriaInfo.setShowCategoryId(paramInt("categoryId", null));
		if (isSearch) {
			criteriaInfo.setKeyword(param("keyword"));
			BigDecimal price1 = StrUtils.tryParseBigDecimal(param("price1"), BigDecimal.ZERO);
			BigDecimal price2 = StrUtils.tryParseBigDecimal(param("price2"), BigDecimal.ZERO);
			if (price1.compareTo(BigDecimal.ZERO) > 0 && price2.compareTo(BigDecimal.ZERO) > 0 && price1.compareTo(price2) > 0) {
				BigDecimal temp = price1;
				price1 = price2;
				price2 = temp;
			}
			if (price1.compareTo(BigDecimal.ZERO) > 0) {
				criteriaInfo.setPrice1(price1.setScale(2, BigDecimal.ROUND_DOWN));
			}
			if (price2.compareTo(BigDecimal.ZERO) > 0) {
				criteriaInfo.setPrice2(price2.setScale(2, BigDecimal.ROUND_DOWN));
			}
		}
		int pCount = paramInt("pc", 0);
		if (pCount > 0) {
			List<Integer> searchPropertyValueIds = new LinkedList<Integer>();
			List<Integer> searchPropertyValueGroupIds = new LinkedList<Integer>();
			List<ListItem<Integer, String>> urlPropertyIds = new LinkedList<ListItem<Integer, String>>();// 已选择的属性值或分组ID
			Map parameterMap = request.getParameterMap();
			String key;
			int pId;
			for (int i = 1; i <= pCount; i++) {
				key = PARAM_PROPERTY_VALUE + i;
				if (parameterMap.containsKey(key)) {
					// 属性值ID
					pId = paramInt(key, 0);
					if (pId > 0) {
						searchPropertyValueIds.add(pId);
						urlPropertyIds.add(new ListItem<Integer, String>(pId, PARAM_PROPERTY_VALUE));
					}
				} else {
					// 属性值分组ID
					key = PARAM_PROPERTY_VALUE_GROUP + i;
					pId = paramInt(key, 0);
					if (pId > 0) {
						searchPropertyValueGroupIds.add(pId);
						urlPropertyIds.add(new ListItem<Integer, String>(pId, PARAM_PROPERTY_VALUE_GROUP));
					}
				}
			}
			if (!searchPropertyValueIds.isEmpty()) {
				criteriaInfo.setPropertyValueIds(searchPropertyValueIds);
			}
			if (!searchPropertyValueGroupIds.isEmpty()) {
				criteriaInfo.setPropertyValueGroupIds(searchPropertyValueGroupIds);
			}
			if (!urlPropertyIds.isEmpty()) {
				criteriaInfo.setUrlPropertyIds(urlPropertyIds);
			}
		}
		return criteriaInfo;
	}

	private static ProductUrl getProductUrl(ShowCategory category, int pageIndex, int showMode, int sortIndex) {
		ProductUrl url = new ProductUrl();
		String baseUrl = UrlFunction.getCategoryProducts(category.getId(), category.getName(), pageIndex, showMode);
		String baseUrlOfFirstPage = UrlFunction.getCategoryProducts(category.getId(), category.getName(), 1, showMode);
		String showModeUrl = UrlFunction.getCategoryProductsShowModeFormat(category.getId(), category.getName(), pageIndex);
		String pagerUrl = UrlFunction.getCategoryProductsPagerFormat(category.getId(), category.getName(), showMode);
		String sorterUrl = baseUrlOfFirstPage + "?sortIndex={sortIndex}";
		if (sortIndex > 1) {
			String sortQuery = "?sortIndex=" + sortIndex;
			baseUrl += sortQuery;
			baseUrlOfFirstPage += sortQuery;
			showModeUrl += sortQuery;
			pagerUrl += sortQuery;
		}
		url.setBaseUrl(baseUrl);
		url.setBaseUrlOfFirstPage(baseUrlOfFirstPage);
		url.setPagerFormatUrl(pagerUrl);
		url.setShowModeFormatUrl(showModeUrl);
		url.setSorterFormatUrl(sorterUrl);
		return url;
	}

	private static ProductUrl getSearchProductUrl(String keyword, Integer categoryId, int pageIndex, int showMode, int sortIndex) {
		ProductUrl url = new ProductUrl();
		String baseUrl = UrlFunction.getProductSearch(keyword, categoryId, pageIndex, showMode, sortIndex);
		String baseUrlOfFirstPage = UrlFunction.getProductSearch(keyword, categoryId, 1, showMode, sortIndex);
		String pagerUrl = UrlFunction.getProductSearchPagerFormat(keyword, categoryId, showMode, sortIndex);
		String showModeUrl = UrlFunction.getProductSearchShowModeFormat(keyword, categoryId, pageIndex, sortIndex);
		String sorterUrl = UrlFunction.getProductSearchSorterFormat(keyword, categoryId, pageIndex, showMode);
		url.setBaseUrl(baseUrl);
		url.setBaseUrlOfFirstPage(baseUrlOfFirstPage);
		url.setPagerFormatUrl(pagerUrl);
		url.setShowModeFormatUrl(showModeUrl);
		url.setSorterFormatUrl(sorterUrl);
		url.setCategoryFormatUrl(UrlFunction.getProductSearchCategoryFormat(keyword, 1, showMode, sortIndex));
		return url;
	}

	private Map<ProductLunceneSearchCriteria, Object> getSearchCriteria(ProductSearchUrlParams info) {
		Map<ProductLunceneSearchCriteria, Object> searchCriteria = new HashMap<ProductLunceneSearchCriteria, Object>();
		if (info == null)
			return searchCriteria;

		if (info.getShowCategoryId() != null) {
			searchCriteria.put(ProductLunceneSearchCriteria.SHOW_CATEGORY_ID, info.getShowCategoryId());
		}
		if (info.getKeyword() != null) {
			searchCriteria.put(ProductLunceneSearchCriteria.KEY_WORD, info.getKeyword());
		}
		if (info.getPrice1() != null) {
			searchCriteria.put(ProductLunceneSearchCriteria.BEGIN_PRICE, info.getPrice1());
		}
		if (info.getPrice2() != null) {
			searchCriteria.put(ProductLunceneSearchCriteria.END_PRICE, info.getPrice2());
		}
		if (info.getPropertyValueIds() != null) {
			searchCriteria.put(ProductLunceneSearchCriteria.PROPERTY_VALUE_ID, info.getPropertyValueIds());
		}
		if (info.getPropertyValueGroupIds() != null) {
			searchCriteria.put(ProductLunceneSearchCriteria.PROPERTY_VALUE_GROUP_ID, info.getPropertyValueGroupIds());
		}
		// searchCriteria.put(ProductLunceneSearchCriteria.IS_HAS_STOCK, true);
		return searchCriteria;
	}

	private static String getPriceQuery(ProductSearchUrlParams urlParams) {
		StringBuilder priceUrlQuery = new StringBuilder();
		if (urlParams.getPrice1() != null) {
			priceUrlQuery.append("&price1=").append(urlParams.getPrice1());
		}
		if (urlParams.getPrice2() != null) {
			priceUrlQuery.append("&price2=").append(urlParams.getPrice2());
		}
		return priceUrlQuery.length() > 0 ? priceUrlQuery.replace(0, 1, "").toString() : null;
	}

	private static String getPriceLinkText(ProductSearchUrlParams info) {
		StringBuilder priceLinkText = new StringBuilder();
		if (info.getPrice1() != null) {
			if (info.getPrice2() != null) {
				priceLinkText.append("US $ ").append(info.getPrice1()).append(" ~ ").append(info.getPrice2());
			} else {
				priceLinkText.append("above US $ ").append(info.getPrice1());
			}
		} else {
			if (info.getPrice2() != null) {
				priceLinkText.append("under US $ ").append(info.getPrice2());
			}
		}
		if (priceLinkText.length() == 0) {
			return null;
		}
		return priceLinkText.toString();
	}

	private void appendPropertiesNavLinks(List<Link> links, ProductSearchUrlParams urlParams, String baseUrl, String priceQuery) {
		List<ListItem<Integer, String>> urlPropertyIds = urlParams.getUrlPropertyIds();
		if (urlPropertyIds == null) {
			return;
		}
		int i = 1;
		for (ListItem<Integer, String> item : urlPropertyIds) {
			if (item.getValue().equals(PARAM_PROPERTY_VALUE)) {
				links.add(getPropertyValueLink(item.getKey(), urlPropertyIds, baseUrl, priceQuery, i < urlPropertyIds.size()));
			} else {
				links.add(getPropertyValueGroupLink(item.getKey(), urlPropertyIds, baseUrl, priceQuery, i < urlPropertyIds.size()));
			}
			i++;
		}
	}

	private void appendPropertiesNavLinks(List<Link> links, ProductSearchUrlParams urlParams, ProductUrl urls, String priceQuery) {
		appendPropertiesNavLinks(links, urlParams, urls.getBaseUrlOfFirstPage(), priceQuery);
	}

	private void appendPriceNavLinks(List<Link> links, ProductSearchUrlParams urlParams, ProductUrl urls, String propertiesQuery) {
		String priceQuery = getPriceQuery(urlParams);
		if (priceQuery != null) {
			String priceLinkText = getPriceLinkText(urlParams);
			String priceUrl = UrlHelper.mergeQueryString(urls.getBaseUrl(), propertiesQuery);
			links.add(new Link(getLinkText(priceLinkText, priceUrl), null, false));
		}
	}

	private Link getPropertyValueLink(Integer propertyValueId, List<ListItem<Integer, String>> selectedPropertyIds, String url, String priceQuery, boolean canClick) {
		PropertyValue pv = propertyService.getPropertyValue(propertyValueId);
		String name = (pv != null ? StringEscapeUtils.escapeHtml(pv.getName()) : "");
		String propertyUrl = getDeletePropertyUrl(url, selectedPropertyIds, propertyValueId, false);
		propertyUrl = UrlHelper.mergeQueryString(propertyUrl, priceQuery);

		if (canClick) {
			String clickPropertyUrl = getDeletePropertysUrl(url, selectedPropertyIds, propertyValueId, false);
			// clickPropertyUrl =
			// UrlHelper.mergeQueryString(justPropertyUrl,priceQuery);
			return new Link(getLinkText(name, clickPropertyUrl, propertyUrl), null, false);
		} else {
			return new Link(getLinkText(name, propertyUrl), null, false);
		}
	}

	private Link getPropertyValueGroupLink(Integer propertyValueGroupId, List<ListItem<Integer, String>> selectedPropertyIds, String url, String priceQuery, boolean canClick) {
		PropertyValueGroup pvg = propertyService.getPropertyValueGroup(propertyValueGroupId);
		String name = (pvg != null ? StringEscapeUtils.escapeHtml(pvg.getName()) : "");
		String propertyUrl = getDeletePropertyUrl(url, selectedPropertyIds, propertyValueGroupId, true);
		propertyUrl = UrlHelper.mergeQueryString(propertyUrl, priceQuery);
		if (canClick) {
			String clickPropertyUrl = getDeletePropertysUrl(url, selectedPropertyIds, propertyValueGroupId, true);
			// clickPropertyUrl =
			// UrlHelper.mergeQueryString(justPropertyUrl,priceQuery);
			return new Link(getLinkText(name, clickPropertyUrl, propertyUrl), null, false);
		} else {
			return new Link(getLinkText(name, propertyUrl), null, false);
		}
	}

	private static String getLinkText(String text, String deleteUrl) {
		String fm = "<p class=\"PropertyFilter\"><strong>%1$s</strong><a class=\"FilterClose\" href=\"%2$s\">Close</a></p>";
		return String.format(fm, text, deleteUrl);
	}

	private static String getLinkText(String text, String clickUrl, String deleteUrl) {
		String fm = "<p class=\"PropertyFilter\"><a href=\"%2$s\"><strong>%1$s</strong></a><a class=\"FilterClose\" href=\"%3$s\">Close</a></p>";
		return String.format(fm, text, clickUrl, deleteUrl);
	}

	private String getDeletePropertyUrl(String url, List<ListItem<Integer, String>> ids, Integer ignoreId, boolean isGroup) {
		String urlString = getDeletePropertyQuery(ids, ignoreId, isGroup);
		return UrlHelper.mergeQueryString(url, urlString);
	}

	private static String getDeletePropertysUrl(String url, List<ListItem<Integer, String>> ids, Integer ignoreId, boolean isGroup) {
		String urlString = getDeletePropertiesQuery(ids, ignoreId, isGroup);
		return UrlHelper.mergeQueryString(url, urlString);
	}

	/**
	 * 获取所有选定属性值URL参数串
	 */
	private static String getPropertiesQuery(List<ListItem<Integer, String>> ids) {
		if (ids == null || ids.isEmpty()) {
			return null;
		}
		int count = ids.size();
		StringBuilder urlQuery = new StringBuilder();
		urlQuery.append("pc=").append(count);
		int index = 1;
		for (ListItem<Integer, String> item : ids) {
			urlQuery.append("&").append(item.getValue()).append(index).append("=").append(item.getKey());
			index++;
		}
		return urlQuery.toString();
	}

	/**
	 * 获取删除某个属性值URL参数串
	 * 
	 * @param ids
	 * @param deleteId
	 * @param isGroup
	 *            是否为属性值分组
	 * @return
	 */
	private String getDeletePropertyQuery(final List<ListItem<Integer, String>> ids, Integer deleteId, boolean isGroup) {
		int count = ids.size();
		if (count < 2) {
			return null;
		}
		StringBuilder url = new StringBuilder();
		url.append("pc=").append(count - 1);
		int index = 1;
		int id;
		List<PropertyValue> subPropertyValues = null;
		ListItem<Integer, String> item;
		for (int i = 0; i < ids.size(); i++) {
			item = ids.get(i);
			id = item.getKey();
			if (deleteId == id) {
				if (isGroup) {
					if (item.getValue().equals(PARAM_PROPERTY_VALUE_GROUP)) {
						if (i < ids.size() - 1) {
							subPropertyValues = propertyService.getVisiblePropertyValuesByPropertyValueGroup(id);
						}
						continue;
					}
				} else {
					if (item.getValue().equals(PARAM_PROPERTY_VALUE)) {
						continue;
					}
				}
			} else {
				// 排除属性值分组下的属性值
				if (isGroup && item.getValue().equals(PARAM_PROPERTY_VALUE)) {
					if (containPropertyValue(subPropertyValues, id)) {
						continue;
					}
				}
			}
			url.append("&").append(item.getValue()).append(index).append("=").append(id);
			index++;
		}
		return url.toString();
	}

	private static boolean containPropertyValue(List<PropertyValue> propertyValues, int propertyValueId) {
		if (propertyValues == null || propertyValues.isEmpty()) {
			return false;
		}
		for (PropertyValue pv : propertyValues) {
			if (pv.getId().equals(propertyValueId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取删除某个属性值后面所有属性值的URL参数串
	 */
	private static String getDeletePropertiesQuery(List<ListItem<Integer, String>> ids, Integer propertyId, boolean isGroup) {
		if (ids.isEmpty()) {
			return null;
		}
		StringBuilder url = new StringBuilder();
		int index = 1;
		int id;
		for (ListItem<Integer, String> item : ids) {
			id = item.getKey();
			url.append("&").append(item.getValue()).append(index).append("=").append(id);
			index++;
			if (propertyId == id) {
				if (isGroup) {
					if (item.getValue().equals(PARAM_PROPERTY_VALUE_GROUP)) {
						break;
					}
				} else {
					if (item.getValue().equals(PARAM_PROPERTY_VALUE)) {
						break;
					}
				}
			}
		}
		return "pc=" + index + url.toString();
	}

	private static String getAddPropertyUrl(String url, int propertyCount) {

		return UrlHelper.setUrlParameter(url, "pc", String.valueOf(propertyCount));
	}

	private static List<SearchOrder<ProductLunceneSearchOrder>> getSearchOrders(int sortIndex) {
		List<SearchOrder<ProductLunceneSearchOrder>> searchOrder = new ArrayList<SearchOrder<ProductLunceneSearchOrder>>();
		switch (sortIndex) {
		case 2: // Price Low To High
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.SALE_PRICED));
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.STOCK, false));
			break;
		case 3: // Price High To Low
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.SALE_PRICED, false));
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.STOCK, false));
			break;
		case 4: // Newest
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.JOIN_DATE, false));
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.STOCK, false));
			break;
		default:// Best Match
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.STOCK, false));
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.JOIN_DATE, false));
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.SALE_PRICED, false));
			break;
		}
		return searchOrder;
	}

	private static List<SearchOrder<ProductLunceneSearchOrder>> getSearchOrdersOfSearch(int sortIndex) {
		List<SearchOrder<ProductLunceneSearchOrder>> searchOrder = new ArrayList<SearchOrder<ProductLunceneSearchOrder>>();
		switch (sortIndex) {
		case 2: // Price Low To High
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.SALE_PRICED));
			break;
		case 3: // Price High To Low
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.SALE_PRICED, false));
			break;
		case 4: // Newest
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.JOIN_DATE, false));
			break;
		default:// Best Match
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.STOCK, false));
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.KEY_WORD));
			searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(ProductLunceneSearchOrder.JOIN_DATE, false));
			break;
		}
		return searchOrder;
	}

	private List<PropertyVO> getPropertyStatistics(ShowCategory category, Map<ProductLunceneSearchCriteria, Object> searchCriteria, ProductSearchUrlParams urlParams) {
		List<Property> propertyList = null;
		if (category != null) {
			if (category.getCategoryType().equals(CategoryService.CATEGORY_TYPE_PRODUCT)) {
				// 商品类别
				propertyList = categoryService.getBindedPropertiesOfShowCategory(category.getId());
			} else {
				// 非商品类别，则合并该子类别属性
				List<ShowCategory> categoryList = categoryService.getVisibleSubShowCategories(category.getId());
				if (categoryList != null && !categoryList.isEmpty()) {
					propertyList = new LinkedList<Property>();
					List<Property> tempList;
					for (ShowCategory sCategory : categoryList) {
						tempList = categoryService.getBindedPropertiesOfShowCategory(sCategory.getId());
						if (tempList != null && !tempList.isEmpty()) {
							propertyList.addAll(tempList);
						}
					}
				}
			}
		} else {
			propertyList = propertyService.getVisibleProperties();
		}

		if (propertyList == null)
			return null;

		propertyList = new ArrayList<Property>(new HashSet<Property>(propertyList));
		Collections.sort(propertyList, new Comparator<Property>() {
			@Override
			public int compare(Property o1, Property o2) {
				return o1.getOrderIndex().compareTo(o2.getOrderIndex());
			}
		});
		return bindPropertyStatistics(searchCriteria, propertyList, urlParams);
	}

	private List<PropertyVO> bindPropertyStatistics(Map<ProductLunceneSearchCriteria, Object> searchCriteria, List<Property> propertyList, ProductSearchUrlParams urlParams) {
		if (propertyList == null || propertyList.isEmpty()) {
			return null;
		}

		Map<String, Integer> pvMap = productService.getProductsStatisticsFromLuncene(searchCriteria, ProductSearchStatistics.PROPERTY_VALUE);
		if (pvMap == null || pvMap.isEmpty()) {
			return null;
		}

		List<Integer> propertyGroupIds = urlParams.getPropertyValueGroupIds();
		List<PropertyVO> propertyVOList = new LinkedList<PropertyVO>();
		PropertyVO propertyVO;
		PropertyValueGroupVO pvgVO;
		List<PropertyValueGroupVO> pvgVOList;

		Integer propertyId;
		List<PropertyValue> pvList;
		List<PropertyValueGroup> pvgList;
		boolean hasGroup;
		String pvId;
		for (Property property : propertyList) {
			if (true != property.getIsVisible()) {
				continue;
			}
			propertyId = property.getId();
			hasGroup = propertyService.existPropertyValueGroup(propertyId);
			propertyVO = new PropertyVO();

			if (hasGroup) {
				// 统计属性值分组
				pvgList = propertyService.getVisiblePropertyValueGroupsByProperty(propertyId);
				if (pvgList == null || pvgList.isEmpty()) {
					continue;
				}
				pvgVOList = new LinkedList<PropertyValueGroupVO>();
				for (PropertyValueGroup pvgItem : pvgList) {
					// 取得该属性值分组下的属性值
					pvList = propertyService.getVisiblePropertyValuesByPropertyValueGroup(pvgItem.getId());
					if (pvList == null || pvList.isEmpty()) {
						continue;
					}

					if (propertyGroupIds != null && propertyGroupIds.contains(pvgItem.getId())) {
						// 统计属性值分组下的属性值
						hasGroup = false;
						statisticsPropertyValue(urlParams.getPropertyValueIds(), pvMap, pvList, propertyVO);
					} else {
						// 统计属性值分组的商品数
						int qty = 0;
						for (PropertyValue pvItem : pvList) {
							pvId = pvItem.getId().toString();
							if (pvMap.containsKey(pvId)) {
								qty += pvMap.get(pvId);
							}
						}
						if (qty > 0) {
							pvgVO = new PropertyValueGroupVO();
							pvgVO.setPropertyValueGroup(pvgItem);
							pvgVO.setProductQty(qty);
							pvgVOList.add(pvgVO);
						}
					}
				}
				if (!pvgVOList.isEmpty()) {
					sortPropertyValueGroup(pvgVOList);
					propertyVO.setPropertyValueGroups(pvgVOList);
				}
			} else {
				// 统计属性值
				pvList = propertyService.getVisiblePropertyValuesByProperty(propertyId);
				if (pvList == null || pvList.isEmpty()) {
					continue;
				}
				statisticsPropertyValue(urlParams.getPropertyValueIds(), pvMap, pvList, propertyVO);
			}
			if (hasGroup) {
				if (propertyVO.getPropertyValueGroups() == null) {
					continue;
				}
			} else {
				if (propertyVO.getPropertyValues() == null) {
					continue;
				}
			}
			propertyVO.setProperty(property);
			propertyVO.setExistPropertyValueGroup(hasGroup);
			propertyVOList.add(propertyVO);
		}
		return propertyVOList;
	}

	private static void statisticsPropertyValue(List<Integer> selectedPropertyValueIds, Map<String, Integer> pvMap, List<PropertyValue> pvList, PropertyVO propertyVO) {
		List<PropertyValueVO> pvVOList = new LinkedList<PropertyValueVO>();
		String pvId;
		PropertyValueVO pvVO;
		for (PropertyValue item : pvList) {
			// 如果是已选属性值，则排除
			if (selectedPropertyValueIds != null && selectedPropertyValueIds.contains(item.getId())) {
				continue;
			}
			pvId = item.getId().toString();
			if (pvMap.containsKey(pvId)) {
				pvVO = new PropertyValueVO();
				pvVO.setPropertyValue(item);
				pvVO.setProductQty(pvMap.get(pvId));
				pvVOList.add(pvVO);
			}
		}
		if (!pvVOList.isEmpty()) {
			sortPropertyValue(pvVOList);
			propertyVO.setPropertyValues(pvVOList);
		}
	}

	private static void sortPropertyValue(List<PropertyValueVO> list) {
		Collections.sort(list, new Comparator<PropertyValueVO>() {
			@Override
			public int compare(PropertyValueVO o1, PropertyValueVO o2) {
				if (o1.getProductQty() == o2.getProductQty()) {
					return o1.getPropertyValue().getName().compareTo(o2.getPropertyValue().getName());
				} else if (o1.getProductQty() > o2.getProductQty()) {
					return -1;
				}
				return 1;
			}
		});
	}

	private static void sortPropertyValueGroup(List<PropertyValueGroupVO> list) {
		Collections.sort(list, new Comparator<PropertyValueGroupVO>() {
			@Override
			public int compare(PropertyValueGroupVO o1, PropertyValueGroupVO o2) {
				if (o1.getProductQty() == o2.getProductQty()) {
					return o1.getPropertyValueGroup().getName().compareTo(o2.getPropertyValueGroup().getName());
				} else if (o1.getProductQty() > o2.getProductQty()) {
					return -1;
				}
				return 1;
			}
		});
	}

	/**
	 * 展示类别统计
	 */
	private List<ShowCategoryTree> getCategoryStatistics(String keyword, ShowCategory selectedCategory) {
		Map<ProductLunceneSearchCriteria, Object> searchCategoryCriteria = new HashMap<ProductLunceneSearchCriteria, Object>();
		searchCategoryCriteria.put(ProductLunceneSearchCriteria.KEY_WORD, keyword);
		Map<String, Integer> categoryStatisticsMap = productService.getProductsStatisticsFromLuncene(searchCategoryCriteria, ProductSearchStatistics.SHOW_CATEGORY);
		if (categoryStatisticsMap == null || categoryStatisticsMap.isEmpty()) {
			return null;
		}
		List<ShowCategory> level2Categories = categoryService.getVisibleLevel2ShowCategories();
		if (level2Categories == null || level2Categories.isEmpty()) {
			return null;
		}

		List<ShowCategoryTree> treeList = new LinkedList<ShowCategoryTree>();
		ShowCategoryTree categoryTree;
		List<ShowCategory> level3Categories;
		String categoryId;
		for (ShowCategory category : level2Categories) {
			categoryId = category.getId().toString();
			categoryTree = new ShowCategoryTree();
			categoryTree.setCategory(category);
			if (category.getCategoryType().equals(CategoryService.CATEGORY_TYPE_PRODUCT)) {
				if (categoryStatisticsMap.containsKey(categoryId)) {
					categoryTree.setProductQty(categoryStatisticsMap.get(categoryId));
					treeList.add(categoryTree);
				}
			} else {
				level3Categories = categoryService.getVisibleSubShowCategories(category.getId());
				if (level3Categories != null && !level3Categories.isEmpty()) {
					int qty = 0;
					List<ShowCategoryTree> treeList3 = new LinkedList<ShowCategoryTree>();
					ShowCategoryTree categoryTree3;
					for (ShowCategory category3 : level3Categories) {
						categoryId = category3.getId().toString();
						if (categoryStatisticsMap.containsKey(categoryId)) {
							categoryTree3 = new ShowCategoryTree();
							categoryTree3.setCategory(category3);
							categoryTree3.setProductQty(categoryStatisticsMap.get(categoryId));
							treeList3.add(categoryTree3);
							qty += categoryTree3.getProductQty();
						}
					}
					if (qty > 0) {
						categoryTree.setProductQty(qty);
						sortCategoryTree(treeList3);
						categoryTree.setSubCategories(treeList3);
						treeList.add(categoryTree);
					}
				}
			}
		}
		if (treeList.size() == 0) {
			return null;
		}
		sortCategoryTree(treeList);
		if (treeList.size() > 1 && selectedCategory != null) {
			int level = categoryService.getShowCategoryLevel(selectedCategory.getId());
			int index = -1;
			if (2 == level) {
				index = getCategoryIndex(treeList, selectedCategory);
			} else if (3 == level) {
				index = getCategoryIndex(treeList, selectedCategory.getParent());
			}
			if (index > 0) {
				// 当前类别排列第一位
				treeList.add(0, treeList.get(index));
				treeList.remove(index + 1);
			}
		}

		return treeList;
	}

	private static int getCategoryIndex(List<ShowCategoryTree> categoryList, ShowCategory category) {
		if (category == null) {
			return -1;
		}
		Integer id = category.getId();
		for (int i = 0; i < categoryList.size(); i++) {
			if (id.compareTo(categoryList.get(i).getCategory().getId()) == 0) {
				return i;
			}
		}
		return -1;
	}

	private static void sortCategoryTree(List<ShowCategoryTree> list) {
		Collections.sort(list, new Comparator<ShowCategoryTree>() {
			@Override
			public int compare(ShowCategoryTree o1, ShowCategoryTree o2) {
				if (o1.getProductQty() == o2.getProductQty()) {
					return o1.getCategory().getName().compareTo(o2.getCategory().getName());
				} else if (o1.getProductQty() > o2.getProductQty()) {
					return -1;
				}
				return 1;
			}
		});
	}

	private static boolean isAddToWishList(List<Integer> wishListProductIds, Integer productId) {
		if (wishListProductIds == null || wishListProductIds.isEmpty()) {
			return false;
		}
		return wishListProductIds.contains(productId);
	}

	private List<LunceneProductVO> toLunceneProductVo(List<LunceneProduct> productList, ShowCategory category, Map<Integer, Integer> shoppingCartProductMap, List<Integer> wishListProductIds) {
		List<LunceneProductVO> products = new LinkedList<LunceneProductVO>();
		LunceneProductVO vo;
		boolean isPromotion;
		BigDecimal discount;
		if (shoppingCartProductMap == null) {
			if (category != null) {
				for (LunceneProduct product : productList) {
					vo = new LunceneProductVO(product, category.getName());
					vo.setIsAddToWishList(isAddToWishList(wishListProductIds, product.getId()));
					isPromotion = productPromotionService.isPromotionProduct(product.getId());
					discount = productPromotionService.getPromoteDiscount(product.getId());
					vo.setIsPromotion(isPromotion);
					vo.setDiscount(discount);
					products.add(vo);
				}
			} else {
				for (LunceneProduct product : productList) {
					vo = new LunceneProductVO(product, categoryService.getShowCategoryName(product.getShowCategoryId()));
					vo.setIsAddToWishList(isAddToWishList(wishListProductIds, product.getId()));
					isPromotion = productPromotionService.isPromotionProduct(product.getId());
					discount = productPromotionService.getPromoteDiscount(product.getId());
					vo.setIsPromotion(isPromotion);
					vo.setDiscount(discount);
					products.add(vo);
				}
			}
		} else {
			Integer qty;
			if (category != null) {
				for (LunceneProduct product : productList) {
					qty = shoppingCartProductMap.get(product.getId());
					vo = new LunceneProductVO(product, category.getName());
					if (qty != null) {
						vo.setProductQtyInShoppingCart(qty.intValue());
					}
					vo.setIsAddToWishList(isAddToWishList(wishListProductIds, product.getId()));
					isPromotion = productPromotionService.isPromotionProduct(product.getId());
					discount = productPromotionService.getPromoteDiscount(product.getId());
					vo.setIsPromotion(isPromotion);
					vo.setDiscount(discount);
					products.add(vo);
				}
			} else {
				for (LunceneProduct product : productList) {
					qty = shoppingCartProductMap.get(product.getId());
					vo = new LunceneProductVO(product, categoryService.getShowCategoryName(product.getShowCategoryId()));
					if (qty != null) {
						vo.setProductQtyInShoppingCart(qty.intValue());
					}
					vo.setIsAddToWishList(isAddToWishList(wishListProductIds, product.getId()));
					isPromotion = productPromotionService.isPromotionProduct(product.getId());
					discount = productPromotionService.getPromoteDiscount(product.getId());
					vo.setIsPromotion(isPromotion);
					vo.setDiscount(discount);
					products.add(vo);
				}
			}
		}
		return products;
	}

}

package com.itecheasy.ph3.web.tag;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.itecheasy.ph3.common.DeployProperties;
import com.itecheasy.ph3.web.utils.StrUtils;

public class UrlFunction {
	public static final String PAGE_INDEX_FORMAT = "{pageIndex}";
	public static final String SHOW_MODE_FORMAT = "{showMode}";
	public static final String SORT_INDEX_FORMAT = "{sortIndex}";
	public static final String CATEGORY_FORMAT = "{categoryId}";

	public static String getContextPath() {
		HttpServletRequest hsr = null;
		try{
			hsr = ServletActionContext.getRequest();
		}catch(Exception e){}
		return getContextPath(hsr);
	}
	
	public static String getContextPath(HttpServletRequest request) {
		if(request != null){
			return request.getScheme() + "://" + request.getHeader("host") + request.getContextPath();
		}else{
			return DeployProperties.getInstance().getProperty("ph3.site.url");
		}
		
	}

	public static String filterUrl(String url) {
		return StrUtils.replaceUrl(url);
	}

	/**
	 * 帮助文章详细URL
	 */
	public static String getHelpArticleDatails(int articleId,
			int helpCategoryId, String helpCategoryName) {
		StringBuilder url = new StringBuilder(getContextPath());
		url.append("/Help/").append(helpCategoryId).append("-").append(
				StrUtils.replaceUrl(helpCategoryName)).append("/HelpDetail-")
				.append(articleId).append(".html");
		return url.toString();
	}

	private static String getHelpArticleListFormat(int helpCategoryId,
			String helpCategoryName, String pageIndex) {
		StringBuilder url = new StringBuilder(getContextPath());
		url.append("/Help/").append(helpCategoryId).append("-").append(
				StrUtils.replaceUrl(helpCategoryName)).append("/HelpList-")
				.append(pageIndex).append(".html");
		return url.toString();
	}

	/**
	 * 帮助文章列表URL
	 */
	public static String getHelpArticleList(int helpCategoryId,
			String helpCategoryName, int pageIndex) {
		return getHelpArticleListFormat(helpCategoryId, helpCategoryName,
				String.valueOf(pageIndex));
	}

	/**
	 * 帮助文章列表，分页格式化URL
	 */
	public static String getHelpArticleListPagerFormat(int helpCategoryId,
			String helpCategoryName) {
		return getHelpArticleListFormat(helpCategoryId, helpCategoryName,
				PAGE_INDEX_FORMAT);
	}

	/**
	 * 分页格式化URL
	 */
	public static String getHelpPopularQuestionsPagerFormat() {
		return getContextPath()
				+ "/Help/Most-Popular-Questions.html?currentPage="
				+ PAGE_INDEX_FORMAT;
	}

	/**
	 * 展示类别URL
	 */
	public static String getShowCategoryList(int categoryId, String categoryName) {
		StringBuilder url = new StringBuilder(getContextPath());
		url.append("/beads-sellers/").append(categoryId).append("-").append(
				StrUtils.replaceUrl(categoryName)).append(".html");
		return url.toString();
	}

	private static String getCategoryProductsFormat(int categoryId,
			String categoryName, String pageIndex, String showMode) {
		StringBuilder url = new StringBuilder(getContextPath());
		url.append("/sale-beads/").append(showMode).append("-").append(
				categoryId).append("-").append(pageIndex).append("/").append(
				StrUtils.replaceUrl(categoryName)).append(".html");
		return url.toString();
	}

	/**
	 * 商品展示URL
	 */
	public static String getCategoryProducts(int categoryId, String categoryName) {
		return getCategoryProductsFormat(categoryId, categoryName, "1", "1");
	}

	/**
	 * 商品展示URL
	 */
	public static String getCategoryProducts(int categoryId,
			String categoryName, int pageIndex, int showMode) {
		return getCategoryProductsFormat(categoryId, categoryName, String
				.valueOf(pageIndex), String.valueOf(showMode));
	}

	/**
	 * 商品展示分页格式化URL
	 */
	public static String getCategoryProductsPagerFormat(int categoryId,
			String categoryName, int showMode) {
		return getCategoryProductsFormat(categoryId, categoryName,
				PAGE_INDEX_FORMAT, String.valueOf(showMode));
	}

	/**
	 * 商品展示显示格式化URL
	 */
	public static String getCategoryProductsShowModeFormat(int categoryId,
			String categoryName, int pageIndex) {
		return getCategoryProductsFormat(categoryId, categoryName, String
				.valueOf(pageIndex), SHOW_MODE_FORMAT);
	}

	/**
	 * 商品详细URL
	 */
	public static String getProductDetail(int productId, String productName,
			String categoryName) {
		// 商品名称（第二个逗号之前的文字）
		productName = StrUtils.getFrontStringOfChar(productName, ",", 2);
		if (categoryName == null || categoryName.length() == 0) {
			categoryName = "category";
		} else {
			categoryName = StrUtils.replaceUrl(categoryName);
		}
		StringBuilder url = new StringBuilder(getContextPath());
		url.append("/on-sale/").append(categoryName).append("/").append(
				productId).append("-").append(StrUtils.replaceUrl(productName))
				.append(".html");
		return url.toString();
	}

	private static String getProductSearchFormat(String keyword,
			String categoryId, String pageIndex, String showMode,
			String sortIndex) {
		StringBuilder url = new StringBuilder(getContextPath());
		url.append("/ProductSearch?keyword=");
		try {
			url.append(URLEncoder.encode(keyword, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
		}
		if (categoryId != null) {
			url.append("&categoryId=").append(categoryId);
		}
		if (pageIndex != null && !pageIndex.equals("1")) {
			url.append("&currentPage=").append(pageIndex);
		}
		if (showMode != null && !showMode.equals("1")) {
			url.append("&showMode=").append(showMode);
		}
		if (sortIndex != null && !sortIndex.equals("1")) {
			url.append("&sortIndex=").append(sortIndex);
		}
		return url.toString();
	}

	public static String getProductSearch(String keyword) {
		StringBuilder url = new StringBuilder(getContextPath());
		url.append("/ProductSearch?keyword=");
		try {
			url.append(URLEncoder.encode(keyword, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
		}
		return url.toString();
	}
	
	public static String addParam(String url,String key,String value){
		  if( url.indexOf("?") > -1)
		  {
			  return url + "&" + key + "=" + value;
		  }
		  else {
			  return url + "?" + key + "=" + value;
		}
	}

	public static String getProductSearch(String keyword, Integer categoryId,
			int pageIndex, int showMode, int sortIndex) {
		String cId = null;
		if (categoryId != null && categoryId > 0) {
			cId = categoryId.toString();
		}
		return getProductSearchFormat(keyword, cId, String.valueOf(pageIndex),
				String.valueOf(showMode), String.valueOf(sortIndex));
	}

	public static String getProductSearchPagerFormat(String keyword,
			Integer categoryId, int showMode, int sortIndex) {
		String cId = null;
		if (categoryId != null && categoryId > 0) {
			cId = categoryId.toString();
		}
		return getProductSearchFormat(keyword, cId, PAGE_INDEX_FORMAT, String
				.valueOf(showMode), String.valueOf(sortIndex));
	}

	public static String getProductSearchShowModeFormat(String keyword,
			Integer categoryId, int pageIndex, int sortIndex) {
		String cId = null;
		if (categoryId != null && categoryId > 0) {
			cId = categoryId.toString();
		}
		return getProductSearchFormat(keyword, cId, String.valueOf(pageIndex),
				SHOW_MODE_FORMAT, String.valueOf(sortIndex));
	}

	public static String getProductSearchSorterFormat(String keyword,
			Integer categoryId, int pageIndex, int showMode) {
		String cId = null;
		if (categoryId != null && categoryId > 0) {
			cId = categoryId.toString();
		}
		return getProductSearchFormat(keyword, cId, String.valueOf(pageIndex),
				String.valueOf(showMode), SORT_INDEX_FORMAT);
	}

	public static String getProductSearchCategoryFormat(String keyword,
			int pageIndex, int showMode, int sortIndex) {
		return getProductSearchFormat(keyword, CATEGORY_FORMAT, String
				.valueOf(pageIndex), String.valueOf(showMode), String
				.valueOf(sortIndex));
	}

	public static String getProductSearchCategoryUrl(String formatUrl,
			int categoryId) {
		return formatUrl.replace(CATEGORY_FORMAT, String.valueOf(categoryId));
	}

}

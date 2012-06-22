package com.itecheasy.ph3.web.buyer;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;

import com.itecheasy.ph3.category.CategoryService;
import com.itecheasy.ph3.category.ShowCategory;
import com.itecheasy.ph3.common.MemCachedUtils;
import com.itecheasy.ph3.web.BuyerBaseAction;
import com.itecheasy.ph3.web.Link;
import com.itecheasy.ph3.web.exception.AppException;
import com.itecheasy.ph3.web.tag.UrlFunction;
import com.itecheasy.ph3.web.utils.FileUtils;
import com.itecheasy.ph3.web.utils.UrlHelper;
import com.itecheasy.ph3.web.utils.WebUtils;
import com.itecheasy.ph3.web.vo.ShowCategoryTree;

public class BuyerCategoryAction extends BuyerBaseAction {
	private static final long serialVersionUID = 660101218L;
	private static final String PAGE_HEAD_BEADS_CATALOG = "Beads_Catalog";
	/**
	 * 类别展示
	 * 
	 * @return
	 */
	public String doCategoryHome() throws AppException {
		
		Integer id = paramInt("id", 0);
		String categoryHomeString = "";
		ShowCategory category = categoryService.getShowCategory(id);
		if (category == null) {
			throw new AppException("Can not find the category.");
		} else if (!category.getIsVisible()) {
			throw new AppException("Can not find the category.");
		}
		String rawUrl = UrlHelper.getRawUrl(request);
		String queryString = UrlHelper.getQueryString(rawUrl);
		String createUrl = UrlFunction.getShowCategoryList(category.getId(), getAlias(category));
		createUrl = queryString == null ? createUrl : createUrl + "?" + queryString;
		
		//重定向以前为存在参数时也转跳，修改为有参数不转跳修。modified :lihua 2011-08-24
		if(!rawUrl.equalsIgnoreCase(createUrl)){
			redirect301(createUrl);
			return SUCCESS;
		}
		
		seo = seoService.getCategoryShowSeo(id, getAlias(category), 
				category.getParent()!= null ?getAlias(category.getParent())	:null);
		List<Link> navs = WebUtils.getCategoryNavLinks(category, false);

		String categoryTemplate = getCategoryTemplate(request, id);
		if (categoryTemplate != null && !categoryTemplate.trim().isEmpty()) {
			categoryHomeString = readAndReplaceCategoryTemple(categoryTemplate,
					category);
		}
		request.setAttribute("navLinks", navs);
		request.setAttribute("categoryHome", categoryHomeString);
		setCurrentCategory(category);
		return SUCCESS;
	}
	
	public String doBeadsCatalog(){
		/*List<ShowCategoryTree> categoryTrees = getCategoryTreeList();
		Map<String,Map<ShowCategory,String>> map  = new HashMap<String,Map<ShowCategory,String>>();
		for(Iterator it = categoryTrees.iterator();it.hasNext();){
			ShowCategoryTree sct1 = (ShowCategoryTree)it.next();
			String topCateName = sct1.getCategory().getName();
			Map<ShowCategory,String> map1 = new LinkedHashMap <ShowCategory,String>();
			if(sct1.getSubCategories() != null){
				for(Iterator ite =sct1.getSubCategories().iterator();ite.hasNext();){
					ShowCategoryTree sct2 = (ShowCategoryTree)ite.next();
					String twoCateName = sct2.getCategory().getName();
					//"1":商品类别
					if(sct2.getCategory().getCategoryType() == 1){
						map1.put(sct2.getCategory(), getImageUrl(sct2.getCategory().getImageName(),130,130));
					}else{
						Map<ShowCategory,String> map2 = new LinkedHashMap<ShowCategory,String>();
						if(sct2.getSubCategories() != null){
							for(Iterator iter = sct2.getSubCategories().iterator();iter.hasNext();){
								ShowCategoryTree sct3 = (ShowCategoryTree)iter.next();
								map2.put(sct3.getCategory(), getImageUrl(sct3.getCategory().getImageName(),130,130));
							}
						}
						map.put(twoCateName, map2);
					}
				}
			}
			map.put(topCateName, map1);
		}
		request.setAttribute("categoryTreeList", categoryTrees);
		request.setAttribute("map", map);*/
		request.setAttribute("homeType", PAGE_HEAD_BEADS_CATALOG);
		return SUCCESS;
	}

	private static String getCategoryUrl(ShowCategory category) {
		return UrlFunction.getShowCategoryList(category.getId(), category
				.getName());
	}

	private static String getCategoryTemplate(HttpServletRequest request,
			Integer categoryId) {
		MemCachedUtils memCached = MemCachedUtils.getInstance();
		String cacheKey = "CATEGORY_TEMPLATE_" + categoryId.toString();
		String fileContent = (String) memCached.get(cacheKey);
		if (fileContent == null) {
			String tempPath = request.getSession().getServletContext()
					.getRealPath("buyer\\categorytemplate");
			String strFileName = tempPath + "\\" + categoryId + ".html";
			File sourceFile = new File(strFileName);
			if (sourceFile.exists()) {
				fileContent = FileUtils.readFile(strFileName.toString(),
						FileUtils.UTF_8);
			} else {
				strFileName = tempPath + "\\default.html";
				sourceFile = new File(strFileName);
				if (sourceFile.exists()) {
					fileContent = FileUtils.readFile(sourceFile.toString(),
							FileUtils.UTF_8);
				}
			}
			memCached.set(cacheKey, fileContent);
		}
		return fileContent;
	}

	private String readAndReplaceCategoryTemple(String fileContent,
			ShowCategory category) {

		// 读出模板的文件
		String strTemplate = fileContent;
		strTemplate = strTemplate.replace("<!-- CategoryName -->", category
				.getName());
		strTemplate = strTemplate.replace("<!-- CategoryDescription -->",
				category.getDescription());

		// 用正则匹配出样式数组
		String styleString = getRegValue(strTemplate,
				"<!-- Repeater -->(.*?)<!-- Repeater -->");
		if (styleString != null && !styleString.isEmpty()) {

			List<ShowCategory> subCategories = categoryService
					.getVisibleSubShowCategories(category.getId());
			if (subCategories != null && !subCategories.isEmpty()) {
				StringBuilder strSubCategory = new StringBuilder();
				StringBuilder strLink;
				StringBuilder strImage;
				String strHref;
				String strName;
				String strDesc;
				String strRow;
				ShowCategory showCategory;
				// String categoryPhotoPath = WebConfig.get("category_image_show_path");
				String photoFormat = "<p class=\"ItemPhoto\"><a title=\"%1$s\" href=\"%2$s\"><img border=\"0\" src=\"%3$s\"/></a></p>";
				String titleFormat = "<p class=\"ItemName\"><a title=\"%1$s\" href=\"%2$s\">%1$s</a></p>";

				for (int i = 0; i < subCategories.size(); i++) {
					showCategory = subCategories.get(i);
					strLink = new StringBuilder();
					strImage = new StringBuilder();
					strName = StringEscapeUtils.escapeHtml(showCategory
							.getName());
					strDesc = StringEscapeUtils.escapeHtml(showCategory
							.getDescription());
					strRow = styleString;

					// 商品类别
					if (showCategory.getCategoryType().equals(
							CategoryService.CATEGORY_TYPE_PRODUCT)) {
						strHref = UrlFunction.getCategoryProducts(showCategory
								.getId(), showCategory.getName(), 1,
								showCategory.getShowMode());
					} else {
						strHref = getCategoryUrl(showCategory);
					}

					String imgName = getImageUrl(showCategory.getImageName(),
							130, 130);
					strImage.append(String.format(photoFormat, strName,
							strHref, imgName));
					strLink
							.append(String
									.format(titleFormat, strName, strHref));

					strRow = strRow.replace("<!-- Name -->", strName);
					strRow = strRow.replace("<!-- Description -->", strDesc);
					strRow = strRow.replace("<!-- ImageUrl -->", strImage
							.toString());
					strRow = strRow
							.replace("<!-- Link -->", strLink.toString());

					if ((i + 1) % 5 == 0) {
						strRow = strRow.replace("[${name}]", "LastItem");
					} else {
						strRow = strRow.replace("[${name}]", "");
					}
					strSubCategory.append(strRow);
				}
				strTemplate = strTemplate.replace(styleString, strSubCategory
						.toString());
			}
		}
		return strTemplate;
	}

	// / <summary>
	// / 获取样式字符串数组
	// / </summary>
	private static String getRegValue(String HtmlCode, String RegexString) {
		Pattern p = Pattern.compile(RegexString);
		Matcher m = p.matcher(HtmlCode);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}
}

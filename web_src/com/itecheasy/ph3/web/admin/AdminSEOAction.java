package com.itecheasy.ph3.web.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.itecheasy.ph3.category.CategoryService;
import com.itecheasy.ph3.category.ShowCategory;
import com.itecheasy.ph3.seo.SeoService;
import com.itecheasy.ph3.seo.SeoTemplet;
import com.itecheasy.ph3.seo.SeoTemplet.SeoTempletType;
import com.itecheasy.ph3.web.AdminBaseAction;
import com.itecheasy.ph3.web.SessionContext;

public class AdminSEOAction extends AdminBaseAction {
	private CategoryService categoryService;
	private Integer categoryId;
	private String categoryAlias;
	private Integer subCategoryId;
	private SeoTemplet categoryShowSeo;
	private SeoTemplet categoryProductListSeo;
	private SeoTemplet productDetailSeo;
	private SeoService seoService;
	/**
	 * 所有展示类别
	 * 
	 * @return
	 */
	public String browseCategoryList() {
		List<ShowCategory> rootList = categoryService.getRootShowCategories();
		List<ShowCategory> subList = null;
		List<ShowCategory> categoryList = null;
		request.setAttribute("rootList", rootList);
		if (isIntegerEmpty(categoryId) && isIntegerEmpty(subCategoryId)) {
			request.setAttribute("categoryList", rootList);
			return SUCCESS;
		} else if (isNotIntegerEmpty(categoryId) && isIntegerEmpty(subCategoryId)) {
			categoryList = categoryService.getSubShowCategories(categoryId);
			subList = categoryService.getSubShowCategoriesOfNotProductType(categoryId);
		} else {
			categoryList = categoryService.getSubShowCategories(subCategoryId);
			subList = categoryService.getSubShowCategoriesOfNotProductType(categoryId);
		}
		request.setAttribute("categoryList", categoryList);
		request.setAttribute("subList", subList);
		String url = (String)(request.getSession().getAttribute("prePage"));
		if(url != null){
			request.getSession().removeAttribute("prePage");
		}
		return SUCCESS;
	}
	
	
	public String SEOEdit(){
		ShowCategory showCategory;
		showCategory = categoryService.getShowCategory(categoryId);
		request.setAttribute("showCategory", showCategory);
		categoryShowSeo = seoService.getSeoTempletInfo(SeoTempletType.CATEGORY_SHOW, categoryId);
		categoryProductListSeo = seoService.getSeoTempletInfo(SeoTempletType.CATEGORY_PRODUCT_LIST, categoryId);
		productDetailSeo = seoService.getSeoTempletInfo(SeoTempletType.PRODUCT_DETAIL, categoryId);
		String url = (String)(request.getSession().getAttribute("prePage"));
		if(url == null){
			request.getSession().setAttribute("prePage", request.getHeader("Referer"));
		}
		return SUCCESS;
	}
	
	public String updateSeo(){
		categoryService.setShowCategoryAlias(categoryId, categoryAlias);
		
		if(categoryShowSeo != null){
			categoryShowSeo.setType(SeoTempletType.CATEGORY_SHOW);
			categoryShowSeo.setKey(categoryId);
			seoService.setSeoTempletInfo(categoryShowSeo);
		}
		if(categoryProductListSeo != null){
		categoryProductListSeo.setType(SeoTempletType.CATEGORY_PRODUCT_LIST);
		categoryProductListSeo.setKey(categoryId);
		seoService.setSeoTempletInfo(categoryProductListSeo);
		}
		if(productDetailSeo != null){
			productDetailSeo.setType(SeoTempletType.PRODUCT_DETAIL);
			productDetailSeo.setKey(categoryId);
			seoService.setSeoTempletInfo(productDetailSeo);
		}
		try {
			String url = ((String)(request.getSession().getAttribute("prePage")));
			request.getSession().removeAttribute("prePage");
			response.sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	private Boolean isIntegerEmpty(Integer mumber) {
		return mumber == null || mumber <= 0;
	}

	private Boolean isNotIntegerEmpty(Integer mumber) {
		return !isIntegerEmpty(mumber);
	}
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	public Integer getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	public Integer getSubCategoryId() {
		return subCategoryId;
	}
	public void setSubCategoryId(Integer subCategoryId) {
		this.subCategoryId = subCategoryId;
	}


	public SeoTemplet getCategoryShowSeo() {
		return categoryShowSeo;
	}


	public void setCategoryShowSeo(SeoTemplet categoryShowSeo) {
		this.categoryShowSeo = categoryShowSeo;
	}


	public SeoTemplet getCategoryProductListSeo() {
		return categoryProductListSeo;
	}


	public void setCategoryProductListSeo(SeoTemplet categoryProductListSeo) {
		this.categoryProductListSeo = categoryProductListSeo;
	}


	public SeoTemplet getProductDetailSeo() {
		return productDetailSeo;
	}


	public void setProductDetailSeo(SeoTemplet productDetailSeo) {
		this.productDetailSeo = productDetailSeo;
	}


	public void setSeoService(SeoService seoService) {
		this.seoService = seoService;
	}


	public void setCategoryAlias(String categoryAlias) {
		this.categoryAlias = categoryAlias;
	}
	
	
	
}

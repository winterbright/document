package com.itecheasy.ph3.web.admin;

import java.util.List;

import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.help.HelpArticle;
import com.itecheasy.ph3.help.HelpArticleService;
import com.itecheasy.ph3.help.HelpCategory;
import com.itecheasy.ph3.help.HelpCategoryService;
import com.itecheasy.ph3.web.AdminBaseAction;
import com.itecheasy.ph3.web.exception.AppException;
import com.itecheasy.ph3.web.utils.StrUtils;

public class AdminHelpAction extends AdminBaseAction {
	private static final long serialVersionUID = 1L;
	private HelpCategoryService helpCategoryService;
	private HelpArticleService helpArticleService;

	private int categoryId;

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public void setHelpArticleService(HelpArticleService helpArticleService) {
		this.helpArticleService = helpArticleService;
	}

	public void setHelpCategoryService(HelpCategoryService helpCategoryService) {
		this.helpCategoryService = helpCategoryService;
	}

	/**
	 * 帮助类别列表
	 */
	public String doHelpCategory() {
		List<HelpCategory> list = helpCategoryService.getAllHelpCategorys();
		for (HelpCategory category : list) {
			category.setArticlesCount(helpCategoryService
					.getAllArticleCountByCategory(category.getId()));
		}
		getRequest().setAttribute("helpCategoryList", list);
		return SUCCESS;
	}

	/**
	 * 帮助类别信息
	 */
	public String doHelpCategoryDetails() {
		Integer categoryId = paramInt("id", 0);
		HelpCategory category = null;
		if (categoryId > 0) {
			// edit
			category = helpCategoryService.getHelpCategory(categoryId);
		} else {
			// add
		}
		getRequest().setAttribute("helpCategroy", category);
		return SUCCESS;
	}

	/**
	 * 保存帮助类别信息
	 * 
	 * @return
	 */
	public String saveHelpCategory() {
		Integer categoryId = paramInt("categoryId", 0);
		String name = param("categoryName");
		String keyword = param("keyword");
		if (keyword.length() > 400) {
			keyword = keyword.substring(0, 400);
		}
		Boolean isVisible = ("1".equals(param("status")));
		HelpCategory category;
		if (categoryId > 0) {
			// update
			category = helpCategoryService.getHelpCategory(categoryId);
			category.setName(name);
			category.setKeyword(keyword);
			category.setIsVisible(isVisible);
			helpCategoryService.updateHelpCategory(category);
		} else {
			// add
			category = new HelpCategory();
			category.setName(name);
			category.setKeyword(keyword);
			category.setIsVisible(isVisible);
			categoryId = helpCategoryService.addHelpCategory(category);
		}
		return SUCCESS;
	}

	/**
	 * 删除帮助类别
	 * 
	 * @return
	 */
	public String deleteHelpCategory() {
		Integer categoryId = paramInt("id", 0);
		if (categoryId > 0) {
			try {
				if (helpCategoryService.getHelpCategory(categoryId) != null) {
					helpCategoryService.deleteHelpCategory(categoryId);
				}
			} catch (BussinessException e) {
				if (HelpCategoryService.ERROR_HELP_CATEGORY_HAVE_ARTICLES
						.equals(e.getErrorMessage())) {
					setMessageInfo("1");
					return ERROR;
				}
			}
		}
		return SUCCESS;
	}

	/**
	 * 排序帮助类别
	 * 
	 * @return
	 */
	public String orderHelpCategory() throws AppException {
		Integer categoryId = paramInt("id", 0);
		Integer orderIndex = paramInt("orderIndex", 0);
		if (categoryId > 0) {
			if (helpCategoryService.getHelpCategory(categoryId) == null) {
				throw new AppException("帮助类别不存在！");
			}
			helpCategoryService.setHelpCategoryOrder(categoryId, orderIndex);
		}
		return SUCCESS;
	}

	/**
	 * 帮助文章列表
	 * 
	 * @return
	 */
	public String doHelpArticles() {
		Integer categoryId = paramInt("categoryId", 0);
		List<HelpArticle> list = null;
		if (categoryId > 0) {
			list = helpArticleService.getAllHelpArticlesByCategory(categoryId);
		}
		getRequest().setAttribute("helpArticleList", list);
		setHelpArticleListAttribute(helpCategoryService.getAllHelpCategorys(),
				categoryId);
		return SUCCESS;
	}

	/**
	 * 帮助文章信息
	 * 
	 * @return
	 */
	public String doHelpArticleDetails() {
		Integer categoryId = paramInt("categoryId", 0);
		Integer articleId = paramInt("id", 0);
		HelpArticle article = null;
		if (articleId > 0) {
			article = helpArticleService.getHelpArticle(articleId);
		}
		getRequest().setAttribute("helpArticle", article);
		setHelpArticleListAttribute(helpCategoryService.getAllHelpCategorys(),
				categoryId);
		return SUCCESS;
	}

	/**
	 * 删除帮助文章
	 * 
	 * @return
	 */
	public String deleteHelpArticle() {
		Integer articleId = paramInt("id", 0);
		if (articleId > 0) {
			if (helpArticleService.getHelpArticle(articleId) != null) {
				helpArticleService.deleteHelpArticle(articleId);
			}
		}
		return SUCCESS;
	}

	/**
	 * 排序帮助文章
	 * 
	 * @return
	 */
	public String orderHelpArticle() throws AppException {
		Integer articleId = paramInt("id", 0);
		Integer orderIndex = paramInt("orderIndex", 0);
		if (articleId > 0) {
			if (helpArticleService.getHelpArticle(articleId) == null) {
				throw new AppException("帮助文章不存在！");
			}
			helpArticleService.setHelpArticleOrder(articleId, orderIndex);
		}
		return SUCCESS;
	}

	/**
	 * 保存帮助文章
	 */
	public String saveHelpArticle() throws AppException {
		Integer articleId = paramInt("articleId", 0);
		Integer categoryId = paramInt("newCategoryId", 0);
		String name = param("articleName");
		String keyword = param("keyword");
		String content = param("content");
		Boolean isVisible = ("1".equals(param("status")));
		if (keyword.length() > 400) {
			keyword = keyword.substring(0, 400);
		}
		if (categoryId < 1) {
			throw new AppException("没选择帮助类别！");
		}
		HelpCategory category = helpCategoryService.getHelpCategory(categoryId);
		if (category == null) {
			throw new AppException("帮助类别不存在！");
		}
		if (content != null) {
			content = StrUtils.filterHtmlNote(content);
			content = StrUtils.filterHtmlScript(content);
		}
		HelpArticle article;
		if (articleId > 0) {
			// update
			article = helpArticleService.getHelpArticle(articleId);
			article.getHelpCategory().setId(categoryId);
			article.setName(name);
			article.setKeyword(keyword);
			article.setContent(content);
			article.setIsVisible(isVisible);
			helpArticleService.updateHelpArticle(article);
		} else {
			// add
			article = new HelpArticle();
			article.setHelpCategory(category);
			article.setName(name);
			article.setKeyword(keyword);
			article.setContent(content);
			article.setIsVisible(isVisible);
			articleId = helpArticleService.addHelpArticle(article);
		}
		setCategoryId(categoryId);
		return SUCCESS;
	}

	private void setHelpArticleListAttribute(List<HelpCategory> list,
			int selectedCategoryId) {
		getRequest().setAttribute("helpCategoryList", list);
		getRequest().setAttribute("selectedCategoryId", selectedCategoryId);
	}

}

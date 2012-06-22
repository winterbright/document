package com.itecheasy.ph3.web.buyer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.itecheasy.common.PageList;
import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.email.EmailService;
import com.itecheasy.ph3.help.HelpArticle;
import com.itecheasy.ph3.help.HelpArticleService;
import com.itecheasy.ph3.help.HelpCategory;
import com.itecheasy.ph3.help.HelpCategoryService;
import com.itecheasy.ph3.ticket.TicketService;
import com.itecheasy.ph3.web.BuyerBaseAction;
import com.itecheasy.ph3.web.DateConverter;
import com.itecheasy.ph3.web.exception.AppException;
import com.itecheasy.ph3.web.tag.FuncitonUtils;
import com.itecheasy.ph3.web.utils.StrUtils;

public class BuyerHelpAction extends BuyerBaseAction {
	private static final long serialVersionUID = 6622225455L;
	private static final int PAGE_SIZE = 20;
	private HelpCategoryService helpCategoryService;
	private HelpArticleService helpArticleService;
	private TicketService ticketService;
	private EmailService emailService;
	private Date date;
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	public void setTicketService(TicketService ticketService) {
		this.ticketService = ticketService;
	}

	public void setHelpArticleService(HelpArticleService helpArticleService) {
		this.helpArticleService = helpArticleService;
	}

	public void setHelpCategoryService(HelpCategoryService helpCategoryService) {
		this.helpCategoryService = helpCategoryService;
	}

	/**
	 * 帮助首页
	 */
	public String doHelpCenter() {
		List<HelpCategory> helpCategoryList = helpCategoryService
				.getVisibleHelpCategorys();
		for (HelpCategory category : helpCategoryList) {
			PageList<HelpArticle> pageList = helpArticleService
					.searchVisibleHelpArticlesByCategory(1, 7, category.getId());
			category.setHelpArticles(pageList.getData());
		}
		setHelpCategoryListAttribute(0, helpCategoryList);
		setHotHelpArticleListAttribute();

		return SUCCESS;
	}

	/**
	 * 帮助文章列表
	 */
	public String doHelpArticles() throws AppException {
		Integer helpCategoryId = paramInt("id", 0);
		if (helpCategoryId < 1) {
			return ERROR;
		}

		HelpCategory category = helpCategoryService
				.getHelpCategory(helpCategoryId);
		if (category == null) {
			throw new AppException("Can't find the help category(id:"
					+ helpCategoryId + ").");
		} else if (!category.getIsVisible()) {
			throw new AppException("The category(id:" + helpCategoryId
					+ ") is not visible.");
		}

		PageList<HelpArticle> pageList = helpArticleService
				.searchVisibleHelpArticlesByCategory(this.getCurrentPage(),
						PAGE_SIZE, helpCategoryId);
		setHelpArticlesAttribute(pageList, category.getName(), false);
		setHelpCategoryListAttribute(helpCategoryId);
		setHotHelpArticleListAttribute();
		return SUCCESS;
	}

	/**
	 * 热门帮助文章列表
	 */
	public String doHotHelpArticles() {
		PageList<HelpArticle> pageList = helpArticleService
				.searchHotHelpArticles(this.getCurrentPage(), PAGE_SIZE);
		setHelpArticlesAttribute(pageList, "Most Popular Questions", true);
		setHelpCategoryListAttribute(0);
		setHotHelpArticleListAttribute();
		getRequest().setAttribute("isPopularQuestions", true);
		return SUCCESS;
	}

	/**
	 * 搜索帮助文章
	 */
	public String doSearchHelpArticles() {
		PageList<HelpArticle> pageList;
		String keyword = param("keyword");
		if (keyword != null && keyword.trim().length() > 0) {
			pageList = helpArticleService.searchHelpArticles(this
					.getCurrentPage(), PAGE_SIZE, keyword.trim());
		} else {
			pageList = new PageList<HelpArticle>();
			pageList.setData(new ArrayList<HelpArticle>());
		}
		getRequest().setAttribute("helpKeyword", keyword);
		getRequest().setAttribute("helpArticlePageList", pageList);
		setHelpCategoryListAttribute(0);
		setHotHelpArticleListAttribute();
		return SUCCESS;
	}

	/**
	 * 帮助详细
	 */
	public String doHelpArticleDatails() throws AppException {
		Integer helpArticleId = paramInt("id", 0);
		if (helpArticleId < 1) {
			throw new AppException("Can't find the help article.");
		}
		HelpArticle helpArticle = helpArticleService
				.getHelpArticle(helpArticleId);
		if (helpArticle == null) {
			throw new AppException("Can't find the help article.");
		} else if (!helpArticle.getIsVisible()) {
			throw new AppException("The help article is not visible.");
		} else if (!helpArticle.getHelpCategory().getIsVisible()) {
			throw new AppException(
					"The help category of this help article is not visible.");
		}
		Integer categoryId = helpArticle.getHelpCategory().getId();
		List<HelpArticle> recommendedHelpArticeles = helpArticleService
				.getOthersRecommendedHelpArticeles(categoryId, helpArticleId, 6);
		getRequest().setAttribute("helpArticle", helpArticle);
		getRequest()
				.setAttribute("helpCategory", helpArticle.getHelpCategory());
		getRequest().setAttribute("recommendedArticeleList",
				recommendedHelpArticeles);
		setHelpCategoryListAttribute(categoryId);
		setHotHelpArticleListAttribute();
		helpArticleService.clickHelpArticle(helpArticleId);
		return SUCCESS;
	}

	/**
	 * 评价文章
	 */
	public void evaluateHelpArticle() {
		Integer helpArticleId = paramInt("articleId");
		Boolean isGood = paramBool("isGood");
		if (helpArticleId == null || isGood == null) {
			return;
		}
		helpArticleService.evaluateHelpArticle(helpArticleId, isGood);
	}

	private void setHelpArticlesAttribute(PageList<HelpArticle> pageList,
			String helpCategoryName, boolean isHotHelpArticles) {
		getRequest().setAttribute("helpArticlePageList", pageList);
		getRequest().setAttribute("helpCategoryName", helpCategoryName);
		getRequest().setAttribute("isHotHelpArticles", isHotHelpArticles);
	}

	private void setHelpCategoryListAttribute(int currentHelpCategoryId) {
		List<HelpCategory> helpCategoryList = helpCategoryService
				.getVisibleHelpCategorys();
		getRequest().setAttribute("helpCategoryList", helpCategoryList);
		getRequest().setAttribute("currentHelpCategoryId",
				currentHelpCategoryId);
	}

	private void setHelpCategoryListAttribute(int currentHelpCategoryId,
			List<HelpCategory> helpCategoryList) {
		getRequest().setAttribute("helpCategoryList", helpCategoryList);
		getRequest().setAttribute("currentHelpCategoryId",
				currentHelpCategoryId);
	}

	private void setHotHelpArticleListAttribute() {
		List<HelpArticle> hotHelpArticleList = helpArticleService
				.searchHotHelpArticles(1, 6).getData();
		getRequest().setAttribute("hotHelpArticleList", hotHelpArticleList);
	}
	/**
	 * 去提交问题页面
	 * @return
	 */
	public String doAskQuestion(){
		//得到左边帮助类别列表数据
		List<HelpCategory> helpCategoryList = helpCategoryService
		.getVisibleHelpCategorys();
		setHelpCategoryListAttribute(0, helpCategoryList);
		setHotHelpArticleListAttribute();
		date = new Date();
		if(getLoginedUserBuyer() != null)
			request.setAttribute("user",getLoginedUserBuyer());
		request.setAttribute("asksign",false);
		return SUCCESS;
	}
	/**
	 * 处理提交问题
	 * @return
	 */
	public String askQuestion(){
		String name = param("name");
		String email = param("email");
		String content=param("content");
		String ip = request.getRemoteAddr();
		date= new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		Integer userId = null;
		if(getLoginedUserBuyer() != null){
			userId = getLoginedUserBuyer().getId();
		}
		//内容转译
		content =StrUtils.htmlConvert(content);
		if(!StringUtils.isEmpty(name)&&!StringUtils.isEmpty(email)&&!StringUtils.isEmpty(content)){
			//添加提问
			ticketService.submitQuestion(ip, email, content, name, userId);
			//发送邮件
			String emailContent = "<table width=\"644\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:3px solid #ddd; font-family:Arial, Helvetica, sans-serif;font-size:12px;color:#333;\">"+
					   "<tr>"+
					   "<td style=\"border-bottom:2px solid #3A5799;padding:14px 14px 10px 14px;font:bold 30px Georgia;margin:0;\">Online Question </td>"+
					   "</tr>"+
					   "<tr>"+
					   "<td style=\"padding:27px 36px 200px 36px;line-height:24px;\">"+
					   "<p>Email:"+email+"</p>"+
					   "<p>User Name:"+name+"</p>"+
					   "<p>提交时间:"+sdf.format(date)+"</p>"+
					   "<p>"+content+"</p>"+
					   "</td></tr></table>"	;
			emailService.sendEmail(emailService.getServiceMail(), "Online Question "+ip, emailContent);
			this.setMessageInfo("SUCCESS_INFO");
		}
		return SUCCESS;
	}
}

package com.itecheasy.ph3.web.buyer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.itecheasy.common.PageList;
import com.itecheasy.ph3.SearchOrder;
import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.customer.CustomerService;
import com.itecheasy.ph3.email.EmailService;
import com.itecheasy.ph3.newsletter.NewsLetterService;
import com.itecheasy.ph3.product.LunceneProduct;
import com.itecheasy.ph3.product.ProductService;
import com.itecheasy.ph3.product.ProductService.ProductLunceneSearchCriteria;
import com.itecheasy.ph3.product.ProductService.ProductLunceneSearchOrder;
import com.itecheasy.ph3.ticket.Ticket;
import com.itecheasy.ph3.ticket.TicketService;
import com.itecheasy.ph3.web.BuyerBaseAction;
import com.itecheasy.ph3.web.exception.AppException;
import com.itecheasy.ph3.web.utils.ConfigHelper;
import com.itecheasy.ph3.web.utils.StrUtils;
import com.itecheasy.ph3.web.utils.ValidateUtils;
import com.itecheasy.ph3.web.vo.LunceneProductVO;

public class BuyerIndexAction extends BuyerBaseAction {
	private static final long serialVersionUID = 6722225455L;
	
	private NewsLetterService newsLetterService;
	private ProductService productService;
	private TicketService ticketService;
	private EmailService emailService;

	private final static int RECOMMEND_PRODUCTS_SIZE = 21;
	private final static int NEW_PRODUCTS_SIZE = 35;

	public void setNewsLetterService(NewsLetterService newsLetterService) {
		this.newsLetterService = newsLetterService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}
	public void setTicketService(TicketService ticketService) {
		this.ticketService = ticketService;
	}
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	/**
	 * 首页
	 */
	public String doIndex() throws AppException {
		searchRecommendProducts();
		searchNewProducts();
		return SUCCESS;
	}
	/**
	 * Deals页面
	 */
	public String doDeals(){
		request.setAttribute("homeType", "deals");
		return SUCCESS;
	}

	/**
	 * 搜索推荐商品
	 */
	private void searchRecommendProducts() throws AppException {
		Map<ProductLunceneSearchCriteria, Object> searchCriteria = new HashMap<ProductLunceneSearchCriteria, Object>();
		searchCriteria.put(ProductLunceneSearchCriteria.IS_RECOMMEND, true);
		searchCriteria.put(ProductLunceneSearchCriteria.IS_DISPLAY, true);
		searchCriteria.put(ProductLunceneSearchCriteria.IS_HAS_STOCK, true);
		List<SearchOrder<ProductLunceneSearchOrder>> searchOrder = new ArrayList<SearchOrder<ProductLunceneSearchOrder>>();
		searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(
				ProductLunceneSearchOrder.RECOMMEND_DATE, false));
		
		PageList<LunceneProduct> result = productService
				.searchProductsFromLuncene(1, RECOMMEND_PRODUCTS_SIZE,
						searchCriteria, searchOrder);

		boolean isShowMoreButton = false;
		List<LunceneProductVO> recommendProducts = null;
		if (result != null) {
			int rowCount = result.getPage().getTotalRowCount();
			if (rowCount > RECOMMEND_PRODUCTS_SIZE) {
				isShowMoreButton = true;
			}
			if (rowCount > 0) {
				recommendProducts = new LinkedList<LunceneProductVO>();
				LunceneProductVO vo;
				for (LunceneProduct p : result.getData()) {
					vo = new LunceneProductVO();
					vo.setProduct(p);
					vo.setShowCategoryName(categoryService
							.getShowCategoryName(p.getShowCategoryId()));
					recommendProducts.add(vo);
				}
			}
		}
		request.setAttribute("isShowMoreButton", isShowMoreButton);
		request.setAttribute("recommendProducts", recommendProducts);
	}

	/**
	 * 搜索新商品
	 */
	private void searchNewProducts() {
		Map<ProductLunceneSearchCriteria, Object> searchCriteria = new HashMap<ProductLunceneSearchCriteria, Object>();
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DAY_OF_YEAR, -ConfigHelper.NEW_PRODUCTS_BEGIN_JOIN_DATE);
		Date beginJoinDate = now.getTime();
		searchCriteria.put(ProductLunceneSearchCriteria.BEGIN_JOIN_DATE,
				beginJoinDate);
		searchCriteria.put(ProductLunceneSearchCriteria.IS_DISPLAY, true);
		searchCriteria.put(ProductLunceneSearchCriteria.IS_HAS_STOCK, true);
		
		List<SearchOrder<ProductLunceneSearchOrder>> searchOrder = new ArrayList<SearchOrder<ProductLunceneSearchOrder>>();
		searchOrder.add(new SearchOrder<ProductLunceneSearchOrder>(
				ProductLunceneSearchOrder.JOIN_DATE, false));

		PageList<LunceneProduct> result = productService
				.searchProductsFromLuncene(1, NEW_PRODUCTS_SIZE,
						searchCriteria, searchOrder);

		boolean isShowMoreButton = false;
		List<LunceneProductVO> newProducts = null;

		if (result != null) {
			int rowCount = result.getPage().getTotalRowCount();
			if (rowCount > NEW_PRODUCTS_SIZE) {
				isShowMoreButton = true;
			}
			if (rowCount > 0) {
				newProducts = new LinkedList<LunceneProductVO>();
				LunceneProductVO vo;
				for (LunceneProduct p : result.getData()) {
					vo = new LunceneProductVO();
					vo.setProduct(p);
					vo.setShowCategoryName(categoryService
							.getShowCategoryName(p.getShowCategoryId()));
					newProducts.add(vo);
				}
			}
		}
		request.setAttribute("isShowMoreButtonOfNewProduct", isShowMoreButton);
		request.setAttribute("newProducts", newProducts);
	}
	/**
	 * 客户意见反馈
	 */
	public void customerFeedback(){
		String feedbackCont = param("feedbackCont");
		String email = param("email");
		String ip = request.getRemoteAddr();
		Integer userId = null;
		if(getLoginedUserBuyer() != null){
			userId = getLoginedUserBuyer().getId();
		}
		//htmlencode
		feedbackCont = StrUtils.htmlConvert(feedbackCont);
		ticketService.submitSuggest(ip, email, feedbackCont, userId);
		//邮箱测试
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		String emailContent = "<table width=\"644\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:3px solid #ddd; font-family:Arial, Helvetica, sans-serif;font-size:12px;color:#333;\">"+
					   "<tr>"+
					   "<td style=\"border-bottom:2px solid #3A5799;padding:14px 14px 10px 14px;font:bold 30px Georgia;margin:0;\">Suggestion Feedback </td>"+
					   "</tr>"+
					   "<tr>"+
					   "<td style=\"padding:27px 36px 200px 36px;line-height:24px;\">"+
					   "<p>Email:"+email+"</p>"+
					   "<p>提交时间:"+sdf.format(now)+"</p>"+
					   "<p>"+feedbackCont+"</p>"+
					   "</td></tr></table>"	;
		emailService.sendEmail(emailService.getServiceMail(), "Suggestion Feedback"+ip, emailContent);
	}
	/**
	 * 订阅邮件
	 */
	public void signUpNewsLetter() {
		String email = param("email").trim();
		String type = "fail";
		if (email.length() <= 100 && ValidateUtils.isEmail(email)) {
			try {
				Customer customer = customerService.getCustomerByEmail(email);
				Integer customerId = customer != null ? customer.getId() : null;
				newsLetterService.subscribe(email, customerId);
				type = "success";
			} catch (Exception e) {
			}
		}
		try {
			returnHtml(type);
		} catch (IOException e) {
		}
	}
	
	/**
	 * 邮件退订
	 */
	public String unsubscribe()
	{
		String email = param("email");
		String mailId = param("MailId");

		if( mailId.length() > 0 ){
			newsLetterService.unsubscribe(email, mailId);
		}	
		
		return SUCCESS;
	}

}

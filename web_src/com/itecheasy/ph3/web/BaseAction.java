package com.itecheasy.ph3.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.json.annotations.JSON;

import com.itecheasy.common.PageList;
import com.itecheasy.common.picture.PictureService;
import com.itecheasy.common.po.PictureStore;
import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.system.Country;
import com.itecheasy.ph3.system.Currency;
import com.itecheasy.ph3.system.DictionaryService;
import com.itecheasy.ph3.system.SystemService;
import com.itecheasy.ph3.web.components.struts2.ParamActionSupport;
import com.itecheasy.ph3.web.utils.SessionUtils;

public class BaseAction extends ParamActionSupport {
	private static final long serialVersionUID = 1L;
	protected transient final Log log = LogFactory.getLog(getClass());

	public final static String AJAX_RESPONSE_STATES_SUCCESS = "success";
	public final static String AJAX_RESPONSE_STATES_ERROR = "error";
	public final static String CONTINUE_URL = "continueURL";
	protected static final int PAGE_SIZE = 20;

	private String continueURL;

	protected String messageInfo;
	protected Integer currentPage = 1;
	protected Integer pageSize;
	protected PictureService pictureService;

	@SuppressWarnings("unchecked")
	protected PageList pageList;

	public void returnJson(String jsonString) throws IOException {
		response.setContentType("application/json");
		PrintWriter pw = response.getWriter();
		pw.print(jsonString);
	}

	public void returnHtml(String html) throws IOException {
		PrintWriter pw = response.getWriter();
		pw.print(html);
	}

	public boolean isAjaxRequest() {
		String value = request.getHeader("X-Requested-With");
		return "XMLHttpRequest".equalsIgnoreCase(value);
	}

	@JSON(serialize = false)
	public Customer getLoginedUserBuyer() {
		return SessionUtils.getLoginedCustomer(request);
	}

	@JSON(serialize = false)
	public void setLoginedUserBuyer(Customer customer) {
		SessionUtils.setLoginedCustomer(request, customer);
	}

	public String getContinueURL() {
		return continueURL;
	}

	public void setContinueURL(String continueURL) {
		this.continueURL = continueURL;
	}

	public void setPictureService(PictureService pictureService) {
		this.pictureService = pictureService;
	}

	@JSON(serialize = false)
	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	@JSON(serialize = false)
	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	@JSON(serialize = false)
	public String getMessageInfo() {
		return messageInfo;
	}

	public void setMessageInfo(String messageInfo) {
		this.messageInfo = messageInfo;
	}

	protected List<String> errorInfos = new ArrayList<String>();

	@JSON(serialize = false)
	public List<String> getErrorInfos() {
		return errorInfos;
	}

	public void setErrorInfos(List<String> errorInfos) {
		this.errorInfos = errorInfos;
	}

	@SuppressWarnings("unchecked")
	@JSON(serialize = false)
	public PageList getPageList() {
		return pageList;
	}

	@SuppressWarnings("unchecked")
	public void setPageList(PageList pageList) {
		this.pageList = pageList;
	}

	public String getImageUrl(String code, int width, int heigth) {
		try {

			PictureStore pictureStore = pictureService.getPictureInfo(code);
			if (pictureStore == null) {
				return "";
			}
			Integer picWidth = pictureStore.getWidth();
			Integer picHeight = pictureStore.getHeight();
			if (picWidth == null || picHeight == null)
				return "";

			if (picWidth < width && picHeight < width) {
				return pictureService.getPictureURL(code, picWidth, picHeight);
			} else if ((float) picWidth / picHeight > (float) width / heigth) {
				return pictureService.getPictureURL(code, width, Math
						.round((float) width * picHeight / picWidth));
			} else {
				return pictureService.getPictureURL(code, Math
						.round((float) heigth * picWidth / picHeight), heigth);
			}
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 分页查询表单参数 ///protected Map<String, Object> parameters = new
	 * HashMap<String, Object>(); public Map<String, Object> getParameters() {
	 * return parameters; }
	 * 
	 * public void setParameters(Map<String, Object> parameters) {
	 * this.parameters = parameters; }
	 */

	
}

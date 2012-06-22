package com.itecheasy.ph3.web.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;
import com.itecheasy.ph3.category.CategoryService;
import com.itecheasy.ph3.category.StandardCategory;
import com.itecheasy.ph3.web.AdminBaseAction;
import com.itecheasy.ph3.web.WebConfig;
import com.itecheasy.ph3.web.exception.AppException;
import com.itecheasy.ph3.web.utils.StrUtils;

public class AdminCategoryAction extends AdminBaseAction {
	private static final long serialVersionUID = 990101218L;

	private static final BigDecimal profitRateTemp = new BigDecimal(WebConfig.getInstance().get("profit.rate"));
	private Integer pId = 0;// 保存利润系数时跳转到列表提供的类别id;
	private String message;
	private CategoryService categoryService;

	/**
	 * AJAX获取子类别
	 */
	public void doSubStandardCategories() {
		Integer categoryId = paramInt("id", 0);
		String fm = "<option value=\"%1$s\">%2$s</option>";
		StringBuffer html = new StringBuffer(String.format(fm, 0, "..."));
		if (categoryId > 0) {
			List<StandardCategory> secondCategories = categoryService
					.getSubStandardCategories(categoryId);
			if (secondCategories != null && !secondCategories.isEmpty()) {
				for (StandardCategory category : secondCategories) {
					html.append(String.format(fm, category.getId(),
							StringEscapeUtils.escapeHtml(category.getName())));
				}
			}
		}
		try {
			returnHtml(html.toString());
		} catch (IOException e) {
		}

	}

	/**
	 * 获取标准类别的利润率
	 */
	public String doProfitList() throws AppException {
		Integer categoryId = paramInt("id", 0);
		StandardCategory category = null;
		StandardCategory firstCategory = null;
		StandardCategory secondCategory = null;
		List<StandardCategory> firstCategoryList = null;
		List<StandardCategory> secondCategoryList = null;
		List<StandardCategory> subCategoryList = null;
		Map<Integer, BigDecimal> profitRateMap = null;

		firstCategoryList = categoryService.getRootStandardCategories();
		if (categoryId > 0) {
			category = categoryService.getStandardCategory(categoryId);
			if (category == null) {
				throw new AppException("Can not find any category.");
			}
			if (category.getParent() == null) {
				firstCategory = category;
				secondCategoryList = categoryService
						.getSubStandardCategories(categoryId);
			} else {
				firstCategory = category.getParent();
				secondCategory = category;

				if (firstCategory.getParent() != null) {
					secondCategory = firstCategory;
					firstCategory = firstCategory.getParent();
				}

				secondCategoryList = categoryService
						.getSubStandardCategories(firstCategory.getId());
			}

			subCategoryList = categoryService
					.getSubStandardCategories(categoryId);

		} else {
			subCategoryList = firstCategoryList;
		}

		profitRateMap = new HashMap<Integer, BigDecimal>();
		BigDecimal profitRate;
		for (StandardCategory item : subCategoryList) {
			if (1 == item.getCategoryType()) {
				profitRate = categoryService.getStandardCategoryProfitRate(item
						.getId());
				profitRate = (profitRate == null) ? new BigDecimal(1)
						: profitRate;
				profitRateMap.put(item.getId(), profitRate);

			}
		}

		request.setAttribute("category", category);// 当前类别
		request.setAttribute("firstCategory", firstCategory);// 一级类别
		request.setAttribute("secondCategory", secondCategory);// 二级类别
		request.setAttribute("firstCategoryList", firstCategoryList);// 一级列表列表
		request.setAttribute("secondCategoryList", secondCategoryList);// 一级列表列表
		request.setAttribute("CategoryList", subCategoryList);// 显示在列表的类别
		request.setAttribute("profitRateMap", profitRateMap);// 显示在列表的类别利润率
		request.setAttribute("profitRateTemp", profitRateTemp);// 显示在列表的类别利润率

		return SUCCESS;
	}

	/**
	 * 设置标准类别的利润率
	 */
	public String saveStandardCategoryProfitRate() throws AppException {
		Integer categoryId = paramInt("id", 0);
		Integer secondId = paramInt("secondId", 0);
		BigDecimal profitRate = vilidata(this.param("profitRate"));
		
		setProfitRate(categoryId, profitRate);
		if (secondId <= 0) {
			StandardCategory item = categoryService
					.getStandardCategory(categoryId);
			StandardCategory itemParent = item.getParent();
			if (itemParent == null) {
				this.setPId(0);
			} else {
				StandardCategory itemGrend = itemParent.getParent();
				if (itemGrend == null) {
					this.setPId(itemParent.getId());
				} else {
					this.setPId(itemGrend.getId());
				}
			}
		} else {
			this.setPId(secondId);
		}
		this.setMessageInfo("SUCCESS_INFO");
		return SUCCESS;
	}

	/**
	 * 设置标准类别的利润率(保存所有)
	 */
	public String saveAllProfitRate() throws Exception {
		Integer secondId = paramInt("secondId", 0);
		Integer[] categoryIds = paramInts("categoryId");
		String[] profitRates = request.getParameterValues("profitRate");
		if (categoryIds != null && categoryIds.length>0) {
			if (profitRates != null && profitRates.length>0) {
				BigDecimal rate;
				Integer categoryId;
				for (int i = 0; i < categoryIds.length; i++) {
					rate = vilidata(profitRates[i]);
					categoryId = categoryIds[i];
					setProfitRate(categoryId, rate);
				}
			}
		}
		this.setPId(secondId);
		this.setMessageInfo("SUCCESS_INFO");
		return SUCCESS;
	}

	/**
	 * 验证并返回利润系数
	 * 
	 * @param rateStr
	 * @return
	 * @throws AppException
	 */
	private BigDecimal vilidata(String rateStr) throws AppException {
		BigDecimal empty = new BigDecimal(0);
		Pattern p = Pattern.compile("^((\\d{1,}\\.\\d{2,2})|(\\d{1,}\\.\\d{1,1})|(\\d{1,}))$");
		Matcher m = p.matcher(rateStr);
		BigDecimal rate = StrUtils.tryParseBigDecimal(rateStr, null);
		if (rateStr == null || rateStr.isEmpty()) {
			throw new AppException("请填写价格系数!");
		} else if (!m.find()) {
			throw new AppException("您的输入错误，请重新输入！");
		} else if (rate == null || rate.compareTo(empty) <= 0
				|| rate.compareTo(profitRateTemp) > 0) {
			throw new AppException("价格系数在 0.01 和 " + profitRateTemp
					+ " 之间,请重新输入.");
		} else {
			return rate;
		}
	}

	/**
	 * 设置标准类别的利润率
	 * 
	 * @param categoryId
	 * @param profitRate
	 */
	private void setProfitRate(Integer categoryId, BigDecimal profitRate) {
		categoryService.setStandardCategoryProfitRate(categoryId, profitRate);
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getPId() {
		return pId;
	}

	public void setPId(Integer id) {
		pId = id;
	}

}

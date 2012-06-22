package com.itecheasy.ph3.web.admin;

import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.product.ProductPromotionService;
import com.itecheasy.ph3.product.PromoteArea;
import com.itecheasy.ph3.web.AdminBaseAction;
import com.itecheasy.ph3.web.exception.AppException;
import com.itecheasy.ph3.web.utils.StrUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminPromoteAreaAction extends AdminBaseAction{
	private static final long serialVersionUID = 1L;
	private ProductPromotionService productPromotionService;

	/**
	 * 促销区列表
	 * @return
	 */
	public String doPromoteAreas()
	{
		List<PromoteArea> promoteAreas= productPromotionService.getPromoteAreas();
		Map<Integer, Integer> countMap = getPromoteProductCount(promoteAreas);
		
		request.setAttribute("countMap", countMap);
		request.setAttribute("promoteAreas", promoteAreas);
		return SUCCESS;
	}
	
	private Map<Integer, Integer> getPromoteProductCount(List<PromoteArea> promoteAreas)
	{
		Map<Integer, Integer> countMap = null;
		
		if (promoteAreas != null) {
			countMap = new HashMap<Integer, Integer>();
			for (PromoteArea item : promoteAreas) {
				int count =productPromotionService.getPromoteProductCount(item.getId());
				countMap.put(item.getId(), count);
			}
		}
		
		return countMap;
	}
	
	/**
	 * 保存促销区
	 * @return
	 */
	public String savePromoteArea ()
	{
		PromoteArea promoteArea = null;
		Integer id = paramInt("areaId",0);
		String name = param("name");
		BigDecimal offDiscount = StrUtils.tryParseBigDecimal(param("discount"), null);
		Date beginDate = paramDate("beginDate");
		Date endDate = paramDate("endDate");		
		Boolean isDisplay = paramBool("isDisplay");
		Integer resultInfo = 0;
		
		if (!checkPromoteArea(name,offDiscount,beginDate,endDate)) {
			new AppException("请填写促销区相应信息");
		}
		promoteArea = setPromoteArea(id,name,offDiscount,beginDate,endDate,isDisplay);

		if (promoteArea != null && promoteArea.getBeginDate() != null && promoteArea.getEndDate() != null && promoteArea.getIsDisplay() != null) {
			synchronized (AdminPromoteAreaAction.class) {
			productPromotionService.setPromoteArea(promoteArea);
			}
			resultInfo = 1;
		}
		
		List<PromoteArea> promoteAreas= productPromotionService.getPromoteAreas();
		Map<Integer, Integer> countMap = getPromoteProductCount(promoteAreas);
		
		request.setAttribute("countMap", countMap);
		request.setAttribute("promoteAreas", promoteAreas);
		request.setAttribute("resultInfo", resultInfo);
		
		return SUCCESS;
	}
	
	/**
	 * 保存促销区
	 * @return
	 */
/*	public void savePromoteArea ()throws AppException
	{
		String fm = "[{\"result\":%1$s}]";
		int result = 0;
		PromoteArea promoteArea = null;
		Integer id = paramInt("areaId",0);
		String name = param("name");
		BigDecimal offDiscount = StrUtils.tryParseBigDecimal(param("discount"), null);
		Date beginDate = paramDate("beginDate");
		Date endDate = paramDate("endDate");		
		Boolean isDisplay = paramBool("isDisplay");
		if (!checkPromoteArea(name,offDiscount,beginDate,endDate)) {
			new AppException("请填写促销区相应信息");
		}
		promoteArea = setPromoteArea(id,name,offDiscount,beginDate,endDate,isDisplay);

		if (promoteArea != null) {
			productPromotionService.setPromoteArea(promoteArea);
		}
		result = 1;
		try {
			returnJson(String.format(fm, result));
		} catch (IOException e) {
		}
	}*/
	/**
	 * 删除促销区
	 * @return
	 * @throws AppException
	 */
	public String deletePromoteArea() throws AppException
	{
		Integer promoteAreaId = paramInt("areaId",0);
		if (promoteAreaId != null) {
			try {
				productPromotionService.deletePromoteArea(promoteAreaId);
			} catch (BussinessException e) {
				throw new AppException("有商品的促销区不允许删除！");
			}
		}
		return SUCCESS;
	}
	/**
	 * 验证
	 * @param name
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	private boolean checkPromoteArea(String name, BigDecimal offDiscount, Date beginDate, Date endDate) {
		Pattern p = Pattern.compile("^[0-9]{1,2}$");
		Matcher m = p.matcher(offDiscount.toString());
		if (name == null || name.isEmpty()) {
			return false;
		}if (name.length()>100) {
			return false;
		}
		if (offDiscount == null || name.isEmpty() || !m.matches()) {
			return false;
		}
		if (beginDate == null) {
			return false;
		}
		if (endDate == null || !endDate.after(new Date())) {
			return false;
		}
		
		return true;
	}
	/**
	 * 设置促销区信息
	 * @param id
	 * @param name
	 * @param offDiscount
	 * @param beginDate
	 * @param endDate
	 * @param isDisplay
	 * @return
	 */
	private PromoteArea setPromoteArea(Integer id, String name, BigDecimal offDiscount, Date beginDate, Date endDate, Boolean isDisplay)
	{
		PromoteArea promoteArea = null;
		if (id > 0) {
			promoteArea = productPromotionService.getPromoteArea(id);
		}else {
			promoteArea = new PromoteArea();
		}
		promoteArea.setName(name);
		promoteArea.setOffDiscount(offDiscount);
		promoteArea.setBeginDate(beginDate);
		promoteArea.setEndDate(endDate);
		promoteArea.setCreateDate(new Date());
		promoteArea.setIsDisplay(isDisplay);
		return promoteArea;
	}
	public void setProductPromotionService(
			ProductPromotionService productPromotionService) {
		this.productPromotionService = productPromotionService;
	}
}

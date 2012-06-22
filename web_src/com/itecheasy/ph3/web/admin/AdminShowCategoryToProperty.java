package com.itecheasy.ph3.web.admin;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.category.CategoryService;
import com.itecheasy.ph3.category.StandardCategory;
import com.itecheasy.ph3.category.CategoryService.PropertyType;
import com.itecheasy.ph3.property.Property;
import com.itecheasy.ph3.web.AdminBaseAction;
import com.itecheasy.ph3.web.exception.AppException;
import com.itecheasy.ph3.web.utils.UrlHelper;
import com.itecheasy.ph3.web.utils.json.JsonUtil;

public class AdminShowCategoryToProperty extends AdminBaseAction {
	private static final long serialVersionUID = 1L;
	
	private Integer categoryId;
	private Integer standardCategoryId;
	private String propertyType;
	private Integer propertyId;
	private Integer orderIndex;
	private String refererUrl;
	private CategoryService categoryService;
	
	public String getRefererUrl() {
		return refererUrl;
	}

	public void setRefererUrl(String refererUrl) {
		this.refererUrl = refererUrl;
	}

	/***
	 * 类别归并与属性关联
	 * 
	 * @return
	 */
	public String doCateoryToProperty()throws AppException {
		if(categoryId==null){
			throw new AppException("归并类别编号为空");
		}
		List<StandardCategory> unMergedList =categoryService.getUnbindStandardCategories();
		List<StandardCategory>  mergedList = categoryService.getBindedStandardCategoriesOfShowCategroy(categoryId);
		sort(unMergedList);
		sort(mergedList);
		request.setAttribute("mergedList",mergedList); 
		request.setAttribute("unMergedList",unMergedList); //未绑定的标准类别
		
		List<Property> propertyList = categoryService.getUnbindPropertiesOfShowCategory(categoryId,PropertyType.SYSTEM);
		List<Property> unPropertyList = categoryService.getBindedPropertiesOfShowCategory(categoryId);
		request.setAttribute("propertyList",propertyList); 
		request.setAttribute("unPropertyList", unPropertyList);
		if(StringUtils.isEmpty(refererUrl))
			refererUrl =  UrlHelper.getRefererUrl(request);
		
		return SUCCESS;
	}
	
	/*
	 * 排序
	 */
	private void sort(List<StandardCategory> list){
		Collections.sort(list,new Comparator<StandardCategory>(){
			@Override
			public int compare(StandardCategory o1,StandardCategory o2){
				if(o1 == o2) return 0;
				return getName(o1).compareTo(getName(o2));
			}
			private String getName(StandardCategory o1){
				while (true) {
					if(o1.getParent()==null)
						break;
					else
						o1 = o1.getParent();
				}
				return o1.getName();
			}
		});
	}

	/***
	 * 归并类别
	 * 
	 * @return
	 */
	public String bindCategory() {
		try {
			categoryService.bindStandardCategoryToShowCategory(categoryId, standardCategoryId);
		} catch (BussinessException e) {
			this.setMessageInfo(e.getErrorMessage());
		}
		return SUCCESS;
	}

	/***
	 * 解除归并类别
	 * 
	 * @return
	 */
	public String unCategory() {
		categoryService.unbindStandardCategoryToShowCategory(categoryId, standardCategoryId);
		return SUCCESS;
	}

	/***
	 * 更新属性排序
	 * 
	 * @return
	 */
	public String updatePropertyOrderIndex() {
		categoryService.setPropertyOrderIndexInShowCategory(categoryId, propertyId, orderIndex);
		return SUCCESS;
	}

	/***
	 * 删除属性
	 * 
	 * @return
	 */
	public String deleteProperty() {
		categoryService.unbindPropertyToShowCategory(categoryId, propertyId);
		return SUCCESS;
	}

	/***
	 * 添加属性
	 * 
	 * @return
	 */
	public String addCategoryProperty() {
		try {
			categoryService.bindPropertyToShowCategory(categoryId, propertyId);
		} catch (BussinessException e) {
			setMessageInfo(e.getErrorMessage());
		}
		return SUCCESS;
	}
	
	public void getProperty(){
		List<Property> propertyList = null;
		if("SYSTEM".equals(propertyType))
			propertyList = categoryService.getUnbindPropertiesOfShowCategory(categoryId,PropertyType.SYSTEM);
		else if("CUSTOM".equals(propertyType))
			propertyList =categoryService.getUnbindPropertiesOfShowCategory(categoryId,PropertyType.CUSTOM);
		String jsonString = JsonUtil.obj2String(propertyList, null, new String[]{"description","type","isVisible","orderIndex"}, null);
		try {
			this.returnJson(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	public Integer getStandardCategoryId() {
		return standardCategoryId;
	}

	public void setStandardCategoryId(Integer standardCategoryId) {
		this.standardCategoryId = standardCategoryId;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	public Integer getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Integer propertyId) {
		this.propertyId = propertyId;
	}

	public Integer getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}
	
	
}

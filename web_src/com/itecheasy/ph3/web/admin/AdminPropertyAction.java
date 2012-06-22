package com.itecheasy.ph3.web.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.property.Property;
import com.itecheasy.ph3.property.PropertyService;
import com.itecheasy.ph3.property.PropertyValue;
import com.itecheasy.ph3.property.PropertyValueGroup;
import com.itecheasy.ph3.web.AdminBaseAction;
import com.itecheasy.ph3.web.exception.AppException;

public class AdminPropertyAction extends AdminBaseAction {
	private static final long serialVersionUID = 620101218L;
	private PropertyService propertyService;

	private Integer propertyId;

	public void setPropertyService(PropertyService propertyService) {
		this.propertyService = propertyService;
	}

	public Integer getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(Integer propertyId) {
		this.propertyId = propertyId;
	}

	/**
	 * 待分级属性列表
	 */
	public String doPropertyList() {
		List<Property> propertyList = propertyService
				.getPropertiesWithHasPropertyValueGroup();
		request.setAttribute("propertyList", propertyList);
		return SUCCESS;
	}

	public String doPropertyMerge() throws AppException {
		Integer propertyId = paramInt("propertyId", 0);
		Integer isAdd = paramInt("isAdd", 0);
		boolean isEdit = false;
		List<Property> propertyList = null;
		List<PropertyValueGroup> propertyValueGroupList = null;
		Map<Integer, List<PropertyValue>> propertyValueMap = null;
		List<PropertyValue> unmergedPropertyValueList = null;
		if (isAdd > 0 || propertyId < 1) {
			// add
			propertyList = propertyService
					.getPropertiesWithoutHasPropertyValueGroup();
			unmergedPropertyValueList = propertyService
			.getUnMergedPropertyValues(propertyId);
		} else {
			// edit
			isEdit = true;
			Property property = propertyService.getProperty(propertyId);
			if (property == null) {
				throw new AppException("Can not find the property.");
			}
			propertyList = new ArrayList<Property>();
			propertyList.add(property);

			propertyValueGroupList = propertyService
					.getAllPropertyValueGroupsByProperty(propertyId);
			unmergedPropertyValueList = propertyService
					.getUnMergedPropertyValues(propertyId);

			if (propertyValueGroupList != null
					&& !propertyValueGroupList.isEmpty()) {
				propertyValueMap = new HashMap<Integer, List<PropertyValue>>();
				for (PropertyValueGroup pvg : propertyValueGroupList) {
					propertyValueMap
							.put(pvg.getId(), propertyService
									.getAllPropertyValuesByPropertyValueGroup(pvg
											.getId()));
				}
			}

		}
		request.setAttribute("isEdit", isEdit);
		request.setAttribute("propertyList", propertyList);
		request.setAttribute("propertyValueGroupList", propertyValueGroupList);
		request.setAttribute("propertyValueMap", propertyValueMap);
		request.setAttribute("unmergedPropertyValueList",
				unmergedPropertyValueList);
		return SUCCESS;
	}

	/**
	 * 增加属性分级
	 * 
	 * @throws AppException
	 */
	public String addPropertyValueGroup() throws AppException {
		Integer propertyId = paramInt("propertyId", 0);
		String name = param("pvgName");
		if (name == null || name.isEmpty()) {
			throw new AppException("The name is incorrect.");
		}
		Property property = null;
		if (propertyId > 0) {
			property = propertyService.getProperty(propertyId);
		}
		if (property == null) {
			throw new AppException("Cannot find the property.");
		}
		PropertyValueGroup pvg = new PropertyValueGroup();
		pvg.setProperty(property);
		pvg.setName(name);
		pvg.setDescription(null);
		pvg.setIsVisible(true);
		propertyService.addPropertyValueGroup(pvg);
		this.propertyId = propertyId;
		return SUCCESS;
	}

	/**
	 * 更改属性分级
	 * 
	 * @throws AppException
	 */
	public void updatePropertyValueGroup() throws AppException {
		Integer pvgId = paramInt("pvgId", 0);
		String name = param("pvgName");
		Integer result = 0;
		if (name == null || name.isEmpty()) {
			result = 0;
		}
		PropertyValueGroup pvg = null;
		if (pvgId > 0) {
			pvg = propertyService.getPropertyValueGroup(pvgId);
		}
		if (pvg == null) {
			result = -1;
		}
		pvg.setName(name);
		try {
			propertyService.updatePropertyValueGroup(pvg);
			result = 1;
			returnHtml(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 属性值分组绑定属性值
	 * 
	 * @return
	 */
	public String bindPropertyValue() throws AppException {
		Integer pvgId = paramInt("pvgId", 0);
		Integer pvId = paramInt("pvId", 0);
		if (pvgId < 1 || pvId < 1) {
			throw new AppException("没有选定属性值分组或属性值。");
		}
		try {
			propertyService.bindPropertyValueToPropertyValueGroup(pvgId, pvId);
		} catch (BussinessException e) {
			return ERROR;
		}
		return SUCCESS;
	}

	public String unbindPropertyValue() throws AppException {
		Integer pvgId = paramInt("pvgId", 0);
		Integer pvId = paramInt("pvId", 0);
		if (pvgId < 1 || pvId < 1) {
			throw new AppException("没有选定属性值分组或属性值。");
		}
		propertyService.unbindPropertyValueToPropertyValueGroup(pvgId, pvId);
		return SUCCESS;
	}

	/**
	 * 删除一个属性分级
	 */
	public String deletePropertyValueGroup() {
		Integer pvgId = paramInt("pvgId", 0);
		if (pvgId > 0) {
			PropertyValueGroup pvg = propertyService
					.getPropertyValueGroup(pvgId);
			if (pvg != null) {
				this.propertyId = pvg.getProperty().getId();
				propertyService.deletePropertyValueGroup(pvg.getId());
			}
		}
		return SUCCESS;
	}

	/**
	 * 删除所有属性分级
	 */
	public String deleteAllPropertyValueGroup() {
		Integer propertyId = paramInt("propertyId", 0);
		if (propertyId > 0) {
			propertyService.deletePropertyValueGroupsByProperty(propertyId);
		}
		return SUCCESS;
	}
}

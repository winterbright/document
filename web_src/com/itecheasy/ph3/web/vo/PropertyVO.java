package com.itecheasy.ph3.web.vo;

import java.util.List;

import com.itecheasy.ph3.property.Property;

public class PropertyVO {
	private Property property;
	private List<PropertyValueVO> propertyValues;
	private List<PropertyValueGroupVO> propertyValueGroups;
	private Boolean existPropertyValueGroup;

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public List<PropertyValueVO> getPropertyValues() {
		return propertyValues;
	}

	public void setPropertyValues(List<PropertyValueVO> propertyValues) {
		this.propertyValues = propertyValues;
	}

	public List<PropertyValueGroupVO> getPropertyValueGroups() {
		return propertyValueGroups;
	}

	public void setPropertyValueGroups(
			List<PropertyValueGroupVO> propertyValueGroups) {
		this.propertyValueGroups = propertyValueGroups;
	}

	public Boolean getExistPropertyValueGroup() {
		return existPropertyValueGroup;
	}

	public void setExistPropertyValueGroup(Boolean existPropertyValueGroup) {
		this.existPropertyValueGroup = existPropertyValueGroup;
	}

}

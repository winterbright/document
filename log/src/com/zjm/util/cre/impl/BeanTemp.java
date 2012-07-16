package com.zjm.util.cre.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @alias
 * @author zjm
 *
 * 2012-3-31
 */
public class BeanTemp {

	private String beanPackage;
	private List<String> beanImport;
	private String beanModifier;
	private String beanType;
	private String beanName;
	private List<FieldTemp> beanFields;
	private List<MethodTemp> beanMethods;
	public String getBeanPackage() {
		return beanPackage;
	}
	public void setBeanPackage(String beanPackage) {
		this.beanPackage = beanPackage;
	}
	public List<String> getBeanImport() {
		return beanImport;
	}
	public void setBeanImport(List<String> beanImport) {
		this.beanImport = beanImport;
	}
	public String getBeanModifier() {
		return beanModifier;
	}
	public void setBeanModifier(String beanModifier) {
		this.beanModifier = beanModifier;
	}
	public String getBeanType() {
		return beanType;
	}
	public List<FieldTemp> getBeanFields() {
		return beanFields;
	}
	public void setBeanFields(List<FieldTemp> beanFields) {
		this.beanFields = beanFields;
	}
	public List<MethodTemp> getBeanMethods() {
		return beanMethods;
	}
	public void setBeanMethods(List<MethodTemp> beanMethods) {
		this.beanMethods = beanMethods;
	}
	public void setBeanType(String beanType) {
		this.beanType = beanType;
	}
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
}

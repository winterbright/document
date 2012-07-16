package com.zjm.util.model;

/**
 * @alias
 * @author zjm
 *
 * 2012-7-9
 */
public class FieldModelFtl {

	private String id;
	private String fieldLabel; // 标签
	private String name; // 后台根据此name属性取值
	private String value; //默认值
	private Object listeners; //监听事件
	private String allowBlank; // 是否允许为空
	private String maxLength; // 可输入的最大文本长度,不区分中英文字符
	private String minLength;// 可输入的最小文本长度,不区分中英文字符
	private String anchor = "100%"; // 宽度百分比, 默认：100%
	private String regexText;// 验证错误之后的提示信息: 电子邮件格式不合法
	private String regex;// 验证电子邮件格式的正则表达式: : /^([\w]+)(.[\w]+)*@([\w-]+\.){1,5}([A-Za-z]){2,4}$/,
	private String emptyText;// 空时提示内容: '请输入身份证号码',
	private String inputType; // 设置为密码框输入类型: : 'password'
	private String xtype; // 设置为数字输入框类型: 'numberfield','textarea','hidden','datefield','htmleditor','checkboxgroup','radiogroup'
	private String readOnly;// 设置只读属性
	private String disabled; // 设置禁用属性: 'x-custom-field-disabled',
	private String fieldClass; //样式： : 'x-custom-field-disabled',
	private String labelStyle; // : 'color:blue;',
	private String allowDecimals; // 是否允许输入小数
	private String allowNegative; // 是否允许输入负数
	private String decimalPrecision;// 小数精度
	private String maxValue; // 允许输入的最大值：99999
	private String minValue; // 允许输入的最小值
	private String disabledDays; //禁止选择的星期:[0,6],
	private String disabledDaysText;//禁止提示:'禁止选择星期天和星期6',
	private String format; // 'Y-m-d','Y年m月d日'
	private String comBox;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFieldLabel() {
		return fieldLabel;
	}
	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Object getListeners() {
		return listeners;
	}
	public void setListeners(Object listeners) {
		this.listeners = listeners;
	}
	public String getAllowBlank() {
		return allowBlank;
	}
	public void setAllowBlank(String allowBlank) {
		this.allowBlank = allowBlank;
	}
	public String getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}
	public String getMinLength() {
		return minLength;
	}
	public void setMinLength(String minLength) {
		this.minLength = minLength;
	}
	public String getAnchor() {
		return anchor;
	}
	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}
	public String getRegexText() {
		return regexText;
	}
	public void setRegexText(String regexText) {
		this.regexText = regexText;
	}
	public String getRegex() {
		return regex;
	}
	public void setRegex(String regex) {
		this.regex = regex;
	}
	public String getEmptyText() {
		return emptyText;
	}
	public void setEmptyText(String emptyText) {
		this.emptyText = emptyText;
	}
	public String getInputType() {
		return inputType;
	}
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	public String getXtype() {
		return xtype;
	}
	public void setXtype(String xtype) {
		this.xtype = xtype;
	}
	public String getReadOnly() {
		return readOnly;
	}
	public void setReadOnly(String readOnly) {
		this.readOnly = readOnly;
	}
	public String getDisabled() {
		return disabled;
	}
	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}
	public String getFieldClass() {
		return fieldClass;
	}
	public void setFieldClass(String fieldClass) {
		this.fieldClass = fieldClass;
	}
	public String getLabelStyle() {
		return labelStyle;
	}
	public void setLabelStyle(String labelStyle) {
		this.labelStyle = labelStyle;
	}
	public String getAllowDecimals() {
		return allowDecimals;
	}
	public void setAllowDecimals(String allowDecimals) {
		this.allowDecimals = allowDecimals;
	}
	public String getAllowNegative() {
		return allowNegative;
	}
	public void setAllowNegative(String allowNegative) {
		this.allowNegative = allowNegative;
	}
	public String getDecimalPrecision() {
		return decimalPrecision;
	}
	public void setDecimalPrecision(String decimalPrecision) {
		this.decimalPrecision = decimalPrecision;
	}
	public String getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}
	public String getMinValue() {
		return minValue;
	}
	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}
	public String getDisabledDays() {
		return disabledDays;
	}
	public void setDisabledDays(String disabledDays) {
		this.disabledDays = disabledDays;
	}
	public String getDisabledDaysText() {
		return disabledDaysText;
	}
	public void setDisabledDaysText(String disabledDaysText) {
		this.disabledDaysText = disabledDaysText;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
}

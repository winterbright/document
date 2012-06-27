package org.eredlab.g4.rif.taglib.html;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eredlab.g4.arm.service.ArmTagSupportService;
import org.eredlab.g4.arm.vo.UserInfoVo;
import org.eredlab.g4.bmf.util.SpringBeanLoader;
import org.eredlab.g4.ccl.datastructure.Dto;
import org.eredlab.g4.ccl.datastructure.impl.BaseDto;
import org.eredlab.g4.ccl.properties.PropertiesFactory;
import org.eredlab.g4.ccl.properties.PropertiesFile;
import org.eredlab.g4.ccl.properties.PropertiesHelper;
import org.eredlab.g4.ccl.tplengine.DefaultTemplate;
import org.eredlab.g4.ccl.tplengine.FileTemplate;
import org.eredlab.g4.ccl.tplengine.TemplateEngine;
import org.eredlab.g4.ccl.tplengine.TemplateEngineFactory;
import org.eredlab.g4.ccl.tplengine.TemplateType;
import org.eredlab.g4.ccl.util.G4Utils;
import org.eredlab.g4.ccl.util.G4Constants;
import org.eredlab.g4.rif.taglib.util.TagConstant;
import org.eredlab.g4.rif.taglib.util.TagHelper;
import org.eredlab.g4.rif.util.WebUtils;

/**
 * HTML标签
 * @author XiongChun
 * @since 2010-01-30
 */
public class HtmlTag extends TagSupport{
	
	private ArmTagSupportService armTagSupportService = (ArmTagSupportService)SpringBeanLoader.getSpringBean("armTagSupportService");
	
	private static Log log = LogFactory.getLog(HtmlTag.class);
	private String extDisabled;
	private String title;
	private String jqueryEnabled;
	private String showLoading;
	private String uxEnabled = "true";
	private String fcfEnabled = "false";
	private String doctypeEnable;  //带有时分秒选择的控件的页面需要设置为:true
	private String exportParams = "false";
	private String exportUserinfo = "false";
	private String isSubPage = "true";
	private String urlSecurity2 = "true";
	
	/**
	 * 标签开始
	 */
	public int doStartTag() throws JspException{
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		UserInfoVo userInfo = WebUtils.getSessionContainer(request).getUserInfo();
		String contextPath = request.getContextPath();
		request.setAttribute("webContext", contextPath);
		Dto dto = new BaseDto();
		PropertiesHelper pHelper = PropertiesFactory.getPropertiesHelper(PropertiesFile.G4);
		String micolor = pHelper.getValue("micolor", "blue");
		dto.put("micolor", micolor);
		String urlSecurity = pHelper.getValue("urlSecurity", "1");
		dto.put("urlSecurity", urlSecurity);
		dto.put("urlSecurity2", urlSecurity2);
		dto.put("userInfo", userInfo);
		dto.put("ajaxErrCode", G4Constants.Ajax_Timeout);
		dto.put("requestURL", request.getRequestURL());
		dto.put("contextPath", contextPath);
		dto.put("doctypeEnable", doctypeEnable);
		dto.put("extDisabled", G4Utils.isEmpty(extDisabled) ? "false" : extDisabled);
		dto.put("title", G4Utils.isEmpty(title) ? "eRedG4" : title);
		dto.put("jqueryEnabled", G4Utils.isEmpty(jqueryEnabled) ? "false" : jqueryEnabled);
		dto.put("showLoading", G4Utils.isEmpty(showLoading) ? "true" : showLoading);
		dto.put("uxEnabled", uxEnabled);
		dto.put("fcfEnabled", fcfEnabled);
		dto.put("exportParams", exportParams);
		dto.put("exportUserinfo", exportUserinfo);
		dto.put("isSubPage", isSubPage);
		dto.put("pageLoadMsg", WebUtils.getParamValue("PAGE_LOAD_MSG", request));
		String titleIcon = WebUtils.getParamValue("TITLE_ICON", request);
		dto.put("titleIcon", G4Utils.isEmpty(titleIcon) ? "eredg4.ico" : titleIcon);
		if (exportParams.equals("true")) {
			dto.put("paramList", WebUtils.getParamList(request));
		}
		//String agent = request.getHeader("user-agent");
		//dto.put("firefox", agent.indexOf("Firefox") == -1 ? "false" : "true");
		PropertiesHelper p = PropertiesFactory.getPropertiesHelper(PropertiesFile.G4);
		dto.put("extMode", p.getValue("extMode", TagConstant.Ext_Mode_Run));
		dto.put("runMode", p.getValue("runMode", TagConstant.RUN_MODE_NORMAL));
		Dto themeDto = new BaseDto();
		Dto resultDto = new BaseDto();
		if(G4Utils.isNotEmpty(userInfo)){
			themeDto.put("userid", userInfo.getUserid());
			resultDto = armTagSupportService.getEauserSubInfo(themeDto);
		}
		String theme = null;
		if(G4Utils.isNotEmpty(resultDto))
			theme = resultDto.getAsString("theme");
		String defaultTheme = WebUtils.getParamValue("SYS_DEFAULT_THEME", request);
		theme = G4Utils.isEmpty(theme) ? defaultTheme : theme;
		dto.put("theme", theme);
		TemplateEngine engine = TemplateEngineFactory.getTemplateEngine(TemplateType.VELOCITY);
		DefaultTemplate template = new FileTemplate();
		template.setTemplateResource(TagHelper.getTemplatePath(getClass().getName()));
		StringWriter writer = engine.mergeTemplate(template, dto);
		try {
			pageContext.getOut().write(writer.toString());
		} catch (IOException e) {
			log.error(G4Constants.Exception_Head + e.getMessage());
			e.printStackTrace();
		}
		return super.EVAL_BODY_INCLUDE;
	}
	
	/**
	 * 标签结束
	 */
	public int doEndTag() throws JspException{
		try {
			pageContext.getOut().write("</html>");
		} catch (IOException e) {
			log.error(G4Constants.Exception_Head + e.getMessage());
			e.printStackTrace();
		}
		return super.EVAL_PAGE;
	}
	
	/**
	 * 释放资源
	 */
	public void release(){
		extDisabled = null;
		title = null;
		jqueryEnabled = null;
		uxEnabled = null;
		fcfEnabled = null;
		doctypeEnable = null;
		exportParams = null;
		exportUserinfo = null;
		isSubPage = null;
		urlSecurity2 = null;
		super.release();
	}

	public void setExtDisabled(String extDisabled) {
		this.extDisabled = extDisabled;
	}

	public void setJqueryEnabled(String jqueryEnabled) {
		this.jqueryEnabled = jqueryEnabled;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setShowLoading(String showLoading) {
		this.showLoading = showLoading;
	}

	public void setUxEnabled(String uxEnabled) {
		this.uxEnabled = uxEnabled;
	}

	public String getFcfEnabled() {
		return fcfEnabled;
	}

	public void setFcfEnabled(String fcfEnabled) {
		this.fcfEnabled = fcfEnabled;
	}

	public void setDoctypeEnable(String doctypeEnable) {
		this.doctypeEnable = doctypeEnable;
	}

	public void setExportParams(String exportParams) {
		this.exportParams = exportParams;
	}

	public void setExportUserinfo(String exportUserinfo) {
		this.exportUserinfo = exportUserinfo;
	}

	public void setIsSubPage(String isSubPage) {
		this.isSubPage = isSubPage;
	}

	public void setUrlSecurity2(String urlSecurity2) {
		this.urlSecurity2 = urlSecurity2;
	}
}

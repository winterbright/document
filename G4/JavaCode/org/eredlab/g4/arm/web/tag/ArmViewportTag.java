package org.eredlab.g4.arm.web.tag;

import java.io.StringWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eredlab.g4.arm.service.ArmTagSupportService;
import org.eredlab.g4.arm.util.ArmConstants;
import org.eredlab.g4.arm.web.tag.vo.MenuVo;
import org.eredlab.g4.bmf.base.IReader;
import org.eredlab.g4.bmf.util.SpringBeanLoader;
import org.eredlab.g4.ccl.datastructure.Dto;
import org.eredlab.g4.ccl.datastructure.impl.BaseDto;
import org.eredlab.g4.ccl.tplengine.DefaultTemplate;
import org.eredlab.g4.ccl.tplengine.FileTemplate;
import org.eredlab.g4.ccl.tplengine.TemplateEngine;
import org.eredlab.g4.ccl.tplengine.TemplateEngineFactory;
import org.eredlab.g4.ccl.tplengine.TemplateType;
import org.eredlab.g4.ccl.util.G4Utils;
import org.eredlab.g4.ccl.util.G4Constants;
import org.eredlab.g4.rif.taglib.util.TagHelper;
import org.eredlab.g4.rif.util.SessionContainer;
import org.eredlab.g4.rif.util.WebUtils;

/**
 * ARMViewport标签:eRedG4_ARM专用
 * 
 * @author XiongChun
 * @since 2010-01-22
 */
public class ArmViewportTag extends TagSupport {

	private static Log log = LogFactory.getLog(ArmViewportTag.class);

	ArmTagSupportService armTagSupportService = (ArmTagSupportService) SpringBeanLoader
			.getSpringBean("armTagSupportService");

	private String northTitle = "";
	private String westTitle = "";
	private String scriptStart = "<script type=\"text/javascript\">";
	private String scriptEnd = "</script>";

	/**
	 * 标签初始方法
	 * 
	 * @return
	 * @throws JspException
	 */
	public int doStartTag() throws JspException {
		return super.SKIP_BODY;
	}

	/**
	 * 标签主体
	 * 
	 * @return
	 * @throws JspException
	 */
	public int doEndTag() throws JspException {
		JspWriter writer = pageContext.getOut();
		try {
			writer.print(getPanelScript());
		} catch (Exception e) {
			log.error(G4Constants.Exception_Head + e.getMessage());
			e.printStackTrace();
		}
		return super.EVAL_PAGE;
	}

	/**
	 * 获取Viewport标记脚本
	 * 
	 * @return 返回Viewport标记脚本
	 */
	private String getPanelScript() {
		IReader g4Reader = (IReader) SpringBeanLoader.getSpringBean("g4Reader");
		// String rootName =
		// (String)g4Reader.queryForObject("getMenuNameForCNPath", "01");
		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		Dto dto = new BaseDto();
		dto.put("northTitle", northTitle);
		dto.put("centerTitle",
				G4Utils.isEmpty(WebUtils.getParamValue("MENU_FIRST", request)) ? "请配置" : WebUtils.getParamValue(
						"MENU_FIRST", request));
		dto.put("welcomePageTitle", G4Utils.isEmpty(WebUtils.getParamValue("WELCOME_PAGE_TITLE", request)) ? "请配置" : WebUtils.getParamValue(
				"WELCOME_PAGE_TITLE", request));
		dto.put("banner", request.getContextPath() + WebUtils.getParamValue("INDEX_BANNER", request));
		dto.put("westTitle", westTitle);
		dto.put("scriptStart", scriptStart);
		dto.put("scriptEnd", scriptEnd);
		dto.put("copyright", WebUtils.getParamValue("BOTTOM_COPYRIGHT", request));
		String activeOnTop = "true";
		if ("0".equals(WebUtils.getParamValue("WEST_CARDMENU_ACTIVEONTOP", request))) {
			activeOnTop = "false";
		}
		dto.put("activeOnTop", activeOnTop);
		SessionContainer sessionContainer = WebUtils.getSessionContainer(request);
		String userid = sessionContainer.getUserInfo().getUserid();
		Dto dto2 = new BaseDto();
		dto2.put("userid", userid);
		String account = sessionContainer.getUserInfo().getAccount();
		account = account == null ? "" : account;
		String accountType = ArmConstants.ACCOUNTTYPE_NORMAL;
		if (account.equalsIgnoreCase(WebUtils.getParamValue("DEFAULT_ADMIN_ACCOUNT", request))) {
			accountType = ArmConstants.ACCOUNTTYPE_SUPER;
		} else if (account.equalsIgnoreCase(WebUtils.getParamValue("DEFAULT_DEVELOP_ACCOUNT", request))) {
			accountType = ArmConstants.ACCOUNTTYPE_DEVELOPER;
		}
		dto2.put("accountType", accountType);
		dto.put("accountType", accountType);
		List cardList = armTagSupportService.getCardList(dto2).getDefaultAList();
		for (int i = 0; i < cardList.size(); i++) {
			MenuVo cardVo = (MenuVo) cardList.get(i);
			if (i != cardList.size() - 1) {
				cardVo.setIsNotLast("true");
			}
		}
		dto.put("date", G4Utils.getCurDate());
		dto.put("week", G4Utils.getWeekDayByDate(G4Utils.getCurDate()));
		dto.put("welcome", getWelcomeMsg());
		dto.put("cardList", cardList);
		dto.put("username", sessionContainer.getUserInfo().getUsername());
		dto.put("account", sessionContainer.getUserInfo().getAccount());
		Dto qDto = new BaseDto();
		qDto.put("deptid", sessionContainer.getUserInfo().getDeptid());
		dto.put("deptname", armTagSupportService.getDepartmentInfo(qDto).getAsString("deptname"));
		Dto themeDto = new BaseDto();
		themeDto.put("userid", WebUtils.getSessionContainer(request).getUserInfo().getUserid());
		Dto resultDto = new BaseDto();
		resultDto = armTagSupportService.getEauserSubInfo(themeDto);
		String theme = resultDto.getAsString("theme");
		theme = G4Utils.isEmpty(theme) ? "default" : theme;
		dto.put("theme", theme);
		dto.put("themeColor", getThemeColor(theme));
		TemplateEngine engine = TemplateEngineFactory.getTemplateEngine(TemplateType.VELOCITY);
		DefaultTemplate template = new FileTemplate();
		template.setTemplateResource(TagHelper.getTemplatePath(getClass().getName()));
		StringWriter writer = engine.mergeTemplate(template, dto);
		String treesString = generateCardTrees(dto);
		return treesString + "\n" + writer.toString();
	}

	/**
	 * 生成卡片树
	 * 
	 * @param rootMenuId
	 */
	private String generateCardTrees(Dto pDto) {
		IReader sqlRunner = (IReader) SpringBeanLoader.getSpringBean("g4Reader");
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		SessionContainer sessionContainer = WebUtils.getSessionContainer(request);
		String userid = sessionContainer.getUserInfo().getUserid();
		Dto qDto = new BaseDto();
		qDto.put("userid", userid);
		List cardList = (List) pDto.get("cardList");
		String treesString = scriptStart + "Ext.onReady(function(){";
		for (int i = 0; i < cardList.size(); i++) {
			MenuVo cardVo = (MenuVo) cardList.get(i);
			qDto.put("menuid", cardVo.getMenuid());
			qDto.put("accountType", pDto.getAsString("accountType"));
			List menuList = armTagSupportService.getCardTreeList(qDto).getDefaultAList();
			String rootName = (String) sqlRunner.queryForObject("Organization.getMenuNameForCNPath", "01");
			Dto pathDto = new BaseDto();
			pathDto.put("01", rootName);
			Dto dto = new BaseDto();
			dto.put("menuList", generateMenuPathName(menuList, pathDto));
			dto.put("menuid", cardVo.getMenuid());
			TemplateEngine engine = TemplateEngineFactory.getTemplateEngine(TemplateType.VELOCITY);
			DefaultTemplate template = new FileTemplate();
			template.setTemplateResource(TagHelper.getTemplatePath(getClass().getName(), "CardTreesTag.tpl"));
			StringWriter writer = engine.mergeTemplate(template, dto);
			treesString = treesString + "\n" + writer.toString();
		}
		return treesString + "\n});" + scriptEnd;
	}

	/**
	 * 生成菜单路径对应中文名
	 * 
	 * @param pMenuList
	 *            菜单列表
	 * @return
	 */
	public List generateMenuPathName(List pMenuList, Dto pDto) {
		for (int i = 0; i < pMenuList.size(); i++) {
			MenuVo vo = (MenuVo) pMenuList.get(i);
			pDto.put(vo.getMenuid(), vo.getMenuname());
		}
		for (int i = 0; i < pMenuList.size(); i++) {
			String path = "";
			MenuVo vo = (MenuVo) pMenuList.get(i);
			String menuId = vo.getMenuid();
			int temp = menuId.length() / 2;
			int m = 0, k = 2;
			for (int j = 0; j < temp; j++) {
				path += pDto.getAsString(menuId.substring(m, k)) + " -> ";
				k += 2;
			}
			vo.setMenupath(path.substring(0, path.length() - 4));
		}
		return pMenuList;
	}

	/**
	 * 释放资源
	 */
	public void release() {
		super.release();
		northTitle = null;
		westTitle = null;
	}

	/**
	 * 生成问候信息
	 * 
	 * @return
	 */
	private String getWelcomeMsg() {
		String welcome = "晚上好";
		Integer timeInteger = new Integer(G4Utils.getCurrentTime("HH"));
		if (timeInteger.intValue() >= 7 && timeInteger.intValue() <= 12) {
			welcome = "上午好";
		} else if (timeInteger.intValue() > 12 && timeInteger.intValue() < 19) {
			welcome = "下午好";
		}
		return welcome;
	}

	/**
	 * 获取和主题对应匹配的颜色值
	 */
	private String getThemeColor(String theme) {
		String color = "slategray";
		if (theme.equalsIgnoreCase("default")) {
			color = "4798D7";
		} else if (theme.equalsIgnoreCase("lightRed")) {
			color = "F094C9";
		} else if (theme.equalsIgnoreCase("lightYellow")) {
			color = "EAAA85";
		} else if (theme.equalsIgnoreCase("gray")) {
			color = "969696";
		} else if (theme.equalsIgnoreCase("lightGreen")) {
			color = "53E94E";
		} else if (theme.equalsIgnoreCase("purple2")) {
			color = "BC5FD8";
		}
		return color;
	}

	public void setNorthTitle(String northTitle) {
		this.northTitle = northTitle;
	}

	public void setWestTitle(String westTitle) {
		this.westTitle = westTitle;
	}

	public void setScriptStart(String scriptStart) {
		this.scriptStart = scriptStart;
	}

	public void setScriptEnd(String scriptEnd) {
		this.scriptEnd = scriptEnd;
	}
}

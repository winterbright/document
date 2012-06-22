package com.itecheasy.ph3.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.xwork.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.itecheasy.common.picture.PictureService;
import com.itecheasy.common.po.PictureStore;

public class Img extends TagSupport {
	private static Log log = LogFactory.getLog(Img.class); 
	private String code;
	private int width;
	private int heigth;

	public Img() {
	}

	public int doStartTag() throws JspException {
		try {
			String url = StringUtils.EMPTY;
			if(StringUtils.isEmpty(code)){
				return SKIP_BODY;
			}
			PictureService pictureService = (PictureService) WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext()).getBean("pictureService");
			PictureStore pictureStore = pictureService.getPictureInfo(code);
			if(pictureStore==null){
				return SKIP_BODY;
			}
			Integer picWidth = pictureStore.getWidth();
			Integer picHeight = pictureStore.getHeight();
			if(picWidth == null || picHeight == null)
			return SKIP_BODY;
			
			if (picWidth < width && picHeight < width) {
				url = pictureService.getPictureURL(code, picWidth, picHeight);
			} else if ((float) picWidth / picHeight > (float) width / heigth) {
				url = pictureService.getPictureURL(code, width, Math.round((float) width * picHeight / picWidth));
			} else {
				url = pictureService.getPictureURL(code, Math.round((float) heigth * picWidth / picHeight), heigth);
			}
			pageContext.getOut().print(url);
		} catch (Exception e) {
			String mess = code == null  ? "code is null " : ("code : " + code );
 			log.error(mess + "  " +e);
 			e.printStackTrace();
		}
		return SKIP_BODY;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeigth() {
		return heigth;
	}

	public void setHeigth(int heigth) {
		this.heigth = heigth;
	}

}

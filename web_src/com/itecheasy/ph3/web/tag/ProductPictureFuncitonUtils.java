package com.itecheasy.ph3.web.tag;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.itecheasy.common.picture.PictureService;
import com.itecheasy.common.po.PictureStore;
import com.itecheasy.ph3.product.picture.ProductPictureService;
import com.itecheasy.ph3.web.WebConfig;

public class ProductPictureFuncitonUtils {

	private static ProductPictureService productPictureService;
	private static PictureService pictureService;

	public void setProductPictureService(
			ProductPictureService productPictureService) {
		ProductPictureFuncitonUtils.productPictureService = productPictureService;
	}
	

	public void setPictureService(PictureService pictureService) {
		ProductPictureFuncitonUtils.pictureService = pictureService;
	}


	/**
	 * 获取图片原始大小的图片URL路径（带水印）
	 * 
	 * @param name
	 *            photosName图片名称
	 */
	public static String getOriginalImageFullUrl(String photosName) {
		return replaceURL(productPictureService.getOriginalImageFullUrl(photosName));
	}

	/**
	 * 获得图片对应的URL
	 * 
	 * @param name
	 *            photosName
	 * @param name
	 *            specWidth
	 * @param name
	 *            specHeight
	 */
	public static String getPhotoUrl(String photosName, int specWidth,
			int specHeight) {
		return replaceURL(productPictureService.getPhotoUrl(photosName, specWidth,
				specHeight));
	}

	/**
	 * 获得图片对应的URL
	 * 
	 * @param name
	 *            photosName
	 * @param name
	 *            specWidth
	 * @param name
	 *            specHeight
	 */
	public static String getPhotoUrl(int index, String photosName,
			int specWidth, int specHeight) {
		return replaceURL(productPictureService.getPhotoUrl(index, photosName, specWidth,
				specHeight));
	}

	/**
	 * 获得NoPhoto图片路径
	 * 
	 * @param name
	 *            specWidth图片规格宽度
	 * @param name
	 *            specHeight图片规格高度
	 */
	public static String getNoPhotoImageFullUrl(int specWidth, int specHeight) {
		return replaceURL(productPictureService.getNoPhotoImageFullUrl(specWidth,
				specHeight));
	}

	/**
	 * 获得NoPhoto图片路径
	 * 
	 * @param name
	 *            specWidth图片规格宽度
	 * @param name
	 *            specHeight图片规格高度
	 */
	public static String getNoPhotoImageFullUrl(int index, int specWidth,
			int specHeight) {
		return replaceURL(productPictureService.getNoPhotoImageFullUrl(index, specWidth,
				specHeight));
	}

	/**
	 * 获得图片存储的物理路径(全路径，包括文件名)
	 * 
	 * @param name
	 *            photosName
	 * @param name
	 *            specWidth
	 * @param name
	 *            specHeight
	 */
	public static String getPhotoStoragePath(String photosName, int specWidth,
			int specHeight) {
		return replaceURL(productPictureService.getPhotoStoragePath(photosName, specWidth,
				specHeight));
	}

	/**
	 * 获得规格图片的名称
	 * 
	 * @param name
	 *            photosName
	 * @param name
	 *            specWidth
	 * @param name
	 *            specHeight 
	 */
	public static String getPhotoName(String photosName, int specWidth,
			int specHeight) {
		return replaceURL(productPictureService.getPhotoName(photosName, specWidth,
				specHeight));
	}
	
	private static String HTTPS = "https";
	
	private static String REPLACE_URL = "https://www.pandahallstock.com/photos/";
	
	public static String replaceURL(String url){
		HttpServletRequest request = ServletActionContext.getRequest();
		if(request == null)
			return url;
		if(HTTPS.equalsIgnoreCase(request.getScheme())){
			String configURL = WebConfig.getInstance().get("replace.url");
			url = url.replaceAll("http://(.*?)/",StringUtils.isEmpty(configURL)? REPLACE_URL : configURL.trim() );
		}
		return url;
	}
	
	public static String getImageUrl(String code, int width, int heigth) {
		try {

			PictureStore pictureStore = pictureService.getPictureInfo(code);
			if (pictureStore == null) {
				return "";
			}
			Integer picWidth = pictureStore.getWidth();
			Integer picHeight = pictureStore.getHeight();
			if (picWidth == null || picHeight == null)
				return "";

			if (picWidth < width && picHeight < width) {
				return pictureService.getPictureURL(code, picWidth, picHeight);
			} else if ((float) picWidth / picHeight > (float) width / heigth) {
				return pictureService.getPictureURL(code, width, Math
						.round((float) width * picHeight / picWidth));
			} else {
				return pictureService.getPictureURL(code, Math
						.round((float) heigth * picWidth / picHeight), heigth);
			}
		} catch (Exception e) {
			return "";
		}
	}
}

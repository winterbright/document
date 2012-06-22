package com.itecheasy.ph3.web.admin.quartz;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itecheasy.common.PageList;
import com.itecheasy.ph3.SearchOrder;
import com.itecheasy.ph3.category.CategoryService;
import com.itecheasy.ph3.category.ShowCategory;
import com.itecheasy.ph3.email.EmailService;
import com.itecheasy.ph3.product.LunceneProduct;
import com.itecheasy.ph3.product.ProductImage;
import com.itecheasy.ph3.product.ProductService;
import com.itecheasy.ph3.product.ProductService.ProductLunceneSearchCriteria;
import com.itecheasy.ph3.product.ProductService.ProductLunceneSearchOrder;
import com.itecheasy.ph3.product.picture.ProductPictureService;
import com.itecheasy.ph3.web.WebConfig;
import com.itecheasy.ph3.web.buyer.GooleProductFeedAction;
import com.itecheasy.ph3.web.tag.FuncitonUtils;
import com.itecheasy.ph3.web.tag.UrlFunction;
import com.itecheasy.ph3.web.vo.productfeed.Author;
import com.itecheasy.ph3.web.vo.productfeed.FeedEntity;
import com.itecheasy.ph3.web.vo.productfeed.ProductFeed;

public class QuartzProductFeed {
	Log log = LogFactory.getLog(QuartzProductFeed.class);
	private ProductService productService;
	private CategoryService categoryService;
	private ProductPictureService productPictureService;
	private EmailService emailService;
	String filePath = WebConfig.getInstance().get("gooleProductFeed.filePath");
	int pageSize = Integer.parseInt(WebConfig.getInstance().get("gooleProductFeed.pageSize"));
	int currentPage = 1;
	int imageWidth = 360;
	int imageHeight = 360;

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	public void setProductPictureService(ProductPictureService productPictureService) {
		this.productPictureService = productPictureService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public void autoGenerateProductFeed() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ProductFeed pf = new ProductFeed();
		pf.setTitle("Pandahallstock product feed.");
		pf.setLink(" rel=\"self\" href=\"http://www.pandahallstock.com\"");
		pf.setUpdated(new Date());
		pf.setAuthor(new Author("Pandahallstock."));
		pf.setId("tag:www.pandahallstock.com,(update)" + sdf.format(new Date()) + " products");
		pf.setFeedItems(generateProductFeedItems());
		try {
			File file = new File(filePath + "temp.xml");
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.delete();
			}
			FileWriter fw = new FileWriter(file);
			pf.toProductFeedXml(fw);
			fw.close();
			File fileDelete = new File(filePath + "GoogleFeed.xml");
			// 删除已存在的文件
			if (fileDelete.exists()) {
				boolean canDelete = false;
				for (int i = 0; i < 5; i++) {
					if (file.canWrite()) {
						canDelete = true;
						break;
					} else {
						Thread.sleep(360000);
					}
				}
				if (canDelete) {
					fileDelete.delete();
				}
			}
			// 重命名
			file.renameTo(new File(filePath + "GoogleFeed.xml"));
		} catch (Exception e) {
			try {
				Properties pro = new Properties();
				InputStream is = GooleProductFeedAction.class.getResourceAsStream("/deploy_config.properties");
				pro.load(is);
				String adminEmail = pro.getProperty("admin.email");
				emailService.sendEmail(adminEmail, "goole feed 生成失败", "具体报错如下：" + e.getMessage());
				is.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	/**
	 * 生成feed实体列表
	 * 
	 * @return
	 */
	private LinkedList<FeedEntity> generateProductFeedItems() {
		LinkedList<FeedEntity> feedEntityItems = new LinkedList<FeedEntity>();
		Map<ProductLunceneSearchCriteria, Object> searchCriteria = new HashMap<ProductLunceneSearchCriteria, Object>(); // 设置查询参数
		List<SearchOrder<ProductLunceneSearchOrder>> searchOrder = new ArrayList<SearchOrder<ProductLunceneSearchOrder>>(); // 设置排序方式
		searchCriteria.put(ProductLunceneSearchCriteria.IS_HAS_STOCK, true);
		searchCriteria.put(ProductLunceneSearchCriteria.IS_DISPLAY, true);
		searchCriteria.put(ProductLunceneSearchCriteria.IS_MIX, false);
		// 查询索引非混装商品
		PageList<LunceneProduct> lProductPageList = productService.searchProductsFromLuncene(currentPage, pageSize, searchCriteria, searchOrder);
		feedEntityItems.addAll(toFeedEntity(lProductPageList.getData(), false));
		int totalRow = lProductPageList.getPage().getTotalRowCount();
		int loopCount = totalRow % pageSize == 0 ? totalRow / pageSize : totalRow / pageSize + 1;
		for (int i = 1; i < loopCount; i++) {
			PageList<LunceneProduct> lProductPageListLoop = productService.searchProductsFromLuncene(i + 1, pageSize, searchCriteria, searchOrder);
			if (lProductPageListLoop != null && lProductPageListLoop.getData() != null) {
				feedEntityItems.addAll(toFeedEntity(lProductPageListLoop.getData(), false));
			}
		}

		// 查询索引混装商品
		searchCriteria.put(ProductLunceneSearchCriteria.IS_MIX, true);
		PageList<LunceneProduct> lProductPageListMix = productService.searchProductsFromLuncene(currentPage, pageSize, searchCriteria, searchOrder);
		feedEntityItems.addAll(toFeedEntity(lProductPageListMix.getData(), true));
		totalRow = lProductPageListMix.getPage().getTotalRowCount();
		loopCount = totalRow % pageSize == 0 ? totalRow / pageSize : totalRow / pageSize + 1;
		for (int i = 1; i < loopCount; i++) {
			PageList<LunceneProduct> lProductPageListLoopMix = productService.searchProductsFromLuncene(i + 1, pageSize, searchCriteria, searchOrder);
			if (lProductPageListLoopMix != null && lProductPageListLoopMix.getData() != null) {
				feedEntityItems.addAll(toFeedEntity(lProductPageListLoopMix.getData(), true));
			}
		}
		return feedEntityItems;
	}

	/**
	 * 
	 * LunceneProduct 到 FeedEntity转换
	 * 
	 * @param listProducts
	 * @return
	 */
	private List<FeedEntity> toFeedEntity(List<LunceneProduct> listProducts, boolean mixFlag) {
		List<FeedEntity> fes = new ArrayList<FeedEntity>();
		if (listProducts != null) {
			for (LunceneProduct product : listProducts) {
				try {
				FeedEntity fe = new FeedEntity();
				fe.setGid(product.getId() + "");
				String name = product.getName();
				StringBuffer tempName = new StringBuffer();
				if (name != null) {
					String[] tempNames = name.split(",");
					if (tempNames.length > 3) {
						tempName.append(tempNames[0] + ",").append(tempNames[1] + ",").append(tempNames[2]);
					} else {
						tempName.append(name);
					}
				}
				fe.setTitle(StringEscapeUtils.escapeHtml(tempName.toString()));
				ShowCategory category = categoryService.getShowCategory(product.getShowCategoryId());
				String urlss = "";
				if (category != null) {
					urlss = UrlFunction.getProductDetail(product.getId(), product.getName(), category.getName());
				}
				fe.setLink(urlss);
				String price = product.getProductSalePrices() == null && product.getProductSalePrices().size() > 0 ? "" : FuncitonUtils.getPriceString(product.getProductSalePrices().get(0)
						.getSalePriceAfterDiscount());
				fe.setgPrice(price);
				StringBuffer sbDescription = new StringBuffer();
				sbDescription.append(StringEscapeUtils.escapeHtml(product.getDescription()));
				sbDescription.append("<br/>Priced per " + product.getNewUnitQuantity() + " " + product.getUnit() + "<br/>");
				if (mixFlag) {
					sbDescription.append("Please Note: Due to stock variety, color or shape of mixed products may vary from photo sample shown on our website.<br/>");
				}
				fe.setDescription(StringEscapeUtils.escapeHtml(sbDescription.toString()));
				fe.setgCondition("new");
				fe.setgBrand("PandaHallStock.com");
				fe.setgMpn(product.getCode());
				List<ProductImage> images = productService.getProductImages(product.getId());
				String photoName = images != null && images.size() > 0 ? images.get(0).getImageUrl() : null;
				String imageSrc = productPictureService.getPhotoUrl(photoName, imageWidth, imageHeight);
				fe.setgImageLink(imageSrc);
				String cateGoryStr = "";
				if (category != null) {
					category.getName();
					for (; category.getParent() != null;) {
						cateGoryStr = category.getParent().getName() + " > " + cateGoryStr;
						category = category.getParent();
					}
				}
				fe.setgProductType(StringEscapeUtils.escapeHtml(cateGoryStr));
				fe.setgQuantity(product.getBatchStock() + "");
				fe.setgAvailability("limited availability");
				fe.setgOnlineOnly("y");
				fe.setgShippingWeight(product.getShippingWeight().intValue() + "");
				fes.add(fe);
				} catch (Exception e) {
					log.error(" googlefeed get image error productId : " + product.getId() + e.getMessage());
				}
			}
		}
		return fes;
	}

}

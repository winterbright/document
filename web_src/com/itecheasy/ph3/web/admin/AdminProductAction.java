package com.itecheasy.ph3.web.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.itecheasy.common.PageList;
import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.SearchOrder;
import com.itecheasy.ph3.category.CategoryService;
import com.itecheasy.ph3.category.ShowCategory;
import com.itecheasy.ph3.product.Product;
import com.itecheasy.ph3.product.ProductImage;
import com.itecheasy.ph3.product.ProductPromotionService;
import com.itecheasy.ph3.product.ProductService;
import com.itecheasy.ph3.product.ProductSpec;
import com.itecheasy.ph3.product.PromoteArea;
import com.itecheasy.ph3.product.ProductService.ProductSearchCriteria;
import com.itecheasy.ph3.product.ProductService.ProductSearchOrder;
import com.itecheasy.ph3.property.PropertyValue;
import com.itecheasy.ph3.web.AdminBaseAction;
import com.itecheasy.ph3.web.exception.AppException;
import com.itecheasy.ph3.web.utils.SessionUtils;
import com.itecheasy.ph3.web.utils.StrUtils;
import com.itecheasy.ph3.web.utils.UrlHelper;
import com.itecheasy.ph3.web.vo.ProductVO;

public class AdminProductAction extends AdminBaseAction {
	private static final long serialVersionUID = 1889954231354L;
	private String refererUrlStr;
	private ProductService productService;
	private CategoryService categoryService;
	private ProductPromotionService productPromotionService;
	private static final int PAGE_SIZE = 20;

	public String getRefererUrlStr() {
		return refererUrlStr;
	}
	public void setRefererUrlStr(String refererUrlStr) {
		this.refererUrlStr = refererUrlStr;
	}
	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public void setProductPromotionService(
			ProductPromotionService productPromotionService) {
		this.productPromotionService = productPromotionService;
	}

	/**
	 * AJAX获取子类别
	 * 
	 * @return
	 */
	public void doSubShowCategories() {
		Integer categoryId = paramInt("id", 0);
		StringBuffer html = new StringBuffer("<option value='0'>...</option>");
		if (categoryId > 0) {
			// 获取二级标准类别
			List<ShowCategory> SubCategories = categoryService
					.getSubShowCategories(categoryId);
			if (SubCategories != null && !SubCategories.isEmpty()) {
				for (ShowCategory category : SubCategories) {
					html.append("<option value='" + category.getId() + "'>"
							+ StringEscapeUtils.escapeHtml(category.getName())
							+ "</option>");
				}
			}
		}
		try {
			returnHtml(html.toString());
		} catch (IOException e) {
		}
	}

	public void doPromoteArea() throws IOException {
		
		if(SessionUtils.getLoginedAdminUser() == null){
			returnHtml("timeout");
			return;
		}
		int productId = paramInt("productId", 0);
		Integer promoteAreaId = null;
		if (productId > 0) {
			PromoteArea area = productPromotionService
					.getPromotionAreaOfProduct(productId);
			if (area != null) {
				promoteAreaId = area.getId();
			}
		}
		List<PromoteArea> promoteAreas = productPromotionService
				.getPromoteAreas();
		String fm = "<div class='lableDiv'><div>" +
				"<input type='radio' name='rdoPromotion' value=\"%1$s\" %4$s/></div>" +
				"<div>%2$s %3$s</div></div>" +
				"<div style='clear:both;height:20px'></div>";
		StringBuffer html = new StringBuffer();
		html.append(String.format(fm, 0, "None", "",
				promoteAreaId == null ? "checked='checked'" : ""));
		if (promoteAreas != null && !promoteAreas.isEmpty()) {

			for (PromoteArea promoteArea : promoteAreas) {
				html.append(String
						.format(fm, promoteArea.getId(), StringEscapeUtils.escapeHtml(promoteArea.getName()),promoteArea.getOffDiscount().setScale(0, BigDecimal.ROUND_UP)
								+ "% off", (promoteArea.getId().equals(
								promoteAreaId) ? "checked='checked'" : "")));
			}
		}
		try {
			returnHtml(html.toString());
		} catch (IOException e) {
		}
	}


	public String doProductList() throws AppException {
		ShowCategory currentCategory = null;
		ShowCategory firstCategory = null;
		ShowCategory secondCategory = null;
		ShowCategory thirdCategory = null;
		List<ShowCategory> firstCategoryList = null;
		List<ShowCategory> secondCategoryList = null;
		List<ShowCategory> thirdCategoryList = null;
		Integer categoryId = paramInt("SearchCategoryId", 0);
		Integer newIsDisplay = paramInt("newIsDisplay", -1);
		Integer oldIsDisplay = paramInt("oldIsDisplay", -1);
		Integer promotionAreaId = paramInt("promotionId", 0);
		Integer isHasStock = paramInt("batchStock", -1);
		String code = param("code");
		String name = param("name");

		firstCategoryList = categoryService.getRootShowCategories();
		if (categoryId > 0) {
			currentCategory = categoryService.getShowCategory(categoryId);
			if (currentCategory == null) {
				throw new AppException("Can not find any category.");
			}
			if (currentCategory.getParent() == null) {
				firstCategory = currentCategory;
				secondCategoryList = categoryService
						.getSubShowCategories(categoryId);
			} else {
				if (currentCategory.getParent().getParent() == null) {
					firstCategory = currentCategory.getParent();
					secondCategory = currentCategory;
					if (firstCategory.getParent() != null) {
						secondCategory = firstCategory;
						firstCategory = firstCategory.getParent();
					}
					secondCategoryList = categoryService
							.getSubShowCategories(firstCategory.getId());
				} else {
					firstCategory = currentCategory.getParent().getParent();
					secondCategory = currentCategory.getParent();
					thirdCategory = currentCategory;
					secondCategoryList = categoryService
							.getSubShowCategories(firstCategory.getId());
					thirdCategoryList = categoryService
							.getSubShowCategories(secondCategory.getId());
				}
			}
		}

		Map<ProductSearchCriteria, Object> searchCriteria = new HashMap<ProductSearchCriteria, Object>();
		if (code != null && !code.isEmpty()) {
			searchCriteria.put(ProductSearchCriteria.PRODUCT_CODE, code);
		} else {
			boolean hasSubCategory = categoryService
					.hasSubShowCategory(categoryId);
			if (!hasSubCategory && categoryId > 0) {
				searchCriteria.put(ProductSearchCriteria.SHOW_CATEGORY_ID,
						categoryId);
			}
			if (newIsDisplay >= 0) {
				boolean p3IsDisplay = newIsDisplay == 1 ? true : false;
				searchCriteria.put(ProductSearchCriteria.P3_IS_DISPLAY,
						p3IsDisplay);
			}
			if (oldIsDisplay >= 0) {
				boolean p2IsDisplay = oldIsDisplay == 1 ? true : false;
				searchCriteria.put(ProductSearchCriteria.P2_IS_DISPLAY,
						p2IsDisplay);
			}
			if (promotionAreaId > 0) {
				searchCriteria.put(ProductSearchCriteria.PROMOTE_AREA_ID,
						promotionAreaId);
			}
			if (isHasStock >= 0) {
				boolean HasStock = isHasStock == 1 ? true : false;
				searchCriteria
						.put(ProductSearchCriteria.IS_HAS_STOCK, HasStock);
			}
			if (name != null && !name.isEmpty()) {
				searchCriteria.put(ProductSearchCriteria.PRODUCT_NAME, name);
			}
		}
		List<SearchOrder<ProductSearchOrder>> searchOrder = new ArrayList<SearchOrder<ProductSearchOrder>>();

		searchOrder.add(new SearchOrder<ProductSearchOrder>(
				ProductSearchOrder.JOIN_DATE, false));

		searchOrder.add(new SearchOrder<ProductSearchOrder>(
				ProductSearchOrder.STOCK, false));

		List<PromoteArea> promoteAreas = productPromotionService.getPromoteAreas();
		ProductVO vo = null;
		List<ProductVO> vos = null;
		
		PageList<Product> products = productService.searchProducts(currentPage,
				PAGE_SIZE, searchCriteria, searchOrder);
	
		this.pageList = products;
	
		if (products != null) {
			vos = new ArrayList<ProductVO>();
			for (Product item : products.getData()) {
				vo = new ProductVO();
				vo.setFirstPrice(productService.getProductFirstSalePrice(item.getId()));
				vo.setProduct(item);
				vo.setShowCategoryName(productService.getShowCategoryNameOfProduct(item.getId()));
				vo.setIsPromote(productPromotionService.isJoinPromotionArea(item.getId()));
				vo.setIsRecommend(productService.isRecommendProduct(item.getId()));
				vos.add(vo);
			}
		}
		
		request.setAttribute("products", vos);
		request.setAttribute("newIsDisplay", newIsDisplay);
		request.setAttribute("oldIsDisplay", oldIsDisplay);
		request.setAttribute("promotionAreaId", promotionAreaId);
		request.setAttribute("isHasStock", isHasStock);
		request.setAttribute("code", code);
		request.setAttribute("name", name);
		request.setAttribute("category", currentCategory);// 当前类别
		request.setAttribute("promoteAreas", promoteAreas);
		request.setAttribute("firstCategory", firstCategory);// 一级类别
		request.setAttribute("secondCategory", secondCategory);// 二级类别
		request.setAttribute("thirdCategory", thirdCategory);// 三级类别
		request.setAttribute("firstCategoryList", firstCategoryList);// 一级列表列表
		request.setAttribute("secondCategoryList", secondCategoryList);// 二级列表列表
		request.setAttribute("thirdCategoryList", thirdCategoryList);// 三级列表列表
		request.setAttribute("messageInfo", param("messageInfo"));
		return SUCCESS;
	}

	public String doProductDetails() {
		Integer productId = paramInt("productId", 0);
		Product product = productService.getProduct(productId);
		List<ProductImage> productImages = productService
				.getProductImages(productId);
		List<ProductSpec> productSpecs = productService
				.getProductSpecs(productId);
		List<PropertyValue> propertyValues = productService.getRelatedPropertyValueOfProduct(productId);
		String description="";
		ProductVO vo = null;
		vo = new ProductVO();
		vo.setFirstPrice(productService.getProductFirstSalePrice(productId));
		vo.setProduct(product);
		vo.setShowCategory(productService.getShowCategoryOfProduct(productId));
		vo.setShowCategoryName(productService
				.getShowCategoryNameOfProduct(productId));
		vo.setProductImages(productImages);
		vo.setProductSpecs(productSpecs);
		vo.setPropertyValues(propertyValues);
		description=productService.getProductDescription(productId);
		vo.setDescription(description);
		request.setAttribute("productVo", vo);
		request.setAttribute("refererUrl", UrlHelper.getRefererUrl(request));
		
		return SUCCESS;
	}

	public void editUnitQuantity() throws AppException {
		String fm = "[{\"result\":%1$s}]";
		int result = 0;
		Integer productId = paramInt("productId", 0);
		Integer unitQuantity = paramInt("unitQuantity", 0);

		if (unitQuantity <= 0) {
			new AppException("data error!");
		}
		productService.setUnitQuantity(productId, unitQuantity);
		result = 1;
		try {
			returnJson(String.format(fm, result));
		} catch (IOException e) {
		}
	}

	public void displayProduct() {
		String fm = "[{\"result\":%1$s}]";
		int result = 0;
		Integer productId = paramInt("productId", 0);
		boolean status = paramBool("status");
		if (productId > 0) {
			if (status) {
				productService.hideProduct(productId);
			} else {
				productService.showProduct(productId);
			}
			result = 1;
		}
		try {
			returnJson(String.format(fm, result));
		} catch (IOException e) {
		}
	}

	public void recommendAllProducts() {
		String productIds = param("productIds").trim();
		boolean isRecommend = paramBool("isRecommend", true);
		String fm = "[{\"result\":%1$s}]";
		int result = 0;
		if (productIds != null && !productIds.isEmpty()) {
			String[] productIdsList = productIds.split("\\|");
			int productId;
			if (isRecommend) {
				for (int i = 0; i < productIdsList.length; i++) {
					productId = StrUtils.tryParseInt(productIdsList[i], 0);
					if (productId > 0) {
						productService.recommendProduct(productId);
					}
				}
			}else{
				for (int i = 0; i < productIdsList.length; i++) {
					productId = StrUtils.tryParseInt(productIdsList[i], 0);
					if (productId > 0) {
						productService.cancelRecommendProduct(productId);
					}
				}
			}
			result = 1;
		}
		try {
			returnJson(String.format(fm, result));
		} catch (IOException e) {
		}
	}

	public void promoteAllProducts() {
		String fm = "[{\"result\":%1$s}]";
		int result = 0;
		String productIds = param("productIds").trim();
		int promoteAreaId = paramInt("promoteAreaId", -1);
		Integer productId = null;
		if (productIds != null && !productIds.isEmpty() && promoteAreaId > -1) {
			String[] productIdsList = productIds.split("\\|");
			Integer id;
			if (productIdsList.length == 1) {
				productId = StrUtils.tryParseInt(productIdsList[0], 0);
				if (productId > 0) {
					if (promoteAreaId > 0) {
						try {
							id = productPromotionService
									.addProductToPromoteArea(productId,
											promoteAreaId);
							if (id != null) {
								result = 1;
							}
						} catch (BussinessException e) {

						}

					} else {
						productPromotionService
								.removeProductFromPromoteArea(productId);
						result = 1;
					}
				}
			} else {
				boolean isCompletedAcion = true;
				for (int i = 0; i < productIdsList.length; i++) {
					productId = StrUtils.tryParseInt(productIdsList[i], 0);

					if (promoteAreaId > 0) {
						if (productId > 0) {
							try {
								id = productPromotionService
										.addProductToPromoteArea(productId,
												promoteAreaId);
								if (id == null) {
									isCompletedAcion = false;
								}
							} catch (BussinessException e) {
								isCompletedAcion = false;
							}
						} else {
							isCompletedAcion = false;
						}
					} else {
						if (productId > 0) {
							productPromotionService
									.removeProductFromPromoteArea(productId);
						}
					}
				}
				if (isCompletedAcion) {
					result = 1;
				} else {
					//
					result = 2;
				}
			}
		}
		try {
			returnJson(String.format(fm, result));
		} catch (IOException e) {
		}
	}

}

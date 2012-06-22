package com.itecheasy.ph3.web.buyer;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.itecheasy.common.Page;
import com.itecheasy.common.PageList;
import com.itecheasy.ph3.category.ShowCategory;
import com.itecheasy.ph3.customer.Customer;
import com.itecheasy.ph3.product.ProductService;
import com.itecheasy.ph3.shopping.WishListItem;
import com.itecheasy.ph3.shopping.ShoppingService.WishListSearchCriteria;
import com.itecheasy.ph3.web.BuyerBaseAction;
import com.itecheasy.ph3.web.exception.AppException;
import com.itecheasy.ph3.web.utils.WebUtils;
import com.itecheasy.ph3.web.vo.WishListItemVO;
import com.itecheasy.sslplugin.annotation.Secured;

public class BuyerWishListAction extends BuyerBaseAction {
	private static final long serialVersionUID = 45666L;

	private static final int PAGE_SIZE = 20;
	private static String NAV_MyWishList = "MyWishList";

	private ProductService productService;

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	/**
	 * 收藏夹列表
	 */
	@Secured
	public String doMyWishList() throws AppException {
		Integer customerId = this.getLoginedUserBuyer().getId();
		int categoryId = paramInt("category", 0);
		Date beginDate = paramDate("d1");
		Date endDate = paramDate("d2");
		Page page = null;
		List<WishListItemVO> list = null;
		int showInfoType = 0;

		List<ShowCategory> categories = shoppingService
				.getShowCategoriesOfWishList(customerId);

		if (categories != null && !categories.isEmpty()) {
			Map<WishListSearchCriteria, Object> searchCriteria = new HashMap<WishListSearchCriteria, Object>();
			searchCriteria.put(WishListSearchCriteria.PRODUCT_VISIBLE, true);
			if (beginDate != null && endDate != null) {
				if (beginDate.after(endDate)) {
					// 交换开始时间跟结束时间
					Date tempDate = beginDate;
					beginDate = endDate;
					endDate = tempDate;
				}
			}
			if (beginDate != null) {
				searchCriteria
						.put(WishListSearchCriteria.BEGIN_DATE, beginDate);
			}
			if (endDate != null) {
				searchCriteria.put(WishListSearchCriteria.END_DATE, WebUtils
						.getLongDateTime(endDate));
			}
			if (categoryId > 0) {
				if (containCategory(categories, categoryId)) {
					searchCriteria.put(WishListSearchCriteria.SHOW_CATEGORY_ID,
							categoryId);
				}
			}
			PageList<WishListItem> items = shoppingService.searchWishList(
					currentPage, PAGE_SIZE, customerId, searchCriteria);

			if (items != null) {
				page = items.getPage();
				if (page.getTotalRowCount() > 0) {
					list = new LinkedList<WishListItemVO>();
					WishListItemVO vo;
					for (WishListItem item : items.getData()) {
						vo = new WishListItemVO();
						vo.setItem(item);
						vo.setProduct(productService.getProduct(item
								.getProductId()));
						vo.setProductSalePrice(productService
								.getProductFirstSalePrice(item.getProductId()));
						//vo.setShowCategoryName(productService.getShowCategoryNameOfProduct(item.getProductId()));
						vo.setShowCategoryName("category");//使用假类别名称。商品详细页面URL已经重定向
						list.add(vo);
					}
				}
			}
			if (list == null) {
				if (searchCriteria.size() <= 1) {
					showInfoType = 1;
				} else if (searchCriteria.size() > 1) {
					showInfoType = 2;
				}
			}
		} else {
			if (beginDate != null || endDate != null) {
				showInfoType = 2;
			} else {
				showInfoType = 1;
			}
		}

		request.setAttribute("showInfoType", showInfoType);
		request.setAttribute("wishListPageList", list);
		request.setAttribute("wishListPage", page);
		request.setAttribute("categories", categories);
		request.setAttribute("selectCategoryId", categoryId);
		request.setAttribute("beginDate", beginDate);
		request.setAttribute("endDate", endDate);
		setNavigation(NAV_MyWishList);
		return SUCCESS;
	}

	/**
	 * 移除收藏夹的商品
	 * 
	 * @return
	 */
	public void removeProductFromWishList() {
		Integer customerId = this.getLoginedUserBuyer().getId();
		Integer productId = paramInt("productId", 0);
		String fm = "[{\"result\":%1$s}]";
		int result = 0;
		if (productId > 0) {
			try {
				shoppingService
						.removeProductFromWishList(customerId, productId);
				result = 1;
			} catch (Exception e) {
			}
		}
		try {
			returnJson(String.format(fm, result));
		} catch (IOException e) {
		}
	}

	/**
	 * 添加商品至收藏夹
	 */
	public void addToWishList() {
		int productId = paramInt("productId", 0);
		Customer user = this.getLoginedUserBuyer();
		String fm = "[{\"result\":%1$s}]";
		int result = 0;
		if (user == null) {
			result = -1;
		} else {
			if (productId > 0) {
				try {
					Integer id = shoppingService.addProductToWishList(user
							.getId(), productId);
					if (id != null) {
						result = 1;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		try {
			returnJson(String.format(fm, result));
		} catch (IOException e) {
		}
	}

	private void setNavigation(String navKey) {
		request.setAttribute("NavHover", navKey);
	}

	private static boolean categoryIsVisible(ShowCategory category) {
		if (true != category.getIsVisible()) {
			return false;
		}
		ShowCategory parent = category.getParent();
		if (parent != null) {
			if (true != parent.getIsVisible()) {
				return false;
			}
			if (parent.getParent() != null) {
				if (true != parent.getParent().getIsVisible()) {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean containCategory(List<ShowCategory> categories,
			int categoryId) {
		for (ShowCategory category : categories) {
			if (category.getId().equals(categoryId)) {
				return true;
			}
		}
		return false;
	}
}

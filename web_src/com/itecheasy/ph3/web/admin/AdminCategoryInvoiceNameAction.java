package com.itecheasy.ph3.web.admin;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itecheasy.ph3.category.CategoryService;
import com.itecheasy.ph3.category.ShowCategory;
import com.itecheasy.ph3.web.AdminBaseAction;

public class AdminCategoryInvoiceNameAction extends AdminBaseAction {
	private static final long serialVersionUID = 2889954231354L;
	private CategoryService categoryService;

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public String doCategoryInvoiceName() {
		String invoiceName = null;
		List<ShowCategory> Level2Categories = null;
		Map<Integer, String> invoiceNameMap = null;
		Level2Categories = categoryService.getVisibleLevel2ShowCategories();
		sortCategory(Level2Categories);
		invoiceNameMap = new HashMap<Integer, String>();
		for (ShowCategory item : Level2Categories) {
			invoiceName = categoryService.getCategoryCustomsInvoiceName(item
					.getId());
			invoiceNameMap.put(item.getId(), invoiceName);
		}
		request.setAttribute("invoiceNameMap", invoiceNameMap);
		request.setAttribute("Level2Categories", Level2Categories);
		return SUCCESS;
	}

	public void saveCategoryInvoiceName() {

		String invoiceName = param("invoiceName");
		Integer categoryId = paramInt("categoryId", 0);
		String fm = "[{\"result\":%1$s}]";
		int result = 0;
		if (categoryId != null && categoryId > 0) {
			if (categoryId > 0) {
				categoryService.setCategoryCustomsInvoiceName(categoryId,
						invoiceName);
				result = 1;
			}
		}
		try {
			returnJson(String.format(fm, result));
		} catch (IOException e) {
		}
	}

	public String saveAllCategoryInvoiceName() {
		Integer[] categoryIds = paramInts("categoryId");
		String[] invoiceNames = request.getParameterValues("invoiceName");
		Integer showCategoryId = null;
		if (categoryIds != null && categoryIds.length > 0) {
			String customsInvoiceName = null;
			for (int i = 0; i < invoiceNames.length; i++) {
				showCategoryId = categoryIds[i];
				customsInvoiceName = invoiceNames[i];
				if (showCategoryId > 0) {
					categoryService.setCategoryCustomsInvoiceName(
							showCategoryId, customsInvoiceName);
				}
			}
		}
		this.setMessageInfo("showSuccess");
		return SUCCESS;
	}

	private static void sortCategory(List<ShowCategory> categories) {
		Collections.sort(categories, new Comparator<ShowCategory>() {
			@Override
			public int compare(ShowCategory o1, ShowCategory o2) {
				if (o1.getParent().getOrderIndex() == o2.getParent()
						.getOrderIndex()) {
					return o1.getOrderIndex().compareTo(o2.getOrderIndex());
				} else {
					return o1.getParent().getOrderIndex().compareTo(o2.getParent().getOrderIndex());
				}
			}
		});
	}
}
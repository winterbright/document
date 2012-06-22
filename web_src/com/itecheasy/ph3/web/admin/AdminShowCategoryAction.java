package com.itecheasy.ph3.web.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.category.CategoryService;
import com.itecheasy.ph3.category.ShowCategory;
import com.itecheasy.ph3.web.AdminBaseAction;
import com.itecheasy.ph3.web.WebConfig;
import com.itecheasy.ph3.web.exception.AppException;
import com.itecheasy.ph3.web.utils.StrUtils;
import com.itecheasy.ph3.web.utils.json.JsonUtil;

public class AdminShowCategoryAction extends AdminBaseAction {
	private static final long serialVersionUID = 1L;

	private ShowCategory bean;
	private Integer categoryId;
	private Integer subCategoryId;
	private Integer id;
	private File upload;
	private String uploadFileName;;
	private CategoryService categoryService;

	/**
	 * 所有展示类别
	 * 
	 * @return
	 */
	public String showCategoryList() {
		List<ShowCategory> rootList = categoryService.getRootShowCategories();
		List<ShowCategory> subList = null;
		List<ShowCategory> categoryList = null;
		request.setAttribute("rootList", rootList);
		if (isIntegerEmpty(categoryId) && isIntegerEmpty(subCategoryId)) {
			request.setAttribute("categoryList", rootList);
			return SUCCESS;
		} else if (isNotIntegerEmpty(categoryId) && isIntegerEmpty(subCategoryId)) {
			categoryList = categoryService.getSubShowCategories(categoryId);
			subList = categoryService.getSubShowCategoriesOfNotProductType(categoryId);
		} else {
			categoryList = categoryService.getSubShowCategories(subCategoryId);
			subList = categoryService.getSubShowCategoriesOfNotProductType(categoryId);
		}
		request.setAttribute("categoryList", categoryList);
		request.setAttribute("subList", subList);
		return SUCCESS;
	}

	/**
	 * 更新排序位置
	 * 
	 * @return
	 */
	public String updateOrdrIndex() throws AppException {
		Integer id = paramInt("id", 0);
		Integer orderIndex = paramInt("orderIndex", 0);
		if (categoryService.getShowCategory(id) == null)
			throw new AppException("展示类别不存在！");

		categoryService.setShowCategoryOrderIndex(id, orderIndex);

		setMessageInfo("ORDER_INDEX_SUCCESS");
		return SUCCESS;
	}

	/**
	 * 保存展示类别
	 * 
	 * @return
	 */
	public String addShowCateory() {
		if (bean == null) {
			return SUCCESS;
		}
		File imgFile = null;
		try {
			imgFile = upLoadFile(upload, uploadFileName);
		} catch (AppException e) {
			doAddShowCateory();
			setMessageInfo(e.getMessage());
			return INPUT;
		}
		if (bean.getCategoryType() == 2) {
			bean.setShowMode(null);
		}
		if (isNotIntegerEmpty(subCategoryId)) {
			bean.setParent(categoryService.getShowCategory(subCategoryId));
		} else if (isNotIntegerEmpty(categoryId)) {
			bean.setParent(categoryService.getShowCategory(categoryId));
		}
		if(bean.getDescription() != null){
			String description  = StrUtils.filterHtmlNote(bean.getDescription());
			bean.setDescription(StrUtils.filterHtmlScript(description));
		}
		
		try {
			categoryService.addShowCategory(bean, imgFile);
		} catch (BussinessException e) {
			setMessageInfo("ERROR_CATEGORY_TYPE_NOT_MATCH");
		}
		return SUCCESS;
	}

	/**
	 * 添加展示类别
	 * 
	 * @return
	 */
	public String doAddShowCateory() {
		List<ShowCategory> rootList = new ArrayList<ShowCategory>();
		Integer level = 0; // 当前节点 0为根节点，1为一级节点，以此类推
		if (isNotIntegerEmpty(categoryId)) {
			rootList.add(categoryService.getShowCategory(categoryId));
			level = 1;
		}
		if (isNotIntegerEmpty(subCategoryId)) {
			ShowCategory  subCategory =	categoryService.getShowCategory(subCategoryId);
			if(subCategory.getCategoryType().equals(CategoryService.CATEGORY_TYPE_PRODUCT)){
				setMessageInfo("ERROR_CATEGORY_TYPE_PRODUCT");
				return "list";
			}
			rootList.add(subCategory);
			level = 2;
		}
		//Collections.reverse(rootList);
		request.setAttribute("rootList", rootList);
		request.setAttribute("level", level);
		return SUCCESS;
	}

	/**
	 * 编辑展示类别
	 * 
	 * @return
	 */
	public String doEditShowCateory() {
		List<ShowCategory> rootList = new ArrayList<ShowCategory>();
		if (id != null && id != 0) { // edit
			ShowCategory currentShowCategory = categoryService
					.getShowCategory(id); // 当前类别
			bean = currentShowCategory;
			rootList.add(currentShowCategory); // 添加当前类别
			while (true) {
				if (currentShowCategory.getParent() == null) {
					break;
				}
				currentShowCategory = categoryService
						.getShowCategory(currentShowCategory.getParent().getId());
				if (currentShowCategory == null) {
					break;
				}
				rootList.add(currentShowCategory);
			}
			Collections.reverse(rootList);
			request.setAttribute("rootList", rootList);
		}
		return SUCCESS;
	}

	/**
	 * 编辑展示类别
	 * 
	 * @return
	 */
	public String editShowCateory() {
		File imgFile = null;
		if (upload != null) {
			try {
				imgFile = upLoadFile(upload, uploadFileName);
			} catch (AppException e) {
				setId(bean.getId());
				doEditShowCateory();
				setMessageInfo(e.getMessage());
				return INPUT;
			}
		}
		if(bean.getDescription() != null){
			String description  = StrUtils.filterHtmlNote(bean.getDescription());
			bean.setDescription(StrUtils.filterHtmlScript(description));
		}
		categoryService.updateShowCategory(bean, imgFile);
		return SUCCESS;
	}

	/**
	 * 屏蔽展示类别
	 * 
	 * @return
	 */
	public String hideCateory() {
		Integer categoryId = paramInt("id", 0);
		categoryService.disableShowCategory(categoryId);
		setMessageInfo("SHOW_SUCCESS");
		return SUCCESS;
	}

	/**
	 * 显示展示类别
	 * 
	 * @return
	 */
	public String showCateory() {
		Integer categoryId = paramInt("id", 0);
		categoryService.enableShowCategory(categoryId);
		setMessageInfo("SHOW_SUCCESS");
		return SUCCESS;
	}

	/**
	 * 删除展示类别
	 * 
	 * @return
	 */
	public String deleteShowCateory() {
		Integer categoryId = paramInt("id", 0);
		try {
			categoryService.deleteShowCategory(categoryId);
		} catch (BussinessException e) {
			setMessageInfo("ERROR_HAS_SUBCATEGORY");
			return SUCCESS;
		}
		setMessageInfo("DELETE_SUCCESS");
		return SUCCESS;
	}

	/**
	 * AJAX获取二级展示类别
	 * 
	 * @return
	 */
	public void doSubShowCategories() throws IOException {
		Integer categoryId = paramInt("categoryId", 0);
		if (categoryId == 0)
			return;
		
		List<ShowCategory> showCategoryList = categoryService
				.getSubShowCategoriesOfNotProductType(categoryId);
		String[] excludes = new String[] { "parent", "description",
				"imageName", "categoryType", "showMode" };
		String jsonString = JsonUtil.obj2String(showCategoryList, null,
				excludes, null);
		this.returnJson(jsonString);
	}

	public File upLoadFile(File upload, String uploadFileName)
			throws AppException {
		String targetDirectory = WebConfig.getInstance().get("category_image_save_path");
		if (upload == null) {
			throw new AppException("请选择要上传的文件");
		}
		String fileExt = uploadFileName.substring(uploadFileName.lastIndexOf(".")).toLowerCase();
		if (!".jpg".equalsIgnoreCase(fileExt) && !".gif".equalsIgnoreCase(fileExt)) {
			throw new AppException("文件类型错误，只能上传jpg,gif");
		}
		if ((upload.length() / 1024) > 200) {
			throw new AppException("文件大小必须小于200K");
		}
		String uuid = UUID.randomUUID().toString();
		File target = new File(targetDirectory, uuid + fileExt);
		try {
			FileUtils.copyFile(upload, target);
			return target;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Boolean isIntegerEmpty(Integer mumber) {
		return mumber == null || mumber <= 0;
	}

	private Boolean isNotIntegerEmpty(Integer mumber) {
		return !isIntegerEmpty(mumber);
	}

	public ShowCategory getBean() {
		return bean;
	}

	public void setBean(ShowCategory bean) {
		this.bean = bean;
	}

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getSubCategoryId() {
		return subCategoryId;
	}

	public void setSubCategoryId(Integer subCategoryId) {
		this.subCategoryId = subCategoryId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}
}

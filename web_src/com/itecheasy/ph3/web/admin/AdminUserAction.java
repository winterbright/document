package com.itecheasy.ph3.web.admin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.xwork.StringUtils;

import com.itecheasy.common.PageList;
import com.itecheasy.ph3.BussinessException;
import com.itecheasy.ph3.adminuser.AdminUserService;
import com.itecheasy.ph3.adminuser.Function;
import com.itecheasy.ph3.adminuser.FunctionGroup;
import com.itecheasy.ph3.adminuser.User;
import com.itecheasy.ph3.adminuser.AdminUserService.UserSearchCriteria;
import com.itecheasy.ph3.web.AdminBaseAction;
import com.itecheasy.ph3.web.utils.SessionUtils;

public class AdminUserAction extends AdminBaseAction {
	private static final long serialVersionUID = 887954231354L;
	private AdminUserService ph3AdminUserService;

	private int userId;
	private int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setPh3AdminUserService(AdminUserService ph3AdminUserService) {
		this.ph3AdminUserService = ph3AdminUserService;
	}

	/**
	 * 登录页面
	 */
	public String doLogin() {
		return SUCCESS;
	}

	/**
	 * 登录请求
	 */
	public String login() {
		String workNumber = this.param("workNumber");
		String password = this.param("password");
		String verifyCode = this.param("verifyCode");
		request.setAttribute("workNumber", workNumber);
		String objVerifyCode = SessionUtils.getVerifyCode(request);
		if (objVerifyCode == null
				|| !(objVerifyCode).toUpperCase().equals(
						verifyCode.toUpperCase())) {
			this.setMessageInfo("ERROR_VERIFYCODE");
			return "fial";
		}
		if (StringUtils.isEmpty(workNumber) || StringUtils.isEmpty(password)) {
			this.setMessageInfo("ERROR_USER_FAIL_LOGIN");
			return "fial";
		}
		try {
			ph3AdminUserService.login(workNumber, password, request
					.getRemoteAddr());
		} catch (BussinessException e) {
			this.setMessageInfo(e.getErrorMessage());
			return "fial";
		}
		SessionUtils.setLoginedAdminUser(request, ph3AdminUserService
				.getUserByWorkNumber(workNumber));
		return SUCCESS;
	}

	public String logout() {
		if (this.getLoginUserAdmin() == null)
			return SUCCESS;
		ph3AdminUserService.logout(getLoginUserAdmin().getId());
		SessionUtils.removeLoginedAdminUser(request);
		return SUCCESS;
	}

	public String doUserList() {
		String workNo = param("workNo");
		String email = param("email");
		int status = paramInt("status", 1);
		Map<UserSearchCriteria, Object> searchCriteria = new HashMap<UserSearchCriteria, Object>();
		if (workNo != null && workNo.trim().length() > 0) {
			searchCriteria.put(UserSearchCriteria.WORK_NUMBER, workNo.trim());
		}
		if (email != null && email.trim().length() > 0) {
			searchCriteria.put(UserSearchCriteria.EMAIL, email.trim());
		}
		if (status > -1) {
			searchCriteria.put(UserSearchCriteria.STATUS, status == 1);
		}

		// List<SearchOrder<UserSearchOrder>> searchOrder = new
		// ArrayList<SearchOrder<UserSearchOrder>>();
		// searchOrder.add(new SearchOrder<UserSearchOrder>(
		// UserSearchOrder.CREATED_DATE, false));
		PageList<User> pageList = ph3AdminUserService.searchUsers(1,
				Integer.MAX_VALUE, searchCriteria, null);
		getRequest().setAttribute("userList", pageList.getData());
		getRequest().setAttribute("workNo", workNo);
		getRequest().setAttribute("email", email);
		getRequest().setAttribute("status", status);
		return SUCCESS;
	}

	/**
	 * 激活用户
	 */
	public String enableUser() {
		Integer userId = paramInt("userId", 0);
		if (userId > 0) {
			ph3AdminUserService.enableUser(userId);
		}
		return SUCCESS;
	}

	/**
	 * 屏蔽用户
	 */
	public String disableUser() {
		Integer userId = paramInt("userId", 0);
		if (userId > 0) {
			ph3AdminUserService.disableUser(userId);
		}
		return SUCCESS;
	}

	/**
	 * 用户功能
	 */
	public String doUserFunction() {
		Integer userId = paramInt("userId", 0);
		User user = null;
		if (userId > 0) {
			user = ph3AdminUserService.getUser(userId);
		}
		List<FunctionGroup> fGroups = ph3AdminFunctionService
				.getEnabledFunctionGroups();
		if (fGroups != null && fGroups.size() > 0) {
			List<Integer> selectedFunIds = null;
			if (user != null) {
				List<Function> selectedFuns = ph3AdminFunctionService
						.getEnabledFunctionsByUser(userId);
				if (selectedFuns != null && selectedFuns.size() > 0) {
					selectedFunIds = new LinkedList<Integer>();
					for (Function fun2 : selectedFuns) {
						selectedFunIds.add(fun2.getId());
					}
				}
			}
			for (FunctionGroup group : fGroups) {
				List<Function> fFuns = ph3AdminFunctionService
						.getEnabledFunctionsByGroup(group.getId());
				if (selectedFunIds != null && fFuns != null && fFuns.size() > 0) {
					for (Function fun : fFuns) {
						if (selectedFunIds.contains(fun.getId())) {
							fun.setIsCheck(true);
						}
					}
				}
				group.setFunctions(fFuns);
			}
		}
		getRequest().setAttribute("user", user);
		getRequest().setAttribute("functionGroupList", fGroups);
		return SUCCESS;
	}

	/**
	 * 增加用户
	 */
	public String addUser() {
		String workNo = param("newWorkNo");
		if (workNo != null && workNo.trim().length() > 0) {
			try {
				setUserId(ph3AdminUserService.addUser(workNo));
			} catch (BussinessException e) {
				String err = e.getErrorMessage();
				if (AdminUserService.ERROR_USER_EXISTS.equals(err)) {
					setMessageInfo("1");
				} else if (AdminUserService.ERROR_USER_FAIL_GET_OA.equals(err)) {
					setMessageInfo("2");
				}
				return ERROR;
			}
		}
		return SUCCESS;
	}

	public String saveUserFunction() {
		Integer userId = paramInt("userId", 0);
		Integer[] ids = paramInts("functionIds");
		if (userId > 0) {
			ph3AdminFunctionService.bindFunctionToUser(userId, ids);
		}
		return SUCCESS;
	}

}

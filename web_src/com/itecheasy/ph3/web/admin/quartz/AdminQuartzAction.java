package com.itecheasy.ph3.web.admin.quartz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.itecheasy.ph3.web.AdminBaseAction;
import com.itecheasy.ph3.web.exception.AppException;

public class AdminQuartzAction extends AdminBaseAction{
	
	private static final long serialVersionUID = 7060303844384165674L;
	private Scheduler scheduler;
	
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	private static String ASYN_EMAIL_CRON ="asynEmailCron";
	
	private static String ASYN_CANCELORDER_CRON ="asynCancelOrderCron";
	
	public String doQuartz(){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		try {
			Map<String, Object> map1 = new HashMap<String, Object>();
			map1.put("name", "自动发送邮件服务");
			map1.put("id", ASYN_EMAIL_CRON);
			int emailState=  scheduler.getTriggerState(ASYN_EMAIL_CRON, Scheduler.DEFAULT_GROUP);
			map1.put("stateName", QuartzState.getState(emailState));
			map1.put("state", emailState);
			
			int cancelOrderState=  scheduler.getTriggerState(ASYN_CANCELORDER_CRON, Scheduler.DEFAULT_GROUP);
			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("name", "自动取消订单服务");
			map2.put("id", ASYN_CANCELORDER_CRON);
			map2.put("stateName", QuartzState.getState(cancelOrderState));
			map2.put("state", cancelOrderState);
			
			list.add(map1);
			list.add(map2);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		request.setAttribute("list", list);
		
		return SUCCESS;
	}
	
	/**
	 * 暂停
	 * @return
	 * @throws AppException
	 */
	public String pauseTrigger() throws AppException {
		String id = param("id");
		if(StringUtils.isEmpty(id))
			return SUCCESS;
		try {
			scheduler.pauseTrigger(id, Scheduler.DEFAULT_GROUP);
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage());
		}
		return SUCCESS;
	} 
	
	/**
	 * 开始
	 * @return
	 * @throws AppException
	 */
	public String resumeTrigger() throws AppException {
		String id = param("id");
		if(StringUtils.isEmpty(id))
			return SUCCESS;
		try {
			scheduler.resumeTrigger(id, Scheduler.DEFAULT_GROUP);
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage());
		}
		return SUCCESS;
	} 
}

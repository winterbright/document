package com.itecheasy.ph3.web.admin.quartz;

public class QuartzState {

	final static int STATE_BLOCKED = 4;
	
	final static int STATE_COMPLETE = 2;
	
	final static int STATE_ERROR = 3;
	
	final static int STATE_NONE = -1;
	
	final static int STATE_NORMAL = 0;
	
	final static int STATE_PAUSED = 1;
	
	public static String getState(int i){
		String state = null;
		switch (i) {
		
		case STATE_BLOCKED:
			state ="堵塞";
			break;
		case STATE_COMPLETE:
			state ="完成";
			break;
		case STATE_ERROR:
			state ="错误";
			break;
		case STATE_NONE:
			state ="无";
			break;
		case STATE_NORMAL:
			state ="运行中";
			break;
		case STATE_PAUSED:
			state ="已停止";
			break;
		default:
			state ="状态错误";
			break;
		}
		return state;
	}
}

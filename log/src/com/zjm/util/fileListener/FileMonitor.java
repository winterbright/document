package com.zjm.util.fileListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @alias
 * @author zjm
 *
 * 2012-4-3
 */
public class FileMonitor {

	 private Timer       timer_;
	 private HashMap     files_;       // File -> Long
	 private Collection  listeners_;   // of WeakReference(FileListener)
	 
	 /**
	 * 
	 */
	public FileMonitor(long pollingInterval) {
		files_ = new HashMap();
		listeners_ = new ArrayList();
		timer_ = new Timer(true);
//		timer_.schedule(new FileMonitorNotifier(), time)
	}
	private class FileMonitorNotifier extends TimerTask{
		
		/** 
		 * 
		 */
		@Override
		public void run() {
			
		}
		
	}
}


package com.itecheasy.ph3.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

public class SessionContext {
	 private static Map<String,HttpSession> mymap = new HashMap<String,HttpSession>();

	    public static synchronized void AddSession(HttpSession session) {
	        if (session != null) {
	            mymap.put(session.getId(), session);
	        }
	    }

	    public static synchronized void DelSession(HttpSession session) {
	        if (session != null) {
	            mymap.remove(session.getId());
	        }
	    }

	    public static synchronized HttpSession getSession(String sessionId) {
	        if (sessionId == null) 
	        	return null;
	        return (HttpSession) mymap.get(sessionId);
	    }
}

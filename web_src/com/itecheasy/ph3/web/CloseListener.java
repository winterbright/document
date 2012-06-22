package com.itecheasy.ph3.web;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.mail.MessagingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.itecheasy.ph3.email.EmailService;

public class CloseListener implements ServletContextListener{
	private static String mailHost;
	private static int port;
	private static String user ;
	private static String password;
	private static String displayName;
	private static String service;
	public  void setEmailService(EmailService emailService) {
		mailHost = emailService.getEmailSmtpHost();
		port = emailService.getEmailSmtpPort();
		user = emailService.getEmailSmtpUsername();
		password = emailService.getEmailSmtpPassword();
		displayName = emailService.getServiceDisplayName();
		service = emailService.getServiceMail();
	}

	

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		Logger logger = Logger.getLogger("PH3");
		logger.error("tomcat 关闭");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String mailbody = df.format(new Date())+ " tomcat 停掉";
		SendEmail themail = new SendEmail(mailHost);
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress().toString();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		themail.setNeedAuth(true);
		
		try {
			themail.setSubject(ip + " tomcat 停了");
			themail.setBody(mailbody) ;
			themail.setTo(WebConfig.getInstance().getTomcatStopEmail()) ;
			themail.setFrom(service,displayName);
		} catch (Exception e) {
			logger.error("tomcat 关闭邮件设置失败",e);
		}
		themail.setNamePass(user, password);

		try {
			themail.sendout();
			logger.info("tomcat 关闭  邮件发送成功");
		} catch (MessagingException e) {
			logger.error("tomcat 关闭邮件发送失败",e);
		}
	}
		

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
	}

}

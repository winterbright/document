package com.itecheasy.ph3.web;

import java.util.*;

import javax.mail.*;

import javax.mail.Flags.Flag;
import javax.mail.internet.*;

import java.util.Date;

import javax.activation.*;

import java.io.*;

public class SendEmail {

	private MimeMessage mimeMsg; // MIME邮件对象

	private Session session; // 邮件会话对象

	private Properties props; // 系统属性

	private boolean needAuth = false; // smtp是否需要认证

	private String username = ""; // smtp认证用户名和密码

	private String password = "";

	private Multipart mp; // Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成MimeMessage对象

	public SendEmail(String smtp) {
		setSmtpHost(smtp);
		createMimeMessage();
	}


	public void setSmtpHost(String hostName) {
		if (props == null)
			props = System.getProperties(); // 获得系统属性对象
		props.put("mail.smtp.host", hostName); // 设置SMTP主机
	}

	public boolean createMimeMessage(){
		try {
			session = Session.getDefaultInstance(props, null); // 获得邮件会话对象
		}catch (Exception e) {
			System.err.println("获取邮件会话对象时发生错误！" + e);
			return false;
		}
		System.out.println("准备创建MIME邮件对象！");
		try {
			mimeMsg = new MimeMessage(session); // 创建MIME邮件对象
			mp = new MimeMultipart();
			return true;
		}catch (Exception e) {
			System.err.println("创建MIME邮件对象失败！" + e);
			return false;
		}
	}

	public void setNeedAuth(boolean need) {
		if (props == null)
			props = System.getProperties();
		if (need) {
			props.put("mail.smtp.auth", "true");
		} else {
			props.put("mail.smtp.auth", "false");
		}
	}

	public void setNamePass(String name, String pass) {
		username = name;
		password = pass;
	}

	public void setSubject(String mailSubject) throws MessagingException {
			mimeMsg.setSubject(mailSubject);
	}
	public void setBody(String mailBody) throws MessagingException {
			BodyPart bp = new MimeBodyPart();
			bp.setContent("" + mailBody, "text/html;charset=UTF-8");
			mp.addBodyPart(bp);
	}

	public void addFileAffix(String filename) throws MessagingException {
		BodyPart bp = new MimeBodyPart();
		FileDataSource fileds = new FileDataSource(filename);
		bp.setDataHandler(new DataHandler(fileds));
		bp.setFileName(fileds.getName());
		mp.addBodyPart(bp);
	}
	public void setFrom(String from,String displayName) throws UnsupportedEncodingException, MessagingException {
		InternetAddress ia = new InternetAddress(from);
		if(displayName != null){
			ia.setPersonal(displayName);
		}
		mimeMsg.setFrom(ia); // 设置发信人

	}

	public void setTo(String to) throws AddressException, MessagingException {
		mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
	}

	public boolean setCopyTo(String copyto){
		if (copyto == null)
			return false;
		try {
			mimeMsg.setRecipients(Message.RecipientType.CC, (Address[]) InternetAddress.parse(copyto));
			return true;
		}catch (Exception e)
		{
			return false;
		}
	}
	public void sendout() throws MessagingException{
			mimeMsg.setContent(mp);
			mimeMsg.saveChanges();
			
			Session mailSession = Session.getInstance(props, null);
			Transport transport = mailSession.getTransport("smtp");
			transport.connect((String) props.get("mail.smtp.host"), username, password);
			transport.sendMessage(mimeMsg, mimeMsg.getRecipients(Message.RecipientType.TO));
			// transport.send(mimeMsg);
			transport.close();
	}


	public static void main(String[] args) throws Exception{

		String mailbody = "" + "csdn";
		SendEmail themail = new SendEmail("mail.pandahallstock.com");

		themail.setNeedAuth(true);

		themail.setSubject("标题") ;

		themail.setBody(mailbody);
	

		themail.setTo("taozt@shops100.cn");


		themail.setFrom("system@pandahallstock.com","com");

//
//		if (themail.addFileAffix("c:\\boot.ini") == false)
//			return;

		themail.setNamePass("system@pandahallstock.com", "p3#system#mail@pwd");

		themail.sendout() ;
		

	}

}
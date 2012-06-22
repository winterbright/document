package com.itecheasy.ph3.web.buyer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import com.itecheasy.ph3.email.EmailService;
import com.itecheasy.ph3.web.BaseAction;
import com.itecheasy.ph3.web.WebConfig;
/**
 * google feed xml 文件下载
 * @author lih
 *
 */
public class GooleProductFeedAction extends BaseAction {
	private static final long serialVersionUID = -309673363735293257L;
	private String path = WebConfig.getInstance().get("gooleProductFeed.filePath");
	private EmailService emailService;

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	/**
	 * goole feed 下载
	 */
	public void gooleProductFeed() {
		try {
			PrintWriter pw = response.getWriter();
			File file = new File(path + "GoogleFeed.xml");
			response.setContentType("application/x-download");
			response.addHeader("Content-Disposition", "attachment;filename=" + "GoogleFeed.xml");
			if (file.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String temp = "";
				while (temp != null) {
					temp = reader.readLine();
					if (temp != null && !"".equals(temp)) {
						pw.write(temp);
					}
					pw.flush();
				}
				pw.close();
				reader.close();
			}else{
				throw new Exception("googleFeed.xml文件不存在！");
			}
		} catch (Exception e) {
			try {
				Properties pro = new Properties();
				InputStream is = GooleProductFeedAction.class.getResourceAsStream("/deploy_config.properties");
				pro.load(is);
				String adminEmail = pro.getProperty("admin.email");
				emailService.sendEmail(adminEmail, "goole feed 下载失败", "具体报错如下：" + e.getMessage());
				is.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
}

package com.itecheasy.ph3.web;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.xwork.StringUtils;

import com.itecheasy.ph3.web.utils.SessionUtils;


public class ValidateCodeServlet extends HttpServlet {

	private static final long serialVersionUID = 3889954231354L;
	// 验证码图片的宽度
	private int width;
	// 验证码图片的高度
	private int height;
	// 验证码字符个数
	private int codeCount; 

	private int x = 0;
	// 字体高度
	private int fontHeight;
	private int codeY;

	char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',  'J', 'K','M', 'N', 'P','R',
			'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z','2', '3', '4', '5', '6', '7', '8', '9' };

	/**
	 * 初始化验证图片属性
	 */
	@Override
	public void init() throws ServletException {
		initData(69,28);
	}
	
	public void initData(int w, int h){
		width =w;
		height =h;
		codeCount = 4;
		x = width / (codeCount + 1);
		fontHeight = height - 2;
		codeY = height - 4;
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
			java.io.IOException {
		String w = req.getParameter("w");
		String h = req.getParameter("h");
		if(StringUtils.isNotEmpty(w) && StringUtils.isNotEmpty(h)){
			initData(Integer.parseInt(w),Integer.parseInt(h));
		}
		
		BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		Font font = new Font("Arial", Font.PLAIN, fontHeight);
		g.setFont(font);
		g.setColor(Color.BLACK);
		Random random = new Random();
		for (int i = 0; i < 3; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}

		StringBuffer randomCode = new StringBuffer();
		int red = 0, green = 0, blue = 0;

		for (int i = 0; i < codeCount; i++) {
			String strRand = String.valueOf(codeSequence[random.nextInt(30)]);
			red = random.nextInt(200);
			green = random.nextInt(180);
			blue = random.nextInt(255);
			g.setColor(new Color(red, green, blue));
			g.drawString(strRand,i*x+x/2, codeY);
			randomCode.append(strRand);
		}
		SessionUtils.setVerifyCode(req, randomCode.toString());
		resp.setHeader("Pragma", "no-cache");
		resp.setHeader("Cache-Control", "no-cache");
		resp.setDateHeader("Expires", 0);
		resp.setContentType("image/jpeg");
		ServletOutputStream sos = resp.getOutputStream();
		ImageIO.write(buffImg, "jpeg", sos);
		sos.close();
	}
}

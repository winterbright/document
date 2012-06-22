package com.itecheasy.ph3.web;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.xwork.StringUtils;
import org.apache.log4j.Logger;

public class ZoomServlet extends HttpServlet {
	private static final long serialVersionUID = 4889954231354L;
    private final static Logger log = Logger.getLogger(ZoomServlet.class);
	
	protected void service(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		
		String imgW = req.getParameter("w");
		String imgH = req.getParameter("h");
		String code = req.getParameter("code");
		if(StringUtils.isEmpty(code))
			return;
		File file = PictureAction.getFileByUUIDFromTempDir(code);
		if(file == null)
			return;
		String fileName = file.getName();
		int doPs = fileName.lastIndexOf(".");
		String fileExt = fileName.substring(fileName.lastIndexOf(".")+1);
		if(req.getRequestURI().endsWith("to_zoom_ico.zoom"))
			fileName = fileName.substring(0,doPs);
		BufferedImage outputObject = null;
		int w = Integer.parseInt(imgW);
		int h = Integer.parseInt(imgH);
		try {
			BufferedImage image = javax.imageio.ImageIO.read(file); 
			outputObject = resize(image,w,h);
		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException(e);
		}
		resp.setContentType(req.getContentType());
	    OutputStream os = resp.getOutputStream(); 
	    ByteArrayOutputStream bss =new ByteArrayOutputStream();   
	    ImageOutputStream imOut =ImageIO.createImageOutputStream(bss);   
	    ImageIO.write(outputObject,fileExt,imOut);   //scaledImage1为BufferedImage，jpg为图像的类型   
	    InputStream is =new ByteArrayInputStream(bss.toByteArray());
	
	    byte[] bs = new byte[1024];
	    int len;
	    while((len=is.read(bs))!=-1){
	       os.write(bs,0,len); 
	    }
	    os.flush();
	    is.close();  
	    os.close();  
    }
	
	public static BufferedImage resize(BufferedImage source, int targetW,int targetH) {
		int type = source.getType();
		BufferedImage target = null;
		double sx = (double) targetW / source.getWidth();
		double sy = (double) targetH / source.getHeight();
		if (sx > sy) {
			sx = sy;
			if(sx * source.getWidth() <1)
				targetW = 1;
			else
				targetW = (int) (sx * source.getWidth());
		} else {
			sy = sx;
			if(sy * source.getHeight() <1)
				targetH = 1;
			else
				targetH = (int) (sy * source.getHeight());
		}
		if (type == BufferedImage.TYPE_CUSTOM) {
			ColorModel cm = source.getColorModel();
			WritableRaster raster = cm.createCompatibleWritableRaster(targetW,targetH);
			boolean alphaPremultiplied = cm.isAlphaPremultiplied();
			target = new BufferedImage(cm, raster, alphaPremultiplied, null);
		} else
			target = new BufferedImage(targetW, targetH, type);
		
		Graphics2D g = target.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
		g.dispose();
		return target;
	}
	
}

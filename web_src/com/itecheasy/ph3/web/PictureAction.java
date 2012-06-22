package com.itecheasy.ph3.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.apache.log4j.Logger;

public class PictureAction extends BaseAction {
	private static final long serialVersionUID = 9889954231354L;
	private final static Logger log = Logger.getLogger(PictureAction.class);

	private File Filedata;

	private String Filename;

	public String getFilename() {
		return Filename;
	}

	public void setFilename(String filename) {
		Filename = filename;
	}

	public File getFiledata() {
		return Filedata;
	}

	public void setFiledata(File filedata) {
		Filedata = filedata;
	}

	public void uploadPicture() {
		if (Filedata != null) {
			try {
				String uuid = UUID.randomUUID().toString();
				// 获得文件名及路径
				File file = new File(WebConfig.getInstance().getPictureDirectory()+File.separator+uuid);
				if(!file.exists()){
					file.mkdirs();
				}
				file = new File(WebConfig.getInstance().getPictureDirectory()+File.separator+uuid+File.separator+Filename);
				this.copy(Filedata, file);
				response.getWriter().write(uuid);
			} catch (Exception e) {
				log.error(e);
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 拷贝文件
	 * 
	 * @param src
	 *            源文件
	 * @param dst
	 *            目的文件
	 */
	private static void copy(File src, File dst) {
		
		try {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = new BufferedInputStream(new FileInputStream(src));
				out = new BufferedOutputStream(new FileOutputStream(dst));
				byte[] buffer = new byte[1024 * 10];
				while (in.read(buffer) > 0) {
					out.write(buffer);
				}
			} finally {
				if (null != in) {
					in.close();
				}
				if (null != out) {
					out.close();
				}
			}
		} catch (IOException e) {
			log.error(e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 根据文件的临时名称从临时目录获取文件对象
	 * @param tempName 文件的临时名称 
	 * @return 文件对象 
	 */
	public static File getFileByTempNameFromTempDir(String  tempName){
		return new File(WebConfig.getInstance().getPictureDirectory()+ "/" + tempName);
	}
	
	/**
	 * 根据文件的临时名称从临时目录获取文件对象
	 * @param uuid 文件的临时uuid
	 * @return 文件对象 
	 */
	public static File getFileByUUIDFromTempDir(String  uuid){
		return new File(WebConfig.getInstance().getPictureDirectory()+ File.separator + uuid).listFiles()[0];
	}
	
	/**
	 * 根据文件的临时名称从临时目录移除文件对象
	 * @param tempName 文件的临时名称 
	 */
	public void deleteTempFile(String  tempName){
		new File(WebConfig.getInstance().getPictureDirectory()+ "/" + tempName).delete();
	}
}

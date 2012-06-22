package com.itecheasy.ph3.web.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

public class ResponseHelper {
	private static final String ENCODE = "UTF-8";

	public static HttpServletResponse getResponse() {
		return ServletActionContext.getResponse();
	}

	public static void downloadFile(HttpServletResponse response,
			String filePath, String saveFileName) {
		response.reset();
		response.setContentType("application/force-download");
		// response.setContentType("application/x-download");

		try {
			saveFileName = URLEncoder.encode(saveFileName, ENCODE);
		} catch (UnsupportedEncodingException e) {
		}
		response.addHeader("Content-Disposition", "attachment;filename="
				+ saveFileName);

		OutputStream outp = null;
		FileInputStream in = null;
		try {
			outp = response.getOutputStream();
			in = new FileInputStream(filePath);
			byte[] b = new byte[1024];
			int i = 0;
			while ((i = in.read(b)) > 0) {
				outp.write(b, 0, i);
			}
			outp.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}
			if (outp != null) {
				try {
					outp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				outp = null;
			}
		}
	}

	public static void downloadFile(HttpServletResponse response,
			FileInputStream inputStream, String saveFileName) {
		response.reset();
		response.setContentType("application/force-download");
		// response.setContentType("application/x-download");

		try {
			saveFileName = URLEncoder.encode(saveFileName, ENCODE);
		} catch (UnsupportedEncodingException e) {
		}
		response.addHeader("Content-Disposition", "attachment;filename="
				+ saveFileName);

		OutputStream outp = null;
		try {
			outp = response.getOutputStream();
			byte[] b = new byte[1024];
			int i = 0;
			while ((i = inputStream.read(b)) > 0) {
				outp.write(b, 0, i);
			}
			outp.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outp != null) {
				try {
					outp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				outp = null;
			}
		}
	}
}

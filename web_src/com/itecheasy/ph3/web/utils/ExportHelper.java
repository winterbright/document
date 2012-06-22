package com.itecheasy.ph3.web.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;

import com.itecheasy.ph3.common.ExcelUtils;

public class ExportHelper {

	public static void alterInfo(HttpServletResponse response, String info) {
		String alert = "<script>alert(\"" + info + "\"); window.close(); </script>";
		try {
			response.setCharacterEncoding("utf-8");
			response.getWriter().print(alert);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出Excel文件
	 * 
	 * @param response
	 * @param templatePath
	 *            Excel模板路径
	 * @param fileName
	 *            导出后的名称
	 * @param beans
	 *            导出时的数据
	 * @return 是否导出成功
	 */
	public static boolean exportExcel(HttpServletResponse response, String templatePath, String fileName, Map<String, Object> beans) {
		String templateFileName = ExcelUtils.getTemplateFileName(templatePath);
		OutputStream outStream = null;
		try {
			outStream = response.getOutputStream();
			Workbook excel = ExcelUtils.readExcel(templateFileName, beans);
			response.reset();
			response.setContentType("application/x-download");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			excel.write(outStream);
			outStream.flush();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
				}
				outStream = null;
			}
		}
	}

	public static void exportXml(HttpServletResponse response, String xml) {
		OutputStream outStream = null;
		try {
			PrintWriter pw = response.getWriter();
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setContentType("application/xml");
			pw.println(xml);
			pw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outStream != null) {
				try {
					outStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}
}

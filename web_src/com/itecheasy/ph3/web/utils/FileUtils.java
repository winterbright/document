package com.itecheasy.ph3.web.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FileUtils {
	public static final String UTF_8 = "UTF-8";

	/***************************************************************************
	 * 根据文件路径以字符流的方式读取文件内容
	 * 
	 * @param 文件完整路径
	 * @param 编码
	 * @return 返回文本内容
	 */
	public static String readFile(String sourceFileName, String encode) {
		BufferedReader reader = null;
		FileInputStream fr = null;
		StringBuilder sb = new StringBuilder();
		try {
			fr = new FileInputStream(sourceFileName);
			InputStreamReader is = new InputStreamReader(fr, encode);
			reader = new BufferedReader(is);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return sb.toString();
	}

	public static void writerFile(String sourceFileName, String content,
			boolean isAppend, String encode) {
		FileOutputStream stream = null; // provides file access
		OutputStreamWriter writer = null; // writes to the file
		try {
			stream = new FileOutputStream(sourceFileName, isAppend);
			writer = new OutputStreamWriter(stream, encode);
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	public static void copyFile() {

	}

	/***************************************************************************
	 * 
	 * @param sourceFileName
	 *            文件完整路径
	 * @param targetFilePath
	 */
	public static void moveFile(String sourceFileName, String targetFilePath) {
		File sourceFile = new File(sourceFileName);
		File targetFile = new File(targetFilePath);
		if (!sourceFile.exists())
			throw new RuntimeException(sourceFileName + "文件不存在");
		if (!targetFile.exists())
			targetFile.mkdirs();
		File newfile = new File(targetFilePath + File.separator
				+ sourceFile.getName());
		sourceFile.renameTo(newfile);
		if (sourceFile.exists())
			sourceFile.delete();
	}

	public static void deleteFile(String sourceFileName) {
		File sourceFile = new File(sourceFileName);
		if (sourceFile.exists())
			sourceFile.delete();
	}

	/***************************************************************************
	 * 获取目录下所有文件,按最后更新时间排序
	 * 
	 * @param sourcePath
	 * @param type
	 * @return
	 */
	public static List<File> getAllFile(String sourcePath,
			final String extension) {
		File file = new File(sourcePath);
		File[] files = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				String fn = pathname.getName();
				if (fn.lastIndexOf(".") > 0)
					return extension.equals(fn.substring(fn.lastIndexOf("."),
							fn.length()).toLowerCase());
				else
					return false;
			}
		});

		if (files == null)
			return new ArrayList<File>();

		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o1.lastModified() > o2.lastModified() ? 1 : 0;
			}
		});
		List<File> fileList = Arrays.asList(files);
		return fileList;
	}

	public static void main(String[] ags) {
		// List<File> fileList = getAllFile("F:\\xml");
		// for(File f : fileList)
		// System.out.println("abc:"+ f.getName());
		// deleteFile("F:\\xml\\a111.xml");
	}
}

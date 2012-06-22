package com.itecheasy.ph3.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.itecheasy.ph3.common.DeployProperties;

/**
 * Web配置文件帮助类
 */
public class WebConfig {
	private static Logger log = Logger.getLogger(WebConfig.class);

	public static final String TEMP_FILE_DIR = "temp_file_dir";
	
	private static final String PROPERTIE_FILE_NAME = "webconfig.properties";
	
	private static Logger logger = Logger.getLogger(DeployProperties.class);


	private static long lastModified = 0L;

	private  Properties properties = null;

	private static WebConfig instance = new WebConfig();;

	private File file = null;

	public static WebConfig getInstance() {
		return instance;
	}

	private WebConfig() {
		try {
			Resource resource = new ClassPathResource(PROPERTIE_FILE_NAME);
			file = resource.getFile();
			lastModified = file.lastModified();
			if (lastModified == 0) {
				logger.error(PROPERTIE_FILE_NAME + " file does not exist!");
			}
			properties = new Properties();
			properties.load(resource.getInputStream());

		} catch (IOException e) {
			logger.error("can not read config file " + PROPERTIE_FILE_NAME);
		}
		logger.info(PROPERTIE_FILE_NAME + " loaded.");
	}

	public final String get(String key) {
		return getProperty(key, StringUtils.EMPTY);
	}

	public final String getProperty(String key, String defaultValue) {
		long newTime = file.lastModified();
		if (newTime == 0) {
			return defaultValue;
		} else if (newTime > lastModified) {
			try {
				properties.clear();
				Resource resource = new ClassPathResource(PROPERTIE_FILE_NAME);
				properties.load(new FileInputStream(resource.getFile()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		lastModified = newTime;
		return properties.getProperty(key) == null ? defaultValue : properties
				.getProperty(key);
	}

	/**
	 * 获取系统存放图片的临时目录
	 * 
	 * @return 图片的临时目录
	 */
	public  String getPictureDirectory() {
		return get(WebConfig.TEMP_FILE_DIR);
	}
	
	public String getTomcatStopEmail(){
		return get("tomcat_stop_email");
	}

}

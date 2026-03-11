package com.bjsasc.avidm.mq.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

// 读取队列服务器的配置文件
public class QueueMap {
	protected final Logger logger = Logger.getLogger(getClass());

	private static QueueMap instance = new QueueMap();

	public synchronized static QueueMap getInstance() {
		return instance;
	}

	private QueueMap() {

	}

	public Properties loadConfig() {
		logger.info("开始读取队列映射配置文件：queue_map.properties...");

		String path = System.getProperty("AVIDM_HOME") + File.separator + "queue_map.properties";
		File f = new File(path);
		if (!f.exists()) {
			String msg = "AVIDM_HOME目录下queue_map.properties文件不存在，系统将退出！";
			logger.error(msg);
			throw new RuntimeException(msg);
		}

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		Properties props = new Properties();

		try {
			fis = new FileInputStream(f);
			bis = new BufferedInputStream(fis);
			props.load(bis);
		} catch (Exception e) {
			String msg = "读取配置文件：queue_map.properties，发生错误，系统将退出！";
			logger.error(msg, e);
			throw new RuntimeException(msg);
		} finally {
			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(fis);
		}

		logger.info("站点队列映射配置如下：");
		for (Object iid : props.keySet()) {
			Object queue = props.get(iid);
			logger.info("站点标识：" + iid + " <------> " + "队列名称：" + queue);
		}

		return props;
	}
}

package com.bjsasc.avidm.mq.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.rabbitmq.client.ConnectionFactory;

// 读取队列服务器的配置文件
public class Config {
	protected final Logger logger = Logger.getLogger(getClass());
	private static Config instance = new Config();

	final static String HOST = "host";

	final static String PORT = "port";

	final static String VHOST = "vhost";

	final static String USERNAME = "username";

	final static String PASSWORD = "password";

	final static String QUEUE_DC = "queue_dc";

	final static String QUEUE_LOG = "queue_dc_log";

	final static String QUEUE_MSG_META = "queue_dc_msg_meta";

	final static String QUEUE_SELF = "queue_self";

	final static String QUEUE_RETRY = "queue_retry";

	final static String QUEUE_SUSPENDED = "queue_suspended";

	private String username = ConnectionFactory.DEFAULT_USER;

	// 登录队列的密码
	private String password = ConnectionFactory.DEFAULT_PASS;

	// 队列服务器的host
	private String host = ConnectionFactory.DEFAULT_HOST;

	// 队列服务器的端口
	private int port = ConnectionFactory.DEFAULT_AMQP_PORT;

	private String vhost = "";

	private String dcQueue = "";

	private String selfQueue = "";

	private String retryQueue = "";

	private String suspendedQ = "";

	private String logQueue = "";

	private String msgMetaQueue = "";

	public synchronized static Config getInstance() {
		return instance;
	}

	private Config() {
		loadConfig();
	}

	private void loadConfig() {
		logger.info("开始读取配置文件：queue.properties...");

		String path = System.getProperty("AVIDM_HOME") + File.separator + "queue.properties";
		File f = new File(path);
		if (!f.exists()) {
			String msg = "AVIDM_HOME目录下queue.properties文件不存在，系统将退出！";
			logger.error(msg);
			//throw new RuntimeException(msg);
		}

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		Properties props = new Properties();

		try {
			fis = new FileInputStream(f);
			bis = new BufferedInputStream(fis);
			props.load(bis);
		} catch (Exception e) {
			String msg = "读取配置文件：queue.properties，发生错误，系统将退出！";
			logger.error(msg, e);
			//throw new RuntimeException(msg);
		} finally {
			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(fis);
		}

		host = props.getProperty(HOST);
		port = Integer.parseInt(props.getProperty(PORT));
		vhost = props.getProperty(VHOST);
		username = props.getProperty(USERNAME);
		password = props.getProperty(PASSWORD);
		dcQueue = props.getProperty(QUEUE_DC);
		selfQueue = props.getProperty(QUEUE_SELF);
		retryQueue = props.getProperty(QUEUE_RETRY);
		suspendedQ = props.getProperty(QUEUE_SUSPENDED);
		logQueue = props.getProperty(QUEUE_LOG);
		msgMetaQueue = props.getProperty(QUEUE_MSG_META);

		logger.info("队列配置内容如下：");
		logger.info("RabbitMQ服务器主机：" + host);
		logger.info("RabbitMQ服务器端口：" + port);
		logger.info("RabbitMQ服务器虚拟主机：" + vhost);
		logger.info("RabbitMQ服务器登录用户名：" + username);
		logger.info("数据中心队列为：" + dcQueue);
		logger.info("进展日志队列为：" + logQueue);
		logger.info("消息元数据队列为：" + msgMetaQueue);
		logger.info("本地站点队列为：" + selfQueue);
		logger.info("本地站点重试队列为：" + retryQueue);
		logger.info("本地站点消息挂起队列为：" + suspendedQ);
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getVhost() {
		return vhost;
	}

	public String getDcQueue() {
		return dcQueue;
	}

	public String getSelfQueue() {
		return selfQueue;
	}

	public String getRetryQueue() {
		return retryQueue;
	}

	public String getSuspendedQ() {
		return suspendedQ;
	}

	public String getLogQueue() {
		return logQueue;
	}

	public String getMsgMetaQueue() {
		return msgMetaQueue;
	}
}

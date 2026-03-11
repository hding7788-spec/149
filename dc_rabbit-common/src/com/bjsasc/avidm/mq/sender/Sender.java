package com.bjsasc.avidm.mq.sender;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.config.Config;
import com.bjsasc.avidm.mq.context.ConnectionContext;
import com.bjsasc.avidm.mq.message.MetaMessage;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

// 发送消息工具类
public class Sender {
	protected final Logger logger = Logger.getLogger(getClass());

	private static Sender instance = new Sender();

	ConnectionContext ctx = null;

	// 缺省发往的队列
	String defaultQ = "";

	// 站点和队列的映射
	Map<String, String> queues = new HashMap<String, String>();

	public synchronized static Sender getInstance() {
		return instance;
	}

	private Sender() {

	}

	public void init(ConnectionContext ctx, String queue) {
		this.ctx = ctx;
		this.defaultQ = queue;
	}

	// 添加站点和队列的映射
	public synchronized void initQueueMap(Properties props) {
		// 检查参数
		for (Object iid : props.keySet()) {
			Object queue = props.get(iid);
			queues.put(iid.toString(), queue.toString());
		}
	}

	// 发到缺省的目标队列
	public void send(JSONObject msg) throws Exception {
		sendToQueue(msg, defaultQ);
	}

	// 发送到目标站点的队列
	public void send(String siteNo, JSONObject msg) throws Exception {
		String queue = (String) this.queues.get(siteNo);
		sendToQueue(msg, queue);
	}

	// 发送日志到日志队列
	public void addLog(JSONObject msg) {
		Config config = Config.getInstance();
		String queue = config.getLogQueue();

		sendToQueue(msg, queue);
	}

	// 发送消息元数据信息到到消息元数据队列
	public void addMetaMessage(MetaMessage msg) {
		updateMetaMessage(msg);
	}

	// 更新消息元数据信息
	public void updateMetaMessage(MetaMessage msg) {
		Config config = Config.getInstance();
		String queue = config.getMsgMetaQueue();

		sendToQueue(msg, queue);
	}

	/**
	 * 挂起消息，把消息发到自己的挂起消息队列里
	 * 
	 * @param msg
	 */
	public void suspendMsg(JSONObject msg) {
		Config config = Config.getInstance();
		String queue = config.getSuspendedQ();

		sendToQueue(msg, queue);
	}

	// 发到自己的队列，用于系统重试
	public void sendToSelf(JSONObject msg) {
		Config config = Config.getInstance();
		String queue = config.getSelfQueue();

		sendToQueue(msg, queue);
	}

	// 发送消息到指定的队列
	public synchronized void sendToQueue(JSONObject msg, String queue) {
		if (ctx == null) {
			String log = "发送消息工具-无法发送消息：未初始化连接";
			logger.error(log);
			throw new RuntimeException(log);
		}

		if (msg == null) {
			String log = "发送消息工具-无法发送消息：消息内容为空";
			logger.error(log);
			throw new RuntimeException(log);
		}

		if (StringUtils.isEmpty(queue)) {
			String log = "发送消息工具-无法发送消息：目标队列为空";
			logger.error(log);
			throw new RuntimeException(log);
		}

		Connection conn = ctx.getConnection();
		if (!conn.isOpen()) {
			String log = "发送消息工具-无法发送消息：连接已关闭";
			logger.error(log);
			throw new RuntimeException(log);
		}

		String id = Thread.currentThread().getName();

		try {
			Channel channel = ctx.getChannel();
			if (!channel.isOpen()) {
				// 尝试重新打开一个channel
				channel = conn.createChannel();
				ctx.setChannel(channel);
			}

			// 确认目标队列
			channel.queueDeclarePassive(queue);

			String s = msg.toString(2);
			byte[] b = s.getBytes("UTF-8");
			BasicProperties.Builder builder = new BasicProperties.Builder();
			BasicProperties props = builder.contentType("application/json").contentEncoding("utf-8").deliveryMode(2)
					.build();
			channel.basicPublish("", queue, props, b);
		} catch (Exception e) {
			String log = "发送消息工具-" + id + " 发送消息出现错误";
			logger.error(log, e);
			throw new RuntimeException(log, e);
		}
	}
}

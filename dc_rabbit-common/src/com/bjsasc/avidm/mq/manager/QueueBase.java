package com.bjsasc.avidm.mq.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.message.Based;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;

public class QueueBase implements Based {
	protected final Logger logger = Logger.getLogger(getClass());

	// 重试队列
	String queue = "";

	// 缓存channel对象
	Channel channel = null;

	public synchronized void init(String queue, Channel channel) {
		this.queue = queue;
		this.channel = channel;
	}

	protected void checkConnection() {
		if (channel == null) {
			logger.error("检查到队列服务器的连接出错：未初始化连接！");
			throw new RuntimeException("检查到队列服务器的连接出错：未初始化连接！");
		}

		if (queue == null) {
			logger.error("检查到队列服务器的连接出错：队列名不能为空！");
			throw new RuntimeException("检查到队列服务器的连接出错：队列名不能为空！");
		}

		if (!channel.isOpen()) {
			logger.error("检查到队列服务器的连接出错：连接通道已关闭，请重新初始化！");
			throw new RuntimeException("检查到队列服务器的连接出错：连接通道已关闭，请重新初始化！");
		}
	}

	/**
	 * 获取队列中的消息数量
	 * 
	 * @return
	 */
	public synchronized long messageCount() {
		checkConnection();
		long cnt = 0;

		try {
			cnt = channel.messageCount(queue);
		} catch (Exception e) {
			String s = "获取队列的消息数量发生错误，队列名：" + queue;
			logger.error(s, e);
			throw new RuntimeException(s);
		}

		return cnt;
	}

	/**
	 * 根据消息ID获取队列中的消息内容
	 * 
	 * @param id
	 * @return 对应ID的消息内容对象，如无匹配，则返回null
	 */
	public synchronized JSONObject getMessage(String id) {
		JSONObject result = null;
		if (StringUtils.isEmpty(id)) {
			return result;
		}

		List<JSONObject> objs = getMessages();
		for (JSONObject obj : objs) {
			Object msgId = obj.opt(MSG_ID);
			if (id.equals(msgId)) {
				result = obj;
				break;
			}
		}

		return result;
	}

	/**
	 * 获取队列中的所有消息 （使用同一个channel，需要同步）
	 * 
	 * @return
	 */
	public synchronized List<JSONObject> getMessages() {
		checkConnection();
		List<JSONObject> objs = new ArrayList<JSONObject>();

		try {
			long tag = 0;
			GetResponse resp = channel.basicGet(queue, false);
			while (resp != null) {
				JSONObject obj = null;

				// 非JSON格式的消息直接丢弃，不处理并直接确认
				try {
					String s = new String(resp.getBody(), "UTF-8");
					obj = new JSONObject(s);
				} catch (Exception e) {
					logger.error("队列收到的消息非JSON格式！");
					channel.basicAck(resp.getEnvelope().getDeliveryTag(), false);

					resp = channel.basicGet(queue, false);
					continue;
				}

				tag = resp.getEnvelope().getDeliveryTag();

				objs.add(obj);
				resp = channel.basicGet(queue, false);
			}

			// 重新放到队列里
			if (tag != 0) {
				channel.basicNack(tag, true, true);
			}
		} catch (Exception e) {
			String s = "获取队列中的消息发生错误，队列名：" + queue;
			logger.error(s, e);
			throw new RuntimeException(s);
		}

		return objs;
	}

	/**
	 * 获取队列中的所有消息的ID集合 （使用同一个channel，需要同步）
	 * 
	 * @return
	 */
	public synchronized List<String> getMessageIds() {
		checkConnection();
		List<String> objs = new ArrayList<String>();

		try {
			long tag = 0;
			GetResponse resp = channel.basicGet(queue, false);
			while (resp != null) {
				JSONObject obj = null;

				// 非JSON格式的消息直接丢弃，不处理并直接确认
				try {
					String s = new String(resp.getBody(), "UTF-8");
					obj = new JSONObject(s);
				} catch (Exception e) {
					logger.error("队列收到的消息非JSON格式！");
					channel.basicAck(resp.getEnvelope().getDeliveryTag(), false);

					resp = channel.basicGet(queue, false);
					continue;
				}

				tag = resp.getEnvelope().getDeliveryTag();

				String msgId = obj.optString(MSG_ID);
				if (!StringUtils.isEmpty(msgId)) {
					objs.add(msgId);
				}

				resp = channel.basicGet(queue, false);
			}

			// 重新放到队列里
			if (tag != 0) {
				channel.basicNack(tag, true, true);
			}
		} catch (Exception e) {
			String s = "获取队列中的消息发生错误，队列名：" + queue;
			logger.error(s, e);
			throw new RuntimeException(s);
		}

		return objs;
	}

	/**
	 * 某个消息已处理完成，从队列中删除 （使用同一个channel，需要同步）
	 * 
	 * @param msg_id
	 */
	public synchronized void finish(String msg_id) {
		if (StringUtils.isEmpty(msg_id)) {
			return;
		}

		checkConnection();

		try {
			long tag = 0;
			GetResponse resp = channel.basicGet(queue, false);
			while (resp != null) {
				JSONObject obj = null;

				// 非JSON格式的消息直接丢弃，不处理并直接确认
				try {
					String s = new String(resp.getBody(), "UTF-8");
					obj = new JSONObject(s);
				} catch (Exception e) {
					logger.error("队列收到的消息非JSON格式！");
					channel.basicAck(resp.getEnvelope().getDeliveryTag(), false);

					resp = channel.basicGet(queue, false);
					continue;
				}

				Object id = obj.get(MSG_ID);
				if (msg_id.equals(id)) {
					logger.debug("删除队列：" + queue + " 中的消息：" + msg_id);
					channel.basicAck(resp.getEnvelope().getDeliveryTag(), false);
					break;
				}

				tag = resp.getEnvelope().getDeliveryTag();
				resp = channel.basicGet(queue, false);
			}

			// 把其它的消息重新放到队列里
			if (tag != 0) {
				channel.basicNack(tag, true, true);
			}
		} catch (Exception e) {
			String s = "从队列中删除消息发生错误，队列名：" + queue;
			logger.error(s, e);
			throw new RuntimeException(s);
		}
	}
}

package com.bjsasc.avidm.mq.queue;

import com.bjsasc.avidm.mq.manager.QueueBase;
import com.rabbitmq.client.Channel;

/**
 * 处理重试队列中的消息
 *
 * @author hwz
 *
 */
public class RetryQueue extends QueueBase {
	private static RetryQueue instance = new RetryQueue();

	public synchronized static RetryQueue getInstance() {
		return instance;
	}

	private RetryQueue() {

	}

	public void init(String queue, Channel channel) {
		super.init(queue, channel);
	}
}

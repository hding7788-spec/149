package com.bjsasc.avidm.mq.queue;

import com.bjsasc.avidm.mq.manager.QueueBase;
import com.rabbitmq.client.Channel;

/**
 * 处理挂起队列中的消息
 *
 * @author hwz
 *
 */
public class SuspendedQueue extends QueueBase {
	private static SuspendedQueue instance = new SuspendedQueue();

	public synchronized static SuspendedQueue getInstance() {
		return instance;
	}

	private SuspendedQueue() {

	}

	public void init(String queue, Channel channel) {
		super.init(queue, channel);
	}
}

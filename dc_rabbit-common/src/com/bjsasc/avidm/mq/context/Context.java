package com.bjsasc.avidm.mq.context;

import org.apache.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;

// 每次消息的上下文
public class Context {
	private Logger logger = Logger.getLogger(getClass());

	private Channel channel;

	private Envelope envelope;

	private byte[] body;

	public Context(Channel channel, Envelope envelope, byte[] body) {
		this.channel = channel;
		this.envelope = envelope;
		this.body = body;
	}

	public Channel getChannel() {
		return channel;
	}

	public Envelope getEnvelope() {
		return envelope;
	}

	public byte[] getBody() {
		return body;
	}

	public void acknowledge() {
		try {
			// 同一个channel里的操作要同步
			synchronized (channel) {
				channel.basicAck(envelope.getDeliveryTag(), false);
			}
		} catch (Exception e) {
			logger.error("反馈消息失败：", e);
		}
	}
}

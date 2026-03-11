package com.bjsasc.avidm.mq.listener;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.exception.RetryException;
import com.bjsasc.avidm.mq.util.TUtil;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;

// 监听线程，可以有多个
public class Listener extends Thread {
	protected final Logger logger = Logger.getLogger(getClass());

	// 到Queue的连接
	Connection conn = null;

	// 监听队列
	String queue = "";

	// 重试队列
	String retryQueue = "";

	// 监听线程退出信号
	CountDownLatch exitConsumer = null;

	public Listener(Connection conn, String queue, String retryQueue, CountDownLatch exitConsumer) {
		this.conn = conn;
		this.queue = queue;
		this.retryQueue = retryQueue;
		this.exitConsumer = exitConsumer;
	}

	@Override
	public void run() {
		try {
			logger.info("监听线程-开始运行...");
			final Channel channel = conn.createChannel();
			logger.info("监听线程-成功连接到队列服务器");

			try {
				// 声明持久化的队列
				logger.info("监听线程-开始监听队列：" + queue);
				boolean durable = true;
				channel.queueDeclare(queue, durable, false, false, null);
				channel.queueDeclarePassive(retryQueue);

				// 等消息处理完确认之后再接收下一个消息
				channel.basicQos(1);

				Consumer consumer = new DefaultConsumer(channel) {
					@Override
					public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
							byte[] body) throws IOException {
						JSONObject obj = null;
						// 非JSON格式的消息直接丢弃，不处理并直接确认
						try {
							String s = new String(body, "UTF-8");
							obj = new JSONObject(s);
							System.out.println("监听线程-收到的消息："+s);

						} catch (Exception e) {
							logger.error("监听线程-收到的消息非JSON格式！", e);
							channel.basicAck(envelope.getDeliveryTag(), false);
							return;
						}

						// 同步处理消息，处理业务组件无论发生什么异常都需反馈相应的消息
						try {
							TWorker worker = new TWorker(obj);
							worker.work();
						} catch (RetryException e) {
							// 如果需要重试，则将消息原样放入重试队列
							channel.basicPublish("", retryQueue, MessageProperties.PERSISTENT_BASIC, body);
						}

						// 业务处理无论是否异常都确认此消息
						channel.basicAck(envelope.getDeliveryTag(), false);
					}
				};

				String ctag = channel.basicConsume(queue, consumer);
				logger.info("监听线程-已就绪");

				// 等待退出信号
				exitConsumer.await();
				channel.basicCancel(ctag);
			} catch (InterruptedException ie) {
				logger.info("监听线程-发现系统正在退出...");
			} catch (Exception e) {
				logger.error("监听线程-正在退出，发生异常:", e);
			} finally {
				logger.info("监听线程-正在清理...");
				TUtil.close(channel);
				logger.info("监听线程-到队列服务器的连接已成功关闭");
			}
		} catch (Exception e) {
			logger.error("监听线程-无法连接到队列服务器", e);
		}

		logger.info("监听线程-已退出");
	}

}
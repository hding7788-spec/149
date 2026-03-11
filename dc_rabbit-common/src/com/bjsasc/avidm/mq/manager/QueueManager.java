package com.bjsasc.avidm.mq.manager;

import com.bjsasc.avidm.mq.config.Config;
import com.bjsasc.avidm.mq.context.ConnectionContext;
import com.bjsasc.avidm.mq.listener.Listener;
import com.bjsasc.avidm.mq.queue.RetryQueue;
import com.bjsasc.avidm.mq.queue.SuspendedQueue;
import com.bjsasc.avidm.mq.sender.Sender;
import com.bjsasc.avidm.mq.thread.ExitHandler;
import com.bjsasc.avidm.mq.util.TUtil;
import com.rabbitmq.client.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

// 队列管理器
public class QueueManager {
	protected final Logger logger = Logger.getLogger(getClass());

	private static QueueManager instance = new QueueManager();

	// 启动监听线程的数量
	final static int LISTENER_COUNT = 1;

	// 通知监听线程退出
	private CountDownLatch exitConsumer = new CountDownLatch(1);

	// 登录队列的用户名
	private String username = ConnectionFactory.DEFAULT_USER;

	// 登录队列的密码
	private String password = ConnectionFactory.DEFAULT_PASS;

	// 队列服务器的host
	private String host = ConnectionFactory.DEFAULT_HOST;

	// 队列服务器的端口
	private int port = ConnectionFactory.DEFAULT_AMQP_PORT;

	private ShutdownListener connectionShutdownListener;
	private ShutdownListener senderShutdownListener;

	private String vhost = "";

	private boolean isBg;

	public boolean isBg() {
		return isBg;
	}

	public void setBg(boolean bg) {
		isBg = bg;
	}

	// 发给对方的队列ID
	String toQueue = "";

	// 自己站点的队列ID
	String myQueue = "";

	// 自己站点的重试队列
	String retryQueue = "";

	// 自己站点的任务挂起队列
	String suspendedQ = "";

	// 管理的线程列表
	List<Thread> threads = new ArrayList<Thread>();

	// 监听器的连接
	Connection cListener = null;

	// 发生消息的连接
	ConnectionContext ctx = null;

	public synchronized static QueueManager getInstance() {
		return instance;
	}

	private QueueManager() {
		// 监听系统退出
		Thread exit = new ExitHandler(this);
		Runtime.getRuntime().addShutdownHook(exit);
	}

	public void config(Config config) {
		if (config == null) {
			return;
		}

		host = config.getHost();
		port = config.getPort();
		vhost = config.getVhost();
		username = config.getUsername();
		password = config.getPassword();
		myQueue = config.getSelfQueue();
		toQueue = config.getDcQueue();
		retryQueue = config.getRetryQueue();
		suspendedQ = config.getSuspendedQ();
	}

	public List<Thread> getThreads() {
		return threads;
	}

	public synchronized void addThread(Thread thread) {
		threads.add(thread);
	}

	// 初始化队列
	public synchronized void init() throws Exception {
		logger.info("队列管理器-正在初始化...");

		// 先清理
		clear();

		// 重置变量
		threads.clear();
		exitConsumer = new CountDownLatch(1);

		// 测试到队列服务器的连接
		Channel ch = null;

		try {
			logger.info("队列管理器-使用队列服务器：" + host);
			logger.info("队列管理器-使用vhost：" + vhost);
			logger.info("队列管理器-队列服务器端口：" + port);
			logger.info("队列管理器-登录用户名：" + username);
			logger.info("队列管理器-测试到队列服务器的连接...");

			ConnectionFactory fac = new ConnectionFactory();
			fac.setHost(host);
			fac.setPort(port);
			fac.setVirtualHost(vhost);
			fac.setUsername(username);
			fac.setPassword(password);

			// 每15秒发一次心跳，防止防火墙关闭闲置的TCP连接
			fac.setRequestedHeartbeat(30);

			// 自动恢复连接
			fac.setAutomaticRecoveryEnabled(true);
			fac.setNetworkRecoveryInterval(10000);

			cListener = fac.newConnection();
			ch = cListener.createChannel();
			connectionShutdownListener = new ShutdownListener() {
				@Override
				public void shutdownCompleted(ShutdownSignalException cause) {
					logger.error("队列管理器-连接断开，原因：" + cause.getMessage());
					// 触发重连逻辑
					reconnect();
				}
			};
			cListener.addShutdownListener(connectionShutdownListener);

			logger.info("队列管理器-到队列服务器的连接正常");
		} catch (Exception e) {
			logger.error("队列管理器-连接到队列服务器失败", e);
			throw e;
		} finally {
			TUtil.close(ch);
		}

		// 先初始化到重试队列和挂起队列的处理工作
		CountDownLatch channelReady = new CountDownLatch(1);
		InitChannel ic = new InitChannel(cListener, channelReady);
		ic.start();
		addThread(ic);

		channelReady.await();
		RetryQueue retry = RetryQueue.getInstance();
		retry.init(retryQueue, ic.getRetryChannel());

		SuspendedQueue sq = SuspendedQueue.getInstance();
		sq.init(suspendedQ, ic.getSuspendChannel());

		// 再启动指定数量的监听线程，监听线程直接使用上面初始化后的重试队列
		//if(!isBg){
			for (int i = 0; i < LISTENER_COUNT; i++) {
				Thread thread = new Listener(cListener, myQueue, retryQueue, exitConsumer);
				thread.start();
				// 添加到线程列表
				addThread(thread);
			}

		//}

		// 初始化发送工具
		initSender();
	}

	// 重连机制
	private void reconnect() {
		int retryCount = 0;
		int maxRetry = 18; // 最大重试次数
		long retryInterval = 600000; // 重试间隔，10分钟

		while (retryCount < maxRetry) {
			try {
				logger.info("队列管理器-尝试重新连接，重试次数：" + (retryCount + 1));
				clear(); // 清理现有资源
				init();  // 重新初始化
				logger.info("队列管理器-重新连接成功！");
				break; // 重连成功，退出循环
			} catch (Exception e) {
				retryCount++;
				logger.error("队列管理器-重新连接失败，等待重试...", e);
				try {
					Thread.sleep(retryInterval); // 等待一段时间后重试
				} catch (InterruptedException ie) {
					logger.error("队列管理器-重连等待被中断", ie);
				}
			}
		}

		if (retryCount >= maxRetry) {
			logger.error("队列管理器-重连失败，已达到最大重试次数");
		}
	}

	// 创建并缓存channel对象
	class InitChannel extends Thread {
		Connection conn = null;
		CountDownLatch latch = null;
		Channel retryChannel = null;
		Channel suspendChannel = null;

		public InitChannel(Connection c, CountDownLatch latch) {
			this.conn = c;
			this.latch = latch;
		}

		@Override
		public void run() {
			try {
				logger.info("创建到队列：" + retryQueue + " 的连接通道...");
				retryChannel = conn.createChannel();

				// 声明持久化的队列
				boolean durable = true;
				retryChannel.queueDeclare(retryQueue, durable, false, false, null);
				logger.info("已成功创建到队列：" + retryQueue + " 的连接通道！");

				logger.info("创建到队列：" + suspendedQ + " 的连接通道...");
				suspendChannel = conn.createChannel();

				// 声明持久化的队列
				suspendChannel.queueDeclare(suspendedQ, durable, false, false, null);
				logger.info("已成功创建到队列：" + suspendedQ + " 的连接通道！");

				// 连接通道已经创建完成，可以使用了
				latch.countDown();

				// 等待退出信号
				exitConsumer.await();
			} catch (Exception e) {
				logger.error("创建连接通道发生异常：", e);
			} finally {
				logger.info("正关闭到队列：" + retryQueue + " 的连接通道...");
				TUtil.close(retryChannel);

				logger.info("正关闭到队列：" + suspendedQ + " 的连接通道...");
				TUtil.close(suspendChannel);
				logger.info("已关闭到队列的连接通道！");
			}
		}

		public Channel getRetryChannel() {
			return retryChannel;
		}

		public Channel getSuspendChannel() {
			return suspendChannel;
		}
	}

	// 清理原有线程池
	public synchronized void clear() {
		if ((threads != null) && threads.size() > 0) {
			logger.info("队列管理器-通知监听线程退出...");
			exitConsumer.countDown();

			for (Thread thread : threads) {
				try {
					thread.join();
				} catch (Exception e) {
					logger.error("队列管理器-等待线程退出时发生异常", e);
				}
			}

			logger.info("队列管理器-所有监听线程都已退出.");
		}

		if (cListener != null) {
			// 移除监听器
			if (connectionShutdownListener != null) {
				cListener.removeShutdownListener(connectionShutdownListener);
				logger.info("队列管理器-已移除连接监听器.");
			}
		}

		if (ctx != null && ctx.getConnection() != null) {
			// 移除发送器的监听器
			if (senderShutdownListener != null) {
				ctx.getConnection().removeShutdownListener(senderShutdownListener);
				logger.info("队列管理器-已移除发送器监听器.");
			}
		}

		// 清理到队列服务器的连接
		logger.info("队列管理器-关闭到队列服务器的连接...");

		TUtil.close(cListener);
		TUtil.close(ctx);

		logger.info("队列管理器-到队列服务器的连接已成功关闭");
	}

	protected void initSender() throws Exception {
		try {
			ConnectionFactory fac = new ConnectionFactory();
			fac.setHost(host);
			fac.setPort(port);
			fac.setVirtualHost(vhost);
			fac.setUsername(username);
			fac.setPassword(password);

			// 每15秒发一次心跳，防止防火墙关闭闲置的TCP连接
			fac.setRequestedHeartbeat(30);

			// 自动恢复连接
			fac.setAutomaticRecoveryEnabled(true);
			fac.setNetworkRecoveryInterval(10000);

			Connection conn = fac.newConnection();
			senderShutdownListener = new ShutdownListener() {
				@Override
				public void shutdownCompleted(ShutdownSignalException cause) {
					logger.error("队列管理器-发送器连接断开，原因：" + cause.getMessage());
					reconnect();
				}
			};
			conn.addShutdownListener(senderShutdownListener);

			Channel ch = conn.createChannel();
			ctx = new ConnectionContext(conn, ch);

			// 声明缺省的发送队列
			ch.queueDeclarePassive(toQueue);
			Sender sender = Sender.getInstance();
			sender.init(ctx, toQueue);
		} catch (Exception e) {
			logger.error("队列管理器-初始化发生消息工具类失败：", e);
			throw e;
		}
	}
}

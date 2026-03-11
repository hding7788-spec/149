package com.bjsasc.avidm.mq.thread;

import java.util.List;

import org.apache.log4j.Logger;

import com.bjsasc.avidm.mq.manager.QueueManager;

public class ExitHandler extends Thread {
	protected final Logger logger = Logger.getLogger(getClass());

	QueueManager manager = null;

	public ExitHandler(QueueManager manager) {
		this.manager = manager;
	}

	@Override
	public void run() {
		try {
			logger.info("系统退出，正在清理...");
			if (manager != null) {
				manager.clear();
			}

			logger.info("等待其它工作线程退出...");
			List<Thread> threads = manager.getThreads();
			// 必须和主线程同时等待工作线程退出
			for (Thread t : threads) {
				t.join();
			}

			logger.debug("完成清理工作！");
		} catch (Exception e) {
		}
	}
}
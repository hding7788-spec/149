package com.bjsasc.avidm.mq.handler;

import org.apache.log4j.Logger;

import com.bjsasc.avidm.mq.event.MessageDeliveredEvent;
import com.bjsasc.avidm.mq.framework.AbstractHandler;

public class NoneHandler extends AbstractHandler<MessageDeliveredEvent> {
	protected final Logger logger = Logger.getLogger(getClass());

	@Override
	public void onEvent(MessageDeliveredEvent event) {
		logger.debug("NoneHandler开始处理事件...");
		logger.debug("NoneHandler事件处理完毕！");
	}

}

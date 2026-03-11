package com.bjsasc.avidm.mq.handler;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.event.MessageDeliveredEvent;
import com.bjsasc.avidm.mq.framework.AbstractHandler;

public class LogHandler extends AbstractHandler<MessageDeliveredEvent> {
	protected final Logger logger = Logger.getLogger(getClass());

	@Override
	public void onEvent(MessageDeliveredEvent event) {
		logger.debug("开始处理事件...");
		JSONObject obj = event.getContent();

		logger.debug("事件ID为：" + obj.get("msg_id"));
		logger.debug("事件处理完毕！");
	}
}

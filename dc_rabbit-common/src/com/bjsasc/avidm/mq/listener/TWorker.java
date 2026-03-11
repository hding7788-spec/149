package com.bjsasc.avidm.mq.listener;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.exception.RetryException;
import com.bjsasc.avidm.mq.framework.Event;
import com.bjsasc.avidm.mq.framework.EventDispatcher;
import com.bjsasc.avidm.mq.message.Based;

// 处理消息的工作类
public class TWorker implements Based {

	protected final Logger logger = Logger.getLogger(getClass());

	private JSONObject obj = null;

	public TWorker(JSONObject obj) {
		this.obj = obj;
	}

	public void work() {
		String id = Thread.currentThread().getName();

		try {
			Object ver = null;
			Object type = null;

			// 消息体如异常直接确认忽略
			try {
				obj.get(MSG_ID);
				ver = obj.get(SYS_VERSION_REQUEST);
				type = obj.get(MSG_TYPE);
			} catch (Exception e) {
				logger.error("工作线程-" + id + "，消息异常，消息体不含:msg_id、sys_version或msg_type标识！");
				return;
			}

			// 只有消息体协议正确才处理该事件
			EventDispatcher disp = EventDispatcher.getInstance();
			Event event = disp.getEvent(ver.toString() + "/" + type.toString(), obj);
			disp.dispatch(event);
		} catch (RetryException e) {
			logger.error("工作线程-" + id + " 处理消息发生异常，需要稍后重试", e);
			throw e;
		} catch (Exception e) {
			logger.error("工作线程-" + id + " 处理消息发生异常", e);
		}
	}
}

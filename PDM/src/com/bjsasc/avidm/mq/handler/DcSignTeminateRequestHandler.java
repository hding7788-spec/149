package com.bjsasc.avidm.mq.handler;

import com.bjsasc.avidm.mq.event.dc.DcSignTerminateRequestEvent;
import com.bjsasc.avidm.mq.framework.AbstractHandler;
import com.bjsasc.avidm.mq.message.Based;
import ext.sast.center.util.JsonConvertUtil;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import wt.log4j.LogR;

import java.io.File;

/**
 * A4处理会签强制结束
 *
 * @author YHJ
 *
 */
public class DcSignTeminateRequestHandler extends AbstractHandler<DcSignTerminateRequestEvent> implements Based {

	protected final Logger logger = LogR.getLogger(DcSignTeminateRequestHandler.class.getName());
	public DcSignTeminateRequestHandler(){

	}
	@Override
	public void onEvent(DcSignTerminateRequestEvent event) {
		logger.debug("开始处理数据中心转发的会签强制结束请求...");
		JSONObject msg = event.getContent();
		JsonConvertUtil.persistentJson(msg.toString(), JsonConvertUtil.PERSISTENTPATH+ File.separator+"DcSignTeminateRequestHandler");
		HandlerProcess.doDcSignTeminateRequestHandler(msg);
		logger.debug("处理数据中心转发的会签强制结束。");
	}
}

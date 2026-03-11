package com.bjsasc.avidm.mq.handler;

import com.bjsasc.avidm.mq.event.dc.DcSignRequestEvent;
import com.bjsasc.avidm.mq.framework.AbstractHandler;
import ext.sast.center.util.JsonConvertUtil;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import wt.log4j.LogR;

import java.io.File;

/**
 * 厂所接收中心域会签信息(经中心域中转)
 * @author wyq
 *
 */
public class DcSignRequestHandler extends AbstractHandler<DcSignRequestEvent>{
	private static final Logger logger  = LogR.getLogger(DcSignRequestHandler.class.getName());
	public DcSignRequestHandler(){}
	@Override
	public void onEvent(DcSignRequestEvent event) {
		logger.info("***********厂所开始接收中心域会签消息********************");
		JSONObject msg = event.getContent();
		JsonConvertUtil.persistentJson(msg.toString(), JsonConvertUtil.PERSISTENTPATH+ File.separator+"DcSignRequestHandler");
		HandlerProcess.doDcSignRequestHandler(msg);
	}
}

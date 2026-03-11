package com.bjsasc.avidm.mq.handler;

import com.bjsasc.avidm.mq.event.dc.DcSignTaskSynResponseEvent;
import com.bjsasc.avidm.mq.framework.AbstractHandler;
import ext.sast.center.util.JsonConvertUtil;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import wt.log4j.LogR;

import java.io.File;

/**
 * 厂所接收中心域会签反馈信息(经中心域中转)
 * @author wyq
 *
 */
public class DcSignTaskSynResponseHandler extends AbstractHandler<DcSignTaskSynResponseEvent>{
	private static final Logger logger  = LogR.getLogger(DcSignTaskSynResponseHandler.class.getName());
	public DcSignTaskSynResponseHandler(){}
	@Override
	public void onEvent(DcSignTaskSynResponseEvent event) {
		logger.info("***********DcSignTaskSynResponseHandler 厂所开始接收中心域会签反馈消息********************");
		JSONObject msg = event.getContent();
		JsonConvertUtil.persistentJson(msg.toString(), JsonConvertUtil.PERSISTENTPATH+ File.separator+"DcSignTaskSynResponseHandler");
		HandlerProcess.doDcSignTaskSynResponseHandler(msg);

	}

}

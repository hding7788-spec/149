package com.bjsasc.avidm.mq.handler;

import com.bjsasc.avidm.mq.event.dc.DcShareRequestEvent;
import com.bjsasc.avidm.mq.framework.AbstractHandler;
import com.bjsasc.avidm.mq.message.Based;
import ext.sast.center.util.JsonConvertUtil;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import wt.log4j.LogR;

import java.io.File;

/**
 * 厂所接收中心域预审信息(经中心域中转)
 * @author wyq
 *
 */
public class DcShareRequestHandler extends AbstractHandler<DcShareRequestEvent> implements Based {
	private final static Logger logger = LogR.getLogger(DcShareRequestHandler.class.getName());
	public DcShareRequestHandler() {}
	@Override
	public void onEvent(DcShareRequestEvent event) {
		logger.info("***********厂所开始接收中心域预审消息********************");
		JSONObject msg = event.getContent();

		JsonConvertUtil.persistentJson(msg.toString(), JsonConvertUtil.PERSISTENTPATH+ File.separator+"DcShareRequestHandler");
		HandlerProcess.doDcShareRequestHandler(msg);
        logger.info("***********厂所成功接收中心域预审消息********************");
	}

}

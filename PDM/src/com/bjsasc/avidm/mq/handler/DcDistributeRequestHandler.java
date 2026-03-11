package com.bjsasc.avidm.mq.handler;

import com.bjsasc.avidm.mq.event.dc.DcDistributeRequestEvent;
import com.bjsasc.avidm.mq.framework.AbstractHandler;
import ext.sast.center.util.JsonConvertUtil;
import org.json.JSONObject;

import java.io.File;

/**
 * 厂所接收中心域发放信息(经中心域中转)
 * @author wyq
 *
 */
public class DcDistributeRequestHandler extends AbstractHandler<DcDistributeRequestEvent> {
	public DcDistributeRequestHandler() {}
	@Override
	public void onEvent(DcDistributeRequestEvent event) {
		System.out.println("***********厂所开始接收中心域发放消息********************");
		JSONObject msg = event.getContent();
		JsonConvertUtil.persistentJson(msg.toString(), JsonConvertUtil.PERSISTENTPATH+ File.separator+"DcDistributeRequestHandler");
		HandlerProcess.doDcDistributeRequestHandler(msg);
	}


}

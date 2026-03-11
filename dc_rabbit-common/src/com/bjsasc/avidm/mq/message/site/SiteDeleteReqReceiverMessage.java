package com.bjsasc.avidm.mq.message.site;

import com.bjsasc.avidm.mq.message.Message;

// 域注册消息：数据中心 --> 接收方
public abstract class SiteDeleteReqReceiverMessage extends Message {

	public SiteDeleteReqReceiverMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_SITEDELETE_RECEIVER);
	}
}

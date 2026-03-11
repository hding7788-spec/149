package com.bjsasc.avidm.mq.message.site;

import com.bjsasc.avidm.mq.message.Message;

// 域注册消息：数据中心 --> 接收方
public abstract class RegisterReqReceiverMessage extends Message {

	public RegisterReqReceiverMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_REGISTER_RECEIVER);
	}
}

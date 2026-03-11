package com.bjsasc.avidm.mq.message.site;

import com.bjsasc.avidm.mq.message.Message;

// 域注册消息：厂所--->数据中心
public abstract class RegisterRespDcMessage extends Message {

	public RegisterRespDcMessage() {
		super();
		put(MSG_TYPE, DC_RESPONSE_REGISTER_RECEIVER);
	}
}

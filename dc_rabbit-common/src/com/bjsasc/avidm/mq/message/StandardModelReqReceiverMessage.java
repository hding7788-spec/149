package com.bjsasc.avidm.mq.message;

// 标准模型及属性下发消息：数据中心 --> 接收方
public abstract class StandardModelReqReceiverMessage extends Message {

	public StandardModelReqReceiverMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_STANDARD_MODEL_RECEIVER);
	}
}

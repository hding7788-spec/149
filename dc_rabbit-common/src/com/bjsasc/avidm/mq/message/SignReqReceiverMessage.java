package com.bjsasc.avidm.mq.message;

// 会签：数据中心 --> 接收方
public abstract class SignReqReceiverMessage extends Message {

	public SignReqReceiverMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_SIGN_RECEIVER);
	}
}

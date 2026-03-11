package com.bjsasc.avidm.mq.message;

// 会签：发起方 --> 数据中心
public abstract class SignReqDcMessage extends Message {

	public SignReqDcMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_SIGN_DC);
	}
}

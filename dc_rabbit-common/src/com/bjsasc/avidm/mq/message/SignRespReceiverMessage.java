package com.bjsasc.avidm.mq.message;

// 会签：数据中心 <-- 接收方
public abstract class SignRespReceiverMessage extends Message {

	public SignRespReceiverMessage() {
		super();
		put(MSG_TYPE, DC_RESPONSE_SIGN_RECEIVER);
	}
}

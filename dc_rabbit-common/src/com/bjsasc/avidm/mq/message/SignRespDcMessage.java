package com.bjsasc.avidm.mq.message;

// 会签：发起方 <-- 数据中心
public abstract class SignRespDcMessage extends Message {

	public SignRespDcMessage() {
		super();
		put(MSG_TYPE, DC_RESPONSE_SIGN_DC);
	}
}

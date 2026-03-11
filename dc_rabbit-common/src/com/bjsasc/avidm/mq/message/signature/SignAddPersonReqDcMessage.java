package com.bjsasc.avidm.mq.message.signature;

import com.bjsasc.avidm.mq.message.Message;

// 会签增加人员：发起方 --> 数据中心
public abstract class SignAddPersonReqDcMessage extends Message {

	public SignAddPersonReqDcMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_SIGN_ADDPERSON_DC);
	}
}

package com.bjsasc.avidm.mq.message.signature;

import com.bjsasc.avidm.mq.message.Message;

// 会签删除人员：发起方 --> 数据中心
public abstract class SignDeletePersonReqDcMessage extends Message {

	public SignDeletePersonReqDcMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_SIGN_DELETEPERSON_DC);
	}
}

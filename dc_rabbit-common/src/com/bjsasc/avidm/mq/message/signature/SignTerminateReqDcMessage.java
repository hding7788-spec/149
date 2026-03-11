package com.bjsasc.avidm.mq.message.signature;

import com.bjsasc.avidm.mq.message.Message;

// 会签强制结束：发起方 --> 数据中心
public abstract class SignTerminateReqDcMessage extends Message {

	public SignTerminateReqDcMessage() {
		super();
		put(MSG_TYPE, DC_REQUEST_SIGN_TERMINATE_DC);
	}
}

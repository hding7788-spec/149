package com.bjsasc.avidm.mq.message.signature;

import com.bjsasc.avidm.mq.message.Message;

// 会签任务操作同步：接收方 --> 数据中心
public abstract class SignTaskSynRespReceiverMessage extends Message {

	public SignTaskSynRespReceiverMessage() {
		super();
		put(MSG_TYPE, DC_RESPONSE_SIGN_TASKSYN_RECEIVER);
	}
}

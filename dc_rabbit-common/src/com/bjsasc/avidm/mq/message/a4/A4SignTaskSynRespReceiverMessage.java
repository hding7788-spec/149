package com.bjsasc.avidm.mq.message.a4;


import com.bjsasc.avidm.mq.message.signature.SignTaskSynRespReceiverMessage;

// 会签任务操作同步：A4系统 --> 数据中心
public class A4SignTaskSynRespReceiverMessage extends SignTaskSynRespReceiverMessage {

	// 带的任务及意见对象
	public A4SignTaskSynRespReceiverMessage() {
		super();

		// 系统版本为A4
		put(SYS_VERSION_INITIAL, SYS_VERSION_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A4);

	}

	// 带的任务及意见对象
	public A4SignTaskSynRespReceiverMessage(String content) {
		super();

		// 系统版本为A4
		put(SYS_VERSION_INITIAL, SYS_VERSION_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A4);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

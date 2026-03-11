package com.bjsasc.avidm.mq.message.a5;


import com.bjsasc.avidm.mq.message.signature.SignTaskSynRespReceiverMessage;

// 会签任务操作同步：A5系统 --> 数据中心
public class A5SignTaskSynRespReceiverMessage extends SignTaskSynRespReceiverMessage {

	// 带的任务及意见对象
	public A5SignTaskSynRespReceiverMessage() {
		super();

		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

	}

	// 带的任务及意见对象
	public A5SignTaskSynRespReceiverMessage(String content) {
		super();

		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

package com.bjsasc.avidm.mq.message.a4;


import com.bjsasc.avidm.mq.message.distribute.DistributeTaskSynRespReceiverMessage;

// 发放任务操作同步：A4系统 --> 数据中心
public class A4DistributeTaskSynRespReceiverMessage extends DistributeTaskSynRespReceiverMessage {

	// 带的任务及意见对象
	public A4DistributeTaskSynRespReceiverMessage() {
		super();

		// 系统版本为A4
		put(SYS_VERSION_INITIAL, SYS_VERSION_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A4);

	}

	// 带的任务及意见对象
	public A4DistributeTaskSynRespReceiverMessage(String content) {
		super();

		// 系统版本为A4
		put(SYS_VERSION_INITIAL, SYS_VERSION_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A4);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

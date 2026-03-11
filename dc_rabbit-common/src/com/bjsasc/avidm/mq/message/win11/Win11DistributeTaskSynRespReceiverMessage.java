package com.bjsasc.avidm.mq.message.win11;


import com.bjsasc.avidm.mq.message.distribute.DistributeTaskSynRespReceiverMessage;

// 发放任务操作同步：Win11系统 --> 数据中心
public class Win11DistributeTaskSynRespReceiverMessage extends DistributeTaskSynRespReceiverMessage {

	// 带的任务及意见对象
	public Win11DistributeTaskSynRespReceiverMessage() {
		super();

		// 系统版本为WIN11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

	}

	// 带的任务及意见对象
	public Win11DistributeTaskSynRespReceiverMessage(String content) {
		super();

		// 系统版本为WIN11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

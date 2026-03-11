package com.bjsasc.avidm.mq.message.win10;


import com.bjsasc.avidm.mq.message.distribute.DistributeTaskSynRespReceiverMessage;

// 发放任务操作同步：Win10系统 --> 数据中心
public class Win10DistributeTaskSynRespReceiverMessage extends DistributeTaskSynRespReceiverMessage {

	// 带的任务及意见对象
	public Win10DistributeTaskSynRespReceiverMessage() {
		super();

		// 系统版本为WIN10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

	}

	// 带的任务及意见对象
	public Win10DistributeTaskSynRespReceiverMessage(String content) {
		super();

		// 系统版本为WIN10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

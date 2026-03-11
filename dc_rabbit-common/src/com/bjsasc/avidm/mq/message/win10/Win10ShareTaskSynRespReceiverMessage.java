package com.bjsasc.avidm.mq.message.win10;


import com.bjsasc.avidm.mq.message.share.ShareTaskSynRespReceiverMessage;

// 预审任务操作同步：WIN10系统 --> 数据中心
public class Win10ShareTaskSynRespReceiverMessage extends ShareTaskSynRespReceiverMessage {

	// 带的任务及意见对象
	public Win10ShareTaskSynRespReceiverMessage() {
		super();

		// 系统版本为WIN10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

	}

	// 带的任务及意见对象
	public Win10ShareTaskSynRespReceiverMessage(String content) {
		super();

		// 系统版本为WIN10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

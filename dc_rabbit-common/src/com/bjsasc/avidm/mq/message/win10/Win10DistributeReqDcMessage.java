package com.bjsasc.avidm.mq.message.win10;


import com.bjsasc.avidm.mq.message.DistributeReqDcMessage;

// 分发：win10系统 --> 数据中心
public class Win10DistributeReqDcMessage extends DistributeReqDcMessage {

	// 带的单据对象和分发对象
	public Win10DistributeReqDcMessage() {
		super();

		// 系统版本为WIN10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

	}

	// 带的单据对象和分发对象
	public Win10DistributeReqDcMessage(String content) {
		super();

		// 系统版本为WIN10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

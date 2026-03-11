package com.bjsasc.avidm.mq.message.win11;


import com.bjsasc.avidm.mq.message.DistributeReqDcMessage;

// 分发：win11系统 --> 数据中心
public class Win11DistributeReqDcMessage extends DistributeReqDcMessage {

	// 带的单据对象和分发对象
	public Win11DistributeReqDcMessage() {
		super();

		// 系统版本为WIN10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

	}

	// 带的单据对象和分发对象
	public Win11DistributeReqDcMessage(String content) {
		super();

		// 系统版本为WIN11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

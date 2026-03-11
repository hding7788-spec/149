package com.bjsasc.avidm.mq.message.win10;

import com.bjsasc.avidm.mq.message.signature.SignAddPersonReqDcMessage;

// 会签增加人员：Win10系统 --> 数据中心
public class Win10SignAddPersonReqDcMessage extends SignAddPersonReqDcMessage {

	// 带的单据对象和会签对象
	public Win10SignAddPersonReqDcMessage() {
		super();
		// 系统版本为WIN10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

	}

	// 带的单据对象和会签对象
	public Win10SignAddPersonReqDcMessage(String content) {
		super();

		// 系统版本为WIN10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

package com.bjsasc.avidm.mq.message.win10;

import com.bjsasc.avidm.mq.message.signature.SignDeletePersonReqDcMessage;

// 会签删除人员：WIN10系统 --> 数据中心
public class Win10SignDeletePersonReqDcMessage extends SignDeletePersonReqDcMessage {

	// 带的单据对象和会签对象
	public Win10SignDeletePersonReqDcMessage() {
		super();
		// 系统版本为WIN10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

	}

	// 带的单据对象和会签对象
	public Win10SignDeletePersonReqDcMessage(String content) {
		super();

		// 系统版本为WIN10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

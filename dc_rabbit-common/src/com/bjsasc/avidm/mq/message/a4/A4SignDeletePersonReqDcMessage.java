package com.bjsasc.avidm.mq.message.a4;

import com.bjsasc.avidm.mq.message.signature.SignDeletePersonReqDcMessage;

// 会签删除人员：A4系统 --> 数据中心
public class A4SignDeletePersonReqDcMessage extends SignDeletePersonReqDcMessage {

	// 带的单据对象和会签对象
	public A4SignDeletePersonReqDcMessage() {
		super();
		// 系统版本为A4
		put(SYS_VERSION_INITIAL, SYS_VERSION_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A4);

	}

	// 带的单据对象和会签对象
	public A4SignDeletePersonReqDcMessage(String content) {
		super();

		// 系统版本为A4
		put(SYS_VERSION_INITIAL, SYS_VERSION_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A4);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

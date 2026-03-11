package com.bjsasc.avidm.mq.message.win11;

import com.bjsasc.avidm.mq.message.signature.SignAddPersonReqDcMessage;

// 会签增加人员：Win11系统 --> 数据中心
public class Win11SignAddPersonReqDcMessage extends SignAddPersonReqDcMessage {

	// 带的单据对象和会签对象
	public Win11SignAddPersonReqDcMessage() {
		super();
		// 系统版本为WIN11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

	}

	// 带的单据对象和会签对象
	public Win11SignAddPersonReqDcMessage(String content) {
		super();

		// 系统版本为WIN11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

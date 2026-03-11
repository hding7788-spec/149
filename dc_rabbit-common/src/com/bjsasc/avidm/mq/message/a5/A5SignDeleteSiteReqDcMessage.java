package com.bjsasc.avidm.mq.message.a5;

import com.bjsasc.avidm.mq.message.signature.SignDeleteSiteReqDcMessage;

// 会签删除单位：A5系统 --> 数据中心
public class A5SignDeleteSiteReqDcMessage extends SignDeleteSiteReqDcMessage {

	// 带的单据对象和会签对象
	public A5SignDeleteSiteReqDcMessage() {
		super();
		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

	}

	// 带的单据对象和会签对象
	public A5SignDeleteSiteReqDcMessage(String content) {
		super();

		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

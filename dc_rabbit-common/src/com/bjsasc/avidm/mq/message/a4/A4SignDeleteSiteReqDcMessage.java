package com.bjsasc.avidm.mq.message.a4;

import com.bjsasc.avidm.mq.message.signature.SignDeleteSiteReqDcMessage;
//会签删除单位：A4系统 --> 数据中心
public class A4SignDeleteSiteReqDcMessage extends SignDeleteSiteReqDcMessage {

	// 带的单据对象和会签对象
	public A4SignDeleteSiteReqDcMessage() {
		super();
		// 系统版本为A4
		put(SYS_VERSION_INITIAL, SYS_VERSION_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A4);

	}

	// 带的单据对象和会签对象
	public A4SignDeleteSiteReqDcMessage(String content) {
		super();

		// 系统版本为A4
		put(SYS_VERSION_INITIAL, SYS_VERSION_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A4);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

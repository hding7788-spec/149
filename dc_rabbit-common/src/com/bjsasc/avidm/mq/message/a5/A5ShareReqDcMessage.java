package com.bjsasc.avidm.mq.message.a5;

import org.json.JSONArray;

import com.bjsasc.avidm.mq.message.ShareReqDcMessage;

// 共享：A5系统 --> 数据中心
public class A5ShareReqDcMessage extends ShareReqDcMessage {

	// 带的单据对象和共享对象
	public A5ShareReqDcMessage(JSONArray objects) {
		super();

		// 系统版本为A4
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

		put(JA_OBJECTS_INITIAL, objects);
		put(JA_OBJECTS_REQUEST, objects);
	}

	// 带的单据对象和共享对象
	public A5ShareReqDcMessage(String content) {
		super();

		// 系统版本为A4
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

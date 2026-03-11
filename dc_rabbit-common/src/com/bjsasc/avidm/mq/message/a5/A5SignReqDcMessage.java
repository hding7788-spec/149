package com.bjsasc.avidm.mq.message.a5;

import org.json.JSONArray;

import com.bjsasc.avidm.mq.message.SignReqDcMessage;

// 会签：A5系统 --> 数据中心
public class A5SignReqDcMessage extends SignReqDcMessage {

	// 带的单据对象和会签对象
	public A5SignReqDcMessage(JSONArray objects) {
		super();

		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

		put(JA_OBJECTS_INITIAL, objects);
		put(JA_OBJECTS_REQUEST, objects);
	}

	// 带的单据对象和会签对象
	public A5SignReqDcMessage(String content) {
		super();

		// 系统版本为A5
		put(SYS_VERSION_INITIAL, SYS_VERSION_A5);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A5);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

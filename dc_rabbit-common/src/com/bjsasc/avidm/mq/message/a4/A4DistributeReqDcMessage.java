package com.bjsasc.avidm.mq.message.a4;

import org.json.JSONArray;

import com.bjsasc.avidm.mq.message.DistributeReqDcMessage;

// 分发：A4系统 --> 数据中心
public class A4DistributeReqDcMessage extends DistributeReqDcMessage {

	// 带的单据对象和分发对象
	public A4DistributeReqDcMessage(JSONArray objects) {
		super();

		// 系统版本为A4
		put(SYS_VERSION_INITIAL, SYS_VERSION_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A4);

		put(JA_OBJECTS_INITIAL, objects);
		put(JA_OBJECTS_REQUEST, objects);
	}

	// 带的单据对象和分发对象
	public A4DistributeReqDcMessage(String content) {
		super();

		// 系统版本为A4
		put(SYS_VERSION_INITIAL, SYS_VERSION_A4);
		put(SYS_VERSION_REQUEST, SYS_VERSION_A4);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

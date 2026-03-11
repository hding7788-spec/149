package com.bjsasc.avidm.mq.message.win11;

import org.json.JSONArray;

import com.bjsasc.avidm.mq.message.ShareReqDcMessage;

// 预审：WIN11系统 --> 数据中心
public class Win11ShareReqDcMessage extends ShareReqDcMessage {

	// 带的预审对象
	public Win11ShareReqDcMessage(JSONArray objects) {
		super();

		// 系统版本为Win11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

		put(JA_OBJECTS_INITIAL, objects);
		put(JA_OBJECTS_REQUEST, objects);
	}

	// 带的单据对象和共享对象
	public Win11ShareReqDcMessage(String content) {
		super();

		// 系统版本为win11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

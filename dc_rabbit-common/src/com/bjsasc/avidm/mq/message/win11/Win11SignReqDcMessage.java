package com.bjsasc.avidm.mq.message.win11;

import org.json.JSONArray;

import com.bjsasc.avidm.mq.message.SignReqDcMessage;

// 会签：WIN11系统 --> 数据中心
public class Win11SignReqDcMessage extends SignReqDcMessage {

	// 带的单据对象和会签对象
	public Win11SignReqDcMessage(JSONArray objects) {
		super();

		// 系统版本为WIN11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

		put(JA_OBJECTS_INITIAL, objects);
		put(JA_OBJECTS_REQUEST, objects);
	}

	// 带的单据对象和会签对象
	public Win11SignReqDcMessage(String content) {
		super();

		// 系统版本为WIN11
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN11);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN11);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

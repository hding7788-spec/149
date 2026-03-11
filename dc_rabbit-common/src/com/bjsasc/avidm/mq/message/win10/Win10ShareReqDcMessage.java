package com.bjsasc.avidm.mq.message.win10;

import org.json.JSONArray;

import com.bjsasc.avidm.mq.message.ShareReqDcMessage;

// 预审：WIN10系统 --> 数据中心
public class Win10ShareReqDcMessage extends ShareReqDcMessage {

	// 带的预审对象
	public Win10ShareReqDcMessage(JSONArray objects) {
		super();

		// 系统版本为Win10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

		put(JA_OBJECTS_INITIAL, objects);
		put(JA_OBJECTS_REQUEST, objects);
	}

	// 带的单据对象和预审对象
	public Win10ShareReqDcMessage(String content) {
		super();

		// 系统版本为win10
		put(SYS_VERSION_INITIAL, SYS_VERSION_WIN10);
		put(SYS_VERSION_REQUEST, SYS_VERSION_WIN10);

		put(MSG_CONTENT_INITIAL, content);
		put(MSG_CONTENT_REQUEST, content);
	}

}

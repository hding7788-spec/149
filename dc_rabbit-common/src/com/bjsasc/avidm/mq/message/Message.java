package com.bjsasc.avidm.mq.message;

import org.json.JSONObject;


// 基础的消息
public abstract class Message extends JSONObject implements Based {

	public Message() {
		// 包含ID和创建时间
		//put(MSG_ID, TUUID.getUUID());
		put(MSG_CREATED_TIME, System.currentTimeMillis());
	}
}

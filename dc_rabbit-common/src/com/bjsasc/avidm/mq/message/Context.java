package com.bjsasc.avidm.mq.message;

import org.json.JSONObject;

// 上下文的消息
public class Context extends JSONObject implements Based {

	public Context(String iid, String id, String name) {
		put(IID, iid);
		put(ID, id);
		put(NAME, name);
	}
}

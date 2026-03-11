package com.bjsasc.avidm.mq.message;

import org.json.JSONObject;

// 单据的消息
public class Order extends JSONObject implements Based {

	public Order(String iid, String name, String description, String url) {
		put(IID, iid);
		put(NAME, name);
		put(DESCRIPTION, description);
		put(URL, url);
	}
}

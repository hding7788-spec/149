package com.bjsasc.avidm.mq.framework;

import org.json.JSONObject;

// 统一的消息事件接口
public interface Event {

	// 返回该事件的类型
	Class<? extends Event> getType();

	// 匹配的标识
	String matchID();

	// 设置该事件的内容
	void setContent(JSONObject jso);

	// 返回该事件的内容
	JSONObject getContent();
}

package com.bjsasc.avidm.mq.framework;

import org.json.JSONObject;

// 统一的抽象事件类，所有的其它事件从此类继承
public abstract class AbstractEvent implements Event {

	private JSONObject obj;

	@Override
	public void setContent(JSONObject jso) {
		obj = jso;
	}

	// 返回该事件的类名
	@Override
	public Class<? extends Event> getType() {
		return getClass();
	}

	@Override
	public JSONObject getContent() {
		return obj;
	}

	// 缺省不匹配任何事件
	@Override
	public String matchID() {
		return "";
	}
}

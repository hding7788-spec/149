package com.bjsasc.avidm.mq.framework;

// 统一的事件处理器接口
public interface Handler<E extends Event> {

	// 设置事件
	void setEvent(E event);

	// 处理事件
	void onEvent(E event);

	// 支持处理哪种事件
	Class<E> supportEvent();
}

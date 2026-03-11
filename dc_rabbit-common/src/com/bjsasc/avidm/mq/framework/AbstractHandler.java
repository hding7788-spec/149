package com.bjsasc.avidm.mq.framework;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbstractHandler<E extends Event> implements Handler<E> {

	E e;

	@Override
	public Class<E> supportEvent() {
		ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
		Type[] types = parameterizedType.getActualTypeArguments();
		@SuppressWarnings("unchecked")
		Class<E> clazz = (Class<E>) types[0];
		return clazz;
	}

	@Override
	public void setEvent(E event) {
		e = event;
	}
}

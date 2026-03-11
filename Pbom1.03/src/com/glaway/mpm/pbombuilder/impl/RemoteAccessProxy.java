package com.glaway.mpm.pbombuilder.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import wt.method.RemoteMethodServer;

public class RemoteAccessProxy implements InvocationHandler {

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (!RemoteMethodServer.ServerFlag) {
			return RemoteMethodServer.getDefault().invoke(method.getName(),
					proxy.getClass().getName(), proxy, method.getParameterTypes(), args);
		}
		return method.invoke(proxy, args);
	}

}

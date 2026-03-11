package com.glaway.mpm.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogUtil {
	static {
		PropertyConfigurator.configure(LogUtil.class.getResource("/resource/log4j.properties"));
	}

	public static Logger getLogger(Class<?> clazz) {
		return Logger.getLogger(clazz);
	}
}
package com.glaway.mpm.util;

/**
 * @author fly
 * @version 1.0
 * @since 2011-09-19
 */
public class GLLogger {
	private static final String GLAWAY_LOGGER_NAME = "glawayLogger";
	private static final org.apache.log4j.Logger GLLOGGER = org.apache.log4j.Logger
			.getLogger(GLAWAY_LOGGER_NAME);

	public static boolean isDebugEnabled(){
		return GLLOGGER.isDebugEnabled();
	}
	
	public static boolean isInfoEnabled(){
		return GLLOGGER.isInfoEnabled();
	}
	
	public static boolean isTraceEnabled(){
		return GLLOGGER.isTraceEnabled();
	}
	
	public static final void debug(Object message) {
		GLLOGGER.debug(message);
	}

	public static final void info(Object message) {
		GLLOGGER.info(message);
	}

	public static final void error(Object message) {
		GLLOGGER.error(message);
	}

	public static final void warn(Object message) {
		GLLOGGER.warn(message);
	}

	public static final void fatal(Object message) {
		GLLOGGER.fatal(message);
	}

	public static final void debug(Object messageClass, Object message) {
		message = getMessageClassName(messageClass) + " : " + message;
		GLLOGGER.debug(message);
	}

	public static final void info(Object messageClass, Object message) {
		message = getMessageClassName(messageClass) + " : " + message;
		GLLOGGER.info(message);
	}

	public static final void error(Object messageClass, Object message) {
		message = getMessageClassName(messageClass) + " : " + message;
		GLLOGGER.error(message);
	}

	public static final void warn(Object messageClass, Object message) {
		message = getMessageClassName(messageClass) + " : " + message;
		GLLOGGER.warn(message);
	}

	public static final void fatal(Object messageClass, Object message) {
		message = getMessageClassName(messageClass) + " : " + message;
	}

	public static final void debug(String CLASSNAME, Object message) {
		GLLOGGER.debug(CLASSNAME + " : " + message);
	}

	public static final void info(String CLASSNAME, Object message) {
		GLLOGGER.info(CLASSNAME + " : " + message);
	}

	public static final void error(String CLASSNAME, Object message) {
		GLLOGGER.error(CLASSNAME + " : " + message);
	}

	public static final void warn(String CLASSNAME, Object message) {
		GLLOGGER.warn(CLASSNAME + " : " + message);
	}

	public static final void fatal(String CLASSNAME, Object message) {
		GLLogger.fatal(CLASSNAME + " : " + message);
	}

	public static final org.apache.log4j.Logger getLog4jLogger() {
		return GLLOGGER;
	}

	public static final void debug(String CLASSNAME, String methodName,
			Object message) {
		message = CLASSNAME + " : " + methodName + " - " + message;
		GLLOGGER.debug(message);
	}

	public static final void info(String CLASSNAME, String methodName,
			Object message) {
		message = CLASSNAME + " : " + methodName + " - " + message;
		GLLOGGER.debug(message);
	}

	public static final void error(String CLASSNAME, String methodName,
			Object message) {
		message = CLASSNAME + " : " + methodName + " - " + message;
		GLLOGGER.debug(message);
	}

	public static final void warn(String CLASSNAME, String methodName,
			Object message) {
		message = CLASSNAME + " : " + methodName + " - " + message;
		GLLOGGER.debug(message);
	}

	public static final void fatal(String CLASSNAME, String methodName,
			Object message) {
		message = CLASSNAME + " : " + methodName + " - " + message;
		GLLOGGER.debug(message);
	}

	public static final String getMessageClassName(Object messageClass) {
		if (messageClass != null) {
			String temp = messageClass.getClass().getName();
			return temp.substring(temp.lastIndexOf(".") + 1, temp.length());
			// return messageClass.getClass().getName();
		} else {
			return "";
		}
	}
}
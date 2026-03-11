package com.bjsasc.avidm.mq.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtil {
	public static String getStackTrace(Throwable exception) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		return sw.toString();
	}
}
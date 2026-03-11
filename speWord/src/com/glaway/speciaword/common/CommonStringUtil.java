package com.glaway.speciaword.common;

/***
 * 
 * @author mosesx
 * @date 2013-4-23
 * @version V1.0
 */
public class CommonStringUtil {

	public static boolean isEmpty(String obj) {
		if (null == obj || "".equals(obj.trim())) {
			return true;
		} else {
			return false;
		}
	}

	public static String booleanToString(boolean flag) {
		if (flag) {
			return "Y";
		} else {
			return "N";
		}
	}

	public static String emptyToNumber(String obj) {
		if (null == obj || "".equals(obj.trim())) {
			return "0";
		} else {
			return String.valueOf(Integer.parseInt(obj));
		}
	}

	public static String emptyToString(String obj) {
		if (null == obj || "".equals(obj.trim())) {
			return "";
		} else {
			return obj;
		}
	}

	public static long emptyToLong(String obj) {
		if (null == obj || "".equals(obj)) {
			return 0;
		} else {
			return Long.parseLong(obj);
		}
	}
}

package com.glaway.mpm.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SwitchUtil {

	// 原capp特殊字符
	public static final int SPECIALCHAR_INDEX = 22;

	public static void main(String[] args) {
		// System.out.println(toolToHashMap(new Frock("1", "2", "3", "4", "5"),
		// "frock"));
		// Equipment ep1 = new Equipment("1", "2", "3", "4");
		// Equipment ep2 = new Equipment("11", "12", "33", "41");
		// Equipment ep3 = new Equipment("12", "23", "34", "42");
		// List<Equipment> list = new ArrayList<Equipment>();
		// list.add(ep1);
		// list.add(ep2);
		// list.add(ep3);
		// System.out.println(list);
	}

	public static HashMap<String, String> javaBeanToHashMap(Object object) {
		HashMap<String, String> map = new HashMap<String, String>();
		Class<?> clazy = object.getClass();
		List<Class<?>> clazz = new ArrayList<Class<?>>();
		while (!clazy.getName().equals("java.lang.Object")) {
			clazz.add(clazy);
			clazy = clazy.getSuperclass();
		}
		for (Class<?> temp : clazz) {
			for (Field field : temp.getDeclaredFields()) {
				try {
					field.setAccessible(true);
					map.put(field.getName(), trim(field.get(object)));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}

	public static HashMap<String, String> toolToHashMap(Object object,
			String str) {
		HashMap<String, String> map = new HashMap<String, String>();
		Class<?> clazy = object.getClass();
		List<Class<?>> clazz = new ArrayList<Class<?>>();
		while (!clazy.getName().equals("java.lang.Object")) {
			clazz.add(clazy);
			clazy = clazy.getSuperclass();
		}
		for (Class<?> temp : clazz) {
			for (Field field : temp.getDeclaredFields()) {
				try {
					field.setAccessible(true);
					if("frockType".equals(field.getName())) {
						map.put(field.getName(), trim(field.get(object)));
					} else {
						map.put(field.getName().replaceFirst(str, "tool"), trim(field.get(object)));
					}
					map.put(field.getName().replaceFirst(str, "tool"), trim(field.get(object)));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}

	private static String trim(Object object) {
		String value = (object == null) ? "" : object.toString();
		return CommonUtil.trim(value);

	}

	/**
	 * 过滤特殊字符
	 *
	 * @param str
	 * @return
	 */
	public static String filterSpecialChar(String str) {
		if (str != null && str.length() != 0) {
			char c = (char) SPECIALCHAR_INDEX;
			String special = String.valueOf(c);
			str = str.replaceAll(special, "");
			// List<Integer> list = new ArrayList<Integer>();
			// for (int i = 0; i < str.length(); i++) {
			// char temp = str.charAt(i);
			// int index = (int) temp;
			// if (index == SPECIALCHAR_INDEX) {
			// list.add(i);
			// }
			// }
			// int size = list.size();
			// if (size != 0) {
			// List<String> returnStr = new ArrayList<String>();
			// for (int i = 0; i < size; i++) {
			// returnStr.add(str.substring(0, list.get(i)));
			// }
			//
			// }
			//
		}
		return str;
	}
}

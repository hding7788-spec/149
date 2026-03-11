package com.bjsasc.avidm.mq.util;

import java.util.UUID;

// 生成UUID的工具类
public class TUUID {

	public static String getUUID() {
		UUID id = UUID.randomUUID();
		String s = id.toString();

		s = s.replace("-", "");
		return s;
	}

}

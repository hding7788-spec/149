package com.test;

import java.io.UnsupportedEncodingException;

public class Test {

	static int GB_SP_DIFF = 160;
	// 存放国标一级汉字不同读音的起始区位码
	static int[] secPosValueList = { 1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635,
			3722, 3730, 3858, 4027, 4086, 4390, 4558, 4684, 4925, 5249, 5600 };

	// 存放国标一级汉字不同读音的起始区位码对应读音
	static char[] firstLetter = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
			's', 't', 'w', 'x', 'y', 'z' };

	public static char convert(String ch) {
		// 国标码和区位码转换常量
		byte[] bytes = new byte[2];
		char result = '-';
		try {
			bytes = ch.getBytes("GB2312");
			byte[] bs = ch.getBytes("utf-8");
			System.out.println(bs.length);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return 'a';
		}

		int secPosValue = 0;
		int i;
		for (i = 0; i < bytes.length; i++) {
			bytes[i] -= GB_SP_DIFF;
		}
		if(bytes.length == 2) {
			secPosValue = bytes[0] * 100 + bytes[1];
		} else {
			secPosValue = bytes[0] * 100;
		}
		for (i = 0; i < 23; i++) {
			if (secPosValue >= secPosValueList[i] && secPosValue < secPosValueList[i + 1]) {
				result = firstLetter[i];
				break;
			}
		}
		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filefolder = "/Default/01设计文件/aaa";
		filefolder = filefolder.substring(0, 15);
		System.out.println(filefolder);
	}

}

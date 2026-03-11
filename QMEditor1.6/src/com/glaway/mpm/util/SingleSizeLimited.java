package com.glaway.mpm.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;

public class SingleSizeLimited extends PlainDocument {

	private String prefix;

	public SingleSizeLimited(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * 重写insertString()方法
	 */
	public void insertString(int offs, String str, AttributeSet attr) {
		try {
			System.out.println("str= " + str);
			if (str == null) {// 输入为空，直接返回
				return;
			}
			char[] charArray = str.toCharArray();// 将新输入字符串转换为字节数组

			int length = 0;
			for (int i = 0; i < charArray.length; i++) {
				if (charArray[i] >= '0' && charArray[i] <= '9') {// 筛选出数字
					charArray[length++] = charArray[i];
				} else {// 新输入全为数字
					charArray[length++] = charArray[i];
				}

			}
			super.insertString(offs, new String(charArray, 0, length), attr);// 插入满足条件的字符串

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

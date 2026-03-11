package com.glaway.mpm.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.PlainDocument;

public class InputLimited extends PlainDocument {

	private int limitedLength;
	private boolean flag;

	public InputLimited(int limitedLength, boolean flag) {
		this.limitedLength = limitedLength;
		this.flag = flag;
	}

	/**
	 * 重写insertString()方法
	 */
	public void insertString(int offs, String str, AttributeSet attr) {
		try {
			if (str == null) {// 输入为空，直接返回
				return;
			}
			if ((this.getLength() + str.length()) <= limitedLength) {// 原有的字符串和新输入的字符串长度小于限制长度
				char[] charArray = str.toCharArray();// 将新输入字符串转换为字节数组

				int length = 0;
				for (int i = 0; i < charArray.length; i++) {
					if (flag = true) {// 截取出数字
						if (charArray[i] >= '0' && charArray[i] <= '9') {// 筛选出数字
							charArray[length++] = charArray[i];
						}
					} else {// 新输入全为数字
						charArray[length++] = charArray[i];
					}

				}
				super.insertString(offs, new String(charArray, 0, length), attr);// 插入满足条件的字符串

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

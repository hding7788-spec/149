package com.glaway.mpm.parameter.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.glaway.mpm.log.VaLogger;

public class InputIntegerLimited extends PlainDocument {

	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(InputIntegerLimited.class.getName());
	private boolean flag = true;
	private int limitedLength;

	public InputIntegerLimited(int limitedLength, boolean flag) {
		this.limitedLength = limitedLength;
		this.flag = flag;
	}

	@Override
	public void insertString(int offs, String str, AttributeSet attr)
			throws BadLocationException {
		try {
			if (str == null) {
				return;
			}
			if ((this.getLength() + str.length()) <= limitedLength) {
				char[] charArray = str.toCharArray();
				int length = 0;
				for (int i = 0; i < charArray.length; i++) {
					if (flag) {
						if ((charArray[i] >= '0' && charArray[i] <= '9')) {
							charArray[length++] = charArray[i];
						}
					} else {
						charArray[length++] = charArray[i];
					}
				}
				super.insertString(offs, new String(charArray, 0, length), attr);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
}

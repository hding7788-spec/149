package com.glaway.mpm.parameter.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.glaway.mpm.log.VaLogger;


public class InputEnglishLimited extends PlainDocument {

	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(InputEnglishLimited.class.getName());

	@Override
	public void insertString(int offs, String str, AttributeSet attr)
			throws BadLocationException {
		try {
			if (str == null) {
				return;
			}
			char[] charArray = str.toCharArray();
			int length = 0;
			for (int i = 0; i < charArray.length; i++) {
				if ((charArray[i] >= 'a' && charArray[i] <= 'z')
						|| (charArray[i] >= 'A' && charArray[i] <= 'Z')
						|| (charArray[i] >= '0' && charArray[i] <= '9')
						|| (charArray[i] == '_')) {
					charArray[length++] = charArray[i];
				}
			}
			super.insertString(offs, new String(charArray, 0, length), attr);
		} catch (Exception e) {
			logger.error(e);
		}
	}
}

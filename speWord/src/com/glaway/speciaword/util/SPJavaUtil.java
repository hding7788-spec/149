package com.glaway.speciaword.util;

import java.awt.Toolkit;

import javax.swing.JDialog;

public class SPJavaUtil {

	public static String replaceAtSymbol(String str) {
		return str == null ? str : str.replace("&", "&amp;");
	}
	
	
	public static int SCREEN_WIDTH = Toolkit.getDefaultToolkit()
			.getScreenSize().width;

	public static int SCREEN_HEIGHT = Toolkit.getDefaultToolkit()
			.getScreenSize().height;

	public static void setMiddle(JDialog dialog) {
		dialog.setLocation((SCREEN_WIDTH - dialog.getWidth()) / 2,
				(SCREEN_HEIGHT - dialog.getHeight()) / 2);
	}
}

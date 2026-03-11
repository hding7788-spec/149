package com.glaway.mpm.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

public class ExceptionLogTool {
	private static final long FILE_LENGTH_LIMIT = 150000L;
	private static final String LOG_FILE_NAME = "exception.log";

	public static void writeException(Throwable throwable) {
		try {
			String filePath = WorkSpaceUtil.getWorkSpace() + "\\"
					+ "exception.log";
			writeException(throwable, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeException(Throwable throwable, String fileDirAndName) {
		try {
			Date date = new Date();
			DateFormat df = DateFormat.getDateTimeInstance();
			PrintWriter out = null;

			File file = new File(fileDirAndName);
			if (file.length() > 150000L)
				file.delete();
			out = new PrintWriter(new FileOutputStream(fileDirAndName, true));

			out.println(df.format(date) + " error:");
			throwable.printStackTrace(out);
			out.println("");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

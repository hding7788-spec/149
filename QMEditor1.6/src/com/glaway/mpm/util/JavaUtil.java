package com.glaway.mpm.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class JavaUtil {

	static String filePath1 = "C:\\Users\\ylshao\\Desktop\\新建文件夹 (3)";
	static String filePath2 = "C:\\Users\\ylshao\\Desktop\\新建文件夹";

	public static void main(String[] args) {
		// jar();
		getJarNames();
	}

	public static void jar() {
		// jar -cvf cappEditor.jar *.*
		File file = new File(filePath1);
		for (File subFile : file.listFiles()) {
			System.out.println("jar xf " + subFile.getName());
		}
	}

	public static void getJarNames() {
		File file = new File("C:\\Users\\ylshao\\git\\gwcapp\\lib");
		String str = "";
		for (File subFile : file.listFiles()) {
			if (subFile.getName().startsWith("batik")) {
				str += subFile.getName() + ",";
			}
		}
		System.out.println(str);
	}

	public static String switchCode(String str, String source, String out) {
		try {
			return new String(str.getBytes(source), out);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return str;
		}
	}

	public static void closeStream(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeStream(OutputStream os) {
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeStream(BufferedReader br) {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeStream(BufferedWriter bw) {
		if (bw != null) {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static String convertNull(Object str) {
		return str == null ? "" : str.toString();
	}

	public static String getSetMethodName(String str) {
		String methodName = null;
		if (str != null && str.trim().length() != 0) {
			methodName = "set" + str.substring(0, 1).toUpperCase()
					+ str.substring(1);
		}
		return methodName;
	}

	public static String transferBoolean(String str) {
		if ("false".equals(str)) {
			str = "否";
		} else if ("true".equals(str)) {
			str = "是";
		}
		return str;
	}

	public static String deletePrefix(String str) {
		if (str.startsWith(",")) {
			return str.substring(1);
		}
		return "";
	}
}

package com.glaway.mpm.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.dom4j.io.XMLWriter;

import com.glaway.mpm.visual.log.VaLogger;

public class CappJavaUtil {
	public static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyyMMddhhmmss");

	private static VaLogger logger = VaLogger.getLogger();

	static String filePath1 = "C:\\\\Users\\\\ylshao\\\\Desktop\\\\新建文件夹 (3)";
	static String filePath2 = "C:\\\\Users\\\\ylshao\\\\Desktop\\\\新建文件夹";

	public static String[] UNSUPPORT_SEPARATOR = new String[] { "\\", "/", "<",
		">", ":", "*", "\"", "?", "|" };

	public static void jar() {
		// jar -cvf cappEditor.jar *.*
		File file = new File(filePath1);
		for (File subFile : file.listFiles()) {
			logger.debug("jar xf " + subFile.getName());
		}
	}

	public static void getJarNames() {
		File file = new File("C:\\\\Users\\\\ylshao\\\\git\\\\gwcapp\\\\lib");
		String str = "";
		for (File subFile : file.listFiles()) {
			if (subFile.getName().startsWith("batik")) {
				str += subFile.getName() + ",";
			}
		}
		logger.debug(str);
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

	public static void closeWriter(FileWriter fr) {
		if (fr != null) {
			try {
				fr.close();
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

	public static void closeStream(XMLWriter writer) {
		if (writer != null) {
			try {
				writer.close();
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
		return CappJavaUtil.convertNull(str);
	}

	public static String deletePrefix(String str) {
		if (str.startsWith(",")) {
			return str.substring(1);
		}
		return "";
	}

	public static void openFile(String filePath) {
		try {
			Runtime.getRuntime()
					.exec("cmd /c start \"\"  \"" + filePath + "\"");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "文件打开失败", "提示", 1);
			e.printStackTrace();
		}

	}

	public static void openFileWithTool(String toolPath, String filePath) {
		try {
			Runtime.getRuntime().exec(
					"cmd /c start \"\"  \"" + toolPath + "\"  \"" + filePath
							+ "\" ");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "文件打开失败", "提示", 1);
			e.printStackTrace();
		}

	}

	public static void openCatiaFile(String toolPath,String toolEnvPath,String filePath) {
		try {
//			System.out.println("---toolPath--"+toolPath);
//			System.out.println("---toolEnvPath--"+toolEnvPath);
//			System.out.println("---filePath--"+filePath);
			String cmd = "\"" + toolPath + "\"  -run \"CNEXT.exe\" -env WGM_10.0_CATIA_V5_R21 -direnv \"" + toolEnvPath + "\" -object " + "\"  \"" + filePath + "\" -nowindow";
//			System.out.println("---cmd--"+cmd);
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "文件打开失败", "提示", 1);
			e.printStackTrace();
		}
	}

	public static boolean isBlank(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static boolean isNumber(String str) {
		boolean flag = false;
		if (str != null) {
			Pattern pattern = Pattern.compile("^[1-9][0-9]*$");
			flag = pattern.matcher(str).find();
		}

		return flag;
	}

	public static boolean isAllNumber(String str) {
		boolean flag = false;
		if (str != null) {
			Pattern pattern = Pattern.compile("^[0-9][0-9]*$");
			flag = pattern.matcher(str).find();
		}

		return flag;
	}

	public static void main(String[] args) {
		System.out.println(replaceSeperator("1mom312m!@(**($$<>:!<$@<:"));
	}

	public static boolean isPositiveNumber(String str) {
		boolean flag = false;
		if (str != null) {
			Pattern pattern = Pattern.compile("^\\d*(\\.)?\\d*$");
			flag = pattern.matcher(str).find() && !".".equals(str);
		}
		return flag;
	}

	public static String toHexString(String oid) {
		if (CappJavaUtil.isAllNumber(oid)) {
			oid = Integer.toHexString(Integer.parseInt(oid));
			int length = oid.length();
			if (length < 6) {
				for (int i = 0; i < 6 - length; i++) {
					oid = "0" + oid;
				}
			}
			oid = oid.toUpperCase();
		}
		return oid;
	}

	/**
	 * 生成Id
	 *
	 * @return
	 */
	public static String generateUniqueId() {
		return CappJavaUtil.simpleDateFormat.format(new Date())
				+ System.nanoTime();
	}

	/**
	 *
	 * @return
	 */
	public static String getDateStr() {
		return CappJavaUtil.simpleDateFormat.format(new Date());
	}

	/**
	 * 替换分隔符
	 *
	 * @param str
	 * @return
	 */
	public static String replaceSeperator(String str) {
		if (str != null) {
			for (String temp : UNSUPPORT_SEPARATOR) {
				str = str.replace(temp, "~");
			}
		}
		return str;
	}

	public static String replaceAtSymbol(String str) {
		return str == null ? str : str.replace("&", "&amp;");
	}

	public static String removeContainsStr(String[] array, String str) {
		String returnStr = null;
		boolean flag = false;
		if (array != null) {
			returnStr = "";
			for (String temp : array) {
				if (temp.equals(str)) {
					flag = true;
				} else {
					returnStr += "," + temp;
				}
			}
		}
		if (!flag) {
			returnStr = null;
		} else {
			if (returnStr.length() > 0) {
				returnStr = returnStr.substring(1);
			}
		}
		return returnStr;

	}

	public static String getFrockName(Object name) {
		String frockName = null;
		if (name != null) {
			frockName = name.toString();
			if (frockName != null && frockName.indexOf("(") != -1) {
				frockName = frockName.substring(0, frockName.indexOf("("));
			}
			if (frockName != null && frockName.indexOf("（") != -1) {
				frockName = frockName.substring(0, frockName.indexOf("（"));
			}
		}
		return frockName;
	}

	public static double strToDouble(String str) {
		double d = 0;
		try {
			d = Double.parseDouble(str);
		} catch (Exception e) {
		}
		return d;

	}

	public static boolean isBetween(double start, double end, String str) {
		boolean flag = false;
		if (str != null) {
			try {
				double d = Double.parseDouble(str);
				flag = d >= start && d <= end;
			} catch (Exception e) {

			}
		}
		return flag;
	}
}

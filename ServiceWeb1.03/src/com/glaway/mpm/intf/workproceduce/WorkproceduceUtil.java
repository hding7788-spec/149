package com.glaway.mpm.intf.workproceduce;

import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.WTContainerUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.inf.container.WTContainer;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class WorkproceduceUtil implements RemoteAccess {

	public static void main(String[] args) throws BiffException, WTException, PropertyVetoException, IOException {
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		methodServer.setUserName("wcadmin");
		methodServer.setPassword("wcadmin");
		try {
			methodServer.invoke("getWrokproceduces", WorkproceduceUtil.class.getName(), null, new Class[] {},
					new Object[] {});
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	static int GB_SP_DIFF = 160;
	// 存放国标一级汉字不同读音的起始区位码
	static int[] secPosValueList = { 1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635,
			3722, 3730, 3858, 4027, 4086, 4390, 4558, 4684, 4925, 5249, 5600 };

	// 存放国标一级汉字不同读音的起始区位码对应读音
	static char[] firstLetter = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
			's', 't', 'w', 'x', 'y', 'z' };

	/**
	 * 工序名称对象存放位置
	 */
	private static Map<String, String> WORKPROCEDUCES = new HashMap<String, String>();
	private static String WORKPROCEDECES_DOCNAME = "working procedure name list";
	private static String WORKPROCEDECES_DOC_CONTAINERNAME = "工艺资源库";

	public static HashMap<String, String> getWrokproceduces() throws WTException, PropertyVetoException, BiffException,
			IOException {
		GLLogger.debug("getWrokproceduces start...");
		WTContainer lib = WTContainerUtil.getContainerByName(WORKPROCEDECES_DOC_CONTAINERNAME);
		GLLogger.debug("lib name=" + lib.getContainerName());
		WTDocument wp_doc = WTDocumentUtil.getWrokproceduceDoc(WORKPROCEDECES_DOCNAME, lib);
		GLLogger.debug("wp_doc name=" + wp_doc.getName());
		ContentItem item = ContentHelper.service.getPrimary(wp_doc);
		ApplicationData ad = (ApplicationData) item;
		GLLogger.debug("filename---" + ad.getFileName());
		HashMap<String, String> WORKPROCEDUCES = new HashMap<String, String>();
		InputStream inputStream = ContentServerHelper.service.findContentStream(ad);
		WorkbookSettings wbs = new WorkbookSettings();
		wbs.setGCDisabled(true);
		wbs.setSuppressWarnings(true);
		Workbook wb = Workbook.getWorkbook(inputStream, wbs);
		Sheet sheet0 = wb.getSheet(0);
		String wpName = sheet0.getCell(1, 2).getContents();
		int rowNumber = sheet0.getRows();
		GLLogger.debug("rowNumber="+rowNumber);
		for (int i = 0; i < rowNumber; i++) {
			wpName = sheet0.getCell(1, i).getContents();
			String convertStr = convertStr(wpName);
			GLLogger.debug("wpName" + i + " " + wpName + " = " + convertStr);
			WORKPROCEDUCES.put(wpName, convertStr);
		}
		inputStream.close();
		return WORKPROCEDUCES;
	}

	public static String convertStr(String str) {
		String convertStr = "";
		if(str.contains("/"))str=str.replace("/", "");
		if(str.contains("\\"))str=str.replace("\\", "");
		if(str.contains("-"))str=str.replace("-", "");
		if(str.contains("("))str=str.replace("(", "");
		if(str.contains(")"))str=str.replace(")", "");
		if(str.contains("）"))str=str.replace("）", "");
		if(str.contains("（"))str=str.replace("（", "");
		if(str.contains("."))str=str.replace(".", "");
		if(str.contains("0"))str=str.replace("0", "l");
		if(str.contains("1"))str=str.replace("1", "y");
		if(str.contains("2"))str=str.replace("2", "e");
		if(str.contains("3"))str=str.replace("3", "s");
		if(str.contains("4"))str=str.replace("4", "s");
		if(str.contains("5"))str=str.replace("5", "w");
		if(str.contains("6"))str=str.replace("6", "l");
		if(str.contains("7"))str=str.replace("7", "q");
		if(str.contains("8"))str=str.replace("8", "b");
		if(str.contains("9"))str=str.replace("9", "j");
		if (str != null && !"".equals(str)) {
			for (int i = 0; i < str.length(); i++) {
				String substr=str.substring(i, i + 1);
				char c=substr.charAt(0);
				if(('z'>=c&&c>='a')||'Z'>=c&&c>='A'){
					convertStr = convertStr+substr;
				}else{
					convertStr = convertStr + convert(substr);
				}
			}
		}
		return convertStr;
	}

	static char convert(String ch) {
		if("钣".equals(ch)){
			return 'b';
		} else if("镗".equals(ch)) {
			return 't';
		} else if("调".equals(ch)) {
			return 't';
		}
		// 国标码和区位码转换常量
		byte[] bytes = new byte[2];
		char result = '-';
		try {
			bytes = ch.getBytes("GB2312");
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

}

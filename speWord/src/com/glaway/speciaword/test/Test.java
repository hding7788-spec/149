package com.glaway.speciaword.test;

import java.util.regex.Pattern;

public class Test {
	public static void main(String[] args) {
//		List<ParamPart> lstPart = ExcelUtil.parseExcel(new File("C:\\Users\\MosesX\\Desktop\\工艺文件下载打印（按整件清单）-模板.xls"));
//		for (ParamPart temp : lstPart) {
//			String tempFileName = "" + System.currentTimeMillis();
//			String onBuildNumber = temp.getOnBuildNumber();
//			String partNumber = temp.getPartNumber();
//			System.out.println(partNumber);
//			if ("".equals(partNumber) || null == partNumber) {
//				continue;
//			}
//			System.out.println(isALZJPart(partNumber));
//		}
	}

	public static boolean isALZJPart(String number) {
		return Pattern.matches("(ALK|AL|ALJ|Q/AL)[1-4]", number.substring(0, number.indexOf(".")));
	}
}

class ExcelUtil {
//	public static List<ParamPart> parseExcel(File file) {
//		List<ParamPart> list = new ArrayList<ParamPart>();
//
//		try {
//			Workbook wb = Workbook.getWorkbook(file);
//			if (wb == null)
//				return list;
//
//			Sheet[] sheetlist = wb.getSheets();
//			for (Sheet sheet : sheetlist) {
//				int rowNum = sheet.getRows();
//				for (int j = 0; j < rowNum; j++) {
//					Cell[] cells = sheet.getRow(j);
//					int index = 0;
//					String OnBuildNumber = "";
//					String PartNumber = "";
//					String ProcessVersion = "";
//					while (cells.length > index) {
//						if (index == 0) {
//							OnBuildNumber = cells[0] != null ? cells[0].getContents() : "";
//						} else if (index == 1) {
//							PartNumber = cells[1] != null ? cells[1].getContents() : "";
//						} else if (index == 2) {
//							ProcessVersion = cells[2] != null ? cells[2].getContents() : "";
//						}
//						index++;
//					}
//
//					ParamPart temp = new ParamPart(OnBuildNumber.trim(), PartNumber.trim(), ProcessVersion.trim());
//					list.add(temp);
//				}
//			}
//			wb.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		// for(ParamPart p : list){
		// System.out.println(p);
		// }
//		return list;
	}

//}

class ParamPart {

	private String onBuildNumber;
	private String partNumber;
	private String processVersion;

	public ParamPart(String onBuildNumber, String partNumber, String processVersion) {
		super();
		this.onBuildNumber = onBuildNumber;
		this.partNumber = partNumber;
		this.processVersion = processVersion;
	}

	public String getProcessVersion() {
		return processVersion;
	}

	public void setProcessVersion(String processVersion) {
		this.processVersion = processVersion;
	}

	public String getOnBuildNumber() {
		return onBuildNumber;
	}

	public void setOnBuildNumber(String onBuildNumber) {
		this.onBuildNumber = onBuildNumber;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	@Override
	public String toString() {
		return "ParamPart [onBuildNumber=" + onBuildNumber + ", partNumber=" + partNumber + ", processVersion="
				+ processVersion + "]";
	}
}
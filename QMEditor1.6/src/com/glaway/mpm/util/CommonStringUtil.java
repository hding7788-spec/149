package com.glaway.mpm.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;

import com.glaway.mpm.qmIntf.commonString.CsTreeXmlUtil;
import org.apache.poi.ss.usermodel.VerticalAlignment;

public class CommonStringUtil {

	/**
	 * 根据Excel模板获取常用语Map
	 *
	 * @param workbook
	 * @return
	 */
	private static Map<String, List<String>> getCommonStringsFromWorkbook(
			HSSFWorkbook workbook) {
		Map<String, List<String>> commonStrings = null;
		if (workbook != null) {
			commonStrings = new LinkedHashMap<String, List<String>>();
			HSSFSheet sheet = workbook.getSheetAt(0);
			List<String> list = new ArrayList<String>();
			String type = "";
			boolean flag = true;
			for (int i = 0; i < sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					Cell cell = row.getCell(0);
					if (cell != null
							&& cell.getCellType() != HSSFCell.CELL_TYPE_BLANK
							&& flag) {
						type = ExcelUtil.getCellStringValue(cell);
						Cell cell1 = row.getCell(1);
						if (cell1 != null
								&& cell1.getCellType() != HSSFCell.CELL_TYPE_BLANK
								&& ExcelUtil.getCellStringValue(cell1) != null
								&& !"".equals(ExcelUtil
										.getCellStringValue(cell1))) {
							list.add(ExcelUtil.getCellStringValue(cell1));
						}
					} else {
						flag = false;
						Cell cell1 = row.getCell(1);
						if (cell1 == null
								|| cell1.getCellType() == HSSFCell.CELL_TYPE_BLANK
								|| ExcelUtil.getCellStringValue(cell1) == null
								|| "".equals(ExcelUtil
										.getCellStringValue(cell1))) {
							if (!"".equals(type)) {
								commonStrings.put(type, list);
								type = "";
								list = new ArrayList<String>();
							}
							flag = true;
						} else {
							list.add(ExcelUtil.getCellStringValue(cell1));
						}
					}
					continue;
				}
				if (!"".equals(type)) {
					commonStrings.put(type, list);
					type = "";
					list = new ArrayList<String>();
					flag = true;
				}
			}
			if (!"".equals(type)) {
				commonStrings.put(type, list);
				type = "";
				list = new ArrayList<String>();
				flag = true;
			}
		}
		return commonStrings;
	}

	public static void importCommonStringFromWorkbook(HSSFWorkbook workbook) {
		Map<String, List<String>> map = getCommonStringsFromWorkbook(workbook);
		System.out.println(map);
		CsTreeXmlUtil.mergeCsFromTemplate(map);
	}

	public static void exportCommonString(String directory) {
		if (!directory.endsWith(".xls")) {
			directory = directory.concat(".xls");
		}
		// String excelPath = directory + File.separator + "常用语模板.xls";
		Map<String, List<String>> map = CsTreeXmlUtil.getCommonStringMap();
		if (map == null || map.size() == 0) {
			JOptionPane.showMessageDialog(null, "个人工艺常用语为空！", "提示", 1);
		}
		writeExcel(directory, map);
	}

	public static void writeExcel(String excelPath,
			Map<String, List<String>> map) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle style = wb.createCellStyle(); // 样式对象
		style.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
		style.setAlignment(HorizontalAlignment.CENTER);// 水平
		HSSFSheet sheet = wb.createSheet("常用语模板");
		Set<Entry<String, List<String>>> set = map.entrySet();
		int i = 0;
		for (Entry<String, List<String>> entry : set) {
			String type = entry.getKey();
			List<String> commonStrings = entry.getValue();
			HSSFRow row = sheet.createRow(i);
			HSSFCell cell = row.createCell(0);

			// 写常用语
			if (commonStrings != null && commonStrings.size() != 0) {
				for (String cs : commonStrings) {
					HSSFRow row1 = sheet.createRow(i);
					HSSFCell cell1 = row1.createCell(1);
					cell1.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell1.setCellValue(cs);
					cell1.setCellStyle(style);
					i++;
				}
			}

			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(type);
			cell.setCellStyle(style);
			i = i + 1;
			// 写空白行
			HSSFRow blankRow = sheet.createRow(i + 1);
			HSSFCell blankCell = blankRow.createCell(0);
			blankCell.setCellType(HSSFCell.CELL_TYPE_BLANK);
			blankCell.setCellStyle(style);

		}

		FileOutputStream writeFile = null;
		try {
			writeFile = new FileOutputStream(excelPath);
			wb.write(writeFile);
			writeFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		// File file = new File("C:\\Users\\xuehu\\Desktop\\常用语导入模板.xls");
		// HSSFWorkbook workbook = ExcelUtil.getWorkbook(file);
		// CommonStringUtil.importCommonStringFromWorkbook(workbook);
		CommonStringUtil.exportCommonString("C:\\Users\\xuehu\\Desktop\\");
	}
}

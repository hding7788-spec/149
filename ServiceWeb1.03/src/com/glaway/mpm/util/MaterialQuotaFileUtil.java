package com.glaway.mpm.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.glaway.mpm.model.MaterialCal;

public class MaterialQuotaFileUtil {
	static String path = "C:\\Users\\Administrator\\Desktop\\MaterialQuotaFile.xls";

	public static List<MaterialCal> readExcel(InputStream is) {
		List<MaterialCal> materialCals = new ArrayList<MaterialCal>();
		try {
			// Excel 2007
			// Workbook wb = new XSSFWorkbook(is);
			// Excel 2003
			HSSFWorkbook wb = new HSSFWorkbook(is);
			Sheet sheet = wb.getSheetAt(0);
			for (int i = 1; i < sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				Cell cell = row.getCell(1);
				if (cell.getCellType() == 3) {
					continue;
				}
				MaterialCal meterialCal = new MaterialCal();
				// 名称
				meterialCal.setName(getCellValue(row.getCell(0), sheet, i, 0));
				// 编码
				meterialCal.setNumber(cell.toString());
				// 密度
				meterialCal.setMaterialDensity(getCellValue((row.getCell(2)), sheet, i, 2));
				// 材料损耗系数
				meterialCal.setAttritionRate(getCellValue((row.getCell(3)), sheet, i, 3));
				// 单件毛坯尺寸
				meterialCal.setSingleSize(getCellValue((row.getCell(4)), sheet, i, 4));
				// 公式
				meterialCal.setFormula(getCellValue((row.getCell(5)), sheet, i, 5));
				// 定额单位
				meterialCal.setMaterialUnit(getCellValue((row.getCell(6)), sheet, i, 6));
				materialCals.add(meterialCal);
				if (GLLogger.isDebugEnabled()) {
					System.out.println("meterialCal name=" + meterialCal.getName() + " " + meterialCal.getSingleSize()
							+ " " + meterialCal.getFormula());
				}
				// System.out.println("meterialCal name="+meterialCal.getName()+" "+meterialCal.getSingleSize()
				// +" "+meterialCal.getFormula());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return materialCals;

	}

	/**
	 * 获取合并单元格的值
	 * 
	 * @author fly
	 * @date 2013-5-6
	 * @param cell
	 * @param sheet
	 * @param row
	 * @param column
	 * @return
	 * 
	 */
	protected static String getCellValue(Cell cell, Sheet sheet, int row, int column) {
		String value = null;
		if (cell.getCellType() == 3) {
			for (int i = row - 1; i > 1; i--) {
				Cell previousCell = sheet.getRow(i).getCell(column);
				if (previousCell.getCellType() == 3) {
					continue;
				} else {
					value = previousCell.toString();
					break;
				}
			}
		} else {
			value = cell.toString();
		}
		return value;
	}

	public static void main(String[] args) {
		try {
			readExcel(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}

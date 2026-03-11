package com.glaway.mpm.mpmresource.gznumber.loader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelFileDataSource implements DataSource{
	private HSSFSheet sheet;
	private HSSFWorkbook wb;
	
	public ExcelFileDataSource(String filepath){
		try {
			InputStream is = new FileInputStream(filepath);
			read(is);
		} catch (Exception e) {
			// TODO: handle exception
			
			e.printStackTrace();
		} 
	}
	
	private void read(InputStream is) {
		try {
			wb = new HSSFWorkbook(is);
			sheet = wb.getSheetAt(0);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getValue(int row, int col) {
		String value = "";
		try {						
			HSSFRow hssfrow = sheet.getRow(row);
			HSSFCell cell = hssfrow.getCell((short)col);
			if(cell == null){
				return "";
			}
			
			int type = cell.getCellType();
			if(type == HSSFCell.CELL_TYPE_STRING){
				value = cell.getStringCellValue();
			}else if (HSSFDateUtil.isCellDateFormatted(cell)) {
		        double d = cell.getNumericCellValue();  
		        if(d != 0.0){
		        Date date = HSSFDateUtil.getJavaDate(d);    
		        SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
		        value = dateFormat.format(date);
		        }else{
		        	value="";
		        }
			}else if(type == HSSFCell.CELL_TYPE_NUMERIC){
				double dvalue = cell.getNumericCellValue();
				if(isIntegerNum(String.valueOf(dvalue))){
					value = String.valueOf((int)dvalue);
				}else{
					value = String.valueOf(dvalue);
				}
			}else if(type == HSSFCell.CELL_TYPE_BOOLEAN){
				value = cell.getBooleanCellValue() + "";
			}else if(type == HSSFCell.CELL_TYPE_BLANK){
				value = "";
			}else if(type == HSSFCell.CELL_TYPE_FORMULA){
				value = cell.getCellFormula();
			}else{
				value = cell.getStringCellValue();
			}
			
			if (value == null){
				value = "";
			}
			value = value.trim();
			
		} catch (Exception e) {
			//e.printStackTrace();
			value = "";
		}
		return value;
	}
	
	public int end(){
		return sheet.getLastRowNum();
	}
	
	public void changeSheet(int i) {
		sheet = wb.getSheetAt(i);
	}
	
	public HashMap getRecordData(int row) {
		HashMap record = new HashMap();
		String value = "";
		short i = 0;
//		short i = sheet.getLeftCol();
//		while (sheet.getRow(row).getCell(i) !=  null){
		while (i<8){
			value = getValue(row, i);
			record.put("ROW " + i, value);
			i++;
		}
		return record;
	}
	
	private boolean isIntegerNum(String s) { 
		String[] strNum = s.split("\\.");
		try{
			if(Integer.parseInt(strNum[1]) == 0){
				return true;
			}
		}catch(ArrayIndexOutOfBoundsException ex){
			return true;
		}
		return false;
	} 
	
}
package ext.casc.folder;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ReportExcelGenerateUtil {
	private HSSFWorkbook workbook;
	private HSSFSheet sheet;
	private HSSFRow row;
	private HSSFCell cell;
	private HSSFFont font;
	private static  FileInputStream in;
	
	public HSSFWorkbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(HSSFWorkbook workbook) {
		this.workbook = workbook;
	}

    
	
	public ReportExcelGenerateUtil(String templatePath){
		try {
			if ((templatePath != null) && (!templatePath.equals(""))){
				in = new FileInputStream(templatePath);
				workbook = new HSSFWorkbook(in);
				
			}else{
				workbook = new HSSFWorkbook();
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setSheet(int index){
		sheet= workbook.getSheetAt(index);
	}
	
	public void removeSheetAt(int index){
		workbook.removeSheetAt(index);
	}
	
	public void setSheet(String index){
		sheet = workbook.getSheet(index);
	}
	
	public void setColumnWidth(int column, int width){
		sheet.setColumnWidth((short)column, (short)width);
	}
	
	public void cloneSheet(int index){
		workbook.cloneSheet(index);
	}
	
	public void createSheet(String sheetName){
		sheet = workbook.createSheet(sheetName);
	}
	
	public void setCellContent(int a, int b, String s){
		row = sheet.getRow(a);
		if(row==null)
			row = sheet.createRow(a);
		cell = row.getCell((short) b);
		if(cell==null){
			cell = row.createCell((short) b);
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		s = s==null?"":s;
		cell.setCellValue(s);
	    
	}
	
	public String getCellContent(int a, int b){
		String cellvalue=null;
		row = sheet.getRow(a);
		if(row==null)
			row = sheet.createRow(a);
		cell = row.getCell((short) b);
		if(cell==null){
			cell = row.createCell((short) b);
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		try{
			cellvalue = cell.getStringCellValue();
		}catch (NullPointerException e){
			cellvalue="0";
			setCellContent(a, b, "");
		}
		return cellvalue;
	}
	
	public void setCellWithCenterAlign(int a, int b, String s){
		row = sheet.getRow(a);
		if(row==null)
			row = sheet.createRow(a);
		cell = row.getCell((short) b);
		if(cell==null){
			cell = row.createCell((short) b);
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		HSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		cell.setCellStyle(style);
		s = s==null?"":s;
		cell.setCellValue(s);
	}
	
	public void setCellWithStyle(int a, int b, String s){

		row = sheet.getRow(a);
		if(row==null)
		row = sheet.createRow(a);
		cell = row.getCell((short) b);
		if(cell==null){
			cell = row.createCell((short) b);
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}

		HSSFCellStyle style = workbook.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		cell.setCellStyle(style);
		s = s==null?"":s;
		cell.setCellValue(s);
	}
	public void setCellWithLeftBorder(int a, int b, String s){
		row = sheet.getRow(a);
		if(row==null)
			row = sheet.createRow(a);
		cell = row.getCell((short) b);
		if(cell==null){
			cell = row.createCell((short) b);
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		HSSFCellStyle style = workbook.createCellStyle();
		style.setBorderLeft(BorderStyle.THIN);
		cell.setCellStyle(style);
		s = s==null?"":s;
		cell.setCellValue(s);
	}
	public void setCellWithRightBorder(int a, int b, String s){
		row = sheet.getRow(a);
		if(row==null)
			row = sheet.createRow(a);
		cell = row.getCell((short) b);
		if(cell==null){
			cell = row.createCell((short) b);
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		HSSFCellStyle style = workbook.createCellStyle();
		style.setBorderRight(BorderStyle.THIN);
		cell.setCellStyle(style);
		s = s==null?"":s;
		cell.setCellValue(s);
	}
	public void setCellWithColorStyle(int a, int b, String s, short point, short color){
		row = sheet.getRow(a);
		if(row==null)
			row = sheet.createRow(a);
		cell = row.getCell((short) b);
		if(cell==null){
			cell = row.createCell((short) b);
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		}
		HSSFCellStyle style = workbook.createCellStyle();

		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFillBackgroundColor(HSSFColor.DARK_BLUE.index);
		style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
		style.setFillForegroundColor(color);
		
		font = workbook.createFont();
		font.setFontName("����");
		font.setCharSet(HSSFFont.DEFAULT_CHARSET);
		font.setFontHeightInPoints(point);
		style.setFont(font);
		cell.setCellStyle(style);
		s = s==null?"":s;
		cell.setCellValue(s);
	}
}

package com.glaway.mpm.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import wt.util.WTProperties;

/**
 * 热加载组部门的映射文件
 * @author Jacky
 * @date 2025年11月6日下午7:12:08
 */
public class DepartmentMappingUtils {
	private static Logger LOGGER=Logger.getLogger(DepartmentMappingUtils.class);
	private static long FILE_MODIFIED_DATE = 0L;
	private static File CONFIG_FILE = null;
	private static FileInputStream inputStream = null;
	private static String FILE_PATH;
	private static XSSFSheet sheet;
	private static XSSFWorkbook workbook;
	private static String SEPARATOR = System.getProperty("file.separator");
	static {
		try {
			WTProperties wtProperties = WTProperties.getLocalProperties();
			FILE_PATH = new StringBuilder().append(wtProperties.getProperty("wt.home")).append(SEPARATOR)
					.append("codebase").append(SEPARATOR).append("department_mapping_table.xlsx").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/** 
	  * @Description: 获取用户组与部件的映射值
	  * @date 2025年11月6日下午7:11:31
	  * @author Jacky
	  * @return  
	  * @return 
	*/
	public static Map<String,String> getDepartmentMappingMap() {
		reload();
		int totalRows = sheet.getPhysicalNumberOfRows();
		Map<String,String> map=new HashMap<String,String>();
		for(int i=1;i<totalRows;i++) {
			XSSFRow row=sheet.getRow(i);
			XSSFCell group=row.getCell(1);	
			XSSFCell department=row.getCell(0);
			if(group!=null&&department!=null) {
				String groupValue=group.getStringCellValue();
				String departmentValue=department.getStringCellValue();
				map.put(groupValue, departmentValue);
			}
		}
		return map;
	}
	
	public static void reload() {
		CONFIG_FILE = new File(FILE_PATH);
		LOGGER.debug(MessageFormat.format("Current file path {0}", FILE_PATH));
		LOGGER.debug(MessageFormat.format("File Modified  date {0}", FILE_MODIFIED_DATE));
		
		if (FILE_MODIFIED_DATE < CONFIG_FILE.lastModified()) {
			try {
				inputStream = new FileInputStream(CONFIG_FILE);
				workbook=new XSSFWorkbook(inputStream);
				sheet=workbook.getSheetAt(0);	
				FILE_MODIFIED_DATE = CONFIG_FILE.lastModified();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		Map<String,String> map=DepartmentMappingUtils.getDepartmentMappingMap();
		System.out.println("map =========="+map);
	}
}

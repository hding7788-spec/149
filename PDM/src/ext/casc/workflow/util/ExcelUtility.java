package ext.casc.workflow.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ext.casc.workflow.CSCWorkflowException;

public class ExcelUtility {
	public static final int START_SHEET = 0;
	public static final int START_CELL = 0;
	public static final int START_ROW = 1;
	
	@SuppressWarnings("deprecation")
	public static List<WorkflowConfigBean> readExcel(InputStream is){
		List<WorkflowConfigBean> excelData = new ArrayList<WorkflowConfigBean>();
		List<String> title = null;
		boolean readFlag = true;
		HSSFWorkbook hssfworkbook = null;
		HSSFSheet hssfsheet = null;
		
		try {
			hssfworkbook = new HSSFWorkbook(is);
		} catch (IOException e) {
			CSCWorkflowException ei = new CSCWorkflowException("使用POI工具转换输入流失败!",e);
			ei.printMessage();
		}
		
		if(hssfworkbook == null){
			return excelData;
		}
		

		int count = hssfworkbook.getNumberOfSheets();
		
		for(int i = START_SHEET ; i < count ; i++){
			title = new ArrayList<String>();
			hssfsheet = hssfworkbook.getSheetAt(i);
			if(hssfsheet == null){
				continue;
			}
			
			int rowLength = hssfsheet.getLastRowNum();
			int cellLength ;
			
			HSSFRow hssfrowFirst = hssfsheet.getRow(0);
			
			if( hssfrowFirst == null ){
				
				continue ;
			}
			
			cellLength = hssfrowFirst.getLastCellNum();
			
			for(int r = 0 ; r < cellLength ; r ++){
				HSSFCell hssfcell = hssfrowFirst.getCell((short)r);
				title.add(hssfcell.getStringCellValue().trim());
			}
			String tempTitle = "";
			for(int j = START_ROW ; j <= rowLength ; j++){
				HSSFRow hssfrow = hssfsheet.getRow(j);
				Map<String,Object> map = new HashMap<String,Object>();
				for(int k = START_CELL ; k < cellLength; k++){
					HSSFCell hssfcell = hssfrow.getCell((short)k);
					if(hssfcell == null){
						readFlag = false;
						break;
					}
					int type = hssfcell.getCellType();
					String tempValue = "";
					switch (type){
					case 0:
						tempValue = Integer.toString((int)hssfcell.getNumericCellValue());
						break;
					case 1:
						tempValue = hssfcell.getStringCellValue().trim();
						break;
					case 3:
						tempValue = hssfcell.getStringCellValue();
						break;
					}
					if (k==0 && tempValue.equals("")) {
						tempValue = tempTitle;
					} else if (k==0 && !tempValue.equals("")) {
						tempTitle = tempValue;
					}
					map.put(title.get(k), tempValue);
				}
				
				if(readFlag){
					WorkflowConfigBean wcb = WorkflowConfigBeanFactory.getWorkflowConfigBeanInstance();
					wcb.setTitleList(title);
					wcb.setContentMap(map);
					wcb.setConfigBean(true);
					excelData.add(wcb);
				}else{
					readFlag = true;
				}
			}
		}
		
		return excelData;
	}
		
	public static FileInputStream test() throws CSCWorkflowException{
		try {
			return new FileInputStream("D:\\ASES_WorkflowConfig.xls");
		} catch (FileNotFoundException e) {
			throw new CSCWorkflowException("指定文件未找到，文件不存在或地址错误",e);
		}
	}
	
	public static void main(String args[]){
		FileInputStream is = null;
		try {
			is = test();
		} catch (CSCWorkflowException e) {
			e.printMessage();
		}
		
		WorkflowConfigBean mergedBean = null;
		if(is != null){
			List<WorkflowConfigBean> list = readExcel(is);
			for(int i = list.size()-4 ; i < list.size() ; i++){
				WorkflowConfigBean wcb = list.get(i);
				mergedBean = wcb.mergeBean(mergedBean);
			}
		
//			for(int i = 0 ; i < list.size() ; i++){
//				WorkflowConfigBean wcb = list.get(i);
//				List<String> titleList = wcb.getTitleList();
//				Map<String,Object> contentMap = wcb.getContentMap();
//				for(int j = 0 ; j < titleList.size(); j++){
//					String tempTitle = titleList.get(j);
//					System.out.printf("%10s",tempTitle);
//				}
//				System.out.println();
//				for(int j = 0 ; j < titleList.size(); j++){
//					String tempTitle = titleList.get(j);
//					System.out.printf("%10s", contentMap.get(tempTitle));
//				}
//				System.out.println();
//			}
		}
		List<String> titleList = mergedBean.getTitleList();
		Map<String,Object> contentMap = mergedBean.getContentMap();
		for(int j = 0 ; j < titleList.size(); j++){
			String tempTitle = titleList.get(j);
			System.out.printf("%10s",tempTitle);
		}
		for(int j = 0 ; j < titleList.size(); j++){
			String tempTitle = titleList.get(j);
			System.out.printf("%10s", contentMap.get(tempTitle));
		}
	}
}

package com.glaway.mpm.mesDataSearch.helper;

import com.glaway.mpm.dataPackage.service.ProcessMesParameterToWCIntf;
import com.glaway.mpm.mesDataSearch.service.MesDataSearchToWCIntf;
import com.glaway.mpm.mesDataSearch.ui.MesDataMainInfoPanel;
import com.glaway.mpm.model.TempObject;
import com.glaway.mpm.parameter.commonui.CommonTableModel;
import com.glaway.mpm.parameter.designui.NewCommonParamTablePanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTabbedPanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTablePanel;
import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.parameter.model.data.CmParameterTablePackage;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MesDataSearchProcesser {

	public static List<TempObject> getTechnicsNumberList(String lukahao, String productNumber, String batch, String tuhao){
		List<TempObject> technicsNumberList = null;
		try {
			technicsNumberList = MesDataSearchToWCIntf.getTechnicsNumberList(lukahao, productNumber, batch, tuhao);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return technicsNumberList;
	}

	public static CmParamTableType getCommonParamTableTypeForDataSearch(String technicsNumber, String productNumber, String lukahao){
		try {
			return MesDataSearchToWCIntf.getCommonParamTableTypeForDataSearch(technicsNumber, productNumber, lukahao);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static List<CmParamTableType> getMesDataSearchParamTableTypes(String technicsNumber, String productNumber, String lukahao){
		try {
			return MesDataSearchToWCIntf.getMesDataSearchParamTableTypes(technicsNumber, productNumber, lukahao);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static void exportQualityData(Container component, String path){
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFCellStyle style = workbook.createCellStyle(); // 样式对象
		style.setVerticalAlignment(VerticalAlignment.CENTER); // 使用枚举值
		style.setAlignment(HorizontalAlignment.CENTER);

		CommonTableModel tableModel = null;
		List<String> showColumnNames = null;
		List<String> notshowColumnNames = null;

		if(component instanceof MesDataMainInfoPanel){
			MesDataMainInfoPanel mainInfoPanel = (MesDataMainInfoPanel) component;
			NewCommonParamTablePanel commonPanel = mainInfoPanel.getCommonParamTablePanel();
			tableModel = commonPanel.getTableModel();
			String[] columnName = commonPanel.getTableColumnName();
			int[] notShowColumns = commonPanel.getNotShowColumns();
			showColumnNames = new ArrayList<String>();
			notshowColumnNames = new ArrayList<String>();
			for(int n = 0; n < notShowColumns.length; n++){
				notshowColumnNames.add(String.valueOf(notShowColumns[n]));
			}
			for(int i = 0; i < columnName.length; i++){
				if(!notshowColumnNames.contains(String.valueOf(i))){
					showColumnNames.add(columnName[i]);
				}
			}
			HSSFSheet sheet = workbook.createSheet("质量记录表");
			Vector<Vector<Object>> dataVector = tableModel.getDataVector();
			HSSFRow row = sheet.createRow(0);
			for(int n = 0; n < showColumnNames.size(); n++){
				HSSFCell cell = row.createCell(n);
				cell.setCellValue(showColumnNames.get(n));
			}
			for(int i = 0; i < dataVector.size(); i++){
				row = sheet.createRow(i + 1);
				int count = 0;
				for(int j = 0; j < dataVector.get(i).size(); j++){
					if(!notshowColumnNames.contains(String.valueOf(j))){
						HSSFCell cell = row.createCell(count);
						String cellValue = String.valueOf(dataVector.get(i).get(j));
						if(cellValue.contains("已选图片数量") || cellValue.contains("img")){
							cellValue = "";
						}
						if(cellValue.contains("<html>") && cellValue.contains("<head>") && cellValue.contains("<body>")){
							cellValue = getBlobStr(cellValue);
						}
						if(cellValue.equals("true")){
							cellValue = "是";
						}
						if(cellValue.equals("false")){
							cellValue = "否";
						}
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(cellValue);
						cell.setCellStyle(style);
						count++;
					}
				}
			}

			NewSpecialParamTabbedPanel tabbedPanel = mainInfoPanel.getSpecialParamTabbedPanel();
			List<NewSpecialParamTablePanel> specialParamTablePanelList = new ArrayList<NewSpecialParamTablePanel>();
			specialParamTablePanelList = tabbedPanel.getSpecialParamTablePanelList();
			int x = 1;
			for(NewSpecialParamTablePanel specialPanel : specialParamTablePanelList){
				CmParamTableType cp = specialPanel.getParamTableType();
				tableModel = specialPanel.getTableModel();
				String[] columnName2 = specialPanel.getTableColumnName();
				int[] notShowColumns2 = specialPanel.getNotShowColumns();
				showColumnNames = new ArrayList<String>();
				notshowColumnNames = new ArrayList<String>();
				for(int n = 0; n < notShowColumns2.length; n++){
					notshowColumnNames.add(String.valueOf(notShowColumns2[n]));
				}
				for(int i = 0; i < columnName2.length; i++){
					if(!notshowColumnNames.contains(String.valueOf(i))){
						showColumnNames.add(columnName2[i]);
					}
				}
				HSSFSheet sheet2 = workbook.createSheet(cp.getName());
				Vector<Vector<Object>> dataVector2 = tableModel.getDataVector();
				HSSFRow row2 = sheet2.createRow(0);
				for(int n = 0; n < showColumnNames.size(); n++){
					HSSFCell cell = row2.createCell(n);
					cell.setCellValue(showColumnNames.get(n));
				}
				for(int i = 0; i < dataVector2.size(); i++){
					row2 = sheet2.createRow(i + 1);
					int count = 0;
					for(int j = 0; j < dataVector2.get(i).size(); j++){
						if(!notshowColumnNames.contains(String.valueOf(j))){
							HSSFCell cell = row2.createCell(count);
							String cellValue = String.valueOf(dataVector2.get(i).get(j));
							if(cellValue.contains("已选图片数量") || cellValue.contains("img")){
								cellValue = "";
							}
							if(cellValue.contains("<html>") && cellValue.contains("<head>") && cellValue.contains("<body>")){
								cellValue = getBlobStr(cellValue);
							}
							if(cellValue.equals("true")){
								cellValue = "是";
							}
							if(cellValue.equals("false")){
								cellValue = "否";
							}
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellValue(cellValue);
							cell.setCellStyle(style);
							count++;
						}
					}
				}
				x++;
			}
		}
		FileOutputStream writeFile = null;
		try {
			writeFile = new FileOutputStream(path);
			workbook.write(writeFile);
			writeFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static String getTechnicsNumberByProductNumber(String productNumber){
		String technicsNumber = "";
		try {
			technicsNumber =  ProcessMesParameterToWCIntf.getTechnicsNumberByProductNumber(productNumber);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return technicsNumber;
	}
	public static File exportSearchData(String technicsNumber, String productNumber, String filePath){
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFCellStyle style = workbook.createCellStyle(); // 样式对象
		style.setVerticalAlignment(VerticalAlignment.CENTER); // 使用枚举值
		style.setAlignment(HorizontalAlignment.CENTER);

		/**通用表的数据*/
		CmParamTableType paramTableType = getCommonParamTableTypeForDataSearch(technicsNumber, productNumber, "");
		CmParameterTablePackage tablePackage = MPMParameterProcessor.getParameterTablePackage(paramTableType, "", "");
		String[] tableColumnName = tablePackage.getMesDataSearchTableColumnName();
		int[] notShowColumns = tablePackage.getNotShowDataSearchColumns();
		List<String> showColumnNames = new ArrayList<String>();
		List<String> notshowColumnNames = new ArrayList<String>();
		for(int n = 0; n < notShowColumns.length; n++){
			notshowColumnNames.add(String.valueOf(notShowColumns[n]));
		}
		for(int i = 0; i < tableColumnName.length; i++){
			if(!notshowColumnNames.contains(String.valueOf(i))){
				showColumnNames.add(tableColumnName[i]);
			}
		}
		HSSFSheet sheet = workbook.createSheet("质量记录表");
		Vector<Vector<Object>> dataVector = paramTableType.getParameters();
		HSSFRow row = sheet.createRow(0);
		for(int n = 0; n < showColumnNames.size(); n++){
			HSSFCell cell = row.createCell(n);
			cell.setCellValue(showColumnNames.get(n));
		}
		for(int i = 0; i < dataVector.size(); i++){
			row = sheet.createRow(i + 1);
			int count = 0;
			for(int j = 0; j < dataVector.get(i).size(); j++){
				if(!notshowColumnNames.contains(String.valueOf(j))){
					HSSFCell cell = row.createCell(count);
					String cellValue = String.valueOf(dataVector.get(i).get(j));
					if(cellValue.contains("已选图片数量") || cellValue.contains("img")){
						cellValue = "";
					}
					if(cellValue.contains("<html>") && cellValue.contains("<head>") && cellValue.contains("<body>")){
						cellValue = getBlobStr(cellValue);
					}
					if(cellValue.equals("true")){
						cellValue = "是";
					}
					if(cellValue.equals("false")){
						cellValue = "否";
					}
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(cellValue);
					cell.setCellStyle(style);
					count++;
				}
			}
		}
		/**特殊表的数据*/
		List<CmParamTableType> list = MesDataSearchProcesser.getMesDataSearchParamTableTypes(technicsNumber, productNumber, "");
		if (list != null) {
			for (CmParamTableType paramTableType2 : list) {
				CmParameterTablePackage tablePackage2 = MPMParameterProcessor.getParameterTablePackage(paramTableType2, "", "");
				String[] tableColumnName2 = tablePackage2.getMesDataSearchTableColumnName();
				int[] notShowColumns2 = tablePackage2.getNotShowDataSearchColumns();
				List<String> showColumnNames2 = new ArrayList<String>();
				List<String> notshowColumnNames2 = new ArrayList<String>();
				for(int n = 0; n < notShowColumns2.length; n++){
					notshowColumnNames2.add(String.valueOf(notShowColumns2[n]));
				}
				for(int i = 0; i < tableColumnName2.length; i++){
					if(!notshowColumnNames2.contains(String.valueOf(i))){
						showColumnNames2.add(tableColumnName2[i]);
					}
				}
				HSSFSheet sheet2 = workbook.createSheet(paramTableType2.getName());
				Vector<Vector<Object>> dataVector2 = paramTableType2.getParameters();
				HSSFRow row2 = sheet2.createRow(0);
				for(int n = 0; n < showColumnNames2.size(); n++){
					HSSFCell cell = row2.createCell(n);
					cell.setCellValue(showColumnNames2.get(n));
				}
				for(int i = 0; i < dataVector2.size(); i++){
					row2 = sheet2.createRow(i + 1);
					int count = 0;
					for(int j = 0; j < dataVector2.get(i).size(); j++){
						if(!notshowColumnNames2.contains(String.valueOf(j))){
							HSSFCell cell = row2.createCell(count);
							String cellValue = String.valueOf(dataVector2.get(i).get(j));
							if(cellValue.contains("已选图片数量") || cellValue.contains("img")){
								cellValue = "";
							}
							if(cellValue.contains("<html>") && cellValue.contains("<head>") && cellValue.contains("<body>")){
								cellValue = getBlobStr(cellValue);
							}
							if(cellValue.equals("true")){
								cellValue = "是";
							}
							if(cellValue.equals("false")){
								cellValue = "否";
							}
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellValue(cellValue);
							cell.setCellStyle(style);
							count++;
						}
					}
				}
			}
		}
		FileOutputStream writeFile = null;
		try {
			writeFile = new FileOutputStream(filePath);
			workbook.write(writeFile);
			writeFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new File(filePath);
	}
	public static String getBlobStr(String str){
		str = str.replace("\r\n", "");
		str = str.replace("<html>", "");
		str = str.replace("<head>", "");
		str = str.replace("</head>", "");
		str = str.replace("<body>", "");
		str = str.replace("<p style='margin-top:5'>", "");
		str = str.replace("</p>", "");
		str = str.replace("</body>", "");
		str = str.replace("</html>", "");
		return str.trim();
	}

}

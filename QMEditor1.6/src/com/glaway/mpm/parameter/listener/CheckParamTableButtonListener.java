package com.glaway.mpm.parameter.listener;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.glaway.mpm.parameter.designui.NewCheckParamTablePanel;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.ExcelUtil;
import com.glaway.mpm.util.FileChooserTool;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 检验记录表监听器
 * @author zhuhao 2017.10.31
 *
 */
public class CheckParamTableButtonListener implements ActionListener {

	private NewCheckParamTablePanel panel;
//	private Container parentPanel;

	public CheckParamTableButtonListener(NewCheckParamTablePanel panel, Container parentPanel){
		this.panel = panel;
//		this.parentPanel = parentPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String typeName = panel.getTypeName();
		Object obj = e.getSource();
		JTable table = panel.getTable();
		CommonUIUtil.stopTableCellEditing(table);
		if (obj == panel.getAddButton()) {
			panel.addOneRow();
		} else if (obj == panel.getRemoveButton()) {
			panel.removeRow();
		} else if (obj == panel.getCopyButton()) {
			panel.copyRow();
		} else if (obj == panel.getPasteButton()){
			panel.pasteRow();
		} else if (obj == panel.getImportexeclButton()){
		//导入excel
			int cellNum = 0;
			if("检测类".equals(typeName)){
				cellNum = 6;
			}else if("记录类".equals(typeName)){
				cellNum = 4;
			}
			try{
				File file = FileChooserTool.getSaveFile("xls", panel);
				if (file != null) {
					if (file.isFile() && file.exists()) {
						if (file.renameTo(file)) {
							int rowCount = panel.getTable().getRowCount();
							if(rowCount > 0){
								int flag = JOptionPane.showConfirmDialog(null, "表中已存在数据，导入将清空表中数据，确定导入？", "确认", JOptionPane.OK_CANCEL_OPTION);
								if(flag != 0){
									return;
								}
							}
							HSSFWorkbook workbook = ExcelUtil.getWorkbook(file);
							HSSFSheet sheet = workbook.getSheetAt(0);
							//判断是否超出表格范围
							String isTrue = "";
//							System.out.println(sheet.getLastRowNum());
							for (int i = 1; i <= sheet.getLastRowNum(); i++) {
								HSSFRow row = sheet.getRow(i);
								if (row != null) {
//									System.out.println( row.getLastCellNum());
									for(int j = 0; j < row.getLastCellNum(); j++){
										if(row.getLastCellNum()>cellNum){
											JOptionPane.showMessageDialog(panel, "第"+ i + "行超出模板范围！", "提示", 1);
											isTrue = "false";
											return;
										}else{
											//检验表处理非法字符
											if(cellNum==6){
												HSSFCell cell = row.getCell(j);
												if(cell == null || j ==0){
													continue;
												}
												cell.setCellType(HSSFCell.CELL_TYPE_STRING);
												Object cellValue = cell.getStringCellValue();
												String text = cellValue.toString();
												if(!"".equals(text) && text!=null){
													String beginText = text.substring(0,1);
													String remainText = "";
													if(text.length()>1 && "-".equals(beginText)){
														remainText = text.substring(1);
													}
													if(!isInt(beginText)&&!"-".equals(beginText)){
														JOptionPane.showMessageDialog(panel, "第"+i+"行存在非法字符！", "提示", 1);
														isTrue = "false";
														return;
													}else if(isInt(beginText)){
														if(!isInt(text)&&!isDouble(text)){
															JOptionPane.showMessageDialog(panel, "第"+i+"行存在非法字符！", "提示", 1);
															isTrue = "false";
															return;
														}
													}else if("-".equals(beginText)){
														if(!isInt(remainText)&&!isDouble(remainText)){
															JOptionPane.showMessageDialog(panel, "第"+i+"行存在非法字符！", "提示", 1);
															isTrue = "false";
															return;
														}
													}
												}
											}
											isTrue = "true";
										}
									}
								}
							}
							if("true".equals(isTrue)){
								panel.removeAllTableValues();
//								System.out.println(sheet.getLastRowNum());
								for (int i = 1; i <= sheet.getLastRowNum(); i++) {
									panel.addOneRow();
									HSSFRow row = sheet.getRow(i);
									if (row != null) {
//										System.out.println(row.getLastCellNum());
										for(int j = 0; j < row.getLastCellNum(); j++){
											HSSFCell cell = row.getCell(j);
											if(cell == null){
												continue;
											}
											cell.setCellType(HSSFCell.CELL_TYPE_STRING);
											Object cellValue = cell.getStringCellValue();
											panel.getTable().setValueAt(cellValue, i - 1 , j+2);
										}
									}
								}
								JOptionPane.showMessageDialog(panel, "模板导入成功！", "提示", 1);
							}else{
								JOptionPane.showMessageDialog(panel, "模板导入失败！", "提示", 1);
							}
						}else{
							JOptionPane.showMessageDialog(panel, "另一个程序正在使用此文件！", "提示", 1);
						}
					}
				}
			}catch (Exception excep){
				excep.printStackTrace();
				JOptionPane.showMessageDialog(panel, "导入模板过程出现错误！", "提示", 1);
			}
		} else if(obj == panel.getGetexeclButton()){
			try {
				File file = FileChooserTool.getSaveFile("xls", panel);
				String[] columnNames = panel.getColumnNames();
				DefaultTableModel tablemodel = panel.getTableModel();
				if (file != null) {
					if (file.isFile() && file.exists()) {
						if (!file.renameTo(file)) {
							JOptionPane.showMessageDialog(panel, "另一个程序正在使用此文件！", "提示", 1);
							return;
						}
					}
					String path = file.getPath();
					if (!path.endsWith(".xls")) {
						path = path.concat(".xls");
					}
					HSSFWorkbook workbook = new HSSFWorkbook();
					HSSFCellStyle style = workbook.createCellStyle(); // 样式对象
					style.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直
					style.setAlignment(HorizontalAlignment.CENTER);// 水平
					HSSFSheet sheet = workbook.createSheet("sheet1");
					Vector<Vector<Object>> dataVector = tablemodel.getDataVector();
					HSSFRow row = sheet.createRow(0);
					for(int n = 0;n<columnNames.length-2;n++){
						HSSFCell cell = row.createCell(n);
						cell.setCellValue(columnNames[n+2]);
					}
					for(int i = 0; i < dataVector.size(); i++){
						row = sheet.createRow(i + 1);
						int count = 0;
						for(int j = 0; j < dataVector.get(i).size()-2; j++){
							HSSFCell cell = row.createCell(count);
							String cellValue = String.valueOf(dataVector.get(i).get(j+2));
							if(cellValue.contains("已选图片数量") || cellValue.contains("img")){
								cellValue = "";
							}
							if(cellValue.contains("<html>") && cellValue.contains("<head>") && cellValue.contains("<body>")){
								cellValue = getBlobStr(cellValue);
							}
							if("null".equals(cellValue)||cellValue==null){
								cellValue = "";
							}
							cellValue = delHTMLTag(cellValue);
							cellValue = cellValue.replace("&lt;", "<");
							cellValue = cellValue.replace("&gt;", ">");
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellValue(cellValue);
							cell.setCellStyle(style);
							count++;
						}
					}
					FileOutputStream writeFile = null;
					try {
						writeFile = new FileOutputStream(path);
						workbook.write(writeFile);
						writeFile.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					JOptionPane.showMessageDialog(panel, "模板导出成功！", "提示", 1);
				}
			} catch (Exception exc) {
				exc.printStackTrace();
				JOptionPane.showMessageDialog(panel, "导出模板过程出现错误！", "提示", 1);
			}

		} else if (obj == panel.getSaveButton()) {
			panel.save();
		}

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

	public boolean isInt(String str){
		 boolean isInt = Pattern.compile("^-?[0-9]\\d*$").matcher(str).find();
		 return isInt;
	}

	public boolean isDouble(String str) {
		boolean isDouble = Pattern.compile("^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$").matcher(str).find();
		return isDouble;
	}
	public static String delHTMLTag(String htmlStr){
	    String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
	    String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
	    String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式

	    Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
	    Matcher m_script=p_script.matcher(htmlStr);
	    htmlStr=m_script.replaceAll(""); //过滤script标签

	    Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
	    Matcher m_style=p_style.matcher(htmlStr);
	    htmlStr=m_style.replaceAll(""); //过滤style标签

	    Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
	    Matcher m_html=p_html.matcher(htmlStr);
	    htmlStr=m_html.replaceAll(""); //过滤html标签

	    return htmlStr.trim(); //返回文本字符串
	}

}

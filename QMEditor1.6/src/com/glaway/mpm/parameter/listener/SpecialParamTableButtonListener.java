package com.glaway.mpm.parameter.listener;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.glaway.mpm.mesParameter.helper.MesParameterProcessor;
import com.glaway.mpm.mesParameter.ui.CampPersonSearchDialog;
import com.glaway.mpm.mesParameter.ui.RightTechnicInfoPanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTablePanel;
import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.parameter.model.data.CmParameterTableColumn;
import com.glaway.mpm.parameter.model.data.CmParameterType;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.ExcelUtil;
import com.glaway.mpm.util.FileChooserTool;

public class SpecialParamTableButtonListener implements ActionListener {

	private NewSpecialParamTablePanel panel;
	private Container parentPanel;

	public SpecialParamTableButtonListener(NewSpecialParamTablePanel panel, Container parentPanel) {
		this.panel = panel;
		this.parentPanel = parentPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		JTable table = panel.getTable();
		CommonUIUtil.stopTableCellEditing(table);
		if (obj == panel.getAddButton()) {
			panel.addOneRow();
		} else if (obj == panel.getRemoveButton()) {
			panel.removeRow();
		} else if (obj == panel.getUpButton()) {
			panel.changeRowValue(true, panel.getTable());
		} else if (obj == panel.getDownButton()) {
			panel.changeRowValue(false, panel.getTable());
		}else if (obj == panel.getImportButton()) {
			try {
				File file = FileChooserTool.getSaveFile("xls", panel);
				if (file != null) {
					if (file.isFile() && file.exists()) {
						if (file.renameTo(file)) {
							int rowCount = panel.getTable().getRowCount();
							if(rowCount > 0){
								int flag = JOptionPane.showConfirmDialog(null, "表中已存在数据，导入将清空表中数据，确定导入？", "确认", JOptionPane.OK_CANCEL_OPTION);
								if(flag == 0){
									panel.removeAllTableValues();
								}else{
									return;
								}
							}
							List<String> parameterValues = new ArrayList<String>();
							for (CmParameterType parameterType : MPMParameterProcessor.getAllChildParameterTypes(panel.getTechnicsType())) {
								String value = parameterType.getName();
								parameterValues.add(value);
							}
							List<Integer> showColumnList = getShowColumnsList();
							HSSFWorkbook workbook = ExcelUtil.getWorkbook(file);
							HSSFSheet sheet = workbook.getSheetAt(0);
							String alertValue = "";
							for (int i = 1; i <= sheet.getLastRowNum(); i++) {
								panel.addOneRow();
								HSSFRow row = sheet.getRow(i);
								if (row != null) {
									for(int j = 0; j < row.getLastCellNum(); j++){
										HSSFCell cell = row.getCell(j);
										cell.setCellType(HSSFCell.CELL_TYPE_STRING);
										Object cellValue = cell.getStringCellValue();
										if(cellValue.equals("是")){
											cellValue = true;
										}
										if(cellValue.equals("否")){
											cellValue = false;
										}
										CmParameterTableColumn tableColumn = panel.getTablePackage().getParamTableType().getTableColumns().get(showColumnList.get(j));
										String isFromParam = CommonUtil.objectToString(tableColumn.getIsfromparam());
										if("true".equals(isFromParam)){
											if(parameterValues.contains(cellValue) || "".equals(cellValue)){
												panel.getTable().setValueAt(cellValue, i - 1 , showColumnList.get(j));
											}else{
												alertValue = alertValue + cellValue + ",";
												panel.getTable().setValueAt("", i - 1 , showColumnList.get(j));
											}
										}else{
											panel.getTable().setValueAt(cellValue, i - 1 , showColumnList.get(j));
										}
									}
								}
							}
							if("".equals(alertValue)){
								JOptionPane.showMessageDialog(panel, "记录表模板导入成功！", "提示", 1);
							}else{
								JOptionPane.showMessageDialog(panel, alertValue + "非特性库中数据，请修改后重新导入,或者手动填写相关内容！", "提示", 1);
							}
						} else {
							JOptionPane.showMessageDialog(panel, "另一个程序正在使用此文件！", "提示", 1);
						}
					}
				}
			} catch (Exception excep) {
				excep.printStackTrace();
				JOptionPane.showMessageDialog(panel, "导入记录表模板过程出现错误！", "提示", 1);
			}

		} else if (obj == panel.getExportButton()) {
			try {
				File file = FileChooserTool.getSaveFile("xls", panel);
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
					MPMParameterProcessor.exportQualityForm(panel, path);
					JOptionPane.showMessageDialog(panel, "模板导出成功！", "提示", 1);
				}
			} catch (Exception exc) {
				exc.printStackTrace();
				JOptionPane.showMessageDialog(panel, "导出个人工艺常用语过程出现错误！", "提示", 1);
			}
		} else if (obj == panel.getSaveButton()) {
			CmParamTableType paramTableType = panel.getParamTableType();
			String productNumber = panel.getProductNumber();
			String technicsNumber = panel.getTechnicsNumber();
			String objNumber = panel.getObjNumber();
			String objType = panel.getObjType();
			String lukahao = panel.getLukahao();
			String gxPK = panel.getGxPK();
			String bsoID = panel.getBsoID();
			String version = panel.getVersion();
			if(parentPanel instanceof RightTechnicInfoPanel){
				if(productNumber == null || "".equals(productNumber)){
					JOptionPane.showMessageDialog(null, "产品编号不能为空！");
				}else{
//					MesParameterProcessor.saveMesParameters(paramTableType, productNumber, technicsNumber, objType, objNumber, lukahao, gxPK);
					JOptionPane.showMessageDialog(null, "保存成功");
				}
			}else{
//				Vector<Vector<Object>> vec = paramTableType.getParameters();
//				if(vec != null && vec.size() > 0){
					MPMParameterProcessor.saveParameters(paramTableType, technicsNumber, objType, objNumber, bsoID, version);
					panel.save();
					MPMParameterProcessor.deleteOldUUIDFiles(panel.getOldUUIDFileNameList());
					panel.uploadImage();
//				}
//				JOptionPane.showMessageDialog(null, "保存成功");
			}
		}else if (obj == panel.getAssignCZRY()){//批量指定操作人员
			if(panel.getParentPanel() instanceof RightTechnicInfoPanel){
				RightTechnicInfoPanel rightPanel = (RightTechnicInfoPanel) panel.getParentPanel();
				String[] columnNames = panel.getTableColumnName();
				int columnNum = -1;
				for(int i = 0; i < columnNames.length; i++){
					String columnName = columnNames[i];
					if(columnName.contains("操作人员")){
						columnNum = i;
					}
				}
				if(columnNum == -1){
					JOptionPane.showMessageDialog(null, "表中不存在操作人员列！");
				}else{
					CampPersonSearchDialog campPersonSearchDialog = new CampPersonSearchDialog(rightPanel.getFrame(), panel, columnNum, 2);
					campPersonSearchDialog.showDialog();
				}
			}
		} else if (obj == panel.getAssignJYRY()){//批量制定检验人员
			if(panel.getParentPanel() instanceof RightTechnicInfoPanel){
				RightTechnicInfoPanel rightPanel = (RightTechnicInfoPanel) panel.getParentPanel();
				String[] columnNames = panel.getTableColumnName();
				int columnNum = -1;
				for(int i = 0; i < columnNames.length; i++){
					String columnName = columnNames[i];
					if(columnName.contains("检验人员")){
						columnNum = i;
					}
				}
				if(columnNum == -1){
					JOptionPane.showMessageDialog(null, "表中不存在检验人员列！");
				}else{
					CampPersonSearchDialog campPersonSearchDialog = new CampPersonSearchDialog(rightPanel.getFrame(), panel, columnNum, 3);
					campPersonSearchDialog.showDialog();
				}
			}
		}
	}
	public List<Integer> getShowColumnsList(){
		List<Integer> notShowColumnsList = new ArrayList<Integer>();
		List<Integer> showColumnsList = new ArrayList<Integer>();
		for(Integer notShowColumn : panel.getNotShowColumns()){
			notShowColumnsList.add(notShowColumn);
		}
		for(int i = 0; i < panel.getTable().getColumnCount(); i++){
			if(!notShowColumnsList.contains(i)){
				showColumnsList.add(i);
			}
		}
		return showColumnsList;
	}

}

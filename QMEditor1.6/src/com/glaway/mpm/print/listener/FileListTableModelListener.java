package com.glaway.mpm.print.listener;

import java.awt.Container;

import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.ui.ButtonPanel;
import com.glaway.mpm.print.ui.FileListTable;
import com.glaway.mpm.print.ui.FilePrintMainPanel;
import com.glaway.mpm.print.ui.SelectBaselineOrDeptDialog;
import com.glaway.mpm.print.ui.SelectBaselineOrDeptPanel;
import com.glaway.mpm.util.CommonUtil;

public class FileListTableModelListener implements TableModelListener {

	private JPanel tablePanel;
	private String type;

	public FileListTableModelListener(JPanel tablePanel, String type) {
		this.tablePanel = tablePanel;
		this.type = type;
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		DefaultTableModel tableModel = (DefaultTableModel) e.getSource();
		if (tablePanel instanceof FileListTable) {
			boolean isSelectAll = true;
			for (int row = 0; row < tableModel.getRowCount(); row++) {
				boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(tableModel.getValueAt(row, 1)));
				if (!isSelect) {
					isSelectAll = false;
				}
			}
			Container component = ((FileListTable)tablePanel).getComponent();
			if (component instanceof FilePrintMainPanel) {
				ButtonPanel buttonPanel = ((FilePrintMainPanel)component).getButtonPanel();
				if(PrintConstants.TITLE_MAINPANEL_JGYZGL_RELATE.equals(type)){
					ButtonPanel addButtonPanel = ((FilePrintMainPanel)component).getAddButtonPanel();
					if (addButtonPanel != null) {
						addButtonPanel.getSelectAll().setSelected(isSelectAll);
					}
				}else{
					if (buttonPanel != null) {
						buttonPanel.getSelectAll().setSelected(isSelectAll);
					}
				}
				//如果是零星添加或者基于BOM添加，测基于工艺文件目录添加按钮无法编辑，反之亦然
				if(PrintConstants.TITLE_MAINPANEL_REQUEST.equals(type)){
					if(tableModel.getRowCount()>0){
						Object object = (Object) tableModel.getValueAt(0, tableModel.getColumnCount()-1);
						if(object instanceof CmPrintInfoBean){
							CmPrintInfoBean cmPrintInfoBean = (CmPrintInfoBean)object;
							boolean addFormBOM = cmPrintInfoBean.isAddFormBOM();
							boolean processfile = cmPrintInfoBean.isProcessfile();
							if(processfile){
								buttonPanel.getAddButton().setEnabled(false);
								buttonPanel.getAddOnBomButton().setEnabled(false);
								buttonPanel.getAddOnProcessDirectory().setEnabled(false);
							}else if(addFormBOM){
								buttonPanel.getAddButton().setEnabled(false);
								buttonPanel.getAddOnBomButton().setEnabled(false);
								buttonPanel.getAddOnProcessDirectory().setEnabled(false);
							}else{
								buttonPanel.getAddButton().setEnabled(true);
								buttonPanel.getAddOnBomButton().setEnabled(false);
								buttonPanel.getAddOnProcessDirectory().setEnabled(false);
							}
						}
					}else{
						buttonPanel.getAddButton().setEnabled(true);
						buttonPanel.getAddOnBomButton().setEnabled(true);
						buttonPanel.getAddOnProcessDirectory().setEnabled(true);
					}
				}else if(PrintConstants.TITLE_MAINPANEL_WJBDSQ.equals(type)){
					if(tableModel.getRowCount()>0){
						Object object = (Object) tableModel.getValueAt(0, tableModel.getColumnCount()-1);
						if(object instanceof CmPrintInfoBean){
							CmPrintInfoBean cmPrintInfoBean = (CmPrintInfoBean)object;
							boolean processfile = cmPrintInfoBean.isProcessfile();
							if(processfile){
								buttonPanel.getAddButton().setEnabled(false);
								buttonPanel.getAddOnProcessDirectory().setEnabled(false);
							}else{
								buttonPanel.getAddButton().setEnabled(true);
								buttonPanel.getAddOnProcessDirectory().setEnabled(false);
							}
						}
					}else{
						buttonPanel.getAddButton().setEnabled(true);
						buttonPanel.getAddOnProcessDirectory().setEnabled(true);
					}
				}
			}
		} else if (tablePanel instanceof SelectBaselineOrDeptPanel) {
			boolean isSelectAll = true;
			for (int row = 0; row < tableModel.getRowCount(); row++) {
				boolean isSelect = true;
				if (type.equals(PrintConstants.TITLE_DIALOG_BASELINE)) {
					isSelect = Boolean.parseBoolean(CommonUtil.objectToString(tableModel.getValueAt(row, 2)));
				} else if (type.equals(PrintConstants.TITLE_DIALOG_DEPT)) {
					isSelect = Boolean.parseBoolean(CommonUtil.objectToString(tableModel.getValueAt(row, 0)));
				} else if (type.equals(PrintConstants.TITLE_DIALOG_SEAL)) {
					isSelect = Boolean.parseBoolean(CommonUtil.objectToString(tableModel.getValueAt(row, 0)));
				}
				if (!isSelect) {
					isSelectAll = false;
				}
			}
			Container component = ((SelectBaselineOrDeptPanel)tablePanel).getComponent();
			if (component instanceof SelectBaselineOrDeptDialog) {
				ButtonPanel buttonPanel = ((SelectBaselineOrDeptDialog)component).getButtonPanel();
				if (buttonPanel != null) {
					buttonPanel.getSelectAll().setSelected(isSelectAll);
				}
			}
		}
	}

}

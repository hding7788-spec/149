package com.glaway.mpm.print.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;

public class FileListTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 3277838964674606156L;
	private String type;

	public FileListTableCellRenderer(String type) {
		super();
		this.type = type;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)) {
			Object object = table.getValueAt(row, 13);
			if(object instanceof CmPrintInfoBean){
				CmPrintInfoBean infoBean = (CmPrintInfoBean) table.getValueAt(row, 13);
				if (infoBean != null) {
					boolean isPrintRequest = infoBean.isPrintRequest();
					if (isPrintRequest) {
						setBackground(Color.GREEN);
					} else {
						setBackground(Color.WHITE);
					}
				}
			}
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}

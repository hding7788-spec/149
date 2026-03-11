package com.glaway.mpm.print.editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.ui.SelectBaselineOrDeptDialog;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.CommonUtil;

public class FileSelectedCellEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 5892644412503608229L;
	private JPanel panel;
	private JTextField textField;
	private JButton button;
	private JTable table;

	public FileSelectedCellEditor(JTable table) {
		super(new JTextField());
		this.table = table;
		this.setClickCountToStart(1);
		textField = new JTextField();
		button = new JButton("设 置");

		textField.setPreferredSize(new Dimension(180, 20));
		textField.setEditable(false);
		panel = new JPanel();

		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.setBackground(table.getBackground());
		panel.add(textField);
		panel.add(button);
		initListener();
	}

	private void initListener() {
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CommonUIUtil.stopTableCellEditing(table);
				int column = table.getSelectedColumn();
//				String type = MPMPrintFileFrame.getType();
				if(column == 11) {
					new SelectBaselineOrDeptDialog(PrintConstants.TITLE_DIALOG_SEAL, table, column);
				} else if (column == 9) {
					new SelectBaselineOrDeptDialog(PrintConstants.TITLE_DIALOG_DEPT, table, column);
				} else if (column == 8) {
//					if(type.equals(PrintConstants.TITLE_MAINPANEL_WJBDSQ)){
//						boolean flag = false;
//						for(int row = 0; row < table.getRowCount(); row++){
//							String value = CommonUtil.objectToString(table.getValueAt(row, 8));
//							if(!"".equals(value)){
//								flag = true;
//							}
//						}
//						if(flag){
//							int showMes = CommonUIUtil.showConfirmDialog(null, "若您想继续此操作,将会清除其他已经设置好的补打信息");
//							if(showMes == 0){
//								for(int row = 0; row < table.getRowCount(); row++){
//									table.setValueAt("", row, 8);
//								}
//								new SelectBaselineOrDeptDialog(PrintConstants.TITLE_DIALOG_DEPT, table, column);
//							}else{
//								return;
//							}
//						}else{
//							new SelectBaselineOrDeptDialog(PrintConstants.TITLE_DIALOG_DEPT, table, column);
//						}
//					}else{
						new SelectBaselineOrDeptDialog(PrintConstants.TITLE_DIALOG_DEPT, table, column);
//					}
				} else if (column == 7) {
					new SelectBaselineOrDeptDialog(PrintConstants.TITLE_DIALOG_SEAL, table, column);
				}
			}
		});
	}

	@Override
	public Object getCellEditorValue() {
		int column = table.getSelectedColumn();
		int row = table.getSelectedRow();
		String value = "";
		if(row >=0 && column >=0){
			value = CommonUtil.objectToString(table.getValueAt(row, column));
		}
		return value;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		textField.setText(CommonUtil.objectToString(value));
		return panel;
	}


}

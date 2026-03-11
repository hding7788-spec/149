package com.glaway.mpm.parameter.commonui;

import com.glaway.mpm.parameter.designui.PhotoRecordTablePanel;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.Arrays;

public class MultiComboBoxTableCellEditor extends AbstractCellEditor implements
		TableCellEditor {

	private static final long serialVersionUID = 1L;

	private MultiComboBox comboBox;

	private JTable table;
	private int row;
	private int column;
	private String oldText;
	private PhotoRecordTablePanel photoRecordTablePanel;

	public MultiComboBoxTableCellEditor(PhotoRecordTablePanel photoRecordTablePanel,String[] values) {
		comboBox = new MultiComboBox(values);
		comboBox.setRenderer(new CheckListCellRenderer());
		this.photoRecordTablePanel = photoRecordTablePanel;
	}

	@Override
	public Object getCellEditorValue() {
		String value = String.valueOf(comboBox.getSelectedItem());;
		if(photoRecordTablePanel!=null) {
			int selectedRow = photoRecordTablePanel.getTable().getSelectedRow();
			String localName = (String) photoRecordTablePanel.getTableModel().getValueAt(selectedRow,9);
			if(localName!=null && !"".equals(localName)){
				return value;
			}else{
				JOptionPane.showMessageDialog(photoRecordTablePanel, "只有本地上传的照片样张可以修改内容!", "提示", JOptionPane.INFORMATION_MESSAGE);
				return oldText;
			}
		}
		if (table != null && row > -1 && column > -1) {
			table.setValueAt(value, row, column);
		}
		return value;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		this.table = table;
		this.row = row;
		this.column = column;
		if(value!=null){
		}
		String selvalue = objectToString(value);
		oldText = selvalue;
		this.comboBox.setSelectedItem(selvalue);

		int count = comboBox.getItemCount();
		for (int i = 0; i < count; i++) {
			CheckValue checkValue = (CheckValue)comboBox.getItemAt(i);
			if (selvalue.contains(checkValue.value)) {
				checkValue.bolValue = true;
			} else {
				checkValue.bolValue = false;
			}
		}
		
		return this.comboBox;
	}
	
	public static String objectToString(Object obj) {
        if (obj == null) {
            return "";
        } else if ("null".equals(obj)) {
            return "";
        } else {
            return obj.toString();
        }
    }

}

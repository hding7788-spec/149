package com.glaway.mpm.parameter.designui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PhotoRecordEditor extends DefaultCellEditor {
    private static final long serialVersionUID = -2224083977153934301L;
    private String oldText;
    private PhotoRecordTablePanel photoRecordTablePanel;
    public PhotoRecordEditor() {
        super(new JTextField());
    }

    public PhotoRecordEditor(PhotoRecordTablePanel photoRecordTablePanel) {
        super(new JTextField());
        this.photoRecordTablePanel = photoRecordTablePanel;
    }

    public PhotoRecordEditor(PhotoRecordTablePanel photoRecordTablePanel,JComboBox comboBox) {
        super(comboBox);
        this.photoRecordTablePanel = photoRecordTablePanel;
    }

    public Object getCellEditorValue() {
        String value = (String) delegate.getCellEditorValue();
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
        return value;
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int col) {
    	if(value!=null){
    		oldText = value.toString();
    	}

        return super.getTableCellEditorComponent(table, value, isSelected, row,
                col);
    }
}

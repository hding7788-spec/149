/**
 * 南京国睿信维软件有限公司
 */
package com.glaway.mpm.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * 类功能：
 *
 * @author LB
 * @date 2020/9/21
 */
public class ResourceTableCellEditor extends DefaultCellEditor implements FocusListener {

    private JTextField textField;

    public ResourceTableCellEditor(JTextField textField) {
        super(textField);
        this.textField = textField;
        textField.addFocusListener(this);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        DefaultCellEditor editor = (DefaultCellEditor) table.getColumnModel().getColumn(column).getCellEditor();
        if (null == editor) {
            textField = new JTextField();
            table.getColumnModel().getColumn(column).setCellEditor(new DefaultCellEditor(textField));
        } else if (editor.getComponent() instanceof JTextField) {
            textField = (JTextField) editor.getComponent();
        }
        textField.setText(String.valueOf(value));

        return textField;
    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {

    }

    @Override
    public boolean stopCellEditing() {
        if (this.textField.getText() == null || this.textField.getText().length() == 0) {
            JOptionPane.showMessageDialog(null, "使用数量为必填项！", "提示", JOptionPane.OK_OPTION);
            return false;
        }
        return super.stopCellEditing();
    }
}

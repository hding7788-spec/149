package com.glaway.mpm.parameter.commonui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MultiComboBox extends JComboBox implements ActionListener {
	
	private static final long serialVersionUID = 1L;

	public MultiComboBox() {
        setEditable(true);
        ((JTextField) getEditor().getEditorComponent()).setEditable(false);
        
        this.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent ae) {
                itemSelected();
            }
        });
        
        setSelectedItem("");
    }
    
    public MultiComboBox(String[] values) {
        setEditable(true);
        ((JTextField) getEditor().getEditorComponent()).setEditable(false);
        
        if (values != null) {
            CheckValue checkValue = null;
            for (String value : values) {
                checkValue = new CheckValue(false, value);
                addItem(checkValue);
            }
        }
        
        this.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent ae) {
                itemSelected();
            }
        });
        
        setSelectedItem("");
    }

    private void itemSelected() {
        if (getSelectedItem() instanceof CheckValue) {
            CheckValue jcb = (CheckValue) getSelectedItem();
            jcb.bolValue = (!jcb.bolValue);
            
            String selValue = "";
            for (int i = 0; i < getItemCount(); i++) {
                CheckValue checkValue = (CheckValue) getItemAt(i);
                if (checkValue.bolValue) {
                    if ("".equals(selValue)) {
                        selValue = checkValue.value;
                    } else {
                        selValue = selValue + "," + checkValue.value;
                    }
                }
            }
            
            setSelectedItem(selValue);
            
            if (this.isShowing()) {
            	SwingUtilities.invokeLater(new Runnable() {
                    @Override
					public void run() {
                        /* 选中后依然保持当前弹出状态 */
                        showPopup();
                    }
                });
			}
        }
    }
}

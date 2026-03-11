package com.glaway.mpm.erp.component;


import com.glaway.mpm.view.NewTechnicsPart;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class TechnicsMaterialJCombox extends JComboBox {
    private TechnicsMaterialEditor editor = new TechnicsMaterialEditor();
    private Vector<String> values;

    public TechnicsMaterialJCombox(String dicName) {
        setEditable(true);
        setEditor(editor);
        getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                 inputEvent(e);
            }
        });
        values = getItems(dicName);
    }

    private void inputEvent(KeyEvent e) {
       if((char)e.getKeyChar()==KeyEvent.VK_ENTER) {
           String s = (String) getEditor().getItem();
           removeAllItems();
           Vector<String> v = getShowVector(s);
           if ((v == null) || (v.size() == 0)) {
               hidePopup();
               return;
           }
           addItem("");
           for (int i = 0; i < v.size(); i++) {
               addItem(v.get(i));
           }
           if (v.size() > 0) {
               hidePopup();
               showPopup();
               repaint();
           }
       }

    }


    private Vector<String> getShowVector(String prefix) {
        Vector<String> returnV = new Vector<String>();
        if (prefix != null && !"".equals(prefix)) {
            for (String value : values) {
                if (value == null || "".equals(value)) {
                    continue;
                }
                if (value.contains(prefix)) {
                    returnV.add(value);
                }
            }
        }
        return returnV;
    }

    class TechnicsMaterialEditor implements ComboBoxEditor {
        private JTextField editor = new JTextField();
        private EventListenerList listenerList = new EventListenerList();

        public void addActionListener(ActionListener l) {
            this.listenerList.add(ActionListener.class, l);
        }

        public Component getEditorComponent() {
            return this.editor;
        }

        public Object getItem() {
            return this.editor.getText();
        }

        public void removeActionListener(ActionListener l) {
            this.listenerList.remove(ActionListener.class, l);
        }

        public void selectAll() {
        }

        public void setItem(Object anObject) {
            if ((anObject instanceof String)) {
                String s = (String) anObject;
                this.editor.setText(s);
            } else {
                this.editor.setText("");
            }
        }
    }

    public Vector getItems(String dicName) {
        Vector<String> vector = NewTechnicsPart.dicNameMap.get(dicName);
        return vector;
    }
}
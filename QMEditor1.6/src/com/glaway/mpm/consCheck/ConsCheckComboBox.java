package com.glaway.mpm.consCheck;

import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;
import com.glaway.mpm.sop.util.StringUtil;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

public class ConsCheckComboBox extends JComboBox implements Observer {
    public String type;
    public Vector<String> recordMap;
    public Vector<String> jcMap;
    public Vector<String> jlMap;
    public Vector<String> tableMap;

    public ConsCheckEditor consCheckEditor = new ConsCheckEditor();

    public ConsCheckComboBox(String type) {
        setEditable(true);
        setEditor(consCheckEditor);

        try {
            this.type = type;
            recordMap = ProcessParameterToWCIntf.getAllConsCheckRecords(type);
            if("TABLE".equals(type)){
                tableMap = ProcessParameterToWCIntf.getAllConsCheckRecords(type);
            }else{
                jcMap = recordMap;
                jlMap = ProcessParameterToWCIntf.getAllConsCheckRecords("JL");
            }
        } catch(InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch(RemoteException e) {
            throw new RuntimeException(e);
        }

        getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                ConsCheckComboBox.this.inputEvent(e);
            }
        });
    }

    public void reSetList() {
        removeAllItems();
        for(String s : recordMap) {
            addItem(s);
        }
    }

    private Vector<String> reSetVoidList() {
        if("TABLE".equals(type)){
            return tableMap;
        }else{
            if("JC".equals(type)){
                return jcMap;
            }else{
                return jlMap;
            }
        }
    }

    private void inputEvent(KeyEvent e) {
        String s = (String) getEditor().getItem();
        removeAllItems();
        Vector v = null;
        if((s == null) || (s.equals(""))) {
            setSelectedItem("");
            v = reSetVoidList();
        } else {
            setSelectedItem(s);
            v = getShowVector(s.toLowerCase());
        }
        if((v == null) || (v.size() == 0)) {
            hidePopup();
            return;
        }
        for(int i = 0; i < v.size(); i++) {
            addItem(v.get(i));
        }
        if(v.size() > 0) {
            hidePopup();
            showPopup();
            repaint();
        }
    }

    private Vector<String> getShowVector(String s) {
        Vector<String> returnV = new Vector();
        if("TABLE".equals(type)){
            for(String record : tableMap) {
                String words = record.lastIndexOf("_")>-1 ? record.substring(record.lastIndexOf("_")+1) : record;
                String shortCut = StringUtil.convertStr(words);
                if(shortCut.contains(s)){
                    returnV.add(record);
                }
            }
        }else{
            if("JC".equals(type)){
                for(String record : jcMap) {
                    String words = record.lastIndexOf("_")>-1 ? record.substring(record.lastIndexOf("_")+1) : record;
                    String shortCut = StringUtil.convertStr(words);
                    if(shortCut.contains(s)){
                        returnV.add(record);
                    }
                }
            }else{
                for(String record : jlMap) {
                    String words = record.lastIndexOf("_")>-1 ? record.substring(record.lastIndexOf("_")+1) : record;
                    String shortCut = StringUtil.convertStr(words);
                    if(shortCut.contains(s)){
                        returnV.add(record);
                    }
                }
            }
        }
        return returnV;
    }

    public class ConsCheckEditor implements ComboBoxEditor {
        private JTextField editor = new JTextField();
        private EventListenerList listenerList = new EventListenerList();

        private ConsCheckEditor() {
            editor.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    if(recordMap != null) {
                        for(String s : recordMap) {
                            String name = editor.getText().trim();
                            if(s.equals(name)) {
                                return;
                            }
                        }
                    }
                    editor.setText("");
                }
            });
        }

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
            if((anObject instanceof String)) {
                String s = (String) anObject;
                this.editor.setText(s);
            } else {
                this.editor.setText("");
            }
        }


    }

    public void update(Observable o, Object arg) {
    }
}

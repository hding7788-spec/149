package com.glaway.mpm.mesParameter.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

public class MultiPopup extends JPopupMenu {

    private List<ActionListener> listeners = new ArrayList<ActionListener>();

    private List<String> values;

    private List<String> defaultValues;

    private List<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();

    private MultiComboBox box;

    private JButton commitButton ;

    private JButton cancelButton;

    public static final String COMMIT_EVENT = "commit";

    public static final String CANCEL_EVENT = "cancel";

    public MultiPopup(MultiComboBox box,List<String> value , List<String> defaultValue) {
        super();
        this.box = box;
        values = value;
        defaultValues = defaultValue;
        initComponent();
    }

    public void addActionListener(ActionListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeActionListener(ActionListener listener) {
        if (listeners.contains(listener))
            listeners.remove(listener);
    }

    private void initComponent() {

    	JPanel totalPanel = new JPanel();
    	totalPanel.setLayout(new BorderLayout());

        JPanel checkboxPane = new JPanel();

        JPanel buttonPane = new JPanel();

        this.setLayout(new BorderLayout());

        for(Object v : values){
            JCheckBox temp = new JCheckBox(v.toString() , selected(v));
            checkBoxList.add(temp);
        }

//        for(Object v : values){
//        	String textValue = box.getTextFieldValue();
//        	JCheckBox temp = null;
//        	if(textValue.contains(v.toString())){
//        		temp = new JCheckBox(v.toString(), true);
//        	}else{
//        		temp = new JCheckBox(v.toString(), false);
//        	}
//            checkBoxList.add(temp);
//        }

        if(checkBoxList.get(0).getText().equals("全选"))
            checkBoxList.get(0).addItemListener(new ItemListener()
        {
               public void itemStateChanged(ItemEvent e)
               {
                   System.out.println("被选中状态   "+checkBoxList.get(0).isSelected());
                   if(checkBoxList.get(0).isSelected())//Select All 被选中
                   {
                       //检查其他的是否被选中如果没有就选中他们
                       for(int i=1; i< checkBoxList.size();i++)
                       {
                           if(!checkBoxList.get(i).isSelected())
                               checkBoxList.get(i).setSelected(true);
                       }
                   }
                   else
                   {
                       for(int i=1; i< checkBoxList.size();i++)
                       {
                           if(checkBoxList.get(i).isSelected())
                               checkBoxList.get(i).setSelected(false);
                       }
                   }
               }
             });



        checkboxPane.setLayout(new GridLayout(checkBoxList.size() , 1 ,3, 3));
        for(JCheckBox box : checkBoxList){
            checkboxPane.add(box);
        }

        commitButton = new JButton("确定");

        commitButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                commit();
            }

        });

        cancelButton = new JButton("取消");

        cancelButton.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                cancel();
            }

        });

        buttonPane.add(commitButton);

        buttonPane.add(cancelButton);

        if(checkBoxList.size() > 35){
          JScrollPane scrolPane = new JScrollPane(checkboxPane);
          scrolPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//          scrolPane.setVerticalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
          totalPanel.add(scrolPane, BorderLayout.CENTER);
          totalPanel.add(buttonPane, BorderLayout.SOUTH);
          this.add(totalPanel);
          this.setPopupSize(200, 600);
        }else{
        	totalPanel.add(checkboxPane, BorderLayout.CENTER);
            totalPanel.add(buttonPane, BorderLayout.SOUTH);
            this.add(totalPanel);
        }

    }

    private boolean selected(Object v) {
    	if(defaultValues != null){
    		for(Object dv : defaultValues){
    			if( dv .equals(v) ){
    				return true;
    			}
    		}
    	}
        return false;
    }

    protected void fireActionPerformed(ActionEvent e) {
        for (ActionListener l : listeners) {
            l.actionPerformed(e);
        }
    }

    public List<String> getSelectedValues(){
        List<String> selectedValues = new ArrayList<String>();

        if(checkBoxList.get(0).getText().equals("全选"))
        {
            if(checkBoxList.get(0).isSelected())
            {
                for(int i = 1 ; i < checkBoxList.size() ; i++)
             {
                 selectedValues.add(values.get(i));
             }
            }
            else
            {
                for(int i = 1 ; i < checkBoxList.size() ; i++){

                    if(checkBoxList.get(i).isSelected())
                        selectedValues.add(values.get(i));
                }
            }
        }else
            for(int i = 0 ; i < checkBoxList.size() ; i++){

            if(checkBoxList.get(i).isSelected())
                selectedValues.add(values.get(i));
        }


        return selectedValues;
    }

    public void setDefaultValue(List<String> defaultValue) {
        defaultValues = defaultValue;
    }

    public void commit(){
        fireActionPerformed(new ActionEvent(this, 0, COMMIT_EVENT));
    }

    public void cancel(){
        fireActionPerformed(new ActionEvent(this, 0, CANCEL_EVENT));
    }

}
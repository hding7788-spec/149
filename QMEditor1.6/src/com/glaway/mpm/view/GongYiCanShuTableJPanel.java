package com.glaway.mpm.view;

import com.glaway.mpm.util.XmlUtility;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工艺参数
 *
 */
public class GongYiCanShuTableJPanel extends JPanel {
	private JFrame frame;
	private JFrame parentPanel;
    public GongYiCanShuTableJPanel(NewTechnicsPart frame) {
        this.frame = frame;
        initComponents();
        setName("GongYiCanShuTableJPanel");
    }
    public GongYiCanShuTableJPanel(JFrame parentPanel, JFrame frame) {
        this.parentPanel = parentPanel;
        this.frame = frame;
        this.initComponents();
        this.setName("GongYiCanShuTableJPanel");
    }

	private JLabel canshuLabel_1= new JLabel("工艺文件名称");
    private JTextField canShuText_1 = new JTextField();

    JButton okButton ;
    JButton clearButton ;
    JButton deleteButton ;
    JButton refushButton ;

    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JTable jTable1;
    private JScrollPane jScrollPane1;
    private Element currentElement;
    private String currentContext;
    private SpecialWordPanel currentEditorPane;


    private void initComponents() {
        jTable1 = new JTable();
        okButton = new JButton();
        clearButton = new JButton();
        deleteButton = new JButton();
        refushButton = new JButton();

        okButton.setText("应	  用");
        clearButton.setText("清	   空");
        deleteButton.setText("删	  除");
        refushButton.setText("刷   新");
        jScrollPane1 = new JScrollPane();
        jPanel1 = new JPanel();
        jPanel2 = new JPanel();
        jPanel3 = new JPanel();

        jPanel3.setBorder(BorderFactory.createTitledBorder("新参数值填完后，需点击右侧<应用>按钮。鼠标需新参数值输入框移出再保存。"));

        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        refushButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	refushButtonButtonActionPerformed(evt);
            }
        });



        jTable1.setModel(new DefaultTableModel(
                new Object[][]{}, new String[]{"序号", "参数名称", "旧参数值", "参数值/单位",  "公称值", "上偏差", "下偏差"}) {
                     private static final long serialVersionUID = 1L;
                     Class[] types = new Class[]{String.class, String.class, String.class, String.class, String.class, String.class, String.class};
                     boolean[] canEdit = new boolean[]{false, false,false, true,true,true, true };
                     public Class getColumnClass(int columnIndex) {
                         return types[columnIndex];
                     }
                     public boolean isCellEditable(int rowIndex, int columnIndex) {
                         return canEdit[columnIndex];
                     }
                 });
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(20);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(0);
        jTable1.getColumnModel().getColumn(2).setMinWidth(0);
        jTable1.getColumnModel().getColumn(2).setWidth(0);
        jTable1.getColumnModel().getColumn(2).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(40);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(40);
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(40);
        //jTable1.getColumnModel().getColumn(7).setPreferredWidth(20);
        jScrollPane1.setViewportView(jTable1);

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(okButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(clearButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(deleteButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(refushButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                               )


        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(okButton)
                                .addGap(26, 26, 26)
                                .addComponent(clearButton)
                                .addGap(26, 26, 26)
                                .addComponent(deleteButton)
                                 .addGap(26, 26, 26)
                                .addComponent(refushButton)
                                .addContainerGap(225, Short.MAX_VALUE))
        );





        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 766, GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 917, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap()))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 407, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap()))
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

    }

    private void okButtonActionPerformed(ActionEvent evt) {
        int n = jTable1.getRowCount();
        Map<String,String> replaces = new HashMap<String,String>();
        for (int i = 0; i < n; i++) {
            String key = String.valueOf(jTable1.getValueAt(i, 1));
            String oldGycs = String.valueOf(jTable1.getValueAt(i, 2));
            StringBuilder  endValue = new StringBuilder("");
            if(oldGycs.contains("符号：")||oldGycs.contains("基准1：")||oldGycs.contains("基准2：")||oldGycs.contains("基准3：")){
            	String[] ss = oldGycs.split("；");
            	for(String s:ss){
            		if(s.contains("：")){
                		String[] vv = s.split("：");
                		if(vv.length==2){
                			if("符号".equals(vv[0])){
                				endValue.append("；");
                				endValue.append("符号：");
                				endValue.append(vv[1].replaceAll("】", ""));
                			}else if("基准1".equals(vv[0])){
                				endValue.append("；");
                				endValue.append("基准1：");
                				endValue.append(vv[1].replaceAll("】", ""));
                			}else if("基准2".equals(vv[0])){
                				endValue.append("；");
                				endValue.append("基准2：");
                				endValue.append(vv[1].replaceAll("】", ""));
                			}else if("基准3".equals(vv[0])){
                				endValue.append("；");
                				endValue.append("基准3：");
                				endValue.append(vv[1].replaceAll("】", ""));
                			}
                		}
            		}


            	}
            }

            String newValue = String.valueOf(jTable1.getValueAt(i, 3));
            String newValue2 = String.valueOf(jTable1.getValueAt(i, 4));
            String newValue3 = String.valueOf(jTable1.getValueAt(i, 5));
            String newValue4 = String.valueOf(jTable1.getValueAt(i, 6));
            StringBuilder newGysc = new StringBuilder("【");
            newGysc.append(key).append("：").append(newValue);
            if(newValue2!=null&&!"".equals(newValue2)){
                newGysc.append("；");
                newGysc.append("公称值：");
                newGysc.append(newValue2);
            }
            if(newValue3!=null&&!"".equals(newValue3)){
                newGysc.append("；");
                newGysc.append("上偏差：");
                newGysc.append(newValue3);
            }
            if(newValue4!=null&&!"".equals(newValue4)){
                newGysc.append("；");
                newGysc.append("下偏差：");
                newGysc.append(newValue4);
            }
            newGysc.append(endValue).append("】");
            replaces.put(oldGycs,newGysc.toString());
        }
        if(currentElement!=null&&currentContext!=null &&!"".equals(currentContext)){
        	if("QMFawTechnicsInfo".equals(currentElement.getName())){
        		Set<Entry<String, String>> entrySet = replaces.entrySet();
        		String newContext = this.currentContext;

        		for(Entry<String, String> entry : entrySet){
        			String oldValue = entry.getKey();
        			String newValue = entry.getValue();
        			newContext  = newContext.replaceAll(oldValue,newValue);
        		}

        		XmlUtility.setTechnicsDescribe(currentElement, newContext);

        		List<Element> stateElements = XmlUtility.getTechnicsStateTables(currentElement);
        		if(stateElements!=null){
        			for(Element e:stateElements){
                		Element gyztE = e.element("gyzt");
                		String gyzt = gyztE.getText();
        				for(Entry<String, String> entry : entrySet){
                			String oldValue = entry.getKey();
                			String newValue = entry.getValue();
                			gyzt  = gyzt.replaceAll(oldValue,newValue);
                		}
        				gyztE.setText(gyzt);
            		}
        		}



        	}else  if("QMProcedureInfo".equals(currentElement.getName())){
        		Set<Entry<String, String>> entrySet = replaces.entrySet();
        		String newContext = this.currentContext;

        		for(Entry<String, String> entry : entrySet){
        			String oldValue = entry.getKey();
        			String newValue = entry.getValue();
        			System.out.println("replace QMProcedureInfo:"+entry);
        			newContext  = newContext.replaceAll(oldValue,newValue);
        		}
        		 XmlUtility.setProcedureContent(currentElement, newContext);

                 //this.currentEditorPane.setText(newContext);
                 //this.currentEditorPane.repaint();
                 Element parentElement = this.currentElement.getParent();
        		 if (parentElement!=null && "paces".equals(this.currentElement.getParent().getName())) {

        			 /*Element checkRecordTablesE = currentElement.element("checkRecordTables");
        			 if(checkRecordTablesE!=null){
        				 String s = checkRecordTablesE.getStringValue();
        				 for(Entry<String, String> entry : entrySet){
        	        			String oldValue = entry.getKey();
        	        			String newValue = entry.getValue();
        	        			s  = s.replaceAll(oldValue,newValue);
        	        	 }

        				 try {
							checkRecordTablesE.setDocument( XmlUtility.getDocument(s.getBytes()));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        			 }*/

                     TechnicsPaceJDialog paceDialog = (TechnicsPaceJDialog)this.parentPanel;
                     paceDialog.getEditorPane().setText(newContext);
                     paceDialog.getEditorPane().repaint();
                 }
        	}
        }

    }

    private void clearButtonActionPerformed(ActionEvent evt) {
        int[] sel = jTable1.getSelectedRows();
        if (sel == null || sel.length == 0) {
            JOptionPane.showMessageDialog(frame, "请选择需要清除的数据！");
            return;
        }
        DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
        for (int i = 0; i < sel.length; i++) {
            tableModel.setValueAt("", sel[i], 3);
            tableModel.setValueAt("", sel[i], 4);
            tableModel.setValueAt("", sel[i], 5);
            tableModel.setValueAt("", sel[i], 6);
        }
    }
    private void refushButtonButtonActionPerformed(ActionEvent evt) {

        StringBuilder allContext = new StringBuilder("");
    	if("QMFawTechnicsInfo".equals( this.currentElement.getName())){
    		currentContext = XmlUtility.getTechnicsDescribe(this.currentElement);
    		allContext.append(currentContext);
    		List<Element> stateElements = XmlUtility.getTechnicsStateTables(currentElement);
    		if(stateElements!=null){
    			for(Element e:stateElements){
            		String gyzt = e.elementText("gyzt");
            		allContext.append(gyzt);
        		}
    		}
            this.currentContext = allContext.toString();
        }else  if("QMProcedureInfo".equals(this.currentElement.getName())){
    		//Element procedureContentElement = techEle.element("procedureContent");
    		//currentContext = procedureContentElement.getText();
        	if(currentEditorPane==null){
        		Element procedureContentElement = this.currentElement.element("procedureContent");
        		currentContext = procedureContentElement.getText();
        	}else{
                this.currentContext = this.currentEditorPane.getText();
        	}
            allContext.append(currentContext);

    	}


        DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(0);
        Set<String> gycsSet = new LinkedHashSet<String>();
        String regex = "【.*?】";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(allContext.toString());
        while (matcher.find()) {
            gycsSet.add(matcher.group());
        }
        System.out.println(gycsSet);
        int index = 0;
        for(String gycs:gycsSet){
            //【参数名：参数值 计量单位;公称值:Xx;上偏差:xx;下偏差:xx】
        	index ++;
            String oldGycs = gycs;
            String newGycs = gycs.substring(1,gycs.length()-1);
            if(newGycs.contains("；")){
                String[] ss = newGycs.split("；");
                if(ss.length==1){
                    processGongYiCanShu(ss[0],index,tableModel,oldGycs);
                }else if(ss.length>=2){
                    processGongYiCanShuAndOthers(ss,index,tableModel,oldGycs);
                }
            }else{
                processGongYiCanShu(newGycs,index,tableModel,oldGycs);
            }

        }
    }
    private void deleteButtonActionPerformed(ActionEvent evt) {

        int[] sel = jTable1.getSelectedRows();
        if (sel == null || sel.length == 0) {
            JOptionPane.showMessageDialog(frame, "请选择需要移除的参数！");
            return;
        }

        DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
        Map<String,String> replaces = new HashMap<String, String>();

        for (int i = 0; i < sel.length; i++) {
            String oldGycs = String.valueOf(tableModel.getValueAt(sel[i], 2));
            replaces.put(oldGycs,"");
			tableModel.removeRow(sel[i] - i);

        }
        if(currentElement!=null&&currentContext!=null &&!"".equals(currentContext)){
            if("QMFawTechnicsInfo".equals(currentElement.getName())){
                Set<Entry<String, String>> entrySet = replaces.entrySet();
                String newContext = this.currentContext;

                for(Entry<String, String> entry : entrySet){
                    String oldValue = entry.getKey();
                    String newValue = entry.getValue();
                    newContext  = newContext.replaceAll(oldValue,newValue);
                }

                XmlUtility.setTechnicsDescribe(currentElement, newContext);
                List<Element> stateElements = XmlUtility.getTechnicsStateTables(currentElement);
                if(stateElements!=null){
                    for(Element e:stateElements){
                        Element gyztE = e.element("gyzt");
                        String gyzt = gyztE.getText();
                        for(Entry<String, String> entry : entrySet){
                            String oldValue = entry.getKey();
                            String newValue = entry.getValue();
                            gyzt  = gyzt.replaceAll(oldValue,newValue);
                        }
                        gyztE.setText(gyzt);
                    }
                }
            }else  if("QMProcedureInfo".equals(currentElement.getName())){
                Set<Entry<String, String>> entrySet = replaces.entrySet();
                String newContext = this.currentContext;

                for(Entry<String, String> entry : entrySet){
                    String oldValue = entry.getKey();
                    String newValue = entry.getValue();
                    System.out.println("replace QMProcedureInfo:"+entry);
                    newContext  = newContext.replaceAll(oldValue,newValue);
                }
                this.currentEditorPane.setText(newContext);
                this.currentEditorPane.repaint();
                XmlUtility.setProcedureContent(currentElement, newContext);

                Element parentElement = this.currentElement.getParent();
                if (parentElement!=null && "paces".equals(parentElement.getName())) {

                    TechnicsPaceJDialog paceDialog = (TechnicsPaceJDialog)this.parentPanel;
                    paceDialog.getEditorPane().setText(newContext);
                    paceDialog.getEditorPane().repaint();
                }
            }
        }
    }

    public void setTableValues(Element techEle,SpecialWordPanel currentEditorPane) {
        this.currentEditorPane = currentEditorPane;
        this.currentElement = techEle;
        StringBuilder allContext = new StringBuilder("");
    	if("QMFawTechnicsInfo".equals(techEle.getName())){
    		currentContext = XmlUtility.getTechnicsDescribe(techEle);
    		allContext.append(currentContext);
    		List<Element> stateElements = XmlUtility.getTechnicsStateTables(currentElement);
    		if(stateElements!=null){
    			for(Element e:stateElements){
            		String gyzt = e.elementText("gyzt");
            		allContext.append(gyzt);
        		}
    		}
            this.currentContext = allContext.toString();
        }else  if("QMProcedureInfo".equals(techEle.getName())){
    		//Element procedureContentElement = techEle.element("procedureContent");
    		//currentContext = procedureContentElement.getText();
        	if(currentEditorPane==null){
        		Element procedureContentElement = techEle.element("procedureContent");
        		currentContext = procedureContentElement.getText();
        	}else{
                this.currentContext = this.currentEditorPane.getText();
        	}

            allContext.append(currentContext);

    		/*Element checkRecordTablesElement = techEle.element("checkRecordTables");
    		if(checkRecordTablesElement!=null){
    			String crts = checkRecordTablesElement.getStringValue();
    			allContext.append(crts);

    		}*/

    	}


        DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(0);
        Set<String> gycsSet = new LinkedHashSet<String>();
        String regex = "【.*?】";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(allContext.toString());
        while (matcher.find()) {
            gycsSet.add(matcher.group());
        }
        System.out.println(gycsSet);
        int index = 0;
        for(String gycs:gycsSet){
            //【参数名：参数值 计量单位;公称值:Xx;上偏差:xx;下偏差:xx】
        	index ++;
            String oldGycs = gycs;
            String newGycs = gycs.substring(1,gycs.length()-1);
            if(newGycs.contains("；")){
                String[] ss = newGycs.split("；");
                if(ss.length==1){
                    processGongYiCanShu(ss[0],index,tableModel,oldGycs);
                }else if(ss.length>=2){
                    processGongYiCanShuAndOthers(ss,index,tableModel,oldGycs);
                }
            }else{
                processGongYiCanShu(newGycs,index,tableModel,oldGycs);
            }

        }

    }

    private void processGongYiCanShu(String gycs ,int index,DefaultTableModel tableModel,String oldGycs) {
        String[] ss = gycs.split("：");
        if(ss.length>=2){
            addOneRow(tableModel);
            int rowCount = jTable1.getRowCount();
            String gycsKey = ss[0];
            String gycsValue = ss[1];
            tableModel.setValueAt(index, rowCount - 1, 0);
            tableModel.setValueAt(gycsKey, rowCount - 1, 1);
            tableModel.setValueAt(oldGycs, rowCount - 1, 2);
            tableModel.setValueAt(gycsValue, rowCount - 1, 3);
            tableModel.setValueAt("", rowCount - 1, 4);
            tableModel.setValueAt("", rowCount - 1, 5);
            tableModel.setValueAt("", rowCount - 1, 6);
        }else if(ss.length==1){
            addOneRow(tableModel);
            int rowCount = jTable1.getRowCount();
            String gycsKey = ss[0];
            tableModel.setValueAt(index, rowCount - 1, 0);
            tableModel.setValueAt(gycsKey, rowCount - 1, 1);
            tableModel.setValueAt(oldGycs, rowCount - 1, 2);
            tableModel.setValueAt("", rowCount - 1, 3);
            tableModel.setValueAt("", rowCount - 1, 4);
            tableModel.setValueAt("", rowCount - 1, 5);
            tableModel.setValueAt("", rowCount - 1, 6);
        }
    }

    private void processGongYiCanShuAndOthers(String[] gycsValues,int index,DefaultTableModel tableModel,String oldGycs) {

        String gycsKey = "";
        String gycsVal1 = "";
        String gycsVal2 = "";
        String gycsVal3 = "";
        String gycsVal4 = "";
        for(int i = 0;i<gycsValues.length;i++){
            if(i==0){
               String gycsValue =  gycsValues[i];
               String[] ss = gycsValue.split("：");
               gycsKey = ss[0];
               gycsVal1 = ss[1];
            }else {
                String gycsValue =  gycsValues[i];
                String[] ss = gycsValue.split("：");
                if("公称值".equals(ss[0])){
                    gycsVal2 = ss[1];
                }else if("上偏差".equals(ss[0])){
                    gycsVal3 = ss[1];
                }else if("下偏差".equals(ss[0])){
                    gycsVal4 = ss[1];
                }
            }

        }
        addOneRow(tableModel);
        int rowCount = jTable1.getRowCount();
        tableModel.setValueAt(index, rowCount - 1, 0);
        tableModel.setValueAt(gycsKey, rowCount - 1, 1);
        tableModel.setValueAt(oldGycs, rowCount - 1, 2);
        tableModel.setValueAt(gycsVal1, rowCount - 1, 3);
        tableModel.setValueAt(gycsVal2, rowCount - 1, 4);
        tableModel.setValueAt(gycsVal3, rowCount - 1, 5);
        tableModel.setValueAt(gycsVal4, rowCount - 1, 6);

    }

    private void addOneRow(DefaultTableModel tableModel) {
        Vector vector = new Vector();
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            vector.add("");
        }
        tableModel.addRow(vector);
    }

}

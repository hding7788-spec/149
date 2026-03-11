package com.glaway.mpm.sop.view;

import com.glaway.mpm.sop.SopUitl;
import com.glaway.mpm.sop.intf.SopIntf;
import com.glaway.mpm.sop.model.ParametersBean;
import com.glaway.mpm.sop.model.SopBean;
import com.glaway.mpm.sop.util.SopXMLUtility;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.*;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;

/**
 * 引用SOP文件列表
 *
 * @author LongXiuChuan
 */
public class SopFileTableJPanel extends NewLinkJPanel {

    private static final long serialVersionUID = 1L;
    private JFrame frame;
    private JDialog parentDialog;
    private JFrame parentJFrame;
    private JComboBox comboBox;
    private List<ParametersBean> parameters;


//    private JButton useYQZButton = new IconButton("/images/button_add.png", "应用要求值");
    private JButton copyJButton = new IconButton("/images/button_add.png", "复制");
    private JButton useSOPJButton = new IconButton("/images/button_add.png", "应用SOP");

    public SopFileTableJPanel(Container parentpanel, JFrame frame) {
        super(parentpanel);
        this.frame = frame;
        initComponent();
    }

    public SopFileTableJPanel(Container parentpanel, JFrame frame, JFrame parentJFrame) {
        super(parentpanel);
        this.frame = frame;
        this.parentJFrame = parentJFrame;
        initComponent();
    }

    public SopFileTableJPanel(Container parentpanel, JDialog parentDialog) {
        super(parentpanel);
        this.parentDialog = parentDialog;
        initComponent();
    }


    protected void initComponent() {
        searchJButton.setText("添加");

        addJButton.setVisible(false);
        upJButton.setVisible(false);
        downJButton.setVisible(false);
        setNoteJButton.setVisible(false);

        panel.add(searchJButton, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                5, 5, 0, 5), 0, 0));
//        panel.add(useYQZButton, new GridBagConstraints(1, 7, 1, 1, 1.0, 0.0,
//                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
//                5, 5, 0, 5), 0, 0));
        panel.add(copyJButton, new GridBagConstraints(1, 8, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                5, 5, 0, 5), 0, 0));
        panel.add(useSOPJButton, new GridBagConstraints(1, 9, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                5, 5, 0, 5), 0, 0));

        CommonUIUtil.hiddenCell(table, 0);
        CommonUIUtil.hiddenCell(table, 1);
        CommonUIUtil.hiddenCell(table, 8);

        comboBox = new JComboBox();
        TableColumnModel tableColumnModel = table.getColumnModel();
        tableColumnModel.getColumn(5).setCellEditor(new DefaultCellEditor(new JTextField()));
        tableColumnModel.getColumn(6).setCellEditor(new DefaultCellEditor(comboBox));//@ 物资列表需要从资源库中加载
        tableColumnModel.getColumn(7).setCellEditor(new DefaultCellEditor(new JTextField()));

        this.tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int type = e.getType();
                if(type == 0) {
                    int i = e.getColumn();
                    if(i == 6) {
                        int row = e.getFirstRow();
                        String wzlb = (String) tableModel.getValueAt(row, i);
                        String csxm = (String) tableModel.getValueAt(row, 4);
//						String sopNumber = (String) tableModel.getValueAt(row, 1);
//						WTDocument document = null;
//						try {
//							document = WTDocumentUtil.getLatestDocumentByNumber(sopNumber);
//						} catch (WTException e1) {
//							e1.printStackTrace();
//						}
//						SopBean sopBean = SopIntf.buildSopBean(document);
//						List<ParametersBean> parameters = sopBean.getArameters();
                        boolean isHas = false;
                        if(parameters != null) {
                            for(ParametersBean parametersBean : parameters) {
                                String materialCategory = parametersBean.getMaterialCategory();
                                String parameter = parametersBean.getName();
                                if(parameter.equals(csxm) && materialCategory.equals(wzlb)) {
                                    tableModel.setValueAt(parametersBean.getCanShuZhi(), row, 5);
                                    isHas = true;
                                    table.updateUI();
                                    break;
                                }
                            }
                        }
                        if(!isHas) {
                            tableModel.setValueAt("", row, 5);
                        }
                    }
                }
            }
        });

        /**comboBox.addItemListener(new ItemListener() {
        @Override public void itemStateChanged(ItemEvent e) {
        int stateChange = e.getStateChange();
        if(stateChange==2){
        int i = tableModel.getColumnCount();
        if(i>=0){
        String para = (String) tableModel.getValueAt(i, 4);
        String wzlb = comboBox.getSelectedItem().toString();
        if("".equals(wzlb)){
        tableModel.setValueAt("", i, 5);
        }else{
        Iterator<Map<String, String>> iterator = vec.iterator();
        while(iterator.hasNext()){
        Map<String, String> map = iterator.next();
        String p = map.get("parameters");
        String w = map.get("wzlb");
        if(p.equals(para) && w.equals(wzlb)){
        tableModel.setValueAt(map.get("canshuzhi"), i, 5);
        }
        }
        }
        }
        }
        }
        });*/

        CmActionListener actionListener = new CmActionListener();
        searchJButton.addActionListener(actionListener);
//        useYQZButton.addActionListener(actionListener);
        copyJButton.addActionListener(actionListener);
        useSOPJButton.addActionListener(actionListener);
        removeJButton.addActionListener(actionListener);

    }

    public void addData(Vector<Map<String, String>> vector, List<ParametersBean> beanList) {
        if(tableModel.getRowCount() > 0) {
            SwingUtil.showMessageDialog("一道工序或工步只能关联一份SOP文件！", "提示", 2);
            return;
        }
        if(vector != null) {
            parameters = new ArrayList<ParametersBean>();
            parameters.addAll(beanList);
            Vector<String> rowData = null;
            /**String sopNumber = vector.get(0).get("number");
             WTDocument document = null;
             try {
             document = WTDocumentUtil.getLatestDocumentByNumber(sopNumber);
             } catch (WTException e) {
             e.printStackTrace();
             }
             SopBean sopBean = SopIntf.buildSopBean(document);
             List<ParametersBean> parameters = sopBean.getArameters();
             comboBox.removeAllItems();
             comboBox.addItem("");
             List<String> list = new ArrayList<String>();
             for (ParametersBean parametersBean : parameters) {
             String materialCategory = parametersBean.getMaterialCategory();
             if(!list.contains(materialCategory)){
             list.add(materialCategory);
             comboBox.addItem(materialCategory);
             }
             }*/
            comboBox.removeAllItems();
            comboBox.addItem("");
            List<String> list = new ArrayList<String>();
            for(ParametersBean parametersBean : parameters) {
                String materialCategory = parametersBean.getMaterialCategory();
                if(!list.contains(materialCategory)) {
                    list.add(materialCategory);
                    comboBox.addItem(materialCategory);
                }
            }
            for(Map<String, String> map : vector) {
                rowData = new Vector<String>();
                rowData.add(map.get("oid"));
                rowData.add(map.get("number"));
                rowData.add(map.get("ppnumber"));
                rowData.add(map.get("name"));
                rowData.add(map.get("parameters"));
                rowData.add("");
                rowData.add("");
                rowData.add("");
                rowData.add(map.get("zylb"));
                tableModel.addRow(rowData);
            }
        }
        setTabTitle();
    }

    public void saveToXml(Vector<Map<String, String>> vector) {
        Element parentEle = null;
        Element techEle = null;
        if(parentPanel instanceof TechnicsPaceJDialog) {
            TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentPanel;
            parentEle = paceJDialog.paceElement;
            techEle = paceJDialog.getStepElement().getParent().getParent();
        } else if(parentPanel instanceof TechnicsStepJPanel_XW) {
            TechnicsStepJPanel_XW stepPanel = (TechnicsStepJPanel_XW) parentPanel;
            parentEle = stepPanel.getStepElement();
            techEle = parentEle.getParent().getParent();
        }
        if(parentEle != null) {
            Element relatedSopTech = SopXMLUtility.getRelatedSopTech(parentEle);
            for(Map<String, String> map : vector) {
                Element sopTech = relatedSopTech.addElement(XmlUtility.SOP_TAG);
                XmlUtility.setAttributeValue(sopTech, "oid", map.get("oid"));
                XmlUtility.setAttributeValue(sopTech, "number", map.get("number"));
                XmlUtility.setAttributeValue(sopTech, "ppnumber", map.get("ppnumber"));
                XmlUtility.setAttributeValue(sopTech, "name", map.get("name"));
                XmlUtility.setAttributeValue(sopTech, "parameters", map.get("parameters"));
                XmlUtility.setAttributeValue(sopTech, "canshuzhi", "");
                XmlUtility.setAttributeValue(sopTech, "wzlb", "");
                XmlUtility.setAttributeValue(sopTech, "zylb", map.get("zylb"));
            }
        }
        ((NewTechnicsPart) frame).saveProcess(techEle);
    }

    String startType = com.glaway.mpm.EditorConfig.startType;

    public void setTabTitle() {
        int i = tableModel.getRowCount();
        if(!"SOP".equals(startType)) {
            if(i > 0) {
                if(parentPanel instanceof TechnicsStepJPanel_XW) {
                    ((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(15, "引用SOP文件" + "(" + i + ")");
                } else if(parentPanel instanceof TechnicsStepJPanel_View) {
                    ((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(10, "引用SOP文件" + "(" + i + ")");
                } else if(parentPanel instanceof TechnicsPaceJDialog) {
                    ((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(13, "引用SOP文件" + "(" + i + ")");
                }
            } else {
                if(parentPanel instanceof TechnicsStepJPanel_XW) {
                    ((TechnicsStepJPanel_XW) parentPanel).getTabbedPane().setTitleAt(15, "引用SOP文件");
                } else if(parentPanel instanceof TechnicsStepJPanel_View) {
                    ((TechnicsStepJPanel_View) parentPanel).getTabbedPane().setTitleAt(10, "引用SOP文件");
                } else if(parentPanel instanceof TechnicsPaceJDialog) {
                    ((TechnicsPaceJDialog) parentPanel).getTabbedPane().setTitleAt(13, "引用SOP文件");
                }
            }
        }

    }

    protected void addModelColumn() {
        tableModel.addColumn("oid");
        tableModel.addColumn("number");
        tableModel.addColumn("SOP文件编号");
        tableModel.addColumn("SOP文件名称");
        tableModel.addColumn("参数项目");
        tableModel.addColumn("要求值");
        tableModel.addColumn("物资类别");
        tableModel.addColumn("物资信息");
        tableModel.addColumn("zylb");
        table.updateUI();
    }

    public Vector<Element> getElements() {
        stopTableCellEditing();
        Vector<Element> elements = new Vector<Element>();
        for(int i = 0; i < tableModel.getRowCount(); i++) {
            if(isRowNull(i)) {
                continue;
            }
            Element element = DocumentHelper.createElement(XmlUtility.SOP_TAG);
            XmlUtility.setAttributeValue(element, "oid", (String) tableModel.getValueAt(i, 0));
            XmlUtility.setAttributeValue(element, "number", (String) tableModel.getValueAt(i, 1));
            XmlUtility.setAttributeValue(element, "ppnumber", (String) tableModel.getValueAt(i, 2));
            XmlUtility.setAttributeValue(element, "name", (String) tableModel.getValueAt(i, 3));
            XmlUtility.setAttributeValue(element, "parameters", (String) tableModel.getValueAt(i, 4));
            XmlUtility.setAttributeValue(element, "canshuzhi", (String) tableModel.getValueAt(i, 5));
            XmlUtility.setAttributeValue(element, "wzlb", (String) tableModel.getValueAt(i, 6));
            XmlUtility.setAttributeValue(element, "wzxx", (String) tableModel.getValueAt(i, 7));
            XmlUtility.setAttributeValue(element, "zylb", (String) tableModel.getValueAt(i, 8));

            elements.add(element);
        }
        return elements;
    }

    public void setTableValues(Vector<Element> vector) {
        clearTable();
        for(int i = 0; i < vector.size(); i++) {
            Element element = vector.get(i);
            setOneRowTableValue(element);
        }
        setTabTitle();
    }

    public void setOneRowTableValue(Element element) {
        if(element != null && element.getName().equals(XmlUtility.SOP_TAG)) {
            addProcess();
            int i = tableModel.getRowCount();
            i--;
            tableModel.setValueAt(element.attributeValue("oid"), i, 0);
            tableModel.setValueAt(element.attributeValue("number"), i, 1);
            tableModel.setValueAt(element.attributeValue("ppnumber"), i, 2);
            tableModel.setValueAt(element.attributeValue("name"), i, 3);
            tableModel.setValueAt(element.attributeValue("parameters"), i, 4);
            tableModel.setValueAt(element.attributeValue("canshuzhi"), i, 5);
            tableModel.setValueAt(element.attributeValue("wzlb"), i, 6);
            tableModel.setValueAt(element.attributeValue("wzxx"), i, 7);
            tableModel.setValueAt(element.attributeValue("zylb"), i, 8);

            SopBean sopBean = SopIntf.buildSopBeanByNumber(element.attributeValue("number"));
            if(sopBean != null) {
                List<ParametersBean> parameters = sopBean.getArameters();
                comboBox.removeAllItems();
                comboBox.addItem("");
                ArrayList<String> list = new ArrayList<String>();
                if(parameters != null) {
                    for(ParametersBean parametersBean : parameters) {
                        String materialCategory = parametersBean.getMaterialCategory();
                        if(!list.contains(materialCategory)) {
                            list.add(materialCategory);
                            comboBox.addItem(materialCategory);
                        }
                    }
                }
            }

        }
    }

    public Integer getRow() {
        int i = tableModel.getRowCount();
        return i;
    }

    public Element getTechElement() {
        Element techElement = null;
        if(parentPanel instanceof TechnicsStepJPanel_XW) {
            TechnicsStepJPanel_XW technicsStepJPanel_XW = (TechnicsStepJPanel_XW) parentPanel;
            Element procedureElement = technicsStepJPanel_XW.getElement();
            techElement = ((NewTechnicsPart) frame).getTechElement(procedureElement);
        } else if(parentPanel instanceof TechnicsPaceJDialog) {
            TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentPanel;
            Element procedureElement = paceJDialog.getStepElement();
            techElement = ((NewTechnicsPart) frame).getTechElement(procedureElement);
        }
        return techElement;
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    /**
     * 查询并添加SOP文件
     */
    private void search() {
        stopTableCellEditing();
        SearchSopDialog dialog = null;
        if(frame != null) {
            dialog = new SearchSopDialog(SopFileTableJPanel.this, frame, parentJFrame);
        } else {
            dialog = new SearchSopDialog(SopFileTableJPanel.this, parentDialog);
        }
        Vector<Map<String, String>> vec = dialog.showDialog();
        Vector<Map<String, String>> vector = new Vector<Map<String, String>>();
        List<String> list = new ArrayList<String>();
        if(vec != null) {
            List<ParametersBean> parametersList = new ArrayList<ParametersBean>();
            Iterator<Map<String, String>> iterator = vec.iterator();
            while(iterator.hasNext()) {
                Map<String, String> map = iterator.next();
                ParametersBean pb = new ParametersBean();
                pb.setMaterialCategory(map.get("wzlb"));
                pb.setName(map.get("parameters"));
                pb.setCanShuZhi(map.get("canshuzhi"));
                parametersList.add(pb);
                String pa = map.get("parameters");
                if(!list.contains(pa)) {
                    list.add(pa);
                    vector.add(map);
                }
            }
            addData(vector, parametersList);
            saveToXml(vector);
        }
    }

    /**
     * 将编辑的参数项目和参数值追加到工序或工步内容末尾
     * 合并到应用SOP按钮   2023-5-22
     */
    private void useYQZ() {
        String value = "";
        String value2 = "";
        int count = table.getRowCount();
        if(count > 0) {
            String sopNumber = String.valueOf(table.getValueAt(0, 2));
            String sopName = String.valueOf(table.getValueAt(0, 3));
            if(sopName != null && !"".equals(sopName)) {
                int lastIndexOf = sopName.lastIndexOf("(");
                if(lastIndexOf > -1) {
                    sopName = sopName.substring(0, lastIndexOf);
                }
            }
            String parameter = String.valueOf(table.getValueAt(0, 4));
            String canShuZhi = String.valueOf(table.getValueAt(0, 5));
            String wzlb = String.valueOf(table.getValueAt(0, 6));
            String wzxx = String.valueOf(table.getValueAt(0, 7));
            if(wzlb == null || "null".equals(wzlb)) {
                wzlb = "";
            }
            if(wzxx == null || "null".equals(wzxx)) {
                wzxx = "";
            }
            value = "具体按" + sopNumber + "、" + sopName + "执行，" + "\r\n" + wzlb + " (" + wzxx + ") " + parameter + " " + canShuZhi;
            if(count > 1) {
                for(int i = 1; i < count; i++) {
                    String parameter2 = String.valueOf(table.getValueAt(i, 4));
                    String canShuZhi2 = String.valueOf(table.getValueAt(i, 5));
                    String wzlb2 = String.valueOf(table.getValueAt(i, 6));
                    String wzxx2 = String.valueOf(table.getValueAt(i, 7));
                    if(wzlb2 == null || "null".equals(wzlb2)) {
                        wzlb2 = "";
                    }
                    if(wzxx2 == null || "null".equals(wzxx2)) {
                        wzxx2 = "";
                    }
                    value2 = value2 + "\r\n" + wzlb2 + " (" + wzxx2 + ") " + parameter2 + " " + canShuZhi2;
                }
            }
            value = value + value2;
        }

        if(value != null && value.length() > 0) {
            if(parentPanel instanceof TechnicsStepJPanel_XW) {
                TechnicsStepJPanel_XW technicsStepJPanel_XW = (TechnicsStepJPanel_XW) parentPanel;
                String stepContent = technicsStepJPanel_XW.getSpeCharPanel().getText();
                if(stepContent != null && !stepContent.isEmpty()) {
                    technicsStepJPanel_XW.getSpeCharPanel().insertText("\r\n" + value);
                } else {
                    technicsStepJPanel_XW.getSpeCharPanel().insertText(value);
                }
            } else if(parentPanel instanceof TechnicsPaceJDialog) {
                TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentPanel;
                String paceContent = paceJDialog.getEditorPane().getText();
                if(paceContent != null && !paceContent.isEmpty()) {
                    paceJDialog.getEditorPane().insertText("\r\n" + value);
                } else {
                    paceJDialog.getEditorPane().insertText(value);
                }
            }
            SwingUtil.showMessageDialog("应用要求值成功！", "提示", 2);
        } else {
            SwingUtil.showMessageDialog("没有可应用的要求值！", "提示", 2);
        }
    }

    /**
     * 复制选中的行并在列表末尾进行粘贴
     */
    private void copy() {
        Vector<?> dataVector = tableModel.getDataVector();
        if(dataVector.size() > 0) {
            for(int row : table.getSelectedRows()) {
                Vector<?> rowVector = (Vector<?>) ((Vector<?>) dataVector.get(row)).clone();
                tableModel.addRow(rowVector);
            }
        } else {
            SwingUtil.showMessageDialog("请选中需要复制的SOP条目", "提示", 2);
        }
        setTabTitle();
    }

    /**
     * 将编辑的参数项目和参数值追加到工序或工步内容末尾
     * 将引用的SOP文件中的资源、检验记录表、质量记录表应用到当前工序对应的内容中去
     */
    private void useSOP() {
        int count = table.getRowCount();
        //应用要求值
        String value = "";
        String value2 = "";
        if(count > 0) {
            String sopNumber = String.valueOf(table.getValueAt(0, 2));
            String sopName = String.valueOf(table.getValueAt(0, 3));
            if(sopName != null && !"".equals(sopName)) {
                int lastIndexOf = sopName.lastIndexOf("(");
                if(lastIndexOf > -1) {
                    sopName = sopName.substring(0, lastIndexOf);
                }
            }
            String parameter = String.valueOf(table.getValueAt(0, 4));
            String canShuZhi = String.valueOf(table.getValueAt(0, 5));
            String wzlb = String.valueOf(table.getValueAt(0, 6));
            String wzxx = String.valueOf(table.getValueAt(0, 7));
            if(wzlb == null || "null".equals(wzlb)) {
                wzlb = "";
            }
            if(wzxx == null || "null".equals(wzxx)) {
                wzxx = "";
            }
            value = "具体按" + sopNumber + "、" + sopName + "执行，" + "\r\n" + wzlb + " (" + wzxx + ") " + parameter + " " + canShuZhi;
            if(count > 1) {
                for(int i = 1; i < count; i++) {
                    String parameter2 = String.valueOf(table.getValueAt(i, 4));
                    String canShuZhi2 = String.valueOf(table.getValueAt(i, 5));
                    String wzlb2 = String.valueOf(table.getValueAt(i, 6));
                    String wzxx2 = String.valueOf(table.getValueAt(i, 7));
                    if(wzlb2 == null || "null".equals(wzlb2)) {
                        wzlb2 = "";
                    }
                    if(wzxx2 == null || "null".equals(wzxx2)) {
                        wzxx2 = "";
                    }
                    value2 = value2 + "\r\n" + wzlb2 + " (" + wzxx2 + ") " + parameter2 + " " + canShuZhi2;
                }
            }
            value = value + value2;
        }

        if(value != null && value.length() > 0) {
            if(parentPanel instanceof TechnicsStepJPanel_XW) {
                TechnicsStepJPanel_XW technicsStepJPanel_XW = (TechnicsStepJPanel_XW) parentPanel;
                String stepContent = technicsStepJPanel_XW.getSpeCharPanel().getText();
                if(stepContent != null && !stepContent.isEmpty()) {
                    technicsStepJPanel_XW.getSpeCharPanel().insertText("\r\n" + value);
                } else {
                    technicsStepJPanel_XW.getSpeCharPanel().insertText(value);
                }
            } else if(parentPanel instanceof TechnicsPaceJDialog) {
                TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentPanel;
                String paceContent = paceJDialog.getEditorPane().getText();
                if(paceContent != null && !paceContent.isEmpty()) {
                    paceJDialog.getEditorPane().insertText("\r\n" + value);
                } else {
                    paceJDialog.getEditorPane().insertText(value);
                }
            }
        }

        //应用SOP
        if(count > 0) {
            String sopOid = String.valueOf(table.getValueAt(0, 0));
            XWTreeNode selXwTreeNode = ((NewTechnicsPart) frame).getTechnicsTreePanel().getSelectedTreeNode();
            if(selXwTreeNode != null) {
                XWTreeObject treeObject = selXwTreeNode.getObject();
                if((treeObject instanceof XWStepTreeObject)) {
                    if(parentPanel instanceof TechnicsStepJPanel_XW) {
                        ((NewTechnicsPart) frame).treeSelectedValueChanged(selXwTreeNode);
                        Element stepElement = treeObject.getTreeCellData();
                        try {
                            SopUitl.useSop(stepElement, sopOid);
                            ((NewTechnicsPart) frame).saveProcess(stepElement);
                            ((NewTechnicsPart) frame).getTechnicsStepJPanel().setUIValues(stepElement);
                            SwingUtil.showMessageDialog("复用SOP标准规程时成功！", "提示", 1);
                        } catch(Exception e) {
                            SwingUtil.showMessageDialog("复用SOP标准规程时出错！", "提示", 2);
                            e.printStackTrace();
                        }
                    } else if(parentPanel instanceof TechnicsPaceJDialog) {
                        TechnicsPaceJDialog pace = (TechnicsPaceJDialog) parentPanel;
                        pace.save(false);
                        Element paceElement = pace.getPaceElement();
//						Element element = paceElement.element("sops");
//						List<Element> elements = element.elements();
//						Element element2 = elements.get(0);
//						String attributeValue = element2.attributeValue("name");
                        try {
                            SopUitl.useSop(paceElement, sopOid);
                            NewTechnicsPart newTechnicsPart = (NewTechnicsPart) pace.getFrame();
                            Element techele = XmlUtility.getTechnicsElement(newTechnicsPart.getCurrentTechnics());
                            ((NewTechnicsPart) frame).saveProcess(techele);
                            pace.setUIValues(paceElement);
                            SwingUtil.showMessageDialog("复用SOP标准规程时成功！", "提示", 1);
                        } catch(Exception e) {
                            SwingUtil.showMessageDialog("复用SOP标准规程时出错！", "提示", 2);
                            e.printStackTrace();
                        }
                    }

                } else {
                    SwingUtil.showMessageDialog("请在工艺树上选中工序节点！", "提示", 2);
                }
            } else {
                SwingUtil.showMessageDialog("请在工艺树上选中工序节点！", "提示", 2);
            }
        }
    }

    /**
     * 从表格中移除选中的SOP条目
     */
    private void remove() {
        int count[] = table.getSelectedRows();
        if(count.length <= 0) {
            SwingUtil.showMessageDialog("请选中需要移除的SOP条目", "提示", 2);
            return;
        } else {
            for(int i = count.length; i > 0; i--) {
                tableModel.removeRow(table.getSelectedRow());
            }
        }
    }


    class CmActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == searchJButton) {
                search();
            } else if(e.getSource() == copyJButton) {
                copy();
            } else if(e.getSource() == useSOPJButton) {
                useSOP();
            } else if(e.getSource() == removeJButton) {
                remove();
            }
        }
    }


}
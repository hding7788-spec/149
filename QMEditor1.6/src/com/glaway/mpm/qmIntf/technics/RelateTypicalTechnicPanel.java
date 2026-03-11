package com.glaway.mpm.qmIntf.technics;

import com.glaway.mpm.mesParameter.model.GLZhuFuLink;
import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.util.ApacheZipUtil;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class RelateTypicalTechnicPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private VaLogger logger = VaLogger.getLogger(this.getClass());

    private JPanel mainPanel;
    private JPanel middlePanel;
    private JPanel middleMainPanel;

    private JCheckBox allSelectBox;
    private JComboBox comboBox;

    private JButton sureButton;
    private JButton cancelButton;

    private JScrollPane jScrollPane;
    private JTable jTable;

    private JDialog dialog;
    private NewTechnicsPart frame;
    private List<Technics> technicsList;
    private Element technicElement;
    private int rowNumber = 0;

    public RelateTypicalTechnicPanel(JDialog dialog, NewTechnicsPart frame) {
        this.frame = frame;
        this.dialog = dialog;
        init();
    }

    public RelateTypicalTechnicPanel(JDialog dialog, NewTechnicsPart frame, List<Technics> technicsList, Element technicElement) {
        this.frame = frame;
        this.dialog = dialog;
        this.technicsList = technicsList;
        this.technicElement = technicElement;
        init();
    }

    private void init() {
        initLookAndFeel();
        initDimension();
        initComponents();
        initLayout();
        loadInitDatas();
        initActions();
    }

    private void initLookAndFeel() {

    }

    private void initDimension() {

    }

    private void initComponents() {
        mainPanel = new JPanel();
        middlePanel = new JPanel();
        middleMainPanel = new JPanel();

        allSelectBox = new JCheckBox();

        sureButton = new JButton();
        cancelButton = new JButton();

        jScrollPane = new JScrollPane();
        jTable = new JTable();
    }

    private void initLayout() {

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(10, 0, 5, 5);
        c.gridx = 1;

        c.gridy = 1;
        middleMainPanel.setLayout(new GridBagLayout());
        jTable.setRowHeight(23);
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable.getTableHeader().setPreferredSize(new Dimension(20, 25));
        jScrollPane.setViewportView(jTable);
        jScrollPane.setPreferredSize(new Dimension(590, 270));
        middleMainPanel.add(jScrollPane, c);

        c.gridy = 2;
        allSelectBox.setPreferredSize(new Dimension(90, 25));
        sureButton.setPreferredSize(new Dimension(90, 25));
        cancelButton.setPreferredSize(new Dimension(90, 25));

        c.insets = new Insets(0, 15, 5, 290);
        middleMainPanel.add(allSelectBox, c);

        c.insets = new Insets(30, 390, 5, 0);
        middleMainPanel.add(sureButton, c);

        c.insets = new Insets(30, 490, 5, 20);
        middleMainPanel.add(cancelButton, c);

        middlePanel.add(middleMainPanel, BorderLayout.CENTER);

        mainPanel.setLayout(new BorderLayout(1, 2));
        mainPanel.add(middlePanel, BorderLayout.CENTER);

        this.add(mainPanel);

    }

    private void initActions() {
        TableModel model = jTable.getModel();
        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                System.out.println("rowNumber" + rowNumber);
                int selectedRow = jTable.getSelectedRow();
                int selectedColumn = jTable.getSelectedColumn();
                if (selectedColumn == 2) {
                    String selectedValue = (String) jTable.getValueAt(selectedRow, selectedColumn);
                    String isSelected = String.valueOf(jTable.getValueAt(selectedRow, 0));
                    if (isSelected.equals("true")) {
                        if (rowNumber < jTable.getRowCount()) {
                            String isSelect = String.valueOf(jTable.getValueAt(rowNumber, 0));
                            if (isSelect.equals("true")) {
                                rowNumber++;
                                jTable.setValueAt(selectedValue, rowNumber - 1, 2);
                            }
                        } else {
                            rowNumber = 0;
                        }
                    }
                }
            }
        });

        allSelectBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                JCheckBox box = (JCheckBox) e.getSource();
                int row = jTable.getRowCount();
                String isSelected = String.valueOf(box.isSelected());
                if (isSelected.equals("true")) {
                    for (int i = 0; i < row; i++) {
                        jTable.setValueAt(true, i, 0);
                    }
                } else if (isSelected.equals("false")) {
                    for (int i = 0; i < row; i++) {
                        jTable.setValueAt(false, i, 0);
                    }
                }
            }
        });
        sureButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stopCellEditing();
                DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
                int row = tableModel.getRowCount();
                boolean hasSame = Boolean.FALSE;
                boolean hasZF = Boolean.FALSE;
                Technics technics;
                Map<String, Technics> technicsMap = new HashMap<String, Technics>();
                String technicsType = technicElement.attributeValue("ZFFLAG");
                String technicsNumber = technicElement.attributeValue("technicsNumber");
                String technicsVersion = technicElement.attributeValue("version");
                try {
                    for (int i = 0; i < row; i++) {
                        String typicalTechnicsNumber = tableModel.getValueAt(i, 1).toString();
                        String currentTechnicsStep = tableModel.getValueAt(i, 2).toString();
                        if (currentTechnicsStep.contains("_")) {
                            currentTechnicsStep = currentTechnicsStep.substring(0, currentTechnicsStep.indexOf("_"));
                        }
                        if ("Z".equals(technicsType)) {
                            List<GLZhuFuLink> zhuFuLinkList = TechnicsIntf.getZFLinks(technicsNumber,technicsVersion);
                            String message = checkZhuFuLinks(zhuFuLinkList,currentTechnicsStep);
                            if(!"".equals(message)){
                                hasZF = Boolean.TRUE;
                                JOptionPane.showMessageDialog(dialog, message, "提示", JOptionPane.INFORMATION_MESSAGE);
                                break;
                            }
                        }
                        String typicalDocNumber = tableModel.getValueAt(i, 3).toString();
                        String typicalTechnicsVersion = tableModel.getValueAt(i, 4).toString();
                        if (currentTechnicsStep != null && !"".equals(currentTechnicsStep)) {
                            if (technicsMap.containsKey(currentTechnicsStep)) {
                                hasSame = Boolean.TRUE;
                                JOptionPane.showMessageDialog(dialog, "存在关联相同工序号的典型工艺，关联失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                break;
                            }
                            technics = new Technics();
                            technics.setTechnicsNumber(typicalTechnicsNumber);
                            technics.setDocNumber(typicalDocNumber);
                            technics.setVersion(typicalTechnicsVersion);
                            technicsMap.put(currentTechnicsStep, technics);
                        }
                    }
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                if (!hasSame && !hasZF && row > 0) {
                    List<Element> proceduresList = XmlUtility.getAllSteps(technicElement);
                    for (Element stepElement : proceduresList) {
                        String stepNumber = stepElement.attributeValue("stepNumber");
                        removeAttribute(stepElement, "relatedTypicalTechnicsNumber");
                        removeAttribute(stepElement, "relatedTypicalNumber");
                        removeAttribute(stepElement, "relatedTypicalTecThnicsVersion");
                        if (technicsMap.get(stepNumber) != null) {
                            stepElement.addAttribute("relatedTypicalTechnicsNumber", technicsMap.get(stepNumber).getTechnicsNumber());
                            stepElement.addAttribute("relatedTypicalNumber", technicsMap.get(stepNumber).getDocNumber());
                            stepElement.addAttribute("relatedTypicalTecThnicsVersion", technicsMap.get(stepNumber).getVersion());
                        }
                    }
                    Element element = technicElement.getParent();
                    saveProcess(element);

//                    frame.technicsMasterJPanel.setBorrowTechnicsTableValue(technicElement);
                    String state = technicElement.attributeValue("lifecycle");
                    if ("正在工作".equals(state) || "修改中".equals(state)) {
                        if(frame == null){
                            int isUpload = JOptionPane.showConfirmDialog(dialog, "关联成功，是否上载工艺？", "确定", JOptionPane.YES_NO_OPTION);
                            if (isUpload == JOptionPane.YES_OPTION) {
                                try {
                                    String dirPath = WorkSpaceUtil.getMesTechnicsDirectory(technicsNumber);
                                    boolean flag = getTechnicsByte(technicsNumber, technicsVersion, dirPath);
                                    if (flag) {
                                        JOptionPane.showMessageDialog(dialog, "上载成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                    } else {
                                        JOptionPane.showMessageDialog(dialog, "上载失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                    }
                                } catch (Exception e1) {
                                    JOptionPane.showMessageDialog(dialog, "上载失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                    e1.printStackTrace();
                                }
                            }
                        }else{
                            JOptionPane.showMessageDialog(dialog, "关联成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        int isUpload = JOptionPane.showConfirmDialog(frame, "关联成功，是否上载工艺？", "确定", JOptionPane.YES_NO_OPTION);
                        if (isUpload == JOptionPane.YES_OPTION) {
                            try {

                                String dirPath;
                                if (frame == null) {
                                    dirPath = WorkSpaceUtil.getMesTechnicsDirectory(technicsNumber);
                                } else {
                                    dirPath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
                                }
                                boolean flag = getTechnicsByte(technicsNumber, technicsVersion, dirPath);
                                if (flag) {
                                    JOptionPane.showMessageDialog(dialog, "上载成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(dialog, "上载失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                }
                            } catch (Exception e1) {
                                JOptionPane.showMessageDialog(dialog, "上载失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                e1.printStackTrace();
                            }
                        }
                    }
                    dialog.dispose();
                }

            }
        });
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                technicsList = null;
                dialog.dispose();
            }
        });
    }
    public String checkZhuFuLinks(List<GLZhuFuLink> zhuFuLinkList,String currentStepNumber){
        String message = "";
        for(GLZhuFuLink glZhuFuLink : zhuFuLinkList){
            String zzStepNumber = glZhuFuLink.getZzprocedurenumber();
            if(zzStepNumber.contains("_")){
                zzStepNumber = zzStepNumber.substring(0,zzStepNumber.indexOf("_"));
            }
            if(zzStepNumber.equals(currentStepNumber)){
                message = "工序" + currentStepNumber + "已与主工艺关联，请选择相应的辅工艺关联！";
                break;
            }
        }
        return message;
    }

    public void removeAttribute(Element element, String attributeName) {
        Attribute attribute = element.attribute(attributeName);
        if (attribute != null) {
            element.remove(attribute);
        }
    }

    public static boolean getTechnicsByte(String technicsNumber, String version, String dirPath) throws Exception {
        File file = new File(dirPath);
        String tempFile = System.getenv("TEMP") + File.separator + System.currentTimeMillis() + ".zip";
        ApacheZipUtil.compress(file, tempFile);
        byte[] techByte = FileUtil.readFilePathToByte(tempFile);
        return TechnicsIntf.uploadPrimaryOfDocument(techByte, technicsNumber, version);
    }

    public void saveProcess(Element newone) {
        if (newone == null) {
            return;
        }
        Document doc = newone.getDocument();
        if (doc != null) {
            try {
                Element techele = XmlUtility.getTechnicsElement(doc);
                String technicsNumber = XmlUtility.getAttributeValue(techele, "technicsNumber");

                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setTrimText(false);
                format.setEncoding("GBK");
                String path;
                if (frame != null) {
                    path = WorkSpaceUtil.getTechnicsDirectory(technicsNumber) + File.separator + technicsNumber + ".xml";
                } else {
                    path = WorkSpaceUtil.getMesTechnicsDirectory(technicsNumber) + File.separator + technicsNumber + ".xml";
                }
                XMLWriter writer = new XMLWriter(new FileOutputStream(path), format);
                writer.write(doc);
                writer.close();
                // this.technicsMasterJPanel.setUIValues(techele);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "保存过程出现错误！", "提示", 1);
            }
        }
    }

    private void loadInitDatas() {
        allSelectBox.setText("全选");
        sureButton.setText("确定");
        cancelButton.setText("取消");
        jTable.setModel(getModel());
        Vector<String> vector = new Vector<String>();
        vector.add("");
        List<Element> proceduresTE = XmlUtility.getAllSteps(technicElement);
        for (Element procedureTE : proceduresTE) {
            String stepNumber = procedureTE.attributeValue("stepNumber");
            String stepName = procedureTE.attributeValue("stepName");
            vector.add(stepNumber + "_" + stepName);
        }
        comboBox = new JComboBox(vector);
//        comboBox.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String key = (String) comboBox.getSelectedItem();
//                if (key != null && !"".equals(key)) {
//                    int count = jTable.getColumnCount();
//                    int column = jTable.getSelectedColumn();
//                    for (int i = 0; i < count; i++) {
//                        if (i == column) {
//                            continue;
//                        } else {
//                            String value = jTable.getValueAt(i, 2).toString();
//                            if (value != null && value.equals(key)) {
//                                jTable.setValueAt("", i, 2);
//                            }
//                        }
//                    }
//                }
//            }
//        });
        jTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBox));
        jTable.getTableHeader().setReorderingAllowed(false);
        loadTable();

    }

    private DefaultTableModel getModel() {
        DefaultTableModel model = null;
        model = new DefaultTableModel(new Object[][]{},
                new Object[]{"", "典型工艺规程编号", "本工艺规程编号", "docNumber", "technicsVersion"}) {
            private static final long serialVersionUID = 1L;
            Class[] types = new Class[]{Boolean.class, String.class, String.class, String.class, String.class};
            boolean[] canEdit = new boolean[]{true, false, true, false, false};

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        return model;
    }

    public void setHiddenColumn(JTable table, int columnIndex) {
        if (columnIndex >= 0 && columnIndex < table.getColumnCount()) {
            // 隐藏ID列
            table.getTableHeader().getColumnModel().getColumn(columnIndex).setMaxWidth(0);
            table.getTableHeader().getColumnModel().getColumn(columnIndex).setMinWidth(0);
            table.getColumnModel().getColumn(columnIndex).setMaxWidth(0);
            table.getColumnModel().getColumn(columnIndex).setPreferredWidth(0);
            table.getColumnModel().getColumn(columnIndex).setWidth(0);
            table.getColumnModel().getColumn(columnIndex).setMinWidth(0);
        }
    }

    private String convertNull(Object str) {
        return str == null ? "" : str.toString();
    }

    private void loadTable() {

        TableColumn column = jTable.getColumnModel().getColumn(0);
        column.setMaxWidth(50);
        column.setMinWidth(50);
        column.setPreferredWidth(50);
        column.setWidth(50);

        jTable.getColumnModel().getColumn(2).setMinWidth(10);
        jTable.getColumnModel().getColumn(2).setPreferredWidth(10);

        setHiddenColumn(jTable, 3);
        setHiddenColumn(jTable, 4);

        Map<String, String> relatedTypicalTechnicsMap = getRelatedTypicalTech();
        DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
        for (int i = 0; i < technicsList.size(); i++) {
            addOneRow(tableModel);
            String techNumber = technicsList.get(i).getTechnicsNumber();
            String docNumber = technicsList.get(i).getDocNumber();
            String docVersion = technicsList.get(i).getVersion();
            tableModel.setValueAt(false, i, 0);
            tableModel.setValueAt(techNumber, i, 1);
            if (relatedTypicalTechnicsMap.get(docNumber) != null) {
                tableModel.setValueAt(relatedTypicalTechnicsMap.get(docNumber), i, 2);
            }
            tableModel.setValueAt(docNumber, i, 3);
            tableModel.setValueAt(docVersion, i, 4);
        }
    }

    public Map<String, String> getRelatedTypicalTech() {
        Map<String, String> map = new HashMap<String, String>();
        List<Element> proceduresList = XmlUtility.getAllSteps(technicElement);
        for (Element step : proceduresList) {
            String stepNumber = step.attributeValue("stepNumber");
            String stepName = step.attributeValue("stepName");
            String relatedTypicalDocNumber = step.attributeValue("relatedTypicalNumber");
            if (relatedTypicalDocNumber != null) {
                map.put(relatedTypicalDocNumber, stepNumber + "_" + stepName);
            }
        }
        return map;
    }

    private void addOneRow(DefaultTableModel tableModel) {
        Vector vector = new Vector();
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            vector.add("");
        }
        tableModel.addRow(vector);
    }

    public void stopCellEditing() {
        if (jTable.getCellEditor() != null) {
            jTable.getCellEditor().stopCellEditing();
        }
    }
}

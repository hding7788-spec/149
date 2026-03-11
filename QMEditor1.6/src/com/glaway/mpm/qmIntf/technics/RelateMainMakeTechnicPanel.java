package com.glaway.mpm.qmIntf.technics;

import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.List;

public class RelateMainMakeTechnicPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private VaLogger logger = VaLogger.getLogger(this.getClass());

    private JPanel mainPanel;
    private JPanel middlePanel;
    private JPanel middleMainPanel;

    private JCheckBox allSelectBox;
    private JComboBox comboBox;

    private JLabel tips;

    private JButton sureButton;
    private JButton cancelButton;

    private JScrollPane jScrollPane;
    private JTable jTable;

    private JDialog dialog;
    private NewTechnicsPart frame;
    private Technics technics;
    private Element technicElement;        //辅制工艺element
    private String docNumber;            //主制工艺编号
    private List<Element> proceduresDX;
    private int rowNumber = 0;
    private String zzPicihao;
    private Map<String, Element> zzTechnicsElement;

    public RelateMainMakeTechnicPanel(JDialog dialog, NewTechnicsPart frame) {
        this.frame = frame;
        this.dialog = dialog;
        init();
    }

    public RelateMainMakeTechnicPanel(JDialog dialog, NewTechnicsPart frame, Technics technics, Element technicElement, String docNumber, String picihao, Map<String, Element> zzTechnicsElement) {
        this.frame = frame;
        this.dialog = dialog;
        this.technics = technics;
        this.technicElement = technicElement;
        this.docNumber = docNumber;
        this.zzPicihao = picihao;
        this.zzTechnicsElement = zzTechnicsElement;
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

        tips = new JLabel();
        tips.setVisible(false);

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
        middleMainPanel.add(tips, c);

        c.gridy = 2;
        jTable.setRowHeight(23);
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable.getTableHeader().setPreferredSize(new Dimension(20, 25));
        jScrollPane.setViewportView(jTable);
        jScrollPane.setPreferredSize(new Dimension(590, 270));
        middleMainPanel.add(jScrollPane, c);

        c.gridy = 3;
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
        jTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Point point = e.getPoint();
                int row = jTable.rowAtPoint(point);
                int column = jTable.columnAtPoint(point);
                System.out.println(row + "," + column);
                if (row != -1 && column != -1) {
                    List<Element> proceduresFZ = XmlUtility.getAllSteps(technicElement);
                    Element fzProcedure = proceduresFZ.get(row);

                    String workShop = fzProcedure.getParent().getParent().attributeValue("DEPT");
                    Vector<String> vector = new Vector<String>();
                    try {
                        proceduresDX = TechnicsIntf.getProceduresOfTechnic(docNumber);
                        for (Element dxProcedure : proceduresDX) {
                            String dxWorkShop = dxProcedure.attributeValue("workShop");
                            if (dxWorkShop.equals(workShop)) {
                                String stepNumber = dxProcedure.attributeValue("stepNumber");
                                String stepName = dxProcedure.attributeValue("stepName");
                                vector.add(stepNumber + "_" + stepName);
                            }
                        }
                        comboBox = new JComboBox(vector);
                        jTable.getColumnModel().getColumn(column).setCellEditor(new DefaultCellEditor(comboBox));
                        jTable.getTableHeader().setReorderingAllowed(false);
                    } catch (RemoteException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (InvocationTargetException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    jTable.updateUI();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Point point = e.getPoint();
                int row = jTable.rowAtPoint(point);
                int column = jTable.columnAtPoint(point);
                if (row != -1 && column != -1) {
                    jTable.getColumnModel().getColumn(column).setCellEditor(null);
                    jTable.updateUI();
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
                DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
                int row = tableModel.getRowCount();
                Map<String, String> zfLink = new HashMap<String, String>();
                String fzTechnicsNumber = technicElement.attributeValue("technicsNumber"); //辅制工艺文件编号
                String fzVersion = technicElement.attributeValue("version");// 辅制工艺文件版本
                fzVersion = fzVersion.substring(0, fzVersion.indexOf("."));
                String fzPicihao = technicElement.attributeValue("PCNO");//辅制工艺文件批次号
                //获取关联典型工艺的工序
                boolean hasDX = Boolean.FALSE;
                List<String> relatedTypicalStepList = new ArrayList<String>();
                if(proceduresDX == null){
                    try {
                        proceduresDX = TechnicsIntf.getProceduresOfTechnic(docNumber);
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    } catch (InvocationTargetException e1) {
                        e1.printStackTrace();
                    }
                }
                List<Element> stepList = XmlUtility.getAllSteps(proceduresDX.get(0).getParent().getParent());
                for (Element stepElement : stepList) {
                    String relatedTypicalDocNumber = stepElement.attributeValue("relatedTypicalNumber");
                    String stepNumber = stepElement.attributeValue("stepNumber");
                    String stepName = stepElement.attributeValue("stepName");
                    if (relatedTypicalDocNumber != null) {
                        if (!relatedTypicalStepList.contains(stepNumber + "_" + stepName)) {
                            relatedTypicalStepList.add(stepNumber + "_" + stepName);
                        }
                    }
                }
                //关联主制工艺
                if (fzPicihao.equals(zzPicihao)) {
                    for (int i = 0; i < row; i++) {
                        String fzProcedureNumber = tableModel.getValueAt(i, 1).toString();
                        String zzProcedureNubmer = tableModel.getValueAt(i, 2).toString();
                        if (zzProcedureNubmer == null || zzProcedureNubmer.equals("")) {
                            JOptionPane.showMessageDialog(dialog, "存在未关联的工序", "提示", JOptionPane.OK_OPTION);
                            break;
                        }
                        if (relatedTypicalStepList.contains(zzProcedureNubmer)) {
                            hasDX = Boolean.TRUE;
                            JOptionPane.showMessageDialog(dialog, "工序 "+zzProcedureNubmer+" 已与典型工艺关联，请选择其他工序关联！", "提示", JOptionPane.OK_OPTION);
                            break;
                        }
                        zfLink.put(fzProcedureNumber, zzProcedureNubmer);
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "主辅工艺批次号不一致，不能关联", "提示", JOptionPane.OK_OPTION);
                }
                try {
                    if (zfLink != null && zfLink.size() > 0 && !hasDX) {
                        Element zzElement = zzTechnicsElement.get(docNumber);
                        String zzVersion = zzElement.attributeValue("version");
                        zzVersion = zzVersion.substring(0, zzVersion.indexOf("."));
                        boolean flag = TechnicsIntf.saveZhuFuLink(fzTechnicsNumber, fzVersion, docNumber, zzVersion, fzPicihao, zfLink);
                        if (flag) {
                            JOptionPane.showMessageDialog(dialog, "关联成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, "关联失败", "提示", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }

            }
        });
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                technics = null;
                dialog.dispose();
            }
        });
    }

    private void loadInitDatas() {
        allSelectBox.setText("全选");
        sureButton.setText("确定");
        cancelButton.setText("取消");
        jTable.setModel(getModel());
//		Vector<String> vector = new Vector<String>();
//		try {
//			proceduresDX = TechnicsIntf.getProceduresOfTechnic(docNumber);
//			if(proceduresDX != null && proceduresDX.size() > 0){
//				for(Element procedureTE : proceduresDX){
//					String stepNumber = procedureTE.attributeValue("stepNumber");
//					String stepName = procedureTE.attributeValue("stepName");
//					vector.add(stepNumber + "_" + stepName);
//				}
//				comboBox = new JComboBox(vector);
//				jTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBox));

        loadTable();
//			}else{
//				JOptionPane.showMessageDialog(dialog, "错误：该主制工艺文件下没有相关工序", "提示", JOptionPane.OK_OPTION);
//				dialog.dispose();
//			}
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}

    }

    private DefaultTableModel getModel() {
        DefaultTableModel model = null;
        model = new DefaultTableModel(new Object[][]{},
                new Object[]{"", "本辅制工艺工序号", "主制工艺工序号"}) {
            private static final long serialVersionUID = 1L;
            Class[] types = new Class[]{Boolean.class,
                    String.class, String.class};
            boolean[] canEdit = new boolean[]{true, false, true};

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        return model;
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

        DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
        List<Element> proceduresFZ = XmlUtility.getAllSteps(technicElement);
        String fzTechnicsNumber = technicElement.attributeValue("technicsNumber");
        String fzVersion = technicElement.attributeValue("version");
        fzVersion = fzVersion.substring(0, fzVersion.indexOf("."));
        String picihao = technicElement.attributeValue("PCNO");
        if (proceduresFZ != null && proceduresFZ.size() > 0) {
            for (int i = 0; i < proceduresFZ.size(); i++) {
                try {
                    addOneRow(tableModel);
                    String value = proceduresFZ.get(i).attributeValue("stepNumber");
                    String name = proceduresFZ.get(i).attributeValue("stepName");
                    tableModel.setValueAt(false, i, 0);
                    tableModel.setValueAt(value + "_" + name, i, 1);
                    Element zzElement = zzTechnicsElement.get(docNumber);
                    String zzVersion = zzElement.attributeValue("version");
                    zzVersion = zzVersion.substring(0, zzVersion.indexOf("."));
                    String technicsName = zzElement.attributeValue("technicsName");
                    Map<String, String> zhufuLinkMap = TechnicsIntf.getZhuFuLink(fzTechnicsNumber, fzVersion, picihao, docNumber, zzVersion);
                    if (zhufuLinkMap != null && zhufuLinkMap.size() > 0) {
                        tips.setVisible(true);
                        if (zhufuLinkMap.get(fzTechnicsNumber).equals(docNumber)) {
                            tips.setText("提示：该辅制工艺已与所选工艺关联！");
                            tableModel.setValueAt(zhufuLinkMap.get(value + "_" + name), i, 2);
                        } else {
                            tips.setText("提示：该辅制工艺已与主制工艺（" + technicsName + "_" + zzElement.attributeValue("version") + "）关联！");
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(dialog, "错误！当前辅制工艺文件下没有相关工序", "提示", JOptionPane.OK_OPTION);
            dialog.dispose();
        }
    }

    public Technics getBorrowTechnics() {
        return technics;
    }

    private void addOneRow(DefaultTableModel tableModel) {
        Vector vector = new Vector();
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            vector.add("");
        }
        tableModel.addRow(vector);
    }

    public List<Element> getElements() {
        Vector<Element> elements = new Vector<Element>();
        Element technicDX = proceduresDX.get(0).getParent().getParent();
        Element element = DocumentHelper.createElement("borrowTechnics");
        element.setAttributeValue("bsoID", "");
        element.setAttributeValue("oid", "");
        element.setAttributeValue("technicsNumber", "");
        element.setAttributeValue("technicsName", "");
        element.setAttributeValue("technicsType", "");
        element.setAttributeValue("docNumber", "");

        XmlUtility.setAttributeValue(element, "bsoID", "");
        XmlUtility.setAttributeValue(element, "technicsNumber", technicDX.attributeValue("technicsNumber"));
        XmlUtility.setAttributeValue(element, "technicsName", technicDX.attributeValue("technicsName"));
        XmlUtility.setAttributeValue(element, "technicsType", technicDX.attributeValue("technicsType"));
        XmlUtility.setAttributeValue(element, "oid", technicDX.attributeValue("oid"));
        XmlUtility.setAttributeValue(element, "docNumber", technicDX.attributeValue("docNumber"));
        XmlUtility.setAttributeValue(element, "DEPT", technicDX.attributeValue("DEPT"));
        XmlUtility.setAttributeValue(element, "comment", technicDX.attributeValue("comment"));
        elements.add(element);
        return elements;
    }

    public static void removeHistoryData(DefaultTableModel model, Element technicElement) {
        int row = model.getRowCount();
        for (int i = 0; i < row; i++) {
            String stepNumberTE = model.getValueAt(i, 2).toString();
            List<Element> proceduresList = XmlUtility.getAllSteps(technicElement);
            for (Element procedure : proceduresList) {
                if (procedure.attributeValue("stepNumber").equals(stepNumberTE)) {
                    List<Attribute> attributes = procedure.attributes();
                    List<String> nameList = new ArrayList<String>();
                    for (Attribute attribute : attributes) {
                        String name = attribute.getName();
                        if (name.contains("relatedTypical")) {
                            nameList.add(name);
                        }
                    }
                    for (String name : nameList) {
                        procedure.remove(procedure.attribute(name));
                    }
                }
            }
        }

    }
}

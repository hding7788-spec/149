package com.glaway.mpm.view;

import com.glaway.mpm.model.data.CmAttachment;
import com.glaway.mpm.model.data.CmTreeNode;
import com.glaway.mpm.util.TechnicsReleaseUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Element;

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
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class CreateChangeMarkTechnicsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private VaLogger logger = VaLogger.getLogger(this.getClass());

    private JPanel mainPanel;
    private JPanel middlePanel;
    private JPanel middleMainPanel;

    private JCheckBox allSelectBox;

    private JLabel tips;

    private JButton sureButton;
    private JButton cancelButton;

    private JScrollPane jScrollPane;
    private JTable jTable;

    private JDialog dialog;
    private NewTechnicsPart frame;
    private int rowNumber = 0;
    private VaActionProgressBar progressBar;

    public CreateChangeMarkTechnicsPanel(JDialog dialog, NewTechnicsPart frame,VaActionProgressBar progressBar) {
        this.frame = frame;
        this.dialog = dialog;
        this.progressBar = progressBar;
        init();
        progressBar.finish();
        progressBar.setVisible(false);
    }

    private void init() {
        initComponents();
        initLayout();
        loadInitDatas();
        initActions();
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
        jScrollPane.setPreferredSize(new Dimension(690, 400));
        middleMainPanel.add(jScrollPane, c);

        c.gridy = 3;
        allSelectBox.setPreferredSize(new Dimension(90, 25));
        sureButton.setPreferredSize(new Dimension(90, 25));
        cancelButton.setPreferredSize(new Dimension(90, 25));

        c.insets = new Insets(0, 15, 5, 290);
        middleMainPanel.add(allSelectBox, c);

        c.insets = new Insets(30, 490, 5, 0);
        middleMainPanel.add(sureButton, c);

        c.insets = new Insets(30, 590, 5, 20);
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

                final VaActionProgressBar progressBar = new VaActionProgressBar(frame, "创建工艺文件", "正在创建工艺,请等待...", "工艺创建中");
                final Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            List<String> technicsNumberList = new ArrayList<String>();
                            for (int i = 0; i < jTable.getModel().getRowCount(); i++) {
                                String selected = String.valueOf(jTable.getValueAt(i, 0));
                                if ("true".equals(selected)) {
                                    technicsNumberList.add((String) jTable.getValueAt(i, 1));
                                }
                            }
                            XWTreeNode selectNode = frame.xwPartTreePanel.getSelectedTreeNode();
                            String newTechnicsNumber;
                            List<CmAttachment> attachmentList = TechnicsIntf.getTechnicsCmAttachment(technicsNumberList);
                            List<String> ppNumberList = getPpNumberList();
                            String ppNumber = "";
                            if (attachmentList != null) {
                                for (CmAttachment cmAttachment : attachmentList) {
                                    if(ppNumberList.contains(cmAttachment.getOrderNo())){
                                        if("".equals(ppNumber)){
                                            ppNumber = cmAttachment.getOrderNo();
                                        }else{
                                            ppNumber = ppNumber + "，" + cmAttachment.getOrderNo();
                                        }
                                        continue;
                                    }
                                    newTechnicsNumber = TechnicsIntf.genTechnicsNumber();
                                    if("".equals(newTechnicsNumber)){
                                        JOptionPane.showMessageDialog(frame, "工艺文件流水号生成失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                        return;
                                    }
                                    WorkSpaceUtil.createTechnicsDirectory(newTechnicsNumber);
                                    String upZipPath = WorkSpaceUtil.getTechnicsDirectory(newTechnicsNumber);
                                    TechnicsReleaseUtil.unZip(cmAttachment.getBytes(), upZipPath);
                                    File attachxmlFile = new File(upZipPath + File.separator + cmAttachment.getNumber() + ".xml");
                                    File techFile = new File(upZipPath + File.separator + newTechnicsNumber + ".xml");
                                    if (attachxmlFile.exists()) {
                                        attachxmlFile.renameTo(techFile);
                                    }
                                    if (techFile.exists()) {
                                        frame.addChangeMarkTechnic(selectNode, newTechnicsNumber);
                                    }
                                }
                            }
                            if(!"".equals(ppNumber)){
                                JOptionPane.showMessageDialog(frame, "编号为" + ppNumber + "的工艺文件已经存在，创建失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        progressBar.finish();
                        progressBar.setVisible(false);
                    }
                };
                thread.start();
                progressBar.setVisible(true);
                dialog.dispose();
            }
        });
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
    }

    private void loadInitDatas() {
        allSelectBox.setText("全选");
        sureButton.setText("确定");
        cancelButton.setText("取消");
        jTable.setModel(getModel());
        loadTable();
    }

    private DefaultTableModel getModel() {
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, new Object[]{"", "编号", "名称", "版本", "当前阶段", "关重件标识", "主制车间", "所属成品", "型号代号"}) {
            private static final long serialVersionUID = 1L;
            Class[] types = new Class[]{Boolean.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class};
            boolean[] canEdit = new boolean[]{true, false, false, false, false, false, false, false, false};

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

        jTable.getColumnModel().getColumn(1).setMinWidth(100);
        jTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTable.getColumnModel().getColumn(2).setMinWidth(200);
        jTable.getColumnModel().getColumn(2).setPreferredWidth(200);

        DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
        try {
            XWTreeNode selectNode = frame.xwPartTreePanel.getSelectedTreeNode();
            if (selectNode != null) {
                String partNumber = "";
                String partVersion = "";
                XWTreeObject object = selectNode.getObject();
                if (object instanceof XWPartTreeObject) {
                    XWPartTreeObject obj = (XWPartTreeObject) object;
                    Element partElement = obj.getTreeCellData();
                    partNumber = partElement.attributeValue("partNumber");
                    partVersion = partElement.attributeValue("version");
                    if(partVersion == null || "".equals(partVersion) || partVersion.contains("space")){
                        JOptionPane.showMessageDialog(dialog,"该部件未转阶段，无法创建转阶段工艺");
                        progressBar.finish();
                        progressBar.setVisible(false);
                        dialog.dispose();
                        return;
                    }
                }
                partVersion = getPreVersion(partVersion);

                List<CmTreeNode> cmTreeNodeList = TechnicsIntf.getPartRelatedTechnics(partNumber, partVersion);
                if(cmTreeNodeList != null){
                    for (int i = 0; i < cmTreeNodeList.size(); i++) {
                        addOneRow(tableModel);
                        tableModel.setValueAt(false, i, 0);
                        tableModel.setValueAt(cmTreeNodeList.get(i).getNumber(), i, 1);
                        tableModel.setValueAt(cmTreeNodeList.get(i).getName(), i, 2);
                        tableModel.setValueAt(cmTreeNodeList.get(i).getVersion(), i, 3);
                        tableModel.setValueAt(cmTreeNodeList.get(i).getIbaAttributes().get("PHASE_CODE"), i, 4);
                        tableModel.setValueAt(cmTreeNodeList.get(i).getIbaAttributes().get("KEYCOMPONENT"), i, 5);
                        tableModel.setValueAt(cmTreeNodeList.get(i).getIbaAttributes().get("ZZCJ"), i, 6);
                        tableModel.setValueAt(cmTreeNodeList.get(i).getIbaAttributes().get("ENDITEMIN"), i, 7);
                        tableModel.setValueAt(cmTreeNodeList.get(i).getIbaAttributes().get("MINDEX"), i, 8);
                    }
                }
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前版本的上一大版本
     * @param currentVersion
     * @return
     */
    public static String getPreVersion(String currentVersion){
        String preVersion="";
        if(currentVersion.contains(".")){
            currentVersion = currentVersion.substring(0, currentVersion.indexOf("."));
        }
        if(currentVersion == null || "".equals(currentVersion) || "space".equals(currentVersion)){
            preVersion="";
        }else if("Z".equals(currentVersion)){
            preVersion = "space";
        }else if("a".equals(currentVersion)){
            preVersion = "Z";
        }else{
            char c = currentVersion.charAt(0);
            int index = (int)c;
            index -= 1;
            c = (char)index;
            preVersion = String.valueOf(c);
        }
        return preVersion;
    }
    private void addOneRow(DefaultTableModel tableModel) {
        Vector vector = new Vector();
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            vector.add("");
        }
        tableModel.addRow(vector);
    }

    private List<String> getPpNumberList() {
        List<String> ppNumberList = new ArrayList<String>();
        Enumeration childs = frame.xwPartTreePanel.getSelectedTreeNode().children();
        if (childs != null) {
            while (childs.hasMoreElements()) {
                Object obj = childs.nextElement();
                if (obj instanceof XWTreeNode) {
                    XWTreeNode techObj = (XWTreeNode) obj;
                    XWTreeObject treeObj = techObj.getObject();
                    if (treeObj instanceof TechnicsMessageTreeObject) {
                        TechnicsMessageTreeObject tmo = (TechnicsMessageTreeObject) treeObj;
                        ppNumberList.add(tmo.getPplanNumber());
                    }
                }
            }
        }
        return ppNumberList;
    }

}

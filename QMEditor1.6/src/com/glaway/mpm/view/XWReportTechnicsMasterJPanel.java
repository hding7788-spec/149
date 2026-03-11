package com.glaway.mpm.view;

import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.List;

public class XWReportTechnicsMasterJPanel<E> extends JPanel {
    private static final long serialVersionUID = 4777218881959584579L;
    private NewTechnicsPart frame;
    private String[] tableHeaders;
    private Element techElement;
    private JButton reSet;
    //	private JButton add = new JButton("添加");
    private JTable jTable1;
    private JButton remove = new JButton("移除");
    private JButton up = new JButton("上移");
    private JButton down = new JButton("下移");
    private JButton save = new JButton("保存");
    private JButton add = new JButton("新增");
    private JSplitPane pane = new JSplitPane();
    private JPanel basicPanel = new JPanel();

    private JLabel numberLabel = new JLabel("工艺编号:");
    private JLabel numberValue = new JLabel("");

    private JLabel typeLabel = new JLabel("工艺类型:");
    private JLabel typeValue = new JLabel("");

    private JLabel stateLabel = new JLabel("状态:");
    private JLabel stateValue = new JLabel("");


    private JLabel creatorLabel = new JLabel("创建者:");
    private JLabel creatorValue = new JLabel("");

    private JLabel deptLabel = new JLabel("责任部门:");

    private JComboBox deptComboBox = new JComboBox(getAllDepts().toArray());
    private JSplitPane bottomSplit;
    private ReportTechnicsTableModel tableModel;

    public ReportTechnicsTableModel getTableModel() {
        return tableModel;
    }

    private ArrayList<XWTreeNode> technicsList;

    public XWReportTechnicsMasterJPanel(NewTechnicsPart frame) {
        this.frame = frame;
        initComponents();
        getBottomSplit().setDividerLocation(0.9);

    }

    public static List<String> getAllDepts() {
        //获取到所有的车间组，然后自动定位到当前用户所在的组
        List<String> allList = new ArrayList<String>();
        Map<String, String> workShop = ResourceIntf.getWorkShops();
        if (workShop != null && workShop.size() > 0) {
            Collection<String> coll = workShop.values();
            Iterator<String> it = coll.iterator();
            while (it.hasNext()) {
                String temp = (String) it.next();
                if (temp != null && temp.trim().length() > 0) {
                    allList.add(temp);
                }
            }

            Collections.sort(allList);
        }
        return allList;
    }

    private void changeRowValue(boolean up) {
        //add by Mchen
        if (jTable1.getSelectedRowCount() > 1) {
            int[] selectedRows = jTable1.getSelectedRows();
            int maxRow = selectedRows[selectedRows.length - 1];
            int minRow = selectedRows[0];
            int copareRow = maxRow - minRow + 1;
            if (copareRow > selectedRows.length) {
                JOptionPane.showMessageDialog(frame, "请选择连续的多行进行上移或下移!");
                return;

            }
            if (up && minRow == 0) {
                return;
            }
            if (!up && maxRow == jTable1.getRowCount() - 1) {
                return;
            }
            if (up) {
                for (int i = 0; i < selectedRows.length; i++) {
                    int currentRow = selectedRows[i];
                    int neighbor = currentRow - 1;
                    Object[] obj1 = new Object[jTable1.getColumnCount()];
                    Object[] obj2 = new Object[jTable1.getColumnCount()];
                    for (int i1 = 0; i1 < jTable1.getColumnCount(); i1++) {
                        obj1[i1] = jTable1.getValueAt(currentRow, i1);
                        obj2[i1] = jTable1.getValueAt(neighbor, i1);
                    }
                    for (int j = 0; j < jTable1.getColumnCount(); j++) {
                        jTable1.setValueAt(obj2[j], currentRow, j);
                        jTable1.setValueAt(obj1[j], neighbor, j);
                    }
                }
                jTable1.setRowSelectionInterval(selectedRows[0] - 1, selectedRows[selectedRows.length - 1] - 1);
            } else if (!up) {
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int currentRow = selectedRows[i];
                    int neighbor = currentRow + 1;
                    Object[] obj1 = new Object[jTable1.getColumnCount()];
                    Object[] obj2 = new Object[jTable1.getColumnCount()];
                    for (int i1 = jTable1.getColumnCount() - 1; i1 >= 0; i1--) {
                        obj1[i1] = jTable1.getValueAt(currentRow, i1);
                        obj2[i1] = jTable1.getValueAt(neighbor, i1);
                    }
                    for (int j = 0; j < jTable1.getColumnCount(); j++) {
                        jTable1.setValueAt(obj2[j], currentRow, j);
                        jTable1.setValueAt(obj1[j], neighbor, j);
                    }
                }
                jTable1.setRowSelectionInterval(selectedRows[0] + 1, selectedRows[selectedRows.length - 1] + 1);

            }

        } else {
            int select = jTable1.getSelectedRow();
            if (select < 0)
                return;
            if (up && select == 0)
                return;
            if (!up && select == jTable1.getRowCount() - 1)
                return;
            Object[] obj1 = new Object[jTable1.getColumnCount()];
            Object[] obj2 = new Object[jTable1.getColumnCount()];
            int neighbor;
            if (up)
                neighbor = select - 1;
            else
                neighbor = select + 1;
            for (int i = 0; i < jTable1.getColumnCount(); i++) {
                obj1[i] = jTable1.getValueAt(select, i);
                obj2[i] = jTable1.getValueAt(neighbor, i);
            }
            for (int j = 0; j < jTable1.getColumnCount(); j++) {
                jTable1.setValueAt(obj2[j], select, j);
                jTable1.setValueAt(obj1[j], neighbor, j);
            }
            jTable1.setRowSelectionInterval(neighbor, neighbor);
        }
    }

    private void initComponents() {
        jTable1 = new JTable();
        setLayout(new GridBagLayout());

        initBasicPanel();

        JScrollPane scp = new JScrollPane(basicPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scp.setBorder(BorderFactory.createTitledBorder("报表类工艺信息"));
        pane.add(scp, JSplitPane.TOP);

        JScrollPane scp1 = new JScrollPane(jTable1,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel botton = new JPanel();
        JPanel bottom = new JPanel();
//		bottom.add(scp1, JSplitPane.TOP);
//		bottom.add(botton,JSplitPane.BOTTOM);

        bottomSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scp1, botton);
        bottom.add(bottomSplit);
        scp1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        ;
        SpringLayout bottomLayout = new SpringLayout();
        bottom.setLayout(bottomLayout);
        bottomLayout.putConstraint(SpringLayout.NORTH, bottomSplit, 0, SpringLayout.NORTH, bottom);
        bottomLayout.putConstraint(SpringLayout.SOUTH, bottomSplit, 0, SpringLayout.SOUTH, bottom);
        bottomLayout.putConstraint(SpringLayout.WEST, bottomSplit, 0, SpringLayout.WEST, bottom);
        bottomLayout.putConstraint(SpringLayout.EAST, bottomSplit, 0, SpringLayout.EAST, bottom);
        bottomSplit.setDividerSize(10);

        SpringLayout bottomLayout1 = new SpringLayout();

        reSet = new JButton("重新汇总");
//		botton.add(reSet, java.awt.BorderLayout.CENTER);
//		botton.add(add, java.awt.BorderLayout.CENTER);
//		botton.add(remove, java.awt.BorderLayout.CENTER);
//		botton.add(save, java.awt.BorderLayout.CENTER);
        botton.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        botton.add(reSet, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NORTH, new Insets(
                5, 50, 15, 15), 0, 0));
//		botton.add(add, new GridBagConstraints(2, 0, 1, 1, 0, 0,
//				GridBagConstraints.WEST, GridBagConstraints.NORTH, new Insets(
//						5, 50, 15, 15), 0, 0));
        botton.add(remove, new GridBagConstraints(3, 0, 1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NORTH, new Insets(
                5, 50, 15, 15), 0, 0));
        botton.add(up, new GridBagConstraints(4, 0, 1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NORTH, new Insets(
                5, 50, 15, 15), 0, 0));
        botton.add(down, new GridBagConstraints(5, 0, 1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NORTH, new Insets(
                5, 50, 15, 15), 0, 0));
        botton.add(save, new GridBagConstraints(6, 0, 1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NORTH, new Insets(
                5, 50, 15, 15), 0, 0));
        botton.add(add, new GridBagConstraints(7, 0, 1, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NORTH, new Insets(
                5, 50, 15, 15), 0, 0));


        reSet.setPreferredSize(new Dimension(80, 30));
        reSet.setMinimumSize(new Dimension(80, 30));
        reSet.setMaximumSize(new Dimension(80, 30));
//		add.setPreferredSize(new Dimension(80, 30));
//		add.setMinimumSize(new Dimension(80, 30));
//		add.setMaximumSize(new Dimension(80, 30));
        remove.setPreferredSize(new Dimension(80, 30));
        remove.setMinimumSize(new Dimension(80, 30));
        remove.setMaximumSize(new Dimension(80, 30));

        up.setPreferredSize(new Dimension(80, 30));
        up.setMinimumSize(new Dimension(80, 30));
        up.setMaximumSize(new Dimension(80, 30));

        down.setPreferredSize(new Dimension(80, 30));
        down.setMinimumSize(new Dimension(80, 30));
        down.setMaximumSize(new Dimension(80, 30));
        save.setPreferredSize(new Dimension(80, 30));
        save.setMinimumSize(new Dimension(80, 30));
        save.setMaximumSize(new Dimension(80, 30));
        add.setPreferredSize(new Dimension(80, 30));
        add.setMinimumSize(new Dimension(80, 30));
        add.setMaximumSize(new Dimension(80, 30));
        pane.add(bottom, JSplitPane.BOTTOM);
        scp1.setViewportView(jTable1);

        jTable1.getTableHeader().setReorderingAllowed(false);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 0;
        c.gridheight = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 0, 0);
        gridbag.setConstraints(pane.getTopComponent(), c);
        gridbag.setConstraints(pane.getBottomComponent(), c);

        setLayout(new GridBagLayout());
        add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(
                0, 0, 0, 0), 0, 0));
        pane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        pane.setMinimumSize(new Dimension(100, 226));
        pane.setContinuousLayout(true);
        pane.setOneTouchExpandable(true);
        pane.setDividerSize(10);
        pane.setDividerLocation(250);

        reSet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int flag = JOptionPane.showConfirmDialog(frame, "是否重新汇总？", "确定", JOptionPane.YES_NO_OPTION);
                if (flag == JOptionPane.YES_OPTION) {
                    if (frame instanceof NewTechnicsPart) {
                        try {
                            frame.xwPartTreePanel.refreshSelectNode(true);
                            XWTreeNode xwTreeNode = frame.xwPartTreePanel.getSelectedTreeNode();
                            System.out.println("------------>>>" + xwTreeNode);
                            frame.showData(frame.xwPartTreePanel.getSelectedTreeNode(), "reset");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        up.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changeRowValue(true);
            }
        });

        down.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changeRowValue(false);
            }
        });
//		add.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				int columnCount = tableModel.getColumnCount();
//				Object[] Object=new Object[columnCount];
//				Object[0]=String.valueOf(tableModel.getRowCount()+1);
//				tableModel.addRow(Object);
//			}
//		});
        remove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] sel = jTable1.getSelectedRows();
                if (sel == null || sel.length == 0) {
                    JOptionPane.showMessageDialog(frame, "请选择需要移除的数据！");
                    return;
                }
                int flag = JOptionPane.showConfirmDialog(frame, "确定要移除？", "确定", JOptionPane.YES_NO_OPTION);
                if (flag == JOptionPane.YES_OPTION) {
                    for (int i = 0; i < sel.length; i++) {
                        tableModel.removeRow(jTable1.getSelectedRow());
                    }
                    int rowCount = tableModel.getRowCount();
                    for (int i = 0; i < rowCount; i++) {
                        String value = String.valueOf(i + 1);
                        tableModel.setValueAt(value, i, 0);
                    }

                }
            }
        });
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int flag = JOptionPane.showConfirmDialog(frame, "确定要保存此工艺报表？", "确定", JOptionPane.YES_NO_OPTION);
                if (flag == JOptionPane.YES_OPTION) {
                    XWTreeNode xwTreeNode = frame.xwPartTreePanel.getSelectedTreeNode();
                    Element element2 = xwTreeNode.getObject().getTreeCellData();
                    String technicsNumber = XmlUtility.getAttributeValue(element2, "technicsNumber");
                    String path = WorkSpaceUtil.getTechnicsPath(technicsNumber);
                    List<Element> selectNodes = element2.selectNodes("dataItemValue");
                    for (int i = 0; i < selectNodes.size(); i++) {
                        element2.remove(selectNodes.get(i));
                    }
                    String type = XmlUtility.getAttributeValue(element2, "technicsType");
                    String dept = deptComboBox.getSelectedItem().toString();
                    XmlUtility.setAttributeValue(element2,"DEPT",dept);
                    ((NewTechnicsPart) frame).saveProcess(element2);
                    if ("工艺装备明细表".equals(type)) {
                        int rowCount = tableModel.getRowCount();
                        int columnCount = tableModel.getColumnCount();
                        for (int j = 0; j < rowCount; j++) {
                            Element dataEle = DocumentHelper.createElement("dataItemValue");
                            XmlUtility.setAttributeValue(dataEle, "bianhao", (String) tableModel.getValueAt(j, 1));
                            XmlUtility.setAttributeValue(dataEle, "mingcheng", (String) tableModel.getValueAt(j, 2));
                            XmlUtility.setAttributeValue(dataEle, "guige", (String) tableModel.getValueAt(j, 3));
                            XmlUtility.setAttributeValue(dataEle, "leibie", (String) tableModel.getValueAt(j, 4));
                            XmlUtility.setAttributeValue(dataEle, "shuliang", (String) tableModel.getValueAt(j, 5));
                            XmlUtility.setAttributeValue(dataEle, "jingdudengji", (String) tableModel.getValueAt(j, 6));
                            XmlUtility.setAttributeValue(dataEle, "partNumder", (String) tableModel.getValueAt(j, 7));
                            XmlUtility.setAttributeValue(dataEle, "partName", (String) tableModel.getValueAt(j, 8));
                            XmlUtility.setAttributeValue(dataEle, "shiyongchejian", (String) tableModel.getValueAt(j, 9));
                            XmlUtility.setAttributeValue(dataEle, "beizhu", (String) tableModel.getValueAt(j, 10));

                            element2.add(dataEle);
                            ((NewTechnicsPart) frame).saveProcess(element2);
                        }
                    } else if ("仪器仪表明细表".equals(type)) {
                        int rowCount = tableModel.getRowCount();
                        int columnCount = tableModel.getColumnCount();
                        for (int j = 0; j < rowCount; j++) {
                            Element dataEle = DocumentHelper.createElement("dataItemValue");
                            XmlUtility.setAttributeValue(dataEle, "bianhao", (String) tableModel.getValueAt(j, 1));
                            XmlUtility.setAttributeValue(dataEle, "mingcheng", (String) tableModel.getValueAt(j, 2));
                            XmlUtility.setAttributeValue(dataEle, "guige", (String) tableModel.getValueAt(j, 3));
                            XmlUtility.setAttributeValue(dataEle, "leibie", (String) tableModel.getValueAt(j, 4));
                            XmlUtility.setAttributeValue(dataEle, "shuliang", (String) tableModel.getValueAt(j, 5));
                            XmlUtility.setAttributeValue(dataEle, "jingdudengji", (String) tableModel.getValueAt(j, 6));
                            XmlUtility.setAttributeValue(dataEle, "partNumder", (String) tableModel.getValueAt(j, 7));
                            XmlUtility.setAttributeValue(dataEle, "partName", (String) tableModel.getValueAt(j, 8));
                            XmlUtility.setAttributeValue(dataEle, "shiyongchejian", (String) tableModel.getValueAt(j, 9));
                            XmlUtility.setAttributeValue(dataEle, "beizhu", (String) tableModel.getValueAt(j, 10));
                            element2.add(dataEle);
                            ((NewTechnicsPart) frame).saveProcess(element2);
                        }
                    } else if ("非标仪器仪表、设备明细表".equals(type)) {
                        int rowCount = tableModel.getRowCount();
                        int columnCount = tableModel.getColumnCount();
                        for (int j = 0; j < rowCount; j++) {
                            Element dataEle = DocumentHelper.createElement("dataItemValue");
                            XmlUtility.setAttributeValue(dataEle, "bianhao", (String) tableModel.getValueAt(j, 1));
                            XmlUtility.setAttributeValue(dataEle, "mingcheng", (String) tableModel.getValueAt(j, 2));
                            XmlUtility.setAttributeValue(dataEle, "guige", (String) tableModel.getValueAt(j, 3));
                            XmlUtility.setAttributeValue(dataEle, "leibie", (String) tableModel.getValueAt(j, 4));
                            XmlUtility.setAttributeValue(dataEle, "shuliang", (String) tableModel.getValueAt(j, 5));
                            XmlUtility.setAttributeValue(dataEle, "jingdudengji", (String) tableModel.getValueAt(j, 6));
                            XmlUtility.setAttributeValue(dataEle, "partNumder", (String) tableModel.getValueAt(j, 7));
                            XmlUtility.setAttributeValue(dataEle, "partName", (String) tableModel.getValueAt(j, 8));
                            XmlUtility.setAttributeValue(dataEle, "shiyongchejian", (String) tableModel.getValueAt(j, 9));
                            XmlUtility.setAttributeValue(dataEle, "beizhu", (String) tableModel.getValueAt(j, 10));
                            element2.add(dataEle);
                            ((NewTechnicsPart) frame).saveProcess(element2);
                        }
                    } else if ("标准刀量具明细表".equals(type)) {
                        int rowCount = tableModel.getRowCount();
                        int columnCount = tableModel.getColumnCount();
                        for (int j = 0; j < rowCount; j++) {
                            Element dataEle = DocumentHelper.createElement("dataItemValue");
                            XmlUtility.setAttributeValue(dataEle, "bianhao", (String) tableModel.getValueAt(j, 1));
                            XmlUtility.setAttributeValue(dataEle, "mingcheng", (String) tableModel.getValueAt(j, 2));
                            XmlUtility.setAttributeValue(dataEle, "guige", (String) tableModel.getValueAt(j, 3));
                            XmlUtility.setAttributeValue(dataEle, "leibie", (String) tableModel.getValueAt(j, 4));
                            XmlUtility.setAttributeValue(dataEle, "shuliang", (String) tableModel.getValueAt(j, 5));
                            XmlUtility.setAttributeValue(dataEle, "jingdudengji", (String) tableModel.getValueAt(j, 6));
                            XmlUtility.setAttributeValue(dataEle, "partNumber", (String) tableModel.getValueAt(j, 7));
                            XmlUtility.setAttributeValue(dataEle, "partName", (String) tableModel.getValueAt(j, 8));
                            XmlUtility.setAttributeValue(dataEle, "shiyongchejian", (String) tableModel.getValueAt(j, 9));
                            XmlUtility.setAttributeValue(dataEle, "beizhu", (String) tableModel.getValueAt(j, 10));
                            element2.add(dataEle);
                            ((NewTechnicsPart) frame).saveProcess(element2);
                        }
                    } else if ("外协件明细表".equals(type)) {
                        int rowCount = tableModel.getRowCount();
                        for (int j = 0; j < rowCount; j++) {
                            Element dataEle = DocumentHelper.createElement("dataItemValue");
                            XmlUtility.setAttributeValue(dataEle, "partNumber", (String) tableModel.getValueAt(j, 1));
                            XmlUtility.setAttributeValue(dataEle, "partName", (String) tableModel.getValueAt(j, 2));
                            XmlUtility.setAttributeValue(dataEle, "cailiao", (String) tableModel.getValueAt(j, 3));
                            XmlUtility.setAttributeValue(dataEle, "shuliang", (String) tableModel.getValueAt(j, 4));
                            XmlUtility.setAttributeValue(dataEle, "jishuxieyi", (String) tableModel.getValueAt(j, 5));
                            XmlUtility.setAttributeValue(dataEle, "chengzhidanwei", (String) tableModel.getValueAt(j, 6));
                            XmlUtility.setAttributeValue(dataEle, "gongyizhuangbei", (String) tableModel.getValueAt(j, 7));
                            XmlUtility.setAttributeValue(dataEle, "beizhu", (String) tableModel.getValueAt(j, 8));
                            element2.add(dataEle);
                            ((NewTechnicsPart) frame).saveProcess(element2);
                        }
                    } else if ("关键工序明细表".equals(type)) {
                        int rowCount = tableModel.getRowCount();
                        for (int j = 0; j < rowCount; j++) {
                            Element dataEle = DocumentHelper.createElement("dataItemValue");
                            XmlUtility.setAttributeValue(dataEle, "partNumber", (String) tableModel.getValueAt(j, 1));
                            XmlUtility.setAttributeValue(dataEle, "partName", (String) tableModel.getValueAt(j, 2));
                            XmlUtility.setAttributeValue(dataEle, "gongxuhao", (String) tableModel.getValueAt(j, 3));
                            XmlUtility.setAttributeValue(dataEle, "gongxumingcheng", (String) tableModel.getValueAt(j, 4));
                            XmlUtility.setAttributeValue(dataEle, "kongzhineirong", (String) tableModel.getValueAt(j, 5));
                            XmlUtility.setAttributeValue(dataEle, "zzdw", (String) tableModel.getValueAt(j, 6));
                            XmlUtility.setAttributeValue(dataEle, "beizhu", (String) tableModel.getValueAt(j, 7));
                            XmlUtility.setAttributeValue(dataEle, "technicsNumber", (String) tableModel.getValueAt(j, 8));
                            XmlUtility.setAttributeValue(dataEle, "kznrFlag", (String) tableModel.getValueAt(j, 9));
                            element2.add(dataEle);
                            ((NewTechnicsPart) frame).saveProcess(element2);
                        }
                    } else if ("辅助材料定额表".equals(type)) {
                        int rowCount = tableModel.getRowCount();
                        for (int j = 0; j < rowCount; j++) {
                            Element dataEle = DocumentHelper.createElement("dataItemValue");
                            XmlUtility.setAttributeValue(dataEle, "partNumber", (String) tableModel.getValueAt(j, 1));
                            XmlUtility.setAttributeValue(dataEle, "partName", (String) tableModel.getValueAt(j, 2));
                            XmlUtility.setAttributeValue(dataEle, "mingcheng", (String) tableModel.getValueAt(j, 3));
                            XmlUtility.setAttributeValue(dataEle, "paihao", (String) tableModel.getValueAt(j, 4));
                            XmlUtility.setAttributeValue(dataEle, "guige", (String) tableModel.getValueAt(j, 5));
                            XmlUtility.setAttributeValue(dataEle, "jishutiaojian", (String) tableModel.getValueAt(j, 6));
                            XmlUtility.setAttributeValue(dataEle, "pingzhongguige", (String) tableModel.getValueAt(j, 7));
                            XmlUtility.setAttributeValue(dataEle, "jiliangdanwei", (String) tableModel.getValueAt(j, 8));
                            XmlUtility.setAttributeValue(dataEle, "shuliang", (String) tableModel.getValueAt(j, 9));
                            XmlUtility.setAttributeValue(dataEle, "shiyongchejian", (String) tableModel.getValueAt(j, 10));
                            XmlUtility.setAttributeValue(dataEle, "beizhu", (String) tableModel.getValueAt(j, 11));
                            element2.add(dataEle);
                            ((NewTechnicsPart) frame).saveProcess(element2);
                        }
                    } else if ("辅助材料定额汇总表".equals(type)) {
                        int rowCount = tableModel.getRowCount();
                        for (int j = 0; j < rowCount; j++) {
                            Element dataEle = DocumentHelper.createElement("dataItemValue");
                            XmlUtility.setAttributeValue(dataEle, "mingcheng", (String) tableModel.getValueAt(j, 1));
                            XmlUtility.setAttributeValue(dataEle, "paihao", (String) tableModel.getValueAt(j, 2));
                            XmlUtility.setAttributeValue(dataEle, "guige", (String) tableModel.getValueAt(j, 3));
                            XmlUtility.setAttributeValue(dataEle, "biaozhunhao", (String) tableModel.getValueAt(j, 4));
                            XmlUtility.setAttributeValue(dataEle, "jiliangdanwei", (String) tableModel.getValueAt(j, 5));
                            XmlUtility.setAttributeValue(dataEle, "heji", (String) tableModel.getValueAt(j, 6));
                            XmlUtility.setAttributeValue(dataEle, "beizhu", (String) tableModel.getValueAt(j, 7));
                            element2.add(dataEle);
                            ((NewTechnicsPart) frame).saveProcess(element2);
                        }
                    } else if ("材料消耗工艺定额明细表".equals(type)) {
                        int rowCount = tableModel.getRowCount();
                        for (int j = 0; j < rowCount; j++) {
                            Element dataEle = DocumentHelper.createElement("dataItemValue");
                            XmlUtility.setAttributeValue(dataEle, "ljdh", (String) tableModel.getValueAt(j, 1));
                            XmlUtility.setAttributeValue(dataEle, "ljmc", (String) tableModel.getValueAt(j, 2));
                            XmlUtility.setAttributeValue(dataEle, "mtcpsl", (String) tableModel.getValueAt(j, 3));
                            XmlUtility.setAttributeValue(dataEle, "yclmc", (String) tableModel.getValueAt(j, 4));
                            XmlUtility.setAttributeValue(dataEle, "yclph", (String) tableModel.getValueAt(j, 5));
                            XmlUtility.setAttributeValue(dataEle, "yclgg", (String) tableModel.getValueAt(j, 6));
                            XmlUtility.setAttributeValue(dataEle, "ycljstj", (String) tableModel.getValueAt(j, 7));
                            XmlUtility.setAttributeValue(dataEle, "xlcc", (String) tableModel.getValueAt(j, 8));
                            XmlUtility.setAttributeValue(dataEle, "kzjs", (String) tableModel.getValueAt(j, 9));
                            XmlUtility.setAttributeValue(dataEle, "jldw", (String) tableModel.getValueAt(j, 10));
                            XmlUtility.setAttributeValue(dataEle, "mgljmz", (String) tableModel.getValueAt(j, 11));
                            XmlUtility.setAttributeValue(dataEle, "mgljgyde", (String) tableModel.getValueAt(j, 12));
                            XmlUtility.setAttributeValue(dataEle, "gymz", (String) tableModel.getValueAt(j, 13));
                            XmlUtility.setAttributeValue(dataEle, "gyde", (String) tableModel.getValueAt(j, 14));
                            XmlUtility.setAttributeValue(dataEle, "sjsl", (String) tableModel.getValueAt(j, 15));
                            XmlUtility.setAttributeValue(dataEle, "mpcc", (String) tableModel.getValueAt(j, 16));
                            XmlUtility.setAttributeValue(dataEle, "gyztrcl", (String) tableModel.getValueAt(j, 17));
                            XmlUtility.setAttributeValue(dataEle, "bz", (String) tableModel.getValueAt(j, 18));
                            element2.add(dataEle);
                            ((NewTechnicsPart) frame).saveProcess(element2);
                        }
                    } else if ("工艺文件目录".equals(type)) {
                        int rowCount = tableModel.getRowCount();
                        for (int j = 0; j < rowCount; j++) {
                            Element dataEle = DocumentHelper.createElement("dataItemValue");
                            XmlUtility.setAttributeValue(dataEle, "bianhao", (String) tableModel.getValueAt(j, 1));
                            XmlUtility.setAttributeValue(dataEle, "mingcheng", (String) tableModel.getValueAt(j, 2));
                            XmlUtility.setAttributeValue(dataEle, "daihao", (String) tableModel.getValueAt(j, 3));
                            XmlUtility.setAttributeValue(dataEle, "partName", (String) tableModel.getValueAt(j, 4));
                            XmlUtility.setAttributeValue(dataEle, "shuliang", (String) tableModel.getValueAt(j, 5));
                            XmlUtility.setAttributeValue(dataEle, "zerenbumen", (String) tableModel.getValueAt(j, 6));
                            XmlUtility.setAttributeValue(dataEle, "comment", (String) tableModel.getValueAt(j, 7));
                            XmlUtility.setAttributeValue(dataEle, "lifecycle", (String) tableModel.getValueAt(j, 8));
                            XmlUtility.setAttributeValue(dataEle, "fileNumber", (String) tableModel.getValueAt(j, 9));
                            XmlUtility.setAttributeValue(dataEle, "version", (String) tableModel.getValueAt(j, 10));
                            element2.add(dataEle);
                            ((NewTechnicsPart) frame).saveProcess(element2);
                        }
                    } else if ("工艺路线表".equals(type)) {
                        int rowCount = tableModel.getRowCount();
                        for (int j = 0; j < rowCount; j++) {
                            Element dataEle = DocumentHelper.createElement("dataItemValue");
                            XmlUtility.setAttributeValue(dataEle, "daihao", (String) tableModel.getValueAt(j, 1));
                            XmlUtility.setAttributeValue(dataEle, "mingcheng", (String) tableModel.getValueAt(j, 2));
                            XmlUtility.setAttributeValue(dataEle, "mcpsl", (String) tableModel.getValueAt(j, 3));
                            XmlUtility.setAttributeValue(dataEle, "bjsl", (String) tableModel.getValueAt(j, 4));
                            XmlUtility.setAttributeValue(dataEle, "syjsl", (String) tableModel.getValueAt(j, 5));
                            XmlUtility.setAttributeValue(dataEle, "zhuzhibumen", (String) tableModel.getValueAt(j, 6));
                            XmlUtility.setAttributeValue(dataEle, "diyibumen", (String) tableModel.getValueAt(j, 7));
                            XmlUtility.setAttributeValue(dataEle, "comment", (String) tableModel.getValueAt(j, 8));
                            element2.add(dataEle);
                            ((NewTechnicsPart) frame).saveProcess(element2);
                        }
                    } else if ("外购件（元器件、标准件）消耗工艺定额汇总表".equals(type)) {
                        int rowCount = tableModel.getRowCount();
                        for (int j = 0; j < rowCount; j++) {
                            Element dataEle = DocumentHelper.createElement("dataItemValue");
                            XmlUtility.setAttributeValue(dataEle, "mingcheng", (String) tableModel.getValueAt(j, 1));
                            XmlUtility.setAttributeValue(dataEle, "leibie", (String) tableModel.getValueAt(j, 2));
                            XmlUtility.setAttributeValue(dataEle, "paihao", (String) tableModel.getValueAt(j, 3));
                            XmlUtility.setAttributeValue(dataEle, "guige", (String) tableModel.getValueAt(j, 4));
                            XmlUtility.setAttributeValue(dataEle, "cscj", (String) tableModel.getValueAt(j, 5));
                            XmlUtility.setAttributeValue(dataEle, "jstj", (String) tableModel.getValueAt(j, 6));
                            XmlUtility.setAttributeValue(dataEle, "cailiao", (String) tableModel.getValueAt(j, 7));
                            XmlUtility.setAttributeValue(dataEle, "jxxndj", (String) tableModel.getValueAt(j, 8));
                            XmlUtility.setAttributeValue(dataEle, "bmcl", (String) tableModel.getValueAt(j, 9));
                            XmlUtility.setAttributeValue(dataEle, "danwei", (String) tableModel.getValueAt(j, 10));
                            XmlUtility.setAttributeValue(dataEle, "gyde", (String) tableModel.getValueAt(j, 11));
                            XmlUtility.setAttributeValue(dataEle, "comment", (String) tableModel.getValueAt(j, 12));
                            element2.add(dataEle);
                            ((NewTechnicsPart) frame).saveProcess(element2);
                        }
                    }
                }
            }
        });
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                XWTreeNode xwTreeNode = frame.xwPartTreePanel.getSelectedTreeNode();
                Element element2 = xwTreeNode.getObject().getTreeCellData();
                String type = XmlUtility.getAttributeValue(element2, "technicsType");
                if ("关键工序明细表".equals(type)) {
                    int rowCount = tableModel.getRowCount();
                    Integer i = rowCount + 1;
                    Vector vector = new Vector();
                    vector.add(i);
                    vector.add("");
                    vector.add("");
                    vector.add("");
                    vector.add("");
                    vector.add("新增");
                    vector.add("");
                    vector.add("");
                    vector.add("");
                    vector.add("");
                    tableModel.addRow(vector);
                }
            }
        });

        jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TableColumn column = jTable1.getTableHeader().getColumnModel().getColumn(5);
                    if(column!=null){
                        String headerValue = column.getHeaderValue().toString();
                        if(!"控制内容".equals(headerValue)){
                            return;
                        }
                        Point p = e.getPoint();
                        int row = jTable1.rowAtPoint(p);
                        if ((row < 0) || (row >= jTable1.getRowCount()))
                            return;
                        int columnIndex = jTable1.columnAtPoint(p);
                        if (columnIndex == 5) {
                            String kznr = (String) jTable1.getModel().getValueAt(row,5);
                            if("Y".equals(kznr) || "N".equals(kznr)){
                                return;
                            }
                            String kznrFlag = (String) jTable1.getModel().getValueAt(row,9);
                            XWReportTechnicsMasterJPanel.this.stopTableCellEditing();
                            new EditControlContentPanel(kznrFlag, frame, "控制内容",row,tableModel);
                        }
                    }
                }
            }
        });
    }

    private Insets insets = new Insets(0, 0, 0, 0);

    private void initBasicPanel() {

        basicPanel.setLayout(new GridBagLayout());

        numberLabel.setMaximumSize(new Dimension(130, 23));
        numberLabel.setMinimumSize(new Dimension(130, 23));
        numberLabel.setPreferredSize(new Dimension(130, 23));
        numberLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        basicPanel.add(numberLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                new Insets(0, 5, 0, 5), 0, 0));

        numberValue.setMaximumSize(new Dimension(130, 23));
        numberValue.setMinimumSize(new Dimension(130, 23));
        numberValue.setPreferredSize(new Dimension(130, 23));
        basicPanel.add(numberValue, new GridBagConstraints(1, 0, 1, 1, 1.0, 0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));


        typeLabel.setMaximumSize(new Dimension(130, 23));
        typeLabel.setMinimumSize(new Dimension(130, 23));
        typeLabel.setPreferredSize(new Dimension(130, 23));
        typeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        basicPanel.add(typeLabel, new GridBagConstraints(2, 0, 1, 1, 0, 0,
                GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                new Insets(0, 5, 0, 5), 0, 0));

        typeValue.setMaximumSize(new Dimension(130, 23));
        typeValue.setMinimumSize(new Dimension(130, 23));
        typeValue.setPreferredSize(new Dimension(130, 23));
        basicPanel.add(typeValue, new GridBagConstraints(3, 0, 1, 1, 1.0, 0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

        //第2行
        //零部件名称
        stateLabel.setMaximumSize(new Dimension(130, 23));
        stateLabel.setMinimumSize(new Dimension(130, 23));
        stateLabel.setPreferredSize(new Dimension(130, 23));
        stateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        basicPanel.add(stateLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                new Insets(0, 5, 0, 5), 0, 0));

        stateValue.setMaximumSize(new Dimension(130, 23));
        stateValue.setMinimumSize(new Dimension(130, 23));
        stateValue.setPreferredSize(new Dimension(130, 23));
        basicPanel.add(stateValue, new GridBagConstraints(1, 1, 1, 1, 1.0,
                0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));

        creatorLabel.setMaximumSize(new Dimension(130, 23));
        creatorLabel.setMinimumSize(new Dimension(130, 23));
        creatorLabel.setPreferredSize(new Dimension(130, 23));
        creatorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        basicPanel.add(creatorLabel, new GridBagConstraints(2, 1, 1, 1, 0, 0,
                GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                new Insets(0, 5, 0, 5), 0, 0));

        creatorValue.setMaximumSize(new Dimension(130, 23));
        creatorValue.setMinimumSize(new Dimension(130, 23));
        creatorValue.setPreferredSize(new Dimension(130, 23));
        basicPanel.add(creatorValue, new GridBagConstraints(3, 1, 1, 1, 1.0, 0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

        deptLabel.setMaximumSize(new Dimension(130, 23));
        deptLabel.setMinimumSize(new Dimension(130, 23));
        deptLabel.setPreferredSize(new Dimension(130, 23));
        deptLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        basicPanel.add(deptLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0,
                GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                new Insets(0, 5, 0, 5), 0, 0));

        deptComboBox.setMaximumSize(new Dimension(30, 23));
        deptComboBox.setMinimumSize(new Dimension(30, 23));
        deptComboBox.setPreferredSize(new Dimension(30, 23));
        basicPanel.add(deptComboBox, new GridBagConstraints(1, 2, 1, 1, 1.0,
                0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                insets, 0, 0));
    }

    private void setTechnicsElementAttributeValue(Element techElement) {
        numberValue.setText(techElement.attributeValue("pplanNumber"));
        typeValue.setText(techElement.attributeValue("technicsType"));
        stateValue.setText(techElement.attributeValue("lifecycle"));
        creatorValue.setText(techElement.attributeValue("creatorDisplay"));
        deptComboBox.setSelectedItem(techElement.attributeValue("DEPT"));
    }

    public void stopTableCellEditing() {
        if (jTable1.getCellEditor() != null)
            jTable1.getCellEditor().stopCellEditing();
    }

    public void setUIValues(Element element, XWTreeNode Node, String flag) {
        techElement = element;
        frame.tecnicsJTabbedPane.setTitleAt(0, "报表类工艺文件:" + element.attributeValue("pplanNumber"));
        String type = element.attributeValue("technicsType");
        String code = element.attributeValue("code");
        Map<String, List<List<String>>> map = frame.getReportTechnicsAttriMap();
        System.out.println("--------------" + Node);
        List<List<String>> list = map.get(type + ":" + code);
        if (list != null) {
            tableHeaders = new String[list.size()];
            List<Integer> editableColums = new ArrayList<Integer>();
            List<String> attriList = null;
            String isEditable = "";
            for (int i = 0; i < list.size(); i++) {
                attriList = list.get(i);
                tableHeaders[i] = attriList.get(1);
                isEditable = attriList.get(3);
                if (isEditable != null && !"".equals(isEditable) && "true".equals(isEditable)) {
                    editableColums.add(i);
                }
            }
            //Object[][] datas = getData(element, list,Node,type,flag);
            final VaActionProgressBar progressBar = new VaActionProgressBar(frame, "正在从服务端加载数据...", "正在从服务端加载数据,请等待...", "工艺加载中");
            progressBar.setHeaderMessage("从服务端获取数据中...");
            Object[][] datas = new Object[][]{};
            try {
                datas = getData(element, Node, type, flag);
                if (datas == null || datas.length == 0) {
                    int sure = JOptionPane.showConfirmDialog(frame, "该报表类工艺汇总内容为空，请确认是否继续？", "确定", JOptionPane.YES_NO_OPTION);
                    if (sure == JOptionPane.NO_OPTION) {
                        frame.delete();
                        progressBar.finish();
                        progressBar.setVisible(false);
                        return;
                    }
                }
            } catch (Exception e) {
                progressBar.setHeaderMessage("从服务端获取数据失败");
                e.printStackTrace();
            }
            progressBar.setHeaderMessage("从服务端获取数据成功");
            progressBar.finish();
            progressBar.setVisible(false);
            tableModel = new ReportTechnicsTableModel(datas, tableHeaders);
            tableModel.setEditableColums(editableColums);
            jTable1.setModel(tableModel);
            if ("关键工序明细表".equals(type)) {
                jTable1.getColumnModel().getColumn(8).setMaxWidth(0);
                jTable1.getColumnModel().getColumn(8).setMinWidth(0);
                jTable1.getColumnModel().getColumn(9).setMaxWidth(0);
                jTable1.getColumnModel().getColumn(9).setMinWidth(0);
                jTable1.getColumnModel().getColumn(8).setPreferredWidth(0);
                jTable1.getColumnModel().getColumn(9).setPreferredWidth(0);
            }
            jTable1.updateUI();
        }

        setTechnicsElementAttributeValue(techElement);
        String state = techElement.attributeValue("lifecycle");
        String creator = techElement.attributeValue("creator");
        if (("正在工作".equals(state) || "修改中".equals(state)) && NewTechnicsPart.currentUser.equals(creator)) {
            setUIEnable(true);
        } else {
            setUIEnable(false);
        }

        if ("关键工序明细表".equals(type) && (("正在工作".equals(state) || "修改中".equals(state)) && NewTechnicsPart.currentUser.equals(creator))) {
            add.setVisible(true);
            add.setEnabled(true);
        }else{
            add.setVisible(false);
            add.setEnabled(false);
        }
    }


    public ArrayList<XWTreeNode> getTechinicsList(XWTreeNode node, String type, boolean isTop) {
        ArrayList<XWTreeNode> list = new ArrayList<XWTreeNode>();
        for (int i = 0; i < node.getChildCount(); i++) {
            XWTreeNode child = (XWTreeNode) node.getChildAt(i);
            Element treeCellData = child.getObject().getTreeCellData();
            String value = treeCellData.attributeValue("PPLANTYPE");
            if (!"临时工艺文件".equals(value)) {
                if (isTop && "工艺文件目录".equals(type)) {
                    if (child.getObject() instanceof ReportTechnicsTreeObject) {
                        ReportTechnicsTreeObject tech = (ReportTechnicsTreeObject) child.getObject();
                        String ttype = tech.getType();
                        if (!"工艺文件目录".equals(ttype)) {
                            list.add(child);
                        }

                    }
                }
                if (child.getObject() instanceof TechnicsMessageTreeObject) {
                    list.add(child);
                } else if (child.getObject() instanceof XWPartTreeObject) {
                    ArrayList<XWTreeNode> list2 = getTechinicsList(child, type, false);
                    for (int j = 0; j < list2.size(); j++) {
                        list.add(list2.get(j));
                    }
                }
            }


        }
        return list;

    }

    public void getPartList2(XWTreeNode node, List<XWTreeNode> listPart) {
        for (int i = 0; i < node.getChildCount(); i++) {
            XWTreeNode child = (XWTreeNode) node.getChildAt(i);
            if (child.getObject() instanceof XWPartTreeObject) {
                listPart.add(child);
                getPartList2(child, listPart);
            }
        }
    }

    public ArrayList<XWTreeNode> getPartList(XWTreeNode node) {
        ArrayList<XWTreeNode> list = new ArrayList<XWTreeNode>();
        for (int i = 0; i < node.getChildCount(); i++) {
            //int m=node.getChildCount();
            XWTreeNode child = (XWTreeNode) node.getChildAt(i);
            String mtype = child.getObject().getTreeCellData().attributeValue("mtype");
            if (child.getObject() instanceof XWPartTreeObject && "外购件".equals(mtype)) {
                list.add(child);
                ArrayList<XWTreeNode> list2 = getPartList(child);
                for (int j = 0; j < list2.size(); j++) {
                    list.add(list2.get(j));
                }

            }

        }
        return list;

    }

    private String getElementValues(Element ele, String qname, String key) {
        String values = "";
        List<Element> list = ele.elements(qname);
        for (Element element : list) {
            String temp = element.attributeValue(key);
            if (temp != null && !"".equals(temp)) {
                if (temp.startsWith("A%")) {
                    temp = temp.replaceAll("A%", "");
                }
                if (!"".equals(values)) {
                    values = values + "," + temp;
                } else {
                    values = temp;
                }
            }
        }
        return values;
    }

    private Object[][] getData(Element element, XWTreeNode node, String type, String flag) throws RemoteException, InvocationTargetException {
        XWTreeNode treeNode = node.getP();
        List<XWTreeNode> listPart = new ArrayList<XWTreeNode>();
        List<Element> list2 = element.selectNodes("dataItemValue");
        if (list2.size() == 0 || "reset".equals(flag)) {
            if ("cache".equals(com.glaway.mpm.EditorConfig.startType)) {
                Element partEle = treeNode.getObject().getTreeCellData();
                String partOid = partEle.attributeValue("oid");
                Object[][] datas = TechnicsIntf.getReportDatasRMI(partOid, type, com.glaway.mpm.EditorConfig.startType);
                return datas;
            }

            if (!"1".equals(com.glaway.mpm.EditorConfig.startType)) {
                List<String> partOids = new ArrayList<String>();
                listPart.add(treeNode);
                getPartList2(treeNode, listPart);
                for (XWTreeNode n : listPart) {
                    Element partEle = n.getObject().getTreeCellData();
                    String oid = partEle.attributeValue("oid");
                    if (!partOids.contains(oid))
                        partOids.add(oid);
                }
                Object[][] datas = TechnicsIntf.getReportDatasRMI(partOids, type);
                return datas;
            } else {
                Element partEle = treeNode.getObject().getTreeCellData();
                String partOid = partEle.attributeValue("oid");
                Object[][] datas = TechnicsIntf.getReportDatasRMI(partOid, type);
                return datas;
            }


        } else {
            if ("工艺装备明细表".equals(type)) {
                Object[][] object = new Object[list2.size()][11];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("bianhao");
                    object[j][2] = list2.get(j).attributeValue("mingcheng");
                    object[j][3] = list2.get(j).attributeValue("guige");
                    object[j][4] = list2.get(j).attributeValue("leibie");
                    object[j][5] = list2.get(j).attributeValue("shuliang");
                    object[j][6] = list2.get(j).attributeValue("jingdudengji");
                    object[j][7] = list2.get(j).attributeValue("partNumber");
                    object[j][8] = list2.get(j).attributeValue("partName");
                    object[j][9] = list2.get(j).attributeValue("shiyongchejian");
                    object[j][10] = list2.get(j).attributeValue("beizhu");
                }
                return object;
            } else if ("仪器仪表明细表".equals(type)) {
                Object[][] object = new Object[list2.size()][11];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("bianhao");
                    object[j][2] = list2.get(j).attributeValue("mingcheng");
                    object[j][3] = list2.get(j).attributeValue("guige");
                    object[j][4] = list2.get(j).attributeValue("leibie");
                    object[j][5] = list2.get(j).attributeValue("shuliang");
                    object[j][6] = list2.get(j).attributeValue("jingdudengji");
                    object[j][7] = list2.get(j).attributeValue("partNumber");
                    object[j][8] = list2.get(j).attributeValue("partName");
                    object[j][9] = list2.get(j).attributeValue("shiyongchejian");
                    object[j][10] = list2.get(j).attributeValue("beizhu");
                }

                return object;

            } else if ("非标仪器仪表、设备明细表".equals(type)) {

                Object[][] object = new Object[list2.size()][11];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("bianhao");
                    object[j][2] = list2.get(j).attributeValue("mingcheng");
                    object[j][3] = list2.get(j).attributeValue("guige");
                    object[j][4] = list2.get(j).attributeValue("leibie");
                    object[j][5] = list2.get(j).attributeValue("shuliang");
                    object[j][6] = list2.get(j).attributeValue("jingdudengji");
                    object[j][7] = list2.get(j).attributeValue("partNumber");
                    object[j][8] = list2.get(j).attributeValue("partName");
                    object[j][9] = list2.get(j).attributeValue("shiyongchejian");
                    object[j][10] = list2.get(j).attributeValue("beizhu");
                }
                return object;
            } else if ("标准刀量具明细表".equals(type)) {
                Object[][] object = new Object[list2.size()][11];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("bianhao");
                    object[j][2] = list2.get(j).attributeValue("mingcheng");
                    object[j][3] = list2.get(j).attributeValue("guige");
                    object[j][4] = list2.get(j).attributeValue("leibie");
                    object[j][5] = list2.get(j).attributeValue("shuliang");
                    object[j][6] = list2.get(j).attributeValue("jingdudengji");
                    object[j][7] = list2.get(j).attributeValue("partNumber");
                    object[j][8] = list2.get(j).attributeValue("partName");
                    object[j][9] = list2.get(j).attributeValue("shiyongchejian");
                    object[j][10] = list2.get(j).attributeValue("beizhu");
                }
                return object;
            } else if ("外协件明细表".equals(type)) {
                Object[][] object = new Object[list2.size()][9];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("partNumber");
                    object[j][2] = list2.get(j).attributeValue("partName");
                    object[j][3] = list2.get(j).attributeValue("cailiao");
                    object[j][4] = list2.get(j).attributeValue("shuliang");
                    object[j][5] = list2.get(j).attributeValue("jishuxieyi");
                    object[j][6] = list2.get(j).attributeValue("chengzhidanwei");
                    object[j][7] = list2.get(j).attributeValue("gongyizhuangbei");
                    object[j][8] = list2.get(j).attributeValue("beizhu");
                }
                return object;
            } else if ("关键工序明细表".equals(type)) {
                Object[][] object = new Object[list2.size()][10];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("partNumber");
                    object[j][2] = list2.get(j).attributeValue("partName");
                    object[j][3] = list2.get(j).attributeValue("gongxuhao");
                    object[j][4] = list2.get(j).attributeValue("gongxumingcheng");
                    object[j][5] = list2.get(j).attributeValue("kongzhineirong");
                    object[j][6] = list2.get(j).attributeValue("zzdw");
                    object[j][7] = list2.get(j).attributeValue("beizhu");
                    object[j][8] = list2.get(j).attributeValue("technicsNumber");
                    object[j][9] = list2.get(j).attributeValue("kznrFlag");
                }
                return object;
            } else if ("辅助材料定额表".equals(type)) {
                Object[][] object = new Object[list2.size()][12];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("partNumber");
                    object[j][2] = list2.get(j).attributeValue("partName");
                    object[j][3] = list2.get(j).attributeValue("mingcheng");
                    object[j][4] = list2.get(j).attributeValue("paihao");
                    object[j][5] = list2.get(j).attributeValue("guige");
                    object[j][6] = list2.get(j).attributeValue("jishutiaojian");
                    object[j][7] = list2.get(j).attributeValue("pingzhongguige");
                    object[j][8] = list2.get(j).attributeValue("jiliangdanwei");
                    object[j][9] = list2.get(j).attributeValue("shuliang");
                    object[j][10] = list2.get(j).attributeValue("shiyongchejian");
                    object[j][11] = list2.get(j).attributeValue("beizhu");
                }
                return object;
            } else if ("辅助材料定额汇总表".equals(type)) {
                Object[][] object = new Object[list2.size()][8];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("mingcheng");
                    object[j][2] = list2.get(j).attributeValue("paihao");
                    object[j][3] = list2.get(j).attributeValue("guige");
                    object[j][4] = list2.get(j).attributeValue("biaozhunhao");
                    object[j][5] = list2.get(j).attributeValue("jiliangdanwei");
                    object[j][6] = list2.get(j).attributeValue("heji");
                    object[j][7] = list2.get(j).attributeValue("beizhu");
                }
                return object;
            } else if ("材料消耗工艺定额明细表".equals(type)) {

                Object[][] object = new Object[list2.size()][19];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("ljdh");
                    object[j][2] = list2.get(j).attributeValue("ljmc");
                    object[j][3] = list2.get(j).attributeValue("mtcpsl");
                    object[j][4] = list2.get(j).attributeValue("yclmc");
                    object[j][5] = list2.get(j).attributeValue("yclph");
                    object[j][6] = list2.get(j).attributeValue("yclgg");
                    object[j][7] = list2.get(j).attributeValue("ycljstj");
                    object[j][8] = list2.get(j).attributeValue("xlcc");
                    object[j][9] = list2.get(j).attributeValue("kzjs");
                    object[j][10] = list2.get(j).attributeValue("jldw");
                    object[j][11] = list2.get(j).attributeValue("mgljmz");
                    object[j][12] = list2.get(j).attributeValue("mgljgyde");
                    object[j][13] = list2.get(j).attributeValue("gymz");
                    object[j][14] = list2.get(j).attributeValue("gyde");
                    object[j][15] = list2.get(j).attributeValue("sjsl");
                    object[j][16] = list2.get(j).attributeValue("mpcc");
                    object[j][17] = list2.get(j).attributeValue("gyztrcl");
                    object[j][18] = list2.get(j).attributeValue("bz");
                }
                return object;

            } else if ("工艺文件目录".equals(type)) {
                Object[][] object = new Object[list2.size()][11];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("bianhao");
                    object[j][2] = list2.get(j).attributeValue("mingcheng");
                    object[j][3] = list2.get(j).attributeValue("daihao");
                    object[j][4] = list2.get(j).attributeValue("partName");
                    object[j][5] = list2.get(j).attributeValue("shuliang");
                    object[j][6] = list2.get(j).attributeValue("zerenbumen");
                    object[j][7] = list2.get(j).attributeValue("comment");
                    object[j][8] = list2.get(j).attributeValue("lifecycle");
                    object[j][9] = list2.get(j).attributeValue("fileNumber");
                    object[j][10] = list2.get(j).attributeValue("version");
                }
                return object;
            } else if ("工艺路线表".equals(type)) {
                Object[][] object = new Object[list2.size()][9];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("daihao");
                    object[j][2] = list2.get(j).attributeValue("mingcheng");
                    object[j][3] = list2.get(j).attributeValue("mcpsl");
                    object[j][4] = list2.get(j).attributeValue("bjsl");
                    object[j][5] = list2.get(j).attributeValue("syjsl");
                    object[j][6] = list2.get(j).attributeValue("zhuzhibumen");
                    object[j][7] = list2.get(j).attributeValue("diyibumen");
                    object[j][8] = list2.get(j).attributeValue("comment");
                }
                return object;
            } else if ("外购件（元器件、标准件）消耗工艺定额汇总表".equals(type)) {
                Object[][] object = new Object[list2.size()][13];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("mingcheng");
                    object[j][2] = list2.get(j).attributeValue("leibie");
                    object[j][3] = list2.get(j).attributeValue("paihao");
                    object[j][4] = list2.get(j).attributeValue("guige");
                    object[j][5] = list2.get(j).attributeValue("cscj");
                    object[j][6] = list2.get(j).attributeValue("jstj");
                    object[j][7] = list2.get(j).attributeValue("cailiao");
                    object[j][8] = list2.get(j).attributeValue("jxxndj");
                    object[j][9] = list2.get(j).attributeValue("bmcl");
                    object[j][10] = list2.get(j).attributeValue("danwei");
                    object[j][11] = list2.get(j).attributeValue("gyde");
                    object[j][12] = list2.get(j).attributeValue("comment");
                }
                return object;
            }
        }
        return null;

    }

    private Object[][] getData(Element element, List<List<String>> list, XWTreeNode node, String type, String flag) {
        XWTreeNode treeNode = node.getP();
        ArrayList<XWTreeNode> listPart = getPartList(treeNode);

        technicsList = new ArrayList<XWTreeNode>();
        ArrayList<XWTreeNode> partList = new ArrayList<XWTreeNode>();

        ArrayList<XWTreeNode> list1 = getTechinicsList(treeNode, type, true);
        ArrayList<XWTreeNode> zzTechnicsList = new ArrayList<XWTreeNode>();
        for (int i = 0; i < list1.size(); i++) {
            technicsList.add(list1.get(i));
            if ("Z".equals(list1.get(i).getObject().getTreeCellData().attributeValue("ZFFLAG"))) {
                zzTechnicsList.add(list1.get(i));
            }
        }
        for (int i = 0; i < listPart.size(); i++) {
            partList.add(listPart.get(i));
        }
        List<Element> list2 = element.selectNodes("dataItemValue");
        if (list2.size() == 0 || "reset".equals(flag)) {
            if ("工艺装备明细表".equals(type)) {
                Object[][] gongZhuang = NewTechnicsReportUtil.getGongZhuang(technicsList);
                return gongZhuang;
            } else if ("仪器仪表明细表".equals(type)) {
                Object[][] biaozhunYiQi = NewTechnicsReportUtil.getBiaozhunYiQi(technicsList);
                return biaozhunYiQi;
            } else if ("非标仪器仪表、设备明细表".equals(type)) {
                Object[][] feiBiaoZhunYiQi = NewTechnicsReportUtil.getFeiBiaoZhunYiQi(technicsList);
                return feiBiaoZhunYiQi;
            } else if ("标准刀量具明细表".equals(type)) {
                Object[][] daoLiangJu = NewTechnicsReportUtil.getDaoLiangJu(technicsList);
                return daoLiangJu;
            } else if ("外协件明细表".equals(type)) {
                Object[][] waiXieJian = NewTechnicsReportUtil.getWaiXieJian(technicsList);
                return waiXieJian;
            } else if ("关键工序明细表".equals(type)) {
                Object[][] guanJianGongXu = NewTechnicsReportUtil.getGuanJianGongXu(technicsList);
                return guanJianGongXu;
            } else if ("辅助材料定额表".equals(type)) {
                Object[][] fuZhuCaiLiao = NewTechnicsReportUtil.getFuZhuCaiLiao(technicsList);
                return fuZhuCaiLiao;
            } else if ("辅助材料定额汇总表".equals(type)) {
                Object[][] fuZhuCaiLiaoHuiZong = NewTechnicsReportUtil.getFuZhuCaiLiaoHuiZong(technicsList);
                return fuZhuCaiLiaoHuiZong;
            } else if ("材料消耗工艺定额明细表".equals(type)) {
                Object[][] caiLiaoHuiZong = NewTechnicsReportUtil.getCaiLiaoHuiZong(technicsList);
                return caiLiaoHuiZong;
            } else if ("工艺文件目录".equals(type)) {
                Object[][] gonyiMulu = NewTechnicsReportUtil.getGonyiMulu(technicsList);
                return gonyiMulu;
            } else if ("工艺路线表".equals(type)) {
                Object[][] gongYiLuXian = NewTechnicsReportUtil.getGongYiLuXian(zzTechnicsList);
                return gongYiLuXian;
            } else if ("外购件（元器件、标准件）消耗工艺定额汇总表".equals(type)) {
                Object[][] waigoujian = NewTechnicsReportUtil.getWaigoujian(technicsList, partList);
                return waigoujian;
            }
        } else {

            if ("工艺装备明细表".equals(type)) {
                Object[][] object = new Object[list2.size()][11];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("bianhao");
                    object[j][2] = list2.get(j).attributeValue("mingcheng");
                    object[j][3] = list2.get(j).attributeValue("guige");
                    object[j][4] = list2.get(j).attributeValue("leibie");
                    object[j][5] = list2.get(j).attributeValue("shuliang");
                    object[j][6] = list2.get(j).attributeValue("jingdudengji");
                    object[j][7] = list2.get(j).attributeValue("partNumber");
                    object[j][8] = list2.get(j).attributeValue("partName");
                    object[j][9] = list2.get(j).attributeValue("shiyongchejian");
                    object[j][10] = list2.get(j).attributeValue("beizhu");
                }
                return object;
            } else if ("仪器仪表明细表".equals(type)) {
                Object[][] object = new Object[list2.size()][11];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("bianhao");
                    object[j][2] = list2.get(j).attributeValue("mingcheng");
                    object[j][3] = list2.get(j).attributeValue("guige");
                    object[j][4] = list2.get(j).attributeValue("leibie");
                    object[j][5] = list2.get(j).attributeValue("shuliang");
                    object[j][6] = list2.get(j).attributeValue("jingdudengji");
                    object[j][7] = list2.get(j).attributeValue("partNumber");
                    object[j][8] = list2.get(j).attributeValue("partName");
                    object[j][9] = list2.get(j).attributeValue("shiyongchejian");
                    object[j][10] = list2.get(j).attributeValue("beizhu");
                }

                return object;

            } else if ("非标仪器仪表、设备明细表".equals(type)) {

                Object[][] object = new Object[list2.size()][11];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("bianhao");
                    object[j][2] = list2.get(j).attributeValue("mingcheng");
                    object[j][3] = list2.get(j).attributeValue("guige");
                    object[j][4] = list2.get(j).attributeValue("leibie");
                    object[j][5] = list2.get(j).attributeValue("shuliang");
                    object[j][6] = list2.get(j).attributeValue("jingdudengji");
                    object[j][7] = list2.get(j).attributeValue("partNumber");
                    object[j][8] = list2.get(j).attributeValue("partName");
                    object[j][9] = list2.get(j).attributeValue("shiyongchejian");
                    object[j][10] = list2.get(j).attributeValue("beizhu");
                }
                return object;
            } else if ("标准刀量具明细表".equals(type)) {
                Object[][] object = new Object[list2.size()][11];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("bianhao");
                    object[j][2] = list2.get(j).attributeValue("mingcheng");
                    object[j][3] = list2.get(j).attributeValue("guige");
                    object[j][4] = list2.get(j).attributeValue("leibie");
                    object[j][5] = list2.get(j).attributeValue("shuliang");
                    object[j][6] = list2.get(j).attributeValue("jingdudengji");
                    object[j][7] = list2.get(j).attributeValue("partNumber");
                    object[j][8] = list2.get(j).attributeValue("partName");
                    object[j][9] = list2.get(j).attributeValue("shiyongchejian");
                    object[j][10] = list2.get(j).attributeValue("beizhu");
                }
                return object;
            } else if ("外协件明细表".equals(type)) {
                Object[][] object = new Object[list2.size()][9];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("partNumber");
                    object[j][2] = list2.get(j).attributeValue("partName");
                    object[j][3] = list2.get(j).attributeValue("cailiao");
                    object[j][4] = list2.get(j).attributeValue("shuliang");
                    object[j][5] = list2.get(j).attributeValue("jishuxieyi");
                    object[j][6] = list2.get(j).attributeValue("chengzhidanwei");
                    object[j][7] = list2.get(j).attributeValue("gongyizhuangbei");
                    object[j][8] = list2.get(j).attributeValue("beizhu");
                }
                return object;
            } else if ("关键工序明细表".equals(type)) {
                Object[][] object = new Object[list2.size()][10];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("partNumber");
                    object[j][2] = list2.get(j).attributeValue("partName");
                    object[j][3] = list2.get(j).attributeValue("gongxuhao");
                    object[j][4] = list2.get(j).attributeValue("gongxumingcheng");
                    object[j][5] = list2.get(j).attributeValue("kongzhineirong");
                    object[j][6] = list2.get(j).attributeValue("neirongbiaozhun");
                    object[j][7] = list2.get(j).attributeValue("beizhu");
                    object[j][8] = list2.get(j).attributeValue("technicsNumber");
                    object[j][9] = list2.get(j).attributeValue("kznrFlag");
                }
                return object;
            } else if ("辅助材料定额表".equals(type)) {
                Object[][] object = new Object[list2.size()][12];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("partNumber");
                    object[j][2] = list2.get(j).attributeValue("partName");
                    object[j][3] = list2.get(j).attributeValue("mingcheng");
                    object[j][4] = list2.get(j).attributeValue("paihao");
                    object[j][5] = list2.get(j).attributeValue("guige");
                    object[j][6] = list2.get(j).attributeValue("jishutiaojian");
                    object[j][7] = list2.get(j).attributeValue("pingzhongguige");
                    object[j][8] = list2.get(j).attributeValue("jiliangdanwei");
                    object[j][9] = list2.get(j).attributeValue("shuliang");
                    object[j][10] = list2.get(j).attributeValue("shiyongchejian");
                    object[j][11] = list2.get(j).attributeValue("beizhu");
                }
                return object;
            } else if ("辅助材料定额汇总表".equals(type)) {
                Object[][] object = new Object[list2.size()][8];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("mingcheng");
                    object[j][2] = list2.get(j).attributeValue("paihao");
                    object[j][3] = list2.get(j).attributeValue("guige");
                    object[j][4] = list2.get(j).attributeValue("biaozhunhao");
                    object[j][5] = list2.get(j).attributeValue("jiliangdanwei");
                    object[j][6] = list2.get(j).attributeValue("heji");
                    object[j][7] = list2.get(j).attributeValue("beizhu");
                }
                return object;
            } else if ("材料消耗工艺定额明细表".equals(type)) {

                Object[][] object = new Object[list2.size()][19];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("ljdh");
                    object[j][2] = list2.get(j).attributeValue("ljmc");
                    object[j][3] = list2.get(j).attributeValue("mtcpsl");
                    object[j][4] = list2.get(j).attributeValue("yclmc");
                    object[j][5] = list2.get(j).attributeValue("yclph");
                    object[j][6] = list2.get(j).attributeValue("yclgg");
                    object[j][7] = list2.get(j).attributeValue("ycljstj");
                    object[j][8] = list2.get(j).attributeValue("xlcc");
                    object[j][9] = list2.get(j).attributeValue("kzjs");
                    object[j][10] = list2.get(j).attributeValue("jldw");
                    object[j][11] = list2.get(j).attributeValue("mgljmz");
                    object[j][12] = list2.get(j).attributeValue("mgljgyde");
                    object[j][13] = list2.get(j).attributeValue("gymz");
                    object[j][14] = list2.get(j).attributeValue("gyde");
                    object[j][15] = list2.get(j).attributeValue("sjsl");
                    object[j][16] = list2.get(j).attributeValue("mpcc");
                    object[j][17] = list2.get(j).attributeValue("gyztrcl");
                    object[j][18] = list2.get(j).attributeValue("bz");
                }
                return object;

            } else if ("工艺文件目录".equals(type)) {
                Object[][] object = new Object[list2.size()][11];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("bianhao");
                    object[j][2] = list2.get(j).attributeValue("mingcheng");
                    object[j][3] = list2.get(j).attributeValue("daihao");
                    object[j][4] = list2.get(j).attributeValue("partName");
                    object[j][5] = list2.get(j).attributeValue("shuliang");
                    object[j][6] = list2.get(j).attributeValue("zerenbumen");
                    object[j][7] = list2.get(j).attributeValue("comment");
                    object[j][8] = list2.get(j).attributeValue("lifecycle");
                    object[j][9] = list2.get(j).attributeValue("fileNumber");
                    object[j][10] = list2.get(j).attributeValue("version");
                }
                return object;
            } else if ("工艺路线表".equals(type)) {
                Object[][] object = new Object[list2.size()][9];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("daihao");
                    object[j][2] = list2.get(j).attributeValue("mingcheng");
                    object[j][3] = list2.get(j).attributeValue("mcpsl");
                    object[j][4] = list2.get(j).attributeValue("bjsl");
                    object[j][5] = list2.get(j).attributeValue("syjsl");
                    object[j][6] = list2.get(j).attributeValue("zhuzhibumen");
                    object[j][7] = list2.get(j).attributeValue("diyibumen");
                    object[j][8] = list2.get(j).attributeValue("comment");
                }
                return object;
            } else if ("外购件（元器件、标准件）消耗工艺定额汇总表".equals(type)) {
                Object[][] object = new Object[list2.size()][13];
                for (int j = 0; j < list2.size(); j++) {
                    object[j][0] = String.valueOf(j + 1);
                    object[j][1] = list2.get(j).attributeValue("mingcheng");
                    object[j][2] = list2.get(j).attributeValue("leibie");
                    object[j][3] = list2.get(j).attributeValue("paihao");
                    object[j][4] = list2.get(j).attributeValue("guige");
                    object[j][5] = list2.get(j).attributeValue("cscj");
                    object[j][6] = list2.get(j).attributeValue("jstj");
                    object[j][7] = list2.get(j).attributeValue("cailiao");
                    object[j][8] = list2.get(j).attributeValue("jxxndj");
                    object[j][9] = list2.get(j).attributeValue("bmcl");
                    object[j][10] = list2.get(j).attributeValue("danwei");
                    object[j][11] = list2.get(j).attributeValue("gyde");
                    object[j][12] = list2.get(j).attributeValue("comment");
                }
                return object;
            }
        }


        return new Object[][]{};
    }

    public void setUIEnable(boolean b) {
        jTable1.setEnabled(b);
        reSet.setEnabled(b);
//    	add.setEnabled(b);
        remove.setEnabled(b);
        up.setEnabled(b);
        down.setEnabled(b);
        save.setEnabled(b);
    }

    public JSplitPane getBottomSplit() {
        return bottomSplit;
    }

    public void setButtonEnable(boolean b) {
//    	add.setVisible(b);
        remove.setVisible(b);
        save.setVisible(b);
    }

    public JTable getjTable1() {
        return jTable1;
    }


}

package com.glaway.mpm.view;

import com.glaway.mpm.task.SearchTaskUserDialog;
import com.glaway.mpm.util.*;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.List;

/**
 * 用户批量提交辅制工艺任务
 * 入口在PBOM的右键菜单，收集该PBOM及其所有子件的属于当前用户创建的主制工艺文件
 *
 * @author cjh
 */
public class BatchFzTaskSubmitJDialog {
    private NewTechnicsPart frame;
    private JDialog dialog;
    private JPanel centerPanel;
    private JScrollPane scrollPane;
    private JTable table;
    private JPanel bottomPanel;
    private JButton sure;
    private JButton cancel;
    private JButton selectall;
    private JButton noselect;
    private XWTreeNode node;
    private JComboBox renwuTypeComboBox;
    private String[] taskType = new String[]{"工艺设计任务", "工艺更改任务", "临时工艺任务"};
    private JTextField planTimeField;
    private JTextField cldePlanTimeField;
    private JTextField taskDescField;

    public BatchFzTaskSubmitJDialog(NewTechnicsPart frame, XWTreeNode node) {
        super();
        this.frame = frame;
        this.node = node;
        showDialog();
    }

    public void showDialog() {
        newJDialog();
        initComponents();
        loadData();
        dialog.setModal(true);
        dialog.setResizable(false);
        setMiddleOnScreenWithDialog(dialog);
        dialog.setVisible(true);
    }

    public void newJDialog() {
        dialog = new JDialog();
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setTitle("批量提交辅制工艺任务");
        dialog.setIconImage(frame.getIconImage());
        dialog.setSize(1600, 800);
    }

    private void initComponents() {
        centerPanel = new JPanel(new BorderLayout()); // 设置BorderLayout
        scrollPane = new JScrollPane();
        table = new JTable();
        bottomPanel = new JPanel();
        sure = new JButton();
        cancel = new JButton();
        selectall = new JButton();
        noselect = new JButton();

        table.setModel(new DefaultTableModel(new Object[][]{},
                new String[]{"选择", "零件编号", "零件名称", "主工艺编号", "主工艺名称", "工艺员", "任务类型", "计划完成时间", "材料定额计划完成时间", "任务说明", "element"}) {
            private static final long serialVersionUID = 1L;
            Class[] types = new Class[]{Boolean.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
                    String.class, String.class, Element.class};
            boolean[] canEdit = new boolean[]{true, false, false, false, false, true, true, true, true, true, false};

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        table.getColumnModel().getColumn(5).setCellEditor(new ProcessUserCellEditor());

        renwuTypeComboBox = new JComboBox();
        renwuTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String value = (String) renwuTypeComboBox.getSelectedItem();
                if(value == null || "".equals(value)) {
                    return;
                }
                int row = table.getSelectedRow();
                if(row > -1) {
                    boolean currentSelected = (Boolean) table.getValueAt(row, 0);
                    if(currentSelected) {
                        for(int n = 0; n < table.getRowCount(); n++) {
                            boolean b = (Boolean) table.getValueAt(n, 0);
                            if(b) {
                                table.setValueAt(value, n, 6);
                            }
                        }
                    }
                }
            }
        });
        renwuTypeComboBox.setModel(new DefaultComboBoxModel(taskType));
        table.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(renwuTypeComboBox));
        planTimeField = new JTextField();
        planTimeField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String value = planTimeField.getText();
                if(value == null) {
                    return;
                }
                int row = table.getSelectedRow();
                if(row > -1) {
                    boolean currentSelected = (Boolean) table.getValueAt(row, 0);
                    if(currentSelected) {
                        for(int n = 0; n < table.getRowCount(); n++) {
                            boolean b = (Boolean) table.getValueAt(n, 0);
                            if(b) {
                                table.setValueAt(value, n, 7);
                            }
                        }
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String value = planTimeField.getText();
                if(value == null) {
                    return;
                }
                int row = table.getSelectedRow();
                if(row > -1) {
                    boolean currentSelected = (Boolean) table.getValueAt(row, 0);
                    if(currentSelected) {
                        for(int n = 0; n < table.getRowCount(); n++) {
                            boolean b = (Boolean) table.getValueAt(n, 0);
                            if(b) {
                                table.setValueAt(value, n, 7);
                            }
                        }
                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        DateChooser dateChooser = DateChooser.getInstance("yyyy/MM/dd");
        dateChooser.register(planTimeField);
        table.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(planTimeField));
        cldePlanTimeField = new JTextField();
        cldePlanTimeField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String value = cldePlanTimeField.getText();
                if(value == null) {
                    return;
                }
                int row = table.getSelectedRow();
                if(row > -1) {
                    boolean currentSelected = (Boolean) table.getValueAt(row, 0);
                    if(currentSelected) {
                        for(int n = 0; n < table.getRowCount(); n++) {
                            boolean b = (Boolean) table.getValueAt(n, 0);
                            if(b) {
                                table.setValueAt(value, n, 8);
                            }
                        }
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String value = cldePlanTimeField.getText();
                if(value == null) {
                    return;
                }
                int row = table.getSelectedRow();
                if(row > -1) {
                    boolean currentSelected = (Boolean) table.getValueAt(row, 0);
                    if(currentSelected) {
                        for(int n = 0; n < table.getRowCount(); n++) {
                            boolean b = (Boolean) table.getValueAt(n, 0);
                            if(b) {
                                table.setValueAt(value, n, 8);
                            }
                        }
                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        DateChooser dateChooser2 = DateChooser.getInstance("yyyy/MM/dd");
        dateChooser2.register(cldePlanTimeField);
        cldePlanTimeField.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    cldePlanTimeField.setText("");
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        table.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(cldePlanTimeField));
        taskDescField = new JTextField();
        taskDescField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String value = taskDescField.getText();
                if(value == null) {
                    return;
                }
                int row = table.getSelectedRow();
                if(row > -1) {
                    boolean currentSelected = (Boolean) table.getValueAt(row, 0);
                    if(currentSelected) {
                        for(int n = 0; n < table.getRowCount(); n++) {
                            boolean b = (Boolean) table.getValueAt(n, 0);
                            if(b) {
                                table.setValueAt(value, n, 9);
                            }
                        }
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String value = taskDescField.getText();
                if(value == null) {
                    return;
                }
                int row = table.getSelectedRow();
                if(row > -1) {
                    boolean currentSelected = (Boolean) table.getValueAt(row, 0);
                    if(currentSelected) {
                        for(int n = 0; n < table.getRowCount(); n++) {
                            boolean b = (Boolean) table.getValueAt(n, 0);
                            if(b) {
                                table.setValueAt(value, n, 9);
                            }
                        }
                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        table.getColumnModel().getColumn(9).setCellEditor(new DefaultCellEditor(taskDescField));

        CommonUIUtil.hiddenCell(table, 10);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(25);
        scrollPane.setViewportView(table);
        table.getColumnModel().getColumn(0).setMinWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(3).setPreferredWidth(240);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        centerPanel.add(scrollPane);
        bottomPanel.add(sure);
        bottomPanel.add(cancel);
        bottomPanel.add(selectall);
        bottomPanel.add(noselect);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setContentPane(mainPanel);

        // 添加底部面板最大高度限制
        bottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); // 设置最大高度

        sure.setText("确  定");
        sure.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                sure(evt);
            }
        });

        cancel.setText("取  消");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancel(evt);
            }
        });

        selectall.setText("全  选");
        selectall.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                selectall(evt);
            }
        });

        noselect.setText("取消选择");
        noselect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                noselect(evt);
            }
        });
    }

    private void loadData() {
        try {
            if(node != null) {
                List<Element> techElementList = getAllZhuZhiProcessPlan();
                if(techElementList != null && techElementList.size() > 0) {
                    DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                    for(Element element : techElementList) {
                        setTableValues(element, tableModel);
                    }
                }
            }
        } catch(InvocationTargetException e) {
            e.printStackTrace();
        } catch(RemoteException e) {
            e.printStackTrace();
        }
    }

    private List<Element> getAllZhuZhiProcessPlan() throws InvocationTargetException, RemoteException {
        String partOid = node.getObject().getTreeCellData().attributeValue("oid");
        return TechnicsIntf.getAllZhuZhiProcessPlan(partOid, NewTechnicsPart.currentUser);
    }

    private void setTableValues(Element ele, DefaultTableModel tableModel) {
        Vector vector = new Vector();
        for(int j = 0; j < table.getColumnCount(); j++) {
            if(j == 0) {
                vector.add(false);
            } else {
                vector.add("");
            }

        }
        tableModel.addRow(vector);

        tableModel.setValueAt(ele.attributeValue("partNumber"), table.getRowCount() - 1, 1);
        tableModel.setValueAt(ele.attributeValue("partName"), table.getRowCount() - 1, 2);
        tableModel.setValueAt(ele.attributeValue("pplanNumber"), table.getRowCount() - 1, 3);
        tableModel.setValueAt(ele.attributeValue("pplanName"), table.getRowCount() - 1, 4);
        tableModel.setValueAt("工艺设计任务", table.getRowCount() - 1, 6);
        tableModel.setValueAt(DateUtil.getTodayDate(), table.getRowCount() - 1, 7);
        tableModel.setValueAt(ele, table.getRowCount() - 1, 10);
    }

    private void sure(ActionEvent evt) {
        Map<String, Map<String, String>> allMap = new HashMap<String, Map<String, String>>();

        int rows = table.getRowCount();
        if(rows > 0) {
            for(int n = 0; n < rows; n++) {
                boolean b = (Boolean) table.getValueAt(n, 0);
                if(b) {
                    String name = table.getValueAt(n, 3).toString();
                    String fzcj = table.getValueAt(n, 5).toString();
                    String taskType = table.getValueAt(n, 6).toString();
                    String planTime = table.getValueAt(n, 7).toString();
                    String cldePlanTime = table.getValueAt(n, 8).toString();
                    Element techElement = (Element) table.getValueAt(n, 10);
                    if(planTime == null || "".equals(planTime)) {
                        JOptionPane.showMessageDialog(dialog, name + "计划完成时间不能为空！");
                        return;
                    } else if(fzcj == null || "".equals(fzcj)) {
                        JOptionPane.showMessageDialog(dialog, name + "工艺员不能为空！");
                        return;
                    } else if(taskType == null || "".equals(taskType)) {
                        JOptionPane.showMessageDialog(dialog, "任务类型不能为空！");
                        return;
                    } else {
                        String taskDescribe = table.getValueAt(n, 9).toString();
                        planTime = planTime.replaceAll("/", "-") + " " + "00:00:00";
                        if(cldePlanTime != null && !cldePlanTime.isEmpty()) {
                            cldePlanTime = cldePlanTime.replaceAll("/", "-") + " " + "00:00:00";
                        }
                        String technicsNumber = techElement.attributeValue("technicsNumber");
                        String pplanNumber = techElement.attributeValue("pplanNumber");
                        String technicsName = techElement.attributeValue("technicsName");
                        String partNumber = techElement.attributeValue("partNumber");
                        String partVersion = techElement.attributeValue("partVersion");
                        String oid = null;

                        oid = (String) IntfUtil.getPeRemoteMethodInvoke("getProcessTaskItemOidByPartNumber",
                                new Class[]{String.class, String.class, String.class}, new Object[]{partNumber, "工艺编制", partVersion});
                        if(oid == null) {
                            oid = (String) IntfUtil.getPeRemoteMethodInvoke("getProcessTaskItemOidByPartNumber",
                                    new Class[]{String.class, String.class, String.class}, new Object[]{partNumber, "临时工艺任务", partVersion});
                        }
                        if(NewTechnicsPart.workItemOid != null && oid == null) {
                            String taskItemName = (String) IntfUtil.getPeRemoteMethodInvoke("getTaskItemName",
                                    new Class[]{String.class}, new Object[]{NewTechnicsPart.workItemOid});
                            if(taskItemName != null && ("工艺编制".equals(taskItemName) || "临时工艺任务".equals(taskItemName))) {
                                oid = (String) IntfUtil.getPeRemoteMethodInvoke("getProcessTaskItemOidByPartNumber",
                                        new Class[]{String.class, String.class, String.class}, new Object[]{partNumber, taskItemName, partVersion});
                            }
                        }

                        if(oid == null) {
                            //JOptionPane.showMessageDialog(dialog,"找不到相应的工艺任务！请到对应的工艺任务启动工艺编辑器后再提交。");
                            //return;
                        }
                        Map<String, String> map = new HashMap<String, String>();

                        map.put("technicsNumber", technicsNumber);
                        map.put("pplanNumber", pplanNumber);
                        map.put("technicsName", technicsName);
                        map.put("FZCJ", fzcj);
                        map.put("taskType", taskType);
                        map.put("JHWCSJ", planTime);
                        map.put("cldePlanTime", cldePlanTime);
                        map.put("taskDescribe", taskDescribe);
                        map.put("partVersion", partVersion);
                        map.put("ZGYOID", oid);
                        System.out.println("------map----" + map);
                        allMap.put(pplanNumber, map);
                    }
                }
            }
        }
        if(allMap.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "请选择需要提交辅制工艺任务的工艺文件！");
        } else {
            List<String> list = new ArrayList<>();
            try {
                list = TechnicsIntf.createTaskItemBatch(allMap);
            } catch(RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch(InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(list.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "批量提交辅制工艺编制任务成功！");
            } else {
                String name = "";
                for(String pplanNumber : list) {
                    name += pplanNumber + "、";
                }
                name = name.substring(0, name.length() - 1);
                JOptionPane.showMessageDialog(dialog, "批量提交辅制工艺编制任务失败，请联系管理员！\n失败的工艺文件有：" + name);
            }
            this.dialog.setVisible(false);
        }
    }

    private void cancel(ActionEvent evt) {
        dialog.setVisible(false);
    }

    private void selectall(ActionEvent evt) {
        int rows = table.getRowCount();
        if(rows > 0) {
            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
            for(int n = 0; n < rows; n++) {
                tableModel.setValueAt(true, n, 0);
            }
        }
    }

    private void noselect(ActionEvent evt) {
        int rows = table.getRowCount();
        if(rows > 0) {
            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
            for(int n = 0; n < rows; n++) {
                tableModel.setValueAt(false, n, 0);
            }
        }
    }

    public static void setMiddleOnScreenWithDialog(JDialog dialog) {
        int windowWidth = dialog.getWidth();                     //获得窗口宽
        int windowHeight = dialog.getHeight();                   //获得窗口高
        Toolkit kit = Toolkit.getDefaultToolkit();              //定义工具包
        Dimension screenSize = kit.getScreenSize();             //获取屏幕的尺寸
        int screenWidth = screenSize.width;                     //获取屏幕的宽
        int screenHeight = screenSize.height;                   //获取屏幕的高
        dialog.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);//设置窗口居中显示
    }

    /**
     * 自定义单元格编辑器，用于工艺员列，支持文本框和搜索按钮
     */
    private class ProcessUserCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JTextField textField;
        private JButton searchButton;
        private String currentValue;
        private int currentRow;

        public ProcessUserCellEditor() {
            panel = new JPanel(new BorderLayout());
            textField = new JTextField();
            textField.setEditable(false); // 设置为不可编辑，只能通过搜索按钮选择
            searchButton = new JButton("搜索");

            panel.add(textField, BorderLayout.CENTER);
            panel.add(searchButton, BorderLayout.EAST);

            // 添加文本变化监听
            textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    handleTextChange();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    handleTextChange();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    handleTextChange();
                }

                private void handleTextChange() {
                    String value = textField.getText();
                    if(value == null || value.equals(currentValue)) {
                        return;
                    }

                    // 检查当前行是否被选中
                    boolean currentSelected = (Boolean) table.getValueAt(currentRow, 0);

                    if(currentSelected) {
                        // 如果当前行被选中，批量设置所有选中行的工艺员
                        for(int n = 0; n < table.getRowCount(); n++) {
                            boolean b = (Boolean) table.getValueAt(n, 0);
                            if(b) {
                                table.setValueAt(value, n, 5);
                            }
                        }
                    } else {
                        table.setValueAt(value, currentRow, 5);
                    }
                    currentValue = value;
                }
            });

            searchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 弹出多选用户对话框
                    new SearchTaskUserDialog(frame, textField);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentValue = value == null ? "" : value.toString();
            currentRow = row;
            textField.setText(currentValue);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return textField.getText();
        }

        @Override
        public boolean stopCellEditing() {
            fireEditingStopped();
            return true;
        }

        @Override
        public void cancelCellEditing() {
            fireEditingCanceled();
        }
    }

}

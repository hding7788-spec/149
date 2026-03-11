package com.glaway.mpm.view;

import com.glaway.mpm.EditorConfig;
import com.glaway.mpm.model.UploadTechnics;
import com.glaway.mpm.util.*;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import ext.casc.process.ProcessTaskItem;
import ext.casc.sop.util.StringUtil;
import org.dom4j.Element;
import wt.fc.ReferenceFactory;
import wt.util.WTException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.List;

/**
 * 用户批量提交三级工艺签审或五级工艺签审
 * 入口在PBOM的右键菜单
 *
 * @author cjh
 */
public class BatchSignedTaskSubmitJDialog {

    private static VaLogger logger = VaLogger.getLogger(BatchSignedTaskSubmitJDialog.class);

    private NewTechnicsPart frame;
    private JDialog dialog;
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private JScrollPane scrollPane;
    private JTable table;
    private JButton sure;
    private JButton cancel;
    private JButton selectall;
    private JButton noselect;
    private XWTreeNode node;
    private String signedType;
    private Boolean isSanji = false;
    private Map<String, Element> map;
    private JComboBox comboBox;
    private Map<String, String> taskMap = new HashMap<String, String>();
    private int lastSelect = -1;

    public BatchSignedTaskSubmitJDialog(NewTechnicsPart frame, XWTreeNode node, String signedType) {
        super();
        this.frame = frame;
        this.node = node;
        this.signedType = signedType;
        if("3".equals(signedType)) {
            isSanji = true;
        }
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
        if(isSanji) {
            dialog.setTitle("批量提交三级工艺任务");
        } else {
            dialog.setTitle("批量提交五级工艺任务");
        }
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

        table.setModel(new DefaultTableModel(
                new Object[][]{},
                new String[]{"选择", "零件编号", "工艺文件编号", "工艺文件名称", "工艺文件状态", "工艺任务", "partOid", "docNumber", "taskOid", "docVersion", "element"}
        ) {
            private static final long serialVersionUID = 1L;
            Class[] types = new Class[]{
                    Boolean.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, Element.class
            };
            boolean[] canEdit = new boolean[]{true, false, false, false, false, true, false, false, false, false, false};

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        comboBox = new JComboBox();
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTask = (String) comboBox.getSelectedItem();
                if(selectedTask == null) {
                    return;
                }
                String taskid = taskMap.get(selectedTask);
                int row = table.getSelectedRow();
                if(row != -1 && lastSelect != row) {
                    table.setValueAt(taskid, row, 8);
                }
                lastSelect = row;
                table.clearSelection();
            }
        });
        table.getColumnModel().getColumn(5).setCellEditor(new SignedSubmitTableCellEditor(comboBox));

        CommonUIUtil.hiddenCell(table, 6);
        CommonUIUtil.hiddenCell(table, 7);
        CommonUIUtil.hiddenCell(table, 8);
        CommonUIUtil.hiddenCell(table, 9);
        CommonUIUtil.hiddenCell(table, 10);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(25);
        scrollPane.setViewportView(table);
        table.getColumnModel().getColumn(0).setMinWidth(40);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(5).setMinWidth(500);
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
                List<Element> techElementList = getAllSignedProcessPlan();
                if(techElementList != null && techElementList.size() > 0) {
                    map = new HashMap<String, Element>();
                    for(Element techElement : techElementList) {
                        String technicNumber = techElement.attributeValue("technicsNumber");
                        map.put(technicNumber, techElement);
                    }
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

    private List<Element> getAllSignedProcessPlan() throws InvocationTargetException, RemoteException {
        String partOid = node.getObject().getTreeCellData().attributeValue("oid");
        return TechnicsIntf.getAllInWorkProcessPlan(partOid, NewTechnicsPart.currentUser, signedType);
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

        String partNumber = ele.attributeValue("partNumber");
        String technicsNumber = ele.attributeValue("technicsNumber");
        String version = ele.attributeValue("version");
        tableModel.setValueAt(partNumber, table.getRowCount() - 1, 1);
        tableModel.setValueAt(ele.attributeValue("pplanNumber"), table.getRowCount() - 1, 2);
        tableModel.setValueAt(ele.attributeValue("pplanName"), table.getRowCount() - 1, 3);
        tableModel.setValueAt(ele.attributeValue("lifecycle"), table.getRowCount() - 1, 4);
        tableModel.setValueAt(ele.attributeValue("partOid"), table.getRowCount() - 1, 6);
        tableModel.setValueAt(technicsNumber, table.getRowCount() - 1, 7);
        tableModel.setValueAt(version, table.getRowCount() - 1, 9);
        tableModel.setValueAt(ele, table.getRowCount() - 1, 10);

        if(!version.contains("space")) {
            return;
        }

        String user = NewTechnicsPart.currentUser;
        try {
            String style = "common";
            String qname = ele.getName();
            if("QMFawTechnicsInfo".equals(qname)) {
                List<String> resultList = (List) IntfUtil.getPeRemoteMethodInvoke("getProcessTaskItemByPartNumber2",
                        new Class[]{String.class, String.class, String.class, String.class}, new Object[]{technicsNumber, partNumber, user, style});
                if(resultList != null && resultList.size() == 1) {
                    String result = resultList.get(0);
                    String[] resultstr = result.split("@@");
                    tableModel.setValueAt(resultstr[0], table.getRowCount() - 1, 5);
                    tableModel.setValueAt(resultstr[1], table.getRowCount() - 1, 8);
                }
            } else if("XWReportTechnicsInfo".equals(qname)) {
                style = "report";
                List<ProcessTaskItem> list = (List) IntfUtil.getPeRemoteMethodInvoke("getProcessTaskItemByPartNumber",
                        new Class[]{String.class, String.class, String.class}, new Object[]{partNumber, user, style});
                if(list != null && list.size() == 1) {
                    String name = list.get(0).getNumber() + "  " + list.get(0).getTaskItemName();
                    ReferenceFactory refefence = new ReferenceFactory();
                    String workItemOid = refefence.getReferenceString(list.get(0));
                    tableModel.setValueAt(name, table.getRowCount() - 1, 5);
                    tableModel.setValueAt(workItemOid, table.getRowCount() - 1, 8);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void sure(ActionEvent evt) {
        Set<String> editNumbers = new HashSet<>();
        if((NewTechnicsPart.editTechnics != null) && (NewTechnicsPart.editTechnics.size() > 0)) {
            for(UploadTechnics technic : NewTechnicsPart.editTechnics) {
                String technicsNumber = technic.getTechnicsNumber();
                editNumbers.add(technicsNumber);
            }
        }

        Set<String> taskOids = new HashSet<>();
        XWTreeNode node = frame.xwPartTreePanel.getSelectedTreeNode();
        XWTreeObject xo = null;
        if(node != null) {
            xo = node.getObject();
        }
        int rows = table.getRowCount();
        if(rows > 0) {
            boolean isHasSelected = false;
            for(int n = 0; n < rows; n++) {
                boolean b = (Boolean) table.getValueAt(n, 0);
                if(b) {
                    isHasSelected = true;
                    String taskOid = String.valueOf(table.getValueAt(n, 8));
                    String pplanNumber = String.valueOf(table.getValueAt(n, 2));
                    if(taskOid == null || "".equals(taskOid) || "null".equals(taskOid)) {
                        JOptionPane.showMessageDialog(dialog, "请为工艺" + pplanNumber + "选择任务提交！", "提示", 1);
                        return;
                    } else if(taskOids.contains(taskOid)) {
                        JOptionPane.showMessageDialog(dialog, "所选任务重复，请为工艺选择不同的任务提交！", "提示", 1);
                        return;
                    } else {
                        taskOids.add(taskOid);
                    }
                    String technicsNumber = String.valueOf(table.getValueAt(n, 7));
                    Element technicsElement = map.get(technicsNumber);

                    if(editNumbers.contains(technicsNumber)) {
                        JOptionPane.showMessageDialog(dialog, "工艺" + pplanNumber + "还未上载，请上载后再批量提交工艺签审！", "提示", 1);
                        return;
                    }

                    if(!NewTechnicsPart.checkBeforeSubmit(technicsElement, dialog)) {
                        return;
                    }

                    String ecnNo = (String) IntfUtil.getPeRemoteMethodInvoke("getChangeNoByTechnics", new Class[]{String.class}, new Object[]{technicsNumber});
                    if(ecnNo != null && !"".equals(ecnNo)) {
                        JOptionPane.showMessageDialog(dialog, pplanNumber + "已经提交更改签审任务不允许再提交签审！", "提示", 1);
                        return;
                    }

                    String qname = technicsElement.getName();
                    if("QMFawTechnicsInfo".equals(qname)) {
                        if(xo != null) {
                            if(!NewTechnicsPart.checkCanZhuang4Technics(xo, technicsElement, dialog)) {
                                return;
                            }
                        }
                    } else if("XWReportTechnicsInfo".equals(qname)) {
                        //报表类工艺文件
                        String technicsType = technicsElement.attributeValue("technicsType");
                        if("工艺文件目录".equals(technicsType)) {
                            if(!NewTechnicsPart.check4Gongyimulu(technicsElement, frame)) {
                                return;
                            }
                        }
                    }
                }
            }

            if(!isHasSelected) {
                JOptionPane.showMessageDialog(dialog, "请选择需要提交签审的工艺文件！");
                return;
            }

            final VaActionProgressBar progressBar = new VaActionProgressBar(frame, "提交签审", "正在提交签审工艺文件,请等待...", "签审提交中");
            Thread thread = new Thread() {
                public void run() {
                    Map<String, Map<String, Object>> inputMaps = new HashMap<String, Map<String, Object>>();
                    for(int n = 0; n < rows; n++) {
                        boolean b = (Boolean) table.getValueAt(n, 0);
                        if(b) {
                            String taskOid = String.valueOf(table.getValueAt(n, 8));
                            taskOid = taskOid.split("ext.casc.process.ProcessTaskItem:")[1];

                            String pplanNumber = String.valueOf(table.getValueAt(n, 2));
                            String technicsNumber = String.valueOf(table.getValueAt(n, 7));
                            Element technicsElement = map.get(technicsNumber);
                            String isSanJiGengGai = "false";

                            String ecnNo = (String) IntfUtil.getPeRemoteMethodInvoke("getChangeNoByTechnics", new Class[]{String.class}, new Object[]{technicsNumber});
                            if(StringUtil.isEmpty(ecnNo)) {
                                if("3".equals(signedType)) {
                                    if(NewTechnicsPart.isSanJiGengGai(technicsElement)) {
                                        isSanJiGengGai = "true";
                                    }
                                }
                            }

                            logger.debug("开始校验工艺文件" + pplanNumber + "...");
                            long startTime = System.currentTimeMillis();
                            // 校验工艺文件
                            boolean checkOk = true;
                            try {
                                checkOk = NewTechnicsPart.check(technicsElement, progressBar, frame, dialog);
                            } catch(RemoteException e3) {
                                e3.printStackTrace();
                            } catch(WTException e3) {
                                e3.printStackTrace();
                            } catch(InvocationTargetException e3) {
                                e3.printStackTrace();
                            }
                            long endTime = System.currentTimeMillis();
                            logger.debug("校验工艺文件" + pplanNumber + "耗时：" + (endTime - startTime) + " ms");
                            if(!checkOk) {
                                progressBar.finish();
                                progressBar.setVisible(false);
                                return;
                            }

                            Map<String, Object> inputMap = TechnicsUtil.generateSubmitMap(technicsElement, null);

                            String technicsType = technicsElement.attributeValue("technicsType");
                            String partOid = technicsElement.attributeValue("partOid");
                            inputMap.put("isReport", "false");//false标识一般类工艺
                            inputMap.put("technicsType", technicsType);
                            inputMap.put("technicsNumber", technicsNumber);
                            inputMap.put("partOid", partOid);
                            try {
                                inputMap.put("technicsName", technicsNumber);
                                inputMap.put("flag", signedType);
                                inputMap.put("taskOid", taskOid);
                                inputMap.put("isSanJiGengGai", isSanJiGengGai);
                                inputMap.put("startType", EditorConfig.startType);
                                inputMap.put("pplanNumber", pplanNumber);

                                inputMaps.put(technicsNumber, inputMap);
                            } catch(Exception e1) {
                                e1.printStackTrace();
                                JOptionPane.showMessageDialog(dialog, "工艺规程提交签审出现错误！", "提示", 1);
                                progressBar.setHeaderMessage("工艺规程提交签审出现错误！");
                            }
                        }
                    }

                    if(inputMaps.size() > 0) {
                        progressBar.setHeaderMessage("开始批量提交签审！");
                        HashMap<String, Map<String, String>> returnMap = TechnicsIntf.submitSignedBatch(inputMaps);
                        progressBar.setHeaderMessage("结束批量提交签审！");
                        StringBuilder errorMsg = new StringBuilder();
                        for(String pplanNumber : returnMap.keySet()) {
                            Map<String, String> returnMapItem = (Map<String, String>) returnMap.get(pplanNumber);
                            String success = returnMapItem.get("success");
                            if(!"success".equals(success)) {
                                String errorMessage = returnMapItem.get("errorMessage");
                                if((errorMessage != null) && (!errorMessage.equals(""))) {
                                    errorMsg.append(errorMessage).append("\n");
                                } else {
                                    errorMsg.append("工艺" + pplanNumber + "提交签审出现错误！").append("\n");
                                }
                            }
                        }
                        if(errorMsg.length() > 0) {
                            JOptionPane.showMessageDialog(dialog, errorMsg.toString(), "提示", 1);
                            progressBar.setHeaderMessage("批量提交工艺签审失败！");
                        } else {
                            JOptionPane.showMessageDialog(dialog, "工艺批量提交签审成功,在个人主页我的任务列表中可以收到任务！", "提示", 1);
                            progressBar.setHeaderMessage("工艺提交签审成功！");
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog, "请选择需要提交签审的工艺文件！");
                    }
                    progressBar.finish();
                    progressBar.setVisible(false);
                    dialog.setVisible(false);
                }
            };
            thread.start();
            progressBar.setVisible(true);
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

    class SignedSubmitTableCellEditor extends DefaultCellEditor {

        private static final long serialVersionUID = 1L;
        JComboBox combox = null;

        public SignedSubmitTableCellEditor(JComboBox comboBox) {
            super(comboBox);
            this.combox = comboBox;
            this.setClickCountToStart(1);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            combox.removeAllItems();
            taskMap.clear();
            BatchSignedTaskSubmitJDialog.this.table.clearSelection();
            String partNumber = (String) table.getValueAt(row, 1);
            String docNumber = (String) table.getValueAt(row, 7);
            String docVersion = (String) table.getValueAt(row, 9);
            if(!docVersion.contains("space")) {
                return combox;
            }
            combox.addItem("");
            String user = NewTechnicsPart.currentUser;
            try {
                String style = "common";
                Element element = (Element) table.getValueAt(row, 10);
                String qname = element.getName();
                if("QMFawTechnicsInfo".equals(qname)) {
                    List<String> resultList = (List) IntfUtil.getPeRemoteMethodInvoke("getProcessTaskItemByPartNumber2",
                            new Class[]{String.class, String.class, String.class, String.class}, new Object[]{docNumber, partNumber, user, style});
                    if(resultList != null) {
                        for(int i = 0; i < resultList.size(); i++) {
                            String result = resultList.get(i);
                            String[] resultstr = result.split("@@");
                            taskMap.put(resultstr[0], resultstr[1]);
                            combox.addItem(resultstr[0]);
                        }
                    }
                } else if("XWReportTechnicsInfo".equals(qname)) {
                    style = "report";
                    List<ProcessTaskItem> list = (List) IntfUtil.getPeRemoteMethodInvoke("getProcessTaskItemByPartNumber",
                            new Class[]{String.class, String.class, String.class}, new Object[]{partNumber, user, style});
                    if(list != null) {
                        for(int i = 0; i < list.size(); i++) {
                            String name = list.get(i).getNumber() + "  " + list.get(i).getTaskItemName();
                            ReferenceFactory refefence = new ReferenceFactory();
                            String workItemOid = refefence.getReferenceString(list.get(i));
                            taskMap.put(name, workItemOid);
                            combox.addItem(name);
                        }
                    }
                }
                if(combox.getItemCount() == 2) {
                    combox.setSelectedIndex(1);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            return combox;
        }
    }
}

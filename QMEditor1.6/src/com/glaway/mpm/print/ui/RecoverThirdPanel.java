package com.glaway.mpm.print.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.helper.MPMPrintProcessor;
import com.glaway.mpm.print.service.PrintToWCIntf;
import com.glaway.mpm.print.util.LocalPrintUtil;
import com.glaway.mpm.util.CommonUIUtil;

import ext.casc.util.CommonUtil;

/**
 * @author lkc
 */
public class RecoverThirdPanel extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = 7868553829314100194L;
    private JLabel userLabel;
    private JTextField userTextField;
    //	private JLabel dateLabel;
//	private JTextField dateTextField;
//	private JLabel deptLabel;
//	private JTextField deptTextField;
    private JCheckBox checkAll;
    private JButton completeButton;
    private JButton okButton;
    private JButton loseButton;
    private JButton closeButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel label;
    private JPanel topPanel;
    private JPanel centerPanel;
    private JPanel downPanel;
    private JScrollPane tablePanel;
    private List<String> strList;
    private String infor;
    private String identity = "";
    private String scanInput = "";

    public RecoverThirdPanel(String infor) {
        this.infor = infor;
        initComponents();
        setUIValue();
        initLayout();
        initListener();
        initUI();
    }

    private void setUIValue() {
//		List<String> list = MPMPrintProcessor.queryBarTableID(strList);
        List<CmPrintRecordInfoBean> listBean = MPMPrintProcessor.getQueryRecoverTableIsDelay(getList(infor));
        if (listBean != null && !listBean.isEmpty()) {
            tableModel.setRowCount(0);
            for (CmPrintRecordInfoBean cmPrintRecordQueryBean : listBean) {
                String barCodeTableID = CommonUtil.objectToString(cmPrintRecordQueryBean.getBarTableID());
                String fileNumber = CommonUtil.objectToString(cmPrintRecordQueryBean.getFileNumber());
                String fileName = CommonUtil.objectToString(cmPrintRecordQueryBean.getFileName());
                String fileStatus = CommonUtil.objectToString(cmPrintRecordQueryBean.getMiddleStatus());
                String docVersion = CommonUtil.objectToString(cmPrintRecordQueryBean.getDocVersion());
                String phaseCode = CommonUtil.objectToString(cmPrintRecordQueryBean.getPhaseCode());
                String secret = CommonUtil.objectToString(cmPrintRecordQueryBean.getSecret());
                String getDept = CommonUtil.objectToString(cmPrintRecordQueryBean.getGetDept());
                if(MPMPrintFileFrame.dept != null && !MPMPrintFileFrame.dept.isEmpty() && !getDept.equals(MPMPrintFileFrame.dept)){
                    continue;
                }
                String barCode = CommonUtil.objectToString(cmPrintRecordQueryBean.getBarCode());
                String containerName = CommonUtil.objectToString(cmPrintRecordQueryBean.getContainerName());
                String lifeCycleState = CommonUtil.objectToString(cmPrintRecordQueryBean.getLifeCycleState());
                String recycler = CommonUtil.objectToString(cmPrintRecordQueryBean.getRecycler());
                String recycleTime = CommonUtil.objectToString(cmPrintRecordQueryBean.getRecycleTime());
                String recyclerDept = CommonUtil.objectToString(cmPrintRecordQueryBean.getRecyclerDept());
                String delayDate = CommonUtil.objectToString(cmPrintRecordQueryBean.getDelayDate());
                String[] value = {barCodeTableID, Boolean(false), fileNumber, fileName, fileStatus, docVersion, phaseCode, secret, getDept, barCode, containerName, lifeCycleState, recycler, recycleTime, recyclerDept, delayDate};
                tableModel.addRow(value);
            }
        }
    }

    public void initComponents() {
        topPanel = new JPanel();
        centerPanel = new JPanel();
        downPanel = new JPanel();
        tablePanel = new JScrollPane();
        strList = new ArrayList<String>();
        strList = getList(infor);
        checkAll = new JCheckBox();
        checkAll.setText("全选");
        userLabel = new JLabel("退回人:");
        userTextField = new JTextField();

        label = new JLabel("回收文件列表:");

        completeButton = new JButton("过滤");
        okButton = new JButton("确定回收");
        loseButton = new JButton("文件遗失");
        closeButton = new JButton("关闭");

        String[] tableColumn = {"ID", "", "文件编号", "文件名称", "状态", "版本", "阶段标记", "密级", "领取部门", "条码", "所在产品库", "受控状态", "回退人", "回退时间", "回退部门", "延迟时间"};
        String[][] tableData = {};
        tableModel = new DefaultTableModel(tableData, tableColumn) {

            private static final long serialVersionUID = -7469201343628813334L;

            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 1) {
                    return true;
                }
                return false;
            }

        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer render = (DefaultTableCellRenderer) table.getDefaultRenderer(getClass());
        render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        //table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 20));
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
        table.getColumnModel().getColumn(0).setResizable(false);
        table.getColumnModel().getColumn(1).setCellEditor(table.getDefaultEditor(Boolean.class));
        table.getColumnModel().getColumn(1).setCellRenderer(table.getDefaultRenderer(Boolean.class));
        table.getColumnModel().getColumn(1).setPreferredWidth(30);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(250);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(100);
        table.getColumnModel().getColumn(8).setPreferredWidth(100);

        table.getColumnModel().getColumn(9).setPreferredWidth(200);
        table.getColumnModel().getColumn(10).setPreferredWidth(100);
        table.getColumnModel().getColumn(11).setPreferredWidth(100);
        table.getColumnModel().getColumn(12).setPreferredWidth(100);
        table.getColumnModel().getColumn(13).setPreferredWidth(100);
        table.getColumnModel().getColumn(14).setPreferredWidth(100);


        tableModel.setRowCount(0);
        tablePanel.setViewportView(table);
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        tablePanel.setPreferredSize(new Dimension((int) (width * 0.9), (int) (height * 0.6)));

		/*this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		this.add(Box.createVerticalStrut(0));
		this.add(topPanel);
		this.add(Box.createVerticalStrut(10));
		this.add(centerPanel);
		this.add(Box.createVerticalStrut(10));
		this.add(downPanel);*/

    }

    public void initLayout() {
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints topGrid = new GridBagConstraints();

        topGrid.gridx = 0;
        topGrid.gridy = 0;
        topGrid.weightx = 1;
        topGrid.weighty = 1;
        topGrid.insets = new Insets(20, 10, 5, 15);
        topGrid.anchor = GridBagConstraints.NORTHEAST;
        topPanel.add(userLabel, topGrid);
        topGrid.gridx = 1;
        topGrid.anchor = GridBagConstraints.NORTHWEST;
        topPanel.add(userTextField, topGrid);

        topGrid.gridx = 0;
        topGrid.gridy = 1;
        topGrid.weightx = 1;
        topGrid.weighty = 1;
        topGrid.insets = new Insets(20, 10, 5, 15);
        topGrid.anchor = GridBagConstraints.NORTHWEST;
        topPanel.add(label, topGrid);


        centerPanel.setLayout(new VFlowLayout(0, 0, 0, true, true));
        centerPanel.add(tablePanel);

        downPanel.setLayout(new GridBagLayout());
        GridBagConstraints downGrid = new GridBagConstraints();

        downGrid.gridx = 0;
        downGrid.gridy = 2;
        downGrid.weightx = 1;
        downGrid.weighty = 1;
        downGrid.insets = new Insets(5, 10, 5, 5);
        downGrid.anchor = GridBagConstraints.NORTHWEST;
        downPanel.add(checkAll, downGrid);

        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        downGrid.gridx = 3;
        downGrid.gridy = 2;
        downGrid.weightx = 1;
        downGrid.weighty = 1;
        downGrid.anchor = GridBagConstraints.NORTHWEST;
        downGrid.insets = new Insets(5, (int) (width * 0.6), 5, 10);
        downPanel.add(completeButton, downGrid);

        downGrid.gridx = 4;
        downGrid.gridy = 2;
        downGrid.weightx = 1;
        downGrid.weighty = 1;
        downGrid.anchor = GridBagConstraints.NORTHWEST;
        downGrid.insets = new Insets(5, 5, 5, 5);
        downPanel.add(loseButton, downGrid);

        downGrid.gridx = 5;
        downGrid.gridy = 2;
        downGrid.weightx = 1;
        downGrid.weighty = 1;
        downGrid.anchor = GridBagConstraints.NORTHWEST;
        downGrid.insets = new Insets(5, 5, 5, 5);
        downPanel.add(okButton, downGrid);

        downGrid.gridx = 6;
        downGrid.gridy = 2;
        downGrid.weightx = 1;
        downGrid.weighty = 1;
        downGrid.anchor = GridBagConstraints.NORTHWEST;
        downGrid.insets = new Insets(5, 5, 5, 5);
        downPanel.add(closeButton, downGrid);

        this.add(topPanel);
        this.add(centerPanel);
        this.add(downPanel);
        SpringLayout springLayout = new SpringLayout();
        springLayout.putConstraint(SpringLayout.NORTH, topPanel, 0, SpringLayout.NORTH, this.getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, topPanel, 0, SpringLayout.WEST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, topPanel, 0, SpringLayout.EAST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, centerPanel, 0, SpringLayout.SOUTH, topPanel);
        springLayout.putConstraint(SpringLayout.WEST, centerPanel, 0, SpringLayout.WEST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, centerPanel, 0, SpringLayout.EAST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, downPanel, 0, SpringLayout.SOUTH, centerPanel);
        springLayout.putConstraint(SpringLayout.WEST, downPanel, 0, SpringLayout.WEST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, downPanel, 0, SpringLayout.EAST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.SOUTH, downPanel, 0, SpringLayout.SOUTH, this.getContentPane());

        this.setLayout(springLayout);


        userTextField.setPreferredSize(new Dimension(120, 25));
        label.setPreferredSize(new Dimension(120, 25));
        completeButton.setPreferredSize(new Dimension(100, 25));
        okButton.setPreferredSize(new Dimension(100, 25));
        loseButton.setPreferredSize(new Dimension(100, 25));
        closeButton.setPreferredSize(new Dimension(100, 25));

        if (MPMPrintFileFrame.dept != null && !MPMPrintFileFrame.dept.isEmpty()) {
            userLabel.setVisible(false);
            userTextField.setVisible(false);
            completeButton.setVisible(false);
            loseButton.setVisible(true);
        }else{
            userLabel.setVisible(true);
            userTextField.setVisible(true);
            completeButton.setVisible(true);
            loseButton.setVisible(false);
        }
        if(MPMPrintFileFrame.isComplete){
            okButton.setEnabled(false);
            loseButton.setEnabled(false);
        }else{
            okButton.setEnabled(true);
            loseButton.setEnabled(true);
        }

    }

    public void initListener() {
        //扫码监听
        class keyboardListener implements KeyListener {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    boolean flag = LocalPrintUtil.checkStr(scanInput);//true = 扫文件 ;false = 扫工牌
                    Map<String, String> map = new HashMap<String, String>();
                    if (flag) {//扫文件
                        map = LocalPrintUtil.buildFileMap(scanInput);
                        String QRCode = map.get("DABH");
                        if (QRCode == null || "".equals(QRCode)) {
                            return;
                        }
                        for (int row = 0; row < table.getRowCount(); row++) {
                            String QRName = CommonUtil.objectToString(table.getValueAt(row, 9));
                            if (QRCode.equals(QRName)) {
                                boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                                if (isSelect) {
                                    table.setValueAt(false, row, 1);
                                } else {
                                    table.setValueAt(true, row, 1);
                                }
                            }
                        }
                        userTextField.setText("");
                    } else {//扫工牌
                        String userName = MPMPrintProcessor.getUserNameBySign(scanInput);
                        Boolean flag1 = null;
                        try {
                            flag1 = checkUser(userName, strList);
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        } catch (InvocationTargetException e1) {
                            e1.printStackTrace();
                        }
                        if (flag1) {
                            List<CmPrintRecordInfoBean> listBean = MPMPrintProcessor.queryRecoverValueByUser(strList, userName);
                            if (listBean != null && !listBean.isEmpty()) {
                                tableModel.setRowCount(0);
                                for (CmPrintRecordInfoBean cmPrintRecordQueryBean : listBean) {
                                    String barCodeTableID = cmPrintRecordQueryBean.getBarTableID();
                                    String fileNumber = cmPrintRecordQueryBean.getFileNumber();
                                    String fileName = cmPrintRecordQueryBean.getFileName();
                                    String fileStatus = cmPrintRecordQueryBean.getMiddleStatus();
                                    String docVersion = cmPrintRecordQueryBean.getDocVersion();
                                    String phaseCode = cmPrintRecordQueryBean.getPhaseCode();
                                    String secret = cmPrintRecordQueryBean.getSecret();
                                    String getDept = cmPrintRecordQueryBean.getGetDept();
                                    String barCode = cmPrintRecordQueryBean.getBarCode();
                                    String containerName = CommonUtil.objectToString(cmPrintRecordQueryBean.getContainerName());
                                    String lifeCycleState = CommonUtil.objectToString(cmPrintRecordQueryBean.getLifeCycleState());
                                    String recycler = CommonUtil.objectToString(cmPrintRecordQueryBean.getRecycler());
                                    String recycleTime = CommonUtil.objectToString(cmPrintRecordQueryBean.getRecycleTime());
                                    String recyclerDept = CommonUtil.objectToString(cmPrintRecordQueryBean.getRecyclerDept());
                                    String delayDate = CommonUtil.objectToString(cmPrintRecordQueryBean.getDelayDate());
                                    String[] value = {barCodeTableID, Boolean(false), fileNumber, fileName, fileStatus, docVersion, phaseCode, secret, getDept, barCode, containerName, lifeCycleState, recycler, recycleTime, recyclerDept, delayDate};
                                    tableModel.addRow(value);
                                    identity = userName;
                                }
                            } else {
                                tableModel.setRowCount(0);
                                identity = "";
                            }
                            userTextField.setText(userName);
                        } else {
                            tableModel.setRowCount(0);
                            identity = "";
                            JOptionPane.showMessageDialog(getContentPane(), "当前人员未回收任何文件！");
                        }
                    }
                    scanInput = "";
                } else {
                    String str = CommonUtil.objectToString(e.getKeyChar());
                    String regEx = "[`~!@#$%^&*()+-=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                    Pattern p = Pattern.compile(regEx);
                    Matcher m = p.matcher(str);
                    if (str.matches(".*[a-zA-z].*") || str.matches("^[0-9]*$") || m.find()) {
                        scanInput = scanInput + str;
                    }
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        }
        userTextField.addKeyListener(new keyboardListener());
        table.addKeyListener(new keyboardListener());
        checkAll.addKeyListener(new keyboardListener());
        completeButton.addKeyListener(new keyboardListener());
        okButton.addKeyListener(new keyboardListener());
        loseButton.addKeyListener(new keyboardListener());
        closeButton.addKeyListener(new keyboardListener());
        //扫码监听 end

        checkAll.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                CommonUIUtil.stopTableCellEditing(table);
                if (checkAll.isSelected()) {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        table.getModel().setValueAt(true, i, 1);
                    }
                } else {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        table.getModel().setValueAt(false, i, 1);
                    }
                }
            }
        });

        completeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                CommonUIUtil.stopTableCellEditing(table);
                String userName = CommonUtil.objectToString(userTextField.getText());
                if ("".equals(userName)) {
                    setUIValue();
                    identity = "";
                } else {
                    try {
                        Boolean flag = checkUser(userName, strList);
                        if (flag) {
                            List<CmPrintRecordInfoBean> listBean = MPMPrintProcessor.queryRecoverValueByUser(strList, userName);
                            if (listBean != null && !listBean.isEmpty()) {
                                tableModel.setRowCount(0);
                                for (CmPrintRecordInfoBean cmPrintRecordQueryBean : listBean) {
                                    String barCodeTableID = cmPrintRecordQueryBean.getBarTableID();
                                    String fileNumber = cmPrintRecordQueryBean.getFileNumber();
                                    String fileName = cmPrintRecordQueryBean.getFileName();
                                    String fileStatus = cmPrintRecordQueryBean.getMiddleStatus();
                                    String docVersion = cmPrintRecordQueryBean.getDocVersion();
                                    String phaseCode = cmPrintRecordQueryBean.getPhaseCode();
                                    String secret = cmPrintRecordQueryBean.getSecret();
                                    String getDept = cmPrintRecordQueryBean.getGetDept();
                                    String barCode = cmPrintRecordQueryBean.getBarCode();
                                    String containerName = CommonUtil.objectToString(cmPrintRecordQueryBean.getContainerName());
                                    String lifeCycleState = CommonUtil.objectToString(cmPrintRecordQueryBean.getLifeCycleState());
                                    String recycler = CommonUtil.objectToString(cmPrintRecordQueryBean.getRecycler());
                                    String recycleTime = CommonUtil.objectToString(cmPrintRecordQueryBean.getRecycleTime());
                                    String recyclerDept = CommonUtil.objectToString(cmPrintRecordQueryBean.getRecyclerDept());
                                    String delayDate = CommonUtil.objectToString(cmPrintRecordQueryBean.getDelayDate());
                                    String[] value = {barCodeTableID, Boolean(false), fileNumber, fileName, fileStatus, docVersion, phaseCode, secret, getDept, barCode, containerName, lifeCycleState, recycler, recycleTime, recyclerDept, delayDate};
                                    tableModel.addRow(value);
                                    identity = userName;
                                }
                            } else {
                                tableModel.setRowCount(0);
                                identity = "";
                            }
                        } else {
                            tableModel.setRowCount(0);
                            identity = "";
                            JOptionPane.showMessageDialog(getContentPane(), "当前人员未回收任何文件！");
                        }
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    } catch (InvocationTargetException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean flag = true;
                CommonUIUtil.stopTableCellEditing(table);
                if ("".equals(identity) && (MPMPrintFileFrame.dept == null || MPMPrintFileFrame.dept.isEmpty())) {
                    JOptionPane.showMessageDialog(getContentPane(), "请先确认身份");
                    return;
                }

                //判断是否已经回收 start
                for (int i = 0; i < table.getRowCount(); i++) {
                    boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(i, 1)));
                    if (isSelect) {
                        String state = CommonUtil.objectToString(table.getValueAt(i, 4));
                        if (!"回收中".equals(state) && !"延迟回收".equals(state)) {
                            JOptionPane.showMessageDialog(getContentPane(), "所选文件状态不是“回收中”或者“延迟回收”");
                            return;
                        }
                        List<String> list = new ArrayList<String>();
                        String id = ((String) (table.getModel().getValueAt(i, 0)));
                        list.add(id);
                        if (list != null && list.size() > 0) {
                            try {
                                String dept = getUserDepartment(identity);
                                String date = getCurrentTime();
                                //储存回收数据
                                saveInfoToDB(list, identity, dept, date);
                                tableModel.setValueAt("已回收", i, 4);
                                tableModel.setValueAt(identity, i, 12);
                                tableModel.setValueAt(date, i, 13);
                                tableModel.setValueAt(dept, i, 14);
                                table.updateUI();
                            } catch (RemoteException e1) {
                                flag = false;
                                e1.printStackTrace();
                            } catch (InvocationTargetException e1) {
                                flag = false;
                                e1.printStackTrace();
                            }
                        }
                    }
                }
                if (flag) {
                    JOptionPane.showMessageDialog(getContentPane(), "已确定回收！");
                } else {
                    JOptionPane.showMessageDialog(getContentPane(), "回收失败");
                }
            }
        });

        loseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean flag = true;

                //判断是否已经回收 start
                for (int i = 0; i < table.getRowCount(); i++) {
                    boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(i, 1)));
                    if (isSelect) {
                        String state = CommonUtil.objectToString(table.getValueAt(i, 4));
                        if (!"回收中".equals(state) && !"延迟回收".equals(state)) {
                            JOptionPane.showMessageDialog(getContentPane(), "所选文件状态不是“回收中”或者“延迟回收”");
                            return;
                        }
                        List<String> list = new ArrayList<String>();
                        String id = ((String) (table.getModel().getValueAt(i, 0)));
                        list.add(id);
                        if (list != null && list.size() > 0) {
                            try {
                                String dept = getUserDepartment(identity);
                                String date = getCurrentTime();
                                //储存回收数据
                                saveInfoToDB2(list, identity, dept, date);
                                tableModel.setValueAt("已遗失", i, 4);
                                tableModel.setValueAt(identity, i, 12);
                                tableModel.setValueAt(date, i, 13);
                                tableModel.setValueAt(dept, i, 14);
                                table.updateUI();
                            } catch (RemoteException e1) {
                                flag = false;
                                e1.printStackTrace();
                            } catch (InvocationTargetException e1) {
                                flag = false;
                                e1.printStackTrace();
                            }
                        }
                    }
                }
                if (flag) {
                    JOptionPane.showMessageDialog(getContentPane(), "文件已确定遗失！");
                } else {
                    JOptionPane.showMessageDialog(getContentPane(), "设置遗失失败");
                }
            }
        });

        closeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String str = e.getActionCommand();
                if (str == "关闭") {
                    int result = JOptionPane.showConfirmDialog(getContentPane(), "是否关闭当前页面？", "提示", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                }
            }
        });

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int close = JOptionPane.showConfirmDialog(getContentPane(), "是否关闭当前页面？", "提示", JOptionPane.YES_NO_OPTION);
                if (close == JOptionPane.YES_OPTION) {
                    System.exit(0);
                } else {
                    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }

    public void initUI() {
        this.setTitle("回收文件确认");
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.setSize((int) (width * 0.9), (int) (height * 0.9));
        //this.setLocation(500, 0);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }

    public List<CmPrintRecordInfoBean> getQueryRecoverTableIsDelay(List<String> list) throws RemoteException, InvocationTargetException {
        return PrintToWCIntf.queryRecoverTableIsDelay(list);
    }

    public List<String> queryBarTableID(List<String> list) throws RemoteException, InvocationTargetException {
        return PrintToWCIntf.queryBarTableID(list);
    }

    public CmPrintRecordInfoBean getInfoByBarCode(String barCode) throws RemoteException, InvocationTargetException {
        return PrintToWCIntf.getInfoByBarCode(barCode);
    }

    public CmPrintRecordInfoBean getInfoByUserName(String userName) throws RemoteException, InvocationTargetException {
        return PrintToWCIntf.getInfoByUserName(userName);
    }

    public Boolean checkUser(String userName, List<String> strList) throws RemoteException, InvocationTargetException {
        return PrintToWCIntf.checkUserByRecover(userName, strList);
    }

    public String getUserDepartment(String userName) throws RemoteException, InvocationTargetException {
        return PrintToWCIntf.getUserDepartment(userName);
    }

    public List<String> saveInfoToDB(List<String> strList, String userName, String dept, String date) throws RemoteException, InvocationTargetException {
        return PrintToWCIntf.saveInfoToDB(strList, userName, dept, date);
    }

    public List<String> saveInfoToDB2(List<String> strList, String userName, String dept, String date) throws RemoteException, InvocationTargetException {
        return PrintToWCIntf.saveInfoToDB2(strList, userName, dept, date);
    }

    public String getCurrentTime() {
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String time = sdf.format(nowDate);
        return time;
    }

    public List<String> getTableInfoID(JTable table) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < table.getRowCount(); i++) {
            String id = ((String) (table.getModel().getValueAt(i, 0)));
            list.add(id);
        }
        return list;
    }

    public List<String> getList(String str) {
        List<String> list = new ArrayList<String>();
        String[] temp = str.split(":");
        for (String string : temp) {
            list.add(string);
        }
        return list;
    }

    protected String Boolean(boolean b) {
        return null;
    }
}

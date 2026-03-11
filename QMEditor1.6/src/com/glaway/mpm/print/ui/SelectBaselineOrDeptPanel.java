package com.glaway.mpm.print.ui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.model.data.CmBaseline;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmSealBean;
import com.glaway.mpm.print.helper.MPMPrintHelper;
import com.glaway.mpm.print.helper.MPMPrintProcessor;
import com.glaway.mpm.print.listener.FileListTableModelListener;
import com.glaway.mpm.print.service.PrintToWCIntf;
import com.glaway.mpm.print.util.LoadPrintConfigurations;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.CommonUtil;

public class SelectBaselineOrDeptPanel extends JPanel {

    private static final long serialVersionUID = -3219876314334818021L;
    private JTable table;
    private DefaultTableModel tableModel;
    /**
     * 表头
     */
    private String[] tableColumnName;
    private Class<?>[] tableColumnClass;
    /**
     * 可编辑的列
     */
    private int[] editableColumns = new int[]{};
    private String type;
    private Container component;
    private JTable resourceTable;
    private int column;
//	private JComboBox comboBox;

    /**
     * 定义的静态vector，获取的是制造单位，实际上就是指分发部门
     *
     * @param column
     */
//	private static Vector<String> vec = MPMPrintHelper.getDistributeDept();
    public SelectBaselineOrDeptPanel(Container component, String type, JTable resourceTable, int column) {
        this.type = type;
        this.component = component;
        this.resourceTable = resourceTable;
        this.column = column;
        init();
        initComponent();
    }

    public void loadData() {
        if (column == -1) {
            if (type.equals(PrintConstants.TITLE_ALLDIALOG_DEPT)) {
                setDeptValues("");
            } else if (type.equals(PrintConstants.TITLE_ALLDIALOG_SEAL)) {
                setSealValues("");
            }
        } else {
            int row = resourceTable.getSelectedRow();
            String oid = CommonUtil.objectToString(resourceTable.getValueAt(row, 0));
            String fileType = "";

            if (type.equals(PrintConstants.TITLE_DIALOG_BASELINE)) {
                List<CmBaseline> baselineList = MPMPrintHelper.getBaseline(oid, fileType);
                setBaselineValues(baselineList);
            } else if (type.equals(PrintConstants.TITLE_DIALOG_DEPT)) {
                String dept = CommonUtil.objectToString(resourceTable.getValueAt(row, column));
                setDeptValues(dept);
            } else if (type.equals(PrintConstants.TITLE_DIALOG_SEAL)) {
                String batch = CommonUtil.objectToString(resourceTable.getValueAt(row, column));
                setSealValues(batch);
            }
        }
    }

    private void setSealValues(String batch) {
        ArrayList<CmSealBean> list = new ArrayList<CmSealBean>();
        String[] batchList = null;
        if (batch.contains(",")) {
            batchList = batch.split(",");
        } else {
            batchList = new String[1];
            batchList[0] = batch;
        }
        try {
            list = PrintToWCIntf.selectSeal();
            Object[] value = new Object[3];
            if (list != null && !list.isEmpty()) {
                for (CmSealBean cmSealBean : list) {
                    value[0] = false;
                    for (int i = 0; i < batchList.length; i++) {
                        if (batchList[i].equals(cmSealBean.getName())) {
                            value[0] = true;
                        }
                    }
                    value[1] = cmSealBean.getGwKeyId();
                    value[2] = cmSealBean.getName();
                    tableModel.addRow(value);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void addOneRow() {
        if (type.equals(PrintConstants.TITLE_DIALOG_BASELINE)) {

        } else if (type.equals(PrintConstants.TITLE_DIALOG_DEPT)) {
            CommonUIUtil.addOneRow(tableModel);
            int row = table.getRowCount() - 1;
            table.setValueAt(true, row, 0);
            table.setValueAt("", row, 1);
            table.setValueAt("1", row, 2);
        }
    }

    public void removeRow(List<Integer> list) {
        if (type.equals(PrintConstants.TITLE_DIALOG_BASELINE)) {

        } else if (type.equals(PrintConstants.TITLE_DIALOG_DEPT)) {
            for (int i = 0; i < list.size(); i++) {
                tableModel.removeRow(list.get(i) - i);
            }
        }
    }

    private void init() {
        if (type.equals(PrintConstants.TITLE_DIALOG_BASELINE)) {
            tableColumnClass = new Class[]{Long.class, String.class, Boolean.class};
            tableColumnName = new String[]{"oid", "已投放", "打印需要"};
            editableColumns = new int[]{2};
        } else if (type.equals(PrintConstants.TITLE_DIALOG_DEPT)) {
            tableColumnClass = new Class[]{String.class, String.class, String.class};
            tableColumnName = new String[]{"序号", "分发部门", "份数"};
            editableColumns = new int[]{2};
        } else if (type.equals(PrintConstants.TITLE_DIALOG_SEAL)) {
            tableColumnClass = new Class[]{Boolean.class, String.class, String.class};
            tableColumnName = new String[]{"", "序号", "印章"};
            editableColumns = new int[]{0};
        } else if (type.equals(PrintConstants.TITLE_ALLDIALOG_DEPT)) {
            tableColumnClass = new Class[]{String.class, String.class, String.class};
            tableColumnName = new String[]{"序号", "分发部门", "份数"};
            editableColumns = new int[]{2};
        } else if (type.equals(PrintConstants.TITLE_ALLDIALOG_SEAL)) {
            tableColumnClass = new Class[]{Boolean.class, String.class, String.class};
            tableColumnName = new String[]{"", "序号", "印章"};
            editableColumns = new int[]{0};
        }
    }

    private void initComponent() {
        tableModel = new CommonTableModel();
        table = new JTable();
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setModel(tableModel);
        tableModel.addTableModelListener(new FileListTableModelListener(this, type));

        for (String columnName : tableColumnName) {
            tableModel.addColumn(columnName);
        }

        table.setRowHeight(30);
        if (type.equals(PrintConstants.TITLE_DIALOG_BASELINE)) {
            CommonUIUtil.hiddenCell(table, 0);
        } else if (type.equals(PrintConstants.TITLE_DIALOG_DEPT) || type.equals(PrintConstants.TITLE_ALLDIALOG_DEPT)) {
            /*
             * comboBox = new JComboBox(); comboBox.setEditable(true);
             * Vector<String> vec = MPMPrintHelper.getDistributeDept(); for
             * (String dept : vec) { comboBox.addItem(dept); }
             * table.getColumnModel().getColumn(1).setCellEditor(new
             * DefaultCellEditor(comboBox));
             * table.getColumnModel().getColumn(2).setCellEditor(new
             * CommonTextFiledEditor(new JTextField(), true));
             */
            CommonUIUtil.setColumnWidth(table, 0, 35);
            table.getColumnModel().getColumn(2).setCellEditor(new checkEditor(new JTextField()));
        } else if (type.equals(PrintConstants.TITLE_DIALOG_SEAL) || type.equals(PrintConstants.TITLE_ALLDIALOG_SEAL)) {
            CommonUIUtil.setColumnWidth(table, 0, 35);
        }

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(table);
        scrollPane.setPreferredSize(new Dimension(420, 200));
        setLayout(new VFlowLayout(0, 0, 0, true, true));
        add(table.getTableHeader());
        add(scrollPane);
    }

    public void setBaselineValues(List<CmBaseline> baselineList) {
        tableModel.setRowCount(0);
        int selectRow = resourceTable.getSelectedRow();
        String value = CommonUtil.objectToString(resourceTable.getValueAt(selectRow, 9));
        for (CmBaseline baseline : baselineList) {
            CommonUIUtil.addOneRow(tableModel);
            int row = table.getRowCount() - 1;
            table.setValueAt(baseline.getOid(), row, 0);
            table.setValueAt(baseline.getStatus(), row, 1);
            if (value.equals("") || value.contains(baseline.getStatus())) {
                table.setValueAt(true, row, 2);
            } else {
                table.setValueAt(false, row, 2);
            }
        }
    }

    public void setDeptValues(String deptAndCount) {
        Map<String, String> map = new HashMap<String, String>();
        if (!"".equals(deptAndCount)) {
            if (deptAndCount.contains(",")) {
                String[] deptAndCountList = deptAndCount.split(",");
                for (int i = 0; i < deptAndCountList.length; i++) {
                    String dept = deptAndCountList[i].substring(0, deptAndCountList[i].indexOf(":"));
                    String count = deptAndCountList[i].substring(deptAndCountList[i].indexOf(":") + 1, deptAndCountList[i].indexOf("份"));
                    map.put(dept, count);
                }
            } else {
                String dept = deptAndCount.substring(0, deptAndCount.indexOf(":"));
                String count = deptAndCount.substring(deptAndCount.indexOf(":") + 1, deptAndCount.indexOf("份"));
                map.put(dept, count);
            }
        }

        String[] value = new String[3];
//        if (MPMPrintFileFrame.dept != null) {
//            value[0] = 1 + "";
//            value[1] = MPMPrintFileFrame.dept;
//            value[2] = map.get(MPMPrintFileFrame.dept);
//            tableModel.addRow(value);
//        } else {
            String[] fileType = MPMPrintProcessor.getAllDept();
            for (int i = 0; i < fileType.length; i++) {
                value[0] = String.valueOf(i + 1);
                value[1] = fileType[i];
                String count = map.get(fileType[i]);
                if (!"".equals(count)) {
                    value[2] = count;
                }
                tableModel.addRow(value);
            }
//        }
    }

    public JTable getTable() {
        return table;
    }

    public String getType() {
        return type;
    }

    public Container getComponent() {
        return component;
    }

    public JTable getResourceTable() {
        return resourceTable;
    }

    public int getColumn() {
        return column;
    }

    public final static boolean isNumeric(String s) {
        if (s != null && !"".equals(s.trim()))
            return s.matches("^[0-9]*$");
        else
            return false;
    }


    class CommonTableModel extends DefaultTableModel {

        private static final long serialVersionUID = 1L;

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (tableColumnClass != null) {
                return tableColumnClass[columnIndex];
            } else {
                return super.getColumnClass(columnIndex);
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            for (int i : editableColumns) {
                if (i == column) {
                    return true;
                }
            }
            return false;
        }
    }

    class checkEditor extends DefaultCellEditor {
        private static final long serialVersionUID = 1L;
        private JTextField checkField = null;
        private int rowNum;
        private JTable jTable;
        public checkEditor(JTextField textField) {
            super(textField);
            System.out.println("rowNum====" + rowNum);
            this.checkField = textField;
            checkField.addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    int keyChar = e.getKeyChar();
                    if (keyChar >= 47 && keyChar <= 57) {
                        if (keyChar == 47) {
                            e.consume();
                        }
                    } else {
                        e.consume(); //屏蔽掉非法输入
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            System.out.println("value:" + value + ",isSelected:" + isSelected + ",row:" + row + ",column:" + column);
            if(MPMPrintFileFrame.dept != null && !MPMPrintFileFrame.dept.isEmpty()){
                String selectDept = (String) table.getValueAt(row,1);
                if(selectDept.equals(MPMPrintFileFrame.dept)){
                    this.checkField.setEditable(true);
                }else{
                    this.checkField.setEditable(false);
                }
            }
            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }

        public Object getCellEditorValue() {
            String text = this.checkField.getText();

            if (isNumeric(text)) {
                return text;
            }
            return "";
        }
    }
}

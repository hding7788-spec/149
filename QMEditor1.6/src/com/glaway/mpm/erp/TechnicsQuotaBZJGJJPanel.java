package com.glaway.mpm.erp;

import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.view.GwIconButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 *
 */
public class TechnicsQuotaBZJGJJPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private Container parentPanel;
    public AbstractERPDialog parentDialog;

    public JButton jButton1;
    public JButton jButton2;
    public JButton jButton4;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JScrollPane jScrollPane2;

    private JTable jTable;

    public TechnicsQuotaBZJGJJPanel(Container parentPanel,AbstractERPDialog parentDialog) {
        this.parentPanel = parentPanel;
        this.parentDialog = parentDialog;
        initComponents();
        setName("标准件");
    }

    public JTable getTable() {
        return jTable;
    }

    public TableModel getTableModel() {
        return jTable.getModel();
    }

    private void initComponents() {
        jTable = new JTable();
        jPanel1 = new JPanel();
        jButton1 = new GwIconButton("/images/tech_quota_setting.png", "设置");
        jButton2 = new GwIconButton("/images/tech_quota_remove.png", "移除");
        jButton4 = new GwIconButton("/images/part_selected.gif", "全选");
        /*jButton3 = new GwIconButton("/images/tech_quota_edit.png", "修改");
        jButton4 = new GwIconButton("/images/tech_quota_setting.png", "设置分类");*/
        jPanel2 = new JPanel();
        jScrollPane2 = new JScrollPane();
        jPanel1.setVisible(true);
        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout
                .setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jButton1)
                        .addGap(15).addComponent(jButton2).addGap(15).addComponent(jButton4)));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGroup(
                jPanel1Layout
                        .createSequentialGroup()
                        .addGroup(
                                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(jButton1).addComponent(jButton2).addComponent(jButton4))));

        jTable.setModel(new DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "工艺物资条目oid", "选择", "*项目分类", "物资名称", "规格", "标准号", "机械性能等级或硬度", "*数量", "*单位", "材料",
                        "表面处理", "热处理", "生产厂家", "产品形式", "产品等级", "拧板形式", "特殊说明", "是否进口", "设计编码", "物资编码",
                        "物资简称", "编码优选级别", "编码状态", "编码类型", "编码等级"
                }
        ) {
            Class[] types = new Class[]{
                    String.class, Boolean.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class
                    , String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class
                    , String.class, String.class, String.class, String.class, String.class
            };
            boolean[] canEdit = new boolean[]{
                    false, true, true, false, false, false, false, true, true, false,
                    false, false, false, false, false, false, false, false, false, false,
                    false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });


        int tw1 = 60;
        int[] tableColWidth = new int[]{tw1 - 35, tw1, tw1, tw1, tw1, tw1, tw1, tw1, tw1, tw1 + 15
                , tw1, tw1, tw1, tw1, tw1, tw1, tw1 + 15, tw1, tw1, tw1
                , tw1, tw1, tw1, tw1, tw1
        };
        JtableUtil.setColumnWidth(jTable, tableColWidth);
        int[] columnHiddenValue = new int[]{0};
        JtableUtil.setColumnsHidden(jTable, columnHiddenValue);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jTable .getTableHeader().setReorderingAllowed(false);
        TableColumn tableColumn = jTable.getColumn("*项目分类");
        final JComboBox xmflJComboBox = CommonUtil.getXmflJComboBox();
        tableColumn.setCellEditor(new DefaultCellEditor(xmflJComboBox));
        xmflJComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                int stateChange = e.getStateChange();
                if (stateChange == 2) {
                    String selectedItem = (String) xmflJComboBox.getSelectedItem();
                    int rowCount = jTable.getRowCount();
                    for (int i = 0; i < rowCount; i++) {
                        Boolean selected = (Boolean) jTable.getValueAt(i, 1);
                        if (selected) {
                            jTable.setValueAt(selectedItem, i, 2);
                        }
                    }
                }
            }
        });

        TableColumn dWtableColumn = jTable.getColumn("*单位");
        dWtableColumn.setCellEditor(new DefaultCellEditor(CommonUtil.getDWJComboBox()));
        jTable.setPreferredScrollableViewportSize(new Dimension(750, 850));
        jTable.setRowHeight(25);
        this.setLayout(new BorderLayout());
        this.add(jPanel1, BorderLayout.NORTH);
        jPanel2.setLayout(new BorderLayout());
        jPanel2.add(new JScrollPane(jTable), BorderLayout.CENTER);
        this.add(jPanel2, BorderLayout.CENTER);
        /*jButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = jTable.getSelectedRows();
                if (selectedRows.length == 0) {
                    JOptionPane.showMessageDialog(null, "请选择相应数据", "提示", JOptionPane.OK_OPTION);
                    return;
                }

                String[] ids = new String[selectedRows.length];
                int flag = 0;
                for (int m : selectedRows) {
                    String id = (String) jTable.getValueAt(m, 0);
                    ids[flag] = id;
                    flag++;
                }
                for (int m : selectedRows) {
                    String sjbm = (String) jTable.getValueAt(m, 18);
                    if (!"".equals(sjbm) && !"null".equals(sjbm) && sjbm != null) {
                        JOptionPane.showMessageDialog(null, "请选择正确数据设置物资分类", "提示", JOptionPane.OK_OPTION);
                        return;
                    }
                }
                try {
                    SelectWZFLDialog selectWZFLDialog = new SelectWZFLDialog((NewTechnicsPart) parentPanel, "02_标准件");
                    String s = selectWZFLDialog.showDialog();
                    List<String> idList = new ArrayList<>();
                    ErpToWCIntf.updateWZFLData(ids, "标准紧固件", s);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });*/


    }

    class ViewTechnicsTableButtonEditor extends DefaultCellEditor {

        private static final long serialVersionUID = 1L;
        private JCheckBox viewButton;
        private int column = -1;
        private String path = "";
        private JPanel panel2;

        public ViewTechnicsTableButtonEditor() {
            super(new JTextField());
            this.setClickCountToStart(1);
            viewButton = new JCheckBox();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            return viewButton;
        }

    }

    class ViewTechnicsTableButtonRenderer implements TableCellRenderer {

        private JCheckBox viewButton;

        public ViewTechnicsTableButtonRenderer() {
            viewButton = new JCheckBox();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return viewButton;
        }

    }


    class IsKeyRenderer implements TableCellRenderer {
        private JCheckBox box = new JCheckBox();

        public Component getTableCellRendererComponent(JTable table, Object arg1,
                                                       boolean arg2, boolean arg3, int arg4, int arg5) {
            this.box.setSelected(false);
            if ((arg1 != null) && ((arg1 instanceof String))) {
                if (arg1.toString().equalsIgnoreCase("true")) {
                    this.box.setSelected(true);
                }
            }
            return this.box;
        }

        public void setBackground(Color color) {
            this.box.setBackground(color);
        }
    }

    class IsKeyEditor extends DefaultCellEditor {
        JCheckBox checkBox = null;
        ;
        private JTable editingTable = null;
        private int editingRow = -1;
        private int editingColumn = -1;

        IsKeyEditor(JCheckBox box) {
            super(box);
            checkBox = box;
        }

        public Component getTableCellEditorComponent(JTable table,
                                                     Object value, boolean isSelected, int row, int column) {
            editingTable = table;
            editingRow = row;
            editingColumn = column;
            checkBox.setSelected(false);
            if (value != null && value instanceof String) {
                if (value.toString().equalsIgnoreCase("true")) {
                    checkBox.setSelected(true);
                }
            }
            return checkBox;
        }

        public Object getCellEditorValue() {
            boolean bool = checkBox.isSelected();
            return String.valueOf(bool);
        }
    }

    public void setUIEnabled(boolean b) {
        jButton1.setEnabled(b);
    }

}

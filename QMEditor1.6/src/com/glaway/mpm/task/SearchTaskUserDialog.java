package com.glaway.mpm.task;

import com.glaway.mpm.parameter.commonui.CommonTableModel;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.wcIntf.TechnicsIntf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.List;
import java.util.Vector;

public class SearchTaskUserDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private NewTechnicsPart frame = null;
    private JTable table = null;
    private CommonTableModel tableModel = null;
    private JButton searchBtn = null;
    private JButton comfirmBtn = null;
    private JButton cancleBtn = null;
    private JLabel nameLabel = null;
    private JTextField nameField = null;
    private JPanel searchPanel = null;
    private JTextField userField = null;

    public SearchTaskUserDialog(NewTechnicsPart frame, JTextField userField) {
        this.frame = frame;
        this.userField = userField;
        setModal(true);
        initComponent();
        initDialog();
    }

    private void initComponent() {
        JScrollPane tableScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel topBtnPanel = new JPanel();
        JPanel downBtnPanel = new JPanel();
        String[] header = {"用户名", "全名"};
        Class<?>[] colClass = {String.class, String.class};
        tableModel = new CommonTableModel(header, colClass, null);
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        // 为表格添加双击事件监听
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 双击事件
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        performConfirm();
                    }
                }
            }
        });

        JViewport viewport = new JViewport();
        viewport.add(table.getTableHeader());
        tableScrollPane.setColumnHeader(viewport);
        tableScrollPane.setViewportView(table);
        this.add(tableScrollPane, BorderLayout.CENTER);

        nameLabel = new JLabel("用户名：");
        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(200, 25));

        // 为nameField添加回车键监听
        nameField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        searchBtn = new JButton("搜索");
        searchBtn.addActionListener(this);

        searchPanel = new JPanel();
        searchPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.insets = new Insets(5, 5, 0, 0);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        searchPanel.add(nameLabel, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        searchPanel.add(nameField, gridBagConstraints);

        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        searchPanel.add(searchBtn, gridBagConstraints);

        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(20);
        flowLayout.setAlignment(FlowLayout.LEFT);
        topBtnPanel.setLayout(flowLayout);
        topBtnPanel.add(searchPanel);
        FlowLayout layout = new FlowLayout();
        layout.setHgap(20);
        layout.setAlignment(FlowLayout.RIGHT);
        JPanel panel = new JPanel();
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(10, 0, 5, 5);
        c.gridy = 1;
        c.gridx = 1;
        panel.setLayout(new GridBagLayout());
        panel.add(topBtnPanel, c);
        this.add(panel, BorderLayout.NORTH);
        comfirmBtn = new JButton("确定");
        comfirmBtn.addActionListener(this);
        cancleBtn = new JButton("取消");
        cancleBtn.addActionListener(this);
        FlowLayout flowLayout2 = new FlowLayout();
        flowLayout2.setHgap(20);
        flowLayout2.setAlignment(FlowLayout.RIGHT);
        downBtnPanel.setLayout(flowLayout2);
        downBtnPanel.add(comfirmBtn);
        downBtnPanel.add(cancleBtn);
        this.add(downBtnPanel, BorderLayout.SOUTH);
    }

    private void initDialog() {
        setTitle("查询用户");
        setSize(600, 400);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        SwingUtil.setMiddle(this);
        setVisible(true);
    }

    private String convertNull(Object str) {
        return str == null ? "" : str.toString();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(comfirmBtn == e.getSource()) {
            performConfirm();
        } else if(cancleBtn == e.getSource()) {
            this.dispose();
        } else if(searchBtn == e.getSource()) {
            performSearch();
        }
    }

    private void performSearch() {
        tableModel.setRowCount(0);
        String name = nameField.getText().trim();
        if(name.isEmpty()) {
            SwingUtil.showMessageDialog("请输入用户名", "提示", 2);
            return;
        }
        List<Map<String, String>> list = TechnicsIntf.searchUsers(name);
        for(Map<String, String> map : list) {
            Vector<Object> rowData = new Vector<Object>();
            rowData.add(convertNull(map.get("name")));
            rowData.add(convertNull(map.get("fullName")));
            tableModel.addRow(rowData);
        }
    }

    private void performConfirm() {
        int[] selectedRows = table.getSelectedRows();
        if(selectedRows == null || selectedRows.length == 0) {
            SwingUtil.showMessageDialog("请选择用户", "提示", 2);
            return;
        }
        for(int index : selectedRows) {
            String name = (String) tableModel.getValueAt(index, 0);
            userField.setText(name);
        }
        this.dispose();
    }

}

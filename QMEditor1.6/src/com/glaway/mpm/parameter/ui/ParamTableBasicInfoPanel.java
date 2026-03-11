package com.glaway.mpm.parameter.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.mesParameter.helper.MesParameterProcessor;
import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.parameter.model.data.CmParameterTableColumn;
import com.glaway.mpm.parameter.model.tree.XWTreeNode;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.CommonUtil;

public class ParamTableBasicInfoPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -4435106597278957483L;
	private JLabel tableNameLabel;
	private JTextField tableNameTextField;
	private JLabel tableStatusLabel;
	private JComboBox tableStatusComboBox;
	private JButton addButton;
	private JButton frozenButton;
	private JButton recoverButton;
	private JButton moveUpButton;
	private JButton moveDownButton;
	private JButton saveButton;
	private JPanel buttonPanel;
	private CmParamTableType paramTableType;

	public ParamTableBasicInfoPanel() {
		initComponents();
		initLayout();
		initListener();
	}

	private void initComponents() {
		String[] status = new String[] {"启用", "禁用"};

		tableNameLabel = new JLabel("表单模板名称：");
		tableNameTextField = new JTextField();
		tableStatusLabel = new JLabel("启用/禁用：");
		tableStatusComboBox = new JComboBox(status);
		addButton = new JButton("添 加 行");
		frozenButton = new JButton("冻 结 行");
		recoverButton = new JButton("恢 复 行");
		moveUpButton = new JButton("上移");
		moveDownButton = new JButton("下移");
		saveButton = new JButton("保 存");
		buttonPanel = new JPanel();

		tableNameTextField.setPreferredSize(new Dimension(210, 30));
		tableStatusComboBox.setPreferredSize(new Dimension(80, 30));
		addButton.setPreferredSize(new Dimension(80, 25));
		frozenButton.setPreferredSize(new Dimension(80, 25));
		moveUpButton.setPreferredSize(new Dimension(80, 25));
		moveDownButton.setPreferredSize(new Dimension(80, 25));
		saveButton.setPreferredSize(new Dimension(80, 25));
	}

	private void initLayout() {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();

		g.gridx = 0;
		g.gridy = 0;
		g.insets = new Insets(10, 10, 0, 10);
		g.anchor = GridBagConstraints.EAST;
		contentPanel.add(tableNameLabel, g);
		g.gridx = 1;
		g.anchor = GridBagConstraints.WEST;
		contentPanel.add(tableNameTextField, g);
		g.gridx = 2;
		g.anchor = GridBagConstraints.WEST;
		contentPanel.add(tableStatusLabel, g);
		g.gridx = 3;
		g.anchor = GridBagConstraints.WEST;
		contentPanel.add(tableStatusComboBox, g);

		buttonPanel.setLayout(new GridBagLayout());
		GridBagConstraints g1 = new GridBagConstraints();
		g1.gridx = 0;
		g1.gridy = 0;
		g1.insets = new Insets(10, 10, 0, 90);
		buttonPanel.add(addButton, g1);
		g1.gridx =  1;
		buttonPanel.add(frozenButton, g1);
		g1.gridx =  2;
		buttonPanel.add(recoverButton, g1);
		g1.gridx = 3;
		buttonPanel.add(moveUpButton,g1);
		g1.gridx = 4;
		buttonPanel.add(moveDownButton,g1);
		g1.gridx =  5;
		buttonPanel.add(saveButton, g1);

		g.gridx = 0;
		g.gridy = 1;
		g.gridwidth = 4;
		g.insets = new Insets(20, 10, 0, 10);
		contentPanel.add(buttonPanel, g);

		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(contentPanel);
	}

	public void setUIValues(XWTreeNode treeNode) {
		if (treeNode == null)
			return ;

		clear();
		CmParamTableType paramTableType = (CmParamTableType) treeNode.getTreeObject().getTreeNode();
		if (paramTableType != null) {
			if (paramTableType.getName().equals("通用检查项定义")) {
				tableNameTextField.setEditable(false);
			} else {
				tableNameTextField.setEditable(true);
			}
			tableNameTextField.setText(paramTableType.getName());
			tableStatusComboBox.setSelectedItem(paramTableType.getIsUsed());
			this.paramTableType = paramTableType;
		}
	}

	private void clear() {
		tableNameTextField.setText("");
		tableStatusComboBox.setSelectedIndex(0);
	}

	public CmParamTableType getSelectedParamTableType() {
		this.paramTableType.setName(CommonUtil.objectToString(tableNameTextField.getText()));
		this.paramTableType.setIsUsed(CommonUtil.objectToString(tableStatusComboBox.getSelectedItem()));
		return paramTableType;
	}

	private void initListener() {
		addButton.addActionListener(this);
		frozenButton.addActionListener(this);
		recoverButton.addActionListener(this);
		moveUpButton.addActionListener(this);
		moveDownButton.addActionListener(this);
		saveButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		ParamTableTypePanel paramTableTypePanel = MPMParameterMainFrame.getParamTableTypePanel();
		ParamTableDetailInfoPanel detailPanel = paramTableTypePanel.getDetailInfoPanel();
		JTable jTable = detailPanel.getTable();
		if (paramTableTypePanel == null)
			return ;
		JTable table = paramTableTypePanel.getDetailInfoPanel().getTable();
		DefaultTableModel tableModel = paramTableTypePanel.getDetailInfoPanel().getTableModel();
		if (obj == addButton) {
			CommonUIUtil.addOneRow(tableModel);
			int row = table.getRowCount() - 1;
			String enName = "GW" + new Date().getTime();
			table.setValueAt(tableModel.getRowCount(), row, 1);
			table.setValueAt(enName, row, 2);
			table.setValueAt(false, row, 6);
			table.setValueAt(false, row, 7);
			table.setValueAt("正常", row, 9);
			table.setValueAt(false, row, 10);
			table.setValueAt(false, row, 11);
		} else if (obj == frozenButton) {
			int selRow = table.getSelectedRow();
			if (selRow == -1) {
				JOptionPane.showMessageDialog(null, "请先选择一行！");
				return;
			}
			table.setValueAt("冻结", selRow, 9);
		}  else if (obj == recoverButton) {
			int selRow = table.getSelectedRow();
			if (selRow == -1) {
				JOptionPane.showMessageDialog(null, "请先选择一行！");
				return;
			}
			table.setValueAt("正常", selRow, 9);
		}else if(obj == moveUpButton){
			changeRowValue(true, jTable);
		}else if(obj == moveDownButton){
			changeRowValue(false, jTable);

		}else if (obj == saveButton) {
			ParamTableDetailInfoPanel detailInfoPanel = paramTableTypePanel.getDetailInfoPanel();
			ParamTableBasicInfoPanel basicInfoPanel = paramTableTypePanel.getBasicInfoPanel();
			CommonUIUtil.stopTableCellEditing(detailInfoPanel.getTable());
			if (detailInfoPanel.verify()) {
				CmParamTableType paramTableType = basicInfoPanel.getSelectedParamTableType();
				if (paramTableType.getName().equals("")) {
					JOptionPane.showMessageDialog(null, "表单模板名称不能为空！");
					return ;
				}
				paramTableType.setTableColumns(detailInfoPanel.getCmParameterTableColumns());

				for (CmParameterTableColumn column : detailInfoPanel.getCmParameterTableColumns()) {
					System.out.println("columnName======>>>>" + column.getName());
				}

				CmParamTableType newParamTableType = MPMParameterProcessor.saveParamTableType(paramTableType);
				if (newParamTableType == null) {
					JOptionPane.showMessageDialog(null, "保存失败！");
					return ;
				}
				MPMParameterProcessor.refreshTreeNode(newParamTableType == null ? paramTableType : newParamTableType);
				JOptionPane.showMessageDialog(null, "保存成功！");
			}
		}
	}

	private void changeRowValue(boolean up,JTable jTable1) {
		if (jTable1.getSelectedRowCount() > 1)
			return;
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
			if(j == 1){
				continue;
			}
			jTable1.setValueAt(obj2[j], select, j);
			jTable1.setValueAt(obj1[j], neighbor, j);
		}
		jTable1.setRowSelectionInterval(neighbor, neighbor);
	}
}

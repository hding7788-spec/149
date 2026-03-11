package com.glaway.mpm.parameter.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.glaway.mpm.parameter.commonui.CommonTableModel;
import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.parameter.model.data.CmBaiyuParamTable;
import com.glaway.mpm.parameter.model.data.CmBaiyuParamTableColumn;
import com.glaway.mpm.parameter.model.data.CmParameterTableColumn;
import com.glaway.mpm.parameter.model.tree.XWTreeNode;
import com.glaway.mpm.util.CommonUIUtil;

public class BaiyuTableDetailInfoPanel extends JPanel {

	private static final long serialVersionUID = 2974396865547222482L;
	private JTable table;
	private CommonTableModel tableModel;
	private String[] columnName;
	private Class<?>[] columnClass;
	private int[] editableColumn;
	private List<CmParameterTableColumn> defaultTableColumns = new ArrayList<CmParameterTableColumn>();

	private TableRowSorter<DefaultTableModel> sorter;
	
	public BaiyuTableDetailInfoPanel() {
		initComponents();
	}

	private void initComponents() {
		columnClass = new Class[] {String.class, String .class,String.class, String.class, String.class, String.class,String.class, String.class, String.class, String.class, String.class};
		columnName = new String[] {"oid","序号", "ID", "名称", "创建人", "修改人", "创建时间", "最后修改时间", "状态","表单类型","部门"};
		editableColumn = new int[] {};

		tableModel = new CommonTableModel(columnName, columnClass, editableColumn);
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setModel(tableModel);
		table.setRowHeight(30);
		sorter = new TableRowSorter<>(tableModel);
		table.setRowSorter(sorter);
		
		CommonUIUtil.hiddenCell(table, 0);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(table);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    	int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		scrollPane.setPreferredSize(new Dimension(width, height - 320));
		setLayout(new VFlowLayout(0, 0, 0, true, true));
		add(scrollPane);
	}

	public void setUIValues(XWTreeNode treeNode) {
		if (treeNode == null)
			return ;
		tableModel.setRowCount(0);
		CmBaiyuParamTable baiyuParamTable = (CmBaiyuParamTable) treeNode.getTreeObject().getTreeNode();
		if (baiyuParamTable != null) {
			defaultTableColumns.clear();
			List<CmBaiyuParamTableColumn> list = baiyuParamTable.getTableColumns();
			for (CmBaiyuParamTableColumn column : list) {
				CommonUIUtil.addOneRow(tableModel);
				int row = table.getRowCount() - 1;
				table.setValueAt(column.getTableOid(), row, 0);
				table.setValueAt(column.getOrderNo(), row, 1);
				table.setValueAt(column.getTableId(), row, 2);
				table.setValueAt(column.getTableName(), row, 3);
				table.setValueAt(column.getTableCreator(), row, 4);
				table.setValueAt(column.getTableModifier(), row, 5);
				table.setValueAt(column.getTableCreateTime(), row, 6);
				table.setValueAt(column.getTableModifyTime(), row, 7);
				table.setValueAt(column.getIsUsed(), row, 8);
				table.setValueAt(column.getTableType(), row, 9);
				table.setValueAt(column.getDept(), row, 10);
			}
		}
	}

	public JTable getTable() {
		return table;
	}

	public DefaultTableModel getTableModel() {
		return tableModel;
	}

	public String[] getColumnName() {
		return columnName;
	}
}

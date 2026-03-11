package com.glaway.mpm.print.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.util.CommonUIUtil;

public class SearchBaselineResultTablePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	/** 可编辑的列 */
	private static final int[] editableColumns = { 1 };
	private static final Class<?>[] tableColumnClass = { String.class, Boolean.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class };
	protected JScrollPane tableScrollPane;
	protected JTable table;
	protected DefaultTableModel tableModel;

	public SearchBaselineResultTablePanel() {
		initComponent();
		initUI();
	}

	private void initComponent() {
		tableModel = new CommonTableModel();
		table = new JTable(tableModel);
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setGridColor(Color.GRAY);
		table.setRowHeight(25);
		table.getTableHeader().setReorderingAllowed(false);

		tableScrollPane = new JScrollPane(table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		JViewport viewport = new JViewport();
		viewport.add(table.getTableHeader());

		tableScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tableScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		tableScrollPane.setColumnHeader(viewport);


		addModelColumn();

		setLayout(new GridBagLayout());
		add(tableScrollPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
	}

	private void initUI() {
		setBorder(BorderFactory.createTitledBorder("查询结果"));

		CommonUIUtil.setColumnWidth(table, 1, 30);
		CommonUIUtil.setColumnWidth(table, 2, 50);
		CommonUIUtil.setColumnPrefferredWidth(table, 3, 180);
		CommonUIUtil.setColumnPrefferredWidth(table, 4, 180);

		CommonUIUtil.hiddenCell(table, 0);
	}

	public void addModelColumn() {
		tableModel.addColumn("oid");
		tableModel.addColumn("");
		tableModel.addColumn("序号");
		tableModel.addColumn("编号");
		tableModel.addColumn("名称");
		tableModel.addColumn("产品代号");
		tableModel.addColumn("阶段标记");
		tableModel.addColumn("技术状态标识");
		tableModel.addColumn("创建者");
		tableModel.addColumn("上次修改时间");
		tableModel.addColumn("状态");
		tableModel.addColumn("上下文");
	}

	public void setOneRowTableValue(CmPrintInfoBean printInfoBean) {
		String oid = printInfoBean.getOid();
		String number = printInfoBean.getFileNumber();
		String name = printInfoBean.getFileName();
		String phaseCode = printInfoBean.getPhaseCode();
		String lifeCycle = printInfoBean.getLifeCycle();
		String pindex = printInfoBean.getPindex();
		String status = printInfoBean.getTs_status();
		String containerName = printInfoBean.getContainerName();
		String modifyTime = printInfoBean.getModifyTime();
		String creatorName = printInfoBean.getFileCreator();

		int row = tableModel.getRowCount();
		addRows(1);
		tableModel.setValueAt(oid, row, 0);
		tableModel.setValueAt(String.valueOf(row + 1), row, 2);
		tableModel.setValueAt(number, row, 3);
		tableModel.setValueAt(name, row, 4);
		tableModel.setValueAt(pindex, row, 5);
		tableModel.setValueAt(phaseCode, row, 6);
		tableModel.setValueAt(status, row, 7);
		tableModel.setValueAt(creatorName, row, 8);
		tableModel.setValueAt(modifyTime, row, 9);
		tableModel.setValueAt(lifeCycle, row, 10);
		tableModel.setValueAt(containerName, row, 11);
	}

	private void addRows(int rowCount) {
		for (int i = 0; i < rowCount; i++) {
			Vector<Object> vec = new Vector<Object>();
			vec.add("");
			vec.add(false);
			vec.add("");
			vec.add("");
			vec.add("");
			vec.add("");
			vec.add("");
			vec.add("");
			vec.add("");
			vec.add("");
			vec.add("");
			vec.add("");
			tableModel.addRow(vec);
		}
	}

	public void setTableValues(List<CmPrintInfoBean> list) {
		tableModel.setRowCount(0);
		for (CmPrintInfoBean printInfoBean : list) {
			setOneRowTableValue(printInfoBean);
		}
	}

	public JTable getTable() {
		return table;
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
			if (editableColumns != null) {
				for (int i : editableColumns) {
					if (i == column) {
						return true;
					}
				}
			}
			return false;
		}

	}
}

package com.glaway.mpm.view;

import com.glaway.mpm.sop.view.SopFileTableJPanel;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.Observer;
import java.util.Vector;

public abstract class NewLinkJPanel extends JPanel implements Observer {
	private static final long serialVersionUID = 1L;

	protected Container parentPanel;

	class LinkTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column) {
			if (NewLinkJPanel.this instanceof NewMaterialJPanel) {
				if (column == 11 || column == 12 || column == 13||column == 14)
					return true;
			} else if (NewLinkJPanel.this instanceof NewEquipJPanel) {
				 if (column == 6 || column==7)
				return true;
			}else if (NewLinkJPanel.this instanceof NewToolJPanel) {
				if (column == 5||column>=7)
					return true;
			} else if (NewLinkJPanel.this instanceof NewPartJPanel) {
//				if (column == 5)
//					return true;
			} else if (NewLinkJPanel.this instanceof NewKnifeToolPanel) {
				if (column >= 17 ) {
					return true;
				}
			}else if (NewLinkJPanel.this instanceof NewStandardDashboardJPanel) {
				if(column>=6){
					return true;
				}
			}else if(NewLinkJPanel.this instanceof NewUnStandardDashboardJPanel){
				if(column>=6){
					return true;
				}
			}else if(NewLinkJPanel.this instanceof NewMeasureJPanel){
				if(column>=5){
					return true;
				}
			}else if (NewLinkJPanel.this instanceof PaceTablePane) {
				if (column >= 0 && column <= 2) {
					return true;
				}
				if(column == 4 || column == 14||column == 3) {
					return true;
				}
			}else if(NewLinkJPanel.this instanceof SopFileTableJPanel){
				if(column > 4){
					return true;
				}
			}
			return false;
		}
	}

	protected JPanel panel = new JPanel();

	protected DefaultTableModel tableModel = new LinkTableModel();

	protected MyTable table = new MyTable(tableModel);

	protected JButton getMaterialJButton = new IconButton("/images/material_get.gif", "获取辅料");
	protected JButton getPartMaterial = new IconButton("/images/material_get.gif", "获取零件");
	protected JButton addJButton = new IconButton("/images/button_add.png", "添加");
	protected JButton upJButton = new IconButton("/images/button_upmove.png", "上移");
	protected JButton downJButton = new IconButton("/images/button_downmove.png", "下移");

	// protected JButton calQuotaButton = new IconButton(
	// "/images/button_save.png", "保存");

	protected JButton removeJButton = new IconButton("/images/button_remove.png", "移除");
	protected JButton searchJButton = new IconButton("/images/button_tool_search.gif", "搜索");
	protected JButton setNoteJButton = new IconButton("/images/button_tool_search.gif", "设置备注");
	private JButton searchFrockCardButton = new IconButton("", "申请卡");

	protected JScrollPane pane = null;

	public NewLinkJPanel(Container parentPanel) {
		this.parentPanel = parentPanel;
		jbInit();
	}

	public NewLinkJPanel(Container parentPanel, String string) {
		table = new MyTable(tableModel) {
			private static final long serialVersionUID = 1L;

			public String getToolTipText(MouseEvent event) {
				Point p = event.getPoint();
				int row = rowAtPoint(p);
				int column = columnAtPoint(p);
				if (row == -1 || column == -1 || column != 10) {
					return null;
				}
				Object toolTip = table.getValueAt(row, 14);
				return toolTip == null ? null : toolTip.toString();
			}
		};
		this.parentPanel = parentPanel;
		jbInit();
	}

	protected void jbInit() {
		// getMaterialJButton.setMaximumSize(new Dimension(89, 23));
		// getMaterialJButton.setMinimumSize(new Dimension(89, 23));
		// getMaterialJButton.setPreferredSize(new Dimension(89, 23));

		// addJButton.setMaximumSize(new Dimension(89, 23));
		// addJButton.setMinimumSize(new Dimension(89, 23));
		// addJButton.setPreferredSize(new Dimension(89, 23));
		addJButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addProcess();
			}
		});
		// upJButton.setMaximumSize(new Dimension(89, 23));
		// upJButton.setMinimumSize(new Dimension(89, 23));
		// upJButton.setPreferredSize(new Dimension(89, 23));
		upJButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveUpProcess();
			}
		});

		// downJButton.setMaximumSize(new Dimension(89, 23));
		// downJButton.setMinimumSize(new Dimension(89, 23));
		// downJButton.setPreferredSize(new Dimension(89, 23));
		downJButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveDownProcess();
			}
		});

		// calQuotaButton.setMaximumSize(new Dimension(89, 23));
		// calQuotaButton.setMinimumSize(new Dimension(89, 23));
		// calQuotaButton.setPreferredSize(new Dimension(89, 23));
		//
		// removeJButton.setMaximumSize(new Dimension(89, 23));
		// removeJButton.setMinimumSize(new Dimension(89, 23));
		// removeJButton.setPreferredSize(new Dimension(89, 23));
		removeJButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeProcess();
			}
		});

		panel.setLayout(new GridBagLayout());
		panel.add(getMaterialJButton, new GridBagConstraints(1, 0, 1, 1, 1.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(5, 5, 0, 5), 0, 0));
		panel.add(addJButton, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(getPartMaterial, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(upJButton, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(downJButton, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		// panel.add(calQuotaButton, new GridBagConstraints(1, 4, 1, 1, 1.0,
		// 0.0,
		// GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
		// 5, 5, 0, 5), 0, 0));
		panel.add(removeJButton, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		// calQuotaButton.setVisible(false);
		getMaterialJButton.setVisible(false);
		getPartMaterial.setVisible(false);
		searchFrockCardButton.setVisible(false);
		addModelColumn();
		// JTable显示表头
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		// p.add(table.getTableHeader(), BorderLayout.PAGE_START);
		p.add(table, BorderLayout.CENTER);

		table.setGridColor(Color.GRAY);
		pane = new JScrollPane(p,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		JViewport vp = new JViewport();
		vp.add(table.getTableHeader());

		pane.setColumnHeader(vp);

		table.setSelectionMode(2);
		table.setRowHeight(25);
		table.getTableHeader().setReorderingAllowed(false);
		// pane.getViewport().setBackground(Color.white);
		setLayout(new GridBagLayout());
		add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		add(panel, new GridBagConstraints(1, 0, 1, 1, 0, 1.0,
				GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0));

		table.putClientProperty("terminateEditOnFocusLost", true);

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				stopTableCellEditing();
			}
		});

		table.getColumnModel().addColumnModelListener(
				new TableColumnModelListener() {
					public void columnAdded(TableColumnModelEvent e) {
					}

					public void columnMarginChanged(ChangeEvent e) {
						stopTableCellEditing();
					}

					public void columnMoved(TableColumnModelEvent e) {
					}

					public void columnRemoved(TableColumnModelEvent e) {
					}

					public void columnSelectionChanged(ListSelectionEvent e) {
					}
				});
	}

	protected void initOneself() {

	}

	protected void setTabTitle() {

	}

	protected abstract void addModelColumn();

	public abstract Vector<Element> getElements();

	public abstract void setTableValues(Vector<Element> vec);

	public abstract void setOneRowTableValue(Element element);

	protected boolean isRowNull(int row) {
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			if (tableModel.getValueAt(row, i) != null
					&& !tableModel.getValueAt(row, i).equals(""))
				return false;
		}
		return true;
	}

	public void addProcess() {
		// stopTableCellEditing();
		Vector<String> vector = new Vector<String>();
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			vector.add("");
		}
		tableModel.addRow(vector);
		setTabTitle();
	}

	private void changeRowValue(boolean up) {
		if (table.getSelectedRowCount() > 1)
			return;
		int select = table.getSelectedRow();
		if (select < 0 || select >= table.getRowCount())
			return;
		if (up && select == 0)
			return;
		if (!up && select == table.getRowCount() - 1)
			return;
		Object[] obj1 = new Object[table.getColumnCount()];
		Object[] obj2 = new Object[table.getColumnCount()];
		int neighbor;
		if (up)
			neighbor = select - 1;
		else
			neighbor = select + 1;
		for (int i = 0; i < table.getColumnCount(); i++) {
			obj1[i] = table.getValueAt(select, i);
			obj2[i] = table.getValueAt(neighbor, i);
		}
		for (int j = 0; j < table.getColumnCount(); j++) {
			table.setValueAt(obj2[j], select, j);
			table.setValueAt(obj1[j], neighbor, j);
		}
		table.setRowSelectionInterval(neighbor, neighbor);
	}

	private void moveUpProcess() {
		// stopTableCellEditing();
		changeRowValue(true);
	}

	private void moveDownProcess() {
		// stopTableCellEditing();
		changeRowValue(false);
	}

	public void stopTableCellEditing() {
		if (table.getCellEditor() != null)
			table.getCellEditor().stopCellEditing();
		else {
			// System.out.println("KKKKKKKKKKKKKKKKKKKKKKKK");
		}
	}

	protected void removeProcess() {
		// stopTableCellEditing();
		int[] rows = table.getSelectedRows();
		for (int i = 0; i < rows.length; i++) {
			tableModel.removeRow(rows[i]);
			for (int j = i + 1; j < rows.length; j++) {
				rows[j] = rows[j] - 1;
			}
		}
		table.updateUI();
		setTabTitle();
	}

	public void clearTable() {
		tableModel.setRowCount(0);
	}

	protected boolean isHasExistData(DefaultTableModel model, int colIndex,
			String data) {
		if (null == model || model.getRowCount() == 0)
			return false;

		for (int i = 0; i < model.getRowCount(); i++) {
			String str = "";
			if (model.getValueAt(i, colIndex) == null
					|| (str = (((String) (model.getValueAt(i, colIndex)))
							.trim())).equals(""))
				continue;
			if (str.equals(data))
				return true;
		}
		return false;
	}

	public void setHiddenColumn(int columnIndex) {
		if (columnIndex >= 0 && columnIndex < table.getColumnCount()) {
			// 隐藏ID列
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMaxWidth(0);
			table.getTableHeader().getColumnModel().getColumn(columnIndex).setMinWidth(0);
			table.getColumnModel().getColumn(columnIndex).setMaxWidth(0);
			table.getColumnModel().getColumn(columnIndex).setPreferredWidth(0);
			table.getColumnModel().getColumn(columnIndex).setWidth(0);
			table.getColumnModel().getColumn(columnIndex).setMinWidth(0);
		}
	}

	private boolean isEnabled = true;

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setUIEnabled(boolean b) {
		isEnabled = b;
		table.setEnabled(b);
		getMaterialJButton.setEnabled(b);
		getPartMaterial.setEnabled(b);
		addJButton.setEnabled(b);
		upJButton.setEnabled(b);
		downJButton.setEnabled(b);
		// calQuotaButton.setEnabled(b);
		removeJButton.setEnabled(b);
		searchJButton.setEnabled(b);
		setNoteJButton.setEnabled(b);
		searchFrockCardButton.setEnabled(b);

	}

	public JTable getTable() {
		return table;
	}

	public class MyTable extends JTable {
		private static final long serialVersionUID = 1L;

		public void valueChanged(ListSelectionEvent e) {
			super.valueChanged(e);
			changeRow();
		}

		public MyTable(DefaultTableModel tableModel) {
			super(tableModel);
		}
	}

	protected void changeRow() {

	}
}
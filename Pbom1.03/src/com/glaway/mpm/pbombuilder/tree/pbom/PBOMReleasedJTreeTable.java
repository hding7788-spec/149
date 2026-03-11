package com.glaway.mpm.pbombuilder.tree.pbom;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.glaway.mpm.pbom.table.DefaultGroup;
import com.glaway.mpm.pbom.table.GroupableTableHeader;
import com.glaway.mpm.pbombuilder.util.CmUtil;

public class PBOMReleasedJTreeTable extends JTable {
	private static final long serialVersionUID = 1L;
	/** A subclass of JTree. */
	protected TreeTableCellRenderer tree;

	public PBOMReleasedJTreeTable(PBOMReleasedListModel treeTableModel) {
		super();

		// Create the tree. It will be used as a renderer and editor.
		tree = new TreeTableCellRenderer(treeTableModel);

		// Install a tableModel representing the visible rows in the tree.
		super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

		// Force the JTable and JTree to share their row selection models.
		ListToTreeSelectionModelWrapper selectionWrapper = new ListToTreeSelectionModelWrapper();
		tree.setSelectionModel(selectionWrapper);
		setSelectionModel(selectionWrapper.getListSelectionModel());

		// Install the tree editor renderer and editor.
		setDefaultRenderer(TreeTableModel.class, tree);
		setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

		GroupableTableHeader tableHeader = new GroupableTableHeader();
		this.setTableHeader(tableHeader);

		DefaultGroup  group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(0);
        group.setHeaderValue("编号");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(1);
        group.setHeaderValue("图号");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(2);
        group.setHeaderValue("阶段标记");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(3);
        group.setHeaderValue("关重件标记");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(4);
        group.setHeaderValue("零组件生产类型");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(5);
        group.setHeaderValue("工艺路线");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(6);
        group.setHeaderValue("物资编码");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(7);
        group.setHeaderValue("主工艺编号");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(8);
        group.setHeaderValue("主工艺状态");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(9);
        group.setHeaderValue("材料定额状态");
        tableHeader.addGroup(group);

        group = new DefaultGroup();
        group.setRow(0);
        group.setColumn(10);
        group.setHeaderValue("批次号");
        tableHeader.addGroup(group);

		// 在构造函数中，在批次号列之后添加是否借用件列的表头定义
		group = new DefaultGroup();
		group.setRow(0);
		group.setColumn(11);
		group.setHeaderValue("是否借用件");
		tableHeader.addGroup(group);

		TableColumnModel columnModel = this.getColumnModel();
		TableColumn borrowedColumn = columnModel.getColumn(11);
		borrowedColumn.setCellRenderer(new DefaultTableCellRenderer() {
			private final JCheckBox checkBox = new JCheckBox();
			{
				checkBox.setHorizontalAlignment(JCheckBox.CENTER);
				checkBox.setOpaque(true);
			}

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				if (isSelected) {
					checkBox.setBackground(table.getSelectionBackground());
					checkBox.setForeground(table.getSelectionForeground());
				} else {
					checkBox.setBackground(table.getBackground());
					checkBox.setForeground(table.getForeground());
				}
				checkBox.setSelected(value instanceof Boolean && (Boolean)value);
				return checkBox;
			}
		});

		// 2. 创建自定义的单元格编辑器，避免位置跳转
		borrowedColumn.setCellEditor(new IsBroowedPartEditor());

		setShowGrid(true);// 是否有边框线

		// No intercell spacing
		setIntercellSpacing(new Dimension(0, 0));

		// And update the height of the trees row to match that of
		// the table.
		if (tree.getRowHeight() < 1) {
			// Metal looks better like this.
			setRowHeight(18);
		}

	}

	public void updateUI() {
		super.updateUI();
		if (tree != null) {
			tree.updateUI();
		}
		// Use the tree's default foreground and background colors in the
		// table.
		LookAndFeel.installColorsAndFont(this, "Tree.background", "Tree.foreground", "Tree.font");
	}

	public int getEditingRow() {
		return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 : editingRow;
	}

	public void setRowHeight(int rowHeight) {
		super.setRowHeight(rowHeight);
		if (tree != null && tree.getRowHeight() != rowHeight) {
			tree.setRowHeight(getRowHeight());
		}
	}

	public JTree getTree() {
		return tree;
	}

	public class TreeTableCellRenderer extends JTree implements TableCellRenderer {
		/** Last table/tree row asked to renderer. */
		protected int visibleRow;

		public TreeTableCellRenderer(TreeModel model) {
			super(model);
			setCellRenderer(new PBOMReleasedTreeTableCellRenderer());
		}

		/**
		 * updateUI is overridden to set the colors of the Tree's renderer to
		 * match that of the table.
		 */
		public void updateUI() {
			super.updateUI();
			// Make the tree's cell renderer use the table's cell selection
			// colors.
			TreeCellRenderer tcr = getCellRenderer();
			if (tcr instanceof DefaultTreeCellRenderer) {
				DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer) tcr);
				// For 1.1 uncomment this, 1.2 has a bug that will cause an
				// exception to be thrown if the border selection color is
				// null.
				// dtcr.setBorderSelectionColor(null);
				dtcr.setTextSelectionColor(UIManager.getColor("Table.selectionForeground"));
				dtcr.setBackgroundSelectionColor(UIManager.getColor("Table.selectionBackground"));
				Image img = CmUtil.getImageFromServer("iconPart.gif");
				ImageIcon icon = new ImageIcon(img);
				dtcr.setLeafIcon(icon);
				dtcr.setOpenIcon(icon);
				dtcr.setClosedIcon(icon);
			}
		}

		/**
		 * Sets the row height of the tree, and forwards the row height to the
		 * table.
		 */
		public void setRowHeight(int rowHeight) {
			if (rowHeight > 0) {
				super.setRowHeight(rowHeight);
				if (PBOMReleasedJTreeTable.this != null && PBOMReleasedJTreeTable.this.getRowHeight() != rowHeight) {
					PBOMReleasedJTreeTable.this.setRowHeight(getRowHeight());
				}
			}
		}

		/**
		 * This is overridden to set the height to match that of the JTable.
		 */
		public void setBounds(int x, int y, int w, int h) {
			super.setBounds(x, 0, w, PBOMReleasedJTreeTable.this.getHeight());
		}

		/**
		 * Sublcassed to translate the graphics such that the last visible row
		 * will be drawn at 0,0.
		 */
		public void paint(Graphics g) {
			g.translate(0, -visibleRow * getRowHeight());
			super.paint(g);
		}

		/**
		 * TreeCellRenderer method. Overridden to update the visible row.
		 */
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			if (isSelected) {
				setBackground(table.getSelectionBackground());
			} else {
				setBackground(table.getBackground());
			}

			visibleRow = row;

			return this;
		}
	}

	public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor {
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
			return tree;
		}

		public boolean isCellEditable(EventObject e) {
			if (e instanceof MouseEvent) {
				for (int counter = getColumnCount() - 1; counter >= 0; counter--) {
					if (getColumnClass(counter) == TreeTableModel.class) {
						MouseEvent me = (MouseEvent) e;
						MouseEvent newME = new MouseEvent(tree, me.getID(), me.getWhen(), me.getModifiers(), me.getX()
								- getCellRect(0, counter, true).x, me.getY(), me.getClickCount(), me.isPopupTrigger());
						tree.dispatchEvent(newME);
						break;
					}
				}
			}
			return false;
		}
	}

	class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel {
		/** Set to true when we are updating the ListSelectionModel. */
		protected boolean updatingListSelectionModel;

		public ListToTreeSelectionModelWrapper() {
			super();
			getListSelectionModel().addListSelectionListener(createListSelectionListener());
		}

		ListSelectionModel getListSelectionModel() {
			return listSelectionModel;
		}

		public void resetRowSelection() {
			if (!updatingListSelectionModel) {
				updatingListSelectionModel = true;
				try {
					super.resetRowSelection();
				} finally {
					updatingListSelectionModel = false;
				}
			}
		}

		protected ListSelectionListener createListSelectionListener() {
			return new ListSelectionHandler();
		}

		protected void updateSelectedPathsFromSelectedRows() {
			if (!updatingListSelectionModel) {
				updatingListSelectionModel = true;
				try {
					int min = listSelectionModel.getMinSelectionIndex();
					int max = listSelectionModel.getMaxSelectionIndex();

					clearSelection();
					if (min != -1 && max != -1) {
						for (int counter = min; counter <= max; counter++) {
							if (listSelectionModel.isSelectedIndex(counter)) {
								TreePath selPath = tree.getPathForRow(counter);
								if (selPath != null) {
									addSelectionPath(selPath);
								}
							}
						}
					}
				} finally {
					updatingListSelectionModel = false;
				}
			}
		}

		class ListSelectionHandler implements ListSelectionListener {
			public void valueChanged(ListSelectionEvent e) {
				updateSelectedPathsFromSelectedRows();
			}
		}
	}
}

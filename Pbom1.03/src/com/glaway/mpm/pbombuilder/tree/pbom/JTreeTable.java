package com.glaway.mpm.pbombuilder.tree.pbom;

import com.glaway.mpm.pbom.table.DefaultGroup;
import com.glaway.mpm.pbom.table.GroupableTableHeader;
import com.glaway.mpm.pbom.table.KVItem;
import com.glaway.mpm.pbombuilder.util.CmUtil;
import com.glaway.mpm.pbombuilder.util.Constants;
import com.glaway.mpm.pbombuilder.util.LoadConfig;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.List;

public class JTreeTable extends JTable {
	private static final long serialVersionUID = 1L;
	/** A subclass of JTree. */
	protected TreeTableCellRenderer tree;
	private String type;

	public JTreeTable(TreeTableModel treeTableModel,String type) {
		super();
		this.type = type;

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

		if("2".equals(type)){
			GroupableTableHeader tableHeader = new GroupableTableHeader();
			this.setTableHeader(tableHeader);
			DefaultGroup  group = new DefaultGroup();
	        group.setRow(0);
	        group.setColumn(0);
	        group.setRowSpan(2);
	        group.setHeaderValue("批量操作");
	        tableHeader.addGroup(group);

	        group = new DefaultGroup();
	        group.setRow(0);
	        group.setRowSpan(2);
	        group.setColumn(1);
	        group.setHeaderValue("编号");
	        tableHeader.addGroup(group);

	        //数量
	        group = new DefaultGroup();
	        group.setRow(0);
	        group.setRowSpan(2);
	        group.setColumn(2);
	        group.setHeaderValue("数量");
	        tableHeader.addGroup(group);

	        //材料
	        group = new DefaultGroup();
	        group.setRow(0);
	        group.setRowSpan(2);
	        group.setColumn(3);
	        group.setHeaderValue("材料");
	        tableHeader.addGroup(group);

	        //材料名称
	        group = new DefaultGroup();
	        group.setRow(0);
	        group.setRowSpan(2);
	        group.setColumn(4);
	        group.setHeaderValue("材料名称");
	        tableHeader.addGroup(group);

	        //规格
	        group = new DefaultGroup();
	        group.setRow(0);
	        group.setRowSpan(2);
	        group.setColumn(5);
	        group.setHeaderValue("规格");
	        tableHeader.addGroup(group);

	        //零部件类型
	        group = new DefaultGroup();
	        group.setRow(0);
	        group.setRowSpan(2);
	        group.setColumn(6);
	        group.setHeaderValue("零部件类型");
	        tableHeader.addGroup(group);

	        group = new DefaultGroup();
	        group.setRow(0);
	        group.setColumn(7);
	        group.setRowSpan(2);
	        group.setHeaderValue("零组件生产类型");
	        tableHeader.addGroup(group);

	        group = new DefaultGroup();
	        group.setRow(0);
	        group.setRowSpan(2);
	        group.setColumn(8);
	        group.setHeaderValue("主制车间");
	        tableHeader.addGroup(group);

	        List<KVItem> plantList = LoadConfig.getInstance().getPlans();
	        group = new DefaultGroup();
	        group.setRow(0);
	        group.setColumn(9);
	        group.setColumnSpan(plantList.size());
	        group.setHeaderValue("辅制车间");
	        tableHeader.addGroup(group);

	        for(int i=0;i<plantList.size();i++){
		        group = new DefaultGroup();
		        group.setRow(1);
		        group.setColumn(9+i);
		        group.setHeaderValue(plantList.get(i).getValue());
		        tableHeader.addGroup(group);
	        }

		}

		setShowGrid(true);// 是否有边框线

		// No intercell spacing
		setIntercellSpacing(new Dimension(0, 0));

		// And update the height of the trees row to match that of
		// the table.
		if (tree.getRowHeight() < 1) {
			// Metal looks better like this.
			setRowHeight(18);
		}

		hiddenColumn(2);
	}

	public void hiddenColumn(int n) {
		TableColumnModel columnModel = this.getColumnModel();
		TableColumn column = columnModel.getColumn(n);
		column.setMinWidth(0);
		column.setMaxWidth(0);
		column.setWidth(0);
		column.setPreferredWidth(0);
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
			setCellRenderer(new CmTreeTableCellRenderer());
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
				if (JTreeTable.this != null && JTreeTable.this.getRowHeight() != rowHeight) {
					JTreeTable.this.setRowHeight(getRowHeight());
				}
			}
		}

		/**
		 * This is overridden to set the height to match that of the JTable.
		 */
		public void setBounds(int x, int y, int w, int h) {
			super.setBounds(x, 0, w, JTreeTable.this.getHeight());
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

			if("1".equals(type)){
				table.getColumnModel().getColumn(7).setCellEditor(
						new DefaultCellEditor(new JComboBox(LoadConfig.getInstance().getPartType())));
			} else if("2".equals(type)){
				table.getColumnModel().getColumn(7).setCellEditor(
						new DefaultCellEditor(new JComboBox(LoadConfig.getInstance().getPartType())));
				table.getColumnModel().getColumn(8).setCellEditor(
						new DefaultCellEditor(new JComboBox(LoadConfig.getInstance().getMainPlant())));
				List<KVItem> plantList = LoadConfig.getInstance().getPlans();
//				for(int i=0;i<plantList.size();i++){
//					table.getColumnModel().getColumn(4+i).setCellRenderer(new IsFzbmRenderer());
//					table.getColumnModel().getColumn(4+i).setCellEditor(new IsFzbmEditor(new JCheckBox()));
//				}
			} else if ("3".equals(type)) {
				table.getColumnModel().getColumn(7).setCellEditor(
						new DefaultCellEditor(new JComboBox(new String[]{"N","G","Z"})));
			} else if ("4".equals(type)) {
				table.getColumnModel().getColumn(6).setCellEditor(
						new DefaultCellEditor(new JComboBox(Constants.phasecodeValues)));
				table.getColumnModel().getColumn(7).setCellEditor(
						new DefaultCellEditor(new JComboBox(Constants.setMarks)));
				table.getColumnModel().getColumn(8).setCellEditor(
						new DefaultCellEditor(new JComboBox(Constants.adjustable)));
				table.getColumnModel().getColumn(9).setCellEditor(
						new DefaultCellEditor(new JComboBox(Constants.secretValue)));

			}


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

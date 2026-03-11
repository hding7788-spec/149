package com.glaway.mpm.pbom.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;


public class DefaultZTableFactory implements TableModelListener, ZTableOp,
		MouseListener {
	/** 向上图标。 */
	final static Icon upIcon = new UpDownArrow(0);

	/** 向下图标。 */
	final static Icon downIcon = new UpDownArrow(1);

	/** 表格。 */
	private JTable table;

	/** 需要排序的列。 */
	private int sortColumn;

	/** 行对象。 */
	private Row rows[];

	/** 排序方式。 */
	private boolean ascending;

	/** 要排序的列。 */
	private int sortableColumns[];

	/** 不可以编辑的列。 */
	private int[] columns;

	/** 读写锁，对读读不互斥，对其他都互斥。 */
	private ReentrantReadWriteLock reentLock = new ReentrantReadWriteLock();

	/** 表头对象。 */
	private Object[] gpsTableHead;

	/** 表体对象。 */
	private Object[][] gpsTableContent;

	/** 列宽对象。 */
	private int[] gpsTableColsWidth;

	private CommonTableModelListener gpsModelListener = new CommonTableModelListener();

	private volatile CommonTableModel gpsModel;

	/**
	 * <BR>
	 * <UL>
	 * table初始化方法。
	 * <LI>table初始化方法。</LI>
	 * </UL>
	 *
	 * @param 无
	 *            。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public synchronized void initialize() {
		CommonTableModel gpsModel = getTableModel();
		gpsModel.addTableModelListener(gpsModelListener);
		table.getTableHeader().addMouseListener(this);
		JTableHeader jtableheader = table.getTableHeader();
		jtableheader.setDefaultRenderer(createHeaderRenderer());
		if (table.getRowCount() > 0) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					reinitialize();
				}
			});
		}
	}

	/**
	 * <BR>
	 * <UL>
	 * 得到一个单元格渲染器。
	 * <LI>得到一个单元格渲染器。</LI>
	 * </UL>
	 *
	 * @param 无
	 *            。
	 * @return defaultHeaderRenderer 表头渲染器。
	 * @throws 无
	 *             。
	 */
	private TableCellRenderer createHeaderRenderer() {
		DefaultTableCellRenderer defaultHeaderRenderer = new SortHeaderRenderer();
		defaultHeaderRenderer.setHorizontalAlignment(0);
		defaultHeaderRenderer.setHorizontalTextPosition(2);
		return defaultHeaderRenderer;
	}

	/**
	 * <BR>
	 * <UL>
	 * 对table内容重新初始化。
	 * <LI>对table内容重新初始化。</LI>
	 * </UL>
	 *
	 * @param 无
	 *            。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public synchronized void reinitialize() {
		rows = null;
		if (table.getRowCount() > 0) {
			rows = new Row[table.getRowCount()];
			for (int i = 0; i < rows.length; i++) {
				rows[i] = new Row();
				rows[i].index = i;
			}
		}
	}

	/**
	 * <BR>
	 * <UL>
	 * 判断列是否是需要排序的列。
	 * <LI>判断列是否是需要排序的列。</LI>
	 * </UL>
	 *
	 * @param i
	 *            列。
	 * @return 列是否需要排序。
	 * @throws 无
	 *             。
	 */
	private boolean columnIsSortable(int i) {
		if (rows != null) {
			if (sortableColumns != null) {
				for (int j = 0; j < sortableColumns.length; j++) {
					if (i == sortableColumns[j]) {
						return true;
					}
				}

			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * <BR>
	 * <UL>
	 * 根据表头排序。
	 * <LI>根据表的表头进行排序。</LI>
	 * </UL>
	 *
	 * @param 无
	 *            。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public void sort() {
		CommonTableModel gpsModel = getTableModel();
		if (rows == null) {
		} else {
			gpsModel.removeTableModelListener(gpsModelListener);
			// 分组排序。
			Arrays.sort(rows);
			// 重新设置数据。
			resetData();
			gpsModel.fireTableDataChanged();
			gpsModel.addTableModelListener(gpsModelListener);
			table.revalidate();
			table.repaint();
		}
	}

	/**
	 * <BR>
	 * <UL>
	 * 重新设置数据。
	 * <LI>重新设置数据。</LI>
	 * </UL>
	 *
	 * @param 无
	 *            。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	@SuppressWarnings("unchecked")
	public void resetData() {
		CommonTableModel gpsModel = getTableModel();
		Vector data = new Vector(gpsModel.getRowCount());

		for (int i = 0; i < gpsModel.getRowCount(); i++) {

			final Vector vv = new Vector(gpsModel.getColumnCount());
			for (int j = 0; j < gpsModel.getColumnCount(); j++) {
				vv.add(gpsModel.getValueAt(i, j));
			}
			data.add(vv);
		}

		for (int i = 0; i < rows.length; i++) {
			if (rows[i].index != i) {
				final Vector vv = (Vector) data.get(rows[i].index);
				for (int j = 0; j < gpsModel.getColumnCount(); j++) {
					gpsModel.setValueAt(vv.get(j), i, j);
				}
			}
		}
	}

	/**
	 * <BR>
	 * <UL>
	 * 当table内容改变的时候。
	 * <LI>当table中的内容改变后的触发事件。</LI>
	 * </UL>
	 *
	 * @param tablemodelevent
	 *            table模型事件。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public void tableChanged(TableModelEvent tablemodelevent) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				reinitialize();
			}
		});
	}

	/**
	 * <BR>
	 * <UL>
	 * 设置table中的信息。
	 * <LI>设置table中的信息。</LI>
	 * </UL>
	 *
	 * @param tableHeadValue
	 *            表头信息。
	 * @param tableContentValue
	 *            表体信息。
	 * @param preferredWidthValue
	 *            表头列宽。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public void setTableInfors(Object[] tableHeadValue,
			Object[][] tableContentValue, int[] preferredWidthValue) {
		makeTable(tableHeadValue, tableContentValue, preferredWidthValue);
	}

	/**
	 * <BR>
	 * <UL>
	 * 设置table中的信息。
	 * <LI>设置table中的信息。</LI>
	 * </UL>
	 *
	 * @param tableHeadValue
	 *            表头信息。
	 * @param tableContentValue
	 *            表体信息。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public void setTableInfors(Object[] tableHeadValue,
			Object[][] tableContentValue) {
		gpsTableHead = tableHeadValue;
		gpsTableContent = tableContentValue;
	}

	/**
	 * <BR>
	 * <UL>
	 * 设置table中的列宽。
	 * <LI>设置table中的列宽。</LI>
	 * </UL>
	 *
	 * @param preferredWidthValue
	 *            table的列宽。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public void setPreferredWidth(int[] preferredWidthValue) {
		gpsTableColsWidth = preferredWidthValue;
		makeTable(gpsTableHead, gpsTableContent, gpsTableColsWidth);
	}

	/**
	 * <BR>
	 * <UL>
	 * 制造需要的table。
	 * <LI>制造需要的table，粗加工。</LI>
	 * </UL>
	 *
	 * @param tableHeadValue
	 *            表头内容数组。
	 * @param tableContentValue
	 *            表体内容数组。
	 * @param preferredWidthValue
	 *            列宽对象数组。
	 * @return 。
	 * @throws 。
	 */
	private void makeTable(Object[] tableHeadValue,
			Object[][] tableContentValue, int[] preferredWidthValue) {
		rows = null;
		ascending = true;
		sortableColumns = null;

		CommonTableModel defModel = new CommonTableModel(tableContentValue,
				tableHeadValue);
		defModel.setColumnsEditable(columns);
		table = new JTable(defModel);
		int length = table.getModel().getColumnCount();
		final int[] columns = new int[length];
		for (int i = 0; i < length; i++) {
			columns[i] = i;
		}
		sortableColumns = columns;
		initialize();

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		for (int i = 0; i < tableHeadValue.length; i++) {
//			table.getColumn(tableHeadValue[i]).setPreferredWidth(
//					preferredWidthValue[i]);
			table.getColumn(tableHeadValue[i]).setMinWidth(preferredWidthValue[i]);
		}
	}

	/**
	 * <BR>
	 * <UL>
	 * 得到需要的table。
	 * <LI>得到经过加工后的table。</LI>
	 * </UL>
	 *
	 * @param 无
	 *            。
	 * @return table 经过加工的table。
	 * @throws 。
	 */
	public JTable getZTable() {
		return table;
	}

	/**
	 * <BR>
	 * <UL>
	 * 插入一行。
	 * <LI>在table的最后面插入一行。</LI>
	 * </UL>
	 *
	 * @param table
	 *            需要操作的table。
	 * @param tableAddRow
	 *            table中需要插入的内容。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public synchronized void addOneRow(final Object[] tableAddRow) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				CommonTableModel gpsModel = getTableModel();
				gpsModel.removeTableModelListener(gpsModelListener);
				gpsModel.addRow(tableAddRow);
				reinitialize();
				gpsModel.addTableModelListener(gpsModelListener);
			}
		});
	}

	/**
	 * <BR>
	 * <UL>
	 * 插入一行。
	 * <LI>在table的最后面插入一行。</LI>
	 * </UL>
	 *
	 * @param table
	 *            需要操作的table。
	 * @param tableAddRow
	 *            table中需要插入的内容。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public synchronized void addOneRow(JTable table, Object[] tableAddRow) {
		CommonTableModel gpsModel = (CommonTableModel) table.getModel();
		gpsModel.addRow(tableAddRow);
	}

	/**
	 * <BR>
	 * <UL>
	 * 插入一行。
	 * <LI>在用户选择的行的上面插入用户需要的行。</LI>
	 * </UL>
	 *
	 * @param table
	 *            操作table。
	 * @param currentRow
	 *            当前的行。
	 * @param tableInsertRow
	 *            需要插入的内容。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public synchronized void insertOneRow(final int currentRow,
			final Object[] tableInsertRow) {
		if (currentRow > -1 && currentRow < table.getRowCount()) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					getTableModel().removeTableModelListener(gpsModelListener);
					getTableModel().insertRow(
							(currentRow >= 0 ? currentRow : 0), tableInsertRow);
					reinitialize();
					getTableModel().addTableModelListener(gpsModelListener);
				}
			});
		}
	}

	/**
	 * <BR>
	 * <UL>
	 * 插入一行。
	 * <LI>在用户选择的行的上面插入用户需要的行。</LI>
	 * </UL>
	 *
	 * @param table
	 *            操作table。
	 * @param currentRow
	 *            当前的行。
	 * @param tableInsertRow
	 *            需要插入的内容。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public synchronized void insertOneRow(JTable table, int currentRow,
			Object[] tableInsertRow) {
		TableModel dataModel = table.getModel();
		if (currentRow > -1 && currentRow < table.getRowCount()) {
			((CommonTableModel) dataModel).insertRow(
					(currentRow >= 0 ? currentRow : 0), tableInsertRow);
		}

		table.getSelectionModel().setSelectionInterval(currentRow + 1,
				currentRow + 1);

	}

	/**
	 * <BR>
	 * <UL>
	 * 对指定table的指定的行进行维护。
	 * <LI>对指定table的指定的行进行维护。</LI>
	 * </UL>
	 *
	 * @param table
	 *            需要操作的table。
	 * @param opRow
	 *            table中需要操作的一行。
	 * @param voValues
	 *            订单的信息。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public synchronized void updateTableOneRow(final int opRow,
			final Object[] voValues) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				CommonTableModel gpsModel = getTableModel();
				gpsModel.removeTableModelListener(gpsModelListener);
				for (int i = 0; i < voValues.length; i++) {
					gpsModel.setValueAt(voValues[i], opRow, i);
				}
				reinitialize();
				gpsModel.addTableModelListener(gpsModelListener);
			}
		});
	}

	/**
	 * <BR>
	 * <UL>
	 * 对指定table的指定的行进行维护。
	 * <LI>对指定table的指定的行进行维护。</LI>
	 * </UL>
	 *
	 * @param table
	 *            需要操作的table。
	 * @param opRow
	 *            table中需要操作的一行。
	 * @param voValues
	 *            订单的信息。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public synchronized void updateTableOneRow(JTable table, int opRow,
			Object[] voValues) {
		int tempRow = opRow;
		Object[] tempVo = voValues;
		CommonTableModel gpsModel = (CommonTableModel) table.getModel();

		for (int i = 0; i < tempVo.length; i++) {
			gpsModel.setValueAt(tempVo[i], tempRow, i);
		}

		table.getSelectionModel().setSelectionInterval(opRow, opRow);

	}

	/**
	 * <BR>
	 * <UL>
	 * 删除一行。
	 * <LI>删除操作table中选中的一行。</LI>
	 * </UL>
	 *
	 * @param table
	 *            操作table。
	 * @param currentRow
	 *            当前的行。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public void removeOneRow(int currentRow) {
		if (currentRow > -1 && currentRow < table.getRowCount()) {
			getTableModel().removeRow(currentRow);

			EventQueue.invokeLater(new Runnable() {
				public void run() {
					reinitialize();
				}
			});
		}
	}

	/**
	 * <BR>
	 * <UL>
	 * 删除一行。
	 * <LI>删除操作table中选中的一行。</LI>
	 * </UL>
	 *
	 * @param table
	 *            操作table。
	 * @param currentRow
	 *            当前的行。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public synchronized void removeOneRow(JTable table, int currentRow) {
		CommonTableModel gpsModel = (CommonTableModel) table.getModel();
		if (currentRow > -1 && currentRow < table.getRowCount()) {
			gpsModel.removeRow(currentRow);
			table.getSelectionModel().setSelectionInterval(currentRow,
					currentRow);
		}
	}

	/**
	 * <BR>
	 * <UL>
	 * 将table中选中的一行上移。
	 * <LI>将table中选中的一行上移。</LI>
	 * </UL>
	 *
	 * @param table
	 *            需要操作的table。
	 * @param currentRow
	 *            table中选中的行。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public synchronized void currentRowMoveUp(int currentRow) {
		CommonTableModel gpsModel = getTableModel();
		gpsModel.removeTableModelListener(gpsModelListener);
		int colNum = gpsModel.getColumnCount();
		Object value;
		if (currentRow > 0) {
			for (int i = 0; i < colNum; i++) {
				value = gpsModel.getValueAt(currentRow - 1, i);
				gpsModel.setValueAt(gpsModel.getValueAt(currentRow, i),
						currentRow - 1, i);
				gpsModel.setValueAt(value, currentRow, i);

			}
		}
		gpsModel.addTableModelListener(gpsModelListener);
	}

	/**
	 * <BR>
	 * <UL>
	 * 将table中选中的一行上移。
	 * <LI>将table中选中的一行上移。</LI>
	 * </UL>
	 *
	 * @param table
	 *            需要操作的table。
	 * @param currentRow
	 *            table中选中的行。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public synchronized void currentRowMoveUp(JTable table, int currentRow) {
		TableModel dataModel = table.getModel();
		dataModel.removeTableModelListener(gpsModelListener);
		int colNum = table.getColumnCount();
		Object value;
		if (currentRow > 0) {
			for (int i = 0; i < colNum; i++) {
				value = dataModel.getValueAt(currentRow - 1, i);
				dataModel.setValueAt(dataModel.getValueAt(currentRow, i),
						currentRow - 1, i);
				dataModel.setValueAt(value, currentRow, i);

			}
			table.getSelectionModel().setSelectionInterval(currentRow - 1,
					currentRow - 1);
		}
		dataModel.addTableModelListener(gpsModelListener);
	}

	/**
	 * <BR>
	 * <UL>
	 * 将table中选中的一行下移。
	 * <LI>将table中选中的一行下移。</LI>
	 * </UL>
	 *
	 * @param table
	 *            需要操作的table。
	 * @param currentRow
	 *            table中选中的行。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public synchronized void currentRowMoveDown(int currentRow) {
		CommonTableModel gpsModel = getTableModel();
		gpsModel.removeTableModelListener(gpsModelListener);
		int rowNum = table.getRowCount();
		int colNum = table.getColumnCount();
		Object value;
		if (rowNum > currentRow + 1) {
			for (int i = 0; i < colNum; i++) {
				value = gpsModel.getValueAt(currentRow + 1, i);
				gpsModel.setValueAt(gpsModel.getValueAt(currentRow, i),
						currentRow + 1, i);
				gpsModel.setValueAt(value, currentRow, i);

			}
		}
		gpsModel.addTableModelListener(gpsModelListener);

	}

	/**
	 * <BR>
	 * <UL>
	 * 将table中选中的一行下移。
	 * <LI>将table中选中的一行下移。</LI>
	 * </UL>
	 *
	 * @param table
	 *            需要操作的table。
	 * @param currentRow
	 *            table中选中的行。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public synchronized void currentRowMoveDown(JTable table, int currentRow) {
		TableModel dataModel = table.getModel();
		dataModel.removeTableModelListener(gpsModelListener);
		int rowNum = table.getRowCount();
		int colNum = table.getColumnCount();
		Object value;
		if (rowNum > currentRow + 1) {
			for (int i = 0; i < colNum; i++) {
				value = dataModel.getValueAt(currentRow + 1, i);
				dataModel.setValueAt(dataModel.getValueAt(currentRow, i),
						currentRow + 1, i);
				dataModel.setValueAt(value, currentRow, i);
			}
			table.getSelectionModel().setSelectionInterval(currentRow + 1,
					currentRow + 1);
		}
		dataModel.addTableModelListener(gpsModelListener);

	}

	/**
	 * <BR>
	 * <UL>
	 * 设置一列为隐藏列。
	 * <LI>将table中指定的列设为隐藏。</LI>
	 * </UL>
	 *
	 * @param table
	 *            用户操作table。
	 * @param clumnNum
	 *            需要隐藏的列。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public void setOneColumnHidden(JTable table, int columnNum) {
		TableColumnModel dfcm = table.getColumnModel();
		dfcm.getColumn(columnNum).setMinWidth(0);
		dfcm.getColumn(columnNum).setMaxWidth(0);
	}

	/**
	 * <BR>
	 * <UL>
	 * 设置若干列为隐藏列。
	 * <LI>将table中指定的若干列设为隐藏。</LI>
	 * </UL>
	 *
	 * @param table
	 *            用户操作table。
	 * @param clumnNum
	 *            需要隐藏的列。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public void setColumnsHidden(JTable table, int[] columnNum) {
		TableColumnModel dfcm = table.getColumnModel();
		for (int i = 0; i < columnNum.length; i++) {
			dfcm.getColumn(columnNum[i]).setMinWidth(0);
			dfcm.getColumn(columnNum[i]).setMaxWidth(0);
		}
	}

	/**
	 * <BR>
	 * <UL>
	 * 设置table中的哪一列可以编辑。
	 * <LI>设置table中的哪一列可以编辑。</LI>
	 * </UL>
	 *
	 * @param 。
	 * @return 。
	 * @throws 。
	 */
	public void setColumnsEditable(int[] cols) {
		columns = cols;
	}

	/**
	 * <BR>
	 * <UL>
	 * 设置生成的table的样式。
	 * <LI>设置生成的table的样式。</LI>
	 * </UL>
	 *
	 * @param table
	 *            需要设置样式的table。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public synchronized void setTableStyle(JTable table) {
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer() {

			private static final long serialVersionUID = -7032987399719259731L;

			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				// 设置奇数行底色。
				if (row % 2 == 0) {
					setBackground(Color.white);
				}
				// 设置偶数行底色
				else if (row % 2 == 1) {
					setBackground(new Color(206, 231, 255));

				}
				return super.getTableCellRendererComponent(table, value,
						isSelected, hasFocus, row, column);
			}
		};
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumn(table.getColumnName(i)).setCellRenderer(tcr);
		}
	}

	/**
	 * <BR>
	 * <UL>
	 * 当鼠标点击后的触发事件。
	 * <LI>当鼠标点击表头后的触发事件。</LI>
	 * </UL>
	 *
	 * @param event
	 *            鼠标事件。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public synchronized void mouseClicked(MouseEvent event) {
		if (event.getSource() == table.getTableHeader()) {
			table.getTableHeader().removeMouseListener(this);
			int i = table.columnAtPoint(event.getPoint());
			int j = table.convertColumnIndexToModel(i);

			// 转换出用户想排序的列和底层数据的列，然后判断
			if (!columnIsSortable(j)) {
				return;
			}
			if (j == sortColumn) {
				ascending = !ascending;
			} else {
				ascending = true;
				sortColumn = j;
			}

			EventQueue.invokeLater(new Runnable() {
				public void run() {
					sort();
				}
			});

			table.getTableHeader().addMouseListener(this);
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/**
	 * <BR>
	 * <UL>
	 * 得到table的model。
	 * <LI>得到table的model，为防止脏读，脏写进行加锁控制。</LI>
	 * </UL>
	 *
	 * @param table
	 *            需要操作的table。
	 * @param opRow
	 *            table中需要操作的一行。
	 * @param voValues
	 *            订单的信息。
	 * @return 无。
	 * @throws 无
	 *             。
	 */
	public CommonTableModel getTableModel() {

		try {
			// 对写线程进行加锁，对读读不排斥，其他都排斥。
			reentLock.readLock().lock();

			gpsModel = (CommonTableModel) table.getModel();
			return gpsModel;
		} finally {
			// 解锁。
			reentLock.readLock().unlock();
		}
	}

	/**
	 * <BR>
	 * <UL>
	 * 利用正则表达式判断一个字符是否是数字。
	 * <LI>利用正则表达式判断一个字符是否是数字。</LI>
	 * </UL>
	 *
	 * @param str
	 *            字符串。
	 * @return 是否是数字。
	 * @throws 无
	 *             。
	 */
	private boolean isNumeric(String str) {
		if ("".equals(str.trim())) {
			return false;
		}
		// 是否是int型。
		boolean isInt = false;
		// 是否是float型。
		boolean isFloat = false;

		// 匹配int行。
		Pattern pattern = Pattern.compile("[0-9]*");
		// 匹配结果。
		isInt = pattern.matcher(str).matches();

		// 匹配float型。
		pattern = Pattern.compile("[0-9]*+\\.+[0-9]*");
		// 匹配结果。
		isFloat = pattern.matcher(str).matches();

		// 是否是数字。
		return isInt | isFloat;
	}


	private class SortHeaderRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = -8059140121463202150L;

		public Component getTableCellRendererComponent(JTable jtable,
				Object obj, boolean flag, boolean flag1, int i, int j) {
			if (jtable != null) {
				JTableHeader jtableheader = jtable.getTableHeader();
				if (jtableheader != null) {
					setForeground(jtableheader.getForeground());
					setBackground(jtableheader.getBackground());
					setFont(jtableheader.getFont());
				}
			}
			setText(obj != null ? obj.toString() : "");
			int k = jtable.convertColumnIndexToModel(j);
			if (k == sortColumn) {
				setIcon(ascending ? DefaultZTableFactory.upIcon
						: DefaultZTableFactory.downIcon);
			} else {
				setIcon(null);
			}
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			return this;
		}
	}


	private class CommonTableModelListener implements TableModelListener {
		public void tableChanged(TableModelEvent e) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					reinitialize();
				}
			});
		}
	}


	private class Row implements Comparable<Object> {
		private CommonTableModel gpsModel = (CommonTableModel) getTableModel();

		private int index;

		public int compareTo(Object obj) {
			Row row = (Row) obj;
			Collator cnCollator = Collator.getInstance(Locale.getDefault());

			/*
			 * 代码级处理线程的脏读(只有在比较极端的情况下才会出现，系统已最大程序上避免脏读)。
			 * 当出现脏读的时候直接返回-1，虽然不是最稳妥的方法， 但可以增加程序的健壮性，这样处理最多在出现脏读的情况下50%的概率出现
			 * 一次两条数据的排序错误，不会有数据的损害和线程的崩溃。。
			 */
			if (gpsModel.getRowCount() < row.index) {
				// 出现脏读直接返回，防止程序继续执行时出错。
				return -1;
			}

			Object obj1 = gpsModel.getValueAt(index, sortColumn);
			Object obj2 = gpsModel.getValueAt(row.index, sortColumn);
			float num1;
			float num2;
			if (ascending) {
				if (!(obj1 instanceof Comparable<?>)) {
					return -1;
				}
				if (!(obj2 instanceof Comparable<?>)) {
					return 1;
				} else {
					if (isNumeric(String.valueOf(obj1))
							&& isNumeric(String.valueOf(obj2))) {
						num1 = Float.parseFloat(String.valueOf(obj1));
						num2 = Float.parseFloat(String.valueOf(obj2));
						if (num1 > num2) {
							return 1;
						} else {
							return -1;
						}
					} else {
						return cnCollator.compare(obj1, obj2);
					}
				}
			}
			if (!(obj1 instanceof Comparable<?>)) {
				return 1;
			}
			if (!(obj2 instanceof Comparable<?>)) {
				return -1;
			} else {
				if (isNumeric(String.valueOf(obj1))
						&& isNumeric(String.valueOf(obj2))) {
					num1 = Float.parseFloat(String.valueOf(obj1));
					num2 = Float.parseFloat(String.valueOf(obj2));
					if (num1 > num2) {
						return -1;
					} else {
						return 1;
					}
				} else {
					return cnCollator.compare(obj2, obj1);
				}
			}
		}
	}
}

package com.glaway.mpm.parameter.commonui.listener;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * 列表的表头监听器,当双击表头的分隔线时,会根据表格中内容自动调整列宽,使该列的数据可以完全显示出来.
 * 
 * @author 龙秀川
 *
 */
public class CommonTableHeaderMouseListener extends MouseAdapter {

	/**
	 * 根据给定的列号调整表格的列宽
	 * 
	 * @param table
	 * @param col
	 */
	public static void adjustColumnPreferredWidths(JTable table, int col) {
		TableColumnModel columnModel = table.getColumnModel();
		int maxwidth = 0;
		for (int row = 0; row < table.getRowCount(); row++) {
			TableCellRenderer rend = table.getCellRenderer(row, col);
			Object value = table.getValueAt(row, col);
			Component comp = rend.getTableCellRendererComponent(table, value,
					false, false, row, col);
			maxwidth = Math.max(comp.getPreferredSize().width, maxwidth);
		}
		TableColumn column = columnModel.getColumn(col);
		column.setPreferredWidth(maxwidth + 3);
	}

	private JTable table;

	public CommonTableHeaderMouseListener(JTable table) {
		this.table = table;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// 当光标处于两个列表间的分隔线上时，表头的光标呈东西调整的样式，通过
		// 鼠标的样式和点击次数来判断是否需要进行列宽调整
		int cursorType = table.getTableHeader().getCursor().getType();
		if (cursorType == Cursor.E_RESIZE_CURSOR
				|| cursorType == Cursor.W_RESIZE_CURSOR) {
			if (e.getClickCount() == 2) {
				// 获取光标点击位置的列号，这里将X的坐标减去3个像素,是为了保证取到的点始终是分隔线前的列号
				int col = table.getTableHeader().getColumnModel()
						.getColumnIndexAtX(e.getX() - 3);
				adjustColumnPreferredWidths(table, col);
			}
		}
	}
}

package com.glaway.mpm.parameter.commonui;

import java.awt.Color;
import java.awt.Component;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.speciaword.component.EditorPane;

public class ParameterSpecialSymbolRenderer extends EditorPane implements TableCellRenderer {

	private static final long serialVersionUID = 1L;
	private final DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
	@SuppressWarnings("rawtypes")
	private final Map cellSizes = new HashMap();

	public ParameterSpecialSymbolRenderer() {
		super(MPMParameterProcessor.getParamWorkSpace());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addSize(JTable table, int row, int column, int height) {
		Map rows = (Map) cellSizes.get(table);
		if (rows == null) {
			rows = new HashMap();
			cellSizes.put(table, rows);
		}
		Map rowHeights = (Map) rows.get(new Integer(row));
		if (rowHeights == null) {
			rowHeights = new HashMap();
			rows.put(new Integer(row), rowHeights);
		}

		rowHeights.put(new Integer(column), new Integer(height));
	}

	@SuppressWarnings("rawtypes")
	private int findMaximumRowSize(JTable table, int row) {
		Map rows = (Map) cellSizes.get(table);
		if (rows == null)
			return 0;
		Map rowHeights = (Map) rows.get(new Integer(row));
		if (rowHeights == null)
			return 0;
		int maximum_height = 0;
		for (Iterator it = rowHeights.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Entry) it.next();
			int cellHeight = ((Integer) entry.getValue()).intValue();
			maximum_height = Math.max(maximum_height, cellHeight);
		}
		return maximum_height;
	}

	@SuppressWarnings({ "rawtypes" })
	private int findTotalMaximumRowSize(JTable table, int row) {
		int maximum_height = 0;
		Enumeration columns = table.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
			TableColumn tc = (TableColumn) columns.nextElement();
			TableCellRenderer renderer = tc.getCellRenderer();
			if (renderer instanceof ParameterSpecialSymbolRenderer) {
				ParameterSpecialSymbolRenderer ser = (ParameterSpecialSymbolRenderer) renderer;
				maximum_height = Math.max(maximum_height, ser.findMaximumRowSize(table, row));
			}
		}
		return maximum_height;
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, Object obj,
			boolean isSelected, boolean hasFocus, final int row, final int column) {
		cellRenderer.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column);
		setBorder(null);
		setFont(cellRenderer.getFont());
		setText(cellRenderer.getText());

		TableColumnModel columnModel = table.getColumnModel();
		setSize(columnModel.getColumn(column).getWidth(), 100000);
		int height_wanted = (int) getPreferredSize().getHeight();
		addSize(table, row, column, height_wanted);
		height_wanted = findTotalMaximumRowSize(table, row);
		if (height_wanted > table.getRowHeight(row)) {
			table.setRowHeight(row, height_wanted);
		}

		if (isSelected) {
			setBackground(table.getSelectionBackground());
		} else {
			setBackground(Color.WHITE);
		}
		return this;
	}
}

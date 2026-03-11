package com.glaway.mpm.parameter.commonui.listener;

import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

import com.glaway.mpm.util.CommonUIUtil;


public class CommonTableColumnModelListener implements TableColumnModelListener {

	private JTable table;

	public CommonTableColumnModelListener(JTable table) {
		super();
		this.table = table;
	}

	@Override
	public void columnAdded(TableColumnModelEvent e) {

	}

	@Override
	public void columnMarginChanged(ChangeEvent e) {
		CommonUIUtil.stopTableCellEditing(table);
	}

	@Override
	public void columnMoved(TableColumnModelEvent e) {

	}

	@Override
	public void columnRemoved(TableColumnModelEvent e) {

	}

	@Override
	public void columnSelectionChanged(ListSelectionEvent e) {

	}

}

package com.glaway.mpm.parameter.commonui.listener;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;


public class CommonTableHeaderFocusListener implements FocusListener {

	private Color bgColor;
	private JTable table;
	private Border tableBorder;
	private Border tableHeaderBorder;

	public CommonTableHeaderFocusListener(JTable table) {
		this.table = table;
	}

	@Override
	public void focusGained(FocusEvent e) {
		JTableHeader tableHeader = table.getTableHeader();
		bgColor = tableHeader.getBackground();
		tableBorder = table.getBorder();
		tableHeaderBorder = tableHeader.getBorder();

		//设置表头的背景色
		tableHeader.setBackground(new Color(204,255,255));
		tableHeader.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0, 0, 0)));
		table.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0, 0, 0)));

		foucsManager(true);
	}

	@Override
	public void focusLost(FocusEvent e) {
		JTableHeader tableHeader = table.getTableHeader();

		//设置表头的背景色
		tableHeader.setBackground(bgColor);
		tableHeader.setBorder(tableHeaderBorder);
		table.setBorder(tableBorder);

		table.clearSelection();

		foucsManager(false);
	}

	private void foucsManager(boolean b) {
		//设置工具栏上按钮的状态
		//ComponentAuthorityManager.toolBarAuthorityManage(b);

		if (!b) {
			//MainFrameHelper.setTableBean(null);
		}
	}

}

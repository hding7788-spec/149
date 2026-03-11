package com.glaway.mpm.pbombuilder.tree.pbom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;

public class IsBroowedPartEditor extends DefaultCellEditor {
	private static final long serialVersionUID = 1L;
	private JCheckBox checkBox;
	private boolean value;

	public IsBroowedPartEditor() {
		super(new JCheckBox());
		this.checkBox = (JCheckBox)getComponent();
		// 设置复选框居中对齐
		checkBox.setHorizontalAlignment(JCheckBox.CENTER);
		checkBox.setOpaque(true);

		// 添加鼠标监听器，确保点击整个单元格区域都能触发状态切换
		checkBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// 切换状态
				value = !value;
				checkBox.setSelected(value);
				// 立即停止编辑，保存值
				stopCellEditing();
			}
		});
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		// 保存当前值
		this.value = value instanceof Boolean && (Boolean)value;
		checkBox.setSelected(this.value);

		// 设置与表格选择状态匹配的背景色
		if (isSelected) {
			checkBox.setBackground(table.getSelectionBackground());
			checkBox.setForeground(table.getSelectionForeground());
		} else {
			checkBox.setBackground(table.getBackground());
			checkBox.setForeground(table.getForeground());
		}

		// 设置单元格边框
		checkBox.setBorder(UIManager.getBorder("Table.cellBorder"));

		// 确保复选框填充整个单元格
		checkBox.setPreferredSize(new Dimension(table.getColumnModel().getColumn(column).getWidth(), table.getRowHeight(row)));

		return checkBox;
	}

	@Override
	public Object getCellEditorValue() {
		return value;
	}

	// 修改为1次点击即可编辑
	@Override
	public int getClickCountToStart() {
		return 1;
	}

	// 允许单元格在任何情况下都可编辑
	@Override
	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}
}

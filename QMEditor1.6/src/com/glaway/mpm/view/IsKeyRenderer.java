package com.glaway.mpm.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class IsKeyRenderer implements TableCellRenderer {
	private JPanel panel = new JPanel();
	private JCheckBox box = new JCheckBox();

	public Rectangle getBound() {
		return this.panel.getBounds();
	}

	public Component getTableCellRendererComponent(JTable table, Object arg1,
			boolean arg2, boolean arg3, int arg4, int arg5) {
		this.box.setSelected(false);
		this.panel.setLayout(new GridBagLayout());
		this.panel.add(this.box, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D,
				10, 0, new Insets(0, 0, 0, 0), 0, 0));
		if ((arg1 != null) && ((arg1 instanceof String))) {
			if (arg1.toString().equalsIgnoreCase("true")) {
				this.box.setSelected(true);
			}
		}

		if (arg2) {
			this.panel.setForeground(table.getSelectionForeground());
			this.panel.setBackground(table.getSelectionBackground());
			this.box.setForeground(table.getSelectionForeground());
			this.box.setBackground(table.getSelectionBackground());
		} else {
			this.panel.setForeground(table.getForeground());
			this.panel.setBackground(table.getBackground());
			this.box.setForeground(table.getForeground());
			this.box.setBackground(table.getBackground());
		}

		return this.panel;
	}

	public void setBackground(Color color) {
		this.box.setBackground(color);
		this.panel.setBackground(color);
	}
}

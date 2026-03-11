package com.glaway.mpm.view;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class LabelRender extends DefaultTableCellRenderer {
	private boolean bool = false;

	public LabelRender() {
	}

	public LabelRender(boolean flag) {
		this();
		this.bool = flag;
	}

	public Component getTableCellRendererComponent(JTable arg0, Object arg1,
			boolean arg2, boolean arg3, int arg4, int arg5) {
		JLabel com = (JLabel) super.getTableCellRendererComponent(arg0, arg1,
				arg2, arg3, arg4, arg5);
		if (this.bool) {
			// if (!arg0.isCellEditable(arg4, arg5)) {
			// // com.setBackground(Color.LIGHT_GRAY);
			// com.setForeground(Color.BLACK);
			// } else {
			// // com.setBackground(Color.white);
			// com.setForeground(Color.BLACK);
			// }
		} else {
			com.setHorizontalAlignment(0);
			com.setVerticalAlignment(0);
		}
		
		return com;
	}
}

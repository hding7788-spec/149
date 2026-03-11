package com.glaway.mpm.qmIntf.tool;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.TreeCellRenderer;

public class ToolTreeRenderer extends JPanel implements TreeCellRenderer {
	private static final long serialVersionUID = 1L;

	protected JLabel icon;
	protected JLabel text;
	private Icon icon1 = new ImageIcon(
			ToolTreeRenderer.class.getResource("/image/close.png"));
	private Icon icon2 = new ImageIcon(
			ToolTreeRenderer.class.getResource("/image/file.png"));
	private Icon icon3 = new ImageIcon(
			ToolTreeRenderer.class.getResource("/image/open.png"));

	public ToolTreeRenderer() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		icon = new JLabel() {
			private static final long serialVersionUID = 1L;

			public void setBackground(Color color) {
				if (color instanceof ColorUIResource)
					color = null;
				super.setBackground(color);
			}

		};
		add(icon);
		add(Box.createHorizontalStrut(4));
		add(text = new JLabel());
		text.setOpaque(true);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean isSelected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		setBackground(Color.WHITE);
		text.setPreferredSize(new Dimension(200, 18));
		if (isSelected) {
			text.setBackground(UIManager.getColor("Tree.selectionBackground"));
		} else {
			text.setBackground(UIManager.getColor("Tree.textBackground"));
		}
		if (value instanceof ToolTreeNode) {
			if (expanded) {
				icon.setIcon(icon3);
			} else {
				icon.setIcon(icon1);
			}
			ToolTreeNode toolTreeNode = (ToolTreeNode) value;
			toolTreeNode.setSelected(isSelected);
			text.setText(toolTreeNode.getName());
		} else if (value instanceof ToolNode) {
			ToolNode toolNode = (ToolNode) value;
			toolNode.setSelected(isSelected);
			text.setText(toolNode.getNumber() + "_" + toolNode.getName());
			icon.setIcon(icon2);
		}
		return this;
	}

	// public Dimension getPreferredSize() {
	// Dimension d_label = labelDisplay.getPreferredSize();
	// Dimension d_info = icon.getPreferredSize();
	//
	// return new Dimension(d_label.width + d_info.width + 6, 16);
	//
	// }
	//
	// public void doLayout() {
	// Dimension d_icon = icon.getPreferredSize();
	// Dimension d_label = labelDisplay.getPreferredSize();
	//
	// int y_icon = 0;
	// int y_label = 0;
	//
	// if (d_icon.height > d_label.height)
	// y_icon = (d_label.height - d_icon.height) / 2;
	//
	// icon.setLocation(0, y_icon);
	// icon.setBounds(0, y_icon, d_icon.width, d_icon.height);
	//
	// labelDisplay.setLocation(d_icon.width, y_label);
	// labelDisplay.setBounds(d_icon.width, y_label, d_label.width + 2,
	// d_label.height);
	//
	// }

}

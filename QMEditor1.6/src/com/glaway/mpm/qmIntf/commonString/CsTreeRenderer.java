package com.glaway.mpm.qmIntf.commonString;

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

public class CsTreeRenderer extends JPanel implements TreeCellRenderer {
	private static final long serialVersionUID = 1L;

	protected JLabel icon;
	protected JLabel text;
	private Icon icon1 = new ImageIcon(
			CsTreeRenderer.class.getResource("/image/close.png"));
	private Icon icon2 = new ImageIcon(
			CsTreeRenderer.class.getResource("/image/file.png"));
	private Icon icon3 = new ImageIcon(
			CsTreeRenderer.class.getResource("/image/open.png"));

	public CsTreeRenderer() {
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
		text.setPreferredSize(new Dimension(300, 18));
		if (isSelected) {
			text.setBackground(UIManager.getColor("Tree.selectionBackground"));
		} else {
			text.setBackground(UIManager.getColor("Tree.textBackground"));
		}
		if (value instanceof CsTreeNode) {
			CsTreeNode csTreeNode = (CsTreeNode) value;
			if (expanded) {
				icon.setIcon(icon3);
			} else {
				icon.setIcon(icon1);
			}
			if (!csTreeNode.isRoot()) {
				text.setText(csTreeNode.getValue());
			} else {
				text.setText("常用语");
			}
			csTreeNode.setSelected(isSelected);
		} else if (value instanceof CsNode) {
			CsNode csNode = (CsNode) value;
			text.setText(csNode.getValue());
			csNode.setSelected(isSelected);
			icon.setIcon(icon2);
		} else {

		}
		return this;
	}
}

package com.glaway.mpm.qmIntf.template;

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

public class StTreeRenderer extends JPanel implements TreeCellRenderer {
	private static final long serialVersionUID = 1L;

	protected JLabel icon;
	protected JLabel text;
	private Icon icon1 = new ImageIcon(
			StTreeRenderer.class.getResource("/image/close.png"));
	private Icon icon2 = new ImageIcon(
			StTreeRenderer.class.getResource("/image/file.png"));
	private Icon icon3 = new ImageIcon(
			StTreeRenderer.class.getResource("/image/open.png"));

	public StTreeRenderer() {

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
		text.setPreferredSize(new Dimension(500, 18));
		if (isSelected) {
			text.setBackground(UIManager.getColor("Tree.selectionBackground"));
		} else {
			text.setBackground(UIManager.getColor("Tree.textBackground"));
		}

		if (value instanceof TpTreeNode) {
			TpTreeNode tpTreeNode = (TpTreeNode) value;
			if (!tpTreeNode.isRoot()) {
				text.setText(tpTreeNode.getName());
				if (expanded) {
					icon.setIcon(icon3);
				} else {
					icon.setIcon(icon1);
				}
			} else {
				text.setText("cmTree");
				icon.setIcon(new ImageIcon(""));
			}
		} else if (value instanceof TpNode) {
			TpNode tpNode = (TpNode) value;
			String name = tpNode.getTemplate().getName();
			text.setText(name.endsWith(".zip") ? name.substring(0,
					name.length() - 4) : name);
			icon.setIcon(icon2);
		} else {

		}
		return this;
	}
}
package com.glaway.mpm.qmIntf.template;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class TpTreeRenderer extends JPanel implements TreeCellRenderer {
	private static final long serialVersionUID = 1L;

	protected JLabel icon;
	protected JLabel text;
	private Icon icon1 = new ImageIcon(
			TpTreeRenderer.class.getResource("/image/close.png"));
	private Icon icon2 = new ImageIcon(
			TpTreeRenderer.class.getResource("/image/file.png"));
	private Icon icon3 = new ImageIcon(
			TpTreeRenderer.class.getResource("/image/open.png"));

	public TpTreeRenderer() {

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
		text.setPreferredSize(new Dimension(800, 18));
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
			text.setText(tpNode.getTemplate().getName());
			icon.setIcon(icon2);
		} else {

		}
		return this;
	}

}
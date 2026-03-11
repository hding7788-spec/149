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

public class TpTypeRenderer extends JPanel implements TreeCellRenderer {
	private static final long serialVersionUID = 1L;

	protected JLabel icon;
	protected JLabel text;
	private Icon icon1 = new ImageIcon(
			TpTypeRenderer.class.getResource("/image/close.png"));
	private Icon icon3 = new ImageIcon(
			TpTypeRenderer.class.getResource("/image/open.png"));

	public TpTypeRenderer() {

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
		text.setPreferredSize(new Dimension(200, 25));
		TpTreeNode tpTreeNode = (TpTreeNode) value;
		if (isSelected && tpTreeNode.isLeaf()) {
			text.setBackground(UIManager.getColor("Tree.selectionBackground"));
		} else {
			text.setBackground(UIManager.getColor("Tree.textBackground"));
		}

		text.setText(tpTreeNode.getName());
		if (expanded) {
			icon.setIcon(icon3);
		} else {
			icon.setIcon(icon1);
		}
		if (tpTreeNode.isLeaf()) {
			icon.setIcon(icon3);
		}
		return this;
	}
}
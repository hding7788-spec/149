package com.glaway.mpm.qmIntf.dashboard;

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

import com.glaway.mpm.model.UnSDashboard;

public class UnSDashboardTreeRenderer extends JPanel implements TreeCellRenderer {
	private static final long serialVersionUID = 1L;

	protected JLabel icon;
	protected JLabel text;
	private Icon icon1 = new ImageIcon(UnSDashboardTreeRenderer.class.getResource("/image/close.png"));
	private Icon icon2 = new ImageIcon(UnSDashboardTreeRenderer.class.getResource("/image/file.png"));
	private Icon icon3 = new ImageIcon(UnSDashboardTreeRenderer.class.getResource("/image/open.png"));

	public UnSDashboardTreeRenderer() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		icon = new JLabel() {
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
		if (value instanceof UnSDashboardTreeNode) {
			if (expanded) {
				icon.setIcon(icon3);
			} else {
				icon.setIcon(icon1);
			}
			UnSDashboardTreeNode epTreeNode = (UnSDashboardTreeNode) value;
			epTreeNode.setSelected(isSelected);
			text.setText(epTreeNode.getName());
		} else if (value instanceof UnSDashboardNode) {
			UnSDashboardNode epNode = (UnSDashboardNode) value;
			UnSDashboard dashboard = epNode.getUnSDashboard();
			if (epNode.isParent()) {
				text.setText(epNode.getName()+"_"+dashboard.getEquipmentType()+"_"+dashboard.getCsize()+"_"+dashboard.getMindex());
			} else {
				text.setText(dashboard.getNumber() + "_" + dashboard.getEquipmentNumber());
			}
			epNode.setSelected(isSelected);
			icon.setIcon(icon2);
		}
		return this;
	}

}

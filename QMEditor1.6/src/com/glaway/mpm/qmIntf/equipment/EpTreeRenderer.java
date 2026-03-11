package com.glaway.mpm.qmIntf.equipment;

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

import com.glaway.mpm.model.Equipment;

public class EpTreeRenderer extends JPanel implements TreeCellRenderer {
	private static final long serialVersionUID = 1L;

	protected JLabel icon;
	protected JLabel text;
	private Icon icon1 = new ImageIcon(
			EpTreeRenderer.class.getResource("/image/close.png"));
	private Icon icon2 = new ImageIcon(
			EpTreeRenderer.class.getResource("/image/file.png"));
	private Icon icon3 = new ImageIcon(
			EpTreeRenderer.class.getResource("/image/open.png"));

	public EpTreeRenderer() {
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
		if (value instanceof EpTreeNode) {
			if (expanded) {
				icon.setIcon(icon3);
			} else {
				icon.setIcon(icon1);
			}
			EpTreeNode epTreeNode = (EpTreeNode) value;
			epTreeNode.setSelected(isSelected);
			text.setText(epTreeNode.getName());
		} else if (value instanceof EpNode) {
			EpNode epNode = (EpNode) value;
			Equipment equipment = epNode.getEquipment();
			if (epNode.isParent()) {
				text.setText(equipment.getName() + "_" + equipment.getMindex()+"_"+equipment.getCsize());
			} else {
				text.setText(equipment.getNumber() + "_" + equipment.getEquipmentNumber());
			}
			epNode.setSelected(isSelected);
			icon.setIcon(icon2);
		}
		return this;
	}

}

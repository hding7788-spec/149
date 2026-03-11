package com.glaway.mpm.qmIntf.pdName;

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

public class PdNameTreeRenderer extends JPanel implements TreeCellRenderer {
	private static final long serialVersionUID = 1L;

	protected JLabel icon;
	protected JLabel text;
	private Icon icon1 = new ImageIcon(
			PdNameTreeRenderer.class.getResource("/image/close.png"));
	private Icon icon2 = new ImageIcon(
			PdNameTreeRenderer.class.getResource("/image/file.png"));
	private Icon icon3 = new ImageIcon(
			PdNameTreeRenderer.class.getResource("/image/open.png"));
	private Icon icon4 = new ImageIcon(PdNameTreeRenderer.class.getResource("/images/fujian.gif"));
	private Icon icon5 = new ImageIcon(PdNameTreeRenderer.class.getResource("/images/ppoint.gif"));
	public PdNameTreeRenderer() {
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
		if (value instanceof WorkShopNode) {
			if (expanded) {
				icon.setIcon(icon3);
			} else {
				icon.setIcon(icon1);
			}
			WorkShopNode workShopNode = (WorkShopNode) value;
			if (workShopNode.isRoot()) {
				text.setText(workShopNode.getName());
			} else {
				text.setText(workShopNode.getName());
			}
			workShopNode.setSelected(isSelected);

		} else if (value instanceof ShopTypeNode) {
			ShopTypeNode shopTypeNode = (ShopTypeNode) value;
			shopTypeNode.setSelected(isSelected);
			text.setText(shopTypeNode.getName());
			icon.setIcon(icon2);
		} else if(value instanceof SkillNode){
			SkillNode skillNode = (SkillNode) value;
			skillNode.setSelected(isSelected);
			text.setText(skillNode.getName());
			if (expanded) {
				icon.setIcon(icon4);
			} else {
				icon.setIcon(icon4);
			}
		} else if(value instanceof PdNameTypeNode){
			PdNameTypeNode type = (PdNameTypeNode) value;
			type.setSelected(isSelected);
			text.setText(type.getName());
			if (expanded) {
				icon.setIcon(icon3);
			} else {
				icon.setIcon(icon1);
			}
		}
		else if (value instanceof PdNameNode) {
			PdNameNode pdNameNode = (PdNameNode) value;
			pdNameNode.setSelected(isSelected);
			text.setText(pdNameNode.getName());
			if (expanded) {
				icon.setIcon(icon5);
			} else {
				icon.setIcon(icon5);
			}
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

package com.glaway.mpm.qmIntf.resourceTree;

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

import com.glaway.mpm.qmIntf.resourceTree.model.FkNode;
import com.glaway.mpm.qmIntf.resourceTree.model.FkTreeNode;
import com.glaway.mpm.qmIntf.resourceTree.model.KtNode;
import com.glaway.mpm.qmIntf.resourceTree.model.KtTreeNode;
import com.glaway.mpm.qmIntf.resourceTree.model.MstNode;
import com.glaway.mpm.qmIntf.resourceTree.model.MstTreeNode;
import com.glaway.mpm.qmIntf.resourceTree.model.ToolNode;
import com.glaway.mpm.qmIntf.resourceTree.model.ToolTreeNode;

public class ResourceTreeRenderer extends JPanel implements TreeCellRenderer {
	private static final long serialVersionUID = 1L;

	protected JLabel icon;
	protected JLabel text;
	private Icon icon1 = new ImageIcon(
			ResourceTreeRenderer.class.getResource("/image/close.png"));
	private Icon icon2 = new ImageIcon(
			ResourceTreeRenderer.class.getResource("/image/file.png"));
	private Icon icon3 = new ImageIcon(
			ResourceTreeRenderer.class.getResource("/image/open.png"));

	public ResourceTreeRenderer() {
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
		text.setPreferredSize(new Dimension(200, 18));
		setBackground(Color.WHITE);
		if (isSelected) {
			text.setBackground(UIManager.getColor("Tree.selectionBackground"));
		} else {
			text.setBackground(UIManager.getColor("Tree.textBackground"));
		}

		if (value instanceof FkTreeNode) {
			if (expanded) {
				icon.setIcon(icon3);
			} else {
				icon.setIcon(icon1);
			}
			FkTreeNode fkTreeNode = (FkTreeNode) value;
			fkTreeNode.setSelected(isSelected);
			text.setText(fkTreeNode.getName());
		} else if (value instanceof ToolTreeNode) {
			if (expanded) {
				icon.setIcon(icon3);
			} else {
				icon.setIcon(icon1);
			}
			ToolTreeNode toolTreeNode = (ToolTreeNode) value;
			toolTreeNode.setSelected(isSelected);
			text.setText(toolTreeNode.getName());
		} else if (value instanceof KtTreeNode) {
			if (expanded) {
				icon.setIcon(icon3);
			} else {
				icon.setIcon(icon1);
			}
			KtTreeNode treeNode = (KtTreeNode) value;
			treeNode.setSelected(isSelected);
			text.setText(treeNode.getName());
		} else if (value instanceof MstTreeNode) {
			if (expanded) {
				icon.setIcon(icon3);
			} else {
				icon.setIcon(icon1);
			}
			MstTreeNode treeNode = (MstTreeNode) value;
			treeNode.setSelected(isSelected);
			text.setText(treeNode.getName());
		} else if (value instanceof FkNode) {
			FkNode fkNode = (FkNode) value;
			fkNode.setSelected(isSelected);
			String number = fkNode.getNumber();
			if(number.startsWith("A%")) {
				number = number.replace("A%", "");
			}
			if(number.startsWith("B%")) {
				number = number.replace("B%", "");
			}
			text.setText(number + "_" + fkNode.getName());
			icon.setIcon(icon2);
		} else if (value instanceof ToolNode) {
			ToolNode toolpNode = (ToolNode) value;
			toolpNode.setSelected(isSelected);
			text.setText(toolpNode.getNumber() + "_" + toolpNode.getName());
			icon.setIcon(icon2);
		} else if (value instanceof KtNode) {
			KtNode node = (KtNode) value;
			node.setSelected(isSelected);
			text.setText(node.getNumber() + "_" + node.getName());
			icon.setIcon(icon2);
		} else if (value instanceof MstNode) {
			MstNode node = (MstNode) value;
			node.setSelected(isSelected);
			text.setText(node.getNumber() + "_" + node.getName());
			icon.setIcon(icon2);
		} else {
			if (expanded) {
				icon.setIcon(icon3);
			} else {
				icon.setIcon(icon1);
			}
			text.setText("工装树");
		}
		return this;
	}
}

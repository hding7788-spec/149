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

import com.glaway.mpm.qmIntf.resourceTree.model.KtNode;
import com.glaway.mpm.qmIntf.resourceTree.model.KtTreeNode;

public class KtTreeRenderer extends JPanel implements TreeCellRenderer {

	private static final long serialVersionUID = 1L;
	protected JLabel icon;
	protected JLabel text;
	private Icon icon1 = new ImageIcon(KtTreeRenderer.class.getResource("/image/close.png"));
	private Icon icon2 = new ImageIcon(KtTreeRenderer.class.getResource("/image/file.png"));
	private Icon icon3 = new ImageIcon(KtTreeRenderer.class.getResource("/image/open.png"));

	public KtTreeRenderer() {
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

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		text.setPreferredSize(new Dimension(200, 18));
		setBackground(Color.WHITE);
		if (selected) {
			text.setBackground(UIManager.getColor("Tree.selectionBackground"));
		} else {
			text.setBackground(UIManager.getColor("Tree.textBackground"));
		}

		if (value instanceof KtTreeNode) {
			if (expanded) {
				icon.setIcon(icon3);
			} else {
				icon.setIcon(icon1);
			}
			KtTreeNode treeNode = (KtTreeNode) value;
			treeNode.setSelected(selected);
			text.setText(treeNode.getName());
		} else if (value instanceof KtNode) {
			KtNode node = (KtNode) value;
			node.setSelected(selected);
			text.setText(node.getName() + "_" + node.getKnifeTool().getCmat()+"_"+node.getKnifeTool().getRkzj()
					+"_"+node.getKnifeTool().getJczj()+"_"+node.getKnifeTool().getRkcd()
					+"_"+node.getKnifeTool().getZcd()+"_"+node.getKnifeTool().getGc());
			icon.setIcon(icon2);
		}

		return this;
	}

}

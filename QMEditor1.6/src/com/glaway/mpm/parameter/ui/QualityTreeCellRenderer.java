package com.glaway.mpm.parameter.ui;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.glaway.mpm.parameter.model.tree.XWTreeNode;

/**
 * 检验特性树渲染器
 *
 */
public class QualityTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, this.selected, expanded, leaf, row, hasFocus);
		if (sel) {
			setForeground(getTextSelectionColor());
		} else {
			setForeground(getTextNonSelectionColor());
		}

		if ((value instanceof XWTreeNode)) {
			XWTreeNode node = (XWTreeNode) value;
			setText(node.getDisplayName());
			setToolTipText(node.getTipNoteText());
		}
		this.selected = sel;
		return this;
	}
}

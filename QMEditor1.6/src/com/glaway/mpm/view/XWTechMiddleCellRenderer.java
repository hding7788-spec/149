package com.glaway.mpm.view;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpTecnicsNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpTreeRenderer;
import com.glaway.mpm.qmIntf.viewPanel.CmpTreeNode;
import com.glaway.mpm.qmIntf.viewPanel.IMCmpTreeNode;

public class XWTechMiddleCellRenderer extends DefaultTreeCellRenderer {
	
	private Icon icon1 = new ImageIcon(
			DpTreeRenderer.class.getResource("/image/technics.gif"));
	private Icon icon2 = new ImageIcon(
			DpTreeRenderer.class.getResource("/image/step.gif"));
	private Icon icon3 = new ImageIcon(
			DpTreeRenderer.class.getResource("/images/zhongjian.gif"));

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, this.selected,
				expanded, leaf, row, hasFocus);
		
		if ((value instanceof DpTecnicsNode)) {
			setIcon(icon1);
		}else if ((value instanceof IMCmpTreeNode)) {
			setIcon(icon2);
		}else if ((value instanceof CmpTreeNode)) {
			setIcon(icon3);
		}
		if (sel) {
			setForeground(getTextSelectionColor());
		} else {
			setForeground(getTextNonSelectionColor());
		}
		this.selected = sel;
		return this;
	}
}

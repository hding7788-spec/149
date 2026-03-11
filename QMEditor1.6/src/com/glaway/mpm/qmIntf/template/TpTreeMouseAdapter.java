package com.glaway.mpm.qmIntf.template;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

public class TpTreeMouseAdapter extends MouseAdapter {
	private JTree tree;

	public TpTreeMouseAdapter(JTree tree) {
		this.tree = tree;
	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int row = tree.getRowForLocation(x, y);
		TreePath path = tree.getPathForRow(row);
		if (path != null) {
			Object object = path.getLastPathComponent();
			if (object instanceof TpTreeNode) {
				if (!tree.isCollapsed(path)) {
					tree.collapsePath(path);
				} else {
					tree.expandPath(path);
				}
			}
		}
	}
}

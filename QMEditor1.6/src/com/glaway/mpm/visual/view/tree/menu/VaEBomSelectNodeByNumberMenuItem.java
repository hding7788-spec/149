/**
 * <br>Created on 2011-3-28
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.visual.view.tree.menu;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.media.j3d.BoundingBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.tree.TreePath;
import javax.vecmath.Point3d;

import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;

public class VaEBomSelectNodeByNumberMenuItem extends VaMenuItem {

	private static final long	serialVersionUID	= -8396429397442896329L;
	private VaTree				tree;
	private static VaLogger logger = VaLogger.getLogger(VaEBomSelectNodeByNumberMenuItem.class);
	public VaEBomSelectNodeByNumberMenuItem(VaTree tree) {
		this.tree = tree;
		setText("批量选择");
		// setIconStr("copy.gif");
	}

	/**
	 * @param evt
	 */
	@Override
	protected void actionPerformed(ActionEvent evt) {
		List<VaTreeNode> nodeList = new ArrayList<VaTreeNode>();
		TreePath[] paths = tree.getSelectionPaths();
		VaTreeNode node = (VaTreeNode) paths[0].getLastPathComponent();
		
		VaTree ebomTree = tree.getLinkedEBomTree();

		VaTreeNode ebomTreeRoot = ebomTree.getRoot();
		Enumeration<VaTreeNode> child = ebomTreeRoot.breadthFirstEnumeration();
		while (child.hasMoreElements()) {
			VaTreeNode vaTreeNode = (VaTreeNode) child.nextElement();
			if (compare(node,vaTreeNode)) {
				TreePath tp = new TreePath(vaTreeNode.getPath());
				tree.setSelectionPath(tp);
				break;
			}
		}
		tree.repaint();
	}

	private boolean compare(VaTreeNode e, VaTreeNode p) {
		
		String eNumber = e.getPart().getNumber();
		String pNumber = p.getPart().getNumber();
		if (eNumber != null && pNumber != null && eNumber.equals(pNumber)) {
			return true;
		} else {
			return false;
		}
	}
}

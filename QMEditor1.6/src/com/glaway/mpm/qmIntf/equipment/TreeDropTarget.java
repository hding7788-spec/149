package com.glaway.mpm.qmIntf.equipment;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class TreeDropTarget implements DropTargetListener {

	DropTarget target;

	JTree targetTree;

	public TreeDropTarget(JTree tree) {
		targetTree = tree;
		target = new DropTarget(targetTree, this);
	}

	/*
	 * Drop Event Handlers
	 */
	private TreeNode getNodeForEvent(DropTargetDragEvent dtde) {
		Point p = dtde.getLocation();
		DropTargetContext dtc = dtde.getDropTargetContext();
		JTree tree = (JTree) dtc.getComponent();
		TreePath path = tree.getClosestPathForLocation(p.x, p.y);
		return (TreeNode) path.getLastPathComponent();
	}

	public void dragEnter(DropTargetDragEvent dtde) {
		// TreeNode node = getNodeForEvent(dtde);
		// System.out.println(((EpTreeNode)node).getName());
		// if (node.isLeaf()) {
		// dtde.acceptDrag(dtde.getDropAction());
		// } else {
		// dtde.rejectDrag();
		// }

	}

	public void dragOver(DropTargetDragEvent dtde) {
//		EpTreeNode node = (EpTreeNode) getNodeForEvent(dtde);
//		node.setSelected(true);
//		DropTargetContext dtc = dtde.getDropTargetContext();
//		EpTree tree = (EpTree) dtc.getComponent();
//		Enumeration nodes = tree.getRoot().children();
//		while (nodes.hasMoreElements()) {
//			EpTreeNode epTreeNode = (EpTreeNode) nodes.nextElement();
//			epTreeNode.setSelected(true);
//		}
//		System.out.println(tree.getRoot().getIndex(node));
//		tree.repaint();
		// if (node.isLeaf()) {
		// dtde.acceptDrag(dtde.getDropAction());
		// } else {
		// dtde.rejectDrag();
		// }
	}

	public void dragExit(DropTargetEvent dte) {
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {

	}

	public void drop(DropTargetDropEvent dtde) {
		Point pt = dtde.getLocation();
		DropTargetContext dtc = dtde.getDropTargetContext();
		EpTree epTree = (EpTree) dtc.getComponent();
		TreePath targetPath = epTree.getClosestPathForLocation(pt.x, pt.y);
		DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) targetPath
				.getLastPathComponent();
		try {
			Transferable tr = dtde.getTransferable();
			DataFlavor[] flavors = tr.getTransferDataFlavors();
			for (int i = 0; i < flavors.length; i++) {
				if (tr.isDataFlavorSupported(flavors[i])) {
					dtde.acceptDrop(dtde.getDropAction());
					TreePath p = (TreePath) tr.getTransferData(flavors[i]);
					EpTreeNode node = (EpTreeNode) p.getLastPathComponent();
					EpTreeNode rootNode = epTree.getRoot();
					// int index = getIndexof(node, epTree);
					int targetIndex = rootNode.getIndex(targetNode);
					if (!targetNode.isLeaf()) {
						rootNode.insert(node, 0);
					} else {
						rootNode.insert(node, targetIndex + 1);
					}
					// System.out.println(index + ":" + targetIndex);
					// if (index <= targetIndex) {

					// } else {
					// rootNode.insert(node, targetIndex);
					// }
					dtde.dropComplete(true);
					epTree.updateUI();
					return;
				}
			}
			dtde.rejectDrop();
		} catch (Exception e) {
			e.printStackTrace();
			dtde.rejectDrop();
		}
	}

	// public int getIndexof(EpTreeNode epTreeNode, EpTree epTree) {
	// EpTreeNode rootNode = epTree.getRoot();
	// Enumeration nodes = rootNode.children();
	// while (nodes.hasMoreElements()) {
	// EpTreeNode node = (EpTreeNode) nodes.nextElement();
	// if (node.getName().equals(epTreeNode.getName())) {
	// return rootNode.getIndex(node);
	// }
	// }
	// return -1;
	// }
}
/**
 * @author wanghaoyu
 */
package com.glaway.mpm.visual.view.tree;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.media.j3d.BoundingBox;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.vecmath.Point3d;

import com.glaway.mpm.qmIntf.participatePart.ParticipatePartAddDialog;
import com.glaway.mpm.visual.log.VaLogger;

/**
 *
 *
 *
 * @author wanghaoyu
 */
public class VaTreeSelectionListener implements TreeSelectionListener {
	private VaTreeLinkage			linkage;

	private static final VaLogger	log	= VaLogger.getLogger(VaTreeSelectionListener.class);

	public VaTreeSelectionListener(VaTreeLinkage linkage) {
		this.linkage = linkage;
	}

	public VaTreeSelectionListener() {

	}

	public void valueChanged(TreeSelectionEvent e) {
		log.debug("TreeSelectionEvent...");
		TreePath[] theTreePaths = e.getPaths();
		ArrayList<VaTreeNode> theSelectedNodes = new ArrayList<VaTreeNode>();
		// Treat the removed elements first
		for (int index = 0; index < theTreePaths.length; index++) {
			TreePath theCurrentTreePath = theTreePaths[index];
			VaTreeNode theNode = (VaTreeNode) theCurrentTreePath.getLastPathComponent();
			if (!e.isAddedPath(index))
				if(theNode.isLeaf() || theNode.getPart().getNumber().endsWith(".PRT")){
					//log.debug("theNode..."+theNode);
				setSelectionChangedRecursively(theNode, false);
				}else{
					unSelectChildrenPart(theNode);
				}
			else
				theSelectedNodes.add(theNode);
		}

		// Treat the new selection
		Iterator theSelectedNodesIt = theSelectedNodes.iterator();
		while (theSelectedNodesIt.hasNext()) {
			VaTreeNode tNode = (VaTreeNode) theSelectedNodesIt.next();
			if (tNode.isLeaf() || tNode.getPart().getNumber().endsWith(".PRT")) {
				//log.debug("tNode..."+tNode);
				setSelectionChangedRecursively(tNode, true);
			} else {
				selectChildrenPart(tNode);
			}

		}

		ArrayList<VaTreeNode> theSelectedNodeList = new ArrayList<VaTreeNode>();
		JTree tree = (JTree) e.getSource();
		TreePath[] selectionPaths = tree.getSelectionPaths();
		if (selectionPaths == null)
			return;

		for (int index = 0; index < selectionPaths.length; index++) {
			VaTreeNode node = (VaTreeNode) selectionPaths[index].getLastPathComponent();
			// if (node.isLeaf())
			theSelectedNodeList.add(node);
		}
		if (linkage != null)
			linkage.valueChange(theSelectedNodeList);
	}

	private void setSelectionChangedRecursively(VaTreeNode node, boolean selectionMode) {
		//log.debug("setSelectionChangedRecursively..."+node);
		if (node.isLeaf() || node.getPart().getNumber().endsWith(".PRT")) {
			try {
				//log.debug("((VaTreeNode) node).get_pviewShapeInstance()..."+((VaTreeNode) node).get_pviewShapeInstance());
				if (!(((VaTreeNode) node).get_pviewShapeInstance() == null))
					((VaTreeNode) node).get_pviewShapeInstance().SetHighlight(selectionMode);

				// if (selectionMode) {
				// Vector bboxs = node.getBboxes();
				// VaMainframe main = VaMainframe.getInstance();
				// VaPartDialog main = VaPartDialog.getInstance();
				//
				//
				// ParticipatePartAddDialog main =
				// ParticipatePartAddDialog.getInstance();
				// if (bboxs != null) {
				// for (Object obj : bboxs) {
				// BoundingBox bbox = (BoundingBox) obj;
				// Point3d lower = new Point3d();
				// Point3d upper = new Point3d();
				// bbox.getLower(lower);
				// bbox.getUpper(upper);
				//
				// main.setX1(String.valueOf(lower.getX() * 1000.0));
				// main.setX2(String.valueOf(upper.getX() * 1000.0));
				// main.setY1(String.valueOf(lower.getY() * 1000.0));
				// main.setY2(String.valueOf(upper.getY() * 1000.0));
				// main.setZ1(String.valueOf(lower.getZ() * 1000.0));
				// main.setZ2(String.valueOf(upper.getZ() * 1000.0));
				// }
				// }
				// main.setReferenceString(node.getPart().getNumber());
				// main.setInstanceString(node.getOccId());
				// }
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void selectChildrenPart(VaTreeNode node) {
		Enumeration<VaTreeNode> children = node.children();
		while (children.hasMoreElements()) {
			VaTreeNode child = children.nextElement();
			if (child.isLeaf() || child.getPart().getNumber().endsWith(".PRT")) {
				setSelectionChangedRecursively(child, true);
			} else {
				selectChildrenPart(child);
			}
		}
	}

	private void unSelectChildrenPart(VaTreeNode node) {
		Enumeration<VaTreeNode> children = node.children();
		while (children.hasMoreElements()) {
			VaTreeNode child = children.nextElement();
			if (child.isLeaf() || child.getPart().getNumber().endsWith(".PRT")) {
				setSelectionChangedRecursively(child, false);
			} else {
				unSelectChildrenPart(child);
			}
		}
	}
}

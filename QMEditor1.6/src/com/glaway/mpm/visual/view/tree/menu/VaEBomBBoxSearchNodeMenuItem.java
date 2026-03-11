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

public class VaEBomBBoxSearchNodeMenuItem extends VaMenuItem {

	private static final long	serialVersionUID	= -8396429397442896329L;
	private VaTree				tree;
	private static VaLogger logger = VaLogger.getLogger(VaEBomBBoxSearchNodeMenuItem.class);
	public VaEBomBBoxSearchNodeMenuItem(VaTree tree) {
		this.tree = tree;
		setText("空间搜寻");
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

		JOptionPane.showMessageDialog(null, "node.getBboxes() : " + node.getBboxes() );
		
//		for (int i = 0; i < node.getBboxes().size(); i++) {
//			BoundingBox b = (BoundingBox)node.getBboxes().get(i);
//			Point3d lower = new Point3d();
//			Point3d upper = new Point3d();
//			b.getLower(lower);
//			b.getUpper(upper);
//			
//			JOptionPane.showMessageDialog(null, "Xmin : "+lower.getX()+", Ymin : "+lower.getY()+", "+"Zmin : "+lower.getZ() + "\n" + "Xmax : "+upper.getX()+", Ymax : "+upper.getY()+", "+"Zmax : "+upper.getZ());
//		}		
		
		VaTree ebomTree = tree.getLinkedEBomTree();
//		JDialog d = new JDialog();
//		d.add(new JScrollPane(ebomTree));
//		d.setVisible(true);
		VaTreeNode ebomTreeRoot = ebomTree.getRoot();
		Enumeration<VaTreeNode> child = ebomTreeRoot.depthFirstEnumeration();
		while (child.hasMoreElements()) {
			VaTreeNode vaTreeNode = (VaTreeNode) child.nextElement();
			if (compare(vaTreeNode, node)) {
//				BoundingBox bbox = (BoundingBox) vaTreeNode.getBboxes().get(0);
//				List<BoundingBox> bboxs = new ArrayList<BoundingBox>();
//				bboxs.add(bbox);
				check(ebomTreeRoot, vaTreeNode.getBboxes());
				break;
			}
		}
		tree.repaint();
	}

	private boolean compare(VaTreeNode e, VaTreeNode p) {
		String ePath = e.getOccpath();
		String pPath = p.getOccpath();
		logger.debug("[[[[[[[[[[[[[[[[[ePath:" + ePath);
		logger.debug("[[[[[[[[[[[[[[[[[pPath:" + pPath);
		if (ePath != null && pPath != null && ePath.indexOf('+') > -1 && pPath.indexOf('+') > -1
				&& ePath.substring(ePath.indexOf('+')).equals(pPath.substring(pPath.indexOf('+')))) {
			logger.debug("]]]]]]]]true");
			return true;

		} else {
			logger.debug("]]]]]]]]false");
			return false;
		}
	}

	private void getBondingBoxes(VaTreeNode node, String referenceStr, String instanceStr, List<BoundingBox> bboxs) {
		boolean isInstanceStrNull = true;
		if (instanceStr != null && instanceStr.trim().length() > 0) {
			isInstanceStrNull = false;
		}
		if (node.getPart().getNumber().equals(referenceStr)) {
			Vector vec = node.getBboxes();
			if (!isInstanceStrNull) {
				if (node.getOccId().equals(instanceStr)) {
					if (vec != null && vec.size() >= 1) {
						BoundingBox box = (BoundingBox) vec.get(0);
						if (box != null) {
							Point3d lower = new Point3d();
							Point3d upper = new Point3d();
							box.getLower(lower);
							box.getUpper(upper);
							BoundingBox boxtoAdd = new BoundingBox(lower, upper);
							bboxs.add(boxtoAdd);
						}
					}
				}
			} else {
				if (vec != null && vec.size() >= 1) {
					BoundingBox box = (BoundingBox) vec.get(0);
					if (box != null) {
						Point3d lower = new Point3d();
						Point3d upper = new Point3d();
						box.getLower(lower);
						box.getUpper(upper);
						BoundingBox boxtoAdd = new BoundingBox(lower, upper);
						bboxs.add(boxtoAdd);
					}
				}
			}
		}
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			VaTreeNode temp = (VaTreeNode) node.getChildAt(i);
			getBondingBoxes(temp, referenceStr, instanceStr, bboxs);
		}
	}

	private void check(VaTreeNode node, List<BoundingBox> bboxs) {
		Vector vec = node.getBboxes();
		node.setSelected(false);
		checkLinkPbomNode(node);
		if (vec != null) {
			BoundingBox tocheckbbox = (BoundingBox) vec.get(0);
			for (BoundingBox box : bboxs) {
				// if (tocheckbbox.intersect(box)) {
				if (box.intersect(tocheckbbox)) {
					node.setSelected(true);
					checkLinkPbomNode(node);
				}
			}
		}
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			VaTreeNode child = (VaTreeNode) node.getChildAt(i);
			check(child, bboxs);
		}
	}

	private void checkLinkPbomNode(VaTreeNode ebomNode) {
		VaTreeNode ebomTreeRoot = tree.getRoot();
		Enumeration<VaTreeNode> child = ebomTreeRoot.depthFirstEnumeration();
		while (child.hasMoreElements()) {
			VaTreeNode vaTreeNode = (VaTreeNode) child.nextElement();
			if (compare(vaTreeNode, ebomNode)) {
				vaTreeNode.setSelected(ebomNode.isSelected());
				break;
			}
		}
	}
}

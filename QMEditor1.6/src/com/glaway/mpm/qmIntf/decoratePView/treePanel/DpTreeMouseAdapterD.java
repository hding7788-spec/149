package com.glaway.mpm.qmIntf.decoratePView.treePanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.tree.TreePath;

import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;

public class DpTreeMouseAdapterD extends MouseAdapter {

	private static VaLogger logger = VaLogger.getLogger(DpTreeMouseAdapter.class);

	private VaTree tree;

	private VaTree ebomTree;

	public DpTreeMouseAdapterD(VaTree tree ,VaTree ebom) {
		this.tree = tree;
		this.ebomTree = ebom;
	}

	private static boolean setNodeSelectUtil(VaTreeNode vaTreeNode, String number, String occId, boolean flag) {
		Enumeration<VaTreeNode> childs = vaTreeNode.children();
		while (childs.hasMoreElements()) {
			VaTreeNode treeNode = childs.nextElement();
			logger.debug("part number===" + treeNode.getPart().getNumber());
			logger.debug("number===" + number);
			if ((treeNode.getPart().getNumber() + "").equals(number)) {
				for (String str : occId.split(",")) {
					if (str.equals(treeNode.getOccId())) {
						logger.debug("part occid===" + treeNode.getOccId());
						logger.debug("occid===" + occId);
						treeNode.setSelected(flag);
						return true;
					} else {
						continue;
					}
				}
				return false;

			} else {
				if (setNodeSelectUtil(treeNode, number, occId, flag)) {
					break;
				} else {
					continue;
				}
			}
		}
		return false;
	}

	public void setVaTreeNodeSelect(String number, String occId, boolean flag) {

		if (this.ebomTree != null) {
			logger.debug("occId====" + occId);
			setNodeSelectUtil(this.ebomTree.getRoot(), number, occId, flag);

		} else {
			logger.error("vaTree==null");
		}
	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int row = tree.getRowForLocation(x, y);
		TreePath path = tree.getPathForRow(row);
		if (path != null) {
			Object object = path.getLastPathComponent();
			if (object instanceof DpStepNode) {
				DpStepNode node = (DpStepNode) object;
				node.setSelected(!node.isSelected());
				Enumeration<Object> element = node.children();
				while (element.hasMoreElements()) {
					DpPaceNode paceNode = (DpPaceNode) element.nextElement();
					for (DpPartNode temp : paceNode.getPace().getParts()) {
						setVaTreeNodeSelect(temp.getPart().getNumber(), temp.getOccId(),
								node.isSelected());
					}
					paceNode.setSelected(node.isSelected());
				}
			} else if (object instanceof DpPaceNode) {
				DpPaceNode node = (DpPaceNode) object;
				boolean flag = !node.isSelected();
				node.setSelected(flag);
				for (DpPartNode temp : node.getPace().getParts()) {
					setVaTreeNodeSelect(temp.getPart().getNumber(), temp.getOccId(), flag);
				}

				DpStepNode stepNode = (DpStepNode) node.getParent();

				Enumeration<Object> element = stepNode.children();

				if (flag) {
					stepNode.setSelected(flag);
				} else {
					flag: while (true) {
						while (element.hasMoreElements()) {
							DpPaceNode paceNode = (DpPaceNode) element.nextElement();
							if (!paceNode.isSelected()) {
								continue;
							} else {
								stepNode.setSelected(!flag);
								break flag;
							}
						}
						stepNode.setSelected(flag);
						break;
					}
				}

			}
			tree.revalidate();
			tree.repaint();
		}
	}
}

package com.glaway.mpm.visual.view.tree;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class VaTreeModelLinkage extends VaAbstractTreeLinkage<VaTreeNode> {
	public VaTreeModelLinkage(Comparator comtor) {
		super(comtor);
	}

	@Override
	public void doLinkage(JTree tree, List<VaTreeNode> nodes) {
		for (VaTreeNode vaTreeNode : nodes) {

			VaTreeNode root = (VaTreeNode) tree.getModel().getRoot();
			Enumeration<VaTreeNode> treeNodes = root.breadthFirstEnumeration();
			while (treeNodes.hasMoreElements()) {
				VaTreeNode treeNode = treeNodes.nextElement();

				if (treeNode.getPart().getNumber()
						.equals(vaTreeNode.getPart().getNumber())) {
					System.out.println(treeNode.getOccId());
					System.out.println(vaTreeNode.getOccId());
					if (treeNode.getOccId().equals(vaTreeNode.getOccId())) {
						System.out.println("Check : check");
						treeNode.setSelected(vaTreeNode.isSelected());
						continue;
					}
				}
			}
		}
	}

	public void setComparator(Comparator comtor) {
		super.comtor = comtor;
	}

	@Override
	public void doLinkageNode(List<VaTreeNode> nodes, List<VaTreeNode> nodes2) {

	}

}

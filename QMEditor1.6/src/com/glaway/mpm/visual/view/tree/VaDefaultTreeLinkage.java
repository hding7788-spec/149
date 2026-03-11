/**
 * <br>Created on 2011-3-18
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.visual.view.tree;

import java.util.Comparator;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * <br>
 * Created on 2011-3-18
 *
 * @author Alex.Huang - ����
 */
public class VaDefaultTreeLinkage extends VaAbstractTreeLinkage<VaTreeNode> {
	public VaDefaultTreeLinkage(Comparator comtor) {
		super(comtor);
	}

	@Override
	public void doLinkage(JTree tree, List<VaTreeNode> nodes) {
		TreePath[] paths = new TreePath[nodes.size()];
		int i = 0;
		for (VaTreeNode n : nodes) {
			paths[i] = new TreePath(n.getPath());
			i++;
		}
		tree.setSelectionPaths(paths);
		if (paths.length > 0)
			tree.scrollPathToVisible(paths[paths.length - 1]);

	}

	public void setComparator(Comparator comtor) {
		super.comtor = comtor;
	}

	@Override
	public void doLinkageNode(List<VaTreeNode> nodes, List<VaTreeNode> nodes2) {

	}

}

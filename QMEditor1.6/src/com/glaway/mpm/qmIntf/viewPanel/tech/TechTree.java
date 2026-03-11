package com.glaway.mpm.qmIntf.viewPanel.tech;

import java.util.Map;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.glaway.mpm.qmIntf.common.model.CommonTree;
import com.glaway.mpm.qmIntf.resourceTree.model.FkNode;

public class TechTree extends CommonTree {
	private static final long serialVersionUID = 1L;
	private TechTreeRootNode root = null;

	public TechTree(Map<String, String> numbers) {
		super();
		setCellRenderer(new TechTreeRenderer(numbers));
		addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = e.getPath();
				if (path != null) {
					Object value = path.getLastPathComponent();
					if (value instanceof FkNode) {
					}
				}
			}
		});
		setRootVisible(false);
	}

	public TechTreeRootNode getRoot() {
		return root;
	}

	public void setRoot(TechTreeRootNode root) {
		this.root = root;
		((DefaultTreeModel) this.getModel()).setRoot(root);
	}
}

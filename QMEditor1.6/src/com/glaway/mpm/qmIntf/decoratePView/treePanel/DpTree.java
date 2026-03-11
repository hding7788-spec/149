package com.glaway.mpm.qmIntf.decoratePView.treePanel;

import java.awt.Color;

import javax.swing.JToolTip;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNodeMouseAdapter;

public class DpTree extends VaTree {
	private static final long serialVersionUID = 1L;
	private DpTecnicsNode root = null;
	private boolean showTooltip = false;
	private ToolTipManager ttm;

	public DpTree() {
		this(null);
	}

	public DpTree(DpTecnicsNode dictn) {
		super(dictn);
		getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		if (dictn != null)
			root = dictn;
		((DefaultTreeModel) getModel()).setRoot(root);
		this.setRootVisible(false);
		setCellRenderer(new DpTreeRenderer());
//		addMouseListener(new DpTreeNodeMouseAdapter(this));
//		addMouseListener(new VaTreeNodeMouseAdapter(this));
//		ttm = ToolTipManager.sharedInstance();
	}

}

package com.glaway.mpm.qmIntf.viewPanel.cmp;

import java.io.File;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.glaway.mpm.qmIntf.common.model.CommonTree;
import com.glaway.mpm.qmIntf.viewPanel.CreoModelPanel;
import com.glaway.mpm.qmIntf.viewPanel.tech.TechTreeRenderer;
import com.glaway.mpm.qmIntf.viewPanel.tech.TechTreeRootNode;
import com.glaway.mpm.util.WorkSpaceUtil;

public class CmpTree extends CommonTree {
	private static final long serialVersionUID = 1L;
	private TechTreeRootNode root = null;

	public CmpTree(Map<String, String> numbers) {
		super();
		setCellRenderer(new TechTreeRenderer(numbers));
		setRootVisible(false);

		getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = e.getPath();
				if (path != null) {
					Object value = path.getLastPathComponent();
					if (value instanceof CmpImageNode) {
						CmpImageNode imageNode = (CmpImageNode) value;
						String tempPath = WorkSpaceUtil.getCmpTmpDir();
						CreoModelPanel.image.setIcon(new ImageIcon(tempPath
								+ File.separator + imageNode.getName()
								+ File.separator + imageNode.getDir()));
					}
				}
			}
		});
	}

	public TechTreeRootNode getRoot() {
		return root;
	}

	public void setRoot(TechTreeRootNode root) {
		this.root = root;
		((DefaultTreeModel) this.getModel()).setRoot(root);
	}
}

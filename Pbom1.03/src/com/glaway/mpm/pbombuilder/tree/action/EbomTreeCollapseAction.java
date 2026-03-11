package com.glaway.mpm.pbombuilder.tree.action;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.util.CmUtil;

public class EbomTreeCollapseAction extends CmAction  {
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private ImageIcon expandImage = new ImageIcon(CmUtil.getImageFromServer("collapse.gif"));

	public EbomTreeCollapseAction(CmTree tree) {
		setIcon(expandImage);
		setToolTipText("全部收起");
		this.tree = tree;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		tree.collapse(tree.getRoot());
	}
}

package com.glaway.mpm.pbombuilder.tree.action;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.util.CmUtil;

public class EbomTreeExpandAction extends CmAction {

	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private ImageIcon expandImage = new ImageIcon(CmUtil.getImageFromServer("expand.gif"));

	public EbomTreeExpandAction(CmTree tree) {
		setIcon(expandImage);
		setToolTipText("全部展开");
		this.tree = tree;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		tree.expandAllLevels(tree.getRoot());
	}

}

package com.glaway.mpm.pbombuilder.tree.action;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.action.CmCommonSearchAction;
import com.glaway.mpm.pbombuilder.panel.CmMBomTreePanel;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.util.CmUtil;

public class PbomTreeSearchAction extends CmAction {
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private ImageIcon expandImage = new ImageIcon(CmUtil.getImageFromServer("search.png"));

	public PbomTreeSearchAction(CmTree tree) {
		this.tree = tree;
		setIcon(expandImage);
		setToolTipText("搜索");
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		CmCommonSearchAction.bomTreeSearchAction(CmMBomTreePanel.pbomSearchText.getText(), tree, "PBOM");
	}
}

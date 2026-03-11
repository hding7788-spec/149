package com.glaway.mpm.pbombuilder.tree.action;

import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;

import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.action.CmCommonPackageAction;
import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;

public class EbomTreeUnPackageAction  extends CmAction {
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private ImageIcon expandImage = new ImageIcon(CmUtil.getImageFromServer("unpackage.png"));
	private CmTree cmtree;

	public EbomTreeUnPackageAction(CmTree tree) {
		this.tree=tree;
		setIcon(expandImage);
		setToolTipText("全部拆包");
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		CmCommonPackageAction action=new CmCommonPackageAction();
		if("PBOM".equals(tree.getRoot().toString())){
			cmtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree();
		}
		else{
			cmtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();
		}
		if(action.uppackageAllNode(tree) && action.uppackageAllNode(cmtree)){
//			EbomTreeCancelAction.addPbomTreePackage(null, "unpackages",true);
			CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
			CmCommonStringUtil.sortTheTreeNode(cmtree.getRoot());
			tree.updateUI();
			cmtree.updateUI();
		}
	}
}

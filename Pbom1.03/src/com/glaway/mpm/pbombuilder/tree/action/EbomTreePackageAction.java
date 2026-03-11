package com.glaway.mpm.pbombuilder.tree.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.action.CmCommonPackageAction;
import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;

public class EbomTreePackageAction extends CmAction {
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private ImageIcon expandImage = new ImageIcon(CmUtil.getImageFromServer("package.png"));
	private CmTree cmtree;

	public EbomTreePackageAction(CmTree tree) {
		setIcon(expandImage);
		setToolTipText("全部打包");
		this.tree = tree;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		CmCommonPackageAction action=new CmCommonPackageAction();
		CmCommonNodeUtil common = new CmCommonNodeUtil();
		StringBuffer packageBuf = new StringBuffer(1024);
		if("PBOM".equals(tree.getRoot().toString())){
			common.clearBomSearchResult(tree.getRoot());
			action.checkThePackagePbomTree(tree.getRoot(), tree.getRoot(), packageBuf, new ArrayList<CmTreeNode>(),tree.getRoot().toString());
			cmtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree();
		}
		else{
			cmtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();
			common.clearBomSearchResult(cmtree.getRoot());
			action.checkThePackagePbomTree(cmtree.getRoot(), cmtree.getRoot(), packageBuf, new ArrayList<CmTreeNode>(),cmtree.getRoot().toString());
		}
		
		if(packageBuf.toString().length()>0){
			JOptionPane.showMessageDialog(tree.getRootPane(), packageBuf);
		}
		else{
			if("PBOM".equals(tree.getRoot().toString())){
				if(action.packageAllNode(tree)){
//					EbomTreeCancelAction.addPbomTreePackage(null, "package",true);
					CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
					tree.updateUI();
					if(action.packageAllNode(cmtree)){         //PBOM能打包，但是EBOM可能没有
						CmCommonStringUtil.sortTheTreeNode(cmtree.getRoot());
						cmtree.updateUI();
					}
				}else if(action.packageAllNode(cmtree)){
					CmCommonStringUtil.sortTheTreeNode(cmtree.getRoot());
					cmtree.updateUI();
				}
			}
			else{
				if(action.packageAllNode(cmtree)){
//					EbomTreeCancelAction.addPbomTreePackage(null, "package",true);
					CmCommonStringUtil.sortTheTreeNode(cmtree.getRoot());
					cmtree.updateUI();
					if(action.packageAllNode(tree)){
						CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
						tree.updateUI();
					}
				}else if(action.packageAllNode(tree)){
					CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
					tree.updateUI();
				}
			}
		}
	}
}

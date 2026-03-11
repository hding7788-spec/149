package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.action.CmCommonPackageAction;
import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

public class CmPackageNodeMenuItem  extends CmMenuItem {
	private static final long serialVersionUID = -8396429397442896329L;
	private CmTree tree;
	private CmTreeNode currNode;


	public CmPackageNodeMenuItem(CmTree tree, CmTreeNode currNode) {
		this.tree = tree;
		this.currNode=currNode;
		setText("打包");
		setIconStr("package.png");
		setEnabled(displayValidate(this.currNode));
	}
	
	private boolean displayValidate(CmTreeNode currNode) {
		TreePath[] paths = CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if (paths.length == 1 && null!=currNode.getParent()) {
			if("assistant".equals(currNode.getPart().getPartType())){
				return false;
			}
			else{ 
				List<CmTreeNode> childList = new ArrayList<CmTreeNode>();
				Enumeration children = currNode.getParent().children();
				while(children.hasMoreElements()){
					CmTreeNode child = (CmTreeNode) children.nextElement();
					childList.add(child);
				}
				return CmCommonNodeUtil.checkHasBrotherNodeIsInList(childList,currNode);
			}
		}
		else if(paths.length > 1){
			//选中多个节点，这多个节点都是相同的节点
			boolean flag = true;
			for(int i=0;i<paths.length;i++){
				for(int j=i+1;j<paths.length;j++){
					CmTreeNode nodei = (CmTreeNode)paths[i].getLastPathComponent();
					CmTreeNode nodej = (CmTreeNode)paths[j].getLastPathComponent();
					if("assistant".equals(nodei.getPart().getPartType()) || "assistant".equals(nodej.getPart().getPartType())){
						flag = false;
						break;
					}
					else if(!"middle".equals(nodei.getPart().getPartType()) 
							&& !"middle".equals(nodej.getPart().getPartType()) 
							&& !CmCommonNodeUtil.checkHasSameOidAndNotSameOccpath(nodei, nodej)){
						flag = false;
						break;
					}else if("middle".equals(nodei.getPart().getPartType()) 
							&& "middle".equals(nodej.getPart().getPartType())
							&& !CmCommonStringUtil.checkIsCommonMiddleNodeButNotSame(nodei, nodej)){
						flag = false;
						break;
					}
				}
			}
			return flag;
		}
		else{
			return false;
		}
	}
	
	@Override
	protected void actionPerformed(ActionEvent evt) {
		CmTree cmtree = null;
		StringBuffer errorBuf = new StringBuffer(1024);
		CmCommonPackageAction common = new CmCommonPackageAction();
		if("middle".equals(this.currNode.getPart().getPartType())){
			errorBuf = common.checkThePackageNode(this.tree.getRoot(), this.currNode,"打包");
			if(errorBuf.toString().length()>0){
				JOptionPane.showMessageDialog(tree.getRootPane(), errorBuf.toString());
			}
			else{
				common.packageOneNode(this.tree.getRoot(), this.currNode);
//				EbomTreeCancelAction.addPbomTreePackage(this.currNode,"package",false);
				CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
				tree.updateUI();
			}
		}
		else{
			if("PBOM".equals(tree.getRoot().toString())){
				errorBuf = common.checkThePackageNode(this.tree.getRoot(), this.currNode,"打包");
				cmtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree();
			}
			else{
				cmtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();
				errorBuf = common.checkThePackageNode(cmtree.getRoot(), this.currNode,"打包");
			}
			if(errorBuf.toString().length()>0){
				JOptionPane.showMessageDialog(tree.getRootPane(), errorBuf.toString());
			}
			else{
				common.packageOneNode(this.tree.getRoot(), this.currNode);
				common.packageOneNode(cmtree.getRoot(), this.currNode);
//				EbomTreeCancelAction.addPbomTreePackage(this.currNode,"package",false);
				CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
				CmCommonStringUtil.sortTheTreeNode(cmtree.getRoot());
				tree.updateUI();
				cmtree.updateUI();
			}
		}
	}
}

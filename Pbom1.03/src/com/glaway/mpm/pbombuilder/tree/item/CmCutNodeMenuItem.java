package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmTaskInfo;
import com.glaway.mpm.pbombuilder.util.PviewTask;

/**
 * 
 * Created on 2012-10-23
 * 
 * @author chenyunlong
 */
public class CmCutNodeMenuItem extends CmMenuItem {
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode currNode;
	private Window owner;

	public CmCutNodeMenuItem(CmTree tree, CmTreeNode currNode, Window owner) {
		this.tree = tree;
		this.currNode = currNode;
		this.owner = owner;
		setText("剪切");
		setIconStr("cut.png");
		setEnabled(displayValidate(this.currNode));
	}

	private boolean displayValidate(CmTreeNode currNode) {
		TreePath[] paths = CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if(paths.length==1){
			if(null == currNode.getParent() || null == currNode.getParent().getParent()){
				return false;
			}else if(CmCommonStringUtil.isHasFilingOfParent(currNode)){
				return false;
			}else if("assistant".equals(currNode.getPart().getPartType())){
				return false;
			}else if(CmCommonStringUtil.isPackageOfParent(currNode)){
				return false;
			}
			else{
				return true;
			}
		}
		else if(paths.length>1){
			boolean flag = true;
			List<CmTreeNode> list = new ArrayList<CmTreeNode>();//如果父节点和子节点都被选中，记录父节点
			for (int i = 0; i < paths.length; i++) {
				CmTreeNode node = (CmTreeNode) paths[i].getLastPathComponent();
				for(int j = i+1; j < paths.length; j++){
					CmTreeNode cmnode = (CmTreeNode) paths[j].getLastPathComponent();
					CmTreeNode parent = (CmTreeNode) cmnode.getParent();
					if (null != parent
							&&	!"PBOM".equals(parent.toString())
							&& CmCommonNodeUtil.isEqualOfOccpath(node, parent)
							&& null ==CmCommonStringUtil.checkTheNodeIsInList(parent, list)) {
						list.add(parent);
					}
				}
			}
			for (TreePath path : paths) {
				CmTreeNode selectNode = (CmTreeNode) path.getLastPathComponent();
				if(null==CmCommonStringUtil.checkTheNodeIsInList((CmTreeNode)selectNode.getParent(), list)){//如果父节点和子节点都被选中，子节点不处理
					if("assistant".equals(selectNode.getPart().getPartType())){
						flag = false;
						return false;
					}else if(null==selectNode.getParent() || null==selectNode.getParent().getParent()){
						flag = false;
						return false;
					}
					else if(CmCommonStringUtil.isHasFilingOfParent(selectNode)){
						//如果父节点已归档,不能剪切
						flag = false;
						return false;
					}
					else if(CmCommonStringUtil.isPackageOfParent((CmTreeNode)selectNode.getParent())){
						//如果父节点是打包结构,不能剪切
						flag = false;
						return false;
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
		TreePath[] paths = CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		CmTreeNode cascadeNode=CmCommonStringUtil.isExistParentAndChildNode(paths);
		if(null != cascadeNode){
			if(JOptionPane.showConfirmDialog(tree.getRootPane(), "是否进行级联剪切节点 " + cascadeNode.getPart().getPartNumber() + "?", "提示", JOptionPane.OK_CANCEL_OPTION) == 0){
				cutAction(evt,paths);
			}
		}
		else{
			cutAction(evt,paths);
		}
	}
	
	public void cutAction(ActionEvent evt,TreePath[] paths){
		List<CmTreeNode> deletelist = new ArrayList<CmTreeNode>();
		   for (TreePath path : paths) {
			   CmTreeNode cmnode = (CmTreeNode) path.getLastPathComponent();
			   if(!CmCommonNodeUtil.checkHasSameOidInList(cmnode, deletelist)){
				   deletelist.add(cmnode);
			   }
		}
		List<CmTreeNode> nodeList = new ArrayList<CmTreeNode>();
		for (CmTreeNode node : deletelist) {
			if (isChildNode(node, paths)) {
				nodeList.add(node);
			}
		}
		CmTaskInfo taskInfo = CmTaskInfo.newCmTaskInfo("CmEBomTreePanel.setSelectedCopyNodeList", evt.getSource(),
				nodeList);
		try {
			PviewTask.postTask(taskInfo, null);
		} catch (CmTaskException e1) {
		}
		CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
		tree.updateUI();
		BomTreeReportAction.updateBomReport();
	}
	
	
	/**
	 * 如果该节点的父节点也被选中，则不需要重复操作
	 * @param node
	 * @param paths
	 * @return
	 *
	 */
	public boolean isChildNode(CmTreeNode node, TreePath[] paths) {
		boolean flag = true;
		CmTreeNode parent = (CmTreeNode) node.getParent();
		for (int i = 0; i < paths.length; i++) {
			CmTreeNode child = (CmTreeNode) paths[i].getLastPathComponent();
			if (child.getOccId().equals(parent.getOccId())) {
				flag = false;
			}
		}
		return flag;
	}
}

package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmTaskInfo;
import com.glaway.mpm.pbombuilder.util.PviewTask;

/**
 * <br>
 * Created on 2012-10-19
 * 
 * @author chenyunlong
 */
public class CmMBomCopyNodeMenuItem extends CmMenuItem {
	private static final CmLogger log = CmLogger.getLogger(CmMBomCopyNodeMenuItem.class.getName());

	private static final long serialVersionUID = -8396429397442896329L;
	private CmTree tree;
	private CmTreeNode currNode;

	public CmMBomCopyNodeMenuItem(CmTree tree, CmTreeNode currNode) {
		this.tree = tree;
		this.currNode = currNode;
		setText("复制");
		setIconStr("copy.png");
		setEnabled(displayValidate(this.currNode));
	}
	
	private boolean displayValidate(CmTreeNode currNode) {
		TreePath[] paths =  CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if(paths.length==1 && null!=currNode.getParent() && null != currNode.getParent().getParent()){
			if(null==currNode.getParent() || null == currNode.getParent().getParent() || currNode.getPart().getEchangeIndex()==3){
				return false;
			}
			return true;
		}else if(paths.length>1){
			for(TreePath path:paths){
				CmTreeNode node = (CmTreeNode) path.getLastPathComponent();
				if(null==currNode.getParent() || null == currNode.getParent().getParent() || node.getPart().getEchangeIndex()==3){
					return false;
				}
			}
			return true;
		}else{
			return false;
		}
	}
	/**
	 * @param evt
	 */
	@Override
	protected void actionPerformed(ActionEvent evt) {

		TreePath[] paths = tree.getSelectionPaths();
		List<CmTreeNode> nodeList = new ArrayList<CmTreeNode>();
		for (TreePath path : paths) {
			CmTreeNode node = (CmTreeNode) path.getLastPathComponent();
			if (null == node.getParent() && "EBOM".equals(node.toString())) {
				nodeList.add((CmTreeNode) node.children().nextElement());
			} else {
				nodeList.add(node);
			}

		}

		CmTaskInfo taskInfo = CmTaskInfo.newCmTaskInfo("CmEBomTreePanel.setSelectedCopyNodeList", evt.getSource(),
				nodeList);
		nodeList=null;
		try {
			PviewTask.postTask(taskInfo, null);
		} catch (CmTaskException e1) {
		}

	}

}

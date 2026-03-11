package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.AddMPModelDialog;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

/**
 * 添加工艺中间件
 *
 * @author chenyunlong
 *
 */
public class CmAddMPModelNodeMenuItem extends CmMenuItem {
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode currNode;
	private Window owner;

	public CmAddMPModelNodeMenuItem(CmTree tree, CmTreeNode currNode, Window owner) {
		this.tree = tree;
		this.currNode = currNode;
		this.owner = owner;
		setText("创建毛坯件");
		setIconStr("fujian.gif");
//		 setEnabled(canEdit?displayValidate(this.currNode):canEdit);
		setEnabled(displayValidate(this.currNode));
	}

	private boolean displayValidate(CmTreeNode node) {
		CmTreeNode rootPart = (CmTreeNode)tree.getRoot().children().nextElement();
		// 工艺辅件没有添加工艺中间件功能
		TreePath[] paths =  CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if(paths.length>1){
			return false;
		}
		else if(null==node.getParent()){
			return false;
		}
		else if (!node.isLeaf()) {
			return false;
		}
		else if (rootPart.getPart().getContainerId() != node.getPart().getContainerId()) {//借用件
			return false;
		}
//		else if(null==node.getParent().getParent()
//				&& !CmCommonStringUtil.isHasFilingOfObj(node)
//				&& CmCommonNodeUtil.checkNodeHasStructure(node)){
//			return true;
//		}
		else if (node.getPart().getPartType().equals("assistant")) {
			return false;
		}
//		else if(CmCommonStringUtil.isPackageOfParent(node)){
//			//父节点是打包结构时
//			return false;
//		}
		else if(CmCommonStringUtil.isPackage(node)){
			return false;
		}
//		else if(!"middle".equals(node.getPart().getPartType())
//				&&!CmCommonNodeUtil.checkNodeHasStructure(node)
//				&& !"PBOM".equals(node.getParent().toString())){
//			return false;
//		}
//		else if(!CmCommonNodeUtil.checkNodeHasStructure(node)){
//			return false;
//		}
		else {
			return !CmCommonStringUtil.isHasFilingOfObj(node);
		}
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		TreePath[] paths = CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if (paths.length == 1) {
			CmTreeNode node = (CmTreeNode) paths[0].getLastPathComponent();
			AddMPModelDialog d = new AddMPModelDialog(node, tree);
			d.showDialog();
		}
	}

}

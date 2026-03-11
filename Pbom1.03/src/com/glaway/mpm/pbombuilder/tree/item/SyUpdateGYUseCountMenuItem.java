package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Enumeration;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.panel.CmEBomTreePanel;
import com.glaway.mpm.pbombuilder.tree.CmScrollPaneTree;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.CmTreeSelectionListener;
import com.glaway.mpm.pbombuilder.tree.dialog.UpdateGYUseCountDialog;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

	public class SyUpdateGYUseCountMenuItem extends CmMenuItem {
		private static final long serialVersionUID = 1L;
		private CmTree tree;
		private CmTreeNode currNode;
		private Window owner;

		public SyUpdateGYUseCountMenuItem(CmTree tree, CmTreeNode currNode, Window owner) {
			this.tree = tree;
			this.currNode = currNode;
			this.owner = owner;
			setText("同步跟新工艺数量");
			setIconStr("edit.gif");
			// setEnabled(canEdit?displayValidate(this.currNode):canEdit);
			setEnabled(displayValidate(this.currNode));
		}

		private boolean displayValidate(CmTreeNode node) {
			String parentNodeName = node.getParent().toString();
			if(!CmCommonStringUtil.isHasFilingOfObj(node) && (node != node.getRoot()) && parentNodeName != "PBOM"){
				return true;
			}

			return false;
		}

		@Override
		protected void actionPerformed(ActionEvent evt) {

			CmTreeNode enode = (CmTreeNode)CmEBomTreePanel.ebomTree.getSelectedNode();
		if(enode!=null){
			String egysl = enode.getPart().getGysl();
			if(enode.getOccpath().equals(currNode.getOccpath()) && enode.getPart().getPartNumber().equals(currNode.getPart().getPartNumber())){
				currNode.getPart().setGysl(egysl);

				TreeNode parent = currNode.getParent();
				if(parent!=null && parent instanceof CmTreeNode){
					CmTreeNode p = (CmTreeNode)parent;
					String parentPartNumber = p.getPart().getPartNumber();
					String key = parentPartNumber+"->"+currNode.getPart().getPartNumber();
					CmScrollPaneTree.changeGysl.put(key, egysl);
				}

				currNode.getPart().setEdit(true);
				if(null!=currNode.getListNode()){
				  if(currNode.getListNode().size()>0){
					for(CmTreeNode pnode:currNode.getListNode()){
						pnode.getPart().setGysl(egysl);
						pnode.getPart().setEdit(true);
					}
				  }
			    }
			}

			CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
			tree.updateUI();
		}else{
			JOptionPane.showMessageDialog(tree.getRootPane(), "EBOM节点未找到，请手动修改工艺数量！");
		}
	 }

}




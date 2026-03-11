package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.data.CmTreeNodeAttributProxy;
import com.glaway.mpm.pbombuilder.data.CmXmlDataProxy;
import com.glaway.mpm.pbombuilder.tree.CmCancelNode;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreeCancelAction;
import com.glaway.mpm.pbombuilder.util.CmAbstractPart;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

/**
 * <br>
 * Created on 2012-10-29
 *
 * @author chenyunlong
 */
public class CmMBomMDeleteNodeMenuItem extends CmMenuItem {
	private static final long serialVersionUID = -4543662941800561080L;
	private CmTree tree;
	private CmTreeNode node;
	private Window owner;
	private CmAbstractPart absPart;

	public CmMBomMDeleteNodeMenuItem(CmTree tree, CmTreeNode currNode, Window owner, boolean canEdit) {
		this.tree = tree;
		this.node = currNode;
		this.owner = owner;
		setText("移除");
		setIconStr("delete.png");
		//setEnabled(displayValidate(currNode));
		setEnabled(true);
	}

	private boolean displayValidate(CmTreeNode currNode) {
		TreePath[] paths = CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if(paths.length==1){
			if(null == currNode.getParent() || null == currNode.getParent().getParent()){
				return false;
			}else if(CmCommonStringUtil.isHasFilingOfParent(currNode)){
				return false;
			}else if("assistant".equals(currNode.getPart().getPartType())){
				return true;
			}else if(CmCommonStringUtil.isPackageOfParent(currNode)){
				return false;
			}else{
				return true;
			}
		}
		else if(paths.length>1){
			boolean flag = true;
			List<CmTreeNode> list = new ArrayList<CmTreeNode>();// 如果父节点和子节点都被选中，记录父节点
			for (int i = 0; i < paths.length; i++) {
				CmTreeNode node = (CmTreeNode) paths[i].getLastPathComponent();
				for (int j = i + 1; j < paths.length; j++) {
					CmTreeNode cmnode = (CmTreeNode) paths[j].getLastPathComponent();
					CmTreeNode parent = (CmTreeNode) cmnode.getParent();
					if (null != parent
							&&	!"PBOM".equals(parent.toString())
							&& CmCommonNodeUtil.isEqualOfOccpath(node, parent)
							&& null == CmCommonStringUtil.checkTheNodeIsInList(parent, list)) {
						list.add(parent);
					}
				}
			}
			for (TreePath path : paths) {
				CmTreeNode selectNode = (CmTreeNode) path.getLastPathComponent();
				if (null == CmCommonStringUtil.checkTheNodeIsInList((CmTreeNode) selectNode.getParent(), list)) {// 如果父节点和子节点都被选中，子节点不处理
					if (null == selectNode.getParent() || null == selectNode.getParent().getParent()) {
						flag = false;
						return false;
					}else if (CmCommonStringUtil.isHasFilingOfParent(selectNode)) {
						// 如果父节点已归档,不能剪切
						flag = false;
						return false;
					} else if (CmCommonStringUtil.isPackageOfParent(selectNode)) {
						// 如果父节点是打包结构,不能剪切
						flag = false;
						return false;
					}
				}
			}
			return flag;
		}else{
			return false;
		}

	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		TreePath[] paths = CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		CmTreeNode cascadeNode = CmCommonStringUtil.isExistParentAndChildNode(paths);
		if (null != cascadeNode) {
			if (JOptionPane.showConfirmDialog(tree.getRootPane(), "是否要级联移除节点"
					+ cascadeNode.getPart().getPartNumber()
					+ "?", "提示", JOptionPane.OK_CANCEL_OPTION) == 0) {
				deleteAction(paths);
			}
		} else {
			if (JOptionPane.showConfirmDialog(tree.getRootPane(), "是否要移除节点?", "提示", JOptionPane.OK_CANCEL_OPTION) == 0) {
				deleteAction(paths);
			}
		}
	}

	public void deleteAction(TreePath[] paths) {
		List<CmTreeNode> deletelist = new ArrayList<CmTreeNode>();
		for (TreePath path : paths) {
			CmTreeNode cmnode = (CmTreeNode) path.getLastPathComponent();
			if (!CmCommonNodeUtil.checkHasSameOidInList(cmnode, deletelist)) {
				deletelist.add(cmnode);
			}
		}
		for (Iterator<CmTreeNode> it = deletelist.iterator(); it.hasNext();) {
			CmTreeNode cmnode = it.next();
			if (null != CmCommonStringUtil.checkTheNodeIsInList((CmTreeNode) cmnode.getParent(), deletelist)){
				it.remove();
			}
		}
		CmXmlDataProxy proxy = CmXmlDataProxy.getCmXmlDataProxy();
		CmTreeNodeAttributProxy attribut = (CmTreeNodeAttributProxy) proxy.getProxy(CmXmlDataProxy.MBOM_ATTRIBUTE_PROXY);
		List<CmCancelNode> list = new ArrayList<CmCancelNode>();
		for (CmTreeNode node : deletelist) {
			if ("assistant".equals(node.getPart().getPartType())) {
				list.add(CmCommonStringUtil.createNewCancelNode(node, null, true, "delete"));// 节点的回退
				CmCommonStringUtil.updateNodeOperType(node, "delete");// 更新节点的操作状态
				// 删除属性值
				attribut.deleteAttributNode((CmTreeNode) node.getParent(), node);
				CmCommonStringUtil.deleteAssistWithCommonParent(tree.getRoot(), node, (CmTreeNode) node.getParent());
			} else {
				CmCommonNodeUtil.deleteCommonNodeWithCommonParent(node, (CmTreeNode)node.getParent(), tree.getRoot(), list);
			}
		}
		EbomTreeCancelAction.addPbomTreeChange(null, null, "delete", list);
		CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
		tree.updateUI();
		BomTreeReportAction.updateBomReport();
	}
}

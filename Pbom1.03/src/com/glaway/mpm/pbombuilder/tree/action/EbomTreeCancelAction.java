package com.glaway.mpm.pbombuilder.tree.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.action.CmCommonPackageAction;
import com.glaway.mpm.pbombuilder.tree.CmCancelNode;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;

public class EbomTreeCancelAction extends CmAction {
	private static final long serialVersionUID = 1L;
	private static CmTree tree;
	private ImageIcon cancelImage = new ImageIcon(CmUtil.getImageFromServer("cancel.png"));
	private static List<CmCancelNode> cancelList;
	private static JButton button;
	private CmCommonPackageAction cmCommonPackageAction = new CmCommonPackageAction();
	private CmTree ebomtree;

	public EbomTreeCancelAction(CmTree tree, CmTree ebomtree) {
		setIcon(cancelImage);
		setToolTipText("撤销");
		this.tree = tree;
		this.ebomtree = ebomtree;
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		button = (JButton) evt.getSource();
		if (null != cancelList && cancelList.size() > 0) {
			CmCommonNodeUtil common = new CmCommonNodeUtil();
			common.clearBomSearchResult(tree.getRoot());
			CmCancelNode cmCancelNode = cancelList.get(cancelList.size() - 1);
			if (cmCancelNode.getOperation().equals("virtual")) {
				if (null != cmCancelNode.getCancelList() && cmCancelNode.getCancelList().size() > 0) {
					for (CmCancelNode cancel : cmCancelNode.getCancelList()) {
						cancelVirtual(cancel);
					}
				} else {
					cancelVirtual(cmCancelNode);
				}

				cancelList.remove(cmCancelNode);
			} else if (cmCancelNode.getOperation().equals("create")) {
				// 删除节点
				if (null != cmCancelNode.getCancelList() && cmCancelNode.getCancelList().size() > 0) {
					for (CmCancelNode cancel : cmCancelNode.getCancelList()) {
						removeNodeFromParentNode(cancel.getParentNode(), cancel, null, true);
					}
				} else {
					removeNodeFromParentNode(cmCancelNode.getParentNode(), cmCancelNode, null, true);
				}
				cancelList.remove(cmCancelNode);
			} else if (cmCancelNode.getOperation().equals("move")) {
				// 拖拽节点或PBOM的剪切后复制
				if (null != cmCancelNode.getCancelList() && cmCancelNode.getCancelList().size() > 0) {
					for (CmCancelNode cancel : cmCancelNode.getCancelList()) {
						removeNodeFromParentNode(cancel.getParentNode(), cancel, cancel.getNewParentNode(), false);
					}
				} else {
					removeNodeFromParentNode(cmCancelNode.getParentNode(), cmCancelNode, cmCancelNode
							.getNewParentNode(), false);
				}
				cancelList.remove(cmCancelNode);
			} else if (cmCancelNode.getOperation().equals("delete")) {
				if (null != cmCancelNode.getCancelList() && cmCancelNode.getCancelList().size() > 0) {
					for (CmCancelNode cancel : cmCancelNode.getCancelList()) {
						CmTreeNode parent = cancel.getParentNode();
						CmTreeNode obj = cancel.getObjNode();
						if ("assistant".equals(obj.getPart().getPartType())) {
							CmCommonStringUtil.addCommonAssistNodeWithCommonParent(tree.getRoot(), obj, parent);
						} else {
							addToTree(tree.getRoot(), parent, obj, cancel.getObjindex(), cancel.getParetnindex());
						}
					}
				} else {
					CmTreeNode parent = cmCancelNode.getParentNode();
					CmTreeNode obj = cmCancelNode.getObjNode();
					if ("assistant".equals(obj.getPart().getPartType())) {
						CmCommonStringUtil.addCommonAssistNodeWithCommonParent(tree.getRoot(), obj, parent);
					} else {
						parent.add(obj);
					}
				}
				cancelList.remove(cmCancelNode);
			} else if (cmCancelNode.getOperation().equals("package")) {
				if (cmCancelNode.isAllPackage()) {
					cmCommonPackageAction.uppackageAllNode(ebomtree);
					cmCommonPackageAction.uppackageAllNode(tree);
				} else {
					cmCommonPackageAction.unpackageOneNode(ebomtree.getRoot(), cmCancelNode.getObjNode());
					cmCommonPackageAction.unpackageOneNode(tree.getRoot(), cmCancelNode.getObjNode());
				}
				cancelList.remove(cmCancelNode);
				CmCommonStringUtil.sortTheTreeNode(ebomtree.getRoot());
				ebomtree.updateUI();
			} else if (cmCancelNode.getOperation().equals("unpackages")) {
				if (cmCancelNode.isAllPackage()) {
					cmCommonPackageAction.packageAllNode(ebomtree);
					cmCommonPackageAction.packageAllNode(tree);
				} else {
					cmCommonPackageAction.packageOneNode(ebomtree.getRoot(), cmCancelNode.getObjNode());
					cmCommonPackageAction.packageOneNode(tree.getRoot(), cmCancelNode.getObjNode());
				}
				cancelList.remove(cmCancelNode);
				CmCommonStringUtil.sortTheTreeNode(ebomtree.getRoot());
				ebomtree.updateUI();
			}
		} else {
			button.setEnabled(false);
		}
		if (null == cancelList || cancelList.size() == 0) {
			button.setEnabled(false);
		}
		PbomTreeEditReportAction.updatePbomTreeEditReport();
		BomTreeReportAction.updateBomReport();
		CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
		tree.updateUI();
	}

	public void cancelVirtual(CmCancelNode cmCancelNode) {
		CmTreeNode parent = cmCancelNode.getParentNode();
		List<CmTreeNode> childList = cmCancelNode.getChildNode();
		CmTreeNode obj = cmCancelNode.getObjNode();
		for (CmTreeNode node : childList) {
			obj.add(node);
		}
		Enumeration children = parent.children();
		List<CmTreeNode> oldChildList = new ArrayList<CmTreeNode>();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if (isChildNode(child, childList)) {
				oldChildList.add(child);
			}
		}
		for (CmTreeNode old : oldChildList) {
			old.removeFromParent();
		}
		parent.add(obj);
	}

	@SuppressWarnings("unchecked")
	public static void addPbomTreeChange(CmTreeNode node, CmTreeNode newParent, String operaType,
			List<CmCancelNode> list) {
		if (null == cancelList) {
			cancelList = new ArrayList<CmCancelNode>();
		}
		CmCancelNode cancel = new CmCancelNode();
		if (null == node) {
			cancel.setCancelList(list);
		} else {
			List<CmTreeNode> childList = new ArrayList<CmTreeNode>();
			CmTreeNode objnode = CmCommonStringUtil.copyNewNode(node);
			Enumeration children = objnode.children();
			while (children.hasMoreElements()) {
				childList.add((CmTreeNode) children.nextElement());
			}
			cancel.setObjNode(objnode);
			cancel.setParentNode((CmTreeNode) node.getParent());
			cancel.setNewParentNode(newParent);
			cancel.setChildNode(childList);
		}
		cancel.setOperation(operaType);
		cancelList.add(cancel);
		if (null != cancelList) {
			button.setEnabled(true);
		}
	}

	public static void addPbomTreePackage(CmTreeNode packagenode, String operaType, boolean flag) {
		if (null == cancelList) {
			cancelList = new ArrayList<CmCancelNode>();
		}
		CmCancelNode cancel = new CmCancelNode();
		if (!flag) {
			CmTreeNode objnode = CmCommonStringUtil.copyNewNode(packagenode);
			cancel.setObjNode(objnode);
		}
		cancel.setOperation(operaType);
		cancel.setAllPackage(flag);
		cancelList.add(cancel);
		if (null != cancelList) {
			button.setEnabled(true);
		}
	}

	public boolean isChildNode(CmTreeNode objnode, List<CmTreeNode> list) {
		boolean flag = false;
		for (CmTreeNode node : list) {
			if (node.getOccId().equals(objnode.getOccId())) {
				flag = true;
			}
		}
		return flag;
	}

	public void initButton(JButton cancelButton) {
		this.button = cancelButton;
	}

	/**
	 * 将节点从它的父节点中删除
	 *
	 * @date 2013-2-21
	 * @param parent
	 * @param objnode
	 *
	 */
	public void removeNodeFromParentNode(CmTreeNode parent, CmCancelNode objnode, CmTreeNode newparent, boolean isdelete) {
		List<CmTreeNode> packagelist = new ArrayList<CmTreeNode>();
		packagelist.add(objnode.getObjNode());
		if(CmCommonStringUtil.isPackage(objnode.getObjNode())){
			for(CmTreeNode packagenode:objnode.getObjNode().getListNode()){
				packagelist.add(packagenode);
			}
		}
		List<CmTreeNode> brotherList = new ArrayList<CmTreeNode>();
		Enumeration children = parent.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(CmCommonNodeUtil.checkNodeIsCommon(child,objnode.getObjNode())){
				for (Iterator<CmTreeNode> it = packagelist.iterator(); it.hasNext();) {
					CmTreeNode cmnode = it.next();
					if (("middle".equals(child.getPart().getPartType())
							&& CmCommonStringUtil.checkIsSameMiddleNode(cmnode,child))
							|| (!"middle".equals(child.getPart().getPartType())
									&& CmCommonStringUtil.checkNodeIsSame(cmnode, child))) {
						brotherList.add(child);
					}
				}
			}
		}
		List<CmCancelNode> list = new ArrayList<CmCancelNode>();
		for (CmTreeNode brother : brotherList) {
			if ("assistant".equals(brother.getPart().getPartType())) {
				CmCommonStringUtil.deleteAssistWithCommonParent(tree.getRoot(), brother, parent);
			}else if("middle".equals(brother.getPart().getPartType()) && isdelete){
				CmCommonNodeUtil.deleteCommonNodeWithCommonParent(brother, (CmTreeNode)brother.getParent(), tree.getRoot(), list);
			}
			else {
				CmTreeNode broparent = (CmTreeNode) brother.getParent();
				brother.removeFromParent();
				CmCommonStringUtil.checkNodeIsChangeOfStructure(broparent, false);
				CmTreeNode root = CmCommonStringUtil.getRootNode(newparent);
				if (null != root) {
					removeFromTree(root, brother);
				}
				if (!isdelete) {
					if (null != root && !"PBOM".equals(root.toString())) {
						selectNodeFromPBOMByOccpath(tree.getRoot(), newparent, brother, true);
					}else{
						newparent.add(brother);
						CmCommonStringUtil.checkNodeIsChangeOfStructure(newparent, false);
						CmCommonStringUtil.isMoved(brother);
					}
				}
			}
		}
	}

	/**
	 * 当节点树与当前PBOM树不是同一条树，需要从PBOM树删除节点
	 *
	 * @author chenyunlong
	 * @date 2013-5-2
	 * @param root
	 * @param deletenode
	 *
	 */
	public void removeFromTree(CmTreeNode root, CmTreeNode deletenode) {
		Enumeration children = root.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if (("middle".equals(child.getPart().getPartType())
					&& CmCommonStringUtil.checkIsSameMiddleNode(deletenode,	child))
					|| (!"middle".equals(child.getPart().getPartType())
							&& CmCommonStringUtil.checkNodeIsSame(deletenode, child))) {
				child.removeFromParent();
				CmCommonStringUtil.checkNodeIsChangeOfStructure(root, false);
				break;
			}
			removeFromTree(child, deletenode);
			if (CmCommonStringUtil.isPackage(child)) {
				for (CmTreeNode brother : child.getListNode()) {
					removeFromTree(brother, deletenode);
				}
			}
		}
	}

	/**
	 * 当节点树与当前PBOM树不是同一条树，需要从PBOM树删除节点
	 *
	 * @author chenyunlong
	 * @date 2013-5-2
	 * @param root
	 * @param deletenode
	 *
	 */
	public void addToTree(CmTreeNode root, CmTreeNode parent, CmTreeNode newnode, String objindex, String parentindex) {
		Enumeration children = root.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if (("middle".equals(child.getPart().getPartType())
					&& CmCommonStringUtil.checkIsSameMiddleNode(parent, child)
					&& CmCommonStringUtil.isEqual(parentindex, child.getPart().getMiddleIndex()))
					|| (!"middle".equals(child.getPart().getPartType())
							&& CmCommonStringUtil.checkNodeIsSame(parent,child))) {
				child.add(newnode);
				CmCommonStringUtil.checkNodeIsChangeOfStructure(child, false);
				break;
			}
			addToTree(child, parent, newnode, objindex, parentindex);
			if (CmCommonStringUtil.isPackage(child)) {
				for (CmTreeNode brother : child.getListNode()) {
					addToTree(brother, parent, newnode, objindex, parentindex);
				}
			}
		}
	}

	/**
	 * 根据occpath找到bom树上的唯一节点
	 *
	 * @author chenyunlong
	 * @date 2013-5-20
	 * @param root
	 * @param node
	 *
	 */
	public boolean selectNodeFromPBOMByOccpath(CmTreeNode root, CmTreeNode node, CmTreeNode newnode, boolean flag) {
		Enumeration children = root.children();
		while (children.hasMoreElements()) {
			if (flag) {
				CmTreeNode child = (CmTreeNode) children.nextElement();
				if (("middle".equals(child.getPart().getPartType())
						&& CmCommonNodeUtil.isEqualOfOccpath(child, node)
						&& CmCommonStringUtil.isEqual(node.getPart().getMiddleIndex(), child.getPart().getMiddleIndex()))
						|| (!"middle".equals(child.getPart().getPartType())
								&& CmCommonNodeUtil.isEqualOfOccpath(child, node))) {
					child.add(newnode);
					CmCommonStringUtil.isMoved(newnode);
					flag = false;
					break;
				}
				flag = selectNodeFromPBOMByOccpath(child, node, newnode, flag);
				if (flag && CmCommonStringUtil.isPackage(child)) {
					for (CmTreeNode brother : child.getListNode()) {
						if (("middle".equals(brother.getPart().getPartType())
								&& CmCommonNodeUtil.isEqualOfOccpath(brother, node)
								&& CmCommonStringUtil.isEqual(node.getPart().getMiddleIndex(), brother.getPart().getMiddleIndex()))
								|| (!"middle".equals(brother.getPart().getPartType())
										&& CmCommonNodeUtil.isEqualOfOccpath(brother, node))) {
							brother.add(newnode);
							CmCommonStringUtil.isMoved(newnode);
							flag = false;
							break;
						}
						flag = selectNodeFromPBOMByOccpath(brother, node, newnode, flag);
					}
				}
			}else{
				break;
			}
		}
		return flag;
	}

	@SuppressWarnings("unchecked")
	public static void clearCancel() {
		if(!cancelList.isEmpty()){
			cancelList.clear();

			if(cancelList.isEmpty()){
				button.setEnabled(false);
			}
		}
	}
}

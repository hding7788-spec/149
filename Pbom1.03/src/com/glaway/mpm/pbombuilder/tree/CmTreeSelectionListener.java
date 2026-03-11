package com.glaway.mpm.pbombuilder.tree;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmTreeLinkage;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.ptc.pview.utils.dom.ActorShutdownException;
import com.ptc.pview.utils.dom.ConnectionLostException;
import com.ptc.pview.utils.dom.InvalidActorException;
import com.ptc.pview.utils.dom.MessageProtocolException;

/**
 * <br>
 * Created on 2012-12-5
 *
 * @author chenyunlong
 */
public class CmTreeSelectionListener implements TreeSelectionListener {
	private CmTreeLinkage linkage;

	public CmTreeSelectionListener(CmTreeLinkage linkage) {
		this.linkage = linkage;
	}

	public CmTreeSelectionListener() {

	}

	public void valueChanged(TreeSelectionEvent e) {
		TreePath[] theTreePaths = e.getPaths();
		ArrayList<CmTreeNode> theSelectedNodes = new ArrayList<CmTreeNode>();
		// Treat the removed elements first
		for (int index = 0; index < theTreePaths.length; index++) {
			TreePath theCurrentTreePath = theTreePaths[index];
			CmTreeNode theNode = (CmTreeNode) theCurrentTreePath.getLastPathComponent();
			if (!e.isAddedPath(index))
				setSelectionChangedRecursively(theNode, false);
			else
				theSelectedNodes.add(theNode);
		}

		// Treat the new selection
		Iterator theSelectedNodesIt = theSelectedNodes.iterator();
		while (theSelectedNodesIt.hasNext()) {
			setSelectionChangedRecursively((CmTreeNode) theSelectedNodesIt.next(), true);
		}

		ArrayList<CmTreeNode> theSelectedNodeList = new ArrayList<CmTreeNode>();
		JTree tree = (JTree) e.getSource();
		TreePath[] selectionPaths = tree.getSelectionPaths();
		if (selectionPaths == null)
			return;

		for (int index = 0; index < selectionPaths.length; index++) {
			CmTreeNode node = (CmTreeNode) selectionPaths[index].getLastPathComponent();
			// if (node.isLeaf())
			theSelectedNodeList.add(node);
			setSelectionChangedRecursively(node, true);
		}
		if (linkage != null)
			linkage.valueChange(theSelectedNodeList);
	}

	private void setSelectionChangedRecursively(CmTreeNode node, boolean selectionMode) {
		setSelectionPackageNodeChangedRecursively(node,selectionMode);
		setSelectionPackageParentNodeChangedRecursively(node,node,selectionMode);
	}

	@SuppressWarnings("unchecked")
	public void cancelBrotherNodeSelected(CmTreeNode node, boolean selectionMode) {
		Enumeration children = node.getParent().children();
		while(children.hasMoreElements()){
			CmTreeNode child=(CmTreeNode) children.nextElement();
			if(CmCommonStringUtil.isCommonNode(node, child) && null!=child.getOccId() && !child.getOccId().equals(node.getOccId())){
				try {
					child.setCreoClick(false);
					if(null != child.get_pviewShapeInstance()){
						child.get_pviewShapeInstance().SetHighlight(selectionMode);
					}
				} catch (MessageProtocolException e) {
					e.printStackTrace();
				} catch (ActorShutdownException e) {
					e.printStackTrace();
				} catch (InvalidActorException e) {
					e.printStackTrace();
				} catch (ConnectionLostException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setSelectionPackageNodeChangedRecursively(CmTreeNode node, boolean selectionMode){
		if (node.isLeaf()) {
			try {
				if (!(((CmTreeNode) node).get_pviewShapeInstance() == null) && node.isIspackage()) {
					((CmTreeNode) node).get_pviewShapeInstance().SetHighlight(selectionMode);
					if (node.isCreoClick() && CmCommonStringUtil.isPackage(node)) {
						for (CmTreeNode brother : node.getListNode()) {
							if(null!=brother.get_pviewShapeInstance()){
								brother.get_pviewShapeInstance().SetHighlight(selectionMode);
							}
						}
					}
					if(null!=node.getParent() && node.isSelected()&& !CmCommonStringUtil.isPackage(node)){
						//如果此被选中的节点不是打包结构，就有可能是拆包结构，应取消其他节点被选中的状态
						cancelBrotherNodeSelected(node,false);
					}
				}

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
//		else{
//			Enumeration<CmTreeNode> children =  node.children();
//			while(children.hasMoreElements()){
//				CmTreeNode child = children.nextElement();
//				if(CmCommonStringUtil.isPackage(child)){
//					for (CmTreeNode brother : child.getListNode()) {
//						setSelectionPackageNodeChangedRecursively(brother,selectionMode);
//					}
//				}else{
//					setSelectionPackageNodeChangedRecursively(child,selectionMode);
//				}
//			}
//		}
	}

	public void setSelectionPackageParentNodeChangedRecursively(CmTreeNode node,CmTreeNode obj, boolean selectionMode){
		if(!CmCommonStringUtil.isPackage(node) && CmCommonStringUtil.isPackageOfParent(node)){
			CmTreeNode parentPackageNode = CmCommonStringUtil.getParentPackageNode(node);
			for(CmTreeNode cmnode:parentPackageNode.getListNode()){
				setSelectionBrotherPackageNode(cmnode,obj,selectionMode);
			}
			setSelectionPackageParentNodeChangedRecursively(parentPackageNode,obj,selectionMode);
		}
	}

	public void setSelectionBrotherPackageNode(CmTreeNode node,CmTreeNode obj, boolean selectionMode){
		Enumeration children = node.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(CmCommonStringUtil.isCommon(obj, child) && CmCommonStringUtil.isCommon((CmTreeNode)obj.getParent(),(CmTreeNode)child.getParent())){
				setSelectionPackageNodeChangedRecursively(child,selectionMode);
			}
			setSelectionBrotherPackageNode(child,obj,selectionMode);
		}

	}
}

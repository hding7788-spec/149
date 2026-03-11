package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.glaway.mpm.pbombuilder.action.CmCommonPackageAction;
import com.glaway.mpm.pbombuilder.action.CmPPViewStructureGenerator;
import com.glaway.mpm.pbombuilder.action.CmPropertiesVisitorImpl;
import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.data.CmDefaultTreeLinkage;
import com.glaway.mpm.pbombuilder.data.CmTreeNodeComparator;
import com.glaway.mpm.pbombuilder.pview.CmPViewFactory;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

public class CmPBomPviewMenuItem extends CmMBomMPViewMenuItem {
	private static final long serialVersionUID = 1L;
	private CmDefaultTreeLinkage treeLinkage;

	public CmPBomPviewMenuItem(CmTree tree,CmTreeNode currNode,Window owner) {
		super(tree,currNode,owner);
		treeLinkage = new CmDefaultTreeLinkage(new CmTreeNodeComparator());
		setText("保存可视化3D模型");
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		CmCommonPackageAction common = new CmCommonPackageAction();
		StringBuffer errorBuf = common.checkThePackageNode(tree.getRoot(), node,"保存可视化3D模型",true);
		if(errorBuf.toString().length()>0){
			JOptionPane.showMessageDialog(tree.getRootPane(),errorBuf);
			return;
		}
//		if (this.node.getPart().isStructureSaved()) {
//	      super.actionPerformed(evt);
//	      return;
//	    }
		if(!CmMBomMainFrame.getMainFrame().isPviewInitialized()) {
			JOptionPane.showMessageDialog(owner,"请先显示EBOM可视化。","错误",JOptionPane.ERROR_MESSAGE);
			return;
		}
		CmTree ebomTree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree();
		CmTreeNode root;
		try {
			CmTreeNode ebomRoot = treeLinkage.findLinkNode(node,ebomTree);
			if(ebomRoot != null)
				root = clonePViewProperties(ebomRoot);
			else
				root = new CmTreeNode("");

			root.setPart(node.getPart());

			List<CmTreeNode> childlist = CmCommonNodeUtil.getAllLeafChildNode(node, null);
			boolean flag = false;
			if(null != childlist){
				for(CmTreeNode child:childlist){
					CmTreeNode ebomNode = treeLinkage.findLinkNode(child,ebomTree);
					if(null != ebomNode && null != ebomNode.get_pviewShapeInstance()){
						flag = true;
						break;
					}
				}
			}
			if(!flag){
				JOptionPane.showMessageDialog(tree.getRootPane(),"没有可视化图档信息,不需要进行保存！");
				return;
			}

			unpackage(node);

			findLinkedEBomNodes(node,root,ebomTree);
		} catch(Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(owner,"错误信息：" + e.toString(),"错误",JOptionPane.ERROR_MESSAGE);
			return;
		}

		final CmPPViewStructureGenerator generator = new CmPPViewStructureGenerator(root, owner);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				generator.generatePVStructure("ZoomAll");
			}
		});
	}

	private void unpackage(CmTreeNode node) {
		CmTree cmtree = null;
		if("PBOM".equals(tree.getRoot().toString())){
			cmtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree();
		}
		else{
			cmtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();
		}

		unpackageOneNode(cmtree,node);

		CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
		CmCommonStringUtil.sortTheTreeNode(cmtree.getRoot());
		tree.updateUI();
		cmtree.updateUI();
	}

	private void unpackageOneNode(CmTree cmtree,CmTreeNode node) {
		CmCommonPackageAction action = new CmCommonPackageAction();
		action.unpackageOneNode(tree.getRoot(), node);
		action.unpackageOneNode(cmtree.getRoot(), node);
		Enumeration num = node.children();
		while(num.hasMoreElements()) {
			CmTreeNode childNode = (CmTreeNode)num.nextElement();
			unpackageOneNode(cmtree,childNode);
		}
	}

	private void findLinkedEBomNodes(CmTreeNode pbomNode,CmTreeNode parentNode,CmTree ebomTree) throws Exception {
		for(int i = 0;i < pbomNode.getChildCount();i++) {
			CmTreeNode child = (CmTreeNode) pbomNode.getChildAt(i);
			if(child.isLeaf()) {
				CmTreeNode ebomNode = treeLinkage.findLinkNode(child,ebomTree);
				if(ebomNode != null) {
					CmTreeNode childNode = clonePViewProperties(ebomNode);
					childNode.setPart(child.getPart());
					parentNode.add(childNode);
				}
			} else {
				CmTreeNode ebomNode = treeLinkage.findLinkNode(child,ebomTree);
				if(ebomNode != null) {
					CmTreeNode newEbomNode = clonePViewProperties(ebomNode);
					newEbomNode.setPart(child.getPart());
					parentNode.add(newEbomNode);
					findLinkedEBomNodes(child,newEbomNode,ebomTree);
				} else if(child.getChildCount() > 0) {
					findLinkedEBomNodes(child,parentNode,ebomTree);
				}
			}
		}
	}

	private void copyInstanceProperties(CmTreeNode source,CmTreeNode target) {
		Collection<String[]> properties = new ArrayList<String[]>();
		CmPropertiesVisitorImpl propertiesVisitor = CmPViewFactory.getPViewImpl(CmPViewFactory.PV_NAME_MBOM).getPropertiesVisitor(properties);
		try {
			if(null == source.get_pviewComponentInstance()){

			}else
			if(source.get_pviewComponentInstance().GetInstance() != null)
				source.get_pviewComponentInstance().GetInstance().Visit(propertiesVisitor);
			else
				source.get_pviewShapeInstance().GetInstance().Visit(propertiesVisitor);
		} catch(Exception e) {
			e.printStackTrace();
		}

		target.set_pviewInstanceProperties(properties);
	}

	private CmTreeNode clonePViewProperties(CmTreeNode source) throws Exception {
		CmTreeNode target;
		if(source.get_pviewComponentNode() != null && source.get_pviewComponentNode().GetShapeSource() != null)
			target = new CmTreeNode(CmPPViewStructureGenerator.PVIEW_TEMP_PATH + File.separator + source.get_pviewComponentNode().GetShapeSource().GetFileSource());
		else
			target = new CmTreeNode("");
		target.setMatrix(source.getMatrix());
		target.setOccId(source.getOccId());
		target.setOccpath(source.getOccpath());

		copyInstanceProperties(source,target);
		if(CmCommonStringUtil.isPackage(source)){
			List<CmTreeNode> list = new ArrayList<CmTreeNode>();
			for(CmTreeNode brother:source.getListNode()){
				CmTreeNode brothertarget;
				if(brother.get_pviewComponentNode() != null && brother.get_pviewComponentNode().GetShapeSource() != null)
					brothertarget = new CmTreeNode(CmPPViewStructureGenerator.PVIEW_TEMP_PATH + File.separator + brother.get_pviewComponentNode().GetShapeSource().GetFileSource());
				else
					brothertarget = new CmTreeNode("");
				brothertarget.setMatrix(brother.getMatrix());
				brothertarget.setOccId(brother.getOccId());
				brothertarget.setOccpath(brother.getOccpath());
				brothertarget.setPart(brother.getPart());
				copyInstanceProperties(brother,brothertarget);
				list.add(brothertarget);
			}
			target.setListNode(list);
		}
		return target;
	}
}
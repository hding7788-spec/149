package com.glaway.mpm.visual.view.tree;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpPaceNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpPartNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpStepNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpTecnicsNode;
import com.glaway.mpm.visual.log.VaLogger;

public class VaTreeModelListener implements TreeModelListener {
	private static final VaLogger    log          = VaLogger.getLogger(VaTreeModelListener.class);
	private VaTreeLinkage linkage;
	
	public VaTreeModelListener(VaTreeLinkage link) {
		this.linkage = link;
	}
	
	@Override
	public void treeNodesChanged(TreeModelEvent arg0) {
		Object[] children = arg0.getChildren();
		if(children == null || children[0] == null){
			return;
		}
		VaTreeNode node = (VaTreeNode)children[0];
		List<VaTreeNode> nodes = new ArrayList<VaTreeNode>();
		
//		if(node instanceof DpTecnicsNode ){
//		    Enumeration<DpStepNode> steps =  node.children();
//		    while(steps.hasMoreElements()){
//		    	Enumeration<DpPaceNode> paces =  steps.nextElement().children();
//		    	while (paces.hasMoreElements()) {
//					Enumeration<DpPartNode> parts =  paces.nextElement().children();
//					while (parts.hasMoreElements()) {
//						nodes.add(parts.nextElement());
//					}
//					
//				}
//		    }
//		}
//		else 
			if( node instanceof DpStepNode) {
			Enumeration<DpPaceNode> paces =  node.children();
			while (paces.hasMoreElements()) {
				Enumeration<DpPartNode> parts =  paces.nextElement().children();
				while (parts.hasMoreElements()) {
					nodes.add(parts.nextElement());
				}
				
			}
		}
		else if(node instanceof DpPaceNode)
		{
			Enumeration<DpPartNode> parts =  node.children();
			while (parts.hasMoreElements()) {
				nodes.add(parts.nextElement());
			}
			
		}else if(node instanceof DpPartNode){
			nodes.add(node);
		}
		else{
			nodes.add(node);
		}
		if(linkage != null){
			linkage.nodeChange(nodes);
		}
	}
	

	@Override
	public void treeNodesInserted(TreeModelEvent arg0) {
		Object[] children = arg0.getChildren();
		for (int i = 0; i < children.length; i++) {
			if(linkage != null){
				linkage.modelNodeAdd((VaTreeNode)children[i]);
			}
		}
	}

	@Override
	public void treeNodesRemoved(TreeModelEvent arg0) {
		
		Object[] children = arg0.getChildren();
		for (int i = 0; i < children.length; i++) {
			if(linkage != null){
				linkage.modelNodeRemove((VaTreeNode)children[i]);
			}
		}
	}

	@Override
	public void treeStructureChanged(TreeModelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}

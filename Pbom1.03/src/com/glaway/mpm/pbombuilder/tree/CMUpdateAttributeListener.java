package com.glaway.mpm.pbombuilder.tree;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;

public class CMUpdateAttributeListener implements TreeSelectionListener {



	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreePath[] theTreePaths = e.getPaths();
		for (int index = 0; index < theTreePaths.length; index++) {
			TreePath theCurrentTreePath = theTreePaths[index];
			CmTreeNode node = (CmTreeNode) theCurrentTreePath.getLastPathComponent();
			//CmCommonNodeUtil.updateNodeAtrribute(node);
//			 List<CmTreeNode> list = CmScrollPaneTree.pbomlist;
//			 for(CmTreeNode n:list){
//				if(CmCommonStringUtil.isEqual(n.getPart().getPartNumber(), node.getPart().getPartNumber())
//						){
//					CmCommonNodeUtil.updateNodeAtrribute(n);
//
//				}
//			 }
		}
	}

}

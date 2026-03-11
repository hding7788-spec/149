package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

	public class SyUpdateRepsMenuItem extends CmMenuItem {
		private static final long serialVersionUID = 1L;
		private CmTree tree;
		private CmTreeNode currNode;
		private Window owner;

		public SyUpdateRepsMenuItem(CmTree tree, CmTreeNode currNode, Window owner) {
			this.tree = tree;
			this.currNode = currNode;
			this.owner = owner;
			setText("更新当前零件及其子件可视化");
			setIconStr("view3d.png");
			// setEnabled(canEdit?displayValidate(this.currNode):canEdit);
			setEnabled(true);
		}


		@Override
		protected void actionPerformed(ActionEvent evt) {

			String partNumber = currNode.getPart().getPartNumber();
			String msg =PBOMEditorToWCIntf.replacePartRepFromEBOMToMBOM(partNumber,true);
			if(!"".equals(msg)){
				JOptionPane.showMessageDialog(tree.getRootPane(), msg);
			}else{
				JOptionPane.showMessageDialog(tree.getRootPane(), "更新成功");
			}

	 }

}




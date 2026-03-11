
package com.glaway.mpm.pbombuilder.tree.action;

import java.awt.event.ActionEvent;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.action.CmCommonPackageAction;
import com.glaway.mpm.pbombuilder.bom.CmConnectFrame;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;

public class EbomTreeBackProductAction  extends CmAction {
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private ImageIcon expandImage = new ImageIcon(CmUtil.getImageFromServer("back_product.png"));

	public EbomTreeBackProductAction(CmTree tree) {
		setIcon(expandImage);
		setToolTipText("返回EBOM");
		this.tree = tree;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		CmTreeNode firstNode = (CmTreeNode) tree.getRoot().children().nextElement();
		if(firstNode.getPart().getPartNumber().startsWith("AL1") && !CmCommonStringUtil.isEqual(String.valueOf(CmConnectFrame.partOid),String.valueOf(firstNode.getPart().getOid()))){
			firstNode.removeFromParent();
			tree.updateUI();
			backEbomFirstNode(tree,firstNode,CmConnectFrame.partOid,new CmTreeNode("EBOM"));
			CmCommonPackageAction commonPackageAction=new CmCommonPackageAction();
			commonPackageAction.packageAllNode(tree);
			tree.updateUI();
		}
		else if(firstNode.getPart().getPartNumber().startsWith("AL1") && CmCommonStringUtil.isEqual(String.valueOf(CmConnectFrame.partOid),String.valueOf(firstNode.getPart().getOid()))){
			JOptionPane.showMessageDialog(tree.getRootPane(), "EBOM已是完整的产品结构！");
		}
	}
	
	public void backEbomFirstNode(CmTree ebomTree,CmTreeNode node,String oid,CmTreeNode newNode){
		Enumeration children = node.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(oid.equals(String.valueOf(child.getPart().getOid()).trim())){
				child.removeFromParent();
				ebomTree.getRoot().add(child);
				break;
			}
			else{
				backEbomFirstNode(ebomTree,child,oid,newNode);
			}
		}
	}
}

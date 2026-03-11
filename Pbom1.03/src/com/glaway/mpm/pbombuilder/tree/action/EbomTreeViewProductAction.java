package com.glaway.mpm.pbombuilder.tree.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import wt.part.WTPart;

import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.action.CmCommonPackageAction;
import com.glaway.mpm.pbombuilder.bom.CmCIMerger;
import com.glaway.mpm.pbombuilder.bom.CmConnectFrame;
import com.glaway.mpm.pbombuilder.jws.CmContext;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmSearchHelper;
import com.glaway.mpm.pbombuilder.util.CmUtil;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

public class EbomTreeViewProductAction extends CmAction  {
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private ImageIcon expandImage = new ImageIcon(CmUtil.getImageFromServer("view_product.png"));

	public EbomTreeViewProductAction(CmTree tree) {
		setIcon(expandImage);
		setToolTipText("查看产品结构");
		this.tree = tree;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		CmTreeNode firstNode = (CmTreeNode) tree.getRoot().children().nextElement();
		if(!firstNode.getPart().getPartNumber().startsWith("AL1")){
			String newOid = PBOMEditorToWCIntf.getRootByChild(CmConnectFrame.partOid);
			if(!CmCommonStringUtil.isEmpty(newOid)){
				tree.setRoot(createNewTree(newOid).getRoot());
				CmCommonPackageAction commonPackageAction=new CmCommonPackageAction();
				commonPackageAction.packageAllNode(tree);
				tree.updateUI();
			}
		}
		else{
			JOptionPane.showMessageDialog(tree.getRootPane(), "已经到产品结构最上层！");
		}
	}
	
	public CmTree createNewTree(String oid){
		CmTreeNode root = new CmTreeNode("EBOM");
		CmTree ebomTree = new CmTree(new CmTreeNode("EBOM"));
		CmCIMerger merger = new CmCIMerger(ebomTree, root, CmContext.getMainFrame());
		WTPart part;
		try {
			part = (WTPart) CmSearchHelper.search(WTPart.class,Long.valueOf(oid));
			CmLightPart lightpart = CmBizObjUtil.buildCmLightPartFromWTPart(part);
			List<CmLightPart> parts = new ArrayList<CmLightPart>();
			parts.add(lightpart);
			merger.doMerger(parts);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ebomTree;
	}
}

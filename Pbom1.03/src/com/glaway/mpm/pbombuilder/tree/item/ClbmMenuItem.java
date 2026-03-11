package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import wt.part.WTPart;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.jws.CmContext;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.TxcLbmDialog;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.ErpUtil;

public class ClbmMenuItem extends CmMenuItem {
	private CmTree tree ;
	private CmTreeNode node;
	private Window owner;
	private String title = "查询填写自制零件材料编码";
	public ClbmMenuItem(CmTree tree,CmTreeNode node,Window owner){
		this.setText(title);
		this.setIconStr("view2d.png");
		this.tree = tree;
		this.node = node;
		this.owner = owner;
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		// TODO Auto-generated method stub
	      CmLightPart lightPart = node.getPart();
	     // WTPart part = CmBizObjUtil.getWTPartFromLightPart(lightPart);

	      TreePath[] paths = tree.getSelectionPaths();
		  List<CmTreeNode> selNodes = new ArrayList<CmTreeNode>();
		  StringBuffer msg = new StringBuffer();
			for (TreePath path : paths) {
				CmTreeNode node = (CmTreeNode) path.getLastPathComponent();
				if (null == node.getParent() || "PBOM".equals(node.toString())) {
					//nodeList.add((CmTreeNode) node.children().nextElement());
				} else {
					if(node.getPart().getWzk().getOpType()==ErpUtil.ERP_OP_TYPE_CLBM||node.getPart().getWzk().getOpType()==ErpUtil.ERP_OP_TYPE_ALL){
						selNodes.add(node);
					}else {
						msg = new StringBuffer();
						msg.append("编号【"+node.getPart().getPartNumber()+"】之前操作的是"+ErpUtil.getErpOptypeDesc(node.getPart().getWzk().getOpType())+"，现在将进行"+title+"操作\n");

						int flag = JOptionPane.showConfirmDialog(owner, msg.toString(),"操作确认",JOptionPane.OK_CANCEL_OPTION);
						if(flag==0){
							selNodes.add(node);
						}
					}
				}
			}
	      if(selNodes==null||selNodes.isEmpty()){
	    	  JOptionPane.showMessageDialog(owner, "请选择PBOM节点！");
	    	  return;
	      }

	      TxcLbmDialog dialog = new TxcLbmDialog(CmContext.getMainFrame(),selNodes,title, tree);
	}

}

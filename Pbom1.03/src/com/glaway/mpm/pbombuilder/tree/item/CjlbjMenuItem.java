package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;

import wt.part.WTPart;

import com.glaway.mpm.pbombuilder.action.CmActionProgressBar;
import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.TxcjlbjDialog;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

public class CjlbjMenuItem extends CmMenuItem{

	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode node;
	private Window owner;
	String title = "";

	public CjlbjMenuItem(CmTree tree,CmTreeNode node,Window owner){
		this.setText("查询创建标准件/元器件节点");
		this.setIconStr("view2d.png");
		this.owner = owner;
		this.tree = tree;
		this.node = node;
		setEnabled(displayValidate(this.node));
	}

	private boolean displayValidate(CmTreeNode node) {
		CmTreeNode rootPart = (CmTreeNode)tree.getRoot().children().nextElement();
		String mtype = node.getPart().getMtype();
		if(null==node.getParent()){
			return false;
		}
		else if (node.isLeaf()) {
			return false;
		}
		else if ("标准件".equals(mtype) || "元器件".equals(mtype) || "外购件".equals(mtype)) {
			return false;
		}
		else if (rootPart.getPart().getContainerId() != node.getPart().getContainerId()) {//借用件
			return false;
		}
		else if(CmCommonStringUtil.isPackageOfParent(node)){
			//父节点是打包结构时
			return false;
		}
		else if(CmCommonStringUtil.isPackage(node)){
			return false;
		}
		else {
			return !CmCommonStringUtil.isHasFilingOfObj(node);
		}
	}

	protected void actionPerformed(ActionEvent evt) {
//		  if(node.isLeaf()){
//			  JOptionPane.showMessageDialog(owner, "查询创建元器件/紧固件节点不能选择叶子节点");
//			 return;
//		  }
      CmLightPart lightPart = node.getPart();
      WTPart part = CmBizObjUtil.getWTPartFromLightPart(lightPart);
      title = "【"+part.getName()+"】"+"查询创建标准件/元器件节点";

      final CmActionProgressBar progressBar = new CmActionProgressBar(null,owner, "查询创建标准件/元器件节点", "正在加载数据,请等待...", "数据加载中");
		Thread thread = new Thread() {
			public void run() {
				new TxcjlbjDialog(tree,owner,node,title);

				progressBar.setHeaderMessage("数据加载完成！");
				progressBar.finish();
				progressBar.setVisible(false);
			}
		};
		thread.start();
		progressBar.setVisible(true);
	}

}

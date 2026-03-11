package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Image;
import java.awt.Window;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;

public class CMErpMenu extends JMenu{

	private static final long serialVersionUID = 1L;

	private CmTreeNode node;
	private Window owner;
	private CmTree tree;

	private CmMenuItem wlbmItem; //物料编码
	private CmMenuItem wlbmItem2; //物料编码
	private CmMenuItem clbmItem;//材料编码
	private CmMenuItem cjlbjMenuItem;//创建零部件

	public CMErpMenu(CmTree tree,CmTreeNode node,Window owner){
		this.tree = tree;
		this.node = node;
		this.owner = owner;


		this.wlbmItem = new WlbmMenuItem(tree,node,owner);//查询填写标准件/外购件物资编码
		this.clbmItem = new ClbmMenuItem(tree,node,owner);//查询填写自制零件材料编码
		this.cjlbjMenuItem = new CjlbjMenuItem(tree, node, owner);//查询创建标准件/外购件节点
		this.wlbmItem2 = new WlbmMenuItem2(tree,node,owner);//修改标准件/外购件物资编码

		this.setText("ERP集成");
		this.setIconStr("view2d.png");
		this.add(wlbmItem);
		//this.add(clbmItem);
		this.add(cjlbjMenuItem);
		this.add(wlbmItem2);
		//this.add(new ExportPbomMenuItem(tree,node,owner));

		setEnabled(displayValidate(this.node));
	}
	private boolean displayValidate(CmTreeNode node) {
		if("PBOM".equals(node.toString())){
			//父节点是打包结构时
			return false;
		}
//		else if(CmCommonStringUtil.isHasFilingOfObj(node)){//已批准状态的不能再修改
//			return false;
//		}
		return true;
	}
	   protected void setIconStr(String iconStr) {
		      Image image = CmUtil.getImageFromServer(iconStr);
		      if (image != null) {
		         Icon icon = new ImageIcon(image);
		         if (icon != null)
		            setIcon(icon);
		      }
		   }

}

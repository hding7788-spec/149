package com.glaway.mpm.erp.pbom;

import java.awt.Window;

import javax.swing.JMenu;

import com.glaway.mpm.erp.CjlbjMenuItem;
import com.glaway.mpm.erp.CmMenuItem;
import com.glaway.mpm.erp.SjycldeMenuItem;
import com.glaway.mpm.erp.ZycldeMenuItem;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTreeNode;

public class CMPbomErpMenu extends JMenu{

	private static final long serialVersionUID = 1L;

	private XWTreeNode node;
	private Window owner;

	private CmMenuItem wlbmItem; //物料编码
	private CmMenuItem cjlbjMenuItem;//创建零部件
	private CmMenuItem zycljMenuItem;//主要材料定额
	private CmMenuItem sjycldeMenuItem;//试件原材料定额

	public CMPbomErpMenu(XWTreeNode node,Window owner){
		this.node = node;
		this.owner = owner;

		this.wlbmItem = new MatchClbmMenuItem((NewTechnicsPart)owner,node);//查询填写标准件/外购件物资编码
		this.cjlbjMenuItem = new SearchAndCreateMenuItem(node, (NewTechnicsPart)owner);//查询创建标准件/外购件节点
		this.zycljMenuItem = new CjlbjMenuItem((NewTechnicsPart)owner,node,"ZPGYDE");//主要材料定额
		this.sjycldeMenuItem = new SjycldeMenuItem((NewTechnicsPart)owner, node,"ZPGYDE");//试件原材料定额

//		this.setText("工艺定额");
		this.setText("装配工艺定额");
		this.add(wlbmItem);
		this.add(cjlbjMenuItem);
		this.add(zycljMenuItem);
		this.add(sjycldeMenuItem);
	}

	public void setEnable(boolean b) {
		setEnabled(b);
	}

}

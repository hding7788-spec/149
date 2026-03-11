package com.glaway.mpm.sjzyk;

import java.awt.Window;

import javax.swing.JMenu;

import com.glaway.mpm.erp.CmMenuItem;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTreeNode;

public class CMPbomSjzykMenu extends JMenu{

	private static final long serialVersionUID = 1L;

	private CmMenuItem matchItem; //物料编码
	private CmMenuItem newMenuItem;//创建零部件

	public CMPbomSjzykMenu(XWTreeNode node,Window owner){

		//从设计资源库查询添加标准件/元器件
		this.newMenuItem = new NewPartMenuItem((NewTechnicsPart)owner,node);
		//PBOM标准件/元器件匹配设计编码
		this.matchItem = new MatchPartMenuItem((NewTechnicsPart)owner,node);

//		this.setText("工艺定额(设计资源库)");
		this.setText("装配工艺定额(设计资源库)");
		this.add(newMenuItem);
		this.add(matchItem);
	}

	public void setEnable(boolean b) {
		setEnabled(b);
	}

}

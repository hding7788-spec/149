package com.glaway.mpm.erp;

import javax.swing.JMenu;

import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTreeNode;

public class GwTechnicsQuotaMenu extends JMenu{

	private static final long serialVersionUID = 1L;

	private NewTechnicsPart frame;
	private CmMenuItem yclgydeItem; //零件工艺定额
	private CmMenuItem zpgydeItem; //装配工艺定额

	public GwTechnicsQuotaMenu(NewTechnicsPart frame,XWTreeNode node){
		this.frame = frame;
		this.yclgydeItem = new GwPartTechnicsQuotaMenuItem(frame,node);//零件工艺定额
		this.zpgydeItem = new GwAssembTechnicsQuotaMenuItem(frame,node);//装配工艺定额
		this.setText("工艺定额");
		this.add(yclgydeItem);
		this.add(zpgydeItem);
		setEnabled(true);
	}

	public void setEnable(boolean b) {
		setEnabled(b);
	}

}

package com.glaway.mpm.sjzyk;

import javax.swing.JMenu;

import com.glaway.mpm.erp.CmMenuItem;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTreeNode;

public class CMSjzykMenu extends JMenu{

	private static final long serialVersionUID = 1L;

	private NewTechnicsPart frame;
	private CmMenuItem ycldeItem; //原材料定额
	private CmMenuItem ZycldeMenuItem;//主要材料定额
	private CmMenuItem sjycldeMenuItem;//试件原材料定额

	public CMSjzykMenu(NewTechnicsPart frame,XWTreeNode node){
		this.frame = frame;
		this.ycldeItem = new YcldeMenuItem(frame,node);//原材料定额
		this.ZycldeMenuItem = new ZycldeMenuItem(frame,node);//主要材料定额
		this.sjycldeMenuItem = new SjycldeMenuItem(this.frame,node);//试件原材料定额
		this.setText("材料定额(设计资源库)");
		this.add(ycldeItem);
		this.add(ZycldeMenuItem);
		this.add(sjycldeMenuItem);
		setEnabled(true);
	}

	public void setEnable(boolean b) {
		setEnabled(b);
	}

}

package com.glaway.mpm.erp;

import javax.swing.JMenu;

import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTreeNode;

public class CMErpMenu extends JMenu{

	private static final long serialVersionUID = 1L;

	private NewTechnicsPart frame;
	private CmMenuItem wlbmItem; //原材料定额
	private CmMenuItem cjlbjMenuItem;//主要材料定额
	private CmMenuItem sjycldeMenuItem;//试件原材料定额

	public CMErpMenu(NewTechnicsPart frame,XWTreeNode node){
		this.frame = frame;
		this.wlbmItem = new WlbmMenuItem(frame,node);//原材料定额
		this.cjlbjMenuItem = new CjlbjMenuItem(frame,node);//主要材料定额
		this.sjycldeMenuItem = new SjycldeMenuItem(this.frame,node);//试件原材料定额
//		this.setText("材料定额");
		this.setText("零件工艺定额");
		this.add(wlbmItem);
		this.add(cjlbjMenuItem);
		this.add(sjycldeMenuItem);
		setEnabled(true);
	}

	public void setEnable(boolean b) {
		setEnabled(b);
	}

}

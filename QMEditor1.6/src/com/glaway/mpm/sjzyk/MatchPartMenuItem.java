package com.glaway.mpm.sjzyk;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import org.dom4j.Element;

import com.glaway.mpm.erp.CmMenuItem;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.TechnicsMessageTreeObject;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.view.XWTreeObject;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class MatchPartMenuItem extends CmMenuItem {

	private static final long serialVersionUID = 1L;
	private NewTechnicsPart frame;
	private XWTreeNode node;
	private String title = "PBOM标准件/元器件匹配设计编码";

	public MatchPartMenuItem(NewTechnicsPart frame,XWTreeNode node) {
		this.setText(title);
		this.setIconStr("view2d.png");
		this.frame = frame;
		this.node = node;
		setEnabled(true);
	}

	private boolean displayValidate(XWTreeNode node) {
		if(node != null) {
			XWTreeObject treeObject = node.getObject();
			if(treeObject instanceof TechnicsMessageTreeObject) {
				TechnicsMessageTreeObject obj = (TechnicsMessageTreeObject)treeObject;
				Element element = obj.getTechnicsDocument().getRootElement();
				String state = element.attributeValue("lifecycle");
				if("正在工作".equals(state) || "修改中".equals(state)) {
					return true;
				}
				String techNumber = element.attributeValue("technicsNumber");
				String cldestate = "";
				try {
					cldestate = TechnicsIntf.getCLDEState(techNumber);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				if("已批准".equals(cldestate)) {
					return false;
				}
			}
		}
		return false;
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		ShowMatchPartJDialog dialog = new ShowMatchPartJDialog(frame, node, true);
		dialog.setVisible(true);
	}

}

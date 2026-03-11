package com.glaway.mpm.sjzyk;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import org.dom4j.Element;

import com.glaway.mpm.erp.CmMenuItem;
import com.glaway.mpm.erp.TxwLbmDialog;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.TechnicsMessageTreeObject;
import com.glaway.mpm.view.XWTechnicsTreeObject;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.view.XWTreeObject;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class ZycldeMenuItem extends CmMenuItem {

	private static final long serialVersionUID = 1L;
	private NewTechnicsPart frame;
	private XWTreeNode node;
	private String title = "";
	private static VaActionProgressBar progressBar;

	public ZycldeMenuItem(NewTechnicsPart frame,XWTreeNode node) {
		this.setText("主要材料定额");
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
		progressBar = new VaActionProgressBar(null, frame, "主要材料定额", "正在初始化界面,请等待...", "界面加载中");
    	Thread thread = new Thread() {
			public void run() {
				node = frame.technicsTreePanel.getSelectedTreeNode();
				ShowZYCLDEForSJZYKJDialog dialog = new ShowZYCLDEForSJZYKJDialog(frame, node, true, progressBar);
				dialog.setVisible(true);
			}
		};
		thread.start();
		progressBar.setVisible(true);
	}
}

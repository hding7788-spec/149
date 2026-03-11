package com.glaway.mpm.erp;

import com.glaway.mpm.view.*;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Element;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

public class ZycldeMenuItem extends CmMenuItem {

	private static final long serialVersionUID = 1L;
	private XWTreeNode node;
	NewTechnicsPart frame;
	String title = "";

	public ZycldeMenuItem(NewTechnicsPart frame, XWTreeNode node) {
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

	protected void actionPerformed(ActionEvent evt) {
		node = frame.technicsTreePanel.getSelectedTreeNode();
		String techniceNumber = "";
		if (node != null) {
			XWTreeObject xo = node.getObject();
			if ((xo instanceof XWTechnicsTreeObject)) {
				Element element = xo.getTreeCellData();
				techniceNumber = element.attributeValue("technicsNumber");
			}
		}
		title = "【" + techniceNumber + "】" + "主要材料定额";
		final VaActionProgressBar progressBar = new VaActionProgressBar(frame, "主要材料定额", "正在加载数据,请等待...", "数据加载中");
		Thread thread = new Thread() {
			public void run() {
				new TxcjlbjDialog(frame, title, node);

				progressBar.setHeaderMessage("数据加载完成！");
				progressBar.finish();
				progressBar.setVisible(false);
			}
		};
		thread.start();
		progressBar.setVisible(true);
	}

}

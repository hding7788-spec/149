package com.glaway.mpm.erp;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

import org.dom4j.Element;

import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.TechnicsMessageTreeObject;
import com.glaway.mpm.view.XWTechnicsTreeObject;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.view.XWTreeObject;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class SjycldeMenuItem extends CmMenuItem {

	private static final long serialVersionUID = 1L;
	private NewTechnicsPart frame;
	private XWTreeNode node;
	String title = "";
	private String type;

	public SjycldeMenuItem(NewTechnicsPart frame,XWTreeNode node) {
		this.setText("试件原材料定额");
		this.setIconStr("view2d.png");
		this.frame = frame;
		this.node = node;
		setEnabled(true);
	}

	public SjycldeMenuItem(NewTechnicsPart frame,XWTreeNode node, String type) {
		this.setText("试件原材料定额");
		this.setIconStr("view2d.png");
		this.frame = frame;
		this.node = node;
		this.type = type;
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
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
		node = frame.technicsTreePanel.getSelectedTreeNode();
		String techniceNumber = "";
		if (node != null) {
			XWTreeObject xo = node.getObject();
			if ((xo instanceof XWTechnicsTreeObject)) {
				Element element = xo.getTreeCellData();
				techniceNumber = element.attributeValue("technicsNumber");
			}
		}
		title = "【" + techniceNumber + "】" + "试件原材料定额";

		final VaActionProgressBar progressBar = new VaActionProgressBar(frame, "试件原材料定额", "正在加载数据,请等待...", "数据加载中");
		Thread thread = new Thread() {
			public void run() {
				new SjycldeDialog(frame,title,node,type);

				progressBar.setHeaderMessage("数据加载完成！");
				progressBar.finish();
				progressBar.setVisible(false);
			}
		};
		thread.start();
		progressBar.setVisible(true);
	}
}

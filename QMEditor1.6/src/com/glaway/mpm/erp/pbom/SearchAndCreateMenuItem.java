package com.glaway.mpm.erp.pbom;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.dom4j.Element;

import com.glaway.mpm.erp.CmMenuItem;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWPartTreeObject;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.view.XWTreeObject;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;

public class SearchAndCreateMenuItem extends CmMenuItem {

	private static final long serialVersionUID = 1L;
	private XWTreeNode node;
	private NewTechnicsPart frame;
	String title = "";

	public SearchAndCreateMenuItem(XWTreeNode node, NewTechnicsPart frame) {
		this.setText("从ERP查询添加外购件");
		this.setIconStr("view2d.png");
		this.frame = frame;
		this.node = node;
		setEnabled(displayValidate(this.node));
	}

	private boolean displayValidate(XWTreeNode node) {
		return true;
	}

	protected void actionPerformed(ActionEvent evt) {
		node = frame.xwPartTreePanel.getSelectedTreeNode();
		XWTreeObject obj = node.getP().getObject();
		if(obj instanceof XWPartTreeObject) {
			XWPartTreeObject partObj = (XWPartTreeObject) obj;
			Element partEle = partObj.getTreeCellData();
			title = "【" + partEle.attributeValue("partNumber") + "】" + "查询添加外购件";

			final VaActionProgressBar progressBar = new VaActionProgressBar(null, frame, "查询添加外购件", "正在加载数据,请等待...", "数据加载中");
			Thread thread = new Thread() {
				public void run() {
					new SearchAndCreateDialog(frame, node, title);

					progressBar.setHeaderMessage("数据加载完成！");
					progressBar.finish();
					progressBar.setVisible(false);
				}
			};
			thread.start();
			progressBar.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(frame, "请在PBOM树上选中工艺节点！");
		}
	}

}

package com.glaway.mpm.erp.pbom;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JOptionPane;

import org.dom4j.Element;

import com.glaway.mpm.erp.CmMenuItem;
import com.glaway.mpm.util.ValueCache;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWPartTreeObject;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.view.XWTreeObject;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;

public class MatchClbmMenuItem extends CmMenuItem {

	private static final long serialVersionUID = 1L;
	private NewTechnicsPart frame;
	private XWTreeNode node;
	private String title = "外购件匹配ERP物资编码";

	public MatchClbmMenuItem(NewTechnicsPart frame,XWTreeNode node) {
		this.setText(title);
		this.setIconStr("view2d.png");
		this.frame = frame;
		this.node = node;
		setEnabled(true);
	}

	private boolean displayValidate(XWTreeNode node) {
		return true;

	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		final VaActionProgressBar progressBar = new VaActionProgressBar(null,frame, "查询匹配外购件物资编码", "正在加载数据,请等待...", "数据加载中");
		Thread thread = new Thread() {
			public void run() {
				List<XWTreeNode> selNodes = new ArrayList<XWTreeNode>();
				node = frame.xwPartTreePanel.getSelectedTreeNode();
//				System.out.println("---------node---"+node);
				Enumeration<XWTreeNode> enu = node.getP().children();
				while(enu.hasMoreElements()) {
					XWTreeNode partNode = enu.nextElement();
					XWTreeObject treeObj = partNode.getObject();
					if(treeObj instanceof XWPartTreeObject) {
						XWPartTreeObject partObj = (XWPartTreeObject)treeObj;
						Element partEle = partObj.getTreeCellData();
						String mtype = partEle.attributeValue("MTYPE");
						String number = partEle.attributeValue("partNumber");
						String ctype = partEle.attributeValue("CTYPE");
						ValueCache.allPartsType.put(number, mtype);
						if("标准件".equals(mtype) || "元器件".equals(mtype) || "外购件".equals(mtype)) {
							selNodes.add(partNode);
						}else if("标准件".equals(ctype) || "元器件".equals(ctype) || "外购件".equals(ctype)){
							selNodes.add(partNode);
						}
					}
				}

//				if (selNodes == null || selNodes.isEmpty()) {
//					progressBar.setVisible(false);
//					JOptionPane.showMessageDialog(frame, "没有可操作的标准件/元器件/外购件！");
//					return;
//				}

				new MatchClbmDialog(frame, selNodes,title,node);

				progressBar.setHeaderMessage("数据加载完成！");
				progressBar.finish();
				progressBar.setVisible(false);
			}
		};
		thread.start();
		progressBar.setVisible(true);
	}

}

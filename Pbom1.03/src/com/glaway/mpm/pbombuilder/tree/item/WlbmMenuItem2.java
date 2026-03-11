package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import wt.part.WTPart;

import com.glaway.mpm.pbombuilder.action.CmActionProgressBar;
import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.TxwLbmDialog2;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.ErpUtil;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

public class WlbmMenuItem2 extends CmMenuItem {

	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode node;
	private Window owner;
	private String title = "修改标准件/元器件物资编码";

	public WlbmMenuItem2(CmTree tree, CmTreeNode node, Window owner) {
		this.setText(title);
		this.setIconStr("view2d.png");
		this.node = node;
		this.tree = tree;
		this.owner = owner;
		setEnabled(displayValidate(this.node));
	}

	private boolean displayValidate(CmTreeNode node) {
		CmTreeNode rootPart = (CmTreeNode)tree.getRoot().children().nextElement();
		String mtype = node.getPart().getMtype();
		if(null==node.getParent()){
			return false;
		}
		else if ("NS".equals(node.getPart().getPartType())) {//通过PBOM新建的标准件不允许修改，只能先删除然后再新建
			return false;
		}
		else if (!"标准件".equals(mtype) && !"元器件".equals(mtype)) {
			return false;
		}
//		else if (rootPart.getPart().getContainerId() != node.getPart().getContainerId()) {//借用件
//			return false;
//		}
		else if(CmCommonStringUtil.isEmpty(node.getPart().getWzk().getInvcode())){
			return false;
		}
//		else if(CmCommonStringUtil.isPackage(node)){
//			return false;
//		}
		else {
			return !CmCommonStringUtil.isHasFilingOfObj(node);
		}
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		final CmActionProgressBar progressBar = new CmActionProgressBar(null,owner, "修改标准件/元器件物资编码", "正在加载数据,请等待...", "数据加载中");
		Thread thread = new Thread() {
			public void run() {
				CmLightPart lightPart = node.getPart();
				WTPart part = CmBizObjUtil.getWTPartFromLightPart(lightPart);
				TreePath[] paths = tree.getSelectionPaths();
				List<CmTreeNode> selNodes = new ArrayList<CmTreeNode>();

				for (TreePath path : paths) {
					CmTreeNode node = (CmTreeNode) path.getLastPathComponent();
					if (null == node.getParent() || "PBOM".equals(node.toString())) {
					} else {
						CmLightPart rootPart = node.getPart();
						String parentPartNumber = node.getPart().getParentPartNumber();
						long oid = rootPart.getOid();
						String partType = node.getPart().getMtype();//PBOMEditorToWCIntf.getPartTypeByPartOid(oid);
						if((partType != null) && (ErpUtil.ERP_PARTTYPE_WGJ.equals(partType)
								|| ErpUtil.ERP_PARTTYPE_BZJ.equals(partType) || ErpUtil.ERP_PARTTYPE_YQJ.equals(partType))) {
							//String chbm = PBOMEditorToWCIntf.getPartLinkIBAValueByPartOid(parentPartNumber,oid, "CHBM");
							if(!CmCommonStringUtil.isEmpty(node.getPart().getWzk().getInvcode())) {
								selNodes.add(node);
							}
						}
					}
				}

				if (selNodes == null || selNodes.isEmpty()) {
					progressBar.setVisible(false);
					JOptionPane.showMessageDialog(owner, "你选择的标准件/元器件还没有填写存货编码，请先选择填写存货编码！");
					return;
				}

				new TxwLbmDialog2(tree,owner, selNodes,title,node);

				progressBar.setHeaderMessage("数据加载完成！");
				progressBar.finish();
				progressBar.setVisible(false);
			}
		};
		thread.start();
		progressBar.setVisible(true);
	}

	private void getAllChildPart(CmTreeNode node,List<CmTreeNode> selNodes) {
		Enumeration enu = node.children();
		while(enu.hasMoreElements()) {
			CmTreeNode childNode = (CmTreeNode)enu.nextElement();
			CmLightPart childPart = childNode.getPart();
			long childOid = childPart.getOid();
			String partType = PBOMEditorToWCIntf.getPartTypeByPartOid(childOid);
			if((partType != null) && (ErpUtil.ERP_PARTTYPE_WGJ.equals(partType)
					|| ErpUtil.ERP_PARTTYPE_BZJ.equals(partType) || ErpUtil.ERP_PARTTYPE_YQJ.equals(partType))) {
				String chbm = PBOMEditorToWCIntf.getPartIBAValueByPartOid(childOid, "CHBM");
				if(chbm == null || "".equals(chbm)) {
					selNodes.add(childNode);
				}
			}
			getAllChildPart(childNode,selNodes);
		}
	}

}

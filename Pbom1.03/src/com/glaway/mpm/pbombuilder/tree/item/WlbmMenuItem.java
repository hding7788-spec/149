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
import com.glaway.mpm.pbombuilder.tree.dialog.TxwLbmDialog;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.ErpUtil;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

public class WlbmMenuItem extends CmMenuItem {

	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode node;
	private Window owner;
	private String title = "查询填写标准件/元器件物资编码";

	public WlbmMenuItem(CmTree tree, CmTreeNode node, Window owner) {
		this.setText(title);
		this.setIconStr("view2d.png");
		this.node = node;
		this.tree = tree;
		this.owner = owner;
		setEnabled(displayValidate(this.node));
	}

	private boolean displayValidate(CmTreeNode node) {
//		if(CmCommonStringUtil.isPackageOfParent(node)||CmCommonStringUtil.isPackage(node)){
//			//父节点是打包结构时
//			return false;
//		}
		String mtype = node.getPart().getMtype();
		if(null==node.getParent()){
			return false;
		}
		else if (!"标准件".equals(mtype) && !"元器件".equals(mtype)) {
			return false;
		}
		else if ("NS".equals(node.getPart().getPartType())) {//通过PBOM新建的标准件不允许修改，只能先删除然后再新建
			return false;
		}
		else if(!CmCommonStringUtil.isEmpty(node.getPart().getWzk().getInvcode())){
			return false;
		}
		else {
			return true;
		}

	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		final CmActionProgressBar progressBar = new CmActionProgressBar(null,owner, "查询填写标准件/元器件物资编码", "正在加载数据,请等待...", "数据加载中");
		Thread thread = new Thread() {
			public void run() {
				CmLightPart lightPart = node.getPart();
				WTPart part = CmBizObjUtil.getWTPartFromLightPart(lightPart);
				TreePath[] paths = tree.getSelectionPaths();
				List<CmTreeNode> selNodes = new ArrayList<CmTreeNode>();
				StringBuffer msg = new StringBuffer();

				for (TreePath path : paths) {
					CmTreeNode node = (CmTreeNode) path.getLastPathComponent();
					if (null == node.getParent() || "PBOM".equals(node.toString())) {
						// nodeList.add((CmTreeNode) node.children().nextElement());
					} else {
						CmLightPart nodePart = node.getPart();
						String parentPartNumber = node.getPart().getParentPartNumber();
						long oid = nodePart.getOid();
						String partType = nodePart.getMtype();//PBOMEditorToWCIntf.getPartTypeByPartOid(oid);
						if((partType != null) && (ErpUtil.ERP_PARTTYPE_BZJ.equals(partType) || ErpUtil.ERP_PARTTYPE_YQJ.equals(partType))) {
							String chbm = PBOMEditorToWCIntf.getPartLinkIBAValueByPartOid(parentPartNumber,oid, "CHBM");
							if(chbm == null || "".equals(chbm)) {
								selNodes.add(node);
							}
						}
						getAllChildPart(node,selNodes);
					}
				}

				// List<CmTreeNode> selNodes =tree.getSelectedNodes();
				if (selNodes == null || selNodes.isEmpty()) {
					progressBar.setVisible(false);
					JOptionPane.showMessageDialog(owner, "没有找到可操作的标准件/元器件！");
					return;
				}

				new TxwLbmDialog(tree,owner, selNodes,title,node);

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
			String parentPartNumber = node.getPart().getParentPartNumber();
			long childOid = childPart.getOid();
			String partType = PBOMEditorToWCIntf.getPartTypeByPartOid(childOid);
			if((partType != null) && (ErpUtil.ERP_PARTTYPE_WGJ.equals(partType)
					|| ErpUtil.ERP_PARTTYPE_BZJ.equals(partType) || ErpUtil.ERP_PARTTYPE_YQJ.equals(partType))) {
				String chbm = PBOMEditorToWCIntf.getPartLinkIBAValueByPartOid(parentPartNumber,childOid, "CHBM");
				if(chbm == null || "".equals(chbm)) {
					selNodes.add(childNode);
				}
			}
			getAllChildPart(childNode,selNodes);
		}
	}

}

package com.glaway.mpm.visual.view.tree.menu;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.glaway.mpm.util.PbomUtil;
import com.glaway.mpm.visual.bean.VaLightPart;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.glaway.mpm.wcIntf.PBomIntf;

/**
 * <br>
 * Created on 2012-11-21
 * 
 * @author chenyunlong
 */
public class VaBomMPView2DMenuItem extends VaMenuItem {

	private static final long serialVersionUID = -8396429397442896329L;
	private VaTreeNode node;
	private Window owner;
	private VaTree tree;

	public VaBomMPView2DMenuItem(VaTree tree, VaTreeNode currNode, Window owner) {
		this.node = currNode;
		this.owner = owner;
		this.tree = tree;
		setText("可视化2D图形");
		setIconStr("view2d.png");
//		setEnabled(displayValidate(this.node));
	}

//	private boolean displayValidate(VaTreeNode node) {
//		// 如果PBOM节点下没有子节点，不可显示可视化
//		TreePath[] paths =  CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
//		if (paths.length > 1) {
//			return false;
//		} else if ((null == node.getParent())) {
//			return false;
//		} else {
//			return true;
//		}
//	}

	/**
	 * @param evt
	 */
	@Override
	protected void actionPerformed(ActionEvent evt) {
		VaLightPart lightPart = this.node.getPart();
		if(lightPart == null){
			JOptionPane.showMessageDialog(owner, "该节点没有零件");
			return;
		}
		String oid = String.valueOf(lightPart.getOid());
		if (oid != null && !"".equals(oid.trim()) ) {
			String url = PBomIntf.get2DDocumentUrl(String.valueOf(lightPart.getOid()),
					PbomUtil.DESIGNOID);
			System.out.println("url : " + url);
			if (url == null || "".equals(url.trim())) {
				JOptionPane.showMessageDialog(owner, "没有2D可视化结构");
			} else {
				try {
					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			JOptionPane.showMessageDialog(owner, "没有2D可视化结构");
		}
	}

}

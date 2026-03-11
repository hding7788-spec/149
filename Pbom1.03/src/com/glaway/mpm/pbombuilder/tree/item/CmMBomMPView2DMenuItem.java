package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.bom.CmConnectFrame;
import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

/**
 * <br>
 * Created on 2012-11-21
 * 
 * @author chenyunlong
 */
public class CmMBomMPView2DMenuItem extends CmMenuItem {

	private static final long serialVersionUID = -8396429397442896329L;
	private CmTreeNode node;
	private Window owner;
	private CmTree tree;

	public CmMBomMPView2DMenuItem(CmTree tree, CmTreeNode currNode, Window owner) {
		this.node = currNode;
		this.owner = owner;
		this.tree = tree;
		setText("可视化2D图形");
		setIconStr("view2d.png");
		setEnabled(displayValidate(this.node));
	}

	private boolean displayValidate(CmTreeNode node) {
		// 如果PBOM节点下没有子节点，不可显示可视化
		TreePath[] paths =  CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if (paths.length > 1) {
			return false;
		} else if ((null == node.getParent())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @param evt
	 */
	@Override
	protected void actionPerformed(ActionEvent evt) {
		CmLightPart lightPart = this.node.getPart();
		if (null != lightPart && !CmCommonStringUtil.isEmpty(String.valueOf(lightPart.getOid()))) {
			String url = PBOMEditorToWCIntf.get2DDocumentUrl(String.valueOf(lightPart.getOid()),
					CmConnectFrame.designOid);
			if (CmCommonStringUtil.isEmpty(url)) {
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

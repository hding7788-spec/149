package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

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
public class CmMBomMPViewMenuItem extends CmMenuItem {

	private static final long serialVersionUID = -8396429397442896329L;
	protected CmTreeNode node;
	protected Window owner;
	protected CmTree tree;

	public CmMBomMPViewMenuItem(CmTree tree,CmTreeNode currNode, Window owner) {
		this.node = currNode;
		this.owner = owner;
		this.tree = tree;
		setText("可视化3D模型");
		setIconStr("view3d.png");
		setEnabled(displayValidate(this.node));
	}

	private boolean displayValidate(CmTreeNode node) {
		// 如果PBOM节点下没有子节点，不可显示可视化
		TreePath[] paths = CmCommonNodeUtil.removeSelectNodeFromPackage(tree.getSelectionPaths());
		if(paths.length > 1)
			return false;
		else if((null == node.getParent()))
			return false;

		return true;
	}

	/**
	 * @param evt
	 */
	@Override
	protected void actionPerformed(ActionEvent evt) {
		CmLightPart lightPart=this.node.getPart();
		if(null != lightPart && !CmCommonStringUtil.isEmpty(String.valueOf(lightPart.getOid()))){
			String url = PBOMEditorToWCIntf.get3DDocumentUrl(String.valueOf(lightPart.getOid()));
			if(CmCommonStringUtil.isEmpty(url)) {
				if(node.getPart().getPartType().equals("middle") && node.getChildCount() > 0) {
					Set<Long> partOids = new HashSet<Long>();
					initMiddlePartChildren(node,partOids);

				} else {
					JOptionPane.showMessageDialog(owner, "没有3D可视化结构");
				}
			} else {
				try {
					//Runtime.getRuntime().exec("cmd /c start "+url);
					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
				} catch(IOException e1) {
					JOptionPane.showMessageDialog(owner,e1.toString(),"错误",JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		}else{
			JOptionPane.showMessageDialog(owner, "没有3D可视化结构");
		}
	}

	private void initMiddlePartChildren(CmTreeNode parentNode,Set<Long> partOids) {
		for(int i = 0;i < parentNode.getChildCount();i++) {
			CmTreeNode child = (CmTreeNode) parentNode.getChildAt(i);
			if(child.isLeaf()) {
				checkNode(child,partOids);
				continue;
			}
			if(child.isIspackage()) {
				initParts(child.getListNode().get(0),partOids);
			} else {
				initParts(child,partOids);
			}
		}
	}

	private void initParts(CmTreeNode parentNode,Set<Long> partOids) {
		for(CmTreeNode child : node.getChildren()) {
			if(child.isLeaf()) {
				checkNode(child,partOids);
				continue;
			}
			if(child.isIspackage()) {
				initParts(child.getListNode().get(0),partOids);
			} else {
				initParts(child,partOids);
			}
		}
	}

	private void checkNode(CmTreeNode node,Set<Long> partOids) {
		CmLightPart childPart;
		if(node.isIspackage())
			childPart = node.getListNode().get(0).getPart();
		else
			childPart = node.getPart();

		if(!partOids.contains(childPart.getOid()))
			partOids.add(childPart.getOid());
	}
}

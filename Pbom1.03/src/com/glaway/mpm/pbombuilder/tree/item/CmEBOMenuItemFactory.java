package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.util.List;
import java.util.Vector;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.data.CmMenuItemFactory;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;

/**
 * <br>
 * Created on 2012-10-28
 *
 * @author chenyunlong
 */
public class CmEBOMenuItemFactory extends CmMenuItemFactory {
	private boolean isEdit = true;

	public CmEBOMenuItemFactory(boolean b) {
		this.isEdit = b;
	}

	public CmMenuItem[] createMenuItem(CmTree tree, CmTreeNode currNode, Window owner) {
		List<CmMenuItem> list = new Vector<CmMenuItem>();
		// CmMBomShowPathMenuItem showPath = new
		// CmMBomShowPathMenuItem(currNode, owner);
		CmMBomCopyNodeMenuItem copyNode = new CmMBomCopyNodeMenuItem(tree, currNode);
		CmPackageNodeMenuItem packageNode = new CmPackageNodeMenuItem(tree, currNode);
		CmUnPackageNodeMenuItem unPackageNode = new CmUnPackageNodeMenuItem(tree, currNode);
		CmMBomMPViewMenuItem cmMBomMPViewMenuItem=new CmMBomMPViewMenuItem(tree,currNode, owner);
		CmMBomMPView2DMenuItem cmMBomMPView2DMenuItem = new CmMBomMPView2DMenuItem(tree,currNode, owner);
		CmMBomCompareEbomMenuItem cmMBomCompareEBomMenuItem = new CmMBomCompareEbomMenuItem(tree,currNode, owner);
		// list.add(showPath);
		if (isEdit) {
			list.add(copyNode);
		}
		list.add(packageNode);
		list.add(unPackageNode);
		list.add(cmMBomMPViewMenuItem);
		list.add(cmMBomMPView2DMenuItem);
		list.add(cmMBomCompareEBomMenuItem);
		list.add(new CmCancelMenuItem());
		list.add(new CmCancelMenuItem());
		return list.toArray(new CmMenuItem[0]);
	}

}

/**
 * <br>Created on 2011-3-28
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.visual.view.tree.menu;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.glaway.mpm.qmIntf.participatePart.ParticipatePartAddDialog;
import com.glaway.mpm.qmIntf.participatePart.VaClipboard;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;

public class VaEBomClearFittingsMenuItem extends VaMenuItem {

	private static final long serialVersionUID = -8396429397442896329L;
	private VaTree tree;
	private static VaLogger logger = VaLogger.getLogger(VaEBomClearFittingsMenuItem.class);
	public VaEBomClearFittingsMenuItem(VaTree tree) {
		this.tree = tree;
		setText("恢复参装件");
//		setIconStr("copy.gif");
	}

	/**
	 * @param evt
	 */
	@Override
	protected void actionPerformed(ActionEvent evt) {

		TreePath[] paths = tree.getSelectionPaths();
		
		Vector vFittings = new Vector();
		for (TreePath path : paths) {
			VaTreeNode node = (VaTreeNode) path.getLastPathComponent();
			if(node.isUsed()){
				vFittings.add(node.getOccId());
				node.setUsed(false);
//				((DefaultTreeModel) tree.getModel()).nodeChanged(node);
			}
		}

		try {
			logger.debug("FRame :::: "+com.glaway.mpm.controller.CancelPartHandler.frame);
			logger.debug("clear :::: "+com.glaway.mpm.controller.CancelPartHandler.clearFittings(vFittings, false));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}

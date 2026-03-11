package com.glaway.mpm.visual.view.tree.menu;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import com.glaway.mpm.qmIntf.participatePart.VaClipboard;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;

public class VaEBomCopyNodeMenuItem extends VaMenuItem {

	private static final long	serialVersionUID	= -8396429397442896329L;
	private VaTree				tree;

	// private static VaLogger logger =
	// VaLogger.getLogger(VaEBomCopyNodeMenuItem.class);

	public VaEBomCopyNodeMenuItem(VaTree tree) {
		this.tree = tree;
		setText("复制");
		setIconStr("copy.gif");
	}

	/**
	 * @param evt
	 */
	@Override
	protected void actionPerformed(ActionEvent evt) {
		VaClipboard.clipboard2.clear();

		// boolean isSamePart = false;
		TreePath[] paths = tree.getSelectionPaths();
		// List<VaTreeNode> nodeList = new ArrayList<VaTreeNode>();
		for (TreePath path : paths) {
			VaTreeNode node = (VaTreeNode) path.getLastPathComponent();

//			if (!this.validateCopyNode(node)) {
//				continue;
//			}

			// Map<String, String> partMap = new HashMap();
			// isSamePart = false;
			//
			// for (Map<String, String> map : VaClipboard.clipboard) {
			// String key = "oid";
			// String value = String.valueOf(node.getPart().getOid());
			//
			// if (map.containsKey(key) && map.containsValue(value)) {
			// map.put("occId", map.get("occId") + "," + node.getOccId());
			// map.put("useCount", String.valueOf(Integer.parseInt(map
			// .get("useCount")) + 1));
			//
			// isSamePart = true;
			// break;
			// }
			// }
			// // partMap.containsKey(key)
			// if (!isSamePart) {
			// partMap.put("partNumber", node.getPart().getNumber());
			// partMap.put("oid", String.valueOf(node.getPart().getOid()));
			// partMap.put("occId", node.getOccId());
			// partMap.put("partName", node.getPart().getName());
			// partMap.put("material", "");
			// partMap.put("dutu", "");
			// partMap.put("remark", "");
			// partMap.put("useCount", "1");
			// nodeList.add(node);
			// VaClipboard.clipboard.add(partMap);
			// }
			VaClipboard.clipboard2.add((VaTreeNode) node);
		}
		// ParticipatePartAddDialog.getInstance().setVisible(false);
		// logger.debug(VaClipboard.clipboard2);
	}

	private boolean validateCopyNode(VaTreeNode treenode) {
		if (treenode.isLeaf() && !"PBOM".equals(treenode.getParent().toString())) {
			if (treenode.isUsed()) {
				JOptionPane.showMessageDialog(null, "该零件已参装!");
				return false;
			}
			return true;
		} else {
			JOptionPane.showMessageDialog(null, "不能选择根节点或装配整件!");
			return false;
		}
	}
}

package com.glaway.mpm.qmIntf.decoratePView.showPanel.view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.glaway.mpm.task.CmTaskInfo;
import com.glaway.mpm.task.CmTaskResultSet;
import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.task.util.CmTaskHelper;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.tree.VaDefaultTreeLinkage;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeLinkage;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.glaway.mpm.visual.view.tree.VaTreeNodeComparator;
import com.ptc.pview.pvapi.SelectionObserver;
import com.ptc.pview.pvkapp.Instance;

public class DpEPVSelectionObserver extends SelectionObserver {
	private VaLogger			log	= VaLogger.getLogger();
	private VaTree				tree;
	private List<VaTreeNode>	nodeFromPrvList;

	public DpEPVSelectionObserver(VaTree theCurrentTree) {
		tree = theCurrentTree;
		nodeFromPrvList = new ArrayList<VaTreeNode>();
	}

	protected void OnBeginUpdate() {
	}

	protected void OnEndUpdate() {
	}

	protected void OnInsertItems(Instance[] items, long recurseMask) {
		log.debug("dp:oninsertitems");
		TreePath[] theTreePaths = new TreePath[items.length];
		if (tree != null && tree.getRoot().getChildCount() > 0) {
			doJob(tree, theTreePaths, items);
		}
	}

	protected void OnRemoveItems(Instance[] items, long recurseMask) {
		//logger.debug("dp:PVSelectionObserver.OnRemoveItems()");
	}

	protected void OnClearSelection() {
		log.debug("dp :OnClearSelection");
		nodeFromPrvList.clear();
		// ICBMainFrame.getMainFrame().getICBTree().setSelectionPath(null);
	}

	private void doJob(VaTree a_tree, TreePath[] theTreePaths, Instance[] items) {
		log.debug("dp:doJob");

		if (theTreePaths != null) {
			VaTreeNode theNode = null;
			for (int i = 0; i < items.length; i++) {
				Instance theInstance = items[i];
				theNode = a_tree.getNodeFromInstance(theInstance);

				if (theNode != null && !theNode.getUserObject().equals("EBOM")) {

					if (theNode.getPath().length > 3) {
						TreeNode[] pathNodes = theNode.getPath();

						VaTreeNode treeNode = (VaTreeNode) pathNodes[2];
						nodeFromPrvList.add(treeNode);
					} else {
						nodeFromPrvList.add(theNode);
					}

					log.debug("selected node " + theNode.getPart().getName());
					// Vector bboxs = theNode.getBboxes();
					// VaMainPanel main = VaMainPanel.getInstance();
					// if (bboxs != null) {
					// for (Object obj : bboxs) {
					// BoundingBox bbox = (BoundingBox) obj;
					// Point3d lower = new Point3d();
					// Point3d upper = new Point3d();
					// bbox.getLower(lower);
					// bbox.getUpper(upper);

					// main.setX1(String.valueOf(lower.getX() * 1000.0));
					// main.setX2(String.valueOf(upper.getX() * 1000.0));
					// main.setY1(String.valueOf(lower.getY() * 1000.0));
					// main.setY2(String.valueOf(upper.getY() * 1000.0));
					// main.setZ1(String.valueOf(lower.getZ() * 1000.0));
					// main.setZ2(String.valueOf(upper.getZ() * 1000.0));
					// }
					// }

					// main.setReferenceString(theNode.getPart().getNumber());
					// main.setInstanceString(theNode.getOccId());

					theTreePaths[i] = new TreePath(theNode.getPath());
				}
			}
			// ������CmTree ����ѡ������Ϣ
			// CmTree etree =
			// CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree();
			// CmTree mtree =
			// CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();

			Comparator<VaTreeNode> comtor = new VaTreeNodeComparator();
			VaTreeLinkage linkage = new VaDefaultTreeLinkage(comtor).addLinkageTree(tree);
			linkage.valueChange(nodeFromPrvList);

			// 设置选中可视化信息部件
			CmTaskInfo pastePViewTaskInfo = CmTaskInfo.newCmTaskInfo("DpTreePanel.setSelectedPViewNodeList", this,
					nodeFromPrvList);
			try {
				CmTaskResultSet pviewResultSet = CmTaskHelper.sendTask(pastePViewTaskInfo, null);
			} catch (CmTaskException e) {
				e.printStackTrace();
			}
		}
	}

	public String GetObjectClass() {
		log.debug("GetObjectClass");
		return "com::glaway::mpm::view::pview::DpEPVSelectionObserver";
	}
}

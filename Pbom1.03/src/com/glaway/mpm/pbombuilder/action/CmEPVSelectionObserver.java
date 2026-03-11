package com.glaway.mpm.pbombuilder.action;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.data.CmDefaultTreeLinkage;
import com.glaway.mpm.pbombuilder.data.CmTreeLinkage;
import com.glaway.mpm.pbombuilder.data.CmTreeNodeComparator;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmTaskInfo;
import com.glaway.mpm.pbombuilder.util.CmTaskResultSet;
import com.glaway.mpm.pbombuilder.util.PviewTask;
import com.ptc.pview.pvapi.SelectionObserver;
import com.ptc.pview.pvkapp.Instance;

public class CmEPVSelectionObserver extends SelectionObserver {
	private static final CmLogger	log	= CmLogger.getLogger(CmEPVSelectionObserver.class);
	private CmTree					tree;
	private List<CmTreeNode>		nodeFromPrvList;

	public CmEPVSelectionObserver(CmTree theCurrentTree) {
		tree = theCurrentTree;
		nodeFromPrvList = new ArrayList<CmTreeNode>();
	}

	protected void OnBeginUpdate() {
		System.out.println("PVSelectionObserver.OnBeginUpdate()");
	}

	protected void OnEndUpdate() {
		System.out.println("PVSelectionObserver.OnEndUpdate()");
	}

	protected void OnInsertItems(Instance[] items, long recurseMask) {
		System.out.println("PVSelectionObserver.OnInsertItems()");
		TreePath[] theTreePaths = new TreePath[items.length];
		if (tree != null && tree.getRoot().getChildCount() > 0) {
			doJob(tree, theTreePaths, items);
		}
	}

	protected void OnRemoveItems(Instance[] items, long recurseMask) {
		System.out.println("PVSelectionObserver.OnRemoveItems()");
	}

	protected void OnClearSelection() {
		// ICBMainFrame.getMainFrame().getICBTree().setSelectionPath(null);
		System.out.println("PVSelectionObserver.OnClearSelection()");
		nodeFromPrvList.clear();
	}

	private void doJob(CmTree a_tree, TreePath[] theTreePaths, Instance[] items) {
		// List<CmTreeNode> nodeFromPrvList = new
		// ArrayList<CmTreeNode>();//存放选中的可视化信息部件

		if (theTreePaths != null) {
			CmTreeNode theNode = null;
			for (int i = 0; i < items.length; i++) {
				Instance theInstance = items[i];
				theNode = a_tree.getNodeFromInstance(theInstance);
				if (theNode != null && !"EBOM".equals(theNode.toString()) && null == theNode.getParent()) {
					theNode = a_tree.getNodeFromTreeWith(a_tree.getRoot(), theNode);
					theNode.setIspackage(false);
				}

				if (theNode != null) {
					theNode.setCreoClick(false);
					nodeFromPrvList.add(theNode);
					theTreePaths[i] = new TreePath(theNode.getPath());
				}
			}
			// 向其它CmTree 发送选择更改消息
			CmTree etree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree();
			CmTree mtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();

			Comparator<CmTreeNode> comtor = new CmTreeNodeComparator();
			CmTreeLinkage linkage = new CmDefaultTreeLinkage(comtor).addLinkageTree(etree).addLinkageTree(mtree);
			linkage.valueChange(nodeFromPrvList);

			// 设置选中可视化信息部件
			CmTaskInfo pastePViewTaskInfo = CmTaskInfo.newCmTaskInfo("CmEBomTreePanel.setSelectedPViewNodeList", this,
					nodeFromPrvList);
			try {
				CmTaskResultSet pviewResultSet = PviewTask.sendTask(pastePViewTaskInfo, null);
			} catch (CmTaskException e) {
				e.printStackTrace();
			}
		}
	}

	public String GetObjectClass() {
		return "ext::ideal::samc::jws::pview::CmEPVSelectionObserver";
	}
}

/*
 * @author wanghaoyu
 * @date 2013-4-17
 * 版权属 南京国睿信维软件有限公司 所有
 */

package com.glaway.mpm.qmIntf.fittingTool.view;

import com.glaway.mpm.qmIntf.decoratePView.showPanel.view.DpPViewScenesPanel;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpTecnicsNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpTreePanel;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.XmlUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.visual.view.VaContext;
import com.glaway.mpm.visual.view.pview.VaPViewFactory;
import com.glaway.mpm.visual.view.pview.VaPViewImpl;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.glaway.mpm.visual.view.ui.VaPVNotInstalledPanel;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FittingsInProcessPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	// private Logger logger = LogUtil.getLogger(this.getClass());
	private VaTree ebomTree;
	// private VaTree pbomTree;
	DpTreePanel leftPanel;
	private Window mainOwner;

	public FittingsInProcessPanel(Map<String, String> map, Window mainOwner) {
		this.mainOwner = mainOwner;
		setLayout(new BorderLayout());
		// logger.debug("map==" + map);
		// String oid = map.get("oid") + "";
		String partNumber = map.get("partNumber") + "";
		String xmlPath = map.get("xmlPath") + "";
		String technicsPath = map.get("technicsPath") + "";
		String stepNumber = map.get("stepNumber") + "";
		String paceNumber = map.get("paceNumber") + "";
		String procedureContent = map.get("procedureContent") + "";

		ebomTree = new VaTree(new VaTreeNode("EBOM"));
		ebomTree.initTree(partNumber.toString(), "", "EBOM");

		// if(NewTechnicsPart.flag){
		// ebomTree.expandAllLevels(ebomTree.getRoot());
		// JDialog dia = new JDialog();
		// dia.setTitle("FittingsInProcessPanel");
		// JScrollPane diaJsp = new JScrollPane(ebomTree);
		// dia.add(diaJsp);
		// dia.setVisible(true);
		// dia.setSize(400,800);
		// }

		// VaTreeNode rootNode = new VaTreeNode("PBOM");
		// pbomTree = new VaTree(rootNode);
		// pbomTree.buildPbomTree();
		// pbomTree.expandAllLevels(rootNode);
		//
		// JDialog pbomDia = new JDialog();
		// JScrollPane pbomDiaJsp = new JScrollPane(pbomTree);
		// pbomDia.add(pbomDiaJsp);
		// pbomDia.setTitle("PBOM树");
		// pbomDia.setVisible(true);
		// pbomDia.setSize(400,800);

		final JSplitPane jSplitPanel = new JSplitPane();

		if (VaPViewImpl.isPviewInitialized()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					jSplitPanel.setRightComponent(new DpPViewScenesPanel(VaPViewFactory.PV_NAME_DP));
				}
			});
		} else
			jSplitPanel.setRightComponent(new VaPVNotInstalledPanel());

		leftPanel = new DpTreePanel(xmlPath, technicsPath, stepNumber,
				paceNumber, procedureContent, ebomTree, mainOwner);
		jSplitPanel.setLeftComponent(leftPanel);

		// jSplitPanel.setEnabled(false);
		// jSplitPanel.setDividerSize(1);
		jSplitPanel.setDividerLocation(500);
		add(jSplitPanel);

		// initTreeSelectionListener();
	}

	public VaTree getEBomTree() {
		return this.ebomTree;
	}

	public VaTree getProcessTree() {
		return leftPanel.dpTree;
	}

	/**
	 * 配置VaTree的选择监听,包含联动控制
	 */
	// private void initTreeSelectionListener() {
	// ebomTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	// leftPanel.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	//
	// // Comparator comtor = new VaTreeNodeComparator();
	// Comparator comtor2 = new VaTreeNodeModelComparator();
	// VaTreeLinkage linkage_etree = new
	// VaDefaultTreeLinkage(comtor2).addLinkageTree(leftPanel.getTree());
	// VaTreeLinkage linkage_mtree = new
	// VaDefaultTreeLinkage(comtor2).addLinkageTree(ebomTree);
	//
	// VaTreeLinkage linkage_model = new
	// VaTreeModelLinkage(comtor2).addLinkageTree(ebomTree);
	//
	// TreeSelectionListener treeSelectListenre_etree = new
	// VaTreeSelectionListener(linkage_etree);
	// TreeSelectionListener treeSelectListenre_mtree = new
	// VaTreeSelectionListener(linkage_mtree);
	// TreeModelListener treeModelListener_mtree = new
	// VaTreeModelListener(linkage_model);
	//
	// ebomTree.addTreeSelectionListener(treeSelectListenre_etree);
	// leftPanel.getTree().addTreeSelectionListener(treeSelectListenre_mtree);
	// leftPanel.getTree().getModel().addTreeModelListener(treeModelListener_mtree);
	// }
}

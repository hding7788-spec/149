package com.glaway.mpm.parameter.ui;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import com.glaway.mpm.parameter.actions.CheckParameterPopupMenu;
import com.glaway.mpm.parameter.actions.CheckRecordTablePopupMenu;
import com.glaway.mpm.parameter.listener.QualityMouseListener;
import com.glaway.mpm.parameter.listener.QualityTreeSelectListener;
import com.glaway.mpm.parameter.model.data.CmBaiyuParamTable;
import com.glaway.mpm.parameter.model.data.CmBaiyuParamTableColumn;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.parameter.model.data.CmTreeNode;
import com.glaway.mpm.parameter.model.tree.XWBaiyuParamTableTreeObject;
import com.glaway.mpm.parameter.model.tree.XWParamTableTypeTreeObject;
import com.glaway.mpm.parameter.model.tree.XWParameterRootTreeObject;
import com.glaway.mpm.parameter.model.tree.XWTreeNode;
import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;

public class LeftTreePanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private JScrollPane scrollPane;
	private JTree qualityTree;
	private CheckParameterPopupMenu paramPopupMenu;
	private CheckRecordTablePopupMenu recordPopupMenu;

	public LeftTreePanel() {
		initComponent();
	}

	public JTree getQualityTree() {
		return qualityTree;
	}

	private void initComponent() {
		scrollPane = new JScrollPane();
		scrollPane.setBorder(null);

		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);

		CmTreeNode treeNode = new CmTreeNode();
		treeNode.setName("检验特性树");
		XWParameterRootTreeObject rootObject = new XWParameterRootTreeObject(treeNode);
		XWTreeNode rootNode = new XWTreeNode(rootObject);

		treeNode = new CmTreeNode();
		treeNode.setName("检验特性管理");
		XWParameterRootTreeObject paramTypeObject = new XWParameterRootTreeObject(treeNode);
		XWTreeNode paramTypeNode = new XWTreeNode(paramTypeObject);
		rootNode.add(paramTypeNode);

		treeNode = new CmTreeNode();
		treeNode.setName("检验记录表管理");
		XWParameterRootTreeObject paramTableTypeObject = new XWParameterRootTreeObject(treeNode);
		XWTreeNode paramTableTypeNode = new XWTreeNode(paramTableTypeObject);
		rootNode.add(paramTableTypeNode);

		CmParamTableType paramTableType = new CmParamTableType();
		paramTableType.setName("通用检查项定义");
		paramTableType.setEnName("CommonParamTable");
		paramTableType.setTechnicsType("通用检查项定义");
		XWParamTableTypeTreeObject commonParamTableTypeObject = new XWParamTableTypeTreeObject(paramTableType);
		XWTreeNode commonParamTableTypeNode = new XWTreeNode(commonParamTableTypeObject);
		paramTableTypeNode.add(commonParamTableTypeNode);

		treeNode = new CmTreeNode();
		treeNode.setName("特殊检查项表记录");
		XWParameterRootTreeObject speParamTableTypeObject = new XWParameterRootTreeObject(treeNode);
		XWTreeNode speParamTableTypeNode = new XWTreeNode(speParamTableTypeObject);
		paramTableTypeNode.add(speParamTableTypeNode);

		CmBaiyuParamTable baiyuTable = new CmBaiyuParamTable();
		baiyuTable.setName("白羽表单模板管理");
		try {
			List<CmBaiyuParamTableColumn> paramLists = ProcessParameterToWCIntf.getBaiyuParamLists("启用");
			baiyuTable.setTableColumns(paramLists);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		XWBaiyuParamTableTreeObject baiyuTableObject = new XWBaiyuParamTableTreeObject(baiyuTable);
		XWTreeNode baiyuTableNode = new XWTreeNode(baiyuTableObject);
		rootNode.add(baiyuTableNode);

		paramTableTypeNode.expandNode();
		rootNode.expandNode();
		qualityTree = new JTree(rootNode);
		qualityTree.setRootVisible(false);
		scrollPane.setViewportView(qualityTree);

		scrollPane.setHorizontalScrollBarPolicy(30);
		scrollPane.setVerticalScrollBarPolicy(20);
		scrollPane.getViewport().updateUI();

		paramPopupMenu = new CheckParameterPopupMenu(qualityTree);
		recordPopupMenu = new CheckRecordTablePopupMenu();
		qualityTree.addMouseListener(new QualityMouseListener(this));
		qualityTree.addTreeSelectionListener(new QualityTreeSelectListener());
		qualityTree.setCellRenderer(new QualityTreeCellRenderer());
	}

	public CheckParameterPopupMenu getParamPopupMenu() {
		return paramPopupMenu;
	}

	public CheckRecordTablePopupMenu getRecordPopupMenu() {
		return recordPopupMenu;
	}

}

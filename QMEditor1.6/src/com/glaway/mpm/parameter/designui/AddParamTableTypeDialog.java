package com.glaway.mpm.parameter.designui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.parameter.model.data.CmTechnicsType;
import com.glaway.mpm.parameter.model.data.CmTreeNode;
import com.glaway.mpm.parameter.model.tree.XWParameterRootTreeObject;
import com.glaway.mpm.parameter.model.tree.XWTechnicsTypeTreeObject;
import com.glaway.mpm.parameter.model.tree.XWTreeNode;
import com.glaway.mpm.parameter.model.tree.XWTreeObject;
import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.view.IconButton;
import com.glaway.mpm.view.NewTechnicsPart;

public class AddParamTableTypeDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -3630597765562361129L;
	private JTree paramTableTree;
	private JScrollPane scrollPane;
	private JButton sureButton;
	private JButton cancelButton;
	private NewSpecialParamTabbedPanel panel;
	private String bsoID;
	private String version;

	public AddParamTableTypeDialog(NewSpecialParamTabbedPanel panel) {
		this.panel = panel;
		this.bsoID = panel.getBsoID();
		this.version = panel.getVersion();
		initComponents();
		initUI();
	}

	private void initComponents() {
		sureButton = new IconButton("/images/button_save.png", "确 定");
		cancelButton = new IconButton("/images/cancel.png", "取 消");
		sureButton.addActionListener(this);
		cancelButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(sureButton);
		buttonPanel.add(cancelButton);

		JPanel treePanel = new JPanel();
		scrollPane = new JScrollPane();
		scrollPane.setBorder(null);

		treePanel.setLayout(new BorderLayout());
		treePanel.add(scrollPane, BorderLayout.CENTER);

		CmTreeNode root = new CmTreeNode();
		root.setName("检验记录表");
		XWParameterRootTreeObject rootTreeObject = new XWParameterRootTreeObject(root);
		XWTreeNode rootNode = new XWTreeNode(rootTreeObject);
		paramTableTree = new JTree(rootNode);
		paramTableTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		loadParamTableTypeTree();

		scrollPane.setViewportView(paramTableTree);
		scrollPane.setHorizontalScrollBarPolicy(30);
		scrollPane.setVerticalScrollBarPolicy(20);
		scrollPane.getViewport().updateUI();

		setLayout(new BorderLayout());
		add(buttonPanel, BorderLayout.NORTH);
		add(treePanel, BorderLayout.CENTER);
	}

	private void loadParamTableTypeTree() {
		XWTreeNode root = (XWTreeNode) paramTableTree.getModel().getRoot();
		root.removeAllChildren();
		DefaultTreeModel model = (DefaultTreeModel) paramTableTree.getModel();
		model.reload(root);

		paramTableTree.setShowsRootHandles(false);

		try {
			expandSimpleParamTableType(root, panel.getTechnicsType());
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		MPMParameterProcessor.expandAllNode(root, paramTableTree);

		TreePath path = new TreePath(root.getPath());
		paramTableTree.expandPath(path);
		paramTableTree.scrollPathToVisible(path);
		paramTableTree.setSelectionPath(path);
		paramTableTree.repaint();
	}

	/**
	 * 展开所有工艺类型的记录表
	 * @param root
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 */
	private void expandParamTableType(XWTreeNode root) throws RemoteException, InvocationTargetException {
		List<CmTechnicsType> paramTableTypes = ProcessParameterToWCIntf.loadParamTableTypeData();
		XWTechnicsTypeTreeObject technicsTypeTreeObject = null;
		for (CmTechnicsType technicsType : paramTableTypes) {
			technicsTypeTreeObject = new XWTechnicsTypeTreeObject(technicsType);
			XWTreeNode treeNode = new XWTreeNode(technicsTypeTreeObject);
			root.add(treeNode);

			expandSubNode(treeNode);
		}
	}
	/**
	 * 展开特定类型的记录表
	 * @param root
	 * @param technicsTypes
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 */
	private void expandSimpleParamTableType(XWTreeNode root, String technicsTypes) throws RemoteException, InvocationTargetException {
		CmTechnicsType paramTableTypes = ProcessParameterToWCIntf.loadSimpleParamTableTypeData(technicsTypes);
		XWTechnicsTypeTreeObject technicsTypeTreeObject = null;
		technicsTypeTreeObject = new XWTechnicsTypeTreeObject(paramTableTypes);
		XWTreeNode treeNode = new XWTreeNode(technicsTypeTreeObject);
		root.add(treeNode);

		expandSubNode(treeNode);
	}

	public static void expandSubNode(XWTreeNode node) {
		if (node != null) {
			XWTreeObject treeObject = node.getTreeObject();
			if (treeObject instanceof XWTechnicsTypeTreeObject) {
				node.expand();
			}
		}
	}

	private void initUI() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("新增检验记录表");
		setModal(true);
		setSize(400, 600);
		CommonUIUtil.setMiddleOnScreenWithDialog(this);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == sureButton) {
			XWTreeNode xwTreeNode = (XWTreeNode) paramTableTree.getLastSelectedPathComponent();
			if (xwTreeNode != null) {
				CmTreeNode cmTreeNode = xwTreeNode.getTreeObject().getTreeNode();
				if (cmTreeNode instanceof CmParamTableType) {
					CmParamTableType paramTableType = (CmParamTableType) cmTreeNode;
					List<String> tables = panel.getTables();
					if (tables.contains(paramTableType.getEnName())) {
						JOptionPane.showMessageDialog(null, "已存在该检验记录表");
						return ;
					}
					panel.addParamTableType(paramTableType);

					/** 建立数据库表连接 */
					String technicsNumber = panel.getTechnicsNumber();
					String objNumber = panel.getObjNumber();
					String objType = panel.getObjType();
					String tableIndex = String.valueOf(tables.size());

					MPMParameterProcessor.createObjToParamTableLink(technicsNumber, objType, objNumber, String.valueOf(paramTableType.getOid()), tableIndex, bsoID, version);

					this.dispose();
					return;
				}
			}
			JOptionPane.showMessageDialog(null, "请选择检验记录表！");

		} else if (obj == cancelButton) {
			this.dispose();
		}
	}

}

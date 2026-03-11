package com.glaway.mpm.consCheck;

import com.glaway.mpm.model.ConsCheckRecord;
import com.glaway.mpm.model.ConsCheckTree;
import com.glaway.mpm.parameter.designui.CreateCheckParamTableTypeDialog;
import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;
import com.glaway.mpm.qmIntf.template.TpNode;
import com.glaway.mpm.qmIntf.template.TpTree;
import com.glaway.mpm.qmIntf.template.TpTreeNode;
import com.glaway.mpm.util.SwingUtil;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;

public class ConsCheckApplyMainPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JPanel mainPanel;
	private JScrollPane jScrollPane;
	public static TpTree tpTree;
	private CreateCheckParamTableTypeDialog dialog;
	private JDialog parentDialog;
	private String type;

	public ConsCheckApplyMainPanel(CreateCheckParamTableTypeDialog dialog,JDialog parentDialog, String type) {
		this.dialog = dialog;
		this.parentDialog = parentDialog;
		this.type = type;
		init();
	}

	private void init() {
		initComponents();
		initLayout();
	}

	private void initComponents() {
		mainPanel = new JPanel();
		jScrollPane = new JScrollPane(mainPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		tpTree = ToTpTree();
		tpTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getSource() == tpTree && e.getClickCount() == 2) {
					TreePath selPath = tpTree.getPathForLocation(e.getX(), e.getY());
					if (selPath != null){
						Object path = selPath.getLastPathComponent();
						if(path instanceof TpNode){
							TpNode tpNode = (TpNode) path;
							TreeNode[] nodePath = tpNode.getPath();
							String name = "";
							for(int i = 2; i < nodePath.length; i++) {
								TreeNode treeNode = nodePath[i];
								if(treeNode instanceof TpTreeNode) {
									if("".equals(name)){
										name += ((TpTreeNode)treeNode).getName();
									}else{
										name += "_" + ((TpTreeNode)treeNode).getName();
									}
								} else if(treeNode instanceof TpNode) {
									if("".equals(name)){
										name += ((TpNode) treeNode).getTemplate().getName();
									}else{
										name += "_" + ((TpNode) treeNode).getTemplate().getName();
									}
								}
							}
							if("TABLE".equals(type)){
								dialog.getTableComboBox().setSelectedItem(name);
							}else{
								dialog.getProComboBox().setSelectedItem(name);
							}
							ConsCheckApplyMainPanel.this.parentDialog.dispose();
						}
					}
				}
			}
		});
		tpTree.updateUI();
		SwingUtil.expandAll(tpTree);
		jScrollPane.setViewportView(tpTree);
	}

	private void initLayout() {
		jScrollPane.getViewport().setBackground(Color.WHITE);
		jScrollPane.setPreferredSize(new Dimension(490, 630));
		mainPanel.setLayout(new BorderLayout(0, 1));
		mainPanel.add(jScrollPane);
		add(mainPanel);
	}

	public TpTree ToTpTree() {
		TpTree tpTree = null;
		try {
			ConsCheckTree checkTree = ProcessParameterToWCIntf.packageConsCheckDetailTree(type);
			TpTreeNode rootNode = new TpTreeNode("tpTree");
			TpTreeNode node = new TpTreeNode(checkTree.getName(),checkTree.getGekeyid());
			convertToTpTree(checkTree,node);
			rootNode.add(node);
			tpTree = new TpTree(rootNode);
		} catch(InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch(RemoteException e) {
			throw new RuntimeException(e);
		}
		return tpTree;
	}

	public void convertToTpTree(ConsCheckTree checkTree,TpTreeNode tpTreeNode){
		List<ConsCheckRecord> records = checkTree.getRecords();
		if(records != null && records.size()>0){
			for(ConsCheckRecord record : records) {
				tpTreeNode.add(new TpNode(record.getGwkeyid(), null,record.getName()));
			}
		}
		List<ConsCheckTree> trees = checkTree.getTrees();
		if(trees != null && trees.size()>0){
			for(ConsCheckTree tree : trees) {
				TpTreeNode treeNode = new TpTreeNode(tree.getName(),tree.getGekeyid());
				convertToTpTree(tree,treeNode);
				tpTreeNode.add(treeNode);
			}
		}
	}
}

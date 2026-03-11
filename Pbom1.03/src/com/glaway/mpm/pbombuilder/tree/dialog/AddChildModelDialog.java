package com.glaway.mpm.pbombuilder.tree.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmUtil;

/**
 * 
 * Created on 2012-10-23
 * 
 * @author chenyunlong
 */
public class AddChildModelDialog {
	private CmTree tree;
	private JDialog dialog;
	private static CmTreeNode node;
	private static CmTreeNode root;

	private JButton sureButton;
	private JButton cancelButton;
	private JPanel leftPanel;
	private JPanel rightPanel;
	private JPanel rightUpPanel;
	private JPanel mainPanel;
	private JScrollPane jScrollPane;

	public AddChildModelDialog(CmTreeNode cmNode, CmTree cmTree) {
		super();
		node = cmNode;
		tree = cmTree;
		root = cmTree.getRoot();
	}

	public void showDialog() {
		// 新增对话框
		newJDialog();
		initComponents();
		initLayout();
		initActions();
		loadInitDatas();
		tree.updateUI();
	}

	/**
	 * 新增对话框jdialog
	 */
	public void newJDialog() {
		dialog = new JDialog();
		dialog.setTitle("添加工艺子件");
		dialog.setSize(400, 450);
		dialog.setLocation(800, 300);
		dialog.setIconImage(CmUtil.getImageFromServer("addDoc.jpg"));
		dialog.setResizable(true);
	}

	private void initComponents() {
		mainPanel = new JPanel();
		leftPanel = new JPanel();
		jScrollPane = new JScrollPane();
		jScrollPane.setViewportView(new CmTree(filterTheNode()));
		rightPanel = new JPanel();
		rightUpPanel = new JPanel();
		sureButton = new JButton();
		cancelButton = new JButton();
	}

	private void initLayout() {
		jScrollPane.getViewport().setBackground(Color.WHITE);
		jScrollPane.setPreferredSize(new Dimension(300, 420));
		leftPanel.add(jScrollPane);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(20, 0, 10, 0);
		c.gridx = 1;
		c.gridy = 0;
		rightUpPanel.setLayout(new GridBagLayout());
		rightUpPanel.add(sureButton, c);
		c.insets = new Insets(10, 0, 10, 0);
		c.gridy = 1;
		rightUpPanel.add(cancelButton, c);

		rightPanel.add(rightUpPanel);
		mainPanel.setLayout(new BorderLayout(1, 2));
		mainPanel.add(leftPanel, BorderLayout.WEST);
		mainPanel.add(rightPanel, BorderLayout.CENTER);
		dialog.add(mainPanel);
		Container contentPane = dialog.getContentPane();
		contentPane.add(mainPanel);
		dialog.setVisible(true);
	}

	private void initActions() {
		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
	}

	private void loadInitDatas() {
		sureButton.setText("确定");
		cancelButton.setText("取消");
	}

	/**
	 * 过来掉选择的节点
	 * 
	 * @param node
	 * @return
	 */
	public CmTreeNode filterTheNode() {
		CmTreeNode parent = (CmTreeNode) node.getParent();
		CmTreeNode pbom = root;
		if (pbom.equals(node)) {
			pbom = null;
		} else {
			filterTheTreeNode(pbom, node);
		}
		parent.add(node);
		return pbom;
	}

	@SuppressWarnings("unchecked")
	public void filterTheTreeNode(CmTreeNode pbom, CmTreeNode filterNode) {
		Enumeration children = pbom.children();
		while (children.hasMoreElements()) {
			CmTreeNode cmTreeNode = (CmTreeNode) children.nextElement();
			if (cmTreeNode.equals(filterNode)) {
				cmTreeNode.removeFromParent();
				break;
			} else {
				filterTheTreeNode(cmTreeNode, filterNode);
			}
		}
	}
}

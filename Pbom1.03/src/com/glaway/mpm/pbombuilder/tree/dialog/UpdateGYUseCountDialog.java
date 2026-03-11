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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreeNode;

import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmScrollPaneTree;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeEditReportAction;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;
import com.glaway.mpm.pbombuilder.util.ExtCommonDellFunction;
import com.glaway.mpm.pbombuilder.util.InputLimited;
import com.glaway.mpm.pbombuilder.util.LoadConfig;

/**
 *
 * 修改工艺使用数量
 *
 * Created on 2014-10-11
 *
 * @author longxiuchuan
 */
public class UpdateGYUseCountDialog {
	private CmTree tree;
	private CmTreeNode node;
	private CmLightPart part;
	private JDialog dialog;

	private JPanel mainPanel;
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JLabel lable0;
	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JTextArea lable4;
	private JLabel label5;

	private JTextField partNumber;
	private JTextField partName;
	private JTextField account;
	private InputLimited limit;
	private JTextField oldUseCount;

	private JButton sureButton;
	private JButton cancelButton;
	private String[] mtypeValues = LoadConfig.getInstance().getPartType();

	public UpdateGYUseCountDialog(CmTreeNode cmNode, CmTree cmTree) {
		super();
		this.node = cmNode;
		this.tree = cmTree;
	}


	public void showDialog() {
		// 新增对话框
		newJDialog();
		initComponents();
		loadInitDatas();
		initActions();
		initLayout();
	}

	/**
	 * 新增对话框jdialog
	 */
	public void newJDialog() {
		dialog = new JDialog();
		dialog.setTitle("修改工艺使用数量");
		dialog.setSize(460, 360);
		dialog.setIconImage(CmUtil.getImageFromServer("middle.gif"));
		dialog.setResizable(false);
		CmCommonStringUtil.setMiddleOnScreenWithDialog(dialog);
	}

	private void initComponents() {
		mainPanel = new JPanel();
		topPanel = new JPanel();
		bottomPanel = new JPanel();

		lable0 = new JLabel("上级零组件节点:");
		label1 = new JLabel("         编号:");
		label2 = new JLabel("         名称:");
		label3 = new JLabel("     *修改数量:");
		label5 = new JLabel("      原有数量:");
		lable4 = new JTextArea(this.node.getParent().toString());
		lable4.setLineWrap(true);
		lable4.setRows(3);
		lable4.setEditable(false);
		lable4.setFont(new java.awt.Font("宋体", 1, 12)); // NOI18N
		lable4.setForeground(new java.awt.Color(0, 0, 255));

		partNumber = new JTextField();
		partName = new JTextField();
		account = new JTextField();
		oldUseCount = new JTextField();
		limit = new InputLimited(1001, true);
		account.setDocument(limit);
		account.setText("1");
		sureButton = new JButton();
		cancelButton = new JButton();
		account.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if(!CmCommonStringUtil.isEmpty(account.getText()) && account.getText().startsWith("0")){
					account.setText("1");
				}
				else if(!CmCommonStringUtil.isEmpty(account.getText()) && Integer.parseInt(account.getText())>1000){
					account.setText(account.getText().substring(0, 4));
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {

			}
		});
	}

	private void initLayout() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		topPanel.setLayout(new GridBagLayout());

		lable0.setPreferredSize(new Dimension(110, 25));
		label1.setPreferredSize(new Dimension(110, 25));
		label2.setPreferredSize(new Dimension(110, 25));
		label3.setPreferredSize(new Dimension(110, 25));
		lable4.setPreferredSize(new Dimension(243, 25));

		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 1;
		c.gridx = 1;
		topPanel.add(lable0, c);

		c.insets = new Insets(10, 105, 5, 25);
		account.setPreferredSize(new Dimension(150, 25));
		topPanel.add(lable4, c);

		//编号
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 2;
		topPanel.add(label1, c);

		partNumber.setText(this.node.getPart().getPartNumber());
		partNumber.setEditable(false);
		c.insets = new Insets(10, 105, 5, 0);
		partNumber.setPreferredSize(new Dimension(150, 25));
		topPanel.add(partNumber, c);

		//名称
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 3;
		topPanel.add(label2, c);

		partName.setText(this.node.getPart().getPartName());
		partName.setEditable(false);
		c.insets = new Insets(10, 105, 5, 15);
		partName.setPreferredSize(new Dimension(150, 25));
		topPanel.add(partName, c);

		//原有使用数量
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 4;
		topPanel.add(label5, c);

		String gysl = node.getPart().getGysl();
		if(gysl == null || "".equals(gysl)) {
			if(node.getListNode() != null) {
				gysl = String.valueOf(node.getListNode().size());
			} else {
				gysl = "1";
			}
		}

		oldUseCount.setText(gysl);
		oldUseCount.setEditable(false);
		c.insets = new Insets(10, 105, 5, 15);
		oldUseCount.setPreferredSize(new Dimension(150, 25));
		topPanel.add(oldUseCount, c);

		//数量
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 5;
		topPanel.add(label3, c);

		c.insets = new Insets(10, 105, 5, 25);
		account.setPreferredSize(new Dimension(150, 25));
		topPanel.add(account, c);
		c.insets = new Insets(10, 260, 5, 0);
		JLabel message = new JLabel("大于0的正整数");
		message.setForeground(Color.GRAY);
		topPanel.add(message, c);

		bottomPanel.setLayout(new GridBagLayout());
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 5;
		c.gridx = 1;
		bottomPanel.add(sureButton, c);
		c.insets = new Insets(10, 85, 15, 15);
		bottomPanel.add(cancelButton, c);

		mainPanel.setLayout(new BorderLayout(1, 3));
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		dialog.add(mainPanel);
		Container contentPane = dialog.getContentPane();
		contentPane.add(mainPanel);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				dialog.setModal(true);
				dialog.setVisible(true);
			}
		});
	}

	private void initActions() {
		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(CmCommonStringUtil.isEmpty(account.getText())){
					JOptionPane.showMessageDialog(tree.getRootPane(), "工艺使用数量不能为空！");
				} else if(node.getPart().getGysl()!=null &&node.getPart().getGysl().equals(account.getText())) {
					JOptionPane.showMessageDialog(tree.getRootPane(), "请输入一个跟原有数量值不同的正整数！");
				} else {
					CmTreeNode root = tree.getRoot();
					CmTreeNode pNode = (CmTreeNode)node.getParent();
					List<CmTreeNode> list = new ArrayList<CmTreeNode>();
					ExtCommonDellFunction.getAllCommonCmTreeNode(root, pNode, list);
					for (CmTreeNode cmTreeNode : list) {
						Enumeration<CmTreeNode> en = cmTreeNode.children();
						while(en.hasMoreElements()){
							CmTreeNode cNode = en.nextElement();
							if(CmCommonStringUtil.isCommon(cNode, node)){
								cNode.getPart().setGysl(account.getText());

								TreeNode parent = cNode.getParent();
								if(parent!=null && parent instanceof CmTreeNode){
									CmTreeNode p = (CmTreeNode)parent;
									String parentPartNumber = p.getPart().getPartNumber();
									String key = parentPartNumber+"->"+cNode.getPart().getPartNumber();
									CmScrollPaneTree.changeGysl.put(key, account.getText());
								}
								cNode.getPart().setEdit(true);

								if(CmCommonStringUtil.isPackage(cNode)) {
									List<CmTreeNode> blist = cNode.getListNode();
									for (CmTreeNode bnode : blist) {
										bnode.getPart().setGysl(account.getText());
										bnode.getPart().setEdit(true);
									}
								}
							}
						}
					}

					CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
					tree.updateUI();
					PbomTreeEditReportAction.updatePbomTreeEditReport();
					BomTreeReportAction.updateBomReport();
					dialog.setVisible(false);
				}
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
}

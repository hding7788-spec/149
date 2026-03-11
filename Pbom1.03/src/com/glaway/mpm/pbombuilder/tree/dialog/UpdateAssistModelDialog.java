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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;
import com.glaway.mpm.pbombuilder.util.InputLimited;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

public class UpdateAssistModelDialog {
	private CmTree tree;
	private CmTreeNode node;
	private JDialog dialog;

	private JPanel mainPanel;
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JLabel label1;
	private JLabel label2;
	private JLabel label3;

	private JLabel partNumber;
	private JLabel partName;
	private InputLimited limit;
	
	private ButtonGroup group;
	private JRadioButton pici;
	private JTextField piciText;
	private JRadioButton bilv;
	private JTextField fenziText;
	private JTextField fenmuText;

	private JButton sureButton;
	private JButton cancelButton;

	public UpdateAssistModelDialog(CmTreeNode cmNode, CmTree cmTree) {
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
		dialog.setTitle("修改工艺辅件数量");
		dialog.setSize(450, 270);
		dialog.setIconImage(CmUtil.getImageFromServer("assist.gif"));
		dialog.setResizable(false);
		CmCommonStringUtil.setMiddleOnScreenWithDialog(dialog); 
	}

	private void initComponents() {
		mainPanel = new JPanel();
		topPanel = new JPanel();
		bottomPanel = new JPanel();

		label1 = new JLabel("         *编号:");
		label2 = new JLabel("         *名称:");
		label3 = new JLabel("/");
		limit = new InputLimited(5, true);
		piciText = new JTextField();
		piciText.setDocument(limit);
		fenziText = new JTextField();
		limit = new InputLimited(5, true);
		fenziText.setDocument(limit);
		fenmuText = new JTextField();
		limit = new InputLimited(5, true);
		fenmuText.setDocument(limit);
		group = new ButtonGroup();
		pici = new JRadioButton("投产数量");
		bilv = new JRadioButton("投产比例");
		group.add(pici);
		group.add(bilv);
		if(this.node.getPart().getProductionQuantity()!=0){
			pici.setSelected(true);
			piciText.setText(String.valueOf(node.getPart().getProductionQuantity()));
			fenziText.setEnabled(false);
			fenmuText.setEnabled(false);
		}
		else{
			bilv.setSelected(true);
			String[] ratio = node.getPart().getProductionRatio().split("/");
			fenziText.setText(ratio[0]);
			fenmuText.setText(ratio[1]);
			piciText.setEnabled(false);
		}
		partNumber = new JLabel(this.node.getPart().getPartNumber());
		partName = new JLabel(this.node.getPart().getPartName());
		sureButton = new JButton();
		cancelButton = new JButton();
		piciText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				if(!CmCommonStringUtil.isEmpty(piciText.getText()) && piciText.getText().startsWith("0")){
					piciText.setText("1");
				}
				else if(!CmCommonStringUtil.isEmpty(piciText.getText()) && Integer.parseInt(piciText.getText())>1000){
					piciText.setText(piciText.getText().substring(0, 3));
				}
			}
			@Override
			public void keyTyped(KeyEvent e) {}
		});
		fenziText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {
				if(!CmCommonStringUtil.isEmpty(fenziText.getText()) && fenziText.getText().startsWith("0")){
					fenziText.setText("1");
				}
				else if(!CmCommonStringUtil.isEmpty(fenziText.getText()) && Integer.parseInt(fenziText.getText())>1000){
					fenziText.setText(fenziText.getText().substring(0, 3));
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {}
		});
		fenmuText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				if(!CmCommonStringUtil.isEmpty(fenmuText.getText()) && fenmuText.getText().startsWith("0")){
					fenmuText.setText("1");
				}
				else if(!CmCommonStringUtil.isEmpty(fenmuText.getText()) && Integer.parseInt(fenmuText.getText())>1000){
					fenmuText.setText(fenmuText.getText().substring(0, 3));
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {}
		});
	}

	private void initLayout() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		topPanel.setLayout(new GridBagLayout());

		label1.setPreferredSize(new Dimension(110, 25));
		label2.setPreferredSize(new Dimension(110, 25));
		label3.setPreferredSize(new Dimension(10, 25));

		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 1;
		c.gridx = 1;
		topPanel.add(label1, c);
		c.insets = new Insets(10, 135, 5, 0);
		partNumber.setPreferredSize(new Dimension(150, 25));
		topPanel.add(partNumber, c);
		
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 2;
		topPanel.add(label2, c);
		
		c.insets = new Insets(10, 135, 5, 15);
		partName.setPreferredSize(new Dimension(150, 25));
		topPanel.add(partName, c);
		
		c.insets = new Insets(10, 25, 5, 15);
		c.gridy = 3;
		topPanel.add(pici, c);

		c.insets = new Insets(10, 135, 5, 25);
		piciText.setPreferredSize(new Dimension(150, 25));
		topPanel.add(piciText, c);
		c.insets = new Insets(10, 290, 5, 0);
		JLabel message = new JLabel("不大于1000的正整数");
		message.setForeground(Color.GRAY);
		topPanel.add(message, c);
		
		c.insets = new Insets(0, 25, 5, 15);
		c.gridy = 4;
		topPanel.add(bilv, c);

		c.insets = new Insets(0, 135, 5, 25);
		fenziText.setPreferredSize(new Dimension(65, 25));
		topPanel.add(fenziText, c);
		
		c.insets = new Insets(0, 210, 5, 5);
		topPanel.add(label3, c);
		
		c.insets = new Insets(0, 220, 5, 25);
		fenmuText.setPreferredSize(new Dimension(65, 25));
		topPanel.add(fenmuText, c);
		c.insets = new Insets(10, 290, 5, 0);
		JLabel mes = new JLabel("不大于1000的正分数");
		mes.setForeground(Color.GRAY);
		topPanel.add(mes, c);

		bottomPanel.setLayout(new GridBagLayout());
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 5;
		c.gridx = 1;
		bottomPanel.add(sureButton, c);
		c.insets = new Insets(10, 135, 15, 15);
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
				CmLightPart part = node.getPart();
				if(pici.isSelected() && CmCommonStringUtil.isEmpty(piciText.getText())){
					JOptionPane.showMessageDialog(tree.getRootPane(), "批次数量不能为空！");
				}else if(bilv.isSelected()&& (CmCommonStringUtil.isEmpty(fenziText.getText()) || CmCommonStringUtil.isEmpty(fenmuText.getText()))){
					JOptionPane.showMessageDialog(tree.getRootPane(), "比例信息填写不完整！");
				}
				else{
					Map<String,String> map = new HashMap<String,String>();
					if(pici.isSelected() && part.getProductionQuantity()!=Integer.parseInt(piciText.getText())){
						map.put("productionQuantity", piciText.getText());
						map.put("productionRatio", "");
						boolean flag = PBOMEditorToWCIntf.modifyAssistantPartQuantity(String.valueOf(part.getOid()), map);
						if(flag){
							part.setProductionQuantity(Integer.parseInt(piciText.getText()));
							part.setProductionRatio("");
							CmCommonStringUtil.checkAssistCountIsEdit(node);
							updateCommonAssistantNode(node,tree.getRoot());
							BomTreeReportAction.updateBomReport();
							tree.updateUI();
						}else{
							JOptionPane.showMessageDialog(tree.getRootPane(), "更新辅件数量失败！");
						}
					}
					else if(bilv.isSelected() && !CmCommonStringUtil.isEqual(part.getProductionRatio(), fenziText.getText()+"/"+fenmuText.getText())){
						map.put("productionQuantity", "0");
						map.put("productionRatio", fenziText.getText()+"/"+fenmuText.getText());
						boolean flag = PBOMEditorToWCIntf.modifyAssistantPartQuantity(String.valueOf(part.getOid()), map);
						if(flag){
							part.setProductionQuantity(0);
							part.setProductionRatio(fenziText.getText()+"/"+fenmuText.getText());
							CmCommonStringUtil.checkAssistCountIsEdit(node);
							updateCommonAssistantNode(node,tree.getRoot());
							BomTreeReportAction.updateBomReport();
							tree.updateUI();
						}
						else{
							JOptionPane.showMessageDialog(tree.getRootPane(), "更新辅件数量失败！");
						}
					}
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
		//选择批次
		pici.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(pici.isSelected()){
					piciText.setEnabled(true);
					fenziText.setEnabled(false);
					fenmuText.setEnabled(false);
				}
			}
		});
		//选择比例
		bilv.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				piciText.setEnabled(false);
				fenziText.setEnabled(true);
				fenmuText.setEnabled(true);
			}
		});
	}

	private void loadInitDatas() {
		sureButton.setText("确定");
		cancelButton.setText("取消");
	}
	
	public void updateCommonAssistantNode(CmTreeNode assist,CmTreeNode node){
		Enumeration children = node.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if("assistant".equals(child.getPart().getPartType()) && CmCommonStringUtil.isCommon(assist, child)){
				child.getPart().setProductionQuantity(assist.getPart().getProductionQuantity());
				child.getPart().setProductionRatio(assist.getPart().getProductionRatio());
				child.getPart().setEditOfAssistCount(assist.getPart().isEditOfAssistCount());
			}
			if(CmCommonStringUtil.isPackage(child)){
				for(CmTreeNode brother:child.getListNode()){
					updateCommonAssistantNode(assist,brother);
				}
			}
			updateCommonAssistantNode(assist,child);
		}
	}
}

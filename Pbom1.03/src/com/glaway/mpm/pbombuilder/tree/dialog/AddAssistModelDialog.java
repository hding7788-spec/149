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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreeCancelAction;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeEditReportAction;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;
import com.glaway.mpm.pbombuilder.util.InputLimited;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

/**
 * 
 * Created on 2012-10-23
 * 
 * @author chenyunlong
 */
public class AddAssistModelDialog {
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

	private JTextField partNumber;
	private JTextField partName;

	private ButtonGroup group;
	private JRadioButton pici;
	private JTextField piciText;
	private JRadioButton bilv;
	private JTextField fenziText;
	private JTextField fenmuText;
	private InputLimited limit;
	private JButton searchButton;
	private JButton sureButton;
	private JButton cancelButton;

	public AddAssistModelDialog(CmTreeNode cmNode, CmTree cmTree) {
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
		dialog.setTitle("添加工艺辅件");
		dialog.setSize(460, 360);
		dialog.setIconImage(CmUtil.getImageFromServer("assist.gif"));
		dialog.setResizable(false);
		CmCommonStringUtil.setMiddleOnScreenWithDialog(dialog);
	}

	private void initComponents() {
		mainPanel = new JPanel();
		topPanel = new JPanel();
		bottomPanel = new JPanel();

		lable0 = new JLabel("上级零组件节点:");
		label1 = new JLabel("         *编号:");
		label2 = new JLabel("         *名称:");
		label3 = new JLabel("/");
		lable4 = new JTextArea(this.node.toString());
		lable4.setLineWrap(true);
		lable4.setRows(3);
		lable4.setEditable(false);
		lable4.setFont(new java.awt.Font("宋体", 1, 12)); // NOI18N
		lable4.setForeground(new java.awt.Color(0, 0, 255));
		partNumber = new JTextField();
		partName = new JTextField();
		limit = new InputLimited(4, true);
		piciText = new JTextField();
		piciText.setDocument(limit);
		fenziText = new JTextField();
		fenziText.setEnabled(false);
		limit = new InputLimited(4, true);
		fenziText.setDocument(limit);
		fenmuText = new JTextField();
		fenmuText.setEnabled(false);
		limit = new InputLimited(4, true);
		fenmuText.setDocument(limit);
		group = new ButtonGroup();
		pici = new JRadioButton("投产数量");
		pici.setSelected(true);
		bilv = new JRadioButton("投产比例");
		group.add(pici);
		group.add(bilv);
		searchButton = new JButton("搜索");
		sureButton = new JButton();
		cancelButton = new JButton();
		piciText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!CmCommonStringUtil.isEmpty(piciText.getText()) && piciText.getText().startsWith("0")) {
					piciText.setText("1");
				} else if (!CmCommonStringUtil.isEmpty(piciText.getText())
						&& Integer.parseInt(piciText.getText()) > 1000) {
					piciText.setText(piciText.getText().substring(0, 3));
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {

			}
		});
		fenziText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!CmCommonStringUtil.isEmpty(fenziText.getText()) && fenziText.getText().startsWith("0")) {
					fenziText.setText("1");
				} else if (!CmCommonStringUtil.isEmpty(fenziText.getText())
						&& Integer.parseInt(fenziText.getText()) > 1000) {
					fenziText.setText(fenziText.getText().substring(0, 3));
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {

			}
		});
		fenmuText.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!CmCommonStringUtil.isEmpty(fenmuText.getText()) && fenmuText.getText().startsWith("0")) {
					fenmuText.setText("1");
				} else if (!CmCommonStringUtil.isEmpty(fenmuText.getText())
						&& Integer.parseInt(fenmuText.getText()) > 1000) {
					fenmuText.setText(fenmuText.getText().substring(0, 3));
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
		label3.setPreferredSize(new Dimension(10, 25));
		lable4.setPreferredSize(new Dimension(243, 25));

		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 1;
		c.gridx = 1;
		topPanel.add(lable0, c);

		c.insets = new Insets(10, 135, 5, 25);
		topPanel.add(lable4, c);

		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 2;
		topPanel.add(label1, c);

		c.insets = new Insets(10, 135, 5, 0);
		partNumber.setPreferredSize(new Dimension(150, 25));
		topPanel.add(partNumber, c);

		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 3;
		topPanel.add(label2, c);

		c.insets = new Insets(10, 135, 5, 15);
		partName.setPreferredSize(new Dimension(150, 25));
		topPanel.add(partName, c);

		c.insets = new Insets(10, 25, 5, 15);
		c.gridy = 4;
		topPanel.add(pici, c);

		c.insets = new Insets(10, 135, 5, 25);
		piciText.setPreferredSize(new Dimension(150, 25));
		topPanel.add(piciText, c);
		c.insets = new Insets(10, 290, 5, 0);
		JLabel message = new JLabel("不大于1000的正整数");
		message.setForeground(Color.GRAY);
		topPanel.add(message, c);

		c.insets = new Insets(0, 25, 5, 15);
		c.gridy = 5;
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

		c.insets = new Insets(10, 50, 5, 15);
		c.gridy = 6;
		topPanel.add(searchButton, c);

		bottomPanel.setLayout(new GridBagLayout());
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 7;
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
				if (null == partNumber.getText() || "".equals(partNumber.getText().trim())) {
					JOptionPane.showMessageDialog(tree.getRootPane(), "工艺辅件编号不能为空！");
				} else if (CmCommonStringUtil.checkHasChinaese(partNumber.getText())) {
					JOptionPane.showMessageDialog(tree.getRootPane(), "工艺辅件编号中不能有中文！");
				} else if (null == partName.getText() || "".equals(partName.getText().trim())) {
					JOptionPane.showMessageDialog(tree.getRootPane(), "工艺辅件名称不能为空！");
				} else if (pici.isSelected() && CmCommonStringUtil.isEmpty(piciText.getText())) {
					JOptionPane.showMessageDialog(tree.getRootPane(), "批次数量不能为空！");
				} else if (bilv.isSelected()
						&& (CmCommonStringUtil.isEmpty(fenziText.getText()) || CmCommonStringUtil.isEmpty(fenmuText
								.getText()))) {
					JOptionPane.showMessageDialog(tree.getRootPane(), "比例信息填写不完整！");
				} else {
					sureButton.setVisible(false);
					Map<String, String> ibamap = new HashMap<String, String>();
					int productionQuantity = 0;
					String productionRatio = "";
					if (pici.isSelected()) {
						productionQuantity = Integer.parseInt(piciText.getText());
					} else {
						productionRatio = fenziText.getText() + "/" + fenmuText.getText();
					}
					ibamap.put("productionQuantity", productionQuantity + "");
					ibamap.put("productionRatio", productionRatio);
					Map<String,String> map = new HashMap<String,String>();
					map.put("number", partNumber.getText());
					map.put("name", partName.getText());
					map.put("parentPartOid", String.valueOf(node.getPart().getOid()));
					map.put("typeName","assistant");
					List<Object> list = PBOMEditorToWCIntf.createMiddleORAssistantPart(map, ibamap);
					if (CmCommonStringUtil.isEmpty((String) list.get(0))) {
						WTPart wtPart = (WTPart) list.get(1);
						part = new CmLightPart();
						part.setPartName(partName.getText().toLowerCase());
						part.setPartNumber(partNumber.getText().toUpperCase());
						part.setPartType("assistant");
						part.setProductionQuantity(productionQuantity);
						part.setProductionRatio(productionRatio);
						part.setOperType("new");
						part.setOid(wtPart.getPersistInfo().getObjectIdentifier().getId());
						part.setContainerId(node.getPart().getContainerId());
						try {
							part.setLifecycle(wtPart.getLifeCycleState().getDisplay(SessionHelper.getLocale()));
							part.setVersion(wtPart.getVersionIdentifier().getValue() + "."
									+ wtPart.getIterationIdentifier().getValue());
						} catch (WTException e1) {
							e1.printStackTrace();
						}
						String item = part.getPartNumber() + "(" + part.getPartName() + ") ";
						CmTreeNode cmTreeNode = new CmTreeNode(item);
						cmTreeNode.setPart(part);
						CmCommonStringUtil.addCommonAssistNodeWithCommonParent(tree.getRoot(), cmTreeNode, node);
						EbomTreeCancelAction.addPbomTreeChange(cmTreeNode, null, "create", null);
						CmCommonStringUtil.addNodeToCheckList(cmTreeNode);
						CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
						tree.updateUI();
						PbomTreeEditReportAction.updatePbomTreeEditReport();
						BomTreeReportAction.updateBomReport();
						dialog.setVisible(false);
					} else {
						JOptionPane.showMessageDialog(tree.getRootPane(), list.get(0));
						sureButton.setVisible(true);
					}
				}
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
		// 选择批次
		pici.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (pici.isSelected()) {
					piciText.setEnabled(true);
					fenziText.setEnabled(false);
					fenmuText.setEnabled(false);
				}
			}
		});
		// 选择比例
		bilv.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				piciText.setEnabled(false);
				fenziText.setEnabled(true);
				fenmuText.setEnabled(true);
			}
		});
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AddSearchAssistDialog search = new AddSearchAssistDialog(node, dialog, tree);
				search.showDialog();
			}
		});
	}

	private void loadInitDatas() {
		sureButton.setText("确定");
		cancelButton.setText("取消");
	}
}

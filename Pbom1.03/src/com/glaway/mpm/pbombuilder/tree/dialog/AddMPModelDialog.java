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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.glaway.mpm.pbombuilder.action.CmCommonPackageAction;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreeCancelAction;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeEditReportAction;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;
import com.glaway.mpm.pbombuilder.util.Constants;
import com.glaway.mpm.pbombuilder.util.ExtCommonDellFunction;
import com.glaway.mpm.pbombuilder.util.InputLimited;
import com.glaway.mpm.pbombuilder.util.LoadConfig;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

/**
 * 创建毛坯件
 *
 * Created on 2013-12-24
 *
 * @author longxiuchuan
 */
public class AddMPModelDialog {
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
	private JLabel label5;
	private JTextArea lable4;
	private JLabel pindexLabel;//产品代号
	private JLabel keyComponentLabel;//关重件标记
	private JLabel mindexLabel;//所属型号
	private JLabel phasecodeLabel;//阶段标记
	private JLabel cindexLabel;//图号

	private JTextField partNumber;
	private JTextField partName;
	private JTextField account;
	private InputLimited limit;
	private JComboBox mptype;
	private JTextField pindex;
	private JComboBox keyComponent;
	private JTextField mindex;
	private JComboBox phasecode;
	private JTextField cindex;

	private String[] mpValues = LoadConfig.getInstance().getPartType();
	private String[] keyComponentValues = {"N","G","Z"};

	private JButton sureButton;
	private JButton cancelButton;

	public AddMPModelDialog(CmTreeNode cmNode, CmTree cmTree) {
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
		dialog.setTitle("创建毛坯件");
		dialog.setSize(460, 500);
		dialog.setIconImage(CmUtil.getImageFromServer("middle.gif"));
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
		label3 = new JLabel("     *装配数量:");
		label5 = new JLabel("    毛坯件类型:");
		pindexLabel = new JLabel("      产品代号:");
		keyComponentLabel = new JLabel("   *关重件标记:");
		mindexLabel = new JLabel("      所属型号:");
		phasecodeLabel = new JLabel("      阶段标记:");
		cindexLabel = new JLabel("          图号:");

		lable4 = new JTextArea(this.node.toString());
		lable4.setLineWrap(true);
		lable4.setRows(3);
		lable4.setEditable(false);
		lable4.setFont(new java.awt.Font("宋体", 1, 12)); // NOI18N
		lable4.setForeground(new java.awt.Color(0, 0, 255));

		partNumber = new JTextField();
		if(node.getPart().getPartNumber() == null || "null".equals(node.getPart().getPartNumber())) {
			partNumber.setText("MP");
		} else {
			partNumber.setText(node.getPart().getPartNumber()+"MP");
		}

		partName = new JTextField();
		partName.setText(node.getPart().getPartName());

		account = new JTextField();
		mptype = new JComboBox(mpValues);
		pindex = new JTextField();
		keyComponent = new JComboBox(keyComponentValues);
		mindex = new JTextField();
		phasecode = new JComboBox(Constants.phasecodeValues);
		cindex = new JTextField();

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
		label5.setPreferredSize(new Dimension(243, 25));

		//上级零组件节点
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

		c.insets = new Insets(10, 105, 5, 0);
		partNumber.setPreferredSize(new Dimension(150, 25));
		topPanel.add(partNumber, c);

		//名称
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 3;
		topPanel.add(label2, c);

		c.insets = new Insets(10, 105, 5, 15);
		partName.setPreferredSize(new Dimension(150, 25));
		topPanel.add(partName, c);

		//毛坯件类型
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 4;
		topPanel.add(label5, c);

		c.insets = new Insets(10, 105, 5, 15);
		mptype.setPreferredSize(new Dimension(150, 25));
		topPanel.add(mptype, c);

		//数量
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 5;
		topPanel.add(label3, c);
		account.setEditable(false);
		c.insets = new Insets(10, 105, 5, 25);
		account.setPreferredSize(new Dimension(150, 25));
		topPanel.add(account, c);
		c.insets = new Insets(10, 260, 5, 0);
		JLabel message = new JLabel("大于0的正整数");
		message.setForeground(Color.GRAY);
		topPanel.add(message, c);

		//产品代号
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 6;
		topPanel.add(pindexLabel, c);

		c.insets = new Insets(10, 105, 5, 15);
		pindex.setPreferredSize(new Dimension(150, 25));
		topPanel.add(pindex, c);

		//关重件标记
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 7;
		topPanel.add(keyComponentLabel, c);

		c.insets = new Insets(10, 105, 5, 15);
		keyComponent.setPreferredSize(new Dimension(150, 25));
		topPanel.add(keyComponent, c);

		//所属型号
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 8;
		topPanel.add(mindexLabel, c);

		c.insets = new Insets(10, 105, 5, 15);
		mindex.setPreferredSize(new Dimension(150, 25));
		topPanel.add(mindex, c);

		//阶段标记
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 9;
		topPanel.add(phasecodeLabel, c);

		c.insets = new Insets(10, 105, 5, 15);
		phasecode.setPreferredSize(new Dimension(150, 25));
		topPanel.add(phasecode, c);

		//图号
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 10;
		topPanel.add(cindexLabel, c);
		c.insets = new Insets(10, 105, 5, 15);
		cindex.setPreferredSize(new Dimension(150, 25));
		topPanel.add(cindex, c);

		bottomPanel.setLayout(new GridBagLayout());
		c.insets = new Insets(10, 5, 5, 15);
		c.gridy = 11;
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
				if (null == partNumber.getText() || "".equals(partNumber.getText().trim())) {
					JOptionPane.showMessageDialog(dialog, "毛坯件编号不能为空！");
				}else if(CmCommonStringUtil.checkHasChinaese(partNumber.getText())){
					JOptionPane.showMessageDialog(dialog, "毛坯件编号中不能有中文！");
				} else if (null == partName.getText() || "".equals(partName.getText().trim())) {
					JOptionPane.showMessageDialog(dialog, "毛坯件名称不能为空！");
				} else if(CmCommonStringUtil.isEmpty(account.getText())){
					JOptionPane.showMessageDialog(dialog, "毛坯件数量不能为空！");
				} else {
					long ida2a2 = PBOMEditorToWCIntf.queryLatestPartIdByNumberRMI(partNumber.getText());
					if(ida2a2 != -1) {
						int flag = JOptionPane.showConfirmDialog(dialog, "PDM中已经存在相同编号的零部件，请确认是否需要添加？","确认", JOptionPane.OK_CANCEL_OPTION);
						if(flag != 0) {
							return;
						}
					} else {
						int flag = JOptionPane.showConfirmDialog(dialog, "请确认是否创建？","确认", JOptionPane.OK_CANCEL_OPTION);
						if(flag != 0) {
							return;
						}
					}
					Map<String,String> map = new HashMap<String,String>();
					map.put("number", partNumber.getText());
					map.put("name", partName.getText());
					map.put("parentPartOid", String.valueOf(node.getPart().getOid()));
					map.put("typeName","mp");

					Map<String,String> ibamap = new HashMap<String,String>();
					ibamap.put("MTYPE", String.valueOf(mptype.getSelectedItem()));
					ibamap.put("PINDEX", String.valueOf(pindex.getText()));
					ibamap.put("KEYCOMPONENT", String.valueOf(keyComponent.getSelectedItem()));
					ibamap.put("MINDEX", String.valueOf(mindex.getText()));
					ibamap.put("PHASE_CODE", String.valueOf(phasecode.getSelectedItem()));
					ibamap.put("CINDEX", String.valueOf(cindex.getText()));

					List<Object> list=PBOMEditorToWCIntf.createMiddleORAssistantPart(map,ibamap);
					if(CmCommonStringUtil.isEmpty((String)list.get(0))){
						sureButton.setVisible(false);
						WTPart wtPart = (WTPart)list.get(1);
						part = new CmLightPart();
						part.setPartName(partName.getText().toLowerCase());
						part.setPartNumber(partNumber.getText().toUpperCase());
						part.setPartType("mp");
						part.setMtype(String.valueOf(mptype.getSelectedItem()));
						part.setPindex(String.valueOf(pindex.getText()));
						part.setKeycomponent(String.valueOf(keyComponent.getSelectedItem()));
						part.setMindex(String.valueOf(mindex.getText()));
						part.setPhase_code(String.valueOf(phasecode.getSelectedItem()));
						part.setCindex(String.valueOf(cindex.getText()));
						part.setUseCount(Integer.parseInt(account.getText()));
						part.setOperType("new");
						part.setOid(wtPart.getPersistInfo().getObjectIdentifier().getId());
						part.setContainerId(node.getPart().getContainerId());
						try {
							part.setLifecycle(wtPart.getLifeCycleState().getDisplay(SessionHelper.getLocale()));
							part.setVersion(wtPart.getVersionIdentifier().getValue() + "."+ wtPart.getIterationIdentifier().getValue());
						} catch (WTException e1) {
							e1.printStackTrace();
						}
						String item = part.getPartNumber() + "(" + part.getPartName()+ ") ";
						CmTreeNode cmTreeNode = new CmTreeNode(item);
						cmTreeNode.setPart(part);

//						CmCommonStringUtil.addCommonMiddleNodeWithCommonParent(tree.getRoot(),cmTreeNode,Integer.parseInt(account.getText()),node);
//						EbomTreeCancelAction.addPbomTreeChange(cmTreeNode,null, "create",null);
//
//						CmCommonPackageAction common = new CmCommonPackageAction();
//						common.packageOneNode(tree.getRoot(), cmTreeNode);

						try {
//							ExtCommonDellFunction.addExistPartNodeToCommonNode(tree.getRoot(), cmTreeNode, node, sl, tree);
							List<CmTreeNode> retList = ExtCommonDellFunction.addNewPartToCommonNode(tree.getRoot(), cmTreeNode, node, Integer.parseInt(account.getText()), false);
							CmCommonStringUtil.setIsNewTop(retList);
							ExtCommonDellFunction.changeCommonNodeUseCount(tree.getRoot(), cmTreeNode, node, Integer.parseInt(account.getText()), false, tree);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						ExtCommonDellFunction.packageAllNodeContanChildernNode(node, tree);

						CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
						tree.updateUI();
						PbomTreeEditReportAction.updatePbomTreeEditReport();
						BomTreeReportAction.updateBomReport();
						dialog.setVisible(false);
					}
					else{
						JOptionPane.showMessageDialog(tree.getRootPane(),list.get(0));
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
	}

	private void loadInitDatas() {
		sureButton.setText("确定");
		cancelButton.setText("取消");
	}
}

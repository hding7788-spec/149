package com.glaway.mpm.pbombuilder.tree.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreeCancelAction;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeEditReportAction;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

public class AddSearchAssistDialog {
	private JDialog dialog;
	private JTable table;
	
	private JPanel topPanel;
	private JLabel numberLable;
	private JTextField numberText;
	private JLabel nameLable;
	private JTextField nameText;
	private JButton searchButton;
	private JButton clearButton;
	private JButton sureButton;
	private JButton cancelButton;
	private JScrollPane scroTablePanel;
	private JPanel buttonpanel;
	private List<Map<String,String>> partlist;
	private CmTreeNode objNode;
	private JDialog parentDialog;
	private CmTree tree;
	private DefaultTableModel model;

	public AddSearchAssistDialog(CmTreeNode node,JDialog jdialog,CmTree tree) {
		super();
		this.objNode = node;
		this.parentDialog = jdialog;
		this.tree = tree;
	}

	public void showDialog() {
		// 新增对话框
		newJDialog();
		initComponents();
		initActions();
		initLayout();
	}

	/**
	 * 新增对话框jdialog
	 */
	public void newJDialog() {
		dialog = new JDialog();
		dialog.setTitle("插入现有的工艺辅件");
		dialog.setSize(500, 530);
		dialog.setIconImage(CmUtil.getImageFromServer("assist.gif"));
		dialog.setResizable(false);
		dialog.setLayout(new BorderLayout());
		CmCommonStringUtil.setMiddleOnScreenWithDialog(dialog); 
	}

	private void initComponents() {
		topPanel = new JPanel();
		numberLable = new JLabel("编号：");
		numberText = new JTextField();
		nameLable = new JLabel("名称：");
		nameText = new JTextField();
		searchButton = new JButton("搜索");
		clearButton = new JButton("清除");
		model = new DefaultTableModel(new Object [][] { }, new String [] {"编号", "名称", "批次", "比例"});
		table = new JTable(model){
			public boolean isCellEditable(int row, int column) { 
				return false;
			}
		};
		table.getTableHeader().setReorderingAllowed(false);// 表格是否可移动
		table.setCursor(Cursor.getDefaultCursor());
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				 if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {// 双击鼠标左键
						 String partNumber = (String) table.getModel().getValueAt(table.getSelectedRow(), 0);
						 doubleClick(partNumber);
				 }else if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1){
					 sureButton.setEnabled(true);
				 }
			}
		});
		buttonpanel = new JPanel();
		sureButton = new JButton("确定");
		sureButton.setEnabled(false);
		cancelButton = new JButton("取消");
	}
	/**
	 * 双击事件
	 * @author chenyunlong
	 * @date  2013-4-12
	 * @param value
	 *
	 */
	public void doubleClick(String value){
		boolean flag = true;
		Enumeration children = this.objNode.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if("assistant".equals(child.getPart().getPartType()) && CmCommonStringUtil.isEqual(child.getPart().getPartNumber(), value)){
				flag = false;
			}
		}
		if(flag){
			Map<String,String> selectPart = null;
			for(Map<String,String> map:partlist){
				if(CmCommonStringUtil.isEqual(map.get("partNumber"), value)){
					selectPart = map;
					break;
				}
			}
			CmLightPart newpart = new CmLightPart();
			newpart.setOid(Long.valueOf(selectPart.get("oid")));
			newpart.setPartNumber(selectPart.get("partNumber"));
			newpart.setPartName(selectPart.get("partName"));
			newpart.setPartType("assistant");
			newpart.setRemark(selectPart.get("remark"));
			//对系统中的脏数据的处理
			if(CmCommonStringUtil.isEmpty(selectPart.get("productionQuantity")) && CmCommonStringUtil.isEmpty(selectPart.get("productionRatio"))){
				//如果批次和比例信息都为空，则默认设置批次为1
				newpart.setProductionQuantity(1);
			}
			else{
				newpart.setProductionQuantity(Integer.parseInt(selectPart.get("productionQuantity")));
			}
			newpart.setProductionRatio(selectPart.get("productionRatio"));
			newpart.setKey(Boolean.valueOf(selectPart.get("isKey")));
			newpart.setSpecial(Boolean.valueOf(selectPart.get("isSpecial")));
			newpart.setMaterialType(selectPart.get("materialType"));
			newpart.setBackupRate(selectPart.get("backupRate"));
			newpart.setMaxBackupCount(selectPart.get("maxBackupCount"));
			newpart.setBackupReason(selectPart.get("backupReason"));
			newpart.setWorkShop(selectPart.get("workShop"));
			newpart.setOutsourcingUnits(selectPart.get("outsourcingUnits"));
			newpart.setLifecycle(selectPart.get("lifecycle"));
			newpart.setVersion(selectPart.get("version"));
			newpart.setContainerId(Long.valueOf(selectPart.get("containerId")));
			String item = newpart.getPartNumber() + "(" + newpart.getPartName()+ ") " ;
			CmTreeNode cmTreeNode = new CmTreeNode(item);
			cmTreeNode.setPart(newpart);
			CmCommonStringUtil.addCommonAssistNodeWithCommonParent(tree.getRoot(),cmTreeNode,this.objNode);
			EbomTreeCancelAction.addPbomTreeChange(cmTreeNode,null, "create",null);
			CmCommonStringUtil.addNodeToCheckList(cmTreeNode);
			CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
			tree.updateUI();
			PbomTreeEditReportAction.updatePbomTreeEditReport();
			BomTreeReportAction.updateBomReport();
			dialog.setVisible(false);
			parentDialog.setVisible(false);
		}else{
			JOptionPane.showMessageDialog(tree.getRootPane(), "无法添加相同工艺辅件！");
		}
	}

	private void initLayout() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.SOUTHWEST;
		topPanel.setLayout(new GridBagLayout());
		
		c.insets = new Insets(10, 0, 5, 0);
		c.gridy = 1;
		c.gridx = 1;
		topPanel.add(numberLable, c);
		
		c.insets = new Insets(10, 60, 5, 0);
		numberText.setPreferredSize(new Dimension(150, 25));
		topPanel.add(numberText, c);
		
		c.insets = new Insets(10, 0, 5, 15);
		c.gridy = 2;
		topPanel.add(nameLable, c);
		
		c.insets = new Insets(10, 60, 5, 0);
		nameText.setPreferredSize(new Dimension(150, 25));
		topPanel.add(nameText, c);
		
		c.insets = new Insets(10, 60, 5, 15);
		c.gridy = 3;
		topPanel.add(searchButton, c);
		c.insets = new Insets(10, 110, 5, 0);
		topPanel.add(clearButton, c);
		
		scroTablePanel =new JScrollPane(table);
		scroTablePanel.getViewport().setBackground(Color.WHITE);
		scroTablePanel.setPreferredSize(new Dimension(500, 350));
		
		buttonpanel.setLayout(new GridBagLayout());
		c.insets = new Insets(10, 350, 5, 15);
		c.gridy = 1;
		buttonpanel.add(sureButton, c);
		c.insets = new Insets(10, 400, 5, 0);
		buttonpanel.add(cancelButton, c);

		dialog.add(topPanel, BorderLayout.NORTH);
		dialog.add(scroTablePanel,BorderLayout.CENTER);
		dialog.add(buttonpanel,BorderLayout.SOUTH);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				dialog.setModal(true);
				dialog.setVisible(true);
			}
		});
	}
	
	private void initActions() {
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(CmCommonStringUtil.isEmpty(numberText.getText().trim()) && CmCommonStringUtil.isEmpty(nameText.getText().trim())){
					model.setDataVector(new Object[0][4], new String [] {"编号", "名称", "批次", "比例"});
					sureButton.setEnabled(false);
					table.updateUI();
					JOptionPane.showMessageDialog(tree.getRootPane(), "请至少指定一个有效的搜索条件！");
				}else{
					try {
						Thread updateSearch = new Thread() {
							public void run() {
								searchButton.setEnabled(false);
								Map<String,String> map = new HashMap<String,String>();
								map.put("number", numberText.getText().toUpperCase());
								map.put("name", nameText.getText().toLowerCase());
								partlist = PBOMEditorToWCIntf.getAllAssistantPart(map);
								if(null == partlist || partlist.size()==0){
									JOptionPane.showMessageDialog(tree.getRootPane(), "未找到结果！");
								}
								else{
									sureButton.setEnabled(false);
									Object[][] asnode = new Object[partlist.size()][4];
									for(int i=0;i<partlist.size();i++){
										asnode[i][0] = partlist.get(i).get("partNumber");
										asnode[i][1] = partlist.get(i).get("partName");
										asnode[i][2] = partlist.get(i).get("productionQuantity");
										asnode[i][3] = partlist.get(i).get("productionRatio");
									}
									model.setDataVector(asnode, new String [] {"编号", "名称", "批次", "比例"});
									table.updateUI();
								}
								searchButton.setEnabled(true);
							}
						};
						updateSearch.start();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				numberText.setText(null);
				nameText.setText(null);
			}
		});
		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String partNumber = (String) table.getModel().getValueAt(table.getSelectedRow(), 0);
				doubleClick(partNumber);
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
	}
}

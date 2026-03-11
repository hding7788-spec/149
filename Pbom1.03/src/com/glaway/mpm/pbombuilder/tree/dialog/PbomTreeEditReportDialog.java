package com.glaway.mpm.pbombuilder.tree.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmScrollPaneTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeEditReportAction;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;

public class PbomTreeEditReportDialog  extends JDialog{
	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;

	private JLabel newNode;
	private JTable newNodeTable;

	private JPanel bottomPanel;//按钮
	private JButton sureButton;
	private JButton cancelButton;
	private static List<CmTreeNode> editList;
	private JScrollPane tableScrollPanel;
	private static PbomTreeEditReportDialog instance;

	private PbomTreeEditReportDialog(){
		// 新增对话框
		newJDialog( );
		filterNode();
		initComponents();
		initActions();
		initLayout();
	}

	public void showDialog() {
		filterNode();
		newNodeTable.setModel(new javax.swing.table.DefaultTableModel(
				getBomReportTable(),
				new String [] {"零件编号", "零件名称", "版本", "数量","主制单位","辅制单位","物料类型","材料编码","材料名称","物资编码","物资名称"}
				//	            new String [] {"零件编号", "零件名称", "版本","关键件(Y/N)","特殊件(Y/N)","星载表格化(Y/N)","主制单位","建议外协单位","物料类型","工艺备份比例(%)","最大备份数","备份原因","备注"}
	        ));
	}

	/**
	 * 新增对话框jdialog
	 */
	public void newJDialog() {
		this.setTitle("PBOM编辑报表");
		this.setSize(1050, 500);
		this.setIconImage(CmUtil.getImageFromServer("pbom_report.gif"));
		this.setResizable(true);
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		CmCommonStringUtil.setMiddleOnScreenWithDialog(this);
	}

	@SuppressWarnings("serial")
	private void initComponents() {
		newNode = new JLabel("PBOM编辑报表");
		newNode.setPreferredSize(new Dimension(110, 25));
		mainPanel = new JPanel();
		mainPanel.setBorder(new EtchedBorder());
		mainPanel.setLayout(new BorderLayout());
		bottomPanel=new JPanel();
		sureButton = new JButton("确定");
		cancelButton = new JButton("取消");
		DefaultTableModel tableModel=new DefaultTableModel();
		newNodeTable = new JTable(tableModel){
			@SuppressWarnings("unused")
			public boolean isCellEditable(Object node, int column) {
				return false;//表格不可编辑
			}
		};
		newNodeTable.getTableHeader().setReorderingAllowed(false);// 表格是否可移动
		newNodeTable.setEnabled(false);
		newNodeTable.setModel(new javax.swing.table.DefaultTableModel(
				getBomReportTable(),
				new String [] {"零件编号", "零件名称", "版本", "数量","主制单位","辅制单位","物料类型","材料编码","材料名称","物资编码","物资名称"}
//	            new String [] {"零件编号", "零件名称", "版本","关键件(Y/N)","特殊件(Y/N)","星载表格化(Y/N)","主制单位","建议外协单位","物料类型","工艺备份比例(%)","最大备份数","备份原因","备注"}
	        ));
	}

	private void initLayout() {
		tableScrollPanel=new JScrollPane(newNodeTable);
		tableScrollPanel.getViewport().setBackground(Color.WHITE);
		tableScrollPanel.setPreferredSize(new Dimension(1010, 280));
		mainPanel.add(tableScrollPanel,BorderLayout.CENTER);

		//按钮
		bottomPanel.add(sureButton);
//		bottomPanel.add(cancelButton);
		bottomPanel.setBorder(new EtchedBorder());
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		FlowLayout flow1 = new FlowLayout();
		flow1.setAlignment(FlowLayout.CENTER);
		bottomPanel.setLayout(flow1);

		this.add(mainPanel);
	}

	private void initActions() {
		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PbomTreeEditReportAction.flag=false;
				CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree().updateUI();
				setVisible(false);
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
	}

	public Object[][] getBomReportTable(){
		//List<CmTreeNode> pbomList=CmScrollPaneTree.pbomlist;
		Collection<CmTreeNode> pbomList= CmScrollPaneTree.pbomMap.values();
		Object[][] obj=new Object[editList.size()][13];
		for(int i=0;i<editList.size();i++){
			CmTreeNode report=editList.get(i);
			obj[i][0]=report.getPart().getPartNumber();
			obj[i][1]=report.getPart().getPartName();
			obj[i][2]=getBomCompareResult(report.getPart().getVersion(),getTheNodeFromPbomList(report,pbomList,2),false);
			obj[i][3]=getBomCompareResult(CmCommonStringUtil.emptyToString(String.valueOf(report.getPart().getUseCount())),getTheNodeFromPbomList(report,pbomList,3),false);
			obj[i][4]=getBomCompareResult(CmCommonStringUtil.emptyToString(report.getPart().getFirstPlant()),getTheNodeFromPbomList(report,pbomList,4),false);
			obj[i][5]=getBomCompareResult(CmCommonStringUtil.emptyToString(report.getPart().getSecondePlantStr()),getTheNodeFromPbomList(report,pbomList,5),false);
			obj[i][6]=getBomCompareResult(report.getPart().getMaterialType(),getTheNodeFromPbomList(report,pbomList,6),false);
			obj[i][7]=getBomCompareResult(report.getPart().getWzk().getClcode(),getTheNodeFromPbomList(report,pbomList,7),false);
			obj[i][8]=getBomCompareResult(report.getPart().getWzk().getClName(),getTheNodeFromPbomList(report,pbomList,8),false);
			obj[i][9]=getBomCompareResult(report.getPart().getWzk().getInvcode(),getTheNodeFromPbomList(report,pbomList,9),true);
			obj[i][10]=getBomCompareResult(report.getPart().getWzk().getInvname(),getTheNodeFromPbomList(report,pbomList,10),true);
		}
		return obj;
	}
	public String getTheNodeFromPbomList(CmTreeNode node,Collection<CmTreeNode> list,int column){
		for(CmTreeNode cmnode:list){
			if(CmCommonStringUtil.isCommonNode(cmnode, node)){
				CmLightPart n=cmnode.getPart();
				switch(column){
				case 2:
					return n.getVersion();
				case 3:
					return CmCommonStringUtil.emptyToString(String.valueOf(n.getUseCount()));
				case 4:
					return CmCommonStringUtil.emptyToString(n.getFirstPlant());
				case 5:
					return CmCommonStringUtil.emptyToString(n.getSecondePlantStr());
				case 6:
					return CmCommonStringUtil.emptyToString(n.getMaterialType());
				case 7:
					return CmCommonStringUtil.emptyToString(n.getWzk().getClcode());
				case 8:
					return CmCommonStringUtil.emptyToString(n.getWzk().getClName());
				case 9:
					return CmCommonStringUtil.emptyToString(n.getWzk().getInvcode());
				case 10:
					return CmCommonStringUtil.emptyToString(n.getWzk().getInvname());

				}
			}
		}
		return null;
	}

	public String getBomCompareResult(String edit,String before,boolean isNumber){
		if(CmCommonStringUtil.emptyToString(edit).equals(CmCommonStringUtil.emptyToString(before))){
			return edit;
		}
		else{
			if(CmCommonStringUtil.isEmpty(before)&&!CmCommonStringUtil.isEmpty(edit)){
				return "() > "+edit;
			}
			else if(!CmCommonStringUtil.isEmpty(before)&&CmCommonStringUtil.isEmpty(edit)){
				return before + " > ()";
			}
			else if(isNumber){
				return before +" -> "+edit;
			}
			else{
				return before +" > "+edit;
			}
		}
	}

	public void filterNode() {
		editList=new ArrayList<CmTreeNode>();
		Enumeration children =CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree().getRoot().children();
		if(children.hasMoreElements()){
			CmTreeNode root=(CmTreeNode) children.nextElement();
			getEditNodeList(root);
		}
	}

	@SuppressWarnings("unchecked")
	public void getEditNodeList(CmTreeNode node){
		if(("new".equals(node.getPart().getOperType()) || node.getPart().isEdit()) && null==CmCommonStringUtil.checkTheNodeIsInList(node,editList)){
			editList.add(node);
		}
		Enumeration children=node.children();
		while(children.hasMoreElements()){
			CmTreeNode child=(CmTreeNode) children.nextElement();
			getEditNodeList(child);
		}
	}



	public static PbomTreeEditReportDialog getInstance(){
		if(instance==null){
			instance = new PbomTreeEditReportDialog();
		}
		return instance;
	}

	public static void colseBomReport(){
		if(null != instance){
			instance.setVisible(false);
		}
	}
}

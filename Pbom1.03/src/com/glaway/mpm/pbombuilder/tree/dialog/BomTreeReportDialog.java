package com.glaway.mpm.pbombuilder.tree.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
import com.glaway.mpm.pbombuilder.tree.CmReportNode;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;

/**
 * EBOM和PBOM比较的报表
 * @author chenyunlong
 *
 */
public class BomTreeReportDialog extends JDialog{
	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;

	private JLabel newNode;
	private JTable newNodeTable;

	private JPanel bottomPanel;//按钮
	private JButton sureButton;
	private JButton cancelButton;
	private static List<CmReportNode> reportNodeList;
	private List<CmTreeNode> list;
	private JScrollPane tableScrollPanel;
	private static BomTreeReportDialog instance;

	private BomTreeReportDialog(){
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
	            new String [] {"零件编号", "零件名称", "版本", "数量","主制单位","辅制单位","零组件生产类型","材料编码","材料名称","物资编码","物资名称"}
	        ));
	}

	/**
	 * 新增对话框jdialog
	 */
	public void newJDialog() {
		this.setTitle("PBOM与EBOM比较报表");
		this.setSize(1050, 500);
		this.setIconImage(CmUtil.getImageFromServer("report.gif"));
		this.setResizable(true);
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		CmCommonStringUtil.setMiddleOnScreenWithDialog(this);
	}

	@SuppressWarnings("serial")
	private void initComponents() {
		newNode = new JLabel("BOM比较报告");
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
				new String [] {"零件编号", "零件名称", "版本", "数量","主制单位","辅制单位","零组件生产类型","材料编码","材料名称","物资编码","物资名称"}
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
				BomTreeReportAction.flag=false;
				CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree().updateUI();
				CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree().updateUI();
				setVisible(false);
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BomTreeReportAction.flag=false;
				setVisible(false);
			}
		});
	}

	public Object[][] getBomReportTable(){
		Object[][] obj=new Object[reportNodeList.size()][14];
		for(int i=0;i<reportNodeList.size();i++){
			CmReportNode report=reportNodeList.get(i);
			obj[i][0]=report.getPbomPart()!=null?report.getPbomPart().getPartNumber():report.getEbomPart().getPartNumber();
			obj[i][1]=report.getPbomPart()!=null?report.getPbomPart().getPartName():report.getEbomPart().getPartName();
			obj[i][2]=getBomVersion(report);
			obj[i][3]=getBomCount(report);
			obj[i][4]=report.getPbomPart()!=null?CmCommonStringUtil.emptyToString(report.getPbomPart().getZzcj()):CmCommonStringUtil.emptyToString(report.getEbomPart().getZzcj());
			obj[i][5]=report.getPbomPart()!=null?CmCommonStringUtil.emptyToString(report.getPbomPart().getFzcj()):CmCommonStringUtil.emptyToString(report.getEbomPart().getFzcj());
			obj[i][6]=report.getPbomPart()!=null?CmCommonStringUtil.emptyToString(report.getPbomPart().getMtype()):CmCommonStringUtil.emptyToString(report.getEbomPart().getMtype());
			obj[i][7]=report.getPbomPart()!=null?CmCommonStringUtil.emptyToString(report.getPbomPart().getWzk().getClcode()):CmCommonStringUtil.emptyToString(report.getEbomPart().getWzk().getClcode());
			obj[i][8]=report.getPbomPart()!=null?CmCommonStringUtil.emptyToString(report.getPbomPart().getWzk().getClName()):CmCommonStringUtil.emptyToString(report.getEbomPart().getWzk().getClName());
			obj[i][9]=report.getPbomPart()!=null?CmCommonStringUtil.emptyToString(report.getPbomPart().getWzk().getInvcode()):CmCommonStringUtil.emptyToString(report.getEbomPart().getWzk().getInvcode());
			obj[i][10]=report.getPbomPart()!=null?CmCommonStringUtil.emptyToString(report.getPbomPart().getWzk().getInvname()):CmCommonStringUtil.emptyToString(report.getEbomPart().getWzk().getInvname());
		}
		return obj;
	}

	public String emptyToString(String obj,boolean flag) {
		if(CmCommonStringUtil.isEmpty(obj)){
			return "";
		}
		else{
			if(flag){
				return obj+" -> 0";
			}
			else{
				return "0 -> " + obj;
			}
		}
	}

	public String getBomVersion(CmReportNode report){
		if(null==report.getEbomPart() && null!=report.getPbomPart()){
			//新增中间件、辅件
			return report.getPbomPart().getVersion();
		}else if(null!=report.getEbomPart() && null==report.getPbomPart()){
			//删除
			return report.getEbomPart().getVersion()+" > ()";
		}
		else{
			return getBomCompareResult(report.getEbomPart().getVersion(),report.getPbomPart().getVersion());
		}
	}

	public String getBomCount(CmReportNode report){
		if(null==report.getEbomPart() && null!=report.getPbomPart()){
			//新增中间件、辅件
			return emptyToString(report.getPbomPart().getGysl(),false);
		}else if(null!=report.getEbomPart() && null==report.getPbomPart()){
			//删除
			return emptyToString(report.getEbomPart().getUseCount()+"",true);
		}
		else{
			return report.getEbomPart().getUseCount() +" -> "+report.getPbomPart().getGysl();
		}
	}

	public String getBomCompareResult(String ebom,String pbom){
		if(CmCommonStringUtil.emptyToString(ebom).equals(CmCommonStringUtil.emptyToString(pbom))){
			return ebom;
		}
		else{
			if(CmCommonStringUtil.isEmpty(ebom)&&!CmCommonStringUtil.isEmpty(pbom)){
				return "() > "+pbom;
			}
			else if(!CmCommonStringUtil.isEmpty(ebom)&&CmCommonStringUtil.isEmpty(pbom)){
				return ebom + " > ()";
			}
			else{
				return ebom +" > "+pbom;
			}
		}
	}

	public void filterNode() {
		List<CmTreeNode> ebomList = CmCommonStringUtil.getBomNodeList(CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree());
		List<CmTreeNode> pbomList = CmCommonStringUtil.getBomNodeList(CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree());
		list= new ArrayList<CmTreeNode>();
		reportNodeList = new ArrayList<CmReportNode>();
		for (CmTreeNode ebom : ebomList) {
			if(!CmCommonStringUtil.checkHasCommonNodeInList(ebom, list)){
				list.add(ebom);
				addPbomUpdateNode(ebom, pbomList,ebomList);
			}
		}
		addPbomNewNode(pbomList);
	}

	/**
	 * 想报表中添加Pbom变动过的节点信息（包括更改的节点和删除的节点）
	 *
	 * @param ebom
	 * @param pbomList
	 * @param report
	 *
	 */
	public void addPbomUpdateNode(CmTreeNode ebom, List<CmTreeNode> pbomList,List<CmTreeNode> ebomList) {
		CmReportNode report = new CmReportNode();
		report.setEbomPart(ebom.getPart());
		report.setEbomCount(getCommonNodeCountInList(ebom, ebomList)+"");
		CmTreeNode pbomNode = null;
		for (CmTreeNode pbom : pbomList) {
			if (!CmCommonStringUtil.isNewNode(pbom.getPart().getPartType()) && CmCommonStringUtil.isCommonNode(ebom, pbom)) {
				pbomNode = pbom;
				break;
			}
		}
		if(null!=pbomNode){
			report.setPbomPart(pbomNode.getPart());
		}
		else{
			report.setPbomPart(null);
		}
		report.setPbomCount(getCommonNodeCountInList(ebom, pbomList)+"");
		if (!CmCommonStringUtil.isEqual(report.getEbomCount(), report.getPbomCount())
				|| !CmCommonStringUtil.isSamePart(report.getEbomPart(), report.getPbomPart())) {
			//节点的数量或者属性值
			reportNodeList.add(report);
		}
	}
	/**
	 * 在list中获取相同零件的数量
	 * @author chenyunlong
	 * @date  2013-4-25
	 * @param node
	 * @param list
	 * @return
	 *
	 */
	public int getCommonNodeCountInList(CmTreeNode node, List<CmTreeNode> list) {
		int i = 0;
		for (CmTreeNode cmnode : list) {
			if (CmCommonStringUtil.isCommonNode(node, cmnode)) {
				i++;
			}
		}
		return i;
	}

	/**
	 * 添加PBOM中添加的中间件和辅件
	 *
	 * @param pbomList
	 *
	 */
	public void addPbomNewNode(List<CmTreeNode> pbomList) {
		List<CmTreeNode> list = new ArrayList<CmTreeNode>();
		for (CmTreeNode pbom : pbomList) {
			if ("middle".equals(pbom.getPart().getPartType())
					|| "assistant".equals(pbom.getPart().getPartType())
					|| "mp".equals(pbom.getPart().getPartType())
					|| "zuhe".equals(pbom.getPart().getPartType())
					|| "middle2".equals(pbom.getPart().getPartType())) {
				boolean flag = true;
				for(CmTreeNode node:list){
					if(CmCommonStringUtil.isCommonNode(node, pbom)){
						flag = false;
						break;
					}
				}
				if(flag){
					list.add(pbom);
					CmReportNode report = new CmReportNode();
					report.setPbomCount(getCommonNodeCountInList(pbom, pbomList)+"");
					report.setPbomPart(pbom.getPart());
					reportNodeList.add(report);
				}
			}
		}
	}

	public static BomTreeReportDialog getInstance(){
		if(instance==null){
			instance = new BomTreeReportDialog();
		}
		return instance;
	}

	public static int getTheReportNodeStatus(CmTreeNode node){
		int i=0;
		if(BomTreeReportAction.flag && null!=reportNodeList && reportNodeList.size()>0){
			for(CmReportNode report:reportNodeList){
				if(CmCommonStringUtil.isCommonPart(node.getPart(),report.getEbomPart()) && CmCommonStringUtil.isCommonPart(node.getPart(),report.getPbomPart())){
					//更新节点-----该节点在EBOM和PBOM众都存在
					i= 1;
				}
				else if(!CmCommonStringUtil.isCommonPart(node.getPart(),report.getEbomPart()) && CmCommonStringUtil.isCommonPart(node.getPart(),report.getPbomPart())){
					//新增节点-----该节点只在PBOM中存在
					i= 2;
				}else if(CmCommonStringUtil.isCommonPart(node.getPart(),report.getEbomPart()) && !CmCommonStringUtil.isCommonPart(node.getPart(),report.getPbomPart())){
					//删除节点----该节点只在EBOM众存在
					i= 3;
				}
			}
		}
		return i;
	}

	public static void colseBomReport(){
		if(null != instance){
			instance.setVisible(false);
		}
	}
}

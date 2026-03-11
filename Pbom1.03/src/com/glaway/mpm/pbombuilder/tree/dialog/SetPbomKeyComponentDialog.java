package com.glaway.mpm.pbombuilder.tree.dialog;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.glaway.mpm.pbombuilder.bom.CmConnectDialog;
import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.tree.CmCancelPart;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmScrollPaneTree;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeEditReportAction;
import com.glaway.mpm.pbombuilder.tree.pbom.JTreeTable;
import com.glaway.mpm.pbombuilder.tree.pbom.KeyComponentListModel;
import com.glaway.mpm.pbombuilder.tree.pbom.ListModel;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;

/**
 *
 * Created on 2012-10-23
 *
 * @author chenyunlong
 */
public class SetPbomKeyComponentDialog{
	private static final long serialVersionUID = 1L;
	private CmTreeNode treeNode;
	public static List<CmTreeNode> cmTreeNodeList = null;//保存所有的节点
	private JButton sureButton;//确定按钮
	public static JButton cancelButton;//撤销按钮
	private JButton clearButton;//重置按钮
	private JButton clearRemarkButton;//清空备注按钮
	public static JTreeTable treeTable;
	public static JCheckBox checkBox;
	private JButton notSelectButton;
	private CmTree pbomtree;
	public static JDialog dialog;
	private static SetPbomKeyComponentDialog instance;  //定义PBOM关重件标识
	private KeyComponentListModel listModel;
	private String type;
	private String title = null;
	private CmTreeNode rootPart;

	public static SetPbomKeyComponentDialog getInstance(CmTreeNode node,CmTree pbomTree,String type){
		if(instance==null){
			instance = new SetPbomKeyComponentDialog(node,pbomTree,type);
		}
		return instance;
	}

	public SetPbomKeyComponentDialog(CmTreeNode node,CmTree pbomTree,String type) {
		cmTreeNodeList = new ArrayList<CmTreeNode>();
		this.treeNode = node;
		this.type = type;
		clearTreeTable(pbomTree.getRoot());
		this.pbomtree=pbomTree;
		this.rootPart = (CmTreeNode)pbomTree.getRoot().children().nextElement();
		title = "定义关重件标识";
		newJDialog();
		initComponents();
		initTable();
		initAction();
		initLayout();
	}

	@SuppressWarnings("unchecked")
	public void clearTreeTable(CmTreeNode root){
		root.setChildren(null);
		if(CmCommonStringUtil.isPackage(root)){
			for(CmTreeNode brother:root.getListNode()){
				clearTreeTable(brother);
			}
		}
		Enumeration children = root.children();
		while(children.hasMoreElements()){
			clearTreeTable((CmTreeNode)children.nextElement());
		}
	}

	/**
	 * 用户选择完中间模型，关闭对话框后，返回被下载的文件的地址list
	 *
	 * @return
	 */
	public void showDialog(CmTreeNode node,CmTree pbomTree) {
		cmTreeNodeList = new ArrayList<CmTreeNode>();
		this.treeNode = node;
		this.pbomtree=pbomTree;
		checkBox.setSelected(false);
		//cancelButton.setEnabled(false);
		initTable();
		initLayout();
		dialog.setVisible(true);
	}

	@SuppressWarnings("unchecked")
	public void arrayCmTreeNode(CmTreeNode node) {
		node.getPart().setSelected(false);
		Enumeration list = node.children();
		List<CmTreeNode> nodeList = new ArrayList<CmTreeNode>();
		while (list.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) list.nextElement();

			//过滤掉借用件
			if(rootPart.getPart().getContainerId() != child.getPart().getContainerId()) {
				System.out.println(title +" 借用件："+child.getPart().getPartNumber()+" 不能修改！");
				continue;
			}

			if(!CmCommonStringUtil.isHasFilingOfObj(child)){
				if (!CmCommonStringUtil.checkHasCommonNodeInList(child, cmTreeNodeList)) {
					child.getPart().setSelected(false);
					arrayCmTreeNode(child);
					String mtype = child.getPart().getMtype();
					nodeList.add(child);
					//CmCommonNodeUtil.updateNodeAtrribute(child);
					cmTreeNodeList.add(child);
				}
			}
		}
		node.setChildren(nodeList);
	}

	/**
	 * 新增对话框jdialog
	 */
	public void newJDialog() {
		dialog = new JDialog();
		dialog.setTitle(title);
		dialog.setSize(1100, 500);
		dialog.setMinimumSize(new Dimension(900, 500));
		dialog.setIconImage(CmUtil.getImageFromServer("mbom_edit.png"));
		dialog.setResizable(true);
		dialog.setCursor(Cursor.getDefaultCursor());
		CmCommonStringUtil.setMiddleOnScreenWithDialog(dialog);
		dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                if(!CmCommonStringUtil.isEmpty(CmConnectDialog.partOid)){//只进行PBOM属性编辑，而不进行PBOM重构
                	CmMBomMainFrame.savePbomWithOnlyPbomEdit(pbomtree, dialog, true);
                }
            }
        });
	}

	private void initComponents() {
		checkBox=new JCheckBox("全选");
		notSelectButton=new JButton("不选");
		sureButton = new JButton("确定");
		cancelButton = new JButton("取消");
		clearButton = new JButton("重置");
		clearRemarkButton = new JButton("清空备注");
		checkBox.setCursor(Cursor.getDefaultCursor());
		notSelectButton.setCursor(Cursor.getDefaultCursor());
		sureButton.setCursor(Cursor.getDefaultCursor());
		cancelButton.setCursor(Cursor.getDefaultCursor());
		clearButton.setCursor(Cursor.getDefaultCursor());
		clearRemarkButton.setCursor(Cursor.getDefaultCursor());
	}

	private void initTable(){
		cmTreeNodeList.add(this.treeNode);
		arrayCmTreeNode(this.treeNode);
		addDialogType(this.treeNode,this.type);
		listModel = new KeyComponentListModel(this.treeNode,pbomtree,this.type);
		treeTable = new JTreeTable(listModel,type);
		treeTable.revalidate();
		treeTable.getColumnModel().getColumn(0).setPreferredWidth(350);
		treeTable.getColumnModel().getColumn(0).setMinWidth(240);
		treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//只能单选，没有拖拽事件
		treeTable.setRowHeight(25);
		treeTable.getTableHeader().setReorderingAllowed(false);// 表格是否可移动
		treeTable.getTableHeader().setResizingAllowed(true);// 表格大小是否可以变化
		treeTable.getColumnModel().moveColumn(1,0);//移动列（将第二列移动到第一列）
		treeTable.putClientProperty("terminateEditOnFocusLost",Boolean.TRUE);
		treeTable.setCursor(Cursor.getDefaultCursor());
	}

	private void initLayout() {
		JPanel background=new JPanel();
		background.setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		panel.setSize(new Dimension(800, 100));
		background.add(panel, BorderLayout.SOUTH);
		FlowLayout flow = new FlowLayout();
		flow.setAlignment(FlowLayout.CENTER);
		panel.setLayout(flow);
		panel.add(checkBox);
		panel.add(notSelectButton);
		panel.add(sureButton);
//		panel.add(clearButton);
		panel.add(cancelButton);
//		panel.add(clearRemarkButton);
		background.add(new JScrollPane(treeTable), BorderLayout.CENTER);
		background.setCursor(Cursor.getDefaultCursor());
		dialog.setContentPane(background);
	}

	public void addDialogType(CmTreeNode node,String type){
		node.getPart().setDialogType(3);
		List<CmTreeNode> childlist = node.getChildren();
		if(null != childlist && childlist.size()>0){
			for(CmTreeNode child:childlist){
				addDialogType(child,type);
			}
		}
	}

	public void initAction() {
		treeTable.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				@SuppressWarnings("unused")
				JTreeTable table =(JTreeTable) evt.getSource();
				System.out.println("addPropertyChangeListener");

			}
		});

		//确定
		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
        		for(CmTreeNode cmnode:cmTreeNodeList){
            		updateBrotherNodesOfPbomTree(cmnode,true);
        		}
            	pbomtree.updateUI();
            	dialog.setVisible(false);
			}
		});

		//撤销
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelOperation();
				treeTable.updateUI();
				pbomtree.updateUI();
			}
		});

		//全选
		checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				JCheckBox box=(JCheckBox) e.getSource();
				allNodesSelected(box.isSelected());
				treeTable.updateUI();
			}
		});

		//不选
		notSelectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkBox.setSelected(false);
				allNodesSelected(false);
				treeTable.updateUI();
			}
		});

		//重置
	   clearButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			CmCancelPart clearCancel = new CmCancelPart();
			List<CmCancelPart> typelist = new ArrayList<CmCancelPart>();
			addNodeToCancelList(treeNode,typelist);
			listModel.updateBrotherNodesOfPbomTree(treeNode, false);
			circleCleanTreeNode(treeNode,typelist);
			clearCancel.setList(typelist);
			clearCancel.setClear(true);
			ListModel.cancelList.add(clearCancel);
			if(null != ListModel.cancelList && ListModel.cancelList.size()>0){
				SetPbomKeyComponentDialog.cancelButton.setEnabled(true);
			}
			else{
				SetPbomKeyComponentDialog.cancelButton.setEnabled(false);
			}
			treeTable.updateUI();
			checkBox.setSelected(false);
			pbomtree.updateUI();
		}
	   });

	   //清空备注
	   clearRemarkButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			List<CmCancelPart> remarklist = new ArrayList<CmCancelPart>();
			clearRemark(treeNode,remarklist);
			if(null != remarklist && remarklist.size()>0){
				CmCancelPart clearRemark = new CmCancelPart();
				clearRemark.setList(remarklist);
				clearRemark.setClearRemark(true);
				ListModel.cancelList.add(clearRemark);
				if(null != ListModel.cancelList && ListModel.cancelList.size()>0){
					SetPbomKeyComponentDialog.cancelButton.setEnabled(true);
				}
				else{
					SetPbomKeyComponentDialog.cancelButton.setEnabled(false);
				}
				treeTable.updateUI();
				pbomtree.updateUI();
			}
		}
	   });

	}

	/**
	 * 遍历treetable
	 * @date  2013-1-15
	 * @param node
	 * @param typelist
	 *
	 */
	public void circleCleanTreeNode(CmTreeNode node,List<CmCancelPart> typelist){
		if(null!=node.getChildren()&& node.getChildren().size()>0){
			for(CmTreeNode cmnode:node.getChildren()){
				addNodeToCancelList(cmnode,typelist);
				listModel.updateBrotherNodesOfPbomTree(cmnode, false);
				circleCleanTreeNode(cmnode,typelist);
			}
		}
	}

	/**
	 * 同步更新零件的其他兄弟节点
	 * @date  2013-1-29
	 * @param node
	 *
	 */
	public void updateBrotherNodesOfPbomTree(CmTreeNode node, boolean isUpdateUI) {
		for (CmTreeNode cmnode : listModel.getPbomTreeList()) {
			if (CmCommonStringUtil.isCommon(node, cmnode)) {
				cmnode.getPart().setKeycomponent(node.getPart().getKeycomponent());
				CmCommonStringUtil.checkPbomTreeNodeIsEdit(cmnode);
			}
		}
		PbomTreeEditReportAction.updatePbomTreeEditReport();
		BomTreeReportAction.updateBomReport();
		if (isUpdateUI) {
			pbomtree.updateUI();
		}
	}

	/**
	 * 将节点添加到撤销前的对象list中去
	 * @date  2013-1-15
	 * @param node
	 * @param typelist
	 *
	 */
	public void addNodeToCancelList(CmTreeNode node,List<CmCancelPart> typelist){
		CmCancelPart cancel=new CmCancelPart();
		cancel.setCancelNode(node);
		cancel.setPart(CmCommonStringUtil.copyCmlightPart(node.getPart()));
		clearPbomTreeNode(node);
		typelist.add(cancel);
	}

	/**
	 * 全选
	 * @date  2013-1-15
	 * @param flag
	 *
	 */
	public void allNodesSelected(boolean flag){
		for(CmTreeNode edit:cmTreeNodeList){
			if(!"PBOM".equals(edit.toString())){
				edit.getPart().setSelected(flag);
				if(flag){
					//批量操作对象集
					KeyComponentListModel.nodeList.add(edit);
				}
				else{
					KeyComponentListModel.nodeList.remove(edit);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void allNodesSelected(boolean flag,CmTreeNode node){
		Enumeration children=node.children();
		while(children.hasMoreElements()){
			CmTreeNode child=(CmTreeNode) children.nextElement();
			if(!CmCommonStringUtil.isHasFilingOfObj(child)){
				child.getPart().setSelected(flag);
				if(flag){
					//批量操作对象集
					KeyComponentListModel.nodeList.add(child);
				}
				else{
					KeyComponentListModel.nodeList.remove(child);
				}
			}
			allNodesSelected(flag,child);
		}
	}

	/**
	 * 撤销
	 * @date  2013-1-15
	 *
	 */
	public void cancelOperation() {
		List<CmCancelPart> cancelList=ListModel.cancelList;
		System.out.println("--cancelList---"+cancelList);
		if (null != cancelList && cancelList.size() > 0) {
//			CmCancelPart part = cancelList.get(cancelList.size() - 1);
//			if(part.isClear()){
//				for(CmCancelPart cancel:part.getList()){
//					cancel.getCancelNode().setPart(cancel.getPart());
//					listModel.updateBrotherNodesOfPbomTree(cancel.getCancelNode(), false);
//					CmCommonStringUtil.checkPbomTreeNodeIsEdit(cancel.getCancelNode());
//				}
//			}

			for(CmCancelPart cancel:cancelList) {
				if(cancel.isChangeKeyComponent()) {
					cancel.getCancelNode().getPart().setKeycomponent(cancel.getOldKeyComponent());
				}
				CmCommonStringUtil.checkPbomTreeNodeIsEdit(cancel.getCancelNode());
			}

			PbomTreeEditReportAction.updatePbomTreeEditReport();
			BomTreeReportAction.updateBomReport();
			pbomtree.updateUI();
			//cancelList.remove(part);
			if(null != cancelList && cancelList.size()>0){
				cancelButton.setEnabled(true);
			}
			else{
				cancelButton.setEnabled(false);
			}
		}
		dialog.setVisible(false);
	}

	/**
	 * 将零件（包括父节点或其他节点上的相同零件）属性清空
	 * @date  2013-1-15
	 * @param node
	 *
	 */
	public void clearPbomTreeNode(CmTreeNode node){
		for (CmTreeNode cmnode : cmTreeNodeList) {
			if (CmCommonStringUtil.isCommonNode(node, cmnode)) {
				CmTreeNode pbom =getNodeFromPbomTreeList(node);
				CmLightPart pbompart=pbom.getPart();
				CmLightPart part =node.getPart();
				part.setKey(pbompart.isKey());
				part.setSpecial(pbompart.isSpecial());
				part.setSpaceBorneTable(pbompart.isSpaceBorneTable());
				part.setSelected(pbompart.isSelected());
				part.setWorkShop(pbompart.getWorkShop());
				part.setOutsourcingUnits(pbompart.getOutsourcingUnits());
				part.setMaterialType(pbompart.getMaterialType());
				part.setBackupRate(pbompart.getBackupRate());
				part.setMaxBackupCount(pbompart.getMaxBackupCount());
				part.setBackupReason(pbompart.getBackupReason());
				part.setRemark(pbompart.getRemark());
				CmCommonStringUtil.checkPbomTreeNodeIsEdit(node);
				PbomTreeEditReportAction.updatePbomTreeEditReport();
				BomTreeReportAction.updateBomReport();
			}
		}
	}

	/**
	 * 找到刚开始打开PBOM的时候的节点信息
	 * @author chenyunlong
	 * @date  2013-4-7
	 * @param node
	 * @return
	 *
	 */
	public CmTreeNode getNodeFromPbomTreeList(CmTreeNode node){
//		for(CmTreeNode cmnode:CmScrollPaneTree.pbomlist){
//			if(CmCommonStringUtil.isSameNode(node, cmnode)){
//				return cmnode;
//			}
//		}
//		return null;
		return CmScrollPaneTree.pbomMap.get(node);
	}

	/**
	 * pbomTreeList是按照零部件的个数存放在PbomTreeList里的，所以
	 * 需要将相同编号的零部件过滤出来
	 *
	 * @param pbomTreeList
	 * @return
	 */
	public List<CmTreeNode> filterPbomTreeList(List<CmTreeNode> pbomTreeList) {
		List<CmTreeNode> pbomTreeListTemp = new ArrayList<CmTreeNode>();
		if(pbomTreeList != null) {
			for (CmTreeNode cmTreeNode : pbomTreeList) {
				String partNumber = cmTreeNode.getPart().getPartNumber();
				boolean flag = false;
				for (CmTreeNode cmTreeNode2 : pbomTreeListTemp) {
					String partNumberTemp = cmTreeNode2.getPart().getPartNumber();
					if(partNumber.equals(partNumberTemp)) {
						flag = true;
						break;
					}
				}
				if(!flag) {
					pbomTreeListTemp.add(cmTreeNode);
				}
			}
		}
		return pbomTreeListTemp;
	}

	/**
	 * 清空备注
	 * @author chenyunlong
	 * @date  2013-4-8
	 * @param node
	 * @param remarkList
	 *
	 */
	public void clearRemark(CmTreeNode node,List<CmCancelPart> remarkList){
		if(!CmCommonStringUtil.isEmpty(node.getPart().getRemark())){
			CmCancelPart remark = new CmCancelPart();
			remark.setObject(node.getPart().getRemark());
			remark.setCancelNode(node);
			remarkList.add(remark);
			node.getPart().setRemark(null);
			listModel.updateBrotherNodesOfPbomTree(node, false);
		}
		if(null!=node.getChildren()&& node.getChildren().size()>0){
			for(CmTreeNode cmnode:node.getChildren()){
				clearRemark(cmnode,remarkList);
			}
		}
	}

	public static void colseEditPbomDialog(){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(null != dialog){
					dialog.setVisible(false);
				}
			}
		});
	}

	public static void unSelectAllCheckBox() {
		checkBox.setSelected(false);
	}
}

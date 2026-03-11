package com.glaway.mpm.pbombuilder.tree.pbom;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.glaway.mpm.pbom.table.KVItem;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.CmCancelPart;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeEditReportAction;
import com.glaway.mpm.pbombuilder.tree.dialog.SetPbomKeyComponentDialog;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.LoadConfig;

public class KeyComponentListModel extends AbstractTreeTableModel implements TreeTableModel {
	private static final CmLogger log = CmLogger.getLogger(KeyComponentListModel.class.getName());
	public static List<CmTreeNode> nodeList;//保存批量操作的对象
	public static List<CmCancelPart> cancelList;//PBOM编辑属性：保存记录便于回退
	private CmTree pbomTree;
	private List<CmTreeNode> pbomTreeList;//保存PBOM树上所有节点
	private String type ;
	protected String[] cNames = null;
	List<KVItem> plantList = LoadConfig.getInstance().getPlans();
	static protected Class[] cTypes = { TreeTableModel.class, Boolean.class, Boolean.class, Boolean.class,Boolean.class,
			String.class,String.class, String.class, String.class, String.class, String.class, String.class };

	public KeyComponentListModel(CmTreeNode p,CmTree pbomtree,String type) {
		super(p);
		this.type = type;
		cNames = new String[]{ "编号", "批量操作", "数量", "材料", "材料名称", "规格", "关重件标识", "新值"};
		cTypes = new Class[]{ TreeTableModel.class, Boolean.class, String.class, String.class, String.class, String.class, String.class, String.class };
		this.pbomTree = pbomtree;
		getAllNodesFromPbom(pbomTree);
		nodeList = new ArrayList<CmTreeNode>();
		cancelList = new ArrayList<CmCancelPart>();
	}

	protected List<CmTreeNode> getChildren(Object node) {
		return ((CmTreeNode) node).getChildren();
	}

	public int getChildCount(Object node) {
		List<CmTreeNode> children = getChildren(node);
		return (children == null) ? 0 : children.size();
	}

	public Object getChild(Object node, int i) {
		return getChildren(node).get(i);
	}

	public boolean isLeaf(Object node) {
		CmTreeNode root = (CmTreeNode) node;
		return root.getChildren() == null || root.getChildren().size() == 0;
	}

	public int getColumnCount() {
		return cNames.length;
	}

	public String getColumnName(int column) {
		return cNames[column];
	}

	public Class getColumnClass(int column) {
		return cTypes[column];
	}

	public Object getValueAt(Object node, int column) {
		CmTreeNode part = ((CmTreeNode) node);
		CmLightPart n = part.getPart();
		switch (column) {
			case 0:
				return n.getPartNumber();
			case 1:
				return n.isSelected();
			case 2:
				return n.getUseCount();
			case 3:
				return n.getCmat();
			case 4:
				return n.getPtc_material_name();
			case 5:
				return n.getCsize();
			case 6:
				return n.getKeycomponent();
			case 7:
				return n.getKeycomponent();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, Object node, int column) {
		CmTreeNode part = (CmTreeNode) node;
		//System.out.println("ListModel.setValueAt----"+part.getPart().getPartNumber()+"  column:"+column+"  value:"+aValue);
		setBrotherNodeValue(part, aValue, column);
	}

	public void setBrotherNodeValue(CmTreeNode node, Object aValue, int column) {
		List<CmTreeNode> list = SetPbomKeyComponentDialog.cmTreeNodeList;
		for (CmTreeNode cmnode : list) {
			if (CmCommonStringUtil.isCommonNode(node, cmnode)) {
				CmLightPart n = node.getPart();

				switch (column) {
				case 1:
					if (null == nodeList) {
						nodeList = new ArrayList<CmTreeNode>();
					}
					if (n.isSelected()) {
						System.out.println("---取消了---"+n.getPartNumber());
						nodeList.remove(node);
						n.setSelected(false);
					} else {
						System.out.println("---选择了---"+n.getPartNumber());
						nodeList.add(node);
						n.setSelected(true);
					}
					//n.setSelected(!n.isSelected());
					break;

				case 7:
					if(!CmCommonStringUtil.isEqual(n.getKeycomponent(), (String) aValue)){
						System.out.println("---nodeList---"+nodeList);
						if (isBatchOperation(node, nodeList)) {
							for (CmTreeNode batchnode : nodeList) {
								//保存修改前的值，以便取消时恢复
								saveBeforeKeyComponentUpdateValue(batchnode.getPart().getKeycomponent(),batchnode);

								batchnode.getPart().setKeycomponent((String) aValue);
								batchnode.getPart().setSelected(false);
								batchnode.getPart().setEdit(true);
								//updateBrotherNodesOfPbomTree(batchnode,false);
							}
							nodeList.clear();

							//取消批量勾选
							unSelectAllCheckBox();
						} else {
							//保存修改前的值，以便取消时恢复
							saveBeforeKeyComponentUpdateValue(n.getKeycomponent(),node);

							n.setKeycomponent((String) aValue);
							n.setSelected(false);
							n.setEdit(true);
							//updateBrotherNodesOfPbomTree(node,true);
						}
						SetPbomKeyComponentDialog.treeTable.updateUI();
						pbomTree.updateUI();
					}
				}

				super.setValueAt(aValue, node, column);
			}
		}
	}

	/**
	 * 保存修改前的零组件生产类型值
	 * @param column
	 * @param beforeValue
	 * @param node
	 *
	 */
	public void saveBeforeKeyComponentUpdateValue(String beforeValue,CmTreeNode node){
		boolean b = false;
		for(CmCancelPart cancelPart:cancelList) {
			if(cancelPart.getCancelNode().getPart().getPartNumber().equals(node.getPart().getPartNumber())) {
				if(!cancelPart.isChangeKeyComponent()) {
					cancelPart.setOldKeyComponent(beforeValue);
					cancelPart.setChangeKeyComponent(true);
					b = true;
					break;
				} else {
					b = true;
				}
			}
		}
		if(!b) {
			CmCancelPart before = new CmCancelPart();
			before.setOldKeyComponent(beforeValue);
			before.setCancelNode(node);
			before.setChangeKeyComponent(true);
			cancelList.add(before);
		}
	}

	/**
	 * 是否进行批量操作
	 *
	 * @author chenyunlong
	 * @date 2012-11-30
	 * @param node
	 * @param nodeList
	 * @return
	 *
	 */
	public boolean isBatchOperation(CmTreeNode node, List<CmTreeNode> nodeList) {
		boolean flag = false;
		if (null != nodeList && nodeList.size() > 0) {
			for (CmTreeNode cmnode : nodeList) {
				if (CmCommonStringUtil.isCommon(node, cmnode)) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	/**
	 * 获取树上所有节点
	 * @date  2013-1-29
	 * @param pbomtree
	 *
	 */
	public void getAllNodesFromPbom(CmTree pbomtree){
		pbomTreeList = new ArrayList<CmTreeNode>();
		getNodesFromCycle(pbomtree.getRoot());
	}

	public List<CmTreeNode> getPbomTreeList(){
		return this.pbomTreeList;
	}

	public void getNodesFromCycle(CmTreeNode node){
		Enumeration children = node.children();
		while(children.hasMoreElements()){
			CmTreeNode child =(CmTreeNode) children.nextElement();
			CmCommonNodeUtil.setTreeNodeFzbm(child);
			pbomTreeList.add(child);
			if(CmCommonStringUtil.isPackage(child)){
				for(CmTreeNode brother:child.getListNode()){
					CmCommonNodeUtil.setTreeNodeFzbm(brother);
					pbomTreeList.add(brother);
				}
			}
			if(CmCommonStringUtil.isPackageOfParent(child)){
				CmTreeNode parent = CmCommonStringUtil.getParentPackageNode(child);
				if(null != parent){
					for(CmTreeNode cn:parent.getListNode()){
						getNodesFromCycle(cn);
					}
				}
			}
			getNodesFromCycle(child);
		}
	}

	/**
	 * 同步更新零件的其他兄弟节点
	 * @date  2013-1-29
	 * @param node
	 *
	 */
	public void updateBrotherNodesOfPbomTree(CmTreeNode node, boolean isUpdateUI) {
		for (CmTreeNode cmnode : pbomTreeList) {
			if (CmCommonStringUtil.isCommon(node, cmnode)) {
				cmnode.getPart().setMtype(node.getPart().getMtype());
				cmnode.getPart().setZzcj(node.getPart().getZzcj());
				cmnode.getPart().setFzcj(node.getPart().getFzcj());
				cmnode.getPart().setSecondePlant(node.getPart().getSecondePlant());
				CmCommonStringUtil.checkPbomTreeNodeIsEdit(cmnode);
			}
		}
		PbomTreeEditReportAction.updatePbomTreeEditReport();
		BomTreeReportAction.updateBomReport();
		if (isUpdateUI) {
			pbomTree.updateUI();
		}
	}

	public String objToNumber(Object aValue){
		String obj = ((String) aValue).matches("[0-9]*") ? (String) aValue : "0";
		return CmCommonStringUtil.emptyToNumber(obj);
	}

	private void unSelectAllCheckBox() {
		SetPbomKeyComponentDialog.checkBox.setSelected(false);
	}
}
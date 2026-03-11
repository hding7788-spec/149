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
import com.glaway.mpm.pbombuilder.tree.dialog.SetPBOMReleasedDialog;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.LoadConfig;

public class PBOMReleasedListModel extends AbstractTreeTableModel implements TreeTableModel {
	private static final CmLogger log = CmLogger.getLogger(PBOMReleasedListModel.class.getName());
	public static List<CmTreeNode> nodeList;//保存批量操作的对象
	public static List<CmCancelPart> cancelList;//PBOM编辑属性：保存记录便于回退
	public static boolean isOk = true;
	private CmTree pbomTree;
	private List<CmTreeNode> pbomTreeList;//保存PBOM树上所有节点
	protected String[] cNames = null;
	List<KVItem> plantList = LoadConfig.getInstance().getPlans();
	// Types of the columns.
	static protected Class[] cTypes = { TreeTableModel.class, String.class,String.class,
			String.class,String.class, String.class, String.class, String.class, String.class, String.class,String.class, Boolean.class };

	private SetPBOMReleasedDialog dialog;

	public PBOMReleasedListModel(CmTreeNode p,CmTree pbomtree,SetPBOMReleasedDialog dialog) {
		super(p);
		cNames = new String[]{ "编号", "图号", "阶段标记", "关重件标记", "零组件生产类型", "工艺路线", "物资编码", "主工艺编号","主工艺状态","材料定额状态","批次号", "是否借用件"};
		this.pbomTree = pbomtree;
		this.dialog = dialog;
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
		CmTreeNode rootNode = (CmTreeNode)pbomTree.getRoot().children().nextElement();
		CmLightPart rootPart = rootNode.getPart();
		CmTreeNode part = ((CmTreeNode) node);
		CmLightPart n = part.getPart();
		if("否".equals(n.getIsOk()) && rootPart.getBatch().equals(n.getBatch())) {
			dialog.setSureButton(false);
		}

		switch (column) {
		case 0:
			return n.getPartNumber();
		case 1:
			return n.getCindex();
		case 2:
			return n.getPhase_code();
		case 3:
			return n.getKeycomponent();
		case 4:
			return n.getMtype();
		case 5:
			return n.getRouting();
		case 6:
			return n.getWzk().getInvcode();
		case 7:
			return n.getZgyNumber();
		case 8:
			return n.getZgyzt();
		case 9:
			return n.getZldezt();
		case 10:
			return n.getBatch();
		case 11:
			return n.isBorrowedPart();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, Object node, int column) {
		super.setValueAt(aValue, node, column);
		// 处理借用件列的值设置
		if (column == 11 && aValue instanceof Boolean) {
			CmTreeNode partNode = (CmTreeNode)node;
			CmLightPart lightPart = partNode.getPart();
			lightPart.setBorrowedPart((Boolean)aValue);
		}
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

}
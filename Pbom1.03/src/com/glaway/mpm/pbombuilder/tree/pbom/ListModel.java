package com.glaway.mpm.pbombuilder.tree.pbom;

import com.glaway.mpm.pbom.table.KVItem;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.CmCancelPart;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeEditReportAction;
import com.glaway.mpm.pbombuilder.tree.dialog.SetPbomAttributeDialog;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.LoadConfig;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class ListModel extends AbstractTreeTableModel implements TreeTableModel {
	private static final CmLogger log = CmLogger.getLogger(ListModel.class.getName());
	public static List<CmTreeNode> nodeList;//保存批量操作的对象
	public static List<CmCancelPart> cancelList;//PBOM编辑属性：保存记录便于回退
	private CmTree pbomTree;
	private List<CmTreeNode> pbomTreeList;//保存PBOM树上所有节点
	// Names of the columns.
	private String type ;
//	static protected String[] cNames = { "Part编号", "批量操作", "关键件", "特殊件","*星载表格化",
//			"*主制部门","建议外协单位", "*物料类型", "工艺备份比例(%)", "最大备份数", "备份原因",	"备注" };
	protected String[] cNames = null;
	List<KVItem> plantList = LoadConfig.getInstance().getPlans();
	// Types of the columns.
	static protected Class[] cTypes = { TreeTableModel.class, Boolean.class, Boolean.class, Boolean.class,Boolean.class,
			String.class,String.class, String.class, String.class, String.class, String.class, String.class };

	public ListModel(CmTreeNode p,CmTree pbomtree,String type) {
		super(p);
		this.type = type;
		if("1".equals(type)){//
			cNames = new String[]{ "编号", "批量操作", "数量", "材料", "材料名称", "规格", "零部件类型", "零组件生产类型"};
			cTypes = new Class[]{ TreeTableModel.class, Boolean.class, String.class, String.class, String.class, String.class, String.class, String.class };
		}else if("2".equals(type)){

//			cNames = new String[]{ "Part编号", "批量操作", "关键件",   "物料类型" ,"主制车间","辅制车间1","辅制车间2","辅制车间3" };
//			cTypes = new Class[]{ TreeTableModel.class, Boolean.class, Boolean.class, String.class ,String.class,Boolean.class,Boolean.class,Boolean.class};

			cNames = new String[9+plantList.size()];
			cTypes = new Class[9+plantList.size()];

			cNames[0]= "编号";
			cNames[1]= "批量操作";
			cNames[2]= "零组件生产类型";
			cNames[3]= "主制车间";
			cNames[4]= "数量";
			cNames[5]= "材料";
			cNames[6]= "材料名称";
			cNames[7]= "规格";
			cNames[8]= "零部件类型";
			for(int i=0;i<plantList.size();i++){
				cNames[9+i] = plantList.get(i).getValue();
			}
			cTypes[0] = TreeTableModel.class;
			cTypes[1] = Boolean.class;
			cTypes[2] = String.class;
			cTypes[3] = String.class;
			cTypes[4] = String.class;
			cTypes[5] = String.class;
			cTypes[6] = String.class;
			cTypes[7] = String.class;
			cTypes[8] = String.class;
			for(int i=0;i<plantList.size();i++){
				cTypes[9+i] = Boolean.class;
			}
		}else if("4".equals(type)){//
			cNames = new String[]{ "编号", "批量操作", "数量", "产品代号", "图号", "型号代号", "阶段标记","是否成套件","可调整","密级"};
			cTypes = new Class[]{ TreeTableModel.class, Boolean.class, String.class, String.class, String.class, String.class, String.class,String.class,String.class,String.class};
		}


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
		if("1".equals(type)){
			//如果"零组件生产类型"值为空，则自动继承"零部件分类"的值
			String mtype = n.getMtype();
			if(mtype == null || "".equals(mtype)) {
				//EBOM零部件类型有"辅助材料",则PBOM的零组件生产类型自动映射为"主要材料"
				if(!"辅助材料".equals(n.getCtype())) {
					mtype = n.getCtype();
					if(mtype != null && !"".equals(mtype)) {
						saveBeforeMtypeUpdateValue(n.getMtype(),part);
						n.setMtype(mtype);
						n.setSelected(false);
						n.setEdit(true);
					}
				} else {
					mtype = "主要材料";
					saveBeforeMtypeUpdateValue(n.getMtype(),part);
					n.setMtype(mtype);
					n.setSelected(false);
					n.setEdit(true);
				}
			}

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
					return n.getCtype();
				case 7:
					return mtype;
			}
		}else if("2".equals(type)){
			return	autoColGetValue(plantList.size(),n,column);
		}
		else if("4".equals(type)){
			//如果"可调整"值为空，默认为否
			String adjustable = n.getAdjustable();
			if(adjustable == null || "".equals(adjustable)) {
				adjustable = "否";
				saveBeforeAdjustableUpdateValue(n.getAdjustable(),part);
				n.setAdjustable(adjustable);
				n.setSelected(false);
				n.setEdit(true);
			}

			String secret = n.getSecret();
			if(secret == null || "".equals(secret)) {
				secret = "公开";
				n.setSecret(secret);
				n.setSelected(false);
				n.setEdit(true);
			}

			switch (column) {
			case 0:
				return n.getPartNumber();
			case 1:
				return n.isSelected();
			case 2:
				return n.getUseCount();
			case 3:
				return n.getPindex();
			case 4:
				return n.getCindex();
			case 5:
				return n.getMindex();
			case 6:
				return n.getPhase_code();
			case 7:
				return n.getSetmark();
			case 8:
				return adjustable;
			case 9:
				return secret;
		 }
		}
		return null;
	}

	private Object autoColGetValue(int plantCount,CmLightPart n ,int column){
		if(column==0){
			return n.getPartNumber();
		}else if(column==1){
			return n.isSelected();
		}else if(column==2){
			return n.getUseCount();
		}else if(column==3){
			return n.getCmat();
		}else if(column==4){
			return n.getPtc_material_name();
		}else if(column==5){
			return n.getCsize();
		}else if(column==6){
			return n.getCtype();
		}else if(column==7){
			return n.getMtype();
		}else if(column==8){
			return n.getZzcj();
		}

		Map<String,Boolean> plant = n.getSecondePlant();
		for(int i=1;i<=plantCount;i++){
			String key = plantList.get(i-1).getKey();
			if(column==(i+8)){
				return plant.get(key);
			}
		}

		return null;
	}

	@Override
	public void setValueAt(Object aValue, Object node, int column) {
		CmTreeNode part = (CmTreeNode) node;
		setBrotherNodeValue(part, aValue, column);
	}

	public void setBrotherNodeValue(CmTreeNode node, Object aValue, int column) {
		List<CmTreeNode> list = SetPbomAttributeDialog.cmTreeNodeList;
		for (CmTreeNode cmnode : list) {
			if (CmCommonStringUtil.isCommonNode(node, cmnode)) {
				CmLightPart n = node.getPart();
				if("1".equals(type)){
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
						if(!CmCommonStringUtil.isEqual(n.getMtype(), (String) aValue)){
							//System.out.println("---nodeList---"+nodeList);
							if (isBatchOperation(node, nodeList)) {
								for (CmTreeNode batchnode : nodeList) {
									//保存修改前的值，以便取消时恢复
									saveBeforeMtypeUpdateValue(batchnode.getPart().getMtype(),batchnode);

									batchnode.getPart().setMtype((String) aValue);
									batchnode.getPart().setSelected(false);
									batchnode.getPart().setEdit(true);
									//updateBrotherNodesOfPbomTree(batchnode,false);
								}
								nodeList.clear();

								//取消批量勾选
								unSelectAllCheckBox();
							} else {
								//保存修改前的值，以便取消时恢复
								saveBeforeMtypeUpdateValue(n.getMtype(),node);

								n.setMtype((String) aValue);
								n.setSelected(false);
								n.setEdit(true);
								//updateBrotherNodesOfPbomTree(node,true);
							}
							SetPbomAttributeDialog.treeTable.updateUI();
							pbomTree.updateUI();
						}
					}
				}else if("2".equals(type)){
					autoColSetValue( node, n,  aValue, column, plantList.size());
				}else if("4".equals(type)){
					switch (column) {
					case 1:
						if (null == nodeList) {
							nodeList = new ArrayList<CmTreeNode>();
						}
						if (n.isSelected()) {
							//System.out.println("---取消了---"+n.getPartNumber());
							nodeList.remove(node);
							n.setSelected(false);
						} else {
							//System.out.println("---选择了---"+n.getPartNumber());
							nodeList.add(node);
							n.setSelected(true);
						}
						//n.setSelected(!n.isSelected());
						break;
					case 3:
						if(!CmCommonStringUtil.isEqual(n.getPindex(), (String) aValue)){
							System.out.println("---nodeList---"+nodeList);
							if (isBatchOperation(node, nodeList)) {
								for (CmTreeNode batchnode : nodeList) {
									//保存修改前的值，以便取消时恢复
									saveBeforePindexUpdateValue(batchnode.getPart().getPindex(),batchnode);

									batchnode.getPart().setPindex((String) aValue);
									batchnode.getPart().setSelected(false);
									batchnode.getPart().setEdit(true);
									//updateBrotherNodesOfPbomTree(batchnode,false);
								}
								nodeList.clear();

								//取消批量勾选
								unSelectAllCheckBox();
							} else {
								//保存修改前的值，以便取消时恢复
								saveBeforePindexUpdateValue(n.getPindex(),node);

								n.setPindex((String) aValue);
								n.setSelected(false);
								n.setEdit(true);
								//updateBrotherNodesOfPbomTree(node,true);
							}
							SetPbomAttributeDialog.treeTable.updateUI();
							pbomTree.updateUI();
						}
						 break;
					case 4:
						if(!CmCommonStringUtil.isEqual(n.getCindex(), (String) aValue)){
							System.out.println("---nodeList---"+nodeList);
							if (isBatchOperation(node, nodeList)) {
								for (CmTreeNode batchnode : nodeList) {
									//保存修改前的值，以便取消时恢复
									saveBeforeCindexUpdateValue(batchnode.getPart().getCindex(),batchnode);

									batchnode.getPart().setCindex((String) aValue);
									batchnode.getPart().setSelected(false);
									batchnode.getPart().setEdit(true);
									//updateBrotherNodesOfPbomTree(batchnode,false);
								}
								nodeList.clear();

								//取消批量勾选
								unSelectAllCheckBox();
							} else {
								//保存修改前的值，以便取消时恢复
								saveBeforeCindexUpdateValue(n.getCindex(),node);

								n.setCindex((String) aValue);
								n.setSelected(false);
								n.setEdit(true);
								//updateBrotherNodesOfPbomTree(node,true);
							}
							SetPbomAttributeDialog.treeTable.updateUI();
							pbomTree.updateUI();
						}
						    break;
					case 5:
						if(!CmCommonStringUtil.isEqual(n.getMindex(), (String) aValue)){
							System.out.println("---nodeList---"+nodeList);
							if (isBatchOperation(node, nodeList)) {
								for (CmTreeNode batchnode : nodeList) {
									//保存修改前的值，以便取消时恢复
									saveBeforeMindexUpdateValue(batchnode.getPart().getMindex(),batchnode);

									batchnode.getPart().setMindex((String) aValue);
									batchnode.getPart().setSelected(false);
									batchnode.getPart().setEdit(true);
									//updateBrotherNodesOfPbomTree(batchnode,false);
								}
								nodeList.clear();

								//取消批量勾选
								unSelectAllCheckBox();
							} else {
								//保存修改前的值，以便取消时恢复
								saveBeforeMindexUpdateValue(n.getMindex(),node);

								n.setMindex((String) aValue);
								n.setSelected(false);
								n.setEdit(true);
								//updateBrotherNodesOfPbomTree(node,true);
							}
							SetPbomAttributeDialog.treeTable.updateUI();
							pbomTree.updateUI();
						}
						    break;
					case 6:
						if(!CmCommonStringUtil.isEqual(n.getPhase_code(), (String) aValue)){
							System.out.println("---nodeList---"+nodeList);
							if (isBatchOperation(node, nodeList)) {
								for (CmTreeNode batchnode : nodeList) {
									//保存修改前的值，以便取消时恢复
									saveBeforePhase_codeUpdateValue(batchnode.getPart().getPhase_code(),batchnode);

									batchnode.getPart().setPhase_code((String) aValue);
									batchnode.getPart().setSelected(false);
									batchnode.getPart().setEdit(true);
									//updateBrotherNodesOfPbomTree(batchnode,false);
								}
								nodeList.clear();

								//取消批量勾选
								unSelectAllCheckBox();
							} else {
								//保存修改前的值，以便取消时恢复
								saveBeforePhase_codeUpdateValue(n.getPhase_code(),node);

								n.setPhase_code((String) aValue);
								n.setSelected(false);
								n.setEdit(true);
								//updateBrotherNodesOfPbomTree(node,true);
							}
							SetPbomAttributeDialog.treeTable.updateUI();
							pbomTree.updateUI();
						}
							break;
					case 7:
						if(!CmCommonStringUtil.isEqual(n.getSetmark(), (String) aValue)){
							//System.out.println("---nodeList---"+nodeList);
							if (isBatchOperation(node, nodeList)) {
								for (CmTreeNode batchnode : nodeList) {
									//保存修改前的值，以便取消时恢复
									saveBeforeSetMarkUpdateValue(batchnode.getPart().getSetmark(),batchnode);

									batchnode.getPart().setSetmark((String) aValue);
									batchnode.getPart().setSelected(false);
									batchnode.getPart().setEdit(true);
								}
								nodeList.clear();

								//取消批量勾选
								unSelectAllCheckBox();
							} else {
								//保存修改前的值，以便取消时恢复
								saveBeforeSetMarkUpdateValue(n.getSetmark(),node);

								n.setSetmark((String) aValue);
								n.setSelected(false);
								n.setEdit(true);
							}
							SetPbomAttributeDialog.treeTable.updateUI();
							pbomTree.updateUI();
						}
							break;
						case 8:
							if(!CmCommonStringUtil.isEqual(n.getAdjustable(), (String) aValue)){
								//System.out.println("---nodeList---"+nodeList);
								if (isBatchOperation(node, nodeList)) {
									for (CmTreeNode batchnode : nodeList) {
										//保存修改前的值，以便取消时恢复
										saveBeforeAdjustableUpdateValue(batchnode.getPart().getAdjustable(),batchnode);

										batchnode.getPart().setAdjustable((String) aValue);
										batchnode.getPart().setSelected(false);
										batchnode.getPart().setEdit(true);
									}
									nodeList.clear();

									//取消批量勾选
									unSelectAllCheckBox();
								} else {
									//保存修改前的值，以便取消时恢复
									saveBeforeAdjustableUpdateValue(n.getAdjustable(),node);

									n.setAdjustable((String) aValue);
									n.setSelected(false);
									n.setEdit(true);
								}
								SetPbomAttributeDialog.treeTable.updateUI();
								pbomTree.updateUI();
							}
							break;
						case 9:
							if(!CmCommonStringUtil.isEqual(n.getSecret(), (String) aValue)){
								if (isBatchOperation(node, nodeList)) {
									for (CmTreeNode batchnode : nodeList) {
										//保存修改前的值，以便取消时恢复
										saveBeforeSecretUpdateValue(batchnode.getPart().getSecret(),batchnode);

										batchnode.getPart().setSecret((String) aValue);
										batchnode.getPart().setSelected(false);
										batchnode.getPart().setEdit(true);
									}
									nodeList.clear();

									//取消批量勾选
									unSelectAllCheckBox();
								} else {
									//保存修改前的值，以便取消时恢复
									saveBeforeSecretUpdateValue(n.getAdjustable(),node);
									n.setSecret((String) aValue);
									n.setSelected(false);
									n.setEdit(true);
								}
								SetPbomAttributeDialog.treeTable.updateUI();
								pbomTree.updateUI();
							}
							break;
				   }
			   }
			   super.setValueAt(aValue, node, column);
		    }
	    }
	}

	private void saveBeforeSecretUpdateValue(String secret, CmTreeNode node) {
		boolean b = false;
		for(CmCancelPart cancelPart:cancelList) {
			if(cancelPart.getCancelNode().getPart().getPartNumber().equals(node.getPart().getPartNumber())) {
				if(!cancelPart.isChangeSecret()) {
					cancelPart.setOldSecret(secret);
					cancelPart.setChangeSecret(true);
					b = true;
					break;
				} else {
					b = true;
				}
			}
		}
		if(!b) {
			CmCancelPart before = new CmCancelPart();
			before.setOldSecret(secret);
			before.setCancelNode(node);
			before.setChangeSecret(true);
			cancelList.add(before);
		}
	}


	private void autoColSetValue(CmTreeNode node,CmLightPart n, Object aValue,int column,int plantCount){
		//System.out.println("autoColSetValue---------column="+column+"     aValue="+aValue);
		if(column==1){
			if (null == nodeList) {
				nodeList = new ArrayList<CmTreeNode>();
			}
			if (n.isSelected()) {
				//System.out.println("---取消了---"+n.getPartNumber());
				nodeList.remove(node);
				n.setSelected(false);
			} else {
				//System.out.println("---选择了---"+n.getPartNumber());
				nodeList.add(node);
				n.setSelected(true);
			}
			//n.setSelected(!n.isSelected());
		}
//		else if(column==2){
//			saveBeforeUpdateValue(2,String.valueOf(n.isKey()),node);
//			n.setKey(!n.isKey());
//			updateBrotherNodesOfPbomTree(node,true);
//		}
		else if(column==7){
			if(!CmCommonStringUtil.isEqual(n.getMtype(), (String) aValue)){
				if (isBatchOperation(node, nodeList)) {
					for (CmTreeNode batchnode : nodeList) {
						//保存修改前的值，以便取消时恢复
						saveBeforeMtypeUpdateValue(batchnode.getPart().getMtype(),batchnode);
						batchnode.getPart().setMtype((String) aValue);
						batchnode.getPart().setSelected(false);
						batchnode.getPart().setEdit(true);
						//updateBrotherNodesOfPbomTree(batchnode,false);
					}

					nodeList.clear();

					//取消批量勾选
					unSelectAllCheckBox();
				} else {
					//保存修改前的值，以便取消时恢复
					saveBeforeMtypeUpdateValue(n.getMtype(),node);

					n.setMtype((String) aValue);
					n.setSelected(false);
					n.setEdit(true);
					//updateBrotherNodesOfPbomTree(node,true);
				}
				SetPbomAttributeDialog.treeTable.updateUI();
				pbomTree.updateUI();
			}
		}else if(column==8){
			if(!CmCommonStringUtil.isEqual(n.getZzcj(), (String) aValue)){
				if (isBatchOperation(node, nodeList)) {
					for (CmTreeNode batchnode : nodeList) {
						CmLightPart partNode = batchnode.getPart();
						if("自制件".equals(partNode.getMtype())||"外配套件".equals(partNode.getMtype())
								||"带料委外件".equals(partNode.getMtype())||"不带料委外件".equals(partNode.getMtype())) {
							//保存修改前的主制车间值，以便取消时恢复
							saveBeforeZzcjUpdateValue(partNode.getZzcj(),batchnode);

							partNode.setZzcj((String) aValue);
							partNode.setSelected(false);
							partNode.setEdit(true);
							if(!"".equals(partNode.getZzcj())){
								String key = null;
								for(int i=1;i<=plantCount;i++){
									key = plantList.get(i-1).getKey();
									//saveBeforeUpdateValue(11+i,String.valueOf(partNode.getSecondePlant().get(key)),node);
									if(aValue.equals(key)) {//当主制车间选择的值已经在辅制车间选择了，则取消辅制车间的选择
										//保存修改前的辅制车间的值，以便取消时恢复
										saveBeforeFzcjUpdateValue(partNode.getFzcj(),batchnode);

										partNode.getSecondePlant().put(key, false);
										n.setFzcj(n.getSecondePlantStr());
										//updateBrotherNodesOfPbomTree(batchnode,true);
										//JOptionPane.showMessageDialog(SetPbomAttributeDialog.dialog, partNode.getPartNumber()+" 的辅制车间已经选择该车间！");
									}
								}
							}
						}
					}

					nodeList.clear();

					//取消批量勾选
					unSelectAllCheckBox();
				} else {
					//保存修改前的主制车间值，以便取消时恢复
					saveBeforeZzcjUpdateValue(n.getZzcj(),node);

					n.setZzcj((String) aValue);
					n.setSelected(false);
					n.setEdit(true);
					//updateBrotherNodesOfPbomTree(node,true);
					if(!"".equals(n.getZzcj())){
						String key = null;
						for(int i=1;i<=plantCount;i++){
							key = plantList.get(i-1).getKey();
							//saveBeforeUpdateValue(11+i,String.valueOf(n.getSecondePlant().get(key)),node);
							if(aValue.equals(key)) {//当主制车间选择的值已经在辅制车间选择了，则取消辅制车间的选择
								//保存修改前的辅制车间的值，以便取消时恢复
								saveBeforeFzcjUpdateValue(n.getFzcj(),node);

								n.getSecondePlant().put(key, false);
								n.setFzcj(n.getSecondePlantStr());
								//n.setEdit(true);
								//JOptionPane.showMessageDialog(SetPbomAttributeDialog.dialog, n.getPartNumber()+" 的辅制车间已经选择该车间！");
							}
						}
					}
				}
			}
			SetPbomAttributeDialog.treeTable.updateUI();
			pbomTree.updateUI();
		} else if (column >8) {
			String key = null;
			for(int i=1;i<=plantCount;i++){
				key = plantList.get(i-1).getKey();
				if(column==i+8){
					//saveBeforeUpdateValue(11+i,String.valueOf(n.getSecondePlant().get(key)),node);
					if (isBatchOperation(node, nodeList)) {
						for (CmTreeNode batchnode : nodeList) {
							CmLightPart partNode = batchnode.getPart();
							if(!key.equals(partNode.getZzcj())) {
								//保存修改前的辅制车间的值，以便取消时恢复
								saveBeforeFzcjUpdateValue(partNode.getFzcj(),batchnode);

								partNode.getSecondePlant().put(key, (Boolean)aValue);
								partNode.setFzcj(partNode.getSecondePlantStr());
								partNode.setEdit(true);
								//updateBrotherNodesOfPbomTree(batchnode,true);
							} else {
								JOptionPane.showMessageDialog(SetPbomAttributeDialog.dialog, partNode.getPartNumber()+" 的主制车间已经选择该车间！");
								//break;
							}
							partNode.setSelected(false);
						}

						nodeList.clear();

						//取消批量勾选
						unSelectAllCheckBox();
					} else {
						if(!key.equals(n.getZzcj())) {
							//保存修改前的辅制车间的值，以便取消时恢复
							saveBeforeFzcjUpdateValue(n.getFzcj(),node);

							n.getSecondePlant().put(key, (Boolean)aValue);
							n.setFzcj(n.getSecondePlantStr());
							n.setEdit(true);
							//updateBrotherNodesOfPbomTree(node,true);
						} else {
							JOptionPane.showMessageDialog(SetPbomAttributeDialog.dialog, "主制车间已经选择该车间！");
						}
					}

					break;
				}
			}
			SetPbomAttributeDialog.treeTable.updateUI();
			pbomTree.updateUI();
		}
	}

	/**
	 * 保存修改前的内容
	 * @param column
	 * @param beforeValue
	 * @param node
	 *
	 */
	public void saveBeforeFzcjUpdateValue(String beforeValue,CmTreeNode node){
		boolean b = false;
		for(CmCancelPart cancelPart:cancelList) {
			if(cancelPart.getCancelNode().getPart().getPartNumber().equals(node.getPart().getPartNumber())) {
				if(!cancelPart.isChangeFzcj()) {
					cancelPart.setOldFzcj(beforeValue);
					cancelPart.setSecondePlant(CmCommonStringUtil.copySecondePlant(node.getPart().getSecondePlant()));
					cancelPart.setChangeFzcj(true);
					b = true;
					break;
				} else {
					b = true;
				}
			}
		}
		if(!b) {
			CmCancelPart before = new CmCancelPart();
			before.setOldFzcj(beforeValue);
			before.setSecondePlant(CmCommonStringUtil.copySecondePlant(node.getPart().getSecondePlant()));
			before.setCancelNode(node);
			before.setChangeFzcj(true);
			cancelList.add(before);
		}
	}

	/**
	 * 保存修改前的零组件生产类型值
	 * @param column
	 * @param beforeValue
	 * @param node
	 *
	 */
	public void saveBeforeMtypeUpdateValue(String beforeValue,CmTreeNode node){
		boolean b = false;
		for(CmCancelPart cancelPart:cancelList) {
			if(cancelPart.getCancelNode().getPart().getPartNumber().equals(node.getPart().getPartNumber())) {
				if(!cancelPart.isChangeMType()) {
					cancelPart.setOldMType(beforeValue);
					cancelPart.setChangeMType(true);
					b = true;
					break;
				} else {
					b = true;
				}
			}
		}
		if(!b) {
			CmCancelPart before = new CmCancelPart();
			before.setOldMType(beforeValue);
			before.setCancelNode(node);
			before.setChangeMType(true);
			cancelList.add(before);
		}
	}

	/**
	 * 保存修改前的主制车间值
	 * @param column
	 * @param beforeValue
	 * @param node
	 *
	 */
	public void saveBeforeZzcjUpdateValue(String beforeValue,CmTreeNode node){
		boolean b = false;
		for(CmCancelPart cancelPart:cancelList) {
			if(cancelPart.getCancelNode().getPart().getPartNumber().equals(node.getPart().getPartNumber())) {
				if(!cancelPart.isChangeZzcj()) {
					cancelPart.setOldZzcj(beforeValue);
					cancelPart.setChangeZzcj(true);
					b = true;
					break;
				} else {
					b = true;
				}
			}
		}
		if(!b) {
			CmCancelPart before = new CmCancelPart();
			before.setOldZzcj(beforeValue);
			before.setCancelNode(node);
			before.setChangeZzcj(true);
			cancelList.add(before);
		}
	}

	/**
	 * 保存修改前的产品代号值
	 * @param beforeValue
	 * @param node
	 */

	public void saveBeforePindexUpdateValue(String beforeValue,CmTreeNode node){
		boolean b = false;
		for(CmCancelPart cancelPart:cancelList) {
			if(cancelPart.getCancelNode().getPart().getPartNumber().equals(node.getPart().getPartNumber())) {
				if(!cancelPart.isChangepindex()) {
					cancelPart.setOldPindex(beforeValue);
					cancelPart.setChangepindex(true);
					b = true;
					break;
				} else {
					b = true;
				}
			}
		}
		if(!b) {
			CmCancelPart before = new CmCancelPart();
			before.setOldPindex(beforeValue);
			before.setCancelNode(node);
			before.setChangepindex(true);
			cancelList.add(before);
		}
	}

	/**
	 * 保存修改前的产品图号值
	 * @param beforeValue
	 * @param node
	 */
	public void saveBeforeCindexUpdateValue(String beforeValue,CmTreeNode node){
		boolean b = false;
		for(CmCancelPart cancelPart:cancelList) {
			if(cancelPart.getCancelNode().getPart().getPartNumber().equals(node.getPart().getPartNumber())) {
				if(!cancelPart.isChangecindex()) {
					cancelPart.setOldCindex(beforeValue);
					cancelPart.setChangecindex(true);
					b = true;
					break;
				} else {
					b = true;
				}
			}
		}
		if(!b) {
			CmCancelPart before = new CmCancelPart();
			before.setOldCindex(beforeValue);
			before.setCancelNode(node);
			before.setChangecindex(true);
			cancelList.add(before);
		}
	}
	/**
	 * 保存修改前的产品型号代号值
	 * @param beforeValue
	 * @param node
	 */

	public void saveBeforeMindexUpdateValue(String beforeValue,CmTreeNode node){
		boolean b = false;
		for(CmCancelPart cancelPart:cancelList) {
			if(cancelPart.getCancelNode().getPart().getPartNumber().equals(node.getPart().getPartNumber())) {
				if(!cancelPart.isChangemindex()) {
					cancelPart.setOldMindex(beforeValue);
					cancelPart.setChangemindex(true);
					b = true;
					break;
				} else {
					b = true;
				}
			}
		}
		if(!b) {
			CmCancelPart before = new CmCancelPart();
			before.setOldMindex(beforeValue);
			before.setCancelNode(node);
			before.setChangemindex(true);
			cancelList.add(before);
		}
	}

    /**
     * 	保存修改前的产品阶段标记值
     * @param beforeValue
     * @param node
     */
	public void saveBeforePhase_codeUpdateValue(String beforeValue,CmTreeNode node){
		boolean b = false;
		for(CmCancelPart cancelPart:cancelList) {
			if(cancelPart.getCancelNode().getPart().getPartNumber().equals(node.getPart().getPartNumber())) {
				if(!cancelPart.isChangephase_code()) {
					cancelPart.setOldPhase_code(beforeValue);
					cancelPart.setChangephase_code(true);
					b = true;
					break;
				} else {
					b = true;
				}
			}
		}
		if(!b) {
			CmCancelPart before = new CmCancelPart();
			before.setOldPhase_code(beforeValue);
			before.setCancelNode(node);
			before.setChangephase_code(true);
			cancelList.add(before);
		}
	}

	/**
	 * 	保存修改前的是否成套件
	 * @param beforeValue
	 * @param node
	 */
	public void saveBeforeSetMarkUpdateValue(String beforeValue,CmTreeNode node){
		boolean b = false;
		for(CmCancelPart cancelPart:cancelList) {
			if(cancelPart.getCancelNode().getPart().getPartNumber().equals(node.getPart().getPartNumber())) {
				if(!cancelPart.isChangesetMark()) {
					cancelPart.setOldSetMark(beforeValue);
					cancelPart.setChangesetMark(true);
					b = true;
					break;
				} else {
					b = true;
				}
			}
		}
		if(!b) {
			CmCancelPart before = new CmCancelPart();
			before.setOldSetMark(beforeValue);
			before.setCancelNode(node);
			before.setChangesetMark(true);
			cancelList.add(before);
		}
	}

	/**
	 * 	保存修改前的可调整
	 * @param beforeValue
	 * @param node
	 */
	public void saveBeforeAdjustableUpdateValue(String beforeValue,CmTreeNode node){
		boolean b = false;
		for(CmCancelPart cancelPart:cancelList) {
			if(cancelPart.getCancelNode().getPart().getPartNumber().equals(node.getPart().getPartNumber())) {
				if(!cancelPart.isChangeAdjustable()) {
					cancelPart.setOldAdjustable(beforeValue);
					cancelPart.setChangeAdjustable(true);
					b = true;
					break;
				} else {
					b = true;
				}
			}
		}
		if(!b) {
			CmCancelPart before = new CmCancelPart();
			before.setOldAdjustable(beforeValue);
			before.setCancelNode(node);
			before.setChangeAdjustable(true);
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
		getNodesFromCycle2(pbomtree.getRoot());
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

	public void getNodesFromCycle2(CmTreeNode node){
		Enumeration children = node.children();
		while(children.hasMoreElements()){
			CmTreeNode child =(CmTreeNode) children.nextElement();
			CmCommonNodeUtil.setTreeNodeFzbm(child);
			pbomTreeList.add(child);
//			if(CmCommonStringUtil.isPackage(child)){
//				for(CmTreeNode brother:child.getListNode()){
//					CmCommonNodeUtil.setTreeNodeFzbm(brother);
//					pbomTreeList.add(brother);
//				}
//			}
			getNodesFromCycle2(child);
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
		SetPbomAttributeDialog.checkBox.setSelected(false);
	}
}
package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.util.List;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.tree.TreeNode;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.data.CmMenuItemFactory;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmLightType;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmSettings;
import com.glaway.mpm.pbombuilder.util.CmTypeHelper;

/**
 * PBOM右键菜单
 * <br>Created on 2012-10-29
 * @author chenyunlong
 */
public class CmMBomMMenuItemFactory extends CmMenuItemFactory {
//	 private static CmLogger   log      = CmLogger.getLogger(CmMBomMMenuItemFactory.class);

	   private boolean           isEdit   = true;
	   private List<CmLightType> canCreateType;

	   private CmLightType       gyzjType = null;
	   private List<String>      gyzjEditState;

	   private CmLightType       aoType   = null;
	   private List<String>      aoEditState;

	   /**
	    * @param b
	    */
	   public CmMBomMMenuItemFactory(boolean b) {
	      this.isEdit = b;
	      canCreateType = new Vector<CmLightType>();
	      gyzjEditState = new Vector<String>();
	      CmSettings setting = CmSettings.getSection(CmSettings.SECTION_MBOM);
	      String gyzjDisplayType = setting.get("mbom.newNode.type.工艺组件", "");
	      gyzjType = CmTypeHelper.getLightType(gyzjDisplayType, true);
	      aoType = CmTypeHelper.getLightType("AO", true);
	      String[] states = setting.get("mbom.gongyizujian.canEdit.state.displayName", new String[0]);
	      for (String state : states) {
	         gyzjEditState.add(state);
	      }

	      states = setting.get("mbom.ao.canEdit.state.displayName", new String[0]);
	      aoEditState = new Vector<String>();
	      for (String state : states) {
	         aoEditState.add(state);
	      }

	      String[] canCreateTypes = setting.get("mbom.newNode.canCreate.types", new String[0]);
	      for (String strCanCreateType : canCreateTypes) {
	         canCreateType.add(CmTypeHelper.getLightType(strCanCreateType, true));
	      }

	   }

	   public CmMenuItem[] createMenuItem(CmTree tree, CmTreeNode currNode, Window owner) {
	      return getMenuItem(tree, currNode, owner);
	      /*if (isEdit) {
	       return createEditMenuItem(tree, currNode, owner);
	       } else {
	       return createCommMenuItem(tree, currNode, owner);
	       }*/
	   }

		public JMenu[] createMenu(CmTree tree, CmTreeNode currNode, Window owner) {
			JMenu [] menus = new JMenu[1];
			menus[0] = new CMErpMenu(tree,currNode,owner);
			return menus;
		}

	   public CmMenuItem[] getMenuItem(CmTree tree, CmTreeNode currNode, Window owner) {
		  // CmCommonNodeUtil common = new CmCommonNodeUtil();
		   //common.clearBomSearchResult(tree.getRoot());
		   CmTreeNode aoRoot=null;
		   String aoState = "";
		   if(tree.getRoot().children().hasMoreElements()){
			   aoRoot = (CmTreeNode) tree.getRoot().getFirstChild();
			   aoState = aoRoot.getPart().getState();
		   }
//	      CmLightType currNodeType = CmTypeHelper.getLightType(currNode.getPart().getPartType(), false);
	      //String currNodeState = currNode.getPart().getState();

	      boolean canEdit = isEdit;
	      List<CmMenuItem> itemList = new Vector<CmMenuItem>();

	      if(currNode==aoRoot){
//	            itemList.add(new CmUpdateZhanWeiiMenuItem(owner,tree,currNode));
	      }
	      //if(currNodeType.isA(gyzjType)&&!jyzjEditState.contains(currNodeState)){
	      CmTreeNode gyzjNode = isGYZJChild(currNode);

	      if (gyzjNode != null) {
	         String gyzjNodeState = gyzjNode.getPart().getState();
	         CmMenuItem gyzjNewVersion= new CmGYZJNewVersionMenuItem(tree,gyzjNode);
	         itemList.add(gyzjNewVersion);
	         if (!gyzjEditState.contains(gyzjNodeState))
	            canEdit = false;
	      }

	     /* if (parentIsCIChild(currNode)) {
	         canEdit = false;
	      }*/
	      //if(currNodeType.isA(aoType)&&!aoEditState.contains(currNodeState)){
	      //  if (currNodeType.getExtType().equals(aoType.getExtType()) && !aoEditState.contains(currNodeState)) {
	      //  canEdit = false;
	      //}
	      if (!aoEditState.contains(aoState)) {
	         canEdit = false;
	      }
//	      if (canCreateType.contains(currNodeType)) {
//	         itemList.add(new CmMBomMNewNodeMenuItem(tree, currNode, owner, canEdit));//新建节点
//	      }

	      //itemList.add(new CmMBomMSearchToTreeMenuItem(tree, currNode, owner, canEdit));//1查找添加
	      //itemList.add(new CmMBomMAOUpdataMenuItem(tree, currNode, owner, canEdit));//2更新组件
//	      itemList.add(new CmChangeQuantityMenuItem(tree, currNode, owner, canEdit));//修改零件数量
	      /*
	       * commented out by Leo@Aug.09,2011
	       * 不再需要有编辑有效性的功能
	       */
	      //itemList.add(new CmMBomMEditEffMenuItem(tree, currNode, owner, canEdit));//3编辑有效性
//	      itemList.add(new CmPastePVNodeMenuItem(tree, currNode, owner, canEdit));//5可视化粘贴
//	      itemList.add(new CmMBomMEditorAttributMenuItem(tree, currNode, owner, canEdit,"ao"));//6属性编辑
//	      itemList.add(new CmPartCheckMenuItem(tree, currNode));//零件核查
//	      itemList.add(new CmShowCIUseMenuItem(tree, currNode, owner));//查看Ci消费
	      itemList.add(new SetPBOMReleasedMenuItem(tree, currNode, owner));//PBOM发布
	      itemList.add(new StartPbomReleasedNoticeWorkflowMenuItem(tree, currNode, owner));//提交PBOM发布通知流程
	      itemList.add(new SetPBOMBatchMenuItem(tree, currNode, owner));//设置PBOM批次号
	      itemList.add(new ReNameMenuItem(tree, currNode, owner));//重命名
	      itemList.add(new CmAddMiddleModelNodeMenuItem(tree, currNode, owner));//添加工艺中间件
	      itemList.add(new CmAddZuHeModelNodeMenuItem(tree, currNode, owner));//添加工艺组合件
	      itemList.add(new CmAddMPModelNodeMenuItem(tree, currNode, owner));//添加毛坯件
	      itemList.add(new SelectPartMenuItem(tree,currNode, owner)); //选取已有零部件
//	      itemList.add(new CmAddAssistModelNodeMenuItem(tree, currNode, owner));//添加工艺辅件
//	      itemList.add(new CmUpdateAssistModelNodeMenuItem(tree, currNode, owner));//修改工艺辅件数量
	      itemList.add(new UpdateUseCountMenuItem(tree, currNode, owner));//修改工艺中间件、工艺组合件及毛坯件的使用数量
	      itemList.add(new UpdateGYUseCountMenuItem(tree, currNode, owner));//修改工艺使用数量
	      itemList.add(new SyUpdateGYUseCountMenuItem(tree, currNode, owner));//同步跟新工艺数量
//	      itemList.add(new CmProcessChangeMenuItem(tree, currNode, canEdit));//添加/修改工艺更改标识
	      itemList.add(new CmEditPBOMPropertyMenuItem(tree, currNode, owner,"1"));//定义零组件生产类型
	      itemList.add(new CmEditPBOMPropertyMenuItem(tree, currNode, owner,"2"));//定义零组件工艺路线
	      itemList.add(new CmEditPBOMKeyComponentMenuItem(tree, currNode, owner,"3"));//定义PBOM关重件标识
	      itemList.add(new CmEditPBOMPropertyMenuItem(tree, currNode, owner,"4"));//修改PBOM属性
//	      itemList.add(new SychronizePartCountMenuItem(tree, currNode, owner));//同步当前部件使用数量
	      //itemList.add(new AddPartItem(tree,currNode, owner)); //添加零部件

	      /*if (!CmCommonNodeUtil.checkNodeIsLeaf(currNode) && !CmCommonNodeUtil.checkNoodIsSavePview(currNode)) {
	    	  itemList.add(new CmPBomPviewMenuItem(tree, currNode, owner));// 保存可视化3D模型
	      }*/
	      itemList.add(new SyUpdateRepMenuItem(tree, currNode, owner));
	      itemList.add(new SyUpdateRepsMenuItem(tree, currNode, owner));

		  itemList.add(new CmMBomMPViewMenuItem(tree,currNode, owner)); //显示可视化-3D
	      itemList.add(new CmMBomMPView2DMenuItem(tree,currNode, owner)); //显示可视化-2D

	      itemList.add(new CmSetVirtualModelNodeMenuItem(tree, currNode, owner));//设置虚拟件
	      itemList.add(new CmUpdateVersionMenuItem(tree, currNode));//修订新版
	      itemList.add(new SetPBOMPhaseCodeMenuItem(tree, currNode, owner));//PBOM转阶段
	      itemList.add(new CmSynchAttributesMenuItem(tree, currNode,owner));//同步更新EBOM属性
	      itemList.add(new CmPackageNodeMenuItem(tree, currNode));//打包
	      itemList.add(new CmUnPackageNodeMenuItem(tree, currNode));//拆包
	      itemList.add(new CmCutNodeMenuItem(tree, currNode, owner));//剪切

	      itemList.add(new CmPasteNodeMenuItem(tree, currNode, owner, canEdit));//4粘贴
	      itemList.add(new CmMBomMDeleteNodeMenuItem(tree, currNode, owner, canEdit));//8删除
	      itemList.add(new CmCancelMenuItem());//取消
	      itemList.add(new SynchOccIdMenuItem(tree, currNode, owner));
	      return itemList.toArray(new CmMenuItem[0]);
	   }

	   private CmTreeNode isGYZJChild(CmTreeNode currNode) {
	      TreeNode[] path = currNode.getPath();
	      for (TreeNode n : path) {
	         CmTreeNode node = (CmTreeNode) n;
//	         if (gyzjType.getExtType().equals(node.getPart().getType())) {
//	            return node;
//	         }

	      }
	      return null;
	   }

	   /**
	    *此节点的路径中有一个是来自于CI则它不可被编辑，
	    *但路径中不包含它自己
	    */
	   private boolean parentIsCIChild(CmTreeNode currNode) {
	      TreeNode[] parents = currNode.getPath();
	      for (TreeNode node : parents) {
	         if (node.getParent() == currNode.getParent()) {
	            continue;
	         }
	         CmTreeNode cmTreeNode = (CmTreeNode) node;
	         List<CmLightPart> pathFromCI = cmTreeNode.getPathFromCI();
	         if ((pathFromCI != null && pathFromCI.size() > 0)) {
	            return true;
	         }
	      }

	      return false;
	   }
}

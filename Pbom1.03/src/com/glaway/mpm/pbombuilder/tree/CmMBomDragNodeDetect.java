package com.glaway.mpm.pbombuilder.tree;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;

import com.glaway.mpm.pbombuilder.data.CmDetect;
import com.glaway.mpm.pbombuilder.jws.CmContext;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmConstants;
import com.glaway.mpm.pbombuilder.util.CmSettings;
import com.glaway.mpm.pbombuilder.util.CmTypeHelper;

/**
 * <br>Created on 2012-12-17
 * @author chenyunlong
 */
public class CmMBomDragNodeDetect implements CmDetect<CmTreeNode> {

	 /**
	    * @param onode
	    * @return
	    */
	   public boolean detectObject(List<?> onodelist) {
	      CmTreeNode onode = (CmTreeNode) onodelist.get(0);
	      if (onode.isRoot() || onode == null || onode.getPart() == null){
	    	  return false;
	      }
	      else if("assistant".equals(onode.getPart().getPartType())){
	    	//工艺辅件
	    	  JOptionPane.showMessageDialog(CmContext.getMainFrame(), "工艺辅件不能挂载任何零件！");
	    	  return false;
	      }
	      else if(CmCommonStringUtil.isHasFilingOfObj(onode)){
	    	//已归档
	    	  JOptionPane.showMessageDialog(CmContext.getMainFrame(), "已归档零件不能挂载新的零件！");
	    	  return false;
	      }
	      CmSettings setting = CmSettings.getSection(CmSettings.SECTION_MBOM);
	      String[] states = setting.get("mbom.ao.canEdit.state.displayName", new String[0]);
	      Vector<String> aoEditState = new Vector<String>();
	      for (String state : states) {
	         aoEditState.add(state);
	      }

	      List<String> gyzjEditState = new Vector<String>();
	      states = setting.get("mbom.gongyizujian.canEdit.state.displayName", new String[0]);
	      for (String state : states) {
	         gyzjEditState.add(state);
	      }

	      TreeNode[] paths = onode.getPath();
	      for (TreeNode treeNode : paths) {
	         CmTreeNode tn = (CmTreeNode) treeNode;
	         CmLightPart part = tn.getPart();
	         CmLightType typeA = CmTypeHelper.getLightType(part.getPartType(), false);
	         CmLightType typeB = CmTypeHelper.getLightType(CmConstants.TYPE_PART_AO, false);//AO
	         String gyzjDisplayType = setting.get("mbom.newNode.type.工艺组件", "");
	         CmLightType typeC = CmTypeHelper.getLightType(gyzjDisplayType, true);//工艺组件
	      }

	      return true;
	   }

	   /**
	    * 检测源节点中可拖动的节点
	    * @param snode
	    * @return
	    */
	   public List<CmTreeNode> detectSource(List<CmTreeNode> snode) {
	      CmSettings settings = CmSettings.getSection(CmSettings.SECTION_MBOM);
	      String[] unPasteTypes = settings.get("mbom.newNode.unPaste.type", new String[0]);
	      List<String> unPastes = new ArrayList<String>();
	      for (String unPasteType : unPasteTypes) {
	         String extType = CmTypeHelper.getExtType(unPasteType);

	         if (extType != null)
	            unPastes.add(extType);
	      }

	      List<CmTreeNode> nodeList = new ArrayList<CmTreeNode>();
	      Iterator<CmTreeNode> iter = snode.iterator();
	      while (iter.hasNext())
	         addToSelectedNodeList(nodeList, iter.next(), unPastes);
	      return nodeList;
	   }

	   /**
	    * 过滤节点类型
	    * @param nodeList
	    * @param node
	    * @param excludeExtTypes
	    */
	   private void addToSelectedNodeList(List<CmTreeNode> nodeList, CmTreeNode node, List<String> excludeExtTypes) {
	      if (node != null && node.getPart() != null) {
	         String extType = node.getPart().getPartType();
	         if (!excludeExtTypes.contains(extType))
	            nodeList.add(node);
	         else {
	            Enumeration en = node.children();
	            while (en.hasMoreElements())
	               addToSelectedNodeList(nodeList, (CmTreeNode) en.nextElement(), excludeExtTypes);
	         }
	      }
	   }

	}

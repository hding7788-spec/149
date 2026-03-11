/**
 * <br>Created on 2011-3-17
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.visual.view.tree;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import com.glaway.mpm.visual.bean.VaLightPart;
import com.glaway.mpm.visual.bean.VaLightType;
import com.glaway.mpm.visual.conf.VaConstants;
import com.glaway.mpm.visual.conf.VaSettings;
import com.glaway.mpm.visual.util.VaTypeHelper;

/**
 * <br>Created on 2011-3-17
 * @author Alex.Huang - ����
 */
public class VaMBomDragNodeDetect implements VaDetect<VaTreeNode> {

   /**
    * @param onode
    * @return
    */
   public boolean detectObject(List<?> onodelist) {
      VaTreeNode onode = (VaTreeNode) onodelist.get(0);
      if (onode.isRoot() || onode == null || onode.getPart() == null)
         return false;

      if (onode.getPathFromCI() != null && onode.getPathFromCI().size() > 0) {
         return false;
      }

      VaSettings setting = VaSettings.getSection(VaSettings.SECTION_MBOM);
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
         VaTreeNode tn = (VaTreeNode) treeNode;
         VaLightPart part = tn.getPart();
         VaLightType typeA = VaTypeHelper.getLightType(part.getType(), false);
         VaLightType typeB = VaTypeHelper.getLightType(VaConstants.TYPE_PART_AO, false);//AO
         String gyzjDisplayType = setting.get("mbom.newNode.type.�������", "");
         VaLightType typeC = VaTypeHelper.getLightType(gyzjDisplayType, true);//�������

         //AO״̬У��
         if (typeB.getExtType().equals(typeA.getExtType())) {
            if (!aoEditState.contains(part.getState())) {
               return false;
            }
         }
         //�������״̬У��
         if (typeC.getExtType().equals(typeA.getExtType())) {
            if (!gyzjEditState.contains(part.getState())) {
               return false;
            }
         }
      }

      return true;
   }

   /**
    * ���Դ�ڵ��п��϶��Ľڵ�
    * @param snode
    * @return
    */
   public List<VaTreeNode> detectSource(List<VaTreeNode> snode) {
      VaSettings settings = VaSettings.getSection(VaSettings.SECTION_MBOM);
      String[] unPasteTypes = settings.get("mbom.newNode.unPaste.type", new String[0]);
      List<String> unPastes = new ArrayList<String>();
      for (String unPasteType : unPasteTypes) {
         String extType = VaTypeHelper.getExtType(unPasteType);

         if (extType != null)
            unPastes.add(extType);
      }

      List<VaTreeNode> nodeList = new ArrayList<VaTreeNode>();
      Iterator<VaTreeNode> iter = snode.iterator();
      while (iter.hasNext())
         addToSelectedNodeList(nodeList, iter.next(), unPastes);
      return nodeList;
   }

   /**
    * ���˽ڵ�����
    * @param nodeList
    * @param node
    * @param excludeExtTypes
    */
   private void addToSelectedNodeList(List<VaTreeNode> nodeList, VaTreeNode node, List<String> excludeExtTypes) {
      if (node != null && node.getPart() != null) {
         String extType = node.getPart().getType();
         if (!excludeExtTypes.contains(extType))
            nodeList.add(node);
         else {
            Enumeration en = node.children();
            while (en.hasMoreElements())
               addToSelectedNodeList(nodeList, (VaTreeNode) en.nextElement(), excludeExtTypes);
         }
      }
   }

}

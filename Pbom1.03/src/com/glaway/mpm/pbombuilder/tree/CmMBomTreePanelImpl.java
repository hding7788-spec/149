/**
 * <br>Created on 2010-11-11
 * @author renchengwei - �γ�ΰ
 */
package com.glaway.mpm.pbombuilder.tree;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmSettings;
import com.glaway.mpm.pbombuilder.util.CmTypeHelper;

/**
 * <br>Created on 2010-11-11
 * @author renchengwei - �γ�ΰ
 */
public class CmMBomTreePanelImpl {
   private StringBuffer errorBuf = null;

   public static CmMBomTreePanelImpl newCmMBomTreePanelImpl() {
      CmMBomTreePanelImpl ret = new CmMBomTreePanelImpl();
      return ret;
   }

   private CmMBomTreePanelImpl() {}

   public String pasteNodesToMBom(CmTree tree, CmTreeNode parentNode, List<CmTreeNode> selectedNodeList) {
      CmSettings settings = CmSettings.getSection(CmSettings.SECTION_MBOM);
      String[] unPasteTypes = settings.get("mbom.newNode.unPaste.type", new String[0]);
      List<String> unPastes = new ArrayList<String>();
      for (String unPasteType : unPasteTypes) {
         String extType = CmTypeHelper.getExtType(unPasteType);
         if (extType != null)
            unPastes.add(extType);
      }

      List<CmTreeNode> nodeList = new ArrayList<CmTreeNode>();
      Iterator<CmTreeNode> iter = selectedNodeList.iterator();
      while (iter.hasNext())
         addToSelectedNodeList(nodeList, iter.next(), unPastes);

      nodeList = filterNodeWithParent(nodeList);
      errorBuf = new StringBuffer(1024);
      for (CmTreeNode node : nodeList) {
         CmTreeNode nodeToAdd = addToChild(tree, parentNode, node);

         if (nodeToAdd != null && nodeToAdd.getChildCount() > 0)
            tree.expandPath(new TreePath(nodeToAdd.getPath()));
         else {
            tree.expandPath(new TreePath(parentNode.getPath()));

         }
      }
      tree.updateUI();
      if (errorBuf.toString().length() > 0) {
         errorBuf.insert(0, "����EBom���ڵ�����ӽڵ㣺\n");
         errorBuf.append("\n��MBOM�����Ѿ�����!\n");
      }
      return errorBuf.toString();
   }

   /**
    * ����CI��LO����
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

   /**
    * ����Ƕ��
    * @param nodeList
    * @return
    */
   private List<CmTreeNode> filterNodeWithParent(List<CmTreeNode> nodeList) {
      List<CmTreeNode> ret = new ArrayList<CmTreeNode>();

      Iterator<CmTreeNode> iter = nodeList.iterator();
      while (iter.hasNext())
         addToList(ret, iter.next());

      return ret;
   }

   private void addToList(List<CmTreeNode> nodeList, CmTreeNode node) {
      TreeNode[] nodePath = node.getPath();

      Iterator<CmTreeNode> iter = nodeList.iterator();
      while (iter.hasNext()) {
         CmTreeNode node0 = iter.next();

         TreeNode[] nodePath0 = node0.getPath();
         if (nodePath.length < nodePath0.length) {
            if (nodePath0[nodePath.length - 1] == node) { // ��������ͬ
               iter.remove();
            }
         } else if (nodePath.length > nodePath0.length) {
            if (nodePath[nodePath0.length - 1] == node0) { // ��������ͬ
               node = null;
               break;
            }
         }
      }

      if (node != null)
         nodeList.add(node);
   }

   private CmTreeNode addToChild(CmTree tree, CmTreeNode parent, CmTreeNode node) {
      CmTreeNode ret = null;

      if (node != null) {
         // node��tree�в��ܴ���
         if (!exists(tree, node)) {
            ret = CmBizObjUtil.convertEBomNode2MBomNode(node,true);
            if (parent != null)
               parent.add(ret);
         } else {
            // ͳ�ƴ��󣬷�����ͻ�
            TreeNode[] path = node.getPath();
            errorBuf.append("EBOM");
            for (int i = 1; i < path.length; i++) {
               CmTreeNode pathNode = (CmTreeNode) path[i];
               errorBuf.append('/').append(pathNode.getPart().getPartNumber());
            }
            errorBuf.append("\n");
         }
      }

      return ret;
   }

   private boolean exists(CmTree tree, CmTreeNode node) {
      boolean ret = false;

      List<String> nodeOccIdList = getAllNodesOccIdList(node);

      Enumeration en = tree.getRoot().breadthFirstEnumeration();
      while (en.hasMoreElements()) {
         CmTreeNode treeNode = (CmTreeNode) en.nextElement();
         if (nodeOccIdList.contains(treeNode.getOccId())) { // ʵ����Ϣ��ͬ������Ϊ���Ѿ�����
            ret = true;
            break;
         }
      }

      return ret;
   }

   private List<String> getAllNodesOccIdList(CmTreeNode node) {
      List<String> ret = new ArrayList<String>();

      Enumeration en = node.breadthFirstEnumeration();
      while (en.hasMoreElements()) {
         CmTreeNode item = (CmTreeNode) en.nextElement();
         ret.add(item.getOccId());
      }

      return ret;
   }
}

package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.jws.CmContext;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmSettings;
import com.glaway.mpm.pbombuilder.util.CmTaskInfo;
import com.glaway.mpm.pbombuilder.util.CmTaskResultSet;
import com.glaway.mpm.pbombuilder.util.CmTypeHelper;
import com.glaway.mpm.pbombuilder.util.PviewTask;

/**
 * 可视粘贴
 * <br>Created on 2012-10-28
 * @author chenyunlong
 */
public class CmPastePVNodeMenuItem extends CmMenuItem {
   private CmTree            tree;
   private CmTreeNode        currNode;
   private Window            owner;
   private StringBuffer      errorBuf;
   private static final long serialVersionUID = -1676220621674073524L;
   private List<CmTreeNode>      selectedPViewNodeList=new ArrayList<CmTreeNode>();
   public CmPastePVNodeMenuItem(CmTree tree, CmTreeNode currNode, Window owner, boolean canEdit){
      this.tree = tree;
      this.currNode = currNode;
      this.owner = owner;
      setText("可视粘贴");
      setIconStr("attribute_edit.gif");
      setEnabled(canEdit?displayValidate(this.currNode):canEdit);
   }
   private boolean displayValidate(CmTreeNode node) {
      CmTaskInfo pastePViewTaskInfo = CmTaskInfo.newCmTaskInfo("CmEBomTreePanel.getSelectedPViewNodeList", this, null);
      try {
         CmTaskResultSet pviewResultSet = PviewTask.sendTask(pastePViewTaskInfo, null);
         selectedPViewNodeList = (List<CmTreeNode>) pviewResultSet.getFirstReturnVal();
      } catch (CmTaskException e) {
         e.printStackTrace();
      }
      if(selectedPViewNodeList.size()==0){
         return false;
      }
      return true;
   }
   /**
    * @param evt
    */
   @Override
   protected void actionPerformed(ActionEvent evt) {
      CmSettings settings = CmSettings.getSection(CmSettings.SECTION_MBOM);
      String[] unPasteTypes = settings.get("mbom.newNode.unPaste.type", new String[0]);
      List<String> unPastes = new ArrayList<String>();
      for (String unPasteType : unPasteTypes) {
         String extType = CmTypeHelper.getExtType(unPasteType);
         if (extType != null)
            unPastes.add(extType);
      }

      List<CmTreeNode> nodeList = new ArrayList<CmTreeNode>();
      Iterator<CmTreeNode> iter = selectedPViewNodeList.iterator();
      while (iter.hasNext())
         addToSelectedNodeList(nodeList, iter.next(), unPastes);

      nodeList = filterNodeWithParent(nodeList);
      errorBuf = new StringBuffer(1024);
      for (CmTreeNode node : nodeList) {
         CmTreeNode nodeToAdd = addToChild(tree, currNode, node);

         if (nodeToAdd != null && nodeToAdd.getChildCount() > 0)
            tree.expandPath(new TreePath(nodeToAdd.getPath()));
         else {
            tree.expandPath(new TreePath(currNode.getPath()));

         }
      }
      CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
      tree.updateUI();
      if (errorBuf.toString().length() > 0) {
         errorBuf.insert(0, "����EBom���ڵ�����ӽڵ㣺\n");
         errorBuf.append("\n�Ѿ�����!\n");
      }

      if (errorBuf.toString().length() > 0) {
         JOptionPane.showMessageDialog(CmContext.getMainFrame(), errorBuf.toString());
      }
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

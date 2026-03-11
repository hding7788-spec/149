/**
 * <br>Created on 2011-3-17
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.visual.view.tree;

import java.awt.Window;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.VaContext;

/**
 * <br>Created on 2011-3-17
 * @author Alex.Huang - ����
 */
public class VaDefaultDragNodeMerger extends VaTreeNodeMerger<VaTreeNode> {
	private StringBuffer errorBuf = null;
	   private VaLogger log=VaLogger.getLogger();
	   public VaDefaultDragNodeMerger(VaTree tree, VaTreeNode parentNode, Window owner) {
	      super(tree, parentNode);
	   }
	   @Override
	   public void doMerger(List<VaTreeNode> nodes) {
	      nodes = filterNodeWithParent(nodes);
	      errorBuf = new StringBuffer(1024);
	      for (VaTreeNode node : nodes) {
	         log.debug(node+" 路径:"+node.getPathFromCI());
	         VaTreeNode nodeToAdd = addToChild(tree, parent, node);

	         if (nodeToAdd != null && nodeToAdd.getChildCount() > 0)
	            tree.expandPath(new TreePath(nodeToAdd.getPath()));
	         else {
	            tree.expandPath(new TreePath(parent.getPath()));

	         }
	      }
	      tree.updateUI();
	      if (errorBuf.toString().length() > 0) {
	         errorBuf.insert(0, "下列EBom树节点或其子节点：\n");
	         errorBuf.append("\n已经存在!\n");
	      }
	      if (errorBuf.length() > 0) {
	         JOptionPane.showMessageDialog(VaContext.getMainFrame(), errorBuf);
	      }
	   }

	   /**
	    * 避免嵌套
	    * @param nodeList
	    * @return
	    */
	   private List<VaTreeNode> filterNodeWithParent(List<VaTreeNode> nodeList) {
	      List<VaTreeNode> ret = new ArrayList<VaTreeNode>();

	      Iterator<VaTreeNode> iter = nodeList.iterator();
	      while (iter.hasNext())
	         addToList(ret, iter.next());

	      return ret;
	   }

	   private void addToList(List<VaTreeNode> nodeList, VaTreeNode node) {
	      TreeNode[] nodePath = node.getPath();

	      Iterator<VaTreeNode> iter = nodeList.iterator();
	      while (iter.hasNext()) {
	         VaTreeNode node0 = iter.next();

	         TreeNode[] nodePath0 = node0.getPath();
	         if (nodePath.length < nodePath0.length) {
	            if (nodePath0[nodePath.length - 1] == node) { // 物理上相同
	               iter.remove();
	            }
	         } else if (nodePath.length > nodePath0.length) {
	            if (nodePath[nodePath0.length - 1] == node0) { // 物理上相同
	               node = null;
	               break;
	            }
	         }
	      }

	      if (node != null)
	         nodeList.add(node);
	   }

	   private VaTreeNode addToChild(VaTree tree, VaTreeNode parent, VaTreeNode node) {
	      VaTreeNode ret = null;

	      if (node != null) {
	         // node在tree中不能存在
	         if (!exists(tree, node)) {
//	            ret = CmBizObjUtil.convertEBomNode2MBomNode(node);
	            if (parent != null)
	               parent.add(ret);
	         } else {
	            // 统计错误，反馈给客户
	            TreeNode[] path = node.getPath();
	            errorBuf.append("EBOM");
	            for (int i = 1; i < path.length; i++) {
	               VaTreeNode pathNode = (VaTreeNode) path[i];
	               errorBuf.append('/').append(pathNode.getPart().getNumber());
	            }
	            errorBuf.append("\n");
	         }
	      }

	      return ret;
	   }

	   private boolean exists(VaTree tree, VaTreeNode node) {
	      boolean ret = false;

	      List<String> nodeOccIdList = getAllNodesOccIdList(node);

	      Enumeration en = tree.getRoot().breadthFirstEnumeration();
	      while (en.hasMoreElements()) {
	         VaTreeNode treeNode = (VaTreeNode) en.nextElement();
	         if (nodeOccIdList.contains(treeNode.getOccId())) { // 实例信息相同，则认为是已经存在
	            ret = true;
	            break;
	         }
	      }

	      return ret;
	   }

	   private List<String> getAllNodesOccIdList(VaTreeNode node) {
	      List<String> ret = new ArrayList<String>();

	      Enumeration en = node.breadthFirstEnumeration();
	      while (en.hasMoreElements()) {
	         VaTreeNode item = (VaTreeNode) en.nextElement();
	         ret.add(item.getOccId());
	      }

	      return ret;
	   }

	}

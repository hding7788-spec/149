package com.glaway.mpm.visual.view.tree;

import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.tree.TreeNode;

import wt.part.WTPart;

import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.visual.bean.VaLightPart;
import com.glaway.mpm.visual.bean.VaPartWithOcc;
import com.glaway.mpm.visual.biz.VaBizObjUtil;
import com.glaway.mpm.visual.control.VaPartStructureUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.util.VaSearchHelper;

/**
 * <br>Created on 2011-3-18
 * @author Alex.Huang - ����
 */
public class VaPartMerger extends VaTreeNodeMerger<VaLightPart> {
	   private Window   dialogOwner;
	   private VaLogger log = VaLogger.getLogger();

	   /**
	    * @param tree
	    * @param parentNode
	    */
	   public VaPartMerger(VaTree tree, VaTreeNode parentNode, Window owner) {
	      super(tree, parentNode);
	      dialogOwner = owner;
	   }

	   /**
	    * @param part
	    */
	   @SuppressWarnings("unchecked")
	   @Override
	   public void doMerger(List<VaLightPart> parts) {
		  log.debug("merger tree");
//	      parts = filterNotInTreeParts(getTree(), parts);
	      if (parts.size() == 0) {
	         JOptionPane.showMessageDialog(dialogOwner, "part size is 0!");
	         return;
	      }
	      VaTreeNode root = getTree().getRoot();
	      log.debug("part"+ root);
	      for (VaLightPart lightPart : parts) {
	         VaPartWithOcc nodeChild = null;
	         try {
	            nodeChild = buildStructure(lightPart.getOid());
	         } catch (Exception e) {
	            e.printStackTrace();
	         }
	         VaTreeNode treeNodeChild = VaBizObjUtil.buildTree(nodeChild);
	         root.add(treeNodeChild);
	      }
	      //记录EBom中节点的路径
	      Enumeration<VaTreeNode> children = root.breadthFirstEnumeration();
	      log.debug(root + "-子节点:" + root.getChildCount());
	      while (children.hasMoreElements()) {
	         VaTreeNode child = children.nextElement();

	         TreeNode[] path = child.getPath();
	         List<VaLightPart> lightPartPaths = new Vector<VaLightPart>();
	         for (int i = 0; i < path.length; i++) {
	            VaLightPart lightPart = ((VaTreeNode) path[i]).getPart();
	            if (lightPart.equals(VaLightPart.EMPTY_PART)) {
	               lightPart = null;
	            }
	            lightPartPaths.add(lightPart);
	         }
	         log.debug(child + "-路径:" + lightPartPaths);
	         child.setPathFromCI(lightPartPaths);
	      }
	      //end;
//	      SwingUtil.expandAll(getTree());
	      getTree().updateUI();
	   }

	   private VaPartWithOcc buildStructure(long parentOid) throws RemoteException, InvocationTargetException,Exception {
	      WTPart parent = (WTPart) VaSearchHelper.search(WTPart.class, parentOid);

	      return VaPartStructureUtil.buildStructure(parent);
	   }

	   private List<VaLightPart> filterNotInTreeParts(VaTree tree, List<VaLightPart> lightParts) {
	      Vector<VaLightPart> vecRet = new Vector<VaLightPart>();

	      if (lightParts != null && lightParts.size() > 0) {
	         Set<VaLightPart> existLightParts = getRootCIfromEBomTree(tree);
	         for (VaLightPart lightPart : lightParts) {
	            if (!existLightParts.contains(lightPart))
	               vecRet.add(lightPart);
	         }
	      }

	      return vecRet;
	   }

	   @SuppressWarnings("unchecked")
	   private Set<VaLightPart> getRootCIfromEBomTree(VaTree tree) {
	      Set<VaLightPart> ret = new HashSet<VaLightPart>();

	      if (tree != null && tree.getRoot() != null) {
	         Enumeration<VaTreeNode> children = tree.getRoot().children();
	         while (children.hasMoreElements()) {
	            VaTreeNode node = children.nextElement();
	            ret.add(node.getPart());
	         }
	      }

	      return ret;
	   }

	}

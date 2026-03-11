package com.glaway.mpm.pbombuilder.bom;

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
import com.glaway.mpm.pbombuilder.data.CmPartWithOcc;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.CmTreeNodeMerger;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmPartStructureUtil;
import com.glaway.mpm.pbombuilder.util.CmSearchHelper;

/**
 * <br>Created on 2013-6-18
 * @author chenyunlong
 */
public class CmCIMerger extends CmTreeNodeMerger<CmLightPart> {
	   private Window   dialogOwner;
	   private CmLogger log = CmLogger.getLogger();

	   /**
	    * @param tree
	    * @param parentNode
	    */
	   public CmCIMerger(CmTree tree, CmTreeNode parentNode, Window owner) {
	      super(tree, parentNode);
	      dialogOwner = owner;
	   }

	   /**
	    * @param part
	    */
	   @SuppressWarnings("unchecked")
	   @Override
	   public void doMerger(List<CmLightPart> parts) {
//	      parts = filterNotInTreeParts(getTree(), parts);
	      if (parts.size() == 0) {
	         JOptionPane.showMessageDialog(dialogOwner, "没有新的节点需要添加到EBOM树上!");
	         return;
	      }
	      CmTreeNode root = getTree().getRoot();
	      for (CmLightPart lightPart : parts) {
	         CmPartWithOcc nodeChild = null;
	         try {
	            nodeChild = buildStructure(lightPart.getOid());
	         } catch (Exception e) {
	            e.printStackTrace();
	         }
	         CmTreeNode treeNodeChild = CmBizObjUtil.buildTree(nodeChild);
	         root.add(treeNodeChild);
	      }
	      //记录EBom中节点的路径
	      Enumeration<CmTreeNode> children = root.breadthFirstEnumeration();
	      log.debug(root + "-子节点:" + root.getChildCount());
	      while (children.hasMoreElements()) {
	         CmTreeNode child = children.nextElement();

	         TreeNode[] path = child.getPath();
	         List<CmLightPart> lightPartPaths = new Vector<CmLightPart>();
	         for (int i = 0; i < path.length; i++) {
	            CmLightPart lightPart = ((CmTreeNode) path[i]).getPart();
	            if (lightPart.equals(CmLightPart.EMPTY_PART)) {
	               lightPart = null;
	            }
	            lightPartPaths.add(lightPart);
	         }
	         //log.debug(child + "-路径:" + lightPartPaths);
	         child.setPathFromCI(lightPartPaths);
	      }
	      //end;
	      getTree().updateUI();
	   }

	   private CmPartWithOcc buildStructure(long parentOid) throws RemoteException, InvocationTargetException {
	      WTPart parent = null;
		try {
			parent = (WTPart) CmSearchHelper.search(WTPart.class, parentOid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	      return CmPartStructureUtil.buildStructure(parent);
	   }

	   private List<CmLightPart> filterNotInTreeParts(CmTree tree, List<CmLightPart> lightParts) {
	      Vector<CmLightPart> vecRet = new Vector<CmLightPart>();

	      if (lightParts != null && lightParts.size() > 0) {
	         Set<CmLightPart> existLightParts = getRootCIfromEBomTree(tree);
	         for (CmLightPart lightPart : lightParts) {
	            if (!existLightParts.contains(lightPart))
	               vecRet.add(lightPart);
	         }
	      }
	      return vecRet;
	   }

	   @SuppressWarnings("unchecked")
	   private Set<CmLightPart> getRootCIfromEBomTree(CmTree tree) {
	      Set<CmLightPart> ret = new HashSet<CmLightPart>();

	      if (tree != null && tree.getRoot() != null) {
	         Enumeration<CmTreeNode> children = tree.getRoot().children();
	         while (children.hasMoreElements()) {
	            CmTreeNode node = children.nextElement();
	            ret.add(node.getPart());
	         }
	      }

	      return ret;
	   }

	}

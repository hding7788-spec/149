/**
 * <br>Created on 2010-11-2
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.visual.view.tree.menu;

import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.util.WTException;

import com.glaway.mpm.visual.bean.VaEPartMaster;
import com.glaway.mpm.visual.bean.VaLightPart;
import com.glaway.mpm.visual.biz.VaBizObjUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.util.VaSearchHelper;
import com.glaway.mpm.visual.view.tree.VaTreeNode;

/**
 * <br>Created on 2010-11-2
 * @author Alex.Huang
 */
public class VaEBomNodePathTreeImpl {
   private static final VaLogger log = VaLogger.getLogger(VaEBomNodePathTreeImpl.class);

   public static VaEBomNodePathTreeImpl newVaEBomNodePathTreeImpl() {
      VaEBomNodePathTreeImpl ret = new VaEBomNodePathTreeImpl();
      return ret;
   }

   private VaEBomNodePathTreeImpl() {}

   public VaTreeNode getRootNode(VaTreeNode node) {
      WTPart curPart = null ;
	VaTreeNode curNode = null;
	try {
		VaLightPart lightPart = node.getPart();
		  curPart = (WTPart) VaSearchHelper.search(WTPart.class, lightPart.getOid());

		  curNode = new VaTreeNode(new VaEPartMaster(VaBizObjUtil.buildVaLightPartFromWTPart(curPart)));
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

      return buildStructure(curNode, curPart);
   }

   private VaTreeNode buildStructure(VaTreeNode childNode, WTPart childPart) {
      QueryResult qr = null;
      try {
         qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster) childPart.getMaster());
      } catch (WTException e) {
         log.error(e);
      }

      while (qr != null && qr.hasMoreElements()) {
         WTPart parent = (WTPart) qr.nextElement();
         VaTreeNode curNode = new VaTreeNode(new VaEPartMaster(VaBizObjUtil.buildVaLightPartFromWTPart(parent)));
         childNode.add(curNode);
         
         buildStructure(curNode, parent);
      }
      return childNode;
   }
   

   public  void expandAll(JTree tree, TreePath parent, boolean expand) { 
      VaTreeNode node = (VaTreeNode) parent.getLastPathComponent(); 
      if (node.getChildCount() >= 0) { 
          for (Enumeration e = node.children(); e.hasMoreElements(); ) { 
             VaTreeNode n = (VaTreeNode) e.nextElement(); 
              TreePath path = parent.pathByAddingChild(n); 
              expandAll(tree, path, expand); 
          } 
      } 

      if (expand) { 
          tree.expandPath(parent); 
      } else { 
          tree.collapsePath(parent); 
      } 
  } 

}

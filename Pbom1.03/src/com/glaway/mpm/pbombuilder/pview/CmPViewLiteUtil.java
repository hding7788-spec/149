package com.glaway.mpm.pbombuilder.pview;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.vecmath.Matrix4d;

import wt.part.WTPart;

import com.glaway.mpm.pbombuilder.data.CmPartUsesOcc;
import com.glaway.mpm.pbombuilder.data.CmPartWithOcc;
import com.glaway.mpm.pbombuilder.impl.CmPViewNode;
import com.glaway.mpm.pbombuilder.jws.CmContext;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.CmPViewLiteDialog;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmPartStructureUtil;
import com.ptc.pview.pvkapp.ComponentNode;

public class CmPViewLiteUtil {
   private static final CmLogger                 logger = CmLogger.getLogger();
   private static Map<String, CmPViewLiteDialog> cache  = new HashMap<String, CmPViewLiteDialog>();

   public synchronized static CmPViewLiteDialog getPViewLiteDialog(String name) {
	   
      if (name == null)
         name = "default";
      else
         name = name.toLowerCase();

      CmPViewLiteDialog ret = cache.get(name);
      if (ret == null) {
         ret = new CmPViewLiteDialog(CmContext.getMainFrame(), name);
         cache.put(name, ret);
      }
      logger.debug(cache);
      return ret;
   }

   public static CmPViewNode buildPViewStructureTree(WTPart root) {
      CmPViewNode ret = null;

      if (root != null) {
         try {
            CmPartWithOcc partOccRoot = CmPartStructureUtil.buildStructure(root);
            CmTreeNode treeNodeRoot = CmBizObjUtil.buildTree(partOccRoot);

            ret = convertTreeNode2PViewNodeTree(treeNodeRoot);
         } catch (RemoteException e) {
            logger.error(e);
         } catch (InvocationTargetException e) {
            logger.error(e);
         }
      }

      return ret;
   }

   @SuppressWarnings("unchecked")
   public static CmPViewNode convertTreeNode2PViewNodeTree(CmTreeNode node) {
      CmPViewNode ret = new CmDefaultPViewNode(node);

      if (!node.isLeaf()) {
         Enumeration<CmTreeNode> children = (Enumeration<CmTreeNode>) node.children();
         while (children.hasMoreElements()) {
            CmTreeNode child = children.nextElement();
            ret.addChild(convertTreeNode2PViewNodeTree(child));
         }
      }

      return ret;
   }

   public static CmPViewNode createDummyPViewNode(String id) {
      return new CmDefaultPViewNode(id);
   }
}

class CmDefaultPViewNode implements CmPViewNode {
   private ComponentNode       componentNode;
   private Matrix4d            matrix4d;
   private String              id;
   private long                partOid;

   private Vector<CmPViewNode> children;

   public CmDefaultPViewNode(CmTreeNode node) {
      this.componentNode = null;
      this.matrix4d = node.getMatrix();
      this.id = node.getOccId();
      this.partOid = node.getPart() == null ? 0 : node.getPart().getOid();
      this.children = new Vector<CmPViewNode>();
   }

   public CmDefaultPViewNode(String id) {
      this.componentNode = null;
      this.matrix4d = CmPartUsesOcc.STD_MATRIX4D;
      this.id = id;
      this.partOid = 0;
      this.children = new Vector<CmPViewNode>();
   }

   public Enumeration<CmPViewNode> children() {
      return this.children.elements();
   }

   public void addChild(CmPViewNode child) {
      if (child != null)
         this.children.add(child);
   }

   public String getId() {
      return id;
   }

   public Matrix4d getMatrix() {
      return matrix4d;
   }

   public long getPartOid() {
      return partOid;
   }

   public boolean isLeaf() {
      return children.isEmpty();
   }

   public ComponentNode getComponentNode() {
      return componentNode;
   }

   public void setComponentNode(ComponentNode componentNode) {
      this.componentNode = componentNode;
   }

   public String toString() {
      return "CmDefaultPViewNode [id=" + id + ", partOid=" + partOid + "]";
   }
}

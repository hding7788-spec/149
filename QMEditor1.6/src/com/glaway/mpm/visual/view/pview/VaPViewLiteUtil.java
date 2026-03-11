package com.glaway.mpm.visual.view.pview;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.vecmath.Matrix4d;

import wt.part.WTPart;

import com.glaway.mpm.visual.bean.VaPartUsesOcc;
import com.glaway.mpm.visual.bean.VaPartWithOcc;
import com.glaway.mpm.visual.biz.VaBizObjUtil;
import com.glaway.mpm.visual.control.VaPartStructureUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.VaContext;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.ptc.pview.pvkapp.ComponentNode;

public class VaPViewLiteUtil {
   private static final VaLogger                 logger = VaLogger.getLogger();
   private static Map<String, VaPViewLiteDialog> cache  = new HashMap<String, VaPViewLiteDialog>();

   public synchronized static VaPViewLiteDialog getPViewLiteDialog(String name) {
      if (name == null)
         name = "default";
      else
         name = name.toLowerCase();

      VaPViewLiteDialog ret = cache.get(name);
      if (ret == null) {
         ret = new VaPViewLiteDialog(VaContext.getMainFrame(), name);
         cache.put(name, ret);
      }
      return ret;
   }

   public static VaPViewNode buildPViewStructureTree(WTPart root) {
      VaPViewNode ret = null;

      if (root != null) {
         try {
            VaPartWithOcc partOccRoot = VaPartStructureUtil.buildStructure(root);
            VaTreeNode treeNodeRoot = VaBizObjUtil.buildTree(partOccRoot);

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
   public static VaPViewNode convertTreeNode2PViewNodeTree(VaTreeNode node) {
      VaPViewNode ret = new VaDefaultPViewNode(node);

      if (!node.isLeaf()) {
         Enumeration<VaTreeNode> children = (Enumeration<VaTreeNode>) node.children();
         while (children.hasMoreElements()) {
            VaTreeNode child = children.nextElement();
            ret.addChild(convertTreeNode2PViewNodeTree(child));
         }
      }

      return ret;
   }

   public static VaPViewNode createDummyPViewNode(String id) {
      return new VaDefaultPViewNode(id);
   }
}

class VaDefaultPViewNode implements VaPViewNode {
   private ComponentNode       componentNode;
   private Matrix4d            matrix4d;
   private String              id;
   private long                partOid;

   private Vector<VaPViewNode> children;

   public VaDefaultPViewNode(VaTreeNode node) {
      this.componentNode = null;
      this.matrix4d = node.getMatrix();
      this.id = node.getOccId();
      this.partOid = node.getPart() == null ? 0 : node.getPart().getOid();
      this.children = new Vector<VaPViewNode>();
   }

   public VaDefaultPViewNode(String id) {
      this.componentNode = null;
      this.matrix4d = VaPartUsesOcc.STD_MATRIX4D;
      this.id = id;
      this.partOid = 0;
      this.children = new Vector<VaPViewNode>();
   }

   public Enumeration<VaPViewNode> children() {
      return this.children.elements();
   }

   public void addChild(VaPViewNode child) {
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
      return "VaDefaultPViewNode [id=" + id + ", partOid=" + partOid + "]";
   }
}

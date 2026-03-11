package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Map;

import wt.fc.PersistenceHelper;
import wt.part.WTPart;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;

import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.data.CmTreeNodeAttributProxy;
import com.glaway.mpm.pbombuilder.data.CmXmlDataProxy;
import com.glaway.mpm.pbombuilder.panel.CmMPartMaster;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmPartAttributNode;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;

/**
 * <br>Created on 2012-10-29
 * @author chenyunlong
 *以发布的工艺组件 升版 菜单操作
 */
public class CmGYZJNewVersionMenuItem extends CmMenuItem {
   private CmTree     tree;
   private CmTreeNode node;

   public CmGYZJNewVersionMenuItem(CmTree tree, CmTreeNode currNode) {
      this.tree = tree;
      this.node = currNode;
      setText("工艺组件升版");
      setEnabled(enabled(currNode));
   }

   private boolean enabled(CmTreeNode currNode) {
      String gyzjNodeState = currNode.getPart().getState();
      if ("已发布".equals(gyzjNodeState)) {
         return true;
      }
      return false;
   }

   @Override
   protected void actionPerformed(ActionEvent evt) {
      CmLightPart lightPart = node.getPart();
      WTPart part = CmBizObjUtil.getWTPartFromLightPart(lightPart);
      try {
         part = (WTPart) newVersion(part, true);
      } catch (Exception e) {
         e.printStackTrace();
      }
      CmLightPart newLightPart = CmBizObjUtil.buildCmLightPartFromWTPart(part);
      CmMPartMaster master = new CmMPartMaster(newLightPart);
      node.setUserObject(master);
      tree.updateUI();
   }

   private void updateAttribute(CmTreeNode node){
      CmXmlDataProxy xmlProxy = CmXmlDataProxy.getCmXmlDataProxy();
      CmTreeNodeAttributProxy attributeProxy=(CmTreeNodeAttributProxy)xmlProxy.getProxy(CmXmlDataProxy.MBOM_ATTRIBUTE_PROXY);
      Map<String, CmPartAttributNode> attributes = attributeProxy.getAttributs();
      
      String partOid = String.valueOf(node.getPart().getOid());
      for (String key : attributes.keySet()) {
         CmPartAttributNode attNode = attributes.get(key);
         Enumeration<CmPartAttributNode> children = attNode.breadthFirstEnumeration();
         
      }
   }
   private  Versioned newVersion(Versioned v, boolean save) throws Exception {
      v = VersionControlHelper.service.newVersion(v);
      if (save) {
         v = (Versioned) PersistenceHelper.manager.save(v);
      }
      return v;
   }
}

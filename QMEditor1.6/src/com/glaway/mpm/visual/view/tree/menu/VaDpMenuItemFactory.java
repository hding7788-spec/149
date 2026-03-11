package com.glaway.mpm.visual.view.tree.menu;

import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;

import java.awt.*;
import java.util.List;
import java.util.Vector;
/**
 * <br>Created on 2012-10-28
 * @author chenyunlong
 */
public class VaDpMenuItemFactory implements VaMenuItemFactory {
   private boolean isEdit=true;
   public VaDpMenuItemFactory(boolean b){
      this.isEdit=b;
   }
   public VaMenuItem[] createMenuItem(VaTree tree, VaTreeNode currNode, Window owner) {
      List<VaMenuItem> list = new Vector<VaMenuItem>();
//      VaEBomShowPathMenuItem showPath = new VaEBomShowPathMenuItem(currNode, owner);
//      VaEBomCopyNodeMenuItem copyNode = new VaEBomCopyNodeMenuItem(tree);
      VaBomPasteNodeMenuItem pasteNode = new VaBomPasteNodeMenuItem(tree);
      VaBomCancelNodeMenuItem cancelNode = new VaBomCancelNodeMenuItem(tree);
//      VaEBomPackupNodeMenuItem packupNodeMenuItem = new VaEBomPackupNodeMenuItem(tree);
//      VaEBomUnpackNodeMenuItem unpackNodeMenuItem = new VaEBomUnpackNodeMenuItem(tree);
//      VaEBomClearFittingsMenuItem clearFitting = new VaEBomClearFittingsMenuItem(tree);
//      list.add(showPath);
//      if(isEdit)
//      list.add(copyNode);
      list.add(pasteNode);
      list.add(cancelNode);
//      list.add(packupNodeMenuItem);
//      list.add(unpackNodeMenuItem);
//      list.add(clearFitting);
//      String[] types = {"DCI", "SCI", "CI"};
//      if (currNode != null && currNode.getPart() != null) {
//         CmLightPart lightPart = currNode.getPart();
//         for (String key : types) {
//            CmLightType lightType = CmTypeHelper.getLightType(key, true);
//            if (lightType != null && null != lightType.getExtType() && lightType.getExtType().equals(lightPart.getType())) {
//               list.add(new CmDeleteCINodeMenuItem(tree, currNode, owner));
//            }
//
//         }
//      }
//      list.add(new VaCancelMenuItem());
      return list.toArray(new VaMenuItem[0]);
   }

}

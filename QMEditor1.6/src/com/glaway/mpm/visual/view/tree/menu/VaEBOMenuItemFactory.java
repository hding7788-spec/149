package com.glaway.mpm.visual.view.tree.menu;

import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;

import java.awt.*;
import java.util.List;
import java.util.Vector;

/**
 * <br>
 * Created on 2012-10-28
 *
 * @author chenyunlong
 */
public class VaEBOMenuItemFactory implements VaMenuItemFactory {
    private boolean isEdit = true;

    public VaEBOMenuItemFactory(boolean b) {
        this.isEdit = b;
    }

    public VaMenuItem[] createMenuItem(VaTree tree, VaTreeNode currNode, Window owner) {
        List<VaMenuItem> list = new Vector<VaMenuItem>();
        // VaEBomShowPathMenuItem showPath = new
        // VaEBomShowPathMenuItem(currNode, owner);
        VaEBomCopyNodeMenuItem copyNode = new VaEBomCopyNodeMenuItem(tree);
        //装
        VaEomZhuangCopyNodeMenuItem Zcopy = new VaEomZhuangCopyNodeMenuItem(tree);
        //拆
        VaEomChaiCopyNodeMenuItem Ccopy = new VaEomChaiCopyNodeMenuItem(tree);
        //打包
//        VaEBomPackupNodeMenuItem packupNodeMenuItem = new VaEBomPackupNodeMenuItem(tree);
        //拆包
//        VaEBomUnpackNodeMenuItem unpackNodeMenuItem = new VaEBomUnpackNodeMenuItem(tree);

        // VaEBomClearFittingsMenuItem clearFitting = new
        // VaEBomClearFittingsMenuItem(tree);
        // VaEBomBBoxSearchNodeMenuItem bboxSearch = new
        // VaEBomBBoxSearchNodeMenuItem(tree);
        VaBomMPView2DMenuItem show2dpview = new VaBomMPView2DMenuItem(tree, currNode, owner);
        //取消批量选择按钮
//		VaEBomSelectNodeByNumberMenuItem nodesSelect = new VaEBomSelectNodeByNumberMenuItem(tree);
        VaTreeNode node = tree.getSelectedNode();
        // list.add(showPath);
        // if(isEdit)
//		list.add(copyNode);
        list.add(Zcopy);
        list.add(Ccopy);
//        list.add(packupNodeMenuItem);
//        list.add(unpackNodeMenuItem);
        list.add(show2dpview);
//		list.add(nodesSelect);
        // list.add(bboxSearch);
        // list.add(clearFitting);
        // String[] types = {"DCI", "SCI", "CI"};
        // if (currNode != null && currNode.getPart() != null) {
        // CmLightPart lightPart = currNode.getPart();
        // for (String key : types) {
        // CmLightType lightType = CmTypeHelper.getLightType(key, true);
        // if (lightType != null && null != lightType.getExtType() &&
        // lightType.getExtType().equals(lightPart.getType())) {
        // list.add(new CmDeleteCINodeMenuItem(tree, currNode, owner));
        // }
        //
        // }
        // }
        // list.add(new VaCancelMenuItem());
        return list.toArray(new VaMenuItem[0]);
    }

}

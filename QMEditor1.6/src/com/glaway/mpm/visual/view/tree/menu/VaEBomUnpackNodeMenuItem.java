package com.glaway.mpm.visual.view.tree.menu;

import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpPaceNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpPartNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpStepNode;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.visual.bean.VaEPartInstance;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.ActionEvent;
import java.util.Map;

public class VaEBomUnpackNodeMenuItem extends VaMenuItem {

    private VaTree tree;

    public VaEBomUnpackNodeMenuItem(VaTree tree) {
        this.tree = tree;
        setText("拆包");
//        setIconStr("copy.gif");

    }

    @Override
    protected void actionPerformed(ActionEvent evt) {
        TreePath[] paths = tree.getSelectionPaths();
        if (paths.length > 1 || paths.length == 0) {
            JOptionPane.showMessageDialog(null, "请选择单个节点进行拆包！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else {
            VaTreeNode node = (VaTreeNode) paths[0].getLastPathComponent();
            //左下角工艺树拆包
            if (node instanceof DpPartNode) {
                DpPartNode dpPartNode = (DpPartNode) node;
                VaTreeNode parentNode = (VaTreeNode) dpPartNode.getParent();
                DpPartNode unpackedNode;
                Map<String, Integer> map = VaTree.getNodeUsedMap();
                dpPartNode.getPart().setAmount(1);
                parentNode.remove(dpPartNode);
                String[] occIds = dpPartNode.getOccId().split(",");
//                String[] occPaths = dpPartNode.getOccpath().split(",");
                for (int i = 0; i < occIds.length; i++) {
                    unpackedNode = new DpPartNode(new VaEPartInstance(dpPartNode.getPart()));
                    unpackedNode.setOccId(occIds[i]);
//                    unpackedNode.setOccpath(occPaths[i]);
                    unpackedNode.setDataType(dpPartNode.getDataType());
                    unpackedNode.setBzh(dpPartNode.getBzh());
                    unpackedNode.setJxxndjhyd(dpPartNode.getJxxndjhyd());
                    unpackedNode.setBmcl(dpPartNode.getBmcl());
                    unpackedNode.setRcl(dpPartNode.getRcl());
                    unpackedNode.setCpxs(dpPartNode.getCpxs());
                    unpackedNode.setCpdj(dpPartNode.getCpdj());
                    unpackedNode.setBnxs(dpPartNode.getBnxs());
                    unpackedNode.setSfjk(dpPartNode.getSfjk());
                    unpackedNode.setComment(dpPartNode.getComment());
                    unpackedNode.setXhph(dpPartNode.getXhph());
                    unpackedNode.setGg(dpPartNode.getGg());
                    unpackedNode.setDw2(dpPartNode.getDw2());
                    unpackedNode.setDw(dpPartNode.getDw());
                    unpackedNode.setFlag(dpPartNode.getFlag());
                    unpackedNode.setZcmark((dpPartNode).getZcmark());
                    if (map.containsKey(unpackedNode.getOccId())) {
                        Integer cishu = map.get(unpackedNode.getOccId());
                        if (cishu == 0) {
                            unpackedNode.setUsed(true);
                        } else {
                            unpackedNode.setUsed(false);
                        }
                    } else {
                        unpackedNode.setUsed(false);
                    }
                    parentNode.add(unpackedNode);
                }
                VaTree.sortVatree(parentNode);
                tree.updateUI();
            } else {
                //右上角FPBOM树拆包
                VaTreeNode parentNode = (VaTreeNode) node.getParent();
                VaTreeNode unpackedNode;
                Map<String, Integer> map = VaTree.getNodeUsedMap();
                node.getPart().setAmount(1);
                node.removeFromParent();
                tree.updateUI();
                String[] occIds = node.getOccId().split(",");
                String[] occPaths = node.getOccpath().split(",");
                for (int i = 0; i < occIds.length; i++) {
                    unpackedNode = new VaTreeNode(new VaEPartInstance(node.getPart()));
                    unpackedNode.setOccId(occIds[i]);
                    unpackedNode.setOccpath(occPaths[i]);
                    unpackedNode.setDataType(node.getDataType());
                    unpackedNode.setBzh(node.getBzh());
                    unpackedNode.setJxxndjhyd(node.getJxxndjhyd());
                    unpackedNode.setBmcl(node.getBmcl());
                    unpackedNode.setRcl(node.getRcl());
                    unpackedNode.setCpxs(node.getCpxs());
                    unpackedNode.setCpdj(node.getCpdj());
                    unpackedNode.setBnxs(node.getBnxs());
                    unpackedNode.setSfjk(node.getSfjk());
                    unpackedNode.setComment(node.getComment());
                    unpackedNode.setXhph(node.getXhph());
                    unpackedNode.setGg(node.getGg());
                    unpackedNode.setDw2(node.getDw2());
                    unpackedNode.setDw(node.getDw());
                    unpackedNode.setFlag(node.getFlag());
                    if (map.containsKey(unpackedNode.getOccId())) {
                        Integer cishu = map.get(unpackedNode.getOccId());
                        if (cishu == 0) {
                            unpackedNode.setUsed(true);
                        } else {
                            unpackedNode.setUsed(false);
                        }
                    } else {
                        unpackedNode.setUsed(false);
                    }
                    parentNode.add(unpackedNode);
                    tree.updateUI();
                }
                VaTree.sortVatree(parentNode);
                tree.updateUI();
                SwingUtil.expandAll(tree);
            }
//            tree.updateUI();
        }
    }
}

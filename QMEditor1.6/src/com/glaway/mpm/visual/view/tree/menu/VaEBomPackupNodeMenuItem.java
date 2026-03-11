package com.glaway.mpm.visual.view.tree.menu;

import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpPartNode;
import com.glaway.mpm.visual.bean.VaEPartInstance;
import com.glaway.mpm.visual.bean.VaLightPart;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class VaEBomPackupNodeMenuItem extends VaMenuItem {

    private VaTree tree;

    public VaEBomPackupNodeMenuItem(VaTree tree) {
        this.tree = tree;
        setText("打包");
//        setIconStr("copy.gif");

    }

    @Override
    protected void actionPerformed(ActionEvent evt) {
        TreePath[] paths = tree.getSelectionPaths();
        if (paths.length > 1 || paths.length == 0) {
            JOptionPane.showMessageDialog(null, "请选择单个节点进行打包！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        } else {
            VaTreeNode node = (VaTreeNode) paths[0].getLastPathComponent();
            if(node instanceof DpPartNode){
                //左下角工艺树打包
                DpPartNode dpPartNode = (DpPartNode) node;
                String partNumber = dpPartNode.getPart().getNumber();
                String zcMark = dpPartNode.getZcmark();
                VaTreeNode parentNode = (VaTreeNode) dpPartNode.getParent();
                int amount = 0;
                String occId = "";
                List<VaTreeNode> removeNodeList = new ArrayList<VaTreeNode>();
                for (int i = 0; i < parentNode.getChildCount(); i++) {
                    DpPartNode childNode = (DpPartNode) parentNode.getChildAt(i);
                    if (childNode.getPart().getNumber().equals(partNumber)
                            && childNode.getZcmark().endsWith(zcMark)) {
                        removeNodeList.add(childNode);
                        if (childNode.isUsed()) {
                            amount++;
                        }
                    }
                }
                for (int i = 0; i < removeNodeList.size(); i++) {
                    if ("".equals(occId)) {
                        occId = removeNodeList.get(i).getOccId();
                    } else {
                        occId = occId + "," + removeNodeList.get(i).getOccId();
                    }
                    parentNode.remove(removeNodeList.get(i));
                }
                VaLightPart vaLightPart = (VaLightPart) node.getPart().clone();
                vaLightPart.setAmount(removeNodeList.size());
                DpPartNode packedNode = new DpPartNode(new VaEPartInstance(vaLightPart));
                packedNode.setOccId(occId);
                packedNode.setDataType(dpPartNode.getDataType());
                packedNode.setBzh(dpPartNode.getBzh());
                packedNode.setJxxndjhyd(dpPartNode.getJxxndjhyd());
                packedNode.setBmcl(dpPartNode.getBmcl());
                packedNode.setRcl(dpPartNode.getRcl());
                packedNode.setCpxs(dpPartNode.getCpxs());
                packedNode.setCpdj(dpPartNode.getCpdj());
                packedNode.setBnxs(dpPartNode.getBnxs());
                packedNode.setSfjk(dpPartNode.getSfjk());
                packedNode.setComment(dpPartNode.getComment());
                packedNode.setXhph(dpPartNode.getXhph());
                packedNode.setGg(dpPartNode.getGg());
                packedNode.setDw2(dpPartNode.getDw2());
                packedNode.setDw(dpPartNode.getDw());
                packedNode.setFlag(dpPartNode.getFlag());
                packedNode.setZcmark(dpPartNode.getZcmark());
                if (amount == removeNodeList.size()) {
                    packedNode.setUsed(true);
                } else {
                    packedNode.setUsed(false);
                }
                parentNode.add(packedNode);
                VaTree.sortVatree(parentNode);
                tree.updateUI();
            }else{
                //右上角FPBOM打包
                String partNumber = node.getPart().getNumber();
                VaTreeNode parentNode = (VaTreeNode) node.getParent();
                int amount = 0;
                String occId = "";
                String occPath = "";
                List<VaTreeNode> removeNodeList = new ArrayList<VaTreeNode>();
                for (int i = 0; i < parentNode.getChildCount(); i++) {
                    VaTreeNode childNode = (VaTreeNode) parentNode.getChildAt(i);
                    if (childNode.getPart().getNumber().equals(partNumber)) {
                        removeNodeList.add(childNode);
                        if (childNode.isUsed()) {
                            amount++;
                        }
                    }
                }
                for (int i = 0; i < removeNodeList.size(); i++) {
                    if ("".equals(occId)) {
                        occId = removeNodeList.get(i).getOccId();
                    } else {
                        occId = occId + "," + removeNodeList.get(i).getOccId();
                    }
                    if ("".equals(occPath)) {
                        occPath = removeNodeList.get(i).getOccpath();
                    } else {
                        occPath = occPath + "," + removeNodeList.get(i).getOccpath();
                    }
                    parentNode.remove(removeNodeList.get(i));
//                tree.updateUI();
                }
                VaLightPart vaLightPart = (VaLightPart) node.getPart().clone();
                vaLightPart.setAmount(removeNodeList.size());
                VaTreeNode packedNode = new VaTreeNode(new VaEPartInstance(vaLightPart));
                packedNode.setOccpath(occPath);
                packedNode.setOccId(occId);
                packedNode.setDataType(node.getDataType());
                packedNode.setBzh(node.getBzh());
                packedNode.setJxxndjhyd(node.getJxxndjhyd());
                packedNode.setBmcl(node.getBmcl());
                packedNode.setRcl(node.getRcl());
                packedNode.setCpxs(node.getCpxs());
                packedNode.setCpdj(node.getCpdj());
                packedNode.setBnxs(node.getBnxs());
                packedNode.setSfjk(node.getSfjk());
                packedNode.setComment(node.getComment());
                packedNode.setXhph(node.getXhph());
                packedNode.setGg(node.getGg());
                packedNode.setDw2(node.getDw2());
                packedNode.setDw(node.getDw());
                packedNode.setFlag(node.getFlag());
                if (amount == removeNodeList.size()) {
                    packedNode.setUsed(true);
                } else {
                    packedNode.setUsed(false);
                }
                parentNode.add(packedNode);
                VaTree.sortVatree(parentNode);
                tree.updateUI();
            }

        }
    }
}

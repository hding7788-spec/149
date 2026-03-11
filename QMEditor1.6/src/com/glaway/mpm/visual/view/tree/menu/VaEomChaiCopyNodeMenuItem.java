package com.glaway.mpm.visual.view.tree.menu;

import com.glaway.mpm.qmIntf.participatePart.VaClipboard;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VaEomChaiCopyNodeMenuItem extends VaMenuItem {

    private static final long serialVersionUID = -8396429397442896329L;
    private VaTree tree;

    public VaEomChaiCopyNodeMenuItem(VaTree tree) {
        this.tree = tree;
        setText("拆");
        setIconStr("copy.gif");
    }


    @Override
    public void setEnabled(boolean b) {
        // TODO Auto-generated method stub
        super.setEnabled(b);

    }

    @Override
    protected void actionPerformed(ActionEvent evt) {
        VaClipboard.clipboard3.clear();
        List<VaTreeNode> vaTreeNodeList = new ArrayList<VaTreeNode>();
        TreePath[] paths = tree.getSelectionPaths();
        List<String> partNubmerList = new ArrayList<String>();
        String usedNumber = "";
        if(paths != null){
            for (TreePath path : paths) {
                VaTreeNode node = (VaTreeNode) path.getLastPathComponent();
                vaTreeNodeList.add(node);
                String partNumber = node.getPart().getNumber();
                if (!partNubmerList.contains(partNumber)) {
                    partNubmerList.add(partNumber);
                }
                if(node.getcCount() == 0){
                    if("".equals(usedNumber)){
                        usedNumber = partNumber;
                    }else{
                        usedNumber += "," + partNumber;
                    }
                }
            }
//            if(partNubmerList.size() > 1){
//                JOptionPane.showMessageDialog(null, "请选择单个节点，或选择编号相同的节点进行参装", "提示", JOptionPane.INFORMATION_MESSAGE);
//                return;
//            }
            if(!"".equals(usedNumber)){
                JOptionPane.showMessageDialog(null, "您选择的节点中有部分不可以参与' 拆 '！", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            VaClipboard.clipboard3.put("C",vaTreeNodeList);
        }
    }
//    @Override
    protected void actionPerformed_old(ActionEvent evt) {
        VaClipboard.clipboard2.clear();
        VaClipboard.clipboard3.clear();
        TreePath[] paths = tree.getSelectionPaths();
        Boolean flag = false;
        int i = 0;
        for (TreePath path : paths) {
            VaTreeNode node = (VaTreeNode) path.getLastPathComponent();
            VaTreeNode root = (VaTreeNode) node.getRoot();
            String value = root.getUserObject().toString();
            String[] occIds = node.getOccId().split(",");
            String[] occPaths = node.getOccpath().split(",");
            if (occIds.length > 1) {
                for (int j = 0; j < occIds.length; j++) {
                    if (j == 0) {
                        flag = isUsed(occIds[j]);
                    } else {
                        if (!flag.equals(isUsed(occIds[j]))) {
                            JOptionPane.showMessageDialog(null, "您选择的节点中有部分不可以参与' 拆 '！", "提示", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                    }
                }
            } else {
                if (i == 0) {
                    flag = node.isUsed();
                    i++;
                } else {
                    if (!flag.equals(node.isUsed())) {
                        JOptionPane.showMessageDialog(null, "您选择的节点中有部分不可以参与' 拆 '！", "提示", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
            }
            VaClipboard.clipboard2.add(node);
//            VaClipboard.clipboard3.put("C",node);



//            if ("FPBOM".equals(value)) {
//                if (node.equals(root)) {
//                    JOptionPane.showMessageDialog(null, "顶层不参与参装！", "提示", JOptionPane.INFORMATION_MESSAGE);
//                    return;
//                }
//                String[] occIds = node.getOccId().split(",");
//                String[] occPaths = node.getOccpath().split(",");
//                if (occIds.length > 1) {
//                    Map<String, Integer> map = VaTree.getNodeUsedMap();
//                    VaTreeNode unpackedNode;
//                    node.setUsed(false);
//                    for (int j = 0; j < occIds.length; j++) {
//                        unpackedNode = new VaTreeNode(new VaEPartInstance(node.getPart()));
//                        unpackedNode.getPart().setAmount(0);
//                        unpackedNode.setOccId(occIds[j]);
//                        unpackedNode.setOccpath(occPaths[j]);
//                        unpackedNode.setDataType(node.getDataType());
//                        unpackedNode.setBzh(node.getBzh());
//                        unpackedNode.setJxxndjhyd(node.getJxxndjhyd());
//                        unpackedNode.setBmcl(node.getBmcl());
//                        unpackedNode.setRcl(node.getRcl());
//                        unpackedNode.setCpxs(node.getCpxs());
//                        unpackedNode.setCpdj(node.getCpdj());
//                        unpackedNode.setBnxs(node.getBnxs());
//                        unpackedNode.setSfjk(node.getSfjk());
//                        unpackedNode.setComment(node.getComment());
//                        unpackedNode.setXhph(node.getXhph());
//                        unpackedNode.setGg(node.getGg());
//                        unpackedNode.setFlag("SJZYKGYDENEWPART");
//                        if (map.containsKey(unpackedNode.getOccId())) {
//                            Integer cishu = map.get(unpackedNode.getOccId());
//                            if (cishu == 0) {
//                                unpackedNode.setUsed(true);
//                            } else {
//                                unpackedNode.setUsed(false);
//                            }
//                        } else {
//                            unpackedNode.setUsed(false);
//                        }
//                        if (j == 0) {
//                            flag = unpackedNode.isUsed();
//                        } else {
//                            if (!flag.equals(unpackedNode.isUsed())) {
//                                JOptionPane.showMessageDialog(null, "您选择的节点中有部分不可以参与' 拆 '！", "提示", JOptionPane.INFORMATION_MESSAGE);
//                                return;
//                            }
//                        }
//                        VaClipboard.clipboard2.add(unpackedNode);
//                    }
//                } else {
//                    if (i == 0) {
//                        flag = node.isUsed();
//                        i++;
//                    } else {
//                        if (!flag.equals(node.isUsed())) {
//                            JOptionPane.showMessageDialog(null, "您选择的节点中有部分不可以参与' 拆 '！", "提示", JOptionPane.INFORMATION_MESSAGE);
//                            return;
//                        }
//                    }
//                    VaClipboard.clipboard2.add((VaTreeNode) node);
//                }
//            } else {
//                VaTreeNode child = (VaTreeNode) root.getChildAt(0);
//                if (node.equals(root) || node.equals(child)) {
//                    JOptionPane.showMessageDialog(null, "顶层不参与参装！", "提示", JOptionPane.INFORMATION_MESSAGE);
//                    return;
//                }
//                String[] occIds = node.getOccId().split(",");
//                String[] occPaths = node.getOccpath().split(",");
//                if (occIds.length > 1) {
//                    Map<String, Integer> map = VaTree.getNodeUsedMap();
//                    VaTreeNode unpackedNode;
//                    node.setUsed(false);
//                    for (int j = 0; j < occIds.length; j++) {
//                        unpackedNode = new VaTreeNode(new VaEPartInstance(node.getPart()));
//                        unpackedNode.getPart().setAmount(0);
//                        unpackedNode.setOccId(occIds[j]);
//                        unpackedNode.setOccpath(occPaths[j]);
//                        unpackedNode.setDataType(node.getDataType());
//                        unpackedNode.setBzh(node.getBzh());
//                        unpackedNode.setJxxndjhyd(node.getJxxndjhyd());
//                        unpackedNode.setBmcl(node.getBmcl());
//                        unpackedNode.setRcl(node.getRcl());
//                        unpackedNode.setCpxs(node.getCpxs());
//                        unpackedNode.setCpdj(node.getCpdj());
//                        unpackedNode.setBnxs(node.getBnxs());
//                        unpackedNode.setSfjk(node.getSfjk());
//                        unpackedNode.setComment(node.getComment());
//                        unpackedNode.setXhph(node.getXhph());
//                        unpackedNode.setGg(node.getGg());
//                        unpackedNode.setFlag("SJZYKGYDENEWPART");
//                        if (map.containsKey(unpackedNode.getOccId())) {
//                            Integer cishu = map.get(unpackedNode.getOccId());
//                            if (cishu == 0) {
//                                unpackedNode.setUsed(true);
//                            } else {
//                                unpackedNode.setUsed(false);
//                            }
//                        } else {
//                            unpackedNode.setUsed(false);
//                        }
//                        if (j == 0) {
//                            flag = unpackedNode.isUsed();
//                        } else {
//                            if (!flag.equals(unpackedNode.isUsed())) {
//                                JOptionPane.showMessageDialog(null, "您选择的节点中有部分不可以参与' 拆 '！", "提示", JOptionPane.INFORMATION_MESSAGE);
//                                return;
//                            }
//                        }
//                        VaClipboard.clipboard2.add(unpackedNode);
//                    }
//                } else {
//                    if (i == 0) {
//                        flag = node.isUsed();
//                        i++;
//                    } else {
//                        if (!flag.equals(node.isUsed())) {
//                            JOptionPane.showMessageDialog(null, "您选择的节点中有部分不可以参与' 拆 '！", "提示", JOptionPane.INFORMATION_MESSAGE);
//                            return;
//                        }
//                    }
//                    VaClipboard.clipboard2.add((VaTreeNode) node);
//                }
//            }
        }
    }
    public static boolean isUsed(String occId) {
        Boolean isUsed = false;
        Map<String, Integer> map = VaTree.getNodeUsedMap();
        Integer integer = map.get(occId);
        if (integer != null && integer == 0) {
            isUsed = true;
        }
        return isUsed;
    }
}



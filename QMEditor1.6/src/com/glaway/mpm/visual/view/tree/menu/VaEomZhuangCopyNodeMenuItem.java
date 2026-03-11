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

public class VaEomZhuangCopyNodeMenuItem extends VaMenuItem {

    private static final long serialVersionUID = -8396429397442896329L;
    private VaTree tree;

    public VaEomZhuangCopyNodeMenuItem(VaTree tree) {
        this.tree = tree;
        setText("装");
        setIconStr("copy.gif");

    }


    @Override
    public void setEnabled(boolean b) {
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
                if(node.getzCount() == 0){
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
                JOptionPane.showMessageDialog(null, "您选择的节点中有部分不可以参与' 装 '！", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            VaClipboard.clipboard3.put("Z", vaTreeNodeList);
        }

    }
//    @Override
    protected void actionPerformed_old(ActionEvent evt) {
//        VaClipboard.clipboard2.clear();
        VaClipboard.clipboard3.clear();
        TreePath[] paths = tree.getSelectionPaths();
        Boolean flag = false;
        Boolean isUsed;
        int i = 0;
        List<String> partNumberList = new ArrayList<String>();
        for (TreePath path : paths) {
            VaTreeNode node = (VaTreeNode) path.getLastPathComponent();
            String partNumber = node.getPart().getNumber();
            VaTreeNode root = (VaTreeNode) node.getRoot();
            String value = root.getUserObject().toString();
            String[] occIds = node.getOccId().split(",");
            if (occIds.length > 1) {
                for (int j = 0; j < occIds.length; j++) {
                    if (j == 0) {
                        flag = isUsed(occIds[j]);
                    } else {
                        if (!flag.equals(isUsed(occIds[j]))) {
                            JOptionPane.showMessageDialog(null, "您选择的节点中有部分不可以参与' 装 '！", "提示", JOptionPane.INFORMATION_MESSAGE);
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
                        JOptionPane.showMessageDialog(null, "您选择的节点中有部分不可以参与' 装 '！", "提示", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }
            }
//            VaClipboard.clipboard2.add(node);
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

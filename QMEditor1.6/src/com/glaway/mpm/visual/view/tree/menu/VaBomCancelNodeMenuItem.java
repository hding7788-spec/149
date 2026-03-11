package com.glaway.mpm.visual.view.tree.menu;

import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpPaceNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpPartNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpStepNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpTreePanel;
import com.glaway.mpm.qmIntf.fittingTool.view.FittingsDistributionFrame;
import com.glaway.mpm.qmIntf.participatePart.ParticipatePartAddDialog;
import com.glaway.mpm.qmIntf.participatePart.ParticipatePartUtil;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.bean.VaLightPart;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.VaContext;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.glaway.mpm.visual.view.ui.VaEBomTreePanel;
import com.glaway.mpm.visual.view.ui.VaScrollPaneTree;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class VaBomCancelNodeMenuItem<E> extends VaMenuItem {
    private static final long serialVersionUID = -8396429397442896329L;
    private VaTree tree;
    private static VaLogger logger = VaLogger.getLogger(VaBomCancelNodeMenuItem.class);

    public VaBomCancelNodeMenuItem(VaTree tree) {
        this.tree = tree;
        setText("取消参装");
        setIconStr("cut.gif");
    }


    @Override
    public void actionPerformed(ActionEvent evt) {
        logger.debug("clear fittings!");
        TreePath[] paths = tree.getSelectionPaths();
        DefaultTreeModel treeModel = (DefaultTreeModel) (tree.getModel());
        DpTreePanel dpPanel = null;
        if(paths != null && paths.length > 0){
            Map<String, Double> partCountMap = new HashMap<String, Double>();
            for (int i = 0; i < paths.length; i++) {
                Vector<Map<String, String>> parts = new Vector<Map<String, String>>();
                VaTreeNode node = (VaTreeNode) paths[i].getLastPathComponent();
                if(node instanceof DpPartNode){
                    DpPartNode dpPartNode = (DpPartNode) node;
                    String partNumber = dpPartNode.getPart().getNumber();
                    String occId = dpPartNode.getOccId();
                    String key = partNumber + "_" + occId;
                    Map<String, String> partMap = new HashMap<String, String>();
                    partMap.put("partNumber", node.getPart().getNumber());
                    partMap.put("oid", String.valueOf(node.getPart().getOid()));
                    partMap.put("occId", node.getOccId());
                    partMap.put("partName", node.getPart().getName());
                    partMap.put("material", "");
                    partMap.put("dutu", "");
                    partMap.put("remark", "");
                    partMap.put("useCount", String.valueOf(node.getPart().getAmount()));
                    parts.add(partMap);
                    if(partCountMap.containsKey(key)){
                        if("Z".equals(dpPartNode.getZcmark())){
                            partCountMap.put(key,CommonUtil.addDouble(partCountMap.get(key),dpPartNode.getPart().getAmount()));
                        }else if("C".equals(dpPartNode.getZcmark())){
                            partCountMap.put(key,CommonUtil.subDouble(partCountMap.get(key),dpPartNode.getPart().getAmount()));
                        }
                    }else{
                        if("Z".equals(dpPartNode.getZcmark())){
                            partCountMap.put(key,CommonUtil.addDouble(0,dpPartNode.getPart().getAmount()));
                        }else if("C".equals(dpPartNode.getZcmark())){
                            partCountMap.put(key,CommonUtil.subDouble(0,dpPartNode.getPart().getAmount()));
                        }
                    }
                    //删除xml中的数据
                    ParticipatePartUtil.deleteParticipateParts(parts);
                    //删除工艺树上的参装件节点
                    if(node.getParent() instanceof DpStepNode){
                        //如果移除的是工序节点下的参装件
                        DpStepNode step = ((DpStepNode) node.getParent());
                        String stepOid = step.getStep().getOid();
                        FittingsDistributionFrame.deletePart(stepOid, parts);
                    }else if(node.getParent() instanceof DpPaceNode){
                        //如果移除的是工步节点下的参装件
                        DpPaceNode pace = ((DpPaceNode) node.getParent());
                        DpStepNode stepNode = (DpStepNode) pace.getParent();
                        String stepOid = stepNode.getStep().getOid();
                        String paceOid = pace.getPace().getOid();

                        FittingsDistributionFrame.deletePart(stepOid, paceOid, parts);
                    }else {
                        JOptionPane.showMessageDialog(null, "只能删除参装件!");
                    }

                    if (node.isSelected()) {
                        node.setSelected(false);
                        treeModel.nodeChanged(node);
                        Container jc = tree.getParent();
                        while (jc != null) {
                            if (jc instanceof DpTreePanel) {
                                dpPanel = (DpTreePanel) jc;
                                break;
                            }
                            jc = jc.getParent();
                        }
                    }
                    //移除参装件工艺树上的节点
                    VaTreeNode parentNode = (VaTreeNode) node.getParent();
                    treeModel.removeNodeFromParent(node);
                    if(node.isMore()){
                        ParticipatePartAddDialog.morePartList = FittingsDistributionFrame.checkIsHasMoreParts();
                        refreshParentNode(parentNode);
                    }
                }
            }

            if (dpPanel != null) {
                dpPanel.pviewAction();
            }

            //更新ZPBOM树上的信息
            VaTree zPBomTree = VaEBomTreePanel.getScrollTreePane().getVaTree();
            VaTreeNode pbomTreeNode = zPBomTree.getRoot();
            VaTreeNode child = (VaTreeNode) pbomTreeNode.getChildAt(0);
            Enumeration<VaTreeNode> pbomNodeChildren = child.children();
            while (pbomNodeChildren.hasMoreElements()) {
                VaTreeNode pbomChild = pbomNodeChildren.nextElement();
                VaLightPart pbomChildPart = pbomChild.getPart();
                String key = pbomChildPart.getNumber() + "_" +pbomChild.getOccId();
                if(partCountMap.containsKey(key)){
                    pbomChild.setzCount(CommonUtil.addDouble(pbomChild.getzCount(),partCountMap.get(key)));
                    pbomChildPart.setzCount(pbomChild.getzCount());
                    pbomChild.setcCount(CommonUtil.subDouble(pbomChild.getzCount(),partCountMap.get(key)));
                    pbomChildPart.setcCount(pbomChild.getcCount());
                    if(pbomChild.getzCount() == 0){
                        pbomChild.setUsed(true);
                    }else{
                        pbomChild.setUsed(false);
                    }
                }
            }
            zPBomTree.updateUI();
            //更新FPBOM树上的信息
            VaTree fPBomTree = VaEBomTreePanel.getScrollTreePane2().getVaTree();
            VaTreeNode treeNode = fPBomTree.getRoot();
            Enumeration<VaTreeNode> pbomChildren = treeNode.children();
            while (pbomChildren.hasMoreElements()) {
                VaTreeNode pbomChild = pbomChildren.nextElement();
                VaLightPart pbomChildPart = pbomChild.getPart();
                String key = pbomChildPart.getNumber() + "_" +pbomChild.getOccId();
                if(partCountMap.containsKey(key)){
                    pbomChild.setzCount(CommonUtil.addDouble(pbomChild.getzCount(),partCountMap.get(key)));
                    pbomChildPart.setzCount(pbomChild.getzCount());
                    pbomChild.setcCount(CommonUtil.subDouble(pbomChild.getcCount(),partCountMap.get(key)));
                    pbomChildPart.setcCount(pbomChild.getcCount());
                    if(pbomChild.getzCount() == 0){
                        pbomChild.setUsed(true);
                    }else{
                        pbomChild.setUsed(false);
                    }
                }
            }
            fPBomTree.updateUI();

            tree.updateUI();

            //保存xml
            JFrame frame = VaContext.getMainFrame();
            if (frame != null && frame instanceof NewTechnicsPart) {
                NewTechnicsPart newTechnicsPart = (NewTechnicsPart) frame;
                Element techele = XmlUtility.getTechnicsElement(newTechnicsPart.getCurrentTechnics());
                newTechnicsPart.saveProcess(techele);
            }
        }
    }

    /**
     * 刷新父节点
     * @param parentNode
     */
    private void refreshParentNode(VaTreeNode parentNode) {
        boolean hasMore = false;
        if(parentNode instanceof DpStepNode){
            DpStepNode stepNode = (DpStepNode) parentNode;
            Enumeration<VaTreeNode> children = stepNode.children();
            while(children.hasMoreElements()){
                VaTreeNode vaTreeNode = children.nextElement();
                if(vaTreeNode instanceof DpPartNode){
                    DpPartNode dpPartNode = (DpPartNode) vaTreeNode;
                    if(dpPartNode.isMore()){
                        hasMore = true;
                        break;
                    }
                }else if(vaTreeNode instanceof DpPaceNode){
                    DpPaceNode dpPaceNode = (DpPaceNode) vaTreeNode;
                    if(dpPaceNode.isMore()){
                        hasMore = true;
                        break;
                    }
                }
            }
            if(!hasMore){
                stepNode.setMore(false);
            }
        }else if(parentNode instanceof DpPaceNode){
            DpPaceNode dpPaceNode = (DpPaceNode) parentNode;
            Enumeration<VaTreeNode> children = dpPaceNode.children();
            while(children.hasMoreElements()){
                VaTreeNode vaTreeNode = children.nextElement();
                if(vaTreeNode instanceof DpPartNode){
                    DpPartNode dpPartNode = (DpPartNode) vaTreeNode;
                    if(dpPartNode.isMore()){
                        hasMore = true;
                        break;
                    }
                }
            }
            if(!hasMore){
                dpPaceNode.setMore(false);
                DpStepNode dpStepNode = (DpStepNode) dpPaceNode.getParent();
                Enumeration<VaTreeNode> childrenNode = dpStepNode.children();
                while (childrenNode.hasMoreElements()) {
                    VaTreeNode vaTreeNode = childrenNode.nextElement();
                    if (vaTreeNode instanceof DpPaceNode) {
                        DpPaceNode paceNode = (DpPaceNode) vaTreeNode;
                        if (paceNode.isMore()) {
                            hasMore = true;
                            break;
                        }
                    }
                }
                if (!hasMore) {
                    dpStepNode.setMore(false);
                }
            }

        }
    }

    /**
     * 老的操作逻辑20191017
     * @param evt
     */
    public void actionPerformed_old(ActionEvent evt) {
        Vector<String> vector = new Vector<String>();
        logger.debug("clear fittings!");
        TreePath[] paths = tree.getSelectionPaths();
        DpTreePanel dpPanel = null;
        for (int i = 0; i < paths.length; i++) {
            VaTreeNode node = (VaTreeNode) paths[i].getLastPathComponent();
            if (node instanceof DpPartNode && node.getParent() instanceof DpPaceNode) {
                DefaultTreeModel treeModel = (DefaultTreeModel) (tree.getModel());
                String[] occIds = node.getOccId().split(",");
//                String[] occPaths = node.getOccpath().split(",");
                Vector<Map<String, String>> parts = new Vector<Map<String, String>>();
//                int n= 0;
                for (String occIdStr : occIds) {
                    vector.add(occIdStr);
                    Map<String, String> partMap = new HashMap<String, String>();
                    partMap.put("partNumber", node.getPart().getNumber());
                    partMap.put("oid", String.valueOf(node.getPart().getOid()));
                    partMap.put("occId", occIdStr);
//                    partMap.put("occpath", occPaths[n]);
                    partMap.put("partName", node.getPart().getName());
                    partMap.put("material", "");
                    partMap.put("dutu", "");
                    partMap.put("remark", "");
                    partMap.put("useCount", "1");
                    parts.add(partMap);
//                    n++;
                }
                ParticipatePartUtil.deleteParticipateParts(parts);

                DpPaceNode pace = ((DpPaceNode) node.getParent());
                DpStepNode stepNode = (DpStepNode) pace.getParent();
                String stepOid = stepNode.getStep().getOid();
                String paceOid = pace.getPace().getOid();
                FittingsDistributionFrame.deletePart(stepOid, paceOid, parts);
                if (node.isSelected()) {
                    node.setSelected(false);
                    treeModel.nodeChanged(node);
                    Container jc = tree.getParent();
                    while (jc != null) {
                        if (jc instanceof DpTreePanel) {
                            dpPanel = (DpTreePanel) jc;
                            break;
                        }
                        jc = jc.getParent();
                    }
                }
                treeModel.removeNodeFromParent(node);

            } else if (node instanceof DpPartNode && node.getParent() instanceof DpStepNode) {
                DefaultTreeModel treeModel = (DefaultTreeModel) (tree.getModel());
                String[] occIds = node.getOccId().split(",");
//                String[] occPaths = node.getOccpath().split(",");
                Vector<Map<String, String>> parts = new Vector<Map<String, String>>();
//                int n = 0;
                for (String occIdStr : occIds) {
                    vector.add(occIdStr);
                    Map<String, String> partMap = new HashMap<String, String>();
                    partMap.put("partNumber", node.getPart().getNumber());
                    partMap.put("oid", String.valueOf(node.getPart().getOid()));
                    partMap.put("occId", occIdStr);
//                    partMap.put("occpath", occPaths[n]);
                    partMap.put("partName", node.getPart().getName());
                    partMap.put("material", "");
                    partMap.put("dutu", "");
                    partMap.put("remark", "");
                    partMap.put("useCount", "1");
                    parts.add(partMap);
//                    n++;
                }

                ParticipatePartUtil.deleteStepParticipateParts(parts);

                DpStepNode step = ((DpStepNode) node.getParent());
                String stepOid = step.getStep().getOid();
                FittingsDistributionFrame.deletePart(stepOid, parts);
                if (node.isSelected()) {
                    node.setSelected(false);
                    treeModel.nodeChanged(node);
                    Container jc = tree.getParent();
                    while (jc != null) {
                        if (jc instanceof DpTreePanel) {
                            dpPanel = (DpTreePanel) jc;
                            break;
                        }
                        jc = jc.getParent();
                    }
                }
                treeModel.removeNodeFromParent(node);

            } else {
                JOptionPane.showMessageDialog(null, "只能删除参装件!");
            }

        }
        if (dpPanel != null) {
            dpPanel.pviewAction();
        }
        VaScrollPaneTree pane = VaEBomTreePanel.getScrollTreePane();

        VaTree FTree = VaEBomTreePanel.getScrollTreePane().getVaTree();
        VaTreeNode pbomTreeNode = FTree.getRoot();
        VaTreeNode child = (VaTreeNode) pbomTreeNode.getChildAt(0);
        Enumeration<VaTreeNode> pbomNodeChildren = child.children();
        while (pbomNodeChildren.hasMoreElements()) {
            VaTreeNode pbomChild = pbomNodeChildren.nextElement();
            String[] occIds = pbomChild.getOccId().split(",");
            if (occIds.length > 1) {
                for (String occId : occIds) {
                    if (vector.contains(occId)) {
                        pbomChild.setUsed(false);
                    }
                }
            } else {
                if (vector.contains(pbomChild.getOccId())) {
                    pbomChild.setUsed(false);

                }
            }
        }
        VaTree vaTree = VaEBomTreePanel.getScrollTreePane2().getVaTree();
        VaTreeNode treeNode = vaTree.getRoot();
        Enumeration<VaTreeNode> pbomChildren = treeNode.children();
        while (pbomChildren.hasMoreElements()) {
            VaTreeNode pbomChild = pbomChildren.nextElement();
            String[] occIds = pbomChild.getOccId().split(",");
            if (occIds.length > 1) {
                for (String occId : occIds) {
                    if (vector.contains(occId)) {
                        pbomChild.setUsed(false);
                    }
                }
            } else {
                if (vector.contains(pbomChild.getOccId())) {
                    pbomChild.setUsed(false);
                }
            }
        }
        tree.updateUI();

    }
}

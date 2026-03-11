package com.glaway.mpm.visual.view.tree.menu;

import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpPaceNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpPartNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpStepNode;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpTreePanel;
import com.glaway.mpm.qmIntf.fittingTool.view.FittingsDistributionFrame;
import com.glaway.mpm.qmIntf.participatePart.ParticipatePartUtil;
import com.glaway.mpm.qmIntf.participatePart.VaClipboard;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.bean.VaEPartInstance;
import com.glaway.mpm.visual.bean.VaLightPart;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.VaContext;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.glaway.mpm.visual.view.ui.VaEBomTreePanel;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.*;

public class VaBomPasteNodeMenuItem extends VaMenuItem {
    private static final long serialVersionUID = -8396429397442896329L;
    private VaTree tree;
    private static VaLogger logger = VaLogger.getLogger(VaBomPasteNodeMenuItem.class);
    private String currentOperation;

    public VaBomPasteNodeMenuItem(VaTree tree) {
        this.tree = tree;
        setText("粘贴");
        setIconStr("paste.gif");
    }

    private boolean checkUsed(VaTreeNode vaTreeNode) {
        VaTreeNode parentNode = (VaTreeNode) vaTreeNode.getParent();
        if (parentNode == null) {
            return false;
        }
        Enumeration children = parentNode.children();
        while (children.hasMoreElements()) {
            VaTreeNode child = (VaTreeNode) children.nextElement();
            if (child.isUsed() || child.equals(vaTreeNode)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void actionPerformed(ActionEvent evt) {
        logger.debug("paste");
        TreePath[] paths = tree.getSelectionPaths();
        VaTreeNode node = (VaTreeNode) paths[0].getLastPathComponent();
        int nodeCount = VaClipboard.clipboard2.size();

        List<VaTreeNode> vaTreeNodeList;
        if(VaClipboard.clipboard3.get("Z") != null){
            currentOperation = "Z";
            vaTreeNodeList = VaClipboard.clipboard3.get("Z");
        }else if(VaClipboard.clipboard3.get("C") != null){
            currentOperation = "C";
            vaTreeNodeList = VaClipboard.clipboard3.get("C");
        }else{
            JOptionPane.showMessageDialog(null, "未获取到参装节点，请重新进行参装！");
            return;
        }
        for(VaTreeNode selectedNode : vaTreeNodeList){
        	//获取数量
            Double zCount = selectedNode.getzCount();
            String num = JOptionPane.showInputDialog(tree.getParent(), "请输入"+selectedNode.getPart().getNumber()+"("+selectedNode.getPart().getName()+")数量",Math.round(zCount));
            //获取单位
            String unit = getUnit(selectedNode);
            if (unit == null || unit.isEmpty()) {
                JOptionPane.showMessageDialog(null, "未获取到参装节点的单位，请重新进行参装！");
                return;
            }
            //校验输入的值是否符合规范
            if (!CommonUtil.checkGysl(num, unit)) {
                JOptionPane.showMessageDialog(null, "数量填写不规范：\r\n单位为“个”，“只”，“件”的数量只能为整数，其他的只能为最多三位小数！");
                continue;
            }
            //校验数量是否小于最大值
            Double inputNum = Double.valueOf(num);
            Double cCount = selectedNode.getcCount();
            if ("Z".equals(currentOperation)) {
                if (inputNum > zCount) {
                    JOptionPane.showMessageDialog(null, "输入数量不合理：\r\n当前可装数量为" + zCount + ",填写数量必须小于等于此数值！");
                    continue;
                }
            } else if ("C".equals(currentOperation)) {
                if (inputNum > cCount) {
                    JOptionPane.showMessageDialog(null, "输入数量不合理：\r\n当前可拆数量为" + cCount + ",填写数量必须小于等于此数值！");
                    continue;
                }
            }
            DefaultTreeModel treeModel = (DefaultTreeModel) (tree.getModel());
            String stepOid = "";
            String paceOid = "";
            if (node instanceof DpStepNode) {
                DpStepNode stepNode = (DpStepNode) node;
                stepOid = stepNode.getStep().getOid();
                //在工序节点下粘贴
                if (node.getChildCount() > 0 && (node.getChildAt(0) instanceof DpPaceNode)) {
                    //如果工序下有工步节点，则不允许参装
                    JOptionPane.showMessageDialog(null, "该工序下有工步，无法进行拆装！");
                    return;
                }
            } else if (node instanceof DpPaceNode) {
                DpPaceNode paceNode = (DpPaceNode) node;
                DpStepNode stepNode = (DpStepNode) paceNode.getParent();
                stepOid = stepNode.getStep().getOid();
                paceOid = paceNode.getPace().getOid();
            }else{
                JOptionPane.showMessageDialog(null, "请选择工序或者工步节点进行参装！");
                return;
            }
            //判断当前节点下有没有参装过同样的，有则数量累加
            DpPartNode dpPartNode = getDpPartNode(node, selectedNode.getOccId());
            if (dpPartNode != null) {
                double old_count = dpPartNode.getPart().getAmount();
                dpPartNode.getPart().setAmount(CommonUtil.addDouble(old_count, inputNum));
            } else {
                dpPartNode = covertTreeNodeToPartNode(selectedNode);
                dpPartNode.setZcmark(currentOperation);
                dpPartNode.getPart().setzCount(inputNum);
                dpPartNode.getPart().setAmount(inputNum);
                dpPartNode.getPart().setRootType("TECHNICS");
                treeModel.insertNodeInto(dpPartNode, node, 0);
            }
            //更新数量
            if ("Z".equals(currentOperation)) {
                selectedNode.setzCount(CommonUtil.subDouble(zCount, inputNum));
                selectedNode.setcCount(CommonUtil.addDouble(cCount, inputNum));
                selectedNode.getPart().setzCount(selectedNode.getzCount());
                selectedNode.getPart().setcCount(selectedNode.getcCount());
                if (selectedNode.getzCount() == 0) {
                    selectedNode.setUsed(true);
                } else {
                    selectedNode.setUsed(false);
                }
            } else if ("C".equals(currentOperation)) {
                selectedNode.setzCount(CommonUtil.addDouble(zCount, inputNum));
                selectedNode.setcCount(CommonUtil.subDouble(cCount, inputNum));
                selectedNode.getPart().setzCount(selectedNode.getzCount());
                selectedNode.getPart().setcCount(selectedNode.getcCount());
                if (selectedNode.getzCount() == 0) {
                    selectedNode.setUsed(true);
                } else {
                    selectedNode.setUsed(false);
                }
            }
            //保存到xml
            saveToXml(stepOid, paceOid, dpPartNode, selectedNode);

            //刷新ZPBOM树
            VaTree zPbomTree = VaEBomTreePanel.getScrollTreePane().getVaTree();
            zPbomTree.updateUI();
            //刷新FPBOM树
            VaTree fPbomTree = VaEBomTreePanel.getScrollTreePane2().getVaTree();
            fPbomTree.updateUI();

            //保存xml
            JFrame frame = VaContext.getMainFrame();
            if (frame != null && frame instanceof NewTechnicsPart) {
                NewTechnicsPart newTechnicsPart = (NewTechnicsPart) frame;
                Element techele = XmlUtility.getTechnicsElement(newTechnicsPart.getCurrentTechnics());
                newTechnicsPart.saveProcess(techele);
            }
        }

        //清空缓存
        VaClipboard.clipboard3.clear();
    }

    private void saveToXml(String stepOid, String paceOid, DpPartNode dpPartNode, VaTreeNode selectedNode) {
        Vector<Map<String, String>> parts = new Vector<Map<String, String>>();
        Map<String, String> partMap = new HashMap<String, String>();
        partMap.put("partNumber", dpPartNode.getPart().getNumber());
        partMap.put("oid", String.valueOf(dpPartNode.getPart().getOid()));
        partMap.put("occId", dpPartNode.getOccId());
        partMap.put("occpath", dpPartNode.getOccpath());
        partMap.put("partName", dpPartNode.getPart().getName());
        partMap.put("materialNumber",selectedNode.getPart().getMaterialNumber());//材料编号
        partMap.put("materialName",selectedNode.getPart().getMaterialName());//材料名称
        partMap.put("materialBrand",selectedNode.getPart().getMaterialBrand());//材料牌号
        partMap.put("materialCrision",selectedNode.getPart().getMaterialCrision());//材料标准号
        partMap.put("material", "");
        partMap.put("dutu", "");
        partMap.put("remark", "");
        partMap.put("useCount", String.valueOf(dpPartNode.getPart().getAmount()));

        partMap.put("MTYPE", dpPartNode.getPart().getMtype());
        String dataType = selectedNode.getDataType();
        String wh = selectedNode.getWh();
        if(dataType!=null && "元器件".equals(dataType) && wh!=null){
            partMap.put("wh", selectedNode.getWh());
        }
        String adjustable = selectedNode.getPart().getAdjustable();
        if(adjustable != null){
            partMap.put("adjustable", adjustable);
        }
        partMap.put("CSIZE", dpPartNode.getPart().getCsize());
        partMap.put("XHPH", dpPartNode.getPart().getXhph());
        partMap.put("JSTJ", dpPartNode.getPart().getJstj());
        partMap.put("GG", dpPartNode.getPart().getGg());
        partMap.put("DW", selectedNode.getDw());
        partMap.put("DW2", selectedNode.getDw2());
        partMap.put("bzh", dpPartNode.getBzh());
        partMap.put("dataType", dpPartNode.getDataType());
//        partMap.put("HASEPM", selectedNode.isHasEpmDoc() + "");
        if(selectedNode.getFlag().contains("SJZYK")){
            partMap.put("materialGg", selectedNode.getPart().getGg());//材料规格
            partMap.put("materialLb", selectedNode.getPart().getDataType());//材料类别
            partMap.put("materialPh", selectedNode.getPart().getCl());//材料牌号
            partMap.put("materialBzh", selectedNode.getPart().getBzh());//材料标准号
        }else{
            partMap.put("materialGg", selectedNode.getPart().getGg());//材料编号
            partMap.put("materialLb", selectedNode.getPart().getDataType());//材料名称
            partMap.put("materialPh", selectedNode.getPart().getXhph());//材料牌号
            partMap.put("materialBzh", selectedNode.getPart().getJstj());//材料标准号
        }

        if ("C".equals(dpPartNode.getPart().getZcmark())) {
            partMap.put("ZCMARK", "C");
        } else if ("Z".equals(dpPartNode.getPart().getZcmark())) {
            partMap.put("ZCMARK", "Z");
        }
        parts.add(partMap);
        if (paceOid != null && !paceOid.isEmpty()) {
            ParticipatePartUtil.addParticipateParts(stepOid, paceOid, parts, "");
            FittingsDistributionFrame.addParts(stepOid, paceOid);
        } else {
            ParticipatePartUtil.addParticipateParts(stepOid, parts, "");
            FittingsDistributionFrame.addParts(stepOid);
        }
    }

    private DpPartNode getDpPartNode(VaTreeNode node, String occId) {
        DpPartNode dpPartNode = null;
        if(node.getChildCount() > 0){
            for (int i = 0; i < node.getChildCount(); i++) {
                if(node.getChildAt(i) instanceof DpPartNode){
                    DpPartNode partNode = (DpPartNode) node.getChildAt(i);
                    if(partNode.getOccId().equals(occId) && partNode.getZcmark().equals(currentOperation)){
                        dpPartNode = partNode;
                    }
                }
            }
        }
        return dpPartNode;
    }

    /**
     * 校验参装数量
     * @param num
     * @param selectedNode
     * @return
     */
    private boolean checkCount(String num, VaTreeNode selectedNode) {
        Double inputNum = Double.valueOf(num);
        Double zCount = selectedNode.getzCount();
        Double cCount = selectedNode.getcCount();
        if("Z".equals(currentOperation)){
            return inputNum <= zCount;
        }else if("C".equals(currentOperation)){
            return inputNum <= cCount;
        }
        return false;
    }

    private String getUnit(VaTreeNode selectedNode) {
        String unit = null;
        if("GYDENEWPART".equals(selectedNode.getFlag())){
            unit = selectedNode.getDw2();
        } else if ("GYDEMATCHPART".equals(selectedNode.getFlag())){
            unit = selectedNode.getDw2();
        } else if ("SJZYKGYDENEWPART".equals(selectedNode.getFlag())){
            unit = selectedNode.getDw();
        } else if ("SJZYKGYDEMATCHPART".equals(selectedNode.getFlag())){
            unit = selectedNode.getDw();
        } else if ("ZPBOM".equals(selectedNode.getFlag())) {
            unit = selectedNode.getDw2();
        } else if ("LJZYCLDENEWPART".equals(selectedNode.getFlag()) || "ZPZYCLDENEWPART".equals(selectedNode.getFlag())) {
            unit = selectedNode.getDw2();
        }
        return unit;
    }

    /**
     * 老的处理方式保留，以作备份
     * @param evt
     */
//    @Override
    protected void actionPerformed_old(ActionEvent evt) {
        logger.debug("paste");
        TreePath[] paths = tree.getSelectionPaths();
        // List<VaTreeNode> nodeList = new ArrayList<VaTreeNode>();
        VaTreeNode node = (VaTreeNode) paths[0].getLastPathComponent();

        String num = JOptionPane.showInputDialog(tree.getParent(), "请输入数量");


        if (node instanceof DpPaceNode) {
            DpTreePanel dpPanel = null;
            Vector<Map<String, String>> parts = new Vector<Map<String, String>>();

            DpStepNode step = (DpStepNode) node.getParent();
            String isComplete = "false";
            VaTreeNode v = null;
            if (VaClipboard.clipboard2.size() != 0) {
                for (VaTreeNode vaTreeNode : VaClipboard.clipboard2) {

                    List<VaTreeNode> cVaTreeNodes = getCVatreeNodes(vaTreeNode);
                    if (cVaTreeNodes != null && cVaTreeNodes.size() > 0) {
                        for (VaTreeNode cVaTreeNode : cVaTreeNodes) {
                            Enumeration<VaTreeNode> fittings = tree.getRoot().depthFirstEnumeration();
                            while (fittings.hasMoreElements()) {
                                VaTreeNode treeNode = fittings.nextElement();
                                if (treeNode instanceof DpPartNode) {
                                    if (cVaTreeNode.getOccpath().equals(treeNode.getOccpath())) {
                                        JOptionPane.showMessageDialog(this, "参装件已添加!");
                                        return;
                                    }
                                }
                            }

                            DefaultTreeModel treeModel = (DefaultTreeModel) (tree.getModel());
                            if (checkUsed(cVaTreeNode)) {
                                isComplete = "true";
                            }
                            if (cVaTreeNode.isUsed()) {
                                VaTreeNode CNode = new VaTreeNode(cVaTreeNode.clone());
                                CNode.getPart().setZcmark("C");
                                cVaTreeNode.getPart().setZcmark("C");
                                treeModel.insertNodeInto(this.covertTreeNodeToPartNode(cVaTreeNode), node, 0);
                            } else {
                                VaTreeNode ZNode = new VaTreeNode(cVaTreeNode.clone());
                                ZNode.getPart().setZcmark("Z");
                                cVaTreeNode.getPart().setZcmark("Z");
                                treeModel.insertNodeInto(this.covertTreeNodeToPartNode(cVaTreeNode), node, 0);
                            }
                            System.out.println("vaaa=============" + cVaTreeNode.getPart().getZcmark());
                            cVaTreeNode.setSelected(true);

                            Map<String, String> partMap = new HashMap<String, String>();
                            partMap.put("partNumber", cVaTreeNode.getPart().getNumber());
                            partMap.put("oid", String.valueOf(cVaTreeNode.getPart().getOid()));
                            partMap.put("occId", cVaTreeNode.getOccId());
                            partMap.put("occpath", cVaTreeNode.getOccpath());
                            partMap.put("partName", cVaTreeNode.getPart().getName());
                            partMap.put("materialNumber",vaTreeNode.getPart().getMaterialNumber());//材料编号
                            partMap.put("materialName",vaTreeNode.getPart().getMaterialName());//材料名称
                            partMap.put("materialBrand",vaTreeNode.getPart().getMaterialBrand());//材料牌号
                            partMap.put("materialCrision",vaTreeNode.getPart().getMaterialCrision());//材料标准号
                            partMap.put("material", "");
                            partMap.put("dutu", "");
                            partMap.put("remark", "");
                            partMap.put("useCount", "1");

                            partMap.put("MTYPE", cVaTreeNode.getPart().getMtype());
                            partMap.put("CSIZE", cVaTreeNode.getPart().getCsize());
                            partMap.put("XHPH", cVaTreeNode.getPart().getXhph());
                            partMap.put("JSTJ", cVaTreeNode.getPart().getJstj());
                            partMap.put("GG", cVaTreeNode.getPart().getGg());
                            partMap.put("DW", cVaTreeNode.getDw());
                            partMap.put("DW2", cVaTreeNode.getDw2());
                            partMap.put("bzh", cVaTreeNode.getBzh());
                            partMap.put("dataType", cVaTreeNode.getDataType());
                            if(vaTreeNode.getFlag().contains("SJZYK")){
                                partMap.put("materialGg", vaTreeNode.getPart().getGg());//材料规格
                                partMap.put("materialLb", vaTreeNode.getPart().getDataType());//材料类别
                                partMap.put("materialPh", vaTreeNode.getPart().getCl());//材料牌号
                                partMap.put("materialBzh", vaTreeNode.getPart().getBzh());//材料标准号
                            }else{
                                partMap.put("materialGg", vaTreeNode.getPart().getGg());//材料编号
                                partMap.put("materialLb", vaTreeNode.getPart().getDataType());//材料名称
                                partMap.put("materialPh", vaTreeNode.getPart().getXhph());//材料牌号
                                partMap.put("materialBzh", vaTreeNode.getPart().getJstj());//材料标准号
                            }

                            if ("C".equals(cVaTreeNode.getPart().getZcmark())) {
                                partMap.put("ZCMARK", "C");
                            } else if ("Z".equals(cVaTreeNode.getPart().getZcmark())) {
                                partMap.put("ZCMARK", "Z");
                            }
                            parts.add(partMap);
                            v = cVaTreeNode;
                        }
                        if(cVaTreeNodes.size()%2==1){
                            if(!vaTreeNode.isUsed()){
                                vaTreeNode.setUsed(false);
                            }else{
                                vaTreeNode.setUsed(true);
                            }
                        }else if(cVaTreeNodes.size()%2==0){
                            if(!vaTreeNode.isUsed()){
                                vaTreeNode.setUsed(true);
                            }else{
                                vaTreeNode.setUsed(false);
                            }
                        }
                    } else {
                        Enumeration<VaTreeNode> fittings = tree.getRoot().depthFirstEnumeration();
                        while (fittings.hasMoreElements()) {
                            VaTreeNode treeNode = (VaTreeNode) fittings.nextElement();
                            if (treeNode instanceof DpPartNode) {
                                if (vaTreeNode.getOccpath().equals(treeNode.getOccpath())) {
                                    JOptionPane.showMessageDialog(this, "参装件已添加!");
                                    return;
                                }
                            }
                        }

                        DefaultTreeModel treeModel = (DefaultTreeModel) (tree.getModel());
                        if (checkUsed(vaTreeNode)) {
                            isComplete = "true";
                        }
                        if (vaTreeNode.isUsed()) {
                            VaTreeNode CNode = new VaTreeNode(vaTreeNode.clone());
                            CNode.getPart().setZcmark("C");
                            vaTreeNode.getPart().setZcmark("C");
                            treeModel.insertNodeInto(this.covertTreeNodeToPartNode((VaTreeNode) vaTreeNode), node, 0);
                        } else {
                            VaTreeNode ZNode = new VaTreeNode(vaTreeNode.clone());
                            ZNode.getPart().setZcmark("Z");
                            vaTreeNode.getPart().setZcmark("Z");
                            treeModel.insertNodeInto(this.covertTreeNodeToPartNode((VaTreeNode) vaTreeNode), node, 0);
                        }
                        System.out.println("vaaa=============" + vaTreeNode.getPart().getZcmark());
                        vaTreeNode.setSelected(true);
                        boolean isSamePart = false;

//				for (Map<String, String> map : parts) {
//					String key = "oid";
//					String value = String.valueOf(vaTreeNode.getPart().getOid());
//
//					if (map.containsKey(key) && map.containsValue(value)) {
//						map.put("occpath", map.get("occpath") + "," + vaTreeNode.getOccpath());
//						map.put("occId", map.get("occId") + "," + vaTreeNode.getOccId());
//						map.put("useCount", String.valueOf(Integer.parseInt(map.get("useCount")) + 1));
//
//						isSamePart = true;
//						break;
//					}
//				}
//				if (!isSamePart) {
                        Map<String, String> partMap = new HashMap<String, String>();
                        partMap.put("partNumber", vaTreeNode.getPart().getNumber());
                        partMap.put("oid", String.valueOf(vaTreeNode.getPart().getOid()));
                        partMap.put("occId", vaTreeNode.getOccId());
                        partMap.put("occpath", vaTreeNode.getOccpath());
                        partMap.put("partName", vaTreeNode.getPart().getName());
                        partMap.put("materialNumber",vaTreeNode.getPart().getMaterialNumber());//材料编号
                        partMap.put("materialName",vaTreeNode.getPart().getMaterialName());//材料名称
                        partMap.put("materialBrand",vaTreeNode.getPart().getMaterialBrand());//材料牌号
                        partMap.put("materialCrision",vaTreeNode.getPart().getMaterialCrision());//材料标准号
                        partMap.put("material", "");
                        partMap.put("dutu", "");
                        partMap.put("remark", "");
                        partMap.put("useCount", "1");

                        partMap.put("MTYPE", vaTreeNode.getPart().getMtype());
                        partMap.put("CSIZE", vaTreeNode.getPart().getCsize());
                        partMap.put("XHPH", vaTreeNode.getPart().getXhph());
                        partMap.put("JSTJ", vaTreeNode.getPart().getJstj());
                        partMap.put("GG", vaTreeNode.getPart().getGg());
                        partMap.put("DW", vaTreeNode.getDw());
                        partMap.put("DW2", vaTreeNode.getDw2());
                        partMap.put("bzh", vaTreeNode.getBzh());
                        partMap.put("dataType", vaTreeNode.getDataType());
                        if(vaTreeNode.getFlag() != null){
                            if (vaTreeNode.getFlag().contains("SJZYK")) {
                                partMap.put("materialGg", vaTreeNode.getPart().getGg());//材料规格
                                partMap.put("materialLb", vaTreeNode.getPart().getDataType());//材料类别
                                partMap.put("materialPh", vaTreeNode.getPart().getCl());//材料牌号
                                partMap.put("materialBzh", vaTreeNode.getPart().getBzh());//材料标准号
                            } else {
                                partMap.put("materialGg", vaTreeNode.getPart().getGg());//材料编号
                                partMap.put("materialLb", vaTreeNode.getPart().getDataType());//材料名称
                                partMap.put("materialPh", vaTreeNode.getPart().getXhph());//材料牌号
                                partMap.put("materialBzh", vaTreeNode.getPart().getJstj());//材料标准号
                            }
                        }
                        if ("C".equals(vaTreeNode.getPart().getZcmark())) {
                            partMap.put("ZCMARK", "C");
                        } else if ("Z".equals(vaTreeNode.getPart().getZcmark())) {
                            partMap.put("ZCMARK", "Z");
                        }
                        parts.add(partMap);
//				}
                        v = vaTreeNode;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "没有部件可装配，请先复制!");
            }
            System.out.println("v=============" + v.getPart().getZcmark());
            String stepOid = step.getStep().getOid();
            String paceOid = ((DpPaceNode) node).getPace().getOid();
            ParticipatePartUtil.addParticipateParts(stepOid, paceOid, parts, isComplete);
            DpPaceNode pace = ((DpPaceNode) node);
            // 刚刚复制过来的参装件，将其所在的工步节点取消选中
            pace.setSelected(false);
            DpStepNode stepNode = (DpStepNode) pace.getParent();
            FittingsDistributionFrame.addParts(stepOid, paceOid);//参装件写入工步XML
            System.out.println("v=============" + v.getPart().getZcmark());
            tree.updateUI();
            System.out.println("v111=============" + v.getPart().getZcmark());
            Container jc = tree.getParent();
            while (jc != null) {
                if (jc instanceof DpTreePanel) {
                    dpPanel = (DpTreePanel) jc;
                    break;
                }
                jc = jc.getParent();
            }

//			if(dpPanel != null){
//				dpPanel.pviewAction();
//			}
            VaClipboard.clipboard2.clear();
        } else if ((node instanceof DpStepNode && node.getChildCount() == 0) || (node instanceof DpStepNode && node.getChildCount() != 0 && !(node.getChildAt(0) instanceof DpPaceNode))) {
            DpTreePanel dpPanel = null;
            Vector<Map<String, String>> parts = new Vector<Map<String, String>>();

            DpStepNode step = (DpStepNode) node;
            String isComplete = "false";
            VaTreeNode v = null;
            if (VaClipboard.clipboard2.size() != 0) {
                for (VaTreeNode vaTreeNode : VaClipboard.clipboard2) {//工序循环
                    List<VaTreeNode> cVaTreeNodes = getCVatreeNodes(vaTreeNode);
                    if (cVaTreeNodes != null && cVaTreeNodes.size() > 0) {
                        for (VaTreeNode cVaTreeNode : cVaTreeNodes) {
                            Enumeration<VaTreeNode> fittings = tree.getRoot().depthFirstEnumeration();
                            while (fittings.hasMoreElements()) {
                                VaTreeNode treeNode = fittings.nextElement();
                                if (treeNode instanceof DpPartNode) {
                                    if (cVaTreeNode.getOccpath().equals(treeNode.getOccpath())) {
                                        JOptionPane.showMessageDialog(this, "参装件已添加!");
                                        return;
                                    }
                                }
                            }

                            DefaultTreeModel treeModel = (DefaultTreeModel) (tree.getModel());
                            if (checkUsed(cVaTreeNode)) {
                                isComplete = "true";
                            }
                            if (cVaTreeNode.isUsed()) {
                                VaTreeNode CNode = new VaTreeNode(cVaTreeNode.clone());
                                CNode.getPart().setZcmark("C");
                                cVaTreeNode.getPart().setZcmark("C");
                                treeModel.insertNodeInto(this.covertTreeNodeToPartNode(cVaTreeNode), node, 0);
                            } else {
                                VaTreeNode ZNode = new VaTreeNode(cVaTreeNode.clone());
                                ZNode.getPart().setZcmark("Z");
                                cVaTreeNode.getPart().setZcmark("Z");
                                treeModel.insertNodeInto(this.covertTreeNodeToPartNode(cVaTreeNode), node, 0);
                            }
                            cVaTreeNode.setSelected(true);
                            Map<String, String> partMap = new HashMap<String, String>();
                            partMap.put("partNumber", cVaTreeNode.getPart().getNumber());
                            partMap.put("oid", String.valueOf(cVaTreeNode.getPart().getOid()));
                            partMap.put("occId", cVaTreeNode.getOccId());
                            partMap.put("occpath", cVaTreeNode.getOccpath());
                            partMap.put("partName", cVaTreeNode.getPart().getName());
                            partMap.put("materialNumber",cVaTreeNode.getPart().getMaterialNumber());//材料编号
                            partMap.put("materialName",cVaTreeNode.getPart().getMaterialName());//材料名称
                            partMap.put("materialBrand",cVaTreeNode.getPart().getMaterialBrand());//材料牌号
                            partMap.put("materialCrision",cVaTreeNode.getPart().getMaterialCrision());//材料标准号
                            partMap.put("material", "");
                            partMap.put("dutu", "");
                            partMap.put("remark", "");
                            partMap.put("useCount", "1");

                            partMap.put("MTYPE", cVaTreeNode.getPart().getMtype());
                            partMap.put("CSIZE", cVaTreeNode.getPart().getCsize());
                            partMap.put("XHPH", cVaTreeNode.getPart().getXhph());
                            partMap.put("JSTJ", cVaTreeNode.getPart().getJstj());
                            partMap.put("GG", cVaTreeNode.getPart().getGg());
                            partMap.put("DW", cVaTreeNode.getDw());
                            partMap.put("DW2", cVaTreeNode.getDw2());
                            if(vaTreeNode.getFlag().contains("SJZYK")){
                                partMap.put("materialGg", vaTreeNode.getPart().getGg());//材料规格
                                partMap.put("materialLb", vaTreeNode.getPart().getDataType());//材料类别
                                partMap.put("materialPh", vaTreeNode.getPart().getCl());//材料牌号
                                partMap.put("materialBzh", vaTreeNode.getPart().getBzh());//材料标准号
                            }else{
                                partMap.put("materialGg", vaTreeNode.getPart().getGg());//材料编号
                                partMap.put("materialLb", vaTreeNode.getPart().getDataType());//材料名称
                                partMap.put("materialPh", vaTreeNode.getPart().getXhph());//材料牌号
                                partMap.put("materialBzh", vaTreeNode.getPart().getJstj());//材料标准号
                            }
                            if ("C".equals(cVaTreeNode.getPart().getZcmark())) {
                                partMap.put("ZCMARK", "C");
                            } else if ("Z".equals(cVaTreeNode.getPart().getZcmark())) {
                                partMap.put("ZCMARK", "Z");
                            }
                            parts.add(partMap);
                            v = cVaTreeNode;
                        }
                        if(cVaTreeNodes.size()%2==1){
                            if(!vaTreeNode.isUsed()){
                                vaTreeNode.setUsed(false);
                            }else{
                                vaTreeNode.setUsed(true);
                            }
                        }else if(cVaTreeNodes.size()%2==0){
                            if(!vaTreeNode.isUsed()){
                                vaTreeNode.setUsed(true);
                            }else{
                                vaTreeNode.setUsed(false);
                            }
                        }

                    } else {
                        Enumeration<VaTreeNode> fittings = tree.getRoot().depthFirstEnumeration();
                        while (fittings.hasMoreElements()) {
                            VaTreeNode treeNode = fittings.nextElement();
                            if (treeNode instanceof DpPartNode) {
                                if (vaTreeNode.getOccpath().equals(treeNode.getOccpath())) {
                                    JOptionPane.showMessageDialog(this, "参装件已添加!");
                                    return;
                                }
                            }
                        }

                        DefaultTreeModel treeModel = (DefaultTreeModel) (tree.getModel());
                        if (checkUsed(vaTreeNode)) {
                            isComplete = "true";
                        }
                        if (vaTreeNode.isUsed()) {
                            VaTreeNode CNode = new VaTreeNode(vaTreeNode.clone());
                            CNode.getPart().setZcmark("C");
                            vaTreeNode.getPart().setZcmark("C");
                            treeModel.insertNodeInto(this.covertTreeNodeToPartNode((VaTreeNode) vaTreeNode), node, 0);
                        } else {
                            VaTreeNode ZNode = new VaTreeNode(vaTreeNode.clone());
                            ZNode.getPart().setZcmark("Z");
                            vaTreeNode.getPart().setZcmark("Z");
                            treeModel.insertNodeInto(this.covertTreeNodeToPartNode((VaTreeNode) vaTreeNode), node, 0);
                        }
                        System.out.println("vaaa=============" + vaTreeNode.getPart().getZcmark());
                        vaTreeNode.setSelected(true);

                        Map<String, String> partMap = new HashMap<String, String>();
                        partMap.put("partNumber", vaTreeNode.getPart().getNumber());
                        partMap.put("oid", String.valueOf(vaTreeNode.getPart().getOid()));
                        partMap.put("occId", vaTreeNode.getOccId());
                        partMap.put("occpath", vaTreeNode.getOccpath());
                        partMap.put("partName", vaTreeNode.getPart().getName());
                        partMap.put("materialNumber",vaTreeNode.getPart().getMaterialNumber());//材料编号
                        partMap.put("materialName",vaTreeNode.getPart().getMaterialName());//材料名称
                        partMap.put("materialBrand",vaTreeNode.getPart().getMaterialBrand());//材料牌号
                        partMap.put("materialCrision",vaTreeNode.getPart().getMaterialCrision());//材料标准号
                        partMap.put("material", "");
                        partMap.put("dutu", "");
                        partMap.put("remark", "");
                        partMap.put("useCount", "1");

                        partMap.put("MTYPE", vaTreeNode.getPart().getMtype());
                        partMap.put("CSIZE", vaTreeNode.getPart().getCsize());
                        partMap.put("XHPH", vaTreeNode.getPart().getXhph());
                        partMap.put("JSTJ", vaTreeNode.getPart().getJstj());
                        partMap.put("GG", vaTreeNode.getPart().getGg());
                        partMap.put("DW", vaTreeNode.getDw());
                        partMap.put("DW2", vaTreeNode.getDw2());
                        partMap.put("bzh", vaTreeNode.getBzh());
                        partMap.put("dataType", vaTreeNode.getDataType());
                        if(vaTreeNode.getFlag() != null){
                            if(vaTreeNode.getFlag().contains("SJZYK")){
                                partMap.put("materialGg", vaTreeNode.getPart().getGg());//材料规格
                                partMap.put("materialLb", vaTreeNode.getPart().getDataType());//材料类别
                                partMap.put("materialPh", vaTreeNode.getPart().getCl());//材料牌号
                                partMap.put("materialBzh", vaTreeNode.getPart().getBzh());//材料标准号
                            }else{
                                partMap.put("materialGg", vaTreeNode.getPart().getGg());//材料编号
                                partMap.put("materialLb", vaTreeNode.getPart().getDataType());//材料名称
                                partMap.put("materialPh", vaTreeNode.getPart().getXhph());//材料牌号
                                partMap.put("materialBzh", vaTreeNode.getPart().getJstj());//材料标准号
                            }
                        }else{
                            partMap.put("materialGg", vaTreeNode.getPart().getGg());//材料编号
                            partMap.put("materialLb", vaTreeNode.getPart().getMtype());//材料类别
                            partMap.put("materialPh", vaTreeNode.getPart().getXhph());//材料牌号
                            partMap.put("materialBzh", vaTreeNode.getPart().getJstj());//材料标准号
                        }
                        if ("C".equals(vaTreeNode.getPart().getZcmark())) {
                            partMap.put("ZCMARK", "C");
                        } else if ("Z".equals(vaTreeNode.getPart().getZcmark())) {
                            partMap.put("ZCMARK", "Z");
                        }
                        parts.add(partMap);
//				}
                        v = vaTreeNode;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "没有部件可装配，请先复制!");
            }
//            System.out.println("v============="+v.getPart().getZcmark());
            String stepOid = step.getStep().getOid();
            //将添加的零件写入至工艺的xml主内容中
            ParticipatePartUtil.addParticipateParts(stepOid, parts, isComplete);
            //DpPaceNode pace = ((DpPaceNode) node);
            // 刚刚复制过来的参装件，将其所在的工步节点取消选中
            step.setSelected(false);
            //DpStepNode stepNode = (DpStepNode) pace.getParent();

            FittingsDistributionFrame.addParts(stepOid);

            System.out.println("v=============" + v.getPart().getZcmark());
            tree.updateUI();
            System.out.println("v111=============" + v.getPart().getZcmark());
            Container jc = tree.getParent();
            while (jc != null) {
                if (jc instanceof DpTreePanel) {
                    dpPanel = (DpTreePanel) jc;
                    break;
                }
                jc = jc.getParent();
            }

//			if(dpPanel != null){
//				dpPanel.pviewAction();
//			}
            VaClipboard.clipboard2.clear();

        } else if (node instanceof DpStepNode && node.getChildCount() != 0 && (node.getChildAt(0) instanceof DpPaceNode)) {
            JOptionPane.showMessageDialog(null, "该工序下有工步，无法进行拆装！");
        }
    }

    private DpPartNode covertTreeNodeToPartNode(VaTreeNode pauseNode) {
        VaLightPart vaLightPart = (VaLightPart) pauseNode.getPart().clone();
        VaTreeNode treenode = new VaTreeNode(new VaEPartInstance(vaLightPart));
        DpPartNode partNode = new DpPartNode(treenode.getUserObject());
        partNode.set_cadObject(pauseNode.get_cadObject());
        partNode.set_index(pauseNode.get_index());
        partNode.set_LockedForCAD(pauseNode.get_LockedForCAD());
        partNode.set_pviewComponentInstance(pauseNode.get_pviewComponentInstance());
        partNode.set_pviewComponentNode(pauseNode.get_pviewComponentNode());
        partNode.set_pviewShapeInstance(pauseNode.get_pviewShapeInstance());
        partNode.set_wsInfo(pauseNode.get_wsInfo());
        partNode.setAllowsChildren(pauseNode.getAllowsChildren());
        partNode.setBaseMatrix(pauseNode.getBaseMatrix());
        partNode.setBboxes(pauseNode.getBboxes());
        partNode.setDiode(pauseNode.getDiode());
        partNode.setFktGrp(pauseNode.getFktGrp());
        partNode.setInfoTxt(pauseNode.getInfoTxt());
        partNode.setInstanceIdentifier(pauseNode.getInstanceIdentifier());
        partNode.setLocChange(pauseNode.getLocChange());
        partNode.setMatrix(pauseNode.getMatrix());
        partNode.setNodeType(pauseNode.getNodeType());
        partNode.setOccId(pauseNode.getOccId());
//		partNode.setOccpath(treenode.getOccpath());
        partNode.setPathFromCI(pauseNode.getPathFromCI());
        partNode.setRelative_matrix(pauseNode.getRelative_matrix());
        partNode.setRelativeMatrix(pauseNode.getRelativeMatrix());
        partNode.setDataType(pauseNode.getDataType());
        partNode.setBzh(pauseNode.getBzh());
//        if (!pauseNode.isUsed()) {
//            partNode.getPart().setZcmark("Z");
//            partNode.setZcmark("Z");
//            pauseNode.setUsed(false);
//        } else {
//            partNode.getPart().setZcmark("C");
//            partNode.setZcmark("C");
//            pauseNode.setUsed(true);
//        }
        partNode.getPart().setZcmark(currentOperation);
        partNode.setZcmark(currentOperation);

        return partNode;
    }

    public static List<VaTreeNode> getCVatreeNodes(VaTreeNode packedNode) {
        List<VaTreeNode> cVaTreeNodes = null;
        String[] occIds = packedNode.getOccId().split(",");
        if (occIds.length > 1) {
            cVaTreeNodes = new ArrayList<VaTreeNode>();
            String[] occPaths = packedNode.getOccpath().split(",");
            Map<String, Integer> map = VaTree.getNodeUsedMap();
            VaTreeNode unpackedNode;
            VaLightPart vaLightPart = (VaLightPart) packedNode.getPart().clone();
            vaLightPart.setAmount(1);
            for (int j = 0; j < occIds.length; j++) {
                unpackedNode = new VaTreeNode(new VaEPartInstance(vaLightPart));
                unpackedNode.setOccId(occIds[j]);
                unpackedNode.setOccpath(occPaths[j]);
                unpackedNode.setDataType(packedNode.getDataType());
                unpackedNode.setBzh(packedNode.getBzh());
                unpackedNode.setJxxndjhyd(packedNode.getJxxndjhyd());
                unpackedNode.setBmcl(packedNode.getBmcl());
                unpackedNode.setRcl(packedNode.getRcl());
                unpackedNode.setCpxs(packedNode.getCpxs());
                unpackedNode.setCpdj(packedNode.getCpdj());
                unpackedNode.setBnxs(packedNode.getBnxs());
                unpackedNode.setSfjk(packedNode.getSfjk());
                unpackedNode.setComment(packedNode.getComment());
                unpackedNode.setXhph(packedNode.getXhph());
                unpackedNode.setGg(packedNode.getGg());
                unpackedNode.setDw2(packedNode.getDw2());
                unpackedNode.setDw(packedNode.getDw());
                unpackedNode.setFlag(packedNode.getFlag());
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
                cVaTreeNodes.add(unpackedNode);
            }
        }
        return cVaTreeNodes;
    }
}

package com.glaway.mpm.visual.view.tree;

import com.glaway.mpm.qmIntf.fittingTool.view.FittingsDistributionFrame;
import com.glaway.mpm.util.*;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.visual.bean.VaEPartInstance;
import com.glaway.mpm.visual.bean.VaLightPart;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.VaContext;
import com.ptc.pview.pvkapp.Instance;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.*;

public class VaTree extends JTree implements Cloneable {
    private static final long serialVersionUID = 4607849422784307260L;
    private VaLogger log = VaLogger.getLogger();
    private VaTreeNode root = null;
    private HashMap<Instance, VaTreeNode> instanceDICNodeMapping = new HashMap<Instance, VaTreeNode>();
    private boolean showTooltip = false;
    private ToolTipManager ttm;
    private int stopLevel = 1;
    private List<List<String>> packageList = new ArrayList<List<String>>();
    private VaTree linkedEBomTree;
    private VaTree linkedProcessEBomTree;
    private VaTreeNode partRoot;
    private static Map<String, String> pbomMap = new HashMap<String, String>();
    private static Map<Long, String> occMap = new HashMap<Long, String>();
    private Map<String, Double> partCountMap;

    public VaTreeNode getPartRoot() {
        return partRoot;
    }

    public void setPartRoot(VaTreeNode partRoot) {
        this.partRoot = partRoot;
    }

    public VaTree getLinkedEBomTree() {
        return linkedEBomTree;
    }

    public void setLinkedEBomTree(VaTree linkedEBomTree) {
        this.linkedEBomTree = linkedEBomTree;
    }

    public VaTree getLinkedProcessEBomTree() {
        return linkedProcessEBomTree;
    }

    public void setLinkedProcessEBomTree(VaTree linkedProcessEBomTree) {
        this.linkedProcessEBomTree = linkedProcessEBomTree;
    }

    public VaTree copyTree() {
        try {
            return (VaTree) this.clone();
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public VaTree(VaTreeNode node) {
        super();
        if (node != null)
            root = node;
        ((DefaultTreeModel) getModel()).setRoot(root);

        setCellRenderer(new VaTreeRenderer());
        addMouseListener(new VaTreeNodeMouseAdapter(this));
        getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        // addTreeWillExpandListener(new DICTreeWillExpandListener());

        // addMouseListener(new DICMouseAdapter(this));
        // addTreeSelectionListener(this);

        setOpaque(false);
        setRowHeight(16);

        ttm = ToolTipManager.sharedInstance();
        ttm.setDismissDelay(10000);// pref
        ttm.setInitialDelay(100);// pref
        ttm.setReshowDelay(50);
    }

    public void initTree(String pNum, String vName, String rootType) {
        try {
            if (rootType == "EBOM") {
                buildPBOMTree(buildTreePbom());
            } else if (rootType == "ZPBOM") {
      		List<Element>  list = buildTreePbom();
                buildTreeForFitting(list);
            } else if (rootType == "FPBOM") {
//                buildPBOMTree2(buildTreePbom(), rootType);
                buildFPOMTree(buildTreePbom(), rootType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建FPOM树
     * @param list
     * @param rootType
     */
    private void buildFPOMTree(List<Element> list, String rootType){
        root.removeAllChildren();
        updateUI();
        String xmlPath = FittingsDistributionFrame.getPathString();
        Document document = null;

        document = XmlUtil.getDocument(new File(xmlPath));
        Element rootElement = document.getRootElement();
        Element technicsEle = XmlUtil.getElementsByName(rootElement, "QMFawTechnicsInfo").get(0);
        List<Element> stepElements = technicsEle.selectNodes("steps/QMProcedureInfo");
        List<Element> paceElements = technicsEle.selectNodes("steps/QMProcedureInfo/paces/QMProcedureInfo");
        Element gyde = XmlUtility.getTechnicsDEElement(technicsEle);
        Element clde = XmlUtility.getTechnicsCLDEElement(technicsEle);
        partCountMap = new HashMap<String, Double>();
        partCountMap = getPartZCount(stepElements,partCountMap);
        partCountMap = getPartZCount(paceElements,partCountMap);
        List<Element> gydeElements;
        List<Element> cldeElements;
        Map<String, ArrayList<VaTreeNode>> all = new HashMap<String, ArrayList<VaTreeNode>>();
        if(gyde != null){
            //获取从erp新增的物资
            gydeElements = XmlUtility.getTechnicsGYDENewPart(gyde);
            getFPOMTreeNodes(gydeElements,rootType,all,"ERP_NEW");
            //获取从erp匹配的物资
            gydeElements = XmlUtility.getTechnicsGYDEMatchPart(gyde);
            getFPOMTreeNodes(gydeElements,rootType,all,"ERP_MATCH");
            //获取从设计资源库新增的物资
            gydeElements = XmlUtility.getTechnicsSJZYKGYDENewPart(gyde);
            getFPOMTreeNodes(gydeElements,rootType,all,"SJZYK_NEW");
            //获取从设计资源库匹配的物资
            gydeElements = XmlUtility.getTechnicsSJZYKGYDEMatchPart(gyde);
            getFPOMTreeNodes(gydeElements,rootType,all,"SJZYK_MATCH");
            //获取零件节点主要材料定额
            cldeElements = XmlUtility.getTechnicsZYCLDE(clde);
            getFPOMTreeNodes(cldeElements,rootType,all,"LJZYCLDE_MATCH");
            //获取装配节点主要材料定额
            gydeElements = XmlUtility.getTechnicsZYCLDE(gyde);
            getFPOMTreeNodes(gydeElements,rootType,all,"ZPZYCLDE_MATCH");

            //构建节点
            addFPBOMChildNodes(all);
        }
        SwingUtil.expandAll(this);
        updateUI();
    }

    private void addFPBOMChildNodes(Map<String, ArrayList<VaTreeNode>> all) {
        ArrayList<VaTreeNode> nodes = all.get("标准紧固件");
        if(nodes != null){
            for (VaTreeNode node : nodes) {
                root.add(node);
            }
        }
        nodes = all.get("标准件");
        if(nodes != null){
            for (VaTreeNode node : nodes) {
                root.add(node);
            }
        }
        nodes = all.get("元器件");
        if(nodes != null){
            for (VaTreeNode node : nodes) {
                root.add(node);
            }
        }
        nodes = all.get("金属材料");
        if(nodes != null){
            for (VaTreeNode node : nodes) {
                root.add(node);
            }
        }
        nodes = all.get("非金属材料");
        if(nodes != null){
            for (VaTreeNode node : nodes) {
                root.add(node);
            }
        }
        nodes = all.get("复合材料");
        if(nodes != null){
            for (VaTreeNode node : nodes) {
                root.add(node);
            }
        }
        nodes = all.get("机电材料");
        if(nodes != null){
            for (VaTreeNode node : nodes) {
                root.add(node);
            }
        }
        nodes = all.get("火工品");
        if(nodes != null){
            for (VaTreeNode node : nodes) {
                root.add(node);
            }
        }
        nodes = all.get("劳防、文版用品");
        if(nodes != null){
        	for (VaTreeNode node : nodes) {
				root.add(node);
			}
        }
        nodes = all.get("零件主要材料定额");
        if(nodes != null){
            for (VaTreeNode node : nodes) {
                root.add(node);
            }
        }
        nodes = all.get("装配主要材料定额");
        if(nodes != null){
            for (VaTreeNode node : nodes) {
                root.add(node);
            }
        }
    }

    /**
     * 获取工序工步已参装物资的数量
     * @param stepElements
     * @param paceElements
     * @return
     */
    private Map<String, Double> getPartZCount(List<Element> elementList, Map<String, Double> partCountMap) {
        for (Element stepEle : elementList) {
            List<Element> partsEleList = stepEle.selectNodes("parts/QMPartInfo");
            for (Element partEle : partsEleList) {
                String partNumber = partEle.attributeValue("partNumber");
                String occId = partEle.attributeValue("occId");
                String key = partNumber + "_" + occId;
                String zcMark = partEle.attributeValue("ZCMARK");
                double amount = Double.valueOf(partEle.attributeValue("useCount"));
                if (partCountMap.containsKey(key)) {
                    if ("Z".equals(zcMark)) {
                        partCountMap.put(key, CommonUtil.addDouble(partCountMap.get(key), amount));
                    } else if ("C".equals(zcMark)) {
                        partCountMap.put(key, CommonUtil.subDouble(partCountMap.get(key), amount));
                    }
                } else {
                    if ("Z".equals(zcMark)) {
                        partCountMap.put(key, CommonUtil.addDouble(0, amount));
                    } else if ("C".equals(zcMark)) {
                        partCountMap.put(key, CommonUtil.subDouble(0, amount));
                    }
                }

            }
        }
        return partCountMap;
    }

    private void getFPOMTreeNodes(List<Element> gydeElements, String rootType, Map<String, ArrayList<VaTreeNode>> allMap, String type){
        if(gydeElements != null && gydeElements.size() > 0){
            for(Element gydeEle : gydeElements){
                if ("ERP_NEW".equals(type)) {
                    String tabType = gydeEle.attributeValue("tabType");
                    if(tabType !=null && !"".equals(tabType)){
                        if("电子元器件".equals(tabType)){
                            tabType = "元器件";
                        }
                        String chbm = gydeEle.attributeValue("chbm");
                        String chmc = gydeEle.attributeValue("chmc");
                        String dw = gydeEle.attributeValue("dw");
                        String dw2 = gydeEle.attributeValue("dw2");
                        String xhph = gydeEle.attributeValue("xhph");
                        if("标准紧固件".equals(tabType)){
                            xhph = gydeEle.attributeValue("cl");
                        }
                        String gg = gydeEle.attributeValue("gg");
                        if("机电材料".equals(tabType) && StringUtils.isEmpty(gg)){
                            gg = CMUtilToString(gydeEle.attributeValue("xhgg"));
                        }
                        String sccj = gydeEle.attributeValue("sccj");
                        String dcstxyq = gydeEle.attributeValue("dcstxyq");
                        String fzxs = gydeEle.attributeValue("fzxs");
                        String zldj = gydeEle.attributeValue("zldj");
                        String comment = gydeEle.attributeValue("comment");
                        String jstj = gydeEle.attributeValue("jstj");
                        String wh = gydeEle.attributeValue("wh");
                        String bzh = gydeEle.attributeValue("bzh");
                        if(!CommonUtil.isNull(jstj)){
                            bzh = jstj;
                        }else{
                            jstj = bzh;
                        }
                        String sl = CMUtilToString(gydeEle.attributeValue("sl"));
                        if("".equals(sl)){
                            sl = "1";
                        }
                        double gysl = Double.parseDouble(sl);
                        double zCount = gysl;
                        if(partCountMap.containsKey(chbm + "_" + chbm)){
                            zCount = CommonUtil.subDouble(gysl,partCountMap.get(chbm + "_" + chbm));
                        }
                        VaLightPart part = VaLightPart.newLightPart(chbm, chmc, "", 1, xhph, gg);
                        part.setAmount(gysl);
                        part.setzCount(zCount);
                        part.setDataType(tabType);
                        part.setRootType(rootType);
                        part.setJstj(jstj);
                        part.setBzh(bzh);
                        VaTreeNode childNode = new VaTreeNode(new VaEPartInstance(part));
                        childNode.setOccpath(chbm);
                        childNode.setOccId(chbm);
                        childNode.setDw(dw2);
                        childNode.setDw2(dw);
                        childNode.setXhph(xhph);
                        childNode.setGg(gg);
                        childNode.setSccj(sccj);
                        childNode.setDcstxyq(dcstxyq);
                        childNode.setFzxs(fzxs);
                        childNode.setZldj(zldj);
                        childNode.setJstj(jstj);
                        childNode.setDataType(tabType);
                        childNode.setComment(comment);
                        childNode.setzCount(zCount);
                        childNode.setWh(wh);
                        childNode.setBzh(bzh);
                        childNode.setcCount(CommonUtil.subDouble(gysl,zCount));
                        childNode.setFlag("GYDENEWPART");
                        if(childNode.getzCount() == 0){
                            childNode.setUsed(true);
                        }else{
                            childNode.setUsed(false);
                        }
                        ArrayList<VaTreeNode> val;
                        if(allMap.containsKey(tabType)){
                            val = allMap.get(tabType);
                            boolean isHas = false;
                            for(VaTreeNode treeNode : val){
                                if(treeNode.getPart().getNumber().equals(chbm)){
                                    isHas = true;
                                    double amount = CommonUtil.addDouble(treeNode.getPart().getAmount(), gysl);
                                    double zcount = amount;
                                    if(partCountMap.containsKey(chbm + "_" + chbm)){
                                        zcount = CommonUtil.subDouble(amount,partCountMap.get(chbm + "_" + chbm));
                                    }
                                    treeNode.getPart().setAmount(amount);
                                    treeNode.getPart().setzCount(zcount);
                                    treeNode.setzCount(zcount);
                                    treeNode.setcCount(CommonUtil.subDouble(amount,zcount));
                                    if(treeNode.getzCount() == 0){
                                        treeNode.setUsed(true);
                                    }else{
                                        treeNode.setUsed(false);
                                    }
                                    break;
                                }
                            }
                            if(!isHas){
                                val.add(childNode);
                            }
                        }else{
                            val = new ArrayList<VaTreeNode>();
                            val.add(childNode);
                            allMap.put(tabType, val);
                        }
                    }else{
                        String number = gydeEle.attributeValue("number");
                        String name = gydeEle.attributeValue("chmc");
                        String dw = gydeEle.attributeValue("dw");
                        String dw2 = gydeEle.attributeValue("dw2");
                        String xhph = gydeEle.attributeValue("xhph");
                        String gg = gydeEle.attributeValue("gg");
                        String sccj = gydeEle.attributeValue("sccj");
                        String dcstxyq = gydeEle.attributeValue("dcstxyq");
                        String fzxs = gydeEle.attributeValue("fzxs");
                        String zldj = gydeEle.attributeValue("zldj");
                        String comment = gydeEle.attributeValue("comment");
                        String jstj = gydeEle.attributeValue("jstj");
                        String wzlb = gydeEle.attributeValue("wzlb");
                        String dataType = getDataType(wzlb);
                        String sl = CMUtilToString(gydeEle.attributeValue("sl"));
                        double gysl = Double.parseDouble(sl);
                        double zCount = gysl;
                        if(partCountMap.containsKey(number + "_" + number)){
                            zCount = CommonUtil.subDouble(gysl,partCountMap.get(number + "_" + number));
                        }
                        VaLightPart part = VaLightPart.newLightPart(number, name, "", 1, xhph, gg);
                        part.setAmount(gysl);
                        part.setzCount(zCount);
                        part.setDataType(dataType);
                        part.setRootType(rootType);
                        part.setJstj(jstj);

                        VaTreeNode childNode = new VaTreeNode(new VaEPartInstance(part));
                        childNode.setOccpath(number);
                        childNode.setOccId(number);
                        childNode.setDw(dw);
                        childNode.setDw2(dw2);
                        childNode.setXhph(xhph);
                        childNode.setGg(gg);
                        childNode.setSccj(sccj);
                        childNode.setDcstxyq(dcstxyq);
                        childNode.setFzxs(fzxs);
                        childNode.setZldj(zldj);
                        childNode.setJstj(jstj);
                        childNode.setDataType(dataType);
                        childNode.setComment(comment);
                        childNode.setzCount(zCount);
                        childNode.setcCount(CommonUtil.subDouble(gysl,zCount));
                        childNode.setFlag("GYDENEWPART");
                        if(childNode.getzCount() == 0){
                            childNode.setUsed(true);
                        }else{
                            childNode.setUsed(false);
                        }
                        if ("标准件".equals(dataType)) {
                            dataType = "标准紧固件";
                        }
                        ArrayList<VaTreeNode> val;
                        if(allMap.containsKey(dataType)){
                            val = allMap.get(dataType);
                            boolean isHas = false;
                            for(VaTreeNode treeNode : val){
                                if(treeNode.getPart().getNumber().equals(number)){
                                    isHas = true;
                                    double amount = CommonUtil.addDouble(treeNode.getPart().getAmount(), gysl);
                                    double zcount = amount;
                                    if(partCountMap.containsKey(number + "_" + number)){
                                        zcount = CommonUtil.subDouble(amount,partCountMap.get(number + "_" + number));
                                    }
                                    treeNode.getPart().setAmount(amount);
                                    treeNode.getPart().setzCount(zcount);
                                    treeNode.setzCount(zcount);
                                    treeNode.setcCount(CommonUtil.subDouble(amount,zcount));
                                    if(treeNode.getzCount() == 0){
                                        treeNode.setUsed(true);
                                    }else{
                                        treeNode.setUsed(false);
                                    }
                                    break;
                                }
                            }
                            if(!isHas){
                                val.add(childNode);
                            }
                        }else{
                            val = new ArrayList<VaTreeNode>();
                            val.add(childNode);
                            allMap.put(dataType, val);
                        }
                    }
//                    root.add(childNode);
                } else if ("ERP_MATCH".equals(type)) {
                    String number = gydeEle.attributeValue("chbm");
                    String name = gydeEle.attributeValue("chmc");
                    String dw = gydeEle.attributeValue("dw");
                    String dw2 = gydeEle.attributeValue("dw2");
                    String xhph = gydeEle.attributeValue("xhph");
                    String gg = gydeEle.attributeValue("gg");
                    String sccj = gydeEle.attributeValue("sccj");
                    String dcstxyq = gydeEle.attributeValue("dcstxyq");
                    String fzxs = gydeEle.attributeValue("fzxs");
                    String zldj = gydeEle.attributeValue("zldj");
                    String comment = gydeEle.attributeValue("comment");
                    String wzlb = gydeEle.attributeValue("wzlb");
                    String dataType = getDataType(wzlb);
                    String sl = CMUtilToString(gydeEle.attributeValue("gyCount"));
                    double gysl = Double.parseDouble(sl);
                    double zCount = gysl;
                    if(partCountMap.containsKey(number + "_" + number)){
                        zCount = CommonUtil.subDouble(gysl,partCountMap.get(number + "_" + number));
                    }
                    VaLightPart part = VaLightPart.newLightPart(number, name, "", 1, xhph, gg);
                    part.setAmount(gysl);
                    part.setzCount(zCount);
                    part.setDataType(dataType);
                    part.setRootType(rootType);
                    VaTreeNode childNode = new VaTreeNode(new VaEPartInstance(part));
                    childNode.setOccpath(number);
                    childNode.setOccId(number);
                    childNode.setDw(dw);
                    childNode.setDw2(dw2);
                    childNode.setXhph(xhph);
                    childNode.setGg(gg);
                    childNode.setSccj(sccj);
                    childNode.setDcstxyq(dcstxyq);
                    childNode.setFzxs(fzxs);
                    childNode.setZldj(zldj);
                    childNode.setComment(comment);
                    childNode.setDataType(dataType);
                    childNode.setzCount(zCount);
                    childNode.setcCount(CommonUtil.subDouble(gysl,zCount));
                    childNode.setFlag("GYDEMATCHPART");
                    if(childNode.getzCount() == 0){
                        childNode.setUsed(true);
                    }else{
                        childNode.setUsed(false);
                    }
                    if ("标准件".equals(dataType)) {
                        dataType = "标准紧固件";
                    }
                    ArrayList<VaTreeNode> val;
                    if(allMap.containsKey(dataType)){
                        val = allMap.get(dataType);
                        boolean isHas = false;
                        for(VaTreeNode treeNode : val){
                            if(treeNode.getPart().getNumber().equals(number)){
                                isHas = true;
                                double amount = CommonUtil.addDouble(treeNode.getPart().getAmount(), gysl);
                                double zcount = amount;
                                if(partCountMap.containsKey(number + "_" + number)){
                                    zcount = CommonUtil.subDouble(amount,partCountMap.get(number + "_" + number));
                                }
                                treeNode.getPart().setAmount(amount);
                                treeNode.getPart().setzCount(zcount);
                                treeNode.setzCount(zcount);
                                treeNode.setcCount(CommonUtil.subDouble(amount,zcount));
                                if(treeNode.getzCount() == 0){
                                    treeNode.setUsed(true);
                                }else{
                                    treeNode.setUsed(false);
                                }
                                break;
                            }
                        }
                        if(!isHas){
                            val.add(childNode);
                        }
                    }else{
                        val = new ArrayList<VaTreeNode>();
                        val.add(childNode);
                        allMap.put(dataType, val);
                    }
//                    root.add(childNode);
                } else if ("SJZYK_NEW".equals(type)) {
                    String number = CMUtilToString(gydeEle.attributeValue("sjbm"));
                    String name = CMUtilToString(gydeEle.attributeValue("name"));
                    //零组件生产类型
                    String dataType = CMUtilToString(gydeEle.attributeValue("dataType"));
                    //标准号
                    String bzh = CMUtilToString(gydeEle.attributeValue("bzh"));
                    String jstj = CMUtilToString(gydeEle.attributeValue("jstj"));
                    if(!CommonUtil.isNull(jstj)){
                        bzh = jstj;
                    }else{
                        jstj = bzh;
                    }
                    //机械性能等级
                    String jxxndjhyd = CMUtilToString(gydeEle.attributeValue("jxxndjhyd"));
                    //表面处理
                    String bmcl = CMUtilToString(gydeEle.attributeValue("bmcl"));
                    //热处理
                    String rcl = CMUtilToString(gydeEle.attributeValue("rcl"));
                    //产品形式
                    String cpxs = CMUtilToString(gydeEle.attributeValue("cpxs"));
                    //产品等级
                    String cpdj = CMUtilToString(gydeEle.attributeValue("cpdj"));
                    //扳令形式
                    String bnxs = CMUtilToString(gydeEle.attributeValue("bnxs"));
                    //是否进口
                    String sfjk = CMUtilToString(gydeEle.attributeValue("sfjk"));
                    //备注
                    String comment = CMUtilToString(gydeEle.attributeValue("comment"));
                    //型号牌号
                    String xhph = CMUtilToString(gydeEle.attributeValue("xhph"));
                    //规格
                    String gg = CMUtilToString(gydeEle.attributeValue("gg"));
                    if("元器件".equals(dataType) && "".equals(gg)){
                        gg = CMUtilToString(gydeEle.attributeValue("xhgg"));
                    }
                    //单位
                    String dw2 = CMUtilToString(gydeEle.attributeValue("dw2"));
                    //主计量单位
                    String dw = CMUtilToString(gydeEle.attributeValue("dw"));
                    String cl = CMUtilToString(gydeEle.attributeValue("cl"));
                    String sl = CMUtilToString(gydeEle.attributeValue("gysl"));
                    double gysl = Double.parseDouble(sl);
                    double zCount = gysl;
                    if(partCountMap.containsKey(number + "_" + number)){
                        zCount = CommonUtil.subDouble(gysl,partCountMap.get(number + "_" + number));
                    }
                    VaLightPart part = VaLightPart.newLightPart(number, name, "", 1, xhph, gg, bzh, dataType);
                    part.setAmount(gysl);
                    part.setzCount(zCount);
                    part.setCl(cl);
                    part.setRootType(rootType);
                    part.setBzh(bzh);
                    part.setJstj(jstj);
                    VaTreeNode childNode = new VaTreeNode(new VaEPartInstance(part));
                    childNode.setOccpath(number);
                    childNode.setOccId(number);
                    childNode.setDataType(dataType);
                    childNode.setBzh(bzh);
                    childNode.setJxxndjhyd(jxxndjhyd);
                    childNode.setBmcl(bmcl);
                    childNode.setRcl(rcl);
                    childNode.setCpxs(cpxs);
                    childNode.setCpdj(cpdj);
                    childNode.setBnxs(bnxs);
                    childNode.setSfjk(sfjk);
                    childNode.setComment(comment);
                    childNode.setXhph(xhph);
                    childNode.setGg(gg);
                    childNode.setDw2(dw2);
                    childNode.setDw(dw);
                    childNode.setCl(cl);
                    childNode.setzCount(zCount);
                    childNode.setcCount(CommonUtil.subDouble(gysl,zCount));
                    childNode.setFlag("SJZYKGYDENEWPART");
                    if(childNode.getzCount() == 0){
                        childNode.setUsed(true);
                    }else{
                        childNode.setUsed(false);
                    }

                    if ("标准件".equals(dataType)) {
                        dataType = "标准紧固件";
                    }
                    ArrayList<VaTreeNode> val;
                    if(allMap.containsKey(dataType)){
                        val = allMap.get(dataType);
                        boolean isHas = false;
                        for(VaTreeNode treeNode : val){
                            if(treeNode.getPart().getNumber().equals(number)){
                                isHas = true;
                                double amount = CommonUtil.addDouble(treeNode.getPart().getAmount(), gysl);
                                double zcount = amount;
                                if(partCountMap.containsKey(number + "_" + number)){
                                    zcount = CommonUtil.subDouble(amount,partCountMap.get(number + "_" + number));
                                }
                                treeNode.getPart().setAmount(amount);
                                treeNode.getPart().setzCount(zcount);
                                treeNode.setzCount(zcount);
                                treeNode.setcCount(CommonUtil.subDouble(amount,zcount));
                                if(treeNode.getzCount() == 0){
                                    treeNode.setUsed(true);
                                }else{
                                    treeNode.setUsed(false);
                                }
                                break;
                            }
                        }
                        if(!isHas){
                            val.add(childNode);
                        }
                    }else{
                        val = new ArrayList<VaTreeNode>();
                        val.add(childNode);
                        allMap.put(dataType, val);
                    }
//                    root.add(childNode);
                } else if ("SJZYK_MATCH".equals(type)) {
                    String number = gydeEle.attributeValue("sjbm");
                    String name = gydeEle.attributeValue("name");
                    //零组件生产类型
                    String dataType = CMUtilToString(gydeEle.attributeValue("dataType"));
                    //标准号
                    String bzh = CMUtilToString(gydeEle.attributeValue("bzh"));

                    String jstj = CMUtilToString(gydeEle.attributeValue("jstj"));
                    if(CommonUtil.isNull(bzh)){
                    	bzh = jstj;
                    }else{
                    	jstj = bzh;
                    }
                    //机械性能等级
                    String jxxndjhyd = CMUtilToString(gydeEle.attributeValue("jxxndjhyd"));
                    //表面处理
                    String bmcl = CMUtilToString(gydeEle.attributeValue("bmcl"));
                    //热处理
                    String rcl = CMUtilToString(gydeEle.attributeValue("rcl"));
                    //产品形式
                    String cpxs = CMUtilToString(gydeEle.attributeValue("cpxs"));
                    //产品等级
                    String cpdj = CMUtilToString(gydeEle.attributeValue("cpdj"));
                    //扳令形式
                    String bnxs = CMUtilToString(gydeEle.attributeValue("bnxs"));
                    //是否进口
                    String sfjk = CMUtilToString(gydeEle.attributeValue("sfjk"));
                    //备注
                    String comment = CMUtilToString(gydeEle.attributeValue("comment"));
                    //型号牌号
                    String xhph = CMUtilToString(gydeEle.attributeValue("xhph"));
                    //规格
                    String gg = CMUtilToString(gydeEle.attributeValue("gg"));
                    if("元器件".equals(dataType) && "".equals(gg)){
                        gg = CMUtilToString(gydeEle.attributeValue("xhgg"));
                    }
                    //单位
                    String dw2 = CMUtilToString(gydeEle.attributeValue("dw2"));
                    //主计量单位
                    String dw = CMUtilToString(gydeEle.attributeValue("dw"));
                    String cl = CMUtilToString(gydeEle.attributeValue("cl"));
                    String sl = CMUtilToString(gydeEle.attributeValue("gysl"));
                    double gysl = Double.parseDouble(sl);
                    double zCount = gysl;
                    if(partCountMap.containsKey(number + "_" + number)){
                        zCount = CommonUtil.subDouble(gysl,partCountMap.get(number + "_" + number));
                    }
                    VaLightPart part = VaLightPart.newLightPart(number, name, "", 1, xhph, gg, bzh, dataType);
                    part.setAmount(gysl);
                    part.setzCount(zCount);
                    part.setCl(cl);
                    part.setRootType(rootType);
                    part.setBzh(bzh);
                    part.setJstj(jstj);

                    VaTreeNode childNode = new VaTreeNode(new VaEPartInstance(part));
                    childNode.setOccpath(number);
                    childNode.setOccId(number);
                    childNode.setDataType(dataType);
                    childNode.setBzh(bzh);
                    childNode.setJxxndjhyd(jxxndjhyd);
                    childNode.setBmcl(bmcl);
                    childNode.setRcl(rcl);
                    childNode.setCpxs(cpxs);
                    childNode.setCpdj(cpdj);
                    childNode.setBnxs(bnxs);
                    childNode.setSfjk(sfjk);
                    childNode.setComment(comment);
                    childNode.setXhph(xhph);
                    childNode.setGg(gg);
                    childNode.setDw2(dw2);
                    childNode.setDw(dw);
                    childNode.setCl(cl);
                    childNode.setzCount(zCount);
                    childNode.setcCount(CommonUtil.subDouble(gysl,zCount));
                    childNode.setFlag("SJZYKGYDEMATCHPART");
                    if(childNode.getzCount() == 0){
                        childNode.setUsed(true);
                    }else{
                        childNode.setUsed(false);
                    }

                    if ("标准件".equals(dataType)) {
                        dataType = "标准紧固件";
                    }
                    ArrayList<VaTreeNode> val;
                    if(allMap.containsKey(dataType)){
                        val = allMap.get(dataType);
                        boolean isHas = false;
                        for(VaTreeNode treeNode : val){
                            if(treeNode.getPart().getNumber().equals(number)){
                                isHas = true;
                                double amount = CommonUtil.addDouble(treeNode.getPart().getAmount(), gysl);
                                double zcount = amount;
                                if(partCountMap.containsKey(number + "_" + number)){
                                    zcount = CommonUtil.subDouble(amount,partCountMap.get(number + "_" + number));
                                }
                                treeNode.getPart().setAmount(amount);
                                treeNode.getPart().setzCount(zcount);
                                treeNode.setzCount(zcount);
                                treeNode.setcCount(CommonUtil.subDouble(amount,zcount));
                                if(treeNode.getzCount() == 0){
                                    treeNode.setUsed(true);
                                }else{
                                    treeNode.setUsed(false);
                                }
                                break;
                            }
                        }
                        if(!isHas){
                            val.add(childNode);
                        }
                    }else{
                        val = new ArrayList<VaTreeNode>();
                        val.add(childNode);
                        allMap.put(dataType, val);
                    }
//                    root.add(childNode);
                } else if ("LJZYCLDE_MATCH".equals(type)) {
                    String number = gydeEle.attributeValue("chbm");
                    String name = gydeEle.attributeValue("chmc");
                    String dw = gydeEle.attributeValue("zjldw");
                    String dw2 = gydeEle.attributeValue("dw");
                    String jstj = gydeEle.attributeValue("jstj");
                    String bzh = gydeEle.attributeValue("bzh");
                    if(CommonUtil.isNull(bzh)){
                        bzh = jstj;
                    }
                    String gg = gydeEle.attributeValue("gg");
                    String sccj = gydeEle.attributeValue("sccj");
                    String zldj = gydeEle.attributeValue("zldj");
                    String fzxs = gydeEle.attributeValue("fzxs");
                    String dcstxyq = gydeEle.attributeValue("dcstxyq");
                    String comment = gydeEle.attributeValue("comment");
                    String tabType = gydeEle.attributeValue("tabType");
                    String xhph = gydeEle.attributeValue("xhph");
                    if("标准紧固件".equals(tabType)){
                        xhph = gydeEle.attributeValue("cl");
                    }
                    if("机电材料".equals(tabType) && StringUtils.isEmpty(gg)){
                        gg = CMUtilToString(gydeEle.attributeValue("xhgg"));
                    }
                    String dataType = "零件主要材料定额";
                    String wh = "";
                    if(tabType!=null && !"".equals(tabType)) {
                        if ("电子元器件".equals(tabType)) {
                            tabType = "元器件";
                            wh = gydeEle.attributeValue("wh");
                        }
                        dataType = tabType;
                    }
                    String sl = CMUtilToString(gydeEle.attributeValue("sl"));
                    double gysl = Double.parseDouble(sl);
                    double zCount = gysl;
                    if(partCountMap.containsKey(number + "_" + number)){
                        zCount = CommonUtil.subDouble(gysl,partCountMap.get(number + "_" + number));
                    }
                    VaLightPart part = VaLightPart.newLightPart(number, name, "", 1, xhph, gg);
                    part.setAmount(gysl);
                    part.setzCount(zCount);
                    part.setDataType(dataType);
                    part.setRootType(rootType);
                    part.setBzh(bzh);
                    part.setJstj(jstj);
                    VaTreeNode childNode = new VaTreeNode(new VaEPartInstance(part));
                    childNode.setOccpath(number);
                    childNode.setOccId(number);
                    childNode.setDw(dw);
                    childNode.setDw2(dw2);
                    childNode.setXhph(xhph);
                    childNode.setGg(gg);
                    childNode.setSccj(sccj);
                    childNode.setDcstxyq(dcstxyq);
                    childNode.setFzxs(fzxs);
                    childNode.setZldj(zldj);
                    childNode.setJstj(jstj);
                    childNode.setBzh(bzh);

                    childNode.setComment(comment);
                    childNode.setDataType(dataType);
                    childNode.setzCount(zCount);
                    childNode.setWh(wh);
                    childNode.setcCount(CommonUtil.subDouble(gysl,zCount));
                    childNode.setFlag("LJZYCLDENEWPART");
                    if(childNode.getzCount() == 0){
                        childNode.setUsed(true);
                    }else{
                        childNode.setUsed(false);
                    }
                    ArrayList<VaTreeNode> val;
                    if(allMap.containsKey(dataType)){
                        val = allMap.get(dataType);
                        boolean isHas = false;
                        for(VaTreeNode treeNode : val){
                            if(treeNode.getPart().getNumber().equals(number)){
                                isHas = true;
                                double amount = CommonUtil.addDouble(treeNode.getPart().getAmount(), gysl);
                                double zcount = amount;
                                if(partCountMap.containsKey(number + "_" + number)){
                                    zcount = CommonUtil.subDouble(amount,partCountMap.get(number + "_" + number));
                                }
                                treeNode.getPart().setAmount(amount);
                                treeNode.getPart().setzCount(zcount);
                                treeNode.setzCount(zcount);
                                treeNode.setcCount(CommonUtil.subDouble(amount,zcount));
                                if(treeNode.getzCount() == 0){
                                    treeNode.setUsed(true);
                                }else{
                                    treeNode.setUsed(false);
                                }
                                break;
                            }
                        }
                        if(!isHas){
                            val.add(childNode);
                        }
                    }else{
                        val = new ArrayList<VaTreeNode>();
                        val.add(childNode);
                        allMap.put(dataType, val);
                    }
                } else if ("ZPZYCLDE_MATCH".equals(type)) {
                    String number = gydeEle.attributeValue("chbm");
                    String name = gydeEle.attributeValue("chmc");
                    String dw = gydeEle.attributeValue("zjldw");
                    String dw2 = gydeEle.attributeValue("dw");
                    String jstj = gydeEle.attributeValue("jstj");
                    String bzh = gydeEle.attributeValue("bzh");
                    if(CommonUtil.isNull(bzh)){
                        bzh = jstj;
                    }

                    String gg = gydeEle.attributeValue("gg");
                    String sccj = gydeEle.attributeValue("sccj");
                    String zldj = gydeEle.attributeValue("zldj");
                    String fzxs = gydeEle.attributeValue("fzxs");
                    String dcstxyq = gydeEle.attributeValue("dcstxyq");
                    String comment = gydeEle.attributeValue("comment");
                    String tabType = gydeEle.attributeValue("tabType");
                    String xhph = gydeEle.attributeValue("xhph");
                    if("标准紧固件".equals(tabType)){
                        xhph = gydeEle.attributeValue("cl");
                    }
                    if("机电材料".equals(tabType) && StringUtils.isEmpty(gg)){
                        gg = CMUtilToString(gydeEle.attributeValue("xhgg"));
                    }
                    String dataType = "装配主要材料定额";
                    String wh = "";
                    if(tabType!=null && !"".equals(tabType)) {
                        if ("电子元器件".equals(tabType)) {
                            tabType = "元器件";
                            wh = gydeEle.attributeValue("wh");
                        }
                        dataType = tabType;
                    }
                    String sl = CMUtilToString(gydeEle.attributeValue("sl"));
                    double gysl = Double.parseDouble(sl);
                    double zCount = gysl;
                    if(partCountMap.containsKey(number + "_" + number)){
                        zCount = CommonUtil.subDouble(gysl,partCountMap.get(number + "_" + number));
                    }
                    VaLightPart part = VaLightPart.newLightPart(number, name, "", 1, xhph, gg);
                    part.setAmount(gysl);
                    part.setzCount(zCount);
                    part.setDataType(dataType);
                    part.setRootType(rootType);
                    part.setBzh(bzh);
                    part.setJstj(jstj);
                    VaTreeNode childNode = new VaTreeNode(new VaEPartInstance(part));
                    childNode.setOccpath(number);
                    childNode.setOccId(number);
                    childNode.setDw(dw);
                    childNode.setDw2(dw2);
                    childNode.setXhph(xhph);
                    childNode.setGg(gg);
                    childNode.setSccj(sccj);
                    childNode.setDcstxyq(dcstxyq);
                    childNode.setFzxs(fzxs);
                    childNode.setZldj(zldj);
                    childNode.setJstj(jstj);
                    childNode.setBzh(bzh);
                    childNode.setComment(comment);
                    childNode.setDataType(dataType);
                    childNode.setWh(wh);
                    childNode.setzCount(zCount);
                    childNode.setcCount(CommonUtil.subDouble(gysl,zCount));
                    childNode.setFlag("ZPZYCLDENEWPART");
                    if(childNode.getzCount() == 0){
                        childNode.setUsed(true);
                    }else{
                        childNode.setUsed(false);
                    }
                    ArrayList<VaTreeNode> val;
                    if(allMap.containsKey(dataType)){
                        val = allMap.get(dataType);
                        boolean isHas = false;
                        for(VaTreeNode treeNode : val){
                            if(treeNode.getPart().getNumber().equals(number)){
                                isHas = true;
                                double amount = CommonUtil.addDouble(treeNode.getPart().getAmount(), gysl);
                                double zcount = amount;
                                if(partCountMap.containsKey(number + "_" + number)){
                                    zcount = CommonUtil.subDouble(amount,partCountMap.get(number + "_" + number));
                                }
                                treeNode.getPart().setAmount(amount);
                                treeNode.getPart().setzCount(zcount);
                                treeNode.setzCount(zcount);
                                treeNode.setcCount(CommonUtil.subDouble(amount,zcount));
                                if(treeNode.getzCount() == 0){
                                    treeNode.setUsed(true);
                                }else{
                                    treeNode.setUsed(false);
                                }
                                break;
                            }
                        }
                        if(!isHas){
                            val.add(childNode);
                        }
                    }else{
                        val = new ArrayList<VaTreeNode>();
                        val.add(childNode);
                        allMap.put(dataType, val);
                    }
                }
            }
        }

    }

    @SuppressWarnings("unchecked")
    private void buildPBOMTree2(List<Element> list, String rootType) {
        root.removeAllChildren();
        updateUI();
        System.out.println("----->>>:::::" + root.getChildCount());
//        HashMap<String, Integer> map = new HashMap<String, Integer>();
        System.out.println("---->>>::" + list.get(0).toString());
        String xmlPath = FittingsDistributionFrame.getPathString();
        Document document = null;
        try {
//            VaTreeNode node = (VaTreeNode) root.getChildAt(0);
            document = XmlUtil.getDocument(new File(xmlPath));
            Element rootElement = document.getRootElement();
            Element elementsByName = XmlUtil.getElementsByName(rootElement, "QMFawTechnicsInfo").get(0);
            List<Element> stepElements = elementsByName.selectNodes("steps/QMProcedureInfo");
            List<Element> paceElements = elementsByName.selectNodes("steps/QMProcedureInfo/paces/QMProcedureInfo");

//            Map<String, PDFBuilder.CzjPart> czjPartMap = PDFBuilder.getAllCzjMap(elementsByName);
//
            Map<String, String> occIdMap = new HashMap<String, String>();

            Element gyde = XmlUtility.getTechnicsDEElement(elementsByName);
            if (gyde != null) {
                List<Element> newSjzykParts = XmlUtility.getTechnicsSJZYKGYDENewPart(gyde);
                if (newSjzykParts != null && !newSjzykParts.isEmpty()) {
                    for (Element element : newSjzykParts) {
                        String sl = element.attributeValue("gysl");
                        int gysl;
                        if (sl.contains(".")) {
                            continue;
                        } else {
                            gysl = Integer.parseInt(sl);
                        }
//                        for (int i = 0; i < gysl; i++) {
                        String number = CMUtilToString(element.attributeValue("sjbm"));
                        String name = CMUtilToString(element.attributeValue("name"));
                        //零组件生产类型
                        String dataType = CMUtilToString(element.attributeValue("dataType"));
                        //标准号
                        String bzh = CMUtilToString(element.attributeValue("bzh"));
                        //机械性能等级
                        String jxxndjhyd = CMUtilToString(element.attributeValue("jxxndjhyd"));
                        //表面处理
                        String bmcl = CMUtilToString(element.attributeValue("bmcl"));
                        //热处理
                        String rcl = CMUtilToString(element.attributeValue("rcl"));
                        //产品形式
                        String cpxs = CMUtilToString(element.attributeValue("cpxs"));
                        //产品等级
                        String cpdj = CMUtilToString(element.attributeValue("cpdj"));
                        //扳令形式
                        String bnxs = CMUtilToString(element.attributeValue("bnxs"));
                        //是否进口
                        String sfjk = CMUtilToString(element.attributeValue("sfjk"));
                        //备注
                        String comment = CMUtilToString(element.attributeValue("comment"));
                        //型号牌号
                        String xhph = CMUtilToString(element.attributeValue("xhph"));
                        //规格
                        String gg = CMUtilToString(element.attributeValue("gg"));
                        //单位
                        String dw2 = CMUtilToString(element.attributeValue("dw2"));
                        //主计量单位
                        String dw = CMUtilToString(element.attributeValue("dw"));
                        String cl = CMUtilToString(element.attributeValue("cl"));
                        VaLightPart part = VaLightPart.newLightPart(number, name, "", 1, xhph, gg, bzh, dataType);
                        part.setAmount(gysl);
                        part.setCl(cl);
                        part.setRootType(rootType);
//                            VaLightPart part = new VaLightPart();
                        VaTreeNode childNode = new VaTreeNode(new VaEPartInstance(part));
//                            int count = root.getChildCount();
//                            occIdMap.put(number + "$@" + count + 1, number + "$$@" + (i + 1));
                        String occPath = "";
                        String occId = "";
                        for (int i = 0; i < gysl; i++) {
                            if ("".equals(occPath)) {
                                occPath = number + "$$@" + (i + 1);
                            } else {
                                occPath += "," + number + "$$@" + (i + 1);
                            }
                            if ("".equals(occId)) {
                                occId = number + "$$@" + (i + 1);
                            } else {
                                occId += "," + number + "$$@" + (i + 1);
                            }
                        }
                        childNode.setOccpath(occPath);
                        childNode.setOccId(occId);
                        childNode.setDataType(dataType);
                        childNode.setBzh(bzh);
                        childNode.setJxxndjhyd(jxxndjhyd);
                        childNode.setBmcl(bmcl);
                        childNode.setRcl(rcl);
                        childNode.setCpxs(cpxs);
                        childNode.setCpdj(cpdj);
                        childNode.setBnxs(bnxs);
                        childNode.setSfjk(sfjk);
                        childNode.setComment(comment);
                        childNode.setXhph(xhph);
                        childNode.setGg(gg);
                        childNode.setDw2(dw2);
                        childNode.setDw(dw);
                        childNode.setCl(cl);
                        childNode.setFlag("SJZYKGYDENEWPART");
//                            if (map.containsKey(childNode.getOccId())) {
//                                Integer cishu = map.get(childNode.getOccId());
//                                if (cishu == 0) {
//                                    childNode.setUsed(true);
//                                } else {
//                                    childNode.setUsed(false);
//                                }
//                            } else {
//                                childNode.setUsed(false);
//                            }

                        root.add(childNode);
                        updateUI();
//                        }
                    }
                }


                // ERP集成获取的数据
                List<Element> newParts = XmlUtility.getTechnicsGYDENewPart(gyde);
                if (newParts != null && !newParts.isEmpty()) {
                    for (Element element : newParts) {
                        System.out.println(element.asXML());
                        String sl = element.attributeValue("sl");
                        int gysl;
                        if (sl.contains(".")) {
                            continue;
                        } else {
                            gysl = Integer.parseInt(sl);
                        }
//                        for (int i = 0; i < gysl; i++) {
                        String number = element.attributeValue("number");
                        String name = element.attributeValue("chmc");
                        String dw = element.attributeValue("dw");
                        String dw2 = element.attributeValue("dw2");
                        String xhph = element.attributeValue("xhph");
                        String gg = element.attributeValue("gg");
                        String sccj = element.attributeValue("sccj");
                        String dcstxyq = element.attributeValue("dcstxyq");
                        String fzxs = element.attributeValue("fzxs");
                        String zldj = element.attributeValue("zldj");
                        String comment = element.attributeValue("comment");
                        String jstj = element.attributeValue("jstj");
                        String wzlb = element.attributeValue("wzlb");
                        String dataType = getDataType(wzlb);

                        VaLightPart part = VaLightPart.newLightPart(number, name, "", 1, xhph, gg);
                        part.setAmount(gysl);
                        part.setDataType(dataType);
                        part.setRootType(rootType);
                        VaTreeNode childNode = new VaTreeNode(new VaEPartInstance(part));
//                            int count = root.getChildCount();
//                            occIdMap.put(number + "$@" + count + 1, number + "$$@" + (i + 1));
                        String occPath = "";
                        String occId = "";
                        for (int i = 0; i < gysl; i++) {
                            if ("".equals(occPath)) {
                                occPath = number + "$$@" + (i + 1);
                            } else {
                                occPath += "," + number + "$$@" + (i + 1);
                            }
                            if ("".equals(occId)) {
                                occId = number + "$$@" + (i + 1);
                            } else {
                                occId += "," + number + "$$@" + (i + 1);
                            }
                        }
                        childNode.setOccpath(occPath);
                        childNode.setOccId(occId);
                        childNode.setDw(dw);
                        childNode.setDw2(dw2);
                        childNode.setXhph(xhph);
                        childNode.setGg(gg);
                        childNode.setSccj(sccj);
                        childNode.setDcstxyq(dcstxyq);
                        childNode.setFzxs(fzxs);
                        childNode.setZldj(zldj);
                        childNode.setJstj(jstj);
                        childNode.setDataType(dataType);
                        childNode.setComment(comment);
                        childNode.setFlag("GYDENEWPART");
//                            if (map.containsKey(childNode.getOccId())) {
//                                Integer cishu = map.get(childNode.getOccId());
//                                if (cishu == 0) {
//                                    if (cishu == 0) {
//                                        childNode.setUsed(true);
//                                    } else {
//                                        childNode.setUsed(false);
//                                    }
//                                }
//                            } else {
//                                childNode.setUsed(false);
//                            }
                        root.add(childNode);
                        updateUI();
//                        }
                    }
                }
                // 工艺定额 匹配
                List<Element> newParts1 = XmlUtility.getTechnicsGYDEMatchPart(gyde);
                if (newParts1 != null && !newParts1.isEmpty()) {
                    for (Element element : newParts1) {
                        String sl = element.attributeValue("gyCount");
                        int gysl;
                        if (sl.contains(".")) {
                            continue;
                        } else {
                            gysl = Integer.parseInt(sl);
                        }
//                        for (int i = 0; i < gysl; i++) {
                        String number = element.attributeValue("chbm");
                        String name = element.attributeValue("chmc");
                        String dw = element.attributeValue("dw");
                        String dw2 = element.attributeValue("dw2");
                        String xhph = element.attributeValue("xhph");
                        String gg = element.attributeValue("gg");
                        String sccj = element.attributeValue("sccj");
                        String dcstxyq = element.attributeValue("dcstxyq");
                        String fzxs = element.attributeValue("fzxs");
                        String zldj = element.attributeValue("zldj");
                        String comment = element.attributeValue("comment");
                        String wzlb = element.attributeValue("wzlb");
                        String dataType = getDataType(wzlb);
                        VaLightPart part = VaLightPart.newLightPart(number, name, "", 1, xhph, gg);
                        part.setAmount(gysl);
                        part.setDataType(dataType);
                        part.setRootType(rootType);
                        VaTreeNode childNode = new VaTreeNode(new VaEPartInstance(part));
//                            int count = root.getChildCount();
//                            occIdMap.put(number + "$@" + count + 1, number + "$$@" + (i + 1));
                        String occPath = "";
                        String occId = "";
                        for (int i = 0; i < gysl; i++) {
                            if ("".equals(occPath)) {
                                occPath = number + "$$@" + (i + 1);
                            } else {
                                occPath += "," + number + "$$@" + (i + 1);
                            }
                            if ("".equals(occId)) {
                                occId = number + "$$@" + (i + 1);
                            } else {
                                occId += "," + number + "$$@" + (i + 1);
                            }
                        }
                        childNode.setOccpath(occPath);
                        childNode.setOccId(occId);
                        childNode.setDw(dw);
                        childNode.setDw2(dw2);
                        childNode.setXhph(xhph);
                        childNode.setGg(gg);
                        childNode.setSccj(sccj);
                        childNode.setDcstxyq(dcstxyq);
                        childNode.setFzxs(fzxs);
                        childNode.setZldj(zldj);
                        childNode.setComment(comment);
                        childNode.setDataType(dataType);
                        childNode.setFlag("GYDEMATCHPART");
//                            if (map.containsKey(childNode.getOccId())) {
//                                Integer cishu = map.get(childNode.getOccId());
//                                if (cishu == 0) {
//                                    if (cishu == 0) {
//                                        childNode.setUsed(true);
//                                    } else {
//                                        childNode.setUsed(false);
//                                    }
//                                }
//                            } else {
//                                childNode.setUsed(false);
//                            }
                        root.add(childNode);
                        updateUI();
//                        }
                    }
                }

                List<Element> newParts2 = XmlUtility.getTechnicsSJZYKGYDEMatchPart(gyde);
                if (newParts2 != null && !newParts2.isEmpty()) {
                    for (Element element : newParts2) {
                        String sl = element.attributeValue("gysl");
                        int gysl;
                        if (sl.contains(".")) {
                            continue;
                        } else {
                            gysl = Integer.parseInt(sl);
                        }
//                        for (int i = 0; i < gysl; i++) {
                        String number = element.attributeValue("sjbm");
                        String name = element.attributeValue("name");
                        //零组件生产类型
                        String dataType = CMUtilToString(element.attributeValue("dataType"));
                        //标准号
                        String bzh = CMUtilToString(element.attributeValue("bzh"));
                        //机械性能等级
                        String jxxndjhyd = CMUtilToString(element.attributeValue("jxxndjhyd"));
                        //表面处理
                        String bmcl = CMUtilToString(element.attributeValue("bmcl"));
                        //热处理
                        String rcl = CMUtilToString(element.attributeValue("rcl"));
                        //产品形式
                        String cpxs = CMUtilToString(element.attributeValue("cpxs"));
                        //产品等级
                        String cpdj = CMUtilToString(element.attributeValue("cpdj"));
                        //扳令形式
                        String bnxs = CMUtilToString(element.attributeValue("bnxs"));
                        //是否进口
                        String sfjk = CMUtilToString(element.attributeValue("sfjk"));
                        //备注
                        String comment = CMUtilToString(element.attributeValue("comment"));
                        //型号牌号
                        String xhph = CMUtilToString(element.attributeValue("xhph"));
                        //规格
                        String gg = CMUtilToString(element.attributeValue("gg"));
                        //单位
                        String dw2 = CMUtilToString(element.attributeValue("dw2"));
                        //主计量单位
                        String dw = CMUtilToString(element.attributeValue("dw"));
                        String cl = CMUtilToString(element.attributeValue("cl"));
                        VaLightPart part = VaLightPart.newLightPart(number, name, "", 1, xhph, gg, bzh, dataType);
                        part.setAmount(gysl);
                        part.setCl(cl);
                        part.setRootType(rootType);
                        VaTreeNode childNode = new VaTreeNode(new VaEPartInstance(part));
                        int count = root.getChildCount();
                        String occPath = "";
                        String occId = "";
                        for (int i = 0; i < gysl; i++) {
                            if ("".equals(occPath)) {
                                occPath = number + "$$@" + (i + 1);
                            } else {
                                occPath += "," + number + "$$@" + (i + 1);
                            }
                            if ("".equals(occId)) {
                                occId = number + "$$@" + (i + 1);
                            } else {
                                occId += "," + number + "$$@" + (i + 1);
                            }
                        }
//                            occIdMap.put(number + "$@" + count + 1, number + "$$@" + (i + 1));
                        childNode.setOccpath(occPath);
                        childNode.setOccId(occId);
                        childNode.setDataType(dataType);
                        childNode.setBzh(bzh);
                        childNode.setJxxndjhyd(jxxndjhyd);
                        childNode.setBmcl(bmcl);
                        childNode.setRcl(rcl);
                        childNode.setCpxs(cpxs);
                        childNode.setCpdj(cpdj);
                        childNode.setBnxs(bnxs);
                        childNode.setSfjk(sfjk);
                        childNode.setComment(comment);
                        childNode.setXhph(xhph);
                        childNode.setGg(gg);
                        childNode.setDw2(dw2);
                        childNode.setDw(dw);
                        childNode.setCl(cl);
                        childNode.setFlag("SJZYKGYDEMATCHPART");
//                            if (map.containsKey(childNode.getOccId())) {
//                                Integer cishu = map.get(childNode.getOccId());
//                                if (cishu == 0) {
//                                    if (cishu == 0) {
//                                        childNode.setUsed(true);
//                                    } else {
//                                        childNode.setUsed(false);
//
//                                    }
//                                }
//                            } else {
//                                childNode.setUsed(false);
//                            }
                        root.add(childNode);
                        updateUI();
                    }
                }

            }
            //FPBOM列表根据型号牌号排序 add by zhuhao 2017.12.27
            List<VaTreeNode> lis = new ArrayList<VaTreeNode>();
            for (int i = 0; i < root.getChildCount(); i++) {
                VaTreeNode va = (VaTreeNode) root.getChildAt(i);
                lis.add(va);
            }
            Collections.sort(lis, new Comparator<VaTreeNode>() {
                        @Override
                        public int compare(VaTreeNode o1, VaTreeNode o2) {
                            return o1.getXhph().compareTo(o2.getXhph());
                        }
                    }
            );
            Iterator iterator_xhph = lis.iterator();
            root.removeAllChildren();
            while (iterator_xhph.hasNext()) {
                VaTreeNode s = (VaTreeNode) iterator_xhph.next();
                root.add(s);
            }
            updateUI();
            //FPBOM列表排序 end
//            }
            /**装配工具启动时，刷新occId历史数据***Start***/
            Map<String, Integer> zcMap = new HashMap<String, Integer>();
            String occId;
            String zcMark;

            XWTreeNode technicsTreeNode = FittingsDistributionFrame.parentFrame.technicsTreePanel.getCurrentTechnicsNode();
            if (technicsTreeNode != null) {
                Element technicsElement = technicsTreeNode.getObject().getTreeCellData();
                List<XWTreeNode> stepNodes = technicsTreeNode.getAllSteps();
                for (XWTreeNode stepNode : stepNodes) {
                    Element stepElement = stepNode.getObject().getTreeCellData();
                    List<Element> czjElements = stepElement.selectNodes("parts/QMPartInfo");
                    for (Element czjElement : czjElements) {
                        occId = czjElement.attributeValue("occId");
                        zcMark = czjElement.attributeValue("ZCMARK");
                        if (occIdMap.containsKey(occId)) {
                            XmlUtility.setAttributeValue(czjElement, "occId", occIdMap.get(occId));
                            occId = czjElement.attributeValue("occId");
                        }
                        if (zcMap.containsKey(occId)) {
                            Integer jishu = zcMap.get(occId);
                            if ("Z".equals(zcMark)) {
                                jishu = jishu + 1;
                            } else {
                                jishu = jishu - 1;
                            }
                            zcMap.put(occId, jishu);
                        } else {
                            zcMap.put(occId, 0);
                        }
                    }
                    List<XWTreeNode> paceNodes = stepNode.getAllSteps();
                    for (XWTreeNode paceNode : paceNodes) {
                        Element paceElement = paceNode.getObject().getTreeCellData();
                        List<Element> paceCzjElements = paceElement.selectNodes("parts/QMPartInfo");
                        for (Element czjElement : paceCzjElements) {
                            occId = czjElement.attributeValue("occId");
                            zcMark = czjElement.attributeValue("ZCMARK");
                            if (occIdMap.containsKey(occId)) {
                                XmlUtility.setAttributeValue(czjElement, "occId", occIdMap.get(occId));
                                occId = czjElement.attributeValue("occId");
                            }
                            if (zcMap.containsKey(occId)) {
                                Integer jishu = zcMap.get(occId);
                                if ("Z".equals(zcMark)) {
                                    jishu = jishu + 1;
                                } else {
                                    jishu = jishu - 1;
                                }
                                zcMap.put(occId, jishu);
                            } else {
                                zcMap.put(occId, 0);
                            }
                        }
                    }
                }
                XmlUtility.saveDocument(technicsElement.getDocument(), xmlPath);
            }
            /**判断节点是否被使用*/
            boolean isUsed;
            int zcCount;
            for (int i = 0; i < root.getChildCount(); i++) {
                isUsed = true;
                VaTreeNode vaTreeNode = (VaTreeNode) root.getChildAt(i);
                String nodeOccId = vaTreeNode.getOccId();
                if(nodeOccId.contains(",")){
                    String[] nodeOccids = nodeOccId.split(",");
                    for(String childOccid : nodeOccids){
                        if(zcMap.containsKey(childOccid)){
                            zcCount = zcMap.get(childOccid);
                            if(zcCount != 0){
                                isUsed = false;
                                break;
                            }
                        }else{
                            isUsed = false;
                            break;
                        }
                    }
                }else{
                    if (zcMap.containsKey(nodeOccId)) {
                        zcCount = zcMap.get(nodeOccId);
                            if (zcCount != 0) {
                                isUsed = false;
                            }
                    } else {
                        isUsed = false;
                    }
                }
                vaTreeNode.setUsed(isUsed);
            }

            /***End***/

        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtil.expandAll(this);
        updateUI();

    }


    @Override
    public JToolTip createToolTip() {
        if (showTooltip) {
            JToolTip ttp = super.createToolTip();
            ttp.setBackground(new Color(255, 255, 200));
            ttp.setForeground(Color.BLACK);
            return ttp;
        }
        return null;
    }

    public void addPVMapping(Instance instance, VaTreeNode node) {
        instanceDICNodeMapping.put(instance, node);
    }

    public void removePVMapping(Instance instance) {
        instanceDICNodeMapping.remove(instance);
    }

    public VaTreeNode getNodeFromInstance(Instance instance) {
        return (VaTreeNode) instanceDICNodeMapping.get(instance);
    }

    public boolean isShowTooltip() {
        return showTooltip;
    }

    public void setShowTooltip(boolean showTooltip) {
        if (showTooltip)
            ttm.registerComponent(this);
        else
            ttm.unregisterComponent(this);
        this.showTooltip = showTooltip;
    }

    public VaTreeNode getSelectedNode() {
        return (VaTreeNode) getLastSelectedPathComponent();
    }

    public void collapse(VaTreeNode selectedDICTreeNode) {
        if (selectedDICTreeNode != null) {
            Enumeration depthFirstEnumeration = selectedDICTreeNode.depthFirstEnumeration();
            while (depthFirstEnumeration.hasMoreElements()) {
                VaTreeNode currentICBTreeNode = (VaTreeNode) depthFirstEnumeration.nextElement();
                if (!isCollapsed(new TreePath(currentICBTreeNode.getPath())))
                    collapsePath(new TreePath(currentICBTreeNode.getPath()));
            }
        }
    }

    public void expandAllLevels(VaTreeNode anICBTreeNode) {
        if (anICBTreeNode != null) {
            Enumeration breadthFirstEnumeration = anICBTreeNode.breadthFirstEnumeration();
            while (breadthFirstEnumeration.hasMoreElements()) {
                VaTreeNode currentICBTreeNode = (VaTreeNode) breadthFirstEnumeration.nextElement();
                if (isCollapsed(new TreePath(currentICBTreeNode.getPath())))
                    expandPath(new TreePath(currentICBTreeNode.getPath()));
            }
        }
    }

    public VaTreeNode getRoot() {
        return root;
    }

    public void setRoot(VaTreeNode root) {
        this.root = root;
        ((DefaultTreeModel) this.getModel()).setRoot(root);
    }

    /**
     * ��д������ʼ����ʾ�ڵ���
     *
     * @return
     */
    public boolean getShowsRootHandles() {
        return true;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        try {
            return super.clone();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static void setPbomVersion(Element element) {

        String number = element.attributeValue("partNumber");
        String version = element.attributeValue("version");

        pbomMap.put(number, version);

        String oid = element.attributeValue("oid");
        String occId = element.attributeValue("occId");
        if (oid != null && !oid.equals("") && occId != null) {
            String[] oids = oid.split(",");
            String[] occIds = occId.split(",");
            for (int i = 0; i < oids.length; i++) {
                occMap.put(emptyToLong(oids[i]), occIds[i]);
            }
        }

        List<Element> e = element.elements();
        if (e.size() > 0) {
            for (int i = 0; i < e.size(); i++) {
                setPbomVersion(e.get(i));
            }
        }
    }

    public static String getPbomVersion(String partNumber) {
        return pbomMap.get(partNumber);
    }

    public static String getPbomOccId(long oid) {
        return occMap.get(oid);
    }

    public static List<Element> buildTreePbom() {
        byte[] bytes = ((NewTechnicsPart) VaContext.getMainFrame()).pbomBytes;
        String currNum = VaContext.getCurrentPartNumber();
        if (bytes == null) {
            JOptionPane.showMessageDialog(null, "PBom_xml获取失败!\nPartNumber: " + currNum);
            return null;
        }
        Document doc = null;
        try {
            String xml = new String(bytes, "GBK");
//            log.debug(xml);
            doc = DocumentHelper.parseText(xml);

            // VaTree.setPbomVersion(doc.getRootElement());

            String uri = doc.getRootElement().getNamespaceURI();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("xx", uri);
            String prefix = " /Product/parts/QMPartInfo";
            String queryField = "[@partNumber='" + currNum + "']";
            XPath xpath = DocumentHelper.createXPath(prefix + queryField);
            xpath.setNamespaceURIs(map);
            List<Element> list = xpath.selectNodes(doc);

            int index = 0;
            if (list.size() == 0) {
                while (true) {
                	index++;
                    prefix += "/childs";

                    xpath = DocumentHelper.createXPath(prefix);

                    List<Element> listForCheck = xpath.selectNodes(doc);
                    prefix += "/QMPartInfo";
                    if (listForCheck.size() > 0) {
                        xpath = DocumentHelper.createXPath(prefix + queryField);
                        list = xpath.selectNodes(doc);
                        if (list.size() > 0) {
                            break;
                        }
                    }else{
                    	if(index>50){//只往下找50层，找不到就跳出
                        	break;
                        }
                    }

                }
            }

            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean buildTreeForFitting(List<Element> list) {

        try {

            if (list != null && list.size() != 0) {
                Element e = list.get(0);
                String[] occId = e.attributeValue("occId").split(",");
                String[] occpath = e.attributeValue("occpath").split(",");
                String[] middleIndex = e.attributeValue("middleIndex").split(",");

                String structureSaved = e.attributeValue("structureSaved");
                if (structureSaved == null || structureSaved.equals("")) {
                    VaContext.setPbomSaved("false");
                } else {
                    VaContext.setPbomSaved(structureSaved);
                }

                VaTreeNode childnode = null;
                if (occId.length > 0) {
                    if (occId[0].indexOf("&") > 0) {
                        String curOccId = occId[0].substring(0, occId[0].indexOf("&"));
                        String curOccpath = occpath[0].substring(0, occpath[0].indexOf("&"));
                        String curMiddleIndex = middleIndex[0].substring(0, middleIndex[0].indexOf("&"));
                        childnode = addTreeNode(e, "-1", "-1", "-1");
                    } else {
                        childnode = addTreeNode(e, "-1", "-1", "-1");
                    }
                }

                partRoot = childnode;
                root.add(childnode);

                log.debug(childnode);
                if (!"".equals(childnode.toString())) {
                    int le = 0;
//                    addChildNode(e, childnode, le);
                    addZpbomChileNode(e, childnode, le);
                    partRoot = childnode;
                    root.add(childnode);
                }
//				FittingsDistributionFrame.getMatrixFromEbom(root);
            }
            SwingUtil.expandAll(this);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "xml格式不正确！");
            return false;
        }
    }

    /**
     * 加载ZPBOM节点
     * @param e
     * @param childnode
     * @param level
     */
    private void addZpbomChileNode(Element e, VaTreeNode childnode, int level) throws InvocationTargetException, RemoteException {
        String xmlPath = FittingsDistributionFrame.getPathString();
        Document document = null;

        document = XmlUtil.getDocument(new File(xmlPath));
        Element rootElement = document.getRootElement();
        Element technicsEle = XmlUtil.getElementsByName(rootElement, "QMFawTechnicsInfo").get(0);
        List<Element> stepElements = technicsEle.selectNodes("steps/QMProcedureInfo");
        List<Element> paceElements = technicsEle.selectNodes("steps/QMProcedureInfo/paces/QMProcedureInfo");
        partCountMap = new HashMap<String, Double>();
        partCountMap = getPartZCount(stepElements,partCountMap);
        partCountMap = getPartZCount(paceElements,partCountMap);


        Set<String> numberList = new HashSet<String>();
        List<Element> listEle = technicsEle.selectNodes("GYDE/MATCHPART/MatchPart");
        if(listEle != null && listEle.size() > 0) {
            for(Element element : listEle) {
                String number = element.attributeValue("number");
                if(StringUtils.isNotEmpty(number)) {
                    numberList.add(number);
                }
            }
        }
        listEle = technicsEle.selectNodes("GYDE/SJZYKMATCHPART/SjzykMatchPart");
        if(listEle != null && listEle.size() > 0) {
            for(Element element : listEle) {
                String number = element.attributeValue("partNumber");
                if(StringUtils.isNotEmpty(number)) {
                    numberList.add(number);
                }
            }
        }
        System.out.println("numberList:"+numberList);

        //只加载一级子件
        if (level == stopLevel) {
            return;
        }
        level++;
        // 第一层childs层
        List<Element> list = e.elements();
        for (Element e1 : list) {
            // 第二层QMPartInfo层
            List<Element> childList = e1.elements();
            for (Element e2 : childList) {
                String partNumber = e2.attributeValue("partNumber");
//                String partOid = e2.attributeValue("oid");
//                String occid = e2.attributeValue("occId");
//                String occpath = e2.attributeValue("occpath");
                String middleIndex = e2.attributeValue("middleIndex");
//                String[] occIds = null;
//                String[] occpaths = null;
                String[] middleIndexs = null;
                //标准件、元器件、外购件、主要材料不展示
                if (numberList.contains(partNumber)||"标准件".equals(e2.attributeValue("MTYPE")) || "元器件".equals(e2.attributeValue("MTYPE")) || "外购件".equals(e2.attributeValue("MTYPE")) || "主要材料".equals(e2.attributeValue("MTYPE"))) {
                    continue;
                }
                /*if (occid.indexOf(e.attributeValue("partNumber")) > 0) {
                    occIds = occid.split("&" + e.attributeValue("partNumber") + "&");
                    occIds = occIds[0].split(",");
                } else {
                    occIds = occid.split(",");
                }

                if (occpath.indexOf(e.attributeValue("partNumber")) > 0) {
                    occpaths = occpath.split("&" + e.attributeValue("partNumber") + "&");
                    occpaths = occpaths[0].split(",");
                } else {
                    occpaths = occpath.split(",");
                }*/

                if (middleIndex.indexOf(e.attributeValue("partNumber")) > 0) {
                    middleIndexs = middleIndex.split("&" + e.attributeValue("partNumber") + "&");
                    middleIndexs = middleIndexs[0].split(",");
                } else {
                    middleIndexs = middleIndex.split(",");
                }
                VaTreeNode brother = null;
                String gysl = BomXMLUtil.getGYSLFromWNC(e2, e2.attributeValue("partNumber"));
//                String occidfont = occIds[0].substring(0, occIds[0].indexOf("@") + 1);
//                if(occIds[0].contains("@")){
//                    occidfont = occIds[0].substring(0, occIds[0].indexOf("@") + 1);
//                }else{
//                    occidfont = occIds[0];
//                }
                String curOccId;
                String curOccpath;
                //校验是否有对应的模型
                /*if(TechnicsIntf.checkIsHasEpm(partOid)){
//                if(true){
                    //如果有模型，则逐个展示
                    int sl = Integer.valueOf(gysl);
                    for (int i = 0; i < sl; i++) {
                        curOccId = occidfont + String.valueOf(i + 1);
                        curOccpath = "-1+" + curOccId;
                        String key = partNumber + "_" + curOccId;
                        brother = addTreeNode(e2, curOccId, curOccpath, middleIndexs[0]);
                        Double amount = 1.0;
                        Double zCount = 1.0;
                        if(partCountMap.containsKey(key)){
//                            zCount = 0.0;
                            zCount = CommonUtil.subDouble(1,partCountMap.get(key));
                        }
                        brother.setzCount(zCount);
                        brother.setcCount(CommonUtil.subDouble(amount, zCount));
                        brother.setHasEpmDoc(true);
                        brother.setFlag("ZPBOM");
                        brother.getPart().setAmount(amount);
                        brother.getPart().setzCount(brother.getzCount());
                        brother.getPart().setcCount(brother.getcCount());
                        brother.getPart().setRootType("ZPBOM");
                        if(brother.getzCount() == 0){
                            brother.setUsed(true);
                        }else{
                            brother.setUsed(false);
                        }
                        childnode.add(brother);
                    }
                }else{*/
                    //如果没有模型，则合并展示
                    curOccId = partNumber;
                    String key = partNumber + "_" + curOccId;
                    curOccpath = "-1+" + curOccId;
                    brother = addTreeNode(e2, curOccId, curOccpath, middleIndexs[0]);
                    double zCount = Double.valueOf(gysl);
                    if(partCountMap.containsKey(key)){
                        zCount = CommonUtil.subDouble(Double.valueOf(gysl),partCountMap.get(key));
                    }
                    brother.setzCount(zCount);
                    brother.setcCount(CommonUtil.subDouble(Double.valueOf(gysl), zCount));
                    brother.setFlag("ZPBOM");
//                    brother.setHasEpmDoc(false);
                    brother.getPart().setAmount(Double.valueOf(gysl));
                    brother.getPart().setzCount(brother.getzCount());
                    brother.getPart().setcCount(brother.getcCount());
                    brother.getPart().setRootType("ZPBOM");
                    if(brother.getzCount() == 0){
                        brother.setUsed(true);
                    }else{
                        brother.setUsed(false);
                    }
                    childnode.add(brother);
//                }

            }
        }

    }

    // private void reBuildOccPath(VaTreeNode partRoot) {
    // if (partRoot.getOccpath() != null || partRoot.getOccpath() != "") {
    // String occPathPrefix = partRoot.getOccpath();
    // partRoot.setOccpath("0");
    // partRoot.setOccId("0");
    // Enumeration<VaTreeNode> childrenP = partRoot.breadthFirstEnumeration();
    //
    // while (childrenP.hasMoreElements()) {
    // VaTreeNode vaTreeNode = (VaTreeNode) childrenP.nextElement();
    // vaTreeNode.setOccpath(vaTreeNode.getOccpath().replace(occPathPrefix,
    // ""));
    // }
    // }
    // }

    public Element getParts(Element doc) {
        Element parts = null;
        if (!doc.getQName().getQualifiedName().equals("parts")) {
            List<Element> list = doc.elements();
            for (Element e : list) {
                if (e.getQName().getQualifiedName().equals("parts")) {
                    parts = e;
                    break;
                } else {
                    getParts(e);
                }
            }
        } else {
            parts = doc;
        }
        return parts;

    }

    public static VaTreeNode addTreeNode(Element e, String occId, String occpath, String middleIndex) {
        VaLightPart part = new VaLightPart();
        String oid = e.attributeValue("oid");
        part.setOid(VaTree.emptyToLong(oid));

        part.setNumber(e.attributeValue("partNumber"));
        part.setName(e.attributeValue("partName"));
        part.setType(e.attributeValue("partType"));
        part.setDutu(e.attributeValue("dutu"));
        part.setRemark(e.attributeValue("remark"));
        part.setUseCount(Integer.parseInt(e.attributeValue("useCount")));
        part.setProductionQuantity(Integer.parseInt(e.attributeValue("productionQuantity")));
        part.setProductionRatio(e.attributeValue("productionRatio"));
        part.setRate(e.attributeValue("rate"));
        part.setKey(Boolean.valueOf(e.attributeValue("isKey")));
        //part.setSpecialPart(e.attributeValue("specialPart"));
        part.setSpaceBorneTable(Boolean.valueOf(e.attributeValue("spaceBorneTable")));
        part.setEbomKey(Boolean.valueOf(e.attributeValue("isEbomKey")));
        part.setMaterialType(e.attributeValue("materialType"));
        part.setBackupRate(e.attributeValue("backupRate"));
        part.setMaxBackupCount(e.attributeValue("maxBackupCount"));
        part.setBackupReason(e.attributeValue("backupReason"));
        part.setWorkShop(e.attributeValue("workShop"));
        part.setOutsourcingUnits(e.attributeValue("outsourcingUnits"));
        part.setMaterialNumber(e.attributeValue("materialNumber"));
        part.setMaterialName(e.attributeValue("materialName"));
        part.setMaterialBrand(e.attributeValue("materialBrand"));
        part.setMaterialCrision(e.attributeValue("materialCrision"));
        part.setResponser(e.attributeValue("responser"));
        part.setResponserGroup(e.attributeValue("responserGroup"));
        part.setContainerId(VaTree.emptyToLong(e.attributeValue("containerId")));
        part.setE_version(e.attributeValue("e_version"));
        part.setEu_number(e.attributeValue("eu_number"));
        part.setEu_version(e.attributeValue("eu_version"));
        part.setVersion(e.attributeValue("version"));
        part.setChange(Boolean.valueOf(e.attributeValue("isChange")));
        part.setMiddleIndex(middleIndex);

        part.setMtype(e.attributeValue("MTYPE"));
        part.setCsize(e.attributeValue("CSIZE"));
        part.setXhph(e.attributeValue("XHPH"));
        part.setJstj(e.attributeValue("JSTJ"));
        part.setAdjustable(e.attributeValue("ADJUSTABLE"));

        VaTreeNode node = new VaTreeNode(new VaEPartInstance(part));

        if (part.getType().equals("middle")) {
            node.setOccId(occId + middleIndex);
            node.setOccpath(occpath + middleIndex);
        } else {
            node.setOccId(occId);
            node.setOccpath(occpath);
        }
        node.setDw2("个");
        setNewLifecycle(node);
        return node;
    }

    private String toWTPartType(String partType) {
        if ("WTPart".equals(partType)) {
            return "wt.part.WTPart";
        }
        return partType;
    }

    @SuppressWarnings("unchecked")
    public void addChildNode(Element e, VaTreeNode node, int level) {
        if (level == stopLevel) {
            return;
        }
        level++;
        // 第一层childs层
        List<Element> list = e.elements();
        for (Element e1 : list) {
            // 第二层QMPartInfo层
            List<Element> childList = e1.elements();
            for (Element e2 : childList) {
                String occid = e2.attributeValue("occId");
                String occpath = e2.attributeValue("occpath");
                String middleIndex = e2.attributeValue("middleIndex");
                String[] occIds = null;
                String[] occpaths = null;
                String[] middleIndexs = null;
                if ("标准件".equals(e2.attributeValue("MTYPE")) || "元器件".equals(e2.attributeValue("MTYPE")) || "外购件".equals(e2.attributeValue("MTYPE")) || "主要材料".equals(e2.attributeValue("MTYPE"))) {
                    continue;
                }
                if (occid.indexOf(e.attributeValue("partNumber")) > 0) {
                    occIds = occid.split("&" + e.attributeValue("partNumber") + "&");
                    occIds = occIds[0].split(",");
                } else {
                    occIds = occid.split(",");
                }

                if (occpath.indexOf(e.attributeValue("partNumber")) > 0) {
                    occpaths = occpath.split("&" + e.attributeValue("partNumber") + "&");
                    occpaths = occpaths[0].split(",");
                } else {
                    occpaths = occpath.split(",");
                }

                if (middleIndex.indexOf(e.attributeValue("partNumber")) > 0) {
                    middleIndexs = middleIndex.split("&" + e.attributeValue("partNumber") + "&");
                    middleIndexs = middleIndexs[0].split(",");
                } else {
                    middleIndexs = middleIndex.split(",");
                }


                VaTreeNode brother = null;
                String gysl = BomXMLUtil.getGYSLFromWNC(e2, e2.attributeValue("partNumber"));
                if (gysl != null && !"".equals(gysl)) {
                    int sl = Integer.valueOf(gysl);
                    if (sl > 0) {
                        String occidfont = occIds[0].substring(0, occIds[0].indexOf("@") + 1);
                        for (int i = 0; i < sl; i++) {
                            String curOccId = occidfont + String.valueOf(i + 1);
                            String curOccpath = "-1+" + curOccId;
                            brother = addTreeNode(e2, curOccId, curOccpath, middleIndexs[0]);
                            node.add(brother);
                        }
                    }
                }
//				if(occIds.length>0){
//				   String occidfont=occIds[0].substring(0, occIds[0].indexOf("@")+1);
//				for (int i = 0; i <occIds.length; i++) {
//						String curOccId = occidfont+String.valueOf(i+1);
//						String curOccpath = "-1+" + curOccId;
//						String curMiddleIndex = middleIndexs[i];
//						brother = addTreeNode(e2, curOccId, curOccpath, curMiddleIndex);
//					node.add(brother);
//				}
//			  }
            }
        }
    }

    public static long emptyToLong(String obj) {
        if (null == obj || "".equals(obj)) {
            return 0;
        } else {
            return Long.parseLong(obj);
        }
    }

    /**
     * 读取XML的时候获取零件最新的状态
     *
     * @param node
     * @author chenyunlong
     * @date 2013-4-16
     */
    public static void setNewLifecycle(VaTreeNode node) {
        // VaTreeNode brother = CmCommonUtil.checkTheNodeIsInList(node,
        // planingList);
        // if(null ==brother){
        // WTPart wtPart = (WTPart) VaSearchHelper.search(WTPart.class,
        // node.getPart().getOid());
        // node.getPart().setLifecycle(wtPart.getLifeCycleState().getDisplay(Locale.CHINA));
        // planingList.add(node);
        // }
        // else{
        // node.getPart().setLifecycle(brother.getPart().getLifecycle());
        // }
    }

    @SuppressWarnings("unchecked")
    public void buildPBOMTree(List<Element> list) {
        VaTreeNode childnode = null;
        try {
            for (Element e : list) {
                String[] occId = e.attributeValue("occId").split(",");
                String[] occpath = e.attributeValue("occpath").split(",");
                String[] middleIndex = e.attributeValue("middleIndex").split(",");
                for (int i = 0; i < occId.length; i++) {
                    childnode = addTreeNode(e, occId[i], occpath[i], middleIndex[i]);
                    if (!"".equals(childnode.toString())) {
                        addChildNode(e, childnode);
                    }
                }
            }
            if(childnode!=null){
            	root.add(childnode);
            }
//			FittingsDistributionFrame.getMatrixFromEbom(root);
        } catch (Exception e) {
            log.debug(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void addChildNode(Element e, VaTreeNode node) {


        // 第一层childs层
        List<Element> list = e.elements();
        for (Element e1 : list) {
            // 第二层QMPartInfo层
            List<Element> childList = e1.elements();
            for (Element e2 : childList) {
                String[] occId = null;
                String[] occpath = null;
                String[] middleIndex = null;
                String[] path = null;
                String[] id = null;
                String[] index = null;

                String occpathe = e2.attributeValue("occpath");
                String occide = e2.attributeValue("occId");
                String indexe = e2.attributeValue("middleIndex");
                System.out.println(e2.attributeValue("MTYPE"));

                if (null != occpathe && !"".equals(occpathe) && !"assistant".equals(e2.attributeValue("partType"))) {
                    for (int m = 0; m < packageList.size(); m++) {
                        path = occpathe.split("&" + packageList.get(m).get(1) + "&");
                        id = occide.split("&" + packageList.get(m).get(1) + "&");
                        index = indexe.split("&" + packageList.get(m).get(1) + "&");
                        if (path.length == 1) {
                            occpathe = path[0];
                            occide = id[0];
                            indexe = index[0];
                        } else {
                            occpathe = path[Integer.valueOf(packageList.get(m).get(0))];
                            occide = id[Integer.valueOf(packageList.get(m).get(0))];
                            indexe = index[Integer.valueOf(packageList.get(m).get(0))];
                        }
                    }
                }

                occpath = occpathe.split(",");
                occId = occide.split(",");
                middleIndex = indexe.split(",");

                // 如果isHasXml字段为false时,到后台查询零件
                // checkElementHasXml(e2);

                if (occId.length > 1 && e2.elements().size() > 0 && !checkisOnlyHasAssistantNode(e2)) {
                    // 整件打包，或带有子节点的中间件，并且中间件数量不为1
                    if (Integer.valueOf(e2.attributeValue("useCount")) > 0) {
                        List<String> cmlist = new ArrayList<String>();
                        cmlist.add(0 + "");
                        cmlist.add(e2.attributeValue("partNumber"));
                        packageList.add(cmlist);
                    }
                    addPackageChildNode(e2, node, occId.length, false);
                    if (Integer.valueOf(e2.attributeValue("useCount")) > 0) {
                        packageList.remove(packageList.size() - 1);
                    }
                } else {
                    if ("middle".equals(e2.attributeValue("partType"))) {// 中间件
                        int useCount = Integer.valueOf(e2.attributeValue("useCount"));
                        if (e2.elements().size() > 0) {
                            addPackageChildNode(e2, node, useCount, false);
                        } else {
                            for (int k = 0; k < useCount; k++) {
                                VaTreeNode brother = addTreeNode(e2, e2.attributeValue("occId"),
                                        e2.attributeValue("occpath"), e2.attributeValue("middleIndex"));
                                node.add(brother);
                                brother.getPart().setUseCount(1);
                            }
                        }
                    } else if ("assistant".equals(e2.attributeValue("partType"))) {// 辅件
                        VaTreeNode assist = addTreeNode(e2, e2.attributeValue("occId"), e2.attributeValue("occId"),
                                e2.attributeValue("middleIndex"));
                        // node.add(assist);
                    } else {
                        String[] paths = null;
                        String[] brothers = null;
                        String[] middle = null;
                        occpathe = e2.attributeValue("occpath");
                        occide = e2.attributeValue("occId");
                        indexe = e2.attributeValue("middleIndex");
                        for (int m = 0; m < packageList.size(); m++) {
                            paths = occpathe.split("&" + packageList.get(m).get(1) + "&");
                            brothers = occide.split("&" + packageList.get(m).get(1) + "&");
                            middle = indexe.split("&" + packageList.get(m).get(1) + "&");
                            if (paths.length == 1) {
                                occpathe = paths[0];
                                occide = brothers[0];
                                indexe = middle[0];
                            } else {
                                occpathe = paths[Integer.valueOf(packageList.get(m).get(0))];
                                occide = brothers[Integer.valueOf(packageList.get(m).get(0))];
                                indexe = middle[Integer.valueOf(packageList.get(m).get(0))];
                            }
                        }
                        paths = occpathe.split(",");
                        brothers = occide.split(",");
                        middle = indexe.split(",");
                        for (int j = 0; j < brothers.length; j++) {
                            VaTreeNode brother = addTreeNode(e2, brothers[j], paths[j], middle[j]);

                            node.add(brother);
                            if (!isOnlyHasAssistantElements(e2)) {
                                addChildNode(e2, brother);
                            } else {
                                addAssistOnCommonNode(e2, brother);
                            }
                        }
                    }
                }
            }
        }
    }


    public void addChildNode2(Element e, VaTreeNode node) {
        // 第一层childs层
        List<Element> list = e.elements();
        for (Element e1 : list) {
            // 第二层QMPartInfo层
            List<Element> childList = e1.elements();
            for (Element e2 : childList) {
                VaTreeNode childnode = null;
                String usecount = null;
                String gysl = null;
                int size = 0;
                usecount = e2.attributeValue("useCount");
                if (null == usecount || "null".equals(usecount) || "".equals(usecount)) {
                    usecount = "0";
                }
                gysl = e2.attributeValue("gysl");
                if (null == gysl || "null".equals(gysl) || "".equals(gysl)) {
                    gysl = "0";
                }
                size = Integer.valueOf(gysl) - Integer.valueOf(usecount);
                if (size <= 0) {
                    childnode = addTreeNode(e2, "", "", "");
                    childnode.setUsed(true);
                    node.add(childnode);
                } else {
                    String occid = e2.attributeValue("occId");
                    String[] occIds = null;
                    if (occid.indexOf(e.attributeValue("partNumber")) > 0) {
                        occIds = occid.split("&" + e.attributeValue("partNumber") + "&");
                        occIds = occIds[0].split(",");
                    } else {
                        occIds = occid.split(",");
                    }

                    int k = occIds.length;
                    String occidfont = occIds[0].substring(0, occIds[0].indexOf("@") + 1);
                    for (int i = k; i < k + size; i++) {
                        String newOccId = occidfont + Integer.valueOf(i + 1);
                        String newOccpath = "-1+" + newOccId;
                        String partType = e2.attributeValue("partType");
                        childnode = addTreeNode(e2, newOccId, newOccpath, "0");
                        node.add(childnode);
                    }

                }

            }
        }
    }

    /**
     * 判断当前节点(非工艺中间件)下的子零件是否只有工艺辅件或没有子零件
     *
     * @return
     * @date 2013-1-17
     */
    @SuppressWarnings("unchecked")
    public boolean checkisOnlyHasAssistantNode(Element e) {
        boolean flag = true;
        List<Element> list = e.elements();
        for (Element e1 : list) {
            // 第二层QMPartInfo层
            List<Element> childList = e1.elements();
            for (Element e2 : childList) {
                if (!"assistant".equals(e2.attributeValue("partType"))) {
                    flag = false;
                }
            }
        }
        return flag;
    }

    /**
     * 判断子节点是否只有辅件
     *
     * @param assist
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean isOnlyHasAssistantElements(Element assist) {
        boolean flag = true;
        // 第一层childs层
        List<Element> list = assist.elements();
        for (Element e1 : list) {
            // 第二层QMPartInfo层
            List<Element> childList = e1.elements();
            for (Element e2 : childList) {
                if (!"assistant".equals(e2.attribute("partType"))) {
                    flag = false;
                }
            }
        }
        return flag;
    }

    @SuppressWarnings("unchecked")
    public void addPackageChildNode(Element e, VaTreeNode node, int packageSize, boolean isPackageOfParent) {
        for (int i = 0; i < packageSize; i++) {
            for (int n = 0; n < packageList.size(); n++) {
                if (isEqual(e.attributeValue("partNumber"), packageList.get(n).get(1))) {
                    packageList.get(n).set(0, i + "");
                }
            }
            VaTreeNode childnode = null;
            String[] occId = null;
            String[] occpath = null;
            String[] middleIndex = null;
            String[] path = null;
            String[] id = null;
            String[] index = null;
            String occpathe = e.attributeValue("occpath");
            String occide = e.attributeValue("occId");
            String indexe = e.attributeValue("middleIndex");
            if ("middle".equals(e.attributeValue("partType"))) {
                if (isPackageOfParent || e.attributeValue("occpath").split("&").length > 1) {
                    for (int m = 0; m < packageList.size(); m++) {
                        if (!isEqual(e.attributeValue("partNumber"), packageList.get(m).get(1))) {
                            path = occpathe.split("&" + packageList.get(m).get(1) + "&");
                            id = occide.split("&" + packageList.get(m).get(1) + "&");
                            index = indexe.split("&" + packageList.get(m).get(1) + "&");
                            if (path.length == 1) {
                                occpathe = path[0];
                                occide = id[0];
                                indexe = index[0];
                            } else {
                                occpathe = path[Integer.valueOf(packageList.get(m).get(0))];
                                occide = id[Integer.valueOf(packageList.get(m).get(0))];
                                indexe = index[Integer.valueOf(packageList.get(m).get(0))];
                            }
                        }
                    }
                    occpath = occpathe.split(",");
                    occId = occide.split(",");
                    middleIndex = indexe.split(",");
                } else {
                    occId = e.attributeValue("occId").split(",");
                    occpath = e.attributeValue("occpath").split(",");
                    middleIndex = e.attributeValue("middleIndex").split(",");
                }
                childnode = addTreeNode(e, occId[i], occpath[i], middleIndex[i]);
                childnode.getPart().setUseCount(1);
            } else {
                if (null != occpathe && !"".equals(occpathe) && !"assistant".equals(e.attributeValue("partType"))) {
                    for (int m = 0; m < packageList.size(); m++) {
                        path = occpathe.split("&" + packageList.get(m).get(1) + "&");
                        id = occide.split("&" + packageList.get(m).get(1) + "&");
                        index = indexe.split("&" + packageList.get(m).get(1) + "&");
                        if (path.length == 1) {
                            occpathe = path[0];
                            occide = id[0];
                            indexe = index[0];
                        } else {
                            occpathe = path[Integer.valueOf(packageList.get(m).get(0))];
                            occide = id[Integer.valueOf(packageList.get(m).get(0))];
                            indexe = index[Integer.valueOf(packageList.get(m).get(0))];
                        }
                    }
                }
                occpath = occpathe.split(",");
                occId = occide.split(",");
                middleIndex = indexe.split(",");
                childnode = addTreeNode(e, occId[i], occpath[i], middleIndex[i]);
            }
            node.add(childnode);

            // 第一层childs层
            List<Element> list = e.elements();
            for (Element e1 : list) {
                // 第二层QMPartInfo层
                List<Element> childList = e1.elements();
                for (Element e2 : childList) {
                    occId = null;
                    occpath = null;
                    middleIndex = null;
                    path = null;
                    id = null;
                    index = null;
                    occpathe = e2.attributeValue("occpath");
                    occide = e2.attributeValue("occId");
                    indexe = e2.attributeValue("middleIndex");
                    if (null != occpathe && !"".equals(occpathe) && !"assistant".equals(e2.attributeValue("partType"))) {
                        for (int m = 0; m < packageList.size(); m++) {
                            path = occpathe.split("&" + packageList.get(m).get(1) + "&");
                            id = occide.split("&" + packageList.get(m).get(1) + "&");
                            index = indexe.split("&" + packageList.get(m).get(1) + "&");
                            if (path.length == 1) {
                                occpathe = path[0];
                                occide = id[0];
                                indexe = index[0];
                            } else {
                                occpathe = path[Integer.valueOf(packageList.get(m).get(0))];
                                occide = id[Integer.valueOf(packageList.get(m).get(0))];
                                indexe = index[Integer.valueOf(packageList.get(m).get(0))];
                            }
                        }
                    }
                    occpath = occpathe.split(",");
                    occId = occide.split(",");
                    middleIndex = indexe.split(",");
                    // checkElementHasXml(e2);
                    if (occId.length > 1 && e2.elements().size() > 0 && !checkisOnlyHasAssistantNode(e2)) {
                        // 整件打包，或带有子节点的中间件，并且中间件数量不为1
                        if (Integer.valueOf(e2.attributeValue("useCount")) > 0) {
                            List<String> cmlist = new ArrayList<String>();
                            cmlist.add(i + "");
                            cmlist.add(e2.attributeValue("partNumber"));
                            packageList.add(cmlist);
                        }
                        addPackageChildNode(e2, childnode, occId.length, true);
                        if (Integer.valueOf(e2.attributeValue("useCount")) > 0) {
                            packageList.remove(packageList.size() - 1);
                        }
                    } else {
                        if ("middle".equals(e2.attributeValue("partType"))) {
                            int useCount = Integer.valueOf(e2.attributeValue("useCount"));
                            if (e2.elements().size() > 0) {
                                addPackageChildNode(e2, childnode, useCount, true);
                            } else {
                                for (int k = 0; k < useCount; k++) {
                                    VaTreeNode brother = addTreeNode(e2, e2.attributeValue("occId"),
                                            e2.attributeValue("occpath"), e2.attributeValue("middleIndex"));
                                    childnode.add(brother);
                                    brother.getPart().setUseCount(1);
                                }
                            }
                        } else if ("assistant".equals(e2.attributeValue("partType"))) {
                            VaTreeNode assist = addTreeNode(e2, occId[0], occpath[0], middleIndex[0]);
                            childnode.add(assist);
                        } else {
                            String[] paths = null;
                            String[] brothers = null;
                            String[] middle = null;
                            occpathe = e2.attributeValue("occpath");
                            occide = e2.attributeValue("occId");
                            indexe = e2.attributeValue("middleIndex");
                            for (int m = 0; m < packageList.size(); m++) {
                                paths = occpathe.split("&" + packageList.get(m).get(1) + "&");
                                brothers = occide.split("&" + packageList.get(m).get(1) + "&");
                                middle = indexe.split("&" + packageList.get(m).get(1) + "&");
                                if (paths.length == 1) {
                                    occpathe = paths[0];
                                    occide = brothers[0];
                                    indexe = middle[0];
                                } else {
                                    occpathe = paths[Integer.valueOf(packageList.get(m).get(0))];
                                    occide = brothers[Integer.valueOf(packageList.get(m).get(0))];
                                    indexe = middle[Integer.valueOf(packageList.get(m).get(0))];
                                }
                            }
                            paths = occpathe.split(",");
                            brothers = occide.split(",");
                            middle = indexe.split(",");
                            for (int j = 0; j < brothers.length; j++) {
                                VaTreeNode brother = addTreeNode(e2, brothers[j], paths[j], middle[j]);
                                childnode.add(brother);
                                if (!isOnlyHasAssistantElements(e2)) {
                                    addChildNode(e2, brother);
                                } else {
                                    addAssistOnCommonNode(e2, brother);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // public void checkElementHasXml(Element e){
    // if("false".equals(e.attributeValue("isHasXml"))){
    // boolean flag = false;
    // for(Map<String,Object> map:byteslist){
    // if(e.attributeValue("partNumber").equals(map.get(e.attributeValue("partNumber")))){
    // e.setAttributeValue("isHasXml", "true");
    // flag = true;
    // break;
    // }
    // }
    // if(!flag){
    // byte[] bytes = PBOMEditorToWCIntf.getPBOMXml(e.attributeValue("oid"));
    // if(null != bytes){
    // Map<String,Object> map = new HashMap<String,Object>();
    // map.put("partNumber", e.attributeValue("partNumber"));
    // map.put("oid", e.attributeValue("oid"));
    // map.put("bytes", bytes);
    // byteslist.add(map);
    // e.setAttributeValue("isHasXml", "true");
    // }
    // }
    // }
    // }


    /**
     * 添加辅件的node
     *
     * @param e
     * @param common
     */
    @SuppressWarnings("unchecked")
    public void addAssistOnCommonNode(Element e, VaTreeNode common) {
        if ("assistant".equals(e.attribute("partType"))) {
            // 第一层childs层
            List<Element> list = e.elements();
            for (Element e1 : list) {
                // 第二层QMPartInfo层
                List<Element> childList = e1.elements();
                for (Element e2 : childList) {
                    VaTreeNode assist = addTreeNode(e2, e.attributeValue("occId"), e.attributeValue("occpath"),
                            e.attributeValue("middleIndex"));
                    common.add(assist);
                }
            }
        }
    }

    public static boolean isEqual(String obj1, String obj2) {
        if (emptyToString(obj1).equals(emptyToString(obj2))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param obj
     * @return
     * @date 2013-10-11
     */
    public static String emptyToString(Object obj) {
        if (null == obj) {
            return "";
        } else {
            String objStr = obj.toString();
            if ("".equals(objStr.trim())) {
                return "";
            } else {
                return objStr;
            }
        }
    }

    private static String CMUtilToString(String value) {
        if (!"".equals(value) && !"null".equals(value) && value != null) {
            return value;
        } else {
            return "";
        }

    }

    public static Map<String, Integer> getNodeUsedMap() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        String xmlPath = FittingsDistributionFrame.getPathString();
        Document document = XmlUtil.getDocument(new File(xmlPath));
        Element rootElement = document.getRootElement();
        Element elementsByName = XmlUtil.getElementsByName(rootElement, "QMFawTechnicsInfo").get(0);
        List<Element> stepElements = elementsByName.selectNodes("steps/QMProcedureInfo");
        List<Element> paceElements = elementsByName.selectNodes("steps/QMProcedureInfo/paces/QMProcedureInfo");
        for (int i = 0; i < stepElements.size(); i++) {
            Element element = stepElements.get(i);
            List<Element> selectNodes = element.selectNodes("parts/QMPartInfo");
            for (int j = 0; j < selectNodes.size(); j++) {
                Element childElement = selectNodes.get(j);
                String occid = childElement.attributeValue("occId");
                String zcmark = childElement.attributeValue("ZCMARK");
                if (map.containsKey(occid)) {
                    Integer jishu = map.get(occid);
                    if ("Z".equals(zcmark)) {
                        jishu = jishu + 1;
                    } else {
                        jishu = jishu - 1;
                    }
                    map.put(occid, jishu);
                } else {
                    map.put(occid, 0);
                }
            }
        }
        for (int i = 0; i < paceElements.size(); i++) {
            Element element = paceElements.get(i);
            List<Element> selectNodes = element.selectNodes("parts/QMPartInfo");
            for (int j = 0; j < selectNodes.size(); j++) {
                Element childElement = selectNodes.get(j);
                String occid = childElement.attributeValue("occId");
                String zcmark = childElement.attributeValue("ZCMARK");
                if (map.containsKey(occid)) {
                    Integer jishu = map.get(occid);
                    if ("Z".equals(zcmark)) {
                        jishu = jishu + 1;
                    } else {
                        jishu = jishu - 1;
                    }
                    map.put(occid, jishu);
                } else {
                    map.put(occid, 0);
                }
            }
        }
        return map;
    }

    public static void sortVatree(VaTreeNode root) {
        //FPBOM列表根据型号牌号排序 add by zhuhao 2017.12.27
        List<VaTreeNode> lis = new ArrayList<VaTreeNode>();
        for (int i = 0; i < root.getChildCount(); i++) {
            VaTreeNode va = (VaTreeNode) root.getChildAt(i);
            lis.add(va);
        }
        Collections.sort(lis, new Comparator<VaTreeNode>() {
                    @Override
                    public int compare(VaTreeNode o1, VaTreeNode o2) {
                        if (o1.getXhph() != null && o2.getXhph() != null) {
                            return o1.getXhph().compareTo(o2.getXhph());
                        } else {
                            return o1.getPart().getNumber().compareTo(o2.getPart().getNumber());
                        }
                    }
                }
        );
        Iterator iterator_xhph = lis.iterator();
        root.removeAllChildren();
        while (iterator_xhph.hasNext()) {
            VaTreeNode s = (VaTreeNode) iterator_xhph.next();
            root.add(s);
        }
        //FPBOM列表排序 end
    }

    public static String getDataType(String wzlb){
        String dataType;
        if(wzlb.startsWith("01")){
            dataType = "元器件";
        }else if(wzlb.startsWith("02")){
            dataType = "标准紧固件";
        }else if(wzlb.startsWith("03")){
            dataType = "金属材料";
        }else if(wzlb.startsWith("04")){
            dataType = "非金属材料";
        }else if(wzlb.startsWith("05")){
            dataType = "复合材料";
        }else if(wzlb.startsWith("06")){
            dataType = "机电材料";
        }else if(wzlb.startsWith("07")){
            dataType = "火工品";
        }else if(wzlb.startsWith("08")){
            dataType = "劳防、文版用品";
        }else{
            dataType = "";
        }
        return dataType;
    }
}

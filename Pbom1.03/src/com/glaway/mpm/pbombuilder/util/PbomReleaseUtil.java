package com.glaway.mpm.pbombuilder.util;

import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmScrollPaneTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;

import java.util.*;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2018/11/1
 * @ Description：
 * @ Modified By：
 */
public class PbomReleaseUtil {

    private static final CmLogger log = CmLogger.getLogger(PbomReleaseUtil.class.getName());

    private static String useroid;
    /**
     * 得到所有改动过的节点：新增、删除、移动、属性修改、移动和属性修改
     *
     * @author chenyunlong
     * @date 2013-3-20
     * @param root
     * @return
     *
     */
    public static List<List<Object>> dealWithAllChangeNodes(CmTreeNode root) {
        // 记录所有变化的节点信息
        List<List<Object>> updateNodesList = new ArrayList<List<Object>>();
        // 记录PBOM树上完全不同的节点信息 ,此处用处是----如果有多个相同的零件，节点属性有修改有且只修改一次
        List<CmTreeNode> onlyNodeList = new ArrayList<CmTreeNode>();
        updatePbomPlanning((CmTreeNode) root.children().nextElement(), null, onlyNodeList, updateNodesList);
        // 更新planning视图-----删除节点
        List<CmTreeNode> delList = getDeleteNode(root);
        //System.out.println("----------------delete-------------");
        for (CmTreeNode cmnode : delList) {
            // if ("delete".equals(cmnode.getPart().getOperType())) {
            log.debug("删除的节点*****：" + cmnode.toString() + "===============" + cmnode.getPart().getOperType());
            log.debug("OldParentPartOid" + "==============" + getOldParentPartOid(cmnode));
            log.debug("oid" + "==============" + String.valueOf(cmnode.getPart().getOid()));
            List<Object> ulist = new ArrayList<Object>();
            ulist.add("delete");
            ulist.add(getOldParentPartOid(cmnode));
            ulist.add(String.valueOf(cmnode.getPart().getOid()));
            ulist.add(cmnode.getPart().getPartType());
            updateNodesList.add(ulist);
        }
        System.out.println("----------------delete-------------");

        return updateNodesList;
    }

    public static List<CmTreeNode> getDeleteNode(CmTreeNode root) {
        List<CmTreeNode> delList = new ArrayList<CmTreeNode>();
        List<CmTreeNode> realDelList = new ArrayList<CmTreeNode>();
        HashMap<CmTreeNode, CmTreeNode> mMap = new HashMap<CmTreeNode, CmTreeNode>(); // MBOM修改后
        // 结构
        HashMap<CmTreeNode, CmTreeNode> initMap = CmScrollPaneTree.pbomMap;
        CmCommonStringUtil.node2StructureMap(root, mMap);

        Set<CmTreeNode> set = mMap.keySet();
        for (CmTreeNode node : set) {
            initMap.remove(node);
        }

        set = initMap.keySet();
        for (CmTreeNode node : set) {
            CmTreeNode delNode = initMap.get(node);
            if(delNode != null){
                delNode.getPart().setOperType(node.getPart().getOperType()); // 修改数量
                delList.add(initMap.get(delNode));
            }
        }

        for (CmTreeNode n1 : delList) {
            CmTreeNode n = (CmTreeNode) n1.getParent();
            if (!"new".equals(n1.getPart().getOperType())) { // 修改数量 添加
                // if(!n1.isNewTopNode()){ //刚添加就删除的情况
                if (!delList.contains(n)) {
                    realDelList.add(n1);
                }
                // }

            }
        }

        return realDelList;

    }


    public static boolean isHasOtherNodeInTree(List<CmTreeNode> list, CmTreeNode node) {
        boolean flag = false;
        for (CmTreeNode treeNode : list) {
            if (CmCommonStringUtil.isCommon(node, treeNode)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public static CmLightPart getParentPart(CmTreeNode node, CmTreeNode parent) {
        return null == node.getParent() ? parent.getPart() : ((CmTreeNode) node.getParent()).getPart();
    }

    @SuppressWarnings("unchecked")
    public static void updatePbomPlanning(CmTreeNode node, CmTreeNode parent, List<CmTreeNode> list, List<List<Object>> updateNodesList) {
        log.debug(node.toString());
        CmLightPart part = node.getPart();
        boolean flag = isHasOtherNodeInTree(list, node);// 是否已经处理过相同的节点

        if ("new".equals(part.getOperType())) {
            boolean isAdd = true;
            if (node.getParent() == null) {
                isAdd = false;
            } else {
                for (CmTreeNode lNode : list) {
                    if (CmCommonStringUtil.isCommon(lNode, node)) {
                        if ("new".equals(lNode.getPart().getOperType())) {
                            if (CmCommonStringUtil.isCommon((CmTreeNode) lNode.getParent(), (CmTreeNode) node.getParent())) {
                                isAdd = false;
                                if (lNode.getParent().equals(node.getParent())) {
                                    isAdd = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (isAdd) {
                // 更新planning视图-----新增节点
                log.debug("新增的节点*****：" + node.toString() + "===============" + node.getPart().getOperType());
                log.debug("parentPartOid" + "==============" + getParentPart(node, parent).getOid());
                log.debug("oid" + "==============" + node.getPart().getOid());
                log.debug("partNumber" + "==============" + part.getPartNumber(), part.getPartName());
                log.debug("containerId" + "==============" + getParentPart(node, parent).getContainerId());
                List<Object> ulist = new ArrayList<Object>();
                // 使用数量
                int count = 1;
                if (CmCommonStringUtil.isPackage(node)) {
                    count += node.getListNode().size();
                } else {
                    count = node.getPart().getUseCount();
                }

                ulist.add("new");
                ulist.add(getParentPart(node, parent).getOid() + "");
                ulist.add(node.getPart().getOid() + "");
                ulist.add(part.getPartType());
                ulist.add(getNodePart(part, part.isEdit()));
                // TODO 812 link invcode
                Wzk wzk = part.getWzk();
                ulist.add(CmCommonStringUtil.emptyToString(wzk.getInvcode()));

                updateNodesList.add(ulist);
                if (!CmCommonStringUtil.isEmpty(useroid) && CmCommonStringUtil.isNewNode(part.getPartType())) {
                    part.setResponser(useroid);
                }
                ulist.add(count);
                list.add(node);
            }

            // part.setOperType("");
            // part.setEdit(false);
            // part.setMove(false);
        } else if (part.isMove()) {
            // 更新planning视图---节点位置有变化
            log.debug("节点位置和节点属性都变化：" + node.toString() + "===============" + node.getPart().getOperType());
            log.debug("OldParentPartOid" + "==============" + getOldParentPartOid(node));
            log.debug("Parent" + "==============" + getNewParentPartOid(node, parent));
            log.debug("oid" + "==============" + String.valueOf(part.getOid()));
            List<Object> ulist = new ArrayList<Object>();
            ulist.add("move");
            ulist.add(getOldParentPartOid(node));
            ulist.add(getNewParentPartOid(node, parent));
            ulist.add(String.valueOf(part.getOid()));
            ulist.add(part.getPartType());
            Map<String, String> map = new HashMap<String, String>();
            if (!flag) {
                map = getNodePart(part, part.isEdit());
            }
            ulist.add(map);

            // TODO 812 link invcode
            Wzk wzk = part.getWzk();
            ulist.add(CmCommonStringUtil.emptyToString(wzk.getInvcode()));

            updateNodesList.add(ulist);

            // part.setEdit(false);
            // part.setMove(false);
        } else if (!part.isMove() && part.isEdit() && !flag) {
            // 更新planning视图---节点属性有修改---如果有多个相同的零件，有且只修改一次
            log.debug("节点属性变化的节点*****：" + node.toString() + "===============" + node.getPart().getOperType());
            List<Object> ulist = new ArrayList<Object>();
            ulist.add("modify");
            ulist.add(getParentPart(node, parent).getOid() + "");
            // ulist.add(node.getPart().getOid() + "");
            ulist.add(String.valueOf(part.getOid()));
            // TODO 812 link invcode
            Wzk wzk = part.getWzk();
            ulist.add(CmCommonStringUtil.emptyToString(wzk.getInvcode()));

            ulist.add(getNodePart(part, true));

            // 使用数量
            int count = 1;
            if (CmCommonStringUtil.isPackage(node)) {
                count += node.getListNode().size();
            } else {
                count = node.getPart().getUseCount();
            }

            ulist.add(count);

            updateNodesList.add(ulist);
            // part.setEdit(false);
        }
        if (!flag) {
            list.add(node);
        }
        Enumeration children = node.children();
        while (children.hasMoreElements()) {
            CmTreeNode child = (CmTreeNode) children.nextElement();
            updatePbomPlanning(child, null, list, updateNodesList);
        }
        if (CmCommonStringUtil.isPackage(node)) {
            for (CmTreeNode brother : node.getListNode()) {
                updatePbomPlanning(brother, (CmTreeNode) node.getParent(), list, updateNodesList);
            }
        }
    }

    public static Map<String, String> getNodePart(CmLightPart part, boolean isEdit) {
        Map<String, String> map = new HashMap<String, String>();
        if (isEdit) {
            // map.put("isKey", String.valueOf(part.isKey()));
            // map.put("isSpecial", String.valueOf(part.isSpecial()));
            // map.put("spaceBorneTable",
            // String.valueOf(part.isSpaceBorneTable()));
            // map.put("workShop",
            // CmCommonStringUtil.isEmpty(part.getWorkShop())?"":part.getWorkShop());
            // map.put("outsourcingUnits",
            // CmCommonStringUtil.isEmpty(part.getOutsourcingUnits())?"":part.getOutsourcingUnits());
            // map.put("materialType",
            // CmCommonStringUtil.isEmpty(part.getMaterialType())?"":part.getMaterialType());
            // map.put("backupRate",
            // CmCommonStringUtil.isEmpty(part.getBackupRate())?"0":part.getBackupRate());
            // map.put("maxBackupCount",
            // CmCommonStringUtil.isEmpty(part.getMaxBackupCount())?"0":part.getMaxBackupCount());
            // map.put("backupReason",
            // CmCommonStringUtil.isEmpty(part.getBackupReason())?"":part.getBackupReason());
            // map.put("remark",
            // CmCommonStringUtil.isEmpty(part.getRemark())?"":part.getRemark());

            // TODO 添加149属性 保存
            map.put("ZZCJ", CmCommonStringUtil.emptyToString(part.getZzcj()));
            map.put("FZCJ", CmCommonStringUtil.emptyToString(part.getFzcj()));

            String zzcj = part.getZzcj();
            String fzcj = part.getFzcj();
            String routing = "";
            if (zzcj != null && !"".equals(zzcj)) {
                routing = zzcj;
            }
            if (fzcj != null && !"".equals(fzcj)) {
                if (!"".equals(routing)) {
                    routing = routing + "-" + fzcj;
                } else {
                    routing = fzcj;
                }
            }
            map.put("ROUTING", routing);

            map.put("MTYPE", CmCommonStringUtil.emptyToString(part.getMtype()));
            map.put("BATCH", CmCommonStringUtil.emptyToString(part.getBatch()));

            map.put("CMAT", CmCommonStringUtil.emptyToString(part.getCmat()));
            map.put("PTC_MATERIAL_NAME", CmCommonStringUtil.emptyToString(part.getPtc_material_name()));
            map.put("CMAT_UP", CmCommonStringUtil.emptyToString(part.getCmat_up()));
            map.put("CMAT_DOWN", CmCommonStringUtil.emptyToString(part.getCmat_down()));
            map.put("SETMARK", CmCommonStringUtil.emptyToString(part.getSetmark()));
            map.put("ADJUSTABLE", CmCommonStringUtil.emptyToString(part.getAdjustable()));
            map.put("PHASE_CODE", CmCommonStringUtil.emptyToString(part.getPhase_code()));
            map.put("KEYCOMPONENT", CmCommonStringUtil.emptyToString(part.getKeycomponent()));
            map.put("CSIZE", CmCommonStringUtil.emptyToString(part.getCsize()));
            map.put("CTYPE", CmCommonStringUtil.emptyToString(part.getCtype()));
            map.put("SECRET", CmCommonStringUtil.emptyToString(part.getSecret()));
            map.put("ENDITEMIN", CmCommonStringUtil.emptyToString(part.getEnditemin()));
            map.put("MINDEX", CmCommonStringUtil.emptyToString(part.getMindex()));
            map.put("CINDEX", CmCommonStringUtil.emptyToString(part.getCindex()));
            map.put("PINDEX", CmCommonStringUtil.emptyToString(part.getPindex()));
            map.put("PTC_COMMON_NAME", CmCommonStringUtil.emptyToString(part.getPtc_common_name()));

            map.put("XHPH", CmCommonStringUtil.emptyToString(part.getXhph()));
            map.put("JSTJ", CmCommonStringUtil.emptyToString(part.getJstj()));
            // map.put("CHBM",CmCommonStringUtil.emptyToString(
            // part.getChbm()));

            // 设计资源库应用改造新增属性
            // start
            map.put("SHORTNAME", CmCommonStringUtil.emptyToString(part.getShortname()));// 物资简称
            map.put("STANDARDNUMBER", CmCommonStringUtil.emptyToString(part.getStandardnumber()));// 标准号
            map.put("MECHANICALPROPERTYORHARDNESS", CmCommonStringUtil.emptyToString(part.getMechanicalpropertyorhardness()));// 机械性能等级或硬度
            map.put("SURFACETREATMENT", CmCommonStringUtil.emptyToString(part.getSurfacetreatment()));// 表面处理
            map.put("HEATTREATMENT", CmCommonStringUtil.emptyToString(part.getHeattreatment()));// 热处理
            map.put("PRODUCTFORM", CmCommonStringUtil.emptyToString(part.getProductform()));// 产品型式
            map.put("PRODUCTLEVEL", CmCommonStringUtil.emptyToString(part.getProductlevel()));// 产品等级
            map.put("PLATECSCREWFORM", CmCommonStringUtil.emptyToString(part.getPlatecscrewform()));// 板拧形式
            map.put("ISIMPORT", CmCommonStringUtil.emptyToString(part.getIsimport()));// 是否进口
            map.put("SPECIALINSTRUCTION", CmCommonStringUtil.emptyToString(part.getSpecialinstruction()));// 特殊说明
            map.put("MEASUREUNIT", CmCommonStringUtil.emptyToString(part.getMeasureunit()));// 计量单位
            map.put("TYPE", CmCommonStringUtil.emptyToString(part.getType()));// 型号
            map.put("TYPESTANDARD", CmCommonStringUtil.emptyToString(part.getTypestandard()));// 型号规格
            map.put("QUALITYLEVEL", CmCommonStringUtil.emptyToString(part.getQualitylevel()));// 质量等级
            map.put("TOTALSTANDARD", CmCommonStringUtil.emptyToString(part.getTotalstandard()));// 总规范
            map.put("DETAILSTANDARD", CmCommonStringUtil.emptyToString(part.getDetailstandard()));// 详细规范
            map.put("PACKAGINGFORM", CmCommonStringUtil.emptyToString(part.getPackagingform()));// 封装形式
            map.put("OUTLINESIZE", CmCommonStringUtil.emptyToString(part.getOutlinesize()));// 外形尺寸
            map.put("SPECIALCONDITION", CmCommonStringUtil.emptyToString(part.getSpecialcondition()));// 专用条件
            map.put("EXTRACONDITION", CmCommonStringUtil.emptyToString(part.getExtracondition()));// 附加协议
            map.put("MATTYPE", CmCommonStringUtil.emptyToString(part.getMattype()));// 材料类型

            map.put("NUMBER", CmCommonStringUtil.emptyToString(part.getCmatnumber()));// 材料编号
            map.put("MARKNUMBER", CmCommonStringUtil.emptyToString(part.getMarknumber()));// 牌号
            map.put("SUPPLYSTATE", CmCommonStringUtil.emptyToString(part.getSupplystate()));// 供应状态
            map.put("USESTANDARD", CmCommonStringUtil.emptyToString(part.getUsestandard()));// 采用标准
            // end

            if (part.getWzk() != null) {
                // ErpUtil.setPartByWzk(map, part.getWzk(),
                // part.getWzk().getOpType());
            }

            String gysl = part.getGysl();
            if (gysl != null && !"".equals(gysl)) {
                map.put("GYSL", gysl);
            }
        }
        return map;
    }


    public static String getOldParentPartOid(CmTreeNode node) {
        // CmTreeNode cmnode = CmCommonStringUtil.checkTheNodeIsInList(node,
        // CmScrollPaneTree.pbomlist);
        CmTreeNode cmnode = CmScrollPaneTree.pbomMap.get(node);
        return null == cmnode ? "" : ((CmTreeNode) cmnode.getParent()).getPart().getOid() + "";
    }

    public static String getNewParentPartOid(CmTreeNode node, CmTreeNode parent) {
        return null == node.getParent() ? String.valueOf(parent.getPart().getOid()) : String.valueOf(((CmTreeNode) node.getParent()).getPart().getOid());
    }
}

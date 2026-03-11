package com.glaway.mpm.intf;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.sop.model.ParametersBean;
import com.glaway.mpm.sop.model.SopBean;
import com.glaway.mpm.sop.model.SopResourceBean;
import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.mpml.resource.MPMTooling;
import ext.casc.fileprint.cache.GLFilePrintData;
import ext.casc.fileprint.cache.GLFilePrintDataHelper;
import ext.casc.process.ProcessTaskItem;
import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.QueryUtil;
import ext.casc.sop.util.SopPartUtil;
import ext.casc.sop.util.SopUtil;
import ext.casc.sop.util.SopWorkflowUtil;
import ext.casc.util.IBAUtility;
import ext.casc.workflow.PrintHelper;
import org.apache.batik.dom.util.HashTable;
import org.json.JSONObject;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.fc.collections.WTCollection;
import wt.inf.container.WTContainer;
import wt.method.RemoteAccess;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;

import java.util.*;

import static ext.casc.fileprint.cache.GLFilePrintDataHelper.getObject;

public class SopProcessEditorToWCIntfRMI implements RemoteAccess {

    private static VaLogger logger = VaLogger.getLogger(SopProcessEditorToWCIntfRMI.class.getName());


    public static List<String> getAllCSXM(String zylb) {
        try {
        	List<String> csxm = new ArrayList<String>();
            List<WTPart> allCSXM = SopUtil.getAllCSXM(zylb);
            for (WTPart wtPart : allCSXM) {
				String name = wtPart.getName();
				csxm.add(name);
			}
            return csxm;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getSopResourceByType(String objType) {
        try {
        	List<String> sop = new ArrayList<String>();
            List<WTPart> sopResource = SopUtil.getSopResourceByType(objType);
            for (WTPart wtPart : sopResource) {
				String name = wtPart.getName();
				sop.add(name);
			}
            return sop;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过partNumber获得实时容器名称
     * @param partNumber
     * @return
     * @throws WTException
     */
 	public static String getProductNumberAndName (String partNumber) {
         boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
 		WTPart wtPart = null;
		try {
			wtPart = WTPartUtil.getPartByNumberAndView(partNumber, "Manufacturing");
		} catch (WTException e) {
			e.printStackTrace();
		}
 		WTContainer container = wtPart.getContainer();
 		String name = container.getName();
         SessionServerHelper.manager.setAccessEnforced(flag);
 		return name;
 	}

    public static Map<String, String> getSopProcessStepName(String zylb) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            List<MPMTooling> allGxmc = SopUtil.getAllGxmc(zylb);
            for (MPMTooling wtPart : allGxmc) {
                map.put(wtPart.getNumber(), wtPart.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static Map<String, String> getAllSopProcessStepName(String type) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            List<MPMTooling> allGxmc = SopUtil.getSOPAllGxmc();
            for (MPMTooling wtPart : allGxmc) {
                map.put(wtPart.getNumber(), wtPart.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static List<String> getSopResourceName(String objType) throws Exception {
    	return SopUtil.getSopResourceName(objType);
    }

    public static List<SopBean> querySop(SopBean sopBeanInfo) throws Exception {
    	return SopUtil.querySop(sopBeanInfo);
    }

    public static SopBean buildSopBean(WTDocument document) throws Exception {
    	return SopUtil.buildSopBean(document);
    }

    public static SopBean buildSopBeanByNumber(String number) throws Exception {
        return SopUtil.buildSopBeanByNumber(number);
    }

    public static List<ParametersBean> queryParameters(ParametersBean parametersBean) throws Exception {
    	return SopUtil.queryParameters(parametersBean);
    }

    public static List<Vector<Object>> downloadTechnics(String sopOid) throws Exception {
    	return SopUtil.downloadTechnics(sopOid);
    }
    /**
     * 根据专业类别获取物资类别
     * @param zylb 专业类别
     * @return map
     */
    public static Map<String, String> getMaterialCategoryByWzlb(String zylb) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            List<MPMTooling> wzlbList = SopUtil.getAllMaterialCategory(zylb);
            for (MPMTooling wtPart : wzlbList) {
                map.put(wtPart.getNumber(), wtPart.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static List<SopResourceBean> searchParameters(String zylb, String csxmmc, String gxmc, String wzlb) {
        List<SopResourceBean> resourceBeanList = new ArrayList<SopResourceBean>();
        try {
            Map<String, String> ibaMap = new HashMap<String, String>();
            ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE,zylb);
//            ibaMap.put(SopConstants.SOP_IBA_PARAMETERSNAME,csxmmc);
            ibaMap.put(SopConstants.SOP_IBA_PROCEDUCENAME,gxmc);
            if(wzlb != null && !wzlb.isEmpty()){
                ibaMap.put(SopConstants.SOP_IBA_MATERIALCATEGORY,wzlb);
            }
            List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK, null, null, null, ibaMap, false);
            SopResourceBean sopResourceBean;
            IBAUtility ibaUtility;
            for(WTPart wtPart : wtPartList){
                ibaUtility = new IBAUtility(wtPart);
                String oid = String.valueOf(wtPart.getPersistInfo().getObjectIdentifier().getId());;
                String number = wtPart.getNumber();
                String name = wtPart.getName();
                String specializedType = ibaUtility.getIBAValue(SopConstants.SOP_IBA_SPECIALIZEDTYPE);
                String proceduceName = ibaUtility.getIBAValue(SopConstants.SOP_IBA_PROCEDUCENAME);
                String materialCategory = ibaUtility.getIBAValue(SopConstants.SOP_IBA_MATERIALCATEGORY);
                String canshuzhi = ibaUtility.getIBAValue(SopConstants.SOP_IBA_CANSHUZHI);
                String description = ibaUtility.getIBAValue(SopConstants.SOP_ATTR_REMARK);
                sopResourceBean = new SopResourceBean();
                sopResourceBean.setOid(oid);
                sopResourceBean.setNumber(number);
                sopResourceBean.setName(name);
                sopResourceBean.setSpecializedType(specializedType);
                sopResourceBean.setProcedureName(proceduceName);
                sopResourceBean.setMaterialCategory(materialCategory);
                sopResourceBean.setCanshuzhi(canshuzhi);
                sopResourceBean.setDescription(description);
                resourceBeanList.add(sopResourceBean);
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
        return resourceBeanList;
    }

    /**
     * SOP工艺获取流水号
     *
     * @param type 1:查询；2:更新
     * @param pre
     * @return
     * @throws WTException
     */
    public static String getSopSeqNumber(Integer type, String pre) throws WTException {
        return SopUtil.getSopSeqNumber(type,pre);
    }

    public static boolean checkProcessTaskItem(String taskItemOid, String currentUser) throws WTException {
        boolean flag = false;
        ProcessTaskItem taskItem = (ProcessTaskItem) QueryUtil.getObjectByOid(ProcessTaskItem.class, Long.valueOf(taskItemOid));
        String gongyiyuan = taskItem.getOwner();
        String taskItemName = taskItem.getTaskItemName();
        String taskType = taskItem.getTaskType();
        String taskItemState = taskItem.getTaskItemState();
        if (!"报表类工艺任务".equals(taskType)) {
            if (!"".equals(gongyiyuan) && !"null".equals(gongyiyuan) && gongyiyuan != null &&
                    (taskItemName.endsWith("编制") || taskItemName.endsWith("任务")) && "正在进行".equals(taskItemState)) {
                if (gongyiyuan.equals(currentUser)) {
                    flag = true;
                }
            }
        }
        return flag;
    }
    public static Map<String, List<String>> initSopResource() throws Exception {
        Map<String, List<String>> resourceCache = new HashMap<String, List<String>>();
        List<String> resourceList;
        List<MPMTooling> czgw = SopUtil.getSopResourceByType2(SopConstants.SOP_TYPE_OPERATIONJOB);
        if(czgw != null && czgw.size() > 0){
            resourceList = new ArrayList<String>();
            for(MPMTooling wtPart : czgw){
                resourceList.add(wtPart.getName());
            }
            resourceCache.put(SopConstants.SOP_TYPE_OPERATIONJOB,resourceList);
        }
        List<MPMTooling> dzqy = SopUtil.getSopResourceByType2(SopConstants.SOP_TYPE_CUSTOMAREA);
        if(dzqy != null && dzqy.size() > 0){
            resourceList = new ArrayList<String>();
            for(MPMTooling wtPart : dzqy){
                resourceList.add(wtPart.getName());
            }
            resourceCache.put(SopConstants.SOP_TYPE_CUSTOMAREA,resourceList);
        }
        return resourceCache;
    }

    /**
     * 方法功能:校验当前用户是不是标审者
     *
     * @param technicsNumber 工艺文件编号
     * @param technicVersion 工艺文件版本
     * @param currentUser 当前用户
     * @return boolean
     * @author LB
     * @date 2019/12/25
     */
    public static boolean checkIsBiaoShenZhe(String technicsNumber, String technicVersion, String currentUser) throws Exception {
        boolean flag = false;
        WfAssignedActivity activity;
        WTDocument wtDocument = WTDocumentUtil.getDocumentByNumberAndVersion(technicsNumber, technicVersion);
        WTCollection coll2 = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(wtDocument);
        Iterator it2 = coll2.iterator();
        if (it2.hasNext()) {
            WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it2.next()).getObject();
            activity = SopWorkflowUtil.getWfActivity(ecn, "标审");
        } else {
            activity = SopWorkflowUtil.getWfActivity(wtDocument, "标审");
        }
        if (activity != null && "OPEN_RUNNING".equals(activity.getState().toString())) {
            WTPrincipal activityPrincipal = SopWorkflowUtil.getActivityPrincipal(activity);
            if (activityPrincipal instanceof WTUser) {
                WTUser user = (WTUser) activityPrincipal;
                if (currentUser.equals(user.getName())) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    public static JSONObject getPrintInfo(String technicsNumber, String version) throws Exception {

        WTObject pbo;
        WTDocument wtDocument = WTDocumentUtil.getDocumentByNumberAndVersion(technicsNumber, version);

        GLFilePrintData filePrintData = getObject(wtDocument);
        if(filePrintData!=null){
            JSONObject signInfo = new JSONObject(filePrintData.getPrintData()) ;
            return signInfo;
        }else{
            WTCollection coll2 = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(wtDocument);
            Iterator it2 = coll2.iterator();
            if (it2.hasNext()) {
                WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it2.next()).getObject();
                pbo = ecn;
            } else {
                pbo = wtDocument;
            }
            QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(pbo, null, null);
            if (qrProcs.hasMoreElements()) {
                WfProcess process = (WfProcess) qrProcs.nextElement();
                Hashtable hashtable = PrintHelper.getPrintInfo(pbo, process);
                Set<Map.Entry<String, Hashtable<String, String>>> entrys = hashtable.entrySet();
                for(Map.Entry<String, Hashtable<String, String>> entry:entrys) {
                    Hashtable<String, String> signInfoTable = entry.getValue();
                    JSONObject signInfo = new JSONObject(signInfoTable);
                    return signInfo;
                }
            }
        }

        return null;
    }

    public static String getLastestNumber(String pre, String typeName, String lastestNumber) throws Exception {
        lastestNumber = pre + SopUtil.getSopZYSeqNumber(1, pre);
        MPMTooling tooling = MPMResourceUtil.getMPMToolingByNumber(lastestNumber, typeName);
        if (tooling != null) {
            SopUtil.getSopZYSeqNumber(2, pre);
            lastestNumber = getLastestNumber(pre, typeName, lastestNumber);
        }
        return lastestNumber;
    }

    public static List<WTPart> searchLatestPartList(String containerName, String number, String name, String viewName, Map<String, String> ibaMap,
            boolean isEqual,String type) throws WTException {
        return SopPartUtil.searchLatestPartList(containerName,number,name,viewName,ibaMap,isEqual,type);
    }

    public static List<MPMTooling> getSopResourceListByType(String type) throws Exception {
        return SopUtil.getSopResourceByType2(type);
    }

    public static Map<String, String> getAllCZMCByType(String type) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        List<MPMTooling> list = SopUtil.getSopResourceByType2(type);
        for(MPMTooling tooling : list) {
            map.put(tooling.getNumber(), tooling.getName());
        }
        return map;
    }

    public static Map<String,String> getIBAMapByTooling(MPMTooling tooling) throws Exception {
        IBAUtility utility = new IBAUtility(tooling);
        String zylb = utility.getIBAValue(SopConstants.SOP_IBA_SPECIALIZEDTYPE);
        String gxmc = utility.getIBAValue(SopConstants.SOP_IBA_PROCEDUCENAME);
        String wzlb = utility.getIBAValue(SopConstants.SOP_IBA_MATERIALCATEGORY);
        String csz = utility.getIBAValue(SopConstants.SOP_IBA_CANSHUZHI);
        String desc = utility.getIBAValue(SopConstants.SOP_IBA_REMARK);
        Map<String,String> map = new HashMap<String, String>();
        map.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE,zylb);
        map.put(SopConstants.SOP_IBA_PROCEDUCENAME,gxmc);
        map.put(SopConstants.SOP_IBA_MATERIALCATEGORY,wzlb);
        map.put(SopConstants.SOP_IBA_CANSHUZHI,csz);
        map.put(SopConstants.SOP_IBA_REMARK,desc);
        return map;
    }

}

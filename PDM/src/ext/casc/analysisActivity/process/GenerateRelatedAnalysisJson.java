package ext.casc.analysisActivity.process;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.DynaBean;
import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.constants.Constants;
import com.glaway.mpm.print.util.PrintWorkflowUtil;
import com.glaway.mpm.util.DateUtil;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.htmlcomp.util.VersionComparator;
import ext.casc.analysisActivity.bean.AnalysisObjEntry;
import ext.casc.analysisActivity.bean.AnalysisObjHistoryRecord;
import ext.casc.analysisActivity.helper.AnalysisConstant;
import ext.casc.analysisActivity.helper.AnalysisUtil;
import ext.casc.common.PartCommonHelper;
import ext.casc.process.ProcessConstants;
import ext.casc.process.ProcessTaskItem;
import ext.sast.common.fc.CmPersistenceHelper;
import org.dom4j.DocumentException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wt.change2.WTAnalysisActivity;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.*;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.IconSelector;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.util.WTStandardDateFormat;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WfAssignmentState;
import wt.workflow.work.WorkItem;

import java.beans.PropertyVetoException;
import java.rmi.RemoteException;
import java.util.*;

public class GenerateRelatedAnalysisJson {

    String workItemOid = "";
    String activityName = "";
    WorkItem wi = null;
    WfAssignmentState state = null;
    WfActivity wfAct = null;
    WTAnalysisActivity pbo = null;
    WTUser currentuser = null;
    ReferenceFactory rf = new ReferenceFactory();

    public WTAnalysisActivity getPbo() {
        return pbo;
    }

    public GenerateRelatedAnalysisJson(String oid) {
        try {
            Persistable persistable = rf.getReference(oid).getObject();
            if(persistable != null) {
                if(persistable instanceof WorkItem) {
                    workItemOid = oid;
                    wi = (WorkItem) persistable;
                    state = wi.getStatus();
                    wfAct = (WfActivity) wi.getSource().getObject();
                    activityName = wfAct.getName();
                    Object object = wfAct.getContext().getValue("primaryBusinessObject");
                    if(object != null && object instanceof WTAnalysisActivity) {
                        pbo = (WTAnalysisActivity) object;
                    }
                    currentuser = (WTUser) SessionHelper.manager.getPrincipal();
                } else if(persistable instanceof WTAnalysisActivity) {
                    pbo = (WTAnalysisActivity) persistable;
                    state = WfAssignmentState.POTENTIAL;
                    activityName = AnalysisConstant.ACTIVITY_NAME_CONFIRM;
                }
            }
        } catch(WTRuntimeException e) {
            e.printStackTrace();
        } catch(WTException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法功能: 生成更改影响分析任务表格数据
     *
     * @author cjh
     * @date 2024/3/28
     */
    public JSONObject generateTable(String type) throws JSONException, WTException, RemoteException, PropertyVetoException, DocumentException {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        JSONObject jsonObject = null;
        try {
            if("COMPLETED".equals(state.toString())) {
                jsonObject = generateRecordData(type);
            } else {
                List<AnalysisObjEntry> list = new ArrayList();
                if(pbo != null) {
                    if(AnalysisConstant.ACTIVITY_NAME_EDITANALYSIS.equals(activityName)) {
                        list = AnalysisUtil.getModifyAnalysisObjEntries(pbo.getNumber(), null, type);
                    } else {
                        list = AnalysisUtil.getAnalysisObjEntries(pbo.getNumber(), null, type);
                    }
                    //如果是影响分析主页或者是闭环确认的受影响工艺，增加新增PBOM下的正式工艺
                    if(AnalysisConstant.TYPE_TECHNICS.equals(type) && AnalysisConstant.ACTIVITY_NAME_CONFIRM.equals(activityName)) {
                        List<AnalysisObjEntry> pboms = AnalysisUtil.getAnalysisObjEntries(pbo.getNumber(), null, AnalysisConstant.TYPE_PBOM);
                        for(AnalysisObjEntry pbom : pboms) {
                            if(AnalysisConstant.ANALYSIS_AFFECTED_NEW.equals(pbom.getAffected())) {
                                Persistable persistable = null;
                                try {
                                    persistable = rf.getReference("VR:" + pbom.getVerOid()).getObject();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if(persistable != null && persistable instanceof WTPart) {
                                    WTPart part = (WTPart) persistable;
                                    if(part.getViewName().equals("Design")){
                                        part = WTPartUtil.getLatestPartByNumberAndView(part.getNumber(), "Manufacturing");
                                    }
                                    if(part != null) {
                                        List<WTDocument> documentList = PartCommonHelper.getDescribedByWTDocuments(part);
                                        for(WTDocument document : documentList) {
                                            String pplantype = ext.casc.util.IBAHelper.getIBAStringValue(document, "PPLANTYPE");
                                            if("正式工艺文件".equals(pplantype)) {
                                                String verOid = rf.getReference(document).toString();
                                                verOid = verOid.replaceAll(">", ":");
                                                AnalysisObjEntry old = AnalysisUtil.getAnalysisObjEntry(pbo.getNumber(), verOid, AnalysisConstant.TYPE_TECHNICS);
                                                if(old == null) {
                                                    AnalysisObjEntry entry = new AnalysisObjEntry(pbo.getNumber(), verOid, AnalysisConstant.TYPE_TECHNICS);
                                                    if(Constants.STATE_YIPIZHUN.equals(document.getState().getState().getDisplay(Locale.CHINA))) {
                                                        entry.setDealStatus(AnalysisConstant.DEAL_STATUS_FINISH);
                                                    }
                                                    list.add(entry);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                jsonObject = generateData(list, type);
            }
            JSONArray array = (JSONArray) jsonObject.get("data");
            jsonObject.put("totalCount", array.length());
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return jsonObject;
    }

    /**
     * 方法功能: 生成已完成的更改影响分析任务表格历史数据
     *
     * @author cjh
     * @date 2024/3/28
     */
    public JSONObject generateRecordData(String type) {
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            Map<String,JSONObject> productMap = new HashMap<String,JSONObject>();
            List<AnalysisObjHistoryRecord> records = AnalysisUtil.getAnalysisObjHistoryRecords(pbo.getNumber(), workItemOid, null, type);
            for(AnalysisObjHistoryRecord record : records) {
                String verOid = record.getVerOid();
                if(StrUtil.isNotEmpty(verOid)) {
                    JSONObject jsonObject = new JSONObject();
                    Persistable persistable = rf.getReference("VR:" + verOid).getObject();
                    if(persistable != null) {
                        //人员转换
                        String responser = record.getResponser();
                        if(StrUtil.isNotEmpty(responser)){
                            try {
                                WTUser user = (WTUser) rf.getReference(responser).getObject();
                                responser = user.getFullName() + "(" + user.getName() + ")";
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }

                        if(persistable instanceof WTPart && AnalysisConstant.TYPE_PBOM.equals(type)) {
                            WTPart part = (WTPart) persistable;
                            jsonObject.put("id", verOid);
                            jsonObject.put("icontype", getIcon(part));
                            jsonObject.put("number", part.getNumber() + "@" + rf.getReferenceString(part));
                            jsonObject.put("code", part.getNumber());
                            jsonObject.put("name", part.getName());
                            jsonObject.put("version", part.getIterationDisplayIdentifier().toString());
                            jsonObject.put("state", part.getLifeCycleState().getDisplay(Locale.CHINA));
                            jsonObject.put("modifier", part.getModifier().getFullName());
                            jsonObject.put("modifyTime", WTStandardDateFormat.format(part.getModifyTimestamp(), "yyyy-MM-dd"));
                            jsonObject.put("affected", record.getAffected());
                            jsonObject.put("affectedGyy", record.getAffectedGyy());
                            jsonObject.put("responser", responser);
                            jsonObject.put("requirement", record.getRequirement());
                            jsonObject.put("completeTime", record.getCompleteTime());
                            jsonObject.put("dealStatus", record.getDealStatus());
                        } else if(persistable instanceof WTDocument && AnalysisConstant.TYPE_TECHNICS.equals(type)) {
                            WTDocument doc = (WTDocument) persistable;
                            jsonObject.put("id", verOid);
                            jsonObject.put("icontype", getIcon(doc));
                            jsonObject.put("number", doc.getNumber() + "@" + rf.getReferenceString(doc));
                            jsonObject.put("code", doc.getNumber());
                            jsonObject.put("name", doc.getName());
                            jsonObject.put("version", doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());
                            jsonObject.put("state", doc.getLifeCycleState().getDisplay(Locale.CHINA));
                            jsonObject.put("modifier", doc.getModifier().getFullName());
                            jsonObject.put("modifyTime", WTStandardDateFormat.format(doc.getModifyTimestamp(), "yyyy-MM-dd"));
                            jsonObject.put("affected", record.getAffected());
                            jsonObject.put("affectedGyy", record.getAffectedGyy());
                            AnalysisObjEntry entry = new AnalysisObjEntry();
                            entry.setRemarkGyy(record.getRemarkGyy());
                            jsonObject.put("remarkGyy", getDataValue("remarkGyy", entry, doc, verOid, type));
                            jsonObject.put("responser", responser);
                            jsonObject.put("requirement", record.getRequirement());
                            jsonObject.put("completeTime", record.getCompleteTime());
                            jsonObject.put("relatedOrder", record.getRelatedOrder());
                            jsonObject.put("dealStatus", record.getDealStatus());
                            jsonObject.put("dept", IBAHelper.getIBAValue(doc, "DEPT"));
                        } else if(persistable instanceof WTPart && AnalysisConstant.PRODUCT.equals(type)) {
                            WTPart part = (WTPart) persistable;
                            if(productMap.containsKey(verOid)) {
                                jsonObject = productMap.get(verOid);
                            } else {
                                jsonObject.put("id", verOid);
                                jsonObject.put("icontype", getIcon(part));
                                jsonObject.put("number", part.getNumber() + "@" + rf.getReferenceString(part));
                                jsonObject.put("code", part.getNumber());
                                jsonObject.put("name", part.getName());
                                jsonObject.put("requirement", record.getRequirement());
                                jsonObject.put("completeTime", record.getCompleteTime());
                            }
                            if(AnalysisConstant.TYPE_ZAIZHIPIN.equals(record.getDataType())){
                                jsonObject.put("zaizhipin", record.getProduct());
                                jsonObject.put("zcount", record.getCount());
                                jsonObject.put("zrepaircount", record.getRepaircount());
                                jsonObject.put("zrepairtec", getDataValue("zrepairtec", null, part, verOid, type));
                                jsonObject.put("responser", responser);
                                jsonObject.put("zdealstatus",record.getDealStatus());
                            } else if(AnalysisConstant.TYPE_YIZHIPIN.equals(record.getDataType())) {
                                jsonObject.put("yizhipin", record.getProduct());
                                jsonObject.put("ycount", record.getCount());
                                jsonObject.put("yrepaircount", record.getRepaircount());
                                jsonObject.put("yrepairtec", getDataValue("yrepairtec", null, part, verOid, type));
                                jsonObject.put("jidiaoyuan", responser);
                                jsonObject.put("ydealstatus", record.getDealStatus());
                            }

                            if(!productMap.containsKey(verOid)) {
                                productMap.put(verOid, jsonObject);
                                continue;
                            }
                            if(AnalysisConstant.ACTIVITY_NAME_ANALYSIS.equals(activityName)) {
                                //TODO 制品发送失败在这里添加重发按钮
                            }
                        }
                        array.put(jsonObject);
                    }
                }
            }
            result.put("data", array);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 方法功能: 生成未完成的更改影响分析任务表格数据
     *
     * @author cjh
     * @date 2024/3/28
     */
    public JSONObject generateData(List<AnalysisObjEntry> entries, String type) {
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();

        try {
            Map<String,JSONObject> productMap = new HashMap<String,JSONObject>();
            for(AnalysisObjEntry entry : entries) {
                Persistable obj = null;
                try {
                    obj = rf.getReference("VR:" + entry.getVerOid()).getObject();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                if(obj != null) {
                    JSONObject jsonObject = new JSONObject();
                    String verOid = entry.getVerOid();
                    if(obj instanceof WTPart && AnalysisConstant.TYPE_PBOM.equals(type)) {
                        WTPart part = (WTPart) obj;
                        jsonObject.put("id", verOid);
                        jsonObject.put("icontype", getIcon(part));
                        jsonObject.put("number", part.getNumber() + "@" + rf.getReferenceString(part));
                        jsonObject.put("code", part.getNumber());
                        jsonObject.put("name", part.getName());
                        jsonObject.put("version", part.getIterationDisplayIdentifier().toString());
                        jsonObject.put("state", part.getLifeCycleState().getDisplay(Locale.CHINA));
                        jsonObject.put("modifier", part.getModifier().getFullName());
                        jsonObject.put("modifyTime", WTStandardDateFormat.format(part.getModifyTimestamp(), "yyyy-MM-dd"));
                        jsonObject.put("affected", getDataValue("affected", entry, part, verOid, type));
                        jsonObject.put("affectedGyy", getDataValue("affectedGyy", entry, part, verOid, type));
                        jsonObject.put("responser", getDataValue("responser", entry, part, verOid, type));
                        jsonObject.put("requirement", getDataValue("requirement", entry, part, verOid, type));
                        jsonObject.put("completeTime", getDataValue("completeTime", entry, part, verOid, type));
                        jsonObject.put("dealStatus", getDataValue("dealStatus", entry, part, verOid, type));
                    } else if(obj instanceof WTDocument && AnalysisConstant.TYPE_TECHNICS.equals(type)) {
                        WTDocument doc = (WTDocument) obj;
                        jsonObject.put("id", verOid);
                        jsonObject.put("icontype", getIcon(doc));
                        jsonObject.put("number", doc.getNumber() + "@" + rf.getReferenceString(doc));
                        jsonObject.put("code", doc.getNumber());
                        jsonObject.put("name", doc.getName());
                        jsonObject.put("version", doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());
                        jsonObject.put("state", doc.getLifeCycleState().getDisplay(Locale.CHINA));
                        jsonObject.put("modifier", doc.getModifier().getFullName());
                        jsonObject.put("modifyTime", WTStandardDateFormat.format(doc.getModifyTimestamp(), "yyyy-MM-dd"));
                        jsonObject.put("dept", IBAHelper.getIBAValue(doc, "DEPT"));
                        jsonObject.put("affected", getDataValue("affected", entry, doc, verOid, type));
                        jsonObject.put("affectedGyy", getDataValue("affectedGyy", entry, doc, verOid, type));
                        jsonObject.put("remarkGyy", getDataValue("remarkGyy", entry, doc, verOid, type));
                        jsonObject.put("responser", getDataValue("responser", entry, doc, verOid, type));
                        jsonObject.put("requirement", getDataValue("requirement", entry, doc, verOid, type));
                        jsonObject.put("completeTime", getDataValue("completeTime", entry, doc, verOid, type));
                        jsonObject.put("relatedOrder", getDataValue("relatedOrder", entry, doc, verOid, type));
                        jsonObject.put("dealStatus", getDataValue("dealStatus", entry, doc, verOid, type));
                    } else if(obj instanceof WTPart && AnalysisConstant.PRODUCT.equals(type) && AnalysisConstant.ACTIVITY_NAME_ANALYSIS.equals(activityName)) {
                        WTPart part = (WTPart) obj;
                        if(productMap.containsKey(verOid)) {
                            jsonObject = productMap.get(verOid);
                        }else {
                            jsonObject.put("id", verOid);
                            jsonObject.put("icontype", getIcon(part));
                            jsonObject.put("number", part.getNumber() + "@" + rf.getReferenceString(part));
                            jsonObject.put("name", part.getName());
                            jsonObject.put("code", part.getNumber());
                            jsonObject.put("requirement", getDataValue("requirement", entry, part, verOid, type));
                            jsonObject.put("completeTime", getDataValue("completeTime", entry, part, verOid, type));
                        }
                        if(AnalysisConstant.TYPE_ZAIZHIPIN.equals(entry.getDataType())){
                            jsonObject.put("zaizhipin", getDataValue("zaizhipin", entry, part, verOid, type));
                            jsonObject.put("responser", getDataValue("responser", entry, part, verOid, type));
                        } else if(AnalysisConstant.TYPE_YIZHIPIN.equals(entry.getDataType())) {
                            jsonObject.put("yizhipin", getDataValue("yizhipin", entry, part, verOid, type));
                            jsonObject.put("jidiaoyuan", getDataValue("jidiaoyuan", entry, part, verOid, type));
                        }
                        if(!productMap.containsKey(verOid)){
                            productMap.put(verOid, jsonObject);
                            continue;
                        }
                    } else if(obj instanceof WTPart && AnalysisConstant.PRODUCT.equals(type) && AnalysisConstant.ACTIVITY_NAME_CONFIRM.equals(activityName)) {
                        WTPart part = (WTPart) obj;
                        if(productMap.containsKey(verOid)) {
                            jsonObject = productMap.get(verOid);
                        }else {
                            jsonObject.put("id", verOid);
                            jsonObject.put("icontype", getWorkflowPictureUrl(verOid) + "&nbsp;&nbsp;" + getIcon(part));
                            jsonObject.put("number", part.getNumber() + "@" + rf.getReferenceString(part));
                            jsonObject.put("code", part.getNumber());
                            jsonObject.put("name", part.getName());
                            jsonObject.put("requirement", getDataValue("requirement", entry, part, verOid, type));
                            jsonObject.put("completeTime", getDataValue("completeTime", entry, part, verOid, type));
                        }
                        if(AnalysisConstant.TYPE_ZAIZHIPIN.equals(entry.getDataType())){
                            jsonObject.put("zaizhipin", getDataValue("zaizhipin", entry, part, verOid, type));
                            jsonObject.put("zcount", getDataValue("zcount", entry, part, verOid, type));
                            jsonObject.put("zrepaircount", getDataValue("zrepaircount", entry, part, verOid, type));
                            jsonObject.put("zjwxcount", getDataValue("zjwxcount", entry, part, verOid, type));
                            jsonObject.put("zjwxfzr", getDataValue("zjwxfzr", entry, part, verOid, type));
                            jsonObject.put("zrepairtec", getDataValue("zrepairtec", entry, part, verOid, type));
                            jsonObject.put("responser", getDataValue("responser", entry, part, verOid, type));
                            jsonObject.put("zdealstatus", getDataValue("zdealstatus", entry, part, verOid, type));
                        } else if(AnalysisConstant.TYPE_YIZHIPIN.equals(entry.getDataType())) {
                            jsonObject.put("yizhipin", getDataValue("yizhipin", entry, part, verOid, type));
                            jsonObject.put("ycount", getDataValue("ycount", entry, part, verOid, type));
                            jsonObject.put("yrepaircount", getDataValue("yrepaircount", entry, part, verOid, type));
                            jsonObject.put("yrepairtec", getDataValue("yrepairtec", entry, part, verOid, type));
                            jsonObject.put("jidiaoyuan", getDataValue("jidiaoyuan", entry, part, verOid, type));
                            jsonObject.put("ydealstatus", getDataValue("ydealstatus", entry, part, verOid, type));
                        }
                        if(!productMap.containsKey(verOid)){
                            productMap.put(verOid, jsonObject);
                            continue;
                        }
                    }
                    array.put(jsonObject);
                }
            }
            result.put("data", array);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //20250517 获取流程图页面地址
    private String getWorkflowPictureUrl(String verOid)
    {
        String str = "/Windchill/netmarkets/jsp/ext/casc/distribute/distribute_workflow.jsp?changeNumber="
                + pbo.getNumber() + "&partId=" + verOid;
        str = "<a href='" + str + "' target=‘_blank’ tip='查看流程'>" +
                "<img src=\"/Windchill/netmarkets/jsp/ext/casc/distribute/resource/workflow.png\" width=\"20px\" height=\"20px\" >" +
                "</a>";

        return str;
    }

    /**
     * 方法功能: 生成表格字段
     *
     * @author cjh
     * @date 2024/3/28
     */
    @SuppressWarnings("unused")
    public String getDataValue(String columnName, AnalysisObjEntry entry, WTObject obj, String veroid, String type) throws Exception {
        String key = veroid + "_" + columnName + "_" + type;
        if(columnName.equals("affected")) {
            String oldValue = entry.getAffected();
            StringBuffer sb = new StringBuffer();
            List<String> list = new ArrayList<String>();
            list.add(AnalysisConstant.ANALYSIS_AFFECTED_HAS);
            list.add(AnalysisConstant.ANALYSIS_AFFECTED_NO);
            if(AnalysisConstant.TYPE_PBOM.equals(type)) {
                list.add(AnalysisConstant.ANALYSIS_AFFECTED_NEW);
                if(StrUtil.isEmpty(oldValue) && obj instanceof WTPart && ((WTPart) obj).getViewName().equals("Design")) {
                    oldValue = AnalysisConstant.ANALYSIS_AFFECTED_NEW;
                }
            }
            if(AnalysisConstant.ACTIVITY_NAME_ANALYSIS.equals(activityName) || AnalysisConstant.ACTIVITY_NAME_EDITANALYSIS.equals(activityName)) {
                sb.append("<select name=\"" + key + "\" " + "id=\"" + key + "\" onchange=\"selectAffected(this,'" + type + "')\">");
            } else {
                sb.append("<select disabled=\"disabled\" name=\"" + key + "\" " + "id=\"" + key + "\" >");
            }

            for(int i = 0; i < list.size(); i++) {
                String str = list.get(i);
                if(str.equals(oldValue)) {
                    sb.append("<option selected=\"selected\" value=\"" + str + "\">");
                } else {
                    sb.append("<option value=\"" + str + "\">");
                }
                sb.append(str);
                sb.append("</option>");
            }
            sb.append("</select>");
            String value = sb.toString();
            return value;
        } else if(columnName.equals("affectedGyy")) {
            StringBuffer sb = new StringBuffer();
            List<String> list = new ArrayList<String>();
            String oldValue = entry.getAffectedGyy();
            if(AnalysisConstant.ACTIVITY_NAME_CONFIRM.equals(activityName)){
                list.add("");
            }else {
                if(StrUtil.isEmpty(oldValue)) {
                    oldValue = entry.getAffected();
                }
            }
            list.add(AnalysisConstant.ANALYSIS_AFFECTED_HAS);
            list.add(AnalysisConstant.ANALYSIS_AFFECTED_NO);
            if(AnalysisConstant.TYPE_PBOM.equals(type)) {
                list.add(AnalysisConstant.ANALYSIS_AFFECTED_NEW);
            }
            if(AnalysisConstant.ACTIVITY_NAME_DEAL.equals(activityName)) {
                sb.append("<select name=\"" + key + "\" " + "id=\"" + key + "\" onchange=\"selectAffected(this,'" + type + "')\">");
            } else {
                sb.append("<select disabled=\"disabled\" name=\"" + key + "\" " + "id=\"" + key + "\" >");
            }

            for(int i = 0; i < list.size(); i++) {
                String str = list.get(i);
                if(str.equals(oldValue)) {
                    sb.append("<option selected=\"selected\" value=\"" + str + "\">");
                } else {
                    sb.append("<option value=\"" + str + "\">");
                }
                sb.append(str);
                sb.append("</option>");
            }
            sb.append("</select>");
            String value = sb.toString();
            return value;
        } else if(columnName.equals("responser")) {
            String userName = "";
            String responser = entry.getResponser();
            if(StrUtil.isNotEmpty(responser)) {
                try {
                    Persistable object = rf.getReference(responser).getObject();
                    if(object != null && object instanceof WTUser) {
                        WTUser user = (WTUser) object;
                        userName = user.getFullName() + "(" + user.getName() + ")";
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    WTUser user = null;
                    if(obj instanceof WTPart && AnalysisConstant.TYPE_PBOM.equals(type)) {
                        WTPart part = (WTPart) obj;
                        user = (WTUser) part.getModifier().getObject();
                    } else if(obj instanceof WTDocument) {
                        WTDocument document = (WTDocument) obj;
                        user = (WTUser) document.getModifier().getObject();
                    }
                    if(user != null) {
                        responser = rf.getReferenceString(user);
                        userName = user.getFullName() + "(" + user.getName() + ")";
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            String value = "<input type=\"text\" readonly name=\"" + key + "\"  id=\"" + key + "\" value=\"" + userName + "\"/><input type=\"button\" value=\"选择责任人\" onclick=\"javascript:selectUser('" + veroid + "','" + type + "','选择责任人');\"  id=\"" + veroid + "_selecPer_" + type + "\"/><input type=\"button\" value=\"清空\" onclick=\"javascript:clearUser('" + veroid + "','" + type + "','1');\"  id=\"" + veroid + "_selecClearPer_" + type + "\"/><input type=\"hidden\"   name=\"" + veroid + "_responser_value_" + type + "\" id=\"" + veroid + "_responser_value_" + type + "\" value=\"" + responser + "\">";
            if(AnalysisConstant.ACTIVITY_NAME_CONFIRM.equals(activityName)) {
                value = "<input type=\"text\" readonly name=\"" + key + "\" id=\"" + key + "\" value=\"" + userName + "\" />";
            }
            return value;
        } else if(columnName.equals("requirement")) {
            String requirement = entry.getRequirement();
            if(AnalysisConstant.ACTIVITY_NAME_DEAL.equals(activityName) || AnalysisConstant.ACTIVITY_NAME_CONFIRM.equals(activityName)) {
                return "<input readonly value=\"" + requirement + "\" type=\"text\" size='40' name=\"" + key + "\" id=\"" + key + "\"/>";
            }else {
                return "<input value=\"" + requirement + "\" type=\"text\" size='40' onchange=\"changeRequirement(this,'" + type + "')\" name=\"" + key + "\" id=\"" + key + "\"/>";
            }
        } else if(columnName.equals("completeTime")) {
            String completeTime = entry.getCompleteTime();
            if(AnalysisConstant.ACTIVITY_NAME_DEAL.equals(activityName) || AnalysisConstant.ACTIVITY_NAME_CONFIRM.equals(activityName)) {
                return "<input type=\"text\" readonly id=\"" + key + "\" name=\"" + key + "\" value=\"" + completeTime + "\"  size=\"10\" maxlength=\"10\"/>";
            }
            if(StrUtil.isEmpty(completeTime)) {
                completeTime = DateUtil.afterNDay("yyyy/MM/dd", 5);
            }
            String value = "<input type=\"text\" readonly id=\"" + key + "\" name=\"" + key + "\" onchange=\"verifyDate(this,'" + type + "')\" " +
                    "value=\"" + completeTime + "\"  size=\"10\" maxlength=\"10\"/>\n\t\t\t<A HREF=\"javascript:void(0)\"  " +
                    "onmousedown=\"suppressCalendarBlur(event);\"" +
                    "onClick=\"initCal('\\u65e5\\u5386', '<LINK REL=stylesheet HREF=/Windchill/netmarkets/css/siteStyles.css TYPE=text/css>', " +
                    "'/Windchill/templates/cadx/common/trlUtils.js', 3, 2, 1, '', '', false, 100, 1932); " +
                    "setDateField(document.getElementsByName('" + key + "')[0], 'yyyy/MM/dd', '\\u4e00\\u6708#\\u4e8c\\u6708#\\u4e09\\u6708#\\u56db\\u6708#\\u4e94\\u6708#\\u516d\\u6708#\\u4e03\\u6708#\\u516b\\u6708#\\u4e5d\\u6708#\\u5341\\u6708#\\u5341\\u4e00\\u6708#\\u5341\\u4e8c\\u6708#', '#', '\\u661f\\u671f\\u65e5#\\u661f\\u671f\\u4e00#\\u661f\\u671f\\u4e8c#\\u661f\\u671f\\u4e09#\\u661f\\u671f\\u56db#\\u661f\\u671f\\u4e94#\\u661f\\u671f\\u516d#', '0'); " +
                    "newCalendarWindow(event, '/Windchill/netmarkets/jsp/util/calPopup.jsp', 'height=220,width=240')\">\n\t\t\t" +
                    "<IMG name=\"calImg\" SRC=\"/Windchill/netmarkets/images/calendar.gif\" WIDTH=18 HEIGTH=16 BORDER=0></A>";

            return value;
        } else if(columnName.equals("dealStatus")) {
            String dealStatus = entry.getDealStatus();
            if(StrUtil.isEmpty(dealStatus)) {
                dealStatus = AnalysisConstant.DEAL_STATUS_WORKING;
            }
            return dealStatus;
        } else if(columnName.equals("relatedOrder")) {
            String relatedOrder = entry.getRelatedOrder();
            return relatedOrder;
        } else if(columnName.equals("relatedOrderState")) {
            String relatedOrder = entry.getRelatedOrder();
            if(StrUtil.isNotEmpty(relatedOrder)){
                WTChangeOrder2 order2 = PrintWorkflowUtil.getWTChangeOrder2ByNumber(relatedOrder);
                if(order2 != null) {
                    return order2.getState().getState().getDisplay(Locale.CHINA);
                }
            }
            return "";
        } else if(columnName.equals("zaizhipin") || columnName.equals("yizhipin")) {
            StringBuffer sb = new StringBuffer();
            List<String> list = new ArrayList<String>();
            list.add(AnalysisConstant.ANALYSIS_ZHIPIN_FANXIU);
            list.add(AnalysisConstant.ANALYSIS_ZHIPIN_BAOFEI);
            list.add(AnalysisConstant.ANALYSIS_AFFECTED_NO);

            String oldValue = entry.getProduct();
            if(AnalysisConstant.ACTIVITY_NAME_CONFIRM.equals(activityName)) {
                if(StrUtil.isEmpty(oldValue)) {
                    oldValue = "_";
                } else if(AnalysisConstant.ANALYSIS_ZHIPIN_FANXIU.equals(oldValue)) {
                    oldValue = AnalysisConstant.ANALYSIS_ZHIPIN_FANXIUFANGONG;
                }
                sb.append("<a id='" + key + "' href='javascript:void(0)' onclick=\"javascript:showAllDealRecord('" + veroid + "','" + pbo.getNumber() + "','" + columnName + "','" + ((WTPart) obj).getNumber() + "');\">" + oldValue + "</a>");
                return sb.toString();
            } else {
                sb.append("<select name=\"" + key + "\" " + "id=\"" + key + "\" onchange=\"selectZhiPin(this,'" + columnName + "')\">");
            }
            for(int i = 0; i < list.size(); i++) {
                String str = list.get(i);
                String displayStr = str;
                if(AnalysisConstant.ANALYSIS_ZHIPIN_FANXIU.equals(displayStr)) {
                    displayStr = AnalysisConstant.ANALYSIS_ZHIPIN_FANXIUFANGONG;
                }
                if(str.equals(entry.getProduct())) {
                    sb.append("<option selected=\"selected\" value=\"" + str + "\">");
                } else {
                    sb.append("<option value=\"" + str + "\">");
                }
                sb.append(displayStr);
                sb.append("</option>");
            }
            sb.append("</select>");
            String value = sb.toString();
            return value;
        } else if(columnName.equals("jidiaoyuan")) {
            String jidiaoyuan = entry.getResponser();
            String userName = "";
            if(StrUtil.isNotEmpty(jidiaoyuan)) {
                try {
                    Persistable object = rf.getReference(jidiaoyuan).getObject();
                    if(object != null && object instanceof WTUser) {
                        WTUser user = (WTUser) object;
                        userName = user.getFullName() + "(" + user.getName() + ")";
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            if(AnalysisConstant.ACTIVITY_NAME_CONFIRM.equals(activityName)) {
                return "<input type=\"text\" readonly name=\"" + key + "\" id=\"" + key + "\" value=\"" + userName + "\" />";
            }else {
                return "<input type=\"text\" readonly name=\"" + key + "\"  id=\"" + key + "\" value=\"" + userName + "\"/><input type=\"button\" value=\"选择计调员\" onclick=\"javascript:selectUser('" + veroid + "','" + type + "','选择计调员');\"  id=\"" + veroid + "_selecJiDiao_" + type + "\"/><input type=\"button\" value=\"清空\" onclick=\"javascript:clearUser('" + veroid + "','" + type + "','2');\"  id=\"" + veroid + "_clearJiDiao_" + type + "\"/><input type=\"hidden\"   name=\"" + veroid + "_jidiaoyuan_value_" + type + "\" id=\"" + veroid + "_jidiaoyuan_value_" + type + "\" value=\"" + jidiaoyuan + "\">";
            }
        } else if(columnName.equals("zrepairtec") || columnName.equals("yrepairtec")) {
            String docNum = "";
            if(pbo != null && obj instanceof WTPart) {
                int[] index = {0};
                QuerySpec qs = new QuerySpec(ProcessTaskItem.class);
                qs.setAdvancedQueryEnabled(true);
                ClassAttribute caId = new ClassAttribute(ProcessTaskItem.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
                qs.appendWhere(new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.NUMBER, SearchCondition.EQUAL, ((WTPart) obj).getNumber()), index);
                qs.appendAnd();
                String yijv = "";
                if(columnName.equals("zrepairtec")) {
                    yijv = AnalysisConstant.RENWUYIJV_ZAIZHIPIN;
                } else if(columnName.equals("yrepairtec")) {
                    yijv = AnalysisConstant.RENWUYIJV_YIZHIPIN;
                }
                qs.appendWhere(new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.RENWUYIJU, SearchCondition.EQUAL, yijv), index);
                qs.appendAnd();
                qs.appendWhere(new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.TASK_ITEM_NAME, SearchCondition.EQUAL, ProcessConstants.TASK_NAME_LINSHIGONGYIRENWU), index);
                qs.appendAnd();
                qs.appendOpenParen();
                SubSelectExpression se = IBAHelper.getStringIBAQuery("ANALYSISNUMBER", pbo.getNumber());
                qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, se), index);
                qs.appendCloseParen();
                qs.appendOrderBy(new OrderBy(new ClassAttribute(ProcessTaskItem.class, ProcessTaskItem.CREATE_TIMESTAMP), true), index);
                QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
                if(qr.hasMoreElements()) {
                    ProcessTaskItem taskItem = (ProcessTaskItem) qr.nextElement();
                    String processdocNum = IBAHelper.getIBAValue(taskItem, "PROCESSDOCNUM");
                    if(StrUtil.isNotEmpty(processdocNum) && !"null".equals(processdocNum)) {
                        WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(processdocNum);
                        if(document != null) {
                            docNum = document.getNumber() + "@" + rf.getReferenceString(document);
                        }
                    }
                }
            }
            return docNum;
        } else if(columnName.equals("zdealstatus") || columnName.equals("ydealstatus")) {
            if(pbo != null && obj instanceof WTPart) {
                String zp = entry.getDealStatus();
                if(AnalysisConstant.DEAL_STATUS_FINISH.equals(zp)){
                    return zp;
                }
                boolean dealResult = AnalysisUtil.checkProductDealResult(entry, false);
                if(dealResult){
                    entry.setDealStatus(AnalysisConstant.DEAL_STATUS_FINISH);
                    CmPersistenceHelper.manager.update(entry);
                    return AnalysisConstant.DEAL_STATUS_FINISH;
                }
            }
            return AnalysisConstant.DEAL_STATUS_WORKING;
        } else if(columnName.equals("zcount") || columnName.equals("ycount")) {
            if(StrUtil.isEmpty(entry.getReceiveStatus())){
                return "";
            }
            return entry.getCount().toString();
        } else if(columnName.equals("zrepaircount") || columnName.equals("yrepaircount")) {
            if(StrUtil.isEmpty(entry.getReceiveStatus())){
                return "";
            }
            return entry.getRepaircount().toString();
        } else if(columnName.equals("zjwxcount")) {
            AnalysisObjEntry yizhipin = AnalysisUtil.getAnalysisObjEntry(entry.getAnalysisNumber(), entry.getVerOid(), AnalysisConstant.TYPE_YIZHIPIN);
            if(yizhipin != null && StrUtil.isEmpty(yizhipin.getReceiveStatus())){
                return "";
            }
            return entry.getZjwxcount().toString();
        } else if(columnName.equals("remarkGyy")) {
            String remarkGyy = entry.getRemarkGyy();
            String isEditable = "false";
            if(AnalysisConstant.ACTIVITY_NAME_DEAL.equals(activityName) && !"COMPLETED".equals(state.toString())) {
                isEditable = "true";
            }
            return "<input readonly onfocus=showRemark('" + remarkGyy + "','" + veroid + "','" + isEditable + "') value=\"" + remarkGyy + "\" type=\"text\" size='10' name=\"" + key + "\" id=\"" + key + "\"/>";
        } else {
            try {
                DynaBean bean = DynaBean.create(entry);
                String string = bean.get(columnName).toString();
                return string;
            } catch(Exception e) {
                return "";
            }
        }
    }

    /**
     * 方法功能:图标
     *
     * @author cjh
     * @date 2024/3/28
     */
    private String getIcon(WTObject obj) throws WTException {
        String imgURL = null;
        try {
            IconDelegate delegate = IconDelegateFactory.getInstance()
                    .getIconDelegate(obj);
            IconSelector selector = delegate.getStandardIconSelector();
            while(!selector.isResourceKey()) {
                delegate = delegate.resolveSelector(selector);
                selector = delegate.getStandardIconSelector();
            }
            imgURL = selector.getIconKey();
        } catch(Exception e) {
            throw new WTException(e);
        }
        return "<img src=\"" + imgURL + "\">";
    }

    /**
     * 方法功能:保存意见
     * data 需要保存的意见集合
     *
     * @author cjh
     * @date 2024/3/19
     */
    public void process(String data) {
        if(StrUtil.isNotEmpty(data)) {
            String[] datas = data.split("@!@");
            for(String str : datas) {
                if(StrUtil.isNotEmpty(str)) {
                    Map<String,String> map = new HashMap();
                    String[] ss = str.split("&");
                    for(String s : ss) {
                        if(!"".equals(s) && s.contains("=")) {
                            String[] ss2 = s.split("=");
                            String key = ss2[0];
                            if(s.endsWith("=")) {
                                map.put(key, "");
                            } else {
                                map.put(key, ss2[1]);
                            }
                        }
                    }
                    String id = map.get("id");
                    String type = map.get("type");
                    AnalysisObjEntry entry = AnalysisUtil.getAnalysisObjEntry(pbo.getNumber(), id, type);
                    if(entry != null) {
                        String responser = map.get("responser");
                        String requirement = map.get("requirement");
                        String completeTime = map.get("completeTime");
                        //制品
                        if(type.contains(AnalysisConstant.PRODUCT)) {
                            String product = map.get("product");
                            entry.setProduct(product);
                            entry.setResponser(responser);
                            entry.setRequirement(requirement);
                            entry.setCompleteTime(completeTime);
                        } else {
                            //PBOM或工艺
                            String affected = map.get("affected");
                            entry.setAffected(affected);
                            entry.setResponser(responser);
                            entry.setRequirement(requirement);
                            entry.setCompleteTime(completeTime);
                        }
                        try {
                            CmPersistenceHelper.manager.update(entry);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 方法功能: 完成对受影响对象的处理
     *
     * @author cjh
     * @date 2024/3/28
     */
    public static void finishDeal(String analysisNumber, String ids, String type) {
        try {
            String[] idColl = ids.split("~");
            for(String id : idColl) {
                if(StrUtil.isNotEmpty(id) && id.contains("@")) {
                    String[] datas = id.split("@");
                    String verId = datas[0];
                    AnalysisObjEntry entry = AnalysisUtil.getAnalysisObjEntry(analysisNumber, verId, type);
                    if(entry != null) {
                        entry.setAffectedGyy(datas[1]);
                        entry.setDealStatus(AnalysisConstant.DEAL_STATUS_FINISH);
                        CmPersistenceHelper.manager.update(entry);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 方法功能: 生成执行更改任务表格的数据
     *
     * @author cjh
     * @date 2024/3/28
     */
    public JSONObject generateDealTable(String type) throws JSONException, WTException {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        JSONObject jsonObject = null;
        try {
            if("COMPLETED".equals(state.toString())) {
                jsonObject = generateRecordData(type);
            } else {
                String userId = rf.getReferenceString(currentuser);
                Map<String, String> map = new HashMap<String, String>();
                map.put(AnalysisObjEntry.ANALYSISNUMBER, pbo.getNumber());
                map.put(AnalysisObjEntry.DATATYPE, type);
                map.put(AnalysisObjEntry.RESPONSER, userId);
                List<AnalysisObjEntry> entries = AnalysisUtil.getAnalysisObjEntries(map);
                jsonObject = generateDealData(entries, type);
            }
            JSONArray array = (JSONArray) jsonObject.get("data");
            jsonObject.put("totalCount", array.length());
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return jsonObject;
    }

    /**
     * 方法功能: 生成正在进行的执行更改表格数据
     *
     * @author cjh
     * @date 2024/3/28
     */
    public JSONObject generateDealData(List<AnalysisObjEntry> entries, String type) {
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();

        try {
            for(AnalysisObjEntry entry : entries) {
                Persistable obj = null;
                try {
                    obj = rf.getReference("VR:" + entry.getVerOid()).getObject();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                if(obj != null) {
                    JSONObject jsonObject = new JSONObject();
                    String verOid = entry.getVerOid();
                    if(obj instanceof WTPart && AnalysisConstant.TYPE_PBOM.equals(type)) {
                        WTPart part = (WTPart) obj;
                        jsonObject.put("id", verOid);
                        jsonObject.put("icontype", getIcon(part));
                        jsonObject.put("number", part.getNumber() + "@" + rf.getReferenceString(part));
                        jsonObject.put("code", part.getNumber());
                        jsonObject.put("name", part.getName());
                        jsonObject.put("version", part.getIterationDisplayIdentifier().toString());
                        jsonObject.put("state", part.getLifeCycleState().getDisplay(Locale.CHINA));
                        jsonObject.put("modifier", part.getModifier().getFullName());
                        jsonObject.put("modifyTime", WTStandardDateFormat.format(part.getModifyTimestamp(), "yyyy-MM-dd"));
                        jsonObject.put("affected", getDataValue("affected", entry, part, verOid, type));
                        jsonObject.put("affectedGyy", getDataValue("affectedGyy", entry, part, verOid, type));
                        jsonObject.put("responser", getDataValue("responser", entry, part, verOid, type));
                        jsonObject.put("requirement", getDataValue("requirement", entry, part, verOid, type));
                        jsonObject.put("completeTime", getDataValue("completeTime", entry, part, verOid, type));
                        jsonObject.put("dealStatus", getDataValue("dealStatus", entry, part, verOid, type));
                    } else if(obj instanceof WTDocument && AnalysisConstant.TYPE_TECHNICS.equals(type)) {
                        WTDocument doc = (WTDocument) obj;
                        jsonObject.put("id", verOid);
                        jsonObject.put("icontype", getIcon(doc));
                        jsonObject.put("number", doc.getNumber() + "@" + rf.getReferenceString(doc));
                        jsonObject.put("code", doc.getNumber());
                        jsonObject.put("name", doc.getName());
                        jsonObject.put("version", doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());
                        jsonObject.put("state", doc.getLifeCycleState().getDisplay(Locale.CHINA));
                        jsonObject.put("modifier", doc.getModifier().getFullName());
                        jsonObject.put("modifyTime", WTStandardDateFormat.format(doc.getModifyTimestamp(), "yyyy-MM-dd"));
                        jsonObject.put("dept", IBAHelper.getIBAValue(doc, "DEPT"));
                        jsonObject.put("affected", getDataValue("affected", entry, doc, verOid, type));
                        jsonObject.put("affectedGyy", getDataValue("affectedGyy", entry, doc, verOid, type));
                        jsonObject.put("remarkGyy", getDataValue("remarkGyy", entry, doc, verOid, type));
                        jsonObject.put("responser", getDataValue("responser", entry, doc, verOid, type));
                        jsonObject.put("requirement", getDataValue("requirement", entry, doc, verOid, type));
                        jsonObject.put("completeTime", getDataValue("completeTime", entry, doc, verOid, type));
                        jsonObject.put("relatedOrder", getDataValue("relatedOrder", entry, doc, verOid, type));
                        jsonObject.put("relatedOrderState", getDataValue("relatedOrderState", entry, doc, verOid, type));
                        jsonObject.put("dealStatus", getDataValue("dealStatus", entry, doc, verOid, type));
                        String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
                        if(docType.indexOf("casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN") > -1) {
                            //工艺
                            jsonObject.put("pplantype", IBAHelper.getIBAValue(doc, "PPLANTYPE"));
                        } else {
                            //工艺总方案等文档
                            jsonObject.put("pplantype", "DOC");
                        }
                    }
                    array.put(jsonObject);
                }
            }
            result.put("data", array);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 方法功能: 加载执行更改界面更改后表格数据
     *
     * @author cjh
     * @date 2024/3/28
     */
    public JSONObject generateChangeAfter(String type) throws JSONException, WTException {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        JSONObject jsonObject = null;
        try {
            if("COMPLETED".equals(state.toString())) {
                jsonObject = generateChangeAfterRecordData(type);
            } else {
                String userId = rf.getReferenceString(currentuser);
                Map<String, String> map = new HashMap<String, String>();
                map.put(AnalysisObjEntry.ANALYSISNUMBER, pbo.getNumber());
                map.put(AnalysisObjEntry.DATATYPE, type);
                map.put(AnalysisObjEntry.RESPONSER, userId);
                List<AnalysisObjEntry> entries = AnalysisUtil.getAnalysisObjEntries(map);
                jsonObject = generateChangeAfterData(entries);
            }
            JSONArray array = (JSONArray) jsonObject.get("data");
            jsonObject.put("totalCount", array.length());
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);

        }
        return jsonObject;
    }

    /**
     * 方法功能: 生成未完成的执行更改任务的受影响对象表格数据
     *
     * @author cjh
     * @date 2024/3/28
     */
    public JSONObject generateChangeAfterData(List<AnalysisObjEntry> entries) {
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();

        try {
            for(AnalysisObjEntry entry : entries) {
                Persistable obj = null;
                try {
                    obj = rf.getReference("VR:" + entry.getVerOid()).getObject();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                if(obj != null) {
                    JSONObject jsonObject = new JSONObject();
                    String verOid = entry.getVerOid();
                    if(obj instanceof WTPart) {
                        WTPart part = (WTPart) obj;
                        WTPart latestPart = WTPartUtil.getLatestPartByNumberAndView(part, part.getViewName());
                        int versionResult = VersionComparator.getInstance(false).compare(part, latestPart);
                        if(versionResult < 0) {
                            jsonObject.put("id", verOid);
                            jsonObject.put("icontype", getIcon(part));
                            jsonObject.put("number", latestPart.getNumber() + "@" + rf.getReferenceString(latestPart));
                            jsonObject.put("name", latestPart.getName());
                            jsonObject.put("version", latestPart.getIterationDisplayIdentifier().toString());
                            jsonObject.put("state", latestPart.getLifeCycleState().getDisplay(Locale.CHINA));
                            jsonObject.put("modifier", latestPart.getModifier().getFullName());
                            jsonObject.put("modifyTime", WTStandardDateFormat.format(latestPart.getModifyTimestamp(), "yyyy-MM-dd"));
                            jsonObject.put("code", latestPart.getNumber());
                        } else {
                            continue;
                        }
                    } else if(obj instanceof WTDocument) {
                        WTDocument doc = (WTDocument) obj;
                        WTDocument latestDoc = WTDocumentUtil.getLatestDocumentByNumber(doc.getNumber());
                        int versionResult = VersionComparator.getInstance(false).compare(doc, latestDoc);
                        if(versionResult < 0) {
                            jsonObject.put("id", verOid);
                            jsonObject.put("icontype", getIcon(doc));
                            jsonObject.put("number", latestDoc.getNumber() + "@" + rf.getReferenceString(latestDoc));
                            jsonObject.put("name", latestDoc.getName());
                            jsonObject.put("version", latestDoc.getVersionIdentifier().getValue() + "." + latestDoc.getIterationIdentifier().getValue());
                            jsonObject.put("state", latestDoc.getLifeCycleState().getDisplay(Locale.CHINA));
                            jsonObject.put("modifier", latestDoc.getModifier().getFullName());
                            jsonObject.put("modifyTime", WTStandardDateFormat.format(latestDoc.getModifyTimestamp(), "yyyy-MM-dd"));
                            jsonObject.put("dept", IBAHelper.getIBAValue(latestDoc, "DEPT"));
                            jsonObject.put("code", latestDoc.getNumber());
                        } else {
                            continue;
                        }
                    }
                    array.put(jsonObject);
                }
            }
            result.put("data", array);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 方法功能: 生成已经完成的执行更改任务的更改后表格数据
     *
     * @author cjh
     * @date 2024/3/28
     */
    public JSONObject generateChangeAfterRecordData(String type) {
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();

        try {
            List<AnalysisObjHistoryRecord> records = AnalysisUtil.getAnalysisObjHistoryRecords(pbo.getNumber(), workItemOid, null, type);
            for(AnalysisObjHistoryRecord record : records) {
                Persistable obj = null;
                try {
                    obj = rf.getReference("VR:" + record.getVerOid()).getObject();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                if(obj != null){
                    String verOid = record.getVerOid();
                    JSONObject jsonObject = new JSONObject();
                    if(obj instanceof WTPart && AnalysisConstant.TYPE_PBOM.equals(type)) {
                        WTPart part = (WTPart) obj;
                        WTPart latestPart = WTPartUtil.getLatestPartByNumberAndView(part, part.getViewName());
                        int versionResult = VersionComparator.getInstance(false).compare(part, latestPart);
                        if(versionResult < 0) {
                            jsonObject.put("id", verOid);
                            jsonObject.put("icontype", getIcon(part));
                            jsonObject.put("number", latestPart.getNumber() + "@" + rf.getReferenceString(latestPart));
                            jsonObject.put("name", latestPart.getName());
                            jsonObject.put("version", latestPart.getIterationDisplayIdentifier().toString());
                            jsonObject.put("state", latestPart.getLifeCycleState().getDisplay(Locale.CHINA));
                            jsonObject.put("modifier", latestPart.getModifier().getFullName());
                            jsonObject.put("modifyTime", WTStandardDateFormat.format(latestPart.getModifyTimestamp(), "yyyy-MM-dd"));
                            jsonObject.put("code", latestPart.getNumber());
                        } else {
                            continue;
                        }
                    } else if(obj instanceof WTDocument) {
                        WTDocument doc = (WTDocument) obj;
                        WTDocument latestDoc = WTDocumentUtil.getLatestDocumentByNumber(doc.getNumber());
                        int versionResult = VersionComparator.getInstance(false).compare(doc, latestDoc);
                        if(versionResult < 0) {
                            jsonObject.put("id", verOid);
                            jsonObject.put("icontype", getIcon(doc));
                            jsonObject.put("number", latestDoc.getNumber() + "@" + rf.getReferenceString(latestDoc));
                            jsonObject.put("name", latestDoc.getName());
                            jsonObject.put("version", latestDoc.getVersionIdentifier().getValue() + "." + latestDoc.getIterationIdentifier().getValue());
                            jsonObject.put("state", latestDoc.getLifeCycleState().getDisplay(Locale.CHINA));
                            jsonObject.put("modifier", latestDoc.getModifier().getFullName());
                            jsonObject.put("modifyTime", WTStandardDateFormat.format(latestDoc.getModifyTimestamp(), "yyyy-MM-dd"));
                            jsonObject.put("dept", IBAHelper.getIBAValue(latestDoc, "DEPT"));
                            jsonObject.put("code", latestDoc.getNumber());
                        } else {
                            continue;
                        }
                    }
                    array.put(jsonObject);
                }
            }
            result.put("data", array);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 方法功能: 执行更改任务/闭环确认完成任务生成历史记录
     *
     * @author cjh
     * @date 2024/3/28
     */
    public void saveDealHistoryRecord() {
        try {
            //先查询删除已有的历史记录
            List<AnalysisObjHistoryRecord> records = AnalysisUtil.getAnalysisObjHistoryRecords(pbo.getNumber(), workItemOid, null, null);
            for(AnalysisObjHistoryRecord record : records) {
                CmPersistenceHelper.manager.delete(record);
            }
            //根据影响信息条目生成历史记录
            if(AnalysisConstant.ACTIVITY_NAME_CONFIRM.equals(activityName)){
                List<AnalysisObjEntry> entries = AnalysisUtil.getAnalysisObjEntries(pbo.getNumber(), null, null);
                for(AnalysisObjEntry entry : entries) {
                    AnalysisObjHistoryRecord record = new AnalysisObjHistoryRecord(workItemOid);
                    BeanUtil.copyProperties(entry, record, "keyId");
                    CmPersistenceHelper.manager.save(record);
                }
            } else if(AnalysisConstant.ACTIVITY_NAME_DEAL.equals(activityName)) {
                String userId = rf.getReferenceString(currentuser);
                Map<String, String> map = new HashMap<String, String>();
                map.put(AnalysisObjEntry.ANALYSISNUMBER, pbo.getNumber());
                map.put(AnalysisObjEntry.RESPONSER, userId);
                List<AnalysisObjEntry> entries = AnalysisUtil.getAnalysisObjEntries(map);
                for(AnalysisObjEntry entry : entries) {
                    if(AnalysisConstant.TYPE_PBOM.equals(entry.getDataType()) || AnalysisConstant.TYPE_TECHNICS.equals(entry.getDataType())){
                        AnalysisObjHistoryRecord record = new AnalysisObjHistoryRecord(workItemOid);
                        BeanUtil.copyProperties(entry, record, "keyId");
                        CmPersistenceHelper.manager.save(record);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void saveGyyRemark(String data) {
        if(data.contains("@!@")) {
            String[] info = data.split("@!@");
            String id = info[0];
            String remark = "";
            if(info.length > 1) {
                remark = info[1];
            }
            AnalysisObjEntry entry = AnalysisUtil.getAnalysisObjEntry(pbo.getNumber(), id, AnalysisConstant.TYPE_TECHNICS);
            if(entry != null) {
                entry.setRemarkGyy(remark);
                try {
                    CmPersistenceHelper.manager.update(entry);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

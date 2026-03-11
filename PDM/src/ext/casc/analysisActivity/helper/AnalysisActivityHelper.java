package ext.casc.analysisActivity.helper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.WTPartUtil;
import ext.casc.analysisActivity.bean.AnalysisObjEntry;
import ext.casc.analysisActivity.bean.AnalysisObjHistoryRecord;
import ext.casc.analysisActivity.bean.GWDealProductRecord;
import ext.casc.analysisActivity.process.GenerateRelatedAnalysisJson;
import ext.casc.util.WTUserUtil;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;
import org.dom4j.DocumentException;
import org.json.JSONArray;
import org.json.JSONObject;
import wt.change2.WTAnalysisActivity;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.method.RemoteAccess;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.project.Role;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.workflow.engine.WfProcess;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AnalysisActivityHelper implements RemoteAccess, Serializable {

    /**
     * 方法功能: 完成受影响对象分析 保存分析意见和历史记录
     *
     * @author cjh
     * @date 2024/3/28
     */
    public static String setAnalysisResult(String workItemOid, String data, String type) throws WTRuntimeException {
        if(StrUtil.isEmpty(workItemOid)) {
            return "流程异常！请联系管理员！";
        }
        StringBuilder sb = new StringBuilder();
        try {
            //保存意见
            GenerateRelatedAnalysisJson json = new GenerateRelatedAnalysisJson(workItemOid);
            json.process(data);

            //新建历史记录
            WTAnalysisActivity analysisActivity = json.getPbo();
            //先查询删除已有的历史记录
            List<AnalysisObjHistoryRecord> records = AnalysisUtil.getAnalysisObjHistoryRecords(analysisActivity.getNumber(), workItemOid, null, null);
            for(AnalysisObjHistoryRecord record : records) {
                CmPersistenceHelper.manager.delete(record);
            }
            //根据影响信息条目生成历史记录
            List<AnalysisObjEntry> entries = new ArrayList<AnalysisObjEntry>();
            if("1".equals(type)){
                entries = AnalysisUtil.getAnalysisObjEntries(analysisActivity.getNumber(), null, null);
            } else if("2".equals(type)){
                entries = AnalysisUtil.getModifyAnalysisObjEntries(analysisActivity.getNumber(), null, null);
            }
            for(AnalysisObjEntry entry : entries) {
                //完成无影响的条目
                if(AnalysisConstant.ANALYSIS_AFFECTED_NO.equals(entry.getAffected())) {
                    entry.setDealStatus(AnalysisConstant.DEAL_STATUS_FINISH);
                    CmPersistenceHelper.manager.update(entry);
                }
                //制品只有在制品已制品均为无影响才直接完成，否则等待对应系统闭环
                if(AnalysisConstant.ANALYSIS_AFFECTED_NO.equals(entry.getProduct()) && AnalysisUtil.isAllNoAffected(entry.getAnalysisNumber(), entry.getVerOid())) {
                    entry.setDealStatus(AnalysisConstant.DEAL_STATUS_FINISH);
                    CmPersistenceHelper.manager.update(entry);
                }

                if(!AnalysisConstant.DEAL_STATUS_FINISH.equals(entry.getDealStatus())){
                    //设置条目下发时间
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
                    String date = sdf.format(new Date());
                    Timestamp timestamp = Timestamp.valueOf(date);
                    entry.setTaskTime(timestamp);
                    //设置责任部门
                    String responser = entry.getResponser();
                    if(StrUtil.isNotEmpty(responser)){
                        ReferenceFactory rf = new ReferenceFactory();
                        WTUser user = (WTUser) rf.getReference(responser).getObject();
                        String dept = WTUserUtil.getDeptShortName(user);
                        entry.setUnit(dept);
                        CmPersistenceHelper.manager.update(entry);
                    }
                }
                AnalysisObjHistoryRecord record = new AnalysisObjHistoryRecord(workItemOid);
                BeanUtil.copyProperties(entry, record, "keyId");
                CmPersistenceHelper.manager.save(record);
            }
        } catch(Exception e) {
            e.printStackTrace();
            sb.append("保存更改影响分析信息失败，请联系管理员！错误信息：" + e.getMessage());
        }
        return sb.toString();
    }

    /**
     * 方法功能: 保存人员至角色
     *
     * @author cjh
     * @date 2024/8/28
     */
    public static void saveTeamRole(String roleName, List<WTUser> users, WfProcess process) {
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        Transaction tx = new Transaction();
        try {
            tx.start();
            Team team = (Team) process.getTeamId().getObject();
            Role role = Role.toRole(roleName);
            if(role == null) {
                return;
            }
            TeamHelper.service.deleteRole(role, team);

            // 将该用户保存至流程团队角色
            for(WTUser user : users) {
                team.addPrincipal(role, user);
            }
            team = (Team) PersistenceHelper.manager.refresh(team);
            team = (Team) PersistenceHelper.manager.save(team);

            tx.commit();
            tx = null;
        } catch(WTRuntimeException e) {
            e.printStackTrace();
        } catch(WTException e) {
            e.printStackTrace();
        } finally {
            if(tx != null) {
                tx.rollback();
                tx = null;
            }
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }

    }

    /**
     * 方法功能: 执行更改完成任务 保存处理历史记录
     *
     * @author cjh
     * @date 2024/3/28
     */
    public static void dealAnalysisResult(String workItemOid) throws WTRuntimeException {
        if(StrUtil.isEmpty(workItemOid)) {
            return;
        }
        try {
            //保存历史记录
            new GenerateRelatedAnalysisJson(workItemOid).saveDealHistoryRecord();
        } catch(Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 方法功能: 完成新增的受影响PBOM的时候，校验EBOM是否生成了PBOM
     *
     * @author cjh
     * @date 2024/3/28
     */
    public static Boolean validateNewTec(WTPart part) throws WTException, PropertyVetoException, DocumentException {
        if(part.getViewName().equals("Design")){
            WTPart pbom = WTPartUtil.getLatestPartByNumberAndView(part.getNumber(), "Manufacturing");
            if(pbom == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 方法功能: 校验某个影响分析任务制品是否处理完毕
     *
     * @author cjh
     * @date 2024/3/28
     */
    public static Boolean validateDealProduct(WTAnalysisActivity activity, String zids, String yids) {
        try {
            if(StrUtil.isNotEmpty(zids)) {
                String[] zs = zids.split("@@");
                for(String id : zs) {
                    CmQuerySpec qs = new CmQuerySpec(GWDealProductRecord.class);
                    qs.appendWhere(GWDealProductRecord.ANALYSISNUMBER, CmQuerySpec.EQUAL, activity.getNumber());
                    qs.appendAnd();
                    qs.appendWhere(GWDealProductRecord.VEROID, CmQuerySpec.EQUAL, id);
                    qs.appendAnd();
                    qs.appendWhere(GWDealProductRecord.SOURCE, CmQuerySpec.EQUAL, AnalysisConstant.SOURCE_ZAIZHIPIN);
                    CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
                    if(qr.size() == 0) {
                        return false;
                    }
                    while(qr.hasNext()) {
                        GWDealProductRecord record = (GWDealProductRecord) qr.next();
                        String status = record.getStatus();
                        if(StrUtil.isEmpty(status)) {
                            return false;
                        }
                    }
                }
            }
            if(StrUtil.isNotEmpty(yids)) {
                String[] ys = yids.split("@@");
                for(String id : ys) {
                    CmQuerySpec qs = new CmQuerySpec(GWDealProductRecord.class);
                    qs.appendWhere(GWDealProductRecord.ANALYSISNUMBER, CmQuerySpec.EQUAL, activity.getNumber());
                    qs.appendAnd();
                    qs.appendWhere(GWDealProductRecord.VEROID, CmQuerySpec.EQUAL, id);
                    qs.appendAnd();
                    qs.appendWhere(GWDealProductRecord.SOURCE, CmQuerySpec.EQUAL, "已制品");
                    CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
                    if(qr.size() == 0) {
                        return false;
                    }
                    while(qr.hasNext()) {
                        GWDealProductRecord record = (GWDealProductRecord) qr.next();
                        String status = record.getStatus();
                        if(StrUtil.isEmpty(status)) {
                            return false;
                        }
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 方法功能:生成制品处理记录数据
     *
     * @author cjh
     * @date 2024/6/26
     */
    public static JSONObject generateDealRecordData(String oid, String number, String type, String partnumber) throws Exception {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray array = new JSONArray();
            CmQuerySpec qs = new CmQuerySpec(GWDealProductRecord.class);
            qs.appendWhere(GWDealProductRecord.VEROID, CmQuerySpec.EQUAL, oid);
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.ANALYSISNUMBER, CmQuerySpec.EQUAL, number);
            qs.appendAnd();
            if("zaizhipin".equals(type)) {
                type = "在制品";
            } else if("yizhipin".equals(type)) {
                type = "已制品";
            }
            qs.appendWhere(GWDealProductRecord.SOURCE, CmQuerySpec.EQUAL, type);
            qs.appendOrderBy(GWDealProductRecord.CREATETIME, false);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while(qr.hasNext()) {
                GWDealProductRecord record = (GWDealProductRecord) qr.next();
                JSONObject object = new JSONObject();
                object.put("number", partnumber);
                object.put("dealtype", StrUtil.isEmpty(record.getDealType()) ? "" : record.getDealType());
                object.put("count", record.getCount());
                object.put("repaircount", record.getRepairCount());
                String card = StrUtil.isEmpty(record.getCard()) ? "" : record.getCard();
                if(AnalysisConstant.SOURCE_YIZHIPIN.equals(record.getSource()) && card.contains("_")){
                    card = card.split("_")[0];
                }
                object.put("card", card);
                object.put("code", StrUtil.isEmpty(record.getCode()) ? "" : record.getCode());
                object.put("responser", StrUtil.isEmpty(record.getResponser()) ? "" : record.getResponser());
                object.put("responsibleunit", StrUtil.isEmpty(record.getResponsibleUnit()) ? "" : record.getResponsibleUnit());
                object.put("status", StrUtil.isEmpty(record.getStatus()) ? "" : record.getStatus());
                object.put("finishtime", StrUtil.isEmpty(record.getFinishTime()) ? "" : record.getFinishTime());
                object.put("comments", StrUtil.isEmpty(record.getComments()) ? "" : record.getComments());
                array.put(object);
            }
            jsonObject.put("data", array);
            jsonObject.put("totalCount", array.length());
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return jsonObject;
    }

    public static String verifyData(String number) {
        StringBuilder sb = new StringBuilder();
        List<AnalysisObjEntry> entries = AnalysisUtil.getAnalysisObjEntries(number, null, null);
        ReferenceFactory rf = new ReferenceFactory();
        for(AnalysisObjEntry entry : entries) {
            if(AnalysisConstant.TYPE_TECHNICS.equals(entry.getDataType()) || AnalysisConstant.TYPE_PBOM.equals(entry.getDataType())) {
                if(StrUtil.isEmpty(entry.getAffected())){
                    String verOid = entry.getVerOid();
                    Persistable persistable = null;
                    try {
                        persistable = rf.getReference("VR:" + verOid).getObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(persistable != null){
                        sb.append("当前影响分析单据未成功保存意见，请刷新页面后重试！");
                        break;
                    }
                }
            } else if(entry.getDataType().contains(AnalysisConstant.PRODUCT)) {
                if(StrUtil.isEmpty(entry.getProduct())){
                    String verOid = entry.getVerOid();
                    Persistable persistable = null;
                    try {
                        persistable = rf.getReference("VR:" + verOid).getObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(persistable != null){
                        sb.append("当前影响分析单据未成功保存意见，请刷新页面后重试！");
                        break;
                    }
                }
            }
        }
        return sb.toString();
    }
}

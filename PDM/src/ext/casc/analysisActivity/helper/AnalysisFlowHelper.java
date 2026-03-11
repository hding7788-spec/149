package ext.casc.analysisActivity.helper;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.glaway.mpm.util.WorkflowUtil;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.analysisActivity.bean.AnalysisObjEntry;
import ext.casc.analysisActivity.bean.AnalysisToSourceLink;
import ext.casc.analysisActivity.bean.GWDealProductRecord;
import ext.casc.analysisActivity.bean.JointCellBean;
import ext.casc.constants.Constants;
import ext.casc.workflow.PrintHelper;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;
import wt.change2.WTAnalysisActivity;
import wt.change2.WTChangeRequest2;
import wt.fc.*;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.team.Team;
import wt.util.WTException;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import java.util.*;

public class AnalysisFlowHelper {

    public static String workflowData(String oid) throws WTException {
        //环节集合
        List<JointCellBean> cells = new ArrayList<JointCellBean>();
        //开始环节
        JointCellBean begin = new JointCellBean(null, JointCellBean.TYPE_RECT, 20, 20, 120, 80, "", "", "开始");
        cells.add(begin);
        ReferenceFactory rf = new ReferenceFactory();
        WTAnalysisActivity activity = null;
        try {
            Persistable persistable = rf.getReference(oid).getObject();
            if(persistable instanceof WTAnalysisActivity) {
                activity = (WTAnalysisActivity) persistable;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        //工艺更改申请单或者设计签审包环节
        JointCellBean pkg = null;
        if(activity != null) {
            QueryResult qr = PersistenceHelper.manager.navigate(activity, "sourceObject", AnalysisToSourceLink.class);
            if(qr.hasMoreElements()) {
                WTObject object = (WTObject) qr.nextElement();
                if(object instanceof WTChangeRequest2) {
                    WTChangeRequest2 request2 = (WTChangeRequest2) object;
                    String ecr2State = request2.getState().getState().getDisplay(Locale.CHINA);
                    String creator = request2.getCreator().getFullName();
                    String label = "责任人：" + creator + "\n";
                    pkg = new JointCellBean(begin.getId(), JointCellBean.TYPE_HEAD, 240, 20, 240, 80, "", "工艺更改申请单", label);
                    if(Constants.STATE_YIPIZHUN.equals(ecr2State)) {
                        QueryResult result = WfEngineHelper.service.getAssociatedProcesses(request2, null, null);
                        while(result.hasMoreElements()) {
                            WfProcess process = (WfProcess) result.nextElement();
                            if(process.getName().contains("工艺变更申请单签审流程")) {
                                List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
                                activityList = PrintHelper.getActivities(process, activityList);
                                Iterator iterator = activityList.iterator();
                                while(iterator.hasNext()) {
                                    WfAssignedActivity wfAssignedActivity = (WfAssignedActivity) iterator.next();
                                    String wfactivityName = wfAssignedActivity.getName();
                                    if(wfactivityName.equals("批准")) {
                                        ArrayList<WorkItem> workItemList = WorkflowUtil.getWorkItemFromActivityAndState(wfAssignedActivity, "COMPLETED");
                                        if(workItemList.size() != 0) {
                                            for(WorkItem workitem : workItemList) {
                                                String completeTime = workitem.getModifyTimestamp().toString();
                                                label += "完成时间：" + completeTime;
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    pkg.setLabel(label);
                    cells.add(pkg);
                } else if(object instanceof ProcessEnvelope || object instanceof ChangePackaged) {
                    String label = "";
                    if(object instanceof ChangePackaged) {
                        ChangePackaged packaged = (ChangePackaged) object;
                        label = "编号：" + packaged.getNumber() + "\n";
                    } else if(object instanceof ProcessEnvelope) {
                        ProcessEnvelope envelope = (ProcessEnvelope) object;
                        label = "编号：" + envelope.getNumber() + "\n";
                    }
                    QueryResult result = WfEngineHelper.service.getAssociatedProcesses(object, null, null);
                    if(result.hasMoreElements()) {
                        WfProcess process = (WfProcess) result.nextElement();
                        Role role = Role.toRole("ZHIPAIGONGYIZUZHANGZHE");
                        Team team = (Team) process.getTeamId().getObject();
                        Map map = team.getRolePrincipalMap();
                        List tempUserList = (List) map.get(role);
                        if(tempUserList != null && tempUserList.size() > 0) {
                            WTUser zhurengongyishi = (WTUser) ((WTPrincipalReference) tempUserList.get(0)).getObject();
                            label += "责任人：" + zhurengongyishi.getFullName();
                        }
                    }
                    pkg = new JointCellBean(begin.getId(), JointCellBean.TYPE_HEAD, 640, 20, 240, 80, "", "设计更改/偏离签审包", label);
                    pkg.setLabel(label);
                    cells.add(pkg);
                }
            }
        }
        //创建更改影响分析
        JointCellBean anaCell = null;
        String pre = begin.getId();
        if(pkg != null) {
            pre = pkg.getId();
        }
        String label = "责任人：" + activity.getCreator().getFullName() + "\n" + "创建时间：" + activity.getCreateTimestamp().toLocaleString();
        anaCell = new JointCellBean(pre, JointCellBean.TYPE_HEAD, 640, 160, 240, 80, "", "创建更改影响分析", label);
        cells.add(anaCell);
        //受影响对象分支
        boolean isComplete = false;
        QueryResult result = WfEngineHelper.service.getAssociatedProcesses(activity, null, null);
        if(result.hasMoreElements()) {
            WfProcess process = (WfProcess) result.nextElement();
            if(process.getName().contains("更改影响分析执行流程")) {
                List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
                activityList = PrintHelper.getActivities(process, activityList);
                Iterator iterator = activityList.iterator();
                while(iterator.hasNext()) {
                    WfAssignedActivity wfAssignedActivity = (WfAssignedActivity) iterator.next();
                    String wfactivityName = wfAssignedActivity.getName();
                    if(wfactivityName.equals("更改影响分析")) {
                        if(!"正在运行".equals(wfAssignedActivity.getState().getDisplay(Locale.CHINA))){
                            isComplete = true;
                            break;
                        }
                    }
                }
            }
        }
        if(anaCell != null) {
            //pbom
            JointCellBean pbom = getChild(activity, AnalysisConstant.TYPE_PBOM, "受影响PBOM更改执行", anaCell.getId(), isComplete, 20, 320);
            cells.add(pbom);
            //工艺
            JointCellBean technics = getChild(activity, AnalysisConstant.TYPE_TECHNICS, "受影响工艺更改执行", anaCell.getId(), isComplete, 420, 320);
            cells.add(technics);
            //在制品
            JointCellBean zaizhipin = getChild(activity, AnalysisConstant.TYPE_ZAIZHIPIN, "在制品更改执行", anaCell.getId(), isComplete, 820, 320);
            cells.add(zaizhipin);
            //自制
            JointCellBean zizhi = getChild(activity, AnalysisConstant.TYPE_ZAIZHIPIN, "在制品自制更改执行", zaizhipin.getId(), isComplete, 720, 500);
            cells.add(zizhi);
            //整件外协
            JointCellBean zjwx = getChild(activity, AnalysisConstant.TYPE_ZAIZHIPIN, "在制品外协更改执行", zaizhipin.getId(), isComplete, 950, 500);
            cells.add(zjwx);
            //已制品
            JointCellBean yizhipin = getChild(activity, AnalysisConstant.TYPE_YIZHIPIN, "已制品更改执行", anaCell.getId(), isComplete, 1220, 320);
            cells.add(yizhipin);
            //结束环节
            String endPre = pbom.getId() + "@" + technics.getId() + "@" + zizhi.getId() + "@" + zjwx.getId() + "@" + yizhipin.getId();
            JointCellBean end = new JointCellBean(endPre, JointCellBean.TYPE_RECT, 700, 800, 120, 80, "", "", "结束");
            cells.add(end);
        }
        JSONArray array = JSONUtil.parseArray(cells);
        return array.toString();
    }

    private static JointCellBean getChild(WTAnalysisActivity activity, String type, String header, String preId, boolean isComplete, int x, int y) {
        if(isComplete) {
            List<AnalysisObjEntry> entries = new ArrayList<AnalysisObjEntry>();
            try {
                CmQuerySpec qs = new CmQuerySpec(AnalysisObjEntry.class);
                qs.appendWhere(AnalysisObjEntry.ANALYSISNUMBER, CmQuerySpec.EQUAL, activity.getNumber());
                qs.appendAnd();
                qs.appendWhere(AnalysisObjEntry.DATATYPE, CmQuerySpec.EQUAL, type);
                if(AnalysisConstant.TYPE_PBOM.equals(type) || AnalysisConstant.TYPE_TECHNICS.equals(type)) {
                    qs.appendAnd();
                    qs.appendWhere(AnalysisObjEntry.AFFECTED, CmQuerySpec.NOT_EQUAL, AnalysisConstant.ANALYSIS_AFFECTED_NO);
                } else {
//                    qs.appendWhere(AnalysisObjEntry.PRODUCT, CmQuerySpec.NOT_EQUAL, AnalysisConstant.ANALYSIS_AFFECTED_NO);
                }
                CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
                while(qr.hasNext()) {
                    AnalysisObjEntry entry = (AnalysisObjEntry) qr.next();
                    if(type.contains(AnalysisConstant.PRODUCT)) {
                        if(AnalysisUtil.isAllNoAffected(entry.getAnalysisNumber(), entry.getVerOid())) {
                            continue;
                        }
                        if("在制品外协更改执行".equals(header)) {
                            AnalysisObjEntry yizhipin = AnalysisUtil.getAnalysisObjEntry(entry.getAnalysisNumber(), entry.getVerOid(), AnalysisConstant.TYPE_YIZHIPIN);
                            if(StrUtil.isEmpty(yizhipin.getReceiveStatus())) {
                                JointCellBean cell = new JointCellBean(preId, JointCellBean.TYPE_HEAD, x, y, 200, 80, "", header, "未返回全部数量");
                                cell.setStatus("未完成");
                                return cell;
                            }
                        } else if(StrUtil.isEmpty(entry.getReceiveStatus())) {
                            JointCellBean cell = new JointCellBean(preId, JointCellBean.TYPE_HEAD, x, y, 200, 80, "", header, "未返回全部数量");
                            cell.setStatus("未完成");
                            return cell;
                        }
                    }
                    entries.add(entry);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            int oks = 0;
            for(AnalysisObjEntry entry : entries) {
                if("在制品自制更改执行".equals(header)) {
                    if(getProductDeal(entry, "MES")) {
                        oks++;
                    }
                } else if("在制品外协更改执行".equals(header)) {
                    if(getProductDeal(entry, "ZJWX")) {
                        oks++;
                    }
                } else {
                    if(AnalysisConstant.DEAL_STATUS_FINISH.equals(entry.getDealStatus())) {
                        oks++;
                    }
                }
            }

            if("在制品自制更改执行".equals(header) || "在制品外协更改执行".equals(header)) {
                String label = "已完成条目：" + oks + "条";
                JointCellBean cell = new JointCellBean(preId, JointCellBean.TYPE_HEAD, x, y, 200, 80, "", header, label);
                if(oks < entries.size()) {
                    cell.setStatus("未完成");
                }
                return cell;
            } else {
                String label = "子任务条目：" + entries.size() + "条\n" + "已完成条目：" + oks + "条";
                Set<String> users = new HashSet<String>();
                String title = "";
                for(AnalysisObjEntry entry : entries) {
                    if(StrUtil.isNotEmpty(entry.getResponser())) {
                        users.add(entry.getResponser());
                    }
                }
                if(users.size() > 0) {
                    title += "负责人：";
                    ReferenceFactory rf = new ReferenceFactory();
                    for(String uid : users) {
                        try {

                            WTUser user = (WTUser) rf.getReference(uid).getObject();
                            title += user.getFullName() + "、";
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                    title = title.substring(0, title.length() - 1);
                }
                JointCellBean cell = new JointCellBean(preId, JointCellBean.TYPE_HEAD, x, y, 200, 80, title, header, label);
                if(oks < entries.size()) {
                    cell.setStatus("未完成");
                }
                return cell;
            }
        } else {
            JointCellBean cell = new JointCellBean(preId, JointCellBean.TYPE_HEAD, x, y, 200, 80, "", header, "未触发");
            cell.setStatus("未完成");
            return cell;
        }
    }

    private static boolean getProductDeal(AnalysisObjEntry entry, String type) {
        try {
            CmQuerySpec qs = new CmQuerySpec(GWDealProductRecord.class);
            qs.appendWhere(GWDealProductRecord.ANALYSISNUMBER, CmQuerySpec.EQUAL, entry.getAnalysisNumber());
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.VEROID, CmQuerySpec.EQUAL, entry.getVerOid());
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.SOURCE, CmQuerySpec.EQUAL, AnalysisConstant.SOURCE_ZAIZHIPIN);
            qs.appendAnd();
            qs.appendWhere(GWDealProductRecord.KEY_ID, CmQuerySpec.LIKE, "%" + type + "%");
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            if(qr.size() == 0 && "MES".equals(type)) {
                return false;
            }
            int count = 0;
            while(qr.hasNext()) {
                GWDealProductRecord record = (GWDealProductRecord) qr.next();
                String status = record.getStatus();
                if("ZJWX".equals(type)){
                    count += record.getCount();
                }
                if(StrUtil.isEmpty(status)) {
                    return false;
                }
            }
            if("ZJWX".equals(type) && count < entry.getZjwxcount()){
                return false;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}

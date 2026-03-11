package ext.casc.work;

import cn.hutool.core.util.StrUtil;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.access.AccessAdminUtil;
import ext.casc.constants.Constants;
import ext.casc.util.IBAHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.workflow.definer.WfTemplateObjectReference;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfAssignmentEventAudit;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import java.sql.Timestamp;
import java.util.*;

public class AssignmentsListHelper {
    public static List<String> gongyiProcessNames = new ArrayList<String>();
    public static List<String> ziyanProcessNames = new ArrayList<String>();

    static {
        gongyiProcessNames.add("材料定额流程");
        gongyiProcessNames.add("工艺更改单签审流程");
        gongyiProcessNames.add("工艺更改流程");
        gongyiProcessNames.add("工艺任务分工流程");
        gongyiProcessNames.add("工艺文件打印流程");
        gongyiProcessNames.add("三级工艺文件签审流程");
        gongyiProcessNames.add("五级工艺文件签审流程");
        gongyiProcessNames.add("三级报表类工艺文件签审流程");
        gongyiProcessNames.add("五级报表类工艺文件签审流程");
        gongyiProcessNames.add("技术协议签审流程");
        gongyiProcessNames.add("质量报告签审流程");

        ziyanProcessNames.add("ECN流程");
        ziyanProcessNames.add("文档签审流程");
        ziyanProcessNames.add("零部件签审流程");
    }

    public static List listRessignAssignments() throws WTException {
        List result = new ArrayList();
        WTUser currentuser = (WTUser) SessionHelper.manager.getPrincipal();
        WTUser admin = (WTUser) SessionHelper.manager.setAdministrator();
        QuerySpec queryspec = new QuerySpec(WorkItem.class);
        queryspec.appendWhere(new SearchCondition(WorkItem.class,
                "actionPerformed", SearchCondition.EQUAL, "Reassigned"), new int[0]);
        queryspec.appendAnd();
        queryspec.appendWhere(new SearchCondition(WorkItem.class,
                "role", SearchCondition.EQUAL, "ZHIPAIGONGYIZUZHANGZHE"), new int[0]);
        QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
        while (queryresult.hasMoreElements()) {
            Object object = queryresult.nextElement();
            if (object instanceof WorkItem) {
                WorkItem wi = (WorkItem) object;
                //过滤已隐藏流程任务 add by cjh 20231228
                if(Constants.WORKITEM_NAME_YIYINCANG.equals(wi.getEventSet().toString())){
                    continue;
                }
                //保密检查改造：过滤高密于用户的数据条目 20251021
                try {
                    Persistable obj = wi.getPrimaryBusinessObject().getObject();
                    String secret = IBAHelper.getIBAStringValue((WTObject) obj, "SECRET");
                    if(StrUtil.isNotEmpty(secret)) {
                        if(secret.indexOf("机密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("秘密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("内部") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")
                                    && !AccessAdminUtil.isGroup("内部用户密级组")) {
                                continue;
                            }
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
                WfActivity wfAct = (WfActivity) wi.getSource().getObject();
                if (wfAct.getTemplate().getName().equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)) {
                    QuerySpec queryspec2 = new QuerySpec(WfAssignmentEventAudit.class);
                    queryspec2.appendWhere(new SearchCondition(WfAssignmentEventAudit.class,
                            "activityKey", SearchCondition.EQUAL, wfAct.getKey()), new int[0]);
                    QueryResult queryresult2 = PersistenceHelper.manager.find(queryspec2);

                    while (queryresult2.hasMoreElements()) {
                        WfAssignmentEventAudit assignment = (WfAssignmentEventAudit) queryresult2.nextElement();
                        Object o = assignment.getOldAssigneeRef();
                        if (assignment.getOldAssigneeRef() != null) {
                            if (currentuser.equals(assignment.getOldAssigneeRef().getObject())) {
                                result.add(wi);
                                break;
                            }
                        }

                    }

                }

            }
        }
        SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
        return result;
    }

    public static Object listAll(QueryResult qResult) throws WTException {
        List result = new ArrayList();
        while (qResult.hasMoreElements()) {
            Object object = qResult.nextElement();
            if (object instanceof WorkItem) {
                WorkItem workItem = (WorkItem) object;
                //过滤已隐藏流程任务 add by cjh 20231228
                if(Constants.WORKITEM_NAME_YIYINCANG.equals(workItem.getEventSet().toString())){
                    continue;
                }
                //保密检查改造：过滤高密于用户的数据条目 20251021
                try {
                    Persistable obj = workItem.getPrimaryBusinessObject().getObject();
                    String secret = IBAHelper.getIBAStringValue((WTObject) obj, "SECRET");
                    if(StrUtil.isNotEmpty(secret)) {
                        if(secret.indexOf("机密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("秘密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("内部") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")
                                    && !AccessAdminUtil.isGroup("内部用户密级组")) {
                                continue;
                            }
                        }
                    }
                } catch(Exception e) {
                    //e.printStackTrace();
                }
                result.add(workItem);
            }
        }
        return result;
    }

    public static Object listSendFrom805Assignments(QueryResult qResult) throws WTException {
        List result = new ArrayList();
        while (qResult.hasMoreElements()) {
            Object object = qResult.nextElement();
            if (object instanceof WorkItem) {
                WorkItem workItem = (WorkItem) object;
                //过滤已隐藏流程任务 add by cjh 20231228
                if(Constants.WORKITEM_NAME_YIYINCANG.equals(workItem.getEventSet().toString())){
                    continue;
                }
                //保密检查改造：过滤高密于用户的数据条目 20251021
                try {
                    Persistable obj = workItem.getPrimaryBusinessObject().getObject();
                    String secret = IBAHelper.getIBAStringValue((WTObject) obj, "SECRET");
                    if(StrUtil.isNotEmpty(secret)) {
                        if(secret.indexOf("机密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("秘密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("内部") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")
                                    && !AccessAdminUtil.isGroup("内部用户密级组")) {
                                continue;
                            }
                        }
                    }
                } catch(Exception e) {
                    //e.printStackTrace();
                }
                WfAssignedActivity wa = (WfAssignedActivity) workItem.getSource().getObject();
                WfProcess process = wa.getParentProcess();
                if (process.getTemplate().getName().startsWith("149")) {
                    //是设计制造协同流程
                    if (workItem.getPrimaryBusinessObject() == null) {
                        continue;
                    }
                    Persistable p = workItem.getPrimaryBusinessObject().getObject();
                    if (p instanceof ProcessEnvelope) {
                        String name = ((ProcessEnvelope) p).getName();
                        if (name.endsWith("(805)")) {
                            result.add(workItem);
                        }
                    } else if (p instanceof ChangePackaged) {
                        String name = ((ChangePackaged) p).getName();
                        if (name.endsWith("(805))")) {
                            result.add(workItem);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static Object listSendFromNO8Assignments(QueryResult qResult) throws WTException {
        List result = new ArrayList();
        while (qResult.hasMoreElements()) {
            Object object = qResult.nextElement();
            if (object instanceof WorkItem) {
                WorkItem workItem = (WorkItem) object;
                //过滤已隐藏流程任务 add by cjh 20231228
                if(Constants.WORKITEM_NAME_YIYINCANG.equals(workItem.getEventSet().toString())){
                    continue;
                }
                //保密检查改造：过滤高密于用户的数据条目 20251021
                try {
                    Persistable obj = workItem.getPrimaryBusinessObject().getObject();
                    String secret = IBAHelper.getIBAStringValue((WTObject) obj, "SECRET");
                    if(StrUtil.isNotEmpty(secret)) {
                        if(secret.indexOf("机密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("秘密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("内部") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")
                                    && !AccessAdminUtil.isGroup("内部用户密级组")) {
                                continue;
                            }
                        }
                    }
                } catch(Exception e) {
                    //e.printStackTrace();
                }
                WfAssignedActivity wa = (WfAssignedActivity) workItem.getSource().getObject();
                WfProcess process = wa.getParentProcess();
                if (process.getTemplate().getName().startsWith("149")) {
                    //是设计制造协同流程
                    if (workItem.getPrimaryBusinessObject() == null) {
                        continue;
                    }
                    Persistable p = workItem.getPrimaryBusinessObject().getObject();
                    if (p instanceof ProcessEnvelope) {
                        String name = ((ProcessEnvelope) p).getName();
                        if (name.endsWith("(八部)")) {
                            result.add(workItem);
                        }
                    } else if (p instanceof ChangePackaged) {
                        String name = ((ChangePackaged) p).getName();
                        if (name.endsWith("(八部))")) {
                            result.add(workItem);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static Object listGongYiAssignments(QueryResult qResult) throws WTException {
        List result = new ArrayList();
        while (qResult.hasMoreElements()) {
            Object object = qResult.nextElement();
            if (object instanceof WorkItem) {
                WorkItem workItem = (WorkItem) object;
                //过滤已隐藏流程任务 add by cjh 20231228
                if(Constants.WORKITEM_NAME_YIYINCANG.equals(workItem.getEventSet().toString())){
                    continue;
                }
                //保密检查改造：过滤高密于用户的数据条目 20251021
                try {
                    Persistable obj = workItem.getPrimaryBusinessObject().getObject();
                    String secret = IBAHelper.getIBAStringValue((WTObject) obj, "SECRET");
                    if(StrUtil.isNotEmpty(secret)) {
                        if(secret.indexOf("机密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("秘密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("内部") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")
                                    && !AccessAdminUtil.isGroup("内部用户密级组")) {
                                continue;
                            }
                        }
                    }
                } catch(Exception e) {
                    //e.printStackTrace();
                }
                WfAssignedActivity wa = (WfAssignedActivity) workItem.getSource().getObject();
                WfProcess process = wa.getParentProcess();
                if (gongyiProcessNames.contains(process.getTemplate().getName())) {
                    result.add(workItem);
                }
            }
        }
        return result;
    }

    public static Object listZiYanAssignments(QueryResult qResult) throws WTException {
        List result = new ArrayList();
        while (qResult.hasMoreElements()) {
            Object object = qResult.nextElement();
            if (object instanceof WorkItem) {
                WorkItem workItem = (WorkItem) object;
                //过滤已隐藏流程任务 add by cjh 20231228
                if(Constants.WORKITEM_NAME_YIYINCANG.equals(workItem.getEventSet().toString())){
                    continue;
                }
                //保密检查改造：过滤高密于用户的数据条目 20251021
                try {
                    Persistable obj = workItem.getPrimaryBusinessObject().getObject();
                    String secret = IBAHelper.getIBAStringValue((WTObject) obj, "SECRET");
                    if(StrUtil.isNotEmpty(secret)) {
                        if(secret.indexOf("机密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("秘密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("内部") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")
                                    && !AccessAdminUtil.isGroup("内部用户密级组")) {
                                continue;
                            }
                        }
                    }
                } catch(Exception e) {
                    //e.printStackTrace();
                }
                WfAssignedActivity wa = (WfAssignedActivity) workItem.getSource().getObject();
                WfProcess process = wa.getParentProcess();
                if (ziyanProcessNames.contains(process.getTemplate().getName())) {
                    result.add(workItem);
                }
            }
        }
        return result;
    }

    //add by libo 2017.02.06 begin
    //工时定额
    public static Object listGSDEAssignments(QueryResult qResult) throws WTException {
        List result = new ArrayList();
        while (qResult.hasMoreElements()) {
            Object object = qResult.nextElement();
            if (object instanceof WorkItem) {
                WorkItem workItem = (WorkItem) object;
                //过滤已隐藏流程任务 add by cjh 20231228
                if(Constants.WORKITEM_NAME_YIYINCANG.equals(workItem.getEventSet().toString())){
                    continue;
                }
                //保密检查改造：过滤高密于用户的数据条目 20251021
                try {
                    Persistable obj = workItem.getPrimaryBusinessObject().getObject();
                    String secret = IBAHelper.getIBAStringValue((WTObject) obj, "SECRET");
                    if(StrUtil.isNotEmpty(secret)) {
                        if(secret.indexOf("机密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("秘密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("内部") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")
                                    && !AccessAdminUtil.isGroup("内部用户密级组")) {
                                continue;
                            }
                        }
                    }
                } catch(Exception e) {
                    //e.printStackTrace();
                }
                WfAssignedActivity wa = (WfAssignedActivity) workItem.getSource().getObject();
                String taskName = wa.getName();
                if (Constants.WORKITEM_NAME_GSDE.equals(taskName)) {
                    result.add(workItem);
                }
            }
        }
        return result;
    }

    //仅显示最新活动
    public static Object listJXSZXHDAssignments(QueryResult qResult) throws WTException {
        WTUser currentuser = (WTUser) SessionHelper.manager.getPrincipal();
        Map<WfTemplateObjectReference, WorkItem> map = new HashMap<WfTemplateObjectReference, WorkItem>();
        QuerySpec queryspec = new QuerySpec(WorkItem.class);
        queryspec.appendWhere(new SearchCondition(WorkItem.class,
                "completedBy", SearchCondition.EQUAL, currentuser.getName()), new int[0]);
        QueryResult qResult1 = PersistenceHelper.manager.find(queryspec);
        while (qResult1.hasMoreElements()) {
            Object object = qResult1.nextElement();
            if (object instanceof WorkItem) {
                WorkItem workItem = (WorkItem) object;
                //过滤已隐藏流程任务 add by cjh 20231228
                if(Constants.WORKITEM_NAME_YIYINCANG.equals(workItem.getEventSet().toString())){
                    continue;
                }
                //保密检查改造：过滤高密于用户的数据条目 20251021
                try {
                    Persistable obj = workItem.getPrimaryBusinessObject().getObject();
                    String secret = IBAHelper.getIBAStringValue((WTObject) obj, "SECRET");
                    if(StrUtil.isNotEmpty(secret)) {
                        if(secret.indexOf("机密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("秘密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("内部") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")
                                    && !AccessAdminUtil.isGroup("内部用户密级组")) {
                                continue;
                            }
                        }
                    }
                } catch(Exception e) {
                    //e.printStackTrace();
                }
                WfAssignedActivity wa = (WfAssignedActivity) workItem.getSource().getObject();
                WfTemplateObjectReference wto = wa.getTemplate();
                if (map.containsKey(wto)) {
                    WorkItem workItem2 = map.get(wto);
                    Timestamp timestamp2 = workItem2.getModifyTimestamp();
                    Date date2 = timestamp2;
                    Date date = workItem.getModifyTimestamp();
                    if (date.getTime() > date2.getTime()) {
                        map.put(wto, workItem);
                    }
                } else {
                    map.put(wto, workItem);
                }
            }
        }
        List result = new ArrayList(map.values());
        while (qResult.hasMoreElements()) {
            Object object = qResult.nextElement();
            if (object instanceof WorkItem) {
                WorkItem workItem = (WorkItem) object;
                //过滤已隐藏流程任务 add by cjh 20231228
                if(Constants.WORKITEM_NAME_YIYINCANG.equals(workItem.getEventSet().toString())){
                    continue;
                }
                //保密检查改造：过滤高密于用户的数据条目 20251021
                try {
                    Persistable obj = workItem.getPrimaryBusinessObject().getObject();
                    String secret = IBAHelper.getIBAStringValue((WTObject) obj, "SECRET");
                    if(StrUtil.isNotEmpty(secret)) {
                        if(secret.indexOf("机密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("秘密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("内部") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")
                                    && !AccessAdminUtil.isGroup("内部用户密级组")) {
                                continue;
                            }
                        }
                    }
                } catch(Exception e) {
                    //e.printStackTrace();
                }
                result.add(workItem);
            }
        }
        return result;
    }
    //add by libo 2017.02.06 end

    /**
     * 本人发起的任务
     *
     * @param qResult
     * @return
     * @throws WTException
     */
    public static Object listBRFQAssignments(QueryResult qResult) throws WTException {
        List result = new ArrayList();
        WTUser currentuser = (WTUser) SessionHelper.manager.getPrincipal();
        while (qResult.hasMoreElements()) {
            Object object = qResult.nextElement();
            if (object instanceof WorkItem) {
                WorkItem workItem = (WorkItem) object;
                //过滤已隐藏流程任务 add by cjh 20231228
                if(Constants.WORKITEM_NAME_YIYINCANG.equals(workItem.getEventSet().toString())){
                    continue;
                }
                //保密检查改造：过滤高密于用户的数据条目 20251021
                try {
                    Persistable obj = workItem.getPrimaryBusinessObject().getObject();
                    String secret = IBAHelper.getIBAStringValue((WTObject) obj, "SECRET");
                    if(StrUtil.isNotEmpty(secret)) {
                        if(secret.indexOf("机密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("秘密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("内部") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")
                                    && !AccessAdminUtil.isGroup("内部用户密级组")) {
                                continue;
                            }
                        }
                    }
                } catch(Exception e) {
                    //e.printStackTrace();
                }
                WfAssignedActivity wa = (WfAssignedActivity) workItem.getSource().getObject();
                WfProcess process = wa.getParentProcess();
                if(process.getCreator().getName().equals(currentuser.getName())){
                    result.add(workItem);
                }
            }
        }
        return result;
    }

    /**
     * 本人发起未完成的任务
     *
     * @param qResult
     * @return
     * @throws WTException
     */
    public static Object listBRFQWWCAssignments(QueryResult qResult) throws WTException {
        List result = new ArrayList();
        WTUser currentuser = (WTUser) SessionHelper.manager.getPrincipal();
        while (qResult.hasMoreElements()) {
            Object object = qResult.nextElement();
            if (object instanceof WorkItem) {
                WorkItem workItem = (WorkItem) object;
                //过滤已隐藏流程任务 add by cjh 20231228
                if(Constants.WORKITEM_NAME_YIYINCANG.equals(workItem.getEventSet().toString())){
                    continue;
                }
                //保密检查改造：过滤高密于用户的数据条目 20251021
                try {
                    Persistable obj = workItem.getPrimaryBusinessObject().getObject();
                    String secret = IBAHelper.getIBAStringValue((WTObject) obj, "SECRET");
                    if(StrUtil.isNotEmpty(secret)) {
                        if(secret.indexOf("机密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("秘密") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")) {
                                continue;
                            }
                        } else if(secret.indexOf("内部") > -1) {
                            if(!AccessAdminUtil.isGroup("重要用户密级组")
                                    && !AccessAdminUtil.isGroup("一般用户密级组")
                                    && !AccessAdminUtil.isGroup("内部用户密级组")) {
                                continue;
                            }
                        }
                    }
                } catch(Exception e) {
                    //e.printStackTrace();
                }
                WfAssignedActivity wa = (WfAssignedActivity) workItem.getSource().getObject();
                WfProcess process = wa.getParentProcess();
                String processState = process.getState().getDisplay(Locale.CHINA);
                if(process.getCreator().getName().equals(currentuser.getName()) && workItem.getStatus().getDisplay(Locale.CHINA).equals("潜在的")){
                    result.add(workItem);
                }
            }
        }
        return result;
    }

    //已隐藏
    public static Object listYYCAssignments(QueryResult qResult) throws WTException {
        List result = new ArrayList();
        while (qResult.hasMoreElements()) {
            Object object = qResult.nextElement();
            if (object instanceof WorkItem) {
                WorkItem workItem = (WorkItem) object;
                if(Constants.WORKITEM_NAME_YIYINCANG.equals(workItem.getEventSet().toString())){
                    result.add(workItem);
                }
            }
        }
        return result;
    }
}

package ext.casc.workflow;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.PhaseTemplate;
import wt.lifecycle.State;
import wt.maturity.MaturityHelper;
import wt.maturity.PromotionNotice;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.team.TeamReference;
import wt.team.WTRoleHolder2;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.util.CollationKeyFactory;
import wt.util.SortedEnumeration;
import wt.util.WTException;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.workflow.definer.UserEventVector;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfVotingEventAudit;
import wt.workflow.work.WorkItem;

import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.util.LDAPAdapter;

/**
 * 工作流支持例程
 * 
 */
public class WfUtil {
    /**
     * 获取已指配活动所在流程的历史用户意见
     * 
     * @param act
     *            已指配活动对象
     * @return Vector of UserComment
     * @throws WTException
     */
    public static Vector getHistoryComments(WfProcess process) throws WTException {
        Vector result = new Vector();

        WTCollection votingCol = WfEngineHelper.service.getVotingEvents(
                process, null, null, null);
        for (Iterator it = votingCol.persistableIterator(); it.hasNext();) {
            UserComment uc = new UserComment();
            WfVotingEventAudit audit = (WfVotingEventAudit) it.next();

            UserEventVector eventList = audit.getEventList();
            String route = "";
            for (int i = 0; eventList != null && i < eventList.size(); i++) {
                if (route.length() > 0)
                    route += ",";
                route += eventList.get(i);
            }

            uc.route = route;
            uc.actName = audit.getActivityName();
            uc.userRef = audit.getAssigneeRef();
            uc.comments = audit.getUserComment();
            uc.role = audit.getRole();
            uc.timestamp = audit.getPersistInfo().getCreateStamp();

            result.add(uc.repair());
        }

        Collections.sort(result, new Comparator() {
            public int compare(Object o1, Object o2) {
                UserComment uc1 = (UserComment) o1;
                UserComment uc2 = (UserComment) o2;
                if (uc1.timestamp != null && uc2.timestamp == null)
                    return -1;
                if (uc1.timestamp == null && uc2.timestamp != null)
                    return 1;
                if (uc1.timestamp == null && uc2.timestamp == null)
                    return 0;
                if (uc1.timestamp.equals(uc2.timestamp))
                    return 0;
                return uc1.timestamp.after(uc2.timestamp) ? 1 : -1;
            }
        });

        return result;
    }

    /**
     * 取对象最近一次流程实例的签审历史
     * 
     * @param p
     *            流程主对象
     * @return Vector of UserComment
     * @throws WTException
     */
    public static Vector getHistoryComments(Persistable p) throws WTException {
        // 取所有关联流程实例
        QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(p,
                null, null);
        if (qrProcs.size() < 1)
            return new Vector();

        // 按时间排序,取最新一个
        CollationKeyFactory timeKeyFact = new CollationKeyFactory() {
            public String getCollationString(Object o) {
                if (!(o instanceof Persistable) || !PersistenceHelper.isPersistent(o))
                    return "";
                return ((Persistable) o).getPersistInfo().getModifyStamp().toString();
            }
        };
        Enumeration enProcs = new SortedEnumeration(qrProcs, timeKeyFact,
                SortedEnumeration.DESCENDING);
        WfProcess proc = (WfProcess) enProcs.nextElement();

        // 取签审历史
        Vector result = getHistoryComments(proc);
        return result;
    }

    /**
     * 从签审历史中按活动名称、角色名称匹配签名
     * 
     * @param hisComments
     *            签审历史
     * @param regxAct
     *            活动名regx
     * @param regxRole
     *            角色名regx
     * @return 签名列表，有多人时以逗号分隔
     */
    public String matchSignature(Vector hisComments, String regxAct, String regxRole) {
        if (hisComments == null)
            return null;

        ArrayList list = new ArrayList();
        for (int i = hisComments.size() - 1; i >= 0; i--) {
            UserComment uc = (UserComment) hisComments.get(i);
            String actName = uc.actName;

            if (regxAct != null) {
                if (!actName.matches(regxAct))
                    continue;
            }
            if (regxRole != null) {
                String roleName = uc.role.getDisplay(Locale.SIMPLIFIED_CHINESE);
                if (!roleName.matches(regxRole))
                    continue;
            }

            String userName = uc.userRef.getFullName();
            if (!list.contains(userName))
                list.add(userName);
        }

        String result = null;
        if (list.size() > 0) {
            result = (String) list.get(0);
            for (int i = 1; i < list.size(); i++)
                result += "," + list.get(i);
        }

        return result;
    }

    /**
     * 取对象最近一次流程实例中所有已经过活动角色名单
     * 
     * @param p
     *            对象
     * @return 角色、名单HashMap
     * @throws WTException
     */
    public static HashMap getSigRoles(Persistable p) throws WTException {
        Vector comments = getHistoryComments(p);
        HashMap result = new HashMap();
        HashSet roleUserSet = new HashSet();
        for (int i = 0; comments != null && i < comments.size(); i++) {
            UserComment uc = (UserComment) comments.get(i);
            String role = uc.role.getDisplay(Locale.SIMPLIFIED_CHINESE);
            String user = uc.userRef.getFullName();
            if (result.get(role) == null) {
                result.put(role, user);
            } else {
                if (!roleUserSet.contains(role + "$$$" + user))
                    result.put(role, result.get(role) + "," + user);
            }
            roleUserSet.add(role + "$$$" + user);
        }

        return result;
    }

    /**
     * 取对象最近一次流程实例中所有已经过活动角色部门清单
     * 
     * @param p
     * @return
     * @throws WTException
     */
    public static HashMap getSigRoleDepts(Persistable p) throws WTException {
        Vector comments = getHistoryComments(p);
        HashMap result = new HashMap();
        HashSet roleDeptSet = new HashSet();
        LDAPAdapter ldap = new LDAPAdapter();
        for (int i = 0; comments != null && i < comments.size(); i++) {
            UserComment uc = (UserComment) comments.get(i);
            String role = uc.role.getDisplay(Locale.SIMPLIFIED_CHINESE);
            String user = uc.userRef.getName();
            String dept = ldap.getTopDept(user);
            if (result.get(role) == null)
                result.put(role, dept);
            else {
                if (!roleDeptSet.contains(role + "$$$" + dept))
                    result.put(role, result.get(role) + "," + dept);
            }
            roleDeptSet.add(role + "$$$" + dept);
        }

        return result;
    }

    /**
     * 取对象最近一次流程实例中所有已经过活动角色意见
     * 
     * @param p
     *            对象
     * @return 角色、名单HashMap
     * @throws WTException
     */
    public static HashMap getSigComments(Persistable p) throws WTException {
        Vector comments = getHistoryComments(p);
        HashMap result = new HashMap();
        HashSet roleCommSet = new HashSet();
        for (int i = 0; comments != null && i < comments.size(); i++) {
            UserComment uc = (UserComment) comments.get(i);
            String role = uc.role.getDisplay(Locale.SIMPLIFIED_CHINESE) + "意见";
            String comm = uc.mergeComment();
            if (result.get(role) == null)
                result.put(role, comm);
            else {
                if (!roleCommSet.contains(role + "$$$;;;" + comm))
                    result.put(role, result.get(role) + ";" + comm);
            }
            roleCommSet.add(role + "$$$;;;" + comm);
        }

        return result;
    }

    /**
     * 取对象最近一次流程实例中所有已经过活动角色签署日期
     * 
     * @param p
     *            对象
     * @return 角色、名单HashMap
     * @throws WTException
     */
    public static HashMap getSigDates(Persistable p) throws WTException {
        Vector comments = getHistoryComments(p);
        HashMap result = new HashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        for (int i = 0; comments != null && i < comments.size(); i++) {
            UserComment uc = (UserComment) comments.get(i);
            String role = uc.role.getDisplay(Locale.SIMPLIFIED_CHINESE) + "日期";
            String date = sdf.format(uc.timestamp);
            if (result.get(role) == null)
                result.put(role, date);
        }

        return result;
    }

    /**
     * 取对象最近一次流出实例中所有已经过活动角色的意见签名集
     * 
     * @param p
     *            对象
     * @return 角色、意见－签名－日期HashMap
     * @throws WTException
     */
    public static HashMap getSigRoleCommentSet(Persistable p) throws WTException {
        Vector comments = getHistoryComments(p);
        HashMap result = new HashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        HashSet roleUserSet = new HashSet();
        for (int i = 0; comments != null && i < comments.size(); i++) {
            UserComment uc = (UserComment) comments.get(i);
            String role = uc.role.getDisplay(Locale.SIMPLIFIED_CHINESE) + "意见签名集";
            String user = uc.userRef.getFullName();
            String date = sdf.format(uc.timestamp);
            String comm = uc.mergeComment();
            String text = "<tr><td align=right>" + comm + "<td align=right valign=bottom>" +
                    user + "&nbsp;&nbsp;" + date + "</td></tr>";
            if (result.get(role) == null)
                result.put(role, text);
            else {
                if (!roleUserSet.contains(user))
                    result.put(role, result.get(role) + text);
            }
            roleUserSet.add(role + "$$$;;;" + user);
        }

        return result;
    }

    /**
     * 
     * @param cb
     * @return
     * @throws WTException
     */
    public static WorkItem getWorkItem(NmCommandBean cb) throws WTException {
        WorkItem ret = null;

        NmOid oid = cb.getPageOid() == null ? cb.getPrimaryOid() == null ? null : cb.getPrimaryOid() : cb.getPageOid();
        if (oid != null && oid.getRef() instanceof WorkItem) {
            ret = (WorkItem) oid.getRef();
        }
        return ret;
    }

    /**
     * 
     * @param activity
     * @param variable
     * @return
     */
    public static Object getActivityVariableValue(WfActivity activity,
            String variable) {
        try {
            ProcessData pd = activity.getContext();
            return pd.getValue(variable);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据用户名查询用户，返回WTUser，用户名是唯一的
     * 
     * @param userName
     * @return
     * @throws WTException
     */
    public static WTUser getWTUserByName(String userName) throws WTException {
        WTUser user = null;
        QuerySpec qs = new QuerySpec(WTUser.class);
        qs.appendWhere(new SearchCondition(WTUser.class, WTUser.NAME,SearchCondition.EQUAL, userName));
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        while (qr.hasMoreElements()) {
            user = (WTUser) qr.nextElement();
        }
        return user;
    }

    /**
     * 计算pbo中指定角色的人数
     * 
     * @param pbo0
     *            流程主对象
     * @param roleStr
     *            角色中文名或KEY值
     * @return
     * @throws WTException
     */
    public static int countRoleAssignee(Object pbo0, String roleStr) throws WTException {
        int cnt = 0;

        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            TeamManaged pbo = (TeamManaged) pbo0;
            Team team = TeamHelper.service.getTeam(pbo);
            HashMap rpMap = TeamHelper.service.findAllParticipantsByRole(team);
            for (Iterator it = rpMap.keySet().iterator(); it.hasNext();) {
                Role role = (Role) it.next();
                if (role.toString().equals(roleStr)
                        || role.getDisplay(Locale.SIMPLIFIED_CHINESE).equals(roleStr)) {
                    ArrayList assigneeList = (ArrayList) rpMap.get(role);
                    cnt = assigneeList == null ? 0 : assigneeList.size();
                    break;
                }
            }
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }

        return cnt;
    }
    
    public static InputStream getAttachByWfProcess(WfProcess process) throws WTException {
        QueryResult qr = ContentHelper.service.getContentsByRole(process, ContentRoleType.SECONDARY);
        ApplicationData ad = null;
        InputStream is = null;
        while (qr.hasMoreElements()) {
            Object objQr = qr.nextElement();
            if (objQr instanceof ApplicationData) {
                ad = (ApplicationData) objQr;
                String adName = ad.getFileName();
                if (adName.equals("record.properties")) {
                    is = ContentServerHelper.service.findContentStream(ad);
                }
            }
        }
        return is;
    }
    
    public static void saveAttachToWfProcess(WfProcess process,String path, String fileName) throws WTException,
            FileNotFoundException, PropertyVetoException, IOException{
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        //先删除附件
        QueryResult qr = ContentHelper.service.getContentsByRole(process, ContentRoleType.SECONDARY);
        ApplicationData ad = null;
        while (qr.hasMoreElements()) {
            Object objQr = qr.nextElement();
            if (objQr instanceof ApplicationData) {
                ad = (ApplicationData) objQr;
                String adName = ad.getFileName();
                if (adName.equals("record.properties")) {
                    ContentServerHelper.service.deleteContent(process, ad);
                }
            }
        }
        
        //添加新的附件
        String filePath = path+File.separator+fileName;
        ContentHolder holder = (ContentHolder) ContentHelper.service.getContents(process);
        holder = (ContentHolder) PersistenceHelper.manager.refresh(holder);
        ApplicationData data = ApplicationData.newApplicationData(holder);
        data.setRole(ContentRoleType.SECONDARY);
        data.setFileName(fileName);
        data.setUploadedFromPath(fileName);
        data = ContentServerHelper.service.updateContent(holder, data, filePath);
        PersistenceServerHelper.manager.update(data);
        SessionServerHelper.manager.setAccessEnforced(flag);
    }

    /**
     * 设定升级请求所有Target对象的生命周期状态为指定状态
     * 
     * @param pn
     *            流程主对象－－升级请求
     * @param state
     *            生命周期状态中文名或KEY
     * @throws WTException
     */
    /*
     * public static void setTargetsState(Object pn, String stateName) throws WTException {
     * State[] states = State.getStateSet();
     * HashMap mapKey = new HashMap();
     * HashMap mapDsp = new HashMap();
     * for (int i = 0; i < states.length; i++) {
     * mapKey.put(states[i].toString(), states[i]);
     * mapDsp.put(states[i].getDisplay(Locale.SIMPLIFIED_CHINESE), states[i]);
     * }
     * 
     * State state = (State) mapKey.get(stateName);
     * if (state == null)
     * state = (State) mapDsp.get(stateName);
     * if (state == null)
     * throw new WTException("系统未定义的生命周期状态：" + stateName);
     * 
     * QueryResult qr = MaturityHelper.service.getPromotionTargets((PromotionNotice) pn, true);
     * Transaction tx = new Transaction();
     * tx.start();
     * boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
     * 
     * try {
     * // 对设定为已发布状态的操作，将相关对象加入到工艺任务中列出的基线中
     * boolean setReleased = LCUtil.isReleased(state);
     * WTSet objects = new WTHashSet();
     * Set baselines = new HashSet();
     * 
     * // 获取基线列表
     * if (setReleased) {
     * HashMap ibaMap = new HashMap();
     * WTUtil.getIBAValuesLite((PromotionNotice) pn, ibaMap);
     * String baselineNumbers = (String) ibaMap.get("shiyong_jx");
     * String[] number = null;
     * if (baselineNumbers != null && !baselineNumbers.trim().equals(""))
     * number = baselineNumbers.split(",");
     * for (int i = 0; number != null && i < number.length; i++) {
     * number[i] = number[i].trim();
     * if (!number[i].equals("")) {
     * Baseline bl = CAPPUtil.findBaseline(number[i]);
     * if (bl != null)
     * baselines.add(bl);
     * }
     * }
     * }
     * 
     * // 设定提交文档状态，获取基于基线管理的提交文档集合
     * while (qr.hasMoreElements()) {
     * Promotable o = (Promotable) qr.nextElement();
     * if (o instanceof Iterated && !o.isLatestIteration())
     * o = (Promotable) VersionControlHelper.getLatestIteration(o, false);
     * // 不改变已归档对象的状态
     * if (!LCUtil.isReleased(o))
     * LifeCycleHelper.service.setLifeCycleState(o, state);
     * if (!baselines.isEmpty() && o instanceof WTDocument) {
     * String typeName = TypedUtility.getLocalizedTypeName(o, Locale.SIMPLIFIED_CHINESE);
     * if (typeName == null)
     * continue;
     * if (typeName.indexOf("装配工艺") >= 0
     * || typeName.indexOf("零件工艺") >= 0
     * || typeName.indexOf("绕线工艺") >= 0
     * || typeName.indexOf("NC代码") >= 0)
     * objects.add(o);
     * }
     * }
     * 
     * // 对设定为已发布状态的操作，将相关对象加入到指定基线中
     * if (setReleased && !objects.isEmpty()) {
     * for (Iterator it = baselines.iterator(); it.hasNext();) {
     * Baseline bl = (Baseline) it.next();
     * try {
     * CAPPUtil.addDocs2Baseline(objects, bl);
     * }
     * catch (Exception e) {
     * e.printStackTrace();
     * }
     * }
     * }
     * 
     * tx.commit();
     * tx = null;
     * }
     * finally {
     * SessionServerHelper.manager.setAccessEnforced(flag);
     * if (tx != null)
     * tx.rollback();
     * }
     * }
     */

    /**
     * 设定工装对象的生命周期状态。如工装对象为“工装图样文档”，将文档的生命周期状态设定为指定状态；
     * 如果工装对象为“工装设计件”（WTPart），将所有下级零部件中类型为“工装设计件”、状态不是
     * “已完成”、没有关联进程的WTPart设定为指定状态。
     * 
     * @param toolingObj
     *            工装设计对象－工装设计件或工装图样文档
     * @param stateName
     *            生命周期状态名称（中文名或KEY）
     * @throws Exception
     */
    public static void setToolingState(Object toolingObj, String stateName) throws Exception {
        State[] states = State.getStateSet();
        HashMap mapKey = new HashMap();
        HashMap mapDsp = new HashMap();
        for (int i = 0; i < states.length; i++) {
            mapKey.put(states[i].toString(), states[i]);
            mapDsp.put(states[i].getDisplay(Locale.SIMPLIFIED_CHINESE), states[i]);
        }

        State state = (State) mapKey.get(stateName);
        if (state == null)
            state = (State) mapDsp.get(stateName);
        if (state == null)
            throw new WTException("系统未定义的生命周期状态：" + stateName);

        // 支持类型：工装设计件、工装图样文档
        String extTypeId = TypedUtility.getExternalTypeIdentifier(toolingObj);
        if (!(toolingObj instanceof WTPart) && !(toolingObj instanceof WTDocument)
                || extTypeId.indexOf("工装") < 0)
            throw new Exception("不支持的工装设计对象类型："
                    + toolingObj.getClass().getName());

        // 工装图样文档：设定本对象状态
        if (toolingObj instanceof WTDocument) {
            LifeCycleManaged toolingDoc = (LifeCycleManaged) toolingObj;
            LifeCycleHelper.service.setLifeCycleState(toolingDoc, state);
            return;
        }

        // 工装设计件：设定整个子树状态
        WTPart toolingPart = (WTPart) toolingObj;
        long typeId = toolingPart.getTypeDefinitionReference().getKey().getBranchId();
        Transaction tx = new Transaction();
        tx.start();
        try {
            new Object() {
                void setTreeState(WTPart p, State state, long typeId) throws Exception {
                    TypeDefinitionReference tdf = p.getTypeDefinitionReference();
                    long thisTypeId = tdf == null ? 0 : tdf.getKey().getBranchId();
                    if (tdf == null || thisTypeId != typeId)
                        return;
                    String currState = p.getLifeCycleState().getDisplay(
                            Locale.SIMPLIFIED_CHINESE);
                    if (currState.equals("已归档") || currState.equals("涉密归档") ||
                            currState.equals("已完成"))
                        return;
                    LifeCycleHelper.service.setLifeCycleState(p, state);

                    QueryResult qr = WTPartHelper.service.getUsesWTParts(p,
                            new LatestConfigSpec());
                    while (qr.hasMoreElements()) {
                        Object[] pp = (Object[]) qr.nextElement();
                        if (!(pp[1] instanceof WTPart))
                            continue;
                        WTPart part = (WTPart) pp[1];
                        setTreeState(part, state, typeId);
                    }
                }
            }.setTreeState(toolingPart, state, typeId);
            tx.commit();
            tx = null;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            throw e;
        }
    }

    /**
     * 复制工艺任务的团队成员到任务提交文档团队，仅针对拟制状态和重新工作状态到提交文档
     * 
     * @param cappTask
     *            工艺任务对象
     */
    public static void copyTaskTeam(Object cappTask) throws Exception {
        if (!(cappTask instanceof PromotionNotice))
            return;

        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            PromotionNotice pn = (PromotionNotice) cappTask;
            QueryResult qr = MaturityHelper.service.getPromotionTargets(pn, true);
            if (qr.size() <= 0)
                return;

            Team team = TeamHelper.service.getTeam(pn);
            HashMap rpMap = TeamHelper.service.findAllParticipantsByRole(team);
            HashSet inworkSet = new HashSet();
            inworkSet.add(State.INWORK);
            inworkSet.add(State.toState("REWORK"));
            inworkSet.add(State.toState("BOHUI"));
            while (qr.hasMoreElements()) {
                Object obj = qr.nextElement();
                // Debug.P(obj);
                if (!(obj instanceof LifeCycleManaged))
                    continue;
                LifeCycleManaged lcm = (LifeCycleManaged) obj;
                if (lcm instanceof Iterated && !((Iterated) lcm).isLatestIteration())
                    lcm = (LifeCycleManaged) VersionControlHelper.service.getLatestIteration(
                            (Iterated) lcm, false);
                // Debug.P(lcm.getLifeCycleState());
                if (!(inworkSet.contains(lcm.getLifeCycleState())))
                    continue;

                // 获取角色列表，取最新版的生命周期模板
                LifeCycleTemplate lct = (LifeCycleTemplate) lcm.getLifeCycleTemplate().getObject();
                if (!lct.isLatestIteration()) {
                    lct = (LifeCycleTemplate) VersionControlHelper.getLatestIteration(
                            lct, false /* includeMarkedForDelete */);
                }
                Vector v = LifeCycleHelper.service.getPhaseTemplates(lct);
                HashSet roleSet = new HashSet();
                for (int i = 0; v != null && i < v.size(); i++) {
                    PhaseTemplate pt = (PhaseTemplate) v.get(i);
                    for (Enumeration en = pt.getRoles(); en.hasMoreElements();)
                        roleSet.add(en.nextElement());
                }
                Vector vRole = new Vector(roleSet);
                // Debug.P(vRole);

                Team objTeam = TeamHelper.service.getTeam(lcm);
                HashMap objRPMap = TeamHelper.service.findAllParticipantsByRole(objTeam);
                for (Iterator it = vRole.iterator(); it.hasNext();) {
                    Role role = (Role) it.next();
                    // Debug.P(role);
                    ArrayList pRefList = (ArrayList) rpMap.get(role);
                    if (pRefList == null)
                        continue;
                    ArrayList rpList = (ArrayList) objRPMap.get(role);
                    for (int i = 0; rpList != null && i < rpList.size(); i++) {
                        WTPrincipalReference pRef = (WTPrincipalReference) rpList.get(i);
                        TeamHelper.service.deleteRolePrincipalMap(role, pRef.getPrincipal(), objTeam);
                    }
                    TeamHelper.service.deleteRole(role, objTeam);
                    for (int i = 0; i < pRefList.size(); i++) {
                        WTPrincipalReference pRef = (WTPrincipalReference) pRefList.get(i);
                        TeamHelper.service.addRolePrincipalMap(role, pRef.getPrincipal(), objTeam);
                        // Debug.P("==> ", pRef.getFullName());
                    }
                }
                objTeam = (Team) PersistenceHelper.manager.refresh(objTeam);
                TeamReference tRef = TeamReference.newTeamReference(objTeam);
                // 本行代码使生命周期权限生效
                TeamHelper.service.augmentRoles(lcm, tRef);
            }
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
    }

    /**
     * 检查WTPart是否有工艺部计划员角色，如没有则从产品团队复制角色成员
     * 
     * @param part
     *            检查的目标part
     * @throws Exception
     */
    private static void setCAPPNotifyRole(WTPart part) throws Exception {
        WTRoleHolder2 team = TeamHelper.service.getTeam(part);
        HashMap rpMap = TeamHelper.service.findAllParticipantsByRole(team);
        Role role = Role.toRole("GONGYIBUJIHUAYUAN");
        ArrayList prefList = (ArrayList) rpMap.get(role);
        if (prefList != null && prefList.size() > 0)
            return;

        // 复制团队的工艺部计划员角色
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            ContainerTeamManaged cont = (ContainerTeamManaged) part.getContainer();
            WTRoleHolder2 team2 = ContainerTeamHelper.service.getContainerTeam(cont);
            HashMap rpMap2 = TeamHelper.service.findAllParticipantsByRole(team2);
            ArrayList list2 = (ArrayList) rpMap2.get(role);
            if (list2 == null || list2.size() <= 0)
                return;

            for (int i = 0; i < list2.size(); i++) {
                WTPrincipalReference pref = (WTPrincipalReference) list2.get(i);
                TeamHelper.service.addRolePrincipalMap(role, pref.getPrincipal(), team);
            }
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
    }

    /**
     * 用户意见对象
     * 
     * Created on 2005-10-4
     * 
     * @author liuld
     */
    public static class UserComment {
        public String actName;
        public WTPrincipalReference userRef;
        public Role role;
        public String route;
        public String comments;
        public Timestamp timestamp;

        public UserComment repair() {
            if (route == null || route.trim().length() == 0)
                route = "&nbsp;";
            if (comments == null || comments.trim().length() == 0)
                comments = "&nbsp;";
            return this;
        }

        public String mergeComment() {
            String mc = "";
            if (route != null && route.trim().length() > 0 && !route.equals("&nbsp;"))
                mc += route;
            if (comments != null && comments.trim().length() > 0 && !comments.equals("&nbsp;")) {
                if (mc.trim().length() > 0)
                    mc += ",";
                mc += comments;
            }

            return mc;
        }
    }
}

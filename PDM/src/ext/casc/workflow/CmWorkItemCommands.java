package ext.casc.workflow;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.model.NmException;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmURL;
import com.ptc.netmarkets.work.NmWorkItemCommands;
import com.ptc.netmarkets.work.StandardNmWorkItemService;
import ext.ases.envelope.ProcessEnvelope;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.*;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.ownership.OwnershipHelper;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.team.TeamReference;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;
import wt.workflow.definer.WfAssignedActivityTemplate;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfVariable;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class CmWorkItemCommands extends StandardNmWorkItemService implements RemoteAccess {

    private static final long serialVersionUID = 5175182826136558245L;

    public static FormResult complete(NmCommandBean cb) throws WTException {
        System.out.println("====static=====complete===========");
        preComplete(cb);
        new CmWorkItemCommands().complete(cb, getParams(cb));
        return postComplete(cb);
    }

    public void complete(NmCommandBean cb, HashMap params) throws WTException {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "complete";
            Class[] argTypes = { NmCommandBean.class, HashMap.class };
            Object[] argValues = { cb, params };
            try {
                RemoteMethodServer.getDefault().invoke(method, null, this, argTypes, argValues);
            } catch (Exception e) {
                if (e instanceof WTException){
                    throw (WTException) e;
                } else {
                    throw new WTException(e);
                }
            }
            return;
        }

        List docList = new ArrayList();

        WorkItem workItem = getWorkItem(cb);
        Persistable pbo = getLcmObject(workItem);
        WfActivity wfAct = getWfActivity(workItem);

        WfAssignedActivityTemplate wfActTemp = getWfAssignedActivity(wfAct);

        if (notWorkItemOwner(workItem)){
            throw new NmException("ext.casc.workflow.workflowResource", "workflow.workitem.notSameAssignees", null);
        }
        if (isCompleted(workItem)){
            throw new NmException("com.ptc.netmarkets.work.workResource", "90", null);
        }
        // 如果当前的对象为ChangeNotice则还需要判断里面关联文档处于检出状态则，报错
        if (pbo instanceof WTChangeRequest2) {
            QueryResult qr = ChangeHelper2.service.getChangeables((WTChangeRequest2) pbo);
            boolean checkedOut = false;
            while (qr.hasMoreElements()) {
                Object o = qr.nextElement();
                if (o instanceof WTDocument) {
                    docList.add((WTDocument) o);
                } else if (o instanceof EPMDocument) {
                    docList.add((EPMDocument) o);
                } else if (o instanceof WTPart) {
                    docList.add((WTPart) o);
                }else if (o instanceof ProcessEnvelope) {
                    ProcessEnvelope processEnvelope = (ProcessEnvelope)o;
                    docList.add(processEnvelope);
                }
                if (o instanceof Workable && WorkInProgressHelper.isCheckedOut((Workable) o)) {
                    checkedOut = true;
                    break;
                }
            }
            if (checkedOut) {
                throw new NmException("ext.casc.workflow.workflowResource", "workflow.workitem.docIsCheckout", null);
            }
        }
        if (pbo instanceof WTDocument) {
            docList.add((WTDocument) pbo);

            if(wfAct.getName().contains("编制")||wfAct.getName().contains("修改")||wfAct.getName().contains("提交签审")){
            	 if (pbo instanceof Workable && WorkInProgressHelper.isCheckedOut((Workable) pbo)) {
                     throw new NmException("ext.casc.workflow.workflowResource", "workflow.workitem.docIsCheckout", null);
                 }
            }

        } else if (pbo instanceof WTPart) {
            docList.add((WTPart) pbo);
        } else if (pbo instanceof EPMDocument) {
            docList.add((EPMDocument) pbo);
        }else if (pbo instanceof ProcessEnvelope) {
            ProcessEnvelope processEnvelope = (ProcessEnvelope)pbo;
            docList.add(processEnvelope);
        }

        saveTeamRoles(cb, params, workItem);

        Transaction tx = new Transaction();
        tx.start();
        try {
            processCompleteAction(workItem, pbo, wfActTemp, wfAct, cb, params);
            tx.commit();
            tx = null;
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
            throw new WTException(e);
        } finally {
            if (tx != null)
                tx.rollback();
        }

    }

    protected static void preComplete(NmCommandBean nmcommandbean) throws WTException {
        Vector vector = null;
        String s = null;
        Enumeration enumeration = nmcommandbean.getRequest().getParameterNames();
        boolean flag = false;
        do
        {
            if(!enumeration.hasMoreElements()){
                break;
            }
            String s1 = (String)enumeration.nextElement();
            String s2 = NmCommandBean.convert(s1);
            if(s2.indexOf("WfUserEvent") >= 0 && s2.lastIndexOf("old") == -1) {
                String s3 = null;
                if(s2.indexOf("WfRouterCheck") >= 0){
                    s3 = s2.substring(s2.indexOf("WfRouterCheck") + "WfRouterCheck".length(), s2.lastIndexOf("___"));
                } else {
                    s3 = nmcommandbean.getTextParameter(s1);
                }
                if(vector == null){
                    vector = new Vector();
                }
                vector.addElement(s3);
            } else if(s2.indexOf("CustActVar") >= 0 && !s2.endsWith("old")) {
                int i = s2.indexOf("CustActVar") + "CustActVar".length();
                int j = s2.lastIndexOf("CustActVar");
                nmcommandbean.getMap().put(s2.substring(i, j), nmcommandbean.getTextParameter(s1));
            } else if(s2.indexOf("voteAction") >= 0 && s2.lastIndexOf("old") == -1){
                nmcommandbean.getMap().put("voteAction", nmcommandbean.getTextParameter(s1));
            } else if(s2.indexOf("signatureEngine_password") >= 0 && !s2.endsWith("old")){
                nmcommandbean.getMap().put("signatureEngine_password", nmcommandbean.getTextParameter(s1));
            } else if(s2.indexOf("signatureEngine_username") >= 0 && !s2.endsWith("old"))
                nmcommandbean.getMap().put("signatureEngine_username", nmcommandbean.getTextParameter(s1));
            else
            if(s2.indexOf("___comments___") >= 0 && !s2.endsWith("___old")){
                s = nmcommandbean.getTextParameter(s1);
            } else if(s2.indexOf("___automateFastTrack___automateFastTrack") >= 0) {
                HashMap hashmap = nmcommandbean.getChecked();
                if(hashmap != null) {
                    Object obj = hashmap.get("automateFastTrack");
                    if(obj != null && (obj instanceof ArrayList)) {
                        ArrayList arraylist = (ArrayList)obj;
                        String s5 = (String)arraylist.get(0);
                        if(s5 != null && s5.equals("automateFastTrack")){
                            flag = true;
                        }
                    }
                }
            }
        } while(true);
        if(s == null || s != null && s.trim().length() == 0){
            s = " ";
        }
        nmcommandbean.getMap().put("automateFastTrack", String.valueOf(flag));
        nmcommandbean.getMap().put("WfUserEventList", vector);
        nmcommandbean.getMap().put("comments", s);
        HttpServletRequest httpservletrequest = nmcommandbean.getRequest();
        HttpSession httpsession = nmcommandbean.getRequest().getSession();
        HashMap hashmap1 = (HashMap)httpsession.getAttribute("CHECKED_USERS");
        if(hashmap1 != null) {
            Boolean boolean1 = (Boolean)httpsession.getAttribute("isPaged");
            if(boolean1 != null && !boolean1.booleanValue()){
                nmcommandbean.getMap().put("isPaged", "false");
            }
            HashMap hashmap2 = nmcommandbean.getOldChecked();
            HashMap hashmap3 = nmcommandbean.getChecked();
            for(Iterator iterator = hashmap2.keySet().iterator(); iterator.hasNext();) {
                Object obj1 = iterator.next();
                if(!hashmap3.containsKey(obj1)) {
                    hashmap1.remove(obj1);
                } else {
                    hashmap2.put(obj1, hashmap3.get(obj1));
                    hashmap1.put(obj1, hashmap2.get(obj1));
                }
            }

            HashMap hashmap4 = nmcommandbean.getUnChecked();
            Object obj2;
            for(Iterator iterator1 = hashmap4.keySet().iterator(); iterator1.hasNext(); hashmap1.remove(obj2)){
                obj2 = iterator1.next();
            }
            hashmap1.remove("ROLE_MAP");
            hashmap3.putAll(hashmap1);
            nmcommandbean.setChecked(hashmap3);
        }
    }


    protected static FormResult postComplete(NmCommandBean nmcommandbean) throws WTException {
        NmURL nmurl = new NmURL();
        nmurl.setType("work");
        nmurl.setAction("list");
        nmurl.setOid(null);
        try
        {
            HttpSession httpsession = nmcommandbean.getRequest().getSession();
            String s4 = (String)nmcommandbean.getSessionBean().getStorage().get("tk");
            if(s4 == null || s4 != null && !s4.equals("null")) {
                s4 = httpsession.getAttribute("tk").toString();
                httpsession.removeAttribute("tk");
            }
            if(s4.equals("prj")) {
                nmurl.setAction("listProjectAssignments");
                nmurl.setOid(new NmOid("project", (ObjectIdentifier)nmcommandbean.getContainerRef().getKey()));
            } else if(s4.equals("prd")) {
                nmurl.setAction("listProductAssignments");
                nmurl.setOid(new NmOid("object", (ObjectIdentifier)nmcommandbean.getContainerRef().getKey()));
            } else if(s4.equals("lib")) {
                nmurl.setAction("listLibraryAssignments");
                nmurl.setOid(new NmOid("object", (ObjectIdentifier)nmcommandbean.getContainerRef().getKey()));
            } else if(s4.equals("pln")) {
                if(nmcommandbean.getContainer().getConceptualClassname().equalsIgnoreCase("wt.projmgmt.admin.Project2")) {
                    nmurl.setType("project");
                    nmurl.setAction("view_plan");
                    nmurl.setOid(new NmOid("project", (ObjectIdentifier)nmcommandbean.getContainerRef().getKey()));
                }
            } else if(s4.equals("dtl")) {
                nmurl.setType("object");
                nmurl.setAction("view");
                String s7 = (String)nmcommandbean.getSessionBean().getStorage().get("task-type");
                if(s7 == null || s7 != null && !s7.equals("null")) {
                    s7 = httpsession.getAttribute("task-type").toString();
                    httpsession.removeAttribute("task-type");
                }
                String s8 = (String)nmcommandbean.getSessionBean().getStorage().get("task-oid");
                if(s8 == null || s8 != null && !s8.equals("null")) {
                    s8 = httpsession.getAttribute("task-oid").toString();
                    httpsession.removeAttribute("task-oid");
                }
                if((nmcommandbean.getActionOid().getRef() instanceof WorkItem) && s7.equals("workflow")) {
                    WorkItem workitem = (WorkItem)nmcommandbean.getActionOid().getRef();
                    if(workitem != null && workitem.getPrimaryBusinessObject() != null){
                        nmurl.setOid(new NmOid(workitem.getPrimaryBusinessObject().getObject()));
                    } else {
                        nmurl.setOid(new NmOid(s8));
                    }
                } else {
                    nmurl.setOid(new NmOid(s8));
                }
            } else if(s4.equals("orv")) {
                nmurl.setType("netmarkets");
                nmurl.setAction("view");
                nmurl.setOid(null);
            } else if(s4.equals("viewDocument")) {
                String s6 = (String)nmcommandbean.getSessionBean().getStorage().get("task-oid");
                if(s6 == null || s6 != null && !s6.equals("null")) {
                    s6 = httpsession.getAttribute("task-oid").toString();
                    httpsession.removeAttribute("task-oid");
                }
                ConcurrentHashMap concurrenthashmap = nmcommandbean.getSessionBean().getStorage();
                nmurl.setAction("view");
                nmurl.setType("object");
                nmurl.setOid(new NmOid(s6));
            }
        } catch(NullPointerException nullpointerexception) {
            nmurl.setType("netmarkets");
            nmurl.setAction("view");
            nmurl.setOid(null);
        }
        FormResult formresult = new FormResult(FormProcessingStatus.SUCCESS);
        formresult.setNextAction(FormResultAction.FORWARD);
        formresult.setForcedUrl(nmurl.toString2(nmcommandbean.getUrlFactoryBean()));
        //nmcommandbean.setRedirectURL(nmurl);


        WorkItem workItem = WfUtil.getWorkItem(nmcommandbean);
        WfActivity wfAct = (WfActivity) workItem.getSource().getObject();
        Object pbo = wfAct.getContext().getValue("primaryBusinessObject");
        WfAssignedActivityTemplate template = (WfAssignedActivityTemplate)wfAct.getTemplate().getObject();
        Enumeration enumeration = template.getRoles();
        Role role = Role.toRole("");
        if(enumeration.hasMoreElements())  role = (Role)enumeration.nextElement();

        Object obj = WfUtil.getActivityVariableValue(wfAct, "setRelatedObjectSignature");
        boolean flag = false;
        if(obj instanceof Boolean){
            flag = (Boolean)obj;
        }

        String userName = null;
        String comments = "";
        String vote = null;
        WTUser user = null;
        Enumeration parameterNames = nmcommandbean.getRequest().getParameterNames ();
        while (parameterNames.hasMoreElements ()) { // loop through all the user's form fields
            String plainKey = (String) parameterNames.nextElement();
            String key = NmCommandBean.convert(plainKey);
            if(key.indexOf("WF_CURRENT_USER")>=0){
                userName = nmcommandbean.getTextParameter (plainKey);
            }
            if(key.indexOf("___comments___") >= 0 && !key.endsWith("___old")){
                comments = nmcommandbean.getTextParameter(plainKey);
            }

            if (key.indexOf("WfUserEvent") >= 0 && key.lastIndexOf("old") == -1) {
                String eventValue = null;
//              System.out.println("has router event                               "+plainKey);
                if (key.indexOf("WfRouterCheck") >= 0) {
//                  System.out.println("router :::::::::::::::::::"+key+   "       "+cb.getTextParameter(plainKey));
                    eventValue = key.substring(key.indexOf("WfRouterCheck") + NmWorkItemCommands.ROUTER_CHECK.length(), key.lastIndexOf("___"));
                } else {
                    vote = nmcommandbean.getTextParameter(plainKey);
                }
            }
        }

        if(userName!=null){
            user = WfUtil.getWTUserByName(userName);
        }

//      if(printCtrl){
//          System.out.println();
//          System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ View Begin @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//          System.out.println("user "+user);
//          System.out.println("pbo "+pbo);
//          System.out.println("comments "+comments);
//          System.out.println("wfAct.getName() "+wfAct.getName());
//          System.out.println("vote "+vote);
//          System.out.println("role "+role);
//          System.out.println("role.toString() "+role.toString());
//          System.out.println("role.getDisplay() "+role.getDisplay());
//
//          System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ View end @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//          System.out.println();
//
//      }


//      System.out.println("SIGNATURE = " + flag + ":" + user + ":" + pbo + ":" + wfAct + ":" + role);
        if( flag && user!=null && pbo!=null && wfAct!=null && role!=null ){
            ResultExpression.setElectronicSignature(user, (WTObject)pbo, comments, wfAct.getName(), role.getDisplay(), vote);
        }


        return formresult;
    }

    protected boolean isCompleted(WorkItem workItem) {
        if (workItem == null || workItem.isComplete()){
            return true;
        }
        return false;
    }

    protected boolean notWorkItemOwner(WorkItem workItem) {
        boolean isOwner = false;
        try {
            WTPrincipal currentUser = SessionHelper.manager.getPrincipal();
            isOwner = OwnershipHelper.isOwnedBy(workItem, currentUser);
            if (!isOwner) {
                WTPrincipal owner = OwnershipHelper.getOwner(workItem);
                if (owner instanceof WTGroup) {
                    WTGroup wtgroup = (WTGroup) owner;
                    if (wtgroup.isMember(currentUser))
                        isOwner = true;
                }
            }
        } catch (WTException e) {
        }
        return !isOwner;
    }

    protected boolean isPBOCheckout(WorkItem workItem) {
        try {
            WfActivity wfactivity = (WfActivity) workItem.getSource().getObject();
            Object pbo = null;
            WTReference ref = wfactivity.getParentProcess().getBusinessObjectReference(new ReferenceFactory());
            if (ref == null) {
                System.out.println("获取pbo失败");
                return false;
            } else pbo = ref.getObject();

            boolean checkedOut = false;
            if (pbo instanceof Workable) {
                checkedOut = WorkInProgressHelper.isCheckedOut((Workable) pbo);
            } else if (pbo instanceof WTChangeRequest2) {
                WTChangeRequest2 ecr = (WTChangeRequest2) pbo;
                QueryResult qr = ChangeHelper2.service.getChangeables(ecr);
                while (qr.hasMoreElements()) {
                    Object o = qr.nextElement();
                    if (o instanceof Workable && WorkInProgressHelper.isCheckedOut((Workable) o)) {
                        checkedOut = true;
                        break;
                    }
                }
            }

            return checkedOut;
        } catch (WTException wte) {
            wte.printStackTrace();
        }
        return false;
    }

    protected boolean saveTeamRoles(NmCommandBean cb, HashMap params, WorkItem workItem) throws WTException {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        // 辨认是否是设定参与者
        if(cb.getTextParameter(CmWfTaskProcessorCommands.KEY_WF_AUGMENT_ROLES_FORM) == null) {
            return true;
        }
        // 获取PBO对象
        WfActivity wa = (WfActivity) workItem.getSource().getObject();
        String waname = wa.getName();
        WfProcess process = wa.getParentProcess();
        System.out.println("-----wa:" + waname);
        if("设置分发部门和份数".equals(waname) && process.getName().startsWith("149")) {
            String roleKey = "角色.审核者";
            String[] participants = (String[]) cb.getMap().get(roleKey);
            Team processteam = (Team) process.getTeamId().getObject();
            Role role = Role.toRole("SHENHEZHE");
            ReferenceFactory rf = new ReferenceFactory();
            for(int j = 0; participants != null && j < participants.length; j++) {
                String uoid = participants[j].trim();
                try {
                    if(uoid.length() <= 0) {
                        continue;
                    }
                    WTPrincipal p = (WTPrincipal) rf.getReference(uoid).getObject();
                    System.out.println(roleKey + "=" + p.getName());
                    TeamHelper.service.addRolePrincipalMap(role, p, processteam);
                    processteam = (Team) PersistenceHelper.manager.refresh(processteam);
                    processteam = (Team) PersistenceHelper.manager.save(processteam);
                } catch(Exception ex) {
                    // 跳过异常的uoid
                    ex.printStackTrace();
                }
            }
            return true;
        }
        ProcessData vars = wa.getContext();
        Object obj = vars.getValue("primaryBusinessObject");
        if(!(obj instanceof TeamManaged)) {
            return true;
        }
        try {
            // 取所有角色
            TeamManaged pbo = (TeamManaged) obj;
            Vector<Team> teamVec = new Vector<Team>();
            Vector<Team> pboteamVec = new Vector<Team>();
            HashMap rolePrincipalListMap = new HashMap();
            TeamReference reference = pbo.getTeamId();
            Team team = null;
//            if (reference != null) {
//                team = (Team) reference.getObject();
//                rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
//                pboteamVec.add(team);
//            }
            //else {
            Team processteam = (Team) process.getTeamId().getObject();
            HashMap teaMap = TeamHelper.service.findAllParticipantsByRole(processteam);
            System.out.println(">>>>>>>>>>>teaMap:" + teaMap);
            rolePrincipalListMap.putAll(teaMap);
            teamVec.add(processteam);
//            }
            System.out.println(">>>>>>>>>>>rolePrincipalListMap:" + rolePrincipalListMap);

            // 获取团队角色的中英文对照表
            HashMap rolesValue = new HashMap();
            HashMap rolesDisp = new HashMap();
            if(team != null) {
                Vector allRoles = TeamHelper.service.findRoles(team);
                for(int i = 0; allRoles != null && i < allRoles.size(); i++) {
                    Role role = (Role) allRoles.get(i);
                    rolesValue.put(role.toString(), role);
                    rolesDisp.put(role.getDisplay(Locale.SIMPLIFIED_CHINESE), role);
                }
            }
            HashMap rolesValueAll = new HashMap();
            HashMap rolesDispAll = new HashMap();
            Role[] roleArray = Role.getRoleSet();
            for(int i = 0; i < roleArray.length; i++) {
                Role role = roleArray[i];
                rolesValueAll.put(role.toString(), role);
                rolesDispAll.put(role.getDisplay(Locale.SIMPLIFIED_CHINESE), role);
            }

            HashMap permissionMap = WorkflowHelper.service.getPermissionMap(workItem);
            Collection col = permissionMap == null ? new Vector() : permissionMap.keySet();
            // CmWfTaskProcessorCommands.initWfActivity(cb, "prepareRoles");

            // 取要设定的角色清单
            Vector roles = new Vector();
            WfVariable varRolesDefined = vars.getVariable("rolesDefined");
            if(varRolesDefined != null) {
                String roleDefList = (String) varRolesDefined.getValue();
                String[] roleKey = roleDefList.split(",");
                for(int i = 0; i < roleKey.length; i++) {
                    String roleStr = roleKey[i].trim();
                    if(roleStr.length() > 0) {
                        Role role = (Role) rolesDisp.get(roleStr); // 是否角色中文名
                        if(role == null) {
                            role = (Role) rolesValue.get(roleStr); // 是否角色英文KEY
                        }
                        if(role == null) {
                            role = (Role) rolesDispAll.get(roleStr); // 是否不在已定义团队内的角色中文名
                        }
                        if(role == null) {
                            role = (Role) rolesValueAll.get(roleStr); // 是否不在已定义团队内的角色KEY
                        }
                        if(role != null && !roles.contains(role) && col.contains(role)) {
                            roles.add(role);
                        }
                    }
                }
            } else {
                roles.addAll(rolePrincipalListMap.keySet());
            }
            System.out.println(">>>>>>>>>>>roles:" + roles);
            // 逐个角色设定参与者
            ReferenceFactory rf = new ReferenceFactory();

            for(int i = 0; i < roles.size(); i++) {
                // 取新设定的参与者清单
                Role role = (Role) roles.get(i);
                String roleKey = "角色." + role.getDisplay(cb.getLocale());
                String[] participants = (String[]) cb.getMap().get(roleKey);

                // 获取新参与者的reference
                ArrayList newList = new ArrayList();
                for(int j = 0; participants != null && j < participants.length; j++) {
                    String uoid = participants[j].trim();
                    try {
                        if(uoid.length() <= 0) {
                            continue;
                        }
                        WTPrincipal p = (WTPrincipal) rf.getReference(uoid).getObject();
                        System.out.println(roleKey + "=" + p.getName());
                        newList.add(WTPrincipalReference.newWTPrincipalReference(p));
                    } catch(Exception ex) {
                        // 跳过异常的uoid
                        ex.printStackTrace();
                    }
                }

                // 删除去掉的参与者
                ArrayList oldList = (ArrayList) rolePrincipalListMap.get(role);
                if("修改".equals(waname)) {
                    System.out.println("--------删除去掉的用户!!");
                    for(int j = 0; oldList != null && j < oldList.size(); j++) {
                        WTPrincipalReference pRef = (WTPrincipalReference) oldList.get(j);
                        if(!newList.contains(pRef)) {
                            for(Team t : teamVec) {
                                TeamHelper.service.deleteRolePrincipalMap(role, pRef.getPrincipal(), t);
                            }
                        }
                    }
                }
                for(int j = 0; oldList != null && j < oldList.size(); j++) {
                    WTPrincipalReference pRef = (WTPrincipalReference) oldList.get(j);
                    if(!newList.contains(pRef)) {
                        for(Team t : teamVec) {
                            TeamHelper.service.deleteRolePrincipalMap(role, pRef.getPrincipal(), t);
                        }
                    }
                }

                // 添加新增的参与者
                for(int j = 0; newList != null && j < newList.size(); j++) {
                    WTPrincipalReference pRef = (WTPrincipalReference) newList.get(j);
                    if(oldList == null || !oldList.contains(pRef)) {
                        for(Team t : teamVec) {
                            TeamHelper.service.addRolePrincipalMap(role, pRef.getPrincipal(), t);
                        }
                    }
                }
            }

            for(Team t : teamVec) {
                t = (Team) PersistenceHelper.manager.refresh(t);
                t = (Team) PersistenceHelper.manager.save(t);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }

        return true;
    }

    private static HashMap getParams(NmCommandBean cb) {
        HashMap hashmap = new HashMap(12);
        HttpServletRequest httpservletrequest = cb.getRequest();
        String name;
        for (Enumeration enumeration = httpservletrequest.getParameterNames(); enumeration.hasMoreElements(); addParam(
                hashmap, httpservletrequest,
                name, cb)) {name = (String) enumeration.nextElement();
        }
        cb.getMap().putAll(httpservletrequest.getParameterMap());
        return hashmap;
    }

    private static void addParam(HashMap hashmap, HttpServletRequest httpservletrequest, String name, NmCommandBean cb) {
        String value = cb.getTextParameter(name);
        if (value == null){
            value = "";
        }
        hashmap.put(name, value);
    }

    @SuppressWarnings("deprecation")
    private WorkItem getWorkItem(NmCommandBean cb) throws WTException {
        return (WorkItem) cb.getPageOid().getRef();
    }
}

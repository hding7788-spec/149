package ext.casc.workflow.setparticipant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.session.SessionHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignment;
import wt.workflow.work.WfBallot;
import wt.workflow.work.WorkItem;

import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.TypeIdentifierHelper;

import ext.ases.envelope.EnvelopeHelper;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.change.ChangeHelper;
import ext.casc.util.CSCPrincipal;
import ext.casc.util.IBAUtility;
import ext.casc.workflow.CSCWorkflowException;
import ext.casc.workflow.util.ExcelUtility;
import ext.casc.workflow.util.PropertiesUtil;
import ext.casc.workflow.util.WorkflowConfigBean;
import ext.casc.workflow.util.WorkflowConfigBeanFactory;

public class WorkflowUtil {
    public static final String WORKFLOW_CONFIG_KEY = "workflow_config_file";

    public static final String IS_NEED_SEARCH_BUTTON = "isNeedSearchButton";

    public static PropertiesUtil setparticipant_properties = null;

    public static String wtHome = null;

    static {
        try {
            wtHome = WTProperties.getLocalProperties().getProperty("wt.home");
            setparticipant_properties = new PropertiesUtil(File.separator + "codebase" + File.separator + "ext"
                    + File.separator + "ases" + File.separator + "workflow" + File.separator + "setparticipant"
                    + File.separator + "setparticipant_properties.properties");
        } catch (IOException e) {
            CSCWorkflowException e1 = new CSCWorkflowException("读取系统wtproperties失败!", e);
            e1.printMessage();
        } catch (CSCWorkflowException e) {
            e.printMessage();
        }
    }

    public static void autoSetPrincipal(String roleKey, Object pbo, Object self, Locale locale) {
        Role targetRole = Role.toRole(roleKey);
        try {
            Map<Role, List<WTUser>> roleAndUserMap = PrincipalHelper.service.getRoleAndUserByContainer(pbo);
            for (Role role : roleAndUserMap.keySet()) {
                if (targetRole.equals(role)) {
                    List<WTUser> tempUserList = roleAndUserMap.get(role);
                    PrincipalHelper.service.saveTeamRole(targetRole.toString(), tempUserList, self, locale);
                    break;
                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        }
    }

    public static void setCreatorToProcessTeamRole(String roleKey, ObjectReference self) {
        Role role = Role.toRole(roleKey);
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        try {
            WfProcess process = (WfProcess) self.getObject();
            Team team = (Team) process.getTeamId().getObject();
            List<WTUser> userList = new ArrayList<WTUser>();
            userList.add((WTUser) process.getCreator().getObject());
            HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
            List tempUserList = (List) rolePrincipalListMap.get(role);
            for (int i = 0; tempUserList != null && i < tempUserList.size(); i++) {
                WTUser user = (WTUser) ((WTPrincipalReference) tempUserList.get(i)).getObject();
                team.deletePrincipalTarget(role, user);
            }
            for (int i = 0; i < userList.size(); i++) {
                WTUser user = userList.get(i);
                team.addPrincipal(role, user);
            }
            team = (Team) PersistenceHelper.manager.refresh(team);
            team = (Team) PersistenceHelper.manager.save(team);
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }

    /**将业务对象所在容器团队名称为roleName的角色人员添加到流程对应角色中
     * @param roleKey 角色名称(key值)
     * @param pbo
     * @param self
     */
    public static void setContainerTeamRoleToProcessTeamRole(String fromRoleName, String toRoleName,Object pbo, ObjectReference self) {
        Role fromRole = Role.toRole(fromRoleName);
        Role targetRole = Role.toRole(toRoleName);
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        try {
            Map<Role, List<WTUser>> roleAndUserMap = PrincipalHelper.service.getRoleAndUserByContainer(pbo);
            for (Role role : roleAndUserMap.keySet()) {
                if (fromRole.equals(role)) {
                    List<WTUser> userList = roleAndUserMap.get(role);
                    WfProcess process = (WfProcess) self.getObject();
                    Team team = (Team) process.getTeamId().getObject();
                    HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
                    List tempUserList = (List) rolePrincipalListMap.get(targetRole);
                    for (int i = 0; tempUserList != null && i < tempUserList.size(); i++) {
                        WTUser user = (WTUser) ((WTPrincipalReference) tempUserList.get(i)).getObject();
                        team.deletePrincipalTarget(targetRole, user);
                    }
                    for (int i = 0; i < userList.size(); i++) {
                        WTUser user = userList.get(i);
                        team.addPrincipal(targetRole, user);
                    }
                    team = (Team) PersistenceHelper.manager.refresh(team);
                    team = (Team) PersistenceHelper.manager.save(team);
                    break;
                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }

    /**将业务对象所在容器团队名称为roleName的角色人员添加到流程对应角色中
     * @param roleKey 角色名称(key值)
     * @param pbo
     * @param self
     */
    public static void setContainerTeamRoleToProcessTeamRole(String roleName, Object pbo, ObjectReference self) {
        Role targetRole = Role.toRole(roleName);
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        try {
            Map<Role, List<WTUser>> roleAndUserMap = PrincipalHelper.service.getRoleAndUserByContainer(pbo);
            for (Role role : roleAndUserMap.keySet()) {
                if (targetRole.equals(role)) {
                    List<WTUser> userList = roleAndUserMap.get(role);
                    WfProcess process = (WfProcess) self.getObject();
                    Team team = (Team) process.getTeamId().getObject();
                    HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
                    List tempUserList = (List) rolePrincipalListMap.get(role);
                    for (int i = 0; tempUserList != null && i < tempUserList.size(); i++) {
                        WTUser user = (WTUser) ((WTPrincipalReference) tempUserList.get(i)).getObject();
                        team.deletePrincipalTarget(role, user);
                    }
                    for (int i = 0; i < userList.size(); i++) {
                        WTUser user = userList.get(i);
                        team.addPrincipal(role, user);
                    }
                    team = (Team) PersistenceHelper.manager.refresh(team);
                    team = (Team) PersistenceHelper.manager.save(team);
                    break;
                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }

    public static Map<String, Vector<WTUser>> autoSetPrincipalByGroup(String oid, Map<String, String> roleAndGroupMap,
            Locale locale) throws WTException {
        Map<String, Vector<WTUser>> groupAnduserMap = new HashMap<String, Vector<WTUser>>();
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
        for (String tempRoleKey : roleAndGroupMap.keySet()) {
            Role targetRole = Role.toRole(tempRoleKey);
            String tempGroupName = roleAndGroupMap.get(tempRoleKey);
            WTGroup tempGroup = CSCPrincipal.getGroupByName(tempGroupName);
            Vector<WTUser> userList = new Vector<WTUser>();
            userList = CSCPrincipal.getGroupMemberUsers(tempGroup, userList);
            PrincipalHelper.service.saveTeamRole(targetRole.toString(), userList, wi, locale);
            groupAnduserMap.put(tempGroupName, userList);
        }
        return groupAnduserMap;
    }

    public static WorkflowConfigBean getDefaultPath(String oid) {
        WorkflowConfigBean wcb = WorkflowConfigBeanFactory.getWorkflowConfigBeanInstance();
        wcb.setConfigBean(false);
        wcb.setDefaultBean(oid);
        return wcb;
    }

    public static WorkflowConfigBean getConfigPath(List<String> identifyList) throws CSCWorkflowException {
        WorkflowConfigBean mergedBean = null;
        String configFile = wtHome + File.separator
                + setparticipant_properties.getValue(WORKFLOW_CONFIG_KEY).replace("\\", File.separator);
        FileInputStream fis = null;
        List<WorkflowConfigBean> data = null;
        try {
            fis = new FileInputStream(configFile);
            if (fis != null) {
                data = ExcelUtility.readExcel(fis);
            }
        } catch (FileNotFoundException e) {
            throw new CSCWorkflowException("工作流配置文件未找到，就查看配置文件是否存在或文件地址是否正确！", e);
        }

        if (data != null) {
            for (int i = 0; i < identifyList.size(); i++) {
                String identify = identifyList.get(i);
                for (int j = 0; j < data.size(); j++) {
                    WorkflowConfigBean wcb = data.get(j);
                    String tempIdentify = wcb.getIdentifyStr();
                    if (tempIdentify.contains(identify)) {
                        mergedBean = wcb.mergeBean(mergedBean);
                        break;
                    }
                }
            }
        }

        return mergedBean;
    }

    public static String isNeedSearchUserButton() {
        if (setparticipant_properties == null) {
            return "true";
        }
        return setparticipant_properties.getValue(IS_NEED_SEARCH_BUTTON);
    }

    public static List<String> getIdentifyList(String oid) throws WTRuntimeException, WTException {
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
        WfActivity wfAct = (WfActivity) wi.getSource().getObject();
        Persistable obj = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
        List<String> identifyList = new ArrayList<String>();
        if (obj instanceof WTDocument) {
            WTDocument wtd = (WTDocument) obj;
            String wtdtype = wtd.getDisplayType().getLocalizedMessage(SessionHelper.getLocale());
            IBAUtility ibau = new IBAUtility(wtd);
            String value = ibau.getIBAValue("SUBTYPE");
            if (value != null && !value.equals("")) {
                identifyList.add(wtdtype + "-" + value);
            } else {
                identifyList.add(wtdtype + "-" + "");
            }
        } else if (obj instanceof WTChangeOrder2) {
            WTChangeOrder2 ecn = (WTChangeOrder2) obj;
            List changeItemList = new ArrayList();
            TypeIdentifier tid = TypeIdentifierHelper.getType(ecn);
            if (tid.toExternalForm().contains("CHANGE_ECN")) {
                // 如果是变更通告，取改后数据
                changeItemList = ChangeHelper.getChangeResultItem((WTChangeOrder2) obj);
            } else {
                changeItemList = ChangeHelper.getChangeAffectItem((WTChangeOrder2) obj);
            }
            if (changeItemList != null) {
                for (int i = 0; i < changeItemList.size(); i++) {
                    WTObject wto = (WTObject) changeItemList.get(i);
                    if (wto instanceof WTDocument) {
                        WTDocument wtd = (WTDocument) wto;
                        String wtdtype = wtd.getDisplayType().getLocalizedMessage(SessionHelper.getLocale());
                        IBAUtility ibau = new IBAUtility(wtd);
                        String value = ibau.getIBAValue("SUBTYPE");
                        if (value != null && !value.equals("")) {
                            identifyList.add(wtdtype + "-" + value);
                        } else {
                            identifyList.add(wtdtype + "-" + "");
                        }
                    } else if (wto instanceof EPMDocument) {
                        identifyList.add("图档-");
                    }
                }
            }
            if (identifyList.size() == 0) {
                identifyList.add("变更通告-");
            }
        } else if (obj instanceof ProcessEnvelope) {
            ProcessEnvelope pe = (ProcessEnvelope) obj;
            List objList = EnvelopeHelper.service.getAllMembers(pe);
            for (int i = 0; i < objList.size(); i++) {
                Object tempObj = objList.get(i);
                if (tempObj instanceof WTDocument) {
                    IBAUtility ibau = new IBAUtility((WTDocument) tempObj);
                    WTDocument wtd = (WTDocument) tempObj;
                    String wtdtype = wtd.getDisplayType().getLocalizedMessage(SessionHelper.getLocale());
                    String value = ibau.getIBAValue("SUBTYPE");
                    if (value != null && !value.equals("")) {
                        identifyList.add(wtdtype + "-" + value);
                    } else {
                        identifyList.add(wtdtype + "-" + "");
                    }
                } else if (tempObj instanceof EPMDocument) {
                    identifyList.add("图档-");
                }
            }
            if (identifyList.size() == 0) {
                identifyList.add("零部件-");
            }
        }
        return identifyList;
    }

    public static void main(String args[]) throws WTRuntimeException, WTException, RemoteException {
        // ReferenceFactory rf = new ReferenceFactory();
        // WTPart part = (WTPart)rf.getReference("VR:wt.part.WTPart:38321").getObject();
        // WTDocument describDocument = (WTDocument)rf.getReference("VR:wt.doc.WTDocument:42206").getObject();
        // List wtoList = CSCPart.getRelatedWTObjectByPart(part);
        // for(int i = 0 ; i < wtoList.size() ; i++){
        // Object obj = wtoList.get(i);
        // if(obj instanceof WTDocument){
        // WTDocument doc = (WTDocument)obj;
        // System.out.println("doc name is "+doc.getName());
        // }
        // }
        // WTDocument referenceDocument = (WTDocument)rf.getReference("VR:wt.doc.WTDocument:42047").getObject();
        // WTPartDescribeLink wtpdl1 = WTPartDescribeLink.newWTPartDescribeLink(part, describDocument);
        // WTPartDescribeLink wtpdl2 = WTPartDescribeLink.newWTPartDescribeLink(part, referenceDocument);
        // PersistenceHelper.manager.save(wtpdl1);
        // PersistenceHelper.manager.save(wtpdl2);
        List<String> typeList = getIdentifyList("VR:wt.change2.WTChangeOrder2:44071");
        System.out.println(typeList);
    }

    /*
     * 指定主任工艺师
     */
    public static void appointZhuRenGongYiShji(Object obj) {
        boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        try {
	    	WfProcess process = (WfProcess) obj;
	    	Enumeration enumeration = WfEngineHelper.service.getProcessSteps(process, null);
	    	String zhuRenGongyishi = null;
	        while (enumeration.hasMoreElements()) {
	            WfActivity wfactivity = (WfActivity) enumeration.nextElement();
	            if (wfactivity instanceof WfAssignedActivity) {
	                WfAssignedActivity wfassignedactivity = (WfAssignedActivity) wfactivity;
	                if("指派工艺组长".equals(wfassignedactivity.getName())){
	                	QuerySpec qs = new QuerySpec(WorkItem.class);
	                	qs.setAdvancedQueryEnabled(true);

	        			qs.appendWhere(new SearchCondition(WorkItem.class,
	        					"source.key.id",SearchCondition.EQUAL, wfassignedactivity.getPersistInfo().getObjectIdentifier().getId()),  new int[0]);

	        			/*ClassAttribute createstampa2 = new ClassAttribute(WorkItem.class,
	        					WorkItem.CREATE_TIMESTAMP);*/
	        			TableColumn ca = new TableColumn("A0", "createstampa2");
	        		    OrderBy orderBy = new OrderBy(ca, true);
	        		    qs.appendOrderBy(orderBy, new int[0]);
	        		    QueryResult  qr = PersistenceServerHelper.manager.query( qs);
	        		    if(qr.hasMoreElements()){
	        		    	WorkItem it = (WorkItem)qr.nextElement();
	        		    	zhuRenGongyishi = it.getCompletedBy();
	        		    }

	                }
	            }
	        }
	        if(zhuRenGongyishi!=null&&!"".equals(zhuRenGongyishi)){
                WTUser user = CSCPrincipal.getUserByName(zhuRenGongyishi);
                if(user!=null) {
                    Team team = (Team) process.getTeamId().getObject();
                    Role targetRole = Role.toRole("ZHIPAIGONGYIZUZHANGZHE");

                    HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
                    List tempUserList = (List) rolePrincipalListMap.get(targetRole);
                    for (int i = 0; tempUserList != null && i < tempUserList.size(); i++) {
                        WTUser tuser = (WTUser) ((WTPrincipalReference) tempUserList.get(i)).getObject();
                        team.deletePrincipalTarget(targetRole, tuser);
                    }
                    team.addPrincipal(targetRole, user);
                    team = (Team) PersistenceHelper.manager.refresh(team);
                    team = (Team) PersistenceHelper.manager.save(team);
                }

	        }
        }catch (WTException e) {
            e.printStackTrace();
        }
        finally {
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }

}

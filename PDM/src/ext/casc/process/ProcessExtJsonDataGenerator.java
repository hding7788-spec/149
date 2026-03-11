package ext.casc.process;

import ext.casc.constants.Constants;
import ext.casc.process.util.ProcessUtil;
import ext.casc.workflow.signtrue.zp.SignatureService;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;

import java.util.*;
import java.util.Map.Entry;

public class ProcessExtJsonDataGenerator {
    public static String genGyyJsonDataByCurrentUser(String taskOid) {
        boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            ReferenceFactory rf = new ReferenceFactory();
            ProcessTaskItem taskItem = (ProcessTaskItem) rf.getReference(taskOid).getObject();
            String executorRole = taskItem.getExecutorRole();
            if(executorRole.equals(ProcessConstants.ROLE_GONGYIZUZHANG)) {
                return getGYYJsonData(taskOid);
            } else if(executorRole.equals(ProcessConstants.ROLE_ZHURENGONGYISHI)) {
                return getGYZZJson(taskOid);
            }
        } catch(WTRuntimeException e) {
            e.printStackTrace();
        } catch(WTException e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(access);
        }
        return "";
    }

    private static String getGYYJsonData(String taskOid) {
    	ReferenceFactory rf = new ReferenceFactory();
        try {
            Map<String, Set<WTUser>> dataMap = getGYYContainerUsersData(taskOid);

            Set<Entry<String, Set<WTUser>>> set = dataMap.entrySet();
            StringBuffer result = new StringBuffer("[");
            for (Entry<String, Set<WTUser>> entry : set) {
                String roleS = entry.getKey();
                if (roleS.contains("工艺员")) {
                    Set<WTUser> userSet = entry.getValue();
                    result.append("{");
                    result.append("text:'" + roleS + "',");
                    result.append("expand:true,");
                    result.append("children:[");
                    for (WTUser u : userSet) {
                        String name = u.getName() + "(" + u.getFullName() + ")";
                        result.append("{");
                        result.append("id:'" + rf.getReferenceString(u) + "',");
                        result.append("text:'" + name + "',");
                        result.append("checked:false,");
                        result.append("leaf:true");
                        result.append("},");
                    }
                    if (!userSet.isEmpty()) {
                        result = result.deleteCharAt(result.length() - 1);
                    }
                    result.append("]");
                    result.append("}");
                    result.append(",");
                }
            }
            if (!set.isEmpty()) {
                result = result.deleteCharAt(result.length() - 1);
            }
            result.append("]");
            return result.toString();
        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getGYZZJson(String taskOid) {
    	ReferenceFactory rf = new ReferenceFactory();
        try {
            Map<String, Set<WTUser>> dataMap = getGYZZ(taskOid);
            Set<Entry<String, Set<WTUser>>> set = dataMap.entrySet();
            StringBuffer result = new StringBuffer("[");
            for (Entry<String, Set<WTUser>> entry : set) {
                String roleS = entry.getKey();
                if (roleS.contains("工艺组长")) {
                    Set<WTUser> userSet = entry.getValue();
                    result.append("{");
                    result.append("text:'" + roleS + "',");
                    result.append("expand:true,");
                    result.append("children:[");
                    for (WTUser u : userSet) {
                        String name = u.getName() + "(" + u.getFullName() + ")";
                        result.append("{");
                        result.append("xtype:'radio',");
                        result.append("id:'" + rf.getReferenceString(u) + "',");
                        result.append("text:'" + name + "',");
                        result.append("checked:false,");
                        result.append("leaf:true");
                        result.append("},");
                    }
                    if (!userSet.isEmpty()) {
                        result = result.deleteCharAt(result.length() - 1);
                    }
                    result.append("]");
                    result.append("}");
                    result.append(",");
                }
            }
            if (!set.isEmpty()) {
                result = result.deleteCharAt(result.length() - 1);
            }
            result.append("]");
            return result.toString();
        } catch (WTRuntimeException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Map<String, Set<WTUser>> getGYYContainerUsersData(String taskOid) throws WTException {
        Map<String, Set<WTUser>> dataMap = new TreeMap<String, Set<WTUser>>();
        WTContained contained = getContainedByWorkItem(taskOid);
        String chejian = getCheJianOrXiangMuNumByUser((WTContainer) contained);
        System.out.println("--->>chejian:" + chejian);
        String filterRole = "";
        filterRole =  Constants.allChejianToWorkFlowGYYRoleMap.get(chejian);

        /*if ("1".equals(chejian)) {
        	filterRole = ProcessConstants.ROLE_NAME_YICHEJIAN;
        } else if ("2".equals(chejian)) {
        	filterRole = ProcessConstants.ROLE_NAME_ERCHEJIAN;
        } else if ("3".equals(chejian)) {
        	filterRole = ProcessConstants.ROLE_NAME_SANCHEJIAN;
        } else if ("4".equals(chejian)) {
        	filterRole = ProcessConstants.ROLE_NAME_SICHEJIAN;
        } else if ("5".equals(chejian)) {
        	filterRole = ProcessConstants.ROLE_NAME_WUCHEJIAN;
        } else if ("6".equals(chejian)) {
        	filterRole = ProcessConstants.ROLE_NAME_LIUCHEJIAN;
        } else if ("7".equals(chejian)) {
        	filterRole = ProcessConstants.ROLE_NAME_QICHEJIAN;
        } else if ("8".equals(chejian)) {
        	filterRole = ProcessConstants.ROLE_NAME_BACHEJIAN;
        } else if (Constants.ROLE_XIANGMUBUXINGHAOZHUGUAN.equals(chejian)) {
        	filterRole = ProcessConstants.ROLE_NAME_XIANGMUBU;
        } else if (Constants.ROLE_HOUQINBU.equals(chejian)) {
        	filterRole = ProcessConstants.ROLE_HOUQINBUGONGYIYUAN;
        }*/

        ContainerTeamManaged teamManaged = (ContainerTeamManaged) contained;
        ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(teamManaged);
        Vector<Role> vector = containerTeam.getRoles();
        Iterator<Role> iterator = vector.iterator();
        Role role = null;
        Set<WTUser> users = null;
        while (iterator.hasNext()) {
            role = iterator.next();
            if (role.toString().equals(filterRole)) {
                ArrayList<WTPrincipalReference> allUser = containerTeam.getAllPrincipalsForTarget(role);
                users = new HashSet<WTUser>();
                for (WTPrincipalReference ref : allUser) {
                    Persistable per = ref.getObject();
                    if (per instanceof WTUser) {
                        WTUser user = (WTUser) per;
                        users.add(user);
                    }
                    if (per instanceof WTGroup) {
                        SignatureService.getUserFromWTGroup((WTGroup) per, users);
                    }
                }
                dataMap.put(role.getDisplay(Locale.CHINA), users);
            }
        }
        return dataMap;
    }

    private static Map<String, Set<WTUser>> getGYZZ(String taskOid) throws WTRuntimeException, WTException {
    	Map<String, Set<WTUser>> dataMap = new TreeMap<String, Set<WTUser>>();
    	WTContained contained = getContainedByWorkItem(taskOid);
    	ContainerTeamManaged teamManaged = (ContainerTeamManaged) contained;
        ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(teamManaged);
        Role role = Role.toRole(ProcessConstants.ROLE_KEY_GONGYIZUZHANG);
        if(role != null) {
        	ArrayList<WTPrincipalReference> allUser = containerTeam.getAllPrincipalsForTarget(role);
            Set<WTUser> users = new HashSet<WTUser>();
            for (WTPrincipalReference ref : allUser) {
                Persistable per = ref.getObject();
                if (per instanceof WTUser) {
                    WTUser user = (WTUser) per;
                    users.add(user);
                }
                if (per instanceof WTGroup) {
                    SignatureService.getUserFromWTGroup((WTGroup) per, users);
                }
            }
            dataMap.put(role.getDisplay(Locale.CHINA), users);
        }
        return dataMap;
    }

    public static WTContained getContainedByWorkItem(String taskOid) throws WTRuntimeException, WTException {
        ReferenceFactory rf = new ReferenceFactory();
        ProcessTaskItem taskItem = (ProcessTaskItem) rf.getReference(taskOid).getObject();
        WTContained contained = taskItem.getContainer();
        return contained;
    }

    /**
     * 根据当前用户获取产品团队的用户所在的车间号或者项目号
     *
     * @param wtContainer
     * @return
     * @throws WTException
     */
    public static String getCheJianOrXiangMuNumByUser(WTContainer wtContainer) throws WTException {
        Map<String, String> map = getAllCheJianAndXiangMuBuMapGYZZ(wtContainer);
        WTUser currentuser = (WTUser) SessionHelper.manager.getPrincipal();
        ReferenceFactory rf = new ReferenceFactory();
        Set<Entry<String, String>> set = map.entrySet();
        for (Iterator<Entry<String, String>> iterator = set.iterator(); iterator.hasNext();) {
            Entry<String, String> entry = iterator.next();
            if (entry.getValue().contains(rf.getReferenceString(currentuser))) {
                return entry.getKey();
            }
        }
        return "";
    }

    /**
     * 获取车间和项目办对应的工艺组长
     *
     * @return ep.[<1,"OR:wt.">,<2,"OR:wt.">,....，<"项","OR:wt.">]
     */
    public static Map<String, String> getAllCheJianAndXiangMuBuMapGYZZ(WTContainer wtContainer) {
        Map<String, String> treeMap = new TreeMap<String, String>();
        ReferenceFactory rf = new ReferenceFactory();
        try {
            Map<String, List<WTUser>> map = ProcessUtil.getGroupAndUsersInOrgContainer();
            Set<Entry<String, List<WTUser>>> set = map.entrySet();
            for (Iterator iterator = set.iterator(); iterator.hasNext();) {
                Entry<String, List<WTUser>> entry = (Entry<String, List<WTUser>>) iterator.next();
                List<WTUser> list = entry.getValue();
                String values = "";
                for (WTUser wtUser : list) {
                    if("".equals(values)){
                        values = rf.getReferenceString(wtUser);
                    } else {
                        values = values + ";" + rf.getReferenceString(wtUser);
                    }
                }
                if (!entry.getValue().isEmpty()) {
                    treeMap.put(entry.getKey(), values);
                }
            }
            List<WTUser> xmbuser = ProcessUtil.getRoleUsersByWTContainer("XIANGMUBUXINGHAOZHUGUANG", wtContainer);
            if (!xmbuser.isEmpty()) {
                treeMap.put("项", rf.getReferenceString(xmbuser.get(0)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return treeMap;
    }
}

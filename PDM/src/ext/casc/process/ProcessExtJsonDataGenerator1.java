package ext.casc.process;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

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
import wt.util.WTException;
import wt.util.WTRuntimeException;
import ext.casc.constants.Constants;
import ext.casc.process.util.ProcessUtil;
import ext.casc.workflow.signtrue.zp.SignatureService;

public class ProcessExtJsonDataGenerator1 {
    public static String genGyyJsonDataByCurrentUser(String taskOid) {
    	boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
    	try {
			ReferenceFactory rf = new ReferenceFactory();
			ProcessTaskItem taskItem = (ProcessTaskItem) rf.getReference(taskOid).getObject();
			String executorRole = taskItem.getExecutorRole();
			if(executorRole.equals(ProcessConstants.ROLE_GONGYIZUZHANG)){
				return getGYYJsonData(taskOid);
			} else if(executorRole.equals(ProcessConstants.ROLE_ZHURENGONGYISHI)){
				return getGYZZJson(taskOid);
			}
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}finally{
       	 wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
       }
    	return "";
    }
    private static String getGYYJsonData(String taskOid) {
    	ReferenceFactory rf = new ReferenceFactory();
        try {
            Map<String, Set<WTUser>> dataMap = getGYYContainerUsersData(taskOid);
            Set<Entry<String, Set<WTUser>>> set = dataMap.entrySet();
            StringBuffer result = new StringBuffer("[");
            List<String> list = standardSortRoles1();
            for (int i = 0; i < list.size(); i++) {
            	 for (Entry<String, Set<WTUser>> entry : set) {
            		 String roleS = entry.getKey();
            		 if (roleS.contains("工艺员")||roleS.contains("物资员")) {
            		 if (list.get(i).equals(roleS)) {

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
//        String filterRole = "";
//        if ("1".equals(chejian)) {
//        	filterRole = ProcessConstants.ROLE_NAME_YICHEJIAN;
//        } else if ("2".equals(chejian)) {
//        	filterRole = ProcessConstants.ROLE_NAME_ERCHEJIAN;
//        } else if ("3".equals(chejian)) {
//        	filterRole = ProcessConstants.ROLE_NAME_SANCHEJIAN;
//        } else if ("4".equals(chejian)) {
//        	filterRole = ProcessConstants.ROLE_NAME_SICHEJIAN;
//        } else if ("5".equals(chejian)) {
//        	filterRole = ProcessConstants.ROLE_NAME_WUCHEJIAN;
//        } else if ("6".equals(chejian)) {
//        	filterRole = ProcessConstants.ROLE_NAME_LIUCHEJIAN;
//        } else if ("7".equals(chejian)) {
//        	filterRole = ProcessConstants.ROLE_NAME_QICHEJIAN;
//        } else if ("8".equals(chejian)) {
//        	filterRole = ProcessConstants.ROLE_NAME_BACHEJIAN;
//        } else if (Constants.ROLE_XIANGMUBUXINGHAOZHUGUAN.equals(chejian)) {
//        	filterRole = ProcessConstants.ROLE_NAME_XIANGMUBU;
//        } else if (Constants.ROLE_HOUQINBU.equals(chejian)) {
//        	filterRole = ProcessConstants.ROLE_HOUQINBUGONGYIYUAN;
//        }

        /*roleList.add(ProcessConstants.ROLE_NAME_YICHEJIAN);
        roleList.add(ProcessConstants.ROLE_NAME_ERCHEJIAN);
        roleList.add(ProcessConstants.ROLE_NAME_SANCHEJIAN);
        roleList.add(ProcessConstants.ROLE_NAME_SICHEJIAN);
        roleList.add(ProcessConstants.ROLE_NAME_WUCHEJIAN);
        roleList.add(ProcessConstants.ROLE_NAME_LIUCHEJIAN);
        roleList.add(ProcessConstants.ROLE_NAME_QICHEJIAN);
        roleList.add(ProcessConstants.ROLE_NAME_BACHEJIAN);
        roleList.add(ProcessConstants.ROLE_NAME_XIANGMUBU);
        roleList.add(ProcessConstants.ROLE_HOUQINBUGONGYIYUAN);*/
       // filterRole =  Constants.allChejianToWorkFlowGYYRoleMap.get(chejian);

        ContainerTeamManaged teamManaged = (ContainerTeamManaged) contained;
        ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(teamManaged);
        Vector<Role> vector = containerTeam.getRoles();
        Iterator<Role> iterator = vector.iterator();
        Role role = null;
        Set<WTUser> users = null;
        while (iterator.hasNext()) {
            role = iterator.next();
            for (int i = 0; i < Constants.ALLGONGYIYUAN.size(); i++) {
            String	filterRole=(String) Constants.ALLGONGYIYUAN.get(i);
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

    public static List<String> standardSortRoles1() {
        List<String> list = new ArrayList<String>();
        /*list.add("一车间工艺员");
        list.add("二车间工艺员");
        list.add("三车间工艺员");
        list.add("四车间工艺员");
        list.add("五车间工艺员");
        list.add("六车间工艺员");
        list.add("七车间工艺员");
        list.add("八车间工艺员");
        list.add("九车间工艺员");
        list.add("项目部工艺员");
        list.add("后勤部工艺员");*/
        for(String s:Constants.ALLGONGYIYUAN){
        	Role role = Role.toRole(s);
        	list.add(role.getDisplay(Locale.CHINA));
        }
        return list;
    }
}

package com.glaway.mpm.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.util.WTException;
import wt.workflow.engine.WfProcess;
import ext.casc.workflow.setparticipant.PrincipalHelper;

public class PbomWorkflowHelper {

    /**将业务对象所在容器团队名称为roleName的角色人员添加到流程对应角色中
     * @param roleKey 角色名称(key值)
     * @param pbo
     * @param self
     */
    public static void setContainerTeamRoleToProcessTeamRole(String roleName, Object pbo, ObjectReference self) {
        Role targetRole = Role.toRole(roleName);
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            WfProcess process = (WfProcess) self.getObject();
            Team team = (Team) process.getTeamId().getObject();
            
            if(!"ZHURENGONGYISHI".equals(roleName)) {//主任工艺师取当前用户
                Map<Role, List<WTUser>> roleAndUserMap = PrincipalHelper.service.getRoleAndUserByContainer(pbo);
                for (Role role : roleAndUserMap.keySet()) {
                    if (targetRole.equals(role)) {
                        List<WTUser> userList = roleAndUserMap.get(role);
                        
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
            }       
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);
        }
    }
    
}

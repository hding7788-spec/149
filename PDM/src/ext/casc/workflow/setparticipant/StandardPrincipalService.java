package ext.casc.workflow.setparticipant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import wt.change2.WTChangeIssue;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.lifecycle.LifeCycleManaged;
import wt.org.OrganizationServicesHelper;
import wt.org.PrincipalCollationKeyFactory;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.services.StandardManager;
import wt.team.Team;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.team.TeamReference;
import wt.util.SortedEnumeration;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfContainer;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;
import ext.casc.change.ChangeHelper;
import ext.ases.envelope.EnvelopeHelper;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.util.QueryHelper;

public class StandardPrincipalService extends StandardManager implements
		PrincipalService, Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2708775060607535427L;

	public static StandardPrincipalService newStandardPrincipalService()
			throws WTException {
		StandardPrincipalService instance = new StandardPrincipalService();
		instance.initialize();
		return instance;
	}
	
	/**
	 * 本方法用于查找用户所在的所有组,外部方法,可调用.
	 * @author wei,yu
	 * @version windchill9.0 F000 - windchill 9.1
	 * @param 参数就是一个用户对象
	 * @return 返回结果是一个list集合，里面存放的是一组WTGroup对象
	 * */
	public List<WTGroup> getUserGroups(WTUser user) throws WTException{
		List<WTGroup> userGroupList = new ArrayList<WTGroup>();
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
		try{
			Enumeration em = OrganizationServicesHelper.manager.parentGroups(user);
			while(em.hasMoreElements()){
				WTGroup group = (WTGroup)((WTPrincipalReference)em.nextElement()).getObject();
				userGroupList.add(group);
			}
		}catch(WTException e){
			e.printStackTrace();
		}finally{
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return userGroupList;
	}
	
	
	
	/**
	 * 本方法用于保存工作流的参与者到工作流的主要业务对象的团队角色中,外部方法,可调用.
	 * @author wei,yu
	 * @version windchill9.0 F000 - windchill 9.1
	 * @param 参数一是一个map容器，里面存放角色(Key)-用户列表(value)对应关系。其中用户列表是一个list集合，里面存放着一组用户名。参数二是工作流的oid,参数三是本地语言
	 * @return 返回结果是空
	 * */
	public void saveTeamRole(Map<String,List<String>> roleUser, String oid,Locale locale) throws WTException {
		ReferenceFactory rf = new ReferenceFactory();
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
		Transaction tx = new Transaction();
		try {
			tx.start();
			WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
			WfActivity wfAct = (WfActivity) wi.getSource().getObject();
			WfContainer wfcont = (WfContainer) wfAct.getParentProcessRef().getObject();
			WfProcess process = null;
			if (wfcont instanceof WfBlock)
				process = ((WfBlock)wfcont).getParentProcess();
			else
				process = (WfProcess) wfcont;
			Team team = (Team) process.getTeamId().getObject();
			Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");

			//delete role and user in pbo team first, then set the new role and users to pbo team
			Team pbo_team = TeamHelper.service.getTeam((TeamManaged)pbo);
            HashMap pbo_rpMap = TeamHelper.service.findAllParticipantsByRole(pbo_team);			
			/*
			 TeamManaged pbo = (TeamManaged) obj;
			 Team team = (Team) pbo.getTeamId().getObject();
			 */
			HashMap rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
			Iterator it1 = roleUser.keySet().iterator();
			while (it1.hasNext()) {
				String roleName = (String) it1.next();
				//System.out.println("begin to del role user role name is " + roleName);
				Role role = Role.toRole(roleName);
				List userList = (List) rolePrincipalListMap.get(role);
				for (int i = 0; userList != null && i < userList.size(); i++) {
					WTUser user = (WTUser) ((WTPrincipalReference) userList.get(i)).getObject();
					//System.out.println("wait for del Role is " + roleName);
					//System.out.println("wait for del userName is " + user.getFullName());
					//TeamHelper.service.deleteRolePrincipalMap(role, user, team);
					team.deletePrincipalTarget(role, user);
				}
				List userList_pbo = (List) pbo_rpMap.get(role);
				for (int i = 0; userList_pbo != null && i < userList_pbo.size(); i++) {
					WTUser user = (WTUser) ((WTPrincipalReference) userList_pbo.get(i)).getObject();
					//TeamHelper.service.deleteRolePrincipalMap(role, user, team);
					pbo_team.deletePrincipalTarget(role, user);
				}
			}
			/*
			 team = (Team) PersistenceHelper.manager.refresh(team);
			 team = (Team) PersistenceHelper.manager.save(team);
			 
			 TeamReference teamreferences1 = TeamReference.newTeamReference(team);
			 TeamHelper.service.augmentRoles((LifeCycleManaged) obj, teamreferences1);
			 */
			Iterator it2 = roleUser.keySet().iterator();
			while (it2.hasNext()) {
				String roleName = (String) it2.next();
				Role role = Role.toRole(roleName);
				List<String> userList = roleUser.get(roleName);
				for (int i = 0; i < userList.size(); i++) {
					String userName = userList.get(i);
					WTUser user = QueryHelper.getUserByName(userName,locale).get(0);
					//System.out.println("begin to add role user role name is " + roleName);
					//System.out.println("begin to add role user role name is " + user.getFullName());
					//TeamHelper.service.addRolePrincipalMap(role, user, team);
					team.addPrincipal(role, user);
				}
				//added by Leo ,add the role and users to pbo team
				List<String> userList_pbo = roleUser.get(roleName);
				for (int i = 0; userList_pbo != null && i < userList_pbo.size(); i++) {
					String userName = userList_pbo.get(i);
					WTUser user = QueryHelper.getUserByName(userName,locale).get(0);
					//TeamHelper.service.addRolePrincipalMap(role, user, team);
					pbo_team.addPrincipal(role, user);
				}
			}

			team = (Team) PersistenceHelper.manager.refresh(team);
			team = (Team) PersistenceHelper.manager.save(team);
			pbo_team = (Team) PersistenceHelper.manager.refresh(pbo_team);
			pbo_team = (Team) PersistenceHelper.manager.save(pbo_team);
			//TeamReference teamreferences2 = TeamReference.newTeamReference(team);
			//TeamHelper.service.augmentRoles((LifeCycleManaged) obj, teamreferences2);
            /*
			if (pbo != null) {
				TeamReference tr = TeamReference.newTeamReference(team);
				TeamReference pbo_tr = TeamReference.newTeamReference(pbo_team);
				TeamHelper.service.augmentRoles((LifeCycleManaged) pbo, tr);
				TeamHelper.service.augmentRoles((LifeCycleManaged) pbo, pbo_tr);
			}*/
			//对对象关联物进行参与者设置
			/*if(pbo instanceof ProcessEnvelope){
				ArrayList envelopeItems = EnvelopeHelper.service.getAllMembers((ProcessEnvelope)pbo);
				for (Object envelopeItemObj : envelopeItems) {
					WTObject relatedObject = (WTObject) envelopeItemObj;
					if(relatedObject instanceof TeamManaged){
						saveObjectTeamRole(roleUser, (TeamManaged)relatedObject, locale);
					}
				}
			}else if(pbo instanceof WTChangeOrder2){
				WTChangeOrder2 ecn = (WTChangeOrder2)pbo;
				ArrayList aRelatedObject1 = ChangeHelper.getChangeAffectItem(ecn);
				ArrayList aRelatedObject2 = ChangeHelper.getChangeResultItem(ecn);
				
				ArrayList aRelatedObject = new ArrayList();
				aRelatedObject.addAll(aRelatedObject1);
				aRelatedObject.addAll(aRelatedObject2);
				for (int i = 0; i < aRelatedObject.size(); i++) {
					WTObject relatedObject = (WTObject)aRelatedObject.get(i);
					if(relatedObject instanceof TeamManaged){
						saveObjectTeamRole(roleUser, (TeamManaged)relatedObject, locale);
					}
				}
			}else if(pbo instanceof WTChangeRequest2){
				WTChangeRequest2 ecr = (WTChangeRequest2)pbo;
				ArrayList aRelatedObject = ChangeHelper.getChangeRelatedItem(ecr);
				for (int i = 0; i < aRelatedObject.size(); i++) {
					WTObject relatedObject = (WTObject)aRelatedObject.get(i);
					if(relatedObject instanceof TeamManaged){
						saveObjectTeamRole(roleUser, (TeamManaged)relatedObject, locale);
					}
				}
			}else if(pbo instanceof WTChangeIssue){
				WTChangeIssue pr = (WTChangeIssue)pbo;
				ArrayList aRelatedObject = ChangeHelper.getChangeIssueItem(pr);
				for (int i = 0; i < aRelatedObject.size(); i++) {
					WTObject relatedObject = (WTObject)aRelatedObject.get(i);
					if(relatedObject instanceof TeamManaged){
						saveObjectTeamRole(roleUser, (TeamManaged)relatedObject, locale);
					}
				}
			}*/
			tx.commit();
			tx = null;
		} catch (WTRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(tx != null){
				tx.rollback();
				tx = null;
			}
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
		}

	}
	
	public void saveTeamRole(String roleName,List<WTUser> userList, Object self,Locale locale) throws WTException {
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
		Transaction tx = new Transaction();
		try {
			tx.start();
			WfProcess process = (WfProcess) self;
			Team team = (Team) process.getTeamId().getObject();
			Persistable pbo = (Persistable) process.getContext().getValue("primaryBusinessObject");
      Role role = Role.toRole(roleName);
      if (pbo!=null){
      	Team pbo_team = TeamHelper.service.getTeam((TeamManaged)pbo);
      	if(pbo_team !=null){
      	    HashMap pbo_rpMap = TeamHelper.service.findAllParticipantsByRole(pbo_team);	
      	    
      	    List userList_pbo = (List) pbo_rpMap.get(role);
      	    for (int i = 0; userList_pbo != null && i < userList_pbo.size(); i++) {
      	        WTUser user = (WTUser) ((WTPrincipalReference) userList_pbo.get(i)).getObject();
      	        //TeamHelper.service.deleteRolePrincipalMap(role, user, team);
      	        pbo_team.deletePrincipalTarget(role, user);
      	    }	
      	    for (int i = 0; userList_pbo != null && i < userList_pbo.size(); i++) {
      	        WTUser user = (WTUser) ((WTPrincipalReference) userList_pbo.get(i)).getObject();
      	        //TeamHelper.service.addRolePrincipalMap(role, user, team);
      	        pbo_team.addPrincipal(role, user);
      	    }
      	    
      	    pbo_team = (Team) PersistenceHelper.manager.refresh(pbo_team);
      	    pbo_team = (Team) PersistenceHelper.manager.save(pbo_team);
      	    
      	    TeamReference pbo_tr = TeamReference.newTeamReference(pbo_team);
      	    TeamHelper.service.augmentRoles((LifeCycleManaged) pbo, pbo_tr);
      	}
      	}
      
      
				
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

			//added by Leo ,add the role and users to pbo team
			
			team = (Team) PersistenceHelper.manager.refresh(team);
			team = (Team) PersistenceHelper.manager.save(team);

			
			if (pbo != null) {
				TeamReference tr = TeamReference.newTeamReference(team);
				
				TeamHelper.service.augmentRoles((LifeCycleManaged) pbo, tr);
				
			}
			ArrayList<String> userNames = new ArrayList<String>();
			for (int i = 0; i < userList.size(); i++) {
				WTUser wtuser = userList.get(i);
				userNames.add(wtuser.getName());
			}
			HashMap<String, List<String>> roleUser = new HashMap<String, List<String>>();
			roleUser.put(roleName, userNames);
			//对对象关联物进行参与者设置
			if(pbo instanceof ProcessEnvelope){	
				ArrayList envelopeItems = EnvelopeHelper.service.getAllMembers((ProcessEnvelope)pbo);
				for (Object envelopeItemObj : envelopeItems) {
					WTObject relatedObject = (WTObject) envelopeItemObj;
					if(relatedObject instanceof TeamManaged){
						saveObjectTeamRole(roleUser, (TeamManaged)relatedObject, locale);
					}
				}
			}else if(pbo instanceof WTChangeOrder2){
				WTChangeOrder2 ecn = (WTChangeOrder2)pbo;
				ArrayList aRelatedObject1 = ChangeHelper.getChangeAffectItem(ecn);
				ArrayList aRelatedObject2 = ChangeHelper.getChangeResultItem(ecn);
				
				ArrayList aRelatedObject = new ArrayList();
				aRelatedObject.addAll(aRelatedObject1);
				aRelatedObject.addAll(aRelatedObject2);
				for (int i = 0; i < aRelatedObject.size(); i++) {
					WTObject relatedObject = (WTObject)aRelatedObject.get(i);
					if(relatedObject instanceof TeamManaged){
						saveObjectTeamRole(roleUser, (TeamManaged)relatedObject, locale);
					}
				}
			}else if(pbo instanceof WTChangeRequest2){
				WTChangeRequest2 ecr = (WTChangeRequest2)pbo;
				ArrayList aRelatedObject = ChangeHelper.getChangeRelatedItem(ecr);
				for (int i = 0; i < aRelatedObject.size(); i++) {
					WTObject relatedObject = (WTObject)aRelatedObject.get(i);
					if(relatedObject instanceof TeamManaged){
						saveObjectTeamRole(roleUser, (TeamManaged)relatedObject, locale);
					}
				}
			}else if(pbo instanceof WTChangeIssue){
				WTChangeIssue pr = (WTChangeIssue)pbo;
				ArrayList aRelatedObject = ChangeHelper.getChangeIssueItem(pr);
				for (int i = 0; i < aRelatedObject.size(); i++) {
					WTObject relatedObject = (WTObject)aRelatedObject.get(i);
					if(relatedObject instanceof TeamManaged){
						saveObjectTeamRole(roleUser, (TeamManaged)relatedObject, locale);
					}
				}
			}
			tx.commit();
			tx = null;
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			if(tx != null){
				tx.rollback();
				tx = null;
			}
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
		}
	}
	
	
	/**
	 * 本方法用于查找工作流中的参与者,外部方法,可调用.
	 * @author wei,yu
	 * @version windchill9.0 F000 - windchill 9.1
	 * @param 参数一是工作流的oid,参数二是参与者的角色
	 * @return 返回结果是一个map容易里面存放角色(key)-参与者集合(value)对应关系
	 * */
	public Map<Role,List<WTUser>> getUserByRole(String oid, List<String> roleList) throws WTException {
		Map<Role,List<WTUser>> roleAndUserMap = new HashMap<Role,List<WTUser>>();
		ReferenceFactory rf = new ReferenceFactory();
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
			WfActivity wfAct = (WfActivity) wi.getSource().getObject();
			WfProcess process = wfAct.getParentProcess();
			Team team = (Team) process.getTeamId().getObject();
			Map rolePrincipalListMap = TeamHelper.service.findAllParticipantsByRole(team);
			for (int i = 0; i < roleList.size(); i++) {
				String roleName = roleList.get(i);
				Role role = Role.toRole(roleName);
				List tempList = (List) rolePrincipalListMap.get(role);
				List<WTUser> userList = new ArrayList<WTUser>();
				if(tempList != null ){
					for(int j = 0 ; j < tempList.size() ; j ++){
						userList.add((WTUser)((WTPrincipalReference)tempList.get(j)).getObject());
					}
				}
				roleAndUserMap.put(role, userList);
			}

		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
		}

		return roleAndUserMap;
	}
	
	public Map<Role,List<WTPrincipalReference>> getRoleAndUserByContainer(Object pbo,String roleKey) throws WTException {
//		CSCDebug.outDebugInfo("Now you enter getRoleAndUserByContainer function!!");
		Map<Role,List<WTPrincipalReference>> roleAndUserMap = new HashMap<Role,List<WTPrincipalReference>>();
		List<WTPrincipalReference> principalList = new ArrayList<WTPrincipalReference>();
		if (pbo != null) {
			boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
			try {
				WTContained obj = (WTContained)pbo;
				WTContainer container = obj.getContainer();
				ContainerTeamManaged ctm = null;
				if(container instanceof ContainerTeamManaged){
					ctm = (ContainerTeamManaged)container;
				}
				ContainerTeam containerteam = ContainerTeamHelper.service.getContainerTeam(ctm);
				Vector vector = containerteam.getRoles();
				for(int i = 0 ; i < vector.size() ; i++){
					Role tempRole = (Role)vector.get(i);
//					CSCDebug.outDebugInfo("tempRole is :" + tempRole.toString());
					if(tempRole.toString().equals(roleKey)){
						principalList=containerteam.getAllPrincipalsForTarget(tempRole);
						//System.out.println("is exist user " + tempUserList);
						roleAndUserMap.put(tempRole, principalList);
						if (principalList != null ){
//						CSCDebug.outDebugInfo("tempUserList size: "+principalList.size());
						}
						break;
					}
				}
			} catch (WTException e) {
				e.printStackTrace();
			} finally {
				wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
			}
		}
		
//		List<WTPrincipalReference> temp = roleAndUserMap.get(Role.toRole(PrincipalConstants.ASES_ROLE_RECEIVER));
//		if (temp != null){
////			CSCDebug.outDebugInfo("roleAndUserMap WTPrincipalReference: "+temp.size());
//		}
		return roleAndUserMap;
	}
	
	public Map<Role,List<WTUser>> getRoleAndUserByContainer(String oid) throws WTException {
		Map<Role,List<WTUser>> roleAndUserMap = new HashMap<Role,List<WTUser>>();
		ReferenceFactory rf = new ReferenceFactory();
		List<WTUser> userList = new ArrayList<WTUser>();

		if (oid != null) {
			boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
			try {
				WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
				WfActivity wfAct = (WfActivity) wi.getSource().getObject();
				/*modified by zf 20100406
				 * WfContainer wfcont = (WfContainer) wfAct.getParentProcessRef().getObject();
				if (wfcont instanceof WfBlock) {
					return roleAndUserMap;
				}*/
				//WfProcess process = (WfProcess) wfcont;
				//Team team = (Team) process.getTeamId().getObject();
				Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
				
				WTContained obj = (WTContained)pbo;
				WTContainer container = obj.getContainer();
				ContainerTeamManaged ctm = null;
				if(container instanceof ContainerTeamManaged){
					ctm = (ContainerTeamManaged)container;
				}
				ContainerTeam containerteam = ContainerTeamHelper.service.getContainerTeam(ctm);
				Vector vector = containerteam.getRoles();

				for (int i = 0; i < vector.size(); i++) {
					List tempUserList = containerteam.getAllPrincipalsForTarget((Role) vector.get(i));
					//System.out.println("is exist user " + tempUserList);
					Role role = (Role)vector.get(i);
					for (int j = 0; tempUserList != null && j < tempUserList.size(); j++) {
						WTPrincipalReference wtprincipalreference = (WTPrincipalReference) tempUserList.get(j);
						Persistable persistable = wtprincipalreference.getObject();
						if(persistable instanceof WTUser){
							WTUser user = (WTUser)persistable;
							
							//String userName = ((WTUser) persistable).getFullName() + "("+ ((WTUser) wtprincipalreference.getObject()).getName() + ")";
							//System.out.println(userName);
							userList.add((WTUser)persistable);	
						}else if (persistable instanceof WTGroup){
							List<WTUser> tempList = getGroupMembersOfUser((WTGroup)persistable);
							for(int k = 0 ; k < tempList.size() ; k ++){
								userList.add(tempList.get(k));
							}
						}
					}
					roleAndUserMap.put(role, userList);
					userList = new ArrayList<WTUser>();
				}
			} catch (WTException e) {
				e.printStackTrace();
			} finally {
				wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
			}
		}

		return roleAndUserMap;
	}
	
	public Map<Role,List<WTUser>> getRoleAndUserByContainer(Object obj) throws WTException {
		Map<Role,List<WTUser>> roleAndUserMap = new HashMap<Role,List<WTUser>>();
		List<WTUser> userList = new ArrayList<WTUser>();

		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
		try {
			Persistable pbo = (Persistable) obj;				
			WTContained wtc = (WTContained)pbo;
			WTContainer container = wtc.getContainer();
			ContainerTeamManaged ctm = null;
			if(container instanceof ContainerTeamManaged){
				ctm = (ContainerTeamManaged)container;
			}
			ContainerTeam containerteam = ContainerTeamHelper.service.getContainerTeam(ctm);
			Vector vector = containerteam.getRoles();

			for (int i = 0; i < vector.size(); i++) {
				List tempUserList = containerteam.getAllPrincipalsForTarget((Role) vector.get(i));
				Role role = (Role)vector.get(i);
				for (int j = 0; tempUserList != null && j < tempUserList.size(); j++) {
					WTPrincipalReference wtprincipalreference = (WTPrincipalReference) tempUserList.get(j);
					Persistable persistable = wtprincipalreference.getObject();
					if(persistable instanceof WTUser){
						userList.add((WTUser)persistable);	
					}else if (persistable instanceof WTGroup){
						List<WTUser> tempList = getGroupMembersOfUser((WTGroup)persistable);
						for(int k = 0 ; k < tempList.size() ; k ++){
							userList.add(tempList.get(k));
						}
					}
				}
				roleAndUserMap.put(role, userList);
				userList = new ArrayList<WTUser>();
			}
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		
		return roleAndUserMap;
	}

	/**
	 * 本方法用于查找系统组中的用户,内部方法,不可调用.
	 * @author wei,yu
	 * @version windchill9.0 F000 - windchill 9.1
	 * @param 参数是一个WTGroup对象
	 * @return 返回结果是一个list列表,里面存放一组WTUser对象
	 * */
	private List<WTUser> getGroupMembersOfUser(WTGroup group)
			throws WTException {
		List<WTUser> users = new ArrayList<WTUser>();
		Enumeration member = group.members();

		while (member.hasMoreElements()) {
			WTPrincipal principal = (WTPrincipal) member.nextElement();
			if (principal instanceof WTUser) {
				users.add((WTUser) principal);
			} else if (principal instanceof WTGroup) {
				//System.out.println("contains group in group");
				List<WTUser> ausers = getGroupMembersOfUser((WTGroup) principal);
				for (int i = 0; i < ausers.size(); i++) {
					users.add(ausers.get(i));
				}
			}
		}

		return users;
	}


	/**
	 * 本方法用于在开发工具中测试以上所有方法
	 * @author wei,yu
	 * @version windchill9.0 F000 - windchill 9.1
	 * @param 参数是无
	 * @return 返回结果空
	 * */
	public static void main(String args[]) throws WTException{
		Map <String,List<String>> roleAndUserMap = new HashMap<String,List<String>>();
		String roleName1 = "批准者";
		String roleName2 = "审阅者";
		String roleName3 = "签约者";
		String user1 = "OR:wt.org.WTUser:304167";
		String user2 = "OR:wt.org.WTUser:304180";
		String user3 = "OR:wt.org.WTUser:304188";
		String user4 = "OR:wt.org.WTUser:304196";
		String user5 = "OR:wt.org.WTUser:304204";
		String user6 = "OR:wt.org.WTUser:10";
		String user7 = "OR:wt.org.WTUser:8491";
		List<String> userList1 = new ArrayList<String>();
		userList1.add(user1);
		userList1.add(user2);
		List<String> userList2 = new ArrayList<String>();
		userList2.add(user3);
		userList2.add(user4);
		List<String> userList3 = new ArrayList<String>();
		userList3.add(user5);
		userList3.add(user6);
		userList3.add(user7);
		roleAndUserMap.put(roleName1, userList1);
		roleAndUserMap.put(roleName2, userList2);
		roleAndUserMap.put(roleName3, userList3);
	}

	public List<String> getUserRoles(WTUser user, String oid) throws WTException {
		List<String> userRoleList = new ArrayList<String>();
		ReferenceFactory rf = new ReferenceFactory();
		//List<WTUser> userList = new ArrayList<WTUser>();

		if (oid != null) {
			boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
			try {
				WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
				WfActivity wfAct = (WfActivity) wi.getSource().getObject();
				/*modified by zf 20100406
				 * WfContainer wfcont = (WfContainer) wfAct.getParentProcessRef().getObject();
				if (wfcont instanceof WfBlock) {
					return userRoleList;
				}*/
				Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
				
				WTContained obj = (WTContained)pbo;
				WTContainer container = obj.getContainer();
				ContainerTeamManaged ctm = null;
				if(container instanceof ContainerTeamManaged){
					ctm = (ContainerTeamManaged)container;
				}
				ContainerTeam containerteam = ContainerTeamHelper.service.getContainerTeam(ctm);
				Vector vector = containerteam.getRoles();
				boolean flag = false;
				for (int i = 0; i < vector.size(); i++) {
					Role role = (Role)vector.get(i);
					List tempUserList = containerteam.getAllPrincipalsForTarget(role);
					for (int j = 0; tempUserList != null && j < tempUserList.size(); j++) {
						WTPrincipalReference wtprincipalreference = (WTPrincipalReference) tempUserList.get(j);
						Persistable persistable = wtprincipalreference.getObject();
						if(persistable instanceof WTUser){
							WTUser tempUser = (WTUser)persistable;
							if(tempUser.equals(user)){
								flag = true;
								break;
							}
						}else if (persistable instanceof WTGroup){
							WTGroup tempGroup = (WTGroup)persistable;
							if(tempGroup.isMember(user)){
								flag = true;
								break;
							}
						}
					}
					if(flag){
						userRoleList.add(role.toString());
						flag = false;
					}
				}
			} catch (WTException e) {
				e.printStackTrace();
			} finally {
				wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
			}
		}
		return userRoleList;
	}

	public List<String> getRoles(WTUser user, String oid) throws WTException {
		List<String> userRoleList = new ArrayList<String>();
		ReferenceFactory rf = new ReferenceFactory();
		//List<WTUser> userList = new ArrayList<WTUser>();

		if (oid != null) {
			boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
			try {
				WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
				WfActivity wfAct = (WfActivity) wi.getSource().getObject();
				/*modified by zf 20100406
				 * WfContainer wfcont = (WfContainer) wfAct.getParentProcessRef().getObject();
				if (wfcont instanceof WfBlock) {
					return userRoleList;
				}*/
				Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
				
				WTContained obj = (WTContained)pbo;
				WTContainer container = obj.getContainer();
				ContainerTeamManaged ctm = null;
				if(container instanceof ContainerTeamManaged){
					ctm = (ContainerTeamManaged)container;
				}
				ContainerTeam containerteam = ContainerTeamHelper.service.getContainerTeam(ctm);
				Vector vector = containerteam.getRoles();
				boolean flag = false;
				for (int i = 0; i < vector.size(); i++) {
					Role role = (Role)vector.get(i);
					List tempUserList = containerteam.getAllPrincipalsForTarget(role);
					for (int j = 0; tempUserList != null && j < tempUserList.size(); j++) {
						WTPrincipalReference wtprincipalreference = (WTPrincipalReference) tempUserList.get(j);
						Persistable persistable = wtprincipalreference.getObject();
						if(persistable instanceof WTUser){
							WTUser tempUser = (WTUser)persistable;
							if(tempUser.equals(user)){
								flag = true;
								break;
							}
						}else if (persistable instanceof WTGroup){
							WTGroup tempGroup = (WTGroup)persistable;
							if(tempGroup.isMember(user)){
								flag = true;
								break;
							}
						}
					}
					if(flag){
						userRoleList.add(role.toString());
						flag = false;
					}
				}
			} catch (WTException e) {
				e.printStackTrace();
			} finally {
				wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
			}
		}
		return userRoleList;
	}
	
	public List<WTUser> getUser(String userName, Locale locale) throws WTException {
		List<WTUser> userList = new ArrayList<WTUser>();

		if (userName == null) {
			return userList;
		}

		if (("").equals(userName)) {
			return userList;
		} else if (("*").equals(userName)) {
			return getAllUser();
		} else if (userName.indexOf("*") != -1) {
			return getUserLikeName(userName, locale);
		} else {
			return getUserByName(userName, locale);
		}
	}
	
	@SuppressWarnings("deprecation")
	private List<WTUser> getAllUser() throws WTException {
		List<WTUser> userList = new ArrayList<WTUser>();
		boolean enforce = wt.session.SessionServerHelper.manager
				.setAccessEnforced(false);
		try {
			QuerySpec qs = new QuerySpec(WTUser.class);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WTUser user = (WTUser) qr.nextElement();
				if (!user.isDisabled())
					userList.add(user);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return userList;
	}
	
	@SuppressWarnings("deprecation")
	private List<WTUser> getUserLikeName(String criteria, Locale locale) throws WTException {
		List<WTUser> results = new ArrayList<WTUser>();
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
		try {
			Enumeration usernames = OrganizationServicesHelper.manager.findLikeUser("fullName", criteria);
			if (usernames != null) {
				while (usernames.hasMoreElements()) {
					WTUser curUser = (WTUser) usernames.nextElement();
					results.add(curUser);
				}
			}

			SortedEnumeration fullusernames = new SortedEnumeration(
			OrganizationServicesHelper.manager.findLikeUser("name",criteria), new PrincipalCollationKeyFactory(locale));
			if (fullusernames != null){
				while (fullusernames.hasMoreElements()) {
					WTUser curUser = (WTUser) fullusernames.nextElement();
					if (!results.contains(curUser) && !curUser.isDisabled()){
						results.add(curUser);
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return results;
	}
	
	@SuppressWarnings("deprecation")
	private List<WTUser> getUserByName(String userName, Locale criteria) throws WTException {
		List<WTUser> results = new ArrayList<WTUser>();
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
		try {
			Enumeration usernames = OrganizationServicesHelper.manager.findUser("fullName", userName);
			if (usernames != null) {
				while (usernames.hasMoreElements()) {
					WTUser curUser = (WTUser) usernames.nextElement();
					results.add(curUser);
				}
			}

			SortedEnumeration fullusernames = new SortedEnumeration(OrganizationServicesHelper.manager.findUser("name",userName), new PrincipalCollationKeyFactory(criteria));
			if (fullusernames != null){
				while (fullusernames.hasMoreElements()) {
					WTUser curUser = (WTUser) fullusernames.nextElement();
					if (!results.contains(curUser) && !curUser.isDisabled()){
						results.add(curUser);
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return results;
	}
	
	
	public static void saveObjectTeamRole(Map<String,List<String>> roleUser, TeamManaged obj, Locale locale){
		try {
			Team obj_team = TeamHelper.service.getTeam((TeamManaged)obj);
	        HashMap obj_rpMap = TeamHelper.service.findAllParticipantsByRole(obj_team);		
	        
			Iterator it1 = roleUser.keySet().iterator();
			while (it1.hasNext()) {
				String roleName = (String) it1.next();
				System.out.println("begin to del role user role name is " + roleName);
				Role role = Role.toRole(roleName);

				List userList_obj = (List) obj_rpMap.get(role);
				for (int i = 0; userList_obj != null && i < userList_obj.size(); i++) {
					WTUser user = (WTUser) ((WTPrincipalReference) userList_obj.get(i)).getObject();
					obj_team.deletePrincipalTarget(role, user);
				}				
			}
			
			Iterator it2 = roleUser.keySet().iterator();
			while (it2.hasNext()) {
				String roleName = (String) it2.next();
				Role role = Role.toRole(roleName);

				List<String> userList_obj = roleUser.get(roleName);
				for (int i = 0; userList_obj != null && i < userList_obj.size(); i++) {
					String userName = userList_obj.get(i);
					WTUser user = QueryHelper.getUserByName(userName,locale).get(0);
					obj_team.addPrincipal(role, user);
				}
			}	
			
			obj_team = (Team) PersistenceHelper.manager.refresh(obj_team);
			obj_team = (Team) PersistenceHelper.manager.save(obj_team);

			if (obj != null) {
				TeamReference obj_tr = TeamReference.newTeamReference(obj_team);
				TeamHelper.service.augmentRoles((LifeCycleManaged) obj, obj_tr);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}

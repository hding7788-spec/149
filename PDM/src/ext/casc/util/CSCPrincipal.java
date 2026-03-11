package ext.casc.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import ext.casc.process.ProcessTaskItem;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTReference;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.inf.team.StandardContainerTeamService;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.util.WTException;

public class CSCPrincipal {
	/**
	 * Successed in Release 8.0
	 * Failed in Release 9.0
	 * @param containerteammanaged
	 * @param s
	 * @return
	 */
    public static ArrayList getPrinciaplByRole(ContainerTeamManaged containerteammanaged, String s){
        ArrayList arraylist = new ArrayList();
        if(containerteammanaged == null)
        	return arraylist;
        try{
            ContainerTeam containerteam = ContainerTeamHelper.service.getContainerTeam(containerteammanaged);
            Role role = Role.toRole(s);
            for(Enumeration enumeration = containerteam.getPrincipalTarget(role); enumeration.hasMoreElements(); arraylist.add(enumeration.nextElement())) { }
        }catch(TeamException teamexception){
            teamexception.printStackTrace();
        }catch(WTException wtexception){
            wtexception.printStackTrace();
        }
        return arraylist;
    }
    
    public static String getEmailStringByRole(ContainerTeamManaged containerteammanaged, String role){
    	String mailList = "";
    	ArrayList userList = getPrinciaplByRole(containerteammanaged,role);
    	if(userList != null && userList.size() > 0){
    		for(int i=0; i<userList.size();i++){
    			WTPrincipalReference wtp = (WTPrincipalReference)userList.get(i);
    			if(wtp != null){
    				mailList = mailList + wtp.getEMail()+ ";";
    			}
    		}
    	}
    	if(mailList.endsWith(";")){
    		mailList = mailList.substring(0,mailList.length()-1);
    	}
    	return mailList;    	
    }

    public static ArrayList getUserLikeName(String name){
    	ArrayList list = new ArrayList();
    	try {
    		QuerySpec qs = new QuerySpec(WTUser.class);
			SearchCondition sc = new SearchCondition(WTUser.class,WTUser.NAME, SearchCondition.LIKE, "%"+name+"%", false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()){
				WTUser user = (WTUser)qr.nextElement();

				list.add(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return list;
    }


	public static WTUser getUserByNumber(String value) throws WTException {
		if (value ==null||"".equals(value)||"null".equals(value)) {
             return null;
		}
		wt.fc.ReferenceFactory factory = new wt.fc.ReferenceFactory();
		WTReference reference = factory.getReference(value);
		WTUser taskItem = (WTUser) reference.getObject();
		return taskItem;
	}

    public static WTUser getUserByName(String name){
    	WTUser user = null;
    	try {
    		QuerySpec qs = new QuerySpec(WTUser.class);
			SearchCondition sc = new SearchCondition(WTUser.class,WTUser.NAME, SearchCondition.EQUAL, name, false);
			qs.appendSearchCondition(sc);			
			
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()){
				user = (WTUser)qr.nextElement();
			}
			
			if(user == null)
				user = OrganizationServicesHelper.manager.getUser(name);				
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return user;
    }
    
    public static ArrayList getUserLikeMail(String mail){
    	ArrayList list = new ArrayList();
    	try {
    		QuerySpec qs = new QuerySpec(WTUser.class);
			SearchCondition sc = new SearchCondition(WTUser.class,WTUser.EMAIL, SearchCondition.LIKE, "%"+mail+"%", false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()){
				WTUser user = (WTUser)qr.nextElement();
				list.add(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return list;
    }

    public static Map getPrincipals(ContainerTeamManaged containerteammanaged){
        Object obj = new HashMap();
        try{
            ContainerTeam containerteam = ContainerTeamHelper.service.getContainerTeam(containerteammanaged);
            obj = containerteam.getRolePrincipalMap();          
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return ((Map) (obj));
    }
    
    public static WTGroup getGroupByName(String groupName){
    	WTGroup wtGroup = null;
    	try {
    		QuerySpec qs = new QuerySpec(WTGroup.class);
			SearchCondition sc = new SearchCondition(WTGroup.class,WTGroup.NAME, SearchCondition.EQUAL,groupName, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()){
				wtGroup = (WTGroup)qr.nextElement();
				if(wtGroup.getDn().indexOf("cn=public") >= 0){
					return wtGroup;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return wtGroup;
    }
    
    public static ArrayList getGroups(String groupName){
    	ArrayList list = new ArrayList();
    	try {
    		QuerySpec qs = new QuerySpec(WTGroup.class);
			SearchCondition sc = new SearchCondition(WTGroup.class,WTGroup.NAME, SearchCondition.LIKE,"%"+groupName+"%", false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()){
				WTGroup wtGroup = (WTGroup)qr.nextElement();
				list.add(wtGroup);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return list;
    }
    
    public static Map getRolePrincipalMap(TeamManaged teammanaged){
    	Team team;
		try {
			team = TeamHelper.service.getTeam(teammanaged);
			return team.getRolePrincipalMap();
		} catch (TeamException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}	
    	return null;
    }
    
    public static ArrayList getTeamRoles(TeamManaged teammanaged){
    	ArrayList list = new ArrayList();
    	try {
			Team team = TeamHelper.service.getTeam(teammanaged);			
			Vector v = team.getRoles();
			if(v == null)
				return new ArrayList();
			for (int i = 0; i < v.size(); i++) {				
				list.add(((Role)v.get(i)).getStringValue());				
			}
		} catch (TeamException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
    }

    public static ArrayList getTeamRoles(ContainerTeamManaged teammanaged){
    	ArrayList list = new ArrayList();
    	try {
			ContainerTeam team = ContainerTeamHelper.service.getContainerTeam(teammanaged);			
			Vector v = team.getRoles();
			
			if(v == null)
				return new ArrayList();
			for (int i = 0; i < v.size(); i++) {				
				list.add(((Role)v.get(i)).getStringValue());				
			}
		} catch (TeamException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
    }
    
    public static Vector getMembers(ContainerTeamManaged containerteammanaged){
    	try {
    		ContainerTeam containerteam = ContainerTeamHelper.service.getContainerTeam(containerteammanaged);
            return containerteam.getMembers();   
		} catch (Exception e) {
			e.printStackTrace();
		}         
        return new Vector();
    }
    
    public static String getEmailOfContainerTeamMember(ContainerTeamManaged containerteammanaged){
    	Vector members = getMembers(containerteammanaged);
    	String maillist = "";
    	if(members != null && members.size()>0){
    		for (int i = 0; i < members.size(); i++) {
    			WTPrincipalReference wtp = (WTPrincipalReference)members.get(i);
    			String mail = wtp.getEMail();
    			if(mail != null && !"".equals(mail))
    				maillist = maillist + mail + ";";				
			}
    		
    		if(maillist.endsWith(";"))
    			maillist = maillist.substring(0,maillist.length()-1);
    	}
    	
    	return maillist;
    }
    
	public static ContainerTeamManaged addContainerTeamMemebers(ContainerTeamManaged container, String roleName, ArrayList members){
		try{
			ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(container);
			Role role = Role.toRole(roleName);
			for (int i = 0; i < members.size(); i++) {
				WTPrincipal wtMember = (WTPrincipal)members.get(i);
				containerTeam.addPrincipal(role, wtMember);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
		return container;
	}
	
	/**
	 * @author Chris Wu
	 * Successed in Release 9.0, 9.1
	 * @param group
	 * @param userVector
	 * @return
	 * @throws WTException
	 */
	public static Vector getGroupMemberUsers(WTGroup group, Vector userVector)
			throws WTException {
		Enumeration groupMembers = group.members();
		while (groupMembers.hasMoreElements()) {
			WTPrincipal principal = (WTPrincipal) groupMembers.nextElement();
			if (principal instanceof WTUser) {
				WTUser user = (WTUser) principal;
				if (!userVector.contains(user))
					userVector.add(user);
			}
			if (principal instanceof WTGroup) {
				WTGroup thegroup = (WTGroup) principal;
				getGroupMemberUsers(thegroup, userVector);
			}
		}
		return userVector;
	}
	
	/**
	 * @author Chris Wu
	 * Successed in Release 9.0, 9.1
	 * @param role
	 * @param ct
	 * @return
	 * @throws WTException
	 */
	public static ArrayList getAllUsersByRole(Role role, ContainerTeam ct) throws WTException {
		ArrayList arraylist = new ArrayList();
		StandardContainerTeamService scts = StandardContainerTeamService.newStandardContainerTeamService();
		WTGroup wtgroup = scts.findContainerTeamGroup(ct, "roleGroups", role.toString());
		if (wtgroup != null) {
			Enumeration enumeration = OrganizationServicesHelper.manager.members(wtgroup, false, true);
			while(enumeration.hasMoreElements()){
				WTPrincipalReference wtprincipalreference = WTPrincipalReference
						.newWTPrincipalReference((WTPrincipal) enumeration
								.nextElement());
				arraylist.add(wtprincipalreference.getObject());
			}
		}
		return arraylist;
	}

	/**
	 * @author Chris Wu
	 * Successed in Release 9.0, 9.1
	 * @param role
	 * @param ct
	 * @return
	 * @throws WTException
	 */
	public static ArrayList getAllPrincipalsByRole(Role role, ContainerTeam ct) throws WTException {
		ArrayList arraylist = new ArrayList();
		StandardContainerTeamService scts = StandardContainerTeamService.newStandardContainerTeamService();
		WTGroup wtgroup = scts.findContainerTeamGroup(ct, "roleGroups", role.toString());
		if (wtgroup != null) {
			Enumeration enumeration = OrganizationServicesHelper.manager.members(wtgroup, false, true);
			while(enumeration.hasMoreElements()){
				WTPrincipalReference wtprincipalreference = WTPrincipalReference
						.newWTPrincipalReference((WTPrincipal) enumeration
								.nextElement());
				arraylist.add(wtprincipalreference);
			}
		}
		return arraylist;
	}
	
}

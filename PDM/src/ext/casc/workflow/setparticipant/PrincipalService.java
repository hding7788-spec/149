package ext.casc.workflow.setparticipant;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.util.WTException;

public interface PrincipalService {
	public Map<Role,List<WTUser>> getRoleAndUserByContainer(String oid) throws WTException;
	public Map<Role,List<WTUser>> getRoleAndUserByContainer(Object obj) throws WTException;
	public Map<Role,List<WTUser>> getUserByRole(String oid, List<String> roleList) throws WTException ;
	public void saveTeamRole(Map<String,List<String>> roleUser, String oid,Locale locale) throws WTException;
	public void saveTeamRole(String roleName,List<WTUser> userList, Object obj,Locale locale) throws WTException;
	public List<WTGroup> getUserGroups(WTUser user) throws WTException;
	public List<String> getUserRoles(WTUser user,String oid) throws WTException;
	public List<String> getRoles(WTUser user,String oid) throws WTException;
	public List<WTUser> getUser(String userName, Locale locale) throws WTException;
	public Map<Role,List<WTPrincipalReference>> getRoleAndUserByContainer(Object pbo,String roleKey) throws WTException;
}

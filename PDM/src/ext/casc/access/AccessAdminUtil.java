package ext.casc.access;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;

public class AccessAdminUtil {
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

	public static boolean isSysAdmin() throws WTException{
		WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
		WTGroup group = getGroupByName("系统管理员组");
		if(group == null) return false;
		return group.isMember(currentUser);
	}
	public static boolean isAdmin() throws WTException{
		WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
		WTGroup group = getGroupByName("Administrators");
		if(group == null) return false;
		return group.isMember(currentUser);
	}
	public static boolean isSecAdmin() throws WTException{
		WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
		WTGroup group = getGroupByName("安全管理员组");
		if(group == null) return false;
		return group.isMember(currentUser);
	}
	public static boolean isAuditAdmin() throws WTException{
		WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
		WTGroup group = getGroupByName("安全审计管理员组");
		if(group == null) return false;
		return group.isMember(currentUser);
	}

	public static boolean isJIMIGroup() throws WTException{
		WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
		WTGroup group = getGroupByName("机密组");
		if(group == null) return false;
		return group.isMember(currentUser);
	}
	public static boolean isMIMIGroup() throws WTException{
		WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
		WTGroup group = getGroupByName("秘密组");
		if(group == null) return false;
		return group.isMember(currentUser);
	}
	public static boolean isNEIBUGroup() throws WTException{
		WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
		WTGroup group = getGroupByName("内部组");
		if(group == null) return false;
		return group.isMember(currentUser);
	}

	public static boolean isGroup(String groupName) throws WTException{
		WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
		WTGroup group = getGroupByName(groupName);
		if(group == null) return false;
		return group.isMember(currentUser);
	}


}

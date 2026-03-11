package com.glaway.mpm.mpmresource.gznumber.number;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.glaway.mpm.mpmresource.gznumber.bean.PropertiesBean;

public class AdministrationHelper {
	public static String ADMINISTRATOR = "Admin";

	public static String getUser() {
		String result = "N.A.";
		WTUser wtuser = null;

		PropertiesBean pb = new PropertiesBean();
		String adminGroup = pb.getAdminGroup();

		try {
			wtuser = (WTUser) SessionHelper.manager.getPrincipal();
			result = wtuser.getName();
			ArrayList<WTGroup> userGroups = (ArrayList<WTGroup>) getUserGroups(wtuser);
			for (int i = 0; i < userGroups.size(); i++) {
				WTGroup wtgroup = userGroups.get(i);
				String strGroup = wtgroup.getName();
				if (strGroup.equals(adminGroup)) {
					result = ADMINISTRATOR;
					return result;
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static List<WTGroup> getUserGroups(WTUser user) {
		try {
			if (!RemoteMethodServer.ServerFlag) {
				return (List<WTGroup>) RemoteMethodServer.getDefault()
						.invoke("getUserGroupsRemote", AdministrationHelper.class.getName(), null,
								new Class[] { WTUser.class }, new Object[] { user });
			} else {
				return getUserGroupsRemote(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<WTGroup> getUserGroupsRemote(WTUser user) throws WTException {
		List<WTGroup> userGroupList = new ArrayList<WTGroup>();
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
		try {
			Enumeration em = OrganizationServicesHelper.manager.parentGroups(user);
			while (em.hasMoreElements()) {
				WTGroup group = (WTGroup) ((WTPrincipalReference) em.nextElement()).getObject();
				userGroupList.add(group);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return userGroupList;
	}
}

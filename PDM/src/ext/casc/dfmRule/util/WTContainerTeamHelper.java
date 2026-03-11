
package ext.casc.dfmRule.util;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.inf.team.StandardContainerTeamService;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.session.SessionServerHelper;
import wt.util.WTException;

/**
 * <pre>
 * 功能描述：容器团队相关方法
 * 使用方法：
 * 修改记录:（修改时间、修改人、修改内容、修改原因）
 * </pre>
 *
 * @author
 * @since 1.0
 */
public class WTContainerTeamHelper implements RemoteAccess{

	private static String CLASSNAME = ContainerTeamHelper.class.getName();

	private static Logger logger = LogR.getLogger(CLASSNAME);

	/**
	 * <pre>
	 * 功能描述: 查找指定容器团队的角色及人员，包括本地团队和共享团队
	 * 使用方法：
	 * 修改记录:（修改时间、修改人、修改内容、修改原因）
	 * </pre>
	 *
	 * @param wtc 容器
	 * @return
	 * @throws WTException
	 * @author
	 */
	public static HashMap findRolePrincipalMap(WTContainer wtc) throws WTException {

		if (wtc == null || !(wtc instanceof ContainerTeamManaged))
			return new HashMap();
		HashMap userMap = findAllRolePrincipalMap(wtc, /* filterNoUserRoles */true);
		return userMap;
	}

	/**
	 * <pre>
	 * 功能描述: 查找指定容器团队的角色及人员，包括本地团队和共享团队
	 * 使用方法：
	 * 修改记录:（修改时间、修改人、修改内容、修改原因）
	 * </pre>
	 *
	 * @param wtc 容器
	 * @param filterNoUserRoles 是否过滤无人员的角色
	 * @return
	 * @author
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static HashMap findAllRolePrincipalMap(WTContainer wtc, boolean filterNoUserRoles) {

		try {
			if (!RemoteMethodServer.ServerFlag) {
				String method = "findAllRolePrincipalMap";
				String klass = WTContainerTeamHelper.class.getName();
				Class[] types = { WTContainer.class, boolean.class};
				Object[] vals = { wtc, filterNoUserRoles };
				return (HashMap) RemoteMethodServer.getDefault().invoke(method, klass, null, types, vals);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		HashMap roleUserMap = findLocalTeamRolePrincipalMap(wtc, filterNoUserRoles);
		roleUserMap.putAll(findSharedTeamRolePrincipalMap(filterNoUserRoles));
		return roleUserMap;
	}

	/**
	 * <pre>
	 * 功能描述: 查找指定容器的本地团队中的角色人员 返回HashMap, Key值为RoleKey，Value为ArrayList<WTUser>
	 * 使用方法：
	 * 修改记录:（修改时间、修改人、修改内容、修改原因）
	 * </pre>
	 *
	 * @param wtc 容器
	 * @param filterNoUserRoles 是否过滤无人员的角色
	 * @return
	 * @author liul180434 Dec 14, 2012 9:44:21 AM
	 */
	public static HashMap findLocalTeamRolePrincipalMap(WTContainer wtc, boolean filterNoUserRoles) {

		HashMap roleUserMap = new HashMap();

		if (wtc == null || !(wtc instanceof ContainerTeamManaged))
			return roleUserMap;
		try {
			ContainerTeam localteam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtc);

			// 产品容器中的本地团队角色对象
			Enumeration enumeration = ContainerTeamHelper.service.findContainerTeamGroups(localteam,
				ContainerTeamHelper.ROLE_GROUPS);

			while (enumeration.hasMoreElements()) {
				ArrayList userList = new ArrayList();
				WTGroup roleGroup = (WTGroup) enumeration.nextElement();
				ArrayList principalList = localteam.getAllPrincipalsForTarget(Role.toRole(roleGroup.getName()));
				for (int i = 0; principalList != null && i < principalList.size(); i++) {
					WTPrincipal principal = (WTPrincipal) ((WTPrincipalReference) principalList.get(i)).getPrincipal();
					if (principal instanceof WTUser)
						userList.add(principal);
					else if (principal instanceof WTGroup) {
						Enumeration en = ((WTGroup) principal).members();
						while (en.hasMoreElements()) {
							Object o = en.nextElement();
							if (o instanceof WTUser && userList.indexOf(o) < 0)
								userList.add(o);
						}
					}
				}

				if (!filterNoUserRoles || userList.size() > 0)
					roleUserMap.put(roleGroup.getName(), userList);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return roleUserMap;
	}

	/**
	 * <pre>
	 * 功能描述: 查找系统共享团队中的角色人员 返回HashMap, Key值为RoleKey，Value为ArrayList<WTUser>
	 * 使用方法：
	 * 修改记录:（修改时间、修改人、修改内容、修改原因）
	 * </pre>
	 *
	 * @param filterNoUserRoles 是否过滤无人员的角色
	 * @return
	 */
	public static HashMap findSharedTeamRolePrincipalMap(boolean filterNoUserRoles) {

		HashMap roleUserMap = new HashMap();
		try {
			List sharedTeams = findSharedTeams();
			for (int i = 0, m = sharedTeams.size(); i < m; i++) {
				ContainerTeam sharedTeam = (ContainerTeam) sharedTeams.get(i);

				// 产品容器中的本地团队角色对象
				Enumeration enumeration = ContainerTeamHelper.service.findContainerTeamGroups(sharedTeam,
					ContainerTeamHelper.ROLE_GROUPS);

				while (enumeration.hasMoreElements()) {
					ArrayList userList = new ArrayList();
					WTGroup roleGroup = (WTGroup) enumeration.nextElement();
					ArrayList principalList = sharedTeam.getAllPrincipalsForTarget(Role.toRole(roleGroup.getName()));
					for (int j = 0; principalList != null && j < principalList.size(); j++) {
						WTPrincipal principal = (WTPrincipal) ((WTPrincipalReference) principalList.get(j))
							.getPrincipal();
						if (principal instanceof WTUser)
							userList.add(principal);
						else if (principal instanceof WTGroup) {
							Enumeration en = ((WTGroup) principal).members();
							while (en.hasMoreElements()) {
								Object o = en.nextElement();
								if (o instanceof WTUser && userList.indexOf(o) < 0)
									userList.add(o);
							}
						}
					}
					if (!filterNoUserRoles || userList.size() > 0)
						roleUserMap.put(roleGroup.getName(), userList);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return roleUserMap;
	}

	/**
	 * <pre>
	 * 功能描述: 查找系统共享团队中的角色人员 返回HashMap, Key值为RoleKey，Value为ArrayList<WTUser>
	 * 使用方法：
	 * 修改记录:（修改时间、修改人、修改内容、修改原因）
	 * </pre>
	 *
	 * @return
	 * @throws WTException
	 * @author liul180434 Dec 14, 2012 9:42:55 AM
	 */
	public static List findSharedTeams() throws WTException {

		List allSharedTeams = new ArrayList();
		Enumeration orgContainers = findOrgContainers();
		while (orgContainers.hasMoreElements()) {
			Object[] orgContainerArray = (Object[]) orgContainers.nextElement();
			OrgContainer orgContainer = (OrgContainer) orgContainerArray[0];
			Enumeration sharedTeams = ContainerTeamHelper.service.findSharedTeams(orgContainer, false);
			while (sharedTeams.hasMoreElements()) {
				Object[] sharedTeamArray = (Object[]) sharedTeams.nextElement();
				allSharedTeams.add(sharedTeamArray[0]);
			}
		}
		return allSharedTeams;
	}

	/**
	 * <pre>
	 * 功能描述: 判断用户是否担任产品团队的某个角色
	 * 使用方法：
	 * 修改记录:（修改时间、修改人、修改内容、修改原因）
	 * </pre>
	 *
	 * @param user
	 * @param role
	 * @param container
	 * @return
	 * @author liul180434 Dec 20, 2012 7:15:31 PM
	 */
	public static boolean isMemberOfContainerRole(WTUser user, Role role, WTContainer container)
	{
		boolean access = true;
		boolean flag = false;
		try{
			access = SessionServerHelper.manager.setAccessEnforced(false);
			ContainerTeam team = (ContainerTeam)((ContainerTeamManaged)container).getContainerTeamReference().getObject();
			WTGroup wtgroup=ContainerTeamHelper.service.findContainerTeamGroup(team, "roleGroups", role.toString());
			if (wtgroup!=null&&wtgroup.isMember(user))
				return Boolean.TRUE;
		}
		catch (Exception e) {
			flag = false;
		}
		finally{
			SessionServerHelper.manager.setAccessEnforced(access);
		}
		return flag;
	}


	/**
	 * <pre>
	 * 功能描述: 根据团队角色得到其中的用户
	 * 使用方法：
	 * 修改记录:（修改时间、修改人、修改内容、修改原因）
	 * </pre>
	 *
	 * @param role
	 * @param ct
	 * @return
	 * @throws WTException
	 * @author liul180434 Dec 20, 2012 7:18:37 PM
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
	 * <pre>
	 * 功能描述: 取所有的组织容器
	 * 使用方法：
	 * 修改记录:（修改时间、修改人、修改内容、修改原因）
	 * </pre>
	 *
	 * @return
	 * @throws WTException
	 * @throws QueryException
	 * @author liul180434 Dec 14, 2012 9:41:55 AM
	 */
	private static Enumeration findOrgContainers() throws WTException, QueryException {

		QuerySpec qs = new QuerySpec();
		qs.appendClassList(OrgContainer.class, true);
		QueryResult qr = PersistenceServerHelper.manager.query(qs);
		Enumeration orgContainers = qr.getEnumeration();
		return orgContainers;
	}

}

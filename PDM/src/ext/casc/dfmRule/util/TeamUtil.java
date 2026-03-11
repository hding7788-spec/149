package ext.casc.dfmRule.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import ext.casc.dfmRule.common.Constants;

import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.inf.team.StandardContainerTeamService;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.util.WTException;


/**
 * @author
 *
 */
public class TeamUtil {

	/**
	 * 从产品团队中获取指定角色的成员
	 *
	 * @param teammanaged
	 * @param roleName
	 * @return
	 * @throws WTException
	 */
	public static List<WTPrincipal> getRoleMember(ContainerTeamManaged teamManaged, String roleName) throws WTException {
		List<WTPrincipal> principals = null;
		if (roleName != null && roleName.length() > 0) {
			Role role = Role.toRole(roleName);
			principals = getRoleMember(teamManaged, role);
		} else {
			principals = Collections.emptyList();
		}

		return principals;
	}

	/**
	 * 从产品团队中获取指定角色的成员
	 *
	 * @param teammanaged
	 * @param role
	 * @return
	 * @throws WTException
	 */
	public static List<WTPrincipal> getRoleMember(ContainerTeamManaged teamManaged, Role role) throws WTException {
		List<WTPrincipal> principals = null;

		ContainerTeam team = ContainerTeamHelper.service.getContainerTeam(teamManaged);
		StandardContainerTeamService teamService = StandardContainerTeamService.newStandardContainerTeamService();

		WTGroup wtgroup = teamService.findContainerTeamGroup(team, ContainerTeamHelper.ROLE_GROUPS, role.toString());
		if (wtgroup != null) {
			principals = new ArrayList<WTPrincipal>();
			Enumeration<?> enumeration = wtgroup.members();
			while (enumeration.hasMoreElements()) {
				Object obj = enumeration.nextElement();
				if (obj instanceof WTPrincipal) {
					principals.add((WTPrincipal) obj);
				}
			}
		} else {
			principals = Collections.emptyList();
		}

		return principals;
	}

	/**
	 * 判断当前用户是否产品经理或者存储库经理
	 * reconstruction by clli 2017-06-01
	 *
	 * @param container
	 * @return
	 * @throws WTException
	 */
	public static boolean isManager(WTContainer container) throws WTException {
		boolean isMager = false;

		List<WTPrincipal> principals = null;
		if (container instanceof WTLibrary) {
			principals = getRoleMember((ContainerTeamManaged) container, Constants.LIBRARY_MANAGER);
		} else if (container instanceof PDMLinkProduct) {
			principals = getRoleMember((ContainerTeamManaged) container, Constants.PRODUCT_MANAGER);
		} else {
			principals = Collections.emptyList();
		}

		WTPrincipal currentUser = SessionHelper.getPrincipal();
		if (principals.contains(currentUser)) {
			isMager = true;
		}

		return isMager;
	}

}

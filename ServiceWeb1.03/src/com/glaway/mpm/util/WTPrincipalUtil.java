package com.glaway.mpm.util;

import java.util.ArrayList;
import java.util.Enumeration;

import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.team.ContainerTeam;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.project.Role;
import wt.util.WTException;
import wt.util.WTRuntimeException;

import com.glaway.mpm.mpmresource.Constants;

public class WTPrincipalUtil {
	private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.QIAN_LONG_CONFIG_PATH);

	/**
	 * 根据群组名称获取群组
	 * 
	 * @author qianlong
	 * @date 2013-6-3
	 * @param group
	 * @return
	 * @throws WTException
	 * 
	 */
	@SuppressWarnings("deprecation")
	public static WTGroup getGroupByName(String groupName) throws WTException {
		return OrganizationServicesHelper.manager.getGroup(groupName);
	}

	/**
	 * 获取群组的成员
	 * 
	 * @author qianlong
	 * @date 2013-6-3
	 * @param group
	 * @return
	 * @throws WTException
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static Enumeration getAllMembers(WTGroup group) throws WTException {
		return OrganizationServicesHelper.manager.members(group, false);
	}

	/**
	 * 获取群组下面的人员
	 * 
	 * @author qianlong
	 * @date 2013-6-3
	 * @param group
	 * @return
	 * @throws WTException
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static Enumeration getChildUser(WTGroup group) throws WTException {
		return OrganizationServicesHelper.manager.members(group);
	}

	/**
	 * 获取零件所在产品的工艺主师
	 * 
	 * @author lbzhang
	 * @date 2013-6-6
	 * @param part
	 * @return
	 * @throws WTRuntimeException
	 * @throws WTException
	 * 
	 */
	public static WTPrincipalReference getMainDivisher(WTPart part) throws WTRuntimeException, WTException {
		WTContainerRef containerRef = part.getContainerReference();
		WTPrincipal mainDivishPrin = Util.getWTContainerOfRole(containerRef.toString(), Constants.DESIGNERSYSTEM15);
		WTPrincipalReference mainDivish = WTPrincipalReference.newWTPrincipalReference(mainDivishPrin);// 工艺主师
		return mainDivish;
	}

	/**
	 * 获取上下文中某个角色中的人员
	 * 
	 * @author qianlong
	 * @date 2013-7-1
	 * @param container
	 * @throws WTException
	 */
	public static Enumeration<WTPrincipalReference> getPeopleByRoleInContainer(WTContainer container, String roleName)
			throws WTException {
		ContainerTeam containerTeam = WorkflowUtil.getContainerTeam(container);
		Role role = Role.toRole(roleName);
		return containerTeam.getPrincipalTarget(role);
	}

	/**
	 * 获取工艺部计划员
	 * 
	 * @author qianlong
	 * @throws WTException
	 * @date 2013-7-1
	 */
	@SuppressWarnings("deprecation")
	public static WTPrincipalReference getGONGYIBUJIHUAYUAN(WTContainer container) throws WTException {
		// 如果工艺计划员存在就发个工艺计划员
		// 如果工艺计划员不存在就发给‘周华’
		WTPrincipalReference principalReference = null;
		Enumeration<WTPrincipalReference> enumeration = null;
		if (null != container) {
			enumeration = getPeopleByRoleInContainer(container, Constants.GONGYIBUJIHUAYUAN);
		}
		if (null == enumeration || !enumeration.hasMoreElements()) {
			principalReference = WTPrincipalReference.newWTPrincipalReference(OrganizationServicesHelper.manager
					.getPrincipal(propertiesUtil.getProperty("default-GONGYIBUJIHUAYUAN")));
		} else {
			principalReference = enumeration.nextElement();
		}
		return principalReference;
	}
	
	/**
	 * 获取当前使用者所在的专业组组长群组
	 * @author lbzhang
	 * @date  2013-7-20
	 * @param user
	 * @return
	 * @throws WTException
	 *
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<String> getUserInGroupLeaderName(WTPrincipal principal) throws WTException{
		WTUser user = (WTUser)principal;
		ArrayList<String> groupNameList = new ArrayList<String>();
		Enumeration localEnumeration = user.parentGroups(false);
		while(localEnumeration.hasMoreElements()){
			WTPrincipalReference principalRef = (WTPrincipalReference) localEnumeration.nextElement();
			WTGroup localWTGroup = (WTGroup)principalRef.getPrincipal();
			if((!localWTGroup.isInternal()) && (!(localWTGroup instanceof WTOrganization))){
				String groupName = localWTGroup.getName();
				if(groupName.endsWith("组长")){
					groupName = groupName.substring(0, groupName.length() - 2);
					groupNameList.add(groupName);
				}
			}
		}
		return groupNameList;
	}
	
	/**
	 * 获取当前用户所在的专业组
	 * @author lbzhang
	 * @date  2013-7-24
	 * @param principal
	 * @return
	 * @throws WTException
	 *
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<String> getUserInGorupName(WTPrincipal principal) throws WTException{
		WTUser user = (WTUser)principal;
		ArrayList<String> groupNameList = new ArrayList<String>();
		Enumeration localEnumeration = user.parentGroups(false);
		while(localEnumeration.hasMoreElements()){
			WTPrincipalReference principalRef = (WTPrincipalReference) localEnumeration.nextElement();
			WTGroup localWTGroup = (WTGroup)principalRef.getPrincipal();
			if((!localWTGroup.isInternal()) && (!(localWTGroup instanceof WTOrganization))){
				groupNameList.add(localWTGroup.getName());
			}
		}
		return groupNameList;
	}

	public static void main(String[] args) {
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		methodServer.setUserName("ql");
		methodServer.setPassword("1");
		try {
			System.out.println(getGONGYIBUJIHUAYUAN(WTContainerUtil.getContainerByName("科研工装设计库")).getName());
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

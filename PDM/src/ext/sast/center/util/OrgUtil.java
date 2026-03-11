package ext.sast.center.util;

import com.ptc.netmarkets.group.NmGroup;
import com.ptc.windchill.uwgm.common.container.OrganizationHelper;
import wt.inf.container.*;
import wt.org.*;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class OrgUtil {

	/**
	 * 获取当前用户所在的组织
	 *
	 * @return OrgContainer 组织
	 * @throws WTException
	 */
	public static OrgContainer getOrgContainer() throws WTException {
		WTPrincipal currentUser = SessionHelper.getPrincipal();
		WTOrganization wtOrganization = OrganizationServicesHelper.manager
				.getOrganization(currentUser);
		OrgContainer orgContainer = WTContainerHelper.service.getOrgContainer(wtOrganization);
		return orgContainer;
	}



	/**
	 * 获取149组织下的某个组
	 *
	 * @param obj
	 * @return List 组集合
	 * @throws WTException
	 */
	public static WTGroup getOrgGroupByName(String groupName) throws WTException {
		WTOrganization org = OrganizationHelper.getOrganizationByName("149");
		OrgContainer orgContainer = WTContainerHelper.service.getOrgContainer(org);
		PrincipalSpec principalspec = new PrincipalSpec();
		try {
			principalspec.setContainerReference(newWTContainerRef(orgContainer));
			principalspec.setPerformLookup(false);
			principalspec.setIncludeAllServices(false);
		} catch(WTPropertyVetoException wtpropertyvetoexception) {
			throw new WTException();
		}
		DirectoryContextProvider adirectorycontextprovider[] = getPublicContextProviders(principalspec);
		DirectoryContextProvider adirectorycontextprovider1[] = adirectorycontextprovider;
		int i = adirectorycontextprovider1.length;
		for(int j = 0; j < i; j++) {
			DirectoryContextProvider directorycontextprovider = adirectorycontextprovider1[j];
			directorycontextprovider.setInternalGroupsSearchCriteria(null);
		}
		Enumeration groups = findLikeGroups(groupName, adirectorycontextprovider[0]);
		if(groups.hasMoreElements()) {
			return (WTGroup) groups.nextElement();
		}
		return null;
	}

	public static DirectoryContextProvider[] getDirectoryContextProvider(OrgContainer org) throws WTException {
		PrincipalSpec principalspec = new PrincipalSpec();
		try {
			principalspec.setContainerReference(newWTContainerRef(org));
			principalspec.setPerformLookup(false);
			principalspec.setIncludeAllServices(false);
		} catch(WTPropertyVetoException wtpropertyvetoexception) {
			throw new WTException();
		}
		DirectoryContextProvider adirectorycontextprovider[] = getPublicContextProviders(principalspec);
		return adirectorycontextprovider;
	}

	/**
	 * 获取指定组织下的所有组
	 *
	 * @param obj
	 * @return List 组集合
	 * @throws WTException
	 */
	public static List getNodes(Object obj) throws WTException {
		ArrayList arraylist = new ArrayList();
		if (obj instanceof OrgContainer) {
			OrgContainer orgcontainer = (OrgContainer) obj;
			PrincipalSpec principalspec = new PrincipalSpec();
			try {
				principalspec
						.setContainerReference(newWTContainerRef(orgcontainer));
				principalspec.setPerformLookup(false);
				principalspec.setIncludeAllServices(false);
			} catch (WTPropertyVetoException wtpropertyvetoexception) {
				throw new WTException();
			}
			DirectoryContextProvider adirectorycontextprovider[] = getPublicContextProviders(principalspec);
			DirectoryContextProvider adirectorycontextprovider1[] = adirectorycontextprovider;
			int i = adirectorycontextprovider1.length;
			for (int j = 0; j < i; j++) {
				DirectoryContextProvider directorycontextprovider = adirectorycontextprovider1[j];
				directorycontextprovider.setInternalGroupsSearchCriteria(null);
			}

			WTGroup wtgroup1;
			for (Enumeration enumeration1 = findLikeGroups("*",
					adirectorycontextprovider[0]); enumeration1
					.hasMoreElements(); arraylist.add(wtgroup1))
				wtgroup1 = (WTGroup) enumeration1.nextElement();

		} else if (obj instanceof WTGroup) {
			WTGroup wtgroup = (WTGroup) obj;
			Enumeration enumeration = OrganizationServicesHelper.manager
					.members(wtgroup, false);
			Object obj1 = null;
			for (; enumeration.hasMoreElements(); arraylist.add(obj1)) {
				WTPrincipal wtprincipal = (WTPrincipal) enumeration
						.nextElement();
				wtprincipal = OrganizationServicesHelper.manager
						.inflate(wtprincipal);
				if (wtprincipal instanceof WTUser) {
					obj1 = (WTUser) wtprincipal;
					continue;
				}
				if (wtprincipal instanceof WTGroup)
					obj1 = ((WTGroup) wtprincipal).getOrganization() != null ? ((Object) (NmGroup
							.getNmGroup((WTGroup) wtprincipal)))
							: ((Object) (wtprincipal));
			}

		}
		return arraylist;
	}

	protected static WTContainerRef newWTContainerRef(WTContainer wtcontainer)
			throws WTException {
		return WTContainerRef.newWTContainerRef(wtcontainer);
	}

	protected static DirectoryContextProvider[] getPublicContextProviders(
			PrincipalSpec principalspec) throws WTException {
		return WTContainerHelper.service
				.getPublicContextProviders(principalspec);
	}

	protected static Enumeration findLikeGroups(String s,
			DirectoryContextProvider directorycontextprovider)
			throws WTException {
		return OrganizationServicesHelper.manager.findLikeGroups(s,
				directorycontextprovider);
	}
}

package com.glaway.mpm.print.validator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import wt.change2.WTChangeOrder2;
import wt.fc.WTReference;
import wt.folder.Cabinet;
import wt.folder.Folder;
import wt.folder.SubFolder;
import wt.inf.container.OrgContainer;
import wt.inf.container.PrincipalSpec;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.project._Role;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.constants.ProcessPlanConstants;
import com.glaway.mpm.print.util.MBAUtil;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.netmarkets.group.NmGroup;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;

import ext.casc.util.CommonUtil;
import ext.casc.util.IBAUtility;

public class PrintManagerValidator extends DefaultSimpleValidationFilter {

	@Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
    	WTReference ref = criteria.getContextObject();
    	String id = key.getComponentID();
        try {
			if (id.indexOf("filePrintRequest") > -1 ) {
				if(ref == null) {
					if (isGroupMember("型号工艺师")
							|| isGroupMember("专业主任师")) {
						return UIValidationStatus.ENABLED;
					}
				} else {
					Object object = ref.getObject();
					WTContainer container = null;
					if (object instanceof MPMProcessPlan) {
						MPMProcessPlan processPlan = (MPMProcessPlan) object;

						String processNumber = CommonUtil.objectToString(MBAUtil.getValue(processPlan, ProcessPlanConstants.MBA_PROCESSNUMBER));
		        		if (processNumber.endsWith("(无效)")) {
		        			return UIValidationStatus.HIDDEN;
		        		}

						String processCategory = CommonUtil.objectToString(MBAUtil.getValue(processPlan, ProcessPlanConstants.MBA_PROCESSCATEGORY));
						if (ProcessPlanConstants.DOCTYPE_ZS.equals(processCategory)
								|| ProcessPlanConstants.DOCTYPE_TY.equals(processCategory)
								|| ProcessPlanConstants.DOCTYPE_GYZTB.equals(processCategory)) {
							String state = processPlan.getState().getState().getDisplay(Locale.CHINA);
							if (!"已批准".equals(state)) {
								return UIValidationStatus.HIDDEN;
							}
							container = processPlan.getContainer();
						}
					} else if (object instanceof WTChangeOrder2) {
						WTChangeOrder2 ecn = (WTChangeOrder2) object;
						String type = IBAUtility.getSoftType(ecn);
						if (ProcessPlanConstants.SOFT_PROCESSECFORM.equals(type)) {
							String state = ecn.getState().getState().getDisplay(Locale.CHINA);
							if (!"已批准".equals(state)) {
								return UIValidationStatus.HIDDEN;
							}
							container = ecn.getContainer();
						}
					}/* else if (object instanceof WTDocument) {
						WTDocument doc = (WTDocument) object;
						String type = IBAUtility.getSoftType(doc);
						if (Constants.OBJECT_TYPE_PROCESS_PROGRAM.equals(type)
								|| Constants.OBJECT_TYPE_PROCESS_NOTICE.equals(type)) {
							String state = doc.getState().getState().getDisplay(Locale.CHINA);
							if (!"已批准".equals(state)) {
								return UIValidationStatus.HIDDEN;
							}
							container = doc.getContainer();
						}
					}*/else if(object instanceof PDMLinkProduct){
						PDMLinkProduct product = (PDMLinkProduct) object;
						container = product.getContainer();
					}else if(object instanceof SubFolder){
						SubFolder subFolder = (SubFolder) object;
						container = subFolder.getContainer();
					}else if(object instanceof Folder){
						Folder folder = (Folder) object;
						container = folder.getContainer();
					}else if(object instanceof Cabinet){
						Cabinet folder = (Cabinet) object;
						container = folder.getContainer();
					}

					//判断是否型号工艺师
					if(container != null){
						if (isRoleMember(container, "ZHURENGONGYISHI")) {
							return UIValidationStatus.ENABLED;
						}
					}
				}
			} else if (id.indexOf("printFileDistribute") > -1 ) {
				if (isGroupMember("现行文件调拨员")) {
					return UIValidationStatus.ENABLED;
				}
			} else if (id.indexOf("zxFilePrint") > -1 ) {
				if(ref == null) {
					if (isGroupMember("型号工艺师")
							|| isGroupMember("工艺员")
							|| isGroupMember("专业主任师")) {
						return UIValidationStatus.ENABLED;
					}
				} else {
					Object object = ref.getObject();
					WTContainer container = null;
					if (object instanceof MPMProcessPlan) {
						MPMProcessPlan processPlan = (MPMProcessPlan) object;

						String processNumber = CommonUtil.objectToString(MBAUtil.getValue(processPlan, ProcessPlanConstants.MBA_PROCESSNUMBER));
		        		if (processNumber.endsWith("(无效)")) {
		        			return UIValidationStatus.HIDDEN;
		        		}

						container = processPlan.getContainer();
					} else if (object instanceof WTChangeOrder2) {
						WTChangeOrder2 ecn = (WTChangeOrder2) object;
						container = ecn.getContainer();
					}/* else if (object instanceof WTDocument) {
						WTDocument doc = (WTDocument) object;
						container = doc.getContainer();
					}*/
					if (container != null) {
//						if (isRoleMember(container, "ZHURENGONGYISHI")) {
							return UIValidationStatus.ENABLED;
//						}
					}
				}
			} else if (id.indexOf("recoverApply") > -1 ) {
				if (isGroupMember("资料员_")) {
					return UIValidationStatus.ENABLED;
				}
			} else if (id.indexOf("tyPrintFileManage") > -1 ) {
				return UIValidationStatus.ENABLED;
			} else if (id.indexOf("zxPrintFileManage") > -1 ) {
				return UIValidationStatus.ENABLED;
			} else if (id.indexOf("qrCodeManage") > -1 ) {
				WTPrincipal principal = SessionHelper.manager.getPrincipal();
				WTPrincipal administrator = SessionHelper.manager.getAdministrator();
				if (administrator.equals(principal)) {
					return UIValidationStatus.ENABLED;
				}
			}
        } catch (WTException e) {
        	e.printStackTrace();
        }

        return UIValidationStatus.HIDDEN;
    }

    public static boolean isRoleMember(WTContainer wtContainer, String roleKey) throws WTException {
		boolean flag = false;
		WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
		if(wtContainer instanceof ContainerTeamManaged){
			ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);
	        Role role = _Role.toRole(roleKey);
	        if (role == null) {
	            return flag;
	        }
	        ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
	        for (WTPrincipalReference reference : arrayList) {
	            Object object2 = reference.getPrincipal();
	            if (object2 instanceof WTUser) {
	                WTUser user = (WTUser) object2;
	                if (user.getName().equals(currentUser.getName())) {
	                    flag = true;
	                }
	            } else if (object2 instanceof WTGroup) {
	            	WTGroup group = (WTGroup) object2;
	            	if (group.isMember(currentUser)) {
	            		flag = true;
	            	}
	            }
	        }
		}
        return flag;
	}

    public static boolean isGroupMember(String groupName) throws WTException {
        boolean inGroup = false;
        WTUser curentUser = (WTUser) SessionHelper.getPrincipal();
        OrgContainer orgContainer = getOrgContainer();
        List<Object> list = getNodes(orgContainer);
        if (list != null) {
            WTGroup group = null;
            for (int i = 0; i < list.size(); i++) {
                Object object = list.get(i);
                if (object instanceof WTGroup) {
                    group = (WTGroup) object;
                    String name = group.getName();
                    if (name.indexOf(groupName) > -1) {
                        Enumeration<?> enumeration = group.members();
                        while (enumeration.hasMoreElements()) {
                            Object userObject = enumeration.nextElement();
                            if (userObject instanceof WTUser) {
                            	if (curentUser.equals(userObject)) {
									inGroup = true;
									break;
								}
                            }
                        }
                    }
                }
            }
        }
        return inGroup;
    }

    /**
     * 获取当前用户所在的组织
     *
     * @return OrgContainer 组织
     * @throws WTException
     */
    public static OrgContainer getOrgContainer() throws WTException {
        WTPrincipal currentUser = SessionHelper.getPrincipal();
        WTOrganization wtOrganization = OrganizationServicesHelper.manager.getOrganization(currentUser);
        OrgContainer orgContainer = WTContainerHelper.service.getOrgContainer(wtOrganization);
        return orgContainer;
    }

    /**
     * 获取指定组织下的所有组
     *
     * @param obj
     * @return List 组集合
     * @throws WTException
     */
    private static List<Object> getNodes(Object obj) throws WTException {
        ArrayList<Object> arraylist = new ArrayList<Object>();
        if (obj instanceof OrgContainer) {
            OrgContainer orgcontainer = (OrgContainer) obj;
            PrincipalSpec principalspec = new PrincipalSpec();
            try {
                principalspec.setContainerReference(newWTContainerRef(orgcontainer));
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
            for (Enumeration<?> enumeration1 = findLikeGroups("*", adirectorycontextprovider[0]); enumeration1
                    .hasMoreElements(); arraylist.add(wtgroup1))
                wtgroup1 = (WTGroup) enumeration1.nextElement();

        } else if (obj instanceof WTGroup) {
            WTGroup wtgroup = (WTGroup) obj;
            Enumeration<?> enumeration = OrganizationServicesHelper.manager.members(wtgroup, false);
            Object obj1 = null;
            for (; enumeration.hasMoreElements(); arraylist.add(obj1)) {
                WTPrincipal wtprincipal = (WTPrincipal) enumeration.nextElement();
                wtprincipal = OrganizationServicesHelper.manager.inflate(wtprincipal);
                if (wtprincipal instanceof WTUser) {
                    obj1 = wtprincipal;
                    continue;
                }
                if (wtprincipal instanceof WTGroup)
                    obj1 = ((WTGroup) wtprincipal).getOrganization() != null ? ((Object) (NmGroup
                            .getNmGroup((WTGroup) wtprincipal))) : ((Object) (wtprincipal));
            }

        }
        return arraylist;
    }

    protected static Enumeration<?> findLikeGroups(String s, DirectoryContextProvider directorycontextprovider)
            throws WTException {
        return OrganizationServicesHelper.manager.findLikeGroups(s, directorycontextprovider);
    }

    protected static WTContainerRef newWTContainerRef(WTContainer wtcontainer)
            throws WTException {
        return WTContainerRef.newWTContainerRef(wtcontainer);
    }

    protected static DirectoryContextProvider[] getPublicContextProviders(PrincipalSpec principalspec)
            throws WTException {
        return WTContainerHelper.service.getPublicContextProviders(principalspec);
    }
}

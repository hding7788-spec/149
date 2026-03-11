package ext.ases.techMaterial.filter;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import ext.ases.techMaterial.TechnicsMaterial;
import ext.ases.techMaterial.constance.TechnicsMaterialConstance;
import wt.folder.Cabinet;
import wt.folder.SubFolder;
import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.session.SessionHelper;

import java.util.ArrayList;
import java.util.Enumeration;

public class TechnicsMaterialFilter extends DefaultSimpleValidationFilter {

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		String objectType = key.getObjectType();
		String actionName = key.getComponentID();
		try {
			WTUser curentuser = (WTUser) SessionHelper.getPrincipal();
			if ("technicsMaterial".equals(objectType) || "technicsMaterialEntries".equals(objectType)) {
				if ("createTechnicsMaterial".equals(actionName) || "addDictionary".equals(actionName) || "deleteDictionary".equals(actionName) || "importTechnicsMaterialEntries".equals(actionName)
						|| "exportStandardInfo".equals(actionName) || "exportEleComponentsInfo".equals(actionName) || "exportNonMetallicInfo".equals(actionName)
						|| "exportCompoundMaterialInfo".equals(actionName) || "exportMetallicInfo".equals(actionName)|| "exportEleMachineInfo".equals(actionName)|| "exporExpDeviceInfo".equals(actionName)
						|| "deleteTechMaterial".equals(actionName)
						|| "deleteTechMaterialEntries".equals(actionName)) {
					Object object = criteria.getContextObject().getObject();
					Object pageObj = criteria.getPageObject().getObject();
					if (object instanceof WTLibrary || object instanceof SubFolder || object instanceof TechnicsMaterial||object instanceof Cabinet) {
						String containerName = "";
						WTContainer library = null;
						if (object instanceof WTLibrary) {
							library = (WTLibrary) object;
							containerName = library.getName();
						} else if (object instanceof SubFolder) {
							SubFolder sub = (SubFolder) object;
							library = sub.getContainer();
							containerName = sub.getContainerName();
						} else if (object instanceof TechnicsMaterial) {
							TechnicsMaterial tm = (TechnicsMaterial) object;
							library = tm.getContainer();
							containerName = tm.getContainerName();
						} else if (object instanceof Cabinet) {
							Cabinet cab = (Cabinet) object;
							library = cab.getContainer();
							containerName = cab.getContainerName();
						}
						if (!(library instanceof WTLibrary)) {
							return UIValidationStatus.HIDDEN;
						}
						if (TechnicsMaterialConstance.TECHNICS_MATERIAL_LIBRARY_NAME.equals(containerName)) {
							ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) library);
							Role role = Role.toRole("BIAOZHUNHUASHI");
							if (role == null) {
								return UIValidationStatus.DISABLED;
							}
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 instanceof WTUser) {
									WTUser user = (WTUser) object2;
									if (user.getName().equals(curentuser.getName())) {
										return UIValidationStatus.ENABLED;
									}
								} else if (object2 instanceof WTGroup) {
									WTGroup group = (WTGroup) object2;
									if (group.isMember(curentuser)) {
										return UIValidationStatus.ENABLED;
									}
								}
							}
						} else {
							return UIValidationStatus.DISABLED;
						}
					}
				}
			} else if ("customProduct".equals(objectType)) {
				if ("customCreateProduct".equals(actionName)) {
					WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
					Enumeration groups = currentUser.parentGroups(false);
					while (groups.hasMoreElements()) {
						WTPrincipalReference principalRef = (WTPrincipalReference) groups.nextElement();
						WTGroup group = (WTGroup) principalRef.getPrincipal();
						if ("主任工艺师".equals(group.getName())) {
							return UIValidationStatus.ENABLED;
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return UIValidationStatus.HIDDEN;
	}

}

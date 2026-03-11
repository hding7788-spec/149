package ext.casc.importdata.validator;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import wt.fc.PersistenceHelper;
import wt.inf.container.WTContainer;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.util.ArrayList;



public class ImportActionValidator extends DefaultSimpleValidationFilter {
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		boolean access = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WTUser curentuser = (WTUser) SessionHelper.getPrincipal();
			Object object = criteria.getContextObject().getObject();
			String actionName = key.getComponentID();
			if(object instanceof PDMLinkProduct) {
				if("productstructure".equals(actionName)) {
					if("niyongjun".equals(curentuser.getName())) {
						return UIValidationStatus.ENABLED;
					}
				}
				PDMLinkProduct container = (PDMLinkProduct) object;
				container = (PDMLinkProduct) PersistenceHelper.manager.refresh(container);
				System.out.println("------输入导入容器类:" + container.getName());
				ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
				Role role = Role.toRole("PRODUCT MANAGER");
				if(role == null) {
					return UIValidationStatus.HIDDEN;
				}
				ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
				for(WTPrincipalReference reference : arrayList) {
					Object object2 = reference.getPrincipal();
					if(object2 instanceof WTUser) {
						WTUser user = (WTUser) object2;
						if(user.getName().equals(curentuser.getName())) {
							return UIValidationStatus.ENABLED;
						}
					} else if(object2 instanceof WTGroup) {
						WTGroup group = (WTGroup) object2;
						if(group.isMember(curentuser)) {
							return UIValidationStatus.ENABLED;
						}

					}
				}
				role = Role.toRole("ZHURENGONGYISHI");
				if(role == null) {
					return UIValidationStatus.HIDDEN;
				}
				arrayList = containerTeam.getAllPrincipalsForTarget(role);
				for(WTPrincipalReference reference : arrayList) {
					Object object2 = reference.getPrincipal();
					if(object2 instanceof WTUser) {
						WTUser user = (WTUser) object2;
						if(user.getName().equals(curentuser.getName())) {
							return UIValidationStatus.ENABLED;
						}
					} else if(object2 instanceof WTGroup) {
						WTGroup group = (WTGroup) object2;
						if(group.isMember(curentuser)) {
							return UIValidationStatus.ENABLED;
						}

					}
				}
			} else if(object instanceof WTLibrary) {
				WTLibrary library = (WTLibrary) object;
				WTContainer container = library;
				System.out.println("------输入导入容器类:" + container.getName());
				ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) container);
				Role role = Role.toRole("LIBRARY MANAGER");
				if(role == null) {
					return UIValidationStatus.HIDDEN;
				}
				ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
				for(WTPrincipalReference reference : arrayList) {
					Object object2 = reference.getPrincipal();
					if(object2 instanceof WTUser) {
						WTUser user = (WTUser) object2;
						if(user.getName().equals(curentuser.getName())) {
							return UIValidationStatus.ENABLED;
						}
					} else if(object2 instanceof WTGroup) {
						WTGroup group = (WTGroup) object2;
						if(group.isMember(curentuser)) {
							return UIValidationStatus.ENABLED;
						}

					}
				}
				role = Role.toRole("ZHURENGONGYISHI");
				if(role == null) {
					return UIValidationStatus.HIDDEN;
				}
				arrayList = containerTeam.getAllPrincipalsForTarget(role);
				for(WTPrincipalReference reference : arrayList) {
					Object object2 = reference.getPrincipal();
					if(object2 instanceof WTUser) {
						WTUser user = (WTUser) object2;
						if(user.getName().equals(curentuser.getName())) {
							return UIValidationStatus.ENABLED;
						}
					} else if(object2 instanceof WTGroup) {
						WTGroup group = (WTGroup) object2;
						if(group.isMember(curentuser)) {
							return UIValidationStatus.ENABLED;
						}

					}
				}
			}


		} catch(WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(access);
		}
		return UIValidationStatus.HIDDEN;
	}
}

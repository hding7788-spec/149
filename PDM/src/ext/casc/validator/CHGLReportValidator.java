package ext.casc.validator;

import java.util.ArrayList;

import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class CHGLReportValidator extends DefaultSimpleValidationFilter {

    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        Object object = criteria.getContextObject().getObject();
        try {
        	WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
        	if(object instanceof WTPart){
        		WTPart part =( WTPart)object;
        		if("Manufacturing".equals(part.getViewName())){
        			WTContainer wtContainer = part.getContainer();
            		ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);
                    Role role = Role.toRole("XINXIHUABUSHUJUYUAN");
                    if (role == null) {
                        return UIValidationStatus.DISABLED;
                    }
                    ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
                    for (WTPrincipalReference reference : arrayList) {
                        Object object2 = reference.getPrincipal();
                        if (object2 instanceof WTUser) {
                            WTUser user = (WTUser) object2;
                            if (user.getName().equals(currentUser.getName())) {
                                return UIValidationStatus.ENABLED;
                            }
                        }else if (object2 instanceof WTGroup) {
							WTGroup group = (WTGroup) object2;
							if (group.isMember(currentUser)) {
                                return UIValidationStatus.ENABLED;

							}
						}
                    }
        		}

        	}
        } catch (WTInvalidParameterException e) {
            e.printStackTrace();
        } catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return UIValidationStatus.HIDDEN;
    }

}

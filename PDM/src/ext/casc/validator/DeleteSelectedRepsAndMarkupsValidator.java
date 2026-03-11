package ext.casc.validator;

import java.util.ArrayList;

import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.inf.container.WTContained;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.part.WTPart;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class DeleteSelectedRepsAndMarkupsValidator extends DefaultSimpleValidationFilter {
	@Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        Object object = criteria.getContextObject().getObject();
        String id = key.getComponentID();
        WTContained contained = null;
        if(object instanceof WTPart){
        	contained = ((WTPart) object).getContainer();
        }else  if(object instanceof EPMDocument){
        	contained = ((EPMDocument) object).getContainer();
        }
        if(contained==null)  return UIValidationStatus.HIDDEN;
        ContainerTeamManaged teamManaged = (ContainerTeamManaged) contained;
        try {
			ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(teamManaged);
			WTPrincipal currentUser = SessionHelper.getPrincipal();
	        Role role =  Role.toRole("PRODUCT MANAGER");
	        ArrayList<WTPrincipalReference> allUser = containerTeam.getAllPrincipalsForTarget(role);
	        for(WTPrincipalReference user :allUser){
	        	Persistable per = user.getObject();
	        	if(per.getPersistInfo().getObjectIdentifier().getId()==currentUser.getPersistInfo().getObjectIdentifier().getId()){
	        		return UIValidationStatus.ENABLED;
	        	}
	        }
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return UIValidationStatus.HIDDEN;
    }
	
}

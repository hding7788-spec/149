package ext.casc.validator;

import java.rmi.RemoteException;
import java.util.ArrayList;

import wt.doc.WTDocument;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class ProcessPlanDocumentValidator extends DefaultSimpleValidationFilter {

    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        Object object = criteria.getContextObject().getObject();
        try {
            if (object instanceof WTDocument) {
            	WTDocument doc = (WTDocument) object;
				String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
				if (docType.contains("casc.sast.149.PROCESS_PLAN") || docType.contains("casc.sast.149.DX_PROCESS_DOC")) {
					return UIValidationStatus.ENABLED;
				}
            }
        } catch (WTInvalidParameterException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return UIValidationStatus.HIDDEN;
    }

}

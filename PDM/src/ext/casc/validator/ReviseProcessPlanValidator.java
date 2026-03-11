package ext.casc.validator;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import ext.casc.constants.Constants;
import ext.casc.util.IBAHelper;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.vc.VersionControlHelper;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

public class ReviseProcessPlanValidator extends DefaultSimpleValidationFilter {

    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        Object object = criteria.getContextObject().getObject();
        try {
        	WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
            if (object instanceof WTDocument) {
            	WTDocument d = (WTDocument)object;

            	QueryResult qr =  VersionControlHelper.service.allIterationsOf(d.getMaster());
            	if(qr.hasMoreElements()){
            		d =(WTDocument) qr.nextElement();
            	}
            	if(!d.getModifier().getPrincipal().getName().equals(currentUser.getName())){
            		 return UIValidationStatus.DISABLED;
            	}
            	String typeName = TypedUtility.getTypeIdentifier(object).getTypename();
            	String state = d.getState().getState().toString();

            	/*if(typeName.contains("PROCESS_NOTICE")&&state.equals("APPROVED")) {
            		return UIValidationStatus.ENABLED;
            	}*/

            	/**if(typeName.contains("TECHNOLOGY_AGREEMENT")&&state.equals("APPROVED")) {
            		return UIValidationStatus.ENABLED;
            	}
            	if(typeName.contains("GONGYIZONGFANGAN")&&state.equals("APPROVED")) {
            		return UIValidationStatus.ENABLED;
            	}
            	if(typeName.contains("GONGYIFENFANGAN")&&state.equals("APPROVED")) {
            		return UIValidationStatus.ENABLED;
            	}
            	if(typeName.contains("QITALEIWENDANG")&&state.equals("APPROVED")) {
            		return UIValidationStatus.ENABLED;
            	}
            	if(typeName.contains("TY_PROCESS_DOC")&&state.equals("APPROVED")){
            		return UIValidationStatus.ENABLED;
            	}
            	if(typeName.contains("GONGYIJIANDING")&&state.equals("APPROVED")){
        			return UIValidationStatus.ENABLED;
            	}
            	if(typeName.contains("GONGYIFENXICEHUAZONGJIE")&&state.equals("APPROVED")){
        			return UIValidationStatus.ENABLED;
            	}
            	if(typeName.contains("GONGYIDINGXING")&&state.equals("APPROVED")){
        			return UIValidationStatus.ENABLED;
            	}
            	if(typeName.contains("JISHUKETI")&&state.equals("APPROVED")){
        			return UIValidationStatus.ENABLED;
            	}*/
            	//总体测发报告 修订权限
				if(typeName.contains("TEST_REPORT")&&state.equals("APPROVED")){
					return UIValidationStatus.ENABLED;
				}
				if(typeName.contains("PROCESS_PLAN")&&state.equals("APPROVED")) {
					if(d.getModifier().getPrincipal().getName().equals(currentUser.getName())){
						String pplantype = IBAHelper.getIBAStringValue(d, "PPLANTYPE");
						if("临时工艺文件".equals(pplantype)){
							return UIValidationStatus.ENABLED;
						}
						QueryResult qrProcs = WfEngineHelper.service
		 						.getAssociatedProcesses(d, null, null);
		 				if (qrProcs.hasMoreElements()) {
		 					WfProcess proc = (WfProcess) qrProcs.nextElement();
		 					if(proc.getTemplate().getName().equals(Constants.WFN_SANJIPROCESSWF)||proc.getTemplate().getName().equals(Constants.WFN_SANJIGENGGAIWF)){
		 						return UIValidationStatus.ENABLED;
		 					}

		 				}

					}

				}
            }
        } catch (WTInvalidParameterException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }
        return UIValidationStatus.HIDDEN;
    }

}

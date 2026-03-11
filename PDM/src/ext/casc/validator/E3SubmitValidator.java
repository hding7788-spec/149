package ext.casc.validator;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.casc.constants.Constants;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressHelper;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;

import java.rmi.RemoteException;
import java.util.Locale;

public class E3SubmitValidator extends DefaultSimpleValidationFilter {
    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        Object object = criteria.getContextObject().getObject();
        try {
			WTUser currentUser = (WTUser)SessionHelper.getPrincipal();

            if (object instanceof WTDocument) {
                WTDocument document = (WTDocument)object;
                String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
                //技术通知单和工艺规程隐藏该入口
                if (docType.indexOf("casc.sast.149.DIANZHUANG_PROCESSPLAN") != -1 &&"E3工艺".equals(document.getDescription())) {
                    String state = document.getState().getState().getDisplay(Locale.CHINA);
                    boolean isModifier = false;
                    if(document.getModifier().getName().equals(currentUser.getName())||document.getCreator().getName().equals(currentUser.getName())){
                        isModifier = true;
                    }
                    if (isModifier&&!WorkInProgressHelper.isCheckedOut(document)) {
                        if (state.contains(Constants.STATE_ZHENGZAIGONGZUO)) {
                            boolean hasProcess = false;
                            QueryResult qrProcs = WfEngineHelper.service
                                    .getAssociatedProcesses(document, null, null);
                            while (qrProcs.hasMoreElements()) {
                                WfProcess process = (WfProcess) qrProcs.nextElement();
                                if(process.getState().equals(WfState.OPEN_RUNNING)){
                                    hasProcess  = true;
                                    break;
                                }
                            }
                            if(hasProcess){
                                return UIValidationStatus.DISABLED;
                            }else{
                                return UIValidationStatus.ENABLED;
                            }
                        }else{
                            return UIValidationStatus.DISABLED;
                        }

                    }else{
                        return UIValidationStatus.DISABLED;
                    }
                }

            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }
        return UIValidationStatus.HIDDEN;
    }
}

package ext.casc.validator;

import com.ptc.core.ui.validation.*;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.casc.constants.Constants;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.collections.WTCollection;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Locale;

public class CreateChangeNoticeValidator extends DefaultSimpleValidationFilter {

    public CreateChangeNoticeValidator(){

    }

    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        Object object = criteria.getContextObject().getObject();
        try {
            WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
            String id = key.getComponentID();
            if("createDocCustomChangeNotice".equals(id)){
                if(object instanceof WTDocument) {
                    WTDocument document = (WTDocument) object;
                    if (validate(document, currentUser)) {
                        return UIValidationStatus.ENABLED;
                    }else {
                        return UIValidationStatus.DISABLED;
                    }
                }
                return UIValidationStatus.HIDDEN;
            }else {
                if (object instanceof WTDocument) {
                    WTDocument document = (WTDocument) object;
                    String state = document.getState().getState().getDisplay(Locale.CHINA);
                    if (state.contains(Constants.STATE_YIPIZHUN)&& document.getCreatorName().equals(currentUser.getName())) {
                        return UIValidationStatus.ENABLED;
                    }
                }
                if (object instanceof EPMDocument) {
                    EPMDocument epmDocument = (EPMDocument) object;
                    String state = epmDocument.getState().getState().getDisplay(Locale.CHINA);
                    if (state.contains(Constants.STATE_YIPIZHUN)&& epmDocument.getCreatorName().equals(currentUser.getName())) {
                        return UIValidationStatus.ENABLED;
                    }
                }
                if (object instanceof WTPart) {
                    WTPart part = (WTPart) object;
                    String state = part.getState().getState().getDisplay(Locale.CHINA);
                    if (state.contains(Constants.STATE_YIPIZHUN)&& part.getCreatorName().equals(currentUser.getName())) {
                        return UIValidationStatus.ENABLED;
                    }
                }
                if (object instanceof MPMProcessPlan) {
                    MPMProcessPlan plan = (MPMProcessPlan) object;
                    String state = plan.getState().getState().getDisplay(Locale.CHINA);
                    if (state.contains(Constants.STATE_YIPIZHUN)&& plan.getCreatorName().equals(currentUser.getName())) {
                        return UIValidationStatus.ENABLED;
                    }
                }
            }
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return UIValidationStatus.DISABLED;
    }

    @Override
    public UIValidationResultSet preValidateMultiTargetAction(UIValidationKey uivalidationkey,
            UIValidationCriteria uivalidationcriteria) {
        UIValidationResultSet uivalidationresultset = UIValidationResultSet.newInstance();
        uivalidationresultset.addResult(UIValidationResult.newInstance(uivalidationkey, UIValidationStatus.ENABLED));
        WTCollection wtcollection = uivalidationcriteria.getTargetObjects();
        Iterator iterator = wtcollection.referenceIterator();
        String id = uivalidationkey.getComponentID();
        while(iterator.hasNext()){
            Object object = iterator.next();
            if (object instanceof WTDocument) {
                if("createDocCustomChangeNotice".equals(id)) {
                    try {
                        WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
                        WTDocument document = (WTDocument) object;
                        String state = document.getState().getState().getDisplay(Locale.CHINA);
                        String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
                        if(docType.indexOf("casc.sast.149.GONGYIZONGFANGAN") != -1 || docType.indexOf("casc.sast.149.GONGYIFENFANGAN") != -1
                                || docType.indexOf("casc.sast.149.GONGYIFENXICEHUAZONGJIE") != -1 || docType.indexOf("casc.sast.149.GONGYIDINGXING") != -1
                                || docType.indexOf("casc.sast.149.GONGYIJIANDING") != -1 || docType.indexOf("casc.sast.149.JISHUKETI") != -1
                                || docType.indexOf("casc.sast.149.TEST_REPORT") != -1 || docType.indexOf("casc.sast.149.TECHNOLOGY_AGREEMENT") != -1) {
                            if(state.contains(Constants.STATE_YIPIZHUN) && (document.getCreatorName().equals(currentUser.getName()) || document.getModifier().equals(currentUser.getName()))) {
                                uivalidationresultset.addResult(UIValidationResult.newInstance(uivalidationkey, UIValidationStatus.ENABLED));
                            }
                        }
                    } catch(WTException e) {
                        throw new RuntimeException(e);
                    } catch(RemoteException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    WTDocument document = (WTDocument) object;
                    String state = document.getState().getState().getDisplay(Locale.CHINA);
                    if (state.contains(Constants.STATE_YIPIZHUN)) {
                        uivalidationresultset.addResult(UIValidationResult.newInstance(uivalidationkey, UIValidationStatus.ENABLED));
                    }
                }
            }else if (object instanceof EPMDocument) {
                EPMDocument epmDocument = (EPMDocument) object;
                String state = epmDocument.getState().getState().getDisplay(Locale.CHINA);
                if (state.contains(Constants.STATE_YIPIZHUN)) {
                    uivalidationresultset.addResult(UIValidationResult.newInstance(uivalidationkey, UIValidationStatus.ENABLED));
                }
            }else if (object instanceof WTPart) {
                WTPart part = (WTPart) object;
                String state = part.getState().getState().getDisplay(Locale.CHINA);
                if (state.contains(Constants.STATE_YIPIZHUN)) {
                    uivalidationresultset.addResult(UIValidationResult.newInstance(uivalidationkey, UIValidationStatus.ENABLED));
                }
            }else if (object instanceof MPMProcessPlan) {
                MPMProcessPlan plan = (MPMProcessPlan) object;
                String state = plan.getState().getState().getDisplay(Locale.CHINA);
                if (state.contains(Constants.STATE_YIPIZHUN)) {
                    uivalidationresultset.addResult(UIValidationResult.newInstance(uivalidationkey, UIValidationStatus.ENABLED));
                }
            }else {
                uivalidationresultset.addResult(UIValidationResult.newInstance(uivalidationkey, UIValidationStatus.DISABLED));
            }
        }
        return uivalidationresultset;
    }

    public static boolean validate(WTDocument document, WTUser currentUser) {
        try {
            String state = document.getState().getState().getDisplay(Locale.CHINA);
            String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
            if (docType.indexOf("casc.sast.149.GONGYIZONGFANGAN") != -1||docType.indexOf("casc.sast.149.GONGYIFENFANGAN") != -1
                    ||docType.indexOf("casc.sast.149.GONGYIFENXICEHUAZONGJIE") != -1||docType.indexOf("casc.sast.149.GONGYIDINGXING") != -1
                    ||docType.indexOf("casc.sast.149.GONGYIJIANDING") != -1 || docType.indexOf("casc.sast.149.JISHUKETI") != -1
                    || docType.indexOf("casc.sast.149.TEST_REPORT") != -1 || docType.indexOf("casc.sast.149.TECHNOLOGY_AGREEMENT") != -1){
                if (state.contains(Constants.STATE_YIPIZHUN)&& (document.getCreatorName().equals(currentUser.getName()) || document.getModifierName().equals(currentUser.getName()))) {
                    return true;
                }else {
                    return false;
                }
            }
        } catch(WTException e) {
            e.printStackTrace();
        } catch(RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }
}

package ext.casc.validator;

import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.core.ui.validation.*;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.resource.MPMResourceHelper;
import ext.casc.constants.Constants;
import ext.casc.process.ProcessTaskItem;
import ext.casc.sop.constants.SopConstants;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

public class CreateProcessChangeNoticeValidator extends DefaultSimpleValidationFilter {

    public CreateProcessChangeNoticeValidator(){

    }

    @Override
    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
        Object object = criteria.getContextObject().getObject();
		Object pageObj = criteria.getPageObject().getObject();
        try {
			WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
			if (object instanceof MPMProcessPlan) {
			    MPMProcessPlan plan = (MPMProcessPlan) object;
			    String state = plan.getState().getState().getDisplay(Locale.CHINA);
			    //已批准并且是自己创建的

			    boolean isModifier = false;
			    if(plan.getModifier().getName().equals(currentUser.getName())||plan.getCreator().getName().equals(currentUser.getName())){
                	isModifier = true;
                }
                QueryResult qr2 = RelatedChangesQueryCommands.getRelatedAffectingChangeNotices( plan);
                if (qr2.hasMoreElements()) {
                	 return UIValidationStatus.DISABLED;
                }else{
	                if (state.contains(Constants.STATE_YIPIZHUN)&&isModifier) {
	                	Collection localCollection = null;
	                    localCollection = MPMResourceHelper.service.getAssociatedDescribeDocuments(plan);
	                    Iterator it = localCollection.iterator();
	                    if(it.hasNext()){
	                    	Object o = it.next();
	                    	if(o instanceof ObjectReference){
	                    		ObjectReference orf = (ObjectReference)o;
	                    		WTDocument doc = (WTDocument)orf.getObject();
	                    		 QueryResult qr3 = RelatedChangesQueryCommands.getRelatedAffectingChangeNotices( doc);
	                             if (qr3.hasMoreElements()) {
	                             	 return UIValidationStatus.DISABLED;
	                             }else{
	             	                return UIValidationStatus.ENABLED;
	                             }
	                    	}
	                    }
	                }
                }
			}else if (object instanceof WTDocument) {
				WTDocument doc = (WTDocument) object;
				boolean validate = validate(doc,currentUser);
				if(validate){
					return UIValidationStatus.ENABLED;
				}else {
					return UIValidationStatus.DISABLED;
				}
			}else if(object instanceof WTPart){
				WTPart part = (WTPart) object;
				if ("Manufacturing".equals(part.getViewName())) {
					if(pageObj instanceof ProcessTaskItem){
						ProcessTaskItem taskItem = (ProcessTaskItem) pageObj;
						String taskType = taskItem.getTaskType();
						if(SopConstants.SOP_TASK_TASKTYPE.equals(taskType) || SopConstants.SOP_TASK_TASKTYPE_CHANGE.equals(taskType)){
							return UIValidationStatus.HIDDEN;
						}
					}
					return UIValidationStatus.ENABLED;
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
        return UIValidationStatus.HIDDEN;
    }

	public static boolean validate(WTDocument doc,WTUser user){
		//已批准并且是自己创建的
		String docType;
		try {
			docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
			if (docType.contains("casc.sast.149.PROCESS_PLAN") || docType.contains("casc.sast.149.DX_PROCESS_DOC") || docType.contains("casc.sast.149.SOPDoc")) {
				boolean isModifier = false;
				if(doc.getModifier().getName().equals(user.getName())||doc.getCreator().getName().equals(user.getName())){
					isModifier = true;
				}
				WTDocument lastdoc = WTDocumentUtil.getLatestDocumentByNumber(doc.getNumber());
				QueryResult qr2 = RelatedChangesQueryCommands.getRelatedAffectingChangeNotices( lastdoc);
				String state = lastdoc.getState().getState().getDisplay(Locale.CHINA);
				if (qr2.hasMoreElements()) {
					return false;
				}else{
					if (state.contains(Constants.STATE_YIPIZHUN)&&isModifier&&doc.equals(lastdoc)) {
						return true;
					}else{
						return false;
					}
				}
			}else{
				return false;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch(WTException e) {
			e.printStackTrace();
		}
		return false;
	}

    @Override
    public UIValidationResultSet preValidateMultiTargetAction(UIValidationKey uivalidationkey,
            UIValidationCriteria uivalidationcriteria) {
        UIValidationResultSet uivalidationresultset = UIValidationResultSet.newInstance();
        uivalidationresultset.addResult(UIValidationResult.newInstance(uivalidationkey, UIValidationStatus.ENABLED));
        WTCollection wtcollection = uivalidationcriteria.getTargetObjects();
        Iterator iterator = wtcollection.referenceIterator();
        WTPrincipal currentUser;
		try {
			currentUser = SessionHelper.getPrincipal();
	        while(iterator.hasNext()){
	            Object object = iterator.next();
	            //boolean isCreator = false;
	            boolean isModifier = false;
	            if (object instanceof WTDocument) {
	                WTDocument document = (WTDocument) object;
	                /*if(document.getCreator().getName().equals(currentUser.getName())){
	                	isCreator = true;
	                }*/
	                if(document.getModifier().getName().equals(currentUser.getName())&&isModifier){
	                	isModifier = true;
	                }
	                String state = document.getState().getState().getDisplay(Locale.CHINA);
	                if (state.contains(Constants.STATE_YIPIZHUN)&&isModifier) {
	                    uivalidationresultset.addResult(UIValidationResult.newInstance(uivalidationkey, UIValidationStatus.ENABLED));
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
	                /*if(document.getCreator().getName().equals(currentUser.getName())){
	                	isCreator = true;
	                }*/
	                if(plan.getModifier().getName().equals(currentUser.getName())&&isModifier){
	                	isModifier = true;
	                }
	                QueryResult qr2 = RelatedChangesQueryCommands.getRelatedAffectingChangeNotices( plan);
	                if (qr2.hasMoreElements()) {
	                	 uivalidationresultset.addResult(UIValidationResult.newInstance(uivalidationkey, UIValidationStatus.DISABLED));
	                }else{
	                	String state = plan.getState().getState().getDisplay(Locale.CHINA);
		                if (state.contains(Constants.STATE_YIPIZHUN)&&isModifier) {
		                    uivalidationresultset.addResult(UIValidationResult.newInstance(uivalidationkey, UIValidationStatus.ENABLED));
		                }
	                }

	            }else {
	                uivalidationresultset.addResult(UIValidationResult.newInstance(uivalidationkey, UIValidationStatus.DISABLED));
	            }
	        }
        } catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return uivalidationresultset;
    }

}

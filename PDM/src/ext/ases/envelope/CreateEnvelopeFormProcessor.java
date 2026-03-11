/**
 * @(#)CreateEnvelopeFormProcessor.java
 *
 *
 * @author Leon Zhang
 * @version 1.00 2010/1/5
 */
package ext.ases.envelope;

import com.ptc.core.command.server.delegate.ServerCommandDelegateUtility;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.*;
import com.ptc.core.meta.type.common.TypeInstance;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import org.apache.log4j.Logger;
import wt.enterprise.RevisionControlled;
import wt.fc.*;
import wt.iba.value.litevalue.AbstractValueViewMap;
import wt.iba.value.service.MultiObjIBAValueDBService;
import wt.log4j.LogR;
import wt.method.MethodContext;
import wt.part.WTPart;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CreateEnvelopeFormProcessor extends CreateObjectFormProcessor{
    
    private static final String RESOURCE = "ext.ases.envelop.envelopResource";
    private static final Logger log;

    static {
       try {
          log = LogR.getLogger(CreateEnvelopeFormProcessor.class.getName());
       }
       catch (Exception e) {
          throw new ExceptionInInitializerError(e);
       }
    }
    
     @Override
    public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {

        //String relatedObjTableID=nmcommandbean.getTextParameter("relatedObjTable_id");
        FormResult phaseResult = new FormResult();
        phaseResult.setStatus(FormProcessingStatus.SUCCESS);
        phaseResult = super.doOperation(clientData, objectBeans);
        log.debug("phaseResult:" + phaseResult);
        Iterator iterator1= objectBeans.iterator();
        do
        {
            if(!iterator1.hasNext())
            {
                break;
            }
            ObjectBean objectbean = (ObjectBean)iterator1.next();
            if(objectbean.getObject() != null && (objectbean.getObject() instanceof Persistable))
            {
                ProcessEnvelope processEnvelope = (ProcessEnvelope)objectbean.getObject();
                PersistenceHelper.manager.refresh(processEnvelope);
                String[] relatedObjOids =objectbean.getTextParameterValues("change_affectedData_table__objRef");	
                log.debug("relatedObjectOids is: " + relatedObjOids);
                //如果是从对象下拉菜单里，获得的oid是当前对象的oid，可以用来判断是否成套件，进行TopObject设置
				String[] oids = clientData.getTextParameterValues("oid");
				String objoid = new String();
		    	for(String oid:oids){
		    		objoid = oid;
		    		log.debug("oid is:" + oid);
		    	}
		    	log.debug(relatedObjOids.length);
				if(relatedObjOids!=null && relatedObjOids.length>0)
				{
					saveRelatedObjLink(relatedObjOids, processEnvelope,objectbean);		
					
				}
            }
        } while(true);
        
        return phaseResult;

    }
    
    private void saveTopObjLink(WTPart wtpart,ProcessEnvelope processEnvelope) throws WTException{
		try
		{
			log.debug("***Get top obj:"+wtpart.getIdentity());				
			log.debug("***Get processEnvelope:"+processEnvelope.getIdentity());
			EnvelopeTopObjLink envelotopobjlink = EnvelopeTopObjLink.newEnvelopeTopObjLink(processEnvelope,(RevisionControlled)wtpart);					
			PersistenceHelper.manager.save(envelotopobjlink);
		}
		catch (wt.util.WTException e){
			e.printStackTrace();
		}
	}
    
    private void saveRelatedObjLink(String[] oids,ProcessEnvelope processEnvelope,ObjectBean objectbean) throws WTException{

		ReferenceFactory rf = new ReferenceFactory();
		log.debug("saveRelatedObjLink");
		for(String oid:oids){
			log.debug("***related obj oid="+oid+"***");			
			WTReference ref=rf.getReference(oid);
			Persistable obj=ref.getObject();
			WTObject wtobject;
			if(obj!=null && obj instanceof WTObject){
				wtobject=(WTObject)obj;
				try
				{
					log.debug("***Get related obj:"+wtobject.getIdentity());				
					log.debug("***Get processEnvelope:"+processEnvelope.getIdentity());
					EnvelopeMemberLink envelopememberlink = EnvelopeMemberLink.newEnvelopeMemberLink(processEnvelope,(RevisionControlled)wtobject);					
					PersistenceHelper.manager.save(envelopememberlink);
				}
				catch (wt.util.WTException e){
					e.printStackTrace();
				}
			
			}
		}
	}

    @Override
    public FormResult preProcess(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {

        if (log.isDebugEnabled()) {
            log.debug("\npreProcess() - request parameters on entry:");
            log.debug(clientData.requestParametersToString());
        }
        FormResult phaseResult = new FormResult(FormProcessingStatus.SUCCESS);
        for (ObjectBean objBean : objectBeans) {
            FormResult itemResult = new FormResult(FormProcessingStatus.SUCCESS);
            objBean.setObject(createItemInstance(clientData, objBean, itemResult));
            log.debug("objBean.getObject()" + objBean.getObject());
            phaseResult = mergeIntermediateResult(phaseResult, itemResult);
            if (!continueProcessing(phaseResult)) {
                // *****Put objBeans in phaseResult ????
                return phaseResult;
            }
        }

        // Call super(), which will call registered processor delegates
        FormResult superResult = super.preProcess(clientData, objectBeans);
        phaseResult = mergeIntermediateResult(phaseResult, superResult);
        log.debug("superResult:-------------" + superResult);
        return phaseResult;

    }

    protected Object createItemInstance(NmCommandBean clientData, ObjectBean objBean, FormResult result)
            throws WTException {

        TypeInstance ti = createAndValidateTypeInstance(clientData, objBean, result);
        if (ti == null) {
            return null;
        }
        objBean.setObject(createPersistable(ti, clientData, objBean, result));
        return objBean.getObject();

    }

    /**
     * Converts the given TypeInstance to a Persistable.
     * 
     * <BR>
     * <BR>
     * <B>Supported API: </B>false
     * 
     * @param ti
     *            - The TypeInstance to be converted. Input. Required.
     * @param clientData
     *            - Contains all the form data and other wizard context information. Input. Required.
     * @param objBean
     *            - Contains the form data for one object. Input. Required.
     * @param result
     *            The return status of the method. Output, but must be instantiated on entry.
     * @return Object - the Persistable created
     * @exception wt.util.WTException
     */

    protected Object createPersistable(TypeInstance ti, NmCommandBean clientData, ObjectBean objBean, FormResult result)
            throws WTException {

        // Convert the type instance to a Persistable
        boolean already = MethodContext.getContext().containsKey(MultiObjIBAValueDBService.METHOD_CONTEXT_KEY);
        if (!already) {
            MethodContext.getContext().put(MultiObjIBAValueDBService.METHOD_CONTEXT_KEY, new AbstractValueViewMap());
        }

        if (log.isTraceEnabled()) {
            log.trace("Type instance to be translated:n" + ti);
        }

        Persistable obj = translateTIToPersistable(ti, objBean);

        if (log.isDebugEnabled()) {
            log.debug("preProcess(): translated TypeInstance into Persistable: " + obj.toString());
        }

        // Set container
        FormResult setContainerResult = CreateEditFormProcessorHelper.setContainerRef(clientData, objBean);
        result = mergeIntermediateResult(setContainerResult, result);
        if (!continueProcessing(result)) {
            return null;
        }
        return obj;

    }

    /**
     * Translates the given TypeInstance to a Persistable.
     * 
     * <BR>
     * <BR>
     * <B>Supported API: </B>false
     * 
     * @param ti
     *            - The TypeInstance to be translated. Input. Required.
     * @param objBean
     *            - Contains the form data for one object. Input. Required.
     * @return Object - the Persistable created
     * @exception wt.util.WTException
     */

    protected Persistable translateTIToPersistable(TypeInstance ti, ObjectBean objBean) throws WTException {
        Persistable obj = null;
        return ServerCommandDelegateUtility.translate(ti, obj);
    }

    /**
     * Sets the information needed by the WizardServlet to refresh the folder browser page dynamically when the
     * "nextAction" attribute of the FormResult is FormResultAction.REFRESH_OPENER and the wizard was launched from the
     * folder browser. The amount of the parent page to be refreshed is indicated by the the "ajax" attribute of the
     * action tag for the wizard action. This method may be overridden by subclasses to achieve the behavior desired in
     * a specific wizard context.
     * 
     * <BR>
     * <BR>
     * <B>Supported API: </B>false
     * 
     * @param result
     *            - The FormResult to which the refresh info should be added. Input and output. Required.
     * @param clientData
     *            - Contains all the form data and other wizard context information. Input. Required.
     * @param objectBeans
     *            - Contain the objects that were created by the processor in the "object" attribute. One bean per
     *            object. Input. Required.
     * @return FormResult - the refreshInfo attribute is set
     * @exception wt.util.WTException
     */
    @Override
    protected FormResult setRefreshInfo(FormResult result, NmCommandBean clientData, List<ObjectBean> objectBeans)
            throws WTException {
        FormResult r = CreateEditFormProcessorHelper.setStandardRefreshInfo(result, clientData, objectBeans,
                NmCommandBean.DYNAMIC_ADD, null);
        if (log.isDebugEnabled()) {
            ArrayList<DynamicRefreshInfo> refreshInfoList = r.getDynamicRefreshInfo();
            log.debug("setRefreshInfo() returning:");
            for (DynamicRefreshInfo ri : refreshInfoList) {
                log.debug(ri.toString());
            }
        }
        return r;
    }

    /**
     * Sets the "nextAction" attribute on the given FormResult based on the processing status. <BR>
     * <BR>
     * 
     * If the status is SUCCESS or NON_FATAL_ERROR, the nextAction is set to REFRESH_OPENER and a success feedback
     * message is added to the FormResult. If the launching window was a folder browser, information needed to
     * dynamically refresh the parent window also will be returned. <BR>
     * <BR>
     * 
     * If the status is FAILURE, the nextAction is set to NONE. <BR>
     * <BR>
     * This method may be overridden by subclasses desiring different behavior.
     * 
     * <BR>
     * <BR>
     * <B>Supported API: </B>true
     * 
     * @param result
     *            - The FormResult to which the action info should be added. Input and output. Required.
     * @param clientData
     *            - Contains all the form data and other wizard context information. Input. Required.
     * @param objectBeans
     *            - Contain the object(s) that were created (if successful). One bean per object. Input. Required.
     * @return FormResult - the nextAction attribute is set; the refreshInfo attribute may also be set and a success
     *         feedback message added.
     * @exception wt.util.WTException
     * 
     */

    @Override
    public FormResult setResultNextAction(FormResult result, NmCommandBean clientData, List<ObjectBean> objectBeans)
            throws WTException {
        if (result.getNextAction() == null) {
            if (result.getStatus() == FormProcessingStatus.SUCCESS
                    || result.getStatus() == FormProcessingStatus.NON_FATAL_ERROR) {
                result.setNextAction(FormResultAction.REFRESH_OPENER);
                // If launched from the folder browser dynamically add rows for the new
                // object; otherwise display a Success message
                if (CreateEditFormProcessorHelper.parentIsFolderBrowser(clientData)) {
                    result = setRefreshInfo(result, clientData, objectBeans);
                    cleanseRefreshInfoForFoldersPage(result, clientData);
                }
                result.addFeedbackMessage(getSuccessFeedbackMessage());
            } else if (result.getStatus() == FormProcessingStatus.FAILURE) {
                result.setNextAction(FormResultAction.NONE);
            }
        }
        return result;
    }

    /**
     * This method will remove the refresh info for any object which was created in a folder we're not currently looking
     * at. It makes the assumption that there is a single default cabinet for the Folders page.
     * 
     * <BR>
     * <BR>
     * <B>Supported API: </B>false
     */
    protected void cleanseRefreshInfoForFoldersPage(FormResult result, NmCommandBean clientData) throws WTException {
        if (result == null || clientData == null) {
            return;
        }

        ArrayList<DynamicRefreshInfo> refreshInfos = result.getDynamicRefreshInfo();
        if (refreshInfos == null) {
            return;
        }

        ArrayList<DynamicRefreshInfo> removeInfos = new ArrayList<DynamicRefreshInfo>(refreshInfos.size());

        NmOid parentLocation = clientData.getPageOid();

        if (parentLocation != null) {
            // convert all Cabinet oids to Container oids.
            // Often the parentLocation will be represented as a container oid and the create location will be
            // a cabinet oid. We want same oid-types for comparison purposes. Since it's easier to
            // get the container from the cabinet rather than the cabinet from the container we convert all
            // cabinet oids down to the container oid.
            if ("wt.folder.Cabinet".equals(parentLocation.getReferencedClassString())) {
                parentLocation = new NmOid(parentLocation.getContainer());
            }

            for (DynamicRefreshInfo dri : refreshInfos) {
                NmOid createLocation = dri.getLocation();

                if ("wt.folder.Cabinet".equals(createLocation.getReferencedClassString())) {
                    createLocation = new NmOid(createLocation.getContainer());
                }

                // Since the folder browser is nolonger a tree we need to make sure that we're looking at
                // the correct folder. If we're not then clear the refresh info.
                if (createLocation != null
                        && (!parentLocation.getReferenceString().equals(createLocation.getReferenceString()))) {
                    removeInfos.add(dri);
                }
            }

            for (DynamicRefreshInfo dri : removeInfos) {
                refreshInfos.remove(dri);
            }

            result.setDynamicRefreshInfo(refreshInfos);

            if (refreshInfos.size() == 0) {
                result.setNextAction(FormResultAction.NONE);
            }
        }
    }
}
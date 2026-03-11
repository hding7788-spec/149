/**
 * @(#)EditTechNoticeFormProcessor.java
 *
 *
 * @author Leon Zhang
 * @version 1.00 2010/3/23
 */
package ext.ases.technotice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ptc.core.components.forms.*;
import org.apache.log4j.Logger;

import wt.clients.vc.CheckInOutTaskLogic;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.iba.value.litevalue.AbstractValueViewMap;
import wt.iba.value.service.MultiObjIBAValueDBService;
import wt.log4j.LogR;
import wt.method.MethodContext;
import wt.part.WTPart;
import wt.part.WTPartReferenceLink;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.Workable;

import com.ptc.core.command.server.delegate.ServerCommandDelegateUtility;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.meta.type.common.TypeInstance;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ases.envelope.EnvelopeHelper;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.util.IBAHelper;

public class EditTechNoticeFormProcessor extends DefaultEditFormProcessor {
    
    private static final String RESOURCE = "ext.ases.technotice.technoticeResource";
    private static final Logger log;

    static {
       try {
          log = LogR.getLogger(EditTechNoticeFormProcessor.class.getName());
       }
       catch (Exception e) {
          throw new ExceptionInInitializerError(e);
       }
    }
    
     @Override
    public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {

        //String relatedObjTableID=nmcommandbean.getTextParameter("relatedObjTable_id");
        Iterator iterator2= objectBeans.iterator();
        while(iterator2.hasNext()){
            ObjectBean objectbean = (ObjectBean)iterator2.next();
            System.out.println("objectbean is: " + objectbean);
        }
        FormResult phaseResult = new FormResult();
        phaseResult.setStatus(FormProcessingStatus.SUCCESS);
        phaseResult = super.doOperation(clientData, objectBeans);
        System.out.println("phaseResult:" + phaseResult);
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
                System.out.println("objectbean.getObject() is: " + objectbean.getObject());
                WTDocument wtdoc = (WTDocument)objectbean.getObject();
                System.out.println("wtdoc is: " + wtdoc);
                boolean flag = CheckInOutTaskLogic.isCheckedOut((Workable)wtdoc);
                System.out.println("flag is: " + flag);
                if(flag)
                    try {
                        CheckInOutTaskLogic.checkInObject((Workable)wtdoc, null);
                    } catch (WTPropertyVetoException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                PersistenceHelper.manager.refresh(wtdoc);
                
                String relatedBeforeObjInitOids =objectbean.getTextParameter("initialRows_changeTask_affectedItems_table");
                String relatedBeforeObjAddOids =objectbean.getTextParameter("addRows_changeTask_affectedItems_table");
                String relatedBeforeObjRmOids =objectbean.getTextParameter("rmRows_changeTask_affectedItems_table");
                String relatedBeforeObjOidsStr=relatedBeforeObjInitOids+relatedBeforeObjAddOids;
                String[] tempOids = relatedBeforeObjRmOids.split("#");
                for (int i = 0; i < tempOids.length; i++) {
                    relatedBeforeObjOidsStr=relatedBeforeObjOidsStr.replace(tempOids[i], "");
                }
                String[] relatedBeforeObjOids =relatedBeforeObjOidsStr.split("#");
                
//                String[] relatedAfterObjOids =objectbean.getTextParameterValues("changeTask_resultingItems_table__objRef");
                
                String relatedAfterObjInitOids =objectbean.getTextParameter("initialRows_changeTask_resultingItems_table");
                String relatedAfterObjAddOids =objectbean.getTextParameter("addRows_changeTask_resultingItems_table");
                String relatedAfterObjRmOids =objectbean.getTextParameter("rmRows_changeTask_resultingItems_table");
                String relatedAfterObjOidsStr=relatedAfterObjInitOids+relatedAfterObjAddOids;
                tempOids = relatedAfterObjRmOids.split("#");
                for (int i = 0; i < tempOids.length; i++) {
                    relatedAfterObjOidsStr=relatedAfterObjOidsStr.replace(tempOids[i], "");
                }
                String[] relatedAfterObjOids =relatedAfterObjOidsStr.split("#");
                System.out.println("relatedBeforeObjOids is: " + relatedBeforeObjOids);
                System.out.println("relatedAfterObjOids is: " + relatedAfterObjOids);
                //如果是从对象下拉菜单里，获得的oid是当前对象的oid，可以用来判断是否成套件，进行TopObject设置
                String[] oids = clientData.getTextParameterValues("oid");
                String objoid = new String();
                for(String oid:oids){
                    objoid = oid;
                    System.out.println("oid is:" + oid);
                }
                deleteBeforeObjLink(wtdoc);//modified by liaojun 2012-06-20 将此前移，处理删掉所有偏离前数据不生效的bug
                if(relatedBeforeObjOids!=null && relatedBeforeObjOids.length>0)
                {
//                  deleteBeforeObjLink(wtdoc);
                    saveBeforeObjLink(relatedBeforeObjOids, wtdoc,objectbean);      
                    //建立技术通知单与WTPart的关联
                    deleteDocPartLink(relatedBeforeObjOids,wtdoc);
                    saveDocPartLink(relatedBeforeObjOids,wtdoc);
                }
                if(relatedAfterObjOids!=null && relatedAfterObjOids.length>0)
                {
                    deleteAfterObjLink(wtdoc);
                    saveAfterObjLink(relatedAfterObjOids, wtdoc,objectbean);
                    //建立技术通知单与WTPart的关联
                    deleteDocPartLink(relatedAfterObjOids,wtdoc);
                    saveDocPartLink(relatedAfterObjOids,wtdoc);
                    //ProcessEnvelope proen = createApproveForm(wtdoc);
                    ProcessEnvelope proen = null;
                    QueryResult qr = EnvelopeHelper.service.getEnvelopeByMemberObject(wtdoc);
                    while(qr.hasMoreElements()){
                        proen = ((EnvelopeMemberLink)qr.nextElement()).getProcessEnvelope();
                        String type = IBAHelper.getSoftType(proen);
                        if(type.equals("APPROVEFORM")){
                            break;
                        }
                    }
                    System.out.println("proen is: " + proen);
                    if(proen!=null){
                        deleteRelatedObjLink(proen);
                        saveRelatedObjLink(relatedAfterObjOids,proen);
                        EnvelopeMemberLink envelopememberlink = EnvelopeMemberLink.newEnvelopeMemberLink(proen, wtdoc);
                        PersistenceHelper.manager.save(envelopememberlink);
                    }
                }else{
                    //add by liaojun 2012-06-20，处理删掉所有偏离后数据不生效的bug begin
                    deleteAfterObjLink(wtdoc);
                    ProcessEnvelope proen = null;
                    QueryResult qr = EnvelopeHelper.service.getEnvelopeByMemberObject(wtdoc);
                    while(qr.hasMoreElements()){
                        proen = ((EnvelopeMemberLink)qr.nextElement()).getProcessEnvelope();
                        String type = IBAHelper.getSoftType(proen);
                        if(type.equals("APPROVEFORM")){
                            break;
                        }
                    }
                    if(proen!=null){
                        deleteRelatedObjLink(proen);
                        EnvelopeMemberLink envelopememberlink = EnvelopeMemberLink.newEnvelopeMemberLink(proen, wtdoc);
                        PersistenceHelper.manager.save(envelopememberlink);
                    }
                    //add by liaojun 2012-06-20 ，处理删掉所有偏离后数据不生效的bug end
                    
                }
                
            }
        } while(true);
        
        return phaseResult;

    }
     
     private void deleteDocPartLink(String[] oids,WTDocument wtdoc) 
        throws WTException{
        ReferenceFactory rf = new ReferenceFactory();
            for(String oid:oids){
                System.out.println("***related obj oid="+oid+"***");
                if (oid==null||"".equals(oid)) {
                    continue;
                }
                WTReference ref=rf.getReference(oid);
                Persistable obj=ref.getObject();
                WTPart wtpart = null;
                if(obj!=null && obj instanceof WTPart){
                    wtpart=(WTPart)obj;
                    WTPartReferenceLink wtpartreferencelinkOld = getPartReferenceLink(wtpart, (WTDocumentMaster)wtdoc.getMaster());
                    if (wtpartreferencelinkOld != null) {
                        PersistenceHelper.manager.delete(wtpartreferencelinkOld);
                    }
                }
            }
      }
    
     private void saveDocPartLink(String[] oids,WTDocument wtdoc) throws WTException{
        ReferenceFactory rf = new ReferenceFactory();
            for(String oid:oids){
                System.out.println("***related obj oid="+oid+"***");
                if (oid==null||"".equals(oid)) {
                    continue;
                }
                WTReference ref=rf.getReference(oid);
                Persistable obj=ref.getObject();
                WTPart wtpart = null;
                if(obj!=null && obj instanceof WTPart){
                    wtpart=(WTPart)obj;
                WTPartReferenceLink wtpartreferencelinkOld = getPartReferenceLink(wtpart, (WTDocumentMaster)wtdoc.getMaster());
                if (wtpartreferencelinkOld == null) {
                    WTPartReferenceLink wtpartreferencelink = WTPartReferenceLink
                            .newWTPartReferenceLink(wtpart, (WTDocumentMaster)wtdoc.getMaster());
                    PersistenceServerHelper.manager.insert(wtpartreferencelink);
                }
                }
            }
     }
     
     public static WTPartReferenceLink getPartReferenceLink(WTPart wtpart,
            WTDocumentMaster docMaster) throws WTException {
        QueryResult queryresult = PersistenceHelper.manager.find(
                wt.part.WTPartReferenceLink.class, wtpart,
                WTPartReferenceLink.REFERENCES_ROLE, docMaster);
        if (queryresult == null || queryresult.size() == 0)
            return null;
        else
            return (WTPartReferenceLink) queryresult.nextElement();
    }
     
     private void saveRelatedObjLink(String[] oids,ProcessEnvelope processEnvelope) throws WTException{

        ReferenceFactory rf = new ReferenceFactory();
        for(String oid:oids){
            System.out.println("***related obj oid="+oid+"***");
            if (oid==null||"".equals(oid)) {
                continue;
            }
            WTReference ref=rf.getReference(oid);
            Persistable obj=ref.getObject();
            WTObject wtobject;
            if(obj!=null && obj instanceof WTObject){
                wtobject=(WTObject)obj;
                try
                {
                    System.out.println("***Get related obj:"+wtobject.getIdentity());                
                    System.out.println("***Get processEnvelope:"+processEnvelope.getIdentity());
                    EnvelopeMemberLink envelopememberlink = EnvelopeMemberLink.newEnvelopeMemberLink(processEnvelope,(RevisionControlled)wtobject);                 
                    PersistenceHelper.manager.save(envelopememberlink);
                }
                catch (wt.util.WTException e){
                    e.printStackTrace();
                }
            
            }
        }
    }
    
    private void saveBeforeObjLink(String[] oids,WTDocument wtdoc,ObjectBean objectbean) throws WTException{

        ReferenceFactory rf = new ReferenceFactory();
        for(String oid:oids){
            System.out.println("***related obj oid="+oid+"***");
            if (oid==null||"".equals(oid)) {
                continue;
            }
            WTReference ref=rf.getReference(oid);
            Persistable obj=ref.getObject();
            WTObject wtobject;
            if(obj!=null && obj instanceof WTObject){
                wtobject=(WTObject)obj;
                try
                {
                    System.out.println("***Get related obj:"+wtobject.getIdentity());                
                    System.out.println("***Get processEnvelope:"+wtdoc.getIdentity());
                    TechNoticeBeforeLink technoticebeforelink = TechNoticeBeforeLink.newTechNoticeBeforeLink((RevisionControlled)wtdoc,(RevisionControlled)wtobject);                   
                    PersistenceHelper.manager.save(technoticebeforelink);
                }
                catch (wt.util.WTException e){
                    e.printStackTrace();
                }
            
            }
        }
    }
    
    private void saveAfterObjLink(String[] oids,WTDocument wtdoc,ObjectBean objectbean) throws WTException{

        ReferenceFactory rf = new ReferenceFactory();
        for(String oid:oids){
            System.out.println("***related obj oid="+oid+"***");
            if (oid==null||"".equals(oid)) {
                continue;
            }
            WTReference ref=rf.getReference(oid);
            Persistable obj=ref.getObject();
            WTObject wtobject;
            if(obj!=null && obj instanceof WTObject){
                wtobject=(WTObject)obj;
                try
                {
                    System.out.println("***Get related obj:"+wtobject.getIdentity());                
                    System.out.println("***Get processEnvelope:"+wtdoc.getIdentity());
                    TechNoticeAfterLink technoticeafterlink = TechNoticeAfterLink.newTechNoticeAfterLink((RevisionControlled)wtdoc,(RevisionControlled)wtobject);                   
                    PersistenceHelper.manager.save(technoticeafterlink);
                }
                catch (wt.util.WTException e){
                    e.printStackTrace();
                }
            
            }
        }
    }
    
    /**
     * @param processEnvelope
     * @return void
     * @exception WTException
     **/
    private void deleteRelatedObjLink(ProcessEnvelope processEnvelope) 
        throws WTException{
        QueryResult qr = PersistenceHelper.manager.navigate(processEnvelope, "theRevisionControlled", ext.ases.envelope.EnvelopeMemberLink.class, false);
        while(qr.hasMoreElements())
        {
            EnvelopeMemberLink link = (EnvelopeMemberLink)qr.nextElement();
            PersistenceHelper.manager.delete(link);
        }
    }
    
    private void deleteBeforeObjLink(WTDocument wtdoc) 
        throws WTException{
        QueryResult qr = PersistenceHelper.manager.navigate(wtdoc, "beforeObject", ext.ases.technotice.TechNoticeBeforeLink.class, false);
        while(qr.hasMoreElements())
        {
            TechNoticeBeforeLink link = (TechNoticeBeforeLink)qr.nextElement();
            PersistenceHelper.manager.delete(link);
        }
    }
    
    private void deleteAfterObjLink(WTDocument wtdoc)  
        throws WTException{
    QueryResult qr = PersistenceHelper.manager.navigate(wtdoc, "afterObject", ext.ases.technotice.TechNoticeAfterLink.class, false);
    while(qr.hasMoreElements())
    {
        TechNoticeAfterLink link = (TechNoticeAfterLink)qr.nextElement();
        PersistenceHelper.manager.delete(link);
    }
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
            System.out.println("preProcess(): translated TypeInstance into Persistable: " + obj.toString());
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
            System.out.println("setRefreshInfo() returning:");
            for (DynamicRefreshInfo ri : refreshInfoList) {
                System.out.println(ri.toString());
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
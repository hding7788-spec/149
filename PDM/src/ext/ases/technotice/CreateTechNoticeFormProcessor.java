/**
 * @(#)CreateTechNoticeFormProcessor.java
 *
 *
 * @author Leon Zhang
 * @version 1.00 2010/1/5
 *//*
package ext.ases.technotice;

import com.glaway.mpm.pbombuilder.util.CmIBAHelper;
import com.ptc.core.command.server.delegate.ServerCommandDelegateUtility;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.*;
import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.AttributeTypeIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.type.common.TypeInstance;
import com.ptc.core.security.utils.SecurityLabelsHelper;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.copy.server.CoreMetaUtility;
import com.ptc.windchill.enterprise.doc.forms.CreateDocFormProcessor;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.constants.Constants;
import ext.casc.listener.ExtStandardListenerService;
import ext.casc.util.IBAUtility;
import ext.cirpoint.securitymgr.constant.AttributesConstant;
import ext.cirpoint.securitymgr.doc.forms.NfCreateDocFormProcessor;
import org.apache.log4j.Logger;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.enterprise.RevisionControlled;
import wt.fc.*;
import wt.fc.collections.WTValuedHashMap;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.iba.value.AttributeContainer;
import wt.iba.value.litevalue.AbstractValueViewMap;
import wt.iba.value.service.MultiObjIBAValueDBService;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleManaged;
import wt.log4j.LogR;
import wt.method.MethodContext;
import wt.part.WTPart;
import wt.part.WTPartReferenceLink;
import wt.session.SessionHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.rmi.RemoteException;
import java.util.*;

public class CreateTechNoticeFormProcessor extends CreateDocFormProcessor{

    private static final String RESOURCE = "ext.ases.technotice.technoticeResource";
    private static final Logger log;

    static {
       try {
          log = LogR.getLogger(CreateTechNoticeFormProcessor.class.getName());
       }
       catch (Exception e) {
          throw new ExceptionInInitializerError(e);
       }
    }

    @Override
    public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {
        FormResult phaseResult = new FormResult();
        phaseResult = new FormResult();
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
                PersistenceHelper.manager.refresh(wtdoc);

                Map<String, String> attrMap = (Map<String, String>)clientData.getRequest().getSession().getAttribute("attrMap");
                System.out.println("-------attrMap:"+attrMap);
                if (attrMap!=null) {
                    Iterator<String> iterator = attrMap.keySet().iterator();
                    IBAUtility ibaUtility = new IBAUtility(wtdoc);
                    while(iterator.hasNext()){
                        String key = iterator.next();
                        System.out.println("-------key:"+key);
                        try {
                            ibaUtility.setIBAValue(key, attrMap.get(key));
                        } catch (WTPropertyVetoException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        wtdoc = (WTDocument)ibaUtility.updateAttributeContainer(wtdoc);
                        ibaUtility.updateIBAHolder(wtdoc);
                    } catch (WTPropertyVetoException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

//                String[] relatedBeforeObjOids =objectbean.getTextParameterValues("changeTask_affectedItems_table__objRef");
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
                if(relatedBeforeObjOids!=null && relatedBeforeObjOids.length>0)
                {
                    saveBeforeObjLink(relatedBeforeObjOids, wtdoc,objectbean);
                    //建立技术通知单与WTPart的关联
                    saveDocPartLink(relatedBeforeObjOids,wtdoc);
                }

                if(relatedAfterObjOids!=null && relatedAfterObjOids.length>0)
                {
                    saveAfterObjLink(relatedAfterObjOids, wtdoc,objectbean);
                     移到文档提交签审时创建批量签审单
                    ProcessEnvelope proen = createApproveForm(wtdoc);
                    if(proen!=null)
                        saveRelatedObjLink(relatedAfterObjOids,proen);

                    //建立技术通知单与WTPart的关联
                    saveDocPartLink(relatedAfterObjOids,wtdoc);
                }

                ReferenceFactory rf = new ReferenceFactory();
        		String secretValue = null;
        	    if (wtdoc instanceof Persistable) {

        			String pOid = rf.getReferenceString((Persistable) wtdoc);
        			if (SecurityLabelsHelper.isSecurityLabelsExposed(NmOid.newNmOid(pOid))) {
        				Persistable p = (Persistable) wtdoc;
            			secretValue = ext.casc.util.IBAHelper.getIBAStringValue((WTObject) p, "SECRET");
            			if (secretValue != null && !"".equals(secretValue)){
            				if (p instanceof RevisionControlled) {
                				RevisionControlled rc = (RevisionControlled) p;
                				ExtStandardListenerService.setSecurityLabels(rc, "MIJI", Constants.miji.get(secretValue));
                			}
            			}
        			}
        		}

            }
        } while(true);

        return phaseResult;

    }

     protected Locale getLocale()
     {
         Locale locale = null;
         try
         {
             locale = SessionHelper.getLocale();
         }
         catch(WTException wtexception)
         {
             locale = Locale.getDefault();
         }
         return locale;
     }

     private void saveDocPartLink(String[] oids,WTDocument wtdoc) throws WTException{
        ReferenceFactory rf = new ReferenceFactory();
        for(String oid:oids){
            System.out.println("***related obj oid="+oid+"***");
            if (oid==null || "".equals(oid)) {
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

     public static ProcessEnvelope createApproveForm(WTDocument wtd) {
        String objName = null, objNum = null;
        ProcessEnvelope proen = null;

        try {
            proen = ProcessEnvelope.newProcessEnvelope();
            String type = "WCTYPE|ext.ases.envelope.ProcessEnvelope|casc.sast.149.APPROVEFORM";
            TypeIdentifier id = TypeHelper.getTypeIdentifier(type);
            SessionHelper.manager.setPrincipal(wtd.getCreator().getName());
            objName = wtd.getName();
            objNum = wtd.getNumber();
            proen.setNumber("TN_" + objNum);
            proen.setName("技术通知单签审_" + objNum + "_" + objName);

            proen = (ProcessEnvelope) CoreMetaUtility.setType(proen, id);
            AttributeContainer attriCon = wtd.getAttributeContainer();
            WTContainerRef crf = wtd.getContainerReference();

            Folder location = wtd.getFolderingInfo().getFolder();
            if (location != null) {
                WTValuedHashMap map = new WTValuedHashMap();
                map.put(proen, location);
                FolderHelper.assignLocations(map);
            }
            WTContainer container = wtd.getContainer();
            proen.setContainer(container);
            proen.setContainerReference(crf);
            proen = (ProcessEnvelope)PersistenceHelper.manager.save(proen);
            EnvelopeMemberLink envelopememberlink = EnvelopeMemberLink.newEnvelopeMemberLink(proen, wtd);
            PersistenceHelper.manager.save(envelopememberlink);
        } catch (WTException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proen;
    }

     private void saveRelatedObjLink(String[] oids,ProcessEnvelope processEnvelope) throws WTException{

        ReferenceFactory rf = new ReferenceFactory();
        for(String oid:oids){
            System.out.println("***related obj oid="+oid+"***");
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

    private Vector checkAfterObjectState(String[] oids) throws WTException{
        Vector validVector = new Vector();
        ReferenceFactory rf = new ReferenceFactory();
        for(String oid:oids){
            System.out.println("***related obj oid="+oid+"***");
            if (oid==null||"".equals(oid)) {
                continue;
            }
            WTReference ref=rf.getReference(oid);
            Persistable obj=ref.getObject();
            LifeCycleManaged lcm = null;
            if(obj!=null && obj instanceof LifeCycleManaged){
                lcm=(LifeCycleManaged)obj;
                String state = lcm.getLifeCycleState().toString();
                if(!state.equalsIgnoreCase("INWORK"))
                    validVector.add(lcm);
            }
        }
        return validVector;
    }

    @Override
    public FormResult preProcess(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {

        if (log.isDebugEnabled()) {
            System.out.println("\npreProcess() - request parameters on entry:");
            System.out.println(clientData.requestParametersToString());
        }
        FormResult phaseResult = new FormResult(FormProcessingStatus.SUCCESS);
        for (ObjectBean objBean : objectBeans) {
            FormResult itemResult = new FormResult(FormProcessingStatus.SUCCESS);
            objBean.setObject(createItemInstance(clientData, objBean, itemResult));
            phaseResult = mergeIntermediateResult(phaseResult, itemResult);
            if (!continueProcessing(phaseResult)) {
                return phaseResult;
            }
        }

        FormResult superResult = super.preProcess(clientData, objectBeans);
        phaseResult = mergeIntermediateResult(phaseResult, superResult);
        System.out.println("superResult:-------------" + superResult);
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

    *//**
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
     *//*

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

        if (obj instanceof WTDocument) {
            try {
                String str = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(obj);
                WTDocument document = (WTDocument)obj;
                String type = "wt.doc.WTDocument|casc.sast.149.DESIGN_DOC|casc.sast.149.TECHNOTICE_DOC";
                try {
                    TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(type);
                    document.setTypeDefinitionReference(tdr);
                    AttributeTypeIdentifier[] attr = ti.getAttributeTypeIdentifiers();
                    Map<String, String> attrMap = new HashMap<String, String>();
                    if (attr!=null) {
                        for (AttributeTypeIdentifier attributeTypeIdentifier : attr) {
                            Object single = ti.getSingle(attributeTypeIdentifier);
                            String attrName = attributeTypeIdentifier.getAttributeName();
                            if (single == null) {
                                continue;
                            }
                            String attrValue = String.valueOf(single);
                            if (attributeTypeIdentifier.toString().indexOf("IBA")>0) {
                                attrMap.put(attrName, attrValue);
                            }
                        }
                    }
                    clientData.getRequest().getSession().setAttribute("attrMap", attrMap);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (WTPropertyVetoException e) {
                    e.printStackTrace();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

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

    *//**
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
     *//*

    protected Persistable translateTIToPersistable(TypeInstance ti, ObjectBean objBean) throws WTException {
        Persistable obj = null;
        return ServerCommandDelegateUtility.translate(ti, obj);
    }

    *//**
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
     *//*
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

    *//**
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
     *//*

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
        //add by liaojun 2012-06-21 在创建技术通知单时，将相关信息写入word文档  begin
        Iterator iterator1 = objectBeans.iterator();
        WTDocument wtdoc = null;
        do {
            if (!iterator1.hasNext()) {
                break;
            }
            ObjectBean objectbean = (ObjectBean) iterator1.next();
            if (objectbean.getObject() != null
                    && (objectbean.getObject() instanceof Persistable)) {
                wtdoc = (WTDocument) objectbean.getObject();
                PersistenceHelper.manager.refresh(wtdoc);
            }
        }while(true);
//
//      Transaction trans = new Transaction();
//      trans.start();
//      PersistenceHelper.manager.lockAndRefresh(wtdoc);
//      ContentHolder contentholder = wtdoc;
//        try
//        {
//            contentholder = ContentHelper.service.getContents(contentholder);
//        }
//        catch(PropertyVetoException e1)
//        {
//            e1.printStackTrace();
//        }
//        Vector vector = ContentHelper.getContentList(contentholder);
//        ContentItem contentitem = ContentHelper.getPrimary((FormatContentHolder)contentholder);
//        ApplicationData applicationdata = (ApplicationData)contentitem;
//        String fileName = applicationdata.getFileName();
//        Streamed streamed = (Streamed)applicationdata.getStreamData().getObject();
//        InputStream is = streamed.retrieveStream();
//        try
//        {
//            POIFSFileSystem fs = new POIFSFileSystem(is);
//            HWPFDocument doc = new HWPFDocument(fs);
//            Range range = doc.getOverallRange();
//            range.insertAfter("技术通知单模板\13测试");
//            TableIterator ti = new TableIterator(range);
//            int i = 0;
//            while (ti.hasNext()) {
//                System.out.println("Getting table!");
//                Table table = ti.next();
//                System.out.println((new StringBuilder("Number of rows: ")).append(table.numRows()).toString());
//                for(int a = 0; a < table.numRows(); a++)
//                {
//                    TableRow row = table.getRow(a);
//                    System.out.println((new StringBuilder("\tTable row number: ")).append(a).toString());
//                    for(int b = 0; b < row.numCells(); b++)
//                    {
//                        System.out.println("\t\tTable cell number: " + b);
//                        TableCell tablecell = row.getCell(b);
//                        System.out.println(tablecell.text());
//                        if (a==1&&b==5) {
//                          tablecell.insertBefore("805PABCDEF01");
//                      }
//                        if (a==2&&b==0) {
//                          tablecell.insertBefore("秘密");
//                      }
//
//                        if (a==2&&b==5) {
//                          tablecell.insertBefore("转阶\13生产");
//                      }
//                    }
//
//                }
//                System.out.println("\n");
//            }
//            ContentServerHelper.service.deleteContent(wtdoc, contentitem);
//            WTProperties prop = WTProperties.getLocalProperties();
//            String tempPath = prop.getProperty("wt.temp");
//            File temp = new File(tempPath+File.separator+"temp.doc");
//            FileOutputStream out = new FileOutputStream(temp);
//            doc.write(out);
//            out.flush();
//            out.close();
//            FileInputStream in = new FileInputStream(temp);
//            ApplicationData newapplicationdata = ApplicationData.newApplicationData(wtdoc);
//            newapplicationdata.setFileName(fileName);
//            newapplicationdata.setRole(ContentRoleType.PRIMARY);
//            newapplicationdata = ContentServerHelper.service.updateContent(wtdoc, newapplicationdata, in);
//            in.close();
//            temp.delete();
//            trans.commit();
//            trans= null;
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
      //add by liaojun 2012-06-21 在创建技术通知单时，将相关信息写入word文档  end
        return result;
    }

    *//**
     * This method will remove the refresh info for any object which was created in a folder we're not currently looking
     * at. It makes the assumption that there is a single default cabinet for the Folders page.
     *
     * <BR>
     * <BR>
     * <B>Supported API: </B>false
     *//*
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


    *//**
	 * 验证文档的附件 主要内容 和密级属性3者是不是唯一的
	 *//*
	public FormResult postProcess(NmCommandBean paramNmCommandBean, List<ObjectBean> paramList)throws WTException{
		FormResult formresult = super.postProcess(paramNmCommandBean, paramList);
		Iterator it = paramList.iterator();
		WTDocument doc=null;
		while (it.hasNext()) {
			ObjectBean ob = (ObjectBean) it.next();
			doc = (WTDocument) ob.getObject();
		}
		String objMiji="";
		try {
			objMiji=CmIBAHelper.getIBAValue(doc, "SECRET");
			System.out.println(">>>>>>>"+objMiji);
//			objMiji = ObjectUtil.getObjMiji("文档",doc);
//			if(objMiji==null||"".equals(objMiji)||"NULL".equals(objMiji)){
//				objMiji=CmIBAHelper.getIBAValue(doc, "SECRET");
//				System.out.println(">>>>>>>"+objMiji);
//			}
			if(AttributesConstant.MIMI.equals(objMiji)){
				objMiji="秘密";
			}else if(AttributesConstant.JIMI.equals(objMiji)){
				objMiji="机密";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList<String> mijiList=new ArrayList<String>();
		String primaryFileName=NfCreateDocFormProcessor.getPrimaryDocByObj(doc);
		String miji="";
		if(!primaryFileName.equals("")){
			if((primaryFileName.contains("(")&&primaryFileName.contains(""))||(primaryFileName.contains("（")&&primaryFileName.contains("）"))){

				if(primaryFileName.contains("(")&&primaryFileName.contains(")")){
					miji=primaryFileName.substring(primaryFileName.indexOf("(")+1,primaryFileName.indexOf(")"));
				}

				if(primaryFileName.contains("（")&&primaryFileName.contains("）")){
					miji=primaryFileName.substring(primaryFileName.indexOf("（")+1,primaryFileName.indexOf("）"));
				}

				if(AttributesConstant.MIMI.equals(miji)){
					miji="秘密";
				}else if(AttributesConstant.JIMI.equals(miji)){
					miji="机密";
				}
				mijiList.add(miji);
			}else{
				throw new WTException("主要内容命名并不规范，请确保有() 或者（）!");
			}
		}

		ArrayList<String> fileList=NfCreateDocFormProcessor.getSecondrayDocByObj(doc);
		if(fileList.size()!=0){
			for(int i=0;i<fileList.size();i++){
				String secendFileName=fileList.get(i);
				if("PDFCoverPreview.pdf".equals(secendFileName)){
				    continue;
                }
				if((secendFileName.contains("(")&&secendFileName.contains(""))||(secendFileName.contains("（")&&secendFileName.contains("）"))){
					if(secendFileName.contains("(")&&secendFileName.contains(")")){
						miji=secendFileName.substring(secendFileName.indexOf("(")+1,secendFileName.indexOf(")"));
					}

					if(secendFileName.contains("（")&&secendFileName.contains("）")){
						miji=secendFileName.substring(secendFileName.indexOf("（")+1,secendFileName.indexOf("）"));
					}
					if(AttributesConstant.MIMI.equals(miji)){
						miji="秘密";
					}else if(AttributesConstant.JIMI.equals(miji)){
						miji="机密";
					}
					if(!mijiList.contains(miji)){
						mijiList.add(miji);
					}
				}else{
					throw new WTException("附件"+secendFileName+"命名并不规范，请确保有() 或者（）!");
				}
			}
		}

		if(mijiList.size()>1){
			throw new WTException("主要内容和附件的密级不一致，请检查!");
		}

//		for(int i=0;i<mijiList.size();i++){
//			if(!objMiji.contains(mijiList.get(i))){
//				throw new WTException("对象密级和主要内容及附件的密级不一致，请检查!");
//			}
//		}

		if(!mijiList.contains(objMiji)){
			mijiList.add(objMiji);
		}

		if(mijiList.size()>1){
			throw new WTException("对象密级和主要内容及附件的密级不一致，请检查!");
		}

		return formresult;

	}

}*/
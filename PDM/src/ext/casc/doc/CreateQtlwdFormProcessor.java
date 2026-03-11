/**
 *
 *
 *
 * @author Leon Zhang
 * @version 1.00 2010/1/5
 */
package ext.casc.doc;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

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
import wt.fc.collections.WTValuedHashMap;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.iba.value.AttributeContainer;
import wt.iba.value.litevalue.AbstractValueViewMap;
import wt.iba.value.service.IBAValueHelper;
import wt.iba.value.service.MultiObjIBAValueDBService;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleManaged;
import wt.log4j.LogR;
import wt.method.MethodContext;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartReferenceLink;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.util.IBAHelper;
import com.ptc.core.command.server.delegate.ServerCommandDelegateUtility;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.CreateEditFormProcessorHelper;
import com.ptc.core.components.forms.CreateObjectFormProcessor;
import com.ptc.core.components.forms.DynamicRefreshInfo;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.AttributeTypeIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.type.common.TypeInstance;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.copy.server.CoreMetaUtility;
import com.ptc.windchill.enterprise.doc.forms.CreateDocFormProcessor;

import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.util.IBAUtility;

public class CreateQtlwdFormProcessor extends CreateDocFormProcessor{

    private static final Logger log;

    static {
       try {
          log = LogR.getLogger(CreateQtlwdFormProcessor.class.getName());
       }
       catch (Exception e) {
          throw new ExceptionInInitializerError(e);
       }
    }

    @SuppressWarnings("deprecation")
	@Override
    public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {
        FormResult phaseResult = new FormResult();
        phaseResult = new FormResult();
        phaseResult.setStatus(FormProcessingStatus.SUCCESS);
//        phaseResult = super.doOperation(clientData, objectBeans);
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

                String[] oids = clientData.getTextParameterValues("oid");
                String objoid = new String();
                for(String oid:oids){
                    objoid = oid;
                    System.out.println("oid is:" + oid);
                }
                    try {
						saveDocPartLink(oids,wtdoc);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (WTPropertyVetoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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

     private void saveDocPartLink(String[] oids,WTDocument wtdoc) throws Exception{
        ReferenceFactory rf = new ReferenceFactory();
        String oid=oids[0];
            System.out.println("***related obj oid="+oid+"***");
            if (oid!=null &&! "".equals(oid)) {

            WTReference ref=rf.getReference(oid);
            Persistable obj=ref.getObject();
            WTPart wtpart = null;
            if(obj!=null && obj instanceof WTPart){
                wtpart=(WTPart)obj;
                TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference("casc.sast.149.QITALEIWENDANG");
				wtdoc.setTypeDefinitionReference(tdr);
				wtdoc = (WTDocument) PersistenceHelper.manager.save(wtdoc);
                 String pindex = ext.casc.util.IBAHelper.getIBAStringValue(wtpart, "PINDEX");
                 String mindex = ext.casc.util.IBAHelper.getIBAStringValue(wtpart, "MINDEX");
//                 ext.casc.util.IBAHelper.setIBAStringValue(wtdoc, "MINDEX", mindex);
//                 ext.casc.util.IBAHelper.setIBAStringValue(wtdoc, "PINDEX", pindex);
                 IBAHelper iba = new IBAHelper(wtdoc);
     			iba.setIBAValue(wtdoc, "PINDEX",pindex);
     			iba.setIBAValue(wtdoc, "MINDEX",mindex);
     			iba.updateAttributeContainer(wtdoc);
     			iba.updateIBAHolder(wtdoc);
//				IBAUtility ibaUtility = new IBAUtility(wtdoc);
//	            ibaUtility.setIBAValue("MINDEX", ext.casc.util.IBAHelper.getIBAValueOfObject(wtpart, "MINDEX"));
//	            ibaUtility.setIBAValue("PINDEX", ext.casc.util.IBAHelper.getIBAValueOfObject(wtpart, "PINDEX"));
//	            wtdoc = (WTDocument)ibaUtility.updateAttributeContainer(wtdoc);
//                ibaUtility.updateIBAHolder(wtdoc);
				 // 保存为持久对象

				String ibaStringValue = ext.casc.util.IBAHelper.getIBAStringValue(wtdoc,"MINDEX");



                WTPartReferenceLink wtpartreferencelinkOld = getPartReferenceLink(wtpart, (WTDocumentMaster)wtdoc.getMaster());
                if (wtpartreferencelinkOld == null) {
                   /* WTPartReferenceLink wtpartreferencelink = WTPartReferenceLink
                            .newWTPartReferenceLink(wtpart, (WTDocumentMaster)wtdoc.getMaster());*/
                	WTPartDescribeLink wtpartdescribelink=WTPartDescribeLink.newWTPartDescribeLink(wtpart, (WTDocument)wtdoc);
                    PersistenceServerHelper.manager.insert(wtpartdescribelink);
                    PersistenceHelper.manager.refresh(wtpartdescribelink);

                }
            }
        }
     }

      public static WTPartReferenceLink getPartReferenceLink(WTPart wtpart,
            WTDocumentMaster docMaster) throws WTException {
        /*QueryResult queryresult = PersistenceHelper.manager.find(
                wt.part.WTPartReferenceLink.class, wtpart,
                WTPartReferenceLink.REFERENCES_ROLE, docMaster);*/
    	  QueryResult queryresult = PersistenceHelper.manager.find(
                  wt.part.WTPartDescribeLink.class, wtpart,
                  WTPartDescribeLink.DESCRIBES_ROLE, docMaster);
        if (queryresult == null || queryresult.size() == 0)
            return null;
        else
            return (WTPartReferenceLink) queryresult.nextElement();
    }








}
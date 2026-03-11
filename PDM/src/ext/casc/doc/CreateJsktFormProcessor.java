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
import wt.iba.value.service.MultiObjIBAValueDBService;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleManaged;
import wt.log4j.LogR;
import wt.method.MethodContext;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartReferenceLink;
import wt.rule.init.InitRuleHelper;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.util.FolderUtil;
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
import ext.casc.doc.technology.TechnicsTechnologyTreeHander;
import ext.casc.util.IBAUtility;

public class CreateJsktFormProcessor extends CreateDocFormProcessor{

    private static final Logger log;

    static {
       try {
          log = LogR.getLogger(CreateJsktFormProcessor.class.getName());
       }
       catch (Exception e) {
          throw new ExceptionInInitializerError(e);
       }
    }

    @SuppressWarnings("deprecation")
	@Override
    public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {
    	boolean enforce = SessionServerHelper.manager
                .setAccessEnforced(false);
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
                try {
                	TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference("casc.sast.149.JISHUKETI");
					wtdoc.setTypeDefinitionReference(tdr);
					String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(wtdoc);
					if(docType.endsWith("casc.sast.149.JISHUKETI")){
						String docFolder = "/Default/10技术课题类报告";
						FolderUtil.setDocFolder(docFolder, wtdoc);
					}
					 // 保存为持久对象
					wtdoc = (WTDocument) PersistenceHelper.manager.save(wtdoc);
				} catch (WTPropertyVetoException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

                String style = TypeHelper.getLocalizedTypeString(wtdoc, Locale.CHINA);
                String[] oids = clientData.getTextParameterValues("oid");
                String objoid = new String();
                for(String oid:oids){
                    objoid = oid;
                    System.out.println("oid is:" + oid);
                }
                    try {
                        saveDocPartLink(oids,wtdoc);
                    } catch (WTPropertyVetoException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

            }
            SessionServerHelper.manager.setAccessEnforced(enforce);
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

     private void saveDocPartLink(String[] oids,WTDocument wtdoc) throws WTException, WTPropertyVetoException, RemoteException{
		ReferenceFactory rf = new ReferenceFactory();
		String oid = oids[0];
		System.out.println("***related obj oid=" + oid + "***");
		if (oid != null && !"".equals(oid)) {
			com.glaway.mpm.util.IBAHelper iba = new com.glaway.mpm.util.IBAHelper(wtdoc);
			String subType = iba.getIBAValue("SUBTYPE");
			String style = TypeHelper.getLocalizedTypeString(wtdoc, Locale.CHINA);
			String childNumber = TechnicsTechnologyTreeHander.getChildNumber(style, subType);
			iba.setIBAValue("SUBTYPE", childNumber);
			iba.updateAttributeContainer(wtdoc);
			iba.updateIBAHolder(wtdoc);
		}

      }

      public static WTPartReferenceLink getPartReferenceLink(WTPart wtpart,
            WTDocumentMaster docMaster) throws WTException {
      /*  QueryResult queryresult = PersistenceHelper.manager.find(
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
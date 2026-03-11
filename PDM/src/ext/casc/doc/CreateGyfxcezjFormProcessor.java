/**
 *
 *
 *
 * @author Leon Zhang
 * @version 1.00 2010/1/5
 */
package ext.casc.doc;

import com.glaway.mpm.util.FolderUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.doc.forms.CreateDocFormProcessor;
import ext.casc.doc.technology.TechnicsTechnologyTreeHander;
import org.apache.log4j.Logger;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.*;
import wt.folder.Folder;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartReferenceLink;
import wt.rule.algorithm.InvalidAlgorithmArgumentException;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class CreateGyfxcezjFormProcessor extends CreateDocFormProcessor{

    private static final Logger log;

    static {
       try {
          log = LogR.getLogger(CreateGyfxcezjFormProcessor.class.getName());
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
                Folder folder = null;
                try {
                	TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference("casc.sast.149.GONGYIFENXICEHUAZONGJIE");
					wtdoc.setTypeDefinitionReference(tdr);
					String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(wtdoc);
					if(docType.endsWith("casc.sast.149.GONGYIFENXICEHUAZONGJIE")){
						String docFolder = "/Default/02工艺文件/13工艺分析策划总结";
						FolderUtil.setDocFolder(docFolder, wtdoc);
					}
					 // 保存为持久对象
					wtdoc = (WTDocument) PersistenceHelper.manager.save(wtdoc);

                    //创建PDF封面，上传到附件
                    DocumentUtil.createPdfCover(wtdoc);

				} catch (WTPropertyVetoException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidAlgorithmArgumentException e){
					e.printStackTrace();
				}

                /*try {
					String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(objectbean.getObject());
					System.out.println("-------"+docType);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
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
        String oid=oids[0];
            System.out.println("***related obj oid="+oid+"***");
            if (oid!=null && !"".equals(oid)) {
            WTReference ref=rf.getReference(oid);
            Persistable obj=ref.getObject();
            WTPart wtpart = null;
            if(obj!=null && obj instanceof WTPart){
                wtpart=(WTPart)obj;
                WTPartReferenceLink wtpartreferencelinkOld = getPartReferenceLink(wtpart, (WTDocumentMaster)wtdoc.getMaster());
                if (wtpartreferencelinkOld == null) {
                    com.glaway.mpm.util.IBAHelper iba = new com.glaway.mpm.util.IBAHelper(wtdoc);
                    String subType = iba.getIBAValue("SUBTYPE");
                    String style = TypeHelper.getLocalizedTypeString(wtdoc, Locale.CHINA);
                    String childNumber = TechnicsTechnologyTreeHander.getChildNumber(style, subType);
                    iba.setIBAValue("SUBTYPE", childNumber);
                    iba.updateAttributeContainer(wtdoc);
                    iba.updateIBAHolder(wtdoc);

                	WTPartDescribeLink wtpartdescribelink=WTPartDescribeLink.newWTPartDescribeLink(wtpart, (WTDocument)wtdoc);
                    PersistenceServerHelper.manager.insert(wtpartdescribelink);
                    PersistenceHelper.manager.refresh(wtpartdescribelink);

                }
            }
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
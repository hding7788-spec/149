/**
 *
 *
 *
 * @author Leon Zhang
 * @version 1.00 2010/1/5
 */
package ext.casc.dfmRule.processor;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartReferenceLink;
import wt.rule.algorithm.InvalidAlgorithmArgumentException;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.util.FolderUtil;
import com.glaway.mpm.util.IBAHelper;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.doc.forms.CreateDocFormProcessor;

import ext.casc.doc.technology.TechnicsTechnologyTreeHander;

public class CreateDfmRuleFormProcessor extends CreateDocFormProcessor{

    private static final Logger log;

    static {
       try {
          log = LogR.getLogger(CreateDfmRuleFormProcessor.class.getName());
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
					 // 保存为持久对象
                	IBAHelper helper = new IBAHelper(wtdoc);
                	String ruleType = helper.getIBAValue("RULETYPE");
                	String docFolder = "/Default/" + ruleType;
                	FolderUtil.setDocFolder(docFolder, wtdoc);
					wtdoc = (WTDocument) PersistenceHelper.manager.save(wtdoc);

				} catch (InvalidAlgorithmArgumentException e){
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

}
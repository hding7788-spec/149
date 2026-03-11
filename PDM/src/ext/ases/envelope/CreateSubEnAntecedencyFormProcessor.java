package ext.ases.envelope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTRuntimeException;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.CreateObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.meta.common.TypeIdentifierHelper;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.part.PackagedPartHelper;
import ext.casc.util.IBAHelper;

public class CreateSubEnAntecedencyFormProcessor extends CreateObjectFormProcessor{
    private static final Logger log;

    static {
       try {
          log = LogR.getLogger(CreateSubEnAntecedencyFormProcessor.class.getName());
       }
       catch (Exception e) {
          throw new ExceptionInInitializerError(e);
       }
    }
    
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
                String PEType = TypeIdentifierHelper.getType(processEnvelope).toString();
				String[] oids = clientData.getTextParameterValues("oid");
                String[] relatedObjOids =objectbean.getTextParameterValues("change_affectedData_table__objRef");

                log.debug("relatedObjectOids is: " + relatedObjOids);
                if(PEType.indexOf("ANTECEDENCYFORM")!=-1){
                	ReferenceFactory rf = new ReferenceFactory();
                	Object obj = (WTObject)rf.getReference(oids[0]).getObject();
                	if (obj instanceof WTPart){
                		WTPart part = (WTPart)obj;
                		String isPakagedFlag = ext.casc.util.IBAHelper.getIBAStringValue(part, "SETMARK");
                    	if(isPakagedFlag.equals("是")){
                    		saveTopObjLink(part,processEnvelope);
                    	}
                	}
                }
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
}

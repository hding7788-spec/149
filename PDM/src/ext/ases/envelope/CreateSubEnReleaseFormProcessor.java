package ext.ases.envelope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import wt.doc.WTDocument;
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
//import ext.csc.utilities.util.CSCDebug;

public class CreateSubEnReleaseFormProcessor extends CreateObjectFormProcessor{
    private static final Logger log;

    static {
       try {
          log = LogR.getLogger(CreateSubEnReleaseFormProcessor.class.getName());
       }
       catch (Exception e) {
          throw new ExceptionInInitializerError(e);
       }
    }
    
    public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {
        //String relatedObjTableID=nmcommandbean.getTextParameter("relatedObjTable_id");
//    	CSCDebug.outDebugInfo("Now you enter CreateSubEnReleaseFormProcessor function!");
        FormResult phaseResult = new FormResult();
        phaseResult.setStatus(FormProcessingStatus.SUCCESS);
        phaseResult = super.doOperation(clientData, objectBeans);
        log.debug("phaseResult:" + phaseResult);
        Iterator iterator1= objectBeans.iterator();
        WTObject obj = null;
        do
        {
            if(!iterator1.hasNext())
            {
                break;
            }
            ObjectBean objectbean = (ObjectBean)iterator1.next();
            if (objectbean.getObject() != null && (objectbean.getObject() instanceof Persistable)) {
            	ProcessEnvelope processEnvelope = (ProcessEnvelope)objectbean.getObject();
                PersistenceHelper.manager.refresh(processEnvelope);
                String PEType = TypeIdentifierHelper.getType(processEnvelope).toString();
				String[] oids = clientData.getTextParameterValues("oid");
				
                String relatedObjInitOids =objectbean.getTextParameter("initialRows_change_affectedData_table");
                String relatedObjAddOids =objectbean.getTextParameter("addRows_change_affectedData_table");
                String relatedObjRmOids =objectbean.getTextParameter("rmRows_change_affectedData_table");
                String relatedObjOidsStr=relatedObjInitOids+relatedObjAddOids;
                String[] tempOids = relatedObjRmOids.split("#");
                for (int i = 0; i < tempOids.length; i++) {
                    relatedObjOidsStr=relatedObjOidsStr.replace(tempOids[i], "");
                }
                String[] relatedObjOids =relatedObjOidsStr.split("#");
                List<String> oidList = null;
                log.debug("relatedObjectOids is: " + relatedObjOids);
                if(PEType.indexOf(ProcessEnvelopeConstants.RELEASEFORM)!=-1){
                	oidList = filterWTObject(relatedObjOids);
                	log.debug("oidList is "+oidList);
                	ReferenceFactory rf = new ReferenceFactory();
                	obj = (WTObject)rf.getReference(oids[0]).getObject();
                	if (obj instanceof WTPart){
                		WTPart part = (WTPart)obj;
                		String isPakagedFlag = IBAHelper.getIBAStringValue(part, "SETMARK");
                    	if(isPakagedFlag.equals("是")){
                    		saveTopObjLink(part,processEnvelope);
                    	}
                	}
                }
				if(relatedObjOids!=null && relatedObjOids.length>0)
				{
					saveRelatedObjLink(oidList, processEnvelope,objectbean);		
					
				}
			}
        } while(true);
        
        return phaseResult;

    }
    
    private List<String> filterDoc(String[] oids)throws WTRuntimeException, WTException{
    	List<String> oidList = new ArrayList<String>();
    	ReferenceFactory rf = new ReferenceFactory();
    	for(String oid : oids){
    		WTObject wto = (WTObject)rf.getReference(oid).getObject();
    		if(wto instanceof WTDocument){
    			oidList.add(oid);
    		}
    	}
    	return oidList;
    }

    private List<String> filterWTObject(String[] oids) throws WTRuntimeException, WTException {
        List<String> oidList = new ArrayList<String>();
        ReferenceFactory rf = new ReferenceFactory();
        for (String oid : oids) {
            // CSCDebug.outDebugInfo("temp oid is "+oid);
            if (!oid.equals("")) {
                WTObject wto = (WTObject) rf.getReference(oid).getObject();
                if (PackagedPartHelper.isEqualsState(wto, "APPROVED")) {
                    // CSCDebug.outDebugInfo("temp oid stat is "+WorkflowConstants.ASES_STATE_INWORK);
                    // if(PackagedPartHelper.validataFirstVersion(wto)){
                    // CSCDebug.outDebugInfo("temp oid version is first version");
                    oidList.add(oid);
                    // }
                }
            }

        }
        return oidList;
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
    
    private void saveRelatedObjLink(List<String> oids,ProcessEnvelope processEnvelope,ObjectBean objectbean) throws WTException{

    	ReferenceFactory rf = new ReferenceFactory();
		for(int i = 0 ; i < oids.size() ; i++){
			String oid = oids.get(i);
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


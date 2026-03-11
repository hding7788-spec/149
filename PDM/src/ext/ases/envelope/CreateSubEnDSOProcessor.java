package ext.ases.envelope;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.CreateObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.meta.common.TypeIdentifierHelper;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.util.IBAHelper;
import ext.sast.center.synch.MQConstants;
import ext.sast.center.synch.MQKRSynchHelper;
import org.apache.log4j.Logger;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.fc.*;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTRuntimeException;

import java.util.*;

//import ext.casc.part.PackagedPartHelper;
//import ext.casc.util.IBAHelper;

/**
 * 
 * Description：  创建数据发送单<br>
 * @Author： zxu <br>
 * Create Date： 2021年5月10日 <br>
 * @Version： 0.1
 */
public class CreateSubEnDSOProcessor extends CreateObjectFormProcessor{
    private static final Logger log;

    static {
       try {
          log = LogR.getLogger(CreateSubEnDSOProcessor.class.getName());
       }
       catch (Exception e) {
          throw new ExceptionInInitializerError(e);
       }
    }
    
    public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {
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
            if (objectbean.getObject() != null && (objectbean.getObject() instanceof Persistable)) {
            	ProcessEnvelope processEnvelope = (ProcessEnvelope)objectbean.getObject();
                PersistenceHelper.manager.refresh(processEnvelope);
                String PEType = TypeIdentifierHelper.getType(processEnvelope).toString();
				
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
                
                // 过滤相关对象
                oidList = filterWTObject(relatedObjOids);
                log.debug("oidList is "+oidList);
				
                if(relatedObjOids!=null && relatedObjOids.length>0)
				{
					saveRelatedObjLink(oidList, processEnvelope,objectbean);		
				}
                Map<String,String> params = new HashMap<String, String>();
                params.put(MQConstants.FAWANGDANWEI, IBAHelper.getIBAStringValue(processEnvelope,MQConstants.FAWANGDANWEI));
				// 发送数据到科瑞
				String sendError = MQKRSynchHelper.sendToKr(processEnvelope,params);
				if(!"".equals(sendError)){
					throw new WTException(sendError);
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
//        ReferenceFactory rf = new ReferenceFactory();
        for (String oid : oids) {
            // CSCDebug.outDebugInfo("temp oid is "+oid);
            if (!oid.equals("")) {
                //WTObject wto = (WTObject) rf.getReference(oid).getObject();
                //if (PackagedPartHelper.isEqualsState(wto, "APPROVED")) {
                    // CSCDebug.outDebugInfo("temp oid stat is "+WorkflowConstants.ASES_STATE_INWORK);
                    // if(PackagedPartHelper.validataFirstVersion(wto)){
                    // CSCDebug.outDebugInfo("temp oid version is first version");
                    oidList.add(oid);
                    // }
                //}
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


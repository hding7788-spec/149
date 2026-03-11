/**
 * @(#)EditEnvelopeFormProcessor.java
 *
 *
 * @author Leon Zhang
 * @version 1.00 2010/1/19
 */
package ext.ases.envelope;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultEditFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import org.apache.log4j.Logger;
import wt.enterprise.RevisionControlled;
import wt.fc.*;
import wt.log4j.LogR;
import wt.util.WTException;

import java.util.Iterator;
import java.util.List;

public class EditEnvelopeFormProcessor extends DefaultEditFormProcessor {
    
    private static final String RESOURCE = "ext.ases.envelop.envelopResource";
    private static final Logger log;

    static {
       try {
          log = LogR.getLogger(EditEnvelopeFormProcessor.class.getName());
       }
       catch (Exception e) {
          throw new ExceptionInInitializerError(e);
       }
    }
    
    @Override
    public FormResult preProcess(NmCommandBean nmcommandbean, List list)
        throws WTException
    {
        log.debug("ENTER => DefaultObjectFormProcessor.preProcess");
        return super.preProcess(nmcommandbean, list);
    }
    
     @Override
    public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {

        //String relatedObjTableID=nmcommandbean.getTextParameter("relatedObjTable_id");
        FormResult phaseResult = new FormResult();
        phaseResult.setStatus(FormProcessingStatus.SUCCESS);
        phaseResult = super.doOperation(clientData, objectBeans);
        log.debug("phaseResult:" + phaseResult);
        java.util.HashMap hashmap = clientData.getChangedText();
        log.debug("hashmap.keySet():" + hashmap.keySet());
        java.util.HashMap hashmap1 = clientData.getChangedTextArea();
        log.debug("hashmap1.keySet():" + hashmap1.keySet());
        java.util.HashMap hashmap2 = clientData.getChecked();
        log.debug("hashmap2.keySet():" + hashmap2.keySet());
        Iterator iterator1= objectBeans.iterator();
        do
        {
            if(!iterator1.hasNext())
            {
                break;
            }
            //如果是从对象下拉菜单里，获得的oid是当前对象的oid
			String[] oids = clientData.getTextParameterValues("oid");
			String objoid = new String();
	    	for(String oid:oids){
	    		objoid = oid;
	    		log.debug("oid下拉 is:" + oid);
	    	}
			ReferenceFactory rf = new ReferenceFactory();
			ProcessEnvelope processEnvelope = (ProcessEnvelope)rf.getReference(objoid).getObject();
			/*
			boolean flag = false;
			try{
				if(hashmap.keySet().contains("name")){
				String s = (String)hashmap.get("name");
				log.debug("=>Edit hashmap.get(name) : "+hashmap.get("name"));
				processEnvelope.setName(s);
				flag = true;
				}
				if(hashmap.keySet().contains("description")){
					processEnvelope.setDescription((String)hashmap.get("description"));
					flag = true;
				}
				if(flag){
					PersistenceHelper.manager.save(processEnvelope);
				}
				PersistenceHelper.manager.refresh(processEnvelope);
			}catch(WTPropertyVetoException pve){
			}*/
			
            ObjectBean objectbean = (ObjectBean)iterator1.next();
            log.debug("processEnvelope is: "+ processEnvelope.getIdentity());
            String[] relatedObjOids =objectbean.getTextParameterValues("change_affectedData_table__objRef");	
            log.debug("relatedObjectOids is: " + relatedObjOids);                        

			if(relatedObjOids!=null)
			{
	            deleteRelatedObjLink(processEnvelope);
	            if(relatedObjOids.length>0)
	            	saveRelatedObjLink(relatedObjOids, processEnvelope,objectbean);		
			}
            //}
        } while(true);
        
        return phaseResult;

    }
    /**
     * @param oids,processEnvelope,objectbean
     * @return void
     * @exception WTException
     **/
    
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
}
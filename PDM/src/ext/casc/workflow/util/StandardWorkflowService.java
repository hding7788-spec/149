package ext.casc.workflow.util;

import java.io.Serializable;
import java.util.List;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.iba.value.IBAHolder;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import ext.ases.envelope.EnvelopeHelper;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.util.IBAUtility;

public class StandardWorkflowService extends StandardManager implements WorkflowService, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2705230504006983961L;

	public static StandardWorkflowService newStandardWorkflowService()throws WTException {
		StandardWorkflowService instance = new StandardWorkflowService();
		instance.initialize();
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public void setPartContainer(Object pbo,String ibaName,String ibaValue) throws WTException{
		try {
			if(pbo instanceof ProcessEnvelope){
				ProcessEnvelope pe = (ProcessEnvelope)pbo;
				List<Object> wtObjList = null;
				wtObjList = EnvelopeHelper.service.getAllMembers(pe);
				for(int i = 0 ; i < wtObjList.size() ; i++){
					WTObject wto = (WTObject)wtObjList.get(i);
					IBAHolder ibah = (IBAHolder)PersistenceHelper.manager.refresh(wto);
					IBAUtility ibau = new IBAUtility(ibah);
					ibau.setIBAValue(ibaName, ibaValue);
					ibau.updateAttributeContainer(ibah);
					IBAUtility.updateIBAHolder(ibah);
				}
			}else if(pbo instanceof WTDocument){
				WTDocument wtd = (WTDocument)pbo;
				IBAHolder ibah = (IBAHolder)PersistenceHelper.manager.refresh(wtd);
				IBAUtility ibau = new IBAUtility(ibah);
				ibau.setIBAValue(ibaName, ibaValue);
				ibau.updateAttributeContainer(ibah);
				IBAUtility.updateIBAHolder(ibah);
			}
		} catch (WTPropertyVetoException e) {
			throw new WTException(e);
		} catch(Exception e){
			throw new WTException(e);
		}
	}
}

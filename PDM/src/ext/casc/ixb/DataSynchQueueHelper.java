package ext.casc.ixb;

import wt.org.WTPrincipal;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.queue.WtQueue;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTRuntimeException;

public class DataSynchQueueHelper {

	public static void createProcessingQueue(String path,String wfProcessOid,String activityTemplateID,String activityName,
			String activityOid,String reviewType,String workflowType,String sendFrom,String orderIID,String previewUser,String dataImportStateID) throws WTException{
		ProcessingQueue queue = getFreeQueue("DataSynchQueue");
		//String queueName = queue.getName();
		String targetClass = DataSynchQueueHelper.class.getName();
		String targetMethod = "dataImport";
		Class[] argClass = new Class[]{String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class};
		Object[] argObj = new Object[]{path, wfProcessOid, activityTemplateID,activityName,activityOid, reviewType,workflowType,sendFrom,orderIID,previewUser,dataImportStateID};
		WTPrincipal user = SessionHelper.manager.getPrincipal();
		queue.addEntry(user, targetMethod, targetClass, argClass, argObj);
	}

	private static ProcessingQueue getFreeQueue(String queueName) throws WTException {
		WtQueue queue = QueueHelper.manager.getQueue(queueName);
		if(queue==null){
			Manager manager = ManagerServiceFactory.getDefault().getManager(wt.queue.StandardQueueService.class);
			queue = (WtQueue)((wt.queue.StandardQueueService)manager).createQueue(queueName,true);
		}
		return (ProcessingQueue)queue;
	}
	public static void dataImport(String path,String wfProcessOid,String activityTemplateID,String activityName,
			String activityOid,String reviewType,String workflowType,String sendFrom,String orderIID,String previewUser,String dataImportStateID) throws WTRuntimeException, WTException {
        DataImportHandler.processReceivedData(path, wfProcessOid, activityTemplateID,activityName,activityOid, reviewType,workflowType,sendFrom,orderIID,previewUser,dataImportStateID);
	}



}

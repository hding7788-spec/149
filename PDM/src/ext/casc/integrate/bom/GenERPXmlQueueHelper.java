package ext.casc.integrate.bom;

import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.queue.WtQueue;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

public class GenERPXmlQueueHelper {

	public static boolean createProcessingQueue(String number, String version, Integer expansionLevel, String relatedType, String bomType, String fileTypeB, String fileTypeC, String partNumber,Boolean is65) throws WTException{
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			ProcessingQueue queue = getFreeQueue("GenERPXmlQueue");
			//String queueName = queue.getName();
			String targetClass = ProductBomService.class.getName();
			String targetMethod = "genERPXml";
			Class[] argClass = new Class[]{String.class,String.class,Integer.class,String.class,String.class,String.class,String.class,String.class,Boolean.class};
			Object[] argObj = new Object[]{ number,  version,  expansionLevel,  relatedType,  bomType,  fileTypeB,  fileTypeC,  partNumber,is65};
			WTPrincipal user = SessionHelper.manager.getPrincipal();
			queue.addEntry(user, targetMethod, targetClass, argClass, argObj);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		return true;
	}

	private static ProcessingQueue getFreeQueue(String queueName) throws WTException {
		WtQueue queue = QueueHelper.manager.getQueue(queueName);
		if(queue==null){
			Manager manager = ManagerServiceFactory.getDefault().getManager(wt.queue.StandardQueueService.class);
			queue = ((wt.queue.StandardQueueService)manager).createQueue(queueName,true);
		}
		return (ProcessingQueue)queue;
	}
}

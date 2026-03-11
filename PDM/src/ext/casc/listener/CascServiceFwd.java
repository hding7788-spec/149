package ext.casc.listener;

import java.io.Serializable;

import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;

public class CascServiceFwd implements wt.method.RemoteAccess, CascService, Serializable {
	// --- Attribute Section ---
	   private static final String FC_RESOURCE = "wt.fc.fcResource";
	   private static final String CLASSNAME = CascServiceFwd.class.getName();

	   /**
	    * @return    Manager
	    * @exception wt.util.WTException
	    **/
	   private static Manager getManager()throws WTException {

	      Manager manager = ManagerServiceFactory.getDefault().getManager(ext.casc.listener.CascService.class);
	      
	      if ( manager == null ) {
	         Object[] param = { "ext.ases.listener.AsesService" };
	         throw new WTException( FC_RESOURCE, wt.fc.fcResource.UNREGISTERED_SERVICE, param );
	      }
	      return manager;
	   }

}

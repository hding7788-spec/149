package ext.casc.service;

import java.rmi.RemoteException;

import wt.services.ManagerException;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardCascCacheService extends StandardManager implements CascCacheService {
    private static final long serialVersionUID = 1L;

    public static StandardCascCacheService newStandardCascCacheService() throws WTException {
        StandardCascCacheService service = new StandardCascCacheService();
        service.initialize();
        return service;
    }

    @Override
    protected synchronized void performStartupProcess() throws ManagerException {
        System.out.println("******************CascCacheService starting..........");
        SessionContext sessioncontext = SessionContext.newContext();
        try {
            SessionHelper.manager.setAdministrator();
            CascCacheManager instance = new CascCacheManager();
            instance.initializeGroupAndUsers();
            instance.initializeCheJianAndUsers();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            SessionContext.setContext(sessioncontext);
        }
    }
}

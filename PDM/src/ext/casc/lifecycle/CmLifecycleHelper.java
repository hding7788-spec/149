package ext.casc.lifecycle;

import wt.change2.ChangeHelper2;
import wt.change2.WTChangeRequest2;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.util.WTException;
import ext.casc.constants.Constants;

public class CmLifecycleHelper {
    
    public static void setLifeCycleStateToInWork(WTObject pbo) throws WTException {
        if (pbo instanceof WTChangeRequest2) {
            WTChangeRequest2 changeRequest2 = (WTChangeRequest2)pbo;
            QueryResult qr = ChangeHelper2.service.getChangeables(changeRequest2);
            while (qr.hasMoreElements()) {
                Object object = qr.nextElement();
                setLifecycleState((LifeCycleManaged)object, Constants.STATE_INWORK);
            }
        }
    }
    
    public static void setLifeCycleStateToApproved(WTObject pbo) throws WTException {
        if (pbo instanceof WTChangeRequest2) {
            WTChangeRequest2 changeRequest2 = (WTChangeRequest2)pbo;
            QueryResult qr = ChangeHelper2.service.getChangeables(changeRequest2);
            while (qr.hasMoreElements()) {
                Object object = qr.nextElement();
                setLifecycleState((LifeCycleManaged)object, Constants.STATE_APPROVED);
            }
        }
    }
    
    public static LifeCycleManaged setLifecycleState(LifeCycleManaged lifecyclemgd, String pState) throws WTException {
        LifeCycleManaged lifeCycleManaged = null;
        State state = State.toState(pState);
        lifeCycleManaged = LifeCycleHelper.service.setLifeCycleState(lifecyclemgd, state);
        //设置关连的部件到相应的状态
        /*if (lifeCycleManaged instanceof EPMDocument) {
            EPMDocument epmDocument = (EPMDocument)lifeCycleManaged;
            QueryResult qResult = WcUtil.getEPMBuildRoles(epmDocument);
            if (qResult.hasMoreElements()) {
                EPMBuildRule rule = (EPMBuildRule)qResult.nextElement();
                Object object = rule.getRoleBObject();
                if (object instanceof WTPart) {
                    WTPart part = (WTPart)object;
                    LifeCycleHelper.service.setLifeCycleState(part, state);
                }
            }
        }*/
        return lifeCycleManaged;
    }
}

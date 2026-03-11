package ext.casc.workflow.signtrue.zp;

import wt.fc.ReferenceFactory;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WorkItem;

public class HuiQianWorkFlowService {
    public static String getActivityNameByWorkItemOid(String oid){
        ReferenceFactory rf = new ReferenceFactory();
        String name ="";
        try {
            WorkItem wi= (WorkItem) rf.getReference(oid).getObject();
            WfActivity wfAct = (WfActivity) wi.getSource().getObject();
            name = wfAct.getName();
        } catch (WTRuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return name;
    }

    public static String getRoleByWorkItemOid(String oid){
        ReferenceFactory rf = new ReferenceFactory();
        String name ="";
        try {
            WorkItem wi= (WorkItem) rf.getReference(oid).getObject();
            name =  wi.getRole().toString();
        } catch (WTRuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return name;
    }
}

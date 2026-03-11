package ext.casc.workflow;

//import wt.change2.WTChangeRequest2;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfBlock;
import wt.workflow.engine.WfContainer;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

public class TaskConfigrationHelper {
    public static void setActivityVariableByVarName(String oid, String variable, Object value)
            throws WTRuntimeException, WTException {
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        setActivityVariableValue(activity, variable, value);
    }

    public static void setActivityVariableValue(WfActivity activity, String variable, Object value) throws WTException {
    	activity =  (WfActivity)PersistenceHelper.manager.refresh(activity);
    	ProcessData pd = activity.getContext();
        pd.setValue(variable, value);
        PersistenceHelper.manager.save(activity);
    }

    public static void setProcessVariableByVarName(String oid, String variable, Object value)
            throws WTRuntimeException, WTException {
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        WfContainer wfcont = (WfContainer) activity.getParentProcessRef().getObject();
        if (wfcont instanceof WfBlock) {
            WfBlock wb = (WfBlock) wfcont;
            wfcont = wb.getParentProcess();
        }
        WfProcess process = (WfProcess) wfcont;
        setProcessVariableValue(process, variable, value);
    }

    public static void setProcessVariableValue(WfProcess process, String variable, Object value) throws WTException {
        ProcessData pd = process.getContext();
        pd.setValue(variable, value);
        PersistenceHelper.manager.save(process);
    }

    public static Object getActivityVariableByVarName(String oid, String variable) throws WTRuntimeException,
            WTException {
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        Object obj = TaskConfigrationHelper.getActivityVariableValue(activity, variable);
        return obj;
    }

    public static Object getProcessVariableValue(String oid, String variable) throws WTRuntimeException, WTException {
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        WfContainer wfcont = (WfContainer) activity.getParentProcessRef().getObject();
        if (wfcont instanceof WfBlock) {
            WfBlock wb = (WfBlock) wfcont;
            wfcont = wb.getParentProcess();
        }
        WfProcess process = (WfProcess) wfcont;
        Object obj = TaskConfigrationHelper.getProcessVariableValue(process, variable);
        return obj;
    }

    public static Object getActivityVariableValue(WfActivity activity, String variable) {
        try {
            if ("isShowSignResult".equals(variable)) {
                WfContainer wfcont = (WfContainer) activity.getParentProcessRef().getObject();
                if (wfcont instanceof WfBlock) {
                    WfBlock wb = (WfBlock) wfcont;
                    wfcont = wb.getParentProcess();
                }
                WfProcess process = (WfProcess) wfcont;
                String wfName = process.getName();
                if (wfName.contains("工艺更改流程") || wfName.contains("三维工艺签审流程") || wfName.contains("质量报告签审流程")) {
                    return "内部会签,外部会签";
                } else {
                    return "内部会签,外部会签,工艺会签";
                }
            }

            ProcessData pd = activity.getContext();
            return pd.getValue(variable);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getProcessVariableValue(WfProcess process, String variable) {
        try {
            return process.getContext().getValue(variable);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getPBO(String oid) throws WTRuntimeException, WTException {
        ReferenceFactory rf = new ReferenceFactory();
        // WTChangeRequest2 wc = null;
        WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
        WfActivity wfAct = (WfActivity) wi.getSource().getObject();
        Persistable pbo = (Persistable) wfAct.getContext().getValue("primaryBusinessObject");
        return pbo;
    }

    public static WTObject getPBOByWfProcess(WfProcess process){
		WTObject ret = null;
		ReferenceFactory rf = new ReferenceFactory();
		WTReference ref = process.getBusinessObjectReference(rf);
		if(ref == null && process.isNested()){
			ret = (WTObject) process.getContext().getValue("primaryBusinessObject");
		}
		if(ref != null && ref.getKey() != null){
			ret = (WTObject) ref.getObject();
		}
		return ret;
	}

    public static boolean isGongYiECN(String oid) throws WTRuntimeException, WTException {
        ReferenceFactory rf = new ReferenceFactory();
        WorkItem wi = (WorkItem) rf.getReference(oid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        WfContainer wfcont = (WfContainer) activity.getParentProcessRef().getObject();
        if (wfcont instanceof WfBlock) {
            WfBlock wb = (WfBlock) wfcont;
            wfcont = wb.getParentProcess();
        }
        WfProcess process = (WfProcess) wfcont;
        String wfName = process.getName();
        if (wfName.indexOf("工艺更改流程") > -1) {
            return true;
        }
        return false;
    }

    public static void main(String args[]) throws WTRuntimeException, WTException {

    }

}

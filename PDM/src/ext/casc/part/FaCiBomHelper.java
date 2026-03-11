package ext.casc.part;

import ext.ases.envelope.ProcessEnvelope;
import wt.fc.ReferenceFactory;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.util.WTException;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

public class FaCiBomHelper implements RemoteAccess {

    public static final String FACI_BOM_TYPE = "casc.sast.149.FcProduct";
    public static final String FACI_BOM_WORKFLOW = "149签审包工艺会签流程";

    public static void main(String[] args) {
        if (!RemoteMethodServer.ServerFlag) {
            try {
                RemoteMethodServer server = RemoteMethodServer.getDefault();
                server.setUserName("wcadmin");
                server.setPassword("Admin@149");
                String method = "";
                Class<?>[] types = null;
                Object[] vals = null;
                method = "startProcessTest";
                types = new Class<?>[]{String.class};
                vals = new Object[]{args[0]};
                if (types != null && vals != null) {
                    server.invoke(method, FaCiBomHelper.class.getName(), null, types, vals);
                } else {
                    startProcessTest(args[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void startProcessTest(String oid)
    {
        try {
            ReferenceFactory rf = new ReferenceFactory();
            ProcessEnvelope pe = (ProcessEnvelope) rf.getReference(oid).getObject();

            WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
                    .getProcessDefinition(FACI_BOM_WORKFLOW);
            WfProcess wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, null,
                    pe.getContainerReference());
            wfprocess.setName(FACI_BOM_WORKFLOW + "_" + pe.getNumber());
            ProcessData processdata = wfprocess.getContext();
            processdata.setValue("primaryBusinessObject", pe);
            processdata.setValue("sendFrom", "test00");
            processdata.setValue("mqmessage", "msg test");
            WfEngineHelper.service.startProcess(wfprocess, processdata, 1);
        } catch (WTException e) {
            throw new RuntimeException(e);
        }
    }



}

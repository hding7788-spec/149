package ext.casc.synch;

import ext.ases.envelope.ProcessEnvelope;
import org.apache.soap.SOAPException;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

public class DataSynchClient {

    public static void main(String[] args) throws WTException {
        // RemoteMethodServer rms=.getDefault();
        // rms.setUserName("wcadmin");
        // rms.setPassword("wcadmin");

        ReferenceFactory referencefactory = new ReferenceFactory();
        ProcessEnvelope pe = (ProcessEnvelope) referencefactory.getReference(
                "OR:ext.ases.envelope.ProcessEnvelope:347336").getObject();
        DataSynchHelper.exportProcessEnvelopeTargets(pe);

    }

    public static String DeliveryDataToSelectedUnit(ObjectReference self,
            String fileName, String approvedType,String route) throws SOAPException,
            MalformedURLException, WTException {
        String activityOid149 = self.getObjectId().toString();
        WfActivity activity = (WfActivity) self.getObject();
        String containerName = activity.getContainerName();
        HashMap inputparams = new HashMap();
        try {
            WfProcess process = activity.getParentProcess();
            ProcessData pd = process.getContext();
            WTDocument doc =(WTDocument) pd.getValue("primaryBusinessObject");
            //TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
			//if (TypeHelper.isA(ti,TypeHelper.getTypeIdentifier("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_NOTICE"))) {
				approvedType ="PROCESSNOTICE";
			//}
            pd.setValue("activityOidTemp", activityOid149);
            Object activityOid =  pd.getValue("activityOid");
            //外部会签单位等待活动oid
            String sendTo = (String)pd.getValue("selectUnitValue");

            if(sendTo==null||"".equals(sendTo)){
            	sendTo="805";
            }
            if("805".equals(sendTo)&&ext.casc.constants.Constants.processNoticeConfig.contains(containerName)){
            	sendTo="805.11";
            }
            String selectUsersValue = (String)pd.getValue("selectUsersValue");
            PersistenceHelper.manager.save(process);
            //使用反馈方法中的相关配置信息，故将sendTo 的参数变为sendFrom
            inputparams.put("sendFrom", sendTo);
            inputparams.put("sendUnit", "149");
            inputparams.put("usersValue", selectUsersValue);
            if(activityOid!=null &&!"".equals(activityOid)){
            	if("create".equals(route)){
            		inputparams.put("route", "back");
            	}else if("pass".equals(route)){
            		inputparams.put("route", "pass");
            	}

            }else{
            	inputparams.put("route", "");
            }

        } catch (WTException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        if (fileName != null) {
            SoapCall sc = new SoapCall();
            try {
                WTProperties prop = WTProperties.getLocalProperties();
                String hostName = prop.getProperty("java.rmi.server.hostname");
                String url = "http://" + hostName + "/" + fileName;
                inputparams.put("URL", url);
                inputparams.put("activityOidSF", activityOid149);
                inputparams.put("approvedType", approvedType);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            sc.callToServer("Feedback", inputparams);
        }

        return "";
    }
    public static String DeliveryDataTo805(ObjectReference self,
            String fileName, String approvedType) throws SOAPException,
            MalformedURLException, WTException {
        String activityOid805 = self.getObjectId().toString();
        WfActivity activity = (WfActivity) self.getObject();
        String containerName = activity.getContainerName();
        HashMap inputparams = new HashMap();
        try {
            WfProcess process = activity.getParentProcess();
            ProcessData pd = process.getContext();
            String activityOid149 = (String) pd.getValue("activityOid149");
            inputparams.put("activityOid149", activityOid149);
        } catch (WTException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        if (fileName != null) {
            SoapCall sc = new SoapCall();
            try {
                WTProperties prop = WTProperties.getLocalProperties();
                String hostName = prop.getProperty("java.rmi.server.hostname");
                String url = "http://" + hostName + "/" + fileName;
                inputparams.put("URL", url);
                inputparams.put("activityOid805", activityOid805);
                inputparams.put("approvedType", approvedType);

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            sc.callToServer("invokeFrom805", inputparams);
        }

        return "";
    }

    public static String queryOtherServerUsers(String reviewUnit,String userName) throws MalformedURLException, SOAPException{
		SoapCall sc = new SoapCall();
		HashMap inputparams = new HashMap();
		inputparams.put("sendTo", reviewUnit);
		inputparams.put("userName", userName);
		String result = sc.queryOtherServerUsers("queryReviewUsers",inputparams);
		return result;
	}
}

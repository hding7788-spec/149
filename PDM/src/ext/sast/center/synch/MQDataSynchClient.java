package ext.sast.center.synch;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.apache.soap.SOAPException;
import org.json.JSONObject;

import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.util.WTException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;

import com.bjsasc.avidm.mq.fileserver.FSUtil;
import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;

import ext.sast.center.util.PropertiesUtil;

public class MQDataSynchClient {


	/**
	 * 发送工艺技术通知单到805
	 * @param self
	 * @param fileName
	 * @param approvedType
	 * @param route
	 * @return
	 * @throws SOAPException
	 * @throws MalformedURLException
	 * @throws WTException
	 */
	@Deprecated
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
            TypeIdentifier ti = TypeIdentifierUtility.getTypeIdentifier(doc);
			if (TypeHelper.isA(ti,TypeHelper.getTypeIdentifier("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_NOTICE"))) {
				approvedType ="PROCESSNOTICE";
			}
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
            e2.printStackTrace();
        }
        if (fileName != null) {
        	//通过中心域上传文件
        	String localPath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp"+ File.separator + fileName;
			String upload_url = MQConstants.DC_UPLOAD;
			JSONObject j_file = FSUtil.upload(localPath, upload_url);
			System.out.println("j_file == "+j_file);

        	//通过中心域MQ调用接口


            /*SoapCall sc = new SoapCall();
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
            sc.callToServer("Feedback", inputparams);*/
        }

        return "";
    }

}

package ext.casc.product;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.integrate.util.BomUtil;
import ext.casc.report.technics.DownloadTechnicsReportUtil;
import ext.casc.util.IBAHelper;
import ext.casc.webservice.TestConfig;
import ext.casc.webservice.WSConstants;

import ext.casc.webservice.command.ExchangeProcessDocCommandZS;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wt.content.*;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.session.SessionServerHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTProperties;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.beans.PropertyVetoException;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExchangeProcessDocProcessorCS extends DefaultObjectFormProcessor {
    private static String wt_temp;
    public  static String PART_END = " ";
    static {
        try {
            WTProperties pro = WTProperties.getLocalProperties();
            wt_temp = pro.getProperty("wt.temp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
        FormResult formresult = super.doOperation(commandBean, objectBeans);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
		long startTime;
		try {
			startTime = format1.parse(TestConfig.START_DATE).getTime();
			if(	System.currentTimeMillis()<startTime){
				FeedbackMessage message = new FeedbackMessage();
                message.addMessage("工艺迁移失败，请稍后重试！");
	            formresult.addFeedbackMessage(message);
	            formresult.setStatus(FormProcessingStatus.FAILURE);
	            formresult.setNextAction(FormResultAction.NONE);
	            return formresult;
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        Object actionObj = commandBean.getActionOid().getRefObject();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        StringBuilder msg = new StringBuilder("");
        try {
            if (actionObj instanceof WTPart) {
                WTPart part = (WTPart) actionObj;
                List<WTPart> allPart = new ArrayList<WTPart>();
                allPart.add(part);
                DownloadTechnicsReportUtil.getAllChildPart(part,allPart);

                for(WTPart p:allPart){
                   String result =  exchange(p);
                   if(!"".equals(result)){
                       msg.append(result).append(",");
                   }
                }
            }else if (actionObj instanceof WTDocument) {
                WTDocument doc = (WTDocument) actionObj;
                String result =  exchange(doc);
                if(!"".equals(result)){
                    msg.append(result).append(",");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FeedbackMessage message = new FeedbackMessage();
            if(!"".equals(msg.toString())){
	            formresult.setStatus(FormProcessingStatus.SUCCESS);
                message.addMessage("部分工艺迁移成功，以下零件下工艺迁移存在问题："+msg);
            }else{
            	formresult.setStatus(FormProcessingStatus.SUCCESS);
                message.addMessage("操作成功");
            }
            formresult.addFeedbackMessage(message);
            formresult.setNextAction(FormResultAction.NONE);
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formresult;
    }

    private String exchange(WTPart part) throws JSONException  {
        String params = genParameters(part);
        if("".equals(params)){
            return "";
        }
        System.out.println("params:"+params);
        String result = call(ExchangeProcessDocCommandZS.METHOD_NAME,params);
        if(!"".equals(result)){
        	 JSONObject object = new JSONObject(result);
             String msg = object.optString(WSConstants.RTN_MSG);
             if(!"工艺迁移成功".equals(msg)){
                 return part.getNumber();
             }
        }

        return "";
    }

    private String exchange(WTDocument doc) throws JSONException, WTException {
        QueryResult partqr = WTPartHelper.service.getDescribesWTParts(doc);
        if (partqr.hasMoreElements()) {
            WTPart wtPart = (WTPart) partqr.nextElement();
            String params = genParameters(wtPart,doc);
            if("".equals(params)){
                return "";
            }
            System.out.println("params:"+params);
            String result = call(ExchangeProcessDocCommandZS.METHOD_NAME,params);
            if(!"".equals(result)){
                JSONObject object = new JSONObject(result);
                String msg = object.optString(WSConstants.RTN_MSG);
                if(!"工艺迁移成功".equals(msg)){
                    return doc.getNumber();
                }
            }

            return "";
        }
        return "";

    }
    private String  genParameters(WTPart part) {
        JSONObject params = new JSONObject();
        try {
            List<WTDocument> docs = BomUtil.getAllWTDocumentByAllSameVersionViewPart(part);
          // if(!PDMConfig.isZS){
            params.put("partNumber", part.getNumber());
            //}
        	/*else{
                params.put("partNumber", part.getNumber());
            }*/
            JSONArray jsonArray = new JSONArray();
            params.put("processPlans", jsonArray);

            if(docs.size()==0){
                return "";
            }
            for (WTDocument doc : docs) {
                JSONObject jsonObject = genDocJson(doc);
                jsonArray.put(jsonObject);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return params.toString();
    }

    private String  genParameters(WTPart part,WTDocument doc) {
        JSONObject params = new JSONObject();
        try {

            params.put("partNumber", part.getNumber());

            JSONArray jsonArray = new JSONArray();
            params.put("processPlans", jsonArray);

            if(doc==null){
                return "";
            }

            JSONObject jsonObject = genDocJson(doc);
            jsonArray.put(jsonObject);

        }catch(Exception e){
            e.printStackTrace();
        }
        return params.toString();
    }

    private JSONObject genDocJson(WTDocument doc) throws WTException, IOException, PropertyVetoException, JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("docNumber",doc.getNumber());
        jsonObject.put("docName",doc.getName());

        String technicsType = TypedUtilityServiceHelper.service.getLocalizedTypeName(doc, Locale.CHINA);
        System.out.println("docType:"+technicsType);
        jsonObject.put("technicsType",technicsType);
        jsonObject.put("userName",doc.getModifierName());
        jsonObject.put("createTime",doc.getCreateTimestamp().getTime());
        jsonObject.put("modifyTime",doc.getModifyTimestamp().getTime());


        String fileDir = wt_temp + File.separator + "IXBExpImp" + File.separator ;

        downloadDocumentPrimaryToPath(doc,fileDir);

        downloadDocumentSecondToPath(doc,fileDir,jsonObject);

        for(String iba: ExchangeProcessDocCommandZS.ibaAttrs){
            jsonObject.put(iba, IBAHelper.getIBAStringValue(doc,iba));
        }

        return jsonObject;
    }
    private void downloadDocumentSecondToPath(WTDocument doc, String fileDir, JSONObject jsonObject) throws WTException, IOException {
    	 QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.SECONDARY);
         while (qr.hasMoreElements()) {
             ApplicationData applicationdata = (ApplicationData) qr.nextElement();
             String name = applicationdata.getFileName();
             if(name.startsWith("Print_")){
            	 InputStream is = ContentServerHelper.service.findContentStream(applicationdata);
                 FileOutputStream fos = new FileOutputStream(new File(fileDir+name.replace(".pdf", "")));
                 int i = 0;
                 byte abyte[] = new byte[8192];
                 while ((i = is.read(abyte, 0, abyte.length)) >= 0) {
                     fos.write(abyte, 0, i);
                 }
                 is.close();
                 fos.close();
                 jsonObject.put("fileName", name);
             }

         }

	}

	public static void downloadDocumentPrimaryToPath(WTDocument doc,String destPath) throws WTException, PropertyVetoException,
            FileNotFoundException, IOException {
        FormatContentHolder holder = (FormatContentHolder) ContentHelper.service.getContents(doc);
        ApplicationData currdata = (ApplicationData) ContentHelper.service.getPrimary(holder);
        InputStream is = ContentServerHelper.service.findContentStream(currdata);
        String appFileName = currdata.getFileName();
        FileOutputStream fos = new FileOutputStream(new File(destPath+appFileName));
        int i = 0;
        byte abyte[] = new byte[8192];
        while ((i = is.read(abyte, 0, abyte.length)) >= 0) {
            fos.write(abyte, 0, i);
        }
        is.close();
        fos.close();
    }
    public  String call(String methodName,String parameters){

        String result = "";
        try {
            Service service = new Service();
            Call call = (Call) service.createCall();


	    	 call.setTargetEndpointAddress(TestConfig.WSDL_URL);
	         call.setUsername(TestConfig.WINDCHILL_USER);
	         call.setPassword(TestConfig.WINDCHILL_PWD);

            call.setOperationName(new QName(TestConfig.WSDL_URL, "callPDMService"));

            call.addParameter("methodName", XMLType.SOAP_STRING, ParameterMode.IN);
            call.addParameter("parameters", XMLType.SOAP_STRING, ParameterMode.IN);
            call.setReturnClass(String.class);
            result = (String) call.invoke(new Object[]{methodName,parameters});
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

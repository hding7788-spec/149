package ext.casc.doc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.json.JSONObject;

import wt.doc.WTDocument;
import wt.fc.ReferenceFactory;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;

import com.glaway.mpm.print.constants.PrintServerConstants;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.webservice.TestConfig;
import ext.casc.webservice.WSConstants;

public class SetSignatureProcessor{


    public static FormResult setSignature(NmCommandBean commandBean) throws WTException {
        FormResult formResult = new FormResult();
        String message = "";
        String oid = "";
        Object obj = commandBean.getActionOid().getRefObject();
        if(obj == null){
            message = "获取文档出错";
        }
        WTDocument document = null;
        if(obj instanceof WTDocument){
            document = (WTDocument) obj;
            oid = PrintServerConstants.OID_WTDOCUMENT + document.getPersistInfo().getObjectIdentifier().getId();
        }
        try {
            if("".equals(message)){
                String urlBase = WTProperties.getLocalProperties().getProperty("wt.rmi.server.hostname");
                String webAPP = WTProperties.getLocalProperties().getProperty("wt.webapp.name");
                String url = "http://"+urlBase+"/"+webAPP;
                url += "/netmarkets/jsp/ext/casc/document/setSignature.jsp?oid=" + oid;
                formResult.setNextAction(FormResultAction.JAVASCRIPT);
                formResult.setJavascript("window.open(\""+url+"\")");
            }else{
                formResult.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(), null, null, new String[]{message}));
                formResult.setNextAction(FormResultAction.NONE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return formResult;
    }

    public static List<String> addList(){
    	List<String> list = new ArrayList<String>();
    	list.add("编制者");
    	list.add("校对者");
    	list.add("审核者");
    	list.add("标审者");
    	list.add("批准者");
    	list.add("1");
    	list.add("2");
    	list.add("3");
    	list.add("4");
    	list.add("5");
		return list;
    }

    public static JSONObject getSignInfo(String oid){
    	JSONObject map = new JSONObject();
        ReferenceFactory rf = new ReferenceFactory();

    	try {
			Object pbo = rf.getReference(oid).getObject();
			if(pbo!=null&&pbo instanceof WTDocument){
				WTDocument doc = (WTDocument) pbo;
				JSONObject params = new JSONObject();
				params.put("docNumber", doc.getNumber());
				String result = call("getSignInfo",params.toString());
				if(!"".equals(result)){
					JSONObject retObject = new JSONObject(result);
					JSONObject rtnData  =retObject.optJSONObject(WSConstants.RTN_DATA);
					if(rtnData!=null){
						JSONObject signObject = rtnData.optJSONObject("signObject");
						if(signObject!=null){
							return signObject;
						}
					}
				}

			}
		} catch (WTRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
    }

    public static String call(String methodName,String parameters){

        String result = "";
        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(TestConfig.WSDL_URL);
            call.setOperationName(new QName(TestConfig.WSDL_URL, "callPDMService"));
            call.setUsername(TestConfig.WINDCHILL_USER);
            call.setPassword(TestConfig.WINDCHILL_PWD);
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

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
  }

}

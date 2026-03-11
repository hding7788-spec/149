package ext.casc.webservice.command.gy.test;

import java.io.File;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.junit.Test;

import ext.sast.center.util.JsonConvertUtil;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

public class TestGetGyTemplateListCommand {
    public static void main(String[] args) {
    	File jsonFile = new File("C:\\workspace\\149\\test2.json");
    	String msg = JsonConvertUtil.fileRead(jsonFile);
    	 saveParamsMapAndValues(msg);

    }
    private static   String WSDL_URL="http://pdm.149.sast.casc/Windchill/servlet/RPC?CLASS=com.infoengine.soap";

    private static  String WINDCHILL_USER="wcadmin";
    private static  String WINDCHILL_PWD="Admin@149";
    @Test
    public  void test0(){



        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(WSDL_URL);

            call.setOperationName(new QName(WSDL_URL, "callPDMService"));
            call.setUsername(WINDCHILL_USER);
            call.setPassword(WINDCHILL_PWD);
            call.addParameter("methodName", XMLType.SOAP_STRING, ParameterMode.IN);
            call.addParameter("parameters", XMLType.SOAP_STRING, ParameterMode.IN);
            call.setReturnClass(String.class);
            String methodName= "getBom";
            String parameters= " [{\"cadName\": \"JIGUI_0.ASM\",\"cadVersion\": \"space.1\",modeType:\"导管\",params:{}},{\"cadName\": \"JIGUI_0.ASM\",\"cadVersion\": \"space.1\",modeType:\"导管\",params:{}}]";
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
                System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public  void test(){


        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(WSDL_URL);

            call.setOperationName(new QName(WSDL_URL, "callPDMService"));
            call.setUsername(WINDCHILL_USER);
            call.setPassword(WINDCHILL_PWD);
            call.addParameter("methodName", XMLType.SOAP_STRING, ParameterMode.IN);
            call.addParameter("parameters", XMLType.SOAP_STRING, ParameterMode.IN);
            call.setReturnClass(String.class);
            String methodName= "getGyTemplateList";
            String parameters= " [{\"cadName\": \"JIGUI_0.ASM\",\"cadVersion\": \"space.1\",modeType:\"导管\",params:{clfl: \"5A90材料\",yxx: \"是\",rcl: \"是\",bmcl:\"是\"}},{\"cadName\": \"JIGUI_0.ASM\",\"cadVersion\": \"space.1\",modeType:\"导管\",params:{clfl: \"5A90材料\",yxx: \"是\",rcl: \"是\",bmcl:\"是\"}}]";
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
                System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public  void test2(){


        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(WSDL_URL);
            call.setOperationName(new QName(WSDL_URL, "callPDMService"));
            call.setUsername(WINDCHILL_USER);
            call.setPassword(WINDCHILL_PWD);
            call.addParameter("methodName", XMLType.SOAP_STRING, ParameterMode.IN);
            call.addParameter("parameters", XMLType.SOAP_STRING, ParameterMode.IN);
            call.setReturnClass(String.class);
            String methodName= "getParamsMapAndValues";
            String parameters= " [{\"cadName\": \"JIGUI_0.ASM\",\"cadVersion\": \"space.1\",templateId:\"OR:wt.doc.WTDocument:4897162\"}]";
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
                System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveParamsMapAndValues(String msg){


        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(WSDL_URL);
            call.setOperationName(new QName(WSDL_URL, "callPDMService"));
            call.setUsername(WINDCHILL_USER);
            call.setPassword(WINDCHILL_PWD);
            call.addParameter("methodName", XMLType.SOAP_STRING, ParameterMode.IN);
            call.addParameter("parameters", XMLType.SOAP_STRING, ParameterMode.IN);
            call.setReturnClass(String.class);
            String methodName= "saveParamsMapAndValues";
            String parameters=msg;
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
                System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public  void searchProcessPlans(){
        String parameters= " {\"partNumber\": \"20200608-1-1-1\",\"processPlanType\": \"\"}";
        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(WSDL_URL);
            call.setOperationName(new QName(WSDL_URL, "callPDMService"));
            call.setUsername(WINDCHILL_USER);
            call.setPassword(WINDCHILL_PWD);
            call.addParameter("methodName", XMLType.SOAP_STRING, ParameterMode.IN);
            call.addParameter("parameters", XMLType.SOAP_STRING, ParameterMode.IN);
            call.setReturnClass(String.class);
            String methodName= "searchProcessPlans";
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

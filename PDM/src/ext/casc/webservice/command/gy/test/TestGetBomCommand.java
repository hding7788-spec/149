package ext.casc.webservice.command.gy.test;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

public class TestGetBomCommand {
    public static void main(String[] args) {
    	//File jsonFile = new File("C:\\workspace\\149\\test2.json");
    	//String msg = JsonConvertUtil.fileRead(jsonFile);
    	 //saveParamsMapAndValues(msg);

    }
    private static   String WSDL_URL="http://192.168.1.5/Windchill/servlet/RPC?CLASS=com.infoengine.soap";

    private static  String WINDCHILL_USER="wcadmin";
    private static  String WINDCHILL_PWD="1";

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
            String parameters= " { \"partNumber\": \"20200608-1_1A\",\"batch\": \"生三批\"}";
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
                System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void test1(){

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
            String methodName= "getProcessPlansByPartBatch";
            String parameters= " { \"partNumber\": \"20200608-1-1\",\"batch\": \"\",\"pplanType\": \"临时工艺文件\"}";
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
                System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public  void getMaterialBom(){

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
            String methodName= "getMaterialBom";
            String parameters= "{\"partOid\":\"\",\"docOid\": \"\",\"docNumber\": \"3000000000160\",\"docVersion\": \"space.3\"}";
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
                System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public  void searchZFLink(){

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
            String methodName= "getZFLink";
            String parameters= "{\"number\":\"9000000000033\",\"version\":\"space\"}";
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
                System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public  void getPartsInfo(){

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
            String methodName= "getPartsInfo";
            String parameters= "[{\"number\":\"B0000026464\"}]";
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
                System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public  void getDownloadUrl(){

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
            String methodName= "getDownloadUrl";
            String parameters= "[{\"oid\":\"OR:wt.doc.WTDocument:5056939\",\"role\":\"PRIMARY\"},{\"oid\":\"OR:wt.epm.EPMDocument:132670\",\"role\":\"PRIMARY\"}]";
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
                System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public  void searchDesignDoc(){

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
            String methodName= "searchDesignDoc";
            String parameters= "{\"number\":\"T-0000-01.PRT\",\"name\":\"\"}";
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
                System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public  void searchCadDoc(){

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
            String methodName= "searchCadDoc";
            String parameters= "{\"number\":\"JIGUI*\",\"cadName\":\"jigui*\"}";
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
                System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public  void getProceduceName(){

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
            String methodName= "getProceduceName";
            String parameters= "{}";
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
                System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public  void getProcessPlanData(){

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
            String methodName= "getProcessPlanData";
            String parameters= "{\"oid\":\"OR:wt.doc.WTDocument:554441\"}";
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
                System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public  void feedBackSKResult(){

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
            String methodName= "feedBackSKResult";
            String parameters= "{\"docOid\":\"OR:wt.doc.WTDocument:5403986\"}";
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
                System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public  void getProcessPlanInfo(){

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
            String methodName= "getProcessPlanInfo";
            String parameters= "[{\"processFileNum\":\"9000000000033\",\"processFileVersion\":\"space\"},{\"processFileNum\":\"9000000000032\",\"processFileVersion\":\"space.1\"}]";
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
                System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public  void getProcessPlanInfoXml(){

        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(WSDL_URL);

            call.setOperationName(new QName(WSDL_URL, "getProcessPlanInfo"));
            call.setUsername(WINDCHILL_USER);
            call.setPassword(WINDCHILL_PWD);
            call.addParameter("number", XMLType.SOAP_STRING, ParameterMode.IN);
            call.setReturnClass(String.class);
            String parameters= "1387854791564";
            Object result = (Object) call.invoke(new Object[]{parameters});
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public  void feedBackBomStructure(){

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
            String methodName= "feedBackBomStructure";
            String parameters= "{\"instanceId\":\"3000000000060_1750232296721\",\"state\":\"导入失败\",\"msg\":\"xxxx错误\"}";
            Object result = (Object) call.invoke(new Object[]{methodName,parameters});
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

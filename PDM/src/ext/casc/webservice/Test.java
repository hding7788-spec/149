package ext.casc.webservice;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

public class Test {
    public static void main(String[] args) {
        test();
    }

    public static void test(){

        String WSDL_URL="http://pdm.149.sast.casc/Windchill/servlet/RPC?CLASS=com.infoengine.soap";

        String WINDCHILL_USER="wcadmin";
        String WINDCHILL_PWD="Admin@149";

        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(WSDL_URL);
            call.setOperationName(new QName(WSDL_URL, "getBomProductInfo"));
            call.setUsername(WINDCHILL_USER);
            call.setPassword(WINDCHILL_PWD);
            call.addParameter("number", XMLType.SOAP_STRING, ParameterMode.IN);//20200608-1   bomType=Manufacturing
            call.addParameter("bomType", XMLType.SOAP_STRING, ParameterMode.IN);
            call.setReturnClass(String.class);
            String number= "20200608-1";
            String bomType= "Manufacturing";
            Object result = (Object) call.invoke(new Object[]{number,bomType});
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

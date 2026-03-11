package ext.casc.webservice.command;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.json.JSONObject;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TestWebServiceClient {

	// 测试入口
	public static void main(String[] args) throws Exception {

		String endpoint = "http://pdm.149.sast.casc/Windchill/servlet/RPC?CLASS=com.infoengine.soap";
		String operationName = "callPDMService";
		Map<String, Object> params = new HashMap<>();

		JSONObject json = new JSONObject();
		json.put("version", "space");
		json.put("number", "1679546568422");

//		params.put("methodName", "getGongShi");
		params.put("methodName", "getGongShiState");
		params.put("parameters", json.toString());

		Object[][] paramDefs = new Object[][]{{"methodName", XMLType.XSD_STRING}, {"parameters", XMLType.XSD_STRING},};

		Object[] orderedParams = new Object[]{params.get("methodName"), params.get("parameters")};

		String result = (String) invokeWebService(endpoint, operationName, paramDefs, orderedParams, XMLType.XSD_STRING, "wcadmin", "wcadmin");

		System.out.println("输出信息：" + result);

	}


	public static Object invokeWebService(String endpoint, String operationName, Object[][] paramDefs, Object[] paramValues, QName returnType, String username, String password) throws ServiceException {
		Service service = new Service();
		try {
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new URL(endpoint));
			call.setOperationName(new QName(endpoint, operationName));
			call.setTimeout(60000);

			// 参数定义
			for (Object[] def : paramDefs) {
				String name = (String) def[0];
				QName type = (QName) def[1];
				call.addParameter(name, type, ParameterMode.IN);
			}

			call.setReturnType(returnType);
			if (username != null && password != null) {
				call.setUsername(username);
				call.setPassword(password);
			}

			return call.invoke(paramValues);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

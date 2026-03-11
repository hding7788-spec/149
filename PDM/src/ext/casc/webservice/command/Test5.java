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

public class Test5 {

	// 测试入口
	public static void main(String[] args) throws Exception {

		String endpoint = "http://pdm.149.sast.casc/Windchill/servlet/RPC?CLASS=com.infoengine.soap";
		String operationName = "callPDMService";
		Map<String, Object> params = new HashMap<>();

		JSONObject json = new JSONObject();
		json.put("version", "space");
		json.put("number", "1670305704320");

		params.put("methodName", "getGongShi");
		params.put("parameters", json.toString());

		Object[][] paramDefs = new Object[][]{{"methodName", XMLType.XSD_STRING}, {"parameters", XMLType.XSD_STRING},};

		Object[] orderedParams = new Object[]{params.get("methodName"), params.get("parameters")};

		String result = (String) invokeWebService(endpoint, operationName, paramDefs, orderedParams, XMLType.XSD_STRING, "wcadmin", "wcadmin");

		System.out.println("输出信息：" + result);


//
//		Map<String, Object> params = new HashMap<>();
//		params.put("number", "20200608-1");
//		params.put("versionType", "BATCH");
//		params.put("version", "");
//		params.put("expansionLevel", 0);
//		params.put("relatedType", "All");
//		params.put("bomType", "Manufacturing");
//		params.put("fileTypeB", "TEMP");
//		params.put("fileTypeC", "All");
//		params.put("guid", "");
//		params.put("type", "");
//
//		callService(params);
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
		} catch (ServiceException | java.rmi.RemoteException | java.net.MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void callService(Map<String, Object> paramMap) {
		String endpoint = "http://pdm.149.sast.casc/Windchill/servlet/RPC?CLASS=com.infoengine.soap";

		Service service = new Service();
		try {
			Call call = (Call) service.createCall();
			call.setTimeout(60000);
			call.setTargetEndpointAddress(new URL(endpoint));
			call.setOperationName(new QName(endpoint, "getBomStructure"));

			// 添加参数定义
			call.addParameter("number", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("versionType", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("version", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("expansionLevel", XMLType.XSD_INT, ParameterMode.IN);
			call.addParameter("relatedType", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("bomType", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("fileTypeB", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("fileTypeC", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("guid", XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter("type", XMLType.XSD_STRING, ParameterMode.IN);

			call.setUsername("wcadmin");
			call.setPassword("Admin@149.941");
			call.setReturnType(XMLType.XSD_STRING);

			// 按顺序组织参数值
			Object[] params = new Object[]{paramMap.get("number"), paramMap.get("versionType"), paramMap.get("version"), paramMap.get("expansionLevel"), paramMap.get("relatedType"), paramMap.get("bomType"), paramMap.get("fileTypeB"), paramMap.get("fileTypeC"), paramMap.get("guid"), paramMap.get("type")};

			String ret = (String) call.invoke(params);
			System.out.println("输出信息：" + ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void callService(String method,String parameters){
		String endpoint = "http://pdm.149.sast.casc/Windchill/servlet/RPC?CLASS=com.infoengine.soap";

		parameters = parameters.trim();
		parameters = parameters.replaceAll("\r\n","");
		Service service = new Service();
		try {
			Call call = (Call) service.createCall();
			call.setTimeout(new Integer(60000));
			call.setTargetEndpointAddress(new URL(endpoint));
			call.setOperationName(new QName(endpoint, "callPDMService"));
			call.addParameter("methodName", XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 接口的参数
			call.addParameter("parameters", XMLType.XSD_STRING, javax.xml.rpc.ParameterMode.IN);// 接口的参数
			call.setUsername("wcadmin");
			call.setPassword("wcadmin");
			call.setReturnType(XMLType.XSD_STRING);
			String ret = call.invoke(new Object[] {method,parameters }).toString();
			System.out.println("输出信息："+ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

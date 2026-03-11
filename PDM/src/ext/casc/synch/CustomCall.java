package ext.casc.synch;

import javax.xml.namespace.QName;

import ext.sast.center.synch.MQConstants;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

import wt.method.RemoteAccess;

public class CustomCall implements RemoteAccess{
	/**
	 * String sendUnit,//单位  如 1XX所正式系统 （必需是SYNCHSITEINFO表内的name字段  ）
	 * 		String dsoNumber, //发放单号 如  006565  或 GXX_XX_8888888
	 * 		String dsoType,  // 类型  数据发放单/更改单
	 * 		String dsoState  //状态  已发放 / 发放失败
	 * @param dsoNumber
	 * @param dsoType
	 * @param dsoState
	 * @return
	 */
	public static String synDSOStateTo805(String dsoNumber,String dsoType,String dsoState){

		String methodName = "synDSOState";// WebService方法名称
		// 参数名称
		String[] parameterNames = new String[] {"sendUnit","dsoNumber","dsoType","dsoState"};

		QName[] parameterTypes = new QName[] {XMLType.XSD_STRING,XMLType.XSD_STRING,XMLType.XSD_STRING,XMLType.XSD_STRING};
		
		Object[] parameters = new Object[] {MQConstants.SITENAME_149,dsoNumber,dsoType,dsoState};
		String endpoint = "http://pdm.805.sast.casc/Windchill/protocolAuth/servlet/PDMStandardWS?wsdl";
		String namespaceURI = "http://standard.integration.extensions.ext/";
		Integer timeout = new Integer(6000); // Milliseconds
		try {
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setUseSOAPAction(true);
			call.setUsername("wcadmin");
			call.setPassword("manager");// 设置密码
			call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
			call.setOperationName(new QName(namespaceURI, methodName));
			
			call.setTargetEndpointAddress(new java.net.URL(endpoint));
			call.setTimeout(timeout);
			
			for (int i = 0; i < parameterNames.length && i < parameterTypes.length; i++) {
				call.addParameter(parameterNames[i], parameterTypes[i],
						javax.xml.rpc.ParameterMode.IN);
			}
			call.setReturnType(XMLType.XSD_STRING);
			String result = (String)call.invoke(parameters);
			// 输出返回结果
			System.out.println("synDSOStateTo805 = " + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}

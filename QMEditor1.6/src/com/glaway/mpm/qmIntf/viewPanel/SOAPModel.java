package com.glaway.mpm.qmIntf.viewPanel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.soap.Constants;
import org.apache.soap.SOAPException;
import org.apache.soap.rpc.Call;
import org.apache.soap.rpc.Parameter;
import org.apache.soap.rpc.Response;
import org.apache.soap.transport.http.SOAPHTTPConnection;

public class SOAPModel {

	private String taskName;
	private Vector<Parameter> patameters = new Vector<Parameter>();// ���뱻���÷����Ĳ���

	public SOAPModel(Vector<Parameter> patameters, String taskName) {
		setPatameters(patameters);
		setTaskName(taskName);
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Vector<Parameter> getPatameters() {
		return patameters;
	}

	public void setPatameters(Vector<Parameter> patameters) {
		this.patameters = patameters;
	}

	/**
	 * SOAP���
	 * 
	 * @return
	 */
	public String SOAPClient() {
		String forReturn = "";
		SOAPHTTPConnection st = new SOAPHTTPConnection();
		Properties p=getConfig();
		st.setUserName(p.getProperty("config.username"));// �����û���
		st.setPassword(p.getProperty("config.password"));// ��������
		Call call = new Call();
		call.setSOAPTransport(st);
		call.setTargetObjectURI("urn:ie-soap-rpc:com.infoengine.soap!");
		call.setMethodName(this.taskName);// ���ñ����õķ�����
		call.setEncodingStyleURI(Constants.NS_URI_SOAP_ENC);// ����URL Style
		// Encoding
		call.setParams(patameters);// ���ñ����÷����Ĳ���
		try {
			Response res = call.invoke(new URL(p.getProperty("config.url")),
					"urn:ie-soap-rpc:com.infoengine.soap!" + this.taskName);// ���÷���

			Parameter parameter = res.getReturnValue();// ��ȡ����ֵ
			//System.out.println(res.getParams());
			Object obj = parameter.getValue();
			//System.out.println(obj);
			forReturn = obj.toString();
			//System.out.println(forReturn);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return forReturn;
	}
	public Properties getConfig(){
//		String dir=System.getProperty("user.dir");
		String dir=SOAPModel.class.getResource("config.properties").toString();
		System.out.println(dir);
		File file=new File(dir.replace("file:/", ""));//+"\\"+"config.properties");
		Properties p=new Properties();
		try {
			p.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}
}
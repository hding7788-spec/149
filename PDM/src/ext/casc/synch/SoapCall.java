package ext.casc.synch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.soap.SOAPException;
import org.apache.soap.rpc.Call;
import org.apache.soap.rpc.Parameter;
import org.apache.soap.rpc.Response;
import org.apache.soap.transport.http.SOAPHTTPConnection;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import wt.util.WTException;
import wt.util.WTProperties;

public class SoapCall {
    static String CLASSNAME = SoapCall.class.getName();
    public static Properties prop = new Properties();

    public static enum SoapStatus {
        SUCCESS,
        FILE_NOT_FOUND,
        FILE_UNREADABLE,
        FAILED,
        DATA_NOT_EXIST,
        DATA_NOT_RELEASE,
        DATA_NOT_SHARED,
        DATA_CHECKED_OUT,
        DATA_UNSUPPORT,
        SITE_UNDEFINED,
        UNKNOW
    }

    static{

        try {
            WTProperties wtProperties = WTProperties.getLocalProperties();
            String codebasePath = wtProperties.getProperty("wt.codebase.location");
            FileInputStream inputStream = new FileInputStream(new File(codebasePath + File.separator + "ext" + File.separator + "casc"
                    + File.separator + "ixb" + File.separator + "ixbconfig.properties"));
            prop.load(inputStream);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public Properties getConfigInfo(){
    	Properties prop = new Properties();
        try {
            WTProperties wtProperties = WTProperties.getLocalProperties();
            String codebasePath = wtProperties.getProperty("wt.codebase.location");
            FileInputStream inputStream = new FileInputStream(new File(codebasePath + File.separator + "ext" + File.separator + "casc"
                    + File.separator + "ixb" + File.separator + "ixbconfig.properties"));
            prop.load(inputStream);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return prop;
    }

    class XMLReader extends DefaultHandler {
        private final String DocTag = "wc:COLLECTION";
        private final String RecordTag = "wc:INSTANCE";

        private ArrayList recordList = null;
        private HashMap recordMap = null;
        private boolean processBegin = false;
        private boolean recordBegin = false;
        private String mapKey = "";


        public XMLReader() {
            super();
        }

        public void setRecordlist(ArrayList alist) {
            recordList = alist;
        }

        public ArrayList getRecordlist() {
            return recordList;
        }

        public ArrayList readXML(Reader in) throws Exception {

            System.out.println("=========readXML========");

            XMLReader reader = new XMLReader();
            reader.setRecordlist(new ArrayList());
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(new InputSource(in), reader);
            return reader.getRecordlist();
        }

        public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes atts)
                throws SAXException {
            if (qualifiedName.equals(DocTag)) {
                if (!processBegin) {
                    processBegin = true;
                }
            } else if (qualifiedName.equals(RecordTag)) {
                if (processBegin) {
                    recordMap = new HashMap();
                    recordBegin = true;
                }
            } else {
                if (processBegin && recordBegin)
                    mapKey = qualifiedName;
            }
        }

        public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
            if (qualifiedName.equals(DocTag)) {
                processBegin = false;
            } else if (qualifiedName.equals(RecordTag)) {
                if (recordMap != null)
                    recordList.add(recordMap);
                recordBegin = false;
            } else {
                mapKey = "";
            }
        }

        public void characters(char[] text, int start, int length) throws SAXException {
            String strValue = new String(text, start, length);
            if (processBegin && recordBegin) {
                if (!mapKey.equals("") && recordMap != null) {
                    recordMap.put(mapKey, strValue);
                }
            }
        }
    }

    public SoapCall() {

    }

        ArrayList rtnList = new ArrayList();
        public ArrayList callToServer(String soapName, HashMap inputparams) throws WTException {

            String sendFrom = (String)inputparams.get("sendFrom");
            if("no8".equals(sendFrom)||"NO8".equals(sendFrom)){
            	sendFrom ="No8";
            }
            String SERVER_SERVICE_URL = (String) prop.get("SERVER_"+sendFrom+"_URL");
            String SERVER_USERNAME = (String) prop.get("SERVER_"+sendFrom +"_USERNAME");
            String SERVER_USERPASS = (String) prop.get("SERVER_"+sendFrom +"_PASSWORD");
        if (SERVER_SERVICE_URL.equals(""))
            throw new WTException("Not found soap service URL defined in import config.");
        try {
            URL url = new URL(SERVER_SERVICE_URL);
            SOAPHTTPConnection st = new SOAPHTTPConnection();
            st.setPassword(SERVER_USERPASS);
            st.setUserName(SERVER_USERNAME);
            Vector params = new Vector();
            Iterator it = inputparams.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                String value = (String) inputparams.get(key);
                params.addElement(new Parameter(key, java.lang.String.class, value, null));
            }

            Call call = new Call();
            call.setSOAPTransport(st);
            call.setTargetObjectURI("urn:ie-soap-rpc:com.infoengine.soap");
            call.setMethodName(soapName);
            call.setEncodingStyleURI("http://schemas.xmlsoap.org/soap/encoding/");
            call.setParams(params);
            Response resp = call.invoke(url, "urn:ie-soap-rpc:com.infoengine.soap!" + soapName);
            if (resp.generatedFault()) {
                org.apache.soap.Fault fault = resp.getFault();
                System.out.println("Generated fault: " + fault);
                throw new Exception("Soap Call Exception.");
            } else {
                Parameter ret = resp.getReturnValue();
                Object result = ret.getValue();

                // StringReader strbuf = new StringReader(result.toString());
                // ArrayList recordList = (new XMLReader()).readXML(strbuf);
                // for (int i=0; i<recordList.size(); i++) {
                // HashMap hmap=(HashMap)recordList.get(i);
                // Iterator itt=hmap.keySet().iterator();
                // while (itt.hasNext()) {
                // String key=(String)itt.next();
                // String value=(String)hmap.get(key);
                // if (key.equals("result"))
                // rtnList.add(value);
                // }
                // }
            }
        } catch (SOAPException e) {
            System.out.println("Caught SOAPException (" + e.getFaultCode() + "): " + e.getMessage());
            e.printStackTrace();
            throw new WTException(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WTException(e);
        }
        return rtnList;
    }

    public ArrayList previewCallRemoteServer(String soapName,
            HashMap inputparams) throws SOAPException, MalformedURLException {
        ArrayList rtnList = new ArrayList();
        String SERVER_SERVICE_URL = (String) prop.get("SERVER_SERVICE_URL");
        String SERVER_USERNAME = (String) prop.get("SERVER_USERNAME");
        String SERVER_USERPASS = (String) prop.get("SERVER_USERPASS");
        URL url = new URL(SERVER_SERVICE_URL);
        SOAPHTTPConnection st = new SOAPHTTPConnection();
        st.setPassword(SERVER_USERPASS);
        st.setUserName(SERVER_USERNAME);
        Vector params = new Vector();
        Iterator it = inputparams.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String value = (String) inputparams.get(key);
            params.addElement(new Parameter(key, java.lang.String.class, value,
                    null));
        }

        Call call = new Call();
        call.setSOAPTransport(st);
        call.setTargetObjectURI("urn:ie-soap-rpc:com.infoengine.soap");
        call.setMethodName(soapName);
        call.setEncodingStyleURI("http://schemas.xmlsoap.org/soap/encoding/");
        call.setParams(params);
        Response resp = call.invoke(url, "urn:ie-soap-rpc:com.infoengine.soap!"
                + soapName);
        if (resp.generatedFault()) {
            org.apache.soap.Fault fault = resp.getFault();
            System.out.println("Generated fault: " + fault);
        } else {
            Parameter ret = resp.getReturnValue();
            Object result = ret.getValue();

        }
        return rtnList;
    }

	/**查找电子会签单位会签用户
	 * @param soapName
	 * @param inputparams
	 * @return
	 * @throws MalformedURLException
	 * @throws SOAPException
	 */
	public String queryOtherServerUsers(String soapName, HashMap inputparams) throws MalformedURLException, SOAPException {
		Properties prop = getConfigInfo();
		String sendFrom = (String)inputparams.get("sendTo");
	    String SERVER_SERVICE_URL = (String) prop.get("SERVER_"+sendFrom+"_URL");
	    String SERVER_USERNAME = (String) prop.get("SERVER_"+sendFrom +"_USERNAME");
	    String SERVER_USERPASS = (String) prop.get("SERVER_"+sendFrom +"_PASSWORD");
		URL url = new URL(SERVER_SERVICE_URL);
		SOAPHTTPConnection st = new SOAPHTTPConnection();
		st.setUserName(SERVER_USERNAME);
		st.setPassword(SERVER_USERPASS);
		Vector params = new Vector();
		Iterator it = inputparams.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = (String) inputparams.get(key);
			params.addElement(new Parameter(key, java.lang.String.class, value,
					null));
		}

		Call call = new Call();
		call.setSOAPTransport(st);
		call.setTargetObjectURI("urn:ie-soap-rpc:com.infoengine.soap");
		call.setMethodName(soapName);
		call.setEncodingStyleURI("http://schemas.xmlsoap.org/soap/encoding/");
		call.setParams(params);
		Response resp = call.invoke(url, "urn:ie-soap-rpc:com.infoengine.soap!"
				+ soapName);
		if (resp.generatedFault()) {
			org.apache.soap.Fault fault = resp.getFault();
			return fault.getFaultString();
		} else {
			Parameter ret = resp.getReturnValue();
			Object result = ret.getValue();
			String backStr = result.toString();
			int begin = backStr.indexOf("<result>");
			int end = backStr.indexOf("</result>");
			String resultStr = backStr.substring(begin+8, end);
			return resultStr;
			}
	}
	public String setNo8ImportState(String soapName, HashMap inputparams) throws MalformedURLException, SOAPException {
		Properties prop = getConfigInfo();
		String sendFrom = (String)inputparams.get("sendTo");
	    String SERVER_SERVICE_URL = (String) prop.get("SERVER_"+sendFrom+"_URL");
	    String SERVER_USERNAME = (String) prop.get("SERVER_"+sendFrom +"_USERNAME");
	    String SERVER_USERPASS = (String) prop.get("SERVER_"+sendFrom +"_PASSWORD");
		URL url = new URL(SERVER_SERVICE_URL);
		SOAPHTTPConnection st = new SOAPHTTPConnection();
		st.setUserName(SERVER_USERNAME);
		st.setPassword(SERVER_USERPASS);
		Vector params = new Vector();
		Iterator it = inputparams.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = (String) inputparams.get(key);
			params.addElement(new Parameter(key, java.lang.String.class, value,
					null));
		}

		Call call = new Call();
		call.setSOAPTransport(st);
		call.setTargetObjectURI("urn:ie-soap-rpc:com.infoengine.soap");
		call.setMethodName(soapName);
		call.setEncodingStyleURI("http://schemas.xmlsoap.org/soap/encoding/");
		call.setParams(params);
		Response resp = call.invoke(url, "urn:ie-soap-rpc:com.infoengine.soap!"
				+ soapName);
		if (resp.generatedFault()) {
			org.apache.soap.Fault fault = resp.getFault();
			return fault.getFaultString();
		} else {
			Parameter ret = resp.getReturnValue();
			Object result = ret.getValue();
			String backStr = result.toString();
			return backStr;
			}
	}

}

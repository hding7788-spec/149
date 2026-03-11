package ext.casc.workflow.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import ext.casc.workflow.CSCWorkflowException;

import wt.util.WTProperties;

public class PropertiesUtil {

	private HashMap<String,String> hmConfig = new HashMap<String,String>();
	public String filepath = "";
	public static boolean VERBOSE = false;
	public static String delim = ",";
	
	public PropertiesUtil(String configFilePath) throws CSCWorkflowException{
		this.filepath = configFilePath;
		readConfig();
	}
	
	public void readConfig() throws CSCWorkflowException {
		Properties pro = new Properties();
		try {
			String wthome = (String) (WTProperties.getLocalProperties()).getProperty("wt.home", "");
			FileInputStream fis = new FileInputStream(wthome+File.separator + filepath);
			pro.load(fis);
			Enumeration e = pro.propertyNames();
			while (e.hasMoreElements()) {
				String proName = (String) e.nextElement();
				hmConfig.put(proName, (String) pro.getProperty(proName));
			}
			fis.close();
		} catch (FileNotFoundException ex) {
            throw new CSCWorkflowException("读取配置文件出错!",ex);
        } catch(IOException ex){
            throw new CSCWorkflowException("读取配置文件内容出错!",ex);
        }
	}

	public String getValue(String key){
		String strValue = (String) hmConfig.get(key);
		if (strValue == null || "".equals(strValue))
			return "";
		strValue=strValue.trim();
		return strValue;
	}
	
	public Map getAllKeysValues(){
		HashMap allKeysValues = new HashMap();
		allKeysValues = hmConfig;
		return allKeysValues;
	}
}

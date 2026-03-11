/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glaway.mpm.qmIntf.viewPanel;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * 
 * @author hywang
 */
public class ComUtil {

	public String getTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date currentTime = new Date();

		return formatter.format(currentTime);

	}

	public static String ConvertToJsonFormat(String strData) {
		if (strData == null)
			return "";
		strData = strData.replace("&", "&amp;");
		strData = strData.replace(">", "&gt;");
		strData = strData.replace("<", "&lt;");
		strData = strData.replace("\"", "&quot;");
		strData = strData.replace("'", "&apos;");

		return strData;
	}
	
	public static Logger getLogger(Class clazz)
	{
		PropertyConfigurator.configure(System.getProperty("user.dir")+"/log4j.properties");
        return Logger.getLogger(clazz);
	}
}

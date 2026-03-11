package com.glaway.mpm.print.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.glaway.mpm.util.PropertiesUtil;


public class LoadPropertiesConfig {

	private static final Logger log = Logger.getLogger(LoadPropertiesConfig.class);
	private Properties properties = new Properties();
	private static LoadPropertiesConfig instance = null;

	public synchronized static LoadPropertiesConfig getInstance(int type) {
		instance = new LoadPropertiesConfig();
		instance.loadCodeConfig(type);
		return instance;
	}

	public LoadPropertiesConfig() {

	}

	private void loadCodeConfig(int type) {
		InputStream in = null;
		File file = null;
		try {
			if (type == 1) {
				file = new File(PropertiesUtil.getLocalCodeBase() + File.separator + "d800" + File.separator + "conf" + File.separator, "technicsTypeCode.properties");
			} else if (type == 2) {
				file = new File(PropertiesUtil.getLocalCodeBase() + File.separator + "d800" + File.separator + "conf" + File.separator, "departmentCode.properties");
			} else if(type == 3){
				file = new File(PropertiesUtil.getLocalCodeBase() + File.separator + "d800" + File.separator + "conf" + File.separator, "capp-pdm_technicsType.properties");
			} else if(type == 4){
				file = new File(PropertiesUtil.getLocalCodeBase() + File.separator + "d800" + File.separator + "conf" + File.separator, "distributeDept.properties");
			} else if(type == 5){
				file = new File(PropertiesUtil.getLocalCodeBase() + File.separator + "d800" + File.separator + "conf" + File.separator, "outSourcingManager.properties");
			} else if(type == 6){
				file = new File(PropertiesUtil.getLocalCodeBase() + File.separator + "d800" + File.separator + "conf" + File.separator, "testProductList.properties");
			}else if(type == 7){
				file = new File(PropertiesUtil.getLocalCodeBase() + File.separator + "d800" + File.separator + "conf" + File.separator, "otherReportFileType.properties");
			}else if(type == 8){
				file = new File(PropertiesUtil.getLocalCodeBase() + File.separator + "d800" + File.separator + "conf" + File.separator, "otherReport.properties");
			}
			in = new FileInputStream(file);
			properties.load(in);
			//log.info("导入配置文件成功");
		} catch (IOException e) {
			if (type == 1) {
				log.error("导入配置文件：technicsTypeCode.properties异常", e);
			} else if (type == 2) {
				log.error("导入配置文件：departmentCode.properties异常", e);
			} else if (type == 3) {
				log.error("导入配置文件：capp-pdm_technicsType.properties异常", e);
			} else if (type == 4) {
				log.error("导入配置文件：distributeDept.properties异常", e);
			} else if (type == 5) {
				log.error("导入配置文件：outSourcingManager.properties异常", e);
			}
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Map<String, String> getCode() {
		Map<String, String> map = new HashMap<String, String>();
		Set<Object> keySet = this.properties.keySet();
		for (Object object : keySet) {
			String key = String.valueOf(object);
			String value = this.properties.getProperty(key);
			map.put(key, value);
		}
		return map;
	}

	public String getProcessCategory(String cappKey) {
		String value = "";
		Set<Object> keySet = this.properties.keySet();
		for (Object object : keySet) {
			String key = String.valueOf(object);
			if(cappKey.equals(key)){
				value = this.properties.getProperty(key);
				break;
			}
		}
		return value;
	}

	public Vector<String> getDistributeDept() {
		Vector<String> vec = new Vector<String>();
		String dept = this.properties.getProperty("所内部门").trim();
		String[] array = dept.split(",");
		vec.addAll(Arrays.asList(array));
		dept = this.properties.getProperty("所外部门");
		array = dept.split(",");
		vec.addAll(Arrays.asList(array));
		return vec;
	}

	public Vector<String> getOutsideDept() {
		Vector<String> vec = new Vector<String>();
		String dept = this.properties.getProperty("所外部门").trim();
		String[] array = dept.split(",");
		vec.addAll(Arrays.asList(array));
		return vec;
	}

	public String getManager() {
		String value = "";
		value = this.properties.getProperty("manager");
		return value;
	}

	public String getTestProductList() {
		String value = "";
		value = this.properties.getProperty("testProduct");
		return value;
	}

	//其他报告
	public String[] getQTBGVFileTypeValue() {
		String[] array = null;
		String value = this.properties.getProperty("otherReport");
		array = value.split(",");
		return array;
	}
	//其他报告中类型的内部名称
	public String[] getQTBGClassNameValue(String type) {
		String[] array = null;
		String value = this.properties.getProperty(type);
		array = value.split(",");
		return array;
	}

	public Map<String, String> getOtherReport() {
		Map<String, String> map = new HashMap<String, String>();
		Set<Object> keySet = this.properties.keySet();
		for (Object object : keySet) {
			String key = String.valueOf(object);
			String value = this.properties.getProperty(key);
			map.put(key, value);
		}
		return map;
	}
}

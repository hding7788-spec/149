package com.glaway.mpm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

public class LoadPPlanAttrConfig {

	private static final Logger log = Logger.getLogger(LoadPPlanAttrConfig.class);
	private Properties properties = new Properties();
	private static LoadPPlanAttrConfig instance = null;

	public synchronized static LoadPPlanAttrConfig getInstance() {
		if (instance == null) {
			instance = new LoadPPlanAttrConfig();
			instance.loadPPlanAttrConfig();
		}
		return instance;
	}

	private LoadPPlanAttrConfig() {
	}

	private void loadPPlanAttrConfig() {
		InputStream in = null;
		try {
			File file = new File(PropertiesUtil.getLocalCodeBase(), "mpmpplan_attributes.properties");
			in = new FileInputStream(file);
			properties.load(in);
			log.info("导入配置文件成功");
		} catch (IOException e) {
			log.error("导入配置文件：mpmpplan_attributes.properties异常", e);
			properties = new Properties();
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

	public Map<String, String> getAllAttriMapForPrefix(String prefix) {
		Map<String, String> map = new HashMap<String, String>();
		Set<Object> keySet = this.properties.keySet();
		for (Object object : keySet) {
			String key = String.valueOf(object);
			if (key.startsWith(prefix)) {
				String attrKey = key.substring(prefix.length() + 1, key.length());
				String attrName = this.properties.getProperty(key);
				String defaultValue = this.properties.getProperty(attrKey);
				map.put(attrKey, attrName+"@"+defaultValue);
			}
		}
		return map;
	}
}

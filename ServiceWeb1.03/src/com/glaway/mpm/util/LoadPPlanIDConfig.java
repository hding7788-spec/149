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

public class LoadPPlanIDConfig {

	private static final Logger log = Logger.getLogger(LoadPPlanIDConfig.class);
	private Properties properties = new Properties();
	private static LoadPPlanIDConfig instance = null;

	public synchronized static LoadPPlanIDConfig getInstance() {
		if (instance == null) {
			instance = new LoadPPlanIDConfig();
			instance.loadPPlanIDConfig();
		}
		return instance;
	}

	private LoadPPlanIDConfig() {
	}

	private void loadPPlanIDConfig() {
		InputStream in = null;
		try {
			File file = new File(PropertiesUtil.getLocalCodeBase(), "mpmpplan_id.properties");
			in = new FileInputStream(file);
			properties.load(in);
			log.info("导入配置文件成功");
		} catch (IOException e) {
			log.error("导入配置文件：mpmpplan_id.properties异常", e);
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

	public Map<String, String> getPPlanID() {
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

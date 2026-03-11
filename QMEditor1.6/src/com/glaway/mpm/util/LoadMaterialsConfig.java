package com.glaway.mpm.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.glaway.mpm.visual.log.VaLogger;

public class LoadMaterialsConfig {

	private static final VaLogger log = VaLogger.getLogger(LoadMaterialsConfig.class);
	private static Properties properties = new  Properties();
	private static LoadMaterialsConfig instance = null;
	public synchronized static LoadMaterialsConfig getInstance(){
		if(instance == null){
			instance = new LoadMaterialsConfig();
			instance.loadConfig();
		}
		return instance;
	}

	public static void main(String[] args){
		Map<String,String> map = LoadMaterialsConfig.getInstance().getAttributes("BZJ_");
		System.out.println(map);
	}

	private LoadMaterialsConfig(){}
	private void loadConfig(){
		InputStream in = null;
        try {
        	in = LoadMaterialsConfig.class.getClassLoader().getResourceAsStream("materialAttibutes.properties");
			properties.load(new InputStreamReader(in,"UTF-8"));
			log.info("导入配置文件成功");
		} catch (IOException e) {
			log.error("导入配置文件：materialAttibutes.properties异常",e);
			properties = new Properties();
			e.printStackTrace();
		} finally {
			try {
				if(in!=null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Map<String,String> getAttributes(String prefix) {
		Map<String,String> map = new HashMap<String,String>();
		Set<Entry<Object, Object>> set = properties.entrySet();
		if(set != null) {
			String key = "";
			for (Entry<Object, Object> entry : set) {
				key = entry.getKey().toString();
				if(key.startsWith(prefix)) {
					key = key.substring(key.indexOf("_")+1, key.length());
					System.out.println(key);
					map.put(key, entry.getValue().toString());
				}
			}
		}
		return map;
	}

}

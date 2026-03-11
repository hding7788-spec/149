package com.glaway.mpm.config;

import java.io.File;

import com.glaway.mpm.util.PropertiesUtil;

public class ConfigPath {
	
	//群组配置文件路径
	public static String GROUPCONFIG = PropertiesUtil.getLocalCodeBase() + File.separator + "com" + File.separator
			+ "glaway" + File.separator + "mpm" + File.separator + "config" + File.separator + "group.properties";
}

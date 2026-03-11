package com.glaway.mpm.util;

public class PropertiesConfigs {
	public static final String QIAN_LONG_CONFIG_PATH = "/com/glaway/mpm/config/qianlong.properties";

	public static final String MPMRESOURCE_TYPE_TO_FOLDER_CONFIG_PATH = "/com/glaway/mpm/config/typeNameToFolderPath.properties";

	public static final String TEMP_PATH_CONFIG_PATH = "/com/glaway/mpm/config/tempPath.properties";

	public static final String SYMBOL_CONFIG_PATH = "/com/glaway/mpm/config/symbol.properties";

	public static final String GLAWAY_149_CONFIG_PATH = "/ext/casc/config/glaway_149.properties";

	// private static Properties PRODUCT_FOLDER_CONFIG = null;
	//
	// private static Properties PROCESS_RESOURCE_CONFIG = null;
	//
	// public static Properties getPRODUCT_FOLDER_CONFIG() {
	// if (PRODUCT_FOLDER_CONFIG == null) {
	// PRODUCT_FOLDER_CONFIG = getProperties(PRODUCT_FOLDER_CONFIG_PATH);
	// }
	// return PRODUCT_FOLDER_CONFIG;
	// }
	//
	// public static Properties getPROCESS_RESOURCE_CONFIG() {
	// if (PROCESS_RESOURCE_CONFIG == null) {
	// PROCESS_RESOURCE_CONFIG = getProperties(PROCESS_RESOURCE_CONFIG_PATH);
	// }
	// return PROCESS_RESOURCE_CONFIG;
	// }
	//
	// public static Properties getProperties(String filePath) {
	// String basePath;
	// try {
	// basePath = (String) WTProperties.getLocalProperties().get("wt.home");
	// Properties pro = new Properties();
	// FileInputStream in = new FileInputStream(new File(basePath +
	// File.separator + "codebase" + File.separator
	// + filePath));
	// InputStreamReader reader = new InputStreamReader(in, "UTF-8");
	// pro.load(reader);
	// return pro;
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }

}

package ext.sast.center.util;

import wt.util.WTProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class PropertiesUtil {
	private Properties properties;

	/**
	 * 
	 */
	public PropertiesUtil() {

	}

	/**
	 * 
	 * @param path
	 */
	public PropertiesUtil(String path) {
		properties = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(getLocalCodeBase() + path));
			properties.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getValue(String key, String filePath) {
		try {
			FileInputStream fis = new FileInputStream(filePath);
			Properties properties = new Properties();
			properties.load(fis);
			String value = (String) properties.get(key);
			fis.close();
			return value;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取自定义的properties文件路径值
	 * 
	 * @author qianlong
	 * @date 2013-4-22
	 * @param key
	 * @return
	 * 
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	/**
	 * 获取所有的属性名称
	 * 
	 * @author qianlong
	 * @date 2013-4-22
	 * @return
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Enumeration<String> propertyNames() {

		return (Enumeration<String>) properties.propertyNames();
	}

	/**
	 * 获取WTHome的路径值
	 * 
	 * @author qianlong
	 * @date 2013-4-22
	 * @return
	 * 
	 */
	public static String getWTHome() {
		return getLocalProperties().getProperty("wt.home");
	}

	/**
	 * 获取codebase的http路径值
	 * 
	 * @author qianlong
	 * @date 2013-4-22
	 * @return
	 * 
	 */
	public static String getHttpCodeBase() {
		return getLocalProperties().getProperty("wt.server.codebase");
	}

	/**
	 * 获取codeBase的location路径值
	 * 
	 * @author qianlong
	 * @date 2013-4-22
	 * @return
	 * 
	 */
	public static String getLocalCodeBase() {
		return getLocalProperties().getProperty("wt.codebase.location");
	}

	/**
	 * 获取temp临时资料夹
	 * 
	 * @author qianlong
	 * @date 2012-12-6
	 * @return
	 * @throws IOException
	 * 
	 */
	public static String getTempPath() {

		return getLocalProperties().getProperty("wt.temp");

	}

	/**
	 * 获取windchill的本地properties对象
	 * 
	 * @author qianlong
	 * @date 2013-4-22
	 * @return
	 * 
	 */
	private static Properties getLocalProperties() {
		Properties properties = null;
		try {
			properties = WTProperties.getLocalProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}
}

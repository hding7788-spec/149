package com.glaway.mpm.wcIntf;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import wt.method.RemoteAccess;

import com.glaway.mpm.util.CappJavaUtil;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.IntfUtil;
import com.glaway.mpm.visual.log.VaLogger;

/**
 * @author ylshao
 * @ClassName: ProcessEditorToWCIntf
 * @Description:
 * @date 2012-11-28
 *
 */
public class ImageIntf implements RemoteAccess {
	private static VaLogger logger = VaLogger.getLogger(ImageIntf.class);

	/**
	 * 获取中间模型零件的.prt图档
	 *
	 * @Title: getMidModelCAD
	 * @Description:
	 * @param @param partNumber
	 * @param @param toolPath
	 * @return void
	 * @throws
	 */
	public static String getMidModelCAD(Map<String, String> map, String filePath) {
		List<Object> list = (List<Object>) remoteMethodInvoke(
				"getMidModelCADRMI", new Class[] { Map.class },
				new Object[] { map });
		logger.debug("list= " + list);
		if (list == null || list.size() == 0) {
			return null;
		}
		byte[] bytes = null;
		String fileName = (String) list.get(0);
		bytes = (byte[]) list.get(1);
		if (filePath == null) {
			return null;
		}
		FileUtil.writeBytes(filePath + fileName, bytes);
		return filePath + fileName;
	}

	/**
	 * 获取整件及其下皆所有的.prt图档
	 *
	 * @Title: getAllStructureCAD
	 * @Description:
	 * @param @param oid
	 * @param @param filePath
	 * @return void
	 * @throws
	 */
	public static String getAnimationCAD(Map<String, String> map) {
		List<Object> list = (List<Object>) remoteMethodInvoke(
				"getAnimationCADRMI", new Class[] { Map.class },
				new Object[] { map });
		if (list == null || list.size() == 0) {
			logger.debug("list == null || list.size() == 0");
			return null;
		}
		String name = (String) list.get(0);
		Map<String, byte[]> streamMap = (Map<String, byte[]>) list.get(1);
		String filePath = FileUtil.makeTmpDir("mpm/cad/cortona3d/");
		for (String fileName : streamMap.keySet()) {
			FileUtil.writeBytes(filePath + fileName, streamMap.get(fileName));
		}
		return filePath + name;

	}

	/**
	 * 获取.ol图档及其注释的名称,简图
	 *
	 * @Title: getCADAndMarkupName
	 * @Description:
	 * @return void
	 * @throws
	 */
	public static Map<List, Map<String, byte[]>> getCADAndMarkupName(
			Map<String, String> map) {
		return (Map<List, Map<String, byte[]>>) remoteMethodInvoke(
				"getCADAndMarkupNameRMI", new Class[] { Map.class },
				new Object[] { map });
	}

	/**
	 * 获取.ol图档及其注释
	 *
	 * @author qianlong
	 * @date 2012-11-26
	 * @param hashmap
	 * @return
	 *
	 */
	public static Vector<String> getCADAndMarkup(
			Map<String, List<String>> hashmap, String filePath) {
		Vector<String> vector = new Vector<String>();
		Map<Map<String, byte[]>, Map<String, byte[]>> streamMap = (Map<Map<String, byte[]>, Map<String, byte[]>>) remoteMethodInvoke(
				"getCADAndMarkupRMI", new Class[] { Map.class },
				new Object[] { hashmap });
		logger.debug(filePath.lastIndexOf(File.separator));
		filePath = filePath.substring(0, filePath.lastIndexOf(File.separator));
		logger.debug("filePath ==" + filePath);
		String newDir = new SimpleDateFormat("yyyyMMddhhmmssSSS")
				.format(new Date());

		for (Entry<Map<String, byte[]>, Map<String, byte[]>> entry : streamMap
				.entrySet()) {
			Map<String, byte[]> key = entry.getKey();
			String path = FileUtil.generatePath(filePath, newDir);
			for (Entry<String, byte[]> map1 : key.entrySet()) {
				String olName = (String) map1.getKey();
				byte[] olBytes = (byte[]) map1.getValue();
				if (path != null) {
					File file = new File(path);
					if (!file.exists()) {
						file.mkdirs();
					}
					if (olBytes == null) {
						logger.debug("olBytes = null");
						return null;
					}
					logger.debug("name= " + olName);
					if (olName.endsWith(".pvs")) {
						vector.add(newDir + "/" + olName);
					}
					logger.debug("Write " + path + olName);
					FileUtil.writeBytes(path + olName, olBytes);
				}
			}

			Map<String, byte[]> value = entry.getValue();
			for (Entry<String, byte[]> temp : value.entrySet()) {
				String name = temp.getKey();
				byte[] bytes = temp.getValue();
				logger.debug("Write " + path + name);
				FileUtil.writeBytes(path + name, bytes);
			}

		}
		return vector;

	}

	/**
	 * 获取整件pvs
	 *
	 * @param map
	 *            "oid" "viewName"
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, byte[]> getPVSAndMarkupRMI(Map<String, String> map) {
		return (Map<String, byte[]>) remoteMethodInvoke("getPVSAndMarkupRMI",
				new Class[] { Map.class }, new Object[] { map });
	}

	/**
	 * @Description:根据partOid获取零件Creo View的URL
	 */
	public static String getCreoViewUrl(String partOid) {
		logger.debug("partOid= " + partOid);
		String url = (String) remoteMethodInvoke("getCreoViewUrlRMI",
				new Class[] { String.class }, new Object[] { partOid });
		logger.debug("url= " + url);
		return url;
	}

	/**
	 * 添加pds中间模型获取.ol图档及其注释
	 *
	 * @author qianlong
	 * @date 2012-11-26
	 * @param hashmap
	 * @return
	 *
	 */
	public static Vector<List<String>> getCAD(List<String> list1,
			String filePath) {
		String oid = list1.get(0);
		Vector<List<String>> vector = new Vector<List<String>>();
		Map<String, byte[]> streamMap = (Map<String, byte[]>) remoteMethodInvoke(
				"getCADRMI", new Class[] { String.class }, new Object[] { oid });
		logger.debug(filePath.lastIndexOf(File.separator));
		filePath = filePath.substring(0, filePath.lastIndexOf(File.separator));
		logger.debug("filePath ==" + filePath);
		String newDir = CappJavaUtil.generateUniqueId();
		String version = list1.get(1);
		String modelName = list1.get(2);
		String path = FileUtil.generatePath(filePath, newDir);

		for (Entry<String, byte[]> entry : streamMap.entrySet()) {
			String olName = (String) entry.getKey();
			byte[] olBytes = entry.getValue();
			if (path != null) {
				File file = new File(path);
				if (!file.exists()) {
					file.mkdirs();
				}
				if (olBytes == null || olBytes.length == 0) {
					logger.debug("olBytes = null");
					continue;
				}
				logger.debug("name= " + olName);
				if (olName.endsWith(".pvs")) {
					List<String> list = new ArrayList<String>();
					list.add(oid);
					list.add(newDir + "/" + olName);
					list.add(version);
					list.add(modelName);
					vector.add(list);
				}
				logger.debug("Write " + path + olName);
				FileUtil.writeBytes(path + olName, olBytes);

				if (!olName.endsWith(".jpg")) {
					byte[] bytes = FileUtil.getImage("defaultPV.jpg");
					int index = olName.indexOf(".");
					if (index != -1) {
						olName = olName.substring(0, index);
					}
					FileUtil.writeBytes(path + olName + "_short.png", bytes);
				}
			}

		}
		return vector;

	}

	/**
	 * 添加pds中间模型，获取.ol图档及其注释的名称,简图
	 *
	 * @Title: getCADAndMarkupName
	 * @Description:
	 * @return void
	 * @throws
	 */
	public static List<List<Object>> getCADName(Map<String, String> map) {
		return (List<List<Object>>) remoteMethodInvoke("getCADNameRMI",
				new Class[] { Map.class }, new Object[] { map });
	}


	/**
	 *
	 *
	 * @Title: remoteMethodInvoke
	 * @Description:
	 * @return Object
	 * @throws
	 */
	public static Object remoteMethodInvoke(String mentodName,
			Class[] classArray, Object[] objectArray) {
		return IntfUtil.getRemoteMethodInvoke(mentodName, classArray,
				objectArray);
	}

}

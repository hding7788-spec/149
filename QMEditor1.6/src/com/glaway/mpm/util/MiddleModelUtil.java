package com.glaway.mpm.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.visual.log.VaLogger;

public class MiddleModelUtil {
	private static VaLogger logger = VaLogger.getLogger();
	public static final String TRANSFER_OID = "transferOid";
	public static final int ID_COUNT = 7;

	/**
	 * 转换给中间模型用的oid
	 *
	 * @param techElement
	 */
	public static void generateTransferOid(Element techElement) {
		String stepXpath = "/technics/QMFawTechnicsInfo/steps/QMProcedureInfo";
		String paceXpath = "/technics/QMFawTechnicsInfo/steps/QMProcedureInfo/paces/QMProcedureInfo";
		Document document = techElement.getDocument();
		List<Element> stepElements = XPathUtil.getElements(stepXpath, document);
		transferOid(stepElements);
		List<Element> paceElements = XPathUtil.getElements(paceXpath, document);
		transferOid(paceElements);
	}

	/**
	 * 转换成十六进制
	 *
	 * @param elements
	 */
	private static void transferOid(List<Element> elements) {
		if (elements != null && elements.size() != 0) {
			for (Element element : elements) {
				String oid = element.attributeValue("bsoID");
				int length = oid.length();
				if (length > ID_COUNT) {
					oid = oid.substring(length - ID_COUNT);
				}
				oid = oid.replace(":", "");
				element.setAttributeValue(TRANSFER_OID,
						CappJavaUtil.toHexString(oid));
			}
		}
	}

	/**
	 * 更新中间模型
	 *
	 * @param elements
	 */
	public static void refreshModels(List<Element> elements, String techPath) {
		if (elements != null && elements.size() != 0) {
			for (Element element : elements) {
				String oid = element.attributeValue("bsoID");
				String version = element.attributeValue("version");
				String absolutePath = element.attributeValue("absolutePath");
				File file = new File(techPath + File.separator + absolutePath);
				if (file.exists()) {
					List<String> list = refresh(oid, file.getParent()
							+ File.separator);
					if (list != null) {
						element.setAttributeValue("bsoID", list.get(0));
						element.setAttributeValue("version", list.get(1));
						element.setAttributeValue("modelName", list.get(2));
					}
				}
			}
		}
	}

	/**
	 * 根据oid获取最新的中间模型
	 *
	 * @param oid
	 * @param path
	 * @return
	 */
	public static List<String> refresh(String oid, String path) {
		List<String> list = null;
		Map<String, Object> streamMap = (Map<String, Object>) IntfUtil
				.getPeRemoteMethodInvoke("getNewCADRMI",
						new Class[] { String.class }, new Object[] { oid });
		if (streamMap != null && streamMap.size() != 0) {
			FileUtil.deleteSubFile(path);// 删除旧的模型
			String newOid = CappJavaUtil.convertNull(streamMap.get("oid"));
			String version = (String) streamMap.get("version");
			String modelName = (String) streamMap.get("modelName");

			for (Entry<String, Object> map1 : streamMap.entrySet()) {
				String olName = (String) map1.getKey();
				Object values = map1.getValue();
				if (values instanceof byte[]) {
					byte[] olBytes = (byte[]) values;
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
							list = new ArrayList<String>();
							list.add(newOid);
							list.add(version);
							list.add(modelName);
						}
						logger.debug("Write " + path + olName);
						FileUtil.writeBytes(path + olName, olBytes);

						if (!olName.endsWith(".jpg")) {
							byte[] bytes = FileUtil.getImage("defaultPV.jpg");
							int index = olName.indexOf(".");
							if (index != -1) {
								olName = olName.substring(0, index);
							}
							FileUtil.writeBytes(path + olName + "_short.png",
									bytes);
						}
					}
				}
			}
		}
		return list;
	}
}

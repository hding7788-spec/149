package com.test;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.glaway.mpm.util.BomXMLUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtil;

public class ParticipatePartUtil {

	public static void main(String[] args) {
		Vector<Map<String, String>> vector = new Vector<Map<String, String>>();
		Map<String, String> map = new HashMap<String, String>();
		map.put("occId", "testOccId");
		map.put("partNumber", "partNumber");
		map.put("partName", "partName");
		map.put("oid", "oid");
		vector.add(map);

		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("occId", "testOccId1");
		map1.put("partNumber", "partNumber");
		map1.put("partName", "partName");
		map1.put("oid", "oid");
		vector.add(map1);
		// addParticipateParts("AL2_907_1459", "1df24b6d:13e9c7326aa:-7fe0",
		// "1df24b6d:13e9c7326aa:-7fd9", vector);
		System.out.println(deleteParticipateParts("AL2_907_1459", vector));
	}

	/**
	 * 增加参装件的集合
	 */
	public static boolean addParticipateParts(String technicsNumber,
			String stepOid, String paceOid, Vector<Map<String, String>> parts) {
		try {
			String technicsPath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
			Document document = XmlUtil.getDocument(new File(technicsPath));
			Element techInfoElement = document.getRootElement();
			Element paceElement = getPaceElement(techInfoElement, stepOid,
					paceOid);
			addPartsToXml(parts, paceElement);
			XmlUtil.writeDocument(document, technicsPath);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 删除参装件的集合
	 * 
	 */
	public static boolean deleteParticipateParts(String technicsNumber,
			Vector<Map<String, String>> parts) {
		try {
			String technicsPath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
			Document document = XmlUtil.getDocument(new File(technicsPath));
			// 获得节点的命名空间
			String uri = document.getRootElement().getNamespaceURI();
			// 将uri存入map中
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("xx", uri);
			org.dom4j.XPath xpath = DocumentHelper
					.createXPath("/xx:technics/xx:QMFawTechnicsInfo/xx:steps/xx:QMProcedureInfo/xx:paces/xx:QMProcedureInfo/xx:parts/xx:QMPartInfo");
			xpath.setNamespaceURIs(map);
			List list = xpath.selectNodes(document);
			if (deleteParts(list, parts)) {
				XmlUtil.writeDocument(document, technicsPath);
			} else {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取到工步的parts节点
	 * 
	 * @param techInfoElement
	 *            工艺节点
	 * @param stepOid
	 *            工序oid
	 * @param paceOid
	 *            工步oid
	 * @return
	 */
	private static Element getPaceElement(Element techInfoElement,
			String stepOid, String paceOid) {
		Element partsElement = null;
		flag: for (Iterator it = techInfoElement.element("QMFawTechnicsInfo")
				.element("steps").elementIterator("QMProcedureInfo"); it
				.hasNext();) {
			Element procedureElement = (Element) it.next();
			String stepId = procedureElement.attributeValue("bsoID");
			if (stepOid.equals(stepId)) {
				for (Iterator ite = procedureElement.element("paces")
						.elementIterator("QMProcedureInfo"); ite.hasNext();) {
					Element paceElement = (Element) ite.next();
					String paceId = paceElement.attributeValue("bsoID");
					if (paceOid.equals(paceId)) {
						partsElement = paceElement;
						break flag;
					}

				}
			}
		}
		return partsElement;
	}

	/**
	 * 增加到xml中
	 * 
	 * @param parts
	 *            参装件的集合
	 * @param paceElement
	 *            工步节点
	 */
	private static void addPartsToXml(Vector<Map<String, String>> parts,
			Element paceElement) {
		if ((parts != null) && (parts.size() > 0)) {
			for (int i = 0; i < parts.size(); i++) {
				Map temp = (Map) parts.get(i);
				Element part = BomXMLUtil.generatePartData(temp, null);
				paceElement.element("parts").add(part);
			}
		}
	}

	/**
	 * xml中删除参装件
	 * 
	 */
	private static boolean deleteParts(List list,
			Vector<Map<String, String>> parts) {
		boolean flag = true;
		if ((parts != null) && (parts.size() > 0)) {
			for (int i = 0; i < parts.size(); i++) {
				Map temp = (Map) parts.get(i);
				String occId = String.valueOf(temp.get("occId"));
				flag = flag && deletePartsfromXml(occId, list);
			}
			return flag;
		}
		return !flag;
	}

	/**
	 * xml中删除参装件
	 * 
	 */
	private static boolean deletePartsfromXml(String occId, List list) {
		for (int i = 0; i < list.size(); i++) {
			Element element = (Element) list.get(i);// 转型为Element
			String occIds = element.attributeValue("occId");
			if (occIds != null) {
				String[] array = occIds.split(",");
				if (array != null) {
					for (int j = 0; j < array.length; j++) {
						if (array[j].equals(occId)) {
							if (array.length == 1) {
								element.getParent().remove(element);
							} else {
								String result = filterArray(array, j);
//								System.out.println(result);
								XmlUtil.setAttribute(element, "occId", result);
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 删除array中index为j的字符
	 * 
	 * @param array
	 * @param j
	 * @return
	 */
	private static String filterArray(String[] array, int j) {
		if (array == null) {
			return null;
		}
		String result = "";
		for (int m = 0; m < array.length; m++) {
			if (m != j) {
				result += array[m] + ",";
			}
		}
		return result.substring(0, result.length() - 1);
	}
}

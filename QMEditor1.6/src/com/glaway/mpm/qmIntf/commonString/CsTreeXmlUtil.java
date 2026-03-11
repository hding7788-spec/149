package com.glaway.mpm.qmIntf.commonString;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.glaway.mpm.model.CsType;
import com.glaway.mpm.model.ShopType;
import com.glaway.mpm.resource.ResourceCache;
import com.glaway.mpm.util.CommonStringUtil;
import com.glaway.mpm.util.JavaUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtil;
import com.glaway.mpm.wcIntf.CommonStringIntf;
import com.glaway.mpm.wcIntf.ResourceIntf;

/**
 * @author ylshao
 * @Description: CsTree
 * @date 2012-11-9
 *
 */
public class CsTreeXmlUtil {
	private static String fileName = "personalTechnicsCs.xml";

	/**
	 * @Title: searchCommonStrings
	 * @Description:
	 * @param @param filePath
	 * @param @param flag 用于区分是否增加MoustListener
	 * @param @return
	 * @return CsTree
	 * @throws
	 */
	public static CsTree searchCommonStrings(String filePath, boolean flag) {
		CsType csType = null;
		try {
			csType = CommonStringIntf.getPublicCommonStrings();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return addCsToCsTree(xmlToCsTree(filePath, flag), csType);
	}


	/**
	 * @Author caolei
	 * @Date 2015/6/3
	 * @Check caolei
	 * @Description 获取常用语树
	 */

	public static CsTree justSearchCommonStrings(String filePath, boolean flag) {
		List<CsType> csType = null;
		try {
			csType = CommonStringIntf.justGetPublicCommonStrings();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return justAddCsToCsTree(xmlToCsTree(filePath, flag), csType);
	}


	public static CsTree addCsToCsTree(CsTree csTree, CsType csType) {
		CsTreeNode root = csTree.getRoot();
		CsTreeNode csTreeNode = new CsTreeNode("公共工艺常用语");
		root.add(csTreeNode);
		if (csType != null) {
			parseNode(csTreeNode, csType.getCsTypes());
		}
		return csTree;

		// CsTreeNode root = csTree.getRoot();
		// CsTreeNode csTreeNode = new CsTreeNode("公共工艺常用语");
		// root.add(csTreeNode);
		// for (CsType type : csType) {
		// CsTreeNode node = new CsTreeNode(type.getName());
		// for (String value : type.getCommonStrings()) {
		// CsNode csNode = new CsNode(value);
		// node.add(csNode);
		// }
		// csTreeNode.add(node);
		// }
		// return csTree;

	}

	/**
	 * @Author caolei
	 * @Date 2015/6/3
	 * @Check caolei
	 * @Description 添加节点到常用语树中
	 */

	public static CsTree justAddCsToCsTree(CsTree csTree, List<CsType> csType) {
		   CsTreeNode root = csTree.getRoot();
		   CsTreeNode csTreeNode = new CsTreeNode("公共工艺常用语");
		   root.add(csTreeNode);
		if (csType != null) {
			justParseNode(csTreeNode, csType);
		}
		      return csTree;

	 }


	public static void parseNode(CsTreeNode csTreeNode, List<CsType> csType) {
		if (csType != null && csType.size() != 0) {
			for (CsType type : csType) {
				if (type.getCommonStrings() == null) {
					CsTreeNode node = new CsTreeNode(type.getShopType());
					csTreeNode.add(node);
					parseNode(node, type.getCsTypes());
				} else {
					CsTreeNode treeNode = new CsTreeNode(type.getShopType());
					for (String str : type.getCommonStrings()) {
						CsNode node = new CsNode(str);
						treeNode.add(node);
					}
					csTreeNode.add(treeNode);
				}
			}
		}
	}

	/**
	 * @Author caolei
	 * @Date 2015/6/3
	 * @Check caolei
	 * @Description 建立公共常用语树
	 */

	public static void justParseNode(CsTreeNode csTreeNode, List<CsType>
	csType) {
			if (csType != null && csType.size() != 0) {
				for (CsType cstype : csType) {
						CsTreeNode parentNode = new CsTreeNode(cstype.getName());
						csTreeNode.add(parentNode);
			            if (cstype.getCommonStrings()!=null) {
						    for (String str :cstype.getCommonStrings()) {
							CsNode chileNode = new CsNode(str);
							parentNode.add(chileNode);
						}
					}
				}
			}
		 }



	public static CsTree xmlToCsTree(String filePath, boolean flag) {

		return parseXml(filePath, flag);
	}

	public static CsTree parseXml(String filePath, boolean flag) {
		CsTree csTree = null;
		Document document = null;
		CsTreeNode rootNode = null;
		CsTreeNode csTreeNode = null;
		File file = new File(filePath);
		if (!file.isFile()) {
			try {
				file = new File(filePath + fileName);
				if (file.exists()) {
					document = getDocument(file);
					Element root = document.getRootElement();
					rootNode = new CsTreeNode(parseAttribute(root, "value"));
					Element element = getSingleElement(root, "personalCs");
					csTreeNode = new CsTreeNode(
							parseAttribute(element, "value"));
					rootNode.add(csTreeNode);
					List<Element> list = element.elements();
					for (Element temp : list) {
						CsTreeNode node = new CsTreeNode(parseAttribute(temp,
								"value"));
						csTreeNode.add(node);
						List<Element> subList = temp.elements();
						for (Element subTemp : subList) {
							CsNode csNode = new CsNode(parseAttribute(subTemp,
									"value"));
							node.add(csNode);
						}
					}
				} else {
					if (writeTemplateDocument(filePath + fileName)) {
						return parseXml(filePath, flag);
					} else {
						rootNode = new CsTreeNode("csTree");
						CsTreeNode node = new CsTreeNode("个人常用语");
						rootNode.add(node);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		csTree = new CsTree(rootNode, flag);
		return csTree;

	}
	public static void refreshCsTree() {
		Document document = null;
		String xmlPath = WorkSpaceUtil.getPersonalTerminologyDirectory() + "personalTechnicsCs.xml";
		File file = new File(xmlPath);
		if (!file.exists())  return;
		document = getDocument(file);
		Element root = document.getRootElement();
		Element element = getSingleElement(root, "personalCs");
		CsTreeNode csTreeNode = new CsTreeNode(parseAttribute(element, "value"));

		Enumeration nodes =  CsTreePanel.csTree.getRoot().children();
		CsTreeNode node = (CsTreeNode) nodes.nextElement();
		node.removeAllChildren();
		List<Element> list = element.elements();
			for (Element temp : list) {
				CsTreeNode newNode = new CsTreeNode(parseAttribute(temp,"value"));
				node.add(newNode);
				List<Element> subList = temp.elements();
				for (Element subTemp : subList) {
					CsNode csNode = new CsNode(parseAttribute(subTemp,"value"));
						newNode.add(csNode);
					}
			}
		CsTreePanel.csTree.updateUI();

	}
	public static Document getDocument(File file) {
		if (null == file || (!file.isFile()))
			return null;
		SAXReader saxReader = new SAXReader();
		saxReader.setEncoding("GBK");
		Document document = null;
		try {
			document = saxReader.read(file);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return document;
	}

	public static boolean writeTemplateDocument(String filePath) {
		InputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = CsTreeXmlUtil.class
					.getResourceAsStream("/resource/csTemplate.xml");
			fos = new FileOutputStream(filePath);
			byte[] b = new byte[1024];
			int temp = 0;
			while ((temp = fis.read(b)) != -1) {
				fos.write(b, 0, temp);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			JavaUtil.closeStream(fis);
			JavaUtil.closeStream(fos);
		}
		return true;
	}

	public static Element getSingleElement(Element element, String elementName) {

		return element.element(elementName);
	}

	public static String parseAttribute(Element element, String type) {

		return element.attribute(type).getValue();
	}

	/***
	 * obj->xml
	 *
	 * @param xml
	 * @return
	 */
	public static Document csTreeToXML(CsTree csTree, String filePath) {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("csTree");
		CsTreeNode rootNode = csTree.getRoot();
		root.addAttribute("value", rootNode.getValue());
		Element element = root.addElement("personalCs");
		CsTreeNode csTreeNode = (CsTreeNode) rootNode.children().nextElement();
		element.addAttribute("value", csTreeNode.getValue());
		parseNode(csTreeNode, element);
		writeDocument(document, filePath);
		return document;
	}

	public static void parseNode(CsTreeNode node, Element element) {
		Enumeration<CsTreeNode> childen = node.children();
		while (childen.hasMoreElements()) {
			Object object = childen.nextElement();
			if (object instanceof CsTreeNode) {
				CsTreeNode treeNode = (CsTreeNode) object;
				Element childElement = element.addElement("type");
				childElement.addAttribute("value", treeNode.getValue());
				parseNode(treeNode, childElement);
			} else {
				CsNode csNode = (CsNode) object;
				Element childElement = element.addElement("commonString");
				childElement.addAttribute("value", csNode.getValue());
			}
		}
	}

	public static void writeDocument(Document document, String filePath) {
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			OutputFormat xmlFormat = OutputFormat.createPrettyPrint();
			xmlFormat.setEncoding("GBK");
			XMLWriter xmlWriter = new XMLWriter(fos, xmlFormat);
			xmlWriter.write(document);
			xmlWriter.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static CsTree searchWorkShops() {
		CsTreeNode root = new CsTreeNode("工种名称");
		CsTreeNode csTreeNode = new CsTreeNode("工种名称");
		System.out.println("-----CsTreeXmlUtil.searchWorkShops()----ResourceCache.shopTypes:"+ResourceCache.shopTypes);
		if(ResourceCache.shopTypes.isEmpty()) {
			ResourceIntf.getInitData();
		}
		if (ResourceCache.shopTypes != null) {
			for (ShopType shopType : ResourceCache.shopTypes) {
				CsTreeNode node = new CsTreeNode(shopType);
				csTreeNode.add(node);
			}
		}
		root.add(csTreeNode);
		CsTree csTree = new CsTree(root, false);
		return csTree;
	}

	/**
	 * 将模板中的常用语与现有常用语合并
	 *
	 * @param map
	 * @return
	 */
	public static boolean mergeCsFromTemplate(Map<String, List<String>> map) {
		boolean flag = false;

		String xmlPath = WorkSpaceUtil.getPersonalTerminologyDirectory()
				+ "personalTechnicsCs.xml";
		Document document = XmlUtil.getDocument(xmlPath);
		Set<Entry<String, List<String>>> set = map.entrySet();
		for (Entry<String, List<String>> entry : set) {
			String key = entry.getKey();
			List<String> value = entry.getValue();
			mergeCs(key, value, document);
		}

		XmlUtil.writeDocument(document, xmlPath);
		return flag;
	}

	/**
	 * 合并
	 *
	 * @param document
	 * @param value
	 * @param key
	 */
	private static void mergeCs(String key, List<String> value,
			Document document) {
		Element rootElement = document.getRootElement();
		Element personalCsElement = rootElement.element("personalCs");
		String uri = rootElement.getNamespaceURI();
		// 将uri存入map中
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("xx", uri);

		String prefix = " /csTree/personalCs/type[@value='";
		String suffix = "']";
		String path = prefix + key + suffix;
		XPath xpath = DocumentHelper.createXPath(path);
		xpath.setNamespaceURIs(map);
		List<Element> list = xpath.selectNodes(document);
		if (list != null && list.size() != 0) {
			Element element = list.get(0);
			if (value != null && value.size() != 0) {
				for (String cs : value) {
					XPath newPath = DocumentHelper.createXPath(path
							+ "/commonString[@value='" + cs + suffix);
					newPath.setNamespaceURIs(map);
					List<Element> commonStrings = newPath.selectNodes(document);
					if (commonStrings == null || commonStrings.size() == 0) {
						Element csElement = element.addElement("commonString");
						csElement.addAttribute("value", cs);
					}
				}
			}
		} else {
			Element typeElement = personalCsElement.addElement("type");
			typeElement.addAttribute("value", key);
			if (value != null && value.size() != 0) {
				for (String cs : value) {
					XPath newPath = DocumentHelper.createXPath(path
							+ "/commonString[@value='" + cs + suffix);
					newPath.setNamespaceURIs(map);
					List<Element> commonStrings = newPath.selectNodes(document);
					if (commonStrings == null || commonStrings.size() == 0) {
						Element csElement = typeElement
								.addElement("commonString");
						csElement.addAttribute("value", cs);
					}
				}
			}
		}

	}

	public static void main(String[] args) {
		// File file = new File("C:\\Users\\xuehu\\Desktop\\常用语导入模板.xls");
		// HSSFWorkbook workbook = ExcelUtil.getWorkbook(file);
		// CommonStringUtil.importCommonStringFromWorkbook(workbook);
		CommonStringUtil.exportCommonString("C:\\Users\\xuehu\\Desktop\\");
	}

	/**
	 * 获取本地常用语的Map
	 *
	 * @return
	 */
	public static Map<String, List<String>> getCommonStringMap() {
		Map<String, List<String>> returnMap = new LinkedHashMap<String, List<String>>();
		String xmlPath = WorkSpaceUtil.getPersonalTerminologyDirectory() + "personalTechnicsCs.xml";
		Document document = XmlUtil.getDocument(xmlPath);
		String uri = document.getRootElement().getNamespaceURI();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("xx", uri);
		String prefix = " /csTree/personalCs/type";
		XPath xpath = DocumentHelper.createXPath(prefix);
		xpath.setNamespaceURIs(map);
		List<Element> list = xpath.selectNodes(document);
		if (list != null && list.size() != 0) {
			for (Element element : list) {
				List<Element> elements = element.elements();
				List<String> commonStrings = null;
				if (elements != null && elements.size() != 0) {
					commonStrings = new ArrayList<String>();
					for (Element temp : elements) {
						commonStrings.add(temp.attributeValue("value"));
					}
				}
				returnMap.put(element.attributeValue("value"), commonStrings);
			}
		}
		return returnMap;

	}
}
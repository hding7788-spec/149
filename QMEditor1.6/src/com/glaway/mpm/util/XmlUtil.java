package com.glaway.mpm.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.glaway.mpm.visual.log.VaLogger;

/**
 * @author ylshao
 * @Description: Dom4j xml帮助类
 * @date 2012-11-9
 *
 */
public class XmlUtil {

	private static VaLogger logger = VaLogger.getLogger(XmlUtil.class);

	/**
	 * @Description: 解析xml
	 */
	public static boolean isEmpty(String path) {
		return (path == null || path.length() == 0) ? true : false;
	}

	public static boolean isNotEmpty(String arg) {
		return (arg != null && arg.length() > 0) ? true : false;
	}

	/**
	 * @Title: getDocument
	 * @Description: 通过文件获取Document对象
	 * @param @param xmlPath
	 * @param @return
	 * @param @
	 * @return Document
	 * @throws
	 */
	public static Document getDocument(String xmlPath) {
		if (isEmpty(xmlPath)) {
			logger.error("The xml file path is null or ''");
			return null;
		}

		return getDocument(new File(xmlPath));
	}

	public static Document getDocument(File file) {
		if (!file.exists()) {
			logger.error("The xml file is not exist!");
			return null;
		}
		Document document = null;
		SAXReader saxReader = new SAXReader();
		saxReader.setEncoding("GBK");
		try {
			document = saxReader.read(file);
		} catch (DocumentException e) {
			logger.error(e);
		}
		return document;
	}

	/**
	 * @Title: xmlStrToDocument
	 * @Description: xml字符串转换为Document对象
	 * @param @param strXmlContent
	 * @param @return
	 * @param @
	 * @return Document
	 * @throws
	 */
	public static Document xmlStrToDocument(final String strXmlContent) {
		if (isEmpty(strXmlContent)) {
			logger.error("The xml content is null or ''");
		}
		String s = strXmlContent;
		s = s.trim();
		if (s.length() > 0) {
			if (s.charAt(0) != '<')
				logger.error("The String format is not a xml format");
		}
		Document document = null;
		try {
			document = DocumentHelper.parseText(s);
		} catch (Exception e) {
			logger.error(e);
		}
		return document;
	}

	/**
	 * @Title: getRootElement
	 * @Description: 获取根对象
	 * @param @param document
	 * @param @return
	 * @param @
	 * @return Element
	 * @throws
	 */
	public static Element getRootElement(Document document) {
		if (document == null) {
			logger.error("The document is null, connot get it's root element");
			return null;
		}
		return document.getRootElement();
	}

	/**
	 * @Title: getElements
	 * @Description: 获取element元素的子元素集合
	 * @param @param element
	 * @param @return
	 * @param @
	 * @return List<Element>
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> getElements(Element element) {
		if (element == null) {
			return null;
		}
		return element.elements();
	}

	/**
	 * @Title: getElementsByName
	 * @Description: 获取名称为name的element元素的子元素集合
	 * @param @param element
	 * @param @param name
	 * @param @return
	 * @param @
	 * @return List<Element>
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> getElementsByName(Element element, String name) {
		if (element == null) {
			return null;
		}
		return element.elements(name);
	}

	/**
	 * @Title: getFirstElement
	 * @Description: 获取element元素的第一个子元素
	 * @param @param element
	 * @param @return
	 * @param @
	 * @return Element
	 * @throws
	 */
	public static Element getFirstElement(Element element) {
		if (element == null || element.elements().size() == 0) {
			return null;
		}
		return (Element) element.elements().get(0);
	}

	/**
	 * @Title: getFirstElementForName
	 * @Description: 获取名称为name的element元素的第一个子元素
	 * @param @param element
	 * @param @param name
	 * @param @return
	 * @param @
	 * @return Element
	 * @throws
	 */
	public static Element getFirstElementForName(Element element, String name) {
		if (element == null || element.elements(name).size() == 0) {
			return null;
		}
		return (Element) element.elements(name).get(0);
	}

	/**
	 * @Title: setAttribute
	 * @Description: 设置element元素的属性
	 * @param @param element
	 * @param @param attributeName
	 * @param @param value
	 * @return void
	 * @throws
	 */
	public static void setAttribute(Element element, String attributeName,
			String value) {
		Attribute attribute = element.attribute(attributeName);
		if (attribute != null) {
			element.remove(attribute);
		}
		element.addAttribute(attributeName, value);
	}

	/**
	 * @Title: removeAttribute
	 * @Description: 删除element元素的属性
	 * @param @param element
	 * @param @param attributeName
	 * @return void
	 * @throws
	 */
	public static void removeAttribute(Element element, String attributeName) {
		Attribute attribute = element.attribute(attributeName);
		if (attribute != null) {
			element.remove(attribute);
		}
	}

	/**
	 * @Title: convertToString
	 * @Description: Document对象转换成String
	 * @param @param document
	 * @param @return
	 * @param @
	 * @return String
	 * @throws
	 */
	public static String convertToString(Document document) {
		if (document == null) {
			logger.error("The document is null, cannot convert to String");
			return null;
		}
		return document.asXML();
	}

	/**
	 * @Description: 生成xml
	 */

	/**
	 * @Title: addElement
	 * @Description: 增加属性
	 * @param @param element
	 * @param @param elementName
	 * @param @return
	 * @return Element
	 * @throws
	 */
	public static Element addElement(Element element, String elementName) {
		if (element == null) {
			return null;
		}
		return element.addElement(elementName);
	}

	/**
	 * @Title: createDocument
	 * @Description: 通过根元素的rootName生成一个Document对象
	 * @param @param rootName
	 * @param @return
	 * @return Document
	 * @throws
	 */
	public static Document createDocument(String rootName) {
		Element rootElement = DocumentHelper.createElement(rootName);
		return DocumentHelper.createDocument(rootElement);
	}

	/**
	 * @Title: writeDocument
	 * @Description: 将Document元素写到filePath路径下
	 * @param @param document
	 * @param @param filePath
	 * @return void
	 * @throws
	 */
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

	/**
	 * @Title: getDocument
	 * @Description: 通过字节数组生成Document对象
	 * @param @param xmlContentArray
	 * @param @return
	 * @param @throws Exception
	 * @return Document
	 * @throws
	 */
	public static Document getDocument(byte[] xmlContentArray) throws Exception {
		SAXReader saxReader = new SAXReader();
		saxReader.setEncoding("GBK");
		Document document = null;
		ByteArrayInputStream inputStream = null;
		try {
			inputStream = new ByteArrayInputStream(xmlContentArray);
			document = saxReader.read(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (null != inputStream)
				inputStream.close();
			inputStream = null;
			saxReader = null;
		}
		return document;
	}

	/**
	 * @Title: generateDocumentByteArray
	 * @Description: 将Document对象转换成字节数组
	 * @param @param document
	 * @param @return
	 * @param @throws Exception
	 * @return byte[]
	 * @throws
	 */
	public static byte[] generateDocumentByteArray(Document document)
			throws Exception {
		if (null == document)
			return null;
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLWriter writer = new XMLWriter(out, format);
		writer.write(document);
		writer.flush();
		byte[] array = out.toByteArray();
		writer.close();
		out.close();
		out = null;
		return array;
	}
	/**
	 * 解码HTML实体
	 */
	public static String decodeHtmlEntities(String html) {
		// 简化版HTML实体解码
		return html.replace("&lt;", "<")
				.replace("&gt;", ">")
				.replace("&apos;", "'")
				.replace("&quot;", "\"")
				.replace("&amp;", "&");
	}

	/**
	 * 从HTML内容中提取图片文件名
	 */
	public static List<String> extractImageFilenames(String html) {
		List<String> filenames = new ArrayList<>();

		// 匹配img标签中的src属性
		Pattern imgPattern = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
		Matcher imgMatcher = imgPattern.matcher(html);

		while (imgMatcher.find()) {
			String src = imgMatcher.group(1);
			// 提取文件名（从最后一个斜杠或反斜杠后面的部分）
			int lastSeparator = Math.max(src.lastIndexOf('/'), src.lastIndexOf('\\'));
			if (lastSeparator >= 0 && lastSeparator < src.length() - 1) {
				filenames.add(src.substring(lastSeparator + 1));
			}
		}

		return filenames;
	}
}
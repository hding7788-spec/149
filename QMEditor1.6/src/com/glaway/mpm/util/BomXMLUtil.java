package com.glaway.mpm.util;

import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;

public class BomXMLUtil {
	public static final String BOM_ROOT_TAG = "Product";
	public static final String Part_GROUP = "parts";
	public static final String TECHNICS = "QMFawTechnicsInfo";
	public static final String PART_TAG = "QMPartInfo";
	public static final String PARTNUMBER_TAG = "partNumber";
	public static final String PARTNAME_TAG = "partName";
	public static final String PRODUCTNUMBER_TAG = "productNumber";
	public static final String PRODUCTNAME_TAG = "productName";
	public static final String CHILDS = "childs";
	public static final String TECHNICS_GROUP = "technics";
	public static int cishu=1;
	public static int cishu1=1;
	public static byte[] generateDocumentByteArray(Document document)
			throws Exception {
		if (document == null)
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

	public static Document getDocument(String fileName) throws Exception {
		if ((fileName == null) || (fileName.trim().equals("")))
			return null;
		return getDocument(new File(fileName));
	}

	public static Document getDocument(File file) throws Exception {
		if ((file == null) || (!file.isFile()))
			return null;
		SAXReader saxReader = new SAXReader();
		saxReader.setEncoding("GBK");
		Document document = saxReader.read(file);
		return document;
	}

	public static Document getDocument(byte[] xmlContentArray) throws Exception {
		if ((xmlContentArray == null) || (xmlContentArray.length == 0))
			return null;
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
			if (inputStream != null)
				inputStream.close();
			inputStream = null;
			saxReader = null;
		}
		return document;
	}

	public static void saveDocument(Document document, File file)
			throws Exception {
		if ((document == null) || (file == null))
			return;
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GBK");
		XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
		writer.write(document);
		writer.close();
	}

	public static void saveDocument(Document document, String fileName)
			throws Exception {
		if ((document == null) || (fileName == null)
				|| (fileName.trim().equals("")))
			return;
		saveDocument(document, new File(fileName));
	}

	public static Element getProduct(Document document) throws Exception {
		Element rootElement = document.getRootElement();
		if (!rootElement.getName().equals("Product"))
			throw new Exception("BOM文件格式错误！不能正常读取！");
		return rootElement;
	}

	public static List getChildProducts(Element partElement) {
		Element children = partElement.element("childs");
		if (children == null)
			children = partElement.addElement("childs");
		return children.elements();
	}

	public static List getTechnics(Element partElement) throws Exception {
		Element technics = partElement.element("technics");
		if (technics == null)
			technics = partElement.addElement("technics");
		return technics.elements();
	}

	public static void insertChildPart(Element parentPart, Element childPart) {
		Element children = parentPart.element("childs");
		if (children == null)
			children = parentPart.addElement("childs");
		children.add(childPart);
	}

	public static Element getMainPart(Element productElement) {
		if (productElement != null) {
			Element parts = productElement.element("parts");
			if (parts != null) {
				List list = parts.elements();
				if ((list != null) && (!list.isEmpty())) {
					return (Element) list.get(0);
				}
			}
		}
		return null;
	}

	public static String getPartIdentufy(Element partElement) {
		String result = "";
		if ((partElement != null) && (partElement.getName().equals("QMPartInfo"))) {
			String number = partElement.attributeValue("partNumber");
			if (number == null)
				number = "";
			String name = partElement.attributeValue("partName");
			if (name == null) {
				name = "";
			}
			String version = partElement.attributeValue("version");
			if (version == null) {
				version = "";
			}
			String technicsVersion = "";
			try {
				Document doc = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(number);
				if (doc != null) {
					Element tech = XmlUtility.getTechnicsElement(doc);
					technicsVersion = tech.attributeValue("version");
					if (technicsVersion == null) {
						technicsVersion = "";
					}
				}
			} catch (Exception localException) {
				localException.printStackTrace();
			}

			String useCount = "1";
			String conut = partElement.attributeValue("useCount");
			if ((conut != null) && (conut.trim().length() > 0))
				useCount = conut;
			if (useCount.trim().equals("1")) {
				result = number + " (" + name + ") " + version + " " + technicsVersion;
			} else {
				result = number + " (" + name + ") " + version + " " + technicsVersion + " ×" + useCount;
			}

			String gysl = getGYSLFromWNC(partElement,number);
			if(gysl==null || gysl.equals("")){
				gysl = partElement.attributeValue("gysl");
			}
			if(gysl != null && !"".equals(gysl)) {
				result = result + "(" + gysl + ")";
			}else{
				//author chenming   date:2015-9-2
				String useCount1 = partElement.attributeValue("useCount");//工艺编辑器的树状展示，当工艺数量为空时，将useCount用括号括起来
				result = result + "(" + useCount1 + ")";
			}

			String batch = partElement.attributeValue("BATCH");
			if(batch != null && !"".equals(batch)) {
				result = result +" ("+batch+")";
			}
		}
		return result;
	}
	public  static String getGYSLFromWNC( Element partElement,String number ) {
		if(partElement.getParent()==null) return null;

		Element fatherElement = partElement.getParent().getParent();
		String fPartNumber = fatherElement.attributeValue("partNumber");
		String fPartVersion = fatherElement.attributeValue("version");
		if(fPartNumber==null||"".equals(fPartNumber)){
			return null;
		}
		String gysl = null;
		String key = fPartNumber+"->"+number;

		if( NewTechnicsPart.partLinkGYSLFromWNC.containsKey(key)){
			return NewTechnicsPart.partLinkGYSLFromWNC.get(key);
		}
		gysl = (String) IntfUtil.getPeRemoteMethodInvoke("getGYSLOfPart",
                new Class[] { String.class,String.class,String.class },
                new Object[] {fPartNumber, fPartVersion, number});
		NewTechnicsPart.partLinkGYSLFromWNC.put(key,gysl);
		return gysl;
	}
	public static String getProductIdentufy(Element productElement){
		if ((productElement != null) && (productElement.getName().equals("Product"))) {
//			String number = productElement.attributeValue("productNumber");
			String name = productElement.attributeValue("productName");

			Element qMPartInfoElement = productElement.element("parts").element("QMPartInfo");
			String partNumber = qMPartInfoElement.attributeValue("partNumber");
			String productNumber = "";
			try {
				productNumber = TechnicsIntf.getProductNumberAndName(partNumber);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return productNumber + "_" + productNumber;
		}
		return "";
	}

	public static boolean comparePart(Element part1, Element part2) {
		if (part1 == null) {
			if (part2 != null)
				return false;
		} else {
			if (part2 == null) {
				return false;
			}

			String number1 = part1.attributeValue("partNumber");
			String number2 = part2.attributeValue("partNumber");

			String name1 = part1.attributeValue("partName");
			String name2 = part2.attributeValue("partName");

			if ((!number1.equals(number2)) || (!name1.equals(name2))) {
				return false;
			}
		}
		return true;
	}

	public static Element getProductMessage(Element partElement)
			throws Exception {
		Document doc = partElement.getDocument();
		Element productElement = getProduct(doc);
		return productElement;
	}

	public static Vector getSpecifyChildPartDatas(Document product,
			String[] pathData) {
		Vector vec = new Vector();
		if ((product != null) && (pathData != null) && (pathData.length >= 2)) {
			Element root = product.getRootElement();
			String productNumber = root.attributeValue("productNumber");
			if (productNumber.equals(pathData[0])) {
				vec.add(root);
			}
			Element mainPart = getMainPart(root);
			String mainPartNumber = mainPart.attributeValue("partNumber");
			if (mainPartNumber.equals(pathData[1])) {
				vec.add(mainPart);
			}
			Element currentEle = mainPart;
			for (int i = 2; i < pathData.length; i++) {
				Element temp = getDirectChildPart(currentEle, pathData[i]);
				if (temp == null)
					break;
				vec.add(temp);
				currentEle = temp;
			}
		}
		return vec;
	}

	public static Element getDirectChildPart(Element parent, String childNumber) {
		if ((parent != null) && (childNumber != null)) {
			String number = parent.attributeValue("partNumber");
			List list = getChildProducts(parent);
			if ((list != null) && (list.size() > 0)) {
				for (int i = 0; i < list.size(); i++) {
					Element child = (Element) list.get(i);
					String tempNum = child.attributeValue("partNumber");
					if (tempNum.equals(childNumber)) {
						return child;
					}
				}
			}
		}
		return null;
	}

	public static String getPath(Element partElement) {
		Vector vec = new Vector();
		Element parent = partElement;
		do {
			vec.add(0, parent.attributeValue("partNumber"));

			parent = parent.getParent().getParent();
		} while (!parent.getName().equals("Product"));
		vec.add(0, parent.attributeValue("productNumber"));

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < vec.size(); i++) {
			if (i != 0) {
				buffer.append("/");
			}
			buffer.append(vec.get(i).toString());
		}

		return buffer.toString();
	}

	public static Vector getDifferentPartNumber(Element partElement,
			Element techElement) {
		Vector partVector = getAllPartsPartNumber(partElement);
		Vector techVector = getAllTechnicsPartNumber(techElement);
		partVector.removeAll(techVector);
		return partVector;
	}

	public static Vector getAllTechnicsPartNumber(Element techElement) {
		Vector partNumberVec = new Vector();
		Element techInfoElement = techElement;

		for (Iterator it = techInfoElement.element("parts").elementIterator(
				"QMPartInfo"); it.hasNext();) {
			String partNum = ((Element) it.next()).attributeValue("partNumber");
			if (!partNumberVec.contains(partNum)) {
				partNumberVec.add(partNum);
			}
		}
		for (Iterator it = techInfoElement.element("steps").elementIterator(
				"QMProcedureInfo"); it.hasNext();) {
			Element procedureElement = (Element) it.next();

			addProcedurePartNumber(procedureElement, partNumberVec);

			for (Iterator ite = procedureElement.element("paces")
					.elementIterator("QMProcedureInfo"); ite.hasNext();) {
				Element paceElement = (Element) ite.next();

				addProcedurePartNumber(paceElement, partNumberVec);
			}
		}
		return partNumberVec;
	}

	private static void addProcedurePartNumber(Element procedureElement,
			Vector partNumberVec) {
		for (Iterator it = procedureElement.element("parts").elementIterator(
				"QMPartInfo"); it.hasNext();) {
			String partNum = ((Element) it.next()).attributeValue("partNumber");
			if (!partNumberVec.contains(partNum))
				partNumberVec.add(partNum);
		}
	}

	public static Vector getAllPartsPartNumber(Element partElement) {
		Vector partNumberVec = new Vector();
		Element partInfoElement = partElement.element("QMPartInfo");
		getPartNumber(partInfoElement, partNumberVec);
		return partNumberVec;
	}

	private static void getPartNumber(Element partInfoElement,
			Vector partNumberVec) {
		String partNum = partInfoElement.attributeValue("partNumber");
		if ((partNum != null) && (!partNum.equals(""))
				&& (!partNumberVec.contains(partNum))) {
			partNumberVec.add(partNum);
		}
		Element childElement = partInfoElement.element("childs");
		if (childElement != null) {
			for (Iterator it = childElement.elementIterator("QMPartInfo"); it
					.hasNext();) {
				getPartNumber((Element) it.next(), partNumberVec);
			}
		}
	}

	public static Vector getStepReferenceAssemblies(Element procedureElement) {
		Vector result = new Vector();
		HashMap map = new HashMap();

		addProcedurePart(procedureElement, map);

		for (Iterator ite = procedureElement.element("paces").elementIterator(
				"QMProcedureInfo"); ite.hasNext();) {
			Element paceElement = (Element) ite.next();

			addProcedurePart(paceElement, map);
		}
		if (map.size() > 0)
			result.addAll(map.values());
		return result;
	}

	private static void addProcedurePart(Element procedureElement, HashMap map) {
		for (Iterator it = procedureElement.element("parts").elementIterator(
				"QMPartInfo"); it.hasNext();) {
			Element ele = (Element) it.next();
			String partNum = ele.attributeValue("partNumber");
			map.put(partNum, ele);
		}
	}

	public static String judgeTechnicsType(String partNum) {
		String type = "零件工艺";
		if (partNum.startsWith("AL")) {
			char[] chars = partNum.toCharArray();
			char ch = chars[2];
			if (Character.isDigit(ch)) {
				int num = Integer.parseInt(partNum.substring(2, 3));
				if ((num > 0) && (num < 7)) {
					type = "装配工艺";
				}
			}
		} else if (partNum.startsWith("GAL") || partNum.startsWith("GK")
				|| partNum.startsWith("GT")) {
			if (partNum.indexOf("-") == -1) {
				type = "装配工艺";
			} else {
				String[] numbers = partNum.split("-");
				if (numbers[1].startsWith("6.")) {
					type = "装配工艺";
				}
			}
		}
		return type;
	}

	public static void getAllPartDatas(Element ele, Vector numbers,
			Vector result) throws Exception {
		if ((result == null) || (numbers == null) || (ele == null))
			return;
		Element mainPart = ele;
		String partMumber = mainPart.attributeValue("partNumber");
		if (partMumber != null) {
			System.out.println("numbers=========" + numbers);
			System.out.println("partMumber=========" + partMumber);
			if (numbers.contains(partMumber)) {
				result.add(mainPart);
			}
		}
		Element childElement = mainPart.element("childs");
		if (childElement != null) {
			for (Iterator it = childElement.elementIterator("QMPartInfo"); it
					.hasNext();) {
				getAllPartDatas((Element) it.next(), numbers, result);
			}
		}
	}

	public static Element generatePartData(Map map, String partType) {
		if ((map == null) || (map.size() == 0))
			return null;
		Element part = XmlUtility.createPart();
		XmlUtility.setAttributeValue(part, "partNumber", map.get("partNumber").toString());
		XmlUtility.setAttributeValue(part, "partName", map.get("partName").toString());
		if (map.get("oid") != null) {
			XmlUtility.setAttributeValue(part, "oid", map.get("oid").toString());
		} else {
			XmlUtility.setAttributeValue(part, "oid", "");
		}

		if (map.get("occId") != null) {
			XmlUtility.setAttributeValue(part, "occId", map.get("occId").toString());
		} else {
			XmlUtility.setAttributeValue(part, "occId", "");
		}

		if (map.get("partType") != null) {
			XmlUtility.setAttributeValue(part, "partType", map.get("partType").toString());
		} else if (partType != null)
			XmlUtility.setAttributeValue(part, "partType", partType);
		else {
			XmlUtility.setAttributeValue(part, "partType", "normal");
		}

		if (map.get("useCount") != null) {
			XmlUtility.setAttributeValue(part, "useCount", map.get("useCount").toString());
		} else {
			XmlUtility.setAttributeValue(part, "useCount", "1");
		}

		if (map.get("MTYPE") != null) {
			XmlUtility.setAttributeValue(part, "MTYPE", map.get("MTYPE").toString());
		} else {
			XmlUtility.setAttributeValue(part, "MTYPE", "");
		}

		if (map.get("CSIZE") != null) {
			XmlUtility.setAttributeValue(part, "CSIZE", map.get("CSIZE").toString());
		} else {
			XmlUtility.setAttributeValue(part, "CSIZE", "");
		}

		if (map.get("XHPH") != null) {
			XmlUtility.setAttributeValue(part, "XHPH", map.get("XHPH").toString());
		} else {
			XmlUtility.setAttributeValue(part, "XHPH", "");
		}
		if (map.get("ZCMARK") != null) {
            XmlUtility.setAttributeValue(part, "ZCMARK", map.get("ZCMARK").toString());
        } else {
            XmlUtility.setAttributeValue(part, "ZCMARK", "");
        }
		if (map.get("JSTJ") != null) {
            XmlUtility.setAttributeValue(part, "JSTJ", map.get("JSTJ").toString());
        } else {
        	XmlUtility.setAttributeValue(part, "JSTJ", "");
        }
		if (map.get("GG") != null) {
            XmlUtility.setAttributeValue(part, "GG", map.get("GG").toString());
        } else {
        	XmlUtility.setAttributeValue(part, "GG", "");
        }
		if (map.get("DW") != null) {
            XmlUtility.setAttributeValue(part, "DW", map.get("DW").toString());
        } else {
        	XmlUtility.setAttributeValue(part, "DW", "");
        }
		if (map.get("DW2") != null) {
			XmlUtility.setAttributeValue(part, "DW2", map.get("DW2").toString());
		} else {
			XmlUtility.setAttributeValue(part, "DW2", "");
		}
		if (map.get("bzh") != null) {
			XmlUtility.setAttributeValue(part, "bzh", map.get("bzh").toString());
		} else {
			XmlUtility.setAttributeValue(part, "bzh", "");
		}
		if (map.get("dataType") != null) {
			XmlUtility.setAttributeValue(part, "dataType", map.get("dataType").toString());
		} else {
			XmlUtility.setAttributeValue(part, "dataType", "");
		}
		if (map.get("materialNumber") != null) {
			XmlUtility.setAttributeValue(part, "materialNumber", map.get("materialNumber").toString());
		} else {
			XmlUtility.setAttributeValue(part, "materialNumber", "");
		}
		if (map.get("materialName") != null) {
			XmlUtility.setAttributeValue(part, "materialName", map.get("materialName").toString());
		} else {
			XmlUtility.setAttributeValue(part, "materialName", "");
		}
		if (map.get("materialBrand") != null) {
			XmlUtility.setAttributeValue(part, "materialBrand", map.get("materialBrand").toString());
		} else {
			XmlUtility.setAttributeValue(part, "materialBrand", "");
		}
		if (map.get("materialCrision") != null) {
			XmlUtility.setAttributeValue(part, "materialCrision", map.get("materialCrision").toString());
		} else {
			XmlUtility.setAttributeValue(part, "materialCrision", "");
		}
		if (map.get("materialBzh") != null) {
			XmlUtility.setAttributeValue(part, "materialBzh", map.get("materialBzh").toString());
		} else {
			XmlUtility.setAttributeValue(part, "materialBzh", "");
		}
		if (map.get("materialPh") != null) {
			XmlUtility.setAttributeValue(part, "materialPh", map.get("materialPh").toString());
		} else {
			XmlUtility.setAttributeValue(part, "materialPh", "");
		}
		if (map.get("materialLb") != null) {
			XmlUtility.setAttributeValue(part, "materialLb", map.get("materialLb").toString());
		} else {
			XmlUtility.setAttributeValue(part, "materialLb", "");
		}
		if (map.get("materialGg") != null) {
			XmlUtility.setAttributeValue(part, "materialGg", map.get("materialGg").toString());
		} else {
			XmlUtility.setAttributeValue(part, "materialGg", "");
		}
//		if (map.get("HASEPM") != null) {
//			XmlUtility.setAttributeValue(part, "HASEPM", map.get("HASEPM").toString());
//		} else {
//			XmlUtility.setAttributeValue(part, "HASEPM", "");
//		}
		if (map.get("wh") != null) {
			XmlUtility.setAttributeValue(part, "wh", map.get("wh").toString());
		} else {
			XmlUtility.setAttributeValue(part, "wh", "");
		}
		if (map.get("adjustable") != null) {
			XmlUtility.setAttributeValue(part, "adjustable", map.get("adjustable").toString());
		} else {
			XmlUtility.setAttributeValue(part, "adjustable", "");
		}
		if (map.get("replaceableParts") != null) {
			XmlUtility.setAttributeValue(part, "replaceableParts", map.get("replaceableParts").toString());
		} else {
			XmlUtility.setAttributeValue(part, "replaceableParts", "");
		}
		return part;
	}

	public static List<Element> generatePartDatas(Map map, String partType) {
		if ((map == null) || (map.size() == 0))
			return null;
		List<Element> partList = new ArrayList<Element>();
		Element part;
		String occId = map.get("occId").toString();
		if(occId.contains(",")){
			String[] occids = occId.split(",");
			for(String childOccid : occids){
				part = XmlUtility.createPart();
				XmlUtility.setAttributeValue(part, "partNumber", map.get("partNumber").toString());
				XmlUtility.setAttributeValue(part, "partName", map.get("partName").toString());
				if (map.get("oid") != null) {
					XmlUtility.setAttributeValue(part, "oid", map.get("oid").toString());
				} else {
					XmlUtility.setAttributeValue(part, "oid", "");
				}

				if (map.get("occId") != null) {
					XmlUtility.setAttributeValue(part, "occId", childOccid);
				} else {
					XmlUtility.setAttributeValue(part, "occId", "");
				}

				if (map.get("partType") != null) {
					XmlUtility.setAttributeValue(part, "partType", map.get("partType").toString());
				} else if (partType != null)
					XmlUtility.setAttributeValue(part, "partType", partType);
				else {
					XmlUtility.setAttributeValue(part, "partType", "normal");
				}

				if (map.get("useCount") != null) {
					XmlUtility.setAttributeValue(part, "useCount", map.get("useCount").toString());
				} else {
					XmlUtility.setAttributeValue(part, "useCount", "1");
				}

				if (map.get("MTYPE") != null) {
					XmlUtility.setAttributeValue(part, "MTYPE", map.get("MTYPE").toString());
				} else {
					XmlUtility.setAttributeValue(part, "MTYPE", "");
				}

				if (map.get("CSIZE") != null) {
					XmlUtility.setAttributeValue(part, "CSIZE", map.get("CSIZE").toString());
				} else {
					XmlUtility.setAttributeValue(part, "CSIZE", "");
				}

				if (map.get("XHPH") != null) {
					XmlUtility.setAttributeValue(part, "XHPH", map.get("XHPH").toString());
				} else {
					XmlUtility.setAttributeValue(part, "XHPH", "");
				}
				if (map.get("ZCMARK") != null) {
					XmlUtility.setAttributeValue(part, "ZCMARK", map.get("ZCMARK").toString());
				} else {
					XmlUtility.setAttributeValue(part, "ZCMARK", "");
				}
				if (map.get("JSTJ") != null) {
					XmlUtility.setAttributeValue(part, "JSTJ", map.get("JSTJ").toString());
				} else {
					XmlUtility.setAttributeValue(part, "JSTJ", "");
				}
				if (map.get("GG") != null) {
					XmlUtility.setAttributeValue(part, "GG", map.get("GG").toString());
				} else {
					XmlUtility.setAttributeValue(part, "GG", "");
				}
				if (map.get("DW") != null) {
					XmlUtility.setAttributeValue(part, "DW", map.get("DW").toString());
				} else {
					XmlUtility.setAttributeValue(part, "DW", "");
				}
				if (map.get("DW2") != null) {
					XmlUtility.setAttributeValue(part, "DW2", map.get("DW2").toString());
				} else {
					XmlUtility.setAttributeValue(part, "DW2", "");
				}
				if (map.get("bzh") != null) {
					XmlUtility.setAttributeValue(part, "bzh", map.get("bzh").toString());
				} else {
					XmlUtility.setAttributeValue(part, "bzh", "");
				}
				if (map.get("dataType") != null) {
					XmlUtility.setAttributeValue(part, "dataType", map.get("dataType").toString());
				} else {
					XmlUtility.setAttributeValue(part, "dataType", "");
				}
				if (map.get("materialNumber") != null) {
					XmlUtility.setAttributeValue(part, "materialNumber", map.get("materialNumber").toString());
				} else {
					XmlUtility.setAttributeValue(part, "materialNumber", "");
				}
				if (map.get("materialName") != null) {
					XmlUtility.setAttributeValue(part, "materialName", map.get("materialName").toString());
				} else {
					XmlUtility.setAttributeValue(part, "materialName", "");
				}
				if (map.get("materialBrand") != null) {
					XmlUtility.setAttributeValue(part, "materialBrand", map.get("materialBrand").toString());
				} else {
					XmlUtility.setAttributeValue(part, "materialBrand", "");
				}
				if (map.get("materialCrision") != null) {
					XmlUtility.setAttributeValue(part, "materialCrision", map.get("materialCrision").toString());
				} else {
					XmlUtility.setAttributeValue(part, "materialCrision", "");
				}
				if (map.get("materialBzh") != null) {
					XmlUtility.setAttributeValue(part, "materialBzh", map.get("materialBzh").toString());
				} else {
					XmlUtility.setAttributeValue(part, "materialBzh", "");
				}
				if (map.get("materialPh") != null) {
					XmlUtility.setAttributeValue(part, "materialPh", map.get("materialPh").toString());
				} else {
					XmlUtility.setAttributeValue(part, "materialPh", "");
				}
				if (map.get("materialType") != null) {
					XmlUtility.setAttributeValue(part, "materialType", map.get("materialType").toString());
				} else {
					XmlUtility.setAttributeValue(part, "materialType", "");
				}
				if (map.get("materialGg") != null) {
					XmlUtility.setAttributeValue(part, "materialGg", map.get("materialGg").toString());
				} else {
					XmlUtility.setAttributeValue(part, "materialGg", "");
				}
				partList.add(part);
			}
		}else{
			part = XmlUtility.createPart();
			XmlUtility.setAttributeValue(part, "partNumber", map.get("partNumber").toString());
			XmlUtility.setAttributeValue(part, "partName", map.get("partName").toString());
			if (map.get("oid") != null) {
				XmlUtility.setAttributeValue(part, "oid", map.get("oid").toString());
			} else {
				XmlUtility.setAttributeValue(part, "oid", "");
			}

			if (map.get("occId") != null) {
				XmlUtility.setAttributeValue(part, "occId", occId);
			} else {
				XmlUtility.setAttributeValue(part, "occId", "");
			}

			if (map.get("partType") != null) {
				XmlUtility.setAttributeValue(part, "partType", map.get("partType").toString());
			} else if (partType != null)
				XmlUtility.setAttributeValue(part, "partType", partType);
			else {
				XmlUtility.setAttributeValue(part, "partType", "normal");
			}

			if (map.get("useCount") != null) {
				XmlUtility.setAttributeValue(part, "useCount", map.get("useCount").toString());
			} else {
				XmlUtility.setAttributeValue(part, "useCount", "1");
			}

			if (map.get("MTYPE") != null) {
				XmlUtility.setAttributeValue(part, "MTYPE", map.get("MTYPE").toString());
			} else {
				XmlUtility.setAttributeValue(part, "MTYPE", "");
			}

			if (map.get("CSIZE") != null) {
				XmlUtility.setAttributeValue(part, "CSIZE", map.get("CSIZE").toString());
			} else {
				XmlUtility.setAttributeValue(part, "CSIZE", "");
			}

			if (map.get("XHPH") != null) {
				XmlUtility.setAttributeValue(part, "XHPH", map.get("XHPH").toString());
			} else {
				XmlUtility.setAttributeValue(part, "XHPH", "");
			}
			if (map.get("ZCMARK") != null) {
				XmlUtility.setAttributeValue(part, "ZCMARK", map.get("ZCMARK").toString());
			} else {
				XmlUtility.setAttributeValue(part, "ZCMARK", "");
			}
			if (map.get("JSTJ") != null) {
				XmlUtility.setAttributeValue(part, "JSTJ", map.get("JSTJ").toString());
			} else {
				XmlUtility.setAttributeValue(part, "JSTJ", "");
			}
			if (map.get("GG") != null) {
				XmlUtility.setAttributeValue(part, "GG", map.get("GG").toString());
			} else {
				XmlUtility.setAttributeValue(part, "GG", "");
			}
			if (map.get("DW") != null) {
				XmlUtility.setAttributeValue(part, "DW", map.get("DW").toString());
			} else {
				XmlUtility.setAttributeValue(part, "DW", "");
			}
			if (map.get("DW2") != null) {
				XmlUtility.setAttributeValue(part, "DW2", map.get("DW2").toString());
			} else {
				XmlUtility.setAttributeValue(part, "DW2", "");
			}
			if (map.get("bzh") != null) {
				XmlUtility.setAttributeValue(part, "bzh", map.get("bzh").toString());
			} else {
				XmlUtility.setAttributeValue(part, "bzh", "");
			}
			if (map.get("dataType") != null) {
				XmlUtility.setAttributeValue(part, "dataType", map.get("dataType").toString());
			} else {
				XmlUtility.setAttributeValue(part, "dataType", "");
			}
			if (map.get("materialNumber") != null) {
				XmlUtility.setAttributeValue(part, "materialNumber", map.get("materialNumber").toString());
			} else {
				XmlUtility.setAttributeValue(part, "materialNumber", "");
			}
			if (map.get("materialName") != null) {
				XmlUtility.setAttributeValue(part, "materialName", map.get("materialName").toString());
			} else {
				XmlUtility.setAttributeValue(part, "materialName", "");
			}
			if (map.get("materialBrand") != null) {
				XmlUtility.setAttributeValue(part, "materialBrand", map.get("materialBrand").toString());
			} else {
				XmlUtility.setAttributeValue(part, "materialBrand", "");
			}
			if (map.get("materialCrision") != null) {
				XmlUtility.setAttributeValue(part, "materialCrision", map.get("materialCrision").toString());
			} else {
				XmlUtility.setAttributeValue(part, "materialCrision", "");
			}
			if (map.get("materialBzh") != null) {
				XmlUtility.setAttributeValue(part, "materialBzh", map.get("materialBzh").toString());
			} else {
				XmlUtility.setAttributeValue(part, "materialBzh", "");
			}
			if (map.get("materialPh") != null) {
				XmlUtility.setAttributeValue(part, "materialPh", map.get("materialPh").toString());
			} else {
				XmlUtility.setAttributeValue(part, "materialPh", "");
			}
			if (map.get("materialLb") != null) {
				XmlUtility.setAttributeValue(part, "materialLb", map.get("materialLb").toString());
			} else {
				XmlUtility.setAttributeValue(part, "materialLb", "");
			}
			if (map.get("materialGg") != null) {
				XmlUtility.setAttributeValue(part, "materialGg", map.get("materialGg").toString());
			} else {
				XmlUtility.setAttributeValue(part, "materialGg", "");
			}
			partList.add(part);
		}
		return partList;
	}

	public static Vector analyzMiddleChildParts(Map map) {
		Vector result = new Vector();
		if ((map != null) && (map.size() > 0)) {
			Object obj = map.get("subPartNumber");
			if ((obj != null) && ((obj instanceof String))) {
				String partMessage = obj.toString();
				String[] messages = partMessage.split(",");
				for (int i = 0; i < messages.length; i++) {
					result.add(messages[i]);
				}
			}
		}
		return result;
	}

	public static Element findPart(Element mainPart, String partOid) throws Exception {
		Element part = null;
		if ((mainPart != null) && (partOid != null) && (partOid.trim().length() > 0)) {
			String oid = mainPart.attributeValue("oid");
			if ((oid != null) && (oid.trim().length() > 0)) {
				if (oid.equals(partOid)) {
					part = mainPart;
				}
			}
			if (part == null) {
				List list = getChildProducts(mainPart);
				if ((list != null) && (list.size() > 0)) {
					for (int i = 0; i < list.size(); i++) {
						Element childPart = (Element) list.get(i);
						part = findPart(childPart, partOid);
						if (part != null) {
							break;
						}
					}
				}
			}
		}
		return part;
	}

	public static void filterPBOM(Element mainPart, String userOid, HashMap map) {
		if ((mainPart == null) || (userOid == null || userOid.trim().length() == 0) || (map == null)) {
			return;
		}
		String responser = mainPart.attributeValue("responser");
		String partNumber = mainPart.attributeValue("partNumber");
		String oid = mainPart.attributeValue("oid");
//		if ((responser.equalsIgnoreCase(userOid))
//				&& (map.get(responser) == null)) {
			map.put(oid, partNumber);
//		}
		List list = getChildProducts(mainPart);
		if ((list != null) && (list.size() > 0)) {
			for (int i = 0; i < list.size(); i++) {
				Element childPart = (Element) list.get(i);
				filterPBOM(childPart, userOid, map);
			}
		}
	}

	public static void filterPBOM1(Element mainPart, String userOid, HashMap map) {
        if ((mainPart == null) || (userOid == null || userOid.trim().length() == 0) || (map == null)) {
            return;
        }
        cishu1++;
        if (cishu1==2) {
            return;
        }
        String responser = mainPart.attributeValue("responser");
        String partNumber = mainPart.attributeValue("partNumber");
        String oid = mainPart.attributeValue("oid");
//      if ((responser.equalsIgnoreCase(userOid))
//              && (map.get(responser) == null)) {
            map.put(oid, partNumber);
//      }
        List list = getChildProducts(mainPart);
        if ((list != null) && (list.size() > 0)) {
            for (int i = 0; i < list.size(); i++) {
                Element childPart = (Element) list.get(i);
                filterPBOM1(childPart, userOid, map);
            }
        }
    }

	public static void getAllPartOid(Element mainPart,List<String> list) {
		if ((mainPart == null)) {
			return;
		}
		String oid = mainPart.attributeValue("oid");
		list.add(oid);
		List childList = getChildProducts(mainPart);
		if ((childList != null) && (childList.size() > 0)) {
			for (int i = 0; i < childList.size(); i++) {
				Element childPart = (Element) childList.get(i);
				getAllPartOid(childPart, list);
			}
		}
	}

	public static void getTWOPartOid(Element mainPart,List<String> list) {
       cishu++;
       if (cishu==2) {
    	   return;
       }
        if ((mainPart == null)) {
            return;
        }
        String oid = mainPart.attributeValue("oid");
        list.add(oid);
        List childList = getChildProducts(mainPart);
        if ((childList != null) && (childList.size() > 0)) {
            for (int i = 0; i < childList.size(); i++) {
                Element childPart = (Element) childList.get(i);
                getTWOPartOid(childPart, list);
            }
        }
    }

	public static Element getXmlTopPart(Element productElement) {
		if (productElement != null) {
			Element parts = productElement.element("parts");
			if (parts != null) {
				List list = parts.elements();
				if ((list != null) && (!list.isEmpty())) {
					return (Element) list.get(0);
				}
			}
		}
		return null;
	}
	public static Map<String, String> getPartMap(byte[] bytes,String partNumber) throws Exception {
		Map<String, String> partMap = new HashMap<String, String>();
		Document document = getDocument(bytes);
		Element productElement = BomXMLUtil.getProduct(document);
		Element mainPart = BomXMLUtil.getMainPart(productElement);
		List<Element> childElements = getChildProducts(mainPart);
		for(Element element : childElements){
			partMap.put(element.attributeValue("partNumber"), element.attributeValue("gysl"));
		}
		return partMap;
	}
}

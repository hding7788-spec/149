package com.glaway.mpm.pdf;

import com.itextpdf.text.pdf.BaseFont;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PDFUtil {
	public static String getCmatByCLDE(Element element) {
		// TODO Auto-generated method stub
		List list = element.selectNodes("/technics/QMFawTechnicsInfo/CLDE/YCLDE/ycldeRecord");
		StringBuilder cmat = new StringBuilder();
		if(list!=null&&!list.isEmpty()){
			Element e = (Element)list.get(0);
			String chmc = PDFUtil.objectToString(e.attributeValue("chmc"));
			if (!"".equals(chmc)) {
				cmat.append(chmc).append(" ");
			}
			String xhph = PDFUtil.objectToString(e.attributeValue("xhph"));
			if (!"".equals(xhph)) {
				cmat.append(xhph).append(" ");
			}
			String gyztrcl = PDFUtil.objectToString(e.attributeValue("gyztrcl"));
			if (!"".equals(gyztrcl)) {
				cmat.append(gyztrcl).append(" ");
			}
			String gg = PDFUtil.objectToString(e.attributeValue("gg"));
			if (!"".equals(gg)) {
				cmat.append(gg).append(" ");
			}
			String jstj = PDFUtil.objectToString(e.attributeValue("jstj"));
			if (!"".equals(jstj)) {
				cmat.append(jstj).append(" ");
			}

			String xlcc = PDFUtil.objectToString(e.attributeValue("xlcc"));
			if (!"".equals(xlcc)) {
				cmat.append(xlcc).append(" ");
			}

		} else {
			list = element.selectNodes("/technics/QMFawTechnicsInfo/CLDE/SJZYKYCLDE/ycldeRecord");
			if (list != null && !list.isEmpty()) {
				Element e = (Element) list.get(0);
				String chmc = PDFUtil.objectToString(e.attributeValue("name"));
				if (!"".equals(chmc)) {
					cmat.append(chmc).append(" ");
				}
				String xhph = PDFUtil.objectToString(e.attributeValue("ph"));
				if (!"".equals(xhph)) {
					cmat.append(xhph).append(" ");
				}
				String gg = PDFUtil.objectToString(e.attributeValue("gg"));
				if (!"".equals(gg)) {
					cmat.append(gg).append(" ");
				}
				/*
				 * String jstj =
				 * PDFUtil.objectToString(e.attributeValue("jstj"));
				 * if(!"".equals(jstj)){ cmat.append(jstj).append(" "); }
				 */
				String xlcc = PDFUtil.objectToString(e.attributeValue("xlcc"));
				if (!"".equals(xlcc)) {
					cmat.append(xlcc).append(" ");
				}
			}

		}
		return cmat.toString();
	}

	public static String getFormTemplateFolderPath() {
		return "/com/glaway/mpm/pdf/template/";
	}

	public static String objectToString(Object object) {
		if (object == null) {
			return "";
		} else {
			return object.toString();
		}
	}

	public static List<String> getImgStr(String htmlStr) {
		String img = "";
		Pattern p_image;
		Matcher m_image;
		List<String> pics = new ArrayList<String>();

		String regEx_img = "<img.*src=(.*?)[^>]*?>"; // 图片链接地址
		p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
		m_image = p_image.matcher(htmlStr);
		while (m_image.find()) {
			img = img + "," + m_image.group();
			Matcher m = Pattern.compile("src=\'?(.*?)(\'|>|\\s+)").matcher(img); // 匹配src
			while (m.find()) {
				pics.add(m.group(1));
			}
		}
		return pics;
	}

	public static String Html2Text(String inputString) {
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;

		try {
			// 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>}
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";

			// 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>}
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";

			// 定义图片HTML标签的正则表达式
			String regEx_html = "<img src=[^>]+>";

			// 定义HTML标签的正则表达式
			String regEx_html2 = "<[^>]+>";

			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll("♀"); // 过滤html标签

			p_html = Pattern.compile(regEx_html2, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			textStr = htmlStr;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return textStr;// 返回文本字符串
	}

	public static String getPhaseCodeIndex(String phaseCode) {
		String index = "1";
		if (phaseCode.contains("M")) {
			index = "1";
		} else if (phaseCode.contains("C")) {
			index = "2";
		} else if (phaseCode.contains("S") || phaseCode.contains("Z")) {
			index = "3";
		} else if (phaseCode.contains("D")) {
			index = "4";
		} else if (phaseCode.contains("G")) {
			index = "5";
		} else if (phaseCode.contains("P")) {
			index = "6";
		} else if (phaseCode.contains("N") || phaseCode.contains("A")) {
			index = "";
		}
		return index;
	}

	public static String getImgHtmlCode(String imgPath) {
		// return "<img width='20px' height='15px' src='"+imgPath+"'/>";
		return "<img height='15px' src='" + imgPath + "'/>";
	}

	public static int[] getImgWidth(String imgPath) {
//		System.out.println("-----imgPath----" + imgPath);
		FileInputStream fis = null;
		try {
			File picture = new File(imgPath);
			fis = new FileInputStream(picture);
			BufferedImage sourceImg = ImageIO.read(fis);
//			System.out.println("-----Width----" + sourceImg.getWidth());
//			System.out.println("-----Height----" + sourceImg.getHeight());
			int[] img = new int[2];
			img[0] = sourceImg.getWidth();
			img[1] = sourceImg.getHeight();
			return img;
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
		return null;
	}

	public static String getHtmlCode(String value) {
		return "<html><head></head><body>" + value + "</body></html>";
	}

	public static boolean isChina(String str) {
		return CharUtil.isChinese(str);
	}

	/**
	 * 通过读取xml文件中的内容，设计html页面
	 *
	 * @param xmlFileName
	 * @param targetPath
	 * @param fileNameList
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static void createHtmlByXml(String xmlFileName, String targetPath, List<String> fileNameList) throws Exception {
		File file = new File(xmlFileName);
		Map<String, Vector<Vector<String>>> tableValuesMap = new TreeMap<String, Vector<Vector<String>>>();
		Vector<String> mapKeyList = new Vector<String>();
		SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(file);

		Element root = doc.getRootElement();
		// 工艺文件节点
		Element technics = root.element("QMFawTechnicsInfo");
		// steps节点
		Element steps = technics.element("steps");
		// 工序节点
		List<Element> procedureList = steps.elements("QMProcedureInfo");
		getCommonProcedureInfo(procedureList, tableValuesMap, mapKeyList, "_procedure_");
		getSpecialProcedureInfo(procedureList, tableValuesMap, mapKeyList, "_procedure_");
		for (Element procedure : procedureList) {
			String stepNumber = procedure.attributeValue("stepNumber");
			Element paces = procedure.element("paces");
			if (paces != null) {
				// 工步节点
				List<Element> paceList = paces.elements("QMProcedureInfo");
				getCommonProcedureInfo(paceList, tableValuesMap, mapKeyList, "_procedure_" + stepNumber + "_pace_");
				getSpecialProcedureInfo(paceList, tableValuesMap, mapKeyList, "_procedure_" + stepNumber + "_pace_");
			}
		}
		createCommonTableHtmls(mapKeyList, tableValuesMap, targetPath, fileNameList);
		Map<String, Vector<Vector<String>>> newMap = filterMapKeyList(mapKeyList, tableValuesMap);
		createSpecialTableHtmls(mapKeyList, newMap, targetPath, fileNameList);
	}
	public static void createHtmlByXml(Element technics, String targetPath, List<String> fileNameList) throws Exception {
//		File file = new File(xmlFileName);
		Map<String, Vector<Vector<String>>> tableValuesMap = new TreeMap<String, Vector<Vector<String>>>();
		Vector<String> mapKeyList = new Vector<String>();
//		SAXReader saxReader = new SAXReader();
//		Document doc = saxReader.read(file);
		
//		Element root = doc.getRootElement();
		// 工艺文件节点
//		Element technics = root.element("QMFawTechnicsInfo");
		// steps节点
		Element steps = technics.element("steps");
		// 工序节点
		List<Element> procedureList = steps.elements("QMProcedureInfo");
		getCommonProcedureInfo(procedureList, tableValuesMap, mapKeyList, "_procedure_");
		getSpecialProcedureInfo(procedureList, tableValuesMap, mapKeyList, "_procedure_");
		for (Element procedure : procedureList) {
			String stepNumber = procedure.attributeValue("stepNumber");
			Element paces = procedure.element("paces");
			if (paces != null) {
				// 工步节点
				List<Element> paceList = paces.elements("QMProcedureInfo");
				getCommonProcedureInfo(paceList, tableValuesMap, mapKeyList, "_procedure_" + stepNumber + "_pace_");
				getSpecialProcedureInfo(paceList, tableValuesMap, mapKeyList, "_procedure_" + stepNumber + "_pace_");
			}
		}
		createCommonTableHtmls(mapKeyList, tableValuesMap, targetPath, fileNameList);
		Map<String, Vector<Vector<String>>> newMap = filterMapKeyList(mapKeyList, tableValuesMap);
		createSpecialTableHtmls(mapKeyList, newMap, targetPath, fileNameList);
	}

	/**
	 * 获取通用表中的内容
	 *
	 * @param procedureList
	 * @param tableMap
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Vector<Vector<String>>> getCommonProcedureInfo(List<Element> procedureList, Map<String, Vector<Vector<String>>> tableMap, Vector<String> mapKeyList, String type)
			throws IOException {
		Vector<String> columnNameList = null;
		Vector<String> cloumnValueList = null;
		Vector<Vector<String>> columnValues = null;
		String stepNumber = "";
		String eName = "";
		String chinaName = "";
		for (Element procedure : procedureList) {
			columnValues = new Vector<Vector<String>>();
			stepNumber = type + procedure.attributeValue("stepNumber");
			Element commonParamTables = procedure.element("commonParamTables");
			if (commonParamTables != null) {
				Element parameterTable = commonParamTables.element("parameterTable");
				if(parameterTable != null){
					String technicsType = parameterTable.attributeValue("TECHNICSTYPE");
					eName = parameterTable.attributeValue("ENNAME");
					chinaName = parameterTable.attributeValue("CHINANAME");
					if ("通用检查项定义".equals(technicsType)) {
						List<Element> parameterList = parameterTable.elements("parameter");
						for (Element parameter : parameterList) {
							columnNameList = new Vector<String>();
							cloumnValueList = new Vector<String>();
							Element values = parameter.element("values");
							String number = values.attributeValue("number");
							List<Element> valueList = values.elements("value");
							for (Element value : valueList) {
								String isShow = value.attributeValue("isShow");
								if (isShow.equals("true")) {
									String columnName = "";
									if (number.equals("0")) {
										columnName = value.attributeValue("columnName");
										columnNameList.add(columnName);
									}
									Element attribute = value.element("attribute");
									String attributeValue = attribute.getText();
									cloumnValueList.add(attributeValue);
									// System.out.println("columnName:" + columnName
									// + ",attribute:" + attributeValue);
								}
							}
							if (columnNameList.size() > 0) {
								columnValues.add(columnNameList);
							}
							if (cloumnValueList.size() > 0) {
								columnValues.add(cloumnValueList);
							}
							// System.out.println(">>>>>>>>>");
						}
						if (columnValues.size() > 0) {
							tableMap.put(chinaName + "," + eName + stepNumber, columnValues);
							mapKeyList.add(chinaName + "," + eName + stepNumber);
//							System.out.println("commonStepNumber===" + chinaName + "," + eName + stepNumber + "," + columnValues.size());
						}
					}

				}
			}
		}
		return tableMap;
	}

	/**
	 * 获取特殊表中的内容
	 *
	 * @param procedureList
	 * @param tableMap
	 * @param type
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Vector<Vector<String>>> getSpecialProcedureInfo(List<Element> procedureList, Map<String, Vector<Vector<String>>> tableMap, Vector<String> mapKeyList, String type)
			throws IOException {
		Vector<String> columnNameList = null;
		Vector<String> cloumnValueList = null;
		Vector<Vector<String>> columnValues = null;
		String stepNumber = "";
		String technicsType = "";
		String enName = "";
		String chinaName = "";
		for (Element procedure : procedureList) {
			stepNumber = type + procedure.attributeValue("stepNumber");
			Element commonParamTables = procedure.element("specialParamTables");
			if (commonParamTables != null) {
				List<Element> parameterTableList = commonParamTables.elements("parameterTable");
				for (Element parameterTable : parameterTableList) {
					columnValues = new Vector<Vector<String>>();
					technicsType = parameterTable.attributeValue("TECHNICSTYPE");
					enName = parameterTable.attributeValue("ENNAME");
					chinaName = parameterTable.attributeValue("CHINANAME");
					if (!technicsType.equals("通用检查项定义")) {
						List<Element> parameterList = parameterTable.elements("parameter");
						for (Element parameter : parameterList) {
							columnNameList = new Vector<String>();
							cloumnValueList = new Vector<String>();
							Element values = parameter.element("values");
							String number = values.attributeValue("number");
							List<Element> valueList = values.elements("value");
							for (Element value : valueList) {
								String isShow = value.attributeValue("isShow");
								if (isShow.equals("true")) {
									String columnName = "";
									if (number.equals("0")) {
										columnName = value.attributeValue("columnName");
										columnNameList.add(columnName);
									}
									Element attribute = value.element("attribute");
									String attributeValue = attribute.getText();
									cloumnValueList.add(attributeValue);
									// System.out.println("columnName:" +
									// columnName + ",attribute:" +
									// attributeValue);
								}
							}
							if (columnNameList.size() > 0) {
								columnValues.add(columnNameList);
							}
							if (cloumnValueList.size() > 0) {
								columnValues.add(cloumnValueList);
							}
							// System.out.println(">>>>>>>>>");
						}
					}
					if (columnValues.size() > 0) {
						tableMap.put(chinaName + "," + enName + stepNumber, columnValues);
						mapKeyList.add(chinaName + "," + enName + stepNumber);
//						System.out.println("speicalStepNumber===" + chinaName + "," + enName + stepNumber + "," + columnValues.size());
					}
				}
			}
		}
		return tableMap;
	}

	public static void createCommonTableHtmls(Vector<String> mapKeyList, Map<String, Vector<Vector<String>>> tableValuesMap, String targetPath, List<String> fileNameList) throws IOException {
		for (String keyName : mapKeyList) {
			if (keyName.contains("procedure") && !keyName.contains("pace")) {
				String imgName = keyName.substring(keyName.lastIndexOf(",") + 1, keyName.length());
				Vector<Vector<String>> values = tableValuesMap.get(keyName);
				int count = values.size();
				int dataCount = count - 1;
				int pages = dataCount % 20 == 0 ? (dataCount / 20) : (dataCount / 20 + 1);
				StringBuilder sb = null;
				for (int j = 0; j < pages; j++) {
					sb = new StringBuilder();
					sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
					sb.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/><title></title></head><body><table border=\"0\" cellpadding=\"2\" cellspacing=\"1\" bgcolor=\"#000000\">");
					sb.append("<tr>");
					for (String value : values.get(0)) {
						sb.append("<td width=\"90\" style=\"text-align:center;background:#dcdcdc;\">" + value + "</td>");
					}
					sb.append("</tr>");
					int amount = 0;
					if ((j + 1) * 20 >= count) {
						amount = count;
					} else {
						amount = (j + 1) * 20 + 1;
					}
//					System.out.println("count=====>" + count + ",amount====>" + amount);
					for (int i = 1 + j * 20; i < amount; i++) {
						sb.append("<tr>");
						List<String> valuesOfLine = values.get(i);
						for (String value : valuesOfLine) {
							if (value.contains("@#$%^\\")) {
								value = value.replace("@#$%^\\", "");
							}
							if (value.equals("true")) {
								value = "是";
							}
							if (value.equals("false")) {
								value = "";
							}
							sb.append("<td width=\"90\" style=\"text-align:center;\" bgcolor=\"#FFFFFF\">" + value + "</td>");
						}
						sb.append("</tr>");
					}
					sb.append("</table></body></html>");
					String filePath = targetPath + File.separator + j + "_" + imgName + ".html";
					fileNameList.add(j + "_" + imgName);
					writeStringToHtml(sb.toString(), filePath);
				}
			}
		}
	}

	public static void createSpecialTableHtmls(Vector<String> mapKeyList, Map<String, Vector<Vector<String>>> tableValuesMap, String targetPath, List<String> fileNameList) throws IOException {
		for (Map.Entry<String, Vector<Vector<String>>> entry : tableValuesMap.entrySet()) {
			String keyName = entry.getKey();
			String imgName = keyName.substring(keyName.lastIndexOf(",") + 1, keyName.length());
			Vector<Vector<String>> values = entry.getValue();
			// System.out.println("values======>"+values);
			int count = values.size();
			int dataCount = count - 1;
			int pages = dataCount % 20 == 0 ? (dataCount / 20) : (dataCount / 20 + 1);
			StringBuilder sb = null;
			for (int j = 0; j < pages; j++) {
				sb = new StringBuilder();
				sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
				sb.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/><title></title></head><body><table border=\"0\" cellpadding=\"2\" cellspacing=\"1\" bgcolor=\"#000000\">");
				sb.append("<tr>");
				for (String value : values.get(0)) {
					sb.append("<td width=\"90\" style=\"text-align:center;background:#dcdcdc;\">" + value + "</td>");
				}
				sb.append("</tr>");
				int amount = 0;
				if ((j + 1) * 20 >= count) {
					amount = count;
				} else {
					amount = (j + 1) * 20 + 1;
				}
//				System.out.println("count=====>" + count + ",amount====>" + amount);
				for (int i = 1 + j * 20; i < amount; i++) {
					sb.append("<tr>");
					List<String> valuesOfLine = values.get(i);
					for (String value : valuesOfLine) {
						if (value.contains("@#$%^\\")) {
							value = value.replace("@#$%^\\", "");
						}
						if (value.equals("true")) {
							value = "是";
						}
						if (value.equals("false")) {
							value = "";
						}
						sb.append("<td width=\"90\" style=\"text-align:center;\" bgcolor=\"#FFFFFF\">" + value + "</td>");
					}
					sb.append("</tr>");
				}
				sb.append("</table></body></html>");
				String filePath = targetPath + File.separator + j + "_" + imgName + ".html";
				fileNameList.add(j + "_" + imgName);
				writeStringToHtml(sb.toString(), filePath);
			}
		}
	}

	public static Map<String, Vector<Vector<String>>> filterMapKeyList(Vector<String> mapKeyList, Map<String, Vector<Vector<String>>> tableValuesMap) {
		Vector<String> newList = new Vector<String>();
		for (String key : mapKeyList) {
			if (key.contains("procedure") && key.contains("pace")) {
				String newKey = key.substring(0, key.lastIndexOf("_"));
				newList.add(newKey);
			}
		}
		Map<String, Vector<Vector<String>>> newMap = new HashMap<String, Vector<Vector<String>>>();
		Vector<String> newVector = null;
		Vector<Vector<String>> newValues = null;
		for (String newkey : newList) {
			newValues = new Vector<Vector<String>>();
			int valuesCount = 0;
			for (Map.Entry<String, Vector<Vector<String>>> entry : tableValuesMap.entrySet()) {
				String mapKey = entry.getKey();
				if (mapKey.contains(newkey)) {
					String[] strs = mapKey.split("_");
					String paceNumber = strs[4];
					Vector<Vector<String>> values = entry.getValue();
					if (valuesCount == 0) {
						for (int i = 0; i < values.size(); i++) {
							Vector<String> vector = values.get(i);
							newVector = new Vector<String>();
							if (i == 0) {
								newVector.add("工步号");
							} else {
								newVector.add(paceNumber);
							}
							for (int j = 0; j < vector.size(); j++) {
								String value = vector.get(j);
								newVector.add(value);
							}
							newValues.add(newVector);
						}
						valuesCount++;
					} else {
						for (int i = 1; i < values.size(); i++) {
							Vector<String> vector = values.get(i);
							newVector = new Vector<String>();
							newVector.add(paceNumber);
							for (int j = 0; j < vector.size(); j++) {
								String value = vector.get(j);
								newVector.add(value);
							}
							newValues.add(newVector);
						}
						valuesCount++;
					}
				}
				newMap.put(newkey, newValues);
			}
		}
		return newMap;
	}

	public static File writeStringToHtml(String htmlStr, String fileName) throws IOException {
		File file = new File(fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		osw.write(htmlStr);
		osw.flush();
		return file;
	}

	public static List<String> getNameList(Element technicElement) {
		List<String> nameList = new ArrayList<String>();
		// steps节点
		Element steps = technicElement.element("steps");
		// 工序节点
		List<Element> procedureList = steps.elements("QMProcedureInfo");
		String procedureStepNumber = "";
		for (Element procedure : procedureList) {
			procedureStepNumber = procedure.attributeValue("stepNumber"); // 工序号
			Element procedureCommonParamTables = procedure.element("commonParamTables");
			if (procedureCommonParamTables != null) {
				// 该工序存在通用记录表，add
				Element procedureParameterTable = procedureCommonParamTables.element("parameterTable");
				String procedureTechnicsType = procedureParameterTable.attributeValue("TECHNICSTYPE");
				String procedureCommonEname = procedureParameterTable.attributeValue("ENNAME");
				String procedureCommonCname = procedureParameterTable.attributeValue("CHINANAME");
				if (procedureTechnicsType.equals("通用检查项定义")) {
					nameList.add(procedureCommonCname + "," + procedureCommonEname + "_procedure_" + procedureStepNumber);
				}
			}
			// 特殊记录表
			Element procedureSpecialParamTables = procedure.element("specialParamTables");
			List<Element> spaceList = null;
			String procedureSpecialEname = "";
			if (procedureSpecialParamTables != null) {
				List<Element> procedureSpecialParameterTableList = procedureSpecialParamTables.elements("parameterTable");
				for (Element procedureSpecialParameterTable : procedureSpecialParameterTableList) {
					String procedureSpecialTechnicsType = procedureSpecialParameterTable.attributeValue("TECHNICSTYPE");
					procedureSpecialEname = procedureSpecialParameterTable.attributeValue("ENNAME");
					String procedureSpecialCname = procedureSpecialParameterTable.attributeValue("CHINANAME");
					if (!procedureSpecialTechnicsType.equals("通用检查项定义")) {
						// 加入工序上的特殊表
						nameList.add(procedureSpecialCname + "," + procedureSpecialEname + "_procedure_" + procedureStepNumber);
						Element spePaces = procedure.element("paces");
						if (spePaces != null) {
							// 工步节点
							spaceList = spePaces.elements("QMProcedureInfo");
						}
					}
				}
			}
			Element paces = procedure.element("paces");
			if (paces != null) {
				// 工步节点
				List<Element> paceList = paces.elements("QMProcedureInfo");
				for (Element pace : paceList) {
					Element paceCommonParamTables = pace.element("commonParamTables");
					if (paceCommonParamTables != null) {
						Element paceParameterTable = paceCommonParamTables.element("parameterTable");
						String paceTechnicsType = paceParameterTable.attributeValue("TECHNICSTYPE");
						String paceCommonName = paceParameterTable.attributeValue("ENNAME");
						String paceCommonCname = paceParameterTable.attributeValue("CHINANAME");
						if (paceTechnicsType.equals("通用检查项定义")) {
							nameList.add(paceCommonCname + "," + paceCommonName + "_procedure_" + procedureStepNumber + "_pace");
						}
					}
				}
			}
			if (spaceList != null) {
				for (Element pace : spaceList) {
					Element paceSpecialParamTables = pace.element("specialParamTables");
					if (paceSpecialParamTables != null) {
						Element paceSpecialParameterTable = paceSpecialParamTables.element("parameterTable");
						if (paceSpecialParameterTable != null) {
							String paceSpecialTechnicsType = paceSpecialParameterTable.attributeValue("TECHNICSTYPE");
							String paceSpecialEname = paceSpecialParameterTable.attributeValue("ENNAME");
							String paceSpecialCname = paceSpecialParameterTable.attributeValue("CHINANAME");
							if (!paceSpecialTechnicsType.equals("通用检查项定义")) {
								// 加入工步上同名的特殊表
								if (paceSpecialEname.equals(procedureSpecialEname)) {
									nameList.add(paceSpecialCname + "," + paceSpecialEname + "_procedure_" + procedureStepNumber + "_pace");
								}
							}
						}
					}
				}
			}
			Element extraPaces = procedure.element("paces");
			if (extraPaces != null) {
				// 工步节点
				List<Element> paceList = extraPaces.elements("QMProcedureInfo");
				for (Element pace : paceList) {
					Element paceSpecialParamTables = pace.element("specialParamTables");
					if (paceSpecialParamTables != null) {
						Element paceSpecialParameterTable = paceSpecialParamTables.element("parameterTable");
						if (paceSpecialParameterTable != null) {
							String paceSpecialTechnicsType = paceSpecialParameterTable.attributeValue("TECHNICSTYPE");
							String paceSpecialEname = paceSpecialParameterTable.attributeValue("ENNAME");
							String paceSpecialCname = paceSpecialParameterTable.attributeValue("CHINANAME");
							if (!paceSpecialTechnicsType.equals("通用检查项定义")) {
								// 加入工步上同名的特殊表
								if (!nameList.contains(paceSpecialEname)) {
									nameList.add(paceSpecialCname + "," + paceSpecialEname + "_procedure_" + procedureStepNumber + "_pace");
								}
							}
						}
					}
				}
			}
		}
		return nameList;
	}

	public static String getKZD(Element paceElement) {
		List<String> strs = new ArrayList<String>();
		String isKZD = PDFUtil.objectToString(paceElement.attributeValue("isKey")); // 控制点
		if ("true".equals(isKZD)) {
			strs.add("(控制点)");
		}
		String isGJJYD = PDFUtil.objectToString(paceElement.attributeValue("isGJJYD"));// 关键检验点
		if ("true".equals(isGJJYD)) {
			strs.add("(关键检验点)");
		}
		String isQZJYD = PDFUtil.objectToString(paceElement.attributeValue("isQZJYD"));// 强制检验点
		if ("true".equals(isQZJYD)) {
			strs.add("(强制检验点)");
		}
		String isGYGJTX = PDFUtil.objectToString(paceElement.attributeValue("isGYGJTX"));// 工艺关键特性
		if ("true".equals(isGYGJTX)) {
			strs.add("(工艺关键特性)");
		}
		String isGCGJTX = PDFUtil.objectToString(paceElement.attributeValue("isGCGJTX"));// 过程关键特性
		if ("true".equals(isGCGJTX)) {
			strs.add("(过程关键特性)");
		}
		String isDYWKZD = PDFUtil.objectToString(paceElement.attributeValue("isDYWKZD"));// 多余物控制点
		if ("true".equals(isDYWKZD)) {
			strs.add("(多余物控制点)");
		}
		String isGYGJJYD = PDFUtil.objectToString(paceElement.attributeValue("isGYGJJYD"));// 工艺关键检验点
		if ("true".equals(isGYGJJYD)) {
			strs.add("(工艺关键检验点)");
		}
		String isShuangGang = PDFUtil.objectToString(paceElement.attributeValue("isShuangGang"));// 双岗
		if ("true".equals(isShuangGang)) {
			strs.add("(双岗)");
		}
		String kzd = "";
		if (strs.size() > 0) {
			for (int i = 0; i < strs.size(); i++) {
				kzd = kzd + strs.get(i);
			}
		}
		return kzd;
	}

	public static final String[] speWords = new String[] {"β", "γ", "δ", "ε", "ζ", "η", "θ", "ι", "κ", "λ", "μ", "ν", "ξ", "Δ", "Γ", "Β", "Α", "ω", "ψ", "χ", "φ", "υ", "τ", "σ",
		"ρ", "π", "ο", "Ε", "Ζ", "Η", "Θ", "Ι", "Κ", "Λ", "Μ", "Ν", "Ξ", "Ο", "Π", "Ρ", "Σ", "Τ", "Υ", "Φ", "Χ", "Ψ", "Ω", "α"};
	public static Object[] getWholeStr(String htmlStr) {
		Object[] result = new Object[3];
		List<String> values = new ArrayList<String>();
		List<Float> lengths = new ArrayList<Float>();
		result[0] = values;
		result[1] = lengths;
        String speWordStr = "";
        String speWordStr2 = "";
        //拼接特殊符号，如直径等符号
        for (String speWord : speWords) {
        	speWordStr += speWord;
        	speWordStr2 += "|" + speWord;
        }
        //提取特殊符号中字母或指定特殊符号或数字开始，特殊符号，图片，字母或数字结尾的字符串
        String regEx = "(([a-zA-Z0-9" + speWordStr + "])+[" + speWordStr + "[^\u2E80-\u9FFF|\\(|\\)|\\（|\\）|，|,|~|/|。|\\s|\\♣|♀|;|；|、|♣|❤|♠|♥|♂]a-zA-Z0-9" + speWordStr + "]+)" + speWordStr2 +"+";
        Pattern pattern = Pattern.compile(regEx);
        Matcher match = pattern.matcher(htmlStr);
        while(match.find()) {
        	String fullPath = match.group().trim();
//        	System.out.println(fullPath);
        	//System.out.println(0.58*fullPath.length());
        	values.add(fullPath);
        	//lengths.add(0.58f*fullPath.length());
        }

        htmlStr = match.replaceAll("♦");
        result[2] = htmlStr;
        return result;
	}
	public static void main(String[] args) {
		String s = "abc我的,10♂ AASD/FZZB/ASD；AASDFZ/ZBASD；AASDFZZBASD；AASDFZZBASD；AASDFZZBASD；AASDFZZBASD； as asdfasd 我ABC What ARE YOU DOING，How Do you Do!Windchill 使用Java 构建而成。Java 不仅是一种功能强大的编程语言强大的编程语言我的啊Rz/TGWTN2715-GRZ11";
		getWholeStr(s);
		 try {
	        	BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
	        	float  f = baseFont.getWidthPoint("<", 12f);
	        	System.out.println(f);
	        	f =baseFont.getWidthPoint(">", 12f);
	        	System.out.println(f);
	        	f =baseFont.getWidthPoint("A", 12f);
	        	System.out.println(f);
	        	f =baseFont.getWidthPoint("B", 12f);
	        	System.out.println(f);
	        	f =baseFont.getWidthPoint("C", 12f);
	        	System.out.println(f);
	        	f =baseFont.getWidthPoint("中", 12f);
	        	System.out.println(f);
	        	f =baseFont.getWidthPoint("我", 12f);
	        	System.out.println(f);
	        } catch (IOException e) {
				e.printStackTrace();
			} catch (com.itextpdf.text.DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}

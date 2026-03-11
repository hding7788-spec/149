package com.glaway.mpm.pdf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.glaway.mpm.model.TechnicsOutputFormBean;
import com.glaway.mpm.release.ProcessInfoReleaseController;
import com.glaway.mpm.sop.util.SopXMLUtility;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.visual.log.VaLogger;

public class GenTechnicsPdfProcessor {
	private static VaLogger logger = VaLogger.getLogger(GenTechnicsPdfProcessor.class);
	private static String techFloder = null;
	private static List<Map<String, List<String>>> all = null;
	private static int amount = 10;

	public static int start(List<TechnicsOutputFormBean> list, String filePath, boolean isOpenPdf, List<Map<String, String>> partList,
			Map<String, String> params, String TechNo) throws Exception {
		LcmPdfPrinter printer = new LcmPdfPrinter();
		all = new ArrayList<Map<String, List<String>>>();
		String techType = "";
		int pages;
		File xmlfile = new File(filePath + "//" + TechNo + ".xml");
		SAXReader reader = new SAXReader();
		Document document = reader.read(xmlfile);
		Element ele = document.getRootElement();
		Element techElement = ele.element("QMFawTechnicsInfo");
		List<Element> list2 = ele.selectNodes("QMFawTechnicsInfo/steps/QMProcedureInfo");
		// 工艺线路图不超过30道工序打印 add by zhuhao 2017.7.7
		int JpgPage = list2.size();
		int jpgPages = list2.size() / 30;
		if (jpgPages < 1) {
			jpgPages = 1;
		}
		if (list2.size() > 30 && list2.size() % 30 != 0) {
			jpgPages += 1;
		}

		File imgs = new File(filePath + "//" + "technics_route_0.jpg");

		for (TechnicsOutputFormBean formBean : list) {
			String formId = formBean.getFormId();
			String name = formBean.getName();
			techType = formBean.getTechForm();
			logger.info("Form Builder techType:" + techType);
			logger.info("Form Builder name:" + name);
			logger.info("Form Builder formId:" + formId);
			String className = getBuildPdfClassName(formId);
			logger.info("Form Builder class:" + className);
			Class<?> cls = Class.forName(className);
			boolean iscls = PDFBuilder.class.isAssignableFrom(cls);
			if (!iscls) {
				throw new Exception("类型[" + cls + "]不是-" + PDFBuilder.class.getName() + "的子类");
			}
			if ("工艺流程框图".equals(name) && imgs.exists()) {
				for (int i = 0; i < jpgPages; i++) {
					String page = String.valueOf(i);
					if (JpgPage <= 30) {
						page = "";
					}
					Constructor constructor = cls.getConstructor(Element.class, String.class, String.class, String.class);
					PDFBuilder builder = (PDFBuilder) constructor.newInstance(techElement, filePath, name, String.valueOf(page));
					techFloder = builder.getTechFloder();
					builder.buildPDF(printer, partList, params);
					if (!builder.getTemplateList().isEmpty()) {
						Map<String, List<String>> map = new HashMap<String, List<String>>();
						map.put(name, builder.getTemplateList());
						all.add(map);
					}
				}
			} else {
				Constructor constructor = cls.getConstructor(Element.class, String.class, String.class);
				PDFBuilder builder = (PDFBuilder) constructor.newInstance(techElement, filePath, name);
				techFloder = builder.getTechFloder();
				builder.buildPDF(printer, partList, params);
				if (!builder.getTemplateList().isEmpty()) {
					Map<String, List<String>> map = new HashMap<String, List<String>>();
					map.put(name, builder.getTemplateList());
					all.add(map);
				}
				// end
			}
		}
		if ("外协".equals(techType)) {
			pages = setCatalog2(printer, all, filePath);
		} else {
			pages = setCatalog(printer, all, techElement);
		}

		printer.print(techFloder + File.separator + "PDFPreview.pdf");
		if (isOpenPdf) {
			openFile();
		}
		return pages;
	}

	public static int start(List<TechnicsOutputFormBean> list, Element techElement, String filePath, boolean isOpenPdf, List<Map<String, String>> partList,
			Map<String, String> params, String TechNo) throws Exception {
		LcmPdfPrinter printer = new LcmPdfPrinter();
		all = new ArrayList<Map<String, List<String>>>();
		String techType = "";
		int pages;
		/*
		 * File xmlfile = new File(filePath+"//"+TechNo+".xml"); SAXReader
		 * reader = new SAXReader(); Document document = reader.read(xmlfile);
		 * Element ele = document.getRootElement(); Element techElement =
		 * ele.element("QMFawTechnicsInfo");
		 */
		List<Element> list2 = techElement.selectNodes("steps/QMProcedureInfo");
		// 工艺线路图不超过30道工序打印 add by zhuhao 2017.7.7
		int JpgPage = list2.size();
		int jpgPages = list2.size() / 30;
		if (jpgPages < 1) {
			jpgPages = 1;
		}
		if (list2.size() > 30 && list2.size() % 30 != 0) {
			jpgPages += 1;
		}

		File imgs = new File(filePath + "//" + "technics_route_0.jpg");

		for (TechnicsOutputFormBean formBean : list) {
			String formId = formBean.getFormId();
			String name = formBean.getName();
			techType = formBean.getTechForm();
			// logger.info("Form Builder techType:" + techType);
			// logger.info("Form Builder name:" + name);
			// logger.info("Form Builder formId:" + formId);
			String className = getBuildPdfClassName(formId);
			// logger.info("Form Builder class:" + className);
			Class<?> cls = Class.forName(className);
			boolean iscls = PDFBuilder.class.isAssignableFrom(cls);
			if (!iscls) {
				throw new Exception("类型[" + cls + "]不是-" + PDFBuilder.class.getName() + "的子类");
			}
			if ("工艺流程框图".equals(name) && imgs.exists()) {
				for (int i = 0; i < jpgPages; i++) {
					String page = String.valueOf(i);
					if (JpgPage <= 30) {
						page = "";
					}
					Constructor constructor = cls.getConstructor(Element.class, String.class, String.class, String.class);
					PDFBuilder builder = (PDFBuilder) constructor.newInstance(techElement, filePath, name, String.valueOf(page));
					techFloder = builder.getTechFloder();
					builder.buildPDF(printer, partList, params);
					if (!builder.getTemplateList().isEmpty()) {
						Map<String, List<String>> map = new HashMap<String, List<String>>();
						map.put(name, builder.getTemplateList());
						all.add(map);
					}
				}
			} else {
				Constructor constructor = cls.getConstructor(Element.class, String.class, String.class);
				PDFBuilder builder = (PDFBuilder) constructor.newInstance(techElement, filePath, name);
				techFloder = builder.getTechFloder();
				builder.buildPDF(printer, partList, params);
				if (!builder.getTemplateList().isEmpty()) {
					Map<String, List<String>> map = new HashMap<String, List<String>>();
					map.put(name, builder.getTemplateList());
					all.add(map);
				}
				// end
			}
		}
		if ("外协".equals(techType)) {
			pages = setCatalog2(printer, all, filePath);
		} else {
			pages = setCatalog(printer, all, techElement);
		}

		printer.print(techFloder + File.separator + "PDFPreview.pdf");
		if (isOpenPdf) {
			openFile();
		}
		return pages;
	}

	public static int startSOP(List<TechnicsOutputFormBean> list, Element techElement, String filePath, boolean isOpenPdf, List<Map<String, String>> partList,
			Map<String, String> params, String TechNo) throws Exception {
		LcmPdfPrinter printer = new LcmPdfPrinter();
		all = new ArrayList<Map<String, List<String>>>();
		String techType = "";
		int pages;
		List<Element> list2 = techElement.selectNodes("steps/QMProcedureInfo");
		// 工序不超过36
		int JpgPage = list2.size();
		int jpgPages = JpgPage / 36;
		if (jpgPages < 1) {
			jpgPages = 1;
		}
		if (list2.size() > 36 && list2.size() % 36 != 0) {
			jpgPages += 1;
		}
		// File imgs = new File(filePath + "//" + "technics_route_0.jpg");
		for (TechnicsOutputFormBean formBean : list) {
			String formId = formBean.getFormId();
			String name = formBean.getName();
			techType = formBean.getTechForm();
			String className = getBuildPdfClassName(formId);
			Class<?> cls = Class.forName(className);
			boolean iscls = PDFBuilder.class.isAssignableFrom(cls);
			if (!iscls) {
				throw new Exception("类型[" + cls + "]不是-" + PDFBuilder.class.getName() + "的子类");
			}
			Constructor constructor = cls.getConstructor(Element.class, String.class, String.class);
			PDFBuilder builder = (PDFBuilder) constructor.newInstance(techElement, filePath, name);
			techFloder = builder.getTechFloder();
			builder.buildPDF(printer, partList, params);
			if (!builder.getTemplateList().isEmpty()) {
				Map<String, List<String>> map = new HashMap<String, List<String>>();
				map.put(name, builder.getTemplateList());
				all.add(map);
			}
		}
		pages = setSopCatalog(printer, all, techElement);
		printer.print(techFloder + File.separator + "PDFPreview.pdf");
		if (isOpenPdf) {
			openFile();
		}
		return pages;
	}

	private static int countItem(List<Map<String, List<String>>> all, String filePath) {
		int n = 0;

		// 前4个页面是1个封面加3个目录页面，所以从第5个开始计算
		for (Map<String, List<String>> map : all) {
			if (map.get("工艺附表") != null || map.get("工艺附图") != null || map.get("工艺文件封面") != null || map.get("工艺文件目录") != null) {
				continue;
			} else {
				n++;
			}
		}
		// 计算典型/通用工艺条目数量
		Element techElement = XmlUtility.getTechnicsElement(filePath);
		List<Element> elements = XmlUtility.getBorrowTechnics(techElement);
		if (elements != null && !elements.isEmpty()) {
			for (Element element : elements) {
				String name = element.attributeValue("technicsName");
				n = n + calculateCount(name, amount);
			}
		}

		// 如果是主制工艺，则计算辅制工艺数据数量
		String zfFlag = techElement.attributeValue("ZFFLAG");
		if ("Z".equals(zfFlag)) {
			List<Element> felements = XmlUtility.getFZTechnics(techElement);
			if (felements != null && !felements.isEmpty()) {
				for (Element element : felements) {
					String name = element.attributeValue("name");
					n = n + calculateCount(name, amount);
				}
			}
		}

		return n;
	}

	private static int countItem(List<Map<String, List<String>>> all, Element techElement) {
		int n = 0;

		// 前4个页面是1个封面加3个目录页面，所以从第5个开始计算
		for (Map<String, List<String>> map : all) {
			if (map.get("工艺附表") != null || map.get("工艺附图") != null || map.get("工艺文件封面") != null || map.get("工艺文件目录") != null) {
				continue;
			} else {
				n++;
			}
		}
		// 计算典型/通用工艺条目数量
		// Element techElement = XmlUtility.getTechnicsElement(filePath);
		List<Element> elements = XmlUtility.getBorrowTechnics(techElement);
		if (elements != null && !elements.isEmpty()) {
			for (Element element : elements) {
				String name = element.attributeValue("technicsName");
				n = n + calculateCount(name, amount);
			}
		}

		// 如果是主制工艺，则计算辅制工艺数据数量
		String zfFlag = techElement.attributeValue("ZFFLAG");
		if ("Z".equals(zfFlag)) {
			List<Element> felements = XmlUtility.getFZTechnics(techElement);
			if (felements != null && !felements.isEmpty()) {
				for (Element element : felements) {
					String name = element.attributeValue("name");
					n = n + calculateCount(name, amount);
				}
			}
		}

		// 在工艺文件目录中写入工序、工步引用的SOP文件    不作区分 去重
		Map<String, String> sopMaps = new HashMap<String, String>();
		List<Element> allSteps = XmlUtility.getAllSteps(techElement);
		if(allSteps != null){
			for (Element step : allSteps) {
				List<Element> sopTechs = SopXMLUtility.getRelatedSopTechs(step);
				if(sopTechs.size()>0){
					Element stepSop = sopTechs.get(0);
					String number = stepSop.attributeValue("ppnumber");
					String name = stepSop.attributeValue("name");
					int indexOf = name.indexOf("(");
					if(indexOf>-1){
						name = name.substring(0,indexOf);
					}
					sopMaps.put(number, name);
				}
				List<Element> allPaces = XmlUtility.getAllPaces(step);
				for (Element pace : allPaces) {
					List<Element> sopTechs2 = SopXMLUtility.getRelatedSopTechs(pace);
					if(sopTechs2.size()>0){
						Element paceSOP = sopTechs2.get(0);
						String number = paceSOP.attributeValue("ppnumber");
						String name = paceSOP.attributeValue("name");
						int indexOf = name.indexOf("(");
						if(indexOf>-1){
							name = name.substring(0,indexOf);
						}
						sopMaps.put(number, name);
					}
				}
			}
		}
		for (Entry<String, String> entry : sopMaps.entrySet()) {
			String valueStr = entry.getValue();
			char[] clArr = valueStr.toCharArray();
			// n用于计算valueStr总共需要几行来写入数据
			int i = clArr.length / 10;
			n = n + i;
			// 如果不能整除，则表示还需要多一行来写入余下的数据
			if (clArr.length % 10 != 0) {
				n = n + 1;
			}
		}
		return n;
	}

	private static int countPages(List<Map<String, List<String>>> all, String name) {
		int page = 0;
		for (int i = 0; i < all.size(); i++) {
			Map<String, List<String>> map = all.get(i);
			for (String key : map.keySet()) {
				if (name.equals(key)) {
					page = map.get(key).size();
				}
			}

		}
		return page;

	}

	public static int setCatalog(LcmPdfPrinter printer, List<Map<String, List<String>>> all, String filePath) {
		Element techElement = XmlUtility.getTechnicsElement(filePath);

		// 获取PDF总页数
		int pages = 0;
		List<String> fileList = null;
		for (Map<String, List<String>> map : all) {
			pages = pages + map.get(map.keySet().toArray()[0]).size();
			if (map.get("工艺文件目录") != null) {
				fileList = map.get("工艺文件目录");
			}
		}
		// 记录页码
		int count = 3;
		if (techElement.attributeValue("technicsType") != null && !techElement.attributeValue("technicsType").equals("工装工艺")
				&& !"XWReportTechnicsInfo".equals(techElement.getName())) {
			// 计算目录的总条目数量

			// int allCount = countItem(all,techElement);
			int allCount = countItem(all, filePath);
			if (allCount < 17) {
				pages = pages - 2;
				count = 3;
			} else if (allCount > 16 && allCount < 33) {
				pages = pages - 1;
				count = 4;
			} else if (allCount > 48) {
				int addPagesCounts = allCount / 16 - 2;
				for (int i = 0; i < addPagesCounts; i++) {
					String fileName = "工艺文件目录" + (i + 3);
					fileList.add(fileName);
					String templeName;
					if (techElement.attributeValue("technicsType").contains("英文")) {
						templeName = PDFUtil.getFormTemplateFolderPath() + "Form40.pdf";
					} else {
						templeName = PDFUtil.getFormTemplateFolderPath() + "Form3.pdf";
					}
					printer.addTempl(fileName, templeName);
					Map<String, String> templates = printer.getTemplate();
					Map<String, String> newTemplate = addToTemplate(templates, fileName, templeName);
					printer.setTemplate(newTemplate);
				}
				pages = pages + addPagesCounts;
				count = 5 + addPagesCounts;
			} else {
				count = 5;
			}

		} else {
			count = 2;
		}

		String template = "";
		int index = 0;// 记录工艺文件目录页面写到第几行
		int hangshu = 1;

		for (int i = 0; i < all.size(); i++) {
			Map<String, List<String>> map = all.get(i);
			String templateName = (String) map.keySet().toArray()[0];

			// 封面，最后处理
			if (i == 0) {
				printer.addText(templateName, "页码总数", "共" + pages + "页");
			}
			// 目录页面
			else if (i == 1) {
				if (techElement.attributeValue("technicsType") != null && !techElement.attributeValue("technicsType").equals("工装工艺")
						&& !"XWReportTechnicsInfo".equals(techElement.getName())) {
					template = templateName;
					if (techElement.attributeValue("technicsType").contains("英文")) {
						printer.addText(templateName, "页码_1", "共" + pages + "页");
						printer.addText(templateName, "页码_2", "第2页");
						printer.addText(templateName, "页码_3", "第2页");
					} else {
						printer.addText(templateName, "页码_1", "共" + pages + "页");
						printer.addText(templateName, "页码_2", "第2页");
						printer.addText(templateName, "页码_3", "第2页");
					}
				} else {
					List<String> tempList = map.get(templateName);
					for (int j = 0; j < tempList.size(); j++) {
						String pageTemplateName = tempList.get(j);
						printer.addText(pageTemplateName, "页码_1", "共" + pages + "页");
						printer.addText(pageTemplateName, "页码_2", "第" + count + "页");
						printer.addText(pageTemplateName, "页码_3", "第" + count + "页");

						count++;
					}
				}
			} else {
				// 在目录页面记录当前一行数据
				if (!templateName.equals("工艺附图") && !templateName.equals("工艺附表")) {
					printer.addText(template, "序号_" + (hangshu), String.valueOf(hangshu));
					printer.addText(template, "工艺文件编号_" + (hangshu), PDFUtil.objectToString(techElement.attributeValue("pplanNumber")));
					printer.addText(template, "工艺文件名称_" + (hangshu), templateName);
					printer.addText(template, "代号_" + (hangshu), PDFUtil.objectToString(techElement.attributeValue("CINDEX")));
					printer.addText(template, "名称_" + (hangshu), PDFUtil.objectToString(techElement.attributeValue("partName")));
					printer.addText(template, "页数_" + (hangshu), countPages(all, templateName) + "");
					printer.addText(template, "责任部门_" + (hangshu), PDFUtil.objectToString(techElement.attributeValue("DEPT")));
					printer.addText(template, "备注_" + (hangshu), "");
					hangshu++;
					index++;
					// 记录工艺文件目录页面写到第几行
				}
				// 遍历当前模板的所有页面，写入页码
				List<String> tempList = map.get(templateName);
				for (int j = 0; j < tempList.size(); j++) {
					String pageTemplateName = tempList.get(j);
					printer.addText(pageTemplateName, "页码_1", "共" + pages + "页");
					printer.addText(pageTemplateName, "页码_2", "第" + count + "页");
					printer.addText(pageTemplateName, "页码_3", "第" + count + "页");

					count++;
				}
			}
		}

		int i = 0;// 记录目录模板的页数
		// 如果是主工艺文件，则在工艺文件目录最后要写入辅工艺文件的编号和名称
		String zfFlag = techElement.attributeValue("ZFFLAG");
		int xuhao = hangshu;
		if ("Z".equals(zfFlag)) {
			List<Element> felements = XmlUtility.getFZTechnics(techElement);
			index += 1;
			if (felements != null) {
				for (Element ee : felements) {
					// index++;
					// 如果已经写到最后一行，则需要新增一页继续写
					if (index > 16) {
						hangshu = 1;
						index = 1;
						i++;
						Map<String, List<String>> muluMap = all.get(1);
						// System.out.println("----------muluMap----"+muluMap);
						template = ((List<String>) muluMap.values().toArray()[0]).get(i);
						// System.out.println("----------template----"+template);
						// if(i == 1) {
						printer.addText(template, "页码_1", "共" + pages + "页");
						printer.addText(template, "页码_2", "第" + (i + 2) + "页");
						printer.addText(template, "页码_3", "第" + (i + 2) + "页");
						// }
						// if(i == 2) {
						// printer.addText(template, "页码_1", "共"+pages+"页");
						// printer.addText(template, "页码_2", "第4页");
						// printer.addText(template, "页码_3", "第4页");
						// }

						// if(i>2) {
						// break;
						// }
					}
					String name = ee.attributeValue("name");
					String number = ee.attributeValue("number");
					String pagesValue = ee.attributeValue("pages");
					String department = ee.attributeValue("department");
					printer.addText(template, "序号_" + hangshu, String.valueOf(xuhao));
					printer.addText(template, "工艺文件编号_" + hangshu, number);
					// printer.addText(template, "工艺文件名称_"+hangshu, name);
					printer.addText(template, "代号_" + hangshu, PDFUtil.objectToString(techElement.attributeValue("CINDEX")));
					printer.addText(template, "名称_" + hangshu, PDFUtil.objectToString(techElement.attributeValue("partName")));
					printer.addText(template, "页数_" + hangshu, pagesValue);
					printer.addText(template, "责任部门_" + hangshu, department);
					Map<String, Object> map = writeProcess(name, amount, printer, "工艺文件名称_", hangshu, index, template, i, pages);
					xuhao++;
					hangshu = (Integer) map.get("hangshu");
					index = (Integer) map.get("index");
					i = (Integer) map.get("page");
					template = (String) map.get("templateName");
				}
			}
		} else if ("F".equals(zfFlag)) {
			index += 1;
		}

		// 在工艺文件目录中写入"典型工业"和"通用工艺"的编号及名称条目
		List<Element> elements = XmlUtility.getBorrowTechnics(techElement);
		if (elements != null) {
			for (Element ee : elements) {
				// index++;
				// 如果已经写到最后一行，则需要新增一页继续写
				if (index > 16) {
					hangshu = 1;
					index = 1;
					i++;
					Map<String, List<String>> muluMap = all.get(1);
					template = ((List<String>) muluMap.values().toArray()[0]).get(i);
					System.out.println("-------template----" + template);
					// if(i == 1) {
					printer.addText(template, "页码_1", "共" + pages + "页");
					printer.addText(template, "页码_2", "第" + (i + 2) + "页");
					printer.addText(template, "页码_3", "第" + (i + 2) + "页");
					// }
					// if(i == 2) {
					// printer.addText(template, "页码_1", "共"+pages+"页");
					// printer.addText(template, "页码_2", "第4页");
					// printer.addText(template, "页码_3", "第4页");
					// }
					//
					// if(i>2) {
					// break;
					// }
				}
				String name = ee.attributeValue("technicsName");
				String number = ee.attributeValue("technicsNumber");
				String dept = ee.attributeValue("DEPT");
				String comment = ee.attributeValue("comment");
				printer.addText(template, "序号_" + hangshu, String.valueOf(xuhao));
				printer.addText(template, "工艺文件编号_" + hangshu, number);
				// printer.addText(template, "工艺文件名称_"+hangshu, name);
				printer.addText(template, "代号_" + hangshu, PDFUtil.objectToString(techElement.attributeValue("CINDEX")));
				printer.addText(template, "名称_" + hangshu, PDFUtil.objectToString(techElement.attributeValue("partName")));
				printer.addText(template, "页数_" + hangshu, "");
				printer.addText(template, "责任部门_" + hangshu, dept);
				printer.addText(template, "备注_" + hangshu, comment);
				Map<String, Object> map = writeProcess(name, amount, printer, "工艺文件名称_", hangshu, index, template, i, pages);
				xuhao++;
				hangshu = (Integer) map.get("hangshu");
				index = (Integer) map.get("index");
				i = (Integer) map.get("page");
				template = (String) map.get("templateName");
			}
		}

		// 删除没有写入数据的目录页面
		if (!all.isEmpty()) {
			if (techElement.attributeValue("technicsType") != null && !techElement.attributeValue("technicsType").equals("工装工艺")
					&& !"XWReportTechnicsInfo".equals(techElement.getName())) {
				int amount = ((List<String>) all.get(1).values().toArray()[0]).size();
				for (int j = 0; j < amount; j++) {
					if (j > i) {
						printer.removeTempl(((List<String>) all.get(1).values().toArray()[0]).get(j));
					}
				}
				// printer.removeTempl(((List<String>)all.get(1).values().toArray()[0]).get(1));
				// printer.removeTempl(((List<String>)all.get(1).values().toArray()[0]).get(2));
			}
		}
		// else if ( i == 1) {
		// printer.removeTempl(((List<String>)all.get(1).values().toArray()[0]).get(2));
		// }
		if ("XWReportTechnicsInfo".equals(techElement.getName())) {
			pages = pages + 1;
		}

		return pages;
	}

	public static int setCatalog(LcmPdfPrinter printer, List<Map<String, List<String>>> all, Element techElement) {
		// 获取PDF总页数
		int pages = 0;
		List<String> fileList = null;
		for (Map<String, List<String>> map : all) {
			pages = pages + map.get(map.keySet().toArray()[0]).size();
			if (map.get("工艺文件目录") != null) {
				fileList = map.get("工艺文件目录");
			}
		}
		// 记录页码
		int count = 3;
		if (techElement.attributeValue("technicsType") != null && !techElement.attributeValue("technicsType").equals("工装工艺")
				&& !"XWReportTechnicsInfo".equals(techElement.getName())) {
			// 计算目录的总条目数量

			int allCount = countItem(all, techElement);
			System.out.println("----------allCount----" + allCount);
			if (allCount < 17) {
				pages = pages - 5;
				count = 3;
			} else if (allCount > 16 && allCount < 33) {
				pages = pages - 4;
				count = 4;
			} else if (allCount > 32 && allCount < 49) {
				pages = pages - 3;
				count = 5;
			} else if (allCount > 48 && allCount < 65) {
				pages = pages - 2;
				count = 6;
			} else if (allCount > 64 && allCount < 81) {
				pages = pages - 1;
				count = 7;
			} else if (allCount > 80 && allCount < 97) {
				count = 8;
			}
		} else {
			count = 2;
		}

		String template = "";
		int index = 0;// 记录工艺文件目录页面写到第几行
		int hangshu = 1;

		for (int i = 0; i < all.size(); i++) {
			Map<String, List<String>> map = all.get(i);
			String templateName = (String) map.keySet().toArray()[0];

			// 封面，最后处理
			if (i == 0) {
				printer.addText(templateName, "页码总数", "共" + pages + "页");
			}
			// 目录页面
			else if (i == 1) {
				if (techElement.attributeValue("technicsType") != null && !techElement.attributeValue("technicsType").equals("工装工艺")
						&& !"XWReportTechnicsInfo".equals(techElement.getName())) {
					template = templateName;
					if (techElement.attributeValue("technicsType").contains("英文")) {
						printer.addText(templateName, "页码_1", "共" + pages + "页");
						printer.addText(templateName, "页码_2", "第2页");
						printer.addText(templateName, "页码_3", "第2页");
					} else {
						printer.addText(templateName, "页码_1", "共" + pages + "页");
						printer.addText(templateName, "页码_2", "第2页");
						printer.addText(templateName, "页码_3", "第2页");
					}
				} else {
					List<String> tempList = map.get(templateName);
					for (int j = 0; j < tempList.size(); j++) {
						String pageTemplateName = tempList.get(j);
						printer.addText(pageTemplateName, "页码_1", "共" + pages + "页");
						printer.addText(pageTemplateName, "页码_2", "第" + count + "页");
						printer.addText(pageTemplateName, "页码_3", "第" + count + "页");

						count++;
					}
				}
			} else {
				// 在目录页面记录当前一行数据
				if (!templateName.equals("工艺附图") && !templateName.equals("工艺附表")) {
					printer.addText(template, "序号_" + (hangshu), String.valueOf(hangshu));
					printer.addText(template, "工艺文件编号_" + (hangshu), PDFUtil.objectToString(techElement.attributeValue("pplanNumber")));
					printer.addText(template, "工艺文件名称_" + (hangshu), templateName);
					printer.addText(template, "代号_" + (hangshu), PDFUtil.objectToString(techElement.attributeValue("CINDEX")));
					printer.addText(template, "名称_" + (hangshu), PDFUtil.objectToString(techElement.attributeValue("partName")));
					printer.addText(template, "页数_" + (hangshu), countPages(all, templateName) + "");
					printer.addText(template, "责任部门_" + (hangshu), PDFUtil.objectToString(techElement.attributeValue("DEPT")));
					printer.addText(template, "备注_" + (hangshu), "");
					hangshu++;
					index++;
					// 记录工艺文件目录页面写到第几行
				}
				// 遍历当前模板的所有页面，写入页码
				List<String> tempList = map.get(templateName);
				for (int j = 0; j < tempList.size(); j++) {
					String pageTemplateName = tempList.get(j);
					printer.addText(pageTemplateName, "页码_1", "共" + pages + "页");
					printer.addText(pageTemplateName, "页码_2", "第" + count + "页");
					printer.addText(pageTemplateName, "页码_3", "第" + count + "页");

					count++;
				}
			}
		}

		int i = 0;// 记录目录模板的页数
		// 如果是主工艺文件，则在工艺文件目录最后要写入辅工艺文件的编号和名称
		String zfFlag = techElement.attributeValue("ZFFLAG");
		int xuhao = hangshu;
		if ("Z".equals(zfFlag)) {
			List<Element> felements = XmlUtility.getFZTechnics(techElement);
			index += 1;
			if (felements != null) {
				for (Element ee : felements) {
					// index++;
					// 如果已经写到最后一行，则需要新增一页继续写
					if (index > 16) {
						hangshu = 1;
						index = 1;
						i++;
						Map<String, List<String>> muluMap = all.get(1);
						System.out.println("----------muluMap----" + muluMap);
						template = ((List<String>) muluMap.values().toArray()[0]).get(i);
						System.out.println("----------template----" + template);
						// if(i == 1) {
						printer.addText(template, "页码_1", "共" + pages + "页");
						printer.addText(template, "页码_2", "第" + (i + 2) + "页");
						printer.addText(template, "页码_3", "第" + (i + 2) + "页");
						// }
						// if(i == 2) {
						// printer.addText(template, "页码_1", "共"+pages+"页");
						// printer.addText(template, "页码_2", "第4页");
						// printer.addText(template, "页码_3", "第4页");
						// }

						// if(i>2) {
						// break;
						// }
					}
					String name = ee.attributeValue("name");
					String number = ee.attributeValue("number");
					String pagesValue = ee.attributeValue("pages");
					String department = ee.attributeValue("department");
					printer.addText(template, "序号_" + hangshu, String.valueOf(xuhao));
					printer.addText(template, "工艺文件编号_" + hangshu, number);
					// printer.addText(template, "工艺文件名称_"+hangshu, name);
					printer.addText(template, "代号_" + hangshu, PDFUtil.objectToString(techElement.attributeValue("CINDEX")));
					printer.addText(template, "名称_" + hangshu, PDFUtil.objectToString(techElement.attributeValue("partName")));
					printer.addText(template, "页数_" + hangshu, pagesValue);
					printer.addText(template, "责任部门_" + hangshu, department);
					Map<String, Object> map = writeProcess(name, amount, printer, "工艺文件名称_", hangshu, index, template, i, pages);
					xuhao++;
					hangshu = (Integer) map.get("hangshu");
					index = (Integer) map.get("index");
					i = (Integer) map.get("page");
					template = (String) map.get("templateName");
				}
			}
		} else if ("F".equals(zfFlag)) {
			index += 1;
		}

		// 在工艺文件目录中写入"典型工业"和"通用工艺"的编号及名称条目
		List<Element> elements = XmlUtility.getBorrowTechnics(techElement);
		if (elements != null) {
			for (Element ee : elements) {
				// index++;
				// 如果已经写到最后一行，则需要新增一页继续写
				if (index > 16) {
					hangshu = 1;
					index = 1;
					i++;
					Map<String, List<String>> muluMap = all.get(1);
					template = ((List<String>) muluMap.values().toArray()[0]).get(i);
					System.out.println("-------template----" + template);
					// if(i == 1) {
					printer.addText(template, "页码_1", "共" + pages + "页");
					printer.addText(template, "页码_2", "第" + (i + 2) + "页");
					printer.addText(template, "页码_3", "第" + (i + 2) + "页");
					// }
					// if(i == 2) {
					// printer.addText(template, "页码_1", "共"+pages+"页");
					// printer.addText(template, "页码_2", "第4页");
					// printer.addText(template, "页码_3", "第4页");
					// }
					//
					// if(i>2) {
					// break;
					// }
				}
				String name = ee.attributeValue("technicsName");
				String number = ee.attributeValue("technicsNumber");
				String dept = ee.attributeValue("DEPT");
				String comment = ee.attributeValue("comment");
				printer.addText(template, "序号_" + hangshu, String.valueOf(xuhao));
				printer.addText(template, "工艺文件编号_" + hangshu, number);
				// printer.addText(template, "工艺文件名称_"+hangshu, name);
				printer.addText(template, "代号_" + hangshu, PDFUtil.objectToString(techElement.attributeValue("CINDEX")));
				printer.addText(template, "名称_" + hangshu, PDFUtil.objectToString(techElement.attributeValue("partName")));
				printer.addText(template, "页数_" + hangshu, "");
				printer.addText(template, "责任部门_" + hangshu, dept);
				printer.addText(template, "备注_" + hangshu, comment);
				Map<String, Object> map = writeProcess(name, amount, printer, "工艺文件名称_", hangshu, index, template, i, pages);
				xuhao++;
				hangshu = (Integer) map.get("hangshu");
				index = (Integer) map.get("index");
				i = (Integer) map.get("page");
				template = (String) map.get("templateName");
			}
		}

		// 在工艺文件目录中写入工序、工步引用的SOP文件    不作区分 去重
		Map<String, String> sopMaps = new HashMap<String, String>();
		List<Element> allSteps = XmlUtility.getAllSteps(techElement);
		if(allSteps != null){
			for (Element step : allSteps) {
				List<Element> sopTechs = SopXMLUtility.getRelatedSopTechs(step);
				if(sopTechs.size()>0){
					Element stepSop = sopTechs.get(0);
					String number = stepSop.attributeValue("ppnumber");
					String name = stepSop.attributeValue("name");
					int indexOf = name.indexOf("(");
					if(indexOf>-1){
						name = name.substring(0,indexOf);
					}
					sopMaps.put(number, name);
				}
				List<Element> allPaces = XmlUtility.getAllPaces(step);
				for (Element pace : allPaces) {
					List<Element> sopTechs2 = SopXMLUtility.getRelatedSopTechs(pace);
					if(sopTechs2.size()>0){
						Element paceSOP = sopTechs2.get(0);
						String number = paceSOP.attributeValue("ppnumber");
						String name = paceSOP.attributeValue("name");
						int indexOf = name.indexOf("(");
						if(indexOf>-1){
							name = name.substring(0,indexOf);
						}
						sopMaps.put(number, name);
					}
				}
			}
		}
		for (Entry<String, String> entry : sopMaps.entrySet()) {
			String number = entry.getKey();
			String name = entry.getValue();

			// index++;
			// 如果已经写到最后一行，则需要新增一页继续写
			if (index > 16) {
				hangshu = 1;
				index = 1;
				i++;
				Map<String, List<String>> muluMap = all.get(1);
				template = ((List<String>) muluMap.values().toArray()[0]).get(i);
				System.out.println("-------template----" + template);
				printer.addText(template, "页码_1", "共" + pages + "页");
				printer.addText(template, "页码_2", "第" + (i + 2) + "页");
				printer.addText(template, "页码_3", "第" + (i + 2) + "页");
			}
			printer.addText(template, "序号_" + hangshu, String.valueOf(xuhao));
			printer.addText(template, "工艺文件编号_" + hangshu, number);
			// printer.addText(template, "工艺文件名称_"+hangshu, name);
//			printer.addText(template, "代号_" + hangshu, PDFUtil.objectToString(techElement.attributeValue("CINDEX")));
//			printer.addText(template, "名称_" + hangshu, PDFUtil.objectToString(techElement.attributeValue("partName")));
			printer.addText(template, "页数_" + hangshu, "");
			Map<String, Object> map = writeProcess(name, amount, printer, "工艺文件名称_", hangshu, index, template, i, pages);
			xuhao++;
			hangshu = (Integer) map.get("hangshu");
			index = (Integer) map.get("index");
			i = (Integer) map.get("page");
			template = (String) map.get("templateName");

		}

		// 删除没有写入数据的目录页面
		if (!all.isEmpty()) {
			if (techElement.attributeValue("technicsType") != null && !techElement.attributeValue("technicsType").equals("工装工艺")
					&& !"XWReportTechnicsInfo".equals(techElement.getName())) {
				int amount = ((List<String>) all.get(1).values().toArray()[0]).size();
				for (int j = 0; j < amount; j++) {
					if (j > i) {
						printer.removeTempl(((List<String>) all.get(1).values().toArray()[0]).get(j));
					}
				}
				// printer.removeTempl(((List<String>)all.get(1).values().toArray()[0]).get(1));
				// printer.removeTempl(((List<String>)all.get(1).values().toArray()[0]).get(2));
			}
		}
		// else if ( i == 1) {
		// printer.removeTempl(((List<String>)all.get(1).values().toArray()[0]).get(2));
		// }
		if ("XWReportTechnicsInfo".equals(techElement.getName())) {
			pages = pages + 1;
		}

		return pages;
	}

	public static int setCatalog2(LcmPdfPrinter printer, List<Map<String, List<String>>> all, String filePath) {
		// 获取PDF总页数
		int pages = 0;
		for (Map<String, List<String>> map : all) {
			pages = pages + map.get(map.keySet().toArray()[0]).size();
		}

		// 记录页数
		int count = 2;
		for (int i = 0; i < all.size(); i++) {
			Map<String, List<String>> map = all.get(i);
			String templateName = (String) map.keySet().toArray()[0];
			// 封面，最后处理
			if (i == 0) {
				printer.addText(templateName, "页码总数", "共" + pages + "页");

			} else {
				// 遍历当前模板的所有页面，写入页码
				List<String> tempList = map.get(templateName);
				for (int j = 0; j < tempList.size(); j++) {
					String pageTemplateName = tempList.get(j);
					printer.addText(pageTemplateName, "页码_1", "共" + pages + "页");
					printer.addText(pageTemplateName, "页码_2", "第" + count + "页");
					printer.addText(pageTemplateName, "页码_3", "第" + count + "页");

					count++;
				}
			}
		}

		return pages;
	}

	public static int setSopCatalog(LcmPdfPrinter printer, List<Map<String, List<String>>> all, Element techElement) {
		// 获取PDF总页数
		int pages = 0;
		for (Map<String, List<String>> map : all) {
			pages = pages + map.get(map.keySet().toArray()[0]).size();
		}

		int currentPage = 1;
		for(Map<String, List<String>> templateMap : all){
			for(Map.Entry<String, List<String>> entry : templateMap.entrySet()){
				List<String> templateNameList = entry.getValue();
				for(String templateName : templateNameList){
					printer.addText(templateName, "页码总数", pages + "");
					printer.addText(templateName, "当前页", currentPage + "");
					currentPage++;
				}
			}
		}
		return pages;
	}

	private static String getBuildPdfClassName(String formId) {
		String className = "com.glaway.mpm.pdf.processor." + formId + "PDFBuilder";
		return className;
	}

	public static void openFile() throws IOException {
		Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler   " + techFloder + File.separator + "PDFPreview.pdf");
	}

	public static void openFile(String filePath) throws IOException {
		Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler   " + filePath);
	}

	/**
	 * 复制文件
	 *
	 * @param fromPath
	 * @param toPath
	 * @throws Exception
	 */
	private static void copyFiles(String fromPath, String toPath) throws Exception {
		File fromFile = new File(fromPath);
		File toFile = new File(toPath);
		if (fromFile.exists()) {
			if (fromFile.isFile()) {
				File newToFile = new File(toPath);
				newToFile.createNewFile();
				FileInputStream inFile = new FileInputStream(fromFile);
				FileOutputStream outFile = new FileOutputStream(newToFile);
				FileChannel inChannel = inFile.getChannel();
				FileChannel outChannel = outFile.getChannel();
				long bytesWritten = 0;
				long byteCount = inChannel.size();
				while (bytesWritten < byteCount) {
					bytesWritten += inChannel.transferTo(bytesWritten, byteCount - bytesWritten, outChannel);
				}
				inFile.close();
				outFile.close();
			} else {
				if (toFile.exists()) {
					File[] info = fromFile.listFiles();
					for (int i = 0; i < info.length; i++) {
						String toPathTemp = toPath + File.separator + info[i].getName();
						copyFiles(info[i].getAbsolutePath(), toPathTemp);//
					}
				} else {
					if (toFile.mkdir()) {
						File[] info = fromFile.listFiles();
						for (int i = 0; i < info.length; i++) {
							String toPathTemp = toPath + File.separator + info[i].getName();
							copyFiles(info[i].getAbsolutePath(), toPathTemp);//
						}
					} else {
					}
				}

			}

		}
	}

	/**
	 * 模板转换
	 *
	 * @param xmlFileName
	 * @param xslFileName
	 * @param htmlFileName
	 */
	private static void Transform(String xmlFileName, String xslFileName, String htmlFileName) {
		try {
			TransformerFactory tFac = TransformerFactory.newInstance();
			Source xslSource = new StreamSource(xslFileName);
			Transformer t = tFac.newTransformer(xslSource);
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			File xmlFile = new File(xmlFileName);
			File htmlFile = new File(htmlFileName);
			Source source = new StreamSource(xmlFile);
			Result result = new StreamResult(htmlFile);
			t.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从jar中copy文件
	 *
	 * @param sourceFolder
	 */
	private static void copyFromJar(File sourceFolder) {
		try {
			String sourceFolderPath = sourceFolder.getAbsolutePath() + File.separator;
			// String path =
			// ProcessInfoReleaseController.class.getResource("/com/glaway/mpm/release/templetes").getPath();
			String path = ProcessInfoReleaseController.class.getResource(ProcessInfoReleaseController.class.getSimpleName() + ".class").getFile();
			// System.out.println(">>>>>>>>>>>>>path:"+path);
			path = "jar:" + path.substring(0, path.indexOf("!") + 2);
			URL url = new URL(path);
			JarURLConnection con = (JarURLConnection) url.openConnection();
			JarFile jarFile = con.getJarFile();

			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				if (name.startsWith("com/glaway/mpm/release/templetes")) {
					name = name.replace("com/glaway/mpm/release/templetes/", "");
					if (!name.equals("")) {
						if (name.indexOf("/") == -1) {
							writeInputStreamToFile(jarFile.getInputStream(entry), sourceFolderPath + name);
						} else {
							int i = name.lastIndexOf("/");
							String tempPath = name.substring(0, i);
							String fileName = name.substring(i + 1);
							if (!fileName.equals("")) {
								File f = createDir(sourceFolderPath + tempPath);
								writeInputStreamToFile(jarFile.getInputStream(entry), f.getAbsolutePath() + File.separator + fileName);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写文件
	 *
	 * @param is
	 * @param destPath
	 */
	public static void writeInputStreamToFile(InputStream is, String destPath) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(new FileOutputStream(destPath));
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = bis.read(b)) != -1) {
				bos.write(b, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建目录
	 *
	 * @param path
	 * @return
	 */
	private static File createDir(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	public static void createHtmlByXml(String xmlFileName, String targetPath) throws IOException, DocumentException {
		File file = new File(xmlFileName);
		Map<String, List<List<String>>> commonTableMap = new HashMap<String, List<List<String>>>();
		SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(file);
		Element root = doc.getRootElement();
		// 工艺文件节点
		Element technics = root.element("QMFawTechnicsInfo");
		// steps节点
		Element steps = technics.element("steps");
		// 工序节点
		List<Element> procedureList = steps.elements("QMProcedureInfo");
		commonTableMap = getQMProcedureInfo(procedureList, commonTableMap, "_procedure_");
		for (Element procedure : procedureList) {
			Element paces = procedure.element("paces");
			if (paces != null) {
				// 工步节点
				List<Element> paceList = paces.elements("QMProcedureInfo");
				commonTableMap = getQMProcedureInfo(paceList, commonTableMap, "_paces_");
			}
		}
		// 生成工序通用表html
		for (Map.Entry<String, List<List<String>>> entry : commonTableMap.entrySet()) {
			String stepNumber = entry.getKey().toString();
			List<List<String>> tableVlues = entry.getValue();
			String htmlString = bufferedHtml(tableVlues);
			String fileName = targetPath + File.separator + "commonTable" + stepNumber + ".html";
			// System.out.println("FileName:" + fileName);
			File htmlFile = writeStringToHtml(htmlString, fileName);
		}

	}

	public static Map<String, List<List<String>>> getQMProcedureInfo(List<Element> procedureList, Map<String, List<List<String>>> tableMap, String type)
			throws IOException {
		List<String> columnNameList = null;
		List<String> cloumnValueList = null;
		List<List<String>> columnValues = null;
		String stepNumber = "";
		for (Element procedure : procedureList) {
			columnValues = new ArrayList<List<String>>();
			stepNumber = type + procedure.attributeValue("stepNumber");
			Element commonParamTables = procedure.element("commonParamTables");
			if (commonParamTables != null) {
				Element parameterTable = commonParamTables.element("parameterTable");
				List<Element> parameterList = parameterTable.elements("parameter");
				for (Element parameter : parameterList) {
					columnNameList = new ArrayList<String>();
					cloumnValueList = new ArrayList<String>();
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
							// +",attribute:" + attributeValue);
						}
					}
					columnValues.add(columnNameList);
					columnValues.add(cloumnValueList);
					// System.out.println(">>>>>>>>>");
				}
				tableMap.put(stepNumber, columnValues);
			}
		}
		return tableMap;
	}

	public static String bufferedHtml(List<List<String>> values) {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		sb.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/><title></title></head><body><table>");
		for (int i = 0; i < values.size(); i++) {
			sb.append("<tr>");
			List<String> valuesOfLine = values.get(i);
			for (String value : valuesOfLine) {
				sb.append("<td>" + value + "</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</table></body></html>");
		return sb.toString();
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
		// System.out.println("ok");
		return file;
	}

	public static Map<String, String> addToTemplate(Map<String, String> templates, String fileName, String templateName) {
		Map<String, String> temp1 = new LinkedHashMap<String, String>();
		Map<String, String> temp2 = new LinkedHashMap<String, String>();
		for (Map.Entry<String, String> entry : templates.entrySet()) {
			if (!entry.getKey().equals("工艺文件封面") && !entry.getKey().contains("工艺文件目录")) {
				temp2.put(entry.getKey(), entry.getValue());
			} else {
				temp1.put(entry.getKey(), entry.getValue());
			}
		}
		temp1.put(fileName, templateName);
		for (Map.Entry<String, String> entry2 : temp2.entrySet()) {
			temp1.put(entry2.getKey(), entry2.getValue());
		}
		return temp1;
	}

	public static Map<String, Object> writeProcess(String valueStr, int count, LcmPdfPrinter printer, String key, int row, int index, String templateName,
			int page, int pages) {
		Map<String, Object> map = new HashMap<String, Object>();
		char[] clArr = valueStr.toCharArray();

		// n用于计算valueStr总共需要几行来写入数据
		int n = clArr.length / count;

		// 如果不能整除，则表示还需要多一行来写入余下的数据
		if (clArr.length % count != 0) {
			n = n + 1;
		}
		// 循环n行，将第n行的数据写入PDF页面的row+i行
		for (int i = 0; i < n; i++) {
			// 用于将第n行的count个char类型的值转换成String类型的值，以方便写入PDF
			String value = "";
			for (int j = 0; j < count; j++) {
				int col = count * i + j;
				if (col < clArr.length) {
					value = value + clArr[col];
				}
			}
			if (index > 16) {
				row = 1;
				index = 1;
				page++;
				Map<String, List<String>> muluMap = all.get(1);
				System.out.println("----------muluMap----" + muluMap);
				templateName = ((List<String>) muluMap.values().toArray()[0]).get(page);
				System.out.println("----------template----" + templateName);
				printer.addText(templateName, "页码_1", "共" + pages + "页");
				printer.addText(templateName, "页码_2", "第" + (page + 2) + "页");
				printer.addText(templateName, "页码_3", "第" + (page + 2) + "页");
			}
			// 写入PDF的key+(row)格子中
			printer.addText(templateName, key + row, value);
			row++;
			index++;
		}
		map.put("hangshu", row);
		map.put("index", index);
		map.put("templateName", templateName);
		map.put("page", page);
		return map;
	}

	/**
	 * 计算工艺文件名称所占pdf输出的行数
	 *
	 * @param str
	 *            工艺文件名称
	 * @param n
	 *            每行输出字符数量
	 * @return
	 */
	public static int calculateCount(String str, int n) {
		char[] clArr = str.toCharArray();
		// n用于计算valueStr总共需要几行来写入数据
		int count = clArr.length / n;
		if (clArr.length % n != 0) {
			count = count + 1;
		}
		return count;
	}
}

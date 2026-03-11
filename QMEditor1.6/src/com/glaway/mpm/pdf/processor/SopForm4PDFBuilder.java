package com.glaway.mpm.pdf.processor;

import com.glaway.mpm.pdf.*;
import com.glaway.mpm.util.Word2HtmlUtil;
import com.glaway.mpm.util.XmlUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.NewTechnicsPart;
import com.itextpdf.text.Font;

import org.apache.commons.io.FileUtils;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SopForm4PDFBuilder extends PDFBuilder {

	private Element paceElement;
	private String templateName;
	// 用于记录PDF的页数
	private int page = 1;
	// 用于记录工艺附表所占页数
	private int gyfbPage = 1;
	// 用于记录工步内容所占页数
	private int contentPage = 1;
	// 记录行数
	private int row = 1;
	// 设置每行最大字符数
	private float paceContentTempCount = 23;
	private boolean isPace = false;
	// 保存所有的PDF页面名称
	private List<String> templateList = new ArrayList<String>();
	private List<String> allList;

	public SopForm4PDFBuilder(Element techElement, String filePath, String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = techElement;
	}

	public SopForm4PDFBuilder(Element paceElement, String filePath, String formName, boolean isPace) {
		this.techFloder = filePath;
		this.formName = formName;
		this.paceElement = paceElement;
		this.isPace = isPace;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void buildPDF(LcmPdfPrinter printer, List<Map<String, String>> partList, Map<String, String> params) {
		try {
		    Word2HtmlUtil.openActiveXComponent();
			template = PDFUtil.getFormTemplateFolderPath() + "SopForm4.pdf";
			templateName = formName;
			if (isPace) {
				Element stepEle = paceElement.getParent().getParent();
				techElement = stepEle.getParent().getParent();
				List<Element> paceTables = XmlUtility.getTechnicsAdditionTables(paceElement);
				writeAdditionTables(stepEle, paceTables, printer);
				templateName = formName + "_" + contentPage;
				if (!templateList.contains(templateName)) {
					templateList.add(templateName);
					printer.addTempl(templateName, template);
					setStepData(printer, templateName, stepEle);
				}
				Element mEle = (Element) paceElement.elements("procedureContent").get(0);
				String procedureContent = PDFUtil.objectToString(mEle.getTextTrim());
				writePaceContent(stepEle, procedureContent, printer);
				if (gyfbPage > contentPage) {
					page = gyfbPage;
				} else {
					page = contentPage;
				}
				page++;
				row = 1;
				gyfbPage = page;
				contentPage = page;
			} else {
				List<Element> steps = XmlUtility.getAllSteps(techElement);
				for (Element step : steps) {
					List<Element> paces = XmlUtility.getAllPaces(step);
					for (Element pace : paces) {
						// 工步附表
						List<Element> paceTables = XmlUtility.getTechnicsAdditionTables(pace);
						writeAdditionTables(step, paceTables, printer);
						// 工步内容
						templateName = formName + "_" + contentPage;
						if (!templateList.contains(templateName)) {
							templateList.add(templateName);
							printer.addTempl(templateName, template);
							setStepData(printer, templateName, step);
						}
						String procedureContent = XmlUtility.getProcedureContent(pace).trim();
						writePaceContent(step, procedureContent, printer);
						if (gyfbPage > contentPage) {
							page = gyfbPage;
						} else {
							page = contentPage;
						}
						row = 1;
						page++;
						gyfbPage = page;
						contentPage = page;
					}
				}
			}
			for (String templateName : templateList) {
				if (templateName.contains("操作卡片")) {
					this.setCommData(printer, templateName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    Word2HtmlUtil.colseActiveXComponent();
		}
	}

	/**
	 * 输出工序内容
	 *
	 * @param stepElement
	 * @param procedureContent
	 * @param printer
	 */
	private void writePaceContent(Element stepElement, String procedureContent, LcmPdfPrinter printer) throws Exception {
		Object[] wholeStr = transPaceContent(procedureContent);
		List<String> values = (List<String>) wholeStr[0];
		int wholeIndex = 0;
		int m = 0;
		procedureContent = (String) wholeStr[2];
		char[] clArr = procedureContent.toCharArray();
		if (row > 13) {
			paceContentTempCount = printer.getFieldWidth(template, "操作说明_" + (row - 13));
		} else {
			paceContentTempCount = printer.getFieldWidth(template, "操作说明_" + row);
		}
		float tempCount = paceContentTempCount;
		String outputValue = "";
		boolean isNext = false;
		for (int i = 0; i < clArr.length; i++) {
			if (clArr[i] == '♀') {
				String path = allList.get(m++);
				path = path.replace("/", File.separator);
				String imgPath = techFloder + File.separator + path;
				// 计算图片的长度，25占一个中文字符。img：0为长度，1为宽度
				int[] img = PDFUtil.getImgWidth(imgPath);
				if (img != null) {
					int width = img[0];
					if (width > tempCount) {// 剩余长度不够，则换到下一行写
						i--;// 需要回退一个字符
						m--;
						tempCount = 0;
					} else {
						tempCount = tempCount - width;
						outputValue = outputValue + PDFUtil.getImgHtmlCode(imgPath);
					}
				} else {
					System.out.println(imgPath + " is not exsit!");
				}
			} else if (clArr[i] == '♂') {
				isNext = true;
			} else if (clArr[i] == '♣') {
				tempCount = tempCount - baseFont.getWidthPoint(" ", 12f);
				if (tempCount < 0) {
					i--;
					tempCount = 0;
				} else {
					outputValue = outputValue + "&nbsp;";
				}
			} else if (clArr[i] == '❤') {
				tempCount = tempCount - baseFont.getWidthPoint("\"", 12f);
				if (tempCount < 0) {
					i--;
					tempCount = 0;
				} else {
					outputValue = outputValue + "&quot;";
				}
			} else if (clArr[i] == '♠') {
				tempCount = tempCount - baseFont.getWidthPoint(">", 12f);
				if (tempCount < 0) {
					i--;
					tempCount = 0;
				} else {
					outputValue = outputValue + "&gt;";
				}
			} else if (clArr[i] == '♥') {
				tempCount = tempCount - baseFont.getWidthPoint("<", 12f);
				if (tempCount < 0) {
					i--;
					tempCount = 0;
				} else {
					outputValue = outputValue + "&lt;";
				}
			} else if (clArr[i] == '♦') {
				String printvalue = values.get(wholeIndex);
				float tempLength = baseFont.getWidthPoint(printvalue, 12f);
				if (paceContentTempCount < tempLength) {
					tempLength = baseFont.getWidthPoint(message, 12f);
					System.out.println(values.get(wholeIndex) + "内容过长，输出有误，请分割后输出！");
					JOptionPane.showMessageDialog(null, values.get(wholeIndex) + "内容过长，输出有误，请分割换行后输出！", "提示", 1);
					JOptionPane.showMessageDialog(null, "字符过长会造成PDF数据丢失，请引起重视", "提示", 1);
				}
				tempCount = tempCount - tempLength;
				if (tempCount < 0) {
					i--;
					tempCount = 0;
				} else {
					outputValue = outputValue + printvalue;
					wholeIndex++;
				}
			} else {
				tempCount = tempCount - baseFont.getWidthPoint(clArr[i], 12f);
				if (tempCount < 0) {
					i--;
					tempCount = 0;
				} else {
					outputValue = outputValue + clArr[i];
				}
			}
			if (!isNext && tempCount > 0 && i < (clArr.length - 1)) {
				continue;
			}
			if (outputValue == null || "".equals(outputValue.trim())) {
				isNext = false;
				continue;
			}
			if (row > 13) {
				row = 1;
				contentPage++;
				templateName = formName + "_" + contentPage;
				if (!templateList.contains(templateName)) {
					templateList.add(templateName);
					printer.addTempl(templateName, template);
					setStepData(printer, templateName, stepElement);
				}
			}
			printer.addHtml2Blod(templateName, "操作说明_" + row, outputValue, Font.NORMAL);
			row++;
			tempCount = paceContentTempCount;
			outputValue = "";
			isNext = false;
		}
	}

	/**
	 * 转换工步内容，方便输出
	 *
	 * @param gbnr
	 * @return
	 */
	private Object[] transPaceContent(String gbnr) {
		Object[] wholeStr = null;
		if (gbnr != null && !"".equals(gbnr)) {
			gbnr = gbnr.replace("@#$%^\\", "");
			gbnr = gbnr.replaceAll("@#\\$", "");
			gbnr = gbnr.replace("\\", "/");
			gbnr = gbnr.replaceAll("WORKSPACE_PATH/", "");
			gbnr = gbnr.replaceAll("<html>", "").trim();
			gbnr = gbnr.replaceAll("<head>", "").trim();
			gbnr = gbnr.replaceAll("</head>", "").trim();
			gbnr = gbnr.replaceAll("<body>", "").trim();
			gbnr = gbnr.replaceAll("</body>", "").trim();
			gbnr = gbnr.replaceAll("</html>", "").trim();
			gbnr = gbnr.replaceAll("<!--EndFragment-->", "").trim();
			gbnr = gbnr.replaceAll("<br>", "").trim();
			if (gbnr.contains("<p style='margin-top: 0'>")) {
				gbnr = gbnr.replaceAll("<p style='margin-top: 0'>", "").trim();
			}
			if (gbnr.contains("<p style='margin-top:5'>")) {
				gbnr = gbnr.replaceAll("<p style='margin-top:5'>", "").trim();
			}
			// 为了计算字符方便，替换字符串中&nbsp;为特殊符号♣，标识该处为空格，在写入时再替换回来
			gbnr = gbnr.replace("&nbsp;", "♣").trim();
			// 替换字符串中</p>为特殊符号♂，标识该处为换行
			gbnr = gbnr.replaceAll("</p>", "♂").trim();
			gbnr = replaceTeShuFuHao(gbnr);
			// 替换表示图片的内为特殊符号(♀)，以方便写入
			String temp = PDFUtil.Html2Text(gbnr);
			// 获取所有的图片内容(content/*.png)
			allList = PDFUtil.getImgStr(gbnr);
			wholeStr = PDFUtil.getWholeStr(temp);
		}
		return wholeStr;
	}

	private void writeAdditionTables(Element stepElement, List<Element> paceTables, LcmPdfPrinter printer) {
		try {
			gyfbPage--;
			for (Element paceTable : paceTables) {
				String docName = paceTable.attributeValue("absolutePath");
				String docPath = this.getTechFloder() + File.separator + docName;
				File pdfFile = Word2HtmlUtil.genWord2Pdf(new File(docPath));

				String xmlFileName = techElement.attributeValue("technicsNumber");
				FileUtils.copyFileToDirectory(pdfFile, new File(this.getTechFloder()));
				String pdfFilePath = pdfFile.getName();
				paceTable.addAttribute("pdfAbsolutePath",pdfFilePath);

				OutputFormat format = OutputFormat.createPrettyPrint();
				format.setTrimText(false);
				format.setEncoding("GBK");
				XMLWriter writer = new XMLWriter(new FileOutputStream(this.getTechFloder() + File.separator + xmlFileName + ".xml"), format);
				writer.write(techElement.getDocument());
				writer.close();
//				List<String> tempList = printer.addPDFFile(formName, template, "图片", pdfFile, gyfbPage);
				List<String> tempList = printer.addPDFImage2(formName,template, "图片", pdfFile, 0, 0,gyfbPage);
				for (String tempName : tempList) {
					setStepData(printer, tempName, stepElement);
				}
				templateList.addAll(tempList);
				gyfbPage += tempList.size();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getTechFloder() {
		return techFloder;
	}

	@Override
	public List<String> getTemplateList() {
		return templateList;
	}

	@Override
	public void setCommData(LcmPdfPrinter printer, String templateName) {

		printer.addText(templateName, "文件编号", PDFUtil.objectToString(techElement.attributeValue("pplanNumber")));
		printer.addText(templateName, "文件名称", PDFUtil.objectToString(techElement.attributeValue("pplanName")));
		printer.addText(templateName, "版本号", PDFUtil.objectToString(techElement.attributeValue("version")));

	}

	public void setStepData(LcmPdfPrinter printer, String templateName, Element stepElement) {
		printer.addText(templateName, "顺序号", stepElement.attributeValue("stepNumber"));
		printer.addText(templateName, "操作名称", stepElement.attributeValue("stepName"));
	}

}

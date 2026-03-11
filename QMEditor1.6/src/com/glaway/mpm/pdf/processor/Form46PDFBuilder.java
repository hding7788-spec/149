package com.glaway.mpm.pdf.processor;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.glaway.mpm.pdf.LcmPdfPrinter;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.util.Word2HtmlUtil;
import com.glaway.mpm.util.XmlUtility;

public class Form46PDFBuilder extends PDFBuilder{

	private Element techElement;
	private String techFloder = null;
	private String partOid;
	private String formName;
	private List<String> templateList = new ArrayList<String>();
	public Form46PDFBuilder(String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = XmlUtility.getTechnicsElement(filePath);
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}
	public Form46PDFBuilder(Element techElement, String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = techElement;
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}

	@Override
	public void buildPDF(LcmPdfPrinter printer,List<Map<String,String>> partList,Map<String,String>params) {
		// TODO Auto-generated method stub
		try {
			String template = PDFUtil.getFormTemplateFolderPath() + "Form46.pdf";
			setEcnParams(params);
			List<Element> addTables = new ArrayList<Element>();
			List<Element> tables = XmlUtility.getTechnicsAdditionTables(techElement);
			for(Element table : tables){
				addTables.add(table);
			}
//			printer.addText(formName, "更改单号",params.get("ecnNo"));
			List<Element> procedures = XmlUtility.getAllSteps(techElement);
			for(Element procedure : procedures){
				List<Element> procedureTables = XmlUtility.getTechnicsAdditionTables(procedure);
				for(Element procudureTable : procedureTables){
					addTables.add(procudureTable);
				}
				List<Element> paces = XmlUtility.getAllPaces(procedure);
				for(Element pace : paces){
					List<Element> paceTables = XmlUtility.getTechnicsAdditionTables(pace);
					for(Element paceTable : paceTables){
						addTables.add(paceTable);
					}
				}
			}
	    	for(int i=0;i<addTables.size();i++){
	    		Element table = addTables.get(i);
	    		String pdf = XmlUtility.getAttributeValue(table, "absolutePath");
	    		String word = this.getTechFloder()+File.separator+pdf;
	    		File pdfFile = Word2HtmlUtil.genPdf(new File(word));
				List<String> templateNameList = printer.addPDFFile(template,pdfFile);
				templateList.addAll(templateNameList);
				for (String templateName : templateNameList) {
					this.setCommData(printer,templateName);
				}
	    	}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setCommData(LcmPdfPrinter printer,String templateName) {
		//modify by zhuhao 20180312
		String partVersion = PDFUtil.objectToString(techElement.attributeValue("partVersion"));
		if(partVersion.contains(".")){
			partVersion = partVersion.substring(0, partVersion.indexOf("."));
		}
		//modify by zhuhao end
		printer.addText(templateName, "PBOM版本", "P:"+partVersion);
		//modify by machongqi 2015-7-2
          	printer.addText(templateName, "工艺文件版本", PDFUtil.objectToString(techElement.attributeValue("version")));
  		//modify by machongqi end
          	printer.addText(templateName, "批次号", PDFUtil.objectToString(techElement.attributeValue("PCNO")));
//		printer.addText(templateName, "标识", PDFUtil.objectToString(techElement.attributeValue("BIAOSHI")));

		printer.addText(templateName, "产品代号", PDFUtil.objectToString(techElement.attributeValue("PINDEX")));
        printer.addText(templateName, "工艺文件编号", PDFUtil.objectToString(techElement.attributeValue("pplanNumber")));
        printer.addText(templateName, "名称", PDFUtil.objectToString(techElement.attributeValue("partName")));
        printer.addText(templateName, "代号", PDFUtil.objectToString(techElement.attributeValue("CINDEX")));
        printer.addText(templateName, "图纸版本", PDFUtil.objectToString(techElement.attributeValue("imageVersion")));

        String phaseCode = PDFUtil.objectToString(techElement.attributeValue("PHASE_CODE"));
        String index = PDFUtil.getPhaseCodeIndex(phaseCode);
        printer.addText(templateName, "阶段标记_"+index, phaseCode);

        printer.addText(templateName, "编制", "");
        printer.addText(templateName, "编制时间", "");
        printer.addText(templateName, "校对", "");
        printer.addText(templateName, "校对时间", "");
        printer.addText(templateName, "审核", "");
        printer.addText(templateName, "审核时间", "");

        printer.addText(templateName, "会签_1", "");
        printer.addText(templateName, "会签_2", "");
        printer.addText(templateName, "会签_3", "");
        printer.addText(templateName, "会签_4", "");
        printer.addText(templateName, "会签_5", "");
        printer.addText(templateName, "会签_6", "");
        printer.addText(templateName, "会签_7", "");
        printer.addText(templateName, "会签_8", "");
        printer.addText(templateName, "会签_9", "");
        printer.addText(templateName, "会签_10", "");

        printer.addText(templateName, "更改标记", ecnBiaoJi);
        printer.addText(templateName, "更改单号", ecnNo);
        printer.addText(templateName, "更改签名", "");
        printer.addText(templateName, "日期", "");
    }

	@Override
	public String getTechFloder() {
		// TODO Auto-generated method stub
		return techFloder;
	}

	@Override
	public List<String> getTemplateList() {
		// TODO Auto-generated method stub
		return templateList;
	}
}

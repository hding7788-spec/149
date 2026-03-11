package com.glaway.mpm.pdf.processor;

import com.glaway.mpm.EditorConfig;
import com.glaway.mpm.pdf.*;
import com.glaway.mpm.util.Word2HtmlUtil;
import com.glaway.mpm.util.XmlUtility;
import gui.ava.html.image.generator.HtmlImageGenerator;
import org.dom4j.Element;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Form7GYFBPDFBuilder extends PDFBuilder{

	private Element techElement;
	private String techFloder = null;
	private String partOid;
	private String formName;
	private List<String> templateList = new ArrayList<String>();
	public Form7GYFBPDFBuilder(String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = XmlUtility.getTechnicsElement(filePath);
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}
	public Form7GYFBPDFBuilder(Element techElement, String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = techElement;
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}

	@Override
	public void buildPDF(LcmPdfPrinter printer,List<Map<String,String>> partList,Map<String,String>params) {
		// TODO Auto-generated method stub
		try {
			String template = PDFUtil.getFormTemplateFolderPath() + "Form7GYFB.pdf";
			setEcnParams(params);
			List<Element> addTables = new ArrayList<Element>();
			List<Element> tables = XmlUtility.getTechnicsAdditionTables(techElement);
			for(Element table : tables){
				addTables.add(table);
			}
//			printer.addText(formName, "更改单号",params.get("ecnNo"));
			// 20230509 一序一卡改造 普通工艺文件形式只写入工艺端的附表  工艺文件形式为外协的仍然写入全部附表
			String isTabular = techElement.attributeValue("isTabular");
			if("外协".equals(isTabular)){
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
			}
	    	for(int i=0;i<addTables.size();i++){
	    		Element table = addTables.get(i);
	    		String pdf = XmlUtility.getAttributeValue(table, "absolutePath");
	    		String attachName = XmlUtility.getAttributeValue(table, "attachName");
	    		String word = this.getTechFloder()+File.separator+pdf;
	    		///pdm/aaa/cadf/additionaltable\20231109010506021\五的安人_199374a325d84dddbde84fc4b9697261.doc
	    		System.out.println(word);

				File pdfFile =null;
				if(EditorConfig.isWebInfLib){
					word = word.replace("\\", File.separator);
					String pdfPath =  word.substring(0,word.lastIndexOf(File.separatorChar));
					File fpdfPath = new  File(pdfPath);
					if(fpdfPath!=null) {
						File[] files = fpdfPath.listFiles();
						if(files!=null) {
							for (File file : files) {
								if (file.getName().endsWith("pdf")) {
									pdfFile = file;
								}
							}
						}
					}

				}else{
					pdfFile = Word2HtmlUtil.genPdf(new File(word));
				}
				if(pdfFile!=null&&pdfFile.exists()){
					List<String> templateNameList = printer.addPDFFile(template,pdfFile);
					templateList.addAll(templateNameList);
					for (String templateName : templateNameList) {
						this.setCommData(printer,templateName,attachName);
					}
				}

	    		//pdfFile = writeName(pdfFile, pdf);

	    	}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void buildProcedure(LcmPdfPrinter printer,Map<String,String>params,Element procedure) {
		try {
			String template = PDFUtil.getFormTemplateFolderPath() + "Form7GYFB.pdf";
			String stepNumber = procedure.attributeValue("stepNumber");
			String printDanYuanFlag = this.techElement.attributeValue("printDanYuanFlag");
			setEcnParams(params);
			List<Element> addTables = new ArrayList<Element>();
			List<Element> procedureTables = XmlUtility.getTechnicsAdditionTables(procedure);
			for(Element procudureTable : procedureTables){
				addTables.add(procudureTable);
			}
			List<Element> paces = XmlUtility.getAllPaces(procedure);

			List<CheckOutTableUnit> unitList = new ArrayList<CheckOutTableUnit>();
			for(Element pace : paces){
				List<Element> paceTables = XmlUtility.getTechnicsAdditionTables(pace);
				for(Element paceTable : paceTables){
					addTables.add(paceTable);
				}

				if(!"否".equals(printDanYuanFlag)){
					Element checkRecordTables= pace.element("checkRecordTables");
					if(checkRecordTables!=null){
						List<Element> parameterTables  = checkRecordTables.elements();
						for(Element parameterTable:parameterTables){
							String unitTableName = parameterTable.attributeValue("name");
							String tableType = parameterTable.attributeValue("type");
							String projectName = parameterTable.attributeValue("projectName");
							String tableName = parameterTable.attributeValue("tableName");

							List<Element> parameters = parameterTable.elements("parameter");
							for(Element parameter:parameters){
								Element values = parameter.element("values");
								CheckOutTableUnit unit = new CheckOutTableUnit();
								unit.setGongXu(stepNumber);
								unit.setGongBu(pace.attributeValue("stepNumber"));
								unit.setTableType(tableType);
								unit.setUnitTableName(unitTableName);
								unit.setProjectName(projectName);
								unit.setTableName(tableName);
								List<Element> valueList =  values.elements("value");
								for(Element value :valueList){
									String columnName = value.attributeValue("columnName");
									String attributeValue = value.element("attribute").getText();
									attributeValue = PDFUtil.objectToString(attributeValue);
									if ("检测项".equals(columnName) || "记录项".equals(columnName)) {
										unit.setJiLuXiang(attributeValue);
									} else if ("公称值".equals(columnName) || "要求".equals(columnName)) {
										unit.setYaoQiuVale(attributeValue);
									} else if ("上偏差".equals(columnName)) {
										unit.setShangPianCha(attributeValue);
									} else if ("下偏差".equals(columnName)) {
										unit.setXiaPianCha(attributeValue);
									} else if ("实测值".equals(columnName)) {
										unit.setShiCeValue(attributeValue);
									} else if ("判定/结论".equals(columnName)) {
										//无需处理
									} else if ("记录".equals(columnName)) {
										unit.setJiLu(attributeValue);
									}
								}
								unitList.add(unit);
							}
						}
					}
				}


			}
			for(int i=0;i<addTables.size();i++){
				Element table = addTables.get(i);
				String pdf = XmlUtility.getAttributeValue(table, "absolutePath");
				String attachName = XmlUtility.getAttributeValue(table, "attachName");
				String word = this.getTechFloder()+File.separator+pdf;
				File pdfFile =null;


				if(EditorConfig.isWebInfLib){
					word = word.replace("\\", File.separator);
					String pdfPath =  word.substring(0,word.lastIndexOf(File.separatorChar));

					File fpdfPath = new  File(pdfPath);
					if(fpdfPath!=null){
						File[] files = fpdfPath.listFiles();
						if(files!=null){
							for(File file : files){
								if(file.getName().endsWith("pdf")){
									pdfFile = file;
								}
							}
						}

					}
				}else{
					pdfFile = Word2HtmlUtil.genPdf(new File(word));
				}
				if(pdfFile!=null&&pdfFile.exists()){
					List<String> templateNameList = printer.addPDFFile(template,pdfFile);
					templateList.addAll(templateNameList);
					for (String templateName : templateNameList) {
						this.setCommData(printer,templateName,attachName);
						printer.addText(templateName, "工序号", PDFUtil.objectToString(stepNumber));
					}
				}
			}
			if(!unitList.isEmpty()){
				String filePath = this.getTechFloder()+File.separator+"unitTable";
				File unitTableFilePath = new File(filePath);

				List<List<CheckOutTableUnit>> splitLists = splitList(unitList, 16);
				String technicsNumber = XmlUtility.getAttributeValue(techElement, "technicsNumber");
				boolean needReGen = false;
				if(EditorConfig.isWebInfLib ){
					if(technicsNumber!=null &&technicsNumber.startsWith("9")){
						//批量生成工艺，如有异常不抛出
						try {
							if(!unitTableFilePath.exists()){
								unitTableFilePath.mkdirs();
							}
							int index = 1;
							for (List<CheckOutTableUnit> subList : splitLists) {
								String imageFilePath = filePath + File.separator + stepNumber + "_" + index + ".png";
								String htmlContent = HtmlGenerator.generateHtml(subList, "file:///" + this.getTechFloder());
								String htmlFilePath = filePath + File.separator + stepNumber + "_" + index + ".html";
								FileWriter htmlWriter = new FileWriter(htmlFilePath);
								htmlWriter.getEncoding();
								htmlWriter.write(htmlContent);
								htmlWriter.close();
								HtmlImageGenerator imageGenerator = HtmlImageGeneratorFactory.getInstance();
								imageGenerator.loadUrl("file:///" + htmlFilePath);
								imageGenerator.getBufferedImage();
								imageGenerator.saveAsImage(imageFilePath);
								System.out.println("生成 " + stepNumber + "工序 第" + index + "个单元表图片:" + imageFilePath);
								index++;
							}
						}catch (Exception e){
							e.printStackTrace();
						}
					}
				}else{
					if(!unitTableFilePath.exists()){
						unitTableFilePath.mkdirs();
					}
					int index = 1;
					for (List<CheckOutTableUnit> subList : splitLists) {
						String imageFilePath = filePath + File.separator + stepNumber + "_" + index + ".png";
						String htmlContent = HtmlGenerator.generateHtml(subList, "file:///" + this.getTechFloder());
						String htmlFilePath = filePath + File.separator + stepNumber + "_" + index + ".html";
						FileWriter htmlWriter = new FileWriter(htmlFilePath);
						htmlWriter.getEncoding();
						htmlWriter.write(htmlContent);
						htmlWriter.close();
						HtmlImageGenerator imageGenerator = HtmlImageGeneratorFactory.getInstance();
						imageGenerator.loadUrl("file:///" + htmlFilePath);
						imageGenerator.getBufferedImage();
						imageGenerator.saveAsImage(imageFilePath);
						System.out.println("生成 " + stepNumber + "工序 第" + index + "个单元表图片:" + imageFilePath);
						index++;
					}
				}


				String form7QualityRecordTemplate = PDFUtil.getFormTemplateFolderPath() + "Form7QualityRecord.pdf";

				for(int i = 1;i<splitLists.size()+1;i++){
					System.out.println("添加 "+stepNumber+"工序 第"+i+"个单元表页");
					String  imageFilePath = filePath+File.separator+stepNumber+"_"+i+".png";
					String unitFormName = formName +"unitTable"+ stepNumber+"_"+i;
					printer.addTempl(unitFormName, form7QualityRecordTemplate);
					templateList.add(unitFormName);

					this.setCommData(printer,unitFormName,"单元表");
					printer.addText(unitFormName, "工序号", PDFUtil.objectToString(stepNumber));

					printer.addImage(unitFormName,"工艺流程框图",imageFilePath);
				}




			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static  List<List<CheckOutTableUnit>> splitList(List<CheckOutTableUnit> originalList, int chunkSize) {
		List<List<CheckOutTableUnit>> splitLists = new ArrayList<List<CheckOutTableUnit>>();
		for (int i = 0; i < originalList.size(); i += chunkSize) {
			// 计算子列表的结束位置
			int end = Math.min(originalList.size(), i + chunkSize);
			// 截取子列表并添加到结果中
			List<CheckOutTableUnit> subList = originalList.subList(i, end);
			splitLists.add(subList);
		}
		return splitLists;
	}
	public void setCommData(LcmPdfPrinter printer,String templateName,String attachName) {
		//modify by zhuhao 20180312
		String partVersion = PDFUtil.objectToString(techElement.attributeValue("partVersion"));
		if(partVersion.contains(".")){
			partVersion = partVersion.substring(0, partVersion.indexOf("."));
		}
		//modify by zhuhao end
		printer.addText(templateName, "PBOM版本", "P:"+partVersion);
		printer.addText(templateName, "工艺附表", attachName);
		//modify by machongqi 2015-7-2
          	printer.addText(templateName, "工艺文件版本", PDFUtil.objectToString(techElement.attributeValue("version")));
  		//modify by machongqi end
          	printer.addText(templateName, "批次号", PDFUtil.objectToString(techElement.attributeValue("PCNO")));
         	printer.addText(templateName, "标识", "工艺附表");
        printer.addText(templateName, "产品代号", PDFUtil.objectToString(techElement.attributeValue("PINDEX")));
        printer.addText(templateName, "工艺文件编号", PDFUtil.objectToString(techElement.attributeValue("pplanNumber")));
        printer.addText(templateName, "名称", PDFUtil.objectToString(techElement.attributeValue("partName")));
        printer.addText(templateName, "代号", PDFUtil.objectToString(techElement.attributeValue("CINDEX")));

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

	/* (non-Javadoc)
	 * @see com.glaway.mpm.pdf.PDFBuilder#setCommData(com.glaway.mpm.pdf.LcmPdfPrinter, java.lang.String)
	 */
	@Override
	public void setCommData(LcmPdfPrinter printer, String templateName) {
		// TODO Auto-generated method stub

	}


}

package com.glaway.mpm.pdf.processor;

import com.glaway.mpm.pdf.LcmPdfPrinter;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.util.FilesUtil;
import com.glaway.mpm.util.XmlUtility;
import org.dom4j.Element;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Form7QualityRecordPDFBuilder extends PDFBuilder{

	private Element techElement;
	private String techFloder = null;
	private String formName;
	private List<String> templateList = new ArrayList<String>();
	public Form7QualityRecordPDFBuilder(String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = XmlUtility.getTechnicsElement(filePath);
	}
	public Form7QualityRecordPDFBuilder(Element techElement, String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = techElement;
	}
	@Override
	public void buildPDF(LcmPdfPrinter printer, List<Map<String, String>> partList, Map<String, String> params) {
		try {
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
			String template = PDFUtil.getFormTemplateFolderPath() + "Form7QualityRecord.pdf";
			setEcnParams(params);
			String imgPath = techFloder + File.separator + "images";
			List<String> nameList = PDFUtil.getNameList(techElement);
			//删除集合中的重复元素
			for(int i = 0; i < nameList.size() - 1; i++){
				for(int j = nameList.size() - 1; j > i; j--){
					if(nameList.get(j).equals(nameList.get(i))){
						nameList.remove(j);
					}
				}
			}
			int count = 1;
			for(String name : nameList){
				String chinaName = name.substring(0, name.lastIndexOf(","));
				if(chinaName.equals("通用检查项定义")){
					chinaName = "质量记录表";
				}
				String fName = name.substring(name.lastIndexOf(",") + 1, name.length());
				File file = new File(imgPath);
				File[] files = FilesUtil.orderByDate(imgPath);
				int n = 0;
				for(int i = 0; i < files.length; i++){
					String fileName = files[i].getName();
					int fileIndex = Integer.valueOf(fileName.substring(0, fileName.indexOf("_")));
					String sub = fileName.substring(fileName.indexOf("_") + 1, fileName.lastIndexOf("."));
					if(sub.equals(fName) && n == fileIndex){
						String procedureNumber = sub.split("_")[2];
						printer.addTempl(formName + i, template);
						templateList.add(formName + i);
						String imagePath = files[i].getAbsolutePath();
						printer.addImage(formName + i, "工艺流程框图", imagePath);
						if(count == 1){
							printer.addText(formName + i, "表名", "表（"+ (addTables.size() +1) +"） " + chinaName);
						}else{
							printer.addText(formName + i, "表名", "续表（"+ (addTables.size() +1) +"） " + chinaName);
						}
						printer.addText(formName + i, "工序号", procedureNumber);
						//记录基本数据，比如工艺文件编号等
						setCommData(printer,formName + i);
						n++;
					}
				}
				count ++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void setCommData(LcmPdfPrinter printer, String templateName) {
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
	public static String readHtmlToString(String fileName,String encode){
		StringBuffer sb = new StringBuffer();
		String htmlStr = "";
		try {
			File file = new File(fileName);
			InputStreamReader read = new InputStreamReader(new FileInputStream(file),encode);
			BufferedReader ins = new BufferedReader(read);
			String dataLine = "";
			while(null != (dataLine = ins.readLine())){
				sb.append(dataLine);
				sb.append("\r\n");
			}
			ins.close();
			htmlStr = sb.toString();
			htmlStr = htmlStr.replace("@#$%^\\", "");
			htmlStr = htmlStr.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
			htmlStr = htmlStr.replace("&lt;html&gt; &lt;head&gt; &lt;/head&gt; &lt;body&gt; &lt;", "<");
			htmlStr = htmlStr.replace("&gt; &lt;/body&gt; &lt;/html&gt;", ">");
		} catch (Exception e) {
		}
		return htmlStr;
	}
	public static File writeStringToHtml(String htmlStr,String encode, String newFilePath) throws IOException{
		File file = new File(newFilePath);
		if(!file.exists()){
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos,encode);
		osw.write(htmlStr);
		osw.flush();
//		System.out.println("ok");
		return file;
	}
}

package com.glaway.mpm.pdf.processor;

import com.glaway.mpm.pdf.LcmPdfPrinter;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.util.XmlUtility;
import org.dom4j.Element;

import java.util.List;
import java.util.Map;

public class Form51PDFBuilder extends PDFBuilder{
	private float totalWidth ;
	private final int rows = 15;

	/**工艺说明卡片*/
	public Form51PDFBuilder(String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = XmlUtility.getTechnicsElement(filePath);
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}
	public Form51PDFBuilder(Element techElement, String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = techElement;
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}

	@Override
	public void buildPDF(LcmPdfPrinter printer,List<Map<String,String>> partList,Map<String,String>params) {
		template = PDFUtil.getFormTemplateFolderPath() + "Form51.pdf";
		try {
			//输出工艺说明
			List<Element> describes = techElement.selectNodes("TechnicsDescribe");
			if(describes != null && !describes.isEmpty()) {
				Element technicsDescribe = describes.get(0);
				String tDescribe = technicsDescribe.getText();
				if(tDescribe != null && !"".equals(tDescribe)) {
					tDescribe = tDescribe.replace("@#$%^\\", "");
					tDescribe = tDescribe.replaceAll("@#\\$", "");
					tDescribe = tDescribe.replace("\\", "/");
					tDescribe = tDescribe.replaceAll("WORKSPACE_PATH/", "");
					tDescribe = tDescribe.replaceAll("<html>", "").trim();
					tDescribe = tDescribe.replaceAll("<head>", "").trim();
					tDescribe = tDescribe.replaceAll("</head>", "").trim();
					tDescribe = tDescribe.replaceAll("<body>", "").trim();
					tDescribe = tDescribe.replaceAll("</body>", "").trim();
					tDescribe = tDescribe.replaceAll("</html>", "").trim();
					tDescribe = tDescribe.replaceAll("<!--EndFragment-->", "").trim();
					tDescribe = tDescribe.replaceAll("<br>", "").trim();
					if(tDescribe.contains("<p style='margin-top: 0'>")) {
						tDescribe = tDescribe.replaceAll("<p style='margin-top: 0'>", "").trim();
					}
					if(tDescribe.contains("<p style='margin-top:5'>")) {
						tDescribe = tDescribe.replaceAll("<p style='margin-top:5'>", "").trim();
					}
					if(tDescribe != null && !"".equals(tDescribe)){
						//从第一页开始写数据
						setEcnParams(params);
						formName = formName + "_" + page;
						templateList.add(formName);
						printer.addTempl(formName, template);
						setCommData(printer, formName);
						if(index <= rows) {
							totalWidth = printer.getFieldWidth(template, "内容_" + index);
						} else {
							totalWidth = printer.getFieldWidth(template, "内容_1");
						}
						//为了计算字符方便，替换字符串中&nbsp;为特殊符号♣，标识该处为空格，在写入时再替换回来
						tDescribe = tDescribe.replace("&nbsp;", "♣").trim();
						//替换字符串中</p>为特殊符号♂，标识该处为换行
						tDescribe = tDescribe.replaceAll("</p>", "♂").trim();
						tDescribe = tDescribe.replace("\n", "");
						tDescribe = replaceTeShuFuHao(tDescribe);
						//替换表示图片的内为特殊符号(♀)，以方便写入
						String temp = PDFUtil.Html2Text(tDescribe);
						//获取所有的图片内容(content/*.png)
						List<String> allList = PDFUtil.getImgStr(tDescribe);
						Object[] wholeStr = PDFUtil.getWholeStr(temp);
						temp = (String) wholeStr[2];
						index = writeProcessDescribe(temp, totalWidth, printer, "内容_", index, rows, formName, allList, wholeStr);
						index++;
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setCommData(LcmPdfPrinter printer,String templateName) {
		String partVersion = PDFUtil.objectToString(techElement.attributeValue("partVersion"));
		if(partVersion.contains(".")){
			partVersion = partVersion.substring(0, partVersion.indexOf("."));
		}
		printer.addText(templateName, "PBOM版本", "P:"+partVersion);
		printer.addText(templateName, "工艺文件版本", PDFUtil.objectToString(techElement.attributeValue("version")));
		printer.addText(templateName, "批次号", PDFUtil.objectToString(techElement.attributeValue("PCNO")));

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
}

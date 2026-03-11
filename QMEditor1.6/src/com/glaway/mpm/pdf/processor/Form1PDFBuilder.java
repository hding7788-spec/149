package com.glaway.mpm.pdf.processor;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import wt.doc.WTDocument;
import wt.inf.container.WTContainer;
import wt.pdmlink.PDMLinkProduct;
import wt.util.WTException;

import com.glaway.mpm.pdf.LcmPdfPrinter;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.util.WTContainerUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.wcIntf.TechnicsIntf;

import ext.casc.util.IBAUtility;

public class Form1PDFBuilder extends PDFBuilder{

	private Element techElement;
	private String techFloder = null;
	private String partOid;
	private String formName;
	private List<String> templateList = new ArrayList<String>();
	public static String temp;
	public Form1PDFBuilder(String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = XmlUtility.getTechnicsElement(filePath);
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}

	public Form1PDFBuilder(Element techElement, String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = techElement;
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}

	@Override
	public void buildPDF(LcmPdfPrinter printer,List<Map<String,String>> partList,Map<String,String>params) {
		// TODO Auto-generated method stub
		try {
			String PPLANTYPE = techElement.attributeValue("PPLANTYPE");
			String templateFilePath = PDFUtil.getFormTemplateFolderPath() + "Form1.pdf";
			if ("临时工艺文件".equals(PPLANTYPE)){
				templateFilePath = PDFUtil.getFormTemplateFolderPath() + "Form1_tmp.pdf";
			}
			//File templateFile = new File(templateFilePath);
			setEcnParams(params);
			printer.addTempl(formName, templateFilePath);
//			printer.addText(formName, "更改单号_1",params.get("ecnNo"));
			this.setCommData(printer,formName);
			templateList.add(formName);
			temp=formName;
			//printer.print(techFloder+ File.separator+ "Form1.pdf");
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
		printer.addText(templateName, "标识", PDFUtil.objectToString(techElement.attributeValue("BIAOSHI")));
        printer.addText(templateName, "部门", PDFUtil.objectToString(techElement.attributeValue("DEPT")));
        if("临时工艺文件".equals(PDFUtil.objectToString(techElement.attributeValue("PPLANTYPE")))){
			printer.addText(templateName, "临时工艺编制依据_编号", ":"+PDFUtil.objectToString(techElement.attributeValue("bzyjNum")));
			printer.addText(templateName, "临时工艺编制依据_名称", ":"+PDFUtil.objectToString(techElement.attributeValue("bzyjName")));
			printer.addText(templateName, "临时工艺编制依据_原因", PDFUtil.objectToString(techElement.attributeValue("yyfl")));
		}
        String name = techElement.attributeValue("productName");
        String unMindex = "";
        try {
        	if(!"".equals(name) && name!=null){
				unMindex = TechnicsIntf.getUnMindexByProductName(name);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
        if("".equals(unMindex) || "null".equals(unMindex) || unMindex==null){
        	printer.addText(templateName, "型号代号", PDFUtil.objectToString(techElement.attributeValue("MINDEX")));
		}else{
			printer.addText(templateName, "型号代号", unMindex);
		}
        printer.addText(templateName, "产品代号", PDFUtil.objectToString(techElement.attributeValue("PINDEX")));

        String secret = PDFUtil.objectToString(techElement.attributeValue("SECRET"));
        if("无".equals(secret)) {
        	secret = "公开";
        }
        printer.addText(templateName, "密级", secret);

        String keyComponent = PDFUtil.objectToString(techElement.attributeValue("KEYCOMPONENT"));
        if("N".equals(keyComponent)) {
        	keyComponent = "";
        }
        printer.addText(templateName, "关重件标记", keyComponent);

        printer.addText(templateName, "工艺文件编号", PDFUtil.objectToString(techElement.attributeValue("pplanNumber")));
        printer.addText(templateName, "工艺文件名称", PDFUtil.objectToString(techElement.attributeValue("pplanName")));

        String phaseCode = PDFUtil.objectToString(techElement.attributeValue("PHASE_CODE"));
        String index = PDFUtil.getPhaseCodeIndex(phaseCode);

        printer.addText(templateName, "阶段标记_"+index, phaseCode);

        printer.addText(templateName, "名称", PDFUtil.objectToString(techElement.attributeValue("partName")));
        printer.addText(templateName, "代号", PDFUtil.objectToString(techElement.attributeValue("CINDEX")));

		printer.addText(templateName, "更改标记_1", ecnBiaoJi);
		printer.addText(templateName, "更改单号_1", ecnNo);

       /* printer.addText(templateName, "编制", "");
        printer.addText(templateName, "校对", "");
        printer.addText(templateName, "审核", "");
        printer.addText(templateName, "标检", "");
        printer.addText(templateName, "批准", "");

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


        printer.addText(templateName, "更改签名_1", "");
        printer.addText(templateName, "日期_1", "");
        printer.addText(templateName, "更改标记_2", "");
        printer.addText(templateName, "更改单号_2", "");
        printer.addText(templateName, "更改签名_2", "");
        printer.addText(templateName, "日期_2", "");
        printer.addText(templateName, "更改标记_3", "");
        printer.addText(templateName, "更改单号_3", "");
        printer.addText(templateName, "更改签名_3", "");
        printer.addText(templateName, "日期_3", "");*/
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

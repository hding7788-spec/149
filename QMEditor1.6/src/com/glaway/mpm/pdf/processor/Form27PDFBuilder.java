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
import com.glaway.mpm.pdf.WriterStandard;
import com.glaway.mpm.util.XmlUtility;

public class Form27PDFBuilder extends PDFBuilder{
 public Element basicElement;
 public int gongxuhao=1;
	public Form27PDFBuilder(String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = XmlUtility.getTechnicsElement(filePath);
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}
	public Form27PDFBuilder(Element techElement, String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = techElement;
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}

	@Override
	public void buildPDF(LcmPdfPrinter printer,List<Map<String,String>> partList,Map<String,String>params) {
		// TODO Auto-generated method stub
		try {
			template = PDFUtil.getFormTemplateFolderPath() + "Form27.pdf";
			setEcnParams(params);
			//printer.addText(formName, "更改单号",params.get("ecnNo"));
			//获取所有工序元素
			List<Element> steps = techElement.selectNodes("steps/QMProcedureInfo");
			//开始循环遍历所有工序

			for (Element procedure : steps) {

				String isKey = procedure.attributeValue("isKey");
				//是否关键工序
				if("true".equals(isKey)) {
					//从第一页开始写数据
					formName = formName + "_" + page++;
					basicElement=procedure;
					templateList.add(formName);
					printer.addTempl(formName, template);
					//记录基本数据，比如工艺文件编号等
					setCommData(printer,formName);
					writeElement(procedure,procedure,printer,(index),formName);
				}
				gongxuhao++;
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
	    printer.addText(templateName, "工序号", PDFUtil.objectToString(basicElement.attributeValue("stepNumber")));
//	    printer.addText(templateName, "工序号", PDFUtil.objectToString(gongxuhao));
	    printer.addText(templateName, "工序名称", PDFUtil.objectToString(basicElement.attributeValue("stepName")));
        printer.addText(templateName, "批次号2", PDFUtil.objectToString(basicElement.attributeValue("PCNO")));
        printer.addText(templateName, "操作者", PDFUtil.objectToString(basicElement.attributeValue("CZZ")));
        printer.addText(templateName, "使用设备", PDFUtil.objectToString(basicElement.attributeValue("SYSB")));
        printer.addText(templateName, "环境条件", PDFUtil.objectToString(basicElement.attributeValue("HJTJ")));
        printer.addText(templateName, "检测工具", PDFUtil.objectToString(basicElement.attributeValue("JCGJ")));
        printer.addText(templateName, "控制图表", PDFUtil.objectToString(basicElement.attributeValue("KZTB")));
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

	private void writeElement(Element procedure,Element element,LcmPdfPrinter printer,int row,String templateName) {
		printer.addText(templateName, "工序号", PDFUtil.objectToString(element.attributeValue("stepNumber")));
//	    printer.addText(templateName, "工序号", PDFUtil.objectToString(gongxuhao));
		printer.addText(templateName, "工序名称", PDFUtil.objectToString(element.attributeValue("stepName")));
        printer.addText(templateName, "批次号", PDFUtil.objectToString(element.attributeValue("PCNO")));
        printer.addText(templateName, "操作者", PDFUtil.objectToString(procedure.attributeValue("CZZ")));
        printer.addText(templateName, "使用设备", PDFUtil.objectToString(element.attributeValue("SYSB")));
        printer.addText(templateName, "环境条件", PDFUtil.objectToString(element.attributeValue("HJTJ")));
        printer.addText(templateName, "检测工具", PDFUtil.objectToString(procedure.attributeValue("JCGJ")));
        printer.addText(templateName, "控制图表", PDFUtil.objectToString(element.attributeValue("KZTB")));

        //modify by machongqi 2015-6-4
        Element kznrContentEle=element.element("kznrContent");
        if(kznrContentEle!=null){
        String kznr = PDFUtil.objectToString(kznrContentEle.getTextTrim());
        if(kznr != null && !"".equals(kznr)) {
        	kznr=getHtmlString(kznr);
            //替换表示图片的内为特殊符号(♀)，以方便写入
            String temp = PDFUtil.Html2Text(kznr);
            //获取所有的图片内容(content/*.png)
            List<String> allList = PDFUtil.getImgStr(kznr);
//            String kznr_old = PDFUtil.objectToString(element.attributeValue("KZNR"));
//            if(kznr_old!=null&&!"".equals(kznr_old)){
//            	temp=temp+"&nbsp;&nbsp;"+kznr_old;
//            }
            writeProcessGongXu(temp, WriterStandard.Form27_KZNR, printer, "控制内容_",row,6,templateName,allList);
        }else{
//        	String kznr_old = PDFUtil.objectToString(element.attributeValue("KZNR"));
//        	if(kznr_old!=null&&!"".equals(kznr_old)&&kznr_old.contains("@#$")) {
//        		kznr_old = kznr_old.replaceAll("@#\\$", "\r\n");
//            }
//        	writeProcessGongXu(kznr_old, WriterStandard.Form27_KZNR, printer, "控制内容_",row,6,templateName,null);
    }
        }else{
//        	String kznr_old = PDFUtil.objectToString(element.attributeValue("KZNR"));
//        	if(kznr_old!=null&&!"".equals(kznr_old)&&kznr_old.contains("@#$")) {
//        		kznr_old = kznr_old.replaceAll("@#\\$", "\r\n");
//            }
//        	writeProcessGongXu(kznr_old, WriterStandard.Form27_KZNR, printer, "控制内容_",row,6,templateName,null);
    }

        Element zlkzcxContentEle=element.element("zlkzcxContent");
        if(zlkzcxContentEle!=null){
        String zlkzcx = PDFUtil.objectToString(zlkzcxContentEle.getTextTrim());
        if(zlkzcx != null && !"".equals(zlkzcx)) {
        	zlkzcx=getHtmlString(zlkzcx);
            //替换表示图片的内为特殊符号(♀)，以方便写入
            String temp = PDFUtil.Html2Text(zlkzcx);
            //获取所有的图片内容(content/*.png)
            List<String> allList = PDFUtil.getImgStr(zlkzcx);
//            String zlkzcx_old = PDFUtil.objectToString(element.attributeValue("ZLKZCX"));
//            if(zlkzcx_old!=null&&!"".equals(zlkzcx_old)){
//            	temp=temp+"&nbsp;&nbsp;"+zlkzcx_old;
//            }
            writeProcessGongXu(temp, WriterStandard.Form27_ZLKZCX, printer, "质量控制程序_",row,6,templateName,allList);
        }else{
//        	String zlkzcx_old = PDFUtil.objectToString(element.attributeValue("ZLKZCX"));
//        	if(zlkzcx_old!=null&&!"".equals(zlkzcx_old)&&zlkzcx_old.contains("@#$")) {
//        		zlkzcx_old = zlkzcx_old.replaceAll("@#\\$", "\r\n");
//            }
//        	writeProcessGongXu(zlkzcx_old, WriterStandard.Form27_ZLKZCX, printer, "质量控制程序_",row,6,templateName,null);
    }
        }else{
//        	String zlkzcx_old = PDFUtil.objectToString(element.attributeValue("ZLKZCX"));
//        	if(zlkzcx_old!=null&&!"".equals(zlkzcx_old)&&zlkzcx_old.contains("@#$")) {
//        		zlkzcx_old = zlkzcx_old.replaceAll("@#\\$", "\r\n");
//            }
//        	writeProcessGongXu(zlkzcx_old, WriterStandard.Form27_ZLKZCX, printer, "质量控制程序_",row,6,templateName,null);
    }
        	//modify by machongqi end
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

	/**
	 * 将图片生成pdf
	 * @author machongqi
	 * @param valueStr
	 * @param count
	 * @param printer
	 * @param key
	 * @param row
	 * @param templateName
	 * @param allList
	 * @date 2015-6-4
	 * @return
	 */



/**
 * 将html格式标签过滤掉，提取图片路径和文本
 * @author machongqi
 * @param str
 * @date 2015-6-4
 * @return
 */

	private String getHtmlString(String str){
		if(str!=null&&!"".equals(str)){
			str = str.replace("@#$%^\\", "");
			str = str.replaceAll("@#\\$", "");
			str = str.replace("\\", "/");
			str = str.replaceAll("WORKSPACE_PATH/", "");
			str = str.replaceAll("<html>", "").trim();
			str = str.replaceAll("<head>", "").trim();
			str = str.replaceAll("</head>", "").trim();
			str = str.replaceAll("<body>", "").trim();
			str = str.replaceAll("</body>", "").trim();
			str = str.replaceAll("</html>", "").trim();
			str = str.replaceAll("<br>", "").trim();
    	if(str.contains("<p style='margin-top: 0'>")) {
    		str = str.replaceAll("<p style='margin-top: 0'>", "").trim();
    		//gxnr = gxnr.replaceAll("</p>", "").trim();
    	}
    	if(str.contains("<p style='margin-top:5'>")) {
    		str = str.replaceAll("<p style='margin-top:5'>", "").trim();
    		//gxnr = gxnr.replaceAll("</p>", "").trim();
    	}
    	//为了计算字符方便，替换字符串中&nbsp;为特殊符号♣，标识该处为空格，在写入时再替换回来
    	str = str.replace("&nbsp;", "♣").trim();
    	//替换字符串中</p>为特殊符号♂，标识该处为换行
    	str = str.replaceAll("</p>", "♂").trim();
    	str=replaceTeShuFuHao(str);
		}
		return str;
	}

}

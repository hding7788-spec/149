package com.glaway.mpm.pdf.processor;

import com.glaway.mpm.EditorConfig;
import com.glaway.mpm.pdf.LcmPdfPrinter;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import com.glaway.mpm.wcIntf.TemplateIntf;
import org.apache.commons.io.IOUtils;
import org.dom4j.Element;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Form7GYFTPDFBuilder extends PDFBuilder{

	private Element techElement;
	private String techFloder = null;
	private String partOid;
	private String formName;
	//用于记录PDF的页数
	private int page = 1;
	//用于记录当前PDF页面写到了第几行
	private int index = 1;
	private String template;
	//保存所有的PDF页面名称
	private String gongxuhao = "";
	private List<String> templateList = new ArrayList<String>();
	public Form7GYFTPDFBuilder(String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = XmlUtility.getTechnicsElement(filePath);
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}
	public Form7GYFTPDFBuilder(Element techElement, String filePath,String formName) {
		this.techFloder = filePath;
		this.formName = formName;
		this.techElement = techElement;
		partOid = XmlUtility.getAttributeValue(techElement, "partOid");
	}

	@Override
	public void buildPDF(LcmPdfPrinter printer,List<Map<String,String>> partList,Map<String,String>params) {
		// TODO Auto-generated method stub
		try {
			template = PDFUtil.getFormTemplateFolderPath() + "Form7GYFT.pdf";
			//获取所有工序元素
			setEcnParams(params);
			List<Element> steps = techElement.selectNodes("steps/QMProcedureInfo");
//			printer.addText(formName, "更改单号_1",params.get("ecnNo"));
			//20230509 一序一卡改造 普通工艺文件形式只写入工艺端的白羽图片  工艺文件形式为外协的仍然写入全部附图（工艺端白羽图片、工序简图、工步简图、工步端白羽图片）
			List<Element> tecBaiyuImageList = techElement.selectNodes("schemaData/schemaInfo");
			for(int i = 0;i < tecBaiyuImageList.size();i++) {
				Element mEle = tecBaiyuImageList.get(i);
				String docNumber = mEle.attributeValue("id");
				String imageNames = TechnicsIntf.getBaiYuImageBytesByNumber(docNumber);
				String[] ss = imageNames.split(";");
				for(String imageName:ss){
					if(!"".equals(imageName)){
						formName = formName + "_" + page++;
						writeBaiYuElement(mEle,printer,imageName, formName);
					}
				}

			}
			String isTabular = techElement.attributeValue("isTabular");
			String printBaiYuFlag = this.techElement.attributeValue("printBaiYuFlag");

			if("外协".equals(isTabular)){
				//开始循环遍历所有工序
				for (Element procedure : steps) {
					gongxuhao = procedure.attributeValue("stepNumber");
					List<Element> images = procedure.selectNodes("images/PDrawingInfo");
					for (Element element : images) {
						formName = formName + "_" + page++;
						writeElement(procedure,element,printer,index,formName);
					}
					//获取当前工序的所有工步的工艺辅料元素
					List<Element> stepList = procedure.selectNodes("paces/QMProcedureInfo/images/PDrawingInfo");
					for(int i = 0;i < stepList.size();i++) {
						Element mEle = stepList.get(i);
						formName = formName + "_" + page++;
						writeElement(procedure,mEle,printer,index,formName);
					}
					if(!"否".equals(printBaiYuFlag)) {
						List<Element> baiyuImageList = procedure.selectNodes("paces/QMProcedureInfo/schemaData/schemaInfo");
						for (int i = 0; i < baiyuImageList.size(); i++) {
							Element mEle = baiyuImageList.get(i);
							String docNumber = mEle.attributeValue("id");
							String imageNames = TechnicsIntf.getBaiYuImageBytesByNumber(docNumber);
							String[] ss = imageNames.split(";");
							for (String imageName : ss) {
								if (!"".equals(imageName)) {
									formName = formName + "_" + page++;
									writeBaiYuElement(mEle, printer, imageName, formName);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void buildProcedure(LcmPdfPrinter printer,Map<String,String>params,Element procedure) {
		try {
			template = PDFUtil.getFormTemplateFolderPath() + "Form7GYFT.pdf";
			gongxuhao = procedure.attributeValue("stepNumber");
			//获取所有工序元素
			setEcnParams(params);
			//写入指定工序附图
			List<Element> images = procedure.selectNodes("images/PDrawingInfo");
			for (Element element : images) {
				formName = formName + "_" + page++;
				writeElement(procedure,element,printer,index,formName);
			}
			//获取当前工序的所有工步的工艺辅料元素
			List<Element> stepList = procedure.selectNodes("paces/QMProcedureInfo/images/PDrawingInfo");
			for(int i = 0;i < stepList.size();i++) {
				Element mEle = stepList.get(i);
				formName = formName + "_" + page++;
				writeElement(procedure,mEle,printer,index,formName);
			}
			String printBaiYuFlag = this.techElement.attributeValue("printBaiYuFlag");
			if(!"否".equals(printBaiYuFlag)) {

				List<Element> baiyuImageList = procedure.selectNodes("paces/QMProcedureInfo/schemaData/schemaInfo");
				for (int i = 0; i < baiyuImageList.size(); i++) {
					Element mEle = baiyuImageList.get(i);
					String docNumber = mEle.attributeValue("id");
					String imageNames = TechnicsIntf.getBaiYuImageBytesByNumber(docNumber);
					String[] ss = imageNames.split(";");
					for (String imageName : ss) {
						if (!"".equals(imageName)) {
							formName = formName + "_" + page++;
							writeBaiYuElement(mEle, printer, imageName, formName);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeBaiYuElement(Element element, LcmPdfPrinter printer,  String imageName,String templateName) throws IOException, InvocationTargetException {
		String docNumber = element.attributeValue("id");
		String urlPrex = "http://pdm.149.sast.casc/aikeshengData/"+docNumber+"/";
		if(!EditorConfig.isZS){
			urlPrex = "http://pdmtest.149.sast.casc/aikeshengData/"+docNumber+"/";
		}
		String path = urlPrex+imageName;
		if(path!=null &&!"".equals(path)){
			printer.addTempl(templateName, template);
			templateList.add(templateName);
			//记录基本数据，比如工艺文件编号等
			setCommData(printer,templateName);
			printer.addText(templateName, "文件名称", imageName);
			printer.addText(templateName, "标识", "白羽附图");
			printer.addText(templateName, "工序号", PDFUtil.objectToString(gongxuhao));
			printer.addImage(templateName, "工艺流程框图", path);
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
		printer.addText(templateName, "标识", "工艺附图");

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

	private void writeElement(Element procedure,Element element,LcmPdfPrinter printer,int row,String templateName) throws IOException {
        String path = element.attributeValue("absolutePath");
        path = techFloder + File.separator + path;
        System.out.println("-----------------path---"+path);
        if(path.endsWith(".dwg")){
        	String tempDocNumber = XmlUtility.getAttributeValue(element, "tempDocNumber");
    		String docNumber = XmlUtility.getAttributeValue(element, "docNumber");
    		float top = 3.97f;
    		float left = 1.15f;
    		String key = "工艺流程框图";
    		byte[] dwgPdf =  TemplateIntf.getDwgPdfTemplateByteRMI(docNumber);// DWG->PDF
    		if(dwgPdf==null){
    			return;
    		}
    		String tempDir = this.getTechFloder() + File.separator+ "fbtemp";
    		File tempDirFile = new File(tempDir);
    		if(!tempDirFile.exists()) {
    			tempDirFile.mkdir();
    		}
    		String pdf = tempDir + File.separator + System.currentTimeMillis()+ ".pdf";
    		File pdfFile =  new File(pdf);
    		OutputStream dwgOs = null;
    		try {
				dwgOs = new FileOutputStream(pdfFile);
				IOUtils.write(dwgPdf, dwgOs);
				System.out.println("------pdf---" + pdf);
				System.out.println("------formName---" + formName);
				System.out.println("------templateName---" + templateName);
				List<String> list = printer.addPDFImage(template, key, pdfFile ,left,top);
				System.out.println("------list---" + list);
				for (String string : list) {
					//记录基本数据，比如工艺文件编号等
					setCommData(printer,string);
					printer.addText(string, "工序号", PDFUtil.objectToString(gongxuhao));
				}
				templateList.addAll(list);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
    			if(dwgOs!=null)
    				dwgOs.close();
    		}
        } else if(path.endsWith(".gif")||path.endsWith(".jpg")||path.endsWith(".png")||path.endsWith(".bmp")
        		||path.endsWith(".GIF")||path.endsWith(".JPG")||path.endsWith(".PNG")||path.endsWith(".BMP")){
        	printer.addTempl(templateName, template);
        	templateList.add(templateName);
        	//记录基本数据，比如工艺文件编号等
        	setCommData(printer,templateName);
        	printer.addText(templateName, "工序号", PDFUtil.objectToString(gongxuhao));
            printer.addImage(templateName, "工艺流程框图", path);
        }
		String drawingName = XmlUtility.getAttributeValue(element, "drawingName");
		printer.addText(templateName, "文件名称", drawingName);

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

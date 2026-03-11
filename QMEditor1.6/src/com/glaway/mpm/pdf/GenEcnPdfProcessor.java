package com.glaway.mpm.pdf;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wt.change2.WTChangeOrder2;

import com.glaway.mpm.visual.log.VaLogger;

public class GenEcnPdfProcessor {
	private static VaLogger logger = VaLogger.getLogger(GenEcnPdfProcessor.class);
	private static String techFloder = null;
	private static List<Map<String,List<String>>> all = null;

	public static int start(WTChangeOrder2 changeOrder2, String filePath, String formId, boolean isOpenPdf) throws Exception {
		//加封面
		LcmPdfPrinter printer1 = new LcmPdfPrinter();
		all = new ArrayList<Map<String,List<String>>>();
        int pages = 1;
//		String className1 = getBuildPdfClassName("Form1");
//		Class<?> cls1 = Class.forName(className1);
//		boolean iscls1 = PDFBuilder.class.isAssignableFrom(cls1);
//		if (!iscls1) {
//			throw new Exception("类型[" + cls1 + "]不是-"+ PDFBuilder.class.getName() + "的子类");
//		}
//		Constructor constructor1 = cls1.getConstructor(String.class,String.class);
//		PDFBuilder builder1 = (PDFBuilder) constructor1.newInstance(filePath, "工艺文件封面");
//		techFloder = builder1.getTechFloder();
//		builder1.buildPDF(printer1,null,params);
//		printer1.print(techFloder + File.separator + "PDFPreview.pdf");


		logger.info("Form Builder formId:" + formId);
		String className = getBuildPdfClassName(formId);
		logger.info("Form Builder class:" + className);
		Class<?> cls = Class.forName(className);
		boolean iscls = PDFBuilder.class.isAssignableFrom(cls);
		if (!iscls) {
			throw new Exception("类型[" + cls + "]不是-"+ PDFBuilder.class.getName() + "的子类");
		}
		Constructor constructor = cls.getConstructor(WTChangeOrder2.class, String.class,String.class);
		PDFBuilder builder = (PDFBuilder) constructor.newInstance(changeOrder2, filePath, formId);
		techFloder = builder.getTechFloder();

		builder.buildPDF(printer1,null,null);
		if(!builder.getTemplateList().isEmpty()) {
            Map<String,List<String>> hashM = new HashMap<String,List<String>>();
            hashM.put("工艺更改单", builder.getTemplateList());
            all.add(hashM);
        }
		pages=setCatalog(printer1,all,filePath);
		printer1.print(techFloder + File.separator + "PDFPreview.pdf");
		if(isOpenPdf) {
			openFile();
		}
		return pages;
	}

	private static int setCatalog(LcmPdfPrinter printer,List<Map<String,List<String>>> all,String filePath) {
		//获取PDF总页数
		int pages = 0;
		for (Map<String, List<String>> map : all) {
			pages = pages + map.get(map.keySet().toArray()[0]).size();
		}

		//记录页数
		int count = 1;
		for (int i=0;i<all.size();i++) {
			Map<String,List<String>> map = all.get(i);
			String templateName = (String)map.keySet().toArray()[0];
			//封面，最后处理
		        //遍历当前模板的所有页面，写入页码
		        List<String> tempList = map.get(templateName);
		        for(int j=0;j<tempList.size();j++) {
		        	String pageTemplateName = tempList.get(j);
		        	printer.addText(pageTemplateName, "页码总数", String.valueOf(pages));
					printer.addText(pageTemplateName, "当前页数", String.valueOf(count));
					count++;
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
}

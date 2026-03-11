package com.glaway.mpm.pdf;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.glaway.mpm.visual.log.VaLogger;

public class GenReportTechnicsPdfProcessor {
	private static VaLogger logger = VaLogger.getLogger(GenReportTechnicsPdfProcessor.class);
	private static String techFloder = null;
	private static List<Map<String,List<String>>> all = null;

	public static int start(String name, String formId, String filePath,boolean isOpenPdf,Map<String,String> params) throws Exception {
		//加封面
		LcmPdfPrinter printer1 = new LcmPdfPrinter();
		all = new ArrayList<Map<String,List<String>>>();
        int pages;
		String className1 = getBuildPdfClassName("Form1");
		Class<?> cls1 = Class.forName(className1);
		boolean iscls1 = PDFBuilder.class.isAssignableFrom(cls1);
		if (!iscls1) {
			throw new Exception("类型[" + cls1 + "]不是-"+ PDFBuilder.class.getName() + "的子类");
		}
		Constructor constructor1 = cls1.getConstructor(String.class,String.class);
		PDFBuilder builder1 = (PDFBuilder) constructor1.newInstance(filePath, "工艺文件封面");
		techFloder = builder1.getTechFloder();

		builder1.buildPDF(printer1,null,params);
		printer1.print(techFloder + File.separator + "PDFPreview.pdf");



//		LcmPdfPrinter printer = new LcmPdfPrinter();
		logger.info("Form Builder formId:" + formId);
		String className = getBuildPdfClassName(formId);
		logger.info("Form Builder class:" + className);
		Class<?> cls = Class.forName(className);
		boolean iscls = PDFBuilder.class.isAssignableFrom(cls);
		if (!iscls) {
			throw new Exception("类型[" + cls + "]不是-"+ PDFBuilder.class.getName() + "的子类");
		}
		Constructor constructor = cls.getConstructor(String.class,String.class);
		PDFBuilder builder = (PDFBuilder) constructor.newInstance(filePath, name);
		techFloder = builder.getTechFloder();

		builder.buildPDF(printer1,null,params);
		if(!builder.getTemplateList().isEmpty()) {
            Map<String,List<String>> hashM = new HashMap<String,List<String>>();
            hashM.put(name, builder.getTemplateList());
            all.add(hashM);
        }
		pages=GenTechnicsPdfProcessor.setCatalog(printer1,all,filePath);
		printer1.print(techFloder + File.separator + "PDFPreview.pdf");
		if(isOpenPdf) {
			openFile();
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

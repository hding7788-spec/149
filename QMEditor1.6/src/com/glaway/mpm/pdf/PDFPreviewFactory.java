package com.glaway.mpm.pdf;

import com.glaway.mpm.model.TechnicsOutputFormBean;
import com.glaway.mpm.util.IntfUtil;
import com.glaway.mpm.util.ProcedurePictureCreateUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import gui.ava.html.image.generator.HtmlImageGenerator;
import org.dom4j.Element;
import wt.change2.WTChangeOrder2;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PDFPreviewFactory {

	/**
	 * 工艺预览入口
	 * @param filePath
	 * @param isOpenPdf
	 * @param partList
	 * @return
	 */
	public static int preview(String filePath,boolean isOpenPdf,List<Map<String,String>> partList) {
		int pages;
		if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			pages = previewSopTechnics(filePath,isOpenPdf,partList);
		}else{
			pages = previewCommonTechnics(filePath,isOpenPdf,partList);
		}

		return pages;
	}

	public static int previewTechnics(String filePath,boolean isOpenPdf,List<Map<String,String>> partList) {
		int pages;
		pages = previewCommonTechnics(filePath,isOpenPdf,partList);
		return pages;
	}

	private static int previewCommonTechnics(String filePath,boolean isOpenPdf,List<Map<String,String>> partList){
		int pages=0;
		Element element = XmlUtility.getTechnicsElement(filePath);
		String techType = XmlUtility.getAttributeValue(element, "technicsType");
		String techFormType = XmlUtility.getAttributeValue(element, "isTabular");
		String TechNo = XmlUtility.getAttributeValue(element, "technicsNumber");
		String version = XmlUtility.getAttributeValue(element, "version");
		String[] ecnNoAndBiaoJi  =(String[]) IntfUtil.getPeRemoteMethodInvoke("getChangeNoAndBiaoJiByTechnics",
				new Class[] { String.class ,String.class}, new Object[] { TechNo,version });
		/*String ecnNo =(String) IntfUtil.getPeRemoteMethodInvoke("getChangeNoByTechnics",
				new Class[] { String.class }, new Object[] { TechNo });
		String ecnBiaoJi =(String) IntfUtil.getPeRemoteMethodInvoke("getChangeBiaoJiByTechnics",
				new Class[] { String.class ,String.class}, new Object[] { TechNo,version });*/
		String ecnNo = ecnNoAndBiaoJi[0];
		String ecnBiaoJi = ecnNoAndBiaoJi[1];
		Map<String,String> params = new HashMap<String,String>();
		params.put("ecnNo",ecnNo );
		params.put("ecnBiaoJi",ecnBiaoJi );
		if(techFormType == null || "".equals(techFormType)) {
			techFormType = "非表格化";//旧数据没有此属性，所以默认为"非表格化"
		}
		String techID = XmlUtility.getAttributeValue(element, "PPLANID");
		List<TechnicsOutputFormBean> list = new ArrayList<TechnicsOutputFormBean>();;
		try {
			list = TechnicsIntf.getMPMPPlanOutputFormsXml(techType, techID, techFormType);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
		if(list==null || list.size()==0){
			JOptionPane.showMessageDialog(null, "当前业务不支持"+techFormType+"工艺文件形式", "提示", 1);
			return -1;
		}
		try {
			String xmlFileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1) + ".xml";
			String xmlFilePath = filePath + File.separator + xmlFileName;
			String htmlDir = filePath + File.separator + "htmls";
			String imgDir = filePath + File.separator + "images";
			File fileDir = new File(htmlDir);
			File imageDir = new File(imgDir);
			if (!fileDir.exists()) {
				fileDir.mkdir();
			}else{
				String[] children = fileDir.list();
				for(int i = 0; i < children.length; i++){
					File childFile = new File(fileDir, children[i]);
					childFile.delete();
				}
			}
			if(!imageDir.exists()){
				imageDir.mkdir();
			}else{
				String[] children = imageDir.list();
				for(int i = 0; i < children.length; i++){
					File childFile = new File(imageDir, children[i]);
					childFile.delete();
				}
			}
			//复制文件夹content中的内容
			copyFiles(filePath + File.separator + "content",  htmlDir + File.separator + "content");
			List<String> fileNameList = new ArrayList<String>();
			PDFUtil.createHtmlByXml(element, htmlDir, fileNameList);
			for(String htmlFileName : fileNameList){
				String htmlUrl = "file:///" +htmlDir + File.separator + htmlFileName + ".html";
				String imgPath = imageDir + File.separator + htmlFileName + ".png";
				HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
				imageGenerator.loadUrl(htmlUrl);
				imageGenerator.getBufferedImage();
				imageGenerator.saveAsImage(imgPath);
			}
			pages=GenTechnicsPdfProcessor.start(list, element, filePath, isOpenPdf, partList,params,TechNo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			element.clearContent();
			element = null;
			System.gc();
		}
		return pages;
	}

	/**
	 * 预览SOP工艺
	 * @param filePath
	 * @param isOpenPdf
	 * @param partList
	 * @return
	 */
	private static int previewSopTechnics(String filePath,boolean isOpenPdf,List<Map<String,String>> partList){
		int pages = 0;
		Element element = XmlUtility.getTechnicsElement(filePath);
		String techType = XmlUtility.getAttributeValue(element, "technicsType");
		String techFormType = XmlUtility.getAttributeValue(element, "isTabular");
		String TechNo = XmlUtility.getAttributeValue(element, "technicsNumber");
		String version = XmlUtility.getAttributeValue(element, "version");
		String[] ecnNoAndBiaoJi  =(String[]) IntfUtil.getPeRemoteMethodInvoke("getChangeNoAndBiaoJiByTechnics",
				new Class[] { String.class ,String.class}, new Object[] { TechNo,version });
		Map<String,String> params = new HashMap<String,String>();
		//String ecnNo =(String) IntfUtil.getPeRemoteMethodInvoke("getChangeNoByTechnics", new Class[] { String.class }, new Object[] { TechNo });
		//String ecnBiaoJi =(String) IntfUtil.getPeRemoteMethodInvoke("getChangeBiaoJiByTechnics", new Class[] { String.class }, new Object[] { TechNo });

		params.put("ecnNo",ecnNoAndBiaoJi[0] );
		params.put("ecnBiaoJi",ecnNoAndBiaoJi[1]  );
		if(techFormType == null || "".equals(techFormType)) {
			techFormType = "非表格化";//旧数据没有此属性，所以默认为"非表格化"
		}
		String techID = XmlUtility.getAttributeValue(element, "PPLANID");
		List<TechnicsOutputFormBean> list = new ArrayList<TechnicsOutputFormBean>();;
		try {
			list = TechnicsIntf.getMPMPPlanOutputFormsXml(techType, techID, techFormType);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
		try {
			String xmlFileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1) + ".xml";
			String xmlFilePath = filePath + File.separator + xmlFileName;
			String htmlDir = filePath + File.separator + "htmls";
			String imgDir = filePath + File.separator + "images";
			File fileDir = new File(htmlDir);
			File imageDir = new File(imgDir);
			if (!fileDir.exists()) {
				fileDir.mkdir();
			}else{
				String[] children = fileDir.list();
				for(int i = 0; i < children.length; i++){
					File childFile = new File(fileDir, children[i]);
					childFile.delete();
				}
			}
			if(!imageDir.exists()){
				imageDir.mkdir();
			}else{
				String[] children = imageDir.list();
				for(int i = 0; i < children.length; i++){
					File childFile = new File(imageDir, children[i]);
					childFile.delete();
				}
			}
			//复制文件夹content中的内容
			copyFiles(filePath + File.separator + "content",  htmlDir + File.separator + "content");
			List<String> fileNameList = new ArrayList<String>();
			PDFUtil.createHtmlByXml(element, htmlDir, fileNameList);
			for(String htmlFileName : fileNameList){
				String htmlUrl = "file:///" +htmlDir + File.separator + htmlFileName + ".html";
				String imgPath = imageDir + File.separator + htmlFileName + ".png";
				HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
				imageGenerator.loadUrl(htmlUrl);
				imageGenerator.getBufferedImage();
				imageGenerator.saveAsImage(imgPath);
			}
			pages=GenTechnicsPdfProcessor.startSOP(list, element, filePath, isOpenPdf, partList,params,TechNo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			element.clearContent();
			element = null;
			System.gc();
		}
		return pages;
	}


	/**
	 * 预览工步
	 * @param paceElement
	 */
	public static void previewPace(Element paceElement) {
		try {
			Element stepEle = paceElement.getParent().getParent();
			Element techEle = stepEle.getParent().getParent();
			String technicsNumber = techEle.attributeValue("technicsNumber");
			String technicsName = techEle.attributeValue("technicsName");
			String technicsCategory = techEle.attributeValue("technicsCategory");
			String path = ProcedurePictureCreateUtil.createProcedurePicture(technicsNumber, technicsName, technicsCategory);
			LcmPdfPrinter printer = new LcmPdfPrinter();
			String className = "com.glaway.mpm.pdf.processor.SopForm4PDFBuilder";
			Class<?> cls = Class.forName(className);
			Constructor constructor = cls.getConstructor(Element.class,String.class, String.class, boolean.class);
			PDFBuilder builder = (PDFBuilder) constructor.newInstance(paceElement,path,"操作卡片",true);
			String techFloder = builder.getTechFloder();
			builder.buildPDF(printer,null,null);
			printer.print(techFloder + File.separator + "PacePDFPreview.pdf");
			Runtime.getRuntime().exec("rundll32 url.dll FileProtocolHandler   " + techFloder + File.separator + "PacePDFPreview.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int previewForReport(String filePath,boolean isOpenPdf) {
	    int pages=0;
		Element element = XmlUtility.getTechnicsElement(filePath);
		String techType = XmlUtility.getAttributeValue(element, "technicsType");
		String TechNo = XmlUtility.getAttributeValue(element, "technicsNumber");
		String version = XmlUtility.getAttributeValue(element, "version");
		String[] ecnNoAndBiaoJi  =(String[]) IntfUtil.getPeRemoteMethodInvoke("getChangeNoAndBiaoJiByTechnics",
				new Class[] { String.class ,String.class}, new Object[] { TechNo,version });

		Map<String,String> params = new HashMap<String,String>();
		params.put("ecnNo",ecnNoAndBiaoJi[0] );
		params.put("ecnBiaoJi",ecnNoAndBiaoJi[1] );
//		System.out.println("-----techType---"+techType);
		try {
			Map<String,String> map = TechnicsIntf.getReportMPMPPlanFormIdByXML();
//			System.out.println("-----map---"+map);
			String formId = map.get(techType);
			 pages = GenReportTechnicsPdfProcessor.start(techType, formId, filePath, isOpenPdf,params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pages;
	}
	/**
	 * 复制文件
	 *
	 * @param fromPath
	 * @param toPath
	 * @throws Exception
	 */
	private static void copyFiles(String fromPath, String toPath)
			throws Exception {
		File fromFile = new File(fromPath);
		File toFile = new File(toPath);
		if (fromFile.exists()) {
			if (fromFile.isFile()) {
				File newToFile = new File(toPath);
				newToFile.createNewFile();
				FileInputStream inFile = new FileInputStream(fromFile);
				FileOutputStream outFile = new FileOutputStream(newToFile);
				FileChannel inChannel = inFile.getChannel();
				FileChannel outChannel = outFile.getChannel();
				long bytesWritten = 0;
				long byteCount = inChannel.size();
				while (bytesWritten < byteCount) {
					bytesWritten += inChannel.transferTo(bytesWritten,
							byteCount - bytesWritten, outChannel);
				}
				inFile.close();
				outFile.close();
			} else {
				if (toFile.exists()) {
					File[] info = fromFile.listFiles();
					for (int i = 0; i < info.length; i++) {
						String toPathTemp = toPath + File.separator
								+ info[i].getName();
						copyFiles(info[i].getAbsolutePath(), toPathTemp);//
					}
				} else {
					if (toFile.mkdir()) {
						File[] info = fromFile.listFiles();
						for (int i = 0; i < info.length; i++) {
							String toPathTemp = toPath + File.separator
									+ info[i].getName();
							copyFiles(info[i].getAbsolutePath(), toPathTemp);//
						}
					} else {
					}
				}

			}

		}
	}
	public static int previewForEcn(WTChangeOrder2 changeOrder2, String filePath, boolean isOpenPdf){
		int pages=0;
        try {
        		pages = GenEcnPdfProcessor.start(changeOrder2, filePath, "EcnForm", isOpenPdf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pages;
	}
}

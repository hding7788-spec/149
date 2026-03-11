package com.glaway.mpm.util;

import java.beans.PropertyVetoException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.org.WTUser;
import wt.util.WTException;
import wt.util.WTProperties;
import ext.casc.integrate.util.CldeUtil;
import ext.casc.integrate.util.ZipUtil;

public class SWXMLUtil {

	private SAXBuilder builder;
	private Document document;
	private static String wt_temp;
	private static String zip_temp_dir;

	static {
		try {
			WTProperties pro = WTProperties.getLocalProperties();
			wt_temp = pro.getProperty("wt.temp");
			zip_temp_dir = wt_temp;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SWXMLUtil(InputStream inputStream) {
		try {
			builder = new SAXBuilder();
			document = builder.build(new InputStreamReader(inputStream, "GBK"));
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List getChildren(Element Element, String string) {
		return Element.getChildren(string);

	}

	public Element getRootElement() {
		return document.getRootElement();
	}

	public Document getDocument() {
		return document;
	}


	/**
	 * @author fly
	 * @date  2013-6-1
	 * @param doc
	 * @return
	 * @throws Exception
	 *
	 */
	public static String doc2ToString(Document doc) throws Exception {
		Format format = Format.getCompactFormat();
		format.setEncoding("GBK"); // 设置XML文件的字符

		XMLOutputter outputter = new XMLOutputter(format);// 定义输出
															// ,在元素后换行，每一层元素缩排四格
		StringWriter writer = new StringWriter();// 输出流
		outputter.output(doc, writer);
		String xmlString = writer.toString();
		writer.close();
		return xmlString;
	}

	/**
	 * @author fly
	 * @date  2013-6-1
	 * @param doc
	 * @return
	 * @throws Exception
	 *
	 */
	public  String doc2ToString() throws Exception {
		Format format = Format.getCompactFormat();
		format.setEncoding("GBK"); // 设置XML文件的字符

		XMLOutputter outputter = new XMLOutputter(format);// 定义输出
															// ,在元素后换行，每一层元素缩排四格
		StringWriter writer = new StringWriter();// 输出流
		outputter.output(document, writer);
		String xmlString = writer.toString();
		writer.close();
		return xmlString;
	}

	public static Map<String,String> getReleasedInfo(WTDocument doc) throws WTException, DocumentException, PropertyVetoException {
		ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
		if(data == null) {
			return null;
		}

		String zipFilePath = zip_temp_dir+File.separator+doc.getNumber()+File.separator+doc.getNumber();
		File file = new File(zipFilePath);
		if(!file.exists()) {
			file.mkdirs();
		}

		String xmlFile = zipFilePath+File.separator+doc.getNumber()+".xml";
		byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
		ZipUtil.unZip(bytes, zipFilePath);

        Map<String,String> map = readXML(xmlFile,doc);

		//删除临时文件
		CldeUtil.deleteFiles(new File(zip_temp_dir+File.separator+doc.getNumber()));

		return map;
	}

	public static Map<String,String> readXML(String xmlFile,WTDocument doc) throws DocumentException, WTException {
		File file = new File(xmlFile);
		if(!file.exists()) {
			System.out.println(xmlFile+" is not exist!");
			return null;
		}
		SAXReader reader = new SAXReader();
        org.dom4j.Document document = reader.read(file);
        org.dom4j.Element rootElement = document.getRootElement();
        org.dom4j.Element techEle = (org.dom4j.Element)rootElement.elements().get(0);
        String zfFlag = techEle.attributeValue("ZFFLAG");
        String pplantype = techEle.attributeValue("PPLANTYPE");
        if("Z".equals(zfFlag) && "正式工艺文件".equals(pplantype)) {
        	List<org.dom4j.Element> yclde = rootElement.selectNodes("QMFawTechnicsInfo/CLDE/YCLDE");
            List<org.dom4j.Element> zyclde = rootElement.selectNodes("QMFawTechnicsInfo/CLDE/ZYCLDE");
            List<org.dom4j.Element> sjyclde = rootElement.selectNodes("QMFawTechnicsInfo/CLDE/SJYCLDE");

            Map<String,String> map = new HashMap<String,String>();

            map.put("state", doc.getState().getState().getDisplay(Locale.CHINA));
            map.put("pplanNumber", techEle.attributeValue("pplanNumber"));

    		IBAHelper helper = new IBAHelper(doc);
    		String cldezt = helper.getIBAValue("CLDEZT");
    		if(cldezt != null && !"".equals(cldezt)) {
    			map.put("CLDEZT", cldezt);
    		} else if (!yclde.isEmpty() || !zyclde.isEmpty() || !sjyclde.isEmpty()) {
    			map.put("CLDEZT", "编制中");
    		} else {
    			map.put("CLDEZT", "无");
    		}

            return map;
        } else {
        	return null;
        }

	}

	public static void updateTechnicsInfo(WTDocument doc,WTUser creator) throws Exception {
		ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
		if(data == null) {
			return ;
		}

		String zipFilePath = zip_temp_dir+File.separator+doc.getNumber()+File.separator+doc.getNumber();
		File file = new File(zipFilePath);
		if(!file.exists()) {
			file.mkdirs();
		}

		String xmlFile = zipFilePath+File.separator+doc.getNumber()+".xml";
		byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
		ZipUtil.unZip(bytes, zipFilePath);

		//修改工艺文件信息
		updateInfoForECN(xmlFile,doc,creator);

		//打包修改后的工艺数据文件
		boolean compressflag = ApacheZipUtil.compress(zipFilePath, zip_temp_dir +File.separator+ doc.getNumber() + ".zip");
		File zipFile = new File(zip_temp_dir +File.separator+ doc.getNumber() + ".zip");
		byte[] zipBytes = FileUtil.fileToBytes(new FileInputStream(zipFile));
		if(compressflag){
			WTDocumentUtil.setPrimaryForDocument(doc, doc.getNumber() + ".zip", zipBytes);
		}
		//删除临时文件
		//CldeUtil.deleteFiles(new File(zip_temp_dir+File.separator+doc.getNumber()));
	}

	/**
	 * 用已经签名了的PDF文件替换数据包中旧的PDF文件
	 *
	 * @param doc 工艺规程文档对象
	 * @param newPdf 签名后的PDF文件
	 * @throws Exception
	 */
	public static void repalceTechnicsPdf(WTDocument doc,File newPdf) throws Exception {
		ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
		if(data == null) {
			return ;
		}
		String tempPath = java.util.UUID.randomUUID().toString();
		String zipFilePath = zip_temp_dir+File.separator+tempPath+File.separator+doc.getNumber();
		String deleteFile = zip_temp_dir+File.separator+tempPath;
		File file = new File(zipFilePath);
		if(!file.exists()) {
			file.mkdirs();
		}

		byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
		//解压数据包
		ZipUtil.unZip(bytes, zipFilePath);

		//用已经签名了的PDF文件替换数据包中旧的PDF文件
		//删除旧的PDF文件
		File oldFile = new File(zipFilePath+File.separator+"PDFPreview.pdf");
		if(oldFile.exists()) {
			oldFile.delete();
		}
		//将签名后的PDF拷贝到数据包目录
		File newFile = new File(zipFilePath+File.separator+"PDFPreview.pdf");
		copyFile(newPdf,newFile);

		//打包修改后的工艺数据文件
		boolean compressflag = ApacheZipUtil.compress(zipFilePath, zip_temp_dir +File.separator+tempPath+File.separator+ doc.getNumber() + ".zip");
		File zipFile = new File(zip_temp_dir +File.separator+tempPath+File.separator+ doc.getNumber() + ".zip");
		byte[] zipBytes = FileUtil.fileToBytes(new FileInputStream(zipFile));
		if(compressflag){
			WTDocumentUtil.setPrimaryForDocument(doc, doc.getNumber() + ".zip", zipBytes);
		}

		FileUtil.deleteFile(new File(deleteFile));
		zipFile.delete();
	}

	public static void copyFile(File sourceFile, File targetFile) {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			byte[] b = new byte[5120];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}

			outBuff.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inBuff != null)
				try {
					inBuff.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (outBuff != null)
				try {
					outBuff.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public static void updateInfoForECN(String xmlFile,WTDocument doc,WTUser creator) throws Exception {
		File file = new File(xmlFile);
		if(!file.exists()) {
			System.out.println(xmlFile+" is not exist!");
			return ;
		}
		SAXReader reader = new SAXReader();
        org.dom4j.Document document = reader.read(file);
        org.dom4j.Element root = document.getRootElement();

        org.dom4j.Element technicsEle = (org.dom4j.Element)root.elements().get(0);
        technicsEle.setAttributeValue("technicsNumber", doc.getNumber());

        String version = doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue();
        technicsEle.setAttributeValue("version", version);

        technicsEle.setAttributeValue("creator", creator.getName());
        technicsEle.setAttributeValue("creatorOid", creator.getPersistInfo().getObjectIdentifier().toString());
        technicsEle.setAttributeValue("creatorDisplay", creator.getFullName());

        XmlUtility.saveDocument(document, xmlFile);
	}
}

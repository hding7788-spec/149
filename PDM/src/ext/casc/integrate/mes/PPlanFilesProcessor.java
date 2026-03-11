package ext.casc.integrate.mes;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.util.WTException;
import wt.util.WTProperties;

import com.glaway.mpm.qmIntf.technics.TechnicsPreview;
import com.glaway.mpm.util.ApacheZipUtil;

import ext.casc.integrate.util.BomUtil;
import ext.casc.util.WCUtil;

public class PPlanFilesProcessor {

	private static String wt_temp;

	static {
		try {
			WTProperties pro = WTProperties.getLocalProperties();
			wt_temp = pro.getProperty("wt.temp");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getPartProcessFilesInfo(String processFileNumber,String guid) throws WTException, PropertyVetoException, DocumentException {
		System.out.println("----getPartProcessFilesInfo-----processFileNumber:"+processFileNumber);
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		WTDocument doc = WCUtil.getDocumentByNumber(processFileNumber);
		if(doc != null) {
			QueryResult qr = WTPartHelper.service.getDescribesWTParts(doc);
			if(qr.hasMoreElements()) {
				WTPart part = (WTPart)qr.nextElement();
				List<Element> techList = BomUtil.getZTechnicsDocumentByPart(part,null,"F");
				if(techList != null && !techList.isEmpty()) {
					for (Element techEle : techList) {
						if(techEle != null) {
							buffer.append("<file>");
							buffer.append("<number>");
							buffer.append(techEle.attributeValue("technicsNumber"));
							buffer.append("</number>");
							buffer.append("<pplanNumber>");
							buffer.append(techEle.attributeValue("pplanNumber"));
							buffer.append("</pplanNumber>");
							buffer.append("<name>");
							buffer.append(techEle.attributeValue("pplanName"));
							buffer.append("</name>");
							buffer.append("</file>");
						}
					}
				} else {
					buffer.append("<exception>");
					buffer.append("输入编号的工艺文件没有辅工艺文件！");
					buffer.append("</exception>");
				}
			}
		} else {
			buffer.append("<exception>");
			buffer.append("输入编号的工艺文件文档对象不存在，请重新指定！");
			buffer.append("</exception>");
		}
		return buffer.toString();
	}

	/**
	 * 获取指定工艺文件WEB预览数据包
	 *
	 * @param processFileNumber 工艺文件编号
	 * @param fileVersionType 工艺文件批次
	 * @param fileVersion 工艺文件版本
	 * @param guid 用户ID
	 * @throws WTException
	 */
	public String getProcessFiles(String processFileNumber,String fileVersionType,String fileVersion,String guid) throws WTException {
		StringBuffer result = new StringBuffer();
		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		WTDocument doc = WCUtil.getDocumentByNumber(processFileNumber);
		if(doc != null) {
			String path = wt_temp + File.separator + "IXBExpImp";
			File file = new File(path);
			if(!file.exists()){
				file.mkdirs();
			}
			String fileName = getPartAttachment(doc, path);
			System.out.println("fileName===>" + fileName);
			System.out.println("path======>" + path);
			String previwPath = TechnicsPreview.preview(path + File.separator + fileName, path);
			System.out.println("previwPath======>" + previwPath);
			previwPath = previwPath.substring(0, previwPath.lastIndexOf(File.separator));
			System.out.println("previwPath======>" + previwPath);

			//删除临时压缩包文件
			String zipTempFilePath = path + File.separator +doc.getNumber()+".zip";
			File zipTempFile = new File(zipTempFilePath);
			if(zipTempFile.exists()) {
				zipTempFile.delete();
			}

			//将WEB预览数据打包
			String zipFilePath = previwPath+".zip";
			try {
				ApacheZipUtil.compress(previwPath, zipFilePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

//			zipFilePath = zipFilePath.replace("\\", "/");
			File tempFile = new File(zipFilePath);
			if(tempFile.exists()) {
				result.append("<fileName>");
				result.append(tempFile.getName());
				result.append("</fileName>");
			} else {
				result.append("<fileName>");
				result.append("");
				result.append("</fileName>");
			}
		} else {
			result.append("<fileName>");
			result.append("");
			result.append("</fileName>");
		}

		return result.toString();
	}

	/**
	 * 获取文档的主内容
	 *
	 */
	@SuppressWarnings("deprecation")
	public static String getPartAttachment(WTDocument doc, String path) {
		String fileName = "";
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			if (doc == null) {
				System.out.println("the task of doc===>" + doc);
				return "";
			}
			System.out.println("doc>>>>>" + doc.getNumber() + "   " + doc.getName() + "  "
					+ doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());
			FormatContentHolder holder = (FormatContentHolder) ContentHelper.service.getContents(doc);
			ApplicationData currdata = (ApplicationData) ContentHelper.service.getPrimary(holder);

			is = ContentServerHelper.service.findContentStream(currdata);
			fileName = currdata.getFileName();
			fos = new FileOutputStream(new File(path + File.separator + fileName));
			int i = 0;
			byte abyte[] = new byte[8192];
			while ((i = is.read(abyte, 0, abyte.length)) >= 0) {
				fos.write(abyte, 0, i);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return fileName;
	}

}

package com.glaway.mpm.release;

import com.glaway.mpm.EditorConfig;
import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import com.glaway.mpm.sop.SopUitl;
import com.glaway.mpm.sop.util.SopProcessUtil;
import com.glaway.mpm.util.*;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import com.ptc.extend.ixb.CmExpImpSearchHelper;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.json.JSONObject;
import org.w3c.dom.*;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.util.WTException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.rmi.RemoteException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static ext.ases.envelope.EnvelopeTreeHandler.getHighestWTDocument;

/**
 *
 */

/**
 * @author MosesX
 */
public class ProcessInfoReleaseController {

	// private static Logger logger =
	// LogUtil.getLogger(ProcessInfoReleaseController.class);
	private static String QMFAWTECHNICSINFO = "QMFawTechnicsInfo";
	private static String TECHNICSTYPE = "technicsType";
	private static String PART_TYPE = "零件工艺";
	private static String ASSEMBLE_TYPE = "装配工艺";
	private static String targetPath = "";
	static String xmlFileName;
	static String initTargetPath = System.getProperty("user.home") + File.separator + "3D_PView";

	public static void setTargetPath(String path) {
		if (path != null && !path.equals("")) {
			targetPath = path;
			initTargetPath = path;
		}
	}

	/***
	 * 客户端预览功能
	 *
	 * @param filePath
	 *            文件路径
	 * @throws Exception
	 */
	public static void processInfoPview(String filePath) throws Exception {
		if (filePath == null) {
			return;
		}
		// 初始化保存路径
		targetPath = initTargetPath;
		// 获取工艺xml文件名
		String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1) + ".xml";
		targetPath += File.separator + System.currentTimeMillis();
		// 获取模板路径

		// copy工艺文件&模板文件到指定目录下
		try {
			File targetFile = new File(targetPath);
			if (targetFile.exists()) {
				deleteFile(targetFile);
			}
			createDirs(targetPath);

			copyFiles(filePath, targetPath);
			String path = ProcessInfoReleaseController.class.getResource(ProcessInfoReleaseController.class.getSimpleName() + ".class").getFile();
			if (path.lastIndexOf('!') == -1) {
				String templetesPath = ProcessInfoReleaseController.class.getResource("/com/glaway/mpm/release/templetes").getPath();
				copyFiles(templetesPath, targetPath);
			} else {
				copyFromJar(targetFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		xmlFileName = targetPath + File.separator + fileName;
		System.out.println("-----------xmlFileName----" + xmlFileName);
		downLoadTechnicsPDF(xmlFileName);

		writerFilePrintInftoXml(xmlFileName,true);

		// 处理注释集信息
		checkAnnoAndModifyXML(targetPath);

		replaceProcessContent();

		replaceProcessDescribe();

		String type = checkTechnicsType(xmlFileName);
		System.out.println("-----------type----" + type);
		String Nav_XSL_template = null;
		String Main_XSL_template = null;
		// String Nav_XSL_template = "PartTechTemplate_Nav.xsl";
		// String Main_XSL_template = "PartTechTemplate_Main.xsl";
		// if (ASSEMBLE_TYPE.equals(type)) {
		// Nav_XSL_template = "AssembleTechTemplate_Nav.xsl";
		// Main_XSL_template = "AssembleTechTemplate_Main.xsl";
		// }
		String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
		System.out.println("-----------technicsTypes----" + technicsTypes);
		for (int i = 0; i < technicsTypes[1].length; i++) {
			System.out.println("-----------technicsTypes[1][i]----" + technicsTypes[1][i]);
			if (technicsTypes[1][i].equals(type)) {
				Nav_XSL_template = technicsTypes[4][i] + ".xsl";
				Main_XSL_template = technicsTypes[3][i] + ".xsl";
			}
		}

		if (Nav_XSL_template == null || Main_XSL_template == null) {
			Nav_XSL_template = "defaultTemplate_Nav.xsl";
			Main_XSL_template = "defaultTemplate_Main.xsl";
		}

		System.out.println("-----------Nav_XSL_template----" + Nav_XSL_template);
		System.out.println("-----------Main_XSL_template----" + Main_XSL_template);

		// 生成导航页
		String xslFileName = targetPath + File.separator + "xsl" + File.separator + Nav_XSL_template;
		String htmlFileName = targetPath + File.separator + "nav.html";
		Transform(xmlFileName, xslFileName, htmlFileName);
		// 生成内容页
		String xslFileName1 = targetPath + File.separator + "xsl" + File.separator + Main_XSL_template;
		String htmlFileName1 = targetPath + File.separator + "content.html";
		Transform(xmlFileName, xslFileName1, htmlFileName1);
		// -----------------start-------------add by liangbo---
		String xmlFileNames = filePath.substring(filePath.lastIndexOf(File.separator) + 1) + ".xml";
		String xmlFilePath = filePath + File.separator + xmlFileNames;
		String htmlDir = filePath + File.separator + "htmls";
		String imgDir = filePath + File.separator + "images";
		File fileDir = new File(htmlDir);
		File imageDir = new File(imgDir);
		if (!fileDir.exists()) {
			fileDir.mkdir();
		} else {
			String[] children = fileDir.list();
			for (int i = 0; i < children.length; i++) {
				File childFile = new File(fileDir, children[i]);
				childFile.delete();
			}
		}
		if (!imageDir.exists()) {
			imageDir.mkdir();
		} else {
			String[] children = imageDir.list();
			for (int i = 0; i < children.length; i++) {
				File childFile = new File(imageDir, children[i]);
				childFile.delete();
			}
		}
		// copyFiles(filePath + File.separator + "content", htmlDir +
		// File.separator + "content");
		// List<String> fileNameList = new ArrayList<String>();
		// PDFUtil.createHtmlByXml(xmlFilePath, htmlDir, fileNameList);
		// System.out.println("------开始生成质量图片-------");
		// for(String htmlfileName : fileNameList){
		// String htmlUrl = "file:///" +htmlDir + File.separator + htmlfileName
		// + ".html";
		// String imgPath = imageDir + File.separator + htmlfileName + ".png";
		// HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
		// imageGenerator.loadUrl(htmlUrl);
		// imageGenerator.saveAsImage(imgPath);
		// }
		// System.out.println("------质量图片生成结束-------");
		// ------------------------end----------------------------
		System.out.println("==发布路径==" + targetPath);
		// 浏览器打开预览文件
		openURL(targetPath + File.separator + "ReleasePage.html");
	}

	/**
	 * 读取本地工艺文件xml将典型/通用工艺PDF文件磁盘路径写入xml中
	 *
	 * @param fileName
	 *            工艺文件xml路径
	 * @author 马崇奇
	 * @date 2015-6-3
	 */
	public static void downLoadTechnicsPDF(String fileName) {
		org.dom4j.Document doc = XmlUtility.getDocument(fileName);
		org.dom4j.Element rootTechnics = doc.getRootElement();
		List technicsList = rootTechnics.elements();
		org.dom4j.Element technicsEle = (org.dom4j.Element) technicsList.get(0);
		org.dom4j.Element borrowTechnics = technicsEle.element("borrowTechnicss");
		List<org.dom4j.Element> borrowList = borrowTechnics.elements();
		if (borrowList.size() > 0) {
			for (int i = 0; i < borrowList.size(); i++) {
				org.dom4j.Element borrow = borrowList.get(i);
				String docNumber = borrow.attributeValue("docNumber");
				String tempFile;
				if (WorkSpaceUtil.getWorkSpace().endsWith(File.separator)) {
					tempFile = WorkSpaceUtil.getWorkSpace() + "technics" + File.separator + docNumber + ".pdf";
				} else {
					tempFile = WorkSpaceUtil.getWorkSpace() + File.separator + "technics" + File.separator + docNumber + ".pdf";
				}
				borrow.addAttribute("technicsPath", tempFile);
				writeDocument(doc, xmlFileName);
				downPdf(docNumber);
			}
		}
	}

	/**
	 * 将典型/通用工艺PDF文件下载至本地磁盘
	 *
	 * @param number
	 *            典型/通用工艺编号
	 * @throws FileNotFoundException
	 *             、IOException、InvocationTargetException
	 * @author 马崇奇
	 * @date 2015-6-3
	 */
	public static void downPdf(String number) {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			byte[] bytes = null;
			try {
				bytes = TechnicsIntf.getDocumentPrimary(number);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			if (bytes != null) {
				String tempFile = WorkSpaceUtil.getWorkSpace() + File.separator + "technics" + File.separator + number + ".pdf";
				fos = new FileOutputStream(tempFile);
				bos = new BufferedOutputStream(fos);

				bos.write(bytes);

				bos.close();
				fos.close();

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
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
	}

	/**
	 * 将工艺文件签署信息写入XML文件
	 *
	 * @author 曹磊
	 * @date 2015-10-22
	 */
	public static void writerFilePrintInftoXml(String fileName,boolean isClient) {
		org.dom4j.Document doc = XmlUtility.getDocument(fileName);
		org.dom4j.Element rootTechnics = doc.getRootElement();
		List technicsList = null;
		org.dom4j.Element technicsEle = null;
		if(rootTechnics!=null){
			technicsList = rootTechnics.elements();
			if(technicsList.size()>0){
				technicsEle = (org.dom4j.Element) technicsList.get(0);
			}
		}
		String number = technicsEle.attributeValue("technicsNumber");
		System.out.println("--------88888888888" + number);
		String version = technicsEle.attributeValue("version");
		org.dom4j.Element stepE = technicsEle.element("steps");
		List<org.dom4j.Element> steps = null;
		if(stepE!=null){
			steps = technicsEle.element("steps").elements();
		}
		List<String> sops = new ArrayList<String>();
		for (org.dom4j.Element step : steps) {
			org.dom4j.Element sopE = step.element("sops");
			org.dom4j.Element sop = null;
			if(sopE!=null){
				List sopEs = sopE.elements();
				if(sopEs.size()>0){
					sop = (org.dom4j.Element) sopEs.get(0);
					String sopOid = sop.attributeValue("oid");
					String sopNumber = sop.attributeValue("number");
					String sopName = sop.attributeValue("name");
					if (!sops.contains(sopNumber)) {
						sops.add(sopNumber);
					} else {
						break;
					}
					try {
						String sopURL = null;
						if(isClient){
							SopUitl.getTechnics(sopOid);
							String sopPath = ProcedurePictureCreateUtil.createProcedurePicture(sopNumber, sopName, "SOPDoc");
							sopURL = SopProcessUtil.previewSop2(sopPath);
						}else{
							sopURL = SopUitl.previewSOP(sopOid);
							PropertiesUtil propertiesUtil = new PropertiesUtil();
							String httpCodeBase = propertiesUtil.getHttpCodeBase();

							String code = null;
							boolean flag = false;
							if(System.getProperty("os.name").contains("Windows")){
								code = "UTF-8";
								flag = true;
							}else{
								code = "GBK";
							}
							System.out.println("code===>" + code);
							String afterUrl = sopURL.split("codebase")[1];
							System.out.println("before afterUrl = :" + afterUrl);
							afterUrl = URLEncoder.encode(afterUrl, code);
							System.out.println("afterUrl===>" + afterUrl);
							String reglex = afterUrl.substring(0, afterUrl.indexOf("temp"));
							System.out.println("reglex====>" + reglex);
							afterUrl = afterUrl.replace(reglex, "/");
							if(flag){
								afterUrl = afterUrl.replace("+", "20%");
								afterUrl = afterUrl.replace("20%", "%20");//零件中的空格
							}
							sopURL = httpCodeBase + afterUrl;
						}

						step.addAttribute("sopURL", sopURL);
						writeDocument(doc, xmlFileName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			org.dom4j.Element pacE = step.element("paces");
			List<org.dom4j.Element> paces = null;
			if(pacE!=null){
				paces = step.element("paces").elements();
			}
			List<String> paceSops = new ArrayList<String>();
			for (org.dom4j.Element pace : paces) {
				org.dom4j.Element paceSopE = pace.element("sops");
				org.dom4j.Element paceSop = null;
				if(paceSopE!=null){
					List sopEs = paceSopE.elements();
					if(sopEs.size()>0){
						paceSop = (org.dom4j.Element) sopEs.get(0);
						String sopOid = paceSop.attributeValue("oid");
						String sopNumber = paceSop.attributeValue("number");
						String sopName = paceSop.attributeValue("name");
						if (!paceSops.contains(sopNumber)) {
							paceSops.add(sopNumber);
						} else {
							break;
						}
						try {
							String sopURL = null;
							if(isClient){
								SopUitl.getTechnics(sopOid);
								String sopPath = ProcedurePictureCreateUtil.createProcedurePicture(sopNumber, sopName, "SOPDoc");
								sopURL = SopProcessUtil.previewSop2(sopPath);
							}else{
								sopURL = SopUitl.previewSOP(sopOid);
								PropertiesUtil propertiesUtil = new PropertiesUtil();
								String httpCodeBase = propertiesUtil.getHttpCodeBase();

								String code = null;
								boolean flag = false;
								if(System.getProperty("os.name").contains("Windows")){
									code = "UTF-8";
									flag = true;
								}else{
									code = "GBK";
								}
								System.out.println("code===>" + code);
								String afterUrl = sopURL.split("codebase")[1];
								System.out.println("before afterUrl = :" + afterUrl);
								afterUrl = URLEncoder.encode(afterUrl, code);
								System.out.println("afterUrl===>" + afterUrl);
								String reglex = afterUrl.substring(0, afterUrl.indexOf("temp"));
								System.out.println("reglex====>" + reglex);
								afterUrl = afterUrl.replace(reglex, "/");
								if(flag){
									afterUrl = afterUrl.replace("+", "20%");
									afterUrl = afterUrl.replace("20%", "%20");//零件中的空格
								}
								sopURL = httpCodeBase + afterUrl;
							}

							pace.addAttribute("sopURL", sopURL);
							writeDocument(doc, xmlFileName);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				//照片样张
				org.dom4j.Element pacePhotoE = pace.element("photoRecords");
				if(pacePhotoE!=null){
					List<org.dom4j.Element> photoEs = pacePhotoE.elements();
					if(photoEs.size()>0){
						String stepNumber = step.attributeValue("stepNumber");
						String paceNumber = pace.attributeValue("stepNumber");
						String bsoID = stepNumber+"_"+paceNumber;
						String filePath = fileName.substring(0, fileName.lastIndexOf(File.separator));
						String tecPhotoPath = filePath + File.separator + "photoTemplate" + File.separator + bsoID;
						File tecFile = new File(tecPhotoPath);
						deleteFile(tecFile);
						if(!tecFile.exists()) {
							tecFile.mkdirs();
						}
						for (org.dom4j.Element pacePhoto : photoEs) {
							String localFileName = pacePhoto.attributeValue("localFileName");
							String photoNumber = pacePhoto.attributeValue("photoNumber");
							String photoVersion = pacePhoto.attributeValue("photoVersion");
							String targetName = filePath + File.separator + "photoTemplate" + File.separator + photoNumber;
							if("".equals(localFileName)){
								File photoFile = new File(targetName);
								if(!photoFile.exists()) {
									photoFile.mkdirs();
								}
								try {
									byte[] bytes = TechnicsIntf.getImageBytesByNumberAndVersion(photoNumber, photoVersion);
									if(bytes!=null){
										String photoName = TechnicsIntf.getImageNameByNumberAndVersion(photoNumber, photoVersion);
										ByteArrayInputStream is = new ByteArrayInputStream(bytes);
										writeInputStreamToFile(is,targetName + File.separator + photoName);
									}
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								} catch (RemoteException e) {
									e.printStackTrace();
								}
							}
							try {
								copyFiles(targetName,tecPhotoPath);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						ApacheZipUtil.compress(tecPhotoPath, filePath + File.separator + bsoID + ".zip");
						pace.addAttribute("photoUrl", bsoID + ".zip");
						writeDocument(doc, xmlFileName);
					}
				}
			}

		}

		JSONObject signInfo = null;
		try {
			if(version!=null&&!"".equals(version)){
				String[] ss = version.split("\\.");
				if (EditorConfig.isWebInfLib) {
					WTDocument pbo = (WTDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(WTDocument.class,
							number, ss[0], ss[1]);
					signInfo = ProcessEditorToWCIntfRMI.getFilePrintJson(pbo);
				} else {

				}
			}

			if (signInfo != null) {
				technicsEle.addAttribute("SHEJI", signInfo.optString("SHEJI"));
				technicsEle.addAttribute("JIAODUI", signInfo.optString("JIAODUI"));
				technicsEle.addAttribute("SHENHE", signInfo.optString("SHENHE"));
				technicsEle.addAttribute("NEIBUHUIQIAN", signInfo.optString("NEIBUHUIQIAN"));
				technicsEle.addAttribute("WAIBUHUIQIAN", signInfo.optString("WAIBUHUIQIAN"));
				technicsEle.addAttribute("BIAOSHEN", signInfo.optString("BIAOSHEN"));
				technicsEle.addAttribute("PIZHUN", signInfo.optString("PIZHUN"));
				writeDocument(doc, xmlFileName);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	/**
	 * @param @param document
	 * @param @param filePath
	 * @return void
	 * @throws
	 * @Title: writeDocument
	 * @Description: 将Document元素写到filePath路径下
	 */
	public static void writeDocument(org.dom4j.Document document, String filePath) {
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
			OutputFormat xmlFormat = OutputFormat.createPrettyPrint();
			xmlFormat.setEncoding("GBK");
			XMLWriter xmlWriter = new XMLWriter(fos, xmlFormat);
			xmlWriter.write(document);
			xmlWriter.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/***
	 * 服务器端发布
	 *
	 * @param zipPath
	 *            zip包路径
	 * @param path
	 *            解压解析路径
	 */
	public static String processInfoRelease(String zipPath, String path) {
		try {
			System.out.println("=========================发布Begin==========================");
			// 初始化保存路径
			targetPath = initTargetPath;
			// 1、copy工艺资源文件 模板文件到指定目录
			// copy模板文件到指定目录下
			// 设置目标路径

			// path += File.separator + System.currentTimeMillis();
			setTargetPath(path);
			File pathFile = new File(path);
			if (!pathFile.exists()) {
				createDirs(path);
			}

			File targetFile = new File(targetPath);

			String tempPath = ProcessInfoReleaseController.class.getResource(ProcessInfoReleaseController.class.getSimpleName() + ".class").getFile();

			if (tempPath.lastIndexOf('!') == -1) {
				// 获取模板路径
				String templetesPath = ProcessInfoReleaseController.class.getResource("/com/glaway/mpm/release/templetes").getPath();
				copyFiles(templetesPath, targetPath);
			} else {
				copyFromJar(targetFile);
			}
			// copy资源文件到指定目录
			File zipFile = new File(zipPath);
			if (zipFile.isDirectory()) {
				AntTaskUtil.copydir(zipPath, targetPath);
			} else {
				ApacheZipUtil.decompress(zipPath, targetPath);
			}

			// 获取工艺xml文件名
			String fileName = zipPath.substring(zipPath.lastIndexOf(File.separator) + 1, zipPath.lastIndexOf(".zip")) + ".xml";
			xmlFileName = targetPath + File.separator + fileName;

			ProcedurePictureCreateUtil.createPictureDirectory(targetPath);
			ProcedurePictureCreateUtil.operateDocument(XmlUtility.getDocument(xmlFileName), targetPath);
			System.out.println("=========================xmlFileName==========================");
			writerFilePrintInftoXml(xmlFileName,false);
			// 设计文件
			writeDesignFileInfoToXml(xmlFileName);
			// 处理注释集信息
			checkAnnoAndModifyXML(targetPath);

			replaceProcessContent();

			replaceProcessDescribe();

			String type = checkTechnicsType(xmlFileName);
			String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
			System.out.println("==工艺类型==" + type);
			String Nav_XSL_template = "PartTechTemplate_Nav.xsl";
			String Main_XSL_template = "PartTechTemplate_Main.xsl";
			if (ASSEMBLE_TYPE.equals(type)) {
				Nav_XSL_template = "AssembleTechTemplate_Nav.xsl";
				Main_XSL_template = "AssembleTechTemplate_Main.xsl";
			}

			for (int i = 0; i < technicsTypes[0].length; i++) {
				if (technicsTypes[1][i].equals(type)) {
					Nav_XSL_template = technicsTypes[4][i] + ".xsl";
					Main_XSL_template = technicsTypes[3][i] + ".xsl";
					break;
				}
			}
			System.out.println(Nav_XSL_template);

			// 生成导航页
			String xslFileName = targetPath + File.separator + "xsl" + File.separator + Nav_XSL_template;
			String htmlFileName = targetPath + File.separator + "nav.html";
			Transform(xmlFileName, xslFileName, htmlFileName);
			// 生成内容页
			String xslFileName1 = targetPath + File.separator + "xsl" + File.separator + Main_XSL_template;
			String htmlFileName1 = targetPath + File.separator + "content.html";
			Transform(xmlFileName, xslFileName1, htmlFileName1);

			// // 生成导航页
			// String xslFileName = targetPath + File.separator + "xsl" +
			// File.separator + "PartTechTemplate_Nav.xsl";
			// String htmlFileName = targetPath + File.separator + "nav.html";
			// Transform(xmlFileName, xslFileName, htmlFileName);
			// // 生成内容页
			// String xslFileName1 = targetPath + File.separator + "xsl" +
			// File.separator + "PartTechTemplate_Main.xsl";
			// String htmlFileName1 = targetPath + File.separator +
			// "content.html";
			// Transform(xmlFileName, xslFileName1, htmlFileName1);

			// openURL(targetPath + "\\ReleasePage.html");
			System.out.println("==发布路径==" + targetPath);

			return targetPath + File.separator + "ReleasePage.html";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";

	}

	/**
	 * 将工艺文件关联的设计文件信息放入xml
	 *
	 * @param xmlFileName
	 */
	private static void writeDesignFileInfoToXml(String xmlFileName) throws Exception {
		String modelPath = targetPath + File.separator + "model";
		File modelFilePath = new File(modelPath);
		if (!modelFilePath.exists()) {
			modelFilePath.mkdir();
		}
		org.dom4j.Document doc = XmlUtility.getDocument(xmlFileName);
		org.dom4j.Element rootTechnics = doc.getRootElement();
		org.dom4j.Element technicsEle = rootTechnics.element("QMFawTechnicsInfo");
		String number = technicsEle.attributeValue("technicsNumber");
		String partNumber = technicsEle.attributeValue("partNumber");
		WTPart designPart = WTPartUtil.getLatestPartByNumberAndView(partNumber, "Design");
		Map<String, List> docMap = getAllRelatedDoc(designPart);
		System.out.println("docMap ======  " + docMap);
		org.dom4j.Element designFileEle = technicsEle.addElement("designFile");
		List<String> docNumberList = new ArrayList<String>();
		List<String> epmNumberList = new ArrayList<String>();
		for (Map.Entry<String, List> entry : docMap.entrySet()) {
			String relatedType = entry.getKey();
			List docList = entry.getValue();
			org.dom4j.Element relatedTypeEle = designFileEle.addElement(relatedType);
			for (Object o : docList) {
				if (o instanceof WTDocument) {
					WTDocument wtDocument = (WTDocument) o;
					if (docNumberList.contains(wtDocument.getNumber())) {
						continue;
					} else {
						docNumberList.add(wtDocument.getNumber());
					}
					org.dom4j.Element refrenceDoc = relatedTypeEle.addElement("refrenceDoc");
					refrenceDoc.addAttribute("number", wtDocument.getNumber());
					refrenceDoc.addAttribute("name", wtDocument.getName());
					IBAHelper ibaHelper = new IBAHelper(wtDocument);
					String secret = ibaHelper.getIBAValue("SECRET");
					String printPdfName = "";
					if (secret != null && !"内部".equals(secret) && !"公开".equals(secret)) {
						printPdfName = "locked";
					} else {
						printPdfName = WTPartUtil.downloadPrintPdf(wtDocument, modelPath);
						printPdfName = URLEncoder.encode(printPdfName, "gb2312");
					}
					refrenceDoc.addAttribute("path", "model" + File.separator + printPdfName);
					refrenceDoc.addAttribute("type", "doc");
					refrenceDoc.addAttribute("secret", secret);
					refrenceDoc.addAttribute("pvsName", printPdfName);
				} else if (o instanceof EPMDocument) {
					EPMDocument epmDocument = (EPMDocument) o;
					if (epmNumberList.contains(epmDocument.getNumber())) {
						continue;
					} else {
						epmNumberList.add(epmDocument.getNumber());
					}
					org.dom4j.Element refrenceDoc = relatedTypeEle.addElement("refrenceDoc");
					refrenceDoc.addAttribute("number", epmDocument.getNumber());
					refrenceDoc.addAttribute("name", epmDocument.getName());
					IBAHelper ibaHelper = new IBAHelper(epmDocument);
					String secret = ibaHelper.getIBAValue("SECRET");
					String downloadPath;
					if (epmDocument.getNumber().endsWith(".DRW")) {
						downloadPath = modelPath;
					} else {
						downloadPath = modelPath + File.separator + epmDocument.getNumber();
					}
					File file = new File(downloadPath);
					if (!file.exists()) {
						file.mkdir();
					}

					String pvsFileName = "";
					if (secret != null && !"内部".equals(secret) && !"公开".equals(secret)) {
						pvsFileName = "locked";
					} else {
						if (epmDocument.getNumber().endsWith(".DRW")) {
							pvsFileName = WTPartUtil.downloadPrintPdf(epmDocument, downloadPath);
						} else {
							pvsFileName = WTPartUtil.downloadPvzFile(epmDocument, downloadPath);
						}
						pvsFileName = URLEncoder.encode(pvsFileName, "gb2312");


					}
					if (pvsFileName != null && !pvsFileName.isEmpty()) {
						if (epmDocument.getNumber().endsWith(".DRW")) {
							refrenceDoc.addAttribute("path", "model" + File.separator + pvsFileName);
						} else {
							refrenceDoc.addAttribute("path", "model" + File.separator + epmDocument.getNumber() + File.separator + pvsFileName);
						}
					} else {
						refrenceDoc.addAttribute("path", "");
					}
					refrenceDoc.addAttribute("pvsName", pvsFileName);
					if (epmDocument.getNumber().endsWith(".DRW")) {
						refrenceDoc.addAttribute("type", "doc");
					} else {
						refrenceDoc.addAttribute("type", "epm");
					}
					refrenceDoc.addAttribute("secret", secret);
				}
			}
		}

		writeDocument(doc, xmlFileName);

	}

	private static void replaceProcessDescribe() {
		System.out.println("-------replaceProcessDescribe()----");
		try {
			File file = new File(xmlFileName);
			if (file.exists()) {// 是否存在
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(file);
				XPathFactory xpFactory = XPathFactory.newInstance();
				XPath objXpath = xpFactory.newXPath();

				// 找到所有工序
				XPathExpression exprGX = objXpath.compile("technics/QMFawTechnicsInfo/TechnicsDescribe");
				NodeList nodesGX = (NodeList) exprGX.evaluate(doc, XPathConstants.NODESET);
				if (nodesGX != null && nodesGX.getLength() > 0) {
					for (int i = 0; i < nodesGX.getLength(); i++) {
						Node gxNode = nodesGX.item(i);
						String content = gxNode.getTextContent();
						//System.out.println("-------content----" + content);
						if (content != null && content.length() > 0) {
							// content = content.replace("@#$", "<br>");
							content = content.replace("@#$%^\\", "");
						//	System.out.println("-------content----" + content);
							gxNode.setTextContent(content);
							gxNode.setNodeValue(content);
							gxNode.setTextContent(content);
							gxNode.setNodeValue(content);
						}
					}

					TransformerFactory tff = TransformerFactory.newInstance();
					Transformer tf = tff.newTransformer();
					tf.setOutputProperty(OutputKeys.ENCODING, "GBK");
					tf.setOutputProperty(OutputKeys.INDENT, "yes");
					DOMSource source = new DOMSource(doc);
					xmlFileName = targetPath + File.separator + System.currentTimeMillis() + ".xml";
					File proFile = new File(xmlFileName);
					StreamResult rs = new StreamResult(proFile);
					tf.transform(source, rs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void replaceProcessContent() {
		try {
			File file = new File(xmlFileName);
			if (file.exists()) {// 是否存在
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(file);
				XPathFactory xpFactory = XPathFactory.newInstance();
				XPath objXpath = xpFactory.newXPath();

				XPathExpression expr = objXpath.compile("technics/QMFawTechnicsInfo/steps/QMProcedureInfo/commonParamTables/parameterTable/parameter/values/value/attribute");
				NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
				if (nodes != null && nodes.getLength() > 0) {
					for (int i = 0; i < nodes.getLength(); i++) {
						Node gxNode = nodes.item(i);
						String content = gxNode.getTextContent();
						if (content != null && content.length() > 0) {
							content = content.replace("@#$%^\\", "");
							content = content.replace("\\", "/");
							System.out.println("-------content----" + content);
							content = content.replace("WORKSPACE_PATH/", "");
							gxNode.setTextContent(content);
							gxNode.setNodeValue(content);
							// logger.debug(gxNode.getTextContent());
						}
					}
				}
				// 找到所有工序
				XPathExpression exprGX = objXpath.compile("technics/QMFawTechnicsInfo/steps/QMProcedureInfo/procedureContent");
				NodeList nodesGX = (NodeList) exprGX.evaluate(doc, XPathConstants.NODESET);
				if (nodesGX != null && nodesGX.getLength() > 0) {
					for (int i = 0; i < nodesGX.getLength(); i++) {
						Node gxNode = nodesGX.item(i);
						String content = gxNode.getTextContent();
						if (content != null && content.length() > 0) {
							content = content.replace("@#$%^\\", "");
							content = content.replace("\\", "/");
							System.out.println("-------content----" + content);
							content = content.replace("WORKSPACE_PATH/", "");
							gxNode.setTextContent(content);
							gxNode.setNodeValue(content);
							gxNode.setTextContent(content);
							gxNode.setNodeValue(content);
							// logger.debug(gxNode.getTextContent());
						}
					}
					// 找到所有工步
					XPathExpression exprGB = objXpath.compile("technics/QMFawTechnicsInfo/steps/QMProcedureInfo/paces/QMProcedureInfo/procedureContent");
					NodeList nodesGB = (NodeList) exprGB.evaluate(doc, XPathConstants.NODESET);
					if (nodesGB != null && nodesGB.getLength() > 0) {
						for (int i = 0; i < nodesGB.getLength(); i++) {
							Node gbNode = nodesGB.item(i);
							String content = gbNode.getTextContent();
							if (content != null && content.length() > 0) {
								content = content.replace("@#$%^\\", "");
								content = content.replace("\\", "/");
								System.out.println("-------content----" + content);
								content = content.replace("WORKSPACE_PATH/", "");
								gbNode.setTextContent(content);
								gbNode.setNodeValue(content);
								gbNode.setTextContent(content);
								gbNode.setNodeValue(content);
								// logger.debug(gbNode.getTextContent());
							}
						}
					}

					// 找到工艺状态表的所有数据
					XPathExpression exprZTB = objXpath.compile("technics/QMFawTechnicsInfo/technicsStateTables/technicsStateTable/gyzt");
					NodeList nodesZTB = (NodeList) exprZTB.evaluate(doc, XPathConstants.NODESET);
					if (nodesZTB != null && nodesZTB.getLength() > 0) {
						for (int i = 0; i < nodesZTB.getLength(); i++) {
							Node gbNode = nodesZTB.item(i);
							String content = gbNode.getTextContent();
							if (content != null && content.length() > 0) {
								content = content.replace("@#$%^\\", "");
								content = content.replace("\\", "/");
								System.out.println("-------content----" + content);
								content = content.replace("WORKSPACE_PATH/", "");
								gbNode.setTextContent(content);
								gbNode.setNodeValue(content);
								gbNode.setTextContent(content);
								gbNode.setNodeValue(content);
								// logger.debug(gbNode.getTextContent());
							}
						}
					}

					TransformerFactory tff = TransformerFactory.newInstance();
					Transformer tf = tff.newTransformer();
					tf.setOutputProperty(OutputKeys.ENCODING, "GBK");
					tf.setOutputProperty(OutputKeys.INDENT, "yes");
					DOMSource source = new DOMSource(doc);
					xmlFileName = targetPath + File.separator + System.currentTimeMillis() + ".xml";
					File proFile = new File(xmlFileName);
					StreamResult rs = new StreamResult(proFile);
					tf.transform(source, rs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 模板转换
	 *
	 * @param xmlFileName
	 * @param xslFileName
	 * @param htmlFileName
	 */
	private static void Transform(String xmlFileName, String xslFileName, String htmlFileName) {
		try {
			TransformerFactory tFac = TransformerFactory.newInstance();
			Source xslSource = new StreamSource(xslFileName);
			Transformer t = tFac.newTransformer(xslSource);
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			File xmlFile = new File(xmlFileName);
			File htmlFile = new File(htmlFileName);
			Source source = new StreamSource(xmlFile);
			Result result = new StreamResult(htmlFile);
			t.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 复制文件
	 *
	 * @param fromPath
	 * @param toPath
	 * @throws Exception
	 */
	private static void copyFiles(String fromPath, String toPath) throws Exception {
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
					bytesWritten += inChannel.transferTo(bytesWritten, byteCount - bytesWritten, outChannel);
				}
				inFile.close();
				outFile.close();
			} else {
				if (toFile.exists()) {
					File[] info = fromFile.listFiles();
					for (int i = 0; i < info.length; i++) {
						String toPathTemp = toPath + File.separator + info[i].getName();
						copyFiles(info[i].getAbsolutePath(), toPathTemp);//
					}
				} else {
					if (toFile.mkdir()) {
						File[] info = fromFile.listFiles();
						for (int i = 0; i < info.length; i++) {
							String toPathTemp = toPath + File.separator + info[i].getName();
							copyFiles(info[i].getAbsolutePath(), toPathTemp);//
						}
					} else {
					}
				}

			}

		}
	}

	/**
	 * 打开URL浏览器
	 *
	 * @param url
	 */
	public static void openURL(String url) {
		try {
			browse(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void browse(String url) throws Exception {
		String osName = System.getProperty("os.name", "");
		if (osName.startsWith("Mac OS")) {
			Class fileMgr = Class.forName("com.apple.eio.FileManager");
			Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] { String.class });
			openURL.invoke(null, new Object[] { url });
		} else if (osName.startsWith("Windows")) {
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
		} else {
			String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
			String browser = null;
			for (int count = 0; count < browsers.length && browser == null; count++)
				if (Runtime.getRuntime().exec(new String[] { "which", browsers[count] }).waitFor() == 0)
					browser = browsers[count];
			if (browser == null)
				throw new Exception("Could not find web browser");
			else
				Runtime.getRuntime().exec(new String[] { browser, url });
		}
	}

	/**
	 * 深层创建目录
	 *
	 * @param path
	 * @return
	 */
	private static File createDirs(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * 创建目录
	 *
	 * @param path
	 * @return
	 */
	private static File createDir(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	/**
	 * 从jar中copy文件
	 *
	 * @param sourceFolder
	 */
	private static void copyFromJar(File sourceFolder) {
		try {
			String sourceFolderPath = sourceFolder.getAbsolutePath() + File.separator;
			// String path =
			// ProcessInfoReleaseController.class.getResource("/com/glaway/mpm/release/templetes").getPath();
			String path = ProcessInfoReleaseController.class.getResource(ProcessInfoReleaseController.class.getSimpleName() + ".class").getFile();
			//System.out.println(">>>>>>>>>>>>>path:" + path);
			path = "jar:" + path.substring(0, path.indexOf("!") + 2);
			URL url = new URL(path);
			JarURLConnection con = (JarURLConnection) url.openConnection();
			JarFile jarFile = con.getJarFile();

			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				if (name.startsWith("com/glaway/mpm/release/templetes")) {
					name = name.replace("com/glaway/mpm/release/templetes/", "");
					if (!name.equals("")) {
						if (name.indexOf("/") == -1) {
							writeInputStreamToFile(jarFile.getInputStream(entry), sourceFolderPath + name);
						} else {
							int i = name.lastIndexOf("/");
							String tempPath = name.substring(0, i);
							String fileName = name.substring(i + 1);
							if (!fileName.equals("")) {
								File f = createDir(sourceFolderPath + tempPath);
								writeInputStreamToFile(jarFile.getInputStream(entry), f.getAbsolutePath() + File.separator + fileName);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件
	 *
	 * @param file
	 * @return
	 */
	public static boolean deleteFile(File file) {
		try {
			File[] files = file.listFiles();
			for (File subFile : files) {
				if (subFile.isFile()) {
					subFile.delete();
				} else {
					deleteFile(subFile);
					subFile.delete();
				}
			}
			file.delete();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 写文件
	 *
	 * @param is
	 * @param destPath
	 */
	public static void writeInputStreamToFile(InputStream is, String destPath) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(new FileOutputStream(destPath));
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = bis.read(b)) != -1) {
				bos.write(b, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/***
	 * 判断模板类型
	 *
	 * @param file
	 * @return
	 */
	private static String checkTechnicsType(String file) {
		String type = "";
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(file));
			NodeList nodeList = doc.getDocumentElement().getChildNodes();
			if (nodeList != null) {
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node objNode = nodeList.item(i);
					if (objNode.getNodeName().endsWith(QMFAWTECHNICSINFO)) {
						NamedNodeMap maps = objNode.getAttributes();
						if (maps != null) {
							for (int j = 0; j < maps.getLength(); j++) {
								Node childNode = maps.item(j);
								if (childNode.getNodeName().equals(TECHNICSTYPE)) {
									type = childNode.getNodeValue();
									break;
								}
							}
						}
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// logger.debug("====加载" + type + "模板===");
		return type;

	}

	private static void checkAnnoAndModifyXML(String filePath) {
		try {
			File file = new File(filePath);
			if (file.exists()) {// 是否存在
				File[] childFiles = file.listFiles();
				for (File objF : childFiles) {
					if (objF.isDirectory()) {// 是目录
						if (objF.getName().equals("anno")) {// 是注释集目录
							List<AnnoXmlBean> lstAnnoXmlBean = new ArrayList<AnnoXmlBean>();
							System.out.println("======================解析注释集=======================");
							File[] childsOfObjF = objF.listFiles();
							for (File cObjF : childsOfObjF) {// 工序注释集文件夹
								String gongxuName = cObjF.getName();
								File[] gongxuFiles = cObjF.listFiles();
								for (File gongxuFile : gongxuFiles) {
									String gongbuName = gongxuFile.getName();
									// 遍历工序目录文件夹
									if (gongxuFile.isFile()) {
										// 判断工序下是否存在注释集
										// 存在需要添加至xml中
										// 注释集描述文件
										List<AnnoBean> lstBean = dealwithAnnoFiles(gongxuFile);
										if (!lstBean.isEmpty()) {
											// appendNodes2XML("technics/QMFawTechnicsInfo/steps/QMProcedureInfo/images",
											// lstBean);
											String name = gongxuName.replaceAll("`", ":");
											AnnoXmlBean objAnnoXmlBean = new AnnoXmlBean();
											objAnnoXmlBean.setLstBean(lstBean);
											objAnnoXmlBean.setxPath("technics/QMFawTechnicsInfo/steps/QMProcedureInfo[@bsoID='" + name + "']/images");
											// bsoID="-1109ec5a:14038e2e6dd:-7ffe"
											// bsoID="-1109ec5a:14038e2e6dd:-7ffd"
											lstAnnoXmlBean.add(objAnnoXmlBean);
										}
										// technics\QMFawTechnicsInfo\steps\QMProcedureInfo\images
										// technics\QMFawTechnicsInfo\steps\QMProcedureInfo\paces\QMProcedureInfo\images
									} else {
										// 工步注释集文件夹
										// 判断工步下是否存在注释集
										// 存在需要添加至xml中
										File[] gongbuFile = gongxuFile.listFiles();
										for (File gongbuChildFile : gongbuFile) {
											if (gongbuChildFile.isFile()) {
												List<AnnoBean> lstBean = dealwithAnnoFiles(gongbuChildFile);
												if (!lstBean.isEmpty()) {
													// appendNodes2XML(
													// "technics/QMFawTechnicsInfo/steps/QMProcedureInfo/paces/QMProcedureInfo/images",
													// lstBean);
													String name = gongbuName.replaceAll("`", ":");
													AnnoXmlBean objAnnoXmlBean = new AnnoXmlBean();
													objAnnoXmlBean.setLstBean(lstBean);
													objAnnoXmlBean.setxPath("technics/QMFawTechnicsInfo/steps/QMProcedureInfo/paces/QMProcedureInfo[@bsoID='" + name + "']/images");

													lstAnnoXmlBean.add(objAnnoXmlBean);
												}
											}
										}
									}

								}
							}
							appendNodes2XML(lstAnnoXmlBean);

							break;
						}
					}
				}
			}

		} catch (Exception e) {
			// logger.debug("==注释集解析失败==", e);
			e.printStackTrace();
		}
	}

	/**
	 * xml文件中添加节点
	 *
	 * @param file
	 * @param data
	 */
	// @SuppressWarnings("unused")
	// private static void appendNodes2XML(String file, List<Map<String,
	// String>> data) {
	// try {
	// DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	// DocumentBuilder builder = factory.newDocumentBuilder();
	// Document doc = builder.parse(new File(file));
	// // �����ڵ�
	// Element approveInfos = doc.createElement("approveInfos");
	// for (Map<String, String> value : data) {
	// Element approveInfo = doc.createElement("approveInfo");
	// approveInfo.setAttribute(PROCESS_APPROVE, value.get(PROCESS_APPROVE));
	// approveInfo.setAttribute(PROCESS_PERSON, value.get(PROCESS_PERSON));
	// approveInfo.setAttribute(PROCESS_DATE, value.get(PROCESS_DATE));
	// approveInfos.appendChild(approveInfo);
	// }
	// doc.getDocumentElement().appendChild(approveInfos);
	//
	// TransformerFactory tff = TransformerFactory.newInstance();
	// Transformer tf = tff.newTransformer();
	// tf.setOutputProperty(OutputKeys.ENCODING, "GBK");
	// tf.setOutputProperty(OutputKeys.INDENT, "yes");
	// DOMSource source = new DOMSource(doc);
	// xmlFileName = targetPath + "\\" + System.currentTimeMillis() + ".xml";
	// File proFile = new File(xmlFileName);
	// StreamResult rs = new StreamResult(proFile);
	// tf.transform(source, rs);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * @param gongxuFile
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private static List<AnnoBean> dealwithAnnoFiles(File gongxuFile) throws FileNotFoundException, UnsupportedEncodingException, IOException {
		List<AnnoBean> lstBean = new ArrayList<AnnoBean>();
		String tempName = gongxuFile.getName();
		if (tempName.endsWith(".etb")) {
			InputStream in = new FileInputStream(gongxuFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			if (reader.ready()) {
				String strLine = reader.readLine();
				AnnoBean objAnnoBean = null;
				while (strLine != null) {
					// TODO
					// 路径处理
					String prePath = gongxuFile.getAbsolutePath().substring(gongxuFile.getAbsolutePath().indexOf("anno")).replaceAll(gongxuFile.getName(), "");

					objAnnoBean = new AnnoBean();
					// System.out.println(strLine);
					String[] arrStr = strLine.split("<@@>");
					// System.out.println(arrStr[4] + "---" + arrStr[5]);
					String annoName = arrStr[4];
					String annoPic = prePath + arrStr[5].substring(0, arrStr[5].indexOf(".ast")) + ".gif";
					String annoPvs = prePath + tempName.substring(0, tempName.indexOf(".etb")) + ".pvs";
					// System.out.println(annoName + "___gif=" + annoPic +
					// "___pvs=" + annoPvs);
					// xml中添加元素
					// TODO
					objAnnoBean.setAnnoName(annoName);
					objAnnoBean.setAnnoPic(annoPic);
					objAnnoBean.setAbsolutePath(annoPvs);
					lstBean.add(objAnnoBean);

					strLine = reader.readLine();
				}
			}
		}

		return lstBean;
	}

	private static void appendNodes2XML(List<AnnoXmlBean> lstAnnoXmlBean) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(xmlFileName));
			XPathFactory xpFactory = XPathFactory.newInstance();
			XPath objXpath = xpFactory.newXPath();
			if (!lstAnnoXmlBean.isEmpty()) {
				for (AnnoXmlBean objAnnoXmlBean : lstAnnoXmlBean) {
					XPathExpression expr = objXpath.compile(objAnnoXmlBean.getxPath());
					Object result = expr.evaluate(doc, XPathConstants.NODESET);
					NodeList nodes = (NodeList) result;
					Node targetNode = nodes.item(0);
					if (targetNode != null) {
						for (AnnoBean value : objAnnoXmlBean.getLstBean()) {
							Element objPDrawingInfo = doc.createElement("PDrawingInfo");
							objPDrawingInfo.setAttribute("annoName", value.getAnnoName());
							objPDrawingInfo.setAttribute("annoPic", value.getAnnoPic());
							objPDrawingInfo.setAttribute("absolutePath", value.getAbsolutePath());
							objPDrawingInfo.setAttribute("drawingName", "注释集:" + value.getAnnoName());
							targetNode.appendChild(objPDrawingInfo);
						}
					}
				}
			}

			TransformerFactory tff = TransformerFactory.newInstance();
			Transformer tf = tff.newTransformer();
			tf.setOutputProperty(OutputKeys.ENCODING, "GBK");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			xmlFileName = targetPath + File.separator + System.currentTimeMillis() + ".xml";
			File proFile = new File(xmlFileName);
			StreamResult rs = new StreamResult(proFile);
			tf.transform(source, rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取部件相关文档
	 *
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public static Map<String, List> getAllRelatedDoc(WTPart part) throws WTException {
		Map<String, List> docMap = new HashMap<String, List>();
		if(part==null){
			return docMap;
		}
		List list = new ArrayList();
		// 获取CAD文档
		QueryResult qr0 = PartDocServiceCommand.getAssociatedCADDocuments(part);
		while (qr0.hasMoreElements()) {
			Object obj = qr0.nextElement();
			list.add((Persistable) obj);
			if (obj instanceof EPMDocument) {
				EPMDocument epmDocument = (EPMDocument) obj;
				String name = epmDocument.getCADName().toLowerCase();
				if (name.endsWith(".asm") || name.endsWith(".prt")) {
					// 获取三维模型的参考文档
					List<EPMDocument> referenceEPMDoc = EPMDocumentUtil.getReferenceEPMDoc(epmDocument);
					for (EPMDocument epmDoc : referenceEPMDoc) {
						list.add(epmDoc);
					}
				}

			}
		}
		docMap.put("CAD", list);
		list = new ArrayList();
		// 获取说明文档
		QueryResult qr1 = WTPartHelper.service.getDescribedByWTDocuments(part);
		while (qr1.hasMoreElements()) {
			Object obj = qr1.nextElement();
			if (obj instanceof WTDocument) {
				WTDocument wtDocument = (WTDocument) obj;
				if (wtDocument.getName().endsWith(".xml")) {
					continue;
				}
			}
			list.add((Persistable) obj);
		}
		docMap.put("DES", list);
		list = new ArrayList();
		// 获取参考文档
		QueryResult qr2 = WTPartHelper.service.getReferencesWTDocumentMasters(part);
		while (qr2.hasMoreElements()) {
			Object obj = qr2.nextElement();
			WTDocument doc = getHighestWTDocument((WTDocumentMaster) obj);
			list.add(doc);
		}
		docMap.put("REF", list);
		return docMap;
	}

}

class AnnoBean {
	String annoName;
	String annoPic;
	String absolutePath;

	public String getAnnoName() {
		return annoName;
	}

	public void setAnnoName(String annoName) {
		this.annoName = annoName;
	}

	public String getAnnoPic() {
		return annoPic;
	}

	public void setAnnoPic(String annoPic) {
		this.annoPic = annoPic;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}
}

class AnnoXmlBean {
	String xPath;
	List<AnnoBean> lstBean;

	public String getxPath() {
		return xPath;
	}

	public void setxPath(String xPath) {
		this.xPath = xPath;
	}

	public List<AnnoBean> getLstBean() {
		return lstBean;
	}

	public void setLstBean(List<AnnoBean> lstBean) {
		this.lstBean = lstBean;
	}
}

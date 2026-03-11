package ext.casc.fileprint;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.pdf.PDFPreviewFactory;
import com.glaway.mpm.util.*;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.wvs.server.util.Util;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.constants.Constants;
import ext.casc.doc.DocumentUtil;
import ext.casc.fileprint.cache.GLFilePrintDataHelper;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.util.DocUtil;
import ext.casc.util.IBAHelper;
import ext.casc.workflow.signtrue.zp.SignatureGYZZXMLParser;
import ext.casc.workflow.signtrue.zp.SignatureRecord;
import org.apache.log4j.Logger;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import wt.change2.WTChangeOrder2;
import wt.content.*;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.ReferenceFactory;
import wt.fc.*;
import wt.fc.collections.WTCollection;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipalReference;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.session.SessionServerHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.IterationInfo;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;

import java.beans.PropertyVetoException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FilePrintUtil2 implements RemoteAccess, Serializable {
	public static List<String> allExceptKeys = new ArrayList<String>();
	private static final boolean SERVER = RemoteMethodServer.ServerFlag;
	private static final long serialVersionUID = 1308063005528212814L;
	public static DateFormat df;
	public static final String MAPFILENAME = "mapping.txt";
	public static final String DOCSEPEATOR = "&&&";
	public static final String FILESEPEATOR = ";;;qqq";
	public static final String SOURCEKEY = "SOURCE";
	public static final String PRINTKEY = "PRINT";
	public static final String ATTR = "ATTR";
	public static final String ATTRVALUE = "ATTRVALUE";
	public static final String LOCATIONX = "LOCATIONX";
	public static final String LOCATIONY = "LOCATIONY";
	public static final String OUTPUTPAGE = "OUTPUTPAGE";
	public static final String FONTSIZE = "FONTSIZE";
	public static final String DIRECTION = "DIRECTION";
	public static final String propertiesfile = "ext.casc.fileprint.signtemplate";
	private static final String filePrintProperties = "ext.casc.fileprint.fileprint";
	private static final String technologyProperties = "ext.casc.technology.technologyDoc";
	public static WTProperties wtProperties;
	private static final Logger log;
	private static String wt_temp;
	private static String zip_temp_dir;

	static {
		try {
			allExceptKeys.add("NUMBER");
			allExceptKeys.add("MINDEX");
			allExceptKeys.add("PINDEX");
			allExceptKeys.add("REPLACE");
			allExceptKeys.add("FAWANGDANWEI");
			allExceptKeys.add("TONGZHIBIAOTI");
			allExceptKeys.add("CHGREASON");
			allExceptKeys.add("EFFDATE");
			WTProperties pro = WTProperties.getLocalProperties();
			wt_temp = pro.getProperty("wt.temp");
			zip_temp_dir = wt_temp;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static {
		try {
			log = LogR.getLogger(FilePrintUtil2.class.getName());
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	static {
		try {
			wtProperties = WTProperties.getLocalProperties();
			df = DateFormat.getDateInstance(java.text.DateFormat.SHORT, java.util.Locale.CHINA);
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 *
	 * @param hashtable
	 *            存放签审对象，签审活动的参与人和完成时间 Hashtable<WTObject,Hashtable>
	 * @param overWirte
	 *            true:移除原来的附件
	 * @throws WTException
	 * @throws MissingResourceException
	 * @throws IOException
	 * @throws PropertyVetoException
	 */
	public static void writeReviewtoPDF(Hashtable hashtable, Object pbo) throws MissingResourceException, WTException, IOException, PropertyVetoException {
		String overWriteString = getStrFromProperties("fileprint.overWrite", filePrintProperties);
		boolean overWrite = false;
		int outputpage = 0;
		if (overWriteString.equals("true"))
			overWrite = true;
		int listSize = hashtable.size();
		// 文档签审列表为空时，返回""
		if (listSize <= 0) {
			return;
		}
		Vector allinfo = new Vector();
		String format = "FORMATDRWA4";
		String diretion = "vertical";
		String codebaseLocation = wtProperties.getProperty("wt.codebase.location");
		String fileDir = codebaseLocation + File.separator + "ext" + File.separator + "casc" + File.separator + "temp" + File.separator + "writepdf";

		File file = new File(fileDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		for (Enumeration enum2 = hashtable.keys(); enum2.hasMoreElements();) {
			String s = (String) enum2.nextElement();
			log.debug("-----------s:" + s);
			WTObject obj = (WTObject) getObject(s);
			if (obj instanceof EPMDocument || obj instanceof WTDocument) {

				RevisionControlled rc = (RevisionControlled) obj;
				String version = rc.getVersionIdentifier().getValue();
				String doctype = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(rc);
				String secret = IBAHelper.getIBAStringValue(rc, "SECRET");
				// 获得主要文件名称
				String primaryfileName = getPrimaryFileName((FormatContentHolder) obj);
				// 获得打印稿附件名称
				String printFileName = getQualityFileName((FormatContentHolder) obj, primaryfileName, PRINTKEY);
				printFileName = printFileName.replaceAll("/", "_");
//				if (doctype.contains("casc.sast.149.PROCESS_PLAN") && (secret == null || "".equals(secret) || "null".equals(secret) || "公开".equals(secret))) {
//					printFileName = Util.removeExtension(printFileName) + "（专用）.pdf";
//				} else {
//					printFileName = Util.removeExtension(printFileName) + ".pdf";
//				}
				//20251105 签字版文件名根据文件密级命名
				if(StrUtil.isNotEmpty(secret)) {
					printFileName = Util.removeExtension(printFileName) + "（" + secret + "）.pdf";
				} else {
					printFileName = Util.removeExtension(printFileName) + ".pdf";
				}
				// 获得需要处理的附件文件名
				String targetFileName = getQualityFileName((FormatContentHolder) obj, primaryfileName, SOURCEKEY);
				targetFileName = Util.removeExtension(targetFileName) + ".pdf";

				// 查找需要处理的附件，并且下载到临时目录
				ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) obj);
				Vector apps = ContentHelper.getApplicationData(contentHolder);

				ApplicationData templatedata = getTemplateApplicationData(contentHolder, targetFileName, version);

				if (templatedata == null) {
					log.debug("没有相应的空白pdf模板");
					continue;
				}
				log.debug("----------apps:" + apps);
				for (int j = 0; j < apps.size(); j++) {
					ApplicationData applicationdata;
					applicationdata = (ApplicationData) apps.elementAt(j);
					String fileName = applicationdata.getFileName();
					// 应用数据的角色
					String applicationdataRole = applicationdata.getRole().toString();
					log.debug("--------applicationdataRole:" + applicationdataRole);
					// 不是附件，处理下一个applicationdata. AutoCAD2006 检入的主文件角色是
					// "THUMBNAIL"
					if (!applicationdataRole.equalsIgnoreCase("SECONDARY")) {
						continue;// 不是附件，处理下一个
					}
					log.debug("--------targetFileName:" + targetFileName);
					log.debug("--------fileName:" + fileName);
					// 文件名称是要求签名的PDF附件，那么进行签名
					String temptargetFileName = targetFileName.replaceAll(".pdf", "");
					String tempfileName = fileName.replaceAll(".pdf", "");

					if (targetFileName.equals(fileName) || tempfileName.startsWith(temptargetFileName) || fileName.startsWith("PDFPreview")) {
						InputStream inputstream = ContentServerHelper.service.findContentStream(templatedata);
						// InputStream inputstream =
						// ContentServerHelper.service.findContentStream(applicationdata);
						String finaleTargetFileName = fileDir + File.separator + targetFileName;

						String finalePrintFileName = fileDir + File.separator + printFileName;
						File tempFile = new File(finaleTargetFileName);

						String fileAbsolutePath = tempFile.getAbsolutePath();
						FileOutputStream tout = new FileOutputStream(fileAbsolutePath);
						byte abyte1[] = new byte[2048];
						int k;
						while ((k = inputstream.read(abyte1, 0, abyte1.length)) >= 0) {
							tout.write(abyte1, 0, k);
						}
						tout.close();
						PdfReader reader = new PdfReader(finaleTargetFileName);
						Rectangle pageSize = reader.getPageSize(1);

						Rectangle a0 = PageSize.A0;
						Rectangle a1 = PageSize.A1;
						Rectangle a2 = PageSize.A2;
						Rectangle a3 = PageSize.A3;
						Rectangle a4 = PageSize.A4;
						log.debug("A0 width is: " + a0.getWidth() + ",height is: " + a0.getHeight());
						log.debug("A1 width is: " + a1.getWidth() + ",height is: " + a1.getHeight());
						log.debug("A2 width is: " + a2.getWidth() + ",height is: " + a2.getHeight());
						log.debug("A3 width is: " + a3.getWidth() + ",height is: " + a3.getHeight());
						log.debug("A4 width is: " + a4.getWidth() + ",height is: " + a4.getHeight());

						float width = pageSize.getWidth();
						float height = pageSize.getHeight();
						log.debug("width is: " + width + ",height is: " + height);
						Hashtable signHashtable = (Hashtable) hashtable.get(s);
						log.debug("--------signHashtable:" + signHashtable);
						// 添加多审核处理
						processForMultiReview(signHashtable);

						// 如果是EPM需要获取其文档类型，如果是装配件，不需要签名
						if (obj instanceof EPMDocument) {
							String epmdoctype = ((EPMDocument) obj).getDocType().toString();
							log.debug("--------epmdoctype:" + epmdoctype);
							if (!epmdoctype.equalsIgnoreCase("CADASSEMBLY")) {
								format = "FORMATDRWA4";
								String tufu = PrintUtil.getFrmPageSizeByCad((EPMDocument) obj);
								log.debug("----------tufu:" + tufu);
								if (tufu != null && !"".equals(tufu)) {
									if ("a3".equals(tufu)) {
										format = "FORMATDRWA3";
									} else if ("a2".equals(tufu)) {
										format = "FORMATDRWA2";
									} else if ("a1".equals(tufu)) {
										format = "FORMATDRWA1";
									} else if ("a0".equals(tufu)) {
										format = "FORMATDRWA0";
									} else if ("2a0".equals(tufu)) {
										format = "FORMATDRW2A0";
									} else if ("3a0".equals(tufu)) {
										format = "FORMATDRW3A0";
									} else if ("mingxibiao".equals(tufu)) {
										format = "FORMATDRWMXB";
									}
									log.debug("---------format:" + format);
									allinfo = getDRWSignInfo(signHashtable, format, tufu.toUpperCase(), pbo, obj);
								}
							}
							log.debug("--------allinfo:" + allinfo);
						} else if (obj instanceof WTDocument) {
							try {
								String subtype = IBAHelper.getIBAStringValue(obj, "SUBTYPE");
								String type = IBAHelper.getSoftType(obj);
								log.debug("subtype is: " + subtype);
								log.debug("type is: " + type);

								GLFilePrintDataHelper.cache(signHashtable,obj);

								if (subtype != null & "QA_REPORT".equalsIgnoreCase(type)) {
									format = "FORMATQAA4";
									outputpage = 1;
									allinfo = getDocSignInfo(signHashtable, format, pbo, obj);
								} else if (subtype != null & "RESEARCH_DOC".equalsIgnoreCase(type)) {
									format = "FORMATMYSWJA4";
									outputpage = 1;
									allinfo = getDocSignInfo(signHashtable, format, pbo, obj);
								} else if (type.equalsIgnoreCase("DRAWING_DOC") && Constants.TYPE_TUYANG_LINGJIANTU.equals(subtype)) {// "图样"类型下"零件图"
									if (width >= 594.0 & width < 597.0 & height > 840.0 & height <= 843.0) {
										format = "FORMATDWGA4";
										allinfo = getSignInfo(signHashtable, format, pbo, obj);
									} else if (height > 1189.0 & height <= 1192.0 & width > 840.0 & width <= 843.0) {
										format = "FORMATDWGA3H";
										// diretion = "horizontal";
										allinfo = getSignInfo(signHashtable, format, pbo, obj);
									} else if (height > 1682.0 & height <= 1685.0 & width > 1189.0 & width <= 1192.0) {
										format = "FORMATDWGA2H";
										// diretion = "horizontal";
										allinfo = getSignInfo(signHashtable, format, pbo, obj);
									} else if (height > 2382.0 & height <= 2385.0 & width > 1682.0 & width <= 1685.0) {
										format = "FORMATDWGA1H";
										// diretion = "horizontal";
										allinfo = getSignInfo(signHashtable, format, pbo, obj);
									} else if (height > 3368.0 & height <= 3371.0 & width > 2382.0 & width <= 2385.0) {
										format = "FORMATDWGA0H";
										// diretion = "horizontal";
										allinfo = getSignInfo(signHashtable, format, pbo, obj);
									}
								} else if (type.equalsIgnoreCase("DRAWING_DOC") && Constants.TYPE_TUYANG_MINGXIBIAO.equals(subtype)) {// "图样"类型下"明细表"
									format = "FORMATDWGMXBA4";
									allinfo = getSignInfo(signHashtable, format, pbo, obj);
								} else if (type.equalsIgnoreCase("DRAWING_DOC") && Constants.TYPE_TUYANG_JIEXIANBIAO.equals(subtype)) {// "图样"类型下"接线表"
									format = "FORMATDWGJXBA4";
									allinfo = getSignInfo(signHashtable, format, pbo, obj);
								} else if (type.equalsIgnoreCase("DRAWING_DOC") && Constants.TYPE_TUYANG_YUANJIANBIAO.equals(subtype)) {// "图样"类型下"元件表"
									format = "FORMATDWGYJBA4";
									allinfo = getSignInfo(signHashtable, format, pbo, obj);
								} else if (type.equalsIgnoreCase("TECHNOTICE_DOC")) {
									format = "FORMAT4";
									allinfo = getSignInfo(signHashtable, format, pbo, obj);
								} else if (type.equalsIgnoreCase("PROCESS_NOTICE")) {
									if (isOldPrint(obj)) {
										format = "FORMATPROCESSNOTICE4_OLD";

									} else {
										format = "FORMATPROCESSNOTICE4";
									}

									allinfo = getSignInfo(signHashtable, format, pbo, obj);
								} else if (type.equalsIgnoreCase("TECHNOLOGY_AGREEMENT")) {
									format = "FORMATJISHUXIEYIA4";
									allinfo = getDocSignInfo(signHashtable, format, pbo, obj);
								} else if (type.contains("reportTechnics")) {
									format = "FORMATPROCESSPLANA4";
									allinfo = getProcessPlanDocSignInfo(signHashtable, format, pbo, obj);
								} else if (type.contains("PROCESSPLAN")) {
									format = "FORMATPROCESSPLANA4";
									allinfo = getProcessPlanDocSignInfo(signHashtable, format, pbo, obj);
									// } else if
									// (type.contains("QITALEIWENDANG")) {
									// outputpage = 1;
									// format = "FORMATQAA4";
									// allinfo =
									// getProcessPlanDocSignInfo(signHashtable,
									// format,pbo,obj);
								} else if (type.contains("QITALEIWENDANG")) {
									outputpage = 1;
									format = "FORMATQITALEIWENDANGA4";
									allinfo = getDocSignInfo(signHashtable, format, pbo, obj);
								} else if (type.contains("TY_PROCESS_DOC")) {
									outputpage = 1;
									format = "FORMATTY_PROCESS_DOCA4";
									allinfo = getProcessPlanDocSignInfo(signHashtable, format, pbo, obj);
								} else if (type.equalsIgnoreCase("JISHUKETI")) {
									format = "FORMATFANGANA4";
									allinfo = getDocSignInfo(signHashtable, format, pbo, obj);
								} else if (type.equalsIgnoreCase("GONGYIFENFANGAN")// 分方案
										|| type.equalsIgnoreCase("GONGYIFENXICEHUAZONGJIE")
										|| type.equalsIgnoreCase("GONGYIDINGXING")
										|| type.equalsIgnoreCase("GONGYIJIANDING")
										|| type.equalsIgnoreCase("GONGYIZONGFANGAN")
										|| type.equalsIgnoreCase("TEST_REPORT")) { //总体测发报告
									format = "FORMATFANGANA5";
									allinfo = getDocSignInfo(signHashtable, format, pbo, obj);
								}
								else if (type.contains("SOPDoc")) {
									format = "FORMATSOPDOCA4";
									allinfo = getSOPDocSignInfo(signHashtable, format, pbo, obj);
								} else {
									// format = "FORMATDOCA4";
									format = "FORMATDOCGONGYIA4";
									allinfo = getDocSignInfo(signHashtable, format, pbo, obj);
								}
							} catch (WTException e) {
							}
						}
						// 向pdf写签审信息
						log.debug("给 " + obj.getIdentity() + " 电子签名开始...");
						log.debug("format:" + format);
						log.debug("finaleTargetFileName:" + finaleTargetFileName);
						log.debug("finalePrintFileName:" + finalePrintFileName);
						File printFile = PdfUtilities.writeToPDF(finaleTargetFileName, finalePrintFileName, allinfo, outputpage, diretion, 9, format);
						//如果是工艺技术类文档，将封面与内容合并
						if(isTechnicDoc(obj)){
							printFile = mergeCoverToPdf(obj,printFile);
						}

						// 上载打印稿pdf为附件
						if (printFile != null && printFileName != null) {
							log.debug("printFile.getAbsolutePath():" + printFile.getAbsolutePath());
							log.debug("printFileName:" + printFileName);
							if ((pbo instanceof ProcessEnvelope) || (pbo instanceof WTChangeOrder2)) {
								saveFiletoAttachment((ContentHolder) pbo, printFile.getAbsolutePath(), true, printFileName);
								saveFiletoAttachment((ContentHolder) obj, printFile.getAbsolutePath(), true, printFileName);
							} else {
								saveFiletoAttachment((ContentHolder) obj, printFile.getAbsolutePath(), true, printFileName);
							}

							// 替换可视化的PDF文件
							replaceRepPdfFile((Representable) obj, printFile.getAbsolutePath());

							// 如果是工艺规程文件，则替换数据包原来的PDF文件，去掉 防止xml丢失
							if (obj instanceof WTDocument) {
								String type = IBAHelper.getSoftType(obj);
								if (type.contains("PROCESSPLAN")) {
									try {
										SWXMLUtil.repalceTechnicsPdf((WTDocument) obj, printFile);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}

							// 删除源PDF文件
							if (targetFileName != null && targetFileName.length() > 0) {
								log.debug("------targetFileName:" + targetFileName);
								if ((pbo instanceof ProcessEnvelope) || (pbo instanceof WTChangeOrder2)) {
									// removeAttachment4ChangeOrder((ContentHolder)
									// pbo, targetFileName);
								} else {
									// removeAttachment((Representable) obj,
									// targetFileName);
								}
							}
						}
						log.debug("给 " + obj.getIdentity() + " 电子签名结束...");
						printFile.deleteOnExit();
						tempFile.deleteOnExit();
					}
				}

			} else if (obj instanceof WTChangeOrder2) {
				Hashtable signHashtable = (Hashtable) hashtable.get(s);
				// 添加多审核处理
				processForMultiReview(signHashtable);
				writeReviewtoPDF4ChangeOrder(obj, signHashtable, overWrite, pbo);
			}
			allinfo.clear();
		}
		// throw new WTException("测试用");
		// 清除临时文件
		// cleanTempFile(fileDir);
	}

	/** 
	  * @Description: 获取待加封面的文档 
	  * @date 2025年8月27日上午10:21:52
	  * @author Liluwen
	  * @param finaleTargetFileName
	  * @param finalePrintFileName
	  * @return  
	  * @return 
	*/
	private static File getPrintFile(String finaleTargetFileName, String finalePrintFileName) {
		return null;
	}

	/** 
	  * @Description: 判断当前对象是否为工艺总方案文档
	  * @date 2025年8月27日上午10:20:23
	  * @author Liluwen
	  * @param pbo
	  * @return
	  * @throws RemoteException
	  * @throws WTException  
	  * @return 
	*/
	private static boolean isGongYiZongFangAn(WTObject pbo) throws RemoteException, WTException {
		if(pbo instanceof WTDocument){
			WTDocument document = (WTDocument) pbo;
			String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
			if(docType.indexOf("casc.sast.149.GONGYIZONGFANGAN") != -1){
				return true;
			}
		}
		return false;
	}

	private static boolean isTechnicDoc(Object pbo) throws WTException, RemoteException {
		if(pbo instanceof WTDocument){
			WTDocument document = (WTDocument) pbo;
			String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
			if(docType.indexOf("casc.sast.149.GONGYIFENFANGAN") != -1
					|| docType.indexOf("casc.sast.149.GONGYIFENXICEHUAZONGJIE") != -1
					|| docType.indexOf("casc.sast.149.GONGYIDINGXING") != -1
					|| docType.indexOf("casc.sast.149.GONGYIJIANDING") != -1
					|| docType.indexOf("casc.sast.149.GONGYIZONGFANGAN") != -1
					|| docType.indexOf("casc.sast.149.TEST_REPORT") != -1){
				return true;
			}
		}
		return false;
	}

	/**
	 * 方法功能: 合并封面和内容
	 *
	 * @param pbo
	 * @param printFile
	 * @return java.io.File
	 * @author LB
	 * @date 2020/9/20
	 */
	private static File mergeCoverToPdf(Object pbo, File printFile) throws IOException {
		String mergePdfPath = PropertiesUtil.getTempPath() + File.separator + printFile.getName();
		WTDocument document = (WTDocument) pbo;
		//重新生成封面
		File pdfCoverFile = DocumentUtil.createPdfCover(document);
		PDFMergerUtility mergerUtility = new PDFMergerUtility();
		mergerUtility.addSource(pdfCoverFile);
		mergerUtility.addSource(printFile);
		mergerUtility.setDestinationFileName(mergePdfPath);
		mergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
		return new File(mergePdfPath);
	}

	/**
	 *
	 * @param hashtable
	 *            存放签审对象，签审活动的参与人和完成时间 Hashtable<WTObject,Hashtable>
	 * @param overWirte
	 *            true:移除原来的附件
	 * @throws WTException
	 * @throws MissingResourceException
	 * @throws IOException
	 * @throws PropertyVetoException
	 */
	public static void writeProcessReviewtoPDF(Hashtable hashtable, Object pbo) throws MissingResourceException, WTException, IOException,
			PropertyVetoException {
		Vector allinfo = new Vector();
		String codebaseLocation = wtProperties.getProperty("wt.codebase.location");
		String fileDir = codebaseLocation + File.separator + "ext" + File.separator + "casc" + File.separator + "temp" + File.separator + "writepdf";
		String overWriteString = getStrFromProperties("fileprint.overWrite", filePrintProperties);
		boolean overWrite = false;
		if (overWriteString.equals("true"))
			overWrite = true;
		File file = new File(fileDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		for (Enumeration enum2 = hashtable.keys(); enum2.hasMoreElements();) {
			String s = (String) enum2.nextElement();
			log.debug("-----------s:" + s);
			WTObject obj = (WTObject) getObject(s);
			if (obj instanceof WTDocument) {
				Hashtable signHashtable = (Hashtable) hashtable.get(s);
				// decompressZip((WTDocument)obj,signHashtable);
			} else if (obj instanceof WTChangeOrder2) {
				Hashtable signHashtable = (Hashtable) hashtable.get(s);
				writeReviewtoPDF4ChangeOrder(obj, signHashtable, overWrite, pbo);
			}
			allinfo.clear();
		}
		// throw new WTException("测试用");
		// 清除临时文件
		// cleanTempFile(fileDir);
	}

	public static void decompressZip(WTDocument doc) {
		if (!SERVER) {
			Class[] cla = { WTDocument.class };
			Object[] objs = { doc };
			try {
				RemoteMethodServer.getDefault().invoke("decompressZip", FilePrintUtil2.class.getName(), null, cla, objs);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			InputStream input = null;
			try {
				List<Map<String, String>> signHashtable = new ArrayList<Map<String, String>>();
				if (doc != null) {
					String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
					if (docType.contains("reportTechnics") || docType.contains("PROCESSPLAN")) {
						ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
						if (data == null) {
							return;
						}
						String tempPath = java.util.UUID.randomUUID().toString();

						String docNumber = doc.getNumber();
						if(docNumber.endsWith("A")){
							docNumber =	docNumber.substring(0, docNumber.length()-1);
						}
						String zipFilePath = zip_temp_dir + File.separator + tempPath + File.separator + docNumber;
						File file = new File(zipFilePath);
						if (!file.exists()) {
							file.mkdirs();
						}
						byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
						ZipUtil.unZip(bytes, zipFilePath);

						if (docType.indexOf("reportTechnics") != -1) {//
							PDFPreviewFactory.previewForReport(zipFilePath, false);
						} else {
							PDFPreviewFactory.previewTechnics(zipFilePath, false, signHashtable);
						}

						InputStream is = null;
						try {
							saveFiletoAttachment((ContentHolder) doc, zipFilePath + File.separator + "PDFPreview.pdf", true, "PDFPreview.pdf");
							// 设置默认表示法
							is = new FileInputStream(new File(zipFilePath + File.separator + "PDFPreview.pdf"));
							RepUtils.saveFileRep2(doc, is, "PDFPreview.pdf");

							if(ApacheZipUtil.compress(zipFilePath , zipFilePath +".zip")){
								input = new FileInputStream(zipFilePath +".zip");
								WTDocumentUtil.setPrimaryForDocument(doc, docNumber + ".zip", input);

							}


						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							if (is != null) {
								is.close();
							}
						}
						String state = doc.getState().getState().getDisplay(Locale.CHINA);
						if (state.contains(Constants.STATE_YIPIZHUN)) {
							WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(doc);
							Iterator it = coll.iterator();
							if (it.hasNext()) {
								WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
								FilePrintUtil.writeProcessReviewtoPDF(ecn);
							} else {
								FilePrintUtil.writeProcessReviewtoPDF(doc);
							}
						}
						File deleteFile = new File(zip_temp_dir + File.separator + tempPath);
						// deleteFile.deleteOnExit();
						FileUtil.deleteFile(deleteFile);

						File deleteFile2 = new File(zip_temp_dir + File.separator +docNumber + ".zip");
						deleteFile2.deleteOnExit();
					} else {
						QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(doc, null, null);
						while (qrProcs.hasMoreElements()) {
							WfProcess process = (WfProcess) qrProcs.nextElement();
							if (process.getState().equals(WfState.OPEN_RUNNING)) {
								FilePrintUtil.writeReviewtoPDF2(doc, process);
								break;
							}

						}

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				if(input!=null){
                    try {
                        input.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
			}
		}

		// catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	public static void deAllCompressZip(String type) {
		if (!SERVER) {
			Class[] cla = { String.class };
			Object[] objs = { type };
			try {
				RemoteMethodServer.getDefault().invoke("deAllCompressZip", FilePrintUtil2.class.getName(), null, cla, objs);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			try {
				List<Map<String, String>> signHashtable = new ArrayList<Map<String, String>>();
				List<WTDocument> docs = DocUtil.getAllGongyiWenJian(type);
				for (WTDocument doc : docs) {
					if (doc != null) {
						ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
						if (data == null) {
							return;
						}
						String zipFilePath = zip_temp_dir + File.separator + doc.getNumber() + File.separator + doc.getNumber();
						File file = new File(zipFilePath);
						if (!file.exists()) {
							file.mkdirs();
						}
						byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
						ZipUtil.unZip(bytes, zipFilePath);
						PDFPreviewFactory.previewTechnics(zipFilePath, false, signHashtable);
						try {
							saveFiletoAttachment((ContentHolder) doc, zipFilePath + File.separator + "PDFPreview.pdf", true, "PDFPreview.pdf");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String state = doc.getState().getState().getDisplay(Locale.CHINA);
						if (state.contains(Constants.STATE_YIPIZHUN)) {
							FilePrintUtil.writeProcessReviewtoPDF(doc);
						}
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * 获取打印模板，如果没有，则根据附件生成新的模板附件，名称为tempalte.pdf
	 *
	 * @param contentHolder
	 * @param targetFileName
	 * @return
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws PropertyVetoException
	 * @throws IOException
	 */
	private static ApplicationData getTemplateApplicationData(ContentHolder contentHolder, String targetFileName, String version) throws WTException,
			FileNotFoundException, PropertyVetoException, IOException {
		boolean hasSign = true;
		Representation representation = RepresentationHelper.service.getDefaultRepresentation((Representable) contentHolder);
		if (representation != null) {
			QueryResult qr2 = ContentHelper.service.getContentsByRole(representation, ContentRoleType.SECONDARY);
			while (qr2.hasMoreElements()) {
				ApplicationData applicationdata = (ApplicationData) qr2.nextElement();
				if ("PDF".equalsIgnoreCase(applicationdata.getFormat().getDataFormat().getFormatName().trim())) {
					if (!"已签名".equals(applicationdata.getComments())) {
						hasSign = false;
					}
				}
			}
		}

		Vector apps = ContentHelper.getApplicationData(contentHolder);
		ApplicationData retData = null;
		ApplicationData srcData = null;
		for (int j = 0; j < apps.size(); j++) {
			ApplicationData applicationdata;
			applicationdata = (ApplicationData) apps.elementAt(j);
			String fileName = applicationdata.getFileName();
			// 应用数据的角色
			String applicationdataRole = applicationdata.getRole().toString();
			log.debug("--------applicationdataRole:" + applicationdataRole);
			if (!applicationdataRole.equalsIgnoreCase("SECONDARY")) {
				continue;// 不是附件，处理下一个
			}
			if (fileName.startsWith("PDFPreview")) {
				return applicationdata;
			}
			if (("template.pdf").equals(fileName)) {
				retData = applicationdata;
			}
			if (fileName.equals(targetFileName)) {
				srcData = applicationdata;
			} else {
				String tmpName = targetFileName.replaceAll(".pdf", "");
				if (fileName.equals(tmpName + "(1).pdf")) {
					srcData = applicationdata;
				}
			}

		}
		if ((retData == null && srcData != null) || (!hasSign && srcData != null)) {// 如果第一次签名或者是升版有新的模板pdf且没有签名过，则生成template.pdf附件

			if (retData != null) {
				ContentServerHelper.service.deleteContent(contentHolder, retData);
			}
			retData = srcData;
			boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
			Transaction tx = new Transaction();
			tx.start();
			PersistenceHelper.manager.lockAndRefresh(contentHolder);
			ApplicationData appData = ApplicationData.newApplicationData(contentHolder);
			appData.setFileName("template.pdf");
			appData.setRole(ContentRoleType.SECONDARY);
			appData.setDescription(String.valueOf(Calendar.getInstance().getTimeInMillis()));
			appData.setComments("自动生成");
			InputStream inputstream = ContentServerHelper.service.findContentStream(srcData);
			appData = ContentServerHelper.service.updateContent(contentHolder, appData, inputstream);
			tx.commit();
			tx = null;
			PersistenceHelper.manager.refresh(contentHolder);
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return retData;
	}

	private static String getZZCJ(Object pbo, Object object) {
		String zzcj = null;
		SignatureGYZZXMLParser parser = new SignatureGYZZXMLParser((ContentHolder) pbo);
		String oid = PersistenceHelper.getObjectIdentifier((Persistable) object).toString();
		SignatureRecord record = parser.getMap3().get(oid);
		if (record != null)
			zzcj = record.getZhuzhichejian();
		if (zzcj != null && !"".equals(zzcj)) {
			String[] zzs = zzcj.split("-");
			zzcj = zzs[0];
		}

		if ("1".equals(zzcj)) {
			zzcj = "一车间";
		} else if ("2".equals(zzcj)) {
			zzcj = "二车间";
		} else if ("3".equals(zzcj)) {
			zzcj = "三车间";
		} else if ("4".equals(zzcj)) {
			zzcj = "四车间";
		} else if ("5".equals(zzcj)) {
			zzcj = "五车间";
		} else if ("6".equals(zzcj)) {
			zzcj = "六车间";
		} else if ("7".equals(zzcj)) {
			zzcj = "七车间";
		} else if ("8".equals(zzcj)) {
			zzcj = "八车间";
		} else if ("9".equals(zzcj)) {
			zzcj = "九车间";
		} else if ("项".equals(zzcj)) {
			zzcj = "项目部";
		}
		return zzcj;
	}

	// 用于DRW，对左上角文件编号的处理
	public static Vector getDRWSignInfo(Hashtable hashtable, String format, String tufu, Object pbo, Object object) throws UnsupportedEncodingException,
			MissingResourceException {
		int outputpage;
		String direction;
		float fontsizevalue;
		Vector allinfo = new Vector();

		try {
			String value = "";
			String key = "";
			// 获得输出的页位置
			String outputpagestr = getStrFromProperties(format + ".outputpage", propertiesfile);
			outputpage = Integer.parseInt(outputpagestr);
			// 获得输出文字方向
			direction = getStrFromProperties(format + ".direction", propertiesfile);
			if (direction == null || (direction.length() == 0)) {
				direction = "vertical";
			}
			// 获得输出文字大小
			String fontsize = getStrFromProperties(format + ".fontsize", propertiesfile);
			if (fontsize == null || (fontsize.length() == 0) || fontsize.equals("0")) {
				fontsize = "10";
			}
			fontsizevalue = Float.parseFloat(fontsize);
			// 获得唯一值的属性
			String uniqueValueAttr = getStrFromProperties(format + ".uniquevalueattr", propertiesfile);
			int uniqueValueAttrCount = Integer.parseInt(uniqueValueAttr);

			// 用于记录会签最后的坐标
			String tempy = "0";
			String tempx = "0";
			int tempj = 0;
			String tempyspace = "0";

			for (int i = 1; i <= uniqueValueAttrCount; i++) {
				Hashtable ibas = new Hashtable();
				int size = 0;
				// 获得Key
				key = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".ibaattribute", propertiesfile);
				// 获得key值
				if ((key == null) || (key.length() == 0)) {
					continue;
				} else if (key.equals("HUIQIAN")) {

					String value3 = (String) hashtable.get("WAIBUHUIQIAN");
					String value1 = (String) hashtable.get("NEIBUHUIQIAN");
					String value4 = getHuiQianValue(hashtable, pbo, object);

					// String value6 = (String)
					// hashtable.get("GONGYIHUIQIANHUIZONG");
					String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
					String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
					String yspace = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".yspace", propertiesfile);
					fontsize = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".fontsize", propertiesfile);
					String tempValue = "";
					log.debug("tempValue is:" + tempValue + "|aaaaaaaaaaaaaa");
					tempyspace = yspace;
					if (value1 != null && value1.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null")) {
							tempValue = value1;
						} else {
							tempValue = tempValue + ";" + value1;
						}
					}
					log.debug("tempValue2 is: " + tempValue);
					if (value3 != null && value3.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null")) {
							tempValue = value3;
						} else {
							tempValue = tempValue + ";" + value3;
						}
					}
					log.debug("tempValue3 is: " + tempValue);
					if (value4 != null && value4.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null")) {
							tempValue = value4;
						} else {
							tempValue = tempValue + ";" + value4;
						}
					}
					log.debug("tempValue5 is: " + tempValue);
					// if (value6 != null && value6.length() > 0) {
					// if (tempValue == null || tempValue.trim().equals("") ||
					// tempValue.trim().equals("null")) {
					// tempValue = value6;
					// } else {
					// tempValue = tempValue + ";" + value6;
					// }
					// }
					log.debug("tempValue6 is: " + tempValue);
					if (tempValue == null || tempValue.trim().length() == 0) {
						continue;
					}
					value = tempValue;
					String hqValue[] = value.split(";");
					for (int j = 0; j < hqValue.length; j++) {
						tempj = j;
						String hqSplitValue = hqValue[j];
						// HqSplitValue的格式为 张三/1室/2010-02-03
						String hqSplitValue1[] = hqSplitValue.split("/");
						if (format.equals("FORMATDRWA4") || format.equals("FORMATDRWA3") || format.equals("FORMATDRWA2") || format.equals("FORMATDRWA1")
								|| format.equals("FORMATDRWA0") || format.equals("FORMATDRW2A0") || format.equals("FORMATDRW3A0")
								|| format.equals("FORMATDRWA3") || format.equals("FORMATDRWMXB") || format.equals("149_draw-cover")) {
							Hashtable h = new Hashtable();
							float y = 0;
							if (j == 0) {
								y = Float.parseFloat(locationy);
							} else {
								y = Float.parseFloat(locationy) - Float.parseFloat(yspace);
							}
							locationy = String.valueOf(y);
							// 放入hashtable
							// 先放入部门信息
							h.put(ATTR, key + String.valueOf(j));
							// 修改，避免越界
							if (hqSplitValue1.length >= 2) {
								h.put(ATTRVALUE, hqSplitValue1[1]);
							} else {
								h.put(ATTRVALUE, hqSplitValue);
							}

							h.put(LOCATIONX, locationx);
							h.put(LOCATIONY, locationy);
							h.put(FONTSIZE, fontsize);
							h.put(OUTPUTPAGE, outputpage);
							h.put(DIRECTION, direction);
							allinfo.add(h);
							h = new Hashtable();
							y = Float.parseFloat(locationy) - Float.parseFloat(yspace);
							locationy = String.valueOf(y);
							h.put(ATTR, key + String.valueOf(j));
							// 修改，避免越界
							if (hqSplitValue1.length >= 3) {
								h.put(ATTRVALUE, hqSplitValue1[0] + " " + hqSplitValue1[2]);
							} else {
								h.put(ATTRVALUE, hqSplitValue);
							}

							h.put(LOCATIONX, locationx);
							h.put(LOCATIONY, locationy);
							h.put(FONTSIZE, fontsize);
							h.put(OUTPUTPAGE, outputpage);
							h.put(DIRECTION, direction);
							allinfo.add(h);
							log.debug("key is:" + key + ",value is " + hqSplitValue + ",x is:" + locationx + ",y is" + locationy);
						} else if (format.equals("FORMATDWGA3H") || format.equals("FORMATDWGA2H") || format.equals("FORMATDWGA1H")
								|| format.equals("FORMATDWGA0H")) {
							Hashtable h = new Hashtable();
							float x = 0;
							if (j == 0) {
								x = Float.parseFloat(locationx);
							} else {
								x = Float.parseFloat(locationx) - Float.parseFloat(yspace);
							}
							locationx = String.valueOf(x);
							// 放入hashtable
							// 先放放部门
							h.put(ATTR, key + String.valueOf(j));
							// 修改，避免越界
							if (hqSplitValue1.length >= 2) {
								h.put(ATTRVALUE, hqSplitValue1[1]);
							} else {
								h.put(ATTRVALUE, hqSplitValue);
							}

							h.put(LOCATIONX, locationx);
							h.put(LOCATIONY, locationy);
							h.put(FONTSIZE, fontsize);
							h.put(OUTPUTPAGE, outputpage);
							h.put(DIRECTION, direction);
							allinfo.add(h);
							h = new Hashtable();
							x = Float.parseFloat(locationx) - Float.parseFloat(yspace);
							locationx = String.valueOf(x);
							h.put(ATTR, key + String.valueOf(j));
							// 修改，避免越界
							if (hqSplitValue1.length >= 3) {
								h.put(ATTRVALUE, hqSplitValue1[0] + " " + hqSplitValue1[2]);
							} else {
								h.put(ATTRVALUE, hqSplitValue);
							}
							h.put(LOCATIONX, locationx);
							h.put(LOCATIONY, locationy);
							h.put(FONTSIZE, fontsize);
							h.put(OUTPUTPAGE, outputpage);
							h.put(DIRECTION, direction);
							allinfo.add(h);
							log.debug("key is:" + key + ",value is " + hqSplitValue + ",x is:" + locationx + ",y is:" + locationy);
						}
						size++;
					}

					tempx = locationx;
					tempy = locationy;
				} else {
					value = (String) hashtable.get(key);// 获得签审信息，要写入PDF的内容
					if (value != null && value.length() > 0) {
						// 获得坐标
						String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
						String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
						fontsize = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".fontsize", propertiesfile);
						log.debug("key is:" + key + ",value is " + value + ",x is:" + locationx + ",y is:" + locationy);

						if (!key.equals("FILENUMBER") && value.indexOf("/") > -1 && !key.equals("GONGYIHUIQIANSHIJIAN")) {
							String value1[] = value.split("/");
							// 放入hashtable
							if (value1.length >= 3) {
								ibas.put(ATTRVALUE, value1[0] + " " + value1[2]);
							} else {
								ibas.put(ATTRVALUE, value);
							}
						} else {
							ibas.put(ATTRVALUE, value);
						}

						if (key.equals("GONGYIHUIQIANSHIJIAN")) {
							value = getGongYiValue(hashtable, pbo, object);
							if (value.contains(";")) {
								String value1[] = value.split(";");
								for (int n = 0; n < value1.length; n++) {
									ibas = new Hashtable();
									String str = value1[n];
									String value2[] = str.split("/");
									// 修改，避免越界
									if (value2.length >= 2) {
										ibas.put(ATTRVALUE, value2[1]);
									} else {
										ibas.put(ATTRVALUE, str);
									}
									if (n == 0) {
										// ibas.put(ATTR, key);
										// ibas.put(LOCATIONX, locationx);
										// ibas.put(LOCATIONY, locationy);
										// ibas.put(FONTSIZE, fontsize);
										// ibas.put(OUTPUTPAGE, outputpage);
										// ibas.put(DIRECTION, direction);
										// allinfo.add(ibas);
									} else {
										tempj = tempj + 1;
										tempy = String.valueOf(Float.parseFloat(tempy) - Float.parseFloat(tempyspace));
										ibas.put(ATTR, "HUIQIAN" + tempj);
										ibas.put(LOCATIONX, tempx);
										ibas.put(LOCATIONY, tempy);
										ibas.put(FONTSIZE, fontsize);
										ibas.put(OUTPUTPAGE, outputpage);
										ibas.put(DIRECTION, direction);
										allinfo.add(ibas);
									}
									ibas = new Hashtable();
									// 放入hashtable
									if (value2.length >= 3) {
										ibas.put(ATTRVALUE, value2[0] + " " + value2[2]);
									} else {
										ibas.put(ATTRVALUE, str);
									}
									if (n == 0) {
										ibas.put(ATTR, key);
										ibas.put(LOCATIONX, locationx);
										ibas.put(LOCATIONY, locationy);
										ibas.put(FONTSIZE, fontsize);
										ibas.put(OUTPUTPAGE, outputpage);
										ibas.put(DIRECTION, direction);
										allinfo.add(ibas);
									} else {
										// tempj = tempj+1;
										tempy = String.valueOf(Float.parseFloat(tempy) - Float.parseFloat(tempyspace));
										ibas.put(ATTR, "HUIQIAN" + tempj);
										ibas.put(LOCATIONX, tempx);
										ibas.put(LOCATIONY, tempy);
										ibas.put(FONTSIZE, fontsize);
										ibas.put(OUTPUTPAGE, outputpage);
										ibas.put(DIRECTION, direction);
										allinfo.add(ibas);
									}
								}

							} else {
								String value1[] = value.split("/");
								// 放入hashtable
								if (value1.length >= 3) {
									ibas.put(ATTRVALUE, value1[0] + " " + value1[2]);
								} else {
									ibas.put(ATTRVALUE, value);
								}
								ibas.put(ATTR, key);
								ibas.put(LOCATIONX, locationx);
								ibas.put(LOCATIONY, locationy);
								ibas.put(FONTSIZE, fontsize);
								ibas.put(OUTPUTPAGE, outputpage);
								ibas.put(DIRECTION, direction);
								allinfo.add(ibas);
							}
						} else {
							ibas.put(ATTR, key);
							ibas.put(LOCATIONX, locationx);
							ibas.put(LOCATIONY, locationy);
							ibas.put(FONTSIZE, fontsize);
							ibas.put(OUTPUTPAGE, outputpage);
							ibas.put(DIRECTION, direction);
							allinfo.add(ibas);
						}

					}
				}
			}
			// Hashtable ibas = new Hashtable();
			// String locationx = getStrFromProperties(format + ".filenumber." +
			// tufu + ".x", propertiesfile);
			// String locationy = getStrFromProperties(format + ".filenumber." +
			// tufu + ".y", propertiesfile);
			// ibas.put(ATTR, "FILENUMBER");
			// ibas.put(ATTRVALUE, hashtable.get("FILENUMBER"));
			// ibas.put(LOCATIONX, locationx);
			// ibas.put(LOCATIONY, locationy);
			// ibas.put(FONTSIZE, fontsize);
			// ibas.put(OUTPUTPAGE, outputpage);
			// ibas.put(DIRECTION, direction);
			// allinfo.add(ibas);
		} catch (WTException e) {
		}
		return allinfo;
	}

	private static String getHuiQianValue(Hashtable hashtable, Object pbo, Object object) {
		String zzcj = getZZCJ(pbo, object);
		String value1 = (String) hashtable.get("GONGYIHUIQIAN");
		String value2 = (String) hashtable.get("GONGYIHUIQIANSHIJIAN");
		String value = "";
		if (value1 != null && !"".equals(value1)) {
			value = value1;
		}
		if (value2 != null && !"".equals(value2)) {
			if (!"".equals(value)) {
				value = value + ";" + value2;
			} else {
				value = value2;
			}
		}
		String retValue = "";
		if (zzcj != null && !"".equals(zzcj)) {
			if (value.contains(";")) {
				String[] split = value.split(";");
				for (String s : split) {
					if (!"".equals(s) && !s.contains(zzcj)) {
						retValue = retValue + s + ";";
					}
				}
			} else {
				if (!"".equals(value) && !value.contains(zzcj)) {
					retValue = value + ";";
				}
			}

		}
		return retValue;
	}

	private static String getGongYiValue(Hashtable hashtable, Object pbo, Object object) {
		String zzcj = getZZCJ(pbo, object);
		String value1 = (String) hashtable.get("GONGYIHUIQIAN");
		String value2 = (String) hashtable.get("GONGYIHUIQIANSHIJIAN");
		String value = value1 + ";" + value2;
		String retValue = "";
		if (zzcj != null && !"".equals(zzcj)) {
			String[] split = value.split(";");
			for (String s : split) {
				if (!"".equals(s) && s.contains(zzcj)) {
					retValue = s;
				}
			}
		}
		return retValue;
	}

	// 用于DWG，对左上解文件编号的处理，直接签字到DWG文件用
	public static Vector getDWGSignInfo(Hashtable hashtable, String format, String tufu) throws UnsupportedEncodingException, MissingResourceException {
		int outputpage;
		String direction;
		float fontsizevalue;
		Vector allinfo = new Vector();
		try {
			String value = "";
			String key = "";
			String times = "";
			boolean flag = false;
			// 获得输出的页位置
			String outputpagestr = getStrFromProperties(format + ".outputpage", propertiesfile);
			outputpage = Integer.parseInt(outputpagestr);
			// 获得输出文字方向
			direction = getStrFromProperties(format + ".direction", propertiesfile);
			if (direction == null || (direction.length() == 0))
				direction = "vertical";

			// 获得输出文字大小
			String fontsize = getStrFromProperties(format + ".fontsize", propertiesfile);
			if (fontsize == null || (fontsize.length() == 0) || fontsize.equals("0"))
				fontsize = "10";
			fontsizevalue = Float.parseFloat(fontsize);

			// 获得唯一值的属性
			String uniqueValueAttr = getStrFromProperties(format + ".uniquevalueattr", propertiesfile);
			int uniqueValueAttrCount = Integer.parseInt(uniqueValueAttr);

			for (int i = 1; i <= uniqueValueAttrCount; i++) {
				Hashtable ibas = new Hashtable();
				int size = 0;
				// 获得Key
				key = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".ibaattribute", propertiesfile);
				// 获得key值
				if ((key == null) || (key.length() == 0))
					continue;
				else if (key.equals("HUIQIAN")) {
					String value1 = (String) hashtable.get("NEIBUHUIQIAN");
					String value2 = (String) hashtable.get("NEIBUGONGYIHUIQIAN");
					String value3 = (String) hashtable.get("WAIBUHUIQIAN");
					String value4 = (String) hashtable.get("GONGYIHUIQIAN");
					String value5 = (String) hashtable.get("WAIBUGONGYIHUIQIAN");
					String value6 = (String) hashtable.get("GONGYIHUIQIANHUIZONG");
					// String locationx =
					// getStrFromProperties(format+".uniquevalueattr"+Integer.toString(i)+".x",propertiesfile);
					// 使用filenumber的位置
					String locationx = getStrFromProperties(format + ".filenumber." + tufu + ".x", propertiesfile);
					String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
					String yspace = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".yspace", propertiesfile);
					fontsize = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".fontsize", propertiesfile);
					String tempValue = "";
					log.debug("tempValue is:" + tempValue + "|aaaaaaaaaaaaaa");
					if (value1 != null && value1.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null"))
							tempValue = value1;
						else
							tempValue = tempValue + ";" + value1;
					}
					log.debug("tempValue1 is: " + tempValue);
					if (value2 != null && value2.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null"))
							tempValue = value2;
						else
							tempValue = tempValue + ";" + value2;
					}
					log.debug("tempValue2 is: " + tempValue);
					if (value3 != null && value3.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null"))
							tempValue = value3;
						else
							tempValue = tempValue + ";" + value3;
					}
					log.debug("tempValue3 is: " + tempValue);
					if (value4 != null && value4.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null"))
							tempValue = value4;
						else
							tempValue = tempValue + ";" + value4;
					}
					log.debug("tempValue4 is: " + tempValue);
					if (value5 != null && value5.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null"))
							tempValue = value5;
						else
							tempValue = tempValue + ";" + value5;
					}
					log.debug("tempValue5 is: " + tempValue);
					if (value6 != null && value6.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null"))
							tempValue = value6;
						else
							tempValue = tempValue + ";" + value6;
					}
					log.debug("tempValue6 is: " + tempValue);
					if (tempValue == null || tempValue.trim().length() == 0)
						continue;
					value = tempValue;
					String hqValue[] = value.split(";");
					for (int j = 0; j < hqValue.length; j++) {
						String hqSplitValue = hqValue[j];
						// HqSplitValue的格式为 张三/1室/2010-02-03
						String hqSplitValue1[] = hqSplitValue.split("/");
						if (format.equals("FORMATDWG")) {
							Hashtable h = new Hashtable();
							float y = 0;
							if (j == 0)
								y = Float.parseFloat(locationy);
							else
								y = Float.parseFloat(locationy) - Float.parseFloat(yspace);
							locationy = String.valueOf(y);
							// 放入hashtable
							// 先放入部门信息
							h.put(ATTR, key + String.valueOf(j));
							// ywu 2010.11.18
							// 修改，避免越界
							if (hqSplitValue1.length >= 2)
								h.put(ATTRVALUE, hqSplitValue1[1]);
							else
								h.put(ATTRVALUE, hqSplitValue);
							// ywu end

							h.put(LOCATIONX, locationx);
							h.put(LOCATIONY, locationy);
							h.put(FONTSIZE, fontsize);
							h.put(OUTPUTPAGE, outputpage);
							h.put(DIRECTION, direction);
							allinfo.add(h);
							h = new Hashtable();
							y = Float.parseFloat(locationy) - Float.parseFloat(yspace);
							locationy = String.valueOf(y);
							h.put(ATTR, key + String.valueOf(j));
							// ywu 2010.11.18
							// 修改，避免越界
							if (hqSplitValue1.length >= 3)
								h.put(ATTRVALUE, hqSplitValue1[0] + " " + hqSplitValue1[2]);
							else
								h.put(ATTRVALUE, hqSplitValue);
							// ywu end

							h.put(LOCATIONX, locationx);
							h.put(LOCATIONY, locationy);
							h.put(FONTSIZE, fontsize);
							h.put(OUTPUTPAGE, outputpage);
							h.put(DIRECTION, direction);
							allinfo.add(h);
							log.debug("key is:" + key + ",value is " + hqSplitValue + ",x is:" + locationx + ",y is" + locationy);
						} else if (format.equals("FORMATDWGA3H") || format.equals("FORMATDWGA2H") || format.equals("FORMATDWGA1H")
								|| format.equals("FORMATDWGA0H")) {
							Hashtable h = new Hashtable();
							float x = 0;
							if (j == 0)
								x = Float.parseFloat(locationx);
							else
								x = Float.parseFloat(locationx) - Float.parseFloat(yspace);
							locationx = String.valueOf(x);
							// 放入hashtable
							// 先放放部门
							h.put(ATTR, key + String.valueOf(j));
							// ywu 2010.11.18
							// 修改，避免越界
							if (hqSplitValue1.length >= 2)
								h.put(ATTRVALUE, hqSplitValue1[1]);
							else
								h.put(ATTRVALUE, hqSplitValue);
							// ywu end

							h.put(LOCATIONX, locationx);
							h.put(LOCATIONY, locationy);
							h.put(FONTSIZE, fontsize);
							h.put(OUTPUTPAGE, outputpage);
							h.put(DIRECTION, direction);
							allinfo.add(h);
							h = new Hashtable();
							x = Float.parseFloat(locationx) - Float.parseFloat(yspace);
							locationx = String.valueOf(x);
							h.put(ATTR, key + String.valueOf(j));

							// ywu 2010.11.18
							// 修改，避免越界
							if (hqSplitValue1.length >= 3)
								h.put(ATTRVALUE, hqSplitValue1[0] + " " + hqSplitValue1[2]);
							else
								h.put(ATTRVALUE, hqSplitValue);
							// ywu end

							h.put(LOCATIONX, locationx);
							h.put(LOCATIONY, locationy);
							h.put(FONTSIZE, fontsize);
							h.put(OUTPUTPAGE, outputpage);
							h.put(DIRECTION, direction);
							allinfo.add(h);
							log.debug("key is:" + key + ",value is " + hqSplitValue + ",x is:" + locationx + ",y is" + locationy);
						}
						size++;
					}
				} else {
					value = (String) hashtable.get(key);// 获得签审信息，要写入PDF的内容
					if (value != null && value.length() > 0) {
						// 获得坐标
						String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
						String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
						fontsize = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".fontsize", propertiesfile);
						log.debug("key is:" + key + ",value is " + value + ",x is:" + locationx + ",y is" + locationy);
						/*
						 * if(key.startsWith("JDBJ")||key.equals("BIAOZHI")){
						 * //放入hashtable ibas.put(ATTR,key);
						 * ibas.put(ATTRVALUE,value);
						 * ibas.put(LOCATIONX,locationx);
						 * ibas.put(LOCATIONY,locationy);
						 * ibas.put(FONTSIZE,fontsize);
						 * ibas.put(OUTPUTPAGE,outputpage);
						 * ibas.put(DIRECTION,direction); allinfo.add(ibas);
						 * }else{ String value1[] = value.split("/");
						 * //放入hashtable ibas.put(ATTR,key);
						 * ibas.put(ATTRVALUE,value1[0] + " " + value1[2]);
						 * ibas.put(LOCATIONX,locationx);
						 * ibas.put(LOCATIONY,locationy);
						 * ibas.put(FONTSIZE,fontsize);
						 * ibas.put(OUTPUTPAGE,outputpage);
						 * ibas.put(DIRECTION,direction); allinfo.add(ibas); }
						 */
						if (!key.equals("FILENUMBER") && value.indexOf("/") > -1) {
							String value1[] = value.split("/");
							// 放入hashtable
							if (value1.length >= 3)
								ibas.put(ATTRVALUE, value1[0] + " " + value1[2]);
							else
								ibas.put(ATTRVALUE, value);
						} else {
							ibas.put(ATTRVALUE, value);
						}
						ibas.put(ATTR, key);
						ibas.put(LOCATIONX, locationx);
						ibas.put(LOCATIONY, locationy);
						ibas.put(FONTSIZE, fontsize);
						ibas.put(OUTPUTPAGE, outputpage);
						ibas.put(DIRECTION, direction);
						allinfo.add(ibas);
					}
				}
			}
			Hashtable ibas = new Hashtable();
			String locationx = getStrFromProperties(format + ".filenumber." + tufu + ".x", propertiesfile);
			String locationy = getStrFromProperties(format + ".filenumber." + tufu + ".y", propertiesfile);
			ibas.put(ATTR, "FILENUMBER");
			ibas.put(ATTRVALUE, hashtable.get("FILENUMBER"));
			ibas.put(LOCATIONX, locationx);
			ibas.put(LOCATIONY, locationy);
			ibas.put(FONTSIZE, fontsize);
			ibas.put(OUTPUTPAGE, outputpage);
			ibas.put(DIRECTION, direction);
			allinfo.add(ibas);
		} catch (WTException e) {
		}
		return allinfo;
	}

	public static Vector getSignInfo(Hashtable hashtable, String format, Object pbo, WTObject obj) throws UnsupportedEncodingException,
			MissingResourceException {
		int outputpage;
		String direction;
		float fontsizevalue;
		Vector allinfo = new Vector();
		try {
			String value = "";
			String key = "";
			String times = "";
			boolean flag = false;
			// 获得输出的页位置
			String outputpagestr = getStrFromProperties(format + ".outputpage", propertiesfile);
			outputpage = Integer.parseInt(outputpagestr);
			// 获得输出文字方向
			direction = getStrFromProperties(format + ".direction", propertiesfile);
			if (direction == null || (direction.length() == 0)) {
				direction = "vertical";
			}
			// 获得输出文字大小
			String fontsize = getStrFromProperties(format + ".fontsize", propertiesfile);
			if (fontsize == null || (fontsize.length() == 0) || fontsize.equals("0")) {
				fontsize = "10";
			}
			fontsizevalue = Float.parseFloat(fontsize);

			// 用于记录会签最后的坐标
			String tempy = "0";
			String tempx = "0";

			int tempj = 0;
			String tempyspace = "0";

			// 获得唯一值的属性
			String uniqueValueAttr = getStrFromProperties(format + ".uniquevalueattr", propertiesfile);
			int uniqueValueAttrCount = Integer.parseInt(uniqueValueAttr);

			for (int i = 1; i <= uniqueValueAttrCount; i++) {
				Hashtable ibas = new Hashtable();
				int size = 0;
				// 获得Key
				key = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".ibaattribute", propertiesfile);
				// 获得key值
				if ((key == null) || (key.length() == 0)) {
					continue;
				} else if (key.equals("HUIQIAN")) {
					String value1 = (String) hashtable.get("NEIBUHUIQIAN");
					// String value2 = (String)
					// hashtable.get("NEIBUGONGYIHUIQIAN");
					String value3 = (String) hashtable.get("WAIBUHUIQIAN");
					String value4 = getHuiQianValue(hashtable, pbo, obj);
					// String value5 = getGongYiValue(hashtable, pbo, obj);
					String value6 = (String) hashtable.get("GONGYIHUIQIANHUIZONG");
					log.debug("value1: " + value1 + "value3: " + value3 + "value4: " + value4 + "value6: " + value6);
					String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
					String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
					String yspace = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".yspace", propertiesfile);
					fontsize = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".fontsize", propertiesfile);
					tempyspace = yspace;
					String tempValue = "";
					log.debug("tempValue is:" + tempValue + "|aaaaaaaaaaaaaa");
					if (value1 != null && value1.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null")) {
							tempValue = value1;
						} else {
							tempValue = tempValue + ";" + value1;
						}
					}
					log.debug("tempValue1 is: " + tempValue);
					// if (value2 != null && value2.length() > 0) {
					// if (tempValue == null || tempValue.trim().equals("") ||
					// tempValue.trim().equals("null")){
					// tempValue = value2;
					// } else {
					// tempValue = tempValue + ";" + value2;
					// }
					// }
					log.debug("tempValue2 is: " + tempValue);
					if (value3 != null && value3.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null")) {
							tempValue = value3;
						} else {
							tempValue = tempValue + ";" + value3;
						}
					}
					log.debug("tempValue3 is: " + tempValue);
					if (value4 != null && value4.length() > 0 && !value4.contains("null")) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null")) {
							tempValue = value4;
						} else {
							tempValue = tempValue + ";" + value4;
						}
					}
					// log.debug("tempValue4 is: " + tempValue);
					// if (value5 != null && value5.length() > 0) {
					// if (tempValue == null || tempValue.trim().equals("") ||
					// tempValue.trim().equals("null")) {
					// tempValue = value5;
					// } else {
					// tempValue = tempValue + ";" + value5;
					// }
					// }
					log.debug("tempValue5 is: " + tempValue);
					if (value6 != null && value6.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null")) {
							tempValue = value6;
						} else {
							tempValue = tempValue + ";" + value6;
						}
					}
					log.debug("tempValue6 is: " + tempValue);
					if (tempValue == null || tempValue.trim().length() == 0) {
						continue;
					}
					value = tempValue;
					String hqValue[] = value.split(";");
					for (int j = 0; j < hqValue.length; j++) {
						tempj = j;
						String hqSplitValue = hqValue[j];
						// HqSplitValue的格式为 张三/1室/2010-02-03
						String hqSplitValue1[] = hqSplitValue.split("/");
						if (format.equals("FORMATDRWA4") || format.equals("FORMATDWGA4") || format.equals("FORMATDWGA3V") || format.equals("FORMATDWGA2V")
								|| format.equals("FORMATDWGA1V") || format.equals("FORMATDWGA0V") || format.equals("FORMATDWG2A0V")
								|| format.equals("FORMATDWG3A0V") || format.equals("FORMAT4") || format.equals("FORMATDWGMXBA4")
								|| format.equals("FORMATDWGJXBA4") || format.equals("FORMATDWGYJBA4") || format.equals("FORMATPROCESSNOTICE4")) {
							Hashtable h = new Hashtable();
							float y = 0;
							if (j == 0) {
								y = Float.parseFloat(locationy);
							} else {
								y = Float.parseFloat(locationy) - Float.parseFloat(yspace);
							}
							locationy = String.valueOf(y);
							// 放入hashtable
							// 先放入部门信息
							h.put(ATTR, key + String.valueOf(j));

							// 修改，避免越界
							if (hqSplitValue1.length >= 2) {
								h.put(ATTRVALUE, hqSplitValue1[1]);
							} else {
								h.put(ATTRVALUE, hqSplitValue);
							}

							h.put(LOCATIONX, locationx);
							h.put(LOCATIONY, locationy);
							h.put(FONTSIZE, fontsize);
							h.put(OUTPUTPAGE, outputpage);
							h.put(DIRECTION, direction);
							allinfo.add(h);
							h = new Hashtable();
							y = Float.parseFloat(locationy) - Float.parseFloat(yspace);
							locationy = String.valueOf(y);
							h.put(ATTR, key + String.valueOf(j));

							// 修改，避免越界
							if (hqSplitValue1.length >= 3) {
								h.put(ATTRVALUE, hqSplitValue1[0] + " " + hqSplitValue1[2]);
							} else {
								h.put(ATTRVALUE, hqSplitValue);
							}

							h.put(LOCATIONX, locationx);
							h.put(LOCATIONY, locationy);
							h.put(FONTSIZE, fontsize);
							h.put(OUTPUTPAGE, outputpage);
							h.put(DIRECTION, direction);
							allinfo.add(h);
							log.debug("key is:" + key + ",value is " + hqSplitValue + ",x is:" + locationx + ",y is" + locationy);
						} else if (format.equals("FORMATDWGA3H") || format.equals("FORMATDWGA2H") || format.equals("FORMATDWGA1H")
								|| format.equals("FORMATDWGA0H")) {
							Hashtable h = new Hashtable();
							float y = 0;
							if (j == 0) {
								y = Float.parseFloat(locationy);
							} else {
								y = Float.parseFloat(locationy) - Float.parseFloat(yspace);
							}
							locationy = String.valueOf(y);
							// 放入hashtable
							// 先放放部门
							h.put(ATTR, key + String.valueOf(j));

							// 修改，避免越界
							if (hqSplitValue1.length >= 2) {
								h.put(ATTRVALUE, hqSplitValue1[1]);
							} else {
								h.put(ATTRVALUE, hqSplitValue);
							}

							h.put(LOCATIONX, locationx);
							h.put(LOCATIONY, locationy);
							h.put(FONTSIZE, fontsize);
							h.put(OUTPUTPAGE, outputpage);
							h.put(DIRECTION, direction);
							allinfo.add(h);
							h = new Hashtable();
							y = Float.parseFloat(locationy) - Float.parseFloat(yspace);
							locationy = String.valueOf(y);
							h.put(ATTR, key + String.valueOf(j));

							// 修改，避免越界
							if (hqSplitValue1.length >= 3) {
								h.put(ATTRVALUE, hqSplitValue1[0] + " " + hqSplitValue1[2]);
							} else {
								h.put(ATTRVALUE, hqSplitValue);
							}

							h.put(LOCATIONX, locationx);
							h.put(LOCATIONY, locationy);
							h.put(FONTSIZE, fontsize);
							h.put(OUTPUTPAGE, outputpage);
							h.put(DIRECTION, direction);
							allinfo.add(h);
							log.debug("key is:" + key + ",value is " + hqSplitValue + ",x is:" + locationx + ",y is" + locationy);
						}
						size++;
					}
					tempx = locationx;
					tempy = locationy;
					// 当前页码 add by zhuhao 2017.4.20
				} else if (key.equals("CURPAGE")) {
					String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
					String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
					ibas.put(ATTR, "CURPAGE");
					ibas.put(LOCATIONX, locationx);
					ibas.put(LOCATIONY, locationy);
					ibas.put(DIRECTION, direction);
					ibas.put(OUTPUTPAGE, outputpage);
					ibas.put(FONTSIZE, fontsize);
					allinfo.add(ibas);
					// 总页码 add by zhuhao 2017.4.20
				} else if (key.equals("TOTPAGE")) {
					String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
					String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
					ibas.put(ATTR, "TOTPAGE");
					ibas.put(LOCATIONX, locationx);
					ibas.put(LOCATIONY, locationy);
					ibas.put(DIRECTION, direction);
					ibas.put(OUTPUTPAGE, outputpage);
					ibas.put(FONTSIZE, fontsize);
					allinfo.add(ibas);
				} else {
					value = (String) hashtable.get(key);// 获得签审信息，要写入PDF的内容
					if (value != null && value.length() > 0) {
						// 获得坐标
						String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
						String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
						fontsize = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".fontsize", propertiesfile);
						log.debug("key is:" + key + ",value is " + value + ",x is:" + locationx + ",y is" + locationy);
						/*
						 * if(key.startsWith("JDBJ")||key.equals("BIAOZHI")){
						 * //放入hashtable ibas.put(ATTR,key);
						 * ibas.put(ATTRVALUE,value);
						 * ibas.put(LOCATIONX,locationx);
						 * ibas.put(LOCATIONY,locationy);
						 * ibas.put(FONTSIZE,fontsize);
						 * ibas.put(OUTPUTPAGE,outputpage);
						 * ibas.put(DIRECTION,direction); allinfo.add(ibas);
						 * }else{ String value1[] = value.split("/");
						 * //放入hashtable ibas.put(ATTR,key);
						 * ibas.put(ATTRVALUE,value1[0] + " " + value1[2]);
						 * ibas.put(LOCATIONX,locationx);
						 * ibas.put(LOCATIONY,locationy);
						 * ibas.put(FONTSIZE,fontsize);
						 * ibas.put(OUTPUTPAGE,outputpage);
						 * ibas.put(DIRECTION,direction); allinfo.add(ibas); }
						 */
						if ((!allExceptKeys.contains(key) && !key.equals("FILENUMBER") && !key.equals("SYMBOL") && value.indexOf("/") > -1)) {
							String value1[] = value.split("/");
							// 放入hashtable
							if (value1.length >= 3) {
								ibas.put(ATTRVALUE, value1[0] + " " + value1[2]);
							} else {
								ibas.put(ATTRVALUE, value);
							}
						} else {
							ibas.put(ATTRVALUE, value);
						}

						if (key.equals("GONGYIHUIQIAN")) {
							value = getGongYiValue(hashtable, pbo, obj);
							if (value.contains(";")) {
								String value1[] = value.split(";");
								for (int n = 0; n < value1.length; n++) {
									ibas = new Hashtable();
									String str = value1[n];
									String value2[] = str.split("/");
									// 修改，避免越界
									if (value2.length >= 2) {
										ibas.put(ATTRVALUE, value2[1]);
									} else {
										ibas.put(ATTRVALUE, str);
									}
									if (n == 0) {
										// ibas.put(ATTR, key);
										// ibas.put(LOCATIONX, locationx);
										// ibas.put(LOCATIONY, locationy);
										// ibas.put(FONTSIZE, fontsize);
										// ibas.put(OUTPUTPAGE, outputpage);
										// ibas.put(DIRECTION, direction);
										// allinfo.add(ibas);
									} else {
										tempj = tempj + 1;
										tempy = String.valueOf(Float.parseFloat(tempy) - Float.parseFloat(tempyspace));
										ibas.put(ATTR, "HUIQIAN" + tempj);
										ibas.put(LOCATIONX, tempx);
										ibas.put(LOCATIONY, tempy);
										ibas.put(FONTSIZE, fontsize);
										ibas.put(OUTPUTPAGE, outputpage);
										ibas.put(DIRECTION, direction);
										allinfo.add(ibas);
									}
									ibas = new Hashtable();
									// 放入hashtable
									if (value2.length >= 3) {
										ibas.put(ATTRVALUE, value2[0] + " " + value2[2]);
									} else {
										ibas.put(ATTRVALUE, str);
									}
									if (n == 0) {
										ibas.put(ATTR, key);
										ibas.put(LOCATIONX, locationx);
										ibas.put(LOCATIONY, locationy);
										ibas.put(FONTSIZE, fontsize);
										ibas.put(OUTPUTPAGE, outputpage);
										ibas.put(DIRECTION, direction);
										allinfo.add(ibas);
									} else {
										// tempj = tempj+1;
										tempy = String.valueOf(Float.parseFloat(tempy) - Float.parseFloat(tempyspace));
										ibas.put(ATTR, "HUIQIAN" + tempj);
										ibas.put(LOCATIONX, tempx);
										ibas.put(LOCATIONY, tempy);
										ibas.put(FONTSIZE, fontsize);
										ibas.put(OUTPUTPAGE, outputpage);
										ibas.put(DIRECTION, direction);
										allinfo.add(ibas);
									}
								}

							} else {
								String value1[] = value.split("/");
								// 放入hashtable
								if (value1.length >= 3) {
									ibas.put(ATTRVALUE, value1[0] + " " + value1[2]);
								} else {
									ibas.put(ATTRVALUE, value);
								}
								ibas.put(ATTR, key);
								ibas.put(LOCATIONX, locationx);
								ibas.put(LOCATIONY, locationy);
								ibas.put(FONTSIZE, fontsize);
								ibas.put(OUTPUTPAGE, outputpage);
								ibas.put(DIRECTION, direction);
								allinfo.add(ibas);
							}
						} else {
							ibas.put(ATTR, key);
							ibas.put(LOCATIONX, locationx);
							ibas.put(LOCATIONY, locationy);
							ibas.put(FONTSIZE, fontsize);
							ibas.put(OUTPUTPAGE, outputpage);
							ibas.put(DIRECTION, direction);
							allinfo.add(ibas);
						}
					}
				}
			}
		} catch (WTException e) {
		}
		return allinfo;
	}

	public static Vector getDocSignInfo(Hashtable hashtable, String format, Object pbo, WTObject obj) throws UnsupportedEncodingException,
			MissingResourceException {
		int outputpage;
		String direction;
		float fontsizevalue;
		Vector allinfo = new Vector();
		try {
			String value = "";
			String key = "";
			String times = "";
			boolean flag = false;
			// 获得输出的页位置
			String outputpagestr = getStrFromProperties(format + ".outputpage", propertiesfile);
			outputpage = Integer.parseInt(outputpagestr);
			// 获得输出文字方向
			direction = getStrFromProperties(format + ".direction", propertiesfile);
			if (direction == null || (direction.length() == 0))
				direction = "vertical";

			// 获得输出文字大小
			String fontsize = getStrFromProperties(format + ".fontsize", propertiesfile);
			if (fontsize == null || (fontsize.length() == 0) || fontsize.equals("0"))
				fontsize = "10";
			fontsizevalue = Float.parseFloat(fontsize);

			// 获得唯一值的属性
			String uniqueValueAttr = getStrFromProperties(format + ".uniquevalueattr", propertiesfile);
			int uniqueValueAttrCount = Integer.parseInt(uniqueValueAttr);

			for (int i = 1; i <= uniqueValueAttrCount; i++) {
				Hashtable ibas = new Hashtable();
				int size = 0;
				// 获得Key
				key = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".ibaattribute", propertiesfile);
				// 获得key值
				if ((key == null) || (key.length() == 0)) {
					continue;
				} else if (key.equals("HUIQIAN") && !"FORMATQAA4".equals(flag)) {
					String value1 = (String) hashtable.get("NEIBUHUIQIAN");
					// String value2 = (String)
					// hashtable.get("NEIBUGONGYIHUIQIAN");
					String value3 = (String) hashtable.get("WAIBUHUIQIAN");
					String value4 = (String) hashtable.get("GONGYIHUIQIAN");
					// String value5 = (String)
					// hashtable.get("WAIBUGONGYIHUIQIAN");
					// String value6 = (String)
					// hashtable.get("GONGYIHUIQIANHUIZONG");
					String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
					String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
					String yspace = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".yspace", propertiesfile);
					fontsize = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".fontsize", propertiesfile);
					outputpagestr = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".outputpage", propertiesfile);
					outputpage = Integer.parseInt(outputpagestr);
					String tempValue = "";
					if (value1 != null && value1.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null"))
							tempValue = value1;
						else
							tempValue = tempValue + ";" + value1;
					}
					log.debug("tempValue1 is: " + tempValue);
					// if (value2 != null && value2.length() > 0) {
					// if (tempValue == null || tempValue.trim().equals("") ||
					// tempValue.trim().equals("null"))
					// tempValue = value2;
					// else tempValue = tempValue + ";" + value2;
					// }
					log.debug("tempValue2 is: " + tempValue);
					if (value3 != null && value3.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null"))
							tempValue = value3;
						else
							tempValue = tempValue + ";" + value3;
					}
					log.debug("tempValue3 is: " + tempValue);
					if (value4 != null && value4.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null"))
							tempValue = value4;
						else
							tempValue = tempValue + ";" + value4;
					}
					log.debug("tempValue4 is: " + tempValue);
					// if (value5 != null && value5.length() > 0) {
					// if (tempValue == null || tempValue.trim().equals("") ||
					// tempValue.trim().equals("null"))
					// tempValue = value5;
					// else tempValue = tempValue + ";" + value5;
					// }
					// log.debug("tempValue5 is: " + tempValue);
					// if (value6 != null && value6.length() > 0) {
					// if (tempValue == null || tempValue.trim().equals("") ||
					// tempValue.trim().equals("null"))
					// tempValue = value6;
					// else tempValue = tempValue + ";" + value6;
					// }
					log.debug("tempValue6 is: " + tempValue);
					if (tempValue == null || tempValue.trim().length() == 0)
						continue;
					value = tempValue;
					String hqValue[] = value.split(";");
					for (int j = 0; j < hqValue.length; j++) {
						String hqSplitValue = hqValue[j];
						// HqSplitValue的格式为 张三/1室/2010-02-03
						String hqSplitValue1[] = hqSplitValue.split("/");
						Hashtable h = new Hashtable();
						float y = 0;
						if (j == 0)
							y = Float.parseFloat(locationy);
						else
							y = Float.parseFloat(locationy) - Float.parseFloat(yspace);
						locationy = String.valueOf(y);
						// 放入hashtable
						// 先放入部门信息
						h.put(ATTR, key + String.valueOf(j));
						// 修改，避免越界
						if (hqSplitValue1.length >= 2)
							h.put(ATTRVALUE, hqSplitValue1[1]);
						else
							h.put(ATTRVALUE, hqSplitValue);

						h.put(LOCATIONX, locationx);
						h.put(LOCATIONY, locationy);
						h.put(FONTSIZE, fontsize);
						h.put(OUTPUTPAGE, outputpage);
						h.put(DIRECTION, direction);
						allinfo.add(h);
						h = new Hashtable();
						y = Float.parseFloat(locationy) - Float.parseFloat(yspace);
						locationy = String.valueOf(y);
						h.put(ATTR, key + String.valueOf(j));
						// 修改，避免越界
						if (hqSplitValue1.length >= 3)
							h.put(ATTRVALUE, hqSplitValue1[0] + " " + hqSplitValue1[2]);
						else
							h.put(ATTRVALUE, hqSplitValue);

						h.put(LOCATIONX, locationx);
						h.put(LOCATIONY, locationy);
						h.put(FONTSIZE, fontsize);
						h.put(OUTPUTPAGE, outputpage);
						h.put(DIRECTION, direction);
						allinfo.add(h);
						log.debug("key is:" + key + ",value is " + hqSplitValue + ",x is:" + locationx + ",y is" + locationy);
						size++;
					}
				} else if (key.equals("HUIQIANDANWEI") && !"FORMATQAA4".equals(flag)) {
					String value1 = (String) hashtable.get("NEIBUHUIQIAN");
					// String value2 = (String)
					// hashtable.get("NEIBUGONGYIHUIQIAN");
					String value3 = (String) hashtable.get("WAIBUHUIQIAN");
					// String value4 = (String) hashtable.get("GONGYIHUIQIAN");
					// String value5 = (String)
					// hashtable.get("WAIBUGONGYIHUIQIAN");
					// String value6 = (String)
					// hashtable.get("GONGYIHUIQIANHUIZONG");
					String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
					String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
					String xspace = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".xspace", propertiesfile);
					String yspace = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".yspace", propertiesfile);
					fontsize = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".fontsize", propertiesfile);
					outputpagestr = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".outputpage", propertiesfile);
					outputpage = Integer.parseInt(outputpagestr);
					String tempValue = "";
					if (value1 != null && value1.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null"))
							tempValue = value1;
						else
							tempValue = tempValue + ";" + value1;
					}
					log.debug("tempValue1 is: " + tempValue);
					// if (value2 != null && value2.length() > 0) {
					// if (tempValue == null || tempValue.trim().equals("") ||
					// tempValue.trim().equals("null"))
					// tempValue = value2;
					// else tempValue = tempValue + ";" + value2;
					// }
					log.debug("tempValue2 is: " + tempValue);
					if (value3 != null && value3.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null"))
							tempValue = value3;
						else
							tempValue = tempValue + ";" + value3;
					}
					// log.debug("tempValue3 is: " + tempValue);
					// if (value4 != null && value4.length() > 0) {
					// if (tempValue == null || tempValue.trim().equals("") ||
					// tempValue.trim().equals("null"))
					// tempValue = value4;
					// else tempValue = tempValue + ";" + value4;
					// }
					// log.debug("tempValue4 is: " + tempValue);
					// if (value5 != null && value5.length() > 0) {
					// if (tempValue == null || tempValue.trim().equals("") ||
					// tempValue.trim().equals("null"))
					// tempValue = value5;
					// else tempValue = tempValue + ";" + value5;
					// }
					// log.debug("tempValue5 is: " + tempValue);
					// if (value6 != null && value6.length() > 0) {
					// if (tempValue == null || tempValue.trim().equals("") ||
					// tempValue.trim().equals("null"))
					// tempValue = value6;
					// else tempValue = tempValue + ";" + value6;
					// }
					log.debug("tempValue6 is: " + tempValue);
					if (tempValue == null || tempValue.trim().length() == 0)
						continue;
					value = tempValue;
					String hqValue[] = value.split(";");
					String locationyy = locationy;// y的原高度
					String locationxx = locationx; // x的原高度
					for (int j = 0; j < hqValue.length; j++) {
						float y = 0;
						if (j == 0 || j == 10)
							y = Float.parseFloat(locationyy);
						else
							y = Float.parseFloat(locationy) - Float.parseFloat(yspace);
						locationy = String.valueOf(y);

						String hqSplitValue = hqValue[j];
						String hqSplitValue1[] = hqSplitValue.split("/");
						for (int k = 0; k < hqSplitValue1.length; k++) {
							String tempValue1 = hqSplitValue1[k];
							Hashtable h = new Hashtable();
							float x = 0;
							if (j < 10) {
								if (k == 0)
									x = Float.parseFloat(locationxx);
								else
									x = Float.parseFloat(locationx) + Float.parseFloat(xspace);

							} else {
								if (k == 0)
									x = Float.parseFloat(locationxx) + Float.parseFloat(xspace) * 3;
								else
									x = Float.parseFloat(locationx) + Float.parseFloat(xspace);
							}
							locationx = String.valueOf(x);
							// 放入hashtable
							h.put(ATTR, key + String.valueOf(k));
							h.put(ATTRVALUE, tempValue1);
							h.put(LOCATIONX, locationx);
							h.put(LOCATIONY, locationy);
							h.put(FONTSIZE, fontsize);
							h.put(OUTPUTPAGE, outputpage);
							h.put(DIRECTION, direction);
							allinfo.add(h);
							log.debug("key is:" + key + ",value is " + hqSplitValue + ",x is:" + locationx + ",y is" + locationy);
							size++;
						}
					}
				} else if ("NEIBUHUIQIAN".equals(key) && ("FORMATFANGANA4".equals(format) || "FORMATQITALEIWENDANGA4".equals(format))) {
					value = (String) hashtable.get(key);
					if (value != null && value.length() > 0) {
						String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
						String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
						String xspace = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".xspace", propertiesfile);
						String yspace = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".yspace", propertiesfile);
						fontsize = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".fontsize", propertiesfile);
						outputpagestr = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".outputpage", propertiesfile);
						outputpage = Integer.parseInt(outputpagestr);
						String values[] = value.split(";");
						int count = 0;
						for (int n = 0; n < values.length; n++) {
							count += 1;
							ibas = new Hashtable();
							String str = values[n];
							String strs[] = str.split("/");
							String info = strs[0] + " " + strs[2];
							if (count == 2) {
								locationy = String.valueOf(Float.parseFloat(locationy) - Float.parseFloat(yspace));
							}
							if (count > 2) {
								locationx = String.valueOf(Float.parseFloat(locationx) + Float.parseFloat(xspace));
								locationy = String.valueOf(Float.parseFloat(locationy) + Float.parseFloat(yspace));
								count = 1;
							}
							ibas.put(ATTR, key + String.valueOf(n));
							ibas.put(ATTRVALUE, info);
							ibas.put(LOCATIONX, locationx);
							ibas.put(LOCATIONY, locationy);
							ibas.put(FONTSIZE, fontsize);
							ibas.put(OUTPUTPAGE, outputpage);
							ibas.put(DIRECTION, direction);
							allinfo.add(ibas);
						}
					}
				} else {
					value = (String) hashtable.get(key);// 获得签审信息，要写入PDF的内容
					if (key.equals("MIJI") && value == null) {// 处理总方案、分方案密级 add
																// by zhuhao
																// 2017.10.04
						value = (String) hashtable.get("SECRET");
					}
					if (value != null && value.length() > 0) {
						// 获得坐标
						String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
						String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
						fontsize = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".fontsize", propertiesfile);
						outputpagestr = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".outputpage", propertiesfile);
						outputpage = Integer.parseInt(outputpagestr);
						log.debug("key is:" + key + ",value is " + value + ",x is:" + locationx + ",y is" + locationy);
						if (key.equals("SHEJIZHESHIJIAN") || key.equals("CANYURENZHESHIJIAN") || key.equals("JIAODUIZHESHIJIAN")
								|| key.equals("SHENHEZHESHIJIAN") || key.equals("FUSHENZHESHIJIAN") || key.equals("BIAOSHENZHESHIJIAN")
								|| key.equals("PIZHUNZHESHIJIAN") || key.equals("SHENHE1") || key.equals("SHENHE2") || key.equals("NEIBUHUIQIAN")
								|| key.equals("WAIBUHUIQIAN")) {
							// 放入hashtable
							String value1[] = value.split("/");
							ibas.put(ATTR, key);

							// 修改 避免数组越界
							if (key.equals("NEIBUHUIQIAN") || key.equals("WAIBUHUIQIAN")) {
								ibas.put(ATTRVALUE, value);
							} else {
								if (value1.length >= 3) {
									ibas.put(ATTRVALUE, value1[0] + " " + value1[2]);
								} else {
									ibas.put(ATTRVALUE, value);
								}
							}

							ibas.put(LOCATIONX, locationx);
							ibas.put(LOCATIONY, locationy);
							ibas.put(FONTSIZE, fontsize);
							ibas.put(OUTPUTPAGE, outputpage);
							ibas.put(DIRECTION, direction);
							allinfo.add(ibas);
						} else {
							// 放入hashtable
							ibas.put(ATTR, key);
							ibas.put(ATTRVALUE, value);
							ibas.put(LOCATIONX, locationx);
							ibas.put(LOCATIONY, locationy);
							ibas.put(FONTSIZE, fontsize);
							ibas.put(OUTPUTPAGE, outputpage);
							ibas.put(DIRECTION, direction);
							allinfo.add(ibas);
						}

					}
				}
			}
		} catch (WTException e) {
		}

		return allinfo;
	}

	public static Vector getSOPDocSignInfo(Hashtable hashtable, String format, Object pbo, WTObject obj) throws UnsupportedEncodingException,
			MissingResourceException {
		int outputpage;
		String direction;
		float fontsizevalue;
		Vector allinfo = new Vector();
		try {
			String value = "";
			String key = "";
			String times = "";
			boolean flag = false;
			// 获得输出的页位置
			String outputpagestr = getStrFromProperties(format + ".outputpage", propertiesfile);
			outputpage = Integer.parseInt(outputpagestr);
			// 获得输出文字方向
			direction = getStrFromProperties(format + ".direction", propertiesfile);
			if (direction == null || (direction.length() == 0))
				direction = "vertical";

			// 获得输出文字大小
			String fontsize = getStrFromProperties(format + ".fontsize", propertiesfile);
			if (fontsize == null || (fontsize.length() == 0) || fontsize.equals("0"))
				fontsize = "10";
			fontsizevalue = Float.parseFloat(fontsize);

			// 获得唯一值的属性
			String uniqueValueAttr = getStrFromProperties(format + ".uniquevalueattr", propertiesfile);
			int uniqueValueAttrCount = Integer.parseInt(uniqueValueAttr);

			for (int i = 1; i <= uniqueValueAttrCount; i++) {
				Hashtable ibas = new Hashtable();
				int size = 0;
				// 获得Key
				key = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".ibaattribute", propertiesfile);
				// 获得key值
				if ((key == null) || (key.length() == 0)) {
					continue;
				} else if ("NEIBUHUIQIAN".equals(key)) {
					value = (String) hashtable.get(key);
					if (value != null && value.length() > 0) {
						String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
						String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
						String xspace = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".xspace", propertiesfile);
						String yspace = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".yspace", propertiesfile);
						fontsize = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".fontsize", propertiesfile);
						outputpagestr = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".outputpage", propertiesfile);
						outputpage = Integer.parseInt(outputpagestr);
						String values[] = value.split(";");
						for (int n = 0; n < values.length; n++) {
							String str = values[n];
							String strs[] = str.split("/");
							String dept = strs[1];
							String info = strs[0] + " " + strs[2];
							String all[] = { dept, info };
							for (int j = 0; j < all.length; j++) {
								ibas = new Hashtable();
								locationy = String.valueOf(Float.parseFloat(locationy) - Float.parseFloat(yspace));
								ibas.put(ATTR, key + "_" + j);
								ibas.put(ATTRVALUE, all[j]);
								ibas.put(LOCATIONX, locationx);
								ibas.put(LOCATIONY, locationy);
								ibas.put(FONTSIZE, fontsize);
								ibas.put(OUTPUTPAGE, outputpage);
								ibas.put(DIRECTION, direction);
								allinfo.add(ibas);
							}
						}
					}
				} else {
					value = (String) hashtable.get(key);
					if (value != null && value.length() > 0) {
						// 获得坐标
						String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
						String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
						fontsize = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".fontsize", propertiesfile);
						outputpagestr = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".outputpage", propertiesfile);
						outputpage = Integer.parseInt(outputpagestr);
						log.debug("key is:" + key + ",value is " + value + ",x is:" + locationx + ",y is" + locationy);
						if (key.equals("SHEJIZHESHIJIAN") || key.equals("CANYURENZHESHIJIAN") || key.equals("JIAODUIZHESHIJIAN")
								|| key.equals("SHENHEZHESHIJIAN") || key.equals("FUSHENZHESHIJIAN") || key.equals("BIAOSHENZHESHIJIAN")
								|| key.equals("PIZHUNZHESHIJIAN") || key.equals("NEIBUHUIQIAN")) {
							// 放入hashtable
							String value1[] = value.split("/");
							ibas = new Hashtable();
							ibas.put(ATTR, key);

							// 修改 避免数组越界
							if (key.equals("NEIBUHUIQIAN") || key.equals("WAIBUHUIQIAN")) {
								ibas.put(ATTRVALUE, value);
							} else {
								if (value1.length >= 3) {
									ibas.put(ATTRVALUE, value1[0] + " " + value1[2]);
								} else {
									ibas.put(ATTRVALUE, value);
								}
							}

							ibas.put(LOCATIONX, locationx);
							ibas.put(LOCATIONY, locationy);
							ibas.put(FONTSIZE, fontsize);
							ibas.put(OUTPUTPAGE, outputpage);
							ibas.put(DIRECTION, direction);
							allinfo.add(ibas);
						} else {
							// 放入hashtable
							ibas = new Hashtable();
							ibas.put(ATTR, key);
							ibas.put(ATTRVALUE, value);
							ibas.put(LOCATIONX, locationx);
							ibas.put(LOCATIONY, locationy);
							ibas.put(FONTSIZE, fontsize);
							ibas.put(OUTPUTPAGE, outputpage);
							ibas.put(DIRECTION, direction);
							allinfo.add(ibas);
						}

					}
				}
			}
		} catch (WTException e) {
		}
		return allinfo;
	}

	public static Vector getProcessPlanDocSignInfo(Hashtable hashtable, String format, Object pbo, WTObject obj) throws UnsupportedEncodingException,
			MissingResourceException {
		int outputpage;
		String direction;
		float fontsizevalue;
		Vector allinfo = new Vector();
		try {
			String value = "";
			String key = "";
			String times = "";
			boolean flag = false;
			// 获得输出的页位置
			String outputpagestr = getStrFromProperties(format + ".outputpage", propertiesfile);
			outputpage = Integer.parseInt(outputpagestr);
			// 获得输出文字方向
			direction = getStrFromProperties(format + ".direction", propertiesfile);
			if (direction == null || (direction.length() == 0))
				direction = "vertical";

			// 获得输出文字大小
			String fontsize = getStrFromProperties(format + ".fontsize", propertiesfile);
			if (fontsize == null || (fontsize.length() == 0) || fontsize.equals("0"))
				fontsize = "10";
			fontsizevalue = Float.parseFloat(fontsize);

			// 获得唯一值的属性
			String uniqueValueAttr = getStrFromProperties(format + ".uniquevalueattr", propertiesfile);
			int uniqueValueAttrCount = Integer.parseInt(uniqueValueAttr);

			for (int i = 1; i <= uniqueValueAttrCount; i++) {
				Hashtable ibas = new Hashtable();
				int size = 0;
				// 获得Key
				key = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".ibaattribute", propertiesfile);
				// 获得key值
				if ((key == null) || (key.length() == 0)) {
					continue;
				} else if (key.equals("HUIQIAN") && !"FORMATQAA4".equals(flag)) {
					String value1 = (String) hashtable.get("NEIBUHUIQIAN");
					// String value2 = (String)
					// hashtable.get("NEIBUGONGYIHUIQIAN");
					String value3 = (String) hashtable.get("WAIBUHUIQIAN");
					String value4 = (String) hashtable.get("GONGYIHUIQIAN");
					// String value5 = (String)
					// hashtable.get("WAIBUGONGYIHUIQIAN");
					// String value6 = (String)
					// hashtable.get("GONGYIHUIQIANHUIZONG");
					String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
					String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
					String yspace = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".yspace", propertiesfile);
					fontsize = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".fontsize", propertiesfile);
					outputpagestr = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".outputpage", propertiesfile);
					outputpage = Integer.parseInt(outputpagestr);
					String tempValue = "";
					if (value1 != null && value1.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null"))
							tempValue = value1;
						else
							tempValue = tempValue + ";" + value1;
					}
					log.debug("tempValue1 is: " + tempValue);
					// if (value2 != null && value2.length() > 0) {
					// if (tempValue == null || tempValue.trim().equals("") ||
					// tempValue.trim().equals("null"))
					// tempValue = value2;
					// else tempValue = tempValue + ";" + value2;
					// }
					log.debug("tempValue2 is: " + tempValue);
					if (value3 != null && value3.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null"))
							tempValue = value3;
						else
							tempValue = tempValue + ";" + value3;
					}
					log.debug("tempValue3 is: " + tempValue);
					if (value4 != null && value4.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null"))
							tempValue = value4;
						else
							tempValue = tempValue + ";" + value4;
					}
					log.debug("tempValue4 is: " + tempValue);
					// if (value5 != null && value5.length() > 0) {
					// if (tempValue == null || tempValue.trim().equals("") ||
					// tempValue.trim().equals("null"))
					// tempValue = value5;
					// else tempValue = tempValue + ";" + value5;
					// }
					// log.debug("tempValue5 is: " + tempValue);
					// if (value6 != null && value6.length() > 0) {
					// if (tempValue == null || tempValue.trim().equals("") ||
					// tempValue.trim().equals("null"))
					// tempValue = value6;
					// else tempValue = tempValue + ";" + value6;
					// }
					log.debug("tempValue6 is: " + tempValue);
					if (tempValue == null || tempValue.trim().length() == 0)
						continue;
					value = tempValue;
					String hqValue[] = value.split(";");
					for (int j = 0; j < hqValue.length; j++) {
						String hqSplitValue = hqValue[j];
						// HqSplitValue的格式为 张三/1室/2010-02-03
						String hqSplitValue1[] = hqSplitValue.split("/");
						Hashtable h = new Hashtable();
						float y = 0;
						if (j == 0)
							y = Float.parseFloat(locationy);
						else
							y = Float.parseFloat(locationy) - Float.parseFloat(yspace);
						locationy = String.valueOf(y);
						// 放入hashtable
						// 先放入部门信息
						h.put(ATTR, key + String.valueOf(j));
						// 修改，避免越界
						if (hqSplitValue1.length >= 2)
							h.put(ATTRVALUE, hqSplitValue1[1]);
						else
							h.put(ATTRVALUE, hqSplitValue);

						h.put(LOCATIONX, locationx);
						h.put(LOCATIONY, locationy);
						h.put(FONTSIZE, fontsize);
						h.put(OUTPUTPAGE, outputpage);
						h.put(DIRECTION, direction);
						allinfo.add(h);
						h = new Hashtable();
						y = Float.parseFloat(locationy) - Float.parseFloat(yspace);
						locationy = String.valueOf(y);
						h.put(ATTR, key + String.valueOf(j));
						// 修改，避免越界
						if (hqSplitValue1.length >= 3)
							h.put(ATTRVALUE, hqSplitValue1[0] + " " + hqSplitValue1[2]);
						else
							h.put(ATTRVALUE, hqSplitValue);

						h.put(LOCATIONX, locationx);
						h.put(LOCATIONY, locationy);
						h.put(FONTSIZE, fontsize);
						h.put(OUTPUTPAGE, outputpage);
						h.put(DIRECTION, direction);
						allinfo.add(h);
						log.debug("key is:" + key + ",value is " + hqSplitValue + ",x is:" + locationx + ",y is" + locationy);
						size++;
					}
				} else if (key.equals("HUIQIANDANWEI") && !"FORMATQAA4".equals(flag)) {
					String value1 = (String) hashtable.get("NEIBUHUIQIAN");
					// String value2 = (String)
					// hashtable.get("NEIBUGONGYIHUIQIAN");
					String value3 = (String) hashtable.get("WAIBUHUIQIAN");
					// String value4 = (String) hashtable.get("GONGYIHUIQIAN");
					// String value5 = (String)
					// hashtable.get("WAIBUGONGYIHUIQIAN");
					// String value6 = (String)
					// hashtable.get("GONGYIHUIQIANHUIZONG");
					String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
					String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
					String xspace = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".xspace", propertiesfile);
					String yspace = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".yspace", propertiesfile);
					fontsize = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".fontsize", propertiesfile);
					outputpagestr = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".outputpage", propertiesfile);
					outputpage = Integer.parseInt(outputpagestr);
					String tempValue = "";
					if (value1 != null && value1.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null"))
							tempValue = value1;
						else
							tempValue = tempValue + ";" + value1;
					}
					log.debug("tempValue1 is: " + tempValue);
					// if (value2 != null && value2.length() > 0) {
					// if (tempValue == null || tempValue.trim().equals("") ||
					// tempValue.trim().equals("null"))
					// tempValue = value2;
					// else tempValue = tempValue + ";" + value2;
					// }
					log.debug("tempValue2 is: " + tempValue);
					if (value3 != null && value3.length() > 0) {
						if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null"))
							tempValue = value3;
						else
							tempValue = tempValue + ";" + value3;
					}
					// log.debug("tempValue3 is: " + tempValue);
					// if (value4 != null && value4.length() > 0) {
					// if (tempValue == null || tempValue.trim().equals("") ||
					// tempValue.trim().equals("null"))
					// tempValue = value4;
					// else tempValue = tempValue + ";" + value4;
					// }
					// log.debug("tempValue4 is: " + tempValue);
					// if (value5 != null && value5.length() > 0) {
					// if (tempValue == null || tempValue.trim().equals("") ||
					// tempValue.trim().equals("null"))
					// tempValue = value5;
					// else tempValue = tempValue + ";" + value5;
					// }
					// log.debug("tempValue5 is: " + tempValue);
					// if (value6 != null && value6.length() > 0) {
					// if (tempValue == null || tempValue.trim().equals("") ||
					// tempValue.trim().equals("null"))
					// tempValue = value6;
					// else tempValue = tempValue + ";" + value6;
					// }
					log.debug("tempValue6 is: " + tempValue);
					if (tempValue == null || tempValue.trim().length() == 0)
						continue;
					value = tempValue;
					String hqValue[] = value.split(";");
					String locationyy = locationy;// y的原高度
					String locationxx = locationx; // x的原高度
					for (int j = 0; j < hqValue.length; j++) {
						float y = 0;
						if (j == 0 || j == 10)
							y = Float.parseFloat(locationyy);
						else
							y = Float.parseFloat(locationy) - Float.parseFloat(yspace);
						locationy = String.valueOf(y);

						String hqSplitValue = hqValue[j];
						String hqSplitValue1[] = hqSplitValue.split("/");
						for (int k = 0; k < hqSplitValue1.length; k++) {
							String tempValue1 = hqSplitValue1[k];
							Hashtable h = new Hashtable();
							float x = 0;
							if (j < 10) {
								if (k == 0)
									x = Float.parseFloat(locationxx);
								else
									x = Float.parseFloat(locationx) + Float.parseFloat(xspace);

							} else {
								if (k == 0)
									x = Float.parseFloat(locationxx) + Float.parseFloat(xspace) * 3;
								else
									x = Float.parseFloat(locationx) + Float.parseFloat(xspace);
							}
							locationx = String.valueOf(x);
							// 放入hashtable
							h.put(ATTR, key + String.valueOf(k));
							h.put(ATTRVALUE, tempValue1);
							h.put(LOCATIONX, locationx);
							h.put(LOCATIONY, locationy);
							h.put(FONTSIZE, fontsize);
							h.put(OUTPUTPAGE, outputpage);
							h.put(DIRECTION, direction);
							allinfo.add(h);
							log.debug("key is:" + key + ",value is " + hqSplitValue + ",x is:" + locationx + ",y is" + locationy);
							size++;
						}
					}
				} else {
					value = (String) hashtable.get(key);// 获得签审信息，要写入PDF的内容
					if (value != null && value.length() > 0) {
						// 获得坐标
						String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
						String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
						fontsize = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".fontsize", propertiesfile);
						outputpagestr = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".outputpage", propertiesfile);
						outputpage = Integer.parseInt(outputpagestr);
						log.debug("key is:" + key + ",value is " + value + ",x is:" + locationx + ",y is" + locationy);
						if (key.equals("SHEJIZHESHIJIAN") || key.equals("CANYURENZHESHIJIAN") || key.equals("JIAODUIZHESHIJIAN")
								|| key.equals("SHENHEZHESHIJIAN") || key.equals("FUSHENZHESHIJIAN") || key.equals("BIAOSHENZHESHIJIAN")
								|| key.equals("PIZHUNZHESHIJIAN") || key.equals("SHENHE1") || key.equals("SHENHE2") || key.equals("NEIBUHUIQIAN")
								|| key.equals("WAIBUHUIQIAN") || key.equals("JIAODUI") || key.equals("SHENHE") || key.equals("SHEJI")) {
							// 放入hashtable
							String value1[] = value.split("/");
							ibas.put(ATTR, key);

							// 修改 避免数组越界
							if (key.equals("NEIBUHUIQIAN") || key.equals("WAIBUHUIQIAN")) {
								ibas.put(ATTRVALUE, value);
							} else {
								if (value1.length >= 3) {
									if ("FORMATTY_PROCESS_DOCA4".equals(format)) {
										if (key.equals("JIAODUI") || key.equals("SHENHE") || key.equals("SHEJI")) {
											ibas.put(ATTRVALUE, value1[0]);
										} else {
											ibas.put(ATTRVALUE, value1[0] + " " + value1[2]);
										}
									} else {
										ibas.put(ATTRVALUE, value1[0]);
									}

								} else {
									ibas.put(ATTRVALUE, value);
								}
							}

							ibas.put(LOCATIONX, locationx);
							ibas.put(LOCATIONY, locationy);
							ibas.put(FONTSIZE, fontsize);
							ibas.put(OUTPUTPAGE, outputpage);
							ibas.put(DIRECTION, direction);
							allinfo.add(ibas);
						} else {
							// 放入hashtable
							ibas.put(ATTR, key);
							ibas.put(ATTRVALUE, value);
							ibas.put(LOCATIONX, locationx);
							ibas.put(LOCATIONY, locationy);
							ibas.put(FONTSIZE, fontsize);
							ibas.put(OUTPUTPAGE, outputpage);
							ibas.put(DIRECTION, direction);
							allinfo.add(ibas);
						}

					}
				}
			}
			// 自定义签名左上角增加黑块 add by zhuhao 2018.11.01
			String zidingyi = String.valueOf(hashtable.get("ZIDINGYI"));
			if ("true".equals(zidingyi)) {
				Hashtable ibas = new Hashtable();
				ibas.put(ATTR, "ZIDINGYI");
				ibas.put(LOCATIONX, "30");
				ibas.put(LOCATIONY, "562");
				ibas.put(OUTPUTPAGE, 1);
				allinfo.add(ibas);

				Hashtable ibas2 = new Hashtable();
				ibas2.put(ATTR, "ecnBiaoJi");
				ibas2.put(ATTRVALUE, hashtable.get("ecnBiaoJi"));
				ibas2.put(LOCATIONX, "110");
				ibas2.put(LOCATIONY, "63");
				ibas2.put(OUTPUTPAGE, 0);
				allinfo.add(ibas2);

				Hashtable ibas3 = new Hashtable();
				ibas3.put(ATTR, "ecnNumber");
				ibas3.put(ATTRVALUE, hashtable.get("ecnNumber"));
				ibas3.put(LOCATIONX, "150");
				ibas3.put(LOCATIONY, "63");
				ibas3.put(OUTPUTPAGE, 0);
				allinfo.add(ibas3);

				Hashtable ibas4 = new Hashtable();
				ibas4.put(ATTR, "GENGGAI");
				ibas4.put(ATTRVALUE, hashtable.get("GENGGAI"));
				ibas4.put(LOCATIONX, "230");
				ibas4.put(LOCATIONY, "63");
				ibas4.put(OUTPUTPAGE, 0);
				allinfo.add(ibas4);

				Hashtable ibas5 = new Hashtable();
				ibas5.put(ATTR, "GENGGAISHIJIAN");
				ibas5.put(ATTRVALUE, hashtable.get("GENGGAISHIJIAN"));
				ibas5.put(LOCATIONX, "280");
				ibas5.put(LOCATIONY, "63");
				ibas5.put(OUTPUTPAGE, 0);
				allinfo.add(ibas5);
			}
			String version = String.valueOf(hashtable.get("VERSIONITE"));
			if (version != null && !version.isEmpty()) {
				Hashtable ibas = new Hashtable();
				String locationx = getStrFromProperties("PROCESSPLAN_VERSION_X", propertiesfile);
				String locationy = getStrFromProperties("PROCESSPLAN_VERSION_Y", propertiesfile);
				String font = getStrFromProperties("PROCESSPLAN_VERSION_FONTSIZE", propertiesfile);
				ibas.put(ATTR, "VERSIONITE");
				ibas.put(ATTRVALUE, version);
				ibas.put(LOCATIONX, locationx);
				ibas.put(LOCATIONY, locationy);
				ibas.put(FONTSIZE, font);
				ibas.put(OUTPUTPAGE, 0);
				allinfo.add(ibas);
			}
		} catch (WTException e) {
		}
		return allinfo;
	}

	// 返回String->Object
	public static Object getObject(String s) throws WTException {
		if (s != null && s.length() > 0)
			try {
				ReferenceFactory referencefactory = new ReferenceFactory();
				WTReference wtreference = referencefactory.getReference(s);
				wt.fc.Persistable persistable = wtreference.getObject();
				return persistable;

			} catch (WTException wtexception) {
				wtexception.printStackTrace();
				throw new WTException(wtexception);
			}
		return null;
	}

	// 2011.2.7 处理多审核
	public static Hashtable processForMultiReview(Hashtable hashtable) {
		// 处理多审核,将SHENHEZHESHIJIAN拆分
		// e.g.
		// SHENHEZHESHIJIAN=wcadmin//2011-02-07;user2/总体组/2011-02-07;usera//2011-02-07
		// 拆分为：SHENHEZHESHIJIAN=wcadmin//2011-02-07
		// SHENHE1=user2/总体组/2011-02-07
		// SHENHE2=usera//2011-02-07
		String shenhe = "";
		if (hashtable.containsKey("SHENHEZHESHIJIAN")) {
			shenhe = hashtable.get("SHENHEZHESHIJIAN").toString();
		} else if (hashtable.containsKey("SHENHE")) {
			shenhe = hashtable.get("SHENHE").toString();
		}
		if (shenhe.length() == 0 || shenhe.indexOf(";") < 0) {
			return hashtable;
		}
		StringTokenizer tokens = new StringTokenizer(shenhe, ";");
		String token1 = "", token2 = "", token3 = "";
		int count = tokens.countTokens();

		if (count >= 2) {
			token1 = tokens.nextToken();
			token2 = tokens.nextToken();
		}
		if (count >= 3) {
			token3 = tokens.nextToken();
		}

		if (token1.length() > 0) {
			hashtable.put("SHENHEZHESHIJIAN", token1);
			hashtable.put("SHENHE", token1);
			hashtable.put("SHENHESHIJIAN", token1.substring(token1.lastIndexOf("/") + 1));
		}
		if (token2.length() > 0) {
			hashtable.put("SHENHEMARK1", "审核");
			hashtable.put("SHENHE1", token2);
			hashtable.put("SHENHE1SHIJIAN", token2.substring(token2.lastIndexOf("/") + 1));
		}
		if (token3.length() > 0) {
			hashtable.put("SHENHEMARK2", "审核");
			hashtable.put("SHENHE2", token3);
			hashtable.put("SHENHE2SHIJIAN", token3.substring(token3.lastIndexOf("/") + 1));
		}

		System.out.println("end processForMultiReview count=" + count);
		return hashtable;
	}

	public static void writeReviewtoPDF4Doc(WTObject obj, Hashtable hashtable, boolean overWrite, String format) {
		String signTemplate = "";
		Vector allinfo = new Vector();
		int outputpage;
		String direction;
		float fontsizevalue;
		try {
			String value = "";
			String key = "";
			String times = "";
			boolean flag = false;
			// 获得输出的页位置
			String outputpagestr = getStrFromProperties(format + ".outputpage", propertiesfile);
			outputpage = Integer.parseInt(outputpagestr);
			// 获得输出文字方向
			direction = getStrFromProperties(format + ".direction", propertiesfile);
			if (direction == null || (direction.length() == 0))
				direction = "vertical";

			// 获得输出文字大小
			String fontsize = getStrFromProperties(format + ".fontsize", propertiesfile);
			if (fontsize == null || (fontsize.length() == 0) || fontsize.equals("0"))
				fontsize = "10";
			fontsizevalue = Float.parseFloat(fontsize);

			// 获得唯一值的属性
			String uniqueValueAttr = getStrFromProperties(format + ".uniquevalueattr", propertiesfile);
			int uniqueValueAttrCount = Integer.parseInt(uniqueValueAttr);
			for (int i = 1; i <= uniqueValueAttrCount; i++) {
				Hashtable ibas = new Hashtable();
				// 获得Key
				key = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".ibaattribute", propertiesfile);
				// 获得key值
				if ((key == null) || (key.length() == 0))
					continue;
				else {
					value = (String) hashtable.get(key);// 获得签审信息，要写入PDF的内容
					if (value != null && value.length() > 0) {
						// 获得坐标
						String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
						String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
						fontsize = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".fontsize", propertiesfile);
						log.debug("key is:" + key + ",value is " + value + ",x is:" + locationx + ",y is" + locationy);
						// 放入hashtable
						ibas.put(ATTR, key);
						ibas.put(ATTRVALUE, value);
						ibas.put(LOCATIONX, locationx);
						ibas.put(LOCATIONY, locationy);
						ibas.put(FONTSIZE, fontsize);
						ibas.put(OUTPUTPAGE, outputpage);
						allinfo.add(ibas);
					}
				}
			}

			// 获得主要文件名称
			String primaryfileName = getPrimaryFileName((FormatContentHolder) obj);

			// 获得打印稿附件名称
			String printFileName = getQualityFileName((FormatContentHolder) obj, primaryfileName, PRINTKEY);
			printFileName = Util.removeExtension(printFileName) + ".pdf";

			// 获得需要处理的附件文件名
			String targetFileName = getQualityFileName((FormatContentHolder) obj, primaryfileName, SOURCEKEY);
			targetFileName = Util.removeExtension(targetFileName) + ".pdf";

			// 查找需要处理的附件，并且下载到临时目录
			wt.content.ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) obj);
			Vector apps = ContentHelper.getApplicationData(contentHolder);

			for (int j = 0; j < apps.size(); j++) {
				ApplicationData applicationdata;
				applicationdata = (ApplicationData) apps.elementAt(j);
				String fileName = applicationdata.getFileName();
				// 应用数据的角色
				String applicationdataRole = applicationdata.getRole().toString();
				// 不是附件，处理下一个applicationdata. AutoCAD2006 检入的主文件角色是 "THUMBNAIL"
				if (!applicationdataRole.equalsIgnoreCase("SECONDARY"))
					continue;// 不是附件，处理下一个

				// 文件名称是要求签名的PDF附件，那么进行签名
				if (targetFileName.equals(fileName)) {
					String codebaseLocation = wtProperties.getProperty("wt.codebase.location");
					String fileDir = codebaseLocation + File.separator + "ext" + File.separator + "ases" + File.separator + "temp" + File.separator
							+ "writepdf";

					File file = new File(fileDir);
					if (!file.exists()) {
						file.mkdirs();
					}
					InputStream inputstream = ContentServerHelper.service.findContentStream(applicationdata);
					String finaleTargetFileName = fileDir + File.separator + targetFileName;
					String finalePrintFileName = fileDir + File.separator + printFileName;
					File tempFile = new File(finaleTargetFileName);
					String fileAbsolutePath = tempFile.getAbsolutePath();
					FileOutputStream tout = new FileOutputStream(fileAbsolutePath);
					byte abyte1[] = new byte[2048];
					int k;
					while ((k = inputstream.read(abyte1, 0, abyte1.length)) >= 0)
						tout.write(abyte1, 0, k);
					tout.close();
					// 向pdf写签审信息
					File printFile = PdfUtilities.writeToPDF(finaleTargetFileName, finalePrintFileName, allinfo, outputpage, direction, fontsizevalue, format);
					// 上载打印稿pdf为附件
					if (printFile != null && printFileName != null) {
						saveFiletoAttachment((ContentHolder) obj, printFile.getAbsolutePath(), overWrite, printFileName);
						// 删除源PDF文件
						if (overWrite && targetFileName != null && targetFileName.length() > 0) {
							removeAttachment((Representable) obj, targetFileName);
						}
					}
					// 清除临时文件
					cleanTempFile(fileDir);
					break;
				}
			}
			allinfo.clear();
		} catch (WTException wte) {
			System.out.println(wte);
		} catch (java.io.UnsupportedEncodingException uee) {
			System.out.println(uee);
		} catch (WTPropertyVetoException wpve) {
			System.out.println(wpve);
		} catch (PropertyVetoException pve) {
			System.out.println(pve);
		} catch (java.io.IOException ioe) {
			System.out.println(ioe);
		}
	}

	/*
	 * 用于转阶段，在上一版本的打印pdf基础上进行处理
	 */

	public static void writeReviewtoPDF4Doc(WTObject obj, Hashtable hashtable, boolean overWrite, String format, String processType) {
		String signTemplate = "";
		Vector allinfo = new Vector();
		int outputpage;
		String direction;
		float fontsizevalue;
		try {
			String value = "";
			String key = "";
			String times = "";
			boolean flag = false;
			// 获得输出的页位置
			String outputpagestr = getStrFromProperties(format + ".outputpage", propertiesfile);
			outputpage = Integer.parseInt(outputpagestr);
			// 获得输出文字方向
			direction = getStrFromProperties(format + ".direction", propertiesfile);
			if (direction == null || (direction.length() == 0))
				direction = "vertical";

			// 获得输出文字大小
			String fontsize = getStrFromProperties(format + ".fontsize", propertiesfile);
			if (fontsize == null || (fontsize.length() == 0) || fontsize.equals("0"))
				fontsize = "10";
			fontsizevalue = Float.parseFloat(fontsize);

			// 获得唯一值的属性
			String uniqueValueAttr = getStrFromProperties(format + ".uniquevalueattr", propertiesfile);
			int uniqueValueAttrCount = Integer.parseInt(uniqueValueAttr);

			for (int i = 1; i <= uniqueValueAttrCount; i++) {
				Hashtable ibas = new Hashtable();
				// 获得Key
				key = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".ibaattribute", propertiesfile);
				// 获得key值
				if ((key == null) || (key.length() == 0))
					continue;
				else {
					value = (String) hashtable.get(key);// 获得签审信息，要写入PDF的内容
					if (value != null && value.length() > 0) {
						// 获得坐标
						String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
						String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
						log.debug("value is " + value);
						// 放入hashtable
						ibas.put(ATTR, key);
						ibas.put(ATTRVALUE, value);
						ibas.put(LOCATIONX, locationx);
						ibas.put(LOCATIONY, locationy);
						allinfo.add(ibas);
					}
				}
			}

			// 获得主要文件名称
			String primaryfileName = getPrimaryFileName((FormatContentHolder) obj);

			// 获得打印稿附件名称
			String printFileName = getQualityFileName((FormatContentHolder) obj, primaryfileName, PRINTKEY);
			printFileName = Util.removeExtension(printFileName) + ".pdf";

			// 获得需要处理的附件文件名
			String targetFileName = "";

			// 查找需要处理的附件，并且下载到临时目录
			wt.content.ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) obj);
			Vector apps = ContentHelper.getApplicationData(contentHolder);

			for (int j = 0; j < apps.size(); j++) {
				ApplicationData applicationdata;
				applicationdata = (ApplicationData) apps.elementAt(j);
				String fileName = applicationdata.getFileName();
				log.debug("fileName is: " + fileName);
				// 应用数据的角色
				String applicationdataRole = applicationdata.getRole().toString();
				// 不是附件，处理下一个applicationdata. AutoCAD2006 检入的主文件角色是 "THUMBNAIL"
				if (!applicationdataRole.equalsIgnoreCase("SECONDARY"))
					continue;// 不是附件，处理下一个

				// 文件名称是要求签名的PDF附件，那么进行签名
				String formula = "";
				String prefix = "";
				formula = getStrFromProperties(PRINTKEY + "PDF.formula", filePrintProperties);
				prefix = formula.substring(0, formula.indexOf(")") + 1);
				prefix = prefix.substring(prefix.indexOf("(") + 1, prefix.lastIndexOf(")"));
				prefix = getStrFromProperties(prefix, filePrintProperties);
				if (fileName.startsWith(prefix)) {
					targetFileName = fileName;
					log.debug("targetFileName is: " + targetFileName);
					String codebaseLocation = wtProperties.getProperty("wt.codebase.location");
					String fileDir = codebaseLocation + File.separator + "ext" + File.separator + "ases" + File.separator + "temp" + File.separator
							+ "writepdf";

					File file = new File(fileDir);
					if (!file.exists()) {
						file.mkdirs();
					}
					InputStream inputstream = ContentServerHelper.service.findContentStream(applicationdata);
					String finaleTargetFileName = fileDir + File.separator + targetFileName;
					String finalePrintFileName = fileDir + File.separator + printFileName;
					File tempFile = new File(finaleTargetFileName);
					String fileAbsolutePath = tempFile.getAbsolutePath();
					FileOutputStream tout = new FileOutputStream(fileAbsolutePath);
					byte abyte1[] = new byte[2048];
					int k;
					while ((k = inputstream.read(abyte1, 0, abyte1.length)) >= 0)
						tout.write(abyte1, 0, k);
					tout.close();
					// 向pdf写签审信息
					File printFile = PdfUtilities.writeToPDF(finaleTargetFileName, finalePrintFileName, allinfo, outputpage, direction, fontsizevalue, format);
					// 上载打印稿pdf为附件
					if (printFile != null && printFileName != null) {
						saveFiletoAttachment((ContentHolder) obj, printFile.getAbsolutePath(), overWrite, printFileName);
						// 删除源PDF文件
						if (overWrite && targetFileName != null && targetFileName.length() > 0) {
							removeAttachment((Representable) obj, targetFileName);
						}
					}
					// 清除临时文件
					cleanTempFile(fileDir);
					break;
				}
			}
			allinfo.clear();
		} catch (WTException wte) {
			System.out.println(wte);
		} catch (java.io.UnsupportedEncodingException uee) {
			System.out.println(uee);
		} catch (WTPropertyVetoException wpve) {
			System.out.println(wpve);
		} catch (PropertyVetoException pve) {
			System.out.println(pve);
		} catch (java.io.IOException ioe) {
			System.out.println(ioe);
		}
	}

	public static void writeReviewtoPDF4ChangeOrder(WTObject obj, Hashtable hashtable, boolean overWrite, Object pbo) {
		String signTemplate = "";
		Vector allinfo = new Vector();
		int outputpage;
		String direction;
		float fontsizevalue;
		try {
			// 查找需要处理的附件，并且下载到临时目录
			wt.content.ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) obj);
			Vector apps = ContentHelper.getApplicationData(contentHolder);
			log.debug(obj.getIdentity() + " 有" + apps.size() + " 个附件");
			for (int j = 0; j < apps.size(); j++) {
				ApplicationData applicationdata;
				applicationdata = (ApplicationData) apps.elementAt(j);
				String fileName = applicationdata.getFileName();
				// 应用数据的角色
				String applicationdataRole = applicationdata.getRole().toString();
				// 不是附件，处理下一个applicationdata. AutoCAD2006 检入的主文件角色是 "THUMBNAIL"
				if (!applicationdataRole.equalsIgnoreCase("SECONDARY")) {
					continue; // 不是附件，继续
				}
				// 获得打印稿附件名称
				String formula = "";
				String prefix = "";
				formula = getStrFromProperties(PRINTKEY + "PDF.formula", filePrintProperties);
				prefix = formula.substring(0, formula.indexOf(")") + 1);
				prefix = prefix.substring(prefix.indexOf("(") + 1, prefix.lastIndexOf(")"));
				prefix = getStrFromProperties(prefix, filePrintProperties);
				// 文件名称是要求签名的PDF附件，那么进行签名
				if (((fileName.indexOf(".pdf") > -1) && (fileName.indexOf(prefix) < 0)) || fileName.startsWith("ForPrint")) {

					String codebaseLocation = wtProperties.getProperty("wt.codebase.location");
					String fileDir = codebaseLocation + File.separator + "ext" + File.separator + "casc" + File.separator + "temp" + File.separator
							+ "writepdf";
					File file = new File(fileDir);
					if (!file.exists()) {
						log.debug("...writeIBAtoPDF 建立目录 " + fileDir);
						file.mkdirs();
					}

					String printFileName = prefix + "_" + ((WTChangeOrder2) obj).getNumber() + "_" + ((WTChangeOrder2) obj).getName() + "_" + fileName;
					printFileName = printFileName.replaceAll("/", "_");
					InputStream inputstream = ContentServerHelper.service.findContentStream(applicationdata);
					String finaleTargetFileName = fileDir + File.separator + fileName;
					if ("PDFPreview.pdf".equals(fileName)) {
						String ecnNumber = ((WTChangeOrder2) obj).getNumber();
						ecnNumber = ecnNumber.replaceAll("/", "_");
						finaleTargetFileName = fileDir + File.separator + "PDFPreview_" + ecnNumber + ".pdf";
					}
					String finalePrintFileName = fileDir + File.separator + printFileName;
					log.debug("---------finaleTargetFileName:" + finaleTargetFileName);
					log.debug("---------finalePrintFileName:" + finalePrintFileName);
					File tempFile = new File(finaleTargetFileName);
					String fileAbsolutePath = tempFile.getAbsolutePath();
					FileOutputStream tout = new FileOutputStream(fileAbsolutePath);
					byte abyte1[] = new byte[2048];
					int k;
					while ((k = inputstream.read(abyte1, 0, abyte1.length)) >= 0) {
						tout.write(abyte1, 0, k);
					}
					tout.close();
					String extention = Util.getExtension(finaleTargetFileName);
					String removeExtention = Util.removeExtension(finaleTargetFileName);
					File file1 = new File(finaleTargetFileName);
					PdfReader reader = new PdfReader(finaleTargetFileName);
					Rectangle pageSize = reader.getPageSize(1);// 595.92X842.0
					float width = pageSize.getWidth();
					// A0:3370,A1:2384,A2:1684,A3:1190,A4:595
					String format = "FORMATECNA4";
					/*
					 * 更改单只有A4，不再判断图幅 if(width>841F&&width<842F) //变更单是横向打印
					 * format = "FORMATECNA4"; else format = "FORMATECNA3";
					 */
					String type = IBAHelper.getSoftType(obj);
					log.debug("*************type:" + type);
					if (type.equalsIgnoreCase("PROCESS_ECN") || type.equalsIgnoreCase("Process_reportTechnics_ECN")
						|| type.equalsIgnoreCase("DOCUMENT_ECN")) {
						if (isOldPrint(obj)) {
							format = "FORMATGYECNA4_OLD";
						} else {
							format = "FORMATGYECNA4";
						}

					}

					log.debug("format is:" + format);
					// 根据附件获取纸张大小，
					String value = "";
					String key = "";
					String times = "";
					boolean flag = false;
					// 获得输出的页位置
					String outputpagestr = getStrFromProperties(format + ".outputpage", propertiesfile);
					outputpage = Integer.parseInt(outputpagestr);
					// log.debug("输出的页位置 is "+outputpagestr);
					// 获得输出文字方向
					direction = getStrFromProperties(format + ".direction", propertiesfile);
					if (direction == null || (direction.length() == 0)) {
						direction = "vertical";
					}
					// 获得输出文字大小
					String fontsize = getStrFromProperties(format + ".fontsize", propertiesfile);
					if (fontsize == null || (fontsize.length() == 0) || fontsize.equals("0")) {
						fontsize = "10";
					}
					fontsizevalue = Float.parseFloat(fontsize);

					// 获得唯一值的属性
					String uniqueValueAttr = getStrFromProperties(format + ".uniquevalueattr", propertiesfile);
					int uniqueValueAttrCount = Integer.parseInt(uniqueValueAttr);
					log.debug(">>>>>>>hashtable:" + hashtable);
					for (int i = 1; i <= uniqueValueAttrCount; i++) {
						Hashtable ibas = new Hashtable();
						int size = 0;
						// 获得Key
						log.debug("---- key-value:" + format + ".uniquevalueattr" + Integer.toString(i) + ".ibaattribute");
						key = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".ibaattribute", propertiesfile);
						log.debug("-----key:" + key);
						if ((key == null) || (key.length() == 0)) {
							continue;
						} else if (key.equals("HUIQIAN")) {
							String value1 = (String) hashtable.get("NEIBUHUIQIAN");
							// String value2 = (String)
							// hashtable.get("NEIBUGONGYIHUIQIAN");
							String value3 = (String) hashtable.get("WAIBUHUIQIAN");
							String value4 = getGongYiValue(hashtable, pbo, obj);
							String value6 = getHuiQianValue(hashtable, pbo, obj);
							// String value5 = (String)
							// hashtable.get("WAIBUGONGYIHUIQIAN");
							// String value6 = (String)
							// hashtable.get("GONGYIHUIQIAN");
							String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
							String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
							String xspace = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".xspace", propertiesfile);
							String yspace = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".yspace", propertiesfile);
							String zx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".zx", propertiesfile);
							String zy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".zy", propertiesfile);
							String zyspace = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".zyspace", propertiesfile);
							outputpage = Integer.parseInt(outputpagestr);
							log.debug("value xspace:" + xspace + "  yspace:" + yspace + " zx:" + zx + "  zy:" + zy + "  zyspace:" + zyspace);
							log.debug("value locationx:" + locationx + "   locationy:" + locationy);
							String tempValue = "";
							log.debug("value1-6 is: " + value1 + " " + value3 + " " + value4 + " " + value6);
							log.debug("tempValue0 is: " + tempValue);
							if (value1 != null && value1.length() > 0) {
								if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null")) {
									tempValue = value1;
								} else {
									tempValue = tempValue + ";" + value1;
								}
							}
							// log.debug("tempValue1 is: " + tempValue);
							// if (value2 != null && value2.length() > 0) {
							// if (tempValue == null ||
							// tempValue.trim().equals("") ||
							// tempValue.trim().equals("null"))
							// {
							// tempValue = value2;
							// } else {
							// tempValue = tempValue + ";" + value2;
							// }
							// }
							log.debug("tempValue2 is: " + tempValue);
							if (value3 != null && value3.length() > 0) {
								if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null")) {
									tempValue = value3;
								} else {
									tempValue = tempValue + ";" + value3;
								}
							}
							log.debug("tempValue3 is: " + tempValue);
							if (value4 != null && value4.length() > 0) {
								if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null")) {
									tempValue = value4;
								} else {
									tempValue = tempValue + ";" + value4;
								}
							}
							// log.debug("tempValue4 is: " + tempValue);
							// if (value5 != null && value5.length() > 0) {
							// if (tempValue == null ||
							// tempValue.trim().equals("") ||
							// tempValue.trim().equals("null"))
							// {
							// tempValue = value5;
							// } else {
							// tempValue = tempValue + ";" + value5;
							// }
							// }
							log.debug("tempValue5 is: " + tempValue);
							if (value6 != null && value6.length() > 0) {
								if (tempValue == null || tempValue.trim().equals("") || tempValue.trim().equals("null")) {
									tempValue = value6;
								} else {
									tempValue = tempValue + ";" + value6;
								}
							}
							log.debug("tempValue6 is: " + tempValue);
							value = tempValue;
							String hqValue[] = value.split(";");
							String locationyy = locationy;// y的原高度
							String locationxx = locationx; // x的原高度
							log.debug("hqValue[] size is: " + hqValue.length);
							for (int j1 = 0; j1 < hqValue.length; j1++) {
								float y = 0;
								if (type.equalsIgnoreCase("PROCESS_ECN") || type.equalsIgnoreCase("Process_reportTechnics_ECN")
									|| type.equalsIgnoreCase("DOCUMENT_ECN")) {
									if (j1 == 0) {
										y = Float.parseFloat(locationy);
									} else {
										y = Float.parseFloat(locationy) - Float.parseFloat(zyspace);
									}

								} else {
									if (j1 < 4) {
										y = Float.parseFloat(locationyy);
									} else if (j1 == 4) {
										if (zy != null && !"0".equals(zy)) {
											y = Float.parseFloat(zy);
										} else {
											y = Float.parseFloat(locationy) - Float.parseFloat(zyspace);
										}
									} else {
										y = Float.parseFloat(locationy) - Float.parseFloat(zyspace);
									}
								}
								locationy = String.valueOf(y);

								float x = 0;
								if (type.equalsIgnoreCase("PROCESS_ECN") || type.equalsIgnoreCase("Process_reportTechnics_ECN")
									|| type.equalsIgnoreCase("DOCUMENT_ECN")) {
									x = Float.parseFloat(locationxx);
								} else {
									if (j1 == 0) {
										x = Float.parseFloat(locationxx);
									} else if (j1 >= 4) {
										if (zx != null && !"0".equals(zx)) {
											x = Float.parseFloat(zx);
										} else {
											if (xspace != null && !"0".equals(xspace)) {
												x = Float.parseFloat(locationx) + Float.parseFloat(xspace);
											}
										}
									} else {
										if (xspace != null && !"0".equals(xspace)) {
											x = Float.parseFloat(locationx) + Float.parseFloat(xspace);
										} else {
											if (zx != null && !"0".equals(zx)) {
												x = Float.parseFloat(zx);
											}
										}
									}
								}
								locationx = String.valueOf(x);

								String hqSplitValue = hqValue[j1];
								String hqSplitValue1[] = hqSplitValue.split("/");
								if (hqSplitValue1.length == 3) {
									String str = hqSplitValue1[0] + "/" + hqSplitValue1[2];
									hqSplitValue1 = new String[] { str };// add
																			// by
																			// liangbo
								}
								if (j1 < 4) {
									for (int k1 = 0; k1 < hqSplitValue1.length; k1++) {
										String tempValue1 = hqSplitValue1[k1];
										Hashtable h = new Hashtable();
										if (j1 < 4) {
											if (k1 != 0) {
												if (yspace != null && !"0".equals(yspace)) {
													locationy = String.valueOf(Float.parseFloat(locationy) - Float.parseFloat(yspace));
												} else {
													locationy = String.valueOf(Float.parseFloat(locationy) - Float.parseFloat(zyspace));
												}

											}
										} else {
											if (k1 != 0) {
												locationy = String.valueOf(Float.parseFloat(locationy) - Float.parseFloat(zyspace));
											}
										}

										// 放入hashtable
										h.put(ATTR, key + String.valueOf(j1) + String.valueOf(k1));
										// System.out.println("hqSplitValue1.length is: "
										// + hqSplitValue1.length);
										if (hqSplitValue1.length == 3) {
											if (k1 == 0) {
												h.put(ATTRVALUE, hqSplitValue1[1]);
											}
											if (k1 == 1) {
												h.put(ATTRVALUE, hqSplitValue1[0]);
											}
											if (k1 == 2) {
												h.put(ATTRVALUE, tempValue1);
											}
											h.put(LOCATIONX, locationx);
											h.put(LOCATIONY, locationy);
											h.put(FONTSIZE, fontsize);
											h.put(OUTPUTPAGE, outputpage);
											h.put(DIRECTION, direction);
											allinfo.add(h);
										} else if (hqSplitValue1.length == 1) {
											locationx = String.valueOf(Float.parseFloat(locationx) - 20);
											// locationy =
											// String.valueOf(Float.parseFloat(locationy)
											// + Float.parseFloat(zyspace));
											h.put(ATTRVALUE, hqSplitValue1[0]);
											h.put(LOCATIONX, locationx);
											h.put(LOCATIONY, locationy);
											h.put(FONTSIZE, fontsize);
											h.put(OUTPUTPAGE, outputpage);
											h.put(DIRECTION, direction);
											allinfo.add(h);
										}

										log.debug("j1 is:" + j1 + "k1 is: " + k1 + "key is:" + key + ",value is " + tempValue1 + ",x is:" + locationx + ",y is"
												+ locationy);
										size++;
									}
								} else {
									Hashtable h = new Hashtable();
									// if(j1==4)
									locationy = String.valueOf(Float.parseFloat(locationy));
									// else
									// locationy =
									// String.valueOf(Float.parseFloat(locationy)-
									// Float.parseFloat(zyspace));

									// 放入hashtable
									// 先放放部门信息
									h.put(ATTR, key + String.valueOf(j1));

									// 修改 避免数组越界
									if (hqSplitValue1.length >= 3) {
										h.put(ATTRVALUE, hqSplitValue1[0] + " " + hqSplitValue1[2]);
									} else if (hqSplitValue1.length == 2) {
										h.put(ATTRVALUE, hqSplitValue1[1]);
									} else {
										String[] hqSplitValue2 = hqSplitValue.split("/");
										if (hqSplitValue2.length == 3) {
											h.put(ATTRVALUE, hqSplitValue1[0]);
											locationx = String.valueOf(Float.parseFloat(locationx) - 20);
											h.put(LOCATIONX, locationx);
										} else {
											h.put(ATTRVALUE, hqSplitValue);
											h.put(LOCATIONX, locationx);
										}
									}

									h.put(LOCATIONY, locationy);
									h.put(FONTSIZE, fontsize);
									h.put(OUTPUTPAGE, outputpage);
									h.put(DIRECTION, direction);
									allinfo.add(h);
									// h = new Hashtable();
									// y = Float.parseFloat(locationy) -
									// Float.parseFloat(yspace);
									// locationy = String.valueOf(y);
									// h.put(ATTR, key + String.valueOf(j1));
									//
									// // 修改 避免数组越界
									// if (hqSplitValue1.length >= 3) {
									// h.put(ATTRVALUE, hqSplitValue1[0] + " " +
									// hqSplitValue1[2]);
									// } else {
									// String[] hqSplitValue2 =
									// hqSplitValue.split("/");
									// if(hqSplitValue2.length == 3){
									// h.put(ATTRVALUE, hqSplitValue1[0]);
									// }else{
									// h.put(ATTRVALUE, hqSplitValue);
									// }
									// }
									//
									// h.put(LOCATIONX, locationx);
									// h.put(LOCATIONY, locationy);
									// h.put(FONTSIZE, fontsize);
									// h.put(OUTPUTPAGE, outputpage);
									// h.put(DIRECTION, direction);
									// allinfo.add(h);
									log.debug("j1 is:" + j1 + "key is:" + key + ",value is " + hqSplitValue + ",x is:" + locationx + ",y is" + locationy);
									size++;
								}
							}
						} else {
							value = (String) hashtable.get(key);// 获得签审信息，要写入PDF的内容
							if (value != null && value.length() > 0) {
								// 获得坐标
								String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
								String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
								log.debug("locationx is:" + locationx + " locationy is:" + locationy + " key is: " + key + " value is: " + value);
								// 放入hashtable
								if (key.equals("SHEJI") || key.equals("JIAODUI") || key.equals("SHENHE") || key.equals("GONGYI") || key.equals("BIAOSHEN")
										|| key.equals("PIZHUN") || key.equals("SHENHE1") || key.equals("SHENHE2")) {
									String value1[] = value.split("/");
									ibas.put(ATTR, key);
									ibas.put(ATTRVALUE, value1[0]);
									ibas.put(LOCATIONX, locationx);
									ibas.put(LOCATIONY, locationy);
									allinfo.add(ibas);
								} else {
									ibas.put(ATTR, key);
									ibas.put(ATTRVALUE, value);
									ibas.put(LOCATIONX, locationx);
									ibas.put(LOCATIONY, locationy);
									allinfo.add(ibas);
								}
							}
						}
					}

					log.debug("--------allinfo:" + allinfo);
					// 向pdf写签审信息
					File printFile = PdfUtilities.writeToPDFForECN(finaleTargetFileName, finalePrintFileName, allinfo, outputpage, direction, fontsizevalue);

					// 上载打印稿pdf为附件
					if (printFile != null && printFileName != null) {
						saveFiletoAttachment4ChangeOrder((ContentHolder) obj, printFile.getAbsolutePath(), overWrite, printFileName);
						// 删除源PDF文件
						if (finaleTargetFileName != null && finaleTargetFileName.length() > 0) {
							removeAttachment4ChangeOrder((ContentHolder) obj, finaleTargetFileName);
						}
					}

					// 清除临时文件
					// cleanTempFile(fileDir);
					// break;更改单需要处理多个附件
				}
			}
			allinfo.clear();
		} catch (WTException wte) {
			System.out.println(wte);
		} catch (java.io.UnsupportedEncodingException uee) {
			System.out.println(uee);
		} catch (WTPropertyVetoException wpve) {
			System.out.println(wpve);
		} catch (PropertyVetoException pve) {
			System.out.println(pve);
		} catch (java.io.IOException ioe) {
			System.out.println(ioe);
		}
	}

	private static boolean isOldPrint(WTObject obj) {
		if (obj instanceof WTChangeOrder2) {
			WTChangeOrder2 ecn = (WTChangeOrder2) obj;
			SimpleDateFormat dFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date lineDate;
			try {
				lineDate = dFormat.parse("2016/12/13 00:00:00");
				if (ecn.getCreateTimestamp().after(lineDate)) {
					return false;
				} else {
					return true;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (obj instanceof WTDocument) {
			WTDocument doc = (WTDocument) obj;
			SimpleDateFormat dFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date lineDate;
			try {
				lineDate = dFormat.parse("2016/12/13 00:00:00");
				if (doc.getCreateTimestamp().after(lineDate)) {
					return false;
				} else {
					return true;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	/*
	 * 用于转阶段通知单自动生成pdf
	 */
	public static void writeReviewtoPDF4ChangeOrderZJD(WTObject obj, Hashtable hashtable, boolean overWrite) {
		String signTemplate = "";
		Vector allinfo = new Vector();
		int outputpage;
		String direction;
		float fontsizevalue;
		try {
			// 获得打印稿附件名称
			String formula = "";
			String prefix = "";
			formula = getStrFromProperties(PRINTKEY + "PDF.formula", filePrintProperties);
			prefix = formula.substring(0, formula.indexOf(")") + 1);
			prefix = prefix.substring(prefix.indexOf("(") + 1, prefix.lastIndexOf(")"));
			prefix = getStrFromProperties(prefix, filePrintProperties);

			String codebaseLocation = wtProperties.getProperty("wt.codebase.location");
			String fileDir = codebaseLocation + File.separator + "ext" + File.separator + "ases" + File.separator + "temp" + File.separator + "writepdf";
			File file = new File(fileDir);
			if (!file.exists()) {
				log.debug("...writeIBAtoPDF 建立目录 " + fileDir);
				file.mkdirs();
			}

			// String printFileName = prefix + "_" +
			// ((WTChangeOrder2)obj).getNumber() + "_"
			// +((WTChangeOrder2)obj).getName() + "_转阶段更改单.pdf";
			String printFileName = "转阶段更改单.pdf";
			String templateFile = codebaseLocation + File.separator + "ext" + File.separator + "ases" + File.separator + "changephase" + File.separator
					+ "转阶段更改单.pdf";
			InputStream inputstream = new FileInputStream(templateFile);
			String finalePrintFileName = fileDir + File.separator + printFileName;
			FileOutputStream fs = new FileOutputStream(finalePrintFileName);
			int bytesum = 0;
			int byteread = 0;
			byte[] buffer = new byte[1444];
			while ((byteread = inputstream.read(buffer)) != -1) {
				bytesum += byteread;
				fs.write(buffer, 0, byteread);
			}
			inputstream.close();
			fs.close();

			String format = "FORMATZJDECNA4BZ";

			// 根据附件获取纸张大小，
			String value = "";
			String key = "";
			String times = "";
			boolean flag = false;
			// 获得输出的页位置
			String outputpagestr = getStrFromProperties(format + ".outputpage", propertiesfile);
			outputpage = Integer.parseInt(outputpagestr);
			// log.debug("输出的页位置 is "+outputpagestr);
			// 获得输出文字方向
			direction = getStrFromProperties(format + ".direction", propertiesfile);
			if (direction == null || (direction.length() == 0))
				direction = "vertical";

			// 获得输出文字大小
			String fontsize = getStrFromProperties(format + ".fontsize", propertiesfile);
			if (fontsize == null || (fontsize.length() == 0) || fontsize.equals("0"))
				fontsize = "10";
			fontsizevalue = Float.parseFloat(fontsize);

			// 获得唯一值的属性
			String uniqueValueAttr = getStrFromProperties(format + ".uniquevalueattr", propertiesfile);
			int uniqueValueAttrCount = Integer.parseInt(uniqueValueAttr);

			for (int i = 1; i <= uniqueValueAttrCount; i++) {
				Hashtable ibas = new Hashtable();
				int size = 0;
				// 获得Key
				key = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".ibaattribute", propertiesfile);
				// 获得key值
				if ((key == null) || (key.length() == 0))
					continue;
				else {
					value = (String) hashtable.get(key);// 获得签审信息，要写入PDF的内容
					if (value != null && value.length() > 0) {
						// 获得坐标
						String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
						String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
						log.debug("locationx is:" + locationx + " locationy is:" + locationy + " key is: " + key + " value is: " + value);
						// 放入hashtable
						if (key.equals("SHEJI") || key.equals("JIAODUI") || key.equals("SHENHE") || key.equals("GONGYI") || key.equals("BIAOSHEN")
								|| key.equals("PIZHUN")) {
							String value1[] = value.split("/");
							ibas.put(ATTR, key);
							ibas.put(ATTRVALUE, value1[0]);
							ibas.put(LOCATIONX, locationx);
							ibas.put(LOCATIONY, locationy);
							allinfo.add(ibas);
						} else {
							ibas.put(ATTR, key);
							ibas.put(ATTRVALUE, value);
							ibas.put(LOCATIONX, locationx);
							ibas.put(LOCATIONY, locationy);
							allinfo.add(ibas);
						}
					}
				}
			}

			// 向pdf写签审信息
			File printFile = PdfUtilities.writeToPDFForECN(templateFile, finalePrintFileName, allinfo, outputpage, direction, fontsizevalue);

			// 上载打印稿pdf为附件
			if (printFile != null && printFileName != null) {
				saveFiletoAttachment4ChangeOrder((ContentHolder) obj, printFile.getAbsolutePath(), overWrite, printFileName);
				// 删除源PDF文件
				// if(overWrite && finaleTargetFileName!=null &&
				// finaleTargetFileName.length()>0) {
				// removeAttachment4ChangeOrder((ContentHolder)obj,finaleTargetFileName);
				// }
			}
			// 清除临时文件
			cleanTempFile(fileDir);
			// break;更改单需要处理多个附件
			allinfo.clear();
		} catch (WTException wte) {
			System.out.println(wte);
		} catch (java.io.UnsupportedEncodingException uee) {
			System.out.println(uee);
		} catch (WTPropertyVetoException wpve) {
			System.out.println(wpve);
		} catch (PropertyVetoException pve) {
			System.out.println(pve);
		} catch (java.io.IOException ioe) {
			System.out.println(ioe);
		}
	}

	/*
	 * 用于作废申请单自动生成pdf
	 */
	public static void writeReviewtoPDF4ChangeOrderZFD(WTObject obj, Hashtable hashtable, boolean overWrite) {
		String signTemplate = "";
		Vector allinfo = new Vector();
		int outputpage;
		String direction;
		float fontsizevalue;
		try {
			// 获得打印稿附件名称
			String formula = "";
			String prefix = "";
			formula = getStrFromProperties(PRINTKEY + "PDF.formula", filePrintProperties);
			prefix = formula.substring(0, formula.indexOf(")") + 1);
			prefix = prefix.substring(prefix.indexOf("(") + 1, prefix.lastIndexOf(")"));
			prefix = getStrFromProperties(prefix, filePrintProperties);

			String codebaseLocation = wtProperties.getProperty("wt.codebase.location");
			String fileDir = codebaseLocation + File.separator + "ext" + File.separator + "ases" + File.separator + "temp" + File.separator + "writepdf";
			File file = new File(fileDir);
			if (!file.exists()) {
				log.debug("...writeIBAtoPDF 建立目录 " + fileDir);
				file.mkdirs();
			}

			// String printFileName = prefix + "_" +
			// ((WTChangeOrder2)obj).getNumber() + "_"
			// +((WTChangeOrder2)obj).getName() + "_转阶段更改单.pdf";
			String printFileName = "作废申请单.pdf";
			String templateFile = codebaseLocation + File.separator + "ext" + File.separator + "ases" + File.separator + "changephase" + File.separator
					+ "转阶段更改单.pdf";
			InputStream inputstream = new FileInputStream(templateFile);
			String finalePrintFileName = fileDir + File.separator + printFileName;
			FileOutputStream fs = new FileOutputStream(finalePrintFileName);
			int bytesum = 0;
			int byteread = 0;
			byte[] buffer = new byte[1444];
			while ((byteread = inputstream.read(buffer)) != -1) {
				bytesum += byteread;
				fs.write(buffer, 0, byteread);
			}
			inputstream.close();
			fs.close();

			String format = "FORMATZFDECNA4BZ";

			// 根据附件获取纸张大小，
			String value = "";
			String key = "";
			String times = "";
			boolean flag = false;
			// 获得输出的页位置
			String outputpagestr = getStrFromProperties(format + ".outputpage", propertiesfile);
			outputpage = Integer.parseInt(outputpagestr);
			// log.debug("输出的页位置 is "+outputpagestr);
			// 获得输出文字方向
			direction = getStrFromProperties(format + ".direction", propertiesfile);
			if (direction == null || (direction.length() == 0))
				direction = "vertical";

			// 获得输出文字大小
			String fontsize = getStrFromProperties(format + ".fontsize", propertiesfile);
			if (fontsize == null || (fontsize.length() == 0) || fontsize.equals("0"))
				fontsize = "10";
			fontsizevalue = Float.parseFloat(fontsize);

			// 获得唯一值的属性
			String uniqueValueAttr = getStrFromProperties(format + ".uniquevalueattr", propertiesfile);
			int uniqueValueAttrCount = Integer.parseInt(uniqueValueAttr);

			for (int i = 1; i <= uniqueValueAttrCount; i++) {
				Hashtable ibas = new Hashtable();
				int size = 0;
				// 获得Key
				key = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".ibaattribute", propertiesfile);
				// 获得key值
				if ((key == null) || (key.length() == 0))
					continue;
				else {
					value = (String) hashtable.get(key);// 获得签审信息，要写入PDF的内容
					if (value != null && value.length() > 0) {
						// 获得坐标
						String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".x", propertiesfile);
						String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".y", propertiesfile);
						log.debug("locationx is:" + locationx + " locationy is:" + locationy + " key is: " + key + " value is: " + value);
						// 放入hashtable
						if (key.equals("SHEJI") || key.equals("JIAODUI") || key.equals("SHENHE") || key.equals("GONGYI") || key.equals("BIAOSHEN")
								|| key.equals("PIZHUN")) {
							String value1[] = value.split("/");
							ibas.put(ATTR, key);
							ibas.put(ATTRVALUE, value1[0]);
							ibas.put(LOCATIONX, locationx);
							ibas.put(LOCATIONY, locationy);
							allinfo.add(ibas);
						} else {
							ibas.put(ATTR, key);
							ibas.put(ATTRVALUE, value);
							ibas.put(LOCATIONX, locationx);
							ibas.put(LOCATIONY, locationy);
							allinfo.add(ibas);
						}
					}
				}
			}

			// 向pdf写签审信息
			File printFile = PdfUtilities.writeToPDFForECN(templateFile, finalePrintFileName, allinfo, outputpage, direction, fontsizevalue);

			// 上载打印稿pdf为附件
			if (printFile != null && printFileName != null) {
				saveFiletoAttachment4ChangeOrder((ContentHolder) obj, printFile.getAbsolutePath(), overWrite, printFileName);
				// 删除源PDF文件
				// if(overWrite && finaleTargetFileName!=null &&
				// finaleTargetFileName.length()>0) {
				// removeAttachment4ChangeOrder((ContentHolder)obj,finaleTargetFileName);
				// }
			}
			// 清除临时文件
			cleanTempFile(fileDir);
			// break;更改单需要处理多个附件
			allinfo.clear();
		} catch (WTException wte) {
			System.out.println(wte);
		} catch (java.io.UnsupportedEncodingException uee) {
			System.out.println(uee);
		} catch (WTPropertyVetoException wpve) {
			System.out.println(wpve);
		} catch (PropertyVetoException pve) {
			System.out.println(pve);
		} catch (java.io.IOException ioe) {
			System.out.println(ioe);
		}
	}

	/**
	 * 根据对象类型获得签审模板。 图档类型的只处理了AutoCAD的。 EPM处理了proE和AotuCAD的。
	 *
	 * @param obj
	 * @return
	 */
	public static String getSignTemplate(WTObject obj) {
		String result = "";
		String tufuIBAName = "";
		String signTemplate = "";
		String type = "";

		try {
			// 获得表示签审格式的软属性的数值
			if (obj instanceof WTDocument) {// 只对图档类型做了处理
				tufuIBAName = getStrFromProperties("WTDocument.tufuAttribute", "ext.casc.fileprint.fileprint");
				type = "AUTOCAD";
			} else if (obj instanceof EPMDocument) {
				EPMDocument epm = (EPMDocument) obj;
				String app = epm.getAuthoringApplication().toString();

				tufuIBAName = getStrFromProperties("EPMDocument.tufuAttribute", "ext.casc.fileprint.fileprint");

				if (app.equalsIgnoreCase("ACAD")) {
					type = "AUTOCAD";
				} else if (app.equalsIgnoreCase("PROE")) {
					type = "PRT";
				} else {
					System.out.println("不是proE和AutoCAD,没有设置签名模板1");
				}

			}
			String tufuValue = "A4";// new String();

			String typeAndTufu = type + tufuValue;
			signTemplate = getStrFromProperties(typeAndTufu + ".frm", "ext.casc.fileprint.fileprint");

			if (signTemplate != null && (signTemplate.length() > 0))
				result = signTemplate;
		} catch (WTException wte) {
			System.out.println(wte);
		} catch (java.io.UnsupportedEncodingException uee) {
			System.out.println(uee);
		}

		return result;

	}

	public static String getQualityFileName(FormatContentHolder contentholder, String fileName, String type) {
		boolean hasnumber = false;
		boolean hasnumberversion = false;
		String separator = "";
		String formula = "";
		String prefix = "";
		String dockey = "";
		String filename = "";
		String realprefix = "";
		String realdockey = "";
		String realfilename = "";
		String qualityfilename = "";

		try {
			separator = getStrFromProperties("PDF.separator", filePrintProperties);
			formula = getStrFromProperties(type + "PDF.formula", filePrintProperties);
			prefix = formula.substring(0, formula.indexOf(")") + 1);
			prefix = prefix.substring(prefix.indexOf("(") + 1, prefix.lastIndexOf(")"));
			prefix = getStrFromProperties(prefix, filePrintProperties);
			if (prefix.equalsIgnoreCase("null"))// 不加前缀，配置null
				prefix = "";
			realprefix = prefix;

			formula = formula.substring(formula.indexOf(")") + 1, formula.length());
			dockey = formula.substring(0, formula.indexOf(")") + 1);
			dockey = dockey.substring(dockey.indexOf("(") + 1, dockey.lastIndexOf(")"));
			dockey = getStrFromProperties(dockey, filePrintProperties);
			if (dockey.equalsIgnoreCase("docnumber"))
				hasnumber = true;
			else if (dockey.equalsIgnoreCase("docnumber_version"))
				hasnumberversion = true;
			else {
				hasnumber = false;
				hasnumberversion = false;
			}
			if (contentholder instanceof WTDocument) {
				WTDocument doc = (WTDocument) contentholder;
				if (hasnumber && !hasnumberversion)
					realdockey = doc.getNumber();
				if (!hasnumber && hasnumberversion)
					realdockey = doc.getNumber() + "_" + doc.getVersionIdentifier().getValue();
			} else if (contentholder instanceof EPMDocument) {
				EPMDocument doc = (EPMDocument) contentholder;
				String number = doc.getNumber();
				if (number.contains(".")) {
					number = number.substring(0, number.lastIndexOf('.'));
				}
				if (hasnumber && !hasnumberversion)
					realdockey = doc.getNumber();
				if (!hasnumber && hasnumberversion)
					realdockey = doc.getNumber() + "_" + doc.getVersionIdentifier().getValue();
			} else if (contentholder instanceof WTChangeOrder2) {
				WTChangeOrder2 order = (WTChangeOrder2) contentholder;
				realdockey = order.getNumber();
			}

			formula = formula.substring(formula.indexOf(")") + 1, formula.length());
			filename = formula;
			filename = filename.substring(filename.indexOf("(") + 1, filename.lastIndexOf(")"));
			filename = getStrFromProperties(filename, filePrintProperties);
			if (filename.equalsIgnoreCase("filename"))
				realfilename = fileName;

			if (filename.equalsIgnoreCase("docnumber"))
				realfilename = fileName;

			if (realprefix != null && realprefix.length() > 0)
				qualityfilename = realprefix;
			if (realdockey != null && realdockey.length() > 0) {
				if (qualityfilename.length() > 0)
					qualityfilename = qualityfilename + separator + realdockey;
				else
					qualityfilename = realdockey;
			}

			if (realfilename != null && realfilename.length() > 0) {
				if (qualityfilename.length() > 0)
					qualityfilename = qualityfilename + separator + realfilename;
				else
					qualityfilename = realfilename;
			}
		} catch (WTException wte) {
			System.out.println(wte);
		} catch (java.io.UnsupportedEncodingException uee) {
			System.out.println(uee);
		}
		log.debug("----------qualityfilename:" + qualityfilename);
		if (qualityfilename.contains(".DRW")) {
			qualityfilename = qualityfilename.substring(0, qualityfilename.lastIndexOf(".DRW"))
					+ qualityfilename.substring(qualityfilename.lastIndexOf(".DRW") + 4, qualityfilename.length());
		}
		return qualityfilename;
	}

	public static String getPrimaryFileName(FormatContentHolder contentholder) {
		String result = "";
		try {
			wt.content.ContentItem contentitem = ContentHelper.service.getPrimary(contentholder);
			ApplicationData applicationdataPrimary = null;
			if (contentitem != null) {
				applicationdataPrimary = (ApplicationData) contentitem;
				String fileName = applicationdataPrimary.getFileName();

				if (!fileName.equals("{$CAD_NAME}"))
					result = fileName;
				else {
					EPMDocument epm = (EPMDocument) contentholder;
					result = epm.getCADName();
				}
			}

		} catch (WTException wte) {
			System.out.println(wte);
		} catch (PropertyVetoException pve) {
			System.out.println(pve);
		}

		return result;
	}

	public static String getStrFromProperties(String key, String propertiefile) throws WTException, UnsupportedEncodingException, MissingResourceException {

		String strinfo = "";
		try {
			PropertyResourceBundle prBundle = (PropertyResourceBundle) PropertyResourceBundle.getBundle(propertiefile);
			byte[] temp = null;
			temp = key.getBytes("GB2312");
			key = new String(temp, "ISO-8859-1");
			temp = prBundle.getString(key).getBytes("ISO-8859-1");
			strinfo = new String(temp, "GB2312");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return strinfo;

	}

	public static void saveFiletoAttachment4ChangeOrder(ContentHolder contentholder, String filename, boolean flag, String targetFileName) throws WTException,
			WTPropertyVetoException, PropertyVetoException, IOException {
		if (flag && targetFileName != null && targetFileName.length() > 0) {
			removeAttachment4ChangeOrder(contentholder, targetFileName);
		}

		ApplicationData appData = ApplicationData.newApplicationData(contentholder);
		appData.setRole(ContentRoleType.SECONDARY);
		ContentServerHelper.service.updateContent(contentholder, appData, filename);
	}

	public static ContentHolder removeAttachment4ChangeOrder(ContentHolder contentholder, String attachName) throws WTException, PropertyVetoException {
		try {
			ContentHolder changeOrder = ContentHelper.service.getContents(contentholder);
			Vector apps = ContentHelper.getApplicationData(changeOrder);

			for (Enumeration e = apps.elements(); e.hasMoreElements();) {
				ApplicationData contentItem = (ApplicationData) e.nextElement();

				if (contentItem.getFileName().equalsIgnoreCase(attachName)) {
					ContentServerHelper.service.deleteContent(changeOrder, contentItem);
				}

			}
		} catch (WTPropertyVetoException wtpve) {
			wtpve.printStackTrace();

		}
		return contentholder;
	}

	public static void saveFiletoAttachment(ContentHolder contentholder, FileInputStream fileinputstream) throws WTException, WTPropertyVetoException,
			PropertyVetoException, IOException {
		ApplicationData appData = ApplicationData.newApplicationData(contentholder);
		appData.setRole(ContentRoleType.SECONDARY);
		ContentServerHelper.service.updateContent(contentholder, appData, fileinputstream);
	}

	public static void saveFiletoAttachment(ContentHolder contentholder, String filename, boolean flag, String targetFileName) throws WTException,
			WTPropertyVetoException, PropertyVetoException, IOException {
		if (flag && targetFileName != null && targetFileName.length() > 0) {
			removeAttachment4ChangeOrder((ContentHolder) contentholder, targetFileName);
		}
		ApplicationData appData = ApplicationData.newApplicationData(contentholder);
		appData.setRole(ContentRoleType.SECONDARY);
		ContentServerHelper.service.updateContent(contentholder, appData, filename);
	}

	public static void replaceRepPdfFile(Representable representable, String filename) throws WTException, PropertyVetoException, FileNotFoundException,
			IOException {
		System.out.println("---------------------replaceRepPdfFile :" + representable);
		// 如果主内容是AutoCAD，则不处理它的可视化文件
		if (representable instanceof WTDocument) {
			ContentItem item = (ContentItem) ContentHelper.service.getPrimary((WTDocument) representable);
			if (item != null && item instanceof ApplicationData) {
				if (((ApplicationData) item).getFileName().toUpperCase().endsWith(".DWG")) {
					return;
				}
			} else {
				System.out.println("item is null");
			}
		}

		Representation representation = RepresentationHelper.service.getDefaultRepresentation(representable);
		if (representation == null) {
			return;
		}
		QueryResult qr2 = ContentHelper.service.getContentsByRole(representation, ContentRoleType.SECONDARY);
		System.out.println("----------qr2:" + qr2.size());
		while (qr2.hasMoreElements()) {
			ApplicationData applicationdata = (ApplicationData) qr2.nextElement();
			String name = applicationdata.getFileName();
			System.out.println("-------applicationdata:" + name);
			System.out.println("-------filename:" + filename);
			if ("PDF".equalsIgnoreCase(applicationdata.getFormat().getDataFormat().getFormatName().trim())) {
				updateSignSecondaryContent(representation, filename, applicationdata);
			}
		}
	}

	/**
	 * 将旧的表示法删除掉，new一个新的，同时将原有的信息写回去，目的是将带有签名的pdf放到表示法中去
	 *
	 * @param holder
	 * @param filePath
	 * @param oldAd
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws PropertyVetoException
	 * @throws IOException
	 */
	private static void updateSignSecondaryContent(ContentHolder holder, String filePath, ApplicationData oldAd) throws WTException, FileNotFoundException,
			PropertyVetoException, IOException {
		WTPrincipalReference wpf = null;
		String fileName = "";
		String description = "";
		String comments = "";
		System.out.println("oldAd===" + oldAd);
		if (oldAd != null) {// delete the
			// old sign file
			wpf = oldAd.getModifiedBy();
			description = oldAd.getDescription();
			oldAd.getComments();
			fileName = oldAd.getFileName();
			System.out.println("fileName===" + fileName);
			ContentServerHelper.service.deleteContent(holder, oldAd);
		}
		ApplicationData ad = ApplicationData.newApplicationData(holder);
		ad.setRole(ContentRoleType.SECONDARY);
		ad.setComments("已签名");
		ContentServerHelper.service.updateContent(holder, ad, filePath);
		if (fileName != null && !"".equalsIgnoreCase(fileName)) {
			ad.setFileName(fileName);
		}
		if (wpf != null) {
			ad.setModifiedBy(wpf);
			ad.setCreatedBy(wpf);
		}
		if (description != null && !"".equalsIgnoreCase(description)) {
			ad.setDescription(description);
		}
		PersistenceServerHelper.manager.update(ad);
	}

	public static Representable removeAttachment(Representable representable, String attachName) throws WTException, PropertyVetoException {
		try {
			wt.content.ContentHolder doc = ContentHelper.service.getContents(representable);
			Vector apps = ContentHelper.getApplicationData(doc);
			for (Enumeration e = apps.elements(); e.hasMoreElements();) {
				ApplicationData contentItem = (ApplicationData) e.nextElement();

				if (contentItem.getFileName().equalsIgnoreCase(attachName)) {
					ContentServerHelper.service.deleteContent(doc, contentItem);
				}

			}
		} catch (WTPropertyVetoException wtpve) {
			wtpve.printStackTrace();

		}
		return representable;
	}

	public static void cleanTempFile(String filedir) {
		try {
			File parent = new File(filedir);
			if (parent.exists() && parent.isDirectory()) {
				File files[] = parent.listFiles();
				for (int i = 0; i < files.length; i++) {
					files[i].delete();
				}

			}
			// if(parent != null && parent.exists())
			// parent.delete();
		} catch (Exception e) {
			System.out.print("...cleanTempFile:删除临时文件出错 " + e);
		}
	}

	/*
	 * 将可视化添加为附件，用于电子签名 此为通用方法
	 */
	public static Vector addRepToAttachment(Vector reviewList) {
		int listSize = reviewList.size();
		Vector returnedVector = new Vector();
		Vector failedVector = new Vector();
		String faileddocName = "";
		if (listSize <= 0)
			return null;
		try {
			for (int i = 0; i < reviewList.size(); i++) {
				WTObject obj = (WTObject) reviewList.elementAt(i);
				if ((obj instanceof WTDocument) || (obj instanceof EPMDocument)) {
					// 获得文档对象的表示法对象
					Representation representation = RepresentationHelper.service.getDefaultRepresentation((Representable) obj);

					// 把表示法存为附件
					WVSHelper.service.repToAttachment((Representable) obj, true);
					// 如果表示法不存在，则把obj对象存储到failedVector，同时获得obj的Name
					// returnedVector对象存储表示法生成失败的failedVector和这些对象的名称组合字符串faileddocName
					if (representation == null) {
						failedVector.add(obj);
						if (obj instanceof WTDocument) {
							WTDocument wtdoc = (WTDocument) obj;
							faileddocName = faileddocName + wtdoc.getDisplayIdentity() + ";;;";
						} else if (obj instanceof EPMDocument) {
							EPMDocument epmdoc = (EPMDocument) obj;
							faileddocName = faileddocName + epmdoc.getIdentity() + ";;;";
						}
					}
				}
			}
			if (failedVector.size() > 0) {
				returnedVector.add(faileddocName);
				returnedVector.add(failedVector);
			} else
				returnedVector = null;
		} catch (WTException wte) {
			wte.printStackTrace();
		} catch (java.beans.PropertyVetoException pve) {
			pve.printStackTrace();
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		}
		return returnedVector;
	}

	/*
	 * 将可视化添加为附件，用于电子签名 由于转阶段的处理方法不同，此方法增加变量processType，如果processType是ZJD
	 * （转阶段），则不再添加附件
	 */
	public static Vector addRepToAttachment(Vector reviewList, String processType) {
		int listSize = reviewList.size();
		Vector returnedVector = new Vector();
		Vector failedVector = new Vector();
		String faileddocName = "";
		if (processType.equalsIgnoreCase("ZJD"))
			return null;
		if (listSize <= 0)
			return null;
		try {
			for (int i = 0; i < reviewList.size(); i++) {
				WTObject obj = (WTObject) reviewList.elementAt(i);
				if ((obj instanceof WTDocument) || (obj instanceof EPMDocument)) {
					// 获得文档对象的表示法对象
					Representation representation = RepresentationHelper.service.getDefaultRepresentation((Representable) obj);
					// 把表示法存为附件
					WVSHelper.service.repToAttachment((Representable) obj, true);
					// 如果表示法不存在，则把obj对象存储到failedVector，同时获得obj的Name
					// returnedVector对象存储表示法生成失败的failedVector和这些对象的名称组合字符串faileddocName
					if (representation == null) {
						failedVector.add(obj);
						if (obj instanceof WTDocument) {
							WTDocument wtdoc = (WTDocument) obj;
							faileddocName = faileddocName + wtdoc.getDisplayIdentity() + ";;;";
						} else if (obj instanceof EPMDocument) {
							EPMDocument epmdoc = (EPMDocument) obj;
							faileddocName = faileddocName + epmdoc.getIdentity() + ";;;";
						}
					}
				}
			}
			if (failedVector.size() > 0) {
				returnedVector.add(faileddocName);
				returnedVector.add(failedVector);
			} else
				returnedVector = null;
		} catch (WTException wte) {
			wte.printStackTrace();
		} catch (java.beans.PropertyVetoException pve) {
			pve.printStackTrace();
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		}
		return returnedVector;
	}

	/**
	 * 签名测试
	 *
	 * @param num
	 * @param type
	 */
	public static void test(String num, String type) {
		try {
			if (!RemoteMethodServer.ServerFlag) {
				System.out.println("Begin invoke remomte ....");
				RemoteMethodServer.getDefault().invoke("test", FilePrintUtil2.class.getName(), null, new Class[] { String.class, String.class },
						new Object[] { num, type });
				System.out.println("done ....");
				return;
			}

			QuerySpec qs;
			String refStr = null;
			ReferenceFactory rf = new ReferenceFactory();
			if ("doc".equalsIgnoreCase(type)) {
				qs = new QuerySpec(WTDocument.class);
				qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, num, false));
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTDocument.class, Iterated.ITERATION_INFO + "." + IterationInfo.LATEST, SearchCondition.IS_TRUE));
				QueryResult qr = PersistenceHelper.manager.find(qs);
				if (qr.hasMoreElements()) {
					WTDocument doc = (WTDocument) qr.nextElement();
					System.out.println("Doc:" + doc.getIdentity());
					refStr = rf.getReferenceString(doc);
				}

			} else {
				qs = new QuerySpec(EPMDocument.class);
				qs.appendWhere(new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.EQUAL, num, false));
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(EPMDocument.class, Iterated.ITERATION_INFO + "." + IterationInfo.LATEST, SearchCondition.IS_TRUE));
				QueryResult qr = PersistenceHelper.manager.find(qs);
				if (qr.hasMoreElements()) {
					EPMDocument epm = (EPMDocument) qr.nextElement();
					System.out.println("EPM:" + epm.getIdentity());
					refStr = rf.getReferenceString(epm);
				}
			}

			Hashtable wfInfoTable = new Hashtable();
			wfInfoTable.put("SHEJIZHESHIJIAN", "编制人/十一室/2010-05-17");
			wfInfoTable.put("SHEJI", "编制人/十一室/2010-05-17");
			wfInfoTable.put("SHEJISHIJIAN", "2010-05-17");

			wfInfoTable.put("JIAODUIZHESHIJIAN", "校对人/十一室/2010-05-17");
			wfInfoTable.put("JIAODUI", "校对人/十一室/2010-05-17");
			wfInfoTable.put("JIAODUISHIJIAN", "2010-05-17");

			wfInfoTable.put("SHENHEZHESHIJIAN", "审核人/十一室/2010-05-17");
			wfInfoTable.put("SHENHE", "审核人/十一室/2010-05-17");
			wfInfoTable.put("SHENHESHIJIAN", "2010-05-17");

			wfInfoTable.put("HUIQIAN1ZHESHIJIAN", "会签人/十一室/2010-05-17");
			wfInfoTable.put("HUIQIAN1", "会签人/十一室/2010-05-17");
			wfInfoTable.put("HUIQIAN1SHIJIAN", "2010-05-17");

			wfInfoTable.put("NEIBUHUIQIAN", "会签人/十一室/2010-05-17");

			wfInfoTable.put("HUIQIAN2ZHESHIJIAN", "W会签/十一室/2010-05-17");
			wfInfoTable.put("HUIQIAN2", "W会签/十一室/2010-05-17");
			wfInfoTable.put("HUIQIAN2SHIJIAN", "2010-05-17");

			wfInfoTable.put("WAIBUHUIQIAN", "WB会签人/十一室/2010-05-17");

			wfInfoTable.put("HUIQIAN4ZHESHIJIAN", "G会签/十一室/2010-05-17");
			wfInfoTable.put("HUIQIAN4", "G会签/十一室/2010-05-17");
			wfInfoTable.put("SHEJISHIJIAN", "2010-05-17");

			wfInfoTable.put("NEIBUGONGYIHUIQIAN", "G会签/十一室/2010-05-17");

			wfInfoTable.put("HUIQIAN5ZHESHIJIAN", "WG签/十一室/2010-05-17");
			wfInfoTable.put("HUIQIAN5", "WG签/十一室/2010-05-17");
			wfInfoTable.put("SHEJISHIJIAN", "2010-05-17");

			wfInfoTable.put("WAIBUGONGYIHUIQIAN", "WG签/十一室/2010-05-17");

			wfInfoTable.put("149GONGYIHUIQIAN", "149签/十一室/2010-05-17");

			wfInfoTable.put("BIAOSHENZHESHIJIAN", "标审人/十一室/2010-05-17");
			wfInfoTable.put("BIAOSHEN", "标审人/十一室/2010-05-17");
			wfInfoTable.put("BIAOSHENSHIJIAN", "2010-05-17");

			wfInfoTable.put("PIZHUNZHESHIJIAN", "标审人/十一室/2010-05-17");
			wfInfoTable.put("PIZHUN", "标审人/十一室/2010-05-17");
			wfInfoTable.put("PIZHUNSHIJIAN", "2010-05-17");

			wfInfoTable.put("SHIYONGFANG", "使用方");

			wfInfoTable.put("NUMBER", "100000001");
			wfInfoTable.put("NAME", "测试名称");
			wfInfoTable.put("MIJI", "秘密（5年）");
			wfInfoTable.put("DEPT", "部门");
			wfInfoTable.put("SUMMARY", "测试summary");
			wfInfoTable.put("KEYWORD", "关重件标识");
			wfInfoTable.put("ECN_NUMBER", "789708080");
			wfInfoTable.put("ECN_MODIFYTIME", "2010-05-18");
			wfInfoTable.put("ECN_MODIFIER", "用户1");
			wfInfoTable.put("VERSION", "A");
			wfInfoTable.put("MODIFIER", "用户2");
			wfInfoTable.put("MODIFYTIME", "2010-05-18");

			wfInfoTable.put("JDBJ1", "M");
			wfInfoTable.put("JDBJ2", "C");
			wfInfoTable.put("JDBJ3", "S");
			wfInfoTable.put("JDBJ4", "D");
			wfInfoTable.put("BIAOZHI", "M");

			Hashtable data = new Hashtable();
			data.put(refStr, wfInfoTable);

			// writeReviewtoPDF(data);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			String docNumber = args[0];// OR:ext.ases.envelope.ProcessEnvelope:189739
			if (docNumber == null) {
				docNumber = "";
			}
			RemoteMethodServer rms = RemoteMethodServer.getDefault();
			rms.setUserName("wcadmin");
			rms.setPassword("wcadmin");
			FilePrintUtil2.deAllCompressZip(docNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Hashtable table = new Hashtable();
		// table.put("SHENHEZHESHIJIAN",
		// "wcadmin//2011-02-07;user2/总体组/2011-02-07;usera//2011-02-07");
		// processForMultiReview(table);
		// System.out.println(table.get("SHENHEZHESHIJIAN"));
		// System.out.println(table.get("SHENHEMARK1"));
		// System.out.println(table.get("SHENHE1"));
		// System.out.println(table.get("SHENHEMARK2"));
		// System.out.println(table.get("SHENHE2"));
	}

	public static void rePrintPdf(WTDocument doc) throws Exception {
		boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
		WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(doc);
		Iterator it = coll.iterator();
		if (it.hasNext()) {
			WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
			QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(ecn, null, null);
			while (qrProcs.hasMoreElements()) {
				WfProcess process = (WfProcess) qrProcs.nextElement();
				if (process.getState().equals(WfState.OPEN_RUNNING) || process.getState().equals(WfState.CLOSED_COMPLETED_EXECUTED)) {
					FilePrintUtil.writeReviewtoPDF2(ecn, process);
					break;
				}
			}
		} else {
			QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(doc, null, null);
			while (qrProcs.hasMoreElements()) {
				WfProcess process = (WfProcess) qrProcs.nextElement();
				if (process.getState().equals(WfState.OPEN_RUNNING) || process.getState().equals(WfState.CLOSED_COMPLETED_EXECUTED)) {
					FilePrintUtil.writeReviewtoPDF2(doc, process);
					break;
				}
			}
		}

		SessionServerHelper.manager.setAccessEnforced(accessFlag);
	}

	public static Map<String, String> getTechnologyDocType() {
		Map<String, String> technologyMap = new HashMap<String, String>();
		try {
			String technologyEname = getStrFromProperties("TECHNOLOGY_ENAME", technologyProperties);
			String technologyCname = getStrFromProperties("TECHNOLOGY_CNAME", technologyProperties);
			String[] ename = technologyEname.split(",");
			String[] cname = technologyCname.split(",");
			for (int i = 0; i < ename.length; i++) {
				technologyMap.put(cname[i], ename[i]);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MissingResourceException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return technologyMap;
	}
}

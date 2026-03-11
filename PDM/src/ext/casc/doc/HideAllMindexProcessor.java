package ext.casc.doc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.StringDefinition;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import com.glaway.mpm.constants.XMLConstants;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.sop.SopUitl;
import com.glaway.mpm.util.ApacheZipUtil;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.SWXMLUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.sop.util.QueryUtil;
import ext.casc.util.DBConn;
import ext.casc.util.IBAUtility;
import ext.casc.util.SoftTypeUtil;

/**
 * @describe used to hide all mindex
 * @author chenjianhui
 * @since 2020/05/19
 *
 */
public class HideAllMindexProcessor extends DefaultObjectFormProcessor {
	private static int index1[] = { 0 };
	private static VaLogger logger = VaLogger.getLogger(SopUitl.class.getName());


	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
		FormResult formresult = super.doOperation(commandBean, objectBeans);
		FeedbackMessage message = new FeedbackMessage();
		Object actionObj = commandBean.getActionOid().getRefObject();
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		System.out.println("actionObj=" + actionObj);
		PDMLinkProduct product = null;
		String tecSub = "";
		String unMindex = "";
		try {
			long ibaOid = getMindexIBAOid();
			if (actionObj instanceof PDMLinkProduct) {
				product = (PDMLinkProduct) actionObj;
				IBAUtility utility = new IBAUtility(product);
				unMindex = utility.getIBAValue("UNMINDEX");
			}
			if("".equals(unMindex) || "null".equals(unMindex)){
				message.addMessage(product.getName()+"不存在非密型号代号，请先设置非密型号代号");
			}else{
				ArrayList<TypeIdentifier> list = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
				for (TypeIdentifier ti : list) {
					SoftTypeUtil.getTypeIdentifierDefinition(ti).getDisplay();
					String type = ti.toString().substring(7);
					TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(type);
					long typeId = 0;
					if (tdr != null) {
						typeId = tdr.getKey().getBranchId();
					}
					QuerySpec qs = new QuerySpec(WTDocument.class);
					qs.appendWhere(new SearchCondition(WTDocument.class, "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId), new int[] { 0 });
					qs.appendAnd();
					qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] { 0 });
					qs = new LatestConfigSpec().appendSearchCriteria(qs);
//					qs.appendAnd();
//					qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.EQUAL, "APPROVED", true));
					qs.appendAnd();
					qs.appendWhere(new SearchCondition(WTDocument.class, "containerReference.key.id", SearchCondition.EQUAL, product.getPersistInfo().getObjectIdentifier().getId()), index1);
					QueryResult qr = PersistenceHelper.manager.find(qs);
					while (qr.hasMoreElements()) {
						WTDocument document = (WTDocument) qr.nextElement();
						long docOid = PersistenceHelper.getObjectIdentifier(document).getId();
						boolean checkedOut = WorkInProgressHelper.isCheckedOut((Workable) document);
						if (!checkedOut) {
							System.out.println(document.getName());
							writeXML(document, tecSub, unMindex);
							changeMindexValue(docOid,ibaOid,unMindex);
						}
					}
				}
				message.addMessage("操作成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			formresult.addFeedbackMessage(message);
			formresult.setNextAction(FormResultAction.NONE);
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		return formresult;
	}

	public static void writeXML(WTDocument document,String tecSub,String unMindex) throws Exception {
		FileOutputStream fileOutputStream = null;
		InputStream input = null;
		InputStream inputStream = null;
		InputStream input1 = null;
		FileInputStream fis = null;
		FileInputStream fis1 = null;
		FileInputStream stream = null;
		PdfStamper stamper = null;
		PdfStamper stamper1 = null;
		PdfReader reader = null;
		PdfReader reader1 = null;
		SWXMLUtil xmlUtil = null;
		Transaction trans = new Transaction();
		try {
			trans.start();
			WTProperties pro = WTProperties.getLocalProperties();
			String tec_temp_dir = pro.getProperty("wt.temp");
			ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
			if (data == null) {
				return;
			}
			boolean isHasPrintFile = false;
			String tecNumber = document.getNumber();
			String tempPath = java.util.UUID.randomUUID().toString();
			tecSub = tec_temp_dir + File.separator + tempPath;
			String tecFilePath = tec_temp_dir + File.separator + tempPath + File.separator + tecNumber;
			String pdfPath = tec_temp_dir + File.separator + tempPath + File.separator + document.getNumber() + File.separator + "PDFPreview.pdf";
			String xmlPath = tec_temp_dir + File.separator + tempPath + File.separator + document.getNumber() + File.separator + tecNumber + ".xml";
			File file = new File(tecFilePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
			ZipUtil.unZip(bytes, tecFilePath);
			String printPdfName = WTPartUtil.downloadPrintPdf(document, tecSub);
			if(!"".equals(printPdfName) && !"null".equals(printPdfName)){
				isHasPrintFile = true;
			}
			File pdffile = new File(pdfPath);
			String printPdfPath = tecSub + File.separator + printPdfName;
			File printPdfFile = new File(printPdfPath);

			logger.debug("PDF型号代号转换开始...");
			Image image = Image.getInstance(PropertiesUtil.getLocalCodeBase() + File.separator + "1.png");
			int i = 221;
			int j = 510;
			int e = 400;
			int f = 22;
			image.scaleToFit(e, f);
			image.setAbsolutePosition(i, j);
			BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            float axisX = 242;
			float axisY = 514;
			float fontSize = 12;
			if(pdffile.exists()){
				input = new FileInputStream(pdffile);
				reader = new PdfReader(input);
				stamper = new PdfStamper(reader, new FileOutputStream(pdffile));
				PdfContentByte under = stamper.getOverContent(1);
				under.addImage(image);
				under.beginText();
				under.setFontAndSize(bf, fontSize);
				under.showTextAligned(Element.ALIGN_CENTER, unMindex, axisX, axisY, 0);
				under.endText();
				under.closePathStroke();
			}
			if(isHasPrintFile){
				input1 = new FileInputStream(printPdfFile);
				reader1 = new PdfReader(input1);
				stamper1 = new PdfStamper(reader1, new FileOutputStream(printPdfFile));
				PdfContentByte under1 = stamper1.getOverContent(1);
				under1.addImage(image);
				under1.beginText();
				under1.setFontAndSize(bf, fontSize);
				under1.showTextAligned(Element.ALIGN_CENTER, unMindex, axisX, axisY, 0);
				under1.endText();
				under1.closePathStroke();
			}
			logger.debug("PDF型号代号转换结束...");

			logger.debug("工艺xml型号代号转换开始...");
			File xmlFile = new File(xmlPath);
			if (xmlFile.exists()) {
				String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
				inputStream = new FileInputStream(xmlFile);
				xmlUtil = new SWXMLUtil(inputStream);
				org.jdom.Element rootElement = xmlUtil.getRootElement();
				if (XMLConstants.technics.equals(rootElement.getName())) {
					if (docType.contains("casc.sast.149.reportTechnics")) {
						for (org.jdom.Element rootAttrElement : (List<org.jdom.Element>) rootElement.getChildren(XMLConstants.XWReportTechnicsInfo)) {
							if (rootAttrElement.getAttributeValue("technicsNumber") != null && !"".equals(rootAttrElement.getAttributeValue("technicsNumber"))) {
								rootAttrElement.setAttribute("MINDEX", unMindex);
							}
						}
					} else {
						for (org.jdom.Element rootAttrElement : (List<org.jdom.Element>) rootElement.getChildren(XMLConstants.QMFawTechnicsInfo)) {
							if (rootAttrElement.getAttributeValue("technicsNumber") != null && !"".equals(rootAttrElement.getAttributeValue("technicsNumber"))) {
								rootAttrElement.setAttribute("MINDEX", unMindex);
								for (org.jdom.Element ibaAttrElement : (List<org.jdom.Element>) rootAttrElement.getChildren(XMLConstants.IBAAttibutes)) {
									for (org.jdom.Element mindexibaAttrElement : (List<org.jdom.Element>) ibaAttrElement.getChildren(XMLConstants.attribute)) {
										if("产品型号代号".equals(mindexibaAttrElement.getAttributeValue("key"))){
											mindexibaAttrElement.setAttribute("value", unMindex);
											break;
										}
									}
								}
							}
						}
					}
				}
			}
			logger.debug("工艺xml型号代号转换结束...");
			if(stamper!=null){
				stamper.close();
			}
			if(reader!=null){
				reader.close();
			}
			if(input!=null){
				input.close();
			}
			if(stamper1!=null){
				stamper1.close();
			}
			if(reader1!=null){
				reader1.close();
			}
			if(input1!=null){
				input1.close();
			}
			trans.commit();
			trans = null;

			// 替换工艺xml，打包工艺文件夹，上传工艺压缩包，上传pdf附件
			logger.debug("开始上载工艺文件...");
			long startTime1 = System.currentTimeMillis();
			if(pdffile.exists()){
				fis = new FileInputStream(pdffile);
				byte[] pdfbytes = IOUtils.toByteArray(fis);
				WTDocumentUtil.uploadAttachForDocument(document, "PDFPreview.pdf", pdfbytes);
				fis.close();
			}
			if(isHasPrintFile){
				fis1 = new FileInputStream(printPdfFile);
				byte[] printpdfbytes = IOUtils.toByteArray(fis1);
				WTDocumentUtil.uploadPrintAttachForDocument(document, printPdfName, printpdfbytes);
				fis1.close();
			}
			if(xmlFile.exists()){
				fileOutputStream = new FileOutputStream(new File(xmlPath), false);
				Format format = Format.getPrettyFormat();
				format.setEncoding("GBK");
				XMLOutputter xmlOutput = new XMLOutputter(format);
				xmlOutput.output(xmlUtil.getDocument(), fileOutputStream);
				ApacheZipUtil.compress(tecFilePath, tecFilePath + ".zip");
			//	byte[] techByte = FileUtil.readFilePathToByte(tecFilePath + ".zip");
				//ProcessEditorToWCIntfRMI.uploadPrimaryOfDocument(techByte, tecNumber, version);
				stream = new FileInputStream(tecFilePath + ".zip");
				data = ContentServerHelper.service.updateContent((ContentHolder) document, data, stream); // 更新内容

			}
			long endTime1 = System.currentTimeMillis();
			logger.debug("上载工艺文件耗时：" + (endTime1 - startTime1) + " ms");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (trans != null) {
				trans.rollback();
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			deleteFiles(tecSub);
		}
	}

	public static boolean deleteFiles(String inputPath) {
		try {
			File f = new File(inputPath);
			if (f.isDirectory()) {
				File[] flist = f.listFiles();
				for (int i = 0; i < flist.length; i++) {
					File tmpfile = (File) flist[i];
					deleteFiles(tmpfile.getAbsolutePath());
				}
				f.delete();
			} else
				f.delete();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	public static long getMindexIBAOid() throws WTException {
		long ibaOid = 0;
		QuerySpec qs = new QuerySpec(StringDefinition.class);
		SearchCondition sc = new SearchCondition(StringDefinition.class, StringDefinition.NAME, SearchCondition.EQUAL, "MINDEX", false);
		qs.appendSearchCondition(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if(qr.hasMoreElements()) {
			StringDefinition definition = (StringDefinition) qr.nextElement();
			ibaOid = PersistenceHelper.getObjectIdentifier(definition).getId();
		}
		return ibaOid;
	}

	public static void changeMindexValue(long docOid,long ibaOid,String after){
		DBConn conn = null;
		try {
			conn = new DBConn();
			String sql = "";
			sql = "update StringValue set value='"+after+"',value2='"+after+"' where ida3a4="+docOid+" and ida3a6="+ibaOid;
			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

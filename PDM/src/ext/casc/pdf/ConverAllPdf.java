/**
 *
 */
package ext.casc.pdf;

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
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import com.glaway.mpm.constants.XMLConstants;
import com.glaway.mpm.util.ApacheZipUtil;
import com.glaway.mpm.util.IBAHelper;
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
import com.ptc.core.meta.common.TypeIdentifier;

import ext.casc.doc.HideAllMindexProcessor;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.util.SoftTypeUtil;

/**
 * @author cfire
 *
 */
public class ConverAllPdf {
	public static void process (String productId){
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
        try {
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

    		    qs.appendWhere(new SearchCondition(WTDocument.class,
    		    "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId),
    		     new int[]{0});
    		    qs.appendAnd();
    		    qs.appendWhere(new SearchCondition(WTDocument.class,
    	    		    "containerReference.key.id", SearchCondition.EQUAL,Long.parseLong(productId) ),
    	    		     new int[]{0});
    		    qs.appendAnd();
    		    qs.appendWhere(new SearchCondition(WTDocument.class,WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] { 0 });
    		    QueryResult qr = PersistenceHelper.manager.find(qs);
    		    while(qr.hasMoreElements()){
    		    	WTDocument document = (WTDocument)qr.nextElement();
    		    	String batch = IBAHelper.getIBAValue(document, "BATCH");
    		    	boolean  checkedOut = WorkInProgressHelper.isCheckedOut((Workable) document);
    		    	if(checkedOut){
    		    		document = (WTDocument)WorkInProgressHelper.service.undoCheckout(document);
    		    	}
    		    	if("D阶段".equals(batch)){
    		    		IBAHelper.setIBAStringValue(document, "BATCH", "小批量试生产");
    		    		replacePdf(document);
    		    	}

    		    }
    		}

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);
        }

	}
	private static String tec_temp_dir;
	static{
		WTProperties pro;
		try {
			pro = WTProperties.getLocalProperties();
			tec_temp_dir = pro.getProperty("wt.temp");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @param document
	 * @throws WTException
	 */
	public  static void replacePdf(WTDocument document) throws WTException {
			String tecSub = "";
			FileOutputStream fileOutputStream = null;
			InputStream inputStream = null;
			FileInputStream fis = null;
			FileInputStream fis1 = null;
			SWXMLUtil xmlUtil = null;
			Transaction trans = new Transaction();
			try {
				trans.start();

				System.out.println("开始转换："+document.getNumber());
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
				String verison = document.getVersionIdentifier().getValue()+"."+document.getIterationInfo().getIdentifier().getValue();
				if(pdffile.exists()){
			        new PdfConversion().manipulatePdf(pdfPath, pdfPath+"_tmp",verison);
				}
				if(isHasPrintFile){
			        new PdfConversion().manipulatePdf(printPdfPath, printPdfPath+"_tmp",verison);
				}

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
									rootAttrElement.setAttribute("PCNO", "小批量试生产");
								}
							}
						} else {
							for (org.jdom.Element rootAttrElement : (List<org.jdom.Element>) rootElement.getChildren(XMLConstants.QMFawTechnicsInfo)) {
								if (rootAttrElement.getAttributeValue("technicsNumber") != null && !"".equals(rootAttrElement.getAttributeValue("technicsNumber"))) {
									rootAttrElement.setAttribute("PCNO", "小批量试生产");
								}
							}
						}
					}
				}


				// 替换工艺xml，打包工艺文件夹，上传工艺压缩包，上传pdf附件
				System.out.println("开始上载工艺文件...");
				if(pdffile.exists()){
					fis = new FileInputStream(pdffile+"_tmp");
					byte[] pdfbytes = IOUtils.toByteArray(fis);
					WTDocumentUtil.uploadAttachForDocument(document, "PDFPreview.pdf", pdfbytes);
					fis.close();
					fis= null;
				}
				if(isHasPrintFile){
					fis1 = new FileInputStream(printPdfFile+"_tmp");
					byte[] printpdfbytes = IOUtils.toByteArray(fis1);
					WTDocumentUtil.uploadPrintAttachForDocument(document, printPdfName, printpdfbytes);
					fis1.close();
					fis1 = null;
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
					data = ContentServerHelper.service.updateContent((ContentHolder) document, data, new FileInputStream(tecFilePath + ".zip")); // 更新内容
				}
				trans.commit();
				trans = null;
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
				if (fis1 != null) {
					try {
						fis1.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				//deleteFiles(tecSub);
			}
	}
}

package ext.casc.doc;

import com.glaway.mpm.pdf.LcmPdfPrinter;
import com.glaway.mpm.pdf.PDFBuilder;
import ext.casc.fileprint.FilePrintUtil;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DocumentUtil {
	
	public static Map getAttachmentsFromDocument(WTDocument holder, ContentRoleType role) {
		if(holder == null)
			return null;
		HashMap fileNameToURL = new HashMap();
		try {					
			QueryResult qr = ContentHelper.service.getContentsByRole(holder, role);
			while(qr.hasMoreElements()){
				ApplicationData ap = (ApplicationData)qr.nextElement();
				String filename = ap.getFileName();
				URL url = ContentHelper.getDownloadURL(holder, ap);
				fileNameToURL.put(filename, url.toString());
			}			
		} catch (Exception e) {
//			CSCDebug.outDebugInfo(e.getLocalizedMessage());
		}
		
		return fileNameToURL;
	}
	
	public static Map getAttachmentsFromDocument(EPMDocument holder, ContentRoleType role) {
		if(holder == null)
			return null;
		HashMap fileNameToURL = new HashMap();
		try {					
			QueryResult qr = ContentHelper.service.getContentsByRole(holder, role);
			while(qr.hasMoreElements()){
				ApplicationData ap = (ApplicationData)qr.nextElement();
				String filename = ap.getFileName();
				URL url = ContentHelper.getDownloadURL(holder, ap);
				fileNameToURL.put(filename, url.toString());
			}			
		} catch (Exception e) {
//			CSCDebug.outDebugInfo(e.getLocalizedMessage());
		}
		
		return fileNameToURL;
	}

	/**
	 * 方法功能: 创建PDF封面

	 * @param wtDocument
	 * @return void
	 * @author LB
	 * @date 2020/9/18
	 */
	public static File createPdfCover(WTDocument wtDocument) {
		File file = null;
		try {
			LcmPdfPrinter printer = new LcmPdfPrinter();
			String className = "ext.casc.pdf.TechnicDocCoverPDFBuilder";
			Class<?> cls = Class.forName(className);
			String formName = "TechnicDocCoverPDF";
			Constructor constructor = cls.getConstructor(String.class, WTDocument.class);
			PDFBuilder builder = (PDFBuilder) constructor.newInstance(formName,wtDocument);
			String techFloder = builder.getTechFloder();
			String filePath = techFloder + File.separator + "PDFCoverPreview.pdf";
			builder.buildPDF(printer,null,null);
			printer.print(filePath);
			file = new File(filePath);
			if(file.exists()){
				FilePrintUtil.saveFiletoAttachment(wtDocument,filePath,true,"PDFCoverPreview.pdf");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

}

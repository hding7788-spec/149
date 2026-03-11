package ext.casc.doc;

import com.glaway.mpm.constants.XMLConstants;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.util.*;
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
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.integrate.util.ZipUtil;
import org.apache.commons.io.IOUtils;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.pom.Transaction;
import wt.session.SessionServerHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTProperties;

import java.io.*;
import java.util.List;

/**
 * @describe used to change document pdf version
 * @author chenjianhui
 * @since 2021/2/2
 *
 */
public class RePdfVersionProcessor extends DefaultObjectFormProcessor {
    private static VaLogger logger = VaLogger.getLogger(RePdfVersionProcessor.class.getName());

    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
        FormResult formresult = super.doOperation(commandBean, objectBeans);
        Object actionObj = commandBean.getActionOid().getRefObject();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        System.out.println("actionObj" + actionObj);
        try {
            if (actionObj instanceof WTDocument) {
                WTDocument document = (WTDocument) actionObj;
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
                String tecSub = "";
                Transaction trans = new Transaction();
                try {
                    trans.start();
                    WTProperties pro = WTProperties.getLocalProperties();
                    String tec_temp_dir = pro.getProperty("wt.temp");
                    ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
                    String version = document.getIterationDisplayIdentifier().toString();
                    if (data != null) {
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

                        logger.debug("PDF版本转换开始...");
                        Image image = Image.getInstance(PropertiesUtil.getLocalCodeBase() + File.separator + "1.png");
                        int i = 38;
                        int j = 519;
                        int e = 400;
                        int f = 19;
                        image.scaleToFit(e, f);
                        image.setAbsolutePosition(i, j);
                        BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                        float axisX = 60;
                        float axisY = 525;
                        float fontSize = 12;
                        if(pdffile.exists()){
                            input = new FileInputStream(pdffile);
                            reader = new PdfReader(input);
                            stamper = new PdfStamper(reader, new FileOutputStream(pdffile));
                            int pages = reader.getNumberOfPages();
                            for (int i1 = 1; i1 <= pages; i1++) {
                                PdfContentByte under = stamper.getOverContent(i1);
                                under.addImage(image);
                                under.beginText();
                                under.setFontAndSize(bf, fontSize);
                                under.showTextAligned(Element.ALIGN_CENTER, version, axisX, axisY, 0);
                                under.endText();
                                under.closePathStroke();
                            }

                        }
                        if(isHasPrintFile){
                            input1 = new FileInputStream(printPdfFile);
                            reader1 = new PdfReader(input1);
                            stamper1 = new PdfStamper(reader1, new FileOutputStream(printPdfFile));
                            int pages = reader1.getNumberOfPages();
                            for (int i1 = 1; i1 <= pages; i1++) {
                                PdfContentByte under1 = stamper1.getOverContent(i1);
                                under1.addImage(image);
                                under1.beginText();
                                under1.setFontAndSize(bf, fontSize);
                                under1.showTextAligned(Element.ALIGN_CENTER, version, axisX, axisY, 0);
                                under1.endText();
                                under1.closePathStroke();
                            }
                        }
                        logger.debug("PDF版本转换结束...");

                        logger.debug("工艺xml版本转换开始...");
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
                                            rootAttrElement.setAttribute("version", version);
                                        }
                                    }
                                } else {
                                    for (org.jdom.Element rootAttrElement : (List<org.jdom.Element>) rootElement.getChildren(XMLConstants.QMFawTechnicsInfo)) {
                                        if (rootAttrElement.getAttributeValue("technicsNumber") != null && !"".equals(rootAttrElement.getAttributeValue("technicsNumber"))) {
                                            rootAttrElement.setAttribute("version", version);
                                        }
                                    }
                                }
                            }
                        }
                        logger.debug("工艺xml版本转换结束...");
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
                    }
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
                    if(!"".equals(tecSub)){
                        deleteFiles(tecSub);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FeedbackMessage message = new FeedbackMessage();
            message.addMessage("操作成功");
            formresult.addFeedbackMessage(message);
            formresult.setNextAction(FormResultAction.REFRESH_CURRENT_PAGE);
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formresult;
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
}

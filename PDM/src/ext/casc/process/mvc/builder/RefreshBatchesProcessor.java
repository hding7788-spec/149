package ext.casc.process.mvc.builder;

import com.glaway.mpm.constants.XMLConstants;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.mpmresource.processors.CustomerObjectFormProcessor;
import com.glaway.mpm.util.*;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.product.model.Batch;
import ext.casc.util.DBUtil;
import ext.casc.util.IBAHelper;
import org.apache.commons.io.IOUtils;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.fc.ReferenceFactory;
import wt.inf.container.WTContained;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.Transaction;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

public class RefreshBatchesProcessor extends CustomerObjectFormProcessor {
    private static VaLogger logger = VaLogger.getLogger(RefreshBatchesProcessor.class.getName());

    @Override
    public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1) throws WTException {
    	String user = "";
    	try {
            user = wt.session.SessionHelper.manager.getPrincipal().getName();
            wt.session.SessionHelper.manager.setAdministrator();
        } catch (Exception e) {
        }
        HttpServletRequest request = arg0.getRequest();
        ArrayList<NmOid> nmOids = arg0.getNmOidSelected();
        Map map = request.getParameterMap();
        ArrayList<String> batchList = new ArrayList<String>();
        String batch = "";
        for (int i = 1; i < 11; i++) {
            String[] str = (String[]) map.get("batch" + i);
            String value = str[0];
            if(value!=null && !"".equals(value)){
                int length = value.length();
                if(length<13){
                    batchList.add(value);
                }else if(length>12){
                    batchList.add(value.substring(0,12));
                    batchList.add(value.substring(12,length));
                }
                batch = batch + value + "、";
            }
        }
        batch = batch.substring(0,batch.length()-1);
        for (NmOid nmOid : nmOids) {
            Object object = nmOid.getLatestIterationObject();
            if(object instanceof WTDocument){
                WTDocument doc = (WTDocument) object;
                String state = doc.getState().getState().getDisplay(Locale.CHINA);
                if("已批准".equals(state)){
                    boolean checkedOut = WorkInProgressHelper.isCheckedOut((Workable) doc);
                    if (!checkedOut) {
                        System.out.println(doc.getName());
                        IBAHelper.setIBAStringValue(doc,"BIAOSHI",batch);//修改标识
                        writeXML(doc,batchList,batch);
                    }
                }
            }
        }
        if (!"".equals(user)) {
            try {
                wt.session.SessionHelper.manager.setPrincipal(user);
            } catch (Exception e) {
            }
        }
        return super.doOperation(arg0, arg1);
    }

    public static void writeXML(WTDocument document,ArrayList<String> batchList,String biaoshi) {
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

            logger.debug("PDF批次号批量转换开始...");
            Image image = Image.getInstance(PropertiesUtil.getLocalCodeBase() + File.separator + "pici.png");
            int i = 600;
            int j = 95;
            int e = 400;
            int f = 260;
            image.scaleToFit(e, f);
            image.setAbsolutePosition(i, j);
            BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            float axisX = 606;
            float axisY = 325;
            float fontSize = 16;
            if(pdffile.exists()){
                input = new FileInputStream(pdffile);
                reader = new PdfReader(input);
                stamper = new PdfStamper(reader, new FileOutputStream(pdffile));
                PdfContentByte under = stamper.getOverContent(1);
                under.addImage(image);
                under.beginText();
                under.setFontAndSize(bf, fontSize);
                for (String batch : batchList) {
                    under.showTextAligned(Element.ALIGN_LEFT, batch, axisX, axisY, 0);
                    axisY = axisY - 22;
                }
                under.endText();
                under.closePathStroke();
            }
            axisY = 325;
            if(isHasPrintFile){
                input1 = new FileInputStream(printPdfFile);
                reader1 = new PdfReader(input1);
                stamper1 = new PdfStamper(reader1, new FileOutputStream(printPdfFile));
                PdfContentByte under1 = stamper1.getOverContent(1);
                under1.addImage(image);
                under1.beginText();
                under1.setFontAndSize(bf, fontSize);
                for (String batch : batchList) {
                    under1.showTextAligned(Element.ALIGN_LEFT, batch, axisX, axisY, 0);
                    axisY = axisY - 22;
                }
                under1.endText();
                under1.closePathStroke();
            }
            logger.debug("PDF型号代号转换结束...");

            logger.debug("工艺xml批次号转换开始...");
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
                                rootAttrElement.setAttribute("BIAOSHI", biaoshi);
                            }
                        }
                    } else {
                        for (org.jdom.Element rootAttrElement : (List<org.jdom.Element>) rootElement.getChildren(XMLConstants.QMFawTechnicsInfo)) {
                            if (rootAttrElement.getAttributeValue("technicsNumber") != null && !"".equals(rootAttrElement.getAttributeValue("technicsNumber"))) {
                                rootAttrElement.setAttribute("BIAOSHI", biaoshi);
                            }
                        }
                    }
                }
            }
            logger.debug("工艺xml批次号转换结束...");
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

            // 替换工艺xml，打包工艺文件夹，上传工艺压缩包，上传pdf附件
            logger.debug("开始上载工艺文件...");
            long startTime1 = System.currentTimeMillis();
            if(pdffile.exists()){
                fis = new FileInputStream(pdffile);
                byte[] pdfbytes =  IOUtils.toByteArray(fis);
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
                stream = new FileInputStream(tecFilePath + ".zip");
                data = ContentServerHelper.service.updateContent(document, data, stream); // 更新内容
            }
            long endTime1 = System.currentTimeMillis();
            logger.debug("上载工艺文件耗时：" + (endTime1 - startTime1) + " ms");
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

    public  static String getPartNumberByoid(NmOid oid) {
        String number="";
        String[] str = (oid.toString()).split("~");
        String oid1 = str[0];
        WTPart part = (WTPart) findObject(oid1);
        number=part.getNumber();
        return number;

    }

    public  static ArrayList<String> getProductBatchesByoid(NmOid oid) {
        ArrayList<String> list = new ArrayList<String>();
        String[] str = (oid.toString()).split("~");
        String oid1 = str[0];
        WTPart part = (WTPart) findObject(oid1);
        WTContained container = part.getContainer();
        String productName = "";
        if (container instanceof PDMLinkProduct) {
            productName = ((PDMLinkProduct) container).getName();
        }
        String productOid = String.valueOf(PersistenceHelper
                .getObjectIdentifier(container).getId());
        List<Batch> batches = DBUtil.getBatchesByProduct(productOid, productName);
        if(batches!=null){
            for (Batch batch : batches) {
                list.add(batch.getName());
            }
        }
        return list;
    }

    public  static String getPbomBatchByoid(NmOid oid) {
        String[] str = (oid.toString()).split("~");
        String oid1 = str[0];
        WTPart part = (WTPart) findObject(oid1);
        String batch = "";
        try {
            batch = IBAHelper.getIBAStringValue(part, "BATCH");
        } catch (WTException e) {
            e.printStackTrace();
        }
        return batch;
    }

 // 根据oid得到对象
    public static WTObject findObject(String oid) {
        Object obj = null;
        if (oid == null || oid.equals("")) {
            return null;
        }
        ReferenceFactory factory = new ReferenceFactory();
        try {
            WTReference reference = factory.getReference(oid);
            obj = reference.getObject();
        } catch (WTException e) {
            e.printStackTrace();
        }
        return (WTObject) obj;
    }

}
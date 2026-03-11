package ext.casc.zipfile;

import com.glaway.mpm.print.util.ZipUtil;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.download.DownloadFormProcessor;
import ext.casc.preview.Preview;
import ext.casc.util.ExcelFileGenerator;
import ext.casc.util.IBAHelper;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import wt.change2.WTChangeOrder2;
import wt.content.*;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTPrincipal;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;

import java.beans.PropertyVetoException;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class StandardZipFileService extends StandardManager implements ZipFileService, Serializable {

    /** */
    private static final long serialVersionUID = 7602295112787004354L;

    public static StandardZipFileService newStandardZipFileService() throws WTException {
        StandardZipFileService instance = new StandardZipFileService();
        instance.initialize();
        return instance;
    }

    public String zipFile(List<String> oidList, String relist, Integer printsum, String zipFileName)
            throws WTException, PropertyVetoException,
            IOException {
        return zipFile(oidList, zipFileName);
    }

    public String userGuidesZipFile(String zipFileName) throws IOException {
        return userGuidesZipFileDownload(zipFileName);
    }

    public  String userGuidesZipFileDownload(String zipFileName) throws IOException {
        String tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
        String fileDir = WTProperties.getLocalProperties().getProperty("wt.codebase.location") + File.separator + "ext"
                + File.separator + "casc" + File.separator + "userGuide";
        File zipFile = new File(tempDir + File.separator + zipFileName + ".zip");
        InputStream is = null;
        ZipOutputStream zos = new ZipOutputStream(zipFile);
        zos.setEncoding("GBK");
        byte[] buf = new byte[1024];
        File fileList = new File(fileDir);
        File[] files = fileList.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    is = new FileInputStream(file);
                    if (is != null) {
                        zos.putNextEntry(new ZipEntry(file.getName()));
                        zos.setEncoding("GBK");
                        int len = 0;
                        while ((len = is.read(buf)) >= 0) {
                            zos.write(buf, 0, len);
                        }
                        is.close();
                    }
                }
            }
        }
        zos.close();
        return tempDir + File.separator + zipFileName + ".zip";
    }

    public String downloadPrimaryContent(EPMDocument epmDocument,String number) throws Exception{
        String tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
        FormatContentHolder formatcontentholder = (FormatContentHolder)ContentHelper.service.getContents((FormatContentHolder)epmDocument);
        ContentItem contentitem = ContentHelper.getPrimary(formatcontentholder);
        if(contentitem instanceof ApplicationData) {
            ApplicationData applicationdata = (ApplicationData)contentitem;
            String name = epmDocument.getCADName();
            InputStream is = ContentServerHelper.service.findContentStream(applicationdata);
            File tempFile = new File(tempDir + File.separator + name);
            FileOutputStream fos = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int byteread = 0;
            while((byteread=is.read(buffer))!=-1){
                fos.write(buffer,0,byteread);
            }
            is.close();
            fos.close();
            return tempDir + File.separator + name;
        }

        return "";
    }
    public String downloadPrimaryContent(Preview preview,String number) throws Exception{
    	String tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
        if(number.contains(".")) {
            number = number.substring(0, number.indexOf("."));
        }
        String zipFileName = "";
            FormatContentHolder formatcontentholder = (FormatContentHolder) ContentHelper.service.getContents((FormatContentHolder) preview);
            ContentItem contentitem = ContentHelper.getPrimary(formatcontentholder);
//            List list = ContentHelper.getContentList(formatcontentholder);
//            if(list.size() > 0){
//            	contentitem = (ContentItem) list.get(0);
//            }
            if (contentitem instanceof ApplicationData) {
                ApplicationData applicationdata = (ApplicationData) contentitem;
                String name = preview.getName();
                InputStream is = ContentServerHelper.service.findContentStream(applicationdata);
                zipFileName = tempDir + File.separator + name+"_"+applicationdata.getFileName();
                File tempFile = new File(zipFileName);
                FileOutputStream fos = new FileOutputStream(tempFile);
                byte[] buffer = new byte[1024];
                int byteread = 0;
                while ((byteread = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteread);
                }
                is.close();
                fos.close();
        }
        return zipFileName;
    }

    public String downloadPrimaryContent(WTDocument epmDocument,String number) throws Exception{
        String tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
        FormatContentHolder formatcontentholder = (FormatContentHolder)ContentHelper.service.getContents((FormatContentHolder)epmDocument);
        ContentItem contentitem = ContentHelper.getPrimary(formatcontentholder);
        if(contentitem instanceof ApplicationData) {
            ApplicationData applicationdata = (ApplicationData)contentitem;
            String name = epmDocument.getName();
            InputStream is = ContentServerHelper.service.findContentStream(applicationdata);
            File tempFile = new File(tempDir + File.separator + name);
            FileOutputStream fos = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int byteread = 0;
            while((byteread=is.read(buffer))!=-1){
                fos.write(buffer,0,byteread);
            }
            is.close();
            fos.close();
            return tempDir + File.separator + name;
        }

        return "";
    }

    public synchronized String zipFileNew(List<String> oidList, String zipFileName) {
        FileOutputStream fos = null;
        ReferenceFactory rf = null;
        byte[] bytes = null;
        Object obj;
        try {
            rf = new ReferenceFactory();
            ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
            File filePath = new File(PropertiesUtil.getTempPath() + File.separator + zipFileName);
            if(!filePath.exists()){
                filePath.mkdir();
            }
            ArrayList<String> datas;
            IBAHelper iba;
            for(String oid : oidList){
                obj = rf.getReference(oid).getObject();
                //构建excel数据
//                genDownloadDataExcelData(dataList, obj);

                if(obj instanceof WTDocument){
                    WTDocument document = (WTDocument) obj;
                    boolean isDwg = false;
                    QueryResult queryResult = ContentHelper.service.getContentsByRole(document, ContentRoleType.PRIMARY);
                    if(queryResult.hasMoreElements()){
                        ApplicationData ap = (ApplicationData)queryResult.nextElement();
                        String filename = ap.getFileName();
                        if(filename.endsWith("dwg") || filename.endsWith("DWG")){
                            isDwg = true;
                        }
                    }

                    QueryResult qr = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
                    while(qr.hasMoreElements()){
                        ApplicationData ap = (ApplicationData)qr.nextElement();
                        String filename = ap.getFileName();
                        if(isDwg){
                            if((filename.startsWith("Print_") || filename.startsWith("SIGN")) && filename.endsWith("pdf")){
                                bytes = WTDocumentUtil.applicationDataToByte(ap);
                                fos = new FileOutputStream(PropertiesUtil.getTempPath() + File.separator + zipFileName + File.separator + filename);
                                fos.write(bytes);
                                fos.flush();
                                datas = new ArrayList<String>();
                                iba = new IBAHelper();
                                datas.add(document.getNumber());
                                datas.add(tranfString(iba.getIBAStringValue(document, "CINDEX")));// 图号
                                datas.add(document.getName());
                                datas.add(document.getVersionInfo().getIdentifier().getValue() + "." + document.getIterationInfo().getIdentifier().getValue());
                                datas.add(tranfString(iba.getIBAStringValue(document, "MINDEX")));
                                datas.add(tranfString(iba.getIBAStringValue(document, "DESIGNER")));
                                datas.add(tranfString(iba.getIBAStringValue(document, "COMPANY")));// 设计单位
                                datas.add(filename);
                                dataList.add(datas);
                            }
                        }else{
                            bytes = WTDocumentUtil.applicationDataToByte(ap);
                            fos = new FileOutputStream(PropertiesUtil.getTempPath() + File.separator + zipFileName + File.separator + filename);
                            fos.write(bytes);
                            fos.flush();
                            datas = new ArrayList<String>();
                            iba = new IBAHelper();
                            datas.add(document.getNumber());
                            datas.add(tranfString(iba.getIBAStringValue(document, "CINDEX")));// 图号
                            datas.add(document.getName());
                            datas.add(document.getVersionInfo().getIdentifier().getValue() + "." + document.getIterationInfo().getIdentifier().getValue());
                            datas.add(tranfString(iba.getIBAStringValue(document, "MINDEX")));
                            datas.add(tranfString(iba.getIBAStringValue(document, "DESIGNER")));
                            datas.add(tranfString(iba.getIBAStringValue(document, "COMPANY")));// 设计单位
                            datas.add(filename);
                            dataList.add(datas);
                        }
                    }
                }else if(obj instanceof EPMDocument){
                    EPMDocument epmDocument = (EPMDocument) obj;
                    QueryResult qr = ContentHelper.service.getContentsByRole(epmDocument, ContentRoleType.SECONDARY);
                    while(qr.hasMoreElements()){
                        ApplicationData ap = (ApplicationData)qr.nextElement();
                        String filename = ap.getFileName();
                        bytes = WTDocumentUtil.applicationDataToByte(ap);
                        if((filename.startsWith("Print_") || filename.startsWith("SIGN")) && filename.endsWith("pdf")){
                            fos = new FileOutputStream(PropertiesUtil.getTempPath() + File.separator + zipFileName + File.separator + filename);
                            fos.write(bytes);
                            fos.flush();
                            datas = new ArrayList<String>();
                            iba = new IBAHelper();
                            datas.add(epmDocument.getNumber());
                            datas.add(tranfString(iba.getIBAStringValue(epmDocument, "CINDEX")));// 图号
                            datas.add(epmDocument.getName());
                            datas.add(epmDocument.getVersionInfo().getIdentifier().getValue() + "."
                                    + epmDocument.getIterationInfo().getIdentifier().getValue());
                            datas.add(tranfString(iba.getIBAStringValue(epmDocument, "MINDEX")));
                            datas.add(tranfString(iba.getIBAStringValue(epmDocument, "DESIGNER")));
                            datas.add(tranfString(iba.getIBAStringValue(epmDocument, "COMPANY")));// 设计单位
                            datas.add(filename);
                            dataList.add(datas);
                        }
                    }
                }else if(obj instanceof ProcessEnvelope){
                    ProcessEnvelope envelope = (ProcessEnvelope) obj;
                    QueryResult qr = ContentHelper.service.getContentsByRole(envelope, ContentRoleType.SECONDARY);
                    while(qr.hasMoreElements()){
                        ApplicationData ap = (ApplicationData)qr.nextElement();
                        String filename = ap.getFileName();
                        bytes = WTDocumentUtil.applicationDataToByte(ap);
                        if((filename.startsWith("Print_") || filename.startsWith("SIGN")) && filename.endsWith("pdf")){
                            fos = new FileOutputStream(PropertiesUtil.getTempPath() + File.separator + zipFileName + File.separator + filename);
                            fos.write(bytes);
                            fos.flush();

                            datas = new ArrayList<String>();
                            iba = new IBAHelper();
                            datas.add(envelope.getNumber());
                            datas.add(tranfString(iba.getIBAStringValue(envelope, "CINDEX")));// 图号
                            datas.add(envelope.getName());
                            datas.add("");
                            datas.add(tranfString(iba.getIBAStringValue(envelope, "MINDEX")));
                            datas.add(tranfString(iba.getIBAStringValue(envelope, "DESIGNER")));
                            datas.add(tranfString(iba.getIBAStringValue(envelope, "COMPANY")));// 设计单位
                            datas.add(filename);
                            dataList.add(datas);
                        }
                    }
                }else if(obj instanceof ChangePackaged){
                    ChangePackaged changePackaged = (ChangePackaged) obj;
                    QueryResult qr = ContentHelper.service.getContentsByRole(changePackaged, ContentRoleType.SECONDARY);
                    while(qr.hasMoreElements()){
                        ApplicationData ap = (ApplicationData)qr.nextElement();
                        String filename = ap.getFileName();
                        bytes = WTDocumentUtil.applicationDataToByte(ap);
                        if((filename.startsWith("Print_") || filename.startsWith("SIGN")) && (filename.endsWith("pdf") || filename.endsWith("doc"))){
                            fos = new FileOutputStream(PropertiesUtil.getTempPath() + File.separator + zipFileName + File.separator + filename);
                            fos.write(bytes);
                            fos.flush();

                            datas = new ArrayList<String>();
                            iba = new IBAHelper();
                            datas.add(changePackaged.getNumber());
                            datas.add(tranfString(iba.getIBAStringValue(changePackaged, "CINDEX")));// 图号
                            datas.add(changePackaged.getName());
                            datas.add("");
                            datas.add(tranfString(iba.getIBAStringValue(changePackaged, "MINDEX")));
                            datas.add(tranfString(iba.getIBAStringValue(changePackaged, "DESIGNER")));
                            datas.add(tranfString(iba.getIBAStringValue(changePackaged, "COMPANY")));// 设计单位
                            datas.add(filename);
                            dataList.add(datas);
                        }
                    }
                }else if(obj instanceof WTChangeOrder2){
                    WTChangeOrder2 changeOrder2 = (WTChangeOrder2) obj;
                    QueryResult qr = ContentHelper.service.getContentsByRole(changeOrder2, ContentRoleType.SECONDARY);
                    while(qr.hasMoreElements()){
                        ApplicationData ap = (ApplicationData)qr.nextElement();
                        String filename = ap.getFileName();
                        bytes = WTDocumentUtil.applicationDataToByte(ap);
                        if((filename.startsWith("Print_") || filename.startsWith("SIGN")) && filename.endsWith("pdf")){
                            fos = new FileOutputStream(PropertiesUtil.getTempPath() + File.separator + zipFileName + File.separator + filename);
                            fos.write(bytes);
                            fos.flush();

                            datas = new ArrayList<String>();
                            iba = new IBAHelper();
                            datas.add(changeOrder2.getNumber());
                            datas.add(tranfString(iba.getIBAStringValue(changeOrder2, "CINDEX")));// 图号
                            datas.add(changeOrder2.getName());
                            datas.add(changeOrder2.getVersionInfo().getIdentifier().getValue() + "."
                                    + changeOrder2.getIterationInfo().getIdentifier().getValue());
                            datas.add(tranfString(iba.getIBAStringValue(changeOrder2, "MINDEX")));
                            datas.add(tranfString(iba.getIBAStringValue(changeOrder2, "DESIGNER")));
                            datas.add(tranfString(iba.getIBAStringValue(changeOrder2, "COMPANY")));// 设计单位
                            datas.add(filename);
                            dataList.add(datas);
                        }
                    }
                } else if(obj instanceof Preview) {
                    Preview preview = (Preview) obj;
                    QueryResult qr = ContentHelper.service.getContentsByRole(preview, ContentRoleType.PRIMARY);
                    if(qr.hasMoreElements()){
                        ApplicationData ap = (ApplicationData)qr.nextElement();
                        String filename = ap.getFileName();
                        bytes = WTDocumentUtil.applicationDataToByte(ap);
                        fos = new FileOutputStream(PropertiesUtil.getTempPath() + File.separator + zipFileName + File.separator + filename);
                        fos.write(bytes);
                        fos.flush();

                        return PropertiesUtil.getTempPath() + File.separator + zipFileName + File.separator + filename;
                    }
                }
            }

            ArrayList<String> titleList = new ArrayList<String>();
            titleList.add("编号");
            titleList.add("图号");// CINDEX
            titleList.add("名称");
            titleList.add("版本");
            titleList.add("所属型号");// MINDEX
            titleList.add("设计者");// DESIGNER
            titleList.add("设计单位");// COMPANY
            titleList.add("文件名");// COMPANY
            ExcelFileGenerator generator = new ExcelFileGenerator(titleList, dataList);
            File excelFile = new File(PropertiesUtil.getTempPath() + File.separator + zipFileName + File.separator + "149厂打印下载数据包清单.xls");
            if (!excelFile.exists()) {
                excelFile.createNewFile();
            }
            generator.expordExcel(new FileOutputStream(excelFile));

            String zipPath = PropertiesUtil.getTempPath() + File.separator + zipFileName;
            String zipFile = PropertiesUtil.getTempPath() + File.separator + zipFileName + ".zip";
            ZipUtil.compress(zipPath, zipFile);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fos != null){
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return PropertiesUtil.getTempPath() + File.separator + zipFileName + ".zip";
    }

    public synchronized String zipFile(List<String> oidList, String zipFileName) throws WTException,
            PropertyVetoException,
            IOException {
        WTPrincipal currentuser = null;
        String tempPath = null;
        ZipOutputStream zos = null;
        InputStream is = null;
        File zipFile = null;
        Object obj = null;
        ApplicationData ad = null;
        WTDocument wtdoc = null;
        EPMDocument epmdoc = null;
        String containername = null, adName = null;
        Iterator itOid = null;
        ReferenceFactory rf = new ReferenceFactory();
        try {
            WTPrincipal admin = SessionHelper.manager.getAdministrator();
            currentuser = SessionContext.setEffectivePrincipal(admin);
            tempPath = WTProperties.getLocalProperties().getProperty("wt.temp");
            zipFile = new File(tempPath + File.separator + zipFileName + ".zip");
            zos = new ZipOutputStream(zipFile);
            zos.setEncoding("GBK");
            byte[] buf = new byte[1024];
            itOid = oidList.iterator();

            ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
            ArrayList<String> titleList = new ArrayList<String>();
            titleList.add("编号");
            titleList.add("图号");// CINDEX
            titleList.add("名称");
            titleList.add("版本");
            titleList.add("所属型号");// MINDEX
            titleList.add("设计者");// DESIGNER
            titleList.add("设计单位");// COMPANY

            while (itOid.hasNext()) {
                obj = rf.getReference(itOid.next().toString()).getObject();

                genDownloadDataExcelData(dataList, obj);

                ArrayList<String> datas = new ArrayList<String>();
                if (obj instanceof WTDocument) {
                    ContentHolder holder = ContentHelper.service.getContents((ContentHolder) obj);
                    wtdoc = (WTDocument) holder;
                    containername = wtdoc.getNumber() + "_" + wtdoc.getName() + File.separator;
                    QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
                    Vector vec = new Vector();
                    while (qr.hasMoreElements()) {
                        Object objQr = qr.nextElement();
                        if (objQr instanceof ApplicationData) {
                            ad = (ApplicationData) objQr;
                            adName = ad.getFileName();
                            if (isElectronicFile(adName)) {
                                // 对于DWG图样，如果同时有DWG打印文件和PDF，则取DWG文件
                                if (adName.toUpperCase().endsWith(".DWG")) {
                                    vec.clear();
                                    vec.add(ad);
                                    break;
                                } else vec.add(ad);
                            }
                        }
                    }

                    for (int j = 0; j < vec.size(); j++) {
                        ad = (ApplicationData) vec.get(j);
                        is = ContentServerHelper.service.findContentStream(ad);
                        if (is != null) {
                            zos.putNextEntry(new ZipEntry(containername + ad.getFileName()));
                            zos.setEncoding("GBK");
                            int len = 0;
                            while ((len = is.read(buf)) >= 0)
                                zos.write(buf, 0, len);
                            is.close();
                        }
                    }

                } else if (obj instanceof EPMDocument) {
                    ContentHolder holder = ContentHelper.service.getContents((ContentHolder) obj);
                    epmdoc = (EPMDocument) holder;
                    containername = epmdoc.getNumber() + "_" + epmdoc.getName() + File.separator;
                    QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
                    while (qr.hasMoreElements()) {
                        Object objQr = qr.nextElement();
                        if (objQr instanceof ApplicationData) {
                            ad = (ApplicationData) objQr;
                            adName = ad.getFileName();
                            if (isElectronicFile(adName)) {
                                is = ContentServerHelper.service.findContentStream(ad);
                                if (is != null) {
                                    zos.putNextEntry(new ZipEntry(containername + ad.getFileName()));
                                    zos.setEncoding("GBK");
                                    int len = 0;
                                    while ((len = is.read(buf)) >= 0)
                                        zos.write(buf, 0, len);
                                    is.close();
                                }
                            }
                        }
                    }

                } else if (obj instanceof ProcessEnvelope) {
                    ContentHolder holder = ContentHelper.service.getContents((ContentHolder) obj);
                    QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
                    while (qr.hasMoreElements()) {
                        Object objQr = qr.nextElement();
                        if (objQr instanceof ApplicationData) {
                            ad = (ApplicationData) objQr;
                            adName = ad.getFileName();
                            if (isElectronicFile(adName)) {
                                is = ContentServerHelper.service.findContentStream(ad);
                                if (is != null) {
                                    zos.putNextEntry(new ZipEntry(ad.getFileName()));
                                    zos.setEncoding("GBK");
                                    int len = 0;
                                    while ((len = is.read(buf)) >= 0)
                                        zos.write(buf, 0, len);
                                    is.close();
                                }
                            }
                        }
                    }
                } else if (obj instanceof ChangePackaged) {
                    ContentHolder holder = ContentHelper.service.getContents((ContentHolder) obj);
                    QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
                    while (qr.hasMoreElements()) {
                        Object objQr = qr.nextElement();
                        if (objQr instanceof ApplicationData) {
                            ad = (ApplicationData) objQr;
                            adName = ad.getFileName();
                            if (isElectronicFile(adName)) {
                                is = ContentServerHelper.service.findContentStream(ad);
                                if (is != null) {
                                    zos.putNextEntry(new ZipEntry(ad.getFileName()));
                                    zos.setEncoding("GBK");
                                    int len = 0;
                                    while ((len = is.read(buf)) >= 0)
                                        zos.write(buf, 0, len);
                                    is.close();
                                }
                            }
                        }
                    }
                } else if (obj instanceof WTChangeOrder2) {
                    ContentHolder holder = ContentHelper.service.getContents((ContentHolder) obj);
                    QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
                    while (qr.hasMoreElements()) {
                        Object objQr = qr.nextElement();
                        if (objQr instanceof ApplicationData) {
                            ad = (ApplicationData) objQr;
                            adName = ad.getFileName();
                            if (isElectronicFile(adName)) {
                                is = ContentServerHelper.service.findContentStream(ad);
                                if (is != null) {
                                    zos.putNextEntry(new ZipEntry(ad.getFileName()));
                                    zos.setEncoding("GBK");
                                    int len = 0;
                                    while ((len = is.read(buf)) >= 0)
                                        zos.write(buf, 0, len);
                                    is.close();
                                }
                            }
                        }
                    }
                }
            }

            ExcelFileGenerator generator = new ExcelFileGenerator(titleList, dataList);
            File excelFile = new File(tempPath + File.separator + "149厂打印下载数据包清单.xls");
            if (!excelFile.exists()) {
                excelFile.createNewFile();
            }
            try {
                generator.expordExcel(new FileOutputStream(excelFile));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            is = new FileInputStream(excelFile);
            if (is != null) {
                zos.putNextEntry(new ZipEntry("149厂打印下载数据包清单.xls"));
                zos.setEncoding("GBK");
                int len = 0;
                while ((len = is.read(buf)) >= 0)
                    zos.write(buf, 0, len);
                is.close();
            }

            zos.close();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } finally {
            SessionContext.setEffectivePrincipal(currentuser);
        }
        return tempPath + File.separator + zipFileName + ".zip";

    }



    private void genDownloadDataExcelData(ArrayList<ArrayList<String>> dataList, Object obj) throws WTException {
        ArrayList<String> datas = new ArrayList<String>();
        if (obj instanceof WTDocument) {
            WTDocument o = (WTDocument) obj;
            IBAHelper iba = new IBAHelper();
            datas.add(o.getNumber());
            datas.add(tranfString(iba.getIBAStringValue(o, "CINDEX")));// 图号
            datas.add(o.getName());
            datas.add(o.getVersionInfo().getIdentifier().getValue() + "."
                    + o.getIterationInfo().getIdentifier().getValue());
            datas.add(tranfString(iba.getIBAStringValue(o, "MINDEX")));
            datas.add(tranfString(iba.getIBAStringValue(o, "DESIGNER")));
            datas.add(tranfString(iba.getIBAStringValue(o, "COMPANY")));// 设计单位
        } else if (obj instanceof EPMDocument) {
            EPMDocument o = (EPMDocument) obj;
            IBAHelper iba = new IBAHelper();
            datas.add(o.getNumber());
            datas.add(tranfString(iba.getIBAStringValue(o, "CINDEX")));// 图号
            datas.add(o.getName());
            datas.add(o.getVersionInfo().getIdentifier().getValue() + "."
                    + o.getIterationInfo().getIdentifier().getValue());
            datas.add(tranfString(iba.getIBAStringValue(o, "MINDEX")));
            datas.add(tranfString(iba.getIBAStringValue(o, "DESIGNER")));
            datas.add(tranfString(iba.getIBAStringValue(o, "COMPANY")));// 设计单位
        } else if (obj instanceof ProcessEnvelope) {
            ProcessEnvelope o = (ProcessEnvelope) obj;
            IBAHelper iba = new IBAHelper();
            datas.add(o.getNumber());
            datas.add(tranfString(iba.getIBAStringValue(o, "CINDEX")));// 图号
            datas.add(o.getName());
            datas.add("");
            datas.add(tranfString(iba.getIBAStringValue(o, "MINDEX")));
            datas.add(tranfString(iba.getIBAStringValue(o, "DESIGNER")));
            datas.add(tranfString(iba.getIBAStringValue(o, "COMPANY")));// 设计单位
        } else if (obj instanceof ChangePackaged) {
            ChangePackaged o = (ChangePackaged) obj;
            IBAHelper iba = new IBAHelper();
            datas.add(o.getNumber());
            datas.add(tranfString(iba.getIBAStringValue(o, "CINDEX")));// 图号
            datas.add(o.getName());
            datas.add("");
            datas.add(tranfString(iba.getIBAStringValue(o, "MINDEX")));
            datas.add(tranfString(iba.getIBAStringValue(o, "DESIGNER")));
            datas.add(tranfString(iba.getIBAStringValue(o, "COMPANY")));// 设计单位
        } else if (obj instanceof WTChangeOrder2) {
            WTChangeOrder2 o = (WTChangeOrder2) obj;
            IBAHelper iba = new IBAHelper();
            datas.add(o.getNumber());
            datas.add(tranfString(iba.getIBAStringValue(o, "CINDEX")));// 图号
            datas.add(o.getName());
            datas.add(o.getVersionInfo().getIdentifier().getValue() + "."
                    + o.getIterationInfo().getIdentifier().getValue());
            datas.add(tranfString(iba.getIBAStringValue(o, "MINDEX")));
            datas.add(tranfString(iba.getIBAStringValue(o, "DESIGNER")));
            datas.add(tranfString(iba.getIBAStringValue(o, "COMPANY")));// 设计单位
        }
        dataList.add(datas);

    }
    public  String zipPrimaryFile(List<String> oidList, String zipFileName) throws WTException,
    PropertyVetoException,
    IOException {
    	 WTPrincipal currentuser = null;
         String tempPath = null;
         ZipOutputStream zos = null;
         InputStream is = null;
         File zipFile = null;
         Object obj = null;
         ApplicationData ad = null;
         WTDocument wtdoc = null;
         EPMDocument epmdoc = null;
         String containername = null, adName = null;
         Iterator itOid = null;
         ReferenceFactory rf = new ReferenceFactory();
         try {
             WTPrincipal admin = SessionHelper.manager.getAdministrator();
             currentuser = SessionContext.setEffectivePrincipal(admin);
             tempPath = WTProperties.getLocalProperties().getProperty("wt.temp");
             zipFile = new File(tempPath + File.separator + zipFileName + ".zip");
             zos = new ZipOutputStream(zipFile);
             zos.setEncoding("GBK");
             byte[] buf = new byte[1024];
             itOid = oidList.iterator();

             while (itOid.hasNext()) {
                 obj = rf.getReference(itOid.next().toString()).getObject();

                 ArrayList<String> datas = new ArrayList<String>();
                 if (obj instanceof WTDocument) {
                     ContentHolder holder = ContentHelper.service.getContents((ContentHolder) obj);
                     wtdoc = (WTDocument) holder;
                     containername = wtdoc.getNumber() + "_" + wtdoc.getName() + File.separator;
                     QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
                     Vector vec = new Vector();
                     while (qr.hasMoreElements()) {
                         Object objQr = qr.nextElement();
                         if (objQr instanceof ApplicationData) {
                             ad = (ApplicationData) objQr;
                             adName = ad.getFileName();
                             vec.add(ad);

                         }
                     }
                     for (int j = 0; j < vec.size(); j++) {
                         ad = (ApplicationData) vec.get(j);
                         is = ContentServerHelper.service.findContentStream(ad);
                         if (is != null) {
                             zos.putNextEntry(new ZipEntry(containername + ad.getFileName()));
                             zos.setEncoding("GBK");
                             int len = 0;
                             while ((len = is.read(buf)) >= 0)
                                 zos.write(buf, 0, len);
                             is.close();
                         }
                     }

                 }
             }

             zos.close();
         } catch (WTException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         } catch (PropertyVetoException e) {
             e.printStackTrace();
         } finally {
             SessionContext.setEffectivePrincipal(currentuser);
         }
         return tempPath + File.separator + zipFileName + ".zip";
    }
    private String tranfString(String s) {
        if (s == null)
            return "";
        if ("null".equals(s))
            return "";
        return s;
    }

    private boolean isElectronicFile(String adName) {
        if (adName.startsWith("Print_") || adName.startsWith("print_")) {
            return true;
        } else {
            return false;
        }
    }
    //add by libo 2017/2/14 begin
	@Override
	public String zipPackets(String oid) throws Exception {
		DownloadFormProcessor downloadFormProcessor = new DownloadFormProcessor();
		String zipFilepath = downloadFormProcessor.packetsProcessor(oid);
		return zipFilepath;
	}
	//add by libo 2017/2/14 end

}

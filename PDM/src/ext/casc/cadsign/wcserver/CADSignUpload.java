package ext.casc.cadsign.wcserver;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.CRC32;

import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import com.infoengine.SAK.Task;

import ext.ases.envelope.ProcessEnvelope;
import ext.casc.fileprint.FilePrintUtil2;
import ext.casc.util.IBAHelper;

import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.fc.collections.WTKeyedHashMap;
import wt.log4j.LogR;
import wt.method.*;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pds.oracle81.OracleDataSource;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;

/**
 * <p>
 * Description:
 * </p>
 * 
 * @author: Zhong
 * @time: May 12, 2010 3:26:30 PM
 * @version 1.0
 */
// extends TimerTask
public class CADSignUpload implements RemoteAccess {

    private static final String CLASSNAME = CADSignUpload.class.getName();

    private static String wt_home = "";

    private static String wcTempPath = "D:\\temp\\temp\\dwgsign\\";

    // 需要签名的flag,用来标识签名表信息
    private static int needSignFlag = 0;
    
    private static final Logger log;
    
    static {
           try {
              log = LogR.getLogger(CADSignUpload.class.getName());
           }
           catch (Exception e) {
              throw new ExceptionInInitializerError(e);
           }
        }
    
    static {
        try {
            WTProperties wtProp = WTProperties.getLocalProperties();
            wt_home = wtProp.getProperty("wt.home");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("严重错误,获取wt.home值失败！"); 
        }
        wcTempPath = wt_home+File.separator + "temp" + File.separator + "temp" + File.separator + "dwgsign" + File.separator;
        File file = new File(wcTempPath);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    public static void run(WTObject pbo, ObjectReference self, WTPart thePart)
            throws Exception {
        Hashtable ht = CADSignUpdate.getReviews(self);
        Vector docsVector = new Vector();
        docsVector.add(pbo);
        /*WTPart part = (WTPart) new wt.fc.ReferenceFactory().getReference(
                partOid).getObject();*/
        exportMXCab(thePart, ht, docsVector);
    }
    
    public static void run(Object pbo,Hashtable hashtable) throws Exception {
        if(!CADSignHelper.cadsignEnabled())
            return;
        Vector docsVector = new Vector();
        for (java.util.Enumeration enum1 = hashtable.keys() ; enum1.hasMoreElements() ;) {
             java.lang.String s = (java.lang.String)enum1.nextElement();
             wt.fc.WTObject obj = (wt.fc.WTObject)ext.casc.fileprint.FilePrintUtil2.getObject(s);
             if(obj instanceof WTDocument){
                 String primaryFileName = FilePrintUtil2.getPrimaryFileName((FormatContentHolder)obj);
                 if(primaryFileName.substring(primaryFileName.lastIndexOf('.') + 1).toUpperCase().endsWith("DWG")){
                     docsVector.add(obj);
                     Hashtable ht = (Hashtable)hashtable.get(s);
                     exportMXCab(pbo,obj,ht, docsVector);
                 }
             }
        }
    }
    
    // 明细表签名 add by lxc， 2009-05-29
    public static void exportMXCab(Object pbo,Object obj,Hashtable reviewHashtable,
            Vector docsVector) throws WTException, IOException,
            FileNotFoundException, Exception {
        log.debug("---------------------------export mx cab begin-------------------------");
        // 导出
        String tmpSequence = System.currentTimeMillis() + "";// PersistenceHelper.manager.getNextSequence("SignRequest_seq");
        String tempPath = wcTempPath;
        String folderName = ((WTDocument)obj).getNumber() + "-"
            + ((WTDocument)obj).getVersionIdentifier().getValue();
        tempPath = tempPath + folderName;
        newFolder(tempPath);
        do{
            System.out.println("in creating dir...");
        }while(!(new File(tempPath)).exists());
        
        tempPath = tempPath + File.separator + tmpSequence + "-" + folderName;
        newFolder(tempPath);
        log.debug("tempPath-----------" + tempPath);
        String xmlFileName = "structure.xml";
        String filePathName = tempPath + File.separator + xmlFileName;
        FileOutputStream logoutputstream = new FileOutputStream(filePathName,
                true);
        PrintWriter xmlWriter = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(logoutputstream)));
        xmlWriter.println("<?xml version=\"1.0\" encoding=\"gb2312\"?>");
        xmlWriter.println("<PDMDataFile FFDNumber=\"\" action=\"sign\">");
        String txtFileName = "signinfo.txt";
        String filePathName1 = tempPath + File.separator + txtFileName;
        FileOutputStream logoutputstream1 = new FileOutputStream(filePathName1,
                true);
        PrintWriter txtWriter = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(logoutputstream1)));
        //从签审信息中提取信息
        Vector signInfo = new Vector();
        String tufuString = "A4,A3,A2,A1,A0,2A0,3A0";
        String format = "FORMATDWG";
        if(pbo instanceof WTChangeOrder2){
            String type = IBAHelper.getSoftType((WTObject)pbo);
            if(type.indexOf("CHANGEPHASE_ECN")>-1)
                format = "FORMATDWGZJD";
        }
        String tufuList[] = tufuString.split(",");
        for(int j = 0; j<tufuList.length; j++){
            String tufu = tufuList[j];

            //ywu 2011.2.7 添加多审核处理
            FilePrintUtil2.processForMultiReview(reviewHashtable);
            //ywu end
            signInfo = FilePrintUtil2.getDWGSignInfo(reviewHashtable, format,tufu);
            String key = "";
            String value = "";
            String locationx = "";
            String locationy = "";
            for(int i = 0;i < signInfo.size();i++)
            {
                Hashtable hs = (Hashtable)signInfo.elementAt(i);
                key = (String)hs.get(FilePrintUtil2.ATTR);
                value = (String)hs.get(FilePrintUtil2.ATTRVALUE);
                locationx = (String)hs.get(FilePrintUtil2.LOCATIONX);
                locationy = (String)hs.get(FilePrintUtil2.LOCATIONY);
                txtWriter.println(tufu + "||" + key + "||" + value + "||" + locationx + "||" + locationy +"\n\r");
            }
        }

        txtWriter.flush();
        txtWriter.close();

        Vector docnumberVector = new Vector();
        log.debug("--------------------------------");
        System.out
                .println("-----------------docsVector.size()------------------"
                        + docsVector.size());
        log.debug("-------------------------------------");
        a: for (int i = 0; i < docsVector.size(); i++) {
            WTObject wto = (WTObject) docsVector.elementAt(i);
            if (wto instanceof WTDocument) {
                WTDocument doc = (WTDocument) wto;
                // String contentFileName =
                // CADSignHelper.downloadDocPriFile(doc,"d:\\temp");
                String contentFileName = getContentFileName(doc);
                log.debug("-----Name------------------" + contentFileName);
                String docNumber = doc.getNumber();
                log.debug("-----Number---------------" + docNumber);
                System.out.println("-------------------------------------------------------------------");
                String docVersion = doc.getVersionIdentifier().getValue();
                String docIteration = doc.getIterationIdentifier().getValue();
                if (!contentFileName.toUpperCase().endsWith(".DWG")) {
                    continue a;
                }
                String docSoftType = getDocumentType(doc);
                log.debug("docsofttype------->" + docSoftType);
                // if
                // (docSoftType.contains("wt.doc.WTDocument|fai.nic.EOOdrDoc"))
                // {
                if (!docnumberVector.contains(docNumber)) {
                    downLoadContent(doc, tmpSequence);
                    docnumberVector.add(docNumber);
                    xmlWriter.println("<CADDocument DocID=\"" + docNumber
                            + "\" Version=\"" + docVersion + "\" Iteration=\""
                            + docIteration + "\" ContentFileNmae=\""
                            + contentFileName + "\">");
                    xmlWriter.println("<Attributes>");
                    /*
                    xmlWriter.println("<signinfo role=\"" + "设计"
                            + "\" user=\"" + SHEJIZHESHIJIAN
                            + "\" time=\"" + SHEJIZHESHIJIAN + "\"/>");
                    xmlWriter.println("<signinfo role=\"" + "校对"
                            + "\" user=\"" + JIAODUIZHESHIJIAN
                            + "\" time=\"" + JIAODUIZHESHIJIAN + "\"/>");
                    xmlWriter.println("<signinfo role=\"" + "审核"
                            + "\" user=\"" + SHENHEZHESHIJIAN
                            + "\" time=\"" + SHENHEZHESHIJIAN + "\"/>");
                    xmlWriter.println("<signinfo role=\"" + "标审"
                            + "\" user=\"" + BIAOSHENZHESHIJIAN
                            + "\" time=\"" + BIAOSHENZHESHIJIAN + "\"/>");
                    xmlWriter.println("<signinfo role=\"" + "批准"
                            + "\" user=\"" + PIZHUNZHESHIJIAN
                            + "\" time=\"" + PIZHUNZHESHIJIAN + "\"/>");
                            */
                    xmlWriter.println("</Attributes>");
                    xmlWriter.println("</CADDocument>");
                }
            }
        }
        xmlWriter.println("</PDMDataFile>");
        xmlWriter.flush();
        xmlWriter.close();

        // 打包
        String zipPathName = zipFile(tmpSequence, folderName);
        String ftpZipPathName = new File(zipPathName).getName();
        // ftp至中间机
        String rmtIp = CADSignHelper.getValueProperties("sign.ftp.ip");
        String user = CADSignHelper.getValueProperties("sign.ftp.user");
        String password = CADSignHelper.getValueProperties("sign.ftp.password");
        log.debug("本地文件地址：" + zipPathName);
        log.debug("FTP文件地址：" + ftpZipPathName);
        String folderr = CADSignHelper.getValueProperties("sign.ftp.signFolder");
        System.out.println("zipPathName="+zipPathName);
        System.out.println("folderr+ftpZipPathName="+folderr + File.separator+ftpZipPathName);
        FTPUtil.ftpToSign(zipPathName, folderr + File.separator + ftpZipPathName);

        // 写入数据库------------------------------------------------
        SendToMXSignReq(ftpZipPathName.substring(0, ftpZipPathName.indexOf(".zip")), tmpSequence);
        // 删除压缩包
        //ZipFileUtil.deleteFiles(zipPathName);
    }

    // 明细表签名 add by lxc， 2009-05-29
    public static void exportMXCab(WTPart thePart, Hashtable reviewHashtable, Vector docsVector) throws WTException, IOException,
            FileNotFoundException, Exception {
        System.out.println("---------------------------export mx cab begin-------------------------");
        // 导出
        String tmpSequence = System.currentTimeMillis() + "";// PersistenceHelper.manager.getNextSequence("SignRequest_seq");
        String tempPath = wcTempPath;
        String folderName = thePart.getNumber() + "-" + thePart.getVersionIdentifier().getValue();
        tempPath = tempPath + folderName;
        newFolder(tempPath);
        tempPath = tempPath + File.separator + tmpSequence + "-" + folderName;
        newFolder(tempPath);
        log.debug("tempPath-----------" + tempPath);
        String xmlFileName = "structure.xml";
        String filePathName = tempPath + File.separator + xmlFileName;
        FileOutputStream logoutputstream = new FileOutputStream(filePathName, true);
        PrintWriter xmlWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(logoutputstream)));
        xmlWriter.println("<?xml version=\"1.0\" encoding=\"gb2312\"?>");
        xmlWriter.print("<PDMDataFile FFDNumber=\"\" action=\"sign\">");
        String txtFileName = "signinfo.txt";
        String filePathName1 = tempPath + File.separator + txtFileName;
        FileOutputStream logoutputstream1 = new FileOutputStream(filePathName1, true);
        PrintWriter txtWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(logoutputstream1)));
        // txtWriter.println("流程版本||1");
        Enumeration enum2 = reviewHashtable.keys();
        while (enum2.hasMoreElements()) {
            String key = (String) enum2.nextElement();
            Hashtable tempHashtable = (Hashtable) reviewHashtable.get(key);
            String activityName = (String) tempHashtable.get("activityName");
            String rolePrincipalId = (String) tempHashtable.get("rolePrincipalId"); // lxc
            String endTime = (String) tempHashtable.get("endTime");
            txtWriter.println(activityName + "||" + rolePrincipalId + "||" + endTime);
        }
        txtWriter.flush();
        txtWriter.close();

        Vector docnumberVector = new Vector();
        log.debug("--------------------------------");
        System.out.println("-----------------docsVector.size()------------------"
                        + docsVector.size());
        log.debug("-------------------------------------");
        a: for (int i = 0; i < docsVector.size(); i++) {
            WTObject wto = (WTObject) docsVector.elementAt(i);
            if (wto instanceof WTDocument) {
                WTDocument doc = (WTDocument) wto;
                // String contentFileName =
                // CADSignHelper.downloadDocPriFile(doc,"d:\\temp");
                String contentFileName = getContentFileName(doc);
                log.debug("-----Name------------------" + contentFileName);
                String docNumber = doc.getNumber();
                log.debug("-----Number---------------" + docNumber);
                System.out.println("-------------------------------------------------------------------");
                String docVersion = doc.getVersionIdentifier().getValue();
                String docIteration = doc.getIterationIdentifier().getValue();
                if (!contentFileName.toUpperCase().endsWith(".DWG")) {
                    continue a;
                }
                String docSoftType = getDocumentType(doc);
                log.debug("docsofttype------->" + docSoftType);
                // if
                // (docSoftType.contains("wt.doc.WTDocument|fai.nic.EOOdrDoc"))
                // {
                if (!docnumberVector.contains(docNumber)) {
                    downLoadContent(doc, thePart, tmpSequence);
                    docnumberVector.add(docNumber);
                    xmlWriter.print("<CADDocument DocID=\"" + docNumber
                            + "\" Version=\"" + docVersion + "\" Iteration=\""
                            + docIteration + "\" ContentFileNmae=\""
                            + contentFileName + "\">");
                    xmlWriter.print("<Attributes>");
                    Enumeration enum1 = reviewHashtable.keys();
                    while (enum1.hasMoreElements()) {
                        String key = (String) enum1.nextElement();
                        Hashtable tempHashtable = (Hashtable) reviewHashtable.get(key);
                        String activityName = (String) tempHashtable.get("activityName");
                        String rolePrincipalName = (String) tempHashtable.get("rolePrincipalName");
                        String rolePrincipalId = (String) tempHashtable.get("rolePrincipalId");
                        String endTime = (String) tempHashtable.get("endTime");
                        log.debug("keykeykeykeykeykeykey=" + key);
                        log.debug("tempHashtabletempHashtable=" + tempHashtable);
                        log.debug("activityNameactivityName=" + activityName);
                        if (activityName.equals("编制")) {
                            //ywu 2011.1.18　修改编制人为修改者
                            WTUser theModifier = (WTUser) doc.getModifier().getPrincipal();
                            String userFullName = theModifier.getFullName();
                            xmlWriter.print("<signinfo role=\"" + activityName
                                    + "\" user=\"" + userFullName
                                    + "\" time=\"" + endTime + "\"/>");
                        } else if (!activityName.equals("内部分发")) {
                            xmlWriter.print("<signinfo role=\"" + activityName
                                    + "\" user=\"" + rolePrincipalName
                                    + "\" time=\"" + endTime + "\"/>");
                        }
                    }
                    xmlWriter.print("</Attributes>");
                    xmlWriter.print("</CADDocument>");
                }
                // }
                /*
                 * else if (docSoftType.equals("wt.doc.WTDocument")) { String
                 * xhfl = IBAHelper .getIBAStringValue(wto, "DETAILTYPE");
                 * log.debug("xhfl:::::: " + xhfl); if
                 * (xhfl.equals("明细表") || xhfl.equals("图样目录")) { if
                 * (!docnumberVector.contains(docNumber)) { downLoadContent(doc,
                 * thePart, tmpSequence); docnumberVector.add(docNumber);
                 * xmlWriter.print("<CADDocument DocID=\"" + docNumber + "\"
                 * Version=\"" + docVersion + "\" Iteration=\"" + docIteration +
                 * "\" ContentFileNmae=\"" + contentFileName + "\">");
                 * xmlWriter.print("<Attributes>"); Enumeration enum1 =
                 * reviewHashtable.keys(); while (enum1.hasMoreElements()) {
                 * String key = (String) enum1.nextElement(); Hashtable
                 * tempHashtable = (Hashtable) reviewHashtable .get(key); String
                 * activityName = (String) tempHashtable .get("activityName");
                 * String rolePrincipalName = (String) tempHashtable
                 * .get("rolePrincipalName"); String rolePrincipalId = (String)
                 * tempHashtable .get("rolePrincipalId"); String endTime =
                 * (String) tempHashtable .get("endTime");
                 * 
                 * if (activityName.equals("编制")) { WTUser theCreator = (WTUser)
                 * doc .getCreator().getPrincipal(); String userFullName =
                 * theCreator .getFullName(); xmlWriter.print("<signinfo
                 * role=\"" + activityName + "\" user=\"" + userFullName + "\"
                 * time=\"" + endTime + "\"/>"); } else if
                 * (!activityName.equals("内部分发")) { xmlWriter.print("<signinfo
                 * role=\"" + activityName + "\" user=\"" + rolePrincipalName +
                 * "\" time=\"" + endTime + "\"/>"); } } xmlWriter.print("</Attributes>");
                 * xmlWriter.print("</CADDocument>"); } }
                 *  }
                 */
            }
        }
        xmlWriter.print("</PDMDataFile>");
        xmlWriter.flush();
        xmlWriter.close();

        // 打包
        String zipPathName = zipFile(tmpSequence, folderName);
        String ftpZipPathName = new File(zipPathName).getName();
        // ftp至中间机
        String rmtIp = CADSignHelper.getValueProperties("sign.ftp.ip");
        String user = CADSignHelper.getValueProperties("sign.ftp.user");
        String password = CADSignHelper.getValueProperties("sign.ftp.password");
        log.debug("本地文件地址：" + zipPathName);
        log.debug("FTP文件地址：" + ftpZipPathName);
        String folderr = CADSignHelper
                .getValueProperties("sign.ftp.signFolder");
        FTPUtil.ftpToSign(zipPathName, folderr + File.separator
                + ftpZipPathName);

        // 写入数据库------------------------------------------------
        SendToMXSignReq(ftpZipPathName.substring(0, ftpZipPathName
                .indexOf(".zip")), tmpSequence);
        // 删除压缩包
        ZipFileUtil.deleteFiles(zipPathName);
    }

    // 向数据库中间表写入信息，add by lxc,2009-05-30
    public static void SendToMXSignReq(String zipName, String tmpSequence) {
        try {
            if (zipName == null)
                zipName = " ";
            // String tmpSequence =
            // PersistenceHelper.manager.getNextSequence("GiveVpmMsg_seq");
            String sql = "insert into signrequest (REQ_ID, ZIPNAME,FLAG,SIGNTYPE) values ('"
                    + tmpSequence + "','" + zipName + "'," + needSignFlag + ",'dwg')";
            log.debug("向数据库中间表写入信息:" + sql);
            // 执行sql
            // Task task = new Task("ext/xac2/sql/publishSignreq.xml");
            // task.addParam("sql",sql);
            // task.invoke();
            Connection conn1 = OracleDataSource.getOracleDataSource().getConnection();
            conn1.setAutoCommit(false);
            Statement state1 = conn1.createStatement();
            state1.execute(sql);
            conn1.commit();
            state1.close(); 
            conn1.close();
        } catch (Exception wte) {
            log.debug(wte.getLocalizedMessage());
        }
        return;
    }

    public static void downLoadContent(WTDocument theDocument, WTPart thePart,
            String tmpSequence) {
        try {
            String fileName = "";
            String fileAbsolutePath = "";
            FormatContentHolder doc = null;
            doc = (FormatContentHolder) theDocument;
            InputStream inputstream = null;
            FileOutputStream tout = null;
            wt.content.ContentItem docContentItem = ContentHelper.service
                    .getPrimary(theDocument);
            if (docContentItem != null) {
                ApplicationData applicationdataPrimary = (ApplicationData) docContentItem;
                fileName = applicationdataPrimary.getFileName();
                inputstream = ContentServerHelper.service
                        .findContentStream(applicationdataPrimary);
                // WTProperties wtProp = WTProperties.getLocalProperties();
                /*
                 * String tempPath = wtProp
                 * .getProperty("ext.fai.modelexport.home");
                 */
                String tempPath = wcTempPath;
                String folderName = thePart.getNumber() + "-"
                        + thePart.getVersionIdentifier().getValue();
                tempPath = tempPath + folderName + File.separator + tmpSequence
                        + "-" + folderName;
                newFolder(tempPath);

                fileAbsolutePath = tempPath + File.separator + fileName;
                tout = new FileOutputStream(fileAbsolutePath);
                byte abyte0[] = new byte[2048];
                int j = 0;
                while ((j = inputstream.read(abyte0, 0, abyte0.length)) >= 0)
                    tout.write(abyte0, 0, j);
                tout.close();
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } catch (java.beans.PropertyVetoException pve) {
            pve.printStackTrace();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public static void downLoadContent(WTDocument theDocument, String tmpSequence) {
        try {
            String fileName = "";
            String fileAbsolutePath = "";
            FormatContentHolder doc = null;
            doc = (FormatContentHolder) theDocument;
            InputStream inputstream = null;
            FileOutputStream tout = null;
            wt.content.ContentItem docContentItem = ContentHelper.service
                    .getPrimary(theDocument);
            if (docContentItem != null) {
                ApplicationData applicationdataPrimary = (ApplicationData) docContentItem;
                fileName = applicationdataPrimary.getFileName();
                inputstream = ContentServerHelper.service
                        .findContentStream(applicationdataPrimary);
                // WTProperties wtProp = WTProperties.getLocalProperties();
                /*
                 * String tempPath = wtProp
                 * .getProperty("ext.fai.modelexport.home");
                 */
                String tempPath = wcTempPath;
                String folderName = theDocument.getNumber() + "-"
                        + theDocument.getVersionIdentifier().getValue();
                tempPath = tempPath + folderName + File.separator + tmpSequence
                        + "-" + folderName;
                newFolder(tempPath);

                fileAbsolutePath = tempPath + File.separator + fileName;
                tout = new FileOutputStream(fileAbsolutePath);
                byte abyte0[] = new byte[2048];
                int j = 0;
                while ((j = inputstream.read(abyte0, 0, abyte0.length)) >= 0)
                    tout.write(abyte0, 0, j);
                tout.close();
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } catch (java.beans.PropertyVetoException pve) {
            pve.printStackTrace();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * 新建目录
     * 
     * @param folderPath
     *            String 如 c:/fqf
     * @return boolean
     */
    public static void newFolder(String folderPath) {
        try {
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            if (!myFilePath.exists()) {
                boolean result = myFilePath.mkdirs();
                System.out.println("mkdir="+result);
            }
            if(myFilePath.exists())
                System.out.println("mkdir ["+myFilePath.getAbsolutePath()+"] finish");
        } catch (Exception e) {
            log.debug("新建目录操作出错");
            e.printStackTrace();
        }
    }

    private static boolean VERBOSE = true;

    public static String zipFile(String tmpSequence, String folderName)
            throws Exception {
        WTProperties wtproperties = WTProperties.getLocalProperties();
        String inputFileBase = wcTempPath;// wtproperties.getProperty("ext.fai.modelexport.home");
        log.debug("folderName= " + folderName);
        String inputPath = inputFileBase + folderName + File.separator
                + tmpSequence + "-" + folderName;
        String zipName = inputPath + ".zip";
        // String zipName = inputFileBase + tmpSequence + "-" + folderName +
        // ".zip";
        log.debug("要压缩的文件夹=" + inputPath);
        ZipFileUtil.zipFile(inputPath, zipName);
        // 删除该文件夹
//      ZipFileUtil.deleteFiles(inputPath);
        return zipName;
    }

    /**
     * 得到指定文档的类型
     * 
     * @param doc
     * @return 返回形如WCTYPE|wt.doc.WTDocument|com.xacnet.To的串
     */
    public static String getDocumentType(WTDocument doc) {
        String docType = "";
        try {
            if (!RemoteMethodServer.ServerFlag) {
                Class[] cla = { WTDocument.class };
                Object[] obj = { doc };
                return (String) RemoteMethodServer.getDefault().invoke(
                        "getDocumentType", CLASSNAME, null, cla, obj);
            }
            if (doc != null) {
                docType = wt.type.TypedUtility
                        .getExternalTypeIdentifier((wt.type.Typed) doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return docType;
    }

    public static void zip(String zipFileName, File inputFile) throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                zipFileName));
        zip(out, inputFile, "");
        out.close();

    }

    public static void zip(ZipOutputStream out, File f, String base)
            throws Exception {
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], base + fl[i].getName());
            }
        } else {
            ZipEntry zipentry = new org.apache.tools.zip.ZipEntry(base);
            zipentry.setMethod(ZipEntry.STORED);// 不压缩
            zipentry.setSize(f.length());
            zipentry.setCrc(calcChecksum(f));
            out.putNextEntry(zipentry);
            FileInputStream in = new FileInputStream(f);
            int b;
            if (VERBOSE)
                log.debug(base);
            byte[] buf = new byte[2048];
            while ((b = in.read(buf)) > 0) {
                out.write(buf, 0, b);
            }
            in.close();
        }
    }

    private static long calcChecksum(File f) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));

        return calcChecksum(in, f.length());
    }

    /*
     * Necessary in the case where you add a entry that is not compressed.
     */
    private static byte[] buffer = new byte[8192];

    private static long calcChecksum(InputStream in, long size)
            throws IOException {
        CRC32 crc = new CRC32();
        int len = buffer.length;
        int count = -1;
        int haveRead = 0;

        while ((count = in.read(buffer, 0, len)) > 0) {
            haveRead += count;
            crc.update(buffer, 0, count);
        }
        in.close();
        return crc.getValue();
    }

    public static boolean deleteFiles(String inputPath) {
        try {
            File f = new File(inputPath);
            if (f.isDirectory()) {
                File[] flist = f.listFiles();
                for (int i = 0; i < flist.length; i++) {
                    File tmpfile = (File) flist[i];
                    if (VERBOSE)
                        log.debug("current file is : "
                                + tmpfile.getAbsolutePath() + "---delete ");
                    deleteFiles(tmpfile.getAbsolutePath());
                }
                f.delete();
            } else
                f.delete();

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 下载文档对象的主内容到指定的文件夹
     * 
     * @param wtdocument
     *            文档对象
     * @param tempDir
     *            要存放的文件夹
     * @return 下载下来的主内容位置tempDir+ "/" + 文档编号 +"/" + 主内容文件名
     */
    public static String downloadDocPriFile(WTDocument wtdocument,
            String tempDir) {
        try {
            if (!tempDir.endsWith("/") && !tempDir.endsWith("\\")) {
                tempDir = tempDir + File.separator;
            }
            String downloadDirectoryStr = tempDir; // tempDir +
            // wtdocument.getNumber();
            File file = new File(downloadDirectoryStr);
            if (!file.exists()) {
                file.mkdirs();
            }
            String contentFileName = "";
            ContentItem item = (ContentItem) ContentHelper.service
                    .getPrimary(wtdocument);
            if (item instanceof ApplicationData) {
                ApplicationData appData = (ApplicationData) item;
                contentFileName = appData.getFileName();
                downloadDirectoryStr = downloadDirectoryStr + contentFileName;
                ContentServerHelper.service.writeContentStream(appData,
                        downloadDirectoryStr);
            }
            return downloadDirectoryStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Connection getConnection() {

        return null;
    }

    public static String getContentFileName(WTDocument theDocument) {
        String fileName = "";
        try {
            wt.content.ContentItem docContentItem = ContentHelper.service
                    .getPrimary(theDocument);
            if (docContentItem != null) {
                ApplicationData applicationdataPrimary = (ApplicationData) docContentItem;
                fileName = applicationdataPrimary.getFileName();
                return fileName;
            } else {
                fileName = "";
            }
        } catch (WTException wte) {
            wte.printStackTrace();
        } catch (java.beans.PropertyVetoException pve) {
            pve.printStackTrace();
        }
        return fileName;
    }

    public static void test(String partOid, String docOid) {

        Hashtable view = new Hashtable();

        // 编制人就创建者本人
        Hashtable tempHashtable1 = new Hashtable();

        tempHashtable1.put("activityName", "编制");
        tempHashtable1.put("endTime", "2010/10/01");
        tempHashtable1.put("rolePrincipalName", "编制者");
        tempHashtable1.put("rolePrincipalId", "Qwcadmin");

        view.put("tempHashtable1", tempHashtable1);

        Hashtable tempHashtable2 = new Hashtable();

        tempHashtable2.put("activityName", "校对");
        tempHashtable2.put("endTime", "2010/10/01");
        tempHashtable2.put("rolePrincipalName", "李四兄弟");
        tempHashtable2.put("rolePrincipalId", "Qlisi");

        view.put("tempHashtable2", tempHashtable2);

        Hashtable tempHashtable3 = new Hashtable();

        tempHashtable3.put("activityName", "审核");
        tempHashtable3.put("endTime", "2010/12/01");
        tempHashtable3.put("rolePrincipalName", "王五");
        tempHashtable3.put("rolePrincipalId", "Qwangwu");

        view.put("tempHashtable3", tempHashtable3);

        Hashtable tempHashtable4 = new Hashtable();

        tempHashtable4.put("activityName", "批准");
        tempHashtable4.put("endTime", "2010/12/01");
        tempHashtable4.put("rolePrincipalName", "赵六");
        tempHashtable4.put("rolePrincipalId", "Qzhaoliu");

        view.put("tempHashtable4", tempHashtable4);

        try {
            WTPart part = (WTPart) new wt.fc.ReferenceFactory().getReference(
                    partOid).getObject();
            WTDocument doc = (WTDocument) new wt.fc.ReferenceFactory()
                    .getReference(docOid).getObject();
            Vector docVector = new Vector();
            docVector.add(doc);
            exportMXCab(part, view, docVector);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // zip名称,是否签名完毕，是否已更新
    public static void main(String[] args) {
        try {
            RemoteMethodServer rms = RemoteMethodServer.getDefault();
            rms.setUserName("wcadmin");
            rms.setPassword("wcadmin");

            Class cla[] = { String.class, String.class };
            Object obj[] = { args[0], args[1] };
            RemoteMethodServer.getDefault().invoke("test", CLASSNAME, null,
                    cla, obj);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}

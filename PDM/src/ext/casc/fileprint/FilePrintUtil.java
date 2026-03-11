package ext.casc.fileprint;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.work.WfAssignedActivity;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;
import com.ptc.wvs.server.util.Util;

import ext.casc.cadsign.wcserver.CADSignUpload;
import ext.casc.util.IBAHelper;
import ext.casc.util.WCUtil;
import ext.casc.workflow.PrintHelper;

public class FilePrintUtil implements RemoteAccess {
    public static String HTML_DOWNLOAD_PRINT_APPLY_LINK_PRE = "<a href=\"netmarkets/jsp/ext/ases/document/downloadPrintFile.jsp?oid=";
    public static String HTML_LINK_SIGN_PRINT = "<a href=\"netmarkets/jsp/ext/ases/document/sign.jsp?oid=";
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
    public static final String propertiesfile = "ext.casc.fileprint.signtemplate";
    private static final String filePrintProperties = "ext.casc.fileprint.fileprint";
    public static WTProperties wtProperties;
    private static final Logger log;
    private static final boolean SERVER = RemoteMethodServer.ServerFlag;
    public static String HTML_LINK_PRE_PRINT = "<a href=\"netmarkets/jsp/ext/ases/document/prePrint.jsp?oid=";
    public static String HTML_LINK_MID = "\">";
    public static String HTML_LINK_BLANK_MID = "\" target=\"_blank\">";
    public static String HTML_LINK_AFTER = "</a>";
    public static String HTML_LINK_ECN_PRINT = "<a href=\"netmarkets/jsp/ext/ases/ecn/ecnPrint.jsp?oid=";
    public static String HTML_LINK_PRINT_APPLY = "<a href=\"netmarkets/jsp/ext/casc/printApply/openPrintApply.jsp?oid=";

    static {
        try {
            log = LogR.getLogger(FilePrintUtil.class.getName());
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

    public static void main(String[] args) {
        FilePrintUtil filePrintUtil = new FilePrintUtil();
        try {
            String oid1 = args[0];//OR:ext.ases.envelope.ProcessEnvelope:189739
            if (oid1 == null) {
                oid1 = "";
            }
            String oid2 = args[1];//OR:wt.workflow.engine.WfProcess:189741
            if (oid2 == null) {
                oid2 = "";
            }
            RemoteMethodServer rms = RemoteMethodServer.getDefault();
            rms.setUserName("wcadmin");
            rms.setPassword("wcadmin");

            WTObject pbo = (WTObject) WCUtil.getPersistable(oid1);
            WfProcess wf = (WfProcess) WCUtil.getPersistable(oid2);
            filePrintUtil.writeReviewtoPDF2(pbo, wf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 电子签名方法入口
     *
     * @param pbo
     * @param self
     * @throws Exception
     */
    public static void writeReviewtoPDF2(Object pbo, Object self) throws Exception {
        log.debug("-------starting  sign    pbo:" + pbo + "   self:" + self);
        if (!SERVER) {
            Class[] cla = {Object.class, Object.class};
            Object[] objs = {pbo, self};
            try {
                RemoteMethodServer.getDefault().invoke("writeReviewtoPDF2", FilePrintUtil.class.getName(), null, cla,
                        objs);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            //获取签名信息
            boolean accessFlag = SessionServerHelper.manager
                    .setAccessEnforced(false);
            Hashtable hashtable = PrintHelper.getPrintInfo(pbo, self);

            // dwg签名
            CADSignUpload.run(pbo, hashtable);

            //删除签名文件
            Vector reviewList2 = new Vector();
            for (Enumeration enum1 = hashtable.keys(); enum1.hasMoreElements(); ) {
                String s = (String) enum1.nextElement();
                WTObject obj = (WTObject) getObject(s);
                reviewList2.add(obj);
            }
            removePrintAttachement(reviewList2);

            // 添加可视化到附件
            Vector reviewList = new Vector();
            for (Enumeration enum1 = hashtable.keys(); enum1.hasMoreElements(); ) {
                String s = (String) enum1.nextElement();
                WTObject obj = (WTObject) FilePrintUtil.getObject(s);
                reviewList.add(obj);
            }
            FilePrintUtil.addRepToAttachment(reviewList);

            log.debug("-------hashtable:" + hashtable);
            // 写电子签名
            FilePrintUtil2.writeReviewtoPDF(hashtable, pbo);
            // throw new WTException("FOR TEST");
            SessionServerHelper.manager
                    .setAccessEnforced(accessFlag);
        }

    }


    /**
     * 工艺规程电子签名方法入口
     *
     * @param pbo
     * @param self
     * @throws Exception
     */
    public static void writeProcessReviewtoPDF(Object pbo, Object self) throws Exception {
        log.debug("-------starting  sign    pbo:" + pbo + "   self:" + self);
        if (!SERVER) {
            Class[] cla = {Object.class, Object.class};
            Object[] objs = {pbo, self};
            try {
                RemoteMethodServer.getDefault().invoke("writeProcessReviewtoPDF", FilePrintUtil.class.getName(), null, cla,
                        objs);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            boolean accessFlag = SessionServerHelper.manager
                    .setAccessEnforced(false);
            //获取签名信息
            Hashtable hashtable = PrintHelper.getPrintInfo(pbo, self);

            // dwg签名
            CADSignUpload.run(pbo, hashtable);

            //删除签名文件
            Vector reviewList2 = new Vector();
            for (Enumeration enum1 = hashtable.keys(); enum1.hasMoreElements(); ) {
                String s = (String) enum1.nextElement();
                WTObject obj = (WTObject) getObject(s);
                reviewList2.add(obj);
            }
            removePrintAttachement(reviewList2);
            log.debug("-------hashtable:" + hashtable);
            // 写电子签名
            FilePrintUtil2.writeReviewtoPDF(hashtable, pbo);
            // throw new WTException("FOR TEST");
            SessionServerHelper.manager
                    .setAccessEnforced(accessFlag);

        }

    }


    /**
     * @param pbo
     * @param self
     * @throws Exception
     */
    public static void writeProcessTempSign(String oid, Map<String, String> map) throws Exception {
        ReferenceFactory rf = new ReferenceFactory();
        Object pbo = rf.getReference(oid).getObject();
        if (!SERVER) {
            Class[] cla = {Object.class};
            Object[] objs = {pbo};
            try {
                RemoteMethodServer.getDefault().invoke("writeProcessReviewtoPDF", FilePrintUtil.class.getName(), null, cla,
                        objs);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            boolean accessFlag = SessionServerHelper.manager
                    .setAccessEnforced(false);
            //获取签名信息
            Hashtable hashtable = PrintHelper.getPrintTempSign(pbo, map);

            // dwg签名
            CADSignUpload.run(pbo, hashtable);

            //删除签名文件
            Vector reviewList2 = new Vector();
            for (Enumeration enum1 = hashtable.keys(); enum1.hasMoreElements(); ) {
                String s = (String) enum1.nextElement();
                WTObject obj = (WTObject) getObject(s);
                reviewList2.add(obj);
            }
            removePrintAttachement(reviewList2);
            log.debug("-------hashtable:" + hashtable);
            // 写电子签名
            FilePrintUtil2.writeReviewtoPDF(hashtable, pbo);
            // throw new WTException("FOR TEST");
            SessionServerHelper.manager
                    .setAccessEnforced(accessFlag);

        }

    }

    /**
     * 工艺规程电子签名方法入口
     *
     * @param pbo
     * @param self
     * @throws Exception
     */
    public static void writeProcessReviewtoPDF(WTObject pbo) throws Exception {
        if (!SERVER) {
            Class[] cla = {WTObject.class};
            Object[] objs = {pbo};
            try {
                RemoteMethodServer.getDefault().invoke("writeProcessReviewtoPDF", FilePrintUtil.class.getName(), null, cla,
                        objs);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            boolean accessFlag = SessionServerHelper.manager
                    .setAccessEnforced(false);
            try {
                QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(pbo, null, null);
                while (qrProcs.hasMoreElements()) {
                    WfProcess proc = (WfProcess) qrProcs.nextElement();
                    if (proc.getState().equals(WfState.OPEN_RUNNING) || proc.getState().equals(WfState.CLOSED_COMPLETED_EXECUTED)) {
                        //获取签名信息
                        Hashtable hashtable = PrintHelper.getPrintInfo(pbo, proc);

                        // dwg签名
                        CADSignUpload.run(pbo, hashtable);

                        //删除签名文件
                        Vector reviewList2 = new Vector();
                        for (Enumeration enum1 = hashtable.keys(); enum1.hasMoreElements(); ) {
                            String s = (String) enum1.nextElement();
                            WTObject obj = (WTObject) getObject(s);
                            reviewList2.add(obj);
                        }
                        removePrintAttachement(reviewList2);
                        log.debug("-------hashtable:" + hashtable);
                        // 写电子签名
                        FilePrintUtil2.writeReviewtoPDF(hashtable, pbo);
                        break;
                    }
                    // throw new WTException("FOR TEST");
                    SessionServerHelper.manager
                            .setAccessEnforced(accessFlag);
                }
            } catch (WTException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }

    }

    /**
     * 预览电子签名方法入口
     *
     * @param pbo
     * @param self
     * @throws Exception
     */
    public static void writePreReviewtoPDF2(Object pbo, Object self) throws Exception {
        log.debug("-------starting  sign    pbo:" + pbo + "   self:" + self);
        if (!SERVER) {
            Class[] cla = {Object.class, Object.class};
            Object[] objs = {pbo, self};
            try {
                RemoteMethodServer.getDefault().invoke("writeReviewtoPDF2", FilePrintUtil.class.getName(), null, cla,
                        objs);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            //获取签名信息
            Hashtable hashtable = PrintHelper.getPrePrintInfo(pbo, self);

            // dwg签名
            CADSignUpload.run(pbo, hashtable);

            //删除签名文件
            Vector reviewList2 = new Vector();
            for (Enumeration enum1 = hashtable.keys(); enum1.hasMoreElements(); ) {
                String s = (String) enum1.nextElement();
                WTObject obj = (WTObject) getObject(s);
                reviewList2.add(obj);
            }
            removePrintAttachement(reviewList2);

            log.debug("-------hashtable:" + hashtable);
            // 写电子签名
            FilePrintUtil2.writeProcessReviewtoPDF(hashtable, pbo);

        }

    }

    /**
     * 装填预签审数据,用于签审预览
     *
     * @return
     */
    public static Hashtable getPrePrintInfo() {
        Hashtable wfInfoTable = new Hashtable();
        wfInfoTable.put("SHEJIZHESHIJIAN", "编制xx/部门xx/xxxx-xx-xx");
        wfInfoTable.put("SHEJI", "编制xx/部门xx/xxxx-xx-xx");
        wfInfoTable.put("SHEJISHIJIAN", "xxxx-xx-xx");

        wfInfoTable.put("JIAODUIZHESHIJIAN", "校对xx/部门xx/xxxx-xx-xx");
        wfInfoTable.put("JIAODUI", "校对xx/部门xx/xxxx-xx-xx");
        wfInfoTable.put("JIAODUISHIJIAN", "xxxx-xx-xx");

        wfInfoTable.put("SHENHEZHESHIJIAN", "审核xx/部门xx/xxxx-xx-xx");
        wfInfoTable.put("SHENHE", "审核xx/部门xx/xxxx-xx-xx");
        wfInfoTable.put("SHENHESHIJIAN", "xxxx-xx-xx");

        wfInfoTable.put("HUIQIAN1ZHESHIJIAN", "内部会签xx/部门xx/xxxx-xx-xx");

        wfInfoTable.put("HUIQIAN2ZHESHIJIAN", "外被会签1/部门1/xxxx-xx-xx;外被会签2/部门2/xxxx-xx-xx");

        wfInfoTable.put("BIAOSHENZHESHIJIAN", "标审xx/部门xx/xxxx-xx-xx");
        wfInfoTable.put("BIAOSHEN", "标审xx/部门xx/xxxx-xx-xx");
        wfInfoTable.put("BIAOSHENSHIJIAN", "xxxx-xx-xx");

        wfInfoTable.put("PIZHUNZHESHIJIAN", "批准xx/部门xx/xxxx-xx-xx");
        wfInfoTable.put("PIZHUN", "批准xx/部门xx/xxxx-xx-xx");
        wfInfoTable.put("PIZHUNSHIJIAN", "xxxx-xx-xx");

        // 合并外部会签和内部会签到会签中，key为“HUIQIANZHESHIJIAN”
        if (wfInfoTable.get("HUIQIAN1ZHESHIJIAN") != null
                || wfInfoTable.get("HUIQIAN2ZHESHIJIAN") != null) {
            String huiqianResult = "";
            String huiqian1Str = (String) wfInfoTable.get("HUIQIAN1ZHESHIJIAN");
            if (huiqian1Str != null && huiqian1Str.trim().length() > 0) {
                huiqianResult = huiqian1Str;
            }
            String huiqian2Str = (String) wfInfoTable.get("HUIQIAN2ZHESHIJIAN");
            if (huiqian2Str != null && huiqian2Str.trim().length() > 0) {
                if (huiqianResult.equals("")) {
                    huiqianResult = huiqian2Str;
                } else {
                    huiqianResult = huiqianResult + ";" + huiqian2Str;
                }
            }
            wfInfoTable.put("HUIQIANZHESHIJIAN", huiqianResult);
            // MULTIPLE_HUIQIANZHESHIJIAN
            if (huiqianResult.contains(";")) {
                wfInfoTable.put("MULTIPLE_HUIQIANZHESHIJIAN", huiqianResult);
            }
        }
        return wfInfoTable;
    }

    /**
     * @param hashtable 存放签审对象，签审活动的参与人和完成时间 Hashtable<WTObject,Hashtable>
     * @param overWirte true:移除原来的附件
     * @throws WTException
     * @throws MissingResourceException
     * @throws UnsupportedEncodingException
     */
    public static void writeReviewtoPDF(Hashtable hashtable) throws UnsupportedEncodingException,
            MissingResourceException, WTException {
        String overWriteString = getStrFromProperties("fileprint.overWrite", filePrintProperties);
        boolean overWrite = false;
        if (overWriteString.equals("true"))
            overWrite = true;
        int listSize = hashtable.size();
        // 文档签审列表为空时，返回""
        if (listSize <= 0)
            return;
        String ibaname = "";
        String signTemplate = "";
        Vector allinfo = new Vector();
        int outputpage;
        String direction;
        float fontsizevalue;
        Vector reviewList = new Vector();
        for (Enumeration enum2 = hashtable.keys(); enum2.hasMoreElements(); ) {
            String s = (String) enum2.nextElement();
            WTObject obj = (WTObject) getObject(s);
            Hashtable signHashtable = (Hashtable) hashtable.get(s);
            // 如果是EPM需要获取其文档类型，如果是装配件，不需要签名
            if (obj instanceof EPMDocument) {
                String epmdoctype = ((EPMDocument) obj).getDocType().toString();
                if (!epmdoctype.equalsIgnoreCase("CADASSEMBLY"))
                    ;
                writeReviewtoPDF4Doc(obj, signHashtable, overWrite, "FORMAT");
            } else if (obj instanceof WTDocument) {
                try {
                    String type = IBAHelper.getSoftType(obj);
                    if (type.equalsIgnoreCase("DRAWING_DOC")) {
                        writeReviewtoPDF4Doc(obj, signHashtable, overWrite, "FORMAT");
                    } else if (type.equalsIgnoreCase("TECHNOTICE_DOC")) {
                        writeReviewtoPDF4Doc(obj, signHashtable, overWrite, "FORMAT4");
                    } else {
                        writeReviewtoPDF4Doc(obj, signHashtable, overWrite, "FORMAT1");
                    }
                } catch (WTException e) {
                }
            } else if (obj instanceof WTChangeOrder2) {
                writeReviewtoPDF4ChangeOrder(obj, signHashtable, overWrite);
            }
        }
    }

    /**
     * @param hashtable 存放签审对象，签审活动的参与人和完成时间 Hashtable<WTObject,Hashtable>
     * @param overWirte true:移除原来的附件
     * @throws WTException
     * @throws MissingResourceException
     * @throws UnsupportedEncodingException
     */
    public static void writeReviewtoPDF(Hashtable hashtable, String processType) throws UnsupportedEncodingException,
            MissingResourceException, WTException {
        String overWriteString = getStrFromProperties("fileprint.overWrite", filePrintProperties);
        boolean overWrite = false;
        if (overWriteString.equals("true"))
            overWrite = true;
        int listSize = hashtable.size();
        // 文档签审列表为空时，返回""
        if (listSize <= 0)
            return;
        String ibaname = "";
        String signTemplate = "";
        Vector allinfo = new Vector();
        int outputpage;
        String direction;
        float fontsizevalue;
        Vector reviewList = new Vector();
        for (Enumeration enum2 = hashtable.keys(); enum2.hasMoreElements(); ) {
            String s = (String) enum2.nextElement();
            WTObject obj = (WTObject) getObject(s);
            Hashtable signHashtable = (Hashtable) hashtable.get(s);
            // 如果是EPM需要获取其文档类型，如果是装配件，不需要签名
            if (obj instanceof EPMDocument) {
                String epmdoctype = ((EPMDocument) obj).getDocType().toString();
                if (!epmdoctype.equalsIgnoreCase("CADASSEMBLY")) {
                    if (processType.equalsIgnoreCase("ZJD"))
                        writeReviewtoPDF4Doc(obj, signHashtable, overWrite, "FORMATZJD", processType);
                    else writeReviewtoPDF4Doc(obj, signHashtable, overWrite, "FORMAT");
                }
            } else if (obj instanceof WTDocument) {
                try {
                    String type = IBAHelper.getSoftType(obj);
                    if (type.equalsIgnoreCase("DRAWING_DOC")) {
                        if (processType.equalsIgnoreCase("ZJD"))
                            writeReviewtoPDF4Doc(obj, signHashtable, overWrite, "FORMATZJD", processType);
                        else writeReviewtoPDF4Doc(obj, signHashtable, overWrite, "FORMAT");
                    } else if (type.equalsIgnoreCase("TECHNOTICE_DOC")) {
                        writeReviewtoPDF4Doc(obj, signHashtable, overWrite, "FORMAT4");
                    } else {
                        if (processType.equalsIgnoreCase("ZJD"))
                            writeReviewtoPDF4Doc(obj, signHashtable, overWrite, "FORMATZJD1", processType);
                        else writeReviewtoPDF4Doc(obj, signHashtable, overWrite, "FORMAT1");
                    }
                } catch (WTException e) {
                }
            } else if (obj instanceof WTChangeOrder2) {
                writeReviewtoPDF4ChangeOrder(obj, signHashtable, overWrite);
            }
        }
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

    /**
     * @param reviewList
     * @param hashtable  存放签审信息，活动，参与人和完成时间
     * @param overWirte  true:移除原来的附件
     */
    public static void writeReviewtoPDF(Vector reviewList, Hashtable hashtable, boolean overWrite) {
        int listSize = reviewList.size();
        // 文档签审列表为空时，返回""
        if (listSize <= 0)
            return;
        String ibaname = "";
        String signTemplate = "";
        Vector allinfo = new Vector();
        int outputpage;
        String direction;
        float fontsizevalue;
        Vector repVector = addRepToAttachment(reviewList);
        for (int j = 0; j < reviewList.size(); j++) {
            WTObject obj = (WTObject) reviewList.elementAt(j);
            // 如果是EPM需要获取其文档类型，如果是装配件，不需要签名
            if (obj instanceof EPMDocument) {
                String epmdoctype = ((EPMDocument) obj).getDocType().toString();
                if (!epmdoctype.equalsIgnoreCase("CADASSEMBLY"))
                    ;
                writeReviewtoPDF4Doc(obj, hashtable, overWrite, "FORMAT");
            } else if (obj instanceof WTDocument) {
                try {
                    String type = IBAHelper.getSoftType(obj);
                    if (type.equalsIgnoreCase("DRAWING_DOC")) {
                        writeReviewtoPDF4Doc(obj, hashtable, overWrite, "FORMAT");
                    } else if (type.equalsIgnoreCase("TECHNOTICE_DOC")) {
                        writeReviewtoPDF4Doc(obj, hashtable, overWrite, "FORMAT4");
                    } else {
                        writeReviewtoPDF4Doc(obj, hashtable, overWrite, "FORMAT1");
                    }
                } catch (WTException e) {
                }
            } else if (obj instanceof WTChangeOrder2) {
                writeReviewtoPDF4ChangeOrder(obj, hashtable, overWrite);
            }
        }
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
                key = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".ibaattribute",
                        propertiesfile);
                // 获得key值
                if ((key == null) || (key.length() == 0))
                    continue;
                else {
                    value = (String) hashtable.get(key);// 获得签审信息，要写入PDF的内容
                    if (value != null && value.length() > 0) {
                        // 获得坐标
                        String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i)
                                + ".x", propertiesfile);
                        String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i)
                                + ".y", propertiesfile);
                        log.debug("key is:" + key + ",value is " + value + ",x is:" + locationx + ",y is" + locationy);
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
                    String fileDir = codebaseLocation + File.separator + "ext" + File.separator + "ases"
                            + File.separator + "temp" + File.separator + "writepdf";

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
                    File printFile = PdfUtilities.writeToPDF(finaleTargetFileName, finalePrintFileName, allinfo,
                            outputpage, direction, fontsizevalue, format);
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

    public static void writeReviewtoPDF4Doc(WTObject obj, Hashtable hashtable, boolean overWrite, String format,
                                            String processType) {
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
                key = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".ibaattribute",
                        propertiesfile);
                // 获得key值
                if ((key == null) || (key.length() == 0))
                    continue;
                else {
                    value = (String) hashtable.get(key);// 获得签审信息，要写入PDF的内容
                    if (value != null && value.length() > 0) {
                        // 获得坐标
                        String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i)
                                + ".x", propertiesfile);
                        String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i)
                                + ".y", propertiesfile);
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
            // 获得上一版本的最新小版本
            WTObject wtobj = getPreVersionWTObj(obj);
            wt.content.ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) wtobj);
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
                    String fileDir = codebaseLocation + File.separator + "ext" + File.separator + "ases"
                            + File.separator + "temp" + File.separator + "writepdf";

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
                    File printFile = PdfUtilities.writeToPDF(finaleTargetFileName, finalePrintFileName, allinfo,
                            outputpage, direction, fontsizevalue, format);
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
            wte.printStackTrace();
        } catch (UnsupportedEncodingException uee) {
            uee.printStackTrace();
        } catch (WTPropertyVetoException wpve) {
            wpve.printStackTrace();
        } catch (PropertyVetoException pve) {
            pve.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void writeReviewtoPDF4ChangeOrder(WTObject obj, Hashtable hashtable, boolean overWrite) {
        String signTemplate = "";
        Vector allinfo = new Vector();
        int outputpage;
        String direction;
        float fontsizevalue;
        try {
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
                if ((fileName.indexOf(".pdf") > -1) && (fileName.indexOf(prefix) < 0)) {
                    String codebaseLocation = wtProperties.getProperty("wt.codebase.location");
                    String fileDir = codebaseLocation + File.separator + "ext" + File.separator + "ases"
                            + File.separator + "temp" + File.separator + "writepdf";
                    File file = new File(fileDir);
                    if (!file.exists()) {
                        log.debug("...writeIBAtoPDF 建立目录 " + fileDir);
                        file.mkdirs();
                    }

                    String printFileName = prefix + "_" + ((WTChangeOrder2) obj).getNumber() + "_"
                            + ((WTChangeOrder2) obj).getName() + "_" + fileName;
                    InputStream inputstream = ContentServerHelper.service.findContentStream(applicationdata);
                    String finaleTargetFileName = fileDir + File.separator + fileName;
                    String finalePrintFileName = fileDir + File.separator + printFileName;
                    File tempFile = new File(finaleTargetFileName);
                    String fileAbsolutePath = tempFile.getAbsolutePath();
                    FileOutputStream tout = new FileOutputStream(fileAbsolutePath);
                    byte abyte1[] = new byte[2048];
                    int k;
                    while ((k = inputstream.read(abyte1, 0, abyte1.length)) >= 0)
                        tout.write(abyte1, 0, k);
                    tout.close();
                    String extention = Util.getExtension(finaleTargetFileName);
                    String removeExtention = Util.removeExtension(finaleTargetFileName);
                    File file1 = new File(finaleTargetFileName);
                    PdfReader reader = new PdfReader(finaleTargetFileName);
                    Rectangle pageSize = reader.getPageSize(1);// 595.92X842.0
                    float width = pageSize.getWidth();
                    // A0:3370,A1:2384,A2:1684,A3:1190,A4:595
                    String format = "";
                    if (width > 841F && width < 842F) // 变更单是横向打印
                        format = "FORMAT2";
                    else format = "FORMAT3";
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
                        key = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i) + ".ibaattribute",
                                propertiesfile);
                        // 获得key值
                        if ((key == null) || (key.length() == 0))
                            continue;
                        else {
                            value = (String) hashtable.get(key);// 获得签审信息，要写入PDF的内容
                        }

                        if (value != null && value.length() > 0) {
                            // 获得坐标
                            String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i)
                                    + ".x", propertiesfile);
                            String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i)
                                    + ".y", propertiesfile);
                            log.debug("locationx is:" + locationx);
                            log.debug("locationy is:" + locationy);
                            // 放入hashtable
                            ibas.put(ATTR, key);
                            ibas.put(ATTRVALUE, value);
                            ibas.put(LOCATIONX, locationx);
                            ibas.put(LOCATIONY, locationy);
                            allinfo.add(ibas);
                        }
                    }

                    // 向pdf写签审信息
                    File printFile = PdfUtilities.writeToPDFForECN(finaleTargetFileName, finalePrintFileName, allinfo,
                            outputpage, direction, fontsizevalue);

                    // 上载打印稿pdf为附件
                    if (printFile != null && printFileName != null) {
                        saveFiletoAttachment4ChangeOrder((ContentHolder) obj, printFile.getAbsolutePath(), overWrite,
                                printFileName);
                        // 删除源PDF文件
                        if (overWrite && finaleTargetFileName != null && finaleTargetFileName.length() > 0) {
                            removeAttachment4ChangeOrder((ContentHolder) obj, finaleTargetFileName);
                        }
                    }
                    // 清除临时文件
                    cleanTempFile(fileDir);
                    break;
                }
            }
            allinfo.clear();
        } catch (WTException wte) {
            wte.printStackTrace();
        } catch (java.io.UnsupportedEncodingException uee) {
            uee.printStackTrace();
        } catch (WTPropertyVetoException wpve) {
            wpve.printStackTrace();
        } catch (PropertyVetoException pve) {
            pve.printStackTrace();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * 根据对象类型获得签审模板。
     * 图档类型的只处理了AutoCAD的。
     * EPM处理了proE和AotuCAD的。
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
                tufuIBAName = getStrFromProperties("WTDocument.tufuAttribute", "ext.ases.fileprint.fileprint");
                type = "AUTOCAD";
            } else if (obj instanceof EPMDocument) {
                EPMDocument epm = (EPMDocument) obj;
                String app = epm.getAuthoringApplication().toString();

                tufuIBAName = getStrFromProperties("EPMDocument.tufuAttribute", "ext.ases.fileprint.fileprint");

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
            signTemplate = getStrFromProperties(typeAndTufu + ".frm", "ext.ases.fileprint.fileprint");

            if (signTemplate != null && (signTemplate.length() > 0))
                result = signTemplate;
        } catch (WTException wte) {
            wte.printStackTrace();
        } catch (java.io.UnsupportedEncodingException uee) {
            uee.printStackTrace();
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
                else qualityfilename = realdockey;
            }

            if (realfilename != null && realfilename.length() > 0) {
                if (qualityfilename.length() > 0)
                    qualityfilename = qualityfilename + separator + realfilename;
                else qualityfilename = realfilename;
            }
        } catch (WTException wte) {
            System.out.println(wte);
        } catch (java.io.UnsupportedEncodingException uee) {
            System.out.println(uee);
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
            wte.printStackTrace();
        } catch (PropertyVetoException pve) {
            pve.printStackTrace();
        }

        return result;
    }

    public static String getStrFromProperties(String key, String propertiefile)
            throws WTException, UnsupportedEncodingException, MissingResourceException {

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

    public static void saveFiletoAttachment4ChangeOrder(ContentHolder contentholder, String filename, boolean flag,
                                                        String targetFileName)
            throws WTException, WTPropertyVetoException, PropertyVetoException, IOException {
        if (flag && targetFileName != null && targetFileName.length() > 0) {
            removeAttachment4ChangeOrder(contentholder, targetFileName);
        }

        ApplicationData appData = ApplicationData.newApplicationData(contentholder);
        appData.setRole(ContentRoleType.SECONDARY);
        ContentServerHelper.service.updateContent(contentholder, appData, filename);
    }

    public static ContentHolder removeAttachment4ChangeOrder(ContentHolder contentholder, String attachName)
            throws WTException, PropertyVetoException {
        try {
            wt.content.ContentHolder changeOrder = ContentHelper.service.getContents(contentholder);
            Vector apps = ContentHelper.getApplicationData(changeOrder);

            for (Enumeration e = apps.elements(); e.hasMoreElements(); ) {
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

    public static void saveFiletoAttachment(ContentHolder contentholder, FileInputStream fileinputstream)
            throws WTException, WTPropertyVetoException, PropertyVetoException, IOException {
        ApplicationData appData = ApplicationData.newApplicationData(contentholder);
        appData.setRole(ContentRoleType.SECONDARY);
        ContentServerHelper.service.updateContent(contentholder, appData, fileinputstream);
    }

    public static void saveFiletoAttachment(ContentHolder contentholder, String filename, boolean flag,
                                            String targetFileName)
            throws WTException, WTPropertyVetoException, PropertyVetoException, IOException {
        if (flag && targetFileName != null && targetFileName.length() > 0) {
            removeAttachment((Representable) contentholder, targetFileName);
        }

        ApplicationData appData = ApplicationData.newApplicationData(contentholder);
        appData.setRole(ContentRoleType.SECONDARY);
        ContentServerHelper.service.updateContent(contentholder, appData, filename);
    }

    public static Representable removeAttachment(Representable representable, String attachName) throws WTException,
            PropertyVetoException {
        try {
            wt.content.ContentHolder doc = ContentHelper.service.getContents(representable);
            Vector apps = ContentHelper.getApplicationData(doc);

            for (Enumeration e = apps.elements(); e.hasMoreElements(); ) {
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
     * 将可视化添加为附件，用于电子签名
     * 此为通用方法
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
                    RevisionControlled rc = (RevisionControlled) obj;
                    String version = rc.getVersionIdentifier().getValue();

                    //只有附件中无对应版本的pdf模板，才从表示法中把pdf文件拿出来存在附件中
                	/*String primaryfileName = getPrimaryFileName((FormatContentHolder) obj);
                	String targetFileName = getQualityFileName((FormatContentHolder) obj, primaryfileName, SOURCEKEY);
                    targetFileName = Util.removeExtension(targetFileName) +"_"+ version+".pdf";
                    ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) obj);
                    Vector apps = ContentHelper.getApplicationData(contentHolder);
                    boolean flag = true;
                    for (int j = 0; j < apps.size(); j++) {
                        ApplicationData applicationdata;
                        applicationdata = (ApplicationData) apps.elementAt(j);
                        String fileName = applicationdata.getFileName();
                        String applicationdataRole = applicationdata.getRole().toString();
                        log.debug("--------applicationdataRole:" + applicationdataRole);
                        if (!applicationdataRole.equalsIgnoreCase("SECONDARY")) {
                            continue;// 不是附件，处理下一个
                        }
                        if (targetFileName.equals(fileName)) {
                        	flag = false;
                        }
                	}
                    // 获得文档对象的表示法对象
                    Representation representation = RepresentationHelper.service.getDefaultRepresentation((Representable) obj);
                    if(flag){
                        QueryResult qr2 = ContentHelper.service.getContentsByRole(representation, ContentRoleType.SECONDARY);
                        while (qr2.hasMoreElements()) {
                            ApplicationData applicationdata = (ApplicationData) qr2.nextElement();
                            String name = applicationdata.getFileName();

                            if ("PDF".equalsIgnoreCase(applicationdata.getFormat().getDataFormat().getFormatName().trim())) {
                            	boolean enforce = SessionServerHelper.manager
                                .setAccessEnforced(false);
                        		 Transaction tx = new Transaction();
                                 tx.start();
                                 PersistenceHelper.manager.lockAndRefresh(contentHolder);
                                 ApplicationData appData = ApplicationData.newApplicationData(contentHolder);
                                 appData.setFileName(Util.removeExtension(name)+"_"+version+".pdf");
                                 appData.setRole(ContentRoleType.SECONDARY);
                                 appData.setDescription(String.valueOf(Calendar.getInstance()
                                         .getTimeInMillis()));
                                 appData.setComments("自动生成");
                                 InputStream inputstream = ContentServerHelper.service.findContentStream(applicationdata);
                                 appData = ContentServerHelper.service.updateContent(contentHolder, appData,inputstream);
                                 tx.commit();
                                 tx = null;
                                 PersistenceHelper.manager.refresh(contentHolder);
                                 SessionServerHelper.manager.setAccessEnforced(enforce);
                            }
                        }
                    }

                    */

                    // 获得文档对象的表示法对象
                 /* Representation representation = RepresentationHelper.service.getDefaultRepresentation((Representable) obj);
                  if(representation!=null){
	                  QueryResult qr2 = ContentHelper.service.getContentsByRole(representation, ContentRoleType.SECONDARY);
	                  while (qr2.hasMoreElements()) {
	                      ApplicationData applicationdata = (ApplicationData) qr2.nextElement();
	                      String name = applicationdata.getFileName();

	                      if ("PDF".equalsIgnoreCase(applicationdata.getFormat().getDataFormat().getFormatName().trim())) {
	                    	  if(!"已签名".equals(applicationdata.getComments())){
	                    		  WVSHelper.service.repToAttachment((Representable) obj, true);
	                    	  } else{
	                    		  String primaryfileName = getPrimaryFileName((FormatContentHolder) obj);
	                              // 获得需要处理的附件文件名
	                              String targetFileName = getQualityFileName((FormatContentHolder) obj, primaryfileName, SOURCEKEY);
	                              targetFileName = Util.removeExtension(targetFileName) + ".pdf";
	                              ContentHolder contentHolder = ContentHelper.service.getContents((ContentHolder) obj);
	                              Vector apps = ContentHelper.getApplicationData(contentHolder);
	                              boolean flag = true;
	                              for (int j = 0; j < apps.size(); j++) {
	                                  ApplicationData data;
	                                  data = (ApplicationData) apps.elementAt(j);
	                                  String fileName = data.getFileName();
	                                  // 应用数据的角色
	                                  String applicationdataRole = data.getRole().toString();
	                                  log.debug("--------applicationdataRole:" + applicationdataRole);
	                                  // 不是附件，处理下一个applicationdata. AutoCAD2006 检入的主文件角色是 "THUMBNAIL"
	                                  if (!applicationdataRole.equalsIgnoreCase("SECONDARY")) {
	                                      continue;// 不是附件，处理下一个
	                                  }
	                                  if (targetFileName.equals(fileName)) {
	                                	  flag = false;
	                                  }
	                              }
	                              if()

	                    	  }
	                      }
	                  }
                  }*/
                    // 把表示法存为附件
                    // 获得文档对象的表示法对象
                    Representation representation = RepresentationHelper.service.getDefaultRepresentation((Representable) obj);
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
            } else returnedVector = null;
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
     * 将可视化添加为附件，用于电子签名
     * 由于转阶段的处理方法不同，此方法增加变量processType，如果processType是ZJD （转阶段），则不再添加附件
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
            } else returnedVector = null;
        } catch (WTException wte) {
            wte.printStackTrace();
        } catch (java.beans.PropertyVetoException pve) {
            pve.printStackTrace();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }
        return returnedVector;
    }

    public static WTObject getPreVersionWTObj(WTObject obj)
            throws WTException {
        WTObject preVersionObj = null;
        // 得到小版本
        String curVersion = getReversionNumber(obj);
        // 得到大版本
        String Version = getVersionNumber(obj);
        boolean notGet = true;
        try {
            // 得到所有零部件的所有大版本的最新小版本
            QueryResult allIterations = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
            if (allIterations != null)
                while (allIterations.hasMoreElements() && notGet) {
                    preVersionObj = (WTObject) allIterations.nextElement();
                    String theVersion = VersionControlHelper.getVersionIdentifier((Versioned) preVersionObj).getValue();
                    // 如果是当前的大版本，跳出
                    if (theVersion.equalsIgnoreCase(Version))
                        continue;
                    else {
                        notGet = false;
                    }
                }
        } catch (WTException wte) {
            wte.printStackTrace();
        }
        return preVersionObj;
    }

    public static String getReversionNumber(WTObject obj)
            throws WTException {
        String iteration = "";
        try {
            iteration = VersionControlHelper.getIterationIdentifier((Iterated) obj).getValue();
        } catch (VersionControlException e) {
            e.printStackTrace();
        }
        return iteration;
    }

    public static String getVersionNumber(WTObject obj)
            throws WTException {
        String version = "";
        try {
            version = VersionControlHelper.getVersionIdentifier((Versioned) obj).getValue();
        } catch (VersionControlException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 删除签名文件方法入口
     *
     * @param pbo
     * @param self
     * @throws Exception
     */
    public static void removePrintAttachement(WTObject pbo, Object self) throws Exception {
        Hashtable hashtable = PrintHelper.getPrintInfo(pbo, self);
        java.util.Vector reviewList = new java.util.Vector();
        for (java.util.Enumeration enum1 = hashtable.keys(); enum1.hasMoreElements(); ) {
            java.lang.String s = (java.lang.String) enum1.nextElement();
            wt.fc.WTObject obj = (wt.fc.WTObject) FilePrintUtil.getObject(s);
            reviewList.add(obj);
        }
        FilePrintUtil.removePrintAttachement(reviewList);
    }

    /**
     * 删除电子签名PDF附件
     *
     * @param reviewList
     * @throws WTException
     */
    public static void removePrintAttachement(Vector reviewList) throws WTException {

        if (reviewList.size() <= 0)
            return;
        try {
            for (int i = 0; i < reviewList.size(); i++) {
                WTObject obj = (WTObject) reviewList.elementAt(i);
                if ((obj instanceof WTDocument) || (obj instanceof EPMDocument) || (obj instanceof WTChangeOrder2)) {
                    removePrintAttachement((ContentHolder) obj);
                }
            }
        } catch (Exception e) {
            throw new WTException(e);
        }
    }

    /**
     * 删除电子签名PDF附件
     *
     * @param holder
     * @throws WTException
     */
    public static void removePrintAttachement(ContentHolder holder) throws WTException {
        try {
            holder = ContentHelper.service.getContents(holder);
            Vector apps = ContentHelper.getApplicationData(holder);

            for (Enumeration e = apps.elements(); e.hasMoreElements(); ) {
                ApplicationData contentItem = (ApplicationData) e.nextElement();
                String applicationdataRole = contentItem.getRole().toString();
                log.debug("FileName:" + contentItem.getFileName());
                log.debug("Role:" + contentItem.getRole());
                if (!"SECONDARY".equalsIgnoreCase(contentItem.getRole().toString()))
                    continue;// 不是附件

                if (contentItem.getFileName().startsWith("Print_")) {
                    ContentServerHelper.service.deleteContent(holder, contentItem);
                }
            }
        } catch (Exception e) {
            throw new WTException(e);
        }

    }

    /**
     * 删除电子签名相关PDF附件
     *
     * @param holder
     * @throws WTException
     */
    public static void removePrintRelatedAttachement(WTObject o) throws WTException {
        try {
            if (o instanceof ContentHolder) {
                ContentHolder holder = (ContentHolder) o;
                holder = ContentHelper.service.getContents(holder);
                Vector apps = ContentHelper.getApplicationData(holder);

                for (Enumeration e = apps.elements(); e.hasMoreElements(); ) {
                    ApplicationData contentItem = (ApplicationData) e.nextElement();
                    String applicationdataRole = contentItem.getRole().toString();
                    log.debug("FileName:" + contentItem.getFileName());
                    log.debug("Role:" + contentItem.getRole());
                    if (!"SECONDARY".equalsIgnoreCase(contentItem.getRole().toString()))
                        continue;// 不是附件

                    if (contentItem.getFileName().startsWith("Print_")
                            || contentItem.getFileName().startsWith("ForPrint")) {
                        ContentServerHelper.service.deleteContent(holder, contentItem);
                    }
                }
            }
        } catch (Exception e) {
            throw new WTException(e);
        }

    }

    public static String prePrint(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = PersistenceHelper.getObjectIdentifier(pbo).toString();
            String link = HTML_LINK_PRE_PRINT + objOid
                    + "&processOid=" + processOid + HTML_LINK_BLANK_MID
                    + "签名预览" + HTML_LINK_AFTER;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String downloadAttachmentForPrint(WTObject obj)
            throws IOException, WTException, PropertyVetoException {
        // WTDocument doc = (WTDocument) obj;
        try {
            SessionServerHelper.manager.setAccessEnforced(false);
            String tempDir = WTProperties.getLocalProperties().getProperty(
                    "wt.temp");
            ContentHolder contentholder = ContentHelper.service
                    .getContents((ContentHolder) obj);
            Vector v = ContentHelper.getContentList(contentholder);
            String fileName = "";
            if (obj instanceof WTDocument) {
                fileName = ((WTDocument) obj).getName() + ".zip";
            }
            if (obj instanceof WTChangeOrder2) {
                fileName = ((WTChangeOrder2) obj).getName() + ".zip";
                WTObject targetObj = PrintHelper.getReleatedDocByECN((WTChangeOrder2) obj);
                if (targetObj != null) {
                    ContentHolder contentholder2 = ContentHelper.service
                            .getContents((ContentHolder) targetObj);
                    v.addAll(ContentHelper.getContentList(contentholder2));
                }
            }
            if (fileName.equals("")) {
                fileName = "obj.zip";
            }
            fileName = fileName.replaceAll("/", "_");
            File zipFile = new File(tempDir + File.separator + fileName);
            ZipOutputStream zos = new ZipOutputStream(zipFile);
            for (Object o : v) {
                if (o instanceof ApplicationData) {
                    ApplicationData ad = (ApplicationData) o;
                    // String fileName = applicationdata.getFileName();
                    if (ad.getFileName().startsWith("Print_")) {
                        InputStream is = ContentServerHelper.service
                                .findContentStream(ad);
                        // FileOutputStream fos = new
                        // FileOutputStream(tempFile);
                        byte[] buf = new byte[1024];
                        // int byteread = 0;
                        // while ((byteread = is.read(buffer)) != -1) {
                        // fos.write(buffer, 0, byteread);
                        // }
                        // is.close();
                        // fos.close();
                        zos.putNextEntry(new ZipEntry(ad.getFileName()));
                        zos.setEncoding("gbk");
                        int len = 0;
                        while ((len = is.read(buf)) >= 0) {
                            zos.write(buf, 0, len);
                        }
                        is.close();
                    }
                }
            }
            zos.close();
            return tempDir + File.separator + fileName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(true);
        }
        return "";
    }

    public static String getPrintApplyDocDownloadLink(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = PersistenceHelper.getObjectIdentifier(pbo).toString();
            String link = HTML_DOWNLOAD_PRINT_APPLY_LINK_PRE + objOid
                    + "&processOid=" + processOid + HTML_LINK_BLANK_MID
                    + "下载文件" + HTML_LINK_AFTER;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSignLink(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = PersistenceHelper.getObjectIdentifier(pbo).toString();
            String link = HTML_LINK_SIGN_PRINT + objOid
                    + "&processOid=" + processOid + HTML_LINK_BLANK_MID
                    + "电子签名" + HTML_LINK_AFTER;
            return link;
        } catch (WTException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 设置流程变量
     *
     * @param process
     * @throws Exception
     */
    public static void setSignFlag(WfProcess process) throws Exception {
        SessionServerHelper.manager.setAccessEnforced(false);
        try {
            ProcessData data = process.getContext();
            data.setValue("hasSign", true);
            PersistenceHelper.manager.save(process);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            SessionServerHelper.manager.setAccessEnforced(true);
        }
    }

    /**
     * 获取流程变量
     *
     * @param process
     * @return
     * @throws Exception
     */
    public static boolean getSignFlag(WfProcess process) throws Exception {
        boolean flag = false;
        SessionServerHelper.manager.setAccessEnforced(false);
        try {
            ProcessData data = process.getContext();
            flag = (Boolean) data.getValue("hasSign");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            SessionServerHelper.manager.setAccessEnforced(true);
        }
        return flag;
    }

    public static String ecnPrint(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = PersistenceHelper.getObjectIdentifier(pbo).toString();
            String link = HTML_LINK_ECN_PRINT + objOid
                    + "&processOid=" + processOid + HTML_LINK_BLANK_MID
                    + "PDF预览" + HTML_LINK_AFTER;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getOpenLink(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
            String link = HTML_LINK_PRINT_APPLY + objOid
                    + "&processOid=" + processOid + "&type=YLDYSQ" + HTML_LINK_BLANK_MID
                    + "预览打印" + HTML_LINK_AFTER;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String doPrint(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
            String link = HTML_LINK_PRINT_APPLY + objOid
                    + "&processOid=" + processOid + "&type=ZXDYSQ" + HTML_LINK_BLANK_MID
                    + "执行打印" + HTML_LINK_AFTER;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String doPrint(WTObject pbo, ObjectReference self, String dept) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        String wfaOid = PersistenceHelper.getObjectIdentifier(wfa).toString();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
            String link = HTML_LINK_PRINT_APPLY + objOid
                    + "&processOid=" + processOid + "&dept=" + dept + "&wfaOid=" + wfaOid + "&type=ZXDYSQ" + HTML_LINK_BLANK_MID
                    + "执行打印" + HTML_LINK_AFTER;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String doPrintForOffSet(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
            String link = HTML_LINK_PRINT_APPLY + objOid
                    + "&processOid=" + processOid + "&type=ZXDYSQ_BD" + HTML_LINK_BLANK_MID
                    + "执行补打" + HTML_LINK_AFTER;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getPaperFile(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
            String link = HTML_LINK_PRINT_APPLY + objOid
                    + "&processOid=" + processOid + "&type=LQZZWJ" + HTML_LINK_BLANK_MID
                    + "领取纸质文件" + HTML_LINK_AFTER;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getPaperFileForOffSet(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
            String link = HTML_LINK_PRINT_APPLY + objOid
                    + "&processOid=" + processOid + "&type=LQZZWJ_BD" + HTML_LINK_BLANK_MID
                    + "领取纸质文件" + HTML_LINK_AFTER;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getChangeInfo(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
            String link = HTML_LINK_PRINT_APPLY + objOid
                    + "&processOid=" + processOid + "&type=WJDYSQ" + HTML_LINK_BLANK_MID
                    + "修改打印申请" + HTML_LINK_AFTER;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getChangeInfo(WTObject pbo, ObjectReference self, String dept) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        String wfaOid = PersistenceHelper.getObjectIdentifier(wfa).toString();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
            String link = HTML_LINK_PRINT_APPLY + objOid
                    + "&processOid=" + processOid + "&dept=" + dept + "&wfaOid="+wfaOid+"&type=WJDYSQ" + HTML_LINK_BLANK_MID
                    + "修改打印申请" + HTML_LINK_AFTER;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSealPlus(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
            String link = HTML_LINK_PRINT_APPLY + objOid
                    + "&processOid=" + processOid + "&type=JGYZQR" + HTML_LINK_BLANK_MID
                    + "加盖印章确认" + HTML_LINK_AFTER;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String checkSealPlus(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
            String link = HTML_LINK_PRINT_APPLY + objOid
                    + "&processOid=" + processOid + "&type=JGYZQR_JC" + HTML_LINK_BLANK_MID
                    + "加盖印章确认" + HTML_LINK_AFTER;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String modifyAddSeal(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
            String link = HTML_LINK_PRINT_APPLY + objOid
                    + "&processOid=" + processOid + "&type=JGYZGL" + HTML_LINK_BLANK_MID
                    + "修改加盖印章" + HTML_LINK_AFTER;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String loadSealPlus(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
            String link = HTML_LINK_PRINT_APPLY + objOid
                    + "&processOid=" + processOid + "&type=JGYZQR_SJ" + HTML_LINK_BLANK_MID
                    + "收集纸质文件" + HTML_LINK_AFTER;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getPaperSeal(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
            String link = HTML_LINK_PRINT_APPLY + objOid
                    + "&processOid=" + processOid + "&type=JGYZQR_LQ" + HTML_LINK_BLANK_MID
                    + "领取纸质文件" + HTML_LINK_AFTER;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String transferFile(WTObject pbo, ObjectReference self) {
        WfAssignedActivity wfa = (WfAssignedActivity) self.getObject();
        try {
            WfProcess proc = wfa.getParentProcess();
            String processOid = PersistenceHelper.getObjectIdentifier(proc).toString();
            String objOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
            String link = HTML_LINK_PRINT_APPLY + objOid
                    + "&processOid=" + processOid + "&type=WJZYLB" + HTML_LINK_BLANK_MID
                    + "转移文件列表" + HTML_LINK_AFTER;
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
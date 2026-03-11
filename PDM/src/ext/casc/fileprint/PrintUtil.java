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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.Vector;

import org.apache.log4j.Logger;

import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMStructureHelper;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.collections.WTCollection;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pds.oracle81.OracleDataSource;
import wt.representation.Representable;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WorkItem;

import com.glaway.mpm.print.constants.PrintServerConstants;
import com.glaway.mpm.util.IBAHelper;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.wvs.server.util.Util;

import ext.ases.changepackaged.ChangePackaged;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;
import ext.casc.workflow.PrintHelper;

public class PrintUtil implements RemoteAccess {

    private static final boolean SERVER = RemoteMethodServer.ServerFlag;
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

    static {
        try {
            log = LogR.getLogger(PrintUtil.class.getName());
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

    public static void writeReviewtoPDF(WTObject obj, WfProcess wf) throws Exception {
        Hashtable<String, String> hashtable = new Hashtable<String, String>();//PrintHelper.getWfInfo(obj, wf);
        writeReviewtoPDF4Doc(obj, hashtable, true, "FORMATDRWA4");
    }

    public static void writeReviewtoPDF4Doc(Object obj, Hashtable<String, String> hashtable, boolean overWrite,
            String format) {
        if (!SERVER) {
            Class[] cla = { Object.class, Hashtable.class, boolean.class, String.class };
            Object[] objs = { obj, hashtable, overWrite, format };
            try {
                RemoteMethodServer.getDefault().invoke("writeReviewtoPDF4Doc", PrintUtil.class.getName(), null, cla,
                        objs);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
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
                if (direction == null || (direction.length() == 0)) {
                    direction = "vertical";
                }

                // 获得输出文字大小
                String fontsize = getStrFromProperties(format + ".fontsize", propertiesfile);
                if (fontsize == null || (fontsize.length() == 0) || fontsize.equals("0")){
                    fontsize = "10";
                }
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
                    if ((key == null) || (key.length() == 0)) {
                        continue;
                    } else {
                        value = (String) hashtable.get(key);// 获得签审信息，要写入PDF的内容
                        if (value != null && value.length() > 0) {
                            // 获得坐标
                            String locationx = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i)
                                    + ".x", propertiesfile);
                            String locationy = getStrFromProperties(format + ".uniquevalueattr" + Integer.toString(i)
                                    + ".y", propertiesfile);
                            log.debug("key is:" + key + ",value is " + value + ",x is:" + locationx + ",y is"
                                    + locationy);
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
                    if (!applicationdataRole.equalsIgnoreCase("SECONDARY")) {
                        continue;// 不是附件，处理下一个
                    }

                    // 文件名称是要求签名的PDF附件，那么进行签名
                    // if (targetFileName.equals(fileName)) {
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
                    while ((k = inputstream.read(abyte1, 0, abyte1.length)) >= 0) {
                        tout.write(abyte1, 0, k);
                    }
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
                    // cleanTempFile(fileDir);
                    break;
                    // }
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

    public static String getPrimaryFileName(FormatContentHolder contentholder) {
        String result = "";
        if (!SERVER) {
            Class[] cla = { FormatContentHolder.class };
            Object[] obj = { contentholder };
            try {
                return (String) RemoteMethodServer.getDefault().invoke("getPrimaryFileName", PrintUtil.class.getName(),
                        null, cla, obj);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
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
        }
        return result;
    }

    public static String getQualityFileName(FormatContentHolder contentholder, String fileName, String type) {
        String qualityfilename = "";
        if (!SERVER) {
            Class[] cla = { FormatContentHolder.class, String.class, String.class };
            Object[] obj = { contentholder, fileName, type };
            try {
                return (String) RemoteMethodServer.getDefault().invoke("getQualityFileName", PrintUtil.class.getName(),
                        null, cla, obj);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
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

            try {
                separator = getStrFromProperties("PDF.separator", filePrintProperties);
                formula = getStrFromProperties(type + "PDF.formula", filePrintProperties);
                prefix = formula.substring(0, formula.indexOf(")") + 1);
                prefix = prefix.substring(prefix.indexOf("(") + 1, prefix.lastIndexOf(")"));
                prefix = getStrFromProperties(prefix, filePrintProperties);
                if (prefix.equalsIgnoreCase("null")) {// 不加前缀，配置null
                    prefix = "";
                }
                realprefix = prefix;

                formula = formula.substring(formula.indexOf(")") + 1, formula.length());
                dockey = formula.substring(0, formula.indexOf(")") + 1);
                dockey = dockey.substring(dockey.indexOf("(") + 1, dockey.lastIndexOf(")"));
                dockey = getStrFromProperties(dockey, filePrintProperties);
                if (dockey.equalsIgnoreCase("docnumber")) {
                    hasnumber = true;
                } else if (dockey.equalsIgnoreCase("docnumber_version")) {
                    hasnumberversion = true;
                } else {
                    hasnumber = false;
                    hasnumberversion = false;
                }
                if (contentholder instanceof WTDocument) {
                    WTDocument doc = (WTDocument) contentholder;
                    if (hasnumber && !hasnumberversion) {
                        realdockey = doc.getNumber();
                    }
                    if (!hasnumber && hasnumberversion) {
                        realdockey = doc.getNumber() + "_" + doc.getVersionIdentifier().getValue();
                    }
                } else if (contentholder instanceof EPMDocument) {
                    EPMDocument doc = (EPMDocument) contentholder;
                    if (hasnumber && !hasnumberversion) {
                        realdockey = doc.getNumber();
                    }
                    if (!hasnumber && hasnumberversion) {
                        realdockey = doc.getNumber() + "_" + doc.getVersionIdentifier().getValue();
                    }
                } else if (contentholder instanceof WTChangeOrder2) {
                    WTChangeOrder2 order = (WTChangeOrder2) contentholder;
                    realdockey = order.getNumber();
                }

                formula = formula.substring(formula.indexOf(")") + 1, formula.length());
                filename = formula;
                filename = filename.substring(filename.indexOf("(") + 1, filename.lastIndexOf(")"));
                filename = getStrFromProperties(filename, filePrintProperties);
                if (filename.equalsIgnoreCase("filename")) {
                    realfilename = fileName;
                }

                if (filename.equalsIgnoreCase("docnumber")) {
                    realfilename = fileName;
                }

                if (realprefix != null && realprefix.length() > 0) {
                    qualityfilename = realprefix;
                }
                if (realdockey != null && realdockey.length() > 0) {
                    if (qualityfilename.length() > 0) {
                        qualityfilename = qualityfilename + separator + realdockey;
                    } else {
                        qualityfilename = realdockey;
                    }
                }

                if (realfilename != null && realfilename.length() > 0) {
                    if (qualityfilename.length() > 0) {
                        qualityfilename = qualityfilename + separator + realfilename;
                    } else {
                        qualityfilename = realfilename;
                    }
                }
            } catch (WTException wte) {
                System.out.println(wte);
            } catch (java.io.UnsupportedEncodingException uee) {
                System.out.println(uee);
            }
        }
        return qualityfilename;
    }

    public static String getFrmPageSizeByCad(EPMDocument epmDocument) throws WTException {
        if (!SERVER) {
            Class[] cla = { EPMDocument.class};
            Object[] obj = { epmDocument };
            try {
                RemoteMethodServer.getDefault().invoke("getFrmPageSizeByCad", PrintUtil.class.getName(), null, cla, obj);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }else {
            // 查找指定EPMDoucment对象的所有EPMReferenceLink
            QueryResult referenceResult = EPMStructureHelper.service.navigateReferences(epmDocument, null, false);
            while (referenceResult.hasMoreElements()) {
                EPMReferenceLink referenceLink = (EPMReferenceLink) referenceResult.nextElement();
                EPMDocumentMaster document = (EPMDocumentMaster)referenceLink.getRoleBObject();
                String cadName = document.getCADName();
                if (cadName.indexOf("frm")>0) {
                    if (cadName.indexOf("mingxibiao")>0) {
                        return "mingxibiao";
                    }else if(cadName.indexOf("_")>0){
                        String[] str = cadName.split("_");
                        return str[1];
                    }
                }
            }
        }
        return "";
    }

    public static void saveFiletoAttachment(ContentHolder contentholder, FileInputStream fileinputstream)
            throws WTException, WTPropertyVetoException, PropertyVetoException, IOException {
        if (!SERVER) {
            Class[] cla = { ContentHolder.class, FileInputStream.class };
            Object[] obj = { contentholder, fileinputstream };
            try {
                RemoteMethodServer.getDefault().invoke("saveFiletoAttachment", PrintUtil.class.getName(), null, cla,
                        obj);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            ApplicationData appData = ApplicationData.newApplicationData(contentholder);
            appData.setRole(ContentRoleType.SECONDARY);
            ContentServerHelper.service.updateContent(contentholder, appData, fileinputstream);
        }

    }

    public static void saveFiletoAttachment(ContentHolder contentholder, String filename, boolean flag,
            String targetFileName) throws WTException, WTPropertyVetoException, PropertyVetoException, IOException {
        if (!SERVER) {
            Class[] cla = { ContentHolder.class, String.class, boolean.class, String.class };
            Object[] obj = { contentholder, filename, flag, targetFileName };
            try {
                RemoteMethodServer.getDefault().invoke("saveFiletoAttachment", PrintUtil.class.getName(), null, cla,
                        obj);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            if (flag && targetFileName != null && targetFileName.length() > 0) {
                removeAttachment((Representable) contentholder, targetFileName);
            }
            ApplicationData appData = ApplicationData.newApplicationData(contentholder);
            appData.setRole(ContentRoleType.SECONDARY);
            ContentServerHelper.service.updateContent(contentholder, appData, filename);
        }

    }

    public static Representable removeAttachment(Representable representable, String attachName) throws WTException,
            PropertyVetoException {
        if (!SERVER) {
            Class[] cla = { Representable.class, String.class };
            Object[] obj = { representable, attachName };
            try {
                RemoteMethodServer.getDefault().invoke("removeAttachment", PrintUtil.class.getName(), null, cla, obj);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
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

    public static void start(WTObject obj, WfProcess wf) {
        if (!RemoteMethodServer.ServerFlag) {
            Class[] argTypes = { WTObject.class, WfProcess.class };
            Object[] args = { obj, wf };
            try {
                RemoteMethodServer.getDefault().invoke("start", PrintUtil.class.getName(), null, argTypes, args);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            try {
                writeReviewtoPDF(obj, wf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            WTObject obj = (WTObject) WCUtil.getPersistable("VR:wt.doc.WTDocument:148851");
            WfProcess wf = (WfProcess) WCUtil.getPersistable("OR:wt.workflow.engine.WfProcess:148868");
            EPMDocument epmDocument = (EPMDocument)WCUtil.getPersistable("OR:wt.epm.EPMDocument:172251");
            new PrintUtil().writeReviewtoPDF(obj, wf);
            //new PrintUtil().getFrmPageSizeByCad(epmDocument);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static List<String> getReviewOid(String workItemOid) throws WTRuntimeException, WTException {
    	List<String> oidList = new ArrayList<String>();
    	 ReferenceFactory rf = new ReferenceFactory();
    	WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        Persistable pbo = (Persistable) activity.getContext().getValue("primaryBusinessObject");
        if (pbo instanceof WTChangeOrder2) {
        	WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
        	String oid = PersistenceHelper.getObjectIdentifier(changeOrder2).toString();
        	oidList.add(oid);
        	IBAUtility ibaUtility = new IBAUtility(changeOrder2);
        	String type = ibaUtility.getIBAValue("ECNTYPE");
        	if(!"作废更改".equals(type)){
        		QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
    			while (qResult.hasMoreElements()) {
    				Object object = qResult.nextElement();
    				if (object instanceof WTDocument) {
    					WTDocument doc = (WTDocument) object;
    					String docOid = PersistenceHelper.getObjectIdentifier(doc).toString();
    					oidList.add(docOid);
    				}
    			}
        	}
        }else if(pbo instanceof WTDocument){
        	WTDocument doc = (WTDocument) pbo;
        	String docOid = PersistenceHelper.getObjectIdentifier(doc).toString();
        	oidList.add(docOid);
        }
		return oidList;
    }

    /**
     * 获取分发信息和印章
    * @author zhuhao
    * @date 2018-5-16
    * @param workItemOid
    * @param isWTDoc true = WTDocument,false = ChangeHelper2
    * @return
    * @throws WTRuntimeException
    * @throws WTException
     */
    public static String getIssuedInfo(String workItemOid, boolean isWTDoc) throws WTRuntimeException, WTException{
    	String info = "";
    	String oid = "";
    	WTDocument doc = null;
    	ReferenceFactory rf = new ReferenceFactory();
    	WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        Persistable pbo = (Persistable) activity.getContext().getValue("primaryBusinessObject");
        if (pbo instanceof WTChangeOrder2) {
        	WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
        	QueryResult qResult;
			//获取更改前文档
			qResult = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
			while (qResult.hasMoreElements()) {
   				if (qResult.hasMoreElements()) {
   					Object object = qResult.nextElement();
   					if (object instanceof WTDocument) {
   						doc = (WTDocument)object;
   					}
   				}
			}
			if(doc != null){
				if(isWTDoc){
					oid = PrintServerConstants.OID_WTDOCUMENT + doc.getPersistInfo().getObjectIdentifier().getId();
				}else{
					WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(doc);
					Iterator it = coll.iterator();
					if (it.hasNext()) {
						WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
						if(ecn != null){
							oid = PrintServerConstants.OID_WTCHANGEORDER2 + ecn.getPersistInfo().getObjectIdentifier().getId();
						}
					}
				}
				info = queryIssuedInfoByOid(oid);
			}else{
				return "";
			}
        }
		return info;
    }

	private static String queryIssuedInfoByOid(String oid) {
		String batch = "";
		String dismessage = "";
		String info = "";
		 Connection conn = null;
	        try {
				conn = OracleDataSource.getOracleDataSource().getConnection();
				conn.setAutoCommit(false);
				Statement state = conn.createStatement();
				StringBuffer sb = new StringBuffer();
				sb.append("SELECT DISMESSAGE, BATCH FROM GWPRINTAPPLYRECORD WHERE DOCVR = '" + oid + "'");
				ResultSet rs = state.executeQuery(sb.toString());
				while(rs.next()){
					batch = rs.getString("BATCH");
					dismessage = rs.getString("DISMESSAGE");
				}
				if(batch == null || "null".equals(batch)){
					batch = "";
				}
				if(dismessage == null || "null".equals(dismessage)){
				    dismessage = "";
                }
				if(!"".equals(batch) || !"".equals(dismessage)){
					if(batch.contains(",")){
						batch = batch.replace(",", ";");
					}
					if(dismessage.contains(",")){
						dismessage = dismessage.replace(",", ";");
					}
					info = batch + "&" + dismessage;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		return info;
	}

	public static List<String> getObjID(String workItemOid) throws WTRuntimeException, WTException{
		List<String> list = new ArrayList<String>();
		ReferenceFactory rf = new ReferenceFactory();
    	WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        Persistable pbo = (Persistable) activity.getContext().getValue("primaryBusinessObject");
        if (pbo instanceof WTChangeOrder2) {
        	WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
        	list = getObjID2(changeOrder2, list);
        	IBAUtility ibaUtility = new IBAUtility(changeOrder2);
        	String type = ibaUtility.getIBAValue("ECNTYPE");
        	if(!"作废更改".equals(type)){
        		QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
    			while (qResult.hasMoreElements()) {
    				Object object = qResult.nextElement();
    				if (object instanceof WTDocument) {
    					WTDocument doc = (WTDocument) object;
    					list = getObjID2(doc, list);
    				}
    			}
        	}
        }
		return list;
	}

	private static List<String> getObjID2(Object obj, List<String> list) throws WTException{
		ReferenceFactory rf = new ReferenceFactory();
		String veroid = "";
		Versioned version = null;
		if (obj instanceof ChangePackaged) {
			veroid = rf.getReference((ChangePackaged) obj).toString();
         } else {
             QueryResult qr = VersionControlHelper.service.allVersionsFrom((Versioned) obj);
             if (qr.hasMoreElements()) {
                 version = (Versioned) qr.nextElement();
                 veroid = rf.getReference(version).toString();
             }
         }
		veroid = veroid.replaceAll(">", ":");
		list.add(veroid);
		return list;
	}

	public static String getDefaultDept(String workItemOid) throws WTRuntimeException, WTException{
		String value = "";
		ReferenceFactory rf = new ReferenceFactory();
    	WorkItem wi = (WorkItem) rf.getReference(workItemOid).getObject();
        WfActivity activity = (WfActivity) wi.getSource().getObject();
        Persistable pbo = (Persistable) activity.getContext().getValue("primaryBusinessObject");
        if (pbo instanceof WTChangeOrder2) {
        	WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
        	QueryResult qResult = ChangeHelper2.service.getChangeablesBefore(changeOrder2);
        	while (qResult.hasMoreElements()) {
        		Object object = qResult.nextElement();
				if (object instanceof WTDocument) {
					WTDocument doc = (WTDocument) object;
					String dept = IBAHelper.getIBAValue(doc, "DEPT");
					if(dept == null)
						dept = "";
					dept = changeDept(dept);
					if(dept == null || "".equals(dept)){
						return "档案室:1份";
					}
					value = dept + ":1份;档案室:1份";
				}
        	}
        }
		return value;
	}

	private static String changeDept(String str){
		if("1".equals(str)){
			return "一分厂";
		}else if("2".equals(str)){
			return "二分厂";
		}else if("3".equals(str)){
			return "三分厂";
		}else if("4".equals(str)){
			return "四分厂";
		}else if("5".equals(str)){
			return "五分厂";
		}else if("6".equals(str)){
			return "六分厂";
		}else if("7".equals(str)){
			return "七分厂";
		}else if("8".equals(str)){
			return "八分厂";
		}else if("9".equals(str)){
			return "九分厂";
		}else if("10".equals(str)){
			return "十分厂";
		}
		return "";
	}
}

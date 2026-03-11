package ext.casc.integrate.process;

import com.glaway.mpm.mesParameter.MesParameterProcessor;
import com.glaway.mpm.mesParameter.model.GLZhuFuLink;
import com.glaway.mpm.model.TempObject;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.util.*;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.MPMProcessPlanHelper;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import com.ptc.windchill.mpml.resource.MPMResourceHelper;
import ext.casc.change.CSCChange;
import ext.casc.doc.CSCDoc;
import ext.casc.integrate.util.*;
import ext.casc.util.IBAHelper;
import ext.casc.util.IBAUtility;
import ext.casc.util.Tools;
import ext.casc.util.WCUtil;
import ext.casc.workflow.PrintHelper;
import org.apache.commons.net.util.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.content.*;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.*;
import wt.fc.collections.WTCollection;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.method.MethodContext;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.pds.StatementSpec;
import wt.pom.WTConnection;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.session.SessionServerHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTStandardDateFormat;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignment;
import wt.workflow.work.WfBallot;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProcessService implements IProcess {
    private static String wt_temp;
    private static String zip_temp_dir;

    static {
        try {
            WTProperties pro = WTProperties.getLocalProperties();
            wt_temp = pro.getProperty("wt.temp");
            zip_temp_dir = wt_temp;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param number
     * @param versionType 批次常量：BATCH
     * @param version     批次值
     * @return
     * @throws WTException
     * @throws PropertyVetoException
     * @throws DocumentException
     */
    public String getProcessFileDirectory(String number, String versionType, String version) throws WTException, PropertyVetoException, DocumentException {
        WTPart part1 = (WTPart) WCUtil.getPartByNumber(number);
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<lists>");
        if (part1 == null) {
            buffer.append("<exception>");
            buffer.append("输入的编号为空，请重新指定！");
            buffer.append("</exception>");
            buffer.append("</lists>");
            return buffer.toString();
        }

        WTPart part = (WTPart) BomUtil.getLatestPartByView((WTPartMaster) part1.getMaster(), "Manufacturing");
        if (part == null) {
            buffer.append("<exception>");
            buffer.append("输入编号的PBOM不存在，请重新指定！");
            buffer.append("</exception>");
            buffer.append("</lists>");
            return buffer.toString();
        }
        IBAUtility utility = new IBAUtility(part);
        String BATCH = utility.getIBAValue("BATCH");//批次号
        //获取所有部件下所有工艺文件xml的Element
        List<Element> techList = BomUtil.getTechnicsDocumentWithOutReportByPart(part, "TEMP", "Z");
        if (techList != null && !techList.isEmpty()) {
            for (Element techEle : techList) {
                if (techEle != null) {
                    String zzTechnicsNumber = techEle.attributeValue("technicsNumber");
                    //判断是否为主辅合并
                    boolean flag = MesParameterProcessor.isHasZhufuLink(zzTechnicsNumber);
                    //判断是否关联典型工艺
                    boolean isRelatedTypicalTechnics = isRelatedTypicalTechnics(techEle);
                    //1.正式主工艺且有主辅合并
                    if (isZSAndZTechnics(techEle) && flag) {
                        //1.1、未关联典型工艺
                        if (!isRelatedTypicalTechnics) {
                            buffer.append("<file>");
                            buffer.append("<number>");

                            buffer.append(techEle.attributeValue("technicsNumber") + "_ZF");

                            buffer.append("</number>");
                            buffer.append("<pplanNumber>");
                            List<String> ppNumberList = new ArrayList<String>();
                            String technicNumber = techEle.attributeValue("technicsNumber");
                            List<MPMOperationBean> allBeans = (List<MPMOperationBean>) getZFBeans(technicNumber)[1];
                            for (MPMOperationBean bean : allBeans) {
                                if (bean == null) {
                                    continue;
                                }
                                String ppNum = bean.getParent().getPplanNumber();
                                if ("F".equals(bean.getParent().getZfType())) {
                                    ppNum = ppNum.substring(ppNum.lastIndexOf("F"), ppNum.length());
                                    if (!ppNumberList.contains(ppNum)) {
                                        ppNumberList.add(ppNum);
                                    }
                                }
                            }
                            String ppNumber = "";
                            for (String str : ppNumberList) {
                                ppNumber = ppNumber + "、" + str;
                            }
                            buffer.append(techEle.attributeValue("pplanNumber") + ppNumber);
                            buffer.append("</pplanNumber>");
                            buffer.append("<name>");

                            buffer.append(techEle.attributeValue("pplanName") + "_主辅合并");

                            buffer.append("</name>");
                            String PCNO = techEle.attributeValue("PCNO");
                            if (PCNO == null || "".equals(PCNO) || "null".equals(PCNO)) {
                                PCNO = BATCH;
                            }
                            if (PCNO == null || "null".equals(PCNO)) {
                                PCNO = "";
                            }
                            buffer.append("<batch>");
                            buffer.append(PCNO);
                            buffer.append("</batch>");
                            buffer.append("</file>");
                        } else {
                            //主辅
                            buffer.append("<file>");
                            buffer.append("<number>");

                            buffer.append(techEle.attributeValue("technicsNumber") + "_ZF");

                            buffer.append("</number>");
                            buffer.append("<pplanNumber>");
                            List<String> ppNumberList = new ArrayList<String>();
                            String technicNumber = techEle.attributeValue("technicsNumber");
                            List<MPMOperationBean> allBeans = (List<MPMOperationBean>) getZFBeans(technicNumber)[1];
                            for (MPMOperationBean bean : allBeans) {
                                if (bean == null) {
                                    continue;
                                }
                                String ppNum = bean.getParent().getPplanNumber();
                                if ("F".equals(bean.getParent().getZfType())) {
                                    ppNum = ppNum.substring(ppNum.lastIndexOf("F"), ppNum.length());
                                    if (!ppNumberList.contains(ppNum)) {
                                        ppNumberList.add(ppNum);
                                    }
                                }
                            }
                            String ppNumber = "";
                            for (String str : ppNumberList) {
                                ppNumber = ppNumber + "、" + str;
                            }
                            buffer.append(techEle.attributeValue("pplanNumber") + ppNumber);
                            buffer.append("</pplanNumber>");
                            buffer.append("<name>");
                            buffer.append(techEle.attributeValue("pplanName") + "_主辅合并");
                            buffer.append("</name>");
                            String PCNO = techEle.attributeValue("PCNO");
                            if (PCNO == null || "".equals(PCNO) || "null".equals(PCNO)) {
                                PCNO = BATCH;
                            }
                            if (PCNO == null || "null".equals(PCNO)) {
                                PCNO = "";
                            }
                            buffer.append("<batch>");
                            buffer.append(PCNO);
                            buffer.append("</batch>");
                            buffer.append("</file>");
                            //主辅加典型
                            buffer.append("<file>");
                            buffer.append("<number>");
                            buffer.append(techEle.attributeValue("technicsNumber") + "_ZFDX");
                            buffer.append("</number>");
                            buffer.append("<pplanNumber>");

                            buffer.append(techEle.attributeValue("pplanNumber") + ppNumber);
                            buffer.append("</pplanNumber>");
                            buffer.append("<name>");
                            buffer.append(techEle.attributeValue("pplanName") + "_主辅合并_关联典型工艺");
                            buffer.append("</name>");

                            buffer.append("<batch>");
                            buffer.append(PCNO);
                            buffer.append("</batch>");
                            buffer.append("</file>");
                        }
                    } else if (isRelatedTypicalTechnics) {
                        buffer.append("<file>");
                        buffer.append("<number>");
                        buffer.append(techEle.attributeValue("technicsNumber") + "_DX");
                        buffer.append("</number>");
                        buffer.append("<pplanNumber>");
                        //pplanNumber是否需要改变
                        buffer.append(techEle.attributeValue("pplanNumber"));
                        buffer.append("</pplanNumber>");
                        buffer.append("<name>");
                        buffer.append(techEle.attributeValue("pplanName") + "_关联典型工艺");
                        buffer.append("</name>");
                        String PCNO = techEle.attributeValue("PCNO");
                        if (PCNO == null || "".equals(PCNO) || "null".equals(PCNO)) {
                            PCNO = BATCH;
                        }
                        if (PCNO == null || "null".equals(PCNO)) {
                            PCNO = "";
                        }
                        buffer.append("<batch>");
                        buffer.append(PCNO);
                        buffer.append("</batch>");
                        buffer.append("</file>");
                    }
                }
                buffer.append("<file>");
                buffer.append("<number>");
                buffer.append(techEle.attributeValue("technicsNumber"));
                buffer.append("</number>");
                buffer.append("<pplanNumber>");
                buffer.append(techEle.attributeValue("pplanNumber"));
                buffer.append("</pplanNumber>");
                buffer.append("<name>");
                buffer.append(techEle.attributeValue("pplanName"));
                buffer.append("</name>");
                String PCNO = techEle.attributeValue("PCNO");
                if (PCNO == null || "".equals(PCNO) || "null".equals(PCNO)) {
                    PCNO = BATCH;
                }
                if (PCNO == null || "null".equals(PCNO)) {
                    PCNO = "";
                }
                buffer.append("<batch>");
                buffer.append(PCNO);
                buffer.append("</batch>");
                buffer.append("<version>");
                buffer.append(techEle.attributeValue("version"));
                buffer.append("</version>");
                buffer.append("</file>");
            }
        } else {
            buffer.append("<exception>");
            buffer.append("输入编号的PBOM不存在符合条件的工艺文件，请重新指定！");
            buffer.append("</exception>");
        }
        buffer.append("</lists>");

        return buffer.toString();
    }


    public String getProcessFileDirectory(String number, String versionType, String version,String from) throws WTException, PropertyVetoException, DocumentException {
        WTPart part1 = (WTPart) WCUtil.getPartByNumber(number);
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<lists>");
        if (part1 == null) {
            buffer.append("<exception>");
            buffer.append("输入的编号为空，请重新指定！");
            buffer.append("</exception>");
            buffer.append("</lists>");
            return buffer.toString();
        }

        WTPart part = (WTPart) BomUtil.getLatestPartByView((WTPartMaster) part1.getMaster(), "Manufacturing");
        if (part == null) {
            buffer.append("<exception>");
            buffer.append("输入编号的PBOM不存在，请重新指定！");
            buffer.append("</exception>");
            buffer.append("</lists>");
            return buffer.toString();
        }
        IBAUtility utility = new IBAUtility(part);
        String BATCH = utility.getIBAValue("BATCH");//批次号
        //获取所有部件下所有工艺文件xml的Element
        QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
       // LatestConfigSpec lcs = new LatestConfigSpec();
        Set<WTDocument> set = new HashSet<WTDocument>();
       // qr = lcs.process(qr);
        while (qr.hasMoreElements()) {
            WTDocument document = (WTDocument) qr.nextElement();
            QueryResult qr2 = VersionControlHelper.service.allVersionsOf(document.getMaster());
            while(qr2.hasMoreElements()){
                document = (WTDocument)qr2.nextElement();
                String state =document.getState().getState().toString();
                if("APPROVED".equals(state)){
                    set.add(document);
                    break;
                }
            }

        }
        if(set.isEmpty()){
            buffer.append("<exception>");
            buffer.append("输入编号的PBOM不存在符合条件的工艺文件，请重新指定！");
            buffer.append("</exception>");
        } else {
            for(WTDocument doc:set){

                buffer.append("<file>");
                buffer.append("<number>");
                buffer.append(doc.getNumber());
                buffer.append("</number>");
                buffer.append("<pplanNumber>");
                buffer.append(IBAHelper.getIBAStringValue(doc,"PPNUMBER"));
                buffer.append("</pplanNumber>");
                buffer.append("<name>");
                buffer.append(doc.getName());
                buffer.append("</name>");
                String PCNO = IBAHelper.getIBAStringValue(doc,"BATCH");
                if (PCNO == null || "".equals(PCNO) || "null".equals(PCNO)) {
                    PCNO = BATCH;
                }
                if (PCNO == null || "null".equals(PCNO)) {
                    PCNO = "";
                }
                buffer.append("<batch>");
                buffer.append(PCNO);
                buffer.append("</batch>");
                buffer.append("<version>");
                buffer.append(doc.getVersionIdentifier().getValue()+ "."+ doc.getIterationIdentifier().getValue());
                buffer.append("</version>");
                buffer.append("</file>");
            }
        }
        buffer.append("</lists>");

        return buffer.toString();
    }

    public boolean isRelatedTypicalTechnics(Element technicsElement) {
        boolean flag = Boolean.FALSE;
        List<Element> stepList = XmlUtility.getAllSteps(technicsElement);
        for (Element stepElement : stepList) {
            String relatedTypicalDocNumber = stepElement.attributeValue("relatedTypicalNumber");
            if (relatedTypicalDocNumber != null) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 判断是否是正式主工艺
     *
     * @param element
     * @return
     */
    private boolean isZSAndZTechnics(Element element) {
        //主、辅工艺
        String zfFlag = element.attributeValue("ZFFLAG");
        //正式工艺、临时工艺
        String pplantype = element.attributeValue("PPLANTYPE");
        if (Constants.PROCESS_TYPEB_FORMAL.equals(pplantype) && Constants.PROCESS_TYPEC_PRIMARY.equals(zfFlag)) {
            return true;
        }
        return false;
    }

    /**
     * 获取工艺文件信息，数据包集成接口1
     */
    public String getAllApprovedTechnics(String cindex) throws WTException, PropertyVetoException, DocumentException {
        System.out.println("----------DataPackageInterface---getAllApprovedTechnics----");
        WTPart part1 = (WTPart) WCUtil.getPartByNumber(cindex);
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<lists>");
        if (part1 == null) {
            buffer.append("<exception>");
            buffer.append("输入的编号为空，请重新指定！");
            buffer.append("</exception>");
            buffer.append("</lists>");
            return buffer.toString();
        }

        WTPart part = (WTPart) BomUtil.getLatestPartByView((WTPartMaster) part1.getMaster(), "Manufacturing");
        if (part == null) {
            buffer.append("<exception>");
            buffer.append("输入编号的PBOM不存在，请重新指定！");
            buffer.append("</exception>");
            buffer.append("</lists>");
            return buffer.toString();
        }
        IBAUtility partUtility = new IBAUtility(part);
        List<WTDocument> docList = BomUtil.getAllApprovedTempTechnics(part, "TEMP");
        if (docList != null && !docList.isEmpty()) {
            for (WTDocument doc : docList) {
                IBAUtility docUtility = new IBAUtility(doc);
                if (doc != null) {
                    String technicType = docUtility.getIBAValue("PPLANTYPE");
                    if (technicType.equals("临时工艺文件")) {
                        //名称
                        String name = doc.getName();
                        //编号
                        String number = doc.getNumber();
                        //版本
                        String version = doc.getIterationDisplayIdentifier().toString();
                        //工艺文件编号
                        String pplanNumber = docUtility.getIBAValue("PPNUMBER");
                        //批次号
                        String BATCH = partUtility.getIBAValue("BATCH");
                        if (BATCH == null) {
                            BATCH = "";
                        }
                        //编制时间
                        Timestamp timestamp = doc.getCreateTimestamp();
                        java.text.DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                        String bzTime = format.format(timestamp);
                        buffer.append("<technics ");
                        buffer.append("name=\"" + name + "\" number=\"" + number + "\" version=\"" + version + "\" ppNumber=\"" + pplanNumber + "\" bzTime=\"" + bzTime + "\" picihao=\"" + BATCH + "\">");
                        buffer.append("</technics>");
                    }
                }
            }
        } else {
            buffer.append("<exception>");
            buffer.append("输入编号的PBOM不存在符合条件的工艺文件，请重新指定！");
            buffer.append("</exception>");
        }
        buffer.append("</lists>");
        return buffer.toString();
    }

    /**
     * 获取指定工艺文件pdf，数据包接口2
     *
     * @param docNumber
     * @param version
     * @return
     */
    public String getTechnicsPdfUrl(String docNumber, String version) {
        System.out.println("----------DataPackageInterface---getTechnicsPdfUrl----");
        String num = "";
        String ver = "";
        List<TempObject> technicsNumbers = new ArrayList<TempObject>();
        String[] docNumbers = docNumber.split(",");
        String[] versions = version.split(",");
        for (int i = 0; i < docNumbers.length; i++) {
            num = docNumbers[i];
            ver = versions[i];
            if (ver.contains(".")) {
                ver = ver.substring(0, ver.lastIndexOf("."));
            }
            if (num.contains("_ZF")) {
                docNumber = docNumber.substring(0, docNumber.indexOf("_ZF"));
                List<TempObject> technics = ProcessUtil.getAllTechnicsNumber(num, ver);
                technicsNumbers.addAll(technics);
            } else {
                TempObject tempObject = new TempObject();
                tempObject.setNumber(num);
                tempObject.setVersion(ver);
                technicsNumbers.add(tempObject);
            }
        }

        StringBuffer buffer = null;
        String url = "";
        String fileName = "";
        String filePath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp";
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        FileOutputStream fos = null;
        try {
            buffer = new StringBuffer();
            buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><lists>");
            for (TempObject tempObject : technicsNumbers) {
                String number = tempObject.getNumber();
                String vs = tempObject.getVersion();
                WTDocument doc = (WTDocument) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTDocument.class, number, vs, null);
                if (doc == null) {
                    break;
                }
                //工艺类型
                String docType = IBAHelper.getIBAStringValue(doc, "PPLANTYPE");
                String technicsPPnumber = IBAHelper.getIBAStringValue(doc, "PPNUMBER"); //工艺文件编号
                String technicsName = doc.getName();    //工艺文件名称
//        		if("临时工艺文件".equals(docType)){
                // 批次号
                String batch = IBAHelper.getIBAStringValue(doc, "BATCH");
                if (batch == null) {
                    batch = "";
                }
                // 版本
                String docVersion = doc.getIterationDisplayIdentifier().toString();
                // 编制日期
                String createTime = getProcessBianzhiTime(doc);
                if (createTime == null) {
                    Timestamp timestamp = doc.getCreateTimestamp();
                    java.text.DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                    createTime = format.format(timestamp);
                }
                ContentHolder holder = ContentHelper.service.getContents(doc);
                Vector apps = ContentHelper.getApplicationData(holder);
                for (Enumeration e = apps.elements(); e.hasMoreElements(); ) {
                    ApplicationData contentItem = (ApplicationData) e.nextElement();
                    String applicationdataRole = contentItem.getRole().toString();
                    if (!"SECONDARY".equalsIgnoreCase(contentItem.getRole().toString()))
                        continue;// 不是附件

                    if (contentItem.getFileName().startsWith("Print_")) {
                        byte[] bytes = WTDocumentUtil.applicationDataToByte(contentItem);
                        fileName = contentItem.getFileName();
                        String trueFileName = String.valueOf(System.currentTimeMillis()) + ".pdf";
                        fos = new FileOutputStream(fileDir + File.separator + trueFileName);
                        fos.write(bytes);
                        fos.flush();
                        buffer.append("<technics fileName=\"");
                        buffer.append(fileName);
                        buffer.append("\" trueFileName=\"");
                        buffer.append(trueFileName);
                        buffer.append("\" technicsPPNumber=\"");
                        buffer.append(technicsPPnumber);
                        buffer.append("\" technicsName=\"");
                        buffer.append(technicsName);
                        buffer.append("\" batch=\"");
                        buffer.append(batch);
                        buffer.append("\" version=\"");
                        buffer.append(docVersion);
                        buffer.append("\" createTime=\"");
                        buffer.append(createTime);
                        buffer.append("\"></technics>");
                    }
                }
            }
//        	}
            buffer.append("</lists>");
//			WTProperties prop = WTProperties.getLocalProperties();
//            String hostName = prop.getProperty("java.rmi.server.hostname");
//            url = fileName;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (PropertyVetoException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer.toString();
    }

    /**
     * 获取工艺文件目录,数据包接口3
     *
     * @param partNumber
     * @param stage
     * @param batch
     * @return
     * @throws WTException
     */
    public String getTechnicsCatalogs(String partNumber, String stage, String batch) throws WTException {
        System.out.println("----------DataPackageInterface---getTechnicsCatalogs----");
        WTPart part1 = (WTPart) WCUtil.getPartByNumber(partNumber);
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<lists>");
        if (part1 == null) {
            buffer.append("<exception>");
            buffer.append("输入的编号为空，请重新指定！");
            buffer.append("</exception>");
            buffer.append("</lists>");
            return buffer.toString();
        }

        WTPart part = (WTPart) BomUtil.getLatestPartByView((WTPartMaster) part1.getMaster(), "Manufacturing");
        if (part == null) {
            buffer.append("<exception>");
            buffer.append("输入编号的PBOM不存在，请重新指定！");
            buffer.append("</exception>");
            buffer.append("</lists>");
            return buffer.toString();
        }
        IBAUtility partUtility = new IBAUtility(part);
        String partPhaseCode = partUtility.getIBAValue("PHASE_CODE");
        String partBatch = partUtility.getIBAValue("BATCH");
        if (partPhaseCode == null) {
            partPhaseCode = "";
        }
        if (partBatch == null) {
            partBatch = "";
        }
        boolean flag = false;
        boolean isCompareStage = false;
        if (stage != null && !"".equals(stage) && !"null".equals(stage)) {
            isCompareStage = true;
        }
        boolean isCompareBatch = false;
        if (batch != null && !"".equals(batch) && !"null".equals(batch)) {
            isCompareBatch = true;
        }
        if (isCompareBatch && isCompareStage && partBatch.equals(batch) && partPhaseCode.equals(stage)) {
            flag = true;
        }
        if (isCompareBatch && !isCompareStage && partBatch.equals(batch)) {
            flag = true;
        }
        if (!isCompareBatch && isCompareStage && partPhaseCode.equals(stage)) {
            flag = true;
        }
        if (!isCompareBatch && !isCompareStage) {
            flag = true;
        }
        System.out.println("===========" + flag);
        List<WTDocument> docList = BomUtil.getTechnicsCatalogs(part, "TEMP");
        String fileName = "";
        String filePath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp";
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        FileOutputStream fos = null;
        try {
            if (docList != null && !docList.isEmpty() && flag) {
                int count = 0;
                for (WTDocument doc : docList) {
                    ContentHolder holder = ContentHelper.service.getContents(doc);
                    Vector apps = ContentHelper.getApplicationData(holder);
                    for (Enumeration e = apps.elements(); e.hasMoreElements(); ) {
                        ApplicationData contentItem = (ApplicationData) e.nextElement();
                        String applicationdataRole = contentItem.getRole().toString();
                        if (!"SECONDARY".equalsIgnoreCase(contentItem.getRole().toString()))
                            continue;// 不是附件

                        if (contentItem.getFileName().startsWith("Print_")) {
                            byte[] bytes = WTDocumentUtil.applicationDataToByte(contentItem);
                            fileName = contentItem.getFileName();
                            String trueFileName = String.valueOf(System.currentTimeMillis()) + ".pdf";
                            fos = new FileOutputStream(fileDir + File.separator + trueFileName);
                            fos.write(bytes);
                            fos.flush();
                            buffer.append("<technics fileName=\"");
                            buffer.append(fileName);
                            buffer.append("\" trueFileName=\"");
                            buffer.append(trueFileName);
                            buffer.append("\"></technics>");
                            count++;
                        }
                    }
                }
                if (count == 0) {
                    buffer.append("<exception>");
                    buffer.append("该工艺文件没有PDF，请重新指定！");
                    buffer.append("</exception>");
                }
            } else {
                buffer.append("<exception>");
                buffer.append("输入编号的PBOM不存在符合条件的工艺文件，请重新指定！");
                buffer.append("</exception>");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        buffer.append("</lists>");
        return buffer.toString();
    }

    /**
     * 获取工艺更改单，数据包接口4
     *
     * @param partNumber
     * @return
     * @throws WTException
     */
    public String getChangeOrder2(String partNumber, String stage, String batch) throws WTException {
        System.out.println("----------DataPackageInterface---getChangeOrder2----");
        WTPart part1 = (WTPart) WCUtil.getPartByNumber(partNumber);
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<lists>");
        if (part1 == null) {
            buffer.append("<exception>");
            buffer.append("输入的编号为空，请重新指定！");
            buffer.append("</exception>");
            buffer.append("</lists>");
            return buffer.toString();
        }

        WTPart part = (WTPart) BomUtil.getLatestPartByView((WTPartMaster) part1.getMaster(), "Manufacturing");
        if (part == null) {
            buffer.append("<exception>");
            buffer.append("输入编号的PBOM不存在，请重新指定！");
            buffer.append("</exception>");
            buffer.append("</lists>");
            return buffer.toString();
        }
        IBAUtility partUtility = new IBAUtility(part);
        String partPhaseCode = partUtility.getIBAValue("PHASE_CODE");
        String partBatch = partUtility.getIBAValue("BATCH");
        if (partPhaseCode == null) {
            partPhaseCode = "";
        }
        if (partBatch == null) {
            partBatch = "";
        }
        boolean flag = false;
        boolean isCompareStage = false;
        if (stage != null && !"".equals(stage) && !"null".equals(stage)) {
            isCompareStage = true;
        }
        boolean isCompareBatch = false;
        if (batch != null && !"".equals(batch) && !"null".equals(batch)) {
            isCompareBatch = true;
        }
        if (isCompareBatch && isCompareStage && partBatch.equals(batch) && partPhaseCode.equals(stage)) {
            flag = true;
        }
        if (isCompareBatch && !isCompareStage && partBatch.equals(batch)) {
            flag = true;
        }
        if (!isCompareBatch && isCompareStage && partPhaseCode.equals(stage)) {
            flag = true;
        }
        if (!isCompareBatch && !isCompareStage) {
            flag = true;
        }
        List<WTChangeOrder2> changeOrderList = BomUtil.getAllChangeOrder2(part);
        if (changeOrderList != null && !changeOrderList.isEmpty() && flag) {
            for (WTChangeOrder2 changeOrder : changeOrderList) {
                IBAUtility changeOrderUtility = new IBAUtility(changeOrder);
                if (changeOrder != null) {
                    //部件编号
                    String partNum = part.getNumber();
                    //部件名称
                    String partName = part.getName();
                    //更改单编号
                    String changeOrderNumber = PDFUtil.objectToString(changeOrder.getNumber());
                    //产品代号
                    String productIndex = PDFUtil.objectToString(partUtility.getIBAValue("PINDEX"));
                    //产品名称
                    String productName = PDFUtil.objectToString(changeOrder.getContainerReference().getName());
                    //所属整机或分系统
                    String inSys = "";
                    //更改前状态
                    String changeBeforeState = "";
                    //更改前主内容  CHANGEBEFOR
                    String changeBeforeContent = PDFUtil.objectToString(changeOrderUtility.getIBAValue("CHANGEBEFOR"));
                    //更改后主内容  CHANGEAFTER
                    String changeAfterContent = PDFUtil.objectToString(changeOrderUtility.getIBAValue("CHANGEAFTER"));
                    //更改原因  CHANGECAUSE
                    String changeReason = PDFUtil.objectToString(changeOrderUtility.getIBAValue("CHANGECAUSE"));
                    //更改类别  CHANGENOTICETYPE
                    String changeCategory = PDFUtil.objectToString(changeOrderUtility.getIBAValue("CHANGENOTICETYPE"));
                    //工艺落实情况（更改后的工艺文件编号+版本号)
                    String processComplateStatus = "";
                    String technicsPPName = "";
                    //pdf文件名
                    String[] fileNames = BomUtil.getChangeOrderPdf(changeOrder);
                    String fileName = "";
                    String trueFileName = "";
                    if (fileNames.length == 2) {
                        fileName = fileNames[0];
                        trueFileName = fileNames[1];
                    }
                    WTDocument afterDoc = null;
                    List cas = CSCChange.getReleatedCA(changeOrder, false);
                    for (int j = 0; j < cas.size(); j++) {
                        WTChangeActivity2 ca = (WTChangeActivity2) cas.get(j);
                        ArrayList<WTObject> afters = CSCChange.getCAResultItem(ca);
                        for (int i = 0; i < afters.size(); i++) {
                            WTObject after = afters.get(i);
                            if (after instanceof WTDocument) {
                                afterDoc = (WTDocument) after;
                            }
                        }
                    }
                    if (afterDoc != null) {
                        IBAUtility afterDocUtility = new IBAUtility(afterDoc);
                        String pplanNumber = afterDocUtility.getIBAValue("PPNUMBER");
                        String version = afterDoc.getIterationDisplayIdentifier().toString();
                        processComplateStatus = pplanNumber + "," + version;
                        String name = afterDoc.getName();
                        technicsPPName = name.substring(0, name.indexOf("("));
                    }
                    //更改单位
                    String changeUnit = "149";
                    //批准人
                    String[] approve = getApproveTime(changeOrder);
                    String approver = "";
                    //批准时间
                    String approveTime = "";
                    if (approve != null) {
                        approver = approve[0];
                        approveTime = approve[1];
                    }
                    buffer.append("<technics ");
                    buffer.append("changeOrderNumber=\"" + changeOrderNumber + "\" pNumber=\"" + partNum + "\" pName=\"" + partName + "\" productIndex=\"" + productIndex + "\" productName=\"" + productName + "\" inSys=\"" + inSys + "\" changeBeforeState=\"" + changeBeforeState
                            + "\" changeBeforeContent=\"" + changeBeforeContent + "\" changeAfterContent=\"" + changeAfterContent + "\" changeReason=\"" + changeReason + "\" changeCategory=\"" + changeCategory + "\" technicsPPNumber=\"" + processComplateStatus
                            + "\" technicsName=\"" + technicsPPName + "\" changeUnit=\"" + changeUnit + "\" approver=\"" + approver + "\" pdfUrl=\"" + fileName + "\" trueFileName=\"" + trueFileName + "\" approveTime=\"" + approveTime + "\">");
                    buffer.append("</technics>");
                }
            }
        } else {
            buffer.append("<exception>");
            buffer.append("输入编号的PBOM不存在符合条件的工艺文件，请重新指定！");
            buffer.append("</exception>");
        }
        buffer.append("</lists>");
        return buffer.toString();
    }

    /**
     * 数据包接口6，根据过程编号获取工艺文件编号和名称
     *
     * @param processNumber
     * @return
     * @throws WTException
     * @throws RemoteException
     */
    public String getProcessPlanByProcessNumber(String processNumber) throws WTException, RemoteException {
        System.out.println("----------DataPackageInterface---getProcessPlanByProcessNumber----");
        StringBuffer buffer = new StringBuffer();
        String technicsNumber = "";
        String isZF = "";
        String technicsPPNumber = "";
        String technicsName = "";
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<lists>");
        String lukahao = MesParameterProcessor.getLukahaoByLinkProcessNumber(processNumber);
        if (lukahao != null && !"".equals(lukahao)) {
            String[] str = MesParameterProcessor.getTechnicsNumberByLukahao(lukahao);
            technicsNumber = str[0];
            isZF = str[1];
        } else {
            buffer.append("<exception>");
            buffer.append("该过程编号无对应的工艺文件！");
            buffer.append("</exception>");
            buffer.append("</lists>");
            return buffer.toString();
        }
        if ("Y".equals(isZF)) {
            WTDocument doc = WTDocumentUtil.getDocumentByNumber(technicsNumber);
            String version = doc.getVersionInfo().getIdentifier().getValue();
            IBAHelper docHelper = new IBAHelper();
            technicsPPNumber = docHelper.getStringIBAValueOfObject(doc, "PPNUMBER");
            technicsName = doc.getName();
            technicsName = technicsName.substring(0, (technicsName.indexOf(technicsPPNumber) - 1)) + "_主辅合并";

            List<GLZhuFuLink> links = getAllFProcessPlan(technicsNumber, version);
            for (GLZhuFuLink link : links) {
                technicsNumber = link.getFztechnicsnumber();
                doc = WTDocumentUtil.getDocumentByNumber(technicsNumber);
                docHelper = new IBAHelper();
                String fzTechnicsPPNumber = docHelper.getStringIBAValueOfObject(doc, "PPNUMBER");
                fzTechnicsPPNumber = fzTechnicsPPNumber.substring(fzTechnicsPPNumber.lastIndexOf("F"), fzTechnicsPPNumber.length());
                if (technicsPPNumber.contains(fzTechnicsPPNumber)) {
                    continue;
                }
                technicsPPNumber = technicsPPNumber + "、" + fzTechnicsPPNumber;
            }
            buffer.append("<technics");
            buffer.append(" technicsNumber=\"");
            buffer.append(technicsNumber);
            buffer.append("\" technicsPPNumber=\"");
            buffer.append(technicsPPNumber);
            buffer.append("\" technicsName=\"");
            buffer.append(technicsName);
            buffer.append("\"");
            buffer.append("</technics>");
        } else {
            WTDocument doc = WTDocumentUtil.getDocumentByNumber(technicsNumber);
            IBAHelper docHelper = new IBAHelper();
            technicsPPNumber = docHelper.getStringIBAValueOfObject(doc, "PPNUMBER");
            technicsName = doc.getName();
            technicsName = technicsName.substring(0, (technicsName.indexOf(technicsPPNumber) - 1));
            buffer.append("<technics");
            buffer.append(" technicsNumber=\"");
            buffer.append(technicsNumber);
            buffer.append("\" technicsPPNumber=\"");
            buffer.append(technicsPPNumber);
            buffer.append("\" technicsName=\"");
            buffer.append(technicsName);
            buffer.append("\"");
            buffer.append("</technics>");
        }
        buffer.append("</lists>");
        return buffer.toString();
    }

    @Override
    public String getProcessPlan(String processplanType, String productNumber,
                                 String productVersionType, String productVersion,
                                 String processPlanNumber, String processPlanversionType,
                                 String processPlanversion, String guid) throws WTPropertyVetoException, WTException, RemoteException {
        // TODO Auto-generated method stub
        //设置管理员权限
        wt.session.SessionHelper.manager.setAdministrator();
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<processPlan>");
        //验证用户有效性

        //工艺文件编号为空
        if (processPlanNumber.equals("") || processPlanNumber == null) {
            buffer.append("<exception>");
            buffer.append("输入的工艺文件编号为空，请重新指定！");
            buffer.append("</exception>");
            buffer.append("</processPlan>");
            return buffer.toString();
        }
        //根据工艺文件编号和批次获取工序信息
        //MPMProcessPlan plan = ProcessUtil.getProcessPlanByNumBatch(processPlanNumber, processPlanversion);

        //如果是主辅合并工艺
        boolean isZF = false;
        if (processPlanNumber.endsWith(Constants.ZF_SUFFIX)) {
            processPlanNumber = processPlanNumber.replace(Constants.ZF_SUFFIX, "");
            isZF = true;
        }
        boolean isDX = false;
        if (processPlanNumber.endsWith(Constants.DX_SUFFIX)) {
            processPlanNumber = processPlanNumber.replace(Constants.DX_SUFFIX, "");
            isDX = true;
        }
        if (processPlanNumber.endsWith(Constants.ZFDX_SUFFIX)) {
            processPlanNumber = processPlanNumber.replace(Constants.ZFDX_SUFFIX, "");
            isZF = true;
            isDX = true;
        }

        WTDocument doc = WCUtil.getDocumentByNumber(processPlanNumber);
        System.out.println("getProcessPlan-----doc-----" + doc);
        if (doc == null) {
            buffer.append("<exception>");
            buffer.append("PDM中不存在指定编号").append(processPlanNumber).append("的工艺文件！");
            buffer.append("</exception>");
            buffer.append("</processPlan>");
            return buffer.toString();
        }

//		String state = doc.getState().getState().getDisplay(Locale.CHINA);
//		if(!"已批准".equals(state)) {
//			buffer.append("<exception>");
//			buffer.append("工艺文件未批准！");
//			buffer.append("</exception>");
//			buffer.append("</processPlan>");
//			return buffer.toString();
//		}

        //List<MPMOperation> mpmSeqs = new ArrayList<MPMOperation>();
        //mpmSeqs = ProcessUtil.getAllMpmOperationsByMPMProPlan(plan);

        //组装工艺路线信息

        try {
            if (isZF && isDX) {
                //1、主辅合并 Y，关联典型工艺 Y
                buffer.append(getAllZFDXPlanSteps(processPlanNumber));
            } else if (isZF && !isDX) {
                //2、主辅合并 Y，关联典型工艺 N
                buffer.append(getAllPPlanSteps(processPlanNumber));
            } else if (!isZF && isDX) {
                //3、主辅合并 N，关联典型工艺 Y
                buffer.append(getAllRelatedDXPlanSteps(processPlanNumber));

            } else if (!isZF && !isDX) {
                //4、主辅合并 N，关联典型工艺 N
                buffer.append(getPPlanSteps(processPlanNumber));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        buffer.append("</processPlan>");


        return buffer.toString();
    }


    public Object getAllPPlanSteps(String pplanNumber) throws WTException, PropertyVetoException, DocumentException {
        Object[] zfbeans = getZFBeans(pplanNumber);
        MPMProcessPlanBean zbean = (MPMProcessPlanBean) zfbeans[0];
        List<MPMOperationBean> allBeans = (List<MPMOperationBean>) zfbeans[1];
        if (zbean != null && allBeans != null) {
            return processMapToXml(allBeans, zbean);
        }

        return "";
    }

    public Object getAllRelatedDXPlanSteps(String pplanNumber) throws Exception {
        Object[] dxbeans = getDXBeans(pplanNumber);
        MPMProcessPlanBean zbean = (MPMProcessPlanBean) dxbeans[0];
        List<MPMOperationBean> allBeans = (List<MPMOperationBean>) dxbeans[1];
        if (zbean != null && allBeans != null) {
            return processDxMapToXml(allBeans, zbean);
        }

        return "";
    }

    public Object getAllZFDXPlanSteps(String pplanNumber) throws Exception {
        List<MPMOperationBean> allBeans = new ArrayList<MPMOperationBean>();
        Object[] zfbeans = getZFBeans(pplanNumber);
        MPMProcessPlanBean zbean = (MPMProcessPlanBean) zfbeans[0];
        List<MPMOperationBean> allZfBeans = (List<MPMOperationBean>) zfbeans[1];
        for (MPMOperationBean mpmOperationBean : allZfBeans) {
            MPMProcessPlanBean mpmProcessPlanBean = mpmOperationBean.getParent();
            String stepNumber = mpmOperationBean.getNumber();
            String technicsNumber = mpmProcessPlanBean.getProcessNumber();
            Map<String, List<MPMOperationBean>> mapBean = (Map<String, List<MPMOperationBean>>) getDXBeans(technicsNumber)[2];
            if (mapBean.containsKey(stepNumber)) {
                allBeans.addAll(mapBean.get(stepNumber));
            } else {
                allBeans.add(mpmOperationBean);
            }
        }
        if (zbean != null && allBeans != null) {
            return processZFDXMapToXml(allBeans, zbean);
        }
        return "";
    }

    public Object[] getZFBeans(String pplanNumber) throws WTException, PropertyVetoException, DocumentException {
        WTDocument doc = WCUtil.getDocumentByNumber(pplanNumber);
        Object[] zfbeans = new Object[3];
        if (doc == null) {
            return zfbeans;
        }
        ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
        if (data == null) {
            return zfbeans;
        }
        String zipFilePath = zip_temp_dir + File.separator + doc.getNumber() + File.separator + doc.getNumber();
        File file = new File(zipFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        String xmlFile = zipFilePath + File.separator + doc.getNumber() + ".xml";
        byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
        ZipUtil.unZip(bytes, zipFilePath);

        List<MPMOperationBean> beans = readAllXML(xmlFile, pplanNumber);

        MPMProcessPlanBean zbean = null;
        if (!beans.isEmpty()) {
            zbean = beans.get(0).getParent();
        }

        String version = doc.getVersionInfo().getIdentifier().getValue();
        List<GLZhuFuLink> links = getAllFProcessPlan(pplanNumber, version);

        Map<String, MPMOperationBean> fbeans = getAllFMPMOperationBean(links);

        Map<String, List<GLZhuFuLink>> zfLinkMap = parseLinkToMap(links);


        List<MPMOperationBean> allBeans = parseZFCombine(beans, zfLinkMap, fbeans);

        zfbeans[0] = zbean;
        zfbeans[1] = allBeans;
        zfbeans[2] = zfLinkMap;
        //删除临时文件
        CldeUtil.deleteFiles(new File(zip_temp_dir + File.separator + doc.getNumber()));
        return zfbeans;
    }

    public Object[] getDXBeans(String docNumber) throws Exception {
        Object[] objects = new Object[3];
        List<MPMOperationBean> allBeans = new ArrayList<MPMOperationBean>();

        Element technicsElement = getDocTechnicsElement(docNumber);
        List<MPMOperationBean> beans = getMPMOperationBeans(technicsElement);
        List<Element> stepList = XmlUtility.getAllSteps(technicsElement);
        Map<String, List<MPMOperationBean>> mapbeans = new HashMap<String, List<MPMOperationBean>>();
        for (Element step : stepList) {
            String stepNumber = step.attributeValue("stepNumber");
            String relatedTypicalDocNumber = step.attributeValue("relatedTypicalNumber");
            if (relatedTypicalDocNumber != null) {
                Element dxTechnicsElement = getDocTechnicsElement(relatedTypicalDocNumber);
                List<MPMOperationBean> dxbeans = getMPMOperationBeans(dxTechnicsElement);
                mapbeans.put(stepNumber, dxbeans);
            }
        }
        for (MPMOperationBean mpmOperationBean : beans) {
            String stepNumber = mpmOperationBean.getNumber();
            if (mapbeans.containsKey(stepNumber)) {
                allBeans.addAll(mapbeans.get(stepNumber));
            } else {
                allBeans.add(mpmOperationBean);
            }
        }
        MPMProcessPlanBean zbean = null;
        if (!beans.isEmpty()) {
            zbean = beans.get(0).getParent();
        }
        objects[0] = zbean;
        objects[1] = allBeans;
        objects[2] = mapbeans;

        return objects;
    }

    public Element getDocTechnicsElement(String docNumber) throws Exception {
        WTDocument doc = WCUtil.getDocumentByNumber(docNumber);
        Element technicsElement = null;
        if (doc != null) {
            ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
            if (data != null) {
                String zipFilePath = zip_temp_dir + File.separator + doc.getNumber() + File.separator + doc.getNumber();
                File file = new File(zipFilePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String xmlFilePath = zipFilePath + File.separator + doc.getNumber() + ".xml";
                byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
                ZipUtil.unZip(bytes, zipFilePath);
                File xmlFile = new File(xmlFilePath);
                if (xmlFile.exists()) {
                    SAXReader reader = new SAXReader();
                    Document document = reader.read(xmlFile);
                    Element rootElement = document.getRootElement();
                    technicsElement = rootElement.element("QMFawTechnicsInfo");
                }
            }
        }
        //删除临时文件
        CldeUtil.deleteFiles(new File(zip_temp_dir + File.separator + doc.getNumber()));
        return technicsElement;
    }

    /**
     * 获取工序bean集合
     *
     * @param technicsElement
     * @return
     * @throws Exception
     */
    public List<MPMOperationBean> getMPMOperationBeans(Element technicsElement) throws Exception {
        List<MPMOperationBean> mpmOperationBeanList = new ArrayList<MPMOperationBean>();
        Map<String, MPMOperation> map = new HashMap<String, MPMOperation>();
        String technicsNumber = technicsElement.attributeValue("technicsNumber");
        MPMProcessPlan processPlan = MPMProcessPlanUtil.getMPMProcessPlanByNumber(technicsNumber);
        if (processPlan == null) {
            return mpmOperationBeanList;
        }
        QueryResult qr = MPMProcessPlanHelper.service.getMPMOperationUsageLinks(processPlan);
        while (qr.hasMoreElements()) {
            MPMOperationUsageLink link = (MPMOperationUsageLink) qr.nextElement();
            Persistable p = link.getRoleBObject();
            String label = link.getOperationLabel();
            MPMOperation mo = null;
            if (p instanceof MPMOperationMaster) {
                MPMOperationMaster master = (MPMOperationMaster) p;
                QueryResult qResult2 = VersionControlHelper.service.allVersionsOf(master);
                if (qResult2.hasMoreElements()) {
                    mo = (MPMOperation) qResult2.nextElement();
                }
            }
            map.put(label, mo);
        }

        MPMProcessPlanBean ppbean = new MPMProcessPlanBean();
        ppbean.setProcessType(technicsElement.attributeValue("technicsType"));
        ppbean.setZfType(technicsElement.attributeValue("ZFFLAG"));
        ppbean.setFtType(technicsElement.attributeValue("PPLANTYPE"));
        ppbean.setProcessNumber(technicsElement.attributeValue("technicsNumber"));

        String planNum = technicsElement.attributeValue("pplanNumber");
        String pplanName = technicsElement.attributeValue("pplanName");
        String processName;
        if (planNum != null && !"".equals(planNum) && pplanName != null && !"".equals(pplanName)) {
            String technicsName = pplanName + "(" + planNum + ")";
            processName = technicsName;
        } else {
            processName = technicsElement.attributeValue("technicsName");
        }
        ppbean.setProcessName(processName);
        ppbean.setProcessVersion(technicsElement.attributeValue("version"));
        ppbean.setPplanNumber(technicsElement.attributeValue("pplanNumber"));
        ppbean.setBatch(technicsElement.attributeValue("PCNO"));

        //所有工步节点
        List<Element> list = technicsElement.selectNodes("//steps/QMProcedureInfo");
        if (list != null) {
            for (Element ele : list) {
                String stepNumber = ele.attributeValue("stepNumber");

                String tempNumber = stepNumber;
                int numberLength = 3 - tempNumber.length();
                for (int i = 0; i < numberLength; i++) {
                    tempNumber = 0 + tempNumber;
                }

                MPMOperation mo = map.get(tempNumber);

                String ZJGS = "";
                String DJGS = "";
                if (mo != null) {
                    try {
                        ZJGS = IBAHelper.getIBAStringValue(mo, "ZJGS");
                        DJGS = IBAHelper.getIBAStringValue(mo, "DJGS");
                    } catch (WTException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                MPMOperationBean bean = new MPMOperationBean();
                bean.setParent(ppbean);
                bean.setNumber(stepNumber);
                bean.setName(ele.attributeValue("stepName"));
                bean.setVersion("");
                bean.setZrcj(ele.attributeValue("workShop"));
                bean.setZjgs(ZJGS);
                bean.setDegs(DJGS);
                bean.setKeyoper(ele.attributeValue("isKey"));
                bean.setIsgb("false");
                bean.setBsoId(ele.attributeValue("bsoID"));
                List<MPMOperationBean> childrengongbus = new ArrayList<MPMOperationBean>();
                getChildrengongbus(ele, childrengongbus, bean, ppbean);
                bean.setChildrengongbu(childrengongbus);
                mpmOperationBeanList.add(bean);
            }
        }
        return mpmOperationBeanList;
    }


    /**
     * 获取关联的所有辅工艺信息
     *
     * @param links
     * @return
     * @throws PropertyVetoException
     * @throws WTException
     * @throws DocumentException
     */
    private Map<String, MPMOperationBean> getAllFMPMOperationBean(List<GLZhuFuLink> links) throws
            WTException, PropertyVetoException, DocumentException {
        Map<String, MPMOperationBean> mapbeans = new HashMap<String, MPMOperationBean>();
        Set<String> hasBean = new HashSet<String>();
        for (GLZhuFuLink link : links) {
            String fnumber = link.getFztechnicsnumber();
            if (hasBean.contains(fnumber)) {
                continue;
            }
            hasBean.add(fnumber);

            String version = link.getFztechnicsversion();
            WTDocument doc = CSCDoc.getLatestDocByNumberAndVersion(fnumber, version);
            if (doc != null) {
                ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
                if (data != null) {
                    String zipFilePath = zip_temp_dir + File.separator + doc.getNumber() + File.separator + doc.getNumber();
                    File file = new File(zipFilePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    String xmlFile = zipFilePath + File.separator + doc.getNumber() + ".xml";
                    byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
                    ZipUtil.unZip(bytes, zipFilePath);
                    List<MPMOperationBean> beans = readAllXML(xmlFile, fnumber);
                    for (MPMOperationBean bean : beans) {
                        String key = bean.getParent().getProcessNumber() + "_" + bean.getNumber();
                        mapbeans.put(key, bean);
                    }
                }
            }

        }
        return mapbeans;
    }

    private List<MPMOperationBean> parseZFCombine
            (List<MPMOperationBean> beans, Map<String, List<GLZhuFuLink>> zfLinkMap, Map<String, MPMOperationBean> fbeans) {
        List<MPMOperationBean> allBeans = new ArrayList<MPMOperationBean>();
        for (MPMOperationBean bean : beans) {
            String zstepNumber = bean.getNumber();
            List<GLZhuFuLink> zfLinks = zfLinkMap.get(zstepNumber);
            if (zfLinks == null || zfLinks.isEmpty()) {
				/*if("true".equals(bean.getIsgb())){//如果是工步，则判断工步的所属的工序有没有被辅工艺关联，没关联则输出。
					MPMOperationBean pgxbean = bean.getParentgongxu();
					List<GLZhuFuLink> pzfLinks = zfLinkMap.get(pgxbean.getNumber());
					if(pzfLinks==null||pzfLinks.isEmpty()){
						allBeans.add(bean);
					}
				}else{
					allBeans.add(bean);
				}*/
                allBeans.add(bean);
            } else {
                for (GLZhuFuLink link : zfLinks) {
                    String fnumber = link.getFztechnicsnumber();
                    String fzprocedurenumber = link.getFzprocedurenumber();
                    String fstepNumber = Tools.getStepNumber(fzprocedurenumber, "_");

                    MPMOperationBean fbean = fbeans.get(fnumber + "_" + fstepNumber);
                    if (fbean != null) {
                        allBeans.add(fbean);
                    }

                }
            }
        }

        return allBeans;
    }

    private String processMapToXml(List<MPMOperationBean> allBeans, MPMProcessPlanBean zbean) {
        StringBuffer processSeq = new StringBuffer();

        processSeq.append("<processType>");
        processSeq.append(zbean.getProcessType());
        processSeq.append("</processType>");
        //主辅工艺
        processSeq.append("<zfType>");
        processSeq.append(zbean.getZfType());
        processSeq.append("</zfType>");
        //正式临时工艺
        processSeq.append("<ftType>");
        processSeq.append(zbean.getFtType());
        processSeq.append("</ftType>");
        processSeq.append("<processNumber>");
        processSeq.append(zbean.getProcessNumber() + "_ZF");
        processSeq.append("</processNumber>");

        processSeq.append("<processName>");
        processSeq.append(zbean.getProcessName());
        processSeq.append("</processName>");

        processSeq.append("<processVersion>");
        processSeq.append(zbean.getProcessVersion());
        processSeq.append("</processVersion>");
        processSeq.append("<pplanNumber>");
        List<String> ppNumberList = new ArrayList<String>();
        for (MPMOperationBean bean : allBeans) {
            String ppNum = bean.getParent().getPplanNumber();
            if ("F".equals(bean.getParent().getZfType())) {
                ppNum = ppNum.substring(ppNum.lastIndexOf("F"), ppNum.length());
                if (!ppNumberList.contains(ppNum)) {
                    ppNumberList.add(ppNum);
                }
            }
        }
        String ppNumber = "";
        for (String str : ppNumberList) {
            ppNumber = ppNumber + "、" + str;
        }
        processSeq.append(zbean.getPplanNumber() + ppNumber);
        processSeq.append("</pplanNumber>");

        processSeq.append("<batch>");
        processSeq.append(zbean.getBatch());
        processSeq.append("</batch>");
        int newNumber = 10;

        for (MPMOperationBean bean : allBeans) {
            processLinkXml(processSeq, bean, false, newNumber);
            for (MPMOperationBean gongbu : bean.getChildrengongbu()) {
                processLinkXml(processSeq, gongbu, true, newNumber);
            }
            newNumber += 10;
        }

        return processSeq.toString();
    }

    private String processDxMapToXml(List<MPMOperationBean> allBeans, MPMProcessPlanBean zbean) {
        StringBuffer processSeq = new StringBuffer();

        processSeq.append("<processType>");
        processSeq.append(zbean.getProcessType());
        processSeq.append("</processType>");
        //主辅工艺
        processSeq.append("<zfType>");
        processSeq.append(zbean.getZfType());
        processSeq.append("</zfType>");
        //正式临时工艺
        processSeq.append("<ftType>");
        processSeq.append(zbean.getFtType());
        processSeq.append("</ftType>");
        processSeq.append("<processNumber>");
        processSeq.append(zbean.getProcessNumber() + "_DX");
        processSeq.append("</processNumber>");

        processSeq.append("<processName>");
        processSeq.append(zbean.getProcessName());
        processSeq.append("</processName>");

        processSeq.append("<processVersion>");
        processSeq.append(zbean.getProcessVersion());
        processSeq.append("</processVersion>");
        processSeq.append("<pplanNumber>");
        processSeq.append(zbean.getPplanNumber());
//        List<String> ppNumberList = new ArrayList<String>();
////        for (MPMOperationBean bean : allBeans) {
////            String ppNum = bean.getParent().getPplanNumber();
////            if ("F".equals(bean.getParent().getZfType())) {
////                ppNum = ppNum.substring(ppNum.lastIndexOf("F"), ppNum.length());
////                if (!ppNumberList.contains(ppNum)) {
////                    ppNumberList.add(ppNum);
////                }
////            }
////        }
////        String ppNumber = "";
////        for (String str : ppNumberList) {
////            ppNumber = ppNumber + "、" + str;
////        }
////        processSeq.append(zbean.getPplanNumber() + ppNumber);
        processSeq.append("</pplanNumber>");

        processSeq.append("<batch>");
        processSeq.append(zbean.getBatch());
        processSeq.append("</batch>");
        int newNumber = 10;

        for (MPMOperationBean bean : allBeans) {
            processLinkXml(processSeq, bean, false, newNumber);
            for (MPMOperationBean gongbu : bean.getChildrengongbu()) {
                processLinkXml(processSeq, gongbu, true, newNumber);
            }
            newNumber += 10;
        }

        return processSeq.toString();
    }

    private String processZFDXMapToXml(List<MPMOperationBean> allBeans, MPMProcessPlanBean zbean) {
        StringBuffer processSeq = new StringBuffer();

        processSeq.append("<processType>");
        processSeq.append(zbean.getProcessType());
        processSeq.append("</processType>");
        //主辅工艺
        processSeq.append("<zfType>");
        processSeq.append(zbean.getZfType());
        processSeq.append("</zfType>");
        //正式临时工艺
        processSeq.append("<ftType>");
        processSeq.append(zbean.getFtType());
        processSeq.append("</ftType>");
        processSeq.append("<processNumber>");
        processSeq.append(zbean.getProcessNumber() + "_ZFDX");
        processSeq.append("</processNumber>");

        processSeq.append("<processName>");
        processSeq.append(zbean.getProcessName());
        processSeq.append("</processName>");

        processSeq.append("<processVersion>");
        processSeq.append(zbean.getProcessVersion());
        processSeq.append("</processVersion>");
        processSeq.append("<pplanNumber>");
        List<String> ppNumberList = new ArrayList<String>();
        for (MPMOperationBean bean : allBeans) {
            String ppNum = bean.getParent().getPplanNumber();
            if ("F".equals(bean.getParent().getZfType())) {
                ppNum = ppNum.substring(ppNum.lastIndexOf("F"), ppNum.length());
                if (!ppNumberList.contains(ppNum)) {
                    ppNumberList.add(ppNum);
                }
            }
        }
        String ppNumber = "";
        for (String str : ppNumberList) {
            ppNumber = ppNumber + "、" + str;
        }
        processSeq.append(zbean.getPplanNumber() + ppNumber);
        processSeq.append("</pplanNumber>");

        processSeq.append("<batch>");
        processSeq.append(zbean.getBatch());
        processSeq.append("</batch>");
        int newNumber = 10;

        for (MPMOperationBean bean : allBeans) {
            processLinkXml(processSeq, bean, false, newNumber);
            for (MPMOperationBean gongbu : bean.getChildrengongbu()) {
                processLinkXml(processSeq, gongbu, true, newNumber);
            }
            newNumber += 10;
        }

        return processSeq.toString();
    }

    private void processLinkXml(StringBuffer processSeq, MPMOperationBean bean, boolean isgongbu, int newNumber) {
        processSeq.append("<operlink>");
        processSeq.append("<number>");
        processSeq.append(bean.getNumber());
        processSeq.append("</number>");
        if (!isgongbu) {
            processSeq.append("<newnumber>");
            processSeq.append(String.valueOf(newNumber));
            processSeq.append("</newnumber>");
        }
        processSeq.append("<name>");
        processSeq.append(bean.getName());
        processSeq.append("</name>");
        processSeq.append("<version>");
        processSeq.append(bean.getVersion());
        processSeq.append("</version>");
        processSeq.append("<zrcj>");
        processSeq.append(bean.getZrcj());
        processSeq.append("</zrcj>");
        processSeq.append("<zjgs>");
        processSeq.append(bean.getZjgs());
        processSeq.append("</zjgs>");
        processSeq.append("<degs>");
        processSeq.append(bean.getDegs());
        processSeq.append("</degs>");
        processSeq.append("<keyoper>");
        processSeq.append(bean.getKeyoper());
        processSeq.append("</keyoper>");

        processSeq.append("<processType>");
        processSeq.append(bean.getParent().getProcessType());
        processSeq.append("</processType>");
        //主辅工艺
        processSeq.append("<zfType>");
        processSeq.append(bean.getParent().getZfType());
        processSeq.append("</zfType>");
        //正式临时工艺
        processSeq.append("<ftType>");
        processSeq.append(bean.getParent().getFtType());
        processSeq.append("</ftType>");
        processSeq.append("<processNumber>");
        processSeq.append(bean.getParent().getProcessNumber());
        processSeq.append("</processNumber>");

        processSeq.append("<processName>");
        processSeq.append(bean.getParent().getProcessName());
        processSeq.append("</processName>");

        processSeq.append("<processVersion>");
        processSeq.append(bean.getParent().getProcessVersion());
        processSeq.append("</processVersion>");
        processSeq.append("<pplanNumber>");
        processSeq.append(bean.getParent().getPplanNumber());
        processSeq.append("</pplanNumber>");

        processSeq.append("<batch>");
        processSeq.append(bean.getParent().getBatch());
        processSeq.append("</batch>");

        processSeq.append("<isgb>");
        processSeq.append(bean.getIsgb());
        processSeq.append("</isgb>");

        if (isgongbu) {
            processSeq.append("<parentgongxu>");
            processSeq.append(bean.getParentgongxu().getNumber());
            processSeq.append("</parentgongxu>");
        }
        processSeq.append("</operlink>");
    }

    private Map<String, List<GLZhuFuLink>> parseLinkToMap(List<GLZhuFuLink> links) {
        Map<String, List<GLZhuFuLink>> zfLinkMap = new HashMap<String, List<GLZhuFuLink>>();
        for (GLZhuFuLink link : links) {
            String procedurenumber = link.getZzprocedurenumber();
            if (!Tools.isNull(procedurenumber)) {
                String[] ss = procedurenumber.split("_");
                String stepNumber = ss[0];
                //String stepNumber = Tools.getStepNumber(s);
                if (zfLinkMap.get(stepNumber) == null) {
                    List<GLZhuFuLink> ls = new ArrayList<GLZhuFuLink>();
                    ls.add(link);
                    zfLinkMap.put(stepNumber, ls);
                } else {
                    List<GLZhuFuLink> ls = zfLinkMap.get(stepNumber);
                    ls.add(link);
                }
            }
        }
        return zfLinkMap;
    }

    public List<GLZhuFuLink> getAllFProcessPlan(String pplanNumber, String version) {
        List<GLZhuFuLink> links = new ArrayList<GLZhuFuLink>();
        String sql = "select * from gl_zhufulink where zztechnicsnumber='" + pplanNumber + "' and zztechnicsversion='" + version + "' order by fztechnicsnumber asc, to_number(substr(fzprocedurenumber,0,(instr(fzprocedurenumber,'_',1,1)-1))) asc";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        WTConnection wtconnection = null;
        try {
            SessionServerHelper.manager.setAccessEnforced(false);
            MethodContext methodcontext = MethodContext.getContext();
            wtconnection = (WTConnection) methodcontext.getConnection();
            pstmt = wtconnection.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                GLZhuFuLink link = new GLZhuFuLink();
                link.setGwkey(rs.getString("GWKEY"));
                link.setFztechnicsnumber(rs.getString("FZTECHNICSNUMBER"));
                link.setFztechnicsversion(rs.getString("FZTECHNICSVERSION"));
                link.setFzprocedurenumber(rs.getString("FZPROCEDURENUMBER"));
                link.setZztechnicsnumber(rs.getString("ZZTECHNICSNUMBER"));
                link.setZztechnicsversion(rs.getString("ZZTECHNICSVERSION"));
                link.setZzprocedurenumber(rs.getString("ZZPROCEDURENUMBER"));
                link.setPicihao(rs.getString("PICIHAO"));
                links.add(link);
            }
            rs.close();
            pstmt.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            SessionServerHelper.manager.setAccessEnforced(true);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            SessionServerHelper.manager.setAccessEnforced(true);
        }
        return links;
    }

    private List<MPMOperationBean> readAllXML(String xmlFile, String pplanNumber) throws DocumentException {
        List<MPMOperationBean> result = new ArrayList<MPMOperationBean>();
        File file = new File(xmlFile);
        Map<String, MPMOperation> map = new HashMap<String, MPMOperation>();

        try {
            MPMProcessPlan processPlan = MPMProcessPlanUtil.getMPMProcessPlanByNumber(pplanNumber);
            if (processPlan == null) {
                return result;
            }
            QueryResult qr = MPMProcessPlanHelper.service.getMPMOperationUsageLinks(processPlan);
            while (qr.hasMoreElements()) {
                MPMOperationUsageLink link = (MPMOperationUsageLink) qr.nextElement();
                Persistable p = link.getRoleBObject();
                String label = link.getOperationLabel();
                MPMOperation mo = null;
                if (p instanceof MPMOperationMaster) {
                    MPMOperationMaster master = (MPMOperationMaster) p;
                    QueryResult qResult2 = VersionControlHelper.service.allVersionsOf(master);
                    if (qResult2.hasMoreElements()) {
                        mo = (MPMOperation) qResult2.nextElement();
                    }
                }
                map.put(label, mo);

            }
        } catch (WTPropertyVetoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (!file.exists()) {
            System.out.println(xmlFile + " is not exist!");
            return result;
        }
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        Element rootElement = document.getRootElement();

        //工艺文件信息
        List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
        String processName = "";//工艺文件名称
        MPMProcessPlanBean ppbean = null;
        for (Object object : pplanList) {
            ppbean = new MPMProcessPlanBean();
            Element element = (Element) object;
            ppbean.setProcessType(element.attributeValue("technicsType"));
            ppbean.setZfType(element.attributeValue("ZFFLAG"));
            ppbean.setFtType(element.attributeValue("PPLANTYPE"));
            ppbean.setProcessNumber(element.attributeValue("technicsNumber"));

            String planNum = element.attributeValue("pplanNumber");
            String pplanName = element.attributeValue("pplanName");
            if (planNum != null && !"".equals(planNum) && pplanName != null && !"".equals(pplanName)) {
                String technicsName = pplanName + "(" + planNum + ")";
                processName = technicsName;
            } else {
                processName = element.attributeValue("technicsName");
            }
            ppbean.setProcessName(processName);
            ppbean.setProcessVersion(element.attributeValue("version"));
            ppbean.setPplanNumber(element.attributeValue("pplanNumber"));
            ppbean.setBatch(element.attributeValue("PCNO"));
        }
        //所有工步节点
        List<Element> list = rootElement.selectNodes("//steps/QMProcedureInfo");
        if (list != null) {
            for (Element ele : list) {
                String stepNumber = ele.attributeValue("stepNumber");

                String tempNumber = stepNumber;
                int numberLength = 3 - tempNumber.length();
                for (int i = 0; i < numberLength; i++) {
                    tempNumber = 0 + tempNumber;
                }

                MPMOperation mo = map.get(tempNumber);

                String ZJGS = "";
                String DJGS = "";
                if (mo != null) {
                    try {
                        ZJGS = IBAHelper.getIBAStringValue(mo, "ZJGS");
                        DJGS = IBAHelper.getIBAStringValue(mo, "DJGS");
                    } catch (WTException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                MPMOperationBean bean = new MPMOperationBean();
                bean.setParent(ppbean);
                bean.setNumber(stepNumber);
                bean.setName(ele.attributeValue("stepName"));
                bean.setVersion("");
                bean.setZrcj(ele.attributeValue("workShop"));
                bean.setZjgs(ZJGS);
                bean.setDegs(DJGS);
                bean.setKeyoper(ele.attributeValue("isKey"));
                bean.setIsgb("false");
                bean.setBsoId(ele.attributeValue("bsoID"));
                List<MPMOperationBean> childrengongbus = new ArrayList<MPMOperationBean>();
                getChildrengongbus(ele, childrengongbus, bean, ppbean);
                bean.setChildrengongbu(childrengongbus);

                result.add(bean);

            }
        }

        return result;
    }

    /**
     * 获取该工序下的工步信息
     *
     * @param childrengongbus
     * @param ppbean
     */
    private void getChildrengongbus(Element
                                            element, List<MPMOperationBean> childrengongbus, MPMOperationBean parent, MPMProcessPlanBean ppbean) {
        List<Element> paces = element.elements("paces");
        for (Element pace : paces) {
            List<Element> qMProcedureInfos = pace.elements("QMProcedureInfo");
            for (Element e : qMProcedureInfos) {
                String stepNumber = e.attributeValue("stepNumber");

                String ZJGS = "";
                String DJGS = "";

                MPMOperationBean bean = new MPMOperationBean();
                bean.setParent(ppbean);
                bean.setNumber(stepNumber);
                bean.setName(e.attributeValue("stepName"));
                bean.setVersion("");
                bean.setZrcj(e.attributeValue("workShop"));
                bean.setZjgs(ZJGS);
                bean.setDegs(DJGS);
                bean.setKeyoper(e.attributeValue("isKey"));
                bean.setIsgb("true");
                bean.setBsoId(parent.getBsoId() + e.attributeValue("bsoID"));
                bean.setParentgongxu(parent);
                childrengongbus.add(bean);
            }
        }


    }

    public String getProcessPlanByNum(String processPlanNumber) {
        return null;
    }

    @Override
    public String getProcessFileDirectory(String number, String versionType,
                                          String version, int fileLevel, String fileTypeA, String fileTypeB,
                                          String guid) {
        // TODO Auto-generated method stub
        return null;
    }

    //整合XML文件：包含工序（路线）信息
    public String createProcessSeqXML(List<MPMOperation> seqs) {
        StringBuffer processSeq = new StringBuffer();
        String number = "";//工序编号
        String name = "";//工序名称
        String version = "";//工序版本
        String zrcj = "";//责任车间
        String zjgs = "";//准结工时
        String degs = "";//定额工时
        String keyoper = "";//是否关键工序
        if (!seqs.isEmpty()) {
            for (MPMOperation seq : seqs) {
                name = seq.getName();
                number = seq.getNumber();
                version = seq.getIterationDisplayIdentifier().toString();
                /////////////////
                //

                ////////////////
                processSeq.append("<operlink>");

                processSeq.append("<number>");
                processSeq.append(number);
                processSeq.append("</number>");
                processSeq.append("<name>");
                processSeq.append(name);
                processSeq.append("</name>");
                processSeq.append("<version>");
                processSeq.append(version);
                processSeq.append("</version>");
                processSeq.append("<zrcj>");
                processSeq.append(zrcj);
                processSeq.append("</zrcj>");
                processSeq.append("<zjgs>");
                processSeq.append(zjgs);
                processSeq.append("</zjgs>");
                processSeq.append("<degs>");
                processSeq.append(degs);
                processSeq.append("</desj>");
                processSeq.append("<keyoper>");
                processSeq.append(keyoper);
                processSeq.append("</keyoper>");

                processSeq.append("</operlink>");
            }
        }

        return processSeq.toString();
    }

    /**
     * @param pplanNumber
     * @throws WTException
     * @throws PropertyVetoException
     * @throws DocumentException
     * @throws RemoteException
     */
    public static String getPPlanSteps(String pplanNumber) throws
            WTException, PropertyVetoException, DocumentException, RemoteException {
        System.out.println("getPPlanSteps-----pplanNumber-----" + pplanNumber);
        WTDocument doc = WCUtil.getDocumentByNumber(pplanNumber);
        System.out.println("getPPlanSteps-----doc-----" + doc);
        if (doc == null) {
            return "";
        }
        ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
        if (data == null) {
            return "";
        }

        String zipFilePath = zip_temp_dir + File.separator + doc.getNumber() + File.separator + doc.getNumber();
        File file = new File(zipFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        String xmlFile = zipFilePath + File.separator + doc.getNumber() + ".xml";
        byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
        ZipUtil.unZip(bytes, zipFilePath);

        String processSeq = readXML(xmlFile, pplanNumber);
        System.out.println("-----processSeq-----" + processSeq);

        //删除临时文件
        CldeUtil.deleteFiles(new File(zip_temp_dir + File.separator + doc.getNumber()));

        return processSeq;
    }

    public static String readXML(String xmlFile, String pplanNumber) throws DocumentException {
        StringBuffer processSeq = new StringBuffer();
        File file = new File(xmlFile);
        Map<String, MPMOperation> map = new HashMap<String, MPMOperation>();

        try {
            MPMProcessPlan processPlan = MPMProcessPlanUtil.getMPMProcessPlanByNumber(pplanNumber);
            QueryResult qr = MPMProcessPlanHelper.service.getMPMOperationUsageLinks(processPlan);
            while (qr.hasMoreElements()) {
                MPMOperationUsageLink link = (MPMOperationUsageLink) qr.nextElement();
                Persistable p = link.getRoleBObject();
                String label = link.getOperationLabel();
                MPMOperation mo = null;
                if (p instanceof MPMOperationMaster) {
                    MPMOperationMaster master = (MPMOperationMaster) p;
                    QueryResult qResult2 = VersionControlHelper.service.allVersionsOf(master);
                    if (qResult2.hasMoreElements()) {
                        mo = (MPMOperation) qResult2.nextElement();
                    }
                }
                map.put(label, mo);

            }
        } catch (WTPropertyVetoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (!file.exists()) {
            System.out.println(xmlFile + " is not exist!");
            return processSeq.toString();
        }
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        Element rootElement = document.getRootElement();

        //工艺文件信息
        List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
        for (Object object : pplanList) {
            Element element = (Element) object;

            processSeq.append("<processType>");
            processSeq.append(element.attributeValue("technicsType"));
            processSeq.append("</processType>");
            //主辅工艺
            processSeq.append("<zfType>");
            processSeq.append(element.attributeValue("ZFFLAG"));
            processSeq.append("</zfType>");
            //正式临时工艺
            processSeq.append("<ftType>");
            processSeq.append(element.attributeValue("PPLANTYPE"));
            processSeq.append("</ftType>");
            processSeq.append("<processNumber>");
            processSeq.append(element.attributeValue("technicsNumber"));
            processSeq.append("</processNumber>");
            processSeq.append("<processName>");

            String planNum = element.attributeValue("pplanNumber");
            String pplanName = element.attributeValue("pplanName");
            if (planNum != null && !"".equals(planNum) && pplanName != null && !"".equals(pplanName)) {
                String technicsName = pplanName + "(" + planNum + ")";
                processSeq.append(technicsName);
            } else {
                processSeq.append(element.attributeValue("technicsName"));
            }


            processSeq.append("</processName>");
            processSeq.append("<processVersion>");
            processSeq.append(element.attributeValue("version"));
            processSeq.append("</processVersion>");
            processSeq.append("<pplanNumber>");
            processSeq.append(element.attributeValue("pplanNumber"));
            processSeq.append("</pplanNumber>");

            processSeq.append("<batch>");
            processSeq.append(element.attributeValue("PCNO"));
            processSeq.append("</batch>");
        }

        //所有工步节点
        List list = rootElement.selectNodes("//QMProcedureInfo");
        if (list != null) {
            for (Object object : list) {
                Element ele = (Element) object;
                String stepNumber = ele.attributeValue("stepNumber");
                String tempNumber = stepNumber;
                int numberLength = 3 - tempNumber.length();
                for (int i = 0; i < numberLength; i++) {
                    tempNumber = 0 + tempNumber;
                }

                MPMOperation mo = map.get(tempNumber);

                String ZJGS = "";
                String DJGS = "";
                if (mo != null) {
                    try {
                        ZJGS = IBAHelper.getIBAStringValue(mo, "ZJGS");
                        DJGS = IBAHelper.getIBAStringValue(mo, "DJGS");
                    } catch (WTException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                processSeq.append("<operlink>");

                processSeq.append("<number>");
                processSeq.append(stepNumber);
                processSeq.append("</number>");
                processSeq.append("<newnumber>");
                processSeq.append(stepNumber);
                processSeq.append("</newnumber>");
                processSeq.append("<name>");
                processSeq.append(ele.attributeValue("stepName"));
                processSeq.append("</name>");
                processSeq.append("<version>");
                processSeq.append("");
                processSeq.append("</version>");
                processSeq.append("<zrcj>");
                processSeq.append(ele.attributeValue("workShop"));
                processSeq.append("</zrcj>");
                processSeq.append("<zjgs>");
                processSeq.append(ZJGS);
                processSeq.append("</zjgs>");
                processSeq.append("<degs>");
                processSeq.append(DJGS);
                processSeq.append("</degs>");
                processSeq.append("<keyoper>");
                processSeq.append(ele.attributeValue("isKey"));
                processSeq.append("</keyoper>");

                processSeq.append("</operlink>");
            }
        }

        return processSeq.toString();
    }

    private String[] getApproveTime(WTChangeOrder2 changeOrder) {
        QueryResult qrProcs;
        try {
            qrProcs = WfEngineHelper.service.getAssociatedProcesses(changeOrder, null, null);
            WfProcess proc = null;
            while (qrProcs.hasMoreElements()) {
                proc = (WfProcess) qrProcs.nextElement();
            }
            List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
            activityList = PrintHelper.getActivities(proc, activityList);
            Iterator iterator = activityList.iterator();
            while (iterator.hasNext()) {
                WfAssignedActivity wfactivity = (WfAssignedActivity) iterator.next();
                String activityName = wfactivity.getName();
                if (activityName.equals("批准")) {
                    Timestamp endTime = wfactivity.getEndTime();
                    String approverName = getPrincipalName(wfactivity);
                    if (endTime != null) {
                        return new String[]{approverName, WTStandardDateFormat.format(wfactivity.getEndTime(), "yyyy/MM/dd")};
                    }
                }
            }
        } catch (WTException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPrincipalName(WfActivity wfa) throws Exception {
        String field = "";
        String str = "";
        Enumeration en1 = null;
        Enumeration en2 = null;
        en1 = ((WfAssignedActivity) wfa).getAssignments();

        for (int i = 0; en1 != null && en1.hasMoreElements(); i++) {
            WfAssignment wfassignment = (WfAssignment) en1.nextElement();
            en2 = wfassignment.checkBallotStatus().elements();
            for (int j = 0; en2 != null && en2.hasMoreElements(); j++) {
                WfBallot wfballot = (WfBallot) en2.nextElement();
                WTPrincipal wtp = wfballot.getVoter().getPrincipal();
                if (wtp instanceof WTUser) {

                    str = ((WTUser) wtp).getFullName().toString();
                } else if (wtp instanceof WTGroup) {
                    str = ((WTGroup) wtp).getName().toString();
                }
                if (str != null && str.length() > 0) {
                    if (field == "") {
                        field = str;
                    } else {
                        field = field + ";" + str;
                    }
                }
            }
        }
        return field;
    }

    /**
     * 获取工艺技术通知单信息，数据包接口5
     *
     * @param productNumbers
     * @return
     * @throws Exception
     */
    public String getProcessNoticeByProductNum(String productNumbers) throws Exception {
        System.out.println("----------DataPackageInterface---getProcessNoticeByProductNum----");
        String[] params = productNumbers.split(";");
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<lists>");

        if (params.length == 0) {
            buffer.append("<exception>");
            buffer.append("输入的产品编号为空，请重新指定！");
            buffer.append("</exception>");
            buffer.append("</lists>");
            return buffer.toString();
        }

        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            String[] strs = param.split(",");
            String productNumber = "";
            String phaseCode = "";
            String stage = "";
            if (strs.length < 1) {
                buffer.append("<exception>");
                buffer.append("所传参数不合法，请重新指定！");
                buffer.append("</exception>");
                buffer.append("</lists>");
                return buffer.toString();
            }
            if (strs.length == 1) {
                productNumber = strs[0];
            }
            if (strs.length == 2) {
                productNumber = strs[0];//产品代号
                phaseCode = strs[1];
            }
            if (strs.length == 3) {
                productNumber = strs[0];//产品代号
                phaseCode = strs[1];    //阶段
                stage = strs[2];        //批次
            }
            boolean isComparephaseCode = false;
            if (phaseCode != null && !"".equals(phaseCode) && !"null".equals(phaseCode)) {
                isComparephaseCode = true;
            }
            List<WTDocument> list = getDocumentByTypeAndIBAValue("casc.sast.149.PROCESS_NOTICE", "PINDEX", productNumber);
            if (list.size() == 0) {
                buffer.append("<technics>");
                buffer.append("不存在产品代号为：" + productNumber + " 的技术通知单！");
                buffer.append("</technics>");
                buffer.append("</lists>");
                return buffer.toString();
            }
            for (WTDocument doc : list) {
                IBAUtility docUtility = new IBAUtility(doc);
                String docPhaseCode = docUtility.getIBAValue("PHASE_CODE");
                if (docPhaseCode == null) {
                    docPhaseCode = "";
                }

                boolean flag = false;

                if (isComparephaseCode && phaseCode.equals(docPhaseCode)) {
                    flag = true;
                }
                if (!isComparephaseCode) {
                    flag = true;
                }

                if (flag) {
                    String noticeNumber = doc.getNumber(); //通知单编号
					String noticeContent = docUtility.getIBAValue("TONGZHIBIAOTI");  //通知内容
                    if(noticeContent == null){
                        noticeContent = "";
                    }
                    String noticeName = doc.getNumber(); //通知单名称
//                    String noticeContent = "";  //通知内容
                    String noticeReason = docUtility.getIBAValue("CHGREASON");  //通知原因
                    if (noticeReason == null) {
                        noticeReason = "";
                    }
                    String fileNames[] = BomUtil.getWTDocumentPdfUrl(doc); //pdf路径
                    String fileName = "";
                    String trueFileName = "";
                    if (fileNames.length == 2) {
                        fileName = fileNames[0];
                        trueFileName = fileNames[1];
                    }
                    buffer.append("<technics ");
                    buffer.append("noticeNumber=\"" + noticeNumber + "\" noticeName=\"" + noticeName + "\" noticeContent=\"" + noticeContent + "\" noticeReason=\"" + noticeReason + "\" productNumber=\"" + productNumber + "\" pdfUrl=\"" + fileName + "\" trueFileName=\"" + trueFileName + "\">");
                    buffer.append("</technics>");
                }
            }
        }
        buffer.append("</lists>");
        return buffer.toString();
    }

    private String getProcessBianzhiTime(WTObject pbo) {
        QueryResult qrProcs;
        try {
            qrProcs = WfEngineHelper.service.getAssociatedProcesses(pbo, null, null);
            WfProcess proc = null;
            while (qrProcs.hasMoreElements()) {
                WfProcess process = (WfProcess) qrProcs.nextElement();
                if (proc != null) {
                    if (process.getStartTime().after(proc.getStartTime())) {
                        proc = process;
                    }
                } else {
                    proc = process;
                }
            }
            if (proc == null) {
                return null;
            }
            List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
            activityList = PrintHelper.getActivities(proc, activityList);
            Iterator iterator = activityList.iterator();
            while (iterator.hasNext()) {
                WfAssignedActivity wfactivity = (WfAssignedActivity) iterator.next();
                String activityName = wfactivity.getName();
                if (activityName.equals("编制")) {
                    Timestamp endTime = wfactivity.getEndTime();
                    if (endTime != null) {
                        return WTStandardDateFormat.format(wfactivity.getEndTime(), "yyyy/MM/dd");
                    }
                }
            }
        } catch (WTException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return null;
    }

    public String downloadDFMProExcel(String reportName, String str, String partName, String ruleKind, String
            fileBase64) {
        System.out.println("============downloadDFMProExcel==========");
        System.out.println("reportName：" + reportName + ",str：" + str + ",partName：" + partName + ",ruleKind：" + ruleKind);
        boolean enforced = SessionServerHelper.manager.setAccessEnforced(false);
        String message = "";
        FileOutputStream fis = null;
        try {
            EPMDocument epmDoc = BomUtil.getEPMDocumentByName(partName);
            if (epmDoc == null) {
                System.out.println("不存在该模型");
                return "fail";
            }
            WTPart part = BomUtil.getPartByEPMDocument(epmDoc);
            //part = WTPartUtil.getPartByNumberAndView(part.getNumber(), "Manufacturing");

            WTProperties props = WTProperties.getLocalProperties();
            String tempFolder = props.getProperty("wt.temp");
            String filePath = tempFolder + File.separator + reportName;
            byte[] b = Base64.decodeBase64(fileBase64);
            fis = new FileOutputStream(new File(filePath));
            fis.write(b, 0, b.length);
            fis.flush();
            fis.close();
            File file = new File(filePath);
            if (file != null && file.exists()) {
                List<WTDocument> oldDocList = WTDocumentUtil.getDocumnetByNameAndType(reportName, "casc.sast.149.GONGYIJIANCHABAOGAO");
                for (WTDocument oldDoc : oldDocList) {
                    PersistenceHelper.manager.delete(oldDoc);
                }
                WTDocument document = WTDocument.newWTDocument();
                document.setNumber(String.valueOf(System.currentTimeMillis()));
                document.setName(reportName);
                document.setContainer(part.getContainer());
                TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference("casc.sast.149.GONGYIJIANCHABAOGAO");
                document.setTypeDefinitionReference(tdr);
                String docFolder = "/Default/02工艺文件/工艺检查报告";
                FolderUtil.setDocFolder(docFolder, document);
                // 保存为持久对象
                document = (WTDocument) PersistenceHelper.manager.save(document);
                //将excel作为文档主内容
                ContentHolder holder = ContentHelper.service.getContents(document);
                ApplicationData appdata = ApplicationData.newApplicationData(holder);
                appdata.setRole(ContentRoleType.PRIMARY);
                ContentServerHelper.service.updateContent(holder, appdata, filePath);
//				QueryResult descQr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
//            	while(descQr.hasMoreElements()){
//            		WTObject obj2 = (WTObject) descQr.nextElement();
//            		if(obj2 instanceof WTDocument){
//            			WTDocument desDoc = (WTDocument) obj2;
//            			String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(desDoc);
//            			if(docType.endsWith("casc.sast.149.GONGYIJIANCHABAOGAO")){
//            				QueryResult queryresult = PersistenceHelper.manager.find(WTPartDescribeLink.class, part,WTPartDescribeLink.DESCRIBES_ROLE, desDoc);
//            				while(queryresult.hasMoreElements()){
//            					WTPartDescribeLink oldLink = (WTPartDescribeLink) queryresult.nextElement();
//            					PersistenceServerHelper.manager.remove(oldLink);
//            				}
//            			}
//            		}
//            	}
//				WTPartDescribeLink desLink = WTPartDescribeLink.newWTPartDescribeLink(part, document);
//				PersistenceServerHelper.manager.insert(desLink);
//				PersistenceHelper.manager.refresh(desLink);
                message = "success";
            } else {
                message = "fail";
            }
        } catch (FileNotFoundException e) {
            message = "fail";
            e.printStackTrace();
        } catch (IOException e) {
            message = "fail";
            e.printStackTrace();
        } catch (WTException e) {
            message = "fail";
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
            message = "fail";
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            message = "fail";
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforced);
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return message;
    }

    /**
     * 构建根据String软属性查询的子查询语句
     *
     * @param ibaName
     * @param ibaValue
     * @return
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws RemoteException
     */
    public static SubSelectExpression getStringIBAQuery(String ibaName, String ibaValue) throws WTException,
            WTPropertyVetoException, RemoteException {
        // 获取IBA属性定义
        AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
        if (addv == null)
            throw new IBADefinitionException("No IBA Definition: " + ibaName);
        long ibaDefId = addv.getObjectID().getId();
        QuerySpec qs = new QuerySpec();
        int idx = qs.appendClassList(StringValue.class, false);
        qs.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[]{idx},
                false);
        qs.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL,
                ibaDefId), new int[]{idx});
        qs.appendAnd();
        qs.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.EQUAL, ibaValue, false),
                new int[]{idx});
        return new SubSelectExpression(qs);
    }

    public static List<WTDocument> getDocumentByTypeAndIBAValue(String type, String ibaName, String ibaValue) throws
            Exception {
        List<WTDocument> list = new ArrayList<WTDocument>();
        QuerySpec qs = new QuerySpec(WTDocument.class);
        //类型
        TypeUtil.getTypeQuery(WTDocument.class, type, qs);
        qs.appendAnd();
        //iba属性
        qs.setAdvancedQueryEnabled(true);
        int index[] = {0};
        ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
        SubSelectExpression subSelectExpression = getStringIBAQuery(ibaName, ibaValue);
        qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);

        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        qr = new LatestConfigSpec().process(qr);
        while (qr.hasMoreElements()) {
            WTDocument document = (WTDocument) qr.nextElement();
            list.add(document);
        }
        return list;
    }

    /**
     * 获取工艺更改单，数据包接口4
     *
     * @param partNumber
     * @return
     * @throws WTException
     */
    public String getChangeOrder2Info(String number) throws WTException {
        System.out.println("----------DataPackageInterface---getChangeOrder2Info----");
        WTChangeOrder2 changeOrder = getWTChangeOrder2ByNumber(number);
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<lists>");
        if (changeOrder == null) {
            buffer.append("<exception>");
            buffer.append("输入编号的更改单不存在，请重新指定！");
            buffer.append("</exception>");
            buffer.append("</lists>");
            return buffer.toString();
        }else{
            IBAUtility changeOrderUtility = new IBAUtility(changeOrder);
            if (changeOrder != null) {
                //更改单编号
                String changeOrderNumber = PDFUtil.objectToString(changeOrder.getNumber());
                //更改单状态
                String changeOrderState = PDFUtil.objectToString(changeOrder.getState().getState().getDisplay(Locale.CHINA));
                //产品名称
                String productName = PDFUtil.objectToString(changeOrder.getContainerReference().getName());
                //更改前主内容  CHANGEBEFOR
                String changeBeforeContent = PDFUtil.objectToString(changeOrderUtility.getIBAValue("CHANGEBEFOR"));
                //更改后主内容  CHANGEAFTER
                String changeAfterContent = PDFUtil.objectToString(changeOrderUtility.getIBAValue("CHANGEAFTER"));
                //更改原因  CHANGECAUSE
                String changeReason = PDFUtil.objectToString(changeOrderUtility.getIBAValue("CHANGECAUSE"));
                //更改类别  CHANGENOTICETYPE
                String changeCategory = PDFUtil.objectToString(changeOrderUtility.getIBAValue("CHANGENOTICETYPE"));
                //设计更改单号（更改依据）
                String designChangeNum = PDFUtil.objectToString(changeOrderUtility.getIBAValue("DESIGNCHANGENUM"));
                //更改前工艺文件批准人
                String beforeApprover = "";
                QueryResult qResult = ChangeHelper2.service.getChangeablesBefore(changeOrder);
                while (qResult.hasMoreElements()) {
                    Object object = qResult.nextElement();
                    if (object instanceof WTDocument) {
                        WTDocument doc = (WTDocument)object;
                        WfProcess process = null;
                        WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(doc);
                        Iterator it = coll.iterator();
                        if (it.hasNext()) {
                            WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
                            QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(ecn, null, null);
                            while (qrProcs.hasMoreElements()) {
                                process = (WfProcess) qrProcs.nextElement();
                                if (process.getState().equals(WfState.OPEN_RUNNING) || process.getState().equals(WfState.CLOSED_COMPLETED_EXECUTED)) {
                                    beforeApprover = getApprover(process);
                                }
                            }
                        } else {
                            QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(doc, null, null);
                            while (qrProcs.hasMoreElements()) {
                                process = (WfProcess) qrProcs.nextElement();
                                if (process.getState().equals(WfState.OPEN_RUNNING) || process.getState().equals(WfState.CLOSED_COMPLETED_EXECUTED)) {
                                    beforeApprover = getApprover(process);
                                }
                            }
                        }
                    }else if (object instanceof MPMProcessPlan) {
                        WfProcess process = null;
                        MPMProcessPlan pplan = (MPMProcessPlan)object;

                        WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(pplan);
                        Iterator it = coll.iterator();
                        if (it.hasNext()) {
                            WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
                            QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(ecn, null, null);
                            while (qrProcs.hasMoreElements()) {
                                process = (WfProcess) qrProcs.nextElement();
                                if (process.getState().equals(WfState.OPEN_RUNNING) || process.getState().equals(WfState.CLOSED_COMPLETED_EXECUTED)) {
                                    beforeApprover = getApprover(process);
                                }
                            }
                        } else {
                            Collection collection = MPMResourceHelper.service.getAssociatedDescribeDocuments(pplan);
                            Iterator iterator = collection.iterator();
                            while(iterator.hasNext()) {
                                ObjectReference oref = (ObjectReference) iterator.next();
                                WTDocument doc = (WTDocument) oref.getObject();
                                QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(doc, null, null);
                                while (qrProcs.hasMoreElements()) {
                                    process = (WfProcess) qrProcs.nextElement();
                                    if (process.getState().equals(WfState.OPEN_RUNNING) || process.getState().equals(WfState.CLOSED_COMPLETED_EXECUTED)) {
                                        beforeApprover = getApprover(process);
                                    }
                                }
                            }

                        }
                    }
                }
                //工艺落实情况（更改后的工艺文件编号+版本号)
                String processComplateStatus = "";
                String technicsPPName = "";
                //pdf文件名
                String[] fileNames = BomUtil.getChangeOrderPdf(changeOrder);
                String fileName = "";
                String trueFileName = "";
                if (fileNames.length == 2) {
                    fileName = fileNames[0];
                    trueFileName = fileNames[1];
                }
                WTDocument afterDoc = null;
                List cas = CSCChange.getReleatedCA(changeOrder, false);
                for (int j = 0; j < cas.size(); j++) {
                    WTChangeActivity2 ca = (WTChangeActivity2) cas.get(j);
                    ArrayList<WTObject> afters = CSCChange.getCAResultItem(ca);
                    for (int i = 0; i < afters.size(); i++) {
                        WTObject after = afters.get(i);
                        if (after instanceof WTDocument) {
                            afterDoc = (WTDocument) after;
                        }
                    }
                }
                if (afterDoc != null) {
                    IBAUtility afterDocUtility = new IBAUtility(afterDoc);
                    String pplanNumber = afterDocUtility.getIBAValue("PPNUMBER");
                    String version = afterDoc.getIterationDisplayIdentifier().toString();
                    processComplateStatus = pplanNumber + "," + version;
                    String name = afterDoc.getName();
                    technicsPPName = name.substring(0, name.indexOf("("));
                }
                //更改单位
                String changeUnit = "149";
                //批准人
                String[] approve = getApproveTime(changeOrder);
                String approver = "";
                //批准时间
                String approveTime = "";
                if (approve != null) {
                    approver = approve[0];
                    approveTime = approve[1];
                }
                buffer.append("<technics ");
                buffer.append("changeOrderNumber=\"" + changeOrderNumber + "\" changeOrderState=\"" + changeOrderState + "\" productName=\"" + productName + "\" beforeApprover=\"" + beforeApprover + "\" changeBeforeContent=\"" + changeBeforeContent + "\" changeAfterContent=\"" + changeAfterContent + "\" changeReason=\"" + changeReason + "\" changeCategory=\"" + changeCategory + "\" changeBasis=\"" + designChangeNum + "\" technicsPPNumber=\"" + processComplateStatus
                        + "\" technicsName=\"" + technicsPPName + "\" changeUnit=\"" + changeUnit + "\" approver=\"" + approver + "\" pdfUrl=\"" + fileName + "\" trueFileName=\"" + trueFileName + "\" approveTime=\"" + approveTime + "\">");
                buffer.append("</technics>");
            }
        }
        buffer.append("</lists>");
        return buffer.toString();
    }

    /**
     * 根据更改单编号获取文件
     *
     * @param type
     * @return
     * @throws WTException
     * @throws RemoteException
     * @author qianlong
     * @date 2012-10-23
     */
    public static WTChangeOrder2 getWTChangeOrder2ByNumber(String number) throws WTException {
        WTChangeOrder2 ecn = null;
        int index[] = {0};
        QuerySpec qs = new QuerySpec(WTChangeOrder2.class);
        qs.appendWhere(new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.NUMBER, SearchCondition.EQUAL, number), index);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        qr = new LatestConfigSpec().process(qr);
        if (qr.hasMoreElements()) {
            ecn = (WTChangeOrder2) qr.nextElement();
        }
        return ecn;
    }

    private String getApprover(WfProcess proc) {
        try {
            List<WfAssignedActivity> activityList = new ArrayList<WfAssignedActivity>();
            activityList = PrintHelper.getActivities(proc, activityList);
            Iterator iterator = activityList.iterator();
            while (iterator.hasNext()) {
                WfAssignedActivity activity = (WfAssignedActivity) iterator.next();
                String activityName = activity.getName();
                if (activityName.equals("批准")) {
                    String approverName = getPrincipalName(activity);
                    return approverName;
                }
            }
        } catch (WTException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}

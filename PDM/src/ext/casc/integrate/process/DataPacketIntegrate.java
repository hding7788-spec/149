package ext.casc.integrate.process;

import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.TypeUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import ext.casc.change.CSCChange;
import ext.casc.integrate.util.BomUtil;
import ext.casc.util.IBAUtility;
import ext.casc.workflow.PrintHelper;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.*;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTStandardDateFormat;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignment;
import wt.workflow.work.WfBallot;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2018/11/29
 * @ Description：
 * @ Modified By：
 */
public class DataPacketIntegrate {

    private String viewName = "Manufacturing";
    private String tempFilePath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp";
    private int index[] = {0};
    public String TECHNICSNOTICETYPE = "casc.sast.149.PROCESS_NOTICE";

    /**
     * 获取工艺规程清单,数据包接口2
     *
     * @param cpth   产品图号
     * @param cpmc   产品名称
     * @param gywjbh 工艺文件编号
     * @param gywjmc 工艺文件名称
     * @return
     */
    public String getProcessPlanList(String cpth, String cpmc, String gywjbh, String gywjmc) throws WTException, RemoteException, WTPropertyVetoException {
        System.out.println("----------DataPackageInterface---getProcessPlanList----");
        StringBuffer buffer = new StringBuffer();
        IBAUtility ibaUtility;
        List<WTDocument> documentList = new ArrayList<WTDocument>();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<lists>");
        if (cpth != null && !cpth.isEmpty()) {
            WTPart wtPart = WTPartUtil.getPartByNumberAndView(cpth, viewName);
            if (wtPart != null) {
                List<WTDocument> wtDocumentList = getAllApprovedTechnics(wtPart, "");
                for (WTDocument wtDocument : wtDocumentList) {
                    ibaUtility = new IBAUtility(wtDocument);
                    String technicsPPNumber = PDFUtil.objectToString(ibaUtility.getIBAValue("PPNUMBER"));//工艺文件编号
                    String name = wtDocument.getName();
                    if (gywjbh != null && !gywjbh.isEmpty() && gywjmc != null && !gywjmc.isEmpty()) {
                        if (technicsPPNumber.contains(gywjbh) && name.contains(gywjmc)) {
                            documentList.add(wtDocument);
                        }
                    } else if (gywjbh != null && !gywjbh.isEmpty() && (gywjmc == null || gywjmc.isEmpty())) {
                        if (technicsPPNumber.contains(gywjbh)) {
                            documentList.add(wtDocument);
                        }
                    } else if (gywjmc != null && !gywjmc.isEmpty() && (gywjbh == null || gywjbh.isEmpty())) {
                        if (name.contains(gywjmc)) {
                            documentList.add(wtDocument);
                        }
                    }
                }
            }
        } else {
            QueryResult qr = WTDocumentUtil.getDocumentByLikeNumberAndNameType(gywjbh, gywjmc, "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN");
            while (qr.hasMoreElements()) {
                WTDocument document = (WTDocument) qr.nextElement();
                documentList.add(document);
            }
        }
        for (WTDocument wtDocument : documentList) {
            ibaUtility = new IBAUtility(wtDocument);
            String number = wtDocument.getNumber();
            String name = wtDocument.getName();
            String technicsPPNumber = PDFUtil.objectToString(ibaUtility.getIBAValue("PPNUMBER"));//工艺文件编号
            String pici = PDFUtil.objectToString(ibaUtility.getIBAValue("BATCH"));//批次
            String zfType = PDFUtil.objectToString(ibaUtility.getIBAValue("ZFFLAG"));//批次
            String docVersion = wtDocument.getIterationDisplayIdentifier().toString(); //版本
            String bzTime = getProcessBianzhiTime(wtDocument);//编制日期
            String modifier = wtDocument.getModifier().getDisplayName();//上传人
            String modifierTime = WTStandardDateFormat.format(wtDocument.getModifyTimestamp(), "yyyy/MM/dd");//上传时间
            String[] pdfStr = BomUtil.getWTDocumentPdfUrl(wtDocument);
            String fileName = "";
            String trueFileName = "";
            if (pdfStr != null && pdfStr.length == 2) {
                fileName = pdfStr[0];
                trueFileName = pdfStr[1];
            }
            buffer.append("<technics fileName=\"");
            buffer.append(fileName);
            buffer.append("\" trueFileName=\"");
            buffer.append(trueFileName);
            buffer.append("\" technicsPPNumber=\"");
            buffer.append(technicsPPNumber);
            buffer.append("\" technicsName=\"");
            buffer.append(name);
            buffer.append("\" batch=\"");
            buffer.append(pici);
            buffer.append("\" version=\"");
            buffer.append(docVersion);
            buffer.append("\" bzTime=\"");
            buffer.append(bzTime);
            buffer.append("\" uploader=\"");
            buffer.append(modifier);
            buffer.append("\" zfType=\"");
            buffer.append(zfType);
            buffer.append("\" uploadTime=\"");
            buffer.append(modifierTime);
            buffer.append("\"></technics>");
        }
        buffer.append("</lists>");
        return buffer.toString();
    }

    /**
     * 获取工艺文件目录,数据包接口3
     *
     * @param cpth     产品图号
     * @param cpmc     产品名称
     * @param xh       型号
     * @param gywjmlbh 工艺文件目录编号
     * @param gywjmlmc 工艺文件目录名称
     * @return
     * @throws WTException
     */
    public String getTechnicsCatalogs(String cpth, String cpmc, String xh, String gywjmlbh, String gywjmlmc) throws WTException {
        System.out.println("----------DataPackageInterface---getTechnicsCatalogs----");
        System.out.println("partNumber:" + cpth);
        StringBuffer buffer = new StringBuffer();
        List<WTDocument> documentList = new ArrayList<WTDocument>();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<lists>");
        WTPart wtPart;
        IBAUtility ibaUtility;
        FileOutputStream fos = null;
        try {
            if (cpth != null && !cpth.isEmpty()) {
                wtPart = WTPartUtil.getPartByNumberAndView(cpth, viewName);
                if (wtPart != null) {
                    List<WTDocument> docList = BomUtil.getTechnicsCatalogs(wtPart, "TEMP");
                    for (WTDocument wtDocument : docList) {
                        ibaUtility = new IBAUtility(wtDocument);
                        String technicsPPNumber = PDFUtil.objectToString(ibaUtility.getIBAValue("PPNUMBER"));//工艺文件编号
                        String name = wtDocument.getName();
                        if (gywjmlbh != null && !gywjmlbh.isEmpty() && gywjmlmc != null && !gywjmlmc.isEmpty()) {
                            if (technicsPPNumber.contains(gywjmlbh) && name.contains(gywjmlmc)) {
                                documentList.add(wtDocument);
                            }
                        } else if (gywjmlbh != null && !gywjmlbh.isEmpty() && (gywjmlmc == null || gywjmlmc.isEmpty())) {
                            if (technicsPPNumber.contains(gywjmlbh)) {
                                documentList.add(wtDocument);
                            }
                        } else if (gywjmlmc != null && !gywjmlmc.isEmpty() && (gywjmlbh == null || gywjmlbh.isEmpty())) {
                            if (name.contains(gywjmlmc)) {
                                documentList.add(wtDocument);
                            }
                        }
                    }
                }
            } else {
                QueryResult qr = WTDocumentUtil.getDocumentByLikeNumberAndNameType(gywjmlbh, gywjmlmc, "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN|casc.sast.149.reportTechnics");
                while (qr.hasMoreElements()) {
                    WTDocument document = (WTDocument) qr.nextElement();
                    documentList.add(document);
                }
            }
            for (WTDocument document : documentList) {
                ibaUtility = new IBAUtility(document);
                String technicsPPNumber = PDFUtil.objectToString(ibaUtility.getIBAValue("PPNUMBER"));//工艺文件编号
                String pici = PDFUtil.objectToString(ibaUtility.getIBAValue("BATCH"));//批次
                String docVersion = document.getIterationDisplayIdentifier().toString(); //版本
                String[] approve = getApproverAndApproveTime(document);
                String approver = "";//批准人
                String approveTime = "";//批准时间
                if (approve != null) {
                    approver = approve[0];
                    approveTime = approve[1];
                }
                String modifier = document.getModifier().getDisplayName();//上传人
                String modifierTime = WTStandardDateFormat.format(document.getModifyTimestamp(), "yyyy/MM/dd");//上传时间
                String[] pdfStr = BomUtil.getWTDocumentPdfUrl(document);
                String fileName = "";
                String trueFileName = "";
                if (pdfStr != null && pdfStr.length == 2) {
                    fileName = pdfStr[0];
                    trueFileName = pdfStr[1];
                }
                buffer.append("<technics fileName=\"");
                buffer.append(fileName);
                buffer.append("\" trueFileName=\"");
                buffer.append(trueFileName);
                buffer.append("\" technicsPPNumber=\"");
                buffer.append(technicsPPNumber);
                buffer.append("\" batch=\"");
                buffer.append(pici);
                buffer.append("\" version=\"");
                buffer.append(docVersion);
                buffer.append("\" approver=\"");
                buffer.append(approver);
                buffer.append("\" approveTime=\"");
                buffer.append(approveTime);
                buffer.append("\" uploader=\"");
                buffer.append(modifier);
                buffer.append("\" uploadTime=\"");
                buffer.append(modifierTime);
                buffer.append("\"></technics>");
            }
        } catch (PropertyVetoException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        buffer.append("</lists>");
        return buffer.toString();
    }


    /**
     * 获取工艺更改单，数据包接口4
     *
     * @param cpth  产品图号
     * @param cpmc  产品名称
     * @param ggdbh 更改单编号
     * @param cpdh  产品代号
     * @return
     * @throws Exception
     */
    public String getChangeOrderList(String cpth, String cpmc, String ggdbh, String cpdh) throws Exception {
        System.out.println("----------DataPackageInterface---getTechnicsChangeOrders----");
        List<WTChangeOrder2> changeOrder2List = new ArrayList<WTChangeOrder2>();
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<lists>");
        WTPart wtPart;
        IBAUtility partUtility;
        if (cpth != null && !cpth.isEmpty()) {
            wtPart = WTPartUtil.getPartByNumberAndView(cpth, viewName);
            if (wtPart != null) {
                changeOrder2List = BomUtil.getAllChangeOrder2ByLastestDoc(wtPart);

            }
        } else {
            QuerySpec qSpec = new QuerySpec(WTChangeOrder2.class);
            qSpec.appendWhere(new SearchCondition(WTChangeOrder2.class, WTChangeOrder2.NUMBER, SearchCondition.LIKE, ggdbh, false), index);
            QueryResult qResult = PersistenceHelper.manager.find(qSpec);
        	qResult = new LatestConfigSpec().process(qResult);
            while (qResult.hasMoreElements()) {
                WTChangeOrder2 wtChangeOrder2 = (WTChangeOrder2) qResult.nextElement();
                changeOrder2List.add(wtChangeOrder2);
            }
        }
        for (WTChangeOrder2 changeOrder : changeOrder2List) {
            IBAUtility changeOrderUtility = new IBAUtility(changeOrder);
            String partNum = "";
            //部件名称
            String partName = "";
            //更改单编号
            String changeOrderNumber = PDFUtil.objectToString(changeOrder.getNumber());
            //产品代号
            String productIndex = "";
            //产品名称
            String productName = PDFUtil.objectToString(changeOrder.getContainerReference().getName());
            //所属整机或分系统
            String inSys = PDFUtil.objectToString(changeOrder.getContainerReference().getName());
            //更改前状态
            String changeBeforeState = changeOrder.getState().getState().getDisplay(Locale.CHINA);
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
                WTPart part = WTDocumentUtil.getLatestDescribesWTPartsByDocument(afterDoc);
                partUtility = new IBAUtility(part);
                //部件编号
                partNum = part.getNumber();
                //部件名称
                partName = part.getName();
                productIndex = PDFUtil.objectToString(partUtility.getIBAValue("PINDEX"));
            }
            //更改单位
            String changeUnit = "149厂";
            //批准人
            String[] approve = getApproverAndApproveTime(changeOrder);
            String approver = "";
            //批准时间
            String approveTime = "";
            if (approve != null) {
                approver = approve[0];
                approveTime = approve[1];
            }
            buffer.append("<technics changeOrderNumber=\"");
            buffer.append(changeOrderNumber);
            buffer.append("\" pNumber=\"");
            buffer.append(partNum);
            buffer.append("\" pName=\"");
            buffer.append(partName);
            buffer.append("\" productIndex=\"");
            buffer.append(productIndex);
            buffer.append("\" productName=\"");
            buffer.append(productName);
            buffer.append("\" inSys=\"");
            buffer.append(inSys);
            buffer.append("\" changeBeforeState=\"");
            buffer.append(changeBeforeState);
            buffer.append("\" changeBeforeContent=\"");
            buffer.append(changeBeforeContent);
            buffer.append("\" changeAfterContent=\"");
            buffer.append(changeAfterContent);
            buffer.append("\" changeReason=\"");
            buffer.append(changeReason);
            buffer.append("\" changeCategory=\"");
            buffer.append(changeCategory);
            buffer.append("\" processComplateStatus=\"");
            buffer.append(processComplateStatus);
            buffer.append("\" technicsName=\"");
            buffer.append(technicsPPName);
            buffer.append("\" changeUnit=\"");
            buffer.append(changeUnit);
            buffer.append("\" approver=\"");
            buffer.append(approver);
            buffer.append("\" pdfUrl=\"");
            buffer.append(fileName);
            buffer.append("\" trueFileName=\"");
            buffer.append(trueFileName);
            buffer.append("\" approveTime=\"");
            buffer.append(approveTime);
            buffer.append("\"></technics>");
        }

        buffer.append("</lists>");
        return buffer.toString();
    }

    /**
     * 工艺通知单，数据包接口5
     *
     * @param cpth  产品图号
     * @param cpmc  产品名称
     * @param tzdbh 通知单编号
     * @param tzdmc 通知单名称
     * @return
     * @throws Exception
     */
    public String getTechnicsNoticesList(String cpth, String cpmc, String tzdbh, String tzdmc) throws Exception {
        System.out.println("----------DataPackageInterface---getTechnicsNotices----");
        IBAUtility docUtility;
        List<WTDocument> documentList = new ArrayList<WTDocument>();
        QuerySpec qs = new QuerySpec(WTDocument.class);
        TypeUtil.getTypeQuery(WTDocument.class, TECHNICSNOTICETYPE, qs);
        if (tzdbh != null && !tzdbh.isEmpty()) {
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE, tzdbh), index);
        }
        if (tzdmc != null && !tzdmc.isEmpty()) {
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, tzdmc), index);
        }
        if (cpth != null && !tzdmc.isEmpty()) {
            qs.appendAnd();
            qs.setAdvancedQueryEnabled(true);
            ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
            SubSelectExpression subSelectExpression = getStringIBAQuery("PINDEX", cpth);
            qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
        }
        if (cpmc != null && !cpmc.isEmpty()) {

        }
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        qr = new LatestConfigSpec().process(qr);
        while (qr.hasMoreElements()) {
            WTDocument document = (WTDocument) qr.nextElement();
            documentList.add(document);
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        buffer.append("<lists>");
        for (WTDocument document : documentList) {
            docUtility = new IBAUtility(document);
            String fileNames[] = BomUtil.getWTDocumentPdfUrl(document); //pdf路径
            String fileName = "";
            String trueFileName = "";
            if (fileNames.length == 2) {
                fileName = fileNames[0];
                trueFileName = fileNames[1];
            }
            String number = document.getNumber();
            String docVersion = document.getIterationDisplayIdentifier().toString(); //版本
            String noticeContent = docUtility.getIBAValue("TONGZHIBIAOTI") == null ? "" : docUtility.getIBAValue("TONGZHIBIAOTI");  //通知内容
            String noticeReason = docUtility.getIBAValue("CHGREASON") == null ? "" : docUtility.getIBAValue("TONGZHIBIAOTI");  //通知内容
            String[] approve = getApproverAndApproveTime(document);
            String approver = "";//批准人
            String approveTime = "";//批准时间
            if (approve != null) {
                approver = approve[0];
                approveTime = approve[1];
            }
            String modifier = document.getModifier().getDisplayName();//上传人
            String modifierTime = WTStandardDateFormat.format(document.getModifyTimestamp(), "yyyy/MM/dd");//上传时间
            buffer.append("<technics noticeNumber=\"");
            buffer.append(number);
            buffer.append("\" noticeName=\"");
            buffer.append(fileName);
            buffer.append("\" noticeContent=\"");
            buffer.append(noticeContent);
            buffer.append("\" noticeReason=\"");
            buffer.append(noticeReason);
            buffer.append("\" version=\"");
            buffer.append(docVersion);
            buffer.append("\" approver=\"");
            buffer.append(approver);
            buffer.append("\" approveTime=\"");
            buffer.append(approveTime);
            buffer.append("\" uploader=\"");
            buffer.append(modifier);
            buffer.append("\" uploadTime=\"");
            buffer.append(modifierTime);
            buffer.append("\" trueFileName=\"");
            buffer.append(trueFileName);
            buffer.append("\"></technics>");
        }
        buffer.append("</lists>");
        return buffer.toString();
    }

    /**
     * 获取批准人批准时间
     *
     * @param wtObject
     * @return
     */
    private String[] getApproverAndApproveTime(WTObject wtObject) {
        QueryResult qrProcs;
        try {
            qrProcs = WfEngineHelper.service.getAssociatedProcesses(wtObject, null, null);
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

    private List<WTDocument> getDocumentByTypeAndIBAValue(String type, String ibaName, String ibaValue, String containerId) throws
            Exception {

        List<WTDocument> list = new ArrayList<WTDocument>();
        QuerySpec qs = new QuerySpec(WTDocument.class);
        //类型
        TypeUtil.getTypeQuery(WTDocument.class, type, qs);
        if (ibaName != null) {
            qs.appendAnd();
            qs.setAdvancedQueryEnabled(true);
            ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
            SubSelectExpression subSelectExpression = getStringIBAQuery(ibaName, ibaValue);
            qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
        }
        qs.appendAnd();
        qs.appendWhere(new SearchCondition(WTDocument.class, "containerReference.key.id", SearchCondition.EQUAL, containerId), index);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        qr = new LatestConfigSpec().process(qr);
        while (qr.hasMoreElements()) {
            WTDocument document = (WTDocument) qr.nextElement();
            list.add(document);
        }
        return list;
    }

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

    public static List<WTDocument> getAllApprovedTechnics(WTPart part, String technicsType) throws WTException {
        List<WTDocument> list = new ArrayList<WTDocument>();
        QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
        LatestConfigSpec lcs = new LatestConfigSpec();
        qr = lcs.process(qr);
        while (qr.hasMoreElements()) {
            WTDocument document = (WTDocument) qr.nextElement();
            System.out.println(document.getNumber());
            //是工艺文件，不是报表类工艺
            if (TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("PROCESS_PLAN")
                    && !TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("reportTechnics")) {
                document = (WTDocument) VersionControlHelper.service.getLatestIteration(document, true);

                String state = document.getState().toString();
                //状态为已批准
                if ("APPROVED".equals(state)) {
                    list.add(document);
                }
//                Versioned vBase = null;
//                QueryResult qr2  = VersionControlHelper.service.allVersionsOf(document.getMaster());
//                //所有版本
//                while (qr2.hasMoreElements()) {
//                    vBase = (Versioned) qr2.nextElement();
//                    WTDocument doc = (WTDocument)vBase;
//                    String version = doc.getVersionInfo().getIdentifier().getValue();
//                    String state = doc.getState().toString();
//                    System.out.println(state);
//                    //状态为已批准
//                    if("APPROVED".equals(state)){
//                        list.add(doc);
//                    }
//                }
            }
        }
        return list;
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
            e1.printStackTrace();
        }
        return null;
    }
}

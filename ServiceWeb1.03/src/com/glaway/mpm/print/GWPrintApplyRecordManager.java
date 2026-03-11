package com.glaway.mpm.print;

import com.glaway.mpm.constants.ProcessPlanConstants;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.model.data.CmAttachment;
import com.glaway.mpm.parameter.service.gwpersistable.GwPersistenceHelper;
import com.glaway.mpm.parameter.service.gwpersistable.GwQueryResult;
import com.glaway.mpm.parameter.service.gwpersistable.GwQuerySpec;
import com.glaway.mpm.parameter.util.PersistableUtil;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.constants.PrintServerConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.print.data.CmQrCode;
import com.glaway.mpm.print.model.GWPrintApplyRecord;
import com.glaway.mpm.print.sign.Position;
import com.glaway.mpm.print.util.*;
import com.glaway.mpm.util.*;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.casc.access.AccessAdminUtil;
import ext.casc.util.CommonUtil;
import ext.casc.util.WCUtil;
import wt.change2.WTChangeOrder2;
import wt.content.*;
import wt.doc.WTDocument;
import wt.fc.ReferenceFactory;
import wt.fc.*;
import wt.inf.container.WTContainer;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.representation.Representable;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfProcess;

import java.beans.PropertyVetoException;
import java.io.*;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class GWPrintApplyRecordManager {

    private static VaLogger logger = VaLogger.getLogger(GWPrintApplyRecordManager.class.getName());
    private static String number;

    public static String createGwPrintApplyRecord(CmPrintInfoBean cmPrintInfoBean) throws Exception {
        String result = "";
        try {
            if (cmPrintInfoBean != null) {
                GWPrintApplyRecord gwPrintApplyRecord = new GWPrintApplyRecord();
                gwPrintApplyRecord.setBarCode(cmPrintInfoBean.getQrCode());
                gwPrintApplyRecord.setProcessOid(cmPrintInfoBean.getOid());
                gwPrintApplyRecord.setProcessPrintFileOid(cmPrintInfoBean.getProcessPrintFileOid());
                gwPrintApplyRecord.setProcessNumber(cmPrintInfoBean.getFileNumber());
                gwPrintApplyRecord.setPindex(cmPrintInfoBean.getPindex());
                gwPrintApplyRecord.setTs_Baseline(cmPrintInfoBean.getBaseline());
                gwPrintApplyRecord.setPage(cmPrintInfoBean.getPageCount());
                if (cmPrintInfoBean.isBlueCard()) {
                    gwPrintApplyRecord.setIsBlueCard(PrintServerConstants.ISBLUECARD);
                } else {
                    gwPrintApplyRecord.setIsBlueCard(PrintServerConstants.NOTBLUECARD);
                }
                gwPrintApplyRecord.setProcessName(cmPrintInfoBean.getFileName());
                gwPrintApplyRecord.setVersion(cmPrintInfoBean.getVersion());
                gwPrintApplyRecord.setPhaseCode(cmPrintInfoBean.getPhaseCode());
                gwPrintApplyRecord.setFileType(cmPrintInfoBean.getFileType());
                gwPrintApplyRecord.setSecret(cmPrintInfoBean.getSecret());
                gwPrintApplyRecord.setEcnNumber(cmPrintInfoBean.getEcnNumber());
                gwPrintApplyRecord.setTemporarySeal(cmPrintInfoBean.getTemporarySeal());
                gwPrintApplyRecord.setPrintRequire(cmPrintInfoBean.getPrintDescription());
                gwPrintApplyRecord.setPrintStatus(PrintServerConstants.PRINTSTATUS_WDY);
                gwPrintApplyRecord.setRejectStatus(PrintServerConstants.REJECTSTATUS_WBH);
                //设置申请人
                gwPrintApplyRecord.setApplier(cmPrintInfoBean.getCmUser().getOid());
                //设置申请时间
                Date date = new Date();
                gwPrintApplyRecord.setApplyDate(new Timestamp(date.getTime()));
                GwPersistenceHelper.manager.save(gwPrintApplyRecord);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            result = "工艺文件" + cmPrintInfoBean.getFileNumber() + "打印申请失败！\n";
            e.printStackTrace();
        }
        return result;
    }

    public static String updateGwPrintApplyRecord(CmPrintInfoBean cmPrintInfoBean) throws Exception {
        String result = "";
        try {
            if (cmPrintInfoBean != null) {
                GWPrintApplyRecord gwPrintApplyRecord = queryGWPrintApplyRecordByProcessOidAndRejectStatus(cmPrintInfoBean.getOid(), cmPrintInfoBean.getRejectState());
                gwPrintApplyRecord.setTs_Baseline(cmPrintInfoBean.getBaseline());
                if (cmPrintInfoBean.isBlueCard()) {
                    gwPrintApplyRecord.setIsBlueCard(PrintServerConstants.ISBLUECARD);
                } else {
                    gwPrintApplyRecord.setIsBlueCard(PrintServerConstants.NOTBLUECARD);
                }
                gwPrintApplyRecord.setPrintRequire(cmPrintInfoBean.getPrintDescription());
                GwPersistenceHelper.manager.save(gwPrintApplyRecord);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            result = "更新工艺文件" + cmPrintInfoBean.getFileNumber() + "打印申请失败！\n";
            e.printStackTrace();
        }
        return result;
    }

    public static List<GWPrintApplyRecord> queryGWPrintApplyRecordByPrintFileOid(long oid) throws Exception {
        List<GWPrintApplyRecord> list = new ArrayList<GWPrintApplyRecord>();
        GwQuerySpec qs = new GwQuerySpec(GWPrintApplyRecord.class);
        qs.appendWhere(GWPrintApplyRecord.PROCESSPRINTFILEOID, GwQuerySpec.EQUAL, oid);
        GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
        while (qr.hasNext()) {
            GWPrintApplyRecord gwPrintApplyRecord = (GWPrintApplyRecord) qr.next();
            list.add(gwPrintApplyRecord);
        }
        return list;
    }

    public static List<GWPrintApplyRecord> queryGWPrintApplyRecord(CmPrintQueryBean cmPrintQueryBean) throws Exception {
        List<GWPrintApplyRecord> list = new ArrayList<GWPrintApplyRecord>();

        String fileNumber = cmPrintQueryBean.getFileNumber();
        String fileName = cmPrintQueryBean.getFileName();
        String version = cmPrintQueryBean.getVersion();
        String technicsType = cmPrintQueryBean.getTechnicsType();
        String fileType = cmPrintQueryBean.getFileType();
        String phaseCode = cmPrintQueryBean.getPhaseCode();
        String pindex = cmPrintQueryBean.getPindex();
//		String partNumber = cmPrintQueryBean.getPartNumber();
        String cindex = cmPrintQueryBean.getCindex();
        String applyBeginDate = cmPrintQueryBean.getApplyBeginDate();
        String applyOverDate = cmPrintQueryBean.getApplyOverDate();
        String applyDataCheck = cmPrintQueryBean.getApplyDateCheck();
        String printDataCheck = cmPrintQueryBean.getPrintDateCheck();
        String printBeginDate = cmPrintQueryBean.getPrintBeginDate();
        String printOverDate = cmPrintQueryBean.getPrintOverDate();
        String fileState = cmPrintQueryBean.getFileState();

        GwQuerySpec qs = new GwQuerySpec(GWPrintApplyRecord.class);

        qs.appendWhere(GWPrintApplyRecord.BARCODE, GwQuerySpec.IS_NOT_NULL, "");

        if (applyDataCheck.equals("true")) {
            Timestamp fromTime = Timestamp.valueOf(applyBeginDate + " 08:00:00");
            Timestamp overTime = Timestamp.valueOf(applyOverDate + " 08:00:00");

            qs.appendAnd();
            qs.appendWhere(GWPrintApplyRecord.APPLYDATE, GwQuerySpec.GREATER_THAN, fromTime);
            qs.appendAnd();
            qs.appendWhere(GWPrintApplyRecord.APPLYDATE, GwQuerySpec.LESS_THAN, overTime);
        }

        if (printDataCheck.equals("true")) {
            Timestamp fromTime = Timestamp.valueOf(printBeginDate + " 08:00:00");
            Timestamp overTime = Timestamp.valueOf(printOverDate + " 08:00:00");

            qs.appendAnd();
            qs.appendWhere(GWPrintApplyRecord.PRINTDATE, GwQuerySpec.GREATER_THAN, fromTime);
            qs.appendAnd();
            qs.appendWhere(GWPrintApplyRecord.PRINTDATE, GwQuerySpec.LESS_THAN, overTime);
        }

        if (!fileNumber.equals("")) {
            qs.appendAnd();
            qs.appendWhere(GWPrintApplyRecord.PROCESSNUMBER, GwQuerySpec.LIKE, fileNumber);
        }

        if (!fileName.equals("")) {
            qs.appendAnd();
            qs.appendWhere(GWPrintApplyRecord.PROCESSNAME, GwQuerySpec.LIKE, fileName);
        }

        if (!version.equals("")) {
            qs.appendAnd();
            qs.appendWhere(GWPrintApplyRecord.VERSION, GwQuerySpec.LIKE, version);
        }

        if (!fileType.equals("")) {
            qs.appendAnd();
            //其它报告类型
            if (fileType.equals(PrintServerConstants.FILETYPE_QTBG)) {
                List<String> fileList = new ArrayList<String>();
                String[] fileTypes = LoadPropertiesConfig.getInstance(7).getQTBGVFileTypeValue();
                for (String fileTypeString : fileTypes) {
                    fileList.add(fileTypeString);
                }
                qs.appendOpenParen();
                for (int i = 0; i < fileList.size(); i++) {
                    qs.appendWhere(GWPrintApplyRecord.FILETYPE, GwQuerySpec.LIKE, fileList.get(i));
                    if (i < fileList.size() - 1) {
                        qs.appendOr();
                    } else {
                        qs.appendCloseParen();
                    }
                }
            } else {
                qs.appendWhere(GWPrintApplyRecord.FILETYPE, GwQuerySpec.LIKE, fileType);
            }
        }

        if (!phaseCode.equals("")) {
            qs.appendAnd();
            qs.appendWhere(GWPrintApplyRecord.PHASECODE, GwQuerySpec.LIKE, phaseCode);
        }

        if (!pindex.equals("")) {
            qs.appendAnd();
            qs.appendWhere(GWPrintApplyRecord.PINDEX, GwQuerySpec.LIKE, pindex);
        }

        if (fileState != null && fileState.length() > 0) {
            qs.appendAnd();
            if (fileState.equals(PrintServerConstants.REJECTSTATUS_YBH)) {
                qs.appendWhere(GWPrintApplyRecord.REJECTSTATUS, GwQuerySpec.EQUAL, fileState);
            } else {
                qs.appendWhere(GWPrintApplyRecord.PRINTSTATUS, GwQuerySpec.EQUAL, fileState);
                qs.appendAnd();
                qs.appendWhere(GWPrintApplyRecord.REJECTSTATUS, GwQuerySpec.NOT_EQUAL, PrintServerConstants.REJECTSTATUS_YBH);
            }
        }

        GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
        while (qr.hasNext()) {
            GWPrintApplyRecord gwPrintApplyRecord = (GWPrintApplyRecord) qr.next();
            String objectOid = gwPrintApplyRecord.getProcessOid();
            Persistable per = PersistableUtil.getPersistable(objectOid);
            if (!technicsType.equals("") || !cindex.equals("")) {
                if (per != null && per instanceof MPMProcessPlan) {
                    MPMProcessPlan mpmProcessPlan = (MPMProcessPlan) per;
                    String mbaProcessType = CommonUtil.objectToString(MBAUtil.getValue(mpmProcessPlan, ProcessPlanConstants.MBA_PROCESSTYPE));
                    String mbaCindex = CommonUtil.objectToString(MBAUtil.getValue(mpmProcessPlan, ProcessPlanConstants.MBA_CINDEX));
                    if (!technicsType.equals("") && cindex.equals("")) {
                        if (mbaProcessType.equals(technicsType)) {
                            list.add(gwPrintApplyRecord);
                        }
                    } else if (technicsType.equals("") && !cindex.equals("")) {
                        if (mbaCindex.equals(cindex)) {
                            list.add(gwPrintApplyRecord);
                        }
                    } else if (!technicsType.equals("") && !cindex.equals("")) {
                        if (mbaProcessType.equals(technicsType) && mbaCindex.equals(cindex)) {
                            list.add(gwPrintApplyRecord);
                        }
                    }
                }
            } else {
                list.add(gwPrintApplyRecord);
            }
        }
        return list;
    }

    public static WTDocument createProcessPrintDoc(List<CmPrintInfoBean> list) throws Exception {
        // TODO Auto-generated method stub
        CmPrintInfoBean cmPrintInfoBean = list.get(0);
        WTDocument doc = null;
        try {
            Persistable per = PersistableUtil.getPersistable(cmPrintInfoBean.getOid());
            WTContainer wtContainer = null;
            if (per instanceof MPMProcessPlan) {
                MPMProcessPlan mpmProcessPlan = (MPMProcessPlan) per;
                wtContainer = mpmProcessPlan.getContainer();
            } else if (per instanceof WTChangeOrder2) {
                WTChangeOrder2 ecn = (WTChangeOrder2) per;
                wtContainer = ecn.getContainer();
            } else if (per instanceof WTDocument) {
                WTDocument document = (WTDocument) per;
                wtContainer = document.getContainer();
            }
            doc = WTDocumentUtil.createDocument(null, PrintServerConstants.OBJTYPE_PROCESSPRINTDOC_DISPLAY, wtContainer, PrintServerConstants.PROCESSPRINTDOC_LOCATION, PrintServerConstants.OBJTYPE_PROCESSPRINTDOC);
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return doc;
    }

    public static File generalPDFFile(List<CmPrintInfoBean> list, long docOid) throws Exception {
        if (list != null) {
            String zipPath = FileUtil.getWncTmpDir(PrintServerConstants.FOLDER_PRINT);
            String signPdfPath = FileUtil.makeWncTmpDir(PrintServerConstants.FOLDER_PRINT + File.separator + String.valueOf(docOid) + File.separator + PrintServerConstants.FOLDER_SIGNPDF);
            String printQRCodePath = FileUtil.makeWncTmpDir(PrintServerConstants.FOLDER_PRINT + File.separator + String.valueOf(docOid) + File.separator + PrintServerConstants.FOLDER_PRINT_QRCODE);
            String printPDFPath = FileUtil.makeWncTmpDir(PrintServerConstants.FOLDER_PRINT + File.separator + String.valueOf(docOid) + File.separator + PrintServerConstants.FOLDER_PRINT_PDF);
            for (CmPrintInfoBean infoBean : list) {
                String distributeDeptAndCount = infoBean.getDistributeDeptAndCount();
                String baseline = infoBean.getBaseline();
                String fileType = infoBean.getFileType();
                //下载二维码图片
                CmQrCode qrCode = infoBean.getCmQrCode();
                CmAttachment qrCodeAttach = qrCode.getAttachment();
                String qrCodeFilePath = printQRCodePath + File.separator + qrCodeAttach.getFileName();
                FileUtil.writeBytes(qrCodeFilePath, qrCodeAttach.getBytes());

                //下载PDF文件
                Persistable per = PersistableUtil.getPersistable(infoBean.getOid());
                ApplicationData appData = null;
                if (per instanceof MPMProcessPlan
                        || per instanceof WTDocument) {
                    appData = WCUtil.getRepresentation((Representable) per);
                }

                if (appData == null) {
                    appData = PrintUtil.getPDFFile((ContentHolder) per);
                }

                if (appData != null) {
                    byte[] bytes = CommonUtil.applicationDataToByte(appData);
                    InputStream istream = ContentServerHelper.service.findContentStream(appData);
                    String pdfFilePath = printPDFPath + File.separator + appData.getFileName();
                    FileUtil.writeBytes(pdfFilePath, bytes);

                    String temporarySeal = CommonUtil.objectToString(infoBean.getTemporarySeal());
                    List<Position> positions = PrintPDFUtil.getConfigPositions(per, distributeDeptAndCount, baseline, qrCode, String.valueOf(docOid), temporarySeal, fileType, false);
                    String targetPdfPath = signPdfPath + File.separator + docOid + "_sign.pdf";
                    PrintPDFUtil.signature(istream, targetPdfPath, positions, 0, 0);

                    PrintRecordHelper.service.uploadPDFFile((ContentHolder) per, new File(targetPdfPath));
                }
            }
            String targetZipPath = zipPath + File.separator + docOid + ProcessPlanConstants.ZIP;
            ZipUtil.compress(signPdfPath, targetZipPath);
            return new File(targetZipPath);
        }
        return null;
    }

    public static String uploadPDFFile(ContentHolder holder, File file) {
        Transaction tx = new Transaction();
        boolean access = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            tx.start();
            holder = (ContentHolder) PersistenceHelper.manager.refresh(holder);
            byte[] bytes = FileUtil.readFilePathToByte(file);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            ProcessPlanUtil.delete(holder, file.getName());
            ContentHolder content = ContentHelper.service.getContents(holder);
            content = (ContentHolder) PersistenceHelper.manager.refresh(content);
            ApplicationData data = ApplicationData.newApplicationData(content);
            data.setRole(ContentRoleType.SECONDARY);
            data.setFileName(file.getName());
            data.setUploadedFromPath(file.getPath());
            data = ContentServerHelper.service.updateContent(holder, data, inputStream, true);
            PersistenceServerHelper.manager.update(data);
            if (inputStream != null) {
                inputStream.close();
            }
            tx.commit();
            tx = null;
        } catch (ObjectNoLongerExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (tx != null)
                tx.rollback();

            SessionServerHelper.manager.setAccessEnforced(access);
        }
        return "";
    }

    public static WTUser getPrintor(long oid) throws WTException {
        return (WTUser) PersistableUtil.getPersistable(PrintServerConstants.OID_WTUSER + oid);
    }

    public static List<GWPrintApplyRecord> queryGWPrintApplyRecordByProcessOid(String processOid, String fileState) throws Exception {
        List<GWPrintApplyRecord> gwPrintApplyRecords = new ArrayList<GWPrintApplyRecord>();
        GWPrintApplyRecord gwPrintApplyRecord = null;
        GwQuerySpec qs = new GwQuerySpec(GWPrintApplyRecord.class);
        qs.appendWhere(GWPrintApplyRecord.PROCESSOID, GwQuerySpec.EQUAL, processOid);

        if (fileState.length() > 0) {
            qs.appendAnd();
            qs.appendWhere(GWPrintApplyRecord.PRINTSTATUS, GwQuerySpec.EQUAL, fileState);
        }

        GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
        while (qr.hasNext()) {
            gwPrintApplyRecord = (GWPrintApplyRecord) qr.next();
            gwPrintApplyRecords.add(gwPrintApplyRecord);
        }
        return gwPrintApplyRecords;
    }

    public static GWPrintApplyRecord queryGWPrintApplyRecordByProcessOidAndRejectStatus(String processOid, String rejectStaus) throws Exception {
        GWPrintApplyRecord gwPrintApplyRecord = null;
        GwQuerySpec qs = new GwQuerySpec(GWPrintApplyRecord.class);
        qs.appendWhere(GWPrintApplyRecord.PROCESSOID, GwQuerySpec.EQUAL, processOid);
        qs.appendAnd();
        qs.appendWhere(GWPrintApplyRecord.REJECTSTATUS, GwQuerySpec.EQUAL, rejectStaus);
        GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
        while (qr.hasNext()) {
            gwPrintApplyRecord = (GWPrintApplyRecord) qr.next();
        }
        return gwPrintApplyRecord;
    }

    public static GWPrintApplyRecord queryGWPrintApplyRecordByQRCode(String qrCodeNumber) throws Exception {
        GwQuerySpec qs = new GwQuerySpec(GWPrintApplyRecord.class);
        qs.appendWhere(GWPrintApplyRecord.BARCODE, GwQuerySpec.EQUAL, qrCodeNumber);
        GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
        GWPrintApplyRecord gwPrintApplyRecord = null;
        if (qr.hasNext()) {
            gwPrintApplyRecord = (GWPrintApplyRecord) qr.next();
        }
        return gwPrintApplyRecord;
    }

    public static List<GWPrintApplyRecord> queryGWPrintApplyRecordByProcessOidAndBarCode(String processOid, String barCode) throws Exception {
        List<GWPrintApplyRecord> gwPrintApplyRecords = new ArrayList<GWPrintApplyRecord>();
        GWPrintApplyRecord gwPrintApplyRecord = null;
        GwQuerySpec qs = new GwQuerySpec(GWPrintApplyRecord.class);
        qs.appendWhere(GWPrintApplyRecord.PROCESSOID, GwQuerySpec.EQUAL, processOid);

        qs.appendAnd();
        qs.appendWhere(GWPrintApplyRecord.BARCODE, GwQuerySpec.EQUAL, barCode);

        GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
        while (qr.hasNext()) {
            gwPrintApplyRecord = (GWPrintApplyRecord) qr.next();
            gwPrintApplyRecords.add(gwPrintApplyRecord);
        }
        return gwPrintApplyRecords;
    }

    public static String saveGwPrintApplyRecord(List<CmPrintInfoBean> list, String pboOid) {
        //判断基于BOM添加是否有主工艺文件目录 start by zhuhao20180731
        String mainTechnicsName = "";
        for (CmPrintInfoBean cmPrintInfoBean : list) {
            if (cmPrintInfoBean.getMainTechnics() != null && !"".equals(cmPrintInfoBean.getMainTechnics())) {
                mainTechnicsName = cmPrintInfoBean.getMainTechnics();
                break;
            }
        }
        //判断基于BOM添加是否有主工艺文件目录 end by zhuhao20180731
        String result = "";
        DBConnUtil conn = null;
        try {
            WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
            String userName = user.getName();
            String userFullName = user.getFullName();//获取用户名
            String todayDate = DateUtil.getTodayDate("yyyy-MM-dd");
            String todayDate2 = DateUtil.getTodayDate("yyyy/MM/dd");
            boolean start = true;
            WTDocument doc = null;
            result = "修改成功";
            boolean isNew = false;
            if (pboOid == null || "".equals(pboOid)) {

                //创建打印申请文档并启动流程
                WTContainer wtContainer = PrintUtil.getContainerByName("打印分发管理库");
                CmPrintInfoBean printInfoBean = list.get(0);
                String printDocName = printInfoBean.getProcessNumber() + "-" + printInfoBean.getContainerName() + "-" + userFullName + todayDate;
                if (mainTechnicsName != null && !"".equals(mainTechnicsName)) {
                    printDocName = printInfoBean.getProcessNumber() + "-" + printInfoBean.getContainerName() + "-" + mainTechnicsName + "-" + userFullName + todayDate;
                }
                if ("".equals(printDocName)) {
                    printDocName = "打印申请单";
                }
                doc = WTDocumentUtil.createDocument(null, printDocName, wtContainer, "Default/打印申请单", "casc.sast.149.PRINTAPPLYRECORD");
                pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(doc).getId());
                isNew = true;

            } else {
                doc = WTDocumentUtil.getWTDocumentByOid(pboOid);
            }

            //保存到数据库
            conn = new DBConnUtil();
            //保存到GWPRINTAPPLYRECORD
            //删除三张表历史数据 start
            String delSql1 = "DELETE FROM gwprintbarcode WHERE applyrecordid IN " +
                    "(SELECT GWKEYID FROM gwprintdistributerecord WHERE applyrecordid IN " +
                    "(SELECT GWKEYID FROM gwprintapplyrecord WHERE gwprintapplyrecord.pbooid = '" + pboOid + "'))";
            String delSql2 = "DELETE FROM gwprintdistributerecord WHERE applyrecordid IN " +
                    "(SELECT GWKEYID FROM gwprintapplyrecord WHERE gwprintapplyrecord.pbooid = '" + pboOid + "')";
            String delSql3 = "DELETE FROM GWPRINTAPPLYRECORD WHERE PBOOID = '" + pboOid + "'";
            conn.executeUpdate(delSql1);
            conn.executeUpdate(delSql2);
            conn.executeUpdate(delSql3);
            //删除三张表历史数据 end
            for (CmPrintInfoBean cmPrintInfoBean : list) {
                String printStatus = "";//初始打印状态
                String uuid = UUID.randomUUID().toString();
                String category = "";
                if ("ZZ".equals(cmPrintInfoBean.getOutDept())) {
                    category = "厂内纸质";
                    printStatus = "已打印";
                } else if ("NULL".equals(cmPrintInfoBean.getOutDept())) {
                    category = "厂内电子";
                    printStatus = "未打印";
                } else {
                    category = cmPrintInfoBean.getOutDept();
                    printStatus = "已打印";
                }
                String deptAndCount = cmPrintInfoBean.getDistributeDeptAndCount();
                String batch = cmPrintInfoBean.getTemporarySeal();
                String value = "'" + uuid + "','" + cmPrintInfoBean.getDocVR() + "','" + cmPrintInfoBean.getFileNumber() + "','" + cmPrintInfoBean.getFileName()
                        + "','" + cmPrintInfoBean.getVersion() + "','" + cmPrintInfoBean.getPhaseCode() + "','" + cmPrintInfoBean.getSecret()
                        + "','" + batch + "','" + userName + "','" + todayDate2 + "','" + "分发中" + "','" + deptAndCount + "','"
                        + cmPrintInfoBean.getTechnicsNumber() + "','" + cmPrintInfoBean.getFileType() + "','" + pboOid + "','" + category + "','"
                        + String.valueOf(cmPrintInfoBean.isProcessfile()) + "','" + String.valueOf(cmPrintInfoBean.isAddFormBOM()) + "'";
                String sql = "INSERT INTO GWPRINTAPPLYRECORD " +
                        "(GWKEYID,DOCVR,DOCNUMBER,DOCNAME,VERSION,PHASECODE,SECRET,BATCH,APPLIER,APPLYDATE," +
                        "PRINTSTATUS,DISMESSAGE,TECHNICSNUMBER,FILETYPE,PBOOID,OUTDEPT,PROCESSFILE,FROMBOM) " +
                        "values(" + value + ")";
                conn.executeUpdate(sql);
                //保存到GWPRINTDISTRIBUTERECORD
                String sql2 = "";
                String dept = "";
                String count = "";
                if (deptAndCount.contains(",")) {
                    String[] dac = deptAndCount.split(",");
                    for (int i = 0; i < dac.length; i++) {
                        String uuid2 = UUID.randomUUID().toString();
                        dept = dac[i].substring(0, dac[i].lastIndexOf(":"));
                        count = dac[i].substring(dac[i].lastIndexOf(":") + 1, dac[i].length() - 1);
                        String value2 = "'" + uuid2 + "','" + cmPrintInfoBean.getDocVR() + "','" + uuid + "','" + dept + "','" + count + "'";
                        sql2 = "insert into GWPRINTDISTRIBUTERECORD (GWKEYID,DOCVR,APPLYRECORDID,DISTRIBUTEDEPT,DISTRIBUTEQUANTITY) " +
                                "values(" + value2 + ")";
                        conn.executeUpdate(sql2);
                        saveGWPRINTBARCODE(count, uuid2, dept, conn, batch, printStatus);
                    }
                } else {
                    String uuid2 = UUID.randomUUID().toString();
                    dept = deptAndCount.substring(0, deptAndCount.lastIndexOf(":"));
                    count = deptAndCount.substring(deptAndCount.lastIndexOf(":") + 1, deptAndCount.length() - 1);
                    String value2 = "'" + uuid2 + "','" + cmPrintInfoBean.getDocVR() + "','" + uuid + "','" + dept + "','" + count + "'";
                    sql2 = "insert into GWPRINTDISTRIBUTERECORD (GWKEYID,DOCVR,APPLYRECORDID,DISTRIBUTEDEPT,DISTRIBUTEQUANTITY) " +
                            "values(" + value2 + ")";
                    conn.executeUpdate(sql2);
                    saveGWPRINTBARCODE(count, uuid2, dept, conn, batch, printStatus);
                }
            }
            conn.commit();

            if (isNew) {
                //启动流程
                start = WorkflowUtil.startProcess(doc, "打印申请分发流程", "打印申请分发流程");
                result = "已创建打印申请单,打印申请分发流程已启动";
            }

        } catch (WTException e) {
            result = "创建打印申请单失败,请联系管理员";
            e.printStackTrace();
        } catch (RemoteException e) {
            result = "创建打印申请单失败,请联系管理员";
            e.printStackTrace();
        } catch (WTPropertyVetoException e) {
            result = "创建打印申请单失败,请联系管理员";
            e.printStackTrace();
        } catch (Exception e) {
            result = "创建打印申请单失败,请联系管理员";
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
        return result;
    }


    private static void saveGWPRINTBARCODE(String count, String uuid2, String dept, DBConnUtil conn, String batch, String printStatus) throws SQLException {
        //保存到GWPRINTBARCODE
        int printCount = Integer.parseInt(count);
        for (int i = 0; i < printCount; i++) {
            String uuid3 = UUID.randomUUID().toString();
            String value3 = "'" + uuid3 + "','" + uuid2 + "','" + dept + "','" + printStatus + "','" + batch + "'";
            String sql3 = "insert into GWPRINTBARCODE (GWKEYID,APPLYRECORDID,GDEPT,FILESTATUS,BATCH) " +
                    "values(" + value3 + ")";
            conn.executeUpdate(sql3);
        }
    }

    public static String startPrintOffSet(List<CmPrintInfoBean> list) {
        String result = "启动流程失败,请联系管理员";
        String printInfo = "";
        String pbooid = list.get(0).getPbooid();//打印分发流程pbooid
        String persistableOid = PrintServerConstants.OID_WTDOCUMENT + pbooid;
        Object obj = findObject(persistableOid);
        if (obj == null) {
            persistableOid = PrintServerConstants.OID_WTCHANGEORDER2 + pbooid;
            obj = findObject(persistableOid);
        }
//			Persistable persistable = PersistableUtil.getPersistable(persistableOid);
        if (obj == null) {
            return result;
        }
        for (CmPrintInfoBean bean : list) {
            String offSet = bean.getOffSet();//补打信息
            String oldSet = bean.getDistributeDeptAndCount();//已经分发的部门和份数
            String id = bean.getOid();//打印分发表id
            if (id.contains(":")) {
                id = PrintDataQueryUtil.getKeyidByOid(id);
            }
            //保存到数据库
            String barcodeId = PrintDataBuildUtil.saveOffSetInfo(id, offSet, oldSet);
            String info = id + "@" + oldSet + "#" + barcodeId;
            if ("".equals(printInfo)) {
                printInfo = info;
            } else {
                printInfo = printInfo + "&" + info;
            }
        }
        //启动补打流程 start
        if (obj instanceof WTDocument) {
            WTDocument document = (WTDocument) obj;
            PrintWorkflowUtil.startOffSetProcess(document.getContainerReference(), document, PrintServerConstants.WORKFLOWNAME_PRINTOFFSET, printInfo);
        } else if (obj instanceof WTChangeOrder2) {
            WTChangeOrder2 changeOrder = (WTChangeOrder2) obj;
            PrintWorkflowUtil.startOffSetProcess(changeOrder.getContainerReference(), changeOrder, PrintServerConstants.WORKFLOWNAME_PRINTOFFSET, printInfo);
        }
        //启动补打流程 end
        result = "打印申请补打流程启动成功！";
        return result;
    }

    //根据oid获得对象
    public static WTObject findObject(String oid) {
        Object obj = null;
        if (oid == null || oid.equals("")) {
            return null;
        }
        ReferenceFactory factory = new ReferenceFactory();
        WTReference reference = null;
        try {
            reference = factory.getReference(oid);
            obj = reference.getObject();
        } catch (Exception e) {

        }
        return (WTObject) obj;
    }

    public static boolean checkUser(String dept, Object self, Object pbo) throws Exception {
        System.out.println("=========打印流程是否有人校验===========START===========");
        boolean flag = false;
        WTGroup wtGroup = null;
        ObjectReference reference = (ObjectReference) self;
        WfProcess process = (WfProcess) reference.getObject();
        List<String> printDeptList = getPrintDeptList(pbo, process);
        System.out.println("dept" + dept);
        System.out.println("printDeptList" + printDeptList);
        if ("一分厂".equals(dept) && printDeptList.contains("一分厂")) {
            wtGroup = AccessAdminUtil.getGroupByName(PrintConstants.ZLY_1_CHEJIAN);
        } else if ("二分厂".equals(dept) && printDeptList.contains("二分厂")) {
            wtGroup = AccessAdminUtil.getGroupByName(PrintConstants.ZLY_2_CHEJIAN);
        } else if ("三分厂".equals(dept) && printDeptList.contains("三分厂")) {
            wtGroup = AccessAdminUtil.getGroupByName(PrintConstants.ZLY_3_CHEJIAN);
        } else if ("四分厂".equals(dept) && printDeptList.contains("四分厂")) {
            wtGroup = AccessAdminUtil.getGroupByName(PrintConstants.ZLY_4_CHEJIAN);
        } else if ("五分厂".equals(dept) && printDeptList.contains("五分厂")) {
            wtGroup = AccessAdminUtil.getGroupByName(PrintConstants.ZLY_5_CHEJIAN);
        } else if ("六分厂".equals(dept) && printDeptList.contains("六分厂")) {
            wtGroup = AccessAdminUtil.getGroupByName(PrintConstants.ZLY_6_CHEJIAN);
        } else if ("七分厂".equals(dept) && printDeptList.contains("七分厂")) {
            wtGroup = AccessAdminUtil.getGroupByName(PrintConstants.ZLY_7_CHEJIAN);
        } else if ("八分厂".equals(dept) && printDeptList.contains("八分厂")) {
            wtGroup = AccessAdminUtil.getGroupByName(PrintConstants.ZLY_8_CHEJIAN);
        } else if ("九分厂".equals(dept) && printDeptList.contains("九分厂")) {
            wtGroup = AccessAdminUtil.getGroupByName(PrintConstants.ZLY_9_CHEJIAN);
        } else if ("十分厂".equals(dept) && printDeptList.contains("十分厂")) {
            wtGroup = AccessAdminUtil.getGroupByName(PrintConstants.ZLY_10_CHEJIAN);
        } else if ("档案室".equals(dept) && printDeptList.contains("档案室")) {
            wtGroup = AccessAdminUtil.getGroupByName(PrintConstants.ZLY_DANGAN);
        }
        if (wtGroup != null) {
            Enumeration members = wtGroup.members();
            while (members.hasMoreElements()) {
                Object o = members.nextElement();
                if (o instanceof WTUser) {
                    flag = true;
                    System.out.println("flag=======" + flag);
                    return flag;
                }
            }
        }

        System.out.println("=========打印流程是否有人校验===========END===========");
        return flag;
    }

    private static List<String> getPrintDeptList(Object pbo, WfProcess process) throws WTException {
        List<String> printDeptList = new ArrayList<String>();
        DBConnUtil conn = null;
        String sql = "";
        if (PrintConstants.PROCESS_DYSQFF.equals(process.getTemplate().getName())
                || PrintConstants.PROCESS_GYGGDQS.equals(process.getTemplate().getName())
                || PrintConstants.PROCESS_WDGGDQS.equals(process.getTemplate().getName())
                || PrintConstants.PROCESS_GYFABGQSLC.equals(process.getTemplate().getName())
                || PrintConstants.PROCESS_GYTZDQSLC.equals(process.getTemplate().getName())
                || PrintConstants.PROCESS_WXJSXYQSLC.equals(process.getTemplate().getName())) {
            long oid = 0;
            if (pbo instanceof WTDocument) {
                WTDocument document = (WTDocument) pbo;
                oid = document.getPersistInfo().getObjectIdentifier().getId();
            } else if (pbo instanceof WTChangeOrder2) {
                WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
                oid = changeOrder2.getPersistInfo().getObjectIdentifier().getId();
            }
            sql = "select DISMESSAGE from GWPRINTAPPLYRECORD where PBOOID = '" + oid + "'";
        } else if (PrintConstants.PROCESS_CNGYWJHSLC.equals(process.getName())) {
            ProcessData processData = process.getContext();
            boolean isFromRecover = (Boolean)processData.getValue("isFromRecover");
//            if (isFromRecover) {
                //ture 为手动发起的回收流程
                String infor = (String) processData.getValue("infor");
                if (infor != null && !infor.isEmpty()) {
                    String[] infors = infor.split(":");
                    StringBuffer stringBuffer = new StringBuffer("select * from GWPRINTBARCODE where");
                    for (int i = 0; i < infors.length; i++) {
                        if (i == 0) {
                            stringBuffer.append(" GWKEYID='" + infors[i] + "'");
                        } else {
                            stringBuffer.append(" or GWKEYID='" + infors[i] + "'");
                        }
                    }
                    sql = stringBuffer.toString();

                    try {
                        conn = new DBConnUtil();
                        ResultSet resultSet = conn.executeQuery(sql);
                        while (resultSet.next()) {
                            String dept = resultSet.getString("GDEPT");
                            if (!printDeptList.contains(dept)) {
                                printDeptList.add(dept);
                            }

                        }
                        return printDeptList;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (conn != null) {
                            try {
                                conn.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
//            } else {
//                //false为更改单流程中发起的回收流程
//                String docOid = (String) processData.getValue("docOid");
//                if (docOid != null) {
//                    if (docOid.contains("OR:")) {
//                        docOid = docOid.replace("OR:", "");
//                    }
//                    ReferenceFactory rf = new ReferenceFactory();
//                    ObjectReference self = (ObjectReference) rf.getReference(docOid);
//                    WTDocument beforeDoc = (WTDocument) self.getObject();
//                    if (beforeDoc != null) {
//                        String docNumber = beforeDoc.getNumber();
//                        String version = beforeDoc.getIterationDisplayIdentifier().toString();
//                        sql = "select DISMESSAGE from GWPRINTAPPLYRECORD where DOCNUMBER='" + docNumber + "' and VERSION='" + version + "'";
//                    }
//                }
//
//            }
        }
        System.out.println("checkSql:" + sql);
        if (sql != null && !sql.isEmpty()) {
            try {
                conn = new DBConnUtil();
                ResultSet resultSet = conn.executeQuery(sql);
                while (resultSet.next()) {
                    String disMessage = resultSet.getString("DISMESSAGE");
                    String[] messages = disMessage.split(",");
                    for (String message : messages) {
                        String dept = message.split(":")[0];
                        if (!printDeptList.contains(dept)) {
                            printDeptList.add(dept);
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return printDeptList;
    }

    public static String getPrintInfoByDocOid(String oid) {
        DBConnUtil conn = null;
        String disMessage = "";
        try {
            conn = new DBConnUtil();
            String sql = "select DISMESSAGE from GWPRINTAPPLYRECORD where PBOOID = '" + oid + "'";
            ResultSet resultSet = conn.executeQuery(sql);
            if (resultSet.next()) {
                disMessage = resultSet.getString("DISMESSAGE");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return disMessage;
    }

}

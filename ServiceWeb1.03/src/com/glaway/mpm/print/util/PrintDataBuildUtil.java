package com.glaway.mpm.print.util;

import com.glaway.mpm.constants.DocumentConstants;
import com.glaway.mpm.constants.ProcessPlanConstants;
import com.glaway.mpm.parameter.util.PersistableUtil;
import com.glaway.mpm.print.GWPrintApplyRecordManager;
import com.glaway.mpm.print.GWPrintDistributeRecordManager;
import com.glaway.mpm.print.GWPrintRecoverRecordManager;
import com.glaway.mpm.print.bean.CmPrintDistributerecordBean;
import com.glaway.mpm.print.constants.PrintServerConstants;
import com.glaway.mpm.print.data.*;
import com.glaway.mpm.print.model.GWPrintApplyRecord;
import com.glaway.mpm.print.model.GWPrintDistributeRecord;
import com.glaway.mpm.print.model.GWPrintRecoverRecord;
import com.glaway.mpm.util.*;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.casc.util.CommonUtil;
import ext.casc.util.DBConn;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.inf.container.WTContainer;
import wt.org.WTUser;
import wt.pds.oracle81.OracleDataSource;
import wt.session.SessionHelper;
import wt.util.WTException;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class PrintDataBuildUtil {

    public static CmPrintInfoBean buildCmPrintInfoBean(MPMProcessPlan processPlan, WTChangeOrder2 ecn) throws WTException {
        CmPrintInfoBean printInfoBean = new CmPrintInfoBean();
        printInfoBean.setOid(PrintServerConstants.OID_MPMPROCESSPLAN + processPlan.getPersistInfo().getObjectIdentifier().getId());
        printInfoBean.setFileNumber(CommonUtil.objectToString(MBAUtil.getValue(processPlan, ProcessPlanConstants.MBA_PROCESSNUMBER)));
        if (processPlan.getName().indexOf("(") > 0) {
            printInfoBean.setFileName(processPlan.getName().substring(0, processPlan.getName().indexOf("(")));
        } else {
            printInfoBean.setFileName(processPlan.getName());
        }
        printInfoBean.setFileType(CommonUtil.objectToString(MBAUtil.getValue(processPlan, ProcessPlanConstants.MBA_PROCESSCATEGORY)));
        printInfoBean.setPindex(CommonUtil.objectToString(MBAUtil.getValue(processPlan, ProcessPlanConstants.MBA_PINDEX)));
        printInfoBean.setSecret(CommonUtil.objectToString(MBAUtil.getValue(processPlan, ProcessPlanConstants.MBA_SECRET)));
        printInfoBean.setPhaseCode(CommonUtil.objectToString(MBAUtil.getValue(processPlan, ProcessPlanConstants.MBA_PHASECODE)));
        printInfoBean.setVersion(PersistableUtil.getVersion(processPlan));
        printInfoBean.setModifior(UserUtil.getModifier(processPlan).getFullName());
        printInfoBean.setPageCount(PrintUtil.getPDFPageCountByName(processPlan));
        printInfoBean.setBlueCard(true);
//		setPrintInfo(printInfoBean, processPlan);
        String state = "";
        if (ecn != null) {
            printInfoBean.setEcnNumber(ecn.getNumber());
            state = ecn.getState().getState().getDisplay(Locale.CHINA);
        } else {
            printInfoBean.setEcnNumber("");
            state = processPlan.getState().getState().getDisplay(Locale.CHINA);
        }

        if (ProcessPlanConstants.LIFECYCLE_APPROVE.equals(state)) {
            Timestamp approveTime = ProcessPlanUtil.getWTObjectApproveDate(processPlan);
            if (approveTime != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                printInfoBean.setApproveDate(simpleDateFormat.format(approveTime));
            }
        }

        return printInfoBean;
    }

    public static CmPrintInfoBean buildCmPrintInfoBean(WTChangeOrder2 ecn, String fileType) throws WTException {
        IBAHelper helper = new IBAHelper(ecn);
        CmPrintInfoBean printInfoBean = new CmPrintInfoBean();
        String oid = PrintServerConstants.OID_WTCHANGEORDER2 + ecn.getPersistInfo().getObjectIdentifier().getId();
        String ecnNumber = ecn.getNumber();
        String ecnName = ecn.getName();
        printInfoBean.setOid(oid);
        printInfoBean.setFileNumber(ecnNumber);
        printInfoBean.setFileName(ecnName);
        printInfoBean.setTechnicsNumber(ecnNumber);
        printInfoBean.setPhaseCode(helper.getIBAValue(ProcessPlanConstants.MBA_PHASECODE));
        printInfoBean.setPageCount(PrintUtil.getPDFPageCountByName(ecn));
        printInfoBean.setModifior(ecn.getModifier().getFullName());
        printInfoBean.setPindex(helper.getIBAValue(ProcessPlanConstants.MBA_PINDEX));
        printInfoBean.setSecret(helper.getIBAValue(ProcessPlanConstants.MBA_SECRET));
        printInfoBean.setVersion("");
        printInfoBean.setEcnNumber("");
        printInfoBean.setDocVR(PrintServerConstants.OID_WTCHANGEORDER2 + ecn.getPersistInfo().getObjectIdentifier().getId());
        printInfoBean.setFileType(fileType);
//		if(queryFileState(ecnNumber,ecnName,oid)){
//			printInfoBean.setFileState(PrintServerConstants.FILESTATUS_YFF);
//		}else{
//			printInfoBean.setFileState(PrintServerConstants.FILESTATUS_WFF);
//		}
        String state = ecn.getState().getState().getDisplay(Locale.CHINA);
        if (ProcessPlanConstants.LIFECYCLE_APPROVE.equals(state)) {
            Timestamp approveTime = ProcessPlanUtil.getWTObjectApproveDate(ecn);
            if (approveTime != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                printInfoBean.setApproveDate(simpleDateFormat.format(approveTime));
            }
        }
        String containerName = ecn.getContainerName();
        printInfoBean.setContainerName(containerName);
        printInfoBean.setLifeCycle(state);
        String fileState = searchFileState(ecnNumber, ecnName, oid, "");
        printInfoBean.setFileState(fileState);
        if (!"未分发".equals(fileState)) {
            String dismessage = searchDismessage2(ecnNumber, ecnName, oid, "");
            printInfoBean.setDistributeDeptAndCount(dismessage);
        }
        return printInfoBean;
    }

    public static List<CmPrintInfoBean> buildCmPrintInfoBeanByPrintApplyRecord(long oid, String type) throws Exception {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        List<GWPrintApplyRecord> gwPrintApplyRecords = GWPrintApplyRecordManager.queryGWPrintApplyRecordByPrintFileOid(oid);
        if (gwPrintApplyRecords.size() == 0)
            return list;

        for (GWPrintApplyRecord gwPrintApplyRecord : gwPrintApplyRecords) {
            CmPrintInfoBean printInfoBean = new CmPrintInfoBean();
            //Persistable per = PersistableUtil.getPersistable(gwPrintApplyRecord.getProcessOid());
            //setPrintBasicInfo(printInfoBean, per);
            printInfoBean.setOid(gwPrintApplyRecord.getProcessOid());
            printInfoBean.setFileNumber(gwPrintApplyRecord.getProcessNumber());
            printInfoBean.setFileName(gwPrintApplyRecord.getProcessName());
            printInfoBean.setPindex(gwPrintApplyRecord.getPindex());
            printInfoBean.setVersion(gwPrintApplyRecord.getVersion());
            printInfoBean.setPhaseCode(gwPrintApplyRecord.getPhaseCode());
            printInfoBean.setFileType(gwPrintApplyRecord.getFileType());
            printInfoBean.setBaseline(gwPrintApplyRecord.getTs_Baseline());
            printInfoBean.setPageCount(gwPrintApplyRecord.getPage());
            printInfoBean.setBlueCard(Boolean.parseBoolean(gwPrintApplyRecord.getIsBlueCard()));
            printInfoBean.setEcnNumber(gwPrintApplyRecord.getEcnNumber());
            printInfoBean.setSecret(gwPrintApplyRecord.getSecret());
            List<GWPrintDistributeRecord> gwPrintDistributeRecords = GWPrintDistributeRecordManager.queryGWPrintDistributeRecordByBarCode(gwPrintApplyRecord.getBarCode());
            String distributeDeptAndQuanity = GWPrintDistributeRecordManager.getDistributeDeptAndQuanity(gwPrintDistributeRecords);
            printInfoBean.setDistributeDeptAndCount(distributeDeptAndQuanity);
            printInfoBean.setQrCode(gwPrintApplyRecord.getBarCode());
            printInfoBean.setPrintState(gwPrintApplyRecord.getPrintStatus());
            if (gwPrintApplyRecord.getPrintor() != 0) {
                WTUser user = GWPrintApplyRecordManager.getPrintor(gwPrintApplyRecord.getPrintor());
                printInfoBean.setPrinter(user.getFullName());
            }
            if (gwPrintApplyRecord.getPrintDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                printInfoBean.setPrintDate(dateFormat.format(gwPrintApplyRecord.getPrintDate()));
            }
            printInfoBean.setRejectState(gwPrintApplyRecord.getRejectStatus());
            printInfoBean.setRejectRemark(gwPrintApplyRecord.getRejectRemark());
            list.add(printInfoBean);
        }
        return list;
    }

    public static CmPrintInfoBean buildCmPrintInfoBean(WTDocument doc, String fileType) throws WTException {
        IBAHelper helper = new IBAHelper(doc);
        CmPrintInfoBean printInfoBean = new CmPrintInfoBean();
        String oid = PrintServerConstants.OID_WTDOCUMENT + doc.getPersistInfo().getObjectIdentifier().getId();
        String ppnumber = doc.getNumber();
        String ppname = doc.getName();
        String version = PersistableUtil.getVersion(doc);
        printInfoBean.setOid(oid);
        printInfoBean.setFileNumber(ppnumber);
        printInfoBean.setFileName(ppname);
        printInfoBean.setTechnicsNumber(doc.getNumber());
        printInfoBean.setFileType(fileType);
        printInfoBean.setPindex(helper.getIBAValue(DocumentConstants.IBA_PINDEX));
        printInfoBean.setSecret(helper.getIBAValue(DocumentConstants.IBA_SECRET));
        printInfoBean.setPhaseCode(helper.getIBAValue(DocumentConstants.IBA_PHASECODE));
        printInfoBean.setVersion(version);
//        printInfoBean.setPageCount(PrintUtil.getPDFPageCountByName(doc));
        printInfoBean.setPageCount(helper.getIBAValue("PAGE"));
        printInfoBean.setProcessNumber(helper.getIBAValue(DocumentConstants.IBA_NUMBER));
        if (queryFileState(ppnumber, ppname, oid, version)) {
            printInfoBean.setFileState(PrintServerConstants.FILESTATUS_YFF);
        } else {
            printInfoBean.setFileState(PrintServerConstants.FILESTATUS_WFF);
        }
        printInfoBean.setCompileDept(helper.getIBAValue(DocumentConstants.IBA_DEPT));
        printInfoBean.setDocVR(oid);
        String state = doc.getState().getState().getDisplay(Locale.CHINA);
        if (ProcessPlanConstants.LIFECYCLE_APPROVE.equals(state)) {
            Timestamp approveTime = ProcessPlanUtil.getWTObjectApproveDate(doc);
            if (approveTime != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                printInfoBean.setApproveDate(simpleDateFormat.format(approveTime));
            }
        }
        String containerName = doc.getContainerName();
        printInfoBean.setContainerName(containerName);
        printInfoBean.setLifeCycle(state);
        String fileState = searchFileState(ppnumber, ppname, oid, version);
        printInfoBean.setFileState(fileState);
        if (!"未分发".equals(fileState)) {
            String dismessage = searchDismessage2(ppnumber, ppname, oid, version);
            String pbooid = searchPbooid(ppnumber, ppname, oid, version);
            printInfoBean.setDistributeDeptAndCount(dismessage);
            printInfoBean.setPbooid(pbooid);
        }
        return printInfoBean;
    }

    private static String searchPbooid(String ppnumber, String ppname, String oid, String version) {
        String pbooid = "";
        DBConn conn = null;
        try {
            conn = new DBConn();
            StringBuffer sb = new StringBuffer();
            if (oid != null && !"".equals(oid)) {
                sb.append("SELECT PBOOID FROM GWPRINTAPPLYRECORD WHERE DOCVR = '");
                sb.append(oid);
                sb.append("'");

            } else {
                sb.append("SELECT PBOOID FROM GWPRINTAPPLYRECORD WHERE DOCNUMBER = '" + ppnumber + "' ");
                sb.append("AND DOCNAME = '" + ppname + "'");
                if (version != null && !"".equals(version)) {
                    sb.append(" AND VERSION = '" + version + "'");
                }
            }
            ResultSet rs = conn.executeQuery(sb.toString());
            while (rs.next()) {
                pbooid = rs.getString("PBOOID");
            }
        } catch (Exception e) {
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
        return pbooid;
    }

    /**
     * 查询打印分发表中是否存在数据
     *
     * @param ppnumber 工艺文件编号
     * @param ppname   工艺文件名
     * @param veroid   oid
     * @return
     */
    public static boolean queryFileState(String ppnumber, String ppname, String veroid, String version) {
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            if (!"".equals(veroid) && veroid != null) {
                sb.append("SELECT * FROM GWPRINTAPPLYRECORD WHERE DOCVR = '");
                sb.append(veroid);
                sb.append("'");

            } else {
                sb.append("SELECT * FROM GWPRINTAPPLYRECORD WHERE DOCNUMBER = '" + ppnumber + "' ");
                sb.append("AND DOCNAME = '" + ppname + "'");
                if (version != null && !"".equals(version)) {
                    sb.append(" AND VERSION = '" + version + "'");
                }
            }
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                return true;
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
        return false;
    }

    /**
     * 查询打印分发表中是分发信息
     *
     * @param ppnumber 工艺文件编号
     * @param ppname   工艺文件名
     * @param veroid   oid
     * @param version
     * @return
     */
    public static String searchDismessage(String ppnumber, String ppname, String veroid, String version) {
        String dismessage = "";
        StringBuffer sb = new StringBuffer();
        if (!"".equals(veroid) && veroid != null) {
            sb.append("SELECT DISMESSAGE FROM GWPRINTAPPLYRECORD WHERE DOCVR = '");
            sb.append(veroid);
            sb.append("'");

        } else {
            sb.append("SELECT DISMESSAGE FROM GWPRINTAPPLYRECORD WHERE DOCNUMBER = '" + ppnumber + "'");
            sb.append("AND DOCNAME = '" + ppname + "'");
            if (version != null && !"".equals(version)) {
                sb.append(" AND VERSION = '" + version + "'");
            }
        }
        List<String> lists = queryDataToPrint(sb.toString());
        if (lists != null && lists.size() > 0) {
            dismessage = lists.get(0);
        }
        return dismessage;
    }

    /**
     * 查询打印分发表中是分发信息
     *
     * @param ppnumber 工艺文件编号
     * @param ppname   工艺文件名
     * @param veroid   oid
     * @param version
     * @return
     */
    public static String searchDismessage2(String ppnumber, String ppname, String veroid, String version) {
        String dismessage = "";
        StringBuffer sb = new StringBuffer();
        if (!"".equals(veroid) && veroid != null) {
            sb.append("SELECT GWKEYID FROM GWPRINTAPPLYRECORD WHERE DOCVR = '");
            sb.append(veroid);
            sb.append("'");

        } else {
            sb.append("SELECT GWKEYID FROM GWPRINTAPPLYRECORD WHERE DOCNUMBER = '" + ppnumber + "'");
            sb.append("AND DOCNAME = '" + ppname + "'");
            if (version != null && !"".equals(version)) {
                sb.append(" AND VERSION = '" + version + "'");
            }
        }
        List<String> lists = queryDataToPrint(sb.toString());
        DBConnUtil dbConnUtil = null;
        Map<String, Integer> deptAndCountMap = new HashMap<String, Integer>();
        try {
            for (String gwkeyId : lists) {
                String sql = "select DISTRIBUTEDEPT,DISTRIBUTEQUANTITY from GWPRINTDISTRIBUTERECORD where APPLYRECORDID='" + gwkeyId + "'";
                dbConnUtil = new DBConnUtil();
                ResultSet resultSet = dbConnUtil.executeQuery(sql);
                while (resultSet.next()) {
                    String distributedept = resultSet.getString("DISTRIBUTEDEPT");
                    String distributequantity = resultSet.getString("DISTRIBUTEQUANTITY");
                    if (deptAndCountMap.containsKey(distributedept)) {
                        deptAndCountMap.put(distributedept, deptAndCountMap.get(distributedept) + Integer.parseInt(distributequantity));
                    } else {
                        deptAndCountMap.put(distributedept, Integer.parseInt(distributequantity));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dbConnUtil != null) {
                    dbConnUtil.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        for (Map.Entry<String, Integer> entry : deptAndCountMap.entrySet()) {
            if ("".equals(dismessage)) {
                dismessage = entry.getKey() + ":" + entry.getValue() + "份";
            } else {
                dismessage += "," + entry.getKey() + ":" + entry.getValue() + "份";
            }
        }
        return dismessage;
    }

    /**
     * 查询打印分发表中是否存在数据及是否已分发还是分发中
     *
     * @param ppnumber 工艺文件编号
     * @param ppname   工艺文件名
     * @param veroid   oid
     * @param version
     * @return
     */
    public static String searchFileState(String ppnumber, String ppname, String veroid, String version) {
        String fileState = "未分发";
        StringBuffer sb = new StringBuffer();
        if (!"".equals(veroid) && veroid != null) {
            sb.append("SELECT GWKEYID FROM GWPRINTAPPLYRECORD WHERE DOCVR = '");
            sb.append(veroid);
            sb.append("'");

        } else {
            sb.append("SELECT GWKEYID FROM GWPRINTAPPLYRECORD WHERE DOCNUMBER = '" + ppnumber + "'");
            sb.append("AND DOCNAME = '" + ppname + "'");
            if (version != null && !"".equals(version)) {
                sb.append(" AND VERSION = '" + version + "'");
            }
        }
        List<String> lists = queryDataToPrint(sb.toString());
        if (lists != null && lists.size() > 0) {
            String gwKeyId1 = lists.get(0);
            fileState = "已分发";
            String sql1 = "SELECT GWKEYID FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID='" + gwKeyId1 + "'";
            List<String> lists1 = queryDataToPrint(sql1);
            for (String gwKeyId2 : lists1) {
                String sql2 = "SELECT FILESTATUS FROM GWPRINTBARCODE WHERE APPLYRECORDID='" + gwKeyId2 + "'";
                List<String> lists2 = queryDataToPrint(sql2);
                for (String dataFileState : lists2) {
                    if ("未打印".equals(dataFileState) || "已打印".equals(dataFileState)) {
                        fileState = "分发中";
                        return fileState;
                    }
                }
            }
            return fileState;
        }
        return fileState;
    }

    public static List<String> queryDataToPrint(String sql) {
        List<String> lists = new ArrayList<String>();
        DBConn conn = null;
        try {
            conn = new DBConn();
            ResultSet rs = conn.executeQuery(sql);
            while (rs.next()) {
                String string = rs.getString(1);
                lists.add(string);
            }
        } catch (Exception e) {
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
        return lists;
    }

    public static List<CmPrintInfoBean> buildCmPrintInfoBeanByPrintApplyRecord(long oid, String type, String rejectState) throws Exception {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        List<GWPrintApplyRecord> gwPrintApplyRecords = GWPrintApplyRecordManager.queryGWPrintApplyRecordByPrintFileOid(oid);
        if (gwPrintApplyRecords.size() == 0)
            return list;

        for (GWPrintApplyRecord gwPrintApplyRecord : gwPrintApplyRecords) {
            if (type.equals(PrintServerConstants.TABLETYPE_DYSQBH)) {
                if (gwPrintApplyRecord.getRejectStatus().equals(rejectState)) {
                    continue;
                }
            }

            CmPrintInfoBean printInfoBean = new CmPrintInfoBean();
            Persistable per = PersistableUtil.getPersistable(gwPrintApplyRecord.getProcessOid());
            setPrintBasicInfo(printInfoBean, per);
            printInfoBean.setOid(gwPrintApplyRecord.getProcessOid());
            printInfoBean.setPindex(gwPrintApplyRecord.getPindex());
            printInfoBean.setBaseline(gwPrintApplyRecord.getTs_Baseline());
            printInfoBean.setPageCount(gwPrintApplyRecord.getPage());
            printInfoBean.setBlueCard(Boolean.parseBoolean(gwPrintApplyRecord.getIsBlueCard()));
            printInfoBean.setEcnNumber(gwPrintApplyRecord.getEcnNumber());
            List<GWPrintDistributeRecord> gwPrintDistributeRecords = GWPrintDistributeRecordManager.queryGWPrintDistributeRecordByBarCode(gwPrintApplyRecord.getBarCode());
            String distributeDeptAndQuanity = GWPrintDistributeRecordManager.getDistributeDeptAndQuanity(gwPrintDistributeRecords);
            printInfoBean.setDistributeDeptAndCount(distributeDeptAndQuanity);
            printInfoBean.setQrCode(gwPrintApplyRecord.getBarCode());
            printInfoBean.setPrintState(gwPrintApplyRecord.getPrintStatus());
            printInfoBean.setRejectState(gwPrintApplyRecord.getRejectStatus());
            printInfoBean.setPrintDescription(gwPrintApplyRecord.getPrintRequire());
            printInfoBean.setRejectRemark(gwPrintApplyRecord.getRejectRemark());
            list.add(printInfoBean);
        }
        return list;
    }

    public static void setPrintBasicInfo(CmPrintInfoBean printInfoBean, Persistable per) throws WTException {
        if (per instanceof MPMProcessPlan) {
            MPMProcessPlan mpmProcessPlan = (MPMProcessPlan) per;
            Object number = MBAUtil.getValue(mpmProcessPlan, ProcessPlanConstants.MBA_PROCESSNUMBER);
            printInfoBean.setFileNumber(String.valueOf(number));
            if (mpmProcessPlan.getName().indexOf("(") > 0) {
                printInfoBean.setFileName(mpmProcessPlan.getName().substring(0, mpmProcessPlan.getName().indexOf("(")));
            } else {
                printInfoBean.setFileName(mpmProcessPlan.getName());
            }
            printInfoBean.setVersion(mpmProcessPlan.getVersionIdentifier().getValue() + "." + mpmProcessPlan.getIterationIdentifier().getValue());
            Object phaseCode = MBAUtil.getValue(mpmProcessPlan, ProcessPlanConstants.MBA_PHASECODE);
            printInfoBean.setPhaseCode(CommonUtil.objectToString(phaseCode));
            Object processCategory = MBAUtil.getValue(mpmProcessPlan, ProcessPlanConstants.MBA_PROCESSCATEGORY);
            printInfoBean.setFileType(CommonUtil.objectToString(processCategory));
            Object secret = MBAUtil.getValue(mpmProcessPlan, ProcessPlanConstants.MBA_SECRET);
            printInfoBean.setSecret(CommonUtil.objectToString(secret));
        } else if (per instanceof WTChangeOrder2) {
            WTChangeOrder2 ecn = (WTChangeOrder2) per;
            printInfoBean.setFileNumber(ecn.getNumber());
            printInfoBean.setFileName(ecn.getName());
            printInfoBean.setVersion(ecn.getVersionIdentifier().getValue() + "." + ecn.getIterationIdentifier().getValue());
            Object phaseCode = IBAHelper.getIBAValue(ecn, ProcessPlanConstants.MBA_PHASECODE);
            printInfoBean.setPhaseCode(CommonUtil.objectToString(phaseCode));
            printInfoBean.setFileType(PrintServerConstants.FILETYPE_GYGGD);
            Object secret = IBAHelper.getIBAValue(ecn, ProcessPlanConstants.MBA_SECRET);
            printInfoBean.setSecret(CommonUtil.objectToString(secret));
        } else if (per instanceof WTDocument) {
            WTDocument doc = (WTDocument) per;
            printInfoBean.setFileNumber(doc.getNumber());
            printInfoBean.setFileName(doc.getName());
            printInfoBean.setVersion(doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());
            Object phaseCode = IBAHelper.getIBAValue(doc, ProcessPlanConstants.MBA_PHASECODE);
            printInfoBean.setPhaseCode(CommonUtil.objectToString(phaseCode));
            printInfoBean.setFileType(PrintServerConstants.FILETYPE_GYGGD);
            Object secret = IBAHelper.getIBAValue(doc, ProcessPlanConstants.MBA_SECRET);
            printInfoBean.setSecret(CommonUtil.objectToString(secret));
        }
    }

    public static CmPrintInfoBean buildAllPrintsInfo(Persistable per) {
        CmPrintInfoBean printInfoBean = new CmPrintInfoBean();

        return printInfoBean;
    }

    public static CmPrintInfoBean buildReceiptFileInfo(String barCode, String receiptDept) throws Exception {
        CmPrintInfoBean printInfoBean = null;
        GWPrintApplyRecord gwPrintApplyRecord = GWPrintApplyRecordManager.queryGWPrintApplyRecordByQRCode(barCode);
        if (gwPrintApplyRecord != null && gwPrintApplyRecord.getPrintStatus().equals(PrintServerConstants.PRINTSTATUS_YDY)) {
            printInfoBean = new CmPrintInfoBean();
            Persistable per = PersistableUtil.getPersistable(gwPrintApplyRecord.getProcessOid());
            setPrintBasicInfo(printInfoBean, per);
            printInfoBean.setOid(gwPrintApplyRecord.getProcessOid());
            printInfoBean.setPindex(gwPrintApplyRecord.getPindex());
            printInfoBean.setBaseline(gwPrintApplyRecord.getTs_Baseline());
            printInfoBean.setPageCount(gwPrintApplyRecord.getPage());
            printInfoBean.setBlueCard(Boolean.parseBoolean(gwPrintApplyRecord.getIsBlueCard()));
            printInfoBean.setEcnNumber(gwPrintApplyRecord.getEcnNumber());
            printInfoBean.setQrCode(gwPrintApplyRecord.getBarCode());
            printInfoBean.setPrintState(gwPrintApplyRecord.getPrintStatus());
            if (gwPrintApplyRecord.getPrintDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                printInfoBean.setPrintDate(dateFormat.format(gwPrintApplyRecord.getPrintDate()));
            }
            printInfoBean.setRejectState(gwPrintApplyRecord.getRejectStatus());
            printInfoBean.setPrintDescription(gwPrintApplyRecord.getPrintRequire());
            WTUser user = GWPrintApplyRecordManager.getPrintor(gwPrintApplyRecord.getPrintor());
            printInfoBean.setPrinter(user.getFullName());

            List<GWPrintDistributeRecord> gwPrintDistributeRecords = GWPrintDistributeRecordManager.queryGWPrintDistributeRecordByBarCode(barCode);
            String distributeDeptAndQuanity = GWPrintDistributeRecordManager.getDistributeDeptAndQuanity(gwPrintDistributeRecords);
            printInfoBean.setDistributeDeptAndCount(distributeDeptAndQuanity);
            GWPrintDistributeRecord gwPrintDistributeRecord = GWPrintDistributeRecordManager.queryGWPrintDistributeRecordByBarCodeAndDept(barCode, receiptDept);
            printInfoBean.setReceiptCount(String.valueOf(gwPrintDistributeRecord.getGetQuantity() + 1));
        }
        return printInfoBean;
    }

    public static CmPrintInfoBean buildCmPrintInfoBeanByYlqwj(GWPrintDistributeRecord gwPrintDistributeRecord, GWPrintApplyRecord gwPrintApplyRecord) throws Exception {
        CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
        cmPrintInfoBean.setOid(gwPrintApplyRecord.getProcessOid());
        cmPrintInfoBean.setFileNumber(gwPrintApplyRecord.getProcessNumber());
        cmPrintInfoBean.setFileName(gwPrintApplyRecord.getProcessName());
        cmPrintInfoBean.setPindex(gwPrintApplyRecord.getPindex());
        cmPrintInfoBean.setVersion(gwPrintApplyRecord.getVersion());
        cmPrintInfoBean.setPhaseCode(gwPrintApplyRecord.getPhaseCode());
        cmPrintInfoBean.setFileType(gwPrintApplyRecord.getFileType());
        cmPrintInfoBean.setPageCount(gwPrintApplyRecord.getPage());
        cmPrintInfoBean.setSecret(gwPrintApplyRecord.getSecret());
        cmPrintInfoBean.setQrCode(gwPrintApplyRecord.getBarCode());
        cmPrintInfoBean.setEcnNumber(gwPrintApplyRecord.getEcnNumber());

        Persistable per = PersistableUtil.getPersistable(gwPrintApplyRecord.getProcessOid());
        if (per instanceof MPMProcessPlan) {
            List<WTChangeOrder2> ecnList = ProcessPlanUtil.getEcnByPersistable(per);
            if (ecnList != null && !ecnList.isEmpty()) {
                WTChangeOrder2 ecn = ecnList.get(0);
                cmPrintInfoBean.setRecoverRemark(ecn.getNumber());
            }
        }

        WTUser wtUser = (WTUser) PersistableUtil.getPersistable(PrintServerConstants.OID_WTUSER + gwPrintDistributeRecord.getGetUser());
        cmPrintInfoBean.setReceipter(wtUser.getFullName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        cmPrintInfoBean.setReceiptCount(String.valueOf(gwPrintDistributeRecord.getGetQuantity()));
        cmPrintInfoBean.setReceiptDate(dateFormat.format(gwPrintDistributeRecord.getGetDate()));
        cmPrintInfoBean.setReceiptDept(gwPrintDistributeRecord.getGetDept());

        GWPrintRecoverRecord gwPrintRecoverRecord = GWPrintRecoverRecordManager.queryGWPrintRecoverRecordByBarCodeAndDept(gwPrintApplyRecord.getBarCode(), gwPrintDistributeRecord.getGetDept());
        if (gwPrintRecoverRecord != null) {
            cmPrintInfoBean.setRecoverCount(String.valueOf(gwPrintRecoverRecord.getRecoverQuantity() + 1));
        } else {
            cmPrintInfoBean.setRecoverCount("1");
        }
        return cmPrintInfoBean;
    }


    public static CmPrintInfoBean buildRecoverFileInfo(String barCode, String recoverDept) throws Exception {
        CmPrintInfoBean printInfoBean = null;
        GWPrintApplyRecord gwPrintApplyRecord = GWPrintApplyRecordManager.queryGWPrintApplyRecordByQRCode(barCode);
        if (gwPrintApplyRecord != null) {
            printInfoBean = new CmPrintInfoBean();
            Persistable per = PersistableUtil.getPersistable(gwPrintApplyRecord.getProcessOid());
            setPrintBasicInfo(printInfoBean, per);
            printInfoBean.setOid(gwPrintApplyRecord.getProcessOid());
            printInfoBean.setPageCount(gwPrintApplyRecord.getPage());
            printInfoBean.setQrCode(gwPrintApplyRecord.getBarCode());

            if (per instanceof MPMProcessPlan) {
                List<WTChangeOrder2> ecnList = ProcessPlanUtil.getEcnByPersistable(per);
                if (ecnList != null && !ecnList.isEmpty()) {
                    WTChangeOrder2 ecn = ecnList.get(0);
                    printInfoBean.setRecoverRemark(ecn.getNumber());
                }
            }

            GWPrintDistributeRecord gwPrintDistributeRecord = GWPrintDistributeRecordManager.queryGWPrintDistributeRecordByBarCodeAndDept(barCode, recoverDept);
            printInfoBean.setReceiptCount(String.valueOf(gwPrintDistributeRecord.getGetQuantity()));

            GWPrintRecoverRecord gwPrintRecoverRecord = GWPrintRecoverRecordManager.queryGWPrintRecoverRecordByBarCodeAndDept(barCode, recoverDept);
            if (gwPrintRecoverRecord != null) {
                printInfoBean.setRecoverCount(String.valueOf(gwPrintRecoverRecord.getRecoverQuantity() + 1));
            } else {
                printInfoBean.setRecoverCount("1");
            }
        }
        return printInfoBean;
    }

    public static List<CmPrintInfoBean> buildCmPrintInfoBeanByPrintRecoverRecord(long oid) throws Exception {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        List<GWPrintRecoverRecord> gwPrintRecoverRecords = GWPrintRecoverRecordManager.queryGWPrintRecoverRecordByPrintFileOid(oid);
        if (gwPrintRecoverRecords.size() == 0)
            return list;
        for (GWPrintRecoverRecord gwPrintRecoverRecord : gwPrintRecoverRecords) {
            GWPrintApplyRecord gwPrintApplyRecord = GWPrintApplyRecordManager.queryGWPrintApplyRecordByQRCode(gwPrintRecoverRecord.getBarCode());
            if (gwPrintApplyRecord != null) {
                CmPrintInfoBean printInfoBean = new CmPrintInfoBean();
                printInfoBean.setOid(gwPrintRecoverRecord.getProcessOid());
                printInfoBean.setFileNumber(gwPrintApplyRecord.getProcessNumber());
                printInfoBean.setFileName(gwPrintApplyRecord.getProcessName());
                printInfoBean.setPageCount(gwPrintApplyRecord.getPage());
                printInfoBean.setSecret(gwPrintApplyRecord.getSecret());
                printInfoBean.setRecoverCount(String.valueOf(gwPrintRecoverRecord.getRecoverQuantity()));
                printInfoBean.setRecoverDept(gwPrintRecoverRecord.getRecoverDept());
                printInfoBean.setRecoverRemark(gwPrintRecoverRecord.getRecoverRemark());
                printInfoBean.setQrCode(gwPrintRecoverRecord.getBarCode());
                printInfoBean.setReceiveCount(String.valueOf(gwPrintRecoverRecord.getReceiveQuantity()));
                if (gwPrintRecoverRecord.getReceiveUser() != 0) {
                    WTUser user = GWPrintRecoverRecordManager.getReceiveUser(gwPrintRecoverRecord.getReceiveUser());
                    printInfoBean.setReceivePerson(user.getFullName());
                }
                if (gwPrintRecoverRecord.getReceiveDate() != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    printInfoBean.setReceiveDate(dateFormat.format(gwPrintRecoverRecord.getReceiveDate()));
                }

                list.add(printInfoBean);
            }
        }
        return list;
    }

    /**
     * 打印申请查询
     *
     * @param oid
     * @return
     */
    public static List<CmPrintInfoBean> getPrintApplication(String oid) {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        if (oid == null || "".equals(oid)) {
            return list;
        }
        DBConnUtil conn = null;
        try {
            conn = new DBConnUtil();
            String sql = "select * from GWPRINTAPPLYRECORD where PBOOID ='" + oid+"'";
            ResultSet rs = conn.executeQuery(sql);
            while (rs.next()) {
                CmPrintInfoBean printInfoBean = new CmPrintInfoBean();
                printInfoBean.setOid(rs.getString("GWKEYID"));
                printInfoBean.setFileNumber(rs.getString("DOCNUMBER"));
                printInfoBean.setFileName(rs.getString("DOCNAME"));
                printInfoBean.setVersion(rs.getString("VERSION"));
                printInfoBean.setPhaseCode(rs.getString("PHASECODE"));
                printInfoBean.setSecret(rs.getString("SECRET"));
                printInfoBean.setTemporarySeal(rs.getString("BATCH"));
                printInfoBean.setPrintState(rs.getString("PRINTSTATUS"));
                printInfoBean.setDistributeDeptAndCount(rs.getString("DISMESSAGE"));
                printInfoBean.setDocVR(rs.getString("DOCVR"));
                printInfoBean.setPbooid(rs.getString("PBOOID"));
                printInfoBean.setFileType(rs.getString("FILETYPE"));
                printInfoBean.setTechnicsNumber(rs.getString("TECHNICSNUMBER"));

                String processfile = rs.getString("PROCESSFILE");
                if ("true".equals(processfile)) {
                    printInfoBean.setProcessfile(true);
                } else {
                    printInfoBean.setProcessfile(false);
                }
                String fromBOM = rs.getString("FROMBOM");
                if ("true".equals(fromBOM)) {
                    printInfoBean.setAddFormBOM(true);
                } else {
                    printInfoBean.setAddFormBOM(false);
                }
                //add by jyx 增加所属产品库名称 和 分发状态受控状态
                String persistableOid = rs.getString("DOCVR");
                String containerName = "";
                String fileState = "";
                String lifeCycle = "";
                if ("WL".equals(persistableOid) || "ZZ".equals(persistableOid)) {
                    Map<String, String> map = PrintDataQueryUtil.getOutFileNumberAndVersionById(persistableOid, "");
                    containerName = PrintDataQueryUtil.getCategoryByOutFile(map);
                } else {
                    Persistable persistable = PersistableUtil.getPersistable(persistableOid);
                    if (persistable instanceof WTDocument) {
                        WTDocument document = (WTDocument) persistable;
                        containerName = document.getContainerName();
                        lifeCycle = document.getState().getState().getDisplay(Locale.CHINA);
                    } else if (persistable instanceof WTChangeOrder2) {
                        WTChangeOrder2 changeOrder = (WTChangeOrder2) persistable;
                        containerName = changeOrder.getContainerName();
                        lifeCycle = changeOrder.getState().getState().getDisplay(Locale.CHINA);
                    }
                    fileState = searchFileState("", "", persistableOid, "");
                }
                printInfoBean.setContainerName(containerName);
                printInfoBean.setFileState(fileState);
                printInfoBean.setLifeCycle(lifeCycle);
                list.add(printInfoBean);
            }
        } catch (Exception e) {
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
        return list;
    }

    /**
     * 更改打印状态
     *
     * @param list
     * @param status
     * @param printDate
     * @param printer
     */
    public static void setPrintStatus(List<String> list, String status, String printer, String printDate) {
        DBConnUtil conn = null;
        try {
            conn = new DBConnUtil();
            if (list != null && !list.isEmpty()) {
                for (String id : list) {
                    String sql = "UPDATE GWPRINTBARCODE SET FILESTATUS = '" + status + "', PUSER = '"
                            + printer + "', PDATE = '" + printDate + "' WHERE GWKEYID = '" + id + "'";
                    conn.executeUpdate(sql);
                }
                conn.commit();
            }
        } catch (Exception e) {
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

    }

    public static List<CmPrintInfoBean> searchSealPlus(CmPrintQueryBean cmPrintQueryBean, String category) {
        ArrayList<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        String number = cmPrintQueryBean.getFileNumber();
        String name = cmPrintQueryBean.getFileName();
        String version = cmPrintQueryBean.getVersion();
        if ("".equals(number) && "".equals(name) && "".equals(version)) {
            return list;
        }
        String sql1 = trunFuzzySql(number, name, version, 1);
        if ("WL".equals(category)) {
            sql1 = sql1 + " AND OUTDEPT != '厂内电子' AND OUTDEPT != '厂内纸质'";
        } else if ("ZZ".equals(category)) {
            sql1 = sql1 + " AND OUTDEPT = '厂内纸质'";
        } else {
            sql1 = sql1 + " AND OUTDEPT = '厂内电子'";
        }
        DBConnUtil conn1 = null;
        DBConnUtil conn2 = null;
        try {
            conn1 = new DBConnUtil();
            conn2 = new DBConnUtil();
            ResultSet rs1 = conn1.executeQuery(sql1);
            while (rs1.next()) {
                String fileNumber = rs1.getString("DOCNUMBER");
                String fileName = rs1.getString("DOCNAME");
                String fileVersion = rs1.getString("VERSION");
                String phasecode = rs1.getString("PHASECODE");
                String secret = rs1.getString("SECRET");
                String oid = rs1.getString("GWKEYID");
                String docVR = rs1.getString("DOCVR");
                String containerName = "";
                if (null != docVR) {
                    if ("WL".equals(docVR) || "ZZ".equals(docVR)) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("number", fileNumber);
                        map.put("name", fileName);
                        map.put("version", fileVersion);
                        containerName = PrintDataQueryUtil.getCategoryByOutFile(map);
                    } else {
                        Persistable persistable = PersistableUtil.getPersistable(docVR);
                        if (persistable != null) {
                            if (persistable instanceof WTDocument) {
                                WTDocument document = (WTDocument) persistable;
                                containerName = document.getContainerName();
                            } else if (persistable instanceof WTChangeOrder2) {
                                WTChangeOrder2 changeOrder = (WTChangeOrder2) persistable;
                                containerName = changeOrder.getContainerName();
                            }
                        }
                    }
                }
                String sql2 = "SELECT * FROM gwprintbarcode WHERE applyrecordid IN " +
                        "(SELECT GWKEYID FROM gwprintdistributerecord WHERE applyrecordid IN " +
                        "(SELECT GWKEYID FROM gwprintapplyrecord WHERE gwprintapplyrecord.gwkeyid = '" + oid + "'))";
                ResultSet rs2 = conn2.executeQuery(sql2);
                while (rs2.next()) {
                    CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
                    cmPrintInfoBean.setFileNumber(fileNumber);
                    cmPrintInfoBean.setFileName(fileName);
                    cmPrintInfoBean.setVersion(fileVersion);
                    cmPrintInfoBean.setPhaseCode(phasecode);
                    cmPrintInfoBean.setSecret(secret);
                    cmPrintInfoBean.setContainerName(containerName);
                    cmPrintInfoBean.setPrintDate(rs2.getString("PDATE"));
                    cmPrintInfoBean.setTemporarySeal(rs2.getString("BATCH"));
                    cmPrintInfoBean.setOid(rs2.getString("GWKEYID"));
                    cmPrintInfoBean.setDistributeDeptAndCount(rs2.getString("GDEPT"));
                    cmPrintInfoBean.setQrName(rs2.getString("BARCODE"));
                    cmPrintInfoBean.setSealPlus(rs2.getString("ADDBATCH"));
                    cmPrintInfoBean.setFileState(rs2.getString("FILESTATUS"));
                    cmPrintInfoBean.setPbooid(rs2.getString("PBOOID"));
                    list.add(cmPrintInfoBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn1 != null) {
                    conn1.close();
                }
                if (conn2 != null) {
                    conn2.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 多表查询转换sql
     *
     * @param number
     * @param name
     * @param version
     * @param i       1为分发表 3为条码表
     * @return
     * @author zhuhao
     * @date 2018-3-28
     */
    private static String trunFuzzySql(String number, String name, String version, int i) {
        String Sql = new String();
        if (i == 3) {
            Sql = "SELECT * FROM gwprintbarcode WHERE applyrecordid IN" +
                    "(SELECT gwkeyid FROM gwprintdistributerecord WHERE applyrecordid IN" +
                    "(SELECT gwkeyid FROM gwprintapplyrecord WHERE ";
        } else if (i == 1) {
            Sql = "SELECT * FROM gwprintapplyrecord WHERE ";
        }

        if (!"".equals(number)) {
            Sql = Sql + "docnumber LIKE '%" + number + "%'";
            if (!"".equals(name) || !"".equals(version)) {
                Sql = Sql + " AND ";
            }
        }
        if (!"".equals(name)) {
            Sql = Sql + "docname LIKE '%" + name + "%'";
            if (!"".equals(version)) {
                Sql = Sql + " AND ";
            }
        }
        if (!"".equals(version)) {
            Sql = Sql + "version LIKE '%" + version + "%'";
        }
        if (i == 3) {
            Sql = Sql + "))";
        }
        return Sql;
    }

    public static String updatePrintAddSeal(List<CmSealBean> list, String pboOid) {
        String result = "";
        if (list == null || list.isEmpty()) {
            result = "设置失败";
            return result;
        }
        DBConnUtil conn = null;
        DBConnUtil conn1 = null;
        DBConnUtil conn2 = null;
        boolean modify = true;
        try {
            boolean start = true;
            if (pboOid == null || "".equals(pboOid)) {
                WTContainer wtContainer = PrintUtil.getContainerByName("打印分发管理库");
                WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
                String userFullName = user.getFullName();//获取用户名
                String todayDate = DateUtil.getTodayDate("yyyy-MM-dd");
                String docName = userFullName + "-" + todayDate + "-加盖印章申请单";
                WTDocument doc = WTDocumentUtil.createDocument(null, docName, wtContainer, "Default/加盖印章申请单", "casc.sast.149.PRINTSEALPLUS");
                start = WorkflowUtil.startProcess(doc, "加盖印章管理流程", "加盖印章管理流程");
                pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(doc).getId());
                modify = false;
            } else {
                //修改的时候如果删除了初始申请的数据，则需要将初始的数据恢复原状
                conn2 = new DBConnUtil();
                StringBuffer sb1 = new StringBuffer();
                sb1.append("SELECT * FROM GWPRINTBARCODE WHERE PBOOID = '" + pboOid + "'");
                ResultSet rs1 = conn2.executeQuery(sb1.toString());
                while (rs1.next()) {
                    String id = rs1.getString("GWKEYID");
                    boolean delete = true;
                    for (CmSealBean cmSealBean : list) {
                        String gwKeyId = cmSealBean.getGwKeyId();
                        if (id.equals(gwKeyId)) {
                            delete = false;
                        }
                    }
                    if (delete) {
                        conn1 = new DBConnUtil();
                        String sql = "UPDATE GWPRINTBARCODE SET ADDBATCH='',PBOOID='' WHERE GWKEYID='" + id + "'";
                        conn1.executeUpdate(sql);
                    }
                }
            }
            if (!start) {
                result = "流程启动失败,请联系管理员";
                return result;
            } else {
                conn = new DBConnUtil();
                for (CmSealBean cmSealBean : list) {
                    String sql = "UPDATE GWPRINTBARCODE SET ADDBATCH='" + cmSealBean.getName()
                            + "',PBOOID='" + pboOid + "' WHERE GWKEYID='" + cmSealBean.getGwKeyId() + "'";
                    conn.executeUpdate(sql);
                }
                conn.commit();
                if (modify) {
                    result = "修改成功";
                } else {
                    result = "设置成功,加盖印章管理流程已启动";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (conn1 != null) {
                    conn1.close();
                }
                if (conn2 != null) {
                    conn2.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static List<CmPrintInfoBean> loadSealPlus(String oid, String category) {
        ArrayList<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        DBConnUtil conn1 = null;
        DBConnUtil conn2 = null;
        try {
            conn1 = new DBConnUtil();
            conn2 = new DBConnUtil();
            StringBuffer sb1 = new StringBuffer();
            sb1.append("SELECT * FROM GWPRINTBARCODE WHERE PBOOID = '" + oid + "'");
            if ("SJ".equals(category) || "LQ".equals(category)) {
                String dept = PrintUtil.getUserDept();
                sb1.append(" AND GDEPT = '" + dept + "'");
            }
            ResultSet rs1 = conn1.executeQuery(sb1.toString());
            while (rs1.next()) {
                CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
                String id = rs1.getString("GWKEYID");
                cmPrintInfoBean.setOid(id);
                cmPrintInfoBean.setDistributeDeptAndCount(rs1.getString("GDEPT"));
                cmPrintInfoBean.setPrintDate(rs1.getString("PDATE"));
                cmPrintInfoBean.setQrName(rs1.getString("BARCODE"));
                cmPrintInfoBean.setSealPlus(rs1.getString("ADDBATCH"));
                cmPrintInfoBean.setTemporarySeal(rs1.getString("BATCH"));
                cmPrintInfoBean.setPbooid(oid);
                cmPrintInfoBean.setFileState(rs1.getString("FILESTATUS"));
                String sql2 = "SELECT * FROM GWPRINTAPPLYRECORD WHERE GWKEYID IN " +
                        "(SELECT APPLYRECORDID FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID IN " +
                        "(SELECT APPLYRECORDID FROM GWPRINTBARCODE WHERE GWKEYID = '" + id + "'))";
                ResultSet rs2 = conn2.executeQuery(sql2);
                while (rs2.next()) {
                    cmPrintInfoBean.setFileNumber(rs2.getString("DOCNUMBER"));
                    cmPrintInfoBean.setFileName(rs2.getString("DOCNAME"));
                    cmPrintInfoBean.setVersion(rs2.getString("VERSION"));
                    cmPrintInfoBean.setPhaseCode(rs2.getString("PHASECODE"));
                    cmPrintInfoBean.setSecret(rs2.getString("SECRET"));
                }
                list.add(cmPrintInfoBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn1 != null) {
                    conn1.close();
                }
                if (conn2 != null) {
                    conn2.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static List<CmPrintInfoBean> getQRbarcode(String oid) {
        ArrayList<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        DBConnUtil conn = null;
        try {
            conn = new DBConnUtil();
            String sql = "SELECT BARCODE FROM gwprintbarcode WHERE gwkeyid = '" + oid + "'";
            ResultSet rs = conn.executeQuery(sql);
            while (rs.next()) {
                CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
                cmPrintInfoBean.setQrCode(rs.getString("BARCODE"));
                list.add(cmPrintInfoBean);
                break;
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

        return list;
    }

    public static String getPrintStatusByNumber(String fileNumber) {
        DBConnUtil conn = null;
        String printStatus = "";
        try {
            conn = new DBConnUtil();
            String sql = "SELECT PRINTSTATUS FROM GWPRINTAPPLYRECORD WHERE PROCESSNUMBER = '" + fileNumber + "'";
            ResultSet rs = conn.executeQuery(sql);
            while (rs.next()) {
                printStatus = rs.getString("PRINTSTATUS");
            }
            if ("".equals(printStatus)) {
                printStatus = PrintServerConstants.PRINTSTATUS_WDY;
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
        return printStatus;
    }

    public static List<String> setFileStatus(List<String> list, CmDistributionBean cmDistributionBean) {
        List<String> fileIds = new ArrayList<String>();
        DBConnUtil conn = null;
        try {
            conn = new DBConnUtil();
            String GUser = cmDistributionBean.getReceiveFile();
            String GDate = cmDistributionBean.getReceiveTime();
            for (String id : list) {
                String sql = "UPDATE GWPRINTBARCODE SET FILESTATUS = '" + PrintServerConstants.PRINTSTATUS_YXF +
                        "' , GUSER = '" + GUser + "' , GDATE = '" + GDate + "' " +
                        "WHERE GWKEYID = '" + id + "'";
                conn.executeUpdate(sql);
                //查询对应的文件的gwkeyid
                String gwkeyid = queryFileID(id);
                if (!fileIds.contains(gwkeyid)) {
                    fileIds.add(gwkeyid);
                }
            }
            conn.commit();
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
        return fileIds;

    }

    private static boolean queryPrintstatus(String id) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT FILESTATUS FROM GWPRINTBARCODE WHERE APPLYRECORDID IN ");
        sb.append("(SELECT GWKEYID FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID = '" + id + "')");
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                if (!PrintServerConstants.PRINTSTATUS_YXF.equals(rs.getString("FILESTATUS"))) {
                    return false;
                }
            }
        } catch (SQLException e) {
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
        return true;
    }

    private static String queryFileID(String id) {
        String result = "";
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT GWKEYID FROM GWPRINTAPPLYRECORD WHERE GWKEYID IN ");
            sb.append("(SELECT APPLYRECORDID FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID IN ");
            sb.append("(SELECT APPLYRECORDID FROM GWPRINTBARCODE WHERE GWKEYID = '" + id + "'))");
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                result = rs.getString("GWKEYID");
            }
        } catch (SQLException e) {
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
        return result;
    }

    public static void setPrintStatus(List<String> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("list =========null");
            return;
        }
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            for (String id : list) {
                if (queryPrintstatus(id)) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("UPDATE GWPRINTAPPLYRECORD SET PRINTSTATUS = '" + PrintServerConstants.FILESTATUS_YFF + "' WHERE GWKEYID = '" + id + "'");
                    state.executeUpdate(sb.toString());
                }
            }
        } catch (SQLException e) {
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

    public static CmPrintInfoBean addOutFile(CmPrintQueryBean cmPrintQueryBean, boolean isModify, String id) {
        String uuid = UUID.randomUUID().toString();
        CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
        String number = cmPrintQueryBean.getFileNumber();
        String name = cmPrintQueryBean.getFileName();
        String version = cmPrintQueryBean.getVersion();
        String phaseCode = cmPrintQueryBean.getPhaseCode();
        String secret = cmPrintQueryBean.getSecret();
        String fileType = cmPrintQueryBean.getFileType();
        String outDept = cmPrintQueryBean.getOutDept();
        String pageCount = cmPrintQueryBean.getPageCount();
        String container = cmPrintQueryBean.getContainer();
        DBConnUtil conn = null;
        try {
            conn = new DBConnUtil();
            String sql = "";
            if (isModify) {
                sql = "UPDATE GWOTHERFILES SET DEPT = '" + outDept + "', FILENUMBER = '" + number + "', FILENAME='" + name + "', VERSION='" + version
                        + "', PHASECODE='" + phaseCode + "', SECRET='" + secret + "', FILETYPE='" + fileType + "', PAGECOUNT='" + pageCount + "', CONTAINER = '" + container
                        + "' WHERE GWKEYID = '" + id + "'";
                cmPrintInfoBean.setOid(id);
            } else {
                sql = "INSERT INTO GWOTHERFILES (GWKEYID, DEPT, FILENUMBER, FILENAME, VERSION, PHASECODE, SECRET, FILETYPE, PAGECOUNT, CHANGENOTICEID, CONTAINER) VALUES " +
                        "('" + uuid + "','" + outDept + "','" + number + "','" + name + "','" + version + "','" + phaseCode + "','" + secret + "','"
                        + fileType + "','" + pageCount + "','" + CommonUtil.objectToString(cmPrintQueryBean.getChangeNoticeID()) + "', '" + container + "')";
                cmPrintInfoBean.setOid(uuid);
            }
            conn.executeUpdate(sql);
            conn.commit();
            cmPrintInfoBean.setFileNumber(number);
            cmPrintInfoBean.setFileName(name);
            cmPrintInfoBean.setVersion(version);
            cmPrintInfoBean.setPhaseCode(phaseCode);
            cmPrintInfoBean.setSecret(secret);
            cmPrintInfoBean.setFileType(fileType);
            cmPrintInfoBean.setOutDept(outDept);
            cmPrintInfoBean.setPageCount(pageCount);
            cmPrintInfoBean.setChangeNoticeID(CommonUtil.objectToString(cmPrintQueryBean.getChangeNoticeID()));
            cmPrintInfoBean.setContainerName(container);
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
        return cmPrintInfoBean;
    }

    public static List<String> getDeptByPbooid(String pboOid, String workitemName, String info) {
        List<String> list = new ArrayList<String>();
        DBConnUtil conn = null;
        StringBuffer sb = new StringBuffer();
        try {
            conn = new DBConnUtil();
            if ("DYSQ".equals(workitemName)) {
                sb.append("SELECT DISTRIBUTEDEPT FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID IN (SELECT GWKEYID FROM GWPRINTAPPLYRECORD WHERE PBOOID = '" + pboOid + "')");
            } else if ("JGYZ".equals(workitemName)) {
                sb.append("SELECT GDEPT FROM GWPRINTBARCODE WHERE PBOOID = '" + pboOid + "'");
            } else if ("BDSQ".equals(workitemName)) {
                sb.append("SELECT GDEPT FROM GWPRINTBARCODE WHERE APPLYRECORDID IN ");
                sb.append("(SELECT GWKEYID FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID IN ");
                sb.append("(SELECT GWKEYID FROM GWPRINTAPPLYRECORD WHERE PBOOID = '" + pboOid + "')) ");
                sb.append("AND OFFSET = 'true'");
            }
            ResultSet rs = conn.executeQuery(sb.toString());
            while (rs.next()) {
                String dept = "";
                if ("DYSQ".equals(workitemName)) {
                    dept = rs.getString("DISTRIBUTEDEPT");
                } else if ("JGYZ".equals(workitemName) || "BDSQ".equals(workitemName)) {
                    dept = rs.getString("GDEPT");
                }
                if ("".equals(dept) || list.contains(dept)) {
                    continue;
                }
                list.add(dept);
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
        return list;
    }

    public static List<CmPrintInfoBean> searchOutFile(CmPrintQueryBean cmPrintQueryBean) {
        List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
        String outDept = cmPrintQueryBean.getOutDept();
        String fileNumber = cmPrintQueryBean.getFileNumber();
        String fileName = cmPrintQueryBean.getFileName();
        String version = cmPrintQueryBean.getVersion();
        String phaseCode = cmPrintQueryBean.getPhaseCode();
        String fileType = cmPrintQueryBean.getFileType();
        StringBuffer sb = new StringBuffer();
        if ("NOTNULL".equals(outDept)) {
            sb.append("SELECT * FROM GWOTHERFILES WHERE DEPT != 'null' ");
        } else if (outDept == null) {
            sb.append("SELECT * FROM GWOTHERFILES WHERE DEPT = 'null' ");
        } else {
            sb.append("SELECT * FROM GWOTHERFILES WHERE ");
            sb.append("DEPT = '" + outDept + "' ");
        }
        if (!"".equals(fileNumber) && fileNumber != null) {
            sb.append("AND FILENUMBER LIKE '%" + fileNumber + "%' ");
        }
        if (!"".equals(fileName) && fileName != null) {
            sb.append("AND FILENAME LIKE '%" + fileName + "%' ");
        }
        if (!"".equals(version) && version != null) {
            sb.append("AND VERSION LIKE '%" + version + "%' ");
        }
        if (!"".equals(phaseCode) && phaseCode != null) {
            sb.append("AND PHASECODE LIKE '%" + phaseCode + "%' ");
        }
        if (!"".equals(fileType) && fileType != null) {
            sb.append("AND FILETYPE LIKE '%" + fileType + "%' ");
        }
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            Statement state2 = conn.createStatement();
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                String number = CommonUtil.objectToString(rs.getString("FILENUMBER"));
                String name = CommonUtil.objectToString(rs.getString("FILENAME"));
                String fileVersion = CommonUtil.objectToString(rs.getString("VERSION"));
                String changeNoticeID = CommonUtil.objectToString(rs.getString("CHANGENOTICEID"));
                CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
                cmPrintInfoBean.setOid(rs.getString("GWKEYID"));
                cmPrintInfoBean.setOutDept(rs.getString("DEPT"));
                cmPrintInfoBean.setFileNumber(number);
                cmPrintInfoBean.setFileName(name);
                cmPrintInfoBean.setVersion(fileVersion);
                cmPrintInfoBean.setPhaseCode(rs.getString("PHASECODE"));
                cmPrintInfoBean.setSecret(rs.getString("SECRET"));
                cmPrintInfoBean.setFileType(rs.getString("FILETYPE"));
                cmPrintInfoBean.setPageCount(rs.getString("PAGECOUNT"));
                cmPrintInfoBean.setChangeNoticeID(changeNoticeID);
                cmPrintInfoBean.setContainerName(rs.getString("CONTAINER"));
                if (changeNoticeID != null && !"".equals(changeNoticeID)) {
                    String sql = "SELECT * FROM GWOTHERCHANGENOTICE WHERE GWKEYID = '" + changeNoticeID + "'";
                    ResultSet rs2 = state2.executeQuery(sql);
                    while (rs2.next()) {
                        cmPrintInfoBean.setChangeNoticeDept(rs2.getString("DEPT"));
                        cmPrintInfoBean.setChangeNoticeNumber(rs2.getString("FILENUMBER"));
                        cmPrintInfoBean.setChangeDate(rs2.getString("CHANGEDATE"));
                        cmPrintInfoBean.setChangeContent(rs2.getString("CHANGECONTENT"));
                        cmPrintInfoBean.setViewChange(rs2.getString("ISCODE"));
                    }
                }
                String fileState = PrintDataQueryUtil.queryOutFileState(number, name, fileVersion);
                cmPrintInfoBean.setFileState(fileState);
                list.add(cmPrintInfoBean);
            }
        } catch (SQLException e) {
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
        return list;
    }

    public static void deleteOutFileByID(List<CmPrintInfoBean> beanList) {
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            for (CmPrintInfoBean bean : beanList) {
                String id = bean.getOid();
                String sql = "DELETE FROM GWOTHERFILES WHERE GWKEYID = '" + id + "'";
                String sql2 = "";
                if (bean.getChangeNoticeID() != null && !"".equals(bean.getChangeNoticeID())) {
                    sql2 = "DELETE FROM GWOTHERCHANGENOTICE WHERE GWKEYID = '" + bean.getChangeNoticeID() + "'";
                }
                state.executeUpdate(sql);
                if (!"".equals(sql2)) {
                    state.executeUpdate(sql2);
                }
            }
            conn.commit();
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

    public static void saveChangeNoticeInfo(CmPrintQueryBean cmPrintQueryBean, String id, boolean isModify) {
        StringBuffer sb = new StringBuffer();
        if (isModify) {
            sb.append("UPDATE GWOTHERCHANGENOTICE SET");
            sb.append(" FILENUMBER = '" + cmPrintQueryBean.getFileNumber() + "',");
            sb.append(" CHANGEDATE = '" + cmPrintQueryBean.getChangeData() + "',");
            sb.append(" CHANGECONTENT = '" + cmPrintQueryBean.getChangeContent() + "',");
            sb.append(" ISCODE = '" + cmPrintQueryBean.getViewChange() + "' WHERE GWKEYID = '" + id + "'");
        } else {
            sb.append("INSERT INTO GWOTHERCHANGENOTICE (GWKEYID, FILENUMBER, CHANGEDATE, CHANGECONTENT, ISCODE) VALUES ");
            sb.append("('" + id + "','" + cmPrintQueryBean.getFileNumber() + "'");
            sb.append(",'" + cmPrintQueryBean.getChangeData() + "','" + cmPrintQueryBean.getChangeContent() + "'");
            sb.append(",'" + cmPrintQueryBean.getViewChange() + "')");
        }
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            state.executeUpdate(sb.toString());
            conn.commit();
        } catch (SQLException e) {
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

    public static List<String> getAddBatch(String oid) {
        List<String> list = new ArrayList<String>();
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("");
        } catch (SQLException e) {
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
        return null;
    }

    public static String getPrintState(WTObject pbo) throws WTException {
        String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT PUSER,FILESTATUS FROM GWPRINTBARCODE WHERE APPLYRECORDID IN ");
        sb.append("(SELECT GWKEYID FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID IN ");
        sb.append("(SELECT GWKEYID FROM GWPRINTAPPLYRECORD WHERE PBOOID = '" + pboOid + "'))");
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                String filestatus = rs.getString("FILESTATUS");
                if (!"已终止".equals(filestatus)) {
                    if (rs.getString("PUSER") == null || "".equals(rs.getString("PUSER"))) {
                        return "存在未打印的文件";
                    }
                }

            }
        } catch (SQLException e) {
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
        return "";
    }

    public static String getDistributionState(WTObject pbo) throws WTException {
        String pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(pbo).getId());
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT GUSER FROM GWPRINTBARCODE WHERE APPLYRECORDID IN ");
        sb.append("(SELECT GWKEYID FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID IN");
        sb.append("(SELECT GWKEYID FROM GWPRINTAPPLYRECORD WHERE PBOOID ='" + pboOid + "'))");
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                String guser = rs.getString("GUSER");
                if ("".equals(guser) || guser == null || "null".equals(guser)) {
                    return "存在未分发的文件";
                }
            }
        } catch (SQLException e) {
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
        return "";
    }

    public static List<CmPrintInfoBean> saveImportInfo(List<CmImportBean> beanList) {
        List<CmPrintInfoBean> infolist = new ArrayList<CmPrintInfoBean>();
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            List<String> list = new ArrayList<String>();
            Map<String, CmImportBean> map = new HashMap<String, CmImportBean>();
            for (CmImportBean bean : beanList) {
                CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
                String uuid = UUID.randomUUID().toString();
                boolean hasChanged = PrintUtil.hasChangeNotice(bean);
                StringBuffer sb = new StringBuffer();
                sb.append("INSERT INTO GWOTHERFILES (GWKEYID, DEPT, FILENUMBER, FILENAME, VERSION, PHASECODE, SECRET, CONTAINER, FILETYPE, PAGECOUNT");
                if (hasChanged) {
                    sb.append(", CHANGENOTICEID");
                }
                sb.append(") VALUES ");
                sb.append("('" + UUID.randomUUID().toString() + "','" + bean.getOutDept() + "','" + bean.getFileNumber() + "',");
                sb.append("'" + bean.getFileName() + "','" + bean.getVersion() + "','" + bean.getPhaseCode() + "','" + bean.getSecret() + "',");
                sb.append("'" + bean.getXhlx() + "','" + bean.getFileType() + "','" + bean.getPageCount() + "'");
                if (hasChanged) {
                    sb.append(",'" + uuid + "'");
                }
                sb.append(")");
                state.executeUpdate(sb.toString());
                conn.commit();
                cmPrintInfoBean.setFileNumber(bean.getFileNumber());
                cmPrintInfoBean.setFileName(bean.getFileName());
                cmPrintInfoBean.setVersion(bean.getVersion());
                cmPrintInfoBean.setPhaseCode(bean.getPhaseCode());
                cmPrintInfoBean.setSecret(bean.getSecret());
                cmPrintInfoBean.setFileType(bean.getFileType());
                cmPrintInfoBean.setOutDept(bean.getOutDept());
                cmPrintInfoBean.setPageCount(bean.getPageCount());
                cmPrintInfoBean.setContainerName(bean.getXhlx());
                if (hasChanged) {
                    cmPrintInfoBean.setChangeNoticeID(CommonUtil.objectToString(uuid));
                    cmPrintInfoBean.setChangeNoticeNumber(bean.getChangeNoticeNumber());
                    cmPrintInfoBean.setChangeDate(bean.getChangeDate());
                    cmPrintInfoBean.setChangeContent(bean.getChangeContent());
                    if (bean.getViewChange().equals("是")) {
                        cmPrintInfoBean.setViewChange("true");
                    } else {
                        cmPrintInfoBean.setViewChange("false");
                    }
                    list.add(uuid);
                    map.put(uuid, bean);
                }
                infolist.add(cmPrintInfoBean);
            }
            if (list != null && !list.isEmpty()) {
                saveChangeNoticeInfo(list, map);
            }
        } catch (SQLException e) {
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
        return infolist;
    }

    private static void saveChangeNoticeInfo(List<String> list, Map<String, CmImportBean> map) {
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            for (String id : list) {
                CmImportBean bean = map.get(id);
                String viewChange = "false";
                if (bean.getViewChange().equals("是")) {
                    viewChange = "true";
                }
                StringBuffer sb = new StringBuffer();
                sb.append("INSERT INTO GWOTHERCHANGENOTICE (GWKEYID, DEPT, FILENUMBER, CHANGEDATE, CHANGECONTENT, ISCODE) VALUES ");
                sb.append("('" + id + "','" + bean.getChangeNoticeOutDept() + "','" + bean.getChangeNoticeNumber() + "'");
                sb.append(",'" + bean.getChangeDate() + "','" + bean.getChangeContent() + "'");
                sb.append(",'" + viewChange + "')");
                state.executeUpdate(sb.toString());
            }
            conn.commit();
        } catch (SQLException e) {
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

    public static void setChangeFileState(List<String> list) {
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            for (String id : list) {
                StringBuffer sb = new StringBuffer();
                sb.append("UPDATE GWPRINTBARCODE SET FILESTATUS = '已终止' WHERE GWKEYID = '" + id + "'");
                state.executeUpdate(sb.toString());
            }
            conn.commit();
        } catch (SQLException e) {
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

    public static void saveLosePboNumber(String pboNumber, List<CmPrintRecordInfoBean> listBean) {
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            for (CmPrintRecordInfoBean cmPrintRecordInfoBean : listBean) {
                String gwkeyid = cmPrintRecordInfoBean.getUuid();
                StringBuffer sb = new StringBuffer();
                sb.append("UPDATE GWPRINTLOSERECORD SET LOSEPBONUMBER = '" + pboNumber + "' WHERE GWKEYID = '" + gwkeyid + "'");
                state.executeUpdate(sb.toString());
            }
            conn.commit();
        } catch (SQLException e) {
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

    public static void saveAddSealPlus(String id, String allBatch) {
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("UPDATE GWPRINTBARCODE SET BATCH = '" + allBatch + "', ADDBATCH = '' WHERE GWKEYID = '" + id + "'");
            state.executeUpdate(sb.toString());
            conn.commit();
        } catch (SQLException e) {
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

    /**
     * 储存补打信息
     *
     * @param id
     * @param offSet
     * @param oldSet
     * @return
     * @author zhuhao
     * @date 2018-5-25
     */
    public static String saveOffSetInfo(String id, String offSet, String oldSet) {
        String result = "";
        String newInfo = PrintUtil.buildNewDeptAndCount(offSet, oldSet);
        List<String> newInfoList = java.util.Arrays.asList(newInfo.split(","));
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            //更新gwprintapplyrecord表
            sb.append("UPDATE GWPRINTAPPLYRECORD SET DISMESSAGE = '" + newInfo + "' WHERE GWKEYID = '" + id + "'");
            state.executeUpdate(sb.toString());
            conn.commit();
            //更新gwprintDistributerecord表
            List<String> deptList = new ArrayList<String>();
            Map<String, String> deptMap = new HashMap<String, String>();
            for (String newDept : newInfoList) {
                String dept = newDept.substring(0, newDept.indexOf(":"));
                String count = newDept.substring(newDept.indexOf(":") + 1, newDept.indexOf("份"));
                deptList.add(dept);
                deptMap.put(dept, count);
            }
            List<CmPrintDistributerecordBean> list = PrintDataQueryUtil.queryPrintDistributerecord(id);
            List<CmPrintDistributerecordBean> newlist = new ArrayList<CmPrintDistributerecordBean>();
            List<String> oldDeptList = new ArrayList<String>();
            String docVR = "";
            List<String> barcodeDepts1 = new ArrayList<String>();//更新barcode表时所需要的集合和map
            Map<String, String> barcodeCounts1 = new HashMap<String, String>();
            Map<String, String> barcodeApId1 = new HashMap<String, String>();
            for (CmPrintDistributerecordBean bean : list) {
                CmPrintDistributerecordBean newBean = new CmPrintDistributerecordBean();
                docVR = bean.getDocVR();
                String dept = bean.getDistributedept();
                oldDeptList.add(dept);
                newBean.setGwkeyid(bean.getGwkeyid());
                newBean.setApplyrecordid(bean.getApplyrecordid());
                newBean.setDocVR(docVR);
                newBean.setDistributedept(dept);
                if (deptList.contains(dept)) {
                    newBean.setDistributequantity(deptMap.get(dept));
                    String count = String.valueOf(Integer.parseInt(deptMap.get(dept)) - Integer.parseInt(bean.getDistributequantity()));
                    barcodeDepts1.add(dept);
                    barcodeCounts1.put(dept, count);
                    barcodeApId1.put(dept, bean.getGwkeyid());
                } else {
                    newBean.setDistributequantity(bean.getDistributequantity());
                }
                newlist.add(newBean);
            }
            List<String> barcodeDepts2 = new ArrayList<String>();//更新barcode表时所需要的集合和map
            Map<String, String> barcodeCounts2 = new HashMap<String, String>();
            Map<String, String> barcodeApId2 = new HashMap<String, String>();
            for (String dept : deptList) {
                if (oldDeptList.contains(dept)) {
                    continue;
                }
                String uuid = UUID.randomUUID().toString();
                CmPrintDistributerecordBean newBean = new CmPrintDistributerecordBean();
                newBean.setGwkeyid(uuid);
                newBean.setApplyrecordid(id);
                newBean.setDocVR(docVR);
                newBean.setDistributedept(dept);
                newBean.setDistributequantity(deptMap.get(dept));
                barcodeDepts2.add(dept);
                barcodeCounts2.put(dept, deptMap.get(dept));
                barcodeApId2.put(dept, uuid);
                newlist.add(newBean);
            }
            updateGWPrintapplyrecordForOffSet(newlist);
            //更新gwprintbarcode表
            String batch = CommonUtil.objectToString(PrintDataQueryUtil.getBatchById(id));
            result = updateGWPrintbarcodeForOffSet(barcodeDepts1, barcodeCounts1, barcodeApId1, batch, result);
            result = updateGWPrintbarcodeForOffSet(barcodeDepts2, barcodeCounts2, barcodeApId2, batch, result);
        } catch (SQLException e) {
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
        return result;
    }

    private static String updateGWPrintbarcodeForOffSet(List<String> barcodeDepts, Map<String, String> barcodeCounts, Map<String, String> barcodeApId, String batch, String result) {
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            for (String dept : barcodeDepts) {
                String count = barcodeCounts.get(dept);
                String id = barcodeApId.get(dept);
                int counts = Integer.parseInt(count);
                for (int i = 0; i < counts; i++) {
                    StringBuffer sb = new StringBuffer();
                    String uuid = UUID.randomUUID().toString();
                    sb.append("INSERT INTO GWPRINTBARCODE (GWKEYID, APPLYRECORDID, GDEPT, FILESTATUS, BATCH, OFFSET) VALUES ");
                    sb.append("('" + uuid + "','" + id + "','" + dept + "','未打印','" + batch + "','true')");
                    state.executeUpdate(sb.toString());
                    if ("".equals(result)) {
                        result = uuid;
                    } else {
                        result = result + "," + uuid;
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
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
        return result;

    }

    private static void updateGWPrintapplyrecordForOffSet(List<CmPrintDistributerecordBean> newlist) {
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            for (CmPrintDistributerecordBean bean : newlist) {
                StringBuffer sb = new StringBuffer();
                sb.append("DELETE FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID = '" + bean.getGwkeyid() + "'");
                state.executeUpdate(sb.toString());
            }
            conn.commit();
            for (CmPrintDistributerecordBean bean : newlist) {
                StringBuffer sb = new StringBuffer();
                sb.append("INSERT INTO GWPRINTDISTRIBUTERECORD (GWKEYID, APPLYRECORDID, DOCVR, DISTRIBUTEDEPT, DISTRIBUTEQUANTITY) ");
                sb.append("VALUES ('" + bean.getGwkeyid() + "','" + bean.getApplyrecordid() + "','" + bean.getDocVR() + "','" + bean.getDistributedept() + "','" + bean.getDistributequantity() + "')");
                state.executeUpdate(sb.toString());
            }
            conn.commit();
        } catch (SQLException e) {
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

    public static void restoreLinkForOffSet(String id, List<String> oldDeptList, Map<String, String> oldMap) {
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            List<String> deleteList = PrintDataQueryUtil.queryLinkForOffSet(id, oldDeptList);
            if (deleteList != null && !deleteList.isEmpty()) {
                for (String gwkeyid : deleteList) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("DELETE FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID = '" + gwkeyid + "'");
                    state.executeUpdate(sb.toString());
                }
            }
            List<String> list = PrintDataQueryUtil.queryLinkChangeOffSet(id, oldDeptList);
            Map<String, String> map = PrintDataQueryUtil.queryMapChangeOffSet(id, oldDeptList);
            for (String gwkeyid : list) {
                String dept = map.get(gwkeyid);
                StringBuffer sb = new StringBuffer();
                sb.append("UPDATE GWPRINTDISTRIBUTERECORD SET DISTRIBUTEQUANTITY = '" + oldMap.get(dept) + "' WHERE GWKEYID = '" + gwkeyid + "'");
                state.executeUpdate(sb.toString());
            }
            conn.commit();
        } catch (SQLException e) {
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

    public static void deleteBarcodeForOffSet(String ids) {
        List<String> idList = java.util.Arrays.asList(ids.split(","));
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            for (String id : idList) {
                StringBuffer sb = new StringBuffer();
                sb.append("DELETE FROM GWPRINTBARCODE WHERE GWKEYID = '" + id + "'");
                state.executeUpdate(sb.toString());
            }
            conn.commit();
        } catch (SQLException e) {
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

    public static void checkRepeatForAutoPrintApplyBypboOid(String pboOid) {
        boolean repeat = false;
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT * FROM GWPRINTAPPLYRECORD WHERE PBOOID = '" + pboOid + "'");
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                repeat = true;
                break;
            }
            if (repeat) {
                removeRepeatData(pboOid);
            }
        } catch (SQLException e) {
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

    public static void removeRepeatData(String pboOid) {
        String delSql1 = "DELETE FROM gwprintbarcode WHERE applyrecordid IN " +
                "(SELECT GWKEYID FROM gwprintdistributerecord WHERE applyrecordid IN " +
                "(SELECT GWKEYID FROM gwprintapplyrecord WHERE gwprintapplyrecord.pbooid = '" + pboOid + "'))";
        String delSql2 = "DELETE FROM gwprintdistributerecord WHERE applyrecordid IN " +
                "(SELECT GWKEYID FROM gwprintapplyrecord WHERE gwprintapplyrecord.pbooid = '" + pboOid + "')";
        String delSql3 = "DELETE FROM GWPRINTAPPLYRECORD WHERE PBOOID = '" + pboOid + "'";
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            state.executeUpdate(delSql1);
            state.executeUpdate(delSql2);
            state.executeUpdate(delSql3);
            conn.commit();
        } catch (SQLException e) {
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

}
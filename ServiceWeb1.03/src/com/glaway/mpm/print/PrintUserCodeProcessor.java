package com.glaway.mpm.print;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import com.glaway.mpm.mpmresource.gznumber.number.AdministrationHelper;
import com.glaway.mpm.print.util.InsideFileImport;
import com.glaway.mpm.util.UserUtil;
import ext.casc.util.CSCPrincipal;
import org.apache.commons.lang.StringUtils;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.pds.oracle81.OracleDataSource;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import com.glaway.mpm.parameter.util.PersistableUtil;
import com.glaway.mpm.print.bean.CmUserQrCodeBean;
import com.glaway.mpm.print.data.CmImportBean;
import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.data.CmPrintRecordQueryBean;
import com.glaway.mpm.print.data.CmSealBean;
import com.glaway.mpm.print.util.PrintDataQueryUtil;
import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.Util;
import wt.util.WTRuntimeException;

public class PrintUserCodeProcessor {

    public static FormResult save(NmCommandBean commandBean) throws Exception {
        FormResult formResult = new FormResult();
        formResult.setStatus(FormProcessingStatus.SUCCESS);
        //获取选中数据
        ArrayList<?> list = commandBean.getSelectedOidForPopup();
        List<CmUserQrCodeBean> userBeanList = new ArrayList<CmUserQrCodeBean>();
        for (int i = 0; i < list.size(); i++) {
            NmOid nmOid = (NmOid) list.get(i);
            Object object = nmOid.getRefObject();
            if (object instanceof WTUser) {
                WTUser user = (WTUser) object;
                long oid = user.getPersistInfo().getObjectIdentifier().getId();
                HashMap<String, ?> map = commandBean.getParameterMap();
                String userCode = "";
                String dept = "";
                for (String key : map.keySet()) {
                    //二维码
                    if (key.indexOf(oid + "_userCode") != -1 && !key.endsWith("old")) {
                        String[] array = (String[]) map.get(key);
                        userCode = array[0];
                    } else if (key.indexOf(oid + "_dept") != -1) {//部门
                        String[] array = (String[]) map.get(key);
                        dept = array[0];
                    }

                }
                if (!"".equals(userCode) && !"".equals(dept)) {//组建Bean
                    CmUserQrCodeBean userQrCodeBean = new CmUserQrCodeBean();
                    userQrCodeBean.setDept(dept);
                    userQrCodeBean.setFullName(user.getFullName());
                    userQrCodeBean.setUserCode(userCode);
                    userQrCodeBean.setUserName(user.getName());
                    userQrCodeBean.setUserOid(String.valueOf(oid));
                    userBeanList.add(userQrCodeBean);
                }
            }
        }

        //保存至数据库
        saveQRCode(userBeanList);

        formResult.setNextAction(FormResultAction.NONE);
        return formResult;
    }

    public static void saveQRCode(List<CmUserQrCodeBean> userBeanList) throws SQLException {
        if (userBeanList != null && !userBeanList.isEmpty()) {
            DBConnUtil conn = null;
            try {
                conn = new DBConnUtil();
                String querySQL = "";
                String updateSQL = "";
                for (CmUserQrCodeBean userBean : userBeanList) {
                    querySQL = "select * from GWUSERCODETABLE where useroid='" + userBean.getUserOid() + "'";
                    ResultSet rt = conn.executeQuery(querySQL);
                    if (rt.next()) {
                        updateSQL = "UPDATE GWUSERCODETABLE SET USERCODE = '" + userBean.getUserCode() + "' where useroid='" + userBean.getUserOid() + "'";
                    } else {
                        updateSQL = "INSERT INTO GWUSERCODETABLE VALUES('" + userBean.getUserCode() + "','" + userBean.getUserOid() + "','" + userBean.getUserName() + "','" + userBean.getFullName() + "','" + userBean.getDept() + "')";
                    }
                    conn.executeUpdate(updateSQL);
                    conn.commit();
                }
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        }
    }

    //删除
    public static Boolean deleteSealInfo(List<String> list) {//add by lkc 2017.12.12
        DBConnUtil con = null;
        Boolean flag = false;
        try {
            con = new DBConnUtil();
            for (String id : list) {
                String sql = "delete from GWYINZHANG where GWKEYID = '" + id + "'";
                con.executeUpdate(sql);
                con.commit();
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static void updateNumberToDB(List<CmSealBean> listBean) {//add by lkc 2017.12.12
        DBConnUtil con = null;
        Boolean flag = false;
        try {
            con = new DBConnUtil();
            for (CmSealBean cmSealBean : listBean) {
                String gwkeyid = cmSealBean.getGwKeyId();
                String number = cmSealBean.getNumber();
                String sql = "update GWYINZHANG set SEALNUMBER = '" + number + "' where GWKEYID = '" + gwkeyid + "'";
                con.executeUpdate(sql);
                con.commit();
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //修改
    public static Boolean alterSealInfo(String uuid, String name) {//add by lkc 2017.12.12
        DBConnUtil con = null;
        Boolean flag = false;
        try {
            con = new DBConnUtil();
            String sql = "update GWYINZHANG set SEALNAME ='" + name + "'where GWKEYID ='" + uuid + "'";
            con.executeUpdate(sql);
            con.commit();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    //获取印章表中的最大值
    public static String queryMaxNumber() {//add by lkc 2017.12.12
        DBConnUtil con = null;
        String maxNumber = null;
        try {
            con = new DBConnUtil();
            String sql = "select max(TO_NUMBER(sealnumber)) as ID from gwyinzhang";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                maxNumber = rs.getString("ID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return maxNumber;
    }

    //添加
    public static Boolean addSealInfo(String uuid, String name, String number) {//add by lkc 2017.12.12
        DBConnUtil con = null;
        Boolean flag = false;
        try {
            con = new DBConnUtil();
            String sql = "insert into GWYINZHANG(GWKEYID, SEALNAME, SEALNUMBER) values('" + uuid + "','" + name + "', '" + number + "')";
            con.executeUpdate(sql);
            con.commit();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    //查询
    public static List<CmSealBean> selectSealInfo(String sealName) {//add by lkc 2017.12.12
        List<CmSealBean> list = new ArrayList<CmSealBean>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            String sql = "select * from GWYINZHANG where 1 = 1";
            if (!"".equals(sealName) && null != sealName) {
                sql += "and SEALNAME like '%" + sealName + "%' ";
            }
            sql += "order by SEALNUMBER ASC";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                CmSealBean bean = new CmSealBean();
                bean.setGwKeyId(rs.getString("GWKEYID"));
                bean.setName(rs.getString("SEALNAME"));
                bean.setNumber(rs.getString("SEALNUMBER"));
                list.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    //	public static List<String> queryRecoverBarcode(String delayStatus){
//		List<String> list = new ArrayList<String>();
//		DBConnUtil con = null;
//		try {
//			con = new DBConnUtil();
//			int yanchi = 0;
//			if("延迟回收".equals(delayStatus)){
//				yanchi = 1;
//			}else{
//				yanchi = 0;
//			}
//			String sql = "select PRINTBARCODEID from GWPRINTRECOVERRECORD where ISYANCHI = '"+ yanchi +"'";
//			ResultSet rs = con.executeQuery(sql);
//			while(rs.next()){
//				String barCode = rs.getString("PRINTBARCODEID");
//				list.add(barCode);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return list;
//	}
    //查看分发文件信息
    public static List<CmPrintRecordInfoBean> queryDistributeInfo(CmPrintRecordQueryBean cmPrintRecordQueryBean, String category) {
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            String fileNumber = cmPrintRecordQueryBean.getFileNumber();
            String fileName = cmPrintRecordQueryBean.getFileName();
            String fileType = cmPrintRecordQueryBean.getFileType();
            String fileVersion = cmPrintRecordQueryBean.getDocVersion();
            String phaseCode = cmPrintRecordQueryBean.getPhaseCode();
            String distributeStatus = cmPrintRecordQueryBean.getDistributeStatus();
            String printStartDate = cmPrintRecordQueryBean.getPrintStartDate();
            String printEndDate = cmPrintRecordQueryBean.getPrintEndDate();
            String sql = "select * from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c where " +
                    "a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.GDEPT = '档案室' ";
            if ("WL".equals(category)) {
                sql = sql + "and a.OUTDEPT != '厂内电子' and a.OUTDEPT != '厂内纸质' ";
            } else if ("ZZ".equals(category)) {
                sql = sql + "and a.OUTDEPT = '厂内纸质' ";
            } else {
                sql = sql + "and a.OUTDEPT = '厂内电子' ";
            }

            if ((!"".equals(fileNumber) && null != fileNumber)) {
                sql += "and a.DOCNUMBER like '%" + fileNumber + "%'";
            }
            if ((!"".equals(fileName) && null != fileName)) {
                sql += "and a.DOCNAME like '%" + fileName + "%'";
            }
            if ((!"".equals(fileType) && null != fileType)) {
                sql += "and a.FILETYPE = '" + fileType + "'";
            }
            if ((!"".equals(fileVersion) && null != fileVersion)) {
                sql += "and a.VERSION like '%" + fileVersion + "%'";
            }
            if ((!"".equals(phaseCode) && null != phaseCode)) {
                sql += "and a.PHASECODE = '" + phaseCode + "'";
            }
            if ((!"".equals(distributeStatus) && null != distributeStatus)) {
                sql += "and c.FILESTATUS = '" + distributeStatus + "'";
            }
            if (((!"".equals(printStartDate) && null != printStartDate)) && ((!"".equals(printEndDate) && null != printEndDate))) {
                sql += "and c.PDATE between '" + printStartDate + "' and '" + printEndDate + "'";
            }
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                CmPrintRecordInfoBean cmPrintRecordQueryBean1 = new CmPrintRecordInfoBean();
                cmPrintRecordQueryBean1.setFileNumber(rs.getString("DOCNUMBER"));
                cmPrintRecordQueryBean1.setFileName(rs.getString("DOCNAME"));
                cmPrintRecordQueryBean1.setDocVersion(rs.getString("VERSION"));
                cmPrintRecordQueryBean1.setPhaseCode(rs.getString("PHASECODE"));
                cmPrintRecordQueryBean1.setFileType(rs.getString("FILETYPE"));
                cmPrintRecordQueryBean1.setSecret(rs.getString("SECRET"));
                cmPrintRecordQueryBean1.setApplyUser(rs.getString("APPLIER"));
                cmPrintRecordQueryBean1.setApplyDate(rs.getString("APPLYDATE"));
                cmPrintRecordQueryBean1.setPrintUser(rs.getString("PUSER"));
                cmPrintRecordQueryBean1.setPrintDate(rs.getString("PDATE"));
                cmPrintRecordQueryBean1.setDistributeStatus(rs.getString("FILESTATUS"));
                cmPrintRecordQueryBean1.setBarCode(rs.getString("BARCODE"));
                cmPrintRecordQueryBean1.setGetDate(rs.getString("GDATE"));
                cmPrintRecordQueryBean1.setGetDept(rs.getString("GDEPT"));
                cmPrintRecordQueryBean1.setGetUser(rs.getString("GUSER"));
                listBean.add(cmPrintRecordQueryBean1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listBean;
    }

    //厂内文件封存查看分发文件信息
    public static List<CmPrintRecordInfoBean> inFactoryQueryDistributeInfoOfStore(CmPrintRecordInfoBean cmPrintRecordQueryBean, String category) {
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            String fileNumber = cmPrintRecordQueryBean.getFileNumber();
            String fileName = cmPrintRecordQueryBean.getFileName();
            String fileVersion = cmPrintRecordQueryBean.getDocVersion();
            String fileType = cmPrintRecordQueryBean.getFileType();
            String phaseCode = cmPrintRecordQueryBean.getPhaseCode();
            String distributeStatus = cmPrintRecordQueryBean.getDistributeStatus();
            String printStartDate = cmPrintRecordQueryBean.getPrintStartDate();
            String printEndDate = cmPrintRecordQueryBean.getPrintEndDate();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT * FROM GWPRINTBARCODE WHERE APPLYRECORDID IN ");
            sb.append("(SELECT GWKEYID FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID IN ");
            sb.append("(SELECT GWKEYID FROM GWPRINTAPPLYRECORD");
            if ("WL".equals(category)) {
                sb.append(" WHERE OUTDEPT != '厂内电子' AND OUTDEPT != '厂内纸质'");
            } else if ("ZZ".equals(category)) {
                sb.append(" WHERE OUTDEPT = '厂内纸质'");
            } else {
                sb.append(" WHERE OUTDEPT = '厂内电子'");
            }
            if (!"".equals(fileNumber) && null != fileNumber) {
                sb.append(" AND DOCNUMBER LIKE '%" + fileNumber + "%'");
            }
            if (!"".equals(fileName) && null != fileName) {
                sb.append(" AND DOCNAME LIKE '%" + fileName + "%'");
            }
            if (!"".equals(fileVersion) && null != fileVersion) {
                sb.append(" AND VERSION LIKE '%" + fileVersion + "%'");
            }
            if (!"".equals(fileType) && null != fileType) {
                sb.append(" AND FILETYPE LIKE '%" + fileType + "%'");
            }
            if (!"".equals(phaseCode) && null != phaseCode) {
                sb.append(" AND PHASECODE = '" + phaseCode + "'");
            }
            sb.append("))");
            if (!"".equals(distributeStatus) && null != distributeStatus) {
                sb.append(" AND FILESTATUS = '" + distributeStatus + "'");
            }
            if (((!"".equals(printStartDate) && null != printStartDate)) && ((!"".equals(printEndDate) && null != printEndDate))) {
                sb.append(" AND PDATE BETWEEN '" + printStartDate + "' AND '" + printEndDate + "'");
            }
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
                String id = rs.getString("GWKEYID");
                cmPrintRecordInfoBean.setBarTableID(id);
                cmPrintRecordInfoBean.setDistributeStatus(rs.getString("FILESTATUS"));
                cmPrintRecordInfoBean.setBarCode(rs.getString("BARCODE"));
                cmPrintRecordInfoBean.setPrintUser(rs.getString("PUSER"));
                cmPrintRecordInfoBean.setPrintDate(rs.getString("PDATE"));
                cmPrintRecordInfoBean.setGetDate(rs.getString("GDATE"));
                cmPrintRecordInfoBean.setGetDept(rs.getString("GDEPT"));
                cmPrintRecordInfoBean.setGetUser(rs.getString("GUSER"));
                cmPrintRecordInfoBean = buildApplyInfoOfStore(cmPrintRecordInfoBean, id);
                if ("已封存".equals(rs.getString("FILESTATUS"))) {
                    String sql1 = "select STORETIME from GWPRINTSTORERECORD where APPLYRECORDID = '" + id + "'";
                    DBConnUtil con = null;
                    con = new DBConnUtil();
                    ResultSet rs1 = con.executeQuery(sql1);
                    while (rs1.next()) {
                        cmPrintRecordInfoBean.setStoreTime(rs1.getString("STORETIME"));
                    }
                    if (con != null) {
                        con.close();
                    }
                }
                listBean.add(cmPrintRecordInfoBean);
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
        return listBean;
    }

    private static CmPrintRecordInfoBean buildApplyInfoOfStore(CmPrintRecordInfoBean cmPrintRecordInfoBean, String id) {
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT * FROM GWPRINTAPPLYRECORD WHERE GWKEYID IN ");
            sb.append("(SELECT APPLYRECORDID FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID IN ");
            sb.append("(SELECT APPLYRECORDID FROM GWPRINTBARCODE WHERE GWKEYID = '" + id + "'))");
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                String fileNumber = rs.getString("DOCNUMBER");
                String fileName = rs.getString("DOCNAME");
                String version = rs.getString("VERSION");
                cmPrintRecordInfoBean.setFileNumber(fileNumber);
                cmPrintRecordInfoBean.setFileName(fileName);
                cmPrintRecordInfoBean.setDocVersion(version);
                cmPrintRecordInfoBean.setPhaseCode(rs.getString("PHASECODE"));
                cmPrintRecordInfoBean.setFileType(rs.getString("FILETYPE"));
                cmPrintRecordInfoBean.setSecret(rs.getString("SECRET"));
                cmPrintRecordInfoBean.setApplyUser(rs.getString("APPLIER"));
                cmPrintRecordInfoBean.setApplyDate(rs.getString("APPLYDATE"));
                String docVR = rs.getString("DOCVR");
                if (null != docVR) {
                    String containerName = "";
                    if ("WL".equals(docVR) || "ZZ".equals(docVR)) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("number", fileNumber);
                        map.put("name", fileName);
                        map.put("version", version);
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
                    cmPrintRecordInfoBean.setContainerName(containerName);
                }
                break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (WTException e) {
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
        return cmPrintRecordInfoBean;
    }

    //外来文件封存查看分发文件信息
    public static List<CmPrintRecordInfoBean> outsideQueryDistributeInfoOfStore(CmPrintRecordInfoBean cmPrintRecordQueryBean) {
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            String distributeStatus = cmPrintRecordQueryBean.getDistributeStatus();
            String fileType = cmPrintRecordQueryBean.getFileType();
            String fileNumber = cmPrintRecordQueryBean.getFileNumber();
            String fileName = cmPrintRecordQueryBean.getFileName();
            String fileVersion = cmPrintRecordQueryBean.getDocVersion();
            String printDate = cmPrintRecordQueryBean.getPrintDate();
            String getDate = cmPrintRecordQueryBean.getGetDate();
            String getDept = cmPrintRecordQueryBean.getGetDept();
            String getUser = cmPrintRecordQueryBean.getGetUser();
            String sql = "select a.*, b.*, c.FILESTATUS, c.GWKEYID as ID, c.BARCODE, c.GDATE, c.GDEPT, c.GUSER from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and a.OUTDEPT not like '%厂内%' ";
            if ((!"".equals(distributeStatus) && null != distributeStatus)) {
                sql += "and c.FILESTATUS = '" + distributeStatus + "'";
            }
            if ((!"".equals(fileNumber) && null != fileNumber)) {
                sql += "and a.DOCNUMBER like '%" + fileNumber + "%'";
            }
            if ((!"".equals(fileName) && null != fileName)) {
                sql += "and a.DOCNAME like '%" + fileName + "%'";
            }
            if ((!"".equals(fileType) && null != fileType)) {
                sql += "and a.FILETYPE like '%" + fileType + "%'";
            }
            if ((!"".equals(fileVersion) && null != fileVersion)) {
                sql += "and a.VERSION like '%" + fileVersion + "%'";
            }
            if ((!"".equals(printDate) && null != printDate)) {
                sql += "and c.PDATE = '" + printDate + "'";
            }
            if ((!"".equals(getDate) && null != getDate)) {
                sql += "and c.GDATE = '" + getDate + "'";
            }
            if ((!"".equals(getDept) && null != getDept)) {
                sql += "and c.GDEPT like '%" + getDept + "%'";
            }
            if ((!"".equals(getUser) && null != getUser)) {
                sql += "and c.GUSER like '%" + getUser + "%'";
            }
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                CmPrintRecordInfoBean cmPrintRecordQueryBean1 = new CmPrintRecordInfoBean();
                cmPrintRecordQueryBean1.setBarTableID(rs.getString("ID"));
                cmPrintRecordQueryBean1.setFileNumber(rs.getString("DOCNUMBER"));
                cmPrintRecordQueryBean1.setFileName(rs.getString("DOCNAME"));
                cmPrintRecordQueryBean1.setDocVersion(rs.getString("VERSION"));
                cmPrintRecordQueryBean1.setPhaseCode(rs.getString("PHASECODE"));
                cmPrintRecordQueryBean1.setFileType(rs.getString("FILETYPE"));
                cmPrintRecordQueryBean1.setSecret(rs.getString("SECRET"));
                cmPrintRecordQueryBean1.setApplyUser(rs.getString("APPLIER"));
                cmPrintRecordQueryBean1.setApplyDate(rs.getString("APPLYDATE"));
                cmPrintRecordQueryBean1.setDistributeStatus(rs.getString("FILESTATUS"));
                cmPrintRecordQueryBean1.setBarCode(rs.getString("BARCODE"));
                cmPrintRecordQueryBean1.setGetDate(rs.getString("GDATE"));
                cmPrintRecordQueryBean1.setGetDept(rs.getString("GDEPT"));
                cmPrintRecordQueryBean1.setGetUser(rs.getString("GUSER"));
                //cmPrintRecordQueryBean1.setStoreTime(rs.getString("STORETIME"));
                listBean.add(cmPrintRecordQueryBean1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listBean;
    }

    //外来文件封存查看分发文件信息
    public static List<CmPrintRecordInfoBean> queryPaperDistributeInfoOfStore(CmPrintRecordInfoBean cmPrintRecordQueryBean) {
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            String distributeStatus = cmPrintRecordQueryBean.getDistributeStatus();
            String fileType = cmPrintRecordQueryBean.getFileType();
            String fileNumber = cmPrintRecordQueryBean.getFileNumber();
            String fileName = cmPrintRecordQueryBean.getFileName();
            String fileVersion = cmPrintRecordQueryBean.getDocVersion();
            String printDate = cmPrintRecordQueryBean.getPrintDate();
            String getDate = cmPrintRecordQueryBean.getGetDate();
            String getDept = cmPrintRecordQueryBean.getGetDept();
            String getUser = cmPrintRecordQueryBean.getGetUser();
            String sql = "select a.*, b.*, c.FILESTATUS, c.GWKEYID as ID, c.BARCODE, c.GDATE, c.GDEPT, c.GUSER from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and a.OUTDEPT = '厂内纸质' ";
            if ((!"".equals(distributeStatus) && null != distributeStatus)) {
                sql += "and c.FILESTATUS = '" + distributeStatus + "'";
            }
            if ((!"".equals(fileNumber) && null != fileNumber)) {
                sql += "and a.DOCNUMBER like '%" + fileNumber + "%'";
            }
            if ((!"".equals(fileName) && null != fileName)) {
                sql += "and a.DOCNAME like '%" + fileName + "%'";
            }
            if ((!"".equals(fileType) && null != fileType)) {
                sql += "and a.FILETYPE like '%" + fileType + "%'";
            }
            if ((!"".equals(fileVersion) && null != fileVersion)) {
                sql += "and a.VERSION like '%" + fileVersion + "%'";
            }
            if ((!"".equals(printDate) && null != printDate)) {
                sql += "and c = PDATE'" + printDate + "'";
            }
            if ((!"".equals(getDate) && null != getDate)) {
                sql += "and c.GDATE = '" + getDate + "'";
            }
            if ((!"".equals(getDept) && null != getDept)) {
                sql += "and c.GDEPT like '%" + getDept + "%'";
            }
            if ((!"".equals(getUser) && null != getUser)) {
                sql += "and c.GUSER like '%" + getUser + "%'";
            }
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                CmPrintRecordInfoBean cmPrintRecordQueryBean1 = new CmPrintRecordInfoBean();
                cmPrintRecordQueryBean1.setBarTableID(rs.getString("ID"));
                cmPrintRecordQueryBean1.setFileNumber(rs.getString("DOCNUMBER"));
                cmPrintRecordQueryBean1.setFileName(rs.getString("DOCNAME"));
                cmPrintRecordQueryBean1.setDocVersion(rs.getString("VERSION"));
                cmPrintRecordQueryBean1.setPhaseCode(rs.getString("PHASECODE"));
                cmPrintRecordQueryBean1.setFileType(rs.getString("FILETYPE"));
                cmPrintRecordQueryBean1.setSecret(rs.getString("SECRET"));
                cmPrintRecordQueryBean1.setApplyUser(rs.getString("APPLIER"));
                cmPrintRecordQueryBean1.setApplyDate(rs.getString("APPLYDATE"));
                cmPrintRecordQueryBean1.setDistributeStatus(rs.getString("FILESTATUS"));
                cmPrintRecordQueryBean1.setBarCode(rs.getString("BARCODE"));
                cmPrintRecordQueryBean1.setGetDate(rs.getString("GDATE"));
                cmPrintRecordQueryBean1.setGetDept(rs.getString("GDEPT"));
                cmPrintRecordQueryBean1.setGetUser(rs.getString("GUSER"));
                //cmPrintRecordQueryBean1.setStoreTime(rs.getString("STORETIME"));
                listBean.add(cmPrintRecordQueryBean1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listBean;
    }

    //保存发起封存流程时的封存时间
    public static String saveInfoOfStore(List<CmPrintRecordInfoBean> list) {
        DBConnUtil con = null;
        boolean isRestartFlag = false;
        try {
            con = new DBConnUtil();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                String id = list.get(i).getBarTableID();
                String fileNumber = list.get(i).getFileNumber();
                String sql = "select * from GWPRINTBARCODE where FILESTATUS = '封存中' and GWKEYID = '" + id + "'";
                ResultSet rs = con.executeQuery(sql);
                if (rs.next()) {
                    if (sb.indexOf(fileNumber) != -1) {
                        sb.append("");
                    } else {
                        if (sb.toString().isEmpty()) {
                            sb.append(fileNumber);
                        } else {
                            sb.append("," + fileNumber);
                        }
                    }
                }
                String sql2 = "select * from GWPRINTSTORERECORD where APPLYRECORDID = '" + id + "'";
                ResultSet rs2 = con.executeQuery(sql2);
                if (rs2.next()) {
                    String isRestart = rs2.getString("ISRESTART");
                    if ("true".equals(isRestart)) {
                        isRestartFlag = true;
                    } else {
                        if (sb.indexOf(fileNumber) != -1) {
                            sb.append("");
                        } else {
                            if (sb.toString().isEmpty()) {
                                sb.append(fileNumber);
                            } else {
                                sb.append("," + fileNumber);
                            }
                        }
                    }
                }
            }
            if ("".equals(sb.toString()) || null == sb.toString()) {
                for (int i = 0; i < list.size(); i++) {
                    String uuid = list.get(i).getUuid();
                    String id = list.get(i).getBarTableID();
                    String user = list.get(i).getCurrentUser();
                    String dept = list.get(i).getCurrentDept();
                    String time = list.get(i).getCurrentDate();
                    String storeTime = list.get(i).getStoreTime();
                    String sql2 = "";
                    if (isRestartFlag) {
                        sql2 = "update GWPRINTSTORERECORD set STARTUSER='" + user + "',STARTDEPT='" + dept + "',STARTDATE='" + time + "',STORETIME='" + storeTime + "',ISOPENSTORE = 'false',ISRESTART='false' where APPLYRECORDID = '" + id + "'";
                    } else {
                        sql2 = "insert into GWPRINTSTORERECORD(GWKEYID, APPLYRECORDID, STARTUSER, STARTDEPT, STARTDATE, STORETIME,ISOPENSTORE,ISRESTART) values('" + uuid + "','" + id + "','" + user + "','" + dept + "','" + time + "', '" + storeTime + "','false','false')";
                    }
                    //String sql3 = "update GWPRINTBARCODE set FILESTATUS = '封存中' where GWKEYID = '"+ id +"'";
                    con.executeUpdate(sql2);
                    //con.executeUpdate(sql3);
                    con.commit();
                }
            } else {
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String checkInfoOfOpenStore(List<CmPrintRecordInfoBean> list) {
        DBConnUtil con = null;
        StringBuffer sb = new StringBuffer();
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String id = list.get(i).getBarTableID();
                String fileNumber = list.get(i).getFileNumber();
                String sql = "select * from GWPRINTSTORERECORD where ISOPENSTORE = 'true' and APPLYRECORDID = '" + id + "'";
                ResultSet rs = con.executeQuery(sql);
                if (rs.next()) {
                    if (sb.indexOf(fileNumber) != -1) {
                        sb.append("");
                    } else {
                        if (sb.toString().isEmpty()) {
                            sb.append(fileNumber);
                        } else {
                            sb.append("," + fileNumber);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void updateOpenStoreStatus(List<String> list) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String id = list.get(i).toString();
                String sql = "update GWPRINTSTORERECORD set ISOPENSTORE = 'true' where APPLYRECORDID = '" + id + "'";
                con.executeUpdate(sql);
                con.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static String checkInfoOfDelayByRecover(List<CmPrintRecordInfoBean> list) {
        DBConnUtil con = null;
        StringBuffer sb = new StringBuffer();
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String id = list.get(i).getBarTableID();
                String fileNumber = list.get(i).getFileNumber();
                String sql = "select * from GWPRINTBARCODE where  FILESTATUS = '回收中' and GWKEYID = '" + id + "'";
                ResultSet rs = con.executeQuery(sql);
                if (rs.next()) {
                    if (sb.indexOf(fileNumber) != -1) {
                        sb.append("");
                    } else {
                        if (sb.toString().isEmpty()) {
                            sb.append(fileNumber);
                        } else {
                            sb.append("," + fileNumber);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static String checkInfoOfDelayByStore(List<CmPrintRecordInfoBean> list) {
        DBConnUtil con = null;
        StringBuffer sb = new StringBuffer();
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String id = list.get(i).getBarTableID();
                String fileNumber = list.get(i).getFileNumber();
                String sql = "select * from GWPRINTBARCODE where  FILESTATUS = '封存中' and GWKEYID = '" + id + "'";
                ResultSet rs = con.executeQuery(sql);
                if (rs.next()) {
                    if (sb.indexOf(fileNumber) != -1) {
                        sb.append("");
                    } else {
                        if (sb.toString().isEmpty()) {
                            sb.append(fileNumber);
                        } else {
                            sb.append("," + fileNumber);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void updateDelayStatusByRecover(List<String> list, String userName, String userDept) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String id = list.get(i).toString();
                String sql = "update GWPRINTBARCODE set FILESTATUS = '回收中' where GWKEYID = '" + id + "'";
                String sql1 = "update GWPRINTRECOVERRECORD set RECEIVEUSER = '" + userName + "', RECEIVEDEPT = '" + userDept + "' where APPLYRECORDID = '" + id + "'";
                con.executeUpdate(sql);
                con.executeUpdate(sql1);
                con.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateDelayStatusByStore(List<String> list, String userName, String userDept) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String id = list.get(i).toString();
                String sql = "update GWPRINTBARCODE set FILESTATUS = '封存中' where GWKEYID = '" + id + "'";
                String sql1 = "update GWPRINTSTORERECORD set STOREUSER = '" + userName + "', STOREDEPT = '" + userDept + "' where APPLYRECORDID = '" + id + "'";
                con.executeUpdate(sql);
                con.executeUpdate(sql1);
                con.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<CmPrintRecordInfoBean> storeInfoByDept(List<String> list, String dept) {
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String id = list.get(i).toString();
                String sql = "select a.*,b.*,c.*,d.STORETIME as STORETIMESTORE from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c, GWPRINTSTORERECORD d where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid = d.applyrecordid and c.GWKEYID = '" + id + "'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    CmPrintRecordInfoBean cmPrintRecordQueryBean = new CmPrintRecordInfoBean();
                    cmPrintRecordQueryBean.setFileNumber(rs.getString("DOCNUMBER"));
                    cmPrintRecordQueryBean.setFileName(rs.getString("DOCNAME"));
                    cmPrintRecordQueryBean.setMiddleStatus(rs.getString("FILESTATUS"));
                    cmPrintRecordQueryBean.setDocVersion(rs.getString("VERSION"));
                    cmPrintRecordQueryBean.setPhaseCode(rs.getString("PHASECODE"));
                    cmPrintRecordQueryBean.setSecret(rs.getString("SECRET"));
                    cmPrintRecordQueryBean.setGetDept(rs.getString("GDEPT"));
                    cmPrintRecordQueryBean.setStoreTime(rs.getString("STORETIMESTORE"));
                    cmPrintRecordQueryBean.setBarCode(rs.getString("BARCODE"));
                    listBean.add(cmPrintRecordQueryBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listBean;
    }

    public static List<CmPrintRecordInfoBean> storeInfo(List<String> list) {
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String id = list.get(i).toString();
                String sql = "select a.*,b.*,c.*,d.STORETIME as STORETIMESTORE from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c, GWPRINTSTORERECORD d where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid = d.applyrecordid and c.gwkeyid = '" + id + "'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    CmPrintRecordInfoBean cmPrintRecordQueryBean = new CmPrintRecordInfoBean();
                    cmPrintRecordQueryBean.setBarTableID(id);
                    cmPrintRecordQueryBean.setDocVersion(rs.getString("VERSION"));
                    cmPrintRecordQueryBean.setFileType(rs.getString("FILETYPE"));
                    cmPrintRecordQueryBean.setFileNumber(rs.getString("DOCNUMBER"));
                    cmPrintRecordQueryBean.setFileName(rs.getString("DOCNAME"));
                    cmPrintRecordQueryBean.setGetDate(rs.getString("GDATE"));
                    cmPrintRecordQueryBean.setGetDept(rs.getString("GDEPT"));
                    cmPrintRecordQueryBean.setGetUser(rs.getString("GUSER"));
                    cmPrintRecordQueryBean.setPhaseCode(rs.getString("PHASECODE"));
                    cmPrintRecordQueryBean.setSecret(rs.getString("SECRET"));
                    cmPrintRecordQueryBean.setApplyUser(rs.getString("APPLIER"));
                    cmPrintRecordQueryBean.setApplyDate(rs.getString("APPLYDATE"));
                    cmPrintRecordQueryBean.setMiddleStatus(rs.getString("FILESTATUS"));
                    cmPrintRecordQueryBean.setBarCode(rs.getString("BARCODE"));
                    cmPrintRecordQueryBean.setStoreTime(rs.getString("STORETIMESTORE"));
                    listBean.add(cmPrintRecordQueryBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listBean;
    }

    //该方法有待进一步修改
    public static List<CmPrintRecordInfoBean> queryDelayInfo(CmPrintRecordInfoBean cmPrintRecordQueryBean, List<String> list) {
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String barCode = list.get(i).toString();
                String fileType = cmPrintRecordQueryBean.getFileType();
                String fileNumber = cmPrintRecordQueryBean.getFileNumber();
                String fileName = cmPrintRecordQueryBean.getFileName();
                String fileVersion = cmPrintRecordQueryBean.getDocVersion();
                String printDate = cmPrintRecordQueryBean.getPrintDate();
                String getDate = cmPrintRecordQueryBean.getGetDate();
                String getDept = cmPrintRecordQueryBean.getGetDept();
                String getUser = cmPrintRecordQueryBean.getGetUser();
                String sql = "select a.*, b.*, c.*, d.GWKEYID as UUID from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c , GWPRINTRECOVERRECORD d where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid = d.applyrecordid and c.BARCODE = '" + barCode + "' and d.PRINTBARCODEID = '" + barCode + "'";
                if ((!"".equals(fileNumber) && null != fileNumber)) {
                    sql += "and a.DOCNUMBER like '%" + fileNumber + "%'";
                }
                if ((!"".equals(fileName) && null != fileName)) {
                    sql += "and a.DOCNAME like '%" + fileName + "%'";
                }
                if ((!"".equals(fileType) && null != fileType)) {
                    sql += "and a.FILETYPE like '%" + fileType + "%'";
                }
                if ((!"".equals(fileVersion) && null != fileVersion)) {
                    sql += "and a.VERSION like '%" + fileVersion + "%'";
                }
                if ((!"".equals(printDate) && null != printDate)) {
                    sql += "and c.PDATE = '" + printDate + "'";
                }
                if ((!"".equals(getDate) && null != getDate)) {
                    sql += "and c.GDATE = '" + getDate + "'";
                }
                if ((!"".equals(getDept) && null != getDept)) {
                    sql += "and c.GDEPT like '%" + getDept + "%'";
                }
                if ((!"".equals(getUser) && null != getUser)) {
                    sql += "and c.GUSER like '%" + getUser + "%'";
                }
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    CmPrintRecordInfoBean cmPrintRecordQueryBean1 = new CmPrintRecordInfoBean();
                    cmPrintRecordQueryBean1.setUuid(rs.getString("UUID"));
                    cmPrintRecordQueryBean1.setFileNumber(rs.getString("DOCNUMBER"));
                    cmPrintRecordQueryBean1.setFileName(rs.getString("DOCNAME"));
                    cmPrintRecordQueryBean1.setDocVersion(rs.getString("VERSION"));
                    cmPrintRecordQueryBean1.setPhaseCode(rs.getString("PHASECODE"));
                    cmPrintRecordQueryBean1.setFileType(rs.getString("FILETYPE"));
                    cmPrintRecordQueryBean1.setSecret(rs.getString("SECRET"));
                    cmPrintRecordQueryBean1.setApplyUser(rs.getString("APPLIER"));
                    cmPrintRecordQueryBean1.setApplyDate(rs.getString("APPLYDATE"));
                    cmPrintRecordQueryBean1.setDistributeStatus(rs.getString("ISHUISHOU"));
                    cmPrintRecordQueryBean1.setBarCode(rs.getString("BARCODE"));
                    cmPrintRecordQueryBean1.setGetDate(rs.getString("GDATE"));
                    cmPrintRecordQueryBean1.setGetDept(rs.getString("GDEPT"));
                    cmPrintRecordQueryBean1.setGetUser(rs.getString("GUSER"));
                    listBean.add(cmPrintRecordQueryBean1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listBean;
    }

    public static List<CmPrintRecordInfoBean> queryInfo(CmPrintRecordQueryBean cmPrintRecordQueryBean) {
        List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
        DBConnUtil con = null;
        DBConnUtil conn = null;
        try {
            con = new DBConnUtil();
            String outDept = cmPrintRecordQueryBean.getUnit();
//				String mindex = cmPrintRecordQueryBean.getType();
            String fileType = cmPrintRecordQueryBean.getFileType();
            String fileNumber = cmPrintRecordQueryBean.getFileNumber();
            String fileName = cmPrintRecordQueryBean.getFileName();
            String distributeStatus = cmPrintRecordQueryBean.getDistributeStatus();
            String printStartDate = cmPrintRecordQueryBean.getPrintStartDate();
            String printEndDate = cmPrintRecordQueryBean.getPrintEndDate();
            String getUser = cmPrintRecordQueryBean.getGetUser();
            String type = cmPrintRecordQueryBean.getType();
            String getDept = cmPrintRecordQueryBean.getGetDept();
            String sql = "";
//				if((!"".equals(recoverDate) && null != recoverDate)){
//					sql = "select a.*, b.*, d.RECEIVEDATE, c.GWKEYID as ID, c.BARCODE, c.FILESTATUS, c.GDATE, c.GDEPT, c.GUSER, c.PUSER,c.PDATE" +
//							"from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c, GWPRINTRECOVERRECORD d " +
//							"where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid = d.applyrecordid ";
//				}else{
            sql = "select a.*, b.*, c.GWKEYID as ID, c.BARCODE, c.FILESTATUS, c.GDATE, c.GDEPT, c.GUSER,c.PUSER,c.PDATE " +
                    "from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c " +
                    "where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid ";
            //}
            if ((!"".equals(outDept) && null != outDept)) {
                if ("厂内".equals(outDept)) {
                    sql += "and a.OUTDEPT like '%" + outDept + "%'";
                } else {
                    sql += "and a.OUTDEPT not like '%厂内%'";
                }
            }
//				if((!"".equals(mindex) && null != mindex)){
//					sql += "and a.MINDEX like '%"+mindex+"%'";
//				}
            if ((!"".equals(fileNumber) && null != fileNumber)) {
                sql += "and a.DOCNUMBER like '%" + fileNumber + "%'";
            }
            if ((!"".equals(fileName) && null != fileName)) {
                sql += "and a.DOCNAME like '%" + fileName + "%'";
            }
            if ((!"".equals(fileType) && null != fileType)) {
                sql += "and a.FILETYPE = '" + fileType + "'";
            }
            if ((!"".equals(distributeStatus) && null != distributeStatus)) {
                sql += "and c.FILESTATUS = '" + distributeStatus + "'";
            }
            if (((!"".equals(printStartDate) && null != printStartDate)) && ((!"".equals(printEndDate) && null != printEndDate))) {
                sql += "and c.PDATE between '" + printStartDate + "' and '" + printEndDate + "'";
            }
//				if((!"".equals(getDate) && null != getDate)){
//					sql += "and c.GDATE = '"+ getDate +"'";
//				}
            if ((!"".equals(getDept) && null != getDept)) {
                sql += "and c.GDEPT = '" + getDept + "'";
            }
            if ((!"".equals(getUser) && null != getUser)) {
                sql += "and c.GUSER like '%" + getUser + "%'";
            }
//				if((!"".equals(recoverDate) && null != recoverDate)){
//					sql += "and d.RECEIVEDATE like '%"+ recoverDate +"%'";
//				}
            ResultSet rs = con.executeQuery(sql);

            while (rs.next()) {
                CmPrintRecordInfoBean cmPrintRecordInfoBean1 = new CmPrintRecordInfoBean();
                String number = rs.getString("DOCNUMBER");
                String name = rs.getString("DOCNAME");
                String version = rs.getString("VERSION");
                cmPrintRecordInfoBean1.setUnit(rs.getString("OUTDEPT"));
                String applyrecordid = rs.getString("ID");
                cmPrintRecordInfoBean1.setBarTableID(applyrecordid);
                cmPrintRecordInfoBean1.setFileNumber(number);
                cmPrintRecordInfoBean1.setFileName(name);
                cmPrintRecordInfoBean1.setFileType(rs.getString("FILETYPE"));
                cmPrintRecordInfoBean1.setDocVersion(version);
                cmPrintRecordInfoBean1.setPhaseCode(rs.getString("PHASECODE"));
                cmPrintRecordInfoBean1.setSecret(rs.getString("SECRET"));
                cmPrintRecordInfoBean1.setDistributeStatus(rs.getString("FILESTATUS"));
                cmPrintRecordInfoBean1.setGetDate(rs.getString("GDATE"));
                cmPrintRecordInfoBean1.setGetDept(rs.getString("GDEPT"));
                cmPrintRecordInfoBean1.setGetUser(rs.getString("GUSER"));
                cmPrintRecordInfoBean1.setBarCode(rs.getString("BARCODE"));
                cmPrintRecordInfoBean1.setPrintUser(rs.getString("PUSER"));
                cmPrintRecordInfoBean1.setPrintDate(rs.getString("PDATE"));
                String docvr = rs.getString("DOCVR");
                if (null != docvr) {
                    String containerName = "";
                    if ("WL".equals(docvr) || "ZZ".equals(docvr)) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("number", number);
                        map.put("name", name);
                        map.put("version", version);
                        containerName = PrintDataQueryUtil.getCategoryByOutFile(map);
                    } else {
                        Persistable persistable = (Persistable) Util.getObjectByOid(Persistable.class, docvr);
                        if (persistable != null) {
                            if (persistable instanceof WTDocument) {
                                WTDocument document = (WTDocument) persistable;
                                containerName = document.getContainerName();
                            } else if (persistable instanceof WTChangeOrder2) {
                                WTChangeOrder2 changeOrder = (WTChangeOrder2) persistable;
                                containerName = changeOrder.getContainerName();
                            }
                        }
                        if ((!"".equals(type) && null != type)) {
                            if (!containerName.contains(type)) {
                                continue;
                            }
                        }
                    }
                    cmPrintRecordInfoBean1.setContainerName(containerName);
                    cmPrintRecordInfoBean1.setType(containerName);
                }
//					if((!"".equals(recoverDate) && null != recoverDate)){
//						cmPrintRecordInfoBean1.setRecoverDate(rs.getString("RECEIVEDATE"));
//					}else{
                String sql1 = "select RECEIVEDATE from GWPRINTRECOVERRECORD where APPLYRECORDID = '" + applyrecordid + "'";
                conn = new DBConnUtil();
                ResultSet rs1 = conn.executeQuery(sql1);
                while (rs1.next()) {
                    cmPrintRecordInfoBean1.setRecoverDate(rs1.getString("RECEIVEDATE"));
                }
//					}
                //增加查询遗失申请单和延迟回收申请单
                if ("已遗失".equals(rs.getString("FILESTATUS"))) {

                } else if ("延迟回收".equals(rs.getString("FILESTATUS"))) {

                }
                list.add(cmPrintRecordInfoBean1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    //厂内文件自行发起回收查询
    public static List<CmPrintRecordInfoBean> queryFileInfo(String fileNumber, String fileName, String category) throws WTException {//add by lkc 2017.12.20
        List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
        DBConnUtil con = null;
        WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
        String name = user.getName();
        try {
            con = new DBConnUtil();
            String sql = "SELECT a.*, b.*, c.GWKEYID as ID,c.BATCH as BATCH2, c.GDEPT, c.BARCODE FROM GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c " +
                    "WHERE a.GWKEYID = b.APPLYRECORDID and b.gwkeyid = c.applyrecordid and （c.FILESTATUS = '已分发' OR c.FILESTATUS = '已下发'）";
            if ((!"".equals(fileNumber) && null != fileNumber)) {
                sql += " AND a.DOCNUMBER LIKE '%" + fileNumber + "%'";
            }
            if ((!"".equals(fileName) && null != fileName)) {
                sql += " AND a.DOCNAME LIKE '%" + fileName + "%'";
            }
            if ("WL".equals(category) || "ZZ".equals(category)) {
                sql += " AND a.DOCVR = '" + category + "'";
            } else {
//				name = "huhuili";
                sql += " and a.APPLIER = '" + name + "' and a.OUTDEPT = '厂内电子'";
            }
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                String number2 = rs.getString("DOCNUMBER");
                String name2 = rs.getString("DOCNAME");
                String version2 = rs.getString("VERSION");
                CmPrintRecordInfoBean cmPrintRecordInfoBean1 = new CmPrintRecordInfoBean();
                cmPrintRecordInfoBean1.setBarTableID(rs.getString("ID"));
                cmPrintRecordInfoBean1.setFileNumber(number2);
                cmPrintRecordInfoBean1.setFileName(name2);
                cmPrintRecordInfoBean1.setDocVersion(version2);
                cmPrintRecordInfoBean1.setPhaseCode(rs.getString("PHASECODE"));
                cmPrintRecordInfoBean1.setSecret(rs.getString("SECRET"));
                cmPrintRecordInfoBean1.setGetDept(rs.getString("GDEPT"));
                cmPrintRecordInfoBean1.setBatch(rs.getString("BATCH2"));
                cmPrintRecordInfoBean1.setBarCode(rs.getString("BARCODE"));
                String docvr = rs.getString("DOCVR");
                if (null != docvr) {
                    String containerName = "";
                    String fileState = "已下发";
                    String lifeCycle = "";
                    if ("WL".equals(docvr) || "ZZ".equals(docvr)) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("number", number2);
                        map.put("name", name2);
                        map.put("version", version2);
                        containerName = PrintDataQueryUtil.getCategoryByOutFile(map);
                    } else {
                        Persistable persistable = PersistableUtil.getPersistable(docvr);
                        if (persistable != null) {
                            if (persistable instanceof WTDocument) {
                                WTDocument document = (WTDocument) persistable;
                                containerName = document.getContainerName();
                                lifeCycle = document.getState().getState().getDisplay(Locale.CHINA);
                            } else if (persistable instanceof WTChangeOrder2) {
                                WTChangeOrder2 changeOrder = (WTChangeOrder2) persistable;
                                containerName = changeOrder.getContainerName();
                                lifeCycle = changeOrder.getState().getState().getDisplay(Locale.CHINA);
                            }
                        }
                    }
                    cmPrintRecordInfoBean1.setLifeCycleState(lifeCycle);
                    cmPrintRecordInfoBean1.setContainerName(containerName);
                    cmPrintRecordInfoBean1.setDistributeState(fileState);
                }

                list.add(cmPrintRecordInfoBean1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    //更改回收查询
    public static List<CmPrintRecordInfoBean> queryFileInfoByChange(List<String> strlist) {//add by lkc 2017.12.20
        List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < strlist.size(); i++) {
                String id = strlist.get(i).toString();
                String sql = "SELECT a.*, b.*, c.GWKEYID as ID, c.GDEPT, c.BARCODE, c.FILESTATUS FROM " +
                        "GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c WHERE " +
                        "a.GWKEYID = b.APPLYRECORDID and b.gwkeyid = c.applyrecordid and a.OUTDEPT = '厂内电子' and c.GWKEYID = '" + id + "'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    CmPrintRecordInfoBean cmPrintRecordInfoBean1 = new CmPrintRecordInfoBean();
                    cmPrintRecordInfoBean1.setBarTableID(rs.getString("ID"));
                    cmPrintRecordInfoBean1.setFileNumber(rs.getString("DOCNUMBER"));
                    cmPrintRecordInfoBean1.setFileName(rs.getString("DOCNAME"));
                    cmPrintRecordInfoBean1.setDocVersion(rs.getString("VERSION"));
                    cmPrintRecordInfoBean1.setPhaseCode(rs.getString("PHASECODE"));
                    cmPrintRecordInfoBean1.setSecret(rs.getString("SECRET"));
                    cmPrintRecordInfoBean1.setGetDept(rs.getString("GDEPT"));
                    cmPrintRecordInfoBean1.setBatch(rs.getString("BATCH"));
                    cmPrintRecordInfoBean1.setBarCode(rs.getString("BARCODE"));
                    cmPrintRecordInfoBean1.setFileState(rs.getString("FILESTATUS"));
                    list.add(cmPrintRecordInfoBean1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static Boolean insertChangeRecoverToDB(List<CmPrintRecordInfoBean> listBean) {//add by lkc 2017.12.20
        Boolean flag = false;
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < listBean.size(); i++) {
                String barTableID = listBean.get(i).getBarTableID();
                String uuid = listBean.get(i).getUuid();
                String barCode = listBean.get(i).getBarCode();
                String user = listBean.get(i).getCurrentUser();
                String dept = listBean.get(i).getCurrentDept();
                String date = listBean.get(i).getCurrentDate();
                String pboOid = listBean.get(i).getPboOid();
                String sql = "insert into GWPRINTRECOVERRECORD(GWKEYID, APPLYRECORDID, PRINTBARCODEID, RECOVERUSER, RECOVERDEPT, RECOVERDATE, PBOOID) " +
                        "values('" + uuid + "','" + barTableID + "','" + barCode + "','" + user + "','" + dept + "', '" + date + "', '" + pboOid + "')";
                String sql1 = "update GWPRINTBARCODE set FILESTATUS = '回收中' where GWKEYID = '" + barTableID + "'";
                con.executeUpdate(sql);
                con.executeUpdate(sql1);
                flag = true;
            }
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }


    //外来文件自行发起回收查询
    public static List<CmPrintRecordInfoBean> queryOutsideFileInfo(String fileNumber, String fileName) {//add by lkc 2017.12.20
        List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            String sql = "SELECT a.*, b.*, c.GWKEYID as ID, c.GDEPT, c.BARCODE FROM GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c WHERE a.GWKEYID = b.APPLYRECORDID and b.gwkeyid = c.applyrecordid and a.OUTDEPT not like '%厂内%' and c.FILESTATUS = '已下发' ";
            if ((!"".equals(fileNumber) && null != fileNumber)) {
                sql += "AND a.DOCNUMBER LIKE '%" + fileNumber + "%'";
            }
            if ((!"".equals(fileName) && null != fileName)) {
                sql += "AND a.DOCNAME LIKE '%" + fileName + "%'";
            }
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                CmPrintRecordInfoBean cmPrintRecordInfoBean1 = new CmPrintRecordInfoBean();
                cmPrintRecordInfoBean1.setBarTableID(rs.getString("ID"));
                cmPrintRecordInfoBean1.setFileNumber(rs.getString("DOCNUMBER"));
                cmPrintRecordInfoBean1.setFileName(rs.getString("DOCNAME"));
                cmPrintRecordInfoBean1.setDocVersion(rs.getString("VERSION"));
                cmPrintRecordInfoBean1.setPhaseCode(rs.getString("PHASECODE"));
                cmPrintRecordInfoBean1.setSecret(rs.getString("SECRET"));
                cmPrintRecordInfoBean1.setGetDept(rs.getString("GDEPT"));
                cmPrintRecordInfoBean1.setBatch(rs.getString("BATCH"));
                cmPrintRecordInfoBean1.setBarCode(rs.getString("BARCODE"));
                list.add(cmPrintRecordInfoBean1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    //外来文件自行发起回收查询
    public static List<CmPrintRecordInfoBean> queryPaperFileInfo(String fileNumber, String fileName) {//add by lkc 2017.12.20
        List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            con.start();
            String sql = "SELECT a.*, b.*, c.GWKEYID as ID, c.GDEPT, c.BARCODE FROM GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c WHERE a.GWKEYID = b.APPLYRECORDID and b.gwkeyid = c.applyrecordid and a.OUTDEPT = '厂内纸质' and c.FILESTATUS = '已下发' ";
            if ((!"".equals(fileNumber) && null != fileNumber)) {
                sql += "AND a.DOCNUMBER LIKE '%" + fileNumber + "%'";
            }
            if ((!"".equals(fileName) && null != fileName)) {
                sql += "AND a.DOCNAME LIKE '%" + fileName + "%'";
            }
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                CmPrintRecordInfoBean cmPrintRecordInfoBean1 = new CmPrintRecordInfoBean();
                cmPrintRecordInfoBean1.setBarTableID(rs.getString("ID"));
                cmPrintRecordInfoBean1.setFileNumber(rs.getString("DOCNUMBER"));
                cmPrintRecordInfoBean1.setFileName(rs.getString("DOCNAME"));
                cmPrintRecordInfoBean1.setDocVersion(rs.getString("VERSION"));
                cmPrintRecordInfoBean1.setPhaseCode(rs.getString("PHASECODE"));
                cmPrintRecordInfoBean1.setSecret(rs.getString("SECRET"));
                cmPrintRecordInfoBean1.setGetDept(rs.getString("GDEPT"));
                cmPrintRecordInfoBean1.setBatch(rs.getString("BATCH"));
                cmPrintRecordInfoBean1.setBarCode(rs.getString("BARCODE"));
                list.add(cmPrintRecordInfoBean1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    //自行发起回收中点击确定将数据传入后台     add by lkc 2017.12.25
    public static String saveToDBOfRecover(List<CmPrintRecordInfoBean> listBean) {
        DBConnUtil con = null;
        StringBuffer sb = new StringBuffer();
        try {
            con = new DBConnUtil();
            for (int i = 0; i < listBean.size(); i++) {
                String uuid = listBean.get(i).getUuid();
                String fileNumber = listBean.get(i).getFileNumber();
                String sql = "select * from GWPRINTRECOVERRECORD where GWKEYID = '" + uuid + "'";
                ResultSet rs = con.executeQuery(sql);
                if (rs.next()) {
                    if (sb.indexOf(fileNumber) != -1) {
                        sb.append("");
                    } else {
                        if (sb.toString().isEmpty()) {
                            sb.append(fileNumber);
                        } else {
                            sb.append("," + fileNumber);
                        }
                    }
                }
            }
            if ("".equals(sb.toString()) || null == sb.toString()) {
                for (int i = 0; i < listBean.size(); i++) {
                    String id = listBean.get(i).getBarTableID();
                    String uuid = listBean.get(i).getUuid();
                    String userName = listBean.get(i).getCurrentUser();
                    String userDept = listBean.get(i).getCurrentDept();
                    String time = listBean.get(i).getCurrentDate();
                    String sql1 = "select * from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.GWKEYID = '" + id + "'";
                    ResultSet rs1 = con.executeQuery(sql1);
                    while (rs1.next()) {
                        String gwkeyid = uuid;
                        String printBarCodeID = rs1.getString("BARCODE");
                        String sql2 = "insert into GWPRINTRECOVERRECORD(GWKEYID, APPLYRECORDID, PRINTBARCODEID, RECOVERUSER, RECOVERDEPT, RECOVERDATE) values('" + gwkeyid + "','" + id + "','" + printBarCodeID + "','" + userName + "','" + userDept + "', '" + time + "')";
                        String sql3 = "update GWPRINTBARCODE set FILESTATUS = '回收中' where GWKEYID = '" + id + "'";
                        con.executeUpdate(sql2);
                        con.executeUpdate(sql3);
                        con.commit();
                    }
                }
            } else {
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static void updateCurrentUserAndDept(List<String> list, String user, String dept) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String applyrecordid = list.get(i).toString();
                String sql = "update GWPRINTRECOVERRECORD set RECEIVEUSER = '" + user + "', RECEIVEDEPT = '" + dept + "' where APPLYRECORDID = '" + applyrecordid + "'";
                con.executeUpdate(sql);
                con.commit();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> getUUidByRecoverTable() {
        DBConnUtil con = null;
        List<String> list = new ArrayList<String>();
        try {
            con = new DBConnUtil();
            String sql = "select GWKEYID from GWPRINTRECOVERRECORD";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                String keyid = rs.getString("GWKEYID");
                list.add(keyid);
            }
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static List<String> queryBarTableID(List<String> list) {
        DBConnUtil con = null;
        List<String> idList = new ArrayList<String>();
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String uid = list.get(i);
                String sql = "select APPLYRECORDID from GWPRINTRECOVERRECORD where GWKEYID = '" + uid + "'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    String applyrecordid = rs.getString("APPLYRECORDID");
                    idList.add(applyrecordid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return idList;
    }

    public static List<String> queryBarTableIDByOver(List<String> list) {
        DBConnUtil con = null;
        List<String> idList = new ArrayList<String>();
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String uid = list.get(i);
                String sql = "select APPLYRECORDID from GWPRINTRECOVERRECORD where GWKEYID = '" + uid + "'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    String applyrecordid = rs.getString("APPLYRECORDID");
                    idList.add(applyrecordid);
                }
            }
            for (int i = 0; i < list.size(); i++) {
                String uid = list.get(i);
                String sql1 = "select APPLYRECORDID from GWPRINTSTORERECORD where GWKEYID = '" + uid + "'";
                ResultSet rs1 = con.executeQuery(sql1);
                while (rs1.next()) {
                    String applyrecordid = rs1.getString("APPLYRECORDID");
                    idList.add(applyrecordid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return idList;
    }

    public static List<String> queryBarTableIDByDelay(List<String> list, Boolean isFromRecover) {
        DBConnUtil con = null;
        List<String> idList = new ArrayList<String>();
        try {
            con = new DBConnUtil();
            if (isFromRecover) {
                for (int i = 0; i < list.size(); i++) {
                    String uid = list.get(i);
                    String sql = "select APPLYRECORDID from GWPRINTRECOVERRECORD where GWKEYID = '" + uid + "'";
                    ResultSet rs = con.executeQuery(sql);
                    while (rs.next()) {
                        String applyrecordid = rs.getString("APPLYRECORDID");
                        idList.add(applyrecordid);
                    }
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    String uid = list.get(i);
                    String sql = "select APPLYRECORDID from GWPRINTSTORERECORD where GWKEYID = '" + uid + "'";
                    ResultSet rs = con.executeQuery(sql);
                    while (rs.next()) {
                        String applyrecordid = rs.getString("APPLYRECORDID");
                        idList.add(applyrecordid);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return idList;
    }

    public static List<String> queryBarTableIDByChange(String pboOid) {
        DBConnUtil con = null;
        List<String> idList = new ArrayList<String>();
        try {
            con = new DBConnUtil();
            String sql = "select APPLYRECORDID from GWPRINTRECOVERRECORD where PBOOID = '" + pboOid + "'";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                String applyrecordid = rs.getString("APPLYRECORDID");
                idList.add(applyrecordid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return idList;
    }

    public static List<String> queryBarTableIDByDept(List<String> list, String dept) {
        DBConnUtil con = null;
        List<String> idList = new ArrayList<String>();
        List<String> gwkeyidList = new ArrayList<String>();
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String uid = list.get(i);
                String sql = "select APPLYRECORDID from GWPRINTRECOVERRECORD where GWKEYID = '" + uid + "'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    String applyrecordid = rs.getString("APPLYRECORDID");
                    idList.add(applyrecordid);
                }
            }
            if (idList != null && !idList.isEmpty()) {
                gwkeyidList = getBarCodeIDByDept(idList, dept);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return gwkeyidList;
    }

    private static List<String> getBarCodeIDByDept(List<String> idList, String dept) {
        List<String> gwkeyidList = new ArrayList<String>();
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            for (String id : idList) {
                StringBuffer sb = new StringBuffer();
                sb.append("SELECT GWKEYID FROM GWPRINTBARCODE WHERE GWKEYID = '" + id + "' AND GDEPT = '" + dept + "'");
                ResultSet rs = state.executeQuery(sb.toString());
                while (rs.next()) {
                    String gwkeyid = rs.getString("GWKEYID");
                    gwkeyidList.add(gwkeyid);
                }
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
        return gwkeyidList;
    }

    //封存流程中线下回收纸质文件中根据部门获得显示文件在条码表中id
    public static List<String> queryBarTableIDByDeptOfStore(List<String> list, String dept) {
        DBConnUtil con = null;
        List<String> idList = new ArrayList<String>();
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String id = list.get(i).toString();
                String sql = "select GWKEYID from GWPRINTBARCODE where GWKEYID = '" + id + "' and GDEPT = '" + dept + "'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    String applyrecordid = rs.getString("GWKEYID");
                    idList.add(applyrecordid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return idList;
    }

    public static List<String> queryBarTableIDByDeptOfChange(String pboOid, String dept) {
        DBConnUtil con = null;
        List<String> idList = new ArrayList<String>();
        try {
            con = new DBConnUtil();
            String sql = "select GWKEYID from GWPRINTBARCODE where GWKEYID in (select APPLYRECORDID from GWPRINTRECOVERRECORD where PBOOID = '" + pboOid + "') and GDEPT = '" + dept + "'";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                String applyrecordid = rs.getString("GWKEYID");
                idList.add(applyrecordid);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return idList;
    }

    public static List<CmPrintRecordInfoBean> queryRecoverTable(List<String> list) {
        DBConnUtil con = null;
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String applyrecordid = list.get(i).toString();
                String sql1 = "select a.*, b.*, c.GWKEYID as ID, c.GDEPT, c.GUSER, c.GDATE, c.BARCODE, c.FILESTATUS, d.GWKEYID as UUID, d.DELAYREASON, d.DELAYDATE " +
                        "from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c , GWPRINTRECOVERRECORD d where a.gwkeyid = " +
                        "b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid = d.applyrecordid and c.GWKEYID = '" + applyrecordid +
                        "' and d.APPLYRECORDID = '" + applyrecordid + "'";
                ResultSet rs1 = con.executeQuery(sql1);
                while (rs1.next()) {
                    String fileNumber = rs1.getString("DOCNUMBER");
                    String fileName = rs1.getString("DOCNAME");
                    String version = rs1.getString("VERSION");
                    CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
                    cmPrintRecordInfoBean.setBarTableID(rs1.getString("ID"));
                    cmPrintRecordInfoBean.setUuid(rs1.getString("UUID"));
                    cmPrintRecordInfoBean.setFileNumber(fileNumber);
                    cmPrintRecordInfoBean.setFileName(fileName);
                    cmPrintRecordInfoBean.setDocVersion(version);
                    cmPrintRecordInfoBean.setPhaseCode(rs1.getString("PHASECODE"));
                    cmPrintRecordInfoBean.setFileType(rs1.getString("FILETYPE"));
                    cmPrintRecordInfoBean.setSecret(rs1.getString("SECRET"));
                    cmPrintRecordInfoBean.setGetDept(rs1.getString("GDEPT"));
                    cmPrintRecordInfoBean.setApplyUser(rs1.getString("APPLIER"));
                    cmPrintRecordInfoBean.setApplyDate(rs1.getString("APPLYDATE"));
                    cmPrintRecordInfoBean.setGetUser(rs1.getString("GUSER"));
                    cmPrintRecordInfoBean.setGetDate(rs1.getString("GDATE"));
                    cmPrintRecordInfoBean.setBarCode(rs1.getString("BARCODE"));
                    cmPrintRecordInfoBean.setMiddleStatus(rs1.getString("FILESTATUS"));
                    cmPrintRecordInfoBean.setDelayReason(rs1.getString("DELAYREASON"));
                    cmPrintRecordInfoBean.setDelayDate(rs1.getString("DELAYDATE"));
//						cmPrintRecordInfoBean.setLoseReason(rs1.getString("LOSEREASON"));
                    String docvr = rs1.getString("DOCVR");
                    if (null != docvr) {
                        if ("WL".equals(docvr) || "ZZ".equals(docvr)) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("number", fileNumber);
                            map.put("name", fileName);
                            map.put("version", version);
                            String containerName = PrintDataQueryUtil.getCategoryByOutFile(map);
                            cmPrintRecordInfoBean.setContainerName(containerName);
                        } else {
                            Persistable persistable = PersistableUtil.getPersistable(docvr);
                            String containerName = "";
                            String lifeCycle = "";
                            if (persistable != null) {
                                if (persistable instanceof WTDocument) {
                                    WTDocument document = (WTDocument) persistable;
                                    containerName = document.getContainerName();
                                    lifeCycle = document.getState().getState().getDisplay(Locale.CHINA);
                                } else if (persistable instanceof WTChangeOrder2) {
                                    WTChangeOrder2 changeOrder = (WTChangeOrder2) persistable;
                                    containerName = changeOrder.getContainerName();
                                    lifeCycle = changeOrder.getState().getState().getDisplay(Locale.CHINA);
                                }
                            }
                            cmPrintRecordInfoBean.setLifeCycleState(lifeCycle);
                            cmPrintRecordInfoBean.setContainerName(containerName);
                        }
                    }
                    listBean.add(cmPrintRecordInfoBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listBean;
    }

    //封存流程中在纸质文件回收阶段获得待展示文件信息
    public static List<CmPrintRecordInfoBean> queryStoreTable(List<String> list) {
        DBConnUtil con = null;
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String applyrecordid = list.get(i).toString();
                String sql1 = "select a.*, b.*, c.GWKEYID as ID, c.GDEPT, c.GUSER, c.GDATE, c.BARCODE, c.FILESTATUS, d.GWKEYID as UUID, d.DELAYREASON, d.DELAYDATE from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c , GWPRINTSTORERECORD d where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid = d.applyrecordid and c.GWKEYID = '" + applyrecordid + "' and d.APPLYRECORDID = '" + applyrecordid + "'";
                ResultSet rs1 = con.executeQuery(sql1);
                while (rs1.next()) {
                    CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
                    cmPrintRecordInfoBean.setBarTableID(rs1.getString("ID"));
                    cmPrintRecordInfoBean.setUuid(rs1.getString("UUID"));
                    cmPrintRecordInfoBean.setFileNumber(rs1.getString("DOCNUMBER"));
                    cmPrintRecordInfoBean.setFileName(rs1.getString("DOCNAME"));
                    cmPrintRecordInfoBean.setDocVersion(rs1.getString("VERSION"));
                    cmPrintRecordInfoBean.setPhaseCode(rs1.getString("PHASECODE"));
                    cmPrintRecordInfoBean.setFileType(rs1.getString("FILETYPE"));
                    cmPrintRecordInfoBean.setSecret(rs1.getString("SECRET"));
                    cmPrintRecordInfoBean.setGetDept(rs1.getString("GDEPT"));
                    cmPrintRecordInfoBean.setApplyUser(rs1.getString("APPLIER"));
                    cmPrintRecordInfoBean.setApplyDate(rs1.getString("APPLYDATE"));
                    cmPrintRecordInfoBean.setGetUser(rs1.getString("GUSER"));
                    cmPrintRecordInfoBean.setGetDate(rs1.getString("GDATE"));
                    cmPrintRecordInfoBean.setBarCode(rs1.getString("BARCODE"));
                    cmPrintRecordInfoBean.setMiddleStatus(rs1.getString("FILESTATUS"));
                    cmPrintRecordInfoBean.setDelayReason(rs1.getString("DELAYREASON"));
                    cmPrintRecordInfoBean.setDelayDate(rs1.getString("DELAYDATE"));
//						cmPrintRecordInfoBean.setLoseReason(rs1.getString("LOSEREASON"));
                    listBean.add(cmPrintRecordInfoBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listBean;
    }

    //直接提交延迟文件发起回收
    public static List<CmPrintRecordInfoBean> queryImmediateSubmitDelayInfo(List<String> list, Boolean isFromRecover) {
        DBConnUtil con = null;
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        try {
            con = new DBConnUtil();
            if (isFromRecover) {
                for (int i = 0; i < list.size(); i++) {
                    String applyrecordid = list.get(i).toString();
                    String sql1 = "select * from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c , GWPRINTRECOVERRECORD d where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid = d.applyrecordid and c.GWKEYID = '" + applyrecordid + "' and d.APPLYRECORDID = '" + applyrecordid + "' and c.FILESTATUS = '回收中'";
                    ResultSet rs1 = con.executeQuery(sql1);
                    while (rs1.next()) {
                        CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
                        cmPrintRecordInfoBean.setFileType(rs1.getString("FILETYPE"));
                        cmPrintRecordInfoBean.setFileNumber(rs1.getString("DOCNUMBER"));
                        cmPrintRecordInfoBean.setFileName(rs1.getString("DOCNAME"));
                        cmPrintRecordInfoBean.setDocVersion(rs1.getString("VERSION"));
                        cmPrintRecordInfoBean.setPhaseCode(rs1.getString("PHASECODE"));
                        cmPrintRecordInfoBean.setSecret(rs1.getString("SECRET"));
                        cmPrintRecordInfoBean.setGetDept(rs1.getString("GDEPT"));
                        cmPrintRecordInfoBean.setBarCode(rs1.getString("BARCODE"));
                        cmPrintRecordInfoBean.setDelayStatus(rs1.getString("ISYANCHI"));
                        cmPrintRecordInfoBean.setDelayReason(rs1.getString("DELAYREASON"));
                        cmPrintRecordInfoBean.setDelayDate(rs1.getString("DELAYDATE"));
                        listBean.add(cmPrintRecordInfoBean);
                    }
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    String applyrecordid = list.get(i).toString();
                    String sql1 = "select * from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c , GWPRINTSTORERECORD d where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid = d.applyrecordid and c.GWKEYID = '" + applyrecordid + "' and d.APPLYRECORDID = '" + applyrecordid + "' and c.FILESTATUS = '封存中'";
                    ResultSet rs1 = con.executeQuery(sql1);
                    while (rs1.next()) {
                        CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
                        cmPrintRecordInfoBean.setFileType(rs1.getString("FILETYPE"));
                        cmPrintRecordInfoBean.setFileNumber(rs1.getString("DOCNUMBER"));
                        cmPrintRecordInfoBean.setFileName(rs1.getString("DOCNAME"));
                        cmPrintRecordInfoBean.setDocVersion(rs1.getString("VERSION"));
                        cmPrintRecordInfoBean.setPhaseCode(rs1.getString("PHASECODE"));
                        cmPrintRecordInfoBean.setSecret(rs1.getString("SECRET"));
                        cmPrintRecordInfoBean.setGetDept(rs1.getString("GDEPT"));
                        cmPrintRecordInfoBean.setBarCode(rs1.getString("BARCODE"));
                        cmPrintRecordInfoBean.setDelayStatus(rs1.getString("ISYANCHI"));
                        cmPrintRecordInfoBean.setDelayReason(rs1.getString("DELAYREASON"));
                        cmPrintRecordInfoBean.setDelayDate(rs1.getString("DELAYDATE"));
                        listBean.add(cmPrintRecordInfoBean);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listBean;
    }

    public static List<CmPrintRecordInfoBean> queryDelayFileInfo(List<String> list) {
        DBConnUtil con = null;
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String applyrecordid = list.get(i).toString();
                String sql = "select a.*, b.*, c.GWKEYID as ID, c.GDEPT, c.GUSER, c.GDATE, c.BARCODE, c.FILESTATUS, d.GWKEYID as UUID, d.DELAYREASON, d.DELAYDATE from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c , GWPRINTRECOVERRECORD d where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid = d.applyrecordid and c.GWKEYID = '" + applyrecordid + "' and d.APPLYRECORDID = '" + applyrecordid + "' and c.FILESTATUS = '延迟回收'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
                    cmPrintRecordInfoBean.setBarTableID(rs.getString("ID"));
                    cmPrintRecordInfoBean.setUuid(rs.getString("UUID"));
                    cmPrintRecordInfoBean.setFileNumber(rs.getString("DOCNUMBER"));
                    cmPrintRecordInfoBean.setFileName(rs.getString("DOCNAME"));
                    cmPrintRecordInfoBean.setDocVersion(rs.getString("VERSION"));
                    cmPrintRecordInfoBean.setPhaseCode(rs.getString("PHASECODE"));
                    cmPrintRecordInfoBean.setFileType(rs.getString("FILETYPE"));
                    cmPrintRecordInfoBean.setSecret(rs.getString("SECRET"));
                    cmPrintRecordInfoBean.setGetDept(rs.getString("GDEPT"));
                    cmPrintRecordInfoBean.setApplyUser(rs.getString("APPLIER"));
                    cmPrintRecordInfoBean.setApplyDate(rs.getString("APPLYDATE"));
                    cmPrintRecordInfoBean.setGetUser(rs.getString("GUSER"));
                    cmPrintRecordInfoBean.setGetDate(rs.getString("GDATE"));
                    cmPrintRecordInfoBean.setBarCode(rs.getString("BARCODE"));
                    cmPrintRecordInfoBean.setDelayStatus(rs.getString("ISYANCHI"));
                    cmPrintRecordInfoBean.setDelayReason(rs.getString("DELAYREASON"));
                    cmPrintRecordInfoBean.setDelayDate(rs.getString("DELAYDATE"));
//						cmPrintRecordInfoBean.setLoseReason(rs1.getString("LOSEREASON"));
                    listBean.add(cmPrintRecordInfoBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listBean;
    }

    public static List<CmPrintRecordInfoBean> queryDelayFileInfoByOver(List<String> list) {
        DBConnUtil con = null;
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String applyrecordid = list.get(i).toString();
                String sql = "select * from GWPRINTRECOVERRECORD where APPLYRECORDID = '" + applyrecordid + "'";
                ResultSet rs = con.executeQuery(sql);
                if (rs.next()) {
                    String sql1 = "select a.*, b.*, c.GWKEYID as ID, c.GDEPT, c.GUSER, c.GDATE, c.BARCODE, c.FILESTATUS, d.GWKEYID as UUID, d.ISYANCHI, d.DELAYREASON, d.DELAYDATE from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c , GWPRINTRECOVERRECORD d where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid = d.applyrecordid and c.GWKEYID = '" + applyrecordid + "' and d.APPLYRECORDID = '" + applyrecordid + "' and c.FILESTATUS = '延迟回收'";
                    ResultSet rs1 = con.executeQuery(sql1);
                    while (rs1.next()) {
                        CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
                        cmPrintRecordInfoBean.setBarTableID(rs1.getString("ID"));
                        cmPrintRecordInfoBean.setUuid(rs1.getString("UUID"));
                        cmPrintRecordInfoBean.setFileNumber(rs1.getString("DOCNUMBER"));
                        cmPrintRecordInfoBean.setFileName(rs1.getString("DOCNAME"));
                        cmPrintRecordInfoBean.setDocVersion(rs1.getString("VERSION"));
                        cmPrintRecordInfoBean.setPhaseCode(rs1.getString("PHASECODE"));
                        cmPrintRecordInfoBean.setFileType(rs1.getString("FILETYPE"));
                        cmPrintRecordInfoBean.setSecret(rs1.getString("SECRET"));
                        cmPrintRecordInfoBean.setGetDept(rs1.getString("GDEPT"));
                        cmPrintRecordInfoBean.setApplyUser(rs1.getString("APPLIER"));
                        cmPrintRecordInfoBean.setApplyDate(rs1.getString("APPLYDATE"));
                        cmPrintRecordInfoBean.setGetUser(rs1.getString("GUSER"));
                        cmPrintRecordInfoBean.setGetDate(rs1.getString("GDATE"));
                        cmPrintRecordInfoBean.setBarCode(rs1.getString("BARCODE"));
                        cmPrintRecordInfoBean.setDelayStatus(rs1.getString("ISYANCHI"));
                        cmPrintRecordInfoBean.setDelayReason(rs1.getString("DELAYREASON"));
                        cmPrintRecordInfoBean.setDelayDate(rs1.getString("DELAYDATE"));
                        //						cmPrintRecordInfoBean.setLoseReason(rs1.getString("LOSEREASON"));
                        listBean.add(cmPrintRecordInfoBean);
                    }
                } else {
                    String sql1 = "select a.*, b.*, c.GWKEYID as ID, c.GDEPT, c.GUSER, c.GDATE, c.BARCODE, c.FILESTATUS, d.GWKEYID as UUID, d.ISYANCHI, d.DELAYREASON, d.DELAYDATE from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c , GWPRINTSTORERECORD d where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid = d.applyrecordid and c.GWKEYID = '" + applyrecordid + "' and d.APPLYRECORDID = '" + applyrecordid + "' and c.FILESTATUS = '延迟封存'";
                    ResultSet rs1 = con.executeQuery(sql1);
                    while (rs1.next()) {
                        CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
                        cmPrintRecordInfoBean.setBarTableID(rs1.getString("ID"));
                        cmPrintRecordInfoBean.setUuid(rs1.getString("UUID"));
                        cmPrintRecordInfoBean.setFileNumber(rs1.getString("DOCNUMBER"));
                        cmPrintRecordInfoBean.setFileName(rs1.getString("DOCNAME"));
                        cmPrintRecordInfoBean.setDocVersion(rs1.getString("VERSION"));
                        cmPrintRecordInfoBean.setPhaseCode(rs1.getString("PHASECODE"));
                        cmPrintRecordInfoBean.setFileType(rs1.getString("FILETYPE"));
                        cmPrintRecordInfoBean.setSecret(rs1.getString("SECRET"));
                        cmPrintRecordInfoBean.setGetDept(rs1.getString("GDEPT"));
                        cmPrintRecordInfoBean.setApplyUser(rs1.getString("APPLIER"));
                        cmPrintRecordInfoBean.setApplyDate(rs1.getString("APPLYDATE"));
                        cmPrintRecordInfoBean.setGetUser(rs1.getString("GUSER"));
                        cmPrintRecordInfoBean.setGetDate(rs1.getString("GDATE"));
                        cmPrintRecordInfoBean.setBarCode(rs1.getString("BARCODE"));
                        cmPrintRecordInfoBean.setDelayStatus(rs1.getString("ISYANCHI"));
                        cmPrintRecordInfoBean.setDelayReason(rs1.getString("DELAYREASON"));
                        cmPrintRecordInfoBean.setDelayDate(rs1.getString("DELAYDATE"));
//							cmPrintRecordInfoBean.setLoseReason(rs1.getString("LOSEREASON"));
                        listBean.add(cmPrintRecordInfoBean);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listBean;
    }

    public static List<CmPrintRecordInfoBean> queryRecoverTableOfDelay(List<String> list) {
        DBConnUtil con = null;
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String applyrecordid = list.get(i).toString();
                String sql = "select a.*, b.*, c.GWKEYID as ID, c.GDEPT, c.GUSER, c.GDATE, c.BARCODE, c.FILESTATUS, d.GWKEYID as UUID, d.DELAYREASON, d.DELAYDATE from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c , GWPRINTRECOVERRECORD d where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid = d.applyrecordid and c.GWKEYID = '" + applyrecordid + "' and d.APPLYRECORDID = '" + applyrecordid + "' and c.FILESTATUS = '延迟回收中'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
                    cmPrintRecordInfoBean.setBarTableID(rs.getString("ID"));
                    cmPrintRecordInfoBean.setUuid(rs.getString("UUID"));
                    cmPrintRecordInfoBean.setFileNumber(rs.getString("DOCNUMBER"));
                    cmPrintRecordInfoBean.setFileName(rs.getString("DOCNAME"));
                    cmPrintRecordInfoBean.setDocVersion(rs.getString("VERSION"));
                    cmPrintRecordInfoBean.setPhaseCode(rs.getString("PHASECODE"));
                    cmPrintRecordInfoBean.setFileType(rs.getString("FILETYPE"));
                    cmPrintRecordInfoBean.setSecret(rs.getString("SECRET"));
                    cmPrintRecordInfoBean.setGetDept(rs.getString("GDEPT"));
                    cmPrintRecordInfoBean.setApplyUser(rs.getString("APPLIER"));
                    cmPrintRecordInfoBean.setApplyDate(rs.getString("APPLYDATE"));
                    cmPrintRecordInfoBean.setGetUser(rs.getString("GUSER"));
                    cmPrintRecordInfoBean.setGetDate(rs.getString("GDATE"));
                    cmPrintRecordInfoBean.setBarCode(rs.getString("BARCODE"));
                    cmPrintRecordInfoBean.setMiddleStatus(rs.getString("FILESTATUS"));
                    cmPrintRecordInfoBean.setDelayReason(rs.getString("DELAYREASON"));
                    cmPrintRecordInfoBean.setDelayDate(rs.getString("DELAYDATE"));
//						cmPrintRecordInfoBean.setLoseReason(rs1.getString("LOSEREASON"));
                    listBean.add(cmPrintRecordInfoBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listBean;
    }

    public static List<CmPrintRecordInfoBean> queryStoreTableByDelay(List<String> list, Boolean isFromRecover) {
        DBConnUtil con = null;
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        try {
            con = new DBConnUtil();
            if (isFromRecover) {
                for (int i = 0; i < list.size(); i++) {
                    String applyrecordid = list.get(i).toString();
                    String sql = "select a.*, b.*, c.GWKEYID as ID, c.GDEPT, c.GUSER, c.GDATE, c.BARCODE, c.FILESTATUS, d.GWKEYID as UUID, d.DELAYREASON, d.DELAYDATE from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c , GWPRINTRECOVERRECORD d where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid = d.applyrecordid and c.GWKEYID = '" + applyrecordid + "' and d.APPLYRECORDID = '" + applyrecordid + "'";
                    ResultSet rs = con.executeQuery(sql);
                    while (rs.next()) {
                        CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
                        cmPrintRecordInfoBean.setBarTableID(rs.getString("ID"));
                        cmPrintRecordInfoBean.setUuid(rs.getString("UUID"));
                        cmPrintRecordInfoBean.setFileNumber(rs.getString("DOCNUMBER"));
                        cmPrintRecordInfoBean.setFileName(rs.getString("DOCNAME"));
                        cmPrintRecordInfoBean.setDocVersion(rs.getString("VERSION"));
                        cmPrintRecordInfoBean.setPhaseCode(rs.getString("PHASECODE"));
                        cmPrintRecordInfoBean.setFileType(rs.getString("FILETYPE"));
                        cmPrintRecordInfoBean.setSecret(rs.getString("SECRET"));
                        cmPrintRecordInfoBean.setGetDept(rs.getString("GDEPT"));
                        cmPrintRecordInfoBean.setApplyUser(rs.getString("APPLIER"));
                        cmPrintRecordInfoBean.setApplyDate(rs.getString("APPLYDATE"));
                        cmPrintRecordInfoBean.setGetUser(rs.getString("GUSER"));
                        cmPrintRecordInfoBean.setGetDate(rs.getString("GDATE"));
                        cmPrintRecordInfoBean.setBarCode(rs.getString("BARCODE"));
                        cmPrintRecordInfoBean.setMiddleStatus(rs.getString("FILESTATUS"));
                        cmPrintRecordInfoBean.setDelayReason(rs.getString("DELAYREASON"));
                        cmPrintRecordInfoBean.setDelayDate(rs.getString("DELAYDATE"));
//						cmPrintRecordInfoBean.setLoseReason(rs1.getString("LOSEREASON"));
                        listBean.add(cmPrintRecordInfoBean);
                    }
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    String applyrecordid = list.get(i).toString();
                    String sql = "select a.*, b.*, c.GWKEYID as ID, c.GDEPT, c.GUSER, c.GDATE, c.BARCODE, c.FILESTATUS, d.GWKEYID as UUID, d.DELAYREASON, d.DELAYDATE from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c , GWPRINTSTORERECORD d where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid = d.applyrecordid and c.GWKEYID = '" + applyrecordid + "' and d.APPLYRECORDID = '" + applyrecordid + "' and c.FILESTATUS = '延迟封存中'";
                    ResultSet rs = con.executeQuery(sql);
                    while (rs.next()) {
                        CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
                        cmPrintRecordInfoBean.setBarTableID(rs.getString("ID"));
                        cmPrintRecordInfoBean.setUuid(rs.getString("UUID"));
                        cmPrintRecordInfoBean.setFileNumber(rs.getString("DOCNUMBER"));
                        cmPrintRecordInfoBean.setFileName(rs.getString("DOCNAME"));
                        cmPrintRecordInfoBean.setDocVersion(rs.getString("VERSION"));
                        cmPrintRecordInfoBean.setPhaseCode(rs.getString("PHASECODE"));
                        cmPrintRecordInfoBean.setFileType(rs.getString("FILETYPE"));
                        cmPrintRecordInfoBean.setSecret(rs.getString("SECRET"));
                        cmPrintRecordInfoBean.setGetDept(rs.getString("GDEPT"));
                        cmPrintRecordInfoBean.setApplyUser(rs.getString("APPLIER"));
                        cmPrintRecordInfoBean.setApplyDate(rs.getString("APPLYDATE"));
                        cmPrintRecordInfoBean.setGetUser(rs.getString("GUSER"));
                        cmPrintRecordInfoBean.setGetDate(rs.getString("GDATE"));
                        cmPrintRecordInfoBean.setBarCode(rs.getString("BARCODE"));
                        cmPrintRecordInfoBean.setMiddleStatus(rs.getString("FILESTATUS"));
                        cmPrintRecordInfoBean.setDelayReason(rs.getString("DELAYREASON"));
                        cmPrintRecordInfoBean.setDelayDate(rs.getString("DELAYDATE"));
//						cmPrintRecordInfoBean.setLoseReason(rs1.getString("LOSEREASON"));
                        listBean.add(cmPrintRecordInfoBean);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listBean;
    }

    public static void updatePBONumberToDB(List<String> list, String pboNumber) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql = "update GWPRINTRECOVERRECORD set DELAYPBONUMBER = '" + pboNumber + "' where GWKEYID = '" + gwkeyid + "'";
                con.executeUpdate(sql);
            }
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updatePBONumberToDBByStore(List<String> list, String pboNumber) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql = "update GWPRINTSTORERECORD set DELAYPBONUMBER = '" + pboNumber + "' where GWKEYID = '" + gwkeyid + "'";
                con.executeUpdate(sql);
                con.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //将修改的延迟信息暂时保存在回收表中
    public static String saveToRecoverTableDelayInfo(List<CmPrintRecordInfoBean> list) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            StringBuffer sb = new StringBuffer();
            for (CmPrintRecordInfoBean cmPrintRecordInfoBean : list) {
                //String barCode = cmPrintRecordInfoBean.getBarCode();
                String ID = cmPrintRecordInfoBean.getBarTableID();
                String fileNumber = cmPrintRecordInfoBean.getFileNumber();
                String sql = "select * from GWPRINTBARCODE where GWKEYID = '" + ID + "' and FILESTATUS != '回收中'";
                ResultSet rs = con.executeQuery(sql);
                if (rs.next()) {
                    if (sb.indexOf(fileNumber) != -1) {
                        sb.append("");
                    } else {
                        if (sb.toString().isEmpty()) {
                            sb.append(fileNumber);
                        } else {
                            sb.append("," + fileNumber);
                        }
                    }
                }
                String uuid = cmPrintRecordInfoBean.getUuid();
                if (null != uuid && !"".equals(uuid)) {
                    String sql1 = "select * from GWPRINTRECOVERRECORD where GWKEYID = '" + uuid + "' and ISYANCHI = '1'";
                    ResultSet rs1 = con.executeQuery(sql1);
                    if (rs1.next()) {
                        if (sb.indexOf(fileNumber) != -1) {
                            sb.append("");
                        } else {
                            if (sb.toString().isEmpty()) {
                                sb.append(fileNumber);
                            } else {
                                sb.append("," + fileNumber);
                            }
                        }
                    }
                }
                if (null != ID && !"".equals(ID)) {
                    String sql2 = "select * from GWPRINTLOSERECORD where PRINTBARCODEID = '" + ID + "'";
                    ResultSet rs2 = con.executeQuery(sql2);
                    if (rs2.next()) {
                        if (sb.indexOf(fileNumber) != -1) {
                            sb.append("");
                        } else {
                            if (sb.toString().isEmpty()) {
                                sb.append(fileNumber);
                            } else {
                                sb.append("," + fileNumber);
                            }
                        }
                    }
                }
            }
            if ("".equals(sb.toString()) || null == sb.toString()) {
                for (CmPrintRecordInfoBean cmPrintRecordInfoBean : list) {
                    String uuid = cmPrintRecordInfoBean.getUuid();
                    String id = cmPrintRecordInfoBean.getBarTableID();
                    String middleStatus = "延迟".equals(cmPrintRecordInfoBean.getMiddleStatus().toString()) ? "1" : "0";
                    String delayReason = cmPrintRecordInfoBean.getDelayReason();
                    String delayDate = cmPrintRecordInfoBean.getDelayDate();
                    String sql = "update GWPRINTRECOVERRECORD set ISYANCHI ='" + middleStatus + "', DELAYREASON = '" + delayReason + "', DELAYDATE = '" + delayDate + "' where GWKEYID ='" + uuid + "'";
                    //String sql1 = "update GWPRINTBARCODE set FILESTATUS = '延迟回收中' where GWKEYID = '"+ id +"'";
                    con.executeUpdate(sql);
                    //con.executeUpdate(sql1);
                    con.commit();
                }
            } else {
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    //将修改的延迟信息暂时保存在回收表中
    public static String saveToRecoverTableDelayDate(List<CmPrintRecordInfoBean> list) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (CmPrintRecordInfoBean cmPrintRecordInfoBean : list) {
                String uuid = cmPrintRecordInfoBean.getUuid();
                String delayReason = cmPrintRecordInfoBean.getDelayReason();
                String delayDate = cmPrintRecordInfoBean.getDelayDate();
                String sql = "update GWPRINTRECOVERRECORD set DELAYREASON = '" + delayReason + "', DELAYDATE = '" + delayDate + "' where GWKEYID ='" + uuid + "'";
                con.executeUpdate(sql);
                con.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    //封存流程中延迟请求新增数据保存到数据库
    public static String saveToStoreTableDelayInfo(List<CmPrintRecordInfoBean> list) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            StringBuffer sb = new StringBuffer();
            for (CmPrintRecordInfoBean cmPrintRecordInfoBean : list) {
                //String barCode = cmPrintRecordInfoBean.getBarCode();
                String ID = cmPrintRecordInfoBean.getBarTableID();
                String fileNumber = cmPrintRecordInfoBean.getFileNumber();
                String sql = "select * from GWPRINTBARCODE where GWKEYID = '" + ID + "' and FILESTATUS != '封存中'";
                ResultSet rs = con.executeQuery(sql);
                if (rs.next()) {
                    if (sb.indexOf(fileNumber) != -1) {
                        sb.append("");
                    } else {
                        if (sb.toString().isEmpty()) {
                            sb.append(fileNumber);
                        } else {
                            sb.append("," + fileNumber);
                        }
                    }
                }
            }
            if ("".equals(sb.toString()) || null == sb.toString()) {
                for (CmPrintRecordInfoBean cmPrintRecordInfoBean : list) {
                    String uuid = cmPrintRecordInfoBean.getUuid();
                    String id = cmPrintRecordInfoBean.getBarTableID();
                    String middleStatus = "延迟".equals(cmPrintRecordInfoBean.getMiddleStatus().toString()) ? "1" : "0";
                    String delayReason = cmPrintRecordInfoBean.getDelayReason();
                    String delayDate = cmPrintRecordInfoBean.getDelayDate();
                    String sql = "update GWPRINTSTORERECORD set ISYANCHI ='" + middleStatus + "', DELAYREASON = '" + delayReason + "', DELAYDATE = '" + delayDate + "' where GWKEYID ='" + uuid + "'";
                    String sql1 = "update GWPRINTBARCODE set FILESTATUS = '延迟封存中' where GWKEYID = '" + id + "'";
                    con.executeUpdate(sql);
                    con.executeUpdate(sql1);
                    con.commit();
                }
            } else {
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static Boolean saveToRecoverTableDelayInfor(List<CmPrintRecordInfoBean> list) {
        Boolean flag = false;
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (CmPrintRecordInfoBean cmPrintRecordInfoBean : list) {
                String uuid = cmPrintRecordInfoBean.getUuid();
                String delayReason = cmPrintRecordInfoBean.getDelayReason();
                String delayDate = cmPrintRecordInfoBean.getDelayDate();
                String sql = "update GWPRINTRECOVERRECORD set DELAYREASON = '" + delayReason + "', DELAYDATE = '" + delayDate + "' where GWKEYID ='" + uuid + "'";
                con.executeUpdate(sql);
                con.commit();
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static Boolean saveToStoreTableDelayInfor(List<CmPrintRecordInfoBean> list, Boolean isFromRecover) {
        Boolean flag = false;
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            if (isFromRecover) {
                for (CmPrintRecordInfoBean cmPrintRecordInfoBean : list) {
                    String uuid = cmPrintRecordInfoBean.getUuid();
                    String delayReason = cmPrintRecordInfoBean.getDelayReason();
                    String delayDate = cmPrintRecordInfoBean.getDelayDate();
                    String sql = "update GWPRINTRECOVERRECORD set DELAYREASON = '" + delayReason + "', DELAYDATE = '" + delayDate + "' where GWKEYID ='" + uuid + "'";
                    con.executeUpdate(sql);
                    con.commit();
                }
            } else {
                for (CmPrintRecordInfoBean cmPrintRecordInfoBean : list) {
                    String uuid = cmPrintRecordInfoBean.getUuid();
                    String delayReason = cmPrintRecordInfoBean.getDelayReason();
                    String delayDate = cmPrintRecordInfoBean.getDelayDate();
                    String sql = "update GWPRINTSTORERECORD set DELAYREASON = '" + delayReason + "', DELAYDATE = '" + delayDate + "' where GWKEYID ='" + uuid + "'";
                    con.executeUpdate(sql);
                    con.commit();
                }
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static List<String> queryBarCodeIsDelay(List<String> list) {
        DBConnUtil con = null;
        List<String> barCodeList = new ArrayList<String>();
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String uid = list.get(i);
                String sql = "select PRINTBARCODEID from GWPRINTRECOVERRECORD where GWKEYID = '" + uid + "'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    String printBarCode = rs.getString("PRINTBARCODEID");
                    barCodeList.add(printBarCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return barCodeList;
    }

    public static List<CmPrintRecordInfoBean> queryRecoverTableIsDelay(List<String> idList) {
        DBConnUtil con = null;
        List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
        try {
            con = new DBConnUtil();
            for (int i = 0; i < idList.size(); i++) {
                String gwkeyid = idList.get(i);
                String sql1 = "select a.*, b.*, c.GWKEYID as ID, c.GDEPT, c.BARCODE, c.FILESTATUS from " +
                        "GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c " +
                        "where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid = '" + gwkeyid + "'";
                ResultSet rs1 = con.executeQuery(sql1);
                while (rs1.next()) {
                    String fileNumber = rs1.getString("DOCNUMBER");
                    String fileName = rs1.getString("DOCNAME");
                    String version = rs1.getString("VERSION");
                    CmPrintRecordInfoBean cmPrintRecordQueryBean = new CmPrintRecordInfoBean();
                    cmPrintRecordQueryBean.setBarTableID(rs1.getString("ID"));
                    cmPrintRecordQueryBean.setFileNumber(fileNumber);
                    cmPrintRecordQueryBean.setFileName(fileName);
                    cmPrintRecordQueryBean.setMiddleStatus(rs1.getString("FILESTATUS"));
                    cmPrintRecordQueryBean.setDocVersion(version);
                    cmPrintRecordQueryBean.setPhaseCode(rs1.getString("PHASECODE"));
                    cmPrintRecordQueryBean.setSecret(rs1.getString("SECRET"));
                    cmPrintRecordQueryBean.setGetDept(rs1.getString("GDEPT"));
                    cmPrintRecordQueryBean.setBarCode(rs1.getString("BARCODE"));
                    String docvr = rs1.getString("DOCVR");
                    if (null != docvr) {
                        String containerName = "";
                        String lifeCycle = "";
                        if ("WL".equals(docvr) || "ZZ".equals(docvr)) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("number", fileNumber);
                            map.put("name", fileName);
                            map.put("version", version);
                            containerName = PrintDataQueryUtil.getCategoryByOutFile(map);
                        } else {
                            Persistable persistable = null;
                            try{
                                persistable = PersistableUtil.getPersistable(docvr);
                            }catch (WTRuntimeException e){
                                e.printStackTrace();
                            }
                            if (persistable != null) {
                                if (persistable instanceof WTDocument) {
                                    WTDocument document = (WTDocument) persistable;
                                    containerName = document.getContainerName();
                                    lifeCycle = document.getState().getState().getDisplay(Locale.CHINA);
                                } else if (persistable instanceof WTChangeOrder2) {
                                    WTChangeOrder2 changeOrder = (WTChangeOrder2) persistable;
                                    containerName = changeOrder.getContainerName();
                                    lifeCycle = changeOrder.getState().getState().getDisplay(Locale.CHINA);
                                }
                            }
                        }
                        cmPrintRecordQueryBean.setLifeCycleState(lifeCycle);
                        cmPrintRecordQueryBean.setContainerName(containerName);
                    }
                    cmPrintRecordQueryBean = queryRecycleState(cmPrintRecordQueryBean, gwkeyid);
                    list.add(cmPrintRecordQueryBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static CmPrintRecordInfoBean queryRecycleState(CmPrintRecordInfoBean cmPrintRecordQueryBean, String gwkeyid) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT RECEIVEUSER,RECEIVEDEPT,RECEIVEDATE,DELAYDATE FROM GWPRINTRECOVERRECORD WHERE APPLYRECORDID = '" + gwkeyid + "'");
            ResultSet rs = con.executeQuery(sb.toString());
            while (rs.next()) {
                cmPrintRecordQueryBean.setRecycler(rs.getString("RECEIVEUSER"));
                cmPrintRecordQueryBean.setRecycleTime(rs.getString("RECEIVEDATE"));
                cmPrintRecordQueryBean.setRecyclerDept(rs.getString("RECEIVEDEPT"));
                cmPrintRecordQueryBean.setDelayDate(rs.getString("DELAYDATE"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cmPrintRecordQueryBean;
    }

    public static List<CmPrintRecordInfoBean> queryStoreTableIsDelay(List<String> listBean) {
        DBConnUtil con = null;
        List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
        try {
            con = new DBConnUtil();
            for (int i = 0; i < listBean.size(); i++) {
                String applyrecordid = listBean.get(i).toString();
                String sql1 = "select a.*, b.*, c.GWKEYID as ID, c.GDEPT, c.BARCODE, c.FILESTATUS from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid = '" + applyrecordid + "'";
                ResultSet rs1 = con.executeQuery(sql1);
                while (rs1.next()) {
                    CmPrintRecordInfoBean cmPrintRecordQueryBean = new CmPrintRecordInfoBean();
                    cmPrintRecordQueryBean.setBarTableID(rs1.getString("ID"));
                    cmPrintRecordQueryBean.setFileNumber(rs1.getString("DOCNUMBER"));
                    cmPrintRecordQueryBean.setFileName(rs1.getString("DOCNAME"));
                    cmPrintRecordQueryBean.setMiddleStatus(rs1.getString("FILESTATUS"));
                    cmPrintRecordQueryBean.setDocVersion(rs1.getString("VERSION"));
                    cmPrintRecordQueryBean.setPhaseCode(rs1.getString("PHASECODE"));
                    cmPrintRecordQueryBean.setSecret(rs1.getString("SECRET"));
                    cmPrintRecordQueryBean.setGetDept(rs1.getString("GDEPT"));
                    cmPrintRecordQueryBean.setBarCode(rs1.getString("BARCODE"));
                    String sql2 = "select * from GWPRINTSTORERECORD where APPLYRECORDID = '" + applyrecordid + "'";
                    String storeUser = "";
                    String storeDept = "";
                    String storeDate = "";
                    ResultSet rs2 = con.executeQuery(sql2);
                    while (rs2.next()) {
                        storeUser = rs2.getString("STOREUSER");
                        storeDept = rs2.getString("STOREDEPT");
                        storeDate = rs2.getString("STOREDATE");
                    }
                    cmPrintRecordQueryBean.setStoreUser(storeUser);
                    cmPrintRecordQueryBean.setStoreDept(storeDept);
                    cmPrintRecordQueryBean.setStoreDate(storeDate);

                    list.add(cmPrintRecordQueryBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static void updateRecoverStatus(List<String> list) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql = "update GWPRINTBARCODE set FILESTATUS ='已回收' where GWKEYID = (select APPLYRECORDID from GWPRINTRECOVERRECORD where GWKEYID = '" + gwkeyid + "') and FILESTATUS = '回收中'";
                con.executeUpdate(sql);
                con.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateStoreStatusBySelf(List<String> list) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql = "update GWPRINTBARCODE set FILESTATUS ='已封存' where GWKEYID = '" + gwkeyid + "' and FILESTATUS = '已入库'";
                con.executeUpdate(sql);
                con.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> queryRecoverTableID(String pboOid) {
        DBConnUtil con = null;
        List<String> list = new ArrayList<String>();
        try {
            con = new DBConnUtil();
            String sql = "select GWKEYID from GWPRINTRECOVERRECORD where PBOOID = '" + pboOid + "'";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                String gwkeyid = rs.getString("GWKEYID");
                list.add(gwkeyid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static void updateRecoverStatusOfChange(String pboOid) {
        List<String> list = new ArrayList<String>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            String sql = "select GWKEYID from GWPRINTRECOVERRECORD where PBOOID = '" + pboOid + "'";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                String gwkeyid = rs.getString("GWKEYID");
                list.add(gwkeyid);
            }
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql1 = "update GWPRINTBARCODE set FILESTATUS ='已回收' where GWKEYID = (select APPLYRECORDID from GWPRINTRECOVERRECORD where GWKEYID = '" + gwkeyid + "') and FILESTATUS = '回收中'";
                con.executeUpdate(sql1);
                con.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateChangeRecoverData(String pboOid) {
        List<String> list = new ArrayList<String>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            String sql = "select GWKEYID from GWPRINTRECOVERRECORD where PBOOID = '" + pboOid + "'";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                String gwkeyid = rs.getString("GWKEYID");
                list.add(gwkeyid);
            }
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql1 = "delete from GWPRINTRECOVERRECORD where GWKEYID = '" + gwkeyid + "'";
                String sql2 = "update GWPRINTBARCODE set FILESTATUS = '已下发' where GWKEYID = (select APPLYRECORDID from GWPRINTRECOVERRECORD where GWKEYID = '" + gwkeyid + "') and FILESTATUS = '回收中'";
                con.executeUpdate(sql1);
                con.executeUpdate(sql2);
                con.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateRecoverTimeOfDelay(List<String> list, String time, Boolean isFromRecover) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            if (isFromRecover) {
                for (int i = 0; i < list.size(); i++) {
                    String gwkeyid = list.get(i).toString();
                    String sql = "update GWPRINTRECOVERRECORD set RECEIVEDATE = '" + time + "' where APPLYRECORDID = (select GWKEYID from GWPRINTBARCODE where GWKEYID = '" + gwkeyid + "' and FILESTATUS = '回收中')";
                    con.executeUpdate(sql);
                    con.commit();
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    String gwkeyid = list.get(i).toString();
                    String sql = "update GWPRINTSTORERECORD set STOREDATE = '" + time + "' where APPLYRECORDID = (select GWKEYID from GWPRINTBARCODE where GWKEYID = '" + gwkeyid + "' and FILESTATUS = '封存中')";
                    con.executeUpdate(sql);
                    con.commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateRecoverTime(List<String> list, String time) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql = "update GWPRINTRECOVERRECORD set RECEIVEDATE = '" + time + "' where APPLYRECORDID = (select GWKEYID from GWPRINTBARCODE where GWKEYID = '" + gwkeyid + "' and FILESTATUS = '回收中')";
                con.executeUpdate(sql);
                con.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateDelayInfo(List<String> list, Boolean isFromRecover) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            if (isFromRecover) {
                for (int i = 0; i < list.size(); i++) {
                    String gwkeyid = list.get(i).toString();
                    String sql1 = "select * from GWPRINTRECOVERRECORD where GWKEYID = '" + gwkeyid + "'";
                    ResultSet rs = con.executeQuery(sql1);
                    while (rs.next()) {
                        String applyrecordid = rs.getString("APPLYRECORDID");
                        String sql2 = "update GWPRINTRECOVERRECORD set ISYANCHI ='0', DELAYREASON = '', DELAYDATE = '' where GWKEYID = '" + gwkeyid + "'";
                        String sql3 = "update GWPRINTBARCODE set FILESTATUS = '回收中' where GWKEYID = '" + applyrecordid + "'";
                        con.executeUpdate(sql2);
                        con.executeUpdate(sql3);
                        con.commit();
                    }
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    String gwkeyid = list.get(i).toString();
                    String sql1 = "select * from GWPRINTSTORERECORD where GWKEYID = '" + gwkeyid + "'";
                    ResultSet rs = con.executeQuery(sql1);
                    while (rs.next()) {
                        String applyrecordid = rs.getString("APPLYRECORDID");
                        String sql2 = "update GWPRINTSTORERECORD set ISYANCHI ='0', DELAYREASON = '', DELAYDATE = '' where GWKEYID = '" + gwkeyid + "'";
                        String sql3 = "update GWPRINTBARCODE set FILESTATUS = '封存中' where GWKEYID = '" + applyrecordid + "'";
                        con.executeUpdate(sql2);
                        con.executeUpdate(sql3);
                        con.commit();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateDelayEndStatus(List<String> list, Boolean isFromRecover) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            if (isFromRecover) {
                for (int i = 0; i < list.size(); i++) {
                    String gwkeyid = list.get(i).toString();
                    String sql1 = "select * from GWPRINTRECOVERRECORD where GWKEYID = '" + gwkeyid + "'";
                    ResultSet rs = con.executeQuery(sql1);
                    while (rs.next()) {
                        String applyrecordid = rs.getString("APPLYRECORDID");
                        String sql = "update GWPRINTBARCODE set FILESTATUS = '延迟回收' where GWKEYID = '" + applyrecordid + "'";
                        con.executeUpdate(sql);
                        con.commit();
                    }
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    String gwkeyid = list.get(i).toString();
                    String sql1 = "select * from GWPRINTSTORERECORD where GWKEYID = '" + gwkeyid + "'";
                    ResultSet rs = con.executeQuery(sql1);
                    while (rs.next()) {
                        String applyrecordid = rs.getString("APPLYRECORDID");
                        String sql = "update GWPRINTBARCODE set FILESTATUS = '延迟封存' where GWKEYID = '" + applyrecordid + "'";
                        con.executeUpdate(sql);
                        con.commit();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateFileStatus(List<String> list) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql = "update GWPRINTBARCODE set FILESTATUS = '已封存' where GWKEYID = '" + gwkeyid + "' and FILESTATUS = '封存中'";
                con.executeUpdate(sql);
                con.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void updateFileStatusOfOpenStore(List<String> list) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql = "update GWPRINTBARCODE set FILESTATUS = '已入库' where GWKEYID = '" + gwkeyid + "'";
                con.executeUpdate(sql);
                con.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void updateStoreTime(List<String> list) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql = "update GWPRINTBARCODE set FILESTATUS = '已下发' where GWKEYID = '" + gwkeyid + "'";
                con.executeUpdate(sql);
                con.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void updateOpenStore(List<String> list) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql = "update GWPRINTSTORERECORD set ISOPENSTORE = 'false' where APPLYRECORDID = '" + gwkeyid + "'";
                con.executeUpdate(sql);
                con.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void updateDelayStatus(List<String> list, Boolean isFromRecover) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            if (isFromRecover) {
                for (int i = 0; i < list.size(); i++) {
                    String gwkeyid = list.get(i).toString();
                    String sql = "update GWPRINTRECOVERRECORD set COMPLETESTATUS = '完成' where GWKEYID = '" + gwkeyid + "'";
                    con.executeUpdate(sql);
                    con.commit();
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    String gwkeyid = list.get(i).toString();
                    String sql = "update GWPRINTSTORERECORD set COMPLETESTATUS = '完成' where GWKEYID = '" + gwkeyid + "'";
                    con.executeUpdate(sql);
                    con.commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static List<String> getDeptByUuid(List<String> list, Boolean isFromRecover) {
        List<String> deptList = new ArrayList<String>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            if (isFromRecover) {
                for (int i = 0; i < list.size(); i++) {
                    String gwkeyid = list.get(i).toString();
                    String sql = "select GDEPT from GWPRINTBARCODE where GWKEYID = (select APPLYRECORDID from GWPRINTRECOVERRECORD where GWKEYID = '" + gwkeyid + "')";
                    ResultSet rs = con.executeQuery(sql);
                    while (rs.next()) {
                        String dept = rs.getString("GDEPT");
                        if (!deptList.contains(dept)) {
                            deptList.add(dept);
                        }
                    }
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    String gwkeyid = list.get(i).toString();
                    String sql = "select GDEPT from GWPRINTBARCODE where GWKEYID = (select APPLYRECORDID from GWPRINTSTORERECORD where GWKEYID = '" + gwkeyid + "')";
                    ResultSet rs = con.executeQuery(sql);
                    while (rs.next()) {
                        String dept = rs.getString("GDEPT");
                        if (!deptList.contains(dept)) {
                            deptList.add(dept);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return deptList;
    }

    public static List<String> getDeptByUuidOfStore(List<String> list) {
        List<String> deptList = new ArrayList<String>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();

            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql = "select GDEPT from GWPRINTBARCODE where GWKEYID = '" + gwkeyid + "'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    String dept = rs.getString("GDEPT");
                    if (!deptList.contains(dept)) {
                        deptList.add(dept);
                    }
                }
                con.commit();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return deptList;
    }

    public static List<String> getDeptByUuidOfRecover(List<String> list) {
        List<String> deptList = new ArrayList<String>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();

            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql = "select GDEPT from GWPRINTBARCODE where GWKEYID = (select APPLYRECORDID from GWPRINTRECOVERRECORD where GWKEYID = '" + gwkeyid + "')";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    String dept = rs.getString("GDEPT");
                    if (!deptList.contains(dept)) {
                        deptList.add(dept);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return deptList;
    }

    public static List<String> getDeptByID(List<String> list) {
        List<String> deptList = new ArrayList<String>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();

            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql = "select GDEPT from GWPRINTBARCODE where GWKEYID = '" + gwkeyid + "'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    String dept = rs.getString("GDEPT");
                    if (!deptList.contains(dept)) {
                        deptList.add(dept);
                    }
                }
                con.commit();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return deptList;
    }

    public static List<String> getDeptByOid(String oid) {
        List<String> deptList = new ArrayList<String>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            String sql = "select GDEPT from GWPRINTBARCODE where GWKEYID in (select APPLYRECORDID from GWPRINTRECOVERRECORD where PBOOID = '" + oid + "')";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                String dept = rs.getString("GDEPT");
                if (!deptList.contains(dept)) {
                    deptList.add(dept);
                }
            }
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return deptList;
    }

    public static Boolean updateStatusByRecover(List<String> list, String dept) {
        DBConnUtil con = null;
        Boolean flag = false;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql = "select * from GWPRINTRECOVERRECORD where APPLYRECORDID = (select GWKEYID from GWPRINTBARCODE where GWKEYID = (select APPLYRECORDID from GWPRINTRECOVERRECORD where GWKEYID = '" + gwkeyid + "') and (FILESTATUS != '回收中') and (FILESTATUS != '已回收') and GDEPT = '" + dept + "') and COMPLETESTATUS is NULL";
                ResultSet rs = con.executeQuery(sql);
                if (rs.next()) {
                    flag = false;
                    return flag;
                } else {
                    flag = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static Boolean updateStatusByStore(List<String> list, String dept) {
        DBConnUtil con = null;
        Boolean flag = false;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql = "select * from GWPRINTSTORERECORD where APPLYRECORDID = (select GWKEYID from GWPRINTBARCODE where GWKEYID = '" + gwkeyid + "' and (FILESTATUS != '封存中') and (FILESTATUS != '已封存') and GDEPT = '" + dept + "') and COMPLETESTATUS is NULL";
                ResultSet rs = con.executeQuery(sql);
                if (rs.next()) {
                    flag = false;
                    return flag;
                } else {
                    flag = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static Boolean checkSureOfRecover(List<String> list) {
        DBConnUtil con = null;
        Boolean flag = false;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql = "select FILESTATUS from GWPRINTBARCODE where GWKEYID = (select APPLYRECORDID from GWPRINTRECOVERRECORD where GWKEYID = '" + gwkeyid + "')";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    String fileStatus = rs.getString("FILESTATUS");
                    if ("回收中".equals(fileStatus)) {
                        flag = false;
                        return flag;
                    } else {
                        flag = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static Boolean checkSureOfStore(List<String> list) {
        DBConnUtil con = null;
        Boolean flag = false;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql = "select FILESTATUS from GWPRINTBARCODE where GWKEYID = '" + gwkeyid + "'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    String fileStatus = rs.getString("FILESTATUS");
                    if ("封存中".equals(fileStatus)) {
                        flag = false;
                        return flag;
                    } else {
                        flag = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static Boolean updateStatusOfChange(String pboOid, String dept) {
        DBConnUtil con = null;
        Boolean flag = false;
        List<String> list = new ArrayList<String>();
        try {
            con = new DBConnUtil();
            String sql = "select GWKEYID from GWPRINTRECOVERRECORD where PBOOID = '" + pboOid + "'";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                String gwkeyid = rs.getString("GWKEYID");
                list.add(gwkeyid);
            }
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql1 = "select * from GWPRINTRECOVERRECORD where APPLYRECORDID = (select GWKEYID from GWPRINTBARCODE where GWKEYID = (select APPLYRECORDID from GWPRINTRECOVERRECORD where GWKEYID = '" + gwkeyid + "') and (FILESTATUS != '回收中') and GDEPT = '" + dept + "') and COMPLETESTATUS is NULL";
                ResultSet rs1 = con.executeQuery(sql1);
                if (rs1.next()) {
                    flag = false;
                    return flag;
                } else {
                    flag = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static Boolean checkSureOfChangeRecover(String pboOid) {
        DBConnUtil con = null;
        Boolean flag = false;
        List<String> list = new ArrayList<String>();
        try {
            con = new DBConnUtil();
            String sql = "select GWKEYID from GWPRINTRECOVERRECORD where PBOOID = '" + pboOid + "'";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                String gwkeyid = rs.getString("GWKEYID");
                list.add(gwkeyid);
            }
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i).toString();
                String sql1 = "select FILESTATUS from GWPRINTBARCODE where GWKEYID = (select APPLYRECORDID from GWPRINTRECOVERRECORD where GWKEYID = '" + gwkeyid + "')";
                ResultSet rs1 = con.executeQuery(sql1);
                while (rs1.next()) {
                    String fileStatus = rs1.getString("FILESTATUS");
                    if ("已回收".equals(fileStatus) || "已遗失".equals(fileStatus)) {
                        flag = true;
                    } else {
                        flag = false;
                        return flag;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static String saveWfprocessLoseInfoOfRecover(List<CmPrintRecordInfoBean> list) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String uuid = list.get(i).getUuid();
                String barTableID = list.get(i).getBarTableID();
                String loseReason = list.get(i).getLoseReason();
                String sql2 = "update GWPRINTLOSERECORD set LOSEREASON = '" + loseReason + "' where GWKEYID = '" + uuid + "' and PRINTBARCODEID = '" + barTableID + "'";
                con.executeUpdate(sql2);
                con.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String saveLoseInfoOfRecover(List<CmPrintRecordInfoBean> list) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                String ID = list.get(i).getBarTableID();
                //String barCode = list.get(i).getBarCode();
                String fileNumber = list.get(i).getFileNumber();
                String sql = "select * from GWPRINTBARCODE where GWKEYID = '" + ID + "' and FILESTATUS != '回收中'";
                ResultSet rs = con.executeQuery(sql);
                if (rs.next()) {
                    if (sb.indexOf(fileNumber) != -1) {
                        sb.append("");
                    } else {
                        if (sb.toString().isEmpty()) {
                            sb.append(fileNumber);
                        } else {
                            sb.append("," + fileNumber);
                        }
                    }
                }
                String uuid = list.get(i).getUnit();
                if (null != uuid && !"".equals(uuid)) {
                    String sql1 = "select * from GWPRINTRECOVERRECORD where GWKEYID = '" + uuid + "' and ISYANCHI = '1'";
                    ResultSet rs1 = con.executeQuery(sql1);
                    if (rs1.next()) {
                        if (sb.indexOf(fileNumber) != -1) {
                            sb.append("");
                        } else {
                            if (sb.toString().isEmpty()) {
                                sb.append(fileNumber);
                            } else {
                                sb.append("," + fileNumber);
                            }
                        }
                    }
                }
                if (null != ID && !"".equals(ID)) {
                    String sql2 = "select * from GWPRINTLOSERECORD where PRINTBARCODEID = '" + ID + "'";
                    ResultSet rs2 = con.executeQuery(sql2);
                    if (rs2.next()) {
                        if (sb.indexOf(fileNumber) != -1) {
                            sb.append("");
                        } else {
                            if (sb.toString().isEmpty()) {
                                sb.append(fileNumber);
                            } else {
                                sb.append("," + fileNumber);
                            }
                        }
                    }
                }
            }
            if ("".equals(sb.toString()) || null == sb.toString()) {
                for (int i = 0; i < list.size(); i++) {
                    String uuid = list.get(i).getUuid();
                    String barTableID = list.get(i).getBarTableID();
                    String loseUser = list.get(i).getCurrentUser();
                    String loseDate = list.get(i).getCurrentDate();
                    String loseDept = list.get(i).getCurrentDept();
                    String loseReason = list.get(i).getLoseReason();
                    String sql2 = "insert into GWPRINTLOSERECORD(GWKEYID, PRINTBARCODEID, LOSEUSER, LOSEDATE, LOSEDEPT, LOSEREASON) values('" + uuid + "','" + barTableID + "','" + loseUser + "','" + loseDate + "','" + loseDept + "','" + loseReason + "')";
                    //String sql3 = "update GWPRINTBARCODE set FILESTATUS = '遗失中' where GWKEYID = '"+ barTableID +"'";
                    con.executeUpdate(sql2);
                    //con.executeUpdate(sql3);
                    con.commit();
                }
            } else {
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String saveLoseInfoOfStore(List<CmPrintRecordInfoBean> list) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                String ID = list.get(i).getBarTableID();
                //String barCode = list.get(i).getBarCode();
                String fileNumber = list.get(i).getFileNumber();
                String sql = "select * from GWPRINTBARCODE where GWKEYID = '" + ID + "' and FILESTATUS != '封存中'";
                ResultSet rs = con.executeQuery(sql);
                if (rs.next()) {
                    if (sb.indexOf(fileNumber) != -1) {
                        sb.append("");
                    } else {
                        if (sb.toString().isEmpty()) {
                            sb.append(fileNumber);
                        } else {
                            sb.append("," + fileNumber);
                        }
                    }
                }
            }
            if ("".equals(sb.toString()) || null == sb.toString()) {
                for (int i = 0; i < list.size(); i++) {
                    String uuid = list.get(i).getUuid();
                    String barTableID = list.get(i).getBarTableID();
                    String loseUser = list.get(i).getCurrentUser();
                    String loseDate = list.get(i).getCurrentDate();
                    String loseDept = list.get(i).getCurrentDept();
                    String loseReason = list.get(i).getLoseReason();
                    String sql2 = "insert into GWPRINTLOSERECORD(GWKEYID, PRINTBARCODEID, LOSEUSER, LOSEDATE, LOSEDEPT, LOSEREASON) values('" + uuid + "','" + barTableID + "','" + loseUser + "','" + loseDate + "','" + loseDept + "','" + loseReason + "')";
                    String sql3 = "update GWPRINTBARCODE set FILESTATUS = '遗失中' where GWKEYID = '" + barTableID + "'";
                    con.executeUpdate(sql2);
                    con.executeUpdate(sql3);
                    con.commit();
                }
            } else {
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String saveLoseInfo(List<CmPrintRecordInfoBean> list) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                String id = list.get(i).getBarTableID();
                String fileNumber = list.get(i).getFileNumber();
                String sql = "select * from GWPRINTBARCODE where GWKEYID = '" + id + "' and FILESTATUS = '遗失中'";
                ResultSet rs = con.executeQuery(sql);
                if (rs.next()) {
                    if (sb.indexOf(fileNumber) != -1) {
                        sb.append("");
                    } else {
                        if (sb.toString().isEmpty()) {
                            sb.append(fileNumber);
                        } else {
                            sb.append("," + fileNumber);
                        }
                    }
                }
            }
            if ("".equals(sb.toString()) || null == sb.toString()) {
                for (int i = 0; i < list.size(); i++) {
                    String uuid = list.get(i).getUuid();
                    String barTableID = list.get(i).getBarTableID();
                    String loseUser = list.get(i).getCurrentUser();
                    String loseDate = list.get(i).getCurrentDate();
                    String loseDept = list.get(i).getCurrentDept();
                    String loseReason = list.get(i).getLoseReason();
                    String sql2 = "insert into GWPRINTLOSERECORD(GWKEYID, PRINTBARCODEID, LOSEUSER, LOSEDATE, LOSEDEPT, LOSEREASON) values('" + uuid + "','" + barTableID + "','" + loseUser + "','" + loseDate + "','" + loseDept + "','" + loseReason + "')";
                    String sql3 = "update GWPRINTBARCODE set FILESTATUS = '遗失中' where GWKEYID = '" + barTableID + "'";
                    con.executeUpdate(sql2);
                    con.executeUpdate(sql3);
                    con.commit();
                }
            } else {
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static void deleteLoseInfo(List<String> list, Boolean isFromRecover, Boolean isFromStore) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            if (isFromRecover) {
                for (int i = 0; i < list.size(); i++) {
                    String id = list.get(i);
                    String sql = "delete from GWPRINTLOSERECORD where PRINTBARCODEID = '" + id + "'";
                    String sql1 = "update GWPRINTBARCODE set FILESTATUS = '回收中' where GWKEYID = '" + id + "'";
                    con.executeUpdate(sql);
                    con.executeUpdate(sql1);
                    con.commit();
                }
            } else if (isFromStore) {
                for (int i = 0; i < list.size(); i++) {
                    String id = list.get(i);
                    String sql = "delete from GWPRINTLOSERECORD where PRINTBARCODEID = '" + id + "'";
                    String sql1 = "update GWPRINTBARCODE set FILESTATUS = '封存中' where GWKEYID = '" + id + "'";
                    con.executeUpdate(sql);
                    con.executeUpdate(sql1);
                    con.commit();
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    String id = list.get(i);
                    String sql = "delete from GWPRINTLOSERECORD where PRINTBARCODEID = '" + id + "'";
                    String sql1 = "update GWPRINTBARCODE set FILESTATUS = '已下发' where GWKEYID = '" + id + "'";
                    con.executeUpdate(sql);
                    con.executeUpdate(sql1);
                    con.commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateLoseEndStatus(List<String> list, Boolean isFromRecover, Boolean isFromStore) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            if (isFromRecover) {
                for (int i = 0; i < list.size(); i++) {
                    String id = list.get(i);
                    String sql = "update GWPRINTBARCODE set FILESTATUS = '已遗失' where GWKEYID = '" + id + "'";
                    con.executeUpdate(sql);
                    con.commit();
                }
            } else if (isFromStore) {
                for (int i = 0; i < list.size(); i++) {
                    String id = list.get(i);
                    String sql = "update GWPRINTBARCODE set FILESTATUS = '已遗失' where GWKEYID = '" + id + "'";
                    con.executeUpdate(sql);
                    con.commit();
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    String id = list.get(i);
                    String sql = "update GWPRINTBARCODE set FILESTATUS = '已遗失' where GWKEYID = '" + id + "'";
                    con.executeUpdate(sql);
                    con.commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateLoseStatus(List<String> list, Boolean isFromRecover, Boolean isFromStore) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            if (isFromRecover) {
                for (int i = 0; i < list.size(); i++) {
                    String id = list.get(i);
                    String sql = "update GWPRINTRECOVERRECORD set COMPLETESTATUS = '完成' where APPLYRECORDID = (select GWKEYID from GWPRINTBARCODE where GWKEYID = '" + id + "')";
                    con.executeUpdate(sql);
                    con.commit();
                }
            } else if (isFromStore) {
                for (int i = 0; i < list.size(); i++) {
                    String id = list.get(i);
                    String sql = "update GWPRINTSTORERECORD set COMPLETESTATUS = '完成' where APPLYRECORDID = (select GWKEYID from GWPRINTBARCODE where GWKEYID = '" + id + "')";
                    con.executeUpdate(sql);
                    con.commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 封存流程取消后更改封存表中数据ISRESTART
     *
     * @param self
     * @throws RemoteException
     * @throws WTException
     * @author jyx
     * @date 2018-5-17
     */
    public static void deleteStoreInfo(List<String> list) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String id = list.get(i);
                String sql = "update GWPRINTSTORERECORD set ISRESTART = 'true' where APPLYRECORDID = '" + id + "'";
                con.executeUpdate(sql);
                con.commit();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //遗失流程中展示遗失信息
    public static List<CmPrintRecordInfoBean> queryLoseInfo(List<String> list) {
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String id = list.get(i).toString();
                String sql = "select a.*, b.*, c.GWKEYID as ID, c.GDEPT, c.BARCODE, c.FILESTATUS, d.* ,d.GWKEYID as UUID from GWPRINTAPPLYRECORD a, GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c, GWPRINTLOSERECORD d where c.GWKEYID = d.PRINTBARCODEID and a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and d.printbarcodeid = '" + id + "'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
                    cmPrintRecordInfoBean.setBarTableID(rs.getString("ID"));
                    cmPrintRecordInfoBean.setFileType(rs.getString("FILETYPE"));
                    cmPrintRecordInfoBean.setFileNumber(rs.getString("DOCNUMBER"));
                    cmPrintRecordInfoBean.setFileName(rs.getString("DOCNAME"));
                    cmPrintRecordInfoBean.setMiddleStatus(rs.getString("FILESTATUS"));
                    cmPrintRecordInfoBean.setGetDept(rs.getString("GDEPT"));
                    cmPrintRecordInfoBean.setBarCode(rs.getString("BARCODE"));
                    cmPrintRecordInfoBean.setCurrentUser(rs.getString("LOSEUSER"));
                    cmPrintRecordInfoBean.setCurrentDept(rs.getString("LOSEDEPT"));
                    cmPrintRecordInfoBean.setCurrentDate(rs.getString("LOSEDATE"));
                    cmPrintRecordInfoBean.setLoseReason(rs.getString("LOSEREASON"));
                    cmPrintRecordInfoBean.setUuid(rs.getString("UUID"));
                    listBean.add(cmPrintRecordInfoBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listBean;
    }

    public static List<String> saveInfoToDB(List<String> list, String userName, String dept, String date) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String barCodeID = list.get(i);
                String sql1 = "update GWPRINTRECOVERRECORD set RECEIVEUSER = '" + userName + "', RECEIVEDEPT = '" +
                        dept + "', RECEIVEDATE = '" + date + "' where APPLYRECORDID = '" + barCodeID + "'";
                String sql2 = "UPDATE GWPRINTBARCODE SET FILESTATUS = '已回收' WHERE GWKEYID = '" + barCodeID + "'";
                con.executeUpdate(sql1);
                con.executeUpdate(sql2);
            }
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<String> saveInfoToDB2(List<String> list, String userName, String dept, String date) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String barCodeID = list.get(i);
                String sql1 = "update GWPRINTRECOVERRECORD set RECEIVEUSER = '" + userName + "', RECEIVEDEPT = '" +
                        dept + "', RECEIVEDATE = '" + date + "' where APPLYRECORDID = '" + barCodeID + "'";
                String sql2 = "UPDATE GWPRINTBARCODE SET FILESTATUS = '已遗失' WHERE GWKEYID = '" + barCodeID + "'";
                String uuid = UUID.randomUUID().toString();
                String sql3 = "insert into GWPRINTLOSERECORD(GWKEYID, PRINTBARCODEID, LOSEUSER, LOSEDATE, LOSEDEPT, LOSEREASON) values('" + uuid + "','" + barCodeID + "','" + userName + "','" + date + "','" + dept + "','')";
                con.executeUpdate(sql1);
                con.executeUpdate(sql2);
                con.executeUpdate(sql3);
            }
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<String> saveInfoToDBOfStore(List<String> strList, String userName, String dept, String date) {
        DBConnUtil con = null;
        List<String> list = new ArrayList<String>();
        try {
            con = new DBConnUtil();
            for (int i = 0; i < strList.size(); i++) {
                String id = strList.get(i).toString();
                String sql = "select GWKEYID from GWPRINTBARCODE where GWKEYID = '" + id + "' and GDEPT = '" + dept + "'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    String gwkeyid = rs.getString("GWKEYID");
                    list.add(gwkeyid);
                }
            }
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    String applyrecordid = list.get(i).toString();
                    String sql = "update GWPRINTSTORERECORD set STOREUSER = '" + userName + "', STOREDEPT = '" + dept + "', STOREDATE = '" + date + "' where APPLYRECORDID = '" + applyrecordid + "'";
                    con.executeUpdate(sql);
                    con.commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    public static Boolean saveUpdateLoseInfo(List<CmPrintRecordInfoBean> list) {
        Boolean flag = false;
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String id = list.get(i).getBarTableID();
                String loseReason = list.get(i).getLoseReason();
                String sql = "update GWPRINTLOSERECORD set LOSEREASON = '" + loseReason + "' where PRINTBARCODEID = '" + id + "'";
                con.executeUpdate(sql);
                con.commit();
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static List<CmPrintRecordInfoBean> updateBean(List<CmPrintRecordInfoBean> list) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String id = list.get(i).getBarTableID();
                String sql = "select * from GWPRINTBARCODE where FILESTATUS = '遗失中' and GWKEYID = '" + id + "'";
                ResultSet rs = con.executeQuery(sql);
                if (rs.next()) {
                    list.remove(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<CmSealBean> selectAllSeal() {
        ArrayList<CmSealBean> list = new ArrayList<CmSealBean>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            String sql = "SELECT * FROM GWYINZHANG ORDER BY SEALNUMBER ASC";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                CmSealBean cmSealBean = new CmSealBean();
                cmSealBean.setGwKeyId(rs.getString("SEALNUMBER"));
                cmSealBean.setName(rs.getString("SEALNAME"));
                list.add(cmSealBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 重新打印查询
     *
     * @param cmPrintRecordQueryBean
     * @return
     * @author zhuhao
     * @date 2018-5-14
     */
    public static List<CmPrintRecordInfoBean> queryReprintInfo(CmPrintRecordQueryBean cmPrintRecordQueryBean) {
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            String fileNumber = cmPrintRecordQueryBean.getFileNumber();
            String fileName = cmPrintRecordQueryBean.getFileName();
            String fileType = cmPrintRecordQueryBean.getFileType();
            String version = cmPrintRecordQueryBean.getVersion();
            String phaseCode = cmPrintRecordQueryBean.getPhaseCode();
            String printStartDate = cmPrintRecordQueryBean.getPrintStartDate();
            String printEndDate = cmPrintRecordQueryBean.getPrintEndDate();
            WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
            String userName = user.getName();
            StringBuffer sb = new StringBuffer();
            //查询"已下发"或"已打印"并且打印人是自己
            sb.append("SELECT * FROM GWPRINTBARCODE WHERE (FILESTATUS = '已下发' OR FILESTATUS = '已打印') AND PUSER = '" + userName + "'");
            if (((!"".equals(printStartDate) && null != printStartDate)) && ((!"".equals(printEndDate) && null != printEndDate))) {
                sb.append("AND PDATE BETWEEN '" + printStartDate + "' AND '" + printEndDate + "'");
            }
            sb.append(" AND APPLYRECORDID IN ");
            sb.append("(SELECT GWKEYID FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID IN ");
            sb.append("(SELECT GWKEYID FROM GWPRINTAPPLYRECORD WHERE FILETYPE = '" + fileType + "'");
            if (!"".equals(fileNumber) && null != fileNumber) {
                sb.append(" AND DOCNUMBER LIKE '%" + fileNumber + "%'");
            }
            if (!"".equals(fileName) && null != fileName) {
                sb.append(" AND DOCNAME LIKE '%" + fileName + "%'");
            }
            if (!"".equals(version) && null != version) {
                sb.append(" AND VERSION LIKE '%" + version + "%'");
            }
            if (!"".equals(phaseCode) && null != phaseCode) {
                sb.append(" AND PHASECODE = '" + phaseCode + "'");
            }
            sb.append("))");
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                String id = rs.getString("APPLYRECORDID");
                CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
                cmPrintRecordInfoBean.setBarCode(rs.getString("BARCODE"));
                cmPrintRecordInfoBean.setPrintDate(rs.getString("PDATE"));
                cmPrintRecordInfoBean.setPrintUser(rs.getString("PUSER"));
                cmPrintRecordInfoBean.setGetDept(rs.getString("GDEPT"));
                cmPrintRecordInfoBean.setGetDate(rs.getString("GDATE"));
                cmPrintRecordInfoBean.setGetUser(rs.getString("GUSER"));
                cmPrintRecordInfoBean.setFileState(rs.getString("FILESTATUS"));
                cmPrintRecordInfoBean.setBatch(rs.getString("BATCH"));
                cmPrintRecordInfoBean = buildBeanForReprintInfo(cmPrintRecordInfoBean, id);
                listBean.add(cmPrintRecordInfoBean);
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
        return listBean;
    }

    private static CmPrintRecordInfoBean buildBeanForReprintInfo(CmPrintRecordInfoBean cmPrintRecordInfoBean, String id) {
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT * FROM GWPRINTAPPLYRECORD WHERE GWKEYID IN ");
            sb.append("(SELECT APPLYRECORDID FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID = '" + id + "')");
            ResultSet rs = state.executeQuery(sb.toString());
            //只有一条记录
            while (rs.next()) {
                cmPrintRecordInfoBean.setDocVR(rs.getString("DOCVR"));
                cmPrintRecordInfoBean.setFileNumber(rs.getString("DOCNUMBER"));
                cmPrintRecordInfoBean.setFileName(rs.getString("DOCNAME"));
                cmPrintRecordInfoBean.setFileType(rs.getString("FILETYPE"));
                cmPrintRecordInfoBean.setDocVersion(rs.getString("VERSION"));
                cmPrintRecordInfoBean.setPhaseCode(rs.getString("PHASECODE"));
                cmPrintRecordInfoBean.setSecret(rs.getString("SECRET"));
                break;
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
        return cmPrintRecordInfoBean;
    }

    public static CmPrintRecordInfoBean getInfoByBarCode(String barCode) {
        CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            String sql = "select * from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.BARCODE = '" + barCode + "'";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                cmPrintRecordInfoBean.setFileNumber(rs.getString("DOCNUMBER"));
                cmPrintRecordInfoBean.setFileName(rs.getString("DOCNAME"));
                cmPrintRecordInfoBean.setDocVersion(rs.getString("VERSION"));
                cmPrintRecordInfoBean.setPhaseCode(rs.getString("PHASECODE"));
                cmPrintRecordInfoBean.setSecret(rs.getString("SECRET"));
                cmPrintRecordInfoBean.setBatch(rs.getString("BATCH"));
                cmPrintRecordInfoBean.setBarCode(rs.getString("BARCODE"));
                cmPrintRecordInfoBean.setGetDate(rs.getString("GDATE"));
                cmPrintRecordInfoBean.setGetDept(rs.getString("GDEPT"));
                cmPrintRecordInfoBean.setGetUser(rs.getString("GUSER"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cmPrintRecordInfoBean;
    }

    public static CmPrintRecordInfoBean getInfoByUserName(String userName) {
        CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            String sql = "select * from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c, GWPRINTRECOVERRECORD d where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid = d.applyrecordid and d.RECEIVEUSER = '" + userName + "'";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                cmPrintRecordInfoBean.setCurrentUser(rs.getString("RECEIVEUSER"));
                cmPrintRecordInfoBean.setCurrentDate(rs.getString("RECEIVEDATE"));
                cmPrintRecordInfoBean.setGetDept(rs.getString("GDEPT"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cmPrintRecordInfoBean;
    }

    /**
     * 重新打印更新打印时间
     *
     * @param listBean
     * @param time
     * @author zhuhao
     * @date 2018-5-14
     */
    public static void updateReprintInfo(List<CmPrintRecordInfoBean> listBean, String time) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < listBean.size(); i++) {
                String barCode = listBean.get(i).getBarCode();
                String sql = "update GWPRINTBARCODE set PDATE = '" + time + "' where BARCODE = '" + barCode + "'";
                con.executeUpdate(sql);
            }
            con.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //判断是否存在延迟回收的文件
    public static List<String> getDelayInfoByNumber(List<String> list) {
        List<String> listStr = new ArrayList<String>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String pboNumber = list.get(i).toString();
                String sql = "select GWKEYID from GWPRINTBARCODE where GWKEYID in (select APPLYRECORDID from GWPRINTRECOVERRECORD where DELAYPBONUMBER = '" + pboNumber + "') and FILESTATUS = '延迟回收'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    String gwkeyid = rs.getString("GWKEYID");
                    listStr.add(gwkeyid);
                }
            }
            for (int i = 0; i < list.size(); i++) {
                String pboNumber = list.get(i).toString();
                String sql1 = "select GWKEYID from GWPRINTBARCODE where GWKEYID in (select APPLYRECORDID from GWPRINTSTORERECORD where DELAYPBONUMBER = '" + pboNumber + "') and FILESTATUS = '延迟封存'";
                ResultSet rs1 = con.executeQuery(sql1);
                while (rs1.next()) {
                    String gwkeyid = rs1.getString("GWKEYID");
                    listStr.add(gwkeyid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listStr;
    }

    //查找延迟回收或封存文件的延迟时间
    public static Map<String, String> getDelayDateByID(List<String> list) {
        Map<String, String> map = new HashMap<String, String>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String applyrecordid = list.get(i).toString();
                String sql = "select * from GWPRINTRECOVERRECORD where APPLYRECORDID = '" + applyrecordid + "'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    String gwkeyid = rs.getString("GWKEYID");
                    String delayDate = rs.getString("DELAYDATE");
                    map.put(gwkeyid, delayDate);
                }
            }
            for (int i = 0; i < list.size(); i++) {
                String applyrecordid = list.get(i).toString();
                String sql = "select * from GWPRINTSTORERECORD where APPLYRECORDID = '" + applyrecordid + "'";
                ResultSet rs = con.executeQuery(sql);
                while (rs.next()) {
                    String gwkeyid = rs.getString("GWKEYID");
                    String delayDate = rs.getString("DELAYDATE");
                    map.put(gwkeyid, delayDate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static String inFactoryImportInfoToDB2(List<CmImportBean> list) {
        System.out.println("save to DB start");
        StringBuffer sb = new StringBuffer();
        String applyRecordSql = "insert into GWPRINTAPPLYRECORD (GWKEYID,DOCVR,DOCNUMBER,DOCNAME,VERSION,PHASECODE,SECRET,BATCH,APPLIER,APPLYDATE,PRINTSTATUS,DISMESSAGE,FILETYPE,OUTDEPT,TECHNICSNUMBER,ISIMPORT) values ";
        String distributeRecordSql = "insert into GWPRINTDISTRIBUTERECORD (GWKEYID,DOCVR,APPLYRECORDID,DISTRIBUTEDEPT,DISTRIBUTEQUANTITY,ISIMPORT) values ";
        String barcodeSql = "insert into GWPRINTBARCODE (GWKEYID,APPLYRECORDID,GDEPT,FILESTATUS,ISIMPORT) values ";
        StringBuilder applyRecordValue;
        StringBuilder distributeRecordValue;
        StringBuilder barcodeValue;
        DBConnUtil dbConnUtil = null;
        try {
            dbConnUtil = new DBConnUtil();
            for (CmImportBean cmImportBean : list) {
                applyRecordValue = new StringBuilder();
                distributeRecordValue = new StringBuilder();
                String oid = cmImportBean.getOid();
                String fileNumber = cmImportBean.getFileNumber();

                String fileName = cmImportBean.getFileName();
                String version = cmImportBean.getVersion();
                String phaseCode = cmImportBean.getPhaseCode();
                String secret = cmImportBean.getSecret();
                String batch = "";
                String fileType = cmImportBean.getFileType();
                String outDept = cmImportBean.getOutDept();
                String distributeQuantity = cmImportBean.getDistributeQuantity();
                String distributeDept = cmImportBean.getDistributeDept();
                String index = cmImportBean.getIndex();


                String applyRecordGwkeyid = UUID.randomUUID().toString();
                applyRecordValue.append("('").append(applyRecordGwkeyid).append("','").append(oid).append("','").append(fileNumber).append("','").append(fileName).append("','").append(version).append("','");
                applyRecordValue.append(phaseCode).append("','").append(secret).append("','").append(batch).append("','").append("").append("','").append("").append("','").append("已分发").append("','");
                applyRecordValue.append(distributeDept).append(":").append(distributeQuantity).append("份").append("','").append(fileType).append("','").append(outDept).append("','").append(fileNumber).append("','").append("Y").append("')");

                String distributeRecordGwkeyid = UUID.randomUUID().toString();
                distributeRecordValue.append("('").append(distributeRecordGwkeyid).append("','").append(fileNumber).append("','").append(applyRecordGwkeyid).append("','").append(distributeDept).append("','").append(distributeQuantity).append("','").append("Y").append("')");

                int count = Integer.parseInt(distributeQuantity);
                for (int i = 0; i < count; i++) {
                    barcodeValue = new StringBuilder();
                    String barcodeGwkeyid = UUID.randomUUID().toString();
                    barcodeValue.append("('").append(barcodeGwkeyid).append("','").append(distributeRecordGwkeyid).append("','").append(distributeDept).append("','").append("已分发").append("','").append("Y").append("')");
                    dbConnUtil.executeUpdate(barcodeSql + barcodeValue.toString());

                }
                dbConnUtil.executeUpdate(distributeRecordSql + distributeRecordValue);
                dbConnUtil.executeUpdate(applyRecordSql + applyRecordValue);
                dbConnUtil.commit();
                sb.append("第" + index + "行" + fileName + "导入成功</br>");
            }
        } catch (Exception e) {
            try {
                dbConnUtil.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
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
        System.out.println("save to DB end");
        return sb.toString();
    }

    //INSERT INTO GWUSERCODETABLE VALUES('" + userBean.getUserCode() + "','" + userBean.getUserOid() + "','" + userBean.getUserName() + "','" + userBean.getFullName() + "','" + userBean.getDept() + "')
    //厂内将历史信息导入数据库
    public static void inFactoryImportInfoToDB(List<CmImportBean> list) {
        DBConnUtil con = null;
        int count = 0;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String fileType = list.get(i).getFileType();
                String fileNumber = list.get(i).getFileNumber();
                String fileName = list.get(i).getFileName();
                String version = list.get(i).getVersion();
                String distributeDept = list.get(i).getDistributeDept();
                String distributeQuantity = list.get(i).getDistributeQuantity();
                String sql = "select * from GWPRINTAPPLYRECORD where DOCNUMBER = '" + fileNumber + "'";
                ResultSet rs = con.executeQuery(sql);
                if (!rs.next()) {
                    String uuid = UUID.randomUUID().toString();
                    String uuid1 = UUID.randomUUID().toString();
                    String sql1 = "insert into GWPRINTAPPLYRECORD(GWKEYID, DOCNUMBER, DOCNAME, VERSION, FILETYPE, OUTDEPT) values('" + uuid + "', '" + fileNumber + "', '" + fileName + "', '" + version + "', '" + fileType + "', '厂内纸质')";
                    String sql2 = "insert into GWPRINTDISTRIBUTERECORD(GWKEYID, APPLYRECORDID, DISTRIBUTEDEPT, DISTRIBUTEQUANTITY) values('" + uuid1 + "', '" + uuid + "', '" + distributeDept + "', '" + distributeQuantity + "')";
                    for (int j = 0; j < Integer.parseInt(distributeQuantity); j++) {
                        String uuid2 = UUID.randomUUID().toString();
                        String sql3 = "insert into GWPRINTBARCODE(GWKEYID, APPLYRECORDID, GDEPT, FILESTATUS) values('" + uuid2 + "', '" + uuid1 + "', '" + distributeDept + "', '已下发')";
                        con.executeUpdate(sql3);
                    }
                    con.executeUpdate(sql1);
                    con.executeUpdate(sql2);
                    con.commit();
                } else {
                    String sql4 = "select * from GWPRINTDISTRIBUTERECORD where DISTRIBUTEDEPT = '" + distributeDept + "' and APPLYRECORDID = (select GWKEYID from GWPRINTAPPLYRECORD where DOCNUMBER = '" + fileNumber + "')";
                    ResultSet rs1 = con.executeQuery(sql4);
                    if (!rs1.next()) {
                        String sql5 = "select GWKEYID from GWPRINTAPPLYRECORD where DOCNUMBER = '" + fileNumber + "'";
                        ResultSet rs2 = con.executeQuery(sql5);
                        while (rs2.next()) {
                            String uuid3 = rs2.getString("GWKEYID");
                            String uuid4 = UUID.randomUUID().toString();
                            String sql6 = "insert into GWPRINTDISTRIBUTERECORD(GWKEYID, APPLYRECORDID, DISTRIBUTEDEPT, DISTRIBUTEQUANTITY) values('" + uuid4 + "', '" + uuid3 + "', '" + distributeDept + "', '" + distributeQuantity + "')";
                            for (int z = 0; z < Integer.parseInt(distributeQuantity); z++) {
                                String uuid5 = UUID.randomUUID().toString();
                                String sql7 = "insert into GWPRINTBARCODE(GWKEYID, APPLYRECORDID, GDEPT, FILESTATUS) values('" + uuid5 + "', '" + uuid4 + "', '" + distributeDept + "', '已下发')";
                                con.executeUpdate(sql7);
                            }
                            con.executeUpdate(sql6);
                            con.commit();
                        }
                    } else {
                        String sql5 = "select * from GWPRINTBARCODE where GDEPT = '" + distributeDept + "' and APPLYRECORDID = (select GWKEYID from GWPRINTDISTRIBUTERECORD where DISTRIBUTEDEPT = '" + distributeDept + "' and APPLYRECORDID = (select GWKEYID from GWPRINTAPPLYRECORD where DOCNUMBER = '" + fileNumber + "'))";
                        ResultSet rs2 = con.executeQuery(sql5);
                        while (rs2.next()) {
                            count = count + 1;
                        }
                        if (count != Integer.parseInt(distributeQuantity)) {
                            String sql6 = "update GWPRINTDISTRIBUTERECORD set DISTRIBUTEQUANTITY = '" + distributeQuantity + "' where DISTRIBUTEDEPT = '" + distributeDept + "' and APPLYRECORDID = (select GWKEYID from GWPRINTAPPLYRECORD where DOCNUMBER = '" + fileNumber + "')";
                            String sql7 = "delete from GWPRINTBARCODE where GDEPT = '" + distributeDept + "' and APPLYRECORDID = (select GWKEYID from GWPRINTDISTRIBUTERECORD where DISTRIBUTEDEPT = '" + distributeDept + "' and APPLYRECORDID = (select GWKEYID from GWPRINTAPPLYRECORD where DOCNUMBER = '" + fileNumber + "'))";
                            String sql8 = "select GWKEYID from GWPRINTDISTRIBUTERECORD where DISTRIBUTEDEPT = '" + distributeDept + "' and APPLYRECORDID = (select GWKEYID from GWPRINTAPPLYRECORD where DOCNUMBER = '" + fileNumber + "')";
                            con.executeUpdate(sql6);
                            con.executeUpdate(sql7);
                            ResultSet rs3 = con.executeQuery(sql8);
                            while (rs3.next()) {
                                String uuid3 = rs3.getString("GWKEYID");
                                for (int z = 0; z < Integer.parseInt(distributeQuantity); z++) {
                                    String uuid4 = UUID.randomUUID().toString();
                                    String sql9 = "insert into GWPRINTBARCODE(GWKEYID, APPLYRECORDID, GDEPT, FILESTATUS) values('" + uuid4 + "', '" + uuid3 + "', '" + distributeDept + "', '已下发')";
                                    con.executeUpdate(sql9);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //将外来历史信息导入数据库
    public static void outsideImportInfoToDB(List<CmImportBean> list) {
        DBConnUtil con = null;
        int count = 0;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < list.size(); i++) {
                String fileType = list.get(i).getFileType();
                String fileNumber = list.get(i).getFileNumber();
                String fileName = list.get(i).getFileName();
                String version = list.get(i).getVersion();
                String distributeDept = list.get(i).getDistributeDept();
                String distributeQuantity = list.get(i).getDistributeQuantity();
                String outDept = list.get(i).getOutDept();
                String sql = "select * from GWPRINTAPPLYRECORD where DOCNUMBER = '" + fileNumber + "'";
                ResultSet rs = con.executeQuery(sql);
                if (!rs.next()) {
                    String uuid = UUID.randomUUID().toString();
                    String uuid1 = UUID.randomUUID().toString();
                    String sql1 = "insert into GWPRINTAPPLYRECORD(GWKEYID, DOCNUMBER, DOCNAME, VERSION, FILETYPE, OUTDEPT) values('" + uuid + "', '" + fileNumber + "', '" + fileName + "', '" + version + "', '" + fileType + "', '" + outDept + "')";
                    String sql2 = "insert into GWPRINTDISTRIBUTERECORD(GWKEYID, APPLYRECORDID, DISTRIBUTEDEPT, DISTRIBUTEQUANTITY) values('" + uuid1 + "', '" + uuid + "', '" + distributeDept + "', '" + distributeQuantity + "')";
                    for (int j = 0; j < Integer.parseInt(distributeQuantity); j++) {
                        String uuid2 = UUID.randomUUID().toString();
                        String sql3 = "insert into GWPRINTBARCODE(GWKEYID, APPLYRECORDID, GDEPT, FILESTATUS) values('" + uuid2 + "', '" + uuid1 + "', '" + distributeDept + "', '已下发')";
                        con.executeUpdate(sql3);
                    }
                    con.executeUpdate(sql1);
                    con.executeUpdate(sql2);
                    con.commit();
                } else {
                    String sql4 = "select * from GWPRINTDISTRIBUTERECORD where DISTRIBUTEDEPT = '" + distributeDept + "' and APPLYRECORDID = (select GWKEYID from GWPRINTAPPLYRECORD where DOCNUMBER = '" + fileNumber + "')";
                    ResultSet rs1 = con.executeQuery(sql4);
                    if (!rs1.next()) {
                        String sql5 = "select GWKEYID from GWPRINTAPPLYRECORD where DOCNUMBER = '" + fileNumber + "'";
                        ResultSet rs2 = con.executeQuery(sql5);
                        while (rs2.next()) {
                            String uuid3 = rs2.getString("GWKEYID");
                            String uuid4 = UUID.randomUUID().toString();
                            String sql6 = "insert into GWPRINTDISTRIBUTERECORD(GWKEYID, APPLYRECORDID, DISTRIBUTEDEPT, DISTRIBUTEQUANTITY) values('" + uuid4 + "', '" + uuid3 + "', '" + distributeDept + "', '" + distributeQuantity + "')";
                            for (int z = 0; z < Integer.parseInt(distributeQuantity); z++) {
                                String uuid5 = UUID.randomUUID().toString();
                                String sql7 = "insert into GWPRINTBARCODE(GWKEYID, APPLYRECORDID, GDEPT, FILESTATUS) values('" + uuid5 + "', '" + uuid4 + "', '" + distributeDept + "', '已下发')";
                                con.executeUpdate(sql7);
                            }
                            con.executeUpdate(sql6);
                            con.commit();
                        }
                    } else {
                        String sql5 = "select * from GWPRINTBARCODE where GDEPT = '" + distributeDept + "' and APPLYRECORDID = (select GWKEYID from GWPRINTDISTRIBUTERECORD where DISTRIBUTEDEPT = '" + distributeDept + "' and APPLYRECORDID = (select GWKEYID from GWPRINTAPPLYRECORD where DOCNUMBER = '" + fileNumber + "'))";
                        ResultSet rs2 = con.executeQuery(sql5);
                        while (rs2.next()) {
                            count = count + 1;
                        }
                        if (count != Integer.parseInt(distributeQuantity)) {
                            String sql6 = "update GWPRINTDISTRIBUTERECORD set DISTRIBUTEQUANTITY = '" + distributeQuantity + "' where DISTRIBUTEDEPT = '" + distributeDept + "' and APPLYRECORDID = (select GWKEYID from GWPRINTAPPLYRECORD where DOCNUMBER = '" + fileNumber + "')";
                            String sql7 = "delete from GWPRINTBARCODE where GDEPT = '" + distributeDept + "' and APPLYRECORDID = (select GWKEYID from GWPRINTDISTRIBUTERECORD where DISTRIBUTEDEPT = '" + distributeDept + "' and APPLYRECORDID = (select GWKEYID from GWPRINTAPPLYRECORD where DOCNUMBER = '" + fileNumber + "'))";
                            String sql8 = "select GWKEYID from GWPRINTDISTRIBUTERECORD where DISTRIBUTEDEPT = '" + distributeDept + "' and APPLYRECORDID = (select GWKEYID from GWPRINTAPPLYRECORD where DOCNUMBER = '" + fileNumber + "')";
                            con.executeUpdate(sql6);
                            con.executeUpdate(sql7);
                            ResultSet rs3 = con.executeQuery(sql8);
                            while (rs3.next()) {
                                String uuid3 = rs3.getString("GWKEYID");
                                for (int z = 0; z < Integer.parseInt(distributeQuantity); z++) {
                                    String uuid4 = UUID.randomUUID().toString();
                                    String sql9 = "insert into GWPRINTBARCODE(GWKEYID, APPLYRECORDID, GDEPT, FILESTATUS) values('" + uuid4 + "', '" + uuid3 + "', '" + distributeDept + "', '已下发')";
                                    con.executeUpdate(sql9);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateImmediateDelay(List<String> list, Boolean isFromRecover) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            if (isFromRecover) {
                for (int i = 0; i < list.size(); i++) {
                    String barTableID = list.get(i).toString();
                    String sql = "update GWPRINTBARCODE set FILESTATUS = '延迟回收' where GWKEYID = '" + barTableID + "'";
                    con.executeUpdate(sql);
                    con.commit();
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    String barTableID = list.get(i).toString();
                    String sql = "update GWPRINTBARCODE set FILESTATUS = '延迟封存' where GWKEYID = '" + barTableID + "'";
                    con.executeUpdate(sql);
                    con.commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateImmediateDelayStatus(List<String> list, Boolean isFromRecover) {
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            if (isFromRecover) {
                for (int i = 0; i < list.size(); i++) {
                    String barTableID = list.get(i).toString();
                    String sql = "update GWPRINTBARCODE set FILESTATUS = '已回收' where GWKEYID = '" + barTableID + "'";
                    String sql1 = "update GWPRINTRECOVERRECORD set ISYANCHI = '0', DELAYREASON = '', DELAYDATE = '', COMPLETESTATUS = '', DELAYPBONUMBER = '' where APPLYRECORDID = '" + barTableID + "'";
                    con.executeUpdate(sql);
                    con.executeUpdate(sql1);
                    con.commit();
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    String barTableID = list.get(i).toString();
                    String sql = "update GWPRINTBARCODE set FILESTATUS = '已封存' where GWKEYID = '" + barTableID + "'";
                    String sql1 = "update GWPRINTSTORERECORD set ISYANCHI = '0', DELAYREASON = '', DELAYDATE = '', COMPLETESTATUS = '', DELAYPBONUMBER = '' where APPLYRECORDID = '" + barTableID + "'";
                    con.executeUpdate(sql);
                    con.commit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static String queryPhaseCodeByOid(String oid) {
        DBConnUtil con = null;
        String phaseCode = null;
        try {
            con = new DBConnUtil();
            String sql = "select PHASECODE from GWPRINTAPPLYRECORD where DOCVR = '" + oid + "'";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                phaseCode = rs.getString("PHASECODE");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return phaseCode;
    }

    public static String querySecretByOid(String oid) {
        DBConnUtil con = null;
        String secret = null;
        try {
            con = new DBConnUtil();
            String sql = "select SECRET from GWPRINTAPPLYRECORD where DOCVR = '" + oid + "'";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                secret = rs.getString("SECRET");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return secret;
    }

    public static String queryDistributeRecordByOid(String oid) {
        DBConnUtil con = null;
        String distributeRecord = null;
        try {
            con = new DBConnUtil();
            String sql = "select DISMESSAGE from GWPRINTAPPLYRECORD where DOCVR = '" + oid + "'";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                distributeRecord = rs.getString("DISMESSAGE");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return distributeRecord;
    }

    public static String queryPrintStatusByOid(String oid) {
        DBConnUtil con = null;
        String printStatus = null;
        try {
            con = new DBConnUtil();
            String sql = "select PRINTSTATUS from GWPRINTAPPLYRECORD where DOCVR = '" + oid + "'";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                printStatus = rs.getString("PRINTSTATUS");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return printStatus;
    }

//	public static String queryIDByOid(String oid){
//		DBConnUtil con = null;
//		String id = null;
//		try {
//			DBConnUtil con = null;
//			String sql = "select GWKEYID from GWPRINTAPPLYRECORD where DOCVR = '"+ oid +"'";
//			ResultSet rs = con.executeQuery(sql);
//			while(rs.next()){
//				id = rs.getString("GWKEYID");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				con.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		return id;
//	}

    public static List<String> queryBarCodeTableIDByOid(String oid) {
        List<String> list = new ArrayList<String>();
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            String sql = "select c.GWKEYID as ID from GWPRINTAPPLYRECORD a, GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and a.DOCVR = '" + oid + "' and c.FILESTATUS = '已下发'";
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                String id = rs.getString("ID");
                list.add(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static Boolean checkChangeRecoverFile(String oid) {
        Boolean flag = false;
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            String sql = "select * from GWPRINTRECOVERRECORD where PBOOID = '" + oid + "'";
            ResultSet rs = con.executeQuery(sql);
            if (rs.next()) {
                flag = true;
                return flag;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static Boolean SynchDangan(List<CmPrintRecordInfoBean> listBean) {
        Boolean flag = false;
        DBConnUtil con = null;
        try {
            con = new DBConnUtil();
            for (int i = 0; i < listBean.size(); i++) {
                String barCode = listBean.get(i).getBarCode();
                String sql = "update GWPRINTBARCODE set FILESTATUS = '已入库' where BARCODE = '" + barCode + "'";
                con.executeUpdate(sql);
            }
            con.commit();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static List<CmPrintRecordInfoBean> queryReceiveInfo(String pboOid, String dept) {
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("select * from GWPRINTAPPLYRECORD where PBOOID = '" + pboOid + "'");
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
                String count = rs.getString("DISMESSAGE");
                if ("".equals(count) || count == null) {
                    continue;
                }
                if (count.contains(dept)) {
                    count = count.substring(count.indexOf(dept) + dept.length() + 1);
                    count = count.substring(0, count.indexOf("份"));
                } else {
                    continue;
                }
                cmPrintRecordInfoBean.setFileNumber(rs.getString("DOCNUMBER"));
                cmPrintRecordInfoBean.setFileName(rs.getString("DOCNAME"));
                cmPrintRecordInfoBean.setDocVersion(rs.getString("VERSION"));
                cmPrintRecordInfoBean.setSecret(rs.getString("SECRET"));
                cmPrintRecordInfoBean.setPhaseCode(rs.getString("PHASECODE"));
                cmPrintRecordInfoBean.setDisMessage(count);
                listBean.add(cmPrintRecordInfoBean);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return listBean;
    }

    public static List<CmPrintRecordInfoBean> queryOffSetInfo(String oid, String dept) {
        List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT * FROM GWPRINTAPPLYRECORD WHERE PBOOID = '" + oid + "' AND GWKEYID IN ");
            sb.append("(SELECT APPLYRECORDID FROM GWPRINTDISTRIBUTERECORD WHERE GWKEYID IN ");
            sb.append("(SELECT APPLYRECORDID FROM GWPRINTBARCODE WHERE GDEPT = '" + dept + "' AND OFFSET = 'true'))");
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                String id = rs.getString("GWKEYID");
                CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
                cmPrintRecordInfoBean.setFileNumber(rs.getString("DOCNUMBER"));
                cmPrintRecordInfoBean.setFileName(rs.getString("DOCNAME"));
                cmPrintRecordInfoBean.setDocVersion(rs.getString("VERSION"));
                cmPrintRecordInfoBean.setSecret(rs.getString("SECRET"));
                cmPrintRecordInfoBean.setPhaseCode(rs.getString("PHASECODE"));
                String count = queryCountByDeptAndId(id, dept);
                cmPrintRecordInfoBean.setDisMessage(count);
                listBean.add(cmPrintRecordInfoBean);
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
        return listBean;
    }

    private static String queryCountByDeptAndId(String id, String dept) {
        int count = 0;
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT * FROM GWPRINTBARCODE WHERE APPLYRECORDID IN ");
            sb.append("(SELECT GWKEYID FROM GWPRINTDISTRIBUTERECORD WHERE APPLYRECORDID = '" + id + "')");
            sb.append(" AND GDEPT = '" + dept + "' AND OFFSET = 'true'");
            sb.append(" AND FILESTATUS = '已打印'");
            ResultSet rs = state.executeQuery(sb.toString());
            while (rs.next()) {
                count++;
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
        return String.valueOf(count);
    }

    public static Boolean checkRecyclingStateByID(List<String> list) {
        boolean flag = false;
        Connection conn = null;
        try {
            conn = OracleDataSource.getOracleDataSource().getConnection();
            conn.setAutoCommit(false);
            Statement state = conn.createStatement();
            for (int i = 0; i < list.size(); i++) {
                String gwkeyid = list.get(i);
                String sql = "select FILESTATUS from GWPRINTBARCODE where GWKEYID = (select APPLYRECORDID from GWPRINTRECOVERRECORD where GWKEYID = '" + gwkeyid + "')";
                ResultSet rs = state.executeQuery(sql);
                while (rs.next()) {
                    String fileStatus = rs.getString("FILESTATUS");
                    if ("已回收".equals(fileStatus) || "已遗失".equals(fileStatus)) {
                        flag = true;
                    } else {
                        flag = false;
                        return flag;
                    }
                }
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
        return flag;
    }

    public static Map<String, List<String>> getPrinterByDepts(String[] deptSeal) throws WTException {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for(String dept : deptSeal) {
            WTGroup group = null;
            if("档案室".equals(dept)){
                group = CSCPrincipal.getGroupByName(dept);
            }else{
                group = CSCPrincipal.getGroupByName(dept + "资料员组");
            }
            List<String> list = new ArrayList<String>();
            if(group != null){
                Vector<WTUser> users = CSCPrincipal.getGroupMemberUsers(group, new Vector());
                if(!users.isEmpty()){
                    for(WTUser user : users) {
                        list.add(user.getName());
                    }
                }
            }
            map.put(dept,list);
        }
        return map;
    }

    public static List<CmPrintRecordInfoBean> queryTransfer(String ids) {
        String[] infos = ids.split(",");
        List<String> idList = new ArrayList<String>();
        Map<String,String> infoMap = new HashMap<String, String>();
        for(String info : infos) {
            String[] str = info.split("&");
            idList.add("'"+str[0]+"'");
            infoMap.put(str[0],str[1]);
        }
        String gwkeyids = StringUtils.join(idList,",");
        List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
        DBConnUtil con = null;
        DBConnUtil conn = null;
        try {
            con = new DBConnUtil();
            String sql = "select a.*, b.*, c.GWKEYID as ID, c.BARCODE, c.FILESTATUS, c.GDATE, c.GDEPT, c.GUSER,c.PUSER,c.PDATE " +
                    "from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c " +
                    "where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid and c.gwkeyid in ( "+gwkeyids+" )";
            ResultSet rs = con.executeQuery(sql);

            while (rs.next()) {
                CmPrintRecordInfoBean cmPrintRecordInfoBean1 = new CmPrintRecordInfoBean();
                String number = rs.getString("DOCNUMBER");
                String name = rs.getString("DOCNAME");
                String version = rs.getString("VERSION");
                cmPrintRecordInfoBean1.setUnit(rs.getString("OUTDEPT"));
                String applyrecordid = rs.getString("ID");
                cmPrintRecordInfoBean1.setBarTableID(applyrecordid);
                cmPrintRecordInfoBean1.setFileNumber(number);
                cmPrintRecordInfoBean1.setFileName(name);
                cmPrintRecordInfoBean1.setFileType(rs.getString("FILETYPE"));
                cmPrintRecordInfoBean1.setDocVersion(version);
                cmPrintRecordInfoBean1.setPhaseCode(rs.getString("PHASECODE"));
                cmPrintRecordInfoBean1.setSecret(rs.getString("SECRET"));
                cmPrintRecordInfoBean1.setDistributeStatus(rs.getString("FILESTATUS"));
                cmPrintRecordInfoBean1.setGetDate(rs.getString("GDATE"));
                cmPrintRecordInfoBean1.setGetDept(rs.getString("GDEPT"));
                cmPrintRecordInfoBean1.setGetUser(rs.getString("GUSER"));
                cmPrintRecordInfoBean1.setBarCode(rs.getString("BARCODE"));
                cmPrintRecordInfoBean1.setPrintUser(rs.getString("PUSER"));
                cmPrintRecordInfoBean1.setPrintDate(rs.getString("PDATE"));
                cmPrintRecordInfoBean1.setTargetDept(infoMap.get(applyrecordid));
                String docvr = rs.getString("DOCVR");
                if (null != docvr) {
                    String containerName = "";
                    if ("WL".equals(docvr) || "ZZ".equals(docvr)) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("number", number);
                        map.put("name", name);
                        map.put("version", version);
                        containerName = PrintDataQueryUtil.getCategoryByOutFile(map);
                    } else {
                        Persistable persistable = (Persistable) Util.getObjectByOid(Persistable.class, docvr);
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
                    cmPrintRecordInfoBean1.setContainerName(containerName);
                    cmPrintRecordInfoBean1.setType(containerName);
                }
                list.add(cmPrintRecordInfoBean1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static List<CmPrintRecordInfoBean> queryTransferInfo(CmPrintRecordQueryBean cmPrintRecordQueryBean) {
        List<CmPrintRecordInfoBean> list = new ArrayList<CmPrintRecordInfoBean>();
        DBConnUtil con = null;
        DBConnUtil conn = null;
        try {
            con = new DBConnUtil();
            String fileType = cmPrintRecordQueryBean.getFileType();
            String fileNumber = cmPrintRecordQueryBean.getFileNumber();
            String fileName = cmPrintRecordQueryBean.getFileName();
            String printStartDate = cmPrintRecordQueryBean.getPrintStartDate();
            String printEndDate = cmPrintRecordQueryBean.getPrintEndDate();
            String getUser = cmPrintRecordQueryBean.getGetUser();
            String type = cmPrintRecordQueryBean.getType();
            String printer = cmPrintRecordQueryBean.getPrinter();
            String sql = "";
            sql = "select a.*, b.*, c.GWKEYID as ID, c.BARCODE, c.FILESTATUS, c.GDATE, c.GDEPT, c.GUSER,c.PUSER,c.PDATE " +
                    "from GWPRINTAPPLYRECORD a , GWPRINTDISTRIBUTERECORD b, GWPRINTBARCODE c " +
                    "where a.gwkeyid = b.applyrecordid and b.gwkeyid = c.applyrecordid ";
            if ((!"".equals(fileNumber) && null != fileNumber)) {
                sql += "and a.DOCNUMBER like '%" + fileNumber + "%'";
            }
            if ((!"".equals(fileName) && null != fileName)) {
                sql += "and a.DOCNAME like '%" + fileName + "%'";
            }
            if ((!"".equals(fileType) && null != fileType)) {
                sql += "and a.FILETYPE = '" + fileType + "'";
            }
            if (((!"".equals(printStartDate) && null != printStartDate)) && ((!"".equals(printEndDate) && null != printEndDate))) {
                sql += "and c.PDATE between '" + printStartDate + "' and '" + printEndDate + "'";
            }
            if ((!"".equals(getUser) && null != getUser)) {
                sql += "and c.GUSER like '%" + getUser + "%'";
            }
            boolean isZLY = false;
            WTUser user = UserUtil.getWTUserByName(printer);
            if(user != null){
                List<WTGroup> groups = AdministrationHelper.getUserGroups(user);
                if(groups != null && groups.size()>0){
                    for(WTGroup group : groups) {
                        String groupName = group.getName();
                        if(groupName.contains("资料员组")){
                            String dept = groupName.substring(0,groupName.indexOf("资料员组"));
                            if ((!"".equals(dept) && null != dept)) {
                                sql += "and c.GDEPT = '" + dept + "'";
                            }
                            isZLY = true;
                            break;
                        }
                    }
                }
            }
            if(!isZLY){
                return new ArrayList<CmPrintRecordInfoBean>();
            }
            ResultSet rs = con.executeQuery(sql);
            while (rs.next()) {
                CmPrintRecordInfoBean cmPrintRecordInfoBean1 = new CmPrintRecordInfoBean();
                String number = rs.getString("DOCNUMBER");
                String name = rs.getString("DOCNAME");
                String version = rs.getString("VERSION");
                cmPrintRecordInfoBean1.setUnit(rs.getString("OUTDEPT"));
                String applyrecordid = rs.getString("ID");
                cmPrintRecordInfoBean1.setBarTableID(applyrecordid);
                cmPrintRecordInfoBean1.setFileNumber(number);
                cmPrintRecordInfoBean1.setFileName(name);
                cmPrintRecordInfoBean1.setFileType(rs.getString("FILETYPE"));
                cmPrintRecordInfoBean1.setDocVersion(version);
                cmPrintRecordInfoBean1.setPhaseCode(rs.getString("PHASECODE"));
                cmPrintRecordInfoBean1.setSecret(rs.getString("SECRET"));
                cmPrintRecordInfoBean1.setDistributeStatus(rs.getString("FILESTATUS"));
                cmPrintRecordInfoBean1.setGetDate(rs.getString("GDATE"));
                cmPrintRecordInfoBean1.setGetDept(rs.getString("GDEPT"));
                cmPrintRecordInfoBean1.setGetUser(rs.getString("GUSER"));
                cmPrintRecordInfoBean1.setBarCode(rs.getString("BARCODE"));
                cmPrintRecordInfoBean1.setPrintUser(rs.getString("PUSER"));
                cmPrintRecordInfoBean1.setPrintDate(rs.getString("PDATE"));
                String docvr = rs.getString("DOCVR");
                if (null != docvr) {
                    String containerName = "";
                    if ("WL".equals(docvr) || "ZZ".equals(docvr)) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("number", number);
                        map.put("name", name);
                        map.put("version", version);
                        containerName = PrintDataQueryUtil.getCategoryByOutFile(map);
                    } else {
                        Persistable persistable = (Persistable) Util.getObjectByOid(Persistable.class, docvr);
                        if (persistable != null) {
                            if (persistable instanceof WTDocument) {
                                WTDocument document = (WTDocument) persistable;
                                containerName = document.getContainerName();
                            } else if (persistable instanceof WTChangeOrder2) {
                                WTChangeOrder2 changeOrder = (WTChangeOrder2) persistable;
                                containerName = changeOrder.getContainerName();
                            }
                        }
                        if ((!"".equals(type) && null != type)) {
                            if (!containerName.contains(type)) {
                                continue;
                            }
                        }
                    }
                    cmPrintRecordInfoBean1.setContainerName(containerName);
                    cmPrintRecordInfoBean1.setType(containerName);
                }
                list.add(cmPrintRecordInfoBean1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}

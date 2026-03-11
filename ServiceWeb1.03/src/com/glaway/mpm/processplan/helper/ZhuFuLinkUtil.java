package com.glaway.mpm.processplan.helper;

import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.mesParameter.model.GLZhuFuLink;
import com.glaway.mpm.model.MainPlanProcedure;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.util.*;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.util.WTException;

import java.beans.PropertyVetoException;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ZhuFuLinkUtil {
    private static VaLogger logger = VaLogger.getLogger(ZhuFuLinkUtil.class.getName());
    public static Comparator<String> versionComparator = new Comparator<String>() {

        @Override
        public int compare(String o1, String o2) {
            if (o1 != null && o1.startsWith("space")) {
                return -1;
            } else if (o2 != null && o2.startsWith("space")) {
                return 1;
            } else {
                return o1.compareTo(o2);
            }
        }
    };

    public static void updateById(DBConnUtil dbUtil, String gwKey, Map<String, String> rowMap) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("UPDATE GL_ZHUFULINKMASTER SET ");
        int i = 0;
        for (Map.Entry<String, String> entry : rowMap.entrySet()) {
            i++;
            sqlBuilder.append(entry.getKey()).append("=").append("'").append(entry.getValue()).append("'");
            if (i < rowMap.size()) {
                sqlBuilder.append(",");
            }
        }
        sqlBuilder.append(" WHERE GWKEY=").append("'").append(gwKey).append("'");
        logger.debug("update gl_zhufulinkmaster " + sqlBuilder.toString());
        dbUtil.executeUpdate(sqlBuilder.toString());
    }

    public static void insertOneRow(DBConnUtil dbUtil, Map<String, String> rowMap) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO GL_ZHUFULINKMASTER(");
        int i = 0;
        for (Map.Entry<String, String> entry : rowMap.entrySet()) {
            i++;
            sqlBuilder.append(entry.getKey());
            if (i < rowMap.size()) {
                sqlBuilder.append(",");
            } else {
                sqlBuilder.append(")");
            }
        }
        sqlBuilder.append(" VALUES(");
        i = 0;
        for (Map.Entry<String, String> entry : rowMap.entrySet()) {
            i++;
            sqlBuilder.append("'").append(entry.getValue()).append("'");
            if (i < rowMap.size()) {
                sqlBuilder.append(",");
            } else {
                sqlBuilder.append(")");
            }
        }
        logger.debug("insert gl_zhufulinkmaster " + sqlBuilder.toString());
        dbUtil.executeUpdate(sqlBuilder.toString());
    }

    /**
     * 添加主辅关联记录
     */
    public static void addRecord(DBConnUtil dbUtil, Map<String, String> rowMap) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO GL_ZHUFULINKRECORD(");
        int i = 0;
        for (Map.Entry<String, String> entry : rowMap.entrySet()) {
            i++;
            sqlBuilder.append(entry.getKey());
            if (i < rowMap.size()) {
                sqlBuilder.append(",");
            } else {
                sqlBuilder.append(")");
            }
        }
        sqlBuilder.append(" VALUES(");
        i = 0;
        for (Map.Entry<String, String> entry : rowMap.entrySet()) {
            i++;
            sqlBuilder.append("'").append(entry.getValue()).append("'");
            if (i < rowMap.size()) {
                sqlBuilder.append(",");
            } else {
                sqlBuilder.append(")");
            }
        }
        logger.debug("insert gl_zhufulinkmaster " + sqlBuilder.toString());
        dbUtil.executeUpdate(sqlBuilder.toString());
    }

    /**
     * 添加主辅关联记录
     */
    public static void addRecord(DBConnUtil dbUtil, GLZhuFuLink glZhuFuLink) throws SQLException {
        StringBuilder sqlBuilder;
        sqlBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO GL_ZHUFULINKRECORD (ZSTEPNUMBER,ZSTEPNAME,ZSTEPBSOID,ZTECHNICSNUMBER,ZPLANVERSION,FPLANNUMBER,FPLANNAME,FTECHNICSNUMBER,FPLANVERSION,CREATOR,CREATETIME,OPERATION)");
        sqlBuilder.append(" VALUES('");
        sqlBuilder.append(glZhuFuLink.getZstepNumber()).append("','").append(glZhuFuLink.getZstepName()).append("','");
        sqlBuilder.append(glZhuFuLink.getZstepBsoid()).append("','").append(glZhuFuLink.getZztechnicsnumber()).append("','");
        sqlBuilder.append(glZhuFuLink.getZztechnicsversion()).append("','").append(glZhuFuLink.getFppanNumber()).append("','");
        sqlBuilder.append(glZhuFuLink.getFtechnicsName()).append("','").append(glZhuFuLink.getFztechnicsnumber()).append("','");
        sqlBuilder.append(glZhuFuLink.getFztechnicsversion()).append("','").append(glZhuFuLink.getCreator()).append("','");
        sqlBuilder.append(glZhuFuLink.getCreateTime()).append("','").append(glZhuFuLink.getOperation()).append("')");
        logger.debug("insert gl_zhufulinkmaster " + sqlBuilder.toString());
        dbUtil.executeUpdate(sqlBuilder.toString());
    }


    public static List<GLZhuFuLink> getRecord(String technicsNumber, String version, String stepNumber) {
        List<GLZhuFuLink> glZhuFuLinkList = new ArrayList<GLZhuFuLink>();
        if (technicsNumber != null && !technicsNumber.isEmpty() && version != null && !version.isEmpty()) {
            String sql = "select ZSTEPNUMBER,ZSTEPNAME,FPLANNUMBER,FPLANNAME,FPLANVERSION,CREATOR,CREATETIME,OPERATION,FTECHNICSNUMBER from GL_ZHUFULINKRECORD where ZTECHNICSNUMBER='" + technicsNumber + "' and ZPLANVERSION='" + version + "'";
            if (stepNumber != null && !stepNumber.isEmpty()) {
                sql += " and ZSTEPNUMBER='" + stepNumber + "'";
            }
            sql += " order by ZSTEPNUMBER";
            DBConnUtil dbConnUtil = null;
            GLZhuFuLink glZhuFuLink;

            try {
                dbConnUtil = new DBConnUtil();
                ResultSet resultSet = dbConnUtil.executeQuery(sql);
                while (resultSet.next()) {
                    glZhuFuLink = new GLZhuFuLink();
                    glZhuFuLink.setZstepNumber(PDFUtil.objectToString(resultSet.getString("ZSTEPNUMBER")));
                    glZhuFuLink.setZstepName(PDFUtil.objectToString(resultSet.getString("ZSTEPNAME")));
                    glZhuFuLink.setFztechnicsnumber(PDFUtil.objectToString(resultSet.getString("FTECHNICSNUMBER")));
                    glZhuFuLink.setFppanNumber(PDFUtil.objectToString(resultSet.getString("FPLANNUMBER")));
                    glZhuFuLink.setFtechnicsName(PDFUtil.objectToString(resultSet.getString("FPLANNAME")));
                    glZhuFuLink.setFztechnicsversion(PDFUtil.objectToString(resultSet.getString("FPLANVERSION")));
                    glZhuFuLink.setCreator(PDFUtil.objectToString(resultSet.getString("CREATOR")));
                    glZhuFuLink.setCreateTime(PDFUtil.objectToString(resultSet.getString("CREATETIME")));
                    glZhuFuLink.setOperation(PDFUtil.objectToString(resultSet.getString("OPERATION")));
                    glZhuFuLinkList.add(glZhuFuLink);
                }
				sort(glZhuFuLinkList, "createTime", true);
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
        }
        return glZhuFuLinkList;
    }

	@SuppressWarnings("unchecked")
	public static void sort(List<GLZhuFuLink> list, String filedName, boolean ascFlag) {
		if (list.size() == 0 || "".equals(filedName)) {
			return;
		}
		Comparator<?> cmp = ComparableComparator.getInstance();
		if (ascFlag) {
			cmp = ComparatorUtils.nullLowComparator(cmp);
		} else {
			cmp = ComparatorUtils.reversedComparator(cmp);
		}
		Collections.sort(list, new BeanComparator(filedName, cmp));
	}

	/**
	 * 查询主辅link Map{key:columnName;value:columnValue}
	 *
	 * @param params
	 * @throws WTException
	 * @throws Exception
	 */
	public static Map<String, Map<String, String>> queryLink(Map<String, String> params) throws WTException {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT * FROM GL_ZHUFULINKMASTER");
		if (params != null && params.size() > 0) {
			sqlBuilder.append(" WHERE ");
			int i = 0;
			for (Map.Entry<String, String> entry : params.entrySet()) {
				i++;
				sqlBuilder.append(entry.getKey()).append("=").append("'").append(entry.getValue()).append("'");
				if (i < params.size()) {
					sqlBuilder.append(" AND ");
				}
			}
		}
		DBConnUtil dbUtil = null;
		ResultSet rs = null;
		Map<String, Map<String, String>> resultMap = new HashMap<String, Map<String, String>>();
		try {
			dbUtil = new DBConnUtil();
			rs = dbUtil.executeQuery(sqlBuilder.toString());
			WTDocument doc = null;
			Map<String, String> map = null;
			while (rs.next()) {
				map = new HashMap<String, String>();
				String zBsoid = rs.getString("ZZPROCEDUREBSOID");
				String fDocNum = rs.getString("FZTECHNICSNUMBER");
				String fVersion = rs.getString("FZTECHNICSVERSION");
				doc = WTDocumentUtil.getWTDocument(fDocNum, null, fVersion, null);
				map.put("creator", rs.getString("CREATOR"));
				map.put("createTime", rs.getString("CREATETIME"));
				map.put("fDocNum", fDocNum);
				if (doc != null) {
					IBAHelper iba = new IBAHelper(doc);
					map.put("fPlanNumber", iba.getIBAValue("PPNUMBER"));
					map.put("fPlanName", doc.getName());
					map.put("fVersion", doc.getIterationDisplayIdentifier().toString());
					if (resultMap.containsKey(zBsoid)) {
						if (ZhuFuLinkUtil.versionComparator.compare(fVersion, resultMap.get(zBsoid).get("fVersion")) > 0) {
							resultMap.put(zBsoid, map);
						}
					} else {
						resultMap.put(zBsoid, map);
					}
				} else {
					logger.error(String.format("无法找到文档通过编号【%s】版本【%s】", fDocNum, fVersion));
				}
			}
		} catch (Exception e) {
			throw new WTException(e);
		} finally {
			if (dbUtil != null) {
				try {
					dbUtil.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return resultMap;
	}

    public static void delete(DBConnUtil dbUtil, Map<String, String> rowMap) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("DELETE FROM GL_ZHUFULINKMASTER");
        if (rowMap == null || rowMap.size() == 0) {
            throw new WTException("删除条件为空，不允许删除！！！");
        }
        sqlBuilder.append(" WHERE ");
        int i = 0;
        for (Map.Entry<String, String> entry : rowMap.entrySet()) {
            i++;
            sqlBuilder.append(entry.getKey()).append("=").append("'").append(entry.getValue()).append("'");
            if (i < rowMap.size()) {
                sqlBuilder.append(" AND ");
            }
        }
        logger.debug("delete zhufulinkmaster " + sqlBuilder.toString());
        dbUtil.executeUpdate(sqlBuilder.toString());
    }

    public static void deleteZFLink(DBConnUtil dbUtil, Map<String, String> rowMap) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("DELETE FROM GL_ZHUFULINK");
        if (rowMap == null || rowMap.size() == 0) {
            throw new WTException("删除条件为空，不允许删除！！！");
        }
        sqlBuilder.append(" WHERE ");
        int i = 0;
        for (Map.Entry<String, String> entry : rowMap.entrySet()) {
            i++;
            sqlBuilder.append(entry.getKey()).append("=").append("'").append(entry.getValue()).append("'");
            if (i < rowMap.size()) {
                sqlBuilder.append(" AND ");
            }
        }
        logger.debug("delete zhufulink " + sqlBuilder.toString());
        dbUtil.executeUpdate(sqlBuilder.toString());
    }

    public static List<Element> getProceduresOfTechnic(String number, String version) throws WTException, PropertyVetoException, DocumentException {
        WTDocument doc = WTDocumentUtil.getWTDocument(number, null, version, null);
        if (doc != null) {
            ApplicationData appData = WTDocumentUtil.getPrimaryByDocument(doc);
            byte[] bytes = WTDocumentUtil.applicationDataToByte(appData);

            String tempFilePath = PropertiesUtil.getTempPath() + File.separator + "tempDinge" + File.separator + String.valueOf(new Date().getTime()) + File.separator;
            FileUtil.writeBytes(tempFilePath, appData.getFileName(), bytes);
            ApacheZipUtil.decompress(tempFilePath + appData.getFileName(), tempFilePath + number);
            File xmlFile = new File(tempFilePath + number + File.separator + number + ".xml");
            List<Element> procedureList = ProcessEditorToWCIntfRMI.getAllProcedures(xmlFile);
            File dir = new File(tempFilePath);
            if (dir.exists()) {
                dir.delete();
            }
            return procedureList;
        } else {
            return null;
        }
    }

    public static List<Element> getProceduresOfTechnic(WTDocument doc) throws WTException, PropertyVetoException, DocumentException {
        if (doc != null) {
            String number = doc.getNumber();
            ApplicationData appData = WTDocumentUtil.getPrimaryByDocument(doc);
            byte[] bytes = WTDocumentUtil.applicationDataToByte(appData);

            String tempFilePath = PropertiesUtil.getTempPath() + File.separator + "tempDinge" + File.separator + String.valueOf(new Date().getTime()) + File.separator;
            FileUtil.writeBytes(tempFilePath, appData.getFileName(), bytes);
            ApacheZipUtil.decompress(tempFilePath + appData.getFileName(), tempFilePath + number);
            File xmlFile = new File(tempFilePath + number + File.separator + number + ".xml");
            List<Element> procedureList = ProcessEditorToWCIntfRMI.getAllProcedures(xmlFile);
            File dir = new File(tempFilePath);
            if (dir.exists()) {
                dir.delete();
            }
            return procedureList;
        } else {
            return null;
        }
    }

    public static MainPlanProcedure getStepModelByBSOID(String bsoid, List<Element> allSteps) {
        MainPlanProcedure stepModel = null;
        if (allSteps != null && allSteps.size() > 0) {
            String bsid = null;
            String stepNum = null;
            String stepName = null;
            for (Element e : allSteps) {
                bsid = e.attributeValue("bsoID");
                stepNum = e.attributeValue("stepNumber");
                stepName = e.attributeValue("stepName");
                if (bsid.equals(bsoid)) {
                    stepModel = new MainPlanProcedure(bsoid, stepNum + "_" + stepName);
                    break;
                }
            }
        }
        if (stepModel == null) {
            stepModel = new MainPlanProcedure("", "");
        }
        return stepModel;
    }

    /**
     * 工艺更改单流程自动继承上一版主辅关联记录
     *
     * @param flag
     * @param version
     * @param docNum
     * @throws WTException
     */
    public static void copyLink(String flag, String version, String beforeVersion, String docNum) throws WTException {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM GL_ZHUFULINKMASTER");
        if ("F".equals(flag)) {
            sqlBuilder.append(" WHERE ");
            sqlBuilder.append("FZTECHNICSNUMBER='").append(docNum).append("'");
            sqlBuilder.append(" AND ");
            sqlBuilder.append("FZTECHNICSVERSION='").append(beforeVersion).append("'");
        } else if ("Z".equals(flag)) {
            sqlBuilder.append(" WHERE ");
            sqlBuilder.append("ZZTECHNICSNUMBER='").append(docNum).append("'");
            sqlBuilder.append(" AND ");
            sqlBuilder.append("ZZTECHNICSVERSION='").append(beforeVersion).append("'");
        } else {
            throw new WTException("复制主辅关联失败，标记非法--" + flag);
        }
        DBConnUtil dbUtil = null;
        ResultSet rs = null;
        Map<String, Map<String, String>> resultMap = new HashMap<String, Map<String, String>>();
        try {
            SimpleDateFormat dataFormate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE);
            String date = dataFormate.format(new Date());
            dbUtil = new DBConnUtil();
            dbUtil.start();
            logger.debug(sqlBuilder.toString());
            rs = dbUtil.executeQuery(sqlBuilder.toString());
            Map<String, String> map = null;
            while (rs.next()) {
                map = new HashMap<String, String>();
                map.put("FZTECHNICSNUMBER", rs.getString("FZTECHNICSNUMBER"));
                if ("F".equals(flag)) {
                    map.put("FZTECHNICSVERSION", version);
                } else {
                    map.put("FZTECHNICSVERSION", rs.getString("FZTECHNICSVERSION"));
                }
                map.put("ZZTECHNICSNUMBER", rs.getString("ZZTECHNICSNUMBER"));
                if ("Z".equals(flag)) {
                    map.put("ZZTECHNICSVERSION", version);
                } else {
                    map.put("ZZTECHNICSVERSION", rs.getString("ZZTECHNICSVERSION"));
                }
                map.put("ZZPROCEDUREBSOID", rs.getString("ZZPROCEDUREBSOID"));
                map.put("PICIHAO", rs.getString("PICIHAO"));
                map.put("CREATOR", rs.getString("CREATOR"));
                map.put("CREATETIME", date);
                insertOneRow(dbUtil, map);
            }
            dbUtil.commit();
        } catch (Exception e) {
            try {
                dbUtil.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw new WTException(e);
        } finally {
            if (dbUtil != null) {
                try {
                    dbUtil.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 主制变更后复制上一版主辅关联
     *
     * @param version
     * @param beforeVersion
     * @param newDoc
     * @throws WTException
     */
    public static void copyZFLinkZ(String version, String beforeVersion, WTDocument newDoc) throws WTException {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT DISTINCT FZTECHNICSNUMBER,ZZPROCEDUREBSOID FROM GL_ZHUFULINKMASTER");
        sqlBuilder.append(" WHERE ZZTECHNICSNUMBER='").append(newDoc.getNumber()).append("'");
        sqlBuilder.append(" AND ");
        sqlBuilder.append("ZZTECHNICSVERSION='").append(beforeVersion).append("'");
        DBConnUtil dbUtil = null;
        ResultSet rs = null;
        Map<String, String> map = null;
        try {
            SimpleDateFormat dataFormate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE);
            String date = dataFormate.format(new Date());
            String pch = IBAHelper.getAnyIBAValueOfObject(newDoc, "BATCH");
            dbUtil = new DBConnUtil();
            dbUtil.start();
            WTDocument fDoc = null;
            rs = dbUtil.executeQuery(sqlBuilder.toString());
            logger.debug(sqlBuilder.toString());
            List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
            while (rs.next()) {
                String fDocNum = rs.getString("FZTECHNICSNUMBER");
                fDoc = WTDocumentUtil.getLatestDocumentByNumber(fDocNum);
                if (fDoc == null) {
                    logger.debug(String.format("根据文档编号【%s】无法找到对应文档", fDoc));
                    continue;
                }
                map = new HashMap<String, String>();
                map.put("FZTECHNICSNUMBER", fDocNum);
                map.put("FZTECHNICSVERSION", fDoc.getVersionIdentifier().getValue());
                map.put("ZZTECHNICSNUMBER", newDoc.getNumber());
                map.put("ZZTECHNICSVERSION", version);
                map.put("ZZPROCEDUREBSOID", rs.getString("ZZPROCEDUREBSOID"));
                map.put("PICIHAO", pch == null || pch.length() == 0 ? "无" : pch);
                map.put("CREATOR", newDoc.getModifierFullName());
                map.put("CREATETIME", date);
                dataList.add(map);
            }
            if (dataList.size() > 0) {
                for (Map<String, String> temp : dataList) {
                    insertOneRow(dbUtil, temp);
                }
            }
            dbUtil.commit();
        } catch (Exception e) {
            try {
                dbUtil.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw new WTException(e);
        } finally {
            if (dbUtil != null) {
                try {
                    dbUtil.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 辅制修订后复制上一版主辅关联
     *
     * @param version
     * @param beforeVersion
     * @param newDoc
     * @throws WTException
     */
    public static void copyZFLinkF(String version, String beforeVersion, WTDocument newDoc) throws WTException {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT DISTINCT ZZTECHNICSNUMBER, ZZPROCEDUREBSOID FROM GL_ZHUFULINKMASTER");
        sqlBuilder.append(" WHERE FZTECHNICSNUMBER='").append(newDoc.getNumber()).append("'");
        sqlBuilder.append(" AND ");
        sqlBuilder.append("FZTECHNICSVERSION='").append(beforeVersion).append("'");
        DBConnUtil dbUtil = null;
        ResultSet rs = null;
        Map<String, String> map = null;
        try {
            SimpleDateFormat dataFormate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE);
            String date = dataFormate.format(new Date());
            dbUtil = new DBConnUtil();
            dbUtil.start();
            WTDocument zDoc = null;
            logger.debug(sqlBuilder.toString());
            rs = dbUtil.executeQuery(sqlBuilder.toString());
            List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
            while (rs.next()) {
                String zDocNum = rs.getString("ZZTECHNICSNUMBER");
                zDoc = WTDocumentUtil.getLatestDocumentByNumber(zDocNum);
                if (zDoc == null) {
                    logger.debug(String.format("根据文档编号【%s】无法找到对应文档", zDoc));
                    continue;
                }
                String pch = IBAHelper.getAnyIBAValueOfObject(zDoc, "BATCH");
                map = new HashMap<String, String>();
                map.put("FZTECHNICSNUMBER", newDoc.getNumber());
                map.put("FZTECHNICSVERSION", version);
                map.put("ZZTECHNICSNUMBER", zDocNum);
                map.put("ZZTECHNICSVERSION", zDoc.getVersionIdentifier().getValue());
                map.put("ZZPROCEDUREBSOID", rs.getString("ZZPROCEDUREBSOID"));
                map.put("PICIHAO", pch == null || pch.length() == 0 ? "无" : pch);
                map.put("CREATOR", newDoc.getModifierFullName());
                map.put("CREATETIME", date);
                dataList.add(map);
            }
            if (dataList.size() > 0) {
                for (Map<String, String> temp : dataList) {
                    insertOneRow(dbUtil, temp);
                }
            }
            dbUtil.commit();
        } catch (Exception e) {
            try {
                dbUtil.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw new WTException(e);
        } finally {
            if (dbUtil != null) {
                try {
                    dbUtil.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void syncZFLink(WTObject pbo) throws ChangeException2, WTException {
        if (pbo instanceof WTChangeOrder2) {
            WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
            QueryResult afters = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
            while (afters.hasMoreElements()) {
                Object object = afters.nextElement();
                if (object instanceof WTDocument) {
                    syncZFLink((WTDocument) object);
                }
            }
        }
    }

    public static void syncZFLink(WTDocument doc) throws WTException {
        DBConnUtil dbUtil = null;
        try {
            String softType = TypeUtil.getSoftType(doc, true);
            if (softType != null && softType.contains("casc.sast.149.PROCESS_PLAN")) {
                List<Element> procedures = getProceduresOfTechnic(doc);
                if (procedures != null && procedures.size() > 0) {
                    String version = doc.getVersionIdentifier().getValue();
                    String docNumber = doc.getNumber();
                    String zfFlag = IBAHelper.getAnyIBAValueOfObject(doc, "ZFFLAG");
                    StringBuilder select = new StringBuilder();
                    select.append("SELECT * FROM GL_ZHUFULINKMASTER");
                    select.append(" WHERE ");
                    dbUtil = new DBConnUtil();
                    if ("F".equals(zfFlag)) {
                        // 辅制工艺
                        // 缓存主制工艺BSOID与工序名称的键值对<String:文档编号&版本,
                        // Map<String:bsoid, String:工序名称>>
                        Map<String, Map<String, String>> cache = new HashMap<String, Map<String, String>>();

                        select.append("FZTECHNICSNUMBER='").append(docNumber).append("'");
                        select.append(" AND ");
                        select.append("FZTECHNICSVERSION='").append(version).append("'");
                        logger.debug(select.toString());
                        ResultSet rs = dbUtil.executeQuery(select.toString());
                        String stepNum = null;
                        String stepName = null;
                        String label = null;
                        String zDocNum = null;
                        String zDocVersion = null;
                        String pch = null;
                        String bsoid = null;
                        Map<String, String> oneDocCache = null;
                        StringBuilder insert = new StringBuilder("INSERT ALL ");
                        String subInto = "INTO GL_ZHUFULINK(GWKEY, FZTECHNICSNUMBER, FZTECHNICSVERSION, FZPROCEDURENUMBER, ZZTECHNICSNUMBER, ZZTECHNICSVERSION, ZZPROCEDURENUMBER, PICIHAO)";
                        boolean needInsert = false;
                        while (rs.next()) {
                            zDocNum = rs.getString("ZZTECHNICSNUMBER");
                            zDocVersion = rs.getString("ZZTECHNICSVERSION");
                            bsoid = rs.getString("ZZPROCEDUREBSOID");
                            pch = rs.getString("PICIHAO");
                            String cacheKey = zDocNum + "&&&" + zDocVersion;
                            if (cache.containsKey(cacheKey)) {
                                oneDocCache = cache.get(cacheKey);
                            } else {
                                oneDocCache = getEntryOfBsoidAndStep(zDocNum, zDocVersion, "1");
                                cache.put(cacheKey, oneDocCache);
                            }
                            if (oneDocCache.size() > 0) {
                                String ZStepLabel = oneDocCache.get(bsoid);
                                for (Element step : procedures) {
                                    needInsert = true;
                                    stepNum = step.attributeValue("stepNumber");
                                    stepName = step.attributeValue("stepName");
                                    label = stepNum + "_" + stepName;
                                    insert.append(subInto);
                                    insert.append(" VALUES(")//
                                            .append("'").append(UUID.randomUUID().toString()).append("',")//
                                            .append("'").append(docNumber).append("',")//
                                            .append("'").append(version).append("',")//
                                            .append("'").append(label).append("',")//
                                            .append("'").append(zDocNum).append("',")//
                                            .append("'").append(zDocVersion).append("',")//
                                            .append("'").append(ZStepLabel).append("',")//
                                            .append("'").append(pch).append("'")//
                                            .append(")");
                                }
                            }
                        }
                        // 插入SQL拼接完成，执行插入
                        StringBuilder delete = new StringBuilder();
                        delete.append("DELETE FROM GL_ZHUFULINK")//
                                .append(" WHERE ")//
                                .append("FZTECHNICSNUMBER='").append(docNumber).append("'").append(" AND ")//
                                .append("FZTECHNICSVERSION='").append(version).append("'");
                        logger.debug(delete.toString());
                        dbUtil.executeUpdate(delete.toString());
                        logger.debug("==>need insert " + needInsert);
                        if (needInsert) {
                            insert.append("SELECT 1 FROM DUAL");
                            logger.debug(insert.toString());
                            dbUtil.executeUpdate(insert.toString());
                        }
                        dbUtil.commit();
                    } else if ("Z".equals(zfFlag)) {
                        // 主制工艺
                        // 缓存辅制工序的集合
                        Map<String, List<Element>> cache = new HashMap<String, List<Element>>();

                        select.append("ZZTECHNICSNUMBER='").append(docNumber).append("'");
                        select.append(" AND ");
                        select.append("ZZTECHNICSVERSION='").append(version).append("'");
                        logger.debug(select.toString());
                        ResultSet rs = dbUtil.executeQuery(select.toString());
                        StringBuilder insert = new StringBuilder("INSERT ALL ");
                        String subInto = "INTO GL_ZHUFULINK(GWKEY, FZTECHNICSNUMBER, FZTECHNICSVERSION, FZPROCEDURENUMBER, ZZTECHNICSNUMBER, ZZTECHNICSVERSION, ZZPROCEDURENUMBER, PICIHAO)";
                        Map<String, String> bsoidEntry = getEntryOfBsoidAndStep(docNumber, version, "1");
                        if (bsoidEntry.size() > 0) {
                            String bsoid = null;
                            String zStepLabel = null;
                            String fDocNum = null;
                            String fVersion = null;
                            String cacheKey = null;
                            List<Element> allFStepElement = null;
                            String fStepNum = null;
                            String fStepName = null;
                            String pch = null;
                            boolean needInsert = false;
                            while (rs.next()) {
                                fDocNum = rs.getString("FZTECHNICSNUMBER");
                                fVersion = rs.getString("FZTECHNICSVERSION");
                                cacheKey = fDocNum + "&&&" + fVersion;
                                if (cache.containsKey(cacheKey)) {
                                    allFStepElement = cache.get(cacheKey);
                                } else {
                                    allFStepElement = getProceduresOfTechnic(fDocNum, fVersion);
                                    if (allFStepElement != null && allFStepElement.size() > 0) {
                                        cache.put(cacheKey, allFStepElement);
                                    }
                                }
                                if (allFStepElement != null) {
                                    bsoid = rs.getString("ZZPROCEDUREBSOID");
                                    pch = rs.getString("PICIHAO");
                                    zStepLabel = bsoidEntry.get(bsoid);
                                    if (zStepLabel != null) {
                                        for (Element e : allFStepElement) {
                                            fStepNum = e.attributeValue("stepNumber");
                                            fStepName = e.attributeValue("stepName");
                                            needInsert = true;
                                            insert.append(subInto);
                                            insert.append(" VALUES(")//
                                                    .append("'").append(UUID.randomUUID().toString()).append("',")//
                                                    .append("'").append(fDocNum).append("',")//
                                                    .append("'").append(fVersion).append("',")//
                                                    .append("'").append(fStepNum + "_" + fStepName).append("',")//
                                                    .append("'").append(docNumber).append("',")//
                                                    .append("'").append(version).append("',")//
                                                    .append("'").append(zStepLabel).append("',")//
                                                    .append("'").append(pch).append("'")//
                                                    .append(")");
                                        }
                                    }
                                }
                            }
                            // 插入SQL拼接完成，执行插入
                            StringBuilder delete = new StringBuilder();
                            delete.append("DELETE FROM GL_ZHUFULINK")//
                                    .append(" WHERE ")//
                                    .append("ZZTECHNICSNUMBER='").append(docNumber).append("'")//
                                    .append(" AND ")//
                                    .append("ZZTECHNICSVERSION='").append(version).append("'");
                            logger.debug(delete.toString());
                            dbUtil.executeUpdate(delete.toString());
                            logger.debug("==> need insert " + needInsert);
                            if (needInsert) {
                                insert.append("SELECT 1 FROM DUAL");
                                logger.debug(insert.toString());
                                dbUtil.executeUpdate(insert.toString());
                            }
                            dbUtil.commit();
                        } else {
                            // 主工艺下无工序
                            throw new WTException(
                                    String.format("主制工艺【%s-%s(%s)】无工序", docNumber, doc.getName(), version));
                        }
                    }
                }
            } else {
                logger.debug(doc.getName() + " 不是工艺文件-" + softType);
            }
        } catch (Exception e) {
            if (dbUtil != null) {
                try {
                    dbUtil.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            throw new WTException(e);
        } finally {
            if (dbUtil != null) {
                try {
                    dbUtil.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static Map<String, String> getEntryOfBsoidAndStep(String number, String version, String flag) throws Exception {
        Map<String, String> result = new HashMap<String, String>();
        WTDocument doc = WTDocumentUtil.getWTDocument(number, null, version, null);
        if (doc != null) {
            List<Element> allStep = getProceduresOfTechnic(doc);
            if (allStep != null && allStep.size() > 0) {
                String bsoid = null;
                String stepNum = null;
                String stepName = null;
                for (Element step : allStep) {
                    bsoid = step.attributeValue("bsoID");
                    stepNum = step.attributeValue("stepNumber");
                    stepName = step.attributeValue("stepName");
                    if ("1".equals(flag)) {
                        result.put(bsoid, stepNum + "_" + stepName);
                    } else {
                        result.put(stepNum + "_" + stepName, bsoid);
                    }
                }
            }
        }
        return result;
    }

    public static void syncHistoryData() {
        String sql = "SELECT DISTINCT FZTECHNICSNUMBER,FZTECHNICSVERSION,ZZTECHNICSNUMBER,ZZTECHNICSVERSION,ZZPROCEDURENUMBER,PICIHAO FROM GL_ZHUFULINK ORDER BY ZZTECHNICSNUMBER,ZZTECHNICSVERSION,ZZPROCEDURENUMBER";
        DBConnUtil dbUtil = null;
        try {
            dbUtil = new DBConnUtil();
            dbUtil.start();
            logger.debug("==>>sql:" + sql);
            ResultSet rs = dbUtil.executeQuery(sql);
            List<GLZhuFuLink> linkList = new ArrayList<GLZhuFuLink>();
            while (rs.next()) {
                GLZhuFuLink link = new GLZhuFuLink();
                link.setFztechnicsnumber(rs.getString("FZTECHNICSNUMBER"));
                link.setFztechnicsversion(rs.getString("FZTECHNICSVERSION"));
                link.setZztechnicsnumber(rs.getString("ZZTECHNICSNUMBER"));
                link.setZztechnicsversion(rs.getString("ZZTECHNICSVERSION"));
                link.setZzprocedurenumber(rs.getString("ZZPROCEDURENUMBER"));
                link.setPicihao(rs.getString("PICIHAO"));
                linkList.add(link);
            }
            String zNumber = null;
            String zVersion = null;
            String procedureLabel = null;
            String cacheKey = null;
            Map<String, Map<String, String>> cache = new HashMap<String, Map<String, String>>();
            Map<String, String> cacheEntry = null;
            WTDocument doc = null;
            String bsoid = null;
            String checkSql = "select count(*) from gl_zhufulinkmaster where ZZTECHNICSNUMBER='%s' and ZZTECHNICSVERSION='%s' and ZZPROCEDUREBSOID='%s'";
            String inserSql = "insert into gl_zhufulinkmaster(FZTECHNICSNUMBER,FZTECHNICSVERSION,ZZTECHNICSNUMBER,ZZTECHNICSVERSION,ZZPROCEDUREBSOID,PICIHAO,CREATOR,CREATETIME) values('%s','%s','%s','%s','%s','%s','%s','%s')";
            SimpleDateFormat dataFormate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE);
            String date = dataFormate.format(new Date());
            for (GLZhuFuLink link : linkList) {
                zNumber = link.getZztechnicsnumber();
                zVersion = link.getZztechnicsversion();
                doc = WTDocumentUtil.getWTDocument(zNumber, null, zVersion, null);
                if (doc != null) {
                    procedureLabel = link.getZzprocedurenumber();
                    cacheKey = zNumber + "&&&" + zVersion;
                    if (!cache.containsKey(cacheKey)) {
                        cacheEntry = getEntryOfBsoidAndStep(zNumber, zVersion, "2");
                    } else {
                        cacheEntry = cache.get(cacheKey);
                    }
                   if(procedureLabel.contains("_")){
                       procedureLabel = procedureLabel.substring(0,procedureLabel.indexOf("_"));
                   }
                    if (cacheEntry.containsKey(procedureLabel)) {
                        bsoid = cacheEntry.get(procedureLabel);
                        logger.debug("==>>check sql : " + checkSql);
                        ResultSet resultSet = dbUtil.executeQuery(String.format(checkSql, zNumber, zVersion, bsoid));
                        if (resultSet.next()) {
                            int count = resultSet.getInt(1);
                            if (count > 0) {
                                logger.debug(String.format("主制工艺【%s-%s】已关联了辅制工艺，无法再次关联--FZNUMBER:%s;FZVERSION:%s;ZZNUMBER:%s;ZZVERSION:%s;ZZPROCEDURE:%s", doc.getName() + zVersion, procedureLabel, link.getFzprocedurenumber(), link.getFztechnicsversion(), link.getZztechnicsnumber(), link.getZztechnicsversion(), link.getZzprocedurenumber()));
                                continue;
                            }
                        }
                        String creator = doc.getModifierFullName();
                        String insert = String.format(inserSql, link.getFztechnicsnumber(), link.getFztechnicsversion(), link.getZztechnicsnumber(), link.getZztechnicsversion(), bsoid, link.getPicihao(), creator, date);
                        logger.debug("==>>insert sql : " + insert);
                        dbUtil.executeUpdate(insert);
                    } else {
                        throw new WTException(String.format("主制工艺【%s(%s)】不存在工序【%s】", doc.getName(), zVersion, procedureLabel));
                    }
                } else {
                    throw new WTException(String.format("根据编号【%s】版本【%s】无法找到对应工艺文件", zNumber, zVersion));
                }
            }
            dbUtil.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                dbUtil.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            if (dbUtil != null) {
                try {
                    dbUtil.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据主制工艺的工艺编号和版本，获取所有关联的辅制工艺
     * @param gyNumber
     * @param gyVersion
     * @return
     * @throws WTException
     */
    public static List<Map<String, String>> getFZGY(String gyNumber, String gyVersion) throws WTException {
        String sql = String.format("SELECT FZTECHNICSNUMBER, FZTECHNICSVERSION FROM GL_ZHUFULINKMASTER WHERE ZZTECHNICSNUMBER='%s' AND ZZTECHNICSVERSION='%s'", escapeSql(gyNumber), escapeSql(gyVersion));

        DBConnUtil dbUtil = null;
        ResultSet rs = null;
        List<Map<String, String>> result = new ArrayList<>();
        Set<String> dedupSet = new HashSet<>(); // 用于去重
        try {
            dbUtil = new DBConnUtil();
            rs = dbUtil.executeQuery(sql);
            while (rs.next()) {
                Map<String, String> map = new HashMap<>();
                String number = rs.getString("FZTECHNICSNUMBER");
                String version = rs.getString("FZTECHNICSVERSION");
                String key = number + "#" + version;
                if (dedupSet.contains(key)) {
                    continue; // 已存在，跳过
                }
                dedupSet.add(key);
                map.put("number", number);
                map.put("version", version);
                result.add(map);
            }
        } catch (Exception e) {
            throw new WTException(e);
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException ignored) {
            }
            if (dbUtil != null) try {
                dbUtil.close();
            } catch (SQLException ignored) {
            }
        }

        return result;
    }

    private static String escapeSql(String value) {
        return value == null ? null : value.replace("'", "''");
    }

}

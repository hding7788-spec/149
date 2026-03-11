package com.glaway.mpm.mesDataSearch;

import com.glaway.mpm.parameter.constants.ParameterConstants;
import com.glaway.mpm.parameter.model.GWParameterTableColumn;
import com.glaway.mpm.parameter.service.gwpersistable.GwPersistenceHelper;
import com.glaway.mpm.parameter.service.gwpersistable.GwQueryResult;
import com.glaway.mpm.parameter.service.gwpersistable.GwQuerySpec;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.DBConnUtil;
import ext.casc.integrate.process.MPMOperationBean;
import ext.casc.integrate.process.ProcessService;
import wt.pom.Transaction;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MesDataSearchManager {

    protected static Vector<Vector<Object>> queryParamsByTableId(String tableName, String tableId, String productNumber, String technicsNumber, String objNumber, String objType, String isZF, String lukahao, String gxPK, String columnEnames, Vector<Vector<Object>> parmeters) {
        if (tableName != null && !"".equals(tableName)) {
            DBConnUtil conn = null;
            try {
                conn = new DBConnUtil();
                String sql = "SELECT " + columnEnames + " FROM " + "MES" + tableName.toUpperCase() +
                        " WHERE TECHNICSNUMBER='" + technicsNumber +
                        "' and PRODUCTNUMBER LIKE '%" + productNumber +
                        "%' and ISZF = '" + isZF +
//						"'and LUKAHAO='" + lukahao +
                        "' ORDER BY OBJNUMBER";
                System.out.println("MESDataSearch====sql:" + sql);
                ResultSet resultSet = conn.executeQuery(sql);
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                List<GWParameterTableColumn> columns = queryGWParameterTableColumns(tableId);
                Vector<Object> vector = null;
                while (resultSet.next()) {
                    vector = new Vector<Object>();
                    for (int i = 0; i < columnCount; i++) {
                        String dataType = columns.get(i).getDatatype();
                        String value = "";
                        if (ParameterConstants.TABLE_COLUMN_DATATYPE_INTEGER.equals(dataType)) {
                            value = String.valueOf(resultSet.getInt(i + 1));
                        } else if (ParameterConstants.TABLE_COLUMN_DATATYPE_DECIMAL.equals(dataType)) {
                            value = String.valueOf(resultSet.getFloat(i + 1));
                        } else {
                            value = CommonUtil.objectToString(resultSet.getString(i + 1));
                        }

                        if (value != null && !"".equals(value)) {
                            value = value.replaceAll(ParameterConstants.REPLACE_SINGLEQUOTE, "'");
                        }
                        vector.add(value);
                    }
                    parmeters.add(vector);
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
        return parmeters;
    }

    public static Vector<Vector<Object>> queryParamsByTableId2(String tableName, String tableId, String productNumber, String technicsNumber, String lukahao, String gxPK, String columnEnames)
            throws Exception {
        Vector<Vector<Object>> parmeters = new Vector<Vector<Object>>();
        String isZF = "";
        if (technicsNumber.endsWith("_ZF")) {
            isZF = "Y";
            technicsNumber = technicsNumber.substring(0, technicsNumber.indexOf("_ZF"));
            ProcessService ps = new ProcessService();
            List<MPMOperationBean> allBeans = (List<MPMOperationBean>) ps.getZFBeans(technicsNumber)[1];
            int newNumber = 10;
            String objType = "";
            for (MPMOperationBean stepBean : allBeans) {
                String techNumber = stepBean.getParent().getProcessNumber();
                String techVersion = stepBean.getParent().getProcessVersion();
                String stepNumber = stepBean.getNumber();
                String bsoId = stepBean.getBsoId();
                objType = "工序";
                if (techVersion.contains(".")) {
                    techVersion = techVersion.substring(0, techVersion.indexOf("."));
                }
                boolean isMesDataExist = isMesDataExist(tableName, techNumber, lukahao, productNumber, bsoId, techVersion, isZF);
                if (isMesDataExist) {
                    queryTableData(tableName, tableId, productNumber, techNumber, stepNumber, objType, lukahao, gxPK, isZF, columnEnames, parmeters, String.valueOf(newNumber), techVersion, bsoId);
                } else {
                    queryTableDataNoMes(tableName, tableId, productNumber, techNumber, stepNumber, objType, lukahao, gxPK, isZF, columnEnames, parmeters, String.valueOf(newNumber), techVersion, bsoId);
                }
                for (MPMOperationBean paceBean : stepBean.getChildrengongbu()) {
                    String paceNumber = stepNumber + "-" + paceBean.getNumber();
                    objType = "工步";
                    bsoId = paceBean.getBsoId();
                    isMesDataExist = isMesDataExist(tableName, techNumber, lukahao, productNumber, bsoId, techVersion, isZF);
                    if (isMesDataExist) {
                        queryTableData(tableName, tableId, productNumber, techNumber, paceNumber, objType, lukahao, gxPK, isZF, columnEnames, parmeters, String.valueOf(newNumber) + "-" + paceBean.getNumber(), techVersion, bsoId);
                    }else{
                        queryTableDataNoMes(tableName, tableId, productNumber, techNumber, paceNumber, objType, lukahao, gxPK, isZF, columnEnames, parmeters, String.valueOf(newNumber) + "-" + paceBean.getNumber(), techVersion, bsoId);
                    }
                }
                newNumber += 10;
            }
        } else {
            isZF = "N";
            queryParamsByTableId(tableName, tableId, productNumber, technicsNumber, "", "", isZF, lukahao, gxPK, columnEnames, parmeters);
        }
        return parmeters;
    }

    public static void queryTableData(String tableName, String tableId, String productNumber, String technicsNumber, String objNumber, String objType, String lukahao, String gxPK, String isZF, String columnEnames, Vector<Vector<Object>> parmeters, String newNumber, String version, String bsoId) {
        if (tableName != null && !"".equals(tableName)) {
            DBConnUtil conn = null;
            try {
                conn = new DBConnUtil();
                String sql = "SELECT " + columnEnames + " FROM " + "MES" + tableName.toUpperCase() +
                        " WHERE TECHNICSNUMBER='" + technicsNumber +
//                        "' and OBJNUMBER='" + objNumber +
//                        "' and OBJNUMBER='" + objNumber +
                        "' and BSOID='" + bsoId +
                        "' and VERSION='" + version +
                        "' and ISZF='" + isZF +
                        "'and PRODUCTNUMBER like '%" + productNumber +
//						"'and LUKAHAO='" + lukahao +
//						"'and GXPK='" + gxPK +
                        "%' ORDER BY SEQUENCE";
                ResultSet resultSet = conn.executeQuery(sql);
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                List<GWParameterTableColumn> columns = queryGWParameterTableColumns(tableId);
                Vector<Object> vector = null;
                while (resultSet.next()) {
                    vector = new Vector<Object>();
                    for (int i = 0; i < columnCount; i++) {
                        String columnName = metaData.getColumnName(i + 1);
                        String dataType = columns.get(i).getDatatype();
                        String value = "";
                        if (ParameterConstants.TABLE_COLUMN_DATATYPE_INTEGER.equals(dataType)) {
                            value = String.valueOf(resultSet.getInt(i + 1));
                        } else if (ParameterConstants.TABLE_COLUMN_DATATYPE_DECIMAL.equals(dataType)) {
                            value = String.valueOf(resultSet.getFloat(i + 1));
                        } else {
                            value = CommonUtil.objectToString(resultSet.getString(i + 1));
                        }
                        if ("OBJNUMBER".equals(columnName)) {
                            value = newNumber;
                        }
                        if (value != null && !"".equals(value)) {
                            value = value.replaceAll(ParameterConstants.REPLACE_SINGLEQUOTE, "'");
                        }
                        vector.add(value);
                    }
                    parmeters.add(vector);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                }
            }
        }
    }

    public static void queryTableDataNoMes(String tableName, String tableId, String productNumber, String technicsNumber, String objNumber, String objType, String lukahao, String gxPK, String isZF, String columnEnames, Vector<Vector<Object>> parmeters, String newNumber, String version, String bsoId) {
        if (tableName != null && !"".equals(tableName)) {
            DBConnUtil conn = null;
            try {
                conn = new DBConnUtil();
                String sql = "SELECT " + columnEnames + " FROM " + tableName.toUpperCase() +
                        " WHERE TECHNICSNUMBER='" + technicsNumber +
                        "' and BSOID='" + bsoId +
                        "' and VERSION='" + version +
//                        "' and ISZF='" + isZF +
                        "' ORDER BY SEQUENCE";
                ResultSet resultSet = conn.executeQuery(sql);
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                List<GWParameterTableColumn> columns = queryGWParameterTableColumns(tableId);
                Vector<Object> vector = null;
                while (resultSet.next()) {
                    vector = new Vector<Object>();
                    for (int i = 0; i < columnCount; i++) {
                        String columnName = metaData.getColumnName(i + 1);
                        String dataType = columns.get(i).getDatatype();
                        String value = "";
                        if (ParameterConstants.TABLE_COLUMN_DATATYPE_INTEGER.equals(dataType)) {
                            value = String.valueOf(resultSet.getInt(i + 1));
                        } else if (ParameterConstants.TABLE_COLUMN_DATATYPE_DECIMAL.equals(dataType)) {
                            value = String.valueOf(resultSet.getFloat(i + 1));
                        } else {
                            value = CommonUtil.objectToString(resultSet.getString(i + 1));
                        }
                        if ("OBJNUMBER".equals(columnName)) {
                            value = newNumber;
                        }
                        if (value != null && !"".equals(value)) {
                            value = value.replaceAll(ParameterConstants.REPLACE_SINGLEQUOTE, "'");
                        }
                        vector.add(value);
                    }
                    parmeters.add(vector);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                }
            }
        }
    }


    /**
     * 查询指定参数表的定义的列信息
     *
     * @param tableId
     * @return
     * @throws Exception
     */
    protected static List<GWParameterTableColumn> queryGWParameterTableColumns(String tableId) throws Exception {
        GwQuerySpec qs = new GwQuerySpec(GWParameterTableColumn.class);
        qs.appendWhere(GWParameterTableColumn.PARAMETERTABLETYPEID, GwQuerySpec.EQUAL, tableId);
        qs.appendOrderBy("ORDERNO", false);
        GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
        List<GWParameterTableColumn> columns = new ArrayList<GWParameterTableColumn>();
        GWParameterTableColumn column = null;
        while (qr.hasNext()) {
            column = (GWParameterTableColumn) qr.next();
            columns.add(column);
        }

        return columns;
    }

    public static boolean isMesDataExist(String tableName, String technicsNumber, String lukahao, String productNumber, String bsoId, String version, String isZF) {
        Transaction transaction = new Transaction();
        DBConnUtil conn = null;
        try {
            if (version.contains(".")) {
                version = version.substring(0, version.indexOf("."));
            }
            transaction.start();
            if (tableName != null && !"".equals(tableName)) {
                conn = new DBConnUtil();
                String sql = "select count(*) from " + "mes" + tableName + " t where t.TECHNICSNUMBER='" + technicsNumber +
                        //"' and t.LUKAHAO='" + lukahao +
                        "' and t.PRODUCTNUMBER like '%" + productNumber +
                        "%' and t.bsoId='" + bsoId +
                        "' and t.version='" + version +
                        "' and t.ISZF='" + isZF + "'";
                System.out.println("sql====" + sql);
                ResultSet rs = conn.executeQuery(sql);
                conn.commit();
                transaction.commit();
                if (rs.next()) {
                    int i = rs.getInt(1);
                    if (i > 0) {
                        return true;
                    }
                } else {
                    return false;
                }

            }
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            transaction = null;
        }
        return false;
    }

}

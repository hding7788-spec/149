package com.glaway.mpm.mesParameter;

import java.beans.PropertyVetoException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.util.WTException;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.parameter.GWParameterTableTypeManager;
import com.glaway.mpm.parameter.ParameterProcessor;
import com.glaway.mpm.parameter.constants.ParameterConstants;
import com.glaway.mpm.parameter.model.GWParamTableTypeMaster;
import com.glaway.mpm.parameter.model.GWParameterTableColumn;
import com.glaway.mpm.parameter.model.GWParameterTableType;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.parameter.service.gwpersistable.GwPersistenceHelper;
import com.glaway.mpm.parameter.service.gwpersistable.GwQueryResult;
import com.glaway.mpm.parameter.service.gwpersistable.GwQuerySpec;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.WTDocumentUtil;

import ext.casc.util.IBAUtility;

public class MesParameterTypeManager {

	private static VaLogger logger = VaLogger.getLogger(GWParameterTableTypeManager.class.getName());

	public static Vector<Object> getTechnicsByTechnicNumber(String technicNumber){
		Vector<Object> vector = new Vector<Object>();
		WTDocument document = null;
		byte[] bytes = null;
		try {
			document = WTDocumentUtil.getLatestDocumentByNumber(technicNumber);
			IBAUtility iba = new IBAUtility(document);
			String ppnumber = iba.getIBAValue("PPNUMBER");
			String zfFlag = iba.getIBAValue("ZFFLAG");
			if(document != null){
				ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
				bytes = WTDocumentUtil.applicationDataToByte(data);
				vector.add(data.getFileName());
				vector.add(bytes);
				vector.add(ppnumber);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return vector;
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
	public static CmParamTableType getCommonParamTableType(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) {
		return ParameterProcessor.getCommonParamTableType(technicsNumber, objType, objNumber, isApproved, bsoID, version);
	}
	protected static GWParameterTableType queryGwParameterTableType(String gwkey) {
		GWParameterTableType parameterTableType = null;
		try {
			GwQuerySpec qs = new GwQuerySpec(GWParameterTableType.class);
			qs.appendWhere(GWParameterTableType.KEY_ID, GwQuerySpec.EQUAL, gwkey);
			GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
			if (qr.hasNext()) {
				parameterTableType = (GWParameterTableType)qr.next();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return parameterTableType;
	}
	protected static GWParamTableTypeMaster queryParamTableTypeMasterById(String gwkey) {
		GWParamTableTypeMaster master = null;
		try {
			GwQuerySpec qs = new GwQuerySpec(GWParamTableTypeMaster.class);
			qs.appendWhere(GWParamTableTypeMaster.KEY_ID, GwQuerySpec.EQUAL, gwkey);
			GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
			if (qr.hasNext()) {
				master = (GWParamTableTypeMaster) qr.next();
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return master;
	}
	/**
	 * 获取最新版本的参数表类型
	 *
	 * @param masterId
	 * @return
	 * @throws Exception
	 */
	protected static GWParameterTableType getLatestTableType(String masterId) throws Exception {
		//通过主对象ID参数出所有版本的参数表格对象
		GwQuerySpec qs = new GwQuerySpec(GWParameterTableType.class);
		qs.appendWhere(GWParameterTableType.TABLETYPEMASTERID, GwQuerySpec.EQUAL, masterId);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		List<GWParameterTableType> list = new ArrayList<GWParameterTableType>();
		GWParameterTableType tableType = null;
		while (qr.hasNext()) {
			tableType = (GWParameterTableType) qr.next();
			list.add(tableType);
		}

		//按照倒叙排序
		Collections.sort(list);

		//取第一个即为最新版的对象
		if (list.size()>0) {
			tableType = list.get(0);
		}

		return tableType;
	}
	/**
	 * 通过参数表名称查询参数表格主对象
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected static GWParamTableTypeMaster queryParamTableTypeMaster(String name) throws Exception {
		GwQuerySpec qs = new GwQuerySpec(GWParamTableTypeMaster.class);
		qs.appendWhere(GWParamTableTypeMaster.NAME, GwQuerySpec.EQUAL, name);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		GWParamTableTypeMaster master = null;
		if (qr.hasNext()) {
			master = (GWParamTableTypeMaster) qr.next();
		}
		return master;
	}

	/**
	 * 查询指定对象及其指定数据记录类型的参数
	 *
	 * @param tableName 参数表名称
	 * @param tableId 参数表类型ID
	 * @param objClassName 对象类名
	 * @param objId 对象IDA2A2
	 * @param recordType 数据记录类型
	 * @return
	 */
	protected static Vector<Vector<Object>> queryParamsByTableId(String tableName, String tableId, String productNumber, String technicsNumber, String objNumber, String objType, String lukahao, String gxPK, String isZF, String columnEnames,String bsoId,String version) {
		Vector<Vector<Object>> parmeters = new Vector<Vector<Object>>();
		if (tableName != null && !"".equals(tableName)) {
			DBConnUtil conn = null;
			try {
				conn= new DBConnUtil();
				String sql = "SELECT " + columnEnames + " FROM " + "MES" + tableName.toUpperCase() +
						" WHERE TECHNICSNUMBER='" + technicsNumber +
//						"' and OBJNUMBER='" + objNumber +
//						"' and OBJTYPE='" + objType +
						"' and BSOID='" + bsoId +
						"' and VERSION='" + version +
						"'and PRODUCTNUMBER='" + productNumber +
						"'and LUKAHAO='" + lukahao +
//						"'and GXPK='" + gxPK +
						"'and ISZF='" + isZF +
						"' ORDER BY SEQUENCE";
				ResultSet resultSet = conn.executeQuery(sql);
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();
				List<GWParameterTableColumn> columns = MesParameterTypeManager.queryGWParameterTableColumns(tableId);
				Vector<Object> vector = null;
				while(resultSet.next()) {
					vector = new Vector<Object>();
					for (int i = 0; i < columnCount; i++) {
						String dataType = columns.get(i).getDatatype();
						String value = "";
						if (ParameterConstants.TABLE_COLUMN_DATATYPE_INTEGER.equals(dataType)) {
							value = String.valueOf(resultSet.getInt(i+1));
						} else if (ParameterConstants.TABLE_COLUMN_DATATYPE_DECIMAL.equals(dataType)) {
							value = String.valueOf(resultSet.getFloat(i+1));
						} else {
							value = CommonUtil.objectToString(resultSet.getString(i+1));
						}

						if (value != null && !"".equals(value)) {
							value = value.replaceAll(ParameterConstants.REPLACE_SINGLEQUOTE, "'");
						}
						vector.add(value);
					}
					parmeters.add(vector);
				}
			} catch (Exception e) {
				logger.error(e);
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					logger.error(e);
				}
			}
		}
		return parmeters;
	}

	/**
	 * 查询指定对象及其指定数据记录类型的参数
	 *
	 * @param tableName 参数表名称
	 * @param tableId 参数表类型ID
	 * @param objClassName 对象类名
	 * @param objId 对象IDA2A2
	 * @param recordType 数据记录类型
	 * @return
	 */
	protected static Vector<Vector<Object>> queryDataPackageParamsByTableId(String tableName, String tableId, String productNumber, String technicsNumber, String objNumber, String objType, String lukahao, String gxPK, String columnEnames) {
		Vector<Vector<Object>> parmeters = new Vector<Vector<Object>>();
		if (tableName != null && !"".equals(tableName)) {
			DBConnUtil conn = null;
			try {
				conn= new DBConnUtil();
				String sql = "SELECT " + columnEnames + " FROM " + tableName.toUpperCase() + " a"+",MES" + tableName.toUpperCase() + " b" +
						" WHERE a.TECHNICSNUMBER='" + technicsNumber +
						"' and a.OBJNUMBER='" + objNumber +
						"' and a.OBJTYPE='" + objType +
						"'and b.PRODUCTNUMBER='" + productNumber +
//						"'and b.LUKAHAO='" + lukahao +
//						"'and b.GXPK='" + gxPK +
						"'and a.GWKEY=b.GWKEY ORDER BY SEQUENCE";
				ResultSet resultSet = conn.executeQuery(sql);
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();
				List<GWParameterTableColumn> columns = MesParameterTypeManager.queryGWParameterTableColumns(tableId);
				Vector<Object> vector = null;
				while(resultSet.next()) {
					vector = new Vector<Object>();
					for (int i = 0; i < columnCount; i++) {
						String dataType = columns.get(i).getDatatype();
						String value = "";
						if (ParameterConstants.TABLE_COLUMN_DATATYPE_INTEGER.equals(dataType)) {
							value = String.valueOf(resultSet.getInt(i+1));
						} else if (ParameterConstants.TABLE_COLUMN_DATATYPE_DECIMAL.equals(dataType)) {
							value = String.valueOf(resultSet.getFloat(i+1));
						} else {
							value = CommonUtil.objectToString(resultSet.getString(i+1));
						}

						if (value != null && !"".equals(value)) {
							value = value.replaceAll(ParameterConstants.REPLACE_SINGLEQUOTE, "'");
						}
						vector.add(value);
					}
					parmeters.add(vector);
				}
			} catch (Exception e) {
				logger.error(e);
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					logger.error(e);
				}
			}
		}
		return parmeters;
	}

	/**
	 * 查询指定对象及其指定数据记录类型的参数
	 *
	 * @param tableName 参数表名称
	 * @param tableId 参数表类型ID
	 * @param objClassName 对象类名
	 * @param objId 对象IDA2A2
	 * @param recordType 数据记录类型
	 * @return
	 */
	protected static Vector<Vector<Object>> queryMesParamsByTableId(String tableName, String tableId,String productNumber, String technicsNumber, String objNumber, String objType, String lukahao, String gxPK, String isZF, String columnEnames,String bsoId,String version) {
		Vector<Vector<Object>> parmeters = new Vector<Vector<Object>>();
		if (tableName != null && !"".equals(tableName)) {
			DBConnUtil conn = null;
			try {
				conn= new DBConnUtil();
				String sql = "SELECT " + columnEnames + " FROM " + "MES" + tableName.toUpperCase() +
						" T WHERE T.TECHNICSNUMBER='" + technicsNumber +
//						"' and T.OBJNUMBER='" + objNumber +
						"' and T.BSOID='" + bsoId +
						"' and T.VERSION='" + version +
						"' and T.PRODUCTNUMBER='" + productNumber +
						"' and T.LUKAHAO='" + lukahao +
//						"' and T.GXPK='"  + gxPK +
						"' and T.ISZF='"  + isZF +
//						"' and T.OBJTYPE='" + objType +
						"' ORDER BY SEQUENCE";
				ResultSet resultSet = conn.executeQuery(sql);
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();
				List<GWParameterTableColumn> columns = MesParameterTypeManager.queryGWParameterTableColumns(tableId);
				Vector<Object> vector = null;
				while(resultSet.next()) {
					vector = new Vector<Object>();
					for (int i = 0; i < columnCount; i++) {
						String dataType = columns.get(i).getDatatype();
						String value = "";
						if (ParameterConstants.TABLE_COLUMN_DATATYPE_INTEGER.equals(dataType)) {
							value = String.valueOf(resultSet.getInt(i+1));
						} else if (ParameterConstants.TABLE_COLUMN_DATATYPE_DECIMAL.equals(dataType)) {
							value = String.valueOf(resultSet.getFloat(i+1));
						} else {
							value = CommonUtil.objectToString(resultSet.getString(i+1));
						}

						if (value != null && !"".equals(value)) {
							value = value.replaceAll(ParameterConstants.REPLACE_SINGLEQUOTE, "'");
						}
						vector.add(value);
					}
					parmeters.add(vector);
				}
			} catch (Exception e) {
				logger.error(e);
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					logger.error(e);
				}
			}
		}
		return parmeters;
	}


}

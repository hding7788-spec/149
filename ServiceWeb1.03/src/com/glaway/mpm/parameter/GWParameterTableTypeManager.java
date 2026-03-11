package com.glaway.mpm.parameter;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.util.WTException;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.parameter.constants.ParameterConstants;
import com.glaway.mpm.parameter.model.GWParamTableTypeMaster;
import com.glaway.mpm.parameter.model.GWParameterTableColumn;
import com.glaway.mpm.parameter.model.GWParameterTableType;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.parameter.model.data.CmParameterTableColumn;
import com.glaway.mpm.parameter.model.data.CmTechnicsType;
import com.glaway.mpm.parameter.service.gwpersistable.GwPersistenceHelper;
import com.glaway.mpm.parameter.service.gwpersistable.GwQueryResult;
import com.glaway.mpm.parameter.service.gwpersistable.GwQuerySpec;
import com.glaway.mpm.pbom.db.MESDBUtil;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.LoadConfig;
import com.glaway.mpm.util.MPMUtil;
import com.ptc.windchill.mpml.resource.MPMSkill;

public class GWParameterTableTypeManager {

	private static VaLogger logger = VaLogger.getLogger(GWParameterTableTypeManager.class.getName());

	/**
	 * 加载工艺资源库中所有工艺类别资源
	 *
	 * @return
	 */
	protected static List<CmTechnicsType> queryTechnicsTypes() {
		List<CmTechnicsType> list = new ArrayList<CmTechnicsType>();
		try {
			QueryResult qr = MPMUtil.getAllTechnicsTypeMPMSkill();
			MPMSkill skill = null;
			CmTechnicsType technicsType = null;
			while (qr.hasMoreElements()) {
				skill = (MPMSkill)qr.nextElement();

				technicsType = new CmTechnicsType();
				technicsType.setOid(PersistenceHelper.getObjectIdentifier(skill).getId());
				technicsType.setName(skill.getName());

				list.add(technicsType);
			}
		} catch (WTException e) {
			logger.error(e);
		}
		return list;
	}

	/**
	 * 查询指定参数表中的参数值集合
	 *
	 * @param tableName 参数表表名称
	 * @param tableId 参数表类型或模板参数表的ID
	 * @param paramTableTypeIid 参数表类型ID
	 * @return
	 */
	protected static Vector<Vector<String>> queryParamsByTableId(String tableName, String tableId, String paramTableTypeIid) {
		logger.debug("tableName=" + tableName + "  tableId=" + tableId);
		Vector<Vector<String>> parmeters = new Vector<Vector<String>>();
		if ((tableName != null && !"".equals(tableName)) && (tableId != null && !"".equals(tableId))) {
			DBConnUtil conn = null;
			try {
				conn= new DBConnUtil();
				String sql = "SELECT * FROM "+tableName.toUpperCase()+" T WHERE T.PARAMETERTABLETYPEID='"+tableId+"'";
				ResultSet resultSet = conn.executeQuery(sql);
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();
				List<GWParameterTableColumn> columns = GWParameterTableTypeManager.queryGWParameterTableColumns(paramTableTypeIid);
				Vector<String> vector = null;
				while(resultSet.next()) {
					vector = new Vector<String>();
					for (int i = 0; i < columnCount; i++) {
						if (columns.size() > i) {
							String dataType = columns.get(i).getDatatype();
							logger.debug("column=" + i + "  dataType=" + dataType);
							String value = "";
							if (ParameterConstants.TABLE_COLUMN_DATATYPE_INTEGER.equals(dataType)) {
								value = String.valueOf(resultSet.getInt(i+1));
							} else if (ParameterConstants.TABLE_COLUMN_DATATYPE_DECIMAL.equals(dataType)) {
								value = String.valueOf(resultSet.getFloat(i+1));
							} else {
								value = resultSet.getString(i+1);
							}

							if (value != null && !"".equals(value)) {
								value = value.replaceAll(ParameterConstants.REPLACE_SINGLEQUOTE, "'");
							}
							vector.add(value);
						} else {
							logger.error("GWParameterTableColumn index [" + i + "] is not exsit!");
						}
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
	protected static Vector<Vector<Object>> queryParamsByTableId(String tableName, String tableId, String technicsNumber, String objNumber, String objType, String columnEnames, String bsoID, String version) {
		Vector<Vector<Object>> parmeters = new Vector<Vector<Object>>();
//		boolean nextID = true;
		if (tableName != null && !"".equals(tableName)) {
			DBConnUtil conn = null;
			try {
				ResultSet resultSet = null;
				conn= new DBConnUtil();
				String sql = "";
				sql = "SELECT " + columnEnames + " FROM " + tableName.toUpperCase() +
						" T WHERE T.BSOID='" + bsoID +
						"' and T.TECHNICSNUMBER='" + technicsNumber +
						"' and T.VERSION='" + version +
						"' ORDER BY SEQUENCE";
//				ResultSet resultSet1 = conn.executeQuery(sql);
//				while(resultSet1.next()){
//					nextID = false;
//					break;
//				}
//				if(nextID){
//					sql = "SELECT " + columnEnames + " FROM " + tableName.toUpperCase() +
//							" T WHERE T.TECHNICSNUMBER='" + technicsNumber +
//							"' and T.OBJNUMBER='" + objNumber +
//							"' and T.OBJTYPE='" + objType + "' ORDER BY SEQUENCE";
//				}
//				conn = new DBConnUtil();
				resultSet = conn.executeQuery(sql);
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();
				List<GWParameterTableColumn> columns = GWParameterTableTypeManager.queryGWParameterTableColumns(tableId);
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

	protected static Vector<Vector<Object>> queryParamsByTableId2(String tableName, String tableId, String technicsNumber, String version, String newVersion) {
		Vector<Vector<Object>> parmeters = new Vector<Vector<Object>>();
//		boolean nextID = true;
		if (tableName != null && !"".equals(tableName)) {
			DBConnUtil conn = null;
			try {
				ResultSet resultSet = null;
				conn= new DBConnUtil();
				String sql = "";
				String names = "";
				List<GWParameterTableColumn> columns = GWParameterTableTypeManager.queryGWParameterTableColumns(tableId);
				for(GWParameterTableColumn column : columns){
					if("".equals(names)){
						names = column.getName();
					}else{
						names = names + ","+column.getName();
					}
				}
				sql = "SELECT " + names + " FROM " + tableName.toUpperCase() +
						" T WHERE T.TECHNICSNUMBER='" + technicsNumber +
						"' and T.VERSION='" + version +
						"' ORDER BY SEQUENCE";
				resultSet = conn.executeQuery(sql);
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();
				Vector<Object> vector = null;
				while(resultSet.next()) {
					vector = new Vector<Object>();
					for (int i = 0; i < columnCount; i++) {
						String name = columns.get(i).getName();
						String dataType = columns.get(i).getDatatype();
						String value = "";
						if (ParameterConstants.TABLE_COLUMN_DATATYPE_INTEGER.equals(dataType)) {
							value = String.valueOf(resultSet.getInt(i+1));
						} else if (ParameterConstants.TABLE_COLUMN_DATATYPE_DECIMAL.equals(dataType)) {
							value = String.valueOf(resultSet.getFloat(i+1));
						} else {
							value = CommonUtil.objectToString(resultSet.getString(i+1));
						}
						if(name.equalsIgnoreCase("GWKEY")){
							value = String.valueOf(System.nanoTime());
						}
						if(name.equalsIgnoreCase("VERSION")){
							value = newVersion;
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
	 * 创建参数表格类型主对象
	 *
	 * @param paramTableType
	 * @return
	 */
	private static GWParamTableTypeMaster createParamTableTypeMaster(CmParamTableType paramTableType) {
		GWParamTableTypeMaster master = null;
		if (paramTableType != null) {
			master = new GWParamTableTypeMaster();
			master.setName(paramTableType.getEnName());
			master.setChinaname(paramTableType.getName());

			try {
				GwPersistenceHelper.manager.save(master);

				master = queryParamTableTypeMaster(paramTableType.getEnName());
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return master;
	}

	/**
	 * 新增参数表格类型
	 *
	 * @param paramTableType
	 * @return
	 */
	protected static CmParamTableType createParamTableType(CmParamTableType paramTableType) {
		if (paramTableType != null) {
			//判断数据库是否存在该数据库表
			if (checkTableIsExsit(paramTableType.getEnName())) {
				logger.error("数据库中已经存在该数据库表："+paramTableType.getName());
				return null;
			}

			//先创建主对象
			GWParamTableTypeMaster master = createParamTableTypeMaster(paramTableType);
			if (master != null) {
				//更新缓存对象的主对象ID
				paramTableType.setTableTypeMasterId(master.getGwKey());

				//创建参数表格类型
				GWParameterTableType gwParameterTableType = new GWParameterTableType();
				gwParameterTableType.setTabletypemasterid(paramTableType.getTableTypeMasterId());
				gwParameterTableType.setTechnicstype(paramTableType.getTechnicsType());
				gwParameterTableType.setIsUsed("启用");
				gwParameterTableType.setVersion("1");//版本默认从1开始

				try {
					//保存至数据库
					GwPersistenceHelper.manager.save(gwParameterTableType);

					//初始化表格的列对象
					initParameterTableColumn(gwParameterTableType);
					//初始创建该参数表的数据库表
					firstCrateTable(paramTableType.getEnName());

					firstCrateMesTable(paramTableType.getEnName());

					firstCrateRemoteMesTable(paramTableType.getEnName());

					//返回新建的参数表格类型
					gwParameterTableType = getLatestTableType(paramTableType.getTableTypeMasterId());

					//更新缓存对象的OID
					paramTableType.setOid(Long.valueOf(gwParameterTableType.getGwKey()));
					paramTableType.setVersion(gwParameterTableType.getVersion());

					//获取参数表的列数据
					List<CmParameterTableColumn> tableColumns = queryCmParameterTableColumns(gwParameterTableType);
					paramTableType.setTableColumns(tableColumns);
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
		return paramTableType;
	}

	private static void firstCrateRemoteMesTable(String tableName) {
		String columnSql = "";
		String[] columns = null;
		for (int i = 0; i < ParameterConstants.DEFAULTMESCOLUMNS.length; i++) {
			columns = ParameterConstants.DEFAULTMESCOLUMNS[i];
			if (i == 0) {
				columnSql = columns[0] +" VARCHAR2(100 BYTE)";
			} else {
				if(columns[0].equals("SEQUENCE")){
					columnSql = columnSql +", "+ columns[0] +" NUMBER";
				}else{
					columnSql = columnSql +", "+ columns[0] +" VARCHAR2(100 BYTE)";
				}
			}
		}

		String sql = "CREATE TABLE " + "gl_mes" + tableName + " ( " + columnSql  + " )";

		logger.debug("==sql==" + sql);
		MESDBUtil conn = null;
		try {
			conn= MESDBUtil.getDBUtil("03");
			conn.update(sql, new Object[]{});
		} catch (Exception e) {
			logger.error(e);
		} finally {
		}

	}

	/**
	 * 新建属性列对象
	 *
	 * @param tableColumn
	 * @return
	 */
	protected static CmParameterTableColumn createParamTableColumn(CmParameterTableColumn tableColumn) {
		try {
			GWParameterTableColumn gwParameterTableColumn = convertToGwParameterTableColumn(tableColumn);
			GwPersistenceHelper.manager.save(gwParameterTableColumn);

			gwParameterTableColumn = queryParameterTableColumn(tableColumn.getParametertabletypeid(), tableColumn.getName());
		} catch (Exception e) {
			logger.error(e);
		}

		return tableColumn;
	}

	/**
	 * 保存修改后的检验记录表
	 *
	 * @param paramTableType
	 * @return
	 */
	protected static CmParamTableType saveParamTableType(CmParamTableType paramTableType) {
		try {
			if (paramTableType != null) {
				//从系统查询出当前最新版的参数表格对象
				GWParameterTableType gwParameterTableType = getLatestTableType(paramTableType.getTableTypeMasterId());
				if (gwParameterTableType != null) {
					//保存参数表名称
					saveGWParamTableMaster(paramTableType);

					//创建新版的参数表格对象
					GWParameterTableType newParameterTableType = new GWParameterTableType();
					newParameterTableType.setTabletypemasterid(paramTableType.getTableTypeMasterId());
					newParameterTableType.setTechnicstype(paramTableType.getTechnicsType());
					newParameterTableType.setIsUsed(paramTableType.getIsUsed());

					//版本自动加1
					String version = String.valueOf(Long.valueOf(gwParameterTableType.getVersion()) +1);
					newParameterTableType.setVersion(version);

					//保存新版的参数表格对象至数据库
					GwPersistenceHelper.manager.save(newParameterTableType);

					//返回新版的参数表格对象
					newParameterTableType = getLatestTableType(paramTableType.getTableTypeMasterId());

					//保存修改后的定义的列信息
					saveParamTableColumns(newParameterTableType, paramTableType.getTableColumns());

					//修改数据库表结构
					updateTable(paramTableType.getEnName(), newParameterTableType, gwParameterTableType);
					//修改数据库Mes表结构
					updateMesTable(paramTableType.getEnName(), newParameterTableType, gwParameterTableType);

					updateRemoteMesTable(paramTableType.getEnName(), newParameterTableType, gwParameterTableType);

					//更新缓存对象的OID和版本
					paramTableType.setOid(Long.valueOf(newParameterTableType.getGwKey()));
					paramTableType.setVersion(newParameterTableType.getVersion());
				}
			}
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
		return paramTableType;
	}

	private static void updateRemoteMesTable(String tableName, GWParameterTableType newTableType, GWParameterTableType oldTableType) throws Exception{
		List<GWParameterTableColumn> newTableColumns = getAddColumns(newTableType);
		if (newTableColumns != null && !newTableColumns.isEmpty()) {
			MESDBUtil conn = null;
			for (GWParameterTableColumn newColumn : newTableColumns) {
				String name = newColumn.getName();
				String dataType = newColumn.getDatatype();
				String maxLong = newColumn.getMaxlong();

				String columnType = getTableColumnType(dataType, maxLong);

				String selectSql = "SELECT COUNT('" + name + "') FROM cols WHERE TABLE_NAME =UPPER('" + "gl_mes" + tableName + "') AND column_name =UPPER('" + name + "')";
				System.out.println("selectSql=====>>>>>>" + selectSql);
				try {
					conn= MESDBUtil.getDBUtil("03");
					int i = (int) conn.getCount(selectSql, new Object[]{});
					String sql = "";
						if (i != 0) {
							sql = "alter table " + "gl_mes" + tableName + " modify (" + name + " " + columnType + ")";
						} else {
							sql = "alter table "+ "gl_mes" + tableName+ " add "+ name +" " +columnType;
						}
						conn.update(sql, new Object[]{});
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("数据库表修改失败！");
				}
			}
		}

	}

	private static void saveGWParamTableMaster(CmParamTableType paramTableType) throws Exception {
		GWParamTableTypeMaster master = queryParamTableTypeMasterById(paramTableType.getTableTypeMasterId());
		master.setChinaname(paramTableType.getName());
		GwPersistenceHelper.manager.save(master);
	}

	/**
	 * 保存参数表定义的列数据信息至数据库
	 *
	 * @param paramTableType
	 * @param tableColumns
	 * @throws Exception
	 */
	private static void saveParamTableColumns(GWParameterTableType paramTableType, List<CmParameterTableColumn> tableColumns) throws Exception {
		if (tableColumns != null && !tableColumns.isEmpty()) {
			GWParameterTableColumn column = null;
			for (CmParameterTableColumn cmParameterTableColumn : tableColumns) {
				column = convertToGwParameterTableColumn(cmParameterTableColumn);
				column.setParametertabletypeid(paramTableType.getGwKey());

				GwPersistenceHelper.manager.save(column);
			}
		}
	}

	/**
	 * 新增参数表类型时创建该参数表默认的列数据
	 *
	 * @param parameterTableType
	 * @throws Exception
	 */
	private static void initParameterTableColumn(GWParameterTableType parameterTableType) throws Exception {
		String tableTypeId = parameterTableType.getGwKey();

		GWParameterTableColumn column = null;
		String[] columns = null;
		for (int i = 0; i < ParameterConstants.DEFAULTCOLUMNS.length; i++) {
			columns = ParameterConstants.DEFAULTCOLUMNS[i];
			column = getGwParameterTableColumn(tableTypeId, columns[0], columns[1], ParameterConstants.TABLE_COLUMN_DATATYPE_CHAR);
			GwPersistenceHelper.manager.save(column);
		}
	}

	/**
	 * 新增参数表时首次创建该参数表的数据库表，默认创建系统必须的列。
	 *
	 * @param tableName
	 */
	private static void firstCrateTable(String tableName) {
		String columnSql = "";
		String[] columns = null;
		for (int i = 0; i < ParameterConstants.DEFAULTCOLUMNS.length; i++) {
			columns = ParameterConstants.DEFAULTCOLUMNS[i];
			if (i == 0) {
				columnSql = columns[0] +" VARCHAR2(100 BYTE)";
			} else {
				if(columns[0].equals("SEQUENCE")){
					columnSql = columnSql +", "+ columns[0] +" NUMBER";
				}else{
					columnSql = columnSql +", "+ columns[0] +" VARCHAR2(100 BYTE)";
				}
			}
		}

		String sql = "CREATE TABLE " + tableName + " ( " + columnSql  + " )";

		logger.debug("==sql==" + sql);
		DBConnUtil conn = null;
		try {
			conn= new DBConnUtil();
			conn.executeUpdate(sql);
			conn.commit();
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

	/**
	 * 新增参数表时首次创建该参数表的数据库表，默认创建系统必须的列。
	 *
	 * @param tableName
	 */
	private static void firstCrateMesTable(String tableName) {
		String columnSql = "";
		String[] columns = null;
		for (int i = 0; i < ParameterConstants.DEFAULTMESCOLUMNS.length; i++) {
			columns = ParameterConstants.DEFAULTMESCOLUMNS[i];
			if (i == 0) {
				columnSql = columns[0] +" VARCHAR2(100 BYTE)";
			} else {
				if(columns[0].equals("SEQUENCE")){
					columnSql = columnSql +", "+ columns[0] +" NUMBER";
				}else{
					columnSql = columnSql +", "+ columns[0] +" VARCHAR2(100 BYTE)";
				}
			}
		}

		String sql = "CREATE TABLE " + "mes" + tableName + " ( " + columnSql  + " )";

		logger.debug("==sql==" + sql);
		DBConnUtil conn = null;
		try {
			conn= new DBConnUtil();
			conn.executeUpdate(sql);
			conn.commit();
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

	/**
	 * 修改数据库表结构，增加列。
	 *
	 * @param tableName
	 * @param newTableType
	 * @param oldTableType
	 * @throws Exception
	 */
	private static void updateTable(String tableName, GWParameterTableType newTableType, GWParameterTableType oldTableType) throws Exception {
		List<GWParameterTableColumn> newTableColumns = getAddColumns(newTableType);
		if (newTableColumns != null && !newTableColumns.isEmpty()) {
			DBConnUtil conn = null;
			for (GWParameterTableColumn newColumn : newTableColumns) {
				String name = newColumn.getName();
				String dataType = newColumn.getDatatype();
				String maxLong = newColumn.getMaxlong();

				String columnType = getTableColumnType(dataType, maxLong);

				String selectSql = "SELECT COUNT('" + name + "') FROM cols WHERE TABLE_NAME =UPPER('" + tableName + "') AND column_name =UPPER('" + name + "')";
				System.out.println("selectSql=====>>>>>>" + selectSql);
				try {
					conn= new DBConnUtil();
					ResultSet rs = conn.executeQuery(selectSql);
					String sql = "";
					if (rs.next()) {
						int i = rs.getInt(1);
						if (i != 0) {
							sql = "alter table " + tableName + " modify (" + name + " " + columnType + ")";
						} else {
							sql = "alter table "+tableName+" add "+name+" " +columnType;
						}
					}
					conn.executeUpdate(sql);
					conn.commit();
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("数据库表修改失败！");
				} finally {
					try {
						if (conn != null) {
							conn.close();
						}
					} catch (SQLException e) {
						throw new Exception("数据库表修改失败！");
					}
				}
			}
		}
	}

	/**
	 * 修改数据库表结构，增加列。
	 *
	 * @param tableName
	 * @param newTableType
	 * @param oldTableType
	 * @throws Exception
	 */
	private static void updateMesTable(String tableName, GWParameterTableType newTableType, GWParameterTableType oldTableType) throws Exception {
		List<GWParameterTableColumn> newTableColumns = getAddColumns(newTableType);
		if (newTableColumns != null && !newTableColumns.isEmpty()) {
			DBConnUtil conn = null;
			for (GWParameterTableColumn newColumn : newTableColumns) {
				String name = newColumn.getName();
				String dataType = newColumn.getDatatype();
				String maxLong = newColumn.getMaxlong();

				String columnType = getTableColumnType(dataType, maxLong);

				String selectSql = "SELECT COUNT('" + name + "') FROM cols WHERE TABLE_NAME =UPPER('" + "mes" + tableName + "') AND column_name =UPPER('" + name + "')";
				System.out.println("selectSql=====>>>>>>" + selectSql);
				try {
					conn= new DBConnUtil();
					ResultSet rs = conn.executeQuery(selectSql);
					String sql = "";
					if (rs.next()) {
						int i = rs.getInt(1);
						if (i != 0) {
							sql = "alter table " + "mes" + tableName + " modify (" + name + " " + columnType + ")";
						} else {
							sql = "alter table "+ "mes" +tableName+" add "+name+" " +columnType;
						}
					}
					conn.executeUpdate(sql);
					conn.commit();
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("数据库表修改失败！");
				} finally {
					try {
						if (conn != null) {
							conn.close();
						}
					} catch (SQLException e) {
						throw new Exception("数据库表修改失败！");
					}
				}
			}
		}
	}

	private static String getTableColumnType(String dataType, String maxLong) {
		String columnType = "VARCHAR2";
		if (ParameterConstants.TABLE_COLUMN_DATATYPE_BLOB.equals(dataType)) {
			columnType = "VARCHAR2(500)";
		} else if (ParameterConstants.TABLE_COLUMN_DATATYPE_INTEGER.equals(dataType)) {
			columnType = "VARCHAR2(200)";
		} else if (ParameterConstants.TABLE_COLUMN_DATATYPE_CHAR.equals(dataType)) {
			if (maxLong != null && !"".equals(maxLong)) {
				columnType = columnType +"("+maxLong+")";
			} else {
				columnType = "VARCHAR2(200)";
			}
		} else if (ParameterConstants.TABLE_COLUMN_DATATYPE_DECIMAL.equals(dataType)) {
			columnType = "VARCHAR2(200)";
		} else if (ParameterConstants.TABLE_COLUMN_DATATYPE_BOOLEAN.equals(dataType)){
			columnType = "VARCHAR2(200)";
		}else if (ParameterConstants.TABLE_COLUMN_DATATYPE_PICTURE.equals(dataType)){
			columnType = "VARCHAR2(200)";
		}
		return columnType;
	}

	/**
	 * 计算得到新增的列
	 *
	 * @param newTableType
	 * @param oldTableType
	 * @return
	 */
	private static List<GWParameterTableColumn> getAddColumns(GWParameterTableType newTableType) {
		List<GWParameterTableColumn> newAddColumns = new ArrayList<GWParameterTableColumn>();
		try {
			List<GWParameterTableColumn> newTableColumns = queryGWParameterTableColumns(newTableType);
			if (newTableColumns != null) {
				for (GWParameterTableColumn newColumn : newTableColumns) {
					boolean flag = false;
					for (String defaultColumnName : ParameterConstants.DEFAULTNOTSHOWCOLUMNS) {
						if (defaultColumnName.equals(newColumn.getName())) {
							flag = true;
							break;
						}
					}

					if (!flag) {
						newAddColumns.add(newColumn);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return newAddColumns;
	}

	/**
	 * 实例化参数表时创建默认的列
	 *
	 * @param orderno
	 * @param tableTypeId
	 * @param name
	 * @param chinaName
	 * @param dataType
	 * @return
	 */
	private static GWParameterTableColumn getGwParameterTableColumn(String tableTypeId, String name, String chinaName, String dataType) {
		GWParameterTableColumn column = new GWParameterTableColumn();
		column.setParametertabletypeid(tableTypeId);
		column.setName(name);
		column.setChinaname(chinaName);
		column.setDatatype(dataType);
		return column;
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
	 * 查询指定参数表定义的列信息
	 *
	 * @param parameterTableType
	 * @return
	 * @throws Exception
	 */
	private static List<GWParameterTableColumn> queryGWParameterTableColumns(GWParameterTableType parameterTableType) throws Exception {
		GwQuerySpec qs = new GwQuerySpec(GWParameterTableColumn.class);
		qs.appendWhere(GWParameterTableColumn.PARAMETERTABLETYPEID, GwQuerySpec.EQUAL, parameterTableType.getGwKey());
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

	private static List<CmParameterTableColumn> queryCmParameterTableColumns(GWParameterTableType parameterTableType) throws Exception {
		List<GWParameterTableColumn> tableColumns = queryGWParameterTableColumns(parameterTableType);

		List<CmParameterTableColumn> list = new ArrayList<CmParameterTableColumn>();
		if (tableColumns != null && !tableColumns.isEmpty()) {
			for (GWParameterTableColumn gwParameterTableColumn : tableColumns) {
				list.add(convertToCmParamTableColumn(gwParameterTableColumn));
			}
		}

		return list;
	}

	/**
	 * 通过参数表ID和属性英文名查询属性列对象
	 *
	 * @param paramTableTypeId
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected static GWParameterTableColumn queryParameterTableColumn(String paramTableTypeId, String name) throws Exception {
		GwQuerySpec qs = new GwQuerySpec(GWParameterTableColumn.class);
		qs.appendWhere(GWParameterTableColumn.PARAMETERTABLETYPEID, GwQuerySpec.EQUAL, paramTableTypeId);
		qs.appendAnd();
		qs.appendWhere(GWParameterTableColumn.NAME, GwQuerySpec.EQUAL, name);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		GWParameterTableColumn tableColumn = null;
		if (qr.hasNext()) {
			tableColumn = (GWParameterTableColumn) qr.next();
		}
		return tableColumn;
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

	public static List<GWParamTableTypeMaster> queryAllParamTableTypeMasters() {
		List<GWParamTableTypeMaster> list = new ArrayList<GWParamTableTypeMaster>();

		try {
			GwQuerySpec qs = new GwQuerySpec(GWParamTableTypeMaster.class);
			GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
			GWParamTableTypeMaster master = null;
			if (qr.hasNext()) {
				master = (GWParamTableTypeMaster) qr.next();
				list.add(master);
			}
		} catch (Exception e) {
			logger.error(e);
		}

		return list;
	}

	public static List<GWParamTableTypeMaster> queryParamTableTypeMaster(Map<String, String> map) throws Exception {
		List<GWParamTableTypeMaster> list = new ArrayList<GWParamTableTypeMaster>();

		GwQuerySpec qs = new GwQuerySpec(GWParamTableTypeMaster.class);

		String name = map.get(ParameterConstants.TABLE_COLUMN_NAME);
		String chinaName = map.get(ParameterConstants.TABLE_COLUMN_CHINANAME);
		if (name != null && !"".equals(name)) {
			qs.appendWhere(GWParamTableTypeMaster.NAME, GwQuerySpec.LIKE, name+"%");
			if (chinaName != null && !"".equals(chinaName)) {
				qs.appendAnd();
				qs.appendWhere(GWParamTableTypeMaster.CHINANAME, GwQuerySpec.LIKE, chinaName+"%");
			}
		} else {
			if (chinaName != null && !"".equals(chinaName)) {
				qs.appendWhere(GWParamTableTypeMaster.CHINANAME, GwQuerySpec.LIKE, chinaName+"%");
			}
		}

		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		GWParamTableTypeMaster master = null;
		while (qr.hasNext()) {
			master = (GWParamTableTypeMaster) qr.next();
			list.add(master);
		}

		return list;
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

	/**
	 * 通过参数表ID获取参数表的表结构信息
	 *
	 * @param gwkey
	 * @return
	 */
	protected static CmParamTableType queryCmParameterTableType(String gwkey) {
		CmParamTableType cmParamTableType = null;
		try {
			GwQuerySpec qs = new GwQuerySpec(GWParameterTableType.class);
			qs.appendWhere(GWParameterTableType.KEY_ID, GwQuerySpec.EQUAL, gwkey);
			GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
			if (qr.hasNext()) {
				GWParameterTableType parameterTableType = (GWParameterTableType)qr.next();
				GWParamTableTypeMaster master = queryParamTableTypeMasterById(parameterTableType.getTabletypemasterid());

				cmParamTableType = convertToCmParamTableType(parameterTableType);
				cmParamTableType.setName(master.getName());
//				cmParamTableType.setChinaName(master.getChinaname());
				cmParamTableType.setTableTypeMasterId(master.getGwKey());

				//加载参数表定义的列信息
				List<CmParameterTableColumn> tableColumns = queryCmParameterTableColumns(parameterTableType);
				cmParamTableType.setTableColumns(tableColumns);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return cmParamTableType;
	}

	/**
	 * 判断指定名称的数据库表是否存在
	 *
	 * @param tableName
	 * @return
	 */
	protected static boolean checkTableIsExsit(String tableName) {
		if (tableName != null && !"".equals(tableName)) {
			String sql = "select count(*) from all_tables where table_name='"+tableName.toUpperCase()+"'";

			logger.debug("==sql=="+sql);
			DBConnUtil conn = null;
			try {
				conn= new DBConnUtil();
				ResultSet rt = conn.executeQuery(sql);
				if(rt.next()) {
					int count  = rt.getInt(1);
					if (count == 1) {
						return true;
					}
				}
				conn.commit();
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
		return false;
	}

	public static String queryMaxEnname(){
		String maxEnname = "";
		String sql = "select max(ENNAME) from GWPARAMETERTYPE";
		logger.debug("sql==========" + sql);
		DBConnUtil conn = null;
		try {
			conn= new DBConnUtil();
			ResultSet rt = conn.executeQuery(sql);
			if(rt.next()) {
				maxEnname = rt.getString(1);
			}
		} catch (Exception e) {
			logger.error(e);
		}finally{
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
		return maxEnname;
	}

	public static String queryMaxNameFromTypeMaster(){
		String maxName = "";
		String sql = "select max(NAME) from GWPARAMTABLETYPEMASTER";
		logger.debug("sql==========" + sql);
		DBConnUtil conn = null;
		try {
			conn= new DBConnUtil();
			ResultSet rt = conn.executeQuery(sql);
			if(rt.next()) {
				maxName = rt.getString(1);
			}
		} catch (Exception e) {
			logger.error(e);
		}finally{
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
		}
		return maxName;
	}


	public static CmParamTableType convertToCmParamTableType(GWParameterTableType gwParameterTableType) throws Exception {
		CmParamTableType paramTableType = new CmParamTableType();
		paramTableType.setTechnicsType(gwParameterTableType.getTechnicstype());
		paramTableType.setIsUsed(gwParameterTableType.getIsUsed());
		paramTableType.setVersion(gwParameterTableType.getVersion());
		paramTableType.setOid(Long.valueOf(gwParameterTableType.getGwKey()));

		GWParamTableTypeMaster master = queryParamTableTypeMasterById(gwParameterTableType.getTabletypemasterid());
		paramTableType.setEnName(master.getName());
		paramTableType.setName(master.getChinaname());
		paramTableType.setTableTypeMasterId(master.getGwKey());

		List<CmParameterTableColumn> tableColumns = queryCmParameterTableColumns(gwParameterTableType);
		paramTableType.setTableColumns(tableColumns);

		return paramTableType;
	}

	public static CmParameterTableColumn convertToCmParamTableColumn(GWParameterTableColumn gwParameterTableColumn) {
		CmParameterTableColumn tableColumn = new CmParameterTableColumn();

		tableColumn.setOid(Long.valueOf(gwParameterTableColumn.getGwKey()));
		tableColumn.setOrderno(gwParameterTableColumn.getOrderno());
		tableColumn.setParametertabletypeid(gwParameterTableColumn.getParametertabletypeid());
		tableColumn.setName(gwParameterTableColumn.getChinaname());
		tableColumn.setEnName(gwParameterTableColumn.getName());
		tableColumn.setDatatype(gwParameterTableColumn.getDatatype());
		tableColumn.setMaxlong(gwParameterTableColumn.getMaxlong());
		tableColumn.setIsfromparam(gwParameterTableColumn.getIsfromparam());
		tableColumn.setIsrecord(gwParameterTableColumn.getIsrecord());
		tableColumn.setValueRange(gwParameterTableColumn.getValuerange());
		tableColumn.setStatus(gwParameterTableColumn.getStatus());
		tableColumn.setShow(ParameterConstants.isShow(gwParameterTableColumn.getName()));
		tableColumn.setEditable(ParameterConstants.isEditable(gwParameterTableColumn.getName()));
		tableColumn.setVisiless(gwParameterTableColumn.getVisiless());
		tableColumn.setVisilessInMes(gwParameterTableColumn.getVisilessinmes());

		return tableColumn;
	}

	/**
	 * 从CmParameterTableColumn转化为GWParameterTableColumn，其中不含GWKEY值。
	 *
	 * @param cmParameterTableColumn
	 * @return
	 */
	private static GWParameterTableColumn convertToGwParameterTableColumn(CmParameterTableColumn cmParameterTableColumn) {
		GWParameterTableColumn gwParameterTableColumn = new GWParameterTableColumn();

		gwParameterTableColumn.setOrderno(cmParameterTableColumn.getOrderno());
		gwParameterTableColumn.setParametertabletypeid(cmParameterTableColumn.getParametertabletypeid());
		gwParameterTableColumn.setName(cmParameterTableColumn.getEnName());
		gwParameterTableColumn.setChinaname(cmParameterTableColumn.getName());
		gwParameterTableColumn.setMaxlong(cmParameterTableColumn.getMaxlong());
		gwParameterTableColumn.setDatatype(cmParameterTableColumn.getDatatype());
		gwParameterTableColumn.setIsfromparam(cmParameterTableColumn.getIsfromparam());
		gwParameterTableColumn.setIsrecord(cmParameterTableColumn.getIsrecord());
		gwParameterTableColumn.setValuerange(cmParameterTableColumn.getValueRange());
		gwParameterTableColumn.setStatus(cmParameterTableColumn.getStatus());
		gwParameterTableColumn.setVisiless(cmParameterTableColumn.getVisiless());
		gwParameterTableColumn.setVisiless(cmParameterTableColumn.getVisiless());
		gwParameterTableColumn.setVisilessinmes(cmParameterTableColumn.getVisilessInMes());

		return gwParameterTableColumn;
	}

	protected static List<CmTechnicsType> loadParamTableType() throws Exception {
		List<CmTechnicsType> list = new ArrayList<CmTechnicsType>();
		String[] technicsTypes = LoadConfig.getInstance().getTechnicsType()[1];
		for (String technicsType : technicsTypes) {
			CmTechnicsType cmTechnicsType = new CmTechnicsType();
			cmTechnicsType.setName(technicsType);
			cmTechnicsType.setEnName(technicsType);

			List<CmParamTableType> paramTableTypes = queryParamTableTypeByTechnicsType(technicsType);
			cmTechnicsType.setParamTableTypes(paramTableTypes);
			list.add(cmTechnicsType);
		}
		return list;
	}
	protected static CmTechnicsType loadSimpleParamTableType(String technicsType) throws Exception {
			CmTechnicsType cmTechnicsType = new CmTechnicsType();
			cmTechnicsType.setName(technicsType);
			cmTechnicsType.setEnName(technicsType);

			List<CmParamTableType> paramTableTypes = queryParamTableTypeByTechnicsType(technicsType);
			cmTechnicsType.setParamTableTypes(paramTableTypes);
		return cmTechnicsType;
	}

	protected static List<CmParamTableType> queryParamTableTypeByTechnicsType(String technicsType) throws Exception {
		GwQuerySpec qs = new GwQuerySpec(GWParameterTableType.class);
		qs.appendWhere(GWParameterTableType.TECHNICSTYPE, GwQuerySpec.EQUAL, technicsType);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		CmParamTableType paramTableType = null;
		GWParameterTableType gwParameterTableType = null;
		List<CmParamTableType> list = new ArrayList<CmParamTableType>();
		List<String> masterOidlist = new ArrayList<String>();
		while (qr.hasNext()) {
			gwParameterTableType = (GWParameterTableType) qr.next();
			if (masterOidlist.contains(gwParameterTableType.getTabletypemasterid())) {
				continue;
			}
			masterOidlist.add(gwParameterTableType.getTabletypemasterid());
			gwParameterTableType = getLatestTableType(gwParameterTableType.getTabletypemasterid());
			paramTableType = convertToCmParamTableType(gwParameterTableType);
			list.add(paramTableType);
		}

		return list;
	}

	public static CmParamTableType getCommonParamTableType(CmParamTableType paramTableType) {
		try {
			GWParamTableTypeMaster master = queryParamTableTypeMaster(paramTableType.getEnName());
			if (master == null) {
				paramTableType = createParamTableType(paramTableType);
			} else {
				GWParameterTableType gwParameterTableType = getLatestTableType(master.getGwKey());
				paramTableType = convertToCmParamTableType(gwParameterTableType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paramTableType;
	}

	public static CmParamTableType getCommonParamTableType(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) {
		return ParameterProcessor.getCommonParamTableType(technicsNumber, objType, objNumber, isApproved, bsoID,version);
	}
	public static CmParamTableType getCommonParamTableTypeForMes(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) {
		return ParameterProcessor.getCommonParamTableTypeForMes(technicsNumber, objType, objNumber, isApproved, bsoID, version);
	}
}

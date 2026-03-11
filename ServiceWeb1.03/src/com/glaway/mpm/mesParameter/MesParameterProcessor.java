package com.glaway.mpm.mesParameter;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.dom4j.DocumentException;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.pom.Transaction;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

import com.glaway.mpm.constants.XMLConstants;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.mesParameter.model.Bdpsndoc;
import com.glaway.mpm.mesParameter.model.MesBfcccpbh;
import com.glaway.mpm.mesParameter.model.MesParamTableModel;
import com.glaway.mpm.parameter.GWParameterTableTypeManager;
import com.glaway.mpm.parameter.constants.ParameterConstants;
import com.glaway.mpm.parameter.model.GWOperationToParamTableLink;
import com.glaway.mpm.parameter.model.GWParamTableTypeMaster;
import com.glaway.mpm.parameter.model.GWParameterTableColumn;
import com.glaway.mpm.parameter.model.GWParameterTableType;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.parameter.model.data.CmParameterTableColumn;
import com.glaway.mpm.parameter.service.gwpersistable.GwPersistenceHelper;
import com.glaway.mpm.parameter.service.gwpersistable.GwQueryResult;
import com.glaway.mpm.parameter.service.gwpersistable.GwQuerySpec;
import com.glaway.mpm.pbom.db.MESDBUtil;
import com.glaway.mpm.util.ApacheZipUtil;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.SWXMLUtil;
import com.glaway.mpm.util.WTDocumentUtil;

import ext.casc.integrate.process.ProcessService;
import ext.casc.util.WCUtil;

public class MesParameterProcessor {

	private static VaLogger logger = VaLogger.getLogger(MesParameterProcessor.class.getName());

	/**
	 * 保存工艺规程\工序\工步的参数至数据库中。保存时不执行更新操作，直接删除该对象已有参数，再重新写入新的参数。
	 *
	 * @param object
	 * @param paramTableTypes
	 */
	public static void saveMesParameters(CmParamTableType paramTableType, Map<String, String> paramsMap) {
		Transaction transaction = new Transaction();
		String processNumber = paramsMap.get("processNumber");
		String technicsNumber = paramsMap.get("technicsNumber");
		String objType = paramsMap.get("objType");
		String objNumber = paramsMap.get("objNumber");
		String lukahao = paramsMap.get("lukahao");
		String gxPK = paramsMap.get("gxPK");
		String isZF = paramsMap.get("isZF");
		String bsoId = paramsMap.get("bsoId");
		String version = paramsMap.get("version");
		if(version.contains(".")){
			version = version.substring(0, version.indexOf("."));
		}
		try {
			transaction.start();
			String tableId = String.valueOf(paramTableType.getOid());
			String tableName = paramTableType.getEnName();
			Vector<Vector<Object>> params = paramTableType.getParameters();
			//更新历史数据
			updateHistoryTableParams(tableName,processNumber, technicsNumber, objType, objNumber, lukahao, gxPK, isZF,bsoId,version);
			updateHistoryRemoteTableParams(tableName, processNumber, technicsNumber, objType, objNumber, lukahao, gxPK, isZF,bsoId,version);
			//删除mes本地数据库数据
			deleteTableParams(tableName, processNumber, technicsNumber, objType, objNumber, lukahao, gxPK, isZF,bsoId,version);
			//删除mes端数据库数据
			deleteRemoteTableParams(tableName, processNumber, technicsNumber, objType, objNumber, lukahao, gxPK, isZF,bsoId,version);

			//将参数写入数据库表中
			if (params != null && !params.isEmpty()) {
				insertTableParams(processNumber, lukahao, gxPK, isZF, tableId, tableName, params,bsoId,version);
				insertRemoteTableParams(processNumber, lukahao, gxPK, isZF, tableId, tableName, params,bsoId,version);
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error(e);
		} finally {
			transaction = null;
		}
	}
	public static boolean isMesDataExist(CmParamTableType paramTableType, Map<String, String> paramsMap){
		Transaction transaction = new Transaction();
		DBConnUtil conn = null;
		try {
			String productNumber = paramsMap.get("processNumber");
			String technicsNumber = paramsMap.get("technicsNumber");
			String objType = paramsMap.get("objType");
			String objNumber = paramsMap.get("objNumber");
			String lukahao = paramsMap.get("lukahao");
			String gxPK = paramsMap.get("gxPK");
			String isZF = paramsMap.get("isZF");
			String bsoId = paramsMap.get("bsoId");
			String version = paramsMap.get("version");
			if(version.contains(".")){
				version = version.substring(0, version.indexOf("."));
			}
			transaction.start();
			String tableName = paramTableType.getEnName();
			if(tableName != null && !"".equals(tableName)){
				conn = new DBConnUtil();
//				String sql = "select count(*) from " + "mes" + tableName + " t where t.PRODUCTNUMBER='" + productNumber +
//						"' and t.TECHNICSNUMBER='" + technicsNumber +
			    String sql = "select count(*) from " + "mes" + tableName + " t where t.TECHNICSNUMBER='" + technicsNumber +
//						"' and t.OBJTYPE='" + objType +
						"' and t.LUKAHAO='" + lukahao +
						"' and t.PRODUCTNUMBER='" + productNumber +
//						"' and t.GXPK='" + gxPK +
//						"' and t.OBJNUMBER='" + objNumber +
						"' and t.bsoId='" + bsoId +
						"' and t.version='" + version +
						"' and t.ISZF='"+ isZF +"'";
				System.out.println("sql====" + sql);
				ResultSet rs = conn.executeQuery(sql);
				conn.commit();
				transaction.commit();
				if(rs.next()){
					int i = rs.getInt(1);
					if(i > 0){
						return true;
					}
				}else{
					return false;
				}

			}
		} catch (Exception e) {
			transaction.rollback();
			logger.error(e);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
			transaction = null;
		}
		return false;
	}

	public static boolean isDataPackageMesDataExist(CmParamTableType paramTableType, String productNumber, String technicNumber, String objType, String objNumber, String lukahao, String gxPK){
		Transaction transaction = new Transaction();
		DBConnUtil conn = null;
		try {
			transaction.start();
			String tableName = paramTableType.getEnName();
			if(tableName != null && !"".equals(tableName)){
				conn = new DBConnUtil();
				String sql = "select count(*) from " + "mes" + tableName + " t where t.PRODUCTNUMBER='" + productNumber +
						"' and t.TECHNICSNUMBER='" + technicNumber +"'";
				System.out.println("sql====" + sql);
				ResultSet rs = conn.executeQuery(sql);
				conn.commit();
				transaction.commit();
				if(rs.next()){
					int i = rs.getInt(1);
					if(i > 0){
						return true;
					}
				}else{
					return false;
				}

			}
		} catch (Exception e) {
			transaction.rollback();
			logger.error(e);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e);
			}
			transaction = null;
		}
		return false;
	}

	public static void updateHistoryTableParams(String tableName, String productNumber, String technicsNumber, String objType, String objNumber, String lukahao, String gxPK, String isZF,String bsoId,String version) {
		if (tableName != null && !"".equals(tableName)) {
			DBConnUtil conn = null;
			try {
				conn= new DBConnUtil();

				String sql = "select DISTINCT PRODUCTNUMBER from " + "mes" + tableName + " t where t.TECHNICSNUMBER='" + technicsNumber +
//						"' and t.OBJTYPE='" + objType +
						"' and t.LUKAHAO='" + lukahao +
						"' and t.BSOID='" + bsoId +
						"' and t.VERSION='" + version +
						"' and t.GXPK='" + gxPK +
//						"' and t.OBJNUMBER='" + objNumber +
						"' and t.ISZF='"+ isZF +"'";
				System.out.println("selectSql" + sql);
				ResultSet rs = conn.executeQuery(sql);
				while(rs.next()){
					String newProcessNumber = "";
					String processNumber = rs.getString("PRODUCTNUMBER");
					String[] processNumbers = processNumber.split(",");
					int i = 0;
					for (String str : processNumbers) {
						if (!productNumber.contains(str)) {
							if ("".equals(newProcessNumber)) {
								newProcessNumber = str;
							} else {
								newProcessNumber = newProcessNumber + "," + str;
							}
						} else {
							i++;
						}
					}
					if (i == processNumbers.length) {
						deleteTableParams(tableName, processNumber, technicsNumber, objType, objNumber, lukahao, gxPK, isZF,bsoId,version);
//						deleteRemoteTableParams(tableName, processNumber, technicsNumber, objType, objNumber, lukahao, gxPK, isZF);
					}
					if (!"".equals(newProcessNumber)) {

						String updateSql = "update mes" + tableName + " t set t.PRODUCTNUMBER ='" + newProcessNumber + "' where t.TECHNICSNUMBER='" + technicsNumber +
								"' and t.PRODUCTNUMBER='" + processNumber +
//								"' and t.OBJTYPE='" + objType +
								"' and t.LUKAHAO='" + lukahao +
								"' and t.BSOID='" + bsoId +
								"' and t.VERSION='" + version +
								"' and t.GXPK='" + gxPK +
//								"' and t.OBJNUMBER='" + objNumber +
								"' and t.ISZF='" + isZF + "'";
						updateTableParams(updateSql);
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
	}
	public static void updateHistoryRemoteTableParams(String tableName, String productNumber, String technicsNumber, String objType, String objNumber, String lukahao, String gxPK, String isZF,String bsoId,String version) {
		if (tableName != null && !"".equals(tableName)) {
			MESDBUtil conn = null;
			try {
				conn= MESDBUtil.getDBUtil("03");
				String sql = "select DISTINCT PRODUCTNUMBER from " + "gl_mes" + tableName + " t where t.TECHNICSNUMBER='" + technicsNumber +
//						"' and t.OBJTYPE='" + objType +
						"' and t.LUKAHAO='" + lukahao +
						"' and t.BSOID='" + bsoId +
						"' and t.VERSION='" + version +
						"' and t.GXPK='" + gxPK +
//						"' and t.OBJNUMBER='" + objNumber +
						"' and t.ISZF='"+ isZF +"'";
				System.out.println("updateHistory gl_mes===sql===>>>" + sql);
				List<MesParamTableModel> mesParamTableModels = conn.queryForList(sql, MesParamTableModel.class);
				for(MesParamTableModel mesParamTableModel : mesParamTableModels){
					String newProcessNumber = "";
					String processNumber = mesParamTableModel.getProductnumber();
					String[] processNumbers = processNumber.split(",");
					int i = 0;
					for(String str : processNumbers){
						if(!productNumber.contains(str)){
							if("".equals(newProcessNumber)){
								newProcessNumber = str;
							}else{
								newProcessNumber = newProcessNumber + "," + str;
							}
						}else{
							i++;
						}
					}
					if (i == processNumbers.length) {
//						deleteTableParams(tableName, processNumber, technicsNumber, objType, objNumber, lukahao, gxPK, isZF);
						deleteRemoteTableParams(tableName, processNumber, technicsNumber, objType, objNumber, lukahao, gxPK, isZF,bsoId,version);
					}
					if(!"".equals(newProcessNumber)){
						String updateSql = "update gl_mes" + tableName + " t set t.PRODUCTNUMBER ='"+ newProcessNumber +"' where t.TECHNICSNUMBER='" + technicsNumber +
								"' and t.PRODUCTNUMBER='" + processNumber +
//								"' and t.OBJTYPE='" + objType +
								"' and t.LUKAHAO='" + lukahao +
								"' and t.BSOID='" + bsoId +
								"' and t.VERSION='" + version +
								"' and t.GXPK='" + gxPK +
//								"' and t.OBJNUMBER='" + objNumber +
								"' and t.ISZF='"+ isZF +"'";
						updateRemoteTableParams(updateSql);
					}
				}

			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
	public static void deleteTableParams(String tableName, String productNumber, String technicsNumber, String objType, String objNumber, String lukahao, String gxPK, String isZF,String bsoId, String version){
		if (tableName != null && !"".equals(tableName)) {
			DBConnUtil conn = null;
			try {
				conn= new DBConnUtil();

				String sql = "delete from " + "mes" + tableName + " t where t.TECHNICSNUMBER='" + technicsNumber +
						"' and t.PRODUCTNUMBER='" + productNumber +
//						"' and t.OBJTYPE='" + objType +
						"' and t.LUKAHAO='" + lukahao +
						"' and t.BSOID='" + bsoId +
						"' and t.VERSION='" + version +
						"' and t.GXPK='" + gxPK +
//						"' and t.OBJNUMBER='" + objNumber +
						"' and t.ISZF='"+ isZF +"'";
				System.out.println("delete_table_sql====>>>>" + sql);
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
	}
	public static void deleteRemoteTableParams(String tableName, String productNumber, String technicsNumber, String objType, String objNumber, String lukahao, String gxPK, String isZF,String bsoId,String version) {
		if (tableName != null && !"".equals(tableName)) {
			MESDBUtil conn = null;
			try {
				conn= MESDBUtil.getDBUtil("03");

				String sql = "delete from " + "gl_mes" + tableName + " t where t.TECHNICSNUMBER='" + technicsNumber +
						"'and t.PRODUCTNUMBER='" + productNumber +
//						"' and t.OBJTYPE='" + objType +
						"' and t.LUKAHAO='" + lukahao +
						"' and t.BSOID='" + bsoId +
						"' and t.VERSION='" + version +
						"' and t.GXPK='" + gxPK +
//						"' and t.OBJNUMBER='" + objNumber +
						"' and t.ISZF='"+isZF+"'";
				System.out.println("delete gl_mes===sql===>>>" + sql);
				conn.update(sql, new Object[]{});

			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
	public static void updateTableParams(String updateSql){
			DBConnUtil conn = null;
			try {
				conn= new DBConnUtil();

				conn.executeUpdate(updateSql);

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
	public static void updateRemoteTableParams(String updateSql) {
			MESDBUtil conn = null;
			try {
				conn= MESDBUtil.getDBUtil("03");

				conn.update(updateSql, new Object[]{});

			} catch (Exception e) {
				logger.error(e);
			}
	}
	public static void insertTableParams(String productNumber, String lukahao, String gxPK, String isZF, String tableId, String tableName, Vector<Vector<Object>> params,String bsoId,String version) {
		if (tableName != null && !"".equals(tableName)) {
			DBConnUtil conn = null;
			try {
				conn= new DBConnUtil();

				List<GWParameterTableColumn> columns = MesParameterTypeManager.queryGWParameterTableColumns(tableId);

				if (params != null && !params.isEmpty()) {
					for (Vector<Object> vector : params) {
						String values = "";
						String names = "";
						String mesValues = "";
						String mesNames = "";
						for (int i = 0; i < vector.size(); i++) {
//							if(null == columns.get(i).getIsrecord() || columns.get(i).getIsrecord().equals("true")){
							String value = CommonUtil.objectToString(vector.get(i));
							String dataType = columns.get(i).getDatatype();
							String columnName = columns.get(i).getName();
							if (value != null && !"".equals(value)) {
								//将单引号转化为特定的符号，在读取数据时再还原回来
								value = value.replaceAll("'", ParameterConstants.REPLACE_SINGLEQUOTE);
							}
							if (i == 0) {
								values = getValue(dataType, value);
								names = columnName;
							} else {
								values = values + "," + getValue(dataType, value);
								names = names + "," + columnName;
							}
//						}
						}
						mesNames = names + ",PRODUCTNUMBER,LUKAHAO,GXPK,ISZF";
						mesValues = values + ",'" + productNumber + "','" + lukahao + "','" + gxPK + "','"+ isZF +"'";
						String mesSql = "insert into " + "mes" + tableName +"("+ mesNames + ")" + "values(" + mesValues + ")";
						conn.executeUpdate(mesSql);
						System.out.println("MES通用记录表保存sql========>>>>>>" + mesSql);
//						String sql = "insert into " + tableName +"("+ names + ")" + "values(" + values + ")";
//						conn.executeUpdate(sql);
//						System.out.println("MES通用记录表保存sql===forPDM======>>>>>>" + sql);
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
	}

	public static void insertRemoteTableParams(String productNumber, String lukahao, String gxPK, String isZF, String tableId, String tableName, Vector<Vector<Object>> params,String bsoId,String version) {
		if (tableName != null && !"".equals(tableName)) {
			MESDBUtil conn = null;
			try {
				conn= MESDBUtil.getDBUtil("03");

				List<GWParameterTableColumn> columns = MesParameterTypeManager.queryGWParameterTableColumns(tableId);

				if (params != null && !params.isEmpty()) {
					for (Vector<Object> vector : params) {
						String values = "";
						String names = "";
						for (int i = 0; i < vector.size(); i++) {
//							if(null == columns.get(i).getIsrecord() || columns.get(i).getIsrecord().equals("true")){
							String value = CommonUtil.objectToString(vector.get(i));
							String dataType = columns.get(i).getDatatype();
							String columnName = columns.get(i).getName();
							if (value != null && !"".equals(value)) {
								//将单引号转化为特定的符号，在读取数据时再还原回来
								value = value.replaceAll("'", ParameterConstants.REPLACE_SINGLEQUOTE);
							}
							if (i == 0) {
								values = getValue(dataType, value);
								names = columnName;
							} else {
								values = values + "," + getValue(dataType, value);
								names = names + "," + columnName;
							}
//						}
						}
						names = names + ",PRODUCTNUMBER,LUKAHAO,GXPK,ISZF";
						values = values + ",'" + productNumber + "','" + lukahao + "','" + gxPK + "','"+ isZF +"'";
						String sql = "insert into " + "gl_mes" + tableName +"("+ names + ")" + "values(" + values + ")";
						System.out.println("gl_MesCommonParamTable=====sql========>>>>>>" + sql);
						conn.update(sql, new Object[]{});
					}
				}

			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	/**
	 * 根据表格当前列的数据类型返回SQL语句
	 *
	 * @param dataType
	 * @param value
	 * @return
	 */
	private static String getValue(String dataType, String value) {
		if (ParameterConstants.TABLE_COLUMN_DATATYPE_INTEGER.equals(dataType)
				|| ParameterConstants.TABLE_COLUMN_DATATYPE_DECIMAL.equals(dataType)) {
			//整数或小数
			if (value == null || "".equals(value)) {
				value = "0";
			}
			value = "" + value +"";
		} else {
			//字符
			value = "'" + value +"'";
		}
		return value;
	}

	public static CmParamTableType getMESCommonParamTableType(boolean isApproved, Map<String, String> paramsMap) {
		CmParamTableType cmParamTableType = null;
		GWParameterTableType gwParamTableType = null;
		GWParamTableTypeMaster master = null;
		String productNumber = paramsMap.get("processNumber");
		String technicsNumber = paramsMap.get("technicsNumber");
		String objType = paramsMap.get("objType");
		String objNumber = paramsMap.get("objNumber");
		String lukahao = paramsMap.get("lukahao");
		String gxPK= paramsMap.get("gxPK");
		String isZF = paramsMap.get("isZF");
		String bsoId = paramsMap.get("bsoId");
		String version = paramsMap.get("version");
		if(version.contains(".")){
			version = version.substring(0, version.indexOf("."));
		}
		try {
			//获取该对象关联参数表的所有Link
			List<GWOperationToParamTableLink> links = getGwOperationToParamTableLink(technicsNumber, objType, objNumber, bsoId, version);
			if (links != null && !links.isEmpty()) {
				for (GWOperationToParamTableLink link : links) {
					String tableId = link.getParametertableid();
					gwParamTableType = MesParameterTypeManager.queryGwParameterTableType(tableId);
					//获取参数表定义信息
					if (gwParamTableType != null) {
						String masterId = gwParamTableType.getTabletypemasterid();
						master = MesParameterTypeManager.queryParamTableTypeMasterById(masterId);
						if ("通用检查项定义".equals(gwParamTableType.getTechnicstype())) {
							if (!isApproved) {
//								deleteParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey());
//								gwParamTableType = MesParameterTypeManager.getLatestTableType(masterId);
//								createParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey());
							}
							break;
						}
					}
				}
			} else {
				master = MesParameterTypeManager.queryParamTableTypeMaster("CommonParamTable");
				gwParamTableType = MesParameterTypeManager.getLatestTableType(master.getGwKey());
				createParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey());
			}
			cmParamTableType = GWParameterTableTypeManager.convertToCmParamTableType(gwParamTableType);
			if (cmParamTableType != null) {
				//查询参数
				List<CmParameterTableColumn> tableColumns = cmParamTableType.getTableColumns();
//				String columnEnames = "";
//				String commonColumnEnames = "";
//				String mesCommonColumnEnames = "";
//				for(CmParameterTableColumn tableColumn : tableColumns){
//					String isRecord = tableColumn.getIsrecord();
//					String columnEname = tableColumn.getEnName();
//					if(isRecord != null && isRecord.equals("true")){
//							mesCommonColumnEnames = mesCommonColumnEnames +","+ "b." + columnEname;
//					}else{
//						if(commonColumnEnames == ""){
//							commonColumnEnames = "a." + columnEname;
//						}else{
//							commonColumnEnames = commonColumnEnames +","+ "a." + columnEname;
//						}
//					}
//				}
//				columnEnames = commonColumnEnames + mesCommonColumnEnames;
				String columnEnames = "";
				for(CmParameterTableColumn tableColumn : tableColumns){
					String columnEname = tableColumn.getEnName();
					if(columnEnames == ""){
						columnEnames = columnEname;
					}else{
						columnEnames = columnEnames +","+ columnEname;
					}
				}
				Vector<Vector<Object>> paramVectors = MesParameterTypeManager.queryParamsByTableId(master.getName(), gwParamTableType.getGwKey(), productNumber, technicsNumber, objNumber, objType, lukahao, gxPK, isZF, columnEnames,bsoId,version);
				if (paramVectors != null && !paramVectors.isEmpty()) {
					//Collections.sort(paramVectors, new ParamsComparator());
				}
				cmParamTableType.setParameters(paramVectors);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return cmParamTableType;
	}

	public static CmParamTableType getDataPackageCommonParamTableType(String productNumber, String technicsNumber, String objType, String objNumber, boolean isApproved, String lukahao, String gxPK) {
		CmParamTableType cmParamTableType = null;
		GWParameterTableType gwParamTableType = null;
		GWParamTableTypeMaster master = null;
		try {
			//获取该对象关联参数表的所有Link
			List<GWOperationToParamTableLink> links = getGwOperationToParamTableLink(technicsNumber, objType, objNumber,"","");
			if (links != null && !links.isEmpty()) {
				for (GWOperationToParamTableLink link : links) {
					String tableId = link.getParametertableid();
					gwParamTableType = MesParameterTypeManager.queryGwParameterTableType(tableId);
					//获取参数表定义信息
					if (gwParamTableType != null) {
						String masterId = gwParamTableType.getTabletypemasterid();
						master = MesParameterTypeManager.queryParamTableTypeMasterById(masterId);
						if ("通用检查项定义".equals(gwParamTableType.getTechnicstype())) {
							if (!isApproved) {
//								deleteParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey());
//								gwParamTableType = MesParameterTypeManager.getLatestTableType(masterId);
//								createParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey());
								break;
							}
						}
					}
				}
			} else {
				master = MesParameterTypeManager.queryParamTableTypeMaster("CommonParamTable");
				gwParamTableType = MesParameterTypeManager.getLatestTableType(master.getGwKey());
				createParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey());
			}
			cmParamTableType = GWParameterTableTypeManager.convertToCmParamTableType(gwParamTableType);
			if (cmParamTableType != null) {
				//查询参数
				List<CmParameterTableColumn> tableColumns = cmParamTableType.getTableColumns();
				String columnEnames = "";
				String commonColumnEnames = "";
				String mesCommonColumnEnames = "";
				for(CmParameterTableColumn tableColumn : tableColumns){
					String isRecord = tableColumn.getIsrecord();
					String columnEname = tableColumn.getEnName();
					if(isRecord != null && isRecord.equals("true")){
							mesCommonColumnEnames = mesCommonColumnEnames +","+ "b." + columnEname;
					}else{
						if(commonColumnEnames == ""){
							commonColumnEnames = "a." + columnEname;
						}else{
							commonColumnEnames = commonColumnEnames +","+ "a." + columnEname;
						}
					}
				}
				columnEnames = commonColumnEnames + mesCommonColumnEnames;
				Vector<Vector<Object>> paramVectors = MesParameterTypeManager.queryDataPackageParamsByTableId(master.getName(), gwParamTableType.getGwKey(), productNumber, technicsNumber, objNumber, objType, lukahao, gxPK, columnEnames);
				if (paramVectors != null && !paramVectors.isEmpty()) {
					//Collections.sort(paramVectors, new ParamsComparator());
				}
				cmParamTableType.setParameters(paramVectors);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return cmParamTableType;
	}

//	public static CmParamTableType getMESSpecialParamTableType(String productNumber, String technicsNumber, String objType, String objNumber, boolean isApproved, String lukahao, String gxPK) {
//		CmParamTableType cmParamTableType = null;
//		GWParameterTableType gwParamTableType = null;
//		GWParamTableTypeMaster master = null;
//		try {
//			//获取该对象关联参数表的所有Link
//			List<GWOperationToParamTableLink> links = getGwOperationToParamTableLink(technicsNumber, objType, objNumber);
//			if (links != null && !links.isEmpty()) {
//				for (GWOperationToParamTableLink link : links) {
//					String tableId = link.getParametertableid();
//					gwParamTableType = MesParameterTypeManager.queryGwParameterTableType(tableId);
//					//获取参数表定义信息
//					if (gwParamTableType != null) {
//						String masterId = gwParamTableType.getTabletypemasterid();
//						master = MesParameterTypeManager.queryParamTableTypeMasterById(masterId);
//						if (!"通用检查项定义".equals(gwParamTableType.getTechnicstype())) {
//							if (!isApproved) {
//								deleteParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey());
//								gwParamTableType = MesParameterTypeManager.getLatestTableType(masterId);
//								createParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey());
//								break;
//							}
//						}
//					}
//				}
//			} else {
//				master = MesParameterTypeManager.queryParamTableTypeMaster("CommonParamTable");
//				gwParamTableType = MesParameterTypeManager.getLatestTableType(master.getGwKey());
//				createParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey());
//			}
//			cmParamTableType = GWParameterTableTypeManager.convertToCmParamTableType(gwParamTableType);
//			if (cmParamTableType != null) {
//				//查询参数
//				List<CmParameterTableColumn> tableColumns = cmParamTableType.getTableColumns();
//				String columnEnames = "";
//				String commonColumnEnames = "";
//				String mesCommonColumnEnames = "";
//				for(CmParameterTableColumn tableColumn : tableColumns){
//					String isRecord = tableColumn.getIsrecord();
//					String columnEname = tableColumn.getEnName();
//					if(isRecord != null && isRecord.equals("true")){
//							mesCommonColumnEnames = mesCommonColumnEnames +","+ "b." + columnEname;
//					}else{
//						if(commonColumnEnames == ""){
//							commonColumnEnames = "a." + columnEname;
//						}else{
//							commonColumnEnames = commonColumnEnames +","+ "a." + columnEname;
//						}
//					}
//				}
//				columnEnames = commonColumnEnames + mesCommonColumnEnames;
//				Vector<Vector<Object>> paramVectors = MesParameterTypeManager.queryParamsByTableId(master.getName(), gwParamTableType.getGwKey(), productNumber, technicsNumber, objNumber, objType, lukahao, gxPK, columnEnames);
//				if (paramVectors != null && !paramVectors.isEmpty()) {
//					//Collections.sort(paramVectors, new ParamsComparator());
//				}
//				cmParamTableType.setParameters(paramVectors);
//			}
//		} catch (Exception e) {
//			logger.error(e);
//		}
//		return cmParamTableType;
//	}
	/**
	 * 查询对象使用到的参数表
	 *
	 * @param object
	 * @param tableName 参数表表名
	 * @param dataType 数据记录类型
	 * @return
	 * @throws Exception
	 */
	public static List<GWOperationToParamTableLink> getGwOperationToParamTableLink(String technicsNumber, String objType, String objNumber,String bsoId,String version) throws Exception {
		List<GWOperationToParamTableLink> links = new ArrayList<GWOperationToParamTableLink>();
		GwQuerySpec qs = new GwQuerySpec(GWOperationToParamTableLink.class);
		qs.appendWhere(GWOperationToParamTableLink.TECHNICSNUMBER, GwQuerySpec.EQUAL, technicsNumber);
//		qs.appendAnd();
//		qs.appendWhere(GWOperationToParamTableLink.OBJTYPE, GwQuerySpec.EQUAL, objType);
//		qs.appendAnd();
//		qs.appendWhere(GWOperationToParamTableLink.OBJNUMBER, GwQuerySpec.EQUAL, objNumber);
		qs.appendAnd();
		qs.appendWhere(GWOperationToParamTableLink.VERSION, GwQuerySpec.EQUAL, version);
		qs.appendAnd();
		qs.appendWhere(GWOperationToParamTableLink.BSOID, GwQuerySpec.EQUAL, bsoId);
		qs.appendOrderBy(GWOperationToParamTableLink.TABLEINDEX, false);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		GWOperationToParamTableLink link = null;
		while (qr.hasNext()) {
			link = (GWOperationToParamTableLink)qr.next();
			links.add(link);
		}
		return links;
	}
	public static void deleteParamTableLink(String technicsNumber, String objType, String objNumber, String tableId) throws Exception {
		List<GWOperationToParamTableLink> links = getGwOperationToParamTableLink(technicsNumber, objType, objNumber,"","");
		if (links != null && !links.isEmpty()) {
			for (GWOperationToParamTableLink link : links) {
				if (tableId.equals(link.getParametertableid())) {
					GwPersistenceHelper.manager.delete(link);
				}
			}
		}
	}
	public static void createParamTableLink(String technicsNumber, String objType, String objNumber, String tableId) throws Exception {
		GWOperationToParamTableLink link = new GWOperationToParamTableLink();
		link.setTechnicsnumber(technicsNumber);
		link.setObjnumber(objNumber);
		link.setObjtype(objType);
		link.setParametertableid(tableId);

		GwPersistenceHelper.manager.save(link);
	}
	/**
	 * 获取指定对象使用的所有参数表及其参数集合
	 *
	 * @param persistable
	 * @return
	 */
	public static List<CmParamTableType> getMesParamTableTypes(boolean isApproved, Map<String, String> paramsMap) {
		String productNumber = paramsMap.get("processNumber");
		String technicsNumber = paramsMap.get("technicsNumber");
		String objType = paramsMap.get("objType");
		String objNumber = paramsMap.get("objNumber");
		String lukahao = paramsMap.get("lukahao");
		String gxPK = paramsMap.get("gxPK");
		String isZF = paramsMap.get("isZF");
		String bsoId = paramsMap.get("bsoId");
		String version = paramsMap.get("version");
		if(version.contains(".")){
			version = version.substring(0, version.indexOf("."));
		}
		List<CmParamTableType> paramTableTypes = new ArrayList<CmParamTableType>();
		try {
			//获取该对象关联参数表的所有Link
			List<GWOperationToParamTableLink> links = getGwOperationToParamTableLink(technicsNumber, objType, objNumber,bsoId,version);
			if (links != null && !links.isEmpty()) {
				for (GWOperationToParamTableLink link : links) {
					String tableId = link.getParametertableid();
					GWParameterTableType gwParamTableType = MesParameterTypeManager.queryGwParameterTableType(tableId);
					//获取参数表定义信息
					if (gwParamTableType != null) {
						if (!"通用检查项定义".equals(gwParamTableType.getTechnicstype())) {
							String masterId = gwParamTableType.getTabletypemasterid();
							GWParamTableTypeMaster master = MesParameterTypeManager.queryParamTableTypeMasterById(masterId);
							CmParamTableType cmParamTableType = null;
							if (!isApproved) {
//								deleteParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey());
//								gwParamTableType = MesParameterTypeManager.getLatestTableType(masterId);
//								createParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey());
							}
							cmParamTableType = GWParameterTableTypeManager.convertToCmParamTableType(gwParamTableType);
							if (cmParamTableType != null) {
								//查询参数
								List<CmParameterTableColumn> tableColumns = cmParamTableType.getTableColumns();
								String columnEnames = "";
								for(CmParameterTableColumn tableColumn : tableColumns){
									String columnEname = tableColumn.getEnName();
									if(columnEnames == ""){
										columnEnames = columnEname;
									}else{
										columnEnames = columnEnames +","+ columnEname;
									}
								}
								Vector<Vector<Object>> paramVectors = MesParameterTypeManager.queryMesParamsByTableId(master.getName(), gwParamTableType.getGwKey(), productNumber, technicsNumber, objNumber, objType, lukahao, gxPK, isZF, columnEnames,bsoId,version);
								if (paramVectors != null && !paramVectors.isEmpty()) {
//									Collections.sort(paramVectors, new ParamsComparator());
								}
								cmParamTableType.setParameters(paramVectors);
								paramTableTypes.add(cmParamTableType);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return paramTableTypes;
	}

	public static void updataTechnicsPrimary(String technicsNumber, byte[] bytes){
		try {
			WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(technicsNumber);
			if (null != document) {

				//bytes = updateProcessZip(document, bytes, technicsNumber);

				String tempFilePath = updateProcessZip2(document, bytes, technicsNumber);
				bytes = FileUtil.fileToBytes(new File(tempFilePath + technicsNumber + ".zip"));

				document = WTDocumentUtil.setPrimaryForDocument(document, technicsNumber + ".zip", bytes);

				//删除临时文件
				FileUtil.deleteFile(new File(tempFilePath));
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static String updateProcessZip2(WTDocument wtDocument, byte[] bytes, String processZipDocName)
			throws Exception {
		String subpath = java.util.UUID.randomUUID().toString();
		String tempFilePath = PropertiesUtil.getWTHome() + File.separator + "temp" + File.separator
				+ subpath + File.separator;
		FileUtil.writeBytes(tempFilePath, processZipDocName + ".zip", bytes);
		ApacheZipUtil.decompress(tempFilePath + processZipDocName + ".zip", tempFilePath + processZipDocName);
		File xmlFile = new File(tempFilePath + processZipDocName + File.separator + processZipDocName + ".xml");
		InputStream inputStream = new FileInputStream(xmlFile);

		SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
		Element element = xmlUtil.getRootElement().getChild(XMLConstants.QMFawTechnicsInfo);
		if(element == null ){
			//报表类工艺文件
			element = xmlUtil.getRootElement().getChild(XMLConstants.XWReportTechnicsInfo);
		}
		element.setAttribute("version", wtDocument.getVersionIdentifier().getValue() + "."
				+ VersionControlHelper.nextIterationId(wtDocument).getValue());
		element.setAttribute("lifecycle", wtDocument.getState().getState().getDisplay(Locale.CHINA));

		// 替换工艺xml，打包工艺文件夹，上传工艺压缩包
		FileOutputStream fileOutputStream = new FileOutputStream(new File(tempFilePath + processZipDocName
				+ File.separator + processZipDocName + ".xml"), false);
		Format format = Format.getPrettyFormat();
		format.setEncoding("GBK");
		XMLOutputter xmlOutput = new XMLOutputter(format);
		xmlOutput.output(xmlUtil.getDocument(), fileOutputStream);
		ApacheZipUtil.compress(tempFilePath + processZipDocName, tempFilePath + processZipDocName + ".zip");

		xmlOutput.clone();
		fileOutputStream.close();
		inputStream.close();


		return tempFilePath;
		//return FileUtil.fileToBytes(new File(tempFilePath + processZipDocName + ".zip"));
	}

	public static String getTechnicsNumberByProductNumber(String productNumber){
		String technicsNumber = "";
		Transaction transaction = new Transaction();
		DBConnUtil conn = null;
		try {
			transaction.start();
			conn = new DBConnUtil();
			String sql = "select TECHNICSNUMBER from MESCOMMONPARAMTABLE where PRODUCTNUMBER ='" + productNumber + "'";
			ResultSet rs = conn.executeQuery(sql);
			conn.commit();
			transaction.commit();
			if(rs.next()){
				technicsNumber = rs.getString(1);
				return technicsNumber;
			}else{
				return "";
			}
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
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
		return null;
	}

	public static String getLukahaoByLinkProcessNumber(String processNumber){
		String lukahao = "";
		Transaction transaction = new Transaction();
		DBConnUtil conn = null;
		try {
			transaction.start();
			conn = new DBConnUtil();
			String sql = "select distinct LUKAHAO from MESCOMMONPARAMTABLE where PRODUCTNUMBER like '%" + processNumber + "%'";
			ResultSet rs = conn.executeQuery(sql);
			conn.commit();
			transaction.commit();
			if(rs.next()){
				lukahao = rs.getString("LUKAHAO");
			}
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
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
		return lukahao;
	}
	public static String[] getTechnicsNumberByLukahao(String lukahao){
		String[] str = null;
		Transaction transaction = new Transaction();
		DBConnUtil conn = null;
		try {
			transaction.start();
			conn = new DBConnUtil();
			String sql = "select distinct TECHNICSNUMBER,ISZF from MESCOMMONPARAMTABLE where LUKAHAO like '%" + lukahao + "%'";
			ResultSet rs = conn.executeQuery(sql);
			conn.commit();
			transaction.commit();
			while(rs.next()){
				str = new String[2];
				str[0] = rs.getString("TECHNICSNUMBER");
				str[1] = rs.getString("ISZF");
				if("Y".equals(str[1])){
					WTDocument doc = WTDocumentUtil.getDocumentByNumber(str[0]);
					IBAHelper helper = new IBAHelper(doc);
					String zfFlag = helper.getIBAValue("ZFFLAG");
					if("Z".equals(zfFlag)){
						return str;
					}
				}
			}
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
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
		return str;
	}

	public static List<Bdpsndoc> getCampPersonInfo(String name, String number){
		MESDBUtil conn = null;
		try {
			conn = MESDBUtil.getDBUtil("03");
			String sql = "select PSNNAME,PSNCODE,CLERKCODE from BD_PSNDOC where PSNNAME LIKE '%" + name + "%' and PSNCODE LIKE '%"+ number +"%'";
			List<Bdpsndoc> rs = conn.queryForList(sql, Bdpsndoc.class);
			return rs;
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}
	public static Object[] getZFTechnics(String technicNumber){
		ProcessService ps = new ProcessService();
		try {
			return ps.getZFBeans(technicNumber);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static List<String[]> getFzTechnicsNumberList(String zzTechnicsNumber) {
		List<String[]> fzTechnicNumberList = new ArrayList<String[]>();
		String[] fzTechnics = null;
		DBConnUtil con = null;
		Transaction transaction = new Transaction();
		try {
			WTDocument doc = WCUtil.getDocumentByNumber(zzTechnicsNumber);
			String version = doc.getVersionInfo().getIdentifier().getValue();
			transaction.start();
			con = new DBConnUtil();
			String sql = "select DISTINCT FZTECHNICSNUMBER,FZTECHNICSVERSION from GL_ZHUFULINK where ZZTECHNICSNUMBER='" + zzTechnicsNumber + "' and ZZTECHNICSVERSION='"+ version +"'";
			ResultSet rs = con.executeQuery(sql);
			con.commit();
			transaction.commit();
			while(rs.next()){
				fzTechnics = new String[2];
				fzTechnics[0] = rs.getString("FZTECHNICSNUMBER");
				fzTechnics[1] = rs.getString("FZTECHNICSVERSION");
				fzTechnicNumberList.add(fzTechnics);
			}
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		}finally{
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			transaction = null;
		}
		return fzTechnicNumberList;
	}
	public static Vector<Object> getTechnicDocumentByNumberAndVersion(String technicNubmer, String version){
		Vector<Object> vector = new Vector<Object>();
		try {
			WTDocument docuemnt = WTDocumentUtil.getDocumentByNumberAndVersion(technicNubmer, version);
			ApplicationData data = WTDocumentUtil.getPrimaryByDocument(docuemnt);
			byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
			vector.add(data.getFileName());
			vector.add(bytes);
			return vector;
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String getZzTechnicsNumber(String technicsNumber){
		try {
			WTDocument document = WTDocumentUtil.getDocumentByNumber(technicsNumber);
			IBAHelper helper = new IBAHelper(document);
			String zfFlag = helper.getIBAValue("ZFFLAG");
			if(zfFlag != null && "Z".equals(zfFlag)){
				return technicsNumber;
			}else{
				return getzzTechnicsNumberByfzTechnicsNumber(technicsNumber);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public static String getzzTechnicsNumberByfzTechnicsNumber(String fzTechnicsNumber){
		DBConnUtil con = null;
		String zzTechnicsNumber = "";
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			con = new DBConnUtil();
			String sql = "select ZZTECHNICSNUMBER from GL_ZHUFULINK where FZTECHNICSNUMBER ='" + fzTechnicsNumber + "'";
			ResultSet rs = con.executeQuery(sql);
			con.commit();
			transaction.commit();
			if(rs.next()){
				zzTechnicsNumber = rs.getString(1);
				return zzTechnicsNumber;
			}else{
				return "";
			}
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		}finally{
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			transaction = null;
		}
		return zzTechnicsNumber;
	}
	public static boolean isHasZhufuLink(String zzTechnicsNumber){
		DBConnUtil con = null;
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			con = new DBConnUtil();
			String sql = "select count(*) from GL_ZHUFULINK where ZZTECHNICSNUMBER ='" + zzTechnicsNumber + "'";
			ResultSet rs = con.executeQuery(sql);
			con.commit();
			transaction.commit();
			if(rs.next()){
				int i = rs.getInt(1);
				if(i > 0){
					return true;
				}
			}else{
				return false;
			}
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		}finally{
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			transaction = null;
		}
		return false;
	}

	public static List<MesBfcccpbh> getProcessNumberValues(String lukahao){
		MESDBUtil conn = null;
		try {
			conn= MESDBUtil.getDBUtil("03");
			String sql = "select BFCCPBHNO,GYGCKNO from SHHT_MES_BFCCPBH where GYGCKNO ='" + lukahao + "'";
			List<MesBfcccpbh> processNumberList = conn.queryForList(sql, MesBfcccpbh.class);
			return processNumberList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getDefaultValues(Map<String, String> paramsMap){
		String defaultValues = "";
		String technicsNumber = paramsMap.get("technicsNumber");
		String objType = paramsMap.get("objType");
		String objNumber= paramsMap.get("objNumber");
		String lukahao = paramsMap.get("lukahao");
		String isZF = paramsMap.get("isZF");
		String bsoId = paramsMap.get("bsoId");
		String version = paramsMap.get("version");
		if(version.contains(".")){
			version = version.substring(0, version.indexOf("."));
		}
		DBConnUtil con = null;
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			con = new DBConnUtil();
			String sql = "select PRODUCTNUMBER from MESCOMMONPARAMTABLE where TECHNICSNUMBER='" + technicsNumber +
//					"' and OBJTYPE='"+ objType +
//					"' and OBJNUMBER='"+ objNumber +
					"' and BSOID='"+ bsoId +
					"' and VERSION='"+ version +
					"' and LUKAHAO='"+ lukahao +
					"' and ISZF='"+ isZF +"'";
			ResultSet rs = con.executeQuery(sql);
			con.commit();
			transaction.commit();
			if(rs.next()){
				defaultValues = rs.getString(1);
				return defaultValues;
			}else{
				return "";
			}
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		}finally{
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			transaction = null;
		}
		return defaultValues;
	}

	public static Map<String, String> getProcessNumberGroup(Map<String, String> paramsMap){
		Map<String, String> processNumberGroup = new HashMap<String, String>();
		String tableName = paramsMap.get("tableName");
		String technicsNumber = paramsMap.get("technicsNumber");
		String objNumber = paramsMap.get("objNumber");
		String objType = paramsMap.get("objType");
		String lukahao = paramsMap.get("lukahao");
		String isZF = paramsMap.get("isZF");
		String bsoId = paramsMap.get("bsoId");
		String version = paramsMap.get("version");
		if(version.contains(".")){
			version = version.substring(0, version.indexOf("."));
		}
		DBConnUtil con = null;
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			con = new DBConnUtil();
			String sql = "select distinct PRODUCTNUMBER from "+ tableName +" where TECHNICSNUMBER='" + technicsNumber +
//					"' and OBJTYPE='"+ objType +
//					"' and OBJNUMBER='"+ objNumber +
					"' and LUKAHAO='"+ lukahao +
					"' and BSOID='"+ bsoId +
					"' and VERSION='"+ version +
					"' and ISZF='"+ isZF +"'";
			ResultSet rs = con.executeQuery(sql);
			con.commit();
			transaction.commit();
			int i = 1;
			while(rs.next()){
				processNumberGroup.put(String.valueOf(i), rs.getString(1));
				i++;
			}
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		}finally{
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			transaction = null;
		}
		return processNumberGroup;
	}
}

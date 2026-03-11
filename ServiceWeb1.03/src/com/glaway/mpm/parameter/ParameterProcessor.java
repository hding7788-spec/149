package com.glaway.mpm.parameter;

import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.parameter.constants.ParameterConstants;
import com.glaway.mpm.parameter.model.GWOperationToParamTableLink;
import com.glaway.mpm.parameter.model.GWParamTableTypeMaster;
import com.glaway.mpm.parameter.model.GWParameterTableColumn;
import com.glaway.mpm.parameter.model.GWParameterTableType;
import com.glaway.mpm.parameter.model.GWParameterType;
import com.glaway.mpm.parameter.model.data.CmBaiyuParamTableColumn;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.parameter.model.data.CmParameterTableColumn;
import com.glaway.mpm.parameter.service.gwpersistable.GwPersistenceHelper;
import com.glaway.mpm.parameter.service.gwpersistable.GwQueryResult;
import com.glaway.mpm.parameter.service.gwpersistable.GwQuerySpec;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.DepartmentMappingUtils;
import com.glaway.mpm.util.FilesUtil;
import com.glaway.mpm.util.FolderUtil;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.PropertiesConfigs;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.TypeUtil;
import com.glaway.mpm.util.WTContainerUtil;
import com.glaway.mpm.util.WTDocumentUtil;

import ext.casc.process.util.ProcessUtil;
import ext.casc.util.IBAUtil;
import wt.access.NotAuthorizedException;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.fc.ObjectIdentifier;
import wt.fc.PersistInfo;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.config.LatestConfigSpec;

public class ParameterProcessor {

	private static VaLogger logger = VaLogger.getLogger(ParameterProcessor.class.getName());
	private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.QIAN_LONG_CONFIG_PATH);
	private static Logger LOG4J=Logger.getLogger(ParameterProcessor.class);

	/**
	 * 保存工艺规程\工序\工步的参数至数据库中。保存时不执行更新操作，直接删除该对象已有参数，再重新写入新的参数。
	 * @param bsoID
	 *
	 * @param object
	 * @param paramTableTypes
	 */
	public static void saveParameters(CmParamTableType paramTableType, String technicsNumber, String objType, String objNumber, String bsoID, String version) {
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			String tableId = String.valueOf(paramTableType.getOid());
			String tableName = paramTableType.getEnName();
			Vector<Vector<Object>> params = paramTableType.getParameters();

			deleteTableParams(tableName, technicsNumber, objType, objNumber, bsoID, version);

			//将参数写入数据库表中
			if (params != null && !params.isEmpty()) {
				insertTableParams(tableId, tableName, params, bsoID, version);
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			logger.error(e);
			e.printStackTrace();
		} finally {
			transaction = null;
		}
	}

	/**
	 * 获取指定对象使用的所有参数表及其参数集合
	 *
	 * @param persistable
	 * @return
	 */
	public static List<CmParamTableType> getParamTableTypes(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) {
		if(version.contains(".")){
			version = version.substring(0, version.indexOf("."));
		}
		List<CmParamTableType> paramTableTypes = new ArrayList<CmParamTableType>();
		try {
			//获取该对象关联参数表的所有Link
			List<GWOperationToParamTableLink> links = getGwOperationToParamTableLink(technicsNumber, objType, objNumber, bsoID, version);
			if (links != null && !links.isEmpty()) {
				int index = 0;
				for (GWOperationToParamTableLink link : links) {
					String tableId = link.getParametertableid();
					GWParameterTableType gwParamTableType = GWParameterTableTypeManager.queryGwParameterTableType(tableId);
					//获取参数表定义信息
					if (gwParamTableType != null) {
						if (!"通用检查项定义".equals(gwParamTableType.getTechnicstype())) {
							String masterId = gwParamTableType.getTabletypemasterid();
							GWParamTableTypeMaster master = GWParameterTableTypeManager.queryParamTableTypeMasterById(masterId);
							CmParamTableType cmParamTableType = null;
							if (!isApproved) {
								deleteParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey(), bsoID, version);
								gwParamTableType = GWParameterTableTypeManager.getLatestTableType(masterId);
								createParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey(), String.valueOf(index), bsoID, version);
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
								Vector<Vector<Object>> paramVectors = GWParameterTableTypeManager.queryParamsByTableId(master.getName(), gwParamTableType.getGwKey(), technicsNumber, objNumber, objType, columnEnames, bsoID, version);
								if (paramVectors != null && !paramVectors.isEmpty()) {
//									Collections.sort(paramVectors, new ParamsComparator());
								}
								cmParamTableType.setParameters(paramVectors);
								paramTableTypes.add(cmParamTableType);
							}
						}
					}
					index++;
				}
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return paramTableTypes;
	}

	public static void setSpecialParamTableIndex(String tableOid, String technicsNumber, String objType, String objNumber, String index){

			DBConnUtil conn = null;
			try {
				conn= new DBConnUtil();

				String sql = "update GWOPERATIONTOPARAMTABLELINK set TABLEINDEX='"+ index +"' where PARAMETERTABLEID='"+ tableOid
						+"' and TECHNICSNUMBER='"+ technicsNumber +"' and OBJTYPE='"+objType+"' and OBJNUMBER='"+objNumber+"'";
				conn.executeUpdate(sql);

				conn.commit();
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					logger.error(e);
					e.printStackTrace();
				}
			}
	}
	public static List<CmParamTableType> getParamTableTypesForMes(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) {
		List<CmParamTableType> paramTableTypes = new ArrayList<CmParamTableType>();
		if(version.contains(".")){
			version = version.substring(0, version.indexOf("."));
		}
		try {
			//获取该对象关联参数表的所有Link
			List<GWOperationToParamTableLink> links = getGwOperationToParamTableLink(technicsNumber, objType, objNumber, bsoID, version);
			if (links != null && !links.isEmpty()) {
				for (GWOperationToParamTableLink link : links) {
					String tableId = link.getParametertableid();
					GWParameterTableType gwParamTableType = GWParameterTableTypeManager.queryGwParameterTableType(tableId);
					//获取参数表定义信息
					if (gwParamTableType != null) {
						if (!"通用检查项定义".equals(gwParamTableType.getTechnicstype())) {
							String masterId = gwParamTableType.getTabletypemasterid();
							GWParamTableTypeMaster master = GWParameterTableTypeManager.queryParamTableTypeMasterById(masterId);
							CmParamTableType cmParamTableType = null;
							if (!isApproved) {
//								deleteParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey());
//								gwParamTableType = GWParameterTableTypeManager.getLatestTableType(masterId);
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
								Vector<Vector<Object>> paramVectors = GWParameterTableTypeManager.queryParamsByTableId(master.getName(), gwParamTableType.getGwKey(), technicsNumber, objNumber, objType, columnEnames, bsoID, version);
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
			e.printStackTrace();
		}
		return paramTableTypes;
	}

	public static void createParamTableLink(String technicsNumber, String objType, String objNumber, String tableId, String tableIndex, String bsoID, String version) throws Exception {
		GWOperationToParamTableLink link = new GWOperationToParamTableLink();
		link.setTechnicsnumber(technicsNumber);
		link.setObjnumber(objNumber);
		link.setObjtype(objType);
		link.setParametertableid(tableId);
		link.setBsoID(bsoID);
		link.setVersion(version);
		if(tableIndex != null){
			link.setTableindex(tableIndex);
		}
		GwPersistenceHelper.manager.save(link);
	}

	public static void deleteParamTableLink(String technicsNumber, String objType, String objNumber, String tableId, String bsoID, String version) throws Exception {
		List<GWOperationToParamTableLink> links = getGwOperationToParamTableLink(technicsNumber, objType, objNumber, bsoID, version);
		if (links != null && !links.isEmpty()) {
			for (GWOperationToParamTableLink link : links) {
				if (tableId.equals(link.getParametertableid())) {
					GwPersistenceHelper.manager.delete(link);
				}
			}
		}
	}

	/**
	 * 查询对象使用到的参数表
	 * @param bsoID
	 *
	 * @param object
	 * @param tableName 参数表表名
	 * @param dataType 数据记录类型
	 * @return
	 * @throws Exception
	 */
	public static List<GWOperationToParamTableLink> getGwOperationToParamTableLink(String technicsNumber, String objType, String objNumber, String bsoID, String version) throws Exception {
		List<GWOperationToParamTableLink> links = new ArrayList<GWOperationToParamTableLink>();
		GwQuerySpec qs = new GwQuerySpec(GWOperationToParamTableLink.class);
		qs.appendWhere(GWOperationToParamTableLink.TECHNICSNUMBER, GwQuerySpec.EQUAL, technicsNumber);
		qs.appendAnd();
//		qs.appendWhere(GWOperationToParamTableLink.OBJTYPE, GwQuerySpec.EQUAL, objType);
//		qs.appendAnd();
//		qs.appendWhere(GWOperationToParamTableLink.OBJNUMBER, GwQuerySpec.EQUAL, objNumber);
		qs.appendWhere(GWOperationToParamTableLink.VERSION, GwQuerySpec.EQUAL, version);
		qs.appendAnd();
		qs.appendWhere(GWOperationToParamTableLink.BSOID, GwQuerySpec.EQUAL, bsoID);
		qs.appendOrderBy(GWOperationToParamTableLink.TABLEINDEX, false);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		GWOperationToParamTableLink link = null;
		while (qr.hasNext()) {
			link = (GWOperationToParamTableLink)qr.next();
			links.add(link);
		}
		return links;
	}
	public static List<GWOperationToParamTableLink> getGwOperationToParamTableLink2(String technicsNumber, String objType, String objNumber, String bsoID, String version,String paramId) throws Exception {
		List<GWOperationToParamTableLink> links = new ArrayList<GWOperationToParamTableLink>();
		GwQuerySpec qs = new GwQuerySpec(GWOperationToParamTableLink.class);
		qs.appendWhere(GWOperationToParamTableLink.TECHNICSNUMBER, GwQuerySpec.EQUAL, technicsNumber);
		qs.appendAnd();
		qs.appendWhere(GWOperationToParamTableLink.PARAMETERTABLEID,GwQuerySpec.EQUAL,paramId);
		qs.appendAnd();
//		qs.appendWhere(GWOperationToParamTableLink.OBJTYPE, GwQuerySpec.EQUAL, objType);
//		qs.appendAnd();
//		qs.appendWhere(GWOperationToParamTableLink.OBJNUMBER, GwQuerySpec.EQUAL, objNumber);
		qs.appendWhere(GWOperationToParamTableLink.VERSION, GwQuerySpec.EQUAL, version);
		qs.appendAnd();
		qs.appendWhere(GWOperationToParamTableLink.BSOID, GwQuerySpec.EQUAL, bsoID);
		qs.appendOrderBy(GWOperationToParamTableLink.TABLEINDEX, false);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		GWOperationToParamTableLink link = null;
		while (qr.hasNext()) {
			link = (GWOperationToParamTableLink)qr.next();
			links.add(link);
		}
		return links;
	}
	/**
	 * 查询对象使用到的参数表
	 * @param bsoID
	 *
	 * @param object
	 * @param tableName 参数表表名
	 * @param dataType 数据记录类型
	 * @return
	 * @throws Exception
	 */
	public static List<GWOperationToParamTableLink> getGwOperationToParamTableLink2(String technicsNumber, String version,List<GWOperationToParamTableLink> links) throws Exception {
		GwQuerySpec qs = new GwQuerySpec(GWOperationToParamTableLink.class);
		qs.appendWhere(GWOperationToParamTableLink.TECHNICSNUMBER, GwQuerySpec.EQUAL, technicsNumber);
		qs.appendAnd();
		qs.appendWhere(GWOperationToParamTableLink.VERSION, GwQuerySpec.EQUAL, version);
		qs.appendOrderBy(GWOperationToParamTableLink.TABLEINDEX, false);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		GWOperationToParamTableLink link = null;
		while (qr.hasNext()) {
			link = (GWOperationToParamTableLink)qr.next();
			links.add(link);
		}
		return links;
	}

	public static void insertNewData(String tableName, String tableId, Vector<Vector<Object>> paramVectors) {
		System.out.println("===更新表：" + tableName + "表ID为" + tableId);
		if (tableName != null && !"".equals(tableName)) {
			DBConnUtil conn = null;
			try {
				conn= new DBConnUtil();

				List<GWParameterTableColumn> columns = GWParameterTableTypeManager.queryGWParameterTableColumns(tableId);

					for (Vector<Object> vector : paramVectors) {
						String values = "";
						String names = "";
						for (int i = 0; i < vector.size(); i++) {
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
						}
						String sql = "insert into "+tableName+"("+ names + ")" + "values(" + values + ")";
						conn.executeUpdate(sql);
					}

				conn.commit();
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					logger.error(e);
					e.printStackTrace();
				}
			}
		}
	}
	public static void deleteTableParams(String tableName, String technicsNumber, String objType, String objNumber, String bsoID, String version) {
		if (tableName != null && !"".equals(tableName)) {
			DBConnUtil conn = null;
			try {
				conn= new DBConnUtil();
//					sql = "delete from " + tableName + " t where t.TECHNICSNUMBER='" + technicsNumber +
//							"' and t.OBJTYPE='" + objType +
//							"' and t.OBJNUMBER='" + objNumber + "'";
				 String	sql = "delete from " + tableName + " t where t.TECHNICSNUMBER='" + technicsNumber +
							"'and t.VERSION='" + version +
							"' and t.BSOID='" + bsoID + "'";
				conn.executeUpdate(sql);
				conn.commit();
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					logger.error(e);
					e.printStackTrace();
				}
			}
		}
	}

	public static void insertTableParams(String tableId, String tableName, Vector<Vector<Object>> params, String bsoID, String version) {
		if (tableName != null && !"".equals(tableName)) {
			DBConnUtil conn = null;
			try {
				conn= new DBConnUtil();

				List<GWParameterTableColumn> columns = GWParameterTableTypeManager.queryGWParameterTableColumns(tableId);

				if (params != null && !params.isEmpty()) {
					for (Vector<Object> vector : params) {
						String values = "";
						String names = "";
						for (int i = 0; i < vector.size(); i++) {
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
						}
//						if(!names.contains("BSOID")){
//							names = names + ",BSOID" + ",VERSION";
//							values = values + ",'" + bsoID + "','" + version + "'";
//						}else{
//							values = values.substring(0, values.length()-4);
//							values = values + bsoID + "','" + version + "'";
//						}
						String sql = "insert into "+tableName+"("+ names + ")" + "values(" + values + ")";
						System.out.println("通用记录表保存sql========>>>>>>" + sql);
						conn.executeUpdate(sql);
					}
				}

				conn.commit();
			} catch (Exception e) {
				logger.error(e);
				e.printStackTrace();
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					logger.error(e);
					e.printStackTrace();
				}
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
				value = "''";
			}
			value = "" + value +"";
		} else {
			//字符
			value = "'" + value +"'";
		}
		return value;
	}

	public static CmParamTableType getCommonParamTableType(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) {
		if(version.contains(".")){
			version = version.substring(0, version.indexOf("."));
		}
		CmParamTableType cmParamTableType = null;
		GWParameterTableType gwParamTableType = null;
		GWParamTableTypeMaster master = null;
		try {
			//获取该对象关联参数表的所有Link
			List<GWOperationToParamTableLink> links = getGwOperationToParamTableLink(technicsNumber, objType, objNumber, bsoID, version);
			if (links != null && !links.isEmpty()) {
				for (GWOperationToParamTableLink link : links) {
					String tableId = link.getParametertableid();
					gwParamTableType = GWParameterTableTypeManager.queryGwParameterTableType(tableId);
					//获取参数表定义信息
					if (gwParamTableType != null) {
						String masterId = gwParamTableType.getTabletypemasterid();
						master = GWParameterTableTypeManager.queryParamTableTypeMasterById(masterId);
						if ("通用检查项定义".equals(gwParamTableType.getTechnicstype())) {
							if (!isApproved) {
								deleteParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey(), bsoID, version);
								gwParamTableType = GWParameterTableTypeManager.getLatestTableType(masterId);
								createParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey(), null, bsoID, version);
								break;
							}else{
								break;
							}
						}
					}
				}
			} else {
				master = GWParameterTableTypeManager.queryParamTableTypeMaster("CommonParamTable");
				if(master == null){
					return null;
				}
				gwParamTableType = GWParameterTableTypeManager.getLatestTableType(master.getGwKey());
				createParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey(), null, bsoID, version);
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
				Vector<Vector<Object>> paramVectors = GWParameterTableTypeManager.queryParamsByTableId(master.getName(), gwParamTableType.getGwKey(), technicsNumber, objNumber, objType, columnEnames, bsoID, version);
				if (paramVectors != null && !paramVectors.isEmpty()) {
					//Collections.sort(paramVectors, new ParamsComparator());
				}
				cmParamTableType.setParameters(paramVectors);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return cmParamTableType;
	}
	public static CmParamTableType getCommonParamTableTypeForMes(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) {
		if(version.contains(".")){
			version = version.substring(0, version.indexOf("."));
		}
		CmParamTableType cmParamTableType = null;
		GWParameterTableType gwParamTableType = null;
		GWParamTableTypeMaster master = null;
		try {
			//获取该对象关联参数表的所有Link
			List<GWOperationToParamTableLink> links = getGwOperationToParamTableLink(technicsNumber, objType, objNumber, bsoID, version);
			if (links != null && !links.isEmpty()) {
				for (GWOperationToParamTableLink link : links) {
					String tableId = link.getParametertableid();
					gwParamTableType = GWParameterTableTypeManager.queryGwParameterTableType(tableId);
					//获取参数表定义信息
					if (gwParamTableType != null) {
						String masterId = gwParamTableType.getTabletypemasterid();
						master = GWParameterTableTypeManager.queryParamTableTypeMasterById(masterId);
						if ("通用检查项定义".equals(gwParamTableType.getTechnicstype())) {
							if (!isApproved) {
//								deleteParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey());
//								gwParamTableType = GWParameterTableTypeManager.getLatestTableType(masterId);
//								createParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey());
								break;
							}else{
								break;
							}
						}
					}
				}
			} else {
				master = GWParameterTableTypeManager.queryParamTableTypeMaster("CommonParamTable");
				if(master == null){
					return null;
				}
				gwParamTableType = GWParameterTableTypeManager.getLatestTableType(master.getGwKey());
				createParamTableLink(technicsNumber, objType, objNumber, gwParamTableType.getGwKey(), null, bsoID, version);
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
				Vector<Vector<Object>> paramVectors = GWParameterTableTypeManager.queryParamsByTableId(master.getName(), gwParamTableType.getGwKey(), technicsNumber, objNumber, objType, columnEnames, bsoID, version);
				if (paramVectors != null && !paramVectors.isEmpty()) {
					//Collections.sort(paramVectors, new ParamsComparator());
				}
				cmParamTableType.setParameters(paramVectors);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return cmParamTableType;
	}
	public static void createNewParams(String technicsNumber,String version,String newVersion) throws Exception{
		System.out.println("=======开始创建新数据==========");
		System.out.println("technicsNumber:" + technicsNumber);
		System.out.println("version:" + version);
		System.out.println("newVersion:" + newVersion);
		CmParamTableType cmParamTableType = null;
		GWParameterTableType gwParamTableType = null;
		GWParamTableTypeMaster master = null;
		List<String> tableIdList = new ArrayList<String>();
		List<GWOperationToParamTableLink> links = new ArrayList<GWOperationToParamTableLink>();
		getGwOperationToParamTableLink2(technicsNumber, version,links);
		if (links != null && !links.isEmpty()) {
			for (GWOperationToParamTableLink link : links) {
				String tableId = link.getParametertableid();
				gwParamTableType = GWParameterTableTypeManager.queryGwParameterTableType(tableId);
				if (gwParamTableType != null) {
					List<GWOperationToParamTableLink> oldLink = getGwOperationToParamTableLink2(technicsNumber, link.getObjtype(), link.getObjnumber(), link.getBsoID(), newVersion,link.getParametertableid());
					if(oldLink != null && oldLink.size() > 0){
						System.out.println("===数据已存在====");
						continue;
					}
					createParamTableLink(technicsNumber, link.getObjtype(), link.getObjnumber(), link.getParametertableid(), link.getTableindex(), link.getBsoID(), newVersion);
					String masterId = gwParamTableType.getTabletypemasterid();
					master = GWParameterTableTypeManager.queryParamTableTypeMasterById(masterId);
					cmParamTableType = GWParameterTableTypeManager.convertToCmParamTableType(gwParamTableType);
					if (cmParamTableType != null && !tableIdList.contains(tableId)) {
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
						Vector<Vector<Object>> paramVectors = GWParameterTableTypeManager.queryParamsByTableId2(master.getName(), gwParamTableType.getGwKey(), technicsNumber, version, newVersion);
						insertNewData(master.getName(), gwParamTableType.getGwKey(), paramVectors);
						tableIdList.add(tableId);
					}
				}
			}
		}
		System.out.println("=======新数据创建结束==========");
	}
	public static void uploadImage(byte[] bytes, String time, String uuid){
		String fileDir = PropertiesUtil.getLocalCodeBase() + File.separator + "mes" + File.separator + time;
		FilesUtil.getFile(bytes, fileDir, uuid);
	}

	public static Map<String, Map<String, byte[]>> getAllImages(){
		Map<String, Map<String, byte[]>> fileBytesMap = new HashMap<String, Map<String, byte[]>>();
		String imageDir = PropertiesUtil.getLocalCodeBase() + File.separator + "mes";
		File fileDir = new File(imageDir);
		File[] dirList =fileDir.listFiles();
		if(dirList == null){
			return null;
		}
		for(int i = 0; i < dirList.length; i++){
			if(dirList[i].isFile()){

			}else if(dirList[i].isDirectory()){
				String date = dirList[i].getName();

				Map<String, byte[]> imageByteMap = new HashMap<String,byte[]>();
				File fileDir2 = new File(imageDir + File.separator + date);
				File[] fileList = fileDir2.listFiles();
				for(int j = 0; j < fileList.length; j++){

					String uuid = fileList[j].getName();
					File image = new File(fileDir2, uuid);
					byte[] imageBytes = FilesUtil.getBytes(image.getPath());
					imageByteMap.put(uuid, imageBytes);
				}
				fileBytesMap.put(date, imageByteMap);
			}
		}
		return fileBytesMap;
	}
	public static void deleteOldUUIDFiles(List<String> oldUUIDFileNameList){
		String dir = PropertiesUtil.getLocalCodeBase();
		for(String oldFileName : oldUUIDFileNameList){
			File oldFile = new File(dir + File.separator + oldFileName);
			if(oldFile.exists()){
				oldFile.delete();
				System.out.println("文件删除成功！！！");
			}
		}
	}
	public static boolean hasChinaName(CmParamTableType paramTableType){
		GWParameterType gwParameterType = null;
		try {
			gwParameterType = new GWParameterType();
			gwParameterType = queryParameterType(paramTableType.getName());
			if(gwParameterType != null){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 通过参数类型名称查询参数类型对象
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected static GWParameterType queryParameterType(String name) throws Exception {
		GwQuerySpec qs = new GwQuerySpec(GWParameterType.class);
		qs.appendWhere(GWParameterType.CHINANAME, GwQuerySpec.EQUAL, name);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		GWParameterType gwParameterType = null;
		if (qr.hasNext()) {
			gwParameterType = (GWParameterType) qr.next();
		}
		return gwParameterType;
	}

	public static List<String> searhTableConfigName() {
		List<String> list = new ArrayList<String>();
		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();
			String sql = "select * from GLTableName";
			ResultSet rs = conn.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("NAME");
				list.add(name);
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
	public static List<String> searhObjConfigName() {
		List<String> list = new ArrayList<String>();
		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();
			String sql = "select * from GLCHECKRECORDLIB";
			ResultSet rs = conn.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("VALUE");
				list.add(name);
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
	public static List<String> searhObjConfigNameJC() {
		List<String> list = new ArrayList<String>();
		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();
			String sql = "select * from GLCHECKRECORDLIB where TYPE = '检测类'";
			ResultSet rs = conn.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("VALUE");
				list.add(name);
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

	public static List<String> searhObjConfigNameJL() {
		List<String> list = new ArrayList<String>();
		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();
			String sql = "select * from GLCHECKRECORDLIB where TYPE = '记录类'";
			ResultSet rs = conn.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("VALUE");
				list.add(name);
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

	public static List<CmBaiyuParamTableColumn> getBaiyuParamListsByCondition(String id,String name,String creator,String modifier,String isUsed,String department,String formType,Date createfrom,Date createTo,Date modifyFrom,Date modifyTo) {
		boolean falg = SessionServerHelper.manager.setAccessEnforced(false);
		List<CmBaiyuParamTableColumn> list = new ArrayList();
		try {
			int index[] = { 0 };
			QuerySpec qs = new QuerySpec(WTDocument.class);
			WTContainer container = WTContainerUtil.getContainerByName("工艺知识库");
			Folder folder = FolderUtil.getFolder("/Default/爱可生模板文件", WTContainerRef.newWTContainerRef(container));
			if(folder != null){
				qs.appendWhere(new SearchCondition(WTDocument.class, "folderingInfo.parentFolder.key.id", SearchCondition.EQUAL, PersistenceHelper.getObjectIdentifier(folder).getId()), index);
			}
			qs.appendAnd();
			qs.setAdvancedQueryEnabled(true);
			TypeUtil.getTypeQuery(WTDocument.class, "casc.sast.149.BaiYuDocument", qs);
			ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
			if (isUsed != null && !"".equals(isUsed)) {
				qs.appendAnd();
				qs.appendOpenParen();

				AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("templateState");
				if (addv == null) {
					throw new IBADefinitionException("No IBA Definition: " + "templateState");
				}
				long ibaDefId = addv.getObjectID().getId();
				QuerySpec qs2 = new QuerySpec();
				int idx = qs2.appendClassList(StringValue.class, false);
				qs2.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx }, false);
				qs2.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[] { idx });
				qs2.appendAnd();
				qs2.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.EQUAL, isUsed, true), new int[] { idx });

				SubSelectExpression stringIBAQuery = new SubSelectExpression(qs2);
				qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, stringIBAQuery), index);
				qs.appendCloseParen();

			}
			addConditionID(qs,id,index);
			addConditionName(qs,name,index);
			addConditionCreator(qs,creator,index);
			addConditionModifier(qs,modifier,index);
			addConditionDepartment(qs,department,caId,index);
			addConditionFormType(qs,formType,caId,index);
			addConditionCreateDate(qs,createfrom,createTo,caId,index);
			addConditionModifyDate(qs,modifyFrom,modifyTo,caId,index);
			
			qs.appendOrderBy(new OrderBy(new ClassAttribute(WTDocument.class,
					WTDocument.CREATE_TIMESTAMP), false), new int[] { 0 });
			System.out.println("qs============"+qs.toString());
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			int row = 1;
			WTUser currentUser = (WTUser) SessionHelper.manager.getPrincipal();
			List<WTGroup> userGroupList=queryUserGroup(currentUser);
			Map<String,String> departMap=DepartmentMappingUtils.getDepartmentMappingMap();
			boolean isAdminGroup=checkUserIsAdminGroup(userGroupList);
			LOG4J.debug(MessageFormat.format("getBaiyuParamListsByCondition.isAdminGroup {0}", isAdminGroup));
			List<String> departmentList=queryUserDepartment(userGroupList,departMap);
			LOG4J.debug(MessageFormat.format("getBaiyuParamListsByCondition.departmentList {0}", departmentList));
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				if(doc!=null){
					String dept = IBAHelper.getIBAValue(doc, "DEPT");
					//用户为管理员组时可以查看所有数据，否则只能查看他所在组对应部门的数据。
					if(!isAdminGroup) {
						//如果用户即不是管理员，又不属于任务对应的组，不显示数据
						if(departmentList==null||departmentList.size()==0) {
							break;
						} else if(!departmentList.contains(dept)) { //当数据对应的部门不在用户对应的部门中时不显示数据。
							continue;
						}
					}
					CmBaiyuParamTableColumn param = new CmBaiyuParamTableColumn();
					param.setOrderNo(row+"");
					param.setTableOid(String.valueOf(PersistenceHelper.getObjectIdentifier(doc).getId()));
					param.setTableId(doc.getNumber());
					param.setTableName(doc.getName());
					param.setTableCreator(doc.getCreatorFullName());
					param.setTableModifier(doc.getModifierFullName());
					param.setTableCreateTime(ProcessUtil.formatTime(doc.getCreateTimestamp().getTime()));
					param.setTableModifyTime(ProcessUtil.formatTime(doc.getModifyTimestamp().getTime()));
					String templateState = IBAHelper.getIBAValue(doc, "templateState");
					param.setIsUsed(templateState ==null?"":templateState);
					param.setVersion(doc.getIterationDisplayIdentifier().toString());
					String tableType = IBAHelper.getIBAValue(doc, "tableType");
					param.setTableType(tableType==null?"":tableType);
					
					param.setDept(dept==null?"":dept);
					list.add(param);
					row++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(falg);
		}
		return list;
	}
	
	/** 
	  * @Description: 查询用户组对应的部门,用户只能查看他所在组对应的部门数据
	  * @date 2025年11月6日下午8:09:48
	  * @author Liluwen
	  * @param userGroupList
	  * @param departMap
	  * @return  
	  * @return 
	*/
	private static List<String> queryUserDepartment(List<WTGroup> userGroupList, Map<String, String> departMap) {
		List<String> departmentList=new ArrayList<String>();
		if(userGroupList==null) {
			return departmentList;
		}
		for(WTGroup wtGroup:userGroupList) {
			String department=departMap.get(wtGroup.getName());
			departmentList.add(department);
		}
		return departmentList;
	}

	/** 
	  * @Description: 检查用户是否属于管理员组，如果用户的组中包含“部门_工艺质量数据管理员组”则为管理员组。
	  * @date 2025年11月6日下午8:02:25
	  * @author Liluwen
	  * @param userGroupList
	  * @return  
	  * @return 
	*/
	private static boolean checkUserIsAdminGroup(List<WTGroup> userGroupList) {
		if(userGroupList==null) {
			return false;
		}
		for(WTGroup wtGroup:userGroupList) {
			if("部门_工艺质量数据管理员组".equals(wtGroup.getName())) {
				return true;
			}
		}
		return false;
	}

	/** 
	  * @Description: 查询当前用户的所有组
	  * @date 2025年11月6日下午7:48:04
	  * @author Liluwen
	  * @return  
	  * @return 
	*/
	private static List<WTGroup> queryUserGroup(WTUser currentUser) {
		List<WTGroup> userGroupList = new ArrayList<WTGroup>();
		boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
		try {
			
			Enumeration<?> em = OrganizationServicesHelper.manager.parentGroups(currentUser,false);
			while (em.hasMoreElements()) {
				WTGroup group = (WTGroup) ((WTPrincipalReference) em.nextElement()).getObject();
				userGroupList.add(group);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return userGroupList;
	}

	/** 
	  * @Description: 修改时间不为空时设置修改时间作为查询条件
	  * @date 2025年10月23日下午3:52:36
	  * @author Liluwen
	  * @param qs
	  * @param modifyFrom
	  * @param modifyTo
	  * @param caId
	  * @param index  
	  * @return 
	 * @throws QueryException 
	*/
	private static void addConditionModifyDate(QuerySpec qs, Date modifyFrom, Date modifyTo, ClassAttribute caId,
			int[] index) throws QueryException {
		if(modifyFrom!=null) {
			qs.appendAnd();
			Timestamp timestamp = new Timestamp(modifyFrom.getTime());
			qs.appendWhere(new SearchCondition(WTDocument.class, "thePersistInfo.modifyStamp", SearchCondition.GREATER_THAN, timestamp), index);
		}
		if(modifyTo!=null) {
			qs.appendAnd();
			Timestamp timestamp = new Timestamp(modifyTo.getTime());
			qs.appendWhere(new SearchCondition(WTDocument.class, "thePersistInfo.modifyStamp", SearchCondition.LESS_THAN, timestamp), index);
		}
	}

	/** 
	  * @Description: 创建时间不为空时设置创建时间作为查询条件。
	  * @date 2025年10月23日下午3:39:23
	  * @author Liluwen
	  * @param qs
	  * @param createfrom
	  * @param createTo
	  * @param caId
	  * @param index  
	  * @return 
	 * @throws QueryException 
	*/
	private static void addConditionCreateDate(QuerySpec qs, Date createfrom, Date createTo, ClassAttribute caId,
			int[] index) throws QueryException {
		if(createfrom!=null) {
			qs.appendAnd();
			Timestamp timestamp = new Timestamp(createfrom.getTime()); 
			qs.appendWhere(new SearchCondition(WTDocument.class, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN, timestamp), index);
		}
		if(createTo!=null) {
			qs.appendAnd();
			Timestamp timestamp = new Timestamp(createTo.getTime()); 
			qs.appendWhere(new SearchCondition(WTDocument.class, "thePersistInfo.createStamp", SearchCondition.LESS_THAN, timestamp), index);
		}
	}

	/** 
	  * @Description: 修改者不为空时，添加修改者条件
	  * @date 2025年10月23日下午3:21:59
	  * @author Liluwen
	  * @param qs
	  * @param modifier
	  * @param index  
	  * @return 
	 * @throws QueryException 
	*/
	private static void addConditionModifier(QuerySpec qs, String modifier, int[] index) throws QueryException {
		if(StringUtils.isBlank(modifier)) {
			return ;
		}
		ClassAttribute caId = new ClassAttribute(WTDocument.class, "iterationInfo.modifier.key.id");
		qs.appendAnd();
		qs.appendOpenParen();
		QuerySpec qs2 = new QuerySpec();
		int idx = qs2.appendClassList(WTUser.class, false);
		qs2.appendSelect(new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { idx }, false);
		qs2.appendWhere(new SearchCondition(WTUser.class, "fullName", SearchCondition.LIKE, "%"+modifier+"%", true), new int[] { idx });

		SubSelectExpression stringIBAQuery = new SubSelectExpression(qs2);
		qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, stringIBAQuery), index);
		
		qs.appendCloseParen();
	}

	/** 
	  * @Description: 创建人不为空时增加创建人条件
	  * @date 2025年10月23日下午12:06:35
	  * @author Liluwen
	  * @param qs
	  * @param creator
	  * @param index  
	  * @return 
	 * @throws QueryException 
	*/
	private static void addConditionCreator(QuerySpec qs, String creator, int[] index) throws QueryException {
		if(StringUtils.isBlank(creator)) {
			return ;
		}
		ClassAttribute caId = new ClassAttribute(WTDocument.class, "iterationInfo.creator.key.id");
		qs.appendAnd();
		qs.appendOpenParen();
		QuerySpec qs2 = new QuerySpec();
		int idx = qs2.appendClassList(WTUser.class, false);
		qs2.appendSelect(new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { idx }, false);
		qs2.appendWhere(new SearchCondition(WTUser.class, "fullName", SearchCondition.LIKE, "%"+creator+"%", true), new int[] { idx });

		SubSelectExpression stringIBAQuery = new SubSelectExpression(qs2);
		qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, stringIBAQuery), index);
		
		qs.appendCloseParen();
	}

	/** 
	  * @Description: 查询条件中表单类型不为空时，增加表单类型
	  * @date 2025年10月23日上午11:01:52
	  * @author Liluwen
	  * @param qs
	  * @param formType
	  * @param caId
	  * @param index
	  * @throws IBADefinitionException
	  * @throws NotAuthorizedException
	  * @throws RemoteException
	  * @throws WTException  
	  * @return 
	*/
	private static void addConditionFormType(QuerySpec qs, String formType, ClassAttribute caId, int[] index) throws IBADefinitionException, NotAuthorizedException, RemoteException, WTException {
		if(StringUtils.isBlank(formType)) {
			return ;
		}
		qs.appendAnd();
		qs.appendOpenParen();

		AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("tableType");
		if (addv == null) {
			throw new IBADefinitionException("No IBA Definition: " + "tableType");
		}
		long ibaDefId = addv.getObjectID().getId();
		QuerySpec qs2 = new QuerySpec();
		int idx = qs2.appendClassList(StringValue.class, false);
		qs2.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx }, false);
		qs2.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[] { idx });
		qs2.appendAnd();
		qs2.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.EQUAL, formType, true), new int[] { idx });

		SubSelectExpression stringIBAQuery = new SubSelectExpression(qs2);
		qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, stringIBAQuery), index);
		qs.appendCloseParen();
	}

	/** 
	  * @Description: 查询条件中部门不为空时，增加部门条件
	  * @date 2025年10月23日上午10:57:12
	  * @author Liluwen
	  * @param qs
	  * @param department  
	  * @return 
	 * @throws WTException 
	 * @throws RemoteException 
	 * @throws NotAuthorizedException 
	 * @throws IBADefinitionException 
	*/
	private static void addConditionDepartment(QuerySpec qs, String department,ClassAttribute caId,int index[]) throws IBADefinitionException, NotAuthorizedException, RemoteException, WTException {
		if(StringUtils.isBlank(department)) {
			return ;
		}
		qs.appendAnd();
		qs.appendOpenParen();

		AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("DEPT");
		if (addv == null) {
			throw new IBADefinitionException("No IBA Definition: " + "DEPT");
		}
		long ibaDefId = addv.getObjectID().getId();
		QuerySpec qs2 = new QuerySpec();
		int idx = qs2.appendClassList(StringValue.class, false);
		qs2.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx }, false);
		qs2.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[] { idx });
		qs2.appendAnd();
		qs2.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.EQUAL, department, true), new int[] { idx });

		SubSelectExpression stringIBAQuery = new SubSelectExpression(qs2);
		qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, stringIBAQuery), index);
		qs.appendCloseParen();
	}

	/** 
	  * @Description: 名称不为空时，增加名称查询条件，名称对应文档名称
	  * @date 2025年10月23日上午10:36:14
	  * @author Liluwen
	  * @param qs
	  * @param name  
	  * @return 
	 * @throws QueryException 
	*/
	private static void addConditionName(QuerySpec qs, String name,int index[]) throws QueryException {
		if(StringUtils.isBlank(name)) {
			return ;
		}
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, "%"+name+"%"), index);
	}

	/** 
	  * @Description: ID不为空时，增加ID查询条件，ID对应文档编号
	  * @date 2025年10月23日上午10:35:10
	  * @author Liluwen
	  * @param id  
	  * @return 
	 * @throws QueryException 
	*/
	private static void addConditionID(QuerySpec qs,String id,int index[]) throws QueryException {
		if(StringUtils.isBlank(id)) {
			return ;
		}
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE, "%"+id+"%"), index);

	}

	public static List<CmBaiyuParamTableColumn> getBaiyuParamLists(String isUsed) {
		boolean falg = SessionServerHelper.manager.setAccessEnforced(false);
		List<CmBaiyuParamTableColumn> list = new ArrayList();
		try {
			int index[] = { 0 };
			QuerySpec qs = new QuerySpec(WTDocument.class);
			WTContainer container = WTContainerUtil.getContainerByName("工艺知识库");
			Folder folder = FolderUtil.getFolder("/Default/爱可生模板文件", WTContainerRef.newWTContainerRef(container));
			if(folder != null){
				qs.appendWhere(new SearchCondition(WTDocument.class, "folderingInfo.parentFolder.key.id", SearchCondition.EQUAL, PersistenceHelper.getObjectIdentifier(folder).getId()), index);
			}
			qs.appendAnd();
			qs.setAdvancedQueryEnabled(true);
			TypeUtil.getTypeQuery(WTDocument.class, "casc.sast.149.BaiYuDocument", qs);
			ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
			if (isUsed != null && !"".equals(isUsed)) {
				qs.appendAnd();
				qs.appendOpenParen();

				AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("templateState");
				if (addv == null) {
					throw new IBADefinitionException("No IBA Definition: " + "templateState");
				}
				long ibaDefId = addv.getObjectID().getId();
				QuerySpec qs2 = new QuerySpec();
				int idx = qs2.appendClassList(StringValue.class, false);
				qs2.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx }, false);
				qs2.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[] { idx });
				qs2.appendAnd();
				qs2.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.EQUAL, isUsed, true), new int[] { idx });

				SubSelectExpression stringIBAQuery = new SubSelectExpression(qs2);
				qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, stringIBAQuery), index);
				qs.appendCloseParen();

			}
			qs.appendOrderBy(new OrderBy(new ClassAttribute(WTDocument.class,
					WTDocument.CREATE_TIMESTAMP), false), new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			int row = 1;
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				if(doc!=null){
					CmBaiyuParamTableColumn param = new CmBaiyuParamTableColumn();
					param.setOrderNo(row+"");
					param.setTableOid(String.valueOf(PersistenceHelper.getObjectIdentifier(doc).getId()));
					param.setTableId(doc.getNumber());
					param.setTableName(doc.getName());
					param.setTableCreator(doc.getCreatorFullName());
					param.setTableModifier(doc.getModifierFullName());
					param.setTableCreateTime(ProcessUtil.formatTime(doc.getCreateTimestamp().getTime()));
					param.setTableModifyTime(ProcessUtil.formatTime(doc.getModifyTimestamp().getTime()));
					String templateState = IBAHelper.getIBAValue(doc, "templateState");
					param.setIsUsed(templateState ==null?"":templateState);
					param.setVersion(doc.getIterationDisplayIdentifier().toString());
					String tableType = IBAHelper.getIBAValue(doc, "tableType");
					param.setTableType(tableType==null?"":tableType);
					String dept = IBAHelper.getIBAValue(doc, "DEPT");
					param.setDept(dept==null?"":dept);
					list.add(param);
					row++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(falg);
		}
		return list;
	}

	public static String deleteBaiyuTemplateByOid(String oid) {
		try {
			WTDocument document = WTDocumentUtil.getWTDocumentByOid(oid);
			if(document != null){
				PersistenceHelper.manager.delete(document);
				return "Y";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "N";
	}

	public static String changeBaiyuTemplateStateByOid(String oid, String status) {
		try {
			WTDocument document = WTDocumentUtil.getWTDocumentByOid(oid);
			if(document != null){
				IBAUtil.setIBAStringValue(document,"templateState",status);
				return "Y";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "N";
		}
		return "N";
	}

	public static ArrayList<ArrayList<String>> quoteBaiyuTemplate(ArrayList<ArrayList<String>> lists, String technicsNumber, String stepNumber, String paceNumber) {
		boolean falg = SessionServerHelper.manager.setAccessEnforced(false);

		ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
		try {
			if(lists != null){
				for (ArrayList<String> list : lists) {
					if(list.size()>3){
						String number = list.get(1);
						String version = list.get(3);
						WTDocument doc = WTDocumentUtil.getWTDocumentByNumberAndVersion(number, version);
						if(doc != null){
							String qbyName = IBAHelper.getIBAValue(doc, "qbyName");
							String moreJsonName = IBAHelper.getIBAValue(doc, "moreJsonName");
							String oneJsonName = IBAHelper.getIBAValue(doc, "oneJsonName");
							String tableType = IBAHelper.getIBAValue(doc, "tableType");
							String dept = IBAHelper.getIBAValue(doc, "DEPT");
							List<ApplicationData> applicationData = WTDocumentUtil.getAttachFromDocument(doc);
							WTDocument newDoc = createDoc(qbyName, moreJsonName, oneJsonName, doc.getName(), technicsNumber, stepNumber, paceNumber,tableType,dept,doc);
							if(newDoc != null){
								ArrayList<String> strings = new ArrayList<String>();
								strings.add(0,dataList.size()+1+"");
								strings.add(1,newDoc.getNumber());
								strings.add(2,newDoc.getName());
								strings.add(3,newDoc.getIterationDisplayIdentifier().toString());
								strings.add(4,newDoc.getCreatorFullName());
								strings.add(5,newDoc.getModifierFullName());
								strings.add(6,ProcessUtil.formatTime(doc.getCreateTimestamp().getTime()));
								strings.add(7,ProcessUtil.formatTime(doc.getModifyTimestamp().getTime()));
								strings.add(8,newDoc.getNumber());
								strings.add(9,tableType);
								strings.add(10,dept);
								strings.add(11,newDoc.getDescription());
								dataList.add(strings);
								if(applicationData != null && newDoc != null){
									for (ApplicationData data : applicationData) {
										byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
										if(bytes != null){
											InputStream is = new ByteArrayInputStream(bytes);
											uploadAttach(newDoc,data.getFileName(),is);
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}finally {
			SessionServerHelper.manager.setAccessEnforced(falg);
		}
		return dataList;
	}

	public static WTDocument createDoc(String qbyFileName, String mJsonFileName, String oJsonFileName, String tableName
			, String technicsNumber, String stepNum, String paceNum, String tableType, String dept,WTDocument templateDoc){
		Transaction tx = null;
		try {
			tx = new Transaction();
			tx.start();
			WTDocument doc = null;
			String folderName = propertiesUtil.getProperty("baiyu-document-save-folder");
			String containerName = propertiesUtil.getProperty("baiyu-document-save-container");
			WTContainer container = WTContainerUtil.getContainerByName(containerName);
			String folderPath = folderName;
			Folder folder = FolderHelper.service.getFolder(folderPath, WTContainerRef.newWTContainerRef(container));
			String documentType = "casc.sast.149.BaiYuDocument";
			if (folder != null) {
				WTDocument document = createDocument(tableName, container, folderPath, documentType,"APPROVED",templateDoc);
				doc = (WTDocument) PersistenceHelper.manager.refresh(document);
				IBAHelper.setIBAStringValue(document,"qbyName",qbyFileName);
				IBAHelper.setIBAStringValue(document,"moreJsonName",mJsonFileName);
				IBAHelper.setIBAStringValue(document,"oneJsonName",oJsonFileName);
				IBAHelper.setIBAStringValue(document,"PPNUMBER",technicsNumber);
				IBAHelper.setIBAStringValue(document,"stepNum",stepNum);
				IBAHelper.setIBAStringValue(document,"paceNum",paceNum);
				if(tableType != null && !"".equals(tableType) && !"null".equals(tableType)){
					IBAHelper.setIBAStringValue(document,"tableType",tableType);
				}
				if(dept != null && !"".equals(dept) && !"null".equals(dept)){
					IBAHelper.setIBAStringValue(document,"DEPT",dept);
				}
			}
			tx.commit();
			tx = null;
			return doc;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}finally {
			if(tx!=null){
				tx.rollback();
			}
		}
	}

	public static WTDocument createDocument(String name, WTContainer container, String folderPath, String type,String stateStr,WTDocument templdateDoc)
			throws WTPropertyVetoException, WTException, RemoteException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		Transaction transaction = new Transaction();
		transaction.start();
		WTDocument document = WTDocument.newWTDocument();
		document.setName(name);
		document.setContainer(container);
		document.setDescription(templdateDoc.getNumber()+"@@"+templdateDoc.getName());
		Folder folder = FolderUtil.getFolder(folderPath, WTContainerRef.newWTContainerRef(container));
		FolderHelper.assignFolder(document, folder);
		TypeDefinitionReference typeRef = TypedUtilityServiceHelper.service.getTypeDefinitionReference(type);
		document.setTypeDefinitionReference(typeRef);
		LifeCycleState state = LifeCycleState.newLifeCycleState();
		state.setState(State.toState(stateStr));
		document.setState(state);
		document = (WTDocument) PersistenceHelper.manager.save(document);
		transaction.commit();
		SessionServerHelper.manager.setAccessEnforced(flag);
		return document;
	}

	public static WTDocument uploadAttach(WTDocument document, String fileName, InputStream inputStream)
			throws WTException, PropertyVetoException, IOException {
		Transaction tx = null;
		try {
			tx = new Transaction();
			tx.start();
			document = (WTDocument) PersistenceHelper.manager.refresh(document);
			ApplicationData appData = null;
			QueryResult qr = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
			while(qr.hasMoreElements()){
				appData = (ApplicationData)qr.nextElement();
				if(fileName.equals(appData.getFileName())){
					PersistenceHelper.manager.delete(appData);
					PersistenceServerHelper.manager.update(document);
				}
			}
			appData = ApplicationData.newApplicationData(document);
			// ContentRoleType.SECONDARY 表示附件
			appData.setRole(ContentRoleType.SECONDARY);
			// 设置文件名称
			appData.setFileName(fileName);
			appData = ContentServerHelper.service.updateContent(document, appData, inputStream,true); // 更新内容
			PersistenceServerHelper.manager.update(document);
			document = (WTDocument) ContentServerHelper.service.updateHolderFormat((FormatContentHolder) document); // 更新格式
			if (null != inputStream) {
				inputStream.close();
			}
			tx.commit();
			tx = null;
			return document;
		}finally {
			if(tx!=null){
				tx.rollback();
			}
		}
	}

	public static List<CmBaiyuParamTableColumn> getBaiyuParamListsByName(String name, String tableType, String dept) {
		boolean falg = SessionServerHelper.manager.setAccessEnforced(false);
		List<CmBaiyuParamTableColumn> list = new ArrayList();
		try {
			int index[] = { 0 };
			QuerySpec qs = new QuerySpec(WTDocument.class);
			WTContainer container = WTContainerUtil.getContainerByName("工艺知识库");
			Folder folder = FolderUtil.getFolder("/Default/爱可生模板文件", WTContainerRef.newWTContainerRef(container));
			if(folder != null){
				qs.appendWhere(new SearchCondition(WTDocument.class, "folderingInfo.parentFolder.key.id", SearchCondition.EQUAL, PersistenceHelper.getObjectIdentifier(folder).getId()), index);
			}
			qs.appendAnd();
			qs.setAdvancedQueryEnabled(true);
			TypeUtil.getTypeQuery(WTDocument.class, "casc.sast.149.BaiYuDocument", qs);
			if(name != null && !"".equals(name)){
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, "%" + name + "%"), index);
			}
			ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
			qs.appendAnd();
			qs.appendOpenParen();
			qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, getStringIBAQuery("templateState","启用")), index);
			if(tableType != null && !"".equals(tableType)){
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, getStringIBAQuery("tableType",tableType)), index);
			}
			if(dept != null && !"".equals(dept)) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, getStringIBAQuery("DEPT",dept)), index);
			}
			qs.appendCloseParen();
			qs.appendOrderBy(new OrderBy(new ClassAttribute(WTDocument.class,
					WTDocument.CREATE_TIMESTAMP), false), new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			int row = 1;
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				if(doc!=null){
					CmBaiyuParamTableColumn param = new CmBaiyuParamTableColumn();
					param.setOrderNo(row+"");
					param.setTableOid(String.valueOf(PersistenceHelper.getObjectIdentifier(doc).getId()));
					param.setTableId(doc.getNumber());
					param.setTableName(doc.getName());
					param.setTableCreator(doc.getCreatorFullName());
					param.setTableModifier(doc.getModifierFullName());
					param.setTableCreateTime(ProcessUtil.formatTime(doc.getCreateTimestamp().getTime()));
					param.setTableModifyTime(ProcessUtil.formatTime(doc.getModifyTimestamp().getTime()));
					String templateState = IBAHelper.getIBAValue(doc, "templateState");
					param.setIsUsed(templateState ==null?"":templateState);
					param.setVersion(doc.getIterationDisplayIdentifier().toString());
					String type = IBAHelper.getIBAValue(doc, "tableType");
					param.setTableType(type==null?"":type);
					String d = IBAHelper.getIBAValue(doc, "DEPT");
					param.setDept(d==null?"":d);
					list.add(param);
					row++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(falg);
		}
		return list;
	}

	private static SubSelectExpression getStringIBAQuery(String ibaName, String ibaValue) throws WTException,RemoteException {
		// 获取IBA属性定义
		AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
		if (addv == null)
			throw new IBADefinitionException("No IBA Definition: " + ibaName);
		long ibaDefId = addv.getObjectID().getId();
		QuerySpec qs = new QuerySpec();
		int idx = qs.appendClassList(StringValue.class, false);
		qs.appendSelect(new ClassAttribute(StringValue.class,"theIBAHolderReference.key.id"), new int[]{idx}, false);
		qs.appendWhere(new SearchCondition(StringValue.class,"definitionReference.key.id", SearchCondition.EQUAL, ibaDefId),new int[]{idx});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.EQUAL, ibaValue, true), new int[] { idx });
		return new SubSelectExpression(qs);
	}

	public static String updateBaiyuTemplateByOid(String oid, CmBaiyuParamTableColumn tableColumn) {
		try {
			WTDocument document = WTDocumentUtil.getWTDocumentByOid(oid);
			if(document != null){
				String tableType = tableColumn.getTableType();
				if(!tableType.isEmpty()){
					IBAUtil.setIBAStringValue(document,"tableType",tableType);
				}
				String dept = tableColumn.getDept();
				if(!dept.isEmpty()){
					IBAUtil.setIBAStringValue(document,"DEPT",dept);
				}
				return "Y";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "N";
		}
		return "N";
	}
}

package com.glaway.mpm.intf;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.model.ConsCheckRecord;
import com.glaway.mpm.model.ConsCheckTree;
import com.glaway.mpm.parameter.GWParameterServiceImp;
import com.glaway.mpm.parameter.GWParameterTableTypeManager;
import com.glaway.mpm.parameter.ParameterProcessor;
import com.glaway.mpm.parameter.model.GWParamTableTypeMaster;
import com.glaway.mpm.parameter.model.data.CmBaiyuParamTableColumn;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.parameter.model.data.CmParameterType;
import com.glaway.mpm.parameter.model.data.CmTechnicsType;
import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.MPMProcessEditorUtil;
import wt.method.RemoteAccess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class ProcessParameterToWCIntfRMI implements RemoteAccess {

	private static VaLogger logger = VaLogger.getLogger(ProcessParameterToWCIntfRMI.class.getName());

	private static GWParameterServiceImp parameterServiceImp = new GWParameterServiceImp();

	public static List<CmParameterType> loadParamTypeData() {
		return parameterServiceImp.loadParameterType();
	}
	public static CmParameterType loadParamTypeData(String technicsType) {
		return parameterServiceImp.loadParameterType(technicsType);
	}

	public static CmParameterType createParameterType(CmParameterType parameterType) {
		return parameterServiceImp.createParameterType(parameterType);
	}

	public static CmParameterType saveParameterType(CmParameterType parameterType) {
		return parameterServiceImp.saveParameterType(parameterType);
	}

	public static List<CmTechnicsType> queryTechnicsTypes() {
		return parameterServiceImp.queryTechnicsTypes();
	}

	public static CmParamTableType createParamTableType(CmParamTableType paramTableType) {
		return parameterServiceImp.createParamTableType(paramTableType);
	}

	public static boolean hasChinaName(CmParamTableType paramTableType) throws Exception{
		return ParameterProcessor.hasChinaName(paramTableType);
	}

	public static CmParamTableType saveParamTableType(CmParamTableType paramTableType) {
		return parameterServiceImp.saveParamTableType(paramTableType);
	}

	public static CmParamTableType queryCmParameterTableType(String gwkey) {
		return parameterServiceImp.queryCmParameterTableType(gwkey);
	}

	public static List<GWParamTableTypeMaster> queryAllParamTableTypeMasters() {
		return parameterServiceImp.queryAllParamTableTypeMasters();
	}

	public static Vector<Vector<String>> queryParamsByTableId(String tableName, String tableId, String paramTableTypeIid) {
		return parameterServiceImp.queryParamsByTableId(tableName, tableId, paramTableTypeIid);
	}

	public static String createParamTableLink(String tableId, String technicsNumber, String objNumber, String objType, String bsoID, String version) {
		String msg = "success";
		try {
			ParameterProcessor.createParamTableLink(tableId, technicsNumber, objNumber, objType, null, bsoID, version);
		} catch (Exception e) {
			msg = e.getLocalizedMessage();
			logger.error(e);
		}
		return msg;
	}

	public static String deleteParamTableLink(String tableId, String technicsNumber, String objNumber, String objType, String bsoID, String version) {
		String msg = "success";
		try {
			ParameterProcessor.deleteParamTableLink(tableId, technicsNumber, objNumber, objType, bsoID, version);
		} catch (Exception e) {
			msg = e.getLocalizedMessage();
			logger.error(e);
		}
		return msg;
	}

	public static List<CmTechnicsType> loadParamTableTypeData() {
		return parameterServiceImp.loadParamTableType();
	}

	public static CmTechnicsType loadSimpleParamTableTypeData(String technicsType) {
		return parameterServiceImp.loadSimpleParamTableType(technicsType);
	}

	public static CmParamTableType getCommonParamTableType(CmParamTableType paramTableType) {
		return parameterServiceImp.getCommonParamTableType(paramTableType);
	}

	public static CmParamTableType getCommonParamTableType(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) {
		return GWParameterTableTypeManager.getCommonParamTableType(technicsNumber, objType, objNumber, isApproved, bsoID, version);
	}

	public static CmParamTableType getCommonParamTableTypeForMes(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) {
		return GWParameterTableTypeManager.getCommonParamTableTypeForMes(technicsNumber, objType, objNumber, isApproved, bsoID, version);
	}

	public static void saveParameters(CmParamTableType paramTableType, String technicsNumber, String objType, String objNumber, String bsoID, String version) {
		ParameterProcessor.saveParameters(paramTableType, technicsNumber, objType, objNumber, bsoID, version);
	}

	public static List<CmParamTableType> getParamTableTypes(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) {
		return ParameterProcessor.getParamTableTypes(technicsNumber, objType, objNumber, isApproved, bsoID, version);
	}

	public static void setSpecialParamTableIndex(String tableOid, String technicsNumber, String objType, String objNumber, String index){
		ParameterProcessor.setSpecialParamTableIndex(tableOid, technicsNumber, objType, objNumber, index);
	}

	public static List<CmParamTableType> getParamTableTypesForMes(String technicsNumber, String objType, String objNumber, boolean isApproved, String bsoID, String version) {
		return ParameterProcessor.getParamTableTypesForMes(technicsNumber, objType, objNumber, isApproved, bsoID, version);
	}

	public static void createObjToParamTableLink(String technicsNumber, String objType, String objNumber, String tableId, String tableIndex, String bsoID, String version) throws Exception {
		ParameterProcessor.createParamTableLink(technicsNumber, objType, objNumber, tableId, tableIndex, bsoID, version);
	}

	public static void deleteObjToParamTableLink(String technicsNumber, String objType, String objNumber, String tableId, String bsoID, String version) throws Exception {
		ParameterProcessor.deleteParamTableLink(technicsNumber, objType, objNumber, tableId, bsoID, version );
	}

	public static void deleteParameterType(CmParameterType parameterType) {
		parameterServiceImp.deleteParameterType(parameterType);
	}
	public static String queryMaxEnname(){
		return parameterServiceImp.queryMaxEnname();
	}
	public static String queryMaxNameFromTypeMaster(){
		return parameterServiceImp.queryMaxNameFromTypeMaster();
	}
	public static void uploadImage(byte[] bytes, String time, String uuid){
		ParameterProcessor.uploadImage(bytes, time, uuid);
	}
	public static Map<String, Map<String, byte[]>> getAllImages(){
		return ParameterProcessor.getAllImages();
	}
	public static void deleteOldUUIDFiles(List<String> oldUUIDFileNameList){
		ParameterProcessor.deleteOldUUIDFiles(oldUUIDFileNameList);
	}
	public static void deleteTableParams(String tableName, String technicsNumber, String objType, String objNumber, String bsoID, String version){
		ParameterProcessor.deleteTableParams(tableName, technicsNumber, objType, objNumber, bsoID, version);
	}
	public static List<String> searhTableConfigName(){
		return ParameterProcessor.searhTableConfigName();
	}
	public static List<String> searhObjConfigName(){
		return ParameterProcessor.searhObjConfigName();
	}
	public static List<String> searhObjConfigNameJC(){
		return ParameterProcessor.searhObjConfigNameJC();
	}
	public static List<String> searhObjConfigNameJL(){
		return ParameterProcessor.searhObjConfigNameJL();
	}

	public static List<CmBaiyuParamTableColumn> getBaiyuParamLists(String isUsed){
		return ParameterProcessor.getBaiyuParamLists(isUsed);
	}
	
	public static List<CmBaiyuParamTableColumn> getBaiyuParamListsByCondition(String id,String name,String creator,String modifier,String status,String department,String formType,Date createfrom,Date createTo,Date modifyFrom,Date modifyTo){
		return ParameterProcessor.getBaiyuParamListsByCondition(id,name,creator,modifier,status,department,formType,createfrom,createTo,modifyFrom,modifyTo);
	}

	public static String deleteBaiyuTemplateByOid(String oid){
		return ParameterProcessor.deleteBaiyuTemplateByOid(oid);
	}

	public static String changeBaiyuTemplateStateByOid(String oid,String status){
		return ParameterProcessor.changeBaiyuTemplateStateByOid(oid,status);
	}

	public static ArrayList<ArrayList<String>> quoteBaiyuTemplate(ArrayList<ArrayList<String>> lists, String technicsNumber, String stepNumber, String paceNumber){
		return ParameterProcessor.quoteBaiyuTemplate(lists,technicsNumber,stepNumber,paceNumber);
	}

	public static List<CmBaiyuParamTableColumn> getBaiyuParamListsByName(String name,String tableType,String dept){
		return ParameterProcessor.getBaiyuParamListsByName(name,tableType,dept);
	}

	public static String updateBaiyuTemplateByOid(String oid,CmBaiyuParamTableColumn tableColumn){
		return ParameterProcessor.updateBaiyuTemplateByOid(oid,tableColumn);
	}

	public static ConsCheckTree packageConsCheckTree(String type) {
		String rootName = "";
		String tableName = "";
		if("PRO".equals(type)) {
			rootName = "测试项目";
			tableName = "GLCHECKRECORDLIB";
		}else if("TABLE".equals(type)){
			rootName = "试验项目";
			tableName = "GLTABLENAME";
		} else if("JC".equals(type)) {
			rootName = "测试项目";
			tableName = "GLCHECKRECORDLIB";
		} else if("JL".equals(type)) {
			rootName = "测试项目";
			tableName = "GLCHECKRECORDLIB";
		}
		DBConnUtil conn = null;
		ResultSet rs = null;
		ConsCheckTree checkTree = null;
		try {
			conn = new DBConnUtil();
			String qSql = "select * from GLCHECKRECORDTREE where value = '" + rootName + "'";
			rs = conn.executeQuery(qSql);
			while(rs.next()) {
				DBConnUtil connUtil = null;
				ResultSet resultSet = null;
				try {
					connUtil = new DBConnUtil();
					String id = rs.getString("GWKEYID");
					checkTree = new ConsCheckTree(rootName,id);
					String sql = "select * from "+tableName+" WHERE TREEID = '" + id + "'";
					resultSet = connUtil.executeQuery(sql);
					while(resultSet.next()) {
						if("PRO".equals(type)) {
							String gwkeyid = resultSet.getString("GWKEYID");
							String proName = resultSet.getString("VALUE");
							String proType = resultSet.getString("TYPE");
							checkTree.addRecord(new ConsCheckRecord(proName + "(" + proType + ")",gwkeyid));
						}else if("TABLE".equals(type)){
							String gwkeyid = resultSet.getString("GWKEYID");
							String name = resultSet.getString("NAME");
							checkTree.addRecord(new ConsCheckRecord(name,gwkeyid));
						}
					}
					queryCons(id, checkTree,tableName);
				}finally {
					try {
						if(resultSet != null) {
							resultSet.close();
						}
						if(connUtil != null){
							connUtil.close();
						}
					} catch(SQLException e) {
						throw new RuntimeException(e);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return checkTree;
	}

	public static void queryCons(String pid, ConsCheckTree checkTree, String tableName) {
		DBConnUtil conn = null;
		ResultSet rs = null;
		try {
			conn = new DBConnUtil();
			String qSql = "select * from GLCHECKRECORDTREE where PID = '" + pid + "'";
			rs = conn.executeQuery(qSql);
			while(rs.next()) {
				DBConnUtil connUtil = null;
				ResultSet resultSet = null;
				try {
					connUtil = new DBConnUtil();
					String id = rs.getString("GWKEYID");
					String value = rs.getString("VALUE");
					ConsCheckTree tree = new ConsCheckTree(value, id);
					String sql = "select * from " + tableName + " WHERE TREEID = '" + id + "'";
					resultSet = connUtil.executeQuery(sql);
					while(resultSet.next()) {
						if("GLCHECKRECORDLIB".equals(tableName)) {
							String gwkeyid = resultSet.getString("GWKEYID");
							String proName = resultSet.getString("VALUE");
							String proType = resultSet.getString("TYPE");
							tree.addRecord(new ConsCheckRecord(proName + "(" + proType + ")",gwkeyid));
						}else if("GLTABLENAME".equals(tableName)){
							String gwkeyid = resultSet.getString("GWKEYID");
							String name = resultSet.getString("NAME");
							tree.addRecord(new ConsCheckRecord(name,gwkeyid));
						}
					}
					checkTree.addTrees(tree);
					queryCons(id,tree,tableName);
				}finally {
					try {
						if(resultSet != null) {
							resultSet.close();
						}
						if(connUtil != null){
							connUtil.close();
						}
					} catch(SQLException e) {
						throw new RuntimeException(e);
					}
				}
			}
		} catch(SQLException e) {
			throw new RuntimeException(e);
		} catch(Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch(SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static ConsCheckTree packageConsCheckDetailTree(String type) {
		String rootName = "";
		String tableName = "";
		String proType = "";
		if("TABLE".equals(type)){
			rootName = "试验项目";
			tableName = "GLTABLENAME";
		} else if("JC".equals(type)) {
			rootName = "测试项目";
			tableName = "GLCHECKRECORDLIB";
			proType = "检测类";
		} else if("JL".equals(type)) {
			rootName = "测试项目";
			tableName = "GLCHECKRECORDLIB";
			proType = "记录类";
		}
		DBConnUtil conn = null;
		ResultSet rs = null;
		ConsCheckTree checkTree = null;
		try {
			conn = new DBConnUtil();
			String qSql = "select * from GLCHECKRECORDTREE where value = '" + rootName + "'";
			rs = conn.executeQuery(qSql);
			while(rs.next()) {
				DBConnUtil connUtil = null;
				ResultSet resultSet = null;
				try {
					connUtil = new DBConnUtil();
					String id = rs.getString("GWKEYID");
					checkTree = new ConsCheckTree(rootName,id);
					StringBuffer sql = new StringBuffer();
					sql.append("select * from "+tableName+" WHERE TREEID = '" + id + "'");
					if(proType != null && !"".equals(proType)){
						sql.append("and TYPE = '" + proType + "'");
					}
					resultSet = connUtil.executeQuery(sql.toString());
					while(resultSet.next()) {
						if("TABLE".equals(type)){
							String gwkeyid = resultSet.getString("GWKEYID");
							String name = resultSet.getString("NAME");
							checkTree.addRecord(new ConsCheckRecord(name,gwkeyid));
						}else{
							String gwkeyid = resultSet.getString("GWKEYID");
							String proName = resultSet.getString("VALUE");
							checkTree.addRecord(new ConsCheckRecord(proName,gwkeyid));
						}
					}
					queryDetailCons(id, checkTree,tableName,proType);
				}finally {
					try {
						if(resultSet != null) {
							resultSet.close();
						}
						if(connUtil != null){
							connUtil.close();
						}
					} catch(SQLException e) {
						throw new RuntimeException(e);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return checkTree;
	}

	public static void queryDetailCons(String pid, ConsCheckTree checkTree, String tableName,String proType) {
		DBConnUtil conn = null;
		ResultSet rs = null;
		try {
			conn = new DBConnUtil();
			String qSql = "select * from GLCHECKRECORDTREE where PID = '" + pid + "'";
			rs = conn.executeQuery(qSql);
			while(rs.next()) {
				DBConnUtil connUtil = null;
				ResultSet resultSet = null;
				try {
					connUtil = new DBConnUtil();
					String id = rs.getString("GWKEYID");
					String value = rs.getString("VALUE");
					ConsCheckTree tree = new ConsCheckTree(value, id);
					StringBuffer sql = new StringBuffer();
					sql.append("select * from " + tableName + " WHERE TREEID = '" + id + "'");
					if(proType != null && !"".equals(proType)){
						sql.append("and TYPE = '" + proType + "'");
					}
					resultSet = connUtil.executeQuery(sql.toString());
					while(resultSet.next()) {
						if("GLCHECKRECORDLIB".equals(tableName)) {
							String gwkeyid = resultSet.getString("GWKEYID");
							String proName = resultSet.getString("VALUE");
							tree.addRecord(new ConsCheckRecord(proName,gwkeyid));
						}else if("GLTABLENAME".equals(tableName)){
							String gwkeyid = resultSet.getString("GWKEYID");
							String name = resultSet.getString("NAME");
							tree.addRecord(new ConsCheckRecord(name,gwkeyid));
						}
					}
					checkTree.addTrees(tree);
					queryDetailCons(id,tree,tableName,proType);
				}finally {
					try {
						if(resultSet != null) {
							resultSet.close();
						}
						if(connUtil != null){
							connUtil.close();
						}
					} catch(SQLException e) {
						throw new RuntimeException(e);
					}
				}
			}
		} catch(SQLException e) {
			throw new RuntimeException(e);
		} catch(Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch(SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static String addConsCheckTree(String gwkeyid, String fName) {
		DBConnUtil conn = null;
		String result = "";
		try {
			conn= new DBConnUtil();
			String id = UUID.randomUUID().toString();
			String sql = "insert into GLCHECKRECORDTREE (GWKEYID,VALUE,PID) values ('"+id+"','"+fName+"','"+gwkeyid+"')";
			conn.executeUpdate(sql);
			conn.commit();
			result = id;
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
		return result;
	}

	public static String modifyConsCheckTree(String gwkeyid, String fName) {
		DBConnUtil conn = null;
		String result = "sucess";
		try {
			conn= new DBConnUtil();
			String sql = "update GLCHECKRECORDTREE set VALUE = '"+fName+"' where gwkeyid = '"+gwkeyid+"'";
			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception e) {
			result = "fail";
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

	public static String addConsCheckNode(String gwkeyid, String fName,String xmType,String type) {
		DBConnUtil conn = null;
		String result = "";
		try {
			conn= new DBConnUtil();
			String id = UUID.randomUUID().toString();
			String sql = "";
			if("PRO".equals(type)) {
				sql = "insert into GLCHECKRECORDLIB (GWKEYID,VALUE,TYPE,TREEID) values ('"+id+"','"+fName+"','"+xmType+"','"+gwkeyid+"')";
			}else if("TABLE".equals(type)){
				sql = "insert into GLTABLENAME (GWKEYID,NAME,TREEID) values ('"+id+"','"+fName+"','"+gwkeyid+"')";
			}
			conn.executeUpdate(sql);
			conn.commit();
			result = id;
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
		return result;
	}

	public static String modifyConsCheckNode(String gwkeyid, String fName,String type) {
		DBConnUtil conn = null;
		String result = "sucess";
		try {
			conn= new DBConnUtil();
			String sql = "";
			if("PRO".equals(type)) {
				sql = "update GLCHECKRECORDLIB set VALUE = '"+fName+"' where gwkeyid = '"+gwkeyid+"'";
			}else if("TABLE".equals(type)){
				sql = "update GLTABLENAME set NAME = '"+fName+"' where gwkeyid = '"+gwkeyid+"'";
			}
			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception e) {
			result = "fail";
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

	public static Vector getAllConsCheckRecords(String type) {
		ConsCheckTree tree = packageConsCheckDetailTree(type);
		Vector<String> vector = new Vector<String>();
		MPMProcessEditorUtil.getConsCheckRecordPath(tree,vector,"");
		return vector;
	}

	public static String deleteConsCheckTree(String gwkeyid,String type) {
		DBConnUtil conn = null;
		String result = "sucess";
		Map<String, List<String>> map = MPMProcessEditorUtil.queryTreeByTreeIdAndType(gwkeyid, type);
		for(String table : map.keySet()) {
			try {
				conn= new DBConnUtil();
				List<String> list = map.get(table);
				if(list != null && list.size()>0){
					StringBuffer insql = new StringBuffer();
					for(String id : list) {
						insql.append("'" + id + "',");
					}
					String in = insql.substring(0,insql.length()-1);
					String sql = "DELETE FROM " + table + " WHERE gwkeyid in (" + in + ")";
					conn.executeUpdate(sql);
					conn.commit();
				}
			} catch (Exception e) {
				result = "fail";
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
		return result;
	}

	public static String deleteConsCheckNode(String gwkeyid,String type) {
		DBConnUtil conn = null;
		String result = "sucess";
		try {
			conn= new DBConnUtil();
			String sql = "";
			if("PRO".equals(type)) {
				sql = "DELETE FROM GLCHECKRECORDLIB where gwkeyid = '"+gwkeyid+"'";
			}else if("TABLE".equals(type)){
				sql = "DELETE FROM GLTABLENAME where gwkeyid = '"+gwkeyid+"'";
			}
			conn.executeUpdate(sql);
			conn.commit();
		} catch (Exception e) {
			result = "fail";
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

}

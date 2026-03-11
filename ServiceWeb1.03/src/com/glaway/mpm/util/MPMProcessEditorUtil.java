package com.glaway.mpm.util;

import com.glaway.mpm.model.ConsCheckRecord;
import com.glaway.mpm.model.ConsCheckTree;
import wt.pom.Transaction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MPMProcessEditorUtil {

	public static void getConsCheckRecordPath(ConsCheckTree tree, Vector<String> vector,String path) {
		List<ConsCheckRecord> records = tree.getRecords();
		if(records != null && records.size() > 0) {
			for(ConsCheckRecord record : records) {
				if("".equals(path)){
					vector.add(record.getName());
				}else{
					vector.add(path + "_" + record.getName());
				}
			}
		}
		List<ConsCheckTree> trees = tree.getTrees();
		if(trees != null && trees.size() > 0) {
			for(ConsCheckTree checkTree : trees) {
				if("".equals(path)){
					getConsCheckRecordPath(checkTree,vector,checkTree.getName());
				}else{
					getConsCheckRecordPath(checkTree,vector,path+"_"+checkTree.getName());
				}
			}
		}
	}

	public static Map<String,List<String>> queryTreeByTreeIdAndType(String gwkeyid, String type) {
		Map<String,List<String>> map = new HashMap<String, List<String>>();
		Transaction tx = null;
		try {
			tx = new Transaction();
			tx.start();
			String tableName = "";
			if("PRO".equals(type)) {
				tableName = "GLCHECKRECORDLIB";
			}else if("TABLE".equals(type)){
				tableName = "GLTABLENAME";
			}
			List<String> treeList = new ArrayList<String>();
			List<String> recordList = new ArrayList<String>();
			DBConnUtil conn = null;
			ResultSet rs = null;
			try {
				conn = new DBConnUtil();
				String qSql = "select * from GLCHECKRECORDTREE where gwkeyid = '" + gwkeyid + "'";
				rs = conn.executeQuery(qSql);
				while(rs.next()) {
					DBConnUtil connUtil = null;
					ResultSet resultSet = null;
					try {
						connUtil = new DBConnUtil();
						String id = rs.getString("GWKEYID");
						treeList.add(id);
						String sql = "select * from "+tableName+" WHERE TREEID = '" + id + "'";
						resultSet = connUtil.executeQuery(sql);
						while(resultSet.next()) {
							String recordId = resultSet.getString("GWKEYID");
							recordList.add(recordId);
						}
						queryCons(id,tableName,treeList,recordList);
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
				map.put("GLCHECKRECORDTREE", treeList);
				map.put(tableName, recordList);
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
			tx.commit();
			tx = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (tx != null)
				tx.rollback();
		}
		return map;
	}

	private static void queryCons(String pid, String tableName, List<String> treeList, List<String> recordList) {
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
					treeList.add(id);
					String sql = "select * from " + tableName + " WHERE TREEID = '" + id + "'";
					resultSet = connUtil.executeQuery(sql);
					while(resultSet.next()) {
						String gwkeyid = resultSet.getString("GWKEYID");
						recordList.add(gwkeyid);
					}
					queryCons(id,tableName,treeList,recordList);
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
}

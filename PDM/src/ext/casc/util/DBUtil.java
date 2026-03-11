package ext.casc.util;

import ext.casc.product.model.Batch;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DBUtil {

	public static List<Batch> getBatchesByProduct(String productOid,String productName){
		List<Batch> list = new ArrayList<Batch>();
        DBConn conn = null;

        try {
            conn = new DBConn();
            StringBuffer selectSQL = new StringBuffer();
            selectSQL.append("select re.oid,re.PRODUCTOID,re.NAME from ASES_BATCHES_TABLE re where re.PRODUCTOID='"+productOid+"' ");

            ResultSet resultset = conn.executeQuery(selectSQL.toString());
            int no = 1;
            while (resultset.next()) {
                String oid = resultset.getString(1);
                String proOid = resultset.getString(2);
                String name = resultset.getString(3);
                Batch  batch = new Batch();
                batch.setOid(oid);
                batch.setNo(String.valueOf(no++));
                batch.setProductOid(proOid);
                batch.setProductName(productName);
                batch.setName(name);
                list.add(batch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
		return list;
	}

	public static void deleteBatch(String oid) {
        DBConn conn = null;
        try {
            conn = new DBConn();
            StringBuffer sql = new StringBuffer();
            sql.append("DELETE FROM ASES_BATCHES_TABLE re where re.oid='"+oid+"'");
            conn.executeUpdate(sql.toString());
            conn.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

	}

	public static void addBatch(String productOid,String name) {
        DBConn conn = null;
        try {
            conn = new DBConn();
            String oid = String.valueOf(System.currentTimeMillis());
            StringBuffer sql = new StringBuffer();
            sql.append("INSERT INTO ASES_BATCHES_TABLE values('"+oid+"','"+productOid+"','"+name+"')");
            conn.executeUpdate(sql.toString());
            conn.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
	}


    public  static void deleteAll(String tableName) {
        DBConn conn = null;
        try {
            conn = new DBConn();
            String sql = "delete from "+tableName;
            conn.executeUpdate(sql);
            conn.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public  static void deleteBySql(String sql) {
        DBConn conn = null;
        try {
            conn = new DBConn();
            conn.executeUpdate(sql);
            conn.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public static void deleteByTableAndKey(String tableName, String key,String value ) {
    	DBConn conn = null;
        try {
            conn = new DBConn();
            String sql = "delete from "+tableName +" where "+key+" =  '"+value+"'";
            conn.executeUpdate(sql);
            conn.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    public static void batchDeleteByTableInKey(String tableName, String key,String values) {
        DBConn conn = null;
        try {
            conn = new DBConn();
            String sql = "delete from "+tableName +" where "+key+" in ("+values+")";
            conn.executeUpdate(sql);
            conn.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean existData(String tableName, Map<String, String> paramsMap2) {
        DBConn conn = null;
        try {
            conn = new DBConn();
            StringBuilder sqlBuilder =new StringBuilder("select count(1) as totalCount from "+tableName );
            Set<Map.Entry<String, String>> entrys =  paramsMap2.entrySet();
            boolean firstAppend = true;
            for(Map.Entry entry:entrys){
                if(firstAppend){
                    sqlBuilder.append(" where ");
                }else{
                    sqlBuilder.append(" and ");
                }
                sqlBuilder.append(entry.getKey());
                sqlBuilder.append("='");
                sqlBuilder.append(entry.getValue());
                sqlBuilder.append("'");
                firstAppend = false;

            }
            ResultSet resultSet = conn.executeQuery(sqlBuilder.toString());
            if(resultSet.next()){
               int count =  resultSet.getInt("totalCount");
               if(count>0){
                   return true;
               }else{
                   return false;
               }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

	public static void updateByParams(String tableName, Map<String, Object> setValue, Map<String, Object> where) {
		DBConn conn = null;
        try {
            conn = new DBConn();
            StringBuilder sql = new StringBuilder("UPDATE ");
            sql.append(tableName);
            sql.append(" SET ");
            Set<Map.Entry<String, Object >> set = setValue.entrySet();
            for(Map.Entry<String, Object > e:set){
            	sql.append(e.getKey());
            	sql.append("=");
            	if(e.getValue() instanceof Integer){
                	sql.append(e.getValue());
            	}else{
            		sql.append("'");
                	sql.append(e.getValue());
                	sql.append("'");
            	}
            	sql.append(",");
            }
            sql = sql.deleteCharAt(sql.length()-1);

            sql.append(" WHERE ");
            Set<Map.Entry<String, Object >> whereSet = where.entrySet();
            int index = 0;
            for(Map.Entry<String, Object > e:whereSet){
            	index++;
            	sql.append(e.getKey());
            	sql.append("=");
            	if(e.getValue() instanceof Integer){
                	sql.append(e.getValue());
            	}else{
            		sql.append("'");
                	sql.append(e.getValue());
                	sql.append("'");
            	}
            	if(index<where.size()){
            		sql.append(" AND ");
            	}

            }
            conn.executeUpdate(sql.toString());
            conn.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

	}

    public static boolean existData(Class clazz, String column, String oid) throws Exception {
        CmQuerySpec querySpec = new CmQuerySpec(clazz);
        querySpec.appendWhere(column,CmQuerySpec.EQUAL,oid);
        CmQueryResult qr = CmPersistenceHelper.manager.find(querySpec);
        if(qr.hasNext()){
            return true;
        }
        return false;
    }
}

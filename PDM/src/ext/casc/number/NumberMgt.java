package ext.casc.number;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.ResultSet;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import ext.casc.util.DBConn;

public class NumberMgt  implements RemoteAccess {
	public static int BBLGY = 1;
	public static int GYFA = 2;
	/**
	 *
	 * @param type 1:报表类工艺;2:工艺方案
	 * @param s
	 * @return
	 */
	public static synchronized long getNumber(Integer type,String pre){
		if (!RemoteMethodServer.ServerFlag) {
            String method = "getNumber";
            Class[] types = {Integer.class,String.class };
            Object[] vals = {type,pre };
            RemoteMethodServer rms = RemoteMethodServer.getDefault();
            try {
				return (Long)rms.invoke(method, NumberMgt.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		StringBuilder sql = new StringBuilder("select NUM FROM ");
		StringBuilder isql = new StringBuilder("insert into ");
		StringBuilder usql = new StringBuilder("update ");

		if(BBLGY==type){
			sql.append("GL_BBLGY_SEQ ").append("WHERE PRE='").append(pre).append("'");
			isql.append(" GL_BBLGY_SEQ (PRE,NUM) values ('").append(pre).append("',");
			usql.append(" GL_BBLGY_SEQ set NUM=");
		}else if(GYFA==type){
			sql.append("GL_GYFA_SEQ ").append("WHERE PRE='").append(pre).append("'");
			isql.append(" GL_GYFA_SEQ (PRE,NUM) values ('").append(pre).append("',");
			usql.append(" GL_GYFA_SEQ set NUM=");
		}
		DBConn conn = null;
		long num = 0;
		//long isUsed = 0;
		try {
			conn = new DBConn();
			ResultSet rs = conn.executeQuery(sql.toString());
			if(rs.next()){
				num = rs.getLong("NUM");
				//isUsed = rs.getLong("ISUSED");
			}
			if(num == 0 ){
				num = 1001;
				isql.append(num).append(")");
				conn.executeUpdate(isql.toString());
			}else{
				num = num+1;
				usql.append(num).append(" WHERE PRE='").append(pre).append("'");
				conn.executeUpdate(usql.toString());
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return num;
	}

	/**
	 *
	 * @param type 1:报表类工艺;2:工艺方案
	 * @param s
	 * @return
	 */
	public static synchronized long getNumber(Integer type,String pre,long first){
		if (!RemoteMethodServer.ServerFlag) {
           String method = "getNumber";
           Class[] types = {Integer.class,String.class };
           Object[] vals = {type,pre };
           RemoteMethodServer rms = RemoteMethodServer.getDefault();
           try {
				return (Long)rms.invoke(method, NumberMgt.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       }
		StringBuilder sql = new StringBuilder("select NUM FROM ");
		StringBuilder isql = new StringBuilder("insert into ");
		StringBuilder usql = new StringBuilder("update ");

		if(BBLGY==type){
			sql.append("GL_BBLGY_SEQ ").append("WHERE PRE='").append(pre).append("'");
			isql.append(" GL_BBLGY_SEQ (PRE,NUM) values ('").append(pre).append("',");
			usql.append(" GL_BBLGY_SEQ set NUM=");
		}else if(GYFA==type){
			sql.append("GL_GYFA_SEQ ").append("WHERE PRE='").append(pre).append("'");
			isql.append(" GL_GYFA_SEQ (PRE,NUM) values ('").append(pre).append("',");
			usql.append(" GL_GYFA_SEQ set NUM=");
		}
		DBConn conn = null;
		long num = 0;
		//long isUsed = 0;
		try {
			conn = new DBConn();
			ResultSet rs = conn.executeQuery(sql.toString());
			if(rs.next()){
				num = rs.getLong("NUM");
				//isUsed = rs.getLong("ISUSED");
			}
			if(num == 0 ){
				num = first;
				isql.append(num).append(")");
				conn.executeUpdate(isql.toString());
			}else{
				num = num+1;
				usql.append(num).append(" WHERE PRE='").append(pre).append("'");
				conn.executeUpdate(usql.toString());
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return num;
	}

	public static void main(String[] args) {
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		methodServer.setUserName("wcadmin");
		methodServer.setPassword("wcadmin");
		 Class[] types = {Integer.class,String.class };
         Object[] vals = {1,"TEST2" };
		try {
			methodServer.invoke("getNumber", NumberMgt.class.getName(), null,types, vals);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}

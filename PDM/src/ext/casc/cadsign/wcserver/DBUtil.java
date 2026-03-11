package ext.casc.cadsign.wcserver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import wt.method.RemoteAccess;
import wt.pds.oracle81.OracleDataSource;

/**
 * <p>Description:  </p>
 * @version 1.0
 */

public class DBUtil implements RemoteAccess{
	
	private static Connection conn = null;
	private static Statement state = null;
	private static ResultSet rs = null;
	
	public static void execute(String sql){ 
		try {
			 conn = OracleDataSource.getOracleDataSource().getConnection();
			 state = conn.createStatement();
			 state.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	public static ResultSet query(String sql){ 
		try {
			 conn = OracleDataSource.getOracleDataSource().getConnection();
			 state = conn.createStatement();
			 rs = state.executeQuery(sql);
			 return rs; 
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	public static void close() throws Exception{
		if(rs != null){
			rs.close();
		}
		if(state != null){
			state.close();
		}
		if(conn != null && !conn.isClosed()){
			conn.close();
		}
	}
}

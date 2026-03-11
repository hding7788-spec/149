package ext.sast.navigation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.session.SessionServerHelper;

public class GLClassificationNodeService {

	public static boolean createGLClassificationNode(GLClassificationNode node){
		boolean rst = false;
		PreparedStatement pstmt = null;
		WTConnection wtconnection = null;
		Connection con = null;
		StringBuffer sqlSB = new StringBuffer();
		sqlSB.append("insert into GLClassificationNode(id, parentId,glnumber)");
		sqlSB.append("values(?, ?,?) ");
		try {
			SessionServerHelper.manager.setAccessEnforced(false);
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
		    con = wtconnection.getConnection();
		    con.setAutoCommit(false);
			pstmt = con.prepareStatement(sqlSB.toString());
			pstmt.setLong(1, node.getId());
			pstmt.setLong(2, node.getParentId());
			pstmt.setString(3, node.getNumber());
			// 执行批量插入操作
			int num =  pstmt.executeUpdate();
			if(num>=1){
				rst = true;
			}
			con.commit();
			con =null;
			//wtconnection.releaseAll();
			//wtconnection = null;
		} catch (Exception ex) {
			rst = false;
			try {
				con.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ex.printStackTrace();
			SessionServerHelper.manager.setAccessEnforced(true);
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		return rst;
	}
}

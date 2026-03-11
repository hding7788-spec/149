package ext.casc.doc.technology;

import com.glaway.mpm.util.DBConnUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TechnicsTechnologyTreeHander {

	public static List<TechnologyBean> search(String fatherCname, String typeStatus){
		List<TechnologyBean> list = new ArrayList<TechnologyBean>();
		if(fatherCname == null || "".equals(fatherCname)){
			return list;
		}
		DBConnUtil conn = null;
		TechnologyBean technologyBean = null;
		try {
			conn= new DBConnUtil();
			String sql = "select KEYID,FATHERENAME,FATHERCNAME,CHILDNUMBER,CHILDNAME,STATUS from GL_TECHNICSTECHNOLOGY where FATHERCNAME='"+fatherCname+"'";
			if(typeStatus != null && !"".equals(typeStatus)){
				sql += " and STATUS='"+typeStatus+"'";
			}
			sql += " order by CHILDNUMBER";
			ResultSet rs = conn.executeQuery(sql);
			while(rs.next()){
				String keyId = rs.getString("KEYID");
				String fEname = rs.getString("FATHERENAME");
				String fCname = rs.getString("FATHERCNAME");
				String childNumber = rs.getString("CHILDNUMBER");
				String childName = rs.getString("CHILDNAME");
				String status = rs.getString("STATUS");
				technologyBean = new TechnologyBean();
				technologyBean.setKeyid(keyId);
				technologyBean.setFatherEname(fEname);
				technologyBean.setFatherCname(fCname);
				technologyBean.setChildNumber(childNumber);
				technologyBean.setChildName(childName);
				technologyBean.setStatus(status);
				list.add(technologyBean);
			}
			conn.commit();
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
	public static void insert(String keyId, String fatherEname, String fatherCname, String childNumber){
		DBConnUtil conn = null;
		try {
			conn= new DBConnUtil();
			String sql = "insert into GL_TECHNICSTECHNOLOGY (KEYID,FATHERENAME,FATHERCNAME,CHILDNUMBER,STATUS) values ('"+keyId+"','"+fatherEname+"','"+fatherCname+"','"+childNumber+"','0')";
			conn.executeUpdate(sql);
			conn.commit();
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
	}

	/**
	 * 方法功能: 更新状态
	 *
	 * @param fatherEname
	 * @param childNumber
	 * @param status
	 * @return void
	 * @author LB
	 * @date 2020/9/6
	 */
	public static void updateStatus(String fatherEname, String childNumber, String status){
		DBConnUtil conn = null;
		try {
			conn= new DBConnUtil();
			String sql = "update GL_TECHNICSTECHNOLOGY set STATUS='"+status+"' where FATHERENAME='"+fatherEname+"' and CHILDNUMBER='"+childNumber+"'";
			conn.executeUpdate(sql);
			conn.commit();
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
	}
	public static void update(String fatherCname, String childNumber, String childName){
		DBConnUtil conn = null;
		try {
			conn= new DBConnUtil();
			String sql = "update GL_TECHNICSTECHNOLOGY set CHILDNAME='"+childName+"' where FATHERCNAME='"+fatherCname+"' and CHILDNUMBER='"+childNumber+"'";
			conn.executeUpdate(sql);
			conn.commit();
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
	}
	public static String getChildNumber(String fatherCname, String childName){
		DBConnUtil conn = null;
		String childNumber = "";
		try {
			conn= new DBConnUtil();
			String sql = "select CHILDNUMBER from GL_TECHNICSTECHNOLOGY where FATHERCNAME='"+fatherCname+"' and CHILDNAME='"+ childName +"'";
			ResultSet rs = conn.executeQuery(sql);
			while(rs.next()){
				childNumber = rs.getString("CHILDNUMBER");
			}
			conn.commit();
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
		return childNumber;
	}
	public static String getChildName(String fatherCname, String childNumber){
		DBConnUtil conn = null;
		String childName = "";
		try {
			conn= new DBConnUtil();
			String sql = "select CHILDNAME from GL_TECHNICSTECHNOLOGY where FATHERCNAME='"+fatherCname+"' and CHILDNUMBER='"+ childNumber +"'";
			ResultSet rs = conn.executeQuery(sql);
			while(rs.next()){
				childName = rs.getString("CHILDNAME");
			}
			conn.commit();
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
		return childName;
	}

	public static String getStatus(String fatherEname, String childNumber){
		DBConnUtil conn = null;
		String status = "";
		try {
			conn= new DBConnUtil();
			String sql = "select STATUS from GL_TECHNICSTECHNOLOGY where FATHERENAME='"+fatherEname+"' and CHILDNUMBER='"+ childNumber +"'";
			ResultSet rs = conn.executeQuery(sql);
			while(rs.next()){
				status = rs.getString("STATUS");
			}
			conn.commit();
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
		return status;
	}

	public static String getFatherEname(String fatherCname){
		String fatherEname = "";
		if("工艺分析策划总结".equals(fatherCname)){
			fatherEname = "GONGYIFENXICEHUAZONGJIE";
		}
		if("工艺定型".equals(fatherCname)){
			fatherEname = "GONGYIDINGXING";
		}
		if("工艺鉴定".equals(fatherCname)){
			fatherEname = "GONGYIJIANDING";
		}
		if("技术课题".equals(fatherCname)){
			fatherEname = "JISHUKETI";
		}
		if("总体测发报告".equals(fatherCname)){
			fatherEname = "TEST_REPORT";
		}
		return fatherEname;
	}
	public static String getFatherCname(String fatherEname){
		String fatherCname = "";
		if(fatherEname.contains("GONGYIFENXICEHUAZONGJIE")){
			fatherCname = "工艺分析策划总结";
		}
		if(fatherEname.contains("GONGYIDINGXING")){
			fatherCname = "工艺定型";
		}
		if(fatherEname.contains("GONGYIJIANDING")){
			fatherCname = "工艺鉴定";
		}
		if(fatherEname.contains("JISHUKETI")){
			fatherCname = "技术课题";
		}
		if(fatherEname.contains("TEST_REPORT")){
			fatherCname = "总体测发报告";
		}
		return fatherCname;
	}
}

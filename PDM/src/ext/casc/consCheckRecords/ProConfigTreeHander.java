package ext.casc.consCheckRecords;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.glaway.mpm.util.DBConnUtil;

public class ProConfigTreeHander {
	public static List<ProConfigBean> search() {
		List<ProConfigBean> list = new ArrayList<ProConfigBean>();
		DBConnUtil conn = null;
		ProConfigBean proConfigBean = null;
		try {
			conn = new DBConnUtil();
			String sql = "select * from GLCHECKRECORDLIB";
			ResultSet rs = conn.executeQuery(sql);
			while (rs.next()) {
				String id = rs.getString("GWKEYID");
				String value = rs.getString("VALUE");
				String type = rs.getString("TYPE");
				proConfigBean = new ProConfigBean(id, value, type);
				list.add(proConfigBean);
			}
			//conn.commit();
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

	public static void insert(String id) {
		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();
			String sql = "insert into GLCheckRecordLib (GWKEYID) values ('" + id + "')";
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

	public static void delete(String id) {
		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();
			String sql = "delete from GLCheckRecordLib where GWKEYID ='" + id + "'";
			conn.executeQuery(sql);
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

	public static void update(String id, String value, String type) {
		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();
			String sql = "update GLCheckRecordLib set VALUE='" + value + "',TYPE='" + type + "' where GWKEYID='" + id + "'";
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
}

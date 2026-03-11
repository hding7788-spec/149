package ext.casc.consCheckRecords;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.glaway.mpm.util.DBConnUtil;

public class TableConfigTreeHander {
	public static List<TableConfigBean> search() {
		List<TableConfigBean> list = new ArrayList<TableConfigBean>();
		DBConnUtil conn = null;
		TableConfigBean tableConfigBean = null;
		try {
			conn = new DBConnUtil();
			String sql = "select * from GLTableName";
			ResultSet rs = conn.executeQuery(sql);
			while (rs.next()) {
				String id = rs.getString("GWKEYID");
				String name = rs.getString("NAME");
				tableConfigBean = new TableConfigBean(id, name);
				list.add(tableConfigBean);
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
			String sql = "insert into GLTableName (GWKEYID) values ('" + id + "')";
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
			String sql = "delete from GLTableName where GWKEYID ='" + id + "'";
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

	public static void update(String id, String name) {
		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();
			String sql = "update GLTableName set NAME='" + name + "' where GWKEYID ='" + id + "'";
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

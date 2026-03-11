package com.glaway.mpm.print;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.glaway.mpm.util.DBConnUtil;

public class PrintRecordProcessor {

	public static Map<String, String> queryUserInfoByCode(String userCode) {
		Map<String, String> map = null;
		if (userCode != null && !"".equals(userCode)) {
			String sql = "select * from GWUSERCODETABLE where usercode='"+ userCode +"'";
			DBConnUtil conn = null;
			try {
				conn= new DBConnUtil();
				ResultSet rt = conn.executeQuery(sql);
				if(rt.next()) {
					String userOid = rt.getString("useroid");
					String userName = rt.getString("username");
					String fullName = rt.getString("fullname");
					String dept = rt.getString("dept");
					map = new HashMap<String, String>();
					map.put("fullName", fullName);
					map.put("dept", dept);
					map.put("oid", userOid);
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
		}
		return map;
	}

}

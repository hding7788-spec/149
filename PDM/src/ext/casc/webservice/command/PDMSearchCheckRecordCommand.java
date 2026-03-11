/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command;

import com.glaway.mpm.util.DBConnUtil;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 类功能：检验记录结构树查询
 *
 * @author cjh
 * @date 2022/12/7
 */

public class PDMSearchCheckRecordCommand implements WebServiceCommand, InitializingBean {
	// 方法标识
	public static final String METHOD_NAME = "searchCheckRecord";

	@Override
	public String execute(String params) {
		String errorMsg = null;
		JSONObject msg = new JSONObject();
		DBConnUtil conn1 = null;
		DBConnUtil conn2 = null;
		DBConnUtil conn3 = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		try {
			conn1 = new DBConnUtil();
			conn2 = new DBConnUtil();
			conn3 = new DBConnUtil();
			String sql1 = "select * from GLCHECKRECORDTREE";
			String sql2 = "select * from GLCHECKRECORDLIB";
			String sql3 = "select * from GLTABLENAME";
			rs1 = conn1.executeQuery(sql1);
			rs2 = conn2.executeQuery(sql2);
			rs3 = conn3.executeQuery(sql3);
			JSONArray treeArray = new JSONArray();
			JSONArray xmmArray = new JSONArray();
			JSONArray tbmArray = new JSONArray();
			while (rs1.next()) {
				JSONObject tree = new JSONObject();
				tree.put("GWKEYID",rs1.getString("GWKEYID"));
				tree.put("VALUE",rs1.getString("VALUE"));
				tree.put("PID",rs1.getString("PID"));
				treeArray.put(tree);
			}
			while (rs2.next()) {
				JSONObject xmm = new JSONObject();
				xmm.put("GWKEYID",rs2.getString("GWKEYID"));
				xmm.put("VALUE",rs2.getString("VALUE"));
				xmm.put("TYPE",rs2.getString("TYPE"));
				xmm.put("TREEID",rs2.getString("TREEID"));
				xmmArray.put(xmm);
			}
			while (rs3.next()) {
				JSONObject tbm = new JSONObject();
				tbm.put("GWKEYID",rs3.getString("GWKEYID"));
				tbm.put("NAME",rs3.getString("NAME"));
				tbm.put("TREEID",rs3.getString("TREEID"));
				tbmArray.put(tbm);
			}
			msg.put("TREE", treeArray);
			msg.put("ItemName", xmmArray);
			msg.put("TableName", tbmArray);
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = "检验记录结构树查询失败";
		} finally {
			try {
				if (rs1 != null) {
					rs1.close();
				}
				if (rs2 != null) {
					rs2.close();
				}
				if (rs3 != null) {
					rs3.close();
				}
				if (conn1 != null) {
					conn1.close();
				}
				if (conn2 != null) {
					conn2.close();
				}
				if (conn3 != null) {
					conn3.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		JSONObject rtnMsgObj = new JSONObject();
		try {
			if(errorMsg!=null&&!"".equals(errorMsg)){
				rtnMsgObj.put("status", "N");
				rtnMsgObj.put("result", errorMsg);
			}else{
				rtnMsgObj.put("status", "Y");
				rtnMsgObj.put("result", msg.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return rtnMsgObj.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}
}

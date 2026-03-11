/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.e3;

import ext.casc.util.DBConn;
import ext.casc.util.Tools;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.log4j.LogR;

import java.sql.ResultSet;
import java.sql.SQLException;


public class GetZykPartCommand implements WebServiceCommand, InitializingBean {
	private static final Logger LOGGER = LogR.getLogger(GetZykPartCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "getZykParts";
	@Override
	public String execute(String params) {
		String rtnCode = "E";
		String rtnMsg="";
		JSONObject jrtnObj = new JSONObject();

		JSONObject jparams;
		DBConn conn = null;
		ResultSet rs = null;
		try {
			jparams = new JSONObject(params);
			String xhgg =jparams.optString("xhgg");//like
			String zldj =jparams.optString("zldj");
			String xxgf =jparams.optString("xxgf");
			String mtype =jparams.optString("mtype");
			StringBuilder  sqlBuilder  = new StringBuilder("select * from QUERYMIDDLE_TABLE where  BMZT='启用' ") ;
			if(!Tools.isNull(mtype)){
				sqlBuilder.append(" AND ");
				sqlBuilder.append(" mtype='");
				sqlBuilder.append(mtype);
				sqlBuilder.append("' ");
			}
			if(!Tools.isNull(xhgg)){
				sqlBuilder.append(" AND ");
				sqlBuilder.append(" TYPESTANDARD LIKE '%");
				sqlBuilder.append(xhgg);
				sqlBuilder.append("%' ");
			}
			if(!Tools.isNull(zldj)){
				sqlBuilder.append(" AND ");
				sqlBuilder.append(" QUALITYLEVEL='");
				sqlBuilder.append(zldj);
				sqlBuilder.append("' ");
			}
			if(!Tools.isNull(xxgf)){
				sqlBuilder.append(" AND ");
				sqlBuilder.append(" DETAILSTANDARD='");
				sqlBuilder.append(xxgf);
				sqlBuilder.append("' ");
			}
			conn = new DBConn();
			rs = conn.executeQuery(sqlBuilder.toString());
			JSONArray jsonArray = new JSONArray();

			while (rs.next()) {
				String partNumber = rs.getString("WTPARTNUMBER");
				String partName = rs.getString("NAME");
				String shortName = rs.getString("SHORTNAME");
				String typeStandard = rs.getString("TYPESTANDARD");
				String qualityLevel = rs.getString("QUALITYLEVEL");
				String detailStandard = rs.getString("DETAILSTANDARD");
				String supplier = rs.getString("GYS");
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("partNumber",partNumber);
				jsonObject.put("partName",partName);
				jsonObject.put("shortName",shortName);
				jsonObject.put("xhgg",typeStandard);
				jsonObject.put("zldj",qualityLevel);
				jsonObject.put("xxgf",detailStandard);
				jsonObject.put("sccj",supplier);
				jsonArray.put(jsonObject);
			}
			jrtnObj.put(WSConstants.RTN_DATA, jsonArray);
			if(jsonArray.length()>0){
				rtnCode = "S";
				rtnMsg = "找到所有数据信息";
			}else{
				rtnCode = "N";
				rtnMsg = "没找到相应的数据";
			}

		} catch (JSONException e) {
			rtnMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
			LOGGER.error(rtnMsg, e);
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			jrtnObj.put(WSConstants.RTN_MSG, rtnMsg);
			jrtnObj.put(WSConstants.RTN_CODE, rtnCode);
		} catch (JSONException ex) {
			LOGGER.error("Error building JSON: ", ex);
		}

		return jrtnObj.toString();
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}
}

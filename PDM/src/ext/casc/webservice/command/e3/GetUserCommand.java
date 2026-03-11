/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.e3;


import ext.casc.util.CSCPrincipal;
import ext.casc.util.DBConn;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.org.WTUser;

import java.sql.ResultSet;


public class GetUserCommand implements WebServiceCommand, InitializingBean {
	static Logger LOGGER = Logger.getLogger(GetUserCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "getUser";
	@Override
	public String execute(String params) {
		String rtnCode = "E";
		String rtnMsg="";
        JSONObject jrtnObj = new JSONObject();

		JSONObject jparams;
		JSONObject userInfo =  null;
		try {
			jparams = new JSONObject(params);
			String userName =jparams.optString("userName");
			WTUser user= CSCPrincipal.getUserByName(userName);
			if(user!=null){
				rtnCode = "S";
				rtnMsg = "存在该用户";
				userInfo = new JSONObject();
				userInfo.put("oid","wt.org.WTUser:"+user.getPersistInfo().getObjectIdentifier().getId());
				userInfo.put("name",user.getName());
				userInfo.put("fullName",user.getFullName());

				DBConn dbConn = new DBConn();
				try {
					String sql = "select PASSWORD from TCUSERACCESS where USERID = '" + user.getName()
							+ "'  ORDER BY RESETDATE DESC";
					ResultSet rs = dbConn.executeQuery(sql);
					if (rs.next()) {
						userInfo.put("password", rs.getString("PASSWORD"));
					}
				}catch (Exception e){
					e.printStackTrace();
				}finally {
					dbConn.close();
				}

			}else{
				rtnCode = "N";
				rtnMsg = "没找到相应的用户";
			}

		} catch (JSONException e) {
			rtnMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
			LOGGER.error(rtnMsg, e);
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
	           jrtnObj.put(WSConstants.RTN_MSG, rtnMsg);
	           jrtnObj.put(WSConstants.RTN_CODE, rtnCode);
			   jrtnObj.put("userInfo", userInfo);
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

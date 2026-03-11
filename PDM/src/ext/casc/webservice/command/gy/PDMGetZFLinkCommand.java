/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.gy;

import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import ext.casc.constants.Constants;
import ext.casc.util.IBAHelper;
import ext.casc.util.Tools;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import wt.doc.WTDocument;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 类功能：主辅工艺关联关系查询
 *
 * @author cjh
 * @date 2022/11/28
 */
@Component
public class PDMGetZFLinkCommand implements WebServiceCommand, InitializingBean {
	// 方法标识
	public static final String METHOD_NAME = "getZFLink";

	@Override
	public String execute(String params) {
		String errorMsg = null;
		JSONArray msg = new JSONArray();
		JSONObject jparams = null;
		try {
			jparams = new JSONObject(params);
		} catch (JSONException e) {
			errorMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
		}
		String number = jparams.getString("number");
		String version = jparams.getString("version");

		if(!Tools.isNull(number)){
			if(version == null || "".equals(version) || "null".equals(version)){
				DBConnUtil vconn = null;
				try {
					vconn = new DBConnUtil();
					String sql = "select distinct ZZTECHNICSVERSION from GL_ZHUFULINK where ZZTECHNICSNUMBER = '"+number+"'";
					ResultSet rs = vconn.executeQuery(sql);
					List<String> list = new ArrayList<String>();
					while (rs.next()) {
						String zztechnicsversion = rs.getString("ZZTECHNICSVERSION");
						list.add(zztechnicsversion);
					}
					if(list.size()>1){
						Collections.sort(list, new Comparator<String>() {
							public int compare(String o1, String o2) {
								if(o1.equals("space")){
									o1 = "A";
								}
								if(o2.equals("space")){
									o2 = "A";
								}
								return o2.compareTo(o1);
							}
						});
					}
					if(list.size() > 0){
						version = list.get(0);
					}
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
					errorMsg = "主辅工艺关联关系查询失败";
				} finally {
					try {
						if (vconn != null) {
							vconn.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			if(!"".equals(version) && !"null".equals(version)) {
				if(version.contains(".")){
					String[] ss = version.split("\\.");
					version=ss[0];
				}
				DBConnUtil conn = null;
				try {
					conn = new DBConnUtil();
					String sql = "select distinct FZTECHNICSNUMBER,FZTECHNICSVERSION from GL_ZHUFULINK where ZZTECHNICSNUMBER = '"+number+"' and ZZTECHNICSVERSION = '"+version+"'";
					ResultSet rs = conn.executeQuery(sql);
					Map<String,List<String>> map = new HashMap<String, List<String>>();
					while (rs.next()) {
						String fztechnicsnumber = rs.getString("FZTECHNICSNUMBER");
						String fztechnicsversion = rs.getString("FZTECHNICSVERSION");
						if(map.containsKey(fztechnicsnumber)){
							List<String> stringList = map.get(fztechnicsnumber);
							stringList.add(fztechnicsversion);
							map.remove(fztechnicsnumber);
							map.put(fztechnicsnumber, stringList);
						}else{
							List<String> stringList = new ArrayList<String>();
							stringList.add(fztechnicsversion);
							map.put(fztechnicsnumber, stringList);
						}
					}
					if(!map.isEmpty()){
						for(String fzNumber : map.keySet()) {
							List<String> versonList = map.get(fzNumber);
							if(versonList.size()>1){
								Collections.sort(versonList, new Comparator<String>() {
									public int compare(String o1, String o2) {
										if(o1.equals("space")){
											o1 = "A";
										}
										if(o2.equals("space")){
											o2 = "A";
										}
										return o2.compareTo(o1);
									}
								});
							}
							String laestVersion = versonList.get(0);
							DBConnUtil connUtil = null;
							try {
								connUtil = new DBConnUtil();
								String query = "select * from GL_ZHUFULINK where ZZTECHNICSNUMBER = '"+
										number+"' and ZZTECHNICSVERSION = '"+version+"' and FZTECHNICSVERSION = '"+
										laestVersion+"' and FZTECHNICSNUMBER ='"+fzNumber+"'";
								ResultSet resultSet = connUtil.executeQuery(query);
								while (resultSet.next()) {
									JSONObject link = new JSONObject();
									String fztechnicsnumber = resultSet.getString("FZTECHNICSNUMBER");
									String fztechnicsversion = resultSet.getString("FZTECHNICSVERSION");
									String zztechnicsnumber = resultSet.getString("ZZTECHNICSNUMBER");
									String zztechnicsversion = resultSet.getString("ZZTECHNICSVERSION");
									WTDocument fzDocument = WTDocumentUtil.getLaestWTDocumentByNumberAndVersion(fztechnicsnumber, fztechnicsversion);
									WTDocument zzDocument = WTDocumentUtil.getLaestWTDocumentByNumberAndVersion(zztechnicsnumber, zztechnicsversion);
									if(fzDocument != null && zzDocument !=null){
										if(Constants.STATE_YIPIZHUN.equals(fzDocument.getState().getState().getDisplay(Locale.CHINA))
												&& Constants.STATE_YIPIZHUN.equals(zzDocument.getState().getState().getDisplay(Locale.CHINA))){
											link.put("FZTECHNICSPPNUMBER", IBAHelper.getIBAStringValue(fzDocument, "PPNUMBER"));
											link.put("FZTECHNICSNUMBER", fztechnicsnumber);
											link.put("FZTECHNICSVERSION", fztechnicsversion);
											link.put("FZPROCEDURENUMBER",resultSet.getString("FZPROCEDURENUMBER"));
											link.put("ZZTECHNICSPPNUMBER", IBAHelper.getIBAStringValue(zzDocument, "PPNUMBER"));
											link.put("ZZTECHNICSNUMBER", zztechnicsnumber);
											link.put("ZZTECHNICSVERSION", zztechnicsversion);
											link.put("ZZPROCEDURENUMBER",resultSet.getString("ZZPROCEDURENUMBER"));
											link.put("PICIHAO",resultSet.getString("PICIHAO"));
											msg.put(link);
										}
									}
								}
								resultSet.close();
							} catch (Exception e) {
								e.printStackTrace();
								errorMsg = "主辅工艺关联关系查询失败";
							} finally {
								try {
									if (connUtil != null) {
										connUtil.close();
									}
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
					}
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
					errorMsg = "主辅工艺关联关系查询失败";
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
		}else{
			errorMsg = "编号不能为空";
		}
		JSONObject rtnMsgObj = new JSONObject();
		try {
			if(errorMsg!=null&&!"".equals(errorMsg)){
				rtnMsgObj.put("status", "N");
				rtnMsgObj.put("result", errorMsg);
			}else{
				rtnMsgObj.put("status", "Y");
				rtnMsgObj.put("result", msg);
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

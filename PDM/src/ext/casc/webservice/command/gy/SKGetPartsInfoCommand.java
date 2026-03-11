/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.gy;

import ext.casc.part.CSCPart;
import ext.casc.part.cache.JsonObjectCache;
import ext.casc.util.DBConn;
import ext.casc.util.IBAHelper;
import ext.casc.util.Tools;
import ext.casc.version.VersionCommonHelper;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import wt.part.WTPart;
import wt.util.WTException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 类功能：主辅工艺关联关系查询
 *
 * @author cjh
 * @date 2022/11/28
 */
@Component
public class SKGetPartsInfoCommand implements WebServiceCommand, InitializingBean {
	// 方法标识
	public static final String METHOD_NAME = "getPartsInfo";

	@Override
	public String execute(String params) {
		String rtnCode ="S";
		String rtnMsg ="";
		JSONArray jparams = null;
		try {
			jparams = new JSONArray(params);
		} catch (JSONException e) {
			rtnMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
		}

		JSONObject jrtnObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < jparams.length(); i++) {
			JSONObject jparam = jparams.getJSONObject(i);
			String number = jparam.optString("number");
			if (!Tools.isNull(number)) {
				if(number.startsWith("A")||number.startsWith("B")||number.startsWith("C")||number.startsWith("D")||number.startsWith("E")||number.startsWith("F")||number.startsWith("G")){
					DBConn conn = null;
					ResultSet rs = null;
					try {
						JSONObject cacheJson = JsonObjectCache.queryMiddleTableCache.get(number);
						if (cacheJson == null) {
							StringBuilder sqlBuilder = new StringBuilder("select * from QUERYMIDDLE_TABLE where  WTPARTNUMBER='" + number + "' order by synchTime desc");
							conn = new DBConn();
							rs = conn.executeQuery(sqlBuilder.toString());
							if (rs.next()) {
								String partNumber = rs.getString("WTPARTNUMBER");
								String partName = rs.getString("NAME");
								JSONObject partJson = new JSONObject();
								partJson.put("partNumber",partNumber);
								partJson.put("partName",partName);
								partJson.put("dataFrom","资源库物资");
								partJson.put("XYML",Tools.convertNull(rs.getString("XYML")));
								partJson.put("STANDARDNUMBER",Tools.convertNull(rs.getString("STANDARDNUMBER")));
								partJson.put("SHORTNAME",Tools.convertNull(rs.getString("SHORTNAME")));
								partJson.put("CSIZE",Tools.convertNull(rs.getString("CSIZE")));
								partJson.put("CMAT",Tools.convertNull(rs.getString("CMAT")));
								partJson.put("MECHANICALPROPERTYORHARDNESS",Tools.convertNull(rs.getString("MECHANICALPROPERTYORHARDNESS")));
								partJson.put("SURFACETREATMENT",Tools.convertNull(rs.getString("SURFACETREATMENT")));
								partJson.put("HEATTREATMENT",Tools.convertNull(rs.getString("HEATTREATMENT")));
								partJson.put("PRODUCTFORM",Tools.convertNull(rs.getString("PRODUCTFORM")));
								partJson.put("PRODUCTLEVEL",Tools.convertNull(rs.getString("PRODUCTLEVEL")));
								partJson.put("PLATECSCREWFORM",Tools.convertNull(rs.getString("PLATECSCREWFORM")));
								partJson.put("ISIMPORT",Tools.convertNull(rs.getString("ISIMPORT")));
								partJson.put("SPECIALINSTRUCTION",Tools.convertNull(rs.getString("SPECIALINSTRUCTION")));
								partJson.put("MEASUREUNIT",Tools.convertNull(rs.getString("MEASUREUNIT")));
								partJson.put("CLASSIFICATIONNODE",Tools.convertNull(rs.getString("CLASSIFICATIONNODE")));
								partJson.put("YXJB",Tools.convertNull(rs.getString("YXJB")));
								partJson.put("BMZT",Tools.convertNull(rs.getString("BMZT")));
								partJson.put("BMLX",Tools.convertNull(rs.getString("BMLX")));
								partJson.put("TYPE",Tools.convertNull(rs.getString("TYPE")));
								partJson.put("TYPESTANDARD",Tools.convertNull(rs.getString("TYPESTANDARD")));
								partJson.put("QUALITYLEVEL",Tools.convertNull(rs.getString("QUALITYLEVEL")));
								partJson.put("TOTALSTANDARD",Tools.convertNull(rs.getString("TOTALSTANDARD")));
								partJson.put("DETAILSTANDARD",Tools.convertNull(rs.getString("DETAILSTANDARD")));
								partJson.put("PACKAGINGFORM",Tools.convertNull(rs.getString("PACKAGINGFORM")));
								partJson.put("OUTLINESIZE",Tools.convertNull(rs.getString("OUTLINESIZE")));
								partJson.put("SPECIALCONDITION",Tools.convertNull(rs.getString("SPECIALCONDITION")));
								partJson.put("EXTRACONDITION",Tools.convertNull(rs.getString("EXTRACONDITION")));
								partJson.put("MATTYPE",Tools.convertNull(rs.getString("MATTYPE")));
								partJson.put("RATEOFCONVERSION",Tools.convertNull(rs.getString("RATEOFCONVERSION")));
								partJson.put("RATIO",Tools.convertNull(rs.getString("RATIO")));
								partJson.put("MARKNUMBER",Tools.convertNull(rs.getString("MARKNUMBER")));
								partJson.put("USESTANDARD",Tools.convertNull(rs.getString("USESTANDARD")));
								partJson.put("VARIETYSTANDARD",Tools.convertNull(rs.getString("VARIETYSTANDARD")));
								partJson.put("QUALITYCHARACTER",Tools.convertNull(rs.getString("QUALITYCHARACTER")));
								partJson.put("PRECISION",Tools.convertNull(rs.getString("PRECISION")));
								partJson.put("SUPPLYSTATE",Tools.convertNull(rs.getString("SUPPLYSTATE")));
								partJson.put("GYS",Tools.convertNull(rs.getString("GYS")));
								partJson.put("BMDJ",Tools.convertNull(rs.getString("BMDJ")));
								partJson.put("KFZBTID",Tools.convertNull(rs.getString("KFZBTID")));
								partJson.put("KFZBSEE",Tools.convertNull(rs.getString("KFZBSEE")));
								partJson.put("XNCS",Tools.convertNull(rs.getString("XNCS")));
								partJson.put("JDMGDJ_STATE",Tools.convertNull(rs.getString("JDMGDJ_STATE")));
								partJson.put("JDMGDJ",Tools.convertNull(rs.getString("JDMGDJ")));
								partJson.put("SMDJ",Tools.convertNull(rs.getString("SMDJ")));
								partJson.put("SYNCHTIME",Tools.convertNull(rs.getString("SYNCHTIME")));
								partJson.put("CPDH",Tools.convertNull(rs.getString("CPDH")));
								partJson.put("ZL",Tools.convertNull(rs.getString("ZL")));
								partJson.put("TNT",Tools.convertNull(rs.getString("TNT")));
								partJson.put("ZCSM",Tools.convertNull(rs.getString("ZCSM")));
								partJson.put("DCSTXYQ","");
								jsonArray.put(partJson);

								JsonObjectCache.queryMiddleTableCache.put(number, partJson);
							}else{
								rtnMsg =rtnMsg +"【"+ number +"】在PDM系统不存在;";
							}
						} else {
							jsonArray.put(cacheJson);
						}

					} catch (Exception e) {
						rtnMsg =rtnMsg + number +"查询出错" + e.getLocalizedMessage()+";";
						rtnCode = "N";
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
				}else{
					WTPart part = CSCPart.getPartByNumberAndViewName(number, "Manufacturing");
					if(part!=null){
						JSONObject partJson = new JSONObject();
						partJson.put("partNumber",part.getNumber());
						partJson.put("partName",part.getName());
						partJson.put("version", VersionCommonHelper.getVersion(part));
						partJson.put("dataFrom","非资源库物资");
                        try {
                            partJson.put("CINDEX",Tools.convertNull(IBAHelper.getIBAStringValue(part,"CINDEX")));
							partJson.put("PHASE",Tools.convertNull(IBAHelper.getIBAStringValue(part,"PHASE")));
                        } catch (WTException e) {
                           e.printStackTrace();
                        }
						jsonArray.put(partJson);

					}else {
						rtnMsg =rtnMsg +"【"+ number +"】在PDM系统不存在;";
					}

				}

			}
		}

		try {
			jrtnObj.put(WSConstants.RTN_MSG, rtnMsg);
			jrtnObj.put(WSConstants.RTN_CODE, rtnCode);
			jrtnObj.put(WSConstants.DATA, jsonArray);

		} catch (JSONException ex) {
		}

		return jrtnObj.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}
}

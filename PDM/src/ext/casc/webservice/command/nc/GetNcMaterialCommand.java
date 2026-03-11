/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.nc;

import com.ptc.extend.ixb.CmExpImpSearchHelper;
import ext.casc.nc.NCMaterialHelper;
import ext.casc.webservice.WSConstants;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.doc.WTDocument;
import wt.log4j.LogR;


public class GetNcMaterialCommand implements WebServiceCommand, InitializingBean {
	private static final Logger LOGGER = LogR.getLogger(GetNcMaterialCommand.class.getName());
	// 方法标识
	public static final String METHOD_NAME = "getNcMaterial";

	@Override
	public String execute(String params) {
		String rtnCode ;
		String rtnMsg;
		JSONObject jrtnObj = new JSONObject();
		StringBuilder errorMsg = new StringBuilder("");
		JSONObject jparams;
		try {
			rtnCode = "S";
			if("".equals(params)){
				rtnCode = "N";
			}else {
				jparams = new JSONObject(params);
				String docNumber = jparams.getString("docNumber");
				String docVersion = jparams.getString("docVersion");
				String version = docVersion;
				if(docVersion.contains(".")){
					String[] ss = docVersion.split("\\.");
					version=ss[0];
				}
				WTDocument doc = (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumberVersion(WTDocument.class,docNumber,version);
				if (doc != null) {
					if(NCMaterialHelper.hasStructure(doc)){
						rtnCode = "E";
						errorMsg.append("编号："+docNumber + "版本："+docVersion+"的工艺已经生成过,请勿重新生成!");
					}else{
						if(NCMaterialHelper.structure(doc)){
							rtnCode = "S";
						}
					}

				}else{
					rtnCode = "N";
					errorMsg.append("编号："+docNumber + " 版本："+docVersion + " 的工艺在PDM系统中不存在!");
				}
			}
			if ("S".equals(rtnCode)) {
				rtnMsg = "生成成功";
			} else {
				rtnMsg = errorMsg.toString();
			}


		} catch (JSONException e) {
			rtnCode ="N";
			rtnMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
			LOGGER.error(rtnMsg, e);
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rtnCode ="N";
			rtnMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
			LOGGER.error(rtnMsg, e);
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

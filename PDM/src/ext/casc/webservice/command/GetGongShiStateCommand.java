/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command;


import com.glaway.mpm.processplan.helper.ZhuFuLinkUtil;
import ext.casc.doc.CSCDoc;
import ext.casc.util.IBAHelper;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import wt.doc.WTDocument;

import java.util.List;
import java.util.Map;

/**
 * 类功能：PBOM状态查询工时状态接口
 * @author yfn
 * @date 2025/7/2
 */
public class GetGongShiStateCommand implements WebServiceCommand, InitializingBean {

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(GetGongShiStateCommand.class);
	// 方法标识
	public static final String METHOD_NAME = "getGongShiState";

	@Override
	public String execute(String params) {

		logger.info("【getGongShiState】接口接收到参数：{}", params);

		JSONObject jrtnObj = new JSONObject();
		JSONObject jparams;

		try {
			jparams = new JSONObject(params);
			String docNumber = jparams.optString("number");
			String docVersion = jparams.optString("version");

			WTDocument doc;
			if (docVersion == null || docVersion.isEmpty()) {
				doc = CSCDoc.getDoc(docNumber);
			} else {
				doc = CSCDoc.getLatestDocByNumberAndVersion(docNumber, docVersion);
			}

			if (doc == null) {
				jrtnObj.put("code", "E");
				jrtnObj.put("message", "无法找到指定的工艺文件!");
				return jrtnObj.toString();
			}

			String gongShiState = getSafeJsonString(IBAHelper.getIBAStringValue(doc, "GongShiDingEState"));

			// 主工艺结构化封装
			JSONObject mainObj = new JSONObject();
			mainObj.put("number", docNumber);
			mainObj.put("version", docVersion);
			mainObj.put("state", gongShiState);
			jrtnObj.put("ZhuZhi", mainObj);

			// 辅制工艺列表
			List<Map<String, String>> fzList = ZhuFuLinkUtil.getFZGY(docNumber, docVersion);
			JSONArray fzArray = new JSONArray();
			for (Map<String, String> fzMap : fzList) {
				String fzNumber = fzMap.get("number");
				String fzVersion = fzMap.get("version");

				JSONObject fzObj = new JSONObject();


				WTDocument fzDoc = CSCDoc.getLatestDocByNumberAndVersion(fzNumber, fzVersion);
				if (fzDoc != null) {
					String fzState = getSafeJsonString(IBAHelper.getIBAStringValue(fzDoc, "GongShiDingEState"));
					fzObj.put("number", fzNumber);
					fzObj.put("version", fzVersion);
					fzObj.put("state", fzState);
					fzArray.put(fzObj);
				}
			}
			jrtnObj.put("FuZhuList", fzArray);

			jrtnObj.put("code", "S");
		} catch (Exception e) {
			logger.error("【getGongShiState】接口异常", e);
			jrtnObj.put("code", "E");
			jrtnObj.put("message", "系统异常：" + e.getMessage());
		}

		logger.info("return:{}", jrtnObj);
		return jrtnObj.toString();
	}


	private String getSafeJsonString(Object value) {
		return value == null ? "" : value.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}

}

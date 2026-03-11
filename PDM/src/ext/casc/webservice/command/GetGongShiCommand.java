package ext.casc.webservice.command;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.processplan.helper.ZhuFuLinkUtil;
import com.glaway.mpm.util.MPMProcessPlanUtil;
import com.glaway.mpm.util.Util;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import ext.casc.doc.CSCDoc;
import ext.casc.util.IBAHelper;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import ext.ptc.ViewWIHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import wt.doc.WTDocument;
import wt.vc.VersionControlHelper;

import java.util.List;
import java.util.Map;

public class GetGongShiCommand implements WebServiceCommand, InitializingBean {

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(GetGongShiCommand.class);
	public static final String METHOD_NAME = "getGongShi";

	@Override
	public String execute(String params) {
		logger.info("【getGongShi】接口接收到参数：{}", params);
		JSONObject jrtnObj = new JSONObject();
		JSONArray gongshiList = new JSONArray();
		try {
			JSONObject jparams = new JSONObject(params);
			String docNumber = jparams.optString("number");
			String docVersion = jparams.optString("version");

			// 查询主制工艺
			WTDocument mainDoc = getDoc(docNumber, docVersion);
			if (mainDoc == null) {
				return error("找不到有效的工艺文件");
			}

			String state = getSafeJsonString(IBAHelper.getIBAStringValue(mainDoc, "GongShiDingEState"));
			if (!"保存提交".equals(state) && !"已审核".equals(state) && !"已导入".equals(state)) {
				return error("工时定额状态是【" + state + "】，不能查询工时定额！");
			}

			addGongshiWithGongxu(docNumber, docVersion, mainDoc, gongshiList);

			// 查询辅制工艺
			List<Map<String, String>> fzList = ZhuFuLinkUtil.getFZGY(docNumber, docVersion);
			for (Map<String, String> fzMap : fzList) {
				String fzNumber = fzMap.get("number");
				String fzVersion = fzMap.get("version");
				WTDocument fzDoc = getDoc(fzNumber, fzVersion);
				addGongshiWithGongxu(fzNumber, fzVersion, fzDoc, gongshiList);
			}

			if (gongshiList.length() == 0) {
				return error("找不到有效的工艺文件或状态不合法！");
			} else {
				jrtnObj.put("code", "S");
				jrtnObj.put("gongshiList", gongshiList);
			}
		} catch (Exception e) {
			logger.error("【getGongShi】接口异常", e);
			jrtnObj.put("code", "E");
			jrtnObj.put("message", "系统异常：" + e.getMessage());
		}

		logger.info("return:{}", jrtnObj);
		return jrtnObj.toString();
	}

	private void addGongshiWithGongxu(String number, String version, WTDocument doc, JSONArray list) {
		try {
			if (doc == null) return;

			String state = getSafeJsonString(IBAHelper.getIBAStringValue(doc, "GongShiDingEState"));
//			if (!"保存提交".equals(state) && !"已审核".equals(state)) return;

			if(StrUtil.isEmpty(version)) {
				version = VersionControlHelper.getVersionIdentifier(doc).getValue();
			}

			JSONObject item = new JSONObject();
			item.put("number", number);
			item.put("version", version);
			item.put("state", state);

			JSONArray gxArray = new JSONArray();
			MPMProcessPlan plan = MPMProcessPlanUtil.getProcessPlanByWTDocument(doc);
			if (plan != null) {
				List<MPMOperationUsageLink> links = ViewWIHelper.getMPMOperationUsageLinkByMpmPr(plan);
				for (MPMOperationUsageLink link : links) {
					String label = Util.formateInteger(link.getOperationLabel());
					MPMOperationMaster master = (MPMOperationMaster) link.getRoleBObject();
					MPMOperation operation = ViewWIHelper.getMpmOperation(master.getNumber());
					if (operation == null) continue;

					JSONObject opObj = new JSONObject();
					opObj.put("gxId", getSafeJsonString(IBAHelper.getIBAStringValue(operation, "BIAOSHI")));
					opObj.put("gxName", getSafeJsonString(operation.getName()));
					opObj.put("stepNumber", label);
					opObj.put("DJGS", getSafeJsonString(IBAHelper.getIBAStringValue(operation, "DJGS")));
					opObj.put("DanJianSheBeiGS", getSafeJsonString(IBAHelper.getIBAStringValue(operation, "DanJianSheBeiGS")));
					gxArray.put(opObj);
				}
			}

			if (gxArray.length() > 0) {
				item.put("gx", gxArray);
				list.put(item);
			}
		} catch (Exception e) {
			logger.error("【addGongshiWithGongxu】处理工艺失败: {}({})", number, version, e);
		}
	}

	private WTDocument getDoc(String number, String version) {
		if (version == null || version.isEmpty()) {
			return CSCDoc.getDoc(number);
		} else {
			return CSCDoc.getLatestDocByNumberAndVersion(number, version);
		}
	}

	private String getSafeJsonString(Object value) {
		return value == null ? "" : value.toString();
	}

	private String error(String message) {
		JSONObject obj = new JSONObject();
		obj.put("code", "E");
		obj.put("message", message);
		logger.warn("【getGongShi】错误：{}", message);
		return obj.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		WebServiceCommandFactory.register(METHOD_NAME, this);
	}
}

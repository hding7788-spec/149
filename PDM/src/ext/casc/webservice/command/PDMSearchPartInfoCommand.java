/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command;

import com.glaway.mpm.util.TypeUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import ext.casc.part.CSCPart;
import ext.casc.util.IBAHelper;
import ext.casc.util.Tools;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import wt.fc.*;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.*;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;

/**
 * 类功能：PBOM属性查询接口
 *
 * @author cjh
 * @date 2022/11/24
 */

public class PDMSearchPartInfoCommand implements WebServiceCommand, InitializingBean {
	// 方法标识
	public static final String METHOD_NAME = "searchPartInfo";

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
		String name = jparams.getString("name");
		String gcj = jparams.getString("gcj");
		String phaseCode = jparams.getString("phasecode");
		String mindex = jparams.getString("mindex");
		if(!Tools.isNull(number) || !Tools.isNull(name)){
			try {
				int in[] = new int[]{0};
				QuerySpec qs = new QuerySpec(WTPart.class);
				qs.setAdvancedQueryEnabled(true);
				TypeUtil.getTypeQuery(WTPart.class,"wt.part.WTPart", qs);
				View view = CSCPart.getViewByName("Manufacturing");
				if (view != null) {
					long viewOid = view.getPersistInfo().getObjectIdentifier().getId();
					qs.appendAnd();
					qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, viewOid),in);
				}
				if(!Tools.isNull(number)){
					qs.appendAnd();
					qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.LIKE, "%" + number + "%"), in);
				}
				if(!Tools.isNull(name)){
					qs.appendAnd();
					qs.appendWhere(new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.LIKE, "%" + name + "%"), in);
				}
				if(!Tools.isNull(gcj) || !Tools.isNull(phaseCode) || !Tools.isNull(mindex)){
					qs.appendAnd();
					qs.appendOpenParen();
					ClassAttribute caId = new ClassAttribute(WTPart.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
					if(!Tools.isNull(gcj)){
						SubSelectExpression subSelectExpression = WTDocumentUtil.getStringIBAQuery("KEYCOMPONENT", gcj);
						qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), in);
					}
					if(!Tools.isNull(phaseCode)){
						if(!Tools.isNull(gcj)){
							qs.appendAnd();
						}
						SubSelectExpression subSelectExpression = WTDocumentUtil.getStringIBAQuery("PHASE_CODE", phaseCode);
						qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), in);
					}
					if(!Tools.isNull(mindex)){
						if(!Tools.isNull(gcj) || !Tools.isNull(phaseCode)){
							qs.appendAnd();
						}
						SubSelectExpression subSelectExpression = WTDocumentUtil.getStringIBAQuery("MINDEX", mindex);
						qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), in);
					}
					qs.appendCloseParen();
				}
				QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
				LatestConfigSpec lcs = new LatestConfigSpec();
				qr = lcs.process(qr);
				while (qr.hasMoreElements()) {
					WTPart part = (WTPart) qr.nextElement();
					JSONObject bom = new JSONObject();
					bom.put("number", part.getNumber());
					bom.put("name", part.getName());
					bom.put("version", part.getIterationDisplayIdentifier().toString());
					bom.put("mindex", IBAHelper.getIBAStringValue(part,"MINDEX"));
					bom.put("pindex", IBAHelper.getIBAStringValue(part,"PINDEX"));
					bom.put("gcj", IBAHelper.getIBAStringValue(part,"KEYCOMPONENT"));
					bom.put("designer", IBAHelper.getIBAStringValue(part,"DESIGNER"));
					bom.put("modifier", part.getModifier().getFullName());
					bom.put("modifytime", part.getModifyTimestamp().getTime());
					bom.put("batch", IBAHelper.getIBAStringValue(part,"BATCH"));
					msg.put(bom);
				}
			} catch(Exception e) {
				e.printStackTrace();
				errorMsg = "查询PBOM出错";
			}
		}else{
			errorMsg = "编号名称不能都为空";
		}
		JSONObject rtnMsgObj = new JSONObject();
		try {
			if(errorMsg!=null&&!"".equals(errorMsg)){
				rtnMsgObj.put("status", "N");
				rtnMsgObj.put("result", errorMsg);
			}else{
				rtnMsgObj.put("status", "Y");
				rtnMsgObj.put("count", msg.length());
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

package ext.sast.center.productModel;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bjsasc.avidm.mq.message.Based;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.sast.center.productModel.util.SyncModeTypeHelper;
import wt.util.WTException;

/**
 * 
 * @author Wang-ya-qi
 *保存模型属性映射信息
 */
public class SaveSyncModelAttributeInfoProcessor {
	private static final Logger log = Logger.getLogger(SaveSyncModelAttributeInfoProcessor.class);
	public static FormResult saveInfo(NmCommandBean commandBean) throws WTException {
		FormResult formResult = new FormResult();
		try {
			String changModelType = commandBean.getTextParameter("changModelTypeAttribute");
			String localHostModelType_US = commandBean.getTextParameter("localHostModelType_US");
			String sastModelType_US = (String) commandBean.getText().get("sastModelType_US");
			String sastModelType_ZH = (String) commandBean.getText().get("sastModelType");
			if(changModelType == null || changModelType.length()<=0){
				FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null,"成功", null, "属性映射成功");
				formResult.addFeedbackMessage(message);
				formResult.setNextAction(FormResultAction.JAVASCRIPT);
				formResult.setJavascript("reshTableBuilder();");
				return formResult;
			}
			JSONArray jsonArray = new JSONArray();
			String[] changeValueSet = changModelType.split(";");
			for(int i=0;i<changeValueSet.length;i++){
				String valueStr = changeValueSet[i];
				String[] values = valueStr.split(",");
				String attribute_us = values[0];
				String attribute_zh = SyncModeTypeHelper.getLocalModelAttrDisplayName(localHostModelType_US, attribute_us);
				String sast_attribute_us = values[1];
				String sast_attribute_zh = SyncModeTypeHelper.getSastModelAttrDisplayName(sast_attribute_us, sastModelType_US);
				SyncModeTypeHelper.saveModelTypeAttributeInfo(attribute_zh,attribute_us,sast_attribute_zh,sast_attribute_us,sastModelType_ZH,sastModelType_US);
				
				JSONObject json = new JSONObject();
				json.put(Based.IID, localHostModelType_US);
				json.put(Based.MODELID, sastModelType_US);
				json.put(Based.MODELNAME, sastModelType_ZH);
				json.put(Based.MODELATTRID, sast_attribute_us);
				json.put(Based.MODELATTRNAME, sast_attribute_us);
				
				jsonArray.put(json);
			}
			try {
				
			} catch (Exception e) {
				log.info("模型属性映射同步到中心域出错！");
			}
			
			FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null,"成功", null, "属性映射成功");
			formResult.addFeedbackMessage(message);
			formResult.setNextAction(FormResultAction.JAVASCRIPT);
			formResult.setJavascript("reshTableBuilder();");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formResult;
	}
}

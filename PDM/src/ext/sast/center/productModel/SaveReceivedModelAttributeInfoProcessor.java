package ext.sast.center.productModel;

import java.util.List;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.sast.center.productModel.util.SyncModeTypeHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

public class SaveReceivedModelAttributeInfoProcessor extends DefaultObjectFormProcessor {
	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
		FormResult formresult = super.doOperation(commandBean, objectBeans);
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			String changModelType = commandBean.getTextParameter("changModelTypeAttribute");
			
			String localModelType_US = (String) commandBean.getText().get("localModelType_US");
			//String localModelType_ZH = commandBean.getTextParameter("localModelType");
			String sastModelType_US = (String) commandBean.getText().get("sastModelType_US");
			//String sastModelType_ZH = (String) commandBean.getText().get("sastModelType");
			
			if(changModelType == null || changModelType.length()<=0){
				FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null,"成功", null, "属性映射成功");
				formresult.addFeedbackMessage(message);
				formresult.setNextAction(FormResultAction.JAVASCRIPT);
				formresult.setJavascript("reshTableBuilder();");
				return formresult;
			}
			String[] changeValueSet = changModelType.split(";");
			for(int i=0;i<changeValueSet.length;i++){
				String valueStr = changeValueSet[i];
				String[] values = valueStr.split(",");
				String sast_attribute_us = values[0];
				String sast_attribute_zh = SyncModeTypeHelper.getSastModelAttrDisplayName(sast_attribute_us, sastModelType_US);
				String attribute_us = values[1];
				String attribute_zh = SyncModeTypeHelper.getLocalModelAttrDisplayName(localModelType_US,attribute_us);
				
				SyncModeTypeHelper.saveReceivedModelTypeAttributeInfo(sast_attribute_zh,sast_attribute_us,attribute_zh,attribute_us,sastModelType_US,localModelType_US);
			}
			
			FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null,"成功", null, "属性映射成功");
			formresult.addFeedbackMessage(message);
			formresult.setNextAction(FormResultAction.JAVASCRIPT);
			formresult.setJavascript("reshTableBuilder();");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		return formresult;
		
	}
}

package ext.sast.center.productModel;

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
 *保存模型映射信息
 */
public class SaveSyncModelTypeInfoProcessor {
	
	public static FormResult saveModelType(NmCommandBean commandBean) throws WTException {
		FormResult formResult = new FormResult();
		try{
			String changModelType = commandBean.getTextParameter("changModelType");
			if(changModelType == null || changModelType.length()<=0){
				FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null,"成功", null, "类型映射成功");
				formResult.addFeedbackMessage(message);
				formResult.setNextAction(FormResultAction.REFRESH_OPENER);
				return formResult;
			}
			String[] changeValueSet = changModelType.split(";");
			for(int i=0;i<changeValueSet.length;i++){
				String valueStr = changeValueSet[i];
				String[] values = valueStr.split(",");
				String localHostType_US = values[0];
				String localHostType_ZH = SyncModeTypeHelper.getLocalModelTypeDisplayName(localHostType_US);
				String sastType_US = values[1];
				String sastType_ZH = SyncModeTypeHelper.getSastModelTypeNameById(sastType_US);
				
				SyncModeTypeHelper.saveModelType(localHostType_ZH, localHostType_US, sastType_ZH, sastType_US);
			}
			FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null,"成功", null, "类型映射成功");
			formResult.addFeedbackMessage(message);
			formResult.setNextAction(FormResultAction.REFRESH_OPENER);
		}catch(Exception e){
			e.printStackTrace();
		}
		return formResult;

	}
}

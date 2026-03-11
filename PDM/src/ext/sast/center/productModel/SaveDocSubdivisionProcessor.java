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

public class SaveDocSubdivisionProcessor extends DefaultObjectFormProcessor {
	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
		FormResult formresult = super.doOperation(commandBean, objectBeans);
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			String selectedRowValues = commandBean.getTextParameter("selectedRowValues");
			
			String[] values = selectedRowValues.split(",");
			String id = values[0];
			String siteName = values[1];
			String docTypeName = values[2];
			String docTypeInnerName = values[3];
			String localDocTypeInnerName = values[4];
			
			SyncModeTypeHelper.saveDocSubdivisionInfo(id,siteName,docTypeName,docTypeInnerName,localDocTypeInnerName);
			
			FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null,"成功", null, "保存成功");
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
